package com.lenovo.launcher2.bootpolicy;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
//import android.util.Log;
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
import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;
import com.lenovo.launcher.components.XAllAppFace.XLauncher;
import com.lenovo.launcher2.customizer.ConstantAdapter;
import com.lenovo.launcher2.customizer.SettingsValue;

 class UpdateRemindDialog implements DialogInterface.OnCancelListener, DialogInterface.OnDismissListener,
	DialogInterface.OnShowListener  {
	private Dialog mLeosDialog = null;
//	private	Button mCancle;
//	private	Button mFinsh;
//	private	TextView mUserExpText;
//	private	CheckBox mUserExpCheckBox;
	private	Context mContext;
	private Handler mHandler;
	Dialog createDialog( Context context, Handler handler) {
			mContext = context;
			mHandler = handler;
			mLeosDialog = new Dialog(mContext,R.style.Theme_LeLauncher_DialogFullscreen);// , R.style.menu_style);
			mLeosDialog.setCanceledOnTouchOutside(true);
			mLeosDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			mLeosDialog.setContentView(R.layout.boot_policy_remind_dialog);
			Window window = mLeosDialog.getWindow();

			window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			window.setWindowAnimations(R.style.dialogWindowAnim);
			window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			window.setGravity(Gravity.CENTER );

			mLeosDialog.setOnShowListener(this);
			mLeosDialog.setCancelable(false);


			Button finshBtn = (Button) mLeosDialog.findViewById(R.id.btn_finish);
			
//			LinearLayout lBtn = (LinearLayout)mLeosDialog.findViewById(R.id.btn_experience);
			finshBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					cleanup();
					BootPolicyUtility.recordVersion(mContext);
					BootPolicyUtility.setDefaultRegularPref();
					Message msg1 = new Message();  
					msg1.what = BootPolicyUtility.UPDATE_PROFILE_START;
					mHandler.sendMessage(msg1);
//					Log.d("liuyg1","sendMessage msg.what ==  PolicyConstant.BACKUP_PROFILE_START");
//					BootPolicyUtility.setDefaultWallpaper(mContext);
				}

			});
			Button canleBtn = (Button) mLeosDialog.findViewById(R.id.btn_cancel);
			canleBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					cleanup();
					BootPolicyUtility.recordVersion(mContext);
					BootPolicyUtility.setDefaultRegularPref();
					SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
					String defaultAndroidTheme = SettingsValue.getDefaultAndroidTheme(mContext);
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putString(SettingsValue.PREF_THEME_DEFAULT, defaultAndroidTheme);
//					editor.putString(SettingsValue.PREF_THEME, defaultAndroidTheme);
//					editor.putInt(SettingsValue.PREF_ICON_BG_STYLE, SettingsValue.DEFAULT_ICON_BG_INDEX);
					//运营商定制手机，要求加载默认主题，不使用主题图标。此设置将恢复配置为true，即加载默认主题时应用图标
					editor.putBoolean(SettingsValue.PREF_USE_DEFAULTTHEME_ICON, true);
					
					editor.commit();
					SharedPreferences pref = mContext.getSharedPreferences(ConstantAdapter.PREF_FIRST_LAUNCH_CHECK_NAME,
							Activity.MODE_APPEND | Activity.MODE_PRIVATE);
					pref.edit().putBoolean(ConstantAdapter.EXCLUDED_SETTING_KEY, true).commit();
//					BootPolicyUtility.setDefaultWallpaper(mContext);
					getLauncher(mContext).restartLauncher();

				}

			});
			TextView tipsTxt = (TextView)mLeosDialog.findViewById(R.id.txt_remind_title2);
			String title2 =  mContext.getString(R.string.boot_remind_dialog_title2);
			PackageManager manager = mContext.getPackageManager();
			try { PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
			String appVersion = info.versionName;   //版本名
			if(appVersion.contains("_")){
				int end = appVersion.indexOf("_");
				appVersion = appVersion.substring(0,end);
			}
			title2 = title2.replaceFirst("V0.0.0", appVersion);
			tipsTxt.setText(title2);
			
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
//			mUserExpText  = (TextView) mLeosDialog.findViewById(R.id.user_exprierence_text);
//			mUserExpText.setText(Html.fromHtml("<u>"+mContext.getString(R.string.boot_remind_dialog_userqcc_linktext)+"</u>")); 
//			mUserExpText.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					mUserExpCheckBox.setChecked(true);
//	                Intent intent = new Intent();
//		            intent.setAction("android.intent.action.LENUEPROMOTION");
//		            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		            mContext.startActivity(intent);
//
//				}
//
//			});
//			
//			mUserExpCheckBox = (CheckBox)mLeosDialog.findViewById(R.id.user_exprierence_checkbox);
//			mUserExpCheckBox.setOnCheckedChangeListener(new  CompoundButton.OnCheckedChangeListener() {
//				
//				@Override
//				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//					if (isChecked) {
//						  Intent intent = new Intent();
//				            intent.setAction("android.intent.action.LENUEPROMOTION");
//				            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				            mContext.startActivity(intent);
//			          } 
//				}
//			});
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
		XLauncher getLauncher(Context context){
			try {
				LauncherApplication app = (LauncherApplication) context.getApplicationContext();
				return app.getModel().getCallBack().getLauncherInstance();
			} catch (Exception e) {
				// can not retrieve instance , app may gone
				return null;
			}
		}

	}