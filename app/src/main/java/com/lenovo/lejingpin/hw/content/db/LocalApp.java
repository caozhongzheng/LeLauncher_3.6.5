package com.lenovo.lejingpin.hw.content.db;

import android.net.Uri;
import android.provider.BaseColumns;

import com.lenovo.lejingpin.hw.content.data.HwConstant;

public class LocalApp implements BaseColumns {
	private LocalApp(){}
	
	public static final Uri CONENT_LOCAL_URI = Uri.parse("content://"+HwConstant.AUTHORITY+"/local");
	public static final String CONTENT_LOCAL_TYPE = "vnd.android.cursor.dir/vnd.lenovo.local";
	public static final String CONTENT_LOCAL_ITEM_TYPE = "vnd.android.cursor.item/vnd.lenovo.local";
	
	public static final String PACKAGE_NAME = "package_name";
	public static final String VERSION_CODE = "version_code";
	public static final String APP_STATE = "state";
	public static final String APP_DOWNLOAD_PATH = "download_path";
	public static final String EXT_COLUMN_1 = "ext_colums_1";
	public static final String EXT_COLUMN_2 = "ext_colums_2";

}
