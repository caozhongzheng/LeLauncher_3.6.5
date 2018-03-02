/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lenovo.lejingpin.share.download;

import android.app.NotificationManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Collection;

public class DownloadQueueHandler {

	private static final String TAG = "DownloadQueueHandler";
	private final LinkedHashMap<Integer, BeanDownload> mDownloadsQueue = new LinkedHashMap<Integer, BeanDownload>();
	private final HashMap<Integer, BeanDownload> mDownloadsInProgress = new HashMap<Integer, BeanDownload>();
	private BeanDownload currentDownload = null;
	private static final DownloadQueueHandler mDownloadQueueHandler = new DownloadQueueHandler();
	private final int mMaxConcurrentDownloadsAllowed = 1;
	private static boolean mLock = false;

	static DownloadQueueHandler getInstance() {
		return mDownloadQueueHandler;
	}
	
	//xujing3 added
	boolean isLocked(int id){
		if(mDownloadsInProgress.containsKey(id)){
			return mLock;
		}
		return false;
	}

	synchronized void enqueueDownload(Context context, BeanDownload info) {
		if (!hasDownloadInQueue(info.mId)) {
			mDownloadsQueue.put(info.mId, info);
			Log.i(TAG, "DownloadQueueHandler.enqueueDownload,add id : "
					+ info.mId + ", filename:" + info.mFileName);
			startDownloadThread(context);
			Log.i(TAG, "DownloadQueueHandler.enqueueDownload, queue count:"
					+ mDownloadsQueue.size() + ",queueInProgress count:"
					+ mDownloadsInProgress.size());
		} else {
			if (mDownloadsInProgress.containsKey(info.mId)
					&& info.mStatus != Downloads.Impl.STATUS_RUNNING
					&& !mLock) {
				Log.i(TAG,
						"222 DownloadQueueHandler.enqueueDownload, queue count:"
								+ mDownloadsQueue.size()
								+ ",queueInProgress count:"
								+ mDownloadsInProgress.size());
				info.startDownloadThread(context);
				currentDownload = info;
				updateDownloadStatusInProcess(context, info);
			}
		}
	}

	private synchronized void startDownloadThread(Context context) {
		Iterator<Integer> keys = mDownloadsQueue.keySet().iterator();
		ArrayList<Integer> ids = new ArrayList<Integer>();
		mLock = true;
		while (mDownloadsInProgress.size() < mMaxConcurrentDownloadsAllowed
				&& keys.hasNext()) {
			Integer id = keys.next();
			ids.add(id);
			if(Downloads.CONTROL_PAUSED == mDownloadsQueue.get(id).mControl)
				continue;
			
			mDownloadsInProgress.put(id, mDownloadsQueue.get(id));
			
			currentDownload = mDownloadsQueue.get(id);
			updateDownloadStatusInProcess(context, mDownloadsQueue.get(id));

			BeanDownload info = mDownloadsQueue.get(id);
			if (info.mUri.equals(DownloadConstant.TYPE_DOWNLOAD_ACTION)) {
				info.getDownloadUrl(context);
			} else {
				info.startDownloadThread(context);
			}
			Log.i(TAG,
					"DownloadQueueHandler.startDownloadThread,start download id : "
							+ info.mId + ", filename:" + info.mFileName);
		}
		mLock = false;

		for (Integer id : ids) {
			mDownloadsQueue.remove(id);
		}

		Iterator<Integer> keys2 = mDownloadsQueue.keySet().iterator();
		while (mDownloadsQueue.size() > 0 && keys2.hasNext()) {
			Integer id2 = keys2.next();

			ContentValues values = new ContentValues();
			BeanDownload downloadInfo = mDownloadsQueue.get(id2);
			if (downloadInfo != null) {
				if (downloadInfo.mStatus != Downloads.Impl.STATUS_RUNNING_PAUSED) {
					downloadInfo.mStatus = Downloads.Impl.STATUS_RUNNING_PAUSED;
					values.put(Downloads.Impl.COLUMN_STATUS,
							Downloads.Impl.STATUS_RUNNING_PAUSED);
					Log.i(TAG,
							"*****DownloadQueueHandler Downloads.Impl.STATUS_RUNNING_PAUSED, id:"
									+ id2);
					context.getContentResolver().update(Downloads.CONTENT_URI,
							values, "_ID = ?", new String[] { "" + id2 });
//					DownloadInfoContainer.updateDBAndBufferById(context, 
//							Downloads.CONTENT_URI, values, String.valueOf(id2));
				}
			}
		}
	}

	public synchronized boolean hasDownloadInQueue(int id) {
		return mDownloadsQueue.containsKey(id)
				|| mDownloadsInProgress.containsKey(id);
	}

	synchronized void dequeueDownload(Context context, int id, boolean netError) {
		Log.i(TAG, "DownloadQueueHandler.dequeueDownload,remove id : " + id
				+ ", netError:" + netError);
		if (netError) {
			NotificationManager mNotificationMgr = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationMgr.cancel(id);
			return;
		}

		mDownloadsInProgress.remove(id);
		startDownloadThread(context);
		if (mDownloadsInProgress.size() == 0 && mDownloadsQueue.size() == 0) {
			notifyAll();
		}

		Log.i(TAG, "DownloadQueueHandler.dequeueDownload, queue count:"
				+ mDownloadsQueue.size() + ",queueInProgress count:"
				+ mDownloadsInProgress.size() + "\n");
	}

	private synchronized void updateDownloadStatusInProcess(Context context,
			BeanDownload info) {
		info.mStatus = Downloads.Impl.STATUS_RUNNING;
		ContentValues values = new ContentValues();

		Log.i(TAG,
				"DownloadQueueHandler.updateDownloadStatusInProcess(), Update status is STATUS_RUNNING");
		values.put(Downloads.Impl.COLUMN_STATUS, Downloads.Impl.STATUS_RUNNING);
		Log.i(TAG,
				"*****DownloadQueueHandler Downloads.Impl.STATUS_RUNNING, id:"
						+ info.mId);
		context.getContentResolver()
				.update(ContentUris.withAppendedId(Downloads.Impl.CONTENT_URI,
						info.mId), values, null, null);
//		DownloadInfoContainer.updateDBAndBufferById(context, 
//				Downloads.Impl.CONTENT_URI, values, String.valueOf(info.mId));
	}

	public Collection<BeanDownload> getDownloadsInProgress() {
		return mDownloadsInProgress.values();
	}

	public HashMap<Integer, BeanDownload> getDownloadsInProgressMap() {
		return mDownloadsInProgress;
	}

	public BeanDownload getDownloadInfoById(int id) {
		if (hasDownloadInQueue(id)) {
			BeanDownload downloadInfo = mDownloadsInProgress.get(id);
			if (downloadInfo == null) {
				downloadInfo = mDownloadsQueue.get(id);
			}
			return downloadInfo;
		}
		return null;
	}

	public void deleteDownloadInfoById(int id) {
		if (mDownloadsInProgress.containsKey(id)) {
			mDownloadsInProgress.remove(id);
		}
		if (mDownloadsQueue.containsKey(id)) {
			mDownloadsQueue.remove(id);
		}
	}

	public BeanDownload getCurrentDownload() {
		return currentDownload;
	}

	void updateDownloadInfoById(Context context, int id, String downloadUri) {
		if (mDownloadsQueue.containsKey(id)) {
			BeanDownload info = mDownloadsQueue.get(id);
			info.mUri = downloadUri;
		}
		if (mDownloadsInProgress.containsKey(id)) {
			BeanDownload info = mDownloadsInProgress.get(id);
			info.mUri = downloadUri;
			info.startDownloadThread(context);
		}
	}
}
