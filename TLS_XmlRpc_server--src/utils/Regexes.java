package utils;

/**
 * Contains various regexes and regex-related operations.
 * @author Adrien Droguet
 * @version 1.0	26/04/2012<br>
 */
public class Regexes {

	/**
	 * Regular expression that determines if a test script is valid.<br>
	 * @see #preProcessingRegexHead
	 */
	public static final String CLASS_DEFINITION_REGEX =
			"[\\s\\S]*[\\s]*" +					//anything can be before public 
			"public[\\s]+class" +				//public class
			"[\\s]+[\\S]+" +					//ClassName
			"([\\s]+extends[\\s]+[\\w]+)?" +	//extends whitespace word 
			"([\\s]+implements[\\s]+[\\w]+" +	//implements whitespace word
			"(,[\\s]*[\\w]+)*)?" +				//, word	
			"[\\s]*[{]" +						//{
			"[\\s\\S]*";						//anything afterwards

	
	
	//TO-DO: this part needs proper testing. - not stable
	/**
	 * Regex used by the PreProcessor to add checkpoints into the original
	 * script. Must always match CLASS_DEFINITION_REGEX.<br>
	 * FINAL part are to match their CLASS_DEFINITION_REGEX equivalent.
	 */
	private static String preProcessingRegexHead = 
			"[\\s\\S]*[\\s]*" +					//
			"import trace.CheckPointRecorder;" +//this package must be present
			"\\s*^(package)\\s+\\S+" +
			"[\\s\\S]*[\\s]*" +					//FINAL 
			"public[\\s]+class" +				//FINAL
			"[\\s]+[\\S]+" +					//FINAL
			"([\\s]+extends[\\s]+[\\w]+)?" +	//FINAL 
			"([\\s]+implements[\\s]+[\\w]+" +	//FINAL
			"(,[\\s]*[\\w]+)*)?" +				//FINAL	
			"[\\s]*[{]" +						//FINAL
			"[\\s\\S]*";						//FINAL
	@SuppressWarnings("unused")
	private static final String preProcessingRegexTail =
			"@AfterClass\\s+public\\s+[\\S\\s]+{" +
			"[\\S\\s]*cr.endLog();[\\S\\s]*" +
			"}";
	/**
	 * 
	 * @return
	 */
	public static String getPreProcessingRegex() {
		return preProcessingRegexHead;//
		//return preProcessingRegexHead + preProcessingRegexTail;
	}
	/**
	 * Must always match CLASS_DEFINITION_REGEX. Otherwise the pre-processing
	 * regex remains unchanged.
	 * @param preProcessingRegex
	 */
	public static void setPreProcessingRegex(String preProcessingRegex) {
		if (preProcessingRegex.matches(CLASS_DEFINITION_REGEX))
			Regexes.preProcessingRegexHead = preProcessingRegex;
	}
	/**
	 * Adds a new keyword to the regex.
	 * @param keyword
	 * @deprecated unstable
	 */
	public static void addPreProcessingKeyword(String keyword) {
		preProcessingRegexHead += "(cr.addToLog(" + keyword + "\\S*" + ");\\s*" +
				"\\s*" + keyword + "\\S*)*";
		//check regex validity before the end?
	}
}
