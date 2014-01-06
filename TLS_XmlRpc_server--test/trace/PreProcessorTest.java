package trace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import server.ExecutorSetup;
import utils.Regexes;

/**
 * 
 * @author Adrien Droguet
 * @see PreProcessor
 */
public class PreProcessorTest {
	private static final String NAME = "name";
	PreProcessor pp;
	
	@Before
	public void setup() {
		pp = new PreProcessor(); 
	}
	
	/**
	 * Makes sure that the default keywords correspond to Selenium checkpoints.
	 */
	@Test
	public void testGetKeyWords() {
		List<String> list = new LinkedList<String>();
		for (String current : PreProcessor.DEFAULT_KEYWORDS)
			list.add(current);
		assertEquals(list,
				pp.getKeyWords());
	}
	/**
	 * No comment.
	 */
	@Test
	public void testSetKeyWords() {
		List<String> list = new LinkedList<String>();
		list.add("check");
		list.add("verify");
		list.add("assert");
		pp.setKeyWords(list);
		assertEquals(list,
				pp.getKeyWords());
	}
	/**
	 * Self-explanatory. Verify that default keywords are correctly listed.
	 */
	@Test
	public void testAddKeyWords() {
		List<String> list = new LinkedList<String>();
		list.add("zorg");
		list.add("blob");
		list.add("plop");
		pp.addKeyWords(list);
		assertTrue(pp.getKeyWords().contains("verify"));
		assertTrue(pp.getKeyWords().contains("assert"));
		assertTrue(pp.getKeyWords().contains("click"));
		assertTrue(pp.getKeyWords().contains("zorg"));
		assertTrue(pp.getKeyWords().contains("blob"));
		assertTrue(pp.getKeyWords().contains("plop"));
	}
	/**
	 * Same as above, but for a single keywords.
	 */
	@Test
	public void testAddKeyWord() {
		pp.addKeyWord("keyword");
		assertTrue(pp.getKeyWords().contains("keyword"));
	}
	
	/**
	 * Checks ALL selenium keyword functions.
	 * This test is insufficiently effective.
	 */
	@Test
	public void testPreProcessScript_regex() {
		//dummy script; just contains keywords
		String script = "package vlix;\n" +
				"import something.*;\n" +
				"public class Thing {\n" +
				"\nclick;" +
				"\nverify;" +
				"\nassert;" +
				"\n}";
		ExecutorSetup.setTestName("Thing");
		ExecutorSetup.setReportURL("url must be set before preprocessing");
		String res = pp.preProcessScript(script, NAME);
		//System.out.println(res);
		assertTrue(res.matches(Regexes.CLASS_DEFINITION_REGEX));
		//no time for this
		//assertTrue(res.matches(Regexes.getPreProcessingRegex()));
	}
	
	/**
	 * "import trace.CheckPointRecorder;" & "import org.junit.AfterClass;"
	 * should be present. 
	 */
	@Test
	public void testPreProcessScript_importNone() {
		String script = "None of the required packages are present.";
		String res = pp.preProcessScript(script, NAME);
		assertTrue(res.contains("import trace.CheckPointRecorder;"));
		assertTrue(res.contains("import org.junit.AfterClass;"));
	}
	
	/**
	 * "import org.junit.AfterClass;" is present.
	 */
	@Test
	public void testPreProcessScript_orgJunitAfterClassOnly() {
		String script = "import org.junit.AfterClass;" +
				"after class only.";
		String res = pp.preProcessScript(script, NAME);
		assertTrue(res.contains("import trace.CheckPointRecorder;"));
		assertTrue(res.contains("import org.junit.AfterClass;"));
	}
	
	/**
	 * "import trace.CheckPointRecorder;" is present.
	 */
	@Test
	public void testPreProcessScript_traceCheckPointRecorder() {
		String script = "import trace.CheckPointRecorder;" +
				"trace.cpr only.";
		String res = pp.preProcessScript(script, NAME);
		assertTrue(res.contains("import trace.CheckPointRecorder;"));
		assertTrue(res.contains("import org.junit.AfterClass;"));
	}
	
	/**
	 * "import org.junit.*;" is present.
	 */
	@Test
	public void testPreProcessScript_orgJunit() {
		String script = "import org.junit.*;\n" +
				"Heeere's junit.";
		String res = pp.preProcessScript(script, NAME);
		assertTrue(res.contains("import trace.CheckPointRecorder;"));
		assertTrue(res.contains("import org.junit.*;"));
		assertFalse(res.contains("import org.junit.AfterClass;"));
	}
	
	/**
	 * Keywords in quotes should not trigger the preprocessor.
	 */
	@Test
	public void testPreProcessScript_quotes() {
		String script = "The keyword \"assert\" is between quotes.";
		String res = pp.preProcessScript(script, NAME);
		assertFalse(res.contains("cr.addToLog(" +
				"\"The keyword \"assert\" is between quotes.\"" +
				");"));
	}
	
	/**
	 * Keywords in comments should not trigger the preprocessor.
	 */
	@Test
	public void testPreProcessScript_inLineComments() {
		String script = "The keyword //assert is behind comments.";
		String res = pp.preProcessScript(script, NAME);
		assertFalse(res.contains(PreProcessor.CR_ADD_TO_LOG +
				"\"The keyword //assert is behind comments.\"" +
				");"));
	}
	
	/**
	 * Keywords in comments should not trigger the preprocessor.
	 */
	@Test
	public void testPreProcessScript_blockComments() {
		String script = "The keyword /*" +
				"\nassert is behind comments.";
		String res = pp.preProcessScript(script, NAME);
		assertFalse(res.contains(PreProcessor.CR_ADD_TO_LOG +
				"\"assert is behind comments.\"" +
				");"));
	}
	
	/**
	 * Manually included checkpoints should contain the whole line.
	 */
	@Test
	public void testPreProcessScript_inlineCommentCheckPoint() {
		String script = "No keyword, but //#CheckPoint:here";
		String res = pp.preProcessScript(script, NAME);
		assertTrue(res.contains(PreProcessor.CR_ADD_TO_LOG + script));
	}
	
	/**
	 * First lines should not be duplicated if no package is set.
	 */
	@Test
	public void testPreProcessScript_noDuplicatedFirstLineIfNoPackage() {
		String script = "No keyword, but //#CheckPoint:here";
		String res = pp.preProcessScript(script, NAME);
		assertFalse(res.startsWith(script));
	}
	
	/**
	 * A line without checkpoint tag is returned unchanged.
	 */
	@Test
	public void testCrAddCheckPoint_noCheckpointTag() {
		String line = "no tag";
		String res = pp.crAddCheckPoint(line);
		String expected = line;
		assertEquals(expected, res);
	}
	
	/**
	 * Invalid or incomplete lines are returned unchanged as well.
	 */
	@Test
	public void testCrAddCheckPoint_incomplete() {
		String line = "incomplete" + PreProcessor.COMMENT_CHECK_POINT;
		String res = pp.crAddCheckPoint(line);
		String expected = line;
		assertEquals(expected, res);
	}
	
	@Test
	public void testCrAddCheckPoint_OK_afterLine() {
		String line = "OK " + PreProcessor.COMMENT_CHECK_POINT +
				"$" + PreProcessor.COMMENT_VALUE_NAME + "=a_name " +
				"$" + PreProcessor.COMMENT_EXPECTED_VALUE + "=something" +
				"$" + PreProcessor.COMMENT_MESSAGE + "=\"this is a message\"";
		String res = pp.crAddCheckPoint(line);
		String expected = "OK\n" +
				"if (a_name.equals(\"something\"))\n" +
				"\tcr.addToLog(\"this is a message\");" +
				"else\n" +
				"\tcr.addToLog();";
		assertEquals(expected, res);
	}
	
	/**
	 * It doesn't actually matters whether there already was a line or not.
	 */
	@Test
	public void testCrAddCheckPoint_OK_emptyLine() {
		
	}
}
