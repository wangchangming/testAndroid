package com.minbingtuan.mywork.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.minbingtuan.mywork.R;
import com.minbingtuan.mywork.model.DayOfMonth;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 日历gridview中的每一个item显示的textview
 * @author wching
 *
 */
public class CalendarAdapter extends BaseAdapter {
	private boolean isLeapyear = false;  //是否为闰年
	private int daysOfMonth = 0;      //某月的天数
	private int dayOfWeek = 0;        //具体某一天是星期几
	private int lastDaysOfMonth = 0;  //上一个月的总天数
	private Context context;
	private String[] dayNumber;  //一个gridview中的日期存入此数组中
	private SpecialCalendar sc = null;
	private LunarCalendar lc = null; //农历显示
	private Resources res = null;
	private Drawable drawable = null;
	
	private String currentYear = "";
	private String currentMonth = "";
	private String currentDay = "";
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
	private int currentFlag = -1;     //用于标记当天
	private int[] schDateTagFlag = null;  //存储当月所有的日程日期
	
	private String showYear = "";   //用于在头部显示的年份
	private String showMonth = "";  //用于在头部显示的月份
	private String animalsYear = ""; 
	private String leapMonth = "";   //闰哪一个月
	private String cyclical = "";   //天干地支
	//系统当前时间
	private String sysDate = "";  
	private String sys_year = "";
	private String sys_month = "";
	private String sys_day = "";
	private List<DayOfMonth> list;
	private DayOfMonth day;
	private int isSelected = 42;
	
	public CalendarAdapter(){
		Date date = new Date();
		sysDate = sdf.format(date);  //当期日期
		sys_year = sysDate.split("-")[0];
		sys_month = sysDate.split("-")[1];
		sys_day = sysDate.split("-")[2];
		
	}
	
	/**
	 * 功能：用于计算出最终显示的某年某月的日历
	 * @param context
	 * @param rs  资源
	 * @param jumpMonth 每次滑动，增加或减去一个月,默认为0（即显示当前月）
	 * @param jumpYear  滑动跨越一年，则增加或者减去一年,默认为0(即当前年)
	 * @param year_c    当前年份
	 * @param month_c   当前月份
	 * @param day_c     当前日期
	 */
	public CalendarAdapter(Context context,Resources rs,int jumpMonth,int jumpYear,int year_c,int month_c,int day_c){
		this();
		this.context= context;
		sc = new SpecialCalendar();
		lc = new LunarCalendar();
		this.res = rs;
		
		/**
		 * 以下是滑动判断，暂时不用需要，先隐藏
		 */
//		//需要显示的年份
//		int stepYear = year_c+jumpYear;  
//		//需要显示的月份
//		int stepMonth = month_c+jumpMonth ;
//		if(stepMonth > 0){
//			//往下一个月滑动
//			if(stepMonth%12 == 0){//如果下一月是12月，则显示当年的的12月日历
//				stepYear = year_c + stepMonth/12 -1;
//				stepMonth = 12;
//			}else{
//				stepYear = year_c + stepMonth/12;
//				stepMonth = stepMonth%12;
//			}
//		}else{
//			//往上一个月滑动
//			stepYear = year_c - 1 + stepMonth/12;
//			stepMonth = stepMonth%12 + 12;
//			if(stepMonth%12 == 0){
//				
//			}
//		}
	
		currentYear = String.valueOf(year_c);;  //得到当前的年份
		currentMonth = String.valueOf(month_c);  //得到本月 （jumpMonth为滑动的次数，每滑动一次就增加一月或减一月）
		currentDay = String.valueOf(day_c);  //得到当前日期是哪天
		
		getCalendar(Integer.parseInt(currentYear),Integer.parseInt(currentMonth));
		
	}
	
	/**
	 * 构造器
	 * @param context 上下文
	 * @param rs 资源
	 * @param year 年
	 * @param month 月
	 * @param day 日
	 * @param list 每月的签到数据
	 */
	public CalendarAdapter(Context context,Resources rs,int year, int month, int day,List<DayOfMonth> list){
		this();
		this.context= context;
		sc = new SpecialCalendar();
		lc = new LunarCalendar();
		this.res = rs;
		currentYear = String.valueOf(year);  //得到跳转到的年份
		currentMonth = String.valueOf(month);  //得到跳转到的月份
		currentDay = String.valueOf(day);  //得到跳转到的天
		this.list = list;
		
		getCalendar(Integer.parseInt(currentYear),Integer.parseInt(currentMonth));
		
	}
	public void isSelected(int selected){
		isSelected = selected;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return dayNumber.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.gridview_item, null);
		 }
		TextView textView = (TextView) convertView.findViewById(R.id.text);
		ImageView am = (ImageView) convertView.findViewById(R.id.am);
		ImageView pm = (ImageView) convertView.findViewById(R.id.pm);
		RelativeLayout rt = (RelativeLayout) convertView.findViewById(R.id.RelativeLayout1);
		String d = dayNumber[position].split("\\.")[0];
//		String dv = dayNumber[position].split("\\.")[1];

//		SpannableString sp = new SpannableString(d+"\n"+dv);//
//		sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, d.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//		sp.setSpan(new RelativeSizeSpan(1.2f) , 0, d.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//		if(dv != null ||dv != ""){
//            sp.setSpan(new RelativeSizeSpan(0.75f), d.length()+1, dayNumber[position].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//		}
		SpannableString sp = new SpannableString(d);
		sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, d.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(new RelativeSizeSpan(1.2f) , 0, d.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		textView.setText(sp);
		textView.setTextColor(Color.GRAY);
		
		if (position < daysOfMonth + dayOfWeek && position >= dayOfWeek) {
			// 当前月信息显示
			textView.setTextColor(Color.BLACK);// 当月字体设黑
			drawable = res.getDrawable(R.drawable.current_day_bgc);
			
			//获取list里面的数据，填充到日历中
			day = list.get(position - dayOfWeek);
			
			//现在考虑空串的情况
			//上午
			if(!TextUtils.isEmpty(day.getAm())){//如果字符串不为空
				if(day.getAm().compareTo("09:00")>0){//如果时间晚于9点
					am.setBackgroundResource(R.drawable.mark2);
				}else{
					am.setBackgroundResource(R.drawable.mark1);
				}
				
			}
			
			//下午
			if(!TextUtils.isEmpty(day.getPm())){//如果下午签到时间不为空
				if(day.getPm().compareTo("18:00")>=0){//如果时间晚于18点
					pm.setBackgroundResource(R.drawable.mark4);
				}else{
					pm.setBackgroundResource(R.drawable.mark3);
				}
				
			}
			
			
			

		}else{//如果不是本月的，就直接隐藏
			rt.setVisibility(View.GONE);
		}
		
		//这里是设置日程的
		if(schDateTagFlag != null && schDateTagFlag.length >0){
			for(int i = 0; i < schDateTagFlag.length; i++){
				if(schDateTagFlag[i] == position){
					//设置日程标记背景
					textView.setBackgroundResource(R.drawable.mark);
				}
			}
		}
		//设置当天的背景
		if(currentFlag == position){ 
			//设置当天的背景
			LogHelper.trace(position+"");
			drawable = res.getDrawable(R.drawable.current_day_bgc);
			textView.setTextColor(Color.RED);
		}
		//如果某一个item被点击
		if(isSelected == position){
			rt.setBackgroundColor(Color.YELLOW);
		}else{
			rt.setBackgroundColor(Color.WHITE);
		}
		
		
		return convertView;
	}
	
	/**
	 * 得到某年的某月的天数且这月的第一天是星期几
	 * @param year
	 * @param month
	 */
	public void getCalendar(int year, int month){
		isLeapyear = sc.isLeapYear(year);              //是否为闰年
		daysOfMonth = sc.getDaysOfMonth(isLeapyear, month);  //某月的总天数
		dayOfWeek = sc.getWeekdayOfMonth(year, month);      //某月第一天为星期几
		//创建日历数组
		dayNumber = new String[daysOfMonth+dayOfWeek];
		lastDaysOfMonth = sc.getDaysOfMonth(isLeapyear, month-1);  //上一个月的总天数
		Log.d("DAY", isLeapyear+" ======  "+daysOfMonth+"  ============  "+dayOfWeek+"  =========   "+lastDaysOfMonth);
		getweek(year,month);
	}
	
	/**
	 * 将一个月中的每一天的值添加入数组dayNuber中
	 * @param year
	 * @param month
	 */
	private void getweek(int year, int month) {
		int j = 1;
		int flag = 0;
		String lunarDay = "";
		
		//得到当前月的所有日程日期(这些日期需要标记)

		for (int i = 0; i < dayNumber.length; i++) {
			 if(i < dayOfWeek){  //前一个月
				int temp = lastDaysOfMonth - dayOfWeek+1;
				lunarDay = lc.getLunarDate(year, month-1, temp+i,false);
				dayNumber[i] = (temp + i)+"."+lunarDay;
			}else if(i < daysOfMonth + dayOfWeek){   //本月
				String day = String.valueOf(i-dayOfWeek+1);   //得到的日期
				lunarDay = lc.getLunarDate(year, month, i-dayOfWeek+1,false);
				dayNumber[i] = i-dayOfWeek+1+"."+lunarDay;
				//对于当前月才去标记当前日期
				if(sys_year.equals(String.valueOf(year)) && sys_month.equals(String.valueOf(month)) && sys_day.equals(day)){
					//标记当前日期
					currentFlag = i;
				}	
				setShowYear(String.valueOf(year));
				setShowMonth(String.valueOf(month));
				setAnimalsYear(lc.animalsYear(year));
				setLeapMonth(lc.leapMonth == 0?"":String.valueOf(lc.leapMonth));
				setCyclical(lc.cyclical(year));
			}else{   //下一个月
				lunarDay = lc.getLunarDate(year, month+1, j,false);
				dayNumber[i] = j+"."+lunarDay;
				j++;
			}
		}
        
        String abc = "";
        for(int i = 0; i < dayNumber.length; i++){
        	 abc = abc+dayNumber[i]+":";
        }
        Log.d("DAYNUMBER",abc);


	}
	
	
	public void matchScheduleDate(int year, int month, int day){
		
	}
	
	/**
	 * 点击每一个item时返回item中的日期
	 * @param position
	 * @return
	 */
	public String getDateByClickItem(int position){
		return dayNumber[position];
	}
	
	/**
	 * 在点击gridView时，得到这个月中第一天的位置
	 * @return
	 */
	public int getStartPositon(){
		return dayOfWeek+7;
	}
	
	/**
	 * 在点击gridView时，得到这个月中最后一天的位置
	 * @return
	 */
	public int getEndPosition(){
		return  (dayOfWeek+daysOfMonth+7)-1;
	}
	
	public String getShowYear() {
		return showYear;
	}

	public void setShowYear(String showYear) {
		this.showYear = showYear;
	}

	public String getShowMonth() {
		return showMonth;
	}

	public void setShowMonth(String showMonth) {
		this.showMonth = showMonth;
	}
	
	public String getAnimalsYear() {
		return animalsYear;
	}

	public void setAnimalsYear(String animalsYear) {
		this.animalsYear = animalsYear;
	}
	
	public String getLeapMonth() {
		return leapMonth;
	}

	public void setLeapMonth(String leapMonth) {
		this.leapMonth = leapMonth;
	}
	
	public String getCyclical() {
		return cyclical;
	}

	public void setCyclical(String cyclical) {
		this.cyclical = cyclical;
	}
}
