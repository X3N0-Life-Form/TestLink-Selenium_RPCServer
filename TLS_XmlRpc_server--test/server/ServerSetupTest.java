package server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.webserver.WebServer;
import org.junit.Ignore;
import org.junit.Test;

import exceptions.CfgException;

import utils.CfgOptions;
import utils.MyFileUtils;
import utils.ServerUtilsTest;

/**
 * 
 * @author Adrien Droguet
 * @see ServerSetup
 */
public class ServerSetupTest {
	/**
	 * All options are separated by newlines.
	 * @return A File object set to cfg.txt.
	 * @throws IOException
	 */
	private File createCfg() throws IOException {
		File res = new File("cfg.txt");
		res.createNewFile();
		BufferedWriter bw = MyFileUtils.getBufferedWriter(res);
		bw.write(CfgOptions.MAIN_DIRECTORY + "		main_dir\n");
		bw.write(CfgOptions.PORT + "				4919\n");
		bw.write(CfgOptions.LIB + "					lib_dir\n");
		bw.write(CfgOptions.SELENIUM_SERVER_DIR + "	selenium_server_dir\n");
		bw.write(CfgOptions.LOG + "					YES\n");
		bw.write(CfgOptions.SELENIUM_SERVER_JAR + "	selenium_server_jar\n");
		bw.close();
		return res;
	}
	
	/**
	 * The options are NOT separated by new lines.
	 * @return A File object set to cfg.txt.
	 * @throws IOException
	 */
	private File createCfg_noNewLines() throws IOException {
		File res = new File("cfg.txt");
		res.createNewFile();
		BufferedWriter bw = MyFileUtils.getBufferedWriter(res);
		bw.write(CfgOptions.MAIN_DIRECTORY + "		main_dir");
		bw.write(CfgOptions.PORT + "				4919");
		bw.write(CfgOptions.LIB + "					lib_dir");
		bw.write(CfgOptions.SELENIUM_SERVER_DIR + "	selenium_server_dir");
		bw.write(CfgOptions.LOG + "					YES");
		bw.write(CfgOptions.SELENIUM_SERVER_JAR + "	selenium_server_jar");
		bw.close();
		return res;
	}
	
	/**
	 * Verify that every possible option works.
	 * @throws IOException 
	 * @throws CfgException 
	 * @see CfgOptions
	 */
	@Test
	public void testSetupFromCfgFile_all() throws IOException, CfgException {
		File file = createCfg();
		ServerSetup.setupFromCfgFile(file.getAbsolutePath());
		file.delete();
		
		assertEquals("main_dir/", ExecutorSetup.getURLMainTestFolder());
		assertTrue(ServerSetup.getServerPort() == 4919);
		assertEquals("lib_dir", ExecutorSetup.getURLLibFolder());
		assertEquals("selenium_server_dir", ExecutorSetup.getURLSeleniumServer());
		assertTrue(ServerSetup.isRecord());
		assertEquals("selenium_server_jar.jar", ExecutorSetup.getSeleniumServerName());
	}
	
	/**
	 * Error case: options aren't separated by new lines.<br>
	 * Test case disabled. See ServerUtilsTest.testProcessLine_noNewLine()
	 * @see ServerUtilsTest
	 * @throws IOException 
	 * @throws CfgException 
	 */
	@Test(expected=CfgException.class)
	@Ignore
	public void testSetupFromCfgFile_noNewLines()
			throws IOException, CfgException {
		File file = createCfg_noNewLines();
		ServerSetup.setupFromCfgFile(file.getAbsolutePath());
		file.delete();
		//expect an exception
	}

	/**
	 * Check that record is set to true.
	 * @throws IOException 
	 * @throws CfgException 
	 */
	@Test
	public void testSetupLogOutput_exists() throws IOException, CfgException {
		File file = createCfg();
		ServerSetup.setupFromCfgFile(file.getAbsolutePath());
		assertTrue(ServerSetup.isRecord());
	}

	/**
	 * Verify the handler list: it must contain runTestHashMap and debug. 
	 * @throws XmlRpcException 
	 */
	@Test
	public void testSetupHandler() throws XmlRpcException {
		WebServer server = new WebServer(8080);
		PropertyHandlerMapping phm = ServerSetup.setupHandler(server);
		String[] list = phm.getListMethods();
		boolean runTestOk = false;
		boolean debugOk = false;
		for (String current : list) {
			if (current.contains("server.TestExecutor.runTestHashMap"))
				runTestOk = true;
			else if (current.contains("server.DBDebugger.debug"))
				debugOk = true;
		}
		assertTrue(runTestOk);
		assertTrue(debugOk);
	}
	
}
