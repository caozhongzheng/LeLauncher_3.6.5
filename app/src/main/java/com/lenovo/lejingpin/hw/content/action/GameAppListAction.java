package com.lenovo.lejingpin.hw.content.action;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lenovo.lejingpin.ams.AmsRequest;
import com.lenovo.lejingpin.ams.AmsSession5;
import com.lenovo.lejingpin.ams.AmsSession5.AmsCallback;
import com.lenovo.lejingpin.ams.AppPageContentRequest;
import com.lenovo.lejingpin.ams.AppPageContentRequest.AppContent;
import com.lenovo.lejingpin.ams.AppPageContentRequest.AppPageContentResponse;
import com.lenovo.lejingpin.ams.AppTypeRequest;
import com.lenovo.lejingpin.ams.AppTypeRequest.AppTypeRsponse;
import com.lenovo.lejingpin.ams.SpereCommendRequest5.Application;

import com.lenovo.lejingpin.hw.content.util.DeviceInfo;
//import com.lenovo.lejingpin.hw.game.widget.GameConstant;
import com.lenovo.lejingpin.hw.ui.RecommendLocalAppInfo;
import com.lenovo.lejingpin.share.service.TaskService.Action;

public class GameAppListAction implements Action{
	private static String TAG = "GameAppListAction";
//	private Context mContext;
	
	private static GameAppListAction mInstance;
	
	private GameAppListAction(){}
	private GameAppListAction(Context context){
//		mContext = context;
	}
	
	public static GameAppListAction getInstance(){
		if(mInstance==null){
			mInstance = new GameAppListAction();
		}
		return mInstance;
	}
	

	@Override
	public void doAction(Context context) {
		// TODO Auto-generated method stub
		requestGameType(context);
	}
	
	private void requestGameType(final Context context){
		DeviceInfo deviceInfo = DeviceInfo.getInstance(context);
		AmsSession5.init(context, new AmsCallback(){

			@Override
			public void onResult(AmsRequest request, int code, byte[] bytes) {
				if(code==200){
					getGameType(context);
				}else{
					sendIntentGameWidget(context,false,null);
				}
			}
			
		}, deviceInfo.getWidthPixels(), deviceInfo.getHeightPixels());
	}
	
	
	
	private void getGameList(final Context context){
		AppTypeRequest typeRequest = new AppTypeRequest(context);
		typeRequest.setData(1, 8, "top", "yx", null);
		
		AmsSession5.execute(context, typeRequest, new AmsCallback(){
			@Override
			public void onResult(AmsRequest request,
					int code, byte[] bytes) {
				AppTypeRsponse response = new AppTypeRsponse();
				response.parseFrom(bytes);
				boolean isSuccess = response.getIsSuccess();
				if(isSuccess){
					ArrayList<Application> apps = response.getApplicationItemList();
					ArrayList<RecommendLocalAppInfo> rList = new ArrayList<RecommendLocalAppInfo>();
					for(Application  a : apps){
						RecommendLocalAppInfo info = new RecommendLocalAppInfo();
						info.setAppName(a.getAppName());
						info.setIconAddress(a.getIcon_addr());
						info.setPackageName(a.getPackage_name());
						info.setVersionCode(a.getApp_versioncode());
						rList.add(info);
					}
					Intent intent = new Intent();
					//intent.setAction(GameConstant.ACTION_GAME_APP_COMMPLETE);
					intent.putExtra("gamelist", rList);
					context.sendBroadcast(intent);
					for(Application app : apps){
						Log.d(TAG, "Application >> appName : "+app.getAppName());
					}
				}
			}
			
		});
		
	}
	
	private void getGameType(final Context context){
		AppPageContentRequest request = new AppPageContentRequest();
		request.setData(1);
		AmsSession5.execute(context, request, new AmsCallback(){
			@Override
			public void onResult(AmsRequest request, int code, byte[] bytes) {
				if(code==200){
					AppPageContentResponse response = new AppPageContentResponse();
					response.parseFrom(bytes);
					boolean isSuccess = response.isSuccess();
					if(isSuccess){
						ArrayList<AppContent> appContents = response.getAppContents();
						if(appContents!=null&& !appContents.isEmpty()){
							String typeCode = null;
							for(AppContent ac : appContents){
								if("tophot".equals(ac.getmMenuCode())){
									typeCode = ac.getmCode();
									break;
								}
							}
							AppTypeRequest typeRequest = new AppTypeRequest(context);
							typeRequest.setData(1, 24, "top", "yx", typeCode);
							AmsSession5.execute(context, typeRequest, new AmsCallback(){

								@Override
								public void onResult(AmsRequest request,
										int code, byte[] bytes) {
									AppTypeRsponse response = new AppTypeRsponse();
									response.parseFrom(bytes);
									boolean isSuccess = response.getIsSuccess();
									if(isSuccess){
										ArrayList<Application> apps = response.getApplicationItemList();
										ArrayList<RecommendLocalAppInfo> rList = new ArrayList<RecommendLocalAppInfo>();
										for(Application  a : apps){
											RecommendLocalAppInfo info = new RecommendLocalAppInfo();
											info.setAppName(a.getAppName());
											info.setIconAddress(a.getIcon_addr());
											info.setPackageName(a.getPackage_name());
											info.setVersionCode(a.getApp_versioncode());
											rList.add(info);
										}
										sendIntentGameWidget(context,true,rList);
									}else{
										sendIntentGameWidget(context,false,null);
									}
								}
								
							});
							
						}
					}
				}else{
					sendIntentGameWidget(context,false,null);
				}
				
			}
			
		});
	}
	
	
	private void sendIntentGameWidget(Context context,boolean result,ArrayList<RecommendLocalAppInfo> list){
		Intent intent = new Intent();
		//intent.setAction(GameConstant.ACTION_GAME_APP_COMMPLETE);
		intent.putExtra("result", result);
		if(list!=null){
			intent.putExtra("gamelist", list);
		}
		context.sendBroadcast(intent);
	}

}
