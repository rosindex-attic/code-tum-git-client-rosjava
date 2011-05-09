package ros.actionlib.server;

import ros.Ros;
import ros.communication.Message;
import ros.pkg.actionlib_msgs.msg.GoalID;
import ros.pkg.actionlib_msgs.msg.GoalStatus;

public class ServerGoalHandle<T_ACTION_FEEDBACK extends Message, T_ACTION_GOAL extends Message, T_ACTION_RESULT extends Message, T_FEEDBACK extends Message, T_GOAL extends Message, T_RESULT extends Message> {

	protected T_ACTION_GOAL actionGoal;
	protected ActionServer<T_ACTION_FEEDBACK,T_ACTION_GOAL,T_ACTION_RESULT,T_FEEDBACK,T_GOAL,T_RESULT> actionServer;
	protected StatusTracker<T_ACTION_FEEDBACK,T_ACTION_GOAL,T_ACTION_RESULT,T_FEEDBACK,T_GOAL,T_RESULT> statusTracker;
	private static Object statusSync = new Object();

	protected ServerGoalHandle(StatusTracker<T_ACTION_FEEDBACK,T_ACTION_GOAL,T_ACTION_RESULT,T_FEEDBACK,T_GOAL,T_RESULT> statusTracker, ActionServer<T_ACTION_FEEDBACK,T_ACTION_GOAL,T_ACTION_RESULT,T_FEEDBACK,T_GOAL,T_RESULT> actionServer) {

		this.statusTracker = statusTracker;
		this.statusTracker.goalHandle = this;
		this.actionGoal = statusTracker.actionGoal;
		this.actionServer = actionServer;

	}

	protected boolean setCancelRequested() {

		if (actionServer == null || actionGoal == null){
			Ros.getInstance().logError("[ServerGoalHandle] You are attempting to call methods on an uninitialized goal handle");
			return false;
		}

		Ros.getInstance().logDebug("[ServerGoalHandle] Transitioning to a cancel requested state on goal, id: "+getGoalID().id+", stamp: "+(getGoalID().stamp.totalNsecs()/1000000)+"ms");

		boolean ok = false;
		synchronized (statusSync) {
			GoalStatus goalStatus = statusTracker.goalStatus;
			short status = goalStatus.status;
			switch (status) {
			case GoalStatus.PENDING:
				goalStatus.status = GoalStatus.RECALLING;
				actionServer.publishStatus();
				ok = true;
				break;
			case GoalStatus.ACTIVE:
				goalStatus.status = GoalStatus.PREEMPTING;
				actionServer.publishStatus();
				ok = true;
				break;
			}
		}
		return ok;

	}


	public void setAccepted(String text) {

		if (actionServer == null || actionGoal == null){
			Ros.getInstance().logError("[ServerGoalHandle] You are attempting to call methods on an uninitialized goal handle");
			return;
		}

		Ros.getInstance().logDebug("[ServerGoalHandle] Accepting goal, id: "+getGoalID().id+", stamp: "+(getGoalID().stamp.totalNsecs()/1000000)+"ms");

		synchronized (statusSync) {
			GoalStatus goalStatus = statusTracker.goalStatus;
			short status = goalStatus.status;
			switch (status) {
			case GoalStatus.PENDING:
				goalStatus.status = GoalStatus.ACTIVE;
				goalStatus.text = text;
				actionServer.publishStatus();
				break;
			case GoalStatus.RECALLING:
				goalStatus.status = GoalStatus.PREEMPTING;
				goalStatus.text = text;
				actionServer.publishStatus();
				break;
			default:
				Ros.getInstance().logError("[ServerGoalHandle] To transition to " +
						"an active state, the goal must be in state '"+GoalStatus.PENDING +
						"' (PENDING) or '"+GoalStatus.RECALLING+"' (RECALLING), " +
						"it is currently in state '"+goalStatus.status+"'");
				break;
			}        		
		}

	}

	public void setCanceled(T_RESULT result, String text) {

		if (actionServer == null || actionGoal == null){
			Ros.getInstance().logError("[ServerGoalHandle] You are attempting to call methods on an uninitialized goal handle");
			return;
		}		

		Ros.getInstance().logDebug("[ServerGoalHandle] Setting canceled status on goal, id: "+getGoalID().id+", stamp: "+(getGoalID().stamp.totalNsecs()/1000000)+"ms");

		synchronized (statusSync) {
			GoalStatus goalStatus = statusTracker.goalStatus;
			short status = goalStatus.status;
			switch (status) {
			case GoalStatus.PENDING:
			case GoalStatus.RECALLING:
				goalStatus.status = GoalStatus.RECALLED;
				goalStatus.text = text;
				actionServer.publishResult(goalStatus, result);
				break;
			case GoalStatus.ACTIVE:
			case GoalStatus.PREEMPTING:
				goalStatus.status = GoalStatus.PREEMPTED;
				goalStatus.text = text;
				actionServer.publishResult(goalStatus, result);
				break;
			default:
				Ros.getInstance().logError("[ServerGoalHandle] To transition to " +
						"a cancelled state, the goal must be in state '"+GoalStatus.PENDING +
						"' (PENDING), '"+GoalStatus.ACTIVE+"' (ACTIVE), '" +
						GoalStatus.PREEMPTING + "' (PREEMPTING) or '"+GoalStatus.RECALLING +
						"' (RECALLING). It is currently in state '"+goalStatus.status+"'");
				break;
			}
		}

	}

	public void setRejected(T_RESULT result, String text) {

		if (actionServer == null || actionGoal == null){
			Ros.getInstance().logError("[ServerGoalHandle] You are attempting to call methods on an uninitialized goal handle");
			return;
		}	

		Ros.getInstance().logDebug("[ServerGoalHandle] Setting status to 'REJECTED' on goal, id: "+getGoalID().id+", stamp: "+(getGoalID().stamp.totalNsecs()/1000000)+"ms");

		synchronized (statusSync) {
			GoalStatus goalStatus = statusTracker.goalStatus;
			short status = goalStatus.status;
			switch (status) {
			case GoalStatus.PENDING:
			case GoalStatus.RECALLING:
				goalStatus.status = GoalStatus.REJECTED;
				goalStatus.text = text;
				actionServer.publishResult(goalStatus, result);
				break;
			default:
				Ros.getInstance().logError("[ServerGoalHandle] To transition to " +
						"a rejected state, the goal must be in state '"+GoalStatus.PENDING +
						"' (PENDING) or '"+GoalStatus.RECALLING+"' (RECALLING). " +
						"It is currently in state '"+goalStatus.status+"'");
				break;
			}
		}

	}

	public void setAborted(T_RESULT result, String text) {

		if (actionServer == null || actionGoal == null){
			Ros.getInstance().logError("[ServerGoalHandle] You are attempting to call methods on an uninitialized goal handle");
			return;
		}	

		Ros.getInstance().logDebug("[ServerGoalHandle] Setting status to 'ABORTED' on goal, id: "+getGoalID().id+", stamp: "+(getGoalID().stamp.totalNsecs()/1000000)+"ms");

		synchronized (statusSync) {
			GoalStatus goalStatus = statusTracker.goalStatus;
			short status = goalStatus.status;
			switch (status) {
			case GoalStatus.ACTIVE:
			case GoalStatus.PREEMPTING:
				goalStatus.status = GoalStatus.ABORTED;
				goalStatus.text = text;
				actionServer.publishResult(goalStatus, result);
				break;
			default:
				Ros.getInstance().logError("[ServerGoalHandle] To transition to " +
						"an aborted state, the goal must be in state '"+GoalStatus.ACTIVE +
						"' (ACTIVE) or '"+GoalStatus.PREEMPTING+"' (PREEMPTING). " +
						"It is currently in state '"+goalStatus.status+"'");
				break;
			}
		}

	}

	public void setSucceeded(T_RESULT result, String text) {

		if (actionServer == null || actionGoal == null){
			Ros.getInstance().logError("[ServerGoalHandle] You are attempting to call methods on an uninitialized goal handle");
			return;
		}	

		Ros.getInstance().logDebug("[ServerGoalHandle] Setting status to 'SUCCEEDED' on goal, id: "+getGoalID().id+", stamp: "+(getGoalID().stamp.totalNsecs()/1000000)+"ms");

		synchronized (statusSync) {
			GoalStatus goalStatus = statusTracker.goalStatus;
			short status = goalStatus.status;
			switch (status) {
			case GoalStatus.ACTIVE:
			case GoalStatus.PREEMPTING:
				goalStatus.status = GoalStatus.SUCCEEDED;
				goalStatus.text = text;
				actionServer.publishResult(goalStatus, result);
				break;
			default:
				Ros.getInstance().logError("[ServerGoalHandle] To transition to " +
						"a succeeded state, the goal must be in state '"+GoalStatus.ACTIVE +
						"' (ACTIVE) or '"+GoalStatus.PREEMPTING+"' (PREEMPTING). " +
						"It is currently in state '"+goalStatus.status+"'");
				break;
			}
		}

	}

	public void cancelStatusUpdates() {
		
		statusTracker.destructionTime = Ros.getInstance().now();
		
	}
	
	public void publishFeedback(T_FEEDBACK feedback) {

		if (actionServer == null || actionGoal == null){
			Ros.getInstance().logError("[ServerGoalHandle] You are attempting to call methods on an uninitialized goal handle");
			return;
		}	

		Ros.getInstance().logDebug("[ServerGoalHandle] Publishing feedback for goal, id: "+getGoalID().id+", stamp: "+(getGoalID().stamp.totalNsecs()/1000000)+"ms");

		synchronized (statusSync) {
			actionServer.publishFeedback(statusTracker.goalStatus, feedback);
		}

	}

	public T_GOAL getGoal() {
		
		T_GOAL goal;
		if (actionGoal != null && actionServer != null) {
			goal = actionServer.spec.getGoalFromActionGoal(actionGoal);
		} else {
			Ros.getInstance().logError("[ServerGoalHandle] Attempt to getGoal() on an uninitialized ServerGoalHandle");
			goal = null;
		}
		return goal;
	      
	}

	public GoalID getGoalID() {

		GoalID goalId;
		if (actionServer != null && actionGoal != null) {
			synchronized (statusSync) {
				goalId = statusTracker.goalStatus.goal_id;
			}
		} else {
			Ros.getInstance().logError("[ServerGoalHandle] Attempt to getGoalID() on an uninitialized ServerGoalHandle");
			goalId = null;
		}
		return goalId;

	}

	public GoalStatus getGoalStatus() {

		GoalStatus goalStatus;
		if (actionServer != null && actionGoal != null) {
			synchronized (statusSync) {
				goalStatus = statusTracker.goalStatus;
			}
		} else {
			Ros.getInstance().logError("[ServerGoalHandle] Attempt to getGoalStatus() on an uninitialized ServerGoalHandle");
			goalStatus = null;
		}
		return goalStatus;

	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {

		if (!(o instanceof ServerGoalHandle<?,?,?,?,?,?>)) {
			return false;
		}

		ServerGoalHandle<T_ACTION_FEEDBACK,T_ACTION_GOAL,T_ACTION_RESULT,T_FEEDBACK,T_GOAL,T_RESULT> otherHandle = null;
		try {
			otherHandle = (ServerGoalHandle<T_ACTION_FEEDBACK,T_ACTION_GOAL,T_ACTION_RESULT,T_FEEDBACK,T_GOAL,T_RESULT>)o;	
		} catch (Exception cce) {
			return false;
		}

		if (actionGoal == null && otherHandle.actionGoal == null) {
			return true;
		}

		if (actionGoal == null || otherHandle.actionGoal == null) {
			return false;
		}

		return getGoalID().equals(otherHandle.getGoalID());

	}


}
