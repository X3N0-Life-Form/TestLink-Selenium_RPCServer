package core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Used to generate a build.xml Ant file.
 * @author Adrien Droguet
 * @version 1.0 15/03/2012 <br>
 * @version 1.1	19/03/2012 <br>
 * 				Added support for test package name inclusion into the
 * 				build.xml.<br>
 * @version 1.2	30/03/2012<br>
 * 				Changed createBuildXML()'s return type from File to String. It
 * 				now produces a log instead of returning the (never used) File
 * 				object.<br>
 * @version 2.0	17/04/2012<br>
 * 				Moved environment-related attributes into EnvInfo
 */
public class BuildXMLGenerator {

	
	
	/**
	 * Creates the build.xml file based on info stored in the EnvInfo class.
	 * @return A log of the build.xml's creation process.
	 * @throws IOException 
	 */
	public static String createBuildXML(String testName) {
		String log = "\n----- BUILD XML GENERATION -----";
		if (EnvInfo.getTemplate() == null)
			EnvInfo.setTemplate(new File("templateBuild.xml"));
		EnvInfo.setTestCaseName(testName);
		//
		// template reader
		//
		BufferedReader br = null;
		try {
			br = getTemplateReader();
		} catch (FileNotFoundException e) {
			log += "\n" + EnvInfo.getTemplate().getAbsolutePath()
					+ " could not be found.";
		}
		
		//
		// build.xml creation
		//
		File build = new File(EnvInfo.getMainDirectory()
				+ EnvInfo.getTestCaseName() + "/build.xml");
		if (!build.exists()) {
			try {
				build.createNewFile();
				log += "\nbuild.xml file created.";
			} catch (IOException e) {
				log += "\nbuild.xml file could not be created.\n";
			}
		}
		
		//
		// build.xml writer
		//
		BufferedWriter bw = null;
		try {
			bw = getBuildXMLWriter(build);
		} catch (FileNotFoundException e) {
			log += "\n" + build.getAbsolutePath() + " could not be found.";
		}
		
		//
		// readin'n'writin'
		//
		log += createFromTemplate(br, bw);
		
		//close everything
		try {
			br.close();
			bw.close();
		} catch (IOException e) {
			log += "\nAn error was encountered when closing the buffered " +
					"reader/writer";
		} catch (NullPointerException e) {
			log += "\nThe buffered and/or writer could not be initialized.";
		}
		reset();
		
		log += "\n----- END XML GENERATION -----";
		return log;
	}
	
	/**
	 * Same as the other method, but uses whatever test case name is already
	 * set, it can very well be the default value (=null) !!!.
	 * @return A log of the build.xml's creation process.
	 * @throws IOException
	 */
	public static String createBuildXML() throws IOException {
		return createBuildXML(EnvInfo.getTestCaseName());
	}

	/**
	 * EnvInfo to its default values.
	 */
	public static void reset() {
		EnvInfo.reset();
	}
	
	/**
	 * Reads the template using the BufferedReader, and writes the actual
	 * build.xml file using the BufferedWriter.<br>
	 * Note: &lt;!-- ## this kind of lines are ignored-->
	 * @param br BufferedReader reading the templateBuild.xml.
	 * @param bw BufferedWriter writing the build.xml.
	 */
	private static String createFromTemplate(BufferedReader br,
			BufferedWriter bw) {
		if (br == null || bw == null) {
			return "\nBuffered reader and/or writer weren't iniatlized." +
					" Aborting creation from template.";
		} else {
			String log = "\nCreating from template.xml.";
			try {
				String line = br.readLine();
				while (line != null) {
					//<!-- ## this kind of lines are ignored-->
					if ((line.contains("#") && !line.contains("##"))
							|| line.contains("$")) {
						String before = new String(line);
						line = transform(line);
						if (!before.equals(line)) {
							log += "\n  Transforming line from:\n    " + before;
							log += "\n  To:\n    " + line;
						}
					}
					bw.write(line);
					bw.newLine();
					line = br.readLine();
					if (line == null)
						log += "\nCreation complete.";
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return log;
		}
	}

	/**
	 * Replace macros by the real stuff.
	 * @param line The line with something to transform.
	 * @return The transformed line.
	 */
	private static String transform(String line) {
		//not very efficient, but not really a concern
		line = line.replace("#folderName#",
				EnvInfo.getMainDirectory() + EnvInfo.getTestCaseName());
		line = line.replace("#lib#", EnvInfo.getLibURL());
		line = line.replace("#testName#", EnvInfo.getTestCaseName());
		line = line.replace("#testPackage#", EnvInfo.getPackageName());
		line = line.replace("#junit-version#", EnvInfo.getJunitVersion());
		line = line.replace("#selenium-version#", EnvInfo.getSeleniumVersion());
		line = line.replace("#report#", EnvInfo.getReport());
		if (EnvInfo.getPackageName().equals(""))
			line.replace("${test_package}.", "");
		return line;
	}

	/**
	 * Gets a BufferedWriter object for the specified file.
	 * @param build The build.xml File object.
	 * @return A BufferedWriter object for the specified file.
	 * @throws FileNotFoundException
	 */
	private static BufferedWriter getBuildXMLWriter(File build)
			throws FileNotFoundException {
		FileOutputStream fos = new FileOutputStream(build);
		OutputStreamWriter osr = new OutputStreamWriter(fos);
		return new BufferedWriter(osr);
	}
	
	/**
	 * Gets a BufferedReader object that reads the template file.<br>
	 * Note: The template *MUST* be in the same folder as the DirGenerator.
	 * @return A BufferedReader object that reads the template file.
	 * @throws FileNotFoundException
	 */
	private static BufferedReader getTemplateReader()
			throws FileNotFoundException {
		FileInputStream fis;
		try {
			fis = new FileInputStream(EnvInfo.getTemplate());
		} catch (FileNotFoundException e) {
			// if we are in a dev environment
			fis = new FileInputStream("src/core/" + EnvInfo.getTemplate());
		}
		InputStreamReader isr = new InputStreamReader(fis);
		return new BufferedReader(isr);
	}
}
