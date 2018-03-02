package com.lenovo.lejingpin.share.download;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * 
 * @author philn
 * 
 */
public abstract class LDownloadManager {

	private static LDownloadManager mDownloadManager = null;

	public static synchronized LDownloadManager getDefaultInstance(
			Context context) {
		if (mDownloadManager == null) {
			mDownloadManager = new LDownloadManagerImpl(context);
		}
		return mDownloadManager;
	}

	/**
	 * Download the data receiver module mark
	 */
	public static final String RECEIVER_DATA_TAG = "data";

	/**
	 * control download action tag
	 */
	public static final String RECEIVER_ACTION_TAG = "action";

	/**
	 * Notification clicked tag
	 */
	public static final String NOTIFY_CLICK_TAG = "click";

	/**
	 * Add download task to receive broadcast action
	 */
	public static final String DOWNLOAD_START_ACTION = "com.lenovo.lejingpin.share.download.START";

	/**
	 * The third application can control the download action
	 */
	public static final String ACTION_DOWNLOAD_LIST = "com.lenovo.lejingpin.share.download.ACTION";

	/**
	 * Download the module configuration action
	 */
	public static final String ACTION_DOWNLOAD_CONFIG = "com.lenovo.lejingpin.share.download.CONFIG_ACTION";

	/**
	 * Broadcast Action: this is sent by the download manager to the app that
	 * had initiated a download when that download completes.
	 */
	public static final String ACTION_DOWNLOAD_COMPLETED = "com.lenovo.lejingpin.share.download.DOWNLOAD_COMPLETED";
	
	public static final String ACTION_DOWNLOAD_COUNT_CHANGED = "com.lenovo.lejingpin.share.download.DOWNLOAD_CHANGED";

	/**
	 * Broadcast Action: this is sent by the download manager to the app that
	 * had initiated a download when the user selects the notification
	 */
	public static final String ACTION_NOTIFICATION_CLICKED = "com.lenovo.lejingpin.share.download.DOWNLOAD_NOTIFICATION_CLICKED";
	public static final String ACTION_NOTIFICATION_INSTALLED = "com.lenovo.lejingpin.share.download.ACTION_NOTIFICATION_INSTALLED";

	public static final String ACTION_PUSH_APP = "rapp001";

	/**
	 * Push app action
	 */
	public static final String ACTION_DOWNLOAD_PKVC = "com.lenovo.lejingpin.share.download.PACKAGE_VERSION";

	/**
	 * wifistatus 0:download now 1:waiting for wifi handpause 0:switch(etc.)
	 * 1:manual pause network 0:3g network 1:wifi network notifyclick
	 * 0:completed 1:run
	 */
	public static final String[] STATUS_VALUES = { "0", "1" };
	
	/**
	 * Add download tasks
	 */
	public abstract String addTask(DownloadInfo info);

	/**
	 * Start download items
	 */
	public abstract boolean startTask(DownloadInfo info);

	/**
	 * Paused downloading
	 */
	public abstract boolean pauseTask(DownloadInfo info);

	/**
	 * Continue to download items
	 */
	public abstract boolean resumeTask(DownloadInfo info);

	/**
	 * Delete download items
	 */
	public abstract boolean deleteTask(DownloadInfo info);
	
	/**
	 * redownload items
	 */
	public abstract boolean reDownloadTask(DownloadInfo info);

	/**
	 * Remove all download information
	 */
	public abstract void deleteAllTask();

	/**
	 * Whether to support the wifi network downloads flag true:Only wifi to
	 * download false:A network can download
	 */
	public abstract void setNetworkWifi(boolean flag);

	/**
	 * Whether to support the wifi download
	 */
	public abstract boolean getWifiStatus();

	/**
	 * Set the maximum number of downloads
	 */
	public abstract void configDownloadMax(int max);

	/**
	 * Maximum downloads
	 */
	public abstract int getConfigDownloads();

	/**
	 * Get all download items
	 */
	public abstract List<DownloadInfo> getAllDownloadInfo();

	public abstract List<DownloadInfo> getCompletedDownloadInfo();
	
	public abstract int getAllDownloadCount();

	/**
	 * Get a single download items
	 */
	public abstract DownloadInfo getDownloadInfo(DownloadInfo info);

	/**
	 * Reverse listener interfaces up
	 */
	public abstract void setDownloadListener(DownloadInfo info,
			IDownloadListener idl);

	/**
	 * If there is a record immediately notify
	 */
	public abstract void setDownloadListener(Context context,
			DownloadInfo info, IDownloadListener idl);

	/**
	 * Cancel of an item listener
	 */
	public abstract void cancelDownloadListener(DownloadInfo info,
			IDownloadListener idl);

	/**
	 * Cancel all listeners
	 */
	public abstract void cancelDownloadListener();

	/**
	 * Download the list of monitoring
	 */
	public abstract void registerDownloadObserver(Observer obs);

	/**
	 * Cancel monitor download list
	 */
	public abstract void removeDownloadObserver(Observer obs);

	/**
	 * Download the list data for monitoring
	 */

	public abstract Watched getWatcher();

	/**
	 * Not in use onDestroy method LDownloadManager object manual calls
	 */
	public abstract void onDestroy();

	// zdx modify
	public abstract void setDownloadInfoInstall(DownloadInfo appInfo,
			Context context, boolean bInstalled);

	private static void setmDownloadManager(LDownloadManager mDownloadManager) {
		LDownloadManager.mDownloadManager = mDownloadManager;
	}

	private static class LDownloadManagerImpl extends LDownloadManager {

		private static final String TAG = "xujing3";
		private Context mContext;
//		public IDownloadService mLDownloadService;
		private DownloadSubject ds;
//		private LDownloadConnection mLDownloadConnection = new LDownloadConnection();
//		private ContentObserver mObserver = new DownloadObserver();
		private Watched watched = new Watched();
		private static final String CONFIGNAME = "download";
		private static SharedPreferences downloadsp = null;
		
		public LDownloadManagerImpl(Context context) {
			this.mContext = context;
//			context.getApplicationContext().bindService(
//					new Intent(
//							"com.lenovo.lejingpin.share.download.IDownloadService"),
//					this.mLDownloadConnection, Context.BIND_AUTO_CREATE);
			
			ds = DownloadSubject.getInstance();
			downloadsp = context.getSharedPreferences(CONFIGNAME,
					Context.MODE_WORLD_WRITEABLE + Context.MODE_WORLD_READABLE
							+ Context.MODE_APPEND);
			// xujing3 remove
			// context.getContentResolver().registerContentObserver(Downloads.CONTENT_URI,
			// true,mObserver);
			
			Log.d(TAG,"LDownloadManagerImpl");
			
			synchronized(this){
				try {
					this.wait(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		public synchronized String preDownload(DownloadInfo appInfo) {
			// Log.i(TAG,"IService.preDownload, category:"+
			// appInfo.getCategory());
			if (null == appInfo.getDownloadUrl()
					|| "".equals(appInfo.getDownloadUrl().trim())) {
				Log.i(TAG, "Invalid download address");
				return null;
			}
			if (DownloadHelpers.itemExistence(mContext, appInfo)) {
				Log.i(TAG, "download item has in the list");
				return null;
			}
			return DownloadHelpers.preDownload(mContext, appInfo);

		}

		@Override
		public synchronized String addTask(DownloadInfo appInfo) {
			Log.i(TAG,
					"DownloadManager addTask, appInfo iconurl:" + appInfo.getIconAddr());
//			try {
//				if (null != mLDownloadService) {
//					return mLDownloadService.addTask(appInfo);
//				}
//				Log.i(TAG,"DownloadManager addTask failed");
//				return null;
//			} catch (RemoteException e) {
//				e.printStackTrace();
//				return null;
//			}
			if (DownloadHelpers.isValidItem(appInfo)) {
				return preDownload(appInfo);
			}
			 Log.d(TAG, "addTask error");
			return null;

		}

		public synchronized boolean startTask(DownloadInfo info) {
			Log.d(TAG,"DownloadManager startTask ");
//			try {
//				if (null != mLDownloadService) {
//					return mLDownloadService.startTask(info);
//				}
//				return false;
//			} catch (RemoteException e) {
//				e.printStackTrace();
//				return false;
//			}
			if (DownloadHelpers.isValidItem(info)) {
				Log.d(TAG, "startTask:" + info.getPackageName() + ":"
						+ info.getVersionCode());
				DownloadHelpers.doAction(mContext, info, DownloadInfo.START);
				return true;
			}
			Log.d(TAG, "startTask error");
			return false;
		}

		@Override
		public boolean resumeTask(DownloadInfo info) {
//			Log.d(TAG,"DownloadManager resumeTask ");
//			try {
//				if (null != mLDownloadService) {
//					return mLDownloadService.resumeTask(info);
//				}
//				return false;
//			} catch (RemoteException e) {
//				e.printStackTrace();
//				return false;
//			}
			if (DownloadHelpers.isValidItem(info)) {
				Log.d(TAG, "resumeTask:" + info.getPackageName() + ":"
						+ info.getVersionCode());
				DownloadHelpers.doAction(mContext, info, DownloadInfo.CONTINUE);
				return true;
			}
			Log.d(TAG, "resumeTask error");
			return false;
		}

		@Override
		public boolean pauseTask(DownloadInfo info) {
			Log.d(TAG,"DownloadManager pauseTask ");
//			try {
//				if (null != mLDownloadService) {
//					return mLDownloadService.pauseTask(info);
//				}
//				return false;
//			} catch (RemoteException e) {
//				e.printStackTrace();
//				return false;
//			}
			if (DownloadHelpers.isValidItem(info)) {
				Log.d(TAG, "pauseTask:" + info.getPackageName() + ":"
						+ info.getVersionCode());
				DownloadHelpers.doAction(mContext, info, DownloadInfo.PAUSE);
				return true;
			}
			Log.d(TAG, "pauseTask error");
			return false;
		}

		@Override
		public synchronized boolean deleteTask(DownloadInfo appInfo) {
			Log.d(TAG,"DownloadManager deleteTask ");
//			try {
//				if (null != mLDownloadService) {
//					return mLDownloadService.deleteTask(appInfo);
//				}
//				return false;
//			} catch (RemoteException e) {
//				e.printStackTrace();
//				return false;
//			}
			if (DownloadHelpers.isValidItem(appInfo)) {
				Log.d(TAG, "deleteTask:" + appInfo.getPackageName() + ":"
						+ appInfo.getVersionCode());
				DownloadHelpers.doAction(mContext, appInfo, DownloadInfo.DELETE);
				return true;
			}
			Log.d(TAG, "deleteTask error");
			return false;
		}
		
		@Override
		public boolean reDownloadTask(DownloadInfo appInfo) {
//			try {
//				if (null != mLDownloadService) {
//					return mLDownloadService.reDownloadTask(appInfo);
//				}
//				return false;
//			} catch (RemoteException e) {
//				e.printStackTrace();
//				return false;
//			}
			if (DownloadHelpers.isValidItem(appInfo)) {
				Log.d(TAG, "reDownloadTask:" + appInfo.getPackageName() + ":"
						+ appInfo.getVersionCode());
				DownloadHelpers.doAction(mContext, appInfo, DownloadInfo.REDOWNLOAD);
				return true;
			}
			Log.d(TAG, "deleteTask error");
			return false;
		}

		@Override
		public DownloadInfo getDownloadInfo(DownloadInfo dinfo) {
//			try {
//				if (null != getServiceConnection()) {
//					return mLDownloadService.getDownloadInfo(dinfo);
//				}
//				return null;
//			} catch (RemoteException e) {
//				e.printStackTrace();
//				return null;
//			}
			return DownloadHelpers.getDownloadInfo(mContext, dinfo);

		}
		
		@Override
		public int getAllDownloadCount() {
//			try {
//				if (null != getServiceConnection()) {
//					return mLDownloadService.getAllDownloadCount();
//				}
//				return -1;
//			} catch (RemoteException e) {
//				e.printStackTrace();
//				return -1;
//			}
			return DownloadHelpers.getAllDownloadsCount(mContext);
		}

		@Override
		public synchronized List<DownloadInfo> getAllDownloadInfo() {
			return DownloadHelpers.getAllDownloadInfo(mContext);
		}

		@Override
		public void deleteAllTask() {
			DownloadHelpers.deleteAllTable(mContext);
		}

		@Override
		public synchronized void setDownloadListener(DownloadInfo info,
				IDownloadListener idl) {
			ds.setDownloadListener(info, idl);

		}

		@Override
		public synchronized void setDownloadListener(Context context,
				DownloadInfo info, IDownloadListener idl) {
			ds.setDownloadListener(context, info, idl);
		}

		@Override
		public synchronized void cancelDownloadListener(DownloadInfo info,
				IDownloadListener idl) {
			ds.cancelListener(info, idl);
		}

		@Override
		public synchronized void cancelDownloadListener() {
			ds.cancelListener();
		}

		@Override
		public synchronized void registerDownloadObserver(Observer obs) {
			watched.addObserver(obs);
		}

		@Override
		public synchronized void removeDownloadObserver(Observer obs) {
			watched.deleteObserver(obs);
		}

		@Override
		public Watched getWatcher() {
			return watched;
		}

		public synchronized void onDestroy() {
			Log.i(TAG, "Destroy DownloadManager");
//			if (mLDownloadConnection != null && mLDownloadService != null) {
//				try {
//					mContext.getApplicationContext().unbindService(
//							mLDownloadConnection);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
			// mLDownloadConnection = null;
			LDownloadManager.setmDownloadManager(null);

		}

//		private class LDownloadConnection implements ServiceConnection {
//
//			public void onServiceConnected(ComponentName name, IBinder service) {
//				Log.i(TAG, "Bind IDownloadService has connected !!!");
//				synchronized (LDownloadConnection.class) {
//					mLDownloadService = IDownloadService.Stub
//							.asInterface(service);
//				}
//			}
//
//			public void onServiceDisconnected(ComponentName name) {
//				Log.i(TAG, "Bind IDownloadService has disconnected !!!");
////				mContext.getApplicationContext().unbindService(mLDownloadConnection);
//				mLDownloadService = null;
//			}
//
//		}

//		private class DownloadObserver extends ContentObserver {
//			private Lock lock = new ReentrantLock();
//
//			public DownloadObserver() {
//				super(null);
//			}
//
//			@Override
//			public synchronized void onChange(boolean selfChange) {
//				lock.lock();
//				try{
//					List<DownloadInfo> downloads = DownloadHelpers
//					.getAllDownloadInfo(context);
//					watched.refreshTable(downloads);
//					
//				}finally{
//					lock.unlock();
//				}
//			}
//		}

		@Override
		public boolean getWifiStatus() {
			return downloadsp.getBoolean("wifi", false);
		}

		@Override
		public void setNetworkWifi(boolean flag) {
			Editor edit = downloadsp.edit();
			edit.putBoolean("wifi", flag);
			edit.commit();
			Intent intent = new Intent(Downloads.ACTION_DOWNLOAD_CONFIG);
			intent.addFlags(Downloads.FLAG_WIFI);
			intent.putExtra("wifi", flag);
			mContext.sendBroadcast(intent);
		}

		@Override
		public void configDownloadMax(int max) {
			Editor edit = downloadsp.edit();
			edit.putInt("downloadmax", max);
			edit.commit();
			Intent intent = new Intent(Downloads.ACTION_DOWNLOAD_CONFIG);
			intent.addFlags(Downloads.FLAG_DOWNLOADS);
			intent.putExtra("downloadmax", max);
			mContext.sendBroadcast(intent);

		}

		@Override
		public int getConfigDownloads() {
			return downloadsp.getInt("downloadmax", 3);
		}

		@Override
		public List<DownloadInfo> getCompletedDownloadInfo() {
			return DownloadHelpers.getCompletedDownloadInfo(mContext);
		}

		// zdx modify
		@Override
		public void setDownloadInfoInstall(DownloadInfo appInfo,
				Context context, boolean bInstalled) {
			DownloadHelpers
					.setDownloadInfoInstall(appInfo, context, bInstalled);
		}

	}

	public static class Watched extends Observable {
		public void refreshTable(List<DownloadInfo> downloads) {
			setChanged();
			notifyObservers(downloads);
		}
	}

}
