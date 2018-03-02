/*
 * Copyright (C) 2009 The Android Open Source Project
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


import android.graphics.drawable.Drawable;
import android.content.res.Configuration;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
//import com.lenovo.leos.content.ILeIntent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.graphics.Bitmap;
import android.view.View;

import java.util.ArrayList;



//import static com.lenovo.launcher.Launcher.ContactsInfo;

public class LauncherService extends Service {

    static final String TAG = "LauncherService";
    
    public boolean mWidgetPauseFlag = false;

    private int mServiceStartId = -1;
    public boolean mLaunchFlag = false;
    public boolean mLauncherLogFlag = true;
    
    public boolean mBootFlag = false;
    public Bitmap mServiceBitmap = null;
    public Bitmap mWeatherBitmap = null;
    public Bitmap mLandWeatherBitmap = null;
    public Bitmap mPortWeatherBitmap = null;
    public int mWeatherImageSrc = -1;
    public String mWeatherCityName = null;
    public String mWeatherLowTemp = null;
    public String mWeatherHighTemp = null;
    
    static LauncherService sLauncherService;
    public Drawable portBackground;
    public Drawable landBackground;
    public boolean mLanguageChangeFlag = false;
    public boolean mLanguageChangeHomeFlag = false;
    public boolean mHomeScreenLiveFlag = false;
    public boolean mHomeScreenPauseFlag = true;
    public boolean mSwitchFadeFlag = false;
    public boolean mFadeSwitchFlag = false;
    public Bitmap mDefaultHomeContactBitmap = null;
    public boolean mWidgetReminderFlag = false;
    public boolean mLoadScreenFlag = true;


    public boolean mLauncherHomeScreenSwitchFlag = true;
    public boolean mLauncherIMAppFlag = false;
    public String mLauncherIMAppPkgName = null;
    public String mLauncherIMAppClassName = null;

    public boolean  mLauncherMenuVisible = false;
    public boolean bLauncherMenuCreated = false;
    public int mLauncherMenuTaskId = 0;
    //public LauncherMenu mLauncherMenu = null;
    public boolean mTrashFlag = true;
    public boolean mNewTrashFlag = false;
    public boolean btimetickRegisterFlag = false;
    public boolean bFirstLoadWidgetFlag = false;
    public boolean bLongClickMainMenuFlag = false;
    public boolean bWokeSpceScrollingFlag = false;
    public int iFirstClickHomeScreen = 0;
    public int iClickSoundFlag = 0;
    public int callNumber = -1;
    public int msgNumber = -1;
    public int mailNumber = -1;
    public int imNumber = -1;
    public int useExternalTheme = 0;

    public int diffmsgNumber = -1;
    public int diffmailNumber = -1;
    public long WEEKMILSECOND = 7*24*3600*1000;


    public boolean mTaskManagerVisible = false;
    public boolean bTaskManagerCreated = false;

    public boolean bDockPowerOnFlag = false;
    public boolean bPowerOnFlag = false;
    public boolean bAppLoadFinishFlag = false;
    public boolean bSDCardNoMountedFlag = false;
  
    public int mTaskManagerTaskId = 0;
    //public TaskManagerActivity mTaskManagerActivity = null;

    public boolean bLauncherIntentReceiverCreated = false;
    
    public boolean bFavoriteContactsChanged = true;
    public boolean bFavoriteContactsNotify = false;
//    public ArrayList<ContactsInfo> mServiceContactsListInfo = null;
    public ArrayList<Drawable> mServiceDrawable = null;


    public String[] strSDPkgNameList = null;
    public int mWorkspaceWallpaperScrollX = 0;
    public int bdefaultContactFlag = 2;
     


    public ArrayList<Bitmap> mContactBitmaps= new ArrayList<Bitmap>();
//    public boolean bSnapShowFlag = true;
    public int mScreenCount = 6;
//    public int mCurrentScreenIndex = 0;
    public boolean mAddView = false;
    
    public int[] mCellXY = null;

    public static final String APPCHINA_APPSTORE_URL = "http://m.appchina.com/market-web/lemon/search.action?q=";
    public static final String LENOVO_APPSTORE_URL = "http://www.lenovomm.com/appstore/toWapAppDetail.do?pn=";
    public static final String LENOVO_HOME_URL = "http://www.lenovomm.com/appstore/html/index.html";
    public static final String NATIVE_APPSTORE_URL = "leapp://ptn/appinfo.do?service =ptn&packagename=";
	
    /*RK_DOWNLOAD_SEARCH_KEY zhanglz1@lenovo.com 2012-11-16 */
    public static final String LENOVO_APPSTORE_APPNAME_URL = "http://www.lenovomm.com/appstore/html/search.html?skey=";
    
    /*RK_DOWN_GOOGLE_PLAYSTORE dining@lenovo.com 2012-11-16 S*/
    //google play store
    public static final String GOOGLE_PLAYSTORE_URL = "https://play.google.com";
    /*RK_DOWN_GOOGLE_PLAYSTORE dining@lenovo.com 2012-11-16 E*/

    /*RK_MAGICGESTRUE_LEVOICE zhanglz1@lenovo.com 2012-11-21 S*/
    public static String LEVOICE_CALL_ACTION = "com.lenovo.levoice.action.VOICE_RECOGNIZE_CALL";//打电话
    public static String LEVOICE_SMS_ACTION = "com.lenovo.levoice.action.VOICE_RECOGNIZE_SMS";//发短信 
    public static String LEVOICE_CONCACT_ACTION= "com.lenovo.levoice.action.VOICE_RECOGNIZE_CONCACT";//联系人 
    public static String LEVOICE_BROWSER_ACTION= "com.lenovo.levoice.action.VOICE_RECOGNIZE_WEBSITE";//浏览器 
    public static String LEVOICE_APP_ACTION = "com.lenovo.levoice.action.VOICE_RECOGNIZE_APP";//打开应用
    public static String LEVOICE_ALLOVER_ACTION = "com.lenovo.levoice.action.VOICE_RECOGNIZE_SPEECH";//全局快捷命令
    public static String LEVOICE_PACKAGENAME ="com.lenovo.levoice";
    public static String LEVOICE_VERSIONCODE = "46";
    public static String LEVOICE_DOWNLOADURL = "http://launcher.lenovo.com/launcher/data/attachment/app/voiceSearch.apk"; 
    /*RK_MAGICGESTRUE_LEVOICE zhanglz1@lenovo.com 2012-11-21 E*/

    public static synchronized LauncherService getInstance() {
        if (sLauncherService == null) {
            sLauncherService = new LauncherService();
        }

        return sLauncherService;
    }


    public LauncherService() {
    }
    public void recycleBitmap(){
	if(mServiceBitmap != null){
	    mServiceBitmap.recycle();
	    mServiceBitmap = null;
            Log.e(TAG, " onCreate  recycleBitmap");
	}
    }
    public Drawable getRocketBackgroundDrawable(int orientation){
	    	
        if ( orientation == Configuration.ORIENTATION_PORTRAIT){
            Log.e(TAG, " onCreate  portBackgroud");
	    return portBackground;
	}else if ( orientation == Configuration.ORIENTATION_LANDSCAPE){
            Log.e(TAG, " onCreate  landBackgroud");
	    return landBackground;
	}
	return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        Log.e(TAG, " onCreate  onCreateonCreate onCreate onCreate onCreate in TestAppWidgetService");
    }
   @Override
    public void onStart(Intent intent, int startId) {
        mServiceStartId = startId;

        String action = intent.getAction();
        Log.e(TAG, " onStart BroadcastReceiver mIntentReceiver action = "+action);
        Log.e(TAG, " onStart BroadcastReceiver mIntentReceiver action code  = "+startId);
    }

    private void notifyChange(String what,int id) {
        //mAppWidgetProvider.notifyChange(this, what,id);
    }
        
    @Override
    public void onDestroy() {

        //unregisterReceiver(mIntentReceiver);
        super.onDestroy();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    /*RK_MAGICGESTRUE_LEVOICE zhanglz1@lenovo.com 2012-11-21 S*/
	public interface OnDoubleClickListener {
		public void OnSingleClick(View v);
		public void OnDoubleClick(View v);
	}
	private static boolean waitDouble = true;
	private static View viewflag = null;
	public static void registerDoubleClickListener(final View view,
			final OnDoubleClickListener onDoubleClickListener) {
		if (onDoubleClickListener == null)
			return;
		view.setOnClickListener(new View.OnClickListener() {
			private static final int DOUBLE_CLICK_TIME = 1; // 双击间隔时间350毫秒

			private Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					onDoubleClickListener.OnSingleClick((View) msg.obj);
				}

			};

			// 等待双击
			public void onClick(final View v) {
				if (waitDouble) {
					viewflag = v;
					waitDouble = false; // 与执行双击事件
					new Thread() {
						public void run() {
							try {
								Thread.sleep(DOUBLE_CLICK_TIME);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} // 等待双击时间，否则执行单击事件
							if (!waitDouble) {
								// 如果过了等待事件还是预执行双击状态，则视为单击
								waitDouble = true;
								Message msg = handler.obtainMessage();
								msg.obj = v;
								handler.sendMessage(msg);
							}
						}

					}.start();
				} else {
					if (v.equals(viewflag)) {
					    waitDouble = true;
					    onDoubleClickListener.OnDoubleClick(v); // 执行双击
					}else{
						waitDouble = true;
						Message msg = handler.obtainMessage();
						msg.obj = v;
						handler.sendMessage(msg);
					}
				}
			}
		});
	}

	public static void startDownload(Context mContext,String packagename,String versionCode,String category, String appName, String iconUrl, 
 String downloadUrl, String mimeType,
			String installTag, String continueTag, String downloadmessage) {
		//zhangdxa modify for remove magicdownload
		/*
		MagicDownloadControl.Status status = MagicDownloadControl
				.queryDownloadStatus(mContext, packagename, versionCode);
		switch (status) {
		case DOWNLOADING:
			Toast.makeText(mContext, R.string.levoice_download_downloading,
					Toast.LENGTH_SHORT).show();
			break;
		case PAUSE:
			Toast.makeText(mContext, R.string.levoice_download_downloading,
					Toast.LENGTH_SHORT).show();
			// 启动
			MagicDownloadControl.downloadFromCommon(mContext, packagename,
					versionCode, category, appName, null, downloadUrl,
					mimeType, "true", "true");
			break;
		case UNINSTALL:
			Toast.makeText(mContext, R.string.levoice_download_install,
					Toast.LENGTH_SHORT).show();
			// 提示安装
			MagicDownloadControl.downloadFromCommon(mContext, packagename,
					versionCode, category, appName, null, downloadUrl,
					mimeType, "true", "true");
			break;
		case UNDOWNLOAD:
			showWarningDialog(mContext, packagename, versionCode, category,
					appName, iconUrl, downloadUrl, mimeType, installTag,
					continueTag, downloadmessage);
			break;
		default:
			showWarningDialog(mContext, packagename, versionCode, category,
					appName, iconUrl, downloadUrl, mimeType, installTag,
					continueTag, downloadmessage);
			break;
     }  */                  
 }
	protected static void showWarningDialog(final Context mContext,final String packagename,final String versionCode,final String category, final String appName, String iconUrl, 
			final String downloadUrl, final String mimeType,
			final String installTag, final String continueTag ,final String downloadmessage) {
		//zhangdxa modify for remove magicdownload
        /*AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.settings_network_dialog_title)
            .setMessage(downloadmessage)
            .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
            	 MagicDownloadControl.downloadFromCommon (mContext, 
                		 packagename,  
                		 versionCode, 
                		 category, 
                		 appName, 
                         null, 
                         downloadUrl, 
                         mimeType,
                         "true", 
                         "true");
         	    dialog.dismiss();
             }
         })
        .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                 dialog.cancel();
            }
        });

        AlertDialog alert = builder.create();
        alert.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.cancel();
                }
                return false;
            }
        });

        alert.show();*/
    }
    /*RK_MAGICGESTRUE_LEVOICE zhanglz1@lenovo.com 2012-11-21 E*/
}
