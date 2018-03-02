package com.lenovo.lejingpin.hw.content.action;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.lejingpin.ams.AmsRequest;
import com.lenovo.lejingpin.ams.AmsSession;
import com.lenovo.lejingpin.ams.AmsSession.AmsCallback;
import com.lenovo.lejingpin.ams.AppInfoRequest;
import com.lenovo.lejingpin.ams.AppInfoRequest.AmsErrorMsg;
import com.lenovo.lejingpin.ams.AppInfoRequest.AppInfo;
import com.lenovo.lejingpin.ams.AppInfoRequest.AppInfoResponse;
import com.lenovo.lejingpin.hw.content.data.DownloadAppInfo;
import com.lenovo.lejingpin.hw.content.data.HwConstant;
import com.lenovo.lejingpin.hw.content.data.ReCommendsApp;
import com.lenovo.lejingpin.hw.content.data.RecommendsAppList;
import com.lenovo.lejingpin.hw.content.db.HWDBUtil;

import com.lenovo.lejingpin.hw.content.util.ContentManagerLog;
import com.lenovo.lejingpin.hw.content.util.DeviceInfo;
import com.lenovo.lejingpin.share.service.TaskService.Action;

public class AppInfoAction implements Action {
	private static final String TAG = "AppInfoAction";
	
//	private Context mContext;
	private String mPackageName;
	private String mVersionCode;
	private String mAction;
	
	private  RecommendsAppList mRecommendsAppList;
	private  boolean mListFlag = false;
	private static int mListIndex = 0;
	private static ArrayList<DownloadAppInfo> mListAppInfo = new ArrayList<DownloadAppInfo>();
	private static ArrayList<DownloadAppInfo> mListAfterFourteenAppInfo = new ArrayList<DownloadAppInfo>();;
	

	private static AppInfoAction mAppInfoAction;
	
	private AppInfoAction(){}
	
	private AppInfoAction(Context context){
		//mContext = context;
	}
	
	private AppInfoAction(String package_name,String version_code){
		//mContext = context;
	}
	
	public static AppInfoAction getInstance(String packageName,String versionCode,String action){
		if(mAppInfoAction==null){
			mAppInfoAction = new AppInfoAction(packageName,versionCode);
		}
		mAppInfoAction.mPackageName = packageName;
		mAppInfoAction.mVersionCode = versionCode;
		mAppInfoAction.mAction = action;
		return mAppInfoAction;
	}
	

	public static AppInfoAction getInstance(){
		if(mAppInfoAction==null){
			mAppInfoAction = new AppInfoAction();
		}
		return mAppInfoAction;
	}
	
	public void doAction(Context context) {
		requestAppInfo(context);
	}
	
	// yangmao add search_move 1225 start

	public void doAction_hawaiiSearch(Context context) {
		Log.i(TAG, "AppInfoAction.doAction_hawaiiSearch()");
		requestAppInfo_hawaiiSearch(context);
	}

	// yangmao add search_move 1225 start
	
	
	
	public void requestListAppInfo(Context context,RecommendsAppList appList){
		HashMap<String,DownloadAppInfo> appInfoMap = HWDBUtil.queryAppInfoList(context);
		mListFlag = true;
		mRecommendsAppList  = appList;
		if(mRecommendsAppList!=null && !mRecommendsAppList.isEmpty()){
			Log.d(TAG, "requestListAppInfo >> mRecommendsAppList : "+mRecommendsAppList.getSize());
			for(ReCommendsApp app : mRecommendsAppList.getNewAppStoreList()){
				String pkName = app.getPackageName();
				String vCode = app.getVersionCode();
				if(appInfoMap==null || !appInfoMap.containsKey(pkName+vCode)){
					getHwAppInfo(context,pkName,vCode);
				}
			}
		}
	}
	
	
	private void requestAppInfo(final Context context){
		DeviceInfo deviceInfo = DeviceInfo.getInstance(context);
		AmsSession.init(context, new AmsCallback(){

			public void onResult(AmsRequest request, int code, byte[] bytes) {
				if(code!=200){
					if(mListFlag){
						sendIntentSpereFinished(context,true);
						mListFlag = false;
					}else{
						sendIntentForAppInfoFinished(context,false,String.valueOf(code),mPackageName,mVersionCode);
					}
				}else{
					getHwAppInfo(context,mPackageName,mVersionCode);
				}
			}
		},deviceInfo.getWidthPixels(),deviceInfo.getHeightPixels());
	}
	
	
	//yangmao add search_move 1225 start
	
		
		private void requestAppInfo_hawaiiSearch(final Context context){	
			Log.i(TAG, "AppInfoAction.requestAppInfo_hawaiiSearch()");
			DeviceInfo deviceInfo = DeviceInfo.getInstance(context);
			AmsSession.init(context, new AmsCallback(){
				public void onResult(AmsRequest request, int code, byte[] bytes) {
					Log.i(TAG,"AppInfoAction.requestAppInfo >> result code:"+ code);
					if(code!=200){
						Log.i(TAG, "requestAppInfo_hawaiiSearch() code != 200");
						sendIntentForAppInfoFinished(context,false,String.valueOf(code),mPackageName,mVersionCode);
					}else{
						Log.i(TAG,"start getAppInfo_hawaiiSearch");
						getAppInfo_hawaiiSearch(context,mPackageName,mVersionCode);
					}
				}
			},deviceInfo.getWidthPixels(),deviceInfo.getHeightPixels());
		}
		//yangmao add search_move 1225 end
	
	
	private void getHwAppInfo(final Context context,final String packageName,final String versionCode){
		AppInfoRequest infoRequest = new AppInfoRequest(context);
		infoRequest.setData(packageName, versionCode);
		AmsSession.execute(context, infoRequest, new AmsCallback(){

			public void onResult(AmsRequest request, int code, byte[] bytes) {
				AppInfoResponse infoResponse = new AppInfoResponse();
				if(bytes != null) {
					infoResponse.parseFrom(bytes);
				}
				DownloadAppInfo downInfo =null;
				boolean success= infoResponse.getIsSuccess();
				mListIndex++;
				if(!success){
					AmsErrorMsg errorMsg = infoResponse.getErrorMsg();
					String errorCode = "";
					if(errorMsg!=null){
						errorCode = errorMsg.getErrorCode();
					}
					if(!mListFlag){
						sendIntentForAppInfoFinished(context,false,errorCode,packageName,versionCode);
					}
				}else{
					AppInfo info = infoResponse.getAppInfo();
					downInfo = new DownloadAppInfo();
					downInfo.setAppName(info.getAppname());
					downInfo.setPackageName(info.getPackageName());
					downInfo.setVersionCode(info.getAppVersionCode());
					downInfo.setStarLevel(info.getStarLevel());
					downInfo.setPay(Boolean.valueOf(info.getIspay()));
					downInfo.setAppSize(info.getAppSize());
					downInfo.setAppAbstract(info.getAppAbstract());
					downInfo.setAppVersion(info.getAppVersion());
					downInfo.setAppPublishDate(info.getAppPublishDate());
					downInfo.setAppCommentCount(info.getCommentCount());
					downInfo.setDownloadCount(info.getDownloadCount());
					try{
						downInfo.setFirstSnapPath(info.getSnapList().toString().replace("[", "").replace("]", "").replace("&isCompress=true&width=-1&height=-1&quantity=0.6", ""));
					}catch(Exception e){
						ContentManagerLog.d(TAG, "Exception >> e : " + e);
						e.printStackTrace();
					}
				}
				if(mListFlag){
					if(mListIndex <= 14){
						if(downInfo!=null){
							mListAppInfo.add(downInfo);
						}
					}else{
						if(downInfo!=null){
							mListAfterFourteenAppInfo.add(downInfo);
						}
					}
					if(mListIndex==mRecommendsAppList.getSize()){
						saveAppInfoToDb(context,mListAfterFourteenAppInfo);
						mListIndex = 0;
						mListFlag = false;
					}else if(mListIndex==14){
						saveAppInfoToDb(context,mListAppInfo);
					}
				}else{
					HWDBUtil.insertAppInfo(context, downInfo);
					sendIntentForAppInfoFinished(context,true,null,packageName,versionCode);
					
				}
			}
			
		});
	}
	
	private void saveAppInfoToDb(final Context context,final ArrayList<DownloadAppInfo> list){
		new Thread(){
			@Override
			public void run() {
				super.run();
				HWDBUtil.insertAppInfoList(context, list);
				list.clear();
				sendIntentSpereFinished(context,true);
			}
			
		}.start();
	}
	
	private void sendIntentForAppInfoFinished(Context context,boolean result,String errorCode,String packageName,String versionCode){
		Intent intent = new Intent();
		intent.setAction(mAppInfoAction.mAction+"_COMPLETE");
		intent.putExtra("result", result);
		if(!result){
			intent.putExtra("error_code", errorCode);
		}
		intent.putExtra("package_name", packageName);
		intent.putExtra("version_code", versionCode);
		Log.d(TAG,"mAppInfoAction.mAction : "+mAppInfoAction.mAction+"_COMPLETE");
		context.sendBroadcast(intent);
	}
	
	private void sendIntentSpereFinished(Context context,boolean result){
		Intent intent = new Intent();
		intent.setAction(HwConstant.ACTION_SPERE_APP_LIST_COMPLETE);
		intent.putExtra("result", result);
		context.sendBroadcast(intent);
	}
	
	
	
	
	//yangmao add for move 1225
	
	private void getAppInfo_hawaiiSearch(final Context context,final String package_name,final String version_code){
		Log.i(TAG, "getAppInfo_hawaiiSearch");
		AppInfoRequest infoRequest = new AppInfoRequest(context);
		infoRequest.setData(package_name, version_code);
		AmsSession.execute(context, infoRequest, new AmsCallback(){

			public void onResult(AmsRequest request, int code, byte[] bytes) {
				Log.i(TAG,"AppInfoAction.getAppInfo >> result code:"+ code);
				AppInfoResponse infoResponse = new AppInfoResponse();
				if(bytes != null) {
					infoResponse.parseFrom(bytes);
				}
				boolean success= infoResponse.getIsSuccess();
				Log.i(TAG,"AppInfoAction.getAppInfo >> success :"+ success);
				DownloadAppInfo downInfo_hawaiisearch = null;
				if(success){
					AppInfo info = infoResponse.getAppInfo();
					if( info != null ){
					downInfo_hawaiisearch = new DownloadAppInfo();
					downInfo_hawaiisearch.setAppName(info.getAppname());
					downInfo_hawaiisearch.setPackageName(info.getPackageName());
					downInfo_hawaiisearch.setVersionCode(info.getAppVersionCode());
					downInfo_hawaiisearch.setStarLevel(info.getStarLevel());
					downInfo_hawaiisearch.setAppSize(info.getAppSize());
					downInfo_hawaiisearch.setAppAbstract(info.getAppAbstract());
					downInfo_hawaiisearch.setAppVersion(info.getAppVersion());
					downInfo_hawaiisearch.setAppPublishDate(info.getAppPublishDate());
					downInfo_hawaiisearch.setAppCommentCount(info.getCommentCount());
					
					//zdx modify
					downInfo_hawaiisearch.setPay(Boolean.valueOf(info.getIspay()));
					downInfo_hawaiisearch.setAppPrice(Float.valueOf(info.getAppPrice()));
					downInfo_hawaiisearch.setDownloadCount(info.getDownloadCount());
					downInfo_hawaiisearch.setAppPublishName(info.getAuthor());
					downInfo_hawaiisearch.setIconAddr(info.getIconAddr());
					
					try{
						downInfo_hawaiisearch.setFirstSnapPath(info.getSnapList().toString().replace("[", "").replace("]", ""));
					}catch(Exception e){
						Log.i(TAG, "AppInfoAction.getAppInfo(), Exception >> " + e);
					}
				
					}
				}
				Log.i(TAG, "start sendIntentForAppInfoFinished_hawaiiSearch ");
				sendIntentForAppInfoFinished_hawaiiSearch(context,success,package_name,version_code,downInfo_hawaiisearch);
			}
			
		});
	}
	
	
	
	//yangmao add for move 1225
	private void sendIntentForAppInfoFinished_hawaiiSearch(Context context,boolean result,String package_name,String version_code,DownloadAppInfo downInfo_hawaiisearch){
		Log.i(TAG, "AppInfoAction.sendIntentForAppInfoFinished(), result:"+ result);
		Intent intent = new Intent();
		intent.setAction(mAppInfoAction.mAction+"_COMPLETE_NEW");
		Log.i(TAG, "AppInfoAction.sendIntentForAppInfoFinished()==Action is:"+mAppInfoAction.mAction+"_COMPLETE");
		intent.putExtra("result", result);
		intent.putExtra("package_name", package_name);
		intent.putExtra("version_code", version_code);
		intent.putExtra("hawaii_search_appinfo", downInfo_hawaiisearch);
		intent.setPackage(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF);
		context.sendBroadcast(intent);
	}
	
	

}
