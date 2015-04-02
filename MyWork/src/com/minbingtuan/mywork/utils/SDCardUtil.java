package com.minbingtuan.mywork.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

import com.minbingtuan.mywork.Constants;

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
    
    /**
     * 把用户名存储在SD卡中
     * @param str
     */
    public static void saveFile(String str) {  
        String filePath = null;  
        //判断SD卡是否存在
       // boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);  
        //if (hasSDCard) {  
            filePath = Constants.savePath + "name.txt";  
        //} else  
         //   filePath = Environment.getDownloadCacheDirectory().toString() + File.separator + "name.txt";  
        try {  
            File file = new File(filePath);  
            if (!file.exists()) {  
                File dir = new File(file.getParent());  
                dir.mkdirs();  
                file.createNewFile();  
            }  
            FileOutputStream outStream = new FileOutputStream(file);  
            outStream.write(str.getBytes());  
            outStream.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        } 
    }
    
	public static String getFile() {
		try {
			String filePath = Constants.savePath + "name.txt";
			File file = new File(filePath);
			FileInputStream fis=new FileInputStream(file);
			byte[] b = new byte[1024];
			int len=0;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while((len=fis.read(b))!=-1)
            {
				baos.write(b,0, len);
            }
			byte[] data=baos.toByteArray();
			baos.close();
			fis.close();
			return new String(data);
		} catch (Exception e) {
		}

		return null;
	}

}
