package plugins;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import exceptions.TestExecutorException;


import server.ExecutorSetup;
import utils.MyFileUtils;

/**
 * Tests the basic plugin object implementation.
 * @author Adrien Droguet
 * @see Plugin
 * @see PluginImpl
 * @see PluginTest
 */
public class PluginImplTest extends PluginTest {

	private static PluginImpl testPluginPython = null;
	private String pythonScriptName = "PythonTestScript.py";
	private String pythonScriptURL =
			"E:/eclipse workspace & lib/TLS_XmlRpcServer/"; 
	
	
	private PluginImpl getNewTestPlugin() {
		PluginImpl testPlugin = new PluginImpl();
		testPlugin.setName(NAME);
		testPlugin.setVersion(VERSION);
		testPlugin.setFileExtensionList(new LinkedList<String>());
		testPlugin.setScriptRegex(REGEX);
		testPlugin.setRunTestCmdLine(RUN_TEST_COMMAND_LINE);
		testPlugin.setRunTestScriptCmdLine(RUN_TEST_SCRIPT_COMMAND_LINE);
		return testPlugin;
	}
	
	/**
	 * Returns a valid python test script.
	 * @return See above.
	 * @throws IOException 
	 */
	private String getPythonScript() throws IOException {
		File file = new File(pythonScriptURL + pythonScriptName);
		BufferedReader br = MyFileUtils.getBufferedReader(file);
		return MyFileUtils.readFile(br);
	}
	
	/**
	 * Should later be handled by PluginManager.
	 */
	private void setupExecutorSetup() {
		ExecutorSetup.setReportURL(pythonScriptURL + "testReports/");
		File file = new File(pythonScriptURL + "testReports/");
		if (!file.exists())
			file.mkdir();
		
		ExecutorSetup.setTestName(pythonScriptName.replace(".py", ""));
	}
	
	@Override
	public void setup() {
		testPlugin = getNewTestPlugin();
		((PluginImpl) testPlugin).getFileExtensionList().add(".txt");
		
		//python test plugin stuff
		List<String> list = new LinkedList<String>();
		list.add(".py");
		testPluginPython = new PluginImpl();
		testPluginPython.setName("pythonDummyPlugin");
		testPluginPython.setVersion("1.0");
		testPluginPython.setFileExtensionList(list);
		testPluginPython.setScriptRegex(".*");
		testPluginPython.setRunTestCmdLine(
				"python " + pythonScriptURL + pythonScriptName);
		testPluginPython.setRunTestScriptCmdLine("");
	}
	
	/**
	 * Sets testPlugin to a version-less version of itself.
	 */
	private void versionLessSetup() {
		testPlugin = getNewTestPlugin();
		((PluginImpl) testPlugin).getFileExtensionList().add(".txt");
	}
	
	/**
	 * Test the constructor. Note that the constructor is already called is the
	 * setup method.
	 * @see #setup()
	 */
	@Test
	public void testPluginImpl() {
		assertEquals(NAME,
				testPlugin.getName());
		assertEquals(EXTENSION,
				testPlugin.getFileExtension());
		assertEquals(REGEX,
				testPlugin.getScriptRegex());
		assertEquals(RUN_TEST_COMMAND_LINE,
				testPlugin.getRunTestCmdLine());
		assertEquals(RUN_TEST_SCRIPT_COMMAND_LINE,
				testPlugin.getRunTestScriptCmdLine());
		List<String> list = new LinkedList<String>();
		list.add(EXTENSION);
		assertEquals(list ,
				((PluginImpl) testPlugin).getFileExtensionList());
	}
	
	/**
	 * tests the version-less constructor.
	 */
	@Test
	public void testPluginImpl_versionless() {
		versionLessSetup();
		assertEquals(NAME,
				testPlugin.getName());
		assertEquals(EXTENSION,
				testPlugin.getFileExtension());
		assertEquals(REGEX,
				testPlugin.getScriptRegex());
		assertEquals(RUN_TEST_COMMAND_LINE,
				testPlugin.getRunTestCmdLine());
		assertEquals(RUN_TEST_SCRIPT_COMMAND_LINE,
				testPlugin.getRunTestScriptCmdLine());
		List<String> list = new LinkedList<String>();
		list.add(EXTENSION);
		assertEquals(list ,
				((PluginImpl) testPlugin).getFileExtensionList());
	}
	
	@Override
	public void testRunTest() throws TestExecutorException {
		//TODO: need to create a proper plugin - even if a dummy/fake one
		HashMap<Object, Object> resultMap = testPluginPython.runTest();
		assertTrue(resultMap.containsKey("result"));
		assertTrue(resultMap.containsKey("notes"));
		assertTrue(resultMap.containsKey("timestampISO"));
		assertTrue(resultMap.containsKey("scheduled"));
	}
	
	//TODO: sort out this mess
	//current error: report file not found --> create plugin that creates a
	//dummy file
	@Override
	public void testRunTestString()
			throws TestExecutorException {
		String script = null;
		try {
			script = getPythonScript();
		} catch (IOException e) {
			e.printStackTrace();
		}
		setupExecutorSetup();
		
		HashMap<Object, Object> resultMap = testPluginPython.runTest(script);
		assertTrue(resultMap.containsKey("result"));
		assertTrue(resultMap.containsKey("notes"));
		assertTrue(resultMap.containsKey("timestampISO"));
		assertTrue(resultMap.containsKey("scheduled"));
	}

	/**
	 * An invalid script should throw an exception.
	 * @throws TestExecutorException
	 */
	@Test(expected=TestExecutorException.class)
	public void testRunTestString_invalid() throws TestExecutorException {
		testPlugin.runTest("This is an invalid script.");
	}
	
	/**
	 * Returns "" if the List is empty.
	 */
	@Test
	public void testGetFileExtension_emptyList() {
		PluginImpl otherPlugin = getNewTestPlugin();
		assertEquals("", otherPlugin.getFileExtension());
	}
	
	/**
	 * 
	 */
	@Test
	public void testFormatLine() {
		
	}
}
