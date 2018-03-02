package com.lenovo.lejingpin.share.service;

//import com.lenovo.lejingpin.hw.content.action.AppInfoAction;
//import com.lenovo.lejingpin.hw.content.action.SpecialAppListAction;
//import com.lenovo.lejingpin.hw.content.data.HwConstant;
//import com.lenovo.lejingpin.hw.download.DownloadHandler;

import com.lenovo.lejingpin.appsmgr.content.UpgradeUtil;
import com.lenovo.lejingpin.appsmgr.content.action.UpgradeAppListAction;
import com.lenovo.lejingpin.hw.content.action.AppInfoAction5;
import com.lenovo.lejingpin.hw.content.action.CommonListAction5;

import com.lenovo.lejingpin.hw.content.action.GameAppListAction;
import com.lenovo.lejingpin.hw.content.action.SpereAppListAction5;
import com.lenovo.lejingpin.hw.content.data.HwConstant;
import com.lenovo.lejingpin.share.download.AppDownloadUrl;
import com.lenovo.lejingpin.share.download.DownloadConstant;
import com.lenovo.lejingpin.share.download.DownloadHandler;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class TaskService extends ConcurrentIntentService {
	private String TAG = "xujing3";

	@Override
	public void onCreate() {
		super.onCreate();
	}

	public interface Action {
		void doAction(Context context);
	}

	private Action getInstantce(Intent intent) {
		if (intent != null) {
			String type = intent.getType();

			if (UpgradeUtil.TYPE_APPUPGRADE_ACTION.equals(type)) {
				return UpgradeAppListAction.getInstance();

			} else if (UpgradeUtil.TYPE_APPUPGRADE_APPINFO_ACTION.equals(type)) {

			}
		}

		return null;
	}

	private void doUpgradeAppListAction(Intent intent) {
		UpgradeAppListAction.getInstance().doAction(this);
	}

	private void doDownloadAction(Intent intent) {
		String package_name = intent.getStringExtra("package_name");
		String version_code = intent.getStringExtra("version_code");
		String app_name = intent.getStringExtra("app_name");
		String app_iconurl = intent.getStringExtra("app_iconurl");
		String version_name = intent.getStringExtra("version_name");
		int category = intent.getIntExtra("category", DownloadConstant.CATEGORY_ERROR);
		String downloadUrl = intent.getStringExtra("download_url");
		Log.i("yangmao_0218", "download_url is:"+downloadUrl+";category is:"+category);
		// DownloadAppAction.getInstance(this, app_name, package_name,
		// version_code ,
		// app_iconurl, category, DownloadHandler.getInstance(this)).doAction();
		AppDownloadUrl downurl = new AppDownloadUrl();
		if(downloadUrl!=null ){
			downurl.setDownurl(downloadUrl);
		}else{
			downurl.setDownurl(DownloadConstant.TYPE_DOWNLOAD_ACTION);
		}
		downurl.setVersionName(version_name);
		downurl.setPackage_name(package_name);
		downurl.setVersion_code(version_code);
		downurl.setApp_name(app_name);
		downurl.setIconUrl(app_iconurl);
		downurl.setCategory(category);
		if (DownloadConstant.CATEGORY_WALLPAPER == category) {
//		if (category.equals(DownloadConstant.CATEGORY_WALLPAPER_STRING)) {
			downurl.setMimeType(DownloadConstant.MIMETYPE_WALLPAPER);
		} else {
			downurl.setMimeType(DownloadConstant.MIMETYPE_APK);
		}
		sendMessage(DownloadHandler.getInstance(this),
				DownloadConstant.MSG_DOWN_LOAD_URL, downurl);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG,"onHandleIntent -------------");
		if (intent != null) {
			String type = intent.getType();
			Log.d(TAG,"onHandleIntent --- TYPE:"+ type);
			if (UpgradeUtil.TYPE_APPUPGRADE_ACTION.equals(type)) {
				doUpgradeAppListAction(intent);

			} else if (UpgradeUtil.TYPE_APPUPGRADE_APPINFO_ACTION.equals(type)) {

			} else if (DownloadConstant.TYPE_DOWNLOAD_ACTION.equals(type)) {
				doDownloadAction(intent);
			}

			if (HwConstant.TYPE_APPINFO_ACTION.equals(type)) {
				String package_name = intent.getStringExtra("package_name");
				String version_code = intent.getStringExtra("version_code");
				String action = intent.getStringExtra("action");
				AppInfoAction5.getInstance( package_name, version_code,action).doAction(this);
			}

			else if (HwConstant.TYPE_SPERE_ACTION.equals(type)) {
				SpereAppListAction5.getInstance().doAction(this);
			} else if (HwConstant.TYPE_COMMON_LIST_ACTION.equals(type)) {
				String package_name = intent.getStringExtra("package_name");
				String version_code = intent.getStringExtra("version_code");
				CommonListAction5.getInstance(package_name, version_code).doAction(this);
			}

			// yangmao add search_move 1225 start

			else if (HwConstant.TYPE_HAWAII_SEARCH_APPINFO_ACTION.equals(type)) {
				// ------get Hawaii Search app info
				Log.i(TAG, "TaskService get TYPE_HAWAII_SEARCH_APPINFO_ACTION");
				String package_name = intent.getStringExtra("package_name");
				String version_code = intent.getStringExtra("version_code");
				String action = intent.getStringExtra("action");
				AppInfoAction5.getInstance(package_name, version_code,action).doAction_hawaiiSearch(this);
			}

			else if (HwConstant.TYPE_HAWAII_SEARCH_COMMENTLIST_ACTION
					.equals(type)) {
				String package_name = intent.getStringExtra("package_name");
				String version_code = intent.getStringExtra("version_code");
				CommonListAction5.getInstance(package_name, version_code).doAction_hawaii_search(this);
			}else if(HwConstant.TYPE_GAME_APP_LIST.equals(type)){
				GameAppListAction.getInstance().doAction(this);
			}
			// yangmao add search_move 1225 start

		}
		
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
