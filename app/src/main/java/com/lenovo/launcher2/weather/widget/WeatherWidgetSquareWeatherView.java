package com.lenovo.launcher2.weather.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;

public class WeatherWidgetSquareWeatherView extends RelativeLayout{
	
	public TextView mweather_widget_square_empty;
	public RelativeLayout mweather_widget_square_info;
	public ImageView mweather_widget_square_icon;
	public TextView mweather_widget_square_description;
	public TextView mweather_widget_square_temperature;
	public Context mcontext;
	public View mSquareWeatherView;
	LauncherApplication mapp ;
	public WeatherWidgetSquareWeatherView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	public WeatherWidgetSquareWeatherView(Context context) {
		super(context);
		this.setGravity(Gravity.CENTER);
		mcontext = context;
		mapp = (LauncherApplication) getContext().getApplicationContext();
		initlayout(context);
		this.clearChildFocus(mSquareWeatherView);
		// TODO Auto-generated constructor stub
	}
	private void initlayout(final Context context) {
		mSquareWeatherView = mapp.mLauncherContext.getLayoutViewByName(true, "weather_widget_square_weather_layout", null);
      	try{
	    	mweather_widget_square_empty = (TextView) mapp.mLauncherContext.findViewByIdName(mSquareWeatherView, "weather_widget_square_empty");
	    	mweather_widget_square_info = (RelativeLayout) mapp.mLauncherContext.findViewByIdName(mSquareWeatherView, "weather_widget_square_info");
	       	mweather_widget_square_icon = (ImageView) mapp.mLauncherContext.findViewByIdName(mSquareWeatherView, "weather_widget_square_icon");
	       	mweather_widget_square_description = (TextView) mapp.mLauncherContext.findViewByIdName(mSquareWeatherView, "weather_widget_square_description");
	       	mweather_widget_square_temperature = (TextView) mapp.mLauncherContext.findViewByIdName(mSquareWeatherView, "weather_widget_square_temperature");
	       	if (mweather_widget_square_empty == null)
	    		initLocalLayout(context);
	       	else if (mweather_widget_square_info == null)
	       		initLocalLayout(context);
	    	else if (mweather_widget_square_icon == null)
	    		initLocalLayout(context);
	    	else if (mweather_widget_square_description == null)
	    		initLocalLayout(context);
	    	else if (mweather_widget_square_temperature == null)
	    		initLocalLayout(context);
      	}catch(Exception ex){
      		ex.printStackTrace();
      		initLocalLayout(context);
      	}
      	RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		this.addView(mSquareWeatherView, rp);
	}
	private void initLocalLayout(final Context context) {
		mSquareWeatherView = View.inflate(context, R.layout.weather_widget_square_weather_layout, null);
		mweather_widget_square_empty = (TextView) mSquareWeatherView.findViewById(R.id.weather_widget_square_empty);
		mweather_widget_square_info = (RelativeLayout) mSquareWeatherView.findViewById(R.id.weather_widget_square_info);
       	mweather_widget_square_icon = (ImageView) mSquareWeatherView.findViewById(R.id.weather_widget_square_icon);
       	mweather_widget_square_description = (TextView) mSquareWeatherView.findViewById(R.id.weather_widget_square_description);
       	mweather_widget_square_temperature = (TextView) mSquareWeatherView.findViewById(R.id.weather_widget_square_temperature);
	}
}
