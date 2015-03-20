package com.minbingtuan.mywork.utils;

import android.util.Log;

public class LogHelper {

	private static String TAG = "MinBingTuan";

	private static final String CLASS_METHOD_LINE_FORMAT = "%s.%s()_%d";

	public static void trace(String str) {
		StackTraceElement traceElement = Thread.currentThread().getStackTrace()[3];
		String className = traceElement.getClassName();
		className = className.substring(className.lastIndexOf(".") + 1);
		String logcat = String.format(CLASS_METHOD_LINE_FORMAT, className,
				traceElement.getMethodName(), traceElement.getLineNumber());
		Log.i(TAG, logcat+ "-->" +str);
	}
}
