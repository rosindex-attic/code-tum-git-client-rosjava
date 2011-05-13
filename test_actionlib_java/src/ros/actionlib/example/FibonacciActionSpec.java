package ros.actionlib.example;

import ros.NodeHandle;
import ros.RosException;
import ros.actionlib.ActionSpec;
import ros.actionlib.server.ActionServerCallbacks;
import ros.actionlib.server.SimpleActionServerCallbacks;
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

	@Override
	public FibonacciActionServer buildActionServer(String nameSpace, 
			ActionServerCallbacks<FibonacciActionFeedback, FibonacciActionGoal, FibonacciActionResult, FibonacciFeedback, FibonacciGoal, FibonacciResult> callbacks,
			boolean autoStart) {

		return new FibonacciActionServer(nameSpace, this, callbacks, autoStart);

	}
	
	@Override
	public FibonacciActionServer buildActionServer(NodeHandle nodeHandle, String nameSpace, 
			ActionServerCallbacks<FibonacciActionFeedback, FibonacciActionGoal, FibonacciActionResult, FibonacciFeedback, FibonacciGoal, FibonacciResult> callbacks,
			boolean autoStart) {

		return new FibonacciActionServer(nodeHandle, nameSpace, this, callbacks, autoStart);

	}

	@Override
	public FibonacciSimpleActionServer buildSimpleActionServer(String nameSpace, 
			SimpleActionServerCallbacks<FibonacciActionFeedback, FibonacciActionGoal, FibonacciActionResult, FibonacciFeedback, FibonacciGoal, FibonacciResult> callbacks,
			boolean useBlockingGoalCallback, boolean autoStart) {

		return new FibonacciSimpleActionServer(nameSpace, this, callbacks, useBlockingGoalCallback, autoStart);

	}

	@Override
	public FibonacciSimpleActionServer buildSimpleActionServer(NodeHandle nodeHandle, String nameSpace, 
			SimpleActionServerCallbacks<FibonacciActionFeedback, FibonacciActionGoal, FibonacciActionResult, FibonacciFeedback, FibonacciGoal, FibonacciResult> callbacks,
			boolean useBlockingGoalCallback, boolean autoStart) {

		return new FibonacciSimpleActionServer(nodeHandle, nameSpace, this, callbacks, useBlockingGoalCallback, autoStart);

	}
	
}
