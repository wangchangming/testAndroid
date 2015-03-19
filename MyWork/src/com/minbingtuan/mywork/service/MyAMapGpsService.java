package com.minbingtuan.mywork.service;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.minbingtuan.mywork.MyApplication;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class MyAMapGpsService extends Service {
	//声明高德地图的服务管理类
	private LocationManagerProxy aMapManager;
	
	private boolean debug = false;
	public static String gpsIntentAction = "com.minbingtuan.mywork.service.MyAMapGpsService1";
	public static String gpsFerquencyAction = "com.minbingtuan.mywork.service.MyAMapGpsService2";

	private long myAGPSFerquency = -1;

	@Override
	public void onCreate() {
		super.onCreate();
		startGps(1000);
		IntentFilter intentFilter = new IntentFilter(MyAMapGpsService.gpsFerquencyAction);
		registerReceiver(myAGPSBroadcastReceiver, intentFilter);
	}

	public void startGps(long times) {
		if (myAGPSFerquency == times) {
			return;
		}
		myAGPSFerquency = times;
		aMapManager = LocationManagerProxy.getInstance(this);//创建高德地图管理服务类
		//Location API定位采用GPS定位方式，时间最短是times毫秒,即1000毫秒
		aMapManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, times, 10, mAMapLocationListener);
	}

	//重写AMapLocationListener监听的四个方法
	private AMapLocationListener mAMapLocationListener = new AMapLocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

		@Override
		public void onProviderEnabled(String provider) {

		}

		@Override
		public void onProviderDisabled(String provider) {

		}

		@Override
		public void onLocationChanged(Location location) {

		}

		@Override
		public void onLocationChanged(AMapLocation location) {
			if (location != null) {
				SendAMapGpsMessage(location);
			}
		}
	};

	
	/**
	 * 功能：取得地理位置信息，发送此信息
	 * @author wching
	 * @param location  当前地理位置信息
	 */
	public void SendAMapGpsMessage(AMapLocation location) {
		Intent intent = new Intent();
		if (debug) {
			//获取当前位置的纬度
			Double geoLat = location.getLatitude();
			//获取当前位置的经度
			Double geoLng = location.getLongitude();
			String cityCode = "";//当前城市的代码
			String desc = "";
			//把位置信息传入locBundle
			Bundle locBundle = location.getExtras();
			if (locBundle != null) {
				cityCode = locBundle.getString("citycode");//获取城市代码
				desc = locBundle.getString("desc");//获取？？？？？
			}

			float accurcy = location.getAccuracy();
			String provider = location.getProvider();
			long times = location.getTime();
			String province = location.getProvince();
			String city = location.getCity();
			String disctrict = location.getDistrict();
			String adCode = location.getAdCode();

			Log.d("whui", "loction geoLat=" + geoLat + " geoLng=" + geoLng + " cityCode=" + cityCode + " desc=" + desc
					+ " accurcy=" + accurcy + " provider=" + provider + " province=" + province + " city=" + city
					+ " dis=" + disctrict + " adcode=" + adCode);

		}
		MyApplication myApp = (MyApplication) getApplication();

		intent.putExtra("status", location == null ? false : true);
		if (location != null) {
			intent.putExtra("Latitude", location == null ? "" : location.getLatitude());
			intent.putExtra("Longitude", location == null ? "" : location.getLongitude());
			intent.putExtra("Times", location == null ? "" : location.getTime());

			Bundle locBundle = location.getExtras();
			String addr = null;
			if (locBundle != null) {
				addr = locBundle.getString("desc");
			} else {
				addr = location.getCity() + location.getDistrict();
			}
			intent.putExtra("addr", addr);
			
			//设置地理位置信息
			myApp.setGpsInfo(addr, location.getLongitude(), location.getLatitude());
		} else {
			myApp.setGpsInfo(null, 0, 0);
		}

		intent.setAction(gpsIntentAction);
		sendBroadcast(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(myAGPSBroadcastReceiver);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	private BroadcastReceiver myAGPSBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			startGps(1000 * 60);
		}

	};

}