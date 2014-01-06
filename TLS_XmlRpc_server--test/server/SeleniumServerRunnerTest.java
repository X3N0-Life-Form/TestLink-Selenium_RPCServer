package server;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;

/**
 * Note: we are not testing getProcessStatus() as it is unused.
 * @author Adrien Droguet
 *
 */
public class SeleniumServerRunnerTest {

	/**
	 * OK = nothing is printed in the error stream.
	 */
	@Test
	public void testRun() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		System.setErr(ps);
		SeleniumServerRunner ssr = new SeleniumServerRunner();
		ssr.run();
		String res = baos.toString();
		assertTrue(res.equals(""));
	}

}
