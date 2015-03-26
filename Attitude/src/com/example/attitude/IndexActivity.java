package com.example.attitude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.attitude.custom.NetDialog;
import com.example.attitude.module.AttitudeDesign;
import com.example.attitude.util.MyDatabaseHelper;
import com.example.attitude.util.MytabOperate;
import com.example.attitude.util.StaticProperty;
import com.example.attitude.util.Util;

public class IndexActivity extends Activity {
	SharedPreferences share = null;
	SharedPreferences.Editor sedit = null;
	private SQLiteOpenHelper helper = null;
	private MytabOperate mtab = null;
	private Util util = null;
	private Boolean conFlag = null;
	private Handler handler = null;
	private RelativeLayout indexBg = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_index);
		
		indexBg = (RelativeLayout) super.findViewById(R.id.indexBg);
		randomUI();//随机扉页背景
		
		// 保存本地信息
		share = IndexActivity.this.getSharedPreferences(
				StaticProperty.SAVEINFO, Activity.MODE_PRIVATE);
		sedit = share.edit();
		// 保存手机信息
		DisplayMetrics displayMetrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int width = displayMetrics.widthPixels;
		int height = displayMetrics.heightPixels;
		sedit.putInt(StaticProperty.SCREENWIDTH, width);
		sedit.putInt(StaticProperty.SCREENHEIGHT, height);
		sedit.commit();
		// 实例化数据库
		this.helper = new MyDatabaseHelper(this);
		IndexActivity.this.mtab = new MytabOperate(
				IndexActivity.this.helper.getWritableDatabase());

		util = new Util(IndexActivity.this);
		// 检测网络连接
		conFlag = util.isConn();
		
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 1:
					List<AttitudeDesign> moduleList = (List<AttitudeDesign>) msg.obj;
					Intent intent = new Intent();
					intent.setClass(IndexActivity.this, MainActivity.class);
					intent.putExtra("conFlag", conFlag);
					intent.putExtra("AttitudeDesign", (Serializable) moduleList);
					startActivity(intent);
					overridePendingTransition(R.anim.index_in, R.anim.index_out);
					finish();
					break;
				default:
					break;
				}
			}
		};

		Thread thread = new Thread() {
			public void run() {
				String url = "http://223.4.147.79:8080/AttitudeDesign/list";
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("page", "1"));
				params.add(new BasicNameValuePair("num", "20"));
				List<AttitudeDesign> moduleList = null;
				String result = null;
				try {
					result = IndexActivity.this.util.getResult(url, params);
					// System.out.println(result + "result**********");
					moduleList = IndexActivity.this.util
							.getAttitudeDesign(result);
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				// 将接口数据存储至数据库
				if (moduleList != null && moduleList.size() > 0) {
					mtab.formatDataBase();// 格式化数据库
					for (AttitudeDesign attitudeDesign : moduleList) {
						IndexActivity.this.mtab
								.insertAttitudeDesign(attitudeDesign);
					}
				}

				Message locationMsg = IndexActivity.this.handler
						.obtainMessage();
				locationMsg.what = 1;
				locationMsg.obj = moduleList;
				// IndexActivity.this.handler
				// .sendMessageDelayed(locationMsg, 1000);
				IndexActivity.this.handler.sendMessage(locationMsg);
			}
		};
		if (conFlag) {
			Toast.makeText(this, "有网络", Toast.LENGTH_SHORT).show();
			thread.start();
		} else {// 没有网络直接从数据库中查找数据
			List<AttitudeDesign> moduleList = IndexActivity.this.mtab
					.findAllAttitudeDesign();
			// System.out.println(moduleList.get(1).getDsg_photo()+
			// "getDsg_photo**********");
			Message locationMsg = IndexActivity.this.handler.obtainMessage();
			locationMsg.what = 1;
			locationMsg.obj = moduleList;
			IndexActivity.this.handler.sendMessageDelayed(locationMsg, 1000);
		}
	}

	public void randomUI() {
		Random random = new Random();
		int i = random.nextInt(3);
		if (i == 0) {
			indexBg.setBackgroundResource(R.drawable.index_bg01);
		} else if (i == 1) {
			indexBg.setBackgroundResource(R.drawable.index_bg02);
		} else if (i == 2) {
			indexBg.setBackgroundResource(R.drawable.index_bg03);
		}
	}
}
