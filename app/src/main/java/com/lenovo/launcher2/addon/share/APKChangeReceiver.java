package com.lenovo.launcher2.addon.share;

import java.util.ArrayList;
import java.util.HashMap;

import com.lenovo.launcher2.settings.LeosAppSettingInfo;
import com.lenovo.launcher2.settings.LeosAppSettingUtilities;
import com.lenovo.launcher2.settings.SeniorSettings;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.lejingpin.hw.lcapackageinstaller.utils.LcaInstallerUtils;
import com.lenovo.lejingpin.share.download.DownloadConstant;





import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
/**
 * Author : Liuyg1@lenovo.com
 * */
public class APKChangeReceiver extends BroadcastReceiver {
	private static final String TAG = "share_activity";
	Uri mUri;
	Context mContext;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		mContext = context;
		final String action = intent.getAction();

		if (action.equals("com.lenovo.launcher.START_SHARE_RECEIVE_ACTIVITY")) {
			Log.i(TAG, "onreceive START_SHARE_RECEIVE_ACTIVITY");
			//multiy
			ArrayList<Parcelable> list = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
									
			if (list != null) {
				Log.i(TAG, "list is not null");
				Bundle mBundle = intent.getBundleExtra("LAUNCHERBUNDLE");
				String [] packageNameArray = mBundle.getStringArray("PACKAGENAME");
				HashMap<String, String> testApkMap = new HashMap<String, String>();
				
				for(int i = 0 ;i<list.size();i++){
					Uri muri = (Uri) list.get(i);
					Log.i(TAG, "path is:"+muri.getPath());
					testApkMap.put(packageNameArray[i], muri.getPath());
					Log.i(TAG, "testApkMap size is:"+testApkMap.size());
				}

				if (mBundle.getString("CATEGORY").equals("app")) {
					
					Log.i(TAG, "installApplication");
					installApplication( packageNameArray , testApkMap);
					
				}else if(mBundle.getString("CATEGORY").equals("wallpaper")){
				}
			}
			//single
			Uri uri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
			
			if(uri!=null){
				
				Bundle mBundle = intent.getBundleExtra("LAUNCHERBUNDLE");
				
				if (mBundle.getString("CATEGORY").equals("app")) {
					
				}else if(mBundle.getString("CATEGORY").equals("wallpaper")){
					
				}
			}
		}
	}
	
	private void installApplication(String[] packageNameArray ,HashMap<String, String> testApkMap){
		Log.i(TAG, "for start");
		Log.i(TAG, "install path is:"+testApkMap.get(packageNameArray[0]));
		Log.i(TAG, "packageName is :"+packageNameArray[0]);
		LcaInstallerUtils.installApplication(mContext,testApkMap.get(packageNameArray[0]) , DownloadConstant.CATEGORY_LENOVO_APK, packageNameArray[0],"1000");
		
		
	}
}
