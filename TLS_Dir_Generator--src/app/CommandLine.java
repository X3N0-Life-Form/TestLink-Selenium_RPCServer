package app;

import java.io.IOException;

import core.ArgAnalyzer;
import core.BuildXMLGenerator;
import core.DirectoryStructureGenerator;

/**
 * Main entry. A file called templateBuild.xml must be present in the program's
 * directory.
 * @author Adrien Droguet
 * @version 1.0 <br>
 * @version 1.1 15/03/2012 <br>
 * 				Made some adjustments for project integration into .jar file.<br>
 * 				Better argument handling.<br>
 * @version 2.0 19/03/2012 <br>
 * 				Added the ability to read a .java file, deduce its package name
 * 				and include that information into the build.xml.<br>
 * @version	2.1	21/03/2012 <br>
 * 				If a .java file is found, it is now moved into the generated
 * 				test directory.<br>
 * @version	2.2	30/03/2012<br>
 * 				Build xml file generation now produces a log.<br>
 * @version 2.3	17/04/2012<br>
 * 				DirGeneratorInterface now serves as a secondary entry point to
 * 				the application.
 */
public class CommandLine {
	/**
	 * Takes the test case's name as an argument.
	 * Example:
	 * 		nameless -md E:/tests -lib E:/lib --help
	 * @param args
	 */
	public static void main(String[] args) {
		String outlog = "----- BEGIN DirGenerator -----";
		outlog += "\nEvent Log:";
		try {
			if (args[0].equals("--help")) {
				outlog += ArgAnalyzer.analyzeArgs(args);
			} else {
				outlog += ArgAnalyzer.analyzeArgs(args);
				outlog += "\nGenerating directory structure";
				outlog += DirectoryStructureGenerator.generate(args[0]
						.replace(".java", ""));
				outlog += "\nGenerating build.xml file";
				outlog += BuildXMLGenerator.createBuildXML(args[0]);
				outlog += "\nbuild.xml generation complete.";
			}
		} catch (IOException e) {
			outlog += "\nAn error occured when generating the environment.";
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			outlog += "\nTest case name unspecified. Please write a test "
					+ "case name as the first argument."
					+ "\nType --help if you require directions.";
		}
		outlog += "\n----- END DirGenerator -----";
		System.out.println(outlog);
	}
}
