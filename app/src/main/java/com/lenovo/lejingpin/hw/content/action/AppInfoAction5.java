package com.lenovo.lejingpin.hw.content.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.lejingpin.ams.AmsRequest;
import com.lenovo.lejingpin.ams.AmsSession;
import com.lenovo.lejingpin.ams.AmsSession5;
import com.lenovo.lejingpin.ams.AppInfoRequest;
import com.lenovo.lejingpin.ams.AmsSession5.AmsCallback;
import com.lenovo.lejingpin.ams.AppInfoRequest5;
import com.lenovo.lejingpin.ams.AppInfoRequest.AppInfoResponse;
import com.lenovo.lejingpin.ams.AppInfoRequest5.AppInfo;
import com.lenovo.lejingpin.ams.AppInfoRequest5.AppInfoResponse5;
import com.lenovo.lejingpin.hw.content.data.DownloadAppInfo;
import com.lenovo.lejingpin.hw.content.data.HwConstant;
import com.lenovo.lejingpin.hw.content.data.ReCommendsApp;
import com.lenovo.lejingpin.hw.content.data.RecommendsAppList;
import com.lenovo.lejingpin.hw.content.db.HWDBUtil;

import com.lenovo.lejingpin.hw.content.util.ContentManagerLog;
import com.lenovo.lejingpin.hw.content.util.DeviceInfo;
import com.lenovo.lejingpin.share.service.TaskService.Action;

public class AppInfoAction5 implements Action {
	private String TAG = "AppInfoAction";
	
//	private Context mContext;
	private String mPackageName;
	private String mVersionCode;
	private String mAction;
	
	private boolean mListFlag = false;
	private static int mListIndex = 0;
	private static ArrayList<DownloadAppInfo> mListAppInfo = new ArrayList<DownloadAppInfo>();
	

	private static AppInfoAction5 mAppInfoAction;
	
	private AppInfoAction5(){}
	
	private AppInfoAction5(String package_name,String version_code){
//		mContext = context;
	}
	
	public static AppInfoAction5 getInstance(String package_name,String version_code,String action){
		if(mAppInfoAction==null){
			mAppInfoAction = new AppInfoAction5(package_name,version_code);
		}
		mAppInfoAction.mPackageName = package_name;
		mAppInfoAction.mVersionCode = version_code;
		mAppInfoAction.mAction = action;
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
		mListFlag = true;
		HashMap<String,DownloadAppInfo> appInfoMap = HWDBUtil.queryAppInfoList(context);
		if(appList!=null && !appList.isEmpty()){
			List<ReCommendsApp> apps = appList.getAppStoreList();
			if(apps!=null && !apps.isEmpty()){
				int size = apps.size();
				int k=0;
				for(int i=size;i > 0 ; i--){
					if(k==5){
						break;
					}else{
						ReCommendsApp app = apps.get(i-1);
						String pkName = app.getPackageName();
						String vCode = app.getVersionCode();
						if(appInfoMap==null || !appInfoMap.containsKey(pkName+vCode)){
							getHwAppInfoList(context,pkName,vCode);
						}
					}
					k++;
				}
				sendIntentSpereFinished(context,true);
			}
		}
	}
	
	
	private void requestAppInfo(final Context context){
		DeviceInfo deviceInfo = DeviceInfo.getInstance(context);
		AmsSession5.init(context, new AmsCallback(){

			public void onResult(AmsRequest request, int code, byte[] bytes) {
				if(code!=200){
					sendIntentForAppInfoFinished(context,false,mPackageName,mVersionCode);
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
				AmsSession.init(context, new AmsSession.AmsCallback(){
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
	
	
	
	
	
	
	private void getHwAppInfoList(final Context context,final String package_name,final String version_code){
		AppInfoRequest5 infoRequest = new AppInfoRequest5(context);
		infoRequest.setData(package_name, version_code);
		AmsSession5.execute(context, infoRequest, new AmsCallback(){

			public void onResult(AmsRequest request, int code, byte[] bytes) {
				AppInfoResponse5 infoResponse = new AppInfoResponse5();
				if(bytes != null) {
					infoResponse.parseFrom(bytes);
				}
				DownloadAppInfo downInfo = null;
				boolean success= infoResponse.getIsSuccess();
				Log.d(TAG, "getHwAppInfoList >> success : "+success);
				mListIndex++;
				
				if(success){
					AppInfo info = infoResponse.getAppInfo();
					downInfo = new DownloadAppInfo();
					downInfo.setAppName(info.getAppname());
					downInfo.setPackageName(info.getPackage_name());
					downInfo.setVersionCode(info.getApp_versioncode());
					downInfo.setStarLevel(info.getStar_level());
					downInfo.setPay(Boolean.valueOf(info.getIspay()));
					downInfo.setAppSize(info.getApp_size());
					downInfo.setAppAbstract(info.getApp_abstract());
					downInfo.setAppVersion(info.getApp_version());
					downInfo.setAppCommentCount(info.getComment_count());
					try{
						downInfo.setFirstSnapPath(info.getSnapList().toString().replace("[", "").replace("]", "").replace("&isCompress=true&width=-1&height=-1&quantity=0.6", ""));
					}catch(Exception e){
						ContentManagerLog.d(TAG, "Exception >> e : " + e);
						e.printStackTrace();
					}
				}
				
				if(mListIndex<=5){
					if(downInfo!=null){
						mListAppInfo.add(downInfo);
					}
				}
				if(mListIndex==5){
					saveAppInfoToDb(context,mListAppInfo);
					mListFlag = false;
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
			}
			
		}.start();
	}
	
	private void getHwAppInfo(final Context context,final String package_name,final String version_code){
		AppInfoRequest5 infoRequest = new AppInfoRequest5(context);
		infoRequest.setData(package_name, version_code);
		AmsSession5.execute(context, infoRequest, new AmsCallback(){

			public void onResult(AmsRequest request, int code, byte[] bytes) {
				AppInfoResponse5 infoResponse = new AppInfoResponse5();
				if(bytes != null) {
					infoResponse.parseFrom(bytes);
				}
				
				boolean success= infoResponse.getIsSuccess();
				if(!success){
					sendIntentForAppInfoFinished(context,false,package_name,version_code);
				}else{
					AppInfo info = infoResponse.getAppInfo();
					DownloadAppInfo downInfo = new DownloadAppInfo();
					downInfo.setAppName(info.getAppname());
					downInfo.setPackageName(info.getPackage_name());
					downInfo.setVersionCode(info.getApp_versioncode());
					downInfo.setStarLevel(info.getStar_level());
					downInfo.setPay(Boolean.valueOf(info.getIspay()));
					downInfo.setAppSize(info.getApp_size());
					downInfo.setAppAbstract(info.getApp_abstract());
					downInfo.setAppVersion(info.getApp_version());
					downInfo.setAppCommentCount(info.getComment_count());
					try{
						downInfo.setFirstSnapPath(info.getSnapList().toString().replace("[", "").replace("]", "").replace("&isCompress=true&width=-1&height=-1&quantity=0.6", ""));
					}catch(Exception e){
						ContentManagerLog.d(TAG, "Exception >> e : " + e);
						e.printStackTrace();
					}
					HWDBUtil.insertAppInfo(context, downInfo);
					sendIntentForAppInfoFinished(context,true,package_name,version_code);
				}
			}
			
		});
	}
	
	private void sendIntentForAppInfoFinished(Context context,boolean result,String package_name,String version_code){
		Intent intent = new Intent();
		intent.setAction(mAppInfoAction.mAction+"_COMPLETE");
		intent.putExtra("result", result);
		intent.putExtra("package_name", package_name);
		intent.putExtra("version_code", version_code);
		Log.d(TAG,"mAppInfoAction.mAction : "+mAppInfoAction.mAction+"_COMPLETE");
		context.sendBroadcast(intent);
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
		AmsSession.execute(context, infoRequest, new AmsSession.AmsCallback(){

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
					AppInfoRequest.AppInfo info = infoResponse.getAppInfo();
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
