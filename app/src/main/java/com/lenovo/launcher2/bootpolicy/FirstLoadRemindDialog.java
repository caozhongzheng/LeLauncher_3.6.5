package com.lenovo.launcher2.bootpolicy;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
//import android.util.Log;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
//import android.widget.CheckBox;
//import android.widget.TextView;

import com.lenovo.launcher.R;
class FirstLoadRemindDialog implements DialogInterface.OnCancelListener, DialogInterface.OnDismissListener,
	DialogInterface.OnShowListener  {
	private Dialog mLeosDialog = null;
	private Context mContext;
	private  Handler mHandler;
		Dialog createDialog(Context context, Handler handler) {
			mContext = context;
			mHandler = handler;
			mLeosDialog = new Dialog(mContext,R.style.Theme_LeLauncher_DialogFullscreen);// , R.style.menu_style);
			mLeosDialog.setCanceledOnTouchOutside(true);
			mLeosDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			mLeosDialog.setContentView(R.layout.boot_policy_first_dialog);
			Window window = mLeosDialog.getWindow();

			window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			window.setWindowAnimations(R.style.dialogWindowAnim);
			window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			window.setGravity(Gravity.CENTER );

			mLeosDialog.setOnShowListener(this);
			mLeosDialog.setCancelable(false);
			TextView licenceTxt =  (TextView) mLeosDialog.findViewById(R.id.text_license);
			
			 String licenseStr1 = mContext.getString(R.string.boot_first_dialog_license1);
		        String licenseStr2 = mContext.getString(R.string.boot_first_dialog_license2);
		        String licenseStr3 = mContext.getString(R.string.boot_first_dialog_license3);
		        String licenseStr4 = mContext.getString(R.string.boot_first_dialog_license4);
		        String licenseStr5 = mContext.getString(R.string.boot_first_dialog_license5);
		        String licenseStr6= mContext.getString(R.string.boot_first_dialog_license6);
		        String licenseStr7 = mContext.getString(R.string.boot_first_dialog_license7);
		        SpannableString ss = new SpannableString(licenseStr1+licenseStr2+licenseStr3+licenseStr4+licenseStr5+licenseStr6+licenseStr7);
		        
		        ss.setSpan(null, licenseStr1.length(), licenseStr1.length()+licenseStr2.length(),
		                   Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		            ss.setSpan(new ClickableSpan(){
		            @Override
		            public void onClick(View widget)
		            {
	            		Intent intent = new Intent();
	            		intent.setAction("android.intent.action.LICENSECONTENT");
	            		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            		mContext.startActivity(intent);
		            }
		            }, licenseStr1.length(), licenseStr1.length()+licenseStr2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		            
		            ss.setSpan(null, licenseStr1.length()+licenseStr2.length()+licenseStr3.length(),
				            licenseStr1.length()+licenseStr2.length()+licenseStr3.length()+licenseStr4.length(),
			                   Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			            ss.setSpan(new ClickableSpan(){
			            @Override
			            public void onClick(View widget)
			            {
		            		Intent intent = new Intent();
		            		intent.setAction("android.intent.action.STATEMENTCONTENT");
		            		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		            		mContext.startActivity(intent);
			            }
			            }, licenseStr1.length()+licenseStr2.length()+licenseStr3.length(),
			            licenseStr1.length()+licenseStr2.length()+licenseStr3.length()+licenseStr4.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			            
			            ss.setSpan(null, licenseStr1.length()+licenseStr2.length()+licenseStr3.length()+licenseStr4.length()+licenseStr5.length(),
					            licenseStr1.length()+licenseStr2.length()+licenseStr3.length()+licenseStr4.length()+licenseStr5.length()+licenseStr6.length(),
				                   Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			            ss.setSpan(new ClickableSpan(){
			            	@Override
			            	public void onClick(View widget)
			            	{
			            		Intent intent = new Intent();
			            		intent.setAction("android.intent.action.LENUEPROMOTION");
			            		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			            		mContext.startActivity(intent);
			            	}
			            }, licenseStr1.length()+licenseStr2.length()+licenseStr3.length()+licenseStr4.length()+licenseStr5.length(),
				            licenseStr1.length()+licenseStr2.length()+licenseStr3.length()+licenseStr4.length()+licenseStr5.length()+licenseStr6.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); 
		            licenceTxt.setText(ss);
		            licenceTxt.setMovementMethod(LinkMovementMethod.getInstance());
		            
			Button finshBtn = (Button) mLeosDialog.findViewById(R.id.btn_finish);
			finshBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					
					BootPolicyUtility.recordVersion(mContext);
					BootPolicyUtility.setDefaultRegularPref();
					Message msg1 = new Message();  
					msg1.what = BootPolicyUtility.RESTORE_DEFAULT_PROFILE;
					mHandler.sendMessage(msg1);
//					Log.d("liuyg1","mHandler.sendMessage  BACKUP_PROFILE_START");
//					BootPolicyUtility.setDefaultWallpaper(mContext);
					cleanup();
				}

			});
			TextView tipsTxt = (TextView)mLeosDialog.findViewById(R.id.txt_remind_tips);
			String tips =  mContext.getString(R.string.boot_first_dialog_tips);
			PackageManager manager = mContext.getPackageManager();
			try { PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
			String appVersion = info.versionName;   //版本名
			Log.d("liuyg123","appVersion"+appVersion);
			if(appVersion.contains("_")){
				int end = appVersion.indexOf("_");
				appVersion = appVersion.substring(0,end);
			}
			tips = tips.replaceFirst("V0.0.0", appVersion);
			tipsTxt.setText(tips);
			
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			
			return mLeosDialog;
		}
		public void onCancel(DialogInterface dialog) {
		}

		public void onDismiss(DialogInterface dialog) {
		}

		private void cleanup() {
			mLeosDialog.dismiss();
		}
		public void onShow(DialogInterface dialog) {
		}
		

	}
	