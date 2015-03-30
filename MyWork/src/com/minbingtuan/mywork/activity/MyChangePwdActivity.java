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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class MyChangePwdActivity extends Activity implements OnClickListener {

	private EditText mOldPassword;
	private EditText mNewPassWord;
	private EditText mConfirmNewPassWord;

	private Button mDetermine;
	private ImageButton mReturn;

	private String oldPassWord;
	private String newPassWord;
	private String confirmPassWord;

	SharedPreferences shared;
	
	private MyApplication myApp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_changepassword);
		
		shared = getSharedPreferences("userInfo", Activity.MODE_WORLD_WRITEABLE);
		myApp = (MyApplication) getApplication();
		
		mOldPassword = (EditText) findViewById(R.id.EditTextOldPwd);
		mNewPassWord = (EditText) findViewById(R.id.EditTextNewPwd);
		mConfirmNewPassWord = (EditText) findViewById(R.id.EditTextConfirmNewPwd);
		mDetermine = (Button) findViewById(R.id.ButtonDetermine);
		mReturn = (ImageButton) findViewById(R.id.Return);

		mDetermine.setOnClickListener(this);
		mReturn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ButtonDetermine:
			changePassWord();
			break;
		case R.id.Return:
			Intent intent = new Intent(MyChangePwdActivity.this, MySettingActivity.class);
			startActivity(intent);
			finish();
			break;
		default:
			break;
		}
	}

	public void changePassWord() {
		String mobile = "";
		String oldPwd = "";
		
		//获取用户ID
		if(Setting.autoLogin){//来自缓存
			mobile = shared.getString("uMobile", "");
			oldPwd = shared.getString("uPwd", "");
		}else{//来自application
			mobile = myApp.getUserMobile();
			oldPwd = myApp.getUserPassWord();
		}
		oldPassWord = mOldPassword.getText().toString().trim();
		newPassWord = mNewPassWord.getText().toString().trim();
		confirmPassWord = mConfirmNewPassWord.getText().toString().trim();

		if(TextUtils.isEmpty(oldPassWord)){
			Toast.makeText(getApplicationContext(), getString(R.string.EnterOldPassWord), Toast.LENGTH_SHORT).show();
			return;
		}
		if(TextUtils.isEmpty(newPassWord) || TextUtils.isEmpty(confirmPassWord)){
			Toast.makeText(getApplicationContext(), getString(R.string.EnterNewPassWord), Toast.LENGTH_SHORT).show();
			return;
		}
		if (!oldPwd.equals(oldPassWord)) {
			Toast.makeText(getApplicationContext(), getString(R.string.OldPassWordErroy), Toast.LENGTH_SHORT).show();
			return;
		}
		if (!newPassWord.equals(confirmPassWord)) {
			Toast.makeText(getApplicationContext(), getString(R.string.TheTwoPasswordsDoNotMatch), Toast.LENGTH_SHORT)
					.show();
			return;
		}
		if (oldPassWord.equals(newPassWord)) {
			Toast.makeText(getApplicationContext(), getString(R.string.OldPasswordCanNotBeTheSame), Toast.LENGTH_SHORT)
					.show();
			return;
		}
		if (newPassWord.length() < 6 || newPassWord.length() > 20) {
			Toast.makeText(getApplicationContext(), getString(R.string.PassWordLengthShouldBe6to20),
					Toast.LENGTH_SHORT).show();
			return;
		}

		RequestQueue queue = Volley.newRequestQueue(this);
		final HashMap<String, String> params = new HashMap<String, String>();
		params.put("pwd", newPassWord);
		params.put("mobile", mobile);

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
