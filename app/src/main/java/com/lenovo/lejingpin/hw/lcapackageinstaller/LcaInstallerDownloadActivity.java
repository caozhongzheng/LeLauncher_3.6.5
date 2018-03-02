package com.lenovo.lejingpin.hw.lcapackageinstaller;

import com.android.internal.app.AlertActivity;
import com.lenovo.launcher.R;
import com.lenovo.launcher2.commonui.LeAlertDialog;
import com.lenovo.launcher2.customizer.SettingsValue;




import com.lenovo.lejingpin.share.download.AppDownloadUrl;
import com.lenovo.lejingpin.share.download.DownloadConstant;
import com.lenovo.lejingpin.share.download.DownloadHandler;
import com.lenovo.lejingpin.share.download.Downloads;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/*RK_ID: RK_LELAUNCHER_MAGICDOWNLOAD. AUT:zhangdxa. DATE: 2012-10-10.*/
public class LcaInstallerDownloadActivity extends AlertActivity {
	private static final String TAG ="zdx";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"LcaInstallerDownloadActivity.onCreate()");
        final String packageName = getIntent().getStringExtra("package");
        final String installPath = getIntent().getStringExtra("install_path");
        popupInstallFailDownloadAgainDialog(packageName, installPath);
    }
 
    @Override
	protected void onDestroy() {
		Log.i(TAG,"LcaInstallerDownloadActivity.onDestroy() ");
		super.onDestroy();
	}    
    
    private void popupInstallFailDownloadAgainDialog(final String packageName, final String installPath) {
    	Log.i(TAG,"LcaInstallerDownloadActivity.popupInstallFailDownloadAgainDialog()");
//    	AlertDialog.Builder builder = new AlertDialog.Builder(this)
//        .setTitle(R.string.install_title)
//        .setMessage(R.string.install_failed_download_again)
//    	.setNegativeButton(R.string.dialog_cancle,new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                finish();
//            }
//        })
//        .setPositiveButton(R.string.dialog_confirm,new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//            	deleteDownloadInfo(packageName, installPath);
//                dialog.dismiss();
//                finish();
//            }
//        });
//    	AlertDialog alertDialog = builder.create();
//    	alertDialog.setOnCancelListener( new DialogInterface.OnCancelListener() {
//			@Override
//			public void onCancel(DialogInterface dialog) {
//                dialog.dismiss();
//                finish();
//			}
//        	
//        });
//    	alertDialog.show();
    	
    	LeAlertDialog alertDialog = new LeAlertDialog(this,R.style.Theme_LeLauncher_Dialog_Shortcut);
    	alertDialog.setLeTitle(R.string.install_title);
    	alertDialog.setLeMessage(R.string.install_failed_download_again);
    	alertDialog.setLeNegativeButton(this.getString(R.string.cancel_action),new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                LcaInstallerDownloadActivity.this.finish();
            }
        });
        alertDialog.setLePositiveButton(this.getString(R.string.rename_action),new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	deleteDownloadInfo(packageName, installPath);
            	dialog.dismiss();
            	LcaInstallerDownloadActivity.this.finish();
            	
            }
        });
    	alertDialog.setOnCancelListener( new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
                dialog.dismiss();                
                LcaInstallerDownloadActivity.this.finish();
			}
        	
        });
    	alertDialog.show();
    }
    
    private void deleteDownloadInfo(String packageName, String installPath){
    	Cursor sc = null ;
    	try {
	    	ContentResolver resolver = getContentResolver();
			sc = resolver.query(Downloads.CONTENT_URI, null, "pkgname = ? and _data = ?",
					new String[]{packageName,installPath}, null);
			if( sc != null && sc.moveToFirst()){			
				String package_name = sc.getString(sc.getColumnIndex("pkgname")); 
				String version_code = sc.getString(sc.getColumnIndex("versioncode")); 
				String app_name = sc.getString(sc.getColumnIndex("title")); 
				//String iconUrl = sc.getString(sc.getColumnIndex("iconaddr")); 
				String url = sc.getString(sc.getColumnIndex("uri")); 
				int category =  sc.getInt(sc.getColumnIndex("category"));
				//String mime_type =  sc.getString(sc.getColumnIndex("mimetype"));
				
				
				Log.i(TAG,"*************LcaInstallerDownloadActivity.deleteDownloadInfo, uri:"+ url);
				resolver.delete(Downloads.CONTENT_URI,  "pkgname = ? and _data = ?",
						new String[]{packageName,installPath});
				
				notifyDelete(package_name, version_code, app_name,category, DownloadConstant.ACTION_DOWNLOAD_DELETE, true);
			
						//AppDownlaodUrl downurl = new AppDownlaodUrl();
				//		AppDownloadUrl downurl = new AppDownloadUrl();
				//		downurl.setDownurl(url);
				//		downurl.setPackage_name(package_name);
				//		downurl.setVersion_code(version_code);
				//		downurl.setApp_name(app_name);
				//		downurl.setIconUrl(iconUrl);
				//		downurl.setCategory(category);
				//		downurl.setMimeType(mime_type);
				//		sendMessage(DownloadHandler.getInstance(this),DownloadConstant.MSG_DOWN_LOAD_URL,downurl);
			
			}
    	} catch (Exception e) {
			e.printStackTrace();
		} finally{
			Log.d(TAG, "lca downloadActivity finally......");
			if(sc != null && !sc.isClosed()){
				sc.close();
				
			}
		}
	}
    
//    private void sendMessage(Handler handler, int what, Object obj){
//		if(handler != null) {
//			Message msg = new Message();
//			msg.obj = obj;
//			msg.what = what;
//			handler.sendMessage(msg);
//		} else {
//			Log.i(TAG, "sendMessage ,  handler is null, not send!");
//		}
//	}
    
    private void notifyDelete(String packageName, String versionCode,String app_name ,int category,String action, boolean mDelete) {
		Log.d(TAG, "LcaInstallerDownloadActivity.notifyDelete , delete download");
		Intent intent = new Intent(action);
		intent.putExtra(DownloadConstant.EXTRA_PACKAGENAME, packageName);
		intent.putExtra(DownloadConstant.EXTRA_VERSION, versionCode);
		intent.putExtra(DownloadConstant.EXTRA_APPNAME, app_name);
		intent.putExtra(DownloadConstant.EXTRA_RESULT, mDelete);
		intent.putExtra(DownloadConstant.EXTRA_CATEGORY, category);
		intent.putExtra(DownloadConstant.EXTRA_POPTOAST, false);
		sendBroadcast(intent);	
	}
 
}
