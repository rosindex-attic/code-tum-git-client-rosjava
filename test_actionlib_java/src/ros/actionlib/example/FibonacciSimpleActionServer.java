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

	public FibonacciSimpleActionServer(String nameSpace,
			ActionSpec<?, FibonacciActionFeedback, FibonacciActionGoal, FibonacciActionResult, FibonacciFeedback, FibonacciGoal, FibonacciResult> spec,
			SimpleActionServerCallbacks<FibonacciActionFeedback, FibonacciActionGoal, FibonacciActionResult, FibonacciFeedback, FibonacciGoal, FibonacciResult> callbacks,
			boolean useBlockingGoalCallback, boolean autoStart) {
		
		super(nameSpace, spec, callbacks, useBlockingGoalCallback, autoStart);

	}
	
	public FibonacciSimpleActionServer(NodeHandle parent, String nameSpace,
			ActionSpec<?, FibonacciActionFeedback, FibonacciActionGoal, FibonacciActionResult, FibonacciFeedback, FibonacciGoal, FibonacciResult> spec,
			SimpleActionServerCallbacks<FibonacciActionFeedback, FibonacciActionGoal, FibonacciActionResult, FibonacciFeedback, FibonacciGoal, FibonacciResult> callbacks,
			boolean useBlockingGoalCallback, boolean autoStart) {
		
		super(parent, nameSpace, spec, callbacks, useBlockingGoalCallback, autoStart);

	}

}
