package plugins;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Before;
import org.junit.Test;

import exceptions.PluginException;



public class PluginManagerTest {
	private static final File template =
			new File("src/plugins/pluginTemplate.xml");
	private static final File listFile =
			new File ("E:/eclipse workspace & lib/TLS_XmlRpcServer/plugins.txt");
	/**
	 * This plugin's attributes are identical to the ones described in
	 * pluginTemplate.xml. Testers should make sure these are up to date.
	 */
	private static final Plugin testPlugin = createTestPlugin();
	private static Plugin createTestPlugin() {
		PluginImpl plugin = new PluginImpl();
		plugin.setName("test");
		plugin.setVersion("1.0");
		plugin.setScriptRegex("regex");
		plugin.setRunTestCmdLine("echo something");
		plugin.setRunTestScriptCmdLine("echo something else");
		return plugin;
	}
	
	/**
	 * Identical to the first one, except it's name is test2 instead of test. 
	 */
	private static final Plugin testPlugin2 = createTestPlugin2();
	private static Plugin createTestPlugin2() {
		PluginImpl plugin = new PluginImpl();
		plugin.setName("test2");
		plugin.setVersion("1.0");
		plugin.setScriptRegex("regex");
		plugin.setRunTestCmdLine("echo something");
		plugin.setRunTestScriptCmdLine("echo something else");
		return plugin;
	}
			
	
	/**
	 * Reset everything before each test.
	 */
	@Before
	public void setup() {
		PluginManager.reset();
	}
	
	

	/**
	 * Note: we are checking the default file name.
	 */
	@Test
	public void testGetPluginListFile() {
		File file = new File("plugins.txt");
		assertEquals(file, PluginManager.getPluginListFile());
	}
	
	@Test
	public void testSetPluginListFile() {
		File file = new File("other_plugins.txt");
		PluginManager.setPluginListFile(file);
		assertEquals(file, PluginManager.getPluginListFile());
	}
	
	/**
	 * Reset everything to their default value. Keep this test up to date as to
	 * what "everything" is.
	 */
	@Test
	public void testReset() {
		PluginManager.setPluginListFile(listFile);
		PluginManager.refreshPluginList();
		File file = new File("other_plugins.txt");
		PluginManager.setPluginListFile(file);
		PluginManager.reset();
		assertEquals(new LinkedList<String>(), PluginManager.getPluginList());
		File default_file = new File("plugins.txt");
		assertEquals(default_file, PluginManager.getPluginListFile());
	}
	
	/**
	 * The returned list is the name of the various plugins.
	 * No list has been set before.
	 */
	@Test
	public void testGetPluginList_unsetList() {
		PluginManager.setPluginListFile(listFile);
		List<String> list = PluginManager.getPluginList();
		assertTrue(list.contains("dummyPlugin"));
	}
	
	/**
	 * 
	 */
	@Test
	public void testSetPluginList() {
		LinkedList<String> list = new LinkedList<String>();
		list.add("plug1");
		list.add("plug2");
		PluginManager.setPluginList(list);
		assertTrue(PluginManager.getPluginList().contains("plug1"));
		assertTrue(PluginManager.getPluginList().contains("plug2"));
	}
	
	/**
	 * Verify that the list gets reset.
	 */
	@Test
	public void testRefreshPluginList() {
		PluginManager.setPluginListFile(listFile);
		LinkedList<String> list = new LinkedList<String>();
		list.add("plug1");
		list.add("plug2");
		PluginManager.setPluginList(list);
		PluginManager.refreshPluginList();
		assertTrue(PluginManager.getPluginList().contains("dummyPlugin"));
		assertFalse(PluginManager.getPluginList().contains("plug1"));
		assertFalse(PluginManager.getPluginList().contains("plug2"));
	}
	
	/**
	 * Try to load a non existent plugin.
	 * @throws PluginException When the plugin is not on the list.
	 */
	@Test(expected=PluginException.class)
	public void testLoadPlugin_notOnList() throws PluginException {
		PluginManager.loadPlugin("noPlug");
	}
	
	/**
	 * The plugin is named in the list file, but isn't present in the folder.
	 * @throws PluginException When the plugin can't be found.
	 */
	@Test(expected=PluginException.class)
	public void testLoadPlugin_notInFolder() throws PluginException {
		PluginManager.loadPlugin("dummyPlugin");
	}
	
	/**
	 * Try to load an existing an correctly formed plugin.
	 * @throws PluginException
	 */
	@Test
	public void testLoadPlugin_OK() throws PluginException {
		PluginManager.setPluginListFile(listFile);
		PluginManager.refreshPluginList();
		PluginManager.loadPlugin("testPlugin");
		assertFalse(PluginManager.getLoadedPlugins().isEmpty());
		//System.out.println(testPlugin);
		//System.out.println(PluginManager.getLoadedPlugins().get(0));
		assertTrue(PluginManager.getLoadedPlugins().contains(testPlugin));
	}
	
	/**
	 * Note that we only check an OK case (for now).
	 * Also: make sure assertions are up to date.
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	@Test
	public void testExtractPlugin() throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(template);
		Element root = doc.getRootElement();
		
		Plugin plugin = PluginManager.extractPlugin(root);
		assertEquals("#name#", plugin.getName());
		assertEquals("#version-number#", plugin.getVersion());
		assertEquals("#file_extension#",
				plugin.getFileExtension());
		assertEquals("#regex#",
				plugin.getScriptRegex());
		assertEquals("#command-line#",
				plugin.getRunTestCmdLine());
		assertEquals("#another-command-line#",
				plugin.getRunTestScriptCmdLine());
	}
	
	/**
	 * Takes a String List as argument, and calls LoadPlugin on each on them, if
	 * their associated .xml file can be found. Returns a log.
	 */
	@Test
	public void testLoadAllPlugins() {
		PluginManager.setPluginListFile(listFile);
		PluginManager.loadAllPlugins();
		assertTrue(PluginManager.getLoadedPlugins().contains(testPlugin));
		assertTrue(PluginManager.getLoadedPlugins().contains(testPlugin2));
	}
	
	/**
	 * Checking the log.
	 */
	@Test
	public void testLoadAllPlugins_log() {
		PluginManager.setPluginListFile(listFile);
		String log = PluginManager.loadAllPlugins();
		assertTrue(log.contains("Error while loading plugin dummyPlugin"));
		assertTrue(log.contains("Loading testPlugin - done"));
		assertTrue(log.contains("Loading testPlugin2 - done"));
	}
	
	/**
	 * Check that we can have a list that contains all supported file extensions.
	 * Note that we don't check for duplicate file extensions.
	 */
	@Test
	public void testGetSupportedExtensions() {
		PluginManager.setPluginListFile(listFile);
		PluginManager.loadAllPlugins();
		List<String> list = PluginManager.getSupportedExtensions();
		assertTrue(list.contains("#file_extension#"));
	}
	
	
	//TODO:
	//runTest
	//runScript
	//--->need to write actual plugins
	//add load check? if file exist or something?
	//if so, need more stuff in Plugin + PluginImpl
	//else warning?
	/**
	 * Question: wtf do I check???
	 */
	@Test
	public void testRunTest() {
		//PluginManager.runTest("");
	}
}
