package app;

import core.EnvInfo;

/**
 * Contains all information and methods required to configure and use the
 * DirGenerator from the server.
 * @author Adrien Droguet
 * @version 1.0	17/04/2012<br>
 * 				Class created.<br>
 */
public class DirGeneratorInterface {

	private static String testFileName;
	public static String getTestFileName() {
		return testFileName;
	}
	public static void setTestFileName(String testFileName) {
		DirGeneratorInterface.testFileName = testFileName;
	}
	
	public static void setURLMainTestFolder(String url) {
		EnvInfo.setMainDirectory(url);
	}
	
	public static void setLibURL(String url) {
		EnvInfo.setLibURL(url);
	}
	
	public static void setJUnitVersion(String version) {
		EnvInfo.setJunitVersion(version);
	}
	public static String getJUnitVersion() {
		return EnvInfo.getJunitVersion();
	}
	
	public static void setSeleniumVersion(String version) {
		EnvInfo.setSeleniumVersion(version);
	}
	public static String getSeleniumVersion() {
		return EnvInfo.getSeleniumVersion();
	}
	
	
	/**
	 * Runs DirGenerator with the previously specified parameters.
	 */
	public static void runDirGenerator() {
		String[] execLine = {
				testFileName,
				"-md",
				EnvInfo.getMainDirectory(),
				"-lib",
				EnvInfo.getLibURL()
		};
		app.CommandLine.main(execLine);
	}

	
}
