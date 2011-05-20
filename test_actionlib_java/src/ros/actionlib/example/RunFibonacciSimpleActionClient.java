package ros.actionlib.example;

import ros.Ros;
import ros.actionlib.ActionSpec;
import ros.actionlib.client.SimpleActionClient;
import ros.actionlib.state.SimpleClientGoalState;
import ros.communication.Duration;
import ros.pkg.actionlib_tutorials.msg.FibonacciAction;
import ros.pkg.actionlib_tutorials.msg.FibonacciActionFeedback;
import ros.pkg.actionlib_tutorials.msg.FibonacciActionGoal;
import ros.pkg.actionlib_tutorials.msg.FibonacciActionResult;
import ros.pkg.actionlib_tutorials.msg.FibonacciFeedback;
import ros.pkg.actionlib_tutorials.msg.FibonacciGoal;
import ros.pkg.actionlib_tutorials.msg.FibonacciResult;

public class RunFibonacciSimpleActionClient {

	public static void main(String[] args) {

		run();
		
	}
	
	public static void run() {
		
		int length = 20;
		
		Ros ros = Ros.getInstance();
		ros.init("test_fibonacci_client_java");

		// create action specification for the Fibonacci action without
		// having to write a lot of data types as class parameters for 
		// the ActionSpec/ActionClient classes based on Generics
		FibonacciActionSpec spec = new FibonacciActionSpec();
		
		// build simple action client for the Fibonacci action
		FibonacciSimpleActionClient sac = spec.buildSimpleActionClient("fibonacci");

		// wait for the action server to start (for infinite time)
		ros.logInfo("[Test] Waiting for action server to start");
		sac.waitForServer();

		// send a goal to the action server
		ros.logInfo("[Test] Action server started, sending goal");
		FibonacciGoal goal = spec.createGoalMessage();
		goal.order = length;
		sac.sendGoal(goal);

		// wait for the action server to return a result (up to 30s)
		ros.logInfo("[Test] Waiting for result.");
		boolean finished_before_timeout = sac.waitForResult(new Duration(30.0));

		if (finished_before_timeout) {
			// get state of action client
			SimpleClientGoalState state = sac.getState();
			ros.logInfo("[Test] Action finished: " + state);

			// get the result received from the action server
			FibonacciResult res = sac.getResult();
			System.out.print("[Test] Fibonacci sequence (" + goal.order + "):");
			for (int i : res.sequence) {
				System.out.print(" " + i);
			}
			System.out.println();
		} else {
			ros.logInfo("[Test] Action did not finish before the time out");
		}

		// shutdown action client on exit
		sac.shutdown();
		ros.logInfo("[Test] Action client was shutdown");
		
	}

	public static void run2() {

		int length = 20;
		
		Ros ros = Ros.getInstance();
		ros.init("test_fibonacci_client_java");

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

		// wait for the action server to start (for infinite time)
		ros.logInfo("[Test] Waiting for action server to start");
		sac.waitForServer();

		// send a goal to the action server
		ros.logInfo("[Test] Action server started, sending goal");
		FibonacciGoal goal = spec.createGoalMessage();
		goal.order = length;
		sac.sendGoal(goal);

		// wait for the action server to return a result (up to 30s)
		ros.logInfo("[Test] Waiting for result.");
		boolean finished_before_timeout = sac.waitForResult(new Duration(30.0));

		if (finished_before_timeout) {
			// get state of action client
			SimpleClientGoalState state = sac.getState();
			ros.logInfo("[Test] Action finished: " + state);

			// get the result received from the action server
			FibonacciResult res = sac.getResult();
			System.out.print("[Test] Fibonacci sequence (" + goal.order + "):");
			for (int i : res.sequence) {
				System.out.print(" " + i);
			}
			System.out.println();
		} else {
			ros.logInfo("[Test] Action did not finish before the time out");
		}

		// shutdown action client on exit
		sac.shutdown();
		ros.logInfo("[Test] Action client was shutdown");

	}
	
}
