package com.lenovo.lejingpin.appsmgr.content;

import java.util.ArrayList;
import java.util.List;

import com.lenovo.lejingpin.share.download.DownloadConstant;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

public class UpgradeUtil {

	private static final String TAG = "xujing3";
	public static final String KEY_UPGRADE_APPLIST_TIME = "upgrade_applist_time";
	public static final String PREF_MAGICDOWNLOAD = "com.lenovo.launcher.magicdownload_preferences";
	//test by dining 2013-06-24  lejingpin->xlejingpin
	public static final String AUTHORITY = "com.lenovo.lejingpin.appsmgr.content.upgradeprovider";

	// ------Get Upgrade App List------
	public static final String ACTION_SWITCH_APPUPGRADE = "com.lenovo.appsmgr.action.ACTION_SWITCH_APPUPGRADE";
	public static final String ACTION_REQUEST_APPUPGRADE = "com.lenovo.appsmgr.action.ACTION_REQUEST_APPUPGRADE";
	public static final String ACTION_REQUEST_APPUPGRADE_COMPLETE = "com.lenovo.appsmgr.action.ACTION_REQUEST_APPUPGRADE_COMPLETE";
	public static final String ACTION_REQUEST_APPUPGRADE_APPINFO = "com.lenovo.appsmgr.action.ACTION_REQUEST_APPUPGRADE_APPINFO";

	public static final String TYPE_APPUPGRADE_ACTION = "appupgrade";
	public static final String TYPE_SWITCH_APPUPGRADE_ACTION = "switch_appupgrade";
	public static final String TYPE_APPUPGRADE_APPINFO_ACTION = "appupgrade_appinfo";

	private static final int TIME_INTERVAL_UPGRADE = 12 * 60 * 60 * 1000;
	private static final int TIME_INTERVAL_MOBILE_UPGRADE = 2 * 24 * 60 * 60
			* 1000;

	private UpgradeUtil(){
		
	}

	public static void insertUpgradeAppList(Context context,
			List<UpgradeApp> list) {
		Log.d(TAG, "insertUpgradeAppList-----------");
		if (!list.isEmpty()) {
			list = getUpgradeAddedAppList(context, list);
			Log.d(TAG, "insertUpgradeAppList-------2222----");
			/*
			 * LauncherApplication lapp = (LauncherApplication)
			 * context.getApplicationContext(); LauncherProvider lp =
			 * lapp.getLauncherProvider(); if (lp == null) return; long id =
			 * lp.generateNewUpgradesId();
			 */
			if (list == null)
				return;
			int N = list.size();
			Log.d(TAG, "UpgradeUtil.insertLocalApp >> count:" + N);
			ContentValues[] values_array = new ContentValues[N];
			for (int i = 0; i < N; i++) {
				ContentValues values = new ContentValues();
				UpgradeApp app = list.get(i);
				Log.d(TAG,
						"UpgradeUtil.insertLocalApp >> packageName :"
								+ app.getAppName() + ",version : "
								+ app.getVersionName());
				values.put(UpgradeLocalApp.PACKAGE_NAME, app.getPackageName());
				values.put(UpgradeLocalApp.VERSION_CODE, app.getVersionCode());
				values.put(UpgradeLocalApp.APP_NAME, app.getAppName());
				values.put(UpgradeLocalApp.ICON_ADDRESS, app.getIconAddr());
				values.put(UpgradeLocalApp.APP_CATEGORY, app.getCategory());
				values.put(UpgradeLocalApp.APP_SIZE, app.getAppSize());
				values.put(UpgradeLocalApp.APP_STAR, app.getAppStar());
				values.put(UpgradeLocalApp.APP_PAY, app.getAppPay());
				values.put(UpgradeLocalApp.VERSION_NAME, app.getVersionName());
				values_array[i] = values;
			}

			context.getContentResolver().bulkInsert(
					UpgradeLocalApp.CONTENT_UPGRADE_APP_LIST_URI, values_array);
		}
	}

	private static List<UpgradeApp> getUpgradeAddedAppList(Context context,
			List<UpgradeApp> list) {
		List<UpgradeApp> newList = null;
		List<UpgradeApp> dbList = getAllUpgradeAppList(context);
		if (dbList != null && !dbList.isEmpty()) {
			newList = new ArrayList<UpgradeApp>();
			for (UpgradeApp app : list) {
				boolean isContain = dbList.contains(app);
				if (!isContain) {
					newList.add(app);
				}
			}
			if (newList.isEmpty())
				return null;
			else
				return newList;
		} else {
			return list;
		}
	}
	
	public static int getUpgradeAppCount(Context context){
		List<UpgradeApp> dbList = getAllUpgradeAppList(context);
		if(dbList != null){
			return dbList.size();
		}
		return 0;
	}

	public static List<UpgradeApp> getAllUpgradeAppList(Context context) {
		Cursor cursor = null;
		List<UpgradeApp> list = null;
		try {
			Log.d(TAG,"getAllUpgradeAppList.uri:"+UpgradeLocalApp.CONENT_UPGRADE_APP_URI.toString());
			cursor = context.getContentResolver().query(
					UpgradeLocalApp.CONENT_UPGRADE_APP_URI, null, null, null,
					null);
			if (cursor != null && cursor.moveToFirst()) {
				list = new ArrayList<UpgradeApp>();
				while (!cursor.isAfterLast()) {
					UpgradeApp app = new UpgradeApp();
					app.setPackageName(cursor.getString(cursor
							.getColumnIndex(UpgradeLocalApp.PACKAGE_NAME)));
					app.setVersionCode(cursor.getString(cursor
							.getColumnIndex(UpgradeLocalApp.VERSION_CODE)));
					app.setAppName(cursor.getString(cursor
							.getColumnIndex(UpgradeLocalApp.APP_NAME)));
					app.setIconAddr(cursor.getString(cursor
							.getColumnIndex(UpgradeLocalApp.ICON_ADDRESS)));
					app.setCategory(cursor.getInt(cursor
							.getColumnIndex(UpgradeLocalApp.APP_CATEGORY)));
					app.setAppSize(cursor.getString(cursor
							.getColumnIndex(UpgradeLocalApp.APP_SIZE)));
					app.setAppStar(cursor.getString(cursor
							.getColumnIndex(UpgradeLocalApp.APP_STAR)));
					app.setAppPay(cursor.getString(cursor
							.getColumnIndex(UpgradeLocalApp.APP_PAY)));
					app.setVersionName(cursor.getString(cursor
							.getColumnIndex(UpgradeLocalApp.VERSION_NAME)));
					int ignore = cursor.getInt(cursor.getColumnIndex(UpgradeLocalApp.APP_UPDATE_IGNORE));
					if(0 == ignore){
						app.setUpdateIgnore(false);
					}else{
						app.setUpdateIgnore(true);
					}
					
					list.add(app);
					cursor.moveToNext();
				}
			}
		} catch (Exception e) {
			Log.d(TAG,
					"UpgradeUtil.getAllUpgradeAppList, Can not query upgrade app db.");
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}

		return list;
	}

	public static UpgradeApp queryUpgradeApp(Context context,
			String packageName, String versionCode) {
		Cursor cursor = null;
		UpgradeApp app = null;
		try {
			cursor = context.getContentResolver().query(
					UpgradeLocalApp.CONENT_UPGRADE_APP_URI,
					null,
					UpgradeLocalApp.PACKAGE_NAME + " = ? and "
							+ UpgradeLocalApp.VERSION_CODE + " = ? ",
					new String[] { packageName, versionCode }, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					app = new UpgradeApp();
					app.setPackageName(cursor.getString(cursor
							.getColumnIndex(UpgradeLocalApp.PACKAGE_NAME)));
					app.setVersionCode(cursor.getString(cursor
							.getColumnIndex(UpgradeLocalApp.VERSION_CODE)));
					app.setAppName(cursor.getString(cursor
							.getColumnIndex(UpgradeLocalApp.APP_NAME)));
					app.setIconAddr(cursor.getString(cursor
							.getColumnIndex(UpgradeLocalApp.ICON_ADDRESS)));
					app.setCategory(cursor.getInt(cursor
							.getColumnIndex(UpgradeLocalApp.APP_CATEGORY)));
					app.setAppSize(cursor.getString(cursor
							.getColumnIndex(UpgradeLocalApp.APP_SIZE)));
					app.setAppStar(cursor.getString(cursor
							.getColumnIndex(UpgradeLocalApp.APP_STAR)));
					app.setAppPay(cursor.getString(cursor
							.getColumnIndex(UpgradeLocalApp.APP_PAY)));
					app.setVersionName(cursor.getString(cursor
							.getColumnIndex(UpgradeLocalApp.VERSION_NAME)));

					app.setAppDesc(cursor.getString(cursor
							.getColumnIndex(UpgradeLocalApp.APP_DESC)));
					app.setAppSnapShot(cursor.getString(cursor
							.getColumnIndex(UpgradeLocalApp.APP_SNAPSHOT)));
				}
			}
		} catch (Exception e) {
			Log.i(TAG, "UpgradeUtil.queryUpgradeApp, can not query app !");
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return app;
	}

	public static void updateUpgradeApp(Context context, UpgradeApp app) {
		if (null == app)
			return;
		ContentValues values = new ContentValues();
		values.put(UpgradeLocalApp.APP_UPDATE_IGNORE, app.getUpdateIgnore());
		values.put(UpgradeLocalApp.APP_DESC, app.getAppDesc());
		values.put(UpgradeLocalApp.APP_SNAPSHOT, app.getAppSnapShot());
		context.getContentResolver().update(
				UpgradeLocalApp.CONENT_UPGRADE_APP_URI,
				values,
				UpgradeLocalApp.PACKAGE_NAME + " = ? and "
						+ UpgradeLocalApp.VERSION_CODE + " = ? ",
				new String[] { app.getPackageName(), app.getVersionCode() });
	}

	public static boolean isNeedLoadFromServer(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(
				PREF_MAGICDOWNLOAD, Context.MODE_PRIVATE);
		long currentTime = System.currentTimeMillis();
		long savedTime = prefs.getLong(UpgradeUtil.KEY_UPGRADE_APPLIST_TIME,
				currentTime);
		int connectType = DownloadConstant.getConnectType(context);
		if (DownloadConstant.CONNECT_TYPE_MOBILE == connectType) {
			return (currentTime - savedTime) >= TIME_INTERVAL_MOBILE_UPGRADE;
		} else {
			return (currentTime - savedTime) >= TIME_INTERVAL_UPGRADE;
		}
	}

	public static void deleteLocalAppList(Context context, String where) {
		context.getContentResolver().delete(
				UpgradeLocalApp.CONENT_UPGRADE_APP_URI, where, null);
	}

}
