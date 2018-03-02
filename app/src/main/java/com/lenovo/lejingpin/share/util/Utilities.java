package com.lenovo.lejingpin.share.util;

import java.util.ArrayList;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.commonui.LeAlertDialog;


public final class Utilities {
//	public static boolean mIsMainActivityActive = false;
//	public static boolean mDownloadServiceActive = false;
//	public static boolean mIServiceActive = false;
	private static ArrayList<String> mActiveList = new ArrayList<String>();
	
	private static final String TAG = "Utilities";
	
	private Utilities() {
	}
	
	public static synchronized void setDownloadActive(boolean active, String classname){
		if(!active){
			mActiveList.remove(classname);
			return;
		}else if(!mActiveList.contains(classname)){
			mActiveList.add(classname);
		}
	}
	public static synchronized boolean killLejingpinProcessNow(){
                if(0 != mActiveList.size()){
                        return false;
          }
            new Thread(new Runnable() {
                public void run() {
                        Log.d("PAD1021","11111111111killLejingpinProsess killing");
                        if(0 != mActiveList.size())
                                return;
	        	synchronized(this){
		        	try {
						wait(1500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	        	}
                        Log.d("PAD1021"," now 222222222killLejingpinProsess killing");
                        android.os.Process.killProcess(android.os.Process.myPid());
                }
            }).start();

                return true;
        }


	public static synchronized boolean killLejingpinProcess(){
//		Log.d(TAG,"killLejingpinProsess start , mIsMainActivityActive:" + mIsMainActivityActive
//				+ ",mDownloadServiceActive:" + mDownloadServiceActive);
//		if(mIsMainActivityActive)
//			return false;
//		if(mDownloadServiceActive)
//			return false;
		if(0 != mActiveList.size())
			return false;
//		if(mIServiceActive)
//			return false;
		
	    new Thread(new Runnable() {
	        public void run() {
	        	Log.d(TAG,"killLejingpinProsess killing");
	        	synchronized(this){
		        	try {
						wait(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	        	}
//	    		if(mIsMainActivityActive)
//	    			return;
//	    		if(mDownloadServiceActive)
//	    			return;
	    		if(0 != mActiveList.size())
	    			return;
	    		android.os.Process.killProcess(android.os.Process.myPid());
	        }
	    }).start();

		return true;
	}

	public static void showConfirmDownloadDialog(final Context context,
			DialogInterface.OnClickListener Oklistener,DialogInterface.OnClickListener cancellistener){
	    LeAlertDialog dialog = new LeAlertDialog(context, R.style.Theme_LeLauncher_Dialog_Shortcut);
	    dialog.setLeTitle(R.string.letongbu_install_dialog_title);
	    dialog.setLeMessage(context.getText(R.string.d_wifi_m));
	    if(cancellistener == null){
		    dialog.setLeNegativeButton(context.getText(R.string.cancel_action), new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		                dialog.dismiss();
		        }
		    });
	    }else{
	    	dialog.setLeNegativeButton(context.getText(R.string.cancel_action), cancellistener);
	    }

	    if(Oklistener == null){
	    	dialog.setLePositiveButton(context.getText(R.string.rename_action), new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
	                dialog.dismiss();
		        }
		    });
	    }else{
	    	dialog.setLePositiveButton(context.getText(R.string.rename_action), Oklistener);
	    }
	    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
	        @Override
	        public void onCancel(DialogInterface dialog) {
	                dialog.dismiss();
	        }
	    });
	    dialog.show();
	}

}
