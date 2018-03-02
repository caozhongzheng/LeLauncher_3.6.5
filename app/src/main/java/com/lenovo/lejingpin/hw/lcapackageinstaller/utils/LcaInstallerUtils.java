package com.lenovo.lejingpin.hw.lcapackageinstaller.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.lejingpin.hw.lcapackageinstaller.LcaInstallerDownloadActivity;
import com.lenovo.lejingpin.hw.lcapackageinstaller.LcaInstallerService;
import com.lenovo.lejingpin.share.download.DownloadConstant;
import com.lenovo.lejingpin.share.download.DownloadInfo;



public class LcaInstallerUtils {
	
	private static final String TAG ="yangmao_download";
	
	private LcaInstallerUtils(){
		
	}
	
	public static void installApplication(Context context ,DownloadInfo data){ //,String installPath){
		
		int ca = DownloadConstant.getDownloadCategory(data.getCategory());
		if(ca == DownloadConstant.CATEGORY_WALLPAPER){
//		if(data.getCategory().equals("wallpaper")){
			installWallpaper(context, data.getInstallPath());
			return;
		}

		if(ca == DownloadConstant.CATEGORY_LIVE_WALLPAPER ||
				ca == DownloadConstant.CATEGORY_COMMON_APP){
			processPackageInstall(context, data.getInstallPath(),data.getPackageName(),data.getVersionCode(),ca);
				return;
		}
		
		//use service-install app is better than activity-install ,maybe the bug 5364 looks better
		
/*		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setClassName("com.lenovo.launcher", "com.lenovo.lejingpin.hw.lcapackageinstaller.LcaInstallerActivity");
		intent.putExtra("data", data);
		intent.setData(Uri.parse(data.getInstallPath()));
		try {
			context.startActivity(intent);
		} catch (Exception e) {
			Toast.makeText(context, R.string.lca_installer_not_found, Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}*/
		
		processPackageParseAndInstall(context,data.getInstallPath(),data.getPackageName(),data.getVersionCode(),ca);
	}
	
	
	
	
	
	
	
	private static void installWallpaper(Context context,String filename) {
		
		Log.i("yangmao_download", "installWallpaper -------filename is:"+filename);
		
        File inFile = new File(filename);
        FileInputStream inStream = null;
        try {
            inStream = new FileInputStream(inFile);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            WallpaperManager wpm = (WallpaperManager)context.getSystemService(
                            Context.WALLPAPER_SERVICE);
            wpm.setStream(inStream);
        } catch (IOException e) {
            Toast.makeText(context, R.string.wallpaper_insall_fail_toast, Toast.LENGTH_SHORT).show();

        }
    }
	
	
	
	public static void runApp(Context context, String packageName,String appName, int category) {
		Intent launcherIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
		boolean enabled = false;
		if (launcherIntent != null) {
			List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(launcherIntent, 0);
			if (list != null && list.size() > 0) {
				enabled = true;
			}
		}
		if (enabled) {
			context.startActivity(launcherIntent);
		} else {
			String str = context.getString(R.string.download_run_app_fail,appName);
			Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
		}

	}
	
	
	//yangmao add for move start 0108
	
	private static void processPackageParseAndInstall(Context context, String installPath, String packageName,String versionCode,int category){
		Log.i(TAG,"***********LcaInstallerUtils, packageURI:"+ installPath);
		if(installPath!=null){
			Log.i(TAG, "installPath size is:"+installPath.length());
		}
		if( installPath == null || installPath.length()==0 ){
			Toast.makeText(context, R.string.lcapackageinstaller_install_error, Toast.LENGTH_SHORT).show();
			return;
		}
		
		Uri installPathUri = Uri.parse(installPath);
		String path = installPathUri.getPath();
		String dir = path.substring(0, path.lastIndexOf(File.separator));
		String fileName = path.substring(path.lastIndexOf(File.separator)+1);
		Uri uri = installPathUri;
		
		String parsedInstallPath = dir + File.separator + "lca" + fileName;
		Intent intent = new Intent(context, LcaInstallerService.class);
		File parsedFile = new File(parsedInstallPath );
		Log.i(TAG,"***********LcaInstallerUtils, packageURI 111:"+ parsedInstallPath);
		File file = new File(installPath);
		if( parsedFile.exists()){
			Log.i(TAG,"******LcaInstallerUtils, com.lenovo.action.packageinstall-------"+ parsedInstallPath);
			uri = Uri.fromFile(parsedFile);
			intent.setAction("com.lenovo.action.packageinstall");
			intent.putExtra("type", "package_install");
		} else if( file.exists()){
			Log.i(TAG,"******LcaInstallerUtils, com.lenovo.action.packageparse-----------"+ parsedInstallPath);
			intent.setAction("com.lenovo.action.packageparse");
			intent.putExtra("type", "package_parse");
		}else{
			startDownloadAgainDialog(context, packageName, installPath);
			return;
		}
		intent.putExtra("uri", uri.toString());
        intent.putExtra("package", packageName );
        //yangmao modify
        intent.putExtra("versioncode", versionCode);
        intent.putExtra("category", category);
        intent.setPackage(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF);
		context.startService(intent);
	}
	
	private static void processPackageInstall(Context context, String installPath, String packageName,String versionCode,int category){
		if(!judgeInstalledPackageExist(installPath)){
			Log.i(TAG,"LcaInstallerUtils.installApplication(), package not exist!!!");
			startDownloadAgainDialog(context, packageName, installPath);
			return;
		}
		
		Intent intent = new Intent(context, LcaInstallerService.class);
		intent.setAction("com.lenovo.action.packageinstall");
		intent.putExtra("type", "package_install");
		Uri uri = Uri.parse(installPath);
		if( uri == null ){
			Toast.makeText(context, R.string.lcapackageinstaller_install_error, Toast.LENGTH_SHORT).show();
		}else {
			File file = new File(uri.toString() );
			Uri fileUri = Uri.fromFile(file);
			intent.putExtra("uri", fileUri.toString());
			intent.putExtra("package", packageName );
			//yangmao modify
			intent.putExtra("versioncode", versionCode);
			intent.putExtra("category", category);
			intent.setPackage(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF);
			context.startService(intent);
		}
	}
	
	
	private static boolean judgeInstalledPackageExist(String uriStr){
		try{
		    File file = new File( uriStr );
		    if( !file.exists()){
			    return false;
		    }
		}catch(Exception e){
			return false;
		}
		return true;
	}
	
	
	
	private static void startDownloadAgainDialog(Context context, String packageName, String installPath){
	    Intent intent = new Intent();
		intent.setClass( context, LcaInstallerDownloadActivity.class );
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("package", packageName);
		intent.putExtra("install_path", installPath);
		intent.setPackage(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF);
		try {
			context.startActivity(intent);
		} catch (Exception e) {
			Log.i(TAG,"LcaInstallerUtils.startDownloadAgainDialog--- start LcaInstallerActivity error!");
		}
	}
	
	public static void installApplication(Context context ,String installPath, 
			int category, String packageName ,String versionCode){
		Log.i(TAG,"LcaInstallerUtils.installApplication(), install Path:"+ installPath +
				", category:"+ category+", package:"+ packageName);
		
		
//		int categoryInt = -1;
//		try{
//			categoryInt = Integer.valueOf(category);
//		}catch(NumberFormatException e){
//			Log.e(TAG,"LcaInstallerUtils.installApplication() category error!");
//			return;
//		}
	
		
//		if( category < HwConstant.CATEGORY_LELAUNCHER_SERVER_APK){
//			processPackageParseAndInstall(context, installPath, packageName );
//				return;
//		}else{
//			processPackageInstall(context, installPath, packageName);
//			return;
//		}
		
		int ca = DownloadConstant.getDownloadCategory(category);
		
		if (ca == DownloadConstant.CATEGORY_RECOMMEND_APP
				|| ca == DownloadConstant.CATEGORY_SEARCH_APP
				|| ca == DownloadConstant.CATEGORY_APP_UPGRADE
				|| ca == DownloadConstant.CATEGORY_APPMANAGER_UPGRADE
				|| ca == DownloadConstant.CATEGORY_LOCKSCREEN
				|| ca == DownloadConstant.CATEGORY_THEME) {
			Log.i(TAG, "222222222222222");

			processPackageParseAndInstall(context, installPath, packageName ,versionCode ,ca);
			return;
		} else if (ca == DownloadConstant.CATEGORY_COMMON_APP || ca == DownloadConstant.CATEGORY_LIVE_WALLPAPER) {
			Log.i(TAG, "33333333333333");
			processPackageInstall(context, installPath, packageName,versionCode,ca);
			return;
		}
	}
	
	
	
	
	
	
}
