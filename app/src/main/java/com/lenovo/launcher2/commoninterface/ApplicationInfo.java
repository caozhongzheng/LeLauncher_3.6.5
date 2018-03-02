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
import java.util.HashMap;
import java.util.HashSet;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.provider.Settings;
import android.util.Log;

import com.lenovo.launcher2.customizer.HanziToPinyin;
import com.lenovo.launcher2.customizer.Debug.R5;
import com.lenovo.launcher2.customizer.HanziToPinyin.Token;

/**
 * Represents an app in AllAppsView.
 */
public class ApplicationInfo extends ShowStringInfo {
    private static final String TAG = "Launcher2.ApplicationInfo";

    /**
     * The application name.
     */
    public CharSequence title;

    /**
     * The intent used to start the application.
     */
    public Intent intent;

    /**
     * A bitmap version of the application icon.
     */
    public Bitmap iconBitmap;

    /**
     * The time at which the app was first installed.
     */
    public long firstInstallTime;

    public ComponentName componentName;

    /* AUT: liuli liuli0120@use.com.cn DATE: 2011-12-20 START */
    /**
     * A bitmap customized of the application icon.
     */
	 /***RK_ID:RK_PICKLIST_UPDATE_1744 AUT:zhanglz1@lenovo.com. ***/  

    public boolean canDrag;
    /* AUT: liuli liuli0120@use.com.cn DATE: 2011-12-20 END */
    /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. START***/
    public boolean hidden;

    public String[] sortKey;
    /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. END***/

    static final int DOWNLOADED_FLAG = 1;
    static final int UPDATED_SYSTEM_APP_FLAG = 2;

    public int flags = 0;

    public ApplicationInfo() {
        itemType = LauncherSettings.BaseLauncherColumns.ITEM_TYPE_SHORTCUT;
        canDrag = true;
        /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. START***/
        hidden = false;
        sortKey = new String[0];
        /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. END***/
    }

    /**
     * Must not hold the Context.
     */
    public ApplicationInfo(PackageManager pm, ResolveInfo info, IconCache iconCache,
            HashMap<Object, CharSequence> labelCache) {
        final String packageName = info.activityInfo.applicationInfo.packageName;

        this.componentName = new ComponentName(packageName, info.activityInfo.name);
        this.container = ItemInfo.NO_ID;
        this.setActivity(componentName,
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        try {
            int appFlags = pm.getApplicationInfo(packageName, 0).flags;
            if ((appFlags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) == 0) {
                flags |= DOWNLOADED_FLAG;

                /* RK_ID: RK_UNINSTALLAPP. AUT: liuli1 . DATE: 2012-06-07 . START */
//                if ((appFlags & android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
//                    flags |= UPDATED_SYSTEM_APP_FLAG;
//                }
            }
            if ((appFlags & android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                flags |= UPDATED_SYSTEM_APP_FLAG;
            }
            /* RK_ID: RK_UNINSTALLAPP. AUT: liuli1 . DATE: 2012-06-07 . END */
            /*** RK_ID: APP_SORT. AUT: zhaoxy . DATE: 2012-05-09. START***/
            firstInstallTime = pm.getPackageInfo(packageName, 0).lastUpdateTime;//firstInstallTime;
            /*** RK_ID: APP_SORT. AUT: zhaoxy . DATE: 2012-05-09. END***/
        } catch (NameNotFoundException e) {
            Log.d(TAG, "PackageManager.getApplicationInfo failed for " + packageName);
        }

        iconCache.getTitleAndIcon(this, info, labelCache);
        // reload label
        if (labelCache != null && labelCache.containsKey(this.componentName)) {
            this.title = labelCache.get(this.componentName);
        } else {
            this.title = info.activityInfo.loadLabel(pm);
            if (labelCache != null) {
                labelCache.put(this.componentName, this.title);
            }
        }
        canDrag = true;
        /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. START***/
        hidden = false;
        if (title != null) {
            ArrayList<HanziToPinyin.Token> tokens = HanziToPinyin.getInstance().get(title.toString());
            int size = tokens.size();
            sortKey = new String[size];
            for (int i = 0; i < size; i++) {
                HanziToPinyin.Token token = tokens.get(i);
                if (HanziToPinyin.Token.PINYIN == token.type) {
                    sortKey[i] = token.target;
                } else {
                    sortKey[i] = token.source.substring(0, 1);
                }
            }
        }
        /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. END***/
        
        mLookupKeys = HanziToPinyin.getInstance().getNameLookupKeys(title == null ? "" : title.toString());
    }

    public ApplicationInfo(ApplicationInfo info) {
        super(info);
        componentName = info.componentName;
        title = info.title.toString();
        intent = new Intent(info.intent);
        flags = info.flags;
        firstInstallTime = info.firstInstallTime;
        mNewAdd = info.mNewAdd;
        mNewString = info.mNewString;
    }

    /**
     * Creates the application intent based on a component name and various launch flags.
     * Sets {@link #itemType} to {@link LauncherSettings.BaseLauncherColumns#ITEM_TYPE_APPLICATION}.
     *
     * @param className the class name of the component representing the intent
     * @param launchFlags the launch flags
     */
    final void setActivity(ComponentName className, int launchFlags) {
        intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(className);
        intent.setFlags(launchFlags);
        itemType = LauncherSettings.BaseLauncherColumns.ITEM_TYPE_APPLICATION;
    }

    @Override
    public String toString() {
        return "ApplicationInfo(title=" + title.toString() + ")";
    }

    public static void dumpApplicationInfoList(String tag, String label,
            ArrayList<ApplicationInfo> list) {
        Log.d(tag, label + " size=" + list.size());
        for (ApplicationInfo info: list) {
            Log.d(tag, "   title=\"" + info.title + "\" iconBitmap="
                    + info.iconBitmap + " firstInstallTime="
                    + info.firstInstallTime);
        }
    }

    public ShortcutInfo makeShortcut() {
        return new ShortcutInfo(this);
    }
    
    public HashSet<String> mLookupKeys;    
}
