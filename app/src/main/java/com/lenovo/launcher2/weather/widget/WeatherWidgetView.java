package com.lenovo.launcher2.weather.widget;


import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import com.lenovo.launcher2.weather.widget.settings.LenovoExWigetInfo;
import com.lenovo.launcher2.weather.widget.settings.WeatheDetailsApp;
import com.lenovo.launcher2.weather.widget.settings.WeatherApp;
import com.lenovo.launcher2.weather.widget.settings.WeatherDetails;
import com.lenovo.launcher2.weather.widget.utils.WeatherUtilites;
import com.lenovo.launcher2.weather.widget.utils.Debug.R2;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WeatherWidgetView extends LinearLayout{
	
	private TextView mweather_widget_day_city;
	private TextView mweather_widget_day_content;
	private TextView mweather_widget_day_temp;
	private ImageView mweather_widget_time_bg;
	private ImageView mweather_widget_time_num_00;
	private ImageView mweather_widget_time_num_01;
	private ImageView mweather_widget_time_num_02;
	private ImageView mweather_widget_time_num_03;
	private ImageView mweather_widget_time_num_10;
	private ImageView mweather_widget_time_num_11;
	private ImageView mweather_widget_time_num_12;
	private ImageView mweather_widget_time_num_13;
	private ImageView mweather_widget_time_num_colon;
	private ImageView mweather_widget_day_icon;
	private TextView mweather_widget_day_date;
	private TextView mweather_widget_day_week;
	private LinearLayout mweather_widget_day_details_layout;
	private ImageView mweather_widget_layout;
	private ImageView mweather_widget_time_apmp;
	private RelativeLayout mweather_widget_time_layout;
	private LinearLayout mweather_widget_other_day_layout;
	private LinearLayout mweather_widget_animation_layout;
	private ImageView mweather_widget_other_info_switch;
	private LinearLayout mweather_widget_time_num_layout;
	private TextView mweather_widget_city_empty;
	private Context mcontext;
	private View mweatherwidgetmagicview;
   /*RK_ID:RK_LEOSWIDGET AUT:kangwei DATE: 2012-10-17 START */
	private static final String THIS_PUSHMAILWIDGET =
	                 "com.lenovo.launcher2.weather.widget.WeatherWidgetView";
   /*RK_ID:RK_LEOSWIDGET AUT:kangwei DATE: 2012-10-17 E */
	private BroadcastReceiver mIntentReceiver = null ;
	private static Resources mres;
	private static final String EXTRA_NETWORK_ENABLED = "network_enabled";
	private FormatChangeObserver mFormatChangeObserver;
	private static final  Uri muri = Uri.withAppendedPath(Settings.System.CONTENT_URI,
            Settings.System.DATE_FORMAT);
	private Handler mHandler = null;
	private static Bitmap  mPicbitmap = null;
	private static boolean misunlock = true;
    private static final String PREF_NETWORK_ENABLER = "pref_network_enabler";
    private static boolean dis12or24=false;
	private static String mTimeFormat;
    private static final String M12 = "hh:mm";
    private static final String M24 = "kk:mm";
    private static final String DEFALUT_DATE_FORMAT_CN = "yyyy-MM-dd";
    private static final String DEFALUT_DATE_FORMAT_EN = "MM/dd/yyyy";
    private static final String DEFALUT_DATE_FORMAT_WVGA_EN = "MM/dd";
    private static final String ACTION_CHANGE_LETHEME = "action.letheme.apply";
    private boolean mistimeset = false;
    private static String YUBAOMAGICVIEW = "com.lenovo.launcher2.weather.widget.WeatherWidgetYuBaoMagicView";
    private static String ZHISHUMAGICVIEW = "com.lenovo.launcher2.weather.widget.WeatherWidgetZhishuiView";
    LauncherApplication mapp ;
//	Animation mat00;
//	Animation mat01; 
//	Animation mat02;
//	Animation mat03; 
//	Animation mat10;
//	Animation mat11; 
//	Animation mat12;
//	Animation mat13; 
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
	private View manimview = null;
    private WeatherWidgetYuBaoMagicView mweatherwidgetmagicYuBaoview;
    private WeatherWidgetZhishuiView mweatherwidgetzhishuiview;
    private LinearLayout.LayoutParams mlp;
    private String mcurrentnum = "00:00";
    private boolean miscurrentother = false;
	private Bitmap mViewCache = null;
	public WeatherWidgetView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	public WeatherWidgetView(Context context) {
		super(context);
		this.setGravity(Gravity.CENTER);
		mcontext = context;
		mapp = (LauncherApplication) getContext().getApplicationContext();
/*		mat00 = AnimationUtils.loadAnimation(mcontext, R.anim.push_up_in);
		mat01 = AnimationUtils.loadAnimation(mcontext, R.anim.push_up_in);
		mat02 = AnimationUtils.loadAnimation(mcontext, R.anim.push_up_in);
		mat03 = AnimationUtils.loadAnimation(mcontext, R.anim.push_up_in);
		mat10 = AnimationUtils.loadAnimation(mcontext, R.anim.push_up_out);
		mat11 = AnimationUtils.loadAnimation(mcontext, R.anim.push_up_out);
		mat12 = AnimationUtils.loadAnimation(mcontext, R.anim.push_up_out);
		mat13 = AnimationUtils.loadAnimation(mcontext, R.anim.push_up_out);
		mat10.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				int currentnum = mcurrentnum.charAt(0) - '0';
				mweather_widget_time_num_00.setBackgroundDrawable(
						mapp.mLauncherContext.getDrawable(WeatherUtilites.MAGIC_NUMBER[currentnum]));
		    	mweather_widget_time_num_10.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
		});
		mat11.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				int currentnum = mcurrentnum.charAt(1) - '0';

				currentnum = mcurrentnum.charAt(1) - '0';  
				mweather_widget_time_num_01.setBackgroundDrawable(
						mapp.mLauncherContext.getDrawable(WeatherUtilites.MAGIC_NUMBER[currentnum]));
		    	mweather_widget_time_num_11.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
		});
		mat12.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				int currentnum = mcurrentnum.charAt(3) - '0';
				mweather_widget_time_num_02.setBackgroundDrawable(
						mapp.mLauncherContext.getDrawable(WeatherUtilites.MAGIC_NUMBER[currentnum]));
		    	mweather_widget_time_num_12.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
		});
		mat13.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				int currentnum = mcurrentnum.charAt(4) - '0';
				mweather_widget_time_num_03.setBackgroundDrawable(
						mapp.mLauncherContext.getDrawable(WeatherUtilites.MAGIC_NUMBER[currentnum]));
		    	mweather_widget_time_num_13.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
		});*/
		initdatas(context);
		// TODO Auto-generated constructor stub
	}
	private  void initdatas(final Context context)
	{
		mres = context.getResources();
		statusBarHeight =WeatherUtilites.getStatusHeights(context);
		initlayoutWVGA(context);
		mweather_widget_city_empty.setTextColor(
				mapp.mLauncherContext.getColor(R.color.weather_magic_widget_cityname_empty,
						R.color.weather_magic_widget_cityname_empty));
		mweather_widget_city_empty.setText(context.getResources().getString(R.string.weaher_city_nul_empty));
        initHandler(context);
    	Intent intent = new Intent(WeatherUtilites.ACTION_ADD_WEATHER_WIDGET);
//    	context.startService(intent.setClass(context,WidgetService.class));
    	context.sendBroadcast(intent);
    	mFormatChangeObserver = new FormatChangeObserver();
    	mcontext.getContentResolver().registerContentObserver(muri, true,
                mFormatChangeObserver);
        misunlock = true;
        this.clearChildFocus(mweatherwidgetmagicview);
	}
	private void initlayoutWVGA(final Context context)
	{
		final DisplayMetrics display = context.getResources().getDisplayMetrics();
    	mdisplaywidth = display.widthPixels;
    	mdisplayheight = display.heightPixels;
    	mrealheight =mdisplayheight-statusBarHeight;
		this.removeAllViews();
		mweatherwidgetmagicYuBaoview = new WeatherWidgetYuBaoMagicView(context);
		mweatherwidgetzhishuiview =  new WeatherWidgetZhishuiView(context);
      	mweatherwidgetmagicview = mapp.mLauncherContext.getLayoutViewByName(true, "weather_widget_magic_layout",null);
      	if(mweatherwidgetmagicview!=null){
	      	try{
	      		initOtherLayout(context);
		    }catch(Exception ex){
	      		ex.printStackTrace();
	      		initLocalLayout(context);
	      	}
      	}else{
      		initLocalLayout(context);
     	}
      	if(WeatherUtilites.isInDefaultTheme(context)){
      		initDefaultLayout(context);
      	}
    	mlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    	LenovoExWigetInfo lwi = WeatherUtilites.getExWidgetInfo(context);
    	mweather_widget_animation_layout.setVisibility(View.GONE);
    	Log.d("c","lwi="+lwi.activity_name);
    	Log.d("c","lwi="+lwi.widgetView_name);
    	if(lwi.activity_name!=null&&!TextUtils.isEmpty(lwi.activity_name)
    			&&lwi.widgetView_name!=null&&!TextUtils.isEmpty(lwi.widgetView_name)){
    		mweather_widget_other_day_layout.removeAllViews();
	    	if(YUBAOMAGICVIEW.equals(lwi.widgetView_name)){
	        	mweather_widget_other_day_layout.addView(mweatherwidgetmagicYuBaoview,mlp);
	        	miscurrentother = false;
	        	mweather_widget_other_info_switch.setImageDrawable(mapp.mLauncherContext.getDrawable(
						R.drawable.weather_widget_other_zhishu_switch));
	    	}else if(ZHISHUMAGICVIEW.equals(lwi.widgetView_name)){ 
	    		mweather_widget_other_day_layout.addView(mweatherwidgetzhishuiview,mlp);
	    		mweather_widget_other_info_switch.setImageDrawable(mapp.mLauncherContext.getDrawable(
						R.drawable.weather_widget_other_weather_switch));
            	miscurrentother = true;
        	}else{
	    		View view = (View)WeatherUtilites.getLeosWidgetViewToWorkspace(lwi.activity_name,lwi.widgetView_name,mcontext);
	    		if(view!=null)
	    			mweather_widget_other_day_layout.addView(view,mlp);
	    		else{
	    			mweather_widget_other_day_layout.addView(mweatherwidgetmagicYuBaoview,mlp);
		        	mweather_widget_other_info_switch.setImageDrawable(mapp.mLauncherContext.getDrawable(
							R.drawable.weather_widget_other_zhishu_switch));
	    		}
	    	}
    	}
    	else{
    		mweather_widget_other_day_layout.removeAllViews();
			mweather_widget_other_day_layout.addView(mweatherwidgetmagicYuBaoview,mlp);
        	mweather_widget_other_info_switch.setImageDrawable(mapp.mLauncherContext.getDrawable(
					R.drawable.weather_widget_other_zhishu_switch));
			miscurrentother = false;
    	}
    	mweather_widget_time_num_10.setVisibility(View.GONE);
    	mweather_widget_time_num_11.setVisibility(View.GONE);
    	mweather_widget_time_num_12.setVisibility(View.GONE);
    	mweather_widget_time_num_13.setVisibility(View.GONE);
		this.addView(mweatherwidgetmagicview,mlp);
		setDefaultWeatherBG();
	}
	private void initOtherLayout(final Context context)
	{
		mweather_widget_day_city = (TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetmagicview,"weather_widget_day_city");
    	mweather_widget_day_content = (TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetmagicview,"weather_widget_day_content");
    	mweather_widget_day_temp = (TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetmagicview,"weather_widget_day_temp");
    	mweather_widget_time_num_00 = (ImageView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetmagicview,"weather_widget_time_num_00");
    	mweather_widget_time_num_01 = (ImageView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetmagicview,"weather_widget_time_num_01");
    	mweather_widget_time_num_02 = (ImageView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetmagicview,"weather_widget_time_num_02");
    	mweather_widget_time_num_03 = (ImageView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetmagicview,"weather_widget_time_num_03");
    	mweather_widget_time_num_10 = (ImageView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetmagicview,"weather_widget_time_num_10");
    	mweather_widget_time_num_11 = (ImageView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetmagicview,"weather_widget_time_num_11");
    	mweather_widget_time_num_12 = (ImageView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetmagicview,"weather_widget_time_num_12");
    	mweather_widget_time_num_13 = (ImageView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetmagicview,"weather_widget_time_num_13");
    	mweather_widget_time_num_colon = (ImageView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetmagicview,"weather_widget_time_num_colon");
    	mweather_widget_day_icon = (ImageView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetmagicview,"weather_widget_day_icon");
    	mweather_widget_day_date = (TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetmagicview,"weather_widget_day_date");
    	mweather_widget_day_week = (TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetmagicview,"weather_widget_day_week");
    	mweather_widget_layout = (ImageView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetmagicview,"weather_widget_layout");
    	mweather_widget_time_apmp = (ImageView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetmagicview,"weather_widget_time_apmp");
    	mweather_widget_time_layout = (RelativeLayout)mapp.mLauncherContext.findViewByIdName(mweatherwidgetmagicview,"weather_widget_time_layout");
    	mweather_widget_other_day_layout = (LinearLayout)mapp.mLauncherContext.findViewByIdName(mweatherwidgetmagicview,"weather_widget_other_day_layout");
    	mweather_widget_animation_layout = (LinearLayout)mapp.mLauncherContext.findViewByIdName(mweatherwidgetmagicview,"weather_widget_animation_layout");
    	mweather_widget_day_details_layout = (LinearLayout)mapp.mLauncherContext.findViewByIdName(mweatherwidgetmagicview,"weather_widget_day_details_layout");
    	mweather_widget_time_bg  = (ImageView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetmagicview,"weather_widget_time_bg");
    	mweather_widget_other_info_switch= (ImageView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetmagicview,"weather_widget_other_info_switch");
    	mweather_widget_city_empty = (TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetmagicview,"weather_widget_city_empty");
    	mweather_widget_time_num_layout= (LinearLayout)mapp.mLauncherContext.findViewByIdName(mweatherwidgetmagicview,"weather_widget_time_num_layout");
    	if(mweather_widget_day_city==null)
    		initLocalLayout(context);
    	else if(mweather_widget_day_content==null)
    		initLocalLayout(context);
    	else if(mweather_widget_day_temp==null)
    		initLocalLayout(context);
    	else if(mweather_widget_time_num_00==null)
    		initLocalLayout(context);
    	else if(mweather_widget_time_num_01==null)
    		initLocalLayout(context);
    	else if(mweather_widget_time_num_02==null)
    		initLocalLayout(context);
    	else if(mweather_widget_time_num_03==null)
    		initLocalLayout(context);
    	else if(mweather_widget_time_num_10==null)
    		initLocalLayout(context);
    	else if(mweather_widget_time_num_12==null)
    		initLocalLayout(context);
    	else if(mweather_widget_time_num_13==null)
    		initLocalLayout(context);
    	else if(mweather_widget_time_num_11==null)
    		initLocalLayout(context);
    	else if(mweather_widget_time_num_colon==null)
    		initLocalLayout(context);
    	else if(mweather_widget_day_icon==null)
    		initLocalLayout(context);
    	else if(mweather_widget_day_date==null)
    		initLocalLayout(context);
    	else if(mweather_widget_day_week==null)
    		initLocalLayout(context);
    	else if(mweather_widget_layout==null)
    		initLocalLayout(context);
    	else if(mweather_widget_time_apmp==null)
    		initLocalLayout(context);
    	else if(mweather_widget_time_layout==null)
    		initLocalLayout(context);
    	else if(mweather_widget_other_day_layout==null)
    		initLocalLayout(context);
    	else if(mweather_widget_animation_layout==null)
    		initLocalLayout(context);
    	else if(mweather_widget_day_details_layout==null)
    		initLocalLayout(context);
    	else if(mweather_widget_time_bg==null)
    		initLocalLayout(context);
    	else if(mweather_widget_other_info_switch==null)
    		initLocalLayout(context);
    	else if(mweather_widget_city_empty==null)
    		initLocalLayout(context);
    	else if(mweather_widget_time_num_layout==null)
    		initLocalLayout(context);
	}
	private void initLocalLayout(final Context context)
	{
		mweatherwidgetmagicview = View.inflate(context, R.layout.weather_widget_magic_layout, null);
	    mweather_widget_day_city = (TextView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_day_city);
    	mweather_widget_day_content = (TextView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_day_content);
    	mweather_widget_day_temp = (TextView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_day_temp);
    	mweather_widget_time_num_00 = (ImageView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_time_num_00);
    	mweather_widget_time_num_01 = (ImageView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_time_num_01);
    	mweather_widget_time_num_02 = (ImageView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_time_num_02);
    	mweather_widget_time_num_03 = (ImageView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_time_num_03);
    	mweather_widget_time_num_10 = (ImageView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_time_num_10);
    	mweather_widget_time_num_11 = (ImageView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_time_num_11);
    	mweather_widget_time_num_12 = (ImageView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_time_num_12);
    	mweather_widget_time_num_13 = (ImageView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_time_num_13);	    	mweather_widget_time_num_colon = (ImageView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_time_num_colon);
    	mweather_widget_day_icon = (ImageView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_day_icon);
    	mweather_widget_day_date = (TextView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_day_date);
    	mweather_widget_day_week = (TextView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_day_week);
    	mweather_widget_layout = (ImageView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_layout);
    	mweather_widget_time_apmp = (ImageView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_time_apmp);
    	mweather_widget_time_layout = (RelativeLayout)mweatherwidgetmagicview.findViewById(R.id.weather_widget_time_layout);
    	mweather_widget_other_day_layout = (LinearLayout)mweatherwidgetmagicview.findViewById(R.id.weather_widget_other_day_layout);
    	mweather_widget_animation_layout = (LinearLayout)mweatherwidgetmagicview.findViewById(R.id.weather_widget_animation_layout);
    	mweather_widget_day_details_layout = (LinearLayout)mweatherwidgetmagicview.findViewById(R.id.weather_widget_day_details_layout);
    	mweather_widget_time_bg  = (ImageView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_time_bg);
    	mweather_widget_other_info_switch= (ImageView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_other_info_switch);
    	mweather_widget_city_empty = (TextView)mweatherwidgetmagicview.findViewById(R.id.weather_widget_city_empty);
    	mweather_widget_time_num_layout= (LinearLayout)mweatherwidgetmagicview.findViewById(R.id.weather_widget_time_num_layout);
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
					buildViewCache();
					break;
				case WeatherUtilites.MES_CHANGE_CITY_WEATHER:
					try{
						mweather_widget_other_day_layout.removeAllViews();
						mweather_widget_other_day_layout.addView(mweatherwidgetmagicYuBaoview,mlp);
			        	mweather_widget_other_info_switch.setImageDrawable(mapp.mLauncherContext.getDrawable(
								R.drawable.weather_widget_other_zhishu_switch));
						miscurrentother = false;
						dispalyCityName(mcontext);
						updateAppdata(context);
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
					dispalyCityName(mcontext);
					displayTime(mcontext);
					updateWeather(mcontext);
					buildViewCache();
					break;
				case WeatherUtilites.MES_LOCATION_CHANGE:
					dispalyCityName(mcontext);
	                clearWeatherInfo();
					updatelayout();
					buildViewCache();
					break;

				case WeatherUtilites.MES_WEATHER_WIDGET_INIT:
					setDefaultWeatherBG();
					dispalyCityName(mcontext);
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
				case WeatherUtilites.MES_WEATHER_CHANGE_EXWIDGET:
					mweather_widget_other_day_layout.removeAllViews();
					LenovoExWigetInfo lw = (LenovoExWigetInfo)msg.obj;
	            	if(YUBAOMAGICVIEW.equals(lw.widgetView_name)){
	                	mweather_widget_other_day_layout.addView(mweatherwidgetmagicYuBaoview,mlp);
	                	mweather_widget_other_info_switch.setImageDrawable(mapp.mLauncherContext.getDrawable(
	    						R.drawable.weather_widget_other_zhishu_switch));
	            	}else if(ZHISHUMAGICVIEW.equals(lw.widgetView_name)){ 
	                	mweather_widget_other_day_layout.addView(mweatherwidgetzhishuiview,mlp);
	                	mweather_widget_other_info_switch.setImageDrawable(mapp.mLauncherContext.getDrawable(
	    						R.drawable.weather_widget_other_weather_switch));
	            	}
	            	else{
	            		View view = (View)WeatherUtilites.getLeosWidgetViewToWorkspace(lw.activity_name,lw.widgetView_name,mcontext);
	            		if(view!=null)
	            			mweather_widget_other_day_layout.addView(view,mlp);
	            		else
	            			mweather_widget_other_day_layout.addView(mweatherwidgetmagicYuBaoview,mlp);
	            	}
	            	buildViewCache();
					break;
				case WeatherUtilites.MES_WEATHER_ANIMATE_STOP:
//					displayTime(mcontext);
					if(WeatherUtilites.isInDefaultTheme(context) && !mweather_widget_details_pic.getwindowstate()){
						mweather_widget_details_pic.hideview();
					}
						
/*					if(manimview!=null){
		            	mweather_widget_animation_layout.setVisibility(View.GONE);
		            	mweather_widget_animation_layout.removeAllViews();
//		            	manimview = null;
					}*/
					break;
				case WeatherUtilites.MES_WEATHER_ANIMATE_START:
					/*manimview = (View)WeatherUtilites.getLeosWidgetViewToWorkspace("com.lenovo.leos.weatheranimation",
	        				"com.lenovo.leos.weatheranimation.view.WeatherView",mcontext);
	        		if(manimview!=null){
	        			mweather_widget_animation_layout.addView(manimview,mlp);
	        			mweather_widget_animation_layout.setVisibility(View.VISIBLE);
	        		}*/
					break;
				case WeatherUtilites.MES_UPDATE_WEATHER_DETAILS:
					List<WeatherApp> weatherapps = (List<WeatherApp>)msg.obj;
					updateWeatherByApp(mcontext,weatherapps);
					break;
				case WeatherUtilites.MES_UPDATE_WEATHER_ZHISHU_DETAILS:
					WeatheDetailsApp weathedetailsapps =(WeatheDetailsApp)msg.obj;
					updateWeatherzhishuByApp(weathedetailsapps);
					break;
				case WeatherUtilites.MES_UPDATE_WEATHER_CITYNAME:
					String cityname = (String)msg.obj;
					if(cityname!=null&&!TextUtils.isEmpty(cityname)){
						mweather_widget_city_empty.setVisibility(View.GONE);
				    	mweather_widget_day_city.setTextColor(
									mapp.mLauncherContext.getColor(R.color.weather_magic_widget_cityname_color,
											R.color.weather_magic_widget_cityname_color));
				    	mweather_widget_day_city.setText(cityname);
					}else{
						mweather_widget_other_day_layout.removeAllViews();
						mweather_widget_other_day_layout.addView(mweatherwidgetmagicYuBaoview,mlp);
			        	mweather_widget_other_info_switch.setImageDrawable(mapp.mLauncherContext.getDrawable(
								R.drawable.weather_widget_other_zhishu_switch));
						miscurrentother = false;
						mweather_widget_day_city.setText("");
						mweather_widget_city_empty.setVisibility(View.VISIBLE);
					}
					break;
				case WeatherUtilites.MES_UPDATE_SERVICE_WEATHER_DETAILS:
					List<WeatherDetails> weatherdetails  =(List<WeatherDetails>)msg.obj;
					updateWeatherBySoure(context,weatherdetails);
					break;
				case WeatherUtilites.MES_UPDATE_LOCAL_WEATHER_CITYNAME:
					cityname = (String)msg.obj;
					updatelocalcityname(cityname);
					break;
				case WeatherUtilites.MES_UPDATE_TIME:
					updateTime(mcontext);
					WVGAupdateDateWeek(mcontext);
					if(mweather_widget_day_icon.getTag()!=null){
		        		if(mweather_widget_day_icon.getTag().toString().contains("d") && IsDaytime() ||
		        				mweather_widget_day_icon.getTag().toString().contains("n") && !IsDaytime()){
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
    private void updatelocalcityname(String cityname)
    {
    	mweather_widget_day_city.setBackgroundColor(0x00000000);
		if(cityname!=null&&!TextUtils.isEmpty(cityname)){
			mweather_widget_city_empty.setVisibility(View.GONE);
	    	mweather_widget_day_city.setTextColor(
						mapp.mLauncherContext.getColor(R.color.weather_magic_widget_cityname_color,
								R.color.weather_magic_widget_cityname_color));
	    	mweather_widget_day_city.setText(cityname);
	    	cityname = WeatherUtilites.getCityName(mcontext, 1);
	    	Log.i("czz", "settings save cityname= "+cityname);
	    	Settings.System.putString(mcontext.getContentResolver(), "city_name", cityname);
		}else{
			Settings.System.putString(mcontext.getContentResolver(), "city_name", "");
			mweather_widget_day_city.setText("");
			mweather_widget_city_empty.setVisibility(View.VISIBLE);
		}
    }
    private void updatelayout()
    {
    	View parentview = (View) mweatherwidgetmagicview.getParent();
    	mweatherwidgetmagicview.requestLayout();
    	if(parentview!=null)
    		parentview.requestLayout();
    }

	public void dispalyCityName(final Context context)
    {
		String cityname = null ;
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
					}
					R2.echo("dispalyCityName tcityname = "+tcityname);
					Message message = mHandler.obtainMessage(WeatherUtilites.MES_UPDATE_WEATHER_CITYNAME,tcityname);  
					mHandler.sendMessage(message);
				}
			}.start();
/*			if(cityname==null||TextUtils.isEmpty(cityname))
				mweather_widget_day_city.setBackgroundColor(0x00000000);
			else
				mweather_widget_day_city.setBackgroundDrawable(mapp.mLauncherContext.getDrawable(R.drawable.weather_widgt_cityname));;
*/		}
		R2.echo("dispalyCityName cityname = "+cityname);
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
    	mweather_widget_layout.setBackgroundDrawable(mapp.mLauncherContext.getDrawable(R.drawable.weather_widget_bg));
    	mweatherwidgetmagicYuBaoview.mweather_widget_other_day_line.setImageDrawable(mapp.mLauncherContext.getDrawable(R.drawable.weather_widget_line));
    	mweatherwidgetzhishuiview.mweather_widget_zhishu_line.setImageDrawable(mapp.mLauncherContext.getDrawable(R.drawable.weather_widget_line));
    }
    boolean IsDaytime() {
        GregorianCalendar gcal = new GregorianCalendar();
        int hour = gcal.get(Calendar.HOUR_OF_DAY);
        return (hour >= 6 && hour < 18);
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
    private void updateTime(Context context){
		dis12or24=false;
		dis12or24=setTimeFormat(context);
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		final int AmPm = GetAMPM(context,calendar);
		final String curenttime = DateFormat.format(mTimeFormat, calendar).toString();
		R2.echo("displayTime curenttime = "+curenttime);

		if(AmPm==0)
			mweather_widget_time_apmp.setVisibility(View.GONE);
		else{
			mweather_widget_time_apmp.setVisibility(View.VISIBLE);
			if(AmPm==1)
				mweather_widget_time_apmp.setImageDrawable(mapp.mLauncherContext.getDrawable(
						R.drawable.weather_widget_time_am));
			else
				mweather_widget_time_apmp.setImageDrawable(mapp.mLauncherContext.getDrawable(
						R.drawable.weather_widget_time_pm));
		}
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
			mweather_widget_time_num_00.setImageDrawable(
					mapp.mLauncherContext.getDrawable(WeatherUtilites.NUMBER[currentnum]));
			currentnum = hour%10;  
			mweather_widget_time_num_01.setImageDrawable(
					mapp.mLauncherContext.getDrawable(WeatherUtilites.NUMBER[currentnum]));
			int minute = calendar.get(Calendar.MINUTE);
			currentnum = minute/10; 
			mweather_widget_time_num_02.setImageDrawable(
					mapp.mLauncherContext.getDrawable(WeatherUtilites.NUMBER[currentnum]));
			currentnum = minute%10; 
			mweather_widget_time_num_03.setImageDrawable(
					mapp.mLauncherContext.getDrawable(WeatherUtilites.NUMBER[currentnum]));
		}catch(ArrayIndexOutOfBoundsException ex){

			ex.printStackTrace();

		}
		
		/*int currentnum = curenttime.charAt(0) - '0';
		mweather_widget_time_num_00.setImageDrawable(
				mapp.mLauncherContext.getDrawable(WeatherUtilites.NUMBER[currentnum]));
		int currentnum1 = curenttime.charAt(1) - '0';  
		mweather_widget_time_num_01.setImageDrawable(
				mapp.mLauncherContext.getDrawable(WeatherUtilites.NUMBER[currentnum1]));
		int currentnum2 = curenttime.charAt(3) - '0'; 
		mweather_widget_time_num_02.setImageDrawable(
				mapp.mLauncherContext.getDrawable(WeatherUtilites.NUMBER[currentnum2]));
		int currentnum3 = curenttime.charAt(4) - '0'; 
		mweather_widget_time_num_03.setImageDrawable(
				mapp.mLauncherContext.getDrawable(WeatherUtilites.NUMBER[currentnum3]));*/
/*		if(!mcurrentnum.equals(curenttime)){
			int oldcurrentnum = mcurrentnum.charAt(0) - '0';
			mweather_widget_time_num_10.setBackgroundDrawable(
					mapp.mLauncherContext.getDrawable(WeatherUtilites.MAGIC_NUMBER[oldcurrentnum]));
			if(oldcurrentnum!=currentnum){
		    	mweather_widget_time_num_10.setVisibility(View.VISIBLE);
		      	mweather_widget_time_num_00.startAnimation(mat00);
		      	mweather_widget_time_num_10.startAnimation(mat10);
			}
			oldcurrentnum = mcurrentnum.charAt(1) - '0';  
			mweather_widget_time_num_11.setBackgroundDrawable(
					mapp.mLauncherContext.getDrawable(WeatherUtilites.MAGIC_NUMBER[oldcurrentnum]));
			if(oldcurrentnum!=currentnum1){
		    	mweather_widget_time_num_11.setVisibility(View.VISIBLE);
		      	mweather_widget_time_num_01.startAnimation(mat01);
		      	mweather_widget_time_num_11.startAnimation(mat11);
			}
			oldcurrentnum = mcurrentnum.charAt(3) - '0'; 
			mweather_widget_time_num_12.setBackgroundDrawable(
					mapp.mLauncherContext.getDrawable(WeatherUtilites.MAGIC_NUMBER[oldcurrentnum]));
			if(oldcurrentnum!=currentnum2){
		    	mweather_widget_time_num_12.setVisibility(View.VISIBLE);
		      	mweather_widget_time_num_02.startAnimation(mat02);
		      	mweather_widget_time_num_12.startAnimation(mat12);
			}
			oldcurrentnum = mcurrentnum.charAt(4) - '0'; 
			mweather_widget_time_num_13.setBackgroundDrawable(
					mapp.mLauncherContext.getDrawable(WeatherUtilites.MAGIC_NUMBER[oldcurrentnum]));
			if(oldcurrentnum!=currentnum3){
		    	mweather_widget_time_num_13.setVisibility(View.VISIBLE);
		      	mweather_widget_time_num_03.startAnimation(mat03);
		      	mweather_widget_time_num_13.startAnimation(mat13);
			}
	      	mcurrentnum = curenttime;
		}*/
		mweather_widget_time_num_colon.setImageDrawable(mapp.mLauncherContext.getDrawable(
				R.drawable.weather_widget_colon));
    	mweather_widget_time_bg.setBackgroundDrawable(mapp.mLauncherContext.getDrawable(R.drawable.weather_widget_time_bg));
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
	private int GetAMPM(final Context context,Calendar c)
	{
		if(!dis12or24){
	        if(c.get(Calendar.AM_PM)==0){
	            return  1;
	        }
	        else{
	            return  2;
	        }
		}
		else 
			return 0;
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
    private String getDateStr(Date now, String language,Context context) {
    	Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
    	calendar.setTime(now);
    	return DateUtils.getDayOfWeekString(
    			calendar.get(Calendar.DAY_OF_WEEK), DateUtils.LENGTH_MEDIUM);
/*        String dateFormat = Settings.System.getString(context.getContentResolver(), "date_format");
        if (language.equals("zh")) {
        	final int flagsDate = DateUtils.FORMAT_SHOW_DATE;
            return (String)DateUtils.formatDateTime(context, now.getTime(), flagsDate);
        } else {
            return DateFormat.format(DEFALUT_DATE_FORMAT_WVGA_EN, now).toString();
        }*/
    }
    private void WVGAupdateDateWeek(Context context){
    	Configuration configuration = mres.getConfiguration();
    	String language = configuration.locale.getLanguage();
    	Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
    	String dayString = DateUtils.getDayOfWeekString(
    			calendar.get(Calendar.DAY_OF_WEEK), DateUtils.LENGTH_MEDIUM);
    	final String dateString = getDateStr(calendar,language,context);
    	final int flagsWeek = DateUtils.FORMAT_SHOW_WEEKDAY; 
        String weekStr = (String)DateUtils.formatDateTime(context, 
    			System.currentTimeMillis(), flagsWeek);

    	mweather_widget_day_date.setTextColor(
				mapp.mLauncherContext.getColor(R.color.weather_magic_widget_date_color,
						R.color.weather_magic_widget_date_color));
    	mweather_widget_day_week.setTextColor(
				mapp.mLauncherContext.getColor(R.color.weather_magic_widget_week_color,
						R.color.weather_magic_widget_week_color));
    	if(dateString.contains(dayString))
    		mweather_widget_day_week.setVisibility(View.GONE);
    	else
    		mweather_widget_day_week.setVisibility(View.VISIBLE);
    	mweather_widget_day_week.setText(" "+dayString);
	    mweather_widget_day_date.setText(dateString);
	    
	    mweatherwidgetmagicYuBaoview.mweather_widget_other0_day_date.setTextColor(
				mapp.mLauncherContext.getColor(R.color.weather_magic_widget_other_week_color,
						R.color.weather_magic_widget_other_week_color));
	    mweatherwidgetmagicYuBaoview.mweather_widget_other1_day_date.setTextColor(
				mapp.mLauncherContext.getColor(R.color.weather_magic_widget_other_week_color,
						R.color.weather_magic_widget_other_week_color));
    	Date now = calendar.getTime();
    	now.setDate(now.getDate()+1);
    	mweatherwidgetmagicYuBaoview.mweather_widget_other0_day_date.setText(getDateStr(now,language,context));
    	now.setDate(now.getDate()+1);
    	mweatherwidgetmagicYuBaoview.mweather_widget_other1_day_date.setText(getDateStr(now,language,context));
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
		eventFilter.addAction(WeatherUtilites.ACTION_WEATHER_WIDGET_CHANGECITY_FROMSINA);
		eventFilter.addAction(WeatherUtilites.ACTION_WEATHER_WIDGET_ADDCITY_FROMSINA);
		eventFilter.addAction(WeatherUtilites.ACTION_WEATHER_WIDGET_DELETECITY_FROMSINA);
		eventFilter.addAction(WeatherUtilites.ACTION_WEATHER_WIDGET_UPDATECITY_FROMSINA);
		eventFilter.addAction(WeatherUtilites.ACTION_WEATHER_WIDGET_CHANGE_EXWIDGET);
		eventFilter.addAction(WeatherUtilites.ACTION_WEATHER_ANIMATE_STOP);
/*		eventFilter.addAction(WeatherUtilites.ACTION_WEATHER_ANIMATE_START);*/
//		eventFilter.addAction(WeatherUtilites.ACTION_WEATHER_WEATHERANIMATION_STOP);
		eventFilter.addAction(WeatherUtilites.ACTION_WEATHER_WIDGET_PACKAGE_UPDATE);
/*		eventFilter.addAction("android.intent.action.PACKAGE_ADDED");
		eventFilter.addAction("android.intent.action.PACKAGE_REMOVED");
		eventFilter.addDataScheme("package");*/
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
				 R2.echo("!widget hasInstances");
			    	try {
			    		if(mIntentReceiver!=null) 
			    			context.unregisterReceiver(mIntentReceiver);
			    	} catch (IllegalArgumentException e) { 
			    		e.printStackTrace();
			    	}
				return;
			}
            String action = intent.getAction();
            R2.echo("!!!!Widget *********recievied " + action);
            if (action.equals(WeatherUtilites.ACTION_WEATHER_WIDGET_TIME_TICK))
                mHandler.sendEmptyMessage(WeatherUtilites.MES_TIME_TICK);
            else if (action.equals(WeatherUtilites.ACTION_LOCATION_CHANGE)) {
                R2.echo("Location Changed");
                mHandler.sendEmptyMessage(WeatherUtilites.MES_LOCATION_CHANGE);
            }else if (action.equals(WeatherUtilites.ACTION_WEATHER_NETWORK_STATE_CHANGE)) {
            	boolean mNetworkEnabled = SettingsValue.isNetworkEnabled(context);
                R2.echo("magic Network is open ="+mNetworkEnabled);
                mHandler.sendEmptyMessage(WeatherUtilites.MES_WEATHER_NETWORK_STATE_CHANGE);
            }else if(action.equals(Intent.ACTION_SCREEN_ON)){
            	mHandler.sendEmptyMessage(WeatherUtilites.MES_ACTION_SCREEN_ON);
            }else if(action.equals(WeatherUtilites.ACTION_UPDATE_WEATHER)){
            	mHandler.sendEmptyMessage(WeatherUtilites.MES_UPDATE_WEATHER);
            }else if(WeatherUtilites.ACTION_TIEM_CHANGE.equals(action)){
            	mHandler.sendEmptyMessage(WeatherUtilites.MES_TIEM_CHANGE);
            }else if(ACTION_CHANGE_LETHEME.equals(action)){
            	initlayoutWVGA(context);
            	mHandler.sendEmptyMessage(WeatherUtilites.MES_WEATHER_WIDGET_INIT);
            }else if(WeatherUtilites.ACTION_WEATHER_WIDGET_CHANGECITY_FROMSINA.equals(action)||
            		WeatherUtilites.ACTION_WEATHER_WIDGET_ADDCITY_FROMSINA.equals(action)||
            		WeatherUtilites.ACTION_WEATHER_WIDGET_DELETECITY_FROMSINA.equals(action)||
            		WeatherUtilites.ACTION_WEATHER_WIDGET_UPDATECITY_FROMSINA.equals(action)){
            	mHandler.sendEmptyMessage(WeatherUtilites.MES_CHANGE_CITY_WEATHER);
            }else if(WeatherUtilites.ACTION_WEATHER_WIDGET_CHANGE_EXWIDGET.equals(action)){
        		final String className = intent.getStringExtra(WeatherUtilites.EXTRA_WEATHER_EXWIDGET_CLASSNAME);
        		final String packagename = intent.getStringExtra(WeatherUtilites.EXTRA_WEATHER_EXWIDGET_PACKAGENAME);
        		LenovoExWigetInfo lwi = new LenovoExWigetInfo();
        		lwi.activity_name = packagename;
            	lwi.widgetView_name = className;
            	Message msg = mHandler.obtainMessage(WeatherUtilites.MES_WEATHER_CHANGE_EXWIDGET, lwi);
            	mHandler.sendMessage(msg);
            }else if(action.equals(WeatherUtilites.ACTION_WEATHER_ANIMATE_START)){
            	mHandler.sendEmptyMessage(WeatherUtilites.MES_WEATHER_ANIMATE_START);
            }else if(action.equals(WeatherUtilites.ACTION_WEATHER_ANIMATE_STOP)){
            	mHandler.sendEmptyMessage(WeatherUtilites.MES_WEATHER_ANIMATE_STOP);
            }else if(action.equals(WeatherUtilites.ACTION_WEATHER_WEATHERANIMATION_STOP)){
            	mHandler.sendEmptyMessage(WeatherUtilites.MES_WEATHER_ANIMATE_STOP);
            }else if(WeatherUtilites.ACTION_WEATHER_WIDGET_PACKAGE_UPDATE.equals(action)){
            	final String packageName = intent.getStringExtra("packageName");
            	if(packageName!=null&&packageName.contains(WeatherUtilites.SINA_PACKAGENAME)){
            		mHandler.sendEmptyMessage(WeatherUtilites.MES_WEATHER_WIDGET_INIT);
            	}
            }
		}
    }

	public boolean isWeatherOutOfDate24(final Context context) {
	    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
	    final int currenttime = (int)(calendar.getTimeInMillis()/3600000);
	    int oldcurrenttime = 0;
	    if(WeatherUtilites.findForPackage(context,WeatherUtilites.SINA_PACKAGENAME)){
	    	final String time = WeatherUtilites.getWeatherDetailsAppUpdatetime(context);
	    	if(time!=null&&!TextUtils.isEmpty(time)){
		    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		    	try {
					Date date = formatter.parse(time);
					oldcurrenttime = (int)(date.getTime()/3600000);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	    }
	    else
	    	oldcurrenttime = (int)(WeatherUtilites.getupdatetime(context)/3600000);
	    if (Math.abs(oldcurrenttime-currenttime )>=24) {
	        R2.echo("isWeatherOutOfDate24() return true");
	        return true;
	    } else {
	        R2.echo("isWeatherOutOfDate24() return false");
	        return false;
	    }
	}
	private void updateWeatherBySoure(final Context context,List<WeatherDetails> weatherdetails )
	{
    	if(weatherdetails!=null&&weatherdetails.size()>0){
    		Configuration configuration = mres.getConfiguration();
    		String language = configuration.locale.toString();
    		R2.echo("weatherdetails.size()=");
    		for(int i=0;i<4&&i<weatherdetails.size();i++){
        		WeatherDetails weatherdetail =null;
        		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        		Date now = calendar.getTime();
        		now.setDate(now.getDate()+i);
    			final String data = DateFormat.format(DEFALUT_DATE_FORMAT_CN, now).toString();
    			
    			for(int j=0;j<weatherdetails.size();j++){
    				R2.echo(data+"  weatherdetails.get("+j+").mcityDate="+weatherdetails.get(i).mcityDate);
        			if(data.equals(weatherdetails.get(j).mcityDate)){
        				weatherdetail = weatherdetails.get(j);
        				break;
        			}
        		}
    			if(weatherdetail==null){
        			if(i==0){
        		    	setDefaultWeatherPic();
        		    	setDefaultWeatherZhishu();
        			}else if(i==1){
        				setDefaultWeatherPic0();
        			}else if(i==2){
        				setDefaultWeatherPic1();
        			}else if(i==3){
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
                if(i==0){
	                mweather_widget_day_content.setText(content);
	                mweather_widget_day_content.setTextColor(
		    				mapp.mLauncherContext.getColor(R.color.weather_magic_widget_content_color,
		    						R.color.weather_magic_widget_content_color));
	                mweather_widget_day_temp.setText(weatherdetail.mcityTemperature);
	                mweather_widget_day_temp.setTextColor(
		    				mapp.mLauncherContext.getColor(R.color.weather_magic_widget_temp_color,
		    						R.color.weather_magic_widget_temp_color));
//                    Settings.System.putString(mcontext.getContentResolver(), WeatherUtilites.CONDITION,content);
//                    Settings.System.putString(mcontext.getContentResolver(), WeatherUtilites.WEATHERTEMPS,weatherdetail.mcityTemperature);

	                if(iconimage==null ||TextUtils.isEmpty(iconimage)){
	            		mweather_widget_day_temp.setText("");
	            		mweather_widget_day_content.setText("");
	                	mweather_widget_day_icon.setImageDrawable(
	            				mapp.mLauncherContext.getDrawable(R.drawable.weather_widget_big_default));
//	                    Settings.System.putString(mcontext.getContentResolver(), WeatherUtilites.CONDITION,"");
//	                    Settings.System.putString(mcontext.getContentResolver(), WeatherUtilites.WEATHERTEMPS, "");
	                }else{
		                if (!IsDaytime()) {
		                	if(iconimage.contains("d"))
		                		iconimage = iconimage.replace("d", "n");
		                }else{
		                	if(iconimage.contains("n"))
		                		iconimage =iconimage.replace("n", "d");
		                }
		                int iconid = -1;
		    	        try {
			        		iconid = WeatherUtilites.ICON_K5_BIG_MAP.get(iconimage);
			        		if(iconid==-1)
			                	setDefaultWeatherPic();
			                else{
			                	mweather_widget_day_icon.setImageDrawable(
		            					mapp.mLauncherContext.getDrawable(iconid));
			                	mweather_widget_day_icon.setTag(iconimage);
			                	WeatherUtilites.saveWeatherIconUpdate(context,data,iconimage,true);
			            	}
		                }catch(Exception e){
		                	e.printStackTrace();
		                	iconid = -1;
		                	setDefaultWeatherPic();
		                }
	                }
	                if(WeatherUtilites.isInDefaultTheme(context)){
	                	try{
	                		mweather_widget_details_content_city.setText(mweather_widget_day_city.getText()+" "+content);
		                	mweather_widget_details_content_temp.setText(weatherdetail.mcityTemperature);
		                	mweather_widget_details_content_wind.setText((weatherdetail.mcityDirection1==null?"风力":(weatherdetail.mcityDirection1+"风"))+weatherdetail.mcityPower);
		                	if(weatherdetail.mcityChyL==null&&TextUtils.isEmpty(weatherdetail.mcityChyL)){
		                		weatherdetail.mcityChyL="";
		                	}
		                	mweather_widget_details_content_index.setText(" 穿衣: "+weatherdetail.mcityChyL);
		                	Log.d("weather", "穿衣：="+weatherdetail.mcityChyL);
		                   // mweather_widget_details_content_index.setCompoundDrawablesWithIntrinsicBounds(R.drawable.weather_cloths, 0, 0, 0);
		                    
		                    if(weatherdetail.mcityZwxL==null&&TextUtils.isEmpty(weatherdetail.mcityZwxL)){
		                		weatherdetail.mcityZwxL="";
			                }
		                    mweather_widget_details_content_index_uv.setText(" 紫外线: "+weatherdetail.mcityZwxL);
		                    Log.d("weather", "紫外线：="+weatherdetail.mcityZwxL);
		                   // mweather_widget_details_content_index_uv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.weather_ultraviolet_ray, 0, 0, 0);

		                	if(weatherdetail.mcityPollutionL==null&&TextUtils.isEmpty(weatherdetail.mcityPollutionL)){
		                		weatherdetail.mcityPollutionL = "";
		                 	}
		               		mweather_widget_details_content_index_xc.setText(" 污染: "+weatherdetail.mcityPollutionL);
		               	 Log.d("weather", "污染：="+weatherdetail.mcityPollutionL);
		            		//mweather_widget_details_content_index_xc.setCompoundDrawablesWithIntrinsicBounds(R.drawable.weather_pollution, 0, 0, 0);

		                	if(weatherdetail.mcityKtkL==null&&TextUtils.isEmpty(weatherdetail.mcityKtkL)){
		                		weatherdetail.mcityKtkL ="";
		                	}
		            		mweather_widget_details_content_index_tr.setText(" 空调: "+weatherdetail.mcityKtkL);
		            		Log.d("weather", "空调：="+weatherdetail.mcityKtkL);
		            		//mweather_widget_details_content_index_tr.setCompoundDrawablesWithIntrinsicBounds(R.drawable.weather_ktk, 0, 0, 0);
	            		}catch(Exception e){
	            			e.printStackTrace();
	            		}
	                }
	                context.sendBroadcast(new Intent("com.lenovo.leos.widgets.weather.update_weather"));
                }else if(i==1){
/*                	mweatherwidgetmagicYuBaoview.mweather_widget_other0_day_content.setText(content);
                	mweatherwidgetmagicYuBaoview.mweather_widget_other0_day_content.setTextColor(
		    				mapp.mLauncherContext.getColor(R.color.weather_magic_widget_other_content_color,
		    						R.color.weather_magic_widget_other_content_color));
*/                	mweatherwidgetmagicYuBaoview.mweather_widget_other0_day_temp.setText(weatherdetail.mcityTemperature);
                	mweatherwidgetmagicYuBaoview.mweather_widget_other0_day_temp.setTextColor(
		    				mapp.mLauncherContext.getColor(R.color.weather_magic_widget_other_temp_color,
		    						R.color.weather_magic_widget_other_temp_color));
                	if(iconimage==null ||TextUtils.isEmpty(iconimage)){
                		mweatherwidgetmagicYuBaoview.mweather_widget_other0_day_temp.setText("");
//                		mweatherwidgetmagicYuBaoview.mweather_widget_other0_day_content.setText("");
                		mweatherwidgetmagicYuBaoview.mweather_widget_other0_day_icon.setImageDrawable(
		            				mapp.mLauncherContext.getDrawable(R.drawable.weather_widget_small_default));
		            }else{
		                if (!IsDaytime()) {
		                	if(iconimage.contains("d"))
		                		iconimage = iconimage.replace("d", "n");
		                }else{
		                	if(iconimage.contains("n"))
		                		iconimage = iconimage.replace("n", "d");
		                }
		                int iconid = -1;
		    	        try{
			        		iconid = WeatherUtilites.ICON_K5_SMALL_MAP.get(iconimage);
			        		if(iconid==-1)
			                	setDefaultWeatherPic0();
			                else{
			                	mweatherwidgetmagicYuBaoview.mweather_widget_other0_day_icon.setImageDrawable(
		            					mapp.mLauncherContext.getDrawable(iconid));
			            	}
			            }catch(Exception e){
			                	e.printStackTrace();
			                	iconid = -1;
			                	setDefaultWeatherPic0();

			            }
	                }
                }else if(i==2){
/*                	mweatherwidgetmagicYuBaoview.mweather_widget_other1_day_content.setText(content);
                	mweatherwidgetmagicYuBaoview.mweather_widget_other1_day_content.setTextColor(
		    				mapp.mLauncherContext.getColor(R.color.weather_magic_widget_other_content_color,
		    						R.color.weather_magic_widget_other_content_color));*/
                	mweatherwidgetmagicYuBaoview.mweather_widget_other1_day_temp.setText(weatherdetail.mcityTemperature);
                	mweatherwidgetmagicYuBaoview.mweather_widget_other1_day_temp.setTextColor(
    				mapp.mLauncherContext.getColor(R.color.weather_magic_widget_other_temp_color,
    						R.color.weather_magic_widget_other_temp_color));
                	if(iconimage==null ||TextUtils.isEmpty(iconimage)){
                		mweatherwidgetmagicYuBaoview.mweather_widget_other1_day_temp.setText("");
//                		mweatherwidgetmagicYuBaoview.mweather_widget_other1_day_content.setText("");
                		mweatherwidgetmagicYuBaoview.mweather_widget_other1_day_icon.setImageDrawable(
		            				mapp.mLauncherContext.getDrawable(R.drawable.weather_widget_small_default));
		            }else{
		                if (!IsDaytime()) {
		                	if(iconimage.contains("d"))
		                		iconimage = iconimage.replace("d", "n");
		                }else{
		                	if(iconimage.contains("n"))
		                		iconimage= iconimage.replace("n", "d");
		                }
		                int iconid = -1;
		    	        try {
			        		iconid = WeatherUtilites.ICON_K5_SMALL_MAP.get(iconimage);
			        		if(iconid==-1)
			                	setDefaultWeatherPic1();
			                else{
			                	mweatherwidgetmagicYuBaoview.mweather_widget_other1_day_icon.setImageDrawable(
		            					mapp.mLauncherContext.getDrawable(iconid));
			            	}
		                }catch(Exception e){
		                	e.printStackTrace();
		                	iconid = -1;
		                	setDefaultWeatherPic1();
		                }
	                }
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
    		if(weatherdetails.size()<4 && weatherdetails.size()>0){
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
    		clearWeatherInfo();
    	}
	}
	private void updateweatherdata(final Context context)
	{
		List<WeatherApp> weatherapps  = WeatherUtilites.getWeatherApp(context);
		Message message = mHandler.obtainMessage(WeatherUtilites.MES_UPDATE_WEATHER_DETAILS, weatherapps);  
		mHandler.sendMessage(message);
	}
	private void updateweatherzhishudata(final Context context)
	{
		WeatheDetailsApp weathedetailsapps  = WeatherUtilites.getWeatherDetailsApp(context);
		Message message = mHandler.obtainMessage(WeatherUtilites.MES_UPDATE_WEATHER_ZHISHU_DETAILS, weathedetailsapps);  
		mHandler.sendMessage(message);
	}
	private void updateWeatherzhishuByApp(WeatheDetailsApp weathedetailsapps)
	{
		if(weathedetailsapps!=null){
			mweatherwidgetzhishuiview.mweather_widget_cloth.setTextColor(
    				mapp.mLauncherContext.getColor(R.color.weather_magic_widget_zhishu_color,
    						R.color.weather_magic_widget_zhishu_color));
			mweatherwidgetzhishuiview.mweather_widget_ganmao.setTextColor(
    				mapp.mLauncherContext.getColor(R.color.weather_magic_widget_zhishu_color,
    						R.color.weather_magic_widget_zhishu_color));
			mweatherwidgetzhishuiview.mweather_widget_sport.setTextColor(
    				mapp.mLauncherContext.getColor(R.color.weather_magic_widget_zhishu_color,
    						R.color.weather_magic_widget_zhishu_color));
			mweatherwidgetzhishuiview.mweather_widget_shushi.setTextColor(
    				mapp.mLauncherContext.getColor(R.color.weather_magic_widget_zhishu_color,
    						R.color.weather_magic_widget_zhishu_color));
			mweatherwidgetzhishuiview.mweather_widget_zwx.setTextColor(
    				mapp.mLauncherContext.getColor(R.color.weather_magic_widget_zhishu_color,
    						R.color.weather_magic_widget_zhishu_color));
			mweatherwidgetzhishuiview.mweather_widget_xiche.setTextColor(
    				mapp.mLauncherContext.getColor(R.color.weather_magic_widget_zhishu_color,
    						R.color.weather_magic_widget_zhishu_color));
			if(weathedetailsapps.mcityCloth==null||TextUtils.isEmpty(weathedetailsapps.mcityCloth))
				weathedetailsapps.mcityCloth = "";
			mweatherwidgetzhishuiview.mweather_widget_cloth.setText(
					mcontext.getString(R.string.weather_widget_zhishu_cloth)+" "+weathedetailsapps.mcityCloth);
			if(weathedetailsapps.mcityCold==null||TextUtils.isEmpty(weathedetailsapps.mcityCold))
				weathedetailsapps.mcityCold = "";
			mweatherwidgetzhishuiview.mweather_widget_ganmao.setText(
					mcontext.getString(R.string.weather_widget_zhishu_ganmao)+" "+weathedetailsapps.mcityCold);
			if(weathedetailsapps.mcityComfort==null||TextUtils.isEmpty(weathedetailsapps.mcityComfort))
				weathedetailsapps.mcityComfort = "";
			mweatherwidgetzhishuiview.mweather_widget_shushi.setText(
					mcontext.getString(R.string.weather_widget_zhishu_shushi)+" "+weathedetailsapps.mcityComfort);
			if(weathedetailsapps.mcityUV==null||TextUtils.isEmpty(weathedetailsapps.mcityUV))
				weathedetailsapps.mcityUV = "";
			mweatherwidgetzhishuiview.mweather_widget_zwx.setText(
					mcontext.getString(R.string.weather_widget_zhishu_zwx)+" "+weathedetailsapps.mcityUV);
			if(weathedetailsapps.mcitySport==null||TextUtils.isEmpty(weathedetailsapps.mcitySport))
				weathedetailsapps.mcitySport = "";
			mweatherwidgetzhishuiview.mweather_widget_sport.setText(
					mcontext.getString(R.string.weather_widget_zhishu_sport)+" "+weathedetailsapps.mcitySport);
			if(weathedetailsapps.mcityCwash==null||TextUtils.isEmpty(weathedetailsapps.mcityCwash))
				weathedetailsapps.mcityCwash = "";
			mweatherwidgetzhishuiview.mweather_widget_xiche.setText(
					mcontext.getString(R.string.weather_widget_zhishu_xiche)+" "+weathedetailsapps.mcityCwash);
			
			/*try{
				if(WeatherUtilites.isInDefaultTheme(mcontext)){
					mweather_widget_details_content_index.setText(" 穿衣: "+weathedetailsapps.mcityCloth);
//					mweather_widget_details_content_index.setCompoundDrawablesWithIntrinsicBounds(R.drawable.weather_cloths, 0, 0, 0);
				}
				if(WeatherUtilites.isInDefaultTheme(mcontext)){
					mweather_widget_details_content_index_tr.setText(" 体感: "+weathedetailsapps.mcityCold);
					mweather_widget_details_content_index_tr.setCompoundDrawablesWithIntrinsicBounds(R.drawable.weather_comfort, 0, 0, 0);
				}
				if(WeatherUtilites.isInDefaultTheme(mcontext)){
					mweather_widget_details_content_index_uv.setText(" 紫外线: "+weathedetailsapps.mcityUV);
					mweather_widget_details_content_index_uv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.weather_ultraviolet_ray, 0, 0, 0);
				}
				if(WeatherUtilites.isInDefaultTheme(mcontext)){
					mweather_widget_details_content_index_xc.setText(" 洗车: "+weathedetailsapps.mcityCwash);
					mweather_widget_details_content_index_xc.setCompoundDrawablesWithIntrinsicBounds(R.drawable.weather_vehicle, 0, 0, 0);
				}
    		}catch(Exception e){
    			e.printStackTrace();
    		}*/
		}else{
			setDefaultWeatherZhishu();
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
        		now.setDate(now.getDate()+i);
    			final String data = DateFormat.format(DEFALUT_DATE_FORMAT_CN, now).toString();
    			for(int j=0;j<weatherapps.size();j++){
        			if(data.equals(weatherapps.get(j).mcityData)){
        				weatherapp = weatherapps.get(j);
        				break;
        			}
        		}
    			if(weatherapp==null){
        			if(i==0){
        		    	setDefaultWeatherPic();
        		    	setDefaultWeatherZhishu();
        			}else if(i==1){
        				setDefaultWeatherPic0();
        			}else if(i==2){
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

	                mweather_widget_day_content.setText(content);
	                mweather_widget_day_content.setTextColor(
		    				mapp.mLauncherContext.getColor(R.color.weather_magic_widget_content_color,
		    						R.color.weather_magic_widget_content_color));
	                mweather_widget_day_temp.setText(temp);
	                mweather_widget_day_temp.setTextColor(
		    				mapp.mLauncherContext.getColor(R.color.weather_magic_widget_temp_color,
		    						R.color.weather_magic_widget_temp_color));
	                if(iconimage==null ||TextUtils.isEmpty(iconimage)){
	            		mweather_widget_day_temp.setText("");
	            		mweather_widget_day_content.setText("");
	                	mweather_widget_day_icon.setImageDrawable(
	            				mapp.mLauncherContext.getDrawable(R.drawable.weather_widget_big_default));
	                }else{
		                if (!IsDaytime()) {
		                	if(iconimage.contains("d"))
		                		iconimage = iconimage.replace("d", "n");
		                }else{
		                	if(iconimage.contains("n"))
		                		iconimage =iconimage.replace("n", "d");
		                }
		                int iconid = -1;
		    	        try {
			        		iconid = WeatherUtilites.ICON_K5_BIG_MAP.get(iconimage);
			        		if(iconid==-1)
			                	setDefaultWeatherPic();
			                else{
			                	mweather_widget_day_icon.setImageDrawable(
		            					mapp.mLauncherContext.getDrawable(iconid));
			                	mweather_widget_day_icon.setTag(iconimage);
			                	WeatherUtilites.saveWeatherIconUpdate(context,data,iconimage,true);
			            	}
		                }catch(Exception e){
		                	e.printStackTrace();
		                	iconid = -1;
		                	setDefaultWeatherPic();
		                }
	                }
                }else if(i==1){
        			if(IsDaytime())
        				iconimage= WeatherUtilites.ICON_SINA_APP_MAP.get(weatherapp.mcityStatusCode1);
        			else
        				iconimage= WeatherUtilites.ICON_SINA_APP_MAP.get(weatherapp.mcityStatusCode2);
/*                	mweatherwidgetmagicYuBaoview.mweather_widget_other0_day_content.setText(content);
                	mweatherwidgetmagicYuBaoview.mweather_widget_other0_day_content.setTextColor(
		    				mapp.mLauncherContext.getColor(R.color.weather_magic_widget_other_content_color,
		    						R.color.weather_magic_widget_other_content_color));
 */               	mweatherwidgetmagicYuBaoview.mweather_widget_other0_day_temp.setText(temp);
                	mweatherwidgetmagicYuBaoview.mweather_widget_other0_day_temp.setTextColor(
		    				mapp.mLauncherContext.getColor(R.color.weather_magic_widget_other_temp_color,
		    						R.color.weather_magic_widget_other_temp_color));
                	if(iconimage==null ||TextUtils.isEmpty(iconimage)){
                		mweatherwidgetmagicYuBaoview.mweather_widget_other0_day_temp.setText("");
//                		mweatherwidgetmagicYuBaoview.mweather_widget_other0_day_content.setText("");
                		mweatherwidgetmagicYuBaoview.mweather_widget_other0_day_icon.setImageDrawable(
		            				mapp.mLauncherContext.getDrawable(R.drawable.weather_widget_small_default));
		            }else{
		                if (!IsDaytime()) {
		                	if(iconimage.contains("d"))
		                		iconimage = iconimage.replace("d", "n");
		                }else{
		                	if(iconimage.contains("n"))
		                		iconimage = iconimage.replace("n", "d");
		                }
		                int iconid = -1;
		    	        try{
			        		iconid = WeatherUtilites.ICON_K5_SMALL_MAP.get(iconimage);
			        		if(iconid==-1)
			                	setDefaultWeatherPic0();
			                else{
			                	mweatherwidgetmagicYuBaoview.mweather_widget_other0_day_icon.setImageDrawable(
		            					mapp.mLauncherContext.getDrawable(iconid));
			            	}
			            }catch(Exception e){
			                	e.printStackTrace();
			                	iconid = -1;
			                	setDefaultWeatherPic0();

			            }
	                }
                }else if(i==2){
        			if(IsDaytime())
        				iconimage= WeatherUtilites.ICON_SINA_APP_MAP.get(weatherapp.mcityStatusCode1);
        			else
        				iconimage= WeatherUtilites.ICON_SINA_APP_MAP.get(weatherapp.mcityStatusCode2);
/*                	mweatherwidgetmagicYuBaoview.mweather_widget_other1_day_content.setText(content);
                	mweatherwidgetmagicYuBaoview.mweather_widget_other1_day_content.setTextColor(
		    				mapp.mLauncherContext.getColor(R.color.weather_magic_widget_other_content_color,
		    						R.color.weather_magic_widget_other_content_color));
*/                	mweatherwidgetmagicYuBaoview.mweather_widget_other1_day_temp.setText(temp);
                	mweatherwidgetmagicYuBaoview.mweather_widget_other1_day_temp.setTextColor(
    				mapp.mLauncherContext.getColor(R.color.weather_magic_widget_other_temp_color,
    						R.color.weather_magic_widget_other_temp_color));
                	if(iconimage==null ||TextUtils.isEmpty(iconimage)){
                		mweatherwidgetmagicYuBaoview.mweather_widget_other1_day_temp.setText("");
//                		mweatherwidgetmagicYuBaoview.mweather_widget_other1_day_content.setText("");
                		mweatherwidgetmagicYuBaoview.mweather_widget_other1_day_icon.setImageDrawable(
		            				mapp.mLauncherContext.getDrawable(R.drawable.weather_widget_small_default));
		            }else{
		                if (!IsDaytime()) {
		                	if(iconimage.contains("d"))
		                		iconimage = iconimage.replace("d", "n");
		                }else{
		                	if(iconimage.contains("n"))
		                		iconimage= iconimage.replace("n", "d");
		                }
		                int iconid = -1;
		    	        try {
			        		iconid = WeatherUtilites.ICON_K5_SMALL_MAP.get(iconimage);
			        		if(iconid==-1)
			                	setDefaultWeatherPic1();
			                else{
			                	mweatherwidgetmagicYuBaoview.mweather_widget_other1_day_icon.setImageDrawable(
		            					mapp.mLauncherContext.getDrawable(iconid));
			            	}
		                }catch(Exception e){
		                	e.printStackTrace();
		                	iconid = -1;
		                	setDefaultWeatherPic1();
		                }
	                }
                }
                if(WeatherUtilites.isInDefaultTheme(context)){
                	try{
                		if(i == 0){
                        	mweather_widget_details_content_city.setText(mweather_widget_day_city.getText()+" "+content);
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
	private void updateAppdata(final Context context)
	{
		new Thread() {
			@Override
			public void run() {
				try{
					updateweatherdata(context);
				}catch(IllegalStateException ex){
					ex.printStackTrace();
				}
			}
    	}.start();
    	new Thread() {
			@Override
			public void run() {
				try{
					updateweatherzhishudata(context);
				}catch(IllegalStateException ex){
					ex.printStackTrace();
				}
			}
    	}.start();
	}
	private void updateWeather(final Context context)
	{
		if(!WeatherUtilites.findForPackage(context,WeatherUtilites.SINA_PACKAGENAME)){
			mweather_widget_other_info_switch.setVisibility(View.GONE);
			mweather_widget_other_day_layout.removeAllViews();
			mweather_widget_other_day_layout.addView(mweatherwidgetmagicYuBaoview,mlp);
			new Thread() {
				@Override
				public void run() {
					try{
						WeatherUtilites.saveWeatherIconUpdate(context,"","",false);
						List<WeatherDetails> weatherdetails  = null;
						final String cityname =  Settings.System.getString(context.getContentResolver(), WeatherUtilites.CITY_NAME);
						if(cityname!=null && cityname.length()>0){
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
			mweather_widget_other_info_switch.setVisibility(View.VISIBLE);
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
	    	new Thread() {
				@Override
				public void run() {
					try{
						updateweatherzhishudata(context);
					}catch(IllegalStateException ex){
						ex.printStackTrace();
					}
				}
	    	}.start();
		}
	}
    public void clearWeatherInfo()
    { 
    	setDefaultWeatherPic();
    	setDefaultWeatherPic0();
    	setDefaultWeatherPic1();
    	setDefaultWeatherZhishu();
    }
    public void setDefaultWeatherZhishu()
    {
		mweatherwidgetzhishuiview.mweather_widget_cloth.setText("");
		mweatherwidgetzhishuiview.mweather_widget_ganmao.setText("");
		mweatherwidgetzhishuiview.mweather_widget_shushi.setText("");
		mweatherwidgetzhishuiview.mweather_widget_zwx.setText("");
		mweatherwidgetzhishuiview.mweather_widget_sport.setText("");
		mweatherwidgetzhishuiview.mweather_widget_xiche.setText("");
    }
    public void setDefaultWeatherPic()
    {
		mweather_widget_day_temp.setText("");
		mweather_widget_day_content.setText("");
    	mweather_widget_day_icon.setImageDrawable(
				mapp.mLauncherContext.getDrawable(R.drawable.weather_widget_big_default));
    	if(WeatherUtilites.isInDefaultTheme(mcontext)){
    		mweather_widget_details_content_temp.setText("");
    		mweather_widget_details_content_wind.setText("");
        	mweather_widget_details_content_index.setText(" 穿衣: ");
            mweather_widget_details_content_index_uv.setText(" 紫外线: ");
       		mweather_widget_details_content_index_xc.setText(" 污染: ");
    		mweather_widget_details_content_index_tr.setText(" 空调: ");
    	}
    	
    }
    public void setDefaultWeatherPic0()
    {
    	mweatherwidgetmagicYuBaoview.mweather_widget_other0_day_temp.setText("");
//    	mweatherwidgetmagicYuBaoview.mweather_widget_other0_day_content.setText("");
    	mweatherwidgetmagicYuBaoview.mweather_widget_other0_day_icon.setImageDrawable(
   				mapp.mLauncherContext.getDrawable(R.drawable.weather_widget_small_default));
    	if(WeatherUtilites.isInDefaultTheme(mcontext)){
    		mweather_widget_details_content_layout_content[0].setText("");
    		mweather_widget_details_content_layout_temp[0].setText("");
    	}
    }
    public void setDefaultWeatherPic1()
    {
    	mweatherwidgetmagicYuBaoview.mweather_widget_other1_day_temp.setText("");
//    	mweatherwidgetmagicYuBaoview.mweather_widget_other1_day_content.setText("");
    	mweatherwidgetmagicYuBaoview.mweather_widget_other1_day_icon.setImageDrawable(
   				mapp.mLauncherContext.getDrawable(R.drawable.weather_widget_small_default));
    	if(WeatherUtilites.isInDefaultTheme(mcontext)){
    		mweather_widget_details_content_layout_content[1].setText("");
    		mweather_widget_details_content_layout_temp[1].setText("");
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
    public void setTimeLayoutlistener(final Context context)
    {
    	OnClickListener listener = new OnClickListener(){
    		public void onClick(View v){
			    boolean res = WeatherUtilites.setOnClickListenerIntent(context,"com.lenovomobile.deskclock");
			    if (!res) {
			        if(!WeatherUtilites.setOnClickListenerIntent(context ,"com.android.deskclock")) {
			        	if(!WeatherUtilites.setOnClickListenerIntent(context, "com.ontim.clock")){
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
    		}
    	};
//    	OnLongClickListener longlistener = new View.OnLongClickListener() {
//    		public boolean onLongClick(View v) {
//    			((View)(mweatherwidgetmagicview.getParent())).performLongClick();
//    			return true;
//    		}
//    	};
//    	mweather_widget_time_num_layout.setOnLongClickListener(longlistener);
    	mweather_widget_time_num_layout.setOnClickListener(listener);
//    	mweather_widget_time_bg.setOnLongClickListener(longlistener);
    	mweather_widget_time_bg.setOnClickListener(listener);
    }
    	
    public void setWeathInfolistener(final Context context)
    {
    	
    	OnClickListener citylistener = new OnClickListener(){
	   		public void onClick(View v){
	   			boolean mNetworkEnabled = SettingsValue.isNetworkEnabled(context);
	 			if(mNetworkEnabled){
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
   			 boolean mNetworkEnabled = SettingsValue.isNetworkEnabled(context);
		 			if(mNetworkEnabled){
	    				if(!WeatherUtilites.findForPackage(context,WeatherUtilites.SINA_PACKAGENAME)){
	    					if(!isWeatherOutOfDate24(context)){
	    	 		 			if(WeatherUtilites.isInDefaultTheme(context) && mweather_widget_details_pic!=null && mweather_widget_details_pic.getwindowstate())
	    	 		 				displayDetailsWindow(context);
	    	 		 		}else{
	    	 		 			R2.echo("weather is out of date 24hours");
	    	 		 		}
						}else{
							new Thread() {
								@Override
								public void run() {
									Intent intent = new Intent("sina.mobile.tianqitong.main.Splash");
									intent.setClassName("sina.mobile.tianqitong", "sina.mobile.tianqitong.main.Splash");
									intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									try{
					    	            context.startActivity(intent);
					    	            }catch(ActivityNotFoundException e){
					    	            	e.printStackTrace();
					    	        }
								}
							}.start();
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
/*    	OnLongClickListener longlistener = new View.OnLongClickListener() {
    		public boolean onLongClick(View v) {
    			((View)(mweatherwidgetmagicview.getParent())).performLongClick();
    			return true;
    		}
    	};
    	OnLongClickListener longexlistener = new View.OnLongClickListener() {
    		public boolean onLongClick(View v) {
    			((View)(mweatherwidgetmagicview.getParent())).performLongClick();
    			Intent intent = new Intent("android.intent.action.WORKSPACE_PICK_LEOSEXWIDGET");
    			context.startActivity(intent);
    			return true;
    		}
    	};*/
    	OnClickListener otherlistener = new OnClickListener(){
	   		public void onClick(View v){
				new Thread() {
					@Override
					public void run() {
						try{
							boolean isapp  = WeatherUtilites.getWeatherAppstate(context);
							if(!isapp)
								return;
							Reaper.processReaper( context,
				                       Reaper.REAPER_EVENT_CATEGORY_WIDGET,
				                       "WeatherButton",
				                      Reaper.REAPER_NO_LABEL_VALUE,
				                       Reaper.REAPER_NO_INT_VALUE );
				   	    	final Intent intent = new Intent(WeatherUtilites.ACTION_WEATHER_WIDGET_CHANGE_EXWIDGET);
				   			if(miscurrentother){
				   				WeatherUtilites.saveExWidgetInfo(context, "com.lenovo.launcher2", "com.lenovo.launcher2.weather.widget.WeatherWidgetYuBaoMagicView");
					    		intent.putExtra(WeatherUtilites.EXTRA_WEATHER_EXWIDGET_CLASSNAME,"com.lenovo.launcher2.weather.widget.WeatherWidgetYuBaoMagicView");
					    		miscurrentother = false;
				   			}else{
				   				WeatherUtilites.saveExWidgetInfo(context, "com.lenovo.launcher2", "com.lenovo.launcher2.weather.widget.WeatherWidgetZhishuiView");
					    		intent.putExtra(WeatherUtilites.EXTRA_WEATHER_EXWIDGET_CLASSNAME,"com.lenovo.launcher2.weather.widget.WeatherWidgetZhishuiView");
					    		miscurrentother = true;
				   			}
				    		context.sendBroadcast(intent);
				    		intent.putExtra(WeatherUtilites.EXTRA_WEATHER_EXWIDGET_PACKAGENAME,"com.lenovo.launcher2");

						}catch(IllegalStateException ex){
							ex.printStackTrace();
						}
					}
				}.start();
	   		}
    	};
//    	mweather_widget_other_info_switch.setOnLongClickListener(longexlistener);
    	mweather_widget_other_info_switch.setOnClickListener(otherlistener);
    	mweather_widget_day_city.setOnClickListener(citylistener);
    	mweather_widget_day_temp.setOnClickListener(citylistener);
    	mweather_widget_day_content.setOnClickListener(citylistener);
    	mweather_widget_day_details_layout.setOnClickListener(citylistener);
//    	mweather_widget_day_city.setOnLongClickListener(longlistener);

   		mweather_widget_other_day_layout.setOnClickListener(listener);
   		mweather_widget_day_icon.setOnClickListener(listener);
//    	mweather_widget_other_day_layout.setOnLongClickListener(longexlistener);
//    	mweather_widget_day_details_layout.setOnLongClickListener(longlistener);
//    	mweather_widget_day_icon.setOnLongClickListener(longlistener);
    }
    
	private void buildViewCache() {
		/*mViewCache = null;
		destroyDrawingCache();
		Bitmap face = null;
		try {
			clearFocus();
			buildDrawingCache();
			face = getDrawingCache();
			if( face != null ){
				mViewCache = face;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		destroyDrawingCache();
		Log.i("TETE", "buildViewCache in WeatherWidgetView");
		mcontext.sendBroadcast( new Intent(XViewContainer.ACTION_UPDATE_CACHE) );
	}
//	public Bitmap getsnapCache()
//	{
//		return mViewCache;
//	}
}
