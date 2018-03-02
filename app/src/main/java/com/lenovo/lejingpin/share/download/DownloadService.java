package com.lenovo.lejingpin.share.download;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import java.util.Collection;

import com.lenovo.launcher.R;
import com.lenovo.lejingpin.share.util.Utilities;

/**
 * Performs the background downloads requested by applications that use the
 * Downloads provider.
 */
public class DownloadService extends Service {
	private final static String TAG = "DownloadService";

	/** Observer to get notified when the content observer's data changes */
	private DownloadManagerContentObserver mObserver;

	/** Class to handle Notification Manager updates */
//	private DownloadNotification mNotifier;

	/**
	 * The Service's view of the list of downloads. This is kept independently
	 * from the content provider, and the Service only initiates downloads based
	 * on this data, so that it can deal with situation where the data in the
	 * content provider changes or disappears.
	 */
	// private ArrayList<BeanDownload> mDownloads;
	private final HashMap<Integer, BeanDownload> mDownloads = new HashMap<Integer, BeanDownload>();

	/**
	 * The thread that updates the internal download list from the content
	 * provider.
	 */
	private UpdateThread mUpdateThread;

	/**
	 * Whether the internal download list should be updated from the content
	 * provider.
	 */
	private boolean mPendingUpdate;
	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;

	private int mDownloadCount = 0;

	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			processMessage(msg);
		}
	}

	void processMessage(Message msg) {
		Log.i(TAG,"------------------processMessage-------------msg.arg1:" + msg.arg1);
		if (msg.arg1 == 0) {
			sendMessageDownloadResume();
		}
		updateFromProvider();
	}

	/**
	 * Receives notifications when the data in the content provider changes
	 */
	private class DownloadManagerContentObserver extends ContentObserver {

		public DownloadManagerContentObserver() {
			super(new Handler());
		}

		/**
		 * Receives notification when the data in the observed content provider
		 * changes.
		 */
		public void onChange(final boolean selfChange) {
//			BeanDownload downloadInfo = DownloadQueueHandler.getInstance()
//					.getCurrentDownload();
			notifyDownloadCountChanged(DownloadService.this,mDownloads.size());
//			mNotifier.updateNotification(downloadInfo);
			
		}

	}

	/**
	 * Returns an IBinder instance when someone wants to connect to this
	 * service. Binding to this service is not allowed.
	 * 
	 * @throws UnsupportedOperationException
	 */
	public IBinder onBind(Intent i) {
		throw new UnsupportedOperationException(
				"Cannot bind to Download Manager Service");
	}

	/**
	 * Initializes the service when it is first created
	 */
	public void onCreate() {
		super.onCreate();
		Utilities.setDownloadActive(true, this.getClass().getName());
//		Utilities.mDownloadServiceActive = true;
		Log.i(TAG, "^^^^DownloadService.onCreate()^^^^");
		mObserver = new DownloadManagerContentObserver();
		getContentResolver().registerContentObserver(
				Downloads.Impl.CONTENT_URI, true, mObserver);
		mDownloadCount = mDownloads.size();
		
		
//		mNotifier = new DownloadNotification(this);
//		mNotifier.mNotificationMgr.cancelAll();
//		BeanDownload downloadInfo = DownloadQueueHandler.getInstance()
//				.getCurrentDownload();
//		mNotifier.updateNotification(downloadInfo);

		trimDatabase();
		removeSpuriousFiles();

		HandlerThread thread = new HandlerThread("DownloadService",
				Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();
		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);
	}

	/**
	 * Responds to a call to startService
	 */
	/*
	 * public void onStart(Intent intent, int startId) { super.onStart(intent,
	 * startId); Log.i(TAG,
	 * "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^DownloadService.onStart()^^^^"
	 * ); sendMessageDownloadResume(); updateFromProvider(); Message msg =
	 * mServiceHandler.obtainMessage(); mServiceHandler.sendMessage(msg); }
	 */

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			 Log.i(TAG,"***********************onStartCommand, startId:"+
			 startId);
			Message msg = mServiceHandler.obtainMessage();
			msg.arg1 = intent.getIntExtra("type", 0);
			mServiceHandler.sendMessage(msg);
		}
		return Service.START_REDELIVER_INTENT;
	}

	/**
	 * Cleans up when the service is destroyed
	 */
	public void onDestroy() {
		Log.i(TAG, "^^^^DownloadService.onDestroy()^^^^");
		getContentResolver().unregisterContentObserver(mObserver);
		mServiceLooper.quit();
//		Utilities.mDownloadServiceActive = false;
		Utilities.setDownloadActive(false, this.getClass().getName());
		Utilities.killLejingpinProcess();
		super.onDestroy();
	}

	/**
	 * Parses data from the content provider into private array
	 */
	private void updateFromProvider() {
		synchronized (this) {
			mPendingUpdate = true;
			if (mUpdateThread == null) {
				mUpdateThread = new UpdateThread();
				mUpdateThread.start();
			}
		}
	}

	private class UpdateThread extends Thread {
		public UpdateThread() {
			super("Download Service");
		}

		public void run() {
			Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
			boolean keepService = false;
			long wakeUp = Long.MAX_VALUE;
			for (;;) {
				synchronized (DownloadService.this) {
					if (mUpdateThread != this) {
						Log.e(TAG,
								"Not allow multiple UpdateThreads in DownloadService");
						return;
					}
					if (!mPendingUpdate) {
						mUpdateThread = null;
						if (!keepService) {
							notifyDownloadCountChanged(DownloadService.this,0);
							stopSelf();
						}
						if (wakeUp != Long.MAX_VALUE) {
							scheduleAlarm(wakeUp);
						}
						return;
					}
					mPendingUpdate = false;
				}

				// zdx modify
				// Cursor cursor =
				// getContentResolver().query(Downloads.Impl.CONTENT_URI, null,
				// null, null,Downloads.Impl._ID);
				Cursor cursor = null;
				Set<Integer> idsNoLongerInDatabase = new HashSet<Integer>(
						mDownloads.keySet());
				try{
					cursor = getContentResolver().query(
						Downloads.Impl.CONTENT_URI,
						null,
//						Downloads.Impl.COLUMN_CONTROL + " != 1 and " +
						Downloads.Impl.COLUMN_STATUS + " != 200 and "			
						+ Downloads.Impl.COLUMN_STATUS + " != 10000",
						null, Downloads.Impl._ID);
					if (cursor == null) {
						continue;
					}
					long now = System.currentTimeMillis();
					keepService = false;
					wakeUp = Long.MAX_VALUE;

					boolean networkAvailable = Helpers
							.isNetworkAvailable(DownloadService.this);

					int idColumn = cursor
							.getColumnIndexOrThrow(Downloads.Impl._ID);
					//xujing3 added
					boolean hasActiveTask = false;
					
					for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
							.moveToNext()) {
						int id = cursor.getInt(idColumn);
						idsNoLongerInDatabase.remove(id);
						
						//xujing3 added
						int control = cursor.getInt(cursor.getColumnIndexOrThrow(Downloads.Impl.COLUMN_CONTROL));
						if(networkAvailable && Downloads.Impl.CONTROL_RUN == control){
							hasActiveTask = true;
						}
						
						BeanDownload info = mDownloads.get(id);
						if (info != null) {
							updateDownload(cursor, id, networkAvailable, now);
						} else {
							info = insertDownload(cursor, networkAvailable, now);
						}
						if (shouldScanFile(id)) {
							keepService = true;
						}
						long next = nextAction(id, now);
						if (next == 0) {
							keepService = true;
						} else if (next > 0 && next < wakeUp) {
							wakeUp = next;
						}
					}
					
					if(!hasActiveTask){
						keepService = false;
					}
					
				}catch (Exception e) {
					e.printStackTrace();
				} finally {
					if(cursor != null && !cursor.isClosed()){
						
						cursor.close();
					}
				}

				for (Integer id : idsNoLongerInDatabase) {
					deleteDownload(id);
				}
				notifyDownloadCountChanged(DownloadService.this,mDownloads.size());
				// mNotifier.updateNotification(mDownloads.values());
//				BeanDownload downloadInfo = DownloadQueueHandler.getInstance()
//						.getCurrentDownload();
//				mNotifier.updateNotification(downloadInfo);
			}
		}
	}

	/**
	 * Removes files that may have been left behind in the cache directory
	 */
	private void removeSpuriousFiles() {
		File[] files = Environment.getDownloadCacheDirectory().listFiles();
		if (files == null) {
			// The cache folder doesn't appear to exist (this is likely the case
			// when running the simulator).
			return;
		}
		HashSet<String> fileSet = new HashSet();
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().equals(Constants.KNOWN_SPURIOUS_FILENAME)) {
				continue;
			}
			if (files[i].getName().equalsIgnoreCase(
					Constants.RECOVERY_DIRECTORY)) {
				continue;
			}
			fileSet.add(files[i].getPath());
		}
		Cursor cursor = null;
		try{
			cursor = getContentResolver().query(Downloads.Impl.CONTENT_URI,
					new String[] { Downloads.Impl._DATA }, null, null, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						fileSet.remove(cursor.getString(0));
					} while (cursor.moveToNext());
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(cursor!=null && !cursor.isClosed()){
				cursor.close();
			}
		}
		Iterator<String> iterator = fileSet.iterator();
		while (iterator.hasNext()) {
			String filename = iterator.next();
			new File(filename).delete();
		}
	}

	/**
	 * Drops old rows from the database to prevent it from growing too large
	 */
	private void trimDatabase() {
		Cursor cursor = null;
		try{
			cursor = getContentResolver().query(Downloads.Impl.CONTENT_URI,
					new String[] { Downloads.Impl._ID },
					Downloads.Impl.COLUMN_STATUS + " >= '200'", null,
					Downloads.Impl.COLUMN_LAST_MODIFICATION);
			if (cursor == null) {
				// This isn't good - if we can't do basic queries in our database,
				// nothing's gonna work
				Log.e(Constants.TAG, "null cursor in trimDatabase");
				return;
			}
			if (cursor.moveToFirst()) {
				int numDelete = cursor.getCount() - Constants.MAX_DOWNLOADS;
				int columnId = cursor.getColumnIndexOrThrow(Downloads.Impl._ID);
				while (numDelete > 0) {
					getContentResolver().delete(
							ContentUris.withAppendedId(Downloads.Impl.CONTENT_URI,
									cursor.getInt(columnId)), null, null);
//					DownloadInfoContainer.deleteDBAndBufferById(this, Downloads.Impl.CONTENT_URI, String.valueOf(cursor.getInt(columnId)));
					if (!cursor.moveToNext()) {
						break;
					}
					numDelete--;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(cursor!=null && !cursor.isClosed()){
				cursor.close();
			}
		}
	}

	/**
	 * Keeps a local copy of the info about a download, and initiates the
	 * download if appropriate.
	 */
	private BeanDownload insertDownload(Cursor cursor,
			boolean networkAvailable, long now) {
		int retryRedirect = getInt(cursor,
				Constants.RETRY_AFTER_X_REDIRECT_COUNT);
		int id = getInt(cursor, Downloads.Impl._ID);
		BeanDownload info = new BeanDownload(
				getInt(cursor, Downloads.Impl._ID), getString(cursor,
						Downloads.Impl.COLUMN_URI), getInt(cursor,
						Downloads.Impl.COLUMN_NO_INTEGRITY) == 1, getString(
						cursor, Downloads.Impl.COLUMN_FILE_NAME_HINT),
				getString(cursor, Downloads.Impl._DATA), getString(cursor,
						Downloads.Impl.COLUMN_MIME_TYPE), getInt(cursor,
						Downloads.Impl.COLUMN_DESTINATION), getInt(cursor,
						Downloads.Impl.COLUMN_VISIBILITY), getInt(cursor,
						Downloads.Impl.COLUMN_CONTROL), getInt(cursor,
						Downloads.Impl.COLUMN_STATUS), getInt(cursor,
						Constants.FAILED_CONNECTIONS),
				retryRedirect & 0xfffffff, retryRedirect >> 28, getLong(cursor,
						Downloads.Impl.COLUMN_LAST_MODIFICATION), getString(
						cursor, Downloads.Impl.COLUMN_NOTIFICATION_PACKAGE),
				getString(cursor, Downloads.Impl.COLUMN_NOTIFICATION_CLASS),
				getString(cursor, Downloads.Impl.COLUMN_NOTIFICATION_EXTRAS),
				getString(cursor, Downloads.Impl.COLUMN_COOKIE_DATA),
				getString(cursor, Downloads.Impl.COLUMN_USER_AGENT), getString(
						cursor, Downloads.Impl.COLUMN_REFERER), getInt(cursor,
						Downloads.Impl.COLUMN_TOTAL_BYTES), getInt(cursor,
						Downloads.Impl.COLUMN_CURRENT_BYTES), getString(cursor,
						Constants.ETAG),
				getInt(cursor, Constants.MEDIA_SCANNED) == 1,
				// zdx modify
				getInt(cursor, Downloads.Impl.COLUMN_CATEGORY), getString(
						cursor, Downloads.Impl.COLUMN_TITLE), getString(cursor,
						Downloads.Impl.COLUMN_PKGNAME), getString(cursor,
						Downloads.Impl.COLUMN_VERSIONCODE), getString(cursor,
						Downloads.Impl.COLUMN_DESCRIPTION), getString(cursor,
						Downloads.Impl.COLUMN_ICONADDR));
		String title = getString(cursor, Downloads.Impl.COLUMN_TITLE);
		Log.i(TAG, "DownloadService.insertDownload, mDownloads.put id:" + id
				+ ",name:" + title);
		mDownloads.put(id, info);
		if (info.canUseNetwork(networkAvailable)) {
			if (info.isReadyToStart(now)) {
				if (info.mHasActiveThread) {
					return info;
				}
				DownloadQueueHandler.getInstance().enqueueDownload(this, info);
			}
		} else {
			if (info.mStatus == 0
					|| info.mStatus == Downloads.Impl.STATUS_PENDING
					|| info.mStatus == Downloads.Impl.STATUS_RUNNING) {
				info.mStatus = Downloads.Impl.STATUS_RUNNING_PAUSED;
				Uri uri = ContentUris.withAppendedId(
						Downloads.Impl.CONTENT_URI, info.mId);
				ContentValues values = new ContentValues();
				values.put(Downloads.Impl.COLUMN_STATUS,
						Downloads.Impl.STATUS_RUNNING_PAUSED);
				getContentResolver().update(uri, values, null, null);
//				DownloadInfoContainer.updateDBAndBufferById(this, uri, values, null);
			}
		}
		return info;
	}

	/**
	 * Updates the local copy of the info about a download.
	 */
	private void updateDownload(Cursor cursor, int id,
			boolean networkAvailable, long now) {
		if(DownloadQueueHandler.getInstance().isLocked(id)){
			return ;
		}
		
		BeanDownload info = mDownloads.get(id);
		info.mId = getInt(cursor, Downloads.Impl._ID);
		info.mUri = getString(cursor, Downloads.Impl.COLUMN_URI);
		info.mNoIntegrity = getInt(cursor, Downloads.Impl.COLUMN_NO_INTEGRITY) == 1;
		info.mHint = getString(cursor, Downloads.Impl.COLUMN_FILE_NAME_HINT);
		info.mFileName = getString(cursor, Downloads.Impl._DATA);
		info.mMimeType = getString(cursor, Downloads.Impl.COLUMN_MIME_TYPE);
		info.mDestination = getInt(cursor, Downloads.Impl.COLUMN_DESTINATION);
		info.mVisibility = getInt(cursor, Downloads.Impl.COLUMN_VISIBILITY);
		synchronized (info) {
			info.mControl = getInt(cursor, Downloads.Impl.COLUMN_CONTROL);
			info.mStatus = getInt(cursor, Downloads.Impl.COLUMN_STATUS);
		}
		info.mNumFailed = getInt(cursor, Constants.FAILED_CONNECTIONS);

		int retryRedirect = getInt(cursor,
				Constants.RETRY_AFTER_X_REDIRECT_COUNT);
		info.mRetryAfter = retryRedirect & 0xfffffff;
		info.mRedirectCount = retryRedirect >> 28;
		info.mLastMod = getLong(cursor, Downloads.Impl.COLUMN_LAST_MODIFICATION);
		info.mPackage = getString(cursor,
				Downloads.Impl.COLUMN_NOTIFICATION_PACKAGE);
		info.mClass = getString(cursor,
				Downloads.Impl.COLUMN_NOTIFICATION_CLASS);
		info.mCookies = getString(cursor, Downloads.Impl.COLUMN_COOKIE_DATA);
		info.mUserAgent = getString(cursor, Downloads.Impl.COLUMN_USER_AGENT);
		info.mReferer = getString(cursor, Downloads.Impl.COLUMN_REFERER);
		info.mTotalBytes = getInt(cursor, Downloads.Impl.COLUMN_TOTAL_BYTES);
		info.mCurrentBytes = getInt(cursor, Downloads.Impl.COLUMN_CURRENT_BYTES);
		info.mETag = getString(cursor, Constants.ETAG);
		info.mMediaScanned = getInt(cursor, Constants.MEDIA_SCANNED) == 1;
		Log.e(TAG,
				"updateDownload------info.id:" + info.mId + ",info.mStatus: " + info.mStatus + ",control:" + info.mControl);
//		 if( DownloadQueueHandler.getInstance().hasDownloadInQueue(info.mId)){
//			 return;
//		 }
		if (info.canUseNetwork(networkAvailable)) {
			if (info.isReadyToRestart(now)) {
				if (info.mHasActiveThread) {
					Log.e(TAG,
							"updateDownload, Not start DownloadService !!!");
					return;
				}
				
				DownloadQueueHandler.getInstance().enqueueDownload(this, info);
			}
		}
	}

	/**
	 * Removes the local copy of the info about a download.
	 */
	private void deleteDownload(int id) {
		BeanDownload info = (BeanDownload) mDownloads.get(id);
		Log.i(TAG, "*******************deleteDownload , id :" + id + " , name:"
				+ info.mTitle);
		if (info.mStatus == Downloads.Impl.STATUS_RUNNING) {
			info.mStatus = Downloads.Impl.STATUS_CANCELED;
		} else if (info.mDestination != Downloads.DESTINATION_USERCHOOSED
				&& info.mFileName != null) {
			new File(info.mFileName).delete();
		}
//		mNotifier.mNotificationMgr.cancel(info.mId);
		mDownloads.remove(id);
		notifyDownloadCountChanged(DownloadService.this,mDownloads.size());
	}

	/**
	 * Returns the amount of time (as measured from the "now" parameter) at
	 * which a download will be active. 0 = immediately - service should stick
	 * around to handle this download. -1 = never - service can go away without
	 * ever waking up. positive value - service must wake up in the future, as
	 * specified in ms from "now"
	 */
	private long nextAction(int id, long now) {
		BeanDownload info = (BeanDownload) mDownloads.get(id);
		if (Downloads.Impl.isStatusCompleted(info.mStatus)) {
			return -1;
		}
		if (info.mStatus != Downloads.Impl.STATUS_RUNNING_PAUSED) {
			return 0;
		}
		if (info.mNumFailed == 0) {
			return 0;
		}
		long when = info.restartTime();
		if (when <= now) {
			return 0;
		}
		return when - now;
	}

	/**
	 * Returns whether a file should be scanned
	 */
	private boolean shouldScanFile(int id) {
		BeanDownload info = (BeanDownload) mDownloads.get(id);
		return !info.mMediaScanned
				&& (info.mDestination == Downloads.DESTINATION_USERCHOOSED)
				&& Downloads.Impl.isStatusSuccess(info.mStatus)
				&& !Constants.MIMETYPE_DRM_MESSAGE
						.equalsIgnoreCase(info.mMimeType);
	}

	private void sendMessageDownloadResume() {
		Message msg = new Message();
		msg.what = DownloadHandler.MSG_DOWNLOAD_RESUME;
		DownloadHandler.getInstance(this).removeMessages(DownloadHandler.MSG_DOWNLOAD_RESUME);
		DownloadHandler.getInstance(this).sendMessage(msg);
		
	}

	private String getString(Cursor cursor, String column) {
		int index = cursor.getColumnIndexOrThrow(column);
		String s = cursor.getString(index);
		return (TextUtils.isEmpty(s)) ? null : s;
	}

	private int getInt(Cursor cursor, String column) {
		return cursor.getInt(cursor.getColumnIndexOrThrow(column));
	}

	private long getLong(Cursor cursor, String column) {
		return cursor.getLong(cursor.getColumnIndexOrThrow(column));
	}

	private void scheduleAlarm(long wakeUp) {
		AlarmManager alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		if (alarms != null) {
			// Log.v(TAG, "^^^^UpdateThread.run(), scheduling retry in " +
			// wakeUp + "ms");
			Intent intent = new Intent(Constants.ACTION_RETRY);
			alarms.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
					+ wakeUp, PendingIntent.getBroadcast(DownloadService.this,
					0, intent, PendingIntent.FLAG_ONE_SHOT));
		}
	}
	public void notifyDownloadCountChanged(Context context,int count){
		if(mDownloadCount == count)
			return;
		else{
			mDownloadCount = count;
		}
		Log.d(TAG,"notifyDownloadCountChanged , count: " + count);
		Intent intent = new Intent(LDownloadManager.ACTION_DOWNLOAD_COUNT_CHANGED);
		intent.putExtra("count", count);
		context.sendBroadcast(intent);
	}
	
}
