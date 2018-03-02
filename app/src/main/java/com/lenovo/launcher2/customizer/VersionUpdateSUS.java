package com.lenovo.launcher2.customizer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.lenovo.launcher.R;
import com.lenovo.lps.sus.EventType;
import com.lenovo.lps.sus.SUS;
import com.lenovo.lps.sus.SUSListener;

/* RK_ID: RK_LELAUNCHER_VERSION_UPDATE. AUT: zhangdxa DATE: 2012-06-29  */
// Use SUS-SDK_v1.1.2_release_pro_121120.jar
public class VersionUpdateSUS {
    private static final String TAG ="VersionUpdate";
	public static final String  VERSION_UPDATE_ACTIVITY= "android.intent.action.VERSION_UPDATE_ACTIVITY";

	private static final String AUTO_UPDATE_PREF = "com.lenovo.launcher.versionupdate_preferences";
	private static final String KEY_AUTO_UPDATE_TIME = "auto_update_time";
	private static final String KEY_AUTO_UPDATE_ON = "auto_update_on";
    public static final String ACTION_QUERY_COMPLETE = "com.lenovo.action.ACTION_QUERY_COMPLETE";
    public static final String ACTION_QUERY_ERROR = "com.lenovo.action.ACTION_QUERY_ERROR";
	private static final Long AUTO_UPDATE_INTERVAL = 1000L;//92 * 24 * 60 * 60 * 1000L;      
	public boolean mAutoUpdateOn = false;
	public boolean mAutoUpdate = true;
	public boolean isStartVersionUpdateFlag = false;
	public static int INIT_UPDATE_OK = 0;
	public static int INIT_UPDATE_ERROR_CONTEXT_NULL = -1;
	public static int INIT_UPDATE_ERROR_NOT_SDCARD = -2;
	public static int INIT_UPDATE_ERROR_NOT_NETWORK_NOTENABLE = -3;
	
	public int countTestServer = 0;
	private Context mContext = null;
	private static VersionUpdateSUS mInstance;
	public static VersionUpdateSUS getInstance(){
		if( mInstance == null){
			mInstance = new VersionUpdateSUS();
		}
		return mInstance;
	}
	
	public Context getContext(){
		return mContext;
	}
	
	//Auto version update step 1
	public void startAutoVersionUpdate(){
		Log.i(TAG,"startAutoVersionUpdate---isStartVersionUpdateFlag:"+isStartVersionUpdateFlag);
        if( !isNetworkEnabled() ){
        	Log.i(TAG,"startAutoVersionUpdate--network error---return!");
        	return;
        }
        Log.i(TAG,"startAutoVersionUpdate--network error---return!");
		if (!isStartVersionUpdateFlag) {
			mAutoUpdate = true;
    		isStartVersionUpdateFlag = true;
    		countTestServer = 0;
    		Log.i(TAG,"VersionUpdateSUS.startAutoVersionUpdate(), call SUS.testSUSServer");
    		SUS.testSUSServer(mContext);
    	}
	}
    public void finishVersionUpdate() {
   	   SUS.finish();
    }
    public void initVersionUpdate( final Context context ) {
   	    Log.i(TAG, "VersionUpdateSUS.initVersionUpdate()---context:"+ context);
   	    mContext = context;
   	    mAutoUpdate = true;
 		SUS.setDebugModeFlag(true);
 		SUS.setSDKPromptDisableFlag(true);
 		SUS.setSUSListener(new SUSListener() {
 			@Override
 			public void onUpdateNotification(EventType eventType, String param) {
 				// TODO Auto-generated method stub
 				Log.i(TAG,"SUSListener.onUpdateNotifitcation(), eventType:"+ eventType );
 				boolean bShowToast = true;
 				String message = null;
 				switch (eventType) {
 				/** 版本更新过程启动失败的通知事件，原因：当前网络不可用 */
				case SUS_FAIL_NETWORKUNAVAILABLE:
					message = mContext.getString(R.string.SUS_MSG_FAIL_NETWORKUNAVAILABLE);
					break;
				/** 版本更新过程启动失败的通知事件，原因：设置了仅在WLAN模式下更新，但当前设备WLAN没有打开 */
				case SUS_FAIL_NOWLANCONNECTED:
					message = mContext.getString(R.string.SUS_MSG_FAIL_NOWLANCONNECTED);
					break;
				/** 版本更新过程失败的通知事件，原因：存储空间不足 */
				case SUS_FAIL_INSUFFICIENTSTORAGESPACE:
					message = mContext.getString(R.string.SUS_MSG_INSUFFICIENTSTORAGESPACE);
					break;
				/** 版本更新过程失败的通知事件，原因：下载过程出现异常 */
				case SUS_FAIL_DOWNLOAD_EXCEPTION:
					message = mContext.getString(R.string.SUS_MSG_UPDATE_EXCEPTION);
					break;
				/** 已有版本更新过程在处理中的通知事件 */
				case SUS_WARNING_PENDING:
					Log.i(TAG,"******************************SUS_WARNING_PENDING");
					break;
				/** 程序包下载开始的通知事件 */
				case SUS_DOWNLOADSTART:
					Log.i(TAG,"******************************SUS_DOWNLOADSTART");
					break;
				/** 程序包下载完成的通知事件 */
				case SUS_DOWNLOADCOMPLETE:
					Log.i(TAG,"******************************SUS_DOWNLOADCOMPLETE, param:"+ param);
					if( param != null )
					    //Try to install silently, if failed then install in a normal way.
					    SUS.installAppExt(mContext, param, "com.lenovo.launcher", null, 3);
					
					break;
				/** 查询可更新版本的响应事件 */
				case SUS_QUERY_RESP:
					Log.i(TAG,"******************************SUS_QUERY_RESP");
					if( mAutoUpdate ){
						startUpdateActivity( param);
					}else{
					    sendQueryCompleteMessage( param,0);
					}
					break;
				//Auto version update step 2	
				case SUS_TESTSERVER_RESP:
					Log.i(TAG,"******************************SUS_TESTSERVER_RESP,param:"+ param);
					if( param.equals("SUCCESS")){
						if( mAutoUpdate) {
							queryVersion();
						}else{
						    sendQueryCompleteMessage( param,1);
						}
					}else{
						if( countTestServer < 2 ){
						    SUS.testSUSServer(mContext);
						    countTestServer++;
						}else if( !mAutoUpdate){
							sendQueryCompleteMessage( param,1);
						}
					}
			        break;
 				default:
 					bShowToast= false;
 					break;
 				}
 				if( ( mContext != null ) && ( message != null) && bShowToast ){
 				    sendQueryErrorMessage( );
 				    if( !mAutoUpdate )
 				    	Toast.makeText(mContext, message, 1).show();
 				    	
 				}
 			}
 		});
    }
   
    private boolean getStateOfAutoUpdate() {
        SharedPreferences prefs =  mContext.getSharedPreferences(AUTO_UPDATE_PREF, Context.MODE_PRIVATE);
   	    Long currentTime = System.currentTimeMillis();
   	    Log.i(TAG,"VersionUpdateSUS.getStateOfAutoUpdate(), currentTime:"+ currentTime);
   	    Long savedTime = prefs.getLong(KEY_AUTO_UPDATE_TIME, currentTime );
   	    Log.i(TAG,"VersionUpdateSUS.getStateOfAutoUpdate(), savedTime:"+ savedTime);
   	    if( currentTime - savedTime >= AUTO_UPDATE_INTERVAL) {
   	    	Log.i(TAG,"start auto version update!");
   	        return true;
        }
   	    if( savedTime.equals( currentTime ) ) {
   		    Log.i(TAG,"VersionUpdateSUS.getStateOfAutoUpdate(), write to :"+AUTO_UPDATE_PREF);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(KEY_AUTO_UPDATE_TIME, currentTime);
            editor.commit();
   	    }
   	    Log.i(TAG,"Not start auto version update!");
   	    return false;
     }
     
	 private boolean isNetworkEnabled(){
		 if( mContext == null) return false;
         SharedPreferences sharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(mContext);
         boolean isNetworkEnabled = sharedPreferences.getBoolean(SettingsValue.PREF_NETWORK_ENABLER, false);
         return isNetworkEnabled;
	 }
	 
	 public String getNetworkConnectType() {
		 if( mContext == null) return "other";
		 ConnectivityManager mConnMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		 NetworkInfo info = mConnMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		 NetworkInfo infoM = mConnMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		 if(info != null && info.isConnected()) {
			return "wifi";
		 } else if(infoM != null && infoM.isConnected()) { return "mobile"; }
			return "other";
	 }
	 
	 private void sendQueryCompleteMessage(String param, int type){
		 if( mContext == null) return;
		 Intent intent = new Intent(ACTION_QUERY_COMPLETE);
		 intent.putExtra("queryinfo", param);
		 intent.putExtra("type", type);
		 mContext.sendBroadcast(intent);
	 }
	 
	 private void sendQueryErrorMessage(){
		 if( mContext == null) return;
		 Intent intent = new Intent(ACTION_QUERY_ERROR);
		 mContext.sendBroadcast(intent);
	 }
	 
	 private void startUpdateActivity(String param){
		if( mContext == null) return;
 		Log.i(TAG,"startUpdateActivity()---");
        Intent intent = new Intent(VERSION_UPDATE_ACTIVITY);
 		//Intent intent = new Intent();
 		//intent.setClass(context, VersionUpdateActivity.class);
 		intent.putExtra("auto", true);
 		intent.putExtra("version_param", param);
 		mAutoUpdate = true;
 		try {
 			mContext.startActivity(intent);
 		} catch (Exception e) {
 			Log.i(TAG,"startUpdateActivity()----exception---context:"+ mContext);
 		}
	 }
	 
	 public boolean queryVersion() {
		 if(mContext == null) return false;
		 Log.i(TAG, "VersionUpdateSUS.queryVersion()!-----isVersionUpdateStarted:"+ 
	        SUS.isVersionUpdateStarted());
		 if (!SUS.isVersionUpdateStarted()) {
			Log.i(TAG, "VersionUpdateSUS.queryVersion(), start...");
			//SUS.AsyncStartVersionUpdate_IgnoreUserSettings(mContext);
			String packName = null;
			ApplicationInfo applicationinfo = null;
			String channelKey = null;			
			PackageManager packagemanager = mContext.getPackageManager();
			packName = mContext.getPackageName();
			PackageInfo packageinfo = null;
			try {
				packageinfo = packagemanager.getPackageInfo(packName, 0);
			} catch (NameNotFoundException e) {
				if( !mAutoUpdate ){
				    //Toast.makeText(context, R.string.SUS_ERROR_CURRENT_VERSION, 1).show();
					Toast.makeText(mContext, R.string.SUS_MSG_UPDATE_EXCEPTION, 1).show();
				}
				return false;
			}
			int versionCode = packageinfo == null ? 0 : packageinfo.versionCode;
			try {
				applicationinfo = packagemanager.getApplicationInfo(packName, 128);
			} catch (NameNotFoundException e1) {
				if( !mAutoUpdate )
				    //Toast.makeText(context, R.string.SUS_ERROR_CURRENT_VERSION, 1).show();
					Toast.makeText(mContext, R.string.SUS_MSG_UPDATE_EXCEPTION, 1).show();
				return false;
			}
		    if (applicationinfo != null && applicationinfo.metaData != null) {
				channelKey = applicationinfo.metaData.getString("SUS_CHANNEL");
			}
			Log.i(TAG, "VersionUpdateSUS.queryVersion(), start, call SUS.AsyncQueryLatestVersionByPackageName.");
			SUS.AsyncQueryLatestVersionByPackageName(mContext, packName, versionCode, channelKey);
		}else{
			if( !mAutoUpdate ){
			    Toast.makeText(mContext, R.string.SUS_MSG_WARNING_PENDING, 1).show();
			}
			return false;
		}
		return true;
	}	
		private VersionUpdateSUS() {
			// TODO Auto-generated constructor stub
		}
		public void setAutoUpdate(boolean value){
			mAutoUpdate = value;
		}
		
		public boolean getAutoUpdateFromPrefs(){
			SharedPreferences prefs =
	                mContext.getSharedPreferences(AUTO_UPDATE_PREF, Context.MODE_PRIVATE);
			mAutoUpdateOn = prefs.getBoolean(KEY_AUTO_UPDATE_ON, false);
	    	if( !prefs.getBoolean(ConstantAdapter.EXCLUDED_SETTING_KEY, false)){
	    	    SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(ConstantAdapter.EXCLUDED_SETTING_KEY, true);
                editor.commit();
	    	}
            return mAutoUpdateOn;
		}
		
		public boolean getAutoUpdateOn(){
			return mAutoUpdateOn;
		}
}
