/**
 * 
 */
package core;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author adt
 *
 */
public class DirectoryStructureGeneratorTest {

	private File dir = null;
	private File src = null;
	private File report = null;
	private static final String root = "E:/dir_struc_test/";
	
	/**
	 * Sets up the testing directory.
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
		File file = new File(root);
		if (!file.exists())
			file.mkdir();
	}
	
	@Before
	public void setUp() {
		dir = null;
		src = null;
		report = null;
	}


	@After
	public void tearDown() {
		try {
			src.delete();
			report.delete();
			dir.delete();
		} catch (NullPointerException e) {
			// no use in deleting 'em is they don't exist
		}
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		File file = new File(root);
		file.delete();
	}

	/**
	 * Checks the normal behavior of the generate() method.
	 * @throws IOException
	 */
	@Test
	public void testGenerate_normal() throws IOException {
		EnvInfo.setMainDirectory(root);
		DirectoryStructureGenerator.generate("test_case");
		
		dir = new File(root + "test_case/");
		src = new File(root + "test_case/src/");
		report = new File(root + "test_case/report/");
		
		assertTrue(dir.exists());
		assertTrue(src.exists());
		assertTrue(report.exists());
	}
	
	/**
	 * Checks if a .java file is moved properly.
	 * @throws IOException 
	 */
	@Test
	public void testGenerate_withJavaFile() throws IOException {
		EnvInfo.setMainDirectory(root);
		File javaFile = new File(root + "test_case.java");
		javaFile.createNewFile();
		DirectoryStructureGenerator.generate("test_case");
		File nuFile = new File(root + "test_case/src/test_case.java");
		assertTrue(nuFile.exists());
		javaFile.delete();
	}
}
