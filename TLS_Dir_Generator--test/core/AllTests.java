package core;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
			ArgAnalyzerTest.class,
			DirectoryStructureGeneratorTest.class,
			EnvInfoTest.class,
			PackageAnalyzerTest.class,
			BuildXMLGeneratorTest.class
			})
public class AllTests {

}
