package com.lenovo.lejingpin.hw.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.lenovo.launcher.R;

public class AppView extends RelativeLayout {
	private static final String TAG = "AppView";
	private static final boolean LOGD = false;
	private float prevAlpha = 1 ;
	private View mRoot;

	public AppView(Context context) {
		super(context);
	}

	public AppView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AppView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		
		Rect r = new Rect();

		boolean localVisibleRect = getLocalVisibleRect(r);
		
		float alpha = Math.min((float)Math.max(0, r.width()-30)/(getWidth()-30), 1);
		
		if(mRoot==null){
			mRoot = findViewById(R.id.app_container);
		}
		
		log("localVisibleRect >> l,t,r,b=" + r.left + "," + r.top + "," + r.right + "," + r.bottom + //
				" >> foregroundAlpha="+alpha+", prevAlpha="+prevAlpha+//
				" >> w,h=" + r.width() + "," + r.height() + //
				", return=" + localVisibleRect);
		
		if(alpha!=prevAlpha){
			prevAlpha = alpha;
			mRoot.setAlpha(alpha);
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}
	
	private static void log(String msg){
		if(LOGD){
			Log.d(TAG, msg);
		}
	}

}
