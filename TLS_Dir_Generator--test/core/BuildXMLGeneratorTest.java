package core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BuildXMLGeneratorTest {
	
	private static File file = null;
	private static File build = null;
	private static final String root = "E:/dir_struc_test/";
	
	/**
	 * Sets up the testing directory.
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
		File file = new File(root);
		if (!file.exists())
			file.mkdir();
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		File file = new File(root);
		file.delete();
	}

	@Before
	public void setUp() {
		BuildXMLGenerator.reset();
	}
	
	@After
	public void tearDown() {
		try {
			build.delete();
			file.delete();
		} catch (NullPointerException e) {
			
		}
	}
	
	
	
	/**
	 * Test name specified when calling the method.
	 * @throws IOException 
	 */
	@Test
	public void testCreateBuildXML_testName() throws IOException {
		EnvInfo.setMainDirectory(root);
		file = new File(root + "test_name/");
		file.mkdir();
		EnvInfo.setTemplate("src/core/templateBuild.xml");
		String log = BuildXMLGenerator.createBuildXML("test_name");
		build = new File(file.getAbsolutePath() + "/build.xml");
		
		//check that the file actually exist before accessing it
		assertTrue(build.exists());
		//
		// preparing file structure checks
		//
		String fileContent = "";
		FileInputStream fis = new FileInputStream(build);
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(isr);
		String line = "";
		while ((line = br.readLine()) != null) {
			fileContent += "\n" + line;
		}
		br.close();
		// step 1: check log
		//means that there was no error during the generation of the build.xml
		assertTrue(log.contains("Creation complete"));
		// step 2: check file itself
		assertTrue(fileContent.contains(
				"<property name=\"test_name\" value=\"test_name\"/>"));
		assertTrue(fileContent.contains(
				"<property name=\"test_package\" value=\"\"/>"));
	}
	
	/**
	 * Test name unspecified when calling the method.
	 * Note: We only check if it exists as it should. The proper structure-
	 * checking is done in the previous test. 
	 * @throws IOException 
	 */
	@Test
	public void testCreateBuildXML_noName() throws IOException {
		EnvInfo.setMainDirectory(root);
		file = new File(root + "test_name/");
		file.mkdir();
		EnvInfo.setTemplate("src/core/templateBuild.xml");
		EnvInfo.setTestCaseName("test_name");
		String log = BuildXMLGenerator.createBuildXML();
		build = new File(file.getAbsolutePath() + "/build.xml");
		assertTrue(build.exists());
		assertTrue(log.contains("Creation complete"));
	}
	
	/**
	 * Tests the same method as in the previous test, but without setting a test
	 * name beforehand. Which should throw an exception.
	 * @throws IOException 
	 */
	@Test(expected = NullPointerException.class)
	public void testCreateBuildXML_noNameSet() throws IOException {
		EnvInfo.setMainDirectory(root);
		file = new File(root + "test_name/");
		file.mkdir();
		EnvInfo.setTemplate("src/core/templateBuild.xml");
		//not setting any name
		String log = BuildXMLGenerator.createBuildXML();
		build = new File(file.getAbsolutePath() + "/build.xml");
		assertFalse(build.exists());
		assertFalse(log.contains("Creation complete"));
	}

}
