package trace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import exceptions.TraceException;

/**
 * 
 * @author Adrien Droguet
 *
 */
@SuppressWarnings("deprecation")
public class CheckpointRecorderTest {
	
	private static final String BLOB = "blob";
	private static final String ZORG = "zorg";
	private static final String SERIALIZED_OBJECT_URL = "test_misc/";
	private CheckPointRecorder cr = null;
	
	@Before
	public void setup() {
		cr = new CheckPointRecorder("key");
		CheckPointRecorder.setCheckPointRecordTxt(SERIALIZED_OBJECT_URL);
	}
	
	@After
	public void tearDown() {
		CheckPointRecorder.removeRecorder("key");
		File file = new File(SERIALIZED_OBJECT_URL);
		file.delete();
		file = new File(ZORG);
		file.delete();
		file = new File(BLOB);
		file.delete();
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		File file = new File(SERIALIZED_OBJECT_URL);
		file.deleteOnExit();
		file = new File(ZORG);
		file.deleteOnExit();
		file = new File(BLOB);
		file.deleteOnExit();
	}
	
	/**
	 * 
	 */
	@Test
	public void testGetLog() {
		//System.out.println(cr.getLog());
		assertTrue(cr.getLog().matches("Log recording begins at .+\\s*"));
	}
	
	/**
	 * OK = log ends with the new addition on the last line.
	 * Not checked: a time stamp is present at the beginning of the line.
	 */
	@Test
	public void testAddToLog() {
		String addition = "this is an addition";
		cr.addToLog(addition);
		//System.out.println(cr.getLog());
		assertTrue(cr.getLog().endsWith(addition));
	}
	
	/**
	 * OK = " - OK" is added at the end of the current line.
	 */
	@Test
	public void testCheckOK() {
		String addition = "this is an addition";
		cr.addToLog(addition);
		cr.checkOK();
		assertTrue(cr.getLog().endsWith(" - OK"));
	}
	
	/**
	 * Writes an end message on the log with a time stamp.
	 */
	@Test
	public void testEndLog() {
		cr.endLog();
		//System.out.println(cr.getLog());
		assertTrue(cr.getLog().matches(".+\\s*Log ends at .+."));
	}
	
	/**
	 * OK = ANY newly created recorder is added to the list.
	 * @see #testRemoveRecorder()
	 */
	@Test
	public void testGetAllRecorders() {
		new CheckPointRecorder(ZORG);
		new CheckPointRecorder(BLOB);
		HashMap<String, CheckPointRecorder> everything =
				CheckPointRecorder.getAllRecorders();
		
		assertTrue(everything.containsKey("key"));
		assertTrue(everything.containsKey(ZORG));
		assertTrue(everything.containsKey(BLOB));
		
		//teardown
		CheckPointRecorder.removeRecorder(ZORG);
		CheckPointRecorder.removeRecorder(BLOB);
	}
	/**
	 * @see #testGetAllRecorders()
	 */
	@Test
	public void testRemoveRecorder() {
		new CheckPointRecorder(ZORG);
		new CheckPointRecorder(BLOB);
		HashMap<String, CheckPointRecorder> everything =
				CheckPointRecorder.getAllRecorders();
		CheckPointRecorder.removeRecorder(ZORG);
		CheckPointRecorder.removeRecorder(BLOB);
		
		assertFalse(everything.containsKey(ZORG));
		assertFalse(everything.containsKey(BLOB));
	}
	
	/**
	 * When the requested recorders are already present in the record list.
	 * @throws TraceException 
	 */
	@Test
	public void testGetRecorder_normal() throws TraceException {
		CheckPointRecorder zorg = new CheckPointRecorder(ZORG);
		CheckPointRecorder blob = new CheckPointRecorder(BLOB);
		
		assertEquals(zorg, CheckPointRecorder.getRecorder(ZORG));
		assertEquals(blob, CheckPointRecorder.getRecorder(BLOB));
		
		//teardown
		CheckPointRecorder.removeRecorder(ZORG);
		CheckPointRecorder.removeRecorder(BLOB);
	}
	
	/**
	 * Verify that the recorders are serialized when calling endLog().
	 */
	@Test
	public void testEndLog_serialization() {
		CheckPointRecorder zorg = new CheckPointRecorder(ZORG);
		CheckPointRecorder blob = new CheckPointRecorder(BLOB);
		zorg.endLog();
		blob.endLog();
		
		assertTrue(new File(
						CheckPointRecorder.getCheckPointRecordTxt()
						+ zorg.getClassName()
						).exists()
				);
		assertTrue(new File(
				CheckPointRecorder.getCheckPointRecordTxt()
				+ zorg.getClassName()
				).exists());
		
		//teardown
		CheckPointRecorder.removeRecorder(ZORG);
		CheckPointRecorder.removeRecorder(BLOB);
	}
	
	/**
	 * When the requested recorders are not present in the record list, but on
	 * the file system.
	 * @throws TraceException 
	 */
	@Test
	public void testGetRecorder_deserialize() throws TraceException {
		CheckPointRecorder zorg = new CheckPointRecorder(ZORG);
		CheckPointRecorder blob = new CheckPointRecorder(BLOB);
		zorg.endLog();
		blob.endLog();
		CheckPointRecorder.removeRecorder(ZORG);
		CheckPointRecorder.removeRecorder(BLOB);
		
		assertEquals(zorg, CheckPointRecorder.getRecorder(ZORG));
		assertEquals(blob, CheckPointRecorder.getRecorder(BLOB));
		
		//teardown
		CheckPointRecorder.removeRecorder(ZORG);
		CheckPointRecorder.removeRecorder(BLOB);
	}
}
