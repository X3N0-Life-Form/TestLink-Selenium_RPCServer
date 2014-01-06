package plugins;

import java.util.HashMap;

import exceptions.TestExecutorException;



/**
 * Represents a plugin.
 * @author Adrien Droguet
 * @version 1.0	18/04/2012<br>
 * 				Interface created.<br>
 * @version 1.1	24/04/2012<br>
 * 				Various getters added to the interface.<br>
 */
public interface Plugin {

	/**
	 * The name has to be set when the plugin object is created.
	 * @return The plugin's name.
	 */
	public String getName();
	
	/**
	 * Which file extension does this plugin cover. If no extension is defined,
	 * this method should return an empty String.
	 * @return This specific language's File extension (.py, .c, etc.).
	 */
	public String getFileExtension();
	/**
	 * Each script must follow a regular expression in order for the language to
	 * be identified.
	 * @return A regular expression.
	 */
	public String getScriptRegex();
	
	/**
	 * Runs a test from the server.
	 * @return The test results in a HashMap object, ready to be sent back.
	 * @throws TestExecutorException If an error occurred when running the test.
	 */
	public HashMap<Object, Object> runTest()
			throws TestExecutorException;
	
	/**
	 * Compiles and runs the provided test script.
	 * @return The test results in a HashMap object, ready to be sent back.
	 * @throws TestExecutorException If an error occurred when running the test.
	 */
	public HashMap<Object, Object> runTest(String script)
			throws TestExecutorException;
	
	public String getCompileCmdLine();
	public String getRunTestCmdLine();
	public String getRunTestScriptCmdLine();

	/**
	 * Each plugin should have a version number to identify itself.
	 * @return The plugins' version.
	 */
	public String getVersion();
	
	/**
	 * Should be used before running a test script.
	 * @param script Script to be submitted to the regular expression.
	 * @return True if the script matches the script regex.
	 */
	public boolean isScriptValid(String script);
}
