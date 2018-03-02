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
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.util.Log;

import com.lenovo.launcher2.Launcher;
import com.lenovo.launcher2.customizer.ConstantAdapter;
import com.lenovo.launcher2.customizer.Debug.R5;


/**
 * Stores the list of all applications for the all apps view.
 */
public class AllAppsList {
    public static final int DEFAULT_APPLICATIONS_NUMBER = 42;
    
    /** The list off all apps. */
    public ArrayList<ApplicationInfo> data =
            new ArrayList<ApplicationInfo>(DEFAULT_APPLICATIONS_NUMBER);
    /** The list of apps that have been added since the last notify() call. */
    public ArrayList<ApplicationInfo> added =
            new ArrayList<ApplicationInfo>(DEFAULT_APPLICATIONS_NUMBER);
    /** The list of apps that have been removed since the last notify() call. */
    public ArrayList<ApplicationInfo> removed = new ArrayList<ApplicationInfo>();
    /** The list of apps that have been modified since the last notify() call. */
    public ArrayList<ApplicationInfo> modified = new ArrayList<ApplicationInfo>();
    /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. START***/
    /** The list of apps that have been hidden since the last notify() call. */
    public ArrayList<ApplicationInfo> hidden = new ArrayList<ApplicationInfo>();
    /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. END***/
    
    // lechand add for stk problem start
    /** The list off all stk related. */
    private Set<ApplicationInfo> stkData =
            new HashSet<ApplicationInfo>(DEFAULT_APPLICATIONS_NUMBER);
        // lechand add for stk problem end
    
//    /** The list off all avialiable_added apps. */
//    public ArrayList<ApplicationInfo> avialiable_added =
//            new ArrayList<ApplicationInfo>();

    private IconCache mIconCache;

    /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. START***/
    private HiddenApplist mHiddenApplist;

    /**
     * Boring constructor.
     */
    /*public AllAppsList(IconCache iconCache) {
        mIconCache = iconCache;
    }*/

    /**
     * @param iconCache
     * @param mHiddenApplist
     */
    public AllAppsList(IconCache iconCache, HiddenApplist hiddenApplist) {
        mIconCache = iconCache;
        mHiddenApplist = hiddenApplist;

        /* RK_ID: RK_APPSCATEGORY. AUT: liuli1 . DATE: 2012-06-25 . START */
//        CategoryAppsUtil.getInstance().loadAllCategory(iconCache.getLauncherApplication());
        component2AppInfo.clear();
        /* RK_ID: RK_APPSCATEGORY. AUT: liuli1 . DATE: 2012-06-25 . END */
    }
    /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. END***/

    /* RK_ID: RK_APPSCATEGORY. AUT: liuli1 . DATE: 2012-06-25 . START */
    private HashMap<String, ApplicationInfo> component2AppInfo = new HashMap<String, ApplicationInfo>();

    public ArrayList<ApplicationInfo> findAppsInfoByComponent(ArrayList<String> components) {
        if (components == null)
            return null;

        final ArrayList<ApplicationInfo> finalApps = new ArrayList<ApplicationInfo>();
        for (int i = 0; i < components.size(); i++) {
            String componentName = components.get(i);
            if (component2AppInfo != null && component2AppInfo.containsKey(componentName)) {
                ApplicationInfo info = component2AppInfo.get(componentName);
                if (!info.hidden)
                    finalApps.add(info);
            } else {                
                // find it from data
                for (int j = 0; j < size(); j++) {
                    if (get(j).componentName.flattenToShortString().equals(componentName)) {
                        component2AppInfo.put(componentName, get(j));
                        if (!get(j).hidden)
                            finalApps.add(get(j));
                    }
                } // end for
            } // end if-else
        }
        return finalApps;
    }
    /* RK_ID: RK_APPSCATEGORY. AUT: liuli1 . DATE: 2012-06-25 . END */

    /**
     * Add the supplied ApplicationInfo objects to the list, and enqueue it into the
     * list to broadcast when notify() is called.
     *
     * If the app is already in the list, doesn't add it.
     */
    public void add(ApplicationInfo info) {
        if (findActivity(data, info.componentName)) {
            return;
        }
        /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. START***/
        if (mHiddenApplist != null) {
            info.hidden = mHiddenApplist.isHidden(info.componentName.flattenToShortString());
        }
        /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. END***/
        data.add(info);
        added.add(info);

        /* RK_ID: RK_APPSCATEGORY. AUT: liuli1 . DATE: 2012-06-25 . START */
        component2AppInfo.put(info.componentName.flattenToShortString(), info);        
        /* RK_ID: RK_APPSCATEGORY. AUT: liuli1 . DATE: 2012-06-25 . END */
    }
    
//    public void addAvialiableApp(ApplicationInfo info){
//        if (findActivity(data, info.componentName)) {
//            return;
//        }
//        /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. START***/
//        if (mHiddenApplist != null) {
//            info.hidden = mHiddenApplist.isHidden(info.componentName.flattenToShortString());
//        }
//        /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. END***/
//        data.add(info);
//        avialiable_added.add(info);
//
//        /* RK_ID: RK_APPSCATEGORY. AUT: liuli1 . DATE: 2012-06-25 . START */
//        component2AppInfo.put(info.componentName.flattenToShortString(), info);        
//        /* RK_ID: RK_APPSCATEGORY. AUT: liuli1 . DATE: 2012-06-25 . END */
//    }
    
    
    
    public void clear() {
    	// lechand add for stk problem start
    			synchronized (stkData) {
    				// ApplicationInfo.dumpApplicationInfoList("lechang", "clear",
    				// data);
    				// stkData.clear();
    				for (ApplicationInfo a : data) {
    					if (a.componentName.getPackageName().equals("com.android.stk")) {
    						stkData.add(a);
    					}
    				}
    			}
    			// lechand add for stk problem end
        data.clear();
        // TODO: do we clear these too?
        added.clear();
        removed.clear();
        modified.clear();
        /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. START***/
        hidden.clear();
        /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. END***/
//        avialiable_added.clear();
        /* RK_ID: RK_APPSCATEGORY. AUT: liuli1 . DATE: 2012-06-25 . START */
        component2AppInfo.clear();
        /* RK_ID: RK_APPSCATEGORY. AUT: liuli1 . DATE: 2012-06-25 . END */
    }

    public int size() {
        return data.size();
    }

    public ApplicationInfo get(int index) {
        return data.get(index);
    }

    /**
     * Add the icons for the supplied apk called packageName.
     */
    public void addPackage(Context context, String packageName) {
        final List<ResolveInfo> matches = findActivitiesForPackage(context, packageName);

        if (matches.size() > 0) {
            ApplicationInfo temp;
            SharedPreferences preferences = context.getSharedPreferences("newapk", 0);
            Editor editor = preferences.edit();   
            for (ResolveInfo info : matches) {
                temp = new ApplicationInfo(context.getPackageManager(), info, mIconCache, null);
                //新安装的只显示new标志，如果同时有数量标志，等点击后再显示数量标志。
                temp.mNewAdd = 1;
                temp.mNewString = "NEW";
//                if (info.filter.hasCategory("android.intent.category.LENOVO_LAUNCHER_NOTIFICAITON"))
//                {
//                    String str = Settings.System.getString(context.getContentResolver(),"NEWMSG_" + packageName);
//                    if (str != null && !str.isEmpty())
//                    {
//                        int num = Integer.parseInt(str);
//                        temp.updateInfo(num);                    
//                    }                       
//                                                               
//                }
                
                add(temp);
                addNewAddApk(context, info.activityInfo.packageName +  "/" + info.activityInfo.name);
                                
                if (editor != null)
                {                                             
                    editor.putBoolean(info.activityInfo.packageName +  "/" + info.activityInfo.name, true).putBoolean(ConstantAdapter.EXCLUDED_SETTING_KEY, true);
                }
            }
            
            if (editor != null)
            {
                editor.commit();
            }
        }

       
    }
    
//    public void addPackageForAvialiable(Context context, String packageName){
//        final List<ResolveInfo> matches = findActivitiesForPackage(context, packageName);
//
//        if (matches.size() > 0) {
//            ApplicationInfo temp;
//            for (ResolveInfo info : matches) {
//                temp = new ApplicationInfo(context.getPackageManager(), info, mIconCache, null);
//                addAvialiableApp(temp);                  
//            }            
//        }       
//    }
    
    public void addPackageNoNew(Context context, String packageName) {
        final List<ResolveInfo> matches = findActivitiesForPackage(context, packageName);

        if (matches.size() > 0) {
            ApplicationInfo temp;
            for (ResolveInfo info : matches) {
                temp = new ApplicationInfo(context.getPackageManager(), info, mIconCache, null);
                add(temp);                  
            }            
        }       
    }

    /**
     * Remove the apps for the given apk identified by packageName.
     */
    public void removePackage(Context context, String packageName, boolean remoevNew) {
        final List<ApplicationInfo> dataTemp = this.data;
        for (int i = dataTemp.size() - 1; i >= 0; i--) {
            ApplicationInfo info = dataTemp.get(i);
            final ComponentName component = info.intent.getComponent();
            if (packageName.equals(component.getPackageName())) {
                removed.add(info);
                dataTemp.remove(i);

                /* RK_ID: RK_APPSCATEGORY. AUT: liuli1 . DATE: 2012-06-25 . START */
                component2AppInfo.remove(info.componentName.flattenToShortString());
                /* RK_ID: RK_APPSCATEGORY. AUT: liuli1 . DATE: 2012-06-25 . END */
                
                if (remoevNew && info.mNewAdd == 1)
                {
                    info.mNewAdd = 0;
                    removeNewApk(context, info.componentName.flattenToString());
                }              
                
            }
        }
        // This is more aggressive than it needs to be.
        mIconCache.flush();
    }    
    /**
     * Add and remove icons for this package which has been updated.
     */
    public void updatePackage(Context context, String packageName) {
    	Log.i("zdx2","AllAppList.updatePackage------"+ packageName);
        final List<ResolveInfo> matches = findActivitiesForPackage(context, packageName);
        Log.i("zdx2","AllAppList.updatePackage------matches.size():"+ matches.size());
        if (matches.size() > 0) {
            // Find disabled/removed activities and remove them from data and add them
            // to the removed list.
            for (int i = data.size() - 1; i >= 0; i--) {
                final ApplicationInfo applicationInfo = data.get(i);
                final ComponentName component = applicationInfo.intent.getComponent();
                Log.i("zdx2","AllAppList.updatePackage------data component"+ component);
                if (packageName.equals(component.getPackageName())) {
                    if (!findActivity(matches, component) || 
                    		// lechang add start for OROCHI-5114
                    		// when PackageManager.setComponentEnabledSetting, some launcher category activities may be hidden, 
                    		// which are even not able to be found by findActivitiesForPackage, we need to remove them also
                    		( applicationInfo.itemType == LauncherSettings.BaseLauncherColumns.ITEM_TYPE_APPLICATION  &&
                    		context.getPackageManager().resolveActivity(applicationInfo.intent, PackageManager.MATCH_DEFAULT_ONLY) == null) 
                    		// lechang add end for OROCHI-5114
                    		){
                    	Log.i("zdx2","AllAppList.updatePackage 0 ------data remove: "+applicationInfo);
                        removed.add(applicationInfo);
                        mIconCache.remove(component);
                        data.remove(i);
                        /* RK_ID: RK_APPSCATEGORY. AUT: liuli1 . DATE: 2012-06-25 . START */
                        component2AppInfo.remove(applicationInfo.componentName.flattenToShortString());
                        /* RK_ID: RK_APPSCATEGORY. AUT: liuli1 . DATE: 2012-06-25 . END */
                    }
                }
            }

            // Find enabled activities and add them to the adapter
            // Also updates existing activities with new labels/icons
            int count = matches.size();
            for (int i = 0; i < count; i++) {
                final ResolveInfo info = matches.get(i);
                Log.i("zdx2","AllAppList.updatePackage 1, ResolveInfo:"+ info +
                	  ", activityInfo:"+ info.activityInfo +
                	  ", applicationInfo:"+ info.activityInfo.applicationInfo + 
                	  ", applicationInfo.packageName:"+ info.activityInfo.applicationInfo.packageName +
                	  ", activityInfo.name:"+ info.activityInfo.name );
                ApplicationInfo applicationInfo = findApplicationInfoLocked(
                        info.activityInfo.applicationInfo.packageName,
                        info.activityInfo.name);
                Log.i("zdx2","AllAppList.updatePackage 2, applicationInfo:"+ applicationInfo);
                if (applicationInfo == null) {
                	Log.i("zdx2","AllAppList.updatePackage 3------data add----------");
                    add(new ApplicationInfo(context.getPackageManager(), info, mIconCache, null));
                } else {
                    mIconCache.remove(applicationInfo.componentName);
                    mIconCache.getTitleAndIcon(applicationInfo, info, null);
                    /* RK_ID: RK_UNINSTALLAPP. AUT: liuli1 . DATE: 2012-06-07 . START */
                    try {
                        int appFlags = context.getPackageManager().getApplicationInfo(packageName, 0).flags;
                        if ((appFlags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0) {
                            if ((appFlags & android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                                applicationInfo.flags |= ApplicationInfo.UPDATED_SYSTEM_APP_FLAG;
                            } else {
                                applicationInfo.flags = 0;
                            }
                        } else {
                            applicationInfo.flags |= ApplicationInfo.DOWNLOADED_FLAG;
                        }
                    } catch (NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    /* RK_ID: RK_UNINSTALLAPP. AUT: liuli1 . DATE: 2012-06-07 . END */
                    Log.i("zdx2","AllAppList.updatePackage 3------data modify :" + applicationInfo);
                    modified.add(applicationInfo);
                }
            }
        } else {
            // Remove all data for this package.
            for (int i = data.size() - 1; i >= 0; i--) {
                final ApplicationInfo applicationInfo = data.get(i);
                final ComponentName component = applicationInfo.intent.getComponent();
                if (packageName.equals(component.getPackageName())) {
                    removed.add(applicationInfo);
                    mIconCache.remove(component);
                    data.remove(i);
                    Log.i("zdx2","AllAppList.updatePackage 4------data remove :" + applicationInfo);
                    /* RK_ID: RK_APPSCATEGORY. AUT: liuli1 . DATE: 2012-06-25 . START */
                    component2AppInfo.remove(applicationInfo.componentName.flattenToShortString());
                    /* RK_ID: RK_APPSCATEGORY. AUT: liuli1 . DATE: 2012-06-25 . END */
                }
            }
        }
     // lechand add for stk problem start
     		synchronized (stkData) {
     			if (packageName.equals("com.android.stk")) {
     				ArrayList<ApplicationInfo> infos = new ArrayList<ApplicationInfo>();
     				for (ApplicationInfo a : stkData) {
     					if ((a.itemType == LauncherSettings.BaseLauncherColumns.ITEM_TYPE_APPLICATION && context
     							.getPackageManager().resolveActivity(a.intent,
     									PackageManager.MATCH_DEFAULT_ONLY) == null)) {
     						boolean isAlreadyAdded = false;
     						for (ApplicationInfo aRemoved : removed) {
     							if (aRemoved.equals(a)) {
     								isAlreadyAdded = true;
     							}
     						}
     						if (!isAlreadyAdded) {
     							Log.i("zdx2",
     									"AllAppList.updatePackage 6------stkdata removed :"
     											+ a);
     							removed.add(a);
     							infos.add(a);
     							mIconCache.remove(a.intent.getComponent());
     							component2AppInfo.remove(a.componentName
     									.flattenToShortString());
     						}
     					}
     				}
     				stkData.removeAll(infos);
     				infos=null;
     			}
     		}
     		// lechand add for stk problem end
    }

    /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. START***/
    public void hideApps(String[] packageNames) {
        final ArrayList<String> toHideList = new ArrayList<String>(packageNames.length);
        for (int i = 0; i < packageNames.length; i++) {
            if (packageNames[i] != null && !packageNames[i].equals("")) {
                toHideList.add(packageNames[i]);
            }
        }
        mHiddenApplist.add(packageNames, true);
        for (int i = data.size() - 1; i >= 0; i--) {
            final ApplicationInfo applicationInfo = data.get(i);
            if (toHideList.contains(applicationInfo.componentName.flattenToShortString())) {
                //toHideList中的都需要隐藏
                applicationInfo.hidden = true;
                hidden.add(applicationInfo);
            } else {
                if (applicationInfo.hidden) {
                    //不在toHideList中的不需要隐藏
                    applicationInfo.hidden = false;
                    hidden.add(applicationInfo);
                }
            }
        }
    }
    /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. END***/

    /**
     * Query the package manager for MAIN/LAUNCHER activities in the supplied package.
     */
    private static List<ResolveInfo> findActivitiesForPackage(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();

        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mainIntent.setPackage(packageName);

        final List<ResolveInfo> apps = packageManager.queryIntentActivities(mainIntent, PackageManager.GET_RESOLVED_FILTER);
        Log.i("zdx2","findActivitiesForPackage----find:"+ apps);
        return apps != null ? apps : new ArrayList<ResolveInfo>();
    }

    /**
     * Returns whether <em>apps</em> contains <em>component</em>.
     */
    private static boolean findActivity(List<ResolveInfo> apps, ComponentName component) {
        final String className = component.getClassName();
        Log.i("zdx2","AllAppsList.findActivity------className :"+className);
        for (ResolveInfo info : apps) {
            final ActivityInfo activityInfo = info.activityInfo;
            Log.i("zdx2","AllAppsList.findActivity-----matchs activityInfo name:"+activityInfo.name);
            if (activityInfo.name.equals(className)) {
            	Log.i("zdx2","AllAppsList.findActivity-----className:"+ className);
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether <em>apps</em> contains <em>component</em>.
     */
    private static boolean findActivity(ArrayList<ApplicationInfo> apps, ComponentName component) {
        final int N = apps.size();
        for (int i=0; i<N; i++) {
            final ApplicationInfo info = apps.get(i);
            if (info.componentName.equals(component)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Find an ApplicationInfo object for the given packageName and className.
     */
    private ApplicationInfo findApplicationInfoLocked(String packageName, String className) {
    	Log.i("zdx2","AllAppsList.findApplicationInfoLocked, packageName:"+ packageName +", className:"+ className);
        for (ApplicationInfo info: data) {
            final ComponentName component = info.intent.getComponent();
            if (packageName.equals(component.getPackageName())
                    && className.equals(component.getClassName())) {
                return info;
            }
        }
        return null;
    }
    
    /*RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2012-03-15 . S*/
    public ArrayList<ApplicationInfo> updateAllPackagesIcon(Context context) {    	
    	ResolveInfo ri = null;
    	PackageManager pm = context.getPackageManager();    	
    	ArrayList<ApplicationInfo> copys = (ArrayList<ApplicationInfo>)data;//.clone();
//    	int count = copys.size(); 
    	
    	ArrayList<ApplicationInfo> newItems = new ArrayList<ApplicationInfo>();
    	for (int i = 0; i < copys.size(); i++) {
            final ApplicationInfo applicationInfo = copys.get(i);
            /* RK_ID: RK_APPSCATEGORY. AUT: liuli1 . DATE: 2012-06-25 . START */
            component2AppInfo.remove(applicationInfo.componentName.flattenToShortString());
            /* RK_ID: RK_APPSCATEGORY. AUT: liuli1 . DATE: 2012-06-25 . END */
            mIconCache.remove(applicationInfo.componentName);
            
            try {
                /* RK_ID: RK_APPSCATEGORY. AUT: liuli1 . DATE: 2013-05-14 . START */
//            	ri = pm.resolveActivity(applicationInfo.intent, 0);
                final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                mainIntent.setPackage(applicationInfo.componentName.getPackageName());
                List<ResolveInfo> apps = pm.queryIntentActivities(mainIntent, 0);

                if (apps != null && apps.size() > 0) {
                    int size = apps.size();
                    String className = applicationInfo.componentName.getClassName();

                    for (int index = 0; index < size; index++) {
                        ResolveInfo temp = apps.get(index);
                        if (temp.activityInfo.name.equals(className)) {
                            ri = temp;
                            break;
                        }
                    } // end for
                }
                /* RK_ID: RK_APPSCATEGORY. AUT: liuli1 . DATE: 2013-05-14 . END */
            } catch (RuntimeException e) {
            	ri = null;
            }
            if(ri == null) {
            	continue;
            }            
            mIconCache.getTitleAndIcon(applicationInfo, ri, null);
            newItems.add(applicationInfo);
            /* RK_ID: RK_APPSCATEGORY. AUT: liuli1 . DATE: 2012-06-25 . START */
            component2AppInfo.put(applicationInfo.componentName.flattenToShortString(), applicationInfo);
            /* RK_ID: RK_APPSCATEGORY. AUT: liuli1 . DATE: 2012-06-25 . END */
        }
    	data.clear();
    	data.addAll(newItems);
    	newItems.clear();
    	return data;
    }
    /*RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2012-03-15 . E*/
    
    private Map<String, Boolean> mNewAddApk = null;
    public boolean isNewAddApk(Context context, String componentName){
        if (mNewAddApk == null)
        {
            R5.echo("mNewAddApk = null");
            SharedPreferences preferences = context.getSharedPreferences("newapk", 0);
            if (preferences != null)
            {
                mNewAddApk = (Map<String, Boolean>)preferences.getAll();
            } 
        }
        
        if (mNewAddApk != null && !mNewAddApk.isEmpty())
        {
            if (mNewAddApk.get(componentName) != null)
            {
//            	R5.echo("isNewAddApk componentName new " + componentName);
                return true;
            }
        }
        
//        R5.echo("isNewAddApk componentName no new " + componentName);
        
        return false;
    }
    
    private void addNewAddApk(Context context, String componentName){
        if (mNewAddApk == null)
        {
            R5.echo("mNewAddApk = null");
            SharedPreferences preferences = context.getSharedPreferences("newapk", 0);
            if (preferences != null)
            {
                mNewAddApk = (Map<String, Boolean>)preferences.getAll();
            }
        }
             
        if (mNewAddApk != null)
        {
            mNewAddApk.put(componentName, true);
        }
        
        return;
    }
    
    public void removeNewApk(Context context, String componentName){        
        if (mNewAddApk != null)
        {
            mNewAddApk.remove(componentName);
        }
        
        SharedPreferences preferences = context.getSharedPreferences("newapk", 0);
        Editor editor = preferences.edit();   
 
        if (editor != null)
        {     
            editor.remove(componentName);
            editor.commit();
        }                   
    }
}
