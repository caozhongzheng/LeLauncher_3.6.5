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

import java.util.ArrayList;

import com.lenovo.launcher2.commoninterface.LauncherSettings.BaseLauncherColumns;
import com.lenovo.launcher2.commoninterface.LauncherSettings.Favorites;
import com.lenovo.launcher2.customizer.Utilities;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Parcelable;
import android.util.Log;

/**
 * Represents a launchable icon on the workspaces and in folders.
 */
public class ShortcutInfo extends ShowStringInfo {

    /**
     * The application name.
     */
	public CharSequence title;

    /**
     * The intent used to start the application.
     */
	public Intent intent;

    /**
     * Indicates whether the icon comes from an application's resource (if false)
     * or from a custom Bitmap (if true.)
     */
	public boolean customIcon;

    /**
     * Indicates whether we're using the default fallback icon instead of something from the
     * app.
     */
	public boolean usingFallbackIcon;

    /**
     * If isShortcut=true and customIcon=false, this contains a reference to the
     * shortcut icon as an application's resource.
     */
	public Intent.ShortcutIconResource iconResource;

    /**
     * The application icon.
     */
    private Bitmap mIcon;

    /* AUT: liuli liuli0120@use.com.cn DATE: 2012-01-29 START, add iconReplace*/
    /**
     * user customize icon.
     */
    private Bitmap mReplaceIcon;

    /**
     * user customize title.
     */
    public CharSequence replaceTitle;
    /* AUT: liuli liuli0120@use.com.cn DATE: 2012-01-29 END */
    
    /*RK_ID: RK_DOCK_ICON . AUT: zhanggx1 . DATE: 2012-07-10 . S*/
    public Bitmap mSmallIcon;
    /*RK_ID: RK_DOCK_ICON . AUT: zhanggx1 . DATE: 2012-07-10 . E*/

    public ShortcutInfo() {
        itemType = LauncherSettings.BaseLauncherColumns.ITEM_TYPE_SHORTCUT;
    }
    
    public ShortcutInfo(ShortcutInfo info) {
        super(info);
        title = info.title.toString();
        intent = new Intent(info.intent);
        if (info.iconResource != null) {
            iconResource = new Intent.ShortcutIconResource();
            iconResource.packageName = info.iconResource.packageName;
            iconResource.resourceName = info.iconResource.resourceName;
        }
        mIcon = info.mIcon; // TODO: should make a copy here.  maybe we don't need this ctor at all
        customIcon = info.customIcon;
        mReplaceIcon = info.mReplaceIcon;
        replaceTitle = info.replaceTitle;
        mSmallIcon = info.mSmallIcon;
        mNewAdd = info.mNewAdd;
        mNewString = info.mNewString;
    }

    /** TODO: Remove this.  It's only called by ApplicationInfo.makeShortcut. */
    public ShortcutInfo(ApplicationInfo info) {
        super(info);
        title = info.title.toString();
        intent = new Intent(info.intent);
        customIcon = false;
    }

    public void setIcon(Bitmap b) {
        mIcon = b;
    }

    /* RK_ID: RK_QUICKACTION. AUT: liuli1 . DATE: 2012-05-24 . START */
    public void setReplaceIcon(Bitmap b) {
        mReplaceIcon = b;
    }

    public Bitmap getRelaceIcon() {
        return mReplaceIcon;
    }
    /* RK_ID: RK_QUICKACTION. AUT: liuli1 . DATE: 2012-05-24 . END */

    public Bitmap getIcon(IconCache iconCache) {
        if (mIcon == null) {
            mIcon = iconCache.getIcon(this.intent);
            this.usingFallbackIcon = iconCache.isDefaultIcon(mIcon);
        }
        return mIcon;
    }

    /*
     * AUT: liuli liuli0120@use.com.cn DATE: 2012-01-29 START
     * get shortcut icon
     * if replaced icon, return user's customize icon, including theme icon or gallery crop one
     * else return application original icon.
     */
    public Bitmap getIcon(IconCache iconCache, boolean replaced) {
        if (replaced && mReplaceIcon != null) {
            Context c = iconCache.getLauncherApplication();
            return Utilities.createIconBitmap(new BitmapDrawable(c.getResources(), mReplaceIcon),
                    c, infoFromShortcutIntent(intent));
        } else if (customIcon) {
            Context c = iconCache.getLauncherApplication();
            return Utilities.createIconBitmap(new BitmapDrawable(c.getResources(), getIcon(iconCache)),
                    c, infoFromShortcutIntent(intent));
        } else {
            return getIcon(iconCache);
        }
    }

    // add for lenovo background
    String infoFromShortcutIntent(Intent data) {
        if (data == null) {
        } else if (data.getComponent() == null) {
            Log.i("Test0229", "intent = " + data.toString());
            // shortcut info
            Parcelable extra = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);
            if (extra != null && extra instanceof ShortcutIconResource) {
                ShortcutIconResource iconResourceTemp = (ShortcutIconResource) extra;
                return iconResourceTemp.packageName;
            }
        } else {
            Log.i("Test0229", "intent component = " + data.getComponent().toString());
            return data.getComponent().getPackageName();
        }

        return Utilities.DEFAULT_PACKAGE;
    }
    /* AUT: liuli liuli0120@use.com.cn DATE: 2012-01-29 END */

    /**
     * Creates the application intent based on a component name and various launch flags.
     * Sets {@link #itemType} to {@link LauncherSettings.BaseLauncherColumns#ITEM_TYPE_APPLICATION}.
     *
     * @param className the class name of the component representing the intent
     * @param launchFlags the launch flags
     */
    public final void setActivity(ComponentName className, int launchFlags) {
        intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(className);
        intent.setFlags(launchFlags);
        itemType = LauncherSettings.BaseLauncherColumns.ITEM_TYPE_APPLICATION;
    }

    @Override
    public void onAddToDatabase(ContentValues values) {
        super.onAddToDatabase(values);

        String titleStr = title != null ? title.toString() : null;
        values.put(LauncherSettings.BaseLauncherColumns.TITLE, titleStr);

        String intentStr = intent != null ? intent.toUri(0) : null;
        values.put(LauncherSettings.BaseLauncherColumns.INTENT, intentStr);
        
        /*RK_ID:RK_SD_APPS zhangdxa 2013-4-15. S***/
        String uriStr = uri != null ? uri.toString() : null;
        values.put(LauncherSettings.Favorites.URI, uriStr);
        /*RK_ID:RK_SD_APPS zhangdxa 2013-4-15. E***/

        if (customIcon) {
            values.put(LauncherSettings.BaseLauncherColumns.ICON_TYPE,
                    LauncherSettings.BaseLauncherColumns.ICON_TYPE_BITMAP);
            writeBitmap(values, mIcon);
        } else {
            if (!usingFallbackIcon) {
                writeBitmap(values, mIcon);
            }
            values.put(LauncherSettings.BaseLauncherColumns.ICON_TYPE,
                    LauncherSettings.BaseLauncherColumns.ICON_TYPE_RESOURCE);
            if (iconResource != null) {
                values.put(LauncherSettings.BaseLauncherColumns.ICON_PACKAGE,
                        iconResource.packageName);
                values.put(LauncherSettings.BaseLauncherColumns.ICON_RESOURCE,
                        iconResource.resourceName);
            }
        }

        /* AUT: liuli liuli0120@use.com.cn DATE: 2012-01-29 START */
        writeReplaceBitmap(values, mReplaceIcon);
        if (replaceTitle != null) {
            values.put(LauncherSettings.Favorites.TITLE_REPLACE, replaceTitle.toString());
        }
        /* AUT: liuli liuli0120@use.com.cn DATE: 2012-01-29 END */
    }

    @Override
    public String toString() {
        /* RK_ID: RK_QUICKACTION. AUT: liuli1 . DATE: 2012-05-17 . START */
//        return "ShortcutInfo(title=" + title.toString() + ")";
        StringBuffer print = new StringBuffer("ShortcutInfo ");
        print.append(" screen = ").append(screen);
        print.append(" cellX = ").append(cellX);
        print.append(" cellY = ").append(cellY);
        print.append(" title = ").append(title != null ? title.toString() : "");
        print.append(" ").append(this.hashCode());
        return print.toString();
        /* RK_ID: RK_QUICKACTION. AUT: liuli1 . DATE: 2012-05-17 . END */
    }

    public static void dumpShortcutInfoList(String tag, String label,
            ArrayList<ShortcutInfo> list) {
        Log.d(tag, label + " size=" + list.size());
        for (ShortcutInfo info: list) {
            Log.d(tag, "   title=\"" + info.title + " icon=" + info.mIcon
                    + " customIcon=" + info.customIcon);
        }
    }

    /*** fixbug 191242  . AUT: zhaoxy . DATE: 2012-10-12. START***/
    public boolean equalsPosition(ShortcutInfo info) {
        if (info != null
                // added by liuli1, fix bug 171746
                && this.container == info.container) {
            return this.screen == info.screen && this.cellX == info.cellX && this.cellY == info.cellY;
        }
        return false;
    }

    public boolean equalsIgnorePosition(ShortcutInfo info) {
        if (info != null && this.intent != null) {
            return this.intent.filterEquals(info.intent);
        }
        return false;
    }
    /*** fixbug 191242  . AUT: zhaoxy . DATE: 2012-10-12. END***/
}

