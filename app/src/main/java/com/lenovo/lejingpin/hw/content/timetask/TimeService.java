package com.lenovo.lejingpin.hw.content.timetask;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;

import com.lenovo.lejingpin.hw.content.data.HwConstant;
import com.lenovo.lejingpin.hw.content.util.ContentManagerLog;

@Deprecated
public class TimeService extends Service {
	private static final String TAG = "TimeService";
	
	private Handler mHandler;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		mHandler = ContentTimeTask.getInstance(this).mHandler;
		ContentManagerLog.d(TAG, "onStartCommand >> mHandler : "+mHandler);
		// TODO Auto-generated method stub
		if(intent!=null){
			String action = intent.getAction();
			boolean switchValue = intent.getBooleanExtra("switch", true);
			if(!TextUtils.isEmpty(action) && mHandler!=null){
				if(HwConstant.ACTION_TIME_SPERE_APPLIST.equals(action)){
					if(switchValue){
						mHandler.sendEmptyMessage(HwConstant.SPERE_TIME_SCHEDULE);
					}else{
						mHandler.sendEmptyMessage(HwConstant.SPERE_TIME_CANCEL_SCHEDULE);
					}
				}else if(HwConstant.ACTION_TIME_UPGRADE_APPLIST.equals(action)){
					if(switchValue){
						mHandler.sendEmptyMessage(HwConstant.UPGRADE_TIME_SCHEDULE);
					}else{
						mHandler.sendEmptyMessage(HwConstant.UPGRADE_TIME_CANCEL_SCHEDULE);
					}
				}
			}
		}
		return  START_STICKY;
	}

}
