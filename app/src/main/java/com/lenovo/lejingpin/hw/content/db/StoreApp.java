package com.lenovo.lejingpin.hw.content.db;

import android.net.Uri;
import android.provider.BaseColumns;

import com.lenovo.lejingpin.hw.content.data.HwConstant;

public class StoreApp implements BaseColumns{
	private StoreApp (){}

	public static final Uri CONTENT_STORE_URI = Uri.parse("content://"+HwConstant.AUTHORITY+"/store");
	public static final Uri CONTENT_STORE_LIST_URI = Uri.parse("content://"+HwConstant.AUTHORITY+"/storelist");
	public static final String CONTENT_STORE_TYPE = "vnd.android.cursor.dir/vnd.lenovo.store";
	public static final String CONTENT_STORE_ITEM_TYPE = "vnd.android.cursor.item/vnd.lenovo.store";
	
	public static final String APP_NAME = "app_name";
	public static final String PACKAGE_NAME = "package_name";
	public static final String VERSION_CODE = "version_code";
	public static final String ICON_ADDRESS = "icon_addr";
	public static final String APP_FROM = "app_from";
	public static final String APP_DELETE = "del_state";
	public static final String APP_LOCAL_ID = "local_id";
	public static final String EXT_COLUMN_1 = "ext_colums_1";
	public static final String EXT_COLUMN_2 = "ext_colums_2";
}
