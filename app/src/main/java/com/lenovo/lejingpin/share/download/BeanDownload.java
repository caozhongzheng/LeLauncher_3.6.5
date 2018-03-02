package com.lenovo.lejingpin.share.download;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * Stores information about an individual download.
 */
public class BeanDownload {
	public int mId;
	public String mUri;
	public boolean mNoIntegrity;
	public String mHint;
	public String mFileName;
	public String mMimeType;
	public int mDestination;
	public int mVisibility;
	public int mControl;
	public int mStatus;
	public int mNumFailed;
	public int mRetryAfter;
	public int mRedirectCount;
	public long mLastMod;
	public String mPackage;
	public String mClass;
	public String mExtras;
	public String mCookies;
	public String mUserAgent;
	public String mReferer;
	public int mTotalBytes;
	public int mCurrentBytes;
	public String mETag;
	public boolean mMediaScanned;
	// zdx modify
	public int mCategory;
	public String mTitle;
	public String mPackageName;
	public String mVersionCode;
	public String mDescription;
	public String mIconAddr;

	public int mFuzz;

	public volatile boolean mHasActiveThread;

	private final static String TAG = "BeanDownload";

	public BeanDownload(int id, String uri, boolean noIntegrity, String hint,
			String fileName, String mimeType, int destination, int visibility,
			int control, int status, int numFailed, int retryAfter,
			int redirectCount, long lastMod, String pckg, String clazz,
			String extras, String cookies, String userAgent, String referer,
			int totalBytes, int currentBytes, String eTag,
			boolean mediaScanned,
			// zdx modify
			int category, String title, String packageName,
			String versionCode, String description, String iconAddr) {
		mId = id;
		mUri = uri;
		mNoIntegrity = noIntegrity;
		mHint = hint;
		mFileName = fileName;
		mMimeType = mimeType;
		mDestination = destination;
		mVisibility = visibility;
		mControl = control;
		mStatus = status;
		mNumFailed = numFailed;
		mRetryAfter = retryAfter;
		mRedirectCount = redirectCount;
		mLastMod = lastMod;
		mPackage = pckg;
		mClass = clazz;
		mExtras = extras;
		mCookies = cookies;
		mUserAgent = userAgent;
		mReferer = referer;
		mTotalBytes = totalBytes;
		mCurrentBytes = currentBytes;
		mETag = eTag;
		mMediaScanned = mediaScanned;
		mFuzz = Helpers.sRandom.nextInt(1001);
		// zdx modify
		mCategory = category;
		mTitle = title;
		mPackageName = packageName;
		mVersionCode = versionCode;
		mDescription = description;
		mIconAddr = iconAddr;
	}

	public void sendIntentIfRequested(Uri contentUri, Context context) {

		Intent intent = new Intent(LDownloadManager.ACTION_DOWNLOAD_COMPLETED);
		Cursor sc = context.getContentResolver().query(contentUri, null, null,
				null, null);
		DownloadInfo info = new DownloadInfo();
		int status = -1;
		try {
			if (sc.moveToFirst()) {
				long id = ContentUris.parseId(contentUri);
				String pkgName = sc.getString(sc.getColumnIndex("pkgname"));
				String versionCode = sc.getString(sc
						.getColumnIndex("versioncode"));
				String appName = sc.getString(sc.getColumnIndex("title"));
				String iconAddr = sc.getString(sc.getColumnIndex("iconaddr"));
				String installPath = sc.getString(sc.getColumnIndex("_data"));
				String downloadUrl = sc.getString(sc.getColumnIndex("uri"));
				status = sc.getInt(sc.getColumnIndex("status"));

				int wifistatus = sc.getInt(sc.getColumnIndex("wifistatus"));
				long currentBytes = sc.getLong(sc
						.getColumnIndex("current_bytes"));
				long totalBytes = sc.getLong(sc.getColumnIndex("total_bytes"));
				int progress = 0;
				if (totalBytes != 0 && currentBytes != 0) {
					progress = DownloadHelpers.getProgresss(currentBytes,
							totalBytes);
				}
				// zdx modify
				if ((status != Downloads.STATUS_INSTALL)
						&& (status != Downloads.STATUS_SUCCESS)) {
					if(progress == 100){
						status = Downloads.STATUS_SUCCESS;
					}else{
						status = Helpers.getStatus(status);
					}
//					if (progress == 100)
//						status = Downloads.STATUS_SUCCESS;
				}
				int category = sc.getInt(sc.getColumnIndex("category"));
				info.setCategory(category);
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
				info.setWifistatus(wifistatus);
				info.setDownloadStatus(status);
				info.setDownloadUrl(downloadUrl);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sc.close();
		}
		 Log.d(TAG,"BeanDownload.sendIntentIfRequested, send download Completed !!!, status:"+
		 status);
		intent.putExtra(LDownloadManager.RECEIVER_DATA_TAG, info);
		context.sendBroadcast(intent);
	}

	/**
	 * Returns the time when a download should be restarted. Must only be called
	 * when numFailed > 0.
	 */
	public long restartTime() {
		if (mRetryAfter > 0) {
			return mLastMod + mRetryAfter;
		}
		return mLastMod + Constants.RETRY_FIRST_DELAY * (1000 + mFuzz)
				* (1 << (mNumFailed - 1));
	}

	/**
	 * Returns whether this download (which the download manager hasn't seen
	 * yet) should be started.
	 */
	public boolean isReadyToStart(long now) {
		if (mControl == Downloads.Impl.CONTROL_PAUSED) {
			// the download is paused, so it's not going to start
			return false;
		}
		if (mStatus == 0) {
			// status hasn't been initialized yet, this is a new download
			return true;
		}
		if (mStatus == Downloads.Impl.STATUS_PENDING) {
			// download is explicit marked as ready to start
			return true;
		}
		if (mStatus == Downloads.Impl.STATUS_RUNNING) {
			// download was interrupted (process killed, loss of power) while it
			// was running,
			// without a chance to update the database
			return true;
		}
		if (mStatus == Downloads.Impl.STATUS_RUNNING_PAUSED) {
			if (mNumFailed == 0) {
				// download is waiting for network connectivity to return before
				// it can resume
				return true;
			}
			if (restartTime() < now) {
				// download was waiting for a delayed restart, and the delay has
				// expired
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns whether this download (which the download manager has already
	 * seen and therefore potentially started) should be restarted.
	 * 
	 * In a nutshell, this returns true if the download isn't already running
	 * but should be, and it can know whether the download is already running by
	 * checking the status.
	 */
	public boolean isReadyToRestart(long now) {
		if (mControl == Downloads.Impl.CONTROL_PAUSED) {
			// the download is paused, so it's not going to restart
			return false;
		}
		if (mStatus == 0) {
			// download hadn't been initialized yet
			return true;
		}
		if (mStatus == Downloads.Impl.STATUS_PENDING) {
			// download is explicit marked as ready to start
			return true;
		}
		if (mStatus == Downloads.Impl.STATUS_RUNNING_PAUSED) {
			if (mNumFailed == 0) {
				// download is waiting for network connectivity to return before
				// it can resume
				return true;
			}
//			Log.d(TAG, "restartTime:" + restartTime() + ",now" + now + ",mId:" + mId + ",name:" + mTitle);
			if (restartTime() < now) {
				// download was waiting for a delayed restart, and the delay has
				// expired
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns whether this download has a visible notification after
	 * completion.
	 */
	/*
	 * public boolean hasCompletionNotification() { if
	 * (!Downloads.Impl.isStatusCompleted(mStatus)) { return false; } if
	 * (mVisibility == Downloads.Impl.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) {
	 * return true; } return false; }
	 */

	/**
	 * Returns whether this download is allowed to use the network.
	 */
	public boolean canUseNetwork(boolean available) {
		if (!available) {
			return false;
		}
		return true;
	}

	// zdx modify
	void startDownloadThread(Context context) {
		if(mHasActiveThread)
			return;
		DownloadThread downloader = new DownloadThread(context, this);
		mHasActiveThread = true;
		downloader.start();
	}

	void getDownloadUrl(Context context) {
		DownloadAppAction.getInstance(mId, mTitle, mPackageName,
				mVersionCode, mIconAddr, mCategory,
				DownloadHandler.getInstance(context)).doAction(context);
	}
}
