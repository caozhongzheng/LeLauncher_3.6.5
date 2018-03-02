package com.lenovo.launcher2.weather.widget;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;
import com.lenovo.launcher2.customizer.Reaper;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.weather.widget.settings.City;
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
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class WeatherWidgetMagicView extends LinearLayout{
	
	private ImageView mweather_widget_magic_bg;
	private TextView mweather_widget_magic_location;
	private TextView mweather_widget_magic_description;
	private TextView mweather_widget_magic_temperature;
	private ImageView mweather_widget_magic_time_0;
	private ImageView mweather_widget_magic_time_1;
	private ImageView mweather_widget_magic_time_2;
	private ImageView mweather_widget_magic_time_3;
	private ImageView mweather_widget_magic_colon;
	private TextView mweather_widget_magic_time_date;
	private TextView mweather_widget_magic_time_week;
	private TextView mweather_widget_magic_am_pm;
	private ImageView mweather_widget_magic_icon;
	private LinearLayout mweather_widget_magic_layout;
	private LinearLayout mweather_widget_magic_time_and_date;
	
	private ImageView mweather_widget_magic_baidu_bg;
	private ImageView mweather_widget_magic_baidu_pic;
	private TextView mweather_widget_magic_baidu_location;
	private TextView mweather_widget_magic_baidu_description;
	private TextView mweather_widget_magic_baidu_temperature;
	private TextView mweather_widget_magic_baidu_time_date;
	private TextView mweather_widget_magic_baidu_am_pm;
	private LinearLayout mweather_widget_magic_baidu_hour;
	private ImageView mweather_widget_magic_baidu_time_0;
	private ImageView mweather_widget_magic_baidu_time_1;
	private ImageView mweather_widget_magic_baidu_time_2;
	private ImageView mweather_widget_magic_baidu_time_3;
	private LinearLayout mweather_widget_magic_baidu_min;
	private ImageView mweather_widget_magic_baidu_search_bg;
	private ImageView mweather_widget_magic_baidu_search_icon;
	private ImageView mweather_widget_magic_baidu_search_yuyin;
	private LinearLayout mweather_widget_magic_baidu_time_and_date;
	private RelativeLayout mweather_widget_magic_baidu_left;
	private TextView mweather_widget_magic_cmmc;
	private Context mcontext;
	private View mweatherwidgetmagicview;
	/*RK_ID:RK_LEOSWIDGET AUT:zzcao DATE: 2013-06-07 START */
	private RelativeLayout mweatherwidgetdetailsview;
	View mweather_widget_details_content_layout;
	private AnimationImageView mweather_widget_details_pic ;
	private LinearLayout mweather_widget_details_layout ;
	TextView mweather_widget_details_content_city;
	TextView mweather_widget_details_content_date;
	TextView mweather_widget_details_content_temp;
	TextView mweather_widget_details_content_wind;
	TextView mweather_widget_details_content_index;
	TextView mweather_widget_details_content_index_uv;
	TextView mweather_widget_details_content_index_xc;
	TextView mweather_widget_details_content_index_tr;
	LinearLayout mweather_widget_details_content_weather_num[] = new LinearLayout[3];
	TextView mweather_widget_details_content_layout_week[] = new TextView[3];
	TextView mweather_widget_details_content_layout_temp[] = new TextView[3];
	TextView mweather_widget_details_content_layout_content[]= new TextView[3];
	private int weather_widget_details_content_weather_nums[] = {R.id.weather_widget_details_content_weather_num0,
   			R.id.weather_widget_details_content_weather_num1,R.id.weather_widget_details_content_weather_num2};
	private String weather_widget_details_content_weather_nums_s[] = {"weather_widget_details_content_weather_num0",
   			"weather_widget_details_content_weather_num1","weather_widget_details_content_weather_num2"};
	public Bitmap mbitmap = null;
    private int statusBarHeight= 0;
	private int mdisplayheight = 0;
   	private int mdisplaywidth = 0;
   	private int mrealheight =0;
   	/*RK_ID:RK_LEOSWIDGET AUT:zzcao DATE: 2013-06-07 END */
   /*RK_ID:RK_LEOSWIDGET AUT:kangwei DATE: 2012-10-17 START */
	private static final String THIS_PUSHMAILWIDGET =
	                 "com.lenovo.launcher2.weather.widget.WeatherWidgetMagicView";
   /*RK_ID:RK_LEOSWIDGET AUT:kangwei DATE: 2012-10-17 E */
	private BroadcastReceiver mIntentReceiver = null ;
	private Resources mres;
	private static final String EXTRA_NETWORK_ENABLED = "network_enabled";
	private FormatChangeObserver mFormatChangeObserver;
	private static final  Uri muri = Uri.withAppendedPath(Settings.System.CONTENT_URI,
            Settings.System.DATE_FORMAT);
	private Handler mHandler = null;
	private static Bitmap  mPicbitmap = null;
	private static boolean misunlock = true;
    public boolean mNetworkEnabled = false;
    private static final String PREF_NETWORK_ENABLER = "pref_network_enabler";
    private static boolean dis12or24=false;
	private static String mTimeFormat;
    private final static String M12 = "hh:mm";
    private final static String M24 = "kk:mm";
    final private static String DEFALUT_DATE_FORMAT_CN = "yyyy-MM-dd";
    final private static String DEFALUT_DATE_FORMAT_EN = "MM/dd/yyyy";
    final private static String DEFALUT_DATE_FORMAT_WVGA_EN = "MM/dd";
    private static final String ACTION_CHANGE_LETHEME = "action.letheme.apply";
    private boolean mistimeset = false;
    private boolean misQHD = false;
    LauncherApplication mapp ;
    private boolean miscmmc = false;
	public WeatherWidgetMagicView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	public WeatherWidgetMagicView(Context context) {
		super(context);
		mapp = (LauncherApplication) getContext().getApplicationContext();
//		this.setGravity(Gravity.CENTER);
//		final DisplayMetrics display = context.getResources().getDisplayMetrics();
		final SharedPreferences preferences = context.getSharedPreferences(
				WeatherUtilites.PRE_LAUNCHER, Activity.MODE_PRIVATE);
		boolean phonebu = false;
		if(preferences!=null){
			phonebu = preferences.getBoolean(WeatherUtilites.PRE_LAUNCHER_KEY_PHONE_BU,
					WeatherUtilites.PRE_LAUNCHER_KEY_PHONE_DEFAULT_BU);
			R2.echo("phonebu = "+phonebu);
			miscmmc = preferences.getBoolean(WeatherUtilites.PRE_LAUNCHER_KEY_PHONE_CMMC,
					false);
		}
		mres = context.getResources();
		mcontext = context;
		this.clearChildFocus(mweatherwidgetmagicview);
		initdata(context);
		// TODO Auto-generated constructor stub
	}
	private  void initdata(final Context context)
	{
//        mcontext = WeatherUtilites.GetCurrentThemesContext(context);
		mapp = (LauncherApplication) getContext().getApplicationContext();

		statusBarHeight =WeatherUtilites.getStatusHeights(context);
//		if(display.heightPixels>900||!phonebu){
			misQHD = true;
			initlayoutQHD(context);
//		}
/*		else{
			initlayoutWVGA(context);
			misQHD = false;
		}*/
        initHandler(context);
    	Intent intent = new Intent(WeatherUtilites.ACTION_ADD_WEATHER_WIDGET);
//    	context.startService(intent.setClass(context,WidgetService.class));
    	context.sendBroadcast(intent);
    	mFormatChangeObserver = new FormatChangeObserver();
    	mcontext.getContentResolver().registerContentObserver(muri, true,
                mFormatChangeObserver);
        misunlock = true;
	}
	private void initlayoutWVGA(final Context context)
	{
       	mweatherwidgetmagicview = View.inflate(context, R.layout.weather_widget_magic_baidu_layout, null);
      	mweather_widget_magic_baidu_search_icon = (ImageView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_magic_baidu_search_icon);
      	mweather_widget_magic_baidu_search_yuyin = (ImageView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_magic_baidu_search_yuyin);
      	mweather_widget_magic_baidu_bg = (ImageView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_magic_baidu_bg);
       	mweather_widget_magic_baidu_pic =(ImageView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_magic_baidu_pic);
       	mweather_widget_magic_baidu_location = (TextView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_magic_baidu_location);
       	mweather_widget_magic_baidu_description = (TextView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_magic_baidu_description);
       	mweather_widget_magic_baidu_temperature = (TextView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_magic_baidu_temperature);
       	mweather_widget_magic_baidu_time_0 = (ImageView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_magic_baidu_time_0);
       	mweather_widget_magic_baidu_time_1 = (ImageView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_magic_baidu_time_1);
       	mweather_widget_magic_baidu_time_2 = (ImageView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_magic_baidu_time_2);
       	mweather_widget_magic_baidu_time_3 = (ImageView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_magic_baidu_time_3);
       	mweather_widget_magic_baidu_time_date = (TextView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_magic_baidu_time_date);
       	mweather_widget_magic_baidu_am_pm = (TextView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_magic_baidu_am_pm);
       	mweather_widget_magic_baidu_search_bg = (ImageView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_magic_baidu_search_bg);
       	mweather_widget_magic_baidu_min =(LinearLayout)mweatherwidgetmagicview.findViewById(R.id.weather_widget_magic_baidu_min);
       	mweather_widget_magic_baidu_hour =(LinearLayout)mweatherwidgetmagicview.findViewById(R.id.weather_widget_magic_baidu_hour);
       	mweather_widget_magic_baidu_time_and_date = (LinearLayout)mweatherwidgetmagicview.findViewById(R.id.weather_widget_magic_baidu_time_and_date);
       	mweather_widget_magic_baidu_left =(RelativeLayout)mweatherwidgetmagicview.findViewById(R.id.weather_widget_magic_baidu_left);
       	mweather_widget_magic_cmmc = (TextView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_magic_cmmc);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//		lp.leftMargin = 12;
//		lp.rightMargin = 12;
		this.addView(mweatherwidgetmagicview,lp);

	}
	private void initlayoutQHD(final Context context)
	{
		final DisplayMetrics display = context.getResources().getDisplayMetrics();
    	mdisplaywidth = display.widthPixels;
    	mdisplayheight = display.heightPixels;
    	mrealheight =mdisplayheight-statusBarHeight;
       	mweatherwidgetmagicview = View.inflate(context, R.layout.weather_widget_layout, null);
       	mweather_widget_magic_bg = (ImageView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_magic_bg);
       	mweather_widget_magic_location = (TextView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_magic_location);
       	mweather_widget_magic_description = (TextView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_magic_description);
       	mweather_widget_magic_temperature = (TextView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_magic_temperature);
       	mweather_widget_magic_time_0 = (ImageView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_magic_time_0);
       	mweather_widget_magic_time_1 = (ImageView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_magic_time_1);
       	mweather_widget_magic_time_2 = (ImageView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_magic_time_2);
       	mweather_widget_magic_time_3 = (ImageView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_magic_time_3);
       	mweather_widget_magic_colon = (ImageView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_magic_colon);
       	mweather_widget_magic_time_date = (TextView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_magic_time_date);
       	mweather_widget_magic_time_week = (TextView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_magic_time_week);
       	mweather_widget_magic_am_pm = (TextView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_magic_am_pm);
       	mweather_widget_magic_icon =(ImageView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_magic_icon);
       	mweather_widget_magic_layout =(LinearLayout)mweatherwidgetmagicview.findViewById(R.id.weather_widget_magic_layout);
       	mweather_widget_magic_time_and_date =(LinearLayout)mweatherwidgetmagicview.findViewById(R.id.weather_widget_magic_time_and_date);
       	mweather_widget_magic_cmmc = (TextView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_magic_cmmc);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//		lp.leftMargin = 8;
//		lp.rightMargin = 8;
		if(WeatherUtilites.isInDefaultTheme(context)){
      		initDefaultLayout(context);
      	}
		
		this.addView(mweatherwidgetmagicview,lp);

	}
	private void initDefaultLayout(final Context context){
		mweather_widget_details_content_layout = mapp.mLauncherContext.getLayoutViewByName(true, "weather_widget_details_content_layout",null);
       	if(mweather_widget_details_content_layout==null){
       		mweather_widget_details_content_layout = View.inflate(context, R.layout.weather_widget_details_content_layout, null);
       	}
		mweatherwidgetdetailsview = (RelativeLayout) mapp.mLauncherContext.getLayoutViewByName(true, "weather_widget_details_layout",null);
		if(mweatherwidgetdetailsview!=null){
			try{
				mweather_widget_details_layout = (LinearLayout)mapp.mLauncherContext.findViewByIdName(mweatherwidgetdetailsview,"weather_widget_details_layout");
		       	mweather_widget_details_content_city =(TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetdetailsview,"weather_widget_details_content_city");
				mweather_widget_details_content_date =(TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetdetailsview,"weather_widget_details_content_date");
				mweather_widget_details_content_temp =(TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetdetailsview,"weather_widget_details_content_temp");
				mweather_widget_details_content_wind =(TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetdetailsview,"weather_widget_details_content_wind");
				mweather_widget_details_content_index =(TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetdetailsview,"weather_widget_details_content_index");
				mweather_widget_details_content_index_uv =(TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetdetailsview,"weather_widget_details_content_index_uv");
				mweather_widget_details_content_index_xc =(TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetdetailsview,"weather_widget_details_content_index_xc");
				mweather_widget_details_content_index_tr =(TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetdetailsview,"weather_widget_details_content_index_tr");
				for(int i =0;i<3;i++){
					mweather_widget_details_content_weather_num[i] =(LinearLayout)mapp.mLauncherContext.findViewByIdName(mweatherwidgetdetailsview,weather_widget_details_content_weather_nums_s[i]);
					mweather_widget_details_content_layout_temp[i] = (TextView)mapp.mLauncherContext.findViewByIdName(mweather_widget_details_content_weather_num[i], "weather_widget_details_content_layout_temp");
					mweather_widget_details_content_layout_week[i] = (TextView)mapp.mLauncherContext.findViewByIdName(mweather_widget_details_content_weather_num[i], "weather_widget_details_content_layout_week");
					mweather_widget_details_content_layout_content[i] = (TextView)mapp.mLauncherContext.findViewByIdName(mweather_widget_details_content_weather_num[i], "weather_widget_details_content_layout_content");
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}else{
       		mweatherwidgetdetailsview =(RelativeLayout) View.inflate(context, R.layout.weather_widget_details_layout, null);
       		mweather_widget_details_layout = (LinearLayout)mweatherwidgetdetailsview.findViewById(R.id.weather_widget_details_layout);
           	mweather_widget_details_content_city =(TextView)mweatherwidgetdetailsview.findViewById(R.id.weather_widget_details_content_city);
    		mweather_widget_details_content_date =(TextView)mweatherwidgetdetailsview.findViewById(R.id.weather_widget_details_content_date);
    		mweather_widget_details_content_temp =(TextView)mweatherwidgetdetailsview.findViewById(R.id.weather_widget_details_content_temp);
    		mweather_widget_details_content_wind =(TextView)mweatherwidgetdetailsview.findViewById(R.id.weather_widget_details_content_wind);
    		mweather_widget_details_content_index =(TextView)mweatherwidgetdetailsview.findViewById(R.id.weather_widget_details_content_index);;
    		mweather_widget_details_content_index_uv =(TextView)mweatherwidgetdetailsview.findViewById(R.id.weather_widget_details_content_index_uv);
    		mweather_widget_details_content_index_xc =(TextView)mweatherwidgetdetailsview.findViewById(R.id.weather_widget_details_content_index_xc);
    		mweather_widget_details_content_index_tr =(TextView)mweatherwidgetdetailsview.findViewById(R.id.weather_widget_details_content_index_tr);
    		for(int i =0;i<3;i++){
    			mweather_widget_details_content_weather_num[i] =(LinearLayout)mweatherwidgetdetailsview.findViewById(weather_widget_details_content_weather_nums[i]);
    			mweather_widget_details_content_layout_temp[i] = (TextView)mweather_widget_details_content_weather_num[i]
    			.findViewById(R.id.weather_widget_details_content_layout_temp);
    			mweather_widget_details_content_layout_week[i] = (TextView)mweather_widget_details_content_weather_num[i]
    			.findViewById(R.id.weather_widget_details_content_layout_week);
    			mweather_widget_details_content_layout_content[i] = (TextView)mweather_widget_details_content_weather_num[i]
    			.findViewById(R.id.weather_widget_details_content_layout_content);
    		}
		}
       	mweather_widget_details_pic = new AnimationImageView(context);
       	
		RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 
        		LinearLayout.LayoutParams.MATCH_PARENT);
        mweatherwidgetdetailsview.addView(mweather_widget_details_pic,rp);
	}
	private void initHandler(final Context context) {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch(msg.what) {
				case WeatherUtilites.MES_UPDATE_WEATHER:
					dispalyCityName(mcontext);
					updateWeather(mcontext);
					updatelayout();
					break;
				case WeatherUtilites.MES_CHANGE_CITY_WEATHER:
					try{
						dispalyCityName(mcontext);
						updateweatherdata(mcontext);
					}catch(Exception ex){
						ex.printStackTrace();
						clearWeatherInfo();
					}
					updatelayout();
					break;
				case WeatherUtilites.MES_WEATHER_NETWORK_STATE_CHANGE:
					updatelayout();
					setWeathInfolistener(mcontext);
					break;
				case WeatherUtilites.MES_WEATHER_INSTALL_CHANGE:
					break;
				case WeatherUtilites.MES_ACTION_SCREEN_ON:
					updateDataFromLeDesktopSettings(mcontext);
					dispalyCityName(mcontext);
					displayTime(mcontext);
					updateWeather(mcontext);
					break;
				case WeatherUtilites.MES_LOCATION_CHANGE:
					dispalyCityName(mcontext);
	                clearWeatherInfo();
					updatelayout();
					break;

				case WeatherUtilites.MES_WEATHER_WIDGET_INIT:
					updateDataFromLeDesktopSettings(mcontext);
					setDefaultWeatherBG();
					dispalyCityName(mcontext);
					displayTime(mcontext);
		        	updateWeather(mcontext);
		        	setTimeLayoutlistener(mcontext);
		        	if(!misQHD)
		        		setBaidulistener(mcontext);
		        	setWeathInfolistener(mcontext);
					break;
				case WeatherUtilites.MES_TIME_TICK:
					displayTime(mcontext);
					break;
				case WeatherUtilites.MES_TIEM_CHANGE:
					updateWeather(mcontext);
					displayTime(mcontext);
					break;
				case WeatherUtilites.MES_WEATHER_WIDGET_CARRIER_UPDATE:
					if(miscmmc){
	            		String carrie = DisplayTelephoneName(context);
	            		if(carrie!=null){
	            			mweather_widget_magic_cmmc.setText(carrie);
	            		}
	            	}
					break;
				case WeatherUtilites.MES_UPDATE_WEATHER_DETAILS:
					List<WeatherApp> weatherapps = (List<WeatherApp>)msg.obj;
					updateWeatherByApp(mcontext,weatherapps);
//					buildViewCache();
					break;
				case WeatherUtilites.MES_UPDATE_WEATHER_CITYNAME:
					String cityname = (String)msg.obj;
					if(cityname!=null&&!TextUtils.isEmpty(cityname)){
						if(misQHD){
							mweather_widget_magic_location.setTextColor(
									mapp.mLauncherContext.getColor(R.color.weather_magic_widget_cityname_color_41,
											R.color.weather_magic_widget_cityname_color_41));
					    	mweather_widget_magic_location.setText(cityname);
				    	}else{
				    		mweather_widget_magic_baidu_location.setTextColor(
									mapp.mLauncherContext.getColor(R.color.weather_magic_widget_cityname_color_41,
											R.color.weather_magic_widget_cityname_color_41));
				    		mweather_widget_magic_baidu_location.setText(cityname);
				    	}
					}else{
						if(misQHD){
					    	mweather_widget_magic_location.setText("");
				    	}else{
				    		mweather_widget_magic_baidu_location.setText("");
				    	}
					}
//					buildViewCache();
					break;
				case WeatherUtilites.MES_UPDATE_SERVICE_WEATHER_DETAILS:
					List<WeatherDetails> weatherdetails  =(List<WeatherDetails>)msg.obj;
					updateWeatherBySoure(context,weatherdetails);
//					buildViewCache();
					break;
				case WeatherUtilites.MES_UPDATE_LOCAL_WEATHER_CITYNAME:
					cityname = (String)msg.obj;
					updatelocalcityname(cityname);
//					buildViewCache();
					break;
				case WeatherUtilites.MES_WEATHER_ANIMATE_STOP:
//					displayTime(mcontext);
					if(WeatherUtilites.isInDefaultTheme(context) && !mweather_widget_details_pic.getwindowstate()){
						mweather_widget_details_pic.hideview();
					}
					break;
				case WeatherUtilites.MES_UPDATE_TIME:
					updateTime(mcontext);
					if(misQHD)
		        		QHDupdateDateAndWeek(mcontext);
		        	else
		        		WVGAupdateDateWeek(mcontext);
					if(mweather_widget_magic_icon.getTag()!=null){
		        		if(mweather_widget_magic_icon.getTag().toString().contains("d") && IsDaytime() ||
		        				mweather_widget_magic_icon.getTag().toString().contains("n") && !IsDaytime()){
		        			;
		        		}else{
		        			updateWeather(mcontext);
		        		}
		        	}
//					buildViewCache();
					break;
				default:
					break;
				}
			}
		};
	}
    private void updatelayout()
    {
    	View parentview = (View) mweatherwidgetmagicview.getParent();
    	mweatherwidgetmagicview.requestLayout();
    	if(parentview!=null)
    		parentview.requestLayout();
    }
    private void updatelocalcityname(String cityname)
    {
    	if(cityname!=null&&!TextUtils.isEmpty(cityname)){
			if(misQHD)
				mweather_widget_magic_location.setBackgroundColor(0x00000000);
			else
				mweather_widget_magic_baidu_location.setBackgroundColor(0x00000000);
		}
		if(misQHD){
			mweather_widget_magic_location.setTextColor(
					mapp.mLauncherContext.getColor(R.color.weather_magic_widget_cityname_color_41,
							R.color.weather_magic_widget_cityname_color_41));
	    	mweather_widget_magic_location.setText(cityname);
    	}else{
    		mweather_widget_magic_baidu_location.setTextColor(
					mapp.mLauncherContext.getColor(R.color.weather_magic_widget_cityname_color_41,
							R.color.weather_magic_widget_cityname_color_41));
    		mweather_widget_magic_baidu_location.setText(cityname);
    	}
		cityname = WeatherUtilites.getCityName(mcontext, 1);
    	Log.i("czz", "magic settings save cityname= "+cityname);
		Settings.System.putString(mcontext.getContentResolver(), "city_name", cityname);
    }
	public void dispalyCityName(final Context context)
    {
		final int lan = WeatherUtilites.getLan();
		if(!WeatherUtilites.findForPackage(context,WeatherUtilites.SINA_PACKAGENAME)){
			new Thread() {
				@Override
				public void run() {
					String cityname = WeatherUtilites.getCityName(context, lan);
					Message message = mHandler.obtainMessage(WeatherUtilites.MES_UPDATE_LOCAL_WEATHER_CITYNAME,cityname);  
					mHandler.sendMessage(message);
				}
			}.start();
		}
		else{
			new Thread() {
				@Override
				public void run() {
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
						Message message = mHandler.obtainMessage(WeatherUtilites.MES_UPDATE_WEATHER_CITYNAME,tcityname);  
						mHandler.sendMessage(message);
					}
				}
			}.start();
		}
    	
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
    public void setDefaultWeatherBG(){
    	if(mapp==null)
    		mapp = (LauncherApplication) getContext().getApplicationContext();
    	if(misQHD){
    		mweather_widget_magic_bg.setImageDrawable(mapp.mLauncherContext.getDrawable(R.drawable.weather_widget_magic_bg));
    	}else{
    		mweather_widget_magic_baidu_bg.setImageDrawable(mapp.mLauncherContext.getDrawable(R.drawable.weather_widget_magic_baidu_bg));
    		mweather_widget_magic_baidu_min.setBackgroundDrawable(mapp.mLauncherContext.getDrawable(R.drawable.weather_widget_magic_baidu_time_bg));
    		mweather_widget_magic_baidu_hour.setBackgroundDrawable(mapp.mLauncherContext.getDrawable(R.drawable.weather_widget_magic_baidu_time_bg));
    	}
    	if(miscmmc){
    		String carrie = DisplayTelephoneName(mcontext);
    		if(carrie!=null){
    			mweather_widget_magic_cmmc.setText(carrie);
    			mweather_widget_magic_cmmc.setTextColor(
    					mapp.mLauncherContext.getColor(R.color.weather_widget_week_color,
    							R.color.weather_widget_week_color));
    		}
    	}
    }
    public boolean updateDataFromLeDesktopSettings(Context context) {
        int isNetworkEnabled = Settings.System.getInt(
        		context.getContentResolver(), PREF_NETWORK_ENABLER, 0);
        if (isNetworkEnabled == 1) {
            mNetworkEnabled = true;
        } else {
            mNetworkEnabled = false;
        }
        return true;
    } 
    boolean IsDaytime() {
        GregorianCalendar gcal = new GregorianCalendar();
        int hour = gcal.get(Calendar.HOUR_OF_DAY);
        return (hour >= 6 && hour < 18);
    }
    private void updateTime(Context context){
    	dis12or24=false;
		dis12or24=setTimeFormat(context);
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		final String AmPm = GetAMPM(context,calendar);
		final String curenttime = DateFormat.format(mTimeFormat, calendar).toString();
		R2.echo("displayTime curenttime = "+curenttime);
		if(misQHD){
			mweather_widget_magic_am_pm.setTextColor(
					mapp.mLauncherContext.getColor(R.color.weather_widget_AMPM_color,
							R.color.weather_widget_AMPM_color));
			mweather_widget_magic_am_pm.setText(AmPm);
			
			try{
				int hour = 0;
				if(dis12or24)
					hour = calendar.get(Calendar.HOUR_OF_DAY);
				else{
					hour = calendar.get(Calendar.HOUR);
					if(hour==0)
						hour = 12;
				}
				int currentnum = hour/10;
				mweather_widget_magic_time_0.setImageDrawable(
						mapp.mLauncherContext.getDrawable(WeatherUtilites.MAGIC_NUMBER[currentnum]));
				currentnum = hour%10;
				mweather_widget_magic_time_1.setImageDrawable(
						mapp.mLauncherContext.getDrawable(WeatherUtilites.MAGIC_NUMBER[currentnum]));
				int minute = calendar.get(Calendar.MINUTE);
				currentnum = minute/10;
				mweather_widget_magic_time_2.setImageDrawable(
						mapp.mLauncherContext.getDrawable(WeatherUtilites.MAGIC_NUMBER[currentnum]));
				currentnum = minute%10;
				mweather_widget_magic_time_3.setImageDrawable(
						mapp.mLauncherContext.getDrawable(WeatherUtilites.MAGIC_NUMBER[currentnum]));
				mweather_widget_magic_colon.setImageDrawable(mapp.mLauncherContext.getDrawable(
						R.drawable.weather_widget_magic_colon));
			}catch(ArrayIndexOutOfBoundsException ex){
				ex.printStackTrace();
			}

			/*int currentnum = curenttime.charAt(0) - '0';
			mweather_widget_magic_time_0.setImageDrawable(
					mapp.mLauncherContext.getDrawable(WeatherUtilites.MAGIC_NUMBER[currentnum]));
			currentnum = curenttime.charAt(1) - '0';  
			mweather_widget_magic_time_1.setImageDrawable(
					mapp.mLauncherContext.getDrawable(WeatherUtilites.MAGIC_NUMBER[currentnum]));
			currentnum = curenttime.charAt(3) - '0'; 
			mweather_widget_magic_time_2.setImageDrawable(
					mapp.mLauncherContext.getDrawable(WeatherUtilites.MAGIC_NUMBER[currentnum]));
			currentnum = curenttime.charAt(4) - '0'; 
			mweather_widget_magic_time_3.setImageDrawable(
					mapp.mLauncherContext.getDrawable(WeatherUtilites.MAGIC_NUMBER[currentnum]));
			mweather_widget_magic_colon.setImageDrawable(mapp.mLauncherContext.getDrawable(
					R.drawable.weather_widget_magic_colon));*/

		}else{
			mweather_widget_magic_baidu_am_pm.setTextColor(
					mapp.mLauncherContext.getColor(R.color.weather_widget_AMPM_color,
							R.color.weather_widget_AMPM_color));
			mweather_widget_magic_baidu_am_pm.setText(AmPm);
			
			try{
				int hour = 0;
				if(dis12or24)
					hour = calendar.get(Calendar.HOUR_OF_DAY);
				else{
					hour = calendar.get(Calendar.HOUR);
					if(hour==0)
						hour = 12;
				}
				int currentnum = hour/10;
				mweather_widget_magic_baidu_time_0.setImageDrawable(
						mapp.mLauncherContext.getDrawable(WeatherUtilites.MAGIC_BAIDU_NUMBER[currentnum]));
				currentnum = hour%10;
				mweather_widget_magic_baidu_time_1.setImageDrawable(
						mapp.mLauncherContext.getDrawable(WeatherUtilites.MAGIC_BAIDU_NUMBER[currentnum]));
				int minute = calendar.get(Calendar.MINUTE);
				currentnum = minute/10;
				mweather_widget_magic_baidu_time_2.setImageDrawable(
						mapp.mLauncherContext.getDrawable(WeatherUtilites.MAGIC_BAIDU_NUMBER[currentnum]));
				currentnum = minute%10;
				mweather_widget_magic_baidu_time_3.setImageDrawable(
						mapp.mLauncherContext.getDrawable(WeatherUtilites.MAGIC_BAIDU_NUMBER[currentnum]));
			}catch(ArrayIndexOutOfBoundsException ex){
				ex.printStackTrace();
			}

			/*int currentnum = curenttime.charAt(0) - '0';
			mweather_widget_magic_baidu_time_0.setImageDrawable(
					mapp.mLauncherContext.getDrawable(WeatherUtilites.MAGIC_BAIDU_NUMBER[currentnum]));
			currentnum = curenttime.charAt(1) - '0';  
			mweather_widget_magic_baidu_time_1.setImageDrawable(
					mapp.mLauncherContext.getDrawable(WeatherUtilites.MAGIC_BAIDU_NUMBER[currentnum]));
			currentnum = curenttime.charAt(3) - '0'; 
			mweather_widget_magic_baidu_time_2.setImageDrawable(
					mapp.mLauncherContext.getDrawable(WeatherUtilites.MAGIC_BAIDU_NUMBER[currentnum]));
			currentnum = curenttime.charAt(4) - '0'; 
			mweather_widget_magic_baidu_time_3.setImageDrawable(
					mapp.mLauncherContext.getDrawable(WeatherUtilites.MAGIC_BAIDU_NUMBER[currentnum]));*/

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
    	if(get24HourMode(context)){
    		mTimeFormat=M24;
    		return true;
    	}
    	else{
    		mTimeFormat=M12;
    		return false;
    	}
    }
	private String GetAMPM(final Context context,Calendar c)
	{
		if(!dis12or24){
	        if(c.get(Calendar.AM_PM)==0){
	            return  mres.getString(R.string.weather_clock_forenoon);
	        }
	        else{
	            return  mres.getString(R.string.weather_clock_afternoon);
	        }
		}
		else 
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
    private void QHDupdateDateAndWeek(final Context context)
    {
    	Configuration configuration = mres.getConfiguration();
    	String language = configuration.locale.getLanguage();
    	Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
    	String dayString = DateUtils.getDayOfWeekString(
    			calendar.get(Calendar.DAY_OF_WEEK), DateUtils.LENGTH_MEDIUM);
    	final String dateString = getDateStr(calendar,language,context);
    	final int flagsWeek = DateUtils.FORMAT_SHOW_WEEKDAY; 
        String weekStr = (String)DateUtils.formatDateTime(context, 
    			System.currentTimeMillis(), flagsWeek);

		mweather_widget_magic_time_date.setTextColor(
				mapp.mLauncherContext.getColor(R.color.weather_widget_date_color,
						R.color.weather_widget_date_color));
		mweather_widget_magic_time_week.setTextColor(
				mapp.mLauncherContext.getColor(R.color.weather_widget_week_color,
						R.color.weather_widget_week_color));
    	if(dateString.contains(dayString))
    		mweather_widget_magic_time_week.setVisibility(View.GONE);
    	else
    		mweather_widget_magic_time_week.setVisibility(View.VISIBLE);
    	mweather_widget_magic_time_week.setText(" "+dayString);
    	mweather_widget_magic_time_date.setText(dateString);
    	/*if(dateString!=null){
	        if (language.equals("zh")) {
	        	mweather_widget_magic_time_week.setText(dayString);
	        	mweather_widget_magic_time_date.setText(dateString);
	        }  else {
	        	mweather_widget_magic_time_week.setText(dayString.toUpperCase());
	        	mweather_widget_magic_time_date.setText(dateString.toUpperCase());
	        }
        }*/
    	if(WeatherUtilites.isInDefaultTheme(context)){
    		try{
    			ChineseCalendar chinesecalendar = new ChineseCalendar(mcontext);		
        		String sMonth = chinesecalendar.getChinese(ChineseCalendar.CHINESE_MONTH);
        		String sData = chinesecalendar.getChinese(ChineseCalendar.CHINESE_DATE);
        		String sYear = chinesecalendar.getChinese(ChineseCalendar.CHINESE_YEAR);
        		StringBuffer sb = new StringBuffer();
        		sb.append(dateString).append(" ").append(sYear).append(sMonth).append(sData).append(" ").append(weekStr);
        		mweather_widget_details_content_date.setText(sb.toString());
        		for(int i=0;i<3;i++){
        			mweather_widget_details_content_layout_week[i].setText(WeatherUtilites.getWeek(context,i+1));
        		}
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    	}
    }
    private String getDateStr(Date now, String language,Context context) {
//        String dateFormat = Settings.System.getString(context.getContentResolver(), "date_format");
        if (language.equals("zh")) {
        	final int flagsDate = DateUtils.FORMAT_SHOW_DATE;
            return (String)DateUtils.formatDateTime(context, System.currentTimeMillis(), flagsDate);
        } else {
            return DateFormat.format(DEFALUT_DATE_FORMAT_WVGA_EN, now).toString();
        }
    }
    private void WVGAupdateDateWeek(Context context){
    	StringBuffer sb = new StringBuffer();
    	Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
    	Configuration configuration = mres.getConfiguration();
    	calendar.setTimeInMillis(System.currentTimeMillis());
        Date now = calendar.getTime();
        String language = configuration.locale.getLanguage();
        String weekstring = DateUtils.getDayOfWeekString(
        		calendar.get(Calendar.DAY_OF_WEEK), DateUtils.LENGTH_MEDIUM);
        
        String dateString = getDateStr(now, language,context);
        
        
    	if(dateString.contains(weekstring))
    		sb.append(dateString);
    	else
    		sb.append(dateString).append(" ").append(weekstring);

        
/*        if (!language.equals("zh")) {
        	weekstring = weekstring.toUpperCase();
        }
        sb.append(dateString).append(" ").append(weekstring);*/
        mweather_widget_magic_baidu_time_date.setTextColor(
				mapp.mLauncherContext.getColor(R.color.weather_widget_date_color,
						R.color.weather_widget_date_color));
		mweather_widget_magic_baidu_time_date.setText(sb.toString());
    }
    public void registerWeatherIntentReceiver()
    {   
    	mIntentReceiver = new IntentReceiver();
        IntentFilter eventFilter = new IntentFilter();
		eventFilter.addAction(WeatherUtilites.ACTION_WEATHER_WIDGET_TIME_TICK);
		eventFilter.addAction(WeatherUtilites.ACTION_LOCATION_CHANGE);
		eventFilter.addAction(WeatherUtilites.ACTION_WEATHER_NETWORK_STATE_CHANGE);
		eventFilter.addAction(WeatherUtilites.ACTION_TIEM_CHANGE);
		eventFilter.addAction(WeatherUtilites.ACTION_UPDATE_DEFAILD_WEATHER);
		eventFilter.addAction(WeatherUtilites.ACTION_UPDATE_WEATHER);
//		eventFilter.addAction(Intent.ACTION_SCREEN_ON);
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
			if(intent==null) 
				return ;
			mreceivercontext = context;
			if(!WeatherUtilites.hasInstances(context,THIS_PUSHMAILWIDGET)){
				 R2.echo("!magic hasInstances");
			    	try {
			    		if(mIntentReceiver!=null) 
			    			context.unregisterReceiver(mIntentReceiver);
			    	} catch (IllegalArgumentException e) { 
			    		e.printStackTrace();
			    	}
				return;
			}
            String action = intent.getAction();
            R2.echo("!!!!magic *********recievied " + action);
            if (action.equals(WeatherUtilites.ACTION_WEATHER_WIDGET_TIME_TICK))
                mHandler.sendEmptyMessage(WeatherUtilites.MES_TIME_TICK);
            else if (action.equals(WeatherUtilites.ACTION_LOCATION_CHANGE)) {
                R2.echo("Location Changed");
                mHandler.sendEmptyMessage(WeatherUtilites.MES_LOCATION_CHANGE);
            }else if (action.equals(WeatherUtilites.ACTION_WEATHER_NETWORK_STATE_CHANGE)) {
            	mNetworkEnabled = intent.getBooleanExtra(
                        EXTRA_NETWORK_ENABLED, false);
                R2.echo("magic Network is open ="+mNetworkEnabled);
                mHandler.sendEmptyMessage(WeatherUtilites.MES_WEATHER_NETWORK_STATE_CHANGE);
            }else if(action.equals(Intent.ACTION_SCREEN_ON)){
            	mHandler.sendEmptyMessage(WeatherUtilites.MES_ACTION_SCREEN_ON);
            }else if(action.equals(WeatherUtilites.ACTION_UPDATE_WEATHER)){
            	mHandler.sendEmptyMessage(WeatherUtilites.MES_UPDATE_WEATHER);
            }else if(WeatherUtilites.ACTION_TIEM_CHANGE.equals(action)){
            	mHandler.sendEmptyMessage(WeatherUtilites.MES_TIEM_CHANGE);
            }else if(ACTION_CHANGE_LETHEME.equals(action)){
            	initdata(context);
		mHandler.sendEmptyMessage(WeatherUtilites.MES_WEATHER_WIDGET_INIT);
            }else if(action.equals(WeatherUtilites.ACTION_WEATHER_WIDGET_CARRIER_UPDATE)){
            	mHandler.sendEmptyMessage(WeatherUtilites.MES_WEATHER_WIDGET_CARRIER_UPDATE);
            }else if(WeatherUtilites.ACTION_WEATHER_WIDGET_CHANGECITY_FROMSINA.equals(action)||
            		WeatherUtilites.ACTION_WEATHER_WIDGET_ADDCITY_FROMSINA.equals(action)||
            		WeatherUtilites.ACTION_WEATHER_WIDGET_DELETECITY_FROMSINA.equals(action)||
            		WeatherUtilites.ACTION_WEATHER_WIDGET_UPDATECITY_FROMSINA.equals(action)){
            	mHandler.sendEmptyMessage(WeatherUtilites.MES_CHANGE_CITY_WEATHER);
            }else if(WeatherUtilites.ACTION_WEATHER_WIDGET_PACKAGE_UPDATE.equals(action)){
            	final String packageName = intent.getStringExtra("packageName");
            	if(packageName!=null&&packageName.contains(WeatherUtilites.SINA_PACKAGENAME)){
            		mHandler.sendEmptyMessage(WeatherUtilites.MES_WEATHER_WIDGET_INIT);
            	}
            }else if(action.equals(WeatherUtilites.ACTION_WEATHER_ANIMATE_STOP)){
            	mHandler.sendEmptyMessage(WeatherUtilites.MES_WEATHER_ANIMATE_STOP);
            }
		}
    }

	public boolean isWeatherOutOfDate24(final Context context) {
	    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
	    final int currenttime = (int)(calendar.getTimeInMillis()/3600000);
	    int oldcurrenttime = 0;
		try {
			oldcurrenttime = (int)(Settings.System.getLong(context.getContentResolver(), WeatherUtilites.WEATHER_UPDATETIME)/3600000);
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    if (Math.abs(oldcurrenttime-currenttime )>=24) {
	        R2.echo("isWeatherOutOfDate24() return true");
	        return true;
	    } else {
	        R2.echo("isWeatherOutOfDate24() return false");
	        return false;
	    }
	}
	private void updateWeatherBySoure(final Context context,List<WeatherDetails> weatherdetails)
	{
    	if(weatherdetails!=null&&weatherdetails.size()>0){
    		Configuration configuration = mres.getConfiguration();
    		String language = configuration.locale.toString();
    		for(int i=0;i<4&&i<weatherdetails.size();i++){
    			WeatherDetails weatherdetail =null;
	    		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
	    		Date now = calendar.getTime();
	    		now.setDate(now.getDate()+i);
				final String data = DateFormat.format(DEFALUT_DATE_FORMAT_CN, now).toString();
				for(int j=0;j<weatherdetails.size();j++){
	    			R2.echo("weatherDetails.get("+j+").mcityData="+weatherdetails.get(j).mcityDate);
	    			if(data.equals(weatherdetails.get(j).mcityDate)){
	    				weatherdetail = weatherdetails.get(j);
	    				break;
	    			}
	    		}
				if(weatherdetail==null){
					if(i==0){
						Log.i("czz", "no data0****************************");
						setDefaultWeatherPic();
					}else if(i==1){
						Log.i("czz", "no data1****************************");
						setDefaultWeatherPic0();
					}else if(i==2){
						Log.i("czz", "no data2****************************");
						setDefaultWeatherPic1();
					}else if(i==3){
						Log.i("czz", "no data3****************************");
        				if(WeatherUtilites.isInDefaultTheme(mcontext)){
        		    		mweather_widget_details_content_layout_content[2].setText("");
        		    		mweather_widget_details_content_layout_temp[2].setText("");
        		    	}
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
	        		if(content==null||TextUtils.isEmpty(content))
	        			content = weatherdetail.mcityStatus1;
	    		}catch(Exception e){
	            	e.printStackTrace();
	            	content = weatherdetail.mcityStatus1;
	    		}
	    		String iconimage = WeatherUtilites.ICON_SINA_MAP.get(weatherdetail.mcityStatus1);
	    		Log.d("czz",i+"  "+ content+"  iconimage="+iconimage+"  "+data);
    			if(i==0){
//    	            Settings.System.putString(mcontext.getContentResolver(), WeatherUtilites.CONDITION,content);
//    	            Settings.System.putString(mcontext.getContentResolver(), WeatherUtilites.WEATHERTEMPS,weatherdetail.mcityTemperature);
    	    		if(misQHD){
    		    		mweather_widget_magic_description.setText(content);
    		    		mweather_widget_magic_description.setTextColor(
    		    				mapp.mLauncherContext.getColor(R.color.weather_magic_widget_content_color_41,
    		    						R.color.weather_magic_widget_content_color_41));
    	    		}else{
    		    		mweather_widget_magic_baidu_description.setText(content);
    		    		mweather_widget_magic_baidu_description.setTextColor(
    		    				mapp.mLauncherContext.getColor(R.color.weather_magic_widget_content_color_41,
    		    						R.color.weather_magic_widget_content_color_41));
    	    		}
    	    		if(misQHD){
    		    		mweather_widget_magic_temperature.setText(weatherdetail.mcityTemperature);
    		    		mweather_widget_magic_temperature.setTextColor(
    		    				mapp.mLauncherContext.getColor(R.color.weather_magic_widget_temp_color_41,
    		    						R.color.weather_magic_widget_temp_color_41));    		
    		    	}else{
    	    			mweather_widget_magic_baidu_temperature.setText(weatherdetail.mcityTemperature);
    	    			mweather_widget_magic_baidu_temperature.setTextColor(
    	    				mapp.mLauncherContext.getColor(R.color.weather_magic_widget_temp_color_41,
    	    						R.color.weather_magic_widget_temp_color_41));     		
    		    	}
    	    		
    	        	if(iconimage==null ||TextUtils.isEmpty(iconimage)){
    		        	setDefaultWeatherPic();
//    		            Settings.System.putString(mcontext.getContentResolver(), WeatherUtilites.CONDITION,"");
//    		            Settings.System.putString(mcontext.getContentResolver(), WeatherUtilites.WEATHERTEMPS,"");
    	        	}
    	        	else{
    	                if (!IsDaytime()) {
    	                	if(iconimage.contains("d"))
    	                		iconimage = iconimage.replace("d", "n");
    	                }else{
    	                	if(iconimage.contains("n"))
    	                		iconimage = iconimage.replace("n", "d");
    	                }
    	                int iconid = -1;
    	    	        try {
    		        		final String img = "weather_magic_widget_"+iconimage;
    		        		R2.echo("img="+img);
    		        		Log.d("czz",i+"img="+img);
    		        		iconid = WeatherUtilites.MAGIC_ICON_MAP.get(iconimage);
    	            		if(misQHD){
    	            			mweather_widget_magic_icon.setImageDrawable(
    	            					mapp.mLauncherContext.getDrawable(iconid));
    	            			mweather_widget_magic_icon.setTag(iconimage);
    	            		}
    	            		else
    	            			mweather_widget_magic_baidu_pic.setImageDrawable(
    	            					mapp.mLauncherContext.getDrawable(iconid));
    	               		WeatherUtilites.saveWeatherIconUpdate(context,data,iconimage,true);

    	                }catch(Exception e){
    	                	e.printStackTrace();
    	                	iconid = -1;
    	                	setDefaultWeatherPic();
    	                }
    	            }
    	        	if(WeatherUtilites.isInDefaultTheme(context)){
	                	try{
	                		mweather_widget_details_content_city.setText(mweather_widget_magic_location.getText()+" "+content);
		                	mweather_widget_details_content_temp.setText(weatherdetail.mcityTemperature);
		                	mweather_widget_details_content_wind.setText((weatherdetail.mcityDirection1==null?"风力":(weatherdetail.mcityDirection1+"风"))+weatherdetail.mcityPower);
		                	
		                	if(weatherdetail.mcityChyL==null&&TextUtils.isEmpty(weatherdetail.mcityChyL)){
		                		weatherdetail.mcityChyL="";
		                	}
		                	mweather_widget_details_content_index.setText(" 穿衣: "+weatherdetail.mcityChyL);
		                    mweather_widget_details_content_index.setCompoundDrawablesWithIntrinsicBounds(R.drawable.weather_cloths, 0, 0, 0);
		                    
		                    if(weatherdetail.mcityZwxL==null&&TextUtils.isEmpty(weatherdetail.mcityZwxL)){
		                		weatherdetail.mcityZwxL="";
			                }
		                    mweather_widget_details_content_index_uv.setText(" 紫外线: "+weatherdetail.mcityZwxL);
		                    mweather_widget_details_content_index_uv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.weather_ultraviolet_ray, 0, 0, 0);

		                	if(weatherdetail.mcityPollutionL==null&&TextUtils.isEmpty(weatherdetail.mcityPollutionL)){
		                		weatherdetail.mcityPollutionL = "";
		                 	}
		               		mweather_widget_details_content_index_xc.setText(" 污染: "+weatherdetail.mcityPollutionL);
		            		mweather_widget_details_content_index_xc.setCompoundDrawablesWithIntrinsicBounds(R.drawable.weather_pollution, 0, 0, 0);

		                	if(weatherdetail.mcityKtkL==null&&TextUtils.isEmpty(weatherdetail.mcityKtkL)){
		                		weatherdetail.mcityKtkL ="";
		                	}
		            		mweather_widget_details_content_index_tr.setText(" 空调: "+weatherdetail.mcityKtkL);
		            		mweather_widget_details_content_index_tr.setCompoundDrawablesWithIntrinsicBounds(R.drawable.weather_ktk, 0, 0, 0);
	            		}catch(Exception e){
	            			e.printStackTrace();
	            		}
	                }
    	        	context.sendBroadcast(new Intent("com.lenovo.leos.widgets.weather.update_weather"));
    			}
    			if(i>0 && WeatherUtilites.isInDefaultTheme(context)){
                	try{
                		mweather_widget_details_content_layout_content[i-1].setText(content);
                    	mweather_widget_details_content_layout_temp[i-1].setText(weatherdetail.mcityTemperature);
            		}catch(Exception e){
            			e.printStackTrace();
            		}
                }
    		}
    		if(weatherdetails.size()<4){
    			for(int k=weatherdetails.size(); k<4; k++){
    				try{
                		mweather_widget_details_content_layout_content[k-1].setText("");
                    	mweather_widget_details_content_layout_temp[k-1].setText("");
            		}catch(Exception e){
            			e.printStackTrace();
            		}
    			}
    		}
    		
    	}else{
    		setDefaultWeatherPic();
    	}
	}
	private void updateWeatherByApp(final Context context,List<WeatherApp> weatherapps)
	{
    	if(weatherapps!=null&&weatherapps.size()>2){
    		Configuration configuration = mres.getConfiguration();
    		String language = configuration.locale.toString();
    		for(int i=0;i<4 && i<weatherapps.size();i++){
    			WeatherApp weatherapp = null;
        		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        		Date now = calendar.getTime();
    			final String data = DateFormat.format(DEFALUT_DATE_FORMAT_CN, now).toString();
        		for(int j=0;j<weatherapps.size();j++){
        			R2.echo("weatherapps.get(j).mcityData="+weatherapps.get(j).mcityData);
        			if(data.equals(weatherapps.get(j).mcityData)){
        				weatherapp = weatherapps.get(j);
        				break;
        			}
        		}
        		if(weatherapp==null){
        			if(i==0)
						setDefaultWeatherPic();
					else if(i==1)
						setDefaultWeatherPic0();
					else if(i==2){
						setDefaultWeatherPic1();
					}else if(i==3){
        				if(WeatherUtilites.isInDefaultTheme(mcontext)){
        		    		mweather_widget_details_content_layout_content[1].setText("");
        		    		mweather_widget_details_content_layout_temp[1].setText("");
        		    	}
        			}
	    			continue;
        		}
        		String content = null;
        		if(weatherapp.mcityStatus1!=null&&!weatherapp.mcityStatus1.equals(weatherapp.mcityStatus2)){
            		try {
    	        		if (language.equals("zh_CN")){ 
    	        			content = weatherapp.mcityStatus1+"转"+weatherapp.mcityStatus2;
    	        		}
    	        		else if (language.equals("zh_TW")){
    	        			content = weatherapp.mcityStatus1+"转"+weatherapp.mcityStatus2;
    	        		}
    	        		else{
    	        			if(IsDaytime())
    	        				content = WeatherUtilites.ICON_SINA_EN_APP_STRING_MAP.get(weatherapp.mcityStatusCode1);
    	        			else
    	        				content = WeatherUtilites.ICON_SINA_EN_APP_STRING_MAP.get(weatherapp.mcityStatusCode2);
    	        		}
    	        		if(content==null||TextUtils.isEmpty(content))
    	        			content = weatherapp.mcityStatus1+"转"+weatherapp.mcityStatus2;
            		}catch(Exception e){
                    	e.printStackTrace();
                    	content = weatherapp.mcityStatus1+"转"+weatherapp.mcityStatus2;
            		}
    			}else{
            		try {
    	        		if (language.equals("zh_CN")){ 
    	        			content = weatherapp.mcityStatus1;
    	        		}
    	        		else if (language.equals("zh_TW")){
    	        			content = weatherapp.mcityStatus1;
    	        		}
    	        		else{
    	        			if(IsDaytime())
    	        				content = WeatherUtilites.ICON_SINA_EN_APP_STRING_MAP.get(weatherapp.mcityStatusCode1);
    	        			else
    	        				content = WeatherUtilites.ICON_SINA_EN_APP_STRING_MAP.get(weatherapp.mcityStatusCode2);
    	        		}
    	        		if(content==null||TextUtils.isEmpty(content))
    	        			content = weatherapp.mcityStatus1;
            		}catch(Exception e){
                    	e.printStackTrace();
                    	content = weatherapp.mcityStatus1;
            		}
    			}
        		String iconimage =null;
                String low = String.valueOf(Math.min(weatherapp.mcityTemperature1, weatherapp.mcityTemperature2));
                String high = String.format("%1$d\u00B0", Math.max(weatherapp.mcityTemperature1, weatherapp.mcityTemperature2));
                String temp = low + "~" + high + "C";
                if(i==0){
                	if(IsDaytime())
        				iconimage= WeatherUtilites.ICON_SINA_APP_MAP.get(weatherapp.mcityStatusCode1);
        			else
        				iconimage= WeatherUtilites.ICON_SINA_APP_MAP.get(weatherapp.mcityStatusCode2);

            		if(misQHD){
        	    		mweather_widget_magic_description.setText(content);
        	    		mweather_widget_magic_description.setTextColor(
        	    				mapp.mLauncherContext.getColor(R.color.weather_magic_widget_content_color_41,
        	    						R.color.weather_magic_widget_content_color_41));
            		}else{
        	    		mweather_widget_magic_baidu_description.setText(content);
        	    		mweather_widget_magic_baidu_description.setTextColor(
        	    				mapp.mLauncherContext.getColor(R.color.weather_magic_widget_content_color_41,
        	    						R.color.weather_magic_widget_content_color_41));
            		}
            		if(misQHD){
        	    		mweather_widget_magic_temperature.setText(temp);
        	    		mweather_widget_magic_temperature.setTextColor(
        	    				mapp.mLauncherContext.getColor(R.color.weather_magic_widget_temp_color_41,
        	    						R.color.weather_magic_widget_temp_color_41));    		
        	    	}else{
            			mweather_widget_magic_baidu_temperature.setText(temp);
            			mweather_widget_magic_baidu_temperature.setTextColor(
            				mapp.mLauncherContext.getColor(R.color.weather_magic_widget_temp_color_41,
            						R.color.weather_magic_widget_temp_color_41));     		
        	    	}
            		R2.echo("iconimage="+iconimage);
                	if(iconimage==null ||TextUtils.isEmpty(iconimage))
                		setDefaultWeatherPic();
                	else{
                        if (!IsDaytime()) {
                        	if(iconimage.contains("d"))
                        		iconimage = iconimage.replace("d", "n");
                        }else{
                        	if(iconimage.contains("n"))
                        		iconimage = iconimage.replace("n", "d");
                        }
                        int iconid = -1;
            	        try {
        	        		final String img = "weather_magic_widget_"+iconimage;
        	        		R2.echo("img="+img);
        	        		iconid = WeatherUtilites.MAGIC_ICON_MAP.get(iconimage);
                    		if(misQHD){
                    			mweather_widget_magic_icon.setImageDrawable(
                    					mapp.mLauncherContext.getDrawable(iconid));
                    			mweather_widget_magic_icon.setTag(iconimage);
                    		}
                    		else
                    			mweather_widget_magic_baidu_pic.setImageDrawable(
                    					mapp.mLauncherContext.getDrawable(iconid));
                    		WeatherUtilites.saveWeatherIconUpdate(context,data,iconimage,true);           		return ;

                        }catch(Exception e){
                        	e.printStackTrace();
                        	iconid = -1;
                        }
                        if(iconid==-1)
                        	setDefaultWeatherPic();
                        else{
                        	try {
        	            		if(misQHD){
        	            			mweather_widget_magic_icon.setImageDrawable(
        	            					mapp.mLauncherContext.getDrawable(iconid));
        	            			mweather_widget_magic_icon.setTag(iconimage);
        	            		}
        	            		else
        	            			mweather_widget_magic_baidu_pic.setImageDrawable(
        	            					mapp.mLauncherContext.getDrawable(iconid));
        	            		WeatherUtilites.saveWeatherIconUpdate(context,data,iconimage,true);
        	                }catch(Exception e){
        	                	e.printStackTrace();
        	                	iconid = -1;
        	                	setDefaultWeatherPic();
        	                }
                    	}
                    }
                }
                if(WeatherUtilites.isInDefaultTheme(context)){
                	try{
                		if(i == 0){
                        	mweather_widget_details_content_city.setText(mweather_widget_magic_location.getText()+" "+content);
                        	mweather_widget_details_content_temp.setText(temp);
                        }else{
                        	mweather_widget_details_content_layout_content[i-1].setText(content);
                        	mweather_widget_details_content_layout_temp[i-1].setText(temp);
                        }
            		}catch(Exception e){
            			e.printStackTrace();
            		}
                }
    		}
    		
			
    	}else{
    		clearWeatherInfo();
    	}
	}
	private void updateWeather(final Context context)
	{
		if(!WeatherUtilites.findForPackage(context,WeatherUtilites.SINA_PACKAGENAME)){
			new Thread() {
				@Override
				public void run() {
					try{
						WeatherUtilites.saveWeatherIconUpdate(context,"","",false);
						List<WeatherDetails> weatherdetails  = null;
						final String cityName = Settings.System.getString(context.getContentResolver(), WeatherUtilites.CITY_NAME);
						if(cityName!=null && cityName.length()>0){
							weatherdetails  = WeatherUtilites.getWeatherDetails(context);
						}
						Message message = mHandler.obtainMessage(WeatherUtilites.MES_UPDATE_SERVICE_WEATHER_DETAILS, weatherdetails);  
						mHandler.sendMessage(message);
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
			}.start();
		}else{
			new Thread() {
				@Override
				public void run() {
					try{
						WeatherUtilites.saveWeatherIconUpdate(context,"","",false);
						updateweatherdata(context);
					}catch(IllegalStateException ex){
						ex.printStackTrace();
					}
				}
	    	}.start();
		}
	}
    private void updateweatherdata(final Context context)
	{
		List<WeatherApp> weatherapps  = WeatherUtilites.getWeatherApp(context);
		Message message = mHandler.obtainMessage(WeatherUtilites.MES_UPDATE_WEATHER_DETAILS, weatherapps);  
		mHandler.sendMessage(message);
	}
    public void clearWeatherInfo()
    { 
    	setDefaultWeatherPic();
    }
    public void setDefaultWeatherPic()
    {
    	if(misQHD){
        	mweather_widget_magic_temperature.setText("");
        	mweather_widget_magic_description.setText("");
        	mweather_widget_magic_icon.setImageDrawable(
					mapp.mLauncherContext.getDrawable(R.drawable.weather_magic_widget_default));
    	}else{
        	mweather_widget_magic_baidu_temperature.setText("");
        	mweather_widget_magic_baidu_description.setText("");
        	mweather_widget_magic_baidu_pic.setImageDrawable(
					mapp.mLauncherContext.getDrawable(R.drawable.weather_magic_widget_default));
    	}
    	if(WeatherUtilites.isInDefaultTheme(mcontext)){
    		mweather_widget_details_content_temp.setText("");
    		mweather_widget_details_content_wind.setText("");
        	mweather_widget_details_content_index.setText(" 穿衣: ");
            mweather_widget_details_content_index_uv.setText(" 紫外线: ");
       		mweather_widget_details_content_index_xc.setText(" 污染: ");
    		mweather_widget_details_content_index_tr.setText(" 空调: ");
    	}
    }
    public void setDefaultWeatherPic0(){
    	if(WeatherUtilites.isInDefaultTheme(mcontext)){
    		mweather_widget_details_content_layout_content[0].setText("");
    		mweather_widget_details_content_layout_temp[0].setText("");
    	}
    }
    public void setDefaultWeatherPic1(){
    	if(WeatherUtilites.isInDefaultTheme(mcontext)){
    		mweather_widget_details_content_layout_content[1].setText("");
    		mweather_widget_details_content_layout_temp[1].setText("");
    	}
    }
    private void getScreenAnimation(final Context context,String action)
    {
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
    	R2.echo("onAttachedToWindow");
    	mHandler.sendEmptyMessage(WeatherUtilites.MES_WEATHER_WIDGET_INIT);
    	registerWeatherIntentReceiver();
	}
    @Override
    protected void onDetachedFromWindow()
    {
    	if(mFormatChangeObserver!=null)
    		mcontext.getContentResolver().unregisterContentObserver(mFormatChangeObserver);
    	if (mreceivercontext != null) {
	    	try {
	    		R2.echo("mcontext");
	    		if(mIntentReceiver!=null) {
	    			R2.echo("unregisterReceiver");
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
    	R2.echo("onDetachedFromWindow");
    }
    public void setTimeLayoutlistener(final Context context)
    {
    	OnClickListener listener = new OnClickListener(){
    		public void onClick(View v){
			    boolean res = WeatherUtilites.setOnClickListenerIntent(context,"com.lenovomobile.deskclock");
			    if (!res) {
			        if(!WeatherUtilites.setOnClickListenerIntent(context ,"com.android.deskclock")) {
			        	if(!WeatherUtilites.setOnClickListenerIntent(mcontext, "com.ontim.clock")){
	        				if(!WeatherUtilites.setOnClickListenerIntent(mcontext ,"com.lenovo.deskclock")){
	        					Intent intent = new Intent("android.settings.DATE_SETTINGS");
	        					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        	    			try {
	        	    				context.startActivity(intent);
	        	    			}catch (ActivityNotFoundException e) {
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
//    	OnLongClickListener longlistener = new View.OnLongClickListener() {
//    		public boolean onLongClick(View v) {
//    			((View)(mweatherwidgetmagicview.getParent())).performLongClick();
//    			return true;
//    		}
//    	};
    	if(misQHD){
//	    	mweather_widget_magic_time_and_date.setOnLongClickListener(longlistener);
	    	mweather_widget_magic_time_and_date.setOnClickListener(listener);
    	}else{
//	    	mweather_widget_magic_baidu_time_and_date.setOnLongClickListener(longlistener);
	    	mweather_widget_magic_baidu_time_and_date.setOnClickListener(listener);
    	}
    }
    public void setBaidulistener(final Context context)
    {
    	OnClickListener searchlistener = new OnClickListener(){
   		 public void onClick(View v){
   			final SharedPreferences preferences = context.getSharedPreferences(
   					WeatherUtilites.PRE_LAUNCHER, Activity.MODE_PRIVATE);
	   			if(preferences!=null){
	   				final String phonetype = preferences.getString(WeatherUtilites.PRE_LAUNCHER_KEY_PHONE,
	   						WeatherUtilites.PRE_LAUNCHER_KEY_PHONE_DEFAULT);
	   				R2.echo("magic phonetype = "+phonetype);
	   				if(phonetype.equals(WeatherUtilites.PRE_LAUNCHER_KEY_PHONE_BAIDU)){
	   					Intent intent = new Intent();
				    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				        intent.setPackage("com.baidu.searchbox");
				        intent.setClassName("com.baidu.searchbox", "com.baidu.searchbox.MainActivity");
				        try{
				            context.startActivity(intent);
				        }catch(ActivityNotFoundException e){
				            e.printStackTrace();
	   						Toast.makeText(context, mres.getString(R.string.weather_baidu_alert_message), Toast.LENGTH_SHORT).show();
				        }
	   				}else{
	   					Intent intent = new Intent("com.baidu.searchbox.action.HOME");
	   					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	   					try {
	   						context.startActivity(intent);
	   					} catch (ActivityNotFoundException e) {
	   						e.printStackTrace();
	   						Toast.makeText(context, mres.getString(R.string.weather_baidu_alert_message), Toast.LENGTH_SHORT).show();
	                        // TODO: 提示百度搜索被卸载(例如手机被用户Root后,内置程序被删)
	   					}
	   				}
			   	}
   		 	}
    	};
    	OnClickListener baidulistener = new OnClickListener(){
      		 public void onClick(View v){
      			final SharedPreferences preferences = context.getSharedPreferences(
      					WeatherUtilites.PRE_LAUNCHER, Activity.MODE_PRIVATE);
   	   			if(preferences!=null){
   	   				final String phonetype = preferences.getString(WeatherUtilites.PRE_LAUNCHER_KEY_PHONE,
   	   						WeatherUtilites.PRE_LAUNCHER_KEY_PHONE_DEFAULT);
   	   				R2.echo("magic phonetype = "+phonetype);
   	   				if(phonetype.equals(WeatherUtilites.PRE_LAUNCHER_KEY_PHONE_BAIDU)){
   	   					Intent intent = new Intent();
   				    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
   				        intent.setPackage("com.baidu.searchbox");
   				        intent.setClassName("com.baidu.searchbox", "com.baidu.searchbox.MainActivity");
   				        intent.putExtra("GridViewScrolledDown", true);
   				        try{
   				            context.startActivity(intent);
   				        }catch(ActivityNotFoundException e){
   				            e.printStackTrace();
   	   						Toast.makeText(context, mres.getString(R.string.weather_baidu_alert_message), Toast.LENGTH_SHORT).show();
   				        }
   	   				}else{
   	   					Intent intent = new Intent("com.baidu.searchbox.action.HOME");
   	   					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
   	   					try {
   	   						context.startActivity(intent);
   	   					} catch (ActivityNotFoundException e) {
   	   						e.printStackTrace();
   	   						Toast.makeText(context, mres.getString(R.string.weather_baidu_alert_message), Toast.LENGTH_SHORT).show();
   	                        // TODO: 提示百度搜索被卸载(例如手机被用户Root后,内置程序被删)
   	   					}
   	   				}
   			   	}
      		 	}
       	};
    	OnClickListener yuyinlistener = new OnClickListener(){
     		 public void onClick(View v){
     			final SharedPreferences preferences = context.getSharedPreferences(
     					WeatherUtilites.PRE_LAUNCHER, Activity.MODE_PRIVATE);
  	   			if(preferences!=null){
  	   				final String phonetype = preferences.getString(WeatherUtilites.PRE_LAUNCHER_KEY_PHONE,
  	   						WeatherUtilites.PRE_LAUNCHER_KEY_PHONE_DEFAULT);
  	   				R2.echo("magic phonetype = "+phonetype);
  	   				if(phonetype.equals(WeatherUtilites.PRE_LAUNCHER_KEY_PHONE_BAIDU)){
  	   					Intent intent = new Intent();
  				    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
  				        intent.setPackage("com.baidu.searchbox");
  				        intent.setClassName("com.baidu.quicksearchbox", "com.baidu.quicksearchbox.baiduvoice.VoiceActivity");
  				        try{
  				            context.startActivity(intent);
  				        }catch(ActivityNotFoundException e){
  				            e.printStackTrace();
  	   						Toast.makeText(context, mres.getString(R.string.weather_baidu_alert_message), Toast.LENGTH_SHORT).show();
  				        }
  	   				}else{
  	   					Intent intent = new Intent("com.baidu.searchbox.action.HOME");
  	   					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
  	   					try {
  	   						context.startActivity(intent);
  	   					} catch (ActivityNotFoundException e) {
  	   						e.printStackTrace();
  	   						Toast.makeText(context, mres.getString(R.string.weather_baidu_alert_message), Toast.LENGTH_SHORT).show();
  	                        // TODO: 提示百度搜索被卸载(例如手机被用户Root后,内置程序被删)
  	   					}
  	   				}
  			   	}
     		 	}
      	};
//    	OnLongClickListener longlistener = new View.OnLongClickListener() {
//    		public boolean onLongClick(View v) {
//    			((View)(mweatherwidgetmagicview.getParent())).performLongClick();
//    			return true;
//    		}
//    	};
    	mweather_widget_magic_baidu_search_icon.setOnClickListener(baidulistener);
//    	mweather_widget_magic_baidu_search_icon.setOnLongClickListener(longlistener);
    	mweather_widget_magic_baidu_search_yuyin.setOnClickListener(yuyinlistener);
//    	mweather_widget_magic_baidu_search_yuyin.setOnLongClickListener(longlistener);
    	mweather_widget_magic_baidu_search_bg.setOnClickListener(searchlistener);
//    	mweather_widget_magic_baidu_search_bg.setOnLongClickListener(longlistener);
    }
    public void setWeathInfolistener(final Context context)
    {
    	
    	OnClickListener citylistener = new OnClickListener(){
	   		public void onClick(View v){
	   			boolean networkEnabled = SettingsValue.isNetworkEnabled(context);
	 			if(networkEnabled){
    				if(!WeatherUtilites.findForPackage(context,WeatherUtilites.SINA_PACKAGENAME)){
    					Intent intent = new Intent(Intent.ACTION_MAIN, null);
						intent.addCategory(Intent.CATEGORY_LAUNCHER);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.setPackage(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF);
					    intent.setClassName(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF, "com.lenovo.launcher2.settings.ProvinceActivity");
					    try{
					    	context.startActivity(intent);
					    }catch(ActivityNotFoundException e){
					    	e.printStackTrace();
		    	        }
    				}else{
    					Intent intent = new Intent("sina.mobile.tianqitong.action.startservice.switch_appwidget_city");
    		   			context.startService(intent);
    				}
	 			}else{
					Intent intent = new Intent(Intent.ACTION_MAIN, null);
					intent.addCategory(Intent.CATEGORY_LAUNCHER);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setPackage(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF);
					intent.setClassName(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF, 
							"com.lenovo.launcher2.weather.widget.NetworkAlertActivity");
    	            intent.putExtra(WeatherUtilites.EXTRA_DIALOG_TYPE, 0);
    	            try{
    	            	context.startActivity(intent);
    	            }catch(ActivityNotFoundException e){
    	            	e.printStackTrace();
    	            }
    			}
	   		}
    	};
    	OnClickListener listener = new OnClickListener(){
    		 public void onClick(View v){
    			 boolean networkenabled = SettingsValue.isNetworkEnabled(context);
    			 if(networkenabled){
    				if(!WeatherUtilites.findForPackage(context,WeatherUtilites.SINA_PACKAGENAME)){
    					if(!isWeatherOutOfDate24(context)){
    	 		 			if(WeatherUtilites.isInDefaultTheme(context) && mweather_widget_details_pic!=null && mweather_widget_details_pic.getwindowstate())
    	 		 				displayDetailsWindow(context);
    	 		 		}else{
    	 		 			R2.echo("weather is out of date 24hours");
    	 		 		}
					}else{
						Intent intent = new Intent("sina.mobile.tianqitong.main.Splash");
						intent.setClassName("sina.mobile.tianqitong", "sina.mobile.tianqitong.main.Splash");
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						try{
		    	            context.startActivity(intent);
		    	            }catch(ActivityNotFoundException e){
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
    			}else{
					Intent intent = new Intent(Intent.ACTION_MAIN, null);
					intent.addCategory(Intent.CATEGORY_LAUNCHER);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setPackage(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF);
					intent.setClassName(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF, 
							"com.lenovo.launcher2.weather.widget.NetworkAlertActivity");
    	            intent.putExtra(WeatherUtilites.EXTRA_DIALOG_TYPE, 0);
    	            try{
    	            context.startActivity(intent);
    	            }catch(ActivityNotFoundException e){
    	            	e.printStackTrace();
    	            }
    			}
    		}
    	};
//    	OnLongClickListener longlistener = new View.OnLongClickListener() {
//    		public boolean onLongClick(View v) {
//    			((View)(mweatherwidgetmagicview.getParent())).performLongClick();
//    			return true;
//    		}
//    	};
/*    	OnLongClickListener longexlistener = new View.OnLongClickListener() {
    		public boolean onLongClick(View v) {
    			((View)(mweatherwidgetmagicview.getParent())).performLongClick();
    			Intent intent = new Intent("android.intent.action.WORKSPACE_PICK_LEOSEXWIDGET");
    			context.startActivity(intent);
    			return true;
    		}
    	};*/
//    	if(WeatherUtilites.findForPackage(context,WeatherUtilites.SINA_PACKAGENAME)){
    		if(misQHD)
    			mweather_widget_magic_location.setOnClickListener(citylistener);
    		else
    			mweather_widget_magic_baidu_location.setOnClickListener(citylistener);
    	/*}else{
    		if(misQHD)
    			mweather_widget_magic_location.setOnClickListener(listener);
    		else
    			mweather_widget_magic_baidu_location.setOnClickListener(listener);

    	}*/
    	if(misQHD){
    		mweather_widget_magic_icon.setOnClickListener(listener);
//    		mweather_widget_magic_icon.setOnLongClickListener(longlistener);
    		mweather_widget_magic_layout.setOnClickListener(citylistener);
//    		mweather_widget_magic_layout.setOnLongClickListener(longlistener);
    		mweather_widget_magic_bg.setOnClickListener(listener);
//    		mweather_widget_magic_bg.setOnLongClickListener(longlistener);
    	}else{
    		mweather_widget_magic_baidu_pic.setOnClickListener(listener);
//    		mweather_widget_magic_baidu_pic.setOnLongClickListener(longlistener);
    		mweather_widget_magic_baidu_left.setOnClickListener(listener);
//    		mweather_widget_magic_baidu_left.setOnLongClickListener(longlistener);
    		mweather_widget_magic_baidu_bg.setOnClickListener(listener);
//    		mweather_widget_magic_baidu_bg.setOnLongClickListener(longlistener);
    	}
    }
    public String DisplayTelephoneName(Context context){
    	boolean isDouble = true;
    	Method method = null;
    	Object result_0 = null;
    	Object result_1 = null;
    	String Carrier = null;
    	String Carrier1 = null;
    	Carrier = WeatherUtilites.getCarrier(context, "0");
    	Carrier1 = WeatherUtilites.getCarrier(context, "1");
    	Log.d("a","result_01="+Carrier);
    	Log.d("a","result_011="+Carrier1);
    	TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    	try {
	    	//只要在反射getSimStateGemini 这个函数时报了错就是单卡手机（这是我自己的经验，不一定全正确）
	    	method = TelephonyManager.class.getMethod("getSimStateGemini",new Class[] { int.class });
	    	result_0 = method.invoke(tm, new Object[] { new Integer(0) });
	    	result_1 = method.invoke(tm, new Object[] { new Integer(1) });
	    } catch (SecurityException e) {
	    	isDouble = false;
	    	e.printStackTrace();
    	} catch (NoSuchMethodException e) {
    		isDouble = false;
    		e.printStackTrace();
    	} catch (IllegalArgumentException e) {
    		isDouble = false;
    		e.printStackTrace();
    	} catch (IllegalAccessException e) {
    		isDouble = false;
    		e.printStackTrace();
    	} catch (InvocationTargetException e) {
    		isDouble = false;
    		e.printStackTrace();
    	} catch (Exception e){
    		isDouble = false;
    		e.printStackTrace();
    	}
    	Log.d("a","result_0="+result_0);
    	Log.d("a","result_01="+result_1);
    	if(isDouble){
    	//保存双卡是否可用
    	//如下判断哪个卡可用.双卡都可以用
	    	if(result_0!=null&&result_0.toString().equals("5")&&result_1!=null && result_1.toString().equals("5")){
	    		return Carrier+"|"+Carrier1;
	    	} else if(result_0!=null&&!result_0.toString().equals("5") && result_1!=null&&result_1.toString().equals("5")){//卡二可用
	    		return Carrier+"|"+Carrier1;
		    	
		    } else if(result_0!=null&&result_0.toString().equals("5") && result_1!=null&&!result_1.toString().equals("5")){//卡一可用
		    	return Carrier+"|"+Carrier1;
		    } else {//两个卡都不可用(飞行模式会出现这种种情况)
		    	return Carrier+"|"+Carrier1;
		    }
    	}else{
    		if(result_0!=null&&!result_0.toString().equals("5") && result_1!=null&&result_1.toString().equals("5")){//卡二可用
	    		return Carrier1;
		    	
		    } else if(result_0!=null&&result_0.toString().equals("5") && result_1!=null&&!result_1.toString().equals("5")){//卡一可用
		    	return Carrier;
		    } 
    	}
    	if(result_0!=null&&result_0.toString().equals("0")&&result_1!=null&&result_1.toString().equals("0")){
    		return Carrier+"|"+Carrier1;
    	}else if(result_0!=null&&result_0.toString().equals("0")&&result_1!=null&&!result_1.toString().equals("0")){
    		return Carrier;
    	}else if(result_0!=null&&!result_0.toString().equals("0")&&result_1!=null&&result_1.toString().equals("0")){
    		return Carrier1;
    	}else
    		return Carrier;
    	
    }
    
    /** 
     * 截屏方法 
     * @return 
     */  
     private Bitmap shot(int type) {
 	    Bitmap bmp = WeatherUtilites.getBGBitmap(mcontext,mdisplaywidth,mdisplayheight);
 	    Log.d("ad","statusBarHeight="+statusBarHeight);
 	    try{
 		    if(type==0){
 			    bmp = Bitmap.createBitmap(bmp,0,statusBarHeight,mdisplaywidth,(mrealheight)/2);
 		    }
 		    else{
 		    	bmp = Bitmap.createBitmap(bmp,0,statusBarHeight+mrealheight/2,mdisplaywidth,mrealheight/2);
 		    }
 	    }catch(IllegalArgumentException e){
 	    	e.printStackTrace();
 	    }
 	    return bmp;
     }
     
    public void displayDetailsWindow(final Context context)
    {

    	OnClickListener listener = new OnClickListener(){
      		 public void onClick(View v){
      			mweather_widget_details_pic.hideview();
      		 }
       	};
       	mbitmap = shot(0);
    	BitmapDrawable up = new BitmapDrawable(mbitmap);  
    	mbitmap = shot(1);
    	BitmapDrawable down = new BitmapDrawable(mbitmap);
    	mweather_widget_details_pic.setOnClickListener(listener);
    	mweather_widget_details_pic.setBitmap(up, down,mdisplaywidth,
    			mrealheight,mweatherwidgetdetailsview,this);
//    	Log.d("ad", "mweatherwidgetdetailsview.city="+mweatherwidgetdetailsview.m)
    	mweather_widget_details_pic.showview(true,mrealheight/4);
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
        
    }
//    private Bitmap mViewCache = null;
//	private void buildViewCache() {
//		destroyDrawingCache();
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
//	}
//	public Bitmap getsnapCache()
//	{
//		return mViewCache;
//	}
}
