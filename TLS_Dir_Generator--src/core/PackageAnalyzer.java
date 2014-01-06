package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Analyzes a .java file and records this info into the DirGenerator environment
 * at runtime.
 * @author Adrien Droguet
 * @version 1.0	22/03/2012 <br>
 * 				Refactored out of ArgAnalyzer.<br>
 * @version	1.1	30/03/2012<br>
 * 				Now capable of extracting the package's name if the line it is
 * 				in contains more than its name.<br>
 */
public class PackageAnalyzer {

	/**
	 * Looks for a .java file with the name specified in CommandLine's
	 * arguments, and stores that information into BuildXMLGenerator.
	 * @param args
	 * @param log
	 * @return A log String.
	 */
	public static String findJavaPackage(String[] args, String log) {
		log += "\n";
		if (args[0].endsWith(".java")) {
			File file = new File(EnvInfo.getMainDirectory() + args[0]);
			if (file.exists()) {
				log += "Matching .java file found. Reading " + args[0];
				try {
					log += extractPackageName(file);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			} else {
				log += "Warning: No matching .java file found.";
			}
		} else {
			log += "Warning: No .java file specified.";
		}
		return log;
	}

	/**
	 * Reads a .java file and sets its package name in the BuildXMLGenerator
	 * class.
	 * 
	 * @param file
	 *            An existing .java file.
	 * @return Whether or not a package name was found.
	 * @throws FileNotFoundException
	 *             *In theory*, should never be thrown.
	 */
	public static String extractPackageName(File file)
			throws FileNotFoundException {
		String log = "\n  ";
		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(isr);
		try {
			String line = br.readLine();
			while (!line.startsWith("import")
					&& !line.startsWith("public")
					&& !line.startsWith("class")
					&& line != null) {
				if (line.startsWith("package")
						|| line.startsWith("\npackage")) {
					String packageName = line.replace("package", "");
					packageName = packageName.replace(" ", "");
					// make sure you don't read anything after ';'
					int i = packageName.indexOf(';');
					packageName = packageName.substring(0, i);
					//--------------------------------------------
					packageName = packageName.replace(";", "");
					EnvInfo.setPackageName(packageName);
					log += "Package name found: " + packageName;
					break;
				} else {
					line = br.readLine();
				}
			}
			
			if (log.equals("\n  ")) { // nothing was added to log <=> no package
				log += "No package name found.";
			}
			
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return log;
	}
}
