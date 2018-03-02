package com.lenovo.lejingpin.hw.content.action;

import android.content.Context;
import android.content.Intent;

import com.lenovo.lejingpin.ams.AmsRequest;
import com.lenovo.lejingpin.ams.AmsSession;
import com.lenovo.lejingpin.ams.AmsSession.AmsCallback;
import com.lenovo.lejingpin.ams.SpereCommendRequest;
import com.lenovo.lejingpin.ams.SpereCommendRequest.SpereCommendResponse;
import com.lenovo.lejingpin.hw.content.data.HwConstant;
import com.lenovo.lejingpin.hw.content.data.ReCommendsApp;
import com.lenovo.lejingpin.hw.content.data.RecommendsAppList;
import com.lenovo.lejingpin.hw.content.db.HWDBUtil;

import com.lenovo.lejingpin.hw.content.util.AppNumUtil;
import com.lenovo.lejingpin.hw.content.util.DeviceInfo;
import com.lenovo.lejingpin.hw.content.util.SharePreferenceUtil;
import com.lenovo.lejingpin.share.service.TaskService.Action;

public class SpereAppListAction implements Action {
	

	private static final String TAG = "SpereAppListAction";
//	private Context mContext;
	private static SpereAppListAction mSpereAppListAction;
	
	private boolean mFlag = false;
	
	private SpereAppListAction(){}
	
	private SpereAppListAction(Context context){
//		mContext = context;
	}
	
	public static SpereAppListAction getInstance(){
		if(mSpereAppListAction==null){
			mSpereAppListAction = new SpereAppListAction();
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
		AmsSession.init(context, new AmsCallback(){
			public void onResult(AmsRequest request, int code, byte[] bytes) {
				if(code!=200){
					sendIntentSpereFinished(context,false,0);
				}else{
					getSpereAppList(context);
				}
			}
			
		},deviceInfo.getWidthPixels(),deviceInfo.getHeightPixels());
	}
	
	private void getSpereAppList(final Context context){
		SpereCommendRequest request = new SpereCommendRequest(context);
		request.setData(0,HwConstant.COUNT_SPRERE_LIST_DB_TOTAL, "hw", false);
		AmsSession.execute(context, request, new AmsCallback(){
			public void onResult(AmsRequest request, int code, byte[] bytes){
				SpereCommendResponse response = new SpereCommendResponse();
				if (bytes != null) {
					response.parseFrom(bytes);
				}
				int itemCount = response.getApplicationItemCount();
				AppNumUtil.getInstance(context).saveIndex(itemCount, AppNumUtil.FREE_TOP_TYPE);
				RecommendsAppList  appList = new RecommendsAppList();
				if (itemCount > 0){
					for(int i = itemCount; i > 0;i--){
						SpereCommendRequest.Application a = response.getApplicationItemList().get(i-1);
						ReCommendsApp app = new ReCommendsApp();
						app.setAppName(a.getAppName());
						app.setPackageName(a.getPackageName());
						app.setVersionCode(a.getAppVersionCode());
						app.setIconAddress(a.getIconAddr());
						app.setLcaid(a.getLcaId());
						app.setFavorites("0");
						app.setCollect(a.getDataSource());
						appList.add(app);
					}
					SharePreferenceUtil util = SharePreferenceUtil.getInstance(context);
					util.save(SharePreferenceUtil.FLAG_SPERE_APP);
				}
				if(appList!=null && !appList.isEmpty()){
					RecommendsAppList newAppList  = HWDBUtil.insterSpereApp(context, appList);
					mFlag = false;
					if(newAppList!=null && !newAppList.isNewEmpty()){
						AppInfoAction.getInstance().requestListAppInfo(context, newAppList);
					}
				}else{
					sendIntentSpereFinished(context,false,0);
				}
			}
		});
	}
	
	private void sendIntentSpereFinished(Context context,boolean result,int size){
		mFlag = false;
		Intent intent = new Intent();
		intent.setAction(HwConstant.ACTION_SPERE_APP_LIST_COMPLETE);
		intent.putExtra("result", result);
		intent.putExtra("new_size", size);
		context.sendBroadcast(intent);
	}
}
