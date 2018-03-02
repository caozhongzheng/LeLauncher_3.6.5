/*
 * Copyright (C) 2012
 *
 * RK_ID: RK_SETTINGS. AUT: liuli1. DATE: 2012-04-05
 * add activity for personalized settings
 */
package com.lenovo.launcher2.settings;

import java.util.ArrayList;

import com.lenovo.launcher.R;

import com.lenovo.launcher2.commonui.LeAlertDialog;
import com.lenovo.launcher2.customizer.Reaper;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.Utilities;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.SwitchPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.SystemProperties;


public class MoreSettings extends BaseSettingActivty implements OnPreferenceChangeListener {
    private static final String TAG = "MoreSettings";

    private SharedPreferences mSharedPreferences;
	private Context mContext;
	private boolean mIsNetworkEnabled;
	private NetworkEnablerChangedReceiver mNetEnablerReceiver;
	
	private static final int MSG_SEND_NETWORK_ENABLER_CHANGED = 0;
    private static final int MSG_SEND_DATA_ACQU_ENABLER_CHANGED = 1;
    private static final int MSG_AKEY_INSTALL_SHOW_FAIL = 101;
    private static final int MSG_SEND_AUTO_REORDER_CHANGED = 2;
    
    //test by dining 2013-06-09
  	private InnerRecevier mRecevier;
  	private IntentFilter mFilter;
  	private LeAlertDialog mAlertDialog;
  	
    private ArrayList<LeosAppSettingInfo> mLeosAppSettingInfos;
    
    //add by zhanggx1 for reordering all pages on 2013-11-20. s
    private boolean mIsAutoReorderEnabled;    
    //add by zhanggx1 for reordering all pages on 2013-11-20. e
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	//getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    	
        super.onCreate(savedInstanceState);
        addPreferences();
       
        if(title != null && icon != null){
		    title.setText(R.string.more_settings_title);
		    icon.setImageResource(R.drawable.dialog_title_back_arrow);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       
        unregisterReceiver(mNetEnablerReceiver);
         
    }
    @SuppressWarnings("deprecation")
    private void addPreferences() {
        String s = SystemProperties.get("ro.build.product");
        Log.e("yumin0820","getSystemProperites="+s);

        if(s != null && s.contains("wifi")){
        addPreferencesFromResource(R.xml.more_settings_wifi);
        }else{

        addPreferencesFromResource(R.xml.more_settings);
        SwitchPreference networkEnable = (SwitchPreference) findPreference(SettingsValue.PREF_NETWORK_ENABLER);
        networkEnable.setChecked(SettingsValue.isNetworkEnabled(this));
        networkEnable.setOnPreferenceChangeListener(this);
        }

        //add by zhanggx1 for reordering all pages on 2013-11-20. s
        SwitchPreference autoReorderEnabled = (SwitchPreference) findPreference(SettingsValue.PREF_AUTO_REORDER);
        autoReorderEnabled.setChecked(SettingsValue.isAutoReorderEnabled(this));
        autoReorderEnabled.setOnPreferenceChangeListener(this);
        //add by zhanggx1 for reordering all pages on 2013-11-20. e
        
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        
        mContext = this;        
        
        mNetEnablerReceiver = new NetworkEnablerChangedReceiver();
        IntentFilter netEnablerFilter = new IntentFilter(SettingsValue.ACTION_NETWORK_ENABLER_CHANGED);
        registerReceiver(mNetEnablerReceiver, netEnablerFilter);
        
        
        //RK_CHECK_LEOSAPPSETTING dining 2013-07-04
        //这个操作必须在设置Preference之后，包括removeNoNeeding()之后\
        PreferenceScreen pg = (PreferenceScreen)findPreference("pref_moresetting_screen");
        if(pg != null){
           //int count =pg.getPreferenceCount();
           mLeosAppSettingInfos = LeosAppSettingUtilities.getAppSettingInfos();
           if(mLeosAppSettingInfos != null && mLeosAppSettingInfos.size() > 0){
        	   for(int i=0;i < mLeosAppSettingInfos.size();i++){
        		   Preference pTemp = new Preference(mContext);
        		   pTemp.setLayoutResource(R.layout.custom_preference);
        		   pTemp.setWidgetLayoutResource(R.layout.sec_pref_widget_more);
        		   pTemp.setTitle(mLeosAppSettingInfos.get(i).app_name);
        		   pTemp.setIntent(mLeosAppSettingInfos.get(i).app_setting_intent);
        		   
        		   pg.addPreference(pTemp);
        	   }
           }
        }
                   
        
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.i(TAG, "onPreferenceChange(): Preference - " + preference + ", newValue - " + newValue
                + ", newValue type - " + newValue.getClass());
        if (SettingsValue.PREF_NETWORK_ENABLER.equals(preference.getKey())) {
            mIsNetworkEnabled = (Boolean) newValue;
			if (preference instanceof SwitchPreference)
				((SwitchPreference) preference).setChecked(mIsNetworkEnabled);
        	mHandler.removeMessages(MSG_SEND_NETWORK_ENABLER_CHANGED);
        	mHandler.sendEmptyMessage(MSG_SEND_NETWORK_ENABLER_CHANGED);
            
        	return true;
        }
        //add by zhanggx1 for reordering all pages on 2013-11-20. s
        else if (SettingsValue.PREF_AUTO_REORDER.equals(preference.getKey())) {
        	mIsAutoReorderEnabled = (Boolean) newValue;
			if (preference instanceof SwitchPreference) {
				((SwitchPreference) preference).setChecked(mIsAutoReorderEnabled);
			}
        	mHandler.removeMessages(MSG_SEND_AUTO_REORDER_CHANGED);
        	mHandler.sendEmptyMessage(MSG_SEND_AUTO_REORDER_CHANGED);
        	return true;
        }
        //add by zhanggx1 for reordering all pages on 2013-11-20. e
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
    
    private Handler mHandler = new Handler() {
    	
    	@Override
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
    		case MSG_SEND_NETWORK_ENABLER_CHANGED:
                /* RK_ID: RK_SETTING. AUT: liuli1 . DATE: 2012-03-22 . START */
                if (mIsNetworkEnabled) {
                    showWarningDialog();
                } else {
                    setNetWorkEnable(mIsNetworkEnabled);
                }
                /* RK_ID: RK_SETTING. AUT: liuli1 . DATE: 2012-03-22 . END */
    			break;
    		//add by zhanggx1 for reordering all pages on 2013-11-20. s
    		case MSG_SEND_AUTO_REORDER_CHANGED:
    			setAutoReorderEnabled(mIsAutoReorderEnabled);
    			Intent intent = new Intent(SettingsValue.ACTION_DO_AUTO_REORDER);
    	        sendBroadcast(intent);
    			break;
    		//add by zhanggx1 for reordering all pages on 2013-11-20. e
         default:
    			break;
    		}
    	}
    };
    
    public class NetworkEnablerChangedReceiver extends BroadcastReceiver {    	
    	@Override
    	public void onReceive(Context context, Intent intent) {
    		// TODO Auto-generated method stub
    		final String action = intent.getAction();
    		if (SettingsValue.ACTION_NETWORK_ENABLER_CHANGED.equals(action)) {
            	boolean networkEnabler = intent.getBooleanExtra(SettingsValue.EXTRA_NETWORK_ENABLED, false);
            	boolean fromWeather = intent.getBooleanExtra(SettingsValue.EXTRA_ISFROM_WEATHER, false);
            	if (fromWeather) {
            		return;
            	}
            	SwitchPreference ckNetworkEnabler = (SwitchPreference) findPreference(SettingsValue.PREF_NETWORK_ENABLER);
            	if (ckNetworkEnabler.isChecked() != networkEnabler) {
            		ckNetworkEnabler.setChecked(networkEnabler);
            	}
            }
    	}
	}
    
    protected void showWarningDialog() {
    	
    	mAlertDialog = new LeAlertDialog(mContext,
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
		
		mAlertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
        	@Override
        	public void onCancel(DialogInterface  dialog) {

                resetNetworkEnabled(false);                
            }
        });
		
		mAlertDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				stopWatch();
				mRecevier = null;
				mFilter = null;
				mAlertDialog = null;
				Log.e("mAlertDialog", "onDismiss");
			}
		});
		
		mAlertDialog.setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {
				
				if(mRecevier == null){
					mRecevier = new InnerRecevier();
				}
				if(mFilter == null){
					mFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
				}
				startWatch();	

			}
		});
		
		mAlertDialog.show();
    }

    protected void resetNetworkEnabled(final boolean b) {
        mIsNetworkEnabled = b;
        SwitchPreference preference = (SwitchPreference) findPreference(SettingsValue.PREF_NETWORK_ENABLER);
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
        Log.i("LauncherSettings.Senior", "network ====== " + SettingsValue.isNetworkEnabled(MoreSettings.this));

        /* RK_ID: RK_LOCK . AUT: zhanghong5 . DATE: 2012-02-17 . S */
        new Thread(new Runnable() {
            public void run() {
                if (SettingsValue.isNetworkEnabled(MoreSettings.this))
                    Settings.System.putInt(mContext.getContentResolver(), SettingsValue.PREF_NETWORK_ENABLER, 1);
                else
                    Settings.System.putInt(mContext.getContentResolver(), SettingsValue.PREF_NETWORK_ENABLER, 0);
                Intent intent = new Intent(SettingsValue.ACTION_NETWORK_ENABLER_CHANGED);
                intent.putExtra(SettingsValue.EXTRA_NETWORK_ENABLED, SettingsValue
                        .isNetworkEnabled(MoreSettings.this));
                mContext.sendBroadcast(intent);
            }
        }).start();
        /* RK_ID: RK_LOCK . AUT: zhanghong5 . DATE: 2012-02-17 . E */
    }

    class InnerRecevier extends BroadcastReceiver {
		  final String SYSTEM_DIALOG_REASON_KEY = "reason";
		  final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

		  @Override
		  public void onReceive(Context context, Intent intent) {
		   String action = intent.getAction();
		   if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
		        String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
		        if (reason != null) {
		            if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
		            	if(mAlertDialog != null && mAlertDialog.isShowing()){
		            		resetNetworkEnabled(false);
		            		mAlertDialog.dismiss();
		                }
		            }
		     
		        }
		   }
		  }
		 
		}
    
  //test by dining 2013-06-09
  	private void startWatch() {
  		
  	    if (mRecevier != null && mAlertDialog != null && mFilter != null) {
  	    	mAlertDialog.getContext().registerReceiver(mRecevier, mFilter);
  	    	
  		}
      }
  	
  	public void stopWatch() {
  		if (mRecevier != null && mAlertDialog != null) {
  			mAlertDialog.getContext().unregisterReceiver(mRecevier);
  		 }
  	}
  	
  	//add by zhanggx1 for reordering all pages on 2013-11-20. s
  	protected void setAutoReorderEnabled(boolean b) {
        mIsAutoReorderEnabled = b;
        SettingsValue.enableAutoReorder(mIsAutoReorderEnabled);
    }
    //add by zhanggx1 for reordering all pages on 2013-11-20. e

}
