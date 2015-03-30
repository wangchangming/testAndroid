package com.minbingtuan.mywork.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

	public static String getSystemDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());
		String str = formatter.format(curDate);
		return str;
	}

	public static String getgpsDate(long gpsTime) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		Date curDate = new Date(gpsTime);
		String str = formatter.format(curDate);
		return str;
	}

	public static String getDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date curDate = new Date(System.currentTimeMillis());
		String str = formatter.format(curDate);
		return str;
	}
	public static String getMonth() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
		Date curDate = new Date(System.currentTimeMillis());
		String str = formatter.format(curDate);
		return str;
	}
	public static String getTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());
		String str = formatter.format(curDate);
		return str;
	}

}