/*
 * Copyright (C) 2011
 *
 * AUT: liuli liuli0120@use.com.cn DATE: 2011-12-28
 * add activity for profile settings
 */

package com.lenovo.launcher2.settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.SystemProperties;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceClickListener;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.XLauncher;
import com.lenovo.launcher.components.XAllAppFace.XWorkspace;


import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;
import com.lenovo.launcher2.backup.BackupManager;
import com.lenovo.launcher2.backup.BackupManager.State;
import com.lenovo.launcher2.commoninterface.ProfileReloaderSelectionActivity.Constants;
import com.lenovo.launcher2.commonui.LeAlertDialog;
import com.lenovo.launcher2.commonui.LeDialog;
import com.lenovo.launcher2.commonui.LeProcessDialog;
import com.lenovo.launcher2.customizer.ConstantAdapter;
import com.lenovo.launcher2.customizer.Debug;
import com.lenovo.launcher2.customizer.EnableState;
import com.lenovo.launcher2.customizer.GlobalDefine;
import com.lenovo.launcher2.customizer.Reaper;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.ConstantAdapter.OperationState;
import com.lenovo.launcher2.customizer.Utilities;
import com.lenovo.launcher2.weather.widget.utils.Debug.R2;
import com.lenovo.lejingpin.DetailClassicActivity;
import com.lenovo.lejingpin.LEJPConstant;
import com.lenovo.lejingpin.hw.content.data.HwConstant;
import com.lenovo.lejingpin.hw.ui.Util;
import com.lenovo.lejingpin.settings.LejingpingSettingsValues;
import com.lenovo.lejingpin.share.download.AppDownloadUrl;
import com.lenovo.lejingpin.share.download.DownloadConstant;
import com.lenovo.lejingpin.share.download.DownloadHandler;
import com.lenovo.lejingpin.share.download.DownloadInfo;
import com.lenovo.lejingpin.share.download.Downloads;
import com.lenovo.lejingpin.share.download.LDownloadManager;
import com.lenovo.leos.cloud.lcp.common.LcpConstants;
import com.lenovo.leos.cloud.lcp.common.ProgressListener;
import com.lenovo.leos.cloud.lcp.file.entity.Entity;
import com.lenovo.leos.cloud.lcp.file.entity.FileEntity;
import com.lenovo.leos.cloud.lcp.file.impl.profiles.ProfilesBackupInfo;
import com.lenovo.leos.cloud.lcp.file.impl.profiles.ProfilesFileAPIImpl;
import com.lenovo.leos.cloud.lcp.file.impl.profiles.ProfilesFileQueryAPI;
import com.lenovo.leos.cloud.lcp.file.impl.profiles.ProfilesMetaInfo;
import com.lenovo.leos.cloud.lcp.sync.modules.common.Task;
import com.lenovo.lsf.account.PsAuthenServiceL;
//test 
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileSettings extends BaseSettingActivty {
    private static final String TAG = "ProfileSettings";

    private XLauncher mLauncher;
    private ProfileSettings mInstance;
    private boolean mAttached;
    private BackupManager mBackupInstance;

    // progress dialog, show backup/apply profile waiting
    private LeProcessDialog mProgressDlg;
    //private LinearLayout mProgressLayout;

    // current profile
    private String mPrefCurrentProfile;

    private int bigPadding = 0;
    private int smallPadding = 0;

    // backup images directory and name
    //modify for multi-user, begin;
    private static File PREVIEW_DIR;
    //modify for multi-user, end;
    private static final String PREVIEW_IMAGE = "profile_preview";
    private static final String IMAGE_FORMAT = ConstantAdapter.SUFFIX_FOR_PREVIEW_SNAPSHOT;
    private static final int INVALID_INDEX = -1;

    // message
    public static final int DISMISS_BACKUP_PROGRESS = 0;
    protected static final int DISMISS_APPLY_PROGRESS = DISMISS_BACKUP_PROGRESS + 1;
    protected static final int CLOUD_BACKUP_PROGRESS = DISMISS_BACKUP_PROGRESS + 2;
    
    protected static final int APPLY_PROFILE_PROGRESS = 20;//DISMISS_DELETE_PROGRESS + 3;
    protected static final int ADD_PROFILE_PROGRESS = APPLY_PROFILE_PROGRESS + 1;
    
    protected static final int GET_PROFILE_PROGRESS = 40;
    
    // message bundle key
    private static final String KEY_RESULT = "result";
    private static final String KEY_PREVIEW = "preview";
    private static final String KEY_LABEL = "label";
    private static final String KEY_APPLY_FOLLOWING = "apply_following";
    private static final String KEY_RESULT_MSG = "result_msg";
    private static final String KEY_BITMAPS = "bitmaps";
    private static final String KEY_TYPE = "oper_type";

    // backup bitmap
    private Bitmap mPreview;
    private Bitmap mWallpaper;

    private NetworkEnablerChangedReceiver mNetEnablerReceiver;
    
	/*RK_ID: fix bug 168013 . AUT: chengliang . DATE: 2011-07-27 . S*/
    private boolean mDetailUnfold = false;
	/*RK_ID: fix bug 168013 . AUT: chengliang . DATE: 2011-07-27 . E*/
    
     /*RK_ID: PROFILE_SET. AUT: SHENCHAO1. 2012-12-19 S.*/
     private TextView mDialogTitle;
     /*RK_ID: PROFILE_SET. AUT: SHENCHAO1. 2012-12-19 E. */
	
	private ProfileSettings mContext;
    private Preference mLocalRestore;
    private Preference mCloudRestore;
    private Preference mLocalBackup;
    private Preference mCloudBackup;
//    private String mLocalBackupTimestamp;
    private String mCloudBackupTimestamp;
    private static final int MSG_CLOUD_BACKUP = 100;
    private static final int MSG_CLOUD_RESTORE = 101;
    private static final int MSG_CLOUD_BACKUP_PROGRESS = 102;
    private static final int MSG_CLOUD_UPLOAD_PROGRESS = 103;
    private static final int MSG_CLOUD_LAST_BACKUP_TIME = 104;
    private static final int MSG_CLOUD_HAS_NO_PROFILE = 105;
    public static final String DIR_CLOUD_BACKUP_STORE = "//.cloudbackup/";
    private static final String LELAUNCHER_REALM = "lelauncher.lenovo.com";
    private boolean mLbkDownload = false;
    
    private DownloadStateReceiver mDownloadReceiver;
    
    public static final String DEFAULT_PACKNAME = "restore";
    public static final String DEFAULT_VERSIONCODE = "9999";
    private final String LBK_CLOUD_STORE_ACTION = "com.lenovo.lbk.ACTION_RESTORE_COMPLETE";
    
    public static final String LOCAL_BACKUP_DIR = ConstantAdapter.DIR_PARENT_OF_STORAGE_BACKUP_FILE +
    		ConstantAdapter.DIR_TO_STORAGE_BACKUP_FILE + "//.localbackup/";
    
    public static final String CLOUD_BACKUP_DIR = ConstantAdapter.DIR_PARENT_OF_STORAGE_BACKUP_FILE +
    		ConstantAdapter.DIR_TO_STORAGE_BACKUP_FILE + "//.cloudbackup/";
    public static final String SDCARD_BACKUP_DIR = "/storage/sdcard0/";
    public static final String DEFAULT_NAME = "default";

    
    private static final String NO_PROFILE_NAME ="nofile";
    private static final String LOG_SUFFIX =".none";
    
    private boolean isCloud = false;
    private boolean isDownloading;
	/** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle icicle) {
    	//getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(icicle);
        mContext = this;
      //modify for multi-user, begin;
        PREVIEW_DIR = new File(ConstantAdapter.getMyPackageDir(this)
                + ConstantAdapter.PROFILE_SNAPSHOT_STORAGE_PATH);
      //modify for multi-user, end;
        // init launcher
        mLauncher = getLauncher();
        if(!Utilities.isSdcardAvalible() ||
            mLauncher == null){
            Log.w(TAG, "we cannot retrieve launcher instance, so finish it.");
            Toast.makeText(this, R.string.profile_apply_sdcard_unmount, Toast.LENGTH_SHORT).show();
            finish();
        }

        addPreferences();

        //setContentView(R.layout.setup_pererence_layout);
        if(title != null && icon != null){
			getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		    title.setText(R.string.menu_sense_settings);
		    
		}
                
        mAttached = true;
            		
        /** AUT: chengliang 	PUR:fix bug: be compatible with different displays	DATE: 09.18	E*/

        mInstance = this;
        mProgressDlg = new LeProcessDialog(this);
    	
        mNetEnablerReceiver = new NetworkEnablerChangedReceiver();
        IntentFilter netEnablerFilter = new IntentFilter(SettingsValue.ACTION_NETWORK_ENABLER_CHANGED);
        registerReceiver(mNetEnablerReceiver, netEnablerFilter);       
        mDownloadReceiver = new DownloadStateReceiver();
        IntentFilter intentAction = new IntentFilter(LBK_CLOUD_STORE_ACTION);
        //test by dining 2013-07-10
        intentAction.addAction(DownloadConstant.ACTION_DOWNLOAD_STATE_CHANGED);
        intentAction.addAction(DownloadConstant.ACTION_DOWNLOAD_DELETE);
        intentAction.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mDownloadReceiver, intentAction);
        
        bigPadding = this.getResources().getDimensionPixelSize(R.dimen.button_bar_big_padding);
        smallPadding = this.getResources().getDimensionPixelSize(R.dimen.button_bar_small_padding);
        
    	/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-3-26 S*/
        Reaper.processReaper( this, 
        	   Reaper.REAPER_EVENT_CATEGORY_SCENE, 
			   Reaper.REAPER_EVENT_ACTION_SCENE_ENTER,
			   Reaper.REAPER_NO_LABEL_VALUE, 
			   Reaper.REAPER_NO_INT_VALUE );
        /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-3-26 E*/ 
        
		if(!SettingsValue.isRotationEnabled(this)){
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
    }

    private void  setCloudValue(boolean is){
    	isCloud = is;
    }
    private boolean getCloudValue(){
    	return isCloud;
    }
    
    private void setDownloadStatus(boolean is){
    	isDownloading = is;
    }
    
    private boolean getDownloadStatus(){
    	return isDownloading;
    }
    
    
    private void initActionBar(String label) {
    	if(label != null){
    		mDialogTitle.setText(label);
    	}else{
    		Log.e(TAG, "label is null");
    	}
    }

    public void onBackPressed() {
    	
        /**ID: fix bug: 167016  AUT:  chengliang   S*/
		if (mDetailUnfold) {
			mDetailUnfold = false;
		}
        /**ID: fix bug: 167016  AUT:  chengliang   E*/
    	if(mLbkDownload)
    		deleteLbkDownloadTask();
            super.onBackPressed();
    }

    @Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Log.i(TAG,"Enters ProfileSettings' onConfigurationChanged method");
	}

	@Override
    public void onResume() {
        super.onResume();
        
        mAttached = true;       
        mPaused = false;
        
        /**ID: fix bug: 167016  AUT:  chengliang   S*/
        
        if( !mPerformingBackup && mJustPerformedBackup  && !mDetailUnfold){
        	//mPages.setup(this);
        	mJustPerformedBackup = false;
        }
        /**ID: fix bug: 167016  AUT:  chengliang   E*/
    }

    @Override
    public void onStop() {
        /*** fixbug 169901 . AUT: zhaoxy . DATE: 2012-08-30. START***/
        mJustPerformedBackup = false;
        /*** fixbug 169901 . AUT: zhaoxy . DATE: 2012-08-30. END***/
        Log.i(TAG, "onDetachedFromWindow");
        super.onStop();

    }

	private void extracted() {
		mAttached = false;
	}

    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	if( getLauncher() == null)
         	return;
    	 
        extracted();
        
        mPaused = false;

        if (mProgressDlg != null)
            mProgressDlg.dismiss();

        unregisterReceiver(mNetEnablerReceiver);
        unregisterReceiver(mDownloadReceiver);

        recycle(mPreview);
        recycle(mWallpaper);
        clearSelf();

        // clean backup content.
        if (mBackupInstance != null) {
            mBackupInstance.cleanPreviews();
            mBackupInstance = null;
        }

        /*** AUT:zhaoxy . DATE:2012-03-09 . START***/
        //deleteAllThumbnail();
        /*** AUT:zhaoxy . DATE:2012-03-09 . END***/
        
    }

    private void deleteAllThumbnail() {
        if (PREVIEW_DIR != null && PREVIEW_DIR.exists()) {
            File[] lists = PREVIEW_DIR.listFiles();
            if (lists == null) {
                return;
            }
            for (int i = 0; i < lists.length; i++) {
                File f = lists[i];
                // String path = f.getAbsolutePath();
                String ext = f.getName();
                if (ext.contains(IMAGE_FORMAT)) {
                    if (!f.delete())
                        Log.w(TAG, "file cannot be deleted : " + f.getAbsolutePath());
                }
            }// end for
        }// end if
    }

    private void clearSelf() {
        mLauncher = null;
        mInstance = null;

        // views
        mProgressDlg = null;

//        mPages.clearResource();
//        mPages = null;
        mNetEnablerReceiver = null;
        mDownloadReceiver = null;

        mProfileNameWritten = null;
        
        mBackupInstance = null;
        
        System.gc();
    }

    private void getMore() {
        Log.i(TAG, "getMore");
    }

    private final static int REQUEST_CONFIRM_NETWORK_ENABLED = 1;
    /** ID: scene backup & restore. AUT: chengliang . DATE: 2012.04.19 S */
    private final static int REQUEST_CODE_PROFILE_RESTORE_SELECTION = 2;
//    private boolean mProfileSelectionOK = false;  //shenchao delete. date: 2013-03-28.
    protected static final int MSG_NOW_REAL_RESTORE_PROFILE = CLOUD_BACKUP_PROGRESS + 1;
    protected static final int MSG_NOW_RESTORE_CANCELED = MSG_NOW_REAL_RESTORE_PROFILE + 1;
    /** ID: scene backup & restore. AUT: chengliang . DATE: 2012.04.19 E */

    private void showNetworkEnableDialog(final Context context, int title) {
   	 LeAlertDialog leAlertDialog = new LeAlertDialog(mContext, R.style.Theme_LeLauncher_Dialog_Shortcut);
   	leAlertDialog.setLeTitle(R.string.lejingpin_settings_title);
   	leAlertDialog.setLeMessage(mContext.getText(R.string.confirm_network_open));
   	leAlertDialog.setLeNegativeButton(mContext.getText(R.string.cancel_action), new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	dialog.dismiss();
	            Toast.makeText(mContext, R.string.version_update_toast, Toast.LENGTH_SHORT).show();
	        }
	    });
   	
   	leAlertDialog.setLePositiveButton(mContext.getText(R.string.rename_action), new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	Intent intent = new Intent();
	            intent.setClass(context, MoreSettings.class);
	            context.startActivity(intent);
	            dialog.dismiss();
	        }
	    });
   	 leAlertDialog.show();
   }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, final Intent data) {
        if (requestCode == REQUEST_CONFIRM_NETWORK_ENABLED) {
            if (SettingsValue.isNetworkEnabled(this))
                getMore();
            else
                Toast.makeText(this, R.string.version_update_toast, Toast.LENGTH_SHORT).show();
        }
    /** ID: scene backup & restore. AUT: chengliang . DATE: 2012.04.19 S */
        else if(requestCode == REQUEST_CODE_PROFILE_RESTORE_SELECTION){
        	
        		boolean canceled = true;
        		
        		if(data != null){
        			canceled = data.getBooleanExtra("canceled", false);
        		}
        		
        		if( canceled ){
//        			handler.sendMessage(Message.obtain(handler, MSG_NOW_RESTORE_CANCELED));
        			if( mProgressDlg != null){
        				mProgressDlg.dismiss();
        			};
        			return;
        		}
        		
				EnableState enableState = new EnableState();
				try {
					enableState.enableFolder = data.getBooleanExtra(Constants.FOLDERS, true);
					enableState.enablePriorities = data.getBooleanExtra(Constants.ALLAPP_PRIORITY, true);
					enableState.enableQuickEntries = data.getBooleanExtra(Constants.APPLICATIONS, true);
					enableState.enableSettings = data.getBooleanExtra(Constants.SETTINGS, true);
					enableState.enableWallpaper = data.getBooleanExtra(Constants.WALLPAPER, true);
					enableState.enableWidgets = data.getBooleanExtra(Constants.WIDGETS, true);
				} catch (Exception e) {
				}
				if(handler != null){
				    handler.sendMessage(Message.obtain(handler, MSG_NOW_REAL_RESTORE_PROFILE, enableState));
				}
        }
    /** ID: scene backup & restore. AUT: chengliang . DATE: 2012.04.19 E */
    }

    private Dialog setMessageWithEditBox(final boolean applying) {
        final LeDialog addDialog = new LeDialog(this, R.style.Theme_LeLauncher_Dialog_Shortcut);
        addDialog.setLeContentView(R.layout.add_profile_name);
        addDialog.setLeTitle(R.string.add_profile_name_title);
        final TextView message = (TextView) addDialog.findViewById(R.id.add_profile_msg);
        message.setText(R.string.profile_selection_settings);
        
        /**ID: fix bug 164959  AUT: chengliang   DATE: 2012.05.18  S */
        final String timeStamp = String.valueOf((new java.util.Date()).getTime());

        addDialog.setLeNegativeButton(
				getString(R.string.add_profile_cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						if(timeStamp != null) {
//		        			((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
//		        					.hideSoftInputFromWindow(textInput.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		        		}
		                if (applying)
//		                    mBackupFinishedOrCancel = true;
		                    applyProfileNow();
		                addDialog.dismiss();
					}
				});
        addDialog.setLePositiveButton(
				getString(R.string.add_profile_name_save),
				getNameWrittenListener(addDialog, timeStamp, message, addDialog.getLePositiveButton(), addDialog.getLeNegativeButton(), applying));
        
        //setProgressDialogParam(addDialog);

        return addDialog;
    }
    private DialogInterface.OnClickListener mProfileNameWritten;

    private DialogInterface.OnClickListener getNameWrittenListener(final LeDialog addDialog, final String textInput, final TextView message,
            final Button btnSave, final Button btnCancel, final boolean applying) {
//        if (mProfileNameWritten == null) {
            mProfileNameWritten = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // if user does not input anything, get hint string
                	dialog.dismiss();
                    final String profileName = textInput;

                    String regEx="[\"`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]"; 
                    Pattern p = Pattern.compile(regEx); 
                    Matcher m = p.matcher(profileName);                 
                    if( m.find()){
                    	Log.i(TAG, "profile is not match : " + profileName);
                        showProfileNameNotMatch(addDialog, message, textInput, btnSave, applying, btnCancel);
                        return;
                        
                    }
                  //first or last is " " (space)
                    int length = profileName.length();
                    if(length > 0){
                    	if(profileName.substring(0,1).equals(" ")){
                    		Log.i(TAG, "profile first is space: " + profileName);
                            showProfileNameNotMatch(addDialog, message, textInput, btnSave, applying, btnCancel);
                            return;
                    	}
                    	if(profileName.substring(length-1,length).equals(" ")){
                    		Log.i(TAG, "profile last string is space: " + profileName);
                    		showProfileNameNotMatch(addDialog, message, textInput, btnSave, applying, btnCancel);
                            return;
                    	}
                    	
                    }
                    /**ID: fix bug 164959  AUT: chengliang   DATE: 2012.05.18  S */
//                    if (profileName.equals("")){
//                        profileName = getBackupManager().getSensibleDefaultBackupName();
//                    }
                    /**ID: fix bug 164959  AUT: chengliang   DATE: 2012.05.18  E */
                    // check name unique
                    byte backupNow = getBackupManager().checkSensibleName(profileName);

                    Log.i(TAG,"bakup---result:"+ backupNow);
                    switch (backupNow) {
                    case OperationState.SCENE_NAME_UNIQUE:
                    	if(getCloudValue()){
                				PsAuthenServiceL.getStData(mContext, LELAUNCHER_REALM, new PsAuthenServiceL.OnAuthenListener(){
                					@Override
                					public void onFinished(final boolean arg0, String arg1) {	  
                						Log.i(TAG,"---------------------------");
                						Log.i("sss","backup: arg0 is " + arg0 + " and arg1 is " + arg1);
                						new Thread(new Runnable() {
                				            @Override
                				            public void run() {
                				            	if(arg0 == true){
                				            		sendMessage(handler, CLOUD_BACKUP_PROGRESS, profileName);
                				            	}else{
                				            		if(mProgressDlg != null)
                				            			mProgressDlg.dismiss();
                				            	}
                				            }
                						}).start();
                					}			
                				});
                    	}else{
                    		File f = new File(LOCAL_BACKUP_DIR);
                        	File[] files = f.listFiles(ConstantAdapter.FILE_FILTER_BACKUPFILE);
                        	if(files != null){
                        		for (File tf : files) {
                    				tf.delete();
                    			}
                        	}
                        	startBackup(profileName, applying);
                    	}
                    
                        break;

                    case OperationState.FAILED_NO_SDCARD:
                    	Log.i(TAG,"bakup---OperationState.FAILED_NO_SDCARD");
                        /* change  by xingqx for bug 173451 S */
                        LinearLayout.LayoutParams params =
                                     (android.widget.LinearLayout.LayoutParams) btnSave.getLayoutParams();
                        params.setMargins(bigPadding, 0, bigPadding, 0);
                        btnSave.setLayoutParams(params);
                        /* change  by xingqx for bug 173451 E */
                        btnCancel.setVisibility(View.GONE);
                        setSdcardUnmountedMessage(addDialog, message, textInput, btnSave, applying);
                        break;

                    case OperationState.SCENE_NAME_EXISTS:
                        setConfirmMessage(addDialog, message, textInput, btnSave, profileName, applying);
                        break;
                        
                    case OperationState.SCENE_NAME_RESERVED:
                        Toast.makeText(ProfileSettings.this, getString(R.string.profile_name_conflict), Toast.LENGTH_LONG).show();
                        break;
 
                    default:
                        break;
                    }
                } // end on-click
            };
//        }
        return mProfileNameWritten;
    }

    protected void showProfileNameNotMatch(final LeDialog d, final TextView message, final String textInput,
            final Button ok, final boolean applying, final Button cancel) {
        // prompt user illegal name.
//        textInput.setVisibility(View.GONE);
        message.setVisibility(View.VISIBLE);
        message.setText(R.string.profile_name_illegal);

        d.setLePositiveButton(getString(R.string.profile_save_button),
        		new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,
					int whichButton) {
                message.setVisibility(View.GONE);
                d.setLePositiveButton(getString(R.string.profile_save_button),getNameWrittenListener(d, textInput, message, ok, cancel, applying));
			}
		});
    }

    private void setProgressDialogParam(Dialog d) {
        Window window = d.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        window.setGravity(Gravity.CENTER);
    }

    protected void applyProfileNow() {
        // show progress dialog first
//        if (mAttached) {
//            setProgressMessage(R.string.profile_applying_progress_info);
//            mProgressDlg.show();
//        }
        
    	/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-03-04 S*/
        Reaper.processReaper( this, 
        	   Reaper.REAPER_EVENT_CATEGORY_SCENE, 
			   Reaper.REAPER_EVENT_ACTION_SCENE_APPLY,
			   mPrefCurrentProfile, 
			   Reaper.REAPER_NO_INT_VALUE );
        /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-03-04 E*/  

		// begin to restore
    /** ID: scene backup & restore. AUT: chengliang . DATE: 2012.04.19 S */
		// handle default
        boolean isDefaultProfile = getBackupManager().getDefaultProfileName().equals(mPrefCurrentProfile);
        boolean isReservedProfile = getBackupManager().isNameReserved(mPrefCurrentProfile);
		if ( isDefaultProfile || isReservedProfile ) {
			
			String targetFile = isDefaultProfile ? ConstantAdapter.DEFAULT_BACKUP_FILE
					: getBackupManager().getReservedProfileFullPath(mPrefCurrentProfile) ;
			
			R2.echo("Get target file path is : " + targetFile);  
			
			SharedPreferences pref = getSharedPreferences(
					ConstantAdapter.PREF_FIRST_LAUNCH_CHECK_KEY, Activity.MODE_PRIVATE);
			pref.edit().clear().commit();
			pref.edit().putString(ConstantAdapter.PREF_CURR_FACTORY_PROFILE, targetFile).commit();
			
			// Mark
            SharedPreferences.Editor editor = ProfileSettings.this.getSharedPreferences(SettingsValue.PREFS_FILE_NAME,
                    Context.MODE_PRIVATE).edit();
            // we hope not be restore this file.
            editor.putBoolean(ConstantAdapter.EXCLUDED_SETTING_KEY, true).putString(SettingsValue.PREF_PROFILE, mPrefCurrentProfile).commit();
            
            //fix bug 170123 start
            if( isDefaultProfile ){
            	editor.putBoolean(SettingsValue.KEY_IS_DEFAULT_PROFILE, true).commit();
            }else{
            	editor.putBoolean(SettingsValue.KEY_IS_DEFAULT_PROFILE, false).commit();
            }
            //fix bug 170123 END
            // --Mark
  /* AUT: liuyg1@lenovo.com bugfix 172788 DATE  2012.12.01 S */
            Intent intent = new Intent();
            intent.setClass(ProfileSettings.this, XLauncher.class);
        	startActivity(intent);
        	sendBroadcast(new Intent(SettingsValue.ACTION_SCENE_APPLY));
/* AUT: liuyg1@lenovo.com  bugfix 172788 DATE 2012.12.01 E */
			getBackupManager().reLaunch();
		}
		// should get selections
		if( !mPaused && mAttached ){
			showApplyProfileDialog();
		}else{
			Toast.makeText(this, R.string.profile_save_failed, Toast.LENGTH_LONG).show();
		}
    /** ID: scene backup & restore. AUT: chengliang . DATE: 2012.04.19 E */
    }
    
    private LeDialog setResultMessageWithBtn(Bundle args) {
        // dismiss progress dialog first
        if (mProgressDlg != null)
            mProgressDlg.dismiss();
        Intent intent = new Intent(SettingsValue.ACTION_SCENE_APPLY_FINISHED);
        sendBroadcast(intent);

        final int resId = args.getInt(KEY_RESULT_MSG);
        final boolean applying = args.getBoolean(KEY_APPLY_FOLLOWING, false);
        final int type = args.getInt(KEY_TYPE, -1);

        Context c = null;
        if (mAttached) {
            c = mInstance;
        } else {
            c = getLauncher();
        }

        if (c == null) {
            Log.e(TAG, "we cannot get instance ... ");
            return null;
        }
        
      /*RK_USR_LEDIALOG dining 2013-02-22 S*/
        final LeDialog resultDialog = new LeDialog(c, R.style.Theme_LeLauncher_Dialog_Shortcut);
      
        resultDialog.setLeTitle(R.string.menu_sense_settings);
        
        LinearLayout alert = (LinearLayout) this.getLayoutInflater().inflate(R.layout.profile_backup_result, null);
        TextView tv = (TextView) alert.findViewById(R.id.profile_backup_hint);
        tv.setText(resId);
        resultDialog.setLeContentView(alert);
        
        resultDialog.setLePositiveButton(getString(R.string.profile_save_button), 
        		new DialogInterface.OnClickListener() {
        	
        	            @Override
        	            public void onClick(DialogInterface dialog,	int whichButton) {
        	                // if apply sucessfully, we restart launcher
        	                if (resId == R.string.profile_apply_profile_ok || resId == R.string.profile_apply_profile_fail) {
        	                    getBackupManager().reLaunch();
        	                }
        	                if (applying &&
        	                        (type == DISMISS_BACKUP_PROGRESS )){
        	                    applyProfileNow();
        	                }
        	                resultDialog.dismiss();
        	            }
        	        });

        /*** AUT:zhaoxy . DATE:2012-03-09 . START***/
        if (resId == R.string.profile_apply_profile_ok) {
        	resultDialog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface arg0) {
					getBackupManager().reLaunch();
				}
			});
		}
       /*** AUT:zhaoxy . DATE:2012-03-09 . END***/

        //setProgressDialogParam(resultDialog);
        if (resultDialog != null)
            resultDialog.show();
        return resultDialog;
    }

    private void startBackup(final String profile, final boolean applying) {
        /**ID: fix bug: 4285  AUT:  zhaoxy   S*/
        mPerformingBackup = true;
        mJustPerformedBackup = false;
        /**ID: fix bug: 4285  AUT:  zhaoxy   E*/
//        if (d != null)
//            d.dismiss();

        // begin to back up and show dialog
        setProgressMessage(R.string.profile_backuping_progress_info);
        sendBroadcast(new Intent(SettingsValue.ACTION_SCENE_BACKUP));

        Bundle b = new Bundle();
        b.putString(KEY_LABEL, profile);
        b.putBoolean(KEY_APPLY_FOLLOWING, applying);

        /*** AUT:zhaoxy . DATE:2012-03-03 . START***/
        deleteAllThumbnail();
        /*if (mPreview == null) {*/
		/*** AUT:zhaoxy . DATE:2012-03-03 . END***/
            // check storage first
            boolean isStorageOk = false;
            //R2
//            Launcher launcher = getLauncher();
            //2R
            if (mLauncher == null) {
                Log.w(TAG, "cannot backup, because we cannot get launcher instance ... ");
                /**ID: fix bug: 4285  AUT:  zhaoxy   S*/
                mPerformingBackup = false;
                mJustPerformedBackup = true;
                /**ID: fix bug: 4285  AUT:  zhaoxy   E*/
                return;
            } else {
                isStorageOk = mLauncher.getProfileEnabled();
                if (!isStorageOk) {
                    b.putInt(KEY_RESULT_MSG, R.string.profile_apply_memory_low);
                    setResultMessageWithBtn(b);
                    /**ID: fix bug: 4285  AUT:  zhaoxy   S*/
                    mPerformingBackup = false;
                    mJustPerformedBackup = true;
                    /**ID: fix bug: 4285  AUT:  zhaoxy   E*/
                    return;
                } else {
                    Log.i(TAG, "getAvailMemory() = " + getAvailMemory());
                    Log.i(TAG, "getTotalMemory() = " + getTotalMemory());
                    float f = getAvailMemory() * 1.0f / getTotalMemory();
                    Log.i(TAG, "% = " + f);
                }
            }

            SaveCurrentThumbnail task = new SaveCurrentThumbnail();
            task.execute(b);
            /*** AUT:zhaoxy . DATE:2012-03-03 . START***/
        /*} else {
            startBackupProcess(b);
        }*/
            /*** AUT:zhaoxy . DATE:2012-03-03 . END***/
    }

    // retrieve android current memory info
    private long getAvailMemory() {

        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);

        return mi.availMem;
    }

    private long getTotalMemory() {
        String str1 = "/proc/meminfo";// memory info file
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;

        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();// first line

            arrayOfString = str2 == null ? new String[]{} : str2.split("\\s+");
            for (String num : arrayOfString) {
                Log.i(str2, num + "\t");
            }

            // KB, change it to Byte
            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024L;
            localBufferedReader.close();

        } catch (IOException e) {
        }
        return initial_memory;
    }
    
    /**ID: fix bug: 167812  AUT:  chengliang   S*/
    private boolean mPerformingBackup = false;
    private boolean mJustPerformedBackup = false;
    /**ID: fix bug: 167812  AUT:  chengliang   E*/

    public void startBackupProcess(final Bundle b) {
    	//test by dining 2013-07-09 
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                /**ID: fix bug: 167812  AUT:  chengliang   S*/
                /**ID: fix bug: 4285  AUT:  zhaoxy   S*/
//            	mPerformingBackup = true;
//            	mJustPerformedBackup = false;
                /**ID: fix bug: 4285  AUT:  zhaoxy   E*/
                /**ID: fix bug: 167812  AUT:  chengliang   E*/
            	boolean cloud = getCloudValue();
            	byte result;
            	if(cloud == false){
            		result = getBackupManager().backup(b.getString(KEY_LABEL),LOCAL_BACKUP_DIR);// OperationState.SUCCESS;//
            	}else{
            		result = getBackupManager().backup(b.getString(KEY_LABEL),CLOUD_BACKUP_DIR);
            	}

                /**ID: fix bug: 167812  AUT:  chengliang   S*/
                mPerformingBackup = false;
                mJustPerformedBackup = true;
                /**ID: fix bug: 167812  AUT:  chengliang   E*/
                
                //R2
                int count = 0;
                if(mPaused){
                	while(mPaused){
                		try {
							Thread.sleep(100L);
						} catch (InterruptedException e) {
						}
						count ++;
                	}
                }
                //2R
                if(cloud){
                    if (handler != null) {
                    	if(mProgressDlg != null)
                    		mProgressDlg.dismiss();
                    	
                       	Intent intent1 = new Intent(SettingsValue.ACTION_SCENE_APPLY_FINISHED);
                    	sendBroadcast(intent1);
                    	
                        handler.removeMessages(MSG_CLOUD_BACKUP_PROGRESS);
                        // send message to dimiss progress dialog
                        Message msg = handler.obtainMessage();
                        msg.what = MSG_CLOUD_BACKUP_PROGRESS;
                        handler.sendMessage(msg);
                    }
                }else{
	                if (handler != null) {
	                    handler.removeMessages(DISMISS_BACKUP_PROGRESS);
	                    // send message to dimiss progress dialog
	                    Message msg = handler.obtainMessage();
	                    msg.what = DISMISS_BACKUP_PROGRESS;
	                    b.putBoolean("hasPaused", count > 0);
	                    b.putByte(KEY_RESULT, result);
	                    if (mPreview != null)
	                        b.putParcelable(KEY_PREVIEW, mPreview);
	
	                    msg.obj = b;
	                    handler.sendMessage(msg);
	                }
                }
            }
        }).start();

    }
    
    //R2
    private boolean mPaused = false;
    //2R
    
    /**ID: fix bug: 167812  AUT:  chengliang   S*/
    @Override
	protected void onPause() {
		
		
		mPaused = true;
		extracted();
		
		if( mPerformingBackup ){
			mJustPerformedBackup = true;
		}
		super.onPause();
	}
    /**ID: fix bug: 167812  AUT:  chengliang   E*/
	// set dialog content view, and display resId on the Text View
    private void setProgressMessage(int resId) {
        //TextView tv = (TextView) mProgressLayout.findViewById(R.id.progress_msg);
        //tv.setText(resId);
        
        if (mAttached && mProgressDlg != null){
        	mProgressDlg.setLeMessage(resId);
            mProgressDlg.show();  
        }
    }

    protected void setSdcardUnmountedMessage(final Dialog d, TextView message, String textInput, Button ok,
            final boolean applying) {
//        textInput.setVisibility(View.GONE);
        message.setVisibility(View.VISIBLE);

        message.setText(R.string.profile_apply_sdcard_unmount);
        ok.setText(R.string.profile_save_button);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (applying)
//                    mBackupFinishedOrCancel = true;
                    applyProfileNow();
                d.dismiss();
            } // end on-click
        });
    }

    protected void setConfirmMessage(final Dialog dialog, TextView tv, final String et, Button ok,
            String profileName, final boolean applying) {
        tv.setVisibility(View.VISIBLE);
//        et.setVisibility(View.GONE);

        tv.setText(getString(R.string.profile_exist_override_msg, profileName));
        ok.setText(R.string.profile_save_button);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String profileName = et;//et.getEditableText().toString();
                if (profileName.equals("")){
                    /**ID: fix bug 164959  AUT: chengliang   DATE: 2012.05.18  S */
                    profileName = getBackupManager().getSensibleDefaultBackupName();
                    /**ID: fix bug 164959  AUT: chengliang   DATE: 2012.05.18  E */
                }

                if(dialog != null){
                	dialog.dismiss();
                }
                startBackup(profileName, applying);
            } // end on-click
        });
    }

    private class SaveCurrentThumbnail extends AsyncTask<Bundle, Void, Void> {
        private Bundle mBundle;
        @Override
        protected void onPostExecute(Void result) {
            startBackupProcess(mBundle);
        }

        @Override
        protected Void doInBackground(Bundle... arg0) {
            mBundle = arg0[0];
            //test by dining 2013-07-09 no preview bitmap
//            ArrayList<Bitmap> bitmaps = mBundle.getParcelableArrayList(KEY_BITMAPS);
//            mPreview = saveCurrentPageThumb(bitmaps);
            return null;
        }
    }

    private Bitmap saveCurrentPageThumb(ArrayList<Bitmap> bitmaps) {
        Log.i(TAG, "saveCurrentPageThumb = " + bitmaps.size());
        //R2
//        Launcher launcher = getLauncher();
        //2R
        if (mLauncher == null) {
            Log.w(TAG, "cannot backup, because we cannot get launcher instance ... ");
            return null;
        }

        // save the draglayer thumb nail first
        Resources res = getResources();
        WallpaperManager wm = WallpaperManager.getInstance(this);

        int dragWidth = (int)mLauncher.getWorkspace().getWidth();
        double width = 300.0;
        float scale = (float) (width / dragWidth);

        Bitmap preview = null;
        if (!getWallpaper(wm, scale)) {
            Log.i(TAG, "cannot retrieve wallpaper !!! ");
        } else {
            Bitmap layerBitmap = bitmaps.get(0);
            Drawable[] array = new Drawable[3];
            array[0] = new BitmapDrawable(res, mWallpaper);
            array[1] = new BitmapDrawable(res, layerBitmap);
            array[2] = new BitmapDrawable(res, bitmaps.get(1));

            LayerDrawable ld = new LayerDrawable(array);
            ld.setLayerInset(0, 0, 0, 0, 0);
            ld.setLayerInset(1, 0, 0, 0, 0);
            preview = drawableToBitmap(ld, ld.getIntrinsicWidth(), ld.getIntrinsicHeight());

            savePreviewBitmap(preview, INVALID_INDEX);
        }

        return preview;
    }

    // retrieve wall paper
    private boolean getWallpaper(WallpaperManager wm, float scale) {

        if (mWallpaper != null && !mWallpaper.isRecycled()) {
            return true;
        }
        Bitmap wallpaperBmp = wm.getBitmap();

        // check the drawable is avaid
        if (wallpaperBmp == null || wallpaperBmp.isRecycled()) {
            Log.w(TAG, "the bitmap of wallpaper we got is recycle or null");
            return false;
        }

        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top;

        // for navigation bar height
        Resources res = this.getResources();//launcher.getResources();
        boolean hasNavigationBar = res.getBoolean(com.android.internal.R.bool.config_showNavigationBar);

        // if we cannot retrieve navigation configuration,
        // maybe there is an error about framework.jar. so change a way to try.
        if (!hasNavigationBar) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$bool");
                Field f = c.getField("config_showNavigationBar");
                int resid = f.getInt(null);

                hasNavigationBar = this.getResources().getBoolean(resid);
                Log.v(TAG, " res = " + hasNavigationBar);
            } catch (Exception e) {
            }
        }

        // Allow a system property to override this. Used by the emulator.
        // See also hasNavigationBar().
        String navBarOverride = SystemProperties.get("qemu.hw.mainkeys");
        if (!"".equals(navBarOverride)) {
            if (navBarOverride.equals("1"))
                hasNavigationBar = false;
            else if (navBarOverride.equals("0"))
                hasNavigationBar = true;
        }
        int navigationBarHeight = hasNavigationBar ? res
                .getDimensionPixelSize(com.android.internal.R.dimen.navigation_bar_height) : 0;

        int height = wallpaperBmp.getHeight() - statusBarHeight - navigationBarHeight;

        int width = wallpaperBmp.getWidth();
//        width = width > w ? w : width;

        Matrix matrix = new Matrix();
        matrix.postScale(300.0f / width, scale);
        
        /** fix bug: Bug 166400   AUT: chengliang  DATE: 06.26   S*/
        try {
			XWorkspace workspace = mLauncher.getWorkspace();
			int cellWidth = (int)workspace.getWidth();
			
	        SharedPreferences prf = PreferenceManager.getDefaultSharedPreferences(mLauncher);
	        float beginX = prf.getFloat( "wallpaper_offset_x", 0);
			
//			int beginX = (int) workspace.getWorkspaceWallpaperOffsetX();

			mWallpaper = Bitmap.createBitmap(wallpaperBmp, (int) beginX, 0, 
					cellWidth - 1, height, matrix, true);
		} catch (Exception e) {
			mWallpaper = Bitmap.createBitmap(wallpaperBmp, 0, 0, 480, height, matrix, true);
		}
        /** fix bug: Bug 166400   AUT: chengliang  DATE: 06.26   E*/

        return true;
    }

    /**
     * Draw the view into a bitmap.
     * @param width
     */
    private Bitmap getViewBitmap(View v, float scale) {
        v.clearFocus();
        v.setPressed(false);

        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);

        // Reset the drawing cache background color to fully transparent
        // for the duration of this operation
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);
        float alpha = v.getAlpha();
        v.setAlpha(1.0f);

        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        boolean rebuild = false;
        if (cacheBitmap == null) {
          /*** MODIFY BY:zhaoxy . DATE:2012-03-09 . START***/
            //return null;
          try {
				Drawable temp = getResources().getDrawable(R.drawable.comunavailable);
				cacheBitmap = drawableToBitmap(temp, temp.getIntrinsicWidth(), temp.getIntrinsicHeight());
				double width = getResources().getDimension(R.dimen.profile_setting_thumbnails_width);
				scale = (float) (width / cacheBitmap.getWidth());
				rebuild = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
          /*** MODIFY BY:zhaoxy . DATE:2012-03-09 . END***/
        }

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        /*** MODIFY BY:zhaoxy . DATE:2012-03-09 . START***/
        Bitmap bitmap = null;
		try {
			if (cacheBitmap != null) {
				bitmap = Bitmap.createBitmap(cacheBitmap, 0, 0, cacheBitmap.getWidth(), cacheBitmap.getHeight(), matrix,
				        true);
			}			
		} catch (Throwable e) {
			bitmap = null;
			e.printStackTrace();
		}
        /*** MODIFY BY:zhaoxy . DATE:2012-03-09 . END***/

        // Restore the view
        v.destroyDrawingCache();
        v.setAlpha(alpha);
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);

        if (rebuild) {
        	recycle(cacheBitmap);
		}
        /*** MODIFY BY:zhaoxy . DATE:2012-03-09 . START***/
        if (bitmap == null) {
			try {
				bitmap = drawableToBitmap(getResources().getDrawable(R.drawable.comunavailable), cacheBitmap.getWidth(), cacheBitmap.getHeight());
			} catch (Exception e) {
				e.printStackTrace();
			}
		 }
       /*** MODIFY BY:zhaoxy . DATE:2012-03-09 . END***/
        return bitmap;
    }

    private Bitmap getAllAppsThumb(View v, float scale) {
        v.setDrawingCacheEnabled(true);
        Bitmap cacheBitmap = v.getDrawingCache();

        if (cacheBitmap == null) {
            return null;
        }

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap, 0, 0, cacheBitmap.getWidth(), cacheBitmap.getHeight(), matrix,
                true);

        v.setDrawingCacheEnabled(false);
        recycle(cacheBitmap);
        return bitmap;
    }

    private Bitmap drawableToBitmap(Drawable drawable, int w, int h) {
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // create bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // create canvas of bitmap
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // draw
        drawable.draw(canvas);
        return bitmap;
    }

    /*
     * save bitmap given to /data/ disk
     */
    private boolean savePreviewBitmap(Bitmap bitmap, int index) {
        Log.i(TAG, "savePreviewBitmap" + bitmap + "    and index = " + index);
        if (bitmap == null)
            return false;

        try {
            if (!PREVIEW_DIR.exists()) {
                PREVIEW_DIR.mkdirs();
            }
            String fileName = PREVIEW_IMAGE;
            if (index != INVALID_INDEX)
                fileName += String.valueOf(index) + IMAGE_FORMAT;
            else
                fileName = ConstantAdapter.PROFILE_SNAPSHOT_PREVIEW_NAME;
            File f = new File(PREVIEW_DIR, fileName);
            ParcelFileDescriptor fd = ParcelFileDescriptor.open(f, ParcelFileDescriptor.MODE_CREATE
                    | ParcelFileDescriptor.MODE_READ_WRITE);
            if (fd == null) {
                return false; // create or open fail
            }
            FileOutputStream fos = null;
            try {
                fos = new ParcelFileDescriptor.AutoCloseOutputStream(fd);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
            } finally {
                if (fos != null) {
                    fos.close();
                }
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void recycle(Bitmap b) {
        if (b != null && !b.isRecycled()) {
            b.recycle();
//            b = null;
        }

        System.gc();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle resBundle = new Bundle();
            int resInt = 0;

            switch (msg.what) {
            case MSG_CLOUD_LAST_BACKUP_TIME:
            	Log.i("wang","MSG_CLOUD_LAST_BACKUP_TIME");
            	if(mCloudBackupTimestamp == null){
            		mCloudBackup.setSummary(R.string.no_profile_num);
            		renameTxtFile(getTxtFileName(CLOUD_BACKUP_DIR), NO_PROFILE_NAME);
            		Log.i("wang","11111111");
            	}else{
            		Log.i("wang","11111112");
            		if(mCloudBackupTimestamp != getTxtFileName(CLOUD_BACKUP_DIR)){
	            		mCloudBackup.setSummary(mCloudBackupTimestamp);			
	            		Timestamp stamp = Timestamp.valueOf(mCloudBackupTimestamp);
	            		long time = stamp.getTime();
	            		Log.i("wang","mCloudBackupTimestamp is " + mCloudBackupTimestamp);
						renameTxtFile(getTxtFileName(CLOUD_BACKUP_DIR),Long.toString(time));
            		}
					ProfileSettings.this.sendMessage(handler, MSG_NOW_REAL_RESTORE_PROFILE, new EnableState());
            	}
//            	showApplyProfileDialog();
            	break;
            case MSG_CLOUD_UPLOAD_PROGRESS:
            	setProgressMessage(R.string.cloud_backup);
            	break;
            case MSG_CLOUD_BACKUP_PROGRESS:
            	if (mProgressDlg != null)
            		mProgressDlg.dismiss();
//            	Intent intent1 = new Intent(SettingsValue.ACTION_SCENE_APPLY_FINISHED);
//            	sendBroadcast(intent1);
//            	startCloudBackupProcess();
            	uploadProfile();
            	break;
            case MSG_CLOUD_HAS_NO_PROFILE:
            	if (mProgressDlg != null)
            		mProgressDlg.dismiss();
            	if(!isFinishing()){
            		showNoProfileDialog();
            	}
            	break;
            case MSG_CLOUD_BACKUP:
            	if (mProgressDlg != null)
            		mProgressDlg.dismiss();
    			Bundle bundle = (Bundle)msg.obj;
//    			dismissLoadProgressDialog();
    			int result1 = bundle.getInt(Task.KEY_RESULT);
    			final StringBuilder taskMessageBuilder = new StringBuilder();
    			if(result1 == Task.RESULT_OK){
    				int add = bundle.getInt(Task.KEY_RESULT_ADD);
    				int update = bundle.getInt(Task.KEY_RESULT_UPDATE);
    				int delete = bundle.getInt(Task.KEY_RESULT_DELETE);
    				taskMessageBuilder.append("任务成功！新增:" + add + ",修改：" + update + ",删除：" + delete);
    				
					mCloudBackupTimestamp = getFileName(CLOUD_BACKUP_DIR);
					mCloudBackup.setSummary(getSummary(mCloudBackupTimestamp));
					
					renameTxtFile(getTxtFileName(CLOUD_BACKUP_DIR),mCloudBackupTimestamp);
//					saveCloudbackupTimestamp(mCloudBackupTimestamp);
					
    				Toast.makeText(mContext, R.string.boot_backup_desktop_success,Toast.LENGTH_LONG).show();
    			}else if(result1 == Task.RESULT_CANCEL){
                    if (mProgressDlg != null)
                        mProgressDlg.dismiss();
    				taskMessageBuilder.append("Cancel！,result:"+result1);
    				Toast.makeText(mContext, R.string.boot_backup_desktop_fail,Toast.LENGTH_LONG).show();
//    				Toast.makeText(mContext, R.string.boot_backup_desktop_fail,Toast.LENGTH_LONG).show();
    			}else {
                    if (mProgressDlg != null)
                        mProgressDlg.dismiss();
    				taskMessageBuilder.append("任务出错！,result:"+result1);
    				Toast.makeText(mContext, R.string.boot_backup_desktop_fail,Toast.LENGTH_SHORT).show();
    			}
    			break;
            case MSG_CLOUD_RESTORE:
//    			dismissLoadProgressDialog();
            	  if (mProgressDlg != null)
                      mProgressDlg.dismiss();
            	setProgressMessage(R.string.download_already_inlist_running);
    			break;
            case ADD_PROFILE_PROGRESS:
            	 mDetailUnfold = false;
            	setMessageWithEditBox(false).show();
            	break;
            case APPLY_PROFILE_PROGRESS:
            	String fileName = getFileName(LOCAL_BACKUP_DIR);
            	if(fileName == null){
            		Toast.makeText(mContext, R.string.no_profile_num, Toast.LENGTH_SHORT).show();
            		break;
            	}
            	mPrefCurrentProfile = LOCAL_BACKUP_DIR + fileName + ".lbk";//getLocalbackupTimestamp();//msg.obj.toString();
            	Log.i(TAG,"mPrefCurrentProfile is ---" + mPrefCurrentProfile);
            	
            	File localFile = new File(mPrefCurrentProfile);
	        	
            	if(localFile.exists()){
	            	mDetailUnfold = true;
	            	applyProfileNow();
            	}else{
            		Toast.makeText(mContext, R.string.no_profile_num, Toast.LENGTH_SHORT).show();
            	}
            	break;
            case DISMISS_BACKUP_PROGRESS:
            	sendBroadcast(new Intent(SettingsValue.ACTION_SCENE_BACKUP_FINISHED));
                resBundle = (Bundle) msg.obj;
                resBundle.putInt(KEY_TYPE, DISMISS_BACKUP_PROGRESS);
                byte result = resBundle.getByte(KEY_RESULT);
                resInt = R.string.profile_save_failed;
                
                switch (result) {
                case OperationState.SUCCESS:
                	/*RK_ID: SET_SUMMARY. AUT: SHENCHAO1. 2012-06-28 S.*/
                	String summaryname = getFileName(LOCAL_BACKUP_DIR);
                	if(summaryname != null){
                	    String summary = getSummary(summaryname);
                	    Log.i(TAG,"DISMISS_BACKUP_PROGRESS----summary:"+ summary);
                	    if(summary != null){
                		    mLocalBackup.setSummary(summary);
                	    }
                	}else{
                		mLocalBackup.setSummary(R.string.no_profile_num);
                	}       	        
    	        	/*RK_ID: SET_SUMMARY. AUT: SHENCHAO1. 2012-06-28 E.*/
                    resInt = R.string.profile_save_profile_ok;
                    /*** AUT:zhaoxy . DATE:2012-03-09 . START***/
                    final boolean applying = resBundle.getBoolean(KEY_APPLY_FOLLOWING, false);
                   
                    if (applying) {
                    	applyProfileNow();
                    	return;
					    }
                    /*** AUT:zhaoxy . DATE:2012-03-09 . END***/
                    break;

                case OperationState.FAILED_NO_SDCARD:
                    resInt = R.string.profile_apply_sdcard_unmount;
                    break;

                default:
                    break;
                }
                resBundle.putInt(KEY_RESULT_MSG, resInt);
                setResultMessageWithBtn(resBundle);
                break;

            case DISMISS_APPLY_PROGRESS:
                byte apply = (Byte) msg.obj;
                resBundle.putInt(KEY_TYPE, DISMISS_APPLY_PROGRESS);
                resInt = R.string.profile_apply_profile_fail;
                switch (apply) {
                case OperationState.SUCCESS:
                    resInt = R.string.profile_apply_profile_ok;
                    // save profile to prefs
                    SharedPreferences.Editor editor = ProfileSettings.this.getSharedPreferences(SettingsValue.PREFS_FILE_NAME,//launcher.getSharedPreferences(PREFS_FILE_NAME,
                            Context.MODE_PRIVATE).edit();
                    // we hope not be restore this file.
                    editor.putBoolean(SettingsValue.KEY_IS_DEFAULT_PROFILE, false);
                    editor.putBoolean(ConstantAdapter.EXCLUDED_SETTING_KEY, true);
                    editor.putString(SettingsValue.PREF_PROFILE, mPrefCurrentProfile);
                    try {
                        editor.apply();
                    } catch (AbstractMethodError unused) {
                        editor.commit();
                    }
                    break;
                case OperationState.PAD_APPALY_PHONE_SCENE:
                    resInt = R.string.failed_to_unmix;
                    break;
                case OperationState.PHONE_APPALY_PAD_SCENE:
                    resInt = R.string.failed_to_unmix;
                    break; 
                case OperationState.FAILED_NO_SDCARD:
                    resInt = R.string.profile_apply_sdcard_unmount;
                    break;

                default:
                    break;
                }

                resBundle.putBoolean(KEY_APPLY_FOLLOWING, true);
                resBundle.putInt(KEY_RESULT_MSG, resInt);
                setResultMessageWithBtn(resBundle);
                break;

            case CLOUD_BACKUP_PROGRESS:           	          	
   				File f = new File(CLOUD_BACKUP_DIR);
				File[] files = f.listFiles(ConstantAdapter.FILE_FILTER_BACKUPFILE);
				if(files != null){
					for (File tf : files) {
						if(tf.getName().contains(".lbk"))
							tf.delete();
					}
				}
           		if(mProgressDlg != null)
        			mProgressDlg.dismiss();
				setProgressMessage(R.string.profile_backuping_progress_info);
				String backupProfile = (String) msg.obj;
				startBackup(backupProfile,  false);
                break;
                
    /** ID: scene backup & restore. AUT: chengliang . DATE: 2012.04.19 S */
            case MSG_NOW_REAL_RESTORE_PROFILE:
                /*** MODIFY BY:zhaoxy . DATE:2012-03-08 . START***/
            	  if (mProgressDlg != null)
                      mProgressDlg.dismiss();
            	if(getCloudValue()){
            		mPrefCurrentProfile = Environment.getExternalStorageDirectory().getPath() + "/default.lbk";

            	}
            	final EnableState enableState = (EnableState)(msg.obj);  
            	
            	if( enableState != null && !enableState.enableFolder && !enableState.enablePriorities 
            			&& !enableState.enableQuickEntries && !enableState.enableSettings 
            			&& !enableState.enableWallpaper && !enableState.enableWidgets ){
            		
                    if (mProgressDlg != null)
                        mProgressDlg.dismiss();
                    
                    Toast.makeText(mInstance, mInstance.getString(R.string.no_need_to_restore), Toast.LENGTH_LONG).show();
                    
            		break;
            	}
            	
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                    	
//                    	getSharedPreferences(SettingsValue.PREFS_FILE_NAME, Activity.MODE_PRIVATE).edit().putBoolean(SettingsValue.KEY_IS_DEFAULT_PROFILE, false).commit();
                    	
                        byte result = getBackupManager().restore(mPrefCurrentProfile, State.RESTORE_FACTORY, enableState);

                        Log.i("shen","in resotore mPrefCurrentProfile " + mPrefCurrentProfile);
                        if (handler != null) {
                            handler.removeMessages(DISMISS_APPLY_PROGRESS);
                            Message msg = handler.obtainMessage();
                            msg.what = DISMISS_APPLY_PROGRESS;
                            msg.obj = result;
                            handler.sendMessage(msg);
                        }
                    }
                }, "ApplyProfile").start();
            	/*** MODIFY BY:zhaoxy . DATE:2012-03-08 . END***/
                
                Intent intent = new Intent();
                intent.setClass(ProfileSettings.this, XLauncher.class);
            	startActivity(intent);
            	sendBroadcast(new Intent(SettingsValue.ACTION_SCENE_APPLY));
            	
            	break;
    /** ID: scene backup & restore. AUT: chengliang . DATE: 2012.04.19 E */
            	
            case MSG_NOW_RESTORE_CANCELED:
            	break;
                
            case GET_PROFILE_PROGRESS:
            	//set the number textview
//            	resetNumberText();
            	break;
            default:
                break;
            }
        }
    };


    public void reSetup(String label, boolean reload) {
        if (!mPerformingBackup) {
//        mStyle = style;
        initActionBar(label);
        }
    }

    public boolean getAttached() {
        return mAttached;
    }

    public BackupManager getBackupManager() {
        if (mBackupInstance == null) {
            mBackupInstance = BackupManager.getInstance( getLauncher() );//Launcher.getInstance());
        }
        return mBackupInstance;
    }

    public void setBackupEnabled(boolean enable) {
//        mButtonRight.setEnabled(enable);
    }
    
    /*RK_ID: RK_NETWORK_SETTINGS . AUT: zhanggx1 . DATE: 2011-02-01 . S*/
	public class NetworkEnablerChangedReceiver extends BroadcastReceiver {
    	
    	@Override
    	public void onReceive(Context context, Intent intent) {
    		// TODO Auto-generated method stub
    		final String action = intent.getAction();
    		try{
    			if (SettingsValue.ACTION_NETWORK_ENABLER_CHANGED.equals(action)) {
            	boolean networkEnabler = intent.getBooleanExtra(SettingsValue.EXTRA_NETWORK_ENABLED, false);
            	boolean fromWeather = intent.getBooleanExtra(SettingsValue.EXTRA_ISFROM_WEATHER, false);
            	if (fromWeather) {
            		return;
            	}
    			}
            }catch(Exception e ){
            	e.printStackTrace();
            }
    	}
	}
	/*RK_ID: RK_NETWORK_SETTINGS . AUT: zhanggx1 . DATE: 2011-02-01 . E*/
	
	/*RK_ID: MEM OPT . AUT: chengliang . DATE: 2011-07-23 . S*/
	XLauncher getLauncher(){
        try {
			LauncherApplication app = (LauncherApplication) getApplicationContext();
			return app.getModel().getCallBack().getLauncherInstance();
		} catch (Exception e) {
			// can not retrieve instance , app may gone
			return null;
		}
	}
	/*RK_ID: MEM OPT . AUT: chengliang . DATE: 2011-07-23 . E*/
	/*RK_ID: FOR_BACKKEY_PRESSED. AUT: shenchao1. date: 2013-04-08. S*/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		
		return super.onKeyDown(keyCode, event);
		
	}
	
	/* Added by shenchao1
	 *   for user to decide to apply the chosen profile or not
	 */
	 private void showApplyProfileDialog(){	        
		 final LeDialog applyDialog = new LeDialog(this,R.style.Theme_LeLauncher_Dialog_Shortcut);
		 applyDialog.setLeTitle(R.string.apply_profile);
	        LinearLayout alert = (LinearLayout) this.getLayoutInflater().inflate(R.layout.profile_backup_result, null);
	        TextView tv = (TextView) alert.findViewById(R.id.profile_backup_hint);
	        tv.setText(R.string.restore_theme_settings_title);
	        applyDialog.setLeContentView(alert);
		 applyDialog.setLeNegativeButton(this.getText(android.R.string.cancel), new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				if( mProgressDlg != null){
  				mProgressDlg.dismiss();
  			};
  			applyDialog.dismiss();
			}		 
		 });
		 applyDialog.setLePositiveButton(this.getText(android.R.string.ok), new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				EnableState enableState = new EnableState();
				enableState.enableFolder = true;
				enableState.enablePriorities =  true;
				enableState.enableQuickEntries =  true;
				enableState.enableSettings =  true;
				enableState.enableWallpaper = true;
				enableState.enableWidgets = true;

				if(handler != null){
				    handler.sendMessage(Message.obtain(handler, MSG_NOW_REAL_RESTORE_PROFILE, enableState));
				}
				
			}			 
		 });
		 applyDialog.show();
	 }
	 
	 private void cloudRestoreDialog(){	        
		 final LeDialog applyDialog = new LeDialog(this,R.style.Theme_LeLauncher_Dialog_Shortcut);
		 applyDialog.setLeTitle(R.string.cloud_restore);
	        LinearLayout alert = (LinearLayout) this.getLayoutInflater().inflate(R.layout.profile_backup_result, null);
	        TextView tv = (TextView) alert.findViewById(R.id.profile_backup_hint);
	        tv.setText(R.string.restore_theme_settings_title);
	        applyDialog.setLeContentView(alert);
		 applyDialog.setLeNegativeButton(this.getText(android.R.string.cancel), new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				if( mProgressDlg != null){
  				mProgressDlg.dismiss();
  			};
  			applyDialog.dismiss();
			}		 
		 });
		 applyDialog.setLePositiveButton(this.getText(android.R.string.ok), new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {

				if(mProgressDlg != null)
					setProgressMessage(R.string.accessing_server);
				PsAuthenServiceL.getStData(mContext, LELAUNCHER_REALM, new PsAuthenServiceL.OnAuthenListener(){
					@Override
					public void onFinished(final boolean arg0, final String arg1) {	
						new Thread(new Runnable() {
				            @Override
				            public void run() {				            	
				            	ProfilesFileQueryAPI queryAPI = ProfilesFileQueryAPI.getInstance(mContext);
				            	ProfilesBackupInfo backup = null;
								if(arg0 == true){
								if(SettingsValue.getCurrentMachineType(mContext) == -1){
									backup = queryAPI.queryLatestProfile(LcpConstants.DESKTOP_APP_CONFIG_KEY,LcpConstants.DESKTOP_APP_CATEGORY_PHONE);
									Log.i(TAG,"download phone's backup file");
								}else{
									backup = queryAPI.queryLatestProfile(LcpConstants.DESKTOP_APP_CONFIG_KEY,LcpConstants.DESKTOP_APP_CATEGORY_PAD);
									Log.i(TAG,"download pad's backup file");
								}
								mCloudBackupTimestamp = null;
								Log.i("sss","restore : arg0 is " + arg0 + " and arg1 is " + arg1);

									Log.i("sss"," is true");
								if(backup == null ){
									sendMessage(handler, MSG_CLOUD_HAS_NO_PROFILE, null);
									return;
								}
								setDownloadStatus(true);
								Log.d("wang", "----time---- is " + backup.getTime());
								mCloudBackupTimestamp = backup.getTime();
								String fullpathfile = SDCARD_BACKUP_DIR + DEFAULT_NAME + ".lbk";
								File backfile = new File(fullpathfile);
								if(backup.getTime().equals(getLastRetoreTime("restoretime")) && backfile.exists()){
									Log.i(TAG,"time is the same with last time");
									EnableState enableState = new EnableState();
									sendMessage(handler, MSG_NOW_REAL_RESTORE_PROFILE, enableState);							
								}else{
									if(!isFinishing()){
										setRestoreTime("temptime",backup.getTime());
										sendMessage(handler,MSG_CLOUD_RESTORE,null);
										Log.d(TAG, "File url :" + backup.getAttachment());
										sendBroadcastToDownload(backup.getAttachment(),mContext);
									}else{
//										Toast.makeText(mContext, R.string.profile_save_failed, Toast.LENGTH_SHORT).show();
									}
								}
								}else{
									if(mProgressDlg != null)
										mProgressDlg.dismiss();
								}
								backup = null;
								queryAPI = null;
				            }
						}).start();
					}				
				});

				applyDialog.dismiss();			
			}			 
		 });
		 applyDialog.show();
	 }
	 
	 private void showNoProfileDialog(){

	        final LeDialog resultDialog = new LeDialog(this, R.style.Theme_LeLauncher_Dialog_Shortcut);
	        
	        resultDialog.setLeTitle(R.string.menu_sense_settings);
	        
	        LinearLayout alert = (LinearLayout) this.getLayoutInflater().inflate(R.layout.profile_backup_result, null);
	        TextView tv = (TextView) alert.findViewById(R.id.profile_backup_hint);
	        tv.setText(R.string.has_no_backup_profile);
	        resultDialog.setLeContentView(alert);
	        setDownloadStatus(false);
	        resultDialog.setLePositiveButton(getString(R.string.profile_save_button), 
	        		new DialogInterface.OnClickListener() {
	        	
	        	            @Override
	        	            public void onClick(DialogInterface dialog,	int whichButton) {

	        	                resultDialog.dismiss();
	        	            }
	        	        });
	        resultDialog.show();
	 }
	 	 
	    @SuppressWarnings("deprecation")
	    private void addPreferences() {
	        addPreferencesFromResource(R.xml.restore_backup_settings);
	        
	        mLocalRestore = findPreference(SettingsValue.PREF_LOCAL_RESTORE);
	        mLocalRestore.setOnPreferenceClickListener(new OnPreferenceClickListener(){
				@Override
				public boolean onPreferenceClick(Preference arg0) {
					setCloudValue(false);
					Message msg = handler.obtainMessage();
					msg.what = APPLY_PROFILE_PROGRESS;
					handler.sendMessage(msg);
					return false;
				}
	        });
	        
	        mCloudRestore = findPreference(SettingsValue.PREF_CLOUD_RESTORE);
	        mCloudRestore.setOnPreferenceClickListener(new OnPreferenceClickListener(){
				@Override
				public boolean onPreferenceClick(Preference arg0) {
					if(!checkConnectNetwork()){
						Toast.makeText(mContext, R.string.grid_empty_error, Toast.LENGTH_SHORT).show();
					}else{
						if(!checkNetwork()){
							showNetworkEnableDialog(mContext,R.string.lejingpin_settings_title);
						}else if(LejingpingSettingsValues.wlanDownloadValue(ProfileSettings.this) && Util.isMobileNetWork(mContext)){
							LejingpingSettingsValues.popupWlanDownloadDialog(ProfileSettings.this);
							
						}else{
							setCloudValue(true);
							cloudRestoreDialog();
						}
					}
					return false;
				}
	        });
	        
	        mLocalBackup = findPreference(SettingsValue.PREF_LOCAL_BACKUP);
	        mLocalBackup.setOnPreferenceClickListener(new OnPreferenceClickListener(){
				@Override
				public boolean onPreferenceClick(Preference arg0) {
					setCloudValue(false);
					Message msg = handler.obtainMessage();
					msg.what = ADD_PROFILE_PROGRESS;
					handler.sendMessage(msg);
					return false;
				}
	        });
	        String summaryname = getFileName(LOCAL_BACKUP_DIR);
	        Log.i(TAG,"-----local file name is " + summaryname);
	        if(summaryname != null){
	        	String summary = getSummary(summaryname);
	        	mLocalBackup.setSummary(summary);
	        }else{
	        	mLocalBackup.setSummary(R.string.no_profile_num);
	        }
	        
	        mCloudBackup = findPreference(SettingsValue.PREF_CLOUD_BACKUP);
	        mCloudBackup.setOnPreferenceClickListener(new OnPreferenceClickListener(){
				@Override
				public boolean onPreferenceClick(Preference arg0) {
					if(!checkConnectNetwork()){
						Toast.makeText(mContext, R.string.grid_empty_error, Toast.LENGTH_SHORT).show();
					}else{
						if(!checkNetwork()){
							showNetworkEnableDialog(mContext,R.string.lejingpin_settings_title);
						}else if(LejingpingSettingsValues.wlanDownloadValue(ProfileSettings.this) && Util.isMobileNetWork(mContext)){
							LejingpingSettingsValues.popupWlanDownloadDialog(ProfileSettings.this);
							
						}else {
						setCloudValue(true);
						sendMessage(handler, ADD_PROFILE_PROGRESS, null);
						};
					}
					return false;
				}
	        });
	        
	        if(PsAuthenServiceL.LENOVOUSER_OFFLINE == PsAuthenServiceL.getStatus(mContext)){
	        	mCloudBackup.setSummary(R.string.no_profile_num);
	        }else {
		        String cloudSummaryName = getTxtFileName(CLOUD_BACKUP_DIR);
		        Log.i(TAG,"cloudSummaryName is " + cloudSummaryName);
		        if(cloudSummaryName != null && !cloudSummaryName.equals(NO_PROFILE_NAME)){
		        	String summary = getSummary(cloudSummaryName);
		        	mCloudBackup.setSummary(summary);
		        }else{
		        	mCloudBackup.setSummary(R.string.no_profile_num);
	
		        	File titleFile = new File(CLOUD_BACKUP_DIR + NO_PROFILE_NAME + LOG_SUFFIX);
		        	if(!titleFile.exists()){
		        		try{
		        			titleFile.createNewFile();     			
		        		}catch(Exception e){
		        			e.printStackTrace();
		        		}
		        	}
		        	
		        }
	        }
	        
	        
	        PackageManager pm = this.getPackageManager();
	        boolean bFound = false;
	        if( SettingsValue.getCurrentMachineType(this) == -1 ){
	        	Log.i(TAG,"ProfileSettings.addPreferences---phone");
	            try { 
	        	    PackageInfo packageInfo = pm.getPackageInfo("com.lenovo.lsf", 0);
	        	    Log.i(TAG,"ProfileSettings.addPreferences---lsf package info:"+ packageInfo);
			        if( packageInfo != null){
			    	    bFound = true;
			        }
			    } catch (NameNotFoundException e) {
			    	Log.i(TAG,"ProfileSettings.addPreferences--lsf not found");
				    e.printStackTrace();
			    }
	        }
			if(!bFound){
				Log.i(TAG,"ProfileSettings.addPreferences---remove cloud---");
				PreferenceScreen pg = (PreferenceScreen)findPreference("pref_profile_screen");
				Preference tempObj = findPreference("pref_restore_settings");
			   	if(tempObj != null){
					pg.removePreference(tempObj);
			   	}
			   	
			   	if(mCloudBackup != null){
			   		mCloudRestore.setOnPreferenceClickListener(null);
					pg.removePreference(mCloudBackup);
			   	}
									
				if(mCloudRestore != null){
					mCloudRestore.setOnPreferenceClickListener(null);
					pg.removePreference(mCloudRestore);
				}
			}
	        
	        //test by dinin 2013-10-09
	        //remove cloudbackup/restore item in TD version
	        if(GlobalDefine.getVerisonCMCCTDConfiguration(this)){
				PreferenceScreen pg = (PreferenceScreen)findPreference("pref_profile_screen");
				Preference tempObj = findPreference("pref_restore_settings");
			   	if(tempObj != null){
					pg.removePreference(tempObj);
			   	}
			   	
			   	if(mCloudBackup != null){
			   		mCloudRestore.setOnPreferenceClickListener(null);
					pg.removePreference(mCloudBackup);
			   	}
									
				if(mCloudRestore != null){
					mCloudRestore.setOnPreferenceClickListener(null);
					pg.removePreference(mCloudRestore);
				}
			}
	   }
	    
	    private String getSummary(String timestamp){
	    	return getString((R.string.lastest_backup_tips),getTimeStamp(timestamp));
	    }
	    
	    public static String getFileName(String pathandname){
	    	String filename = null;
	    	File f = new File(pathandname);
	    	if(!f.exists()){
	    		f.mkdir();
	    	}
	    	File[] fs = f.listFiles(ConstantAdapter.FILE_FILTER_BACKUPFILE);
	    	if (fs != null){
	    		for(File tf : fs ){
	    			if(tf.getName().contains(".lbk")){
		    			filename = tf.getName();
		    			Log.i(TAG,"filename is " + filename);
		    			if(filename != null && (filename.length() > 0)){
		    				int i = filename.lastIndexOf('.');
		    				if(i > -1 && (i < filename.length()))
		    					return filename.substring(0, i);
		    			}
	    			}
	    		}
	    	}
			return filename;
	    }
	    
	    public String getTxtFileName(String pathandname){
	    	String filename = null;
	    	File f = new File(pathandname);
	    	File[] fs = f.listFiles();
	    	if (fs != null){
	    		for(File tf : fs ){
		    			if(tf.getName().contains(LOG_SUFFIX)){
			    			filename = tf.getName();
			    			if(filename != null && (filename.length() > 0)){
			    				int i = filename.lastIndexOf('.');
			    				if(i > -1 && (i < filename.length()))
			    					return filename.substring(0, i);
			    			}
		    		}
	    		}	
	    	}
			return filename;
	    }
	    
	    private void renameTxtFile(String filename,String defaultname){
	    	if(filename == null){
	    		File nameFile = new File(CLOUD_BACKUP_DIR + defaultname + LOG_SUFFIX);
	    		if(!nameFile.exists()){
	    			try{
	    				File fileDirectory = new File(CLOUD_BACKUP_DIR);
	    				fileDirectory.mkdir();
	    				nameFile.createNewFile();
	    			}catch (Exception e){
	    				e.printStackTrace();
	    			}
	    		}
	    	}else{
	    		File existFile = new File(CLOUD_BACKUP_DIR + filename + LOG_SUFFIX);
	    		if(existFile.exists()){
	    			existFile.renameTo(new File(CLOUD_BACKUP_DIR + defaultname + LOG_SUFFIX));
	    			existFile.delete();
	    		}
	    	}	    	
	    }
	    
	    private String getTimeStamp(String time) {
	    	if(time == null)
	    		return null;
	    	Log.d(TAG,"getTimeStamp time:" + time);
			String date = DateFormat.format("yyyy-MM-dd kk:mm:ss", Long.valueOf(time)).toString();
			Log.d(TAG,"getTimeStamp date:" + date);
			return date;
		}	    

	    /* This method is used to upload the backup profile to server.
	     * add by shenchao1@lenovo.com
	    */
	    //test 
	    private long beginTime = 0;
	    private void uploadProfile(){
	        new Thread(new Runnable() {
	            @Override
	            public void run() {
	            	ProfilesFileAPIImpl<Entity<ProfilesMetaInfo>> syncAPI = ProfilesFileAPIImpl.getInstance(mContext);
					try {
						String timestamp = getFileName(CLOUD_BACKUP_DIR);
						Log.i("shen","enter uploadProfile method");
						if(timestamp == null){
							Bundle bundle = new Bundle();
							bundle.putInt(Task.KEY_RESULT, Task.RESULT_CANCEL);
							sendMessage(handler,MSG_CLOUD_BACKUP,bundle);
							return;
						}
						String fullpathfile = CLOUD_BACKUP_DIR + timestamp + ".lbk";
						Log.d(TAG,"startCloudBackupProcess fullpathfile:" + fullpathfile);
						sendMessage(handler, MSG_CLOUD_UPLOAD_PROGRESS, null);
						ProfilesMetaInfo metaInfo = new ProfilesMetaInfo(LcpConstants.DESKTOP_APP_CONFIG_KEY);
						if(SettingsValue.getCurrentMachineType(mContext) == -1){
							metaInfo.setCategory(LcpConstants.DESKTOP_APP_CATEGORY_PHONE);
							Log.i(TAG,"upload phone's backup file");
						}else{
							metaInfo.setCategory(LcpConstants.DESKTOP_APP_CATEGORY_PAD);
							Log.i(TAG,"upload pad's backup file");
						}
						File uploadFile = new File(fullpathfile);
						Log.i("upload","begin upload******file length:"+ uploadFile.length());
						beginTime = System.currentTimeMillis();
						Log.i("upload","begin upload******current time:"+ beginTime);
						String date = DateFormat.format("yyyy-MM-dd kk:mm:ss", Long.valueOf(beginTime)).toString();
						Log.d("upload","date time:" + date);
						Entity<ProfilesMetaInfo> entity = new FileEntity<ProfilesMetaInfo>(uploadFile, metaInfo);
						syncAPI.upload(new MyProgressListener(), entity);
					} catch (Exception e) {
						Log.d(TAG,"backup Exception");
						e.printStackTrace();
					}
	            }
	        }).start();
	    }
	    	    
	    private String getLastRetoreTime(String timetype){
	    	SharedPreferences getTime = getSharedPreferences("Restore", Context.MODE_PRIVATE);
	    	String lastRestoreTime = getTime.getString(timetype, "0");
	    	
	    	return lastRestoreTime;	    		    	
	    }
	    
	    private void setRestoreTime (String timetype,String time){
	    	Log.i(TAG,"in setRestoreTime time is " + time);
	    	SharedPreferences setTime = getSharedPreferences("Restore", Context.MODE_PRIVATE);
	    	SharedPreferences.Editor editor = setTime.edit();
	    	editor.putBoolean(ConstantAdapter.EXCLUDED_SETTING_KEY, true);
	    	editor.putString(timetype, time);
	    	editor.commit();
	    	Log.i(TAG,"editor.commit() is " + editor.commit());
	    }
	    
	    private void deleteLbkDownloadTask(){
			LDownloadManager.getDefaultInstance(mContext).deleteTask(new DownloadInfo(DEFAULT_PACKNAME,DEFAULT_VERSIONCODE));
			mLbkDownload = false;
	    }
	    
		private void sendBroadcastToDownload(String url,
				Context context) {
			mLbkDownload = true;
			AppDownloadUrl downurl = new AppDownloadUrl();
			Log.i(TAG,"url is " + url);
			downurl.setDownurl(url);
			downurl.setPackage_name(DEFAULT_PACKNAME);
			downurl.setVersion_code(DEFAULT_VERSIONCODE);
			downurl.setCategory(DownloadConstant.CATEGORY_LBK);

			sendMessage(DownloadHandler.getInstance(context),
					DownloadConstant.MSG_DOWN_LOAD_URL, downurl);
		}
		
		private void sendMessage(Handler handler, int what, Object obj) {
			if (handler != null) {
				handler.removeMessages(what);
				Message msg = new Message();
				msg.obj = obj;
				msg.what = what;
				handler.sendMessage(msg);
			}
		}
				
		private class DownloadStateReceiver extends BroadcastReceiver {
			 final String SYSTEM_DIALOG_REASON_KEY = "reason";
			  final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				Log.i("TAG","DownloadStateReceiver action is " + action);

				if(LBK_CLOUD_STORE_ACTION.equals(action)){
					deleteLbkDownloadTask();
					String time = getLastRetoreTime("temptime");
					setRestoreTime("restoretime", time);
					EnableState enableState = new EnableState();
					enableState.enableFolder = true;
					enableState.enablePriorities =  true;
					enableState.enableQuickEntries =  true;
					enableState.enableSettings =  true;
					enableState.enableWallpaper = true;
					enableState.enableWidgets = true;

				if(handler != null){
					    handler.sendMessage(Message.obtain(handler, MSG_CLOUD_LAST_BACKUP_TIME, null));
					}
				}else if(DownloadConstant.ACTION_DOWNLOAD_DELETE.equals(action)){
					//test by dining 2013-07-10
					//check the downlaod service status if delete the task, reset the current status
					
					int result = intent.getIntExtra(DownloadConstant.EXTRA_RESULT, DownloadConstant.FAILD_DOWNLOAD_OTHER_ERROR);
					String pkg = intent.getStringExtra(HwConstant.EXTRA_PACKAGENAME);
									
					if(pkg != null && pkg.equals(DEFAULT_PACKNAME)){
						dismissProgressDialog();
					    if(DownloadConstant.FAILD_DOWNLOAD_NETWORK_ERROR == result){
						    Toast.makeText(context, R.string.download_net_error,
								    Toast.LENGTH_SHORT).show();
					    }else{
					    	Toast.makeText(context, R.string.failed_download_other_error,
								    Toast.LENGTH_SHORT).show();
					    }
					}
					
				}else if(DownloadConstant.ACTION_DOWNLOAD_STATE_CHANGED.equals(action)){
					if(intent.getStringExtra(DownloadConstant.EXTRA_STATUS).equals("pause")){
						deleteLbkDownloadTask();
						dismissProgressDialog();
						Toast.makeText(context, R.string.failed_download_other_error,
								Toast.LENGTH_SHORT).show();			
					}
				}else if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				        String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
				        if (reason != null) {
				            if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
				            	if(checkDownloadStatus() == Downloads.STATUS_RUNNING){
                                    setDownloadStatus(false);
					                deleteLbkDownloadTask();
					                if(mProgressDlg != null){
										mProgressDlg.dismiss();
									}
					                Toast.makeText(mContext, R.string.download_app_fail, Toast.LENGTH_SHORT).show();
				            	}
				            }				     
				        }
				   }				
				return;
			}
			
		}
			
		public class MyProgressListener implements ProgressListener{
			private int lastProgress = 0;
			public MyProgressListener(){
			}

			@Override
			public void onSubProgress(final long current,final long total, Bundle bundle ) {
				onProgress(current, total, bundle);
			}

			@Override
			public void onStart(Bundle bundle) {
			}

			@Override
			public void onProgress(final long current, final long total, Bundle bundle) {
				Log.d("upload","onProgress " + "current="+current+" , total="+total +
						"  , current time:"+ System.currentTimeMillis());
			}

			@Override
			public void onFinish(Bundle bundle) {
				long currentTime = System.currentTimeMillis();
				Log.d("upload","onFinish, current time:"+ currentTime);
				Log.d("upload","onFinish, total use time:"+ ( currentTime - beginTime));
				String date = DateFormat.format("yyyy-MM-dd kk:mm:ss", Long.valueOf(currentTime)).toString();
				Log.d("upload","onFinish end date time:" + date);
				if (Debug.MAIN_DEBUG_SWITCH)
					R2.echo("ProfileSettings.upload end----onFinish---");
				
				int result = bundle.getInt(Task.KEY_RESULT);
				
				Message msg = handler.obtainMessage();
				msg.obj = bundle;
				msg.what = MSG_CLOUD_BACKUP;
				handler.sendMessage(msg);
				
				final StringBuilder taskMessageBuilder = new StringBuilder();
				if(result == Task.RESULT_OK){
					
					int add = bundle.getInt(Task.KEY_RESULT_ADD);
					int update = bundle.getInt(Task.KEY_RESULT_UPDATE);
					int delete = bundle.getInt(Task.KEY_RESULT_DELETE);
					
					taskMessageBuilder.append("任务成功！新增:" + add + ",修改：" + update + ",删除：" + delete);
//					Toast.makeText(mContext, R.string.boot_backup_desktop_success,Toast.LENGTH_SHORT).show();
				}else if(result == Task.RESULT_CANCEL){
					taskMessageBuilder.append("Cancel！,result:"+result);
//					Toast.makeText(mContext, R.string.boot_backup_desktop_fail,Toast.LENGTH_SHORT).show();
				}else {
					taskMessageBuilder.append("任务出错！,result:"+result);
//					Toast.makeText(mContext, R.string.boot_backup_desktop_fail,Toast.LENGTH_SHORT).show();
				}
				Log.d(TAG,"onFinish " + taskMessageBuilder.toString());
			}
		};
		
		//test by dining 2013-07-10 
		private void dismissProgressDialog(){
			if (mProgressDlg != null){
                mProgressDlg.dismiss();
			}
		}
		
		private boolean checkNetwork(){
			if(!SettingsValue.isNetworkEnabled(mContext)){
	   		    return false;
		   }
			return true;
		}
		
		private boolean checkConnectNetwork(){
			ConnectivityManager connectManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
			NetworkInfo networkinfo = connectManager.getActiveNetworkInfo();
			if(null == networkinfo){
				return false;
			}			
			return true;
		}
		
		private int checkDownloadStatus(){
			DownloadInfo downloadInfo = LDownloadManager.getDefaultInstance(mContext)
					.getDownloadInfo(new DownloadInfo(DEFAULT_PACKNAME,DEFAULT_VERSIONCODE));
			int downloadStatus = -1;
			 if( downloadInfo != null){
				   downloadStatus = downloadInfo.getDownloadStatus();
				  }
			return downloadStatus;
		}
}
