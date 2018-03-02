package com.lenovo.lejingpin.hw.content;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.addon.share.LeShareDownloadQieZiCallback;

//import com.lenovo.lejingpin.hw.content.data.AppDownlaodUrl;
import com.lenovo.lejingpin.hw.content.data.HwConstant;
//import com.lenovo.lejingpin.hw.download.DownloadHandler;
//import com.lenovo.lejingpin.hw.download.DownloadHelpers;
//import com.lenovo.lejingpin.hw.download.DownloadHelpers;
//import com.lenovo.lejingpin.hw.download.DownloadInfo;
//import com.lenovo.lejingpin.hw.download.Downloads;
//import com.lenovo.lejingpin.hw.download.LDownloadManager;
import com.lenovo.lejingpin.hw.lcapackageinstaller.utils.LcaInstallerUtils;
import com.lenovo.lejingpin.share.download.AppDownloadUrl;
import com.lenovo.lejingpin.share.download.DownloadConstant;
import com.lenovo.lejingpin.share.download.DownloadHandler;
import com.lenovo.lejingpin.share.download.DownloadHelpers;
import com.lenovo.lejingpin.share.download.DownloadInfo;
import com.lenovo.lejingpin.share.download.Downloads;
import com.lenovo.lejingpin.share.download.LDownloadManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


public class MagicDownloadReceiver extends BroadcastReceiver{

	private final static String TAG = "zdx";

	@Override
	public void onReceive(Context context, Intent intent){
		if(intent!=null){
		
			String action = intent.getAction();
			Log.i(TAG, "----------------------MagicDownloadReceiver.onReceive >> action : "+action);
		 if(HwConstant.ACTION_DOWNLOAD_FROM_COMMON.equals(action)){
				String packageName = intent.getStringExtra("package_name");
				String versionCode = intent.getStringExtra("version_code");
				String appName = intent.getStringExtra("app_name");
				int category = intent.getIntExtra("category", DownloadConstant.CATEGORY_ERROR);
				String iconUrl = intent.getStringExtra("app_iconurl");
				String downloadUrl = intent.getStringExtra("download_url");
				String mimeType = intent.getStringExtra("mime_type");
				String installTag = intent.getStringExtra("tag_install");
				String continueTag = intent.getStringExtra("tag_continue");
				downloadAppFromCommon( context, packageName, versionCode, 
						category, appName, iconUrl, 
						downloadUrl, mimeType,
						installTag, continueTag );
			}
//		 
//		 else if ( Intent.ACTION_PACKAGE_REMOVED.equals(action)){
//				DownloadInfo downloadInfo = new DownloadInfo();
//				String packageName = intent.getData().getSchemeSpecificPart();
//				downloadInfo.setPackageName(packageName);
//				
//				boolean bInstalled  = false;
//				if( Intent.ACTION_PACKAGE_ADDED.equals(action))
//					bInstalled = true;
//				else if( Intent.ACTION_PACKAGE_REMOVED.equals(action))
//					bInstalled = false;
//				
////				
////					LDownloadManager.getDefaultInstance(context).setDownloadInfoInstall(downloadInfo, 
////							context, bInstalled );
//				
////				DownloadHelpers.setDownloadInfoInstall(downloadInfo, context, bInstalled);
//				
//			}
		 
		}
	}
	
	

	

	
	
	
	private void downloadAppFromCommon(Context context, String packageName, String versionCode, 
			int category, String appName, String iconUrl, 
			String downloadUrl, String mimeType,
			String installTag, String continueTag ){
		Log.i(TAG,"MagicDownloadReceiver.downloadAppFromCommon(), context:"+context+" ,pkg:"+ packageName +
				" ,versionCode:"+ versionCode +" ,category:"+ category +" ,appName:"+ appName +" ,iconUrl:"+ iconUrl+
				" ,downloadUrl:"+ downloadUrl +" ,mimeType:"+ mimeType +" ,installTag:"+ installTag +" ,continueTag:"+continueTag);
		if( ( context == null ) ||
			( packageName == null ) ||
			( versionCode == null ) ||
			
			( appName == null )	||
			( downloadUrl == null ) ||
			( mimeType == null )){
			return;
		}
		//DownloadInfo downloadInfo = DownloadHelpers.queryDownloadInfo(context, packageName, versionCode);
		DownloadInfo downloadInfo = LDownloadManager.getDefaultInstance(context).getDownloadInfo(new DownloadInfo(packageName,versionCode));
		if( downloadInfo != null){
			int downloadStatus = downloadInfo.getDownloadStatus();
		    if( downloadStatus == Downloads.Impl.STATUS_SUCCESS ){
		    	if( installTag.equals("true"))
		    	    installApp( context, packageName, versionCode, category);
		    }else if( downloadStatus == Downloads.Impl.STATUS_RUNNING_PAUSED)
		    	if( continueTag.equals("true"))
		    		continueDownloadApp(context, packageName, versionCode);
		    else 
		    	Toast.makeText(context, R.string.added_downloadqueue, Toast.LENGTH_SHORT).show();
		}else{
			startdownloadAppFromCommon( context, packageName, versionCode, category, appName, iconUrl,
		    		downloadUrl, mimeType);
		}
	}
	
	private void startdownloadAppFromCommon(Context context, String packageName, String versionCode, 
			int category, String appName, String iconUrl, String downloadUrl, String mimeType ){
		Log.i(TAG,"MagicDownloadReceiver.startdownloadAppFromCommon, pkg:"+ packageName +", category:"+ category);
		AppDownloadUrl downurl = new AppDownloadUrl();
		downurl.setDownurl(downloadUrl);
		
//		downurl.setPackageName(packageName);
//		downurl.setVersionCode(versionCode);
//		downurl.setAppName(appName);
//		downurl.setIconUrl(iconUrl);
//		downurl.setCategory(category);
		downurl.setPackage_name(packageName);
		downurl.setVersion_code(versionCode);
		downurl.setApp_name(appName);
		downurl.setIconUrl(iconUrl);
		downurl.setCallback(new LeShareDownloadQieZiCallback(context));
		downurl.setCategory(category);
		downurl.setMimeType(mimeType);
		sendMessage(DownloadHandler.getInstance(context),DownloadConstant.MSG_DOWN_LOAD_URL,downurl);
	}
	
	private void continueDownloadApp(Context context, String packageName, String versionCode ){
		if("other".equals(HwConstant.getConnectType(context))) {
			Toast.makeText(context, R.string.error_network_state, Toast.LENGTH_SHORT).show();
		} else {
			LDownloadManager.getDefaultInstance(context).resumeTask(
					new DownloadInfo(packageName, versionCode));
		}
	}
	
	private void installApp( Context context, String packageName, String versionCode, int category){
		Log.i(TAG,"MagicDownloadReceiver.upgradeInstall, pkg:"+ packageName);
		DownloadInfo info = new DownloadInfo(packageName, versionCode);
		DownloadInfo downloadInfo = LDownloadManager.getDefaultInstance(context).getDownloadInfo(info);
		Log.i(TAG,"MagicDownloadReceiver.upgradeInstall, downloadInfo:"+ downloadInfo);
		
		if(downloadInfo != null) {
			LcaInstallerUtils.installApplication(context, downloadInfo.getInstallPath(), 
			downloadInfo.getCategory(), packageName ,versionCode);
		}
	}
	
	private void sendMessage(Handler handler, int what, Object obj){
		if(handler != null) {
			Message msg = new Message();
			msg.obj = obj;
			msg.what = what;
			handler.sendMessage(msg);
		} else {
			Log.i(TAG, "DownloadAppAction.sendMessage ,  handler is null, not send!");
		}
	}

}


