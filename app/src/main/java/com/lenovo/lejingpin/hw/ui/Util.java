package com.lenovo.lejingpin.hw.ui;

import java.io.File;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.commonui.LeAlertDialog;
import com.lenovo.launcher2.customizer.SettingsValue;

import com.lenovo.lejingpin.share.download.DownloadConstant;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class Util {
	
	
	private static Util mInstance;
//	private Context mContext;
	private Util(){}
	private Util(Context context){
//		mContext = context;
	}
	public  static Util getInstance(){
		if(mInstance==null){
			mInstance = new Util();
		}
		return mInstance;
	}
	
    public boolean isNetworkEnabled(Context context){
         if(SettingsValue.isWifiProduct()){
                    return true;
                }

        SharedPreferences sp = context.getSharedPreferences(SettingsValue.PERFERENCE_NAME, 4);
        boolean isNetworkEnabled = sp.getBoolean(SettingsValue.PREF_NETWORK_ENABLER, true);
        return isNetworkEnabled;
    }
    
    public static boolean isMobileNetWork(Context context){
    	return (DownloadConstant.CONNECT_TYPE_MOBILE == DownloadConstant.getConnectType(context));
    }
    
    public static void showDeleDownloadDialog(final Context context,String appName,
			DialogInterface.OnClickListener Oklistener,DialogInterface.OnClickListener cancellistener){
	    LeAlertDialog dialog = new LeAlertDialog(context, R.style.Theme_LeLauncher_Dialog_Shortcut);
	    dialog.setLeTitle(R.string.ljp_delete_download_task_title);
	    dialog.setLeMessage(context.getString(R.string.ljp_delete_download_task_msg,appName));
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
    
    
    public static String getConnectType(Context context) {
		ConnectivityManager mConnMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = mConnMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo infoM = mConnMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (info != null && info.isConnected()) {
			return "wifi";
		} else if (infoM != null && infoM.isConnected()) {
			return "mobile";
		}
		return "other";
	}
    
    private boolean isSDCardAvailable(){
    	return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }
    
    private  long getAvailableExternalMemorySize() {
        if (isSDCardAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            
            return availableBlocks * blockSize;
        } else {
            return -1;
        }
    }
    
    
    public boolean spaceIsEnough(long appSize){
    	
    	long extSize = 10 * 1024 * 1024;
    	
    	long availableSize = getAvailableExternalMemorySize();
    	Log.e("HwPushAppFragment","spaceIsEnough >> appSize : "+appSize+" ; availableSize : "+(availableSize+extSize));
    	return availableSize + extSize > appSize ? true : false;
    }
    
    

}
