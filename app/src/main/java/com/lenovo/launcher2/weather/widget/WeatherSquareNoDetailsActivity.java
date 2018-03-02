package com.lenovo.launcher2.weather.widget;

import java.util.Calendar;
import java.util.TimeZone;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.weather.widget.utils.WeatherUtilites;

public class WeatherSquareNoDetailsActivity extends Activity {
	private RelativeLayout mweatherwidgetnodetailsview;
	LinearLayout mweather_widget_nodetails_date_city = null;
	TextView mweather_widget_nodetails_city;
	TextView mweather_widget_nodetails_date;
	
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
   	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mapp = (LauncherApplication) WeatherSquareNoDetailsActivity.this.getApplicationContext();
		mcontext = WeatherSquareNoDetailsActivity.this;
		mres = WeatherSquareNoDetailsActivity.this.getResources();
		posx = getIntent().getIntExtra(WeatherUtilites.DIALOG_X, 0);
		posy = getIntent().getIntExtra(WeatherUtilites.DIALOG_Y, 0);
		Log.i("sqno", "posx= " + posx + " posy= " + posy);
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
	private final static int TIMER_ID = 0x0021;
	private final static int FINISH_ID = 0x0022;
	private void initHandler(Context context) {
		// TODO Auto-generated method stub
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch(msg.what) {
				case TIMER_ID:
					displayDate(mcontext);
					displayCityname(mcontext);
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
    	mweather_widget_nodetails_date_city.setOnClickListener(citylistener);
	}
	protected void displayCityname(Context context) {
		// TODO Auto-generated method stub
		mweather_widget_nodetails_city.setText(" " + WeatherUtilites.getCityName(context, WeatherUtilites.getLan()));
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
    			mweather_widget_nodetails_date.setText(dateString + " " + weekStr);
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
		mweatherwidgetnodetailsview = (RelativeLayout) mapp.mLauncherContext.getLayoutViewByName(true, "weather_widget_square_nodetails_layout",null);
		if (mweatherwidgetnodetailsview != null) {
			try{
				mweather_widget_nodetails_date_city = (LinearLayout)mapp.mLauncherContext.findViewByIdName(mweatherwidgetnodetailsview, "weather_widget_nodetails_date_city");
				mweather_widget_nodetails_city = (TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetnodetailsview, "weather_widget_nodetails_city");
				mweather_widget_nodetails_date = (TextView)mapp.mLauncherContext.findViewByIdName(mweatherwidgetnodetailsview, "weather_widget_nodetails_date");
			}catch(Exception e){
				e.printStackTrace();
			}
		} else {
       		mweatherwidgetnodetailsview = (RelativeLayout) View.inflate(context, R.layout.weather_widget_square_nodetails_layout, null);
       		mweather_widget_nodetails_date_city = (LinearLayout)mweatherwidgetnodetailsview.findViewById(R.id.weather_widget_nodetails_date_city);
       		mweather_widget_nodetails_city = (TextView)mweatherwidgetnodetailsview.findViewById(R.id.weather_widget_nodetails_city);
       		mweather_widget_nodetails_date = (TextView)mweatherwidgetnodetailsview.findViewById(R.id.weather_widget_nodetails_date);
		}
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.weather_widget_square_details_layout_empty_bg);
		int bgheight = bmp.getHeight();
		int bgwidth = bmp.getWidth();
		int scrwidth = mdisplaywidth * 95 / 100;
		int width = Math.abs(scrwidth - bgwidth) < 20 ? bgwidth : scrwidth;
		Log.i("sqno", "mdisplaywidth = " + mdisplaywidth + " mdisplayheight/2 = " + mdisplayheight/2 + " bgh = " + bgheight + " bgw = " + bgwidth + " width = " + width);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, bgheight);
		/*mAlertDialog = new LeAlertDialog(mcontext, R.style.SquareThemeNoDetails);
		mAlertDialog.setContentView(mweatherwidgetnodetailsview, lp);
		mAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface paramDialogInterface) {
                finish();
            }
        });
		mAlertDialog.show();*/
		
		dialog = new Dialog(mcontext, R.style.SquareThemeNoDetails);
		dialog.setContentView(mweatherwidgetnodetailsview, lp);
		Window window = dialog.getWindow();
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
		Log.i("sqno" , weathet_widget_square_details_pos_big + " is big;  " + weathet_widget_square_details_pos_50 + " is 50; " + weathet_widget_square_details_pos_200 + " is 200; "
				 + weathet_widget_square_details_pos_300 + " is 300; " + weathet_widget_square_details_pos_500 + " is 500; " + weathet_widget_square_details_pos_max + " is max ");
		if (Math.abs(posy - centerY) < 50)
			wl.y = weathet_widget_square_details_pos_50;
		else if (posy > centerY)
			wl.y = weathet_widget_square_details_pos_big;
		else if (Math.abs(posy - centerY) < 200)
			wl.y = weathet_widget_square_details_pos_200;
		else if (Math.abs(posy - centerY) < 300)
			wl.y = weathet_widget_square_details_pos_300;
		else if (Math.abs(posy - centerY) < 500)
			wl.y = weathet_widget_square_details_pos_500;
		else
			wl.y = weathet_widget_square_details_pos_max;
		Log.i("sqno" , wl.y + " is dialog y pos");
		dialog.onWindowAttributesChanged(wl);
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

}
