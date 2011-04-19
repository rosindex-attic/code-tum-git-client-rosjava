package ros.actionlib.spec;

import ros.NodeHandle;
import ros.RosException;
import ros.actionlib.ActionSpec;
import ros.actionlib.client.ActionClient;
import ros.actionlib.client.SimpleActionClient;
import ros.pkg.actionlib_tutorials.msg.FibonacciAction;
import ros.pkg.actionlib_tutorials.msg.FibonacciActionFeedback;
import ros.pkg.actionlib_tutorials.msg.FibonacciActionGoal;
import ros.pkg.actionlib_tutorials.msg.FibonacciActionResult;
import ros.pkg.actionlib_tutorials.msg.FibonacciFeedback;
import ros.pkg.actionlib_tutorials.msg.FibonacciGoal;
import ros.pkg.actionlib_tutorials.msg.FibonacciResult;

/**
 * The FibonacciActionSpec class represents the action specification for the
 * Fibonacci action. It completely hides the Generics approach of the 
 * Actionlib implementation. 
 * 
 * @author Alexander C. Perzylo, perzylo@cs.tum.edu
 * 
 * @see ActionSpec
 */
public class FibonacciActionSpec extends ActionSpec<FibonacciAction, FibonacciActionFeedback, FibonacciActionGoal, FibonacciActionResult, FibonacciFeedback, FibonacciGoal, FibonacciResult> {

	/**
	 * Constructor to create an action specification for the Fibonacci action.
	 */
	public FibonacciActionSpec() {
		super(FibonacciAction.class);
	}

	@Override
	public FibonacciActionClient buildActionClient(String nameSpace) {

		FibonacciActionClient ac = null;
		try {
			ac = new FibonacciActionClient(nameSpace, this);
		} catch (RosException e) {
			e.printStackTrace();
		}
		return ac;

	}

	@Override
	public FibonacciActionClient buildActionClient(NodeHandle nodeHandle, String nameSpace) {

		FibonacciActionClient ac = null;
		try {
			ac = new FibonacciActionClient(nodeHandle, nameSpace, this);
		} catch (RosException e) {
			e.printStackTrace();
		}
		return ac;

	}

	@Override
	public FibonacciSimpleActionClient buildSimpleActionClient(String nameSpace) {

		FibonacciSimpleActionClient sac = null;
		try {
			sac = new FibonacciSimpleActionClient(nameSpace, this, true);
		} catch (RosException e) {
			e.printStackTrace();
		}
		return sac;

	}

	@Override
	public FibonacciSimpleActionClient buildSimpleActionClient(NodeHandle nodeHandle, String nameSpace) {

		FibonacciSimpleActionClient sac = null;
		try {
			sac = new FibonacciSimpleActionClient(nodeHandle, nameSpace, this, true);
		} catch (RosException e) {
			e.printStackTrace();
		}
		return sac;

	}

	/**
	 * The FibonacciActionClient is a specialized ActionClient that is intended
	 * to work with an action server offering services related to the Fibonacci
	 * action. The FibonacciActionClient completely hides the Generics approach
	 * of the ActionClient implementation.
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

	/**
	 * The FibonacciSimpleActionClient is a specialized SimpleActionClient that
	 * is intended to work with an action server offering services related to
	 * the Fibonacci action. The FibonacciSimpleActionClient completely hides 
	 * the Generics approach of the SimpleActionClient implementation.
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

}
