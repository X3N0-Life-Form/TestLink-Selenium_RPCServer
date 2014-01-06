package server;

import static org.junit.Assert.*;
import java.io.IOException;
import java.util.HashMap;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Before;
import org.junit.Test;

import exceptions.TestExecutorException;



/**
 * Tests the results' retrieval.
 * @author Adrien Droguet
 *
 */
public class ResultRetrieverTest {
	
	private static HashMap<Object, Object> resultMap;

	@Before
	public void setUp() {
		resultMap = new HashMap<Object, Object>();
		ExecutorSetup.reset();
	}
	
	//
	// XML doc getters
	//
	public Element getPassedXml() throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(
				"test/xml_sample_files/TEST-TestSeleniumBidonWdSuccess.xml");
		return doc.getRootElement();
	}
	
	public Element getFailedXml() throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(
				"test/xml_sample_files/TEST-TestSeleniumBidonWdFailure.xml");
		return doc.getRootElement();
	}
	
	public Element getErrorXml() throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(
				"test/xml_sample_files/TEST-TestSeleniumBidonWdError.xml");
		return doc.getRootElement();
	}
	
	//
	// Tests
	//
	
	/**
	 * Note to tester: check if the date is correct on the .xml file.
	 * @throws JDOMException
	 * @throws IOException
	 */
	@Test
	public void testFetchTimestampISO() throws JDOMException, IOException {
		ResultRetriever.fetchTimestampISO(getPassedXml(), resultMap);
		assertTrue(resultMap.containsKey("timestampISO"));
		assertTrue(((String) resultMap.get("timestampISO"))
				.startsWith("2012-03-16"));
	}

	/**
	 * No comment.
	 * @throws JDOMException
	 * @throws IOException
	 */
	@Test
	public void testFetchScheduled() throws JDOMException, IOException {
		ResultRetriever.fetchScheduled(getPassedXml(), resultMap);
		assertTrue(resultMap.containsKey("scheduled"));
		assertTrue(((String) resultMap.get("scheduled")).equals("now"));
	}
	
	/**
	 * Note to tester: check what is supposed to be contained in these notes.
	 * @throws JDOMException
	 * @throws IOException
	 */
	@Test
	public void testFetchNotes_passed() throws JDOMException, IOException {
		ResultRetriever.fetchNotes(getPassedXml(), resultMap);
		assertTrue(resultMap.containsKey("notes"));
		String res = (String) resultMap.get("notes");
		assertTrue(res.contains("TestCase: TestSeleniumBidonWdSuccess"));
		assertTrue(res.contains("The test passed successfully."));
		assertTrue(res.contains("Test time: "));
	}
	
	/**
	 * Note: We are not checking what is inside the report, just that it is
	 * correctly wrapped up.
	 * @throws JDOMException
	 * @throws IOException
	 */
	@Test
	public void testFetchNotes_failed() throws JDOMException, IOException {
		ResultRetriever.fetchNotes(getFailedXml(), resultMap);
		assertTrue(resultMap.containsKey("notes"));
		String res = (String) resultMap.get("notes");
		assertTrue(res.contains("TestCase: TestSeleniumBidonWdFailure"));
		assertTrue(res.contains("The test has failed."));
		assertTrue(res.contains("Failure report:"));
		assertTrue(res.contains("Test time: "));
	}
	
	/**
	 * Idem.
	 * @throws JDOMException
	 * @throws IOException
	 */
	@Test
	public void testFetchNotes_error() throws JDOMException, IOException {
		ResultRetriever.fetchNotes(getErrorXml(), resultMap);
		assertTrue(resultMap.containsKey("notes"));
		String res = (String) resultMap.get("notes");
		assertTrue(res.contains("TestCase: TestSeleniumBidonWdError"));
		assertTrue(res.contains("The test didn't run due to an error."));
		assertTrue(res.contains("Error report:"));
		assertTrue(res.contains("Test time: "));
	}

	/**
	 * Check result thingy.
	 * @throws JDOMException
	 * @throws IOException
	 */
	@Test
	public void testFetchResult_passed() throws JDOMException, IOException {
		ResultRetriever.fetchResult(getPassedXml(), resultMap);
		assertTrue(resultMap.containsKey("result"));
		assertEquals("p", resultMap.get("result"));
	}
	
	@Test
	public void testFetchResult_failed() throws JDOMException, IOException {
		ResultRetriever.fetchResult(getFailedXml(), resultMap);
		assertTrue(resultMap.containsKey("result"));
		assertEquals("f", resultMap.get("result"));
	}
	
	@Test
	public void testFetchResult_blocked() throws JDOMException, IOException {
		ResultRetriever.fetchResult(getErrorXml(), resultMap);
		assertTrue(resultMap.containsKey("result"));
		assertEquals("b", resultMap.get("result"));
	}
	
	/**
	 * Technically, this case shouldn't occur, as no report will be created if
	 * the test didn't run.
	 */
	//@Test
	public void testFetchResult_notRun() {
		fail("Not yet implemented");
	}
	
	/**
	 * No test name (or package name) is given. This should raise an exception.
	 * @throws TestExecutorException 
	 */
	@Test (expected=TestExecutorException.class)
	public void testRetrieveResults_nothing() throws TestExecutorException {
		ExecutorSetup.setURLMainTestFolder("test");
		ExecutorSetup.setTestFolder("xml_sample_files");
		ResultRetriever.retrieveResults();
	}
	
	/**
	 * No package report URL is set. This should, in theory, never happen.
	 * @throws TestExecutorException 
	 */
	@Test (expected=TestExecutorException.class)
	public void testRetrieveResults_noReportURL() throws TestExecutorException {
		ExecutorSetup.setURLMainTestFolder("test");
		ExecutorSetup.setTestFolder("xml_sample_files");
		ExecutorSetup.setTestName("TestSeleniumBidonWdSuccess");
		ResultRetriever.retrieveResults();
	}
	
	/**
	 * No package name is given. Should run smoothly.
	 * @throws TestExecutorException 
	 */
	@Test
	public void testRetrieveResults_noPackage() throws TestExecutorException {
		ExecutorSetup.setURLMainTestFolder("test");
		ExecutorSetup.setTestFolder("xml_sample_files");
		ExecutorSetup.setTestName("TestSeleniumBidonWdSuccess");
		ExecutorSetup.setReportURL("test/xml_sample_files");
		HashMap<Object, Object> res = ResultRetriever.retrieveResults();
		assertEquals("p", res.get("result"));
	}
}
