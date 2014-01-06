package server;

import java.util.HashMap;

/**
 * This class holds debugging tools.
 * @author Adrien Droguet
 * @version 1.0	2/04/2012<br>
 * 				Class created.<br>
 * @see TLSJavaServer
 */
public class DBDebugger {
	
	/**
	 * Prints out what is in the HashMap.
	 * @param args
	 * @return The HashMap.
	 */
	public HashMap<Object, Object> debug(HashMap<Object, Object> args) {
		System.out.println("-->Received debug request:");
		if (args.containsKey("debug"))
			System.out.println(args.get("debug"));
		else
			System.out.println("no 'debug' key in incoming args.");
		return args;
	}
}
