package com.lenovo.launcher2.weather.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;

public class WeatherWidgetSquareProgressView extends RelativeLayout{
	
	public ProgressBar mProgress;
	public ProgressBar mProgressBg;
	public Context mcontext;
	public View mSquareWeatherProgressView;
	LauncherApplication mapp ;
	public WeatherWidgetSquareProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	public WeatherWidgetSquareProgressView(Context context) {
		super(context);
		this.setGravity(Gravity.CENTER);
		mcontext = context;
		mapp = (LauncherApplication) getContext().getApplicationContext();
		initlayout(context);
		this.clearChildFocus(mSquareWeatherProgressView);
		// TODO Auto-generated constructor stub
	}
	private void initlayout(final Context context) {
		mSquareWeatherProgressView = mapp.mLauncherContext.getLayoutViewByName(true, "weather_widget_square_progress_layout", null);
      	try{
      		mProgress = (ProgressBar) mapp.mLauncherContext.findViewByIdName(mSquareWeatherProgressView, "weather_widget_square_progress");
      		mProgressBg = (ProgressBar) mapp.mLauncherContext.findViewByIdName(mSquareWeatherProgressView, "weather_widget_square_progress_bg");
	       	if (mProgress == null)
	    		initLocalLayout(context);
	       	else if (mProgressBg == null)
	    		initLocalLayout(context);
      	}catch(Exception ex){
      		ex.printStackTrace();
      		initLocalLayout(context);
      	}
      	RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		this.addView(mSquareWeatherProgressView, rp);
	}
	private void initLocalLayout(final Context context) {
		mSquareWeatherProgressView = View.inflate(context, R.layout.weather_widget_square_progress_layout, null);
		mProgress = (ProgressBar) mSquareWeatherProgressView.findViewById(R.id.weather_widget_square_progress);
		mProgressBg = (ProgressBar) mSquareWeatherProgressView.findViewById(R.id.weather_widget_square_progress_bg);
	}
}
