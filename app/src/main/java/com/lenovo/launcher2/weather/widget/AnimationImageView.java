package com.lenovo.launcher2.weather.widget;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class AnimationImageView extends ImageView{
	Drawable mupdb;
	Drawable mdowndb;
	Context mcontext;
	boolean isshow =false;
	AnimationSet downset;
	private int mwidth ;
	private int mheight ; 
	private CartoomEngine mCartoomEngine;				// 动画引擎
	private int mproup;
	private int mprodown;
	private View mweatherwidgetdetailsview;
	private boolean ishidewindow = true;
	WeatherWidgetView mweatherwidgetview;
	WeatherWidgetMagicView mweatherwidgetmagicview;
//	WeatherWidgetSquareView mweatherwidgetsquareview;
	public AnimationImageView(Context context) {
		super(context);
		mCartoomEngine = new CartoomEngine(this);
		mcontext = context;
		// TODO Auto-generated constructor stub
	}
	public AnimationImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(isshow){
			try{
				mupdb.setBounds(0, mproup, mwidth, mheight/2+mproup);
				mupdb.draw(canvas);
				mdowndb.setBounds(0, mheight/2+mprodown, mwidth, mheight+mprodown);	
				mdowndb.draw(canvas);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	public void setBitmap(Drawable up,Drawable down,
			int width,int height,View view,WeatherWidgetView weatherwidgetview)
	{
		mupdb = up;
		mdowndb = down;
		mwidth = width;
		mheight = height;
		mweatherwidgetdetailsview = view;
		mweatherwidgetview = weatherwidgetview;
	}
	public void setBitmap(Drawable up,Drawable down,
			int width,int height,View view,WeatherWidgetMagicView weatherwidgetmagicview)
	{
		mupdb = up;
		mdowndb = down;
		mwidth = width;
		mheight = height;
		mweatherwidgetdetailsview = view;
		mweatherwidgetmagicview = weatherwidgetmagicview;
	}
	/*public void setBitmap(Drawable up,Drawable down,
			int width,int height,View view,WeatherWidgetSquareView weatherwidgetsquareview)
	{
		mupdb = up;
		mdowndb = down;
		mwidth = width;
		mheight = height;
		mweatherwidgetdetailsview = view;
		mweatherwidgetsquareview = weatherwidgetsquareview;
	}*/
	public void showview(boolean show,int size)
	{
		isshow = show;
		mCartoomEngine.setMaxSize(-size,size);
		mCartoomEngine.startCartoom(10,true);
		ishidewindow = false;
	}
	public void hideview()
	{
		mCartoomEngine.startCartoom(10,false);
		ishidewindow = false;
	}
	public void updateprogress(int proup,int prodown)
	{
		mproup = proup;
		mprodown = prodown;
		invalidate();
	}
	public void closewindow()
	{
		WindowManager wm = (WindowManager) mcontext.getSystemService(Context.WINDOW_SERVICE);
		if(mweatherwidgetdetailsview!=null){
			wm.removeViewImmediate(mweatherwidgetdetailsview);
		}
		ishidewindow = true;
		mupdb = null;
		mdowndb = null;
		
		if(mweatherwidgetview!=null && mweatherwidgetview.mbitmap!=null&&!mweatherwidgetview.mbitmap.isRecycled()){
			mweatherwidgetview.mbitmap.recycle();
			mweatherwidgetview.mbitmap = null;
			System.gc();
		}
		if(mweatherwidgetmagicview!=null && mweatherwidgetmagicview.mbitmap!=null&&!mweatherwidgetmagicview.mbitmap.isRecycled()){
			mweatherwidgetmagicview.mbitmap.recycle();
			mweatherwidgetmagicview.mbitmap = null;
			System.gc();
		}
		/*if(mweatherwidgetsquareview!=null && mweatherwidgetsquareview.mbitmap!=null&&!mweatherwidgetsquareview.mbitmap.isRecycled()){
			mweatherwidgetsquareview.mbitmap.recycle();
			mweatherwidgetsquareview.mbitmap = null;
			System.gc();
		}*/
		
	}
	public boolean getwindowstate()
	{
		return ishidewindow;
	}
}
