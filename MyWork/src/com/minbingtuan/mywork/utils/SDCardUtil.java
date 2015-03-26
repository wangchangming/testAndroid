package com.minbingtuan.mywork.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author wching
 *
 */
public class SDCardUtil {

	/**
	 * 获取手机的物理地址
     * 错误返回12个0
     */
    public static String getMacAddress(Context context) {
        // 获取mac地址：
        String macAddress = "000000000000";
        try {
            WifiManager wifiMgr = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = (null == wifiMgr ? null : wifiMgr
                    .getConnectionInfo());
            if (null != info) {
                if (!TextUtils.isEmpty(info.getMacAddress()))
                    macAddress = info.getMacAddress().replace(":", "");
                else
                    return macAddress;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return macAddress;
        }
        return macAddress;
    }
    
    
    /**
     * 获取手机MIEI
     * @param context
     * @return
     * 参考网址：http://www.cnblogs.com/fly_binbin/archive/2010/12/09/1901612.html
     */
    public static String getMIEI(Context context){
    	

    	 TelephonyManager telephonemanage = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);              

    	   try        {     

    	    return telephonemanage.getDeviceId();      

    	   }    

    	     catch(Exception e)        {     

    	    Log.i("error", e.getMessage());  

    	       }
    	return "";
    }

}
