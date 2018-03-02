package com.lenovo.launcher2.weather.widget.settings;

import android.net.Uri;
import android.provider.BaseColumns;

public class CityColumns implements BaseColumns{
	private CityColumns(){
		
	}
	public static final String AUTHORITY = "com.lenovo.launcher";
	/*
	 * Column definitions
	 */
	public static final String TABLE_NAME = "cities";
	public static final String COLUMN_CITY_ID = "city_id";
	public static final String COLUMN_CITY_NAME = "city_name";
	public static final String COLUMN_CITY_NAME_EN = "city_name_en";
	public static final String COLUMN_CITY_NAME_TW = "city_name_tw";
	public static final String COLUMN_PROVINCE_NAME = "province_name";
	public static final String COLUMN_PROVINCE_NAME_EN = "province_name_en";
	public static final String COLUMN_PROVINCE_NAME_TW = "province_name_tw";

	/*
	 * URI definitions
	 */
	private static final String SCHEME = "content://";
	private static final String PATH_CITY = "/cities";
	private static final String PATH_CITY_ID = "/city/cid";
	private static final String PATH_PROVINCE = "/city/province";
	public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_CITY);
	public static final Uri CONTENT_URI_CITY_ID = Uri.parse(SCHEME + AUTHORITY + PATH_CITY_ID);
	public static final Uri CONTENT_URI_PROVINCE = Uri.parse(SCHEME + AUTHORITY + PATH_PROVINCE);

	/*
	 * MIME type definitions
	 */
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/city";
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/city";
	public static final String DEFAULT_SORT_ORDER = _ID + " ASC";
	
	public static final int INDEX_DEF_CITY_ID = 0;
	public static final int INDEX_DEF_CITY_NAME = 1;
	public static final int INDEX_DEF_CITY_NAME_TW = 2;
	public static final int INDEX_DEF_CITY_NAME_EN = 3;
	public static final int INDEX_DEF_CITY_NAME_PROVINCE = 4;
	public static final int INDEX_DEF_CITY_NAME_PROVINCE_TW = 5;
	public static final int INDEX_DEF_CITY_NAME_PROVINCE_EN = 6;
}
