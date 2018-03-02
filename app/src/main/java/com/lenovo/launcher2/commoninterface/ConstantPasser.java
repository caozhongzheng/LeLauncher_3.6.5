package com.lenovo.launcher2.commoninterface;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.BaseColumns;

import com.lenovo.launcher2.customizer.Utilities;

public class ConstantPasser extends LauncherSettings {
		public static final int ITEM_CONTAINER_DESKTOP = LauncherSettings.Favorites.CONTAINER_DESKTOP;

		public static Bitmap resampleIconBitmap(Bitmap bitmap, Context context) {
			return Utilities.resampleIconBitmap(bitmap,
					context);
		}

		public static interface BaseLauncherColumns extends BaseColumns {

			public static final String TITLE = LauncherSettings.BaseLauncherColumns.TITLE;

			public static final String INTENT = LauncherSettings.BaseLauncherColumns.INTENT;

			public static final String ITEM_TYPE = LauncherSettings.BaseLauncherColumns.ITEM_TYPE;

			public static final int ITEM_TYPE_APPLICATION = LauncherSettings.BaseLauncherColumns.ITEM_TYPE_APPLICATION;

			public static final int ITEM_TYPE_SHORTCUT = LauncherSettings.BaseLauncherColumns.ITEM_TYPE_SHORTCUT;

			public static final String ICON_TYPE = LauncherSettings.BaseLauncherColumns.ICON_TYPE;

			public static final int ICON_TYPE_RESOURCE = LauncherSettings.BaseLauncherColumns.ICON_TYPE_RESOURCE;

			public static final int ICON_TYPE_BITMAP = LauncherSettings.BaseLauncherColumns.ICON_TYPE_BITMAP;

			public static final String ICON_PACKAGE = LauncherSettings.BaseLauncherColumns.ICON_PACKAGE;

			public static final String ICON_RESOURCE = LauncherSettings.BaseLauncherColumns.ICON_RESOURCE;

			public static final String ICON = LauncherSettings.BaseLauncherColumns.ICON;

			public static final String ICON_REPLACE = LauncherSettings.BaseLauncherColumns.ICON_REPLACE;
			public static final String TITLE_REPLACE = LauncherSettings.BaseLauncherColumns.TITLE_REPLACE;
		}

		/**
		 * Favorites.
		 */
		public static final class Favorites implements BaseLauncherColumns {

			public static final Uri CONTENT_URI = LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION;

			public static final Uri CONTENT_URI_NO_NOTIFICATION = LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION;

			public static Uri getContentUri(long id, boolean notify) {
				return LauncherSettings.Favorites.getContentUri(id, notify);
			}

			public static final String CONTAINER = LauncherSettings.Favorites.CONTAINER;

			public static final int CONTAINER_DESKTOP = LauncherSettings.Favorites.CONTAINER_DESKTOP;
			public static final int CONTAINER_HOTSEAT = LauncherSettings.Favorites.CONTAINER_HOTSEAT;

			public static final String SCREEN = LauncherSettings.Favorites.SCREEN;

			public static final String CELLX = LauncherSettings.Favorites.CELLX;

			public static final String CELLY = LauncherSettings.Favorites.CELLY;

			public static final String SPANX = LauncherSettings.Favorites.SPANX;

			public static final String SPANY = LauncherSettings.Favorites.SPANY;

			public static final int ITEM_TYPE_FOLDER = LauncherSettings.Favorites.ITEM_TYPE_FOLDER;

			public static final int ITEM_TYPE_LIVE_FOLDER = LauncherSettings.Favorites.ITEM_TYPE_LIVE_FOLDER;

			public static final int ITEM_TYPE_APPWIDGET = LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET;

			public static final int ITEM_TYPE_COMMEND_APPWIDGET = LauncherSettings.Favorites.ITEM_TYPE_COMMEND_APPWIDGET;
			public static final int ITEM_TYPE_COMMEND_SHORTCUT = LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT;

			public static final int ITEM_TYPE_WIDGET_CLOCK = LauncherSettings.Favorites.ITEM_TYPE_WIDGET_CLOCK;

			public static final int ITEM_TYPE_WIDGET_SEARCH = LauncherSettings.Favorites.ITEM_TYPE_WIDGET_SEARCH;

			public static final int ITEM_TYPE_WIDGET_PHOTO_FRAME = LauncherSettings.Favorites.ITEM_TYPE_WIDGET_PHOTO_FRAME;
			
			// LeosWidget
			public static final int ITEM_TYPE_LEOSWIDGET_VIEW = LauncherSettings.Favorites.ITEM_TYPE_LEOSWIDGET;
			// LeosWidget
			public static final String APPWIDGET_ID = LauncherSettings.Favorites.APPWIDGET_ID;

			@Deprecated
			public static final String IS_SHORTCUT = LauncherSettings.Favorites.IS_SHORTCUT;

			public static final String URI = LauncherSettings.Favorites.URI;

			public static final String DISPLAY_MODE = LauncherSettings.Favorites.DISPLAY_MODE;

			public static final String NEED_CONFIG_WIDGET = LauncherSettings.Favorites.CONFIGABLE_WIDGET;

		}

		/**
		 * Applications.
		 */
		public static final class Applications implements BaseLauncherColumns {

			public static final Uri CONTENT_URI = LauncherSettings.Applications.CONTENT_URI;

			public static final String LABEL = LauncherSettings.Applications.LABEL;

			public static final String CLASS = LauncherSettings.Applications.CLASS;

			public static final String CELL_INDEX = LauncherSettings.Applications.CELL_INDEX;

			public static final String ITEM_DRAGABLE = LauncherSettings.Applications.APP_CAN_DRAG;

		}
	}