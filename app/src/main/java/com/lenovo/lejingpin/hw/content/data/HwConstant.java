package com.lenovo.lejingpin.hw.content.data;

import java.io.DataOutputStream;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class HwConstant {
	
	private static final String TAG = "HwConstant";
	
	private HwConstant(){};
	
	public static final String ACTION_SPERE_APP_LIST_COMPLETE = "com.lenovo.lejingpin.hw.ACTION_SPERE_APP_LIST_COMPLETE";
	public static final String ACTION_APP_INFO_COMPLETE = "com.lenovo.lejingpin.hw.ACTION_APP_INFO_COMPLETE";
	public static final String ACTION_UPGRADE_APP_LIST_COMPLETE = "com.lenovo.leos.hw.ACTION_UPGRADE_APP_LIST_COMPLETE";
	
	
	public static final String ACTION_TIME_SPERE_APPLIST = "com.lenovo.lejingpin.hw.ACTION_TIME_SPERE__APPLIST";
	public static final String ACTION_TIME_UPGRADE_APPLIST = "com.lenovo.lejingpin.hw.ACTION_TIME__UPGRADE_APPLIST";
	
	public static final String ACTION_TIMESHEDULE_SPEREAPPLIST = "com.lenovo.lejingpin.hw.ACTION_TIMESHEDULE_SPEREAPPLIST";
	public static final String ACTION_TIMESHEDULE_UPGRADEAPPLIST = "com.lenovo.lejingpin.hw.ACTION_TIMESHEDULE_UPGRADEAPPLIST";
	
	public static final String ACTION_REQUEST_UPGRADEAPPLIST = "com.lenovo.lejingpin.hw.ACTION_REQUEST_UPGRADEAPPLIST";
	public static final String ACTION_REQUEST_SPEREAPPLIST = "com.lenovo.lejingpin.hw.ACTION_REQUEST_SPEREAPPLIST";
	public static final String ACTION_REQUEST_APP_INFO = "com.lenovo.lejingpin.hw.ACTION_REQUEST_APP_INFO";
	public static final String ACTION_REQUEST_APP_DOWNLOAD = "com.lenovo.lejingpin.hw.ACTION_REQUEST_APP_DOWNLOAD";
	public static final String ACTION_REQUEST_APP_COMMON_LIST = "com.lenovo.lejingpin.hw.ACTION_REQUEST_APP_COMMON_LIST";
	public static final String ACTION_REQUEST_CHECK_HAEAII = "com.lenovo.lejingpin.hw.ACTION_REQUEST_CHECK_HAEAII";
	public static final String ACTION_SETTING_SPERE_SWITCH = "com.lenovo.lejingpin.hw.ACTION_SETTING_SPERE_SWITCH";
	public static final String ACTION_SETTING_UPGRADE = "com.lenovo.lejingpin.hw.ACTION_SETTING_UPGRADE";
	
	// TODO notifaction to laucnher for modify
	public static final  String ACTION_LAUNCHER_UPGRADE_FAILED = "com.lenovo.lejingpin.hw.ACTION_LAUNCHER_UPGRADE_APP_FAILED";
	
	public static final  String ACTION_APK_FAILD_DOWNLOAD = "com.lenovo.lejingpin.hw.ACTION_APK_FAILD_DOWNLOAD";
	public static final  String ACTION_SETTING_ICON_UNUSE = "com.lenovo.lejingpin.hw.ACTION_SETTING_ICON_UNUSE";
	public static final  String ACTION_DOWNLOAD_STATE = "com.lenovo.lejingpin.hw.ACTION_DOWNLOAD_STATE";
	
	public static final  String ACTION_DETAIL_PREVIEW_URL_DOWNLOAD = "com.lenovo.lejingpin.ACTION_DETAIL_PREVIEW_URL_DOWNLOAD";
	public static final  String ACTION_DOWNLOAD_STATE_CHANGED = "com.lenovo.lejingpin.hw.ACTION_DOWNLOAD_STATE_CHANGED";
	
	
	public static final String ACTION_APP_START_DOWNLOAD = "com.lenovo.lejingpin.hw.ACTION_APP_START_DOWNLOAD";
	
	public static final String ACTION_PACKAGE_ADDED = "com.lenovo.lejingpin.hw.ACTION_PACKAGE_ADDED";
	
	public static final String AUTHORITY = "com.lenovo.lejingpin.hw.Provider";
	
    public static final int MSG_RECOMMEND_APP_LIST = 100;
    public static final int MSG_APP_INFO = 101;
    public static final int MSG_UPGRADE_LIST = 102;
    public static final int MSG_DOWN_LOAD_URL = 103;
    public static final int MSG_FRIEND_APP_LIST = 104;
    public static final int MSG_TIME_SCHEDULE = 105;
    
    public static final int TIME_INTERVAL_HOUR = 24 * 60 * 60 * 1000;
    public static final int TIME_INTERVAL_HOUR_MOBILE = 5 * 24 * 60 * 60 * 1000;
    public static final int TIME_INTERVAL_CHECK_HAWAII = 7 * 24 * 60 * 60 * 1000;
    public static final int COUNT_DISPLAY_APP_LIST = 34;
    public static final int COUNT_DB_APP_LIST = 60;
    
	public static final  int SPERE_TIME_SCHEDULE = 0;
	public static final  int UPGRADE_TIME_SCHEDULE = 1;
	public static final  int SPERE_TIME_CANCEL_SCHEDULE = 3;
	public static final  int UPGRADE_TIME_CANCEL_SCHEDULE = 4;
	
	public static final  int RESOLUTION_WIDTH = 1280;
	public static final  int RESOLUTION_HEIGHT = 800;
	
	public static final  String TYPE_SPERE_ACTION = "spere";
	public static final  String TYPE_UPGRADE_ACTION = "upgrade";
	public static final  String TYPE_APPINFO_ACTION = "appinfo";
	public static final  String TYPE_DOWNLOAD_ACTION = "download";
	public static final  String TYPE_COMMON_LIST_ACTION = "common_list";
	public static final  String TYPE_CHECK_HAWAII_ACTION = "check_hawaii";
	public static final  String TYPE_GAME_APP_LIST = "game_app_list";
	public static final   int COUNT_SPRERE_LIST_DB_TOTAL = 28;
	
	public static final int REQUEST_SPERE_APP_NUM = 28;
	public static final int REQUEST_TOP_APP_NUM = 10;
	public static final int REQUEST_SPECIAL_APP_NUM = 2;
	
	public static final  String APP_FROM_HW = "hw";
	public static final  String APP_FROM_STORE = "st";
	public static final  String APP_FROM_MAGIC_LAUNCHER = "MagicLauncher";
	public static final  String APP_FROM_LAUNCHER = "launcher";
	
	public static  final String EXTRA_PACKAGENAME = "package_name";
	public static  final String EXTRA_VERSION = "version_code";
	public static  final String EXTRA_STATUS = "status";
	public static  final String EXTRA_PROGRESS = "progress";
	public static  final String EXTRA_INSTALLPATH = "install_path";
	public static  final String EXTRA_APPNAME = "app_name";
	public static  final String EXTRA_APPSIZE = "app_size";
	public static  final String EXTRA_APPSTAR = "app_star";
	public static  final String EXTRA_APPICONURL = "app_icon_url";
	public static  final String EXTRA_VERSION_NAME = "version_name";
	public static  final String EXTRA_LOCAL_ID = "local_id";
	public static  final String EXTRA_PUSH_POSITION = "app_position";
	
	//yangmao add
	public static  final String EXTRA_CATEGORY = "category";
	
	
	// yangmao add for search_move 1225 start

	public static final String ACTION_DOWNLOAD_DELETE = "com.lenovo.action.ACTION_DOWNLOAD_DELETE";
	public static final String ACTION_DOWNLOAD_INSTALL_UNINSTALL = "com.lenovo.action.ACTION_DOWNLOAD_INSTALL_UNINSTALL";

	public static final String EXTRA_RESULT = "result";

	public static String getConnectType(Context context) {
		ConnectivityManager mConnMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = mConnMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo infoM = mConnMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (info != null && info.isConnected()) {
			return "wifi";
		} else if (infoM != null && infoM.isConnected()) {
			return "mobile";
		}
		return "other";
	}

	public static final String CATEGORY_HAWAII_SEARCH_APP = "search_app";

	public static final String ACTION_REQUEST_APPDOWNLOAD = "com.lenovo.action.ACTION_REQUEST_APPDOWNLOAD";
	// ------Common Download------
	public static final String ACTION_REQUEST_COMMON_DOWNLOAD = "com.lenovo.action.ACTION_REQUEST_COMMON_DOWNLOAD";

	public static final int CATEGORY_WALLPAPER = 2;
	public static final String ACTION_REQUEST_APP_COMMENT_LIST = "com.lenovo.action.ACTION_REQUEST_APP_COMMENT_LIST";

	public static final String ACTION_REQUEST_HAWAII_SEARCH_APP_COMMENT_LIST = "com.lenovo.action.ACTION_REQUEST_HAWAII_SEARCH_APP_COMMENT_LIST";

	public static final String CATEGORY_THEME_STRING = "0";
	public static final String CATEGORY_LOCKSCREEN_STRING = "1";
	public static final String CATEGORY_WALLPAPER_STRING = "2";
	public static final String CATEGORY_SCENE_STRING = "3";
	public static final String CATEGORY_APP_UPGRADE_STRING = "10";
	public static final String CATEGORY_APPMANAGER_UPGRADE_STRING = "11";
	public static final String CATEGORY_APP_RECOMMEND = "12";

	public static final  String ACTION_HAWAII_SEARCH_APP_INFO = "com.lenovo.action.ACTION_HAWAII_SEARCH_APP_INFO";

	public static final String EXTRA_INFO = "com.lenovo.intent.extra.info";
	public static final String EXTRA_ICON = "com.lenovo.intent.extra.icon";

	public static final  String ACTION_DOWNLOAD_APP_FROM_LESTORE = "com.lenovo.action.ACTION_DOWNLOAD_APP_FROM_LESTORE";

	public static final String ACTION_DOWNLOAD_FROM_COMMON = "com.lenovo.action.ACTION_DOWNLOAD_FROM_COMMON";

	public static final  String TYPE_HAWAII_SEARCH_APPINFO_ACTION = "hawaii_search_appinfo";

	public static final  String TYPE_COMMENTLIST_ACTION = "commentlist";
	
	public static final  String TYPE_HAWAII_SEARCH_COMMENTLIST_ACTION = "hawaii_search_commentlist";
	
	public static final int  CATEGORY_LELAUNCHER_SERVER_APK = 201;
	
	public static final String MIMETYPE_APK = "application/vnd.android.package-archive";
	
	// yangmao add for search_move end 1225
	
	
	
	//yangmao add for search_move start 0108
	
	public static int canAKeyIntall(Context context){
		if( checkSystemPermission(context) && checkInstallPermission(context)){
    		return INSTALL_TYPE_AKEY_NORMAL;
    	}/*else if( checkRootSystem(context)){
    		return INSTALL_TYPE_AKEY_SHELL;
    	}*/else{
    		return INSTALL_TYPE_NOT_AKEY;
    	}
	}
	private static boolean checkSystemPermission(Context context){
		PackageInfo packageinfo = null;
    	PackageManager packagemanager = context.getPackageManager();
    	String packName = context.getPackageName();
		try {
			packageinfo = packagemanager.getPackageInfo(packName, 0);
		} catch (NameNotFoundException e) {
			return false;
		}
		ApplicationInfo appInfo = packageinfo.applicationInfo;
        if( appInfo != null && (( appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0)){
        	Log.i(TAG,"checkSystemPermission,  system app!!!");
            return true;
        }
        Log.i(TAG,"checkSystemPermission,not system app!!!");
		return false;
	}
	private static boolean checkInstallPermission(Context context){
		if( context.checkCallingOrSelfPermission("android.permission.INSTALL_PACKAGES") == 0 ){
			Log.i(TAG,"checkInstallPermission, have install permission!!!");
			return true;
		}
		Log.i(TAG,"checkInstallPermission, not have install permission!!!");
		return false;
	}
	
	
	public static final int INSTALL_TYPE_NOT_AKEY = 0;
	public static final int INSTALL_TYPE_AKEY_NORMAL = 1;
	
	public static final String PREF_AKEY_INSTALL_CANCEL = "pref_akey_install_cancel";
	public static final String PREF_MAGICDOWNLOAD = "com.lenovo.launcher.magicdownload_preferences";
	public static final String EXTRA_POPTOAST = "poptoast";
	//end
	
	
	
}
