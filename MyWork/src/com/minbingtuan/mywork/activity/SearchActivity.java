package com.minbingtuan.mywork.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.minbingtuan.mywork.R;
import com.minbingtuan.mywork.utils.CalendarAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
public class SearchActivity extends Activity implements OnGestureListener {

	private GestureDetector gestureDetector = null;  
    private CalendarAdapter calV = null;  
    private GridView gridView = null;  
    private TextView topText = null;  
    private static int jumpMonth = 0;      //每次滑动，增加或减去一个月,默认为0（即显示当前月）  
    private static int jumpYear = 0;       //滑动跨越一年，则增加或者减去一年,默认为0(即当前年)  
    private int year_c = 0;  
    private int month_c = 0;  
    private int day_c = 0;  
    private String currentDate = "";  
    private Bundle bd=null;//发送参数  
    private Bundle bun=null;//接收参数  
    private String ruzhuTime;  
    private String lidianTime;  
    private String state="";  
	
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
          
		
        gridView = (GridView) findViewById(R.id.gridview);
		
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


}
