package ros.actionlib.util;

import ros.Ros;
import ros.communication.Time;
import ros.pkg.actionlib_msgs.msg.GoalID;

/**
 * 
 * The GoalIDGenerator may be used to create unique GoalIDs.
 * 
 * @author Alexander C. Perzylo, perzylo@cs.tum.edu
 * 
 */
public class GoalIDGenerator {

	/**
	 * Monitor used to synchronize access to counter variable
	 */
	private static Object sync = new Object();

	/**
	 * Counter variable used to create unique ids
	 */
	private static long goalCount = 0L;

	/**
	 * Unique name to prepend to the goal id. This will generally be a fully 
	 * qualified node name.
	 */
	private String name;
	
	/**
	 * Constructor to create a GoalIDGenerator using a unique name to prepend
	 * to the goal id. This will generally be a fully qualified node name.
	 * 
	 * @param name An unique name
	 */
	public GoalIDGenerator(String name) {
		this.name = name;
	}
	
	/**
	 * Creates a GoalID object with an unique id and a timestamp of the current
	 * time.
	 * 
	 * @return GoalID object
	 */
	public GoalID generateID() {

		Time t = Ros.getInstance().now();
		GoalID id = new GoalID();

		StringBuffer sb = new StringBuffer(name);
		sb.append("-");
		synchronized (sync) {
			sb.append(++goalCount);
		}
		sb.append("-");
		sb.append(t.secs);
		sb.append(".");
		sb.append(t.nsecs);

		id.id = sb.toString();
		id.stamp = t;
		return id;

	}

}
