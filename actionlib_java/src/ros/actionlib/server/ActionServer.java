package ros.actionlib.server;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

import ros.NodeHandle;
import ros.Publisher;
import ros.Ros;
import ros.RosException;
import ros.Subscriber;
import ros.actionlib.ActionSpec;
import ros.actionlib.util.GoalIDGenerator;
import ros.communication.Duration;
import ros.communication.Message;
import ros.communication.Time;
import ros.pkg.actionlib_msgs.msg.GoalID;
import ros.pkg.actionlib_msgs.msg.GoalStatus;
import ros.pkg.actionlib_msgs.msg.GoalStatusArray;

public class ActionServer<T_ACTION_FEEDBACK extends Message, T_ACTION_GOAL extends Message, T_ACTION_RESULT extends Message, T_FEEDBACK extends Message, T_GOAL extends Message, T_RESULT extends Message> {

	/**
	 * The action specification
	 */
	protected ActionSpec<?,T_ACTION_FEEDBACK,T_ACTION_GOAL,T_ACTION_RESULT,T_FEEDBACK,T_GOAL,T_RESULT> spec;

	protected ActionServerCallbacks<T_ACTION_FEEDBACK,T_ACTION_GOAL,T_ACTION_RESULT,T_FEEDBACK,T_GOAL,T_RESULT> callbacks;
	protected NodeHandle nodeHandle;

	protected Subscriber<T_ACTION_GOAL> subGoal;
	protected Subscriber<GoalID> subCancelGoal;
	protected Publisher<T_ACTION_FEEDBACK> pubFeedback;
	protected Publisher<T_ACTION_RESULT> pubResult;
	protected Publisher<GoalStatusArray> pubStatus;

	protected ArrayList<StatusTracker<T_ACTION_FEEDBACK,T_ACTION_GOAL,T_ACTION_RESULT,T_FEEDBACK,T_GOAL,T_RESULT>> statusList =  new ArrayList<StatusTracker<T_ACTION_FEEDBACK,T_ACTION_GOAL,T_ACTION_RESULT,T_FEEDBACK,T_GOAL,T_RESULT>>(50);

	protected Time lastCancel = new Time(0,0);
	protected Duration statusListTimeout;
	protected Timer statusTimer = new Timer();

	protected GoalIDGenerator idGenerator;
	protected boolean active = false;
	protected boolean shutdown = false;

	protected ReentrantLock lock = new ReentrantLock(true); // fair lock, so no goal has to starve

	public ActionServer(String nameSpace, ActionSpec<?, T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT> spec, ActionServerCallbacks<T_ACTION_FEEDBACK,T_ACTION_GOAL,T_ACTION_RESULT,T_FEEDBACK,T_GOAL,T_RESULT> callbacks, boolean autoStart) {

		this.callbacks = callbacks;
		this.spec = spec;
		this.nodeHandle = Ros.getInstance().createNodeHandle(nameSpace);
		this.idGenerator = new GoalIDGenerator(nodeHandle.getNamespace()+nodeHandle.getName());
		if (autoStart) {
			start();
		}

	}

	public ActionServer(NodeHandle parent, String nameSpace, ActionSpec<?, T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT> spec, ActionServerCallbacks<T_ACTION_FEEDBACK,T_ACTION_GOAL,T_ACTION_RESULT,T_FEEDBACK,T_GOAL,T_RESULT> callbacks, boolean autoStart) {

		this.callbacks = callbacks;
		this.spec = spec;
		this.nodeHandle = Ros.getInstance().createNodeHandle(parent, nameSpace);
		this.idGenerator = new GoalIDGenerator(nodeHandle.getNamespace()+nodeHandle.getName());
		if (autoStart) {
			start();
		}

	}

	public void start() {

		if (shutdown) {
			Ros.getInstance().logError("[ActionServer] Can't start an action server which was shut down.");
			return;
		}
		
		if (callbacks == null) {
			Ros.getInstance().logError("[ActionServer] Can't start an action server without registered user code callbacks.");
			return;
		}
		
		if (!active) {
			if (initServer()) {
				active = true;
				publishStatus();				
			} else {
				Ros.getInstance().logError("[ActionServer] Couldn't set up needed Subscribers and Publishers due to a RosException");
			}
		}

	}
	
	public void shutdown() {

		if (statusTimer != null) {
			statusTimer.cancel();
			statusTimer = null;
		}

		shutdown = true;
		active = false;

		pubStatus.shutdown();
		pubFeedback.shutdown();
		pubResult.shutdown();
		subGoal.shutdown();
		subCancelGoal.shutdown();
		
		nodeHandle.shutdown();
		
	}

	protected boolean initServer() {

		try {

			pubFeedback = nodeHandle.advertise("feedback", spec.createActionFeedbackMessage(), 50);
			pubResult = nodeHandle.advertise("result", spec.createActionResultMessage(), 50);
			pubStatus = nodeHandle.advertise("status", new GoalStatusArray(), 50);

			Subscriber.Callback<T_ACTION_GOAL> goalCallback = new Subscriber.Callback<T_ACTION_GOAL>() {
				@Override
				public void call(T_ACTION_GOAL actionGoal) {
					goalCB(actionGoal);
				}
			};
			subGoal = nodeHandle.subscribe("goal", spec.createActionGoalMessage(), goalCallback, 50);

			Subscriber.Callback<GoalID> cancelCallback = new Subscriber.Callback<GoalID>() {
				@Override
				public void call(GoalID goalID) {
					cancelCB(goalID);
				}
			};
			subCancelGoal = nodeHandle.subscribe("cancel", new GoalID(), cancelCallback, 50);

		} catch (RosException re) {

			if (subGoal != null) {
				subGoal.shutdown();
				subGoal = null;
			}
			if (subCancelGoal != null) {
				subCancelGoal.shutdown();
				subCancelGoal = null;
			}
			if (pubFeedback != null) {
				pubFeedback.shutdown();
				pubFeedback = null;
			}
			if (pubResult != null) {
				pubResult.shutdown();
				pubResult = null;
			}
			if (pubStatus != null) {
				pubStatus.shutdown();
				pubStatus = null;
			}
			return false;

		}

		double pStatusFrequency;
		double pStatusListTimeout;

		try {
			pStatusListTimeout = nodeHandle.getDoubleParam("status_list_timeout");
		} catch (RosException e) {
			pStatusListTimeout = 5.0;
		}
		statusListTimeout = new Duration(pStatusListTimeout);

		try {
			pStatusFrequency = nodeHandle.getDoubleParam("status_frequency");
		} catch (RosException e) {
			pStatusFrequency = 5.0;
		}
		if (pStatusFrequency <= 0) {
			pStatusFrequency = 5.0;
			Ros.getInstance().logWarn("[ActionServer] Status frequency parameter is not a positive number. Using default value of 5Hz!");
		}

		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				publishStatus();
			}
		};
		long milliSecsPeriod = (long)(1000/pStatusFrequency);
		if (milliSecsPeriod == 0) {
			Ros.getInstance().logWarn("[ActionServer] Status frequency parameter is too large. Maximum status update rate capped to 1000Hz.");
			milliSecsPeriod = 1;
		}
		if (statusTimer != null) {
			statusTimer.cancel();
		}
		statusTimer = new Timer();
		statusTimer.scheduleAtFixedRate(task, 0, milliSecsPeriod);

		return true;

	}
	
	/**
	 * Gets the ActionServer's node handle.
	 * 
	 * @return The node handle
	 */
	protected NodeHandle getNodeHandle() {
		return nodeHandle;
	}

	protected void publishFeedback(GoalStatus goalStatus, T_FEEDBACK feedback) {

		if (!active) {
			Ros.getInstance().logWarn("[ActionServer] Trying to publishFeedback() on an inactive ActionServer.");
			return;
		}
		
		lock.lock();
		try {

			Time now = Ros.getInstance().now();
			T_ACTION_FEEDBACK actionFeedback;
			actionFeedback = spec.createActionFeedbackMessage(feedback, now, goalStatus);
			Ros.getInstance().logDebug("[ActionServer] Publishing feedback for goal, id: "+goalStatus.goal_id.id+", stamp: "+(goalStatus.goal_id.stamp.totalNsecs()/1000000)+"ms");
			pubFeedback.publish(actionFeedback);			

		} finally {
			lock.unlock();	
		}

	}

	protected void publishResult(GoalStatus goalStatus, T_RESULT result) {

		if (!active) {
			Ros.getInstance().logWarn("[ActionServer] Trying to publishResult() on an inactive ActionServer.");
			return;
		}
		
		lock.lock();
		try {

			Time now = Ros.getInstance().now();
			T_ACTION_RESULT actionResult;
			actionResult = spec.createActionResultMessage(result, now, goalStatus);
			Ros.getInstance().logDebug("[ActionServer] Publishing result for goal, id: "+goalStatus.goal_id.id+", stamp: "+(goalStatus.goal_id.stamp.totalNsecs()/1000000)+"ms");
			pubResult.publish(actionResult);

		} finally {
			lock.unlock();	
		}

	}

	protected void publishStatus() {

		if (!active) {
			Ros.getInstance().logWarn("[ActionServer] Trying to publishStatus() on an inactive ActionServer.");
			return;
		}
		
		lock.lock();
		try {

			GoalStatusArray statusArray = new GoalStatusArray();
			statusArray.header.stamp = Ros.getInstance().now();
			statusArray.status_list.ensureCapacity(statusList.size());

			for (int i=0; i<statusList.size(); i++) {

				StatusTracker<T_ACTION_FEEDBACK,T_ACTION_GOAL,T_ACTION_RESULT,T_FEEDBACK,T_GOAL,T_RESULT> st = statusList.get(i);
				statusArray.status_list.add(st.goalStatus);

				if (!st.destructionTime.isZero()) {

					Time timeoutTime = st.destructionTime.add(statusListTimeout);
					Duration timeoutDur = timeoutTime.subtract(Ros.getInstance().now());
					if (timeoutDur.isNegative()) {
						statusList.remove(st);
						i--;
					}

				}

			}

			pubStatus.publish(statusArray);

		} finally {
			lock.unlock();	
		}

	}

	protected void goalCB(T_ACTION_GOAL actionGoal) {

		StatusTracker<T_ACTION_FEEDBACK,T_ACTION_GOAL,T_ACTION_RESULT,T_FEEDBACK,T_GOAL,T_RESULT> newTracker = null;

		lock.lock();
		try {

			if (!active) {
				return;
			}

			Ros.getInstance().logDebug("[ActionServer] Received a new goal request");

			// check if new goal already is represented in status list
			GoalID goalID = spec.getGoalIDFromActionGoal(actionGoal);
			for (int i=0; i<statusList.size(); i++) {

				StatusTracker<T_ACTION_FEEDBACK,T_ACTION_GOAL,T_ACTION_RESULT,T_FEEDBACK,T_GOAL,T_RESULT> st = statusList.get(i);
				GoalStatus statusOfExistingGoal = st.goalStatus;
				if (goalID.id.equals(statusOfExistingGoal.goal_id.id)) {

					// The goal can be in a RECALLING state if a cancel message came in before the goal 
					if (statusOfExistingGoal.status == GoalStatus.RECALLING) {
						statusOfExistingGoal.status = GoalStatus.RECALLED;
						publishResult(statusOfExistingGoal, spec.createResultMessage());
					}

					if (st.goalHandle == null) {
						st.destructionTime = Ros.getInstance().now();
					}

					return;

				}

			}

			newTracker = new StatusTracker<T_ACTION_FEEDBACK,T_ACTION_GOAL,T_ACTION_RESULT,T_FEEDBACK,T_GOAL,T_RESULT>(actionGoal, spec, idGenerator);
			statusList.add(newTracker);

			// if goal has already been canceled by a cancel message according to its timestamp
			if (!goalID.stamp.isZero() && !goalID.stamp.subtract(lastCancel).isPositive()) {
				ServerGoalHandle<T_ACTION_FEEDBACK,T_ACTION_GOAL,T_ACTION_RESULT,T_FEEDBACK,T_GOAL,T_RESULT> gh;
				gh = new ServerGoalHandle<T_ACTION_FEEDBACK,T_ACTION_GOAL,T_ACTION_RESULT,T_FEEDBACK,T_GOAL,T_RESULT>(newTracker, this);
				gh.setCanceled(spec.createResultMessage(), "This goal handle was canceled " +
						"by the action server because its timestamp is before the " +
						"timestamp of the last cancel request.");
				return;
			}

		} finally {
			lock.unlock();	
		}

		ServerGoalHandle<T_ACTION_FEEDBACK,T_ACTION_GOAL,T_ACTION_RESULT,T_FEEDBACK,T_GOAL,T_RESULT> goalHandle;
		goalHandle = new ServerGoalHandle<T_ACTION_FEEDBACK,T_ACTION_GOAL,T_ACTION_RESULT,T_FEEDBACK,T_GOAL,T_RESULT>(newTracker, this); 
		this.callbacks.goalCallback(goalHandle);			

	}

	protected void cancelCB(GoalID cancelGoal) {

		ArrayList<StatusTracker<T_ACTION_FEEDBACK,T_ACTION_GOAL,T_ACTION_RESULT,T_FEEDBACK,T_GOAL,T_RESULT>> callbackList = null;

		lock.lock();
		try {

			if (!active) {
				return;
			}

			Ros.getInstance().logDebug("[ActionServer] Received a new cancel request");

			boolean cancelIDfound = false;
			callbackList = new ArrayList<StatusTracker<T_ACTION_FEEDBACK,T_ACTION_GOAL,T_ACTION_RESULT,T_FEEDBACK,T_GOAL,T_RESULT>>();
			for (int i=0; i<statusList.size(); i++) {

				StatusTracker<T_ACTION_FEEDBACK,T_ACTION_GOAL,T_ACTION_RESULT,T_FEEDBACK,T_GOAL,T_RESULT> existingST = statusList.get(i);
				if (existingST.isCancelRequestTracker()) {
					continue;
				}
				GoalID existingGoal = existingST.goalStatus.goal_id;

				// if id is "" and time stamp is 0 => cancel everything
				// if ids match => cancel this goal
				// if time stamp is not 0 => cancel everything before time stamp
				if ((cancelGoal.id.equals("") && cancelGoal.stamp.isZero()) 
						|| cancelGoal.id.equals(existingGoal.id)
						|| (!cancelGoal.stamp.isZero() && 
								cancelGoal.stamp.subtract(existingGoal.stamp).isPositive())) {

					if (cancelGoal.id.equals(existingGoal.id)) {
						cancelIDfound = true;
					}

					if (!existingST.isCancelRequestTracker()) {
						if (existingST.goalHandle == null) {
							new ServerGoalHandle<T_ACTION_FEEDBACK,T_ACTION_GOAL,T_ACTION_RESULT,T_FEEDBACK,T_GOAL,T_RESULT>(existingST, this);
							existingST.destructionTime = new Time(0,0);
						}

						if (existingST.goalHandle.setCancelRequested()) {
							callbackList.add(existingST);
						}		    			
					}

				}

			}

			if (!cancelGoal.id.equals("") && !cancelIDfound) {
				StatusTracker<T_ACTION_FEEDBACK,T_ACTION_GOAL,T_ACTION_RESULT,T_FEEDBACK,T_GOAL,T_RESULT> cancelTracker;
				cancelTracker = new StatusTracker<T_ACTION_FEEDBACK,T_ACTION_GOAL,T_ACTION_RESULT,T_FEEDBACK,T_GOAL,T_RESULT>(cancelGoal, GoalStatus.RECALLING);
				cancelTracker.destructionTime = Ros.getInstance().now();
				statusList.add(cancelTracker);
			}

			if (cancelGoal.stamp.subtract(lastCancel).isPositive()) {
				lastCancel = new Time(cancelGoal.stamp);
			}

		} finally {
			lock.unlock();	
		}

		if (callbackList != null) {
			for (StatusTracker<T_ACTION_FEEDBACK,T_ACTION_GOAL,T_ACTION_RESULT,T_FEEDBACK,T_GOAL,T_RESULT> st : callbackList) {
				callbacks.cancelCallback(st.goalHandle);
			}
		}

	}

}
