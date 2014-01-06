package core;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

public class EnvInfoTest {

	
	@Before
	public void teardown() {
		EnvInfo.reset();
	}
	
	/**
	 * Everything should be reset.
	 */
	@Test
	public void testReset() {
		EnvInfo.setMainDirectory("/home/main_test/");
		EnvInfo.setLibURL("libraries/");
		EnvInfo.setJunitVersion("4.9.7");
		EnvInfo.setSeleniumVersion("2.2.2.1");
		EnvInfo.setTestCaseName("test_name");
		EnvInfo.setPackageName("test_package");
		File file = new File("src/core/templateBuild.xml");
		EnvInfo.setTemplate(file);
		
		EnvInfo.reset();
		
		assertEquals(null, EnvInfo.getTestCaseName());
		assertEquals(EnvInfo.DEFAULT_PACKAGE_NAME, EnvInfo.getPackageName());
		assertEquals(null, EnvInfo.getTemplate());
		assertEquals(EnvInfo.DEFAULT_MAIN_DIRECTORY,
				EnvInfo.getMainDirectory());
		assertEquals(EnvInfo.DEFAULT_JUNIT_VERSION, EnvInfo.getJunitVersion());
		assertEquals(EnvInfo.DEFAULT_SELENIUM_VERSION,
				EnvInfo.getSeleniumVersion());
	}
	
	/**
	 * Basic setter thingy.
	 */
	@Test
	public void testSetTestCaseName_normal() {
		EnvInfo.setTestCaseName("test_name");
		assertEquals("test_name", EnvInfo.getTestCaseName());
	}
	
	/**
	 * The .java file extension, if it is present in the given name, should be
	 * removed.
	 */
	@Test
	public void testSetTestCaseName_java() {
		EnvInfo.setTestCaseName("test_name.java");
		assertEquals("test_name", EnvInfo.getTestCaseName());
	}

	/**
	 * We set set the template to a given file.
	 * Note: We don't check whether the file is a correct template for a
	 * build.xml file.
	 */
	@Test
	public void testSetTemplateFile_existing() {
		File file = new File("src/core/templateBuild.xml");
		EnvInfo.setTemplate(file);
		assertEquals(file, EnvInfo.getTemplate());
	}
	
	/**
	 * We set set the template to a non-existing file. The template should not
	 * be accepted and keep its default value.
	 */
	@Test
	public void testSetTemplateFile_NotExisting() {
		File file = new File("src/not_a_real_folder/templateBuild.xml");
		EnvInfo.setTemplate(file);
		assertEquals(null, EnvInfo.getTemplate());
	}

	/**
	 * Same as before, but using a String url instead.
	 */
	@Test
	public void testSetTemplateString_existing() {
		String string = "src/core/templateBuild.xml";
		File file = new File(string);
		EnvInfo.setTemplate(string);
		assertEquals(file, EnvInfo.getTemplate());
	}
	
	/**
	 * Idem.
	 */
	@Test
	public void testSetTemplateString_notExisting() {
		String string = "src/not_a_real_folder/templateBuild.xml";
		EnvInfo.setTemplate(string);
		assertEquals(null, EnvInfo.getTemplate());
	}
	
	/**
	 * Verifies that the default directory is an empty String.
	 */
	@Test
	public void testGetMainDirectory_default() {
		assertEquals("", EnvInfo.getMainDirectory());
	}

	/**
	 * Checks if the main directory is properly set.
	 */
	@Test
	public void testSetMainDirectory() {
		EnvInfo.setMainDirectory("/home/main_test/");
		assertEquals("/home/main_test/", EnvInfo.getMainDirectory());
	}
	
	/**
	 * Checks if a "/" is added at the end of the URL if it is missing.
	 */
	@Test
	public void testSetMainDirectory_slashAdding() {
		EnvInfo.setMainDirectory("/home/main_test");
		assertEquals("/home/main_test/", EnvInfo.getMainDirectory());
	}

	/**
	 * Verifies that the default lib directory is "lib/".
	 */
	@Test
	public void testGetLibURL_default() {
		assertEquals("lib/", EnvInfo.getLibURL());
	}

	/**
	 * Checks if the lib URL is properly set.
	 */
	@Test
	public void testSetLibURL() {
		EnvInfo.setLibURL("/home/lib/");
		assertEquals("/home/lib/", EnvInfo.getLibURL());
	}
	
	/**
	 * Checks if a "/" is added at the end of the URL if it is missing.
	 */
	@Test
	public void testSetLibURL_slashAdding() {
		EnvInfo.setLibURL("/home/lib");
		assertEquals("/home/lib/", EnvInfo.getLibURL());
	}
}
