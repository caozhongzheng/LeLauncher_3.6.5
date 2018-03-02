package com.lenovo.lejingpin.appsmgr.content.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;

import com.lenovo.lejingpin.appsmgr.content.UpgradeApp;
import com.lenovo.lejingpin.appsmgr.content.UpgradeLocalApp;
import com.lenovo.lejingpin.appsmgr.content.UpgradeUtil;
import com.lenovo.lejingpin.ams.AmsSession;
import com.lenovo.lejingpin.ams.AmsRequest;
import com.lenovo.lejingpin.ams.AmsSession.AmsCallback;
import com.lenovo.lejingpin.ams.CategoryRequest.Application;
import com.lenovo.lejingpin.ams.QueryUpgradeRequest;
import com.lenovo.lejingpin.ams.QueryUpgradeRequest.QueryUpgradeResponse;
import com.lenovo.lejingpin.share.download.DownloadConstant;
import com.lenovo.lejingpin.share.service.TaskService.Action;
import com.lenovo.lejingpin.share.util.DeviceInfo;

//------Get Upgrade App List ------
public class UpgradeAppListAction implements Action, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -697551610833770825L;

	private String TAG = "xujing3";

//	private Context mContext;
	private static UpgradeAppListAction mUpageAppListAction = null;
	
	private UpgradeAppListAction(){}

	private UpgradeAppListAction(Context context) {
//		mContext = context;
	}

	public synchronized static UpgradeAppListAction getInstance() {
		if (mUpageAppListAction == null) {
			mUpageAppListAction = new UpgradeAppListAction();
		}
		return mUpageAppListAction;
	}

	public void doAction(Context context) {
		requestAppList(context);
	}

	private void requestAppList(Context context) {
		HashMap<String, String[]> map = getAllApps(context);
		final String[] packageNameArray = map.get(UpgradeLocalApp.PACKAGE_NAME);
		String[] versionCodeArray = map.get(UpgradeLocalApp.VERSION_CODE);
		 Log.i(TAG,
		 "UpageAppListAction >>> packageNameArray "+Arrays.toString(packageNameArray));
		if (packageNameArray != null && packageNameArray.length != 0) {
			requestLocalAppList(context,packageNameArray, versionCodeArray);
		}
	}

	private HashMap<String, String[]> getAllApps(Context context) {
		 Log.d(TAG, "UpageAppListAction.getAllApps() ");
		List<String> packageNameList = new ArrayList<String>();
		List<String> versionCodeList = new ArrayList<String>();
		HashMap<String, String[]> map = new HashMap<String, String[]>();
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> packages = pm.getInstalledPackages(0);
		for (int i = 0; i < packages.size(); i++) {
			PackageInfo packageInfo = packages.get(i);
			if (packageInfo == null)
				continue;
			Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
			resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			resolveIntent.setPackage(packageInfo.packageName);
			String className = null;
			
			List<ResolveInfo> apps = context.getPackageManager().queryIntentActivities(resolveIntent, 0);
			if(apps.iterator().hasNext()){
				ResolveInfo ri = apps.iterator().next();
				className = ri.activityInfo.name;
			}
			
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0
					|| (className != null && !className.isEmpty())) {
				Log.d(TAG,"-----------------------------------------------------packageName:"+packageInfo.packageName);
				String packageName = packageInfo.packageName;
				String versionCode = String.valueOf(packageInfo.versionCode);
				packageNameList.add(packageName);
				versionCodeList.add(versionCode);
			}
		}
		map.put(UpgradeLocalApp.PACKAGE_NAME,
				packageNameList.toArray(new String[] {}));
		map.put(UpgradeLocalApp.VERSION_CODE,
				versionCodeList.toArray(new String[] {}));
		Log.i(TAG, "UpgradeLocalAppUtil.getAllLauncherMap(), size:"
				+ packageNameList.size());
		return map;
	}

	private void requestLocalAppList(final Context context,final String[] packageNameArray,
			final String[] versionCodeArray) {
		DeviceInfo deviceInfo = DeviceInfo.getInstance(context);
		AmsSession.init(context, new AmsCallback() {
			public void onResult(AmsRequest request, int code, byte[] bytes) {
				Log.i(TAG,
						"UpgradeAppListAction.requestLocalAppList, AmsSession.init >> result code:"
								+ code);
				if (code != 200) {
					sendIntentToLauncher(context,false, null, null);
				} else {
					getLocalAppList(context,packageNameArray, versionCodeArray);
				}
			}
		}, deviceInfo.getWidthPixels(), deviceInfo.getHeightPixels());
	}

	private void getLocalAppList(final Context context,final String[] package_name_array,
			final String[] version_code_array) {
		// Log.i("zdx","UpgradeAppListAction.getLocalAppList()");
		boolean isNoEmpty = (package_name_array != null)
				&& (package_name_array.length != 0)
				&& (version_code_array != null)
				&& (version_code_array.length != 0);
		if (isNoEmpty) {
			final ArrayList<Application> requestAppList = new ArrayList<Application>();
			int size = package_name_array.length;
			for (int i = 0; i < size; i++) {
				Application app = new Application();
				app.setPackage_name(package_name_array[i]);
				app.setApp_versioncode(version_code_array[i]);
				requestAppList.add(app);
			}
			QueryUpgradeRequest queryQequest = new QueryUpgradeRequest(context);
			queryQequest.setData(requestAppList);
			AmsSession.execute(context, queryQequest, new AmsCallback() {
				public void onResult(AmsRequest request, int code, byte[] bytes) {

					boolean success = false;
					if (code == 200) {
						success = true;
						if (bytes != null) {
							QueryUpgradeResponse queryResponse = new QueryUpgradeResponse();
							queryResponse.parseFrom(bytes);
							boolean successResponse = queryResponse
									.getIsSuccess();
							Log.i(TAG,
									"pgradeAppListAction.getLocalAppList >> response success :"
											+ successResponse);
							if (successResponse) {
								ArrayList<Application> responseAppList = queryResponse
										.getApplicationItemList();
								final List<UpgradeApp> uApps = new ArrayList<UpgradeApp>();
								for (Application responseApp : responseAppList) {
									
									UpgradeApp uApp = new UpgradeApp();
									uApp.setPackageName(responseApp
											.getPackageName());
									uApp.setVersionCode(responseApp
											.getAppVersionCode());
									uApp.setAppName(responseApp.getAppName());
									uApp.setIconAddr(responseApp.getIcon_addr());
									uApp.setCategory(DownloadConstant.CATEGORY_APP_UPGRADE | DownloadConstant.CATEGORY_LENOVO_LCA);
									uApp.setAppSize(responseApp.getApp_size());
									uApp.setAppStar(responseApp.getStar_level());
									uApp.setAppPay(responseApp.getApp_price());
									uApp.setVersionName(responseApp
											.getApp_version());
									// Log.i("zdx","============== "+
									// responseApp.getAppName());
									uApps.add(uApp);
								}
								UpgradeUtil.insertUpgradeAppList(context,
										uApps);
								saveUpgradeTime(context);
							}
						}
					}
					sendIntentToLauncher(context,success, null, null);
				}
			});
		} else
			sendIntentToLauncher(context,true, null, null);
	}

	private void sendIntentToLauncher(Context context,boolean result,
			ArrayList<String> packageNames, ArrayList<String> versionCode) {
		Log.i(TAG, "UpgradeAppListAction.sendIntentToLauncher, result:"
				+ result);
		Intent intent = new Intent();
		intent.setAction(UpgradeUtil.ACTION_REQUEST_APPUPGRADE_COMPLETE);
		// intent.putStringArrayListExtra("package_names", packageNames);
		// intent.putStringArrayListExtra("version_codes", versionCode);
		intent.putExtra("result", result);
		context.sendBroadcast(intent);
	}

	private void saveUpgradeTime(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(
				UpgradeUtil.PREF_MAGICDOWNLOAD, Context.MODE_PRIVATE);
		long currentTime = System.currentTimeMillis();
		SharedPreferences.Editor editor = prefs.edit();
		editor.putLong(UpgradeUtil.KEY_UPGRADE_APPLIST_TIME, currentTime);
		editor.commit();
	}

}
