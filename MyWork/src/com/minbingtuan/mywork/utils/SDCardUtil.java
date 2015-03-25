package com.minbingtuan.mywork.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

import android.os.Environment;

/**
 * 把文件存入SD卡中
 * @author zjlong
 *
 */
public class SDCardUtil {

	public static void writeSD(String str,String path){
		 try {
	            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
	                //获取SD卡的目录
	                File sdCardDir = Environment.getExternalStorageDirectory();
	                File targetFile = new File(sdCardDir.getCanonicalPath() + path);
	                //以指定文件创建RandomAccessFile对象
	                RandomAccessFile raf = new RandomAccessFile(targetFile, "rw");
	                //将文件记录指针移动到最后
	                raf.seek(targetFile.length());
	                //输出文件内容
	                raf.write(str.getBytes());
	                raf.close();
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	}
	public static String readSD(String path){
		try {
            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                //获得SD卡对应的存储目录
                File sdCardDir = Environment.getExternalStorageDirectory();
                //获取指定文件对应的输入流
                FileInputStream fis = new FileInputStream(sdCardDir.getCanonicalPath() + path);
                //将指定输入流包装成BufferReader
                BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                StringBuilder sb = new StringBuilder("");
                String line = null;
                //循环读取文件内容
                while((line = br.readLine()) != null){
                    sb.append(line);
                }
                br.close();
                return sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
