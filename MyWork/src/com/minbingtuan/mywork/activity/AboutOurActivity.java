package com.minbingtuan.mywork.activity;

import com.minbingtuan.mywork.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class AboutOurActivity extends Activity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_our);
		
		findViewById(R.id.re).setOnClickListener(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.re:
			finish();
			break;

		default:
			break;
		}
	}

}
