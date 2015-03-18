package com.minbingtuan.mywork.service;

import java.util.HashMap;

import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.minbingtuan.mywork.MyApplication;
import com.minbingtuan.mywork.R;
import com.minbingtuan.mywork.utils.DateUtils;
import com.minbingtuan.mywork.utils.StringUtils;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class MyPositonService extends Service {
	private final static String TAG = "positonService";

	private final int TIME_REPORT_POSITION_ID = 1000;
	private final int TIME_REPORT_POSITON_DELAY = 1800000;// 30*60*1000;

	private String mLatitude;
	private String mLongitude;
	private String mAddr;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate");
		super.onCreate();
		IntentFilter intentFilter = new IntentFilter(MyAMapGpsService.gpsIntentAction);
		registerReceiver(myGPSBroadcastReceiver, intentFilter);
		myHandler.sendEmptyMessageDelayed(TIME_REPORT_POSITION_ID, TIME_REPORT_POSITON_DELAY);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy");
		this.unregisterReceiver(myGPSBroadcastReceiver);
		myHandler.removeMessages(TIME_REPORT_POSITION_ID);
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.i(TAG, "onStart");
		super.onStart(intent, startId);
	}

	public Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case TIME_REPORT_POSITION_ID:
				myHandler.removeMessages(TIME_REPORT_POSITION_ID);
				ReportPositon();
				myHandler.sendEmptyMessageDelayed(TIME_REPORT_POSITION_ID, TIME_REPORT_POSITON_DELAY);
				break;
			default:
				break;
			}
		}
	};

	private void ReportPositon() {
		RequestQueue queue = Volley.newRequestQueue(this);
		HashMap<String, String> params = new HashMap<String, String>();
		MyApplication myApp = (MyApplication) getApplication();

		params.put("inputId", Integer.toString(myApp.getUserId()));
		params.put("inputType", "5");
		params.put("inputTime", DateUtils.getSystemDate());
		params.put("inputLongitude", mLongitude);
		params.put("inputLatitude", mLatitude);
		params.put("inputPosition", mAddr);
		String url = MyApplication.LocalTIMEUrl;
		url += StringUtils.encodeUrl(params);
		JsonObjectRequest jsObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {

					}

				});
		queue.add(jsObjectRequest);
	}

	private BroadcastReceiver myGPSBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			boolean status = intent.getExtras().getBoolean("status");
			if (status) {
				mLatitude = Double.toString(intent.getExtras().getDouble("Latitude"));
				mLongitude = Double.toString(intent.getExtras().getDouble("Longitude"));
				// String mDate =
				// DateUtils.getgpsDate(intent.getExtras().getLong(
				// "Time"));
				mAddr = intent.getExtras().getString("addr");
			}
		}

	};

}
