/*
 * Copyright (C) 2012
 *
 * RK_ID: RK_SETTINGS. AUT: liuli1. DATE: 2012-04-05
 * add activity for wallpaper settings
 */
package com.lenovo.launcher2.settings;

import com.google.android.mms.ContentType;
import com.lenovo.launcher.R;
import com.lenovo.launcher2.customizer.GlobalDefine;
import com.lenovo.launcher2.customizer.SettingsValue;

import android.app.ActionBar;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class WallpaperSettings extends BaseSettingActivty {
    private static final int REQUEST_PICK_WALLPAPER = 1;
    private static final int REQUEST_PICK_APPS_WALLPAPER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferences();

        ActionBar ab = getActionBar();
        ab.setTitle(R.string.settings_pref_wallpaper);
        setContentView(R.layout.setup_pererence_layout);
        init();
    }

    @SuppressWarnings("deprecation")
    private void init() {
        // set wallpaper preference
        Preference wallpaperPref = findPreference(SettingsValue.PREF_WALLPAPER);
        wallpaperPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
            	/*RK_ID: RK_WALLPAPER. AUT: shenchao. 2012-12-06. S*/
            	if(GlobalDefine.BU_VERSION){
            		final Intent pickWallpaper = new Intent(Intent.ACTION_SET_WALLPAPER);
                    Intent chooser = Intent.createChooser(pickWallpaper, getText(R.string.chooser_wallpaper));
                    startActivityForResult(chooser, REQUEST_PICK_WALLPAPER);
    			}else{
                    final Intent pickWallpaper = new Intent(WallpaperSettings.this,WallpaperChooserActivity.class);
                    //Intent chooser = Intent.createChooser(pickWallpaper, getText(R.string.chooser_wallpaper));
                    startActivityForResult(pickWallpaper, REQUEST_PICK_WALLPAPER);
                    /*RK_ID: RK_WALLPAPER. AUT: shenchao. 2012-12-06. E*/
//                  sendBroadcast(new Intent(SettingsValue.ACTION_START_WALLPAPER));
    			}
                return true;
            }
        });
        /*
        Preference appsWallpaperPref = findPreference(SettingsValue.PREF_APPS_WALLPAPER);
        appsWallpaperPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                try {
//                    sendBroadcast(new Intent(SettingsValue.ACTION_START_APPS_WALLPAPER));
                    int appsWallpaperWidth = getResources().getDimensionPixelOffset(R.dimen.apps_wallpaper_width);
                    int apps_wallpaper_height = getResources().getDimensionPixelOffset(R.dimen.apps_wallpaper_height);

                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
                    intent.setType(ContentType.IMAGE_UNSPECIFIED);

                    Uri appsWallpaperPath = SettingsValue.getAppsWallperTempPath(false);
                    if (appsWallpaperPath == null) {
                    	return false;
                    }

                    intent.putExtra("crop", "true");
                    intent.putExtra("outputX", appsWallpaperWidth);
                    intent.putExtra("outputY", apps_wallpaper_height);
                    intent.putExtra("aspectX", appsWallpaperWidth);
                    intent.putExtra("aspectY", apps_wallpaper_height);
                    intent.putExtra("scale", true);
                    intent.putExtra("noFaceDetection", true);
                    intent.putExtra("output", appsWallpaperPath);
                    intent.putExtra("outputFormat", "JPEG");
                    startActivityForResult(intent, REQUEST_PICK_APPS_WALLPAPER);
                } catch (ActivityNotFoundException e) {
                    return false;
                }
                return true;
            }
        });
        
        Preference resetAppsWallpaperPref = findPreference(SettingsValue.PREF_RESET_APPS_WALLPAPER);
        resetAppsWallpaperPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                try {
                    sendBroadcast(new Intent(SettingsValue.ACTION_RESET_APPS_WALLPAPER));
//                    finish();
                } catch (ActivityNotFoundException e) {
                    return false;
                }
                return true;
            }
        });
        */
        /* AUT: liuli liuli0120@use.com.cn DATE: 2012-02-01 START */
        CheckBoxPreference workspaceLoop = (CheckBoxPreference) findPreference(SettingsValue.PREF_WALLPAPER_SLIDE);
        workspaceLoop.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue instanceof Boolean) {
                    SettingsValue.enableWallpaperSlide((Boolean) newValue);
                }
                return true;
            }
        });
        /* AUT: liuli liuli0120@use.com.cn DATE: 2012-02-01 END */
    }

    @SuppressWarnings("deprecation")
    private void addPreferences() {
        addPreferencesFromResource(R.xml.personal_wallpaper_settings);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PICK_WALLPAPER) {
            Intent i = new Intent(SettingsValue.ACTION_START_WALLPAPER);
            i.putExtra(SettingsValue.KEY_WALLPAPER_SETTING_RESULTCODE, resultCode);
            sendBroadcast(i);
        } else if (requestCode == REQUEST_PICK_APPS_WALLPAPER && resultCode == RESULT_OK) {
            sendBroadcast(new Intent(SettingsValue.ACTION_START_APPS_WALLPAPER));
        }
    }

}
