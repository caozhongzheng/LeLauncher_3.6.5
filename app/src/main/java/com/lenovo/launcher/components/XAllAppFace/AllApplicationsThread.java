package com.lenovo.launcher.components.XAllAppFace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.lenovo.launcher2.LauncherProvider;
import com.lenovo.launcher2.commoninterface.ApplicationInfo;
import com.lenovo.launcher2.commoninterface.LauncherSettings;
import com.lenovo.launcher2.customizer.Debug;

public final class AllApplicationsThread/* extends Thread*/ {
    // actions
    public final static long ACTION_SYNC_TODB = 1 << 2;
    public final static long ACTION_ADDPACKAGE_DB = 1 << 3;
    public final static long ACTION_DELPACKAGE_DB = 1 << 4;
    private static final String TAG = "AllApplicationsThread";
    private long mPendingActions = 0;
    private OnActionDoneListener mActionDoneListener;

    /*** RK_ID: Memory Leak. AUT: zhaoxy . DATE: 2012-05-22. START***/
    //private Context mContext;
    private LauncherApplication mLauncherApplication;
    /*** RK_ID: Memory Leak. AUT: zhaoxy . DATE: 2012-05-22. END***/
    private ArrayList<ApplicationInfo> mApps;
    /*** RK_ID: Memory Leak. AUT: zhaoxy . DATE: 2012-05-22. START***/
    //private ArrayList<ApplicationInfo> mAppsAdded;
    //private ArrayList<ApplicationInfo> mAppsRemoved;
    /*** RK_ID: Memory Leak. AUT: zhaoxy . DATE: 2012-05-22. END***/

    public interface OnActionDoneListener {
        public void onActionDone();
    }

    /*** RK_ID: APP_SORT. AUT: zhaoxy . DATE: 2012-05-09. START***/
    private static final int MSG_SYNC_TODB = 100;

    private static final HandlerThread sWorkerThread = new HandlerThread("application-db-keeper");
    static {
        sWorkerThread.start();
    }
    private final Handler sWorker = new Handler(sWorkerThread.getLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_SYNC_TODB:
            	/*
                if( !AppsCustomizePagedView.mJustEnterEditMode ){
                    break;
                }
                AppsCustomizePagedView.mJustEnterEditMode = false;
                */
                syncToDatabase();
                break;
            default:
            	break;
            }
        }
    };
    /*** RK_ID: APP_SORT. AUT: zhaoxy . DATE: 2012-05-09. END***/

    /*** RK_ID: Memory Leak. AUT: zhaoxy . DATE: 2012-05-22. START***/
    //public AllApplicationsThread(Context context) {
    public AllApplicationsThread(LauncherApplication mLauncherApplication) {
        this.mLauncherApplication = mLauncherApplication;
    }

    /*public void run() {
        while (true) {
            long takeActions;
            try {
                synchronized (this) {
                    if (mPendingActions == 0) {
                        wait();
                    }
                    takeActions = mPendingActions;
                    mPendingActions = 0;
                }
            } catch (InterruptedException e) {
                break;
            }

            long actionDone = 0;
            while (takeActions != 0) {
                if (hasAction(takeActions, ACTION_SYNC_TODB)) {
                	
                	/** ID: performance improvement. AUT: chengliang . DATE: 2012.04.23 S */
                	/*if( !AppsCustomizePagedView.mJustEnterEditMode ){
                		actionDone = ACTION_SYNC_TODB;
                		break;
                	}
                	
                    AppsCustomizePagedView.mJustEnterEditMode = false;
                    /** ID: performance improvement. AUT: chengliang . DATE: 2012.04.23 E */
                	
                    /*syncToDatabase();
                    actionDone = ACTION_SYNC_TODB;

                    // action done, next operation
                    if (mActionDoneListener != null) {
                        mActionDoneListener.onActionDone();
                        mActionDoneListener = null;
                    }

                } else if (hasAction(takeActions, ACTION_ADDPACKAGE_DB)) {
                    insertPackagesInDatabase();
                    actionDone = ACTION_ADDPACKAGE_DB;
                } else if (hasAction(takeActions, ACTION_DELPACKAGE_DB)) {
                    delPackagesInDatabase();
                    actionDone = ACTION_DELPACKAGE_DB;
                } else {
                    actionDone = takeActions;
                }
                takeActions &= ~actionDone;
            }
        } // end while (true)
    }

    private boolean hasAction(long flags, long action) {
        return (flags & action) == action;
    }*/
    /*** RK_ID: Memory Leak. AUT: zhaoxy . DATE: 2012-05-22. END***/

    private void syncToDatabase() {
        /*** AUT: zhaoxy . DATE: 2012-04-23. START***/
        if(Debug.MAIN_DEBUG_SWITCH) Log.d("syncToDatabase", "syncToDatabase start.");
//        long s = System.currentTimeMillis();
        /*** RK_ID: Memory Leak. AUT: zhaoxy . DATE: 2012-05-22. START***/
        //LauncherApplication app = (LauncherApplication) mContext.getApplicationContext();
        LauncherProvider cp = mLauncherApplication.getLauncherProvider();
        /*** RK_ID: Memory Leak. AUT: zhaoxy . DATE: 2012-05-22. END***/
//        int size = mApps.size();
        ArrayList<ContentValues> values = new ArrayList<ContentValues>();
//        ContentValues[] values = new ContentValues[size];
        String title = "";
        for (int i = 0; i < mApps.size(); i++) {
        	
            /** ID: performance improvement. AUT: chengliang . DATE: 2012.04.23 S */
            //if (AppsCustomizePagedView.mJustEnterEditMode) {
//                if (Debug.MAIN_DEBUG_SWITCH) Log.d("syncToDatabase", "syncToDatabase break.");
        		//return;
            //}
            /** ID: performance improvement. AUT: chengliang . DATE: 2012.04.23 E */
        	
            ApplicationInfo appInfo = mApps.get(i);
            //final ContentResolver cr = mContext.getContentResolver();

            ComponentName componentName = appInfo.componentName;

            /*// query it in database first
            final Uri uri = findUriByCompoment(componentName);

            if (uri != null) {
                // find this application in databases
                // update index
                final ContentValues values = new ContentValues();
                values.put(LauncherSettings.Applications.CELL_INDEX, i);
                cr.update(uri, values, null, null);

            } else {
                // this application is not in databases
                String title = appInfo.title != null ? appInfo.title.toString() : "";
                addToDatabase(mContext, i, componentName, title, appInfo.canDrag);
            }*/
            ContentValues value = new ContentValues();
            long id = cp.generateNewAppsId();
            value.put("_id", id);
            // this application is not in databases
            title = appInfo.title != null ? appInfo.title.toString() : "";
            value.put(LauncherSettings.Applications.LABEL, title);
            /*** AUT: zhaoxy . DATE: 2012-04-27. START***/
            //values[i].put(LauncherSettings.Applications.CLASS, componentName.flattenToString());
            value.put(LauncherSettings.Applications.CLASS, componentName.flattenToShortString());
            /*** AUT: zhaoxy . DATE: 2012-04-27. END***/
            value.put(LauncherSettings.Applications.CELL_INDEX, i);
            value.put(LauncherSettings.Applications.APP_CAN_DRAG, appInfo.canDrag ? 1 : 0);
            values.add(value);
        } // end for
        /*** AUT: zhaoxy . DATE: 2012-04-27. START***/
        ContentValues[] resList = new ContentValues[values.size()];
        values.toArray(resList);
        cp.bulkInsertApplication(resList);
        /*** AUT: zhaoxy . DATE: 2012-04-27. END***/

        /*** AUT: zhaoxy . DATE: 2012-04-23. END***/
        /*** AUT: zhaoxy . DATE: 2012-03-22 . START***/
//        RegularApplist mRegularApplist = RegularApplist.getInstance();
        if (!mLauncherApplication.getModel().getRegularApplist().isFirstLoadDone()) {
            mLauncherApplication.getModel().getRegularApplist().firstLoadDone();
        }
//        if(Debug.MAIN_DEBUG_SWITCH) Log.d("syncToDatabase", "syncToDatabase finished. take " + (System.currentTimeMillis() - s) + " ms\n+++++++++++++++++++++++++++++");
        /*** AUT: zhaoxy . DATE: 2012-03-22 . END***/
    }

    /*** RK_ID: Memory Leak. AUT: zhaoxy . DATE: 2012-05-22. START***/
    /*private Uri findUriByCompoment(ComponentName componentName) {
        final ContentResolver cr = mContext.getContentResolver();
        Uri uri = null;

        // query it in database first
        Cursor c = cr.query(LauncherSettings.Applications.CONTENT_URI, null,
                LauncherSettings.Applications.CLASS + "=?", new String[] { componentName.flattenToString() }, null);
        long id = 0;
        try {
            if (c.moveToFirst()) {
                // find this application in databases
                final int idIndex = c.getColumnIndexOrThrow("_id");
                id = c.getLong(idIndex);

                uri = Uri.parse("content://" + LauncherProvider.AUTHORITY + "/" + LauncherProvider.TABLE_APPLICATIONS
                        + "/" + id);
            }
        } finally {
            c.close();
        } // end try-finally

        return uri;
    }

    private void insertPackagesInDatabase() {
        if (mAppsAdded == null)
            return;

        // original applications count
        int last = -1;

        // query databases
        Cursor c = mContext.getContentResolver().query(LauncherSettings.Applications.CONTENT_URI, null, null, null,
                LauncherSettings.Applications.CELL_INDEX + " DESC");
        /*try {
            if (c.moveToFirst()) {
                final int cellIndex = c.getColumnIndexOrThrow(LauncherSettings.Applications.CELL_INDEX);
                last = c.getInt(cellIndex);
            }
        } finally {
            c.close();
        }

        if (last == -1) {
            // cannot retrieve last index from database, we get apps from AppsCustomizePagedView
            Launcher l = Launcher.getInstance();
            if (l == null) {
                Log.w(TAG, "we cannot get original application count, and Launcher is null");
                return;
            } else {
                last = l.getAppsCustomizeContent().getApps().size() - 1;
            }
        }

        int length = mAppsAdded.size();
        for (int i = 0; i < length; ++i) {
            ApplicationInfo info = mAppsAdded.get(i);
            String label = info.title != null ? info.title.toString() : "";
            addToDatabase(mContext, last + i + 1, info.componentName, label);
        } // end for
    }

    private void delPackagesInDatabase() {
        if (mAppsRemoved == null)
            return;

        ContentResolver cr = mContext.getContentResolver();
        // loop through all the apps and remove apps that have the same component
        int length = mAppsRemoved.size();
        for (int i = 0; i < length; ++i) {
            ApplicationInfo info = mAppsRemoved.get(i);
            // query it in database first
            final Uri uriToDelete = findUriByCompoment(info.componentName);

            // find and just delete it.
            if (uriToDelete != null) {
                cr.delete(uriToDelete, null, null);
            }
        } // end for
    }*/
    /*** RK_ID: Memory Leak. AUT: zhaoxy . DATE: 2012-05-22. END***/

    public synchronized void setAction(long action, ArrayList<ApplicationInfo> apps) {
        /*** RK_ID: APP_SORT. AUT: zhaoxy . DATE: 2012-05-09. START***/
        //setAction(action, apps, null);
        if (action == ACTION_SYNC_TODB) {
            if(Debug.MAIN_DEBUG_SWITCH) Log.d("syncToDatabase", "+++++++++++++++++++++++++++++\nsyncToDatabase setAction.");
            sWorker.removeMessages(MSG_SYNC_TODB);
            Message msg = sWorker.obtainMessage(MSG_SYNC_TODB);
            mApps = apps;
            sWorker.sendMessage(msg);
        }
        /*** RK_ID: APP_SORT. AUT: zhaoxy . DATE: 2012-05-09. END***/
    }

//    public synchronized void setAction(long action, ArrayList<ApplicationInfo> apps, OnActionDoneListener listener) {
//        mPendingActions |= action;
//        mApps = apps;
//        mActionDoneListener = listener;
//        notifyAll();
//    }

    /*** RK_ID: Memory Leak. AUT: zhaoxy . DATE: 2012-05-22. START***/
    /*protected synchronized void setAddAction(long action, ArrayList<ApplicationInfo> appsAdded) {
        if (action != ACTION_ADDPACKAGE_DB)
            return;
        mPendingActions |= action;
        mAppsAdded = appsAdded;
        notifyAll();
    }

    protected synchronized void setDelAction(long action, ArrayList<ApplicationInfo> appsRemoved) {
        if (action != ACTION_DELPACKAGE_DB)
            return;
        mPendingActions |= action;
        mAppsRemoved = appsRemoved;
        notifyAll();
    }*/
    /*** RK_ID: Memory Leak. AUT: zhaoxy . DATE: 2012-05-22. END***/

    /*
     * customize app order
     */
//    public ArrayList<ApplicationInfo> startSortAllApps(ArrayList<ApplicationInfo> apps) {
//        /*** MODIFYBY: zhaoxy . DATE: 2012-03-22 . START***/
//        //if (applicationsInDatabaseCount(mContext) == 0 || !RegularApplist.getInstance().isFirstLoadDone()) {
//        if (applicationsInDatabaseCount(mLauncherApplication) == 0 || !mLauncherApplication.getModel().getRegularApplist().isFirstLoadDone()) {
//        /*** MODIFYBY: zhaoxy . DATE: 2012-03-22 . END***/
//            return startSortByXml(apps);
//        } else {
//            return startSortByDb(apps);
//        }
//    }

    /*
     * query application table cursor count
     */
    /*** RK_ID: Memory Leak. AUT: zhaoxy . DATE: 2012-05-22. START***/
//    private int applicationsInDatabaseCount(Context context) {
//        final ContentResolver cr = context.getContentResolver();
//    /*** RK_ID: Memory Leak. AUT: zhaoxy . DATE: 2012-05-22. END***/
//        final Cursor c = cr.query(LauncherSettings.Applications.CONTENT_URI, null, null, null, null);
//        int result = 0;
//        try {
//            result = c.getCount();
//        } finally {
//            c.close();
//        }
//        return result;
//    }

//    protected ArrayList<ApplicationInfo> startSortByXml(ArrayList<ApplicationInfo> apps) {
//        // clone apps
//        HashMap<ComponentName, ApplicationInfo> map = new HashMap<ComponentName, ApplicationInfo>();
//        ArrayList<ApplicationInfo> original = new ArrayList<ApplicationInfo>();
//        for (int i = 0; i < apps.size(); i++) {
//            ApplicationInfo appInfo = apps.get(i);
//            original.add(appInfo);
//            map.put(appInfo.componentName, appInfo);
//        }
//
//        /*** AUT: zhaoxy . DATE: 2012-03-22 . START***/
//        RegularApplist mRegularApplist = mLauncherApplication.getModel().getRegularApplist();
//        final HashMap<Integer, RegularApplist.AppSequenceInfo> regularMaps = mRegularApplist.getSortRule();
//        ArrayList<ApplicationInfo> newList = new ArrayList<ApplicationInfo>();
//
//        //if (regularMaps != null && map != null) {
//        if (map != null) {
//            newList.clear();
//            if (regularMaps != null) {
//                int count = regularMaps.size();
//                for (int i = 0; i < count; i++) {
//                    RegularApplist.AppSequenceInfo tempSequenceInfo = regularMaps.get(i);
//                    if (tempSequenceInfo == null) continue;
//                    ComponentName componentName = tempSequenceInfo.getComponent();
//                    if (map.containsKey(componentName)) {
//                        final ApplicationInfo appInfo = map.remove(componentName);
//                        original.remove(appInfo);
//                        appInfo.canDrag = tempSequenceInfo.isCanDrag();
//                        newList.add(appInfo);
//                    }
//                }
//            }
//            /*** AUT: zhaoxy . DATE: 2012-03-22 . END***/
//            
//            // handle left ones
//            Collections.sort(original, LauncherModel.APP_NAME_COMPARATOR);
//            for (int k = 0; k < original.size(); k++) {
//                final ApplicationInfo appInfo = original.get(k);
//                newList.add(appInfo);
//            }
//
//            apps = newList;
//        } // end if (regularList != null && list != null)
//
//        // sync to databases.
//        AppsCustomizePagedView.mJustEnterEditMode = true;
//        setAction(ACTION_SYNC_TODB, apps);
//
//        /*** RK_ID: APP_SORT. AUT: zhaoxy . DATE: 2012-05-09. START***/
//        /*** RK_ID: Memory Leak. AUT: zhaoxy . DATE: 2012-05-22. START***/
//        //if (mContext != null) {
//            //android.content.SharedPreferences.Editor edit = android.preference.PreferenceManager.getDefaultSharedPreferences(mContext).edit();
//        if (mLauncherApplication != null) {
//            android.content.SharedPreferences.Editor edit = android.preference.PreferenceManager.getDefaultSharedPreferences(mLauncherApplication).edit();
//        /*** RK_ID: Memory Leak. AUT: zhaoxy . DATE: 2012-05-22. END***/
//            edit.putInt(com.lenovo.launcher2.settings.SettingsValue.KEY_APPLIST_LAST_SORTMODE, 0);
//            edit.putInt(com.lenovo.launcher2.settings.SettingsValue.KEY_APPLIST_CURRENT_SORTMODE, 0);
//            edit.apply();
//        }
//        /*** RK_ID: APP_SORT. AUT: zhaoxy . DATE: 2012-05-09. END***/
//
//        return apps;
//    }
//
//    private int getRegularNextIndex() {
//        return 1;
//    }

    /*** RK_ID: Memory Leak. AUT: zhaoxy . DATE: 2012-05-22. START***/
    /*private long addToDatabase(Context context, final int index, final ComponentName componentName, final String label) {
        final ContentValues values = new ContentValues();
        final ContentResolver cr = context.getContentResolver();

        LauncherApplication app = (LauncherApplication) context.getApplicationContext();
        long id = app.getLauncherProvider().generateNewAppsId();
        values.put("_id", id);

        values.put(LauncherSettings.Applications.LABEL, label);
        values.put(LauncherSettings.Applications.CLASS, componentName.flattenToString());
        values.put(LauncherSettings.Applications.CELL_INDEX, index);

        cr.insert(LauncherSettings.Applications.CONTENT_URI, values);
        return id;
    }

    private long addToDatabase(Context context, final int index, final ComponentName componentName, final String label,
            boolean canDrag) {
        final ContentValues values = new ContentValues();
        final ContentResolver cr = context.getContentResolver();

        LauncherApplication app = (LauncherApplication) context.getApplicationContext();
        long id = app.getLauncherProvider().generateNewAppsId();
        values.put("_id", id);

        values.put(LauncherSettings.Applications.LABEL, label);
        values.put(LauncherSettings.Applications.CLASS, componentName.flattenToString());
        values.put(LauncherSettings.Applications.CELL_INDEX, index);
        values.put(LauncherSettings.Applications.APP_CAN_DRAG, canDrag ? 1 : 0);

        cr.insert(LauncherSettings.Applications.CONTENT_URI, values);
        return id;
    }*/
    /*** RK_ID: Memory Leak. AUT: zhaoxy . DATE: 2012-05-22. END***/

//    private ArrayList<ApplicationInfo> startSortByDb(ArrayList<ApplicationInfo> apps) {
//        // clone apps
//        HashMap<ComponentName, ApplicationInfo> map = new HashMap<ComponentName, ApplicationInfo>();
//        ArrayList<ApplicationInfo> original = new ArrayList<ApplicationInfo>();
//        for (int i = 0; i < apps.size(); i++) {
//            ApplicationInfo appInfo = apps.get(i);
//            original.add(appInfo);
//            map.put(appInfo.componentName, appInfo);
//        }
//
//        final HashMap<Integer, ApplicationInfoInDb> regularMaps = getRegularMapsByDb();
//        ArrayList<ApplicationInfo> newList = new ArrayList<ApplicationInfo>();
//        boolean isNeedSyncDb = false;
//
//        if (regularMaps != null && map != null) {
//            // handle the regular map
//            newList.clear();
//            int last = 0;
////            for (last = 0; last < regularMaps.size(); last++) {
////                ComponentName componentName = regularMaps.get(last).getComponent();
////
////                if (map.containsKey(componentName)) {
////                    final ApplicationInfo appInfo = map.remove(componentName);
////                    original.remove(appInfo);
////                    appInfo.canDrag = regularMaps.get(last).isCanDrag();
////                    newList.add(appInfo);
////                } else {
////                    // this is exception.
////                    // this component name is not in component list, so delete it.
////                    isNeedSyncDb = true;
////                }
////            }
//
//            /*** AUT: zhaoxy . DATE: 2012-03-14 . START***/
//            int count = regularMaps.size();
//            int index = 0;
//            int curr = 0;
//            while(curr < count) {
//				ApplicationInfoInDb value = regularMaps.get(index);
//				if (value == null) {
//					index++;
//					continue;
//				}
//				ComponentName componentName = value.getComponent();
//
//				if (map.containsKey(componentName)) {
//					final ApplicationInfo appInfo = map.remove(componentName);
//					original.remove(appInfo);
//					appInfo.canDrag = value.isCanDrag();
//					newList.add(appInfo);
//				} else {
//					// this is exception.
//					// this component name is not in component list, so delete
//					// it.
//				    /*** AUT: zhaoxy . DATE: 2012-05-08. START***/
//					//isNeedSyncDb = true;
//				    /*** AUT: zhaoxy . DATE: 2012-05-08. END***/
//				}
//				index++;
//				curr++;
//			}
//            /*Iterator<Map.Entry<Integer, ApplicationInfoInDb>> iter = regularMaps.entrySet().iterator();
//            while (iter.hasNext()) {
//                Map.Entry<Integer, ApplicationInfoInDb> entry = (Entry<Integer, ApplicationInfoInDb>) iter.next();
////                int key = entry.getKey();
//                ApplicationInfoInDb value = entry.getValue();
//                ComponentName componentName = value.getComponent();
//
//                if (map.containsKey(componentName)) {
//                    final ApplicationInfo appInfo = map.remove(componentName);
//                    original.remove(appInfo);
//                    appInfo.canDrag = value.isCanDrag();
//                    newList.add(appInfo);
//                } else {
//                    // this is exception.
//                    // this component name is not in component list, so delete it.
//                    isNeedSyncDb = true;
//                }
//            }*/
//            /*** AUT: zhaoxy . DATE: 2012-03-14 . END***/
//
//            last = newList.size();
//            // handle left ones and save to databases.
//            Collections.sort(original, LauncherModel.APP_NAME_COMPARATOR);
//            for (int k = 0; k < original.size(); k++) {
//                final ApplicationInfo appInfo = original.get(k);
//                newList.add(appInfo);
//                /*** AUT: zhaoxy . DATE: 2012-04-27 . START***/
//                //addToDatabase(mContext, last + k, appInfo.componentName, appInfo.title.toString());
//                isNeedSyncDb = true;
//                /*** AUT: zhaoxy . DATE: 2012-04-27 . END***/
//            }
//
//            apps = newList;
//        } // end if (regularList != null && list != null)
//
//        // sync to databases.
//        /*** AUT: zhaoxy . DATE: 2012-04-27 . START***/
//        if (isNeedSyncDb) {
//            AppsCustomizePagedView.mJustEnterEditMode = true;
//            setAction(ACTION_SYNC_TODB, newList);
//        }
//        /*** AUT: zhaoxy . DATE: 2012-04-27 . END***/
//
//        return apps;
//    }

//    private HashMap<Integer, ApplicationInfoInDb> getRegularMapsByDb() {
//        HashMap<Integer, ApplicationInfoInDb> map = new HashMap<Integer, ApplicationInfoInDb>();
//
//        // query databases
//        /*** RK_ID: Memory Leak. AUT: zhaoxy . DATE: 2012-05-22. START***/
//        //Cursor c = mContext.getContentResolver().query(LauncherSettings.Applications.CONTENT_URI, null, null, null,
//                //LauncherSettings.Applications.CELL_INDEX + " ASC");
//        Cursor c = mLauncherApplication.getContentResolver().query(LauncherSettings.Applications.CONTENT_URI, null, null, null,
//                LauncherSettings.Applications.CELL_INDEX + " ASC");
//        /*** RK_ID: Memory Leak. AUT: zhaoxy . DATE: 2012-05-22. END***/
//
//        try {
//            while (c != null && c.moveToNext()) {
//                // final int idIndex = c.getColumnIndexOrThrow("_id");
//                // final int labelIndex = c.getColumnIndexOrThrow("label");
//                final int classIndex = c.getColumnIndexOrThrow(LauncherSettings.Applications.CLASS);
//                final int cellIndex = c.getColumnIndexOrThrow(LauncherSettings.Applications.CELL_INDEX);
//                /*** AUT:zhaoxy . DATE:2012-03-01 . START***/
//                final int dragIndex = c.getColumnIndex(LauncherSettings.Applications.APP_CAN_DRAG);
//                /*** AUT:zhaoxy . DATE:2012-03-01 . END***/
//
//                // String label = c.getString(labelIndex);
//                ComponentName componentName = ComponentName.unflattenFromString(c.getString(classIndex));
//                int index = c.getInt(cellIndex);
//                Log.i("R4", "index = " + index + "    component name = " + componentName.toString());
//
//                /*** AUT:zhaoxy . DATE:2012-03-01 . START***/
//                boolean drag = true;
//                if (dragIndex > -1) {
//                  drag = c.getInt(dragIndex) == 1;
//				   }
//                /*** AUT:zhaoxy . DATE:2012-03-01 . END***/
//                ApplicationInfoInDb app = new ApplicationInfoInDb(componentName, drag);
//                map.put(index, app);
//            }
//                /*** AUT:zhaoxy . DATE:2012-03-01 . START***/
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        } finally {
//                /*** AUT:zhaoxy . DATE:2012-03-01 . END***/
//        	if (c != null) {
//                c.close();
//                c = null;
//        	}
//        }
//
//        return map;
//    }

//    private class ApplicationInfoInDb{
//        public ApplicationInfoInDb(ComponentName name, boolean drag) {
//            this.compnentName = name;
//            this.canDrag = drag;
//        }
//        public ComponentName getComponent() {
//            return this.compnentName;
//        }
//        public boolean isCanDrag() {
//            return this.canDrag;
//        }
//        private ComponentName compnentName;
//        private boolean canDrag;
//    }
}
