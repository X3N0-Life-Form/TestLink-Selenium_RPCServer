package core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class PackageAnalyzerTest {
	
	private static File packageLessTest;
	private static File packagedTest;
	private static File packagedTestOneLine;
	
	/**
	 * Grab the java files.
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
		packageLessTest = new File("test/IndeedDefaultPackage.java");
		packagedTest = new File("test/forTestPurposeOnly/Indeed.java");
		packagedTestOneLine =
				new File("test/forTestPurposeOnly/IndeedOneLine.java");
	}
	
	@Before
	public void setUp() {
		EnvInfo.setPackageName("");
	}
	
	/**
	 * If no package name is found, BuildXMLGenerator's packageName is its
	 * default value.
	 * @throws FileNotFoundException
	 */
	@Test
	public void testExtractPackageName_noPackage()
			throws FileNotFoundException {
		String res = PackageAnalyzer.extractPackageName(packageLessTest);
		assertTrue(res.endsWith("No package name found."));
		assertEquals("", EnvInfo.getPackageName());
	}
	
	/**
	 * If it is found.
	 * @throws FileNotFoundException
	 */
	@Test
	public void testExtractPackageName_package()
			throws FileNotFoundException {
		String res = PackageAnalyzer.extractPackageName(packagedTest);
		assertTrue(res.endsWith("Package name found: forTestPurposeOnly"));
		assertEquals("forTestPurposeOnly", EnvInfo.getPackageName());
	}
	
	/**
	 * If there is more than the package's name on its line.
	 * @throws FileNotFoundException
	 */
	@Test
	public void testExtractPackageName_oneLine() throws FileNotFoundException {
		String res = PackageAnalyzer.extractPackageName(packagedTestOneLine);
		assertTrue(res.endsWith("Package name found: forTestPurposeOnly"));
		assertEquals("forTestPurposeOnly", EnvInfo.getPackageName());
	}
}
