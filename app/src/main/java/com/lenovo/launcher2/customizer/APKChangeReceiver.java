package com.lenovo.launcher2.customizer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Author : Liuyg1@lenovo.com
 * */
public class APKChangeReceiver extends BroadcastReceiver {

	
	Context mContext;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		mContext = context;
		final String action = intent.getAction();

		if (intent.getData() != null) {
			final String packageName = intent.getData().getSchemeSpecificPart();
			if (packageName == null || packageName.length() == 0
					|| packageName.equals(context.getPackageName())) {

				return;
			}
			if (Intent.ACTION_PACKAGE_REPLACED.equals(action)){

				setLeosSettingIntent(context);
			}else if(Intent.ACTION_PACKAGE_ADDED.equals(action) || Intent.ACTION_INSTALL_PACKAGE.equals(action)){

				setLeosSettingIntent(context);
				
			}else if(Intent.ACTION_PACKAGE_REMOVED.equals(action)){

				setLeosSettingIntent(context);

			}
		}
	}

    private void setLeosSettingIntent(Context context){
    	Intent i = new Intent();
		i.setAction(SettingsValue.ACTION_LEOS_APP_SETTING_REFRESH);
		context.sendBroadcast(i);
    }
}
