package com.minbingtuan.mywork.activity;

import java.util.HashMap;

import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.minbingtuan.mywork.Constants;
import com.minbingtuan.mywork.MyApplication;
import com.minbingtuan.mywork.R;
import com.minbingtuan.mywork.service.MyAMapGpsService;
import com.minbingtuan.mywork.utils.DateUtils;
import com.minbingtuan.mywork.utils.LogHelper;
import com.minbingtuan.mywork.utils.Setting;
import com.minbingtuan.mywork.utils.StringUtils;
import com.minbingtuan.mywork.utils.VolleyErrorHelper;
import com.minbingtuan.mywork.view.CustomProgress;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.KeyEvent;
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
	private TextView amDate;
	private TextView pmDate;
	private MyApplication myApp;
	private int curCheckId = R.id.buttonWork;
	private SharedPreferences shared;
	SharedPreferences shareUserInfo;
	private int userID;
	private CustomProgress dialog;
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mywork);
		
		//获取数据对话框
		dialog = CustomProgress.show(MyWorkActivity.this, getString(R.string.getData), true, null);
		
		shared = getSharedPreferences("sign_message", Activity.MODE_PRIVATE);
		shareUserInfo = getSharedPreferences("userInfo", Activity.MODE_WORLD_WRITEABLE);
		Setting.autoLogin = shareUserInfo.getBoolean("autoLogin", false);
		myApp = (MyApplication) getApplication();


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
		amDate = (TextView) findViewById(R.id.TextView04);
		pmDate = (TextView) findViewById(R.id.TextView07);
		TextPaint tp = mUserName.getPaint();
		tp.setFakeBoldText(true);

		mRadioGroup.check(curCheckId);

		if(Setting.autoLogin){
			//从缓存中取出用户名
			String userName = shareUserInfo.getString("uRealName", "");
			userID = shareUserInfo.getInt("uId", 0);
			mUserName.setText(userName.equals("null")? "" : userName);
		}else{
			//从Application中取出用户名
			userID = myApp.getUserId();//获取用户ID号
			mUserName.setText(myApp.getRealName().equals("null") ? "" : myApp.getRealName());
		}
		Date.setText(DateUtils.getDate());
		buttonSearch.setOnClickListener(this);
		buttonSetting.setOnClickListener(this);
		buttonWorkOn.setOnClickListener(this);
		buttonWorkOff.setOnClickListener(this);
		

		//这里activity每次进入都得重新调用一次服务器
		HttpGetSearchRecord();
		
		//从本地存储查询当天的签到信息
		//ReadSignDate();

		//每隔30秒更新一次UI主界面
		myHandler.sendEmptyMessage(TIME_UPDATE_UI);
	}

	
	/**
	 * 这里调用存储数据，获取签到记录
	 */
//	public void ReadSignDate(){
//		String today = shared.getString("today", DateUtils.getDate());
//		String ID = shared.getString("userId", Integer.toString(userID));
//		String amSharedDate = shared.getString("amTime", "2015.03.19 09:00:35");
//		String pmSharedDate = shared.getString("pmTime", "2015.03.19 18:16:35");
//		if(today.equals(DateUtils.getDate())&&ID.equals(Integer.toString(userID))){//如果今天有签到信息
//			//判断上午是否签到
//			if(!"".equals(amSharedDate)){//如果上午已经签到
//				buttonWorkOn.setClickable(false);
//				buttonWorkOn.setImageResource(R.drawable.buttonqiandao);
//				amDate.setText(amSharedDate);
//			}
//			if(!"".equals(pmSharedDate)){//如果下午已经签到
//				buttonWorkOff.setClickable(false);
//				buttonWorkOff.setImageResource(R.drawable.buttonqiandao);
//				pmDate.setText(pmSharedDate);
//			}
//		}else{//表示今天某人没有签到
//			
//		}
//	}
	
	/**
	 * 菜单、返回键响应
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){//如果点击了返回键
			StringUtils.exitBy2Click(MyWorkActivity.this);
		}
		return false;
	}


	/**
	 * 发送GPS请求，启动广播
	 */
	public void SetGpsFerquency() {
		Intent intent = new Intent(MyAMapGpsService.gpsFerquencyAction);
		sendBroadcast(intent);
	}

	/**
	 * 发送签到Http请求,处理点击签到事件
	 * @param type
	 */
	public void HttpGetRequestRegistration(final int type) {

		RequestQueue queue = Volley.newRequestQueue(this);
		HashMap<String, String> params = new HashMap<String, String>();
		
		//判断是否选择自动登录
		if(Setting.autoLogin){//如果是自动登录，则从缓存中取出数据
			params.put("managerId", Integer.toString(shareUserInfo.getInt("uId", 0)));
		}else{//如果没有选择，从Application中取出数据
			params.put("managerId", Integer.toString(myApp.getUserId()));
		}
		
		params.put("type", Integer.toString(type));
		params.put("time", DateUtils.getSystemDate());
		params.put("longitude", Double.toString(myApp.getGpsLongitude()));
		params.put("latitude", Double.toString(myApp.getGpsLatitude()));
		params.put("position", myApp.getGpsAddr());
		String url = Constants.LocalTIMEUrl;
		url += StringUtils.encodeUrl(params);
		JsonObjectRequest jsObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						int status = response.optInt("status");
						if (status == 0) {
							Toast.makeText(getApplicationContext(), getString(R.string.Registration_Success),
									Toast.LENGTH_SHORT).show();
							SharedPreferences.Editor edit = shared.edit();//已编辑的方式打开
							edit.putString("today",DateUtils.getDate());//存入今天的日期
							edit.putString("userId",Integer.toString(userID));//存入用户的ID
							
							if (type == 1) {
								buttonWorkOn.setClickable(false);
								buttonWorkOn.setImageResource(R.drawable.buttonqiandao);
								amDate.setText(DateUtils.getSystemDate());
								edit.putString("amTime", DateUtils.getSystemDate());
							}
							if (type == 2) {
								buttonWorkOff.setClickable(false);
								buttonWorkOff.setImageResource(R.drawable.buttonqiandao);
								pmDate.setText(DateUtils.getSystemDate());
								edit.putString("pmTime", DateUtils.getSystemDate());
							}
							edit.commit();
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



	public final int TIME_UPDATE_UI = 1000;

	/**
	 * handler用于处理消息，每隔30秒更新一次UI
	 */
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
			intent = new Intent(MyWorkActivity.this, SearchActivity.class);
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
	
	/**
	 * 启动界面的时候，发送Http请求。从服务器获取签到数据
	 */
	public void HttpGetSearchRecord() {
		RequestQueue queue = Volley.newRequestQueue(this);
		HashMap<String, String> params = new HashMap<String, String>();
		
		//判断是否选择自动登录
		if(Setting.autoLogin){//如果是自动登录，则从缓存中取出数据
			params.put("managerId", Integer.toString(shareUserInfo.getInt("uId", 0)));
		}else{//如果没有选择，从Application中取出数据
			params.put("managerId", Integer.toString(myApp.getUserId()));
		}
		
		params.put("today", DateUtils.getDate());
		String url = Constants.localSEARCHREAORD;
		url += StringUtils.encodeUrl(params);
		LogHelper.trace(url);

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
						dialog.dismiss();
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(getApplicationContext(),
								VolleyErrorHelper.handleServerError(error, getApplication()), Toast.LENGTH_SHORT)
								.show();
						dialog.dismiss();
					}
				});
		queue.add(jsObjectRequest);
	}

}
