package com.lenovo.lejingpin.appsmgr.content;

import android.net.Uri;
import android.provider.BaseColumns;

public class UpgradeLocalApp implements BaseColumns {
	private UpgradeLocalApp() {
	}

	public static final Uri CONENT_UPGRADE_APP_URI = Uri.parse("content://"
			+ UpgradeUtil.AUTHORITY + "/upgradeapp");
	public static final Uri CONTENT_UPGRADE_APP_LIST_URI = Uri
			.parse("content://" + UpgradeUtil.AUTHORITY + "/upgradeapplist");
	public static final String CONTENT_UPGRADE_APP_TYPE = "vnd.android.cursor.dir/vnd.lenovo.upgradeapp";
	public static final String CONTENT_UPGRADE_APP_ITEM_TYPE = "vnd.android.cursor.item/vnd.lenovo.upgradeapp";

	public static final String PACKAGE_NAME = "package_name";
	public static final String VERSION_CODE = "version_code";
	public static final String APP_NAME = "app_name";
	public static final String ICON_ADDRESS = "icon_addr";
	public static final String APP_CATEGORY = "category";
	public static final String APP_SIZE = "app_size";
	public static final String APP_STAR = "app_star";
	public static final String APP_PAY = "app_pay";
	public static final String VERSION_NAME = "version_name";

	public static final String APP_DESC = "app_desc";
	public static final String APP_SNAPSHOT = "app_snapshot";

	public static final String APP_ISPAY = "app_ispay";
	public static final String APP_PUBLISH_NAME = "app_publish_name";
	public static final String APP_PUBLISH_DATE = "app_publish_date";
	public static final String APP_COMMENT_COUNT = "app_comment_count";
	public static final String APP_DOWNLAOD_COUNT = "app_download_count";
	public static final String APP_DELETE = "delete_state";
	public static final String APP_UPDATE_IGNORE = "ignore";
	public static final String EXT_COLUMN_1 = "ext_colums_1";
	public static final String EXT_COLUMN_2 = "ext_colums_2";


}
