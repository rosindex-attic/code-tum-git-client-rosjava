package ros.actionlib.example;

import ros.actionlib.server.SimpleActionServer;
import ros.actionlib.server.SimpleActionServerCallbacks;
import ros.pkg.actionlib_tutorials.msg.FibonacciActionFeedback;
import ros.pkg.actionlib_tutorials.msg.FibonacciActionGoal;
import ros.pkg.actionlib_tutorials.msg.FibonacciActionResult;
import ros.pkg.actionlib_tutorials.msg.FibonacciFeedback;
import ros.pkg.actionlib_tutorials.msg.FibonacciGoal;
import ros.pkg.actionlib_tutorials.msg.FibonacciResult;

/**
 * The FibonacciUserImpl class implements the SimpleActionServerCallbacks
 * interface and is responsible for calculating the result for a given
 * goal. For this example only the blockingGoalCallback is implemented.
 * In order to learn about the meaning of the different callback methods 
 * take a look at the SimpleActionServerCallbacks interface.    
 * 
 * @author Alexander C. Perzylo, perzylo@cs.tum.edu
 * 
 * @see SimpleActionServerCallbacks
 *
 */
public class FibonacciUserImpl implements SimpleActionServerCallbacks<FibonacciActionFeedback, FibonacciActionGoal, FibonacciActionResult, FibonacciFeedback, FibonacciGoal, FibonacciResult> {

	@Override
	public void blockingGoalCallback(
			FibonacciGoal goal,
			SimpleActionServer<FibonacciActionFeedback,
				FibonacciActionGoal,
				FibonacciActionResult,
				FibonacciFeedback,
				FibonacciGoal,
				FibonacciResult> actionServer) {

		int order = (goal.order > 0) ? goal.order : 0;
		int[] seq = new int[order];

		if (order > 0) {
			seq[0] = 0;
			publishFeedback(seq, actionServer);
			snore();
		}
		if (order > 1) {
			seq[1] = 1;
			publishFeedback(seq, actionServer);
			snore();
		}

		for (int i = 2; i < order; i++) {
			seq[i] = seq[i - 1] + seq[i - 2];
			publishFeedback(seq, actionServer);
			snore();
		}

		FibonacciResult result = new FibonacciResult();
		result.sequence = seq;
		actionServer.setSucceeded(result, "");

	}

	@Override
	public void goalCallback(
			SimpleActionServer<FibonacciActionFeedback,
				FibonacciActionGoal,
				FibonacciActionResult,
				FibonacciFeedback,
				FibonacciGoal,
				FibonacciResult> actionServer) {

	}

	@Override
	public void preemptCallback(
			SimpleActionServer<FibonacciActionFeedback,
				FibonacciActionGoal,
				FibonacciActionResult,
				FibonacciFeedback,
				FibonacciGoal,
				FibonacciResult> actionServer) {

	}

	/**
	 * Sleeps for a second.
	 */
	private void snore() {

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

	}

	/**
	 * Publishes the currently available sequence of Fibonacci numbers as
	 * Feedback message.
	 * 
	 * @param seq
	 *            The sequence
	 * @param actionServer
	 *            The action server that shall be used to publish
	 */
	private void publishFeedback(
			int[] seq,
			SimpleActionServer<FibonacciActionFeedback,
				FibonacciActionGoal,
				FibonacciActionResult,
				FibonacciFeedback,
				FibonacciGoal,
				FibonacciResult> actionServer) {

		FibonacciFeedback feedback = new FibonacciFeedback();
		feedback.sequence = seq;
		actionServer.publishFeedback(feedback);

	}

}
