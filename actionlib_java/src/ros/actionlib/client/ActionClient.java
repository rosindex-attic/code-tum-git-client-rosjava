package ros.actionlib.client;

import ros.NodeHandle;
import ros.Publisher;
import ros.Ros;
import ros.RosException;
import ros.Subscriber;
import ros.actionlib.ActionSpec;
import ros.communication.Duration;
import ros.communication.Message;
import ros.communication.Time;
import ros.pkg.actionlib_msgs.msg.GoalID;
import ros.pkg.actionlib_msgs.msg.GoalStatusArray;

/**
 * An ActionClient is the client interface of the actionlib package. It provides
 * means to interact with an action server, i.e. sending goal messages to the
 * action server, which tries to compute/create a result according to the
 * request that is sent back to the client. During the process of getting the
 * final result an action server may send feedback messages in order to update
 * the action client on the current state of its request. A request may be
 * canceled by the ActionClient by sending a cancel message to the server.<br>
 * Every goal that is sent to the server is represented by a
 * {@link ClientGoalHandle} that allows to monitor the goal's progress. An
 * ActionClient may send more than one goal message at a time.
 * 
 * @see SimpleActionClient
 * 
 * @author Alexander C. Perzylo, perzylo@cs.tum.edu
 * 
 * @param <T_ACTION_FEEDBACK>
 *            action feedback message
 * @param <T_ACTION_GOAL>
 *            action goal message
 * @param <T_ACTION_RESULT>
 *            action result message
 * @param <T_FEEDBACK>
 *            feedback message
 * @param <T_GOAL>
 *            goal message
 * @param <T_RESULT>
 *            result message
 */
public class ActionClient<T_ACTION_FEEDBACK extends Message, T_ACTION_GOAL extends Message, T_ACTION_RESULT extends Message, T_FEEDBACK extends Message, T_GOAL extends Message, T_RESULT extends Message> {

	/**
	 * The action specification
	 */
	protected ActionSpec<?, T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT> spec;

	/**
	 * The GoalManager used to initialize new goals and to update all active
	 * GoalHandles on the reception of status, feedback and result messages
	 */
	protected GoalManager<T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT> goalManager;

	/**
	 * The ActionClient's node handle
	 */
	protected NodeHandle nodeHandle;

	/**
	 * A Subscriber of the action server's status topic.
	 */
	protected Subscriber<GoalStatusArray> subStatus;

	/**
	 * A Subscriber of the action server's feedback topic.
	 */
	protected Subscriber<T_ACTION_FEEDBACK> subFeedback;

	/**
	 * A Subscriber of the action server's result topic.
	 */
	protected Subscriber<T_ACTION_RESULT> subResult;

	/**
	 * A Publisher for goal messages using the goal topic.
	 */
	protected Publisher<T_ACTION_GOAL> pubGoal;

	/**
	 * A Publisher for cancel messages using the cancel topic.
	 */
	protected Publisher<GoalID> pubCancelGoal;

	/**
	 * A flag indicating whether or not a first status message was already 
	 * received from the action server or not
	 */
	protected boolean statusReceived = false;

	/**
	 * The action server's frame ID
	 */
	protected String callerID = "";

	/**
	 * The time when the last status message was received
	 */
	protected Time latestStatusTime;

	/**
	 * A flag indicating whether or not this ActionClient was shutdown and 
	 * cannot be used anymore
	 */
	protected volatile boolean active = true;
	
	/**
	 * Monitor used to wait for the action server to start up and get notified
	 * as soon as it is ready.
	 */
	private Object waitSync = new Object();

	/**
	 * Constructor used to create an ActionClient that communicates in a given
	 * name space on a given action specification.
	 * 
	 * @param nameSpace
	 *            The name space to communicate within (specified by the action
	 *            server)
	 * @param spec
	 *            The specification of the action the ActionClient shall use
	 * @throws RosException
	 *             If setting up the needed subscribers and publishers fail
	 */
	public ActionClient(String nameSpace,
			ActionSpec<?, T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT> spec)
			throws RosException {

		this.spec = spec;
		NodeHandle nodeHandle = Ros.getInstance().createNodeHandle(nameSpace);
		initClient(nodeHandle);
		this.goalManager = new GoalManager<T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT>(this);
		
	}

	/**
	 * Constructor used to create an ActionClient that will be a child node of
	 * the node represented by the given node handle. It communicates in a given
	 * name space on a given action specification.
	 * 
	 * @param parent
	 *            The parent node of this action client
	 * @param nameSpace
	 *            The name space to communicate within (specified by the action
	 *            server)
	 * @param spec
	 *            The specification of the action the ActionClient shall use
	 * @throws RosException
	 *             If setting up the needed subscribers and publishers fail
	 */
	public ActionClient(NodeHandle parent, String nameSpace,
			ActionSpec<?, T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT> spec)
			throws RosException {

		this.spec = spec;
		NodeHandle nodeHandle = Ros.getInstance().createNodeHandle(parent, nameSpace);
		initClient(nodeHandle);
		this.goalManager = new GoalManager<T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT>(this);

	}

	/**
	 * Sets up subscribers for the action server's status, feedback and result
	 * topics and publishers for the action client's goal and cancel topics.
	 * 
	 * @param nodeHandle
	 *            The node handle to be used by the ActionClient
	 * @throws RosException
	 *             If setting up the needed subscribers and publishers fail
	 */
	protected void initClient(NodeHandle nodeHandle) throws RosException {

		this.nodeHandle = nodeHandle;

		Subscriber.Callback<T_ACTION_FEEDBACK> feedbackCallback = new Subscriber.Callback<T_ACTION_FEEDBACK>() {
			@Override
			public void call(T_ACTION_FEEDBACK actionFeedback) {
				feedbackCB(actionFeedback);
			}
		};
		subFeedback = nodeHandle.subscribe("feedback", spec.createActionFeedbackMessage(), feedbackCallback, 1);

		Subscriber.Callback<T_ACTION_RESULT> resultCallback = new Subscriber.Callback<T_ACTION_RESULT>() {
			@Override
			public void call(T_ACTION_RESULT actionResult) {
				resultCB(actionResult);
			}
		};
		subResult = nodeHandle.subscribe("result", spec.createActionResultMessage(), resultCallback, 1);

		Subscriber.Callback<GoalStatusArray> statusCallback = new Subscriber.Callback<GoalStatusArray>() {
			@Override
			public void call(GoalStatusArray statusArray) {
				statusCB(statusArray);
			}
		};
		subStatus = nodeHandle.subscribe("status", new GoalStatusArray(), statusCallback, 1);

		pubGoal = nodeHandle.advertise("goal", spec.createActionGoalMessage(), 1);
		pubCancelGoal = nodeHandle.advertise("cancel", new GoalID(), 1);

	}

	/**
	 * Sends a goal message to the action server without demanding callback
	 * methods which would enable the user to track the progress of the goal.
	 * 
	 * @param goal
	 *            The goal message
	 * @return The ClientGoalHandle that is associated with the goal message.
	 */
	public ClientGoalHandle<T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT> sendGoal(T_GOAL goal) {
		return sendGoal(goal, null);
	}

	/**
	 * Sends a goal message to the action server demanding callback methods
	 * which enable the user to track the progress of the goal.
	 * 
	 * @param goal
	 *            The goal message
	 * @param callbacks
	 *            The user's callback methods
	 * @return The ClientGoalHandle that is associated with the goal message.
	 */
	public ClientGoalHandle<T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT> sendGoal(T_GOAL goal,
			ActionClientCallbacks<T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT> callbacks) {

		ClientGoalHandle<T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT> goalHandle = null;
		
		if (active) {
			goalHandle = goalManager.initGoal(goal, callbacks, spec);			
		} else {
			Ros.getInstance().logWarn("[ActionClient] Trying to send a goal on an inactive ActionClient. You are incorrectly using an ActionClient.");
		}

		return goalHandle;

	}

	/**
	 * Cancels the execution of all goals that were sent out with a timestamp
	 * equal to or before the specified time.
	 * 
	 * @param time
	 *            A point in time.
	 */
	public void cancelGoalsAtAndBeforeTime(Time time) {

		if (!active) {
			Ros.getInstance().logWarn("[ActionClient] Trying to cancel goals on an inactive ActionClient. You are incorrectly using an ActionClient.");
			return;
		}
		
		GoalID cancelMessage = new GoalID();
		cancelMessage.id = "";
		cancelMessage.stamp = time;
		pubCancelGoal.publish(cancelMessage);

	}

	/**
	 * Cancels the execution of all goals.
	 */
	public void cancelAllGoals() {
		cancelGoalsAtAndBeforeTime(new Time(0, 0));
	}

	/**
	 * Checks if the ActionClient is active or was shutdown before.
	 * 
	 * @return <tt>true</tt> - If the ActionClient is active<br>
	 *         <tt>false</tt> - Otherwise
	 *         
	 * @see #shutdown()
	 */
	public boolean isActive() {
		return active;
	}
	
	/**
	 * Checks if a connection to the action server is established.
	 * 
	 * @return <tt>true</tt> - If the ActionClient and the action server are
	 *         connected<br>
	 *         <tt>false</tt> - Otherwise
	 */
	public boolean isServerConnected() {

		if (!statusReceived) {
			Ros.getInstance().logDebug("[ActionClient] isServerconnected -> false (didn't receive status yet)");
			return false;
		}
		if (!subFeedback.isValid()) {
			Ros.getInstance().logDebug("[ActionClient] isServerconnected -> false (feedback subscriber is invalid)");
			return false;
		}
		if (!subResult.isValid()) {
			Ros.getInstance().logDebug("[ActionClient] isServerconnected -> false (result subscriber is invalid)");
			return false;
		}
		if (!subStatus.isValid()) {
			Ros.getInstance().logDebug("[ActionClient] isServerconnected -> false (status subscriber is invalid)");
			return false;
		}
		if (!pubGoal.isValid()) {
			Ros.getInstance().logDebug("[ActionClient] isServerconnected -> false (goal publisher is invalid)");
			return false;
		}
		if (!pubCancelGoal.isValid()) {
			Ros.getInstance().logDebug("[ActionClient] isServerconnected -> false (cancel goal publisher is invalid)");
			return false;
		}
		if (pubGoal.getNumSubscribers() == 0) {
			Ros.getInstance().logDebug("[ActionClient] isServerconnected -> false (no subscriber for goal topic)");
			return false;
		}
		if (pubCancelGoal.getNumSubscribers() == 0) {
			Ros.getInstance().logDebug("[ActionClient] isServerconnected -> false (no subscriber for cancel topic)");
			return false;
		}

		Ros.getInstance().logDebug("[ActionClient] isServerConnected -> true (action server '" + callerID + "')");
		return true;

	}

	/**
	 * Shuts down all subscribers and publishers and stops tracking all 
	 * goals without canceling them. All ClientGoalHandles created by 
	 * this ActionClient get deactivated. After an ActionClient was shutdown, it
	 * cannot be used anymore. 
	 */
	public void shutdown() {
		
		active = false;
		
		goalManager.clear();
		
		pubGoal.shutdown();
		pubCancelGoal.shutdown();
		subResult.shutdown();
		subFeedback.shutdown();
		subStatus.shutdown();
		
		nodeHandle.shutdown();
		
	}
	
	/**
	 * Waits for the action server to start up.
	 * 
	 * @return <tt>true</tt> - if the SimpleActionClient could establish a
	 *         connection to the action server<br>
	 *         <tt>false</tt> - If the action client's node handle is not ok
	 *         (normally, the method would block forever if no connection is
	 *         established)
	 */
	public boolean waitForActionServerToStart() {
		return waitForActionServerToStart(new Duration(0, 0));
	}

	/**
	 * Waits up to the specified duration for the action server to start up. If
	 * no connection could be established within the given time, the method
	 * returns indicating the error.
	 * 
	 * @param timeout
	 *            The maximum duration to wait for the action server to start
	 *            up. A zero length duration results in unlimited waiting.
	 * @return <tt>true</tt> - if the ActionClient could establish a connection
	 *         to the action server within the given time<br>
	 *         <tt>false</tt> - otherwise
	 */
	public boolean waitForActionServerToStart(Duration timeout) {

		if (!active) {
			Ros.getInstance().logWarn("[ActionClient] Trying to waitForActionServerToStart() on an inactive ActionClient. You are incorrectly using an ActionClient.");
			return false;
		}
		if (timeout.isNegative()) {
			Ros.getInstance().logWarn("[ActionClient] Timeouts can't be negative. Timeout is [" + (timeout.totalNsecs() / 1000000) + " ms]");
			return false;
		}

		Time timeoutTime = Ros.getInstance().now().add(timeout);
		Duration loopCheckTime = new Duration(0, 500000000); // check every 500ms, if NodeHandle is ok

		synchronized (waitSync) {
			while (nodeHandle.ok() && !isServerConnected()) {

				Duration timeLeft = timeoutTime.subtract(Ros.getInstance().now());
				if (timeLeft.totalNsecs() / 1000000 <= 0 && !timeout.isZero()) {
					break;
				}

				if ((timeLeft.totalNsecs() > loopCheckTime.totalNsecs()) || timeout.isZero()) {
					timeLeft = loopCheckTime;
				}

				try {
					waitSync.wait(timeLeft.totalNsecs() / 1000000);
				} catch (InterruptedException e) {
				}

			}
		}

		return isServerConnected();

	}

	/**
	 * Gets the ActionClient's node handle.
	 * 
	 * @return The node handle
	 */
	protected NodeHandle getNodeHandle() {
		return nodeHandle;
	}
	
	/**
	 * Publishes the given action goal message on the goal topic.
	 * 
	 * @param actionGoal
	 *            The action goal message
	 */
	protected void publishActionGoal(T_ACTION_GOAL actionGoal) {
		if (active) {
			pubGoal.publish(actionGoal);	
		}
	}

	/**
	 * Publishes the given GoalID message on the cancel topic. This should cause
	 * the action server to stop working on the goal represented by this GoalID.
	 * 
	 * @param cancelMessage
	 *            The GoalID message using the id of the goal that shall be
	 *            canceled
	 */
	protected void publishCancelGoal(GoalID cancelMessage) {
		if (active) {
			pubCancelGoal.publish(cancelMessage);	
		}
	}

	/**
	 * Callback method used when the subscriber of the action server's status
	 * topic receives a new list of GoalStatus messages. This ActionClient's
	 * GoalManager gets informed about the newly arrived messages.
	 * 
	 * @param goalStatuses
	 *            A list of GoalStatus messages
	 * 
	 * @see GoalManager#updateStatuses(GoalStatusArray)
	 */
	protected void statusCB(GoalStatusArray goalStatuses) {

		String currCallerID = goalStatuses.header.frame_id;

		if (!statusReceived) {
			statusReceived = true;
			Ros.getInstance().logDebug("[ActionClient] Received first status message from action server (" + currCallerID + ")");
		} else if (!callerID.equals(currCallerID)) {
			Ros.getInstance().logWarn("[ActionClient] Previously received status from '" + callerID + "', now from '" + currCallerID + "'. Did the action server change?");
		}
		callerID = currCallerID;
		latestStatusTime = goalStatuses.header.stamp;

		synchronized (waitSync) {
			waitSync.notifyAll();
		}

		goalManager.updateStatuses(goalStatuses);

	}

	/**
	 * Callback method used when the subscriber of the action server's feedback
	 * topic receives a new action feedback message. This ActionClient's
	 * GoalManager gets informed about the newly arrived message.
	 * 
	 * @param actionFeedback
	 *            An action feedback message
	 * 
	 * @see GoalManager#updateFeedbacks(Message)
	 */
	protected void feedbackCB(T_ACTION_FEEDBACK actionFeedback) {
		goalManager.updateFeedbacks(actionFeedback);
	}

	/**
	 * Callback method used when the subscriber of the action server's result
	 * topic receives a new action result message. This ActionClient's
	 * GoalManager gets informed about the newly arrived message.
	 * 
	 * @param actionResult
	 *            An action result message
	 * 
	 * @see GoalManager#updateResults(Message)
	 */
	protected void resultCB(T_ACTION_RESULT actionResult) {
		goalManager.updateResults(actionResult);
	}

}
