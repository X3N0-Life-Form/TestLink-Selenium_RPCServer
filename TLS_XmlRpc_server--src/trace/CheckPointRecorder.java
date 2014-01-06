package trace;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

import utils.Now;
import exceptions.TraceException;

/**
 * Records checkpoints
 * @author Adrien Droguet
 * @version 1.0 25/04/2012<br>
 * 				Class created.<br>
 * @deprecated Use check.Check based methods instead.
 */
public class CheckPointRecorder implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4254789718321565041L;
	public static transient final String DEFAULT_CHECKPOINT_RECORD_FILE_NAME =
			"checkpointRecord";
	private static transient String checkPointRecordTxt = "insertReportURLhere/"
			+ DEFAULT_CHECKPOINT_RECORD_FILE_NAME;
	public static String getCheckPointRecordTxt() {
		return checkPointRecordTxt;
	}
	/**
	 * Sets where the log file is supposed to be saved.
	 * @param url
	 */
	public static void setCheckPointRecordTxt(String url) {
		checkPointRecordTxt = url;
	}
	/**
	 * 
	 */
	private static HashMap<String, CheckPointRecorder> allRecorders = 
			new HashMap<String, CheckPointRecorder>();
	public static HashMap<String, CheckPointRecorder> getAllRecorders() {
		return allRecorders;
	}
	
	//TODO:make this throw an exception
	/**
	 * Looks for a specific CheckpointRecorder.
	 * @param className Name to look for.
	 * @return A CheckPointRecorder with the specified name.
	 * @throws TraceException If the CheckPointRecorder could not be found.
	 */
	public static CheckPointRecorder getRecorder(String className)
			throws TraceException {
		CheckPointRecorder cr = allRecorders.get(className);
		try {
			if (cr == null) {
				FileInputStream fis =
						new FileInputStream(checkPointRecordTxt + className);
				ObjectInputStream ois = new ObjectInputStream(fis);
				cr = (CheckPointRecorder) ois.readObject();
				ois.close();
			}
		} catch (FileNotFoundException e) {
			throw new TraceException(checkPointRecordTxt + className +
					" could not be found.");
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} 
		return cr;
	}
	
	/**
	 * Removes the specified CheckpointRecorder
	 * @param className
	 */
	public static void removeRecorder(String className) {
		allRecorders.remove(className);
	}
	
	private String log = "";
	public String getLog() {
		return log;
	}
	public void setLog(String log) {
		this.log = log;
	}
	
	private String className;
	public String getClassName() {
		return className;
	}
	
	/**
	 * Creates a new recorder for the specified class.
	 * @param className This is the recorder's key.
	 */
	public CheckPointRecorder(String className) {
		log = "Log recording begins at " + Now.get() + ".\n";
		this.className = className;
		allRecorders.put(className, this);
	}
	
	/**
	 * Adds the specified String to the log. Note: the added String is put on a
	 * new line.
	 * @param toAdd Line to add to the log.
	 */
	public void addToLog(String toAdd) {
		log += "\n" + Now.get() + " | " + toAdd;
	}
	
	/**
	 * Adds " - OK" at the end of the log.
	 */
	public void checkOK() {
		log += " - OK";
	}
	
	/**
	 * Adds an end message + a time stamp. Then serializes into a file the 
	 * CheckPointRecorder Object.
	 */
	public void endLog() {
		log += "\nLog ends at " + Now.get() + ".";
		try {
			FileOutputStream fos =
					new FileOutputStream(checkPointRecordTxt + className);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this);
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((className == null) ? 0 : className.hashCode());
		result = prime * result + ((log == null) ? 0 : log.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CheckPointRecorder other = (CheckPointRecorder) obj;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		if (log == null) {
			if (other.log != null)
				return false;
		} else if (!log.equals(other.log))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "CheckPointRecorder [log=" + log + ", className=" + className
				+ "]";
	}
	
}
