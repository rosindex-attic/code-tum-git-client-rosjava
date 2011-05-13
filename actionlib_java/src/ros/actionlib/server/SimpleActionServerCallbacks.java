package ros.actionlib.server;

import ros.communication.Message;

public interface SimpleActionServerCallbacks<T_ACTION_FEEDBACK extends Message,T_ACTION_GOAL extends Message,T_ACTION_RESULT extends Message,T_FEEDBACK extends Message,T_GOAL extends Message,T_RESULT extends Message> {

	public void goalCallback();
	
	public void preemptCallback();

	public void blockingGoalCallback(T_GOAL goal, SimpleActionServer<T_ACTION_FEEDBACK,T_ACTION_GOAL,T_ACTION_RESULT,T_FEEDBACK,T_GOAL,T_RESULT> actionServer);
	
}
