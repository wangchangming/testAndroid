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
import com.minbingtuan.mywork.utils.StringUtils;
import com.minbingtuan.mywork.utils.VolleyErrorHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class MyLoginActivity extends Activity {

	private Button buttonLogin = null;
	private Button buttonRegister = null;
	private CheckBox checkBox = null;
	private EditText mEditTextUserName = null;
	private EditText mEditTextUserPassWord = null;
	private String mUserName;
	private String mPassWord;

	private MyApplication myApp;


	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_userlogin);
		myApp = (MyApplication) getApplication();

		if (MyApplication.getLoginStatus()) {
			Intent intent = new Intent();
			intent.setClass(MyLoginActivity.this, MyWorkActivity.class);
			startActivity(intent);
			finish();
		}

		mEditTextUserName = (EditText) findViewById(R.id.EditTextUserName);
		mEditTextUserPassWord = (EditText) findViewById(R.id.EditTextUserPassWord);
		buttonLogin = (Button) findViewById(R.id.buttonLogin);
		buttonRegister = (Button) findViewById(R.id.buttonRegister);
		checkBox = (CheckBox) findViewById(R.id.check);
		
		
		//如果用户名已经保存
		if(!"".equals(StringUtils.userName)){
			mEditTextUserName.setText(StringUtils.userName);
			mEditTextUserPassWord.setText(StringUtils.password);
		}
		

		buttonLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mUserName = mEditTextUserName.getText().toString();
				mPassWord = mEditTextUserPassWord.getText().toString();
				if (TextUtils.isEmpty(mUserName) || TextUtils.isEmpty(mPassWord)) {
					Toast.makeText(getApplicationContext(), getString(R.string.Please_enter_the_UserName_to_log_on),
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (Utils.isFastDoubleClick()) {  
			        return;  
			    }
				if (!myApp.isConnect()) {
					Toast.makeText(getApplicationContext(), getString(R.string.NetError), Toast.LENGTH_SHORT).show();
					return;
				}
				HttpGetRequestLogin(mUserName, mPassWord);
			}
		});

		buttonRegister = (Button) findViewById(R.id.buttonRegister);
		buttonRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(MyLoginActivity.this, MyRegisterActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}

	public void HttpGetRequestLogin(String mUserName, String mPassWord) {
		RequestQueue queue = Volley.newRequestQueue(this);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("userName", mUserName);
		params.put("userPwd", mPassWord);
		String url = MyApplication.LocalLoginUrl;
		url += StringUtils.encodeUrl(params);

		JsonObjectRequest jsObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						int status = response.optInt("status");
						if (status == 0) {
							JSONObject jobj = response.optJSONObject("userInfo");
							int id = jobj.optInt("id");
							int groupId = jobj.optInt("groupId");
							String userName = jobj.optString("userName");
							String realName = jobj.optString("realName");
							String pwd = jobj.optString("userPwd");
							String mobile = jobj.optString("mobile");
							String email = jobj.optString("email");
							String birthday = jobj.optString("birthday");
							String groupName = jobj.optString("groupName");
							MyApplication myApp = (MyApplication) getApplication();
							myApp.setUserInfo(id, userName, realName, mobile, email, groupId, pwd, birthday,groupName);
							myApp.startGPSService();
							myApp.setLoginStatus(true);

							if(checkBox.isChecked()){
								StringUtils.userName = userName;
								StringUtils.password = pwd; 
							}else{
								StringUtils.userName = "";
								StringUtils.password = "";
							}
							Toast.makeText(getApplicationContext(), getString(R.string.Login_successful),
									Toast.LENGTH_SHORT).show();
							Intent intent = new Intent();
							intent.setClass(MyLoginActivity.this, MyWorkActivity.class);
							startActivity(intent);
							finish();
						}
						if(status == -1){
							Toast.makeText(getApplicationContext(), getString(R.string.PassWordError),
									Toast.LENGTH_SHORT).show();
						}
						if (status == -2) {
							Toast.makeText(getApplicationContext(), getString(R.string.The_user_is_not_registered),
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
	/**
	 * 
	 * @author wching
	 *
	 */
	static class Utils {  
	    public static long lastClickTime;  
	    public static boolean isFastDoubleClick() {  
	        long time = System.currentTimeMillis();     
	        if ( time - lastClickTime < 2000) {     
	            return true;     
	        }     
	        lastClickTime = time;     
	        return false;     
	    }  
	}  
}
