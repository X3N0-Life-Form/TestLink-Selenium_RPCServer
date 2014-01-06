package core;

import java.io.File;

/**
 * Used to store  information related to the environment, Namely the main
 * directory and the library directory. Does NOT store information related to
 * individual test cases. 
 * @author Adrien Droguet
 * @version 1.0
 * @version 1.1 15/03/2012<br>
 * 				Made some adjustments to default paths.<br>
 * @version 2.0 17/04/2012<br>
 * 				Added various new attributes previously located in the class
 * 				BuildXMLGenerator.<br>
 * 				Added default value constants to various attributes.<br>
 */
public class EnvInfo {

	
	//
	// Default values
	//
	public static final String DEFAULT_SELENIUM_VERSION = "2.20.0";
	public static final String DEFAULT_JUNIT_VERSION = "4.10";
	public static final String DEFAULT_LIB_URL = "lib/";
	public static final String DEFAULT_MAIN_DIRECTORY = "";
	public static final String DEFAULT_PACKAGE_NAME = "";
	public static final String DEFAULT_REPORT_FOLDER = "report";
	
	//
	// Attributes
	//
	private static String mainDirectory = DEFAULT_MAIN_DIRECTORY;
	public static String getMainDirectory() {
		return mainDirectory;
	}
	/**
	 * Adds a final "/" if none is present. No URL-validity check is performed.
	 * @param mainDirectory
	 */
	public static void setMainDirectory(String mainDirectory) {
		EnvInfo.mainDirectory = mainDirectory;
		if (!mainDirectory.endsWith("/"))
			EnvInfo.mainDirectory += "/";
	}
	
	private static String libURL = DEFAULT_LIB_URL;
	public static String getLibURL() {
		return libURL;
	}
	/**
	 * Adds a final "/" if none is present. No URL-validity check is performed.
	 * @param libURL
	 */
	public static void setLibURL(String libURL) {
		EnvInfo.libURL = libURL;
		if (!libURL.endsWith("/"))
			EnvInfo.libURL += "/";
	}
	
	/**
	 * JUnit version being used.
	 */
	private static String junitVersion = DEFAULT_JUNIT_VERSION;
	public static String getJunitVersion() {
		return junitVersion;
	}
	public static void setJunitVersion(String version) {
		EnvInfo.junitVersion = version;
	}
	
	/**
	 * Selenium version being used.
	 */
	private static String seleniumVersion = DEFAULT_SELENIUM_VERSION;
	public static String getSeleniumVersion() {
		return seleniumVersion;
	}
	public static void setSeleniumVersion(String version) {
		EnvInfo.seleniumVersion = version;
	}
	
	/**
	 * Default value is null.
	 * Set when createBuildXML() is called.
	 * Resets to default value at the end of the same method.
	 * If it is present, removes .java from the end of the testCaseName.
	 */
	private static String testCaseName = null;
	public static void setTestCaseName(String testCaseName) {
		EnvInfo.testCaseName = testCaseName.replace(".java", "");
	}
	public static String getTestCaseName() {
		return EnvInfo.testCaseName;
	}
	
	/**
	 * Default value is "".
	 * Should be set by the CommandLine class.
	 * Reset to default at the end of createBuildXML().
	 * If null is provided to the setter, it will set packageName to default.
	 */
	private static String packageName = DEFAULT_PACKAGE_NAME;
	public static String getPackageName() {
		return packageName;
	}
	public static void setPackageName(String packageName) {
		if (packageName == null)
			EnvInfo.packageName = DEFAULT_PACKAGE_NAME;
		else
			EnvInfo.packageName = packageName;
	}
	
	/**
	 * This is a template file for creating build.xml files. It should be called
	 * templateBuild.xml.
	 */
	private static File template = null;
	public static File getTemplate() {
		return template;
	}
	public static void setTemplate(File template) {
		if (template.exists())
			EnvInfo.template = template;
	}
	public static void setTemplate(String templateURL) {
		setTemplate(new File(templateURL));
	}
	
	//
	// Methods
	//
	
	/**
	 * Resets everything to their default values.
	 */
	public static void reset() {
		mainDirectory = DEFAULT_MAIN_DIRECTORY;
		libURL = DEFAULT_LIB_URL;
		
		junitVersion = DEFAULT_JUNIT_VERSION;
		seleniumVersion = DEFAULT_SELENIUM_VERSION;
		testCaseName = null;
		packageName = DEFAULT_PACKAGE_NAME;
		template = null;
		report = DEFAULT_REPORT_FOLDER;
	}
	
	private static String report = DEFAULT_REPORT_FOLDER;
	public static void setReport(String report) {
		EnvInfo.report = report;
	}
	public static String getReport() {
		return report;
	}
}
