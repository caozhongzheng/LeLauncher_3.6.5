package com.lenovo.lejingpin.share.download;

import java.io.File;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;
import com.lenovo.launcher.R;

public class DownloadHelpers {

	private static final String TAG = "DownloadHelpers";
	//test by dining 2013-06-24 lejingpin->xlejingpin
	private static final Uri uri = Uri
			.parse("content://com.lenovo.lejingpin.share.download/download");
	private static final String DOWNLOAD_PATH = ".IdeaDesktop/LeDownload/download/";
	private static final String AGENT_DEFAULT = "AppStore3";
	private static final String AGENT_LELAUNCHER = "LeLauncherServer";
	private static final int DOWNLOAD_NOTIFICATION_ID = R.drawable.actionbar_icon;
	private static final int DOWNLOAD_COMPLETED_NOTIFICATION_ID = R.drawable.about_bg;
	public static String mNetworkType;
	private static NotificationManager mDownloadNotificationMgr;
//	private static int mDownloadCount = 0;
	private DownloadHelpers(){
		
	}
	/**
	 * Determine whether the download list
	 */
	public static boolean itemExistence(Context context, DownloadInfo appInfo) {
		if (null == appInfo)
			return false;
		Cursor sc = context.getContentResolver().query(
				uri,
				null,
				"pkgname = ? and versioncode = ?",
				new String[] { appInfo.getPackageName(),
						appInfo.getVersionCode() }, null);
		try {
			if (sc.moveToFirst()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sc.close();
		}
		return false;
	}

	private synchronized static void deleteDownloadFile(String installPath){
		if (null != installPath) {
			File f = new File(installPath);
			if (f.exists()) {
				f.delete();
			}
			// Log.i(TAG,"-------------delete installPath:"+
			// installPath);
			String dir = installPath.substring(0,
					installPath.lastIndexOf(File.separator));
			String filename = installPath.substring(installPath
					.lastIndexOf(File.separator) + 1);
			// String path = dir + File.separator + "lca" +
			// filename.replace(".lca", ".apk");
			File lca = new File(dir + File.separator + "lca"
					+ filename.replace(".lca", ".apk"));
			if (lca.exists()) {
				lca.delete();
			}
		}
	}
	
//	public static void notifyDownloadCountChanged(Context context,int count){
//		if(mDownloadCount == count)
//			return;
//		else{
//			mDownloadCount = count;
//		}
//		Intent intent = new Intent(LDownloadManager.ACTION_DOWNLOAD_COUNT_CHANGED);
//		intent.putExtra("count", count);
//		context.sendBroadcast(intent);
//	}
	
	public static void updateDownloadNotifacition(Context context,int count){

		if (null == mDownloadNotificationMgr) {
			mDownloadNotificationMgr = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
		}
		
		Log.d(TAG,"updateDownloadNotifacition,  count:" + count );
		if(0 == count){
			Log.d(TAG,"----updateDownloadNotifacition--------cancel" );
			mDownloadNotificationMgr.cancel(DOWNLOAD_NOTIFICATION_ID);
			return;
		}
		
		Bundle b = new Bundle();
		b.putString(LDownloadManager.NOTIFY_CLICK_TAG,
				LDownloadManager.STATUS_VALUES[1]);
		
		Intent notificationIntent = new Intent(
				LDownloadManager.ACTION_NOTIFICATION_CLICKED);
		notificationIntent.putExtras(b);
		
		PendingIntent contentIntent = PendingIntent.getBroadcast(context,
				DOWNLOAD_NOTIFICATION_ID, notificationIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		String title = context.getString(R.string.download_notification_title);
		
		Notification n = new Notification(R.drawable.download_notifycation,
				title, System.currentTimeMillis());
		
		n.flags |= Notification.FLAG_ONGOING_EVENT;
		n.setLatestEventInfo(context, 
					title, 
					context.getString(R.string.download_notification_content,count), contentIntent);
		mDownloadNotificationMgr.notify(DOWNLOAD_NOTIFICATION_ID, n);
	}
	private static void updateDownloadCompletedNotifacition(Context context,int count){
		if (null == mDownloadNotificationMgr) {
			mDownloadNotificationMgr = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			
		}
		Log.d(TAG,"updateDownloadCompletedNotifacition,  count:" + count);
		if(count == 0){
			mDownloadNotificationMgr.cancel(DOWNLOAD_COMPLETED_NOTIFICATION_ID);
			return;
		}
		
		Bundle b = new Bundle();
		b.putString(LDownloadManager.NOTIFY_CLICK_TAG,
				LDownloadManager.STATUS_VALUES[1]);
		
		Intent notificationIntent = new Intent(
				LDownloadManager.ACTION_NOTIFICATION_CLICKED);
		notificationIntent.putExtras(b);
		
		PendingIntent contentIntent = PendingIntent.getBroadcast(context,
				DOWNLOAD_COMPLETED_NOTIFICATION_ID, notificationIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		String title = context.getString(R.string.download_notification_title);
		
		Notification n = new Notification(R.drawable.download_notifycation,
				title, System.currentTimeMillis());
		n.flags |= Notification.FLAG_AUTO_CANCEL;
		n.setLatestEventInfo(context, 
				title, 
				context.getString(R.string.download_completed_notification_content,count), contentIntent);
		mDownloadNotificationMgr.notify(DOWNLOAD_COMPLETED_NOTIFICATION_ID, n);
	}
	/**
	 * delete, suspension and continue to designated download items
	 */
	public synchronized static void doAction(Context context,
			DownloadInfo appInfo, int doStatus) {
		if (null == appInfo)
			return;
		if (null == appInfo.getPackageName()
				|| null == appInfo.getVersionCode()) {
			return;
		}
		ContentResolver cr = context.getContentResolver();
		Cursor sc = context.getContentResolver().query(
				uri,
				null,
				"pkgname = ? and versioncode = ?",
				new String[] { appInfo.getPackageName(),
						appInfo.getVersionCode() }, null);
		context.startService(new Intent(context, DownloadService.class));
		try {
			while (sc.moveToNext()) {
				String id = sc.getString(sc.getColumnIndex("_id"));
				String installPath = sc.getString(sc.getColumnIndex("_data"));
				// String status = sc.getString(sc.getColumnIndex("status"));
				String wifistatus = sc.getString(sc
						.getColumnIndex("wifistatus"));
				String appName = sc.getString(sc.getColumnIndex("title"));
				long currentBytes = sc.getLong(sc
						.getColumnIndex("current_bytes"));
				long totalBytes = sc.getLong(sc.getColumnIndex("total_bytes"));
				// String category =
				// sc.getString(sc.getColumnIndex("category"));

				int p = (int) (currentBytes / (float) totalBytes * 100);
				appInfo.setProgress(p);
				appInfo.setAppName(appName);
				appInfo.setId(id);
				appInfo.setWifistatus(Integer.parseInt(wifistatus));
				if(DownloadInfo.REDOWNLOAD == doStatus){
					Log.d(TAG,
							"^^^^^^^^^^^^^^^^^^^ DownloadHelpers.doAction(),"
									+ appInfo.getAppName() + " redownload !");
					if (null != installPath) {
						deleteDownloadFile(installPath);
					}
					ContentValues values = new ContentValues();
					BeanDownload downloadInfo = DownloadQueueHandler
							.getInstance().getDownloadInfoById(
									Integer.valueOf(id));
					BeanDownload currentDownload = DownloadQueueHandler.getInstance().getCurrentDownload();
//					Log.d(TAG,
//							"^^^^^^^^^^1111^^^^^^^^^ DownloadHelpers.doAction(),redownload:"
//									+ downloadInfo + ", current download:" 
//									+ currentDownload);
					if (downloadInfo != null &&  currentDownload != null &&
							downloadInfo.mId == currentDownload.mId) {
						downloadInfo.mStatus = Downloads.Impl.STATUS_RESTART;
						values.put(Downloads.Impl.COLUMN_CURRENT_BYTES, 0);
						cr.update(uri, values, "_ID = ?", new String[] { id });
//						DownloadInfoContainer.updateDBAndBufferById(context, uri, values, id);
					}else{
						if(Helpers.isNetworkAvailable(context))
							values.put(Downloads.Impl.COLUMN_STATUS, Downloads.Impl.STATUS_RUNNING);
						else
							values.put(Downloads.Impl.COLUMN_STATUS, Downloads.Impl.STATUS_RUNNING_PAUSED);
						values.put(Downloads.Impl.COLUMN_CONTROL,
								Downloads.CONTROL_RUN);
						values.put(Downloads.Impl.COLUMN_HANDTOPAUSE,
								LDownloadManager.STATUS_VALUES[0]);
						values.put(Downloads.Impl.COLUMN_WIFISTATUS, 0);
						values.put(Downloads.Impl.COLUMN_CURRENT_BYTES, 0);
						cr.update(uri, values, "_ID = ?", new String[] { id });
//						DownloadInfoContainer.updateDBAndBufferById(context, uri, values, id);
					}
					
				}else if (DownloadInfo.DELETE == doStatus) {
					Log.d(TAG,
							"^^^^^^^^^^^^^^^^^^^ DownloadHelpers.doAction(),"
									+ appInfo.getAppName() + " delete !");
					cr.delete(uri, "_ID = ?", new String[] { id });
//					DownloadInfoContainer.deleteDBAndBufferById(context, uri, id);

					if (null != installPath) {
						File f = new File(installPath);
						if (f.exists()) {
							f.delete();
						}
						// Log.i(TAG,"-------------delete installPath:"+
						// installPath);
						String dir = installPath.substring(0,
								installPath.lastIndexOf(File.separator));
						String filename = installPath.substring(installPath
								.lastIndexOf(File.separator) + 1);
						// String path = dir + File.separator + "lca" +
						// filename.replace(".lca", ".apk");
						File lca = new File(dir + File.separator + "lca"
								+ filename.replace(".lca", ".apk"));
						if (lca.exists()) {
							lca.delete();
						}
					}

					BeanDownload downloadInfo = DownloadQueueHandler
							.getInstance().getDownloadInfoById(
									Integer.valueOf(id));
					if (downloadInfo != null) {
						downloadInfo.mStatus = Downloads.Impl.STATUS_CANCELED;
						DownloadQueueHandler.getInstance()
								.deleteDownloadInfoById(Integer.valueOf(id));
					}

					Message msg = new Message();
					msg.what = DownloadHandler.MSG_DOWNLOAD_DELETE;
					Bundle bundle = new Bundle();
					bundle.putString("packageName", appInfo.getPackageName());
					bundle.putString("versionCode", appInfo.getVersionCode());
					msg.setData(bundle);
					DownloadHandler.getInstance(context).sendMessage(msg);
//					notifyDelete(context, appInfo);

				} else if (DownloadInfo.PAUSE == doStatus) {
					ContentValues values = new ContentValues();
					values.put(Downloads.Impl.COLUMN_CONTROL,
							Downloads.CONTROL_PAUSED);
					values.put(Downloads.Impl.COLUMN_HANDTOPAUSE,
							LDownloadManager.STATUS_VALUES[1]);
					cr.update(uri, values, "_ID = ?", new String[] { id });
//					DownloadInfoContainer.updateDBAndBufferById(context, uri, values, id);
//					notifyPause(context, appInfo);
					Log.d(TAG,
							"DownloadHelpers.doAction(),"
									+ appInfo.getPackageName() + " pause !");
				} else if (DownloadInfo.CONTINUE == doStatus) {
					ContentValues values = new ContentValues();
					values.put(Downloads.Impl.COLUMN_CONTROL,
							Downloads.CONTROL_RUN);
					values.put(Downloads.Impl.COLUMN_HANDTOPAUSE,
							LDownloadManager.STATUS_VALUES[0]);
					values.put(Downloads.Impl.COLUMN_WIFISTATUS, 0);
					cr.update(uri, values, "_ID = ?", new String[] { id });
//					DownloadInfoContainer.updateDBAndBufferById(context, uri, values, id);
					Log.d(TAG,
							"DownloadHelpers.doAction(),"
									+ appInfo.getPackageName() + " continue !");
				} else if (DownloadInfo.START == doStatus) {
					ContentValues values = new ContentValues();
					Log.i(TAG,
							"DownloadHelpers.doAction(), DownloadInfo.START, url:"
									+ appInfo.getDownloadUrl());
					values.put(Downloads.Impl.COLUMN_URI,
							appInfo.getDownloadUrl());
					cr.update(uri, values, "_ID = ?", new String[] { id });
//					DownloadInfoContainer.updateDBAndBufferById(context, uri, values, id);

					DownloadQueueHandler.getInstance().updateDownloadInfoById(
							context, Integer.valueOf(id),
							appInfo.getDownloadUrl());
					Log.d(TAG,
							"DownloadHelpers.doAction(),"
									+ appInfo.getPackageName() + " start !");
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
//			notifyDownloadCountChanged(context, DownloadInfoContainer.getDownloadSize(context));
			sc.close();
		}
	}

	/**
	 * Remove all download information
	 */
	public static synchronized void deleteAllTable(Context context) {
		Log.i(TAG, "DownloadHelpers.deleteAllTable()");
		ContentResolver cr = context.getContentResolver();
		Cursor sc = context.getContentResolver().query(uri, null, null, null,
				null);
		while (sc.moveToNext()) {
			String installPath = sc.getString(sc.getColumnIndex("_data"));
			File f = new File(installPath);
			if (f.exists()) {
				f.delete();
				String dir = installPath.substring(0,
						installPath.lastIndexOf(File.separator));
				String filename = installPath.substring(installPath
						.lastIndexOf(File.separator) + 1);
				File lca = new File(dir + File.separator + "lca"
						+ filename.replace(".lca", ".apk"));
				if (lca.exists()) {
					lca.delete();
				}
			}
		}
		try {
			cr.delete(uri, null, null);
//			DownloadInfoContainer.deleteDBAndBufferById(context, uri, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sc.close();
		}
	}

	/**
	 * Support 2.2 download module
	 */

	// private static final String mimeType =
	// "application/vnd.android.package-archive";

	public static String preDownload(Context context, DownloadInfo info) {
		if (null == info)
			return null;
		Log.i(TAG,
				">>>>>>>>>> DownloadHelpers.preDownload, package:"
						+ info.getPackageName() + " ,versioncode:"
						+ info.getVersionCode() + " ,name:" + info.getAppName()
						+ " ,path:" + info.getDownloadUrl() + " ,category:"
						+ info.getCategory());
		String url = info.getDownloadUrl();
		if (url == null || url.length() <= 0) {
			Log.i(TAG,
					"DownloadHelpers.preDownload, download url is null! Or download url length is 0! error!");
			return null;
		}
		ContentValues values = new ContentValues();
		// zdx modify
		String packageName = info.getPackageName();
		String versionCode = info.getVersionCode();
		if(versionCode.isEmpty()){
			versionCode = DownloadConstant.DEFAULT_VERSION_CODE;
		}
		values.put(Downloads.Impl.COLUMN_PKGNAME, packageName);
		values.put(Downloads.Impl.COLUMN_VERSIONCODE, versionCode);
		values.put(Downloads.Impl.COLUMN_VERSIONNAME, info.getVersionName());
		values.put(Downloads.Impl.COLUMN_APPSIZE, info.getAppSize());
		values.put(Downloads.Impl.COLUMN_ICONADDR, info.getIconAddr());
		values.put(Downloads.Impl.COLUMN_WIFISTATUS, info.getFlag());
		values.put(Downloads.Impl.COLUMN_URI, url);
		values.put(Downloads.Impl.COLUMN_NOTIFICATION_PACKAGE,
				context.getPackageName());
		values.put(Downloads.Impl.COLUMN_VISIBILITY,
				Downloads.Impl.VISIBILITY_VISIBLE);
		values.put(Downloads.Impl.COLUMN_MIME_TYPE, info.getMimeType());
		values.put(Downloads.Impl.COLUMN_TITLE, info.getAppName());
		values.put(Constants.UID, Binder.getCallingUid());
		// zdx modify
		int category = info.getCategory();
		values.put(Downloads.Impl.COLUMN_CATEGORY, info.getCategory());
		String userAgent = AGENT_DEFAULT;
		if (category != DownloadConstant.CATEGORY_ERROR && category > 100)
			userAgent = AGENT_LELAUNCHER;
		values.put(Downloads.Impl.COLUMN_USER_AGENT, userAgent);

		if (info.getFlag() == DownloadInfo.FLAG_WIFI
				&& LDownloadManager.STATUS_VALUES[0]
						.equals(currentNetworkType(context))) {
			values.put(Downloads.Impl.COLUMN_HANDTOPAUSE,
					LDownloadManager.STATUS_VALUES[0]);
			values.put(Downloads.Impl.COLUMN_CONTROL, Downloads.CONTROL_PAUSED);
		} else {
			values.put(Downloads.Impl.COLUMN_CONTROL, Downloads.CONTROL_RUN);
		}
		values.put(Downloads.Impl.COLUMN_STATUS, Downloads.Impl.STATUS_PENDING);
		values.put(Downloads.Impl.COLUMN_LAST_MODIFICATION,
				System.currentTimeMillis());

		String savePath = null;
		// zdx modify
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			values.put(Downloads.Impl.COLUMN_DESTINATION,
					Downloads.DESTINATION_USERCHOOSED);
			savePath = Environment.getExternalStorageDirectory()
					+ File.separator + DOWNLOAD_PATH;
			File saveFile = new File(savePath);
			if (!saveFile.isDirectory()) {
				saveFile.mkdirs();
			}
		} else {
			return "nosdcard";
		}

		UUID uuid = UUID.randomUUID();
		String fileName = "";
		if (packageName != null && !packageName.isEmpty())
			fileName = packageName + "-";
		if (versionCode != null && !versionCode.isEmpty())
			fileName = fileName + info.getVersionCode() + "-";
		fileName = savePath + fileName + uuid.toString();
		Log.i(TAG, "          DownloadHelpers.preDownload, filename:"
				+ fileName);
		values.put(Downloads._DATA, fileName);
		values.put(Downloads.Impl.COLUMN_EXT_1, uuid.toString());

		values.put(Downloads.Impl.COLUMN_NO_INTEGRITY, true);
		final Uri contentUri = context.getContentResolver().insert(uri, values);
//		final Uri contentUri = DownloadInfoContainer.insertDBAndBuffer(context, uri, values);
		if (null != contentUri) {
//			notifyDownloadCountChanged(context,DownloadInfoContainer.getDownloadSize(context));
//			context.startService(new Intent(context, DownloadService.class));
			return contentUri.toString();
		}
		Log.i(TAG, "DownloadHelpers.preDownload, insert downloads db error!");
		return null;

	}

	/**
	 * Download the current environment is still waiting for WIFI
	 */
	/*
	 * private static void checkQiWait(Context context,DownloadInfo di){ if(
	 * null == di ) return; SharedPreferences downloadsp =
	 * context.getSharedPreferences("download",
	 * Context.MODE_WORLD_WRITEABLE+Context
	 * .MODE_WORLD_READABLE+Context.MODE_APPEND); boolean wifiStatus =
	 * downloadsp.getBoolean("wifi", false); if(wifiStatus &&
	 * LDownloadManager.STATUS_VALUES[0].equals(currentNetworkType(context))){
	 * di.setFlag(DownloadInfo.FLAG_WIFI); }else if(wifiStatus &&
	 * LDownloadManager.STATUS_VALUES[1].equals(currentNetworkType(context))){
	 * di.setFlag(DownloadInfo.FLAG_WIFI); }else {
	 * //Log.d(TAG,"DownloadHelpers.checkQiWait,Download FLAG_START");
	 * di.setFlag(DownloadInfo.FLAG_START); } }
	 */

	/**
	 * The current network signal type
	 */
	public static String currentNetworkType(Context mContext) {
		if (null == mNetworkType) {
			ConnectivityManager connectivityManager = (ConnectivityManager) mContext
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetInfo = connectivityManager
					.getActiveNetworkInfo();
			if (activeNetInfo != null
					&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				mNetworkType = LDownloadManager.STATUS_VALUES[1];
			}else
				mNetworkType = LDownloadManager.STATUS_VALUES[0];
		}
		return mNetworkType;
	}

	/**
	 * Download the entry to determine whether a state of waiting WIFI
	 */
	public static synchronized boolean waitWifiStatus(Context context, String status,
			String wifistatus, String handpause) {
		if (downloadsp == null) {
			downloadsp = context.getSharedPreferences("download",
					Context.MODE_WORLD_WRITEABLE + Context.MODE_WORLD_READABLE
							+ Context.MODE_APPEND);
		}
		boolean wifiStatus = downloadsp.getBoolean("wifi", false);
		if (LDownloadManager.STATUS_VALUES[0]
				.equals(currentNetworkType(context)) && wifiStatus) {
			if ((String.valueOf(DownloadInfo.DOWNLOAD_READY).equals(status) && LDownloadManager.STATUS_VALUES[1]
					.equals(wifistatus))
					|| (LDownloadManager.STATUS_VALUES[1].equals(wifistatus) && LDownloadManager.STATUS_VALUES[0]
							.equals(handpause))) {
				return true;
			}

		}
		return false;
	}

	/**
	 * Network signal switch operation If you switch to perform Download WIFI
	 * signal; If you switch to 3G when the switch means to suspend download
	 */
	public static synchronized void changeNetworkAction(Context context, int networkType) {
		// Log.d(TAG," DownloadHelpers.changeNetworkAction, change network , networkType:"+
		// networkType);

		ContentResolver cr = context.getContentResolver();
		Cursor sc  = null;
		try{
			sc = context.getContentResolver().query(
					uri,
					null,
					Downloads.Impl.COLUMN_STATUS + " >= 190 and "
							+ Downloads.Impl.COLUMN_STATUS + " <= 193", null, null);
			if (sc == null)
				return;
			while (sc.moveToNext()) {
				// Log.d(TAG,"get all action download item.");
				String id = sc.getString(sc.getColumnIndex("_id"));
				String pkgName = sc.getString(sc.getColumnIndex("pkgname"));
				String versionCode = sc.getString(sc.getColumnIndex("versioncode"));
				String status = sc.getString(sc.getColumnIndex("status"));
				String wifistatus = sc.getString(sc.getColumnIndex("wifistatus"));
				String handpause = sc.getString(sc.getColumnIndex("handpause"));
				String appName = sc.getString(sc.getColumnIndex("title"));
				long currentBytes = sc.getLong(sc.getColumnIndex("current_bytes"));
				long totalBytes = sc.getLong(sc.getColumnIndex("total_bytes"));
				// String category = sc.getString(sc.getColumnIndex("category"));

				int p = (int) (currentBytes / (float) totalBytes * 100);

				if ((String.valueOf(DownloadInfo.DOWNLOAD_READY).equals(status) && LDownloadManager.STATUS_VALUES[1]
						.equals(wifistatus))
						|| (LDownloadManager.STATUS_VALUES[1].equals(wifistatus) && LDownloadManager.STATUS_VALUES[0]
								.equals(handpause))) {
					DownloadInfo di = new DownloadInfo().setPackageName(pkgName)
							.setVersionCode(versionCode);
					di.setProgress(p);
					di.setAppName(appName);
					di.setId(id);

					if (ConnectivityManager.TYPE_WIFI == networkType) {
						Log.d(TAG,
								"DownloadHelpers.changeNetworkAction, wifi connected, update Downloads.CONTROL_RUN !");
						ContentValues values = new ContentValues();
						values.put(Downloads.Impl.COLUMN_CONTROL,
								Downloads.CONTROL_RUN);
						cr.update(uri, values, "_ID = ?", new String[] { id });
//						DownloadInfoContainer.updateDBAndBufferById(context, uri, values, id);
//						if (null != mNotificationMgr) {
//							mNotificationMgr.cancel(Integer.parseInt(id));
//						}
					} else {
						Log.d(TAG,
								"DownloadHelpers.changeNetworkAction, other connected, update Downloads.CONTROL_PAUSED !");
						ContentValues values = new ContentValues();
						values.put(Downloads.Impl.COLUMN_CONTROL,
								Downloads.CONTROL_PAUSED);
						cr.update(uri, values, "_ID = ?", new String[] { id });
//						DownloadInfoContainer.updateDBAndBufferById(context, uri, values, id);
//						notifyPause(context, di);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(sc!=null && !sc.isClosed()){
				sc.close();
			}
		}
	}
	
	/**
	 * WIFI status changes Check the current network signal if the 3G network,
	 * the download is set to pause (handpause = 0); Has been suspended & &
	 * handpause = 0 set as to download
	 */
	public static synchronized void updataForWifi(Context context, boolean flag) {
		Log.i(TAG, "DownloadHelpers.updateForWifi, support wifi status change:"
				+ flag);
		if (LDownloadManager.STATUS_VALUES[0]
				.equals(currentNetworkType(context))) {
			ContentResolver cr = context.getContentResolver();
			if (flag) {
				Cursor sc = context.getContentResolver().query(
						uri,
						null,
						Downloads.Impl.COLUMN_CONTROL + " = ? and "
								+ Downloads.Impl.COLUMN_STATUS + " = ?",
						new String[] { String.valueOf(Downloads.CONTROL_RUN),
								String.valueOf(Downloads.STATUS_RUNNING) },
						null);
				if (sc == null) {
					return;
				}
				while (sc.moveToNext()) {
					String id = sc.getString(sc.getColumnIndex("_id"));
					String pkgName = sc.getString(sc.getColumnIndex("pkgname"));
					String versionCode = sc.getString(sc
							.getColumnIndex("versioncode"));
					String appName = sc.getString(sc.getColumnIndex("title"));
					long currentBytes = sc.getLong(sc
							.getColumnIndex("current_bytes"));
					long totalBytes = sc.getLong(sc
							.getColumnIndex("total_bytes"));
					int p = (int) (currentBytes / (float) totalBytes * 100);
					DownloadInfo di = new DownloadInfo()
							.setPackageName(pkgName)
							.setVersionCode(versionCode);
					di.setProgress(p);
					di.setAppName(appName);
					di.setId(id);
					ContentValues values = new ContentValues();
					values.put(Downloads.Impl.COLUMN_CONTROL,
							Downloads.CONTROL_PAUSED);
					values.put(Downloads.Impl.COLUMN_HANDTOPAUSE,
							LDownloadManager.STATUS_VALUES[0]);
					values.put(Downloads.Impl.COLUMN_WIFISTATUS,
							LDownloadManager.STATUS_VALUES[1]);
					cr.update(uri, values, "_ID = ?", new String[] { id });
//					DownloadInfoContainer.updateDBAndBufferById(context, uri, values, id);
//					notifyPause(context, di);

				}
				if (sc.getCount() > 0) {
					Toast.makeText(context,
							R.string.download_setting_wifi_toast,
							Toast.LENGTH_SHORT).show();
				}
				sc.close();
			} else {
				Cursor sc = cr.query(uri, null, Downloads.Impl.COLUMN_CONTROL
						+ " = ? and " + Downloads.Impl.COLUMN_HANDTOPAUSE
						+ " = ?",
						new String[] {
								String.valueOf(Downloads.CONTROL_PAUSED),
								LDownloadManager.STATUS_VALUES[0] }, null);
				if (sc == null)
					return;
				while (sc.moveToNext()) {
					String id = sc.getString(sc.getColumnIndex("_id"));
					ContentValues values = new ContentValues();
					values.put(Downloads.Impl.COLUMN_CONTROL,
							Downloads.CONTROL_RUN);
					cr.update(uri, values, "_ID = ?", new String[] { id });
//					DownloadInfoContainer.updateDBAndBufferById(context, uri, values, id);
//					if (null != mNotificationMgr) {
//						mNotificationMgr.cancel(Integer.parseInt(id));
//					}
				}
				// if(sc.getCount()>0){
				// Toast.makeText(context,
				// R.string.download_setting_cancel_wifi_toast,
				// Toast.LENGTH_SHORT).show();
				// }
				sc.close();
			}
		}

	}

	public static int getAllDownloadsCount(Context context){
//		return DownloadInfoContainer.getDownloadSize(context);
		
		int count = 0;
		Cursor sc = context.getContentResolver().query(
				uri,
				null,
				Downloads.Impl.COLUMN_STATUS + " != ? and "
						+ Downloads.Impl.COLUMN_STATUS + " != ?",
				new String[] { String.valueOf(Downloads.STATUS_SUCCESS),
						String.valueOf(Downloads.STATUS_INSTALL)}, null);
		if(sc == null)
			return count;
		try {
			count = sc.getCount();
		} catch (Exception e) {
			Log.d(TAG,"-----------getAllDownloadsCount--Exception---");
			e.printStackTrace();
		} finally {
			sc.close();
		}
		return count;
		
	}
	/**
	 * Get information about all running downloads
	 */
	public static synchronized List<DownloadInfo> getAllRunDownloads(Context context) {
		List<DownloadInfo> infos = new ArrayList<DownloadInfo>();
		Cursor sc = context.getContentResolver().query(
				uri,
				null,
				Downloads.Impl.COLUMN_CONTROL + " = ? and "
						+ Downloads.Impl.COLUMN_STATUS + " = ?",
				new String[] { String.valueOf(Downloads.CONTROL_RUN),
						String.valueOf(Downloads.STATUS_RUNNING) }, null);
		if(sc == null)
			return null;
		try {
			while (sc.moveToNext()) {
				DownloadInfo info = new DownloadInfo();
				String pkgName = sc.getString(sc.getColumnIndex("pkgname"));
				String versionCode = sc.getString(sc.getColumnIndex("versioncode"));
				String appName = sc.getString(sc.getColumnIndex("title"));
				String iconAddr = sc.getString(sc.getColumnIndex("iconaddr"));
				String installPath = sc.getString(sc.getColumnIndex("_data"));
				int status = sc.getInt(sc.getColumnIndex("status"));
				String wifistatus = sc.getString(sc.getColumnIndex("wifistatus"));
				String handpause = sc.getString(sc.getColumnIndex("handpause"));
				long currentBytes = sc.getInt(sc.getColumnIndex("current_bytes"));
				long totalBytes = sc.getInt(sc.getColumnIndex("total_bytes"));
				// zdx modify
				int category = sc.getInt(sc.getColumnIndex("category"));
				//xujing3 added
				int control = sc.getInt(sc.getColumnIndex(Downloads.Impl.COLUMN_CONTROL));
				
				int progress = 0;
				// Log.i(TAG,"DownloadHelpers.getAllRunDownloads, currentBytes:"+currentBytes+", totalBytes:"+totalBytes);
				if (totalBytes != 0 && currentBytes != 0) {
					progress = getProgresss(currentBytes, totalBytes);
				}
	
				// zdx modify
				if ((status != Downloads.STATUS_INSTALL)
						&& (status != Downloads.STATUS_SUCCESS)) {
					if (progress == 100){
						status = Downloads.STATUS_SUCCESS;
					}
//					else if (waitWifiStatus(context, String.valueOf(status), wifistatus,
//							handpause)) {
//						wifistatus = LDownloadManager.STATUS_VALUES[1];
//						status = DownloadInfo.DOWNLOAD_WAIT_WIFI;
//					}
					else{
						status = Helpers.getStatus(status);
					}

				}
	
				info.setPackageName(pkgName);
				info.setVersionCode(versionCode);
				info.setAppName(appName);
				info.setAppSize(String.valueOf(totalBytes));
				info.setProgress((int) progress);
				info.setIconAddr(iconAddr);
				info.setInstallPath(installPath);
				info.setWifistatus(Integer.parseInt(wifistatus));
				info.setDownloadStatus(status);
				// zdx modify
				info.setCategory(category);
				//xujing3 added
				info.setControl(control);
				
				infos.add(info);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sc.close();
		}
		return infos;

	}

	/**
	 * Get all download items
	 */
	public static synchronized List<DownloadInfo> getAllDownloadInfo(Context context) {
//		return DownloadInfoContainer.getAll(context);
		
		List<DownloadInfo> infos = new ArrayList<DownloadInfo>();
		Cursor sc = context.getContentResolver().query(uri, null, null, null,
				null);
		if(sc == null)
			return null;
		try {
			while (sc.moveToNext()) {
				DownloadInfo info = new DownloadInfo();
				int id = sc.getInt(sc.getColumnIndex("_id"));
				String pkgName = sc.getString(sc.getColumnIndex("pkgname"));
				String versionCode = sc.getString(sc
						.getColumnIndex("versioncode"));
				String appName = sc.getString(sc.getColumnIndex("title"));
				String iconAddr = sc.getString(sc.getColumnIndex("iconaddr"));
				String installPath = sc.getString(sc.getColumnIndex("_data"));
				String handpause = sc.getString(sc.getColumnIndex("handpause"));
				int status = sc.getInt(sc.getColumnIndex("status"));
				//xujing3 added
				int control = sc.getInt(sc.getColumnIndex(Downloads.Impl.COLUMN_CONTROL));

				String wifistatus = sc.getString(sc
						.getColumnIndex("wifistatus"));
				long currentBytes = sc.getLong(sc
						.getColumnIndex("current_bytes"));
				long totalBytes = sc.getLong(sc.getColumnIndex("total_bytes"));

				int category = sc.getInt(sc.getColumnIndex("category"));

				int progress = 0;
				if (totalBytes != 0 && currentBytes != 0) {
					progress = getProgresss(currentBytes, totalBytes);
				}

				if ((status != Downloads.STATUS_INSTALL)
						&& (status != Downloads.STATUS_SUCCESS)) {
					if (progress == 100){
						status = Downloads.STATUS_SUCCESS;
					}
//					else if (waitWifiStatus(context, String.valueOf(status), wifistatus,
//							handpause)) {
//						wifistatus = LDownloadManager.STATUS_VALUES[1];
//						status = DownloadInfo.DOWNLOAD_WAIT_WIFI;
//					}else if( Downloads.Impl.STATUS_FILE_ERROR == status){
//						//file error
//						status = -2;
//					}
					else{
						status = Helpers.getStatus(status);
					}
//					if( Downloads.Impl.STATUS_FILE_ERROR == status){
//						//file error
//						status = -2;
//					}else{
//						status = Helpers.checkErrorCode(status);
//					}
//					if (progress == 100)
//						status = Downloads.STATUS_SUCCESS;
//					if (waitWifiStatus(context, String.valueOf(status),
//							wifistatus, handpause)) {
//						wifistatus = LDownloadManager.STATUS_VALUES[1];
//						status = DownloadInfo.DOWNLOAD_WAIT_WIFI;
//					}
				}
				info.setId(String.valueOf(id));
				info.setPackageName(pkgName);
				info.setVersionCode(versionCode);
				info.setAppName(appName);
				info.setAppSize(String.valueOf(totalBytes));
				info.setProgress((int) progress);
				info.setIconAddr(iconAddr);
				info.setInstallPath(installPath);
				info.setWifistatus(Integer.parseInt(wifistatus));
				info.setDownloadStatus(status);
				info.setCurrentBytes(currentBytes);
				info.setTotalBytes(totalBytes);
				// zdx modify
				info.setCategory(category);
				//xujing3 added
				info.setControl(control);
				
				infos.add(info);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sc.close();
		}
		return infos;
		
	}

	/**
	 * Get all download items
	 */
	public static synchronized List<DownloadInfo> getCompletedDownloadInfo(Context context) {
		List<DownloadInfo> infos = new ArrayList<DownloadInfo>();
		Cursor sc = context.getContentResolver().query(uri, null,
				Downloads.Impl.COLUMN_STATUS + " = ?",
				new String[] { String.valueOf(Downloads.Impl.STATUS_SUCCESS) },
				null);
		if(sc == null)
			return null;
		try {
			while (sc.moveToNext()) {
				DownloadInfo info = new DownloadInfo();
				int id = sc.getInt(sc.getColumnIndex("_id"));
				String pkgName = sc.getString(sc.getColumnIndex("pkgname"));
				String versionCode = sc.getString(sc
						.getColumnIndex("versioncode"));
				String appName = sc.getString(sc.getColumnIndex("title"));
				String iconAddr = sc.getString(sc.getColumnIndex("iconaddr"));
				String installPath = sc.getString(sc.getColumnIndex("_data"));
				String handpause = sc.getString(sc.getColumnIndex("handpause"));
				int status = sc.getInt(sc.getColumnIndex("status"));
				String wifistatus = sc.getString(sc
						.getColumnIndex("wifistatus"));
				long currentBytes = sc.getLong(sc
						.getColumnIndex("current_bytes"));
				long totalBytes = sc.getLong(sc.getColumnIndex("total_bytes"));
				// zdx modify
				int category = sc.getInt(sc.getColumnIndex("category"));
				//xujing3 added
				int control = sc.getInt(sc.getColumnIndex(Downloads.Impl.COLUMN_CONTROL));

				int progress = 0;
				if (totalBytes != 0 && currentBytes != 0) {
					progress = getProgresss(currentBytes, totalBytes);
				}

				// zdx modify
				if ((status != Downloads.STATUS_INSTALL)
						&& (status != Downloads.STATUS_SUCCESS)) {
					if (progress == 100){
						status = Downloads.STATUS_SUCCESS;
					}
//					else if (waitWifiStatus(context, String.valueOf(status), wifistatus,
//							handpause)) {
//						wifistatus = LDownloadManager.STATUS_VALUES[1];
//						status = DownloadInfo.DOWNLOAD_WAIT_WIFI;
//					}
					else{
						status = Helpers.getStatus(status);
					}
					
//					status = Helpers.checkErrorCode(status);
//					if (progress == 100)
//						status = Downloads.STATUS_SUCCESS;
//					if (waitWifiStatus(context, String.valueOf(status),
//							wifistatus, handpause)) {
//						wifistatus = LDownloadManager.STATUS_VALUES[1];
//						status = DownloadInfo.DOWNLOAD_WAIT_WIFI;
//					}
				}

				info.setId(String.valueOf(id));
				info.setPackageName(pkgName);
				info.setVersionCode(versionCode);
				info.setAppName(appName);
				info.setAppSize(String.valueOf(totalBytes));
				info.setProgress((int) progress);
				info.setIconAddr(iconAddr);
				info.setInstallPath(installPath);
				info.setWifistatus(Integer.parseInt(wifistatus));
				info.setDownloadStatus(status);
				// zdx modify
				info.setCategory(category);
				//xujing3 added
				info.setControl(control);
				
				infos.add(info);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sc.close();
		}
		return infos;
	}

	/**
	 * Information obtained when one downloads
	 */
	public static synchronized DownloadInfo getDownloadInfo(Context context,
			DownloadInfo appInfo) {
		
//		if (null == appInfo)
//			return null;
//		return DownloadInfoContainer.get(context,appInfo.getPackageName(),appInfo.getVersionCode());
		
		
		ContentResolver cr = context.getContentResolver();
		Cursor sc = cr.query(
				uri,
				null,
				"pkgname = ? and versioncode = ?",
				new String[] { appInfo.getPackageName(),
						appInfo.getVersionCode() }, null);
		if(sc == null)
			return null;
		try {
			if (sc.moveToFirst()) {
				DownloadInfo info = new DownloadInfo();
				int id = sc.getInt(sc.getColumnIndex("_id"));
				String pkgName = sc.getString(sc.getColumnIndex("pkgname"));
				String versionCode = sc.getString(sc
						.getColumnIndex("versioncode"));
				String appName = sc.getString(sc.getColumnIndex("title"));
				String iconAddr = sc.getString(sc.getColumnIndex("iconaddr"));
				String installPath = sc.getString(sc.getColumnIndex("_data"));
				String downloadUrl = sc.getString(sc.getColumnIndex("uri"));
//				String handpause = sc.getString(sc.getColumnIndex("handpause"));
				int status = sc.getInt(sc.getColumnIndex("status"));
				int control = sc.getInt(sc.getColumnIndex(Downloads.Impl.COLUMN_CONTROL));

				String wifistatus = sc.getString(sc
						.getColumnIndex("wifistatus"));
				long currentBytes = sc.getLong(sc
						.getColumnIndex("current_bytes"));
				long totalBytes = sc.getLong(sc.getColumnIndex("total_bytes"));
				int category = sc.getInt(sc.getColumnIndex("category"));

				int progress = 0;
				if (totalBytes != 0 && currentBytes != 0) {
					progress = getProgresss(currentBytes, totalBytes);
				}

				if ((status != Downloads.STATUS_INSTALL)
						&& (status != Downloads.STATUS_SUCCESS)
						&& (status != Downloads.STATUS_UNKNOWN_ERROR)) {
					if (progress == 100){
						status = Downloads.STATUS_SUCCESS;
					}else{
						status = Helpers.getStatus(status);
					}
				}
				
				info.setId(String.valueOf(id));
				info.setPackageName(pkgName);
				info.setVersionCode(versionCode);
				info.setAppName(appName);
				info.setAppSize(String.valueOf(totalBytes));
				info.setCurrentBytes(currentBytes);
				info.setTotalBytes(totalBytes);
				info.setProgress(progress);
				info.setIconAddr(iconAddr);
				info.setInstallPath(installPath);
				info.setWifistatus(Integer.parseInt(wifistatus));
				info.setDownloadStatus(status);
				info.setDownloadUrl(downloadUrl);
				info.setCategory(category);
				info.setControl(control);
				
				return info;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sc.close();
		}
		return null;
		
	}

	public static synchronized int getProgresss(long currentBytes, long totalBytes) {
		int i = (int) (currentBytes / (float) totalBytes * 100);
		return i;
	}

	public static boolean isValidItem(DownloadInfo appInfo) {
		if (null == appInfo) {
			return false;
		}
		String name = appInfo.getPackageName();
		String code = appInfo.getVersionCode();
		if (null == name || "".equals(name) || null == code || "".equals(code)) {
			return false;
		}
		return true;
	}

	private static NotificationManager mNotificationMgr;
	public static Set<String> mId = Collections
			.synchronizedSet(new TreeSet<String>());
	/**
	 * delete message notification
	 */
	public static synchronized void notifyDelete(Context context, DownloadInfo di) {
		if (null == di)
			return;
		Log.d(TAG,
				"*******DownloadHelpers.notifyDelete !!! cancel notification, id:"
						+ di.getId());
		if (null == mNotificationMgr) {
			mNotificationMgr = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
		}
		mNotificationMgr.cancel(Integer.parseInt(di.getId()));
	}
	
	
	/**
	 * delete message notification
	 */
	public static synchronized void notifyDeleteId(Context context,String id){
		Log.d(TAG,"Notify Delete !!!");
		if(null == mNotificationMgr){
			mNotificationMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		}
		mNotificationMgr.cancel(Integer.parseInt(id));
	}
	
	
	
	

	/**
	 * Pause message notification
	 */
	public static synchronized void notifyPause(Context context, DownloadInfo di) {
		if (null == di)
			return;
		Log.d(TAG,
				"********DownloadHelpers.notifyPause, name:" + di.getAppName());
		if (null == mNotificationMgr) {
			mNotificationMgr = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
		}
		Notification n = new Notification(
				R.drawable.magicdownload_download_stop, context.getString(
						R.string.downloadpause_app_title, di.getAppName()),
				System.currentTimeMillis());
		Intent notificationIntent = new Intent(
				LDownloadManager.ACTION_DOWNLOAD_LIST);
		Bundle b = new Bundle();
		b.putInt(LDownloadManager.RECEIVER_ACTION_TAG, DownloadInfo.CONTINUE);
		b.putParcelable(LDownloadManager.RECEIVER_DATA_TAG, di);
		notificationIntent.putExtras(b);
		PendingIntent contentIntent = PendingIntent.getBroadcast(context,
				Integer.parseInt(di.getId()), notificationIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		RemoteViews rw = new RemoteViews(context.getPackageName(),
				R.layout.magicdownload_download_notification_item);
		// rw.setViewVisibility(R.id.end_text, View.VISIBLE);
		rw.setTextViewText(R.id.notification_download_appname, di.getAppName()
				.toString());
		rw.setTextViewText(R.id.notification_downloading,
				context.getString(R.string.downloadpause_app));
		rw.setProgressBar(R.id.notificationProgress, 100, di.getProgress(),
				false);
		rw.setTextViewText(R.id.notification_tx_progress, di.getProgress()
				+ "%");
		// rw.setTextViewText(R.id.end_text,
		// context.getText(R.string.click_start_download));
		// rw.setTextViewText(R.id.end_text,
		// context.getText(R.string.downloadpause_app));
		n.flags = Notification.FLAG_AUTO_CANCEL;
		n.contentView = rw;
		n.contentIntent = contentIntent;
		mId.add(di.getId());
		mNotificationMgr.notify(Integer.parseInt(di.getId()), n);

	}
	
	

	public static void notifyCompleted(Context context,DownloadInfo di) {
		int status = di.getDownloadStatus();
		if (DownloadInfo.DOWNLOAD_ERROR_SDCARD == status
				|| DownloadInfo.DOWNLOAD_ERROR_HTTP == status){
			DownloadHelpers.doAction(context, di, DownloadInfo.DELETE);
		}
		
//		DownloadHelpers.notifyDownloadCountChanged(context, DownloadInfoContainer.getDownloadSize(context));
		DownloadHelpers.updateDownloadCompletedNotifacition(context,getCompletedDownloadInfo(context).size());
	}
	/**
	 * Notice to complete the task
	 */
	public static synchronized void notifyCompletedEx(Context context, DownloadInfo di) {
		if (null == di)
			return;
		if (null == mNotificationMgr) {
			mNotificationMgr = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
		}
		Notification n = null;
		Intent notificationIntent = new Intent(
				LDownloadManager.ACTION_NOTIFICATION_CLICKED);
		PendingIntent contentIntent = null;
		RemoteViews rw = new RemoteViews(context.getPackageName(),
				R.layout.magicdownload_download_notification_item);
		int status = di.getDownloadStatus();

		if (status == 200) {
			Log.d(TAG,
					"**********DownloadHelpers.notifyCompleted, download success!");
			Bundle b = new Bundle();
			b.putString(LDownloadManager.NOTIFY_CLICK_TAG,
					LDownloadManager.STATUS_VALUES[0]);
			b.putParcelable(LDownloadManager.RECEIVER_DATA_TAG, di);
			notificationIntent.putExtras(b);
			contentIntent = PendingIntent.getBroadcast(context,
					Integer.parseInt(di.getId()), notificationIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);

			n = new Notification(R.drawable.magicdownload_download_succuss,
					context.getString(
							R.string.downloadcompleted_succuss_app_title,
							di.getAppName()), System.currentTimeMillis());
			rw.setViewVisibility(R.id.end_text, View.VISIBLE);
			rw.setViewVisibility(R.id.end_text2, View.VISIBLE);
			rw.setViewVisibility(R.id.pb_ll, View.GONE);
			rw.setTextViewText(R.id.notification_download_appname, di
					.getAppName().toString());
			rw.setTextViewText(R.id.notification_downloading,
					context.getString(R.string.download_succuss));
			rw.setTextViewText(R.id.end_text,
					context.getText(R.string.click_start_install));
			rw.setTextViewText(R.id.end_text2, getCurrentTime());

		} else if (DownloadInfo.DOWNLOAD_ERROR_HTTP == status) {
			Log.d(TAG,
					"**********DownloadHelpers.notifyCompleted, download http error!");
			Bundle b = new Bundle();
			b.putString(LDownloadManager.NOTIFY_CLICK_TAG,
					LDownloadManager.STATUS_VALUES[0]);
			b.putParcelable(LDownloadManager.RECEIVER_DATA_TAG, di);
			notificationIntent.putExtras(b);
			contentIntent = PendingIntent.getBroadcast(context,
					Integer.parseInt(di.getId()), notificationIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);

			n = new Notification(R.drawable.magicdownload_download_error,
					context.getString(
							R.string.downloadcompleted_error_app_title,
							di.getAppName()), System.currentTimeMillis());
			rw.setViewVisibility(R.id.end_text, View.VISIBLE);
			rw.setViewVisibility(R.id.end_text2, View.VISIBLE);
			rw.setViewVisibility(R.id.pb_ll, View.GONE);
			rw.setTextViewText(R.id.notification_download_appname, di
					.getAppName().toString());
			rw.setTextViewText(R.id.notification_downloading,
					context.getString(R.string.download_error));
			rw.setTextViewText(R.id.end_text,
					context.getText(R.string.click_start_view));
			rw.setTextViewText(R.id.end_text2, getCurrentTime());

			DownloadHelpers.doAction(context, di, DownloadInfo.DELETE);
		} else if (DownloadInfo.DOWNLOAD_ERROR_SDCARD == status) {
			Log.d(TAG,
					"**********DownloadHelpers.notifyCompleted, sdcard error !");
			Bundle b = new Bundle();
			b.putString(LDownloadManager.NOTIFY_CLICK_TAG,
					LDownloadManager.STATUS_VALUES[0]);
			b.putParcelable(LDownloadManager.RECEIVER_DATA_TAG, di);
			notificationIntent.putExtras(b);
			contentIntent = PendingIntent.getBroadcast(context,
					Integer.parseInt(di.getId()), notificationIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);

			n = new Notification(R.drawable.magicdownload_download_error,
					context.getString(
							R.string.downloadcompleted_error_app_title,
							di.getAppName()), System.currentTimeMillis());
			rw.setViewVisibility(R.id.end_text, View.VISIBLE);
			rw.setViewVisibility(R.id.end_text2, View.VISIBLE);
			rw.setViewVisibility(R.id.pb_ll, View.GONE);
			rw.setTextViewText(R.id.notification_download_appname, di
					.getAppName().toString());
			rw.setTextViewText(R.id.notification_downloading,
					context.getString(R.string.download_sd_error));
			rw.setTextViewText(R.id.end_text,
					context.getText(R.string.click_start_view));
			rw.setTextViewText(R.id.end_text2, getCurrentTime());

			DownloadHelpers.doAction(context, di, DownloadInfo.DELETE);
		}

		if (null == n) {
			return;
		}

		n.flags = Notification.FLAG_AUTO_CANCEL;
		n.contentView = rw;
		n.contentIntent = contentIntent;
//		mNotificationMgr.notify(Integer.parseInt(di.getId()), n);
	}

	public static String getCurrentTime() {
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		return df.format(new Date()).toString();
	}

	static final int RUNNING = 1;
	static final int PAUSE = 2;

	private static Cursor getRunandPause(Context context, int where) {
		Cursor c = null;
		if (RUNNING == where) {
			c = context.getContentResolver().query(
					Downloads.Impl.CONTENT_URI,
					null,
					Downloads.Impl.COLUMN_CONTROL + " = ? and "
							+ Downloads.Impl.COLUMN_STATUS + " = ?",
					new String[] { String.valueOf(Downloads.CONTROL_RUN),
							String.valueOf(Downloads.STATUS_RUNNING) }, null);
		} else if (PAUSE == where) {
			c = context.getContentResolver().query(
					Downloads.Impl.CONTENT_URI,
					null,
					Downloads.Impl.COLUMN_CONTROL + " = ? or "
							+ Downloads.Impl.COLUMN_STATUS + " = ?",
					new String[] { String.valueOf(Downloads.CONTROL_PAUSED),
							String.valueOf(Downloads.STATUS_RUNNING_PAUSED) },
					null);
		}
		return c;
	}

	private static SharedPreferences downloadsp;
	/**
	 * Affected by DownloadReceiver
	 */
	public static int action;
	public static Boolean mWifiStatus = null;

	/**
	 * Check the download number is not currently being configured
	 */
	public static synchronized void checkAction(Context context) {
		ContentResolver cr = context.getContentResolver();
		if (downloadsp == null) {
			downloadsp = context.getSharedPreferences("download",
					Context.MODE_WORLD_WRITEABLE + Context.MODE_WORLD_READABLE
							+ Context.MODE_APPEND);
			action = downloadsp.getInt("downloadmax", 1);

		}
		if (mWifiStatus == null) {
			mWifiStatus = downloadsp.getBoolean("wifi", false);
		}
		Cursor rc = getRunandPause(context, RUNNING);
		if(rc == null)
			return ;
		int t = rc.getCount();
		if (action > t) {
			Cursor pc = getRunandPause(context, PAUSE);
			while (pc.moveToNext()) {
				String id = pc.getString(pc.getColumnIndex("_id"));
				int status = pc.getInt(pc.getColumnIndex("status"));
				String wifistatus = pc.getString(pc
						.getColumnIndex("wifistatus"));
				String handpause = pc.getString(pc.getColumnIndex("handpause"));
				boolean isWifi = DownloadHelpers.waitWifiStatus(context,
						String.valueOf(status), wifistatus, handpause);
				if (!isWifi
						&& LDownloadManager.STATUS_VALUES[0].equals(handpause)
						&& !mWifiStatus/**/) {
					ContentValues values = new ContentValues();
					values.put(Downloads.Impl.COLUMN_CONTROL,
							Downloads.CONTROL_RUN);
					cr.update(uri, values, "_ID = ?", new String[] { id });
//					DownloadInfoContainer.updateDBAndBufferById(context, uri, values, id);
					break;
				}
			}
			pc.close();
		} else if (action < t) {
			if (rc.moveToLast()) {
				String id = rc.getString(rc.getColumnIndex("_id"));
				ContentValues values = new ContentValues();
				values.put(Downloads.Impl.COLUMN_CONTROL,
						Downloads.CONTROL_PAUSED);
				values.put(Downloads.Impl.COLUMN_HANDTOPAUSE,
						LDownloadManager.STATUS_VALUES[0]);
				cr.update(uri, values, "_ID = ?", new String[] { id });
//				DownloadInfoContainer.updateDBAndBufferById(context, uri, values, id);
			}
		}
		rc.close();
	}

	public static synchronized DownloadInfo queryDownloadInfo(Context context,
			String packageName, String versionCode) {
		// Log.i(TAG,">>>>>> DownloadHelpers.queryDownloadInfo(), package:"+
		// packageName);
		// zdx modify
		// String whereStr = Downloads.Impl.COLUMN_PKGNAME + " LIKE '%" +
		// packageName + "%' AND " +
		// Downloads.Impl.COLUMN_VERSIONCODE + " LIKE '%" + versionCode + "%'";
		Cursor sc = null;
		DownloadInfo info = null;
		try {
			/*
			 * sc = context.getContentResolver().query(uri, new
			 * String[]{Downloads.Impl._ID, Downloads.Impl.COLUMN_PKGNAME,
			 * Downloads.Impl.COLUMN_VERSIONCODE,
			 * Downloads.Impl.COLUMN_HANDTOPAUSE, Downloads.Impl.COLUMN_STATUS,
			 * Downloads.Impl.COLUMN_WIFISTATUS,
			 * Downloads.Impl.COLUMN_CURRENT_BYTES,
			 * Downloads.Impl.COLUMN_TOTAL_BYTES,
			 * Downloads.Impl.COLUMN_CATEGORY}, whereStr, null, null);
			 */
			sc = context.getContentResolver().query(
					uri,
					new String[] { Downloads.Impl._ID,
							Downloads.Impl.COLUMN_PKGNAME,
							Downloads.Impl.COLUMN_VERSIONCODE,
							Downloads.Impl.COLUMN_HANDTOPAUSE,
							Downloads.Impl.COLUMN_STATUS,
							Downloads.Impl.COLUMN_WIFISTATUS,
							Downloads.Impl.COLUMN_CURRENT_BYTES,
							Downloads.Impl.COLUMN_TOTAL_BYTES,
							Downloads.Impl.COLUMN_CATEGORY,
							Downloads.Impl.COLUMN_CONTROL},
					Downloads.Impl.COLUMN_PKGNAME + " = ? and "
							+ Downloads.Impl.COLUMN_VERSIONCODE + " = ? ",
					new String[] { packageName, versionCode }, null);
			 Log.i(TAG,"--------111--------- DownloadHelpers.queryDownloadInfo(), package:"+ packageName);
			if (sc != null) {
				if (sc.moveToFirst()) {
					info = new DownloadInfo();
					
					
					int id = sc.getInt(sc.getColumnIndex(Downloads.Impl._ID));
					
					 Log.i(TAG,"-------222-------- DownloadHelpers.queryDownloadInfo(), package:"+ packageName);

					
					String pkgName = sc.getString(sc
							.getColumnIndex(Downloads.Impl.COLUMN_PKGNAME));
					String vcode = sc.getString(sc
							.getColumnIndex(Downloads.Impl.COLUMN_VERSIONCODE));
					String handpause = sc.getString(sc
							.getColumnIndex(Downloads.Impl.COLUMN_HANDTOPAUSE));
					int status = sc.getInt(sc
							.getColumnIndex(Downloads.Impl.COLUMN_STATUS));
					String wifistatus = sc.getString(sc
							.getColumnIndex(Downloads.Impl.COLUMN_WIFISTATUS));
					long currentBytes = sc
							.getLong(sc
									.getColumnIndex(Downloads.Impl.COLUMN_CURRENT_BYTES));
					long totalBytes = sc.getLong(sc
							.getColumnIndex(Downloads.Impl.COLUMN_TOTAL_BYTES));
					int category = sc.getInt(sc
							.getColumnIndex(Downloads.Impl.COLUMN_CATEGORY));
					//xujing3 added
					int control = sc.getInt(sc.getColumnIndex(Downloads.Impl.COLUMN_CONTROL));
					// String appName =
					// sc.getString(sc.getColumnIndex(Downloads.Impl.COLUMN_TITLE));
					// String iconAddr = sc.getString(sc.getColumnIndex(
					// Downloads.Impl.COLUMN_ICONADDR));
					// String installPath =
					// sc.getString(sc.getColumnIndex(Downloads.Impl._DATA));
					// String downloadUrl =
					// sc.getString(sc.getColumnIndex(Downloads.Impl.COLUMN_URI));

					int progress = 0;
					if (totalBytes != 0 && currentBytes != 0) {
						progress = DownloadHelpers.getProgresss(currentBytes,
								totalBytes);
					}

					if ((status != Downloads.STATUS_INSTALL)
							&& (status != Downloads.STATUS_SUCCESS)) {
						if (progress == 100){
							status = Downloads.STATUS_SUCCESS;
						}else if (waitWifiStatus(context, String.valueOf(status), wifistatus,
								handpause)) {
							wifistatus = LDownloadManager.STATUS_VALUES[1];
							status = DownloadInfo.DOWNLOAD_WAIT_WIFI;
						}else{
							status = Helpers.getStatus(status);
						}
//						status = Helpers.checkErrorCode(status);
//						if (progress == 100)
//							status = Downloads.STATUS_SUCCESS;
//						if (waitWifiStatus(context, String.valueOf(status),
//								wifistatus, handpause)) {
//							wifistatus = LDownloadManager.STATUS_VALUES[1];
//							status = DownloadInfo.DOWNLOAD_WAIT_WIFI;
//						}
					}

					info.setId(String.valueOf(id));
					info.setPackageName(pkgName);
					info.setVersionCode(vcode);
					info.setAppSize(String.valueOf(totalBytes));
					info.setCurrentBytes(currentBytes);
					info.setTotalBytes(totalBytes);
					info.setProgress(progress);
					info.setWifistatus(Integer.parseInt(wifistatus));
					info.setDownloadStatus(status);
					// zdx modify
					info.setCategory(category);
					//xujing3 added
					info.setControl(control);

					// info.setAppName(appName);
					// info.setIconAddr(iconAddr);
					// info.setInstallPath(installPath);
					// info.setDownloadUrl(downloadUrl);

				}
			}
		} catch (Exception e) {
			Log.i(TAG,
					"DownloadHelpers.queryDownloadInfo, can not query downloadinfo !");
			e.printStackTrace();
		} finally {
			if (sc != null && !sc.isClosed()) {
				sc.close();
			}
		}
		return info;
	}
	
	//xujing3 added
	public static synchronized DownloadInfo updateInstallStatus(Context context, String packageName){
		Log.i(TAG, ">>>>>> DownloadHelpers.updateInstallStatus(), package:"
				+ packageName);
		ContentResolver cr = context.getContentResolver();
		Cursor sc = context.getContentResolver().query(
				uri,
				null,
				Downloads.Impl.COLUMN_PKGNAME + " = ? and "
						+ Downloads.Impl.COLUMN_STATUS + " = ?",
				new String[] { packageName, String.valueOf(Downloads.Impl.STATUS_SUCCESS) }, null);
		DownloadInfo info = new DownloadInfo();
		try{
			while (sc.moveToNext()) {
				String id = sc.getString(sc.getColumnIndex("_id"));
				int category = sc.getInt(sc.getColumnIndex("category"));
				if(DownloadConstant.getDownloadCategory(category)!=DownloadConstant.CATEGORY_RECOMMEND_APP){
					ContentValues values = new ContentValues();
					values.put(Downloads.Impl.COLUMN_STATUS,
							Downloads.STATUS_INSTALL);

//					DownloadInfoContainer.updateDBAndBufferById(context, uri, values, id);
//					info = DownloadInfoContainer.get(context, Long.valueOf(id));
					
					String pkgName = sc.getString(sc.getColumnIndex("pkgname"));
					String versionCode = sc.getString(sc
							.getColumnIndex("versioncode"));
					String appName = sc.getString(sc.getColumnIndex("title"));
					String iconAddr = sc.getString(sc.getColumnIndex("iconaddr"));
					String installPath = sc.getString(sc.getColumnIndex("_data"));
					String downloadUrl = sc.getString(sc.getColumnIndex("uri"));
					String handpause = sc.getString(sc.getColumnIndex("handpause"));
					int status = Downloads.STATUS_INSTALL;
					int control = sc.getInt(sc.getColumnIndex(Downloads.Impl.COLUMN_CONTROL));

					String wifistatus = sc.getString(sc
							.getColumnIndex("wifistatus"));
					long currentBytes = sc.getLong(sc
							.getColumnIndex("current_bytes"));
					long totalBytes = sc.getLong(sc.getColumnIndex("total_bytes"));

					int progress = 0;
					if (totalBytes != 0 && currentBytes != 0) {
						progress = getProgresss(currentBytes, totalBytes);
					}

					if ((status != Downloads.STATUS_INSTALL)
							&& (status != Downloads.STATUS_SUCCESS)
							&& (status != Downloads.STATUS_UNKNOWN_ERROR)) {
						if (progress == 100){
							status = Downloads.STATUS_SUCCESS;
						}else{
							status = Helpers.getStatus(status);
						}
					}
					
					info.setId(String.valueOf(id));
					info.setPackageName(pkgName);
					info.setVersionCode(versionCode);
					info.setAppName(appName);
					info.setAppSize(String.valueOf(totalBytes));
					info.setCurrentBytes(currentBytes);
					info.setTotalBytes(totalBytes);
					info.setProgress(progress);
					info.setIconAddr(iconAddr);
					info.setInstallPath(installPath);
					info.setWifistatus(Integer.parseInt(wifistatus));
					info.setDownloadStatus(status);
					info.setDownloadUrl(downloadUrl);
					info.setCategory(category);
					info.setControl(control);
					
					cr.update(uri, values, "_ID = ?", new String[] { id });
					Log.d(TAG,"updateInstallStatus download:" + info);
				}
			}
		}catch (Exception e) {
				e.printStackTrace();
		} finally {
				sc.close();
		}
		return info;
	}
	
//	public static void updateInstallErrorStatus(Context context, String packageName){
//		Log.i(TAG, ">>>>>> DownloadHelpers.updateInstallErrorStatus(), package:"
//				+ packageName);
//		if(null == packageName)
//			return;
////		ContentResolver cr = context.getContentResolver();
//		Cursor sc = context.getContentResolver().query(
//				uri,
//				null,
//				Downloads.Impl.COLUMN_PKGNAME + " = ? and "
//						+ Downloads.Impl.COLUMN_STATUS + " = ?",
//				new String[] { packageName, String.valueOf(Downloads.Impl.STATUS_SUCCESS) }, null);
//		try{
//			while (sc.moveToNext()) {
//				String id = sc.getString(sc.getColumnIndex("_id"));
//				int category = sc.getInt(sc.getColumnIndex("category"));
//				if(category!=DownloadConstant.CATEGORY_RECOMMEND_APP){
//					ContentValues values = new ContentValues();
//					values.put(Downloads.Impl.COLUMN_STATUS,
//							Downloads.STATUS_UNKNOWN_ERROR);
////					cr.update(uri, values, "_ID = ?", new String[] { id });
//					DownloadInfoContainer.updateDBAndBufferById(context, uri, values, id);
//				}
//			}
//		}catch (Exception e) {
//				e.printStackTrace();
//		} finally {
//				sc.close();
//		}
//	}

	// zdx modify
	public static synchronized void deleteDownloadInfo(Context context, String packageName,
			String versionCode) {
		Log.i(TAG, ">>>>>> DownloadHelpers.deleteDownloadInfo(), package:"
				+ packageName);
		// String whereStr = Downloads.Impl.COLUMN_PKGNAME + " LIKE '%" +
		// packageName + "%' AND " +
		// Downloads.Impl.COLUMN_VERSIONCODE + " LIKE '%" + versionCode + "%'";

		ContentResolver cr = context.getContentResolver();
		// Cursor sc = context.getContentResolver().query(uri, new
		// String[]{Downloads.Impl._DATA}, whereStr,null, null);
		Cursor sc = context.getContentResolver().query(
				uri,
				new String[] { Downloads.Impl._DATA },
				Downloads.Impl.COLUMN_PKGNAME + " = ? and "
						+ Downloads.Impl.COLUMN_VERSIONCODE + " = ? ",
				new String[] { packageName, versionCode }, null);
		while (sc.moveToNext()) {
			final String installPath = sc.getString(sc
					.getColumnIndex(Downloads.Impl._DATA));
			File f = new File(installPath);
			if (f.exists()) {
				f.delete();
				String dir = installPath.substring(0,
						installPath.lastIndexOf(File.separator));
				String filename = installPath.substring(installPath
						.lastIndexOf(File.separator) + 1);
				File lca = new File(dir + File.separator + "lca"
						+ filename.replace(".lca", ".apk"));
				if (lca.exists()) {
					lca.delete();
				}
			}
		}
		try {
			cr.delete(uri, Downloads.Impl.COLUMN_PKGNAME + " = ? and "
					+ Downloads.Impl.COLUMN_VERSIONCODE + " = ? ",
					new String[] { packageName, packageName });
//			DownloadInfoContainer.deleteDBAndBuffer(context, packageName, packageName);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sc.close();
		}
	}

	// zdx modify
	public static synchronized void setDownloadInfoInstall(DownloadInfo appInfo,
			Context context, boolean bInstalled) {
		if (null == appInfo)
			return;
		if (null == appInfo.getPackageName())
			return;
		String preStatus = null;
		String updateStatus = null;
		if (bInstalled) {
			preStatus = String.valueOf(Downloads.STATUS_SUCCESS);
			updateStatus = String.valueOf(Downloads.STATUS_INSTALL);
		} else {
			preStatus = String.valueOf(Downloads.STATUS_INSTALL);
			updateStatus = String.valueOf(Downloads.STATUS_SUCCESS);
		}
		ContentResolver cr = context.getContentResolver();
		Log.i("zdx", "DownloadHelpers.setDownloadInfoInstall(), installed:"
				+ bInstalled + "pkg:" + appInfo.getPackageName());
		Cursor sc = context.getContentResolver().query(uri, null,
				"pkgname = ? and status = ?",
				new String[] { appInfo.getPackageName(), preStatus }, null);
		if(sc == null)
			return;
		try {
			while (sc.moveToNext()) {
				String id = sc.getString(sc.getColumnIndex(Downloads.Impl._ID));
				String versionCode = sc.getString(sc
						.getColumnIndex(Downloads.Impl.COLUMN_VERSIONCODE));
				ContentValues values = new ContentValues();
				values.put(Downloads.Impl.COLUMN_STATUS, updateStatus);
				Uri urid = ContentUris.withAppendedId(
						Downloads.Impl.CONTENT_URI, Integer.valueOf(id));
				cr.update(urid, values, "_ID = ?", new String[] { id });
//				DownloadInfoContainer.updateDBAndBufferById(context, uri, values, id);
				notifyInstalledOrUninstalled(context, appInfo.getPackageName(),
						versionCode, bInstalled);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sc.close();
		}
	}

	public static void notifyInstalledOrUninstalled(Context context,
			String packageName, String versionCode, boolean bInstalled) {
		Intent intent = new Intent(
				DownloadConstant.ACTION_DOWNLOAD_INSTALL_UNINSTALL);
		intent.putExtra(DownloadConstant.EXTRA_PACKAGENAME, packageName);
		intent.putExtra(DownloadConstant.EXTRA_VERSION, versionCode);
		intent.putExtra(DownloadConstant.EXTRA_RESULT, bInstalled);
		context.sendBroadcast(intent);
	}

	public static synchronized void notifyInstalled(Context context, String packageName,
			String appName, String id) {
		if (null == mNotificationMgr) {
			mNotificationMgr = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
		}

//		Notification n = null;
		Intent notificationIntent = new Intent(
				LDownloadManager.ACTION_NOTIFICATION_INSTALLED);
//		PendingIntent contentIntent = null;
		RemoteViews rw = new RemoteViews(context.getPackageName(),
				R.layout.magicdownload_download_notification_item);
		Log.d(TAG, "**********DownloadHelpers.notifyInstalled*******");
		Bundle b = new Bundle();
		b.putString("package", packageName);
		b.putString("app_name", appName);
		notificationIntent.putExtras(b);
//		contentIntent = PendingIntent.getBroadcast(context,
//				Integer.parseInt(id), notificationIntent,
//				PendingIntent.FLAG_UPDATE_CURRENT);

//		n = new Notification(
//				R.drawable.magicdownload_download_succuss,
//				context.getString(R.string.install_notification_title, appName),
//				System.currentTimeMillis());
		rw.setViewVisibility(R.id.end_text, View.VISIBLE);
		rw.setViewVisibility(R.id.end_text2, View.VISIBLE);
		rw.setViewVisibility(R.id.pb_ll, View.GONE);
		rw.setTextViewText(R.id.notification_download_appname, appName);
		rw.setTextViewText(R.id.notification_downloading,
				context.getString(R.string.install_complete));
		rw.setTextViewText(R.id.end_text, context.getText(R.string.install_run));
		rw.setTextViewText(R.id.end_text2, getCurrentTime());
	}
}
