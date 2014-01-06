package utils;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

public class PluginUtilsTest {
	
	/**
	 * Should replace '#name#' by the Plugin's name. 
	 */
	@Ignore
	@Test
	public void testFormatLine() {
		String line = "vlix #name# hjvo";
		String res = PluginUtils.formatLine(line, "test");
		assertEquals("vlix test hjvo",res);
	}
	//next test: more than 1 #name#
}
