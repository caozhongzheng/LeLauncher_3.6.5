package com.lenovo.launcher2.settings;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.customizer.Reaper;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.weather.widget.utils.WeatherUtilites;
import com.lenovo.launcher2.weather.widget.utils.Debug.R2;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class WeatherSettings extends BaseSettingActivty implements OnPreferenceChangeListener,OnPreferenceClickListener{
	private static final String PREF_WEATHER_ANIM_SETTING = "pref_weather_anim_setting";
	private CheckBoxPreference mdataAcquOnOff ;
	@Override
	protected void onCreate(Bundle icicle) {
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(icicle);
//		getActionBar().setTitle(R.string.weather_settings);
        if(!WeatherUtilites.findForPackage(this,WeatherUtilites.SINA_PACKAGENAME))
        	addPreferencesFromResource(R.xml.weather_settings);
        else
        	addPreferencesFromResource(R.xml.weather_sina_settings);
        title.setText(R.string.weather_settings);
        icon.setImageResource(R.drawable.dialog_title_back_arrow);
		


		mdataAcquOnOff = (CheckBoxPreference) findPreference(PREF_WEATHER_ANIM_SETTING);
		if(!WeatherUtilites.isNeedDownloadApp(this,WeatherUtilites.MPACKAGENAME,WeatherUtilites.MCLASSNAME))
			mdataAcquOnOff.setChecked(getweatheranimsettingenabled());
		else{
			mdataAcquOnOff.setChecked(false);
		}
		mdataAcquOnOff.setOnPreferenceClickListener(this);
		mdataAcquOnOff.setOnPreferenceChangeListener(this);
		/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2012-12-21 S*/    
        Reaper.processReaper( this, 
        	   Reaper.REAPER_EVENT_CATEGORY_DESKTOPSETTING, 
			   Reaper.REAPER_EVENT_ACTION_DESKTOPSETTING_WEATHER,
			   Reaper.REAPER_NO_LABEL_VALUE, 
			   Reaper.REAPER_NO_INT_VALUE );
        /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2012-12-21 E*/ 
	}
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
		if (preference.getKey().equals(PREF_WEATHER_ANIM_SETTING)) {
			boolean b = WeatherUtilites.isNeedDownloadApp(this,WeatherUtilites.MPACKAGENAME,WeatherUtilites.MCLASSNAME);
			if(b)
				return false;
			else{
				saveweatheranimsettingenabled((Boolean) newValue);
				return true;
			}
		}
		return false;
	}
    public boolean getweatheranimsettingenabled() {
      SharedPreferences sharedPreferences = getSharedPreferences(SettingsValue.PERFERENCE_NAME,
                                              MODE_APPEND | MODE_MULTI_PROCESS );
      return sharedPreferences.getBoolean(PREF_WEATHER_ANIM_SETTING, true);
    }
    public void saveweatheranimsettingenabled(boolean isanim) {
    	R2.echo("WeatherSettings isanim ="+isanim);
        SharedPreferences sharedPreferences = getSharedPreferences(SettingsValue.PERFERENCE_NAME,
                                                MODE_APPEND | MODE_MULTI_PROCESS );
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(PREF_WEATHER_ANIM_SETTING, isanim);
		editor.commit();
    }
	@Override
	public boolean onPreferenceClick(Preference preference) {
		// TODO Auto-generated method stub
		if (preference.getKey().equals(PREF_WEATHER_ANIM_SETTING)) {
			boolean b = WeatherUtilites.isNeedDownloadApp(this,WeatherUtilites.MPACKAGENAME,WeatherUtilites.MCLASSNAME);
			if(b){
				Intent intent = new Intent(Intent.ACTION_MAIN, null);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setPackage(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF);
				intent.setClassName(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF, 
						"com.lenovo.launcher2.weather.widget.NetworkAlertActivity");
				intent.putExtra(WeatherUtilites.EXTRA_DIALOG_TYPE, 1);
	            try{
	            	startActivity(intent);
	            }catch(ActivityNotFoundException e){
	            	e.printStackTrace();
	            }
	            return false;
			}else
				return true;
		}
		return false;
	}
}
