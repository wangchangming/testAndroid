package com.minbingtuan.mywork.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * 重写gridview，
 * 1、实现禁止滑动
 * 2、解决在ScrollView只显示一行的问题
 * @author wching
 *
 */
public class MyGridView extends GridView {

	public MyGridView(Context context) {
		super(context);
	}
	
	public MyGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public MyGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * 禁止gridview滑动
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_MOVE) {

            return true;  //禁止GridView滑动

       }
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * 解决在gridview 中只显示一行的问题
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,   
                MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
