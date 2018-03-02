package com.lenovo.launcher2.commoninterface;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;


import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UsageStatsMonitor {

    private class UsageStatsDBHelper extends SQLiteOpenHelper {

        private Context context;
        private static final String DATABASE_NAME = "usagestats.db";
        private static final int DATABASE_VERSION = 1;

        private static final String TABLE_NAME = "usagestats";
        private static final String TAG_INDEX = "_id";
        private static final String TAG_CMPNAME = "cmpname";
        private static final String TAG_LAUNCH_COUNT = "lcount";
        private static final String TAG_LAST_RESUME_TIME = "lastime";

        public UsageStatsDBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            db.execSQL(new StringBuffer("CREATE TABLE ")
            .append(TABLE_NAME)
            .append(" (")
            .append(TAG_INDEX).append(" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL, ")
            .append(TAG_CMPNAME).append(" TEXT NOT NULL, ")
            .append(TAG_LAUNCH_COUNT).append(" INTEGER DEFAULT 0, ")
            .append(TAG_LAST_RESUME_TIME).append(" INTEGER DEFAULT 0")
            .append(");")
            .toString());
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            int version = oldVersion;
            if (version != DATABASE_VERSION) {
                onCreate(db);
            }
        }

    }
    private class UsageStatsValue {
        /**
         * 运行的次数。
         */
        public int launchCount = 0;
        /**
         * 最后一次Resume的时间。
         */
        public long lastResumeTime = 0;

        public UsageStatsValue(int launchCount, long lastResumeTimes) {
            if (launchCount >= 0) {
                this.launchCount = launchCount;
            }
            if (lastResumeTimes >= 0) {
                this.lastResumeTime = lastResumeTimes;
            }
        }
    }
    private static final boolean DEBUG = true;

    private static final String TAG = "UsageStatsMonitor";

    /**
     * 最大闲置天数.超过该天数没有使用记录的应用将在数据库中删除记录.用户卸载应用后使用记录仍保留,载该天数之内如果重新安装,则继续使用之前记录追加.
     */
    private static final long MAX_DAY_UNUSED = 90;
    /*** RK_ID: TASK_TAB.  AUT: zhaoxy . DATE: 2012-10-25 . START***/
    public static final String KEY_LAST_BOOT_TIME = "last_boot_time";
    /*** RK_ID: TASK_TAB.  AUT: zhaoxy . DATE: 2012-10-25 . END***/
    private static final Collator sCollator = Collator.getInstance();
    private static boolean isAvailable = false;
    private volatile boolean updatting = false;

    private final Object mChangeLock = new Object();
    private UsageStatsDBHelper mOpenHelper;
    /**
     * 应用使用频率比较器。使用此比较器前请先调用{@link #updateCache()}以保证刷新最新的使用情况。
     */
    public static final Comparator<ApplicationInfo> APP_LAUNCH_COUNT_COMPARATOR = new Comparator<ApplicationInfo>() {
        UsageStatsValue ausv = null;
        UsageStatsValue busv = null;
        @Override
        public int compare(ApplicationInfo a, ApplicationInfo b) {
            int result = 0;
            ausv = cache.get(a.componentName.flattenToShortString());
            busv = cache.get(b.componentName.flattenToShortString());
            if (ausv == null) {
                if (busv != null) {
                    result = 1;
                }
            } else if (busv == null) {
                result = -1;
            } else {
                result = busv.launchCount - ausv.launchCount;
            }
            if (result == 0) {
                result = sCollator.compare(a.title.toString(), b.title.toString());
                if (result == 0) {
                    result = a.componentName.compareTo(b.componentName);
                }
            }
            return result;
        }
    };
    /**
     * 应用最后运行比较器。使用此比较器前请先调用{@link #updateCache()}以保证刷新最新的使用情况。
     */
    public static final Comparator<ApplicationInfo> APP_LAST_RESUME_TIME_COMPARATOR = new Comparator<ApplicationInfo>() {
        UsageStatsValue ausv = null;
        UsageStatsValue busv = null;
        @Override
        public int compare(ApplicationInfo a, ApplicationInfo b) {
            int result = 0;
            ausv = cache.get(a.componentName.flattenToShortString());
            busv = cache.get(b.componentName.flattenToShortString());
            if (ausv == null) {
                if (busv != null) {
                    result = 1;
                }
            } else if (busv == null) {
                result = -1;
            } else {
                result = busv.lastResumeTime > ausv.lastResumeTime ? 1 : busv.lastResumeTime < ausv.lastResumeTime ? -1 : 0;
            }
            if (result == 0) {
                result = sCollator.compare(a.title.toString(), b.title.toString());
                if (result == 0) {
                    result = a.componentName.compareTo(b.componentName);
                }
            }
            return result;
        }
    };

    private static ConcurrentHashMap<String, UsageStatsMonitor.UsageStatsValue> cache = new ConcurrentHashMap<String, UsageStatsMonitor.UsageStatsValue>();

    public static boolean isAvailable() {
        return isAvailable;
    }

    private long lastUpdateTime = 0;

    public UsageStatsMonitor(Context mContext) {
        // this.mContext = mContext;
        mOpenHelper = new UsageStatsDBHelper(mContext);
        isAvailable = true;
        cleanExpire();
    }

    /**
     * 
     * @param intent 启动应用的intent
     */
    public void add(Intent intent) {
        if (intent != null && intent.getComponent() != null) {
            String cmpName = intent.getComponent().flattenToShortString();
            addOneLaunch(cmpName);
        }
    }

    /**
     * @param cmpName 启动应用的ComponentName
     */
    public void add(String cmpName) {
        ComponentName cmp = ComponentName.unflattenFromString(cmpName);
        addOneLaunch(cmp.flattenToShortString());
    }

    private void addOneLaunch(String cmpName) {
        if (cmpName != null && !cmpName.equals("")) {
            if (DEBUG) Log.d(TAG, "cmpName = " + cmpName);
            SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            Cursor c = db.query(UsageStatsDBHelper.TABLE_NAME,
                    new String[] { UsageStatsDBHelper.TAG_INDEX, UsageStatsDBHelper.TAG_LAUNCH_COUNT },
                    UsageStatsDBHelper.TAG_CMPNAME + " = '" + cmpName + "'", null, null, null, null);
            if (c != null) {
                final int idIndex = c.getColumnIndex(UsageStatsDBHelper.TAG_INDEX);
                final int launchCountIndex = c.getColumnIndex(UsageStatsDBHelper.TAG_LAUNCH_COUNT);
                synchronized (mChangeLock) {
                    db.beginTransaction();
                    try {
                        int launchCount = 0;
                        if (c.moveToFirst()) {
                            db.delete(UsageStatsDBHelper.TABLE_NAME, UsageStatsDBHelper.TAG_INDEX + " = " + c.getLong(idIndex), null);
                            launchCount = c.getInt(launchCountIndex);
                        }
                        ContentValues values = new ContentValues();
                        values.put(UsageStatsDBHelper.TAG_CMPNAME, cmpName);
                        values.put(UsageStatsDBHelper.TAG_LAUNCH_COUNT, launchCount + 1);
                        values.put(UsageStatsDBHelper.TAG_LAST_RESUME_TIME, System.currentTimeMillis());
                        db.insert(UsageStatsDBHelper.TABLE_NAME, null, values);
                        if (DEBUG) Log.d(TAG, "launchCount = " + (launchCount + 1));
                        db.setTransactionSuccessful();
                    } catch (Exception e) {
                        if(DEBUG) Log.d(TAG, "add fail because of " + e.getMessage());
                    } finally {
                        c.close();
                        db.endTransaction();
                    }
                }
            }
            db.close();
        }
    }

    /**
     * 清理超过 MAX_DAY_UNUSED 天没使用的应用记录.
     */
    private void cleanExpire() {
        long expireTime = System.currentTimeMillis() - MAX_DAY_UNUSED * 86400000;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        StringBuffer sql = new StringBuffer("DELETE FROM ")
                .append(UsageStatsDBHelper.TABLE_NAME).append(" WHERE ")
                .append(UsageStatsDBHelper.TAG_LAST_RESUME_TIME).append(" < ")
                .append(expireTime);
        if (DEBUG) Log.d(TAG, "cleanExpire " + sql.toString());
        db.execSQL(sql.toString());
        db.close();
    }

    /**
     * 从数据库中加载最新的使用记录到高速的HashMap表中,为Comparator提供数据支持
     */
    public void updateCatch() {
        if (updatting) {
            return;
        }
        if (System.currentTimeMillis() - lastUpdateTime < 2000) {
            if (DEBUG) Log.d(TAG, "It's too frequent.");
            return;
        }
        updatting = true;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Cursor c = db.query(UsageStatsDBHelper.TABLE_NAME,
                new String[] { UsageStatsDBHelper.TAG_CMPNAME, UsageStatsDBHelper.TAG_LAUNCH_COUNT, UsageStatsDBHelper.TAG_LAST_RESUME_TIME },
                null, null, null, null, null);
        if (c != null) {
            if (c.moveToFirst()) {
                final int cmpIndex = c.getColumnIndex(UsageStatsDBHelper.TAG_CMPNAME);
                final int launchCountIndex = c.getColumnIndex(UsageStatsDBHelper.TAG_LAUNCH_COUNT);
                final int lastResumeIndex = c.getColumnIndex(UsageStatsDBHelper.TAG_LAST_RESUME_TIME);
                String cmpName = null;
                int launchCount = 0;
                long lastResume = 0;
                do {
                    cmpName = c.getString(cmpIndex);
                    if (cmpName != null && !cmpName.equals("")) {
                        launchCount = c.getInt(launchCountIndex);
                        lastResume = c.getLong(lastResumeIndex);
                        cache.put(cmpName, new UsageStatsValue(launchCount, lastResume));
                    }
                } while (c.moveToNext());
                if (DEBUG) Log.d(TAG, "update finished " + c.getCount());
            }
            c.close();
            lastUpdateTime = System.currentTimeMillis();
        }
        updatting = false;
    }

    /*** RK_ID: TASK_TAB.  AUT: zhaoxy . DATE: 2012-08-20 . START***/
    public static void getRecentTasks(ArrayList<ApplicationInfo> list, int max, long filterTime) {
        if (list != null && !list.isEmpty()) {
            if (max <= 0) {
                list.clear();
                return;
            }
            /*** RK_ID: TASK_TAB.  AUT: zhaoxy . DATE: 2012-10-25 . START***/
            filterTime = Math.max(filterTime, System.currentTimeMillis() - 604800000l);
            /*** RK_ID: TASK_TAB.  AUT: zhaoxy . DATE: 2012-10-25 . END***/
            for (int i = list.size() - 1; i >= 0; i--) {
                ApplicationInfo info = list.get(i);
                if (info.hidden)
                {
                    list.remove(i);
                    continue;
                }
                
                UsageStatsValue usv = cache.get(info.componentName.flattenToShortString());
                if (usv == null || usv.lastResumeTime < filterTime) {
                    list.remove(i);
                }
            }
            Collections.sort(list, APP_LAST_RESUME_TIME_COMPARATOR);
            if (list.size() > max) {
                for (int i = list.size() - 1; i >= max; i--) {
                    list.remove(i);
                }
            }
        }
    }
    /*** RK_ID: TASK_TAB.  AUT: zhaoxy . DATE: 2012-08-20 . END***/
}
