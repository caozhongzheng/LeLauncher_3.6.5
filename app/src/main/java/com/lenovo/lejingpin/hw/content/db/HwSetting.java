package com.lenovo.lejingpin.hw.content.db;

import android.net.Uri;
import android.provider.BaseColumns;

import com.lenovo.lejingpin.hw.content.data.HwConstant;

public class HwSetting implements BaseColumns{
	
	private HwSetting(){};

	public static final Uri CONENT_SETTING_URI = Uri.parse("content://"+HwConstant.AUTHORITY+"/setting");
	public static final String CONTENT_APPINFO_ITEM_TYPE = "vnd.android.cursor.item/vnd.lenovo.setting";
	
	public static final String ID = "id";
	public static final String ACTION = "action";
	public static final String VALUE = "value";
	public static final String EXT_COLUMN_1 = "ext_colums_1";
	public static final String EXT_COLUMN_2 = "ext_colums_2";
}
