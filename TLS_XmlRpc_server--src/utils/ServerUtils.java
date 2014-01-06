package utils;

import java.io.FileNotFoundException;
import java.io.IOException;

import exceptions.CfgException;

import app.DirGeneratorInterface;

import server.ExecutorSetup;
import server.ServerSetup;
import server.TLSJavaServer;

/**
 * Contains various methods intended to be used by the server.
 * @author Adrien Droguet
 * @version 1.0	10/04/2012<br>
 * 				Class created.<br>
 * 				Fixed several bugs in dealWithArgs.<br>
 * @version 1.1	25/04/2012<br>
 * 				Clearly states that configuring the server from a config file
 * 				overrides any previous setting.<br>
 * @version 1.2	16/05/2012<br>
 * 				Improved command line argument handling.<br>
 * @see TLSJavaServer
 */
public class ServerUtils {
	
	/**
	 * .+:\\s*(\\S+\\s*)+
	 */
	public static final String LINE_REGEX = ".+:\\s*" +	//Option: (whitespace)
			"(\\S+\\s*)+";								//something (whitespace)
	
	/**
	 * String sub = line.substring(length);<br>
	 * sub = sub.replaceAll("\t", "");
	 * @param line
	 * @param length
	 * @return sub
	 * @throws CfgException If a line is incorrectly formed.
	 */
	public static String processLine(String line, int length)
			throws CfgException {
		if (!line.matches(LINE_REGEX))
			throw new CfgException("Incorrectly formed line:\n\t" + line);
		String sub = line.substring(length);
		//remove any tab
		sub = sub.replaceAll("\t", "");
		//remove any whitespace located before the option
		sub = sub.replaceFirst("\\s*", "");
		return sub;
	}
	
	/**
	 * Interprets the arguments in order to set up the environment.<br>
	 * Recognized options:<br>
	 * 		-md		Specify a main directory.<br>
	 * 		-cfg	Specify a configuration file; overrides any other option.<br> 
	 * 		-log	Tell the server to create a log.txt file instead of
	 * 				outputting everything to the terminal.<br>
	 * @param args
	 * @throws IOException
	 */
	public static void dealWithArgs(String[] args) throws IOException {
		if (args != null && args.length > 0) {
			//
			// checking first args
			//
			int port = -1;
			try {
				if (!args[0].startsWith("-")) {
					port = Integer.parseInt(args[0]);
					ServerSetup.setServerPort(port);
					System.out.println("Setting server port to " + args[0]);
				} else {
					System.out.println("No server port was given. Defaulting" +
							" to " + ServerSetup.getServerPort() + ".");
				}
			} catch (NumberFormatException e) {
				System.out.println(args[0] +
						" is an invalid port number. Defaulting to " +
						ServerSetup.getServerPort() + ".");
			}
			//
			// checking every other argument
			//
			boolean noOverride = false;
			boolean cfg = false;
			String md = "-1";
			boolean log = false;
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals(ArgOptions.MAIN_DIRECTORY)) {
					try {
						md = args[i + 1];
						//not setting from conf or overriding
						if (!cfg || noOverride)
							ExecutorSetup.setURLMainTestFolder(md);
					} catch (IndexOutOfBoundsException e) {
						System.out.println("The specified URL for the main "
								+ "directory is invalid. Defaulting to current"
								+ " directory.");
					}
				} else if (args[i].equals(ArgOptions.LOG)) {
					log = true;
					if (!cfg || noOverride)
						ServerSetup.setRecord(log);
				} else if (args[i].equals(ArgOptions.CONFIGURATION)) {
					cfg = true;
					dealWithCfgFile(args, i);
				} else if (args[i].equals(ArgOptions.NO_OVERRIDE)) {
					noOverride = true;
				}
			}
			//
			// final checks regarding the no override option
			//
			if (cfg && noOverride) {
				System.out.println("Command line arguments override " +
						"configuration file.");
				//restore previously recorded options
				if (port != -1)
					ServerSetup.setServerPort(port);
				if (!md.equals("-1"))
					ExecutorSetup.setURLMainTestFolder(md);
				if (log)
					ServerSetup.setRecord(log);
			} else if (cfg && !noOverride) {
				System.out.println("Configuration file overrides command " +
						"line arguments.");
			} else if (noOverride && !cfg) {
				System.out.println("\"-nooverride\" ignored: no configuration" +
						" file was specified.");
			}
			//the end
		} else {
			System.out.println("No argument was provided. Using default options");
		}
	}
	
	/**
	 * Contains all the exception handling related to configuration files when
	 * calling setupFromCfgFile.
	 * @param args Server args.
	 * @param i Argument index.
	 * @throws IOException
	 * @see {@link ServerSetup#setupFromCfgFile(String)}
	 */
	public static void dealWithCfgFile(String[] args, int i)
			throws IOException {
		try {
			System.out.println("Configuring server from " + "file: "
					+ args[i + 1] + ".");
			try {
				ServerSetup.setupFromCfgFile(args[i + 1]);
			} catch (CfgException e) {
				System.out.println("An error occurred when "
						+ "configuring from file.");
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				System.out.println("The file " + args[i + 1]
						+ " could not be found. Aborting "
						+ "configuration from file.");
			}
		} catch (IndexOutOfBoundsException e) {
			System.out.println("No file name given.");
		}
	}
	
	/**
	 * Provides a summary of the selected settings (port, main directory, lib
	 * directory, etc.).
	 * @return A log String.
	 */
	public static String startupSummary() {
		String log = "\nListening to port " + ServerSetup.getServerPort();
		log += "\nMain directory : " + ExecutorSetup.getURLMainTestFolder();
		log += "\nLib directory : " + ExecutorSetup.getURLLibFolder();
		log += "\nSelenium server directory : "
				+ ExecutorSetup.getURLSeleniumServer();
		log += "\nJUnit version : " + DirGeneratorInterface.getJUnitVersion();
		log += "\nSelenium version : "
				+ DirGeneratorInterface.getSeleniumVersion();
		
		log += "\nServer started at " + Now.get();
		return log;
	}
}
