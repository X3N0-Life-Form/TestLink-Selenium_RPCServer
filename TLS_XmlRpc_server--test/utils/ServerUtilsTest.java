package utils;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

import exceptions.CfgException;

/**
 * 
 * @author Adrien Droguet
 * @see ServerUtils
 */
public class ServerUtilsTest {

	/**
	 * A valid config line. With no whitespace.
	 * @throws CfgException 
	 */
	@Test
	public void testProcessLine_OK_noSpace() throws CfgException {
		String line = "MainDirectory:test_main";
		String res = ServerUtils.processLine(line,
				CfgOptions.MAIN_DIRECTORY.length());
		assertEquals("test_main", res);
	}
	
	/**
	 * A valid config line. With tabs.
	 * @throws CfgException 
	 */
	@Test
	public void testProcessLine_OK_tab() throws CfgException {
		String line = "MainDirectory:	test_main";
		String res = ServerUtils.processLine(line,
				CfgOptions.MAIN_DIRECTORY.length());
		assertEquals("test_main", res);
	}
	
	/**
	 * A valid config line. With a space.
	 * @throws CfgException 
	 */
	@Test
	public void testProcessLine_OK_space() throws CfgException {
		String line = "MainDirectory: test_main";
		String res = ServerUtils.processLine(line,
				CfgOptions.MAIN_DIRECTORY.length());
		assertEquals("test_main", res);
	}
	
	/**
	 * A valid config line. With space after in the option
	 * @throws CfgException 
	 */
	@Test
	public void testProcessLine_OK_spaceAfter() throws CfgException {
		String line = "MainDirectory:test_main vlix";
		String res = ServerUtils.processLine(line,
				CfgOptions.MAIN_DIRECTORY.length());
		assertEquals("test_main vlix", res);
	}
	
	/**
	 * Multiple options are set on a single line.<br>
	 * Decision has been made to accept not to put effort into identifying this
	 * kind of mistake. It is up to the user to make sure its config file is
	 * written properly.
	 * @throws CfgException
	 */
	@Test
	@Ignore
	public void testProcessLine_noNewLine() throws CfgException {
		String line = "MainDirectory: test_mainLib:hjvo";
		ServerUtils.processLine(line,
				CfgOptions.MAIN_DIRECTORY.length());
	}
	
	/**
	 * No url is given.
	 * @throws CfgException
	 */
	@Test(expected=CfgException.class)
	public void testProcessLine_nothing() throws CfgException {
		String line = "MainDirectory:";
		ServerUtils.processLine(line,
				CfgOptions.MAIN_DIRECTORY.length());
	}
	
	/**
	 * OK = no exception.<br>
	 * @throws CfgException
	 */
	@Test
	public void testProcessLine_networkDrive() throws CfgException {
		String line = "MainDirectory:		" +
				"\\\\molene\\echange\\Partage\\adt\\test_main";
		ServerUtils.processLine(line,
				CfgOptions.MAIN_DIRECTORY.length());
		
	}
	
	//not tested here: startup summary, as it doesn't affect anything.
}
