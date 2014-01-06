package utils;

import server.TLSJavaServer;

/**
 * Only contains option field that can present in a configuration file.<br>
 * You may comment out a line by using the character '#'.
 * @author Adrien Droguet
 * @see TLSJavaServer
 */
public class CfgOptions {
	/**
	 * Defines the server's main test directory.
	 */
	public static final String MAIN_DIRECTORY = "MainDirectory:";
	/**
	 * Defines the server's port.
	 */
	public static final String PORT = "Port:";
	/**
	 * Defines the server's main lib directory.
	 */
	public static final String LIB = "Lib:";
	/**
	 * Where the selenium server is located.
	 */
	public static final String SELENIUM_SERVER_DIR = "SeleniumServerDir:";
	/**
	 * When set, changes the standard and error output to log.txt.
	 */
	public static final String LOG = "Log:";
	/**
	 * Name of the selenium server file.
	 */
	public static final String SELENIUM_SERVER_JAR = "SeleniumServerJar:";
	/**
	 * JUnit version being used.
	 */
	public static final String JUNIT_VERSION = "JUnitVersion:";
	/**
	 * Selenium version being used.
	 */
	public static final String SELENIUM_VERSION = "SeleniumVersion:";
	/**
	 * When set to "YES", prevents the cfg file from overriding command line
	 * arguments.
	 */
	public static final String NO_OVERRIDE = "NoOverride:";
}
