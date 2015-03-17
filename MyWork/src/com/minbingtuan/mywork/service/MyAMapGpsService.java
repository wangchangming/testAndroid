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
		aMapManager = LocationManagerProxy.getInstance(this);
		aMapManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, times, 10, mAMapLocationListener);
	}

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

	public void SendAMapGpsMessage(AMapLocation location) {
		Intent intent = new Intent();
		if (debug) {
			Double geoLat = location.getLatitude();
			Double geoLng = location.getLongitude();
			String cityCode = "";
			String desc = "";
			Bundle locBundle = location.getExtras();
			if (locBundle != null) {
				cityCode = locBundle.getString("citycode");
				desc = locBundle.getString("desc");
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
		// TODO Auto-generated method stub
		return null;
	}

	private BroadcastReceiver myAGPSBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			startGps(1000 * 60);
		}

	};

}