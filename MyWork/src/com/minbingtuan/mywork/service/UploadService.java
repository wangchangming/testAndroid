package com.minbingtuan.mywork.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpClientConnection;

import com.minbingtuan.mywork.Constants;
import com.minbingtuan.mywork.utils.LogHelper;
import com.minbingtuan.mywork.utils.Setting;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class UploadService extends Service {

    private Thread thread;
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
        thread = new Thread(new downRun());
        thread.start();
        LogHelper.trace("启动service");
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
	    LogHelper.trace("关闭service");
		super.onDestroy();
	}

	//启动一个线程下载App
	class downRun implements Runnable{

		@Override
		public void run() {
			try {
			    LogHelper.trace("启动线程.....");
				//创建url
				URL url = new URL(Constants.apkUrl);
				//打开连接
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				//执行连接
				conn.connect();
				int length = conn.getContentLength();//返回的长度
				//打开输入流
				InputStream is = conn.getInputStream();
				
				//创建文件
				File file = new File(Constants.savePath);
				if(!file.exists()){
					file.mkdir();
				}
				
				String apkFile = Constants.saveFileName;
                File ApkFile = new File(apkFile);
                FileOutputStream fos = new FileOutputStream(ApkFile);
                
                byte buf[] = new byte[1024];
                
                do {
                    int numread = is.read(buf);
                    LogHelper.trace("下载中...");
                    if (numread <= 0) {
                        break;
                    }
                    fos.write(buf, 0, numread);
                } while (true);// 点击取消就停止下载.

                fos.close();
                is.close();
				
			} catch (Exception e) {
			}
			
            LogHelper.trace("下载完毕....");
            //发送广播
            Intent intent = new Intent();
            intent.setAction("com.minbingtuan.mywork.uploadApp");
            sendBroadcast(intent);
		}
		
	}

    public Object numread(int i) {
        // TODO Auto-generated method stub
        return null;
    }
}
