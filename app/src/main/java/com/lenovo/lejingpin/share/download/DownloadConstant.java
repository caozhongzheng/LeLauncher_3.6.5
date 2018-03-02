package com.lenovo.lejingpin.share.download;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public final class DownloadConstant {

	private static final String TAG = "xujing3";
	// ------Get App List------
	public static final String ACTION_REQUEST_APPLIST = "com.lenovo.action.ACTION_REQUEST_APPLIST";
	// ------Get App Info------
	public static final String ACTION_REQUEST_APP_INFO = "com.lenovo.action.ACTION_REQUEST_APP_INFO";

	// yangmao add
	public static final String ACTION_HAWAII_SEARCH_APP_INFO = "com.lenovo.action.ACTION_HAWAII_SEARCH_APP_INFO";
	// yangmao end

	// ------App Download------
	public static final String ACTION_REQUEST_APPDOWNLOAD = "com.lenovo.share.action.ACTION_REQUEST_APPDOWNLOAD";
	// ------Common Download------
	public static final String ACTION_REQUEST_COMMON_DOWNLOAD = "com.lenovo.action.ACTION_REQUEST_COMMON_DOWNLOAD";

	// ------Get App Comment------
	public static final String ACTION_REQUEST_APP_COMMENT_LIST = "com.lenovo.action.ACTION_REQUEST_APP_COMMENT_LIST";

	// yangmao add start
	public static final String ACTION_REQUEST_HAWAII_SEARCH_APP_COMMENT_LIST = "com.lenovo.action.ACTION_REQUEST_HAWAII_SEARCH_APP_COMMENT_LIST";
	// yangmao add end

	
	//yangmao add start 0227 for install-error to notify us the apk failed installed
	public static final String ACTION_APK_PARSE_OR_INSTALL_FAILED = "com.lenovo.action.ACTION_APK_PARSE_OR_INSTALL_FAILED";
	//yangmao add end 0227
	
	
	
	public static final String ACTION_TYPE_TEST_BG = "com.lenovo.action.ACTION_TYPE_TEST_BG";

	public static final String AUTHORITY = "com.lenovo.lejingpin.share.provider";
	public static final int MSG_DOWN_LOAD_URL = 103;
	public static final int MSG_DOWN_LOAD_START = 104;

	public static final int TIME_INTERVAL_HOUR = 24 * 60 * 60 * 1000;
	public static final int TIME_INTERVAL_HOUR_MOBILE = 5 * 24 * 60 * 60 * 1000;
	public static final int COUNT_DISPLAY_APP_LIST = 30;
	public static final int COUNT_DB_APP_LIST = 60;
	public static final int START_DB_APP_LIST = 0;
	public static final int COUNT_SPRERE_LIST_DB_TOTAL = 120;

	public static final int RESOLUTION_WIDTH = 1280;
	public static final int RESOLUTION_HEIGHT = 800;

	public static final String TYPE_APPLIST_ACTION = "applist";
	public static final String TYPE_APPINFO_ACTION = "appinfo";
	// yangmao add start
	public static final String TYPE_HAWAII_SEARCH_APPINFO_ACTION = "hawaii_search_appinfo";
	// yangmao add end
	public static final String TYPE_DOWNLOAD_ACTION = "download";
	public static final String TYPE_COMMENTLIST_ACTION = "commentlist";

	// yangmao add
	public static final String TYPE_HAWAII_SEARCH_COMMENTLIST_ACTION = "hawaii_search_commentlist";
	// yangmao end

	public static final String TYPE_WALLPAPER = "wallpaper";
	public static final String ACTION_DOWNLOAD_COUNT_CHANGED = "com.lenovo.action.ACTION_DOWNLOAD_COUNT";
	public static final String ACTION_DOWNLOAD_RESUME = "com.lenovo.action.ACTION_DOWNLOAD_RESUME";
	public static final String ACTION_DOWNLOAD_STATE = "com.lenovo.action.ACTION_DOWNLOAD_STATE";
	public static final String ACTION_APK_FAILD_DOWNLOAD = "com.lenovo.action.ACTION_APK_FAILD_DOWNLOAD";
	public static final String ACTION_DOWNLOAD_STATE_CHANGED = "com.lenovo.action.ACTION_DOWNLOAD_STATE_CHANGED";
	public static final String ACTION_APP_UPGRADE = "com.lenovo.action.ACTION_APP_UPGRADE";
	public static final String ACTION_DOWNLOAD_DELETE = "com.lenovo.action.ACTION_DOWNLOAD_DELETE";
	public static final String ACTION_DOWNLOAD_INSTALL_UNINSTALL = "com.lenovo.action.ACTION_DOWNLOAD_INSTALL_UNINSTALL";

	public static final String ACTION_DOWNLOAD_APP_FROM_LESTORE = "com.lenovo.action.ACTION_DOWNLOAD_APP_FROM_LESTORE";
	public static final String ACTION_DOWNLOAD_FROM_COMMON = "com.lenovo.action.ACTION_DOWNLOAD_FROM_COMMON";

	public static final String EXTRA_INFO = "com.lenovo.intent.extra.info";
	public static final String EXTRA_ICON = "com.lenovo.intent.extra.icon";
	public static final String EXTRA_PACKAGENAME = "package_name";
	public static final String EXTRA_VERSION = "version_code";
	public static final String EXTRA_STATUS = "status";
	public static final String EXTRA_PROGRESS = "progress";
	public static final String EXTRA_INSTALLPATH = "install_path";
	public static final String EXTRA_APPNAME = "app_name";
	public static final String EXTRA_CATEGORY = "category";
	public static final String EXTRA_RESULT = "result";
	public static final String EXTRA_POPTOAST = "poptoast";
	public static final String EXTRA_RELOAD = "reload";
	public static final String EXTRA_COUNT = "count";
	
	
	//xujing added
	public static final int FAILD_DOWNLOAD_NO_ENOUGHT_SPACE = 1;
	public static final int FAILD_DOWNLOAD_NETWORK_ERROR = 2;
	public static final int FAILD_DOWNLOAD_OTHER_ERROR = 99;

	//下载类型占用1-8位
	public static final int CATEGORY_ERROR = 0;
	public static final int CATEGORY_THEME = 0x0001;
	public static final int CATEGORY_LOCKSCREEN = 0x0002;
	public static final int CATEGORY_WALLPAPER = 0x0003;
	public static final int CATEGORY_SCENE = 0x0004;
	public static final int CATEGORY_APP_UPGRADE = 0x0006;
	public static final int CATEGORY_APPMANAGER_UPGRADE = 0x0007;
	public static final int CATEGORY_RECOMMEND_APP = 0x0008;
	public static final int CATEGORY_SEARCH_APP = 0x0009;
	public static final int CATEGORY_COMMON_APP = 0x000a;
	public static final int CATEGORY_LIVE_WALLPAPER = 0x000b;
	public static final int CATEGORY_LBK = 0x000c;
	//安装包类型占用9-12位
	public static final int CATEGORY_LENOVO_APK = 0x0100;
	public static final int CATEGORY_LENOVO_LCA = 0x0200;
//	public static final int CATEGORY_SEARCH_APP_INT = 20;
	//public static final int CATEGORY_APP = 12;

	public static final int CATEGORY_LESTORE_SERVER = 100;
	public static final int CATEGORY_LESTORE_APK = 101;

//	public static final int CATEGORY_LELAUNCHER_SERVER = 200;
//	public static final int CATEGORY_LELAUNCHER_SERVER_APK = 201;

//	public static final String CATEGORY_THEME_STRING = "0";
//	public static final String CATEGORY_LOCKSCREEN_STRING = "1";
//	public static final String CATEGORY_WALLPAPER_STRING = "2";
//	public static final String CATEGORY_SCENE_STRING = "3";
//	public static final String CATEGORY_APP_UPGRADE_STRING = "10";
//	public static final String CATEGORY_APPMANAGER_UPGRADE_STRING = "11";
	
//	public static final String CATEGORY_APP = "12";
//	public static final String CATEGORY_SEARCH_APP = "13";
//	
//	public static final String CATEGORY_HAWAII_SEARCH_APP = "20";

	// private static int mCategory;
	public static final String HOST_WALLPAPER = "http://launcher.lenovo.com/launcher/lezhuomian.php";
	public static final String HOST_EXTRA_WALLPAPER = "http://launcher.lenovo.com/launcher/";

	public static final String MIMETYPE_DEFAULT = "application/vnd.android.package-archive";
	public static final String MIMETYPE_APK = "application/vnd.android.package-archive";
	public static final String MIMETYPE_WALLPAPER = "image/jpeg";

	public static final int INSTALL_TYPE_NOT_AKEY = 0;
	public static final int INSTALL_TYPE_AKEY_NORMAL = 1;
	// public static final int INSTALL_TYPE_AKEY_SHELL = 2;

	public static final String WALLPAPER_EXTRA_START_INDEX = "start_index";
	
	public static final int CONNECT_TYPE_WIFI = 1;
	public static final int CONNECT_TYPE_MOBILE = 2;
	public static final int CONNECT_TYPE_OTHER = 0;
	
	public static final String DEFAULT_VERSION_CODE = "99999";
	
	private DownloadConstant(){
		
	}

	public static int getConnectType(Context context) {
		ConnectivityManager mConnMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = mConnMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo infoM = mConnMgr
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (info != null && info.isConnected()) {
			return CONNECT_TYPE_WIFI;
		} else if (infoM != null && infoM.isConnected()) {
			return CONNECT_TYPE_MOBILE;
		}
		return CONNECT_TYPE_OTHER;
	}

	// public static void setAppCategory(int category){
	// mCategory = category;
	// }
	// public static int getAppCategory(){
	// return mCategory;
	// }
	
	//xujing3 added
	public static int getInstallCategory(int ca){
		return (ca & (0x0f00));
	}
	
	public static int getDownloadCategory(int ca){
		return (ca & (0x00ff));
	}

	public static int canAKeyIntall(Context context) {
		if (checkSystemPermission(context) && checkInstallPermission(context)) {
			return INSTALL_TYPE_AKEY_NORMAL;
		}/*
		 * else if( checkRootSystem(context)){ return INSTALL_TYPE_AKEY_SHELL; }
		 */else {
			return INSTALL_TYPE_NOT_AKEY;
		}
	}

	private static boolean checkSystemPermission(Context context) {
		PackageInfo packageinfo = null;
		PackageManager packagemanager = context.getPackageManager();
		String packName = context.getPackageName();
		try {
			packageinfo = packagemanager.getPackageInfo(packName, 0);
		} catch (NameNotFoundException e) {
			return false;
		}
		ApplicationInfo appInfo = packageinfo.applicationInfo;
		if (appInfo != null) {
			if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
				Log.i(TAG, "checkSystemPermission,  system app!!!");
				return true;
			}
		}
		Log.i(TAG, "checkSystemPermission,not system app!!!");
		return false;
	}

	private static boolean checkInstallPermission(Context context) {
		if (context
				.checkCallingOrSelfPermission("android.permission.INSTALL_PACKAGES") == 0) {
			Log.i(TAG, "checkInstallPermission, have install permission!!!");
			return true;
		}
		Log.i(TAG, "checkInstallPermission, not have install permission!!!");
		return false;
	}

//	private static boolean checkRootSystem(Context context) {
//		Process process = null;
//		DataOutputStream os = null;
//		try {
//			process = Runtime.getRuntime().exec("su");
//			os = new DataOutputStream(process.getOutputStream());
//			os.writeBytes("fs\n");
//			os.writeBytes("exit\n");
//			os.flush();
//			process.waitFor();
//
//		} catch (IOException e) {
//			Log.i(TAG, "checkRootSystem, not root system !!!");
////			return false;
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				if (os != null) {
//					os.close();
//				}
//				process.destroy();
//			} catch (IOException e) {
//				Log.i(TAG, "checkRootSystem,Exception !!!");
//			}
//		}
//		Log.i(TAG, "checkRootSystem,root system !!!");
//		return true;
//	}
}
