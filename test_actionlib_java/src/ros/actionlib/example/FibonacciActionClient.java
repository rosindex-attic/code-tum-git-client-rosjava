package ros.actionlib.example;

import ros.NodeHandle;
import ros.RosException;
import ros.actionlib.client.ActionClient;
import ros.pkg.actionlib_tutorials.msg.FibonacciActionFeedback;
import ros.pkg.actionlib_tutorials.msg.FibonacciActionGoal;
import ros.pkg.actionlib_tutorials.msg.FibonacciActionResult;
import ros.pkg.actionlib_tutorials.msg.FibonacciFeedback;
import ros.pkg.actionlib_tutorials.msg.FibonacciGoal;
import ros.pkg.actionlib_tutorials.msg.FibonacciResult;

/**
 * The FibonacciActionClient is a specialized ActionClient that is intended
 * to work with an action server offering services related to the Fibonacci
 * action. The FibonacciActionClient completely hides the Generics approach
 * of the ActionClient's implementation.
 * 
 * @author Alexander C. Perzylo, perzylo@cs.tum.edu
 * 
 * @see ActionClient
 */
public class FibonacciActionClient extends ActionClient<FibonacciActionFeedback, FibonacciActionGoal, FibonacciActionResult, FibonacciFeedback, FibonacciGoal, FibonacciResult> {

	public FibonacciActionClient(String nameSpace, 
			FibonacciActionSpec spec) throws RosException {

		super(nameSpace, spec);

	}

	public FibonacciActionClient(NodeHandle parent, String nameSpace,
			FibonacciActionSpec spec) throws RosException {

		super(parent, nameSpace, spec);

	}

}