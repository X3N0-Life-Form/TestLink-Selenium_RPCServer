package utils;

import java.util.LinkedList;
import java.util.List;

/**
 * Contains various string-related operations.
 * @author Adrien Droguet
 * @version	1.1	25/04/2012<br>
 * 				Class created. Only contains getAllLines(String), used by the
 * 				class trace.PreProcessor.<br>
 */
public class MyStringUtils {
	
	/**
	 * Splits a String into as many sub strings as there are newlines in the
	 * original String. Note: each of the line in the list *end* with a newline
	 * char.
	 * @param string
	 * @return List containing every line.
	 */
	public static List<String> getAllLines(String string) {
		List<String> list = new LinkedList<String>();
		for (int i = 0; i < string.length(); i++) {
			if (string.charAt(i) == '\n') {
				list = getAllLines(string.substring(i + 1));
				list.add(0, string.substring(0, i));
				break;
			} else if (i + 1 == string.length()) {
				list.add(string);
			}
		}
		return list;
	}
	
	/**
	 * Removes any comment present on the specified line.
	 * @param line
	 * @return
	 */
	public static String removeComments(String line) {
		String res = "";
		boolean quote = false;
		boolean slash = false;
		boolean asterisk = false;
		for (char current : line.toCharArray()) {
			switch (current) {
				case '/': {
					if (!quote && !slash && !asterisk) {
						slash = true;
						continue;
					}else if (!quote && asterisk) {
						//-->*/
						//don't forget that "*/" hasn't been added to res
						res = line.substring(res.length() + 2);
						return res;
					} else if (!quote) {
						//-->//
						return res;
					}
					break;
				}
				case '*': {
					if (!quote && slash) {
						//-->/*
						return res;
					} else if (!quote) {
						asterisk = true;
						continue;
					}
					break;
				}
				case '"': {
					if (!quote) {
						quote = true;
					} else {
						quote = false;
					}
					break;
					//Note: quotes are added to the result
				}
			}
			res += current;
		}
		
		return res;
	}
	
	/**
	 * Escapes all '\' present in the specified String.
	 * @param string
	 * @return A modified version of the original String, in which every single
	 * '\' has been doubled.
	 */
	public static String escapeBackSlashes(String string) {
		String res = "";
		for (char current : string.toCharArray()) {
			if (current == '\\')
				res += '\\';
			res += current;
		}
		return res;
	}
	
	/**
	 * Escapes all '"' present in the specified String.
	 * @param string
	 * @return A modified version of the original String, in which every single
	 * '"' has been escaped.
	 */
	public static String escapeQuotes(String string) {
		String res = "";
		for (char current : string.toCharArray()) {
			if (current == '"')
				res += "\\\"";
			else
				res += current;
		}
		return res;
	}
	
	/**
	 * Reads a line and gets the value of a variable. Variables are identified
	 * by a '$' preceding their name. Example: getValueOf("$thing=it","thing")
	 * returns "it".
	 * @param line Line to analyze.
	 * @param toGet Variable to get the value of.
	 * @return Value got from the specified variable if it exists, null
	 * otherwise.
	 */
	public static String getValueOf(String line, String toGet) {
		String res = null;
		String pattern = "$" + toGet + "=";
		if (line.contains(pattern)) {
			res = "";
			String temp = "";
			boolean record = false;
			boolean quotes = false;
			for (char c : line.toCharArray()) {
				temp += c;
				if (record) {
					if (!quotes && c == '"')
						quotes = true;
					else if (quotes && c == '"')
						quotes = false;
					// recording ends at the first whitespace encountered.
					else if (!quotes && ("" + c).matches("\\s"))
						break;
					else
						res += c;
				} else if (temp.contains(pattern)) {
					record = true;
				}
			}
		}
		return res;
	}
}
