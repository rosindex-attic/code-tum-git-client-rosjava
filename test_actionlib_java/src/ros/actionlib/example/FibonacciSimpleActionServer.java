package ros.actionlib.example;

import ros.NodeHandle;
import ros.actionlib.ActionSpec;
import ros.actionlib.server.SimpleActionServer;
import ros.actionlib.server.SimpleActionServerCallbacks;
import ros.pkg.actionlib_tutorials.msg.FibonacciActionFeedback;
import ros.pkg.actionlib_tutorials.msg.FibonacciActionGoal;
import ros.pkg.actionlib_tutorials.msg.FibonacciActionResult;
import ros.pkg.actionlib_tutorials.msg.FibonacciFeedback;
import ros.pkg.actionlib_tutorials.msg.FibonacciGoal;
import ros.pkg.actionlib_tutorials.msg.FibonacciResult;

/**
 * The FibonacciSimpleActionServer is a specialized SimpleActionServer
 * that offers services related to the Fibonacci action. The 
 * FibonacciSimpleActionServer completely hides the Generics approach 
 * of the SimpleActionServer's implementation.
 * 
 * @author Alexander C. Perzylo, perzylo@cs.tum.edu
 * 
 * @see SimpleActionServer
 */
public class FibonacciSimpleActionServer extends SimpleActionServer<FibonacciActionFeedback, FibonacciActionGoal, FibonacciActionResult, FibonacciFeedback, FibonacciGoal, FibonacciResult> {

	/**
	 * Constructor used to create a simple action server for the Fibonacci
	 * action given a names space and the Fibonacci action's specification.
	 * 
	 * @param nameSpace
	 *            The name space to communicate within
	 * @param spec
	 *            The specification of the action
	 * @param callbacks
	 *            The callbacks that will be used by the action server to inform
	 *            the user about newly arrived goal and preempt requests
	 * @param useBlockingGoalCallback
	 *            If set to <tt>true</tt> a new thread gets created which
	 *            services the blockinGoalCallback method by conveniently
	 *            preempting goals and providing the next goal to the callback.
	 *            This also allows the user to have time consuming operations
	 *            inside of this callback method.<br>
	 *            If set to <tt>false</tt>, the user will be responsible for
	 *            goal management and only gets informed on the arrival of new
	 *            goal and preempt requests through calls to the goalCallback
	 *            and preemptCallback methods. This methods have to return
	 *            immediately and thus may only contain non-blocking code.
	 * @param autoStart
	 *            A flag, indicating whether the action server shall be
	 *            automatically started after instantiation or not. An action
	 *            server can be manually started using the {@link #start()}
	 *            method.
	 */
	public FibonacciSimpleActionServer(String nameSpace,
			ActionSpec<?,
					FibonacciActionFeedback,
					FibonacciActionGoal,
					FibonacciActionResult,
					FibonacciFeedback,
					FibonacciGoal,
					FibonacciResult> spec,
			SimpleActionServerCallbacks<FibonacciActionFeedback,
					FibonacciActionGoal,
					FibonacciActionResult,
					FibonacciFeedback,
					FibonacciGoal,
					FibonacciResult> callbacks,
			boolean useBlockingGoalCallback,
			boolean autoStart) {
		
		super(nameSpace, spec, callbacks, useBlockingGoalCallback, autoStart);

	}
	
	/**
	 * Constructor used to create a simple action server for the Fibonacci
	 * action given a parent node handle, names space and the Fibonacci action's
	 * specification.
	 * 
	 * @param parent
	 *            The parent node of this simple action server
	 * @param nameSpace
	 *            The name space to communicate within
	 * @param spec
	 *            The specification of the action
	 * @param callbacks
	 *            The callbacks that will be used by the action server to inform
	 *            the user about newly arrived goal and preempt requests
	 * @param useBlockingGoalCallback
	 *            If set to <tt>true</tt> a new thread gets created which
	 *            services the blockinGoalCallback method by conveniently
	 *            preempting goals and providing the next goal to the callback.
	 *            This also allows the user to have time consuming operations
	 *            inside of this callback method.<br>
	 *            If set to <tt>false</tt>, the user will be responsible for
	 *            goal management and only gets informed on the arrival of new
	 *            goal and preempt requests through calls to the goalCallback
	 *            and preemptCallback methods. This methods have to return
	 *            immediately and thus may only contain non-blocking code.
	 * @param autoStart
	 *            A flag, indicating whether the action server shall be
	 *            automatically started after instantiation or not. An action
	 *            server can be manually started using the {@link #start()}
	 *            method.
	 */
	public FibonacciSimpleActionServer(NodeHandle parent,
			String nameSpace,
			ActionSpec<?,
					FibonacciActionFeedback,
					FibonacciActionGoal,
					FibonacciActionResult,
					FibonacciFeedback,
					FibonacciGoal,
					FibonacciResult> spec,
			SimpleActionServerCallbacks<FibonacciActionFeedback,
					FibonacciActionGoal,
					FibonacciActionResult,
					FibonacciFeedback,
					FibonacciGoal,
					FibonacciResult> callbacks,
			boolean useBlockingGoalCallback,
			boolean autoStart) {
		
		super(parent, nameSpace, spec, callbacks, useBlockingGoalCallback, autoStart);

	}

}
