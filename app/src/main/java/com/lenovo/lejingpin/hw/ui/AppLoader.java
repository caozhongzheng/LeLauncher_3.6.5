package com.lenovo.lejingpin.hw.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.AsyncTaskLoader;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.lenovo.lejingpin.hw.content.data.HwConstant;
import com.lenovo.lejingpin.hw.content.data.ReCommendsApp;
import com.lenovo.lejingpin.hw.content.data.RecommendsAppList;
import com.lenovo.lejingpin.hw.content.db.HWDBUtil;
import com.lenovo.lejingpin.hw.ui.RecommendLocalAppInfo.Status;
import com.lenovo.lejingpin.share.download.DownloadInfo;
import com.lenovo.lejingpin.share.download.LDownloadManager;

public class AppLoader extends AsyncTaskLoader<List<RecommendLocalAppInfo>> {
	private static final String TAG = "AppLoader";

	private List<RecommendLocalAppInfo> mApps;

	private BroadcastReceiver mAppListObserver;

	private boolean mWaitForBroadcastResult;

	private boolean result;
	
	private boolean isSender;
	

	
	public boolean getResult() {
		return result;
	}

	public AppLoader(Context context) {
		super(context);
		Log.d(TAG, "new AppLoader....");
	}

	@Override
	protected void onStartLoading() {
		super.onStartLoading();
		if(mApps != null) {
			if(mApps.isEmpty()) {
				result = true;
			}
			deliverResult(mApps);
		} else {
			mWaitForBroadcastResult = true;
			forceLoad();
		}
	}

	@Override
	protected List<RecommendLocalAppInfo> onLoadInBackground() {
		Log.d(TAG, "onLoadInBackground...");
		return super.onLoadInBackground();
	}

	@Override
	protected void onForceLoad() {
		Log.d(TAG, "onForceLoad...");
		super.onForceLoad();
	}

	@Override
	protected void onStopLoading() {
		Log.d(TAG, "onStopLoading...");
		cancelLoad();
	}

	@Override
	public void onCanceled(List<RecommendLocalAppInfo> data) {
		super.onCanceled(data);
		onReleaseResources(data);
	}
	
	private HashMap<String,Integer> getInstalledAppMap(){
		HashMap<String,Integer> map = new HashMap<String,Integer>();
		List<PackageInfo> pkgs = getContext().getPackageManager().getInstalledPackages(0);
		if(pkgs!=null){
			for(PackageInfo pkg : pkgs){
				map.put(pkg.packageName.trim().toLowerCase(), pkg.versionCode);
			}
		}
		return map;
	}
	
	private HashMap<String,DownloadInfo> getDownlaodMap(){
		HashMap<String,DownloadInfo> map = new HashMap<String,DownloadInfo>();
		List<DownloadInfo> downloadInfos = LDownloadManager.getDefaultInstance(getContext()).getAllDownloadInfo();
		
		if(downloadInfos!=null && !downloadInfos.isEmpty()){
			for(DownloadInfo info : downloadInfos){
				map.put(info.getPackageName()+info.getVersionCode(), info);
			}
		}
		
		return map;
	}
	
	@Override
	public List<RecommendLocalAppInfo> loadInBackground(){
		Log.d(TAG, "loadInBackground >>> mWaitForBroadcastResult=" + mWaitForBroadcastResult);
		RecommendsAppList appList = HWDBUtil.queryUnFavoritesRecommendsAppListByCursor(getContext());
		HashMap<String,DownloadInfo> downloadMap = getDownlaodMap();
		HashMap<String,Integer> installedApp = getInstalledAppMap();
		ArrayList<RecommendLocalAppInfo> resultList = null;
		if(appList != null) {
			resultList = new ArrayList<RecommendLocalAppInfo>();
			for (ReCommendsApp app : appList.getAppStoreList()){
				int progress = -1;
				DownloadInfo info = null;
				if(downloadMap != null && !downloadMap.isEmpty()){
					info = downloadMap.get(app.getPackageName()+app.getVersionCode());
					if(info!=null){
						progress = info.getProgress();
					}
				}
				if(isPackageInstalled(installedApp,app.getPackageName(), app.getVersionCode())){
					continue;
				}

				RecommendLocalAppInfo localAppInfo = new RecommendLocalAppInfo(app);
				if(progress != -1) {
					if(progress==100){
						localAppInfo.status = Status.UNINSTALL;
					}else{
						if(info!=null){
							if(info.getDownloadStatus()==192){
								localAppInfo.status = Status.DOWNLOADING;
							}else if(info.getDownloadStatus()==193){
								localAppInfo.status = Status.PAUSE;
							}else{
								localAppInfo.status = Status.UNDOWNLOAD;
							}
						}
					}
					localAppInfo.downLoadProgress = progress;
				}else{
					localAppInfo.status = Status.UNDOWNLOAD;
				}
				resultList.add(localAppInfo);
			}

			if(!resultList.isEmpty()) {
				mWaitForBroadcastResult = false;
			}
		}
		boolean isOpen = Util.getInstance().isNetworkEnabled(getContext());
		if(!isSender && isOpen){
			sendBroadcastForAppList();
		}
		return resultList;
	}

	private boolean isPackageInstalled(HashMap<String,Integer> map,String pkgName, String vcode) {
		boolean installed = false;
		if(map!=null && !map.isEmpty()){
			Integer versioncode = map.get(pkgName.trim().toLowerCase());
			if(versioncode!=null && versioncode > Integer.parseInt(vcode)){
				installed = true;
			}
		}
		return installed;
	}

	@Override
	public void onContentChanged() {
		Log.d(TAG, "onContentChanged >>> isStarted=" + isStarted());
		super.onContentChanged();
	}

	@Override
	public void deliverResult(List<RecommendLocalAppInfo> apps) {
		if(isStarted() && !mWaitForBroadcastResult) {
			Log.d(TAG, "deliverResult....size="+(apps==null?0:apps.size()));
			mApps = apps;
			super.deliverResult(apps);
		}
	}

	@Override
	protected void onReset() {
		super.onReset();

		Log.d(TAG, "onReset...");
		onStopLoading();

		if(mApps != null) {
			onReleaseResources(mApps);
			mApps = null;
		}

		unregisterAppListObserver();
	}

	private void unregisterAppListObserver() {
		if(mAppListObserver != null) {
			getContext().unregisterReceiver(mAppListObserver);
			mAppListObserver = null;
			Log.d(TAG, "unregisterAppListObserver......");
		}
	}

	protected void onReleaseResources(List<RecommendLocalAppInfo> apps) {
		Log.d(TAG, "onReleaseResources......");
		// if(apps != null) apps.clear();
	}

	private void sendBroadcastForAppList() {
		isSender = true;
		if("other".equals(Util.getConnectType(getContext()))) {
			this.result = false;
			mWaitForBroadcastResult = false;
			return;
		}

		if(mAppListObserver == null) {
			mAppListObserver = new AppListReceiver(this);
		}

		Intent intent = new Intent();
		intent.setAction(HwConstant.ACTION_TIMESHEDULE_SPEREAPPLIST);
		getContext().sendBroadcast(intent);
		Log.d(TAG, "sendBroadcastForAppList......");
	}

	private class AppListReceiver extends BroadcastReceiver {

		private final AppLoader mLoader;

		public AppListReceiver(AppLoader appLoader) {
			this.mLoader = appLoader;
			IntentFilter filter = new IntentFilter(HwConstant.ACTION_SPERE_APP_LIST_COMPLETE);
			appLoader.getContext().registerReceiver(this, filter);
			Log.d(TAG, "register AppListReceiver......");
		}

		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "onReceive >> action : "+intent.getAction());
			result = intent.getBooleanExtra("result", false);
			mWaitForBroadcastResult = false;
			if(HwConstant.ACTION_SPERE_APP_LIST_COMPLETE.equals(intent.getAction())){
				mLoader.onContentChanged();
			}
		}
	}

}