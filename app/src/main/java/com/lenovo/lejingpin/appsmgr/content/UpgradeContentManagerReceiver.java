package com.lenovo.lejingpin.appsmgr.content;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map.Entry;

import com.lenovo.lejingpin.appsmgr.content.action.UpgradeAppListAction;
import com.lenovo.lejingpin.share.download.DownloadConstant;
import com.lenovo.lejingpin.share.service.TaskService;
import com.lenovo.lejingpin.share.service.TaskService.Action;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.lenovo.launcher.R;

public class UpgradeContentManagerReceiver extends BroadcastReceiver {

	private String TAG = "xujing3";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "UpgradeContentMangerReceiver.onReceive start ");
		// Intent intentSend = new Intent(context,TaskService.class);

		if (intent != null) {
			HashMap<String, String> paraMap = new HashMap<String, String>();
			String action = intent.getAction();
			Log.i(TAG, "UpgradeContentMangerReceiver.onReceive >> action : "
					+ action);
			if (UpgradeUtil.ACTION_REQUEST_APPUPGRADE.equals(action)) {
				Log.d(TAG,"ACTION_REQUEST_APPUPGRADE------------------------------------------");
				paraMap.put("type", UpgradeUtil.TYPE_APPUPGRADE_ACTION);

			} else if (UpgradeUtil.ACTION_REQUEST_APPUPGRADE_APPINFO
					.equals(action)) {
				// ------Get Upgrade App Info------
				// String package_name = intent.getStringExtra("package_name");
				// String version_code = intent.getStringExtra("version_code");
				// paraMap.put("type",
				// UpgradeUtil.TYPE_APPUPGRADE_APPINFO_ACTION);
				// paraMap.put("package_name", package_name);
				// paraMap.put("version_code",version_code);
				// paraMap.put("action",action);
				// startService(context,paraMap);
			} 
			else if (DownloadConstant.ACTION_REQUEST_APPDOWNLOAD
					.equals(action)) {
				// ------App Download------
				String app_name = intent.getStringExtra("app_name");
				String toast = context.getString(R.string.add_downloadqueue,
						app_name);
				Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
				String package_name = intent.getStringExtra("package_name");
				String version_code = intent.getStringExtra("version_code");
				String app_iconurl = intent.getStringExtra("app_iconurl");
				int category = intent.getIntExtra("category",DownloadConstant.CATEGORY_ERROR);
				String version_name = intent.getStringExtra("version_name");
				paraMap.put("type", DownloadConstant.TYPE_DOWNLOAD_ACTION);
				paraMap.put("package_name", package_name);
				paraMap.put("version_code", version_code);
				paraMap.put("app_name", app_name);
				paraMap.put("app_iconurl", app_iconurl);
				paraMap.put("category", String.valueOf(category));
				paraMap.put("version_name", version_name);
			}
			startService(context, paraMap);
		}
	}

	private void startService(Context context, HashMap<String, String> map) {
		Intent intent = new Intent(context, TaskService.class);
		if (map != null && !map.isEmpty()) {
			for (Entry<String, String> entry : map.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				if ("type".equals(key)) {
					intent.setType(value);
				}else if("category".equals(key)){
					intent.putExtra(key, Integer.valueOf(value));
				}else {
					intent.putExtra(key, value);
				}
			}
		}
		Log.d(TAG,"startService TaskService------------------------------------------");
		context.startService(intent);
	}

}
