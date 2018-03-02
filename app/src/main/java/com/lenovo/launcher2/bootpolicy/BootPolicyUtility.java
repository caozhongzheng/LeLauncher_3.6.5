package com.lenovo.launcher2.bootpolicy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
//import android.util.Log;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.customizer.ConstantAdapter;
import com.lenovo.launcher2.customizer.Utilities;

public class BootPolicyUtility {
	 public static final int UPDATE_PROFILE_START = 5;
	public static final int BACKUP_PROFILE_SUCCESS = 6;
	public static final int BACKUP_PROFILE_FAIL = 7;
	public static final int RESTORE_DEFAULT_PROFILE = 8;
	public static final int SHOW_REMIND_MSG = 9;
	private BootPolicyUtility(){
		
	}
	public static void recordVersion(Context context) {

		SharedPreferences pref = null;
		pref = context.getSharedPreferences(ConstantAdapter.PREF_VERSION_LAUNCH_OLD_NAME,
				Activity.MODE_APPEND | Activity.MODE_PRIVATE);
		PackageManager manager = context.getPackageManager();
		try { PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
		String appVersion = info.versionName;   
		if(appVersion!=null&&!appVersion.equals("")){
//			Log.d("liuyg1","recordVersion"+appVersion);
			pref.edit().putBoolean(ConstantAdapter.EXCLUDED_SETTING_KEY, true).putString(ConstantAdapter.PREF_VERSION_LAUNCH_OLD_KEY, appVersion).commit();
		}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	public static void setDefaultRegularPref(){

		Utilities.newInstance().ensureDir(ConstantAdapter.PREF_REGULAR_PREFERENCES_DEFAUT_PATH);
		String srcFile = ConstantAdapter.PREF_REGULAR_PREFERENCES_DEFAUT_FILE;
		String desFile = "//data//data//com.lenovo.launcher//shared_prefs//com.lenovo.launcher.regularapplist_preferences.xml";
		if(new File(srcFile).exists()&&!new File(desFile).exists()){
		//	Log.d("liuyg1","Copy file 0: " + srcFile + " , to " + desFile);
			Utilities
			.newInstance().ensureDir("//data//data//com.lenovo.launcher//shared_prefs//");
			Utilities
			.newInstance().copyFile(srcFile,desFile);
		}
	}
}
