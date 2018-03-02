package com.lenovo.lejingpin.hw.ui;


import com.lenovo.lejingpin.hw.content.data.DownloadAppInfo;
import com.lenovo.lejingpin.hw.content.db.HWDBUtil;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class DownloadCountReder {
	private static String TAG ="DownloadCountReder";
	private static Handler mHandler;
	
	private DownloadCountReder(){};
	static{
		mHandler = new Handler();
	}

	public interface OnDownlaodCountCommpleteListener {
		void onLoadComplete(String count,String pkName,String vCode);
	}
	
	
	public static void loadDownLoadCount(final Context context,final String appName,final String packageName,final String versionCode,final OnDownlaodCountCommpleteListener callback){
		AsyncTask.execute(new Runnable() {
			@Override
			public void run() {
				DownloadAppInfo appInfo = HWDBUtil.queryAppInfo(context, packageName, versionCode);
				if(appInfo==null){
					return;
				}
				String count = appInfo.getDownloadCount();
				int downloadCount = Integer.parseInt(count);
				String dc = "";
				int d5 = downloadCount/5000;
				int d52 = d5 / 2;
				if(d5 <=1){
					dc = "5千";
				}else if(d5 > 1 && d52 <= 1 ){
					dc = "5千+";
				}else if(d52 > 1){
					dc = d52+"万+";
				}
				dc = dc +"次下载";
				publishResult(callback,dc,packageName,versionCode);
			}
			
		});
		
	}
	private static void publishResult(final OnDownlaodCountCommpleteListener callback, final String result,final String packageName,final String versionCode){
		mHandler.post(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				callback.onLoadComplete(result,packageName,versionCode);
			}
			
		});
	}
}
