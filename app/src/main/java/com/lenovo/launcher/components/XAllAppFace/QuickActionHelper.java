/*
 * Copyright (C) 2012
 *
 * RK_ID: RK_QUICKACTION. AUT: liuli1. DATE: 2012-03-26
 */
package com.lenovo.launcher.components.XAllAppFace;

import android.graphics.Bitmap;
import android.widget.Toast;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher2.commoninterface.FolderInfo;
import com.lenovo.launcher2.commoninterface.IconCache;
import com.lenovo.launcher2.commoninterface.ShortcutInfo;

public class QuickActionHelper {
    private static String sFilePath;
    
    public static void setFilePath(String path) {
        sFilePath = path;
    }

    public static String getFilePath() {
        return sFilePath;
    }

    /*public static void updateIconAndSyncDb(XLauncher context, Bitmap bitmap, View view) {
        if (view instanceof BubbleTextView) {
            BubbleTextView shortcutView = (BubbleTextView) view;
            ShortcutInfo info = (ShortcutInfo) shortcutView.getTag();
            if (bitmap != null) {
                //context.destroyCellLayoutHardwareLayers(info.screen);

                Bitmap bitmapWithStyle = Utilities.createIconBitmap(new BitmapDrawable(context.getResources(), bitmap),
                        context, infoFromShortcutIntent(info.intent));
                shortcutView.setCompoundDrawablesWithIntrinsicBounds(null, new FastBitmapDrawable(bitmapWithStyle),
                        null, null);

                info.setReplaceIcon(bitmap);
                //context.enableCelllayoutHardwareLayers(info.screen);

                XLauncherModel.updateItemInDatabase(context, info);
                Toast.makeText(context, R.string.quick_action_replace_success, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static String infoFromShortcutIntent(Intent data) {
        if (data.getComponent() == null) {
            // shortcut info
            Parcelable extra = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);
            if (extra != null && extra instanceof ShortcutIconResource) {
                ShortcutIconResource iconResource = (ShortcutIconResource) extra;
                return iconResource.packageName;
            }
        } else {
            return data.getComponent().getPackageName();
        }

        return Utilities.DEFAULT_PACKAGE;
    }*/

    public static void updateIconAndSyncDb(XLauncher context, Bitmap bitmap, DrawableItem view) {
        if (view instanceof XShortcutIconView) {
            XShortcutIconView shortcutView = (XShortcutIconView) view;
            ShortcutInfo info = (ShortcutInfo) shortcutView.getTag();
            if (bitmap != null) {
//                context.destroyCellLayoutHardwareLayers(info.screen);
//
//                Bitmap bitmapWithStyle = Utilities.createIconBitmap(new BitmapDrawable(context.getResources(), bitmap),
//                        context, infoFromShortcutIntent(info.intent));
//                shortcutView.setCompoundDrawablesWithIntrinsicBounds(null, new FastBitmapDrawable(bitmapWithStyle),
//                        null, null);
//
                info.setReplaceIcon(bitmap);
                IconCache iconCache = ((LauncherApplication) context.getApplicationContext())
                        .getIconCache();
                shortcutView.applyFromShortcutInfo(info, iconCache);
//                context.enableCelllayoutHardwareLayers(info.screen);
//
                XLauncherModel.updateItemInDatabase(context, info);
                Toast.makeText(context, R.string.quick_action_replace_success, Toast.LENGTH_SHORT).show();
            }
        } else if (view instanceof XFolderIcon) {
            XFolderIcon folderView = (XFolderIcon) view;
            FolderInfo folderInfo = folderView.mInfo;
            if (bitmap != null) {
//                context.destroyCellLayoutHardwareLayers(folderInfo.screen);

                folderInfo.mReplaceIcon = bitmap;
                folderView.updateFolderPreviewBackground();

//                context.enableCelllayoutHardwareLayers(folderInfo.screen);

                XLauncherModel.updateItemInDatabase(context, folderInfo);
                Toast.makeText(context, R.string.quick_action_replace_success, Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (view instanceof XIconDrawable) {
            XIconDrawable hotseatView = (XIconDrawable) view;
            ShortcutInfo info = (ShortcutInfo) hotseatView.getTag();
            if (bitmap != null) {
                info.setReplaceIcon(bitmap);

                // ui
                IconCache iconCache = ((LauncherApplication) context.getApplicationContext())
                        .getIconCache();
                context.getHotseat().bindInfo(info, iconCache);

                XLauncherModel.updateItemInDatabase(context, info);
                Toast.makeText(context, R.string.quick_action_replace_success, Toast.LENGTH_SHORT)
                        .show();
            }
        }

        view = null;
    }
}
