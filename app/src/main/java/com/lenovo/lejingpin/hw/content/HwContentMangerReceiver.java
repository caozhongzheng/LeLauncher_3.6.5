package com.lenovo.lejingpin.hw.content;

import java.util.HashMap;
import java.util.Map.Entry;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.lejingpin.hw.content.action.SpereAppListAction;
import com.lenovo.lejingpin.hw.content.data.HwConstant;
//import com.lenovo.lejingpin.hw.content.service.TaskService;
import com.lenovo.lejingpin.hw.content.util.SharePreferenceUtil;
//import com.lenovo.lejingpin.hw.download.DownloadHandler;
import com.lenovo.lejingpin.share.download.AppDownloadUrl;
import com.lenovo.lejingpin.share.download.DownloadConstant;
import com.lenovo.lejingpin.share.download.DownloadHandler;
import com.lenovo.lejingpin.share.service.TaskService;
//import com.lenovo.lejingpin.hw.game.widget.GameConstant;

public class HwContentMangerReceiver extends BroadcastReceiver{
	
	private static final String TAG = "HwContentMangerReceiver";

	@Override
	public void onReceive(Context context, Intent intent){
		if(intent!=null){
			HashMap<String,String> paraMap = new HashMap<String,String>();
			String action = intent.getAction();
			Log.d(TAG, " com.lenovo.launcher onReceive >> action : "+action);
			if(HwConstant.ACTION_REQUEST_APP_DOWNLOAD.equals(action)){
				Log.i("yangmao_download", "onreceive get download request");
				String package_name = intent.getStringExtra("package_name");
				String version_code = intent.getStringExtra("version_code");
				String app_name = intent.getStringExtra("app_name");
				int category = intent.getIntExtra("category", DownloadConstant.CATEGORY_ERROR);				
				String icon_url = intent.getStringExtra("app_iconurl");				
				String version_name = intent.getStringExtra("version_name");				
				String download_url = intent.getStringExtra("download_url");

//				paraMap.put("type", HwConstant.TYPE_DOWNLOAD_ACTION);
//				paraMap.put("package_name", package_name);
//				paraMap.put("version_code",version_code);
//				paraMap.put("app_name", app_name);				
//				paraMap.put("download_url", download_url);
//				paraMap.put("version_name", version_name);
//				paraMap.put("app_iconurl", icon_url);
//				paraMap.put("category", String.valueOf(category));
				AppDownloadUrl downurl = new AppDownloadUrl();
				if (download_url != null) {
					downurl.setDownurl(download_url);
				} else {
					downurl.setDownurl(DownloadConstant.TYPE_DOWNLOAD_ACTION);
				}
				downurl.setPackage_name(package_name);
				downurl.setVersionName(version_name);
				downurl.setVersion_code(version_code);
				downurl.setApp_name(app_name);
				downurl.setIconUrl(icon_url);
				downurl.setCategory(category);
				//downurl.setCallback(downurl.getCallback());
				if (DownloadConstant.CATEGORY_WALLPAPER == category) {
					downurl.setMimeType(DownloadConstant.MIMETYPE_WALLPAPER);
				} else {
					downurl.setMimeType(DownloadConstant.MIMETYPE_APK);
				}
				sendMessage(DownloadHandler.getInstance(context),
						DownloadConstant.MSG_DOWN_LOAD_URL, downurl);
				
				//startService(context,paraMap);
			}else if(HwConstant.ACTION_REQUEST_APP_INFO.equals(action)){
				String package_name = intent.getStringExtra("package_name");
				String version_code = intent.getStringExtra("version_code");
				paraMap.put("type", HwConstant.TYPE_APPINFO_ACTION);
				paraMap.put("package_name", package_name);
				paraMap.put("version_code",version_code);
				paraMap.put("action",action);
				startService(context,paraMap);
			}else if(HwConstant.ACTION_TIMESHEDULE_SPEREAPPLIST.equals(action)){
				paraMap.put("type", HwConstant.TYPE_SPERE_ACTION);
				startService(context,paraMap);
			}else if(HwConstant.ACTION_REQUEST_APP_COMMON_LIST.equals(action)){
				String package_name = intent.getStringExtra("package_name");
				String version_code = intent.getStringExtra("version_code");
				paraMap.put("type", HwConstant.TYPE_COMMON_LIST_ACTION);
				paraMap.put("package_name", package_name);
				paraMap.put("version_code",version_code);
				startService(context,paraMap);
/*
			}else if(GameConstant.ACTION_GAME_APP_REQUEST.equals(action)){
				paraMap.put("type", HwConstant.TYPE_GAME_APP_LIST);
				startService(context,paraMap);
*/
			}
			//yangmao add for search_move 1225 start
			else if (HwConstant.ACTION_HAWAII_SEARCH_APP_INFO.equals(action)){
				//------Get Hawaii search
				Log.i(TAG, "HwContentMangerReceiver getaction");
				String package_name = intent.getStringExtra("package_name");
				String version_code = intent.getStringExtra("version_code");
				paraMap.put("type", HwConstant.TYPE_HAWAII_SEARCH_APPINFO_ACTION);
				paraMap.put("package_name", package_name);
				paraMap.put("version_code",version_code);
				paraMap.put("action",action);
				startService(context,paraMap);
			}else if(HwConstant.ACTION_REQUEST_HAWAII_SEARCH_APP_COMMENT_LIST.equals(action)){
				//------Get App Comment------
				String package_name = intent.getStringExtra("package_name");
				String version_code = intent.getStringExtra("version_code");
				paraMap.put("type", HwConstant.TYPE_HAWAII_SEARCH_COMMENTLIST_ACTION);
				paraMap.put("package_name", package_name);
				paraMap.put("version_code",version_code);
				startService(context,paraMap);
			}
			//yangmao add for search_move 1225 end
		}
	}
	private void startService(Context context,HashMap<String,String> map){
		Intent intent = new Intent(context,TaskService.class);
		if(map!=null && !map.isEmpty()){
			for(Entry<String, String> entry : map.entrySet()){
				String key = entry.getKey();
				String value = entry.getValue();
				intent.setPackage(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF);
				if("type".equals(key)){
					intent.setType(value);
				}else if("category".equals(key)){
					intent.putExtra(key, Integer.valueOf(value));
				}else{
					intent.putExtra(key, value);
				}
			}
		}
		context.startService(intent);
	}
	
	
	private void sendMessage(Handler handler, int what, Object obj) {
		if (handler != null) {
			Message msg = new Message();
			msg.obj = obj;
			msg.what = what;
			handler.sendMessage(msg);
		}
	}
	
}
