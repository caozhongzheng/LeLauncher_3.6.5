package com.lenovo.launcher2.settings;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnKeyListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lenovo.discuz.advise.DiscuzAdvisetoGo;
import com.lenovo.launcher.R;
import com.lenovo.launcher2.commonui.LeDialog;
import com.lenovo.launcher2.customizer.Debug;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.lps.reaper.sdk.AnalyticsTracker;

public class FeedbackActivity extends Activity {	
	
	private EditText mInput;
	private Button mSend;
	private Button mCancel;
	private ConnectivityManager mCM;	
	private static final int FUNCTION_TYPE = 7;//4
	private static final int SEND_RESULT_OK = 1;
//	private static final int SEND_RESULT_FAILED = 0;
//	private static final int SEND_RESULT_UNKNOWN = -1;
//	private static final int SEND_ADDRESS_NOT_EXIST = 10;
//	private static final int SEND_SERVER_ERROR = 11;
//	private static final int SEND_CLIENT_PROT_ERR = 12;
//	private static final int SEND_CONNECT_TIMEOUT = 13;
//	private static final int SEND_SOCKET_TIMEOUT = 14;
//	private static final int SEND_INTERRUPT_ERROR = 15;
	private static final int SEND_IO_ERROR = 16;
	private static final int SEND_EMPTY_MSG = -10;
	private static final int SEND_NETWORK_DISABLED = -11;
	private static final int SEND_NOT_CONNECTED = -12;
	private static final int BEEN_SENT = -13;
	private static final long TIMEOUT_FINISH = 300L;
	
	
	private static final int MSG_SEND_RESULT = 0;
	private boolean pendingMsg = false;
	private LeDialog d;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mCM = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		String msg = sp.getString(SettingsValue.PREF_FEEDBACK_CONTENT, null);

		
		d = new LeDialog(this,R.style.Theme_LeLauncher_Dialog_Shortcut);
		d.setCanceledOnTouchOutside(true);
		d.setLeContentView(R.layout.feedback_page);
		d.setLeTitle(R.string.feedback_dialog_title);
		d.setLeNegativeButton(this.getText(android.R.string.cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						mInput.setText(null);
						if(d!=null) d.dismiss();
						finish();
					}
				});
		d.setLePositiveButton(this.getText(R.string.feedback_send),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						new Thread() {
							public void run() {
								sendMessage();
							}
						}.start();
					}
				});
		if(d.getLePositiveButton().getVisibility() == View.VISIBLE)
		mInput = (EditText) d.findViewById(R.id.feedback_input);
		
		
		if (msg != null && !"".equals(msg.trim())) {
			mInput.setText(msg);
		}
		mInput.requestFocus();
		//test by dining
		//mInput.setBackgroundDrawable(null);
		
		/*d.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				 if (keyCode == KeyEvent.KEYCODE_BACK) {
                     dialog.dismiss();
                     finish();
                 }
				return false;
			}
		});*/
		d.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
                finish();
			}
		});
		d.show();
		//p.mTitle = getString(R.string.feedback_dialog_title);
	}
	@Override
	protected void onDestroy() {
		
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
		String msg = mInput.getText() == null ? null : mInput.getText().toString();
		if (msg == null || "".equals(msg.trim())) {
			editor.remove(SettingsValue.PREF_FEEDBACK_CONTENT);
		} else {
			editor.putString(SettingsValue.PREF_FEEDBACK_CONTENT, msg);
		}
		editor.commit();
		super.onDestroy();

	}
	
	private void sendMessage() {
		if (pendingMsg) {
			return;
		}
		pendingMsg = true;
		
		String msg = mInput.getText() == null ? null : mInput.getText().toString();
		if (msg == null || "".equals(msg.trim())) {
			handleResult(SEND_EMPTY_MSG);
			return;
		}
		
		if (!SettingsValue.isNetworkEnabled(this)) {
			handleResult(SEND_NETWORK_DISABLED);
			return;
		}
		
		boolean networkAvailable = isNetworkConnected();
		if (!networkAvailable) {
			handleResult(SEND_NOT_CONNECTED);
			return;
		}
		
		try {
			mHandler.removeMessages(BEEN_SENT);
			mHandler.sendEmptyMessageDelayed(BEEN_SENT, TIMEOUT_FINISH);
			String title = getString(R.string.feedback_title, SettingsValue.getPackageVersion(this));
		    ///DiscuzAdvisetoGo.postAdvise(FUNCTION_TYPE, title, msg);
				AnalyticsTracker.getInstance().addUploadMsg(title,msg);
		} catch (Exception e) {
			Debug.e("===========call DiscuzAdvisetoGo failed ===", e);
		}
		
//		handleResult(ret);
	}
	
	private void handleResult(int result) {
		mHandler.removeMessages(result);
		mHandler.sendEmptyMessage(result);
	}
	
	private boolean isNetworkConnected() {
		boolean ret = SettingsValue.isNetworkEnabled(this);
		NetworkInfo ni = mCM.getActiveNetworkInfo();
		
		return ret && ni != null && ni.isConnected();
	}

	
	private Handler mHandler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SEND_RESULT_OK:
				Toast.makeText(FeedbackActivity.this, R.string.feedback_sent_succeed, Toast.LENGTH_SHORT).show();
				mInput.setText(null);
				if(d!=null) d.dismiss();
				finish();
				break;
			case SEND_EMPTY_MSG:
				Toast.makeText(FeedbackActivity.this, R.string.feedback_err_empty_message, Toast.LENGTH_SHORT).show();
				break;
			case SEND_NETWORK_DISABLED:
				Toast.makeText(FeedbackActivity.this, R.string.feedback_err_network_disabled, Toast.LENGTH_SHORT).show();
				break;
			case SEND_NOT_CONNECTED:
				Toast.makeText(FeedbackActivity.this, R.string.feedback_err_network_not_connected, Toast.LENGTH_SHORT).show();
				break;
			case BEEN_SENT:
				Toast.makeText(FeedbackActivity.this, R.string.feedback_been_sent, Toast.LENGTH_SHORT).show();
				mInput.setText(null);
				if(d!=null) d.dismiss();
				finish();

				break;
			default:
				Toast.makeText(FeedbackActivity.this, R.string.feedback_sent_failed, Toast.LENGTH_SHORT).show();
				break;
			}
			pendingMsg = false;
		}
	};
}
