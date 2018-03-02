package com.lenovo.launcher2.commoninterface;

import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;

public class HiddenApplist {

    public static final boolean DEBUG = true;
    public static final String TAG = "HiddenApplist";
    public static final String ACTION_SET_APP_HIDDEN = "com.lenovo.launcher.action.SET_APP_HIDDEN";
    public static final String KEY_HIDDENLIST_DATE = "HIDDENLIST_DATE";

    private static final String HIDDENAPPLIST_PREFERENCES = "com.lenovo.launcher.hidden_applist_preferences";
    private SharedPreferences mPreferences = null;
    
    public HiddenApplist(Context mContext) {
        mPreferences = mContext.getSharedPreferences(HIDDENAPPLIST_PREFERENCES, Context.MODE_PRIVATE);
//        add("com.sensky.reader.ReadingJoy_lenovo/com.sensky.sunshinereader.logo.logo");
//        add("com.test.apptest3/.AppTest3Activity");
//        add("cn.msn.messenger/.activity.SplashActivity");
    }
    
    /**
     * @param packageName
     * @return
     */
    public boolean isHidden(String packageName) {
        if (mPreferences == null) {
            return false;
        }
        return mPreferences.getBoolean(packageName, false);
    }
    
    /**
     * @return
     */
    public Set<String> getHiddenList(Context context) {
        if (mPreferences == null) {
            mPreferences = context.getSharedPreferences(HIDDENAPPLIST_PREFERENCES, Context.MODE_PRIVATE);
        }
        return mPreferences.getAll().keySet();
    }
    
    /**
     * @param packageName
     * @return
     */
    public boolean add(String packageName) {
        if (mPreferences == null) {
            return false;
        }
        return mPreferences.edit().putBoolean(packageName, true).commit();
    }
    
    /**
     * @param packageName
     * @return
     */
    public boolean remove(String packageName) {
        if (mPreferences == null) {
            return false;
        }
        return mPreferences.edit().remove(packageName).commit();
    }
    
    /**
     * @param hiddenList
     * @param clearBefore 添加前是否清除以前的记录。
     * @return 是否成功
     */
    public boolean add(String[] hiddenList, boolean clearBefore) {
        if (mPreferences == null) {
            return false;
        }
        SharedPreferences.Editor edit = mPreferences.edit();
        if (clearBefore) {
            edit.clear();
        }
        for (int i = 0; i < hiddenList.length; i++) {
            if (hiddenList[i] != null && !hiddenList[i].equals("")) {
                edit.putBoolean(hiddenList[i], true);
            }
        }
        return edit.commit();
    }

}
