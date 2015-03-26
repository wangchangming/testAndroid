package com.minbingtuan.mywork.activity;

import com.minbingtuan.mywork.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class SetPwdActivity extends Activity implements OnClickListener{

	private EditText pwd;
	private EditText confirmpwd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_pwd);
		
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
			break;

		default:
			break;
		}
	}

}
