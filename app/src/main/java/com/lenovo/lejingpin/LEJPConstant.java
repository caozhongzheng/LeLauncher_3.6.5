/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.lenovo.lejingpin;


import android.graphics.drawable.Drawable;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import com.lenovo.lsf.util.PsDeviceInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import com.lenovo.lejingpin.network.AmsRequest;
import com.lenovo.lejingpin.network.AmsSession;
import com.lenovo.lejingpin.network.AppInfoRequest5;
import com.lenovo.lejingpin.network.DeviceInfo;
import com.lenovo.lejingpin.network.AmsSession.AmsSessionCallback;
import com.lenovo.lejingpin.network.AppInfoRequest5.AppInfo;
import com.lenovo.lejingpin.network.AppInfoRequest5.AppInfoResponse5;
import com.lenovo.lejingpin.network.WallpaperResponse.ApplicationData;
import com.lenovo.lejingpin.network.AmsApplication;
import com.lenovo.lejingpin.settings.LejingpingSettingsValues;
import com.lenovo.lejingpin.share.download.DownloadConstant;
import com.lenovo.lejingpin.hw.content.data.HwConstant;


public class LEJPConstant {

    static final String TAG = "LEJPConstant";
    
    static LEJPConstant sLejpConstant;
    public  ArrayList<ApplicationData> mServiceWallPaperDataList = null; 
    public  ArrayList<AmsApplication> mServiceThemeAmsDataList = null; 
    public  ArrayList<AmsApplication> mServiceLockAmsDataList = null; 

    public  ArrayList<ApplicationData> mServiceLocalWallPaperDataList = null; 
    public  ArrayList<AmsApplication> mServiceLocalThemeAmsDataList = null; 
    public  ArrayList<AmsApplication> mServiceLocalLockAmsDataList = null; 

    public  String APK_INSTALL_ACTION  = "APK_INSTALL_ACTION";
    public  String APK_UNINSTALL_ACTION  = "APK_UNINSTALL_ACTION"; 
    public  String WALLPAPER_CHANGED_ACTION = "com.lenovo.launcher.action.SET_WALLPAPER";
    public  String LETHEME_APPLING_ACTION = "action.letheme.appling";
    public  String LETHEME_PACKAGE_NAME = "theme_value";
    public  String mInstalledApkPackageName = null; 
    public boolean mWallpapaerNeedRefresh = false;
    public boolean mThemeNeedRefresh = false;
    public boolean mLockscreenNeedRefresh = false;
    public String mCurrentWallpaper = "";  //wall paper
    public String mCurrentTheme = "";      //theme
    public String mCurrentLockscreen = ""; //lockscreen
    public boolean mIsWallpaperDeleteFlag = false;
    public boolean mIsThemeDeleteFlag = false;
    public boolean mIsLockDeleteFlag = false;
    public boolean mLemActivityonResumeFlag = false; //DetialActivity status
    public boolean mLeMainActivityonResumeFlag = false; //ClassicFragmentActivity status

    public boolean mIsThemeAPKUninstallFlag = false;
    public boolean mIsLockAPKUninstallFlag = false;
    public boolean mIsClickOtherFlag = false;
    public int mOtherWallpaperOrientation = -1;

    private boolean mIsNeedConfirmDownload = true;
    
    public static synchronized LEJPConstant getInstance() {
        if ( sLejpConstant == null) {
            sLejpConstant = new LEJPConstant();
        }
        return sLejpConstant;
    }
    public LEJPConstant() {
    }
    
	public static String getDownloadPath(){
		String path = Environment.getExternalStorageDirectory() + "/.IdeaDesktop/LeJingpin";
//		checkImageHide(path);
		return path;
	}
	
/*	public static void checkImageHide(String path){
		File file = new File(path);
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(file.isDirectory()){
			String nomedia = path + "/.nomedia";
			File f = new File(nomedia);
			if(!f.exists()){
				try {
					f.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}*/
	
    public boolean isNeedConfirmDownload(Context context){
    	if(DownloadConstant.CONNECT_TYPE_MOBILE == DownloadConstant.getConnectType(context)){
    		return LejingpingSettingsValues.wlanDownloadValue(context);
    	}
    	return false;
    }
    
    public void setConfirmDownloadFlag(boolean flag){
    	mIsNeedConfirmDownload = flag;
    }
    
    public String getPostRegist(Context mContext) {
                // TODO Auto-generated method stub
        DeviceInfo deviceInfo = DeviceInfo.getInstance(mContext);
        String post = null;
/*
        try {
            post = new JSONStringer().object()
                    .key("deviceManufacturer").value("lenovo")
                    .key("deviceBrand").value(android.os.Build.BRAND)
                    .key("deviceModel").value(android.os.Build.MODEL)
                    .key("lang").value("zh-CN")
                    .key("os").value("android")
                    .key("osVersion").value(android.os.Build.VERSION.RELEASE)
                    .key("sdkVersion").value(android.os.Build.VERSION.SDK)
                    .key("horizontalResolution").value(deviceInfo.getWidthPixels())
                    .key("verticalResolution").value(deviceInfo.getHeightPixels())
                    .key("dpi").value(deviceInfo.getDensityDpi())
                    .key("deviceIdType").value("imei")
                    .key("deviceId").value(PsDeviceInfo.getDeviceId(mContext))
                    .key("clientVersion").value(PsDeviceInfo.getAppstoreVersion(mContext))
                    .key("packageName").value(PsDeviceInfo.getSource(mContext))
                    .endObject().toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }
*/
        try{
          String model = android.os.Build.MODEL;
          model = model.replace(" ","");
          post = new String(
          "deviceManufacturer=lenovo&deviceBrand="+android.os.Build.BRAND+
          "&deviceModel="+model+
          "&lang=zh-CN"+
          "&os=android"+
          "&osVersion="+android.os.Build.VERSION.RELEASE+
          "&sdkVersion="+android.os.Build.VERSION.SDK+
          "&horizontalResolution="+String.valueOf(deviceInfo.getWidthPixels())+
          "&verticalResolution="+String.valueOf(deviceInfo.getHeightPixels())+
          "&dpi="+String.valueOf(deviceInfo.getDensityDpi())+
          "&deviceIdType=imei"+
          "&deviceId="+PsDeviceInfo.getDeviceId(mContext)+
          "&clientVersion="+PsDeviceInfo.getAppstoreVersion(mContext)+
          "&packageName="+PsDeviceInfo.getSource(mContext));
        Log.i("zdx", "post = " + post);
           }catch(Exception e){
           }
        return post;
        }

    public void requestAppInfo(final Context context,final String pkgname,final String vercode,
    		final int type,final int pos){
        DeviceInfo deviceInfo = DeviceInfo.getInstance(context);
        AmsSession.init(context, new AmsSessionCallback(){
        public void onResult(AmsRequest request, int code, byte[] bytes) {
                Log.i(TAG,"UpgradeAppInfoAction.requestAppInfo, AmsSession.init >> result code:"+ code);
                if(code!=200){
                	//sendIntentForAppInfoFinished(mContext,false,mPackageName,mVersionCode);
                }else{
                	getAppInfo(context,pkgname,vercode,type,pos);
                }
        	}
        },deviceInfo.getWidthPixels(),deviceInfo.getHeightPixels(), deviceInfo.getDensityDpi());
    }
    private void getAppInfo(final Context context,final String package_name,final String version_code,
    		final int type,final int pos){
        AppInfoRequest5 infoRequest = new AppInfoRequest5(context);
        infoRequest.setData(package_name, version_code);
        AmsSession.execute(context, infoRequest, new AmsSessionCallback(){
            public void onResult(AmsRequest request, int code, byte[] bytes) {
            Log.i(TAG,"UpgradeAppInfoAction.getAppInfo,AmsSession.execute >> result code:"+ code);
            boolean success= false;
                if( code == 200 ){
                    success = true;
                    if(bytes != null) {
                        AppInfoResponse5 infoResponse = new AppInfoResponse5();
                        infoResponse.parseFrom(bytes);
                        boolean successResponse= infoResponse.getIsSuccess();
                        Log.i(TAG,"UpgradeAppInfoAction.getAppInfo >> response success : :"+ successResponse);
                        if(successResponse){
                            AppInfo responseApp = infoResponse.getAppInfo();
                            final String firstSnapPath = responseApp.getSnapList().toString().replace("[", "").replace("]", "");
                            Intent intent = new Intent(HwConstant.ACTION_DETAIL_PREVIEW_URL_DOWNLOAD);
                            intent.putExtra("firstSnapPath", firstSnapPath);
                            intent.putExtra("type", type);
                            intent.putExtra("pos", pos);
                            context.sendBroadcast(intent);
                        }
                    }
                }
            }
        });
    }
    public interface ImageCallback {  
        public void imageLoaded(Drawable imageDrawable, String imageUrl);  
    } 
}
