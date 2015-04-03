package com.minbingtuan.mywork.activity;

import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.minbingtuan.mywork.Constants;
import com.minbingtuan.mywork.R;
import com.minbingtuan.mywork.utils.LogHelper;
import com.minbingtuan.mywork.utils.StringUtils;
import com.minbingtuan.mywork.utils.TimeOutTask;
import com.minbingtuan.mywork.utils.Tools;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class BackPwdActivity extends Activity implements OnClickListener{

	private EditText phone;
	private EditText smscode;
	private Button getsmscode;
	private String mobile_code;
	private String return_code;
	private int test = 0;
	private TimeOutTask t;
	private BroadcastReceiver receiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			if("get_mobile".equals(intent.getAction())){
				//处理广播
				IsPhone(phone.getText().toString());
			}
		}};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_back_pwd);
		
		phone = (EditText) findViewById(R.id.phoneNumber);
		smscode = (EditText) findViewById(R.id.sms);
		getsmscode = (Button) findViewById(R.id.getsmscode);
		
		//为界面按钮注册监听事件
		getsmscode.setOnClickListener(this);
		findViewById(R.id.returnbtn).setOnClickListener(this);
		findViewById(R.id.nextbtn).setOnClickListener(this);
		
		//注册广播
		IntentFilter filter = new IntentFilter();
		filter.addAction("get_mobile");
		registerReceiver(receiver, filter);
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.getsmscode:
			//生成一个随机验证码
			mobile_code = (int)((Math.random()*9+1)*100000)+"";
			if(!TextUtils.isEmpty(phone.getText())){//判断电话号是否为空
				if(StringUtils.isTel(phone.getText().toString())){//判断电话号码格式是否正确
					//发送广播，判断手机号是否存在
					Intent intent = new Intent();
					intent.setAction("get_mobile");
					sendBroadcast(intent);
				}else{
					LogHelper.toast(this, getString(R.string.format_phone));
				}
			}else{
				LogHelper.toast(this, getString(R.string.please_input_phone));
			}
			break;
		case R.id.nextbtn:
			if(!TextUtils.isEmpty(phone.getText())){//判断手机号是否为空
				if(!TextUtils.isEmpty(smscode.getText())){//判断短信验证码是否为空
					//判断验证码是否正确
					if(t != null && t.getTimeout()>0){//判断验证码是否失效
						if(mobile_code.equals(smscode.getText().toString())&&"2".equals(return_code)){
							//跳转到修改密码界面
							Intent intent = new Intent(this,SetPwdActivity.class);
							intent.putExtra("phone", phone.getText().toString());
							startActivity(intent);
							finish();
						}else{
							LogHelper.toast(this, getString(R.string.erroe_smscoe));
						}
						
					}else{
						LogHelper.toast(this, getString(R.string.time_out_sms));
					}
				}else{
					LogHelper.toast(this, getString(R.string.please_inputcode));
				}
			}else{
				LogHelper.toast(this, getString(R.string.please_getcode));
			}
			break;
		case R.id.returnbtn:
			finish();
			break;

		default:
			break;
		}
	}
	
	public class MyThread extends Thread{

		@Override
		public void run() {
			return_code = Tools.sendSms(phone.getText().toString(), mobile_code);
		}
		
	}
	
	//发送http请求判断手机号是否存在
	public void IsPhone(String phone){
		RequestQueue queue = Volley.newRequestQueue(this);
		
		String url = Constants.ISPHONE + "mobile="+phone;
		LogHelper.trace(url);
		
		JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, 
			new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				JSONObject data = response.optJSONObject("data");
				String str = data.optString("mobile").trim();
				if(!"null".equals(str)){
					new MyThread().start();
					t = new TimeOutTask(BackPwdActivity.this, getsmscode, 60);
					t.execute(1000);
				}else{
					LogHelper.toast(BackPwdActivity.this, getString(R.string.phone_is_null));
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				LogHelper.toast(BackPwdActivity.this, getString(R.string.net_null));
			}
		});
		queue.add(request);
	}

}
