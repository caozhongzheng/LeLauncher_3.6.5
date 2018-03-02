package com.lenovo.launcher.components.XAllAppFace;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageParser.Component;
import android.content.pm.ResolveInfo;
import android.util.Log;

import com.lenovo.launcher2.commoninterface.ItemInfo;
import com.lenovo.launcher2.commoninterface.LauncherSettings;

public class SimpleItemInfo extends ItemInfo {		
	public int actionType = -1;
	public static final int ACTION_TYPE_ADD_FOLDER = LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT + 100;
	public static final int ACTION_TYPE_ADD_OTHER_WIDGET = ACTION_TYPE_ADD_FOLDER + 1;
	public static final int ACTION_TYPE_ADD_SHORTCUT = ACTION_TYPE_ADD_FOLDER + 2;
	public static final int ACTION_TYPE_CREATE_WIDGET = ACTION_TYPE_ADD_FOLDER + 3;
	//for shortcut
	public ResolveInfo resolveInfo;
	public Intent intent;
	//for widgets
	public AppWidgetProviderInfo widgetProviderInfo;
	public int[] spanXY;
	
    public SimpleItemInfo(int type, ResolveInfo resolveInfo) {
    	actionType = type;        	
    	this.resolveInfo = resolveInfo;
    	intent = getIntent(type);
    	itemType = actionType;
	}
    
    public SimpleItemInfo(AppWidgetProviderInfo apInfo, int type, int[] spanXY) {
    	actionType = type;
    	widgetProviderInfo = apInfo;
    	itemType = actionType;
    	if (spanXY == null) {
    		this.spanXY = new int[2];
    	} else {
    		this.spanXY = spanXY;
    	}
    }
    
    private Intent getIntent(int type) {
    	Intent intent = null;
    	switch (type) {
    	case ACTION_TYPE_ADD_SHORTCUT:
    		intent = new Intent(Intent.ACTION_CREATE_SHORTCUT);
    		if (resolveInfo != null) {
    		    intent.setClassName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
    		}
    		break;
    	case ACTION_TYPE_ADD_FOLDER:
    		break;
    	case ACTION_TYPE_ADD_OTHER_WIDGET:
    		break;
    	default:
    		break;
    	}
    	return intent;
    }
    public boolean filterSpecialShortcut(){
    	ComponentName c = this.intent.getComponent();
    	if(c ==null)return false;
    	//兼容安全一键清理的版本
    	String regEx = ".*\\.shortcut\\.(Clean|Shortcut)Ac(tivi|itiv)ty";
		Pattern p=Pattern.compile(regEx);
		Matcher m=p.matcher(c.getClassName());
    	if(c.getPackageName().startsWith("com.lenovo.safecenter") && m.find()){
    		return true;
    	}
    	return false;
    }
}
