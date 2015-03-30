package com.minbingtuan.mywork.activity;

import com.minbingtuan.mywork.R;
import com.minbingtuan.mywork.utils.LogHelper;
import com.minbingtuan.mywork.utils.StringUtils;
import com.minbingtuan.mywork.utils.TimeOutTask;
import com.minbingtuan.mywork.utils.Tools;

import android.app.Activity;
import android.content.Intent;
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
	
	private TimeOutTask t;
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
	}

	@Override
	protected void onDestroy() {
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
					new MyThread().start();
					t = new TimeOutTask(BackPwdActivity.this, getsmscode, 60);
					t.execute(1000);
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

}
