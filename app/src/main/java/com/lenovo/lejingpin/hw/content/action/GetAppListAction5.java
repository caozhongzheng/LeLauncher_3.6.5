package com.lenovo.lejingpin.hw.content.action;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;

import com.lenovo.lejingpin.ams.AmsRequest;
import com.lenovo.lejingpin.ams.AmsSession5;
import com.lenovo.lejingpin.ams.AmsSession5.AmsCallback;
import com.lenovo.lejingpin.ams.GetAppListRequest;
import com.lenovo.lejingpin.ams.GetAppListRequest.Application;
import com.lenovo.lejingpin.ams.GetAppListRequest.GetAppListResponse;
import com.lenovo.lejingpin.hw.content.data.HwConstant;
import com.lenovo.lejingpin.hw.content.data.ReCommendsApp;
import com.lenovo.lejingpin.hw.content.data.RecommendsAppList;
import com.lenovo.lejingpin.hw.content.db.HWDBUtil;

import com.lenovo.lejingpin.hw.content.util.DeviceInfo;
import com.lenovo.lejingpin.hw.content.util.SharePreferenceUtil;
import com.lenovo.lejingpin.share.service.TaskService.Action;

public class GetAppListAction5 implements Action {
//	private Context mContext;
	
	private GetAppListAction5(){}
	private GetAppListAction5(Context context){
//		mContext = context;
	}
	
	private static GetAppListAction5 mInstance;
	
	public static GetAppListAction5 getInstance(){
		if(mInstance==null){
			mInstance = new GetAppListAction5();
		}
		return  mInstance;
	}

	@Override
	public void doAction(Context context) {
		requestSpereAppList(context);
	}
	private void requestSpereAppList(final Context context){
		DeviceInfo deviceInfo = DeviceInfo.getInstance(context);
		AmsSession5.init(context, new AmsCallback(){
			public void onResult(AmsRequest request, int code, byte[] bytes) {
				if(code!=200){
					//sendIntentSpereFinished(false);
				}else{
					getAppList(context);
				}
			}
			
		},deviceInfo.getWidthPixels(),deviceInfo.getHeightPixels());
	}
	
	private void getAppList(final Context context){
		GetAppListRequest request = new GetAppListRequest();
		request.setData(1, 30, "top", "v", "root", "false", "all", "d");
		AmsSession5.execute(context, request, new AmsCallback(){

			@Override
			public void onResult(AmsRequest request, int code, byte[] bytes) {
				GetAppListResponse response = new GetAppListResponse();
				response.parseFrom(bytes);
				RecommendsAppList  appList = new RecommendsAppList();
				RecommendsAppList  topAppList = new RecommendsAppList();
				boolean isSuccess = response.getIsSuccess();
				if(isSuccess){
					ArrayList<Application> apps = response.getApplicationItemList();
					for(Application a : apps){
						ReCommendsApp app = new ReCommendsApp();
						app.setAppName(a.getAppName());
						app.setPackageName(a.getPackageName());
						app.setVersionCode(a.getAppVersionCode());
						app.setIconAddress(a.getIcon_addr());
						app.setLcaid(a.getLcaId());
						app.setFavorites("0");
						app.setCollect("10");
						app.setStartLevel(a.getStar_level());
						app.setDownloadCount(a.getDownload_count());
						int topSize = topAppList.getSize();
						if(topSize<5){
							topAppList.add(app);
						}
						appList.add(app);
					}
					SharePreferenceUtil util = SharePreferenceUtil.getInstance(context);
					util.save(SharePreferenceUtil.FLAG_SPERE_APP);
				}
				if(appList!=null && !appList.isEmpty()){
					HWDBUtil.insterSpereApp(context, appList);
					AppInfoAction5.getInstance(null, null, null).requestListAppInfo(context,topAppList);
					//sendIntentSpereFinished(true);
				}else{
					sendIntentSpereFinished(context,false);
				}
			}
		});
	}
	
	private void sendIntentSpereFinished(Context context,boolean result){
		Intent intent = new Intent();
		intent.setAction(HwConstant.ACTION_SPERE_APP_LIST_COMPLETE);
		intent.putExtra("result", result);
		context.sendBroadcast(intent);
	}

}
