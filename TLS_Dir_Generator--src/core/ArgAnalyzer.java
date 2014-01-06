package core;


/**
 * Analyzes CommandLine arguments.
 * @author Adrien Droguet
 * @version 1.0 <br>
 * @version 1.1 20/03/2012 <br>
 * 				Added support for packaged tests.<br>
 * @version 1.2	22/03/2012 <br>
 * 				Improved package recognition mechanism. <br>
 * 				Moved said recognition to PackageAnalyzer.<br>
 */
public class ArgAnalyzer {
	/**
	 * Analyzes the arguments & sets the environment accordingly.
	 * @param args
	 * @return A log String.
	 */
	public static String analyzeArgs(String[] args) {
		String log = "\n  ";
		
		try {
			if (args[0].startsWith("-"))
				log += "Invalid test case name.";
			else {
				//
				// analyzing the options
				//
				for (int i = 1; i < args.length; i++) {
					String current = args[i];
					try {
						switch (current) {
						case "-md": {
							// if followed by another option
							if (args[i + 1].startsWith("-"))
								break;
							EnvInfo.setMainDirectory(args[i + 1]);
							log += "\n  Main directory = "
									+ EnvInfo.getMainDirectory();
							break;
						}
						case "-lib": {
							if (args[i + 1].startsWith("-"))
								break;
							EnvInfo.setLibURL(args[i + 1]);
							log += "\n  Library directory = "
									+ EnvInfo.getLibURL();
							break;
						}
						case "--help": {
							log += displayHelp();
							break;
						}
						}
					} catch (IndexOutOfBoundsException e) {
						// do nothing
					}
				}
				
				//
				// package analysis
				//
				// Note: analyze options first to prevent PackageAnalyzer from
				// using the wrong main directory.
				log += PackageAnalyzer.findJavaPackage(args, log);
				log += "\n  Package name = "
						+ EnvInfo.getPackageName();

				// Don't forget to remove the .java
				// Reminder: BuildXMLGenerator's testName variable is only set
				// when
				// calling createBuildXML().
				log += "\n  Test name = " + args[0].replace(".java", "");

				
				log += "\nArgument analysis complete";
			}
		} catch (IndexOutOfBoundsException e) {
			log += "No test case name specified! Aborting argument analysis.";
		}
		return log;
	}

	/**
	 * Writes the help.
	 * Notes: This would be more elegant if stored in a file instead, but so far
	 * it is small enough to be maintained.
	 * @return Help as a String.
	 */
	public static String displayHelp() {
		String res = "\n\n--Help:";
		res += "\nSummary:" +
				"\nThis program is used to create the environment required to" +
				"run test cases called from TestLink, more specifically " +
				"creating the test case folders, using the xmlrpc server " +
				"developped by myself.\nI recommend user to write a small " +
				"script to call this program instead of writing the main " +
				"directory and library path for every single test case.";
		res += "\n\nUsage:" +
				"\nThere must be at least one argument. The first argument" +
				" sets the test case's name. See the command list for more" +
				" details about the other possible arguments." +
				"\nIf the first argument has a .java extension and if that " +
				"file can be found, its package name (if it exists) will be " +
				"read and included into the build.xml." +
				"\nThe main directory is where your test case folder will be " +
				"created. If none is specified, it is set to the current " +
				"directory by default." +
				"The library directory is where all your libraries and the " +
				"selenium server must be located. If none is specified, it is" +
				" set to the current directory/lib by default.";
		res += "\n\nCommand list:" +
				"\n  -md [dir]   : sets the main directory" +
				"\n  -lib [dir]  : sets the library directory" +
				"\n  --help      : displays the help";
		return res;
	}
}
