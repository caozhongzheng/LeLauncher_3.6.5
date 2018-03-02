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
import java.net.URISyntaxException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import android.app.SearchManager;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Parcelable;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.InstallWidgetReceiver.WidgetMimeTypeHandlerData;
import com.lenovo.launcher.components.XAllAppFace.utilities.Debug.R2;
import com.lenovo.launcher2.LauncherProvider;
import com.lenovo.launcher2.bootpolicy.LoadBootPolicy;
import com.lenovo.launcher2.commoninterface.AllAppsList;
import com.lenovo.launcher2.commoninterface.ApplicationInfo;
import com.lenovo.launcher2.commoninterface.DeferredHandler;
import com.lenovo.launcher2.commoninterface.FolderInfo;
import com.lenovo.launcher2.commoninterface.HiddenApplist;
import com.lenovo.launcher2.commoninterface.IconCache;
import com.lenovo.launcher2.commoninterface.ItemInfo;
import com.lenovo.launcher2.commoninterface.LauncherAppWidgetInfo;
import com.lenovo.launcher2.commoninterface.LauncherService;
import com.lenovo.launcher2.commoninterface.LauncherSettings;
import com.lenovo.launcher2.commoninterface.LauncherSettings.Favorites;
import com.lenovo.launcher2.commoninterface.LenovoWidgetViewInfo;
import com.lenovo.launcher2.commoninterface.ShortcutInfo;
import com.lenovo.launcher2.commoninterface.ShowStringInfo;
import com.lenovo.launcher2.customizer.Debug.R5;
import com.lenovo.launcher2.customizer.Constants;
import com.lenovo.launcher2.customizer.Debug;
import com.lenovo.launcher2.customizer.ProgressDialog;
import com.lenovo.launcher2.customizer.RegularApplist;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.Utilities;
import com.lenovo.launcher2.weather.widget.settings.WeatherWidgetPosInfo;

/**
 * Maintains in-memory state of the Launcher. It is expected that there should be only one
 * LauncherModel object held in a static. Also provide APIs for updating the database state
 * for the Launcher.
 */
public class XLauncherModel extends BroadcastReceiver {
    /* RK_ID: RK_LAUNCHERMODEL. AUT: liuli1 . DATE: 2012-05-11 . START */
    static final boolean DEBUG_LOADERS = true;// false;
    /* RK_ID: RK_LAUNCHERMODEL. AUT: liuli1 . DATE: 2012-05-11 . END */
    static final String TAG = "XLauncher.Model";

    private static final int ITEMS_CHUNK = 6; // batch size for the workspace icons
    private final boolean mAppsCanBeOnExternalStorage;
    private int mBatchSize; // 0 is all apps at once
    private int mAllAppsLoadDelay; // milliseconds between batches

    private final LauncherApplication mApp;
    private final Object mLock = new Object();
    private DeferredHandler mHandler = new DeferredHandler();
    private LoaderTask mLoaderTask;
    /** AUT: zhanglq@bj.cobellink.com DATE: 2012-1-12 start*/
    private LauncherService mLauncherService = LauncherService.getInstance();
    /** AUT: zhanglq@bj.cobellink.com DATE: 2012-1-12 end*/
    
    /*** AUT:zhaoxy . DATE:2012-03-07 . START***/
    public static boolean isFirstLoad = false;
//    private UsageStatsMonitor usageStatsMonitor = null;
    private RegularApplist mRegularApplist = null;
    /*** AUT:zhaoxy . DATE:2012-03-07 . END***/
    /*AUT: zhanglq@bj.cobellink.com DATA 2012-03-21 start*/
    public static boolean isFinishLoad = true;
    /*AUT: zhanglq@bj.cobellink.com DATA 2012-03-21 end*/
    private final String LauncherPackageName = "com.lenovo.launcher";
    private final String LauncherThemePackage = "com.lenovo.launcher";//old is com.lenovo.launcher.theme
    
    private final String anotherLauncherPackage = "com.lenovo.xlauncher";
    
    private final String LockThemePackages = "com.qigame.lock.";
    
    /*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. S***/
	private static final String WEATHER_WIDGET_LOTUS = "com.lenovo.launcher2.weather.widget.lotus";
	private static final String WEATHER_WIDGET_TEA = "com.lenovo.launcher2.weather.widget.tea";
	private static final String WEATHER_WIDGET_AURORA = "com.lenovo.launcher2.weather.widget.aurora";
    /*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. E***/
    
    //protected List<ResolveInfo> mAppListCacheTempLenovo=null;

	/*PK_ID:LoadTask AUTH:GEN1 S*/
	//请不要在任何地方修改此值
    private static boolean isLoadTaskHasFirstLoaded = false;
	/*PK_ID:LoadTask AUTH:GEN1 E*/

    /** ID: fix bug: LELAUNCHER-89. AUT: zhaoxy . DATE: 2013.09.02 . S */
    private static boolean isDatabaseDirty = false;
    private final static Object mDirtyLock = new Object();

    public static void setDatabaseDirty(boolean isDirty) {
        synchronized (mDirtyLock) {
            isDatabaseDirty = isDirty;
        }
    }
    /** ID: fix bug: LELAUNCHER-89. AUT: zhaoxy . DATE: 2013.09.02 . E */

    private static final HandlerThread sWorkerThread = new HandlerThread("launcher-loader");
    static {
        sWorkerThread.start();
    }
    private static final Handler sWorker = new Handler(sWorkerThread.getLooper());

    /*** AUT: zhaoxy . DATE: 2012-05-08. START***/
    private static final HandlerThread sReceiverThread = new HandlerThread("launcher-receiver");
    static {
        sReceiverThread.start();
        sReceiverThread.setPriority(Thread.NORM_PRIORITY);
    }
    private static final Handler sReceiverWorker = new Handler(sReceiverThread.getLooper());
    
    // We start off with everything not loaded.  After that, we assume that
    // our monitoring of the package manager provides all updates and we never
    // need to do a requery.  These are only ever touched from the loader thread.
    private boolean mWorkspaceLoaded;
    private boolean mAllAppsLoaded;

    private WeakReference<Callbacks> mCallbacks;

    /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. START***/
    private HiddenApplist mHiddenApplist;
    /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. END***/

    // < only access in worker thread >
    private AllAppsList mAllAppsList;

    // sItemsIdMap maps *all* the ItemInfos (shortcuts, folders, and widgets) created by
    // LauncherModel to their ids
    public static final HashMap<Long, ItemInfo> sItemsIdMap = new HashMap<Long, ItemInfo>();

    // sItems is passed to bindItems, which expects a list of all folders and shortcuts created by
    //       LauncherModel that are directly on the home screen (however, no widgets or shortcuts
    //       within folders).
    static final ArrayList<ItemInfo> sWorkspaceItems = new ArrayList<ItemInfo>();
    static final ArrayList<ItemInfo> sHotseatItems = new ArrayList<ItemInfo>();
    // sAppWidgets is all LauncherAppWidgetInfo created by LauncherModel. Passed to bindAppWidget()
    static final ArrayList<LauncherAppWidgetInfo> sAppWidgets =
        new ArrayList<LauncherAppWidgetInfo>();

    // sFolders is all FolderInfos created by LauncherModel. Passed to bindFolders()
    static final HashMap<Long, FolderInfo> sFolders = new HashMap<Long, FolderInfo>();

    // sDbIconCache is the set of ItemInfos that need to have their icons updated in the database
    static final HashMap<Object, byte[]> sDbIconCache = new HashMap<Object, byte[]>();

    /* RK_ID: RK_ICONSTYLE. AUT: liuli1 . DATE: 2012-05-16 . START */
    private static final HashMap<Long, Bitmap> sViewIconCache = new HashMap<Long, Bitmap>();
    /* RK_ID: RK_ICONSTYLE. AUT: liuli1 . DATE: 2012-05-16 . END */
    
    /*PK_ID: RK_Loading all other apps excluded database AUT:GECN1 DATE:2013-06-14 S */
    //otherAppList will be cleared when finsh loading 
    LinkedList<ShortcutInfo> mOtherAppList = new LinkedList<ShortcutInfo>();
    /*PK_ID: RK_Loading all other apps excluded database AUT:GECN1 DATE:2013-06-14 E */
    // </ only access in worker thread >
    
  	/*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-09-26 START */  
    final ArrayList<LenovoWidgetViewInfo> mLeosWidgetViews = new ArrayList<LenovoWidgetViewInfo>();
    public static final ArrayList<LenovoWidgetViewInfo> sLeosWidgets = new ArrayList<LenovoWidgetViewInfo>();
	/*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-09-26 END */   
    private IconCache mIconCache;
    private Bitmap mDefaultIcon;

    private static int mCellCountX;
    private static int mCellCountY;

    protected int mPreviousConfigMcc;
    protected int mLoadMcc;
    
    /** ID: fix bug: leos40/170148. AUT: chengliang . DATE: 2012.05.17 . S */
    public boolean mLocaleJustChanged = false;
    /** ID: fix bug: leos40/170148. AUT: chengliang . DATE: 2012.05.17 . E */


    public interface Callbacks {
        public boolean setLoadOnResume();
        public int getCurrentWorkspaceScreen();
        public void startBinding();
        public void bindItems(ArrayList<ItemInfo> shortcuts, int start, int end);
        public void bindFolders(HashMap<Long,FolderInfo> folders);
        public void finishBindingItems();
        public void bindAppWidget(LauncherAppWidgetInfo info);
        public void bindAllApplications(ArrayList<ApplicationInfo> apps);
        public void bindAppsAdded(ArrayList<ApplicationInfo> apps);
//        public void bindAppsWorksapce(ArrayList<ApplicationInfo> apps);
//        public void bindAppsAvialiableAdded(ArrayList<ApplicationInfo> apps);
        public void bindAppsUpdated(ArrayList<ApplicationInfo> apps);
        public void bindAppsRemoved(ArrayList<ApplicationInfo> apps, boolean permanent);
        public void bindPackagesUpdated();
        public boolean isAllAppsVisible();
        public void bindSearchablesChanged();
        /*RK_ID: RK_DOCK_ADD . AUT: zhanggx1 . AUT: 2011-12-15 . S*/
        public void initDockAddIcons();
        public void initThemeElements();
        /*RK_ID: RK_DOCK_ADD . AUT: zhanggx1 . AUT: 2011-12-15 . E*/
        /* RK_ID: RK_PROFILE. AUT: liuli1 . DATE: 2012-02-29 . START */
        public void setProfileBackupEnable(String action);
        /* RK_ID: RK_PROFILE. AUT: liuli1 . DATE: 2012-02-29 . END */
      
        /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. START***/
        public void bindAppsHidden(ArrayList<ApplicationInfo> apps);
        /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. END***/
        /*RK_ID: RK_FIX_BUG . AUT: zhanggx1 . DATE: 2012-06-19 . PUR: stop appwidgetHost listening . S*/
        public void restartLauncher();
        /*RK_ID: RK_FIX_BUG . AUT: zhanggx1 . DATE: 2012-06-19 . PUR: stop appwidgetHost listening . E*/
  	    
	/*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-09-26 START */
    	 public void bindLeosWidget(LenovoWidgetViewInfo info);
	/*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-09-26 END */
    	/*RK_ID: MEM OPT . AUT: chengliang . DATE: 2011-07-23 . S*/
    	 public XLauncher getLauncherInstance();
    	/*RK_ID: MEM OPT . AUT: chengliang . DATE: 2011-07-23 . E*/
    	 
    	 /*RK_ID:RK_SD_APPS zhangdxa 2013-4-15. S***/
    	 public void updateItemsCommend(ArrayList<ItemInfo> listInfo, boolean bCommend);
    	 public void updateItemsFolderCommend(ArrayList<ItemInfo> listInfo, ArrayList<ItemInfo> listFolder, boolean bCommend);
    	 /*RK_ID:RK_SD_APPS zhangdxa 2013-4-15. E***/
    	 /*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. S***/
    	 public void removeWidgets(ArrayList<ItemInfo> listInfo);
    	 public void removeLeosWidgetInOtherPackage(ArrayList<ApplicationInfo> apps);
    	 public void addLeosWidgets(String packageName);
    	 /*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. E***/
    	 
    	 /*PK_ID:PK:BIND Other apps Auth gecn1 Date 2013-06-14 S*/
    	 public void bindOtherApps(LinkedList<ShortcutInfo> listInfo);
         /*PK_ID:PK:BIND Other apps Auth gecn1 Date 2013-06-14 E*/

    	 /** ID: fix bug: LELAUNCHER-89. AUT: zhaoxy . DATE: 2013.09.02 . S */
    	 public boolean isDragging();
    	 /** ID: fix bug: LELAUNCHER-89. AUT: zhaoxy . DATE: 2013.09.02 . E */
    	 /*add by zhanggx1 on 2013-10-17 for theme appling.s*/
    	 public void endThemeAppling(final ArrayList<ApplicationInfo> result, final String mFlag);
    	 public void changeIconStyle(final boolean draglayer, final boolean allapp, final boolean bitmapUpdate, final int currentIconStyle,final int currentIconSize);
    	 public void removeCheckedApp(final ArrayList<ShortcutInfo> infos);
    	 /*add by zhanggx1 on 2013-10-17 for theme appling.e*/
    	//add by zhanggx1 for reordering all pages on 2013-11-20. s
    	 public void autoReorder();
    	//add by zhanggx1 for reordering all pages on 2013-11-20. e    	 
    }

    public XLauncherModel(LauncherApplication app, IconCache iconCache) {
        mAppsCanBeOnExternalStorage = !Environment.isExternalStorageEmulated();
        mApp = app;
        /*** RK_ID: APPICON_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. START***/
        mHiddenApplist = new HiddenApplist(app);
        mAllAppsList = new AllAppsList(iconCache, mHiddenApplist);
        /*** RK_ID: APPICON_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. END***/
        mIconCache = iconCache;

        final Resources res = app.getResources();
        
        /*RK_ID:RK_SD_APPS zhangdxa 2013-4-15. S***/
//        mDefaultIcon = Utilities.createIconBitmap(mIconCache.getFullResDefaultActivityIcon(), app,
//                Utilities.DEFAULT_PACKAGE);
        Drawable d = mIconCache.getFullResDefaultActivityIcon();
        BitmapDrawable bd = (BitmapDrawable)d;
        mDefaultIcon = bd.getBitmap();
        /*RK_ID:RK_SD_APPS zhangdxa 2013-4-15. E***/

        mAllAppsLoadDelay = res.getInteger(R.integer.config_allAppsBatchLoadDelay);
        mBatchSize = res.getInteger(R.integer.config_allAppsBatchSize);
        Configuration config = res.getConfiguration();
        mPreviousConfigMcc = config.mcc;
        mLoadMcc = config.mcc;
        /*** AUT: zhaoxy . DATE: 2012-04-16 . START***/
//        usageStatsMonitor = new UsageStatsMonitor(app);
        /*** AUT: zhaoxy . DATE: 2012-04-16 . END***/
    }

    /*** RK_ID: SONAR.  AUT: zhaoxy . DATE: 2012-09-05 . START***/
    public HiddenApplist getHiddenApplist() {
        return mHiddenApplist;
    }
    /*** RK_ID: SONAR.  AUT: zhaoxy . DATE: 2012-09-05 . END***/

    public Bitmap getFallbackIcon() {
        return Bitmap.createBitmap(mDefaultIcon);
    }

    public void unbindWorkspaceItems() {
        sWorker.post(new Runnable() {
            @Override
            public void run() {
                unbindWorkspaceItemsOnMainThread();
            }
        });
    }

    /** Unbinds all the sWorkspaceItems on the main thread, and return a copy of sWorkspaceItems
     * that is save to reference from the main thread. */
    private ArrayList<ItemInfo> unbindWorkspaceItemsOnMainThread() {
        // Ensure that we don't use the same workspace items data structure on the main thread
        // by making a copy of workspace items first.
        final ArrayList<ItemInfo> workspaceItems = new ArrayList<ItemInfo>(sWorkspaceItems);
        final ArrayList<ItemInfo> appWidgets = new ArrayList<ItemInfo>(sAppWidgets);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
               for (ItemInfo item : workspaceItems) {
                   item.unbind();
               }
               for (ItemInfo item : appWidgets) {
                   item.unbind();
               }
            }
        });

        return workspaceItems;
    }

    /**
     * Adds an item to the DB if it was not created previously, or move it to a new
     * <container, screen, cellX, cellY>
     */
    public static void addOrMoveItemInDatabase(Context context, ItemInfo item, long container,
            int screen, int cellX, int cellY) {
        if (item.container == ItemInfo.NO_ID) {
            // From all apps
            addItemToDatabase(context, item, container, screen, cellX, cellY, false);
        } else {
            // From somewhere else
            moveItemInDatabase(context, item, container, screen, cellX, cellY);
        }
    }

    public static void updateItemInDatabaseHelper(Context context, final ContentValues values,
            final ItemInfo item, final String callingFunction) {
        final long itemId = item.id;
        final Uri uri = LauncherSettings.Favorites.getContentUri(itemId, false);
        final ContentResolver cr = context.getContentResolver();

        Runnable r = new Runnable() {
            public void run() {
                cr.update(uri, values, null, null);

                synchronized( sWorkspaceItems ){
		                ItemInfo modelItem = sItemsIdMap.get(itemId);
		                /*RK_ID:RK_SD_APPS zhangdxa 2013-4-15. S***/
		                if( modelItem != null){
		                modelItem.container = item.container;
		                modelItem.cellX = item.cellX;
		                modelItem.cellY = item.cellY;
		                modelItem.screen = item.screen;
		                }
		                
		                //Now the item is not equal to modelItem, 
		                //because the item is the new created item when it begin to drag.
		                //This is different from the android code.
		                /*if (item != modelItem) {
		                    // the modelItem needs to match up perfectly with item if our model is to be
		                    // consistent with the database-- for now, just require modelItem == item
		                    String msg = "item: " + ((item != null) ? item.toString() : "null") +
		                        "modelItem: " + ((modelItem != null) ? modelItem.toString() : "null") +
		                        "Error: ItemInfo passed to " + callingFunction + " doesn't match original";
		                    Log.e(TAG, msg);
		                    //cancel by xingqx 2012.07.09
		                    //throw new RuntimeException(msg);
		                    sWorkspaceItems.remove(modelItem);
		                }*/
		                /*RK_ID:RK_SD_APPS zhangdxa 2013-4-15. E***/
		
		                // Items are added/removed from the corresponding FolderInfo elsewhere, such
		                // as in Workspace.onDrop. Here, we just add/remove them from the list of items
		                // that are on the desktop, as appropriate
		                if (modelItem != null && (modelItem.container == LauncherSettings.Favorites.CONTAINER_DESKTOP ||
		                        modelItem.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT)) {
		                    if (!sWorkspaceItems.contains(modelItem)) {
		                        sWorkspaceItems.add(modelItem);
//bugfix 17646
		                        if(modelItem.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT){
		                        	sHotseatItems.add(modelItem);
		                        }
		                    }
		                } else {
		                    sWorkspaceItems.remove(modelItem);
		                }
		            }
                    setDatabaseDirty(false);
                }
        };

        if (sWorkerThread.getThreadId() == Process.myTid()) {
            r.run();
        } else {
            sWorker.post(r);
        }
    }
    /**
     * Move an item in the DB to a new <container, screen, cellX, cellY>
     */
    public static void moveItemInDatabase(Context context, final ItemInfo item, final long container,
            final int screen, final int cellX, final int cellY) {
        item.container = container;
        item.cellX = cellX;
        item.cellY = cellY;

        // We store hotseat items in canonical form which is this orientation invariant position
        // in the hotseat
        
        item.screen = screen;        

		final ContentValues values = new ContentValues();
		values.put(LauncherSettings.Favorites.CONTAINER, item.container);
		values.put(LauncherSettings.Favorites.CELLX, item.cellX);
		values.put(LauncherSettings.Favorites.CELLY, item.cellY);
		values.put(LauncherSettings.Favorites.SCREEN, item.screen);
		updateItemInDatabaseHelper(context, values, item, "moveItemInDatabase");
    }

    /**
     * Resize an item in the DB to a new <spanX, spanY, cellX, cellY>
     */
    static void resizeItemInDatabase(Context context, final ItemInfo item, final int cellX,
            final int cellY, final int spanX, final int spanY) {
        item.spanX = spanX;
        item.spanY = spanY;
        item.cellX = cellX;
        item.cellY = cellY;

        final ContentValues values = new ContentValues();
        values.put(LauncherSettings.Favorites.CONTAINER, item.container);
        values.put(LauncherSettings.Favorites.SPANX, spanX);
        values.put(LauncherSettings.Favorites.SPANY, spanY);
        values.put(LauncherSettings.Favorites.CELLX, cellX);
        values.put(LauncherSettings.Favorites.CELLY, cellY);
        updateItemInDatabaseHelper(context, values, item, "resizeItemInDatabase");
    }
     /*RK_ID: Jelly_Bean . AUT: yumina . DATE: 2012-08-16 . S*/
     /**
     * Move and/or resize item in the DB to a new <container, screen, cellX, cellY, spanX, spanY>
     */
     static void modifyItemInDatabase(Context context, final ItemInfo item, final long container,
            final int screen, final int cellX, final int cellY, final int spanX, final int spanY) {
        item.container = container;
        item.cellX = cellX;
        item.cellY = cellY;
        item.spanX = spanX;
        item.spanY = spanY;

        // We store hotseat items in canonical form which is this orientation invariant position
        // in the hotseat
        
        item.screen = screen;       

		final ContentValues values = new ContentValues();
		values.put(LauncherSettings.Favorites.CONTAINER, item.container);
		values.put(LauncherSettings.Favorites.CELLX, item.cellX);
		values.put(LauncherSettings.Favorites.CELLY, item.cellY);
		values.put(LauncherSettings.Favorites.SPANX, item.spanX);
		values.put(LauncherSettings.Favorites.SPANY, item.spanY);
		values.put(LauncherSettings.Favorites.SCREEN, item.screen);
		updateItemInDatabaseHelper(context, values, item, "moveItemInDatabase");
    }
	/*RK_ID: Jelly_Bean . AUT: yumina . DATE: 2012-08-16 . E*/
    /**
     * Update an item to the database in a specified container.
     */
    public static void updateItemInDatabase(Context context, final ItemInfo item) {
        final ContentValues values = new ContentValues();
        item.onAddToDatabase(values);
        item.updateValuesWithCoordinates(values, item.cellX, item.cellY);
        updateItemInDatabaseHelper(context, values, item, "updateItemInDatabase");
    }

    /**
     * Returns true if the shortcuts already exists in the database.
     * we identify a shortcut by its title and intent.
     */
    public static boolean shortcutExists(Context context, String title, Intent intent) {
        final ContentResolver cr = context.getContentResolver();
        Cursor c = cr.query(LauncherSettings.Favorites.CONTENT_URI,
            new String[] { "title", "intent" }, "title=? and intent=?",
            new String[] { title, intent.toUri(0) }, null);
        boolean result = false;
        try {
            result = c.moveToFirst();
        } finally {
            c.close();
        }
        return result;
    }

    /**
     * Returns an ItemInfo array containing all the items in the LauncherModel.
     * The ItemInfo.id is not set through this function.
     */
    static ArrayList<ItemInfo> getItemsInLocalCoordinates(Context context) {
        ArrayList<ItemInfo> items = new ArrayList<ItemInfo>();
        final ContentResolver cr = context.getContentResolver();
        Cursor c = cr.query(LauncherSettings.Favorites.CONTENT_URI, new String[] {
                LauncherSettings.Favorites.ITEM_TYPE, LauncherSettings.Favorites.CONTAINER,
                LauncherSettings.Favorites.SCREEN, LauncherSettings.Favorites.CELLX, LauncherSettings.Favorites.CELLY,
                LauncherSettings.Favorites.SPANX, LauncherSettings.Favorites.SPANY }, null, null, null);

        final int itemTypeIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.ITEM_TYPE);
        final int containerIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.CONTAINER);
        final int screenIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.SCREEN);
        final int cellXIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.CELLX);
        final int cellYIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.CELLY);
        final int spanXIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.SPANX);
        final int spanYIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.SPANY);

        try {
            while (c.moveToNext()) {
                ItemInfo item = new ItemInfo();
                item.cellX = c.getInt(cellXIndex);
                item.cellY = c.getInt(cellYIndex);
                item.spanX = c.getInt(spanXIndex);
                item.spanY = c.getInt(spanYIndex);
                item.container = c.getInt(containerIndex);
                item.itemType = c.getInt(itemTypeIndex);
                item.screen = c.getInt(screenIndex);

                items.add(item);
            }
        } catch (Exception e) {
            items.clear();
        } finally {
            c.close();
        }

        return items;
    }

    /**
     * Find a folder in the db, creating the FolderInfo if necessary, and adding it to folderList.
     */
    public FolderInfo getFolderById(Context context, HashMap<Long,FolderInfo> folderList, long id) {
        final ContentResolver cr = context.getContentResolver();
        Cursor c = cr.query(LauncherSettings.Favorites.CONTENT_URI, null,
                "_id=? and (itemType=? or itemType=?)",
                new String[] { String.valueOf(id),
                        String.valueOf(LauncherSettings.Favorites.ITEM_TYPE_FOLDER)}, null);

        try {
            if (c.moveToFirst()) {
                final int itemTypeIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.ITEM_TYPE);
                final int titleIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.TITLE);
                final int containerIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.CONTAINER);
                final int screenIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.SCREEN);
                final int cellXIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.CELLX);
                final int cellYIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.CELLY);

                FolderInfo folderInfo = null;
                switch (c.getInt(itemTypeIndex)) {
                    case LauncherSettings.Favorites.ITEM_TYPE_FOLDER:
                        folderInfo = findOrMakeFolder(folderList, id);
                        break;
                    default:
                       	break;
                }

                if (folderInfo != null) {
	                folderInfo.title = c.getString(titleIndex);
	                folderInfo.id = id;
	                folderInfo.container = c.getInt(containerIndex);
	                folderInfo.screen = c.getInt(screenIndex);
	                folderInfo.cellX = c.getInt(cellXIndex);
	                folderInfo.cellY = c.getInt(cellYIndex);
                }

                return folderInfo;
            }
        } finally {
            c.close();
        }

        return null;
    }

    /**
     * Add an item to the database in a specified container. Sets the container, screen, cellX and
     * cellY fields of the item. Also assigns an ID to the item.
     */
    public static void addItemToDatabase(Context context, final ItemInfo item, final long container,
            final int screen, final int cellX, final int cellY, final boolean notify) { 
        item.container = container;
        item.cellX = cellX;
        item.cellY = cellY;
        // We store hotseat items in canonical form which is this orientation invariant position
        // in the hotseat
        
        item.screen = screen;
       
        final ContentValues values = new ContentValues();
        final ContentResolver cr = context.getContentResolver();
        item.onAddToDatabase(values);

        LauncherApplication app = (LauncherApplication) context.getApplicationContext();
        item.id = app.getLauncherProvider().generateNewId();
        values.put(LauncherSettings.Favorites._ID, item.id);
        item.updateValuesWithCoordinates(values, item.cellX, item.cellY);
        
        /*RK_ID: RK_TRASH . AUT: zhanggx1 . DATE: 2012-04-05 . S*/
        values.put(LauncherSettings.Favorites.LAST_USE_TIME, System.currentTimeMillis());
        /*RK_ID: RK_TRASH . AUT: zhanggx1 . DATE: 2012-04-05 . E*/

        Runnable r = new Runnable() {
            public void run() {
                cr.insert(notify ? LauncherSettings.Favorites.CONTENT_URI :
                        LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION, values);

                if (sItemsIdMap.containsKey(item.id)) {
                    // we should not be adding new items in the db with the same id
                    throw new RuntimeException("Error: ItemInfo id (" + item.id + ") passed to " +
                        "addItemToDatabase already exists." + item.toString());
                }
                sItemsIdMap.put(item.id, item);
                switch (item.itemType) {
                    case LauncherSettings.Favorites.ITEM_TYPE_FOLDER:
                        sFolders.put(item.id, (FolderInfo) item);
                        // Fall through
                        if (item.container == LauncherSettings.Favorites.CONTAINER_DESKTOP ||
                                item.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
                            sWorkspaceItems.add(item);
                        }
                        break;
                    case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
                    case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
			/*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-09-26 START */ 
                   case LauncherSettings.Favorites.ITEM_TYPE_LEOSWIDGET:
			/*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-09-26 END */
                    case LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT:
                        if (item.container == LauncherSettings.Favorites.CONTAINER_DESKTOP ||
                                item.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
                            /*** fixbug 191242  . AUT: zhaoxy . DATE: 2012-10-12. START***/
                            if (item instanceof ShortcutInfo) {
                                ShortcutInfo addInfo = (ShortcutInfo) item;
                                boolean samePos = false;
                                for (ItemInfo info : sWorkspaceItems) {
                                    if (info instanceof ShortcutInfo) {
                                        if (((ShortcutInfo) info).equalsPosition(addInfo)) {
                                            samePos = true;
                                            break;
                                        }
                                    }
                                }
                                if (!samePos) {
                                    sWorkspaceItems.add(item);
                                }
                            }
                            /*** fixbug 191242  . AUT: zhaoxy . DATE: 2012-10-12. END***/
                        }
                        break;
                    case LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET:
                    case LauncherSettings.Favorites.ITEM_TYPE_COMMEND_APPWIDGET:
                        sAppWidgets.add((LauncherAppWidgetInfo) item);
                        break;
                    default:
                    	break;
                }
                setDatabaseDirty(false);
            }
        };

        if (sWorkerThread.getThreadId() == Process.myTid()) {
            r.run();
        } else {
            sWorker.post(r);
        }
    }

    
    public void surelyAddItemInDatabase(final Context context,
            final /**ShortcutInfo[] infos**/LinkedList<ShortcutInfo>itemInfo, final ContentValues[] extraValues) {
        
        Runnable r = new Runnable() {

            @Override
            public void run() {
                ItemInfo[] infos = itemInfo.toArray(new ShortcutInfo[0]);
                ContentValues[] allValues = new ContentValues[infos.length];
                LauncherApplication app = (LauncherApplication) context
                        .getApplicationContext();
                for (int i = 0; i < infos.length; i++) {
                    // R2
                    
                    infos[i].id = app.getLauncherProvider().generateNewId();
                    sItemsIdMap.put(infos[i].id, infos[i]);
                    sWorkspaceItems.add(infos[i]);
                    Log.d("gecn1", "xlaunchermodel ===item = " + infos[i].toString());

                    allValues[i] = new ContentValues();

                    if (extraValues != null) {
                        allValues[i].putAll(extraValues[i]);
                    }
                   
                    allValues[i].put(LauncherSettings.Favorites._ID, infos[i].id);
                    infos[i].updateValuesWithCoordinates(allValues[i],
                            infos[i].cellX, infos[i].cellY);
                    allValues[i].put(Favorites.CONTAINER, infos[i].container);
                    allValues[i].put(Favorites.SCREEN, infos[i].screen);
                    allValues[i].put(Favorites.SPANX, infos[i].spanX);
                    allValues[i].put(Favorites.SPANY, infos[i].spanY);
                    allValues[i].put(Favorites.ITEM_TYPE, infos[i].itemType);

                    if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
                        R2.echo("info is : " + infos[i].getClass().getSimpleName());
                    ShortcutInfo shortcut = (ShortcutInfo) infos[i];
                    allValues[i]
                            .put(Favorites.INTENT, shortcut.intent.toUri(0));
                    allValues[i]
                            .put(Favorites.TITLE, shortcut.title.toString());
                    if (shortcut.replaceTitle != null
                            && !"".equals(shortcut.replaceTitle.toString()
                                    .trim())) {
                        allValues[i].put(Favorites.TITLE_REPLACE,
                                shortcut.replaceTitle.toString());
                    }
                    if (!allValues[i].containsKey(Favorites.ICON)) {
                        allValues[i].put(
                                Favorites.ICON,
                                Utilities.newInstance().bitmap2ByteArray(
                                        app.getModel().getFallbackIcon()));
                    }
                    if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
                        R2.echo("ShortcutInfo add : " + infos[i].id);
                    
                    allValues[i].put(LauncherSettings.Favorites.LAST_USE_TIME, System.currentTimeMillis());

                }
                
                itemInfo.clear();
                LauncherProvider cp = app.getLauncherProvider();
                cp.bulkInsert(
                        LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION,
                        allValues);
                setDatabaseDirty(false);
            }
            
        };
        
        if (sWorkerThread.getThreadId() == Process.myTid()) {
            r.run();
        } else {
            sWorker.post(r);
        }
//        SettingsValue.rescheduleCleanupAlarm(context);
    } 
    /**
     * Creates a new unique child id, for a given cell span across all layouts.
     */
    static int getCellLayoutChildId(
            long container, int screen, int localCellX, int localCellY, int spanX, int spanY) {
        return (((int) container & 0xFF) << 24)
                | (screen & 0xFF) << 16 | (localCellX & 0xFF) << 8 | (localCellY & 0xFF);
    }

    public static int getCellCountX() {
        return mCellCountX;
    }

    public static int getCellCountY() {
        return mCellCountY;
    }

    /**
     * Updates the model orientation helper to take into account the current layout dimensions
     * when performing local/canonical coordinate transformations.
     */
    public static void updateWorkspaceLayoutCells(int shortAxisCellCount, int longAxisCellCount) {
        mCellCountX = shortAxisCellCount;
        mCellCountY = longAxisCellCount;
    }

    /**
     * Removes the specified item from the database
     * @param context
     * @param item
     */
    public static void deleteItemFromDatabase(Context context, final ItemInfo item) {
        final ContentResolver cr = context.getContentResolver();
        final Uri uriToDelete = LauncherSettings.Favorites.getContentUri(item.id, false);
        
        Runnable r = new Runnable() {
            public void run() {
                cr.delete(uriToDelete, null, null);
                switch (item.itemType) {
                    case LauncherSettings.Favorites.ITEM_TYPE_FOLDER:
                        sFolders.remove(item.id);
                        sWorkspaceItems.remove(item);
                        break;
                    case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
                    case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
                    case LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT:
                        sWorkspaceItems.remove(item);
                        break;
                    case LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET:
                    case LauncherSettings.Favorites.ITEM_TYPE_COMMEND_APPWIDGET:
                        sAppWidgets.remove((LauncherAppWidgetInfo) item);
                        break;

                        /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-15 START */    
                    case LauncherSettings.Favorites.ITEM_TYPE_LEOSWIDGET:
                    	sLeosWidgets.remove((LenovoWidgetViewInfo) item);
                    	Log.d("liuyg1","LauncherModel.deleteItemFromDatabase");
                    	break;
                        /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-15 END */
                    default:
                    	break;

                }
                sItemsIdMap.remove(item.id);
                sDbIconCache.remove(item);
                setDatabaseDirty(false);
            }
        };
        if (sWorkerThread.getThreadId() == Process.myTid()) {
            r.run();
        } else {
            sWorker.post(r);
        }
    }

    /*** fixbug 191242  . AUT: zhaoxy . DATE: 2012-10-12. START***/
    public static void deleteItemFromRAM(final long id) {
        Runnable r = new Runnable() {
            public void run() {
                ItemInfo modelItem = sItemsIdMap.get(id);
                if(modelItem !=null){   //add by shenchao for fixing bug LELAUNCHER-340.
                switch (modelItem.itemType) {
                    case LauncherSettings.Favorites.ITEM_TYPE_FOLDER:
                        sFolders.remove(id);
                        sWorkspaceItems.remove(modelItem);
                        break;
                    case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
                    case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
                    case LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT:
                        sWorkspaceItems.remove(modelItem);
                        break;
                    case LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET:
                    case LauncherSettings.Favorites.ITEM_TYPE_COMMEND_APPWIDGET:
                        sAppWidgets.remove((LauncherAppWidgetInfo) modelItem);
                        break;
                    default:
                    	break;
                }
                sItemsIdMap.remove(id);
                sDbIconCache.remove(modelItem);
            }
            }
        };
        if (sWorkerThread.getThreadId() == Process.myTid()) {
            r.run();
        } else {
            sWorker.post(r);
        }
    }
    /*** fixbug 191242  . AUT: zhaoxy . DATE: 2012-10-12. END***/

    /**
     * Remove the contents of the specified folder from the database
     */
    public static void deleteFolderContentsFromDatabase(Context context, final FolderInfo info) {
        final ContentResolver cr = context.getContentResolver();

        Runnable r = new Runnable() {
            public void run() {
                cr.delete(LauncherSettings.Favorites.getContentUri(info.id, false), null, null);
                sItemsIdMap.remove(info.id);
                sFolders.remove(info.id);
                sDbIconCache.remove(info);
                sWorkspaceItems.remove(info);

                cr.delete(LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION,
                        LauncherSettings.Favorites.CONTAINER + "=" + info.id, null);
                for (ItemInfo childInfo : info.contents) {
                    sItemsIdMap.remove(childInfo.id);
                    sDbIconCache.remove(childInfo);
                }
                setDatabaseDirty(false);
            }
        };
        if (sWorkerThread.getThreadId() == Process.myTid()) {
            r.run();
        } else {
            sWorker.post(r);
        }
    }

    /**
     * Set this as the current Launcher activity object for the loader.
     */
    public void initialize(Callbacks callbacks) {
        synchronized (mLock) {
            mCallbacks = new WeakReference<Callbacks>(callbacks);
        }
    }

    /**
     * Call from the handler for ACTION_PACKAGE_ADDED, ACTION_PACKAGE_REMOVED and
     * ACTION_PACKAGE_CHANGED.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (DEBUG_LOADERS) Log.d(TAG, "onReceive intent=" + intent);
        
        /** ID: Profile restoring. AUT: chengliang . DATE: 2012.03.22 . S */
       // if( XLauncher.getDefaultProfileProcessingState() ){
        //	return;
       // }
        /** ID: Profile restoring. AUT: chengliang . DATE: 2012.03.22 . E */

        final String action = intent.getAction();

        if (Intent.ACTION_PACKAGE_CHANGED.equals(action)
                || Intent.ACTION_PACKAGE_REMOVED.equals(action)
                || Intent.ACTION_PACKAGE_ADDED.equals(action)) {
            final String packageName = intent.getData().getSchemeSpecificPart();
            final boolean replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);

            int op = PackageUpdatedTask.OP_NONE;
            if (packageName == null || packageName.length() == 0) {
                // they sent us a bad intent
                return;
            }
            
            /*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. S***/
            //Process Leos widgets in other package
            processLeosWidgetInOtherPackage(action, packageName);
            /*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. E***/
 
            /* RK_ID:  Do not display the lenovo theme icon. AUT: yumina . DATE: 2012-10-08 . S */
            if(packageName.contains(LauncherPackageName) 
            		|| packageName.contains(LauncherThemePackage) 
            		|| packageName.contains(LockThemePackages)
            		|| packageName.contains(anotherLauncherPackage)){
                return;
            }
            /* RK_ID:  Do not display the lenovo theme icon. AUT: yumina . DATE: 2012-10-08 . E */


            if (Intent.ACTION_PACKAGE_CHANGED.equals(action)) {
                op = PackageUpdatedTask.OP_UPDATE;
            } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
                if (!replacing) {
                    op = PackageUpdatedTask.OP_REMOVE;
                    
                }
                // else, we are replacing the package, so a PACKAGE_ADDED will be sent
                // later, we will update the package at this time
            } else if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
                if (!replacing) {
                    op = PackageUpdatedTask.OP_ADD;
                    
                } else {
                    op = PackageUpdatedTask.OP_UPDATE;
                }
            }
            
            if (op != PackageUpdatedTask.OP_NONE) {
                enqueuePackageUpdated(new ReceiverTask(op, new String[] { packageName }));
            }
    		
            
        } else if (Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE.equals(action)) {
            // First, schedule to add these apps back in.
            String[] packages = intent.getStringArrayExtra(Intent.EXTRA_CHANGED_PACKAGE_LIST);
            enqueuePackageUpdated(new ReceiverTask(PackageUpdatedTask.OP_AVALIABLE_ADD, packages));
            // Then, rebind everything.
            /*RK_ID:RK_SD_APPS zhangdxa 2013-4-15. S***/
            //startLoaderFromBackground();
            /*RK_ID:RK_SD_APPS zhangdxa 2013-4-15. E***/
        } else if (Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE.equals(action)) {
        	String[] packages = intent.getStringArrayExtra(Intent.EXTRA_CHANGED_PACKAGE_LIST);
            enqueuePackageUpdated(new ReceiverTask(
                        PackageUpdatedTask.OP_UNAVAILABLE, packages));
        } else if (Intent.ACTION_LOCALE_CHANGED.equals(action)) {
            // If we have changed locale we need to clear out the labels in all apps/workspace.
            /* RK_ID: RK_LAUNCHERMODEL. AUT: liuli1 . DATE: 2012-05-11 . START */
            Log.i(TAG, "Intent.ACTION_LOCALE_CHANGED ... ");
            XFolder.reloadHint(mApp);
            /* RK_ID: RK_LAUNCHERMODEL. AUT: liuli1 . DATE: 2012-05-11 . END */
            /** ID: fix bug: leos40/170148. AUT: chengliang . DATE: 2012.05.17 . S */
            mLocaleJustChanged = true;
            /** ID: fix bug: leos40/170148. AUT: chengliang . DATE: 2012.05.17 . E */
            forceReload();
        } else if (Intent.ACTION_CONFIGURATION_CHANGED.equals(action)) {
             // Check if configuration change was an mcc/mnc change which would affect app resources
             // and we would need to clear out the labels in all apps/workspace. Same handling as
             // above for ACTION_LOCALE_CHANGED
             Configuration currentConfig = context.getResources().getConfiguration();
             if (mPreviousConfigMcc != currentConfig.mcc) {
                   Log.d(TAG, "Reload apps on config change. curr_mcc:"
                       + currentConfig.mcc + " prevmcc:" + mPreviousConfigMcc);
                   forceReload();
             }
             // Update previousConfig
             mPreviousConfigMcc = currentConfig.mcc;
        } else if (SearchManager.INTENT_GLOBAL_SEARCH_ACTIVITY_CHANGED.equals(action) ||
                   SearchManager.INTENT_ACTION_SEARCHABLES_CHANGED.equals(action)) {
            if (mCallbacks != null) {
                Callbacks callbacks = mCallbacks.get();
                if (callbacks != null) {
                    callbacks.bindSearchablesChanged();
                }
            }
            /* RK_ID: RK_PROFILE. AUT: liuli1 . DATE: 2012-02-29 . START */
        } else if (Intent.ACTION_DEVICE_STORAGE_LOW.equals(action) ||
                Intent.ACTION_DEVICE_STORAGE_OK.equals(action)) {
            if (mCallbacks != null) {
                Callbacks callbacks = mCallbacks.get();
                if (callbacks != null) {
                    callbacks.setProfileBackupEnable(action);
                }
            }
            /* RK_ID: RK_PROFILE. AUT: liuli1 . DATE: 2012-02-29 . END */
        }
        /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. START***/
        else if (HiddenApplist.ACTION_SET_APP_HIDDEN.equals(action)) {
            String[] date = intent.getStringArrayExtra(HiddenApplist.KEY_HIDDENLIST_DATE);
            if (date != null) {
                enqueuePackageUpdated(new ReceiverTask(PackageUpdatedTask.OP_HIDDEN, date));
            }
        }
        /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. END***/
    }

    private void forceReload() {
        /* RK_ID: RK_LAUNCHERMODEL. AUT: liuli1 . DATE: 2012-05-11 . START */
        Log.i(TAG, "forceReload start");
        Log.d("gecn1", "mApp == null  is " + (mApp == null));
        if(mApp == null || LoadBootPolicy.getInstance(mApp.getApplicationContext()).getDefaultProfileProcessingState()){
        	Log.d("gecn1", "LoadBootPolicy.getInstance(mApp.getApplicationContext()).getDefaultProfileProcessingState()  =  true" );
			Log.d("gecn1","mApp == null || LoadBootPolicy.getInstance(mApp.getApplicationContext()).getDefaultProfileProcessingState()");
			return;
		}else{
			Log.d("gecn1", "LoadBootPolicy.getInstance(mApp.getApplicationContext()).getDefaultProfileProcessingState()  =  false" );
		}
        /* RK_ID: RK_LAUNCHERMODEL. AUT: liuli1 . DATE: 2012-05-11 . END */
        synchronized (mLock) {
            // Stop any existing loaders first, so they don't set mAllAppsLoaded or
            // mWorkspaceLoaded to true later
            stopLoaderLocked();
            mAllAppsLoaded = false;
            mWorkspaceLoaded = false;
        }
        // Do this here because if the launcher activity is running it will be restarted.
        // If it's not running startLoaderFromBackground will merely tell it that it needs
        // to reload.
        startLoaderFromBackground();
    }

    /* AUT: liuli liuli0120@use.com.cn DATE: 2011-12-20 START */
    // add for theme, after applying theme, reload workspace    
    public void forceReloadWorkspce() {
        synchronized (mLock) {
            // Stop any existing loaders first, so they don't set mAllAppsLoaded or
            // mWorkspaceLoaded to true later
            stopLoaderLocked();
            mWorkspaceLoaded = false;
        }
        mIconCache.flush();
        startLoader(mApp, false);
    }

    public void forceReloadForProfile() {
        forceReload();
    }
    /* AUT: liuli liuli0120@use.com.cn DATE: 2011-12-20 END */
    /** AUT: henryyu1986@163.com DATE: 2011-12-20 */
//    private boolean mIsIconStyleApplying;
    public void forceReloadWorkspceIcon() {
    }
    /** AUT: henryyu1986@163.com DATE: 2011-12-20 */

    /**
     * When the launcher is in the background, it's possible for it to miss paired
     * configuration changes.  So whenever we trigger the loader from the background
     * tell the launcher that it needs to re-run the loader when it comes back instead
     * of doing it now.
     */
    public void startLoaderFromBackground() {
        boolean runLoader = false;
        if (mCallbacks != null) {
            Callbacks callbacks = mCallbacks.get();
            if (callbacks != null) {
                // Only actually run the loader if they're not paused.
                if (!callbacks.setLoadOnResume()) {
                    runLoader = true;
                }
            }
        }
        if (runLoader) {
            startLoader(mApp, false);
        }
    }

    // If there is already a loader task running, tell it to stop.
    // returns true if isLaunching() was true on the old task
    private boolean stopLoaderLocked() {
        boolean isLaunching = false;
        LoaderTask oldTask = mLoaderTask;
        if (oldTask != null) {
            if (oldTask.isLaunching()) {
                isLaunching = true;
            }
            oldTask.stopLocked();
        }
        return isLaunching;
    }

    public void startLoader(Context context, boolean isLaunching) {
        synchronized (mLock) {
        	mWorkspaceLoaded = false;
            if (DEBUG_LOADERS) {
                Log.d(TAG, "startLoader isLaunching=" + isLaunching);
            }

            // Don't bother to start the thread if we know it's not going to do anything
            if (mCallbacks != null && mCallbacks.get() != null) {
                // If there is already one running, tell it to stop.
                // also, don't downgrade isLaunching if we're already running
                isLaunching = isLaunching || stopLoaderLocked();
                mLoaderTask = new LoaderTask(context, isLaunching);
                sWorkerThread.setPriority(Thread.NORM_PRIORITY);
                sWorker.post(mLoaderTask);
                /*AUT: zhanglq@bj.cobellink.com DATA 2012-03-21 start*/
                isFinishLoad = false;
                /*AUT: zhanglq@bj.cobellink.com DATA 2012-03-21 end*/
            }
        }
    }

    public void stopLoader() {
        synchronized (mLock) {
            if (mLoaderTask != null) {
                mLoaderTask.stopLocked();
            }
        }
    }

    public boolean isAllAppsLoaded() {
        return mAllAppsLoaded;
    }

    /**
     * Runnable for the thread that loads the contents of the launcher:
     *   - workspace icons
     *   - widgets
     *   - all apps icons
     */
    private class LoaderTask implements Runnable {
        private Context mContext;
        private Thread mWaitThread;
        private boolean mIsLaunching;
        private boolean mStopped;
        private boolean mLoadAndBindStepFinished;
        private HashMap<Object, CharSequence> mLabelCache;

        LoaderTask(Context context, boolean isLaunching) {
            mContext = context;
            mIsLaunching = isLaunching;
            mLabelCache = new HashMap<Object, CharSequence>();
        }

        boolean isLaunching() {
            return mIsLaunching;
        }

        private void loadAndBindWorkspace() {
            // Load the workspace
            if (DEBUG_LOADERS) {
                Log.d(TAG, "loadAndBindWorkspace mWorkspaceLoaded=" + mWorkspaceLoaded);
            }

            if (!mWorkspaceLoaded) {
            	//mAppListCacheTempLenovo=null;
                loadWorkspace();
                synchronized (LoaderTask.this) {
                    if (mStopped) {
                        return;
                    }
/*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-23 START */
//fix bug 171763
                    /*PK_ID:OPEN THIS CODE AUTH:GECN1 S*/
                    mWorkspaceLoaded = true;   
                    /*PK_ID:OPEN THIS CODE AUTH:GECN1 E*/
/*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-23 END */               
                }
                /***RK_ID:RK_LENOVOADAPTER. DEP_RK_ID:NULL. AUT:zhanghong5@lenovo.com DATE:2012-05-23. START***/      
                // Bind the workspace
//                bindWorkspace();
            }

//            // Bind the workspace
            /** ID: fix bug: LELAUNCHER-89. AUT: zhaoxy . DATE: 2013.09.02 . S */
            synchronized (mDirtyLock) {
                if (isDatabaseDirty) {
                    Log.d("DatabaseDirty", "DatabaseDirty reload!!");
                    stopLoader();
                    forceReload();
                    isDatabaseDirty = false;
                    return;
                } else {
                    Log.d("DatabaseDirty", "Database success, bindWorkspace !");
                    bindWorkspace();
                }
            }
            /** ID: fix bug: LELAUNCHER-89. AUT: zhaoxy . DATE: 2013.09.02 . E */
              /***RK_ID:RK_LENOVOADAPTER. DEP_RK_ID:NULL. AUT:zhanghong5@lenovo.com DATE:2011-05-23. END***/
        }

        private void waitForIdle() {
            Log.d("DatabaseDirty", "waitForIdle");
            // Wait until the either we're stopped or the other threads are done.
            // This way we don't start loading all apps until the workspace has settled
            // down.
            synchronized (LoaderTask.this) {
                final long workspaceWaitTime = DEBUG_LOADERS ? SystemClock.uptimeMillis() : 0;

                mHandler.postIdle(new Runnable() {
                        public void run() {
                            synchronized (LoaderTask.this) {
                                mLoadAndBindStepFinished = true;
                                /*AUT: zhanglq@bj.cobellink.com DATA 2012-03-21 start*/
                                isFinishLoad = true;
                                /*AUT: zhanglq@bj.cobellink.com DATA 2012-03-21 end*/
                                if (DEBUG_LOADERS) {
                                    Log.d(TAG, "done with previous binding step");
                                }
                                LoaderTask.this.notify();
                            }
                        }
                    });

                while (!mStopped && !mLoadAndBindStepFinished) {
                    try {
                        this.wait();
                    } catch (InterruptedException ex) {
                        // Ignore
                    }
                }
                if (DEBUG_LOADERS) {
                    Log.d(TAG, "waited "
                            + (SystemClock.uptimeMillis()-workspaceWaitTime)
                            + "ms for previous step to finish binding");
                }
            }
        }

        public void run() {

        	/*PK_ID LoadTask Has Started E */

            // Optimize for end-user experience: if the Launcher is up and // running with the
            // All Apps interface in the foreground, load All Apps first. Otherwise, load the
            // workspace first (default).
            final Callbacks cbk = mCallbacks.get();
            /***RK_ID:RK_BUGFIX_172844   AUT:zhanglz1@lenovo.com.DATE:2012-12-05. S***/ 
            //new art
//			if (cbk != null) {
//				Launcher launcher = cbk.getLauncherInstance();
//				launcher.mWaitingForResult = true;
//			}
          //new art
			 /***RK_ID:RK_BUGFIX_172844   AUT:zhanglz1@lenovo.com.DATE:2012-12-05. E***/        
            final boolean loadWorkspaceFirst = cbk != null ? (!cbk.isAllAppsVisible()) : true;

            keep_running: {
                // Elevate priority when Home launches for the first time to avoid
                // starving at boot time. Staring at a blank home is not cool.
                synchronized (mLock) {
                    if (DEBUG_LOADERS) Log.d(TAG, "Setting thread priority to " +
                            (mIsLaunching ? "DEFAULT" : "BACKGROUND"));
                    android.os.Process.setThreadPriority(mIsLaunching
                            ? Process.THREAD_PRIORITY_DEFAULT : Process.THREAD_PRIORITY_BACKGROUND);
                }
                if (loadWorkspaceFirst) {
                    if (DEBUG_LOADERS) Log.d(TAG, "step 1: loading workspace");
                    loadAndBindWorkspace();
                } else {
                    if (DEBUG_LOADERS) Log.d(TAG, "step 1: special: loading all apps");
                    loadAndBindAllApps();
                }

                if (mStopped) {
                    break keep_running;
                }

                // Whew! Hard work done.  Slow us down, and wait until the UI thread has
                // settled down.
                synchronized (mLock) {
                    if (mIsLaunching) {
                        if (DEBUG_LOADERS) Log.d(TAG, "Setting thread priority to BACKGROUND");
                        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                    }
                }
                
                waitForIdle();

                // second step
                if (loadWorkspaceFirst) {
                    if (DEBUG_LOADERS) Log.d(TAG, "step 2: loading all apps");
                    loadAndBindAllApps();
                } else {
                    if (DEBUG_LOADERS) Log.d(TAG, "step 2: special: loading workspace");
                    loadAndBindWorkspace();
                }

                // Restore the default thread priority after we are done loading items
                synchronized (mLock) {
                    android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_DEFAULT);
                     
                }
                
            }


            // Update the saved icons if necessary
            if (DEBUG_LOADERS) Log.d(TAG, "Comparing loaded icons to database icons");
            for (Object key : sDbIconCache.keySet()) {
                updateSavedIcon(mContext, (ShortcutInfo) key, sDbIconCache.get(key));
            }
            sDbIconCache.clear();
            
            /*** AUT:zhaoxy . DATE:2012-03-07 . START***/
            if (isFirstLoad && mContext != null) {
            	isFirstLoad = false;
				Intent dimissIntent = new Intent(ProgressDialog.ACTION_DISMISS_PROGRESS_DIALOG);
				dimissIntent.putExtra(ProgressDialog.KEY_TOAST_MSG_ID, R.string.theme_appling_success);
				mContext.sendBroadcast(dimissIntent);
			}
            /*** AUT:zhaoxy . DATE:2012-03-07 . END***/
            
            checkMccPackage(mContext, null, false);

            // Clear out this reference, otherwise we end up holding it until all of the
            // callback runnables are done.
            mContext = null;

            synchronized (mLock) {
                // If we are still the last one to be scheduled, remove ourselves.
                if (mLoaderTask == this) {
                    mLoaderTask = null;
                }
            }            

        	/*PK_ID LoadTask Has Started S */
        	//由于只有此处修改，因此可以这么写
           //add by zhanggx1 for removing on 2013-11-13 . s
            mHandler.post(new Runnable() {            	
          	    public void run() { 
          	//add by zhanggx1 for removing on 2013-11-13 . e
          	    	if(!isLoadTaskHasFirstLoaded){
                    	synchronized (mLock) {
            				isLoadTaskHasFirstLoaded = true;
        					Log.d(TAG, "==============mLock.notifyAll()");
            				mLock.notifyAll();
            			}
                	}
          	    }
            });

            /*RK_ID: RK_DOCK_ADD . AUT: zhanggx1 . AUT: 2011-12-15 . S*/
            mHandler.post(new Runnable() {            	
          	    public void run() { 
          		    Callbacks callbacks = tryGetCallbacks(cbk);
                    if (callbacks != null) {
                        callbacks.initThemeElements();
                    } 
          	    }                
            });
            /***RK_ID:RK_BUGFIX_172844   AUT:zhanglz1@lenovo.com.DATE:2012-12-05. S***/   
          //new art
//            if (mCallbacks != null && mCallbacks.get() != null) {
//            	Launcher launcher = mCallbacks.get().getLauncherInstance();
//                launcher.mWaitingForResult = false;
//            }
          //new art
            /***RK_ID:RK_BUGFIX_172844   AUT:zhanglz1@lenovo.com.DATE:2012-12-05. E***/        
            /*RK_ID: RK_DOCK_ADD . AUT: zhanggx1 . AUT: 2011-12-15 . E*/
            
           //add by zhanggx1 for reordering all pages on 2013-11-20. s
            mHandler.post(new Runnable() {            	
            	public void run() { 
          		    Callbacks callbacks = tryGetCallbacks(cbk);
                    if (callbacks != null) {
                        callbacks.autoReorder();
                    } 
          	    }
            });
       	    //add by zhanggx1 for reordering all pages on 2013-11-20. e  

        }

        public void stopLocked() {
            synchronized (LoaderTask.this) {
            	Intent dimissIntent = new Intent(SettingsValue.ACTION_REMOVE_LOADING_DIALOG);
                if(mContext!= null){
                    mContext.sendBroadcast(dimissIntent);
                }
                mStopped = true;
                /***RK_ID:RK_BUGFIX_172844   AUT:zhanglz1@lenovo.com.DATE:2012-12-05. S***/     
              //new art
//                if (mCallbacks != null && mCallbacks.get() != null) {
//                	Launcher launcher = mCallbacks.get().getLauncherInstance();
//                    launcher.mWaitingForResult = false;
//                }
              //new art
                /***RK_ID:RK_BUGFIX_172844   AUT:zhanglz1@lenovo.com.DATE:2012-12-05. E***/        
                this.notify();
            }
        }

        /**
         * Gets the callbacks object.  If we've been stopped, or if the launcher object
         * has somehow been garbage collected, return null instead.  Pass in the Callbacks
         * object that was around when the deferred message was scheduled, and if there's
         * a new Callbacks object around then also return null.  This will save us from
         * calling onto it with data that will be ignored.
         */
        Callbacks tryGetCallbacks(Callbacks oldCallbacks) {
            synchronized (mLock) {
                if (mStopped) {
                    return null;
                }

                if (mCallbacks == null) {
                    return null;
                }

                final Callbacks callbacks = mCallbacks.get();
                if (callbacks != oldCallbacks) {
                    return null;
                }
                if (callbacks == null) {
                    Log.w(TAG, "no mCallbacks");
                    return null;
                }

                return callbacks;
            }
        }

        // check & update map of what's occupied; used to discard overlapping/invalid items
        private boolean checkItemPlacement(ItemInfo occupied[][][], ItemInfo item) {
            if(item.cellX >= mCellCountX || item.cellY >=mCellCountY || item.cellX<0 ||item.cellY<0){
                Log.e(TAG, "Error loading shortcutmCellCountX  =  "+ mCellCountX);
                Log.e(TAG, "Error loading mCellCountY  =  "+ mCellCountY);
                Log.e(TAG, "Error loading shortcut cellX or cellY is invalidate ");
                return false;
            }
            //added by yumina for the bug LELAUNCHER-506 20131030
            if(item.cellX +item.spanX > mCellCountX || item.cellY+item.spanY > mCellCountY){
                return false;
            }

            int containerIndex = item.screen;
            if (item.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
                // Return early if we detect that an item is under the hotseat button
 /*RK_ID:RK_SINGLE_LAYER liuyg1 2013-6-13. S*/
//                if (XHotseat.isAllAppsButtonRank(item.screen)) {
//                    return false;
//                }
 /*RK_ID:RK_SINGLE_LAYER liuyg1 2013-6-13. S*/

                // We use the last index to refer to the hotseat and the screen as the rank, so
                // test and update the occupied state accordingly
                if (occupied[mLauncherService.mScreenCount][item.screen][0] != null) {
                    Log.e(TAG, "Error loading shortcut into hotseat " + item
                        + " into position (" + item.screen + ":" + item.cellX + "," + item.cellY
                        + ") occupied by " + occupied[mLauncherService.mScreenCount][item.screen][0]);
                    return false;
                } else {
                    occupied[mLauncherService.mScreenCount][item.screen][0] = item;
                    return true;
                }
            } else if (item.container != LauncherSettings.Favorites.CONTAINER_DESKTOP) {
                // Skip further checking if it is not the hotseat or workspace container
                return true;
            }

            // Check if any workspace icons overlap with each other
            for (int x = item.cellX; x < (item.cellX+item.spanX); x++) {
                for (int y = item.cellY; y < (item.cellY+item.spanY); y++) {
                    if (occupied[containerIndex][x][y] != null) {
                        Log.e(TAG, "Error loading shortcut " + item
                            + " into cell (" + containerIndex + "-" + item.screen + ":"
                            + x + "," + y
                            + ") occupied by "
                            + occupied[containerIndex][x][y]);
                        return false;
                    }
                }
            }
            for (int x = item.cellX; x < (item.cellX+item.spanX); x++) {
                for (int y = item.cellY; y < (item.cellY+item.spanY); y++) {
                    occupied[containerIndex][x][y] = item;
                }
            }

            return true;
        }
        
        private boolean findApp(List<android.content.pm.ApplicationInfo> installedApps, String packageName) {
        	if (installedApps == null || packageName == null) {
        		return false;
        	}
        	for (android.content.pm.ApplicationInfo app : installedApps) {
        		if (packageName.equals(app.packageName)) {
        			return true;
        		}
        	}
        	return false;
        }

        private void loadWorkspace() {
        	final long t = DEBUG_LOADERS ? SystemClock.uptimeMillis() : 0;
            final Context context = mContext;
            final ContentResolver contentResolver = context.getContentResolver();
            final PackageManager manager = context.getPackageManager();
            final AppWidgetManager widgets = AppWidgetManager.getInstance(context);
            final boolean isSafeMode = manager.isSafeMode();

            sWorkspaceItems.clear();
            sAppWidgets.clear();
            sFolders.clear();
            sItemsIdMap.clear();
            sDbIconCache.clear();
            
		    /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-09-26 START */
            mLeosWidgetViews.clear();
            sLeosWidgets.clear();
		    /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-09-26 END */
            final ArrayList<Long> itemsToRemove = new ArrayList<Long>();
            final ArrayList<Long> folderToRemove = new ArrayList<Long>();

            final Cursor c = contentResolver.query(
                    LauncherSettings.Favorites.CONTENT_URI, null, null, null, null);

            // +1 for the hotseat (it can be larger than the workspace)
            // Load workspace in reverse order to ensure that latest items are loaded first (and
            // before any earlier duplicates)
            final ItemInfo occupied[][][] =
                    new ItemInfo[mLauncherService.mScreenCount + 1][mCellCountX + 2][mCellCountY + 1];
            EnsureAllApps ensure = new EnsureAllApps();
            
            /*RK_ID: RK_COMMEND_VIEW . AUT: zhanggx1 . DATE: 2012-07-27 . PUR: for bug 167962 . S*/
            List<ItemInfo> changedValue = new ArrayList<ItemInfo>();            
            /*RK_ID: RK_COMMEND_VIEW . AUT: zhanggx1 . DATE: 2012-07-27 . PUR: for bug 167962 . E*/

            try {
                final int idIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites._ID);
                final int intentIndex = c.getColumnIndexOrThrow
                        (LauncherSettings.Favorites.INTENT);
                final int titleIndex = c.getColumnIndexOrThrow
                        (LauncherSettings.Favorites.TITLE);
                final int iconTypeIndex = c.getColumnIndexOrThrow(
                        LauncherSettings.Favorites.ICON_TYPE);
                final int iconIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.ICON);
                final int iconPackageIndex = c.getColumnIndexOrThrow(
                        LauncherSettings.Favorites.ICON_PACKAGE);
                final int iconResourceIndex = c.getColumnIndexOrThrow(
                        LauncherSettings.Favorites.ICON_RESOURCE);
                final int containerIndex = c.getColumnIndexOrThrow(
                        LauncherSettings.Favorites.CONTAINER);
                final int itemTypeIndex = c.getColumnIndexOrThrow(
                        LauncherSettings.Favorites.ITEM_TYPE);
                final int appWidgetIdIndex = c.getColumnIndexOrThrow(
                        LauncherSettings.Favorites.APPWIDGET_ID);
                final int screenIndex = c.getColumnIndexOrThrow(
                        LauncherSettings.Favorites.SCREEN);
                final int cellXIndex = c.getColumnIndexOrThrow
                        (LauncherSettings.Favorites.CELLX);
                final int cellYIndex = c.getColumnIndexOrThrow
                        (LauncherSettings.Favorites.CELLY);
                final int spanXIndex = c.getColumnIndexOrThrow
                        (LauncherSettings.Favorites.SPANX);
                final int spanYIndex = c.getColumnIndexOrThrow(
                        LauncherSettings.Favorites.SPANY);
                final int uriIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.URI);
                
                /** ID: configurable widget restore . AUT: chengliang . DATE: 2012.05.31 . S */
                final int needConfigIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.CONFIGABLE_WIDGET);
                /** ID: configurable widget restore . AUT: chengliang . DATE: 2012.05.31 . E */

                
                boolean isSingleLayer = SettingsValue.getSingleLayerValue(mContext);
                //PackageManager pm = mContext.getPackageManager();       
                while (!mStopped && c.moveToNext()) {
                	//boolean updateDatabase = false;
                    try {
                        int itemType = c.getInt(itemTypeIndex);
                        String intentDescription = c.getString(intentIndex);
                        long id = c.getLong(idIndex);
                        int container = c.getInt(containerIndex);
                        ShortcutInfo info = null;
                        Intent intent = null;
                        
                        
                        switch (itemType) {
                        case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
                        case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
                        case LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT:{
                        	/*RK_ID:RK_SD_APPS zhangdxa 2013-8-12. S***/
                        	//boolean updateDatabase = false;
                        	/*RK_ID:RK_SD_APPS zhangdxa 2013-8-12. E***/
                            try {
                                intent = Intent.parseUri(intentDescription, 0);
                            } catch (URISyntaxException e) {
                                continue;
                            }
                            if( itemType == LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT){
                            	Log.i("zdx1","XLauncherModel.loadWorkspace, remove ITEM_TYPE_COMMEND_SHORTCUT id:"+ id);
                            	itemsToRemove.add(id);
                            	break;
                            }
                            if (itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION /*||
                            	itemType == LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT*/) {
                         		info = getEntryShortcutInfo(manager, intent, context, c, iconIndex,
                                            titleIndex, mLabelCache,true);
                         		/*RK_ID:RK_SD_APPS zhangdxa 2013-8-12. S***/
                         		/*if( info != null ){
                         		    updateDatabase = info.itemType != itemType;
                         		}*/
                         		/*RK_ID:RK_SD_APPS zhangdxa 2013-8-12. E***/
                            } else {
                                info = getShortcutInfo(c, context, iconTypeIndex,
                                        iconPackageIndex, iconResourceIndex, iconIndex,
                                        titleIndex);
                            }
                            if (info != null) {
                                info.intent = intent;
                                info.id = id;
                                info.container = container;
                                info.screen = c.getInt(screenIndex);
                                info.cellX = c.getInt(cellXIndex);
                                info.cellY = c.getInt(cellYIndex);
                                /*RK_ID:RK_SD_APPS zhangdxa 2013-8-12. S***/
                                /*if (updateDatabase) {
                                	Log.i("zdx1","**************change db item:"+ info.title +", itemtype:"+ info.itemType);
                                	changedValue.add(info);
                                }*/
                                /*RK_ID:RK_SD_APPS zhangdxa 2013-8-12. E***/

                                // check & update map of what's occupied
                                if (!checkItemPlacement(occupied, info)) {
                                    /*PK_ID REMOVE_NO_DISPLAY_ITEM AUT:GECN1 .DATE 2013 -07-12 S*/
                                    itemsToRemove.add(id);
                                    /*PK_ID REMOVE_NO_DISPLAY_ITEM AUT:GECN1 .DATE 2013 -07-12 E*/
                                    break;
                                }
                                if(info.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION && isSingleLayer ){
                                    boolean itemUnExsited = ensure.add(new IntentInfo(info));
                                    if(!itemUnExsited){
                                        Log.d("gecn1", "---------*********************************");
                                        itemsToRemove.add(id);
                                        continue;
                                    }
                                    Log.d("gecn1", "---------" + info.intent.toString());
                                }
                                
                                switch (container) {
                                case LauncherSettings.Favorites.CONTAINER_DESKTOP:
                                case LauncherSettings.Favorites.CONTAINER_HOTSEAT:
                                    sWorkspaceItems.add(info);
//bugfix 17646
        	                        if(container == LauncherSettings.Favorites.CONTAINER_HOTSEAT){
        	                        	sHotseatItems.add(info);
        	                        }
                                    break;
                                default:
                                    // Item is in a user folder
                                    FolderInfo folderInfo =  findOrMakeFolder(sFolders, container);
                                    folderInfo.add(info);
                                    break;
                                }
                                sItemsIdMap.put(info.id, info);
                                // now that we've loaded everthing re-save it with the
                                // icon in case it disappears somehow.
                                queueIconToBeChecked(sDbIconCache, info, c, iconIndex);
                            } else {
                                // Failed to load the shortcut, probably because the
                                // activity manager couldn't resolve it (maybe the app
                                // was uninstalled), or the db row was somehow screwed up.
                                // Delete it.
                                Log.e(TAG, "Error loading shortcut " + id + ", removing it");
                                contentResolver.delete(LauncherSettings.Favorites.getContentUri(
                                            id, false), null, null);
                            }
                        }
                        break;

                        case LauncherSettings.Favorites.ITEM_TYPE_FOLDER:{
                            if(container != LauncherSettings.Favorites.CONTAINER_DESKTOP && container != LauncherSettings.Favorites.CONTAINER_HOTSEAT){
                                folderToRemove.add(id);
                                break;
                            }
                            FolderInfo folderInfo = findOrMakeFolder(sFolders, id);
                            folderInfo.title = c.getString(titleIndex);
                            folderInfo.id = id;
                            folderInfo.container = container;
                            folderInfo.screen = c.getInt(screenIndex);
                            folderInfo.cellX = c.getInt(cellXIndex);
                            folderInfo.cellY = c.getInt(cellYIndex);

                            /* RK_ID: RK_ICONSTYLE. AUT: liuli1 . DATE: 2012-05-15 . START */
                            setReplaceIcon(folderInfo, c);
                            /* RK_ID: RK_ICONSTYLE. AUT: liuli1 . DATE: 2012-05-15 . END */

                            // check & update map of what's occupied
                            if (!checkItemPlacement(occupied, folderInfo)) {
                                /*PK_ID REMOVE_NO_DISPLAY_ITEM AUT:GECN1 .DATE 2013 -07-12 S*/
                                folderToRemove.add(id);
                                /*PK_ID REMOVE_NO_DISPLAY_ITEM AUT:GECN1 .DATE 2013 -07-12 E*/
                                break;
                            }
                            switch (container) {
                                case LauncherSettings.Favorites.CONTAINER_DESKTOP:
                                case LauncherSettings.Favorites.CONTAINER_HOTSEAT:
                                    sWorkspaceItems.add(folderInfo);
//bugfix 17646
        	                        if(container == LauncherSettings.Favorites.CONTAINER_HOTSEAT){
        	                        	sHotseatItems.add(info);
        	                        }
                                    break;
                                default:
                                	break;
                            }
                            sItemsIdMap.put(folderInfo.id, folderInfo);
                            sFolders.put(folderInfo.id, folderInfo);
                        }
                        break;

                        case LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET:
                        case LauncherSettings.Favorites.ITEM_TYPE_COMMEND_APPWIDGET :{
                        	/*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. S***/
                        	try {
                                intent = Intent.parseUri(intentDescription, 0);
                                /*intent.getPackage();
                                try{                            			
                        			pm.getReceiverInfo(intent.getComponent(), 0);
                        			itemType = LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET;
                        		}catch (NameNotFoundException e) {
                        			itemType = LauncherSettings.Favorites.ITEM_TYPE_COMMEND_APPWIDGET;
    							}*/
                            } catch (URISyntaxException e) {
                                continue;
                            }catch (NullPointerException e) {
                            	//itemType = LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET;
                            }
                    		int appWidgetId = c.getInt(appWidgetIdIndex);
                    		LauncherAppWidgetInfo appWidgetInfo = new LauncherAppWidgetInfo(appWidgetId);
                            if( itemType == LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET){
                            	final AppWidgetProviderInfo provider = widgets.getAppWidgetInfo(appWidgetId);
                            	if (!isSafeMode && (provider == null || provider.provider == null ||
                                        provider.provider.getPackageName() == null)) {
                            		Log.i("zdx1","*******************XLauncherModel.remove****"+ appWidgetId);
                                    itemsToRemove.add(id);
                                } 
                            	/** ID: configurable widget restore . AUT: chengliang . DATE: 2012.05.31 . S */
                                appWidgetInfo.needConfig = c.getInt(needConfigIndex);
                                if( appWidgetInfo.needConfig == 1 ){
                                	appWidgetInfo.iconBitmap = getWidgetIconFromCursor(c, iconIndex);
                                }
                                /** ID: configurable widget restore . AUT: chengliang . DATE: 2012.05.31 . E */
                            /*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. S***/
                            }else {
                            	appWidgetInfo.iconBitmap = getWidgetIconFromCursor(c, iconIndex);
                                if( appWidgetInfo.iconBitmap == null){
                                	itemsToRemove.add(id);
                            		continue;
                            	}
                            }
                            /*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. E***/
                            
                            appWidgetInfo.itemType = itemType;
                            appWidgetInfo.id = id;
                            appWidgetInfo.screen = c.getInt(screenIndex);
                            appWidgetInfo.cellX = c.getInt(cellXIndex);
                            appWidgetInfo.cellY = c.getInt(cellYIndex);
                            appWidgetInfo.spanX = c.getInt(spanXIndex);
                            appWidgetInfo.spanY = c.getInt(spanYIndex);
                            appWidgetInfo.intent = intent;
                            appWidgetInfo.label = c.getString(titleIndex);

                            if (container != LauncherSettings.Favorites.CONTAINER_DESKTOP &&
                                container != LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
                                continue;
                            }
                            appWidgetInfo.container = container;
                            
                            if(itemType == LauncherSettings.Favorites.ITEM_TYPE_COMMEND_APPWIDGET){
                            	/*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. S***/
                            	/*appWidgetInfo.iconBitmap = getWidgetIconFromCursor(c, iconIndex);
                                String packageName = null;
                            	if (intent != null && intent.getComponent() != null){
                            		packageName = intent.getComponent().getPackageName();
                            	}
                            	appWidgetInfo.uri = getCommandAppDownloadUri(packageName, appWidgetInfo.label);*/
                            	appWidgetInfo.uri = c.getString(uriIndex);
                                /*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. E***/
                            }
                            // check & update map of what's occupied
                            if (!checkItemPlacement(occupied, appWidgetInfo)) {
                                break;
                            }
                            sItemsIdMap.put(appWidgetInfo.id, appWidgetInfo);
                            sAppWidgets.add(appWidgetInfo);
                        }  
                        break;
                        
                	    /*RK_ID: RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-09-26 START */            			
						case  LauncherSettings.Favorites.ITEM_TYPE_LEOSWIDGET :{
            			    LenovoWidgetViewInfo leosWidgetViewInfo = new LenovoWidgetViewInfo();
            			    leosWidgetViewInfo.id = id;
            			    leosWidgetViewInfo.className = c.getString(titleIndex);
            			    leosWidgetViewInfo.packageName = c.getString(uriIndex);
            			    leosWidgetViewInfo.container = container;
            			    leosWidgetViewInfo.screen = c.getInt(screenIndex);
            			    leosWidgetViewInfo.cellX = c.getInt(cellXIndex);
            			    leosWidgetViewInfo.cellY = c.getInt(cellYIndex);
            			    leosWidgetViewInfo.spanX = c.getInt(spanXIndex);
            			    leosWidgetViewInfo.spanY = c.getInt(spanYIndex);
            			    mLeosWidgetViews.add(leosWidgetViewInfo);
            			    sLeosWidgets.add(leosWidgetViewInfo);
            			    sItemsIdMap.put(leosWidgetViewInfo.id, leosWidgetViewInfo);
						}    
            			break;

            			default:
            				break;
                        }
                    } catch (Exception e) {
                        Log.w(TAG, "Desktop items loading interrupted:", e);
                    }
                }
            } catch(Exception e){
                Log.w(TAG, "Desktop items loading interrupted:", e);
            }
                finally {
            
                c.close();
            }

            if (itemsToRemove.size() > 0) {
                ContentProviderClient client = contentResolver.acquireContentProviderClient(
                                LauncherSettings.Favorites.CONTENT_URI);
                // Remove dead items
                for (long id : itemsToRemove) {
                    if (DEBUG_LOADERS) {
                        Log.d(TAG, "Removed id = " + id);
                    }
                    // Don't notify content observers
                    try {
                        client.delete(LauncherSettings.Favorites.getContentUri(id, false),
                                null, null);
                    } catch (RemoteException e) {
                        Log.w(TAG, "Could not remove id = " + id);
                    }
                }
            }
            if(folderToRemove.size() >0){
                ContentProviderClient client = contentResolver.acquireContentProviderClient(
                        LauncherSettings.Favorites.CONTENT_URI);
                for(Long id : folderToRemove){

                    FolderInfo info = sFolders.get(id);
                    if(info == null){
                        continue;
                    }
                    if (DEBUG_LOADERS) {
                        Log.d(TAG, "Removed  Folder id = " + id +"Folder name :" + info.title);
                        Log.d(TAG, "Removed  Folder contents = " + info.contents.toString());

                    }
                    try {
                        client.delete(LauncherSettings.Favorites.getContentUri(id, false),
                                null, null);
                        client.delete(LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION,
                                LauncherSettings.Favorites.CONTAINER + "=" + id, null);

                        for (ShortcutInfo childInfo : info.contents) {
                            sItemsIdMap.remove(childInfo.id);
                            sDbIconCache.remove(childInfo);
                            ensure.removeIntentInfo(new IntentInfo(childInfo));
                        }
                    } catch (RemoteException e) {
                        Log.w(TAG, "Could not remove id = " + id);
                    }
                    sItemsIdMap.remove(id);
                    sFolders.remove(id);
                    sDbIconCache.remove(info);
                    sWorkspaceItems.remove(info);
                }



                
            }
            
            /*RK_ID:RK_SD_APPS zhangdxa 2013-5-8. S***/
            if (changedValue.size() > 0) {
            	for (ItemInfo item : changedValue) {
            		updateItemInDatabase(mApp, item);
            	}
            }
            /*RK_ID:RK_SD_APPS zhangdxa 2013-5-8. E***/

            if (DEBUG_LOADERS) {
                Log.d(TAG, "loaded workspace in " + (SystemClock.uptimeMillis()-t) + "ms");
                Log.d(TAG, "workspace layout: ");
                for (int y = 0; y < mCellCountY; y++) {
                    String line = "";
                    for (int s = 0; s < mLauncherService.mScreenCount; s++) {
                        if (s > 0) {
                            line += " | ";
                        }
                        for (int x = 0; x < mCellCountX; x++) {
                            line += ((occupied[s][x][y] != null) ? "#" : ".");
                        }
                    }
                    Log.d(TAG, "[ " + line + " ]");
                }
            }
           
            try {
				putAllApplicationIntoWorkspace(ensure);
			} catch (Exception e) {
				Log.i(TAG, "Now Exception : " + e.getMessage());
			}
            
        }

        
        private class EnsureAllApps{
            private Set <IntentInfo> mSet = new HashSet<IntentInfo>();
            public boolean add(IntentInfo intentInfo){
                return mSet.add(intentInfo);
            }
            public boolean remove(IntentInfo intentInfo){
                return mSet.remove(intentInfo);
            }
            @Override
            public String toString() {
                return mSet.toString();
            }
            
            public Set <IntentInfo> getFilterIntentInfo(){
                return mSet;
            }
            
            public ShortcutInfo removeIntentInfo(IntentInfo intentInfo){
            	Iterator<IntentInfo>it = mSet.iterator();
            	IntentInfo temp = null;
            	while(it.hasNext()){
            		temp = it.next();
            		if(temp.equals(intentInfo)){
            			it.remove();
            			return temp.mInfo;
            		}
            	}
            	return null;
            }
            
        }
        
        private class IntentInfo{
        	
        	public IntentInfo(ShortcutInfo info){
        		mInfo = info;
        		//add by zhanggx1 for removing on 2013-11-13 . s
//        		mHashCode = mInfo.intent.filterHashCode();
        		mHashCode = filterIntentHashCode(info);
        		//add by zhanggx1 for removing on 2013-11-13 . e
        	}
        	public ShortcutInfo mInfo;
        	private final int mHashCode;
        	
        	 @Override
             public boolean equals(Object obj) {
                 if (obj instanceof IntentInfo) {
                	 IntentInfo other = (IntentInfo)obj;
                     Log.d("gecn1", "other.mInfo.intent  =" + other.mInfo.intent);
                     Log.d("gecn1", "mInfo.intent  =" + mInfo.intent);
                     if(other.mInfo.intent == null){
                    	 return false;
                     }
                   //add by zhanggx1 for removing on 2013-11-13 . s
                     
                     ComponentName cn = mInfo.intent.getComponent();
                     ComponentName otherCn = other.mInfo.intent.getComponent();
                     
                     if (cn == null && otherCn == null) {
                    	 return true;
                     }
                     
                     if (cn == null || otherCn == null) {
                    	 return false;
                     }
                     
                     return cn.equals(otherCn);
//                     return mInfo.intent.filterEquals(other.mInfo.intent);
                   //add by zhanggx1 for removing on 2013-11-13 . e
                 }
                 return false;
             }
             @Override
             public int hashCode() {
                 return mHashCode;
             }
             
             public int filterIntentHashCode(final ShortcutInfo info) {
             	if (info == null) {
             		return 0;
             	}
             	return filterIntentHashCode(info.intent);
             }
             
             public int filterIntentHashCode(final Intent intent) {
                 int code = 0;
                 if (intent != null) {                 	
                 	if (intent.getComponent() != null) {
                 		code += intent.getComponent().hashCode();
                 	}
                 }
                 return code;
             }
        }
        
        
        private void putAllApplicationIntoWorkspace(EnsureAllApps ensure){

            Set <IntentInfo> set = ensure.getFilterIntentInfo();
            Iterator<IntentInfo> it =null;
            ComponentName temp = null;
            if(mOtherAppList!= null){
                mOtherAppList.clear();
            }else{
                mOtherAppList = new LinkedList<ShortcutInfo>();
            }
            
            Iterator<ApplicationInfo> itR = mAllAppsList.data.iterator();
            boolean isUnExsit = true;
            ApplicationInfo r = null;
            while(itR.hasNext()){
                r = itR.next();
                it = set.iterator();
                isUnExsit = true;
                while(it.hasNext()){
                    temp = it.next().mInfo.intent.getComponent();
                    if(temp.equals(r.componentName)){
                        it.remove();
                        isUnExsit = false;
                        break;
                    }
                }
                if(isUnExsit){
                    Log.d("gecn1", "---------------未过滤 = " +r.title);
                    mOtherAppList.add(r.makeShortcut());
                }
            }

        }
        
        
        
        /**
         * Read everything out of our database.
         */
        private void bindWorkspace() {
            final long t = SystemClock.uptimeMillis();

            // Don't use these two variables in any of the callback runnables.
            // Otherwise we hold a reference to them.
            final Callbacks oldCallbacks = mCallbacks.get();
            
            if (oldCallbacks == null) {
                // This launcher has exited and nobody bothered to tell us.  Just bail.
                Log.w(TAG, "LoaderTask running with no launcher");
                return;
            }

            int N;
            // Tell the workspace that we're about to start firing items at it
            mHandler.post(new Runnable() {
                public void run() {
                    Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                    if (callbacks != null) {
                        callbacks.startBinding();
                    }
                }
            });

            // Unbind previously bound workspace items to prevent a leak of AppWidgetHostViews.
            final ArrayList<ItemInfo> workspaceItems = unbindWorkspaceItemsOnMainThread();

            // Add the items to the workspace.
            N = workspaceItems.size();
            for (int i=0; i<N; i+=ITEMS_CHUNK) {
                final int start = i;
                final int chunkSize = (i+ITEMS_CHUNK <= N) ? ITEMS_CHUNK : (N-i);
                mHandler.post(new Runnable() {
                    public void run() {
                        Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                        if (callbacks != null) {
                            callbacks.bindItems(workspaceItems, start, start+chunkSize);
                        }
                    }
                });
            }
            // Ensure that we don't use the same folders data structure on the main thread
            final HashMap<Long, FolderInfo> folders = new HashMap<Long, FolderInfo>(sFolders);
            mHandler.post(new Runnable() {
                public void run() {
                    Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                    if (callbacks != null) {
                        callbacks.bindFolders(folders);
                    }
                }
            });
            // Wait until the queue goes empty.
            mHandler.post(new Runnable() {
                public void run() {
                    if (DEBUG_LOADERS) {
                        Log.d(TAG, "Going to start binding widgets soon.");
                    }
                }
            });
            // Bind the widgets, one at a time.
            // WARNING: this is calling into the workspace from the background thread,
            // but since getCurrentScreen() just returns the int, we should be okay.  This
            // is just a hint for the order, and if it's wrong, we'll be okay.
            // TODO: instead, we should have that push the current screen into here.
            final int currentScreen = oldCallbacks.getCurrentWorkspaceScreen();
            N = sAppWidgets.size();
            // once for the current screen
            for (int i=0; i<N; i++) {
                final LauncherAppWidgetInfo widget = sAppWidgets.get(i);
                if (widget.screen == currentScreen) {
                    mHandler.post(new Runnable() {
                        public void run() {
                            Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                            if (callbacks != null) {
                                callbacks.bindAppWidget(widget);
                            }
                        }
                    });
                }
            }
            // once for the other screens
            for (int i=0; i<N; i++) {
                final LauncherAppWidgetInfo widget = sAppWidgets.get(i);
                if (widget.screen != currentScreen) {
                    mHandler.post(new Runnable() {
                        public void run() {
                            Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                            if (callbacks != null) {
                                callbacks.bindAppWidget(widget);
                            }
                        }
                    });
                }
            }
            
			/*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-09-26 START */ 
           N = mLeosWidgetViews.size();
            // once for the current screen
            for (int i=0; i<N; i++) {
                final LenovoWidgetViewInfo widget = mLeosWidgetViews.get(i);
                if (widget.screen == currentScreen) {
                    mHandler.post(new Runnable() {
                        public void run() {
                            Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                            if (callbacks != null) {
                                callbacks.bindLeosWidget(widget);
                            }
                        }
                    });
                }
            }
            // once for the other screens
            for (int i=0; i<N; i++) {
                final LenovoWidgetViewInfo widget = mLeosWidgetViews.get(i);
                if (widget.screen != currentScreen) {
                    mHandler.post(new Runnable() {
                        public void run() {
                            Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                            if (callbacks != null) {
                                callbacks.bindLeosWidget(widget);
                            }
                        }
                    });
                }
            }
			/*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-09-26 END */
            
            
            /*RK_ID: RK_DOCK_ADD . AUT: zhanggx1 . AUT: 2011-12-15 . S*/
            mHandler.post(new Runnable() {
                public void run() {                
                    Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                    if (callbacks != null) {
                        callbacks.initDockAddIcons();
                    } 
                }                
            });
            /*RK_ID: RK_DOCK_ADD . AUT: zhanggx1 . AUT: 2011-12-15 . E*/
            // Ensure that we don't use the same otherApplist data structure on the main thread
            if (mOtherAppList == null) {
            	mOtherAppList = new LinkedList<ShortcutInfo>();
            }
            final LinkedList<ShortcutInfo> otherApplist = new LinkedList<ShortcutInfo>(mOtherAppList);
            Log.d("other", "======== bindWorkspace  mOtherAppList" );
            mOtherAppList.clear();
            mOtherAppList = null;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                    if(callbacks != null){
                        callbacks.bindOtherApps(otherApplist);
                    }
                }
            });
            
            
			// Tell the workspace that we're done.
            mHandler.post(new Runnable() {
                public void run() {
                    Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                    if (callbacks != null) {
                        callbacks.finishBindingItems();
                    }
                }
            });
            // If we're profiling, this is the last thing in the queue.
            mHandler.post(new Runnable() {
                public void run() {
                    if (DEBUG_LOADERS) {
                        Log.d(TAG, "bound workspace in "
                            + (SystemClock.uptimeMillis()-t) + "ms");
                    }
                }
            });
            

        }

        private void loadAndBindAllApps() {
            if (DEBUG_LOADERS) {
                Log.d(TAG, "loadAndBindAllApps mAllAppsLoaded=" + mAllAppsLoaded);
            }
            if (!mAllAppsLoaded) {
                loadAllAppsByBatch();
                synchronized (LoaderTask.this) {
                    if (mStopped) {
                        return;
                    }
                    /** ID: fix bug: leos40/170148. AUT: chengliang . DATE: 2012.05.17 . S */                    
//                    if(! mLocaleJustChanged ){
                    	mAllAppsLoaded = true;
                    	
//                    }else{
//                    	Log.i(TAG, "Just Hack this.");
//                    	mAllAppsLoaded = false;
//                    }
                    /** ID: fix bug: leos40/170148. AUT: chengliang . DATE: 2012.05.17 . E */
//                    if(mIsIconStyleApplying) {
//                        Log.w("Test0303", " loadAndBindAllApps ... ");
//                        mIsIconStyleApplying = false;
//                        IconStyleSettings.finishSlef();
//                    }
                }
            } else {
                onlyBindAllApps();
            }
        }

        private void onlyBindAllApps() {
            final Callbacks oldCallbacks = mCallbacks.get();
            if (oldCallbacks == null) {
                // This launcher has exited and nobody bothered to tell us.  Just bail.
                Log.w(TAG, "LoaderTask running with no launcher (onlyBindAllApps)");
                return;
            }

            // shallow copy
            final ArrayList<ApplicationInfo> list
                    = (ArrayList<ApplicationInfo>)mAllAppsList.data.clone();
            mHandler.post(new Runnable() {
                public void run() {
                    final long t = SystemClock.uptimeMillis();
                    final Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                    if (callbacks != null) {
                        callbacks.bindAllApplications(list);
                    }
                    if (DEBUG_LOADERS) {
                        Log.d(TAG, "bound all " + list.size() + " apps from cache in "
                                + (SystemClock.uptimeMillis()-t) + "ms");
                    }
                }
            });

        }

        private void loadAllAppsByBatch() {
            final long t = DEBUG_LOADERS ? SystemClock.uptimeMillis() : 0;

            // Don't use these two variables in any of the callback runnables.
            // Otherwise we hold a reference to them.
            final Callbacks oldCallbacks = mCallbacks.get();
            if (oldCallbacks == null) {
                // This launcher has exited and nobody bothered to tell us.  Just bail.
                Log.w(TAG, "LoaderTask running with no launcher (loadAllAppsByBatch)");
                return;
            }

            final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

            final PackageManager packageManager = mContext.getPackageManager();
            List<ResolveInfo> apps = null;

            int N = Integer.MAX_VALUE;

            int startIndex;
            int i=0;
            int batchSize = -1;
            while (i < N && !mStopped) {
                if (i == 0) {
                    mAllAppsList.clear();
                    final long qiaTime = DEBUG_LOADERS ? SystemClock.uptimeMillis() : 0;
                    apps = packageManager.queryIntentActivities(mainIntent, PackageManager.GET_RESOLVED_FILTER);
                    if (DEBUG_LOADERS) {
                        Log.d(TAG, "queryIntentActivities took "
                                + (SystemClock.uptimeMillis()-qiaTime) + "ms");
                    }
                    if (apps == null) {
                        return;
                    }
                    N = apps.size();
                    if (DEBUG_LOADERS) {
                        Log.d(TAG, "queryIntentActivities got " + N + " apps");
                    }
                    if (N == 0) {
                        // There are no apps?!?
                        return;
                    }
                    if (mBatchSize == 0) {
                        batchSize = N;
                    } else {
                        batchSize = mBatchSize;
                    }

                    final long sortTime = DEBUG_LOADERS ? SystemClock.uptimeMillis() : 0;
                    Collections.sort(apps,
                            new XLauncherModel.ShortcutNameComparator(packageManager, mLabelCache));
                    if (DEBUG_LOADERS) {
                        Log.d(TAG, "sort took "
                                + (SystemClock.uptimeMillis()-sortTime) + "ms");
                    }
                }

                final long t2 = DEBUG_LOADERS ? SystemClock.uptimeMillis() : 0;

                startIndex = i;
                ApplicationInfo temp;  
                
                for (int j=0; i<N && j<batchSize; j++) {
                    // This builds the icon bitmaps.
                    /* RK_ID:  Do not display the lenovo theme icon. AUT: yumina . DATE: 2012-10-08 . S */
                    String packname = apps.get(i).activityInfo.packageName;
                    if(packname != null ) {
                    	if( packname.contains(LauncherPackageName) || packname.contains(LauncherThemePackage) || packname.contains(LockThemePackages) ){
                    
                            Log.e(TAG,"packname ============"+packname+" themename="+LauncherThemePackage+" lock packagename "+LockThemePackages);
                            i++;
                            continue;
                    	}
                    }
                    /* RK_ID:  Do not display the lenovo theme icon. AUT: yumina . DATE: 2012-10-08 . S */
                    temp = new ApplicationInfo(packageManager, apps.get(i),
                            mIconCache, mLabelCache);                                     
                    
                    if (mAllAppsList.isNewAddApk(mContext, (packname + "/" + apps.get(i).activityInfo.name)))
                    {                        
                        temp.mNewAdd = ShowStringInfo.SHOW_NEW;
                        temp.mNewString = "NEW";
                    }                    
                    else if (SettingsValue.isLauncherNotification(apps.get(i).filter))
                    {
                        String str = Settings.System.getString(mContext.getContentResolver(),"NEWMSG_" + temp.componentName.flattenToString());
                        if (str != null && !str.isEmpty())
                        {
                            temp.updateInfo(str);
                        }                                          
                    } 
                    
                    mAllAppsList.add(temp);
                    
                    i++;
                }

                final boolean first = i <= batchSize;
                final Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                final ArrayList<ApplicationInfo> added = mAllAppsList.added;
                mAllAppsList.added = new ArrayList<ApplicationInfo>();

                mHandler.post(new Runnable() {
                    public void run() {
                        final long t = SystemClock.uptimeMillis();
                        if (callbacks != null) {
                            if (first) {
                                callbacks.bindAllApplications(added);
                            } else {
                                callbacks.bindAppsAdded(added);
                            }
                            if (DEBUG_LOADERS) {
                                Log.d(TAG, "bound " + added.size() + " apps in "
                                    + (SystemClock.uptimeMillis() - t) + "ms");
                            }
                        } else {
                            Log.i(TAG, "not binding apps: no Launcher activity");
                        }
                    }
                });

                if (DEBUG_LOADERS) {
                    Log.d(TAG, "batch of " + (i-startIndex) + " icons processed in "
                            + (SystemClock.uptimeMillis()-t2) + "ms");
                }

                if (mAllAppsLoadDelay > 0 && i < N) {
                    try {
                        if (DEBUG_LOADERS) {
                            Log.d(TAG, "sleeping for " + mAllAppsLoadDelay + "ms");
                        }
                        Thread.sleep(mAllAppsLoadDelay);
                    } catch (InterruptedException exc) { }
                }
            }

            if (DEBUG_LOADERS) {
                Log.d(TAG, "cached all " + N + " apps in "
                        + (SystemClock.uptimeMillis()-t) + "ms"
                        + (mAllAppsLoadDelay > 0 ? " (including delay)" : ""));
            }
        }

        public synchronized void dumpState() {
            Log.d(TAG, "mLoaderTask.mContext=" + mContext);
            Log.d(TAG, "mLoaderTask.mWaitThread=" + mWaitThread);
            Log.d(TAG, "mLoaderTask.mIsLaunching=" + mIsLaunching);
            Log.d(TAG, "mLoaderTask.mStopped=" + mStopped);
            Log.d(TAG, "mLoaderTask.mLoadAndBindStepFinished=" + mLoadAndBindStepFinished);
            Log.d(TAG, "mItems size=" + sWorkspaceItems.size());
        }
    }

    void enqueuePackageUpdated(ReceiverTask task) {
        /*** AUT: zhaoxy . DATE: 2012-05-08. START***/
        //sWorker.post(task);
        sReceiverWorker.post(task);
        /*** AUT: zhaoxy . DATE: 2012-05-08. END***/
    }
    
    
    private class ReceiverTask implements Runnable{
        int mOp;
        String[] mPackages;
    	public ReceiverTask(int op, String[] packages){
            mOp = op;
            mPackages = packages;
    	}
		@Override
		public void run() {
			synchronized (mLock) {
				 if(!isLoadTaskHasFirstLoaded) {
					 try {
						if(DEBUG_LOADERS){
							Log.d(TAG, "==============SD WAIT LoadTask");
						}
						mLock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				 }
				 if(DEBUG_LOADERS){
					Log.d(TAG, "==============post packageUpdateTask");
				 }
				 /**
				  * setDirty的原因：在updateTask更新ui的时候，会写数据库，如果此时在写数据库之前 forceRload等类似操作，则会导致数据不一致
				  */
				 setDatabaseDirty(true);
				 sWorker.post(new PackageUpdatedTask(mOp, mPackages));
			}
			
		}
    	
    }
    
    private class PackageUpdatedTask implements Runnable {
        int mOp;
        String[] mPackages;

        public static final int OP_NONE = 0;
        public static final int OP_ADD = 1;
        public static final int OP_UPDATE = 2;
        public static final int OP_REMOVE = 3; // uninstlled
        public static final int OP_UNAVAILABLE = 4; // external media unmounted
        /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. START***/
        public static final int OP_HIDDEN = 5; // set app hidden
        /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. END***/
        public static final int OP_AVALIABLE_ADD = 6;


        public PackageUpdatedTask(int op, String[] packages) {
            mOp = op;
            mPackages = packages;
        }

        public void run() {
            /* RK_ID: RK_LAUNCHERMODEL. AUT: liuli1 . DATE: 2012-05-15 . START */
            if (DEBUG_LOADERS) {
                Log.i(TAG, "PackageUpdatedTask run begin ." + mPackages.length + "  mAllAppsList = " + mAllAppsList);
            }
            /* RK_ID: RK_LAUNCHERMODEL. AUT: liuli1 . DATE: 2012-05-15 . END */
            /*** AUT: zhaoxy . DATE: 2012-05-08. END***/
            final Context context = mApp;

            final String[] packages = mPackages;
            final int N = packages.length;
            switch (mOp) {
                case OP_ADD:
                    for (int i=0; i<N; i++) {
                        if (DEBUG_LOADERS) Log.d(TAG, "mAllAppsList.addPackage " + packages[i]);
                        mAllAppsList.addPackage(context, packages[i]);
                    }
                    break;
                case OP_UPDATE:
                    for (int i=0; i<N; i++) {
                        if (DEBUG_LOADERS) Log.d(TAG, "mAllAppsList.updatePackage " + packages[i]);
                        mAllAppsList.updatePackage(context, packages[i]);
                    }
                    break;
                case OP_REMOVE:
                    for (int i=0; i<N; i++) {
                        if (DEBUG_LOADERS) Log.d(TAG, "mAllAppsList.removePackage " + packages[i]);
                        mAllAppsList.removePackage(context, packages[i], true);
                    }
                    break;
                case OP_UNAVAILABLE:
                    for (int i=0; i<N; i++) {
                        if (DEBUG_LOADERS) Log.d(TAG, "mAllAppsList.removePackage OP_UNAVAILABLE" + packages[i]);
                        mAllAppsList.removePackage(context, packages[i], false);
                    }
                    break;
                /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. START***/
                case OP_HIDDEN:
                    mAllAppsList.hideApps(packages);
                    break;
                /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. END***/
                case OP_AVALIABLE_ADD:
                    for (int i=0; i<N; i++) {
                        if (DEBUG_LOADERS) Log.d(TAG, "mAllAppsList.addPackage " + packages[i]);
                        //yangmao add it for lock,we need to hiden the jingling.apk 0528 start
                        if(packages[i].contains(LockThemePackages)){                      	
                        	continue;
                        }
                      //yangmao add it for lock,we need to hiden the jingling.apk 0528 end
                        mAllAppsList.addPackageNoNew(context, packages[i]);
                    }
                    break;
                default:
                	break;
            }
            /* RK_ID: zhanghong5. AUT: zhanghong5 . DATE: 2012-06-20 . START */
            Intent intent = new Intent(SettingsValue.ACTION_ALLAPPLIST_CHANGED);
            Log.d(TAG, "send ACTION_ALLAPPLIST_CHANGED");
            context.sendBroadcast(intent);
            /* RK_ID: zhanghong5. AUT: zhanghong5 . DATE: 2012-06-20 . END */
            ArrayList<ApplicationInfo> added = null;
            ArrayList<ApplicationInfo> removed = null;
            ArrayList<ApplicationInfo> modified = null;
            /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. START***/
            ArrayList<ApplicationInfo> hidden = null;
            /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. END***/
//            ArrayList<ApplicationInfo> avialiable_added = null;


            /* RK_ID: RK_LAUNCHERMODEL. AUT: liuli1 . DATE: 2012-05-15 . START */
            if (DEBUG_LOADERS) {
                Log.i(TAG, "mAllAppsList.added = " + mAllAppsList.added);
                Log.i(TAG, "mAllAppsList.removed = " + mAllAppsList.removed);
                Log.i(TAG, "mAllAppsList.modified = " + mAllAppsList.modified);
                Log.i(TAG, "mHandler = " + mHandler);
            }
            /* RK_ID: RK_LAUNCHERMODEL. AUT: liuli1 . DATE: 2012-05-15 . END */
            if (mAllAppsList.added.size() > 0) {
                added = mAllAppsList.added;
                mAllAppsList.added = new ArrayList<ApplicationInfo>();
            }
            if (mAllAppsList.removed.size() > 0) {
                removed = mAllAppsList.removed;
                mAllAppsList.removed = new ArrayList<ApplicationInfo>();
                for (ApplicationInfo info: removed) {
                    /* RK_ID: RK_LAUNCHERMODEL. AUT: liuli1 . DATE: 2012-05-15 . START */
                    if (DEBUG_LOADERS) {
                        Log.i(TAG, "handle mAllAppsList.removed, info.intent = " + info.intent);
                    }
                    /* RK_ID: RK_LAUNCHERMODEL. AUT: liuli1 . DATE: 2012-05-15 . END */
                    mIconCache.remove(info.intent.getComponent());
                }
            }
            if (mAllAppsList.modified.size() > 0) {
                modified = mAllAppsList.modified;
                mAllAppsList.modified = new ArrayList<ApplicationInfo>();
            }
            /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. START***/
            if (mAllAppsList.hidden.size() > 0) {
                hidden = mAllAppsList.hidden;
                mAllAppsList.hidden = new ArrayList<ApplicationInfo>();
            }
            /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. END***/
            
//            if (mAllAppsList.avialiable_added.size() > 0) {
//                avialiable_added = mAllAppsList.avialiable_added;
//                mAllAppsList.avialiable_added = new ArrayList<ApplicationInfo>();
//            }

            final Callbacks callbacks = mCallbacks != null ? mCallbacks.get() : null;
            if (callbacks == null) {
                Log.w(TAG, "Nobody to tell about the new app.  Launcher is probably loading.");
                return;
            }


            if (modified != null) {
                final ArrayList<ApplicationInfo> modifiedFinal = modified;
                mHandler.post(new Runnable() {
                    public void run() {
                        Callbacks cb = mCallbacks != null ? mCallbacks.get() : null;
                        if (callbacks == cb && cb != null) {
                            callbacks.bindAppsUpdated(modifiedFinal);
                        }
                    }
                });
            }
            if (removed != null) {
                final boolean permanent = mOp != OP_UNAVAILABLE;
                final ArrayList<ApplicationInfo> removedFinal = removed;
                mHandler.post(new Runnable() {
                    public void run() {
                        Callbacks cb = mCallbacks != null ? mCallbacks.get() : null;
                        if (callbacks == cb && cb != null) {
                            callbacks.bindAppsRemoved(removedFinal, permanent);
                        }
                    }
                });
            }
            
            /*AUTH:GECN1  NOTE:bindAppsAdded method must be following bindAppsRemoved*/
            if (added != null) {
                final ArrayList<ApplicationInfo> addedFinal = added;
                mHandler.post(new Runnable() {
                    public void run() {
                        Callbacks cb = mCallbacks != null ? mCallbacks.get() : null;
                        if (callbacks == cb && cb != null) {
                            callbacks.bindAppsAdded(addedFinal);
                        }
                    }
                });
            }
            /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. START***/
            if (hidden != null) {
                final ArrayList<ApplicationInfo> hiddenFinal = hidden;
                mHandler.post(new Runnable() {
                    public void run() {
                        Callbacks cb = mCallbacks != null ? mCallbacks.get() : null;
                        if (callbacks == cb && cb != null) {
                            callbacks.bindAppsHidden(hiddenFinal);
                        }
                    }
                });
            }
            /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. END***/
            
//            if (avialiable_added != null) {
//                final ArrayList<ApplicationInfo> avialiable_addedFinal = avialiable_added;
//                mHandler.post(new Runnable() {
//                    public void run() {
//                        Callbacks cb = mCallbacks != null ? mCallbacks.get() : null;
//                        if (callbacks == cb && cb != null) {
//                            callbacks.bindAppsAvialiableAdded(avialiable_addedFinal);
//                        }
//                    }
//                });
//            }
            
//            ArrayList<ItemInfo> workspaceItems;
//            synchronized (sWorkspaceItems) {
				 /*RK_ID: RK_FIX_BUG . AUT: zhanggx1 . DATE: 2012-05-24 . PUR: for bug 165200 . S*/
//				 workspaceItems = (ArrayList<ItemInfo>)sWorkspaceItems.clone();
                /*RK_ID: RK_FIX_BUG . AUT: zhanggx1 . DATE: 2012-0            mHandler.post(new Runnable() {
            	
            	
				@Override
				public void run() {
					Callbacks cb = mCallbacks != null ? mCallbacks.get() : null;
					 if (callbacks == cb && cb != null) {
						 
						 
						 LinkedList<ItemInfo> items = new LinkedList<ItemInfo>();
						 LinkedList<ItemInfo>commendItems = new LinkedList<ItemInfo>();
						 filterCommendShortcut(workspaceItems, packages, items, commendItems);
						 boolean bCommend = false;
						 if( mOp == OP_UNAVAILABLE){
	               	            bCommend = true;
	               	     }
						 callbacks.updateItemsCommend(commendItems, bCommend);
						 callbacks.
					 }
				}
			});5-24 . PUR: for bug 165200 . E*/
//			 }
            
            
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                	Callbacks cb = mCallbacks != null ? mCallbacks.get() : null;
                    synchronized (sAppWidgets) {
                        if( ( sAppWidgets.size() > 0 )&& (packages.length > 0 )&& 
                            (mOp == OP_ADD || mOp == OP_UNAVAILABLE || mOp == OP_AVALIABLE_ADD) ) {
                 		  ArrayList<ItemInfo> list = new ArrayList<ItemInfo>();
                          Log.i("zdx1","*******************sync sAppWidgets**********"+ mOp);
                          ArrayList<ItemInfo> appWidgets = (ArrayList<ItemInfo>)sAppWidgets.clone();
                   		  if(  mOp == OP_UNAVAILABLE ){
                              for(ItemInfo info : appWidgets) {          			
                   			      if(info instanceof LauncherAppWidgetInfo){
                   				      LauncherAppWidgetInfo widgetInfo = (LauncherAppWidgetInfo) info;
                   				      AppWidgetProviderInfo appWidgetInfo =  AppWidgetManager.getInstance(context)
                   						.getAppWidgetInfo(widgetInfo.appWidgetId );
                   				      if( ( appWidgetInfo == null ) && 
                   				    	  ( widgetInfo.itemType != Favorites.ITEM_TYPE_COMMEND_APPWIDGET)){                   					  
                   					      list.add(info);
                   				      }
                   			      }
                   		      }
                   		  }else {
                   			  for(ItemInfo info : appWidgets) {          			
                     			  if(info instanceof LauncherAppWidgetInfo){
                     				  LauncherAppWidgetInfo widgetInfo = (LauncherAppWidgetInfo) info;
                     				  for (int i=0; i<N; i++) {
                             			 String packageName = packages[i];
                             			 Intent intent = widgetInfo.intent;
                             			 if( intent != null && intent.getComponent() != null &&
                             				intent.getComponent().getPackageName() != null){
                             				 String widgetPackageName = intent.getComponent().getPackageName();
                     				         if(( widgetInfo.itemType == Favorites.ITEM_TYPE_COMMEND_APPWIDGET) &&
                     						     ( mOp == OP_ADD || mOp == OP_AVALIABLE_ADD) &&
                     						     widgetPackageName.equals(packageName)){
                     					         list.add(info);
                     				         }
                             			 }
                     				 }
                     			  }
                   			  }
                   		  }
                   		  appWidgets.clear();
                   		  appWidgets = null;
                          if (callbacks == cb && cb != null && list.size() > 0) {
                   			  callbacks.removeWidgets(list);
   	                      }
                       }
                    }
                    /*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. E***/
                }
            }); 
            
			 /**
			  * setDirty的原因：在updateTask更新ui的时候，会写数据库，如果此时在写数据库之前 forceRload等类似操作，则会导致数据不一致
			  * 需要在所有的数据库操作完成後，才能能设置false，所以需要通过ui线程 post一个swork的runnable
			  */
            //ensure dirty is false
            mHandler.post(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Log.d("gecn1", "package update task set data dirty false");
					sWorker.post(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							setDatabaseDirty(false);
						}
					});
					
					
				}
			});
        }
    }

    /**
     * This is called from the code that adds shortcuts from the intent receiver.  This
     * doesn't have a Cursor, but
     */
    public ShortcutInfo getShortcutInfo(PackageManager manager, Intent intent, Context context) {
        return getShortcutInfo(manager, intent, context, null, -1, -1, null,false);
    }
    
    
    /**
    * gecn1
    * 获取applicon entry info 
    * @return
    */
    public ShortcutInfo getEntryShortcutInfo(PackageManager manager, Intent intent, Context context,
                Cursor c, int iconIndex, int titleIndex, HashMap<Object, CharSequence> labelCache,boolean isApplication){
           return getShortcutInfo(manager, intent, context, c, iconIndex, titleIndex, labelCache,isApplication);
           
        }

    /**
     * Make an ShortcutInfo object for a shortcut that is an application.
     *
     * If c is not null, then it will be used to fill in missing data like the title and icon.
     */
    private ShortcutInfo getShortcutInfo(PackageManager manager, Intent intent, Context context,
    		Cursor c, int iconIndex, int titleIndex, HashMap<Object, CharSequence> labelCache,boolean isApplication) {
        Bitmap icon = null;
        final ShortcutInfo info = new ShortcutInfo();
        PackageManager pm = context.getPackageManager();
        boolean isFound = isApplication;
        
        ComponentName componentName = intent.getComponent();
        if (componentName == null) {
            return null;
        }
        
        /*RK_ID:RK_SD_APPS zhangdxa 2013-8-12. S***/
        /*try{        	
        	pm.getApplicationInfo(componentName.getPackageName(), 0);
        	info.itemType = LauncherSettings.Favorites.ITEM_TYPE_APPLICATION;
        	info.uri = null;
        	//Log.i("zdx1","**********LauncherModel->getShortcutInfo , in pm, componentName:"+componentName);
        }catch (NameNotFoundException e) {
        	info.itemType = LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT;
            info.uri = getCommandAppDownloadUri(componentName.getPackageName(), null);
        	Log.i("zdx1","**********LauncherModel->getShortcutInfo , not found in pm, componentName:"+componentName);
		}*/
        info.itemType = LauncherSettings.Favorites.ITEM_TYPE_APPLICATION;
        
        try {
            PackageInfo pi = manager.getPackageInfo(componentName.getPackageName(), 0);
            if (!pi.applicationInfo.enabled) {
                // If we return null here, the corresponding item will be removed from the launcher
                // db and will not appear in the workspace.
                return null;
            }
        } catch (NameNotFoundException e) {
            Log.d(TAG, "getPackInfo failed for package " + componentName.getPackageName());
            isFound = false;
        }
 
        //if(info.itemType != LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT){
        /*RK_ID:RK_SD_APPS zhangdxa 2013-2013-8-12. E***/
            // TODO: See if the PackageManager knows about this case.  If it doesn't
            // then return null & delete this.

            // the resource -- This may implicitly give us back the fallback icon,
            // but don't worry about that.  All we're doing with usingFallbackIcon is
            // to avoid saving lots of copies of that in the database, and most apps
            // have icons anyway.
            //final ResolveInfo resolveInfo = manager.resolveActivity(intent, 0);
            
            /*RK_RESOLVEINFO 2013.03.05 dining S*/
            //use the BU launcher code from zengzm 
            //final ResolveInfo resolveInfo = manager.resolveActivity(intent, 0);
            	        
            ResolveInfo resolveInfo = null;
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            String packageName = componentName.getPackageName();
            String classname = componentName.getClassName();
              
            /***RK_ID:RK_BUGFIX zhangdxa 2013-6-27. S***/
            /*if(mAppListCacheTempLenovo==null) {
                mAppListCacheTempLenovo = manager.queryIntentActivities(mainIntent, PackageManager.GET_RESOLVED_FILTER);
            }
            String packageName = componentName.getPackageName();
            String classname = componentName.getClassName();
            if (mAppListCacheTempLenovo !=null && mAppListCacheTempLenovo.size() > 0) {
                for (ResolveInfo tempInfo : mAppListCacheTempLenovo) {
                    if (tempInfo != null) {
                        if (packageName.equals(tempInfo.activityInfo.applicationInfo.packageName)
                                && classname.equals(tempInfo.activityInfo.name)) {
                            resolveInfo = tempInfo;
                            break;
                        }
                    }
                }
            }*/
            mainIntent.setPackage(packageName);
	        List<ResolveInfo> apps = pm.queryIntentActivities(mainIntent, PackageManager.GET_DISABLED_COMPONENTS | PackageManager.GET_RESOLVED_FILTER);
	        if (apps != null && apps.size() > 0) {
	            int size = apps.size();
	            for (int index = 0; index < size; index++) {
	                 ResolveInfo temp = apps.get(index);
	                 if (temp.activityInfo.name.equals(classname)) {
	                	 resolveInfo = temp;
	                     break;
	                 }
	            }
	        }
	        /***RK_ID:RK_BUGFIX zhangdxa 2013-6-27. E***/

            if (resolveInfo == null) {
            	if(isFound){
            		Log.d("gecn2", "=======================found");
            		return null;
            	}else{
            		Log.d("gecn2", "=======================not found");

            	}
                resolveInfo = manager.resolveActivity(intent, PackageManager.GET_RESOLVED_FILTER);
            }
            
            /*RK_RESOLVEINFO 2013.03.05 dining E*/

            if (resolveInfo != null) {
                icon = mIconCache.getIcon(componentName, resolveInfo, labelCache);
            }

            /* AUT: liuli liuli0120@use.com.cn DATE: 2012-01-10 START */
            // for icon, we find the custom bitmap first
            setReplaceIcon(info, c, context);

            // the db
            if (icon == null) {
                if (c != null) {
                    icon = getIconFromCursor(c, iconIndex, context);
                }
            }
            
            // from the resource
            if (resolveInfo != null) {
                ComponentName key = Utilities.getComponentNameFromResolveInfo(resolveInfo);
                if (labelCache != null && labelCache.containsKey(key)) {
                    info.title = labelCache.get(key);
                } else {
                    info.title = resolveInfo.activityInfo.loadLabel(manager);
                    if (labelCache != null) {
                        labelCache.put(key, info.title);
                    }
                } 
            }
            
//            if (mAllAppsList.isNewAddApk(context, componentName.flattenToString()))
//            {
//                info.mNewAdd = 1;
//                info.mNewString = "new";
//            }
//            else 
            
        //}
            
        if (mAllAppsList.isNewAddApk(context, componentName.flattenToString()))
        {                        
            info.mNewAdd = ShowStringInfo.SHOW_NEW;
            info.mNewString = "NEW";
        } 
        else if (resolveInfo != null && resolveInfo.filter != null && SettingsValue.isLauncherNotification(resolveInfo.filter))
        {                    
            String str = Settings.System.getString(context.getContentResolver(),"NEWMSG_" + componentName.flattenToString());
            R5.echo("getShorcutInfo pkgName = " + componentName.flattenToString() + "   num = " + str);
            if (str != null && !str.isEmpty())
            {
                info.updateInfo(str);
            }                                          
        }
        
        // the fallback icon
        if (icon == null) {
        	if(DEBUG_LOADERS)R5.echo("usingFallbackIcon");
            icon = getFallbackIcon();
            info.usingFallbackIcon = true;
        }
        info.setIcon(icon);

        // for replace title, we query database first
        // from the db
        if (c != null) {
            final int index = c.getColumnIndexOrThrow(LauncherSettings.Favorites.TITLE_REPLACE);
            info.replaceTitle = c.getString(index);
        }
        /* AUT: liuli liuli0120@use.com.cn DATE: 2012-01-10 END */

        // from the db
        if (info.title == null) {
        	if(DEBUG_LOADERS)R5.echo("usingFallbackTitle");
            if (c != null) {
                info.title =  c.getString(titleIndex);
            }
        }
        // fall back to the class name of the activity
        if (info.title == null) {
            info.title = componentName.getClassName();
        }        

        if(DEBUG_LOADERS)R5.echo("getShortcutInfo 1 info = " + info.title);
        return info;
    }
    
    
    public ShortcutInfo getShortcutInfo(PackageManager manager,Context context,ResolveInfo resolveInfo,HashMap<Object, CharSequence> labelCache){
        if (resolveInfo == null) {
            return null;
        }
        final ShortcutInfo info = new ShortcutInfo();
        ComponentName temp = new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
        info.setActivity(temp, Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        info.itemType = LauncherSettings.Favorites.ITEM_TYPE_APPLICATION;
        mIconCache.getIcon(temp, resolveInfo, labelCache);
        if (labelCache != null && labelCache.containsKey(temp)) {
            info.title = labelCache.get(temp);
        } else {
            info.title = resolveInfo.activityInfo.loadLabel(manager);
            if (labelCache != null) {
                labelCache.put(temp, info.title);
            }
        }
        
        if (mAllAppsList.isNewAddApk(context, temp.flattenToString()))
        {                        
            info.mNewAdd = ShowStringInfo.SHOW_NEW;
            info.mNewString = "NEW";
        }
        else if (resolveInfo.filter != null && resolveInfo.filter.hasCategory("android.intent.category.LENOVO_LAUNCHER_NOTIFICAITON"))
        {
            String str = Settings.System.getString(context.getContentResolver(),"NEWMSG_" + temp.flattenToString());
            if (str != null && !str.isEmpty())
            {
                info.updateInfo(str);
            }                                          
        }
        if(DEBUG_LOADERS)R5.echo("getShortcutInfo 2 info = " + info.title);
        return info;
    }
    
    
    
    public static final String EXTRA_SHORTCUT_LABEL_RESOURCE = "lenovo.intent.extra.shortcut.LABEL_RESOURCE";
    public static final String EXTRA_SHORTCUT_LABEL_RESNAME = "lenovo.intent.extra.LABEL_RESNAME";

    /**
     * Make an ShortcutInfo object for a shortcut that isn't an application.
     */
    private ShortcutInfo getShortcutInfo(Cursor c, Context context,
            int iconTypeIndex, int iconPackageIndex, int iconResourceIndex, int iconIndex,
            int titleIndex) {
        Bitmap icon = null;
        final ShortcutInfo info = new ShortcutInfo();
        info.itemType = LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT;

        // TODO: If there's an explicit component and we can't install that, delete it.

        info.title = c.getString(titleIndex);

        // add by zhanglq   s  
        final int intentIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.INTENT);
        String intentDescription = c.getString(intentIndex);
        Intent intent = null;
        try {
            intent = Intent.parseUri(intentDescription, 0);
            if (intent != null && intent.hasExtra(EXTRA_SHORTCUT_LABEL_RESOURCE)
                    && intent.getIntExtra(EXTRA_SHORTCUT_LABEL_RESOURCE, 0) != 0) {
                info.title = context.getString(intent.getIntExtra(EXTRA_SHORTCUT_LABEL_RESOURCE, 0));
            }
            if (intent != null && intent.hasExtra(EXTRA_SHORTCUT_LABEL_RESNAME)) {
                String resourceName = intent.getStringExtra(EXTRA_SHORTCUT_LABEL_RESNAME);
                int resourceId = context.getResources().getIdentifier(resourceName, null, null);
                if (resourceId != 0)
                    info.title = context.getString(resourceId);
            }
            
            if (intent != null && intent.getComponent() != null
                    && mAllAppsList.isNewAddApk(context, intent.getComponent().flattenToString()))
            {                        
                info.mNewAdd = ShowStringInfo.SHOW_NEW;
                info.mNewString = "NEW";
            }
            else if (intent != null
                  && SettingsValue.isLauncherNotification(intent) 
                  && intent.getComponent() != null)
            {
                String str = Settings.System.getString(context.getContentResolver(),"NEWMSG_" + intent.getComponent().flattenToString());
                R5.echo("getShorcutInfo pkgName = " + intent.getComponent().flattenToString() + "   num = " + str);
                if (str != null && !str.isEmpty())
                {
                    info.updateInfo(str);
                }                                          
            } 
        } catch (URISyntaxException e) {
        }
        // add by zhanglq   e  
        if (info.title == null) {
        	info.title = "";
        }

        int iconType = c.getInt(iconTypeIndex);
        switch (iconType) {
        case LauncherSettings.Favorites.ICON_TYPE_RESOURCE:
            String packageName = c.getString(iconPackageIndex);
            String resourceName = c.getString(iconResourceIndex);
            PackageManager packageManager = context.getPackageManager();
            info.customIcon = false;
            // the resource
            try {
                Resources resources = packageManager.getResourcesForApplication(packageName);
                if (resources != null) {
                	/*RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2012-07-02 . S*/
                    info.iconResource = new Intent.ShortcutIconResource();
                    info.iconResource.packageName = packageName;
                    info.iconResource.resourceName = resourceName;
                    /*RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2012-07-02 . E*/
                    
                    final int id = resources.getIdentifier(resourceName, null, null);
                    /* RK_ID: ICONSTYLE. AUT: liuli1 . DATE: 2012-02-29 . START */
//                    icon = Utilities.createIconBitmap(mIconCache.getFullResIcon(resources, id),
//                            context, packageName);
                    icon = mApp.mLauncherContext.getIconBitmap(resources, id, packageName);
                    /* RK_ID: ICONSTYLE. AUT: liuli1 . DATE: 2012-02-29 . END */
                }
            } catch (Exception e) {
                // drop this.  we have other places to look for icons
            }
            // the db
            if (icon == null) {
                icon = getIconFromCursor(c, iconIndex, context);
            }
            // the fallback icon
            if (icon == null) {
                icon = getFallbackIcon();
                info.usingFallbackIcon = true;
            }
            break;
        case LauncherSettings.Favorites.ICON_TYPE_BITMAP:
            icon = getIconFromCursor(c, iconIndex, context);
            if (icon == null) {
                icon = getFallbackIcon();
                info.customIcon = false;
                info.usingFallbackIcon = true;
            } else {
                info.customIcon = true;
            }
            break;
        default:
            icon = getFallbackIcon();
            info.usingFallbackIcon = true;
            info.customIcon = false;
            break;
        }

        /* AUT: liuli liuli0120@use.com.cn DATE: 2012-01-10 START */
        // for icon, we find the custom bitmap first
        setReplaceIcon(info, c, context);
        // add replace title
        int replaceTitleIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.TITLE_REPLACE);
        info.replaceTitle = c.getString(replaceTitleIndex);
        /* AUT: liuli liuli0120@use.com.cn DATE: 2012-01-10 END */
                
        info.setIcon(icon);
        if(DEBUG_LOADERS)R5.echo("getShortcutInfo 3 info = " + info.title);
        return info;
    }

    /*
     * set replace icon, including theme's icon and user's crop
     */
    private void setReplaceIcon(ShortcutInfo info, Cursor c, Context context) {
        Bitmap iconReplace = null;

        if (c != null) {
            final int index = c.getColumnIndexOrThrow(LauncherSettings.Favorites.ICON_REPLACE);

            byte[] data = c.getBlob(index);
            if (data == null) {
                iconReplace = null;
            } else {
                try {
                    iconReplace = Utilities.createIconBitmap(BitmapFactory.decodeByteArray(data, 0, data.length),
                            context);
                } catch (Exception e) {
                    iconReplace = null;
                }
            } // end if-else
        }

        info.setReplaceIcon(iconReplace);
    }

    // bug 164723
    private void setReplaceIcon(FolderInfo info, Cursor c) {
        if (c != null) {
            final int index = c.getColumnIndexOrThrow(LauncherSettings.Favorites.ICON_REPLACE);

            byte[] data = c.getBlob(index);
            if (data == null) {
                info.mReplaceIcon = null;
            } else {
                try {
                    info.mReplaceIcon = BitmapFactory.decodeByteArray(data, 0, data.length);
                } catch (Exception e) {
                    info.mReplaceIcon = null;
                }
            } // end if-else
        }
    }

    // for icon style issue, 164729
    public static Bitmap findViewIconFromCache(ItemInfo info) {
        if ((info.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION || 
        	info.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT
        	/*RK_ID:RK_SD_APPS zhangdxa 2013-4-15. S***/
        	||info.itemType == LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT
        	/*RK_ID:RK_SD_APPS zhangdxa 2013-4-15. E***/)
                && XLauncherModel.sViewIconCache.containsKey(info.id)) {
            return XLauncherModel.sViewIconCache.get(info.id);
        }
        return null;
    }

    public static void clearViewCache() {
        sViewIconCache.clear();
    }

    public static void addViewIconCache(long id, Bitmap bitmap) {
        if (sViewIconCache != null && !sViewIconCache.containsKey(id))
            sViewIconCache.put(id, bitmap);
    }

    /* AUT: liuli liuli0120@use.com.cn DATE: 2012-01-12 END */

    public Bitmap getIconFromCursor(Cursor c, int iconIndex, Context context) {
        if (DEBUG_LOADERS) {
            Log.d(TAG, "getIconFromCursor app="
                    + c.getString(c.getColumnIndexOrThrow(LauncherSettings.Favorites.TITLE)));
        }
        byte[] data = c.getBlob(iconIndex);
        try {
            return Utilities.createIconBitmap(
                    BitmapFactory.decodeByteArray(data, 0, data.length), context);
        } catch (Exception e) {
            return null;
        }
    }
    
    /** AUT: zhanglq@bj.cobellink.com DATE: 2012-2-10 start*/
    public Bitmap getWidgetIconFromCursor(Cursor c, int iconIndex) {
        if (DEBUG_LOADERS) {
            Log.d(TAG, "getIconFromCursor app="
                    + c.getString(c.getColumnIndexOrThrow(LauncherSettings.Favorites.TITLE)));
        }
        byte[] data = c.getBlob(iconIndex);
        try {
            return BitmapFactory.decodeByteArray(data, 0, data.length);
        } catch (Exception e) {
            return null;
        }
    }
    /** AUT: zhanglq@bj.cobellink.com DATE: 2012-2-10 end*/

    ShortcutInfo addShortcut(Context context, Intent data, long container, int screen,
            int cellX, int cellY, boolean notify) {
        final ShortcutInfo info = infoFromShortcutIntent(context, data, null);
        addItemToDatabase(context, info, container, screen, cellX, cellY, notify);

        return info;
    }

    /**
     * Attempts to find an AppWidgetProviderInfo that matches the given component.
     */
    AppWidgetProviderInfo findAppWidgetProviderInfoWithComponent(Context context,
            ComponentName component) {
        List<AppWidgetProviderInfo> widgets =
            AppWidgetManager.getInstance(context).getInstalledProviders();
        for (AppWidgetProviderInfo info : widgets) {
            if (info.provider.equals(component)) {
                return info;
            }
        }
        return null;
    }

    /**
     * Returns a list of all the widgets that can handle configuration with a particular mimeType.
     */
    List<WidgetMimeTypeHandlerData> resolveWidgetsForMimeType(Context context, String mimeType) {
        final PackageManager packageManager = context.getPackageManager();
        final List<WidgetMimeTypeHandlerData> supportedConfigurationActivities =
            new ArrayList<WidgetMimeTypeHandlerData>();

        final Intent supportsIntent =
            new Intent(InstallWidgetReceiver.ACTION_SUPPORTS_CLIPDATA_MIMETYPE);
        supportsIntent.setType(mimeType);

        // Create a set of widget configuration components that we can test against
        final List<AppWidgetProviderInfo> widgets =
            AppWidgetManager.getInstance(context).getInstalledProviders();
        final HashMap<ComponentName, AppWidgetProviderInfo> configurationComponentToWidget =
            new HashMap<ComponentName, AppWidgetProviderInfo>();
        for (AppWidgetProviderInfo info : widgets) {
            configurationComponentToWidget.put(info.configure, info);
        }

        // Run through each of the intents that can handle this type of clip data, and cross
        // reference them with the components that are actual configuration components
        final List<ResolveInfo> activities = packageManager.queryIntentActivities(supportsIntent,
                PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo info : activities) {
            final ActivityInfo activityInfo = info.activityInfo;
            final ComponentName infoComponent = new ComponentName(activityInfo.packageName,
                    activityInfo.name);
            if (configurationComponentToWidget.containsKey(infoComponent)) {
                supportedConfigurationActivities.add(
                        new InstallWidgetReceiver.WidgetMimeTypeHandlerData(info,
                                configurationComponentToWidget.get(infoComponent)));
            }
        }
        return supportedConfigurationActivities;
    }

    public ShortcutInfo infoFromShortcutIntent(Context context, Intent data, Bitmap fallbackIcon) {
        Intent intent = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);
        String name = data.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);
        Parcelable bitmap = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON);

        if (intent == null) {
            // If the intent is null, we can't construct a valid ShortcutInfo, so we return null
            Log.e(TAG, "Can't construct ShorcutInfo with null intent");
            return null;
        }

        Bitmap icon = null;
        boolean customIcon = false;
        ShortcutIconResource iconResource = null;

        if (bitmap != null && bitmap instanceof Bitmap) {
        	/** AUT: henryyu1986@163.com DATE: 2011-12-31 S */
//            icon = Utilities.createIconBitmap(new FastBitmapDrawable((Bitmap)bitmap), context);
            icon = (Bitmap) bitmap;
            /** AUT: henryyu1986@163.com DATE: 2011-12-31 E */
            customIcon = true;
        } else {
            Parcelable extra = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);
            if (extra != null && extra instanceof ShortcutIconResource) {
                try {
                    iconResource = (ShortcutIconResource) extra;
                    final PackageManager packageManager = context.getPackageManager();
                    Resources resources = packageManager.getResourcesForApplication(
                            iconResource.packageName);
                    final int id = resources.getIdentifier(iconResource.resourceName, null, null);
                    /* RK_ID: ICONSTYLE. AUT: liuli1 . DATE: 2012-02-29 . START */
//                    icon = Utilities.createIconBitmap(
//                            mIconCache.getFullResIcon(resources, id), context, iconResource.packageName);
                    icon = mApp.mLauncherContext.getIconBitmap(resources, id, iconResource.packageName);
                    /* RK_ID: ICONSTYLE. AUT: liuli1 . DATE: 2012-02-29 . END */
                } catch (Exception e) {
                    Log.w(TAG, "Could not load shortcut icon: " + extra);
                }
            }
        }

        final ShortcutInfo info = new ShortcutInfo();

        if (icon == null) {
            if (fallbackIcon != null) {
                icon = fallbackIcon;
            } else {
                icon = getFallbackIcon();
                info.usingFallbackIcon = true;
            }
        }
        info.setIcon(icon);

        info.title = name == null ? "" : name;
        info.intent = intent;
        info.customIcon = customIcon;
        info.iconResource = iconResource;
        if (SettingsValue.isLauncherNotification(intent)
                && intent.getComponent() != null)
        {
            String str = Settings.System.getString(context.getContentResolver(),"NEWMSG_" + intent.getComponent().flattenToString());
            R5.echo("getShorcutInfo pkgName = " + intent.getComponent().flattenToString() + "   num = " + str);
            if (str != null && !str.isEmpty())
            {
                info.updateInfo(str);
            }                                          
        } 

        return info;
    }

    boolean queueIconToBeChecked(HashMap<Object, byte[]> cache, ShortcutInfo info, Cursor c,
            int iconIndex) {
        // If apps can't be on SD, don't even bother.
        if (!mAppsCanBeOnExternalStorage) {
            return false;
        }
        // If this icon doesn't have a custom icon, check to see
        // what's stored in the DB, and if it doesn't match what
        // we're going to show, store what we are going to show back
        // into the DB.  We do this so when we're loading, if the
        // package manager can't find an icon (for example because
        // the app is on SD) then we can use that instead.
        if (!info.customIcon && !info.usingFallbackIcon) {
            cache.put(info, c.getBlob(iconIndex));
            return true;
        }
        return false;
    }
    void updateSavedIcon(Context context, ShortcutInfo info, byte[] data) {
        boolean needSave = false;
        try {
            if (data != null) {
                Bitmap saved = BitmapFactory.decodeByteArray(data, 0, data.length);
                /* AUT: liuli liuli0120@use.com.cn DATE: 2012-01-29 START */
                Bitmap loaded = info.getIcon(mIconCache, false);
                /* AUT: liuli liuli0120@use.com.cn DATE: 2012-01-29 END */
                needSave = !saved.sameAs(loaded);
            } else {
                needSave = true;
            }
        } catch (Exception e) {
            needSave = true;
        }
        if (needSave) {
            Log.d(TAG, "going to save icon bitmap for info=" + info);
            // This is slower than is ideal, but this only happens once
            // or when the app is updated with a new icon.
            updateItemInDatabase(context, info);
        }
    }

    /**
     * Return an existing FolderInfo object if we have encountered this ID previously,
     * or make a new one.
     */
    private static FolderInfo findOrMakeFolder(HashMap<Long, FolderInfo> folders, long id) {
        // See if a placeholder was created for us already
        FolderInfo folderInfo = folders.get(id);
        if (folderInfo == null) {
            // No placeholder -- create a new instance
            folderInfo = new FolderInfo();
            folders.put(id, folderInfo);
        }
        return folderInfo;
    }

    private static final Collator sCollator = Collator.getInstance();
    public static final Comparator<ApplicationInfo> APP_NAME_COMPARATOR
            = new Comparator<ApplicationInfo>() {
        public final int compare(ApplicationInfo a, ApplicationInfo b) {
            /*** AUT: zhaoxy . DATE: 2012-06-08 . START***/
            int result;
            int size = Math.min(a.sortKey.length, b.sortKey.length);
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    result = sCollator.compare(a.sortKey[i], b.sortKey[i]);
                    if (result != 0) {
                        return result;
                    }
                }
            }
            /*** AUT: zhaoxy . DATE: 2012-06-08 . END***/
            result = sCollator.compare(a.title.toString(), b.title.toString());
            if (result == 0) {
                result = a.componentName.compareTo(b.componentName);
            }
            return result;
        }
    };
    public static final Comparator<ApplicationInfo> APP_INSTALL_TIME_COMPARATOR
            = new Comparator<ApplicationInfo>() {
        public final int compare(ApplicationInfo a, ApplicationInfo b) {
            if (a.firstInstallTime < b.firstInstallTime) return 1;
            if (a.firstInstallTime > b.firstInstallTime) return -1;
            return 0;
        }
    };
    /*** AUT: zhaoxy . DATE: 2012-04-16 . START***/
    public static final Comparator<ApplicationInfo> APP_FIRST_INSTALL_COMPARATOR_ASC
            = new Comparator<ApplicationInfo>() {
        public final int compare(ApplicationInfo a, ApplicationInfo b) {
            //int result = (int) (a.firstInstallTime - b.firstInstallTime);
        	int result = a.firstInstallTime > b.firstInstallTime ? 1 : a.firstInstallTime < b.firstInstallTime ? -1 : 0;
            if (result == 0) {
                result = sCollator.compare(a.title.toString(), b.title.toString());
                if (result == 0) {
                    result = a.componentName.compareTo(b.componentName);
                }
            }
            return result;
        }
    };
    public static final Comparator<ApplicationInfo> APP_FIRST_INSTALL_COMPARATOR_DES
            = new Comparator<ApplicationInfo>() {
        public final int compare(ApplicationInfo a, ApplicationInfo b) {
            //int result = (int) (b.firstInstallTime - a.firstInstallTime);
        	int result = a.firstInstallTime > b.firstInstallTime ? -1 : a.firstInstallTime < b.firstInstallTime ? 1 : 0;
            
            if (result == 0) {
                result = sCollator.compare(a.title.toString(), b.title.toString());
                if (result == 0) {
                    result = a.componentName.compareTo(b.componentName);
                }
            }
            return result;
        }
    };
    /*** AUT: zhaoxy . DATE: 2012-04-16 . END***/
    public static final Comparator<AppWidgetProviderInfo> WIDGET_NAME_COMPARATOR
            = new Comparator<AppWidgetProviderInfo>() {
        public final int compare(AppWidgetProviderInfo a, AppWidgetProviderInfo b) {
            return sCollator.compare(a.label, b.label);
        }
    };
    
    public static class ShortcutNameComparator implements Comparator<ResolveInfo> {
        private PackageManager mPackageManager;
        private HashMap<Object, CharSequence> mLabelCache;
        ShortcutNameComparator(PackageManager pm) {
            mPackageManager = pm;
            mLabelCache = new HashMap<Object, CharSequence>();
        }
        ShortcutNameComparator(PackageManager pm, HashMap<Object, CharSequence> labelCache) {
            mPackageManager = pm;
            mLabelCache = labelCache;
        }
        public final int compare(ResolveInfo a, ResolveInfo b) {
            CharSequence labelA, labelB;
            ComponentName keyA = Utilities.getComponentNameFromResolveInfo(a);
            ComponentName keyB = Utilities.getComponentNameFromResolveInfo(b);
            if (mLabelCache.containsKey(keyA)) {
                labelA = mLabelCache.get(keyA);
            } else {
                labelA = a.loadLabel(mPackageManager).toString();

                mLabelCache.put(keyA, labelA);
            }
            if (mLabelCache.containsKey(keyB)) {
                labelB = mLabelCache.get(keyB);
            } else {
                labelB = b.loadLabel(mPackageManager).toString();

                mLabelCache.put(keyB, labelB);
            }
            return sCollator.compare(labelA.toString(), labelB.toString());
        }
    };
    public static class WidgetAndShortcutNameComparator implements Comparator<Object> {
        private PackageManager mPackageManager;
        private HashMap<Object, String> mLabelCache;
        WidgetAndShortcutNameComparator(PackageManager pm) {
            mPackageManager = pm;
            mLabelCache = new HashMap<Object, String>();
        }
        public final int compare(Object a, Object b) {
            String labelA, labelB;
            if (mLabelCache.containsKey(a)) {
                labelA = mLabelCache.get(a);
            } else {
                labelA = (a instanceof AppWidgetProviderInfo) ?
                    ((AppWidgetProviderInfo) a).label :
                    ((ResolveInfo) a).loadLabel(mPackageManager).toString();
                mLabelCache.put(a, labelA);
            }
            if (mLabelCache.containsKey(b)) {
                labelB = mLabelCache.get(b);
            } else {
                labelB = (b instanceof AppWidgetProviderInfo) ?
                    ((AppWidgetProviderInfo) b).label :
                    ((ResolveInfo) b).loadLabel(mPackageManager).toString();
                mLabelCache.put(b, labelB);
            }
            return sCollator.compare(labelA.toString(), labelB.toString());
        }
    };

    public void dumpState() {
        Log.d(TAG, "mCallbacks=" + mCallbacks);
        ApplicationInfo.dumpApplicationInfoList(TAG, "mAllAppsList.data", mAllAppsList.data);
        ApplicationInfo.dumpApplicationInfoList(TAG, "mAllAppsList.added", mAllAppsList.added);
        ApplicationInfo.dumpApplicationInfoList(TAG, "mAllAppsList.removed", mAllAppsList.removed);
        ApplicationInfo.dumpApplicationInfoList(TAG, "mAllAppsList.modified", mAllAppsList.modified);
//        ApplicationInfo.dumpApplicationInfoList(TAG, "mAllAppsList.modified", mAllAppsList.avialiable_added);
        if (mLoaderTask != null) {
            mLoaderTask.dumpState();
        } else {
            Log.d(TAG, "mLoaderTask=null");
        }
    }
    
    /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-16 START */
    public static ArrayList<WeatherWidgetPosInfo> getLeosWidgetIdByScreen(Context context, int screenIndex) {
    	ArrayList<WeatherWidgetPosInfo> widgetViewNameList = new ArrayList<WeatherWidgetPosInfo>();
    	if( sLeosWidgets != null )
    		for( int i = 0; i< sLeosWidgets.size(); i ++ ){

    			LenovoWidgetViewInfo info = sLeosWidgets.get(i);
    			if(info.screen == screenIndex){
    				widgetViewNameList.add( new WeatherWidgetPosInfo(sLeosWidgets.get(i).cellY,sLeosWidgets.get(i).className));
    				Log.d("liuyg1","widgetViewName "+i+"="+sLeosWidgets.get(i).className);
    			}
    		}

    	return widgetViewNameList;
    } 
    /*RK_ID:RK_LEOSWIDGET AUT:kangwei DATE: 2012-10-17 START */
    public static ArrayList<LenovoWidgetViewInfo> getLeosWidgetIds()
    {
    	return sLeosWidgets;
    }
    /*RK_ID:RK_LEOSWIDGET AUT:kangwei DATE: 2012-10-17 E */
    /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-16  END */
    /*FIX BUG:  MOKEY TEST MEMORY LEAK. AUT: chengliang . DATE: 2012-06-29 . S*/
    public static ArrayList<Integer> getAppWidgetIdByScreen(Context context, int screenIndex) {
        ArrayList<Integer> widgetIDList = new ArrayList<Integer>();
        if( sAppWidgets != null )
	    	for( int i = 0; i< sAppWidgets.size(); i ++ ){
	    		
	    		LauncherAppWidgetInfo info = sAppWidgets.get(i);
	    		if(info != null && info.screen == screenIndex)
	    			widgetIDList.add( sAppWidgets.get(i).appWidgetId);
	    	}
        return widgetIDList;
    }   
    /*FIX BUG:  MOKEY TEST MEMORY LEAK. AUT: chengliang . DATE: 2012-06-29 . E*/
    
    
    /*RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2012-03-15 . S*/
    public AllAppsList getAllAppsList() {
    	return mAllAppsList;
    }
    /*RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2012-03-15 . E*/

    /*** AUT: zhaoxy . DATE: 2012-04-16 . START***/
//    public UsageStatsMonitor getUsageStatsMonitor() {
//        return usageStatsMonitor;
//    }
    
    public RegularApplist getRegularApplist() {
        if (mRegularApplist == null) {
            mRegularApplist = new RegularApplist(mApp);
        }
        return mRegularApplist;
    }
    /*** AUT: zhaoxy . DATE: 2012-04-16 . END***/
    /*RK_ID: RK_FIX_BUG . AUT: zhanggx1 . DATE: 2012-06-19 . PUR: stop appwidgetHost listening . S*/
    public void restartLauncher() {
    	if (mCallbacks != null) {
    		android.util.Log.i("dooba", "===============mCallbacks != null ");
            Callbacks callbacks = mCallbacks.get();
            if (callbacks != null) {
            	android.util.Log.i("dooba", "===============call != null ");
            	callbacks.restartLauncher();
            }
    	}
    }
    /*RK_ID: RK_FIX_BUG . AUT: zhanggx1 . DATE: 2012-06-19 . PUR: stop appwidgetHost listening . E*/
    
	/*RK_ID: MEM OPT . AUT: chengliang . DATE: 2011-07-23 . S*/
    public Callbacks getCallBack(){
    	return mCallbacks.get();
    }
	/*RK_ID: MEM OPT . AUT: chengliang . DATE: 2011-07-23 . E*/
    
    /*RK_ID:RK_SD_APPS zhangdxa 2013-4-15. S***/
    public static String getCommandAppDownloadUri(String packageName,String title){
		String uriStr = LauncherService.LENOVO_HOME_URL;
		if (packageName != null && !packageName.equalsIgnoreCase("")) {
			uriStr = LauncherService.LENOVO_APPSTORE_URL + packageName;
		} else if (title != null && !title.equalsIgnoreCase("")) {
			uriStr = LauncherService.LENOVO_APPSTORE_APPNAME_URL+ title;
		}			
		return uriStr;
	}
    
    private void processLeosWidgetInOtherPackage(String action, String packageName){
    	Log.i("zdx1","XLauncher.processLeosWidgetInOtherPackage, action:"+ action +", packageName:"+ packageName);
    	if( action == null || packageName == null){
        	return;
        }
    	if( !action.equals(Intent.ACTION_PACKAGE_ADDED) &&
    		!action.equals(Intent.ACTION_PACKAGE_REMOVED)){
          	return;
        }
    	if( !packageName.equals(WEATHER_WIDGET_LOTUS) &&
      		!packageName.equals(WEATHER_WIDGET_TEA) &&
      		!packageName.equals(WEATHER_WIDGET_AURORA)){
    		return;
    	}
    	
    	final Callbacks callbacks = mCallbacks != null ? mCallbacks.get() : null;
        if (callbacks == null) {
            Log.w(TAG, "Nobody to tell about the new app.  Launcher is probably loading.");
            return;
        }
    	if( action.equals(Intent.ACTION_PACKAGE_ADDED) ) {
    		Log.i("zdx1","XLauncher.processLeosWidgetInOtherPackage, add");
    		final String packName = packageName;
    	    mHandler.post(new Runnable() {
                @Override
                public void run() {
                	Log.i("zdx1","XLauncher.processLeosWidgetInOtherPackage, add 111");
        	        ArrayList<ItemInfo> list = new ArrayList<ItemInfo>();
                    ArrayList<ItemInfo> appWidgets = (ArrayList<ItemInfo>)sAppWidgets.clone();
      		        for(ItemInfo info : appWidgets) {          			
      			       if(info instanceof LauncherAppWidgetInfo && 
      				       info.itemType == Favorites.ITEM_TYPE_COMMEND_APPWIDGET ){
      				       LauncherAppWidgetInfo widgetInfo = (LauncherAppWidgetInfo) info;
      				       String widgetPackage = null;
      				       if( widgetInfo.intent != null && widgetInfo.intent.getComponent() != null){
      				    	   widgetPackage = widgetInfo.intent.getComponent().getPackageName(); 
      				       }
      				       if( widgetPackage != null && widgetPackage.equals(packName)){
      				    	 list.add(info);
      				       }      				       
      			       }
      		        }
      		        appWidgets.clear();
      		        appWidgets = null;
      		        Callbacks cb = mCallbacks != null ? mCallbacks.get() : null;
      		        if (callbacks == cb && cb != null ) {
                       if( list.size() > 0 ){
      			           callbacks.removeWidgets(list);
                       }
                       callbacks.addLeosWidgets(packName);   
      		        }
                }
            });
        }else{
        	 ArrayList<ApplicationInfo>  removedLeos = new ArrayList<ApplicationInfo>();
        	 ApplicationInfo appInfo = new ApplicationInfo();
     		 appInfo.componentName = new ComponentName(packageName, packageName);
     		 appInfo.title = packageName;
			 Log.i("zdx1","XLauncherModel***remove leos widget*****"+ packageName);
     		 removedLeos.add(appInfo);
        	 if( removedLeos.size() > 0 ){
        		final ArrayList<ApplicationInfo>  list = removedLeos;
             	mHandler.post(new Runnable() {
                     public void run() {
                         Callbacks cb = mCallbacks != null ? mCallbacks.get() : null;
                         if (callbacks == cb && cb != null) {
                             callbacks.removeLeosWidgetInOtherPackage(list);
                         }
                     }
                 });
            }
        } 
    }
    /*RK_ID:RK_SD_APPS zhangdxa 2013-4-15. E***/
 /*RK_ID:RK_SINGLE_LAYER liuyg1 2013-6-13. S*/
    public static int getHotseatChildCount(Context context){
        final ContentResolver cr = context.getContentResolver();
        int container = LauncherSettings.Favorites.CONTAINER_HOTSEAT;
        final Cursor c = cr.query(LauncherSettings.Favorites.CONTENT_URI, null,
                "container=?",
                new String[] { String.valueOf(container)}, null);
        int result = 0;
        try {
            while (c.moveToNext()) {
                result++;
            }
        } finally {
            c.close();
        }
        return result;
    }
 /*RK_ID:RK_SINGLE_LAYER liuyg1 2013-6-13. E*/
    
    /* RK_ID:RK_BUG zhangdxa . DATE:2013-7-16. S*/
  	public XLauncher getLauncher(){
          try {
  			return getCallBack().getLauncherInstance();
  		} catch (Exception e) {
  			return null;
  		}
  	}
  	
  	public void resetLoad() {
  	    synchronized (mLock) {
  	        stopLoaderLocked();
  	        mAllAppsLoaded = false;
  	        mWorkspaceLoaded = false;
  	    }
  	}
  	/* RK_ID:RK_BUG zhangdxa . DATE:2013-7-16. E*/
//bugfix 17646
	public static ArrayList<ItemInfo> getHotsetItems() {
		// TODO Auto-generated method stub
		return sHotseatItems;
	}
  	/*add by zhanggx1 on 2013-10-17 for theme appling.s*/
  	public void changeThemeIcon(final Context context, final String mFlag) {
  		if (mAllAppsList == null
  				|| mHandler == null) {
  			return;
  		}
  		final Callbacks callbacks = mCallbacks != null ? mCallbacks.get() : null;
        if (callbacks == null) {
            return;
        }

  		final Runnable r = new Runnable() {
  	        public void run() {
  	        	final ArrayList<ApplicationInfo> apps = mAllAppsList.updateAllPackagesIcon(context);
  	        	if (apps != null) {
  	        		mHandler.post(new Runnable() {
  	                    public void run() {
  	                        Callbacks cb = mCallbacks != null ? mCallbacks.get() : null;
  	                        if (callbacks == cb && cb != null) {
  	                        	cb.endThemeAppling(apps, mFlag);
  	                        }
  	                    }
  	                });
  	        	}
  	        }
  		};
  		sReceiverWorker.post(r);
  	}
  	
  	public void refreshIconStyleAndSize(final Context context,
  			final boolean draglayer,
  			final boolean allapp, final boolean bitmapUpdate,
  			final int currentIconStyle,final int currentIconSize) {
  		if (mHandler == null) {
  			return;
  		}
  		final Callbacks callbacks = mCallbacks != null ? mCallbacks.get() : null;
        if (callbacks == null) {
            return;
        }
        
  		final Runnable r = new Runnable() {
  	        public void run() {
  	        	if (mAllAppsList != null
  	        			&& (currentIconStyle != Integer.MIN_VALUE || currentIconSize!=Integer.MIN_VALUE)) {
  	        		mAllAppsList.updateAllPackagesIcon(context);
  	        	}
  	        	
  	        	mHandler.post(new Runnable() {
  	        		public void run() {
  	        			Callbacks cb = mCallbacks != null ? mCallbacks.get() : null;
                        if (callbacks == cb && cb != null) {
                        	cb.changeIconStyle(draglayer, allapp, bitmapUpdate, currentIconStyle, currentIconSize);
                        }
  	        		}
  	        	});
  	        }
  		};
  		sReceiverWorker.post(r);
  	}
  	/*add by zhanggx1 on 2013-10-17 for theme appling.e*/
  	
  	//add by zhanggx1 for duplicate icons.s
    private HashMap<String, ShortcutInfo> mShortcuts = new HashMap<String, ShortcutInfo>();
    
    public void removeFromShortcutInfos(ComponentName cn) {
    	if (cn == null) {
    		return;
    	}
    	String key = cn.flattenToShortString();
    	android.util.Log.i("dooba", "----------->removeFromShortcutInfos---------" + key);
    	mShortcuts.remove(key);
    }
    		
    
    /**
     * 判断快捷方式是否存在，以确定唯一
     * @param info
     * @return
     */
    public boolean checkShortcutInfoExist(ShortcutInfo info) {
    	if (info == null
    			|| info.intent == null
    			|| info.intent.getComponent() == null) {
    		return false;
    	}
    	String key = info.intent.getComponent().flattenToShortString();    	
    	boolean ret = mShortcuts.containsKey(key);
    	if (ret) {
    		android.util.Log.i("dooba", "----------->checkShortcutInfoExist---------" + key);
    	}
    	return ret;
    }
    
    /**
     * 记录快捷方式
     * @param info
     */
    public void saveShortcutInfo(ShortcutInfo info) {
    	if (info == null
    			|| info.intent == null
    			|| info.intent.getComponent() == null) {
    		return;
    	}
    	mShortcuts.put(info.intent.getComponent().flattenToShortString(), info);
    }
    
    /**
     * 清空记录的快捷方式
     */
    public void clearShortcutInfo() {
    	mShortcuts.clear();
    }
    
    public HashMap<String, ShortcutInfo> getShortcuts() {
    	return mShortcuts;
    }
    
    /**
     * 取得保存的应用
     * @param componentName
     * @return
     */
    public ShortcutInfo getShortcutSaved(String componentName) {
    	if (componentName == null || !mShortcuts.containsKey(componentName)) {
    		return null;
    	}
    	return mShortcuts.get(componentName);
    }
    
    private boolean checkMccPackage(final String packageName) {
    	if (packageName == null) {
    		return true;
    	}
    	int size = Constants.STK_PKG_NAMES.length;
		for (int i = 0; i < size; i++) {
			if (Constants.STK_PKG_NAMES[i].equalsIgnoreCase(packageName)) {
				return false;
			}
		}
		return true;
    }
    public void checkMccPackage(final Context context, String packageName, boolean force) {
    	if (packageName != null && checkMccPackage(packageName)) {
    		return;
    	}
    	final Callbacks callbacks = mCallbacks != null ? mCallbacks.get() : null;
        if (callbacks == null) {
            return;
        }
        
    	final ArrayList<ShortcutInfo> infoList = new ArrayList<ShortcutInfo>();
    	final PackageManager pm = context.getPackageManager();
    	if (mLoadMcc != mPreviousConfigMcc || force) {    		
    		Iterator<ItemInfo> values = sItemsIdMap.values().iterator();
        	ItemInfo info = null;
        	ShortcutInfo shortcut = null;
            while (values.hasNext()) {
            	info = values.next();
            	if (info.itemType != LauncherSettings.Favorites.ITEM_TYPE_APPLICATION                	
            			|| !(info instanceof ShortcutInfo)) {
            		continue;
            	}
            	shortcut = (ShortcutInfo)info;
                if (shortcut.intent == null || shortcut.intent.getComponent() == null) {
                	continue;
                }
                if (checkMccPackage(shortcut.intent.getComponent().getPackageName())) {
                	continue;
                }
                
                if (checkPackageExist(pm, shortcut.intent.getComponent())) {
                	continue;
                }
                
                android.util.Log.i("dooba", "------toRemove--------" + shortcut.intent.getComponent());
                infoList.add(shortcut);
            }
            mHandler.post(new Runnable() {
    			@Override
    			public void run() {
    				callbacks.removeCheckedApp(infoList);
    			}
            	
            });
    	}
    	mLoadMcc = mPreviousConfigMcc;
    }
    
    private boolean checkPackageExist(final PackageManager pm, final ComponentName componentName) {
    	if (componentName == null) {
    		return true;
    	}
    	Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        String packageName = componentName.getPackageName();
        String classname = componentName.getClassName();
        mainIntent.setPackage(packageName);
        
        List<ResolveInfo> apps = pm.queryIntentActivities(mainIntent, PackageManager.GET_RESOLVED_FILTER);
        if (apps != null && apps.size() > 0) {
            int size = apps.size();
            for (int index = 0; index < size; index++) {
                 ResolveInfo temp = apps.get(index);
                 if (temp.activityInfo.name.equals(classname)) {
                	 return true;
                 }
            }
        }
        return false;
    }
    //add by zhanggx1 for duplicate icons.e
}
