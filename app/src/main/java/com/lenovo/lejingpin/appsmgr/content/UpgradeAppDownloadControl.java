package com.lenovo.lejingpin.appsmgr.content;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;

import com.lenovo.lejingpin.LEJPConstant;
import com.lenovo.lejingpin.appsmgr.content.UpgradeApp.Status;
import com.lenovo.lejingpin.hw.lcapackageinstaller.utils.LcaInstallerUtils;
import com.lenovo.lejingpin.hw.ui.Util;
import com.lenovo.lejingpin.settings.LejingpingSettingsValues;
import com.lenovo.lejingpin.share.ams.AmsRequest;
import com.lenovo.lejingpin.share.ams.AmsSession;
import com.lenovo.lejingpin.share.ams.AmsSession.AmsCallback;
import com.lenovo.lejingpin.share.ams.GetImageRequest;
import com.lenovo.lejingpin.share.download.AppDownloadUrl;
import com.lenovo.lejingpin.share.download.DownloadConstant;
import com.lenovo.lejingpin.share.download.DownloadHandler;
import com.lenovo.lejingpin.share.download.DownloadInfo;
import com.lenovo.lejingpin.share.download.Downloads;
import com.lenovo.lejingpin.share.download.LDownloadManager;
import com.lenovo.lejingpin.share.util.Utilities;
import com.lenovo.launcher.R;
//import com.lenovo.launcher2.Launcher;
import com.lenovo.launcher2.commonui.LeAlertDialog;
import com.lenovo.launcher2.customizer.Reaper;
import com.lenovo.launcher2.settings.SeniorSettings;

public class UpgradeAppDownloadControl {
	public static final String TAG = "UpgradeAppDownloadControl";
	
	public static boolean isDownloading = false;
	
	public static final String REAPER_EVENT_CATEGORY = "LeJinpin";
	public static final String REAPER_EVENT_ID_ENTRY = "LocalAppEntry";
	public static final String REAPER_EVENT_ID_UPDATE = "LocalAppUpdate";
	public static final String REAPER_EVENT_ID_UPDATEALL = "LocalAppUpdateAll";
	public static final String REAPER_EVENT_ID_BACKUP = "LocalAppBK";

	private UpgradeAppDownloadControl(){
		
	}
	
	public static void prepareDownload(final UpgradeApp currentApp, final Context context) {
		if (currentApp == null) {
			Toast.makeText(context, R.string.download_error, Toast.LENGTH_SHORT)
					.show();
			return;
		}
		Status status = currentApp.getStatus();
		boolean isComplete = isDownloadComplete(currentApp.getPackageName(),
				currentApp.getVersionCode(), context);
		Log.i(TAG,
				">>>>>>>UpgradeAppDownloadControl.prepareDownload >> "
						+ currentApp.getPackageName() + ",category:"
						+ currentApp.getCategory() + ", status:" + status);
		if (isComplete) {
			installApp(currentApp, context);
			return;
		}
		if (status == null || Status.UNDOWNLOAD.equals(status)) {			
			if(LEJPConstant.getInstance().isNeedConfirmDownload(context)){
				LejingpingSettingsValues.popupWlanDownloadDialog(context);
//				Utilities.showConfirmDownloadDialog(context, new DialogInterface.OnClickListener(){
//					@Override
//					public void onClick(DialogInterface dialog, int postion) {
//						LEJPConstant.getInstance().setConfirmDownloadFlag(false);
////						currentApp.setStatus(Status.DOWNLOAD_CLICK);
//						Reaper.processReaper(context, REAPER_EVENT_CATEGORY, REAPER_EVENT_ID_UPDATE,currentApp.getPackageName() , Reaper.REAPER_NO_INT_VALUE);
//						startDownload(currentApp, context);
//						dialog.dismiss();
//					}
//					
//				},null);
				
				
			}else{
//				currentApp.setStatus(Status.DOWNLOAD_CLICK);
				Reaper.processReaper(context, REAPER_EVENT_CATEGORY, REAPER_EVENT_ID_UPDATE,currentApp.getPackageName() , Reaper.REAPER_NO_INT_VALUE);
				startDownload(currentApp, context);
			}
		} else if (Status.DOWNLOADING.equals(status)) {
			if (DownloadConstant.CONNECT_TYPE_OTHER == DownloadConstant.getConnectType(context)) {
				Toast.makeText(context, R.string.error_network_state,
						Toast.LENGTH_SHORT).show();
			} else {
					currentApp.setControl(Downloads.Impl.CONTROL_PAUSED);
//					currentApp.setStatus(Status.PAUSE);
					LDownloadManager.getDefaultInstance(context).pauseTask(
							new DownloadInfo(currentApp.getPackageName(),
									currentApp.getVersionCode()));
			}
		} else if (Status.UNINSTALL.equals(status)) {
			installApp(currentApp, context);
		} else if (Status.INSTALL.equals(status)) {
			 //xujing3 temp remove
			 LcaInstallerUtils.runApp(context,
			 currentApp.getPackageName(),
			 currentApp.getAppName(), currentApp.getCategory());
		} else if (Status.PAUSE.equals(status)) {
			if (DownloadConstant.CONNECT_TYPE_OTHER == DownloadConstant.getConnectType(context)) {
				Toast.makeText(context, R.string.error_network_state,
						Toast.LENGTH_SHORT).show();
			} else {
				if(LEJPConstant.getInstance().isNeedConfirmDownload(context)){
					Utilities.showConfirmDownloadDialog(context, new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int postion) {
							LEJPConstant.getInstance().setConfirmDownloadFlag(false);
							currentApp.setControl(Downloads.Impl.CONTROL_RUN);
//							currentApp.setStatus(Status.DOWNLOADING);
							LDownloadManager.getDefaultInstance(context).resumeTask(
									new DownloadInfo(currentApp.getPackageName(),
											currentApp.getVersionCode()));
						}
						
					},null);
				}else{
					currentApp.setControl(Downloads.Impl.CONTROL_RUN);
//					currentApp.setStatus(Status.DOWNLOADING);
					LDownloadManager.getDefaultInstance(context).resumeTask(
							new DownloadInfo(currentApp.getPackageName(),
									currentApp.getVersionCode()));
				}
			}
		}
	}

	private static void installApp(UpgradeApp currentApp, Context context) {
		DownloadInfo info = new DownloadInfo(currentApp.getPackageName(),
				currentApp.getVersionCode());
		DownloadInfo downloadInfo = LDownloadManager
				.getDefaultInstance(context).getDownloadInfo(info);
		if (downloadInfo != null) {
			// xujing3 temp remove
			 LcaInstallerUtils.installApplication(context,
			 downloadInfo.getInstallPath(),
			 downloadInfo.getCategory(), downloadInfo.getPackageName(),downloadInfo.getVersionCode());
		}
	}

	private static void startDownload(final UpgradeApp currentApp,
			final Context context) {
		
		if(!Util.getInstance().isNetworkEnabled(context)){
			showNetworkEnableDialog(context, context.getString(R.string.letongbu_install_dialog_title));
			return;
		}
		
		int network = DownloadConstant.getConnectType(context);
		if (DownloadConstant.CONNECT_TYPE_OTHER == network) {
			Toast.makeText(context, R.string.error_network_state,
					Toast.LENGTH_SHORT).show();
			return;
		}else if(DownloadConstant.CONNECT_TYPE_WIFI == network || DownloadConstant.CONNECT_TYPE_MOBILE == network) {
			currentApp.setStatus(Status.DOWNLOAD_CLICK);
			currentApp.setControl(Downloads.Impl.CONTROL_RUN);
			sendBroadcastToDownload(currentApp, context);
		}
	}
	
//	private static void showConfirmDownloadDialog(final Context context,DialogInterface.OnClickListener OnClickOklistener){
//	    LeAlertDialog dialog = new LeAlertDialog(context, R.style.Theme_LeLauncher_Dialog_Alert);
//	    dialog.setTitle(R.string.letongbu_install_dialog_title);
//	    dialog.setLeMessage(context.getText(R.string.d_wifi_m));
//	    dialog.setLeNegativeButton(context.getText(R.string.cancel_action), new DialogInterface.OnClickListener() {
//	        public void onClick(DialogInterface dialog, int which) {
//	                dialog.dismiss();
//	        }
//	    });
//
//	    dialog.setLePositiveButton(context.getText(R.string.rename_action), OnClickOklistener);
//	    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//	        @Override
//	        public void onCancel(DialogInterface dialog) {
//	                dialog.dismiss();
//	        }
//	    });
//	    dialog.show();
//	}
	
	public static void showNetworkEnableDialog(final Context context, String title) {
	    LeAlertDialog dialog = new LeAlertDialog(context, R.style.Theme_LeLauncher_Dialog_Shortcut);
	    dialog.setLeTitle(title);
	    dialog.setLeMessage(context.getText(R.string.confirm_network_open));
	    dialog.setLeNegativeButton(context.getText(R.string.cancel_action), new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	                dialog.dismiss();
	                Toast.makeText(context, R.string.version_update_toast, Toast.LENGTH_SHORT).show();
	        }
	    });

	    dialog.setLePositiveButton(context.getText(R.string.rename_action), new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	                Intent intent = new Intent();
	                intent.setClass(context, SeniorSettings.class);
	                //((Activity) context).startActivityForResult(intent, Launcher.REQUEST_CONFIRM_NETWORK_ENABLED);
	                dialog.dismiss();
	        }
	    });
	    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
	        @Override
	        public void onCancel(DialogInterface dialog) {
	                dialog.dismiss();
	                Toast.makeText(context, R.string.version_update_toast, Toast.LENGTH_SHORT).show();
	        }
	    });
	    dialog.show();
	}

	public static boolean isDownloadComplete(String pkgName,
			String versionCode, Context context) {
		DownloadInfo downloadInfo = LDownloadManager
				.getDefaultInstance(context).getDownloadInfo(
						new DownloadInfo(pkgName, versionCode));
		if (downloadInfo != null
				&& downloadInfo.getDownloadStatus() == Downloads.Impl.STATUS_SUCCESS)
			return true;
		return false;
	}
	
	public static int dip2px(Context context, float dipValue){ 
		final float scale = context.getResources().getDisplayMetrics().density; 
		return (int)(dipValue * scale + 0.5f); 
	} 

	public static void drawDownloadState(UpgradeApp app, View item,Context context) {
//		Log.d(TAG, "-------------------------drawDownloadState");
		if ((app == null) || (item == null))
			return;
		TextView downloadView = (TextView)item.findViewById(R.id.local_upgrade);
		TextView progressState = (TextView)item.findViewById(R.id.download_prosses_status);
//		Log.d(TAG,
//				"----------------------draw download state: pkg="
//						+ app.getPackageName() + ", code="
//						+ app.getVersionCode() + //
//						", status=" + app.getStatus());
		int w = dip2px(context,25);
		int h = dip2px(context,25);
		
		boolean isShowProgressState = false;
		Drawable drawable;
		boolean clickable = true;
		if(app.getUpdateIgnore()){
			drawable = context.getResources().getDrawable(R.drawable.ic_ignored);  
			drawable.setBounds(0, 0, w, h); 
			downloadView.setCompoundDrawables(null, drawable, null, null);
			downloadView.setText(R.string.local_app_ignore);
			clickable = false;
			downloadView.setClickable(clickable);
			return;
		}
		
		switch (app.getStatus()) {
		case DOWNLOADING:
			drawable = context.getResources().getDrawable(R.drawable.ic_download_pause_normal);  
			drawable.setBounds(0, 0, w, h); 
			downloadView.setCompoundDrawables(null, drawable, null, null);
			downloadView.setText(R.string.download_pause);
			break;
		case UNINSTALL:
			drawable = context.getResources().getDrawable(R.drawable.ic_download_install_normal);  
			drawable.setBounds(0, 0, w, h); 
			downloadView.setCompoundDrawables(null, drawable, null, null);
			downloadView.setText(R.string.download_install);
			break;
		case PAUSE:
			if(app.getControl() == Downloads.Impl.CONTROL_PAUSED){
				isShowProgressState = true;
				drawable = context.getResources().getDrawable(R.drawable.ic_download_coutinue_normal);  
				drawable.setBounds(0, 0, w, h); 
				downloadView.setCompoundDrawables(null, drawable, null, null);
				downloadView.setText(R.string.download_resume);
			}else{
				drawable = context.getResources().getDrawable(R.drawable.ic_download_wait);  
				drawable.setBounds(0, 0, w, h); 
				downloadView.setCompoundDrawables(null, drawable, null, null);
				downloadView.setText(R.string.local_app_wait);
				clickable = false;
			}
			break;
		case UNDOWNLOAD:
			drawable = context.getResources().getDrawable(R.drawable.ic_update_normal);  
			drawable.setBounds(0, 0, w, h); 
			downloadView.setCompoundDrawables(null, drawable, null, null);
			if(app.getReDownloadFlag()){
				isShowProgressState = true;
				downloadView.setText(R.string.local_app_redownload);
			}else
				downloadView.setText(R.string.local_apps_upgrade);
			break;
			//yangmao add 0115 start
//		case INSTALL:
//			downloadView.setText(R.string.local_app_upgrade_succeed);
//			break;
			//yangmao add 0115 end
		default:
			drawable = context.getResources().getDrawable(R.drawable.ic_update_normal);  
			drawable.setBounds(0, 0, w, h); 
			downloadView.setCompoundDrawables(null, drawable, null, null);
			downloadView.setText(R.string.local_apps_upgrade);
			break;
		}
		downloadView.setClickable(clickable);
		if(isShowProgressState)
			progressState.setVisibility(View.VISIBLE);
		else
			progressState.setVisibility(View.GONE);
	}

	// ------App Download------
	private static void sendBroadcastToDownload(UpgradeApp currentApp,
			Context context) {
		// Log.i(TAG,"sendBroadcastToDownload, category:"+
		// currentApp.getCategory());

//		Intent intent = new Intent();
//		intent.setAction(DownloadConstant.ACTION_REQUEST_APPDOWNLOAD);
//		intent.putExtra("package_name", currentApp.getPackageName());
//		intent.putExtra("app_name", currentApp.getAppName());
//		intent.putExtra("version_code", currentApp.getVersionCode());
//		intent.putExtra("app_iconurl", currentApp.getIconAddr());
//		intent.putExtra("category", currentApp.getCategory());
//		intent.putExtra("version_name", currentApp.getVersionName());
//		context.sendBroadcast(intent);
		
		AppDownloadUrl downurl = new AppDownloadUrl();
		downurl.setDownurl(DownloadConstant.TYPE_DOWNLOAD_ACTION);
		
		downurl.setVersionName(currentApp.getVersionName());
		downurl.setPackage_name(currentApp.getPackageName());
		downurl.setVersion_code(currentApp.getVersionCode());
		downurl.setApp_name(currentApp.getAppName());
		downurl.setIconUrl(currentApp.getIconAddr());
		downurl.setCategory(currentApp.getCategory());
		downurl.setCallback(currentApp.getCallback());
		if (DownloadConstant.CATEGORY_WALLPAPER == currentApp.getCategory()) {
//		if (category.equals(DownloadConstant.CATEGORY_WALLPAPER_STRING)) {
			downurl.setMimeType(DownloadConstant.MIMETYPE_WALLPAPER);
		} else {
			downurl.setMimeType(DownloadConstant.MIMETYPE_APK);
		}
		sendMessage(DownloadHandler.getInstance(context),
				DownloadConstant.MSG_DOWN_LOAD_URL, downurl);
		
		
	}
	
	private static void sendMessage(Handler handler, int what, Object obj) {
		if (handler != null) {
			Message msg = new Message();
			msg.obj = obj;
			msg.what = what;
			handler.sendMessage(msg);
		}
	}
	
	public static void deleteDownloadTask(String packageName,String versioncode,
			Context context) {

//		Intent intent = new Intent();
//		intent.setAction(DownloadConstant.ACTION_REQUEST_APPDOWNLOAD);
//		intent.putExtra("package_name", currentApp.getPackageName());
//		intent.putExtra("app_name", currentApp.getAppName());
//		intent.putExtra("version_code", currentApp.getVersionCode());
//		intent.putExtra("app_iconurl", currentApp.getIconAddr());
//		intent.putExtra("category", currentApp.getCategory());
//		intent.putExtra("version_name", currentApp.getVersionName());
//		context.sendBroadcast(intent);
		
		Message msg = new Message();
		msg.what = DownloadHandler.MSG_DOWNLOAD_DELETE;
		Bundle bundle = new Bundle();
		bundle.putString("packageName", packageName);
		bundle.putString("versionCode", versioncode);
		msg.setData(bundle);
		DownloadHandler.getInstance(context).sendMessage(msg);
	}
	


	public static void loadImg(final String url,
			final OnImgLoadListener callback, final Context context) {
		if (TextUtils.isEmpty(url))
			return;
		AsyncTask.execute(new Runnable() {
			public void run() {
				GetImageRequest imageRequest = new GetImageRequest();
				imageRequest.setData(url);
				AmsSession.execute(context, imageRequest, new AmsCallback() {
					public void onResult(AmsRequest request, int code,
							final byte[] bytes) {
						// Log.d(TAG, "end load snapshot url=" + url + ", code="
						// + code
						// + ", bytes=" + (bytes == null ? null :
						// bytes.length));
						if (bytes == null)
							return;
						Drawable drawable = null;
						ByteArrayInputStream bs = null;
						GZIPInputStream input = null;
						try {
							input = new GZIPInputStream(
									new ByteArrayInputStream(bytes));
							drawable = Drawable.createFromStream(input, null);
						} catch (Exception e) {
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

	public static void prepareDownloadAll(final List<UpgradeApp> appList,
			final Context context) {
		if (DownloadConstant.CONNECT_TYPE_OTHER == DownloadConstant.getConnectType(context)) {
			Toast.makeText(context, R.string.error_network_state,
					Toast.LENGTH_SHORT).show();
			return;
		}
		if(LEJPConstant.getInstance().isNeedConfirmDownload(context)){
			LejingpingSettingsValues.popupWlanDownloadDialog(context);
			return;
		}
		
		Reaper.processReaper(context, UpgradeAppDownloadControl.REAPER_EVENT_CATEGORY, UpgradeAppDownloadControl.REAPER_EVENT_ID_UPDATEALL, Reaper.REAPER_NO_LABEL_VALUE, Reaper.REAPER_NO_INT_VALUE);
		for (UpgradeApp currentApp : appList) {
			if (currentApp == null || currentApp.getUpdateIgnore()) {
				continue;
			}
			Status status = currentApp.getStatus();
			if (status == null || Status.UNDOWNLOAD.equals(status)) {
				Log.i(TAG,  
						">>>>>>>UpgradeAppDownloadControl.prepareDownloadAll >> packagename:"
								+ currentApp.getPackageName() + ", status:"
								+ status);
				currentApp.setStatus(Status.DOWNLOAD_CLICK);
				currentApp.setControl(Downloads.Impl.CONTROL_RUN);
				sendBroadcastToDownload(currentApp,context);
			}else if(currentApp.getControl() == Downloads.Impl.CONTROL_PAUSED){
				currentApp.setControl(Downloads.Impl.CONTROL_RUN);
				LDownloadManager.getDefaultInstance(context).resumeTask(
						new DownloadInfo(currentApp.getPackageName(),
								currentApp.getVersionCode()));
			}
		}
	}
	
}
