package ros.actionlib.example;

import ros.NodeHandle;
import ros.RosException;
import ros.actionlib.client.SimpleActionClient;
import ros.pkg.actionlib_tutorials.msg.FibonacciActionFeedback;
import ros.pkg.actionlib_tutorials.msg.FibonacciActionGoal;
import ros.pkg.actionlib_tutorials.msg.FibonacciActionResult;
import ros.pkg.actionlib_tutorials.msg.FibonacciFeedback;
import ros.pkg.actionlib_tutorials.msg.FibonacciGoal;
import ros.pkg.actionlib_tutorials.msg.FibonacciResult;

/**
 * The FibonacciSimpleActionClient is a specialized SimpleActionClient that
 * is intended to work with an action server offering services related to
 * the Fibonacci action. The FibonacciSimpleActionClient completely hides 
 * the Generics approach of the SimpleActionClient's implementation.
 * 
 * @author Alexander C. Perzylo, perzylo@cs.tum.edu
 * 
 * @see SimpleActionClient
 */
public class FibonacciSimpleActionClient extends SimpleActionClient<FibonacciActionFeedback, FibonacciActionGoal, FibonacciActionResult, FibonacciFeedback, FibonacciGoal, FibonacciResult> {

	public FibonacciSimpleActionClient(String nameSpace,
			FibonacciActionSpec spec, boolean useSpinThread)
	throws RosException {

		super(nameSpace, spec, useSpinThread);

	}

	public FibonacciSimpleActionClient(NodeHandle parentNode,
			String nameSpace, FibonacciActionSpec spec,
			boolean useSpinThread) throws RosException {

		super(parentNode, nameSpace, spec, useSpinThread);

	}

}
