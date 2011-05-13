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

	public FibonacciActionServer(String nameSpace,
			ActionSpec<?, FibonacciActionFeedback, FibonacciActionGoal, FibonacciActionResult, FibonacciFeedback, FibonacciGoal, FibonacciResult> spec,
			ActionServerCallbacks<FibonacciActionFeedback, FibonacciActionGoal, FibonacciActionResult, FibonacciFeedback, FibonacciGoal, FibonacciResult> callbacks,
			boolean autoStart) {

		super(nameSpace, spec, callbacks, autoStart);

	}
	
	public FibonacciActionServer(NodeHandle parent, String nameSpace,
			ActionSpec<?, FibonacciActionFeedback, FibonacciActionGoal, FibonacciActionResult, FibonacciFeedback, FibonacciGoal, FibonacciResult> spec,
			ActionServerCallbacks<FibonacciActionFeedback, FibonacciActionGoal, FibonacciActionResult, FibonacciFeedback, FibonacciGoal, FibonacciResult> callbacks,
			boolean autoStart) {

		super(parent, nameSpace, spec, callbacks, autoStart);

	}

}
