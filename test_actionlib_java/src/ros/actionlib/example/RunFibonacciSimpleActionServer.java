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

		// the action specification for the Fibonacci action
		FibonacciActionSpec spec = new FibonacciActionSpec();
		
		// build simple action server for the Fibonacci action 
		FibonacciSimpleActionServer sas; 
		sas = spec.buildSimpleActionServer("fibonacci", impl, true, false);

		// start server
		ros.logInfo("[Test] Starting action server");
		sas.start();
		
		// service callbacks
		ros.spin();
		
		// shutdown server on exit 
		sas.shutdown();
		ros.logInfo("[Test] Action server was shutdown");
		
	}
	
}
