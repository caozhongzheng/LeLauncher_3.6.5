package com.lenovo.lejingpin.hw.ui;



import com.lenovo.lejingpin.share.download.DownloadInfo;
import com.lenovo.lejingpin.share.download.LDownloadManager;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

public class AppDownloadInfoReader {
	private static String TAG ="AppDownloadInfoReader";
	private static Handler mHandler;
	
	private AppDownloadInfoReader(){};
	static{
		mHandler = new Handler();
	}
	
	public interface OnDownlaodInfoCommpleteListener {
		void onLoadComplete(DownloadInfo info,TextView v);
	}
	
	public static void loadDownloadInfo(final Context context,final String packageName,final String versionCode,final TextView v,final OnDownlaodInfoCommpleteListener listener){
		AsyncTask.execute(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				DownloadInfo info = LDownloadManager.getDefaultInstance(context).getDownloadInfo(new DownloadInfo(packageName, versionCode));
				publishResult(listener,info,v);
			}
			
		});
	}
	private static void publishResult(final OnDownlaodInfoCommpleteListener callback, final DownloadInfo info,final TextView v){
		mHandler.post(new Runnable(){
			@Override
			public void run() {
				callback.onLoadComplete(info,v);
			}
		});
	}
}
