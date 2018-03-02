package com.lenovo.launcher2.customizer;

import java.io.File;
import java.io.FileFilter;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import com.lenovo.launcher2.customizer.Debug.R2;

/**
 * Author : ChengLiang
 * */
public class ConstantAdapter {
	public static final String WORKED_DIR = "com.lenovo.launcher";

	// for profile <==>
	public static final String PROFILES = "Profiles";
	public static final String VERSION = "version";
	public static final String NAME = "name";
	public static final String KEY = "key";
	public static final String PROFILE = "Profile";
	public static final String FOLDERS = "Folders";
	public static final String FOLDER = "folder";
	public static final String CONFIG = "config";
	public static final String FOLDER_APPS = "applist";
	public static final String APP = "app";
	public static final String FOLDER_TITLES = "labellist";
	public static final String FOLDER_TITLE = "label";
	public static final String PRIORITIES = "Priorities";
	public static final String PRIORITY = "priority";
	public static final String WIDGETS = "Widgets";
	public static final String WIDGET = "widget";
	public static final String XM3DWIDGETS = "XMWidgets";
	public static final String XM3DWIDGET = "xmwidget";
 /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-10 START */
	public static final String LEOSE2EWIDGETS = "LEOSE2EWidgets";
	public static final String LEOSE2EWIDGET = "LEOSE2EWidget";
 /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-10 END */
	public static final String SETTINGS = "Settings";
	public static final String SETTING = "setting";
	public static final String QUICKENTIRES = "ShortCuts";
	public static final String QICKENTRY = "shortcut";
	private ConstantAdapter() {
		// TODO Auto-generated constructor stub
	}
	public static class ProfilesAttributes {

		public static class ContainerType {
			public static final String CONTAINER_DESKTOP = "desktop";
			public static final String CONTAINER_HOTSEAT = "hotseat";
		}

		public static  final class FolderAttributes {

			// public static final String ID = LauncherSettings.Favorites;

			public static final String TITLE = "title";
			public static final String ICON = "icon";
			public static final String SCREEN = "screen";
			public static final String CELLX = "cellX";
			public static final String CELLY = "cellY";
			public static final String CONTAINER = "container";

		}

		public static  final class WidgetAttributes {

			public static final String WIDGETID = "appWidgetId";
			public static final String PACKAGENAME = "packagename";
			public static final String CLASSNAME = "classname";
			public static final String SCREEN = "screen";
			public static final String CELLX = "cellX";
			public static final String CELLY = "cellY";
			public static final String SPANX = "spanX";
			public static final String SAPNY = "sapnY";

			public static final String SNAPTHOT = "preview";

			// if not true
			public static final String URI = "uri";
			public static final String INTENT = "intent";

			public static final String LABEL = "label";

			// need config
			public static final String NEED_CONFIG = "config";

		}

		public final static class QuickEntryAppAttributes extends AppAttributes {
			public static final String CONTAINER = "container";

			// these may be invalid while container is HotSeat
			public static final String CELLX = "cellX";
			public static final String CELLY = "cellY";
			public static final String SCREEN = "screen";
		}

		// XMUI
		public static class BaseAttributes {
			public static final String CELLX = "cellX";
			public static final String CELLY = "cellY";
			public static final String SCREEN = "screen";
			public static final String CONTAINER = "container";
		}

		public final static class XM3DWidgetAttributes extends BaseAttributes {
			public static final String SPANX = "spanX";
			public static final String SPANY = "sapnY";

			public static final String PACKAGE_NAME = "packageName";
			public static final String CLASS_NAME = "className";

			public static final String USE_GL_VERSION = "glVersion";
		}

		// XMUI
		
		 /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-10 START */
		public final static class LeosWidgetAttributes extends BaseAttributes {
			public static final String SPANX = "spanX";
			public static final String SPANY = "sapnY";

			public static final String PACKAGE_NAME = "packageName";
			public static final String CLASS_NAME = "className";
			
			/*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. S***/
			public static final String SNAPTHOT = "preview";
			/*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. E***/

		}

		 /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-10 END */
		public static class AppAttributes {

			public static final String ITEM_TYPE = "type";

			public static final String PACKAGENAME = "packagename";
			public static final String CLASSNAME = "classname";

			public static final String ICON = "icon";
			public static final String ICON_TYPE = "icon_type";
			public static final String ICONREPLACED_MARK = "icon_customed";
			public static final String ICONRESOURCE = "icon_resource";
			public static final String ICONPACKAGE = "icon_package";

			// this is for unnormal ones
			public static final String ACTION = "action";
			public static final String TITLE = "title";
			public static final String REPLACE_TITLE = "replace_title";
			public static final String URI = "uri";
		}

		public static class SettingAttributes {
			public static final String CATEGORY = "category";
			public static final String NAME = "name";
			public static final String VALUE = "value";
			public static final String VALUETYPE = "type";

			// for pref set
			public static final String SETNAME = "set";

		}

		public static class PriorityAttributes extends AppAttributes {

			public static final String TITLE = "label";
			public static final String ORDER = "index";
			public static final String NAME_ALLAPPLIST = "allapplist";
			public static final String DRAGABLE = "dragable";
		}
		public static  final class FolderTitleAttributes {
			public static final String COUNTRY = "countrycode";
			public static final String VALUE = "value";
		}

	}

	/** new profile constants region S */
	// public static final String
	public static final byte ITEM_SNAPSHOT_SAVE_METHOD_NONE = -1;
	public static final byte ITEM_SNAPSHOT_SAVE_METHOD_WIDGET = 0;
	public static final byte ITEM_SNAPSHOT_SAVE_METHOD_SHORTCUT = 1;
	public static final byte ITEM_SNAPSHOT_SAVE_METHOD_FOLDER = 2;

	// sdcard
	public static final long SDCARD_FREE_BEFORE_SPACE = 10 * 1000 * 1024;

	public static final String DIR_SNAPSHOT = "//snapshot";
	public static final String DIR_SNAPSHOT_WIDGET = "//widget";
	public static final String DIR_SNAPSHOT_SHORTCUT = "//shortcut";
	public static final String DIR_SNAPSHOT_PREVIEW = "//preview";
	public static final String DIR_WALLPAPER = "//wallpaper";
	public static final String DIR_DIY_WALLPAPER = "//diy";
	public static final String DIR_DATA_FILES = "//files/";
	public static final String DIR_DATA_FILES_EXTRA_FILES = "//extra/";
	public static final String DIR_FALLBACK_WHILE_RESTORE = "//.fallback/";

	public static final String PROFILE_DESC_XML_NAME = "desc.xml";

	public static final String RESTORE_PROFILE_MARK = "profile_restore";
	//public static final String DIR_DATA = "data/data/" + WORKED_DIR;
	/* Lenovo zhaoxin5 20131111 fix multi-user bug begin*/
	public static final String getMyPackageDir(Context context){
	    return context.getFilesDir().getParentFile().getAbsolutePath();
	}
	/* Lenovo zhaoxin5 20131111 fix multi-user bug end*/
	public static final String DIR_PREFS = "//shared_prefs";
	public static final String PREFIX_PREF_BACKUP = "backup_";

	public static final String PREF_TMP_BACKUP = "bk_time_mark";

	public static final String PREFIX_TIME_BACKUP_FILE = "prefix_time_stamp_";

	public static String EXCLUDED_SETTING_KEY = "exclude_from_backup";

	/** new profile constants region N */

	// for backup & restore

	public static final int ICON_ADJUST_SIZE = 72;

	public static final String PARENT_DIR_ON_SDCARD = "//.IdeaDesktop//";

	public static final String LOCAL_DATA_FILE_PATH_TO_BACK_UP = "//data//data//"
			+ WORKED_DIR;
	public static final String LOCAL_DATA_FILE_PATH_TO_RESTORE = "//data//data";

	public static final String DIR_PARENT_OF_STORAGE_BACKUP_FILE = Environment
			.getExternalStorageDirectory().getPath() + PARENT_DIR_ON_SDCARD;
	public static final String DIR_TO_STORAGE_BACKUP_FILE = "//.backup";
//	public static final String DIR_TO_LOCAL_BACKUP_FILE = DIR_TO_STORAGE_BACKUP_FILE + "//.localbackup/"; //add by shenchao1
	public static final String DIR_TIME_STAMPED_STORE = "//.withtimestamp/";
	public static final String DIR_TO_STORAGE_TMP_PREVIEWS = "//tmp";
	public static final String SUFFIX_FOR_BACKUP_FILE = ".lbk";
	public static final String SUFFIX_FOR_PREF_FILE = ".xml";
	public static final String DIR_TO_STORAGE_CLOUD_BACKUP_FILE = "//.backup";   //add by shenchao.

	public static final String PROFILE_SNAPSHOT_STORAGE_PATH = "//files//snap";
	public static final String PROFILE_SNAPSHOT_PREVIEW_NAME = "preview.png";
	public static final String SUFFIX_FOR_PREVIEW_SNAPSHOT = ".png";

	public static final String PREFIX_FOR_SNAP_CACHE = ".snaps_";
	// public static final String PROFILE_SNAPSHOT_BACKUP_TO_PATH =
	// DIR_PARENT_OF_STORAGE_BACKUP_FILE
	// + "//files";

	// for mix and unmix
	public static final String SUFFIX_FOR_MIX_TEMP_FILE = "_TMP";
	public static final String SUFFIX_FOR_UNMIX_TEMP_FILE = "_TMPX";

	public static final long BACKUP_FILE_MIN_NEEDED_SPACE_IN_BYTE_FOR_CHECK = 1500000;

	// for wall paper
	public static final String BACKUP_WALL_PAPER_FILE = LOCAL_DATA_FILE_PATH_TO_BACK_UP
			+ "//files//wallpaper.png";

	/***
	 * backup status expandition
	 * 
	 */
	public static class OperationState {

		public static final byte SUCCESS = 0;
		public static final byte FAILED_NO_SDCARD = SUCCESS + 1;
		public static final byte FAILED_NORMAL = FAILED_NO_SDCARD + 1;

		public static final byte SCENE_NAME_EXISTS = FAILED_NORMAL + 1;
		public static final byte SCENE_NAME_UNIQUE = SCENE_NAME_EXISTS + 1;
		public static final byte SCENE_NAME_NOT_FOUND = SCENE_NAME_UNIQUE + 1;
		public static final byte FAILED_SDCARD_NO_SPACE = SCENE_NAME_NOT_FOUND + 1;

		public static final byte FAIED_WHILE_BUSY = FAILED_SDCARD_NO_SPACE + 1;

		public static final byte SCENE_NAME_RESERVED = FAIED_WHILE_BUSY + 1;
		public static final byte PAD_APPALY_PHONE_SCENE = SCENE_NAME_RESERVED + 1;
		public static final byte PHONE_APPALY_PAD_SCENE = PAD_APPALY_PHONE_SCENE + 1;
		// this is used by Interactive Face
		public static final byte CRITICAL_DEFAULT_RESTORING_ALREADY_START = FAIED_WHILE_BUSY + 1;
		public static final byte CRITICAL_DEFAULT_RESTORING_NEED_START = CRITICAL_DEFAULT_RESTORING_ALREADY_START + 1;
		public static final byte CRITICAL_DEFAULT_RESTORING_NO_NEED_START = CRITICAL_DEFAULT_RESTORING_NEED_START + 1;
		/* RK_ID:RK_UPDATE_DESKTOP_CONFIG AUT:liuyg1@lenovo.com DATE: 2012-11-1 START */	
//		public static final byte CRITICAL_DEFAULT_UPDATING_NEED_START = CRITICAL_DEFAULT_RESTORING_NO_NEED_START + 1;
//		public static final byte CRITICAL_DEFAULT_UPDATING_ALREADY_START = CRITICAL_DEFAULT_UPDATING_NEED_START + 1;
		// time - stamped
		public static final byte TIME_STAMPED_FILE_NOT_FOUND = CRITICAL_DEFAULT_RESTORING_NO_NEED_START + 1;
		/* RK_ID:RK_UPDATE_DESKTOP_CONFIG AUT:liuyg1@lenovo.com DATE: 2012-11-1 START */	

		// last state check
		public static final byte CRITICAL_LAST_TIME_STATE_FAILED = TIME_STAMPED_FILE_NOT_FOUND + 1;
		public static final byte CRITICAL_LAST_TIME_STATE_SUCCESS = CRITICAL_LAST_TIME_STATE_FAILED + 1;

		// restore state --- no need
		public static final byte RESTORE_NONE = CRITICAL_LAST_TIME_STATE_SUCCESS + 1;

	};

	// R2 -- BU need
	public static final String DEFAULT_BACKUP_FILE = "//system//etc//lenovo_profile.lbk";
	public static final String PREF_REGULAR_PREFERENCES_DEFAUT_PATH = Environment.getExternalStorageDirectory()+ "/.IdeaDesktop/"+"//.regular_pref//";
	public static final String PREF_REGULAR_PREFERENCES_DEFAUT_FILE = Environment.getExternalStorageDirectory()+ "/.IdeaDesktop/"+"//.regular_pref//"+"com.lenovo.launcher.regularapplist_preferences.xml";
	public static final String PREFIX_EXTERNAL_PROFILE_CONFIG = "config_";
	public static final String PREF_FIRST_LAUNCH_CHECK_NAME = "first_check";
	public static final String PREF_FIRST_LAUNCH_CHECK_KEY = "first_check";
	public static final String PREF_FIRST_LOADING_DEFAULT_FILE_NAME = "first_loading";
	 public static final String PREF_FIRST_LOADING_DEFAULT_FILE_KEY = "first_loading";
	public static final String PREF_CURR_FACTORY_PROFILE = "current_factory_path";
	public static final String DEFAULT_PROFILE_NAME = "标准";
	/* RK_ID:RK_UPDATE_DESKTOP_CONFIG AUT:liuyg1@lenovo.com DATE: 2012-11-1 START */	
	public static final String PREF_VERSION_LAUNCH_OLD_NAME = "old_version";
	public static final String PREF_VERSION_LAUNCH_OLD_KEY = "old_version";
	/* RK_ID:RK_UPDATE_DESKTOP_CONFIG AUT:liuyg1@lenovo.com DATE: 2012-11-1 END */
	// another default profile in assets
	public static final String ASSET_PROFILE_BACKUP_FILE = "profile/default_profile.lbk";
	public static final String ASSET_PROFILE_DUMP_PATH = "/data/data/com.lenovo.launcher/files/default_profile.lbk";

	// multi-inner profile
	public static final String INNER_PROFLE_DIR = "//system//etc//build_in_profiles//";

	// 2R --

	// for extral mark
	public static final String PREF_RESTORING_STATE = "restore_state";
	public static final String PREF_RESTORING_STATE_KEY_ISCLEAR = "isClear";
	public static final String PREF_RESTORING_STATE_KEY_NOTIFIED = "notified";
	public static final String PREF_RESTORING_WHICH_KEY = "which";

	// for widget backup
	public static final String WIDGET_BACKUP_HELPER = "backup_widget_helper";

	public static final Uri WIDGET_BACKUP_CONTENT_URI = Uri.parse("content://"
			+ "com.android.launcher2.settings" + "/" + "favorites" + "?"
			+ "notify" + "=true");

	public static final String WIDGET_BACKUP_ITEM_TYPE = "itemType";
	public static final String WIDGET_BACKUP_WIDGETID_SET = "widgetIdSet";
	public static final String WIDGET_BACKUP_WIDGETID = "appWidgetId";
	public static final String WIDGET_BACKUP_PACKAGENAME = "packageName";
	public static final String WIDGET_BACKUP_CLASSNAME = "className";
	public static final String WIDGET_BACKUP_SCREEN = "screen";
	public static final String WIDGET_BACKUP_CELLX = "cellX";
	public static final String WIDGET_BACKUP_CELLY = "cellY";
	public static final String WIDGET_BACKUP_SPANX = "spanX";
	public static final String WIDGET_BACKUP_SPANY = "spanY";
	public static final String WIDGET_BACKUP_CONTAINER = "container";
	public static final Long WIDGET_BACKUP_CONTAINER_DESKTOP = -100L;

	// end for widget backup

	public static final FileFilter FILE_FILTER_SNAPSHOT = new FileFilter() {
		@Override
		public boolean accept(File pathname) {
			try {
				String name = pathname.getName();
				if (name.endsWith(ConstantAdapter.SUFFIX_FOR_PREVIEW_SNAPSHOT)) {
					return true;
				}
				return false;
			} catch (Exception e) {
				return false;
			}
		}
	};

	public static final FileFilter FILE_FILTER_BACKUPFILE = new FileFilter() {
		@Override
		public boolean accept(File pathname) {
			try {
				String name = pathname.getName();
				if (name.endsWith(ConstantAdapter.SUFFIX_FOR_BACKUP_FILE)) {
					return true;
				}
				return false;
			} catch (Exception e) {
				if (Debug.MAIN_DEBUG_SWITCH)
					R2.echo("Exception while fetch suffix");
				return false;
			}
		}
	};

	public static final FileFilter FILE_FILTER_SHARED_PREFS = new FileFilter() {
		@Override
		public boolean accept(File pathname) {
			try {
				String name = pathname.getName();
				if (name.endsWith(".xml")) {
					return true;
				}
				return false;
			} catch (Exception e) {
				if (Debug.MAIN_DEBUG_SWITCH)
					R2.echo("Exception while fetch suffix");
				return false;
			}
		}
	};

	public static final FileFilter FILE_FILTER_DEFAULT_TIME_STAMPED_PREFIX = new FileFilter() {
		@Override
		public boolean accept(File pathname) {
			try {
				String name = pathname.getName();
				if (name.contains(ConstantAdapter.PREFIX_TIME_BACKUP_FILE)) {
					return true;
				}
				return false;
			} catch (Exception e) {
				if (Debug.MAIN_DEBUG_SWITCH)
					R2.echo("Exception while fetch suffix");
				return false;
			}
		}
	};

	// clean temporary lbk files

	public static final FileFilter FILE_FILTER_TMPX = new FileFilter() {
		@Override
		public boolean accept(File pathname) {
			try {
				String name = pathname.getName();
				if (name.endsWith(SUFFIX_FOR_MIX_TEMP_FILE)
						|| name.endsWith(SUFFIX_FOR_UNMIX_TEMP_FILE)) {
					return true;
				}
				return false;
			} catch (Exception e) {
				return false;
			}
		}
	};

}
