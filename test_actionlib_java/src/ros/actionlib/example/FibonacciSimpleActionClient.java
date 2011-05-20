package ros.actionlib.example;

import ros.NodeHandle;
import ros.RosException;
import ros.actionlib.ActionSpec;
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

	/**
	 * Constructor used to create a simple action client for the Fibonacci
	 * action given a names space and the Fibonacci action's specification.
	 * 
	 * @param nameSpace
	 *            The name space to communicate within (specified by the action
	 *            server)
	 * @param spec
	 *            The specification of the action
	 * @param useSpinThread
	 *            A flag, indicating whether a thread for servicing the callbacks
	 *            shall be automatically started with the client or not
	 * @throws RosException
	 *             If setting up the needed subscribers and publishers fail
	 */
	public FibonacciSimpleActionClient(String nameSpace,
			ActionSpec<?,
					FibonacciActionFeedback,
					FibonacciActionGoal,
					FibonacciActionResult,
					FibonacciFeedback,
					FibonacciGoal,
					FibonacciResult> spec,
			boolean useSpinThread) throws RosException {

		super(nameSpace, spec, useSpinThread);

	}

	/**
	 * Constructor used to create as simple action client for the Fibonacci
	 * action given a parent node handle, names space and the Fibonacci action's
	 * specification.
	 * 
	 * @param parent
	 *            The parent node of this simple action client
	 * @param nameSpace
	 *            The name space to communicate within (specified by the action
	 *            server)
	 * @param spec
	 *            The specification of the action
	 * @param useSpinThread
	 *            A flag, indicating whether a thread for servicing the callbacks
	 *            shall be automatically started with the client or not
	 * @throws RosException
	 *             If setting up the needed subscribers and publishers fail
	 */
	public FibonacciSimpleActionClient(NodeHandle parentNode,
			String nameSpace,
			ActionSpec<?,
					FibonacciActionFeedback,
					FibonacciActionGoal,
					FibonacciActionResult,
					FibonacciFeedback,
					FibonacciGoal,
					FibonacciResult> spec,
			boolean useSpinThread) throws RosException {

		super(parentNode, nameSpace, spec, useSpinThread);

	}

}
