package server;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	ResultRetrieverTest.class,
	TestExecutorTest.class,
	ExecutorSetupTest.class,
	TestExecutorTestRegex.class,
	SeleniumServerRunnerTest.class,
	ServerSetupTest.class,
	TLSJavaServerTest.class
	})
public class AllTests {

}
