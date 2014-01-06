package plugins;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import exceptions.TestExecutorException;



/**
 * Extends this class when testing classes that implements Plugin
 * @author Adrien Droguet
 * @see Plugin
 */
public abstract class PluginTest {

	/**
	 * Expected characteristics:<br>
	 * name = "test"<br>
	 * extension = ".txt"<br>
	 * scriptRegex = "regex"<br>
	 * runTestCommandLine = "command line"<br>
	 * runTestScriptCommandLine = "script command line"<br>
	 */
	protected static Plugin testPlugin;
	protected static final String NAME = "test";
	protected static final String EXTENSION = ".txt";
	protected static final String REGEX = "regex\\s\\S+";
	protected static final String RUN_TEST_COMMAND_LINE = "command line";
	protected static final String RUN_TEST_SCRIPT_COMMAND_LINE
		= "script command line";
	protected static final String VERSION = "1.0";
	
	/**
	 * Initialize testPlugin.
	 */
	@Before
	public abstract void setup();
	
	@Test
	public void testGetName() {
		assertEquals(NAME, testPlugin.getName());
	}

	@Test
	public void testGetFileExtension() {
		assertEquals(EXTENSION, testPlugin.getFileExtension());
	}

	@Test
	public void testGetScriptRegex() {
		assertEquals(REGEX, testPlugin.getScriptRegex());
	}

	/**
	 * Should verify that all required keys are present, namely:<br>
	 * -result<br>
	 * -notes<br>
	 * -timestampISO<br>
	 * -scheduled<br>
	 * @throws TestExecutorException Additional tests may be required for other
	 * cases, error or otherwise.
	 */
	@Test
	public abstract void testRunTest() throws TestExecutorException;

	/**
	 * Validate the string through a regular expression, then run it.
	 * @exception TestExecutorException Check invalid script scenarii in other
	 * test cases.
	 */
	@Test
	public abstract void testRunTestString() throws TestExecutorException;

	@Test
	public void testGetRunTestCmdLine() {
		assertEquals(RUN_TEST_COMMAND_LINE, testPlugin.getRunTestCmdLine());
	}

	@Test
	public void testGetRunTestScriptCmdLine() {
		assertEquals(RUN_TEST_SCRIPT_COMMAND_LINE,
				testPlugin.getRunTestScriptCmdLine());
	}
	
	@Test
	public void testGetVersion() {
		assertEquals(VERSION, testPlugin.getVersion());
	}
	
	/**
	 * If something goes wrong, testers should verify that the provided regex is
	 * still valid.
	 */
	@Test
	public void testIsScriptValid() {
		assertTrue(testPlugin.isScriptValid("regex r"));
	}
}
