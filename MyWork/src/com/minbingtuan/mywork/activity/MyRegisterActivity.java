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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MyRegisterActivity extends Activity implements OnClickListener {

	Bundle mySavedInstanceState = null;

	private EditText EditTextName = null;
	private EditText EditTextPassWord = null;
	private EditText EditTextConfirmPassWord = null;
	private EditText EditTextTel = null;
	private TextView ButtonGroup = null;
	private EditText EditTextemail = null;
	private Button ButtonRegister = null;

	private int groupIndex = 0;
	private String name;
	private String groupId = "0";
	private String tel;
	private String password;
	private String confirmPassWord;
	private String eMail;

	private MyApplication myApp;

	public void onCreate(Bundle savedInstanceState) {
		mySavedInstanceState = savedInstanceState;
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_register_account);
		myApp = (MyApplication) getApplication();

		EditTextName = (EditText) findViewById(R.id.editTextusername);
		EditTextPassWord = (EditText) findViewById(R.id.editTextuserpassword);
		EditTextConfirmPassWord = (EditText) findViewById(R.id.editTextConfirmPassWord);
		EditTextemail = (EditText) findViewById(R.id.editTextEmail);
		ButtonGroup = (TextView) findViewById(R.id.ButtonGroup);
		EditTextTel = (EditText) findViewById(R.id.editTextTel);

		ButtonRegister = (Button) findViewById(R.id.buttonregister);
		ButtonRegister.setOnClickListener(this);
		ButtonGroup.setOnClickListener(this);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent();
			intent = new Intent(MyRegisterActivity.this, MyLoginActivity.class);
			startActivity(intent);
			this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ButtonGroup:
			if (!myApp.isConnect()) {
				Toast.makeText(getApplicationContext(), getString(R.string.NetError), Toast.LENGTH_SHORT).show();
				return;
			}
			Intent intent = new Intent(MyRegisterActivity.this, MyGroupActivity.class);
			intent.putExtra("groupIndex", groupIndex);
			startActivityForResult(intent, 1);
			break;
		case R.id.buttonregister:
			name = EditTextName.getText().toString().trim();
			tel = EditTextTel.getText().toString().trim();
			password = EditTextPassWord.getText().toString().trim();
			confirmPassWord = EditTextConfirmPassWord.getText().toString().trim();
			eMail = EditTextemail.getText().toString().trim();
			if (TextUtils.isEmpty(name) || (TextUtils.isEmpty(tel)) || (TextUtils.isEmpty(password))
					|| (TextUtils.isEmpty(eMail))) {
				Toast.makeText(getApplicationContext(), getString(R.string.input_register_info), Toast.LENGTH_SHORT)
						.show();
				return;
			}
			if (password.length() < 6 || password.length() > 20) {
				Toast.makeText(getApplicationContext(), getString(R.string.PassWordLengthShouldBe6to20),
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (!password.equals(confirmPassWord)) {
				Toast.makeText(getApplicationContext(), getString(R.string.Two_passwords_do_not_match),
						Toast.LENGTH_SHORT).show();
				return;
			}

			if (!StringUtils.isTel(tel)) {
				Toast.makeText(getApplicationContext(), getString(R.string.Please_enter_the_correct_phone_number),
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (!StringUtils.isEmail(eMail)) {
				Toast.makeText(getApplicationContext(), getString(R.string.Email_format_error), Toast.LENGTH_SHORT)
						.show();
				return;
			}
			if (!myApp.isConnect()) {
				Toast.makeText(getApplicationContext(), getString(R.string.NetError), Toast.LENGTH_SHORT).show();
				return;
			}

			RequestQueue queue = Volley.newRequestQueue(this);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("userName", name);
			params.put("groupId", groupId);
			params.put("mobile", tel);
			params.put("userPwd", password);
			params.put("email", eMail);
			String url = MyApplication.LocalRegisterUrl;
			url += StringUtils.encodeUrl(params);
			JsonObjectRequest jsObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
					new Response.Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject response) {

							int status = response.optInt("status");
							if (status == 0) {
								Toast.makeText(getApplicationContext(), getString(R.string.Registered_successful),
										Toast.LENGTH_SHORT).show();
								Intent intent = new Intent();
								intent.setClass(MyRegisterActivity.this, MyLoginActivity.class);
								startActivity(intent);
								finish();
							}
							if (status < 0) {
								Toast.makeText(getApplicationContext(), getString(R.string.Registered_failed),
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

			break;
		default:
			break;
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			String name = data.getExtras().getString("name");
			String id = data.getExtras().getString("id");
			Log.d("whui", "index2=" + groupId);
			groupIndex = data.getExtras().getInt("groupIndex");
			ButtonGroup.setText(name);
			groupId = id;
		}
	}
}
