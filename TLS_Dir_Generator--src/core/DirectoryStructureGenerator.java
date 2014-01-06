package core;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Used to create the directory tree required to run the test case.
 * @author Adrien Droguet
 * @version 1.0	15/03/2012 <br>
 * @version 1.1	20/03/2012 <br>
 * 				Added a log return String to generate().<br>
 * @version	1.2	21/03/2012 <br>
 * 				Now moves a .java file if one is found.<br>
 * @version 1.3	27/03/2012 <br>
 * 				Modified behavior: if the file cannot be moved, it is copied
 * 				instead.<br>
 */
public class DirectoryStructureGenerator {

	/**
	 * Generates a test directory for the given test.
	 * If a .java file is found, it is then moved into the generated directory.
	 * @param testCaseName
	 * @throws IOException 
	 */
	public static String generate(String testCaseName) throws IOException {
		String prefix = EnvInfo.getMainDirectory() + testCaseName;
		String log = "\nGenerating directories";
		// test_main/TestName
		File file = new File(prefix);
		file.mkdir();
		log += "\n  " + file.getAbsolutePath();
		// test_main/TestName/src
		file = new File(prefix + "/src");
		file.mkdir();
		log += "\n  " + file.getAbsolutePath();
		// test_main/TestName/report
		file = new File(prefix + "/report");
		file.mkdir();
		log += "\n  " + file.getAbsolutePath();
		// if .java then move
		file = new File(prefix + ".java");
		if (file.exists())
			log += move(prefix, file, testCaseName);
		return log;
	}

	/**
	 * Moves the specified file to the specified test case folder.
	 * Note: Automatically moves to prefix/src, no need to add it yourself.
	 * @param prefix
	 * @param file
	 * @return A log String.
	 * @throws IOException
	 */
	private static String move(String prefix, File file, String testCaseName)
			throws IOException {
		String source = file.getAbsolutePath();
		String destination = new File(prefix + "/src").getAbsolutePath()
				+ "/" + testCaseName + ".java";
		String log = "\nMoving file:" +
				"\n  From: " + source +
				"\n  To: " + destination;
		Path sourcePath = Paths.get(source, "");
		Path destinationPath = Paths.get(destination, "");
		try {
			Files.move(sourcePath,
					destinationPath,
					StandardCopyOption.REPLACE_EXISTING);
		} catch (FileSystemException e) {
			Files.copy(sourcePath, destinationPath);
		}
		return log;
	}
}
