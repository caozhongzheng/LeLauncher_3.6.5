package com.lenovo.launcher2.weather.widget;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.commonui.LeAlertDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Window;

public class ChargeAlertActivity extends Activity {

	private static final String EXTRA_NETWORK_ENABLED = "network_enabled";// same as WeatherAI's EXTRA_NETWORK_ENABLED

	private static final String EXTRA_NETWORK_ACTION = "com.lenovo.action.ACTION_NETWORK_ENABLER_CHANGED";// same as WeatherAI's action

	private static final String EXTRA_ISFROM_WEATHER = "isfrom_weather";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

		String title = getResources().getString(
                R.string.weather_reminder);
		String alertMessage = getResources().getString(
                R.string.weather_charge_alert_message);
        String OK = getResources().getString(
                R.string.weather_button_ok);
        String cencel = getResources().getString(
                R.string.weather_button_cancel);

        final LeAlertDialog mAlertDialog = new LeAlertDialog(this,
				R.style.Theme_LeLauncher_Dialog_Shortcut);
        mAlertDialog.setLeTitle(title);
        mAlertDialog.setLeMessage(alertMessage);
        
        mAlertDialog.setLePositiveButton(OK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
            	Intent intent = new Intent(EXTRA_NETWORK_ACTION);
            	intent.putExtra(EXTRA_NETWORK_ENABLED, true);
            	intent.putExtra(EXTRA_ISFROM_WEATHER, true);
            	ChargeAlertActivity.this.sendBroadcast(intent);
            	mAlertDialog.dismiss();
                finish();
            }
        });
        
        mAlertDialog.setLeNegativeButton(cencel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
            	mAlertDialog.dismiss();
            	finish();
            }
        });
        
        mAlertDialog.show();
        
        mAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface paramDialogInterface) {
            	mAlertDialog.dismiss();
                finish();
            }
        });
	}

}
