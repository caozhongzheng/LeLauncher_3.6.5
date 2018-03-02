package com.lenovo.lejingpin.settings;


import com.lenovo.launcher.R;
import com.lenovo.launcher2.customizer.SettingsValue;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class LejingpingDownloadfilesSave extends PreferenceActivity implements OnPreferenceChangeListener {
	private Context mContext;
	private SharedPreferences mSharedPreferences;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(arg0);
		
		addPreferencesFromResource(R.xml.lejingpin_settings);
		
		//setLejingpinSettingsPreferences();
		
		setContentView(R.layout.lejingpin_setup_pererence_layout);
		
		TextView title = (TextView)findViewById(R.id.dialog_title);
		title.setText(R.string.menu_desktop_settings);
		ImageView icon = (ImageView)findViewById(R.id.dialog_icon);
		icon.setImageResource(R.drawable.preference_desk_setting);
		
	}
	
	/*
	private void setLejingpinSettingsPreferences(){
		mContext = this;
		
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		
		
        CheckBoxPreference wlanDownload = (CheckBoxPreference) findPreference(LejingpingSettingsValues.KEY_WLAN_DOWNLOAD);
        wlanDownload.setChecked(LejingpingSettingsValues.wlanDownloadValue(this));
        wlanDownload.setOnPreferenceChangeListener(this);
        
        
        
		
	}
	*/
	
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
        /*if (LejingpingSettingsValues.KEY_WLAN_DOWNLOAD.equals(preference.getKey())) {
            boolean wLanDownloadvalue = (Boolean) newValue;
        	((CheckBoxPreference)preference).setChecked(wLanDownloadvalue);
        	
			return true;
        }
        */
        
        
		
		
		return false;
	}
	
	
	
	
	
	

}
