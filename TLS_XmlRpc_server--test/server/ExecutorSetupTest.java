package server;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ExecutorSetupTest {
	/**
	 * Checks whether setUrlMainTestFolder() adds a "/" at the end of the url if
	 * none is present.
	 */
	@Test
	public void testSetURLMainTestFolder_noSlash() {
		ExecutorSetup.setURLMainTestFolder("test_main");
		assertEquals("test_main/", ExecutorSetup.getURLMainTestFolder());
	}
	
	@Test
	public void testSetURLMainTestFolder_slash() {
		ExecutorSetup.setURLMainTestFolder("test_main/");
		assertEquals("test_main/", ExecutorSetup.getURLMainTestFolder());
	}
	
	/**
	 * Again, we must make sure that the folder name always gets a "/" added at
	 * the end.
	 */
	@Test
	public void testSetTestFolder_noSlash() {
		ExecutorSetup.setURLMainTestFolder("test_main");
		ExecutorSetup.setTestFolder("test_case");
		assertEquals("test_case/", ExecutorSetup.getTestFolder());
	}
	
	@Test
	public void testSetTestFolder_slash() {
		ExecutorSetup.setURLMainTestFolder("test_main");
		ExecutorSetup.setTestFolder("test_case/");
		assertEquals("test_case/", ExecutorSetup.getTestFolder());
	}
	
	/**
	 * No absolute paths should be tolerated, as the test folder is supposed to
	 * be located in the main test folder.
	 */
	@Test
	public void testSetTestFolder_absolutePath() {
		ExecutorSetup.setURLMainTestFolder("test_main");
		ExecutorSetup.setTestFolder("/home/test_case/");
		assertEquals("test_case/", ExecutorSetup.getTestFolder());
	}
	
	/**
	 * Checks that the report folder's url is set up correctly.
	 */
	@Test
	public void testSetupReportURL() {
		ExecutorSetup.setURLMainTestFolder("test_main");
		ExecutorSetup.setTestFolder("test_case");
		ExecutorSetup.setupReportURL();
		assertEquals("test_main/test_case/report/",
				ExecutorSetup.getReportURL());
	}
	
	/**
	 * Notes to testers: check the values from build.xml.
	 */
	@Test
	public void testSetupFromBuildXML() {
		ExecutorSetup.setURLMainTestFolder("test");
		ExecutorSetup.setTestFolder("xml_sample_files");
		ExecutorSetup.setupFromBuildXML();
		assertEquals("test_case", ExecutorSetup.getTestName());
		assertEquals("test_package", ExecutorSetup.getTestPackage());
		assertEquals("lib/", ExecutorSetup.getURLSeleniumServer());
	}
}
