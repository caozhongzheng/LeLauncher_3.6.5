package com.lenovo.lejingpin.settings;

import java.util.List;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.commonui.LeAlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;


public class LejingpingSettingsValues {
	
	private static final String TAG = "LejingpingSettingsValues";
	
	private LejingpingSettingsValues(){
		
	}
	
	public static final String KEY_WLAN_DOWNLOAD = "key_wlan_download_slide";
	
	public static final String KEY_CLEAR_CACHE = "key_clear_cache";
	
	
	public static final String KEY_PREVIEW_DOWNLOAD = "key_preview_download_slide";
	
	public static final String KEY_DOWNLOAD_SAVE_PATH = "key_downloadfiles_save";
	
	public static final String WLAN_VALUE_CHANGED_ACTION = "com.lenovo.lejingpin.settings.wlanvalue";
	
	public static final String WLAN_VALUE_KEY="wlanValue";
	
	
	public static final String KEY_GO_LOCALLOCK = "key_go_locallock";
	
	public static final String KEY_LOCK_USE = "key_lock_slide";
	
	public static final String KEY_LOCK_SHAKE = "key_lockshake_slide";
	
	public static final String KEY_LOCK_VOICE = "key_lockvoick_slide";
	
	
	public static boolean wlanDownloadValue(Context context) {

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		boolean isWlanDownload = sp.getBoolean(KEY_WLAN_DOWNLOAD, false);
		return isWlanDownload;
	}
	
	
	public static boolean previewDownloadValue(Context context) {

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		boolean isPreviewDownload = sp.getBoolean(KEY_PREVIEW_DOWNLOAD, true);
		return isPreviewDownload;
	}
	
	
	
	 public static void popupWlanDownloadDialog(final Context context) {

	    	LeAlertDialog alertDialog = new LeAlertDialog(context,R.style.Theme_LeLauncher_Dialog_Shortcut);
	    	alertDialog.setLeTitle(R.string.lejingpin_wlan_download_dialog_title);
	    	alertDialog.setLeMessage(R.string.lejingpin_wlan_download_dialog_body);
	    	alertDialog.setLeNegativeButton(context.getString(R.string.lejingpin_wlan_download_dialog_btn_cancel),new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	                dialog.dismiss();
	                
	            }
	        });
	        alertDialog.setLePositiveButton(context.getString(R.string.lejingpin_wlan_download_dialog_btn_confirm),new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            	startLejingpinSettings(context);
	                dialog.dismiss();
	            }
	        });
	    	alertDialog.setOnCancelListener( new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
	                dialog.dismiss();
	                
				}
	        	
	        });
	    	alertDialog.show();
		 
	 }
	
	 public static void startLejingpinSettings(Context context){
	    	Intent mIntent = new Intent(context, LejingpingSettings.class);   	
	    	context.startActivity(mIntent);
	    	
	 }
	
	 
	 public static boolean useJinglingLockValue(Context context) {

//			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
//			boolean isUseLejingpinLock = sp.getBoolean(KEY_LOCK_USE, false);
		 	boolean isUseLejingpinLock = false;
		 	int result = 0;
			try {
				result = Settings.System.getInt(context.getContentResolver(), "isLockOpen");
				Log.i(TAG, "lock result is:"+result);
			} catch (SettingNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.i(TAG, "cat islockopen exception");
				return isUseLejingpinLock;
			}
		 	if(result==0){
		 		isUseLejingpinLock = false;
		 	}else if(result==1){
		 		isUseLejingpinLock = true;
		 	}
		 
			return isUseLejingpinLock;
	 }
	 
	 public static boolean LockShakeValue(Context context) {

//			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
//			boolean isLockShake = sp.getBoolean(KEY_LOCK_SHAKE, false);
		 	boolean isLockShake = false;
		 	int result = 0;
			try {
				result = Settings.System.getInt(context.getContentResolver(), "isOpenVibrate");
				Log.i(TAG, "shake result is:"+result);
			} catch (SettingNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.i(TAG, "cat LockShakeValue exception");
				
				if(findJinglingAppList(context)){
					isLockShake =true;
				}
				
				return isLockShake;
			}
		 	if(result==0){
		 		isLockShake = false;
		 	}else if(result==1){
		 		isLockShake = true;
		 	}
		 	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
			boolean isWlanDownload = sp.getBoolean(KEY_LOCK_USE, true);
		 	if(!isWlanDownload){
		 		isLockShake = false;
		 	}
		 	
			return isLockShake;
	 }
	 
	 
	 public static boolean LockVoiceValue(Context context) {

//			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
//			boolean isLockVoice = sp.getBoolean(KEY_LOCK_VOICE, false);
		 	boolean isLockVoice = false;
		 	int result = 0;
			try {
				result = Settings.System.getInt(context.getContentResolver(), "isOpenSound");
				Log.i(TAG, "voice result is:"+result);
			} catch (SettingNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.i(TAG, "cat lockvoice exception");
				if(findJinglingAppList(context)){
					isLockVoice =true;
				}
				return isLockVoice;
			}
		 	if(result==0){
		 		isLockVoice = false;
		 	}else if(result==1){
		 		isLockVoice = true;
		 	}
		 	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
			boolean isWlanDownload = sp.getBoolean(KEY_LOCK_USE, true);
		 	if(!isWlanDownload){
		 		isLockVoice = false;
		 	}
		 
			return isLockVoice;
	 }
	
	 
	 
	 private static  boolean  findJinglingAppList(Context context) {
			final Intent bmainIntent = new Intent(Intent.ACTION_MAIN);
			bmainIntent.addCategory("android.service.famelock");
			List<ResolveInfo> mLockAPKList ;
			mLockAPKList = context.getPackageManager().queryIntentActivities(
					bmainIntent, 0);
			if(mLockAPKList!= null && mLockAPKList.size()!=0){
				return true;
			}else{
				return false;
			}
			
		}
	
}
