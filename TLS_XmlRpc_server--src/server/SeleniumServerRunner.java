package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Runs the server in a new thread.
 * @author Adrien Droguet
 * @version 1.0	6/04/2012<br>
 * 				Class created.
 */
public class SeleniumServerRunner implements Runnable {
	
	/**
	 * Creates a new thread to run the selenium server.
	 */
	@Override
	public void run() {
		Runtime runtime = Runtime.getRuntime();
		String commandLine = "java -jar "
				+ ExecutorSetup.getURLSeleniumServer()
				+ ExecutorSetup.getSeleniumServerName();
		try {
			//System.out.println("Launching Selenium Server:\t" + commandLine);
			runtime.exec(commandLine, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * No comment.
	 * @param proc
	 * @return A String containing what is into the process' input and error
	 * streams.
	 * @deprecated Unused. Code left it just in case it would prove to be of use
	 * some day.
	 */
	public static String getProcessStatus(Process proc) {
		String res = "\n  Error stream:\n";
		
		InputStreamReader isr =
				new InputStreamReader(proc.getErrorStream());
		BufferedReader br = new BufferedReader(isr);
		String line = "\n";
		try {
			while ((line = br.readLine()) != null) {
				res += line;
			}
		} catch (IOException e) {
			//nothing
		}
		res += "\n  Input stream:\n";
		isr = new InputStreamReader(proc.getInputStream());
		br = new BufferedReader(isr);
		try {
			while ((line = br.readLine()) != null) {
				res += line;
			}
		} catch (IOException e) {
			//nothing
		}
		return res;
	}
}
