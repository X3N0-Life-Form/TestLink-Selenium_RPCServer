package utils;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	MyFileUtilsTest.class,
	MyStringUtilsTest.class,
	NowTest.class,
	PluginUtilsTest.class,
	ServerUtilsTest.class,
	ServerUtilsTestDealWithArgs.class
	})
public class AllTests {

}
