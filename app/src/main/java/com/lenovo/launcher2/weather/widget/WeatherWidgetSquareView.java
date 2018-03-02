package com.lenovo.launcher2.weather.widget;


import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;
import com.lenovo.launcher.components.XAllAppFace.XViewContainer;
import com.lenovo.launcher2.customizer.Reaper;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.weather.widget.settings.City;
import com.lenovo.launcher2.weather.widget.settings.WeatheDetailsApp;
import com.lenovo.launcher2.weather.widget.settings.WeatherApp;
import com.lenovo.launcher2.weather.widget.settings.WeatherDetails;
import com.lenovo.launcher2.weather.widget.utils.WeatherUtilites;
import com.lenovo.launcher2.weather.widget.utils.Debug.R2;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WeatherWidgetSquareView extends LinearLayout {
	
	private static final String TAG = "SquareWidget";
	private static final boolean DEBUG = true;
	private ImageView mweather_widget_square_bg;
	private TextView mweather_widget_square_am_pm;
	private ImageView mweather_widget_square_time_0;
	private ImageView mweather_widget_square_time_1;
	private ImageView mweather_widget_square_time_2;
	private ImageView mweather_widget_square_time_3;
	private ImageView mweather_widget_square_colon;
	private TextView mweather_widget_square_time_date;
	private TextView mweather_widget_square_time_week;
	
	private LinearLayout mweather_widget_square_layout;
	private RelativeLayout mweather_widget_square_time;
	private RelativeLayout mweather_widget_square_weather;
	
	private View mweatherwidgetsquareview;
	private WeatherWidgetSquareWeatherView mWeatherWidgetSquareWeatherView;
	private WeatherWidgetSquareProgressView mWeatherWidgetSquareProgressView;
	private RelativeLayout.LayoutParams mrp;
	/*private RelativeLayout mweatherwidgetdetailsview;
	View mweather_widget_details_content_layout;
	private AnimationSquareImageView mweather_widget_details_pic ;
	private LinearLayout mweather_widget_details_layout ;
	ImageView mweather_widget_details_content_icon;
	TextView mweather_widget_details_content_temp;
	TextView mweather_widget_details_content_content;
	TextView mweather_widget_details_content_wind;
	TextView mweather_widget_details_content_index_uv;
	TextView mweather_widget_details_content_index_polution; //污染
	TextView mweather_widget_details_content_city;
	TextView mweather_widget_details_content_date;
	LinearLayout mweather_widget_details_content_weather_num[] = new LinearLayout[3];
	ImageView mweather_widget_details_content_layout_icon[] = new ImageView[3];
	TextView mweather_widget_details_content_layout_week[] = new TextView[3];
	TextView mweather_widget_details_content_layout_temp[] = new TextView[3];
	TextView mweather_widget_details_content_layout_content[]= new TextView[3];
	private int weather_widget_details_content_weather_nums[] = {R.id.weather_widget_details_content_weather_num0,
   			R.id.weather_widget_details_content_weather_num1,R.id.weather_widget_details_content_weather_num2};
	private String weather_widget_details_content_weather_nums_s[] = {"weather_widget_details_content_weather_num0",
   			"weather_widget_details_content_weather_num1","weather_widget_details_content_weather_num2"};*/
	public Bitmap mbitmap = null;
    private int statusBarHeight= 0;
	private int mdisplayheight = 0;
   	private int mdisplaywidth = 0;
   	private int mrealheight =0;
	private static final String THIS_PUSHMAILWIDGET =
	                 "com.lenovo.launcher2.weather.widget.WeatherWidgetSquareView";
	private BroadcastReceiver mIntentReceiver = null ;
	LauncherApplication mapp ;
    private Context mcontext;
	private Resources mres;
	private static final String EXTRA_NETWORK_ENABLED = "network_enabled";
	private static final String PREF_NETWORK_ENABLER = "pref_network_enabler";
    private FormatChangeObserver mFormatChangeObserver;
	private static final  Uri muri = Uri.withAppendedPath(Settings.System.CONTENT_URI,
            Settings.System.DATE_FORMAT);
	private Handler mHandler = null;
    public boolean mNetworkEnabled = false;
    private static boolean dis12or24=false;
	private static String mTimeFormat;
    private final static String M12 = "hh:mm";
    private final static String M24 = "kk:mm";
    final private static String DEFALUT_DATE_FORMAT_CN = "yyyy-MM-dd";
    final private static String DEFALUT_DATE_FORMAT_EN = "MM/dd/yyyy";
    final private static String DEFALUT_DATE_FORMAT_WVGA_EN = "MM/dd";
    private static final String ACTION_CHANGE_LETHEME = "action.letheme.apply";
    private boolean miscmmc = false;
    private int mposx;
    private int mposy;

    public WeatherWidgetSquareView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	public WeatherWidgetSquareView(Context context) {
		super(context);
		mapp = (LauncherApplication) getContext().getApplicationContext();
		final SharedPreferences preferences = context.getSharedPreferences(
				WeatherUtilites.PRE_LAUNCHER, Activity.MODE_PRIVATE);
		boolean phonebu = false;
		if (preferences != null) {
			phonebu = preferences.getBoolean(WeatherUtilites.PRE_LAUNCHER_KEY_PHONE_BU,
					WeatherUtilites.PRE_LAUNCHER_KEY_PHONE_DEFAULT_BU);
			miscmmc = preferences.getBoolean(WeatherUtilites.PRE_LAUNCHER_KEY_PHONE_CMMC, false);
			showLog("phonebu is " + phonebu + " miscmmc is "+ miscmmc);
		}
		mcontext = context;
		mres = context.getResources();

		initdata(context);
		// TODO Auto-generated constructor stub
	}
	private  void initdata(final Context context) {

		statusBarHeight = WeatherUtilites.getStatusHeights(context);
		initlayoutQHD(context);
        initHandler(context);
    	Intent intent = new Intent(WeatherUtilites.ACTION_ADD_WEATHER_WIDGET);
    	context.sendBroadcast(intent);
    	mFormatChangeObserver = new FormatChangeObserver();
    	mcontext.getContentResolver().registerContentObserver(muri, true,
                mFormatChangeObserver);
	}

	private void initlayoutQHD(final Context context) {
		final DisplayMetrics display = context.getResources().getDisplayMetrics();
    	mdisplaywidth = display.widthPixels;
    	mdisplayheight = display.heightPixels;
    	mrealheight = mdisplayheight - statusBarHeight;
    	this.removeAllViews();
    	mWeatherWidgetSquareWeatherView = new WeatherWidgetSquareWeatherView(context);
    	mWeatherWidgetSquareProgressView = new WeatherWidgetSquareProgressView(context);
    	mweatherwidgetsquareview = mapp.mLauncherContext.getLayoutViewByName(true, "weather_widget_square_layout",null);
    	if (mweatherwidgetsquareview != null) {
	      	try {
	      		initOtherLayout(context);
		} catch(Exception ex) {
	      		ex.printStackTrace();
	      		initLocalLayout(context);
	      	}
      	} else {
      		initLocalLayout(context);
     	}
    	mrp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    	mweather_widget_square_weather.removeAllViews();
    	mweather_widget_square_weather.addView(mWeatherWidgetSquareWeatherView, mrp);
    	mWeatherWidgetSquareWeatherView.mweather_widget_square_empty.setTextColor(
				mapp.mLauncherContext.getColor(R.color.weather_square_widget_empty_color,
						R.color.weather_square_widget_empty_color));
    	mWeatherWidgetSquareWeatherView.mweather_widget_square_empty.setText(context.getResources().getString(R.string.weaher_city_nul_empty));
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

		/*if (WeatherUtilites.isInDefaultTheme(context)) {
      		initDefaultLayout(context);
      	}*/

		this.addView(mweatherwidgetsquareview, lp);
		this.clearChildFocus(mweatherwidgetsquareview);
	}
	private void initOtherLayout(final Context context) {
		mweather_widget_square_bg = (ImageView) mapp.mLauncherContext.findViewByIdName(mweatherwidgetsquareview, "weather_widget_square_bg");
       	mweather_widget_square_am_pm = (TextView) mapp.mLauncherContext.findViewByIdName(mweatherwidgetsquareview, "weather_widget_square_am_pm");
       	mweather_widget_square_time_0 = (ImageView) mapp.mLauncherContext.findViewByIdName(mweatherwidgetsquareview, "weather_widget_square_time_0");
       	mweather_widget_square_time_1 = (ImageView) mapp.mLauncherContext.findViewByIdName(mweatherwidgetsquareview, "weather_widget_square_time_1");
       	mweather_widget_square_time_2 = (ImageView) mapp.mLauncherContext.findViewByIdName(mweatherwidgetsquareview, "weather_widget_square_time_2");
       	mweather_widget_square_time_3 = (ImageView) mapp.mLauncherContext.findViewByIdName(mweatherwidgetsquareview, "weather_widget_square_time_3");
       	mweather_widget_square_colon = (ImageView) mapp.mLauncherContext.findViewByIdName(mweatherwidgetsquareview, "weather_widget_square_colon");
       	mweather_widget_square_time_date = (TextView) mapp.mLauncherContext.findViewByIdName(mweatherwidgetsquareview, "weather_widget_square_time_date");
       	mweather_widget_square_time_week = (TextView) mapp.mLauncherContext.findViewByIdName(mweatherwidgetsquareview, "weather_widget_square_time_week");
       	/*mweather_widget_square_empty = (TextView) mapp.mLauncherContext.findViewByIdName(mweatherwidgetsquareview, "weather_widget_square_empty");
       	mweather_widget_square_icon = (ImageView) mapp.mLauncherContext.findViewByIdName(mweatherwidgetsquareview, "weather_widget_square_icon");
       	mweather_widget_square_description = (TextView) mapp.mLauncherContext.findViewByIdName(mweatherwidgetsquareview, "weather_widget_square_description");
       	mweather_widget_square_temperature = (TextView) mapp.mLauncherContext.findViewByIdName(mweatherwidgetsquareview, "weather_widget_square_temperature");*/
       	mweather_widget_square_layout = (LinearLayout) mapp.mLauncherContext.findViewByIdName(mweatherwidgetsquareview, "weather_widget_square_layout");
       	mweather_widget_square_time = (RelativeLayout) mapp.mLauncherContext.findViewByIdName(mweatherwidgetsquareview, "weather_widget_square_time");
       	mweather_widget_square_weather = (RelativeLayout) mapp.mLauncherContext.findViewByIdName(mweatherwidgetsquareview, "weather_widget_square_weather");
       	if (mweather_widget_square_bg == null)
    		initLocalLayout(context);
    	else if (mweather_widget_square_am_pm == null)
    		initLocalLayout(context);
    	else if (mweather_widget_square_time_0 == null)
    		initLocalLayout(context);
    	else if (mweather_widget_square_time_1 == null)
    		initLocalLayout(context);
    	else if (mweather_widget_square_time_2 == null)
    		initLocalLayout(context);
    	else if (mweather_widget_square_time_3 == null)
    		initLocalLayout(context);
    	else if (mweather_widget_square_colon == null)
    		initLocalLayout(context);
    	else if (mweather_widget_square_time_date == null)
    		initLocalLayout(context);
    	else if (mweather_widget_square_time_week == null)
    		initLocalLayout(context);
    	/*else if (mweather_widget_square_empty == null)
    		initLocalLayout(context);
    	else if (mweather_widget_square_icon == null)
    		initLocalLayout(context);
    	else if (mweather_widget_square_description == null)
    		initLocalLayout(context);
    	else if (mweather_widget_square_temperature == null)
    		initLocalLayout(context);*/
    	else if (mweather_widget_square_layout == null)
    		initLocalLayout(context);
    	else if (mweather_widget_square_time == null)
    		initLocalLayout(context);
    	else if (mweather_widget_square_weather == null)
    		initLocalLayout(context);
	}

	private void initLocalLayout(final Context context) {
		mweatherwidgetsquareview = View.inflate(context, R.layout.weather_widget_square_layout, null);
		mweather_widget_square_bg = (ImageView) mweatherwidgetsquareview.findViewById(R.id.weather_widget_square_bg);
       	mweather_widget_square_am_pm = (TextView) mweatherwidgetsquareview.findViewById(R.id.weather_widget_square_am_pm);
       	mweather_widget_square_time_0 = (ImageView) mweatherwidgetsquareview.findViewById(R.id.weather_widget_square_time_0);
       	mweather_widget_square_time_1 = (ImageView) mweatherwidgetsquareview.findViewById(R.id.weather_widget_square_time_1);
       	mweather_widget_square_time_2 = (ImageView) mweatherwidgetsquareview.findViewById(R.id.weather_widget_square_time_2);
       	mweather_widget_square_time_3 = (ImageView) mweatherwidgetsquareview.findViewById(R.id.weather_widget_square_time_3);
       	mweather_widget_square_colon = (ImageView) mweatherwidgetsquareview.findViewById(R.id.weather_widget_square_colon);
       	mweather_widget_square_time_date = (TextView) mweatherwidgetsquareview.findViewById(R.id.weather_widget_square_time_date);
       	mweather_widget_square_time_week = (TextView) mweatherwidgetsquareview.findViewById(R.id.weather_widget_square_time_week);
       	/*mweather_widget_square_empty = (TextView) mweatherwidgetsquareview.findViewById(R.id.weather_widget_square_empty);
       	mweather_widget_square_icon = (ImageView) mweatherwidgetsquareview.findViewById(R.id.weather_widget_square_icon);
       	mweather_widget_square_description = (TextView) mweatherwidgetsquareview.findViewById(R.id.weather_widget_square_description);
       	mweather_widget_square_temperature = (TextView) mweatherwidgetsquareview.findViewById(R.id.weather_widget_square_temperature);*/
       	mweather_widget_square_layout = (LinearLayout) mweatherwidgetsquareview.findViewById(R.id.weather_widget_square_layout);
       	mweather_widget_square_time = (RelativeLayout) mweatherwidgetsquareview.findViewById(R.id.weather_widget_square_time);
       	mweather_widget_square_weather = (RelativeLayout) mweatherwidgetsquareview.findViewById(R.id.weather_widget_square_weather);
	}
	/*private void initDefaultLayout(final Context context) {
		mweather_widget_details_content_layout = mapp.mLauncherContext.getLayoutViewByName(true, "weather_widget_square_details_content_layout",null);
       	if (mweather_widget_details_content_layout == null) {
       		mweather_widget_details_content_layout = View.inflate(context, R.layout.weather_widget_square_details_content_layout, null);
       	}
		mweatherwidgetdetailsview = (RelativeLayout) mapp.mLauncherContext.getLayoutViewByName(true, "weather_widget_square_details_layout",null);
		if (mweatherwidgetdetailsview != null) {
			try{
				mweather_widget_details_layout = (LinearLayout)mapp.mLauncherContext.findViewByIdName(mweatherwidgetdetailsview, "weather_widget_details_layout");
				mweather_widget_details_content_icon = (ImageView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetdetailsview, "weather_widget_details_content_icon");
				mweather_widget_details_content_temp = (TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetdetailsview, "weather_widget_details_content_temp");
				mweather_widget_details_content_content = (TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetdetailsview, "weather_widget_details_content_content");
				mweather_widget_details_content_wind = (TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetdetailsview, "weather_widget_details_content_wind");
				mweather_widget_details_content_index_uv = (TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetdetailsview, "weather_widget_details_content_index_uv");
				mweather_widget_details_content_index_polution = (TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetdetailsview, "weather_widget_details_content_index_polution");
				mweather_widget_details_content_city = (TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetdetailsview, "weather_widget_details_content_city");
				mweather_widget_details_content_date = (TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetdetailsview, "weather_widget_details_content_date");
				for (int i =0; i<3; i++) {
					mweather_widget_details_content_weather_num[i] = (LinearLayout)mapp.mLauncherContext.findViewByIdName(mweatherwidgetdetailsview, weather_widget_details_content_weather_nums_s[i]);
					mweather_widget_details_content_layout_temp[i] = (TextView)mapp.mLauncherContext.findViewByIdName(mweather_widget_details_content_weather_num[i], "weather_widget_details_content_layout_temp");
					mweather_widget_details_content_layout_week[i] = (TextView)mapp.mLauncherContext.findViewByIdName(mweather_widget_details_content_weather_num[i], "weather_widget_details_content_layout_week");
					mweather_widget_details_content_layout_icon[i] = (ImageView)mapp.mLauncherContext.findViewByIdName(mweather_widget_details_content_weather_num[i], "weather_widget_details_content_layout_icon");
					mweather_widget_details_content_layout_content[i] = (TextView)mapp.mLauncherContext.findViewByIdName(mweather_widget_details_content_weather_num[i], "weather_widget_details_content_layout_content");
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		} else {
       		mweatherwidgetdetailsview = (RelativeLayout) View.inflate(context, R.layout.weather_widget_square_details_layout, null);
       		mweather_widget_details_layout = (LinearLayout)mweatherwidgetdetailsview.findViewById(R.id.weather_widget_details_layout);
       		mweather_widget_details_content_icon = (ImageView)mweatherwidgetdetailsview.findViewById(R.id.weather_widget_details_content_icon);
       		mweather_widget_details_content_temp = (TextView)mweatherwidgetdetailsview.findViewById(R.id.weather_widget_details_content_temp);
       		mweather_widget_details_content_content = (TextView)mweatherwidgetdetailsview.findViewById(R.id.weather_widget_details_content_content);
    		mweather_widget_details_content_wind = (TextView)mweatherwidgetdetailsview.findViewById(R.id.weather_widget_details_content_wind);
    		mweather_widget_details_content_index_uv = (TextView)mweatherwidgetdetailsview.findViewById(R.id.weather_widget_details_content_index_uv);
    		mweather_widget_details_content_index_polution = (TextView)mweatherwidgetdetailsview.findViewById(R.id.weather_widget_details_content_index_polution);
    		mweather_widget_details_content_city = (TextView)mweatherwidgetdetailsview.findViewById(R.id.weather_widget_details_content_city);
    		mweather_widget_details_content_date = (TextView)mweatherwidgetdetailsview.findViewById(R.id.weather_widget_details_content_date);
    		for (int i =0; i<3; i++) {
    			mweather_widget_details_content_weather_num[i] = (LinearLayout)mweatherwidgetdetailsview.findViewById(weather_widget_details_content_weather_nums[i]);
    			mweather_widget_details_content_layout_temp[i] = (TextView)mweather_widget_details_content_weather_num[i]
    					.findViewById(R.id.weather_widget_details_content_layout_temp);
    			mweather_widget_details_content_layout_week[i] = (TextView)mweather_widget_details_content_weather_num[i]
    					.findViewById(R.id.weather_widget_details_content_layout_week);
    			mweather_widget_details_content_layout_icon[i] = (ImageView)mweather_widget_details_content_weather_num[i]
    					.findViewById(R.id.weather_widget_details_content_layout_icon);
    			mweather_widget_details_content_layout_content[i] = (TextView)mweather_widget_details_content_weather_num[i]
    					.findViewById(R.id.weather_widget_details_content_layout_content);
    		}
		}
       	mweather_widget_details_pic = new AnimationSquareImageView(context);

		RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 
        		LinearLayout.LayoutParams.MATCH_PARENT);
        mweatherwidgetdetailsview.addView(mweather_widget_details_pic, rp);
	}*/

	private void initHandler(final Context context) {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch(msg.what) {
				case WeatherUtilites.MES_UPDATE_WEATHER:
					saveCityName(mcontext);
					updateWeather(mcontext);
					updatelayout();
					buildViewCache();
					break;
				case WeatherUtilites.MES_UPDATE_WEATHER_FAILED:
					mweather_widget_square_weather.removeAllViews();
					mweather_widget_square_weather.addView(mWeatherWidgetSquareWeatherView, mrp);
					updatelayout();
					buildViewCache();
					break;
				case WeatherUtilites.MES_CHANGE_CITY_WEATHER:
					try{
						saveCityName(mcontext);
						updateweatherdata(mcontext);
					}catch(Exception ex){
						ex.printStackTrace();
						clearWeatherInfo();
					}
					updatelayout();
					buildViewCache();
					break;
				case WeatherUtilites.MES_WEATHER_NETWORK_STATE_CHANGE:
					updatelayout();
					setWeathInfolistener(mcontext);
					break;
				case WeatherUtilites.MES_WEATHER_INSTALL_CHANGE:
					break;
				case WeatherUtilites.MES_ACTION_SCREEN_ON:
					saveCityName(mcontext);
//					updateDataFromLeDesktopSettings(mcontext);
					displayTime(mcontext);
					updateWeather(mcontext);
					buildViewCache();
					break;
				case WeatherUtilites.MES_LOCATION_CHANGE:
					saveCityName(mcontext);
					mweather_widget_square_weather.removeAllViews();
					mweather_widget_square_weather.addView(mWeatherWidgetSquareProgressView, mrp);
					clearWeatherInfo();
					setWeathInfolistener(mcontext);
					updatelayout();
					buildViewCache();
					break;

				case WeatherUtilites.MES_WEATHER_WIDGET_INIT:
//					updateDataFromLeDesktopSettings(mcontext);
					saveCityName(mcontext);
					setDefaultWeatherBG();
					displayTime(mcontext);
		        	updateWeather(mcontext);
		        	setTimeLayoutlistener(mcontext);
		        	setWeathInfolistener(mcontext);
		        	buildViewCache();
					break;
				case WeatherUtilites.MES_TIME_TICK:
					displayTime(mcontext);
					buildViewCache();
					break;
				case WeatherUtilites.MES_TIEM_CHANGE:
					updateWeather(mcontext);
					displayTime(mcontext);
					buildViewCache();
					break;
				case WeatherUtilites.MES_UPDATE_WEATHER_DETAILS:
					List<WeatherApp> weatherapps = (List<WeatherApp>)msg.obj;
					updateWeatherByApp(mcontext, weatherapps);
					break;
				case WeatherUtilites.MES_UPDATE_LOCAL_WEATHER_CITYNAME:
					String cityname = (String)msg.obj;
					if (cityname != null && !TextUtils.isEmpty(cityname)) {
						showLog("update logcal city name " + cityname);
						List<WeatherDetails> weatherdetails  = WeatherUtilites.getWeatherDetails(mcontext);
						if (weatherdetails != null && weatherdetails.size() > 0) {
							mWeatherWidgetSquareWeatherView.mweather_widget_square_empty.setVisibility(View.GONE);
							mWeatherWidgetSquareWeatherView.mweather_widget_square_info.setVisibility(VISIBLE);
						} else {
							mWeatherWidgetSquareWeatherView.mweather_widget_square_empty.setVisibility(VISIBLE);
							mWeatherWidgetSquareWeatherView.mweather_widget_square_info.setVisibility(GONE);
						}
						
//						mweather_widget_details_content_city.setText(" " + WeatherUtilites.getCityName(context, WeatherUtilites.getLan()));
						cityname = WeatherUtilites.getCityName(mcontext, 1);
						showLog("update logcal city name 22 " + cityname);
						Settings.System.putString(mcontext.getContentResolver(), "city_name", cityname);
					} else {
						mWeatherWidgetSquareWeatherView.mweather_widget_square_empty.setVisibility(View.VISIBLE);
						mWeatherWidgetSquareWeatherView.mweather_widget_square_info.setVisibility(GONE);
						Settings.System.putString(mcontext.getContentResolver(), "city_name", "");
					}
					break;
				case WeatherUtilites.MES_UPDATE_WEATHER_CITYNAME:
					String tcityname = (String)msg.obj;
					if (tcityname != null && !TextUtils.isEmpty(tcityname)) {
						mWeatherWidgetSquareWeatherView.mweather_widget_square_empty.setVisibility(View.GONE);
						mWeatherWidgetSquareWeatherView.mweather_widget_square_info.setVisibility(VISIBLE);
					}else{
						mWeatherWidgetSquareWeatherView.mweather_widget_square_empty.setVisibility(View.VISIBLE);
						mWeatherWidgetSquareWeatherView.mweather_widget_square_info.setVisibility(GONE);
					}
					break;
				case WeatherUtilites.MES_UPDATE_SERVICE_WEATHER_DETAILS:
					List<WeatherDetails> weatherdetails  = (List<WeatherDetails>)msg.obj;
					updateWeatherBySoure(context, weatherdetails);
					break;
				case WeatherUtilites.MES_WEATHER_ANIMATE_STOP:
					/*displayTime(mcontext);
					if (WeatherUtilites.isInDefaultTheme(context) && !mweather_widget_details_pic.getwindowstate()) {
						mweather_widget_details_pic.hideview();
					}*/
					break;
				case WeatherUtilites.MES_UPDATE_TIME:
					updateTime(mcontext);
					QHDupdateDateAndWeek(mcontext);
					if(mWeatherWidgetSquareWeatherView.mweather_widget_square_icon.getTag()!=null){
						if(mWeatherWidgetSquareWeatherView.mweather_widget_square_icon.getTag().toString().contains("d") && IsDaytime() ||
							mWeatherWidgetSquareWeatherView.mweather_widget_square_icon.getTag().toString().contains("n") && !IsDaytime()){
		        			;
		        		}else{
		        			updateWeather(mcontext);
		        		}
		        	}
					break;
				default:
					break;
				}
			}
		};
	}

	public void saveCityName(final Context context)
    {
//		String cityname = null ;
		final int lan = WeatherUtilites.getLan();
		if(!WeatherUtilites.findForPackage(context,WeatherUtilites.SINA_PACKAGENAME)){
			new Thread() {
				@Override
				public void run() {
					String cityname = WeatherUtilites.getCityName(context, lan);
					showLog("settings save cityname= " + cityname);
			    	Message message = mHandler.obtainMessage(WeatherUtilites.MES_UPDATE_LOCAL_WEATHER_CITYNAME, cityname);  
					mHandler.sendMessage(message);
				}
			}.start();
		}
		else{
			new Thread() {
				@Override
				public void run() {
					String tcityname = getTqtCityName(context);
					showLog("dispalyCityName tcityname = " + tcityname);
					Message message = mHandler.obtainMessage(WeatherUtilites.MES_UPDATE_WEATHER_CITYNAME, tcityname);  
					mHandler.sendMessage(message);
				}
			}.start();
/*			if(cityname==null||TextUtils.isEmpty(cityname))
				mweather_widget_day_city.setBackgroundColor(0x00000000);
			else
				mweather_widget_day_city.setBackgroundDrawable(mapp.mLauncherContext.getDrawable(R.drawable.weather_widgt_cityname));;
*/		}
    }

	private String getTqtCityName(final Context context) {
		final int lan = WeatherUtilites.getLan();
		String tcityname = null ;
		String name = WeatherUtilites.getWeatherDetailsAppName(context);
		if(name!=null&&!TextUtils.isEmpty(name)){
			City city = WeatherUtilites.getcityByKey(context,name);
			if(city!=null){
				switch(lan){
				case 0:
					tcityname = city.getencity();
					break;
				case 1:
					tcityname = city.getzhcity();
					break;
				case 2:
					tcityname = city.gettwcity();
					if(tcityname==null||TextUtils.isEmpty(tcityname))
						tcityname = city.getzhcity();
					break;
				default:
					break;
				}
			}
			if(tcityname==null||TextUtils.isEmpty(tcityname))
				tcityname = name;
		}
		return tcityname;
	}
	private void updatelayout() {
    	View parentview = (View) mweatherwidgetsquareview.getParent();
    	mweatherwidgetsquareview.requestLayout();
    	if (parentview != null)
    		parentview.requestLayout();
    }

    private class FormatChangeObserver extends ContentObserver {
        public FormatChangeObserver() {
            super(new Handler());
        }
        @Override
        public void onChange(boolean selfChange) {
        	displayTime(mcontext);
        }
    }

    public void setDefaultWeatherBG() {
    	if (mapp == null)
    		mapp = (LauncherApplication) getContext().getApplicationContext();
//   		mweather_widget_square_bg.setImageDrawable(mapp.mLauncherContext.getDrawable(R.drawable.weather_widget_magic_bg));
//    	mweather_widget_square_bg.setBackgroundColor(0x77ff0000);
    }

    /*public boolean updateDataFromLeDesktopSettings(Context context) {
        int isNetworkEnabled = Settings.System.getInt(
        		context.getContentResolver(), PREF_NETWORK_ENABLER, 0);
        if (isNetworkEnabled == 1) {
            mNetworkEnabled = true;
        } else {
            mNetworkEnabled = false;
        }
        return true;
    } */

    boolean IsDaytime() {
        GregorianCalendar gcal = new GregorianCalendar();
        int hour = gcal.get(Calendar.HOUR_OF_DAY);
        return (hour >= 6 && hour < 18);
    }

    private void updateTime(Context context) {
    	dis12or24 = false;
		dis12or24 = setTimeFormat(context);
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		final String AmPm = GetAMPM(context, calendar);
		final String curenttime = DateFormat.format(mTimeFormat, calendar).toString();
		showLog("displayTime curenttime = " + curenttime);
		mweather_widget_square_am_pm.setTextColor(
				mapp.mLauncherContext.getColor(R.color.weather_square_widget_AMPM_color, R.color.weather_square_widget_AMPM_color));
		mweather_widget_square_am_pm.setText(AmPm);

		try{
			int hour = 0;
			if (dis12or24)
				hour = calendar.get(Calendar.HOUR_OF_DAY);
			else {
				hour = calendar.get(Calendar.HOUR);
				if (hour == 0)
					hour = 12;
			}
			int currentnum = hour/10;
			mweather_widget_square_time_0.setImageDrawable(
					mapp.mLauncherContext.getDrawable(WeatherUtilites.SQUARE_NUMBER[currentnum]));
			currentnum = hour%10;
			mweather_widget_square_time_1.setImageDrawable(
					mapp.mLauncherContext.getDrawable(WeatherUtilites.SQUARE_NUMBER[currentnum]));
			int minute = calendar.get(Calendar.MINUTE);
			currentnum = minute/10;
			mweather_widget_square_time_2.setImageDrawable(
					mapp.mLauncherContext.getDrawable(WeatherUtilites.SQUARE_NUMBER[currentnum]));
			currentnum = minute%10;
			mweather_widget_square_time_3.setImageDrawable(
					mapp.mLauncherContext.getDrawable(WeatherUtilites.SQUARE_NUMBER[currentnum]));
			mweather_widget_square_colon.setImageDrawable(
					mapp.mLauncherContext.getDrawable(R.drawable.weather_widget_square_colon));
		} catch(ArrayIndexOutOfBoundsException ex) {
			ex.printStackTrace();
		}
    }

    private void displayTime(Context context){
    	new Thread() {
			@Override
			public void run() {
				Message message = mHandler.obtainMessage(WeatherUtilites.MES_UPDATE_TIME);  
				mHandler.sendMessage(message);
			}
		}.start();
    }

    private boolean setTimeFormat(Context context) {
    	if (get24HourMode(context)) {
    		mTimeFormat = M24;
    		return true;
    	}
    	else
    	{
    		mTimeFormat = M12;
    		return false;
    	}
    }

    private String GetAMPM(final Context context,Calendar c) {
		if (!dis12or24) {
			if (c.get(Calendar.AM_PM) == 0)
				return  mres.getString(R.string.weather_clock_forenoon);
			else
				return  mres.getString(R.string.weather_clock_afternoon);
		} else
			return "";
	}

    public boolean get24HourMode(final Context context) {
        return android.text.format.DateFormat.is24HourFormat(context);
    }

    private String getDateStr(Calendar calendar ,String language,Context context) {
    	Date now = calendar.getTime();
        String dateFormat = Settings.System.getString(context.getContentResolver(), "date_format");
        if (null == dateFormat) {
            if (language.equals("zh")) {
                return DateFormat.format(DEFALUT_DATE_FORMAT_CN, now).toString();
            } else {
                return DateFormat.format(DEFALUT_DATE_FORMAT_EN, now).toString();
            }
        } else {
            java.text.DateFormat shortDateFormat = DateFormat.getDateFormat(context);
            return shortDateFormat.format(now);
        }
    }

    private String getDateStr(Calendar calendar, String language) {
	    int day = calendar.get(Calendar.DATE);
	    int month = calendar.get(Calendar.MONTH) + 1;
	    if (language.startsWith("zh")) {
            return month + "月" + day + "日";
        } else {
            return WeatherUtilites.MONTH.get(month) + " " + day;
        }
    }
    private void QHDupdateDateAndWeek(final Context context) {
    	Configuration configuration = mres.getConfiguration();
    	String language = configuration.locale.getLanguage();
    	Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
    	final String dateString = getDateStr(calendar, language);
    	final int flagsWeek = DateUtils.FORMAT_SHOW_WEEKDAY; 
        String weekStr = (String)DateUtils.formatDateTime(context, 
    			System.currentTimeMillis(), flagsWeek);

		mweather_widget_square_time_date.setTextColor(
				mapp.mLauncherContext.getColor(R.color.weather_square_widget_date_color,
						R.color.weather_square_widget_date_color));
		mweather_widget_square_time_week.setTextColor(
				mapp.mLauncherContext.getColor(R.color.weather_square_widget_week_color,
						R.color.weather_square_widget_week_color));
    	mweather_widget_square_time_week.setText(" "+weekStr);
    	mweather_widget_square_time_date.setText(dateString);

    	/*if (WeatherUtilites.isInDefaultTheme(context)) {
    		try{
        		mweather_widget_details_content_date.setText(dateString + " " + weekStr);
        		for (int i=0; i<3; i++) {
        			mweather_widget_details_content_layout_week[i].setText(WeatherUtilites.getWeek(context, i+1));
        		}
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    	}*/
    }

    public void registerWeatherIntentReceiver() {   
    	mIntentReceiver = new IntentReceiver();
        IntentFilter eventFilter = new IntentFilter();
		eventFilter.addAction(WeatherUtilites.ACTION_WEATHER_WIDGET_TIME_TICK);
		eventFilter.addAction(WeatherUtilites.ACTION_LOCATION_CHANGE);
		eventFilter.addAction(WeatherUtilites.ACTION_WEATHER_NETWORK_STATE_CHANGE);
		eventFilter.addAction(WeatherUtilites.ACTION_TIEM_CHANGE);
		eventFilter.addAction(WeatherUtilites.ACTION_UPDATE_DEFAILD_WEATHER);
		eventFilter.addAction(WeatherUtilites.ACTION_UPDATE_WEATHER);
		eventFilter.addAction(WeatherUtilites.ACTION_UPDATE_WEATHER_FAILED);
		eventFilter.addAction(ACTION_CHANGE_LETHEME);
		eventFilter.addAction(WeatherUtilites.ACTION_WEATHER_WIDGET_CARRIER_UPDATE);
		eventFilter.addAction(WeatherUtilites.ACTION_WEATHER_ANIMATE_STOP);
		eventFilter.addAction(WeatherUtilites.ACTION_WEATHER_WIDGET_CHANGECITY_FROMSINA);
		eventFilter.addAction(WeatherUtilites.ACTION_WEATHER_WIDGET_ADDCITY_FROMSINA);
		eventFilter.addAction(WeatherUtilites.ACTION_WEATHER_WIDGET_DELETECITY_FROMSINA);
		eventFilter.addAction(WeatherUtilites.ACTION_WEATHER_WIDGET_UPDATECITY_FROMSINA);
		eventFilter.addAction(WeatherUtilites.ACTION_WEATHER_WIDGET_PACKAGE_UPDATE);
		mcontext.registerReceiver(mIntentReceiver, eventFilter);
    }
    private Context mreceivercontext;
    private class IntentReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent == null) 
				return ;
			mreceivercontext = context;
			if (!WeatherUtilites.hasInstances(context, THIS_PUSHMAILWIDGET)) {
				 showLog("square has NO Instances");
			    	try {
			    		if (mIntentReceiver != null)
			    			context.unregisterReceiver(mIntentReceiver);
			    	} catch (IllegalArgumentException e) {
			    		e.printStackTrace();
			    	}
				return;
			}
            String action = intent.getAction();
            showLog(" square widget *********recievied " + action);
            if (action.equals(WeatherUtilites.ACTION_WEATHER_WIDGET_TIME_TICK))
                mHandler.sendEmptyMessage(WeatherUtilites.MES_TIME_TICK);
            else if (action.equals(WeatherUtilites.ACTION_LOCATION_CHANGE)) {
                mHandler.sendEmptyMessage(WeatherUtilites.MES_LOCATION_CHANGE);
            } else if (action.equals(WeatherUtilites.ACTION_WEATHER_NETWORK_STATE_CHANGE)) {
            	/*mNetworkEnabled = intent.getBooleanExtra(
                        EXTRA_NETWORK_ENABLED, false);*/
            	mNetworkEnabled = SettingsValue.isNetworkEnabled(context);
            	showLog("magic Network is open ?:  " + mNetworkEnabled);
                mHandler.sendEmptyMessage(WeatherUtilites.MES_WEATHER_NETWORK_STATE_CHANGE);
            } else if (action.equals(Intent.ACTION_SCREEN_ON)) {
            	mHandler.sendEmptyMessage(WeatherUtilites.MES_ACTION_SCREEN_ON);
            } else if (action.equals(WeatherUtilites.ACTION_UPDATE_WEATHER)) {
            	mHandler.sendEmptyMessage(WeatherUtilites.MES_UPDATE_WEATHER);
            } else if (action.equals(WeatherUtilites.ACTION_UPDATE_WEATHER_FAILED)) {
            	mHandler.sendEmptyMessage(WeatherUtilites.MES_UPDATE_WEATHER_FAILED);
            } else if (WeatherUtilites.ACTION_TIEM_CHANGE.equals(action)) {
            	mHandler.sendEmptyMessage(WeatherUtilites.MES_TIEM_CHANGE);
            } else if (ACTION_CHANGE_LETHEME.equals(action)) {
            	initlayoutQHD(context);
//            	initdata(context);
            	mHandler.sendEmptyMessage(WeatherUtilites.MES_WEATHER_WIDGET_INIT);
            } else if (WeatherUtilites.ACTION_WEATHER_WIDGET_CHANGECITY_FROMSINA.equals(action) ||
            		WeatherUtilites.ACTION_WEATHER_WIDGET_ADDCITY_FROMSINA.equals(action) ||
            		WeatherUtilites.ACTION_WEATHER_WIDGET_DELETECITY_FROMSINA.equals(action) ||
            		WeatherUtilites.ACTION_WEATHER_WIDGET_UPDATECITY_FROMSINA.equals(action)) {
            	mHandler.sendEmptyMessage(WeatherUtilites.MES_CHANGE_CITY_WEATHER);
            } else if (WeatherUtilites.ACTION_WEATHER_WIDGET_PACKAGE_UPDATE.equals(action)) {
            	final String packageName = intent.getStringExtra("packageName");
            	if (packageName != null && packageName.contains(WeatherUtilites.SINA_PACKAGENAME)) {
            		mHandler.sendEmptyMessage(WeatherUtilites.MES_WEATHER_WIDGET_INIT);
            	}
            } else if (action.equals(WeatherUtilites.ACTION_WEATHER_ANIMATE_STOP)) {
            	mHandler.sendEmptyMessage(WeatherUtilites.MES_WEATHER_ANIMATE_STOP);
            }
		}
    }

	public boolean isWeatherOutOfDate24(final Context context) {
	    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
	    final int currenttime = (int)(calendar.getTimeInMillis()/3600000);
	    int oldcurrenttime = 0;
		try {
			oldcurrenttime = (int)(Settings.System.getLong(context.getContentResolver(), WeatherUtilites.WEATHER_UPDATETIME) / 3600000);
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    if (Math.abs(oldcurrenttime - currenttime ) >= 24) {
	        showLog("isWeatherOutOfDate24() return true");
	        return true;
	    } else {
	    	showLog("isWeatherOutOfDate24() return false");
	        return false;
	    }
	}

	private void updateWeatherBySoure(final Context context, List<WeatherDetails> weatherdetails) {
    	if (weatherdetails != null && weatherdetails.size() > 0) {
    		Configuration configuration = mres.getConfiguration();
    		String language = configuration.locale.toString();
    		for (int i=0; i<1 && i<weatherdetails.size(); i++) {
    			WeatherDetails weatherdetail = null;
	    		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
	    		Date now = calendar.getTime();
	    		now.setDate(now.getDate() + i);
				final String data = DateFormat.format(DEFALUT_DATE_FORMAT_CN, now).toString();
				for (int j=0; j<weatherdetails.size(); j++) {
	    			if (data.equals(weatherdetails.get(j).mcityDate)) {
	    				weatherdetail = weatherdetails.get(j);
	    				break;
	    			}
	    		}
				if (weatherdetail == null) {
					if (i == 0) {
						showLog("no data0****************************");
						setDefaultWeatherPic();
					} else if(i == 1) {
						showLog("no data1****************************");
						setDefaultWeatherPic0();
					} else if(i == 2) {
						showLog("no data2****************************");
						setDefaultWeatherPic1();
					} else if(i == 3) {
						showLog("no data3****************************");
						setDefaultWeatherPic2();
        			}
	    			continue;
	    		}

				String content = null;
	    		try {
	        		if (language.equals("zh_CN")) 
	        			content = weatherdetail.mcityStatus1;
	        		else if (language.equals("zh_TW")) 
	        			content = WeatherUtilites.ICON_SINA_TW_STRING_MAP.get(weatherdetail.mcityStatus1);
	        		else
	        			content = WeatherUtilites.ICON_SINA_EN_STRING_MAP.get(weatherdetail.mcityStatus1);
	        		if (content==null || TextUtils.isEmpty(content))
	        			content = weatherdetail.mcityStatus1;
	    		} catch(Exception e) {
	            	e.printStackTrace();
	            	content = weatherdetail.mcityStatus1;
	    		}

	    		String iconimage = WeatherUtilites.ICON_SQUARE_SINA_MAP.get(weatherdetail.mcityStatus1);
	    		showLog(i + "  " + data + "  iconimage = " + iconimage + "  " + content);
    			if (i == 0) {
//    	            Settings.System.putString(mcontext.getContentResolver(), WeatherUtilites.CONDITION,content);
//    	            Settings.System.putString(mcontext.getContentResolver(), WeatherUtilites.WEATHERTEMPS,weatherdetail.mcityTemperature);
    				mWeatherWidgetSquareWeatherView.mweather_widget_square_description.setText(content);
    				mWeatherWidgetSquareWeatherView.mweather_widget_square_description.setTextColor(
    		    			mapp.mLauncherContext.getColor(R.color.weather_square_widget_content_color, R.color.weather_square_widget_content_color));
    				mWeatherWidgetSquareWeatherView.mweather_widget_square_temperature.setText(weatherdetail.mcityTemperature);
    				mWeatherWidgetSquareWeatherView.mweather_widget_square_temperature.setTextColor(
    		    			mapp.mLauncherContext.getColor(R.color.weather_square_widget_temp_color, R.color.weather_square_widget_temp_color));    		
    	    		
    	        	if (iconimage == null || TextUtils.isEmpty(iconimage)) {
    		        	setDefaultWeatherPic();
//    		            Settings.System.putString(mcontext.getContentResolver(), WeatherUtilites.CONDITION,"");
//    		            Settings.System.putString(mcontext.getContentResolver(), WeatherUtilites.WEATHERTEMPS,"");
    	        	}
    	        	else
    	        	{
    	                if (!IsDaytime()) {
    	                	if (iconimage.contains("d"))
    	                		iconimage = iconimage.replace("d", "n");
    	                } else {
    	                	if (iconimage.contains("n"))
    	                		iconimage = iconimage.replace("n", "d");
    	                }
    	                int iconid = -1;
    	    	        try {
    		        		final String img = "weather_square_widget_"+iconimage;
    		        		R2.echo("img="+img);
    		        		Log.d("czz",i+"img="+img);
    		        		iconid = WeatherUtilites.SQUARE_ICON_MAP.get(iconimage);
    		        		if (iconid == -1) {
    		        			setDefaultWeatherPic();
    		        		} else {
    		        			mWeatherWidgetSquareWeatherView.mweather_widget_square_empty.setVisibility(View.GONE);
    		        			mWeatherWidgetSquareWeatherView.mweather_widget_square_info.setVisibility(VISIBLE);
    		        			mWeatherWidgetSquareWeatherView.mweather_widget_square_icon.setImageDrawable(mapp.mLauncherContext.getDrawable(iconid));
    		        			/*mweather_widget_square_icon.setImageDrawable(
    		        					mapp.mLauncherContext.getDrawable(R.drawable.weather_square_icon));*/
    		        			mWeatherWidgetSquareWeatherView.mweather_widget_square_icon.setTag(iconimage);
    		        			/*if (WeatherUtilites.isInDefaultTheme(context)) {
    		        				try {
    		        					mweather_widget_details_content_icon.setImageDrawable(mapp.mLauncherContext.getDrawable(iconid));
    		        				} catch(Exception e) {
    			            			e.printStackTrace();
    			            		}
    		        			}*/
			                	WeatherUtilites.saveWeatherIconUpdate(context,data,iconimage,true);
    		        		}

    	                }catch(Exception e){
    	                	e.printStackTrace();
    	                	iconid = -1;
    	                	setDefaultWeatherPic();
    	                }
    	            }
    	        	/*if (WeatherUtilites.isInDefaultTheme(context)) {
	                	try {
	                		mweather_widget_details_content_content.setText(content);
	                		mweather_widget_details_content_wind.setText((weatherdetail.mcityDirection1 == null ? "风力" : (weatherdetail.mcityDirection1 + "风")) + weatherdetail.mcityPower);
		                	mweather_widget_details_content_temp.setText(weatherdetail.mcityTemperature);
		                    
		                    if (weatherdetail.mcityZwxL == null && TextUtils.isEmpty(weatherdetail.mcityZwxL)) {
		                		weatherdetail.mcityZwxL = "";
			                }
		                    mweather_widget_details_content_index_uv.setText(" 紫外线 : " + weatherdetail.mcityZwxL);

		                	if (weatherdetail.mcityPollutionL == null && TextUtils.isEmpty(weatherdetail.mcityPollutionL)) {
		                		weatherdetail.mcityPollutionL = "";
		                 	}
		               		mweather_widget_details_content_index_polution.setText(" 污染 : " + weatherdetail.mcityPollutionL);
		               		Log.i("ss", " 污染 : " + weatherdetail.mcityPollutionL + " 紫外线 : " + weatherdetail.mcityZwxL + " language is " + language);
		               		
	            		} catch(Exception e) {
	            			e.printStackTrace();
	            		}
	                }*/
    	        	context.sendBroadcast(new Intent("com.lenovo.leos.widgets.weather.update_weather"));
    			}
    			/*if (i>0 && WeatherUtilites.isInDefaultTheme(context)) {
                	try {
                		if (iconimage == null || TextUtils.isEmpty(iconimage)) {
        		        	setDefaultWeatherPic0();
        	        	} else {
        	        		if (!IsDaytime()) {
        	                	if (iconimage.contains("d"))
        	                		iconimage = iconimage.replace("d", "n");
        	                } else {
        	                	if (iconimage.contains("n"))
        	                		iconimage = iconimage.replace("n", "d");
        	                }
        	                int iconid = -1;
        	                try {
        	                	final String img = "weather_square_widget_"+iconimage;
        		        		R2.echo("img="+img);
        		        		Log.d("czz",i+"img="+img);
        		        		iconid = WeatherUtilites.SQUARE_ICON_MAP.get(iconimage);
        		        		if (iconid == -1) {
        		        			setDefaultWeatherPic0();
        		        		} else {
        		        			mweather_widget_details_content_layout_icon[i - 1].setImageDrawable(mapp.mLauncherContext.getDrawable(iconid));
                            		mweather_widget_details_content_layout_content[i - 1].setText(content);
                                	mweather_widget_details_content_layout_temp[i - 1].setText(weatherdetail.mcityTemperature);
        		        		}
        	                } catch(Exception e) {
                    			e.printStackTrace();
                    		}
        	        	}
            		} catch(Exception e) {
            			e.printStackTrace();
            		}
                }*/
    		}
    		/*if (weatherdetails.size() < 4) {
    			for (int k = weatherdetails.size(); k<4; k++) {
    				try {
    					mweather_widget_details_content_layout_icon[k - 1].setImageDrawable(
			    				mapp.mLauncherContext.getDrawable(R.drawable.weather_widget_big_default));
                		mweather_widget_details_content_layout_content[k - 1].setText("");
                    	mweather_widget_details_content_layout_temp[k - 1].setText("");
            		} catch(Exception e) {
            			e.printStackTrace();
            		}
    			}
    		}*/
    		
    		mweather_widget_square_weather.removeAllViews();
			mweather_widget_square_weather.addView(mWeatherWidgetSquareWeatherView, mrp);
    	} else {
    		clearWeatherInfo();
    	}
	}

	private void updateWeatherByApp(final Context context,List<WeatherApp> weatherapps) {
    	if (weatherapps != null && weatherapps.size()>2) {
    		Configuration configuration = mres.getConfiguration();
    		String language = configuration.locale.toString();
    		for (int i=0; i<4 && i<weatherapps.size(); i++) {
    			WeatherApp weatherapp = null;
        		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        		Date now = calendar.getTime();
    			final String data = DateFormat.format(DEFALUT_DATE_FORMAT_CN, now).toString();
        		for (int j=0; j<weatherapps.size(); j++) {
        			if (data.equals(weatherapps.get(j).mcityData)) {
        				weatherapp = weatherapps.get(j);
        				break;
        			}
        		}
        		if (weatherapp == null) {
        			if (i == 0)
						setDefaultWeatherPic();
					else if ( i == 1)
						setDefaultWeatherPic0();
					else if (i == 2)
						setDefaultWeatherPic1();
					else if ( i == 3) {
						setDefaultWeatherPic2();
        			}
	    			continue;
        		}

        		String content = null;
        		showLog(weatherapp.mcityData + " weather is " + weatherapp.mcityName + " " + weatherapp.mcityStatus1);
        		if (weatherapp.mcityStatus1 != null && !weatherapp.mcityStatus1.equals(weatherapp.mcityStatus2)) {
            		try {
    	        		if (language.equals("zh_CN")) {
    	        			content = weatherapp.mcityStatus1 + "转" + weatherapp.mcityStatus2;
    	        		}
    	        		else if (language.equals("zh_TW")) {
    	        			content = weatherapp.mcityStatus1 + "转" + weatherapp.mcityStatus2;
    	        		}
    	        		else
    	        		{
    	        			if (IsDaytime())
    	        				content = WeatherUtilites.ICON_SINA_EN_APP_STRING_MAP.get(weatherapp.mcityStatusCode1);
    	        			else
    	        				content = WeatherUtilites.ICON_SINA_EN_APP_STRING_MAP.get(weatherapp.mcityStatusCode2);
    	        		}
    	        		if (content == null || TextUtils.isEmpty(content))
    	        			content = weatherapp.mcityStatus1 + "转" + weatherapp.mcityStatus2;
            		} catch(Exception e) {
                    	e.printStackTrace();
                    	content = weatherapp.mcityStatus1 + "转" + weatherapp.mcityStatus2;
            		}
    			} else {
            		try {
    	        		if (language.equals("zh_CN")) { 
    	        			content = weatherapp.mcityStatus1;
    	        		}
    	        		else if (language.equals("zh_TW")) {
    	        			content = weatherapp.mcityStatus1;
    	        		}
    	        		else
    	        		{
    	        			if (IsDaytime())
    	        				content = WeatherUtilites.ICON_SINA_EN_APP_STRING_MAP.get(weatherapp.mcityStatusCode1);
    	        			else
    	        				content = WeatherUtilites.ICON_SINA_EN_APP_STRING_MAP.get(weatherapp.mcityStatusCode2);
    	        		}
    	        		if (content == null || TextUtils.isEmpty(content))
    	        			content = weatherapp.mcityStatus1;
            		} catch(Exception e) {
                    	e.printStackTrace();
                    	content = weatherapp.mcityStatus1;
            		}
    			}

        		String iconimage = null;
                /*String low = String.valueOf(Math.min(weatherapp.mcityTemperature1, weatherapp.mcityTemperature2));
                String high = String.format("%1$d\u00B0", Math.max(weatherapp.mcityTemperature1, weatherapp.mcityTemperature2));
                String temp = low + "~" + high + "C";*/
                if (i == 0) {
                	WeatheDetailsApp weathedetailsapps  = WeatherUtilites.getWeatherDetailsApp(context);
                	String temp = String.format("%1$d\u00B0", weathedetailsapps.mcityTemperature) + "C";//String.valueOf(weathedetailsapps.mcityTemperature);
                	if (IsDaytime())
        				iconimage = WeatherUtilites.ICON_SINA_APP_MAP.get(weatherapp.mcityStatusCode1);
        			else
        				iconimage = WeatherUtilites.ICON_SINA_APP_MAP.get(weatherapp.mcityStatusCode2);

                	mWeatherWidgetSquareWeatherView.mweather_widget_square_description.setText(content);
                	mWeatherWidgetSquareWeatherView.mweather_widget_square_description.setTextColor(
        	    			mapp.mLauncherContext.getColor(R.color.weather_square_widget_content_color,
        	    					R.color.weather_square_widget_content_color));
                	mWeatherWidgetSquareWeatherView.mweather_widget_square_temperature.setText(temp);
                	mWeatherWidgetSquareWeatherView.mweather_widget_square_temperature.setTextColor(
       	    				mapp.mLauncherContext.getColor(R.color.weather_square_widget_temp_color,
       	    						R.color.weather_square_widget_temp_color));    		
            		showLog("iconimage is " + iconimage);
                	if (iconimage == null || TextUtils.isEmpty(iconimage))
                		setDefaultWeatherPic();
                	else {
                        if (!IsDaytime()) {
                        	if(iconimage.contains("d"))
                        		iconimage = iconimage.replace("d", "n");
                        } else {
                        	if (iconimage.contains("n"))
                        		iconimage = iconimage.replace("n", "d");
                        }
                        int iconid = -1;
            	        try {
        	        		final String img = "weather_square_widget_" + iconimage;
        	        		showLog("tqt imgage is " + img);
        	        		iconid = WeatherUtilites.SQUARE_ICON_MAP.get(iconimage);
        	        		if (iconid == -1) {
    		        			setDefaultWeatherPic();
    		        		} else {
    		        			mWeatherWidgetSquareWeatherView.mweather_widget_square_icon.setVisibility(VISIBLE);
    		        			/*mweather_widget_square_icon.setImageDrawable(
    		        					mapp.mLauncherContext.getDrawable(R.drawable.weather_square_icon));*/
    		        			mWeatherWidgetSquareWeatherView.mweather_widget_square_icon.setImageDrawable(mapp.mLauncherContext.getDrawable(iconid));
    		        			mWeatherWidgetSquareWeatherView.mweather_widget_square_icon.setTag(iconimage);
			                	WeatherUtilites.saveWeatherIconUpdate(context,data,iconimage,true);
    		        		}

                        } catch(Exception e) {
                        	e.printStackTrace();
                        	iconid = -1;
                        	setDefaultWeatherPic();
                        }
                    }
                }
                /*if (WeatherUtilites.isInDefaultTheme(context)) {
                	try {
                		if (i == 0) {
                        	mweather_widget_details_content_city.setText(WeatherUtilites.getCityName(context, WeatherUtilites.getLan()) + " " + content);
                        	mweather_widget_details_content_temp.setText(temp);
                        } else {
                        	mweather_widget_details_content_layout_content[i - 1].setText(content);
                        	mweather_widget_details_content_layout_temp[i - 1].setText(temp);
                        }
            		}catch(Exception e){
            			e.printStackTrace();
            		}
                }*/
    		}
    		mweather_widget_square_weather.removeAllViews();
			mweather_widget_square_weather.addView(mWeatherWidgetSquareWeatherView, mrp);
    	} else {
    		clearWeatherInfo();
    	}
	}

	private void updateWeather(final Context context) {
		if (!WeatherUtilites.findForPackage(context, WeatherUtilites.SINA_PACKAGENAME)) {
			new Thread() {
				@Override
				public void run() {
					try{
						WeatherUtilites.saveWeatherIconUpdate(context, "", "", false);
						List<WeatherDetails> weatherdetails  = null;
						final String cityName =  WeatherUtilites.getCityName(context, 1);
						//cityName = Settings.System.getString(context.getContentResolver(), WeatherUtilites.CITY_NAME);
						showLog("updateWeather and cityname is " + cityName);
						if (cityName != null && cityName.length()>0) {
							weatherdetails  = WeatherUtilites.getWeatherDetails(context);
						}
						Message message = mHandler.obtainMessage(WeatherUtilites.MES_UPDATE_SERVICE_WEATHER_DETAILS, weatherdetails);  
						mHandler.sendMessage(message);
					} catch(Exception ex) {
						ex.printStackTrace();
					}
				}
			}.start();
		} else {
			new Thread() {
				@Override
				public void run() {
					try {
						WeatherUtilites.saveWeatherIconUpdate(context, "", "", false);
						updateweatherdata(context);
					} catch(IllegalStateException ex) {
						ex.printStackTrace();
					}
				}
	    	}.start();
		}
	}

	private void updateweatherdata(final Context context) {
		List<WeatherApp> weatherapps  = WeatherUtilites.getWeatherApp(context);
		Message message = mHandler.obtainMessage(WeatherUtilites.MES_UPDATE_WEATHER_DETAILS, weatherapps);  
		mHandler.sendMessage(message);
	}

	public void clearWeatherInfo() { 
    	setDefaultWeatherPic();
    	setDefaultWeatherPic0();
    	setDefaultWeatherPic1();
    	setDefaultWeatherPic2();
    }

	public void setDefaultWeatherPic() {
		mWeatherWidgetSquareWeatherView.mweather_widget_square_empty.setVisibility(View.VISIBLE);
		mWeatherWidgetSquareWeatherView.mweather_widget_square_info.setVisibility(GONE);
		Settings.System.putString(mcontext.getContentResolver(), "city_name", "");
		/*mweather_widget_square_temperature.setText("");
        mweather_widget_square_description.setText("");
        mweather_widget_square_icon.setImageDrawable(
				mapp.mLauncherContext.getDrawable(R.drawable.weather_square_icon));*/
        /*mweather_widget_square_icon.setImageDrawable(
				mapp.mLauncherContext.getDrawable(R.drawable.weather_widget_big_default));*/
    	if (WeatherUtilites.isInDefaultTheme(mcontext)) {
    		/*mweather_widget_details_content_icon.setImageDrawable(
    				mapp.mLauncherContext.getDrawable(R.drawable.weather_widget_big_default));
    		mweather_widget_details_content_temp.setText("");
    		mweather_widget_details_content_wind.setText("");
    		mweather_widget_details_content_content.setText("");
            mweather_widget_details_content_index_uv.setText(" 紫外线 : ");
       		mweather_widget_details_content_index_polution.setText(" 污染 : ");*/
    	}
    }

	public void setDefaultWeatherPic0(){
    	if(WeatherUtilites.isInDefaultTheme(mcontext)){
    		/*mweather_widget_details_content_layout_icon[0].setImageDrawable(
    				mapp.mLauncherContext.getDrawable(R.drawable.weather_widget_big_default));
    		mweather_widget_details_content_layout_content[0].setText("");
    		mweather_widget_details_content_layout_temp[0].setText("");*/
    	}
    }

	public void setDefaultWeatherPic1(){
    	if(WeatherUtilites.isInDefaultTheme(mcontext)){
    		/*mweather_widget_details_content_layout_icon[1].setImageDrawable(
    				mapp.mLauncherContext.getDrawable(R.drawable.weather_widget_big_default));
    		mweather_widget_details_content_layout_content[1].setText("");
    		mweather_widget_details_content_layout_temp[1].setText("");*/
    	}
    }

	public void setDefaultWeatherPic2(){
    	if(WeatherUtilites.isInDefaultTheme(mcontext)){
    		/*mweather_widget_details_content_layout_icon[2].setImageDrawable(
    				mapp.mLauncherContext.getDrawable(R.drawable.weather_widget_big_default));
    		mweather_widget_details_content_layout_content[2].setText("");
    		mweather_widget_details_content_layout_temp[2].setText("");*/
    	}
    }

	private void getScreenAnimation(final Context context,String action) {
		Intent intent = new Intent(action);
		intent.setPackage("com.lenovo.leos.weatheranimation");
		intent.setClassName("com.lenovo.leos.weatheranimation", "com.lenovo.leos.weatheranimation.WeatherAnimService");
		PackageManager mPackageManager = context.getPackageManager();
		List<ResolveInfo> list = mPackageManager.queryIntentServices(
		        intent, 0);
		if (list.size()>0) {
			context.startService(intent);
		}
    }

	@Override
    protected void onAttachedToWindow()
    {
    	showLog("onAttachedToWindow and " + getMposx() + " and " + getMposy());
    	mHandler.sendEmptyMessage(WeatherUtilites.MES_WEATHER_WIDGET_INIT);
    	registerWeatherIntentReceiver();
	}

	@Override
    protected void onDetachedFromWindow() {
		showLog("onDetachedFromWindow");
    	if (mFormatChangeObserver != null)
    		mcontext.getContentResolver().unregisterContentObserver(mFormatChangeObserver);
    	if (mreceivercontext != null) {
	    	try {
	    		if (mIntentReceiver != null) {
	    			showLog("unregister broadcast receiver");
	    			mreceivercontext.unregisterReceiver(mIntentReceiver);
	    		}
	    	} catch (IllegalArgumentException e) { 
	    		e.printStackTrace();
	    	}
    	}
//    	if(mViewCache!=null&&!mViewCache.isRecycled()){
//    		mViewCache.recycle();
//    		mViewCache = null;
//    	}
    }

	public void setTimeLayoutlistener(final Context context) {
    	
		OnClickListener listener = new OnClickListener() {
    		public void onClick(View v) {
			    boolean res = WeatherUtilites.setOnClickListenerIntent(context,"com.lenovomobile.deskclock");
			    if (!res) {
			        if (!WeatherUtilites.setOnClickListenerIntent(context ,"com.android.deskclock")) {
			        	if (!WeatherUtilites.setOnClickListenerIntent(mcontext, "com.ontim.clock")) {
	        				if (!WeatherUtilites.setOnClickListenerIntent(mcontext ,"com.lenovo.deskclock")) {
	        					Intent intent = new Intent("android.settings.DATE_SETTINGS");
	        					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        	    			try {
	        	    				context.startActivity(intent);
	        	    			} catch (ActivityNotFoundException e) {
	        	    				e.printStackTrace();
	        	    			}
	        				}
			        	}
			        }
			    }
			    /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2012-12-21 S*/    
	            Reaper.processReaper( getContext(), 
	            	   Reaper.REAPER_EVENT_CATEGORY_WIDGET, 
	    			   Reaper.REAPER_EVENT_ACTION_WIDGET_MAGICWEATHER,
	    			   "Clock", 
	    			   Reaper.REAPER_NO_INT_VALUE );
	            /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2012-12-21 E*/ 
    		}
    	};
    	mweather_widget_square_time.setOnClickListener(listener);
    }

	public void setWeathInfolistener(final Context context) {
    	
    	OnClickListener citylistener = new OnClickListener() {
	   		public void onClick(View v) {
	   			boolean networkEnabled = SettingsValue.isNetworkEnabled(context);
	 			if (networkEnabled) {
    				if (!WeatherUtilites.findForPackage(context,WeatherUtilites.SINA_PACKAGENAME)) {
    					Intent intent = new Intent(Intent.ACTION_MAIN, null);
						intent.addCategory(Intent.CATEGORY_LAUNCHER);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.setPackage(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF);
					    intent.setClassName(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF, "com.lenovo.launcher2.settings.ProvinceActivity");
					    try {
					    	context.startActivity(intent);
					    } catch(ActivityNotFoundException e) {
					    	e.printStackTrace();
		    	        }
    				} else {
    					// 进入天气通后如何设置或者切换城市？
    					/*Intent intent = new Intent("sina.mobile.tianqitong.action.startservice.switch_appwidget_city");
    		   			context.startService(intent);*/
    				}
	 			} else {
					Intent intent = new Intent(Intent.ACTION_MAIN, null);
					intent.addCategory(Intent.CATEGORY_LAUNCHER);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setPackage(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF);
					intent.setClassName(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF, 
							"com.lenovo.launcher2.weather.widget.NetworkAlertActivity");
    	            intent.putExtra(WeatherUtilites.EXTRA_DIALOG_TYPE, 0);
    	            try {
    	            	context.startActivity(intent);
    	            } catch(ActivityNotFoundException e) {
    	            	e.printStackTrace();
    	            }
    			}
	   		}
    	};

    	OnClickListener listener = new OnClickListener() {
    		 public void onClick(View v) {
    			 boolean networkenabled = SettingsValue.isNetworkEnabled(context);
    			 if (networkenabled) {
    				if (!WeatherUtilites.findForPackage(context,WeatherUtilites.SINA_PACKAGENAME)) {
    					Intent intent = new Intent(Intent.ACTION_MAIN, null);
    					intent.addCategory(Intent.CATEGORY_LAUNCHER);
    					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    					intent.setPackage(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF);
    					intent.putExtra(WeatherUtilites.DIALOG_X, getMposx());
    					intent.putExtra(WeatherUtilites.DIALOG_Y, getMposy());
    					final int isConnected = WeatherUtilites.getNetWorkConnectState(context);
    					Log.i("sqno", "isConnected is " + isConnected);
    					if (isConnected == 0) {
    						intent.setClassName(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF, 
        							"com.lenovo.launcher2.weather.widget.WeatherSquareNoDetailsActivity");
    					} else {    					
    						intent.setClassName(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF, 
    								"com.lenovo.launcher2.weather.widget.WeatherSquareDetailsActivity");
    					}
        	            try {
        	            	context.startActivity(intent);
        	            } catch(ActivityNotFoundException e) {
        	            	e.printStackTrace();
        	            }
//    					if (!isWeatherOutOfDate24(context)) {
//    	 		 			if (WeatherUtilites.isInDefaultTheme(context) && mweather_widget_details_pic != null && mweather_widget_details_pic.getwindowstate())
//    	 		 				displayDetailsWindow(context);
    	 		 		/*} else {
    	 		 			showLog("weather is out of date 24hours");
    	 		 		}*/
					} else {
						String tqtcityname = WeatherUtilites.getWeatherDetailsAppName(context);
						Intent intent = null;
						if (tqtcityname != null && !TextUtils.isEmpty(tqtcityname)) {
							intent = new Intent("sina.mobile.tianqitong.SHOW_CITY");
							intent.putExtra("sina.mobile.tianqitong.start_mainactivity_from_widget", true);
						} else {
							intent = new Intent("sina.mobile.tianqitong.main.Splash");
							intent.setClassName("sina.mobile.tianqitong", "sina.mobile.tianqitong.main.Splash");
						}
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						try {
		    	            context.startActivity(intent);
		    	        } catch(ActivityNotFoundException e) {
		    	            	e.printStackTrace();
		    	        }
					}
    				 /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2012-12-21 S*/    
     	            Reaper.processReaper( getContext(), 
     	            	   Reaper.REAPER_EVENT_CATEGORY_WIDGET, 
     	    			   Reaper.REAPER_EVENT_ACTION_WIDGET_MAGICWEATHER,
     	    			   "City", 
     	    			   Reaper.REAPER_NO_INT_VALUE );
     	            /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2012-12-21 E*/ 
    			} else {
					Intent intent = new Intent(Intent.ACTION_MAIN, null);
					intent.addCategory(Intent.CATEGORY_LAUNCHER);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setPackage(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF);
					intent.setClassName(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF, 
							"com.lenovo.launcher2.weather.widget.NetworkAlertActivity");
    	            intent.putExtra(WeatherUtilites.EXTRA_DIALOG_TYPE, 0);
    	            try {
    	            	context.startActivity(intent);
    	            } catch(ActivityNotFoundException e) {
    	            	e.printStackTrace();
    	            }
    			}
    		}
    	};
    	String cityname = WeatherUtilites.getCityName(context, 1);
    	if (cityname != null && !TextUtils.isEmpty(cityname)) {
    		mweather_widget_square_layout.setOnClickListener(listener);
       		mweather_widget_square_bg.setOnClickListener(listener);
    	} else {
    		mweather_widget_square_layout.setOnClickListener(citylistener);
       		mweather_widget_square_bg.setOnClickListener(citylistener);
    	}
    	/*mweather_widget_details_content_city.setOnClickListener(citylistener);
    	mweather_widget_details_content_date.setOnClickListener(citylistener);*/
   		
    }

    /** 
     * 截屏方法 
     * @return 
     */
     private Bitmap shot(int type) {
 	    Bitmap bmp = WeatherUtilites.getBGBitmap(mcontext, mdisplaywidth, mdisplayheight);
 	    showLog("statusBarHeight is " + statusBarHeight);
 	    try {
 		    if (type == 0) {
 			    bmp = Bitmap.createBitmap(bmp, 0, statusBarHeight, mdisplaywidth, (mrealheight) / 2);
 		    }
 		    else{
 		    	bmp = Bitmap.createBitmap(bmp, 0, statusBarHeight + mrealheight / 2, mdisplaywidth, mrealheight / 2);
 		    }
 	    } catch(IllegalArgumentException e) {
 	    	e.printStackTrace();
 	    }
 	    return bmp;
     }

    /*public void displayDetailsWindow(final Context context) {

    	OnClickListener citylistener = new OnClickListener() {
	   		public void onClick(View v) {
	   			mweather_widget_details_pic.hideview();
	   			boolean networkEnabled = SettingsValue.isNetworkEnabled(context);
	 			if (networkEnabled) {
    				if (!WeatherUtilites.findForPackage(context,WeatherUtilites.SINA_PACKAGENAME)) {
    					Intent intent = new Intent(Intent.ACTION_MAIN, null);
						intent.addCategory(Intent.CATEGORY_LAUNCHER);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.setPackage(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF);
					    intent.setClassName(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF, "com.lenovo.launcher2.settings.ProvinceActivity");
					    try {
					    	context.startActivity(intent);
					    } catch(ActivityNotFoundException e) {
					    	e.printStackTrace();
		    	        }
    				} else {
    					// 进入天气通后如何设置或者切换城市？
    					Intent intent = new Intent("sina.mobile.tianqitong.action.startservice.switch_appwidget_city");
    		   			context.startService(intent);
    				}
	 			} else {
					Intent intent = new Intent(Intent.ACTION_MAIN, null);
					intent.addCategory(Intent.CATEGORY_LAUNCHER);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setPackage(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF);
					intent.setClassName(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF, 
							"com.lenovo.launcher2.weather.widget.NetworkAlertActivity");
    	            intent.putExtra(WeatherUtilites.EXTRA_DIALOG_TYPE, 0);
    	            try {
    	            	context.startActivity(intent);
    	            } catch(ActivityNotFoundException e) {
    	            	e.printStackTrace();
    	            }
    			}
	   		}
    	};
    	OnClickListener listener = new OnClickListener(){
      		 public void onClick(View v){
      			mweather_widget_details_pic.hideview();
      		 }
       	};
       	
       	mbitmap = shot(0);
    	BitmapDrawable up = new BitmapDrawable(mbitmap);  
    	mbitmap = shot(1);
    	BitmapDrawable down = new BitmapDrawable(mbitmap);
//    	mweather_widget_details_pic.setOnClickListener(listener);
    	mweatherwidgetdetailsview.setOnClickListener(listener);
    	mweather_widget_details_content_city.setOnClickListener(citylistener);
    	mweather_widget_details_pic.setBitmap(up, down, mdisplaywidth, mrealheight, mweatherwidgetdetailsview, this);
    	mweather_widget_details_pic.showview(true, mrealheight / 4);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams wmParams=new WindowManager.LayoutParams();
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        wmParams.format = PixelFormat.RGBA_8888;

        wmParams.flags=WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        wmParams.x = 0;
        wmParams.y = 0;
        wmParams.width = LayoutParams.MATCH_PARENT;
        wmParams.height = LayoutParams.MATCH_PARENT;
        wm.addView(mweatherwidgetdetailsview, wmParams);
    }*/
//    private Bitmap mViewCache = null;
	private void buildViewCache() {
		destroyDrawingCache();
		Log.i("square", "buildViewCache in " + TAG);
//		Bitmap face = null;
//		try {
//			clearFocus();
//			buildDrawingCache();
//			face = getDrawingCache();
//			if( face != null ){
//				mViewCache = face;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		mcontext.sendBroadcast( new Intent(XViewContainer.ACTION_UPDATE_CACHE) );
	}
//	public Bitmap getsnapCache()
//	{
//		return mViewCache;
//	}
    
    private void showLog(String s) {
    	
    	if (DEBUG) {
    		Log.i(TAG, s);
    	}
    }
	public int getMposx() {
		return mposx;
	}
	public void setMposx(int mposx) {
		this.mposx = mposx;
	}
	public int getMposy() {
		return mposy;
	}
	public void setMposy(int mposy) {
		this.mposy = mposy;
	}
}
