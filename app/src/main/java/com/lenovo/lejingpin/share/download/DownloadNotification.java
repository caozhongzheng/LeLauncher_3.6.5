package com.lenovo.lejingpin.share.download;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.lenovo.launcher.R;

/**
 * This class handles the updating of the Notification Manager for the cases
 * where there is an ongoing download. Once the download is complete (be it
 * successful or unsuccessful) it is no longer the responsibility of this
 * component to show the download in the notification manager.
 * 
 */
public class DownloadNotification {
	private final static String TAG = "xujing3";
	private Context mContext;
	public NotificationManager mNotificationMgr;
	private Set<NotificationItem> mNotification;

	private static final String contant = "com.lenovo.leos.contentmanager";
	private static final String WHERE_RUNNING = "("
			+ Downloads.Impl.COLUMN_STATUS + " >= '100') AND ("
			+ Downloads.Impl.COLUMN_STATUS + " <= '199') AND ("
			+ Downloads.Impl.COLUMN_NOTIFICATION_PACKAGE + " !='" + contant
			+ "') AND (" + Downloads.Impl.COLUMN_VISIBILITY + " IS NULL OR "
			+ Downloads.Impl.COLUMN_VISIBILITY + " == '"
			+ Downloads.Impl.VISIBILITY_VISIBLE + "')";

	private static final String RUNNING_WHERE = "("
			+ Downloads.Impl.COLUMN_STATUS + " == '192'" + ")";
	private static final String WHERE_COMPLETED = "("
			+ Downloads.Impl.COLUMN_STATUS + " == '200'" + ")";
	private static final String WHERE_PAUSE = "("
			+ Downloads.Impl.COLUMN_STATUS + " == '193'" + ")";

	/**
	 * This inner class is used to collate downloads that are owned by the same
	 * application. This is so that only one notification line item is used for
	 * all downloads of a given application.
	 * 
	 */
	static class NotificationItem implements Comparable<NotificationItem> {
		int mId;

		public int getmId() {
			return mId;
		}

		public void setmId(int mId) {
			this.mId = mId;
		}

		long mTotalCurrent = 0;
		long mTotalTotal = 0;
		int mTitleCount = 0;
		String mPackageName; // App package name
		String[] mTitles = new String[2];
		int progress;
		String appName;
		String textProgress;
		String mVersionCode;
		// zdx modify
		int controlStatus;

		public String getTextProgress() {
			return textProgress;
		}

		public void setTextProgress(String textProgress) {
			this.textProgress = textProgress;
		}

		public String getAppName() {
			return appName;
		}

		public void setAppName(String appName) {
			this.appName = appName;
		}

		// zdx modify
		public void setControlStatus(int controlStatus) {
			this.controlStatus = controlStatus;
		}

		public int getControlStatus() {
			return controlStatus;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + mId;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			NotificationItem other = (NotificationItem) obj;
			if (mId != other.mId)
				return false;
			return true;
		}

		/*
		 * Add a second download to this notification item.
		 */
		void addItem(String title, long currentBytes, long totalBytes) {
			mTotalCurrent += currentBytes;
			if (totalBytes <= 0 || mTotalTotal == -1) {
				mTotalTotal = -1;
			} else {
				mTotalTotal += totalBytes;
			}
			if (mTitleCount < 2) {
				mTitles[mTitleCount] = title;
			}
			mTitleCount++;
		}

		int getCurrentProgress() {
			return progress;
		}

		void setCurrentProgress(int progress) {
			this.progress = progress;
		}

		public int compareTo(NotificationItem another) {
			int cop = this.mId - another.mId;
			if (cop != 0) {
				return cop;
			} else {
				return 0;
			}
		}
	}

	/**
	 * The context to use to obtain access to the Notification Service
	 */
	DownloadNotification(Context ctx) {
		mContext = ctx;
		mNotificationMgr = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotification = new TreeSet<NotificationItem>();
	}

	/*
	 * Update the notification ui.
	 */
	public void updateNotification(BeanDownload downloadInfo) {
		//xujing3 temp remove
//		updateActiveNotification(downloadInfo);
	}

	Set<Integer> ids = Collections.synchronizedSet(new HashSet<Integer>());
	private NotificationItem mItem;

	private void updateActiveNotification(BeanDownload downloadInfo) {
		//xujing added
		
		if (downloadInfo != null) {
			if (downloadInfo.mStatus != Downloads.Impl.STATUS_RUNNING) {
				return;
			}

			int id = downloadInfo.mId;
			String appName = downloadInfo.mTitle;
			if (appName == null || appName.length() == 0) {
				appName = mContext.getResources().getString(
						R.string.download_unknown_title);
			}
			int currentBytes = downloadInfo.mCurrentBytes;
			int totalBytes = downloadInfo.mTotalBytes;
			// Log.i(TAG,"updateActiveNotification id:"+ id +", name:"+ appName
			// +"currentBytes:" + currentBytes +", totalBytes:"+totalBytes);

			String packageName = downloadInfo.mPackageName;
			String versionCode = downloadInfo.mVersionCode;
			int p = (int) (currentBytes / (float) totalBytes * 100);

			String textProgress = getDownloadingText(totalBytes, currentBytes);
			NotificationItem item = new NotificationItem();
			item.mId = id;
			item.mPackageName = packageName;
			item.addItem(appName, currentBytes, totalBytes);
			item.mVersionCode = versionCode;
			item.setCurrentProgress(p);
			item.setAppName(appName);
			item.setTextProgress(textProgress);

			// zdx modify
			int controlStatus = downloadInfo.mControl;
			item.setControlStatus(controlStatus);
			
			RemoteViews rw = new RemoteViews(mContext.getPackageName(),
					R.layout.magicdownload_download_notification_item);
			rw.setTextViewText(R.id.notification_download_appname,
					item.getAppName());
			String aName = item.getAppName();
			mItem = item;
			if (null == aName) {
				aName = "";
			}
			Notification notification = new Notification();
			if (item.getControlStatus() == Downloads.CONTROL_PAUSED) {
				notification.tickerText = mContext.getString(
						R.string.downloadpause_app_title, aName);
				notification.icon = R.drawable.magicdownload_download_stop;
				rw.setTextViewText(R.id.notification_downloading,
						mContext.getString(R.string.downloadpause_app));
			} else {
				notification.tickerText = mContext.getString(
						R.string.notification_start_download, aName);
				notification.icon = R.drawable.magicdownload_download_refresh;
				rw.setTextViewText(R.id.notification_downloading, mContext
						.getString(R.string.notification_downloading_app));
			}

			//int progress = item.getCurrentProgress();
			// Log.d("zdx", "updateActiveNotification >>>> name:"+ aName
			// +",progress : " + progress );
			rw.setProgressBar(R.id.notificationProgress, 100,
					item.getCurrentProgress(), false);
			rw.setTextViewText(R.id.notification_tx_progress,
					item.getTextProgress());

			notification.flags |= Notification.FLAG_ONGOING_EVENT;
			Intent notificationIntent = new Intent(
					LDownloadManager.ACTION_NOTIFICATION_CLICKED);
			Bundle b = new Bundle();
			DownloadInfo di = new DownloadInfo().setPackageName(
					mItem.mPackageName).setVersionCode(mItem.mVersionCode);
			b.putParcelable(LDownloadManager.RECEIVER_DATA_TAG, di);
			b.putString(LDownloadManager.NOTIFY_CLICK_TAG,
					LDownloadManager.STATUS_VALUES[1]);
			notificationIntent.putExtras(b);
			PendingIntent contentIntent = PendingIntent
					.getBroadcast(mContext, 2000, notificationIntent,
							PendingIntent.FLAG_UPDATE_CURRENT);
			notification.contentIntent = contentIntent;
			notification.contentView = rw;
			mNotificationMgr.notify(mItem.getmId(), notification);

		}
		
		/*
		mNotification.clear();
		if (downloadInfo != null) {
			if (downloadInfo.mStatus != Downloads.Impl.STATUS_RUNNING) {
				return;
			}

			int id = downloadInfo.mId;
			String appName = downloadInfo.mTitle;
			if (appName == null || appName.length() == 0) {
				appName = mContext.getResources().getString(
						R.string.download_unknown_title);
			}
			int currentBytes = downloadInfo.mCurrentBytes;
			int totalBytes = downloadInfo.mTotalBytes;
			// Log.i(TAG,"updateActiveNotification id:"+ id +", name:"+ appName
			// +"currentBytes:" + currentBytes +", totalBytes:"+totalBytes);

			String packageName = downloadInfo.mPackageName;
			String versionCode = downloadInfo.mVersionCode;
			int p = (int) (currentBytes / (float) totalBytes * 100);

			String textProgress = getDownloadingText(totalBytes, currentBytes);
			NotificationItem item = new NotificationItem();
			item.mId = id;
			item.mPackageName = packageName;
			item.addItem(appName, currentBytes, totalBytes);
			item.mVersionCode = versionCode;
			item.setCurrentProgress(p);
			item.setAppName(appName);
			item.setTextProgress(textProgress);

			// zdx modify
			int controlStatus = downloadInfo.mControl;
			item.setControlStatus(controlStatus);

			mNotification.add(item);
		}

		// Add the notifications
		for (NotificationItem item : mNotification) {
			RemoteViews rw = new RemoteViews(mContext.getPackageName(),
					R.layout.magicdownload_download_notification_item);
			rw.setTextViewText(R.id.notification_download_appname,
					item.getAppName());
			String aName = item.getAppName();
			mItem = item;
			if (null == aName) {
				aName = "";
			}
			Notification notification = new Notification();
			if (item.getControlStatus() == Downloads.CONTROL_PAUSED) {
				notification.tickerText = mContext.getString(
						R.string.downloadpause_app_title, aName);
				notification.icon = R.drawable.magicdownload_download_stop;
				rw.setTextViewText(R.id.notification_downloading,
						mContext.getString(R.string.downloadpause_app));
			} else {
				notification.tickerText = mContext.getString(
						R.string.notification_start_download, aName);
				notification.icon = R.drawable.magicdownload_download_refresh;
				rw.setTextViewText(R.id.notification_downloading, mContext
						.getString(R.string.notification_downloading_app));
			}

			int progress = item.getCurrentProgress();
			// Log.d("zdx", "updateActiveNotification >>>> name:"+ aName
			// +",progress : " + progress );
			rw.setProgressBar(R.id.notificationProgress, 100,
					item.getCurrentProgress(), false);
			rw.setTextViewText(R.id.notification_tx_progress,
					item.getTextProgress());

			notification.flags |= Notification.FLAG_ONGOING_EVENT;
			Intent notificationIntent = new Intent(
					LDownloadManager.ACTION_NOTIFICATION_CLICKED);
			Bundle b = new Bundle();
			DownloadInfo di = new DownloadInfo().setPackageName(
					mItem.mPackageName).setVersionCode(mItem.mVersionCode);
			b.putParcelable(LDownloadManager.RECEIVER_DATA_TAG, di);
			b.putString(LDownloadManager.NOTIFY_CLICK_TAG,
					LDownloadManager.STATUS_VALUES[1]);
			notificationIntent.putExtras(b);
			PendingIntent contentIntent = PendingIntent
					.getBroadcast(mContext, 2000, notificationIntent,
							PendingIntent.FLAG_UPDATE_CURRENT);
			notification.contentIntent = contentIntent;
			notification.contentView = rw;
			mNotificationMgr.notify(mItem.getmId(), notification);
		}
		*/
	}

	/*
	 * Helper function to build the downloading text.
	 */
	public static String getDownloadingText(int totalBytes, int currentBytes) {
		if (totalBytes <= 0) {
			return "";
		}
		long progress = currentBytes * 100 / totalBytes;
		StringBuilder sb = new StringBuilder();
		sb.append(progress);
		sb.append('%');
		sb.append('(');
		int i = String.valueOf(currentBytes / 1024.0 / 1024.0).indexOf(".") + 2;
		sb.append(String.valueOf(currentBytes / 1024.0 / 1024.0)
				.substring(0, i));
		sb.append('M');
		sb.append('\\');
		int j = String.valueOf(totalBytes / 1024.0 / 1024.0).indexOf(".") + 2;
		sb.append(String.valueOf(totalBytes / 1024.0 / 1024.0).substring(0, j));
		sb.append('M');
		sb.append(')');
		return sb.toString();
	}
}
