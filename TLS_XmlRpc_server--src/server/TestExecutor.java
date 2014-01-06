package server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import com.mysql.jdbc.ResultSetImpl;

import core.EnvInfo;

import trace.PostProcessor;
import trace.PreProcessor;
import utils.MyFileUtils;
import utils.Now;
import utils.Regexes;
import app.DirGeneratorInterface;
import exceptions.TestExecutorException;
import exceptions.TraceException;

/**
 * Runs a test case in JUnit & prepares a report that will be sent back to
 * TestLink.<br>
 * NOTE: This class was written based on the assumption that a SINGLE test case
 * would be executed at a time. There is no guarantee so far as to what would
 * happen if a test suite was executed.<br>
 * @author Adrien Droguet
 * @version 1.0 15/03/2012 <br>
 * 				The class only supports single test cases.<br>
 * @version 1.1	20/03/2012 <br>
 * 				Added support for packaged tests.<br>
 * 				Changed the setup..() methods' visibility to public.<br>
 * @version	2.0	26/03/2012 <br>
 * 				Added support to allow HashMap type arguments to be passed to
 * 				runTest(). Method RunTest(String) deprecated.<br>
 * @version 2.1	27/03/2012 <br>
 * 				WARNING: Added a dependency to DirGenerator.<br>
 * 				TestExecutor is now able to receive a .java file compile it, run
 * 				it and retrieve execution results.<br>
 * 				28/03/2012<br>
 * 				Refactoring: the new runTest() method, in order to differentiate
 * 				it more clearly from its deprecated twin, has been renamed
 * 				runTestHashMap().<br>
 * 				The method runTestHashMap() is now runnable by the xml-rpc
 * 				server.<br>
 * 				Added a test script validation feature.<br>
 * @version 2.2	3/04/2012<br>
 * 				Scripts are now validated through regular expressions, and, if
 * 				sent as a custom field, are no longer required to have the same
 * 				class name as their test case name.<br>
 * 				The class now only contains static elements.<br>
 * @version	2.3	5/04/2012<br>
 * 				Added reset() method and default attribute values.<br>
 * 				Refactored out all setup methods and context attributes to new
 * 				class ExecutorSetup.<br>
 * 				6/04/2012<br>
 * 				Improved support for selenium server.<br>
 * @version 3.0	19/06/2012<br>
 * 				Added support for data set file handling.<br>
 * @version 3.1 25/06/2012<br>
 * 				Now implements runnable.<br>
 * @see TLSJavaServer
 */
public class TestExecutor implements Runnable{
	
	private static final String TC_LIST = "tc_list";

	private static final String TLS_SERVER_PASSWORD = "U|_t1m4t3_password_of_doom!";

	private static final String TLS_SERVER_USER = "TLS_Server";

	private static HashMap<String, Object> threadHashMap = null;
	
	public static final String DATA_SET_FILE_NAME = "jeu_de_donnees.txt";
	//
	// Attributes
	//
	/**
	 * Name of the xml field that contains the test case's script.
	 */
	private static final String TEST_SCRIPT = "testScript";
	/**
	 * Name of the xml field that contains the test case's name.
	 */
	private static final String TEST_CASE_NAME = "testCaseName";
	/**
	 * Name of the "target" in the ant build.xml file.
	 */
	public static final String ANT_TEST_REPORT_TARGET_NAME = "test+report";
	
	/**
	 * This is how the execution type is identified in the testlink database.
	 */
	private static final String TESTCASE_EXECUTION_TYPE_AUTO = "2";
	
	//
	// Methods
	//
	
	/**
	 * Runs the specified test and return the results.<br>
	 * Note: the static attribute URLTestcaseFolder must be set correctly before
	 * using this method.<br>
	 * @param testFolder The name of the Ant folder containing the test case.
	 * @return A HashMap containing the test results.
	 * @throws IOException
	 * @throws TestExecutorException 
	 * @deprecated Use the newer runTestHashMap() instead.
	 * @see #runTestHashMap(HashMap)
	 */
	public static HashMap<Object, Object> runTest(String testFolder)
			throws IOException, TestExecutorException {
		System.out.println("Recieved test request for: " + testFolder);
		System.out.println("  Running test from server.");
		return testRunRoutine(testFolder);
	}
	
	/**
	 * The actual method that runs a test. Must only be called when one is
	 * certain that the test folder actually exists.
	 * @param testFolder
	 * @return
	 * @throws IOException
	 * @throws TestExecutorException
	 */
	private static HashMap<Object, Object> testRunRoutine(String testFolder)
			throws IOException, TestExecutorException {
		//
		// setting up stuff
		//
		ExecutorSetup.setTestFolder(testFolder);
		ExecutorSetup.setupFromBuildXML();
		ExecutorSetup.setupReportURL();
		
		// not always needed, but just in case
		// note: removing it would remove the need for a "throws" statement (and
		// possibly improve performance a bit).
		//SeleniumServerRunner ssr = new SeleniumServerRunner();
		//ssr.run();
		
		//
		// run the ant project containing the test
		//
		// -------------------------------------------
		File file = new File(ExecutorSetup.getURLMainTestFolder()
				+ ExecutorSetup.getTestFolder());
		File buildFile = new File(file.getAbsolutePath() + "/build.xml");
		Project p = setupAntProject(file, buildFile);
		ProjectHelper.configureProject(p, buildFile);
		//disabled until it can work properly
		//System.out.println(MyFileUtils
		//		.makeFileAndClassNamesMatch(file.getAbsolutePath() + "/src/"));
		System.out.println("Running test " + ExecutorSetup.getTestName()
				+ " now...");
		// * Note to future devs: eclipse's JAVA_HOME is stronger than the
		// system's variable, check eclipse's preferences regarding the
		// installed JREs
		// * Note aux futures devs: le JAVA_HOME de eclipse a plus de poid que
		// la variable système, pensez à vérifier les JRE et JDK qu'eclipse
		// utilise
		// other notes: added tools.jar to the referenced libraries
		p.executeTarget(ANT_TEST_REPORT_TARGET_NAME);//EXECUTION IS HERE
		System.out.println("Retour de l'exécution:");
		System.out.println("Test run complete.");
		// -------------------------------------------
		
		//
		// retrieve the test results
		//
		System.out.println("Retrieving results.");
		HashMap<Object, Object> res = ResultRetriever.retrieveResults();
		System.out.println("Results retrieved. Sending backs the results");
		System.out.println("Execution ended at " + Now.get());
		System.out.println("##### END TEST EXECUTION #####");
		return res;
	}
	
	/**
	 * Sets up the Ant project.
	 * @param file
	 * @param buildFile
	 * @return An Ant project.
	 * @throws TestExecutorException
	 */
	private static Project setupAntProject(File file, File buildFile)
			throws TestExecutorException {
		Project p = new Project();
		if (!buildFile.exists())
			throw new TestExecutorException("File build.xml does not exist.");
		p.setUserProperty("ant.file", buildFile.getAbsolutePath());
		p.setBaseDir(file);
		p.init();
		return p;
	}
	
	/**
	 * Same basic usage runTest(String), but is meant to accepts a wider array
	 * of functionalities, and be easier to maintain.<br>
	 * From a developper's standpoint, it functions as some sort of hub method.
	 * @param args
	 * @return The results' HashMap.
	 * @throws TestExecutorException Usually means that something was wrong with
	 * the arguments received.
	 * @throws IOException 
	 */
	public HashMap<Object,Object> runTestHashMap(
			HashMap<Object, Object> args)
					throws TestExecutorException, IOException {
		System.out.println("##### BEGIN TEST EXECUTION #####");
		System.out.println("Request received at " + Now.get());
		if (args.get(TC_LIST) != null) {
			Thread thread = new Thread(this);
			threadHashMap = new HashMap<String, Object>();
			threadHashMap.put("args", args);
			//System.out.println("tc_list:" + args.get(TC_LIST));
			threadHashMap.put(TC_LIST, args.get(TC_LIST));
			thread.start();
			
			HashMap<Object, Object> dummy = new HashMap<Object, Object>();
			dummy.put("result", "");
			return dummy;
		} else if (!args.containsKey(TEST_CASE_NAME)) {
			throw new TestExecutorException("No testCaseName was given.");
		} else if (args.containsKey(TEST_SCRIPT)) {
			String testName = (String) args.get(TEST_CASE_NAME);
			String script = "";
			try {
				script = (String) args.get(TEST_SCRIPT);
			} catch (ClassCastException e) {
				@SuppressWarnings("unchecked")
				HashMap<Object, Object> temp =
						(HashMap<Object, Object>) args.get(TEST_SCRIPT);
				script = (String) temp.get("value");
			}
			
			String dataSet = null;
			if (args.containsKey("data_set"))
				dataSet = (String) args.get("data_set");
			
			Thread thread = new Thread(this);
			threadHashMap = new HashMap<String, Object>();
			threadHashMap.put("testName", testName);
			threadHashMap.put("script", script);
			threadHashMap.put("dataSet", dataSet);
			threadHashMap.put("args", args);
			thread.start();
			
			HashMap<Object, Object> dummy = new HashMap<Object, Object>();
			dummy.put("result", "");
			return dummy;
			//return runScript(testName, script, dataSet);
		} else {
			return runTest((String) args.get(TEST_CASE_NAME));
		}
	}
	
	/**
	 * Only supports .java test script files.<br>
	 * Creates the .java file, creates its directory structure using 
	 * DirGenerator, then proceed as usual.
	 * @param testScript
	 * @throws TestExecutorException 
	 * @throws IOException 
	 */
	public static HashMap<Object, Object> runScript(
			String testName, String testScript, String dataSet)
			throws IOException, TestExecutorException {
		System.out.println("Received test script for: " + testName);
		//
		// check script validity 
		//
		System.out.println("Received script =\n" + testScript);
		if (!isScriptValid(testName, testScript))
			throw new TestExecutorException("The provided script is invalid." +
					"The script must match this regex:\n"
					+ Regexes.CLASS_DEFINITION_REGEX);
		//
		// script pre-processing
		//
		String reportURL = ExecutorSetup.getURLMainTestFolder()	+ testName;
		File reportFolder = new File(reportURL + "/" + EnvInfo.getReport() + "/");
		if (reportFolder.exists())
			EnvInfo.setReport(EnvInfo.getReport() + "_" + threadHashMap.get("platformID"));
		ExecutorSetup.setReportURL(reportURL + "/" + EnvInfo.getReport() + "/");
		ExecutorSetup.setTestFolder(ExecutorSetup.getURLMainTestFolder()
				+ testName);
		PreProcessor pp = new PreProcessor();
		testScript = pp.preProcessScript(testScript, testName);
		
		//
		// temporary test file
		//
		File testFile = new File(ExecutorSetup.getURLMainTestFolder()
				+ testName + ".java");
		testFile.createNewFile();
		BufferedWriter bw = MyFileUtils.getBufferedWriter(testFile);
		bw.write(testScript);
		bw.close();
		
		//
		// data set file
		//
		File dataSetFile = null;
		if (dataSet != null) {
			String testFolder = ExecutorSetup.getURLMainTestFolder()
					+ ExecutorSetup.getTestFolder();
			File testFolderFile = new File(testFolder); 
			if (!testFolderFile.exists())
				testFolderFile.mkdir();
			dataSetFile = new File(testFolder + DATA_SET_FILE_NAME);
			dataSetFile.createNewFile();
			bw = MyFileUtils.getBufferedWriter(dataSetFile);
			bw.write(dataSet);
			bw.close();
		}
		
		//
		// run DirGenerator
		//
		DirGeneratorInterface.setTestFileName(testFile.getName());
		DirGeneratorInterface.setURLMainTestFolder(
				ExecutorSetup.getURLMainTestFolder());
		DirGeneratorInterface.setLibURL(ExecutorSetup.getURLLibFolder());
		DirGeneratorInterface.runDirGenerator();
		
		//
		//check directory
		//
		File tempDir = new File(ExecutorSetup.getURLMainTestFolder() + testName
				+ "/");
		if (!tempDir.exists())
			throw new TestExecutorException("The temporary directory was not" +
					" properly created." +
					"\nDirectory URL = " + tempDir.getAbsolutePath());
		
		//
		// run the test
		//
		HashMap<Object, Object> res = testRunRoutine(testName);
		
		//
		// notes post-processing
		//
		try {
			//worst case scenario: we retrieve the default JUnit report.
			System.out.println(PostProcessor.postProcess(testName, res));
		} catch (TraceException e) {
			System.out.println("An error occurred when retrieving the " +
					"checkpoint log:");
			System.out.println(e.getMessage());
		}
		
		//
		// clean up
		//
		MyFileUtils.cleanUpTemp(tempDir);
		testFile.delete();
		if (dataSetFile != null)
			dataSetFile.delete();
		return res;
	}
	
	
	
	/**
	 * Verifies that a testScript is coherently formed.<br>
	 * Note that this was designed for java classes.<br>
	 * @param testName
	 * @param testScript
	 * @return true if the script is considered valid.
	 */
	public static boolean isScriptValid(String testName, String testScript) {
		if (testName != null && !testName.equals("")
				&& testScript != null && !testScript.equals("")) {
			if (!testScript.contains("public class " + testName)) {
				System.out.println("Warning: the script's name and the test's" +
						" name do not match.\nChanging test name to match " +
						"class name.");
				try {
					MyFileUtils.switchToClassName(testScript);
				} catch (TestExecutorException e) {
					System.out.println("No class name found.");
					return false;
				}
			}
			return (testScript.matches(Regexes.CLASS_DEFINITION_REGEX));
		} else {
			return false;
		}
	}

	
	@Override
	public void run() {
		String testName = (String) threadHashMap.get("testName");
		String script = (String) threadHashMap.get("script");
		String dataSet = (String) threadHashMap.get("dataSet");
		@SuppressWarnings("unchecked")
		HashMap<Object, Object> args = (HashMap<Object, Object>) threadHashMap.get("args");
		HashMap<Object, Object> res = null;
		
		if (threadHashMap.get(TC_LIST) == null) {
			run_testRun(testName, script, dataSet, args, res);
		} else {
			@SuppressWarnings("unchecked")
			HashMap<Object, Object> tcList = 
					(HashMap<Object, Object>) threadHashMap.get(TC_LIST);
			//System.out.println(tcList);
			Collection<Object> values = tcList.values();
			//TODO: amélioration possible: récupérer tout d'un bloc
			for (Object currentId : values) {
				System.out.println("Current version id:" + currentId);
				
				try {
					Statement statement = getDBStatement();
					
					//
					// currentTestName
					//
					String selectRequest = "SELECT parent_id FROM nodes_hierarchy WHERE id = " + currentId;
					String parentId = dBGetSingleResult(statement, selectRequest);
					
					selectRequest = "SELECT name FROM nodes_hierarchy WHERE id = " + parentId;
					String currentTestName = dBGetSingleResult(statement, selectRequest);
					System.out.println("Current test name: " + currentTestName);
					
					//
					// currentScript
					//
					selectRequest = "SELECT id FROM custom_fields WHERE name = \'RE-XMLRPC_script\'";
					String scriptCFieldId = dBGetSingleResult(statement, selectRequest);
					
					selectRequest = "SELECT value FROM cfield_design_values WHERE node_id = " + currentId + " AND field_id = " + scriptCFieldId;
					String currentScript = dBGetSingleResult(statement, selectRequest);
					//System.out.println("Current test name: " + currentScript);
					
					//
					//currentDataSet
					//
					String currentDataSet = null;
					if (args.get("attached_files") != null) {
						@SuppressWarnings("unchecked")
						HashMap<Object, Object> attachedFiles = (HashMap<Object, Object>) args.get("attached_files");
						System.out.println("Current test data set:\n" + attachedFiles.get(currentId));
						currentDataSet = (String) attachedFiles.get(currentId);
					}
					
					//
					// args
					//
					HashMap<Object, Object> currentArgs = new HashMap<Object, Object>();
					currentArgs.put("testPlanID", args.get("testPlanID"));
					currentArgs.put("platformID", args.get("platformID"));
					currentArgs.put("buildID", args.get("buildID"));
					currentArgs.put("userID", args.get("userID"));
					currentArgs.put("testCaseVersionID", currentId);
					
					run_testRun(currentTestName,
							currentScript,
							currentDataSet,
							currentArgs,
							res);
				} catch (ClassNotFoundException | SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		 
	}

	private String dBGetSingleResult(Statement statement, String selectRequest)
			throws SQLException {
		ResultSet resultSet = statement.executeQuery(selectRequest);
		resultSet.absolute(1);
		try {
			String res = resultSet.getString(1);
			return res;
		} catch (SQLException e) {
			return null;
		}
	}

	private void run_testRun(String testName, String script, String dataSet,
			HashMap<Object, Object> args, HashMap<Object, Object> res) {
		try {
			res = runScript(testName, script, dataSet);
		} catch (IOException | TestExecutorException e) {
			e.printStackTrace();
		}
		
		try {
			Statement statement = getDBStatement();
			
			String insertRequest = "INSERT INTO executions "  +  
				" (testplan_id,platform_id,build_id,tester_id,execution_type,"  +
				"  tcversion_id,execution_ts,status,notes) "  +
				" VALUES (" + args.get("testPlanID") + ", " + 
				"		  " + args.get("platformID") + ", " +
				"		  " + args.get("buildID") + "," +
				" " + args.get("userID") + "," +
				TESTCASE_EXECUTION_TYPE_AUTO + "," + args.get("testCaseVersionID") + "," +
				"'" + Now.getTimeStamp() + "'" +
				", " + "'" + res.get("result") + "'" + ", " + "\"" + res.get("notes") +"\" )";
			
			System.out.println(insertRequest);
			int ret = statement.executeUpdate(insertRequest);
			System.out.println("Insert result: " + ret);
			
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private Statement getDBStatement() throws ClassNotFoundException,
			SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn =DriverManager.getConnection(
				"jdbc:mysql://192.168.10.178/testlink",
				TLS_SERVER_USER,TLS_SERVER_PASSWORD);
		Statement statement = conn.createStatement();
		return statement;
	}
}
