package com.lenovo.lejingpin.hw.ui;


import com.lenovo.lejingpin.share.download.DownloadInfo;
import com.lenovo.lejingpin.share.download.LDownloadManager;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class DownloadStatusReder {
	private static String TAG ="DownloadStatusReder";
	private static Handler mHandler;
	
	private DownloadStatusReder(){};
	static{
		mHandler = new Handler();
	}

	public interface OnDownlaodStatusCommpleteListener {
		void onLoadComplete(DownloadInfo info,String pkName,String vCode);
	}
	
	
	public static void loadDownLoadStatus(final Context context,final String packageName,final String versionCode,final OnDownlaodStatusCommpleteListener callback){
		AsyncTask.execute(new Runnable() {
			@Override
			public void run() {
				DownloadInfo d = new DownloadInfo();
				d.setPackageName(packageName);
				d.setVersionCode(versionCode);
				LDownloadManager manager = LDownloadManager.getDefaultInstance(context);
				if(manager!=null){
					publishResult(callback,manager.getDownloadInfo(d),packageName,versionCode);
				}else{
					Log.d(TAG, "get DownloadInfo error .");
				}
			}
			
		});
		
	}
	private static void publishResult(final OnDownlaodStatusCommpleteListener callback, final DownloadInfo info,final String packageName,final String versionCode){
		mHandler.post(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				callback.onLoadComplete(info,packageName,versionCode);
			}
			
		});
	}
}
