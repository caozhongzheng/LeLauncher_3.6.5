package com.lenovo.launcher2.weather.widget;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherWidgetZhishuiView extends LinearLayout {
	public TextView mweather_widget_cloth;
	public TextView mweather_widget_ganmao;
	public TextView mweather_widget_sport;
	public ImageView mweather_widget_zhishu_line;
	public TextView mweather_widget_shushi;
	public TextView mweather_widget_zwx;
	public TextView mweather_widget_xiche;
	public Context mcontext;
	public View mweatherwidgetzhishuiview;
	LauncherApplication mapp ;
	public WeatherWidgetZhishuiView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		// TODO Auto-generated constructor stub
	}

	public WeatherWidgetZhishuiView(Context context) {
		super(context);
		this.setGravity(Gravity.CENTER);
		mcontext = context;
		mapp = (LauncherApplication) getContext().getApplicationContext();
		init(context);
		this.clearChildFocus(mweatherwidgetzhishuiview);
		// TODO Auto-generated constructor stub
	}
	private void init(Context context)
	{
      	mweatherwidgetzhishuiview = mapp.mLauncherContext.getLayoutViewByName(true, "weather_widget_magic_zhishu_layout",null);
      	try{
      		mweather_widget_cloth = (TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetzhishuiview,"weather_widget_cloth");
      		mweather_widget_ganmao = (TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetzhishuiview,"weather_widget_ganmao");
      		mweather_widget_sport = (TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetzhishuiview,"weather_widget_sport");
      		mweather_widget_zhishu_line = (ImageView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetzhishuiview,"weather_widget_zhishu_line");
      		mweather_widget_shushi = (TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetzhishuiview,"weather_widget_shushi");
      		mweather_widget_zwx = (TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetzhishuiview,"weather_widget_zwx");
      		mweather_widget_xiche = (TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetzhishuiview,"weather_widget_xiche");
      		if(mweather_widget_cloth==null)
      			initLocalLayout(context);
      		else if(mweather_widget_ganmao==null)
      			initLocalLayout(context);
      		else if(mweather_widget_sport==null)
      			initLocalLayout(context);
      		else if(mweather_widget_zhishu_line==null)
      			initLocalLayout(context);
      		else if(mweather_widget_shushi==null)
      			initLocalLayout(context);
      		else if(mweather_widget_zwx==null)
      			initLocalLayout(context);
      		else if(mweather_widget_xiche==null)
      			initLocalLayout(context);
      	}catch(Exception ex){
      		ex.printStackTrace();
      		initLocalLayout(context);
      	}
    	LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		this.addView(mweatherwidgetzhishuiview,lp);
	}
	private void initLocalLayout(Context context)
	{
		mweatherwidgetzhishuiview = View.inflate(context, R.layout.weather_widget_magic_zhishu_layout, null);
  		mweather_widget_cloth = (TextView)mweatherwidgetzhishuiview.findViewById(R.id.weather_widget_cloth);
  		mweather_widget_ganmao = (TextView)mweatherwidgetzhishuiview.findViewById(R.id.weather_widget_ganmao);
  		mweather_widget_sport = (TextView)mweatherwidgetzhishuiview.findViewById(R.id.weather_widget_sport);
  		mweather_widget_zhishu_line = (ImageView)mweatherwidgetzhishuiview.findViewById(R.id.weather_widget_zhishu_line);
  		mweather_widget_shushi = (TextView)mweatherwidgetzhishuiview.findViewById(R.id.weather_widget_shushi);
  		mweather_widget_zwx = (TextView)mweatherwidgetzhishuiview.findViewById(R.id.weather_widget_zwx);
  		mweather_widget_xiche = (TextView)mweatherwidgetzhishuiview.findViewById(R.id.weather_widget_xiche);
	}
}
