package com.warmer.base.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 日期函数类,实现对日期的一些处理.
 */

public class DateUtil {
	/**
	 * 构造器。
	 */
	public DateUtil() {
	}
	//
	public static String getFormatDateTime() {

		return formatChDate(DateUtil.getCurrentDate()) + " "
				+ formatChTime(DateUtil.getCurrentTime());
	}

	public static String getFormatDate() {
		return formatDate(getCurrentDate());
	}

	/**
	 * 返回显示给用户的中文格式的日期。 将yyyymmdd格式变为yyyy年mm月dd日 格式
	 */
	public static String formatChDate(String date) {
		try {
			return date.substring(0, 4) + "年" + date.substring(4, 6) + "月"
					+ date.substring(6, 8) + "日";
		} catch (Exception e) {
			return "";
		}
	}
	/**
	 * 返回显示给用户的中文格式的日期。 将yyyy年mm月dd日格式变为yyyymmdd格式
	 */
	public static String formatEnDate(String date) {
		try {
			return date.substring(0, 4) +  date.substring(5, 7)
			+ date.substring(8, 10);
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 返回显示给用户的中文格式的时间 即：将hhmm格式变为hh时mm分格式
	 * 
	 * @param time
	 *            日间串hhmm格式
	 * @return hh时mm分
	 */
	public static String formatChTime(String time) {
		return time.substring(0, 2) + "时" + time.substring(2, 4) + "分";
	}

	/**
	 * 返回显示给用户的中文格式的时间 即：将yyyymmdd hhmmss 格式为 yyyy年mm月dd日 hh时mm分ss秒 的格式
	 * 
	 * @param dateTime
	 *            时间日期字符串
	 * @return yyyy年mm月dd日 hh时mm分ss秒
	 */
	public static String formatChDateTime(String dateTime) {
		try {
			return dateTime.substring(0, 4) + "年" + dateTime.substring(4, 6)
					+ "月" + dateTime.substring(6, 8) + "日 "
					+ dateTime.substring(9, 11) + "时"
					+ dateTime.substring(11, 13) + "分"
					+ dateTime.substring(13, 15) + "秒";
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 返回显示给用户的格式的日期 即：将yyyymmdd格式变为yyyy-mm-dd格式
	 * 
	 * @param date 日期串 yyyymmdd格式
	 * @return yyyy-mm-dd格式
	 */
	public static String formatDate(String date) {
		if(date==null || date.length()<8){
			return "Error Date";
		}
		return date.substring(0, 4) + "-" + date.substring(4, 6) + "-"
				+ date.substring(6, 8);
	}

	/**
	 * 返回显示给用户的格式的日期 即：将mmdd格式变为mm-dd格式
	 * 
	 * @param date
	 *            日期串 mmdd格式
	 * @return mm-dd格式
	 */
	public static String formatDate2(String date) {
		return date.substring(0, 2) + "-" + date.substring(2, 4);
	}

	/**
	 * 返回显示给用户的格式的时间 即：将hhmm格式变为hh:mm格式
	 * 
	 * @param time
	 *            hhmm格式串
	 * @return hh:mm格式
	 */
	public static String formatTime(String time) {
		return time.substring(0, 2) + ":" + time.substring(2, 4);
	}

	/**
	 * 获取当前日期时间。
	 * 
	 * @return
	 */
	public static String getCurrentDateTime() {
		return getCurrentDate() + " " + getCurrentTime();
	}
	/**
	 * 返回当前日期 ，格式为2018年04月20日  10:34格式
	 * 
	 * @return
	 */
	public static String getNowDateHm() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm"); 
		long nowtime = System.currentTimeMillis();
		String datetime = sdf.format(nowtime);
		String date = datetime.substring(0, 8);
		String time = datetime.substring(8, 14);
		String newDate = formatChDate(date);
		return newDate+time;
	}
	 
	
	/**
	 * 返回当前日期 ，格式为yyyymmdd格式
	 * 
	 * @return
	 */
	public static String getCurrentDate() {
		long time = System.currentTimeMillis();
		Date date = new Date(time);
		int year = 1900 + date.getYear();
		int month = date.getMonth() + 1;
		int day = date.getDate();

		String ymd = String.valueOf(year);
		if (month < 10)
			ymd += "0";
		ymd += String.valueOf(month);
		if (day < 10)
			ymd += "0";
		ymd += String.valueOf(day);
		return ymd;
	}

	/**
	 * 返回当前时间 ，格式为hhmmss格式
	 * 
	 * @return
	 */
	public static String getCurrentTime() {
		Calendar cal = new GregorianCalendar();
		int hour = cal.get(Calendar.HOUR_OF_DAY); // 0..23
		int minute = cal.get(Calendar.MINUTE); // 0..59
		int second = cal.get(Calendar.SECOND); // 0..59
		String value = "";
		if (hour < 10) {
			value += "0" + hour;
		} else {
			value += hour;
		}
		if (minute < 10) {
			value += "0" + minute;
		} else {
			value += minute;
		}
		if (second < 10) {
			value += "0" + second;
		} else {
			value += second;
		}
		return value;

	}
	/**
	 * 返回要存到数据库里的日期字符串 本操作会对传入的日期进行验证 即：将yyyymmdd格式变为yyyymmdd格式
	 * 
	 * @param str
	 *            yyyymmdd格式串
	 * @return yyyymmdd格式
	 */
	public static String getDate(String str) {
		if (!isLegalDate(str))
			return null;
		return str.substring(0, 4) + str.substring(4, 6) + str.substring(6, 8);
	}

	/**
	 * 判断给定的字符串是否是一个合法的日期表示 本系统中的日期表示均为：YYYYMMDD。 ?表示任一字符
	 * 注：这个格式是用户输入和显示给用户的，实际存到数据库里时要去掉?
	 * 
	 * @param str
	 *            要进行处理的参数字符串
	 * @return boolean true or false
	 */
	public static boolean isLegalDate(String str) {
		String tmp = str.trim();
		// if(tmp.length()!=8) return false;
		try {
			int year = Integer.parseInt(tmp.substring(0, 4));
			if (year < 1900 || year > 3000)
				return false;
			int month = Integer.parseInt(tmp.substring(4, 6));
			if (month < 1 || month > 12)
				return false;
			int day = Integer.parseInt(tmp.substring(6, 8));
			if (day < 1)
				return false;
			if (month == 2) {
				if ((year % 400 == 0) || ((year % 4 == 0) && (year % 100 != 0))) {
					if (day > 29)
						return false;
				} else if (day > 28)
					return false;
			} else if (((month == 1) || (month == 3) || (month == 5)
					|| (month == 7) || (month == 8) || (month == 10) || (month == 12))
					&& day > 31)
				return false;
			else if (((month == 4) || (month == 6) || (month == 9) || (month == 11))
					&& day > 30)
				return false;
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 返回显示给用户的格式的日期 即：将yyyy-mm-dd格式变为yyyymmdd格式
	 * 
	 * @param date
	 *            yyyy-mm-dd格式串
	 * @return
	 */
	public static String unformatDate(String date) {
		if(null==date || "".equals(date)){

		}else{
			date = date.substring(0, 4) + date.substring(5, 7)
			+ date.substring(8, 10);
		}
		return date;
	}

	/**
	 * 传入一个日期,加上或减去一个数后,得到新的日期
	 * 
	 * @param psDate
	 *            String
	 * @param piValue
	 *            int
	 * @return String
	 */
	public static String getBeforeDate(String psDate, int piValue) {
		int year, month, day;
		String asDate;
		Calendar cl = Calendar.getInstance();
		year = Integer.valueOf(psDate.substring(0, 4)).intValue();
		cl.set(cl.YEAR, year);
		// Calendar的月份是从0开始的
		month = Integer.valueOf(psDate.substring(4, 6)).intValue() - 1;
		cl.set(cl.MONTH, month);
		day = Integer.valueOf(psDate.substring(6)).intValue();
		cl.set(cl.DAY_OF_MONTH, day);
		cl.add(cl.DATE, piValue);
		year = cl.get(cl.YEAR);
		month = cl.get(cl.MONDAY) + 1;
		day = cl.get(cl.DAY_OF_MONTH);
		asDate = String.valueOf(year);
		if (month < 10)
			asDate += "0" + String.valueOf(month);
		else
			asDate += String.valueOf(month);
		if (day < 10)
			asDate += "0" + String.valueOf(day);
		else
			asDate += String.valueOf(day);
		return asDate;
	}

	/**
	 * 取某个月的最后一天
	 * 
	 * @param psYear
	 *            String
	 * @param psMon
	 *            String
	 * @return String
	 */
	public static String getLastDay(String psYear, String psMon) {
		int year, month;
		year = Integer.valueOf(psYear).intValue();
		month = Integer.valueOf(psMon).intValue();
		if (month == 12) {
			year += 1;
			month = 0;
		}
		Calendar cl = Calendar.getInstance();
		cl.set(cl.YEAR, year);
		cl.set(cl.MONTH, month);
		cl.set(cl.DAY_OF_MONTH, 1);
		cl.add(cl.DATE, -1);
		return String.valueOf(cl.get(cl.DAY_OF_MONTH));
	}

	/**
	 * 输入两个日期，算之前相差多少天
	 * 
	 * @param psDate1
	 *            String 2008-09-09 OR 20080909
	 * @param psDate2
	 *            String 2008-09-09 OR 20080909
	 * @return int
	 */
	public static long getNumberOfDays(String psDate1, String psDate2) {
		psDate1 = psDate1.replace('-', '/');
		psDate2 = psDate2.replace('-', '/');
		try {

			psDate1 = psDate1.substring(0, 4) + "/" + psDate1.substring(4, 6)
					+ "/" + psDate1.substring(6, 8);
			psDate2 = psDate2.substring(0, 4) + "/" + psDate2.substring(4, 6)
					+ "/" + psDate2.substring(6, 8);

			Date dt1 = new Date(psDate1);
			Date dt2 = new Date(psDate2);
			long l = dt1.getTime() - dt2.getTime();
			l = l / 60 / 60 / 1000 / 24;
			return Math.abs(l);
		} catch (Exception e) {
			return -1;
		}

	}

	/**
	 * 算两个日期之间一共有几个月
	 * 
	 * @param qsrq
	 * @param jzrq
	 * @return
	 */
	public static Integer getNumOfMonths(String qsrq, String jzrq) {
		Integer qs = Integer.parseInt(qsrq.substring(0, 4)) * 12
				+ Integer.parseInt(qsrq.substring(4, 6));
		Integer jz = Integer.parseInt(jzrq.substring(0, 4)) * 12
				+ Integer.parseInt(jzrq.substring(4, 6));
		return jz - qs + 1;
	}

	/**
	 * 得到下个月 
	 * @param rq
	 * @return
	 */
	public static String getNextMonth(String rq) {
		Integer nf = Integer.parseInt(rq.substring(0, 4));
		Integer yf = Integer.parseInt(rq.substring(4, 6));
		yf = yf + 1;
		if (yf > 12) {
			yf = 1;
			nf = nf + 1;
		}
		return yf >= 10 ? nf.toString() + yf.toString() : nf.toString()
				+ "0" + yf.toString();
	}
	
	/**
	 * 通过两个日期（起始和截至）得到第几年
	 * @param args
	 */
	public static Integer getYear(String qsrq,String jzrq){
		int m = DateUtil.getNumOfMonths(qsrq, jzrq);
		int year = m%12==0?m/12:m/12+1;
		return year;
	}
	/**
	 * 得到中文格式 2011年 04月  
	 * @param date 201104
	 * @return
	 */
	public static String formatYearAndMonth(String date){
		return date.substring(0,4)+"年"+date.substring(4,6)+"月";
	}
	/**
	 * 获取现在时间
	 * 
	 * @return 返回时间类型 yyyy-MM-dd HH:mm:ss
	 */
	public static Date getDateNow() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		//ParsePosition pos = new ParsePosition(18);
		Date currentTime_2 = null;
		try {
			currentTime_2 = formatter.parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return currentTime_2;
	}
}
