package com.lenovo.launcher2.weather.widget;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;
import com.lenovo.launcher2.settings.ProvinceActivity;
import com.lenovo.launcher2.weather.widget.settings.City;
import com.lenovo.launcher2.weather.widget.settings.CityColumns;
import com.lenovo.launcher2.weather.widget.settings.WeatherApp;
import com.lenovo.launcher2.weather.widget.settings.WeatherDetails;
import com.lenovo.launcher2.weather.widget.utils.WeatherUtilites;
import com.lenovo.launcher2.weather.widget.utils.Debug.R2;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherWidgetYuBaoMagicView extends LinearLayout{
	
	public ImageView mweather_widget_other0_day_icon;
	public TextView mweather_widget_other0_day_date;
	public TextView mweather_widget_other0_day_temp;
	public ImageView mweather_widget_other_day_line;
	public ImageView mweather_widget_other1_day_icon;
	public TextView mweather_widget_other1_day_date;
	public TextView mweather_widget_other1_day_temp;
	public Context mcontext;
	public View mweatherwidgetmagicYuBaoview;
	LauncherApplication mapp ;
	public WeatherWidgetYuBaoMagicView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	public WeatherWidgetYuBaoMagicView(Context context) {
		super(context);
		this.setGravity(Gravity.CENTER);
		mcontext = context;
		mapp = (LauncherApplication) getContext().getApplicationContext();
		initlayoutWVGA(context);
		this.clearChildFocus(mweatherwidgetmagicYuBaoview);
		// TODO Auto-generated constructor stub
	}
	private void initlayoutWVGA(final Context context)
	{
      	mweatherwidgetmagicYuBaoview = mapp.mLauncherContext.getLayoutViewByName(true, "weather_widget_magic_yubao_layout",null);
      	try{
	    	mweather_widget_other0_day_icon = (ImageView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetmagicYuBaoview,"weather_widget_other0_day_icon");
	    	mweather_widget_other0_day_date = (TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetmagicYuBaoview,"weather_widget_other0_day_date");
	    	mweather_widget_other0_day_temp = (TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetmagicYuBaoview,"weather_widget_other0_day_temp");
	    	mweather_widget_other_day_line = (ImageView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetmagicYuBaoview,"weather_widget_other_day_line");
	    	mweather_widget_other1_day_icon = (ImageView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetmagicYuBaoview,"weather_widget_other1_day_icon");
	    	mweather_widget_other1_day_date = (TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetmagicYuBaoview,"weather_widget_other1_day_date");
	    	mweather_widget_other1_day_temp = (TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetmagicYuBaoview,"weather_widget_other1_day_temp");
	    	if(mweather_widget_other0_day_icon==null)
	    		initLocalLayout(context);
	    	else if(mweather_widget_other0_day_date==null)
	    		initLocalLayout(context);
	    	else if(mweather_widget_other0_day_temp==null)
	    		initLocalLayout(context);
	    	else if(mweather_widget_other_day_line==null)
	    		initLocalLayout(context);
	    	else if(mweather_widget_other1_day_icon==null)
	    		initLocalLayout(context);
	    	else if(mweather_widget_other1_day_date==null)
	    		initLocalLayout(context);
	    	else if(mweather_widget_other1_day_temp==null)
	    		initLocalLayout(context);
      	}catch(Exception ex){
      		ex.printStackTrace();
      		initLocalLayout(context);
      	}
    	LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		this.addView(mweatherwidgetmagicYuBaoview,lp);
	}
	private void initLocalLayout(final Context context)
	{
      	mweatherwidgetmagicYuBaoview = View.inflate(context, R.layout.weather_widget_magic_yubao_layout, null);
    	mweather_widget_other0_day_icon = (ImageView)mweatherwidgetmagicYuBaoview.findViewById(R.id.weather_widget_other0_day_icon);
    	mweather_widget_other0_day_date = (TextView)mweatherwidgetmagicYuBaoview.findViewById(R.id.weather_widget_other0_day_date);
    	mweather_widget_other0_day_temp = (TextView)mweatherwidgetmagicYuBaoview.findViewById(R.id.weather_widget_other0_day_temp);
    	mweather_widget_other_day_line = (ImageView)mweatherwidgetmagicYuBaoview.findViewById(R.id.weather_widget_other_day_line);
    	mweather_widget_other1_day_icon = (ImageView)mweatherwidgetmagicYuBaoview.findViewById(R.id.weather_widget_other1_day_icon);
    	mweather_widget_other1_day_date = (TextView)mweatherwidgetmagicYuBaoview.findViewById(R.id.weather_widget_other1_day_date);
    	mweather_widget_other1_day_temp = (TextView)mweatherwidgetmagicYuBaoview.findViewById(R.id.weather_widget_other1_day_temp);

	}
}
