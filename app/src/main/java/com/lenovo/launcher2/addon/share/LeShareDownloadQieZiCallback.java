package com.lenovo.launcher2.addon.share;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.lenovo.lejingpin.share.download.AppDownloadUrl.Callback;
import com.lenovo.lejingpin.share.download.DownloadInfo;

public class LeShareDownloadQieZiCallback implements Callback{
	
	private String TAG = "LeShareDownloadQieZiCallback";
	
	private Context mContext;
	
	public LeShareDownloadQieZiCallback(Context context){
		mContext = context;
	}

	@Override
	public void doCallback(DownloadInfo dInfo){
		Log.d(TAG, "doCallback >>  dInfo : "+dInfo);
		if(dInfo!=null){
			String pkg = dInfo.getPackageName();
			String vcode = dInfo.getVersionCode();
			Log.d(TAG, "doCallback >>  pkg : "+pkg+" ; vcode : "+vcode+" ; progress : "+dInfo.getProgress()+" ; ");
			if(!TextUtils.isEmpty(pkg) && pkg.equals("com.lenovo.anyshare") && !TextUtils.isEmpty(vcode) && vcode.equals("1000")){
				Log.d(TAG,"doCallback >> send message : ");
				Intent intent = new Intent();
				intent.setAction(LeShareConstant.ACTION_DOWNLOAD_QIEZI_PROGRESS);
				intent.putExtra("progress", dInfo.getProgress());
				mContext.sendBroadcast(intent);
			}
		}
	}
}
