package com.lenovo.lejingpin.share.ams;

import com.lenovo.lsf.account.PsAuthenServiceL;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LenovoUserLogInOutReceiver extends BroadcastReceiver {

	public static final String ACTION_LENOVOUSER_STATUS = "android.intent.action.LENOVOUSER_STATUS";
	private static final int LENOVOUSER_OFFLINE = 1;
	private static final int LENOVOUSER_ONLINE = 2;
	private static final String TAG = "zdx";
	private String lenovoAccountNum;

	public LenovoUserLogInOutReceiver(Context context) {
		lenovoAccountNum = PsAuthenServiceL.getUserName(context);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent != null) {
			String action = intent.getAction();
			if (ACTION_LENOVOUSER_STATUS.equals(action)) {
				Log.d(TAG, "LenovoUserLogInOutReceiver.onReceive(),action:"
						+ action);
				String statusStr = intent.getStringExtra("status");
				int status = Integer.parseInt(statusStr);
				if (LENOVOUSER_OFFLINE == status) {
					Log.d(TAG, "LENOVOUSER_OFFLINE.............");
					// AppIconLayer.getInstance().loadRecomScreen();
				} else if (LENOVOUSER_ONLINE == status) {// Login
					lenovoAccountNum = PsAuthenServiceL.getUserName(context);
					Log.d(TAG, "LENOVOUSER_ONLINE.............");
				}
			}
		}
	}

}
