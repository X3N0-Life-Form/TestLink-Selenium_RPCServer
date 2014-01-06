package utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;

import server.ExecutorSetup;
import server.ServerSetup;

/**
 * Specifically tests the dealWithArgs method.
 * @author Adrien Droguet
 *
 */
public class ServerUtilsTestDealWithArgs {
	
	@Before
	public void setup() {
		ExecutorSetup.reset();
		ServerSetup.reset();
	}
	
	/**
	 * Args is null. Nothing happens. There is no message displayed since it is
	 * not something the user should be able to do.
	 * @throws IOException 
	 */
	@Test
	public void testDealWithaArgs_null() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		System.setOut(ps);
		ServerUtils.dealWithArgs(null);
		assertTrue(baos.toString().startsWith("No argument was provided. " +
				"Using default options"));
		assertTrue(ServerSetup.getServerPort() == 8081);
	}
	
	/**
	 * Args is "empty". Again, nothing happens.
	 * @throws IOException
	 */
	@Test
	public void testDealWithArgs_nothing() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		System.setOut(ps);
		String[] nothing = {};
		ServerUtils.dealWithArgs(nothing);
		assertTrue(baos.toString().startsWith("No argument was provided. " +
				 "Using default options"));
		assertTrue(ServerSetup.getServerPort() == 8081);
	}
	
	/**
	 * Provide only a port number.
	 * @throws IOException 
	 */
	@Test
	public void testDealWithaArgs_portNumberOnly() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		System.setOut(ps);
		String[] args = {"8088"};
		ServerUtils.dealWithArgs(args);
		assertTrue(ServerSetup.getServerPort() == 8088);
	}
	
	/**
	 * A not-an-integer is given as port.
	 * @throws IOException
	 */
	@Test
	public void testDealWithaArgs_portNumberInvalid() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		System.setOut(ps);
		String[] args = {"hjvo"};
		ServerUtils.dealWithArgs(args);
		assertTrue(baos.toString().contains("hjvo is an invalid port number." +
				" Defaulting to 8081."));
		assertTrue(ServerSetup.getServerPort() == 8081);
	}
	
	/**
	 * "-md OK" is a valid option. Main directory url must be set to OK, and a
	 * message specifying
	 * @throws IOException 
	 */
	@Test
	public void testDealWithaArgs_md_OK() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		System.setOut(ps);
		String[] args = {"-md", "OK"};
		ServerUtils.dealWithArgs(args);
		assertTrue(baos.toString().contains("No server port was given. " +
				"Defaulting to 8081."));
		assertTrue(ServerSetup.getServerPort() == 8081);
		assertEquals("OK/", ExecutorSetup.getURLMainTestFolder());
	}
	
	/**
	 * The "-md" option is selected, but no directory is specified afterwards.
	 * @throws IOException
	 */
	@Test
	public void testDealWithArgs_md_nothing() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		System.setOut(ps);
		String[] args = {"-md"};
		ServerUtils.dealWithArgs(args);
		assertTrue(baos.toString().contains("The specified URL for the main" +
				" directory is invalid. Defaulting to current directory."));
	}
	
	/**
	 * "-log" thingy is set.
	 * @throws IOException
	 */
	@Test
	public void testDealWithArgs_log() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		System.setOut(ps);
		String[] args = {"-log"};
		ServerUtils.dealWithArgs(args);
		assertTrue(ServerSetup.isRecord());
	}
	
	/**
	 * The specified file exists.
	 * @throws IOException
	 */
	@Test
	public void testDealWithArgs_cfg_OK() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		System.setOut(ps);
		String[] args = {"-cfg", "dat.cfg"};
		File cfg = new File("dat.cfg");
		cfg.createNewFile();
		ServerUtils.dealWithArgs(args);
		assertTrue(baos.toString().contains("Configuring server from " +
								"file: dat.cfg"));
		cfg.delete();
	}
	
	/**
	 * The specified file does not exist.
	 * @throws IOException 
	 */
	@Test
	public void testDealWithArgs_cfg_nonExistent() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		System.setOut(ps);
		String[] args = {"-cfg", "notDat.cfg"};
		ServerUtils.dealWithArgs(args);
		assertTrue(baos.toString().contains("The file notDat.cfg could not be " +
				"found. Aborting configuration from file."));
	}
	
	/**
	 * "-cfg" selected, but no file name is given.
	 * @throws IOException
	 */
	@Test
	public void testDealWithArgs_cfg_error_noNameGiven() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		System.setOut(ps);
		String[] args = {"-cfg"};
		ServerUtils.dealWithArgs(args);
		assertTrue(baos.toString().contains("No file name given."));
	}
	
	/**
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testDealWithArgs_cfg_nooverride_cmd() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		System.setOut(ps);
		String[] args = {"8070", "-cfg", "dat.cfg", "-nooverride"};
		File cfg = new File("dat.cfg");
		cfg.createNewFile();
		ServerUtils.dealWithArgs(args);
		assertTrue(baos.toString().contains("Setting server port to 8070"));
		assertTrue(baos.toString().contains("Command line arguments override " +
						"configuration file."));
		cfg.delete();
	}
	//TODO: find more "wrong option" cases.
	//check default values more often
}
