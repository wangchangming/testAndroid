package com.minbingtuan.mywork.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.minbingtuan.mywork.R;
import com.minbingtuan.mywork.utils.CalendarAdapter;
import com.minbingtuan.mywork.utils.DateUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
public class SearchActivity extends Activity implements OnGestureListener,OnClickListener {

	private GestureDetector gestureDetector = null;  
    private CalendarAdapter calV = null;  
    private GridView gridView = null;  
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
	
	public SearchActivity() {  
		  
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
        bd=new Bundle();//out  
        bun=getIntent().getExtras();//in  
          
          
          if(bun!=null&&bun.getString("state").equals("ruzhu"))  
          {  
            state=bun.getString("state");  
            System.out.println("%%%%%%"+state);  
          }else if(bun!=null&&bun.getString("state").equals("lidian")){  
              
            state=bun.getString("state");  
            System.out.println("|||||||||||"+state);  
          }  
          
        gestureDetector = new GestureDetector(this);  
        calV = new CalendarAdapter(this,getResources(),jumpMonth,jumpYear,year_c,month_c,day_c);  
        addGridView();  
        gridView.setAdapter(calV);  
        
        //初始化界面
		init();
        
		
	}
	public void init(){
		gridView = (GridView) findViewById(R.id.gridview);
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
	
	//添加gridview  
    private void addGridView() {  
          
        gridView =(GridView)findViewById(R.id.gridview);  
  
        gridView.setOnTouchListener(new OnTouchListener() {  
            //将gridview中的触摸事件回传给gestureDetector  
            @Override  
            public boolean onTouch(View v, MotionEvent event) {  
                // TODO Auto-generated method stub  
                return SearchActivity.this.gestureDetector.onTouchEvent(event);  
            }  
        });             
          
        gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
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
			calV = new CalendarAdapter(this,getResources(),jumpMonth,jumpYear,2015,5,5); 
			calV.notifyDataSetChanged();
			gridView.setAdapter(calV); 
			break;
		default:
			break;
		}
	}


}
