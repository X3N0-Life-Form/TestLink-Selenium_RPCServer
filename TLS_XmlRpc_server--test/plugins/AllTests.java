package plugins;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	//PluginTest is abstract
	PluginImplTest.class,
	PluginManagerTest.class
	})
public class AllTests {

}
