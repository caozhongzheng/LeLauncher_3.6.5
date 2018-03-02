package com.lenovo.lejingpin.hw.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.lenovo.lejingpin.hw.utils.UMentAnalyticsUtil;
import com.lenovo.lps.reaper.sdk.AnalyticsTracker;
import com.lenovo.lps.sus.EventType;
import com.lenovo.lps.sus.SUS;
import com.lenovo.lps.sus.SUSListener;


public class CheckUpdateActivity extends Activity {
	private static final String TAG = "CheckUpdateActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		try {
//			AnalyticsTracker.getInstance().initialize(this);
//			HawaiiApplication.reportAccessHawaiiFromLauncher();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		/*Window window = getWindow();
		LayoutParams lp = window.getAttributes();
		lp.type = LayoutParams.TYPE_BASE_APPLICATION;
		lp.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL;
		lp.width = 200;
		lp.height = 200;
		
		window.getDecorView().setBackgroundColor(Color.GREEN);
		
		window.getWindowManager().addView(view, lp);*/
		
		SUS.AsyncStartVersionUpdate(this);
		SUS.setSUSListener(new SUSListener() {
			@Override
			public void onUpdateNotification(EventType arg0, String arg1) {
				Log.d(TAG, "onUpdateNotification >>> eventType="+arg0+", s="+arg1);
			}
		});
		finish();
		
    	/*new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				ProgressDialog.show(CheckUpdateActivity.this, "","Loading. Please wait...", true);
			}
		},10000);*/
	}
	
	@Override
	protected void onDestroy() {
		try {
			AnalyticsTracker.getInstance().shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		
	}
}
