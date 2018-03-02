package com.lenovo.lejingpin.hw.content.db;

import android.net.Uri;
import android.provider.BaseColumns;

import com.lenovo.lejingpin.hw.content.data.HwConstant;

public class AppInfo implements BaseColumns {
	private AppInfo(){}
	
	public static final Uri CONENT_APPINFO_URI = Uri.parse("content://"+HwConstant.AUTHORITY+"/appinfo");
	public static final Uri CONTENT_APPINFO_LIST_URI = Uri.parse("content://"+HwConstant.AUTHORITY+"/appinfolist");
	public static final String CONTENT_APPINFO_ITEM_TYPE = "vnd.android.cursor.item/vnd.lenovo.appinfo";
	
	public static final String APP_NAME = "app_name";
	public static final String PACKAGE_NAME = "package_name";
	public static final String VERSION_CODE = "version_code";
	public static final String APP_STAR = "app_star";
	public static final String APP_PAY = "app_pay";
	public static final String APP_SIZE = "app_size";
	public static final String APP_DESC = "app_desc";
	public static final String APP_SNAPSHOT="app_snapshot";
	public static final String APP_COLLECTION = "collect";
	public static final String APP_DELETE = "delete";
	public static final String APP_VERSION = "app_version";
	public static final String APP_PUBLISH_DATE = "app_publish_date";
	public static final String APP_COMMENT_COUNT = "app_comment_count";
	public static final String APP_DOWNLOAD_COUNT = "app_download_count";
	public static final String EXT_COLUMN_1 = "ext_colums_1";
	public static final String EXT_COLUMN_2 = "ext_colums_2";
	
}
