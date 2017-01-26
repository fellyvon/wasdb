package com.waspring.wasdb.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 日期常量处理
 * 
 * @author felly
 * 
 */
public class DateUtil {

	private static final String simDateFormat = "yyyy-MM-dd";
	private static final int simDateFormatLength = simDateFormat.length();
	private static final String SimpleDateTimeFormat = "yyyy-MM-dd HH:mm:ss";
	private static final String BJ_TIMEZONE = "GMT+8"; // 北京时间标准时区

	/**
	 * 普通日期型转换sql日期
	 * 
	 * @param date
	 * @return
	 */
	public static java.sql.Date toSqlDate(java.util.Date date) {
		return new java.sql.Date(date.getTime());
	}

	/**
	 * 字符串转换为普通的日期
	 * 
	 * @param str
	 *            要转换的字符串,可以是日期，也可带分秒，但必须符合日期的格式
	 * @return 转换不成功不报错，返回null
	 */
	public static java.util.Date strToDate(String str) {
		if (null != str && str.length() > 0) {
			try {
				if (str.length() <= simDateFormatLength) { // 只包含日期。
					return (new SimpleDateFormat(simDateFormat)).parse(str);
				} else { // 包含日期时间
					return (new SimpleDateFormat(SimpleDateTimeFormat))
							.parse(str);
				}
			} catch (ParseException error) {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * 字符串转换为sql的日期
	 * 
	 * @param str
	 *            要转换的字符串
	 * @return 转换不成功返回null
	 */
	public static java.sql.Date strToSqlDate(String str) {
		if (strToDate(str) == null || str.length() < 1) {
			return null;
		} else {
			return new java.sql.Date(strToDate(str).getTime());
		}
	}

	public static String dateTimeToStr(Date date) {
		return dateTimeToStr(date, ' ');
	}

	public static String dateTimeToISOStr(Date date) {
		return dateTimeToStr(date, 'T');
	}

	public static String dateTimeToStr(Date date, char c) {
		if (c == ' ')
			return dateTimeToStr(date, '-', ' ', ':');
		else
			return dateTimeToStr(date, '\0', 'T', ':');
	}

	public static String dateTimeToStr(Date date, char c, char c1, char c2) {
		if (date == null)
			return null;
		StringBuffer stringbuffer = new StringBuffer(20);
		GregorianCalendar gregoriancalendar = new GregorianCalendar();
		gregoriancalendar.setTime(date);
		stringbuffer.append(gregoriancalendar.get(1));
		if (c != 0)
			stringbuffer.append(c);
		int i = gregoriancalendar.get(2) + 1;
		appendInt(stringbuffer, i);
		if (c != 0)
			stringbuffer.append(c);
		int j = gregoriancalendar.get(5);
		appendInt(stringbuffer, j);
		int k = gregoriancalendar.get(11);
		int l = gregoriancalendar.get(12);
		int i1 = gregoriancalendar.get(13);
		if (k + l + i1 > 0) {
			if (c1 != 0)
				stringbuffer.append(c1);
			appendInt(stringbuffer, k);
			if (c2 != 0)
				stringbuffer.append(c2);
			appendInt(stringbuffer, l);
			if (c2 != 0)
				stringbuffer.append(c2);
			appendInt(stringbuffer, i1);
		}
		return stringbuffer.toString();
	}

	private static void appendInt(StringBuffer stringbuffer, int i) {
		if (i < 10)
			stringbuffer.append("0");
		stringbuffer.append(i);
	}

}
