package com.lenovo.launcher2.addon.share;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lenovo.launcher.R;
import com.lenovo.lejingpin.share.download.DownloadConstant;
import com.lenovo.lejingpin.share.download.DownloadInfo;
import com.lenovo.lejingpin.share.download.LDownloadManager;

public class LeShareProgressDialog extends Activity{
	
	private static String TAG = "LeShareProgressDialog";
	
	private ProgressBar mProgressBar;
	private TextView mTextViewMsg;
	private ProgressDialogBroadCast mReceiver;
	
	private LinearLayout mProgressLayout;
	private LinearLayout mQizeFinishLayout;
	private Button cancelOrAgainButton;
	private LeShareDownloadQiezi mLeShareDownloadQiezi;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mReceiver = new ProgressDialogBroadCast();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.leshare_download_qizi_progress_dilaog);
		mLeShareDownloadQiezi = new LelauncherDownloadAnyShare();
		initLayout();
	}
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(mReceiver!=null){
			unregisterReceiver(mReceiver);
		}
		super.onDestroy();
	}

	private void initLayout(){
		mProgressLayout = (LinearLayout) this.findViewById(R.id.leshare_progress_msg);
		mQizeFinishLayout = (LinearLayout) this.findViewById(R.id.leshare_qiezi_finish);
		Window window = getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.dimAmount = 0.0f;
		window.setAttributes(lp);
		window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		window.setBackgroundDrawableResource(R.drawable.menu_dialog_bg);
		window.setWindowAnimations(R.style.dialogWindowAnim);
		window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		window.setGravity(Gravity.CENTER );
		TextView title = (TextView) findViewById(R.id.dialog_title);
		title.setText(R.string.leshare_dialog_title);
		mProgressBar = (ProgressBar)findViewById(R.id.leshare_dialog_progressbar);
		mTextViewMsg = (TextView) findViewById(R.id.leshare_dialog_msg);
		String msg = this.getString(R.string.leshare_dialog_msg)+" 0 %";
		mTextViewMsg.setText(msg);
		Log.d(TAG, "initLayout >> mProgressBar :  "+mProgressBar);
		Button hidden = (Button) findViewById(R.id.leshare_dialog_hidden);
		hidden.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		cancelOrAgainButton = (Button) this.findViewById(R.id.leshare_dialog_cancel);
		cancelOrAgainButton.setTag("cancel");
		cancelOrAgainButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				if(view.getTag().equals("cancel")){
					
					DownloadInfo info = new DownloadInfo("com.lenovo.anyshare","1000"); 
					info = LDownloadManager.getDefaultInstance(LeShareProgressDialog.this).getDownloadInfo(info);
					if(info!=null){
						LDownloadManager.getDefaultInstance(LeShareProgressDialog.this).deleteTask(info);
					}
				}else if(view.getTag().equals("again")){
					if(mLeShareDownloadQiezi!=null){
						
						mLeShareDownloadQiezi.againDonwloadQiezi(LeShareProgressDialog.this);
					}
				}
				finish();
			}
		});
		
		Button qieziFinishConfirm = (Button) this.findViewById(R.id.leshare_qiezi_finish_confirm);
		qieziFinishConfirm.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}
	
	public interface LeShareDownloadQiezi{
		public void againDonwloadQiezi(Context context);
	}
	
	private class ProgressDialogBroadCast extends BroadcastReceiver{
		
		public ProgressDialogBroadCast(){
			Log.d(TAG,"register broadcast receiver....");
			IntentFilter filter = new IntentFilter();
			filter.addAction(DownloadConstant.ACTION_APK_FAILD_DOWNLOAD);
			filter.addAction(LeShareConstant.ACTION_DOWNLOAD_QIEZI_PROGRESS);
			registerReceiver(this, filter);
		}

		@Override
		public void onReceive(Context context, Intent intent){
			// TODO Auto-generated method stub
			Log.d(TAG, "ProgressDialogBroadCast >> intent : "+intent);
			if(intent!=null){
				String action = intent.getAction();
				Log.d(TAG, "ProgressDialogBroadCast >> action : "+action);
				if(DownloadConstant.ACTION_APK_FAILD_DOWNLOAD.equals(action)){
					String pkg = intent.getStringExtra(DownloadConstant.EXTRA_PACKAGENAME);
					String vcode = intent.getStringExtra(DownloadConstant.EXTRA_VERSION);
					String app_name = intent.getStringExtra(DownloadConstant.EXTRA_APPNAME);
					int result = intent.getIntExtra(DownloadConstant.EXTRA_RESULT,DownloadConstant.FAILD_DOWNLOAD_OTHER_ERROR);
					Log.d(TAG,"ACTION_APK_FAILD_DOWNLOAD >> result : "+result);
					if(!TextUtils.isEmpty(pkg) && pkg.equals("com.lenovo.anyshare") && !TextUtils.isEmpty(vcode) && vcode.equals("1000")){
						cancelOrAgainButton.setText(R.string.leshare_dialog_again_button);
						cancelOrAgainButton.setTag("again");
						String errorString = "\n";
						if (result == DownloadConstant.FAILD_DOWNLOAD_NO_ENOUGHT_SPACE) {
							errorString = errorString
									+ getResources().getString(
											R.string.download_sdcard_notexists);
						} else if (result == DownloadConstant.FAILD_DOWNLOAD_NETWORK_ERROR) {
							errorString = errorString
									+getResources().getString(
											R.string.download_net_error);
						}
					
						Toast.makeText(
								LeShareProgressDialog.this,
								getResources()
										.getString(
												R.string.downloadcompleted_error_app_title,
												app_name)
										+ errorString, Toast.LENGTH_SHORT)
								.show();
					}
				}else if(LeShareConstant.ACTION_DOWNLOAD_QIEZI_PROGRESS.equals(action)){
					int progress = intent.getIntExtra("progress", 0);
					Log.d(TAG, "ACTION_DOWNLOAD_QIEZI_PROGRESS >> progress : "+progress);
					if(mProgressBar!=null){
						mProgressBar.setProgress(progress);
						String msg = getString(R.string.leshare_dialog_msg)+" "+progress+" %";
						mTextViewMsg.setText(msg);
					}
					if(progress==100){
						if(mQizeFinishLayout.getVisibility()==View.GONE){
							mQizeFinishLayout.setVisibility(View.VISIBLE);
							mProgressLayout.setVisibility(View.GONE);
						}
					}
				}
			}
		}
		
	}

}
