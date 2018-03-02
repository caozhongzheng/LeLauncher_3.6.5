package com.lenovo.lejingpin.share.download;

import android.net.Uri;
import android.provider.BaseColumns;

public class SpereApp implements BaseColumns {

	private SpereApp() {
	}

	public static final Uri CONTENT_SPERE_URI = Uri.parse("content://"
			+ DownloadConstant.AUTHORITY + "/spere");
	public static final Uri CONTENT_SPERE_LIST_URI = Uri.parse("content://"
			+ DownloadConstant.AUTHORITY + "/sperelist");
	public static final String CONTENT_SPERE_TYPE = "vnd.android.cursor.dir/vnd.lenovo.spere";
	public static final String CONTENT_SPERE_ITEM_TYPE = "vnd.android.cursor.item/vnd.lenovo.spere";

	public static final String APP_NAME = "app_name";
	public static final String PACKAGE_NAME = "package_name";
	public static final String VERSION_CODE = "version_code";
	public static final String ICON_ADDRESS = "icon_addr";
	public static final String APP_CATEGORY = "category";
	public static final String APP_DELETE = "del_state";
	public static final String APP_LOCAL_ID = "local_id";
	public static final String APP_FAVORITES = "favorites";
	public static final String EXT_COLUMN_2 = "ext_colums_2";

	public static final String APP_PAY = "app_pay";
	public static final String APP_SIZE = "app_size";
	public static final String APP_STAR = "app_star";
	public static final String APP_VERSION = "app_version";
	public static final String EXT_COLUMN_1 = "ext_colums_1";
	// zhanglz1
	public static final String DOWNLOAD_TIMES = "download_times";
	public static final String AUTHER = "auther";
	public static final String APP_COLLECT = "collect";
	public static final String URL = "url";
	public static final String PREVIEW_ADDRESS = "preview_addr";
}
