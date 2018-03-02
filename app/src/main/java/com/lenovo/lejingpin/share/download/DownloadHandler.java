package com.lenovo.lejingpin.share.download;

import java.util.ArrayList;
import java.util.HashMap;

import com.lenovo.lejingpin.share.net.CacheManager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.customizer.Reaper;

public class DownloadHandler extends Handler {

	Context mContext;
	private static final String TAG = "DownloadHandler";
	private static DownloadHandler mDownloadHandler = null;

	public final static int MSG_DOWNLOAD_RESUME = 1;
	public final static int MSG_DOWNLOAD_DELETE = 2;
	
	private final static String REAPER_EVENT_CATEGORY = "LeJingpin";
	private final static String REAPER_EVENT_DOWNLOAD = "DownloadNum";
	private static Looper mHandleLooper = null;

	private ArrayList<DownLoadObserver> downloadObserverList = new ArrayList<DownLoadObserver>();

	private DownloadHandler(Context context,Looper loop) {
		super(loop);
		mContext = context;
		LDownloadManager.getDefaultInstance(mContext);
	}

	public synchronized static DownloadHandler getInstance(Context context) {
		if(mHandleLooper == null){
			HandlerThread thread = new HandlerThread("DownloadHandler",
					Process.THREAD_PRIORITY_BACKGROUND);
			thread.start();
			mHandleLooper = thread.getLooper();
		}
		if (mDownloadHandler == null ) {
			mDownloadHandler = new DownloadHandler(context,mHandleLooper);
		}
		return mDownloadHandler;
	}

	@Override
	public void handleMessage(Message msg) {
		Log.d(TAG,"--------------DownloadHandler-------------------msg.what: " + msg.what);
		switch (msg.what) {
		case DownloadConstant.MSG_DOWN_LOAD_URL: {
			final AppDownloadUrl downloadUrl = (AppDownloadUrl) msg.obj;
			if (downloadUrl != null) {
				String url = downloadUrl.getDownurl();
				final String package_name = downloadUrl.getPackage_name();
				final String version_code = downloadUrl.getVersion_code();
				final String app_name = downloadUrl.getApp_name();
				String iconUrl = downloadUrl.getIconUrl();
				final int category = downloadUrl.getCategory();
				String mime_type = downloadUrl.getMimeType();
				String version_name = downloadUrl.getVersionName();
				Log.i(TAG,
						"DownloadHandler.handleMessage( MSG_DOWN_LOAD_URL), name:"
								+ app_name + ",callback:" + downloadUrl.getCallback());
				if (url != null) {
					final DownloadInfo downloadInfo = new DownloadInfo();
					downloadInfo.setAppName(app_name);
					downloadInfo.setPackageName(package_name);
					downloadInfo.setVersionCode(version_code);
					downloadInfo.setVersionName(version_name);
					downloadInfo.setDownloadUrl(url);
					downloadInfo.setIconAddr(iconUrl);
					downloadInfo.setCategory(category);
					downloadInfo.setMimeType(mime_type);
					
					String path = LDownloadManager.getDefaultInstance(mContext)
							.addTask(downloadInfo);
					if (!TextUtils.isEmpty(path)) {
						if (path.equals("nosdcard")) {
							notifyFaild(
									package_name,
									version_code,
									app_name,
									category,
									DownloadConstant.ACTION_APK_FAILD_DOWNLOAD,
									DownloadConstant.FAILD_DOWNLOAD_NO_ENOUGHT_SPACE);
							return;
						}
						Uri uri = Uri.parse(path);
						removeDownloadObserver(package_name, version_code);
						// Log.i(TAG,
						// "DownloadHandler.handleMessage >> MSG_DOWN_LOAD_URL, new  DownLoadObserver :"+package_name);
						DownLoadObserver observer = new DownLoadObserver(package_name,version_code,downloadUrl.getCallback());
						mContext.getContentResolver().registerContentObserver(
								uri, true, observer);
						downloadObserverList.add(observer);

					} else {
						 Log.i(TAG,"DownloadHandler, add task error! call notifyFaild()");
						notifyFaild(package_name, version_code, app_name,category,
								DownloadConstant.ACTION_APK_FAILD_DOWNLOAD,
								DownloadConstant.FAILD_DOWNLOAD_OTHER_ERROR);
					}
				} else {
					notifyFaild(package_name, version_code, app_name,category,
							DownloadConstant.ACTION_APK_FAILD_DOWNLOAD, DownloadConstant.FAILD_DOWNLOAD_OTHER_ERROR);
				}
			} else {
				notifyFaild(null, null, null,-1,
						DownloadConstant.ACTION_APK_FAILD_DOWNLOAD, DownloadConstant.FAILD_DOWNLOAD_OTHER_ERROR);
			}
			break;
		}
		case MSG_DOWNLOAD_RESUME: {
			Cursor sc = null;
			try{
				sc = mContext.getContentResolver().query(
						Downloads.Impl.CONTENT_URI,
						null,
						Downloads.Impl.COLUMN_STATUS + " = ? or "
						+ Downloads.Impl.COLUMN_STATUS + " = ? or "
						+ Downloads.Impl.COLUMN_STATUS + " = ?",
						new String[] { String.valueOf(Downloads.STATUS_RUNNING),
								String.valueOf(Downloads.STATUS_RUNNING_PAUSED),
								String.valueOf(Downloads.STATUS_PENDING)},
								null);
				if (sc == null) {
					return;
				}
				while (sc.moveToNext()) {
					int id = sc.getInt(sc.getColumnIndex("_id"));
					String packageName = sc.getString(sc.getColumnIndex("pkgname"));
					String versionCode = sc.getString(sc
							.getColumnIndex("versioncode"));
					Uri uri = ContentUris.withAppendedId(
							Downloads.Impl.CONTENT_URI, id);
					addDownloadObserver(packageName, versionCode, uri);
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				if(sc!=null && !sc.isClosed()){
					sc.close();
				}
			}
			break;
		}
		case MSG_DOWNLOAD_DELETE: {
			Bundle bundle = msg.getData();
			String packageName = bundle.getString("packageName");
			String versionCode = bundle.getString("versionCode");
			removeDownloadObserver(packageName, versionCode);
			break;
		}
		case DownloadConstant.MSG_DOWN_LOAD_START: {
			AppDownloadUrl downloadUrl = (AppDownloadUrl) msg.obj;
			if (downloadUrl != null) {
				String url = downloadUrl.getDownurl();
				String package_name = downloadUrl.getPackage_name();
				String version_code = downloadUrl.getVersion_code();
				String app_name = downloadUrl.getApp_name();
				String iconUrl = downloadUrl.getIconUrl();
				String version_name = downloadUrl.getVersionName();
				int category = downloadUrl.getCategory();
				String mime_type = downloadUrl.getMimeType();
				Log.i(TAG,
						"DownloadHandler.handleMessage( MSG_DOWN_LOAD_START), name:"
								+ app_name + ", url:" + url);
				DownloadInfo downloadInfo = new DownloadInfo();
				downloadInfo.setPackageName(package_name);
				downloadInfo.setVersionCode(version_code);
				if (url != null
						&& !url.equals(DownloadConstant.TYPE_DOWNLOAD_ACTION)) {
					
					downloadInfo.setVersionName(version_name);
					downloadInfo.setAppName(app_name);
					downloadInfo.setDownloadUrl(url);
					downloadInfo.setIconAddr(iconUrl);
					downloadInfo.setCategory(category);
					downloadInfo.setMimeType(mime_type);
					LDownloadManager.getDefaultInstance(mContext).startTask(
							downloadInfo);
				} else {
					LDownloadManager.getDefaultInstance(mContext).deleteTask(
							downloadInfo);
					notifyFaild(package_name, version_code, app_name,category,
							DownloadConstant.ACTION_APK_FAILD_DOWNLOAD, DownloadConstant.FAILD_DOWNLOAD_OTHER_ERROR);
				}
			} else {
				notifyFaild(null, null, null,-1,
						DownloadConstant.ACTION_APK_FAILD_DOWNLOAD, DownloadConstant.FAILD_DOWNLOAD_OTHER_ERROR);
			}
			break;
		}
		}
		super.handleMessage(msg);
	}
	
	public boolean registerDownloadCallback(String pkName, String versionCode,AppDownloadUrl.Callback ck){
		for (DownLoadObserver observer : downloadObserverList) {
			if (observer.packageName.equals(pkName)
					&& observer.versionCode.equals(versionCode)) {
				observer.setCallback(ck);
				return true;
			}
		}
		
		return false;
	}

	private void addDownloadObserver(String packageName, String versionCode,
			Uri uri) {
		boolean bFound = false;
		for (DownLoadObserver observer : downloadObserverList) {
			if (observer.packageName.equals(packageName)
					&& observer.versionCode.equals(versionCode)) {
				bFound = true;
				break;
			}
		}
		if (!bFound) {
			DownLoadObserver observer = new DownLoadObserver(packageName,
					versionCode,null);
			mContext.getContentResolver().registerContentObserver(uri, true, observer);
			downloadObserverList.add(observer);
			notifyResumeDownload(packageName,versionCode);
		}
	}

	private void removeDownloadObserver(String packageName, String versionCode) {
		if (packageName == null || versionCode == null)
			return;
		for (DownLoadObserver observer : downloadObserverList) {
			if (observer.packageName.equals(packageName)
					&& observer.versionCode.equals(versionCode)) {
				downloadObserverList.remove(observer);
				mContext.getContentResolver().unregisterContentObserver(
						observer);
				return;
			}
		}
	}

	private DownloadInfo getDownloadInfo(String package_name,
			String version_code) {
		DownloadInfo info = new DownloadInfo();
		info.setPackageName(package_name);
		info.setVersionCode(version_code);
		return LDownloadManager.getDefaultInstance(mContext).getDownloadInfo(
				info);

	}
	
	private void notifyResumeDownload(String packageName, String versionCode) {
		Intent intent = new Intent(DownloadConstant.ACTION_DOWNLOAD_RESUME);
		intent.putExtra(DownloadConstant.EXTRA_PACKAGENAME, packageName);
		intent.putExtra(DownloadConstant.EXTRA_VERSION, versionCode);;
		mContext.sendBroadcast(intent);
	}

	private void notifyFaild(String packageName, String versionCode,
			String app_name,int category ,String action, int result) {
		Log.i(TAG, "    DownloadHandler.notifyFailed(), packagename:"
				+ packageName + ", name:" + app_name + "" + ",result:" + result);
		Intent intent = new Intent(action);
		intent.putExtra(DownloadConstant.EXTRA_PACKAGENAME, packageName);
		intent.putExtra(DownloadConstant.EXTRA_VERSION, versionCode);
		intent.putExtra(DownloadConstant.EXTRA_APPNAME, app_name);
		intent.putExtra(DownloadConstant.EXTRA_CATEGORY, category);
		intent.putExtra(DownloadConstant.EXTRA_RESULT, result);
		mContext.sendBroadcast(intent);
	}

	class DownLoadObserver extends ContentObserver {
		private String packageName;
		private String versionCode;
		private int previousStatus;
		AppDownloadUrl.Callback callBack;
		public DownLoadObserver(String pkname , String version, AppDownloadUrl.Callback ck) {
			super(null);
			callBack = ck;
			packageName = pkname;
			versionCode = version;
			DownloadInfo info = getDownloadInfo(packageName, versionCode);
			if (info != null)
				previousStatus = info.getDownloadStatus();

		}
		
		public void setCallback(AppDownloadUrl.Callback ck){
			callBack = ck;
		}
		
		private void downloadChanged(){
			DownloadInfo info = getDownloadInfo(packageName, versionCode);
			if (info == null) {
				return;
			}
			
			final String pkg = info.getPackageName();
			final String code = info.getVersionCode();
			String app_name = info.getAppName();
			final String installpath = info.getInstallPath();
			int progress = info.getProgress();
			int status = Helpers.getStatus(info.getDownloadStatus());
			String downloadStatus = null;
			int category = info.getCategory();
			 Log.i(TAG,
			 "    DownloadHandler.onChange(), DownloadInfo >> pkg : "+pkg + ",status :" + status + ",callback:" + callBack);

			switch (status) {
			case Downloads.STATUS_SUCCESS:
				downloadStatus = Status.UNINSTALL.value();
				String temp = pkg + "+" + code;
				removeDownloadObserver(packageName, versionCode);
				Reaper.processReaper(mContext, REAPER_EVENT_CATEGORY, REAPER_EVENT_DOWNLOAD,temp, Reaper.REAPER_NO_INT_VALUE);
				break;
			case Downloads.STATUS_RUNNING_PAUSED:
				downloadStatus = Status.PAUSE.value();
				break;
			case Downloads.STATUS_RUNNING:
				downloadStatus = Status.DOWNLOADING.value();
				break;
			case DownloadInfo.DOWNLOAD_ERROR_HTTP:
				notifyFaild(pkg, code, app_name,category,
						DownloadConstant.ACTION_APK_FAILD_DOWNLOAD,
						DownloadConstant.FAILD_DOWNLOAD_NETWORK_ERROR);
				downloadStatus = Status.UNDOWNLOAD.value();
				LDownloadManager.getDefaultInstance(mContext).deleteTask(
						info);
				break;
			case DownloadInfo.DOWNLOAD_ERROR_SDCARD:
				downloadStatus = Status.UNDOWNLOAD.value();
				notifyFaild(pkg, code, app_name,category,
						DownloadConstant.ACTION_APK_FAILD_DOWNLOAD,
						DownloadConstant.FAILD_DOWNLOAD_NO_ENOUGHT_SPACE);
				LDownloadManager.getDefaultInstance(mContext).deleteTask(
						info);
				break;
//			case Downloads.STATUS_INSTALL:
//				downloadStatus = Status.INSTALL.value();
//				LDownloadManager.getDefaultInstance(mContext).deleteTask(
//						info);
//				break;
			default:
					break;
			}
			if (status != previousStatus) {
				previousStatus = status;
				if (downloadStatus != null)
					notifyStatusChange(progress, downloadStatus,category,installpath);
			}
			if(callBack != null){
				callBack.doCallback(info);
			}
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			new Thread(new Runnable() {
				@Override
				public void run() {
					downloadChanged();
				}
				
			}).start();
		}

		private void notifyStatusChange(int progress, String downloadStatus, int category,String installpath) {
			Log.d(TAG,
					"          DownloadHandler.notifyStatusChange , package:"
							+ packageName + "progress : " + progress
							+ ", downloadStatus:" + downloadStatus);
			Intent intent = new Intent(
					DownloadConstant.ACTION_DOWNLOAD_STATE_CHANGED);
			intent.putExtra(DownloadConstant.EXTRA_PACKAGENAME, packageName);
			intent.putExtra(DownloadConstant.EXTRA_VERSION, versionCode);
			intent.putExtra(DownloadConstant.EXTRA_STATUS, downloadStatus);
			intent.putExtra(DownloadConstant.EXTRA_CATEGORY, category);
			intent.putExtra(DownloadConstant.EXTRA_PROGRESS, progress);
			intent.putExtra(DownloadConstant.EXTRA_INSTALLPATH, installpath);
			mContext.sendBroadcast(intent);
		}
	}

	public static enum Status {
		 UNDOWNLOAD("undownload"),
		DOWNLOADING("downloading"), UNINSTALL("uninstall"), UNUPDATE("unupdate"), PAUSE(
				"pause"),
		INSTALL("install");

		private Status(String value) {
			this.value = value;
		}

		private String value;

		public String value() {
			return value;
		}
		/*
		 * public static Status parseStatus(String value) { for (Status status :
		 * Status.values()) { if(status.value.equals(value)) { return status; }
		 * } return null; }
		 */
	}
}
