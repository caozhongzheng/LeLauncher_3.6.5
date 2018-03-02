package com.lenovo.lejingpin;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.provider.Settings;
import com.lenovo.lejingpin.hw.content.data.HwConstant;
import com.lenovo.lejingpin.share.download.DownloadConstant;
import com.lenovo.lejingpin.share.util.Utilities;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.customizer.Reaper;
import com.lenovo.launcher2.customizer.SettingsValue;
import android.content.pm.PackageManager;
import java.util.List;

import android.content.pm.ResolveInfo;


import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class APKInstallReceiver extends BroadcastReceiver {

    private LEJPConstant mLeConstant = LEJPConstant.getInstance();
    private final String TAG ="APKInstallReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        final String action = intent.getAction();
        Log.e("APKInstallReceiver","=====flag======="+mLeConstant.mLemActivityonResumeFlag+" action = "+action);
        if(action.equals("com.lenovo.launcher.Intent.ACTION_SCENE_APPLY")){
//        	android.os.Process.killProcess(android.os.Process.myPid());
        	Utilities.killLejingpinProcess();
        }
        if(intent.getData() != null){
        final String packageName = intent.getData().getSchemeSpecificPart();
        if (Intent.ACTION_PACKAGE_REPLACED.equals(action)
                || Intent.ACTION_PACKAGE_ADDED.equals(action)) {
        
            if (packageName == null || packageName.length() == 0 
            		|| packageName.equals(context.getPackageName())) {
                // they sent us a bad intent
                return;
            }
            String pkgtype = DetailClassicActivity.getDownloadPkgType(context, packageName);
            int type;
            if(pkgtype.equals("theme")){
            	type = 1;
            }else if(pkgtype.equals("lockscreen")){
            	type = 2;
            }else if(pkgtype.equals("wallpaper")){
            	type = 0;
            }else{
            	type = -1;
            }
             Log.i(TAG, "setApkInstalledFlag type is:"+type+" pkgtype===="+pkgtype+" packageName =============="+packageName);
            setApkInstalledFlag(type,packageName,context); 
            if(Intent.ACTION_PACKAGE_REPLACED.equals(action) && pkgtype.equals("theme")){
            	DetailClassicActivity.deleteLocalPreview(context, packageName, 11);
            }

        }else if(action.equals(Intent.ACTION_PACKAGE_REMOVED)){
            if (packageName == null 
                    || packageName.length() == 0) {
                // they sent us a bad intent
                return;
            }
            if (packageName.equals(LockScreenFragment.getCurrentLockScreenPak(context))) {
				LEJPConstant.getInstance().mCurrentLockscreen = "";
				ContentResolver contentResolver = context.getContentResolver();
				Settings.System.putInt(contentResolver,"lock_screen_on_off", 0);
				Settings.System.putString(contentResolver,"lock_setting_package_name" , "");
		    	Settings.System.putString(contentResolver, "lock_setting_class_name", "");
		    	Settings.System.putInt(contentResolver,"isLockOpen", 0);
			}
            setApkUninstalledFlag(packageName, context);
        }
        killMySelf();
        }else if( intent.getData() == null && (Intent.ACTION_PACKAGE_REPLACED.equals(action)
                || Intent.ACTION_PACKAGE_ADDED.equals(action) || Intent.ACTION_PACKAGE_REMOVED.equals(action))){
        Log.e("APKInstallReceiver","=====getData======="+intent.getData());
        killMySelf();
        }
        else if( !mLeConstant.mLemActivityonResumeFlag && DownloadConstant.ACTION_DOWNLOAD_STATE_CHANGED.equals(action)) {
             String pkg = intent.getStringExtra(DownloadConstant.EXTRA_PACKAGENAME);
             if("com.vlife.lenovo.wallpaper".equals(pkg)){
             //if("Lewallpaper".equals(pkg)){
                    Log.i(TAG, "avoid the lewallpaper in APKI 111111111111");
                    return;
             }
             String installPath = intent.getStringExtra(DownloadConstant.EXTRA_INSTALLPATH);
             Log.i(TAG, "installPath is:"+installPath);

             String status = intent.getStringExtra(DownloadConstant.EXTRA_STATUS);// 下载的状态
             String apkLocalPath = intent.getStringExtra(DownloadConstant.EXTRA_INSTALLPATH);
             int cat = intent.getIntExtra(DownloadConstant.EXTRA_CATEGORY, DownloadConstant.CATEGORY_ERROR);
             int category = DownloadConstant.getDownloadCategory(cat);
             Log.i(TAG, "status is:"+status + ", category = " + category);
             if(status.equals("uninstall")){
            	 //wallpaper download finished!
            	 if(category == DownloadConstant.CATEGORY_WALLPAPER || 
				 category == DownloadConstant.CATEGORY_LIVE_WALLPAPER ){
            		 DetailClassicActivity.setDownloadDataFlag(context, 0, pkg, apkLocalPath); //DOWNLOAD.xml
            		 DetailClassicActivity.saveDownloadInfo(context, DetailClassicActivity.SP_WALLPAPER_URL, 
            			 pkg, installPath);
            		 DetailClassicActivity.backupLocalWallpaperPreview(context, pkg);
            		 copySharedPreferencesToSDCard(context, DetailClassicActivity.SP_WALLPAPER_URL, 
            			 DetailClassicActivity.SP_WALLPAPER_URL);
            		 DetailClassicActivity.removeDownloadingStatus(context, 0, pkg);
            		 Log.i("mohl", "download wallpaper suc!  refresh!");
            		 Intent i = new Intent("refresh");
            		 i.putExtra("type", "wallpaper");
          	         context.sendBroadcast(i);
            	 }else if(category == DownloadConstant.CATEGORY_THEME){
            		 DetailClassicActivity.setDownloadDataFlag(context, 1, pkg, apkLocalPath); //DOWNLOAD.xml
            		 DetailClassicActivity.saveDownloadInfo(context, DetailClassicActivity.SP_THEME_URL, 
            			 pkg, installPath);
            		 DetailClassicActivity.removeDownloadingStatus(context, 1, pkg);
            	 }else if(category == DownloadConstant.CATEGORY_LOCKSCREEN){
            		 DetailClassicActivity.setDownloadDataFlag(context, 2, pkg, apkLocalPath); //DOWNLOAD.xml
            		 DetailClassicActivity.saveDownloadInfo(context, DetailClassicActivity.SP_LOCKSCREEN_URL, 
            			 pkg, installPath);
            		 DetailClassicActivity.removeDownloadingStatus(context, 2, pkg);
            	 }
             }
        }else if (DownloadConstant.ACTION_APK_FAILD_DOWNLOAD.equals(action)) {
        	String pkg = intent.getStringExtra(DownloadConstant.EXTRA_PACKAGENAME);
//    		String vcode = intent.getStringExtra(WidgetConstant.EXTRA_VERSION);
//    		String app_name = intent.getStringExtra(WidgetConstant.EXTRA_APPNAME);
//    		String category = intent.getStringExtra(HwConstant.EXTRA_CATEGORY);
    		int cat = intent.getIntExtra(DownloadConstant.EXTRA_CATEGORY, DownloadConstant.CATEGORY_ERROR);
    		int category = DownloadConstant.getDownloadCategory(cat);
    		int result = intent.getIntExtra(DownloadConstant.EXTRA_RESULT, DownloadConstant.FAILD_DOWNLOAD_OTHER_ERROR);
    		clearDownloadInfo(context, pkg, category);
    		switch(result){
	    		case DownloadConstant.FAILD_DOWNLOAD_NO_ENOUGHT_SPACE:
	    			Toast.makeText(context, R.string.failed_download_no_enough_space,
							Toast.LENGTH_SHORT).show();
	    			break;
	    		case DownloadConstant.FAILD_DOWNLOAD_NETWORK_ERROR:
	    			Toast.makeText(context, R.string.failed_download_network_error,
							Toast.LENGTH_SHORT).show();
	    			break;
	    		case DownloadConstant.FAILD_DOWNLOAD_OTHER_ERROR:
	    			Toast.makeText(context, R.string.failed_download_other_error,
							Toast.LENGTH_SHORT).show();
	    			break;
	    		default:
	    			break;
    		}
		}
        if( /*!mLeConstant.mLemActivityonResumeFlag && */mLeConstant.WALLPAPER_CHANGED_ACTION.equals(action)){
        	String wallpaper_name = intent.getStringExtra("name");
        	Log.e("mohl","---- action: WALLPAPER_CHANGED_ACTION,  wallpaper name = wallpaper_name");
        	DetailClassicActivity.setCurrentWallpaper(context, wallpaper_name);
        }else if(mLeConstant.LETHEME_APPLING_ACTION.equals(action)){
        	Log.e("mohl","---- action: LETHEME_APPLING_ACTION");
        	String theme = intent.getStringExtra(mLeConstant.LETHEME_PACKAGE_NAME);
        	final SharedPreferences preferences = context.getSharedPreferences("CURRENT", 0);
            final SharedPreferences.Editor editor = preferences.edit();
            String last_theme = preferences.getString("last_theme", "");
            Log.e("mohl","---- last theme: "+ last_theme+", theme = " + theme);
            if(theme != null && !last_theme.equals(theme)){
            	DetailClassicActivity.clearCurrentWallpaper(context);
            	editor.putString("last_theme", theme).commit();
            	Log.e("mohl","---- clear current theme");
            }
            if(!mLeConstant.mLeMainActivityonResumeFlag){
            	Log.e("mohl","---- kill lejingpin process");
//            	 android.os.Process.killProcess(android.os.Process.myPid());
            	Utilities.killLejingpinProcess();
            }
        }
        
        if(DownloadConstant.ACTION_DOWNLOAD_DELETE.endsWith(action)){
        	String pkg = intent.getStringExtra(DownloadConstant.EXTRA_PACKAGENAME);
    		int cat = intent.getIntExtra(DownloadConstant.EXTRA_CATEGORY, DownloadConstant.CATEGORY_ERROR);
    		int category = DownloadConstant.getDownloadCategory(cat);
    		Log.d("mohl","---- action: DownloadConstant ACTION_DOWNLOAD_DELETE,  pkg name = "+pkg+" category = "+category);
			clearDownloadInfo(context, pkg, category);
        }
    }
    private void killMySelf(){
        if(!mLeConstant.mLeMainActivityonResumeFlag){
            Log.e(TAG,"killMySelf  in apkinstallreceiver  <F2><F2><F2><F2><F2><F2><F2><F2><F2><F2><F2>");
//            android.os.Process.killProcess(android.os.Process.myPid());
            Utilities.killLejingpinProcess();
        }
    }

    private static boolean copySharedPreferencesToSDCard(Context context, String spName, String filename){
   	 LocalDataManager mgr = LocalDataManager.getInstance(context);
   	 boolean res = mgr.writeDataToSDCard(spName, filename);
   	 return res;
    }
    
    private void clearDownloadInfo(Context context, String pkgname, int category){
		//下载失败  删除配置文件相应项
		if(category == DownloadConstant.CATEGORY_WALLPAPER ||
		    category == DownloadConstant.CATEGORY_LIVE_WALLPAPER ){
			DetailClassicActivity.deleteLocalItem(context, pkgname, 10);
			DetailClassicActivity.removeDownloadingStatus(context, 0, pkgname);
			DetailClassicActivity.deleteDownloadDataFlag(context, pkgname, 10);
		}else if(category == DownloadConstant.CATEGORY_THEME){
			DetailClassicActivity.deleteLocalItem(context, pkgname, 11);
			DetailClassicActivity.removeDownloadingStatus(context, 1, pkgname);
			DetailClassicActivity.deleteDownloadDataFlag(context, pkgname, 11);
		}else if(category == DownloadConstant.CATEGORY_LOCKSCREEN){
			DetailClassicActivity.deleteLocalItem(context, pkgname, 12);
			DetailClassicActivity.removeDownloadingStatus(context, 2, pkgname);
			DetailClassicActivity.deleteDownloadDataFlag(context, pkgname, 12);
		}
    }
    
    private static String EXCLUDED_SETTING_KEY = "exclude_from_backup";
    private static void addExcludeSettingKey(SharedPreferences sp){
    	if(!sp.contains(EXCLUDED_SETTING_KEY)){
    		SharedPreferences.Editor editor = sp.edit();
    		editor.putBoolean(EXCLUDED_SETTING_KEY, true).commit();
    	}
    }
    
    private void setApkInstalledFlag(int type,String pkgname,Context context) {
        final SharedPreferences preferences = context.getSharedPreferences("InstallStatus", 0);
        final SharedPreferences.Editor editor = preferences.edit();
        addExcludeSettingKey(preferences);
//        if(type == 0){
	        editor.putBoolean(pkgname, true);
	        editor.commit();
//        }else{
//        }
        Log.e("APKInstallReceiver","setApkInstalledFalg true pkgname======"+pkgname+", type = "+type);
        if( mLeConstant.mLemActivityonResumeFlag ) {
	        Intent i = new Intent(mLeConstant.APK_INSTALL_ACTION);
	        i.putExtra("pkgname", pkgname);
	        context.sendBroadcast(i);
        }else{
        	Intent i = new Intent("refresh");
        	if(type == 1){
        		i.putExtra("type", "theme");
        	}else if(type == 2){
        		i.putExtra("type", "lockscreen");
        	}else if(type == 0){
        		i.putExtra("type", "wallpaper");
        	}else{
        		i.putExtra("type", "");
        	}
            context.sendBroadcast(i);
        }
        /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: mohl DATE: * 2013-1-7 S */
        if(type == 1){
        	Reaper.processReaper(context, "LeJingpin", "RecommThemeInstall",
        			pkgname, Reaper.REAPER_NO_INT_VALUE);
        }else if(type == 2){
			Reaper.processReaper(context, "LeJingpin", "RecommLKScreenInstall",
					pkgname, Reaper.REAPER_NO_INT_VALUE);
        }
		/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: mohl DATE: * 2013-1-7 E */
    }
    
    private void setApkUninstalledFlag(String pkgname, Context context){
    	 final SharedPreferences preferences = context.getSharedPreferences("InstallStatus", 0);
         final SharedPreferences.Editor editor = preferences.edit();
         editor.remove(pkgname).commit();
         
        Log.e("APKInstallReceiver","setApk UnInstalledFalg true pkgname======"+pkgname);
         if( mLeConstant.mLemActivityonResumeFlag ) {
 	        Intent i = new Intent(mLeConstant.APK_UNINSTALL_ACTION);
 	        i.putExtra("pkgname",pkgname);
 	        context.sendBroadcast(i);
         }else{
        	DetailClassicActivity.deleteLocalApk(context, pkgname);
         }

    }
    private void checkTheAPKType(Context mContext,String mPkgName){
        PackageManager mPackageManager = mContext.getPackageManager();

        List<ResolveInfo> mList = mPackageManager.queryIntentActivities(
                new Intent("android.service.lock"),
                PackageManager.GET_META_DATA);

        List<ResolveInfo> mFakeList = mPackageManager.queryIntentActivities(
                new Intent("android.service.fakelock"),
                PackageManager.GET_META_DATA);
        if(mList != null){
        mList.addAll(mFakeList);
        }else{
        mList = mFakeList;
        }
        if(mList != null){
        
        int listSize = mList.size();
        for (int i = 0; i < listSize; i++) {
            ResolveInfo resolveInfo = mList.get(i);

            if (resolveInfo.activityInfo.applicationInfo.packageName.equals(mPkgName))
            {
                mLeConstant.mIsLockAPKUninstallFlag = true;
            }
            Log.e("APKInstallReceiver","csetApkInstalledFalg pkgname======"+mPkgName);
        }
        }
        final Intent bmainIntent = new Intent(Intent.ACTION_MAIN, null);
        bmainIntent.addCategory(SettingsValue.THEME_PACKAGE_CATEGORY);
        final List<ResolveInfo> apps = mPackageManager.queryIntentActivities(bmainIntent, 0);
        if (apps != null) {
        int listSize = apps.size();
        for (int j = 0; j < listSize; j++) {
            ResolveInfo resolveInfo = apps.get(j);

            if (resolveInfo.activityInfo.applicationInfo.packageName.equals(mPkgName))
            {
                mLeConstant.mIsThemeAPKUninstallFlag = true;
            }
        }
        }else{
        }
    }


}
