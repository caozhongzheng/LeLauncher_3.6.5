package com.lenovo.launcher2.customizer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.app.AlertActivity;
import com.lenovo.launcher.R;

public class ProgressDialog extends AlertActivity {

	public static final String ACTION_SHOW_PROGRESS_DIALOG = "com.lenovo.launcher.ACTION_SHOW_PROGRESS_DIALOG";
	public static final String ACTION_DISMISS_PROGRESS_DIALOG = "com.lenovo.launcher.ACTION_DISMISS_PROGRESS_DIALOG";
	public static final String KEY_DIALOG_MSG_ID = "com.lenovo.launcher.KEY_DIALOG_MSG_ID";
	public static final String KEY_TOAST_MSG_ID = "com.lenovo.launcher.KEY_TOAST_MSG_ID";
	private Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LinearLayout ll = (LinearLayout) LinearLayout.inflate(this, R.layout.apply_progressbar, null);
		//ll.setBackgroundColor(Color.WHITE);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(ll);

		Window window = getWindow();
		window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

		//window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		window.setGravity(Gravity.CENTER);

		TextView msgView = (TextView) ll.findViewById(R.id.progress_msg);
		int resid = getIntent().getIntExtra(KEY_DIALOG_MSG_ID, 0);
		if (resid != 0) {
			msgView.setText(resid);
		}
		final IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_SHOW_PROGRESS_DIALOG);
		filter.addAction(ACTION_DISMISS_PROGRESS_DIALOG);
		registerReceiver(mReceiver, filter);
	}

	public void onBackPressed() {
	}

	public boolean onTouchEvent(MotionEvent event) {
		return false;
	}

	public boolean onKeyShortcut(int keyCode, KeyEvent event) {
		return false;
	}

	public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
		return false;
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking() && !event.isCanceled()) {
			onBackPressed();
			return true;
		}
		return false;
	}

	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		return false;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			event.startTracking();
			return true;
		}

		return false;
	}

	public boolean onTrackballEvent(MotionEvent event) {
		return false;
	}

	public boolean onGenericMotionEvent(MotionEvent event) {
		return false;
	}

	public boolean dispatchTouchEvent(MotionEvent ev) {
		return onTouchEvent(ev);
	}

	public void dismissProgressDialog(final int resId) {
		if (resId != 0) {
			handler.post(new Runnable() {
				public void run() {
					Toast.makeText(ProgressDialog.this, resId, Toast.LENGTH_LONG).show();
				}
			});
			// Toast.makeText(this, resId, Toast.LENGTH_LONG).show();
			finish();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
		handler = null;
		System.gc();
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context mContext, Intent intent) {
			final String action = intent.getAction();
			if (ACTION_DISMISS_PROGRESS_DIALOG.equals(action)) {
				intent.getIntExtra(KEY_TOAST_MSG_ID, 0);
				dismissProgressDialog(R.string.theme_appling_success);
			}
		}
	};

}
