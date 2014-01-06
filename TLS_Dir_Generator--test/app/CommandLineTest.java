package app;

import java.io.File;

import org.junit.After;

/**
 * Just a template.
 * @author Adrien Droguet
 *
 */
public class CommandLineTest {
	
	private File dir = null;
	private File src = null;
	private File report = null;
	private File build = null;
	
	/**
	 * Removes the generated files & directories.
	 */
	@After
	public void teardown() {
		build.delete();
		src.delete();
		report.delete();
		dir.delete();
		
		dir = null;
		src = null;
		report = null;
		build = null;
	}
	
	
}
