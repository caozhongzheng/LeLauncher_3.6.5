package com.lenovo.launcher2.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.lenovo.launcher2.customizer.Constants;
import com.lenovo.launcher2.customizer.SettingsValue;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import android.util.Log;


public final class LeosAppSettingUtilities {
	public static final String LENOVO_SETTING_PLUGIN_ACTION = "lenovo.intent.setting.plugin.action";
	public static final String LENOVO_SETTING_WIDGET_ACTION = "lenovo.intent.setting.widget.action";
	public static final int LENOVO_SETTING_PLUGIN = 0;
	public static final int LENOVO_SETTING_WIDGET = 1;
	private static ArrayList<LeosAppSettingInfo> mPluginSettingInfos = new ArrayList<LeosAppSettingInfo>();
	private static ArrayList<LeosAppSettingInfo> mWidgetSettingInfos = new ArrayList<LeosAppSettingInfo>();

	
	public static ArrayList<LeosAppSettingInfo> fetchAllInstalledAppSetting(
			Context mContext,int actionInt) {
		PackageManager pm = mContext.getPackageManager();
		List<PackageInfo> packages = pm.getInstalledPackages(0);
		Intent t = new Intent();
		if(actionInt == 0){
			mPluginSettingInfos.clear();
			t.setAction(LENOVO_SETTING_PLUGIN_ACTION);
		}else if(actionInt == 1){
			mWidgetSettingInfos.clear();
			t.setAction(LENOVO_SETTING_WIDGET_ACTION);
		}
		//fornow
		for (PackageInfo p : packages) {
			t.setPackage(p.packageName);
			List<ResolveInfo> ri = pm.queryIntentActivities(t,
					PackageManager.GET_INTENT_FILTERS);
			//pm.getActivityInfo(p.packageName, PackageManager.GET_ACTIVITIES);
			if (ri != null && ri.size() > 0) {
				for(ResolveInfo r : ri){
					String className = r.activityInfo.name;
					if(className== null){
						continue;
					}
					LeosAppSettingInfo mAppSettingInfo = new LeosAppSettingInfo();
					mAppSettingInfo.app_icon = p.applicationInfo.loadIcon(pm);
					/*int describId = r.activityInfo.descriptionRes;
					if(describId !=0){
						mAppSettingInfo.app_describ = mContext.getResources().getString(describId);
					}*/

					String name = p.applicationInfo.loadLabel(pm).toString();
					String activtiyName = r.loadLabel(pm).toString();
				//	if(activtiyName !=null) name = name + ": "+ activtiyName;
					if(activtiyName == null){
						activtiyName = name; 
					}
					mAppSettingInfo.app_name = activtiyName;
					Intent it = new Intent();
					it.setComponent(new ComponentName(p.packageName,className));
					mAppSettingInfo.app_setting_intent = it;
					boolean isOwnPackage = false;
					if(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF.equalsIgnoreCase(p.applicationInfo.packageName)){
						isOwnPackage = true;
					}
					if(actionInt == 0 && isOwnPackage){
						mPluginSettingInfos.add(mAppSettingInfo);
					}else if(actionInt == 1 && isOwnPackage){
						mWidgetSettingInfos.add(mAppSettingInfo);
					}
				}
			}
		}
		if(actionInt == 0){
			return mPluginSettingInfos;
		}else if(actionInt == 1){
			return mWidgetSettingInfos;
		}else{
			return new ArrayList<LeosAppSettingInfo>();
		}
	}
	public static ArrayList<LeosAppSettingInfo> getAppSettingInfos(){
		ArrayList<LeosAppSettingInfo> allAppSettingInfos = new ArrayList<LeosAppSettingInfo>();
		allAppSettingInfos.addAll(mPluginSettingInfos);
        allAppSettingInfos.addAll(mWidgetSettingInfos);
        
		return allAppSettingInfos;
	}

	
	public static void rmLeosAppSettingInfo(LeosAppSettingInfo item,int actionInt) {
		// TODO Auto-generated method stub
		int index = TextUtils.indexOf(item.app_name, ":");
		String appName = null;
		if(index < 0) {
			appName = item.app_name;
		}else{
			appName = TextUtils.substring(item.app_name,0,index);
		}
         
		if(actionInt == 0 && mPluginSettingInfos !=null){
			int i = 0;
			while (i < mPluginSettingInfos.size()) {
				int indexP = TextUtils.indexOf(mPluginSettingInfos.get(i).app_name,
						":");
				String nameP = null;
				if(index < 0) {
					nameP = mPluginSettingInfos.get(i).app_name;
				}else{
					nameP = TextUtils.substring(mPluginSettingInfos.get(i).app_name,0,indexP);
				}
				if (nameP.equals(appName)) {
					mPluginSettingInfos.remove(i);
				} else {
					i++;
				}
			}
		}else if(actionInt == 1 && mWidgetSettingInfos!=null && mWidgetSettingInfos.contains(item)){
			int i = 0;
			while (i < mWidgetSettingInfos.size()) {
				int indexW = TextUtils.indexOf(mWidgetSettingInfos.get(i).app_name,
						":");
				String nameW = null;
				if(index < 0) {
					nameW = mWidgetSettingInfos.get(i).app_name;
				}else{
					nameW = TextUtils.substring(mWidgetSettingInfos.get(i).app_name,0,indexW);
				}
				if (nameW.equals(appName)) {
					mWidgetSettingInfos.remove(i);
				} else {
					i++;
				}
			}
		}
	}
	private LeosAppSettingUtilities() {
	}
}
