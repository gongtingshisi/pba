package kg.gtss.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

import android.content.Context;
import android.text.format.DateUtils;
import android.text.format.Time;

/**
 * refer to Calendar/src/com/android/calendar/Utils.java
 * */
public class TimeUtils {
	final public static String DATE_FORMAT = "yyyy/MM/DD HH:mm:ss";
	public static final int MONDAY_BEFORE_JULIAN_EPOCH = Time.EPOCH_JULIAN_DAY - 3;

	/**
	 * get current date based special format "yyyy/MM/DD HH:mm:ss"
	 * */
	final public static String getCurrentTime() {
		/*
		 * SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT); Date
		 * date = new Date(System.currentTimeMillis());
		 * 
		 * // get current time return format.format(date);
		 */

		DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.FULL,
				DateFormat.SHORT);
		return formatter.format(new Date(System.currentTimeMillis()));
	}

	/**
	 * MM/dd/yyyy
	 * */
	public static String getSimpleDate() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("MM/dd/yyyy");
		return sDateFormat.format(new java.util.Date());
	}

	public static Date getSimpleDateFromString(String s) {
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		try {
			return (Date) format.parse(s);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new java.util.Date();
	}

	public static int getDateDayFromLongTime(long l) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(l);
		return c.get(Calendar.DAY_OF_MONTH);
	}

	public static String getSimpleDate(long time) {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("MM/dd/yyyy");
		return sDateFormat.format(time);
	}
}
