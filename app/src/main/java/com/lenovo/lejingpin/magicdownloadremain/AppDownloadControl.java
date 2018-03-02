package com.lenovo.lejingpin.magicdownloadremain;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;



import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;



import com.lenovo.launcher.R;
import com.lenovo.launcher2.commonui.LeAlertDialog;
import com.lenovo.launcher2.customizer.SettingsValue;

import com.lenovo.lejingpin.ams.AmsRequest;
import com.lenovo.lejingpin.ams.AmsSession;
import com.lenovo.lejingpin.ams.AmsSession.AmsCallback;
import com.lenovo.lejingpin.ams.GetImageRequest;
import com.lenovo.lejingpin.ams.NewSearchAppName.Application;

import com.lenovo.lejingpin.hw.content.HwContentMangerReceiver;
import com.lenovo.lejingpin.hw.content.data.HwConstant;
//import com.lenovo.lejingpin.hw.download.DownloadHelpers;
//import com.lenovo.lejingpin.hw.download.DownloadInfo;
//import com.lenovo.lejingpin.hw.download.Downloads;
//import com.lenovo.lejingpin.hw.download.LDownloadManager;
import com.lenovo.lejingpin.hw.lcapackageinstaller.utils.LcaInstallerUtils;
import com.lenovo.lejingpin.hw.ui.RecommendLocalAppInfo;
import com.lenovo.lejingpin.hw.ui.RecommendLocalAppInfo.Status;
import com.lenovo.lejingpin.settings.LejingpingSettings;
import com.lenovo.lejingpin.settings.LejingpingSettingsValues;
import com.lenovo.lejingpin.share.download.DownloadConstant;
import com.lenovo.lejingpin.share.download.DownloadHelpers;
import com.lenovo.lejingpin.share.download.DownloadInfo;
import com.lenovo.lejingpin.share.download.Downloads;
import com.lenovo.lejingpin.share.download.LDownloadManager;



public class AppDownloadControl {
	private static final String TAG = "zdx";
	
	private AppDownloadControl(){
		
	}

	public static void prepareDownload(RecommendLocalAppInfo currentApp, Context context) {
		Log.i("yangmao_move","prepareDownload");
		if( currentApp == null ){
			Toast.makeText(context, R.string.download_error, Toast.LENGTH_SHORT).show();
			return;
		}
		Status status = currentApp.getStatus();
	
		Log.i(TAG,">>>>>>>AppDownloadControl.prepareDownload >> "+ currentApp.getPackageName()+
				"category:"+ currentApp.getCategory()+", status:"+ status);

		if(status == null || Status.UNDOWNLOAD.equals(status)) {
			Log.i("yangmao_move","startDownload(currentApp, context);");
			currentApp.setStatus(Status.DOWNLOAD_CLICK);
			startDownload(currentApp, context);
		} else if(Status.DOWNLOADING.equals(status)) {
			if("other".equals(HwConstant.getConnectType(context))) {
				Toast.makeText(context, R.string.error_network_state, Toast.LENGTH_SHORT).show();
			} else if("mobile".equals(HwConstant.getConnectType(context)) && LejingpingSettingsValues.wlanDownloadValue(context)) {				
				popupWlanDownloadDialog(context);
			}
			else {
				LDownloadManager.getDefaultInstance(context.getApplicationContext()).pauseTask(
						new DownloadInfo(currentApp.getPackageName(), currentApp.getVersionCode()));
			}
		} else if( Status.UNINSTALL.equals(status))	{
			installApp(currentApp, context);
		}else if(Status.INSTALL.equals(status)){
			//yangmao add 1214 move		
			Log.i(TAG, "install>>>>>>>>>>>>>>>");
			LcaInstallerUtils.runApp(context, currentApp.getPackageName(), currentApp.getAppName(), currentApp.getCategory());
		}else if(Status.PAUSE.equals(status)) {
			if("other".equals(HwConstant.getConnectType(context))) {
				Toast.makeText(context, R.string.error_network_state, Toast.LENGTH_SHORT).show();
			} 
			else if("mobile".equals(HwConstant.getConnectType(context)) && LejingpingSettingsValues.wlanDownloadValue(context)) {				
				popupWlanDownloadDialog(context);
			}
			
			else {
				//Log.i(TAG,"---------------------------resumeTaks--------------"+ currentApp.getPackageName()+"-----"+ currentApp.getVersionCode());
				LDownloadManager.getDefaultInstance(context).resumeTask(
						new DownloadInfo(currentApp.getPackageName(), currentApp.getVersionCode()));
			}
		}
	}
	
	
	
	
	//yangmao add start
	public static void prepareDownloadBySearch(Application hawaii_search_app, Context context) {

		RecommendLocalAppInfo currentApp = new RecommendLocalAppInfo();
		currentApp.setAppName(hawaii_search_app.getAppName());
		currentApp.setPackageName(hawaii_search_app.getPackage_name());
		currentApp.setVersionCode(hawaii_search_app.getApp_versioncode());
		currentApp.setIconAddress(hawaii_search_app.getIcon_addr());
		currentApp.setAppVersion(hawaii_search_app.getApp_version());
		currentApp.setCategory(DownloadConstant.CATEGORY_SEARCH_APP | DownloadConstant.CATEGORY_LENOVO_LCA);
	
		Status status = queryDownloadStatus(context,hawaii_search_app.getPackage_name(),hawaii_search_app.getApp_versioncode());
		
		status = checkInstalledApp(context,hawaii_search_app.getPackage_name(),status);
		
		currentApp.setStatus(status);		
		prepareDownload(currentApp, context); 
				
	}
	
	
	
	//yangmao add for wall_paper lock_screen theme 0122 start 
	//not use the class , should use the packagename versioncode ;
	
	public static void prepareDownloadByOther(Application hawaii_search_app, Context context) {
		Log.i("yangmao_move", "prepareDownloadBySearch");
		RecommendLocalAppInfo currentApp = new RecommendLocalAppInfo();
		currentApp.setAppName(hawaii_search_app.getAppName());
		currentApp.setPackageName(hawaii_search_app.getPackage_name());
		currentApp.setVersionCode(hawaii_search_app.getApp_versioncode());
		currentApp.setIconAddress(hawaii_search_app.getIcon_addr());
		
		//maybe change the Category
		currentApp.setCategory(DownloadConstant.CATEGORY_LESTORE_APK);
		
		Status status = queryDownloadStatus(context,hawaii_search_app.getPackage_name(),hawaii_search_app.getApp_versioncode());
		currentApp.setStatus(status);		
		prepareDownload(currentApp, context); 
				
	}
	
	//yangmao add for end 0122
	
	
	
	
	

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
			}else if( downloadStatus == Downloads.STATUS_RUNNING ){
		    	status = Status.DOWNLOADING;
		    }
		}
		return status;
	}
	
	//yangmao add end
	
	private static void installApp(RecommendLocalAppInfo currentApp, Context context ) {
		DownloadInfo info = new DownloadInfo(currentApp.getPackageName(), currentApp.getVersionCode());
		DownloadInfo downloadInfo = LDownloadManager.getDefaultInstance(context.getApplicationContext()).getDownloadInfo(info);
		if(downloadInfo != null) {			
			LcaInstallerUtils.installApplication(context, downloadInfo);
		}
	}

	private static void startDownload(final RecommendLocalAppInfo currentApp, final Context context) {
		String network = HwConstant.getConnectType(context);
		if("other".equals(network)) {
			Toast.makeText(context, R.string.error_network_state, Toast.LENGTH_SHORT).show();
			return;
		} /*else if("mobile".equals(network)) {
			final AlertDialog alertDialog = new AlertDialog.Builder(context).setTitle(R.string.d_wifi_t).
					setMessage(R.string.d_wifi_m).create();
			alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getText(R.string.d_wifi_ok), 
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialoginterface, int i) {
					sendBroadcastToDownload( currentApp, context);
					alertDialog.dismiss();
				}
			});
			alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, context.getText(R.string.d_wifi_cancel), 
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					alertDialog.dismiss();
				}
			});
			alertDialog.show();
		} */
		
//		else if("wifi".equals(network) || "mobile".equals(network)) {
//			Log.i("yangmao_move", "sendBroadcastToDownload( currentApp, context);");
//			sendBroadcastToDownload( currentApp, context);
//		}
		else if("mobile".equals(network) && LejingpingSettingsValues.wlanDownloadValue(context)) {
			Log.i("yangmao_move", "sendBroadcastToDownload( currentApp, context);");
			
			popupWlanDownloadDialog(context);
		}else{
			sendBroadcastToDownload( currentApp, context);
		}
		/** RK_ID: RK_DOWNLOAD_COUNT . AUT: zhanglz1 . DATE: 2012-10-18 . S **/
		/*if (currentApp.getCategory().equals(
				HwConstant.CATEGORY_WALLPAPER_STRING)) {
			String package_name = currentApp.getPackageName();
			String version_code = currentApp.getVersionCode();
			boolean isForDownloadNum = true;
			WallpaperAction.getInstance(context, package_name, version_code,
					isForDownloadNum).doAction();
		}*/
	    /** RK_ID: RK_DOWNLOAD_COUNT . AUT: zhanglz1 . DATE: 2012-10-18 . E **/

        
	}

	/*public static RecommendLocalAppInfo findApp(Context context, String pkg, String vcode) {
		//Log.i(TAG,"*********************AppDownloadControl.findApp(), pkg:"+ pkg);
		ReCommendsApp rApp = HWDBUtil.queryApp(context, pkg, vcode);
			
		RecommendLocalAppInfo localAppInfo = null;
		if( rApp != null ){
			localAppInfo = new RecommendLocalAppInfo(rApp);
			DownloadInfo downloadInfo = DownloadHelpers.queryDownloadInfo(context, pkg, vcode);
			if( downloadInfo != null){
				int downloadStatus = -1;
				downloadStatus = downloadInfo.getDownloadStatus();
			    if( downloadStatus== Downloads.STATUS_RUNNING_PAUSED){
				    localAppInfo.setStatus(  Status.PAUSE );
				    localAppInfo.downLoadProgress = downloadInfo.getProgress();
			    }else if( downloadStatus == Downloads.STATUS_SUCCESS ){
				    localAppInfo.setStatus( Status.UNINSTALL);
			    }else if( downloadStatus == Downloads.STATUS_INSTALL){
				    localAppInfo.setStatus( Status.INSTALL);
			    }else if( downloadStatus == Downloads.STATUS_RUNNING ){
			    	localAppInfo.setStatus( Status.DOWNLOADING);
			    }
			}
			return localAppInfo;
		}
		Log.i(TAG,"***********************************AppDownloadControl, findApp:"+ null);
		return null;
	}

	public static Status getAppStatus(Context context, String pkg, String code) {
		RecommendLocalAppInfo app = findApp(context, pkg, code);
		return app == null ? null : app.getStatus();
	}*/
		
	public static boolean isDownloadComplete(String pkgName, String versionCode, Context context) {
		DownloadInfo downloadInfo = LDownloadManager.getDefaultInstance(context.getApplicationContext()).getDownloadInfo(
				new DownloadInfo(pkgName, versionCode ));
		if(downloadInfo != null && downloadInfo.getDownloadStatus() == Downloads.Impl.STATUS_SUCCESS) { return true; }
		return false;
	}
	
	public static void drawDownloadState(RecommendLocalAppInfo app, TextView downloadView) {
		if( ( app == null )|| (downloadView == null))
			return;
	
		switch (app.getStatus()) {
		case DOWNLOADING:
			downloadView.setText(R.string.download_pause);
			break;
		case UNINSTALL:
			if ( app.getCategory()==DownloadConstant.CATEGORY_WALLPAPER) {
				downloadView.setText(R.string.download_install_wallpaper);
			}else{
				downloadView.setText(R.string.app_detail_install);
			}
			break;
		case PAUSE:
			downloadView.setText(R.string.download_resume);
			break;
		case UNDOWNLOAD:
			downloadView.setText(R.string.download_download);
			break;
		case INSTALL:
			downloadView.setText(R.string.download_installed);
			break;
		default:
			downloadView.setText(R.string.download_download);
			break;
		}
	}
	

	
	//yangmao add new
	
	public static void drawDownloadState_hawaiiSearch(Status status, TextView downloadView) {
		
		switch (status) {
		case DOWNLOADING:
			downloadView.setText(R.string.download_pause);
			break;
		case UNINSTALL:
			downloadView.setText(R.string.app_detail_install);
			break;
		case PAUSE:
			downloadView.setText(R.string.download_resume);
			break;
		case UNDOWNLOAD:
			downloadView.setText(R.string.download_download);
			break;
		case INSTALL:
			downloadView.setText(R.string.download_installed);
			break;
		default:
			downloadView.setText(R.string.download_download);
			break;
		}
	}
	
	//yangmao add end
	
	
	//------App Download------
	private static void sendBroadcastToDownload( RecommendLocalAppInfo currentApp, Context context) {
		//Log.i(TAG,"sendBroadcastToDownload, category:"+ currentApp.getCategory());
		Intent intent = new Intent();
		//yangmao add 1214 move
		intent.setAction(HwConstant.ACTION_REQUEST_APP_DOWNLOAD);
		intent.putExtra("package_name", currentApp.getPackageName());
		intent.putExtra("app_name", currentApp.getAppName());
		intent.putExtra("version_code", currentApp.getVersionCode());
		//zdx modify
		intent.putExtra("app_iconurl", currentApp.getIconAddress());
		intent.putExtra("category", currentApp.getCategory());
		
		intent.putExtra("version_name", currentApp.getVersion());
		intent.setPackage(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF);
        //intent.putExtra("from", "hawaii_app");
		Log.i("yangmao_move", "sendBroadcastToDownload");
		context.sendBroadcast(intent);
	}
	
	public static void loadImg(final String url, final OnImgLoadListener callback, final Context context) {
		if(TextUtils.isEmpty(url)) return;
		AsyncTask.execute(new Runnable() {
			public void run() {
				GetImageRequest imageRequest = new GetImageRequest();
				imageRequest.setData(url);
				AmsSession.execute(context, imageRequest, new AmsCallback() {
					public void onResult(AmsRequest request, int code, final byte[] bytes) {
						//Log.d(TAG, "end load snapshot url=" + url + ", code=" + code 
						//		+ ", bytes=" + (bytes == null ? null : bytes.length));
						if(bytes == null) return;
						/*ByteArrayInputStream bs = new ByteArrayInputStream(bytes);
						final Drawable drawable = Drawable.createFromStream(bs, null);
						callback.onLoadComplete(drawable);
						try {
							bs.close();
						} catch (IOException e) {
							e.printStackTrace();
						}*/
						Drawable drawable = null;
						ByteArrayInputStream bs = null;
						GZIPInputStream input = null;
						try {
							input = new GZIPInputStream(new ByteArrayInputStream(bytes));
							drawable = Drawable.createFromStream(input, null);
						} catch (Exception e) {
							// e.printStackTrace();
							bs = new ByteArrayInputStream(bytes);
							drawable = Drawable.createFromStream(bs, null);
						} finally {
							try {
								if (input != null) {
									input.close();
								}
								if (bs != null) {
									bs.close();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						callback.onLoadComplete(drawable);
					}
				});
			}
		});
	}
	public static interface OnImgLoadListener {
		void onLoadComplete(Drawable img);
	}
	
	private static Status checkInstalledApp(Context context, String packagename,Status status) {
		
		List<PackageInfo> packages = context.getPackageManager()
				.getInstalledPackages(0);
		for (int i = 0; i < packages.size(); i++) {
			PackageInfo packageInfo = packages.get(i);
			if (packageInfo == null)
				continue;
			ApplicationInfo appInfo = packageInfo.applicationInfo;
			if (appInfo == null)
				continue;

			if (appInfo.packageName.equals(packagename)) {
				status = Status.INSTALL;
				return status;
			}

		}

		return status;

	}
	
	
	
	
	 private static void popupWlanDownloadDialog(final Context mContext) {

	    	LeAlertDialog alertDialog = new LeAlertDialog(mContext,R.style.Theme_LeLauncher_Dialog_Shortcut);
	    	alertDialog.setLeTitle(R.string.lejingpin_wlan_download_dialog_title);
	    	alertDialog.setLeMessage(R.string.lejingpin_wlan_download_dialog_body);
	    	alertDialog.setLeNegativeButton(mContext.getString(R.string.lejingpin_wlan_download_dialog_btn_cancel),new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	                dialog.dismiss();
	                
	            }
	        });
	        alertDialog.setLePositiveButton(mContext.getString(R.string.lejingpin_wlan_download_dialog_btn_confirm),new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            	startLejingpinSettings(mContext);
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
	
	 public static void startLejingpinSettings(Context mContext){
	    	Intent mIntent = new Intent(mContext, LejingpingSettings.class);   	
	    	mContext.startActivity(mIntent);
	    	
	 }
	
	
	
}

