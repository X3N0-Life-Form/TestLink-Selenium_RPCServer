package utils;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

/**
 * 
 * @author Adrien Droguet
 *
 */
public class MyStringUtilsTest {

	/**
	 * All lines should be present in the list.
	 */
	@Test
	public void testGetAllLines() {
		String DA_FRAKKING_STRING_OF_DOOM = "da\nfrakking\nstring\nof\ndoom";
		List<String> list = MyStringUtils.getAllLines(DA_FRAKKING_STRING_OF_DOOM);
		//System.out.println(list);
		assertEquals("da", list.get(0));
		assertEquals("frakking", list.get(1));
		assertEquals("string", list.get(2));
		assertEquals("of", list.get(3));
		assertEquals("doom", list.get(4));
	}
	
	/**
	 * Any inline comment must be gone.
	 */
	@Test
	public void testRemoveComments_inLine() {
		String line = "truc //comments";
		String res = MyStringUtils.removeComments(line);
		assertEquals("truc ", res);
	}
	
	/**
	 * Everything after the beginning of the block must be gone.
	 */
	@Test
	public void testRemoveComments_blockBegins() {
		String line = "truc /*comments";
		String res = MyStringUtils.removeComments(line);
		assertEquals("truc ", res);
	}
	
	/**
	 * Every before the end of the block must be gone.
	 */
	@Test
	public void testRemoveComments_blockEnds() {
		String line = "truc */comments";
		String res = MyStringUtils.removeComments(line);
		assertEquals("comments", res);
	}
	
	/**
	 * Don't change anything that's between quotes.
	 */
	@Test
	public void testRemoveComments_betweenQuotes() {
		String line = "truc \"//comments\"";
		String res = MyStringUtils.removeComments(line);
		assertEquals("truc \"//comments\"", res);
	}
	
	/**
	 * double every back slash.
	 */
	@Test
	public void testEscapeBackSlashes() {
		String s = "\\\\\\";
		assertEquals("\\\\\\\\\\\\", MyStringUtils.escapeBackSlashes(s));
	}
	
	/**
	 * 
	 */
	@Test
	public void testGetValueOf_exists() {
		String line = "$truc=machin";
		assertEquals("machin",MyStringUtils.getValueOf(line,"truc"));
	}
	
	/**
	 * Value does not exist = return null.
	 */
	@Test
	public void testGetValueOf_notExists() {
		String line = "$trog=machin";
		assertEquals(null,MyStringUtils.getValueOf(line,"truc"));
	}
	
	/**
	 * Empty value returns empty String.
	 */
	@Test
	public void testGetValueOf_empty() {
		String line = "$truc=";
		assertEquals("",MyStringUtils.getValueOf(line,"truc"));
	}
	
	/**
	 * Stuff between quotes is counted as a String, whitespace included.
	 */
	@Test
	public void testGetValueOf_String() {
		String line = "$truc=\"stuff between quotes\"";
		assertEquals("stuff between quotes",
				MyStringUtils.getValueOf(line,"truc"));
	}
}
