package server;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.junit.Ignore;
import org.junit.Test;

import exceptions.TestExecutorException;



/**
 * Note to testers: Some tests will only pass under specific circumstances,
 * ctrl+f "Notes to testers" to find which tests might be sensitive to the test
 * environment.
 * @author Adrien Droguet
 * @see TestExecutorTestRegex
 */
public class TestExecutorTest {

	
	
	private static final String TEST_OK = "TestOK";
	private static final String TEST_XML_SAMPLE_FILES = "test/xml_sample_files";

	/**
	 * OK condition: the specified test runs.<br>
	 * Notes to testers: Check that the specified folder and test name are valid.
	 * @throws IOException
	 * @throws TestExecutorException
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testRunTest_classic()
			throws IOException {
		ExecutorSetup.setURLMainTestFolder(TEST_XML_SAMPLE_FILES);
		try {
			TestExecutor.runTest(TEST_OK);
		} catch (TestExecutorException e) {
			fail("The test failed");
		}
	}
	
	/**
	 * OK conditions remain the same, though the called method is different.<br>
	 * Notes to testers: check that one too.
	 * @throws IOException 
	 */
	@Test
	public void testRunTest_new_nameOnly() throws IOException {
		ExecutorSetup.setURLMainTestFolder(TEST_XML_SAMPLE_FILES);
		HashMap<Object, Object> args = new HashMap<Object, Object>();
		args.put("testCaseName", TEST_OK);
		TestExecutor te = new TestExecutor();
		try {
			te.runTestHashMap(args);
		} catch (TestExecutorException e) {
			e.printStackTrace();
			fail("The test couldn't run.");
		}
	}
	
	/**
	 * Success == the test runs + returns results, i.e. the HashMap contains
	 * something.
	 * @throws TestExecutorException 
	 * @throws IOException 
	 */
	@Test
	public void testRunScript_ok() throws IOException, TestExecutorException {
		ExecutorSetup.setURLMainTestFolder("E:/test_main");
		ExecutorSetup.setURLLibFolder("E:/test_main/lib/");
		// Note: this has nothing to do with how runScript works, we are just
		// reading a .java file containing a test script. 
		File testFile = new File("test/xml_sample_files/FakeTestCase.java");
		
		String script = "";
		String line = "";
		//reading the file
		FileInputStream fis = new FileInputStream(testFile);
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(isr);
		while ((line = br.readLine()) != null) {
			script += "\n" + line;
		}
		br.close();
		//----------------------------------------------------------------------
		HashMap<Object, Object> res =
				TestExecutor.runScript("FakeTestCase", script, null);
		
		assertTrue(res.containsKey("result"));
		assertTrue(res.containsKey("notes"));
		assertTrue(res.containsKey("scheduled"));
		assertTrue(res.containsKey("timestampISO"));
	}
	
	/**
	 * When feeding an invalid script to runScript.
	 * @throws TestExecutorException Expected behavior.
	 * @throws IOException 
	 */
	@Test(expected=TestExecutorException.class)
	public void testRunScript_ko() throws IOException, TestExecutorException {
		ExecutorSetup.setURLMainTestFolder("E:/test_main");
		ExecutorSetup.setURLLibFolder("E:/test_main/lib/");
		String script = "invalid script thingy";
		TestExecutor.runScript("FakeTestCase", script, null);
	}
	
	/**
	 * testName & class name mismatch.<br>
	 * Test case ignored, see deprecated.
	 * @throws TestExecutorException Expected behavior.
	 * @throws IOException 
	 * @deprecated Mismatches are now allowed.
	 */
	@Test(expected=TestExecutorException.class)
	@Ignore
	public void testRunScript_koWrongName()
			throws IOException, TestExecutorException {
		ExecutorSetup.setURLMainTestFolder("E:/test_main");
		ExecutorSetup.setURLLibFolder("E:/test_main/lib/");
		String script = "public class FakeTestThingy";
		TestExecutor.runScript("FakeTestCase", script, null);
	}
	
	/**
	 * OK = script contains public class [testName]
	 * @deprecated insufficient
	 * @see #testIsScriptValid_ok_more()
	 */
	//@Test
	public void testIsScriptValid_ok() {
		assertTrue(TestExecutor.isScriptValid(
				"FakeTestCase",
				"public class FakeTestCase"));
	}
	
	/**
	 * Script is null.
	 */
	@Test
	public void testIsScriptValid_koNull() {
		assertFalse(TestExecutor.isScriptValid("FakeTestCase", null));
	}
	
	/**
	 * !OK
	 * @deprecated Test name and test class name can now be different.
	 */
	//@Test
	public void testIsScriptValid_koWrongTestName() {
		assertFalse(TestExecutor.isScriptValid(
				"FakeTestCaseWrong",
				"public class FakeTestCase"));
	}
	
	/**
	 * Appears technically identical to the previous test, but actually checks
	 * the code's toughness.
	 * @deprecated Test name and test class name can now be different. 
	 */
	//@Test
	public void testIsScriptValid_koWrongScriptName() {
		assertFalse(TestExecutor.isScriptValid(
				"FakeTestCase",
				"public class FakeTestCaseWrong"));
	}
	
	/**
	 * More detailed syntax testing.
	 * @deprecated idem. 
	 */
	//@Test
	public void testIsScriptValid_ok_more() {
		assertTrue(TestExecutor.isScriptValid(
				"FakeTestCase",
				"public class FakeTestCase "));
		assertTrue(TestExecutor.isScriptValid(
				"FakeTestCase",
				"public class FakeTestCase{"));
		assertTrue(TestExecutor.isScriptValid(
				"FakeTestCase",
				"public class FakeTestCase\n"));
	}
	
	
}
