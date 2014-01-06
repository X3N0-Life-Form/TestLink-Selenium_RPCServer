package server;

import java.io.IOException;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.webserver.WebServer;

import utils.ServerUtils;

/**
 * A basic xml-rpc server designed to run JUnit tests and send back the results.
 * @author Adrien Droguet
 * @version 1.0	The server is working and listens to port 8081.<br>
 * @version 1.1 The server can now listen to a specific port.<br>
 * @version 1.2	20/03/2012 <br>
 * 				Better option handling - no longer display an error message when
 * 				using an option without specifying a port first.<br>
 * 				Added support for packaged tests.<br>
 * @version 2.0	26/03/2012 <br>
 * 				Now capable of reading configuration out of a .ini file.<br>
 * @version 2.1	27/03/2012<br>
 * 				Now capable of processing an incoming test file.<br>
 * 				WARNING: since this feature added a dependency to DirGenerator,
 * 				the server now ALSO needs to have a templateBuild.xml file in
 * 				its folder.<br>
 * @version 2.2	2/04/2012<br>
 * 				Added some debugging tools.<br>
 * @version	2.3	5/04/2012<br>
 * 				Added JUnit test suite support.<br>
 * 				The server can now be set to output in a log file instead of the
 * 				terminal.<br>
 * 				Added time stamps at server start, request arrival and end of
 * 				execution.<br>
 * 				6/04/2012<br>
 * 				Improved support for selenium server.<br>
 * 				10/04/2012<br>
 * 				Refactored out all setup methods to the ServerSetup class.<br>
 * 				Fixed a bug involving args set to null.<br>
 * @version 2.4	17/04/2012<br>
 * 				Selenium and JUnit versions can now be set in the configuration
 * 				file.<br>
 * @version 3.0	2/05/2012<br>
 * 				Now generates a more comprehensive report in addition to the
 * 				standard JUnit test report.
 */
public class TLSJavaServer {
	
	/**
	 * Args format [port] ([option] [arg])<br>
	 * Possible options:<br>
	 * 		-md		Specify a main directory.<br>
	 * 		-cfg	Specify a configuration file; overrides any other option.<br> 
	 * 		-log	Tell the server to create a log.txt file instead of
	 * 				outputting everything to the terminal.<br>
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ServerUtils.dealWithArgs(args);
			WebServer server = new WebServer(ServerSetup.getServerPort());
			server.setParanoid(false);
			
			//
			// setup the handler
			//
			PropertyHandlerMapping phm = ServerSetup.setupHandler(server);
			//debug
			System.out.println("List of handled methods:");
			for (String current : phm.getListMethods())
				System.out.println("  " + current);
			
			//
			// Starting the server
			//
			System.out.println("Starting server ...");
			server.start();
			System.out.println("Server started.");
			if (ServerSetup.isRecord())
				ServerSetup.setupLogOutput();
			System.out.println(ServerUtils.startupSummary());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlRpcException e) {
			e.printStackTrace();
		}
	}
}
