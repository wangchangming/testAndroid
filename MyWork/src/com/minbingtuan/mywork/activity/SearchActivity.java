package com.minbingtuan.mywork.activity;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.minbingtuan.mywork.Constants;
import com.minbingtuan.mywork.MyApplication;
import com.minbingtuan.mywork.R;
import com.minbingtuan.mywork.model.DayOfMonth;
import com.minbingtuan.mywork.utils.CalendarAdapter;
import com.minbingtuan.mywork.utils.DateUtils;
import com.minbingtuan.mywork.utils.LogHelper;
import com.minbingtuan.mywork.utils.Setting;
import com.minbingtuan.mywork.utils.SpecialCalendar;
import com.minbingtuan.mywork.utils.StringUtils;
import com.minbingtuan.mywork.utils.VolleyErrorHelper;
import com.minbingtuan.mywork.view.CustomProgress;
import com.minbingtuan.mywork.view.MyGridView;
import com.minbingtuan.mywork.view.NetDialog;
import com.minbingtuan.mywork.wheel.StrericWheelAdapter;
import com.minbingtuan.mywork.wheel.WheelView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
public class SearchActivity extends Activity implements OnGestureListener,OnClickListener {

	private GestureDetector gestureDetector = null;  
    private CalendarAdapter calV = null;  
    private MyGridView gridView = null;  
    private TextView topText = null;  
    private static int jumpMonth = 0;      //每次滑动，增加或减去一个月,默认为0（即显示当前月）  
    private static int jumpYear = 0;       //滑动跨越一年，则增加或者减去一年,默认为0(即当前年)  
    private int year_c = 0;  //当前年份
    private int month_c = 0;  //当前月份
    private int day_c = 0;  //当前日期
    private String currentDate = "";  
    private Bundle bd=null;//发送参数  
    private Bundle bun=null;//接收参数  
    private String ruzhuTime;  
    private String lidianTime;  
    private String state="";  
    private Intent intent;
    private RadioButton buttonWork;
	private RadioButton buttonSetting;
	private RadioGroup mRadioGroup;
	private int curCheckId = R.id.buttonSearch;
	private TextView curDate;
	
	public static String[] yearContent = null;

    public static String[] monthContent = null;
    
    private WheelView yearWheel, monthWheel;
    
    List<DayOfMonth> list;
    

	private int dayOfWeek = 0;        //具体某一天是星期几

	private SpecialCalendar sc = null;
	
	private DayOfMonth day;
	private MyApplication myApp;

	SharedPreferences shareUserInfo;
	ProgressDialog dialog;
	private CustomProgress dialogProgress;
    
	public SearchActivity() {  
	
		/**
		 * 格式化当前日期
		 */
        Date date = new Date();  
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");  
        currentDate = sdf.format(date);  //当期日期  
        year_c = Integer.parseInt(currentDate.split("-")[0]);  
        month_c = Integer.parseInt(currentDate.split("-")[1]);  
        day_c = Integer.parseInt(currentDate.split("-")[2]);  
          
          
    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_calendar_table);
		
		myApp = (MyApplication) getApplication();
        shareUserInfo = getSharedPreferences("userInfo", Activity.MODE_WORLD_WRITEABLE);
        
        
		
		list = new ArrayList<DayOfMonth>();
		sc = new SpecialCalendar();
//		//创建发送Bundle
//        bd=new Bundle();//out  
//        //获取接收参数
//        bun=getIntent().getExtras();//in  
//          
//          
//          if(bun!=null&&bun.getString("state").equals("ruzhu"))  
//          {  
//            state=bun.getString("state");  
//            System.out.println("%%%%%%"+state);  
//          }else if(bun!=null&&bun.getString("state").equals("lidian")){  
//              
//            state=bun.getString("state");  
//            System.out.println("|||||||||||"+state);  
//          }  
          
        gestureDetector = new GestureDetector(this);
		dayOfWeek = sc.getWeekdayOfMonth(year_c, month_c);      //某月第一天为星期几
		
	}
	@Override
	protected void onResume() {
		super.onResume();
        // 判断手机是否连接网络
        if (!myApp.isConnect()) {// 如果没有连接网络
            Dialog dialog = new NetDialog(this, R.style.MyDialog);
            dialog.show();
        }else{
            //获取每月签到数据
            HttpGetMonthData(DateUtils.getMonth()+"-",year_c,month_c);
            //初始化界面
    		init();
            addGridView();  
        }
		
	}
	public void init(){
		gridView = (MyGridView) findViewById(R.id.gridview);
		buttonWork = (RadioButton) findViewById(R.id.buttonWork);
		buttonSetting = (RadioButton) findViewById(R.id.buttonSetting);
		mRadioGroup = (RadioGroup) findViewById(R.id.main_radio);
        curDate = (TextView) findViewById(R.id.curDate);
        curDate.setText(DateUtils.getMonth());
        buttonWork.setOnClickListener(this);
        buttonSetting.setOnClickListener(this);
        curDate.setOnClickListener(this);
        //将当前选项设置为选中状态
        mRadioGroup.check(curCheckId);
        //隐藏系统软键盘
        curDate.setInputType(InputType.TYPE_NULL);
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		return false;
	}
	
	/**
	 * 添加gridview,声明触摸事件和选项点击事件  
	 */
    private void addGridView() {  
        //触摸事件
        gridView.setOnTouchListener(new OnTouchListener() {  
            //将gridview中的触摸事件回传给gestureDetector  
            @Override  
            public boolean onTouch(View v, MotionEvent event) {  
                // TODO Auto-generated method stub  
                return SearchActivity.this.gestureDetector.onTouchEvent(event);  
            }  
        });             
          
        //点击事件
        gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				LogHelper.trace((position-dayOfWeek)+"");
				
				day = list.get(position-dayOfWeek);
				String am = day.getAm();
				String pm = day.getPm();
				
				
				//判断item是否点击显示
				if(!TextUtils.isEmpty(am)){//在有签到信息的情况下item才可以被点击
					String rs = SearchActivity.this.getString(R.string.sign_info);
					String txt = String.format(rs, am,pm);
					
					LayoutInflater layoutInflater = (LayoutInflater) (SearchActivity.this)
		                    .getSystemService(LAYOUT_INFLATER_SERVICE);
					// 获取自定义布局文件poplayout.xml的视图
		            View popview = layoutInflater.inflate(R.layout.activity_popview, null);
		            TextView textView = (TextView) popview.findViewById(R.id.txt);
		            textView.setText(txt);
		            PopupWindow popWindow = new PopupWindow(popview,
		                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		            // 需要设置一下此参数，点击外边可消失 
		            popWindow.setBackgroundDrawable(new BitmapDrawable()); 
		            //设置点击窗口外边窗口消失 
		            popWindow.setOutsideTouchable(true); 
		            if(popWindow.isShowing()){
		            	calV.isSelected(35);
		            }else{
						calV.isSelected(position);
		            }
		            //规定弹窗的位置
		            // 显示窗口 
		            popWindow.showAsDropDown(view);
		            
		            
				}
				
				
			}  
            
        });  
    }
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonWork:
			intent = new Intent(SearchActivity.this, MyWorkActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.buttonSetting:
			intent = new Intent(SearchActivity.this, MySettingActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.curDate://点击弹出日期选择框
			initData();
			View view = ((LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(
                    R.layout.date_time_selected, null);
			
			Calendar calendar = Calendar.getInstance();
            int curYear = calendar.get(Calendar.YEAR);
            int curMonth = calendar.get(Calendar.MONTH) + 1;
            
            yearWheel = (WheelView)view.findViewById(R.id.yearwheel);
            monthWheel = (WheelView)view.findViewById(R.id.monthwheel);
            
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(view);

            yearWheel.setAdapter(new StrericWheelAdapter(yearContent));
            yearWheel.setCurrentItem(curYear - 2000);
            yearWheel.setCyclic(true);
            yearWheel.setInterpolator(new AnticipateOvershootInterpolator());

            monthWheel.setAdapter(new StrericWheelAdapter(monthContent));

            monthWheel.setCurrentItem(curMonth - 1);

            monthWheel.setCyclic(true);
            monthWheel.setInterpolator(new AnticipateOvershootInterpolator());
            
            builder.setTitle(R.string.selected_year_month);
            builder.setPositiveButton(R.string.determine,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            StringBuffer sb = new StringBuffer();
                            sb.append(yearWheel.getCurrentItemValue()).append("-")
                                    .append(monthWheel.getCurrentItemValue());
                            curDate.setText(sb);
                			int dYear = Integer.parseInt(yearWheel.getCurrentItemValue());
                			int dMonth = Integer.parseInt(monthWheel.getCurrentItemValue());
                			
                			//更新adapter
                	        //获取每月签到数据
                	        HttpGetMonthData(sb+"-",dYear,dMonth);
                			
                			
                            dialog.cancel();
                        }
                    }).show();
			break;
		default:
			break;
		}
	}
	
	public void initData() {
        yearContent = new String[50];
        for (int i = 0; i < 50; i++) {
            yearContent[i] = String.valueOf(i + 2000);
        }

        monthContent = new String[12];
        for (int i = 0; i < 12; i++) {
            monthContent[i] = String.valueOf(i + 1);
            if (monthContent[i].length() < 2) {
                monthContent[i] = "0" + monthContent[i];
            }
        }
    }

	/**
	 * 菜单、返回键响应
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){//如果点击了返回键
			StringUtils.exitBy2Click(SearchActivity.this);
		}
		return false;
	}
	
	/**
	 * 发送http请求，获取当月签到数据
	 * @param date
	 * @param dYear
	 * @param dMonth
	 */
	public void HttpGetMonthData(String date,final int dYear,final int dMonth){
	    //获取数据对话框
		dialogProgress = CustomProgress.show(SearchActivity.this, getString(R.string.getData), true, null);
		
		//声明volley队列，包装http请求
		RequestQueue queue = Volley.newRequestQueue(this);
		HashMap<String, String> params = new HashMap<String, String>();
		
		//判断是否选择自动登录
		if(Setting.autoLogin){//如果是自动登录，则从缓存中取出数据
			params.put("managerId", Integer.toString(shareUserInfo.getInt("uId", 0)));
		}else{//如果没有选择，从Application中取出数据
			params.put("managerId", Integer.toString(myApp.getUserId()));
		}
		
		params.put("month", date);//
		String url = Constants.localSEARCHREAORD;
		url += StringUtils.encodeUrl(params);
		
		JsonObjectRequest jsObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
				new Response.Listener<JSONObject>(){

					@Override
					public void onResponse(JSONObject response) {
						/**
						 * 这里json解析
						 */
						JSONArray dayJsonArray = response.optJSONArray("month");
						Type listType = new TypeToken<LinkedList<DayOfMonth>>(){}.getType(); 
						Gson gson = new Gson(); 
						LinkedList<DayOfMonth> listDay = gson.fromJson(dayJsonArray+"", listType);
						list.clear();
						for (Iterator iterator = listDay.iterator(); iterator.hasNext();) { 
							DayOfMonth dayOfMonth = (DayOfMonth) iterator.next();
							list.add(dayOfMonth);
						}
						  
				        if(list.size()>0){
				        	calV = new CalendarAdapter(SearchActivity.this,getResources(),dYear,dMonth,1,list); 
                			dayOfWeek = sc.getWeekdayOfMonth(dYear, dMonth);      //某月第一天为星期几
                			calV.notifyDataSetChanged();
                			gridView.setAdapter(calV);
				        }

						dialogProgress.dismiss();
						
					}

				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						dialogProgress.dismiss();
						Toast.makeText(getApplicationContext(),
								VolleyErrorHelper.handleServerError(error, getApplication()), Toast.LENGTH_SHORT)
								.show();
					}
				});
		queue.add(jsObjectRequest);
	}

}
