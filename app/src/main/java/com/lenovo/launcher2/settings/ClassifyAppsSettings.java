/*
 * Copyright (C) 2012
 *
 * AUT: gecn1@lenovo.com
 * DATE: 2013-03-24
 * add activity for apps classify
 */
package com.lenovo.launcher2.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.commonui.LeAlertDialog;
import com.lenovo.launcher2.customizer.Reaper;
import com.lenovo.launcher2.customizer.SettingsValue;

@SuppressWarnings("deprecation")
public class ClassifyAppsSettings extends BaseSettingActivty implements
		OnPreferenceChangeListener {
	
	
	
	Preference classificationApp;

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle icicle) {
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(icicle);

		addPreferencesFromResource(R.xml.classify_apps_settings);
		
		setDesktopPreferences();
		title.setText(R.string.workspace_classifyapps_title);
		icon.setImageResource(R.drawable.dialog_title_back_arrow);
		
        Reaper.processReaper( this, 
        	   Reaper.REAPER_EVENT_CATEGORY_DESKTOPSETTING, 
			   Reaper.REAPER_EVENT_ACTION_DESKTOPSETTING_CLEANUP,
			   Reaper.REAPER_NO_LABEL_VALUE, 
			   Reaper.REAPER_NO_INT_VALUE );
	}
	
	private void setDesktopPreferences() {        
        
        boolean autoClassifyValue = SettingsValue.isAutoAppsClassify(this);
        final CheckBoxPreference autoClassify = (CheckBoxPreference)findPreference(SettingsValue.PREF_AUTO_APPS_CLASSIFY);
        autoClassify.setChecked(autoClassifyValue);
        autoClassify.setOnPreferenceClickListener(new OnPreferenceClickListener(){

            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(SettingsValue.isFirstAutoUsedClassification(ClassifyAppsSettings.this)){
                    Resources res = ClassifyAppsSettings.this.getResources();
                    final LeAlertDialog ld = new LeAlertDialog(ClassifyAppsSettings.this,R.style.Theme_LeLauncher_Dialog_Shortcut);
                    ld.setLeTitle(R.string.workspace_classifyapps_title);
//                    LinearLayout alert = (LinearLayout) ClassifyAppsSettings.this.getLayoutInflater().inflate(R.layout.profile_backup_result, null);
//                    TextView tv = (TextView) alert.findViewById(R.id.profile_backup_hint);
//        	        tv.setText(R.string.close_apps_aotu_classify);
//        	        ld.setLeContentView(alert);
                    ld.setLeMessage(res.getString(R.string.close_apps_aotu_classify));
                    ld.setLeNegativeButton(res.getString(R.string.btn_cancel), new  DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ld.dismiss();
                            
                        }
                        
                    });
                    ld.setLePositiveButton(res.getString(R.string.btn_ok),  new  DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ld.dismiss();                            
                            SettingsValue.setFirstUsedAutoClassification(false, ClassifyAppsSettings.this);
                            boolean autoClassifyValue = (Boolean)autoClassify.isChecked();
                            SettingsValue.enableAutoAppsClassify(!autoClassifyValue, ClassifyAppsSettings.this);
                            autoClassify.setChecked(!autoClassifyValue);
                        }
                        
                    });
                    ld.show();
                    return false;
                }else{
                    boolean autoClassifyValue = (Boolean)autoClassify.isChecked();
                    SettingsValue.enableAutoAppsClassify(!autoClassifyValue, ClassifyAppsSettings.this);
                    autoClassify.setChecked(!autoClassifyValue);
                    return true;
                }

            }
            
        });
        autoClassify.setOnPreferenceChangeListener(this);
	    
	    
        classificationApp = findPreference("pref_workspace_classification");
        classificationApp.setOnPreferenceClickListener(new OnPreferenceClickListener(){

            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(SettingsValue.isFirstUsedClassification(ClassifyAppsSettings.this)){
                    Resources res = ClassifyAppsSettings.this.getResources();
                    final LeAlertDialog ld = new LeAlertDialog(ClassifyAppsSettings.this,R.style.Theme_LeLauncher_Dialog_Shortcut);
                    ld.setLeTitle(R.string.workspace_classifyapps_title);
                    ld.setLeMessage(res.getString(R.string.pref_workspace_classification_summary));
                    ld.setLeNegativeButton(res.getString(R.string.btn_cancel), new  DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            ld.dismiss();
                            
                        }
                        
                    });
                    ld.setLePositiveButton(res.getString(R.string.btn_ok),  new  DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SettingsValue.setFirstUsedClassification(false, ClassifyAppsSettings.this);
                            Intent intent = new Intent("com.lenovo.launcher.classification");
                            intent.setClassName(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF,"com.lenovo.launcher.components.XAllAppFace.XLauncher");
                            startActivity(intent);                            
                        }
                        
                    });
                    ld.show();
                    return true;
                }else{
                    Intent intent = new Intent("com.lenovo.launcher.classification");
                    intent.setClassName(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF,"com.lenovo.launcher.components.XAllAppFace.XLauncher");
                    startActivity(intent);
                    return true;
                }

            }
            
        });
        
	}
	
	@Override
	public boolean onPreferenceChange(Preference preference, Object value) {
	    if(SettingsValue.PREF_AUTO_APPS_CLASSIFY.equals(preference.getKey())){
            return false;
        }
		return false;
	}
	
}