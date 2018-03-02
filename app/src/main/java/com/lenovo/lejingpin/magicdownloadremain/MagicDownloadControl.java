package com.lenovo.lejingpin.magicdownloadremain;



import com.lenovo.lejingpin.hw.content.data.HwConstant;
import com.lenovo.lejingpin.share.download.DownloadHelpers;
import com.lenovo.lejingpin.share.download.DownloadInfo;
import com.lenovo.lejingpin.share.download.Downloads;
import com.lenovo.lejingpin.share.download.LDownloadManager;
//import com.lenovo.lejingpin.hw.download.DownloadHelpers;
//import com.lenovo.lejingpin.hw.download.DownloadInfo;
//import com.lenovo.lejingpin.hw.download.Downloads;
//import com.lenovo.lejingpin.hw.download.LDownloadManager;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Download interface provided to others.
 */
public class MagicDownloadControl {
	
	private MagicDownloadControl(){
		
	}
	
	public static enum Status {
		UNDOWNLOAD("undownload"),   //no download
		DOWNLOADING("downloading"), //begin downloading
		DOWNLOAD_WAIT("download_wait"), //add to download queue but not begin download
		PAUSE("pause"),             //downloading paused
		UNINSTALL("uninstall"),     //download completed
		INSTALL("install");         //installed

		private Status(String value) {
			this.value = value;
		}

		private String value;
		public String value() {
			return value;
		}

		public static Status parseStatus(String value) {
			for (Status status : Status.values()) {
				if(status.value.equals(value)) { return status; }
			}
			return null;
		}
	}

	public static void downloadFromLeStrore(Context context, String packageName, String versionCode, 
			String category, String appName, String iconUrl, 
			String installTag, String continueTag) {
		final String action = HwConstant.ACTION_DOWNLOAD_APP_FROM_LESTORE;
        Intent intent = new Intent();
        intent.setAction(action);
		intent.putExtra("package_name", packageName);
		intent.putExtra("version_code", versionCode);
		intent.putExtra("app_name", appName);
        intent.putExtra("category", category);
        intent.putExtra("app_iconurl", iconUrl);
        intent.putExtra("tag_install", installTag);
        intent.putExtra("tag_continue", continueTag);
        
        context.sendBroadcast(intent);
	}
	
	public static void downloadFromCommon(Context context, String packageName, String versionCode, 
			int category, String appName, String iconUrl, 
			String downloadUrl, String mimeType,
			String installTag, String continueTag) {
		Log.i("yangmao_0128","downloadFromCommon method");
		LDownloadManager.getDefaultInstance(context.getApplicationContext());
		final String action = HwConstant.ACTION_DOWNLOAD_FROM_COMMON;
        Intent intent = new Intent();
        intent.setAction(action);
		intent.putExtra("package_name", packageName);
		intent.putExtra("version_code", versionCode);
		intent.putExtra("app_name", appName);
        intent.putExtra("category", category);
        intent.putExtra("app_iconurl", iconUrl);
        intent.putExtra("download_url", downloadUrl);
        intent.putExtra("mime_type", mimeType);
        intent.putExtra("tag_install", installTag);
        intent.putExtra("tag_continue", continueTag);
        
        context.sendBroadcast(intent);
	}

	public static Status queryDownloadStatus(Context context, String packageName, String versionCode){		
		//DownloadInfo downloadInfo = DownloadHelpers.queryDownloadInfo(context, packageName, versionCode);	
		DownloadInfo downloadInfo = LDownloadManager.getDefaultInstance(context.getApplicationContext()).getDownloadInfo(new DownloadInfo(packageName,versionCode));
		Status status = Status.UNDOWNLOAD;
		if( downloadInfo != null){
			int downloadStatus = -1;
			downloadStatus = downloadInfo.getDownloadStatus();
			if( downloadStatus== Downloads.STATUS_RUNNING_PAUSED){
				status = Status.PAUSE;
			}else if( downloadStatus == Downloads.STATUS_SUCCESS ){
				status = Status.UNINSTALL;
			}else if( downloadStatus == Downloads.STATUS_INSTALL){
				status = Status.INSTALL;
			}else {
		    	status = Status.DOWNLOADING;
		    }
		}
		return status;
	}
	
}
