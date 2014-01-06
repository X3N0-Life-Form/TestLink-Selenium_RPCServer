package trace;

import java.util.ArrayList;
import java.util.List;

import server.ExecutorSetup;
import server.TestExecutor;
import utils.MyStringUtils;

/**
 * This class provides tools to add checkpoints to a test's execution by looking
 * for keywords in the script's code, and adding the necessary checkpoints.<br>
 * @author Adrien Droguet
 * @version 1.0 25/04/2012<br>
 * 				Class created. Basic keyword pre-processing implemented<br>
 * @version 1.1 2/05/2012<br>
 * 				Contextual pre-processing implemented (comments and quotes are
 * 				now ignored).<br>
 * 				3/05/2012<br>
 * 				Now retrieving {@link ExecutorSetup}'s report url & including it
 * 				to the afterClass method.<br>
 * @version 1.2	PreProcessor now outputs its actions.<br>
 * 				Added tabs to some of the generated lines.<br>
 * @version 1.3	4/06/2012<br>
 * 				CheckPoints can now be added via comments.<br>
 * 				Extracted beginning and end of lines into constants.<br>
 */
public class PreProcessor {
	/**
	 * The first part of an add-to-log line. 
	 */
	public static final String CR_ADD_TO_LOG = "cr.addToLog(\"";
	/**
	 * The end of a code line.
	 */
	public static final String END_OF_LINE = "\");\n";
	/**
	 * When this is present in a commented line, a checkpoint is added.
	 */
	public static final String COMMENT_CHECK_POINT = "//#CheckPoint:";
	public static final String COMMENT_VALUE_NAME = "value-name";
	public static final String COMMENT_EXPECTED_VALUE = "expected";
	public static final String COMMENT_MESSAGE = "message";
	/**
	 * Default name of the tearDownAfterClass method.
	 */
	public static final String LOG_TEAR_DOWN_METHOD_NAME = "logTearDown";
	/**
	 * Just a safety net regarding the tearDown method's name.
	 * @param script Test script the method is to be added to.
	 * @return A valid method name.
	 */
	private String getLogTearDownMethodName(String script) {
		String res = LOG_TEAR_DOWN_METHOD_NAME;
		if (script.contains(LOG_TEAR_DOWN_METHOD_NAME))
			res += "DontTryToBeASmartAssPlease" +
					"TryingToBreakThePreProcessorIsBad";
		return res + "()";
	}
	
	/////////////////////////////
	// Keyword-related section //
	/////////////////////////////
	//TODO:read that list from some file instead || just get rid of it
	/**
	 * Lists basic Selenium checkpoints.
	 */
	public static final String[] DEFAULT_KEYWORDS = {
		"click",
		"verify",
		"assert",
		"driver.",
		"add",
		"assign",
		"capture",
		"check",
		"choose",
		"drag"
	};
	private static final String COMMENT_TYPE = "type";
	private List<String> keywords = null;
	public List<String> getKeyWords() {
		return keywords;
	}
	public void setKeyWords(List<String> list) {
		this.keywords = list;
//		for (String current : keywords) {
//			Regexes.addPreProcessingKeyword(current);
//		}
	}
	/**
	 * Resets the keywords to their default values.
	 * @return The new list of keywords.
	 */
	public List<String> resetKeyWords() {
		ArrayList<String> res = new ArrayList<String>();
		for (String current : DEFAULT_KEYWORDS) {
			res.add(current);
//			Regexes.addPreProcessingKeyword(current);
		}
		keywords = res;
		return res;
	}
	/**
	 * Adds a list of keywords.
	 * @param list
	 */
	public void addKeyWords(List<String> list) {
		keywords.addAll(list);
	}
	/**
	 * Adds a single keyword.
	 * @param keyword
	 */
	public void addKeyWord(String keyword) {
		keywords.add(keyword);
	}
	
	//
	// Regex-related section
	//
	//see utils.Regexes
	
	/////////////////////////////////////
	// Everything-else-related section //
	/////////////////////////////////////
	/**
	 * Creates a new PreProcessor instance using default keywords.
	 */
	public PreProcessor() {
		resetKeyWords();
	}
	
	
	/**
	 * Adds checkpoint-logging methods to the test script.
	 * @param script Script to pre-process.
	 * @param name Test's name.
	 * @return A modified version of the starting script.
	 */
	public String preProcessScript(String script, String name) {
		System.out.println("----- BEGIN PreProcessing -----");
		List<String> lines = MyStringUtils.getAllLines(script);
		
		//
		// imports
		//
		String res = lines.get(0);
		/* not needed anymore
		System.out.print("Setting up imports");
		String res = setupImports(script, lines);
		System.out.println(" - done");
		*/
		//
		// lines
		//
		boolean getUrl = false;
		for (String line : lines) {
			//create CheckPointRecorder attribute
			//WARNING: this line isn't guaranteed to always work properly
			//TODO: improve this - look for the 1st '{' that follows
			if (line.contains("public class " /*+ name*/)) {
				/* CheckPointRecorder is no longer in use
				System.out.print("Adding CheckPointRecorder attribute");
				
				res += line + "\n";
				//create & initialize the recorder
				line = "\tpublic static CheckPointRecorder cr = " +
						"new CheckPointRecorder(\"" + name + END_OF_LINE;
				//end the recording once everything is done
				res += getAfterClass(script);
				
				System.out.println(" - done");
				*/
			} else if (line.contains("public String getUrl()")) {
				getUrl = true;
			} else if (getUrl) {
				getUrl = false;
				String url = ExecutorSetup.getURLMainTestFolder()
						+ ExecutorSetup.getTestFolder()
						+ TestExecutor.DATA_SET_FILE_NAME;
				line = "\t\treturn \"" + MyStringUtils.escapeBackSlashes(url) + "\";";
			} else {
				res += processLineKeywords(line);
			}
			
			//not copying blank lines nor lines beginning with "package"
			if (!line.matches("\\s*")
					//&& !line.matches("\\s*package\\s+\\S+;")
					&& !line.startsWith("package"))
				res += line + "\n";
		}
		System.out.println("-----------------------------");
		System.out.println("Final script =\n" + res);
		System.out.println("----- END PreProcessing -----");
		return res;
	}
	
	/**
	 * Check if any of the keywords if present on the specified line, and deal
	 * with them appropriately.
	 * @param line
	 * @return Either an empty line, or an addToLog(String) line.
	 * @see CheckPointRecorder#addToLog(String)
	 */
	private String processLineKeywords(String line) {
		String res = "";
		line = removeComments(line);
		//if this is a blank line
		if (line.matches("\\s*"))
			return res;
		
		//if (line.contains("private static final String D_MY_LOG = \"D:\\myLog.txt\";"))
		//	res = "private static final String D_MY_LOG =" + ExecutorSetup.getURLMainTestFolder() + "myLog.txt\";";
		
		/*
		if (line.contains(COMMENT_CHECK_POINT)) {
			//res += crAddCheckPoint(line);
			res += crAddToLog(line);
		}*/
		/* partie désactivée de manière temporaire, si jamais le besoin
		 * d'identifier des mots clefs réapparait, ce référer à cette portion de
		 * code.
		for (String keyword : keywords) {
			if (line.contains(COMMENT_CHECK_POINT)) {
				res += crAddToLog(line);
				//no need to look for keywords 
				break;
			} else if (line.contains(keyword)
					//Note: a single regex would probably be more efficient
					&& !line.matches(".*\"" + keyword + "\".*")
					)
				res += crAddToLog(line);
		}
		*/
		
		return res;
	}
	
	/**
	 * Adds a checkpoint to the specified line. If no checkpoint tag is present
	 * in the line, or that the line is incomplete, the line is returned
	 * unchanged.
	 * @param line
	 * @return
	 */
	public String crAddCheckPoint(String line) {
		// TODO Auto-generated method stub
		String valueName = MyStringUtils.getValueOf(line, COMMENT_VALUE_NAME);
		String expected = MyStringUtils.getValueOf(line, COMMENT_EXPECTED_VALUE);
		String message = MyStringUtils.getValueOf(line, COMMENT_MESSAGE);
		String type = MyStringUtils.getValueOf(line, COMMENT_TYPE);
		//check that everything is set
		if (true) {
			line += "if (" + valueName + ".equals(\"" + expected + "\"))";
			line += crAddToLog("");
		}
		
		return line;
	}
	
	/**
	 * Returns the provided line with {@link #CR_ADD_TO_LOG} in front of it and
	 * {@link #END_OF_LINE}at the end of it.
	 * @param line
	 * @return
	 */
	public String crAddToLog(String line) {
		return CR_ADD_TO_LOG +
			MyStringUtils.escapeQuotes(line.trim()) + END_OF_LINE;
	}
	
	/**
	 * True when we are in a comment block. Toggled by removeComments().
	 * @see #removeComments(String)
	 */
	private boolean commentBlock = false;
	/**
	 * Removes any comment present on the specified line, or adds a checkpoint
	 * if the line contains {@link #COMMENT_CHECK_POINT}.
	 * @param line
	 * @return Comment-less line.
	 * @see MyStringUtils#removeComments(String)
	 */
	private String removeComments(String line) {
		if (line.contains(COMMENT_CHECK_POINT)) {
			return line;
		} else if (line.contains("/*")) {
			commentBlock = true;
			//remove everything after the beginning of the block
			line = MyStringUtils.removeComments(line);
		} else if (line.contains("*/")) {
			commentBlock = false;
			//remove everything before the end of the block
			line = MyStringUtils.removeComments(line);
		} else if (!commentBlock) {
			//normal case
			line = MyStringUtils.removeComments(line);
		} else {
			//we are in a comment block, don't return the line we just read
			//said comment block's current line doesn't contain #CheckPoint:
			return "";
		}
		
		return line;
	}
	
	/**
	 * Looks for any missing import and returns an appropriate header String for
	 * the specified script.
	 * @param script Test script to analyze.
	 * @param lines The list of the script's lines.
	 * @return A String containing the package's name (if it exists), followed
	 * by any required import that was not originally present in the file.
	 */
	private String setupImports(String script, List<String> lines) {
		//Note: the first line should be "package something;"
		String res = "";
		if (lines.get(0).contains("package "))
			res += lines.get(0);	
		res += "\nimport trace.CheckPointRecorder;\n";
		if (!script.contains("import org.junit.AfterClass;")
				&& !script.contains("import org.junit.*;"))
			res += "import org.junit.AfterClass;\n";

		return res;
	}
	
	/**
	 * Creates an AfterClass method that saves the CheckPointRecorderon the file
	 * system.
	 * @param script The script the method is to be included into. The method
	 * getAfterClass() does not modify the script directly, but it makes sure
	 * that there is no name collision with another method.
	 * @return The AfterClass method.
	 */
	private String getAfterClass(String script) {
		String res = "";
		res += "\t@AfterClass\n";
		res += "\tpublic static void " + getLogTearDownMethodName(script) +
				" {\n";
		String url = ExecutorSetup.getReportURL();
		url = MyStringUtils.escapeBackSlashes(url);
		res += "\t\tCheckPointRecorder.setCheckPointRecordTxt(\"" +
				url + END_OF_LINE;
		res += "\t\tcr.endLog();\n";
		res += "\t}\n";
		return res;
	}
	
	
	

	

	


	
}
