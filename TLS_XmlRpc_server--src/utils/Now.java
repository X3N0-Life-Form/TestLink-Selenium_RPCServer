package utils;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Get NOW. As in "I don't want to pollute my code with Calendar non-sense.".
 * @author Adrien Droguet
 * @version 1.0 5/04/2012<br>
 * 				Class created.<br>
 * @version	1.1	16/05/2012<br>
 * 				Fixed month number.<br>
 */
public class Now {

	/**
	 * Gets the time.
	 * @return dd/mm/yyyy - hh:mm:ss - xxxx ms
	 */
	public static String get() {
		Calendar c = Calendar.getInstance();
		String NOW = c.get(Calendar.DAY_OF_MONTH) + "/"
				+ (c.get(Calendar.MONTH) + 1) + "/"		//JANUARY == 0
				+ c.get(Calendar.YEAR) + " - "
				+ c.get(Calendar.HOUR_OF_DAY) + ":"
				+ getMinutes(c) + ":"
				+ getSecondes(c) + " - "
				+ c.get(Calendar.MILLISECOND) + " ms"
				;
		return NOW;
	}

	/**
	 * Puts a nice little 0 if minutes < 10. 
	 * @param c
	 * @return Minutes.
	 */
	private static String getMinutes(Calendar c) {
		if (c.get(Calendar.MINUTE) < 10)
			return "0" + c.get(Calendar.MINUTE);
		else
			return "" + c.get(Calendar.MINUTE);
	}

	/**
	 * Same idea, but with seconds.
	 * @param c
	 * @return Seconds.
	 */
	private static String getSecondes(Calendar c) {
		if (c.get(Calendar.SECOND) < 10)
			return "0" + c.get(Calendar.SECOND);
		else
			return "" + c.get(Calendar.SECOND);
	}

	/**
	 * Now.
	 * @return SQL TimeStamp
	 */
	public static java.sql.Timestamp getTimeStamp() {
		Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
		return timeStamp;
	}
}
