package core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ArgAnalyzerTest {
	
	/**
	 * Sets BuildXMLGenerator's packageName to "" (its default value) and resets
	 * EnvInfo.
	 */
	@Before
	public void setUp() {
		EnvInfo.setPackageName("");
		EnvInfo.reset();
	}

	/**
	 * No argument given. Nothing should happen.
	 */
	@Test
	public void testAnalyzeArgs_nothing() {
		String[] args = {};
		String log = ArgAnalyzer.analyzeArgs(args);
		//nothing happens
		assertEquals("",EnvInfo.getMainDirectory());
		assertEquals("lib/",EnvInfo.getLibURL());
		assertTrue(log.endsWith(
				"No test case name specified! Aborting argument analysis."));
	}
	
	/**
	 * No test case name specified, but an option instead. Nothing happens.
	 */
	@Test
	public void testAnalyzeArgs_optionFirst() {
		String[] args = {
				"-md",
				"test_main"
		};
		String log = ArgAnalyzer.analyzeArgs(args);
		assertEquals("",EnvInfo.getMainDirectory());
		assertEquals("lib/",EnvInfo.getLibURL());
		assertTrue(log.contains("Invalid test case name."));
	}
	
	/**
	 * A test name without a .java extension. It should simply acknowledge that
	 * there is a test name in the log and change nothing else.
	 */
	@Test
	public void testAnalyzeArgs_nameOnly() {
		String[] args = {"test_case"};
		String log = ArgAnalyzer.analyzeArgs(args);
		//nothing happens
		assertEquals("", EnvInfo.getMainDirectory());
		assertEquals("lib/",EnvInfo.getLibURL());
		assertTrue(log.contains("Test name = test_case"));
	}
	
	/**
	 * Name.java. The test case name should be the file's name without the .java
	 * extension.
	 */
	@Test
	public void testAnalyzeArgs_nameJava_noFile() {
		String[] args = {"test_case.java"};
		String log = ArgAnalyzer.analyzeArgs(args);
		assertEquals("",EnvInfo.getMainDirectory());
		assertEquals("lib/",EnvInfo.getLibURL());
		assertTrue(log.contains("Test name = test_case"));
	}
	
	/**
	 * OK condition: not empty and not another option.
	 * Notes:
	 * No valid-url checking is performed, though the user will receive an
	 * exception at runtime if an invalid url is given. This is not our concern.
	 */
	@Test
	public void testAnalyzeArgs_name_md_ok() {
		String[] args = {
				"test_case",
				"-md",
				"the_main_directory"
				};
		String log = ArgAnalyzer.analyzeArgs(args);
		//don't forget that a final "/" is added by EnvInfo to the url.
		assertEquals("the_main_directory/", EnvInfo.getMainDirectory());
		assertTrue(log.contains("Main directory = the_main_directory"));
	}
	
	/**
	 * If -md is followed by an option (=something that begins with '-'), it
	 * won't modify EnvInfo's mainDirectory (i.e. it will use its default dir).
	 * Fun fact: right now, it is legal to write any option any number of time
	 * you want, the last option to be written will be the valid one.
	 */
	@Test
	public void testAnalyzeArgs_name_md_followedByOtherOption() {
		String[] args = {
				"test_case",
				"-md",
				"-some_other_option"
		};
		ArgAnalyzer.analyzeArgs(args);
		assertEquals("", EnvInfo.getMainDirectory());
	}
	
	/**
	 * Again, uses EnvInfo's default mainDirectory.
	 */
	@Test
	public void testAnalyzeArgs_name_md_nothing() {
		String[] args = {
				"test_case",
				"-md"
		};
		ArgAnalyzer.analyzeArgs(args);
		assertEquals("", EnvInfo.getMainDirectory());
	}
	
	/**
	 * Same as -md.
	 */
	@Test
	public void testAnalyzeArgs_name_lib_ok() {
		String[] args = {
				"test_case",
				"-lib",
				"lib_directory"
		};
		String log = ArgAnalyzer.analyzeArgs(args);
		// reminder: The ending "/" being added.
		assertEquals("lib_directory/", EnvInfo.getLibURL());
		assertTrue(log.contains("Library directory = lib_directory"));
	}
	
	@Test
	public void testAnalyzeArgs_name_lib_followedByOtherOption() {
		String[] args = {
				"test_case",
				"-lib",
				"-some_other_option"
		};
		ArgAnalyzer.analyzeArgs(args);
		assertEquals("lib/", EnvInfo.getLibURL());
	}

	/**
	 * Defaults to "lib/".
	 */
	@Test
	public void testAnalyzeArgs_name_lib_nothing() {
		String[] args = {
				"test_case",
				"-lib"
		};
		ArgAnalyzer.analyzeArgs(args);
		assertEquals("lib/", EnvInfo.getLibURL());
	}
	
}
