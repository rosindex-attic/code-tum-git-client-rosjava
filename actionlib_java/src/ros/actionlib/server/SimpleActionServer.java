package ros.actionlib.server;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import ros.NodeHandle;
import ros.Ros;
import ros.actionlib.ActionSpec;
import ros.communication.Message;
import ros.pkg.actionlib_msgs.msg.GoalStatus;

public class SimpleActionServer<T_ACTION_FEEDBACK extends Message, T_ACTION_GOAL extends Message, T_ACTION_RESULT extends Message, T_FEEDBACK extends Message, T_GOAL extends Message, T_RESULT extends Message> implements ActionServerCallbacks<T_ACTION_FEEDBACK,T_ACTION_GOAL,T_ACTION_RESULT,T_FEEDBACK,T_GOAL,T_RESULT> {

    protected ActionServer<T_ACTION_FEEDBACK,T_ACTION_GOAL,T_ACTION_RESULT,T_FEEDBACK,T_GOAL,T_RESULT> actionServer;
    protected SimpleActionServerCallbacks<T_ACTION_FEEDBACK,T_ACTION_GOAL,T_ACTION_RESULT,T_FEEDBACK,T_GOAL,T_RESULT> callbacks;
    
    protected ServerGoalHandle<T_ACTION_FEEDBACK,T_ACTION_GOAL,T_ACTION_RESULT,T_FEEDBACK,T_GOAL,T_RESULT> currentGoal;
    protected ServerGoalHandle<T_ACTION_FEEDBACK,T_ACTION_GOAL,T_ACTION_RESULT,T_FEEDBACK,T_GOAL,T_RESULT> nextGoal;
    
    protected volatile boolean newGoal = false;
    protected volatile boolean preemptRequest = false;
    protected volatile boolean newGoalPreemptRequest = false;
    
    protected volatile boolean killCallbackThread = false;
    private Object threadSync = new Object();
    protected Thread callbackThread = null;
    
    protected ReentrantLock lock = new ReentrantLock(true); // fair lock
    protected Condition c = lock.newCondition();

    protected boolean useBlockingGoalCallback = false;
    
	
	public SimpleActionServer(String nameSpace, ActionSpec<?, T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT> spec, SimpleActionServerCallbacks<T_ACTION_FEEDBACK,T_ACTION_GOAL,T_ACTION_RESULT,T_FEEDBACK,T_GOAL,T_RESULT> callbacks, boolean useBlockingGoalCallback, boolean autoStart) {

		this.callbacks = callbacks;
		this.actionServer = new ActionServer<T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT> (nameSpace, spec, this, autoStart);
		if (useBlockingGoalCallback) {
			this.useBlockingGoalCallback = true;
			startCallbackThread();
		}
		
	}

	public SimpleActionServer(NodeHandle parent, String nameSpace, ActionSpec<?, T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT> spec, SimpleActionServerCallbacks<T_ACTION_FEEDBACK,T_ACTION_GOAL,T_ACTION_RESULT,T_FEEDBACK,T_GOAL,T_RESULT> callbacks, boolean useBlockingGoalCallback, boolean autoStart) {

		this.callbacks = callbacks;
		this.actionServer = new ActionServer<T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT> (parent, nameSpace, spec, this, autoStart);
		if (useBlockingGoalCallback) {
			this.useBlockingGoalCallback = true;
			startCallbackThread();
		}
		
	}
	
	public T_GOAL acceptNewGoal() {

		lock.lock();
		try {

			if (!newGoal || nextGoal == null) {
				Ros.getInstance().logError("[SimpleActionServer] Attempting to accept the next goal when a new goal is not available");
				return null;
			}

			if (isActive() 
					&& currentGoal != null 
					&& !currentGoal.equals(nextGoal)) {
				
				currentGoal.setCanceled(actionServer.spec.createResultMessage(), "This goal was canceled because another goal was received by the simple action server");
				
			}

			Ros.getInstance().logDebug("[SimpleActionServer] Accepting a new goal");

			currentGoal = nextGoal;
			nextGoal = null;
		    newGoal = false;
		    
		    preemptRequest = newGoalPreemptRequest;
		    newGoalPreemptRequest = false;		    
		    
		    currentGoal.setAccepted("This goal has been accepted by the simple action server");
		    return currentGoal.getGoal();
		    
		} finally {
			lock.unlock();
		}
		
	}
	
	public boolean isNewGoalAvailable() {
		return newGoal;		
	}

	public boolean isPreemptRequested() {
		return newGoalPreemptRequest;		
	}
	
	public boolean isActive() {

		if (currentGoal == null) {
			return false;
		}

		short currStatus = currentGoal.getGoalStatus().status;
		return (currStatus == GoalStatus.ACTIVE || currStatus == GoalStatus.PREEMPTING);
		
	}

	public void setSucceeded() {
		setSucceeded(actionServer.spec.createResultMessage(), "");
	}
	
	public void setSucceeded(T_RESULT result, String text) {

		lock.lock();
		try {
			Ros.getInstance().logDebug("[SimpleActionServer] setting the current goal to 'SUCCEEDED'");
			currentGoal.setSucceeded(result, text);
		} finally {
			lock.unlock();
		}
		
	}

	public void setAborted() {
		setAborted(actionServer.spec.createResultMessage(), "");
	}
	
	public void setAborted(T_RESULT result, String text) {
		
		lock.lock();
		try {
			Ros.getInstance().logDebug("[SimpleActionServer] setting the current goal to 'ABORTED'");
			currentGoal.setAborted(result, text);
		} finally {
			lock.unlock();
		}
		
	}

	public void setPreempted() {
		setPreempted(actionServer.spec.createResultMessage(), "");
	}
	
	public void setPreempted(T_RESULT result, String text) {
		
		lock.lock();
		try {
			Ros.getInstance().logDebug("[SimpleActionServer] setting the current goal to a canceled state'");
			currentGoal.setCanceled(result, text);
		} finally {
			lock.unlock();
		}
		
	}
	
	public void publishFeedback(T_FEEDBACK feedback) {
		currentGoal.publishFeedback(feedback);
	}
	
	public void start() {
		
		if (callbacks == null) {
			Ros.getInstance().logError("[SimpleActionServer] Can't start an action server without registered user callbacks.");
			return;
		}
		actionServer.start();
	}

	public void shutdown() {
		stopCallbackThread();
		actionServer.shutdown();
	}

	protected void startCallbackThread() {

		synchronized (threadSync) {
			if (callbackThread == null) {
				callbackThread = new Thread() {
					
					@Override
					public void run() {
						
						while (actionServer != null && actionServer.getNodeHandle().ok()) {

							if (killCallbackThread) {
								killCallbackThread = false;
								return;
							}

							lock.lock();
							try {
								
								if (isActive()) {
									Ros.getInstance().logError("[SimpleActionServer] This code should never be reached with an active goal.");
								} else if (isNewGoalAvailable()) {
									
									T_GOAL goal = acceptNewGoal();
									
									lock.unlock();
									boolean exception = false;
									try {
										callbacks.blockingGoalCallback(goal);
									} catch (Exception e) {
										Ros.getInstance().logError("[SimpleActionServer] Exception in user callback, current goal gets aborted: "+e.toString());
										exception = true;
									} finally {
										lock.lock();	
									}

									if (exception) {
										setAborted(actionServer.spec.createResultMessage(), "This goal was set to 'ABORTED' by the simple action server due to an exception in the user callback (blockingGoalCallback()).");	
									} else if (isActive()) {
										Ros.getInstance().logError("The blockingGoalCallback did not set the goal to a terminal status.\nThis is a bug in the user's action server implementation, which has to be fixed!\n For now, the current goal gets set to 'ABORTED'.");
										setAborted(actionServer.spec.createResultMessage(), "This goal was aborted by the simple action server. The user should have set a terminal status on this goal but did not.");
									}
									
								} else {
									if (!killCallbackThread) {
										c.wait(1000);	
									}
								}
								
							} catch (InterruptedException e) {
							} finally {
								lock.unlock();
							}

						}
					}
					
				};
				callbackThread.start();
			} else {
				Ros.getInstance().logWarn("[SimpleActionServer] startCallbackThread(): callback thread is already running");
			}
		}

	}

	protected void stopCallbackThread() {

		synchronized (threadSync) {
			if (callbackThread != null) {
				
				killCallbackThread = true;
				
				lock.lock();
				try{
					c.notifyAll();	
				} finally {
					lock.unlock();
				}
				
				try {
					callbackThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				callbackThread = null;
				
			} else {
				Ros.getInstance().logWarn("[SimpleActionClient] stopCallbackThread(): callback thread is not running");
			}
		}

	}
	
	@Override
	public void cancelCallback(ServerGoalHandle<T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT> goalToCancel) {

		lock.lock();
		try {
			
			Ros.getInstance().logDebug("[SimpleActionServer] received new cancel goal request");

			if (currentGoal != null && goalToCancel.equals(currentGoal)) {
			
				Ros.getInstance().logDebug("[SimpleActionServer] Setting preemptRequest flag for the current goal and invoking callback");

				preemptRequest = true;
				if (callbacks != null) {
					callbacks.preemptCallback();
				}
				
			} else if (nextGoal != null && goalToCancel.equals(nextGoal)) {
				
				Ros.getInstance().logDebug("[SimpleActionServer] Setting preemptRequest flag for the next goal");
				newGoalPreemptRequest = true;
				
			}
			
		} finally {
			lock.unlock();
		}
		
	}

	@Override
	public void goalCallback(ServerGoalHandle<T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT> goal) {

		lock.lock();
		try {
			
			Ros.getInstance().logDebug("[SimpleActionServer] received a new goal");
			
			long goalTime = goal.getGoalID().stamp.totalNsecs();
			if ((currentGoal == null || goalTime >= currentGoal.getGoalID().stamp.totalNsecs())
					&& (nextGoal == null || goalTime >= nextGoal.getGoalID().stamp.totalNsecs())) {
				
				if (nextGoal != null && (currentGoal == null || !nextGoal.equals(currentGoal))) {
					nextGoal.setCanceled(actionServer.spec.createResultMessage(), 
				    		  "This goal was canceled because another goal was " +
				    		  "received by the simple action server");
				}

				nextGoal = goal;
				newGoal = true;
				newGoalPreemptRequest = false;
				
				if (isActive()) {
					preemptRequest = true;
					callbacks.preemptCallback();
				}
				
				callbacks.goalCallback();
				
				if (useBlockingGoalCallback) {
					c.notifyAll();	
				}
				
			} else {
			      goal.setCanceled(actionServer.spec.createResultMessage(), 
			    		  "This goal was canceled because another goal was " +
			    		  "received by the simple action server");
			}
			
		} finally {
			lock.unlock();
		}
		
	}
	
}
