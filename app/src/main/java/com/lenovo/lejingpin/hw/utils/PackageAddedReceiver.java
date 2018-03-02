package com.lenovo.lejingpin.hw.utils;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.lenovo.lejingpin.hw.content.data.HwConstant;
//import com.lenovo.lejingpin.hw.download.DownloadHelpers;
//import com.lenovo.lejingpin.hw.download.DownloadInfo;
//import com.lenovo.lejingpin.hw.download.Downloads;
import com.lenovo.lejingpin.share.download.DownloadConstant;
import com.lenovo.lejingpin.share.download.DownloadHelpers;
import com.lenovo.lejingpin.share.download.DownloadInfo;
import com.lenovo.lejingpin.share.download.Downloads;
import com.lenovo.lejingpin.share.download.LDownloadManager;

public class PackageAddedReceiver extends BroadcastReceiver{
	private static String TAG = "PackageAddedReceiver";
	@Override
	public void onReceive(Context context, Intent intent){
		String action = intent.getAction();
		Log.d(TAG, "PackageAddedReceiver >> action : "+action);
		if ( Intent.ACTION_PACKAGE_ADDED.equals(action) ) {
			String pkgName = intent.getData().getSchemeSpecificPart();
			PackageManager pm = context.getPackageManager();
			int vsCode = -1;
			try {
				vsCode = pm.getPackageInfo(pkgName, 0).versionCode;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			Intent addPackage = new Intent(HwConstant.ACTION_PACKAGE_ADDED);
			addPackage.putExtra("pkname", pkgName);
			addPackage.putExtra("vcode", vsCode);
			context.sendBroadcast(addPackage);
			Cursor cursor = null ;
			try {
				//Uri downloadUri = Uri.parse("content://com.lenovo.lejingpin.hw.content.download/download");
				Uri downloadUri = Downloads.CONTENT_URI;
				cursor = context.getContentResolver().query(downloadUri, 
						null, "pkgname = ? and versioncode = ?", new String[] { pkgName, String.valueOf(vsCode) }, null);

				cursor.moveToFirst();
				int _id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
				int category = cursor.getInt(cursor.getColumnIndexOrThrow("category"));
				Log.i(TAG, "category is:" + category);
				if(DownloadConstant.getDownloadCategory(category) ==DownloadConstant.CATEGORY_RECOMMEND_APP){
					//this notifyDeleteId method is not use now,and the download will control it by self,it 
					//is not necessary to delete by app-self
					//DownloadHelpers.notifyDeleteId(context, _id+"");
					DownloadInfo info = new DownloadInfo();
					info.setPackageName(pkgName);
					info.setVersionCode(String.valueOf(vsCode));
					LDownloadManager.getDefaultInstance(context).deleteTask(info);
				}else{
					Log.i(TAG, "search apk install");
//					DownloadInfo downloadInfo = new DownloadInfo();
//					downloadInfo.setPackageName(pkgName);
//					String updateStatus = null;
//					updateStatus = String.valueOf(Downloads.STATUS_INSTALL);
//					ContentValues values = new ContentValues();
//					values.put(Downloads.Impl.COLUMN_STATUS, updateStatus );
//					Uri urid = ContentUris.withAppendedId(Downloads.Impl.CONTENT_URI,_id);
//					context.getContentResolver().update(urid, values, "_ID = ?", new String[]{_id+""});
//					notifyInstalledOrUninstalled(context, pkgName, String.valueOf(vsCode), true);
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				Log.d(TAG, "ACTION_PACKAGE_ADDED finally......");
				if(cursor != null && !cursor.isClosed()){
					cursor.close();
				}
			}

		}
	}
	
		private static void notifyInstalledOrUninstalled(Context context, String packageName, String versionCode, boolean bInstalled){
			//Log.i("zdx","    DownloadHandler.notifyInstalledOrUninstalled(), installed:"+ bInstalled);
			Log.i(TAG, "packageAddedReceiver notifyInstalledOrUninstalled");
			Intent intent = new Intent(DownloadConstant.ACTION_DOWNLOAD_INSTALL_UNINSTALL);
			intent.putExtra(DownloadConstant.EXTRA_PACKAGENAME, packageName);
			intent.putExtra(DownloadConstant.EXTRA_VERSION, versionCode);
			intent.putExtra(DownloadConstant.EXTRA_RESULT, bInstalled);
			context.sendBroadcast(intent);
		}

	
}
