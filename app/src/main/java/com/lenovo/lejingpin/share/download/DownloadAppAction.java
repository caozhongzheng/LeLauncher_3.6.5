package com.lenovo.lejingpin.share.download;

import com.lenovo.lejingpin.share.util.DeviceInfo;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.lenovo.lejingpin.share.ams.AmsRequest;
import com.lenovo.lejingpin.share.ams.AmsSession;
import com.lenovo.lejingpin.share.ams.AmsSession.AmsCallback;
import com.lenovo.lejingpin.share.ams.GetAppDownLoadUrlRequest;
import com.lenovo.lejingpin.share.ams.GetAppDownLoadUrlRequest.GetAppDownLoadUrlResponse;
import com.lenovo.lejingpin.share.service.TaskService.Action;

public class DownloadAppAction implements Action {

	private String TAG = "DownloadAppAction";
	private static final String CATEGORY_WALLPAPER = "2";

//	private Context mContext;
	private String mPackageName;
	private String mVersionCode;
	private String appName;
	// zdx modify
	private String mAppIconUrl;
	private int mCategory;
	private int mId;

	private Handler mHandler;

	private static int mRetrycount = 0;
	private static final int MAX_RETRY_COUNT = 3;
	private static DownloadAppAction mDownloadAppAction = null;
	
	private DownloadAppAction(){}

	private DownloadAppAction(Context context) {
//		mContext = context;
	}

	public synchronized static DownloadAppAction getInstance(int id,
			String app_name, String package_name, String version_code,
			// zdx modify
			String iconurl, int category, Handler handler) {
		if (mDownloadAppAction == null) {
			mDownloadAppAction = new DownloadAppAction();
		}
		mDownloadAppAction.mId = id;
		mDownloadAppAction.mPackageName = package_name;
		mDownloadAppAction.mVersionCode = version_code;
		mDownloadAppAction.mHandler = handler;
		mDownloadAppAction.appName = app_name;
		// zdx modify
		mDownloadAppAction.mAppIconUrl = iconurl;
		mDownloadAppAction.mCategory = category;
		return mDownloadAppAction;
	}

	public void doAction(Context context) {
		requestDownloadApp(context,mId, appName, mPackageName, mVersionCode,
		// zdx modify
				mAppIconUrl, mCategory);
	}

	public void requestDownloadApp(final Context context,final int id,
			final String app_name, final String package_name,
			final String version_code,
			// zdx modify
			final String iconurl, final int category) {
		DeviceInfo deviceInfo = DeviceInfo.getInstance(context);
		AmsSession.init(context, new AmsCallback() {
			public void onResult(AmsRequest request, int code, byte[] bytes) {
				Log.i(TAG,
						"DownloadAppAction.requestDownloadApp, AmsSession.init >> result code:"
								+ code);
				if (code != 200) {
					if(mRetrycount < MAX_RETRY_COUNT){
						mRetrycount ++;
						requestDownloadApp(context,id,app_name,package_name,version_code
								,iconurl,category);
					}else{
						DownloadQueueHandler.getInstance().dequeueDownload(context,
								mId, true);
					}
//					sendFailedMessage(handler, package_name, version_code,
//							app_name/* ,from */);
				} else {
					mRetrycount = 0;
					getDownloadApp(context,id, app_name, package_name,
							version_code,
							// zdx modify
							iconurl, category);
				}
			}
		}, deviceInfo.getWidthPixels(), deviceInfo.getHeightPixels());
	}

	private void getDownloadApp(final Context context,final int id, final String app_name,
			final String package_name, final String version_code,
			// zdx modify
			final String iconurl, final int category) {
		// zhanglz1
//		if (DownloadConstant.CATEGORY_WALLPAPER == category) {
////		if (category.equals(CATEGORY_WALLPAPER)) {
//			String whereStr = SpereApp.PACKAGE_NAME + "= '" + package_name
//					+ "' and " + SpereApp.VERSION_CODE + "= '" + version_code
//					+ "'";
//			AppDownloadUrl downurl = geAppDownloadUrlByCursor(context, whereStr);
//			// 从数据库读url 为空就失败 package_name version_code 都是id
//			Log.d(TAG,
//					"wallpaper >>> downurl.getDownurl(): "
//							+ downurl.getDownurl());
//
////			sendMessage(handler, DownloadConstant.MSG_DOWN_LOAD_START, downurl);
//			if (downurl.getDownurl() == null
//					|| downurl.getDownurl().length() == 0) {
////				sendFailedMessage(handler, package_name, version_code, app_name);
//			}
//		} else {
			GetAppDownLoadUrlRequest downloadRequest = new GetAppDownLoadUrlRequest(
					context);
			downloadRequest.setData(package_name, version_code);
			AmsSession.execute(context, downloadRequest, new AmsCallback() {

				public void onResult(AmsRequest request, int code, byte[] bytes) {
					Log.i(TAG,
							"DownloadAppAction.getDownloadApp, AmsSession.execute >> result code : "
									+ code);
					if (code == 200 && bytes != null) {
						mRetrycount = 0;
						GetAppDownLoadUrlResponse response = new GetAppDownLoadUrlResponse(
									context);
						response.parseFrom(bytes);
						boolean successResponse = response.getIsSuccess();
						String url = response.getDownLoadUrl();
						Log.i(TAG,
									"DownloadAppAction.getDownloadApp >> response success :"
											+ successResponse);
						if (successResponse && url != null) {
							ContentResolver cr = context.getContentResolver();
							ContentValues values = new ContentValues();
							values.put(Downloads.Impl.COLUMN_URI, url);
							cr.update(Downloads.Impl.CONTENT_URI, values, "_ID = ?", new String[] { String.valueOf(id) });
							
//							DownloadInfoContainer.updateDBAndBufferById(context, 
//										ContentUris.withAppendedId(Downloads.Impl.CONTENT_URI,id), values, null);
							DownloadQueueHandler.getInstance().updateDownloadInfoById(
										context, Integer.valueOf(id),url);
								
//								AppDownloadUrl downurl = new AppDownloadUrl();
//								downurl.setBody(response.getPayBody());
//								downurl.setDownurl(response.getDownLoadUrl());
//								downurl.setPackage_name(package_name);
//								downurl.setVersion_code(version_code);
//								downurl.setApp_name(app_name);
//								// zdx modify
//								downurl.setIconUrl(iconurl);
//								downurl.setCategory(category);
//								downurl.setMimeType(DownloadConstant.MIMETYPE_APK);
								
								
//								sendMessage(handler,
//										DownloadConstant.MSG_DOWN_LOAD_START,
//										downurl);
							return;
							
						}else{
							DownloadQueueHandler.getInstance().dequeueDownload(context,
									mId, true);
						}
					}else if(mRetrycount < MAX_RETRY_COUNT){
						mRetrycount ++;
						getDownloadApp(context,id,app_name,package_name,version_code
								,iconurl,category);
					}else{
						mRetrycount = 0;
						DownloadQueueHandler.getInstance().dequeueDownload(context,
								mId, true);
					}
					
//					sendFailedMessage(handler, package_name, version_code,
//							app_name);
				}
			});
//		}

	}

	private void sendFailedMessage(Handler handler, String packageName,
			String versionCode, String appName) {
		Log.i(TAG,
				"DownloadAppAction.sendFailedMessage(), get download info error!");
		AppDownloadUrl downurl = new AppDownloadUrl();
		downurl.setPackage_name(packageName);
		downurl.setVersion_code(versionCode);
		downurl.setApp_name(appName);
		downurl.setDownurl(null);
		sendMessage(handler, DownloadConstant.MSG_DOWN_LOAD_START, downurl);
	}

	private void sendMessage(Handler handler, int what, Object obj) {
		if (handler != null) {
			Message msg = new Message();
			msg.obj = obj;
			msg.what = what;
			handler.sendMessage(msg);
		} else {
			Log.i(TAG,
					"DownloadAppAction.sendMessage ,  handler is null, not send!");
		}
	}

	// zhanglz1-start
	private static AppDownloadUrl geAppDownloadUrlByCursor(Context context,
			String whereStr) {
		Cursor cursor = null;
		AppDownloadUrl downurl = new AppDownloadUrl();
		try {
			Log.d("zdx", "geAppDownlaodUrlByCursor>>whereStr:" + whereStr);

			cursor = context.getContentResolver().query(
					SpereApp.CONTENT_SPERE_URI, null, whereStr, null, null);

			if (cursor != null && cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					downurl.setBody(cursor.getString(cursor
							.getColumnIndex(SpereApp.PACKAGE_NAME)));// ????
					downurl.setDownurl(cursor.getString(cursor
							.getColumnIndex(SpereApp.URL)));
					downurl.setPackage_name(cursor.getString(cursor
							.getColumnIndex(SpereApp.PACKAGE_NAME)));
					downurl.setVersion_code(cursor.getString(cursor
							.getColumnIndex(SpereApp.VERSION_CODE)));
					downurl.setApp_name(cursor.getString(cursor
							.getColumnIndex(SpereApp.APP_NAME)));
					// downurl.setmFrom("hawaii_app");//???
					downurl.setIconUrl(cursor.getString(cursor
							.getColumnIndex(SpereApp.ICON_ADDRESS)));
					downurl.setCategory(cursor.getInt(cursor
							.getColumnIndex(SpereApp.APP_CATEGORY)));
					downurl.setMimeType(DownloadConstant.MIMETYPE_WALLPAPER);
					cursor.moveToNext();
				}
			}

		} catch (Exception e) {
			Log.d("zdx", "Can not query spere app list===wallpaper.");
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return downurl;
	}
	// zhanglz1-end

}
