package com.lenovo.launcher2.settings;

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

// Need the following import to get access to the app resources, since this
// class is in a sub-package.
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.lenovo.launcher.R;

/**
 * <h3>Dialog Activity</h3>
 * 
 * <p>
 * This demonstrates the how to write an activity that looks like a pop-up
 * dialog.
 * </p>
 */
public class CityActivity extends ListActivity {
	Context mContext;
	private String pcode = "";
	static final String WeatherCityPrefs = "City";
	   
    private static final String ALARM_SRC = "alarm_src";
    private static final String IMAGE_SRC = "image_src";
    private static final String CITY_NAME = "city_name";
    private static final String TEMPS = "temps";
    private static final String CONDITION = "condition";
    private static final String TAG = "CityActivity";
    private ActionBar mActionBar;
    private String pName = null;
    String mLanguage;
    String mCountry;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	    final Configuration configuration = getResources().getConfiguration();
	    mLanguage = configuration.locale.getLanguage();
	    mCountry = configuration.locale.getCountry().toLowerCase();
		
	    mActionBar = getActionBar();
        /*RK_ID: RK_LOCK . AUT: xingqx . DATE: 2012-03-01 . S*/
		String cityName = Settings.System.getString(getContentResolver(),
				"city_name");
		String provinceName = Settings.System.getString(getContentResolver(),
				"province_name");
		String title = this.getString(R.string.lockscreen_weather_city);

		if (provinceName != null) {
			title = title + "   " + provinceName;
		} else {
			title = title + "       ";
		}

		if (cityName != null) {
			title = title + "   " + cityName;
		} else {
			title = title + "       ";
		}
		// mActionBar.setTitle(R.string.lockscreen_weather_city);
        /*RK_ID: RK_LOCK . AUT: xingqx . DATE: 2012-03-01 . E*/
		mActionBar.setTitle(title);
		Intent intent = this.getIntent();
		pcode = intent.getStringExtra("code");
		pName = intent.getStringExtra("province");
		mContext = getApplicationContext();
		setListAdapter(new SimpleAdapter(this, getData(),
				android.R.layout.simple_list_item_1,
				new String[] { "cityname" }, new int[] { android.R.id.text1 }));
		getListView().setTextFilterEnabled(true);
	}

	protected List getData() {
		List<Map> myData = new ArrayList<Map>();
		
	    int cityxml;	
        if (mLanguage.equals("zh")) {
            if ("cn".equals(mCountry))
            {
                cityxml = R.xml.city;
            }
            else
            {
                cityxml = R.xml.city_tw;
            }
        }
        else
        {
            cityxml = R.xml.city_en;
        }
		XmlResourceParser x = mContext.getResources().getXml(cityxml);
		try {
			int eventType = x.getEventType();
			boolean flag = false;
			String code = "";
			while (eventType != XmlPullParser.END_DOCUMENT) {

				if (eventType == XmlPullParser.START_TAG) {
					String tag = x.getName();
					if (tag.equals("dict")) {
						String key = x.getAttributeValue(0);
						if (pcode.equals(key)) {
							flag = true;
						}
					}
					if (tag.equals("string") && flag) {
						code = x.getAttributeValue(0);
					}
				} else if (eventType == XmlPullParser.TEXT) {
					if (flag) {
						String city = x.getText();
						addItem(myData, code, city);
					}
				} else if (eventType == XmlPullParser.END_TAG) {
					String tag = x.getName();
					if (tag.equals("dict") && flag) {
						flag = false;
						break;
					}
				}
				eventType = x.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return myData;
	}

	protected void addItem(List<Map> data, String city_id, String city_name) {
		Map<String, String> temp = new HashMap<String, String>();
		temp.put("cityid", city_id);
		temp.put("cityname", city_name);
		data.add(temp);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Map map = (Map) l.getItemAtPosition(position);

		final String cityid = (String) map.get("cityid");
		if (!"".equals(cityid)) {
//			Log.i("test1125", "you have click :" + cityid + ""
//					+ (String) map.get("cityname"));
		    
		    String oldCityid = Settings.System.getString(getContentResolver(), "cityID");
		    
		    Log.d(TAG, "oldCityid = " + oldCityid + "cityid = " + cityid);
		    
		    final String cityname = (String) map.get("cityname");
		    
		    if (!cityid.equals(oldCityid))
		    {		        
//	          SharedPreferencesManager.getInstance(mContext).setCity(cityid,
//	                  cityname);
		        /*RK_ID: RK_LOCK . AUT: xingqx . DATE: 2012-02-17 . S*/
		        new Thread( new Runnable() {
                    public void run() {
                        Log.d(TAG, "pName = " + pName + "city_name = " + cityname + "cityid = " + cityid);
                        SharedPreferences preferences = getSharedPreferences("lockscreen", 0);
                        if (preferences != null)
                        {
                            Editor editor = preferences.edit();                            
                            editor.putString("pname", pName);
                            editor.putString("pcode", pcode);
                            editor.putString("cityid", cityid);
                            editor.putString("cityname", cityname);
                            editor.commit();
                        }
                    	
                        Settings.System.putString(getContentResolver(), "province_name", pName);
                    	Settings.System.putString(getContentResolver(), "province_code", pcode);
	                    Settings.System.putString(getContentResolver(), "cityID", cityid);
	                    Settings.System.putString(getContentResolver(), "city_name", cityname);
	            
	                    //clear the old data

                        Settings.System.putInt(getContentResolver(), IMAGE_SRC,  -1);
                        Settings.System.putString(getContentResolver(), CONDITION, "");
                        Settings.System.putString(getContentResolver(), TEMPS, "");
                        Settings.System.putInt(getContentResolver(), "day", 0);
                        Settings.System.putInt(getContentResolver(), "hour", 0);           
                        Log.d(TAG, "send broadcast");
                        Intent intent2 = new Intent("com.lenovo.leos.widgets.weather.LOCATION_CHANGED");
                        intent2.putExtra("cityid", cityid);
                        intent2.putExtra("cityname", cityname);
                        mContext.sendBroadcast(intent2);    
                    }
                }).start();
		        /*RK_ID: RK_LOCK . AUT: xingqx . DATE: 2012-02-17 . E*/
		    }	
		    
		    Toast toast = Toast.makeText(mContext,
		            getString(R.string.city_setting) + cityname, Toast.LENGTH_LONG);
		    toast.setGravity(Gravity.CENTER, 0, 0);
		    toast.show();
						
			setResult(RESULT_OK);
			finish();
		}

	}

}
