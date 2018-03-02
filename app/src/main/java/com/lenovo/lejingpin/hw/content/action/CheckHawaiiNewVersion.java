package com.lenovo.lejingpin.hw.content.action;

import android.content.Context;
import android.util.Log;


import com.lenovo.lejingpin.hw.content.util.SharePreferenceUtil;
import com.lenovo.lejingpin.share.service.TaskService.Action;

@Deprecated
public class CheckHawaiiNewVersion implements Action{
	
	private String TAG ="CheckHawaiiNewVersion";
	
//	private Context mContext;
	private static CheckHawaiiNewVersion mInstance;
	
	private CheckHawaiiNewVersion(){}
	
	private CheckHawaiiNewVersion(Context context) {
//		mContext = context;
	}
	public static CheckHawaiiNewVersion getInstance(){
		if(mInstance==null){
			mInstance = new CheckHawaiiNewVersion();
		}
		return mInstance;
	}

	@Override
	public void doAction(Context context) {
		boolean check = SharePreferenceUtil.getInstance(context).checkNewVersion();
		//TODO
		Log.d(TAG, "CheckHawaiiNewVersion >>> check : "+check);
//		if(false){
////			UpdateNewVersion.getInstance(mContext).upGrade();
//		}
	}

}
