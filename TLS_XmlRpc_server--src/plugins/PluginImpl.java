package plugins;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import exceptions.TestExecutorException;


import server.ResultRetriever;
import utils.MyFileUtils;
import utils.PluginUtils;

/**
 * Basic implementation of the Plugin interface.
 * @author Adrien Droguet
 * @version 1.0	19/04/2012<br>
 * 				Class created.<br>
 * @see Plugin
 */
public class PluginImpl implements Plugin {

	private String name;
	@Override
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private String version;
	@Override
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
	private List<String> extensionList;
	@Override
	public String getFileExtension() {
		if (extensionList.size() == 0)
			return "";
		else
			return extensionList.get(0);
	}
	public List<String> getFileExtensionList() {
		return extensionList;
	}
	public void setFileExtensionList(List<String> list) {
		this.extensionList = list;
	}
	
	private String scriptRegex;
	@Override
	public String getScriptRegex() {
		return scriptRegex;
	}
	public void setScriptRegex(String regex) {
		this.scriptRegex = regex;
	}
	
	private String compileCmdLine;
	@Override
	public String getCompileCmdLine() {
		return compileCmdLine;
	}
	public void setCompileCmdLine(String compileCmdLine) {
		this.compileCmdLine = compileCmdLine;
	}
	
	private String runTestCmdLine;
	@Override
	public String getRunTestCmdLine() {
		return runTestCmdLine;
	}
	public void setRunTestCmdLine(String runTestCmdLine) {
		this.runTestCmdLine = runTestCmdLine;
	}
	
	private String runTestScriptCmdLine;
	@Override
	public String getRunTestScriptCmdLine() {
		return runTestScriptCmdLine;
	}
	public void setRunTestScriptCmdLine(String runTestScriptCmdLine) {
		this.runTestScriptCmdLine = runTestScriptCmdLine;
	}
	
	/**
	 * Initializes the extension list. All attributes should be set by
	 * PluginManager.
	 */
	public PluginImpl() {
		extensionList = new LinkedList<String>();
	}
	
	
	@Override
	public HashMap<Object, Object> runTest() throws TestExecutorException {
		Runtime runtime = Runtime.getRuntime();
		try {
			Process p = runtime.exec(runTestCmdLine);
			p.waitFor();
		} catch (IOException e) {
			throw new TestExecutorException("An error occurred when running" +
					"a test from a plugin");
		} catch (InterruptedException e) {
			throw new TestExecutorException("The execution was interrupted.");
		}
		return ResultRetriever.retrieveResults();
	}

	@Override
	public HashMap<Object, Object> runTest(String script)
			throws TestExecutorException {
		if (!isScriptValid(script))
			throw new TestExecutorException("The provided script is invalid.");
		String fileName = "" + this.getFileExtension();
		File file = new File(fileName);
		try {
			file.createNewFile();
			BufferedWriter bw = MyFileUtils.getBufferedWriter(file);
			bw.write(script);
			bw.close();
			testRunRoutine("PythonTestScript");
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			file.delete();
		}
		return ResultRetriever.retrieveResults();
	}
	
	
	private void testRunRoutine(String testName) throws IOException {
		Runtime runtime = Runtime.getRuntime();
		
		Process comp;
		if (this.compileCmdLine != null && !this.compileCmdLine.equals("")) {
			comp = runtime.exec(
					PluginUtils.formatLine(compileCmdLine, testName));
			//to add: if error stream not empty --> put that in another method that deals with streams
			InputStream is = comp.getErrorStream();
			byte[] byteArray = new byte[is.available()];
			is.read(byteArray);
			System.out.println(byteArray);
		}
		//TODO:then compile & run using command line
		
		//TODO:format command line; split the string and include the test name somewhere
		@SuppressWarnings("unused")
		Process p = runtime.exec(
				PluginUtils.formatLine(runTestScriptCmdLine, testName));
		//check streams
		
		
	}//TODO: extract testRunner interface thingy
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((extensionList == null) ? 0 : extensionList.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((scriptRegex == null) ? 0 : scriptRegex.hashCode());
		result = prime * result
				+ ((runTestCmdLine == null) ? 0 : runTestCmdLine.hashCode());
		result = prime
				* result
				+ ((runTestScriptCmdLine == null) ? 0 : runTestScriptCmdLine
						.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PluginImpl other = (PluginImpl) obj;
		if (extensionList == null) {
			if (other.extensionList != null)
				return false;
		} //else if (!extensionList.equals(other.extensionList))
			//return false;//TODO:
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (scriptRegex == null) {
			if (other.scriptRegex != null)
				return false;
		} else if (!scriptRegex.equals(other.scriptRegex))
			return false;
		if (runTestCmdLine == null) {
			if (other.runTestCmdLine != null)
				return false;
		} else if (!runTestCmdLine.equals(other.runTestCmdLine))
			return false;
		if (runTestScriptCmdLine == null) {
			if (other.runTestScriptCmdLine != null)
				return false;
		} else if (!runTestScriptCmdLine.equals(other.runTestScriptCmdLine))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PluginImpl [name=" + name + ", version=" + version
				+ ", extensionList=" + extensionList + ", regex=" + scriptRegex
				+ ", runTestCmdLine=" + runTestCmdLine
				+ ", runTestScriptCmdLine=" + runTestScriptCmdLine + "]";
	}
	
	@Override
	public boolean isScriptValid(String script) {
		return script.matches(scriptRegex);
	}
	

	
}
