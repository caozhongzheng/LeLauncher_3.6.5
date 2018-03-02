package com.lenovo.lejingpin.hw.content.timetask;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.lenovo.lejingpin.hw.content.HwContentMangerReceiver;
import com.lenovo.lejingpin.hw.content.data.HwConstant;
import com.lenovo.lejingpin.hw.content.db.HWDBUtil;
import com.lenovo.lejingpin.hw.content.util.ContentManagerLog;
import com.lenovo.lejingpin.hw.content.util.SharePreferenceUtil;

@Deprecated
public class ContentTimeTask {
	
	private String TAG = "ContentTimeTask";
	
	private Context mContext;
	
	private static ContentTimeTask mContentTimeTask;
	private AlarmManager mAlarmManager;
	private SharePreferenceUtil mSharePreferenceUtil;
	
	public static Handler mHandler;
	
	
	private ContentTimeTask (Context context){
		mContext = context;
		mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		mSharePreferenceUtil = SharePreferenceUtil.getInstance(context);
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg){
				ContentManagerLog.d(TAG, "handleMessage >> msg.waht : "+msg.what);
				switch(msg.what){
				case HwConstant.SPERE_TIME_SCHEDULE:
					spereTimeSchedule();
					break;
				case HwConstant.UPGRADE_TIME_SCHEDULE:
					upgradeTiemSchedule();
					break;
				case HwConstant.SPERE_TIME_CANCEL_SCHEDULE:
					cancelTimeSchedule(HwConstant.ACTION_TIMESHEDULE_SPEREAPPLIST);
					break;
				case HwConstant.UPGRADE_TIME_CANCEL_SCHEDULE:
					cancelTimeSchedule(HwConstant.ACTION_TIMESHEDULE_UPGRADEAPPLIST);
					break;
					default:
					break;
				}
			}
		};
	}
	
	private void spereTimeSchedule(){
		new Thread(){
			@Override
			public void run() {
				super.run();
		boolean spereCando = mSharePreferenceUtil.canDo(SharePreferenceUtil.FLAG_SPERE_APP);
		boolean hasSpereData = HWDBUtil.hasUnFavoritesRecommends(mContext);
		if(hasSpereData){
			Intent intent = new Intent();
			intent.setAction(HwConstant.ACTION_SPERE_APP_LIST_COMPLETE);
			intent.putExtra("result", true);
			mContext.sendBroadcast(intent);
		}
		if(spereCando || !hasSpereData){
			startTimeSchedule(0,HwConstant.ACTION_TIMESHEDULE_SPEREAPPLIST);
		}else{
			startTimeSchedule(HwConstant.TIME_INTERVAL_HOUR,HwConstant.ACTION_TIMESHEDULE_SPEREAPPLIST);
		}
			}
		}.start();
	}
	
	private void upgradeTiemSchedule(){
		boolean upgradeCando = mSharePreferenceUtil.canDo(SharePreferenceUtil.FLAG_APP_UPGRADE);
		ContentManagerLog.d(TAG, "spereTimeSchedule >> upgradeCando : "+upgradeCando);
		if(upgradeCando){
			startTimeSchedule(0,HwConstant.ACTION_TIMESHEDULE_UPGRADEAPPLIST);
		}else{
			startTimeSchedule(HwConstant.TIME_INTERVAL_HOUR,HwConstant.ACTION_TIMESHEDULE_UPGRADEAPPLIST);
		}
	}
	
	public static ContentTimeTask getInstance(Context context){
		if(mContentTimeTask==null){
			mContentTimeTask = new ContentTimeTask(context);
		}
		return mContentTimeTask;
	}
	
	public void startTimeSchedule(long intervalTime,String action){
		Intent intent = new Intent(mContext,HwContentMangerReceiver.class);
		intent.setAction(action);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
		try{
			mAlarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + intervalTime , HwConstant.TIME_INTERVAL_HOUR, pendingIntent);
		}catch(Exception e){
			ContentManagerLog.d(TAG, "startTimeSchedule action : "+action+"; Exception :  "+e);
		}
	}
	
	private void cancelTimeSchedule(String action){
		Intent intent = new Intent(mContext,HwContentMangerReceiver.class);
		intent.setAction(action);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
		try{
			mAlarmManager.cancel(pendingIntent);
		}catch(Exception e){
			ContentManagerLog.d(TAG, "cancelTimeSchedule Exception :  "+e);
		}
	}
}
