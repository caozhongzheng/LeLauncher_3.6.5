/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lenovo.launcher2.commoninterface;



import com.lenovo.launcher2.customizer.Constants;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Settings related utilities.
 */
public class LauncherSettings {
	public static interface BaseLauncherColumns extends BaseColumns {
        /**
         * Descriptive name of the gesture that can be displayed to the user.
         * <P>Type: TEXT</P>
         */
        static final String TITLE = "title";

        /**
         * The Intent URL of the gesture, describing what it points to. This
         * value is given to {@link android.content.Intent#parseUri(String, int)} to create
         * an Intent that can be launched.
         * <P>Type: TEXT</P>
         */
        static final String INTENT = "intent";

        /**
         * The type of the gesture
         *
         * <P>Type: INTEGER</P>
         */
        static final String ITEM_TYPE = "itemType";

        /**
         * The gesture is an application
         */
        static final int ITEM_TYPE_APPLICATION = 0;

        /**
         * The gesture is an application created shortcut
         */
        static final int ITEM_TYPE_SHORTCUT = 1;

        /**
         * The icon type.
         * <P>Type: INTEGER</P>
         */
        static final String ICON_TYPE = "iconType";

        /**
         * The icon is a resource identified by a package name and an integer id.
         */
        static final int ICON_TYPE_RESOURCE = 0;

        /**
         * The icon is a bitmap.
         */
        static final int ICON_TYPE_BITMAP = 1;

        /**
         * The icon package name, if icon type is ICON_TYPE_RESOURCE.
         * <P>Type: TEXT</P>
         */
        static final String ICON_PACKAGE = "iconPackage";

        /**
         * The icon resource id, if icon type is ICON_TYPE_RESOURCE.
         * <P>Type: TEXT</P>
         */
        static final String ICON_RESOURCE = "iconResource";

        /**
         * The custom icon bitmap, if icon type is ICON_TYPE_BITMAP.
         * <P>Type: BLOB</P>
         */
        static final String ICON = "icon";

        /* AUT: liuli liuli0120@use.com.cn DATE: 2012-01-29 START, add iconReplace row */
        /**
         * The custom icon bitmap, by user's modification.
         * <P>Type: BLOB</P>
         */
        static final String ICON_REPLACE = "iconReplace";

        /**
         * The custom title, be displayed to user, by user's modification.
         * <P>Type: TEXT</P>
         */
        static final String TITLE_REPLACE = "titleReplace";
        /* AUT: liuli liuli0120@use.com.cn DATE: 2012-01-29 END */
    }

    /**
     * Favorites.
     */
	public static final class Favorites implements BaseLauncherColumns {
        /**
         * The content:// style URL for this table
         */
		public static final Uri CONTENT_URI = Uri.parse("content://" +
				Constants.AUTHORITY + "/" + Constants.TABLE_FAVORITES +
                "?" + Constants.PARAMETER_NOTIFY + "=true");

        /**
         * The content:// style URL for this table. When this Uri is used, no notification is
         * sent if the content changes.
         */
        public static final Uri CONTENT_URI_NO_NOTIFICATION = Uri.parse("content://" +
        		Constants.AUTHORITY + "/" + Constants.TABLE_FAVORITES +
                "?" + Constants.PARAMETER_NOTIFY + "=false");

        /**
         * The content:// style URL for a given row, identified by its id.
         *
         * @param id The row id.
         * @param notify True to send a notification is the content changes.
         *
         * @return The unique content URL for the specified row.
         */
        public static Uri getContentUri(long id, boolean notify) {
            return Uri.parse("content://" + Constants.AUTHORITY +
                    "/" + Constants.TABLE_FAVORITES + "/" + id + "?" +
                    Constants.PARAMETER_NOTIFY + "=" + notify);
        }

        /**
         * The container holding the favorite
         * <P>Type: INTEGER</P>
         */
        public static final String CONTAINER = "container";

        /**
         * The icon is a resource identified by a package name and an integer id.
         */
        public static final int CONTAINER_DESKTOP = -100;
        public static final int CONTAINER_HOTSEAT = -101;

        /**
         * The screen holding the favorite (if container is CONTAINER_DESKTOP)
         * <P>Type: INTEGER</P>
         */
        static final public String SCREEN = "screen";

        /**
         * The X coordinate of the cell holding the favorite
         * (if container is CONTAINER_HOTSEAT or CONTAINER_HOTSEAT)
         * <P>Type: INTEGER</P>
         */
        static final public String CELLX = "cellX";

        /**
         * The Y coordinate of the cell holding the favorite
         * (if container is CONTAINER_DESKTOP)
         * <P>Type: INTEGER</P>
         */
        static final public String CELLY = "cellY";

        /**
         * The X span of the cell holding the favorite
         * <P>Type: INTEGER</P>
         */
        static final public String SPANX = "spanX";

        /**
         * The Y span of the cell holding the favorite
         * <P>Type: INTEGER</P>
         */
        static final public String SPANY = "spanY";

        /**
         * The favorite is a user created folder
         */
        static final public int ITEM_TYPE_FOLDER = 2;

        /**
        * The favorite is a live folder
        *
        * Note: live folders can no longer be added to Launcher, and any live folders which
        * exist within the launcher database will be ignored when loading.  That said, these
        * entries in the database may still exist, and are not automatically stripped.
        */
        static final int ITEM_TYPE_LIVE_FOLDER = 3;

        /**
         * The favorite is a widget
         */
        public static final int ITEM_TYPE_APPWIDGET = 4;
        
        /** AUT: henryyu1986@163.com DATE: 2011-12-29 S */
        public static final int ITEM_TYPE_COMMEND_APPWIDGET = 5;
        /** AUT: henryyu1986@163.com DATE: 2011-12-29 E */

        /** AUT: zhanglq@bj.cobellink.com DATE: 2012-2-6 S */
        public static final int ITEM_TYPE_COMMEND_SHORTCUT = 6;
        /** AUT: zhanglq@bj.cobellink.com DATE: 2012-2-6 E */
        
        /**
         * The favorite is a clock
         */
        public static final int ITEM_TYPE_WIDGET_CLOCK = 1000;

        /**
         * The favorite is a search widget
         */
        public static final int ITEM_TYPE_WIDGET_SEARCH = 1001;

        /**
         * The favorite is a photo frame
         */
        public static final int ITEM_TYPE_WIDGET_PHOTO_FRAME = 1002;
      
		
		/*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-09-26 START */
		public static final int ITEM_TYPE_LEOSWIDGET = 7;    
		/*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-09-26 END */
        /**
         * The appWidgetId of the widget
         *
         * <P>Type: INTEGER</P>
         */
        public static final String APPWIDGET_ID = "appWidgetId";
        
        /**
         * Indicates whether this favorite is an application-created shortcut or not.
         * If the value is 0, the favorite is not an application-created shortcut, if the
         * value is 1, it is an application-created shortcut.
         * <P>Type: INTEGER</P>
         */
        @Deprecated
        static final String IS_SHORTCUT = "isShortcut";

        /**
         * The URI associated with the favorite. It is used, for instance, by
         * live folders to find the content provider.
         * <P>Type: TEXT</P>
         */
        public static final String URI = "uri";

        /**
         * The display mode if the item is a live folder.
         * <P>Type: INTEGER</P>
         *
         * @see android.provider.LiveFolders#DISPLAY_MODE_GRID
         * @see android.provider.LiveFolders#DISPLAY_MODE_LIST
         */
        public static final String DISPLAY_MODE = "displayMode";
        
        /*RK_ID: RK_TRASH . AUT: zhanggx1 . DATE: 2012-04-05 . S*/
        /**
         * The last time the shortcut used
         * <P>Type: INTEGER</P>
         */
        public static final String LAST_USE_TIME = "lastUseTime";
        /*RK_ID: RK_TRASH . AUT: zhanggx1 . DATE: 2012-04-05 . E*/
        
        /** ID: configurable widget restore . AUT: chengliang . DATE: 2012.05.31 . S */
        public static final String CONFIGABLE_WIDGET = "needConfig";
        /** ID: configurable widget restore . AUT: chengliang . DATE: 2012.05.31 . E */
    }

    /* AUT: liuli liuli0120@use.com.cn DATE: 2012-01-30 START */
    /**
     * Applications.
     */
    public static final class Applications implements BaseLauncherColumns {
        /**
         * The content:// style URL for this table
         */
    	public static final Uri CONTENT_URI = Uri.parse("content://" + Constants.AUTHORITY + "/"
                + Constants.TABLE_APPLICATIONS);

        /**
         * The launcher activity's label of the application
         * <P>Type: TEXT</P>
         */
        public static final String LABEL = "label";

        /**
         * The class name of the application
         * <P>Type: TEXT</P>
         */
        public static final String CLASS = "class";

        /**
         * The index of the cell holding the application
         * <P>Type: INTEGER</P>
         */
        public static final String CELL_INDEX = "cellIndex";

        /**
         * Could the application be dragging ?
         * 0 : not support  1: can drag
         * <P>Type: INTEGER</P>
         */
        public static final String APP_CAN_DRAG = "canDrag";
        
        /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. START***/
        /**
         * Set the enabled state of this application, just in LeLauncher.
         * 0 : invisible  1 : visible.
         * <P>Type: INTEGER</P>
         */
        static final String APP_HIDDEN = "hidden";
        /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. END***/
    }
    /* AUT: liuli liuli0120@use.com.cn DATE: 2012-01-30 END */
}
