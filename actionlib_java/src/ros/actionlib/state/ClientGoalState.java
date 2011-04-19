package ros.actionlib.state;

import ros.Ros;
import ros.pkg.actionlib_msgs.msg.GoalStatus;

/**
 * 
 * A ClientGoalState represents an ActionClient's goal state. It defines an
 * enumeration of possible states and stores one of them as the current state.
 * The states are:
 * <ul>
 * <li>PENDING</li>
 * <li>ACTIVE</li>
 * <li>REJECTED</li>
 * <li>PREEMPTED</li>
 * <li>SUCCEEDED</li>
 * <li>ABORTED</li>
 * <li>LOST</li>
 * </ul>
 * 
 * @author Alexander C. Perzylo, perzylo@cs.tum.edu
 * 
 */
public class ClientGoalState {

	/**
	 * Current goal state
	 */
	private StateEnum state;

	/**
	 * Enumeration of possible states
	 */
	public static enum StateEnum {
		PENDING, ACTIVE, REJECTED, PREEMPTED, SUCCEEDED, ABORTED, LOST;
	}

	/**
	 * Simple constructor.
	 * 
	 * @param initialState
	 *            The initial state
	 */
	public ClientGoalState(StateEnum initialState) {
		this.state = initialState;
	}

	/**
	 * Constructor used to create a ClientGoalState with an initial state
	 * defined by a given GoalStatus message.
	 * 
	 * @param goalStatus
	 *            A goal status message
	 */
	public ClientGoalState(GoalStatus goalStatus) {
		fromGoalStatus(goalStatus);
	}

	/**
	 * Gets current state.
	 * 
	 * @return The current state
	 */
	public StateEnum getState() {
		return this.state;
	}

	/**
	 * Sets current state.
	 * 
	 * @param state
	 *            A new state
	 */
	public void setState(StateEnum state) {
		this.state = state;
	}

	/**
	 * Checks whether the current state is a final state or not.
	 * 
	 * @return <tt>true</tt> - if current state is a final state<br>
	 *         <tt>false</tt> - otherwise
	 */
	public boolean isDone() {
		return (!this.state.equals(StateEnum.PENDING) 
				&& !this.state.equals(StateEnum.ACTIVE));
	}

	/**
	 * Sets the current state to a new value defined by the given GoalStatus
	 * message.
	 * 
	 * @param goalStatus
	 *            A GoalStatus message
	 */
	public void fromGoalStatus(GoalStatus goalStatus) {

		switch (goalStatus.status) {
		case GoalStatus.PREEMPTED:
			this.state = StateEnum.PREEMPTED;
			break;
		case GoalStatus.SUCCEEDED:
			this.state = StateEnum.SUCCEEDED;
			break;
		case GoalStatus.ABORTED:
			this.state = StateEnum.ABORTED;
			break;
		case GoalStatus.REJECTED:
			this.state = StateEnum.REJECTED;
			break;
		default:
			this.state = StateEnum.LOST;
			Ros.getInstance().logError("[ClientGoalState] Cannot convert GoalStatus '" + goalStatus.status + "' to a ClientGoalState");
		}

	}

	@Override
	public boolean equals(Object o) {

		if (o != null) {
			if (o instanceof ClientGoalState)
				return this.state.equals(((ClientGoalState) o).getState());
			if (o instanceof StateEnum) {
				return this.state.equals((StateEnum) o);
			}
		}
		return false;

	}

	@Override
	public String toString() {
		return this.state.toString();
	}

}
