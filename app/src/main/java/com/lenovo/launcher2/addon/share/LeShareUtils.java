package com.lenovo.launcher2.addon.share;

import java.util.List;

import com.lenovo.launcher2.customizer.Debug;
import com.lenovo.launcher2.customizer.SettingsValue;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class LeShareUtils {
	
	private LeShareUtils(){};
	
	public static boolean isInstalledQiezi(Context context) {
		PackageInfo packageInfo;
		String packagename = "com.lenovo.anyshare";
		try {
			packageInfo = context.getPackageManager().getPackageInfo(
					packagename, 0);

		} catch (NameNotFoundException e) {
			packageInfo = null;
			e.printStackTrace();
		}
		if(packageInfo ==null){
			return false;
		}

		return true;
	}
	
	public static boolean isInstalledRightQiezi(Context context) {
		PackageInfo packageInfo;
		String packagename = "com.lenovo.anyshare";
		try {
			packageInfo = context.getPackageManager().getPackageInfo(
					packagename, 0);
			Log.d("liuyg1","packageInfo.versionCode"+packageInfo.versionCode);

			if(packageInfo.versionCode<=4020002){
				return false;
			}
		} catch (NameNotFoundException e) {
			packageInfo = null;
			e.printStackTrace();
		}
		if(packageInfo ==null){
			return false;
		}

		return true;
	}
	
	public static void showInstallDialog(final Context context,boolean needupdate) {
		Intent intent = new Intent(context,LelauncherDownloadAnyShare.class);
		context.startActivity(intent);
	}
	
	public static String[] findStringArrayByResourceName(String name, Context context) {
    	if (context == null) {
    		return null;
    	}
    	int resID = context.getResources().getIdentifier(name, "array", context.getPackageName());
    	if (resID == 0) {
    		return null;
    	}
    	try {
            return context.getResources().getStringArray(resID);
        } catch (NotFoundException e) {
        	Debug.printException("Utilities->findStringArrayByResourceName error", e);
        }
    	return null;
    }
	
	 public static Drawable findDrawableByResourceName(String name, Context context) {
	        if (context == null)
	            return null;
	        try {
		        int resID = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
		        if (resID == 0) {
		        	return null;
		        }
		        Drawable drawable = context.getResources().getDrawable(resID);
		        return drawable;
	        } catch (NotFoundException e) {
	        	Debug.printException("Utilities->findDrawableByResourceName error", e);
//	            e.printStackTrace();
	        } catch (OutOfMemoryError e) {
	        	Debug.printException("Utilities->findDrawableByResourceName error", e);
//	        	e.printStackTrace();
	        }
	        return null;
	    }
	 
	    public static List<ResolveInfo> findActivitiesForSkin(Context context) {
	    	int defaultIndex = -1;
	        final PackageManager packageManager = context.getPackageManager();

	        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
	        mainIntent.addCategory(SettingsValue.THEME_PACKAGE_CATEGORY);

	        final List<ResolveInfo> apps = packageManager.queryIntentActivities(mainIntent, 0);
	        if (apps == null) {
	        	return null;
	        }
	        
	        String defaultTheme = SettingsValue.getDefaultThemeValue(context);
	        for (int i = 0; i < apps.size(); i++) {
	        	if (defaultTheme.equals(apps.get(i).activityInfo.packageName)) {
	        		defaultIndex = i;
	        		break;
	        	}
	        }
	        if (defaultIndex != -1) {
	        	apps.remove(defaultIndex);
	        }
	        
	        return apps;
	    }

}
