package com.lenovo.launcher2.backup;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;
import android.util.Log;

import com.lenovo.launcher2.addon.classification.LoadCategoryFolder;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.WaitableTask;
import com.lenovo.launcher2.customizer.ConstantAdapter;
import com.lenovo.launcher2.customizer.Debug;
import com.lenovo.launcher2.customizer.ConstantAdapter.OperationState;
import com.lenovo.launcher2.customizer.Debug.R2;

/**
 * default restore task
 * */
public class FactoryRestoreTask extends WaitableTask {

	public boolean isRestoring = false;
	private BackupManager backupManager = null;

	private SharedPreferences mPref = null;
	private SharedPreferences mPref_loading = null;
	private Context mContext = null;

	private String mTargetProfilePath = ConstantAdapter.DEFAULT_BACKUP_FILE;

	public FactoryRestoreTask(String taskname, final Context context) {
		super(taskname);
		mContext = context;

		mPref = mContext.getSharedPreferences(ConstantAdapter.PREF_FIRST_LAUNCH_CHECK_NAME,
				Activity.MODE_APPEND | Activity.MODE_PRIVATE);
		mPref_loading = mContext.getSharedPreferences(ConstantAdapter.PREF_FIRST_LOADING_DEFAULT_FILE_NAME,
				Activity.MODE_APPEND | Activity.MODE_PRIVATE);
		isRestoring = false;
		isTaskProcessOK = false;

		setPriority(NORM_PRIORITY);
	}

	public void run() {
		super.run();
		isTaskProcessing = true;
		mPref_loading.edit().putBoolean(ConstantAdapter.PREF_FIRST_LOADING_DEFAULT_FILE_KEY, true).putBoolean(ConstantAdapter.EXCLUDED_SETTING_KEY, true).commit();
//		Log.d("liuyg1","run mPref_loading.getBoolean(ConstantAdapter.PREF_FIRST_LOADING_DEFAULT_FILE_KEY,false)"+mPref_loading.getBoolean(ConstantAdapter.PREF_FIRST_LOADING_DEFAULT_FILE_KEY,
//				false));
		if (backupManager == null) {
			backupManager = BackupManager.getInstance( mContext );
		}

		if (backupManager.isBusy()){
			R2.echo("backupManager.isBusy !");
			isTaskProcessing = false;
			return;
		}

		File defaultProfile = new File(mTargetProfilePath);
		if (!defaultProfile.exists()) {
			if(Debug.MAIN_DEBUG_SWITCH){
			R2.echo("Default profile not found !");
}
			isTaskProcessing = false;
			return;
		}

		byte res = backupManager.realRestore(mTargetProfilePath,
				BackupManager.State.RESTORE_FACTORY, null,true);
		if (res == OperationState.SUCCESS||res == OperationState.PAD_APPALY_PHONE_SCENE||res==OperationState.PHONE_APPALY_PAD_SCENE ) {
			isTaskProcessOK = true;
			R2.echo(" FactoryRestoreTask run return OperationState.SUCCESS");
		}else{
			R2.echo(" FactoryRestoreTask run return OperationState.Failed");
		}
		
		isTaskProcessing = false;
		
		isRestoring = false;

	}

	public void setTargetFactoryPrifilePath(String fullpath) {
		mTargetProfilePath = fullpath;
	}

	@Override
	public void onCycleCheck() {
		super.onCycleCheck();
		if(Debug.MAIN_DEBUG_SWITCH){
			R2.echo("FactoryRestoreTask Write cycle end ****************************!");
			}
		mPref.edit().putBoolean(ConstantAdapter.PREF_FIRST_LAUNCH_CHECK_KEY, true).putBoolean(ConstantAdapter.EXCLUDED_SETTING_KEY, true).commit();
	}

	@Override
	public void onTaskFailed() {
		super.onTaskFailed();
		if(Debug.MAIN_DEBUG_SWITCH)
			R2.echo("FactoryRestoreTask Write Unsuccess ****************************!");
		mPref.edit().putBoolean(ConstantAdapter.PREF_FIRST_LAUNCH_CHECK_KEY, true).putBoolean(ConstantAdapter.EXCLUDED_SETTING_KEY, true).commit();
		/* RK_ID:RK_UPDATE_DESKTOP AUT:liuyg1@lenovo.com DATE: 2013-3-28 START */	
		String appVersion = "1.0";
		PackageManager manager = mContext.getPackageManager();
		try { PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
		appVersion = info.versionName;   //版本名
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		SharedPreferences pref = null;
		pref = mContext.getSharedPreferences(ConstantAdapter.PREF_VERSION_LAUNCH_OLD_NAME,
				Activity.MODE_APPEND | Activity.MODE_PRIVATE);
		pref.edit().putBoolean(ConstantAdapter.EXCLUDED_SETTING_KEY, true).putString(ConstantAdapter.PREF_VERSION_LAUNCH_OLD_KEY, appVersion).commit();
//		Log.d("liuyg1","onTaskFailed appVersion ="+appVersion);
		/* RK_ID:RK_UPDATE_DESKTOP AUT:liuyg1@lenovo.com DATE: 2013-3-28 END */
	}

	@Override
	public void onTaskFinished() {
		super.onTaskFinished();
		try {
			Thread.sleep(100L);
		} catch (InterruptedException e) {
		}
		/*PK_ID: CATEGORY AUTH GECN1 DATE:2013-03-21 S*/
//        LoadCategoryFolder loadFolder = new LoadCategoryFolder(mContext);
//        loadFolder.LoadCategoryFolderIntoWorsapce();
        /*PK_ID: CATEGORY AUTH GECN1 DATE:2013-03-21 S*/
		// Log.d("liuyg1"," onTaskFinished() mPref_loading.getBoolean(ConstantAdapter.PREF_FIRST_LOADING_DEFAULT_FILE,false)"+mPref_loading.getBoolean(ConstantAdapter.PREF_FIRST_LOADING_DEFAULT_FILE_KEY,
		// false));
		mPref_loading
				.edit()
				.putBoolean(
						ConstantAdapter.PREF_FIRST_LOADING_DEFAULT_FILE_KEY,
						false).commit();
		//bugfix orochi-2903 zhanglz1 20131201 s
		SharedPreferences sharedPreferencestest = mContext
				.getSharedPreferences("com.lenovo.launcher_preferences",Activity.MODE_APPEND | Activity.MODE_MULTI_PROCESS);
		Log.d("liuyg1","--reLaunch----getlbkdefaultthemeincase="+SettingsValue.getlbkdefaultthemeincase());
		sharedPreferencestest.edit().putString(SettingsValue.PREF_THEME, SettingsValue.getlbkdefaultthemeincase()).commit();
		//bugfix orochi-2903 zhanglz1 20131201 e

		BackupManager.getInstance(mContext).reLaunch();
	}

	@Override
	public void onTaskSucceed() {
		super.onTaskSucceed();
		if(Debug.MAIN_DEBUG_SWITCH){
			R2.echo("FactoryRestoreTask Write success -****************************!");
			}
		mPref.edit().putBoolean(ConstantAdapter.PREF_FIRST_LAUNCH_CHECK_KEY, false).putBoolean(ConstantAdapter.EXCLUDED_SETTING_KEY, true).commit();
		/* RK_ID:RK_UPDATE_DESKTOP_CONFIG AUT:liuyg1@lenovo.com DATE: 2012-11-1 START */	
		String appVersion = "1.0";
		PackageManager manager = mContext.getPackageManager();
		try { PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
		appVersion = info.versionName;   //版本名
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		SharedPreferences pref = null;
		pref = mContext.getSharedPreferences(ConstantAdapter.PREF_VERSION_LAUNCH_OLD_NAME,
				Activity.MODE_APPEND | Activity.MODE_PRIVATE);
		pref.edit().putBoolean(ConstantAdapter.EXCLUDED_SETTING_KEY, true).putString(ConstantAdapter.PREF_VERSION_LAUNCH_OLD_KEY, appVersion).commit();
		/* RK_ID:RK_UPDATE_DESKTOP_CONFIG AUT:liuyg1@lenovo.com DATE: 2012-11-1 END */
	}

}
