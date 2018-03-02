package com.lenovo.lejingpin.network;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class DeviceInfo {
	private Context mContext;
	private static DeviceInfo mDeviceInfo = null;
	private static DisplayMetrics mDispalyMetrics;
	private DeviceInfo(){}
	private DeviceInfo(Context context){
		mContext =  context;
	}
	
	public synchronized static  DeviceInfo getInstance(Context context){
		if(mDeviceInfo==null){
			mDeviceInfo = new DeviceInfo(context);
			mDispalyMetrics = new DisplayMetrics();
			WindowManager wm = (WindowManager)context.getApplicationContext().getSystemService("window");
			wm.getDefaultDisplay().getMetrics(mDispalyMetrics);
		}
		return mDeviceInfo;
	}
	
	public int getWidthPixels(){
		if(mDispalyMetrics!=null){
			return mDispalyMetrics.widthPixels;
		}else{
			return 480;
		}
	}
	
	public int getHeightPixels(){
		if(mDispalyMetrics!=null){
			return mDispalyMetrics.heightPixels;
		}else{
			return 800;
		}
	}

    public int getDensityDpi() {
        if (mDispalyMetrics != null) {
            return mDispalyMetrics.densityDpi;
        } else {
            return 240;
        }
    }

}
