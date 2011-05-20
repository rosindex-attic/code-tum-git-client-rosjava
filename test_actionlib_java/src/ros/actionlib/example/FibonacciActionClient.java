package ros.actionlib.example;

import ros.NodeHandle;
import ros.RosException;
import ros.actionlib.ActionSpec;
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

	/**
	 * Constructor used to create an action client for the Fibonacci action
	 * given a names space and the Fibonacci action's specification.
	 * 
	 * @param nameSpace
	 *            The name space to communicate within (specified by the action
	 *            server)
	 * @param spec
	 *            The specification of the action
	 * @throws RosException
	 *             If setting up the needed subscribers and publishers fail
	 */
	public FibonacciActionClient(String nameSpace, 
			ActionSpec<?,
					FibonacciActionFeedback,
					FibonacciActionGoal,
					FibonacciActionResult,
					FibonacciFeedback,
					FibonacciGoal,
					FibonacciResult> spec) throws RosException {

		super(nameSpace, spec);

	}

	/**
	 * Constructor used to create an action client for the Fibonacci action
	 * given a parent node handle, names space and the Fibonacci action's
	 * specification.
	 * 
	 * @param parent
	 *            The parent node of this action client
	 * @param nameSpace
	 *            The name space to communicate within (specified by the action
	 *            server)
	 * @param spec
	 *            The specification of the action
	 * @throws RosException
	 *             If setting up the needed subscribers and publishers fail
	 */
	public FibonacciActionClient(NodeHandle parent, String nameSpace,
			ActionSpec<?,
					FibonacciActionFeedback,
					FibonacciActionGoal,
					FibonacciActionResult,
					FibonacciFeedback,
					FibonacciGoal,
					FibonacciResult> spec) throws RosException {

		super(parent, nameSpace, spec);

	}

}