/*
 * Copyright (C) 2011
 *
 * AUT: liuli liuli0120@use.com.cn DATE: 2011-12-20
 * add activity for theme settings
 */

package com.lenovo.launcher2.settings;

import java.util.ArrayList;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.SwitchPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.commonui.LeAlertDialog;
import com.lenovo.launcher2.customizer.APKChangeReceiver;
import com.lenovo.launcher2.customizer.Reaper;
import com.lenovo.launcher2.customizer.SettingsValue;

@SuppressWarnings("deprecation")
public class SeniorSettings extends BaseSettingActivty implements OnPreferenceChangeListener {
	private boolean mIsNetworkEnabled;
	private Context mContext;

    private static final int MSG_SEND_DATA_ACQU_ENABLER_CHANGED = 1;
    
    /** RK_ID: RK_LELAUNCHER_SEND_REPORT. AUT: zhangdxa DATE: 2012-02-23 S */
    private static boolean mIsSendExceptionEnabled = false;
    public static final String ACTION_SEND_EXCEPTION_CHANGED = "com.lenovo.action.ACTION_SEND_EXCEPTION_CHANGED";
    public static final String EXTRA_SEND_EXCEPTION_ENABLED = "send_exception_enabled";
    private static final int MSG_SEND_EXCEPTION_CHANGED = 100;
    /** RK_ID: RK_LELAUNCHER_SEND_REPORT. AUT: zhangdxa DATE: 2012-02-23 E */
    
	private APKChangeReceiver mApkChangeReceiver;
    public static final String ACTION_ENABLE_LEVOICE_CHANGED = "com.lenovo.action.ACTION_ENABLE_LEVOICE_CHANGED";
    public static final String EXTRA_ENABLE_LEVOICE_ENABLED = "levoice_enabled";


	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle icicle) {
		
		super.onCreate(icicle);

		ActionBar tmpBar = getActionBar();
		if( tmpBar != null){
			int options = tmpBar.getDisplayOptions();
			
			options = options ^ ActionBar.DISPLAY_HOME_AS_UP;
			tmpBar.setDisplayOptions( options );
		}else if(title != null && icon != null){
			getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		    title.setText(R.string.menu_desktop_settings);
		    //first activity don't show the icon
		    icon.setVisibility(View.GONE);
		}
		
		addPreferencesFromResource(R.xml.senior_settings);
		setDesktopPreferences();
		
		/***RK_ID:RK_LEOS_APP_SETTING AUT:zhanglz1@lenovo.com. DATE: 2013-03-11 S***/        
		mApkChangeReceiver  = new APKChangeReceiver();
        IntentFilter apkfilter = new IntentFilter();
        apkfilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        apkfilter.addAction(Intent.ACTION_INSTALL_PACKAGE);
        apkfilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        apkfilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        registerReceiver(mApkChangeReceiver, apkfilter);
               
	}

	private void setDesktopPreferences() {
        
        mContext = this;        
        
        SwitchPreference wallpaperLoop = (SwitchPreference) findPreference(SettingsValue.PREF_WALLPAPER_SLIDE);
        
        wallpaperLoop.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue instanceof Boolean) {
                    SettingsValue.enableWallpaperSlide((Boolean) newValue);
                }
                return true;
            }
        });
        
      //RK_NEW_LAYOUT dining 2013-07-03 S
        //特效设置中的选项
        ListPreference workspaceSlidePref = null;
        workspaceSlidePref = (ListPreference) findPreference(SettingsValue.PREF_WORKSPACE_SLIDE);
        
        if(workspaceSlidePref != null){
            String slide_value = SettingsValue.getWorkspaceSlideValue(this);
            workspaceSlidePref.setValue(slide_value);
            workspaceSlidePref.setSummary(workspaceSlidePref.getEntry());
            workspaceSlidePref.setOnPreferenceChangeListener(this);
        }
        // checkbox preference for whether workspace slide in loop
        SwitchPreference workspaceLoop = (SwitchPreference) findPreference(SettingsValue.PREF_WORKSPACE_LOOP);
        workspaceLoop.setSummaryOn(R.string.workspace_loop_on);
        workspaceLoop.setSummaryOff(R.string.workspace_loop_settings_off);
        workspaceLoop.setOnPreferenceChangeListener(this);
      //RK_NEW_LAYOUT dining 2013-07-03 E
        
        Preference versionUpdatePreference = findPreference(SettingsValue.PREF_VERSION_UPDATE);
        if(versionUpdatePreference!=null){  
        	String versionUpdateSummary = this.getString(R.string.lenovo_desktop_verion_title)
        			+": "
        			+getFormattedPackageVersion()
        			/*+"\n"+
        			this.getString(R.string.lenovo_desktop_release_time_title)
        			+": "
        			+getFormattedPackageDate()*/;
        	versionUpdatePreference.setSummary(versionUpdateSummary);
        	
        }
        //RK_LEOS_APP_SETTING,zhanglz1,20130311
        fetchLeosWidgetSettingInfo();

        removeNoNeedSettings();
	}
	
    @Override
    protected void onDestroy() {
        super.onDestroy();
       
        unregisterReceiver(mApkChangeReceiver);
        mApkChangeReceiver = null;
        mContext = null;
   
    }
    /* AUT: xingqx xingqx@lenovo.com DATE 2012.03.05 E */
    public boolean onPreferenceChange(Preference preference, Object newValue) {
    	//RK_NEW_LAYOUT dining 2013-07-03 S
        //特效设置中的选项
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
                Reaper.processReaper( mContext, 
                  	   Reaper.REAPER_EVENT_CATEGORY_EFFECT, 
          			   Reaper.REAPER_EVENT_ACTION_EFFECT_DESKTOPCYCLE,
          			   String.valueOf(workspaceSlideLoop),
          			   Reaper.REAPER_NO_INT_VALUE );
                /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-03-04 E*/
            }
            return true;
        }
      //RK_NEW_LAYOUT dining 2013-07-03 E
        return false;
        
	}
    /*AUT: xingqx xingqx@lenovo.com Date 2012.02.29 S*/    
    public static final boolean getDataAcquisitionEnabled(Context context) {

        return true;
    }
    /*AUT: xingqx xingqx@lenovo.com Date 2012.02.29 E*/    
    private Handler mHandler = new Handler() {
    	
    	@Override
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
    		
    		/** RK_ID: RK_LELAUNCHER_SEND_REPORT. AUT: zhangdxa DATE: 2012-02-23 S */
    		case MSG_SEND_EXCEPTION_CHANGED:
    			Log.i("zdx", "Launcher, SeniorSettings MSG_SEND_EXCEPTION_CHANGED, send exception: "+mIsSendExceptionEnabled);
    			Intent intent = new Intent(ACTION_SEND_EXCEPTION_CHANGED);
    			//zhanglz1 20130121
    			intent.putExtra(EXTRA_SEND_EXCEPTION_ENABLED, false/*mIsSendExceptionEnabled*/);
    			mContext.sendBroadcast(intent);
                break;	
    		/** RK_ID: RK_LELAUNCHER_SEND_REPORT. AUT: zhangdxa DATE: 2012-02-23 E */
    		/* AUT: xingqx xingqx@lenovo.com DATE 2012.02.29 S */
		    case MSG_SEND_DATA_ACQU_ENABLER_CHANGED:
				
				break;
			/* AUT: xingqx xingqx@lenovo.com DATE 2012.02.29 E */
			
         default:
    			break;
    		}
    	}
    };
    
    /** RK_ID: RK_LELAUNCHER_SEND_REPORT. AUT: zhangdxa DATE: 2012-02-23 S */
    public static final boolean getSendExceptionEnabled(Context context) {
    	SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    	mIsSendExceptionEnabled = sharedPreferences.getBoolean(SettingsValue.PREF_SEND_EXCEPTION, false);
    	//zhanglz1 20130121
    	//return mIsSendExceptionEnabled;
    	return false;
    }
    /** RK_ID: RK_LELAUNCHER_SEND_REPORT. AUT: zhangdxa DATE: 2012-02-23 E */

    

    /* RK_ID: RK_SETTING. AUT: liuli1 . DATE: 2012-03-22 . START */
    protected void showWarningDialog() {
    	LeAlertDialog mAlertDialog = new LeAlertDialog(mContext,
				R.style.Theme_LeLauncher_Dialog_Shortcut);
		mAlertDialog.setLeTitle(R.string.settings_network_dialog_title);
		mAlertDialog.setLeMessage(R.string.settings_network_dialog_message);
		mAlertDialog.setOnKeyListener(new OnKeyListener() {
              @Override
              public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                  if (keyCode == KeyEvent.KEYCODE_BACK) {
                      resetNetworkEnabled(false);
                      dialog.cancel();
                  }
                  return false;
              }
          });
		mAlertDialog.setLeNegativeButton(
				mContext.getString(R.string.btn_cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						resetNetworkEnabled(false);
		                 dialog.cancel();
					}
				});
		mAlertDialog.setLePositiveButton(
				mContext.getString(R.string.btn_ok),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
		                 setNetWorkEnable(true);
		                 dialog.dismiss();
					}
				});
		mAlertDialog.show();
    }

    protected void resetNetworkEnabled(final boolean b) {
        mIsNetworkEnabled = b;
        CheckBoxPreference preference = (CheckBoxPreference) findPreference(SettingsValue.PREF_NETWORK_ENABLER);
        preference.setChecked(mIsNetworkEnabled);

        new Thread(new Runnable() {
            public void run() {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
                editor.putBoolean(SettingsValue.PREF_NETWORK_ENABLER, b);
                editor.commit();
            }
        }, "resetNetworkEnabled").start();
    }

    protected void setNetWorkEnable(boolean b) {
        mIsNetworkEnabled = b;
        SettingsValue.enableNetwork(mIsNetworkEnabled);
        Log.i("LauncherSettings.Senior", "network ====== " + SettingsValue.isNetworkEnabled(SeniorSettings.this));

        /* RK_ID: RK_LOCK . AUT: zhanghong5 . DATE: 2012-02-17 . S */
        new Thread(new Runnable() {
            public void run() {
                if (SettingsValue.isNetworkEnabled(SeniorSettings.this))
                    Settings.System.putInt(mContext.getContentResolver(), SettingsValue.PREF_NETWORK_ENABLER, 1);
                else
                    Settings.System.putInt(mContext.getContentResolver(), SettingsValue.PREF_NETWORK_ENABLER, 0);
                Intent intent = new Intent(SettingsValue.ACTION_NETWORK_ENABLER_CHANGED);
                intent.putExtra(SettingsValue.EXTRA_NETWORK_ENABLED, SettingsValue
                        .isNetworkEnabled(SeniorSettings.this));
                mContext.sendBroadcast(intent);
            }
        }).start();
        /* RK_ID: RK_LOCK . AUT: zhanghong5 . DATE: 2012-02-17 . E */
    }
    /* RK_ID: RK_SETTING. AUT: liuli1 . DATE: 2012-03-22 . END */
    
//    public class NetworkEnablerChangedReceiver extends BroadcastReceiver {    	
//    	@Override
//    	public void onReceive(Context context, Intent intent) {
//    		// TODO Auto-generated method stub
//    		final String action = intent.getAction();
//    		if (SettingsValue.ACTION_NETWORK_ENABLER_CHANGED.equals(action)) {
//            	boolean networkEnabler = intent.getBooleanExtra(SettingsValue.EXTRA_NETWORK_ENABLED, false);
//            	boolean fromWeather = intent.getBooleanExtra(SettingsValue.EXTRA_ISFROM_WEATHER, false);
//            	if (fromWeather) {
//            		return;
//            	}
//            	CheckBoxPreference ckNetworkEnabler = (CheckBoxPreference) findPreference(SettingsValue.PREF_NETWORK_ENABLER);
//            	if (ckNetworkEnabler.isChecked() != networkEnabler) {
//            		ckNetworkEnabler.setChecked(networkEnabler);
//            	}
//            }
//    	}
//	}
    
//    private void showAkeyInstallSetFailDialog(){
//    	LeAlertDialog mAlertDialog = new LeAlertDialog(mContext,
//				R.style.Theme_LeLauncher_Dialog_Shortcut);
//		mAlertDialog.setLeTitle(R.string.check_root_title);
//		mAlertDialog.setLeMessage(R.string.check_root_fail);
//		mAlertDialog.setOnKeyListener(new OnKeyListener() {
//              @Override
//              public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                  if (keyCode == KeyEvent.KEYCODE_BACK) {
//                      dialog.cancel();
//                  }
//                  return false;
//              }
//          });
//		mAlertDialog.setLeNegativeButton(
//				mContext.getString(R.string.dialog_confirm),
//				new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog,
//							int whichButton) {
//		                   dialog.cancel();
//					}
//				});
//		mAlertDialog.show();
//    }
    
    /*RK_VERSION_WW dining 2012-10-22 S*/
    private void removeNoNeedSettings(){
    	//RK_TEST dining 2013-06-14
    	// remove some items 
    	PreferenceScreen pg = (PreferenceScreen)findPreference("pref_senior_screen");
    	
	   	Preference tempObj = findPreference("pref_key_classifyapps");
	   	if(tempObj != null){
			pg.removePreference(tempObj);
	   	}
			
		tempObj = findPreference("pref_lock_settings");
		if(tempObj != null){
			pg.removePreference(tempObj);
		}
		
    }
   
    private ArrayList<LeosAppSettingInfo> fetchLeosWidgetSettingInfo(){
        final ArrayList<LeosAppSettingInfo> widgetSettingInfos = LeosAppSettingUtilities.fetchAllInstalledAppSetting(this,LeosAppSettingUtilities.LENOVO_SETTING_WIDGET);
        ArrayList<LeosAppSettingInfo> leosAppSettingInfos = new ArrayList<LeosAppSettingInfo>();
        leosAppSettingInfos.addAll(widgetSettingInfos);
        return leosAppSettingInfos;
    }
//    public class appChangedReceiver extends BroadcastReceiver {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			// TODO Auto-generated method stub
//			final String action = intent.getAction();
//			if ( SettingsValue.ACTION_LEOS_APP_SETTING_REFRESH.equals(action)) {
//				PreferenceScreen pg = SeniorSettings.this.getPreferenceScreen();
//		        pg.removeAll();
//				addPreferencesFromResource(R.xml.senior_settings);
//		        addorRemoveLeosAppSetting(fetchLeosWidgetSettingInfo());
//				removeNoNeedSettings();
//			}
//		}
//	}
    @Override
    protected void onResume() {    	
        super.onResume();
    	//final ArrayList<LeosAppSettingInfo> leosAppSettingInfos = LeosAppSettingUtilities.getAppSettingInfos();
    	//addorRemoveLeosAppSetting(leosAppSettingInfos);
    	
    	/***RK_ID:RK_AUTO_NEW AUT:zhanglz1@lenovo.com DATE: 2013-04-17 S***/ 
    	int newtagindex = 1;
		//addorRemoveNewTag("pref_key_classifyapps");
    	String orderInParent = "-1";
    	SettingsValue.addorRemoveAllNewTag(this, newtagindex ,orderInParent);
    	/***RK_ID:RK_AUTO_NEW AUT:zhanglz1@lenovo.com DATE: 2013-04-17 E***/ 
    }

//    private void addorRemoveLeosAppSetting(ArrayList<LeosAppSettingInfo> leosAppSettingInfos){
//        boolean hasAppSetting = false;
//
//    	if(leosAppSettingInfos!=null && leosAppSettingInfos.size() >0) hasAppSetting = true;
//    	Preference appSettingPref =findPreference(SettingsValue.PREF_LEOS_APP_SETTING);
//
//		if (hasAppSetting && appSettingPref!=null) {
//
//		} else {
//			PreferenceScreen pg = (PreferenceScreen)findPreference("pref_senior_screen");
//			if(pg!=null && appSettingPref!=null){  
//				pg.removePreference(appSettingPref);
//				
//			}
//
//		}
//    }
    /***RK_ID:RK_LEOS_APP_SETTING AUT:zhanglz1@lenovo.com. DATE: 2013-03-11 E***/        
    private String getFormattedPackageVersion() {
		//String title = this.getString(R.string.lenovo_desktop_verion_summary);
		String appVersion = "1.0";
		PackageManager manager = this.getPackageManager();
		try { PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
		    appVersion = info.versionName;   //版本名
		} catch (NameNotFoundException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		//title =  title + appVersion;
		return appVersion;//change by xingqx 09.18 old is title
	}
	
//    private String getFormattedPackageDate() {
//        String appVersion = "v1.0";
//        PackageManager manager = this.getPackageManager();
//        try { PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
//            appVersion = info.versionName;   //版本名
//        } catch (NameNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        String pointStr = "_";
//        int firstIndex = appVersion.lastIndexOf(pointStr);
//        //bugfix outofbun..dry zhanglz1 20130117
//        int secondIndex = 0;
//        if(firstIndex>0){
//			String LastStr = appVersion.substring(0, firstIndex);
//			secondIndex = LastStr.lastIndexOf(pointStr);
//   	    }
//        String dateStr = "2012.03.15";
//        if(firstIndex > secondIndex
//           &&  secondIndex > 0
//           &&  appVersion.substring(secondIndex+1,firstIndex).length() == 6 ) {
//            dateStr =appVersion.substring(secondIndex+1,firstIndex);
//            dateStr = "20" + dateStr.substring(0,2)+"."+dateStr.substring(2, 4)+"."+dateStr.substring(4,6);
//        }
//        return dateStr;
//    }


}
