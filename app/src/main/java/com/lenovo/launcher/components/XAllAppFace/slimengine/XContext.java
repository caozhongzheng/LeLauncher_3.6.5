package com.lenovo.launcher.components.XAllAppFace.slimengine;

import com.lenovo.launcher.components.XAllAppFace.utilities.Debug.D2;
import com.lenovo.launcher.components.XAllAppFace.utilities.Debug.R2;
import com.lenovo.launcher2.customizer.Debug.R5;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

public class XContext extends FrameLayout {

	public static final String TAG = "XContext";
	
	private XContextContent mContentView;

	private boolean hasInited = false;
	
	private boolean mDebug = false;

	public XContext(Context context) {
		super(context);
	}

	public XContext(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		init();
	}

	public void init() {
		
		setLongClickable(false);
		setHapticFeedbackEnabled(false);
		
		if (mContentView == null) {
			mContentView = new XContextContent(this);
			mContentView.setLongClickable(false);
			mContentView.setHapticFeedbackEnabled(false);
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			addView(mContentView, params);
		
			requestLayout();

			hasInited = true;
		}
	}

	public ExchangeManager getExchangee() {

		checkInitState();

		return mContentView.getExchangee();
	}

	public XContentMate getRenderer() {
		checkInitState();

		return mContentView.getRenderer();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		checkInitState();

		super.onSizeChanged(w, h, oldw, oldh);
	}

	public void onTouchCancel( MotionEvent e ) {
		checkInitState();

		if (mContentView.getExchangee() != null) {
			mContentView.getExchangee().onTouchCancel( e );
		}
	}

	private void checkInitState() {
		if (!hasInited) {
			throw new RuntimeException("WOW. INIT.");
		}
	}

	public void bringContentViewToFront() {
	    if (mContentView != null) {
            post(new Runnable() {
                @Override
                public void run() {
                    bringChildToFront(mContentView);
                    requestLayout();
                }
            });
        }
	}
	
	private boolean grabScroll = false;
	private boolean stealLongPress = false;
	private boolean resetContentViewEvent = false;
	private boolean singleTouchIn = false;
	private boolean longPressHappened = false;
	
	public void setGrabScroll( boolean grabScroll ){
		this.grabScroll = grabScroll;
	}
	
	public boolean isGrabScrollState(){
		return this.grabScroll;
	}
	
	public void stealLongPress( boolean stolen){
		this.stealLongPress = stolen;
		
		longPressHappened = true;
	}
	
	public void setLongPressStateExternal( boolean state ){
		longPressHappened = state;
	}
	
	public boolean isStateOfLongPress(){
		return longPressHappened;
	}
	
	public void setSingleTouchIn( boolean forcein){
		this.singleTouchIn = forcein;
	}
	
	public boolean isExternalInterruptAllow(){
		return !singleTouchIn;
	}
	
	VelocityTracker mVelocityTracker;
	MotionEvent lastMotionEventMove = null;
	boolean firstMotionEvent = true;
	
	private static boolean mBlockAllTouchEvent = false;
	public static void blockAllEvent(boolean block){
		mBlockAllTouchEvent = block;
	} 
	
	public static boolean blockAllEvent(){
		return mBlockAllTouchEvent;
	}
	
//	private MotionEvent lastEvent = null;
	@Override
	public boolean dispatchTouchEvent(MotionEvent e) {
		if(mBlockAllTouchEvent){
			if (mDebug) R5.echo("mBlockAllTouchEvent mContentView.dispatchTouchEvent");
			return mContentView.dispatchTouchEvent(e);
		}
		//FIXME:  test touch
//		String logInfo = null;
//		switch(e.getAction()){
//		case MotionEvent.ACTION_DOWN:
//			logInfo = "DOWN";
//			break;
//		case MotionEvent.ACTION_MOVE:
//			logInfo = "MOVE";
//			break;
//		case MotionEvent.ACTION_CANCEL:
//			logInfo = "CANCEL";
//			break;
//		case MotionEvent.ACTION_UP:
//			logInfo = "UP";
//			break;
//		}
//		
//		android.util.Log.i("RV", "\n\nTouch Action is : " + logInfo );
//		android.util.Log.i("RV", "event Info is : \n" + e.toString() + "\n");
//		if( lastEvent != null ){
//			android.util.Log.i("RV", "event gap : " +( e.getEventTime() - lastEvent.getEventTime()));
//		}
//		lastEvent = e;

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(e);

		boolean desireTouch = true;
		if (e.getAction() == MotionEvent.ACTION_MOVE) {

			if (lastMotionEventMove != null) {
				final float disX = Math.abs(lastMotionEventMove.getX()
						- e.getX());
				final float disY = Math.abs(lastMotionEventMove.getY()
						- e.getY());

				if (disX < 0.001f && disY < 0.001f && (!firstMotionEvent)) {
//					android.util.Log.i("touchX", "They are : " + disX + " , "
//							+ disY);
					firstMotionEvent = false;
					
//					android.util.Log.i("cl", "r  3");
					
					if (mDebug) R5.echo("lastMotionEventMove super.onTouchEvent");
					return super.onTouchEvent(e);
				}
			}

			// record event
			lastMotionEventMove = MotionEvent.obtain(e);

			final VelocityTracker velocityTracker = mVelocityTracker;
			velocityTracker.computeCurrentVelocity(1000,
					ViewConfiguration.getMaximumFlingVelocity());
			final float velocityX = velocityTracker.getXVelocity();
			final float velocityY = velocityTracker.getYVelocity();

			// boolean grab1 = Math.abs(distanceY) > 5f * density;
			desireTouch = Math.abs(velocityX) > 7500
					|| Math.abs(velocityY) > 8000;
//			android.util.Log.i("grab0", "grab v : " + desireTouch);

			firstMotionEvent = false;
		}

		if (singleTouchIn) {
			mContentView.onTouchEvent(e);
			// extra 
			MotionEvent extraCancel = MotionEvent.obtain(e);
            if (e.getAction() == MotionEvent.ACTION_UP) {
                extraCancel.setAction(MotionEvent.ACTION_CANCEL);
                if (mDebug) R5.echo("singleTouchIn super.dispatchTouchEvent");
                super.dispatchTouchEvent(extraCancel);
            }
//			android.util.Log.i("cl", "r  2");            
			return true;
		}

		boolean touchDivider = e.getAction() == MotionEvent.ACTION_CANCEL
				|| MotionEvent.ACTION_UP == e.getAction()
				|| MotionEvent.ACTION_POINTER_UP == e.getAction()
				|| e.getAction() == MotionEvent.ACTION_DOWN;

		if (desireTouch & !touchDivider) {
			if (mDebug) R5.echo("desireTouch mContentView.onTouchEvent");
			mContentView.onTouchEvent(e);
//			android.util.Log.i("cl", "r  1");
			return true;
		}

		boolean res = super.dispatchTouchEvent(e);

		if (touchDivider) {
			if( e.getAction() != MotionEvent.ACTION_DOWN )
				grabScroll = false;
			resetContentViewEvent = false;
			stealLongPress = false;
			longPressHappened = false;
			firstMotionEvent = true;

			mVelocityTracker.recycle();
			mVelocityTracker = VelocityTracker.obtain();
		}

//		android.util.Log.i("T", "grabScroll is : " + grabScroll 
//				+ "  , stealLongPress is : " + stealLongPress );
		if (!grabScroll || stealLongPress) {
			if (mDebug) R5.echo("stealLongPress mContentView.onTouchEvent");
			mContentView.onTouchEvent(e);
//			android.util.Log.i("cl", "r  5");
		} else {
			if (grabScroll) {
//				android.util.Log.i( "T", "ignore once long press." );
				MotionEvent e1 = MotionEvent.obtain(e);
				e1.setAction(MotionEvent.ACTION_CANCEL);
				mContentView.getExchangee().injectMontionEvent(e1);
				
				mVelocityTracker.recycle();
				mVelocityTracker = VelocityTracker.obtain();
			}
		}

//		android.util.Log.i("cl", "r  4");
		return res;
	}

}
