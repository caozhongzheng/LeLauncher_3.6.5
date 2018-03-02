/*
 * Copyright (C) 2012
 *
 * RK_ID: RK_SETTINGS. AUT: liuli1. DATE: 2012-04-05
 * add activity for personalized settings
 */
package com.lenovo.launcher2.settings;

import com.lenovo.launcher.R;

import com.lenovo.launcher2.customizer.Reaper;
import com.lenovo.launcher2.customizer.SettingsValue;

import android.app.ActionBar;
import android.content.Intent;
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

public class PersonalizedSettings extends BaseSettingActivty implements OnPreferenceChangeListener {
    private static final String TAG = "PersonalizedSettings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    	
        super.onCreate(savedInstanceState);
        addPreferences();
       
		title.setText(R.string.personalized_settings_title);
		icon.setImageResource(R.drawable.dialog_title_back_arrow);
		
    }

    
    @SuppressWarnings("deprecation")
    private void addPreferences() {
        addPreferencesFromResource(R.xml.personalized_settings);
        
        removeNoNeedSettings();
//        ListPreference yCount = (ListPreference) findPreference(SettingsValue.PREF_APP_CELLY);
//        String cellY = SettingsValue.getAppListCellY(this);
//        yCount.setValue(cellY);
//        yCount.setSummary(yCount.getEntry());
//        yCount.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.i(TAG, "onPreferenceChange(): Preference - " + preference + ", newValue - " + newValue
                + ", newValue type - " + newValue.getClass());
        if (preference.getKey().equals(SettingsValue.PREF_APP_CELLY)) {
            if (newValue instanceof String)
                SettingsValue.onAppListCellYPreferenceChange(preference, (String) newValue, this);
            return true;
        }
        return false;

    }

    /* RK_ID: RK_SETTINGS. AUT: liuli1 . DATE: 2012-06-08 . START */
    // because there is a conflict between FragmentManagerImpl.checkStateLoss
    // and onBackPressed.
    // if activity had called onPaused, we won't call onBackPressed to avoid it
    boolean activityActive = true;

    protected void onResume() {
        super.onResume();
        activityActive = true;
      //keep it for later
        /*int newtagindex = 2;
        String orderInParent = "03";
        SettingsValue.addorRemoveAllNewTag(this, newtagindex,orderInParent);*/
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
    /* RK_ID: RK_SETTINGS. AUT: liuli1 . DATE: 2012-06-08 . END */
    
    /*RK_TEST dining 2013-6-14 S*/
    private void removeNoNeedSettings(){
    	//RK_TEST dining 2013-06-14
    	// remove some items 
    	PreferenceScreen pg = (PreferenceScreen)findPreference("pref_personalized_screen");
    	
	   	Preference tempObj = findPreference("pref_applist_y_count");
	   	if(tempObj != null){
			pg.removePreference(tempObj);
	   	}
			
		
		
    }
}
