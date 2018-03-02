package com.lenovo.lejingpin.hw.content.action;

import android.content.Context;
import android.content.Intent;

import com.lenovo.lejingpin.ams.AmsRequest;
import com.lenovo.lejingpin.ams.AmsSession5.AmsCallback;
import com.lenovo.lejingpin.ams.AmsSession5;
import com.lenovo.lejingpin.ams.SpereCommendRequest5;
import com.lenovo.lejingpin.ams.SpereCommendRequest5.Application;
import com.lenovo.lejingpin.ams.SpereCommendRequest5.SpereCommendResponse5;
import com.lenovo.lejingpin.hw.content.data.HwConstant;
import com.lenovo.lejingpin.hw.content.data.ReCommendsApp;
import com.lenovo.lejingpin.hw.content.data.RecommendsAppList;
import com.lenovo.lejingpin.hw.content.db.HWDBUtil;

import com.lenovo.lejingpin.hw.content.util.AppNumUtil;
import com.lenovo.lejingpin.hw.content.util.ContentManagerLog;
import com.lenovo.lejingpin.hw.content.util.DeviceInfo;
import com.lenovo.lejingpin.hw.content.util.SharePreferenceUtil;
import com.lenovo.lejingpin.share.service.TaskService.Action;

public class SpereAppListAction5 implements Action{
	

	private String TAG = "SpereAppListAction5";
//	private Context mContext;
	private static SpereAppListAction5 mSpereAppListAction;
	
	private  boolean mFlag = false;
	
	private SpereAppListAction5(){}
	
	private SpereAppListAction5(Context context){
//		mContext = context;
	}
	
	public static SpereAppListAction5 getInstance(){
		if(mSpereAppListAction==null){
			mSpereAppListAction = new SpereAppListAction5();
		}
		return mSpereAppListAction;
	}

	
	public void doAction(Context context){
		if(!mFlag){
			requestSpereAppList(context);
		}
	}
	
	private void requestSpereAppList(final Context context){
		mFlag = true;
		DeviceInfo deviceInfo = DeviceInfo.getInstance(context);
		AmsSession5.init(context, new AmsCallback(){
			public void onResult(AmsRequest request, int code, byte[] bytes) {
				if(code!=200){
					sendIntentSpereFinished(context,false);
				}else{
					getSpereAppList(context);
				}
			}
			
		},deviceInfo.getWidthPixels(),deviceInfo.getHeightPixels());
	}
	
	private void getSpereAppList(final Context context){
		SpereCommendRequest5 request = new SpereCommendRequest5(context);
		int startIndex = 0;
		int count = HwConstant.COUNT_SPRERE_LIST_DB_TOTAL;
		request.setData(startIndex,count, "hw", false);
		AmsSession5.execute(context, request, new AmsCallback(){
			public void onResult(AmsRequest request, int code, byte[] bytes){
				SpereCommendResponse5 response = new SpereCommendResponse5();
				if (bytes != null) {
					response.parseFrom(bytes);
				}
				int itemCount = response.getApplicationItemCount();
				AppNumUtil.getInstance(context).saveIndex(itemCount, AppNumUtil.FREE_TOP_TYPE);
				RecommendsAppList  appList = new RecommendsAppList();
				RecommendsAppList  topAppList = new RecommendsAppList();
				if (itemCount > 0){
					for(int i = itemCount; i > 0;i--){
						Application a = response.getApplicationItemList().get(i-1);
						ReCommendsApp app = new ReCommendsApp();
						app.setAppName(a.getAppName());
						app.setPackageName(a.getPackage_name());
						app.setVersionCode(a.getApp_versioncode());
						app.setIconAddress(a.getIcon_addr());
						app.setLcaid(a.getLcaId());
						app.setFavorites("0");
						app.setCollect(a.getData_source());
						app.setDownloadCount(a.getDownload_count());
						app.setStartLevel(a.getStar_level());
						app.setVersion(a.getApp_version());
						app.setAppSize(a.getApp_size());
						if(i <= 5 ){
							topAppList.add(app);
						}
						appList.add(app);
					}
					SharePreferenceUtil util = SharePreferenceUtil.getInstance(context);
					util.save(SharePreferenceUtil.FLAG_SPERE_APP);
				}
				if(appList!=null && !appList.isEmpty()){
					appList = HWDBUtil.insterSpereApp(context, appList);
					AppInfoAction5.getInstance(null, null, null).requestListAppInfo(context,appList);
					mFlag =false;
				}else{
					sendIntentSpereFinished(context,false);
				}
			}
		});
	}
	
	private void sendIntentSpereFinished(Context context,boolean result){
		mFlag = false;
		Intent intent = new Intent();
		intent.setAction(HwConstant.ACTION_SPERE_APP_LIST_COMPLETE);
		intent.putExtra("result", result);
		context.sendBroadcast(intent);
	}
}
