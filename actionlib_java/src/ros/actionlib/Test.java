package ros.actionlib;

import ros.Ros;
import ros.actionlib.client.SimpleActionClient;
import ros.actionlib.example.FibonacciActionSpec;
import ros.actionlib.example.FibonacciSimpleActionClient;
import ros.actionlib.state.SimpleClientGoalState;
import ros.communication.Duration;
import ros.pkg.actionlib_tutorials.msg.FibonacciAction;
import ros.pkg.actionlib_tutorials.msg.FibonacciActionFeedback;
import ros.pkg.actionlib_tutorials.msg.FibonacciActionGoal;
import ros.pkg.actionlib_tutorials.msg.FibonacciActionResult;
import ros.pkg.actionlib_tutorials.msg.FibonacciFeedback;
import ros.pkg.actionlib_tutorials.msg.FibonacciGoal;
import ros.pkg.actionlib_tutorials.msg.FibonacciResult;

/**
 * 
 * A class used for testing the actionlib implementation.
 * 
 * @author Alexander C. Perzylo, perzylo@cs.tum.edu
 * 
 */
public class Test {

	/**
	 * Runs simple tests for action clients and action servers.
	 * 
	 * @param args
	 *            Command line arguments (get ignored)
	 */
	public static void main(String[] args) {

		testSimpleActionClientHidingGenerics(4);
		//testSimpleActionClient(4);

	}

	public static void testSimpleActionClient(int order) {

		Ros ros = Ros.getInstance();
		ros.init("test_fibonacci");

		ActionSpec<FibonacciAction,
			FibonacciActionFeedback,
			FibonacciActionGoal,
			FibonacciActionResult,
			FibonacciFeedback,
			FibonacciGoal,
			FibonacciResult> spec;

		spec = new ActionSpec<FibonacciAction,
			FibonacciActionFeedback,
			FibonacciActionGoal,
			FibonacciActionResult,
			FibonacciFeedback,
			FibonacciGoal,
			FibonacciResult> (FibonacciAction.class);

		SimpleActionClient<FibonacciActionFeedback,
			FibonacciActionGoal, 
			FibonacciActionResult, 
			FibonacciFeedback, 
			FibonacciGoal, 
			FibonacciResult> sac = spec.buildSimpleActionClient("fibonacci");

		ros.logInfo("[Test] Waiting for action server to start");
		// wait for the action server to start
		sac.waitForServer(); // will wait for infinite time

		ros.logInfo("[Test] Action server started, sending goal");
		// send a goal to the action
		FibonacciGoal goal = spec.createGoalMessage();
		goal.order = order;
		sac.sendGoal(goal);

		// wait for the action to return
		ros.logInfo("[Test] Waiting for result.");
		boolean finished_before_timeout = sac.waitForResult(new Duration(100.0));

		if (finished_before_timeout) {
			SimpleClientGoalState state = sac.getState();
			ros.logInfo("[Test] Action finished: " + state.toString());

			FibonacciResult res = sac.getResult();
			System.out.print("[Test] Fibonacci sequence (" + goal.order + "):");
			for (int i : res.sequence) {
				System.out.print(" " + i);
			}
			System.out.println();
		} else {
			ros.logInfo("[Test] Action did not finish before the time out");
		}

		sac.stopSpinThread();
		ros.logInfo("[Test] Spin thread stopped");

	}
	
	public static void testSimpleActionClientHidingGenerics(int order) {
		
		Ros ros = Ros.getInstance();
		ros.init("test_fibonacci");

		// create action specification for the Fibonacci action without
		// having to write a lot of data types as class parameters for 
		// the ActionSpec/ActionClient classes based on Generics
		FibonacciActionSpec spec = new FibonacciActionSpec();
		FibonacciSimpleActionClient sac = spec.buildSimpleActionClient("fibonacci");

		ros.logInfo("[Test] Waiting for action server to start");
		// wait for the action server to start
		sac.waitForServer(); // will wait for infinite time

		ros.logInfo("[Test] Action server started, sending goal");
		// send a goal to the action
		FibonacciGoal goal = spec.createGoalMessage();
		goal.order = order;
		sac.sendGoal(goal);

		// wait for the action to return
		ros.logInfo("[Test] Waiting for result.");
		boolean finished_before_timeout = sac.waitForResult(new Duration(100.0));

		if (finished_before_timeout) {
			SimpleClientGoalState state = sac.getState();
			ros.logInfo("[Test] Action finished: " + state.toString());

			FibonacciResult res = sac.getResult();
			System.out.print("[Test] Fibonacci sequence (" + goal.order + "):");
			for (int i : res.sequence) {
				System.out.print(" " + i);
			}
			System.out.println();
		} else {
			ros.logInfo("[Test] Action did not finish before the time out");
		}

		sac.stopSpinThread();
		ros.logInfo("[Test] Spin thread stopped");
		
	}
	
}
