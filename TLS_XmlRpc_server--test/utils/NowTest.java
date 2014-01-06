package utils;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Test;

/**
 * Tests NOW. 
 * @author Adrien Droguet
 *
 */
public class NowTest {

	private static final String regex = "[\\d]{1,2}/" +	//"dd/"
			"[\\d]{1,2}/" +								//"mm/"
			"\\d\\d\\d\\d - " +							//"yyyy - "
			"[\\d]{1,2}:" +								//"hh:"
			"\\d\\d:" +									//"minmin:"
			"\\d\\d - " +								//"ss - "
			"[\\d]+ ms";								//"ms ms"
	
	/**
	 * Regex-checks the structure of the returned String.
	 */
	@Test
	public void testGet_structure() {
		Calendar c = Calendar.getInstance();
		c.set(2012, 4, 10, 9, 10, 15);
		assertTrue(Now.get().matches(regex));
	}
	
	/**
	 * Verify that seconds < 10 are written correctly.
	 */
	@Test
	public void testGet_seconds() {
		Calendar c = Calendar.getInstance();
		c.set(2012, 4, 10, 9, 10, 5);
		assertTrue(Now.get().matches(regex));
	}
	
	/**
	 * Idem, but with minutes instead.
	 */
	@Test
	public void testGet_minutes() {
		Calendar c = Calendar.getInstance();
		c.set(2012, 4, 10, 9, 1, 15);
		assertTrue(Now.get().matches(regex));
	}
}
