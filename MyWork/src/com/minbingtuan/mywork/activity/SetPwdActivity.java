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
import com.minbingtuan.mywork.utils.LogHelper;
import com.minbingtuan.mywork.utils.Setting;
import com.minbingtuan.mywork.utils.StringUtils;
import com.minbingtuan.mywork.utils.VolleyErrorHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class SetPwdActivity extends Activity implements OnClickListener{

	private EditText pwd;
	private EditText confirmpwd;
	private String phone;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_pwd);
		
		Intent intent = getIntent();
		phone = intent.getStringExtra("phone");
		pwd = (EditText) findViewById(R.id.pwd);
		confirmpwd = (EditText) findViewById(R.id.confirmpwd);
		
		findViewById(R.id.returnbtn).setOnClickListener(this);
		findViewById(R.id.confirm).setOnClickListener(this);
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.returnbtn:
			finish();
			break;
		case R.id.confirm:
			//发送修改密码请求
			changePassWord();
			break;

		default:
			break;
		}
	}
	
	public void changePassWord() {
		
		if(TextUtils.isEmpty(pwd.getText())){//判断密码是否为空
			LogHelper.toast(this, getString(R.string.EnterNewPassWord));
			return;
		}
		if(TextUtils.isEmpty(confirmpwd.getText())){
			LogHelper.toast(this, getString(R.string.TheTwoPasswordsDoNotMatch));
			return;
		}
		if(!confirmpwd.getText().toString().trim().equals(pwd.getText().toString().trim())){
			LogHelper.toast(this, getString(R.string.OldPasswordCanNotBeTheSame));
			return;
		}
		
		RequestQueue queue = Volley.newRequestQueue(this);
		final HashMap<String, String> params = new HashMap<String, String>();
		params.put("pwd", pwd.getText().toString());
		params.put("mobile", phone);

		String url = MyApplication.localSETTINGPWD;
		url += StringUtils.encodeUrl(params);

		JsonObjectRequest jsObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						int status = response.optInt("status");
						if (status == 0) {
							Toast.makeText(getApplicationContext(), getString(R.string.ChangePassWord_successful),
									Toast.LENGTH_SHORT).show();
							MyApplication myApp = (MyApplication) getApplication();
							myApp.setUserPassWord(params.get("pwd"));
						}
						if (status < 0) {
							Toast.makeText(getApplicationContext(), getString(R.string.ChangePassWord_failed),
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
		finish();
	}

}
