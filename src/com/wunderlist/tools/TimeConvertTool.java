package com.wunderlist.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;

public class TimeConvertTool {
	
	@SuppressLint("SimpleDateFormat")
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	/**
	 * 日期转换成字符串
	 * @param date
	 * @return
	 */
	public static String convertToString(Date date) {
		return sdf.format(date);
	}
	
	/**
	 * 字符串转换成日期
	 * @param str
	 * @return
	 */
	public static Date convertToDate(String str) {
		try {
			return sdf.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/** 
	 * 把截止日(2014/1/23 6:00:48)期转换成特定格式的截止日期显示(周四,2014.1.23)
	 * @param enddate
	 * @return
	 */
	public static String convertToSpecialEnddateStr(String enddate) {
		String str = "";
		if(enddate.equals("1900/1/1 0:00:00")) {
			str = "";
		} else {
			if (!enddate.equals("")) {
				Calendar c = Calendar.getInstance();
				c.setTime(TimeConvertTool.convertToDate(enddate));
				int week = c.get(Calendar.DAY_OF_WEEK);
				switch (week) {
				case 1:
					str += "周日, ";
					break;
				case 2:
					str += "周一, ";
					break;
				case 3:
					str += "周二, ";
					break;
				case 4:
					str += "周三, ";
					break;
				case 5:
					str += "周四, ";
					break;
				case 6:
					str += "周五, ";
					break;
				case 7:
					str += "周六, ";
					break;
				default:
					break;
				}
				str += enddate.split(" ")[0].replaceAll("/", ".");
			} else {
				str = "";
			}
		}
		return str;
	}
	
	/**
	 * 和当前日期进行比较
	 * @param dateTime
	 * @return 0表示比当前日期小，1表示比当前日期大
	 */
	public static int compareDate(String dateTime) {
		Date date = convertToDate(dateTime);
		if (date.before(new Date())) {
			return 0;
		} else {
			return 1;
		}
	}
	
	/**
	 * 根据日期字符串计算距现在时间差
	 * @param time
	 */
	public static String calDateTime(String time) {
		Date date = convertToDate(time);
		long now = System.currentTimeMillis();
		long distance = now - date.getTime();
		long year = distance / (1000*60*60*24*365);
		long day = distance / (1000*60*60*24);
		long hour = distance / (1000*60*60);
		long minute = distance / (1000*60);
		long second = distance / 1000;
		if(year == 0) {
			if(day>7 && day<14) {
				return "一周前";
			} else if(day>0 && day<=7) {
				return day + "天前";
			} else if(day<=0 && hour>0) {
				return hour + "小时前";
			} else if(hour<=0 && minute>0) {
				return minute + "分钟前";
			} else if(minute<=0 && second>0) {
				return second + "秒前";
			} else if(second == 0) {
				return "刚刚";
			}
			return time.substring(5, 10);
		}
		return time.substring(0, 10);
	}
	
}
