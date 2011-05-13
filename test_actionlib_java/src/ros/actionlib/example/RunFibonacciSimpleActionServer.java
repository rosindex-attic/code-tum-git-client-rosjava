package ros.actionlib.example;

import ros.Ros;

public class RunFibonacciSimpleActionServer {

	public static void main(String[] args) {

		run();

	}
	
	public static void run() {

		Ros ros = Ros.getInstance();
		ros.init("test_fibonacci_server_java");
		
		// user code implementing the SimpleActionServerCallbacks interface
		FibonacciUserImpl impl = new FibonacciUserImpl();

		FibonacciActionSpec spec = new FibonacciActionSpec();
		FibonacciSimpleActionServer sas; 
		sas = spec.buildSimpleActionServer("fibonacci", impl, true, false);
		sas.start();
		Ros.getInstance().spin();
		
	}
	
}
