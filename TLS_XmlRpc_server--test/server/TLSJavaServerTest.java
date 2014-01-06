package server;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;

/**
 * Tests the front-end part of the server.
 * @author Adrien Droguet
 *
 */
public class TLSJavaServerTest {

	/**
	 * By default, the server should be able to start.
	 */
	@Test
	public void testMain_default() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		System.setOut(ps);
		TLSJavaServer.main(null);
		assertTrue(baos.toString().contains("Server started."));
	}
	//TODO: more tests?
}
