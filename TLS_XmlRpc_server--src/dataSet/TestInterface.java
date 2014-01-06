package dataSet;

import check.Check;

/**
 * Interface used by #{@link dataSet.DataSet} to loop through the test's data
 * set.
 * @author Adrien Droguet
 * @see #dataSet.DataSet
 */
public interface TestInterface {
	
	/**
	 * The test method that is going to be looped through.
	 * @param data
	 * @param check
	 * @throws Exception
	 */
	public void test(String data, Check check) throws Exception;
	
	/**
	 * Url of the data set file. Set automatically by the server's
	 * PreProcessor.
	 * @return
	 * @see #trace.PreProcessor
	 */
	public String getUrl();

}
