package ros.actionlib;

import java.lang.reflect.Field;

import ros.NodeHandle;
import ros.Ros;
import ros.RosException;
import ros.actionlib.client.ActionClient;
import ros.actionlib.client.SimpleActionClient;
import ros.actionlib.server.ActionServer;
import ros.actionlib.server.ActionServerCallbacks;
import ros.actionlib.server.SimpleActionServer;
import ros.actionlib.server.SimpleActionServerCallbacks;
import ros.communication.Message;
import ros.communication.Time;
import ros.pkg.actionlib_msgs.msg.GoalID;
import ros.pkg.actionlib_msgs.msg.GoalStatus;
import ros.pkg.roslib.msg.Header;

/**
 * 
 * An ActionSpec defines the action on which an action client and action server
 * communicate. It provides methods to create all necessary action messages and
 * extract specific pieces of data from given messages. ActionSpecs are needed
 * as a parameter to instantiate an action client or server. For the user's 
 * convenience the ActionSpec class contains methods to build action clients 
 * and servers.
 * <br>
 * Example:
 * <blockquote>
 * <pre>
 * {@code 
 * ActionSpec<FibonacciAction,
 * 	FibonacciActionFeedback,
 * 	FibonacciActionGoal,
 * 	FibonacciActionResult,
 * 	FibonacciFeedback,
 * 	FibonacciGoal,
 * 	FibonacciResult> spec;
 * 
 * spec = new ActionSpec<FibonacciAction,
 * 	FibonacciActionFeedback,
 * 	FibonacciActionGoal,
 * 	FibonacciActionResult,
 * 	FibonacciFeedback,
 * 	FibonacciGoal,
 * 	FibonacciResult> (FibonacciAction.class);
 * 
 * SimpleActionClient<FibonacciActionFeedback,
 * 	FibonacciActionGoal,
 * 	FibonacciActionResult,
 * 	FibonacciFeedback,
 * 	FibonacciGoal,
 * 	FibonacciResult> sac = spec.createSimpleActionClient("fibonacci");
 * }
 * </pre>
 * </blockquote>
 * In order to cut the declaration part short, a specialized ActionSpec can be
 * derived and used (e.g. FibonacciActionSpec, which is part of ROS package 
 * 'test_actionlib_java'):
 * <blockquote>
 * <pre>
 * {@code 
 * FibonacciActionSpec spec = new FibonacciActionSpec();
 * FibonacciSimpleActionClient sac = spec.buildSimpleActionClient("fibonacci");
 * }
 * </pre>
 * </blockquote>
 * 
 * 
 * @author Alexander C. Perzylo, perzylo@cs.tum.edu
 * 
 * @param <T_ACTION>
 *            action message
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
public class ActionSpec<T_ACTION extends Message, T_ACTION_FEEDBACK extends Message, T_ACTION_GOAL extends Message, T_ACTION_RESULT extends Message, T_FEEDBACK extends Message, T_GOAL extends Message, T_RESULT extends Message> {

	/**
	 * Name of action
	 */
	public final String actionName;

	/**
	 * Class of action message
	 */
	public final Class<T_ACTION> clsAction;

	/**
	 * Class of action feedback message
	 */
	public final Class<T_ACTION_FEEDBACK> clsActionFeedback;

	/**
	 * Class of action goal message
	 */
	public final Class<T_ACTION_GOAL> clsActionGoal;

	/**
	 * Class of action result message
	 */
	public final Class<T_ACTION_RESULT> clsActionResult;

	/**
	 * Class of feedback message
	 */
	public final Class<T_FEEDBACK> clsFeedback;

	/**
	 * Class of goal message
	 */
	public final Class<T_GOAL> clsGoal;

	/**
	 * Class of result message
	 */
	public final Class<T_RESULT> clsResult;

	/**
	 * Constructor. Checks if all needed fields are present in the given action
	 * message class object and the referenced sub-messages. If there is
	 * something missing, calling most of the methods of this class will result
	 * in a NullPointerException. The isValid() method may be used to make sure
	 * the ActionSpec was correctly instantiated.
	 * 
	 * @param clsAction
	 *            The class object of an action message
	 */
	@SuppressWarnings("unchecked")
	public ActionSpec(Class<T_ACTION> clsAction) {

		Class<T_ACTION> cA;
		Class<T_ACTION_FEEDBACK> cAF;
		Class<T_ACTION_GOAL> cAG;
		Class<T_ACTION_RESULT> cAR;
		Class<T_FEEDBACK> cF;
		Class<T_GOAL> cG;
		Class<T_RESULT> cR;
		String name;

		try {

			cA = clsAction;

			Field f = cA.getField("action_feedback");
			cAF = (Class<T_ACTION_FEEDBACK>) f.getType();
			f = cA.getField("action_goal");
			cAG = (Class<T_ACTION_GOAL>) f.getType();
			f = cA.getField("action_result");
			cAR = (Class<T_ACTION_RESULT>) f.getType();

			f = cAF.getField("feedback");
			cF = (Class<T_FEEDBACK>) f.getType();
			f = cAG.getField("goal");
			cG = (Class<T_GOAL>) f.getType();
			f = cAR.getField("result");
			cR = (Class<T_RESULT>) f.getType();

			name = cA.getSimpleName();

		} catch (Exception e) {

			cA = null;
			cAF = null;
			cAG = null;
			cAR = null;
			cF = null;
			cG = null;
			cR = null;
			name = null;
			Ros.getInstance().logError("[ActionSpec] Wrong type definitions or action class used for ActionSpec instantiation!");
			e.printStackTrace();

		}

		this.clsAction = cA;
		this.clsActionFeedback = cAF;
		this.clsActionGoal = cAG;
		this.clsActionResult = cAR;
		this.clsFeedback = cF;
		this.clsGoal = cG;
		this.clsResult = cR;
		this.actionName = name;

	}

	/**
	 * Checks, if entailed information is complete in order to use it with an
	 * action client/server.
	 * 
	 * @return <tt>true</tt> - if this ActionSpec was instantiated correctly<br>
	 *         <tt>false</tt> - otherwise (if that happens, please check if the
	 *         given class parameters are correct and if their order complies
	 *         with the class definition.)
	 */
	public boolean isValid() {

		return (clsAction != null && clsActionFeedback != null
				&& clsActionGoal != null && clsActionResult != null
				&& clsFeedback != null && clsGoal != null && clsResult != null);

	}

	/**
	 * Creates an ActionClient using this ActionSpec and a given name space.
	 * 
	 * @param nameSpace
	 *            The name space to communicate within (specified by the action
	 *            server)
	 * @return An ActionClient object
	 */
	public ActionClient<T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT> buildActionClient(String nameSpace) {

		ActionClient<T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT> ac = null;
		try {
			ac = new ActionClient<T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT>(nameSpace, this);
		} catch (RosException e) {
			e.printStackTrace();
		}
		return ac;
	}

	/**
	 * Creates an ActionClient using this ActionSpec and a given node handle and
	 * name space.
	 * 
	 * @param nodeHandle
	 *            The node handle to be used by the ActionClient as its parent 
	 *            node
	 * @param nameSpace
	 *            The name space to communicate within (specified by the action
	 *            server)
	 * @return An ActionClient object
	 */
	public ActionClient<T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT> buildActionClient(NodeHandle nodeHandle, String nameSpace) {

		ActionClient<T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT> ac = null;
		try {
			ac = new ActionClient<T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT>(nodeHandle, nameSpace, this);
		} catch (RosException e) {
			e.printStackTrace();
		}
		return ac;

	}

	/**
	 * Creates a SimpleActionClient using this ActionSpec and a given name
	 * space. The SimpleActionClient gets parameterized to create a new thread
	 * to service the callbacks. This spares the users the effort to call spin()
	 * or spinOnce() themselves.
	 * 
	 * @param nameSpace
	 *            The name space to communicate within (specified by the action
	 *            server)
	 * @return A SimpleActionClient object
	 */
	public SimpleActionClient<T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT> buildSimpleActionClient(String nameSpace) {

		SimpleActionClient<T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT> sac = null;
		try {
			sac = new SimpleActionClient<T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT>(nameSpace, this, true);
		} catch (RosException e) {
			e.printStackTrace();
		}
		return sac;

	}

	/**
	 * Creates a SimpleActionClient using this ActionSpec, a given node handle 
	 * and name space. The SimpleActionClient gets parameterized to create a 
	 * new thread to service the callbacks. This spares the users the effort to
	 * call spin() or spinOnce() themselves.
	 * 
	 * @param nodeHandle
	 *            The node handle to be used by the SimpleActionClient as its 
	 *            parent node
	 * @param nameSpace
	 *            The name space to communicate within (specified by the action
	 *            server)
	 * @return A SimpleActionClient object
	 */
	public SimpleActionClient<T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT> buildSimpleActionClient(NodeHandle nodeHandle, String nameSpace) {

		SimpleActionClient<T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT> sac = null;
		try {
			sac = new SimpleActionClient<T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT>(nodeHandle, nameSpace, this, true);
		} catch (RosException e) {
			e.printStackTrace();
		}
		return sac;

	}

	/**
	 * Creates an ActionServer using this ActionSpec, a given name space and
	 * a callback object intended to be used by the server.
	 * 
	 * @param nameSpace 
	 *            The name space to communicate within
	 * @param callbacks 
	 *            A callback object providing callback methods, which get 
	 *            called by the server 
	 * @param autoStart
	 *            A flag, indicating whether the server shall be immediately 
	 *            started or not 
	 * @return An ActionServer object
	 */
	public ActionServer<T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT> buildActionServer(
			String nameSpace, 
			ActionServerCallbacks<T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT> callbacks,
			boolean autoStart) {
	
		return new ActionServer<T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT> (
				nameSpace, this, callbacks, autoStart);
		
	}
	
	/**
	 * Creates an ActionServer using this ActionSpec, a given node handle
	 * and name space and a callback object intended to be used by the server.
	 * 
	 * @param nodeHandle
	 *            The node handle to be used by the ActionServer as its parent
	 *            node
	 * @param nameSpace 
	 *            The name space to communicate within
	 * @param callbacks 
	 *            A callback object providing callback methods, which get 
	 *            called by the server 
	 * @param autoStart
	 *            A flag, indicating whether the server shall be immediately 
	 *            started or not 
	 * @return An ActionServer object
	 */
	public ActionServer<T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT> buildActionServer(
			NodeHandle nodeHandle, String nameSpace, 
			ActionServerCallbacks<T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT> callbacks,
			boolean autoStart) {
	
		return new ActionServer<T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT> (
				nodeHandle, nameSpace, this, callbacks, autoStart);
		
	}
	
	/**
	 * Creates a SimpleActionServer using this ActionSpec, a given name
	 * space and a callback object intended to be used by the server. The  
	 * useBlockingGoalCallback parameter specifies which callback method
	 * will be used on the reception of goal messages.
	 * 
	 * @param nameSpace
	 *            The name space to communicate within
	 * @param callbacks 
	 *            A callback object providing callback methods, which get 
	 *            called by the server
	 * @param useBlockingGoalCallback
	 *            A Flag, indicating whether the blocking or non-blocking 
	 *            callback method shall be used  
	 * @param autoStart
	 *            A flag, indicating whether the server shall be immediately 
	 *            started or not 
	 * @return A SimpleActionServer object
	 * 
	 * @see SimpleActionServerCallbacks#blockingGoalCallback(Message)
	 * @see SimpleActionServerCallbacks#goalCallback()
	 */
	public SimpleActionServer<T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT> buildSimpleActionServer(
			String nameSpace, 
			SimpleActionServerCallbacks<T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT> callbacks,
			boolean useBlockingGoalCallback, boolean autoStart) {
	
		return new SimpleActionServer<T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT> (
				nameSpace, this, callbacks, useBlockingGoalCallback, autoStart);
		
	}
	
	/**
	 * Creates a SimpleActionServer using this ActionSpec, a given node handle
	 * and name space and a callback object intended to be used by the server.
	 * The useBlockingGoalCallback parameter specifies which callback method
	 * will be used on the reception of goal messages.
	 * 
	 * @param nodeHandle
	 *            The node handle to be used by the ActionServer as its parent
	 *            node
	 * @param nameSpace
	 *            The name space to communicate within
	 * @param callbacks 
	 *            A callback object providing callback methods, which get 
	 *            called by the server
	 * @param useBlockingGoalCallback
	 *            A Flag, indicating whether the blocking or non-blocking 
	 *            callback method shall be used  
	 * @param autoStart
	 *            A flag, indicating whether the server shall be immediately 
	 *            started or not 
	 * @return A SimpleActionServer object
	 * 
	 * @see SimpleActionServerCallbacks#blockingGoalCallback(Message)
	 * @see SimpleActionServerCallbacks#goalCallback()
	 */
	public SimpleActionServer<T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT> buildSimpleActionServer(
			NodeHandle nodeHandle, String nameSpace, 
			SimpleActionServerCallbacks<T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT> callbacks,
			boolean useBlockingGoalCallback, boolean autoStart) {
	
		return new SimpleActionServer<T_ACTION_FEEDBACK, T_ACTION_GOAL, T_ACTION_RESULT, T_FEEDBACK, T_GOAL, T_RESULT> (
				nodeHandle, nameSpace, this, callbacks, useBlockingGoalCallback, autoStart);
		
	}

	/**
	 * Creates an action message.
	 * 
	 * @return A new action message object
	 */
	public T_ACTION createActionMessage() {

		T_ACTION a = null;
		try {
			a = clsAction.newInstance();
		} catch (Exception e) {
		}
		return a;

	}

	/**
	 * Creates an action feedback message.
	 * 
	 * @return A new action feedback message object
	 */
	public T_ACTION_FEEDBACK createActionFeedbackMessage() {

		T_ACTION_FEEDBACK a = null;
		try {
			a = clsActionFeedback.newInstance();
		} catch (Exception e) {
		}
		return a;

	}
	
	/**
	 * Creates an action feedback message with the given feedback 
	 * message, timestamp and GoalStatus.
	 * 
	 * @param feedback
	 *            A feedback message
	 * @param t
	 *            The timestamp of the action feedback message
	 * @param gs
	 *            The GoalStatus
	 * @return A new action feedback message object
	 */
	public T_ACTION_FEEDBACK createActionFeedbackMessage(T_FEEDBACK feedback, Time t, GoalStatus gs) {

		T_ACTION_FEEDBACK a = null;
		try {
			a = clsActionFeedback.newInstance();
			
			Header header = new Header();
			header.stamp = t;

			Field f = clsActionFeedback.getField("feedback");
			f.set(a, feedback);
			f = clsActionFeedback.getField("status");
			f.set(a, gs);
			f = clsActionFeedback.getField("header");
			f.set(a, header);
			
		} catch (Exception e) {
		}
		return a;

	}

	/**
	 * Creates an action goal message.
	 * 
	 * @return A new action goal message object
	 */
	public T_ACTION_GOAL createActionGoalMessage() {

		T_ACTION_GOAL a = null;
		try {
			a = clsActionGoal.newInstance();
		} catch (Exception e) {
		}
		return a;

	}
	
	/**
	 * Creates an action goal message with the given goal message,
	 * timestamp and GoalID.
	 * 
	 * @param goal
	 *            A goal message
	 * @param t
	 *            The timestamp of the action goal message
	 * @param goalID
	 *            The goal's GoalID
	 * @return A new action goal message object
	 */
	public T_ACTION_GOAL createActionGoalMessage(T_GOAL goal, Time t, GoalID goalID) {

		T_ACTION_GOAL a = null;
		try {
			a = clsActionGoal.newInstance();

			Header header = new Header();
			header.stamp = t;

			Field f = clsActionGoal.getField("goal");
			f.set(a, goal);
			f = clsActionGoal.getField("goal_id");
			f.set(a, goalID);
			f = clsActionGoal.getField("header");
			f.set(a, header);

		} catch (Exception e) {
		}
		return a;

	}

	/**
	 * Creates an action result message.
	 * 
	 * @return A new action result message object
	 */
	public T_ACTION_RESULT createActionResultMessage() {

		T_ACTION_RESULT a = null;
		try {
			a = clsActionResult.newInstance();
		} catch (Exception e) {
		}
		return a;

	}
	
	/**
	 * Creates an action result message with the given result
	 * message, timestamp and GoalStatus.
	 * 
	 * @param result
	 *            A result message
	 * @param t
	 *            The timestamp of the action result message
	 * @param gs
	 *            The GoalStatus
	 * @return A new action result message object
	 */
	public T_ACTION_RESULT createActionResultMessage(T_RESULT result, Time t, GoalStatus gs) {

		T_ACTION_RESULT a = null;
		try {
			a = clsActionResult.newInstance();
			
			Header header = new Header();
			header.stamp = t;

			Field f = clsActionResult.getField("result");
			f.set(a, result);
			f = clsActionResult.getField("status");
			f.set(a, gs);
			f = clsActionResult.getField("header");
			f.set(a, header);
			
		} catch (Exception e) {
		}
		return a;

	}

	/**
	 * Creates a feedback message.
	 * 
	 * @return A new feedback message object
	 */
	public T_FEEDBACK createFeedbackMessage() {

		T_FEEDBACK a = null;
		try {
			a = clsFeedback.newInstance();
		} catch (Exception e) {
		}
		return a;

	}

	/**
	 * Creates a goal message.
	 * 
	 * @return A new goal message object
	 */
	public T_GOAL createGoalMessage() {

		T_GOAL a = null;
		try {
			a = clsGoal.newInstance();
		} catch (Exception e) {
		}
		return a;

	}

	/**
	 * Creates a result message.
	 * 
	 * @return A new result message object
	 */
	public T_RESULT createResultMessage() {

		T_RESULT a = null;
		try {
			a = clsResult.newInstance();
		} catch (Exception e) {
		}
		return a;

	}
	
	/**
	 * Retrieves the feedback message from a given action feedback message.
	 * 
	 * @param actionFeedback
	 *            An action feedback message
	 * @return The contained feedback message
	 */
	public T_FEEDBACK getFeedbackFromActionFeedback(T_ACTION_FEEDBACK actionFeedback) {

		T_FEEDBACK feedback = null;
		try {
			Field f = clsActionFeedback.getField("feedback");
			feedback = clsFeedback.cast(f.get(actionFeedback));
		} catch (Exception e) {
			Ros.getInstance().logError("[ActionSpec] Couldn't find field 'feedback' in action feedback message.");
			e.printStackTrace();
		}
		return feedback;

	}

	/**
	 * Retrieves the goal message from a given action goal message.
	 * 
	 * @param actionGoal
	 *            An action goal message
	 * @return The contained goal message
	 */
	public T_GOAL getGoalFromActionGoal(T_ACTION_GOAL actionGoal) {

		T_GOAL goal = null;
		try {
			Field f = clsActionGoal.getField("goal");
			goal = clsGoal.cast(f.get(actionGoal));
		} catch (Exception e) {
			Ros.getInstance().logError("[ActionSpec] Couldn't find field 'goal' in action goal message.");
			e.printStackTrace();
		}
		return goal;

	}

	/**
	 * Retrieves the result message from a given action result message.
	 * 
	 * @param actionResult
	 *            An action result message
	 * @return The contained result message
	 */
	public T_RESULT getResultFromActionResult(T_ACTION_RESULT actionResult) {

		T_RESULT result = null;
		try {
			Field f = clsActionResult.getField("result");
			result = clsResult.cast(f.get(actionResult));
		} catch (Exception e) {
			Ros.getInstance().logError("[ActionSpec] Couldn't find field 'result' in action result message.");
			e.printStackTrace();
		}
		return result;

	}

	/**
	 * Retrieves the GoalID from a given action goal message.
	 * 
	 * @param actionGoal
	 *            An action goal message
	 * @return The contained GoalID
	 */
	public GoalID getGoalIDFromActionGoal(T_ACTION_GOAL actionGoal) {

		GoalID id = null;

		try {
			Field f = clsActionGoal.getField("goal_id");
			id = (GoalID) f.get(actionGoal);
		} catch (Exception e) {
			Ros.getInstance().logError("[ActionSpec] Couldn't find field 'goal_id' of type 'GoalID' in action goal message.");
			e.printStackTrace();
		}

		return id;

	}

	/**
	 * Retrieves the GoalStatus from a given action feedback message.
	 * 
	 * @param actionFeedback
	 *            An action feedback message
	 * @return The contained GoalStatus
	 */
	public GoalStatus getGoalStatusFromActionFeedback(T_ACTION_FEEDBACK actionFeedback) {

		GoalStatus status = null;

		try {
			Field f = clsActionFeedback.getField("status");
			status = (GoalStatus) f.get(actionFeedback);
		} catch (Exception e) {
			Ros.getInstance().logError("[ActionSpec] Couldn't find field 'status' of type 'GoalStatus' in action feedback message.");
			e.printStackTrace();
		}

		return status;

	}

	/**
	 * Retrieves the GoalStatus from a given action result message.
	 * 
	 * @param actionResult
	 *            An action result message
	 * @return The contained GoalStatus
	 */
	public GoalStatus getGoalStatusFromActionResult(T_ACTION_RESULT actionResult) {

		GoalStatus status = null;

		try {
			Field f = clsActionResult.getField("status");
			status = (GoalStatus) f.get(actionResult);
		} catch (Exception e) {
			Ros.getInstance().logError("[ActionSpec] Couldn't find field 'status' of type 'GoalStatus' in action result message.");
			e.printStackTrace();
		}

		return status;

	}

}
