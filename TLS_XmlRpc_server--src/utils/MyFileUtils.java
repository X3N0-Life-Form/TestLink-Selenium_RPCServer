package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import exceptions.TestExecutorException;


import server.ExecutorSetup;

/**
 * Contains several file utility methods.
 * @author Adrien Droguet
 * @version 1.0 4/04/2012<br>
 * 				Class created.<br>
 */
public class MyFileUtils {
	
	/**
	 * Creates a buffered reader for a specified file.
	 * @param file
	 * @return A BufferedReader for the specified File.
	 * @throws FileNotFoundException
	 */
	public static BufferedReader getBufferedReader(File file)
			throws FileNotFoundException {
		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis);
		return new BufferedReader(isr);
	}

	/**
	 * Creates a buffered writer for a specified file.
	 * @param file
	 * @return A BufferedWriter for the specified File.
	 * @throws FileNotFoundException
	 */
	public static BufferedWriter getBufferedWriter(File file)
			throws FileNotFoundException {
		FileOutputStream fos = new FileOutputStream(file);
		OutputStreamWriter osw = new OutputStreamWriter(fos);
		BufferedWriter bw = new BufferedWriter(osw);
		return bw;
	}
	
	/**
	 * Extract the class' name out of a String.
	 * @param testScript
	 * @return Class name.
	 */
	public static String extractClassName(String testScript) {
		int i = testScript.indexOf("public class");
		// start the substring right after "public class"
		String sub = testScript.substring(i + "public class".length());
		i = sub.indexOf('{');
		sub = sub.substring(0, i).trim();
		return sub;
	}

	/**
	 * Extract the class' name out of a File.
	 * @param file
	 * @return Class name.
	 * @throws IOException 
	 */
	public static String extractClassName(File file)
			throws IOException {
		BufferedReader br = MyFileUtils.getBufferedReader(file);
		String line = "";
		String fileBody = "";
		//read the whole file
		while ((line = br.readLine()) != null) {
			fileBody += line;
		}
		return extractClassName(fileBody);
	}
	
	/**
	 * Makes sure class names and file names are a match.
	 * DOESN'T WORK!
	 * @param srcURL
	 * @return A log String.
	 * @throws IOException 
	 */
	public static String makeFileAndClassNamesMatch(String srcURL)
			throws IOException {
		File srcFolder = new File(srcURL);
		File[] files = srcFolder.listFiles();
		String log = "\nVerifying that the name of all .java files present in" +
				" the source folder match their public class name.";
		for (File current : files) {
			if (current.getName().endsWith(".java")) {
				log += "\n  Checking file " + current.getAbsolutePath();
				String name = MyFileUtils.extractClassName(current);
				log += "\n    Class name found: " + name;
				if (!name.equals(current.getName())
						&& !name.equals(current.getName() + ".java")) {
					log += "\n    Changing file name to " + name;
					File newFile = new File(srcFolder, name + ".java");
					//Note to future devs: make Files.move work properly
					try {
						Files.move(Paths.get(current.getAbsolutePath(), ""),
								Paths.get(newFile.getAbsolutePath(), ""),
								StandardCopyOption.REPLACE_EXISTING);
					} catch (FileSystemException e) {
						copyPaste(current, newFile);
						//cleanUpTemp(current);
						current.delete();
					}
				}
			}
		}
		log += "\n  File checking complete.";
		return log;
	}

	/**
	 * Manual copy & paste.
	 * @param source
	 * @param destination
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void copyPaste(File source, File destination)
			throws IOException, FileNotFoundException {
		destination.createNewFile();
		BufferedReader br =
				MyFileUtils.getBufferedReader(source);
		BufferedWriter bw =
				MyFileUtils.getBufferedWriter(destination);
		String line = "";
		while ((line = br.readLine()) != null) {
			bw.write(line);
		}
		br.close();
		bw.close();
	}
	
	/**
	 * Switches the current test name to whatever class name is present in the
	 * specified test script.
	 * @param testScript
	 * @throws TestExecutorException The script is invalid.
	 */
	public static void switchToClassName(String testScript)
			throws TestExecutorException {
		try {
			ExecutorSetup.setTestName(MyFileUtils.extractClassName(testScript));
		} catch (StringIndexOutOfBoundsException e) {
			throw new TestExecutorException("Invalid script.");
		}
	}

	/**
	 * Deletes the specified directory and all it contents.
	 * @param temp
	 */
	public static void cleanUpTemp(File temp) {
		File[] files = temp.listFiles();
		if (files != null) {
			for (File current : files) {
				if (current.isDirectory())
					cleanUpTemp(current);
				else
					current.delete();
			}
		}
		temp.delete();
	}
	
	/**
	 * Reads a file and returns it into a String.
	 * @param br
	 * @return
	 * @throws IOException
	 */
	public static String readFile(BufferedReader br) throws IOException {
		String res = "";
		String line = "";
		while ((line = br.readLine()) != null) {
			res += line;
		}
		return res;
	}
}
