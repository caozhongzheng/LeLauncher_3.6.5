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

package com.lenovo.launcher.components.XAllAppFace;

import java.lang.ref.WeakReference;

import android.app.Application;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.os.Handler;

import com.lenovo.feedback2.agent.FeedBackAgent;
import com.lenovo.launcher2.LauncherProvider;
import com.lenovo.launcher2.commoninterface.HiddenApplist;
import com.lenovo.launcher2.commoninterface.IconCache;
import com.lenovo.launcher2.commoninterface.LauncherContext;
import com.lenovo.launcher2.commoninterface.LauncherSettings;
import com.lenovo.launcher2.commoninterface.LauncherSettings.Favorites;
import com.lenovo.launcher2.customizer.SettingsValue;

public class LauncherApplication extends Application {
    public XLauncherModel mModel;
    public IconCache mIconCache;
    private static boolean sIsScreenLarge;
    private static float sScreenDensity;
    WeakReference<LauncherProvider> mLauncherProvider;
    /*RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2012-08-07 . S*/
    public LauncherContext mLauncherContext;
    /*RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2012-08-07 . E*/

    @Override
    public void onCreate() {
    	SettingsValue.initImportantValues(this);
    	
    	FeedBackAgent.init(this);
        super.onCreate();

        // set sIsScreenXLarge and sScreenDensity *before* creating icon cache
        final int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        sIsScreenLarge = screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE ||
            screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE;
        sScreenDensity = getResources().getDisplayMetrics().density;

        mIconCache = new IconCache(this);
        mModel = new XLauncherModel(this, mIconCache);

        // Register intent receivers
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");
        registerReceiver(mModel, filter);
        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
        filter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);
        filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
        /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. START***/
        filter.addAction(HiddenApplist.ACTION_SET_APP_HIDDEN);
        /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. END***/
        
        registerReceiver(mModel, filter);
        filter = new IntentFilter();
        filter.addAction(SearchManager.INTENT_GLOBAL_SEARCH_ACTIVITY_CHANGED);
        registerReceiver(mModel, filter);
        filter = new IntentFilter();
        filter.addAction(SearchManager.INTENT_ACTION_SEARCHABLES_CHANGED);
        registerReceiver(mModel, filter);

        // Register for changes to the favorites
        ContentResolver resolver = getContentResolver();
        resolver.registerContentObserver(LauncherSettings.Favorites.CONTENT_URI, true,
                mFavoritesObserver);
        
        // for profile, device storage
        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_DEVICE_STORAGE_LOW);
        filter.addAction(Intent.ACTION_DEVICE_STORAGE_OK);
        registerReceiver(mModel, filter);
        
        /*RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2012-08-07 . S*/
        mLauncherContext = new LauncherContext(this);
        /*RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2012-08-07 . E*/
    }

    /**
     * There's no guarantee that this function is ever called.
     */
    @Override
    public void onTerminate() {
        super.onTerminate();

        unregisterReceiver(mModel);

        ContentResolver resolver = getContentResolver();
        resolver.unregisterContentObserver(mFavoritesObserver);
    }

    /**
     * Receives notifications whenever the user favorites have changed.
     */
    private final ContentObserver mFavoritesObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            mModel.startLoader(LauncherApplication.this, false);
        }
    };

    public XLauncherModel setLauncher(XLauncher launcher) {
        mModel.initialize(launcher);
        return mModel;
    }

    public IconCache getIconCache() {
        return mIconCache;
    }

    public XLauncherModel getModel() {
        return mModel;
    }

    public void setLauncherProvider(LauncherProvider provider) {
        mLauncherProvider = new WeakReference<LauncherProvider>(provider);
    }

    public LauncherProvider getLauncherProvider() {
        return mLauncherProvider.get();
    }

    public static boolean isScreenLarge() {
        return sIsScreenLarge;
    }

    public static boolean isScreenLandscape(Context context) {
        return context.getResources().getConfiguration().orientation ==
            Configuration.ORIENTATION_LANDSCAPE;
    }

    public static float getScreenDensity() {
        return sScreenDensity;
    }

    /*** MODIFYBY: zhaoxy . DATE: 2012-03-20 . START***/
    //The Source bellow has been moved to HawaiiHelp.java
    // liuli1 for hawaii
    /*private static boolean sIsHawaiiOpen;
    public static final String HAWAII_PKGNAME = "com.lenovo.leos.hw";

    public static boolean isHawaiiOpen() {
        return sIsHawaiiOpen;
    }

    private boolean initHawaii() {
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);

            // we find hawaii package, then retrieve its settings.
            if (HAWAII_PKGNAME.equals(packageInfo.packageName)
             && AppDataAdapter.getInstance(this).getHawaiiSettings() ) {
                return true;
            }
        }

        return false;
    }

    public void setHawaii(boolean on) {
        sIsHawaiiOpen = on;
    }

    private static final int OP_ADD = 1;
    private static final int OP_REMOVE = 3; // uninstlled
    private static final int OP_UNAVAILABLE = 4;

    public void bindHawaii(int op, String packageName) {
        if (!packageName.equals(HAWAII_PKGNAME))
            return;

        switch (op) {
        case OP_ADD:
            // if (getHawaiiSettings())
            sIsHawaiiOpen = true;
            break;

        case OP_REMOVE:
        case OP_UNAVAILABLE:
            sIsHawaiiOpen = false;
            break;
        }
    }*/
    // for hawaii end
    /*** MODIFYBY: zhaoxy . DATE: 2012-03-20 . END***/
}
