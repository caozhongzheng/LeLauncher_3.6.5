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

import java.util.HashMap;
import java.util.List;
 
import android.app.ActivityManager;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;
import com.lenovo.launcher2.customizer.Utilities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * Cache of application icons.  Icons can be made from any thread.
 */
public class IconCache {
    private static final String TAG = "Launcher.IconCache";

    private static final int INITIAL_ICON_CACHE_CAPACITY = 50;

    private static class CacheEntry {
        public Bitmap icon;
        public String title;
    }

    private final Bitmap mDefaultIcon;
    private final LauncherApplication mContext;
    private final PackageManager mPackageManager;
    private final HashMap<ComponentName, CacheEntry> mCache =
            new HashMap<ComponentName, CacheEntry>(INITIAL_ICON_CACHE_CAPACITY);
    private int mIconDpi;

    public IconCache(LauncherApplication context) {
        mContext = context;
        mPackageManager = context.getPackageManager();
        ActivityManager activityManager =
              (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);


        int density = context.getResources().getDisplayMetrics().densityDpi;
/*      //removed by yumina 2013-07-04 megerd from 4.2.1 for s5000 crashed
        if (LauncherApplication.isScreenLarge()) {
            if (density == DisplayMetrics.DENSITY_LOW) {
                mIconDpi = DisplayMetrics.DENSITY_MEDIUM;
            } else if (density == DisplayMetrics.DENSITY_MEDIUM) {
                mIconDpi = DisplayMetrics.DENSITY_HIGH;
            } else if (density == DisplayMetrics.DENSITY_HIGH) {
                mIconDpi = DisplayMetrics.DENSITY_XHIGH;
            } else if (density == DisplayMetrics.DENSITY_XHIGH) {
                // We'll need to use a denser icon, or some sort of a mipmap
                mIconDpi = DisplayMetrics.DENSITY_XHIGH;
            }
        } else {
            mIconDpi = context.getResources().getDisplayMetrics().densityDpi;
        }
*/
        mIconDpi = activityManager.getLauncherLargeIconDensity();

        // need to set mIconDpi before getting default icon
        mDefaultIcon = makeDefaultIcon();
    }

    public Drawable getFullResDefaultActivityIcon() {
    	/*RK_ID:RK_SD_APPS zhangdxa 2013-8-12. S***/
        return getFullResIcon(Resources.getSystem(),
                com.android.internal.R.mipmap.sym_def_app_icon);
    	//return mContext.getResources().getDrawable(	R.drawable.commend_app);
    	/*RK_ID:RK_SD_APPS zhangdxa 2013-8-12. E***/
    }

    public Drawable getFullResIcon(Resources resources, int iconId) {
        Drawable d;
        try {
            d = resources.getDrawableForDensity(iconId, mIconDpi);
        } catch (Resources.NotFoundException e) {
            d = null;
        }

        return (d != null) ? d : getFullResDefaultActivityIcon();
    }

    public Drawable getFullResIcon(String packageName, int iconId) {
        Resources resources;
        try {
            resources = mPackageManager.getResourcesForApplication(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            resources = null;
        }
        if (resources != null) {
            if (iconId != 0) {
                return getFullResIcon(resources, iconId);
            }
        }
        return getFullResDefaultActivityIcon();
    }

    public Drawable getFullResIcon(ResolveInfo info) {
        Resources resources;
//        try {
//            resources = mPackageManager.getResourcesForApplication(
//                    info.activityInfo.applicationInfo);
//        } catch (PackageManager.NameNotFoundException e) {
//            resources = null;
//        }
        /*zhanglq@bj.cobellink.com DATA 2012-06-28 S*/
//        if(resources == null){
        	try {
				resources = mPackageManager.getResourcesForApplication(info.activityInfo.applicationInfo.packageName);
			} catch (NameNotFoundException e) {
				resources = null;
			}
//        }
        /*zhanglq@bj.cobellink.com DATA 2012-06-28 E*/
        if (resources != null) {
            int iconId = info.activityInfo.getIconResource();
            if (iconId != 0) {
                return getFullResIcon(resources, iconId);
            }
        }
        return getFullResDefaultActivityIcon();
    }

    private Bitmap makeDefaultIcon() {
    	/*RK_ID:RK_SD_APPS zhangdxa 2013-8-12. S***/
        //Drawable d = mContext.getResources().getDrawable(	R.drawable.commend_app);
        Drawable d = getFullResIcon(Resources.getSystem(),
                com.android.internal.R.mipmap.sym_def_app_icon);
        BitmapDrawable bd = (BitmapDrawable)d;
        Bitmap b = bd.getBitmap();
        /*Bitmap b = Bitmap.createBitmap(Math.max(d.getIntrinsicWidth(), 1),
                Math.max(d.getIntrinsicHeight(), 1),
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        d.setBounds(0, 0, b.getWidth(), b.getHeight());
        d.draw(c);
        c.setBitmap(null);*/
        /*RK_ID:RK_SD_APPS zhangdxa 2013-8-12. E***/
        return b;
    }

    /**
     * Remove any records for the supplied ComponentName.
     */
    public void remove(ComponentName componentName) {
        synchronized (mCache) {
            mCache.remove(componentName);
        }
    }

    /**
     * Empty out the cache.
     */
    public void flush() {
        synchronized (mCache) {
            mCache.clear();
        }
    }

    /**
     * Fill in "application" with the icon and label for "info."
     */
    public void getTitleAndIcon(ApplicationInfo application, ResolveInfo info,
            HashMap<Object, CharSequence> labelCache) {
        synchronized (mCache) {
            CacheEntry entry = cacheLocked(application.componentName, info, labelCache);
            if(entry == null) return;

            /*** fixbug 165534 . AUT: zhaoxy . DATE: 2012-05-31. START***/
            application.title = entry.title.replaceAll("\\s+", " ").trim();
            /*** fixbug 165534 . AUT: zhaoxy . DATE: 2012-05-31. END***/
            application.iconBitmap = entry.icon;
        }
    }

    public Bitmap getIcon(Intent intent) {
        synchronized (mCache) {
//            final ResolveInfo resolveInfo = mPackageManager.resolveActivity(intent, 0);
            ComponentName component = intent.getComponent();

            if (/*resolveInfo == null || */component == null) {
                return mDefaultIcon;
            }

            String packageName = component.getPackageName();
            String classname = component.getClassName();

            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            mainIntent.setPackage(packageName);

            ResolveInfo resolveInfo = null;
            List<ResolveInfo> apps = mPackageManager.queryIntentActivities(mainIntent, 0);

            if (apps != null && apps.size() > 0) {
                int size = apps.size();

                for (int index = 0; index < size; index++) {
                    ResolveInfo temp = apps.get(index);
                    if (temp.activityInfo.name.equals(classname)) {
                        resolveInfo = temp;
                        break;
                    }
                } // end for
            }
            
            if (resolveInfo == null) {
                resolveInfo = mPackageManager.resolveActivity(intent, PackageManager.GET_RESOLVED_FILTER);
            }
            
            if (resolveInfo == null) {
                return mDefaultIcon;
            }

            CacheEntry entry = cacheLocked(component, resolveInfo, null);
            return entry.icon;
        }
    }

    public Bitmap getIcon(ComponentName component, ResolveInfo resolveInfo,
            HashMap<Object, CharSequence> labelCache) {
        synchronized (mCache) {
            if (resolveInfo == null || component == null) {
                return null;
            }

            CacheEntry entry = cacheLocked(component, resolveInfo, labelCache);
            return entry.icon;
        }
    }

    public boolean isDefaultIcon(Bitmap icon) {
        return mDefaultIcon.equals(icon);
    }

    private CacheEntry cacheLocked(ComponentName componentName, ResolveInfo info,
            HashMap<Object, CharSequence> labelCache) {
        CacheEntry entry = mCache.get(componentName);
        if (entry == null) {
            entry = new CacheEntry();

            mCache.put(componentName, entry);

            ComponentName key = Utilities.getComponentNameFromResolveInfo(info);
            if (labelCache != null && labelCache.containsKey(key)) {
                entry.title = labelCache.get(key).toString();
            } else {
                entry.title = info.loadLabel(mPackageManager).toString();
                if (labelCache != null) {
                    labelCache.put(key, entry.title);
                }
            }
//            if (entry.title == null) {
//                entry.title = info.activityInfo.name;
//            }

//            entry.icon = Utilities.createIconBitmap(
//                    getFullResIcon(info, mPackageManager), mContext);

            /* AUT: liuli liuli0120@use.com.cn DATE: 2011-12-20 START */
            // for icon style 
            entry.icon = mContext.mLauncherContext.getIconBitmap(info, componentName.getPackageName());
            /* AUT: liuli liuli0120@use.com.cn DATE: 2011-12-20 END */
        }
        return entry;
    }

    /* AUT: liuli liuli0120@use.com.cn DATE: 2011-12-20 START */
    public Context getLauncherApplication() {
        return mContext;
    }

    // fix bug 164488
    public Drawable getFullResIcon(android.content.pm.ActivityInfo info, PackageManager packageManager) {
        Resources resources;
        try {
            resources = packageManager.getResourcesForApplication(info.applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            resources = null;
        }
        if (resources != null) {
            int iconId = info.getIconResource();
            if (iconId != 0) {
                return getFullResIcon(resources, iconId);
            }
        }
        return getFullResDefaultActivityIcon();
    }
    /* AUT: liuli liuli0120@use.com.cn DATE: 2011-12-20 END */

    public HashMap<ComponentName,Bitmap> getAllIcons() {
        synchronized (mCache) {
            HashMap<ComponentName,Bitmap> set = new HashMap<ComponentName,Bitmap>();
            int i = 0;
            for (ComponentName cn : mCache.keySet()) {
                final CacheEntry e = mCache.get(cn);
                set.put(cn, e.icon);
            }
            return set;
        }
    }
}
