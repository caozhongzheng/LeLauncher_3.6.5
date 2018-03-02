package com.lenovo.launcher2.weather.widget;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnKeyListener;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;
import com.lenovo.launcher2.commonui.LeAlertDialog;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.weather.widget.settings.WeatherDetails;
import com.lenovo.launcher2.weather.widget.utils.Debug.R2;
import com.lenovo.launcher2.weather.widget.utils.WeatherUtilites;

public class WeatherSquareDetailsActivity extends Activity {

	private LinearLayout mweatherwidgetdetailsview;
	View mweather_widget_details_content_layout;
	ImageView mweather_widget_details_content_icon;
	TextView mweather_widget_details_content_temp;
	TextView mweather_widget_details_content_content;
	TextView mweather_widget_details_content_wind;
	TextView mweather_widget_details_content_index_uv;
	TextView mweather_widget_details_content_index_polution; //污染
	TextView mweather_widget_details_content_city;
	TextView mweather_widget_details_content_date;
	LinearLayout mweather_widget_details_layout;
	LinearLayout mweather_widget_details_content_weather_num[] = new LinearLayout[3];
	ImageView mweather_widget_details_content_layout_icon[] = new ImageView[3];
	TextView mweather_widget_details_content_layout_week[] = new TextView[3];
	TextView mweather_widget_details_content_layout_temp[] = new TextView[3];
	TextView mweather_widget_details_content_layout_content[]= new TextView[3];
	private int weather_widget_details_content_weather_nums[] = {R.id.weather_widget_details_content_weather_num0,
   			R.id.weather_widget_details_content_weather_num1,R.id.weather_widget_details_content_weather_num2};
	private String weather_widget_details_content_weather_nums_s[] = {"weather_widget_details_content_weather_num0",
   			"weather_widget_details_content_weather_num1","weather_widget_details_content_weather_num2"};
	LauncherApplication mapp;
	private Context mcontext;
	private Resources mres;
	private Handler mHandler = null;
	private int mdisplayheight = 0;
   	private int mdisplaywidth = 0;
   	private int statusBarHeight= 0;
   	private int posx = 0;
   	private int posy = 0;
//   	LeAlertDialog mAlertDialog = null;
   	Dialog dialog = null;
   	private BroadcastReceiver mIntentReceiver = null ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mapp = (LauncherApplication) WeatherSquareDetailsActivity.this.getApplicationContext();
		mcontext = WeatherSquareDetailsActivity.this;
		mres = WeatherSquareDetailsActivity.this.getResources();
		posx = getIntent().getIntExtra(WeatherUtilites.DIALOG_X, 0);
		posy = getIntent().getIntExtra(WeatherUtilites.DIALOG_Y, 0);
		initLayout(mcontext);
		initHandler(mcontext);
		
		new Thread(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = mHandler.obtainMessage(TIMER_ID);
				msg.sendToTarget();
			}

		}.start();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		registerWeatherIntentReceiver();
	}
	private void registerWeatherIntentReceiver() {
		// TODO Auto-generated method stub
		mIntentReceiver = new IntentReceiver();
        IntentFilter eventFilter = new IntentFilter();
		eventFilter.addAction(WeatherUtilites.ACTION_UPDATE_DEFAILD_WEATHER);
		eventFilter.addAction(WeatherUtilites.ACTION_UPDATE_WEATHER);
		eventFilter.addAction(WeatherUtilites.ACTION_WEATHER_WIDGET_CHANGECITY_FROMSINA);
		eventFilter.addAction(WeatherUtilites.ACTION_WEATHER_WIDGET_ADDCITY_FROMSINA);
		eventFilter.addAction(WeatherUtilites.ACTION_WEATHER_WIDGET_DELETECITY_FROMSINA);
		eventFilter.addAction(WeatherUtilites.ACTION_WEATHER_WIDGET_UPDATECITY_FROMSINA);
		mcontext.registerReceiver(mIntentReceiver, eventFilter);
	}

//	 private Context mreceivercontext;
	 private class IntentReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent == null) 
				return ;
//			mreceivercontext = context;
			String action = intent.getAction();
            Log.i("sq", " square details activity*********recievied " + action);
            if (action.equals(WeatherUtilites.ACTION_UPDATE_WEATHER)) {
            	mHandler.obtainMessage(TIMER_ID).sendToTarget();
            } else if (WeatherUtilites.ACTION_WEATHER_WIDGET_CHANGECITY_FROMSINA.equals(action) ||
            		WeatherUtilites.ACTION_WEATHER_WIDGET_ADDCITY_FROMSINA.equals(action) ||
            		WeatherUtilites.ACTION_WEATHER_WIDGET_DELETECITY_FROMSINA.equals(action) ||
            		WeatherUtilites.ACTION_WEATHER_WIDGET_UPDATECITY_FROMSINA.equals(action)) {
            	mHandler.obtainMessage(TIMER_ID).sendToTarget();
            }
		}

	 }
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (mcontext != null) {
	    	try {
	    		if (mIntentReceiver != null) {
	    			Log.i("sq", "square details activity unregister broadcast receiver");
	    			mcontext.unregisterReceiver(mIntentReceiver);
	    		}
	    	} catch (IllegalArgumentException e) { 
	    		e.printStackTrace();
	    	}
    	}
		super.onDestroy();
	}

	private final static int TIMER_ID = 0x0011;
	private final static int FINISH_ID = 0x0012;
	private void initHandler(Context context) {
		// TODO Auto-generated method stub
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch(msg.what) {
				case TIMER_ID:
					displayDate(mcontext);
					updateweather(mcontext);
					setWeatherListener(mcontext);
					break;
				case FINISH_ID:
					finish();
					break;
				default:break;
				}
			}

		};
	}
	protected void setWeatherListener(final Context context) {
		// TODO Auto-generated method stub
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
					    	new Thread(){

								@Override
								public void run() {
									// TODO Auto-generated method stub
									mHandler.sendEmptyMessageDelayed(FINISH_ID, 250);
								}

							}.start();
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
    	mweather_widget_details_layout.setOnClickListener(citylistener);
//		mweather_widget_details_content_date.setOnClickListener(citylistener);
//		mweather_widget_details_content_city.setOnClickListener(citylistener);
	}
	final private static String DEFALUT_DATE_FORMAT_CN = "yyyy-MM-dd";
	protected void updateweather(Context context) {
		// TODO Auto-generated method stub
		mweather_widget_details_content_city.setText(" " + WeatherUtilites.getCityName(context, WeatherUtilites.getLan()));
		List<WeatherDetails> weatherdetails  = null;
		final String cityName =  WeatherUtilites.getCityName(context, 1);
		if (cityName != null && cityName.length()>0) {
			weatherdetails  = WeatherUtilites.getWeatherDetails(context);
		}
		if (weatherdetails != null && weatherdetails.size() > 0) {
    		Configuration configuration = mres.getConfiguration();
    		String language = configuration.locale.toString();
    		for (int i=0; i<4 && i<weatherdetails.size(); i++) {
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
    			if (i == 0) {
    	        	if (iconimage != null && !TextUtils.isEmpty(iconimage)) {
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
    		        		if (iconid != -1 && WeatherUtilites.isInDefaultTheme(context)) {
    		        			try {
    		        				mweather_widget_details_content_icon.setImageDrawable(mapp.mLauncherContext.getDrawable(iconid));
    		        			} catch(Exception e) {
    			            		e.printStackTrace();
    			            	}
    		        		}

    	                }catch(Exception e){
    	                	e.printStackTrace();
    	                }
    	            }
    	        	if (WeatherUtilites.isInDefaultTheme(context)) {
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
	                }
    			}
    			if (i>0 && WeatherUtilites.isInDefaultTheme(context)) {
                	try {
                		if (iconimage != null && !TextUtils.isEmpty(iconimage)) {
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
        		        		Log.d("czz",i+"img="+img);
        		        		iconid = WeatherUtilites.SQUARE_ICON_MAP.get(iconimage);
        		        		if (iconid != -1) {
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
                }
    		}
    		if (weatherdetails.size() < 4) {
    			for (int k = weatherdetails.size(); k<4; k++) {
    				try {
    					mweather_widget_details_content_layout_icon[k - 1].setImageDrawable(null);
//			    				mapp.mLauncherContext.getDrawable(R.drawable.weather_widget_big_default));
                		mweather_widget_details_content_layout_content[k - 1].setText("");
                    	mweather_widget_details_content_layout_temp[k - 1].setText("");
            		} catch(Exception e) {
            			e.printStackTrace();
            		}
    			}
    		}
    		
    	} else {
//    		clearWeatherInfo();
    	}
	}
	boolean IsDaytime() {
        GregorianCalendar gcal = new GregorianCalendar();
        int hour = gcal.get(Calendar.HOUR_OF_DAY);
        return (hour >= 6 && hour < 18);
    }
	protected void displayDate(Context context) {
		// TODO Auto-generated method stub
		Configuration configuration = mres.getConfiguration();
    	String language = configuration.locale.getLanguage();
    	Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
    	final String dateString = getDateStr(calendar, language);
    	final int flagsWeek = DateUtils.FORMAT_SHOW_WEEKDAY; 
        String weekStr = (String)DateUtils.formatDateTime(context, 
    			System.currentTimeMillis(), flagsWeek);
        if (WeatherUtilites.isInDefaultTheme(context)) {
    		try{
        		mweather_widget_details_content_date.setText(dateString + " " + weekStr);
        		for (int i=0; i<3; i++) {
        			mweather_widget_details_content_layout_week[i].setText(WeatherUtilites.getWeek(context, i+1));
        		}
    		}catch(Exception e){
    			e.printStackTrace();
    		}
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
	private void initLayout(Context context) {
		// TODO Auto-generated method stub
		statusBarHeight = WeatherUtilites.getStatusHeights(context);
		final DisplayMetrics display = context.getResources().getDisplayMetrics();
		mdisplaywidth = display.widthPixels;
		mdisplayheight = display.heightPixels;
		mweather_widget_details_content_layout = mapp.mLauncherContext.getLayoutViewByName(true, "weather_widget_square_details_content_layout",null);
       	if (mweather_widget_details_content_layout == null) {
       		mweather_widget_details_content_layout = View.inflate(context, R.layout.weather_widget_square_details_content_layout, null);
       	}
		mweatherwidgetdetailsview = (LinearLayout) mapp.mLauncherContext.getLayoutViewByName(true, "weather_widget_square_details_layout",null);
		if (mweatherwidgetdetailsview != null) {
			try{
				mweather_widget_details_content_icon = (ImageView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetdetailsview, "weather_widget_details_content_icon");
				mweather_widget_details_content_temp = (TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetdetailsview, "weather_widget_details_content_temp");
				mweather_widget_details_content_content = (TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetdetailsview, "weather_widget_details_content_content");
				mweather_widget_details_content_wind = (TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetdetailsview, "weather_widget_details_content_wind");
				mweather_widget_details_content_index_uv = (TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetdetailsview, "weather_widget_details_content_index_uv");
				mweather_widget_details_content_index_polution = (TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetdetailsview, "weather_widget_details_content_index_polution");
				mweather_widget_details_content_city = (TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetdetailsview, "weather_widget_details_content_city");
				mweather_widget_details_content_date = (TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetdetailsview, "weather_widget_details_content_date");
				mweather_widget_details_layout = (LinearLayout)mapp.mLauncherContext.findViewByIdName(mweatherwidgetdetailsview, "weather_widget_details_layout");
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
       		mweatherwidgetdetailsview = (LinearLayout) View.inflate(context, R.layout.weather_widget_square_details_layout, null);
       		mweather_widget_details_content_icon = (ImageView)mweatherwidgetdetailsview.findViewById(R.id.weather_widget_details_content_icon);
       		mweather_widget_details_content_temp = (TextView)mweatherwidgetdetailsview.findViewById(R.id.weather_widget_details_content_temp);
       		mweather_widget_details_content_content = (TextView)mweatherwidgetdetailsview.findViewById(R.id.weather_widget_details_content_content);
    		mweather_widget_details_content_wind = (TextView)mweatherwidgetdetailsview.findViewById(R.id.weather_widget_details_content_wind);
    		mweather_widget_details_content_index_uv = (TextView)mweatherwidgetdetailsview.findViewById(R.id.weather_widget_details_content_index_uv);
    		mweather_widget_details_content_index_polution = (TextView)mweatherwidgetdetailsview.findViewById(R.id.weather_widget_details_content_index_polution);
    		mweather_widget_details_content_city = (TextView)mweatherwidgetdetailsview.findViewById(R.id.weather_widget_details_content_city);
    		mweather_widget_details_content_date = (TextView)mweatherwidgetdetailsview.findViewById(R.id.weather_widget_details_content_date);
    		mweather_widget_details_layout = (LinearLayout)mweatherwidgetdetailsview.findViewById(R.id.weather_widget_details_layout);
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
//		mweatherwidgetdetailsview.setBackgroundColor(0xBB000000);
//		RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 
//        		LinearLayout.LayoutParams.MATCH_PARENT);
//		this.setContentView(mweatherwidgetdetailsview, rp);
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.weather_widget_square_details_layout_bg);
		int bgheight = bmp.getHeight();
		int bgwidth = bmp.getWidth();
		int scrwidth = mdisplaywidth * 95 / 100;
		int width = Math.abs(scrwidth - bgwidth) < 20 ? bgwidth : scrwidth;
		Log.i("sq", "mdisplaywidth = " + mdisplaywidth + " mdisplayheight/2 = " + mdisplayheight/2 + " bgh = " + bgheight + " bgw = " + bgwidth + " width = " + width);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, bgheight); 
		/*mAlertDialog = new LeAlertDialog(mcontext, R.style.SquareTheme);
		mAlertDialog.setContentView(mweatherwidgetdetailsview, lp);
		mAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface paramDialogInterface) {
                finish();
            }
        });
		mAlertDialog.show();*/
		dialog = new Dialog(mcontext, R.style.SquareTheme);
		dialog.setContentView(mweatherwidgetdetailsview, lp);
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		dialog.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_BACK) {
					finish();
				}
				return false;
			}
		});
		Window window = dialog.getWindow();
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.x = 0;
		int centerY = (mdisplayheight - statusBarHeight) / 2;
		posy += bgheight/2;
		Log.i("sqno" , centerY + " is centerY  " + posy + " is posy");
		Integer weathet_widget_square_details_pos_big = context.getResources().getDimensionPixelOffset(R.dimen.weathet_widget_square_details_pos_big);
		Integer weathet_widget_square_details_pos_50 = context.getResources().getDimensionPixelOffset(R.dimen.weathet_widget_square_details_pos_50);
		Integer weathet_widget_square_details_pos_200 = context.getResources().getDimensionPixelOffset(R.dimen.weathet_widget_square_details_pos_200);
		Integer weathet_widget_square_details_pos_300 = context.getResources().getDimensionPixelOffset(R.dimen.weathet_widget_square_details_pos_300);
		Integer weathet_widget_square_details_pos_500 = context.getResources().getDimensionPixelOffset(R.dimen.weathet_widget_square_details_pos_500);
		Integer weathet_widget_square_details_pos_max = context.getResources().getDimensionPixelOffset(R.dimen.weathet_widget_square_details_pos_max);
		Log.i("sqno" , weathet_widget_square_details_pos_big + " is BIG;  " + weathet_widget_square_details_pos_50 + " is 50; " + weathet_widget_square_details_pos_200 + " is 200; "
				 + weathet_widget_square_details_pos_300 + " is 300; " + weathet_widget_square_details_pos_500 + " is 500; " + weathet_widget_square_details_pos_max + " is max ");
		if (Math.abs(posy - centerY) < 50)
			wl.y = weathet_widget_square_details_pos_50;  //s960 s820 A820t
		else if (posy > centerY)
			wl.y = weathet_widget_square_details_pos_big; //s960   s820_180 A678t_100 A820t_150
		else if (Math.abs(posy - centerY) < 200)
			wl.y = weathet_widget_square_details_pos_200; //A678t_-80  A820t_-150
		else if (Math.abs(posy - centerY) < 300)
			wl.y = weathet_widget_square_details_pos_300; //s820 //A678t_-250
		else if (Math.abs(posy - centerY) < 500)
			wl.y = weathet_widget_square_details_pos_500; //s960 S820_-380  A820t_-280
		else
			wl.y = weathet_widget_square_details_pos_max; //s960
		Log.i("sqno" , wl.y + " is dialog y pos");
		dialog.onWindowAttributesChanged(wl);
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

}
