package com.lenovo.launcher2.weather.widget;

import java.util.List;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.commonui.LeAlertDialog;
import com.lenovo.launcher2.weather.widget.utils.WeatherUtilites;
import com.lenovo.leos.cloud.lcp.common.util.StringUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

public class NetworkAlertActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String alertMessage = null;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        final int actiontype = this.getIntent().getIntExtra(WeatherUtilites.EXTRA_DIALOG_TYPE, 0);
        switch(actiontype){
        case 0:
            alertMessage = getResources().getString(
            		R.string.weather_network_alert_message);
        	break;
        case 1:
            alertMessage = getResources().getString(
                    R.string.weather_animation_alert_message);
        	break;
		default:
			break;
        }
        String OK = getResources().getString(
                R.string.weather_button_ok);
        String cencel = getResources().getString(
                R.string.weather_button_cancel);
		String title = getResources().getString(
                R.string.weather_reminder);
		
		final LeAlertDialog mAlertDialog = new LeAlertDialog(this,
				R.style.Theme_LeLauncher_Dialog_Shortcut);
		mAlertDialog.setLeMessage(alertMessage);
		mAlertDialog.setLeTitle(title);
		mAlertDialog.setLeNegativeButton(cencel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
            	mAlertDialog.dismiss();
            	finish();
            }
        });
        
		mAlertDialog.setLePositiveButton(OK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
            	switch(actiontype){
            	case 0:
                    Intent intent = new Intent(NetworkAlertActivity.this, ChargeAlertActivity.class);
                    startActivity(intent);
            		break;
            	case 1:
            		String pacakgeName="com.lenovo.leos.weatheranimation";
            		boolean bool=getIsInstalledApp(pacakgeName);
            		String promptInfo=NetworkAlertActivity.this.getResources().getString(R.string.promptInfo);
            		if (bool) {
						Toast.makeText(NetworkAlertActivity.this,promptInfo, 1000).show();
					}else{
	            		 Intent downloadIntent = new Intent(Intent.ACTION_VIEW,
	            				 Uri.parse(WeatherUtilites.WEATHER_DOWNLOAD_APK_URL));
	            		 try {
	            			 startActivity(downloadIntent);
	            		 }catch (ActivityNotFoundException e) {
	            			 e.printStackTrace();
	            		 }
					}
            		break;
        		default:
        			break;
            	}
            	mAlertDialog.dismiss();
                finish();
            }
        });
		mAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface paramDialogInterface) {
                mAlertDialog.dismiss();
                finish();
            }
        });
        
		mAlertDialog.show();
    }
    
    public boolean getIsInstalledApp(String packageName){
    	 if (packageName==null||packageName.equals("")) {
			return true;
		 }
    	  List<PackageInfo> list = getPackageManager().getInstalledPackages(PackageManager.GET_PERMISSIONS);  
          boolean flag=false;
          for (PackageInfo packageInfo : list) {  
               if (packageName.equals(packageInfo.packageName)) {
            	  flag=true;
            	  break;
			   }
          }
          if (flag) {
			return true;
		  }
          else{
        	 return false;
          }
    }
}
