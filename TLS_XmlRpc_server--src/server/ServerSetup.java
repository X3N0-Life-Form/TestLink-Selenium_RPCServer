package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;

import exceptions.CfgException;

import app.DirGeneratorInterface;

import utils.CfgOptions;
import utils.MyFileUtils;
import utils.ServerUtils;

/**
 * Contains all context attributes and methods used to setup the server.
 * @author Adrien Droguet
 * @version 1.0	10/04/2012<br>
 * 				Class created.<br>
 * @see TLSJavaServer
 */
public class ServerSetup {
	//
	// Attributes
	//
	
	/**
	 * True = write log in log.txt.
	 */
	private static boolean record = false;
	public static boolean isRecord() {
		return record;
	}
	public static void setRecord(boolean record) {
		ServerSetup.record = record;
	}

	/**
	 * Default is 8081.
	 */
	private static int serverPort = 8081;
	public static void setServerPort(int portNumber) {
		serverPort = portNumber;
	}
	/**
	 * Default is 8081.
	 * @return The current server port.
	 */
	public static int getServerPort() {
		return serverPort;
	}
	
	//
	// Methods
	//
	
	/**
	 * Set up the environment by reading a specified file.
	 * @see CfgOptions
	 * @param url
	 * @throws IOException If the specified file does not exist.
	 * @throws CfgException If a line is incorrectly formed in the config file.
	 */
	public static void setupFromCfgFile(String url)
			throws IOException, CfgException {
		File file = new File(url);
		BufferedReader br = MyFileUtils.getBufferedReader(file);
		String line = "not null";
		//the new line is read before being checked
		while ((line = br.readLine()) != null) {
			String sub = "";
			if (line.startsWith("#")) {
				continue;
			} else if (line.startsWith(CfgOptions.MAIN_DIRECTORY)) {
				sub = ServerUtils.processLine(line,
						CfgOptions.MAIN_DIRECTORY.length());
				ExecutorSetup.setURLMainTestFolder(sub);
				continue;
			} else if (line.startsWith(CfgOptions.PORT)) {
				sub = ServerUtils.processLine(line,
						CfgOptions.PORT.length());
				serverPort = Integer.parseInt(sub);
				continue;
			} else if (line.startsWith(CfgOptions.LIB)) {
				sub = ServerUtils.processLine(line,
						CfgOptions.LIB.length());
				ExecutorSetup.setURLLibFolder(sub);
				continue;
			} else if(line.startsWith(CfgOptions.SELENIUM_SERVER_DIR)) {
				sub = ServerUtils.processLine(line,
						CfgOptions.SELENIUM_SERVER_DIR.length());
				ExecutorSetup.setURLSeleniumServer(sub);
				continue;
			} else if (line.startsWith(CfgOptions.LOG)) {
				sub = ServerUtils.processLine(line,
						CfgOptions.LOG.length());
				if (sub.equalsIgnoreCase("YES"))
					record = true;
				continue;
			} else if (line.startsWith(CfgOptions.SELENIUM_SERVER_JAR)) {
				sub = ServerUtils.processLine(line,
						CfgOptions.SELENIUM_SERVER_JAR.length());
				ExecutorSetup.setSeleniumServerName(sub);
				continue;
			} else if (line.startsWith(CfgOptions.JUNIT_VERSION)) {
				sub = ServerUtils.processLine(line,
						CfgOptions.JUNIT_VERSION.length());
				DirGeneratorInterface.setJUnitVersion(sub);
				continue;
			} else if (line.startsWith(CfgOptions.SELENIUM_VERSION)) {
				sub = ServerUtils.processLine(line,
						CfgOptions.SELENIUM_VERSION.length());
				DirGeneratorInterface.setSeleniumVersion(sub);
				continue;
			}
		}
	}
	
	/**
	 * Changes the standard and error output to log.txt.
	 * @throws FileNotFoundException
	 */
	public static void setupLogOutput() throws FileNotFoundException {
		System.out.println("Changing output to log.txt.");
		PrintStream log = new PrintStream(new File("log.txt"));
		System.setOut(log);
		System.setErr(log);
	}
	
	/**
	 * Sets up the server's property handler.
	 * @param server
	 * @return A properly setup PropertyHandlerMapping.
	 * @throws XmlRpcException
	 */
	public static PropertyHandlerMapping setupHandler(WebServer server)
			throws XmlRpcException {
		XmlRpcServer xrs =
				server.getXmlRpcServer();
		PropertyHandlerMapping phm = new PropertyHandlerMapping();
		phm.addHandler("server.TestExecutor", TestExecutor.class);
		phm.addHandler("server.DBDebugger", DBDebugger.class);
		xrs.setHandlerMapping(phm);//!!!
		XmlRpcServerConfigImpl serverConfig =
				(XmlRpcServerConfigImpl) xrs.getConfig();
		serverConfig.setEnabledForExtensions(true);
		return phm;
	}
	
	/**
	 * Resets every attribute to its default value.
	 */
	public static void reset() {
		record = false;
		serverPort = 8081;
		DirGeneratorInterface.setJUnitVersion("4.10");
		DirGeneratorInterface.setSeleniumVersion("2.20.0");
	}
}
