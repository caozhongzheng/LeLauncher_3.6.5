/*
 * Copyright (C) 2011
 *
 * AUT: liuli liuli0120@use.com.cn DATE: 2011-12-20
 * add activity for theme settings
 */

package com.lenovo.launcher2.settings;

import java.util.List;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.commoninterface.LauncherService;
import com.lenovo.launcher2.customizer.GlobalDefine;
import com.lenovo.launcher2.customizer.Reaper;
import com.lenovo.launcher2.customizer.SettingsValue;


@SuppressWarnings("deprecation")
public class EffectSettings extends BaseSettingActivty implements OnPreferenceChangeListener {
	private ActionBar mActionBar;
    private String version = Build.VERSION.RELEASE;
    private Context mContext;

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle icicle) {
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(icicle);
  
		addPreferencesFromResource(R.xml.effect_settings);

		setDesktopPreferences();
		title.setText(R.string.effect_settings);
		icon.setImageResource(R.drawable.dialog_title_back_arrow);
	}

	private void setDesktopPreferences() {
		mContext = this;
		// list preference for workspace slide effect
	/*RK_ID: Jelly_Bean . AUT: yumina . DATE: 2012-09-10 . S*/
        ListPreference workspaceSlidePref = (ListPreference) findPreference(SettingsValue.PREF_WORKSPACE_SLIDE);
        String slide_value = SettingsValue.getWorkspaceSlideValue(this);
        workspaceSlidePref.setValue(slide_value);
        workspaceSlidePref.setSummary(workspaceSlidePref.getEntry());
        workspaceSlidePref.setOnPreferenceChangeListener(this);

        // checkbox preference for whether workspace slide in loop
        CheckBoxPreference workspaceLoop = (CheckBoxPreference) findPreference(SettingsValue.PREF_WORKSPACE_LOOP);
        workspaceLoop.setSummaryOn(R.string.workspace_loop_on);
        workspaceLoop.setSummaryOff(R.string.workspace_loop_settings_off);
        workspaceLoop.setOnPreferenceChangeListener(this);

        // list preference for app list enter/exit effect
//        ListPreference applistPref = (ListPreference) findPreference(SettingsValue.PREF_APPLIST_ENTER);
//        String app_enter_value = SettingsValue.getAppListEnterValue(this);
//        applistPref.setValue(app_enter_value);
//        applistPref.setSummary(applistPref.getEntry());
//        applistPref.setOnPreferenceChangeListener(this);

        // list preference for app list slide effect
//        ListPreference applistSlidePref = (ListPreference) findPreference(SettingsValue.PREF_APPLIST_SLIDE);
//        String app_slide_value = SettingsValue.getAppListSlideValue(this);
//        applistSlidePref.setValue(app_slide_value);
//        applistSlidePref.setSummary(applistSlidePref.getEntry());
//        applistSlidePref.setOnPreferenceChangeListener(this);
//	
//        ListPreference hotseatStylePref = (ListPreference) findPreference(SettingsValue.PREF_HOTSEAT_STYLE);
//        String hotseat_style = SettingsValue.getHotseatStyleValue(this);
//        hotseatStylePref.setValue(hotseat_style);
//        hotseatStylePref.setSummary(hotseatStylePref.getEntry());
//        hotseatStylePref.setOnPreferenceChangeListener(this);

//        ListPreference iconStylePref = (ListPreference) this.findPreference(SettingsValue.PREF_FOLDER_STYLE);
//        String icon_style = SettingsValue.getIconStyleValue(this);
//        iconStylePref.setValue(icon_style);
//        iconStylePref.setSummary(iconStylePref.getEntry());
//        iconStylePref.setOnPreferenceChangeListener(this);
//        
//        CheckBoxPreference applistLoop = (CheckBoxPreference) findPreference(SettingsValue.PREF_APPLIST_LOOP);
//        applistLoop.setSummaryOn(R.string.checkbox_open);
//        applistLoop.setSummaryOff(R.string.checkbox_Close);
//        applistLoop.setOnPreferenceChangeListener(this);
// 
        removeNoNeedSettings();
	}

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equals(SettingsValue.PREF_WORKSPACE_SLIDE)) {
            if (newValue instanceof String) {
                String sPrefWorkspaceSlide = (String) newValue;
				if (preference instanceof ListPreference)
					((ListPreference) preference).setValue(sPrefWorkspaceSlide);
                preference.setSummary(((ListPreference) preference).getEntry());
                SettingsValue.setWorkspaceSlide(sPrefWorkspaceSlide);
                SettingsValue.checkShowToast(sPrefWorkspaceSlide, this);
                /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-03-04 S*/
                Reaper.processReaper( mContext, 
                  	   Reaper.REAPER_EVENT_CATEGORY_EFFECT, 
          			   Reaper.REAPER_EVENT_ACTION_EFFECT_DESKTOPPAGE,
          			   sPrefWorkspaceSlide,
          			   Reaper.REAPER_NO_INT_VALUE );
                /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-03-04 E*/
            }
            return true;
        } else if (preference.getKey().equals(SettingsValue.PREF_WORKSPACE_LOOP)) {
            if (newValue instanceof Boolean) {
                boolean workspaceSlideLoop = (Boolean) newValue;
                Intent i = new Intent(SettingsValue.ACTION_WORKSPACE_LOOP);
                i.putExtra(SettingsValue.EXTRA_WORKSPACE_IS_LOOP, workspaceSlideLoop);
                this.sendBroadcast(i);
//                Launcher launcher = Launcher.getInstance();
//                if (launcher != null && launcher.getWorkspace() != null) {
//                    launcher.getWorkspace().mSlideLoop = sWorkspaceSlideLoop;
//                }
                /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-03-04 S*/
                Reaper.processReaper( mContext, 
                  	   Reaper.REAPER_EVENT_CATEGORY_EFFECT, 
          			   Reaper.REAPER_EVENT_ACTION_EFFECT_DESKTOPCYCLE,
          			   String.valueOf(workspaceSlideLoop),
          			   Reaper.REAPER_NO_INT_VALUE );
                /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-03-04 E*/
            }
            return true;
        } else if (preference.getKey().equals(SettingsValue.PREF_APPLIST_ENTER)) {
            if (newValue instanceof String) {
                String sPrefAppListEnter = (String) newValue;
                ((ListPreference) preference).setValue(sPrefAppListEnter);
                preference.setSummary(((ListPreference) preference).getEntry());
                SettingsValue.setAppListEnterValue(sPrefAppListEnter);
                /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-03-04 S*/
                Reaper.processReaper( mContext, 
                   	   Reaper.REAPER_EVENT_CATEGORY_EFFECT, 
           			   Reaper.REAPER_EVENT_ACTION_EFFECT_APPLISTENTER,
           			   sPrefAppListEnter,
           			   Reaper.REAPER_NO_INT_VALUE );
                 /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-03-04 E*/
            }
            return true;
        } else if (preference.getKey().equals(SettingsValue.PREF_APPLIST_SLIDE)) {
            SettingsValue.onAppListSlidePreferenceChange(preference, newValue, this);
            /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-03-04 S*/
            if (newValue instanceof String) {
            	Reaper.processReaper( mContext, 
                    	   Reaper.REAPER_EVENT_CATEGORY_EFFECT, 
            			   Reaper.REAPER_EVENT_ACTION_EFFECT_APPLISTFLIP,
            			   String.valueOf(newValue),
            			   Reaper.REAPER_NO_INT_VALUE );
            }
            /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-03-04 E*/
            return true;
        }       
    /* RK_ID: RK_MENU_RFACTOR . AUT: LIUYG1 . DATE: 2012-11-29 . S */
        else if (preference.getKey().equals(SettingsValue.PREF_FOLDER_STYLE)) {
            if (newValue instanceof String) {
                String sPrefIconStyle = (String) newValue;
                SettingsValue.setIconStyleValue(sPrefIconStyle);
                ((ListPreference) preference).setValue(sPrefIconStyle);
                preference.setSummary(((ListPreference) preference).getEntry());
                this.sendBroadcast(new Intent(SettingsValue.ACTION_TEXT_SIZE_CHANGED));
            }
            return true;
        } else if (preference.getKey().equals(SettingsValue.PREF_HOTSEAT_STYLE)) {
            if (newValue instanceof String) {
                String sPrefHotseatStyle = (String) newValue;
                SettingsValue.setHotseatStyleValue(sPrefHotseatStyle);
                ((ListPreference) preference).setValue(sPrefHotseatStyle);
                preference.setSummary(((ListPreference) preference).getEntry());
                this.sendBroadcast(new Intent(SettingsValue.ACTION_INDICATOR_CHANGED));
            }
            return true;
        }
       /* RK_ID: RK_MENU_RFACTOR . AUT: LIUYG1 . DATE: 2012-11-29 .E*/
        /*** RK_ID: APPLIST_LOOP.  AUT: zhaoxy . DATE: 2012-12-17 . START***/
        else if (preference.getKey().equals(SettingsValue.PREF_APPLIST_LOOP)) {
            SettingsValue.onApplistLoopChange(this.getApplicationContext(), newValue);
            return true;
        }
        /*** RK_ID: APPLIST_LOOP.  AUT: zhaoxy . DATE: 2012-12-17 . END***/

        return false;
    }
    
    private void removeNoNeedSettings(){
    	PreferenceScreen pg = (PreferenceScreen)findPreference("pref_effect_screen");
    	        	
	    	Preference tempObj = findPreference(SettingsValue.PREF_HOTSEAT_STYLE);
	    	if(tempObj != null){
			    pg.removePreference(tempObj);
	    	}
			
			tempObj = findPreference(SettingsValue.PREF_FOLDER_STYLE);
			if(tempObj != null){
			    pg.removePreference(tempObj);
			}
		
			tempObj = findPreference(SettingsValue.PREF_APPLIST_ENTER);
			if(tempObj != null){
			    pg.removePreference(tempObj);
			}

			tempObj = findPreference(SettingsValue.PREF_APPLIST_SLIDE);
			if(tempObj != null){
			    pg.removePreference(tempObj);
			}
			tempObj = findPreference(SettingsValue.PREF_APPLIST_LOOP);
			if(tempObj != null){
			    pg.removePreference(tempObj);
			}
			
			tempObj = (PreferenceCategory)findPreference("pref_key_effect_applist_settings");
			if(tempObj != null){
			    pg.removePreference(tempObj);
			}
    }
    //keep it for later
    /*@Override
    protected void onResume() {    	
        super.onResume();
        int newtagindex = 3;
        String orderInParent = "0301";
        SettingsValue.addorRemoveAllNewTag(this, newtagindex,orderInParent);
    }*/
}
