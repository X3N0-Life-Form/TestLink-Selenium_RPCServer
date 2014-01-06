package trace;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	CheckpointRecorderTest.class, 
	PostProcessorTest.class,	
	PreProcessorTest.class 
		})
public class AllTests {

}
