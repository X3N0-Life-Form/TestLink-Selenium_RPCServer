package server;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * Contains all context attributes and setup methods used by TestExecutor.
 * @author Adrien Droguet
 * @version 1.0	5/04/2012<br>
 * 				Refactored from TestExecutor.<br>
 * @see TestExecutor
 */
public class ExecutorSetup {
	//
	// Attributes
	//
	/**
	 * "lib/"
	 */
	private static final String DEFAULT_URL_LIB_FOLDER = "lib/";
	/**
	 * URL of the lib folder, which is supposed to contained all the external
	 * libraries required to execute any test case.
	 */
	private static  String URLLibFolder = DEFAULT_URL_LIB_FOLDER;
	public static void setURLLibFolder(String url) {
		URLLibFolder = url;
	}
	public static String getURLLibFolder() {
		return URLLibFolder;
	}
	
	/**
	 * ""
	 */
	private static final String DEFAULT_URL_SELENIUM_SERVER = "";
	/**
	 * URL of the Selenium server. The Selenium server is sometimes required
	 * to run in order to execute a test or a test suite. The URL is deduced
	 * from build.xml.
	 */
	private static String URLSeleniumServer = DEFAULT_URL_SELENIUM_SERVER;
	public static String getURLSeleniumServer() {
		return URLSeleniumServer;
	}
	public static void setURLSeleniumServer(String URLSeleniumServer) {
		ExecutorSetup.URLSeleniumServer = URLSeleniumServer;
	}
	
	/**
	 * ""
	 */
	private static final String DEFAULT_URL_MAIN_TEST_FOLDER = "";
	/**
	 * Where all the tests are located. Change it on a per project basis.
	 * Must end with a '/'.
	 */
	private static String URLMainTestFolder = DEFAULT_URL_MAIN_TEST_FOLDER;
	/**
	 * Always ends with a "/".
	 * @return URLMainTestFolder
	 */
	public static String getURLMainTestFolder() {
		return URLMainTestFolder;
	}
	public static void setURLMainTestFolder(String URLMainTestFolder) {
		ExecutorSetup.URLMainTestFolder = URLMainTestFolder;
		if (!URLMainTestFolder.endsWith("/"))
			ExecutorSetup.URLMainTestFolder += "/";
	}
	
	/**
	 * null
	 */
	private static final String DEFAULT_REPORT_URL = null;
	/**
	 * The URL of the report folder. It is now set automatically whenever
	 * runTest() is called.
	 */
	private static String reportURL = DEFAULT_REPORT_URL;
	public static String getReportURL() {
		return reportURL;
	}
	public static void setReportURL(String reportURL) {
		ExecutorSetup.reportURL = reportURL;
		if (!reportURL.endsWith("/"))
			ExecutorSetup.reportURL += "/";
	}
	
	/**
	 * null
	 */
	private static final String DEFAULT_TEST_NAME = null;
	/**
	 * Deduced from test_name in build.xml whenever runTest() is called.
	 */
	private static String testName = DEFAULT_TEST_NAME;
	public static String getTestName() {
		return testName;
	}
	public static void setTestName(String testName) {
		ExecutorSetup.testName = testName;
	}
	
	/**
	 * null
	 */
	private static final String DEFAULT_TEST_PACKAGE = null;
	/**
	 * Deduced from test_package in build.xml.
	 */
	private static String testPackage = DEFAULT_TEST_PACKAGE;
	public static String getTestPackage() {
		return testPackage;
	}
	public static void setTestPackage(String testPackage) {
		ExecutorSetup.testPackage = testPackage;
	}
	
	/**
	 * null
	 */
	private static final String DEFAULT_TEST_FOLDER = null;
	/**
	 * The name of the folder in which is located the test case. Determined by
	 * the argument passed to runTest().<br>
	 * Note: it is wiser to use setTestFolder() to set its value.
	 */
	private static String testFolder = DEFAULT_TEST_FOLDER;
	/**
	 * Always ends with a '/'.
	 * @return The name of the folder in which is located the test case.
	 */
	public static String getTestFolder() {
		return testFolder;
	}
	public static void setTestFolder(String testFolder) {
		ExecutorSetup.testFolder = testFolder;
		if (!testFolder.endsWith("/"))
			ExecutorSetup.testFolder += "/";
		//no absolute paths please
		int slashCount = 0;
		for (int i = ExecutorSetup.testFolder.length() - 1; i > 0; i--) {
			if (ExecutorSetup.testFolder.charAt(i) == '/'
					|| ExecutorSetup.testFolder.charAt(i) == '\\')
				slashCount++;
			if (slashCount == 2) {
				//charAt(i) == '/' number 2
				ExecutorSetup.testFolder = ExecutorSetup.testFolder
						.substring(i + 1);
				break;
			}
		}
	}
	
	//
	// Methods
	//
	
	/**
	 * Sets up the report folder's url for this testCase.
	 */
	public static void setupReportURL() {
		reportURL = URLMainTestFolder + testFolder + "report/";
	}

	/**
	 * Sets up information based on the information contained in the build.xml
	 * file, namely the test's name, the main directory and the location of the
	 * required libraries.
	 */
	public static void setupFromBuildXML() {
		SAXBuilder builder = new SAXBuilder();
		//BUG -- both paths are absolute
		File buildXMLFile = new File(URLMainTestFolder
				+ testFolder
				+ "build.xml");
		if (!buildXMLFile.exists()) {
			System.out.println(buildXMLFile + " was not found.");
		} else {
			try {
				Document doc = builder.build(buildXMLFile);
				@SuppressWarnings("unchecked")
				List<Element> list = doc.getRootElement().getChildren("property");
				for (Element current : list) {
					switch (current.getAttributeValue("name")) {
					case "test_name": {
						testName = current.getAttributeValue("value");
						break;
					}
					case "lib": {
						URLSeleniumServer = current.getAttributeValue("location");
						break;
					}
					case "test_package": {
						testPackage = current.getAttributeValue("value");
						break;
					}
					}
				}
			} catch (JDOMException | IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * selenium-server-standalone-2.20.0.jar
	 */
	private static String DEFAULT_SELENIUM_SERVER_NAME =
			"selenium-server-standalone-2.20.0.jar";
	/**
	 * Name of the selenium server file; should always end with a .jar.
	 */
	private static String seleniumServerStandAloneJarName =
			DEFAULT_SELENIUM_SERVER_NAME;
	/**
	 * Name of the selenium server .jar file;
	 * @return A String.
	 */
	public static String getSeleniumServerName() {
		return seleniumServerStandAloneJarName;
	}
	/**
	 * Sets the name of the .jar file containing the Selenium server.
	 * @param name
	 */
	public static void setSeleniumServerName(String name) {
		seleniumServerStandAloneJarName = name;
		if (!name.endsWith(".jar"))
			seleniumServerStandAloneJarName += ".jar";
	}
	
	/**
	 * Resets attributes to their default values. Used mostly for testing
	 * purposes.
	 */
	public static void reset() {
		reportURL = DEFAULT_REPORT_URL;
		testFolder = DEFAULT_TEST_FOLDER;
		testName = DEFAULT_TEST_NAME;
		testPackage = DEFAULT_TEST_PACKAGE;
		URLLibFolder = DEFAULT_URL_LIB_FOLDER;
		URLMainTestFolder = DEFAULT_URL_MAIN_TEST_FOLDER;
		URLSeleniumServer = DEFAULT_URL_SELENIUM_SERVER;
		seleniumServerStandAloneJarName = DEFAULT_SELENIUM_SERVER_NAME;
	}
}
