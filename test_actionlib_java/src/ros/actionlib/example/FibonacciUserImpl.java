package ros.actionlib.example;

import ros.actionlib.server.SimpleActionServer;
import ros.actionlib.server.SimpleActionServerCallbacks;
import ros.pkg.actionlib_tutorials.msg.FibonacciActionFeedback;
import ros.pkg.actionlib_tutorials.msg.FibonacciActionGoal;
import ros.pkg.actionlib_tutorials.msg.FibonacciActionResult;
import ros.pkg.actionlib_tutorials.msg.FibonacciFeedback;
import ros.pkg.actionlib_tutorials.msg.FibonacciGoal;
import ros.pkg.actionlib_tutorials.msg.FibonacciResult;

public class FibonacciUserImpl implements SimpleActionServerCallbacks<FibonacciActionFeedback, FibonacciActionGoal, FibonacciActionResult, FibonacciFeedback, FibonacciGoal, FibonacciResult>{

	@Override
	public void blockingGoalCallback(FibonacciGoal goal, SimpleActionServer<FibonacciActionFeedback,FibonacciActionGoal,FibonacciActionResult,FibonacciFeedback,FibonacciGoal,FibonacciResult> actionServer) {

		System.out.println("BLOCKING GOAL CALLBACK");
		
		int order = (goal.order > 0) ? goal.order:0;
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
		
		for (int i=2;i<order;i++) {
			seq[i] = seq[i-1]+seq[i-2];
			publishFeedback(seq, actionServer);
			snore();
		}			
				
		FibonacciResult result =  new FibonacciResult();
		result.sequence = seq;
		actionServer.setSucceeded(result, "");
		
	}

	@Override
	public void goalCallback(SimpleActionServer<FibonacciActionFeedback,FibonacciActionGoal,FibonacciActionResult,FibonacciFeedback,FibonacciGoal,FibonacciResult> actionServer) {
		System.out.println("GOAL CALLBACK");
	}

	@Override
	public void preemptCallback(SimpleActionServer<FibonacciActionFeedback,FibonacciActionGoal,FibonacciActionResult,FibonacciFeedback,FibonacciGoal,FibonacciResult> actionServer) {
		System.out.println("PREEMPT CALLBACK");
	}

	private void snore() {
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		
	}
	
	private void publishFeedback(int[] seq, SimpleActionServer<FibonacciActionFeedback,FibonacciActionGoal,FibonacciActionResult,FibonacciFeedback,FibonacciGoal,FibonacciResult> actionServer) {
		
		FibonacciFeedback feedback = new FibonacciFeedback();
		feedback.sequence = seq;
		actionServer.publishFeedback(feedback);
		
		System.out.print("FEEDBACK:");
		for (int i=0; i< feedback.sequence.length; i++) {
			if (feedback.sequence[i] == 0 && i != 0) {
				break;
			}
			System.out.print(" "+feedback.sequence[i]);
		}
		System.out.println();
		
	}
	
}
