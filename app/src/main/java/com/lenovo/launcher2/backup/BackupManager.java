package com.lenovo.launcher2.backup;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
//import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
//import java.util.Map;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

//import com.android.internal.telephony.ISms;
import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;
import com.lenovo.launcher.components.XAllAppFace.XScreenMngView;
import com.lenovo.launcher2.commoninterface.ProfileExtracter;
import com.lenovo.launcher2.commoninterface.ReliableWaitingThread;
import com.lenovo.launcher2.customizer.ConstantAdapter;
import com.lenovo.launcher2.customizer.Debug;
import com.lenovo.launcher2.customizer.EnableState;
import com.lenovo.launcher2.customizer.ProcessIndicator;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.Utilities;
import com.lenovo.launcher2.customizer.ConstantAdapter.OperationState;
import com.lenovo.launcher2.customizer.Debug.R2;
import com.lenovo.launcher2.customizer.Debug.R3;

/**
 * Author : ChengLiang
 * */
public class BackupManager {

	public Context mContext = null;

	private LinkedHashMap<String, Bitmap> mPreviews = null;

	private List<Bitmap> mCurrentDetails = null;

	private static SdcardStateMonitor sSdcardStateMonitor = null;

	private static boolean isBusy = false;

	private List<String> mReservedName = null;

	// switch for caching preview
	private boolean mMakePreviewCache = true;
	
	// wallpaper manager
	private WallPaperBackupHelper mWallpaperHelper = null;

	//test by dining 2013-06-27 
	private String LAUNCHER_PACKAGE_NAME_PREF = "com.lenovo.launcher";
	
	// indicate diffrent kind of restoring action
	public static enum State {
		RESTORE_FACTORY, RESTORE_TIME_STAMP, RESTORE_NORMAL
	};

	// latest time stamped backup
	private static String mLatestStampedBackupTime = "";

	// avoid some crash.
	public static BackupManager getInstance(Context context) {
		// if (mInstance == null)

		return new BackupManager(context);
	}

	private BackupManager(Context context) {
		mContext = context;

		if (mWallpaperHelper == null) {
			mWallpaperHelper = new WallPaperBackupHelper();
		}

		mMakePreviewCache = true;

		try {
			if (sSdcardStateMonitor == null) {
				sSdcardStateMonitor = new SdcardStateMonitor();
			} else {
				mContext.getApplicationContext().unregisterReceiver(
						sSdcardStateMonitor);
			}
			IntentFilter filter = new IntentFilter();
			filter.addAction(Intent.ACTION_MEDIA_CHECKING);
			filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
			filter.addAction(Intent.ACTION_MEDIA_REMOVED);
			filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
			mContext.getApplicationContext().registerReceiver(
					sSdcardStateMonitor, filter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isBusy() {
		if (Debug.MAIN_DEBUG_SWITCH)
			R2.echo("Busy state is : " + isBusy);
		return isBusy;
	}

	private void setBusy() {
		if (Debug.MAIN_DEBUG_SWITCH)
			R2.echo("Set busy busy !");
		isBusy = true;
	}

	private void setIdle() {
		if (Debug.MAIN_DEBUG_SWITCH)
			R2.echo("Set busy Idle !");
		isBusy = false;
	}

	private void restoreOrFallback(boolean fallback) {
		try {

			if (!Utilities.isSdcardAvalible()) {
				if (Debug.MAIN_DEBUG_SWITCH)
					R2.echo("sdcard not avalible now !");
				return;
			}

			String pathOfBackupCurrentState = ConstantAdapter.DIR_PARENT_OF_STORAGE_BACKUP_FILE
					+ ConstantAdapter.DIR_FALLBACK_WHILE_RESTORE
					+ File.separator;
			/* Lenovo zhaoxin5 20131111 fix multi-user bug begin*/
			//String pathOfBackupWhat = ConstantAdapter.DIR_DATA + File.separator; 	156
			String pathOfBackupWhat = ConstantAdapter.getMyPackageDir(mContext) + File.separator;
			/* Lenovo zhaoxin5 20131111 fix multi-user bug end*/

			if (!fallback) {
				if (Debug.MAIN_DEBUG_SWITCH)
					R2.echo("!restore : " + pathOfBackupWhat + " , to "
							+ pathOfBackupCurrentState);
				Utilities.newInstance().ensureParentsPaths(
						pathOfBackupCurrentState, true);
				// Utilities.newInstance().ensureDir(pathOfBackupCurrentState);
				Utilities.newInstance().deleteFiles(
						new File(pathOfBackupCurrentState), false);
				Utilities.newInstance().copyFiles(pathOfBackupWhat,
						pathOfBackupCurrentState);
			} else {
				if (Debug.MAIN_DEBUG_SWITCH)
					R2.echo("fallback : " + pathOfBackupCurrentState + " , to "
							+ pathOfBackupWhat);
				Utilities.newInstance().copyFiles(pathOfBackupCurrentState,
						pathOfBackupWhat);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static ReliableWaitingThread mFactoryRestoreTask = null;
/* RK_ID:RK_UPDATE_DESKTOP_CONFIG AUT:liuyg1@lenovo.com DATE: 2012-11-1 START */
	private static ReliableWaitingThread mFactoryUpdateTask = null;
/* RK_ID:RK_UPDATE_DESKTOP_CONFIG AUT:liuyg1@lenovo.com DATE: 2012-11-1 END */
	private static ReliableWaitingThread mInterruptRestoreTask = null;

	private String getCurrentFactoryProfileFullPath() {
		String res = ConstantAdapter.DEFAULT_BACKUP_FILE;

		SharedPreferences pf = mContext.getSharedPreferences(
				ConstantAdapter.PREF_FIRST_LAUNCH_CHECK_KEY,
				Activity.MODE_PRIVATE);
		res = pf.getString(ConstantAdapter.PREF_CURR_FACTORY_PROFILE, res);
		if (Debug.MAIN_DEBUG_SWITCH)
			R2.echo("Factory profile full path is : " + res);

		return res;
	}
/* RK_ID:RK_UPDATE_DESKTOP AUT:liuyg1@lenovo.com DATE: 2013-1-17 START */
	public byte performDefaultRestore(boolean loadProfile) {
/* RK_ID:RK_UPDATE_DESKTOP AUT:liuyg1@lenovo.com DATE: 2013-1-17 END */
		try {
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("Touch perform default .");

			boolean fromBU = false, fromInner = false;

			// now first launch restore go !
			File defaultProfile = new File(getCurrentFactoryProfileFullPath());
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("Touch perform default 0.");

			try {
				fromInner = mContext.getResources().getAssets()
						.open(ConstantAdapter.ASSET_PROFILE_BACKUP_FILE) != null;
			} catch (Exception e) {
				fromInner = false;
			}
			fromBU = defaultProfile.exists();
			if (!fromBU && !fromInner) {
				if (Debug.MAIN_DEBUG_SWITCH)
					R2.echo("Default profile not found !");
				return OperationState.CRITICAL_DEFAULT_RESTORING_NO_NEED_START;
			}

			
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("Touch perform default 1.");
//		/* RK_ID:RK_UPDATE_DESKTOP_CONFIG AUT:liuyg1@lenovo.com DATE: 2012-11-1 START */
			final SharedPreferences pref = mContext.getSharedPreferences(
					ConstantAdapter.PREF_FIRST_LAUNCH_CHECK_NAME,
					Activity.MODE_APPEND | Activity.MODE_PRIVATE);
/* RK_ID:RK_UPDATE_DESKTOP AUT:liuyg1@lenovo.com DATE: 2013-1-17 START */
			if (isBusy()) {		
				if (Debug.MAIN_DEBUG_SWITCH)		
					R2.echo("@@@@@@@@@@@@@@Returned for busy .!");		
				if (pref.getBoolean(ConstantAdapter.PREF_FIRST_LAUNCH_CHECK_KEY,
						true)){
					return OperationState.CRITICAL_DEFAULT_RESTORING_ALREADY_START;
				}	
			}
		/* RK_ID:RK_UPDATE_DESKTOP_CONFIG AUT:liuyg1@lenovo.com DATE: 2012-11-1 END */
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("Touch perform default 2.");       

			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("Touch perform default 5.");
/* RK_ID:RK_UPDATE_DESKTOP AUT:liuyg1@lenovo.com DATE: 2013-1-17 START */

			//test by dining 2013-06-26 
			//in the BU version, if the default profile is exist, load it
			if(fromBU&&pref.getBoolean(ConstantAdapter.PREF_FIRST_LAUNCH_CHECK_KEY,
					true)){
				fromInner = false;
			}
			//end test 2013-06-26
			if (loadProfile||pref.getBoolean(ConstantAdapter.PREF_FIRST_LAUNCH_CHECK_KEY,
					true)) {
/* RK_ID:RK_UPDATE_DESKTOP AUT:liuyg1@lenovo.com DATE: 2013-1-17 END */
				if (Debug.MAIN_DEBUG_SWITCH)
					R2.echo("Touch perform default 4.");

				if (mFactoryRestoreTask == null
						|| !mFactoryRestoreTask.isAlive()) {
					if (Debug.MAIN_DEBUG_SWITCH)
						R2.echo("Factory task has not on the way .");
					FactoryRestoreTask CORE = new FactoryRestoreTask(
							"FactoryRestore", mContext);
					if (Debug.MAIN_DEBUG_SWITCH)
						R2.echo("Touch perform default 3.");
					if (fromInner) {
	//					Log.d("liuyg1","fromInner"+fromInner);
						String pathAndName = Utilities
								.newInstance()
								.dumpRawOrAssetsToFile(
										mContext,
										ConstantAdapter.ASSET_PROFILE_BACKUP_FILE,
										ConstantAdapter.ASSET_PROFILE_DUMP_PATH);
						if (Debug.MAIN_DEBUG_SWITCH)
							R2.echo("Now Inner Facory file is : " + pathAndName);
						CORE.setTargetFactoryPrifilePath(pathAndName);
					} else {
						CORE.setTargetFactoryPrifilePath(defaultProfile
								.getPath());
						if (Debug.MAIN_DEBUG_SWITCH)
							R2.echo("Now BU Facory file is : "
									+ defaultProfile.getPath());
					}
					mFactoryRestoreTask = new ReliableWaitingThread(
							"DefaultRestoringWaitingThread", CORE,
							Thread.NORM_PRIORITY);
					mFactoryRestoreTask.start();
				} else {
					if (Debug.MAIN_DEBUG_SWITCH)
						R2.echo("Factory task has on the WAY .");
				}
				return OperationState.CRITICAL_DEFAULT_RESTORING_NEED_START;
			/* RK_ID:RK_UPDATE_DESKTOP_CONFIG AUT:liuyg1@lenovo.com DATE: 2012-11-1 START */
			}else {
				Log.d("liuyg1","fromInner !"+fromInner);
				if((fromInner)&&checkupdateverion()){
					SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
					String defaultAndroidTheme = SettingsValue.getDefaultAndroidTheme(mContext);
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putString(SettingsValue.PREF_THEME_DEFAULT, defaultAndroidTheme);
					editor.putString(SettingsValue.PREF_THEME, defaultAndroidTheme);
					editor.putInt(SettingsValue.PREF_ICON_BG_STYLE, SettingsValue.DEFAULT_ICON_BG_INDEX);
					//运营商定制手机，要求加载默认主题，不使用主题图标。此设置将恢复配置为true，即加载默认主题时应用图标
					editor.putBoolean(SettingsValue.PREF_USE_DEFAULTTHEME_ICON, true);
					
					editor.commit();
					InputStream is = null;			
					try {
						is = mContext.getResources().openRawResource(R.drawable.wallpaper_grass);

						if (is != null) {
							WallpaperManager wm = (WallpaperManager) mContext.getSystemService(Context.WALLPAPER_SERVICE);
							wm.setStream(is);
							is.close();
							is = null;
						}
					} catch (IOException e) {
						Log.d("liuyg1","Launcher->intAtTheBeginning. get Wallpaper failed"+ e);
					} catch (NotFoundException e) {
						Log.d("liuyg1","Launcher->intAtTheBeginning, wallpaper not found"+ e);
					}
				}
//		        	   
//						if (mFactoryUpdateTask == null
//								|| !mFactoryUpdateTask.isAlive()) {
//							if (Debug.MAIN_DEBUG_SWITCH)
//								R2.echo("Factory task has not on the way .");
//							FactoryUpdateProfileTask Core = new FactoryUpdateProfileTask(
//									"FactoryUpdateProfile", mContext);
//		        	   Log.d("liuyg1","checkupdateverion true!" );
//						String pathAndName = Utilities
//								.newInstance()
//								.dumpRawOrAssetsToFile(
//										mContext,
//										ConstantAdapter.ASSET_PROFILE_BACKUP_FILE,
//										ConstantAdapter.ASSET_PROFILE_DUMP_PATH);
//						if (Debug.MAIN_DEBUG_SWITCH)
//							R2.echo("Now Facory file is : " + pathAndName);
//						Core.setTargetFactoryPrifilePath(pathAndName);
//						mFactoryUpdateTask = new ReliableWaitingThread(
//								"DefaultRestoringWaitingThread", Core,
//								Thread.NORM_PRIORITY);
//						mFactoryUpdateTask.start();
//		        	   return OperationState.CRITICAL_DEFAULT_UPDATING_NEED_START;   
//		           }else {
//						if (Debug.MAIN_DEBUG_SWITCH)
//							R2.echo("Factory task has on the WAY .");
//					}
//					return OperationState.CRITICAL_DEFAULT_RESTORING_NO_NEED_START;
//		           }else{
//		        	   Log.d("liuyg1","checkupdateverion false!" );
//		           }
//		           return OperationState.CRITICAL_DEFAULT_RESTORING_NO_NEED_START;
//			/* RK_ID:RK_UPDATE_DESKTOP_CONFIG AUT:liuyg1@lenovo.com DATE: 2012-11-1 END */
			}
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError oom) {
			oom.printStackTrace();
		}

		return OperationState.CRITICAL_DEFAULT_RESTORING_NO_NEED_START;
	}

/* RK_ID:RK_UPDATE_DESKTOP_CONFIG AUT:liuyg1@lenovo.com DATE: 2012-11-1 START */
	private boolean checkupdateverion() {
//		File f = new File(ConstantAdapter.ASSET_PROFILE_DUMP_PATH);
//		if (!f.exists()) {
//			Log.d("liuyg1",ConstantAdapter.ASSET_PROFILE_DUMP_PATH+"!exists");
//			return false;
//		}
		
		SharedPreferences mPref = null;
		mPref = mContext.getSharedPreferences(ConstantAdapter.PREF_VERSION_LAUNCH_OLD_NAME,
				Activity.MODE_APPEND | Activity.MODE_PRIVATE);

		String appVersion = "1.0";
		PackageManager manager = mContext.getPackageManager();
		try { PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
		appVersion = info.versionName;   //版本名
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		Log.d("liuyg1","appVersion"+appVersion);
		String oldVersion = mPref.getString(ConstantAdapter.PREF_VERSION_LAUNCH_OLD_KEY, "");
		Log.d("liuyg1","oldVersion"+oldVersion);
		if(oldVersion.equals(appVersion)){
			return false;

		}else{
			
			SharedPreferences preferrences =
	                mContext.getSharedPreferences(XScreenMngView.CELLLAYOUT_COUNT, Context.MODE_PRIVATE);
			int screenNum = preferrences.getInt(XScreenMngView.CELLLAYOUT_COUNT, 5);
			Log.d("liuyg1","screenNum"+screenNum);
	        if(screenNum>=9){
	        	return false;
	        }
	        
			return true;
		}


	}
/* RK_ID:RK_UPDATE_DESKTOP_CONFIG AUT:liuyg1@lenovo.com DATE: 2012-11-1 END */
	/** mark in case an accident will happen in future */
	public byte checkLastRestoreState() {
		SharedPreferences pref = mContext.getSharedPreferences(
				ConstantAdapter.PREF_RESTORING_STATE, Activity.MODE_APPEND);
		if (!pref.getBoolean(ConstantAdapter.PREF_RESTORING_STATE_KEY_ISCLEAR,
				true)) {
			// we need do something
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("Last plain RESTORE FAILED .");
			return OperationState.CRITICAL_LAST_TIME_STATE_FAILED;
		} else {
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("Last plain RESTORE SUCCEED .");
			return OperationState.CRITICAL_LAST_TIME_STATE_SUCCESS;
		}
	}

	public boolean isNotifiedInterrupt() {
		SharedPreferences pref = mContext.getSharedPreferences(
				ConstantAdapter.PREF_RESTORING_STATE, Activity.MODE_APPEND);
		boolean res = pref.getBoolean(
				ConstantAdapter.PREF_RESTORING_STATE_KEY_NOTIFIED, true);
		return res;
	}

	public boolean performInterruptedRestore() {
		if (checkLastRestoreState() == OperationState.CRITICAL_LAST_TIME_STATE_SUCCESS) {
			return false;
		}

		final SharedPreferences pref = mContext.getSharedPreferences(
				ConstantAdapter.PREF_RESTORING_STATE, Activity.MODE_APPEND);
		//String sceneName = pref.getString(
		//		ConstantAdapter.PREF_RESTORING_WHICH_KEY, "");
		//if (!(mContext instanceof Launcher) || "".equals(sceneName.trim())) {
			// we can not do this in this case
			//return false;
		//}

		for (int i = 0; i < 100; i++) {
			Toast toast = Toast
					.makeText(
							mContext,
							mContext.getString(R.string.profile_restore_failed_of_interruption),
							Toast.LENGTH_LONG);
			if (i < 10)
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			toast.show();
		}
		// Looper.loop();
		if (Debug.MAIN_DEBUG_SWITCH)
			R2.echo("SET TAG : TRUE");
		pref.edit()
				.putBoolean(ConstantAdapter.PREF_RESTORING_STATE_KEY_NOTIFIED,
						true).commit();

		new Thread("DeleteRemainings") {
			public void run() {
				String sceneName = Utilities
						.newInstance()
						.deSuffix(
								new File(
										pref.getString(
												ConstantAdapter.PREF_RESTORING_WHICH_KEY,
												"")).getName(),
								ConstantAdapter.SUFFIX_FOR_BACKUP_FILE);
				/* Lenovo zhaoxin5 20131111 fix multi-user bug begin*/
				//String targetDir = ConstantAdapter.DIR_DATA
				String targetDir = ConstantAdapter.getMyPackageDir(mContext)
				/* Lenovo zhaoxin5 20131111 fix multi-user bug end*/
						+ ConstantAdapter.DIR_DATA_FILES + sceneName
						+ File.separator;
				if (Debug.MAIN_DEBUG_SWITCH)
					R2.echo("Clean remainings: " + targetDir);
				Utilities.newInstance().deleteFiles(new File(targetDir), true);
			}
		}.start();

		return true;
	}

	void setCurrentRestoreState(boolean pre, String whichScene) {
		SharedPreferences pref = mContext.getSharedPreferences(
				ConstantAdapter.PREF_RESTORING_STATE, Activity.MODE_APPEND);

		pref.edit().putBoolean(ConstantAdapter.EXCLUDED_SETTING_KEY, true)
				.commit();

		if (pre) {
			pref.edit()
					.putBoolean(
							ConstantAdapter.PREF_RESTORING_STATE_KEY_ISCLEAR,
							false)
					.putString(ConstantAdapter.PREF_RESTORING_WHICH_KEY,
							whichScene)
					.putBoolean(
							ConstantAdapter.PREF_RESTORING_STATE_KEY_NOTIFIED,
							false).commit();
		} else {
			pref.edit()
					.putBoolean(
							ConstantAdapter.PREF_RESTORING_STATE_KEY_ISCLEAR,
							true).commit();
		}
	}

	/**
	 * if restoreState is not State.RESTORE_DEFAULT, then the
	 * fileOrSceneToRestore should be specified
	 * */
public	byte realRestore(final String fileOrSceneToRestore,
			final State restoreState, EnableState enableState,Boolean isDefaultLbk) {
		// R2
		if (enableState != null && !enableState.enableFolder
				&& !enableState.enablePriorities
				&& !enableState.enableQuickEntries
				&& !enableState.enableSettings && !enableState.enableWallpaper
				&& !enableState.enableWidgets) {
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("None need to restore.");
			return OperationState.RESTORE_NONE;
		}
		// 2R

		if (Debug.MAIN_DEBUG_SWITCH)
			R2.echo("Restore, busy state is : " + isBusy());
		if (isBusy()) {
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("I am busy in restoring.");
			return OperationState.FAIED_WHILE_BUSY;
		}

		// busy
		setBusy();

		final boolean isDefault = restoreState == State.RESTORE_FACTORY;
		final boolean isTimeStamp = restoreState == State.RESTORE_TIME_STAMP;
		final boolean useFullFilePath = isDefault || isTimeStamp;
		// R2
		if (enableState == null || isDefault) {
			enableState = new EnableState();
		}

		final EnableState localEnableState = enableState;
		// 2R

		final String nameOfDefault = useFullFilePath ? Utilities.newInstance()
				.deSuffix(new File(fileOrSceneToRestore).getName(),
						ConstantAdapter.SUFFIX_FOR_BACKUP_FILE) : null;
		try {

			// backup in case falling back
//			if (!isDefault)
//				restoreOrFallback(false);

			ProcessIndicator.getInstance(mContext).setState(
					mContext.getString(R.string.fetch_target_file));

			// -- copy target profile to /data/data/
			final String fullPathWithSuffix = new File(
					useFullFilePath ? fileOrSceneToRestore
							: getFullNameByScenceName(fileOrSceneToRestore))
					.getPath();

			// set state in case power off
			if (!isDefault) {
				setCurrentRestoreState(true, fullPathWithSuffix);
			}

			ProcessIndicator.getInstance(mContext).setState(
					mContext.getString(R.string.unmix_target_file));

			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("unmix is :  " + fullPathWithSuffix);
			final File unmixedFile = useFullFilePath ? unmix(new File(
					/* Lenovo zhaoxin5 20131111 fix multi-user bug begin*/
					//fullPathWithSuffix), ConstantAdapter.DIR_DATA
					fullPathWithSuffix), ConstantAdapter.getMyPackageDir(mContext)
					/* Lenovo zhaoxin5 20131111 fix multi-user bug end*/
					+ ConstantAdapter.DIR_DATA_FILES) : unmix(new File(
					fullPathWithSuffix));

			if (unmixedFile == null || !unmixedFile.exists()) {
				if (Debug.MAIN_DEBUG_SWITCH)
					R2.echo("NULL unmixed file , return");
				ProcessIndicator.getInstance(mContext).setState(
						mContext.getString(R.string.failed_to_unmix));

				// falling back
				restoreOrFallback(true);
				setIdle();
				return OperationState.FAILED_NORMAL;
			}

			ProcessIndicator.getInstance(mContext).setState(
					mContext.getString(R.string.unpacking_file));
			/* Lenovo zhaoxin5 20131111 fix multi-user bug begin*/
			//final String filesWorkIn = ConstantAdapter.DIR_DATA
			final String filesWorkIn = ConstantAdapter.getMyPackageDir(mContext)
			/* Lenovo zhaoxin5 20131111 fix multi-user bug end*/
					+ ConstantAdapter.DIR_DATA_FILES;
			final String filesUnzipedToPath = useFullFilePath ? filesWorkIn
					+ nameOfDefault : filesWorkIn + fileOrSceneToRestore;
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("Unzip : " + unmixedFile.getPath() + "  , to "
						+ filesUnzipedToPath);

			// clean old files
			Utilities.newInstance().deleteFiles(new File(filesUnzipedToPath),
					true);

			boolean unzipRes = Utilities.newInstance().new ZipHelper()
					.unzipToDir(unmixedFile.getPath(), filesUnzipedToPath);
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("Unzip res is : " + unzipRes);
			if (!unzipRes) {
				if (Debug.MAIN_DEBUG_SWITCH)
					R2.echo("Retry To Unzip : " + fullPathWithSuffix);
				Utilities.newInstance().new ZipHelper().unzipToDir(
						fullPathWithSuffix, filesUnzipedToPath);
			}

			new Thread("ExtraFilesHandleThread") {
				public void run() {
					// handle extra files
					String extraPath = filesWorkIn + File.separator
							+ ConstantAdapter.DIR_DATA_FILES_EXTRA_FILES
							+ File.separator;
					List<File> exceptedFilesNotDelete = new ArrayList<File>();
					List<File> exceptedFilesNotCopyTo = new ArrayList<File>();
					if (!localEnableState.enableWallpaper) {
						File exceptedNotDeleteFile = new File(extraPath
								+ "/wallpaper/apps_wallpaper.jpg");
						if (Debug.MAIN_DEBUG_SWITCH)
							R2.echo("Added not delete files ! "
									+ exceptedNotDeleteFile.getPath());
						exceptedFilesNotDelete.add(exceptedNotDeleteFile);

						File exceptedNotCopyFile = new File(filesUnzipedToPath
								+ "/extra/wallpaper/apps_wallpaper.jpg");
						if (Debug.MAIN_DEBUG_SWITCH)
							R2.echo("Added not copy files ! "
									+ exceptedNotCopyFile.getPath());
						exceptedFilesNotCopyTo.add(exceptedNotCopyFile);
					}

					Utilities.newInstance().deleteFiles(new File(extraPath),
							false, exceptedFilesNotDelete);

					Utilities
							.newInstance()
							.copyFiles(
									filesWorkIn
											+ File.separator
											+ new File(filesUnzipedToPath).getName()
											+ File.separator
											+ ConstantAdapter.DIR_DATA_FILES_EXTRA_FILES
											+ File.separator, extraPath,
									exceptedFilesNotCopyTo);
				}
			}.start();
 			/* RK_ID:LOAD_DEFAULT_PROFILE_OPTIMIZE AUT:liuyg1@lenovo.com DATE: 2013-4-11 START */
			new Thread("ExtraSharePrefFilesHandleThread") {
				public void run() {
					// handle extra files
//					String extraPath = filesWorkIn + File.separator
//							+ ConstantAdapter.DIR_DATA_FILES_EXTRA_FILES
//							+ File.separator;
//					List<File> exceptedFilesNotDelete = new ArrayList<File>();
					SharedPreferences regularPrefs = mContext.getSharedPreferences("com.lenovo.launcher.regularapplist_preferences", Context.MODE_PRIVATE);
			        SharedPreferences.Editor editor = regularPrefs.edit();
			        editor.putBoolean(ConstantAdapter.EXCLUDED_SETTING_KEY,
							true);
			        editor.apply();
					List<File> exceptedFilesNotCopyTo = new ArrayList<File>();
					/* Lenovo zhaoxin5 20131111 fix multi-user bug begin*/
					//File f = new File(ConstantAdapter.DIR_DATA + File.separator
					File f = new File(ConstantAdapter.getMyPackageDir(mContext) + File.separator
					/* Lenovo zhaoxin5 20131111 fix multi-user bug end*/
							+ ConstantAdapter.DIR_PREFS);
					File[] files = f
							.listFiles(ConstantAdapter.FILE_FILTER_SHARED_PREFS);
					if (files != null) {
						for (int x = 0; x < files.length; x++) {
							SharedPreferences pref = mContext.getSharedPreferences(
									Utilities.newInstance().deSuffix(
											files[x].getName(),
											ConstantAdapter.SUFFIX_FOR_PREF_FILE),
									Activity.MODE_APPEND);
							// R2.echo("Pref is : " + pref.getAll());
							if (!pref.getBoolean(ConstantAdapter.EXCLUDED_SETTING_KEY,
									false)) {
								 R2.echo("Delete pref file : " + files[x].getName());
								files[x].delete();
							}
							if(files[x].getName().equals("com.lenovo.launcher.regularapplist_preferences.xml")){
								Log.d("liuyg1","files[x].getPath() " + files[x].getPath());
								Log.d("liuyg1","files[x].getPath() " + files[x].getAbsolutePath());
								
								Utilities.newInstance().ensureDir(ConstantAdapter.PREF_REGULAR_PREFERENCES_DEFAUT_PATH);
								String desFile = ConstantAdapter.PREF_REGULAR_PREFERENCES_DEFAUT_FILE;
								if(!new File(desFile).exists()){
									Log.d("liuyg1","Copy file 0: " + files[x].getAbsolutePath() + " , to " + desFile);
								Utilities
								.newInstance().copyFile(files[x].getAbsolutePath(),desFile);
								}
							}
						}
					}
					
					File sf = new File(filesWorkIn
							+ File.separator
							+ new File(filesUnzipedToPath).getName()
							+ File.separator
							+ ConstantAdapter.DIR_DATA_FILES_EXTRA_FILES
							+ File.separator+ConstantAdapter.DIR_PREFS);
					File[] sfiles = sf
							.listFiles(ConstantAdapter.FILE_FILTER_SHARED_PREFS);
					if (sfiles != null) {
						for (int x = 0; x < sfiles.length; x++) {
							SharedPreferences pref = mContext.getSharedPreferences(
									Utilities.newInstance().deSuffix(
											sfiles[x].getName(),
											ConstantAdapter.SUFFIX_FOR_PREF_FILE),
									Activity.MODE_APPEND);
							// R2.echo("Pref is : " + pref.getAll());
							if (pref.getBoolean(ConstantAdapter.EXCLUDED_SETTING_KEY,
									false)) {
								// R2.echo("Delete pref file : " + files[x].getName());
								exceptedFilesNotCopyTo.add(sfiles[x]);
//								Log.d("liuyg1","exceptedFilesNotCopyTo add " + sfiles[x].getAbsolutePath());
							}
						}
					}
					

						/* Lenovo zhaoxin5 20131111 fix multi-user bug begin*/
						//File prefF = new File(ConstantAdapter.DIR_DATA + File.separator
						File prefF = new File(ConstantAdapter.getMyPackageDir(mContext) + File.separator
						/* Lenovo zhaoxin5 20131111 fix multi-user bug end*/		
								+ ConstantAdapter.DIR_PREFS);
						File[] prefFiles = prefF
								.listFiles(ConstantAdapter.FILE_FILTER_SHARED_PREFS);
						if (prefFiles != null) {
							for (int x = 0; x < prefFiles.length; x++) {
								SharedPreferences pref = mContext.getSharedPreferences(
										Utilities.newInstance().deSuffix(
												prefFiles[x].getName(),
												ConstantAdapter.SUFFIX_FOR_PREF_FILE),
										Activity.MODE_APPEND);
								// R2.echo("Pref is : " + pref.getAll());
								if (!pref.getBoolean(ConstantAdapter.EXCLUDED_SETTING_KEY,
										false)) {
									 R2.echo("Delete pref file : " + prefFiles[x].getName());
										Log.d("liuyg1","BackupableSetting Delete "+prefFiles[x].getName());
									prefFiles[x].delete();
								}
							}
						}
					
//					Log.d("liuyg1","src ="+filesWorkIn
//							+ File.separator
//							+ new File(filesUnzipedToPath).getName()
//							+ File.separator
//							+ ConstantAdapter.DIR_DATA_FILES_EXTRA_FILES
//							+ File.separator+ConstantAdapter.DIR_PREFS);
//					
//					Log.d("liuyg1","desc ="+ConstantAdapter.DIR_DATA + File.separator
//							+ ConstantAdapter.DIR_PREFS);
					Utilities
					.newInstance()
					.copyFiles(
							filesWorkIn
									+ File.separator
									+ new File(filesUnzipedToPath).getName()
									+ File.separator
									+ ConstantAdapter.DIR_DATA_FILES_EXTRA_FILES
									/* Lenovo zhaoxin5 20131111 fix multi-user bug begin*/
									//+ File.separator+ConstantAdapter.DIR_PREFS, ConstantAdapter.DIR_DATA + File.separatr
									+ File.separator+ConstantAdapter.DIR_PREFS, ConstantAdapter.getMyPackageDir(mContext) + File.separator
									/* Lenovo zhaoxin5 20131111 fix multi-user bug end*/
									+ ConstantAdapter.DIR_PREFS,
							exceptedFilesNotCopyTo);
					
				}
				
			}.start();
			/* RK_ID:LOAD_DEFAULT_PROFILE_OPTIMIZE AUT:liuyg1@lenovo.com DATE: 2013-4-11 END */
			new Thread("WallPaperRestoreThread") {
				public void run() {
					ProcessIndicator
							.getInstance(mContext)
							.setState(
									mContext.getString(R.string.launch_wallpaper_thread));
					try {
						if (localEnableState.enableWallpaper) {
							// handle diy pics
							String diyPath = ConstantAdapter.DIR_PARENT_OF_STORAGE_BACKUP_FILE
									+ File.separator
									+ ConstantAdapter.DIR_DIY_WALLPAPER
									+ File.separator;
							Utilities.newInstance().deleteFiles(
									new File(diyPath), false);
							Utilities
									.newInstance()
									.copyFiles(
											filesWorkIn
													+ File.separator
													+ new File(
															filesUnzipedToPath).getName()
													+ File.separator
													+ ConstantAdapter.DIR_DIY_WALLPAPER
													+ File.separator, diyPath);

							// handle wallpaper
							// R2
							mWallpaperHelper.restore(
									useFullFilePath ? nameOfDefault
											: fileOrSceneToRestore, mContext);
						}
						// 2R
						mWallpaperHelper.sStateIndicatorOK = true;
					} catch (Exception e) {
						e.printStackTrace();
					} catch (OutOfMemoryError oom) {
						oom.printStackTrace();
					}
				}
			}.start();

			// !
			new Thread() {
				public void run() {
					unmixedFile.delete();
				}
			}.start();

			// 2R
			ProcessIndicator.getInstance(mContext).setState(
					mContext.getString(R.string.init_reloader));
			ProfileReloader reloader = new ProfileReloader(mContext, Utilities
					.newInstance().deSuffix(
							filesWorkIn
									+ File.separator
									+ (useFullFilePath ? nameOfDefault
											: fileOrSceneToRestore),
							ConstantAdapter.SUFFIX_FOR_BACKUP_FILE),
					localEnableState);

			// set is default tag
			reloader.setPerforDefaultProfileTag(isDefault);
		R2.echo("reloader.reloadProfile(isDefaultLbk)");
			boolean res = reloader.reloadProfile(isDefaultLbk);
			R2.echo("reloader.reloadProfile(isDefaultLbk)"+ res);
			if (res) {
				ProcessIndicator.getInstance(mContext).setState(
						mContext.getString(R.string.success_end));
			} else {
				ProcessIndicator.getInstance(mContext).setState(
						mContext.getString(R.string.restore_failed));

				// falling back
				restoreOrFallback(true);
				setIdle();

     			String version = reloader.getProfileVerion();			
				if(version!=null&&version.contains("pad")&&SettingsValue.getCurrentMachineType(mContext)==-1){
					return OperationState.PHONE_APPALY_PAD_SCENE;
				}else if(version!=null&&!version.contains("pad")&&SettingsValue.getCurrentMachineType(mContext)!=-1){
					//Log.d("liuyg123","version = "+version);
                                        //added by yumina for pad systemgui crasehd 
                                        while(!mWallpaperHelper.sStateIndicatorOK){
                                            SystemClock.sleep(100L);
                                        }
					return OperationState.PAD_APPALY_PHONE_SCENE;
				}
				return OperationState.FAILED_NORMAL;
			}
		} catch (Exception e) {
			e.printStackTrace();
			// falling back
			restoreOrFallback(true);
			setIdle();
			return OperationState.FAILED_NORMAL;
		}

		try {
			int count = 0;
			while (!mWallpaperHelper.sStateIndicatorOK) {
				Thread.sleep(100L);
				count++;
				ProcessIndicator.getInstance(mContext).setState(
						mContext.getString(R.string.wait_wallpaper_ready));
				if (count >= 200) {
					ProcessIndicator.getInstance(mContext).setState(
							mContext.getString(R.string.wallpaper_time_out));
					break;
				}
			}
			ProcessIndicator.getInstance(mContext).setState(
					mContext.getString(R.string.clean_remainings));
			Utilities.newInstance().deleteFiles(
					new File(Utilities.newInstance().deSuffix(
							/* Lenovo zhaoxin5 20131111 fix multi-user bug begin*/
							//ConstantAdapter.DIR_DATA 	916
							ConstantAdapter.getMyPackageDir(mContext)
							/* Lenovo zhaoxin5 20131111 fix multi-user bug end*/
									+ ConstantAdapter.DIR_DATA_FILES
									+ (useFullFilePath ? nameOfDefault
											: fileOrSceneToRestore),
							ConstantAdapter.SUFFIX_FOR_BACKUP_FILE)), true);
			ProcessIndicator.getInstance(mContext).setState(
					mContext.getString(R.string.restart_launcher));
			setIdle();

			// set state in case power off
			if (!isDefault) {
				setCurrentRestoreState(false, nameOfDefault);
			}
			return OperationState.SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			// falling back
			restoreOrFallback(true);
			setIdle();
			return OperationState.FAILED_NORMAL;
		} finally {
			System.gc();
			// Mem Opt.
			ProcessIndicator.clean();
		}		
	}

	/**
	 * Restore current data files to a backup file
	 * 
	 * @param fileOrSceneToRestore
	 *            file to backup, if this parameter with suffix ".lbk", it will
	 *            be considered as a full path, otherwise, considered as a scene
	 *            name.
	 * @return {@link OperationState}.FAILED_NO_SDCARD represents sdcard is
	 *         unmount {@link OperationState}.SCENE_SUCCESS represents the
	 *         operation was successful {@link OperationState}
	 *         .SCENE_FAILED_NORMAL represents the operation was failed
	 * 
	 * */
	public byte restore(final String fileOrSceneToRestore,
			EnableState enableState) {
		return restore(fileOrSceneToRestore, State.RESTORE_NORMAL, enableState);
	}

	public byte restore(final String fileOrSceneToRestore,
			final State nowState, EnableState enableState) {
		// handle default lenovo profile
		/*** MODIFYBY: zhaoxy . DATE: 2012-03-20 . START ***/
		if (getDefaultProfileName().equals(fileOrSceneToRestore)
		/*** MODIFYBY: zhaoxy . DATE: 2012-03-20 . END ***/
		&& new File(ConstantAdapter.DEFAULT_BACKUP_FILE).exists()) {
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("Default lenovo profile go.");
			return OperationState.SUCCESS;
		}

		// multi
		else if (isNameReserved(fileOrSceneToRestore)) {

		}
		// firstly

		return realRestore(fileOrSceneToRestore, nowState, enableState,false);
	}

	// R2
	public void setRestoreSelection(Context context) {
		ProcessIndicator.getInstance(mContext).setState("Test indicator now.");

		return;
	}

	// 2R
	/**
	 * check time stamped file
	 * */
	public File getTimeStampedFile() {
		File f = new File(ConstantAdapter.DIR_PARENT_OF_STORAGE_BACKUP_FILE
				+ ConstantAdapter.DIR_TO_STORAGE_BACKUP_FILE
				+ ConstantAdapter.DIR_TIME_STAMPED_STORE);

		File[] fs = f.listFiles(ConstantAdapter.FILE_FILTER_BACKUPFILE);

		if (fs != null) {
			for (int i = 0; i < fs.length; i++) {
				if (fs[i].getName().startsWith(
						ConstantAdapter.PREFIX_TIME_BACKUP_FILE)) {
					if (Debug.MAIN_DEBUG_SWITCH)
						R2.echo("f is : " + fs[i].getPath());
					return fs[i];
				}
			}
		}

		ProcessIndicator.getInstance(mContext).setState(
				mContext.getString(R.string.notify_backup_firstly));
		return null;
	}

	/**
	 * restore latest
	 * */
	public byte performTimeStampedRestore() {

		try {
			File f = getTimeStampedFile();

			if (f == null) {
				ProcessIndicator.getInstance(mContext).setState(
						mContext.getString(R.string.notify_backup_firstly));
				return OperationState.TIME_STAMPED_FILE_NOT_FOUND;
			} else
				return restore(f.getPath(), State.RESTORE_TIME_STAMP,
						new EnableState());

		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError oom) {
			oom.printStackTrace();
		}

		ProcessIndicator.getInstance(mContext).setState(
				mContext.getString(R.string.notify_backup_firstly));
		return OperationState.FAILED_NORMAL;
	}

	/**
	 * Perform time stamped backup
	 * 
	 * @return time stamp
	 * */

	public byte performTimeStampedBackup() {

		if (Debug.MAIN_DEBUG_SWITCH)
			R2.echo("Touch");

		mMakePreviewCache = false;

		String stampStoredPath = ConstantAdapter.DIR_PARENT_OF_STORAGE_BACKUP_FILE
				+ ConstantAdapter.DIR_TO_STORAGE_BACKUP_FILE
				+ ConstantAdapter.DIR_TIME_STAMPED_STORE;

		// ------
		String timeStamp = Utilities.getTimeStamp("yyyy-MM-dd%20hh%30mm%40ss");

		File f = new File(stampStoredPath);
		File[] fs = f.listFiles(ConstantAdapter.FILE_FILTER_BACKUPFILE);

		byte res = backup(ConstantAdapter.PREFIX_TIME_BACKUP_FILE + timeStamp,
				stampStoredPath);

		if (res == OperationState.SUCCESS && fs != null && fs.length > 0) {
			for (File tf : fs) {
				tf.delete();
			}
		}

		mMakePreviewCache = true;

		return res;
	}

	/**
	 * Get latest backup time
	 * 
	 * */

	public String getTimeStampBackupTime() {
		String stampStoredPath = ConstantAdapter.DIR_PARENT_OF_STORAGE_BACKUP_FILE
				+ ConstantAdapter.DIR_TO_STORAGE_BACKUP_FILE
				+ ConstantAdapter.DIR_TIME_STAMPED_STORE;
		File f = new File(stampStoredPath);
		File[] fs = f.listFiles(ConstantAdapter.FILE_FILTER_BACKUPFILE);

		String name = "";
		if (fs != null && fs.length > 0) {
			name = fs[0].getName();
		}

		if ("".equals(name.trim())) {
			return name;
		}

		name = Utilities.newInstance().deSuffix(name,
				ConstantAdapter.SUFFIX_FOR_BACKUP_FILE);
		name = name.replaceAll(ConstantAdapter.PREFIX_TIME_BACKUP_FILE, "");
		name = name.replaceAll("%20", " ");
		name = name.replaceAll("%30", ":");
		name = name.replaceAll("%40", " ");
		String res = mContext.getResources().getString(
				R.string.profile_time_stamped_restore);
		res += "   " + name + "s";
		return res;
	}

	private String DEFAULT_PROFILE_NAME = null;

	public String getDefaultProfileName() {
		/*** MODIFYBY: zhaoxy . DATE: 2012-03-20 . START ***/
		if (DEFAULT_PROFILE_NAME != null && !DEFAULT_PROFILE_NAME.equals("")) {
			return DEFAULT_PROFILE_NAME;
		} else {
			try {
				DEFAULT_PROFILE_NAME = mContext.getResources().getString(
						R.string.pref_default_profile);
			} catch (Exception e) {
				DEFAULT_PROFILE_NAME = "Default Scene";
			}
			return DEFAULT_PROFILE_NAME;
		}
		/*** MODIFYBY: zhaoxy . DATE: 2012-03-20 . END ***/
	}

	public byte backup(String profileName) {
		return backup(profileName, null);
	}

	public byte backup(String profileName, String specifiedStoragePath) {

		if (Debug.MAIN_DEBUG_SWITCH)
			R2.echo("Backup ,busy state is : " + isBusy());

		// check sdcard status
		if (!Utilities.isSdcardAvalible()
				|| !Utilities.newInstance().isFreeSpaceEnough(
						Environment.getExternalStorageDirectory(),
						ConstantAdapter.SDCARD_FREE_BEFORE_SPACE)
				|| !sSdcardStateMonitor.sbCurrentSdcardAvalible) {
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("Sdcard is invalible.");
			setIdle();
			return OperationState.FAILED_NO_SDCARD;
		}

		if (isBusy()) {
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("I am busy backing up.");
			return OperationState.FAIED_WHILE_BUSY;
		}

		setBusy();

		// handle default first
		if (getDefaultProfileName().equals(profileName)
		/*** MODIFYBY: zhaoxy . DATE: 2012-03-20 . START ***/
		|| getDefaultProfileName().equals(profileName)) {
			/*** MODIFYBY: zhaoxy . DATE: 2012-03-20 . END ***/
			setIdle();
			return OperationState.FAILED_NORMAL;
		}

		profileName = profileName.trim();
//		boolean res = false;
		ProfileExtracter extracter = null;
		try {
			extracter = new ProfileExtracter(mContext, profileName);
			ProcessIndicator.getInstance(mContext).setState(
					mContext.getString(R.string.extracter_exracting));
//			res = extracter.extractProfile();
		 extracter.extractProfile();
		} catch (Throwable e) {
			e.printStackTrace();
			if (Debug.MAIN_DEBUG_SWITCH){
				if(extracter != null)
				    R2.echo("Failed to load : " + extracter.getStoragePath());
			}
		}

		// package the profile
		try {
			return realBackup(extracter.getStoragePath(),
					(specifiedStoragePath == null || ""
							.equals(specifiedStoragePath.trim())) ? new File(
							extracter.getStoragePath()).getParentFile()
							.getPath()
							+ File.separator : specifiedStoragePath
							+ File.separator, profileName
							+ ConstantAdapter.SUFFIX_FOR_BACKUP_FILE);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setIdle();
			ProcessIndicator.getInstance(mContext).setState("Not in the FACE.");
			return OperationState.FAILED_NORMAL;
		}
	}

	// be care : this api should be longly scheduled by
	// backup(String profileName, String specifiedStoragePath)
	private byte realBackup(String whosePathOfWorkingIn, String storagePath,
			String storageName) {

		// check sdcard status
		if (!Utilities.isSdcardAvalible()
				|| !Utilities.newInstance().isFreeSpaceEnough(
						Environment.getExternalStorageDirectory(),
						ConstantAdapter.SDCARD_FREE_BEFORE_SPACE)
				|| !sSdcardStateMonitor.sbCurrentSdcardAvalible) {
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("Sdcard is invalible.");
			setIdle();
			return OperationState.FAILED_NO_SDCARD;
		}

		File file = new File(storagePath, storageName);

		// we copy previews to work dir first
		if (mMakePreviewCache) {
			/* Lenovo zhaoxin5 20131111 fix multi-user bug begin*/
			//String previewPics = ConstantAdapter.LOCAL_DATA_FILE_PATH_TO_BACK_UP
			String previewPics = ConstantAdapter.getMyPackageDir(mContext)
			/* Lenovo zhaoxin5 20131111 fix multi-user bug end*/
					+ ConstantAdapter.PROFILE_SNAPSHOT_STORAGE_PATH;

			ProcessIndicator.getInstance(mContext).setState(
					mContext.getString(R.string.ensure_dir));
			String previewTo;
			Utilities.newInstance().ensureDir(
					previewTo = whosePathOfWorkingIn
							+ ConstantAdapter.DIR_SNAPSHOT_PREVIEW);

			ProcessIndicator.getInstance(mContext).setState(
					mContext.getString(R.string.caching_previews));
			Utilities.newInstance().copyFiles(previewPics + File.separator,
					previewTo, ConstantAdapter.FILE_FILTER_SNAPSHOT);
		}

		ProcessIndicator.getInstance(mContext).setState(
				mContext.getString(R.string.picking_wallpaper));

		// now the diy allapplist background
		Utilities.newInstance().copyFiles(
				ConstantAdapter.DIR_PARENT_OF_STORAGE_BACKUP_FILE
						+ ConstantAdapter.DIR_DIY_WALLPAPER + File.separator,
				whosePathOfWorkingIn + ConstantAdapter.DIR_DIY_WALLPAPER);

		// now the extra files , lotus etc.
		List<File> exceptedFilesNotCopyTo =  new ArrayList<File>();
		/* Lenovo zhaoxin5 20131111 fix multi-user bug begin*/
		//File f = new File(ConstantAdapter.DIR_DATA + ConstantAdapter.DIR_DATA_FILES
		File f = new File(ConstantAdapter.getMyPackageDir(mContext) + ConstantAdapter.DIR_DATA_FILES
		/* Lenovo zhaoxin5 20131111 fix multi-user bug begin*/
				+ ConstantAdapter.DIR_DATA_FILES_EXTRA_FILES
				+ ConstantAdapter.DIR_PREFS);
		if(f.exists()){
			File[] files = f
					.listFiles(ConstantAdapter.FILE_FILTER_SHARED_PREFS);
			exceptedFilesNotCopyTo.add(f);
			if(files.length>0){
				for(File file1:files){
					exceptedFilesNotCopyTo.add(file1);	
				}
			}
		}
	
		Utilities.newInstance().copyFiles(
				/* Lenovo zhaoxin5 20131111 fix multi-user bug begin*/
				//ConstantAdapter.DIR_DATA + ConstantAdapter.DIR_DATA_FILES
				ConstantAdapter.getMyPackageDir(mContext) + ConstantAdapter.DIR_DATA_FILES
				/* Lenovo zhaoxin5 20131111 fix multi-user bug end*/
						+ ConstantAdapter.DIR_DATA_FILES_EXTRA_FILES,
				whosePathOfWorkingIn
						+ ConstantAdapter.DIR_DATA_FILES_EXTRA_FILES,exceptedFilesNotCopyTo);

		// now the wall paper
		mWallpaperHelper.backup(whosePathOfWorkingIn
				+ ConstantAdapter.DIR_WALLPAPER, mContext);

		// now backup the data files
		ProcessIndicator.getInstance(mContext).setState(
				mContext.getString(R.string.make_target_file));
		if (Debug.MAIN_DEBUG_SWITCH)
			R2.echo("Will create zip file : " + whosePathOfWorkingIn
					+ "  , to " + storagePath + storageName);
		Utilities.newInstance().new ZipHelper().createZipFile(
				whosePathOfWorkingIn, storagePath + storageName);

		ProcessIndicator.getInstance(mContext).setState(
				mContext.getString(R.string.lock_on_target_file));
		boolean res = mix(file) != null;

		// R2 -- copy snap cache -- ok
//		if (mMakePreviewCache) {
//			String snapCachePath = file.getParentFile().getPath()
//					+ File.separator
//					+ ConstantAdapter.PREFIX_FOR_SNAP_CACHE
//					+ Utilities
//							.newInstance()
//							.deSuffix(file.getName(),
//									ConstantAdapter.SUFFIX_FOR_BACKUP_FILE)
//							.trim();
//			if (Debug.MAIN_DEBUG_SWITCH)
//				R2.echo("Cache path is L : " + snapCachePath);
//
//			try {
//				ProcessIndicator.getInstance(mContext).setState(
//						mContext.getString(R.string.caching_previews));
//				Utilities.newInstance().ensureDir(snapCachePath);
//				Utilities
//						.newInstance()
//						.copyFiles(
//								ConstantAdapter.LOCAL_DATA_FILE_PATH_TO_BACK_UP
//										+ ConstantAdapter.PROFILE_SNAPSHOT_STORAGE_PATH,
//								snapCachePath,
//								ConstantAdapter.FILE_FILTER_SNAPSHOT);
//			} catch (OutOfMemoryError oom) {
//				oom.printStackTrace();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}

		// ! clean up tmp files, do not perform while test
		try {
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("now cleaning -->" + whosePathOfWorkingIn);
			Utilities.newInstance().deleteFiles(new File(whosePathOfWorkingIn),
					true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Mem Opt.
		ProcessIndicator.clean();

		if (res) {
			// ProcessIndicator.getInstance(mContext).setState(
			// mContext.getString(R.string.success_end));
			setIdle();
			return OperationState.SUCCESS;
		} else {
			// ProcessIndicator.getInstance(mContext).setState(
			// mContext.getString(R.string.backup_failed));
			setIdle();
			return OperationState.FAILED_NORMAL;
		}
	}

	/**
	 * check whether the new target name already exists
	 * 
	 * @return {@link OperationState}.FAILED_NO_SDCARD represents sdcard is
	 *         unmount {@link OperationState}.SCENE_NAME_UNIQUE represents the
	 *         given name can be used {@link OperationState}.SCENE_NAME_EXISTS
	 *         represents the given name already exists
	 * */
	public byte checkSensibleName(String sceneName) {
		File f = new File(getFullNameByScenceName(sceneName));
		return checkSensibleName(f.getParent() + File.separator, f.getName());
	}

	/**
	 * check whether the new target name already exists
	 * 
	 * @return please see the overloading implementation
	 *         {@link checkSensibleName}(String sceneName)
	 */
	public byte checkSensibleName(String storagePath, String storageName) {

		// check sdcard status
		if (!Utilities.isSdcardAvalible())
			return OperationState.FAILED_NO_SDCARD;

		File dir = new File(storagePath);
		if (!dir.exists()) {
			Utilities.newInstance().ensureDir(storagePath);
		}

		if (isNameReserved(storageName)) {
			return OperationState.SCENE_NAME_RESERVED;
		}

		File[] files = dir.listFiles(ConstantAdapter.FILE_FILTER_BACKUPFILE);

		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				String name = files[i].getName();

				if (storageName.equalsIgnoreCase(name)) {
					return OperationState.SCENE_NAME_EXISTS;
				}
			}
		}

		return OperationState.SCENE_NAME_UNIQUE;
	}

	/**
	 * Rename a scene form sceneName to newName
	 * 
	 * @return {@link OperationState}.FAILED_NO_SDCARD represents sdcard is
	 *         unmount {@link OperationState}.SCENE_NAME_NOT_FOUND represents
	 *         the operation was successful {@link OperationState}
	 *         .SCENE_NAME_EXISTS represents the operation was failed
	 *         {@link OperationState}.SUCCESS rename success
	 *         {@link OperationState}.FAILED_NORMAL normal fail
	 * */
	public byte renameScene(String sceneName, String newName) {

		// handle default first
		/*** MODIFYBY: zhaoxy . DATE: 2012-03-20 . START ***/
		if ((getDefaultProfileName()!=null&&getDefaultProfileName().equals(sceneName) || getDefaultProfileName()!=null&&getDefaultProfileName()
		/*** MODIFYBY: zhaoxy . DATE: 2012-03-20 . END ***/
		.equals(newName))
				&& new File(ConstantAdapter.DEFAULT_BACKUP_FILE).exists()) {
			return OperationState.FAILED_NORMAL;
		}

		// also check sdcard
		if (!Utilities.isSdcardAvalible())
			return OperationState.FAILED_NO_SDCARD;

		if (!collectSceneNames().contains(sceneName))
			return OperationState.SCENE_NAME_NOT_FOUND;

		if (collectSceneNames().contains(newName))
			return OperationState.SCENE_NAME_EXISTS;

		// this is squid
		if (mPreviews == null) {
			getAvaliblePreviewSnapshots();
		} else {
			mPreviews = new LinkedHashMap<String, Bitmap>();
		}

		Bitmap bmp = mPreviews.get(sceneName);
		if (bmp != null) {
			mPreviews.remove(sceneName);
			mPreviews.put(newName, bmp);
		}

		boolean res = new File(getFullNameByScenceName(sceneName)).delete();

		if (res)
			return OperationState.SUCCESS;

		return OperationState.FAILED_NORMAL;
	}

	/**
	 * fetch all available scene names
	 * */
	public List<String> collectSceneNames() {
		return collectSceneNames(false);
	}

	/**
	 * fetch all available scene full name
	 * */
	private List<String> collectSceneNames(boolean onlySimple) {

		List<String> ls = new ArrayList<String>();

		// check sdcard state
		if (!Utilities.isSdcardAvalible()
				|| !sSdcardStateMonitor.sbCurrentSdcardAvalible)
			return ls;

		Utilities.newInstance().ensureDir(
				ConstantAdapter.DIR_PARENT_OF_STORAGE_BACKUP_FILE);
		Utilities.newInstance().ensureDir(
				ConstantAdapter.DIR_TO_STORAGE_BACKUP_FILE);

		StringBuffer sb = new StringBuffer();
		sb.append(ConstantAdapter.DIR_PARENT_OF_STORAGE_BACKUP_FILE).append(
				ConstantAdapter.DIR_TO_STORAGE_BACKUP_FILE);
		sb.trimToSize();

		try {
			File f = new File(sb.toString());
			File[] fs = f.listFiles(ConstantAdapter.FILE_FILTER_BACKUPFILE);
			if (!(f == null || fs == null))
				for (int i = 0; i < fs.length; i++) {
					if (!fs[i].getName().contains(
							ConstantAdapter.PREFIX_TIME_BACKUP_FILE))
						ls.add(onlySimple ? Utilities.newInstance().deSuffix(
								fs[i].getName(),
								ConstantAdapter.SUFFIX_FOR_BACKUP_FILE) : fs[i]
								.getPath());
				}
		} catch (Exception e) {
			return new ArrayList<String>();
		}

		return ls;
	}

	/**
	 * Extract all preview snapshots. This function is time costly. In case of
	 * thread blocked, the caller should think about a proper handle when it's
	 * time-sensitive.
	 * 
	 * */
	@SuppressWarnings("unchecked")
	public LinkedHashMap<String, Bitmap> getAvaliblePreviewSnapshots() {

		cleanInvalidProfile();

		cleanPreviews();

		cleanReservedName();

		try {

			boolean isSdcardOk = Utilities.isSdcardAvalible()
					&& sSdcardStateMonitor.sbCurrentSdcardAvalible;

			final List<String> ls = collectSceneNames();
			if (mPreviews == null)
				mPreviews = new LinkedHashMap<String, Bitmap>();

			List<Thread> workGroup = new ArrayList<Thread>();

			if (isSdcardOk)
				for (int i = 0; i < ls.size(); i++) {
					final int index = i;

					if (mPreviews.containsKey(ls.get(i)))
						continue;

					// form the work thread group
					workGroup.add(new Thread("FetchPreview"
							+ new File(ls.get(index)).getName()) {
						public void run() {
							// R2
							try {
								String scenceName = new File(ls.get(index))
										.getName();
								if (Debug.MAIN_DEBUG_SWITCH)
									R3.echo("now ------------------- scene is :  "
											+ scenceName);
								scanCacheOrMake(scenceName, false);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				}

			// RX -- for default
			workGroup.add(new Thread() {
				public void run() {
					try {
						String sceneName = new File(
								ConstantAdapter.DEFAULT_BACKUP_FILE).getPath();
						scanCacheOrMake(sceneName, getDefaultProfileName(),
								/* Lenovo zhaoxin5 20131111 fix multi-user bug begin*/
								//false, ConstantAdapter.DIR_DATA
								false, ConstantAdapter.getMyPackageDir(mContext)
								/* Lenovo zhaoxin5 20131111 fix multi-user bug end*/
										+ ConstantAdapter.DIR_DATA_FILES);

						addReservedName(getDefaultProfileName());

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			// XR

			// RX -- for build in profiles
			workGroup.add(new Thread() {
				public void run() {
					try {
						File dir = new File(ConstantAdapter.INNER_PROFLE_DIR);

						if (dir.exists() && dir.isDirectory()) {
							File[] fs = dir
									.listFiles(ConstantAdapter.FILE_FILTER_BACKUPFILE);
							if (fs != null) {
								for (File f : fs) {
									String sceneName = f.getPath();
									String displayName = Utilities
											.newInstance()
											.deSuffix(
													f.getName(),
													ConstantAdapter.SUFFIX_FOR_BACKUP_FILE);

									addReservedName(displayName);

									scanCacheOrMake(
											sceneName,
											displayName,
											false,
											/* Lenovo zhaoxin5 20131111 fix multi-user bug begin*/
											//ConstantAdapter.DIR_DATA
											ConstantAdapter.getMyPackageDir(mContext)
											/* Lenovo zhaoxin5 20131111 fix multi-user bug end*/
													+ ConstantAdapter.DIR_DATA_FILES);
								}
							}
						}
					} catch (Exception t) {
						t.printStackTrace();
					}
				}
			});
			// XR

			// R2
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("Start hard work!");
			// now begin the work
			for (int i = 0; i < workGroup.size(); i++) {
				workGroup.get(i).start();
			}

			// wait all thread return
			// our thread will return for ever
			while (true) {
				// expected
				boolean done = true;
				for (int i = 0; i < workGroup.size(); i++) {
					if (workGroup.get(i).isAlive()) {
						done = false;
						break;
					}
				}

				if (done)
					break;

				try {
					Thread.sleep(80L);
				} catch (InterruptedException e) {
				}
			}

			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("End hard work!");

			workGroup = null;
			System.gc();

			// handle default profile
			// try {
			// if (new File(ConstantAdapter.DEFAULT_BACKUP_FILE).exists())
			// /*** MODIFYBY: zhaoxy . DATE: 2012-03-20 . START ***/
			// mPreviews.put(getDefaultProfileName(), Utilities.newInstance()
			// /*** MODIFYBY: zhaoxy . DATE: 2012-03-20 . END ***/
			// .drawableToBitmap(
			// mLauncher.getResources().getDrawable(R.drawable.default_preview_1)));
			// } catch (Exception e) {
			// e.printStackTrace();
			// }

			// R2
			LinkedHashMap<String, Bitmap> tmpMap = new LinkedHashMap<String, Bitmap>();
			List<String> tmpKeyList = new ArrayList<String>();
			tmpKeyList.addAll(mPreviews.keySet());
			Collections.sort(tmpKeyList, PROFILE_SORT_COMPARATOR);
			for (String s : tmpKeyList) {
				tmpMap.put(s, mPreviews.get(s));
			}

			mPreviews = tmpMap;
			tmpMap = null;
			tmpKeyList = null;
			// 2R

			return mPreviews;

		} catch (Exception e) {
			return new LinkedHashMap<String, Bitmap>();
		} finally {
			System.gc();
		}
	}

	public final Comparator<String> PROFILE_SORT_COMPARATOR = new Comparator<String>() {
		public final int compare(String a, String b) {
			int result;
			if (isNameReserved(a) && isNameReserved(b))
				result = 0;
			else if (!isNameReserved(a) && isNameReserved(b)) {
				result = 1;
			} else if (isNameReserved(a) && !isNameReserved(b)) {
				result = -1;
			} else
				result = Collator.getInstance().compare(a, b);
			return result;
		}
	};

	/**
	 * Please do not set control to true, while scheduling from outside
	 * */
	private boolean scanCacheOrMake(String sceneName, boolean control) {
		return scanCacheOrMake(sceneName, null, control, null);
	}

	/**
	 * Please do not set control to true, while scheduling from outside
	 * */
	private boolean scanCacheOrMake(String sceneName, String displayName,
			boolean control, String toExtraCachePath) {

		if (!mMakePreviewCache) {
			return true;
		}

		boolean useExtraCachePath = toExtraCachePath != null;
		boolean isProfileFullPathFilled = new File(sceneName).exists();

		String sceneNameWithSuffix = isProfileFullPathFilled ? new File(
				sceneName).getName() : sceneName;
		String sceneNameWithoutSuffix = Utilities.newInstance().deSuffix(
				sceneNameWithSuffix, ConstantAdapter.SUFFIX_FOR_BACKUP_FILE);

		String cacheWorkingInPath = (useExtraCachePath ? toExtraCachePath
				: ConstantAdapter.DIR_PARENT_OF_STORAGE_BACKUP_FILE
						+ ConstantAdapter.DIR_TO_STORAGE_BACKUP_FILE)
				+ File.separator
				+ ConstantAdapter.PREFIX_FOR_SNAP_CACHE
				+ sceneNameWithoutSuffix + File.separator;

		if (Debug.MAIN_DEBUG_SWITCH)
			R2.echo("get cachePath : " + cacheWorkingInPath);
		// if cache exists
		String previewPic = cacheWorkingInPath + File.separator
				+ ConstantAdapter.PROFILE_SNAPSHOT_PREVIEW_NAME;
		File preview = new File(previewPic);
		if (!preview.exists()) {
			File[] previews = new File(cacheWorkingInPath)
					.listFiles(ConstantAdapter.FILE_FILTER_SNAPSHOT);
			if (previews != null)
				for (int i = 0; i < previews.length; i++) {
					preview = previews[i];
					if (preview.exists())
						break;
				}
		}

		if (Debug.MAIN_DEBUG_SWITCH)
			R2.echo("check previewPic " + preview.getPath() + "  ,  " + control);

		if (preview.exists()) {
			synchronized (mPreviews) {
				try {
					//Bitmap previewBMP = Bitmap.createScaledBitmap(
					//		BitmapFactory.decodeFile(preview.getPath()), 110,
					//		180, true);
				//test by dining
					//get the half values of the origin bmp
					Bitmap previewBMP = null;
					Bitmap originBmp = BitmapFactory.decodeFile(preview.getPath());
					if(originBmp != null){
						int width = originBmp.getWidth() / 2;
						int height = originBmp.getHeight() / 2;
						
						previewBMP = Bitmap.createScaledBitmap(
								originBmp, width,height, true);
						originBmp.recycle();
						originBmp = null;
					}
					//end test
					if (Debug.MAIN_DEBUG_SWITCH)
						R2.echo("The BMP found is : " + previewBMP + "  for "
								+ sceneName);
					mPreviews.put(displayName == null ? sceneNameWithoutSuffix
							: displayName, previewBMP);
					if (Debug.MAIN_DEBUG_SWITCH)
						R2.echo("Preview Find! " + sceneName);
				} catch (Throwable e) {
					e.printStackTrace();
					if (Debug.MAIN_DEBUG_SWITCH)
						R2.echo("Preview not Find! " + sceneName);
				}
			}

			return true;
		} else {
			// we control this!
			if (control) {
				deleteScene(sceneName);
				return false;
			}

		}

		if (Debug.MAIN_DEBUG_SWITCH)
			R2.echo("cache file not found, now making cache!");

		String sceneFullName = isProfileFullPathFilled ? sceneName
				: getFullNameByScenceName(sceneNameWithoutSuffix);
		if (Debug.MAIN_DEBUG_SWITCH)
			R2.echo("scene full name is : " + sceneFullName);

		// we need copy file to working in path if needed
		if (useExtraCachePath) {
			Utilities.newInstance().copyFile(sceneFullName,
					new File(cacheWorkingInPath).getParent() + File.separator);
		}

		// now mix file
		String unmixedInPath = new File(cacheWorkingInPath).getParent();
		File unmixedFile = unmix(new File(sceneFullName), unmixedInPath);

		// now unzip file
		String unzipStoragePath = unmixedInPath;
		String unzipFilePath = unmixedFile.getPath();
		String targetPreviewExistsParentPath = unzipStoragePath
				+ File.separator + sceneNameWithoutSuffix;
		boolean unziped = Utilities.newInstance().new ZipHelper().unzipToDir(
				unzipFilePath, targetPreviewExistsParentPath);

		String targetPreviewExistsPath = targetPreviewExistsParentPath
				+ File.separator + ConstantAdapter.DIR_SNAPSHOT_PREVIEW
				+ File.separator;

		if (!unziped) {
			Utilities.newInstance().new ZipHelper().unzipToDir(new File(
					sceneFullName).getPath(), targetPreviewExistsParentPath);
		}

		if (Debug.MAIN_DEBUG_SWITCH)
			R2.echo("cachedPath new is : " + cacheWorkingInPath);

		Utilities.newInstance().copyFiles(targetPreviewExistsPath,
				cacheWorkingInPath, ConstantAdapter.FILE_FILTER_SNAPSHOT);

		unmixedFile.delete();
		Utilities.newInstance().deleteFiles(
				new File(unzipStoragePath + File.separator
						+ sceneNameWithoutSuffix), true);

		return scanCacheOrMake(sceneName, displayName, true, toExtraCachePath);

	}

	private List<Bitmap> turboFetchPreviewDetail(String sceneName) {

		String cachePath = new StringBuilder()
				.append(ConstantAdapter.DIR_PARENT_OF_STORAGE_BACKUP_FILE)
				.append(ConstantAdapter.DIR_TO_STORAGE_BACKUP_FILE)
				.append(File.separator)
				.append(ConstantAdapter.PREFIX_FOR_SNAP_CACHE)
				.append(Utilities
						.newInstance()
						.deSuffix(sceneName,
								ConstantAdapter.SUFFIX_FOR_BACKUP_FILE).trim())
				.toString();

		if (Debug.MAIN_DEBUG_SWITCH)
			R2.echo("Get cachePath : " + cachePath);

		File[] files = new File(cachePath)
				.listFiles(ConstantAdapter.FILE_FILTER_SNAPSHOT);

		if (files == null)
			return new ArrayList<Bitmap>();

		List<Bitmap> details = new ArrayList<Bitmap>();
		for (File f : files) {
			Bitmap b = null;
			try {
				b = BitmapFactory.decodeFile(f.getPath());
				details.add(b);
			} catch (Throwable e) {
				continue;
			}
		}

		if (Debug.MAIN_DEBUG_SWITCH)
			R2.echo("Turbo   done!" + sceneName + "   in --> ");

		return details;
	}

	// 2R

	/**
	 * get all snapshots hold by the given scene
	 */
	public List<Bitmap> getDetailSnapshots(String sceneName) {

		// handle default first
		/*** MODIFYBY: zhaoxy . DATE: 2012-03-20 . START ***/
		if (Debug.MAIN_DEBUG_SWITCH)
			R2.echo("Get detail sceneName is : " + sceneName);

		boolean isDefaultProfile = getDefaultProfileName().equals(sceneName)
				&& new File(ConstantAdapter.DEFAULT_BACKUP_FILE).exists();

		boolean isReservedProfile = isNameReserved(sceneName);

		if (isDefaultProfile || isReservedProfile) {
			List<Bitmap> defaultProfileDetail = new ArrayList<Bitmap>();
			try {
				String defaultFileName = new File(
						ConstantAdapter.DEFAULT_BACKUP_FILE).getPath();
				/* Lenovo zhaoxin5 20131111 fix multi-user bug begin*/
				//String targetFile = ConstantAdapter.DIR_DATA
				String targetFile = ConstantAdapter.getMyPackageDir(mContext)
				/* Lenovo zhaoxin5 20131111 fix multi-user bug end*/
						+ ConstantAdapter.DIR_DATA_FILES
						+ ConstantAdapter.PREFIX_FOR_SNAP_CACHE
						+ Utilities
								.newInstance()
								.deSuffix(
										new File(
												isDefaultProfile ? ConstantAdapter.DEFAULT_BACKUP_FILE
														: sceneName).getName(),
										ConstantAdapter.SUFFIX_FOR_BACKUP_FILE)
						+ File.separator
						+ ConstantAdapter.PROFILE_SNAPSHOT_PREVIEW_NAME;
				if (!new File(targetFile).exists()) {
					// R2
					if (Debug.MAIN_DEBUG_SWITCH)
						R2.echo("Touch scanCacheOrMake .");
					boolean res = scanCacheOrMake(defaultFileName,
							getDefaultProfileName(), false,
							/* Lenovo zhaoxin5 20131111 fix multi-user bug begin*/
							ConstantAdapter.getMyPackageDir(mContext)
							//ConstantAdapter.DIR_DATA
							/* Lenovo zhaoxin5 20131111 fix multi-user bug end*/
									+ ConstantAdapter.DIR_DATA_FILES);
					if (Debug.MAIN_DEBUG_SWITCH)
						R2.echo("Touch scanCacheOrMake end . res is : " + res);
					// 2R
				} else {
					if (Debug.MAIN_DEBUG_SWITCH)
						R2.echo("Target File for default is : " + targetFile);
					Bitmap targetPreview = null;
					try {
						targetPreview = BitmapFactory.decodeFile(targetFile);
					} catch (Throwable e) {
						e.printStackTrace();
					}
					if (targetPreview == null) {
						// try {
						// targetPreview =
						// Utilities.newInstance().drawableToBitmap(
						// mContext.getResources().getDrawable(
						// R.drawable.ic));
						// } catch (Throwable e) {
						// }
					}

					if (targetPreview != null)
						defaultProfileDetail.add(targetPreview);
				}

			} catch (Throwable e) {
				e.printStackTrace();
			}

			return defaultProfileDetail;
		}

		return getDetailSnapshots(getFullNameByScenceName(sceneName), true);
	}

	private List<Bitmap> getDetailSnapshots(String bkFile, boolean mixedFile) {

		try {
			cleanPreviews();

			// check sdcard state
			if (!Utilities.isSdcardAvalible()
					|| !sSdcardStateMonitor.sbCurrentSdcardAvalible)
				return new ArrayList<Bitmap>();

			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("bkFile is : " + bkFile + "  , mixed is " + mixedFile);

			String sceneName = Utilities.newInstance().deSuffix(
					new File(bkFile).getName(),
					ConstantAdapter.SUFFIX_FOR_BACKUP_FILE);

			R2.echo("Get detail : " + sceneName);

			// Turbo fetch first
			List<Bitmap> res = turboFetchPreviewDetail(sceneName);
			if (res != null && res.size() != 0) {
				if (Debug.MAIN_DEBUG_SWITCH)
					R2.echo("Turbo fetched : " + sceneName);
				mCurrentDetails = res;
				return res;
			}

			// cache files missing , now make them
			scanCacheOrMake(sceneName, false);

			return turboFetchPreviewDetail(sceneName);
		} catch (Exception e) {
			return new ArrayList<Bitmap>();
		}
	}

	/**
	 * delete scene
	 * 
	 * @return {@link OperationState}.FAILED_NO_SDCARD represents sdcard is
	 *         unmounted {@link OperationState}.SCENE_SUCCESS represents the
	 *         operation was successful {@link OperationState}
	 *         .SCENE_FAILED_NORMAL represents the operation was failed
	 * 
	 * */
	public byte deleteScene(String sceneNameOrFileName) {

		// handle default first
		/*** MODIFYBY: zhaoxy . DATE: 2012-03-20 . START ***/
		if (getDefaultProfileName().equals(sceneNameOrFileName)
		/*** MODIFYBY: zhaoxy . DATE: 2012-03-20 . END ***/
		&& new File(ConstantAdapter.DEFAULT_BACKUP_FILE).exists()) {
			return OperationState.FAILED_NORMAL;
		}

		// sceneNameOrFileName now with suffix
		if (sceneNameOrFileName
				.endsWith(ConstantAdapter.SUFFIX_FOR_BACKUP_FILE)) {
			sceneNameOrFileName = Utilities.newInstance()
					.deSuffix(sceneNameOrFileName,
							ConstantAdapter.SUFFIX_FOR_BACKUP_FILE);
		}

		if (Debug.MAIN_DEBUG_SWITCH)
			R2.echo("Will check delete : " + sceneNameOrFileName);

		if (!Utilities.isSdcardAvalible()
				|| !sSdcardStateMonitor.sbCurrentSdcardAvalible)
			return OperationState.FAILED_NO_SDCARD;

		File f = null;
		if (sceneNameOrFileName
				.contains(ConstantAdapter.SUFFIX_FOR_BACKUP_FILE)) {
			f = new File(sceneNameOrFileName);
		} else {
			f = new File(getFullNameByScenceName(sceneNameOrFileName));
		}

		if (f != null && f.exists()) {
			String sceneName = Utilities.newInstance().deSuffix(f.getName(),
					ConstantAdapter.SUFFIX_FOR_BACKUP_FILE);
			// R2
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("Delete SCENE: " + f.getPath());
			boolean res = f.delete();

			// clear cache
			Utilities.newInstance().deleteFiles(
					new File(ConstantAdapter.DIR_PARENT_OF_STORAGE_BACKUP_FILE
							+ ConstantAdapter.DIR_TO_STORAGE_BACKUP_FILE
							+ File.separator + sceneName), true);
			Utilities
					.newInstance()
					.deleteFiles(
							new File(
									ConstantAdapter.DIR_PARENT_OF_STORAGE_BACKUP_FILE
											+ ConstantAdapter.DIR_TO_STORAGE_BACKUP_FILE
											+ File.separator
											+ ConstantAdapter.PREFIX_FOR_SNAP_CACHE
											+ sceneName), true);

			if (res) {
				if (mPreviews != null) {
					if (Debug.MAIN_DEBUG_SWITCH)
						R2.echo("Recycle bitmap .");
					try {
						mPreviews.get(sceneName).recycle();
					} catch (Exception e) {
						e.printStackTrace();
					}
					mPreviews.remove(sceneName);
				}
			}

			if (res)
				return OperationState.SUCCESS;
		}

		return OperationState.FAILED_NORMAL;
	}

	public String getFullNameByScenceName(String scenceName) {
		StringBuilder fullName = new StringBuilder();
		fullName.append(ConstantAdapter.DIR_PARENT_OF_STORAGE_BACKUP_FILE)
				.append(ConstantAdapter.DIR_TO_STORAGE_BACKUP_FILE)
				.append(File.separator).append(scenceName)
				.append(ConstantAdapter.SUFFIX_FOR_BACKUP_FILE);
		fullName.trimToSize();
		return new File(fullName.toString()).getPath();
	}

	private File mix(File file) {
		if (!file.exists())
			return null;
		File tmpFile = new File(file.getParent(), new StringBuilder()
				.append(file.getName())
				.append(ConstantAdapter.SUFFIX_FOR_MIX_TEMP_FILE).toString());
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		byte[] buf = new byte[1024];
		try {
			bis = new BufferedInputStream(new FileInputStream(file));
			bos = new BufferedOutputStream(new FileOutputStream(tmpFile));
			try {
				int length = 0;
				while ((length = bis.read(buf)) != -1) {
					bos.write(Utilities.newInstance().codecBytes(buf, false),
							0, length);
				}

				File f = file.getAbsoluteFile();
				file.delete();
				tmpFile.renameTo(f);
				return f;
			} catch (Exception e) {
				return null;
			}
		} catch (Exception e) {
			return null;
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}
			if (bos != null) {
				try {
					bos.flush();
					bos.close();
				} catch (IOException e) {
					return null;
				}
			}
		}
	}

	private File unmix(File file) {
		return unmix(file, null);
	}

	private void cleanInvalidProfile() {
		File f = new File(ConstantAdapter.DIR_PARENT_OF_STORAGE_BACKUP_FILE
				+ File.separator + ConstantAdapter.DIR_TO_STORAGE_BACKUP_FILE);
		File[] fsTMP = f.listFiles(ConstantAdapter.FILE_FILTER_TMPX);
		for (int i = 0; fsTMP != null && i < fsTMP.length; i++) {
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("Delete invalid : " + fsTMP[i].getPath());
			fsTMP[i].delete();
		}
	}

	private void cleanReservedName() {

		if (mReservedName != null) {
			mReservedName.clear();
		} else {
			mReservedName = new ArrayList<String>();
		}

	}

	private void addReservedName(String nameToAdd) {
		if (mReservedName == null) {
			mReservedName = new ArrayList<String>();
		}

		if (!mReservedName.contains(nameToAdd)) {
			mReservedName.add(nameToAdd);
		}
	}

	public boolean isNameReserved(String nameToCheck) {

		// R2
		if (Debug.MAIN_DEBUG_SWITCH)
			android.util.Log.i("RX", "check: " + nameToCheck + " \n in : \n"
					+ mReservedName);
		// 2R
		if (nameToCheck == null)
			return false;
		nameToCheck = Utilities.newInstance().deSuffix(nameToCheck,
				ConstantAdapter.SUFFIX_FOR_BACKUP_FILE);

		// check for defalut
		try {
			if (Arrays.asList(
					mContext.getString(
							R.string.reserved_profile_name_for_default_profile)
							.split(":")).contains(nameToCheck)) {
				return true;
			}
		} catch (Exception e) {
			ProcessIndicator.getInstance(mContext).setState(
					R.string.profile_apply_profile_fail);
			return false;
		}

		if (mReservedName == null || mReservedName.isEmpty()) {
			return false;
		} else if (mReservedName.contains(nameToCheck)) {
			return true;
		} else
			return false;
	}

	public String getReservedProfileFullPath(String nameToFind) {
		if (mReservedName.contains(nameToFind)) {
			return ConstantAdapter.INNER_PROFLE_DIR + File.separator
					+ nameToFind + ConstantAdapter.SUFFIX_FOR_BACKUP_FILE;
		} else
			return "[%*#(#FAILD#)&^**(]";
	}

	public void cleanPreviews() {

		try {
			mContext.getApplicationContext().unregisterReceiver(
					sSdcardStateMonitor);
		} catch (Exception e1) {
			// e1.printStackTrace();
		}

		try {
			if (mCurrentDetails != null) {
				for (int i = 0; i < mCurrentDetails.size(); i++) {
					try {
						mCurrentDetails.get(i).recycle();
					} catch (Exception e) {
						// e.printStackTrace();
					}
				}

				mCurrentDetails.clear();
				mCurrentDetails = null;
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}

		try {
			if (mPreviews != null) {
				for (int i = 0; i < mPreviews.size(); i++) {
					try {
						mPreviews.get(""+i).recycle();
					} catch (Exception e) {
						// e.printStackTrace();
					}
				}

				mPreviews.clear();
				mPreviews = null;
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	/**
	 * may change in future , so we separated this
	 */
	private File unmix(File file, String targetDir) {
		Utilities.newInstance().ensureDir(targetDir);
		if (!file.exists())
			return null;
		File tmpFile = null;
		if (targetDir == null) {
			tmpFile = new File(file.getParent(), new StringBuilder()
					.append(file.getName())
					.append(ConstantAdapter.SUFFIX_FOR_UNMIX_TEMP_FILE)
					.toString());
		} else {
			tmpFile = new File(new StringBuilder().append(targetDir)
					.append(File.separator).append(file.getName())
					.append(ConstantAdapter.SUFFIX_FOR_UNMIX_TEMP_FILE)
					.toString());
		}
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		byte[] buf = new byte[1024];
		try {
			bis = new BufferedInputStream(new FileInputStream(file));
			bos = new BufferedOutputStream(new FileOutputStream(tmpFile));
			try {
				int len = 0;
				do {
					len = bis.read(buf);
					if (len != -1)
						bos.write(
								Utilities.newInstance().codecBytes(buf, true),
								0, len);
				} while (len != -1);
				bos.flush();
				if (tmpFile.exists())
					return tmpFile;
				else
					return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}
			if (bos != null) {
				try {
					bos.flush();
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private static boolean isSdcardCapacityOK() {

		return Utilities.newInstance().isFreeSpaceEnough(
				Environment.getExternalStorageDirectory(),
				ConstantAdapter.BACKUP_FILE_MIN_NEEDED_SPACE_IN_BYTE_FOR_CHECK);
	}

	/**
	 * Wallpaper backup & restore
	 * */
	public class WallPaperBackupHelper {

		public boolean sStateIndicatorOK = true;

		private void clear() {
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("In wallpaper clear()");
			/* Lenovo zhaoxin5 20131111 fix multi-user bug begin*/
			//File f = new File(ConstantAdapter.BACKUP_WALL_PAPER_FILE);
			File f = new File(ConstantAdapter.getMyPackageDir(mContext)+"//files//wallpaper.png");
			/* Lenovo zhaoxin5 20131111 fix multi-user bug end*/
			if (f.exists()) {
				f.delete();
			}
			R2.echo("Out wallpaper clear()");
		}

		public void backup(String pathToStorage, Context context) {
			sStateIndicatorOK = false;
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("In wallpaper backup()");
			clear();

			WallpaperManager wpm = WallpaperManager.getInstance(context);
			Utilities.newInstance().ensureDir(pathToStorage);
			String fileIs = pathToStorage + File.separator + "wallpaper.png";
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("Save wallpaper to : " + fileIs);
			Utilities.newInstance().saveBitmapToPng(fileIs, wpm.getBitmap());
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("Out wallpaper backup()");

			sStateIndicatorOK = true;

			wpm = null;

			System.gc();

		}

		public void restore(String profilePath, Context context) {
			sStateIndicatorOK = false;
			try {
				if (Debug.MAIN_DEBUG_SWITCH)
					R2.echo("In wallpaper restore()");
				/* Lenovo zhaoxin5 20131111 fix multi-user bug begin*/
				//String wallpaperPath = ConstantAdapter.DIR_DATA
				String wallpaperPath = ConstantAdapter.getMyPackageDir(mContext)
				/* Lenovo zhaoxin5 20131111 fix multi-user bug end*/
						+ ConstantAdapter.DIR_DATA_FILES + profilePath
						+ ConstantAdapter.DIR_WALLPAPER + "//wallpaper.png";
				/*PK_ID:LBK_READ_WALLPAPER_PNG/JPG AUTH:GECN1 DATE:2012-11-6 S*/
				/* Lenovo zhaoxin5 20131111 fix multi-user bug begin*/
				//String wallpaperPath_JPG = ConstantAdapter.DIR_DATA
				String wallpaperPath_JPG = ConstantAdapter.getMyPackageDir(mContext)
				/* Lenovo zhaoxin5 20131111 fix multi-user bug end*/
						+ ConstantAdapter.DIR_DATA_FILES + profilePath
						+ ConstantAdapter.DIR_WALLPAPER + "//wallpaper.jpg";
				/*PK_ID:LBK_READ_WALLPAPER_PNG/JPG AUTH:GECN1 DATE:2012-11-6 E*/
				WallpaperManager wpm = WallpaperManager.getInstance(context);
				R2.echo("Restore wallpaper search : " + wallpaperPath);
				try {
					if (new File(wallpaperPath).exists()){
                                             String deviceModel = android.os.Build.MODEL;
                                             if (deviceModel.contains("A3500") || deviceModel.contains("A3300")){

                                                 Utilities.setNewWallpaper(context,wallpaperPath);
                                                 return;
                                             }

						R2.echo("PNG Wallpaper found !");
					}else{
						/*PK_ID:LBK_READ_WALLPAPER_PNG/JPG AUTH:GECN1 DATE:2012-11-6 S*/
						if(new File(wallpaperPath_JPG).exists()){
							R2.echo("PNG Wallpaper NOT found !");
							wallpaperPath = wallpaperPath_JPG;
							R2.echo("JPG Wallpaper  found !");
						}else{
							R2.echo("JPG Wallpaper NOT  found !");
							Utilities.setDefaultWallpaper(context);
							return;
						}
						/*PK_ID:LBK_READ_WALLPAPER_PNG/JPG AUTH:GECN1 DATE:2012-11-6 E*/
					}
						
					try {
						wpm.forgetLoadedWallpaper();
						wpm.clear();
					} catch (Exception t) {
					}
					FileInputStream fis = null;
					try {
						final int phoneindex = mContext.getResources().getInteger(R.integer.config_machine_type);
						if(phoneindex!=-1)
							SystemClock.sleep(1000);
						fis = new FileInputStream(new File(wallpaperPath));
						wpm.setStream(fis);
 /* RK_ID:bug 10094 AUT:liuyg1@lenovo.com DATE: 2013-3-28 START */
						Intent intent = new Intent();
						String wallpaperName = "wallpaper.jpg";
						intent.setAction("com.lenovo.launcher.action.SET_WALLPAPER");
						intent.setPackage(LAUNCHER_PACKAGE_NAME_PREF);
						intent.putExtra("name", wallpaperName);
						mContext.sendBroadcast(intent);
 /* RK_ID:bug 10094 AUT:liuyg1@lenovo.com DATE: 2013-3-28 START */
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if (fis != null) {
							fis.close();
						}

						wpm = null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				R2.echo("Out wallpaper restore()");
			} catch (Exception e) {
				e.printStackTrace();
			}

			sStateIndicatorOK = true;
		}
	}

	@SuppressWarnings("deprecation")
	public void reLaunch() {

		try {
			ProcessIndicator.getInstance(mContext).setState(
					mContext.getString(R.string.relaunch_launcher));

			/*
			 * RK_ID: RK_FIX_BUG . AUT: zhanggx1 . DATE: 2012-06-19 . PUR: stop
			 * appwidgetHost listening . S
			 */
			LauncherApplication la = (LauncherApplication) mContext
					.getApplicationContext();
			la.mModel.restartLauncher();
			/*
			 * RK_ID: RK_FIX_BUG . AUT: zhanggx1 . DATE: 2012-06-19 . PUR: stop
			 * appwidgetHost listening . E
			 */			
		} catch (Exception e) {
			android.os.Process.killProcess(android.os.Process.myPid());
		}
	}

	class SdcardStateMonitor extends BroadcastReceiver {

		boolean sbCurrentSdcardAvalible = true;

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			R2.echo("Received sdcard action:  " + action);

			if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
				sbCurrentSdcardAvalible = true;
			} else if (Intent.ACTION_MEDIA_CHECKING.equals(action)) {
				sbCurrentSdcardAvalible = false;
			} else if (Intent.ACTION_MEDIA_REMOVED.equals(action)) {
				sbCurrentSdcardAvalible = false;
			} else if (Intent.ACTION_MEDIA_UNMOUNTED.equals(action)) {
				sbCurrentSdcardAvalible = false;
			}
		}
	}

	public String getSensibleDefaultBackupName() {
		List<String> names = collectSceneNames(true);
		String defaultName = mContext.getResources().getString(
				R.string.profile_new_create_default_name);
		R2.echo("names : " + names.toString());
		String targetName = "";
		if (names != null) {
			int i = 1;
			for (; i < Integer.MAX_VALUE; i++) {
				targetName = new StringBuilder().append(defaultName).append(i)
						.toString();
				R2.echo("CurrName : " + targetName);
				if (names.contains(targetName)) {
					continue;
				} else {
					break;
				}
			}
		}

		R2.echo("returned name : " + targetName);
		return targetName;
	}

}
