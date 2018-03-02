package com.lenovo.lejingpin.hw.ui;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.commonui.LeAlertDialog;
import com.lenovo.launcher2.customizer.Reaper;
import com.lenovo.lejingpin.appsmgr.content.UpgradeApp;
import com.lenovo.lejingpin.appsmgr.content.UpgradeApp.Status;
import com.lenovo.lejingpin.appsmgr.content.UpgradeAppDownloadControl;
import com.lenovo.lejingpin.appsmgr.content.UpgradeUtil;
import com.lenovo.lejingpin.share.download.DownloadConstant;
import com.lenovo.lejingpin.share.download.DownloadHandler;
import com.lenovo.lejingpin.share.download.DownloadInfo;
import com.lenovo.lejingpin.share.download.Downloads;
import com.lenovo.lejingpin.share.download.LDownloadManager;
import com.lenovo.lejingpin.share.download.AppDownloadUrl.Callback;

public class HwLocalAppFragment extends Fragment implements Callback {

	public static final String TAG = "HwLocalAppFragment";

	private View mAppView;
//	private TextView mAppCountView;
	private TextView mUpdateAllView;
	private Context mContext;
	private boolean bGetUpgradeFirst = true;
	private boolean bRequest = true;
	private boolean mWaitForBroadcastResult = true;

	private List<UpgradeApp> mUpgradeList = new ArrayList<UpgradeApp>();
	private ExpandableListView mLocalListView;
	private LocalAppAdapter mLocalAppAdapter;
	private LinearLayout mLoadLayout;

	private DownloadStateReceiver mDownloadReceiver;
	private PackageReceiver mPackageReceiver;
	private AppListReceiver mAppListReceiver;

	private static final int MSG_GET_UPGRADE_FIRST = 100;
	private static final int MSG_GET_UPGRADE_LIST = 101;
	private static final int MSG_GET_LOCAL_LIST = 102;
	private static final int MSG_UPGRADE_ALL = 103;
//	private long mTotalUpdateByte = 0;
	
	private static final String SHARE_MIME_TYPE = "application/vnd.android.package-archive";

	private List<LocalApplicationInfo> mLocalList = new ArrayList<LocalApplicationInfo>();
	
	enum UPDATE_STATE {
		NEWEST, NEED_UPDATE
	};

	private class LocalApplicationInfo {
		public boolean isRoot;
		public String name;
		public String versionName;
		public String packageName;
		public String size;
		public String className;
		public String versionCode;
		public Drawable icon;
		public UPDATE_STATE updateState = UPDATE_STATE.NEWEST;
		public UpgradeApp upgradeApp;
	}
	
	private boolean replaceUpgradeApp(UpgradeApp app){
		if(app == null || mUpgradeList == null)
			return false;
		if(app.getPackageName() == null || app.getVersionCode() == null){
			return false;
		}
		for(UpgradeApp tmp: mUpgradeList){
			if (tmp.getPackageName().equals(app.getPackageName())
					&& tmp.getVersionCode().equals(app.getVersionCode())) {
				tmp = app;
				return true;
			}
		}
		return false;
	}
	
	private UpgradeApp getUpgradeApp(String pkName,String version){
		if(mUpgradeList == null)
			return null;
		if(pkName == null || version == null)
			return null;
		
		for (UpgradeApp app : mUpgradeList) {
			if (app.getPackageName().equals(pkName)
					&& app.getVersionCode().equals(version)) {
				return app;
			}
		}
		return null;
	}
	
	private Status convertStatus(int status){
		Status tmp = Status.UNDOWNLOAD;
		if(status == Downloads.STATUS_SUCCESS ){
			tmp = Status.UNINSTALL;
		}else if(status == Downloads.STATUS_RUNNING_PAUSED){
			tmp = Status.PAUSE;
		}else if (status == Downloads.STATUS_RUNNING){
			tmp = Status.DOWNLOADING;
		}else if(status == Downloads.STATUS_INSTALL){
			tmp = Status.INSTALL;
		}
		
		return tmp;
	}
	
    private void showCountView(TextView tv,String string,String editString){
    	ColorStateList redColors = ColorStateList.valueOf(0xFFF6861F);
    	SpannableString tSS = new SpannableString(string);
        int start = string.indexOf(editString);
        int editlen = editString.length();
    	tSS.setSpan(new TextAppearanceSpan(null, 0, 0, redColors, null), start, 
    			start+editlen, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
    	tv.setText(tSS); 
    }

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	private void registerReceiver() {
		IntentFilter pacakgeFilter = new IntentFilter();
		mPackageReceiver = new PackageReceiver();

		pacakgeFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
		pacakgeFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		pacakgeFilter.addDataScheme("package");
		mContext.registerReceiver(mPackageReceiver, pacakgeFilter);

		mAppListReceiver = new AppListReceiver();
		IntentFilter applistFilter = new IntentFilter(
				UpgradeUtil.ACTION_REQUEST_APPUPGRADE_COMPLETE);
		mContext.registerReceiver(mAppListReceiver, applistFilter);

		IntentFilter filter = new IntentFilter();
		filter.addAction(DownloadConstant.ACTION_DOWNLOAD_STATE_CHANGED);
		filter.addAction(DownloadConstant.ACTION_APK_FAILD_DOWNLOAD);
		filter.addAction(DownloadConstant.ACTION_DOWNLOAD_DELETE);
		filter.addAction(DownloadConstant.ACTION_DOWNLOAD_RESUME);
		
		mDownloadReceiver = new DownloadStateReceiver(mContext);
		mContext.registerReceiver(mDownloadReceiver, filter);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		Log.i(TAG,"------------------density:" + mContext.getResources().getDisplayMetrics().density);
		registerReceiver();
	}

	private void handleGetMessage(int msgID) {
		mInitHandler.removeMessages(msgID);
		mInitHandler.sendEmptyMessage(msgID);
	}

	private void refreshUpgradeList() {
		if(!Util.getInstance().isNetworkEnabled(getActivity())){
			return;
		}
		if (bGetUpgradeFirst) {
			handleGetMessage(MSG_GET_UPGRADE_FIRST);
			bGetUpgradeFirst = false;
		} else
			handleGetMessage(MSG_GET_UPGRADE_LIST);
	}

	private void showLocalListView() {
		if(mLocalList.size() == 0)
			return;
		
		sortLocalList(mLocalList);
		mLocalListView.setVisibility(View.VISIBLE);
		mLoadLayout.setVisibility(View.GONE);

		mLocalAppAdapter.notifyDataSetChanged();
	}

	private class PackageReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG,"UpgradeAppListsActivity.PackageReceiver.onReceiver(),action:"+ intent.getAction());
			
			mInitHandler.sendEmptyMessage(MSG_GET_LOCAL_LIST);
			refreshUpgradeList();
		}
	}

	private Handler mInitHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_GET_UPGRADE_FIRST:
//				if(!Util.getInstance(getActivity()).isNetworkEnabled()){
//					UpgradeAppDownloadControl.showNetworkEnableDialog(mContext, mContext.getString(R.string.letongbu_install_dialog_title));
//					break;
//				}
				
				bRequest = true;
				mWaitForBroadcastResult = true;
				getUpgradeAppListFirst();
				break;
			case MSG_GET_UPGRADE_LIST:
//				if(!Util.getInstance(getActivity()).isNetworkEnabled()){
//					UpgradeAppDownloadControl.showNetworkEnableDialog(mContext, mContext.getString(R.string.letongbu_install_dialog_title));
//					break;
//				}
				bRequest = true;
				mWaitForBroadcastResult = true;
				getUpgradeAppList();
				break;
			case MSG_GET_LOCAL_LIST:
				getLocalAppList(true);
				break;
			case MSG_UPGRADE_ALL:
//				if(!Util.getInstance(getActivity()).isNetworkEnabled()){
//					UpgradeAppDownloadControl.showNetworkEnableDialog(mContext, mContext.getString(R.string.theme_settings_title));
//					break;
//				}
				 UpgradeAppDownloadControl.prepareDownloadAll(mUpgradeList,mContext);
				break;
			default:
					break;
			}
		}
	};

	private void sendBroadcastForUpgradeList() {
		Log.i(TAG,
				"+++++++++++++++++++++++++++UpgradeAppListsActivity.sendBroadcastForAppList, load......");
		String action = UpgradeUtil.ACTION_REQUEST_APPUPGRADE;
		Intent intent = new Intent();
		intent.setAction(action);
		mContext.sendBroadcast(intent);
	}

	private class DownloadStateReceiver extends BroadcastReceiver {
		private Context mContext;

		public DownloadStateReceiver(Context c) {
			mContext = c;
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "DownloadStateReceiver.onReceive");

			String action = intent.getAction();
			String pkg = intent.getStringExtra(DownloadConstant.EXTRA_PACKAGENAME);
			String vcode = intent.getStringExtra(DownloadConstant.EXTRA_VERSION);
			Status s = null;
			Log.i(TAG, "DownloadStateReceiver.onReceive, action:" + action
					+ ", pkg:" + pkg + ",category:"+ intent.getIntExtra(DownloadConstant.EXTRA_CATEGORY, DownloadConstant.CATEGORY_ERROR));
			boolean isReDownloadFlag = false;
			if (DownloadConstant.ACTION_DOWNLOAD_STATE_CHANGED.equals(action)) {
				s = Status.parseStatus(intent
						.getStringExtra(DownloadConstant.EXTRA_STATUS));
			} else if ((DownloadConstant.ACTION_APK_FAILD_DOWNLOAD
					.equals(action))
					|| (DownloadConstant.ACTION_DOWNLOAD_DELETE.equals(action))) {
				s = Status.UNDOWNLOAD;
				int result = intent.getIntExtra(DownloadConstant.EXTRA_RESULT, DownloadConstant.FAILD_DOWNLOAD_OTHER_ERROR);
				Log.i(TAG,"---------------------fail result:" + result);
				if(DownloadConstant.FAILD_DOWNLOAD_NETWORK_ERROR == result){
					Toast.makeText(context, R.string.download_net_error,
							Toast.LENGTH_SHORT).show();
				}else if(DownloadConstant.FAILD_DOWNLOAD_NO_ENOUGHT_SPACE == result){
					Toast.makeText(context, R.string.download_sdcard_notexists,
							Toast.LENGTH_SHORT).show();
				}
			}else if(DownloadConstant.ACTION_DOWNLOAD_RESUME.equals(action)){
				UpgradeApp app = getUpgradeApp(pkg,vcode);
				if(app != null){
					Log.i(TAG, "DownloadStateReceiver.onReceive, -------------ACTION_DOWNLOAD_RESUME-----------------");
					DownloadHandler.getInstance(context).registerDownloadCallback(pkg, vcode, app.getCallback());
				}
				return;
			}
			
			if(DownloadConstant.ACTION_APK_FAILD_DOWNLOAD.equals(action))
				isReDownloadFlag = true;
			UpgradeApp app = updateDownloadStatusByPackageName(mContext, pkg,
					vcode, s,isReDownloadFlag);
			if (app == null)
				return;
			if(Status.UNDOWNLOAD == s){
				app.setControl(Downloads.Impl.CONTROL_PAUSED);
			}

			View view = mLocalListView.findViewWithTag(new LocalViewHolder(pkg));
			if (view == null)
				return;
			
			UpgradeAppDownloadControl.drawDownloadState(app, view, mContext);
			
			LocalApplicationInfo localAppInfo = getLocalApp(pkg);
			if(localAppInfo != null)
				refreshUpgradeItem(localAppInfo,view);
		}
	}
	
	private LocalApplicationInfo getLocalApp(String pkName){
		LocalApplicationInfo appInfo = null;
		if (mLocalList != null) {
			for (LocalApplicationInfo app : mLocalList) {
				if (app.packageName.equals(pkName)) {
					appInfo = app;
					break;
				}
			}
		}
		return appInfo;
	}

	private UpgradeApp updateDownloadStatusByPackageName(Context context,
			String pkg, String vcode, Status status, boolean flag) {
		Log.i(TAG,
				"UpgradeAppListsActivity.updateDownloadStatusByPackageName(), pkg:"
						+ pkg + ", status:" + status + ",vcode:" + vcode);
		UpgradeApp appInfo = null;
		if (mUpgradeList != null) {
			for (UpgradeApp app : mUpgradeList) {
				if (app.getPackageName().equals(pkg)
						&& app.getVersionCode().equals(vcode)) {
					app.setStatus(status);
					app.setReDownloadFlag(flag);
					appInfo = app;
					break;
				}
			}
		}
		return appInfo;
	}
	
	private boolean updateUpgradeApp(UpgradeApp capp) {
		if(capp == null)
			return false;
		Log.i(TAG,
				"updateUpgradeApp, pkg:" + capp.getPackageName());
		if (mUpgradeList != null) {
			for (UpgradeApp app : mUpgradeList) {
				if (app.getPackageName().equals(capp.getPackageName())
						&& app.getVersionCode().equals(capp.getVersionCode())) {
					app = capp;
					return true;
				}
			}
		}
		return false;
	}

	// ------Get App List------
	private class AppListReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			if ((UpgradeUtil.ACTION_REQUEST_APPUPGRADE_COMPLETE)
					.equals(intent.getAction())) {
				Log.i(TAG, "UpgradeAppListActivity.AppListReceiver()");
				mWaitForBroadcastResult = false;
				bRequest = intent.getBooleanExtra("result", true);
				getUpgradeAppList();
			}
		}
	}

	private void getUpgradeAppListFirst() {
		if (UpgradeUtil.isNeedLoadFromServer(mContext)) {
			sendBroadcastForUpgradeList();
		} else
			getUpgradeAppList();
	}

	private void clearUpgradeList() {
		if(mUpgradeList != null){
			mUpgradeList.clear();
		}
		for (LocalApplicationInfo app : mLocalList) {
			app.updateState = UPDATE_STATE.NEWEST;
			app.upgradeApp = null;
		}
	}
	
	private boolean isShowPrograss(UpgradeApp app){
		if(app == null)
			return false;
		if(Status.UNDOWNLOAD == app.getStatus())
			return false;
		if(Status.UNINSTALL == app.getStatus())
			return false;
		return true;
	}
	
	private void showAppInfo(View view, LocalApplicationInfo app){
		if(app == null)
			return;
		LocalViewHolder holder = (LocalViewHolder)view.getTag();
		
		String vn;
		String size;
		if(app.upgradeApp != null){
			vn = getString(R.string.magicdownload_upgrade_currentversion)
					+ app.versionName + " -> " +app.upgradeApp.getVersionName();
			float s;
			try{
				s = (float) (Integer.valueOf(app.upgradeApp.getAppSize())/((1024.0)*(1024.0)));
			}catch(NumberFormatException e){
				s = 0;
			}
			size = getString(R.string.local_app_size) 
					+ app.size + " -> " + String.format("%.2f", s)+"M";
		}else{
			vn = getString(R.string.magicdownload_upgrade_currentversion)
					+ app.versionName;
			size = getString(R.string.local_app_size) + app.size;
		}
//		holder.appIconProgress.setProgress(0);
		holder.appSize.setVisibility(View.VISIBLE);
		holder.appSize.setText(size);
		holder.appVersion.setVisibility(View.VISIBLE);
		holder.appVersion.setText(vn);
//		holder.appIconProgress.setBackgroundDrawable(app.icon);
	}
	
	private void showDownloadPrograss(View view,LocalApplicationInfo app){
		LocalViewHolder holder = (LocalViewHolder)view.getTag();
		if(app == null || app.upgradeApp == null)
			return;
		long currentBytes = app.upgradeApp.getCurrentBytes();
		long totalBytes = app.upgradeApp.getTotalBytes();
		int progress = 0;
		float cb = (float)currentBytes/(1024*1024);
		float tb = (float)totalBytes/(1024*1024);
		String bytes = String.format("%.2f", cb) + "M/" + String.format("%.2f", tb) + "M";

//		if(app.getReDownloadFlag()){
//			holder.appDownloadProssesStatus.setText(R.string.local_app_download_error);
//		}else{
//			holder.appDownloadProssesStatus.setText(R.string.download_pause);
//		}
		
		holder.appSize.setVisibility(View.GONE);
		holder.appVersion.setVisibility(View.GONE);
		holder.downloadProssesLayout.setVisibility(View.VISIBLE);
		holder.appDownloadProsses.setVisibility(View.VISIBLE);

		if(currentBytes != 0 && totalBytes != 0){
			progress = (int)(currentBytes/(float)totalBytes*100);
		}
//		showAppInfo(view,app);
		
//		holder.appIconProgress.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.download_app_icon_def));
//		holder.appIconProgress.setProgress(progress);

//		Log.d(TAG,"---------------------------progress:"+progress+",name:"+app.name);
//		
		holder.appDownloadProssesView.setText(bytes);
		holder.appDownloadProsses.setProgress(progress);
	}
	
	private void showAppDetail(View view,LocalApplicationInfo app){
		if(app == null)
			return;
		LocalViewHolder holder = (LocalViewHolder)view.getTag();
//		
//		String vn;
//		String size;
//		if(app.upgradeApp != null){
//			vn = getString(R.string.magicdownload_upgrade_currentversion)
//					+ app.versionName + " -> " +app.upgradeApp.getVersionName();
//			float s;
//			try{
//				s = (float) (Integer.valueOf(app.upgradeApp.getAppSize())/((1024.0)*(1024.0)));
//			}catch(NumberFormatException e){
//				s = 0;
//			}
//			size = getString(R.string.local_app_size) 
//					+ app.size + " -> " + String.format("%.2f", s)+"M";
//		}else{
//			vn = getString(R.string.magicdownload_upgrade_currentversion)
//					+ app.versionName;
//			size = getString(R.string.local_app_size) + app.size;
//		}
		holder.downloadProssesLayout.setVisibility(View.GONE);
		holder.appDownloadProsses.setVisibility(View.GONE);
		showAppInfo(view,app);
//		holder.appIconProgress.setProgress(0);
//		holder.appSize.setVisibility(View.VISIBLE);
//		holder.appSize.setText(size);
//		holder.appVersion.setVisibility(View.VISIBLE);
//		holder.appVersion.setText(vn);
//		holder.appIconProgress.setBackgroundDrawable(app.icon);
		Log.d(TAG,"---showAppDetail-----name:" + app.name);
	}
	
	private void refreshUpgradeItem(final LocalApplicationInfo app,final View view){
		if(app == null || view == null)
			return ;
		LocalViewHolder holder = (LocalViewHolder)view.getTag();

		if( app.upgradeApp != null && isShowPrograss(app.upgradeApp)){
			showDownloadPrograss(view,app);
		}else{
			showAppDetail(view,app);
		}
		holder.appUpgrade.setVisibility(View.VISIBLE);
		holder.appUpgrade.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
					UpgradeAppDownloadControl.prepareDownload(
							app.upgradeApp, mContext);
					
					UpgradeAppDownloadControl.drawDownloadState(
							app.upgradeApp, view, mContext);
			}
		});
		
		UpgradeAppDownloadControl.drawDownloadState(
				app.upgradeApp, view,mContext);
	}

	public void getUpgradeAppListFinished() {
		Log.i(TAG, "getUpgradeAppListFinished()");
		showLocalListView();
	}

	public void getUpgradeAppList() {
		if (!bRequest) {
			getUpgradeAppListFinished();
			return;
		}
		List<UpgradeApp> appList = UpgradeUtil.getAllUpgradeAppList(mContext);
		List<DownloadInfo> downloadInfos = LDownloadManager.getDefaultInstance(
				mContext).getAllDownloadInfo();

		if (appList != null && !appList.isEmpty()) {
			for (UpgradeApp app : appList) {
				int downloadStatus = -1;
				if (downloadInfos != null) {
					for (DownloadInfo downloadInfo : downloadInfos) {
						if (app.getPackageName().equals(
								downloadInfo.getPackageName())
								&& app.getVersionCode().equals(
										downloadInfo.getVersionCode())) {
							downloadStatus = downloadInfo.getDownloadStatus();
							long b = downloadInfo.getTotalBytes();
							app.setTotalBytes(b);
							app.setCurrentBytes(downloadInfo.getCurrentBytes());
							app.setControl(downloadInfo.getControl());
							downloadInfos.remove(downloadInfo);
							Log.d(TAG,
									"pkg=" + downloadInfo.getPackageName()
											+ ", vcode="
											+ downloadInfo.getVersionCode()
											+ ", downloadstatus="
											+ downloadInfo.getDownloadStatus()
											+ ", category="
											+ app.getCategory()
											+ ", totalbytes=" 
											+ app.getTotalBytes());
							break;
						}
					}
				}
				if (downloadStatus == Downloads.STATUS_RUNNING_PAUSED) {
					app.setStatus(Status.PAUSE);
				} else if (downloadStatus == Downloads.STATUS_SUCCESS) {
					app.setStatus(Status.UNINSTALL);
				} else if (downloadStatus == Downloads.STATUS_RUNNING) {
					app.setStatus(Status.DOWNLOADING);
				} else if (downloadStatus == Downloads.STATUS_INSTALL) {
					app.setStatus(Status.INSTALL);
				}
				app.setCallback(this);
				DownloadHandler.getInstance(mContext).registerDownloadCallback(app.getPackageName(), app.getVersionCode(), this);
			}
			getUpgradeAppList(appList);
			mWaitForBroadcastResult = false;
			getUpgradeAppListFinished();
		} else {
			Log.i(TAG,
					"++++++++++++++++++++++++++UpgradeAppListsActivity.loadAppList, load");
			if (mWaitForBroadcastResult) {
				sendBroadcastForUpgradeList();
			} else
				getUpgradeAppListFinished();
		}
	}

	private void getUpgradeAppList(List<UpgradeApp> mTempApps) {
		if (mTempApps != null && !mTempApps.isEmpty()) {
			clearUpgradeList();
			// mUpgradeList.clear();
			for (int oldPos = 0; oldPos < mLocalList.size(); oldPos++) {
				LocalApplicationInfo oldApp = mLocalList.get(oldPos);
				Float oldVersionCode = Float.valueOf(oldApp.versionCode);
				oldApp.updateState = UPDATE_STATE.NEWEST;
				oldApp.upgradeApp = null;
				for (int newPos = 0; newPos < mTempApps.size(); newPos++) {
					UpgradeApp newApp = mTempApps.get(newPos);
					if (newApp.getStatus().equals(Status.INSTALL))
						continue;
					Float newVersionCode = Float.valueOf(newApp
							.getVersionCode());
					// Log.i(TAG,"old version code:"+
					// oldVersionCode+", new version code:"+ newVersionCode);
					if (oldApp.packageName.equals(newApp.getPackageName())
							&& (oldVersionCode < newVersionCode)) {
						newApp.setOldName(oldApp.name);
						newApp.setOldVersion(oldApp.versionName);
						// UpgradeLocalIconInfo iconInfo = new
						// UpgradeLocalIconInfo();
						// iconInfo.packageName = newApp.getPackageName();
						// iconInfo.icon = oldApp.icon;
						// mUpgradeLocalIconList.add(iconInfo);
						// LocalApplicationInfo localInfo =
						// mLocalList.get(oldPos);
						oldApp.updateState = UPDATE_STATE.NEED_UPDATE;
						mUpgradeList.add(newApp);
						oldApp.upgradeApp = mUpgradeList.get(mUpgradeList
								.size() - 1);
						mLocalList.set(oldPos, oldApp);

						mTempApps.remove(newPos);
						break;
					}
				}
			}
			
			String classes = "com.lenovo.lejingpin.ClassicFragmentActivity";
			String oldcount = Settings.System.getString(mContext.getContentResolver(), "NEWMSG_" + mContext.getPackageName()
					+ "/" + classes);
			Log.d(TAG,"oldcount:" + oldcount + ",newcount:" + mUpgradeList.size());
			if(oldcount == null || !oldcount.equals(String.valueOf(mUpgradeList.size()))){
				Settings.System.putString(mContext.getContentResolver(), "NEWMSG_" + mContext.getPackageName() + "/" + classes,
								String.valueOf(mUpgradeList.size()));
				Intent updateCountIntent = new Intent("com.android.intent.action.NEW_NOTIFICATION");
				updateCountIntent.putExtra("packageName", mContext.getPackageName() + "/" + classes);
				updateCountIntent.putExtra("messageNum", mUpgradeList.size());
				mContext.sendBroadcast(updateCountIntent);
			}
		}
	}

	private void getLocalAppList(boolean bShow) {
		Log.d(TAG,
				"*****************************UpgradeAppListsActivity getLocalAppList......time:"
						+ System.currentTimeMillis());
		if (mLocalList == null)
			return;
		mLocalList.clear();
		// mLocalCount = 0;
		PackageManager pm = mContext.getPackageManager();
		List<PackageInfo> packages = pm.getInstalledPackages(0);
		for (int i = 0; i < packages.size(); i++) {
			PackageInfo packageInfo = packages.get(i);
			if (packageInfo == null)
				continue;
			ApplicationInfo appInfo = packageInfo.applicationInfo;
			if (appInfo == null)
				continue;
			Intent intent = pm.getLaunchIntentForPackage(appInfo.packageName);
			if(intent == null || intent.getAction() == null ||  !intent.getAction().equals(Intent.ACTION_MAIN))
				continue;
			if(packageInfo.packageName.startsWith("com.lenovo.launcher.theme"))
				continue;
//			Set<String> set = intent.getCategories();
//			boolean ignore = true;
//			for(Iterator<String> iterator = set.iterator();iterator.hasNext();){
//				if(iterator.next().equals(Intent.CATEGORY_LAUNCHER)){
//					ignore = false;
//				}
//			}
//			if(ignore)
//				continue;
			
			Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
			resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			resolveIntent.setPackage(appInfo.packageName);
			String className = null;
			
			List<ResolveInfo> apps = mContext.getPackageManager().queryIntentActivities(resolveIntent, 0);
			if(apps.iterator().hasNext()){
				ResolveInfo ri = apps.iterator().next();
				className = ri.activityInfo.name;
			}
//			String className = packageInfo.applicationInfo.;
			
			if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0 
					|| (className != null && !className.isEmpty())) {
				LocalApplicationInfo info = new LocalApplicationInfo();
				if((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0)
					info.isRoot = false;
				else
					info.isRoot = true;
				info.name = appInfo.loadLabel(mContext.getPackageManager())
						.toString();
				info.packageName = packageInfo.packageName;
				info.versionName = packageInfo.versionName;
				info.versionCode = String.valueOf(packageInfo.versionCode);
				info.icon = appInfo.loadIcon(mContext.getPackageManager());
				float size = Integer.valueOf((int)new File(appInfo.publicSourceDir).length());
				info.size = String.format("%.2f", size/(1024*1024)) + "M";
				
				if(mUpgradeList != null){
					for(int j = 0; j<mUpgradeList.size();j++){
						if(info.packageName.equals(mUpgradeList.get(j).getPackageName())){
							info.upgradeApp = mUpgradeList.get(j);
							info.updateState = UPDATE_STATE.NEED_UPDATE;
						}
					}
				}
				
				info.className = className;
				mLocalList.add(info);
			}
		}
//		sortLocalList(mLocalList);
		if (bShow)
			showLocalListView();
		Log.d(TAG,
				"*****************************UpgradeAppListsActivity getLocalAppList......end,time:"
						+ System.currentTimeMillis());
		return;
	}

	private void sortLocalList(List<LocalApplicationInfo> list) {
		if (list != null && !list.isEmpty()){
			Collections.sort(list, new ComparatorLocal());
		}
	}

	private class ComparatorLocal implements Comparator<LocalApplicationInfo> {
		@Override
		public int compare(LocalApplicationInfo object1,
				LocalApplicationInfo object2) {
			if(null == object1 || null == object2)
				return 0;
			String s1 = object1.name;
			String s2 = object2.name;
			if (s1 == null || s2 == null)
				return 0;
			Collator myCollator = Collator.getInstance(java.util.Locale.CHINA);
			int lLevel = getLevel(object1);
			int rLevel = getLevel(object2);
			
			if(lLevel < rLevel){
				return 1;
			}else if(lLevel == rLevel){
				return myCollator.compare(s1.trim(), s2.trim());
			}else{
				return -1;
			}
		}
		
		int getLevel(LocalApplicationInfo app){
			boolean ignore = false;
			if(app.upgradeApp != null){
				ignore = app.upgradeApp.getUpdateIgnore();
			}
				
			int level = 0;
			if(UPDATE_STATE.NEED_UPDATE == app.updateState && !app.isRoot && !ignore)
				level = 5;
			else if(UPDATE_STATE.NEED_UPDATE == app.updateState && app.isRoot && !ignore)
				level = 4;
			else if(UPDATE_STATE.NEED_UPDATE == app.updateState && ignore)
				level = 3;
			else if(UPDATE_STATE.NEWEST == app.updateState && !app.isRoot)
				level = 2;
			else if(UPDATE_STATE.NEWEST == app.updateState && app.isRoot)
				level = 1;
			return level;
				
		}
	}
	
	private void backupAndRestore(){
		String packagename = "com.lenovo.leos.cloud.sync";
		if(checkAppIsExistAndEnable(packagename)){
			Reaper.processReaper(mContext, UpgradeAppDownloadControl.REAPER_EVENT_CATEGORY, UpgradeAppDownloadControl.REAPER_EVENT_ID_BACKUP, Reaper.REAPER_NO_LABEL_VALUE, Reaper.REAPER_NO_INT_VALUE);
			Intent intent = new Intent("com.lenovo.leos.cloud.sync.intent.action.MAIN");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("DEFAULT_ITEM", "appMain");
			intent.putExtra("isShowFinishDlg", false);
			mContext.startActivity(intent);
		}
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
				
		LDownloadManager.getDefaultInstance(mContext);
		mAppView = inflater.inflate(R.layout.fragment_app, container, false);
		mLoadLayout = (LinearLayout)mAppView.findViewById(R.id.loading_layout);
		mLoadLayout.setVisibility(View.VISIBLE);
		mLocalListView = (ExpandableListView) mAppView
				.findViewById(R.id.localapplistview);
		mLocalAppAdapter = new LocalAppAdapter();
		mLocalListView.setAdapter(mLocalAppAdapter);
		mLocalListView.setClickable(false);
		mLocalListView.setVisibility(View.GONE);
		
		AsyncTask.execute(new Runnable(){
			@Override
			public void run() {
				mInitHandler.sendEmptyMessage(MSG_GET_LOCAL_LIST);
				refreshUpgradeList();
				Reaper.processReaper(mContext, UpgradeAppDownloadControl.REAPER_EVENT_CATEGORY, UpgradeAppDownloadControl.REAPER_EVENT_ID_ENTRY, Reaper.REAPER_NO_LABEL_VALUE, Reaper.REAPER_NO_INT_VALUE);
			}
			
		});

//		mInitHandler.sendEmptyMessage(MSG_GET_LOCAL_LIST);
//		refreshUpgradeList();
		
		return mAppView;
	}

	@Override
	public void onDestroy() {
		if(mPackageReceiver != null){ 
			mContext.unregisterReceiver(mPackageReceiver);
		}
		if(mDownloadReceiver != null) {
			mContext.unregisterReceiver(mDownloadReceiver);
		}
		if(mAppListReceiver !=null){
			mContext.unregisterReceiver(mAppListReceiver);
		}
		
		if (mUpgradeList != null) {
			mUpgradeList.clear();
		}
		if (mLocalList != null) {
			mLocalList.clear();
		}
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
			LDownloadManager.getDefaultInstance(mContext).deleteAllTask();
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	final class LocalViewHolder {
		ImageView appIcon;
//		ProgressBar appIconProgress;
		ImageView foldIcon;
		TextView appName;
		TextView appSize;
		TextView appVersion;
		TextView appUpgrade;
		TextView appDownloadProssesStatus;
		TextView appDownloadProssesView;
		ProgressBar appDownloadProsses;
		LinearLayout downloadProssesLayout;
		LinearLayout localAppsItem;
		LinearLayout localAppsItemOption;
		String packageName;
		String versionCode;
		int position;
		int group;
//		boolean hasOption;

		public LocalViewHolder() {
			super();
		}

		public LocalViewHolder(String packageName) {
			super();
			this.packageName = packageName;
		}
		
		public LocalViewHolder(String packageName, String versionCode) {
			super();
			this.packageName = packageName;
//			this.versionCode = versionCode;
		}
		
		@Override
		public boolean equals(Object o) {
			if (o instanceof LocalViewHolder && packageName != null && !packageName.isEmpty()) {
				LocalViewHolder vh = (LocalViewHolder) o;
				return packageName.equals(vh.packageName);
			}
			return false;
		}

//		@Override
//		public boolean equals(Object o) {
//			if (o instanceof LocalViewHolder && packageName != null
//					&& versionCode != null && !packageName.isEmpty()
//					&& !versionCode.isEmpty()) {
//				LocalViewHolder vh = (LocalViewHolder) o;
//				Log.i(TAG, "--------------LocalViewHolder,vh.packageName:"
//						+ vh.packageName + ",packageName:" + packageName
//						+ ",vh.versionCode:" + vh.versionCode + ",versionCode:"
//						+ versionCode);
//				return packageName.equals(vh.packageName)
//						&& versionCode.equals(vh.versionCode);
//
//			}
//			return false;
//		}
	}

	private class LocalAppAdapter extends BaseExpandableListAdapter {
		Integer[] mGroups = {R.string.magicdownload_upgrade_upgradecount,R.string.local_app_group_local};
		final int UPGRADE_APPS_GROUP_ID = 0;
		final int LOCAL_APPS_GROUP_ID = 1;
		
		final int APPS_OPTION_ID_MANAGER = 1;
		final int APPS_OPTION_ID_RUN = 2;
		final int APPS_OPTION_ID_SHARE = 3;
		final int APPS_OPTION_ID_UNINSTALL = 4;
		final int APPS_OPTION_ID_UPDATE_IGNORE = 5;
		//id , stringid , drawable id
		int[][] optionFun={
				{APPS_OPTION_ID_MANAGER,R.string.local_app_manager,R.drawable.ic_local_apps_manage_selector},
				{APPS_OPTION_ID_RUN,R.string.local_app_run,R.drawable.ic_local_apps_run_selector},
				{APPS_OPTION_ID_SHARE,R.string.local_app_share,R.drawable.ic_local_apps_share_selector},
				{APPS_OPTION_ID_UNINSTALL,R.string.magicdownload_upgrade_uninstall,R.drawable.ic_delete_selector},
				{APPS_OPTION_ID_UPDATE_IGNORE,R.string.local_app_update_ignore,R.drawable.ic_update_ignore_selector}
		};
		
		LayoutInflater inflater;
		//expand item's package name
		private String mExpandItemPN;
		
		public LocalAppAdapter() {
			super();
			inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		private void ignoreAppUpdate(View view,Context context,LocalApplicationInfo app){
			if(app == null)
				return;
			if(app.upgradeApp == null)
				return;
			LDownloadManager.getDefaultInstance(context).deleteTask(
					new DownloadInfo(app.upgradeApp.getPackageName(),app.upgradeApp.getVersionCode()));
			notifyDelete(app.upgradeApp.getCategory(),app.upgradeApp.getPackageName(), app.upgradeApp.getVersionCode(), app.upgradeApp.getAppName(), DownloadConstant.ACTION_DOWNLOAD_DELETE);
			
			if(app.upgradeApp.getUpdateIgnore()){
				app.upgradeApp.setUpdateIgnore(false);
			}else{
				app.upgradeApp.setUpdateIgnore(true);
			}
			mExpandItemPN = null;
			updateUpgradeApp(app.upgradeApp);
			UpgradeUtil.updateUpgradeApp(context, app.upgradeApp);
			showLocalListView();
		}
		
		@Override
		public LocalApplicationInfo getChild(int groupPosition, int childPosition) {
			if(groupPosition < 0 || childPosition < 0)
				return null;
			// TODO Auto-generated method stub
			int groupid = (int) getGroupId(groupPosition);
			int childid = childPosition;
			int position = 0;
			
			if(groupid == UPGRADE_APPS_GROUP_ID){
				position = childid;
			}else if(groupid == LOCAL_APPS_GROUP_ID){
				position = childid + mUpgradeList.size();
			}
//			Log.d(TAG,"getChild position :" + position + ",groupid: " + groupid + ",childid: " + childid + ",UpgradeList.size:" + mUpgradeList.size());
			return mLocalList.get(position);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		private void setOnClickListenerById(View tv,int id , final LocalApplicationInfo app){
			if(APPS_OPTION_ID_MANAGER == id){
				tv.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						manageOnClick(app.packageName);
					}
				});
			}
			
			if(APPS_OPTION_ID_RUN == id){
				tv.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						runOnClick(app.packageName,app.className);
					}
				});
			}
			if(APPS_OPTION_ID_SHARE == id){
				tv.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						shareOnClick(app.packageName);
					}
				});
			}
			if(APPS_OPTION_ID_UNINSTALL == id){
				tv.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						uninstallLocalApp(app);
					}
				});
			}
			if(APPS_OPTION_ID_UPDATE_IGNORE == id){
				tv.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						ignoreAppUpdate(v,mContext,app);
					}
				});
			}
		}
		
		private void setOptionView(View view,final LocalApplicationInfo app){
			if(app == null)
				return ;
			boolean isCanRun = true;
			boolean isCanUninstall = true; 
			if(app.className == null || app.className.isEmpty()){
				isCanRun = false;
			}
			if(app.isRoot){
				isCanUninstall = false;
			}
			
			TextView fun1 = (TextView)view.findViewById(R.id.fun1);
			TextView fun2 = (TextView)view.findViewById(R.id.fun2);
			TextView fun3 = (TextView)view.findViewById(R.id.fun3);
			TextView fun4 = (TextView)view.findViewById(R.id.fun4);
			TextView fun5 = (TextView)view.findViewById(R.id.fun5);
			
			ArrayList<TextView> listTextView = new ArrayList<TextView>();
			listTextView.add(fun1);
			listTextView.add(fun2);
			listTextView.add(fun3);
			listTextView.add(fun4);
			listTextView.add(fun5);
			
			ArrayList<Integer> listOption = new ArrayList<Integer>();
			listOption.add(APPS_OPTION_ID_MANAGER);
			if(isCanRun){
				listOption.add(APPS_OPTION_ID_RUN);
			}
			listOption.add(APPS_OPTION_ID_SHARE);
			if(isCanUninstall){
				listOption.add(APPS_OPTION_ID_UNINSTALL);
			}
			if(app.upgradeApp != null){
				listOption.add(APPS_OPTION_ID_UPDATE_IGNORE);
			}
			int w = UpgradeAppDownloadControl.dip2px(mContext,25);
			int h = UpgradeAppDownloadControl.dip2px(mContext,25);
			int textViewWidth = 0;
			if(listOption.size() != 0){
				double temp = (double)320/listOption.size();
				textViewWidth = UpgradeAppDownloadControl.dip2px(mContext,(int)temp);
			}
			Log.d(TAG,"setOptionView  textViewWidth:" + textViewWidth);
			int textid = 0;
			for(int i=0 ; i<listOption.size() ; i++){
				int optionId = listOption.get(i);
				for(int j=0 ; j<optionFun.length; j++){
					if( (optionId == optionFun[j][0]) && (textid < listTextView.size())){
						TextView tv= listTextView.get(textid);
						tv.setVisibility(View.VISIBLE);
						Drawable drawable;
						if(APPS_OPTION_ID_UPDATE_IGNORE == optionId 
								&& app.upgradeApp != null && app.upgradeApp.getUpdateIgnore()){
							tv.setText(R.string.local_app_update_remind);
							drawable = mContext.getResources().getDrawable(R.drawable.ic_add_selector);  
						}else{
							tv.setText(optionFun[j][1]);
							drawable = mContext.getResources().getDrawable(optionFun[j][2]);
						}
						tv.getLayoutParams().width = textViewWidth;
						drawable.setBounds(0, 0, w, h); 
						tv.setCompoundDrawables(null, drawable, null, null);
						textid ++;
						
						setOnClickListenerById(tv,optionId,app);
					}
				}
			}
			for(int k=textid; k<listTextView.size() ; k++){
				listTextView.get(k).setVisibility(View.GONE);
			}
			
			LinearLayout line1 = (LinearLayout)view.findViewById(R.id.local_app_item_option_1);
			LinearLayout line2 = (LinearLayout)view.findViewById(R.id.local_app_item_option_2);
			
			if(listOption.size() == 0){
				line1.setVisibility(View.GONE);
			}else{
				line1.setVisibility(View.VISIBLE);
			}
			if(listOption.size() < 6){
				line2.setVisibility(View.GONE);
			}else{
				line2.setVisibility(View.VISIBLE);
			}
			
		}
		
		boolean hasOptionView(String packageName){
			if(mExpandItemPN != null && mExpandItemPN.equals(packageName))
				return true;
			return false;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			final LocalViewHolder holder;

			if (convertView == null || convertView.getTag() == null) {
				holder = new LocalViewHolder();

				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.local_app_item, null);
				holder.appIcon = (ImageView) convertView
						.findViewById(R.id.local_icon);
//				holder.appIconProgress = (ProgressBar) convertView
//						.findViewById(R.id.local_icon);
				holder.appName = (TextView) convertView
						.findViewById(R.id.local_name);
				holder.appSize = (TextView) convertView
						.findViewById(R.id.local_size);
				holder.appVersion = (TextView) convertView
						.findViewById(R.id.local_version);
				holder.localAppsItem = (LinearLayout) convertView
						.findViewById(R.id.localContainer);
				holder.appUpgrade = (TextView) convertView
						.findViewById(R.id.local_upgrade);
				holder.appDownloadProsses = (ProgressBar)convertView
						.findViewById(R.id.download_prosses);
				holder.appDownloadProssesView = (TextView)convertView
						.findViewById(R.id.download_prosses_view);
				holder.foldIcon = (ImageView)convertView.findViewById(R.id.option_arrow);
				holder.localAppsItemOption = (LinearLayout)convertView
						.findViewById(R.id.local_app_item_option);
				holder.downloadProssesLayout = (LinearLayout)convertView
						.findViewById(R.id.progress_view_layout);
				holder.appDownloadProssesStatus = (TextView)convertView
						.findViewById(R.id.download_prosses_status);
				
			} else {
				holder = (LocalViewHolder) convertView.getTag();
			}
			final LocalApplicationInfo currentApp = getChild(groupPosition,childPosition);

			holder.position = childPosition;
			holder.group = groupPosition;
			holder.packageName = currentApp.packageName;
			if (currentApp.upgradeApp != null)
				holder.versionCode = currentApp.upgradeApp.getVersionCode();
			convertView.setTag(holder);

			holder.appIcon.setImageDrawable(currentApp.icon);
//			holder.appIconProgress.setBackgroundDrawable(currentApp.icon);
			holder.appName.setText(currentApp.name);

			if(currentApp.upgradeApp != null){
				if(isShowPrograss(currentApp.upgradeApp)){
					showDownloadPrograss(convertView,currentApp);
				}else{
					showAppDetail(convertView,currentApp);
				}
			}else{
				showAppDetail(convertView,currentApp);
			}
			
			final View item = convertView;

			if (UPDATE_STATE.NEWEST == currentApp.updateState) {
				holder.appUpgrade.setVisibility(View.GONE);
			} else {
				holder.appUpgrade.setVisibility(View.VISIBLE);
				holder.appUpgrade.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if(currentApp.upgradeApp.getUpdateIgnore())
							return;
						UpgradeAppDownloadControl.prepareDownload(
								currentApp.upgradeApp, mContext);
						UpgradeAppDownloadControl.drawDownloadState(
								currentApp.upgradeApp, item ,mContext);
					}
				});
				UpgradeAppDownloadControl.drawDownloadState(
						currentApp.upgradeApp, item ,mContext);
			}
			
			setOptionView(holder.localAppsItemOption,currentApp);

			if(hasOptionView(holder.packageName)){
				holder.localAppsItemOption.setVisibility(View.VISIBLE);
				holder.foldIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_arrow_up));
			}else{
				holder.localAppsItemOption.setVisibility(View.GONE);
				holder.foldIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_arrow));
			}
			
			holder.localAppsItem.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if(hasOptionView(holder.packageName)){
						mExpandItemPN = null;
						holder.localAppsItemOption.setVisibility(View.GONE);
						holder.foldIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_arrow));
						
					}else{
						holder.localAppsItemOption.setVisibility(View.VISIBLE);
						holder.foldIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_arrow_up));
						
						View view = mLocalListView.findViewWithTag(new LocalViewHolder(mExpandItemPN));
						if(view != null){
							LocalViewHolder tag = (LocalViewHolder)view.getTag();
							tag.localAppsItemOption.setVisibility(View.GONE);
							tag.foldIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_arrow));
						}
						mExpandItemPN = holder.packageName;
						
						if(mLocalListView.getLastVisiblePosition() == mLocalListView.getPositionForView(v)){
							mLocalListView.postDelayed(new Runnable(){
								@Override
								public void run() {
									mLocalListView.smoothScrollToPosition(mLocalListView.getLastVisiblePosition());
								}
							},100);
						}
					}
					Log.d(TAG,"--------------getLastVisiblePosition:" + mLocalListView.getLastVisiblePosition() 
							+ ",getPositionForView:" + mLocalListView.getPositionForView(v));
					
				}
			});

			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			// TODO Auto-generated method stub
			int id = (int) getGroupId(groupPosition);
			int count = 0;
			if(id == UPGRADE_APPS_GROUP_ID){
				count = mUpgradeList.size();
			}else if(id == LOCAL_APPS_GROUP_ID){
				count = mLocalList.size() - mUpgradeList.size();
			}
			
			return count;
		}

		@Override
		public Object getGroup(int groupPosition) {
			return mGroups[(int) getGroupId(groupPosition)];
		}

		@Override
		public int getGroupCount() {
			if(0 == mUpgradeList.size() ){
				return mGroups.length - 1;
			}
			return mGroups.length;
		}

		@Override
		public long getGroupId(int groupPosition) {
			if(0 == mUpgradeList.size() ){
				return groupPosition + 1;
			}
			
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                ViewGroup parent) {
			View contentView = inflater.inflate(R.layout.local_app_item_expandable_group, parent,false);
//			TextView textView = (TextView)contentView.findViewById(R.id.local_app_item_expandable_group);
//			textView.setText(getGroup(groupPosition).toString());
			String grouptips;
			
			TextView tips = (TextView) contentView.findViewById(R.id.appsCount);
			mUpdateAllView = (TextView)contentView.findViewById(R.id.upgradeAll);
			
			int groupid = (int) getGroupId(groupPosition);
			if(UPGRADE_APPS_GROUP_ID == groupid){
//				String string = mContext.getResources().getString(R.string.magicdownload_upgrade_upgradecount, mUpgradeList.size());    
				grouptips = mContext.getResources().getString((Integer)getGroup(groupPosition),mUpgradeList.size());
				showCountView(tips,grouptips,String.valueOf(mUpgradeList.size()));
				
				mUpdateAllView.setVisibility(View.VISIBLE);
		        if(0 == mUpgradeList.size())
		        	mUpdateAllView.setVisibility(View.GONE);
		        else
		        	mUpdateAllView.setVisibility(View.VISIBLE);
				
				mUpdateAllView.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {				
						Log.d(TAG,"-----------onClick----------------------upgrade all");
						showConfirmUpdateAllDialog(mContext);
					}
				});
			}else{
				mUpdateAllView.setVisibility(View.GONE);
				grouptips = mContext.getResources().getString((Integer)getGroup(groupPosition));
				tips.setText(grouptips);
			}
			mLocalListView.setGroupIndicator(null);
			mLocalListView.expandGroup(groupPosition);
			return contentView;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return false;
		}
		
		private void manageOnClick(String packageName){
			Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			Uri uri = Uri.fromParts("package", packageName, null);
			intent.setData(uri);
			mContext.startActivity(intent);
		}
		
		private void runOnClick(String packageName,String className){
			try{
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);
	
				ComponentName cn = new ComponentName(packageName, className);
	
				intent.setComponent(cn);
				startActivity(intent);
			}catch(Exception ex){
				Log.d(TAG,"runOnClick error --- packageName："+packageName + ",className:" + className);
			}
		}
		
		private void shareOnClick(String packageName){
			if(packageName == null || packageName.isEmpty())
				return;

			if(!com.lenovo.launcher2.addon.share.LeShareUtils.isInstalledQiezi(mContext)){
				com.lenovo.launcher2.addon.share.LeShareUtils.showInstallDialog(mContext,false);
        		return;
        	}else if(!com.lenovo.launcher2.addon.share.LeShareUtils.isInstalledRightQiezi(mContext)){
        		com.lenovo.launcher2.addon.share.LeShareUtils.showInstallDialog(mContext,true);
        		return;
        	}

//			ArrayList<Parcelable> list = new ArrayList<Parcelable>();
			
			final Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType(SHARE_MIME_TYPE);
            
            PackageManager pm = mContext.getPackageManager();
            List<ResolveInfo> infoList = pm.queryIntentActivities(shareIntent, 0);
    		for (ResolveInfo info : infoList) {
    			String pn = info.activityInfo.packageName;
    			if (pn.equalsIgnoreCase("com.lenovo.anyshare")) {
    				String activity = info.activityInfo.name;
    				shareIntent.setClassName("com.lenovo.anyshare", activity);
    				Log.d(TAG,"activity:"+activity);
    				break;
    			}
    		}
    		
            try {
                String sourceDir = pm.getApplicationInfo(packageName, 0).sourceDir;
                File file = new File(sourceDir);
                Uri uri = Uri.fromFile(file);
//                list.add(uri);
//                shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(shareIntent);
            } catch (Exception e) {
            	Log.d(TAG,"shareOnClick Exception");
                // TODO Auto-generated catch block
//                	sourceDir = "";
             }
		}

	}
	
	private void notifyDelete(int category,String packageName, String versionCode,String appName ,String action) {
		Intent intent = new Intent(action);
		intent.putExtra(DownloadConstant.EXTRA_PACKAGENAME, packageName);
		intent.putExtra(DownloadConstant.EXTRA_VERSION, versionCode);
		intent.putExtra(DownloadConstant.EXTRA_APPNAME, appName);
		intent.putExtra(DownloadConstant.EXTRA_CATEGORY, category);
		mContext.sendBroadcast(intent);
		Log.d(TAG, "------------- >>> notifyDelete >>> action : " + action);
	}

	private void uninstallLocalApp(LocalApplicationInfo app) {
		// Log.i(TAG,"UpgradeAppListActivity.uninstallLocalApp");
		Intent intent = new Intent(Intent.ACTION_DELETE);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		intent.setData(Uri.parse("package:" + app.packageName));
		startActivity(intent);
	}
	
	private void showInstallLeSyncDialog(final Context context) {

		final String downloadUrl = "http://pim.lenovo.com/contact/portal/pim/down.shtml";
		LeAlertDialog dialog = new LeAlertDialog(context, R.style.Theme_LeLauncher_Dialog_Shortcut);
		
	    dialog.setLeTitle(R.string.letongbu_install_dialog_title);
	    dialog.setLeMessage(context.getText(R.string.letongbu_install_dialog_body));
	    dialog.setLeNegativeButton(context.getText(R.string.cancel_action), new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	                dialog.dismiss();
	        }
	    });

	    dialog.setLePositiveButton(context.getText(R.string.rename_action), new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
				  Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl));
			      context.startActivity(it);
			      dialog.cancel();
	        }
	    });
	    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
	        @Override
	        public void onCancel(DialogInterface dialog) {
	                dialog.dismiss();
	        }
	    });
	    dialog.show();
	}
	
	private boolean checkAppIsExistAndEnable(String packagename) {

		PackageInfo packageInfo;
		try {
			packageInfo = getActivity().getPackageManager().getPackageInfo(
					packagename, 0);

		} catch (NameNotFoundException e) {
			packageInfo = null;
			e.printStackTrace();
		}
		if (packageInfo == null) {
			showInstallLeSyncDialog(getActivity());
			return false;
		}else if(!packageInfo.applicationInfo.enabled){
			showAppEnableDialog(getActivity(),packagename);
			return false;
		}

		return true;

	}
	
	private void showConfirmUpdateAllDialog(final Context context) {
	    LeAlertDialog dialog = new LeAlertDialog(context, R.style.Theme_LeLauncher_Dialog_Shortcut);
	    dialog.setLeTitle(R.string.letongbu_install_dialog_title);
	    dialog.setLeMessage(context.getText(R.string.local_app_confirm_update_all));
	    dialog.setLeNegativeButton(context.getText(R.string.cancel_action), new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	                dialog.dismiss();
	        }
	    });

	    dialog.setLePositiveButton(context.getText(R.string.rename_action), new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	handleGetMessage(MSG_UPGRADE_ALL);
				dialog.dismiss();
	        }
	    });
	    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
	        @Override
	        public void onCancel(DialogInterface dialog) {
	                dialog.dismiss();
	        }
	    });
	    dialog.show();
	}
	
	private void showAppEnableDialog(final Context context, String packageName) {
		final String pkName = packageName;
	    LeAlertDialog dialog = new LeAlertDialog(context, R.style.Theme_LeLauncher_Dialog_Shortcut);
	    dialog.setLeTitle(R.string.letongbu_install_dialog_title);
	    dialog.setLeMessage(context.getText(R.string.local_app_check_LeSync));
	    dialog.setLeNegativeButton(context.getText(R.string.cancel_action), new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	                dialog.dismiss();
	        }
	    });

	    dialog.setLePositiveButton(context.getText(R.string.rename_action), new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
				Uri uri = Uri.fromParts("package", pkName, null);
				intent.setData(uri);
				mContext.startActivity(intent);
				
				dialog.dismiss();
	        }
	    });
	    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
	        @Override
	        public void onCancel(DialogInterface dialog) {
	                dialog.dismiss();
	        }
	    });
	    dialog.show();
	}
	

	@Override
	public void doCallback(final DownloadInfo info) {
		// TODO Auto-generated method stub
		
		int s = info.getDownloadStatus();
		Status status =  convertStatus(s);
//		Log.i(TAG,"-----------------------------------doCallback------,packagename:" + info.getPackageName() 
//				+ ",version:" + info.getVersionCode() + ",status:" + s + ",prograss:" + info.getProgress());

		final UpgradeApp preUpgradeApp = getUpgradeApp(info.getPackageName(),info.getVersionCode());
		if(preUpgradeApp != null){
			if(status == Status.DOWNLOADING || status == Status.DOWNLOAD_CLICK 
					|| status == Status.UNINSTALL || status == Status.PAUSE){
				preUpgradeApp.setCurrentBytes(info.getCurrentBytes());
				preUpgradeApp.setTotalBytes(info.getTotalBytes());
				preUpgradeApp.setControl(info.getControl());
				preUpgradeApp.setStatus(status);
				replaceUpgradeApp(preUpgradeApp);
								
//				Log.d(TAG,"---------------------pkname:"+info.getPackageName()+
//						",currentBytes:"+info.getCurrentBytes());
				((Activity)mContext).runOnUiThread(
						new Runnable(){
							@Override
							public void run() {
								View view = mLocalListView.findViewWithTag(new LocalViewHolder(preUpgradeApp.getPackageName(),
										preUpgradeApp.getVersionCode()));
								
								if(view != null){
									LocalApplicationInfo local = getLocalApp(info.getPackageName());
									if(local != null)
										refreshUpgradeItem(local,view);
								}
						    }
				});
				
			}
		}
	}

}
