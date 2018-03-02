package com.lenovo.launcher2.customizer;

import java.util.HashMap;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;


/**
 * @author zhaoxy
 *
 */
public class RegularApplist {

    private static final String TAG = "RegularApplist";
    private static final boolean DEBUG = true;
    private static final String REGULAR_PREFERENCES = "com.lenovo.launcher.regularapplist_preferences";
    private static final String KEY_CMP_PREFIX = "cmp_";
    private static final String KEY_CANDRAG_PREFIX = "candrag_";
    /*** RK_ID: CANNOTDIY.  AUT: zhaoxy . DATE: 2012-09-14 . START***/
    private static final String KEY_CANNOTDIY_PREFIX = "cannotdiy_";
    /*** RK_ID: CANNOTDIY.  AUT: zhaoxy . DATE: 2012-09-14 . END***/
    private static final String KEY_COUNT = "total";
    private static final String KEY_FIRST_READ_DONE = "first_load_done";
    private Context mContext = null;
//    private static RegularApplist mRegularApplist = null;
    private SharedPreferences mPreferences;

    public RegularApplist(Context c) {
        if (DEBUG) Log.d(TAG, "init");
        if (c != null) {
            mContext = c;
//            mRegularApplist = this;
            //Test
            //makeRuleTest();
        } else {
            throw new IllegalArgumentException("Context can not be null!");
        }
    }

    /*public static RegularApplist getInstance() {
        if (DEBUG) Log.d(TAG, "getInstance");
        if (mRegularApplist != null) {
            return mRegularApplist;
        } else {
            return new RegularApplist(Launcher.getInstance());
        }
    }*/

    /**
     * 返回运营商预置列表
     * 
     * @return
     */
    public HashMap<Integer, RegularApplist.AppSequenceInfo> getSortRule() {
        if (DEBUG) Log.d(TAG, "getSortRule");
        HashMap<Integer, RegularApplist.AppSequenceInfo> appRuleMap = null;
        mPreferences = mContext.getSharedPreferences(REGULAR_PREFERENCES, Context.MODE_PRIVATE);
        int count = mPreferences.getInt(KEY_COUNT, -1);
        int index = -1;
        int skip = 0;
        if (count > 0) {
            appRuleMap = new HashMap<Integer, RegularApplist.AppSequenceInfo>();
            for (int i = 0; i < count; i++) {
                index = i - skip;
                String cmpName = mPreferences.getString(KEY_CMP_PREFIX + i, null);
                if (cmpName == null) {
                    skip++;
                    continue;
                }
                boolean canDrag = mPreferences.getBoolean(KEY_CANDRAG_PREFIX + i, true);
                boolean cannotDiy = mPreferences.getBoolean(KEY_CANNOTDIY_PREFIX + i, false);
                /*** AUT: zhaoxy . DATE: 2012-03-29 . START***/
                ComponentName name = ComponentName.unflattenFromString(cmpName);
                if (name == null) {
                    skip++;
                    continue;
                }
                AppSequenceInfo temp = new AppSequenceInfo(name, canDrag, cannotDiy);
                /*** AUT: zhaoxy . DATE: 2012-03-29 . END***/
                appRuleMap.put(index, temp);
            }
        }
        return appRuleMap;
    }

    /**
     * 返回运营商预置不可编辑列表
     * 
     * @return
     */
    public HashMap<String, Boolean> getCannotDiyRule() {
        if (DEBUG) Log.d(TAG, "getSortRule");
        HashMap<String, Boolean> appRuleMap = new HashMap<String, Boolean>();
        mPreferences = mContext.getSharedPreferences(REGULAR_PREFERENCES, Context.MODE_PRIVATE);
        int count = mPreferences.getInt(KEY_COUNT, -1);
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                String cmpName = mPreferences.getString(KEY_CMP_PREFIX + i, null);
                if (cmpName == null) {
                    continue;
                }
                ComponentName name = ComponentName.unflattenFromString(cmpName);
                if (name == null) {
                    continue;
                }
                boolean cannotDiy = mPreferences.getBoolean(KEY_CANNOTDIY_PREFIX + i, false);
                appRuleMap.put(cmpName, cannotDiy);
            }
        }
        return appRuleMap;
    }

    /*** RK_ID: APP_SORT. AUT: zhaoxy . DATE: 2012-05-09. START***/
    public HashMap<String, Integer> getRegularList(boolean isOnlyCannotDrag) {
        if (DEBUG) Log.d(TAG, "getCannotDragList");
        HashMap<String, Integer> resultList = new HashMap<String, Integer>();
        mPreferences = mContext.getSharedPreferences(REGULAR_PREFERENCES, Context.MODE_PRIVATE);
        int count = mPreferences.getInt(KEY_COUNT, -1);
        int index = -1;
        int skip = 0;
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                index = i - skip;
                String cmpName = mPreferences.getString(KEY_CMP_PREFIX + i, null);
                if (cmpName == null) {
                    skip++;
                    continue;
                }
                boolean canDrag = mPreferences.getBoolean(KEY_CANDRAG_PREFIX + i, true);
                if (canDrag && isOnlyCannotDrag) {
                    skip++;
                    continue;
                }
                ComponentName name = ComponentName.unflattenFromString(cmpName);
                if (name == null) {
                    skip++;
                    continue;
                }
                resultList.put(name.flattenToShortString(), index);
            }
        }
        return resultList;
    }
    /*** RK_ID: APP_SORT. AUT: zhaoxy . DATE: 2012-05-09. END***/

    protected void setSortRule(AppSequenceInfo[] applist) {
        if (applist != null && applist.length > 0) {
            mPreferences = mContext.getSharedPreferences(REGULAR_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.clear();
            int skip = 0;
            int index = -1;
            AppSequenceInfo temp = null;
            for (int i = 0; i < applist.length; i++) {
                temp = applist[i - skip];
                if (temp != null) {
                    index = i - skip;
                    editor.putString(KEY_CMP_PREFIX + index, temp.compnentName.flattenToShortString());
                    editor.putBoolean(KEY_CANDRAG_PREFIX + index, temp.canDrag);
                } else {
                    skip++;
                    continue;
                }
            }
            editor.putInt(KEY_COUNT, index + 1);
            editor.commit();
        }
    }
    
    /*private void makeRuleTest() {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final PackageManager packageManager = mContext.getPackageManager();
        List<ResolveInfo> packages = packageManager.queryIntentActivities(mainIntent, 0);
        
        AppSequenceInfo[] applist = new AppSequenceInfo[15];
        int count = Math.min(15, packages.size());
        for (int i = 0; i < count; i++) {
            ResolveInfo temp = packages.get(i);
            String packageName = temp.activityInfo.applicationInfo.packageName;
            ComponentName componentName = new ComponentName(packageName, temp.activityInfo.name);
            applist[i] = new AppSequenceInfo(componentName, i < 5, false);
        }
        setSortRule(applist);
    }*/

    /**
     * @return finished or not when the first load this list.
     */
    public boolean isFirstLoadDone() {
        if (DEBUG) Log.d(TAG, "isFirstLoadDone");
        boolean res = false;
        mPreferences = mContext.getSharedPreferences(REGULAR_PREFERENCES, Context.MODE_PRIVATE);
        res = mPreferences.getBoolean(KEY_FIRST_READ_DONE, false);
        return res;
    }

    /**
     * Set the status to finished.
     */
    public void firstLoadDone() {
        if (DEBUG) Log.d(TAG, "firstLoadDone");
        mPreferences = mContext.getSharedPreferences(REGULAR_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(KEY_FIRST_READ_DONE, true);
        editor.apply();
    }

    /*** RK_ID: CANNOTDIY.  AUT: zhaoxy . DATE: 2012-09-14 . ***/
    public class AppSequenceInfo {

        public AppSequenceInfo(ComponentName name, boolean drag, boolean cannotDiy) {
            if (name != null) {
                this.compnentName = name;
                this.canDrag = drag;
                this.cannotDiy = cannotDiy;
            } else {
                throw new IllegalArgumentException("ComponentName can not be null!");
            }
        }

        public ComponentName getComponent() {
            return this.compnentName;
        }

        public boolean isCanDrag() {
            return this.canDrag;
        }

        private ComponentName compnentName;
        private boolean canDrag;
        private boolean cannotDiy;
    }

}
