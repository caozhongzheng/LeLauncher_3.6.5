package com.lenovo.launcher2.addon.share;

import java.net.URLDecoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.addon.share.LeShareProgressDialog.LeShareDownloadQiezi;
import com.lenovo.launcher2.customizer.Reaper;
import com.lenovo.lejingpin.magicdownloadremain.MagicDownloadControl;
import com.lenovo.launcher2.commonui.LeAlertDialog;
import com.lenovo.launcher2.settings.SeniorSettings;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.lejingpin.LEJPConstant;
import com.lenovo.lejingpin.appsmgr.content.UpgradeApp.Status;
import com.lenovo.lejingpin.hw.content.data.HwConstant;
//import com.lenovo.lejingpin.hw.download.DownloadInfo;
//import com.lenovo.lejingpin.hw.download.LDownloadManager;
import com.lenovo.lejingpin.hw.lcapackageinstaller.utils.LcaInstallerUtils;
import com.lenovo.lejingpin.hw.ui.Util;
import com.lenovo.lejingpin.share.download.AppDownloadUrl;
import com.lenovo.lejingpin.share.download.DownloadConstant;
import com.lenovo.lejingpin.share.download.DownloadHandler;
import com.lenovo.lejingpin.share.download.DownloadInfo;
import com.lenovo.lejingpin.share.download.LDownloadManager;

/**
 * Author : Liuyg1@lenovo.com
 * */
public class LelauncherDownloadAnyShare extends Activity implements LeShareDownloadQiezi {

private Context mContext;
private View mDialog_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.install_dialog);
        mContext = this;
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
		if(LeShareUtils.isInstalledQiezi(this)){
			title.setText(R.string.qiesi_update_dialog_title);
		}else{
			title.setText(R.string.qiesi_install_dialog_title);
		}
		TextView message1 =  (TextView) findViewById(R.id.message1);
		message1.setText(R.string.qiesi_install_message1);
		TextView message2 =  (TextView) findViewById(R.id.message2);
		message2.setText(R.string.qiesi_install_message2);
		
		mDialog_view =  findViewById(R.id.dialog_bg);
		title.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		View mDownload_dialog =  findViewById(R.id.install_dialog);
		mDownload_dialog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		 Button btn_download = (Button) findViewById(R.id.btn_download);
		 btn_download.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Log.d("liuyg1","download onclick");
					downloadQiezi(mContext);
				}
			});
		/*** RK_ID:RK_FONT AUT:zhanglz1@lenovo.com. DATE: 2013-02-21 S ***/
		Typeface tf = SettingsValue
				.getFontStyle(LelauncherDownloadAnyShare.this);
		if (tf != null && tf != title.getTypeface()) {
			message1.setTypeface(tf);
			message2.setTypeface(tf);
			title.setTypeface(tf);
			btn_download.setTypeface(tf);
		}
		/*** RK_ID:RK_FONT AUT:zhanglz1@lenovo.com. DATE: 2013-02-21 E ***/        

	}
	private void installApp( Context context, String packageName, String versionCode, int category){
		Log.i("liuyg1","MagicDownloadReceiver.upgradeInstall, pkg:"+ packageName);
		DownloadInfo info = new DownloadInfo(packageName, versionCode);
		DownloadInfo downloadInfo = LDownloadManager.getDefaultInstance(context).getDownloadInfo(info);
		Log.i("liuyg1","MagicDownloadReceiver.upgradeInstall, downloadInfo:"+ downloadInfo);
		
		if(downloadInfo != null) {
			LcaInstallerUtils.installApplication(context, downloadInfo.getInstallPath(), 
			downloadInfo.getCategory(), packageName,versionCode );
		}
	}
	 Handler mDowloadHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_DOWNLOAD_ERROR:
				 Toast.makeText(mContext, R.string.downlaod_error_info, Toast.LENGTH_SHORT).show();
				 
				break;
			case MSG_DOWNLOAD_URL:
				Log.i("liuyg1","MagicDownloadControl.downloadFromCommon:");
				Log.i("yangmao_0128", "start download qiezi");
				com.lenovo.lejingpin.magicdownloadremain.MagicDownloadControl.Status status = MagicDownloadControl.queryDownloadStatus(mContext, packagename, "1000");
				if(!com.lenovo.lejingpin.magicdownloadremain.MagicDownloadControl.Status.UNINSTALL.equals(status)){
					createProgressDialog();
				}
                MagicDownloadControl.downloadFromCommon(mContext, 
                 		packagename,  
                         "1000", 
                         DownloadConstant.CATEGORY_COMMON_APP | DownloadConstant.CATEGORY_LENOVO_APK, 
                         mContext.getString(R.string.downlaod_app_name), 
                         null, 
                         mUrl, 
                         HwConstant.MIMETYPE_APK,
                         "true", 
                         "true");
               
                break;
			case MSG_DOWNLOAD_From3G:
				execSUSHttp(mContext);
				break;
			default:{
				break;
			}
			}
		}
	};
	
	
	private  void downloadQiezi(Context context){
		if(!Util.getInstance().isNetworkEnabled(context)){
			showNetworkEnableDialog(context, context.getString(R.string.anyshare_download_network_title));
			return;
		}

		 MagicDownloadControl.Status status = MagicDownloadControl.queryDownloadStatus(context,  packagename, mVersionCode);
		 Log.d("liuyg1", "downloadQiezi >> status : "+status);
            switch (status) {
                case DOWNLOADING:
                    Toast.makeText(context, R.string.anyshare_downloading, Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case PAUSE:
                    Toast.makeText(context, R.string.anyshare_downloading, Toast.LENGTH_SHORT).show();
                    //启动
                    execSUSHttp(context);
                    break;
                case UNINSTALL:
                   // Toast.makeText(mContext, R.string.anyshare_downloaded_install, Toast.LENGTH_SHORT).show();
                    //提示安装
//                    execSUSHttp(mContext);
                    finish();
                    installApp(context,packagename,mVersionCode,DownloadConstant.CATEGORY_COMMON_APP);
                    break; 
                case UNDOWNLOAD:
                	 execSUSHttp(context);
                	break;
                default:
                	 execSUSHttp(context);
                    break;
            }      
	}
	
	
	private void createProgressDialog(){
		Intent intent = new Intent();
		intent.setClass(mContext, LeShareProgressDialog.class);
		mContext.startActivity(intent);
	}
    private static final int MSG_DOWNLOAD_ERROR = 101;
    private static final int MSG_DOWNLOAD_URL = 102;
    private static final int MSG_DOWNLOAD_From3G = 103;
    private static String mUrl;
    private String mVersionCode = "1000";
    private String packagename = "com.lenovo.anyshare";
    private boolean mDownload_From_3G = false;
    private void execSUSHttp(final Context context){
    	if(!mDownload_From_3G&&getConnectType(context)==CONNECT_TYPE_MOBILE){
    		showConfirmDownloadDialog(context);
    		return;
    	}
    	
    	finish();
    	new Thread() {
            @Override
            	 public void run() {
                     final SUSHttpRequest request = new SUSHttpRequest();
                     String resultStr = request.executeHttpGet(
                     		context.getApplicationContext(),request.getUrl());
                     Log.i("liuyg1", "***********result:"+ resultStr);
                     if(resultStr != null&&resultStr.length()>0){
                     	 String jsonObjectStr = null;
                 		 String resultion = null;
                 		 JSONObject jsonObject = null;
                 		 try {
          					jsonObject = new JSONObject(resultStr);
          					resultion = jsonObject.getString("RES");
          					if("SUCCESS".equals(resultion)){
          						//VersionCode
//          						jsonObjectStr = jsonObject.getString("VerCode");
//          						mVersionCode = (null!=jsonObjectStr && jsonObjectStr.length()>0)?URLDecoder.decode(jsonObjectStr):null;
//          						Log.d("liuyg1","SUSHttpRequest >> versionCode: "+ mVersionCode);
          						//Download Url
          						jsonObjectStr = jsonObject.getString("DownloadURL");
          						mUrl = (null!=jsonObjectStr && jsonObjectStr.length()>0)?URLDecoder.decode(jsonObjectStr):null;
          						Log.d("liuyg1","SUSHttpRequest >> url: "+ mUrl);
          						Message msg1 = new Message();  
          						msg1.what = MSG_DOWNLOAD_URL;
          						mDowloadHandler.sendMessage(msg1);
          						return;
          					}else if("LATESTVERSION".equals(resultion) ||
          							 "NOTFOUND".equals(resultion)){
          						
          						Message msg1 = new Message();  
          						msg1.what = MSG_DOWNLOAD_ERROR;
          						mDowloadHandler.sendMessage(msg1);
//          						 Toast.makeText(context, R.string.downlaod_error_info, Toast.LENGTH_SHORT).show();
                                 //Not found apk
          					}else if("EXCEPTION".equals(resultion)){
                                 //Network error
          						Message msg1 = new Message();  
          						msg1.what = MSG_DOWNLOAD_ERROR;
          						mDowloadHandler.sendMessage(msg1);	
//          						Toast.makeText(context, R.string.downlaod_error_info, Toast.LENGTH_SHORT).show();
          					}
          				} catch (JSONException e) {
          					// TODO Auto-generated catch block
          					//e.printStackTrace();
      						
      						Message msg1 = new Message();  
      						msg1.what = MSG_DOWNLOAD_ERROR;
      						mDowloadHandler.sendMessage(msg1);
//          					 Toast.makeText(context, R.string.downlaod_error_info, Toast.LENGTH_SHORT).show();
          				}
                     }
						
						Message msg1 = new Message();  
						msg1.what = MSG_DOWNLOAD_ERROR;
						mDowloadHandler.sendMessage(msg1);
//                     Toast.makeText(context, R.string.downlaod_error_info, Toast.LENGTH_SHORT).show();
                 }
            }.start();
    }
    
    public void showNetworkEnableDialog(final Context context, String title) {
    	mDialog_view.setVisibility(View.INVISIBLE);
    	LeAlertDialog dialog = new LeAlertDialog(context, R.style.Theme_LeLauncher_Dialog_Shortcut);
		 Window window = dialog.getWindow();
		 WindowManager.LayoutParams lp = window.getAttributes();
		 lp.dimAmount = 0.0f;
		 window.setAttributes(lp);
    	dialog.setTitle(title);
    	dialog.setLeMessage(context.getText(R.string.confirm_network_open));
    	dialog.setLeNegativeButton(context.getText(R.string.cancel_action), new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int which) {
    			dialog.dismiss();
    			mDialog_view.setVisibility(View.VISIBLE);
    			Toast.makeText(context, R.string.version_update_toast, Toast.LENGTH_SHORT).show();
    		}
    	});

    	dialog.setLePositiveButton(context.getText(R.string.rename_action), new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int which) {
    			Intent intent = new Intent();
    			intent.setClass(context, SeniorSettings.class);
    			((Activity) context).startActivityForResult(intent, LeShareConstant.REQUEST_CONFIRM_NETWORK_ENABLED);
    			dialog.dismiss();
    			mDialog_view.setVisibility(View.VISIBLE);
    		}
    	});
    	dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
    		@Override
    		public void onCancel(DialogInterface dialog) {
    			dialog.dismiss();
    			mDialog_view.setVisibility(View.VISIBLE);
    			Toast.makeText(context, R.string.version_update_toast, Toast.LENGTH_SHORT).show();
    		}
    	});
    	dialog.show();
    }
	public static final int CONNECT_TYPE_WIFI = 1;
	public static final int CONNECT_TYPE_MOBILE = 2;
	public static final int CONNECT_TYPE_OTHER = 0;
	public static int getConnectType(Context context) {
		ConnectivityManager mConnMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = mConnMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo infoM = mConnMgr
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		
		if (info != null && info.isConnected()) {
			Log.d("liuyg1","CONNECT_TYPE_WIFI");
			return CONNECT_TYPE_WIFI;
		} else if (infoM != null && infoM.isConnected()) {
			Log.d("liuyg1","CONNECT_TYPE_MOBILE");
			return CONNECT_TYPE_MOBILE;
		}
		Log.d("liuyg1","CONNECT_TYPE_OTHER");
		return CONNECT_TYPE_OTHER;
	}
	private void showConfirmDownloadDialog(final Context context){
		Log.d("liuyg1","showConfirmDownloadDialog");
		mDialog_view.setVisibility(View.INVISIBLE);
		LeAlertDialog dialog = new LeAlertDialog(context, R.style.Theme_LeLauncher_Dialog_Shortcut);
		 Window window = dialog.getWindow();
		 WindowManager.LayoutParams lp = window.getAttributes();
		 lp.dimAmount = 0.0f;
		 window.setAttributes(lp);
		dialog.setTitle(R.string.letongbu_install_dialog_title);
		dialog.setLeMessage(context.getText(R.string.d_wifi_m));
		dialog.setLeNegativeButton(context.getText(R.string.cancel_action), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				mDownload_From_3G = false;
				Log.d("liuyg1","showConfirmDownloadDialog Negative onClick");
				dialog.dismiss();
				mDialog_view.setVisibility(View.VISIBLE);
			}
		});

		dialog.setLePositiveButton(context.getText(R.string.rename_action), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Log.d("liuyg1","showConfirmDownloadDialog positive onClick");
    			Message msg1 = new Message();  
    			msg1.what = MSG_DOWNLOAD_From3G;
    			mDowloadHandler.sendMessage(msg1);
				mDownload_From_3G = true;
				dialog.dismiss();
			}
		});
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				Log.d("liuyg1","showConfirmDownloadDialog onCancel");
				mDownload_From_3G = false;
				dialog.dismiss();
				mDialog_view.setVisibility(View.VISIBLE);
			}
		});
		dialog.show();
		Log.d("liuyg1","showConfirmDownloadDialog dialog.show()");
	}
	@Override
	public void againDonwloadQiezi(Context context) {
		// TODO Auto-generated method stub
		mContext = context;
		downloadQiezi(context);
	}
    }






