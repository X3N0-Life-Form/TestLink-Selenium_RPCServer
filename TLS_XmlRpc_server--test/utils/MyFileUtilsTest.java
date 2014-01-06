package utils;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

import org.junit.Test;

import exceptions.TestExecutorException;


import server.ExecutorSetup;

/**
 * Make sure File utils are working.
 * @author Adrien Droguet
 *
 */
public class MyFileUtilsTest {

	/**
	 * Works in a similar way to switch class name method thingy present in the
	 * TestExecutor class.
	 */
	@Test
	public void testExtractClassNameString() {
		String fileBody = "public class ClassName{";
		assertEquals("ClassName", MyFileUtils.extractClassName(fileBody));
	}

	/**
	 * Same thing as before, but passing a File object directly this time.
	 * @throws IOException
	 */
	@Test
	public void testExtractClassNameFile() throws IOException {
		String fileBody = "public class ClassName{";
		File file = new File("test.java");
		file.createNewFile();
		BufferedWriter bw = MyFileUtils.getBufferedWriter(file);
		bw.write(fileBody);
		bw.close();
		assertEquals("ClassName", MyFileUtils.extractClassName(file));
		file.delete();
	}

	/**
	 * Make sure all new files' name match that of their classes.
	 * Make sure old files are gone.
	 * @throws IOException 
	 */
	//@Test//TODO: essayer de faire marcher cette fonctionnalité, ou l'abandonner complètement.
	public void testMakeFileAndClassNamesMatch() throws IOException {
		String fileBody = "public class ClassName{";
		File src = new File("src/");
		src.mkdir();
		File file = new File("src/test.java");
		file.createNewFile();
		BufferedWriter bw = MyFileUtils.getBufferedWriter(file);
		bw.write(fileBody);
		bw.close();
		MyFileUtils.makeFileAndClassNamesMatch("src/");
		File modifiedFile = new File("src/ClassName.java");
		assertTrue(modifiedFile.exists());
		assertFalse(file.exists());
		src.delete();
		file.delete();
		modifiedFile.delete();
	}
	
	/**
	 * The test name should be switched to the class' name present in the test
	 * script.
	 * @throws TestExecutorException 
	 */
	@Test
	public void testSwitchToClassName_ok() throws TestExecutorException {
		ExecutorSetup.setTestName("test_case");
		MyFileUtils.switchToClassName("public class FakeTestCase {");
		assertEquals("FakeTestCase", ExecutorSetup.getTestName());
	}
	
	/**
	 * If switchToClassName is called with an invalid script, i.e. a script that
	 * has no class name(shouldn't happen).
	 * @throws TestExecutorException 
	 */
	@Test(expected=TestExecutorException.class)
	public void testSwitchToClassName_noClassNamePresent()
			throws TestExecutorException {
		ExecutorSetup.setTestName("test_case");
		MyFileUtils.switchToClassName("not a valid script");
		//exception should be thrown
	}

	/**
	 * Cleans up a temporary test file structure.
	 * Note to testers: check that the directory structure is correct.
	 * @throws IOException 
	 */
	@Test
	public void testCleanUpTemp() throws IOException {
		File temp = new File("temp/");
		temp.mkdir();
		File src = new File(temp + "/src/");
		src.mkdir();
		File build = new File(temp + "/build.xml");
		build.createNewFile();
		MyFileUtils.cleanUpTemp(temp);
		assertFalse(temp.exists());
	}
}
