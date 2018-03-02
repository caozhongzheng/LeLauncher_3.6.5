package com.lenovo.lejingpin.share.download;

import java.io.File;
import java.util.Map;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.backup.BackupManager;
import com.lenovo.launcher2.backup.BackupManager.State;
import com.lenovo.launcher2.customizer.EnableState;
import com.lenovo.launcher2.customizer.ConstantAdapter.OperationState;
import com.lenovo.launcher2.customizer.GlobalDefine;
import com.lenovo.lejingpin.hw.lcapackageinstaller.utils.LcaInstallerUtils;
import com.lenovo.lejingpin.settings.LejingpingSettingsValues;
import com.lenovo.lejingpin.share.util.Utilities;

/**
 * 
 * @author philn
 * 
 */
public class IService extends Service implements Runnable {

	private static final String TAG = "IService";

	private volatile Looper mServiceLooper;

	private volatile ServiceHandler mServiceHandler;

	private int mServiceStartId = -1;

	private static final int REQ_DEL = 0;
	private static final int REQ_PAUSE = 1;
	private static final int REQ_CONTINUE = 2;
	private static final int REQ_START = 3;
	private static final int REQ_REDOWNLOAD = 4;

	private static final String ACTION_TYPE = "type";

	@Override
	public IBinder onBind(Intent intent) {
		startService(new Intent(this, DownloadService.class));
		return mBinder;
	}

	private final IDownloadService.Stub mBinder = new IDownloadService.Stub() {
		
		public void requestDownloadTask(DownloadInfo appInfo, int actionType) {
			Bundle args = new Bundle();
			if (null != appInfo) {
				args.putParcelable("download", appInfo);
			}
			switch (actionType) {
			case REQ_PAUSE:
				args.putInt(ACTION_TYPE, REQ_PAUSE);
				break;
			case REQ_CONTINUE:
				args.putInt(ACTION_TYPE, REQ_CONTINUE);
				break;
			case REQ_DEL:
				args.putInt(ACTION_TYPE, REQ_DEL);
				break;
			case REQ_START:
				args.putInt(ACTION_TYPE, REQ_START);
				break;
			case REQ_REDOWNLOAD:
				args.putInt(ACTION_TYPE, REQ_REDOWNLOAD);
				break;
			default:
				break;
			}
			args.putInt("what", DOWNLOAD_ACTION);
			startService(new Intent(IService.this, IService.class)
					.putExtras(args));
		}

		@SuppressWarnings("static-access")
		public int getAllDownloadCount(){
			return DownloadHelpers.getAllDownloadsCount(IService.this);
		}
		
		@SuppressWarnings("static-access")
		public String addTask(DownloadInfo appInfo) throws RemoteException {
			Log.d(TAG,"IDownloadService addTask");
			if (DownloadHelpers.isValidItem(appInfo)) {
				return preDownload(appInfo);
			}
			 Log.d(TAG, "addTask error");
			return null;
		}

		@SuppressWarnings("static-access")
		public boolean startTask(DownloadInfo appInfo) throws RemoteException {

			if (DownloadHelpers.isValidItem(appInfo)) {
				Log.d(TAG, "startTask:" + appInfo.getPackageName() + ":"
						+ appInfo.getVersionCode());
				requestDownloadTask(appInfo, REQ_START);
				return true;
			}
			Log.d(TAG, "startTask error");
			return false;
		}

		@SuppressWarnings("static-access")
		public boolean pauseTask(DownloadInfo appInfo) throws RemoteException {

			if (DownloadHelpers.isValidItem(appInfo)) {
				Log.d(TAG, "pauseTask:" + appInfo.getPackageName() + ":"
						+ appInfo.getVersionCode());
				requestDownloadTask(appInfo, REQ_PAUSE);
				return true;
			}
			Log.d(TAG, "pauseTask error");
			return false;
		}

		@SuppressWarnings("static-access")
		public boolean resumeTask(DownloadInfo appInfo) throws RemoteException {

			if (DownloadHelpers.isValidItem(appInfo)) {
				Log.d(TAG, "resumeTask:" + appInfo.getPackageName() + ":"
						+ appInfo.getVersionCode());
				requestDownloadTask(appInfo, REQ_CONTINUE);
				return true;
			}
			Log.d(TAG, "resumeTask error");
			return false;
		}

		@SuppressWarnings("static-access")
		public boolean deleteTask(DownloadInfo appInfo) throws RemoteException {

			if (DownloadHelpers.isValidItem(appInfo)) {
				Log.d(TAG, "deleteTask:" + appInfo.getPackageName() + ":"
						+ appInfo.getVersionCode());
				requestDownloadTask(appInfo, REQ_DEL);
				return true;
			}
			Log.d(TAG, "deleteTask error");
			return false;
		}
		
		@SuppressWarnings("static-access")
		public boolean reDownloadTask(DownloadInfo appInfo) throws RemoteException {

			if (DownloadHelpers.isValidItem(appInfo)) {
				Log.d(TAG, "reDownloadTask:" + appInfo.getPackageName() + ":"
						+ appInfo.getVersionCode());
				requestDownloadTask(appInfo, REQ_REDOWNLOAD);
				return true;
			}
			Log.d(TAG, "deleteTask error");
			return false;
		}

		public DownloadInfo getDownloadInfo(DownloadInfo info)
				throws RemoteException {
			return DownloadHelpers.getDownloadInfo(IService.this, info);
		}

	};

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "IService.onCreate");
//		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		// Start up the thread running the service. Note that we create a
		// separate thread because the service normally runs in the process's
		// main thread, which we don't want to block.
		Thread thr = new Thread(null, this, "LDownloadService");
		thr.start();
	}

	public void run() {
		Looper.prepare();
		mServiceLooper = Looper.myLooper();
		mServiceHandler = new ServiceHandler();
		Looper.loop();
	}

	/**
	 * Download Task Actions
	 */
	public static final int DOWNLOAD_ACTION = 1;
	/**
	 * Service out of operation
	 */
	public static final int DOWNLOAD_EXIT = 2;

	private final class ServiceHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {

			Bundle data = null;
			if (null != (Bundle) msg.obj) {
				data = (Bundle) msg.obj;
			}
			switch (msg.what) {
				case DOWNLOAD_ACTION:
					if (null != data.getParcelable("download")) {
						int action = data.getInt(ACTION_TYPE);
						action(action,
								(DownloadInfo) data.getParcelable("download"));
					}
					break;
				case DOWNLOAD_EXIT:
					Log.d(TAG, "receiver exit service");
					stopSelf(mServiceStartId);
					break;
				default:
					break;
			}
		}
	};

	private void action(int action, DownloadInfo di) {
		if (null == di) {
			return;
		}
		Log.i(TAG, "IService.action:" + action);
		int actionStatus = 0;
		switch (action) {
			case REQ_DEL:
				actionStatus = DownloadInfo.DELETE;
				break;
			case REQ_PAUSE:
				actionStatus = DownloadInfo.PAUSE;
				break;
			case REQ_CONTINUE:
				actionStatus = DownloadInfo.CONTINUE;
				break;
			case REQ_START:
				actionStatus = DownloadInfo.START;
				break;
			case REQ_REDOWNLOAD:
				actionStatus = DownloadInfo.REDOWNLOAD;
				break;
			default:
				break;
		}
		DownloadHelpers.doAction(IService.this, di, actionStatus);
//		mServiceHandler.sendEmptyMessageDelayed(DOWNLOAD_EXIT, 4000);
		mServiceHandler.sendEmptyMessage(DOWNLOAD_EXIT);
	}

	public synchronized String preDownload(DownloadInfo appInfo) {
		// Log.i(TAG,"IService.preDownload, category:"+
		// appInfo.getCategory());
		if (null == appInfo.getDownloadUrl()
				|| "".equals(appInfo.getDownloadUrl().trim())) {
			Log.i(TAG, "Invalid download address");
			return null;
		}
		if (DownloadHelpers.itemExistence(this, appInfo)) {
			Log.i(TAG, "download item has in the list");
			return null;
		}
		return DownloadHelpers.preDownload(IService.this, appInfo);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "onStartCommand startId " + startId);
		mServiceStartId = startId;
		while (mServiceHandler == null) {
			synchronized (this) {
				try {
					wait(100);
				} catch (InterruptedException e) {
				}
			}
		}
		if (intent == null) {
			Log.e(TAG, "Intent is null in onStartCommand");
			return Service.START_NOT_STICKY;
		}
		Bundle extras = intent.getExtras();
		if (null != extras) {
			Message msg = mServiceHandler.obtainMessage();
			msg.what = extras.getInt("what");
			msg.obj = extras;
			mServiceHandler.sendMessage(msg);
		}
		// Try again later if we are killed before we can finish scanning.
		return Service.START_NOT_STICKY;
	}

//	DownloadReceiver mReceiver;
//	boolean registed;

	// private void registReceiver(){
	// Log.i(TAG,"regist receiver");
	// mReceiver = new DownloadReceiver();
	// IntentFilter intentFilter = new IntentFilter();
	// intentFilter.addAction(LDownloadManager.DOWNLOAD_START_ACTION);
	// intentFilter.addAction(LDownloadManager.ACTION_DOWNLOAD_LIST);
	// intentFilter.addAction(LDownloadManager.ACTION_DOWNLOAD_CONFIG);
	// intentFilter.addAction(LDownloadManager.ACTION_DOWNLOAD_COMPLETED);
	// intentFilter.addAction(LDownloadManager.ACTION_NOTIFICATION_CLICKED);
	// intentFilter.addAction(LDownloadManager.ACTION_NOTIFICATION_INSTALLED);
	// intentFilter.addAction(LDownloadManager.ACTION_PUSH_APP);
	// registerReceiver(mReceiver, intentFilter);
	// registed = true;
	// }

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy");
		// Make sure thread has started before telling it to quit.
		while (mServiceLooper == null) {
			synchronized (this) {
				try {
					wait(100);
				} catch (InterruptedException e) {
				}
			}
		}
		mServiceLooper.quit();
//		Utilities.killLejingpinProcess();
		super.onDestroy();
	}
	
	private static boolean isNeedInstall(int category){
		int ca = DownloadConstant.getDownloadCategory(category);
		if(DownloadConstant.CATEGORY_APP_UPGRADE == ca ||
				DownloadConstant.CATEGORY_APPMANAGER_UPGRADE == ca ||
				DownloadConstant.CATEGORY_COMMON_APP == ca ||
				DownloadConstant.CATEGORY_RECOMMEND_APP == ca || 
				DownloadConstant.CATEGORY_LOCKSCREEN == ca ||
				DownloadConstant.CATEGORY_THEME == ca || 
				DownloadConstant.CATEGORY_SEARCH_APP == ca){
			Log.i(TAG, "NeedInstall");
			return true;
		}
		return false;
		
	}

	public static class DownloadReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.d(TAG, "IService.DownloadReceiver action:" + action);
			if (Downloads.ACTION_DOWNLOAD_CONFIG.equals(action)) {
				int i = intent.getFlags();
				if (Downloads.FLAG_WIFI == i) {
					boolean flag = intent.getBooleanExtra("wifi", false);
					// user config it
					DownloadHelpers.mWifiStatus = flag;
					ConnectivityManager connectivityManager = (ConnectivityManager) context
							.getSystemService(Context.CONNECTIVITY_SERVICE);
					NetworkInfo activeNetInfo = connectivityManager
							.getActiveNetworkInfo();
					if (activeNetInfo != null
							&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
						DownloadHelpers.mNetworkType = LDownloadManager.STATUS_VALUES[1];
					} else {
						DownloadHelpers.mNetworkType = LDownloadManager.STATUS_VALUES[0];
					}
					DownloadHelpers.updataForWifi(context, flag);
				} else if (Downloads.FLAG_DOWNLOADS == i) {
					int max = intent.getIntExtra("downloadmax", 3);
					Log.i(TAG, "max:" + max);
					DownloadHelpers.action = max;
				}
			} else if (LDownloadManager.ACTION_DOWNLOAD_COMPLETED
					.equals(action)) {
				if (null != intent.getExtras()) {
					DownloadInfo di = intent.getExtras().getParcelable(
							LDownloadManager.RECEIVER_DATA_TAG);
					if (null != di && null != di.getId()) {
						 Log.d(TAG,"IService.DownloadReceiver,Download the complete notification is received.\n"+di);
						DownloadHelpers.notifyCompleted(context, di);
						
						if (di.getDownloadStatus() != Downloads.Impl.STATUS_SUCCESS)
							return;
						if (di.getCategory() == DownloadConstant.CATEGORY_ERROR) {
							return;
						}
						int categoryInt = DownloadConstant.CATEGORY_ERROR;
						try {
							categoryInt = di.getCategory();
						} catch (NumberFormatException e) {
							Log.e(TAG,
									"LcaInstallerUtils.installApplication() category error!");
							return;
						}
						Log.i(TAG, "----------------------categoryInt="+categoryInt);
						if (isNeedInstall(categoryInt)) {
//							Log.i(TAG, "step this----------------------");
							 LcaInstallerUtils.installApplication(context,
							 di.getInstallPath(),
							 di.getCategory(), di.getPackageName(),di.getVersionCode());
							 
						}else if(DownloadConstant.CATEGORY_LBK == categoryInt ){ 
							final String defaultLBK = Environment.getExternalStorageDirectory().getPath() + "/default.lbk";
							String file = di.getInstallPath();
							Log.d(TAG,"LBK file:" + file);
							File lbkfile = new File(file);
							boolean isSuccess = false;
							if(lbkfile.exists()){
								if(lbkfile.renameTo(new File(defaultLBK))){

//									byte result = BackupManager.getInstance(context).realRestore(
//											defaultLBK, 
//											State.RESTORE_FACTORY, 
//											new EnableState());
//									if(OperationState.SUCCESS == result)
//										isSuccess = true;
//									Log.d(TAG,"LBK Backup result:" + result);
									Intent lbk = new Intent("com.lenovo.lbk.ACTION_RESTORE_COMPLETE");
//							lbk.putExtra(DownloadConstant.EXTRA_RESULT, isSuccess);
									context.sendBroadcast(lbk);
								}
							}
						}else if (categoryInt != DownloadConstant.CATEGORY_WALLPAPER) {

							 LcaInstallerUtils.installApplication(context,
							 di.getInstallPath(),
							 di.getCategory(), di.getPackageName(),di.getVersionCode());
						}
					}
				}
			} else if (LDownloadManager.ACTION_DOWNLOAD_LIST.equals(action)) {
				Bundle b = intent.getExtras();
				if (null != b) {
					int doStatus = b
							.getInt(LDownloadManager.RECEIVER_ACTION_TAG);
					if (doStatus < 3) {
						DownloadInfo di = b
								.getParcelable(LDownloadManager.RECEIVER_DATA_TAG);
						DownloadHelpers.doAction(context, di, doStatus);
					} else if (doStatus == 3) {
						DownloadHelpers.deleteAllTable(context);
					}
				}

			} else if (LDownloadManager.DOWNLOAD_START_ACTION.equals(action)) {
				Log.d(TAG, "IService.DownloadReceiver, start download !!!");
				Bundle b = intent.getExtras();
				String toast = b.getString("toast");
				Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
			} else if (LDownloadManager.ACTION_NOTIFICATION_CLICKED
					.equals(action)) {
				Bundle b = intent.getExtras();
				if (null != b
						&& LDownloadManager.STATUS_VALUES[0].equals(b
								.getString(LDownloadManager.NOTIFY_CLICK_TAG))) {
					DownloadInfo di = b
							.getParcelable(LDownloadManager.RECEIVER_DATA_TAG);
					int status = di.getDownloadStatus();
					Log.d(TAG,
							"IService.DownloadReceiver, click notification bar !!!"
									+ status + "\n" + di.toString());
					if (DownloadInfo.DOWNLOAD_ERROR_HTTP == status
							|| DownloadInfo.DOWNLOAD_ERROR_SDCARD == status) {
						Log.d(TAG,
								"IService.DownloadReceiver, download error !!!");

						// xujing3 temp remove
						 Intent downloadIntent = new
						 Intent(context,DownloadExpandableActivity.class);
						 downloadIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						 try {
						 context.startActivity(downloadIntent);
						 } catch (Exception e) {
						 Toast.makeText(context, R.string.activity_not_found,
						 Toast.LENGTH_SHORT).show();
						 }

						// 2012-01-04 ljc19851119@126.com E
					} else if (DownloadInfo.DOWNLOAD_COMPLETE == status) {
						Log.d(TAG,
								"IService.DownloadReceiver, download completed !!!");

						// xujing3 temp remove
						
						if(di.getCategory()==DownloadConstant.CATEGORY_LBK){
							return;
						}
						
						
						 LcaInstallerUtils.installApplication(context,
						 di.getInstallPath(),
						 di.getCategory(), di.getPackageName(),di.getVersionCode());
					}

				} else if (null != b
						&& LDownloadManager.STATUS_VALUES[1].equals(b
								.getString(LDownloadManager.NOTIFY_CLICK_TAG))) {
					 DownloadInfo di =
					 b.getParcelable(LDownloadManager.RECEIVER_DATA_TAG);

					// xujing3 temp remove
					 Intent downloadIntent = new
					 Intent(context,DownloadExpandableActivity.class);
					 downloadIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					
					 try {
					 context.startActivity(downloadIntent);
					 } catch (Exception e) {
					 Toast.makeText(context, R.string.activity_not_found,
					 Toast.LENGTH_SHORT).show();
					 }
				}
			} else if (LDownloadManager.ACTION_PUSH_APP.equals(action)) {
				String body = intent.getStringExtra("body");
				Map<String, String> data = null;
				try {
					data = Helpers.getAmp(body);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (data != null) {
					NotificationManager nNM = (NotificationManager) context
							.getSystemService(Context.NOTIFICATION_SERVICE);
					int notify_id = (int) System.currentTimeMillis();
					String type = data.get("Type");
					if ("Download".equalsIgnoreCase(type)) {
						Notification notify = new Notification(
								R.drawable.magicdownload_download_succuss,
								context.getString(
										R.string.notify_push_download_title,
										data.get("AppName")),
								System.currentTimeMillis());
						notify.flags |= Notification.FLAG_AUTO_CANCEL;
						Intent notificationIntent = new Intent(
								LDownloadManager.ACTION_DOWNLOAD_PKVC);
						Bundle extras = new Bundle();
						String pkgName = data.get("PackageName");
						String version = data.get("VersionCode");
						String appName = data.get("AppName");
						Log.d(TAG, "PkgName:" + pkgName + " appName:" + appName
								+ " versionCode=" + version);
						extras.putString("pkgName", data.get("PackageName"));
						extras.putString("versionCode", data.get("VersionCode"));
						extras.putString("appName", data.get("AppName"));
						notificationIntent.putExtras(extras);
						PendingIntent contentIntent = PendingIntent
								.getBroadcast(context, 120, notificationIntent,
										PendingIntent.FLAG_UPDATE_CURRENT);
						notify.setLatestEventInfo(context, context.getString(
								R.string.notify_push_download,
								data.get("AppName")), context
								.getString(R.string.notify_content_download),
								contentIntent);
						nNM.notify(notify_id, notify);
					} else if ("Recommend".equalsIgnoreCase(type)) {
						Notification notify = new Notification(
								R.drawable.magicdownload_ic_launcher,
								context.getString(R.string.notify_push_recommend),
								System.currentTimeMillis());
						notify.flags |= Notification.FLAG_AUTO_CANCEL;

					}

				}
			}/*
			 * else if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
			 * { //context.startService(new Intent(context,
			 * DownloadService.class)); }
			 */else if (intent.getAction().equals(
					ConnectivityManager.CONNECTIVITY_ACTION)) {
				 boolean isTD =  GlobalDefine.getVerisonCMCCTDConfiguration(context);
					Log.d(TAG, "share download  onConnectivity >> isTD : "+isTD);
					if(isTD){
						return;
					}
				NetworkInfo info = (NetworkInfo) intent
						.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
				boolean isRemounted = Environment.getExternalStorageState()
						.equals(Environment.MEDIA_MOUNTED);
				if (info != null && info.isConnected() && isRemounted) {
					if (info.getType() == ConnectivityManager.TYPE_WIFI) {
						DownloadHelpers.mNetworkType = LDownloadManager.STATUS_VALUES[1];
					} else {
						DownloadHelpers.mNetworkType = LDownloadManager.STATUS_VALUES[0];
					}
					DownloadHelpers
							.changeNetworkAction(context, info.getType());
					Log.d(TAG,
							"IService.DownloadReceiver, network connected...start DownloadService");
					context.startService(new Intent(context,
							DownloadService.class));
				}
			} else if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
				ConnectivityManager cm = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
				if (activeNetworkInfo != null
						&& activeNetworkInfo.isConnected()) {
					if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
						DownloadHelpers.mNetworkType = LDownloadManager.STATUS_VALUES[1];
					} else {
						DownloadHelpers.mNetworkType = LDownloadManager.STATUS_VALUES[0];
					}
					DownloadHelpers.changeNetworkAction(context,
							activeNetworkInfo.getType());
					Log.d(TAG,
							"IService receive Intent.ACTION_MEDIA_MOUNTED, sd card mounted...start DownloadService");
					context.startService(new Intent(context,
							DownloadService.class));
				}
			} else if (intent.getAction().equals(Constants.ACTION_RETRY)) {
				Log.d(TAG,
						"IService.DownloadReceiver, sd card mounted...start DownloadService");
				context.startService(new Intent(context, DownloadService.class));
			}else if(Intent.ACTION_PACKAGE_CHANGED.equals(action)
					|| Intent.ACTION_PACKAGE_ADDED.equals(action)){
				Log.d(TAG,
						"IService.DownloadReceiver, install");
				String pkgName = intent.getData().getSchemeSpecificPart();
				DownloadInfo info = DownloadHelpers.updateInstallStatus(context, pkgName);
				if(info != null){
					Log.d(TAG,
							"IService.DownloadReceiver, install info:" + info);
					DownloadHelpers.notifyInstalledOrUninstalled(context, 
							info.getPackageName(), info.getVersionCode(), true);
					DownloadHelpers.doAction(context, info, DownloadInfo.DELETE);
				}
			}else if(DownloadConstant.ACTION_APK_PARSE_OR_INSTALL_FAILED.equals(action)){
//				String pkgName = (String) intent.getExtra(DownloadConstant.EXTRA_PACKAGENAME);
//				DownloadHelpers.updateInstallErrorStatus(context, pkgName);
			}else if(LejingpingSettingsValues.WLAN_VALUE_CHANGED_ACTION.equals(action)){
				boolean isWlanOnly = intent.getBooleanExtra(LejingpingSettingsValues.WLAN_VALUE_KEY,false);
				Log.d(TAG,"----------WLAN_VALUE_CHANGED_ACTION---isWlanOnly:" + isWlanOnly);
				Helpers.setwlanDownloadValue(isWlanOnly);
				context.startService(new Intent(context,DownloadService.class));
			}else if(LDownloadManager.ACTION_DOWNLOAD_COUNT_CHANGED.equals(action)){
				DownloadHelpers.updateDownloadNotifacition(context,intent.getIntExtra("count", 0));
			}
			
			Utilities.killLejingpinProcess();
		}
	}
}
