package com.minbingtuan.mywork.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.minbingtuan.mywork.R;
import com.minbingtuan.mywork.model.DayOfMonth;
import com.minbingtuan.mywork.utils.CalendarAdapter;
import com.minbingtuan.mywork.utils.DateUtils;
import com.minbingtuan.mywork.utils.LogHelper;
import com.minbingtuan.mywork.utils.SpecialCalendar;
import com.minbingtuan.mywork.utils.StringUtils;
import com.minbingtuan.mywork.view.MyGridView;
import com.minbingtuan.mywork.wheel.StrericWheelAdapter;
import com.minbingtuan.mywork.wheel.WheelView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.InputType;
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
    
    String [][]test = {
			{"1","09:55","18:01"},
			{"2","08:51","18:05"},
			{"3","09:03","18:00"},
			{"4","09:10","18:30"},
			{"5","08:40","18:09"},
			{"6","08:01","18:07"},
			{"7","09:00","18:06"},
			{"8","08:47","18:22"},
			{"9","08:33","18:02"},
			{"10","08:54","17:55"},
			{"11","08:47","17:22"},
			{"12","09:00","18:30"},
			{"13","08:55","18:01"},
			{"14","08:51","18:05"},
			{"15","09:03","18:00"},
			{"16","09:10","18:30"},
			{"17","08:40","18:09"},
			{"18","08:01","18:07"},
			{"19","09:00","18:06"},
			{"20","08:47","18:22"},
			{"21","08:33","18:02"},
			{"22","08:54","17:55"},
			{"23","08:47","17:22"},
			{"24","09:00","18:30"},
			{"25","08:55","18:01"},
			{"26","08:51","18:05"},
			{"27","09:03","18:00"},
			{"28","09:10","18:30"},
			{"29","08:40","18:09"},
			{"30","08:01","18:07"},
			{"31","09:00","18:06"}
			};

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
		
		list = new ArrayList<DayOfMonth>();
		sc = new SpecialCalendar();
		for(int i =0; i <test.length ; i++){
			DayOfMonth day = new DayOfMonth();
			day.setDay(test[i][0]);
			day.setAmDate(test[i][1]);
			day.setPmDate(test[i][2]);
			list.add(day);
		}
		
		for(int i = 0;i<list.size();i++){
			DayOfMonth day = new DayOfMonth();
			day = list.get(i);
			String test = new Gson().toJson(day);
			String str = day.getDay() +"日，早上：" +day.getAmDate()+",下午:"+day.getPmDate();
			LogHelper.trace(str);
			LogHelper.trace(test);
		}
		
		
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
        calV = new CalendarAdapter(this,getResources(),year_c,month_c,day_c,list);  
		dayOfWeek = sc.getWeekdayOfMonth(year_c, month_c);      //某月第一天为星期几
        
        //初始化界面
		init();

        addGridView();  
        gridView.setAdapter(calV);  
        
		
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
				String am = day.getAmDate();
				String pm = day.getPmDate();
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
	            //规定弹窗的位置
	            // 显示窗口 
	            popWindow.showAsDropDown(view);
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
                            sb.append(yearWheel.getCurrentItemValue()).append(".")
                                    .append(monthWheel.getCurrentItemValue());
                            curDate.setText(sb);
                			int dYear = Integer.parseInt(yearWheel.getCurrentItemValue());
                			int dMonth = Integer.parseInt(monthWheel.getCurrentItemValue());
                			calV = new CalendarAdapter(SearchActivity.this,getResources(),dYear,dMonth,1,list); 
                			dayOfWeek = sc.getWeekdayOfMonth(dYear, dMonth);      //某月第一天为星期几
                			calV.notifyDataSetChanged();
                			gridView.setAdapter(calV); 
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

}
