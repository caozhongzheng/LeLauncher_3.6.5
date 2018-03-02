package com.lenovo.lejingpin.settings;


import java.io.File;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.commonui.LeAlertDialog;
import com.lenovo.launcher2.commonui.LeProcessDialog;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.Utilities;
import com.lenovo.launcher2.settings.BaseSettingActivty;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LejingpingSettings extends BaseSettingActivty implements OnPreferenceChangeListener {
	private Context mContext;
	private SharedPreferences mSharedPreferences;
	
	private static final String TAG = "LejingpingSettings";
	private static final int MSG_SHOW_CLEAR_CACHE_DIALOG = 1 ;
	
	private static final int MSG_AKEY_INSTALL_SHOW_FAIL =2 ;
	
	private static final int MSG_REMOVE_PROGRESSDIALOG = 3;
		
	private static final int SHOW_PROGRESS_DIALOG = 4;
	
	private static final String cachePath = Environment.getExternalStorageDirectory()+"/.IdeaDesktop/LeJingpin/wallpapers/";
	private static final String cachePath2 = Environment.getExternalStorageDirectory()+"/.IdeaDesktop/LeJingpin/themes/";
	
	private Toast toast = null;
	private View toastView = null;
	
	private LeProcessDialog mPdialog;	
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			
			switch (msg.what){
			
			case MSG_SHOW_CLEAR_CACHE_DIALOG:
				showClearCacheDialog();
				break;
				
			case MSG_AKEY_INSTALL_SHOW_FAIL:
				showAkeyInstallSetFailDialog();
	    		break;
	    		
	    		
			case MSG_REMOVE_PROGRESSDIALOG:				
				Log.i(TAG, "MSG_REMOVE_PROGRESSDIALOG");				
				if(LejingpingSettings.class!=null &&!LejingpingSettings.this.isFinishing()){
					Log.i(TAG, "class is not null");
					dismissDialog(SHOW_PROGRESS_DIALOG);
				}
				toastShow(getString(R.string.lejingpin_cacheclear_sucessed));				
				break;
				
			default :
				
				break;
			}
					
		}
	};
	
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
//		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		super.onCreate(arg0);		
		addPreferencesFromResource(R.xml.lejingpin_settings);		
		setLejingpinSettingsPreferences();		
		//setContentView(R.layout.lejingpin_setup_pererence_layout);		
		ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(  ActionBar.DISPLAY_USE_LOGO | ActionBar.DISPLAY_SHOW_HOME |  ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);

	}
	
	
	private void setLejingpinSettingsPreferences(){
		mContext = this;
		
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		SwitchPreference wlanDownload = (SwitchPreference) findPreference(LejingpingSettingsValues.KEY_WLAN_DOWNLOAD);
        wlanDownload.setChecked(LejingpingSettingsValues.wlanDownloadValue(this));
        wlanDownload.setOnPreferenceChangeListener(this);
    	
        Preference mClearCache = findPreference(LejingpingSettingsValues.KEY_CLEAR_CACHE);
        mClearCache.setOnPreferenceClickListener(new OnPreferenceClickListener() {			
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				// TODO Auto-generated method stub
				mHandler.removeMessages(MSG_SHOW_CLEAR_CACHE_DIALOG);
				mHandler.sendEmptyMessage(MSG_SHOW_CLEAR_CACHE_DIALOG);
				return false;
			}
		});
        
        
//        SwitchPreference previewDownload = (SwitchPreference) findPreference(LejingpingSettingsValues.KEY_PREVIEW_DOWNLOAD);
//        previewDownload.setChecked(LejingpingSettingsValues.previewDownloadValue(this));
//        previewDownload.setOnPreferenceChangeListener(this);
//        
        
        
        
//        CheckBoxPreference akeyInstall = (CheckBoxPreference) findPreference(SettingsValue.PREF_AKEY_INSTALL_SET);
//        akeyInstall.setChecked(SettingsValue.isAKeyInstall(this));
//        akeyInstall.setOnPreferenceChangeListener(this);
        
        Log.i(TAG, "check wifi produce");
        if(SettingsValue.isWifiProduct()){
        	Log.i(TAG, "wifiProduct");
        	removeNoNeedSettings();
        }
	}
	
	
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
        if (LejingpingSettingsValues.KEY_WLAN_DOWNLOAD.equals(preference.getKey())) {
            boolean wLanDownloadvalue = (Boolean) newValue;
        	((SwitchPreference)preference).setChecked(wLanDownloadvalue);
        	Intent mIntent = new Intent(LejingpingSettingsValues.WLAN_VALUE_CHANGED_ACTION);
        	mIntent.putExtra(LejingpingSettingsValues.WLAN_VALUE_KEY,wLanDownloadvalue);
        	sendBroadcast(mIntent);
        	Log.i(TAG, "send wlan value change intent");
			return true;
        }
        
         if (LejingpingSettingsValues.KEY_PREVIEW_DOWNLOAD.equals(preference.getKey())) {
            boolean previewDownload = (Boolean) newValue;
        	((SwitchPreference)preference).setChecked(previewDownload);
        	
			return true;
        }
        
/*        else if (SettingsValue.PREF_AKEY_INSTALL_SET.equals(preference.getKey())) {
            boolean isAkeyInstall = (Boolean) newValue;

        	if( isAkeyInstall){
        		int akeyInstallType = Utilities.canAKeyIntall(this);
        		if( akeyInstallType == Utilities.INSTALL_TYPE_NOT_AKEY){
        			isAkeyInstall = false;
        			SettingsValue.setAKeyInstall(this, isAkeyInstall);
                	mHandler.removeMessages(MSG_AKEY_INSTALL_SHOW_FAIL);
                	mHandler.sendEmptyMessage(MSG_AKEY_INSTALL_SHOW_FAIL);
                	return false;
        		}
        	}
            SettingsValue.setAKeyInstall(this, isAkeyInstall);
            return true;
        }*/
        
        
		
		return false;
	}
		
    @Override
   public boolean onOptionsItemSelected(MenuItem item) {
    	
    	
      	if(item.getItemId() == android.R.id.home ){
                finish();
                return true;            
        }   
       return true;
   }
		
    private void showClearCacheDialog(){
    	LeAlertDialog mAlertDialog = new LeAlertDialog(mContext,
				R.style.Theme_LeLauncher_Dialog_Shortcut);
		mAlertDialog.setLeTitle(R.string.lejingpin_settings_clearcache_dialog_title);
		mAlertDialog.setLeMessage(R.string.lejingpin_settings_clearcache_dialog_content);
		mAlertDialog.setOnKeyListener(new OnKeyListener() {
              @Override
              public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                  if (keyCode == KeyEvent.KEYCODE_BACK) {
                      dialog.cancel();
                  }
                  return false;
              }
          });
		
		
		mAlertDialog.setLePositiveButton(
				mContext.getString(R.string.dialog_confirm),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
		                  //TODO
						
						
						File mFile = new File(cachePath);
						File mFile2 = new File(cachePath2);
						if(mFile.exists() || mFile2.exists()){						
							//mPdialog = ProgressDialog.show(mContext, getString(R.string.lejingpin_cacheclear_progress_title), getString(R.string.lejingpin_cacheclear_progress_content),false,false);
							showDialog(SHOW_PROGRESS_DIALOG);
							
							new Thread(){
								public void run() {
									try {
										DeleteCashNew(cachePath);
										DeleteCashNew(cachePath2);
										mHandler.removeMessages(MSG_REMOVE_PROGRESSDIALOG);
										mHandler.sendEmptyMessage(MSG_REMOVE_PROGRESSDIALOG);
									} catch (Exception e) {			
										Log.i(TAG, "getException :"+e.toString());
										mHandler.removeMessages(MSG_REMOVE_PROGRESSDIALOG);
										mHandler.sendEmptyMessage(MSG_REMOVE_PROGRESSDIALOG);
									}
									
								};
							}.start();
						}else{
							
							toastShow(getString(R.string.lejingpin_cacheclear_sucessed));
						}	
					}
				});
		
		mAlertDialog.setLeNegativeButton(
				mContext.getString(R.string.dialog_cancle),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						dialog.cancel();
		                  //TODO
					}
				});
		
		
		mAlertDialog.show();
    }
	
	
    private void showAkeyInstallSetFailDialog(){
    	LeAlertDialog mAlertDialog = new LeAlertDialog(mContext,
				R.style.Theme_LeLauncher_Dialog_Shortcut);
		mAlertDialog.setLeTitle(R.string.check_root_title);
		mAlertDialog.setLeMessage(R.string.check_root_fail);
		mAlertDialog.setOnKeyListener(new OnKeyListener() {
              @Override
              public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                  if (keyCode == KeyEvent.KEYCODE_BACK) {
                      dialog.cancel();
                  }
                  return false;
              }
          });
		mAlertDialog.setLeNegativeButton(
				mContext.getString(R.string.dialog_confirm),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
		                   dialog.cancel();
					}
				});
		mAlertDialog.show();
    }
	
    
    
    
	public void DeleteCash(String sPath) {
		
		File file = new File(sPath);
		
		if(!file.exists()){
			mHandler.removeMessages(MSG_REMOVE_PROGRESSDIALOG);
			mHandler.sendEmptyMessage(MSG_REMOVE_PROGRESSDIALOG);
		}
		
		
		if (file.isFile()) {
			deletefile(sPath);
			mHandler.removeMessages(MSG_REMOVE_PROGRESSDIALOG);
			mHandler.sendEmptyMessage(MSG_REMOVE_PROGRESSDIALOG);
			
		} else {
			deleteDirectory(sPath);
			
			Log.i(TAG, "step this");
			File checkFile = new File(sPath);
			
			if(!checkFile.exists()){
				Log.i(TAG, "delete ok");
				mHandler.removeMessages(MSG_REMOVE_PROGRESSDIALOG);
				mHandler.sendEmptyMessage(MSG_REMOVE_PROGRESSDIALOG);
			}else{
				DeleteCash(sPath);
			}
			
		}

	}
	
	
	public void DeleteCashNew(String sPath) {
		
		File file = new File(sPath);
		
		if(!file.exists()){
			return;
		}
		
		if (file.isFile()) {
			deletefile(sPath);
			return;
		} else {
			deleteDirectory(sPath);
			Log.i(TAG, "step this");
			File checkFile = new File(sPath);
			if(!checkFile.exists()){
				Log.i(TAG, "delete ok");
				return;
			}else{
				DeleteCash(sPath);
				return;
			}
		}
	}
    
	
	private void deletefile(String sPath) {

		File mFile = new File(sPath);
		if (mFile.isFile() && mFile.exists()) {
			mFile.delete();
			

		}

	}
    
    
	private void deleteDirectory(String sPath) {

		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);

		if (dirFile.exists() && dirFile.isDirectory()) {

			File[] files = dirFile.listFiles();
			for (int i = 0; i < files.length; i++) {

				if (files[i].isFile()) {
					deletefile(files[i].getAbsolutePath());

				} else {
					deleteDirectory(files[i].getAbsolutePath());

				}
			}

			dirFile.delete();

		}

	}
	
	
	
	private void toastShow(String text) {
		if (toast == null) {
			toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
			toastView = toast.getView();
		} else {
			if (toastView != null) {
				toast.setText(text);
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.setView(toastView);
			}
		}
		toast.show();
}
    
	
	
	
	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		if(id==SHOW_PROGRESS_DIALOG){
			//mPdialog = ProgressDialog.show(mContext, getString(R.string.lejingpin_cacheclear_progress_title), getString(R.string.lejingpin_cacheclear_progress_content),false,false);			
			mPdialog = new LeProcessDialog(mContext);
			mPdialog.setTitle(R.string.lejingpin_cacheclear_progress_title);
			mPdialog.setLeMessage(R.string.lejingpin_cacheclear_progress_content);
			mPdialog.show();
			
			return mPdialog;			
		}		
		return super.onCreateDialog(id);
	}
    
	
	
    private void removeNoNeedSettings(){
    	//RK_TEST dining 2013-06-14
    	// remove some items 
    	Log.i(TAG, "remove no needSetting");
    	PreferenceScreen pg = (PreferenceScreen)findPreference("lejingpin_preference_screen");
    	
	   	Preference tempObj = findPreference("key_wlan_download_slide");
	   	if(tempObj != null){
	   		Log.i(TAG, "real move");
			pg.removePreference(tempObj);
	   	}
			
		
		
    }
	
	

}
