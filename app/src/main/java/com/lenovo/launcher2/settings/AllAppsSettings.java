/*
 * Copyright (C) 2012
 *
 * RK_ID: RK_SETTINGS. AUT: liuli1. DATE: 2012-04-01
 * add activity for all app settings
 */
package com.lenovo.launcher2.settings;

import android.app.ActionBar;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.customizer.GlobalDefine;
import com.lenovo.launcher2.customizer.Reaper;
import com.lenovo.launcher2.customizer.SettingsValue;


public class AllAppsSettings extends BaseSettingActivty implements OnPreferenceChangeListener {
    private static final String TAG = "DesktopSettings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        addPreferences();
        init();
		title.setText(R.string.header_category_applist);
		icon.setImageResource(R.drawable.dialog_title_back_arrow);

    }

    @SuppressWarnings("deprecation")
    private void init() {
      //  ActionBar ab = getActionBar();
      //  ab.setTitle(R.string.header_category_applist);

        if (GlobalDefine.WIDGET_TAB_ENABLED) {
            // pref_load_widget_preview
            CheckBoxPreference loadWidget = (CheckBoxPreference) findPreference(SettingsValue.PREF_LOAD_WIDGET_PREVIEW);
            loadWidget.setSummaryOn(R.string.load_widget_on);
            loadWidget.setSummaryOff(R.string.load_widget_off);
            loadWidget.setOnPreferenceChangeListener(this);
        }

        // list preference for app list enter/exit effect
        ListPreference applistPref = (ListPreference) findPreference(SettingsValue.PREF_APPLIST_ENTER);
        String app_enter_value = SettingsValue.getAppListEnterValue(this);
        applistPref.setValue(app_enter_value);
        applistPref.setSummary(applistPref.getEntry());
        applistPref.setOnPreferenceChangeListener(this);

        // list preference for app list slide effect
        ListPreference applistSlidePref = (ListPreference) findPreference(SettingsValue.PREF_APPLIST_SLIDE);
        String app_slide_value = SettingsValue.getAppListSlideValue(this);
        applistSlidePref.setValue(app_slide_value);
        //SettingsValue.setPreferenceForJellyBean(applistSlidePref, app_slide_value);//cancel by xingqx 2012.12.10
        applistSlidePref.setSummary(applistSlidePref.getEntry());
        applistSlidePref.setOnPreferenceChangeListener(this);

        // y count setting
        ListPreference yCount = (ListPreference) findPreference(SettingsValue.PREF_APP_CELLY);
        String cellY = SettingsValue.getAppListCellY(this);
        yCount.setValue(cellY);
        yCount.setSummary(yCount.getEntry());
        yCount.setOnPreferenceChangeListener(this);

        /*** RK_ID: APPLIST_LOOP.  AUT: zhaoxy . DATE: 2012-12-17 . START***/
        // checkbox preference for whether applist icon text background enable
//        CheckBoxPreference applistIconTextBackgroundEnable = (CheckBoxPreference) findPreference(SettingsValue.PREF_APPLIST_ICON_TEXT_BACKGROUND);
//        applistIconTextBackgroundEnable.setSummaryOn(R.string.checkbox_open);
//        applistIconTextBackgroundEnable.setSummaryOff(R.string.checkbox_Close);
//        applistIconTextBackgroundEnable.setOnPreferenceChangeListener(this);
        
        // checkbox preference for whether applist slide in loop
        CheckBoxPreference applistLoop = (CheckBoxPreference) findPreference(SettingsValue.PREF_APPLIST_LOOP);
        applistLoop.setSummaryOn(R.string.checkbox_open);
        applistLoop.setSummaryOff(R.string.checkbox_Close);
        applistLoop.setOnPreferenceChangeListener(this);
        /*** RK_ID: APPLIST_LOOP.  AUT: zhaoxy . DATE: 2012-12-17 . END***/

        removeNoNeedSettings();
    }

    @SuppressWarnings("deprecation")
    private void addPreferences() {
       /*RK_ID: Jelly_Bean . AUT: yumina . DATE: 2012-09-10 . S*/
//       if(Build.VERSION.RELEASE.equals(SettingsValue.VERSION_CODE)){
//       addPreferencesFromResource(R.xml.all_app_settings);
//       }else{
       addPreferencesFromResource(R.xml.all_app_settings);
//       }
       /*RK_ID: Jelly_Bean . AUT: yumina . DATE: 2012-09-10 . S*/

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.i(TAG, "onPreferenceChange(): Preference - " + preference + ", newValue - " + newValue
                + ", newValue type - " + newValue.getClass());

        if (preference.getKey().equals(SettingsValue.PREF_LOAD_WIDGET_PREVIEW)) {
            SettingsValue.onLoadwidgetSettingsChange(this.getApplicationContext(), newValue);
            return true;
        } else if (preference.getKey().equals(SettingsValue.PREF_APPLIST_ENTER)) {
            SettingsValue.onAppListPreferenceChange(preference, newValue);
            /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-3-26 S*/
            Reaper.processReaper( this, 
               	   Reaper.REAPER_EVENT_CATEGORY_EFFECT, 
       			   Reaper.REAPER_EVENT_ACTION_EFFECT_APPLISTENTER2,
       			   String.valueOf(newValue),
       			   Reaper.REAPER_NO_INT_VALUE );
             /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-3-26 E*/
            return true;
        } else if (preference.getKey().equals(SettingsValue.PREF_APPLIST_SLIDE)) {
            SettingsValue.onAppListSlidePreferenceChange(preference, newValue, this); 
            /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-3-26 S*/
            Reaper.processReaper( this, 
                	   Reaper.REAPER_EVENT_CATEGORY_EFFECT, 
        			   Reaper.REAPER_EVENT_ACTION_EFFECT_APPLISTFLIP2,
        			   String.valueOf(newValue),
        			   Reaper.REAPER_NO_INT_VALUE );
            /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-3-26 E*/
            return true;
        } else if (preference.getKey().equals(SettingsValue.PREF_APP_CELLY)) {
            if (newValue instanceof String)
                SettingsValue.onAppListCellYPreferenceChange(preference, (String) newValue, this);
            return true;
        }
        /*** RK_ID: APPLIST_LOOP.  AUT: zhaoxy . DATE: 2012-12-17 . START***/
        else if (preference.getKey().equals(SettingsValue.PREF_APPLIST_LOOP)) {
            SettingsValue.onApplistLoopChange(this.getApplicationContext(), newValue);
            return true;
        }/* else if (preference.getKey().equals(SettingsValue.PREF_APPLIST_ICON_TEXT_BACKGROUND)) {
            SettingsValue.onApplistIconTextBackgroundChange(this.getApplicationContext(), newValue);
            return true;
        }*/
        /*** RK_ID: APPLIST_LOOP.  AUT: zhaoxy . DATE: 2012-12-17 . END***/

        return false;
    }

    private void removeNoNeedSettings(){
    	PreferenceScreen pg = (PreferenceScreen)findPreference("pref_allappsetting_screen");
    	    
    	//if in BU version, don't show whitelist and cellcountXY 
        if(!GlobalDefine.BU_VERSION){
    	    Preference tempObj = findPreference(SettingsValue.PREF_ICON_BG_STYLE);
    		pg.removePreference(tempObj);			
    	}

    }
}
