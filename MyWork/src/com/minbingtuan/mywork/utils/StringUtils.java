package com.minbingtuan.mywork.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.minbingtuan.mywork.MyApplication;
import com.minbingtuan.mywork.R;

import android.content.Context;
import android.text.TextUtils;

public class StringUtils {
	
	public static String userName = "";
	public static String password = "";
	
	/**
	 * 
	 * @param cTime
	 * @param format
	 *            "yyyy/MM/dd HH:mm"
	 * @return
	 */
	public static String FormatUnixTime(long cTime, String format) {
		Timestamp time = new Timestamp(cTime * 1000);
		String stime = "";
		SimpleDateFormat formatString = new SimpleDateFormat(format);
		stime = formatString.format(time);
		return stime;
	}
	
	/**
	 * 获取随机�?	 * 
	 * @return
	 */
	public static String getRandomString() {
		StringBuffer result = new StringBuffer();
		result.append(String.valueOf((int) (Math.random() * 10))).append("_");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String str = formatter.format(curDate);
		result.append(str);
		
		return result.toString();
	}
	
	/**
	 * �?��email
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String strEmail) {
		if (TextUtils.isEmpty(strEmail)) {
			return false;
		}
		
		String strPattern = "^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*$";
		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(strEmail);
		return m.matches();
	}
	
	/**
	 * �?��手机�?	 * 
	 * @param email
	 * @return
	 */
	public static boolean isTel(String content) {
		if (TextUtils.isEmpty(content)) {
			return false;
		}
		Pattern p = Pattern.compile("^((13[0-9])|(14[0-9])|(15[0-9])|(18[0-9]))\\d{8}$");
		Matcher m = p.matcher(content);
		if (!m.matches()) {
			return false;
		}
		return true;
	}
	public static boolean isIDcard(String content){
		if (TextUtils.isEmpty(content)) {
			return false;
		}
		Pattern p = Pattern.compile("^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{4}$");
		Matcher m = p.matcher(content);
		if (!m.matches()) {
			return false;
		}
		return true;
	}
	
	/**
	 * �?��密码(0-9a-zA-Z, 6~20)
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isPassword(String content) {
		if (TextUtils.isEmpty(content)) {
			return false;
		}
		Pattern p = Pattern.compile("^([0-9a-zA-Z])*$");
		Matcher m = p.matcher(content);
		if (!m.matches()) {
			return false;
		}
		p = Pattern.compile("[\\d\\D]{6,20}");
		m = p.matcher(content);
		if (!m.matches()) {
			return false;
		}
		return true;
	}
	
	public static boolean isAlias(String content, boolean cantNull) {
		
		if (!cantNull) {
			if (TextUtils.isEmpty(content)) {
				return false;
			}
		} else {
			if (TextUtils.isEmpty(content)) {
				return true;
			}
			Pattern p = Pattern.compile("^([\u4e00-\u9fa5]|[0-9a-zA-Z])+$");
			Matcher m = p.matcher(content);
			if (!m.matches()) {
				return false;
			}
		}
		return true;
		
	}
	
	/**
	 * MD5
	 * 
	 * @param input
	 * @return
	 */
	public static String md5(String input) {
		String result = input;
		if (input != null) {
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				md.update(input.getBytes());
				BigInteger hash = new BigInteger(1, md.digest());
				result = hash.toString(16);
				if ((result.length() % 2) != 0) {
					result = "0" + result;
				}
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * 将key-value形式的参数集转换成用&号链接的URL查询参数形式�?	 * ***生成请求时，不需要做encode
	 * @param parameters
	 *            key-value形式的参数集
	 * @return �?号链接的URL查询参数
	 */
	public static String encodeUrl(HashMap<String, String> parameters) {
		if (parameters == null) {
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Entry<String, String> entry : parameters.entrySet()) {
			if (first) {
				first = false;
			} else {
				if (TextUtils.isEmpty(entry.getKey())) {
					continue;
				} else {
					sb.append("&");
				}
			}
			String key = entry.getKey();
			String value = entry.getValue();
			if (value == null) {
				value = "";
			}
			sb.append(URLEncoder.encode(key)).append("=").append(URLEncoder.encode(value));
		}
		return sb.toString();
	}
	
	/**
     * ***生成请求时，不需要做encode
	 * @param value
	 * @return
	 */
	public static String encodeUrl(String value) {
		if (value == null) {
			return "";
		}
		String encoded = null;
		try {
			encoded = URLEncoder.encode(value, "UTF-8");
		} catch (UnsupportedEncodingException ignore) {
		}
		StringBuffer buf = new StringBuffer(encoded.length());
		char focus;
		for (int i = 0; i < encoded.length(); i++) {
			focus = encoded.charAt(i);
			if (focus == '*') {
				buf.append("%2A");
			} else if (focus == '+') {
				buf.append("%20");
			} else if (focus == '%' && (i + 1) < encoded.length() && encoded.charAt(i + 1) == '7' && encoded.charAt(i + 2) == 'E') {
				buf.append('~');
				i += 2;
			} else {
				buf.append(focus);
			}
		}
		return buf.toString();
	}
	
	/**
	 * 将用&号链接的URL参数转换成key-value形式的参数集
	 * 
	 * @param s
	 *            将用&号链接的URL参数
	 * @return key-value形式的参数集
	 */
	public static HashMap<String, String> decodeUrl(String s) {
		HashMap<String, String> params = new HashMap<String, String>();
		if (s != null) {
			String array[] = s.split("&");
			for (String parameter : array) {
				String v[] = parameter.split("=");
				if (v.length > 1) {
					params.put(v[0], URLDecoder.decode(v[1]));
				}
			}
		}
		return params;
	}
	
	/**
	 * 将URL中的查询串转换成key-value形式的参数集
	 * 
	 * @param url
	 *            待解析的url
	 * @return key-value形式的参数集
	 */
	public static HashMap<String, String> parseUrl(String url) {
		url = url.replace("/#", "?");
		try {
			URL u = new URL(url);
			HashMap<String, String> b = decodeUrl(u.getQuery());
			HashMap<String, String> ref = decodeUrl(u.getRef());
			if (ref != null)
				b.putAll(ref);
			return b;
		} catch (MalformedURLException e) {
			return new HashMap<String, String>();
		}
	}
	
	/**
	 * check if the string contains chinese character
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isContainChinese(String str) {
		if (str == null || str.trim().length() <= 0) {
			return false;
		}
		
		int len = str.length();
		for (int i = 0; i < len; i++) {
			char word = str.charAt(i);
			if ((word >= 0x4e00) && (word <= 0x9fbb)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * check if the string is int value
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isInteger(String str) {
		if (TextUtils.isEmpty(str)) {
			return false;
		}
		
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(str).matches();
	}
	
	/**
	 * check if the string is double or float
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isDouble(String str) {
		if (TextUtils.isEmpty(str)) {
			return false;
		}
		
		Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
		return pattern.matcher(str).matches();
	}
	/**
	 * 双击退出函数
	 */
	public static void exitBy2Click(Context context){
		Timer tExit = null;
        if (Setting.isExit == false) {
        	Setting.isExit = true; // 准备退出
        	LogHelper.toast(context, context.getString(R.string.exit_click_tip));
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                	Setting.isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            MyApplication.exitMe();
        }
	}
	
}
