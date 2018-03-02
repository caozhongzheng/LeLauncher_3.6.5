/*
 * Copyright (C) 2012
 *
 * RK_ID: RK_SETTINGS. AUT: liuli1. DATE: 2012-04-06
 * add activity for special features settings
 */
package com.lenovo.launcher2.settings;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.customizer.SettingsValue;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.util.Log;

public class SpecialFeatureSettings extends BaseSettingActivty implements OnPreferenceChangeListener {
    private static final String TAG = "SpecialFeatureSettings";
    /*RK_ID: RK_ACTION_BAR . AUT: zhanggx1 . DATE: 2012-05-15 . S*/
    private ActionBar mActionBar;
    /*RK_ID: RK_ACTION_BAR . AUT: zhanggx1 . DATE: 2012-05-15 . E*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferences();
        setContentView(R.layout.setup_pererence_layout);
        init();
    }

    @SuppressWarnings("deprecation")
    private void init() {
        CheckBoxPreference setPersistentOnOff = (CheckBoxPreference) findPreference(SettingsValue.KEY_ALWAYS_ALIVE_ONOFF);
        setPersistentOnOff.setOnPreferenceChangeListener(this);
        
        /*RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2012-04-01 . S*/
        CheckBoxPreference dyncThemeChange = (CheckBoxPreference) findPreference(SettingsValue.PREF_DYNC_THEME_CHANGE);
        dyncThemeChange.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue instanceof Boolean) {
                    SettingsValue.enableDyncThemeChange((Boolean) newValue);
                }
                return true;
            }
        });
        /*RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2012-04-01 . E*/
    }

    @SuppressWarnings("deprecation")
    private void addPreferences() {
        addPreferencesFromResource(R.xml.special_features_settings);
        /*RK_ID: RK_ACTION_BAR . AUT: zhanggx1 . DATE: 2012-05-15 . S*/
        mActionBar = getActionBar();
		mActionBar.setTitle(R.string.settings_pref_special_title);
		/*RK_ID: RK_ACTION_BAR . AUT: zhanggx1 . DATE: 2012-05-15 . E*/
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.i(TAG, "onPreferenceChange(): Preference - " + preference + ", newValue - " + newValue
                + ", newValue type - " + newValue.getClass());

        if (preference.getKey().equals(SettingsValue.KEY_ALWAYS_ALIVE_ONOFF)) {
            boolean persistent = Boolean.valueOf(newValue + "");
            Intent i = new Intent(SettingsValue.ACTION_PERSISTENT_CHANGED);
            i.putExtra(SettingsValue.EXTRA_PERSISTENT_ENABLED, persistent);
            sendBroadcast(i);
            return true;
        }

        return false;
    }
}
