package com.lenovo.lejingpin.hw.content.db;

import android.net.Uri;
import android.provider.BaseColumns;

import com.lenovo.lejingpin.hw.content.data.HwConstant;

public class SpereApp implements BaseColumns{
	
	private SpereApp() {}
	
	public static final Uri CONTENT_SPERE_URI = Uri.parse("content://"+HwConstant.AUTHORITY+"/spere");
	public static final Uri CONTENT_SPERE_LIST_URI = Uri.parse("content://"+HwConstant.AUTHORITY+"/sperelist");
	public static final String CONTENT_SPERE_TYPE = "vnd.android.cursor.dir/vnd.lenovo.spere";
	public static final String CONTENT_SPERE_ITEM_TYPE = "vnd.android.cursor.item/vnd.lenovo.spere";
	
	public static final String APP_NAME = "app_name";
	public static final String PACKAGE_NAME = "package_name";
	public static final String VERSION_CODE = "version_code";
	public static final String ICON_ADDRESS = "icon_addr";
	public static final String APP_COLLECT = "collect";
	public static final String APP_DELETE = "del_state";
	public static final String APP_LOCAL_ID = "local_id";
	public static final String APP_DOWNLOAD_COUNT = "download_count";
	public static final String APP_FAVORITES = "favorites";
	public static final String APP_STAR = "star";
	public static final String EXT_COLUMN_2 = "ext_colums_2";//version_anme
	public static final String APP_SIZE = "size";
	
}
