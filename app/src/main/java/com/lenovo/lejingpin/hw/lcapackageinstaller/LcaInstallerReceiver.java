package com.lenovo.lejingpin.hw.lcapackageinstaller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.lenovo.lejingpin.hw.lcapackageinstaller.utils.LcaInfoUtils;
import com.lenovo.lejingpin.hw.lcapackageinstaller.utils.NetworkUtils;
import com.lenovo.lejingpin.hw.lcapackageinstaller.utils.XmlLcaInfoHandler;

public class LcaInstallerReceiver extends BroadcastReceiver {

	private final static String tag = "LcaPackageInstaller";
	private Map<String, String> mLcaInfo = LcaInstallerActivity.getLcaInfo();
	public static HashMap<String, String> mInstallData = new  HashMap<String, String> ();

	@Override
	public void onReceive(Context context, Intent intent){
//		final String action = intent.getAction();
//		Log.i(tag, "" + action);
//		Uri data = intent.getData();
//		
//		
//		String pkgName = data.getEncodedSchemeSpecificPart();
//		if (action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
////			LcaInfoUtils.update(context, pkgName, "false");
//		} else if ((action.equals(Intent.ACTION_PACKAGE_ADDED))) {
//			if (mInstallData.containsKey(pkgName)) {
//				File apk = new File(mInstallData.get(pkgName));
//				if(apk.exists()){
//					apk.delete();			
//				}
//				mInstallData.remove(pkgName);
//			}
//			
//			Cursor c = LcaInfoUtils.query(context, pkgName);
//			if (c.getCount() > 0) {
//				LcaInfoUtils.update(context, pkgName, "true");
//				if (mLcaInfo != null) {
//					NetworkUtils.notifyServerInstall(context,
//							mLcaInfo.get(XmlLcaInfoHandler.TAG_APPID),
//							mLcaInfo.get(XmlLcaInfoHandler.TAG_VERSION),
//							mLcaInfo.get(XmlLcaInfoHandler.TAG_APPTYPE), null);
//				}
//			}
//			c.close();
//
////			context.sendBroadcast(new Intent(
////					"com.lenovo.leos.lcainstaller.installed").setData(data));
//		}
	}
}
