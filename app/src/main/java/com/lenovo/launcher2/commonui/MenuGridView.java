package com.lenovo.launcher2.commonui;


import android.content.Context;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;

public class MenuGridView extends GridView implements Page {

	@Override
	public boolean onDragEvent(DragEvent event) {
		// TODO Auto-generated method stub
		return true;
	}

	public MenuGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
/* RK_ID: RK_MENU_RFACTOR . AUT: LIUYG1 . DATE: 2012-11-29 . S */
	@Override
	public int getPageChildCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getChildOnPageAt(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeAllViewsOnPage() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeViewOnPageAt(int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int indexOfChildOnPage(View v) {
		// TODO Auto-generated method stub
		return 0;
	}
	
/* RK_ID: RK_MENU_RFACTOR . AUT: LIUYG1 . DATE: 2012-11-29 . E */
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (ev.getPointerCount() > 1) {
			return true;
		}		
		return super.onInterceptTouchEvent(ev);		
    }
	
	@Override
    public boolean onTouchEvent(MotionEvent ev) {
		if (ev.getPointerCount() > 1) {
			return true;
		}
		return super.onTouchEvent(ev);
	}
}
