/*
 * Copyright (C) 2012
 *
 * RK_ID: RK_SETTINGS. AUT: liuli1. DATE: 2012-04-05
 * add activity for personalized settings
 */
package com.lenovo.launcher2.addon.gesture.settings;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.settings.BaseSettingActivty;
/**
 * Author : liuyg1@lenovo.com
 * */
public class GestureSettings extends BaseSettingActivty implements OnPreferenceChangeListener {
    private static final String TAG = "MoreSettings";

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferences();
        
        if(title != null && icon != null){
		    title.setText(R.string.gesture_settings_title);
		    icon.setImageResource(R.drawable.dialog_title_back_arrow);
        }
    }

    
    @SuppressWarnings("deprecation")
    private void addPreferences() {
        addPreferencesFromResource(R.xml.gesture_settings);
        
        ListPreference scrollUpPref = null;
        scrollUpPref = (ListPreference) findPreference(SettingsValue.PREF_SCROLL_UP_GESTURE);
              		
        if(scrollUpPref != null){
            String scrollUpGesture = SettingsValue.getScrollUpGuestureArray(this);
            scrollUpPref.setValue(scrollUpGesture);
            scrollUpPref.setSummary(scrollUpPref.getEntry());
            scrollUpPref.setOnPreferenceChangeListener(this);
        }
        
        ListPreference scrollDownPref = null;
        scrollDownPref = (ListPreference) findPreference(SettingsValue.PREF_SCROLL_DOWN_GESTURE);
        if(scrollDownPref != null){
            String scrollDownGesture = SettingsValue.getScrollDownGuestureArray(this);
            scrollDownPref.setValue(scrollDownGesture);
            scrollDownPref.setSummary(scrollDownPref.getEntry());
            scrollDownPref.setOnPreferenceChangeListener(this);
        }
        
        ListPreference doubleClickPref = null;
        doubleClickPref = (ListPreference) findPreference(SettingsValue.PREF_DOUBLE_CLICK_GESTURE);
        if(doubleClickPref != null){
            String doubleClickGesture = SettingsValue.getDoubleClickGuestureArray(this);
            doubleClickPref.setValue(doubleClickGesture);
            doubleClickPref.setSummary(doubleClickPref.getEntry());
            doubleClickPref.setOnPreferenceChangeListener(this);
        }

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.i(TAG, "onPreferenceChange(): Preference - " + preference + ", newValue - " + newValue
                + ", newValue type - " + newValue.getClass());
        if (preference.getKey().equals(SettingsValue.PREF_SCROLL_UP_GESTURE)) {
            if (newValue instanceof String){
                SettingsValue.onScrollUpGuestureArrayPreferenceChange(preference,  (String)newValue, this);
            }
            return true;
        }else if(preference.getKey().equals(SettingsValue.PREF_SCROLL_DOWN_GESTURE)){
        	 if (newValue instanceof String){
                 SettingsValue.onScrollDownGuestureArrayPreferenceChange(preference,  (String)newValue, this);
             }
             return true;	
        }else if(preference.getKey().equals(SettingsValue.PREF_DOUBLE_CLICK_GESTURE)){
        	 if (newValue instanceof String){
                 SettingsValue.onDoubleClickGuesturePreferenceChange(preference,  (String)newValue, this);
             }
             return true;
        }
        return false;

    }

    boolean activityActive = true;

    protected void onResume() {
        super.onResume();
        activityActive = true;
        
    }

    protected void onPause() {
        super.onPause();
        activityActive = false;
    }

    @Override
    public void onBackPressed() {
        if (activityActive)
            super.onBackPressed();
    }
}


