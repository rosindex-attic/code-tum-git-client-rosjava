package ros.actionlib.example;

import ros.NodeHandle;
import ros.actionlib.ActionSpec;
import ros.actionlib.server.ActionServer;
import ros.actionlib.server.ActionServerCallbacks;
import ros.pkg.actionlib_tutorials.msg.FibonacciActionFeedback;
import ros.pkg.actionlib_tutorials.msg.FibonacciActionGoal;
import ros.pkg.actionlib_tutorials.msg.FibonacciActionResult;
import ros.pkg.actionlib_tutorials.msg.FibonacciFeedback;
import ros.pkg.actionlib_tutorials.msg.FibonacciGoal;
import ros.pkg.actionlib_tutorials.msg.FibonacciResult;

/**
 * The FibonacciActionServer is a specialized ActionServer that offers
 * services related to the Fibonacci action. The FibonacciActionServer
 * completely hides the Generics approach of the ActionServer's 
 * implementation.
 * 
 * @author Alexander C. Perzylo, perzylo@cs.tum.edu
 * 
 * @see ActionServer
 */
public class FibonacciActionServer extends ActionServer<FibonacciActionFeedback, FibonacciActionGoal, FibonacciActionResult, FibonacciFeedback, FibonacciGoal, FibonacciResult> {

	/**
	 * Constructor used to create an action server for the Fibonacci action
	 * given a names space and the Fibonacci action's specification.
	 * 
	 * @param nameSpace
	 *            The name space to communicate within
	 * @param spec
	 *            The specification of the action
	 * @param callbacks
	 *            The callbacks that will be used by the action server to inform
	 *            the user about newly arrived goal and cancel messages
	 * @param autoStart
	 *            A flag, indicating whether the action server shall be
	 *            automatically started after instantiation or not. An action
	 *            server can be manually started using the {@link #start()}
	 *            method.
	 */
	public FibonacciActionServer(String nameSpace,
			ActionSpec<?,
					FibonacciActionFeedback,
					FibonacciActionGoal,
					FibonacciActionResult,
					FibonacciFeedback,
					FibonacciGoal,
					FibonacciResult> spec,
			ActionServerCallbacks<FibonacciActionFeedback,
					FibonacciActionGoal,
					FibonacciActionResult,
					FibonacciFeedback,
					FibonacciGoal,
					FibonacciResult> callbacks,
			boolean autoStart) {

		super(nameSpace, spec, callbacks, autoStart);

	}
	
	/**
	 * Constructor used to create an action server for the Fibonacci action
	 * given a parent node handle, names space and the Fibonacci action's
	 * specification.
	 * 
	 * @param parent
	 *            The parent node of this action server
	 * @param nameSpace
	 *            The name space to communicate within
	 * @param spec
	 *            The specification of the action
	 * @param callbacks
	 *            The callbacks that will be used by the action server to inform
	 *            the user about newly arrived goal and cancel messages
	 * @param autoStart
	 *            A flag, indicating whether the action server shall be
	 *            automatically started after instantiation or not. An action
	 *            server can be manually started using the {@link #start()}
	 *            method.
	 */
	public FibonacciActionServer(NodeHandle parent,
			String nameSpace,
			ActionSpec<?,
					FibonacciActionFeedback,
					FibonacciActionGoal,
					FibonacciActionResult,
					FibonacciFeedback,
					FibonacciGoal,
					FibonacciResult> spec,
			ActionServerCallbacks<FibonacciActionFeedback,
					FibonacciActionGoal,
					FibonacciActionResult,
					FibonacciFeedback,
					FibonacciGoal,
					FibonacciResult> callbacks,
			boolean autoStart) {

		super(parent, nameSpace, spec, callbacks, autoStart);

	}

}
