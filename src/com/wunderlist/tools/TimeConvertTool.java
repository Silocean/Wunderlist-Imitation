package com.wunderlist.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;

/**
 * 时间转换工具
 * @author Silocean
 *
 */
public class TimeConvertTool {
	
	@SuppressLint("SimpleDateFormat")
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
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
	 * 把截止日(2014-1-23 6:00:48)期转换成特定格式的截止日期显示(周四,2014.1.23)
	 * @param enddate
	 * @return
	 */
	public static String convertToSpecialEnddateStr(String enddate) {
		String str = "";
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
			str += enddate.split(" ")[0].replaceAll("-", ".");
			str += ",";
			str += enddate.split(" ")[1].split(":")[0] + ":"+ enddate.split(" ")[1].split(":")[1];
		} else {
			str = "";
		}
		return str;
	}
	
	/**
	 * 判断提醒提前量
	 * 
	 * @param enddate
	 * @param remindnum
	 * @param remindtype
	 * @return false表示截止日期减去提醒提前量在当前日期之前(提醒已过期)，true表示之后
	 */
	public static boolean judgeClock(String enddate, String remindnum, String remindtype) {
		Date date = TimeConvertTool.convertToDate(enddate);
		switch (Integer.parseInt(remindtype)) {
		case 0: {
			date.setTime(date.getTime() - (Integer.parseInt(remindnum) * 24 * 60 * 60 * 1000));
			break;
		}
		case 1: {
			date.setTime(date.getTime() - (Integer.parseInt(remindnum) * 60 * 60 * 1000));
			break;
		}
		case 2: {
			date.setTime(date.getTime() - (Integer.parseInt(remindnum) * 60 * 1000));
			break;
		}
		default:
			break;
		}
		if (date.before(new Date())) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * 根据截止日期和提醒提前量获取提醒时间
	 * @param enddate
	 * @param remindnum
	 * @param remindtype
	 * @return
	 */
	public static Date getClockTime(String enddate, String remindnum, String remindtype) {
		Date date = TimeConvertTool.convertToDate(enddate);
		switch (Integer.parseInt(remindtype)) {
		case 0: {
			date.setTime(date.getTime() - (Integer.parseInt(remindnum) * 24 * 60 * 60 * 1000));
			break;
		}
		case 1: {
			date.setTime(date.getTime() - (Integer.parseInt(remindnum) * 60 * 60 * 1000));
			break;
		}
		case 2: {
			date.setTime(date.getTime() - (Integer.parseInt(remindnum) * 60 * 1000));
			break;
		}
		default:
			break;
		}
		return date;
	}
	
	/**
	 * 和当前日期进行比较
	 * @param dateTime
	 * @return fasle表示比当前日期小，true表示比当前日期大
	 */
	public static boolean compareDate(String dateTime) {
		Date date = convertToDate(dateTime);
		if (date.before(new Date())) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * 和当前日期进行比较
	 * @param date
	 * @return fasle表示比当前日期小，true表示比当前日期大
	 */
	public static boolean compareDate(Date date) {
		if (date.before(new Date())) {
			return false;
		} else {
			return true;
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
			return time.split(" ")[0].substring(5);
		}
		return time.split(" ")[0];
	}
	
}
