package com.minbingtuan.mywork.service;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpClientConnection;

import com.minbingtuan.mywork.Constants;
import com.minbingtuan.mywork.utils.Setting;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class UploadService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	//启动一个线程下载App
	class downRun implements Runnable{

		@Override
		public void run() {
			try {
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
			} catch (Exception e) {
			}
		}
		
	}
}
