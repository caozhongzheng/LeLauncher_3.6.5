/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lenovo.launcher2.settings;

// Need the following import to get access to the app resources, since this
// class is in a sub-package.
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.provider.Settings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.weather.widget.settings.City;
import com.lenovo.launcher2.weather.widget.settings.CityColumns;
import com.lenovo.launcher2.weather.widget.settings.HotCityAdapter;
import com.lenovo.launcher2.weather.widget.settings.SearchCityAdapter;
import com.lenovo.launcher2.weather.widget.settings.WeatherDetails;
import com.lenovo.launcher2.weather.widget.utils.WeatherUtilites;
/**
 * <h3>Dialog Activity</h3>
 * 
 * <p>This demonstrates the how to write an activity that looks like 
 * a pop-up dialog.</p>
 */
public class ProvinceActivity  extends Activity {
	public static String TAG = "LargeWeatherWidgetActivity";
	private ListView mlistview;
	private EditText medittext;
	private GridView msearch_weather_hot_city;
	private ImageView mweaher_city_search_line;
	private ProgressBar mWattingBar;
	private TextView mweather_city_name_tips;
	private boolean isSearching = false;
	private boolean autoisSearching = false;
	private ImageButton mautosearch ;
	private ImageButton medits_search_btn ;
	private static final int SEARCH_CITY = 0x100001;
	private static final int AUTO_SEARCH_CITY = 0x100003;
	private static final int SEARCH_CITY_DETAILS = 0x100002;
	private ProgressDialog mprogressdialog;
	boolean isFromWeatherDetails = false;
	public static String mzhcitynames[];
	public static String mcitynames[];
	private static String DB_PATH  = "/data/data/com.lenovo.launcher/databases/";
	private static String DB_PATH_DIR  = "/data/data/com.lenovo.launcher/databases/cities.db";
	private static String DB_NAME  = "cities.db";
	private ArrayList<City> mcities = new ArrayList<City>();
    private static final String IMAGE_SRC = "image_src";
    private static final String CITY_NAME = "city_name";
    private static final String TEMPS = "temps";
    private static final String CONDITION = "condition";
    
     /*RK_ID: WEATHER_CITY_SET. AUT: SHENCHAO1. 2012-12-19 S.*/
	 private TextView mDialogTitle;
	 /*RK_ID: WEATHER_CITY_SET. AUT: SHENCHAO1. 2012-12-19 E. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_city_select);
		
	    /*RK_ID: WEATHER_CITY_SET. AUT: SHENCHAO1. 2012-12-19 S.*/
		//getActionBar().setTitle(R.string.weather_settings);
		
		mDialogTitle = (TextView) findViewById(R.id.dialog_title);
		mDialogTitle.setText(R.string.setting_weather_city);
		/*RK_ID: WEATHER_CITY_SET. AUT: SHENCHAO1. 2012-12-19 E. */
		setCityNames(getResources().getStringArray(R.array.hot_citys));
		setZhCityNames(getResources().getStringArray(R.array.hot_search_citys));
		mlistview = (ListView)this.findViewById(R.id.weaher_city_seartch_list);
		medittext = (EditText)this.findViewById(R.id.weaher_city_search_edit);
		mweather_city_name_tips = (TextView)this.findViewById(R.id.weather_city_name_tips);
		mweaher_city_search_line = (ImageView)this.findViewById(R.id.weaher_city_search_line);
		msearch_weather_hot_city = (GridView)this.findViewById(R.id.weaher_city_search_hot_city);
		mlistview.setVisibility(View.GONE);
		msearch_weather_hot_city.setVisibility(View.VISIBLE);
		msearch_weather_hot_city.setAdapter(new HotCityAdapter(this));
		msearch_weather_hot_city.setOnItemClickListener(new OnItemClickListener() { 
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) { 
	      		final String cityname = mcitynames[position];
	      		final String zhcityname = mzhcitynames[position];
	      		Settings.System.putInt(getContentResolver(), IMAGE_SRC,  -1);
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
				if(imm.isActive()){  
					 imm.hideSoftInputFromWindow(medittext.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);  
				}  
	      		String oldCityname = WeatherUtilites.getCityName(ProvinceActivity.this, 1);
			    if (!zhcityname.equals(oldCityname))
			    {		        
			    	WeatherUtilites.saveWeatherIconUpdate(ProvinceActivity.this,"","",false);
			        new Thread( new Runnable() {
	                    public void run() {
	                        Settings.System.putString(getContentResolver(), "city_name", cityname);
	                        Settings.System.putString(getContentResolver(), "cityID", "");
	                        Settings.System.putString(getContentResolver(), CONDITION, "");
	                        Settings.System.putString(getContentResolver(), TEMPS, "");
	                        Settings.System.putInt(getContentResolver(), "day", 0);
	                        Settings.System.putInt(getContentResolver(), "hour", 0);           

	                        SharedPreferences preferences = getSharedPreferences("lockscreen", 0);
	                        if (preferences != null)
	                        {
	                            Editor editor = preferences.edit();                            
	                            editor.putString("cityname", zhcityname);
	                            editor.commit();
	                        }
	                        WeatherUtilites.saveupdatetime(ProvinceActivity.this, 0);
	                        
	                        City city= WeatherUtilites.getcityByKey(ProvinceActivity.this,zhcityname);
	                        if(city!=null){
	                        	WeatherUtilites.saveCityName(ProvinceActivity.this, city);
	                        	city.tostring();
	                        }
	                        else
	                        	WeatherUtilites.saveCityName(ProvinceActivity.this, zhcityname);
	                        WeatherUtilites.deleteWeatherDetails(ProvinceActivity.this);
	        		        sendBroadcast(new Intent(WeatherUtilites.ACTION_LOCATION_CHANGE));
	                    }
			        }).start();
			    }
			    Toast toast = Toast.makeText(ProvinceActivity.this,
			            getString(R.string.city_setting) + cityname, Toast.LENGTH_SHORT);
//			    toast.setGravity(Gravity.CENTER, 0, 0);
			    toast.show();
			    finish();
            }
		}); 
        /***RK_ID:RK_FONT AUT:zhanglz1@lenovo.com. DATE: 2013-02-21 S***/        
        Typeface tf = SettingsValue.getFontStyle(this);
        if (tf != null && tf != mDialogTitle.getTypeface()){
        	mDialogTitle.setTypeface(tf);
        	mweather_city_name_tips.setTypeface(tf);
        	medittext.setTypeface(tf);
        }
        /***RK_ID:RK_FONT AUT:zhanglz1@lenovo.com. DATE: 2013-02-21 E***/        

		mlistview.setOnItemClickListener(onItemClickListener);
		medittext.addTextChangedListener(textWatcher);

	}
	
	private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
      	public void onItemClick(AdapterView<?> parent, View view, int position,
                  long id) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
			if(imm.isActive()){  
				 imm.hideSoftInputFromWindow(medittext.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);  
			}  
      		String oldCityname = WeatherUtilites.getCityName(ProvinceActivity.this, 1);
      		final City city = mcities.get(position);
		    final String cityname = city.getcityname();
		    final String zhcityname = city.getzhcity();
		    if (!zhcityname.equals(oldCityname))
		    {		        
		    	Log.d("a","cityname="+cityname);
		    	WeatherUtilites.saveWeatherIconUpdate(ProvinceActivity.this,"","",false);
		        new Thread( new Runnable() {
                    public void run() {
                    	WeatherUtilites.saveupdatetime(ProvinceActivity.this, 0);
                        WeatherUtilites.saveCityName(ProvinceActivity.this, city);
                    	SharedPreferences preferences = getSharedPreferences("lockscreen", 0);
                        if (preferences != null)
                        {
                            Editor editor = preferences.edit();                            
                            editor.putString("cityname", zhcityname);
                            editor.commit();
                        }
                        WeatherUtilites.deleteWeatherDetails(ProvinceActivity.this);
            	        sendBroadcast(new Intent(WeatherUtilites.ACTION_LOCATION_CHANGE));
                    }
		        }).start();
		    }
		    Toast toast = Toast.makeText(ProvinceActivity.this,
		            getString(R.string.city_setting) + cityname, Toast.LENGTH_SHORT);
//		    toast.setGravity(Gravity.CENTER, 0, 0);
		    toast.show();
		    finish();
      	}
    }; 
	private TextWatcher textWatcher =new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			if(s==null||TextUtils.isEmpty(s)){
				mlistview.setVisibility(View.GONE);
				msearch_weather_hot_city.setVisibility(View.VISIBLE);
				mweather_city_name_tips.setVisibility(View.VISIBLE);
				mweaher_city_search_line.setVisibility(View.VISIBLE);
			}else{
				if(s.toString().length()>100)
					return;
				mcities =WeatherUtilites.getcitysByKey(ProvinceActivity.this,s.toString());
				if(mcities!=null&&mcities.size()>0){
			    	SearchCityAdapter adapter = new SearchCityAdapter(ProvinceActivity.this,mcities);
					msearch_weather_hot_city.setVisibility(View.GONE);
					mweather_city_name_tips.setVisibility(View.GONE);
					mweaher_city_search_line.setVisibility(View.GONE);
					mlistview.setVisibility(View.VISIBLE);
					mlistview.setAdapter(adapter);
				}else{
					mlistview.setVisibility(View.GONE);
					msearch_weather_hot_city.setVisibility(View.VISIBLE);
					mweather_city_name_tips.setVisibility(View.VISIBLE);
					mweaher_city_search_line.setVisibility(View.VISIBLE);
				}
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			if(s==null||TextUtils.isEmpty(s)){
				mlistview.setVisibility(View.GONE);
				msearch_weather_hot_city.setVisibility(View.VISIBLE);
				mweather_city_name_tips.setVisibility(View.VISIBLE);
				mweaher_city_search_line.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			
		} 
		
	};
	private void setZhCityNames(String[] stringArray) {
		mzhcitynames = stringArray;
	}
	private void setCityNames(String[] stringArray) {
		mcitynames = stringArray;
	}
}
