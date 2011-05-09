package ros.actionlib.server;

import ros.Ros;
import ros.actionlib.ActionSpec;
import ros.actionlib.util.GoalIDGenerator;
import ros.communication.Message;
import ros.communication.Time;
import ros.pkg.actionlib_msgs.msg.GoalID;
import ros.pkg.actionlib_msgs.msg.GoalStatus;

public class StatusTracker<T_ACTION_FEEDBACK extends Message, T_ACTION_GOAL extends Message, T_ACTION_RESULT extends Message, T_FEEDBACK extends Message, T_GOAL extends Message, T_RESULT extends Message> {

    public final T_ACTION_GOAL actionGoal;
    public GoalStatus goalStatus;
    public Time destructionTime;
    public ServerGoalHandle<T_ACTION_FEEDBACK,T_ACTION_GOAL,T_ACTION_RESULT,T_FEEDBACK,T_GOAL,T_RESULT> goalHandle;
    
    public StatusTracker(GoalID goalID, short status){
        
        actionGoal = null;
        goalHandle = null;
        goalStatus = new GoalStatus();
    	goalStatus.goal_id = goalID;
        goalStatus.status = status;
        destructionTime = new Time(0,0);
        
    }
    
    public StatusTracker(T_ACTION_GOAL actionGoal, ActionSpec<?,?,T_ACTION_GOAL,?,?,?,?> spec, GoalIDGenerator idGen) {
    	
    	this.actionGoal = actionGoal;
    	goalHandle = null;
    	goalStatus = new GoalStatus();
    	goalStatus.goal_id = spec.getGoalIDFromActionGoal(actionGoal);
    	goalStatus.status = GoalStatus.PENDING;
    	destructionTime = new Time(0,0);

    	if (goalStatus.goal_id.id == null || goalStatus.goal_id.id.isEmpty()) {
    		goalStatus.goal_id = idGen.generateID();
    	}

    	if (goalStatus.goal_id.stamp.isZero()) {
    		goalStatus.goal_id.stamp = Ros.getInstance().now();
    	}
    	
	}
    
    public boolean isCancelRequestTracker() {
    	return (actionGoal == null);
    }
    
}
