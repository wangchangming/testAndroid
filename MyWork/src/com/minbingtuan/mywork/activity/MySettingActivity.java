package com.minbingtuan.mywork.activity;

import java.util.Calendar;
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
import com.minbingtuan.mywork.utils.LogHelper;
import com.minbingtuan.mywork.utils.Setting;
import com.minbingtuan.mywork.utils.StringUtils;
import com.minbingtuan.mywork.utils.VolleyErrorHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MySettingActivity extends Activity implements OnClickListener, OnTouchListener {

	private RadioButton buttonSearch;
	private RadioButton buttonWork;
	private Button buttonExc;
	private RelativeLayout reSetPwd;

	private DatePicker datePicker;

	private TextView mName;
	private TextView mPhone;
	private TextView mBirthday;
	private TextView mEmail;
	private TextView mGroup;
	private TextView mPassWord;
	
	private ImageView topImg;

	private ImageView mUserImage;
	Intent intent;

	private RadioGroup mRadioGroup;
	private int curCheckId = R.id.buttonSetting;
	SharedPreferences shared;
	
	private MyApplication myApp;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		

		shared = getSharedPreferences("userInfo", Activity.MODE_WORLD_WRITEABLE);
		myApp = (MyApplication) getApplication();

		RelativeLayout titleLayout = (RelativeLayout) findViewById(R.id.layoutTitle);
		buttonSearch = (RadioButton) titleLayout.findViewById(R.id.buttonSearch);
		buttonWork = (RadioButton) titleLayout.findViewById(R.id.buttonWork);
		mRadioGroup = (RadioGroup) titleLayout.findViewById(R.id.main_radio);
		buttonExc = (Button) findViewById(R.id.buttonEsc);
		mName = (TextView) findViewById(R.id.TextViewName);
		mPhone = (TextView) findViewById(R.id.TextViewPhone);
		mBirthday = (TextView) findViewById(R.id.TextViewDate);
		mEmail = (TextView) findViewById(R.id.TextVieweMail);
		mGroup = (TextView) findViewById(R.id.TextViewGroup);
		mPassWord = (TextView) findViewById(R.id.TextViewResetPassWord);
		reSetPwd = (RelativeLayout) findViewById(R.id.RelativeLayout10);

		mUserImage = (ImageView) findViewById(R.id.imageView1);
		topImg = (ImageView) findViewById(R.id.imageView2);

		mRadioGroup.check(curCheckId);

		//判断是否自动登录
		if(Setting.autoLogin){
			mName.setText(shared.getString("uRealName", null).equals("null") ? getString(R.string.Setting_name) : shared.getString("uRealName", ""));
			mPhone.setText(shared.getString("uMobile", ""));
			mBirthday.setText(shared.getString("uBirthday", null).equals("null") ? getString(R.string.Setting_birthday) : shared.getString("uBirthday", ""));
			mEmail.setText(shared.getString("uEmail", ""));
			mGroup.setText(shared.getString("uGroupName", ""));
		}else{
			mName.setText(myApp.getRealName().equals("null") ? getString(R.string.Setting_name) : myApp.getRealName());
			mPhone.setText(myApp.getUserMobile());
			mBirthday.setText(myApp.getUserBirthday().equals("null") ? getString(R.string.Setting_birthday) : myApp
					.getUserBirthday());
			mEmail.setText(myApp.getUserEmail());
			mGroup.setText(myApp.getUserGroupName());
		}

		buttonSearch.setOnClickListener(this);
		buttonWork.setOnClickListener(this);
		buttonExc.setOnClickListener(this);
		mName.setOnClickListener(this);
		mPhone.setOnClickListener(this);
		mBirthday.setOnTouchListener(this);
		mEmail.setOnClickListener(this);
		mUserImage.setOnClickListener(this);
		mPassWord.setOnClickListener(this);
		reSetPwd.setOnClickListener(this);
		topImg.setOnClickListener(this);
		findViewById(R.id.RelativeLayout07).setOnClickListener(this);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	// 退出程序Dialog
	protected void dialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage(getString(R.string.Sure_you_want_to_quit));
		builder.setTitle(getString(R.string.prompt));
		builder.setPositiveButton(getString(R.string.determine), new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//如果点击了退出程序，则把缓存清除
				String uName = shared.getString("uName", "");
				String uPwd = shared.getString("uPwd", "");
				Editor edit = shared.edit();
				edit.clear();
				edit.putString("uName", uName);
				edit.putString("uPwd", uPwd);
				edit.putBoolean("namePwd", false);
				edit.putBoolean("isFirstLogin", true);//表示非首次登录
				edit.commit();
				
				dialog.dismiss();
				myApp.stopGPSService();
				myApp.stopPositonService();
				myApp.setLoginStatus(false);
				finish();
			}
		});
		builder.setNegativeButton(getString(R.string.cancel), new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	/**
	 * 修改名字的对话框 Dialog
	 */
	private void inputNameDialog() {

		final EditText edName = new EditText(this);
		edName.setFocusable(true);
		edName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.YourNames)).setView(edName);
		builder.setPositiveButton(getString(R.string.determine), new android.content.DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if (edName.getText().toString().trim() == null || edName.getText().toString().trim().equals("")) {
					return;
				} else {
					saveRealName(edName.getText().toString().trim());
				}
			}
		});
		builder.setNegativeButton(getString(R.string.cancel), null);
		builder.show();
	}

	/**
	 * 修改电话对话框  Dialog
	 */
	public void inputPhoneDialog() {
		final EditText edPhone = new EditText(this);
		edPhone.setFocusable(true);
		edPhone.setFocusableInTouchMode(true);
		edPhone.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.YourPhone)).setView(edPhone);
		builder.setPositiveButton(getString(R.string.determine), new android.content.DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if (edPhone.getText().toString().trim() == null || edPhone.getText().toString().trim().equals("")) {
					return;
				} else {
					String tel = edPhone.getText().toString().trim();
					if (!StringUtils.isTel(tel)) {
						Toast.makeText(getApplicationContext(),
								getString(R.string.Please_enter_the_correct_phone_number), Toast.LENGTH_SHORT).show();
						return;
					}
					savePhone(tel);
				}
			}
		});
		builder.setNegativeButton(getString(R.string.cancel), null);
		builder.show();
	}

	/**
	 * 选择出生日期对话框
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			View view = View.inflate(this, R.layout.date_time_dialog, null);
			datePicker = (DatePicker) view.findViewById(R.id.date_picker);
			builder.setView(view);
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(System.currentTimeMillis());
			datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);

			if (v.getId() == R.id.TextViewDate) {
				int inType = mBirthday.getInputType();
				mBirthday.setInputType(InputType.TYPE_NULL);
				mBirthday.onTouchEvent(event);
				mBirthday.setInputType(inType);

				builder.setTitle(getString(R.string.SelectBirthdayTime));
				builder.setPositiveButton(getString(R.string.determine), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						StringBuffer sb = new StringBuffer();
						String mb = sb.append(
								String.format("%d-%02d-%02d", datePicker.getYear(), datePicker.getMonth() + 1,
										datePicker.getDayOfMonth())).toString();
						saveBirthday(mb);
						mBirthday.setText(sb);
						dialog.cancel();
					}
				});
				builder.setNegativeButton(getString(R.string.cancel), null);
			}
			Dialog dialog = builder.create();
			dialog.show();
		}
		return true;
	}

	// 修改邮箱Dialog
	/**
	 * 修改邮箱对话框
	 */
	public void inputEmailDialog() {
		final EditText edEmail = new EditText(this);
		edEmail.setFocusable(true);
		edEmail.setFocusableInTouchMode(true);
		edEmail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.YourEmail)).setView(edEmail);
		builder.setPositiveButton(getString(R.string.determine), new android.content.DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if (edEmail.getText().toString().trim() == null || edEmail.getText().toString().trim().equals("")) {
					return;
				} else {
					saveEmail(edEmail.getText().toString().trim());
				}
			}
		});
		builder.setNegativeButton(getString(R.string.cancel), null);
		builder.show();
	}

	// 保存姓名
	public void saveRealName(String name) {
		RequestQueue queue = Volley.newRequestQueue(this);
		final HashMap<String, String> params = new HashMap<String, String>();
		String id = "";
		//获取用户ID
		if(Setting.autoLogin){//来自缓存
			id = Integer.toString(shared.getInt("uId", 0));
		}else{//来自application
			id = Integer.toString(myApp.getUserId());
		}
		params.put("name", name);
		params.put("id", id);

		String url = Constants.localSETTINGNAME;
		url += StringUtils.encodeUrl(params);
		JsonObjectRequest jsObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						int status = response.optInt("status");
						if (status == 0) {
							Toast.makeText(getApplicationContext(), getString(R.string.ModifiedSuccessfully),
									Toast.LENGTH_SHORT).show();
							//存入application
							myApp.setRealName(params.get("name"));
							//存入缓存
							Editor edit = shared.edit();
							edit.putString("uRealName", params.get("name"));
							edit.commit();
							
							mName.setText(params.get("name"));
						}
						if (status < 0) {
							Toast.makeText(getApplicationContext(), getString(R.string.ModifiedFailed),
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

	// 保存手机号
	public void savePhone(String Phone) {
		RequestQueue queue = Volley.newRequestQueue(this);
		final HashMap<String, String> params = new HashMap<String, String>();
		
		String id = "";
		//获取用户ID
		if(Setting.autoLogin){//来自缓存
			id = Integer.toString(shared.getInt("uId", 0));
		}else{//来自application
			id = Integer.toString(myApp.getUserId());
		}
		
		params.put("mobile", Phone);
		params.put("id", id);
		String url = Constants.localSETTINGMOBILE;
		url += StringUtils.encodeUrl(params);

		JsonObjectRequest jsObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						int status = response.optInt("status");
						if (status == 0) {
							Toast.makeText(getApplicationContext(), getString(R.string.ModifiedSuccessfully),
									Toast.LENGTH_SHORT).show();
							
							//存入application
							myApp.setUserMobile(params.get("mobile"));
							//存入缓存
							Editor edit = shared.edit();
							edit.putString("uMobile", params.get("mobile"));
							edit.commit();
							
							mPhone.setText(params.get("mobile"));
						}
						if (status < 0) {
							Toast.makeText(getApplicationContext(), getString(R.string.ModifiedFailed),
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

	// 保存生日
	public void saveBirthday(String Birthday) {
		RequestQueue queue = Volley.newRequestQueue(this);
		final HashMap<String, String> params = new HashMap<String, String>();
		String id = "";
		//获取用户ID
		if(Setting.autoLogin){//来自缓存
			id = Integer.toString(shared.getInt("uId", 0));
		}else{//来自application
			id = Integer.toString(myApp.getUserId());
		}
		params.put("birthday", Birthday);
		params.put("id", id);
		String url = Constants.localSETTINGBIRTHDAY;
		url += StringUtils.encodeUrl(params);

		JsonObjectRequest jsObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						int status = response.optInt("status");
						if (status == 0) {
							Toast.makeText(getApplicationContext(), getString(R.string.ModifiedSuccessfully),
									Toast.LENGTH_SHORT).show();
							
							//存入application
							myApp.setUserBirthday(params.get("birthday"));
							//存入缓存
							Editor edit = shared.edit();
							edit.putString("uBirthday", params.get("birthday"));
							edit.commit();
							
							mBirthday.setText(params.get("birthday"));
						}
						if (status < 0) {
							Toast.makeText(getApplicationContext(), getString(R.string.ModifiedFailed),
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

	// 保存邮箱
	public void saveEmail(String email) {
		RequestQueue queue = Volley.newRequestQueue(this);
		final HashMap<String, String> params = new HashMap<String, String>();
		String id = "";
		//获取用户ID
		if(Setting.autoLogin){//来自缓存
			id = Integer.toString(shared.getInt("uId", 0));
		}else{//来自application
			id = Integer.toString(myApp.getUserId());
		}
		params.put("email", email);
		params.put("id", id);
		String url = MyApplication.localSETTINGEMAIL;
		url += StringUtils.encodeUrl(params);

		JsonObjectRequest jsObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						int status = response.optInt("status");
						if (status == 0) {
							Toast.makeText(getApplicationContext(), getString(R.string.ModifiedSuccessfully),
									Toast.LENGTH_SHORT).show();
							
							//存入application
							myApp.setUserBirthday(params.get("email"));
							//存入缓存
							Editor edit = shared.edit();
							edit.putString("uEmail", params.get("email"));
							edit.commit();
							
							mEmail.setText(params.get("email"));
						}
						if (status < 0) {
							Toast.makeText(getApplicationContext(), getString(R.string.ModifiedFailed),
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonSearch:
			intent = new Intent(MySettingActivity.this, SearchActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.buttonWork:
			intent = new Intent(MySettingActivity.this, MyWorkActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.buttonEsc:
			dialog();
			break;
		case R.id.TextViewName:
			inputNameDialog();
			break;
		case R.id.TextViewPhone:
			inputPhoneDialog();
			break;
		case R.id.TextViewDate:
			break;
		case R.id.TextVieweMail:
			inputEmailDialog();
			break;
		case R.id.RelativeLayout10:
			intent = new Intent(MySettingActivity.this, MyChangePwdActivity.class);
			startActivity(intent);
			break;
		case R.id.imageView2:
			LogHelper.toast(this, "该功能正在开发，敬请期待。。。");
			break;
		case R.id.RelativeLayout07:
			startActivity(new Intent(MySettingActivity.this, AboutOurActivity.class));
			break;
		default:
			break;
		}
	}
	
	/**
	 * 菜单、返回键响应
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){//如果点击了返回键
			StringUtils.exitBy2Click(MySettingActivity.this);
		}
		return false;
	}
}
