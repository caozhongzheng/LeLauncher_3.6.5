package com.lenovo.launcher.components.XAllAppFace.slimengine;

import com.lenovo.launcher.components.XAllAppFace.utilities.Debug;
import com.lenovo.launcher.components.XAllAppFace.utilities.Debug.D2;
import com.lenovo.launcher2.customizer.Debug.R5;

import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class XContextContent extends View {

	public static final String TAG = "XContext";

	private boolean mInited = false;
//	private boolean mNeedDetach = false;

	private XContentMate renderer;
	private ExchangeManager exchangee;

	private XContext mXContext;

	public XContextContent(XContext context) {
		this(context, null);
	}

	public XContextContent(XContext context, AttributeSet attrs) {
		super(context.getContext(), attrs);

		mXContext = context;

		init();
	}

	public void init() {
		if (mInited) {
			android.util.Log.w(TAG,
					"WARNING : XContext should not be initionlized secondly!");
			return;
		}

		renderer = new XContentMate(this.getClass().getSimpleName());
		exchangee = new ExchangeManager(mXContext);
		renderer.setExchangee(exchangee);
		renderer.setRenderTarget(this);
		renderer.start();

		mInited = true;
	}

	public ExchangeManager getExchangee() {
		return exchangee;
	}

	public XContentMate getRenderer() {
		return renderer;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

//		if (exchangee != null) {
//			exchangee.injectMontionEvent(event);
//		}
		if (renderer != null) {
			renderer.handleMotionEvent();
			exchangee.onTouchEvent(event);
//			renderer.invalidate();
		}
		return true;
	}

	public void onTouchCancel( MotionEvent e ) {
		if (exchangee != null) {
			exchangee.onTouchCancel( e );
		}
	}

	@Override
	protected void onDetachedFromWindow() {

//		mNeedDetach = true;
		invalidate();

		if (exchangee != null) {
			exchangee.clear();
			exchangee = null;
		}
		if (renderer != null) {
			renderer.clearAndDettach();
		}
		super.onDetachedFromWindow();
	}
	
	private volatile boolean mNeedInvalidate = false;
	/*
	 * Do Not Call This From External!
	 * */
	void requestInvalidate(){
//		R5.echo("requestInvalidate");
//		Exception e = new Exception();
//		e.printStackTrace();
		if( mNeedInvalidate ){
			return;
		} 
		mNeedInvalidate = true;
		this.postInvalidate();
	}
	
	@Override
	public void invalidate() {
//		R5.echo("invalidate");
		super.invalidate();
	}

	private NormalDisplayProcess mDisProc = null;

//	private long lastFrameTime = 0L;
	@Override
	protected void onDraw(Canvas canvas) {
		
//		final long fraction = System.currentTimeMillis() - lastFrameTime;
//		lastFrameTime = System.currentTimeMillis();
		
//		R5.echo("XContextContent onDraw");
		
		while (renderer.runnableList != null && !renderer.runnableList.isEmpty()) {
			renderer.runnableList.poll().run();
		}
		
		mNeedInvalidate = false;
		if (mDisProc == null) {
			mDisProc = new NormalDisplayProcess();
		}

		exchangee.updateItem( 17L /*lastFrameTime == 0 ? 16L : fraction*/);
		boolean displayReady = mDisProc.beginDisplay(canvas);
		if (displayReady) {
			exchangee.draw(mDisProc);
			mDisProc.endDisplay();
		}
		
//		if(mNeedInvalidate){
//			invalidate();
//		}
		
//		renderer.wakeupRenderFromDrawing();
		
		
	}
	
	
	static Handler handler = new Handler(){
		
	};
	
	public Handler getExtraHandler(){
		return handler;
	}

}
