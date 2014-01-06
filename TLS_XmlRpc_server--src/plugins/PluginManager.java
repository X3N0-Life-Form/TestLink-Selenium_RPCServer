package plugins;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import exceptions.PluginException;


import utils.MyFileUtils;

/**
 * This class is tasked with : reading the plugin list, load their respective
 * .xml files, and create classes implementing the Plugin interface accordingly.
 * THIS IS EXCITING!
 * @author Adrien Droguet
 * @version 1.0	19/04/2012<br>
 * 				Class created.
 */
public class PluginManager {

	//
	// plugin list file
	//
	private static final File DEFAULT_LIST_FILE = new File("plugins.txt");
	private static File pluginListFile = DEFAULT_LIST_FILE;
	public static File getPluginListFile() {
		return pluginListFile;
	}
	public static void setPluginListFile(File file) {
		pluginListFile = file;
	}
	
	//
	// plugin list
	//
	private static List<String> pluginList = new LinkedList<String>();
	/**
	 * Returns listed plugins. If the list is empty
	 * @return A list containing each plugin's name.
	 */
	public static List<String> getPluginList() {
		if (pluginList == null || pluginList.isEmpty())
			refreshPluginList();
		return pluginList;
	}
	public static void setPluginList(List<String> list) {
		pluginList = list;
	}
	/**
	 * Resets the plugin list to whatever is in the plugin list file.<br>
	 * Does nothing if not file is set or no File can be found.
	 */
	public static void refreshPluginList() {
		try {
			BufferedReader br = MyFileUtils.getBufferedReader(pluginListFile);
			pluginList = new LinkedList<String>();
			String line = "";
			while((line = br.readLine()) != null) {
				pluginList.add(line);
			}
		} catch (FileNotFoundException e) {
			
		} catch (IOException e) {
			
		}
	}
	
	//
	// loaded plugins
	//
	private static List<Plugin> loadedPlugins = new LinkedList<>();
	public static List<Plugin> getLoadedPlugins() {
		return loadedPlugins;
	}
	
	//
	// other methods
	//
	/**
	 * Resets every attribute to their default values.
	 */
	public static void reset() {
		pluginListFile = DEFAULT_LIST_FILE;
		pluginList = new LinkedList<String>();
	}
	
	/**
	 * Loads a plugin out of an .xml file. The file's location must be the same
	 * as the list file's location.
	 * @param pluginName The plugin's .xml file name.
	 * @throws PluginException If the plugin can't be loaded.
	 * @see #getPluginListFile()
	 */
	public static String loadPlugin(String pluginName) throws PluginException {
		String log = "\nLoading " + pluginName;
		if (!pluginList.contains(pluginName))
			throw new PluginException("The plugin " + pluginName
					+ " is not on the list");
		File pluginXML = new File(pluginListFile.getParent() + "/"
				+ pluginName + ".xml");
		if (!pluginXML.exists())
			throw new PluginException("The file " + pluginXML.getAbsolutePath()
					+ " could not be found");
		SAXBuilder builder = new SAXBuilder();
		try {
			Document doc = builder.build(pluginXML);
			Element root = doc.getRootElement();
			
			Plugin plugin = extractPlugin(root);
			
			loadedPlugins.add(plugin);
		} catch (JDOMException | IOException e) {
			e.printStackTrace();
		}
		log += " - done";
		return log;
	}
	
	/**
	 * Extracts values out of a .xml file and creates a Plugin object.
	 * @param root Root element of the plugin's .xml file.
	 * @return A PluginImpl object constructed out of the .xml file.
	 */
	public static Plugin extractPlugin(Element root) {
		String name = root.getAttributeValue("name");
		String version = root.getAttributeValue("version");
		List<String> extensionList = new LinkedList<String>();
		for (Object current : root.getChildren("file-extensions")) {
			//note: no whitespace allowed is file extensions
			extensionList.add(((Element) current).getValue()
					.replaceAll("\\s", ""));
		}
		//and no tab or char return are allowed either
		String regex = root.getChildText("script-regex")
				.replaceFirst("\\s*", "").replaceAll("\\n|\\t", "");
		//TODO: attribute checks
		String runCmdTestLine = root.getChildText("run-test")
				.replaceFirst("\\s*", "").replaceAll("\\n|\\t", "");
		String runTestScriptCmdLine = root.getChildText("run-test-script")
				.replaceFirst("\\s*", "").replaceAll("\\n|\\t", "");
		PluginImpl plugin = new PluginImpl();
		plugin.setName(name);
		plugin.setVersion(version);
		plugin.setFileExtensionList(extensionList);
		plugin.setScriptRegex(regex);
		plugin.setRunTestCmdLine(runCmdTestLine);
		plugin.setRunTestScriptCmdLine(runTestScriptCmdLine);
		//add compile line
		return plugin;
	}
	
	/**
	 * Refreshes the plugin list and attempts to load everything.
	 * @return A log.
	 */
	public static String loadAllPlugins() {
		String log = "\n----- BEGIN PLUGIN LOAD CYCLE -----";
		refreshPluginList();
		for (String current : pluginList) {
			try {
				log += loadPlugin(current);
			} catch (PluginException e) {
				log += "\nError while loading plugin " + current + " : "
						+ e.getMessage();
			}
		}
		log += "\n----- END PLUGIN LOAD CYCLE -----";
		return log;
	}
	
	/**
	 * Returns a list of supported file extensions supported by the loaded
	 * plugins.
	 * @return A List of every supported file extensions.
	 */
	public static List<String> getSupportedExtensions() {
		List<String> list = new LinkedList<String>();
		for (Plugin current : loadedPlugins) {
			//! class dependent bit of code!!!TODO:find an alternative 
			try {
				for (String ext :
					((PluginImpl) current).getFileExtensionList()) {
					if (!list.contains(ext))
						list.add(ext);
				}
			} catch (ClassCastException e) {
				String ext = current.getFileExtension() ;
				if (!list.contains(ext))
					list.add(ext);
			}
			
		}
		return list;
	}
	
	

	

}
