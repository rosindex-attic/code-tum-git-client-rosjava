package ros.actionlib.server;

import ros.communication.Message;

/**
 * An interface between a SimpleActionServer and user code. Allows user code to
 * react on callbacks due to goal and preempt requests. Implementations of this
 * interface may be used as a parameter for the SimpleActionServer's
 * constructors.<br>
 * The SimpleActionServerCallbacks interface can be used in two different modes
 * depending on whether the useBlockingGoalCallback flag is set or not, when one
 * of the SimpleActionServer's constructors is invoked:
 * <ul>
 * <li>Convenience mode: if the useBlockingGoalCallback flag is set to
 * <tt>true</tt>,<br>
 * a new thread gets created which services the
 * {@link #blockingGoalCallback(Message, SimpleActionServer)} method by
 * conveniently providing the next goal to the callback. This also allows the
 * user to have time consuming operations inside of this callback method. An
 * invocation of the {@link #preemptCallback(SimpleActionServer)} method signals
 * the presence of a new goal request and the user has to make sure, that a
 * currently worked on goal request gets canceled. In this mode the
 * {@link #goalCallback(SimpleActionServer)} method is never used.</li>
 * 
 * <li>Normal mode: if the useBlockingGoalCallback flag is set to <tt>false</tt>
 * ,<br>
 * the user will be responsible for picking up new goals from the action server
 * and only gets informed on the arrival of new goal and preempt requests
 * through calls of the {@link #goalCallback(SimpleActionServer)} and
 * {@link #preemptCallback(SimpleActionServer)} methods. This methods have to
 * return immediately and thus may only contain non-blocking code.</li>
 * </ul>
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
public interface SimpleActionServerCallbacks<T_ACTION_FEEDBACK extends Message,T_ACTION_GOAL extends Message,T_ACTION_RESULT extends Message,T_FEEDBACK extends Message,T_GOAL extends Message,T_RESULT extends Message> {

	/**
	 * Gets called by an action server, if the server's constructor was invoked
	 * with the useBlockingGoalCallback parameter set to <tt>true</tt> and a new
	 * goal request has to be worked on. Time consuming operations may take
	 * place during the callback. Please consider that it might be necessary to
	 * react on a call of the preemptCallback method canceling the current task.
	 * The final state of the current goal has to be set before this method
	 * returns. This can be achieved by calling one of the following methods of
	 * a SimpleActionServer:
	 * <ul>
	 * <li>{@link SimpleActionServer#setAborted()}</li>
	 * <li>{@link SimpleActionServer#setAborted(Message, String)}</li>
	 * <li>{@link SimpleActionServer#setPreempted()}</li>
	 * <li>{@link SimpleActionServer#setPreempted(Message, String)}</li>
	 * <li>{@link SimpleActionServer#setSucceeded()}</li>
	 * <li>{@link SimpleActionServer#setSucceeded(Message, String)}</li>
	 * </ul>
	 * 
	 * @param goal
	 *            The new goal, the action server is intended to work on.
	 * @param actionServer
	 *            The action server, which issued the callback.
	 */
	public void blockingGoalCallback(T_GOAL goal,
			SimpleActionServer<T_ACTION_FEEDBACK,
				T_ACTION_GOAL,
				T_ACTION_RESULT,
				T_FEEDBACK,
				T_GOAL,
				T_RESULT> actionServer);
	
	/**
	 * Gets called by an action server, if the server's constructor was invoked
	 * with the useBlockingGoalCallback parameter set to <tt>false</tt> and a
	 * new goal request has to be worked on. The user has to fetch the goal from
	 * the server and delegate the task. Time consuming operations are not
	 * allowed during the execution of this callback.
	 * 
	 * @param actionServer
	 *            The action server, which issued the callback.
	 */
	public void goalCallback(
			SimpleActionServer<T_ACTION_FEEDBACK,
				T_ACTION_GOAL,
				T_ACTION_RESULT,
				T_FEEDBACK,
				T_GOAL,
				T_RESULT> actionServer);
	
	/**
	 * Gets called when a new goal request arrives while the action server is
	 * still working on another goal. The user has to cancel the work on the
	 * current goal. Time consuming operations are not allowed during the
	 * execution of this callback.
	 * 
	 * @param actionServer
	 *            The action server, which issued the callback.
	 */
	public void preemptCallback(
			SimpleActionServer<T_ACTION_FEEDBACK,
				T_ACTION_GOAL,
				T_ACTION_RESULT,
				T_FEEDBACK,
				T_GOAL,
				T_RESULT> actionServer);
	
}
