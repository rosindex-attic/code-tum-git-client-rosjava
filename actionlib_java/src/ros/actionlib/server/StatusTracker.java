package ros.actionlib.server;

import ros.Ros;
import ros.actionlib.ActionSpec;
import ros.actionlib.util.GoalIDGenerator;
import ros.communication.Message;
import ros.communication.Time;
import ros.pkg.actionlib_msgs.msg.GoalID;
import ros.pkg.actionlib_msgs.msg.GoalStatus;

/**
 * A StatusTracker tracks the status of an action goal. If an action server
 * receives a cancel request for a specific GoalID without having received a
 * goal message with the same GoalID before, a StatusTracker can also be used to
 * track the GoalID, in order to cancel the action goal, in case it comes in
 * after the cancel request.
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
public class StatusTracker<T_ACTION_FEEDBACK extends Message, T_ACTION_GOAL extends Message, T_ACTION_RESULT extends Message, T_FEEDBACK extends Message, T_GOAL extends Message, T_RESULT extends Message> {

	/**
	 * The tracked action goal
	 */
	public final T_ACTION_GOAL actionGoal;

	/**
	 * The status of the tracked goal
	 */
	public GoalStatus goalStatus;

	/**
	 * The time, when the action server shall stop tracking this goal. If time
	 * is zero or in the future, the action server continues to publish the
	 * status of the goal. Otherwise the action server stops to do so.
	 */
	public Time destructionTime;
    
    /**
     * The action server's handler for the goal
     */
    public ServerGoalHandle<T_ACTION_FEEDBACK,
    		T_ACTION_GOAL,
    		T_ACTION_RESULT,
    		T_FEEDBACK,
    		T_GOAL,
    		T_RESULT> goalHandle;
    
	/**
	 * Constructor used to create a StatusTracker for tracking a GoalID.
	 * 
	 * @param goalID
	 *            GoalID to track
	 * @param status
	 *            The status of the goal with the given GoalID
	 */
	public StatusTracker(GoalID goalID, short status) {

		actionGoal = null;
		goalHandle = null;
		goalStatus = new GoalStatus();
		goalStatus.goal_id = goalID;
		goalStatus.status = status;
		destructionTime = new Time(0, 0);

	}

	/**
	 * Constructor used to create a StatusTracker for tracking an action goal.
	 * 
	 * @param actionGoal
	 *            The action goal, which shall be tracked
	 * @param spec
	 *            The action specification, which defines the type of the action
	 * @param idGen
	 *            A GoalIDGenerator used to create a GoalID, if it isn't set for
	 *            the goal
	 */
    public StatusTracker(T_ACTION_GOAL actionGoal,
    		ActionSpec<?,
				T_ACTION_FEEDBACK,
				T_ACTION_GOAL,
				T_ACTION_RESULT,
				T_FEEDBACK,
				T_GOAL,
				T_RESULT> spec,
			GoalIDGenerator idGen) {
    	
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
    
	/**
	 * Returns whether this StatusTracker tracks a GoalID, that was part of a
	 * cancel request, or a regular action goal.
	 * 
	 * @return <tt>true</tt> - if this StatusTracker tracks a GoalID, whose
	 *         associated goal wasn't received by the action server yet, but is
	 *         intended to be canceled as soon as it is received.<br>
	 *         <tt>false</tt> - otherwise
	 */
    public boolean isCancelRequestTracker() {
    	return (actionGoal == null);
    }
    
}
