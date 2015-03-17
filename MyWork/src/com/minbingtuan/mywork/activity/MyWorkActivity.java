package com.minbingtuan.mywork.activity;

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
import com.minbingtuan.mywork.service.MyAMapGpsService;
import com.minbingtuan.mywork.utils.DateUtils;
import com.minbingtuan.mywork.utils.StringUtils;
import com.minbingtuan.mywork.utils.VolleyErrorHelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class MyWorkActivity extends Activity implements OnClickListener {
	IntentFilter intentFilter;
	Intent intent;

	private RadioButton buttonSearch;
	private RadioButton buttonSetting;
	private RadioGroup mRadioGroup;

	private ImageButton buttonWorkOn;
	private ImageButton buttonWorkOff;

	private TextView mTextTime;
	private TextView mLocation;
	private TextView mUserName;
	private TextView Date;

	private MyApplication myApp;

	private int curCheckId = R.id.buttonWork;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mywork);
		myApp = (MyApplication) getApplication();
		//发送Http请求，从服务器获取签到数据
		HttpGetSearchRecord();

		RelativeLayout titleLayout = (RelativeLayout) findViewById(R.id.layoutTitle);
		buttonSearch = (RadioButton) titleLayout.findViewById(R.id.buttonSearch);
		buttonSetting = (RadioButton) titleLayout.findViewById(R.id.buttonSetting);
		mRadioGroup = (RadioGroup) titleLayout.findViewById(R.id.main_radio);
		buttonWorkOn = (ImageButton) findViewById(R.id.ButtonWorkOn);
		buttonWorkOff = (ImageButton) findViewById(R.id.ButtonWorkOff);
		mTextTime = (TextView) findViewById(R.id.TextView08);
		mLocation = (TextView) findViewById(R.id.textView1);
		mUserName = (TextView) findViewById(R.id.TextViewName);
		Date = (TextView) findViewById(R.id.TextViewDate);
		TextPaint tp = mUserName.getPaint();
		tp.setFakeBoldText(true);

		mRadioGroup.check(curCheckId);

		MyApplication mApp = (MyApplication) getApplication();
		mUserName.setText(mApp.getRealName().equals("null") ? "" : mApp.getRealName());
		mTextTime.setText("");
		Date.setText(DateUtils.getDate());
		buttonSearch.setOnClickListener(this);
		buttonSetting.setOnClickListener(this);
		buttonWorkOn.setOnClickListener(this);
		buttonWorkOff.setOnClickListener(this);

		myHandler.sendEmptyMessage(TIME_UPDATE_UI);
	}

	public void SetGpsFerquency() {
		Intent intent = new Intent(MyAMapGpsService.gpsFerquencyAction);
		sendBroadcast(intent);
	}

	public void HttpGetRequestRegistration(final int type) {

		RequestQueue queue = Volley.newRequestQueue(this);
		HashMap<String, String> params = new HashMap<String, String>();
		MyApplication myApp = (MyApplication) getApplication();
		params.put("managerId", Integer.toString(myApp.getUserId()));
		params.put("type", Integer.toString(type));
		params.put("time", DateUtils.getSystemDate());
		params.put("longitude", Double.toString(myApp.getGpsLongitude()));
		params.put("latitude", Double.toString(myApp.getGpsLatitude()));
		params.put("position", myApp.getGpsAddr());
		String url = MyApplication.LocalTIMEUrl;
		url += StringUtils.encodeUrl(params);
		JsonObjectRequest jsObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						int status = response.optInt("status");
						if (status == 0) {
							Toast.makeText(getApplicationContext(), getString(R.string.Registration_Success),
									Toast.LENGTH_SHORT).show();
							if (type == 1) {
								buttonWorkOn.setClickable(false);
								buttonWorkOn.setImageResource(R.drawable.buttonqiandao);
							}
							if (type == 2) {
								buttonWorkOff.setClickable(false);
								buttonWorkOff.setImageResource(R.drawable.buttonqiandao);
							}
						}
						if (status < 0) {
							Toast.makeText(getApplicationContext(), getString(R.string.Registration_Failed),
									Toast.LENGTH_SHORT).show();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(getApplicationContext(),
								VolleyErrorHelper.handleServerError(error, getApplication()), Toast.LENGTH_SHORT)
								.show();
					}

				});
		queue.add(jsObjectRequest);
	}

	public void HttpGetSearchRecord() {
		RequestQueue queue = Volley.newRequestQueue(this);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("managerId", Integer.toString(myApp.getUserId()));
		params.put("today", DateUtils.getDate());
		String url = MyApplication.localSEARCHREAORD;
		url += StringUtils.encodeUrl(params);

		JsonObjectRequest jsObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						int type1 = response.optInt("type1");
						int type2 = response.optInt("type2");
						if (type1 == 0) {
							buttonWorkOn.setClickable(false);
							buttonWorkOn.setImageResource(R.drawable.buttonqiandao);
						}
						if (type2 == 0) {
							buttonWorkOff.setClickable(false);
							buttonWorkOff.setImageResource(R.drawable.buttonqiandao);
						}
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(getApplicationContext(),
								VolleyErrorHelper.handleServerError(error, getApplication()), Toast.LENGTH_SHORT)
								.show();
					}

				});
		queue.add(jsObjectRequest);
	}

	public final int TIME_UPDATE_UI = 1000;

	public Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case TIME_UPDATE_UI:
				myHandler.removeMessages(TIME_UPDATE_UI);
				mTextTime.setText(DateUtils.getSystemDate());
				mLocation.setText(myApp.getGpsAddr());
				myHandler.sendEmptyMessageDelayed(TIME_UPDATE_UI, 30 * 1000);
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		myHandler.removeMessages(TIME_UPDATE_UI);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.buttonSearch:
			intent = new Intent(MyWorkActivity.this, MySearchActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.buttonSetting:
			intent = new Intent(MyWorkActivity.this, MySettingActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.ButtonWorkOn:
			if (!myApp.isConnect()) {
				Toast.makeText(getApplicationContext(), getString(R.string.NetError), Toast.LENGTH_SHORT).show();
				return;
			}
			HttpGetRequestRegistration(1);
			((MyApplication) getApplication()).startPositonService();
			SetGpsFerquency();
			break;
		case R.id.ButtonWorkOff:
			if (!myApp.isConnect()) {
				Toast.makeText(getApplicationContext(), getString(R.string.NetError), Toast.LENGTH_SHORT).show();
				return;
			}
			HttpGetRequestRegistration(2);
			((MyApplication) getApplication()).stopPositonService();
			break;

		default:
			break;
		}
	}

}
