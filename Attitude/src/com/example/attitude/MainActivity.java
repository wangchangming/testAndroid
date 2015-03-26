package com.example.attitude;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import libcore.io.DiskLruCache;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.attitude.custom.MainAdapter;
import com.example.attitude.custom.NetDialog;
import com.example.attitude.custom.RefreshableView;
import com.example.attitude.custom.RefreshableView.PullToRefreshListener;
import com.example.attitude.module.AttitudeDesign;
import com.example.attitude.module.DesignDetail;
import com.example.attitude.util.MyDatabaseHelper;
import com.example.attitude.util.MytabOperate;
import com.example.attitude.util.Util;

public class MainActivity extends Activity {

	private SQLiteOpenHelper helper = null;
	private MytabOperate mtab = null;
	// SharedPreferences share = null;
	private Util util;
	private Button mainHomeBtn;
	private RefreshableView refreshableView;
	private ListView listView;
	private MainAdapter adapter;
	private View loadingLayout;// 列表底部显示信息
	private List<AttitudeDesign> attitudeDesignList = null;
	private RelativeLayout mainTopRl = null;
	private Handler myHandler = null;
	private boolean conFlag = false;// 是否可以连接网络
	private ProgressDialog progressDialog = null;
	// 图片硬盘缓存核心类。
	private DiskLruCache mDiskLruCache;
	private int currentPage = 1;// 接口请求的当前页面
	private boolean doMoreThread = true;// 加载更多的线程,为空时可以加载
	private int topDistance = 0;// 列表项据顶部的距离
	private View childView = null;// 所点击的列表子项
	private boolean clickFlag = true;// 列表子项是否可以被点击
	private long mExitTime = 0;// 返回键响应时间

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Intent it = super.getIntent();
		attitudeDesignList = (List<AttitudeDesign>) it
				.getSerializableExtra("AttitudeDesign");// 接口数据
		if (attitudeDesignList == null || attitudeDesignList.size() == 0) {
			attitudeDesignList = new ArrayList<AttitudeDesign>();
		}
		conFlag = it.getBooleanExtra("conFlag", false);// 是否有网
		util = new Util(this);
		// // 保存本地信息
		// share = MainActivity.this.getSharedPreferences(
		// StaticProperty.SAVEINFO, Activity.MODE_PRIVATE);
		// 实例化数据库
		this.helper = new MyDatabaseHelper(this);
		MainActivity.this.mtab = new MytabOperate(
				MainActivity.this.helper.getWritableDatabase());
		if (!conFlag) {
			Dialog dialog = new NetDialog(MainActivity.this, R.style.MyDialog);
			dialog.show();
		}
		myHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 1:
					attitudeDesignList.removeAll(attitudeDesignList);
					List<AttitudeDesign> attitudeDesignList2 = (List<AttitudeDesign>) msg.obj;
					attitudeDesignList.addAll(attitudeDesignList2);
					// System.out.println(attitudeDesignList.size()
					// + "!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					MainActivity.this.adapter.notifyDataSetChanged();
					// System.out.println(adapter.getCount()
					// + "!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					refreshableView.finishRefreshing(); // 通知更新完成
					break;
				case 2:
					Toast.makeText(MainActivity.this, "网络异常，请稍后再试！",
							Toast.LENGTH_SHORT).show();
					refreshableView.finishRefreshing(); // 通知更新完成
					break;
				case 3:// 有网时的处理操作
					Map<String, Object> map = (Map<String, Object>) msg.obj;
					final List<DesignDetail> moduleList = (List<DesignDetail>) map
							.get("detailList");
					final AttitudeDesign attitudeDesign = (AttitudeDesign) map
							.get("attitudeDesign");
					View view = (View) map.get("view");
					final int position = (Integer) map.get("position");
					TranslateAnimation tran = doStartAnimation(view);
					// 动画完成之后进行跳转
					tran.setAnimationListener(new AnimationListener() {
						public void onAnimationEnd(Animation animation) {
							clickFlag = true;
							Intent intent = new Intent();
							intent.setClass(MainActivity.this,
									DetailActivity.class);
							intent.putExtra("DesignDetail",
									(Serializable) moduleList);
							intent.putExtra("AttitudeDesign",
									(Serializable) attitudeDesign);
							intent.putExtra("topDistance", topDistance);
							startActivityForResult(intent, 1);
							overridePendingTransition(R.anim.main_in,
									R.anim.main_out);
						}

						public void onAnimationRepeat(Animation animation) {

						}

						public void onAnimationStart(Animation animation) {
							MainActivity.this.progressDialog.dismiss();
							for (int i = 0; i < attitudeDesignList.size(); i++) {
								if (i != position) {
									attitudeDesignList.get(i)
											.setShowFlag(false);
								} else {
									attitudeDesignList.get(i).setShowFlag(true);
								}
							}
							adapter.notifyDataSetChanged();
						}

					});
					break;
				case 4:// 无网时的处理操作
					Map<String, Object> map4 = (Map<String, Object>) msg.obj;
					final List<DesignDetail> moduleList4 = (List<DesignDetail>) map4
							.get("detailList");
					final AttitudeDesign attitudeDesign4 = (AttitudeDesign) map4
							.get("attitudeDesign");
					View view4 = (View) map4.get("view");
					final int position4 = (Integer) map4.get("position");
					TranslateAnimation tran4 = doStartAnimation(view4);
					// 动画完成之后进行跳转
					tran4.setAnimationListener(new AnimationListener() {
						public void onAnimationEnd(Animation animation) {
							clickFlag = true;
							Intent intent = new Intent();
							intent.setClass(MainActivity.this,
									DetailActivity.class);
							intent.putExtra("DesignDetail",
									(Serializable) moduleList4);
							intent.putExtra("AttitudeDesign",
									(Serializable) attitudeDesign4);
							intent.putExtra("topDistance", topDistance);
							startActivityForResult(intent, 1);
							overridePendingTransition(R.anim.main_in,
									R.anim.main_out);
						}

						public void onAnimationRepeat(Animation animation) {

						}

						public void onAnimationStart(Animation animation) {
							MainActivity.this.progressDialog.dismiss();
							for (int i = 0; i < attitudeDesignList.size(); i++) {
								if (i != position4) {
									attitudeDesignList.get(i)
											.setShowFlag(false);
								} else {
									attitudeDesignList.get(i).setShowFlag(true);
								}
							}
							adapter.notifyDataSetChanged();
						}
					});
					break;
				case 5:
					List<AttitudeDesign> attitudeDesignList5 = (List<AttitudeDesign>) msg.obj;
					// System.out.println(attitudeDesignList2.size()
					// + "attitudeDesignList2!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					if (attitudeDesignList5.size() != 0) {
						attitudeDesignList.addAll(attitudeDesignList5);
						MainActivity.this.adapter.notifyDataSetChanged();
						doMoreThread = true;
					} else {
						listView.removeFooterView(loadingLayout); // 移除页脚视图
					}

					// System.out.println(adapter.getCount()
					// + "!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					refreshableView.finishRefreshing(); // 通知更新完成
					break;
				case 6:
					Dialog dialog = new NetDialog(MainActivity.this, R.style.MyDialog);
					dialog.show();
					refreshableView.finishRefreshing(); // 通知更新完成
					break;
				default:
					break;
				}
			}
		};

		progressDialog = new ProgressDialog(MainActivity.this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("加载中......");
		progressDialog.setCancelable(true);

		try {
			// 获取图片缓存路径
			File cacheDir = util.getDiskCacheDir("thumb");
			if (!cacheDir.exists()) {
				cacheDir.mkdirs();
			}
			// 创建DiskLruCache实例，初始化缓存数据
			mDiskLruCache = DiskLruCache.open(cacheDir, util.getAppVersion(),
					1, 30 * 1024 * 1024);
		} catch (IOException e) {
			e.printStackTrace();
		}

		mainHomeBtn = (Button) super.findViewById(R.id.mainHomeBtn);
		mainHomeBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, TrailerActivity.class);
				MainActivity.this.startActivity(intent);
			}
		});
		mainTopRl = (RelativeLayout) super.findViewById(R.id.mainTopRl);
		System.out.println("mainTopRl!!!!!!!!!!!!!!!!!!!!!!!!!!"
				+ mainTopRl.getHeight());

		refreshableView = (RefreshableView) findViewById(R.id.refreshable_view);
		listView = (ListView) findViewById(R.id.list_view);
		adapter = new MainAdapter(this, attitudeDesignList, mDiskLruCache);
		loadingLayout = LayoutInflater.from(this).inflate(
				R.layout.listview_bottom_info, null);
		listView.addFooterView(loadingLayout);
		listView.setAdapter(adapter);
		refreshableView.setOnRefreshListener(new PullToRefreshListener() {
			@Override
			public void onRefresh() {
				// System.out.println("真正刷新2!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				if(util.isConn()){
				// 重置数据
				currentPage = 1;
				doMoreThread = true;
				MoreThread refreshThread = new MoreThread(1);
				refreshThread.start();
				}else{
					MainActivity.this.myHandler.sendEmptyMessage(6);
				}
			}
		}, 0);
		listView.setItemsCanFocus(false);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				childView = arg1;
				if (clickFlag) {
					clickFlag = false;
					final View view = arg1;
					final int positon = arg2;
					// System.out.println(view.getTop()+"view.getTop()!!!!!!!!!!!!!!!!!!");

					// int[] location = new int[2]; // 创建坐标实例

					// view.getLocationOnScreen(location);
					// System.out.println("x"+location[0]+"Y"+location[1]+"!!!!!!!!!!!!!!!!!!");
					// System.out.println("getLeft "+view.getLeft()+"getTop"+view.getTop()+"getBottom"+view.getBottom()+"getRight"+view.getRight()+"!!!!!!!!!!!!!!!!!!!!!!!!");
					if (arg2 < attitudeDesignList.size()) {
						final AttitudeDesign attitudeDesign = attitudeDesignList
								.get(arg2);
						final int groupId = attitudeDesign.getGroup_id();
						final Thread thread = new Thread() {
							public void run() {
								String url = "http://223.4.147.79:8080/AttitudeDesign/photo";
								List<NameValuePair> params = new ArrayList<NameValuePair>();
								params.add(new BasicNameValuePair("gid", String
										.valueOf(groupId)));
								List<DesignDetail> detailList = null;
								String result = null;
								try {
									result = MainActivity.this.util.getResult(
											url, params);
									// System.out.println(result
									// + "resultdetail**********");
									detailList = MainActivity.this.util
											.getDesignDetail(result);
								} catch (Exception e1) {
									e1.printStackTrace();
								}
								// System.out.println(detailList.size()
								// + "detailList**********");
								// 将接口数据存储至数据库
								if (detailList != null && detailList.size() > 0) {
									for (DesignDetail designDetail : detailList) {
										if (MainActivity.this.mtab
												.findDesignDetailByUrl(designDetail
														.getPhoto_url())) {
											MainActivity.this.mtab
													.insertDesignDetail(
															designDetail,
															groupId);
										}
									}
								}
								Message locationMsg = MainActivity.this.myHandler
										.obtainMessage();
								locationMsg.what = 3;
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("detailList", detailList);
								map.put("attitudeDesign", attitudeDesign);
								map.put("view", view);
								map.put("position", positon);
								locationMsg.obj = map;
								MainActivity.this.myHandler
										.sendMessage(locationMsg);
							}
						};

						// 检测网络连接
						conFlag = util.isConn();
						if (conFlag) {
							MainActivity.this.progressDialog.show();//显示加载进度框
							thread.start();//启动线程
						} else {
							List<DesignDetail> detailList = MainActivity.this.mtab
									.findDesignDetailById(String
											.valueOf(groupId));
							Message locationMsg = MainActivity.this.myHandler
									.obtainMessage();
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("detailList", detailList);
							map.put("attitudeDesign", attitudeDesign);
							map.put("view", view);
							map.put("position", positon);
							locationMsg.what = 4;
							locationMsg.obj = map;
							MainActivity.this.myHandler
									.sendMessage(locationMsg);
						}

					}
				}
			}
		});
		listView.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// System.out.println("滚动!!!!!!!!!!!!!!!!!!" + firstVisibleItem
				// + "、" + visibleItemCount + "、" + totalItemCount);
				if (firstVisibleItem + visibleItemCount == totalItemCount) {
					// 加载更多
					if (doMoreThread) {
						doMoreThread = false;
						currentPage++;
						MoreThread moreThread = new MoreThread(5);// 加载更多的线程
						moreThread.start();
					}
				} else {
				}
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			this.mDiskLruCache.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			this.mDiskLruCache.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	class MoreThread extends Thread {
		int number = 0;

		public MoreThread(int number) {
			this.number = number;
		}

		public void run() {
			// System.out.println( "run!!!!!!!!!!");
			String url = "http://223.4.147.79:8080/AttitudeDesign/list";
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("page", String
					.valueOf(currentPage)));
			params.add(new BasicNameValuePair("num", "20"));
			String result = null;
			List<AttitudeDesign> attitudeDesignList2 = null;
			try {
				result = MainActivity.this.util.getResult(url, params);
				// System.out.println(result + "result2!!!!!!!!!!");
				attitudeDesignList2 = MainActivity.this.util
						.getAttitudeDesign(result);
			} catch (Exception e1) {
				e1.printStackTrace();
				// System.out.println(e1.toString()
				// + "Exception!!!!!!!!!!");
				number = 2;
			}
			Message locationMsg = MainActivity.this.myHandler.obtainMessage(
					number, attitudeDesignList2);
			MainActivity.this.myHandler.sendMessage(locationMsg);
		}
	}

	 //开始动画
	 public TranslateAnimation getStartAnimation() {
		 TranslateAnimation animation = new TranslateAnimation(0, 0, 0,
		 -topDistance);
		 animation.setDuration(2000);// 设置动画持续时间
		 return animation;
	 }

	// 开始动画
	public TranslateAnimation doStartAnimation(View arg1) {
		arg1.findViewById(R.id.lmTypeText).setVisibility(View.GONE);
		arg1.findViewById(R.id.lmImgImg).setVisibility(View.GONE);
		arg1.findViewById(R.id.lmNumberText).setVisibility(View.GONE);
		topDistance = arg1.getTop();// 记录点击时子项据顶部的距离
		// 头像渐变动画
		AnimationSet set2 = new AnimationSet(true);
		TranslateAnimation tran2 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0.0f, // X轴开始位置
				Animation.RELATIVE_TO_SELF, 0.5f, // X轴移动的结束位置
				Animation.RELATIVE_TO_SELF, 0.0f, // Y轴开始位置
				Animation.RELATIVE_TO_SELF, 5.5f); // Y轴移动位置
		tran2.setDuration(500);// 设置动画持续时间
		set2.addAnimation(tran2); // 增加动画
		AlphaAnimation alpha2 = new AlphaAnimation(1, 0); // 由完全显示 -->
															// 完全透明
		alpha2.setDuration(500);// 设置动画持续时间
		set2.addAnimation(alpha2); // 增加动画
		set2.setFillAfter(true);
		arg1.findViewById(R.id.lmHeadImg).startAnimation(set2); // 启动动画

		// 背景位移动画
		AnimationSet set = new AnimationSet(true);
		TranslateAnimation tran = new TranslateAnimation(0, 0, 0, -topDistance
				- mainTopRl.getHeight());
		tran.setDuration(600);// 设置动画持续时间
		tran.setFillAfter(true);
		set.addAnimation(tran); // 增加动画
		set.setFillAfter(true);
		arg1.startAnimation(set); // 启动动画
		return tran;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		for (int i = 0; i < attitudeDesignList.size(); i++) {
			attitudeDesignList.get(i).setShowFlag(true);
		}
		adapter.notifyDataSetChanged();
		AnimationSet set = new AnimationSet(true);
		TranslateAnimation tran = new TranslateAnimation(0, 0, -topDistance
				- mainTopRl.getHeight(), 0);
		tran.setDuration(500);// 设置动画持续时间
		tran.setFillAfter(true);
		set.addAnimation(tran); // 增加动画
		set.setFillAfter(true);
		childView.startAnimation(set); // 启动动画
		childView.findViewById(R.id.lmTypeText).setVisibility(View.VISIBLE);
		childView.findViewById(R.id.lmImgImg).setVisibility(View.VISIBLE);
		childView.findViewById(R.id.lmNumberText).setVisibility(View.VISIBLE);

		// 头像渐变动画
		AnimationSet set2 = new AnimationSet(true);
		TranslateAnimation tran2 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0.5f, // X轴开始位置
				Animation.RELATIVE_TO_SELF, 0.0f, // X轴移动的结束位置
				Animation.RELATIVE_TO_SELF, 5.5f, // Y轴开始位置
				Animation.RELATIVE_TO_SELF, 0.0f); // Y轴移动位置
		tran2.setDuration(600);// 设置动画持续时间
		set2.addAnimation(tran2); // 增加动画
		AlphaAnimation alpha2 = new AlphaAnimation(0, 1); // 由完全显示 -->
															// 完全透明
		alpha2.setDuration(500);// 设置动画持续时间
		set2.addAnimation(alpha2); // 增加动画
		set2.setFillAfter(true);
		childView.findViewById(R.id.lmHeadImg).startAnimation(set2); // 启动动画
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// if(MainActivity.this.popWin.isShowing()){
			// MainActivity.this.popWin.dismiss(); // 不显示
			// }
			if (mExitTime == 0
					|| (System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(this, "再按一次，退出态度", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				finish();
			}
			return true;
		} else {// 防止菜单键不能显示
			return false;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.getMenuInflater().inflate(R.menu.mymenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) { // 判断操作的菜单ID
		case R.id.clearMenu:
			if (mDiskLruCache != null) {
				try {
					mDiskLruCache.delete();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				// 获取图片缓存路径
				File cacheDir = util.getDiskCacheDir("thumb");
				if (!cacheDir.exists()) {
					cacheDir.mkdirs();
				}
				// 创建DiskLruCache实例，初始化缓存数据
				mDiskLruCache = DiskLruCache.open(cacheDir, util.getAppVersion(),
						1, 30 * 1024 * 1024);
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		}
		return false;
	}

}
