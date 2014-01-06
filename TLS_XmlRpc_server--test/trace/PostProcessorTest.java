package trace;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Test;

import exceptions.TraceException;

public class PostProcessorTest {

	/**
	 * Testing the notes.
	 * @throws TraceException 
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testPostProcess_notes() throws TraceException {
		HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
		CheckPointRecorder cr = new CheckPointRecorder("key");
		cr.addToLog("something");
		cr.checkOK();
		
		hashMap.put("notes", cr.getLog());
		
		PostProcessor.postProcess("key", hashMap);
		String notes = (String) hashMap.get("notes");

		assertTrue(notes.contains("Log recording begins at "));
		assertTrue(notes.contains(" | something - OK"));
	}
	
	/**
	 * Testing the log.
	 * @throws TraceException 
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testPostProcess_log() throws TraceException {
		String log = "";
		HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
		CheckPointRecorder cr = new CheckPointRecorder("key");
		cr.addToLog("something");
		cr.checkOK();
		
		hashMap.put("notes", cr.getLog());
		
		log = PostProcessor.postProcess("key", hashMap);
		
		assertTrue(log.contains("+++++ BEGIN TEST NOTES PROCESSING +++++"));
		assertTrue(log.contains("Retrieving key CheckPointRecorder"));
		assertTrue(log.contains("Inserting Checkpoint log"));
		assertTrue(log.contains("+++++ END TEST NOTES PROCESSING +++++"));
	}
	
}
