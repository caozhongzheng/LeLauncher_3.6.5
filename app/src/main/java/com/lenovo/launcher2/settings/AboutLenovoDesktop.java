/*
 * Copyright (C) 2011
 *
 * AUT: xingqx DATE: 2012-03.01
 * add activity for about lenovo desktop settings
 */

package com.lenovo.launcher2.settings;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.backup.BackupManager;
import com.lenovo.launcher2.commonui.LeAlertDialog;
import com.lenovo.launcher2.customizer.GlobalDefine;
import com.lenovo.launcher2.customizer.Debug.R2;


@SuppressWarnings("deprecation")
public class AboutLenovoDesktop extends BaseSettingActivty implements OnPreferenceChangeListener , OnPreferenceClickListener{
	private ActionBar mActionBar;
	private String PREF_PACKAGE_VERSION = "pref_lenovo_desktop_verion";
	private String PREF_HELP_VERSION = "pref_ideadesktop_help";
	private String PREF_DESKTOP_WEBSITE = "pref_lenovo_desktop_website";
	private String PREF_DESTTOP_DATE = "pref_lenovo_desktop_date";
	private String PREF_APP_LICENSE = "pref_lenovo_app_license";
	private String PREF_UE_PROMOT = "pref_lenovo_ue_promot";
	private String PREF_PRIVATE= "pref_lenovo_private";
	private static Preference webPref;

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
        addPreferencesFromResource(R.xml.about_lenovo_desktop_settings);
        if(title != null && icon != null){
		    title.setText(R.string.about_lenovo_desktop_settings);
		    icon.setImageResource(R.drawable.dialog_title_back_arrow);
        }
        setDesktopPreferences();


	}
   
    private void setDesktopPreferences() {
       
    	webPref = findPreference(PREF_DESKTOP_WEBSITE);
        webPref.setOnPreferenceClickListener((OnPreferenceClickListener) this);
        
    }
    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals(PREF_DESKTOP_WEBSITE)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://launcher.lenovo.com"));
            try {
    			startActivity(intent);
    		} catch (Exception e) {
    		    Toast.makeText(this, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
    		}
            return true;
        }
        return false;
    }
    
	/*AUT: xingqx xingqx@lenovo.com Date: 2012.03.20 e*/
	@Override
	public boolean onPreferenceChange(Preference arg0, Object arg1) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
