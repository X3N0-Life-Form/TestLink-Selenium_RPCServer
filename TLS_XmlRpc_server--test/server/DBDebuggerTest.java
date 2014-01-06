package server;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;

import org.junit.Test;

public class DBDebuggerTest {

	/**
	 * Debug's args are correctly formed.
	 */
	@Test
	public void testDebug_good() {
		DBDebugger dbd = new DBDebugger();
		HashMap<Object, Object> args = new HashMap<>();
		args.put("debug", "this is a debug value");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		System.setOut(ps);
		dbd.debug(args);
		String res = baos.toString();
		assertTrue(res.startsWith("-->Received debug request:"));
		assertTrue(res.contains("this is a debug value"));
	}
	
	/**
	 * Debug's args are correctly formed.
	 */
	@Test
	public void testDebug_bad() {
		DBDebugger dbd = new DBDebugger();
		HashMap<Object, Object> args = new HashMap<>();
		args.put("debug_bad", "this is a debug value");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		System.setOut(ps);
		dbd.debug(args);
		String res = baos.toString();
		assertTrue(res.startsWith("-->Received debug request:"));
		assertTrue(res.contains("no 'debug' key in incoming args."));
	}
}
