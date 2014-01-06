package trace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import utils.MyFileUtils;
import utils.MyStringUtils;
import exceptions.TraceException;

/**
 * This class contains tools to retrieve a CheckPointRecorder object, process
 * its log and include it into an existing xml-rpc result HashMap.
 * @author Adrien Droguet
 * @version 1.0 25/04/2012<br>
 * 				Class created.<br>
 * @version	1.1	2/05/2012<br>
 * 				
 */
public class PostProcessor {
	
	/**
	 * 
	 * @param res Result HashMap
	 * @return Post Processing log.
	 * @throws TraceException If an error occurred when retrieving the
	 * CheckPointRecorder's log.
	 */
	public static String postProcess(String name, HashMap<Object, Object> res)
			throws TraceException {
		String log = "\n+++++ BEGIN TEST NOTES PROCESSING +++++";
		String notes = (String) res.get("notes");
		String processedNotes = "";
		List<String> lines = MyStringUtils.getAllLines(notes);
		
		//log += "\nRetrieving " + name + " CheckPointRecorder";
		//CheckPointRecorder.setCheckPointRecordTxt(ExecutorSetup.getReportURL());
		//CheckPointRecorder cr = CheckPointRecorder.getRecorder(name);
		log += "\nInserting Checkpoint log";
		
		processedNotes += "CheckPoint log:\n";
		//File cpLog = new File(ExecutorSetup.getURLMainTestFolder() + "myLog.txt");
		File checkPointLog = new File("D:\\myLog.txt");
		
		try {
			BufferedReader br = MyFileUtils.getBufferedReader(checkPointLog);
			String line = "";
			//TODO: add support for blocked status
			//if ()
			//	
			//else
			res.put("result", "p");
			while ((line = br.readLine()) != null) {
				processedNotes += "\n" + line;
				if (line.contains("Résultat: false")) {
					res.put("result", "f");
				}
			}
			br.close();
			checkPointLog.delete();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//TODO:reuse this part
		for (String line : lines) {
			processedNotes += "\n";
			if (line.contains("TestCase:")) {
				processedNotes += "JUnit report:" + line;
				//processedNotes += "Checkpoint report:\n";
				//processedNotes += cr.getLog();
			} else {
				processedNotes += line;
			}
		}
		
		//CheckPointRecorder.removeRecorder(name);
		
		res.put("notes", processedNotes);
		
		
		
		log += "\n+++++ END TEST NOTES PROCESSING +++++";
		return log;
	}
}
