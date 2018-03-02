package com.lenovo.lejingpin.hw.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

public class AppGallery extends Gallery {

	public AppGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public AppGallery(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		int kEvent;
		if(isScrollingLeft(e1, e2)){
			kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
		}else{
			kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
		}
		
		this.onKeyDown(kEvent, null);
		
		
		//为了能让gallery后退
		if(this.getSelectedItemPosition()==0){
			this.setSelection(4);
		}
		
		return true;
	}
	
	
	
	@Override
	public void setUnselectedAlpha(float unselectedAlpha) {
		unselectedAlpha = 1.0f;
		super.setUnselectedAlpha(unselectedAlpha);
	}

	private boolean isScrollingLeft(MotionEvent e1,MotionEvent e2){
		return e2.getX() > e1.getX();
	}

	
}
