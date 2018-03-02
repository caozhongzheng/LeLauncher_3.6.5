package com.lenovo.lejingpin.hw.lcapackageinstaller;

import com.android.internal.app.AlertActivity;
import com.lenovo.launcher.R;

import com.lenovo.launcher2.customizer.SettingsValue;

import com.lenovo.lejingpin.hw.content.data.HwConstant;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

/*RK_ID: RK_LELAUNCHER_MAGICDOWNLOAD. AUT:zhangdxa. DATE: 2012-09-21.*/
public class MagicLcaInstallerActivity extends AlertActivity {
	private static final String TAG ="zdx";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"LcaInstallerActivity.onCreate()");
        
        if(SettingsValue.getCurrentMachineType(this)==-1){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}        
        popupAKeyDownloadInstallEnabledDialog();
    }
 
    @Override
	protected void onDestroy() {
		Log.i(TAG,"LcaInstallerActivity.onDestroy() ");
		super.onDestroy();
	}    
    
    protected void onPause() {
		Log.i(TAG,"LcaInstallerActivity.onPause() ");
		super.onPause();
	}  
    
    private void popupAKeyDownloadInstallEnabledDialog() {
    	Log.i(TAG,"LcaInstallerActivity.popupAKeyDownloadInstallEnabledDialog()");
    	final Context context = this.getApplicationContext();
    	AlertDialog.Builder builder = new AlertDialog.Builder(this)
        .setTitle(R.string.check_root_title)
        .setMessage(R.string.check_root_prompt)
    	.setNegativeButton(R.string.dialog_cancle,new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	sendSetAkeyInstallToService(false);
                dialog.dismiss();
                finish();
            }
        })
        .setPositiveButton(R.string.dialog_confirm,new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	SettingsValue.setAKeyInstall(context, true);
            	sendSetAkeyInstallToService(true);
                dialog.dismiss();
                finish();
            }
        })
        .setNeutralButton(R.string.dialog_ignore, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	SettingsValue.setAkeyInstallIgnore(context,true);
            	sendSetAkeyInstallToService(false);
                dialog.dismiss();
                finish();
            }
        });
    	AlertDialog alertDialog = builder.create();
    	alertDialog.setOnCancelListener( new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				sendSetAkeyInstallToService(false);
                dialog.dismiss();
                finish();
			}
        	
        });
    	alertDialog.show();
    }
    
    private void sendSetAkeyInstallToService(boolean bAkeyInstall){
    	Log.i(TAG,"LcaInstallerActivity.sendSetAkeyInstallToService()--- "+ bAkeyInstall);
    	//Context context = this.getApplicationContext();
    	Intent inputIntent = getIntent();
    	Intent intent = new Intent(this, LcaInstallerService.class);
		intent.setAction("com.lenovo.action.packageinstall");
		intent.putExtra("type", "package_install_continue");
        intent.putExtra("uri", inputIntent.getStringExtra("uri"));
        intent.putExtra("package", inputIntent.getStringExtra("package") );
        intent.putExtra("install_type", inputIntent.getIntExtra("install_type",HwConstant.INSTALL_TYPE_AKEY_NORMAL));
        intent.putExtra("akeyinstall", bAkeyInstall);
        //context.startService(intent);
    }
}
