package com.lenovo.lejingpin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.customizer.Reaper;
import com.lenovo.launcher2.customizer.Utilities;
import com.lenovo.lejingpin.network.AmsApplication;
import com.lenovo.lejingpin.share.util.DeviceInfo;

public class LockScreenFragment extends Fragment {
	private static final int MSG_GETLOCALDATE = 100;
	private static final int MSG_PACKAGECHANGE = 101;
//	private static final int MSG_APK_ADDED = 102;
//	public static final int MSG_APK_REMOVED = 103;
	private static final int VIEW_LOCALDATE = 200;
	private static final int VIEW_GETMORE = 201;
	// protected static final int MSG_GETLOCALDATE = 100;
	private static final String SP_LOCKSCREEN_PREVIEW_URL = "download_lockscreenpreview_url";
	private static final String LOCK_SETTING_CLASS_NAME = "lock_setting_class_name";
	private static final String LOCK_SETTING_PACKAGE_NAME = "lock_setting_package_name";
	private static final String TAG = "LockScreenFragment";
	private static final int TYPE_LOCAL = 12;
	public static final int START_FOR_DETAIL = 0;
	private LEJPConstant mLeConstant = LEJPConstant.getInstance();;

	private static long lastClickTime;
	private View allView;
	private Activity mActivity;
	private LayoutInflater mInflater;
	private View mLoadingView;
	private GridView mGridView;
	private ArrayList<AmsApplication> mLocalDataList;
	// private boolean mRefreshFlag; // for what?
	private MyGridViewAdpater mGrapAdapter;
	private MyUIReceiver mUiReceiver;
	private MyPakChanger mApkReceiver;
	private List<ResolveInfo> mLockAPKList;
	// private boolean mneedFlash = false;
	private View mcurerntView;
	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_GETLOCALDATE:
				if (mLocalDataList.size() == 0) {
					changeView(VIEW_GETMORE);
				}
				if (mLocalDataList.size() > 0) {
					if (mGrapAdapter == null) {
						mGrapAdapter = new MyGridViewAdpater();
					}
					mGridView.setAdapter(mGrapAdapter);
					mGrapAdapter.notifyDataSetChanged();
					changeView(VIEW_LOCALDATE);
				}
				break;
			case MSG_PACKAGECHANGE:
				Log.i(TAG,"the mLocalDataList.size() is"+mLocalDataList.size());
				if (mLocalDataList.size() == 0) {
					changeView(VIEW_GETMORE);
				} else {
					if(mGrapAdapter== null){
//						Log.i(TAG,"an new adapter");
						mGrapAdapter = new MyGridViewAdpater();
						mGridView.setAdapter(mGrapAdapter);
					}else{
						mGrapAdapter.notifyDataSetChanged();
					}
					if (!mcurerntView.equals(mGridView)) {
						changeView(VIEW_LOCALDATE);
					}
				}
				break;
			 default:
                        	break;

			}
		}

	};

	private ImageButton mEmptyView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 Log.i(TAG, "ThLockScrrenFragement's oncreate");
		mActivity = getActivity();
		// TonghuLog.i(TAG,"oncrete-----"+mActivity.getClass().getName());
		mInflater = LayoutInflater.from(mActivity);
		initThemeList();
//		mLocalDataList = new ArrayList<AmsApplication>();
//		mGrapAdapter = new MyGridViewAdpater();
//		mGridView.setAdapter(mGrapAdapter);
		registerreceiver();

	}

	private void registerreceiver() {
		// Intent i = new Intent(Intent.ACTION_MAIN, null);
		// i.setAction("");
		// mLockAPKList =
		// getActivity().getPackageManager().queryIntentActivities(i, 0);

		mUiReceiver = new MyUIReceiver();
		IntentFilter filter = new IntentFilter("refresh");
		mActivity.registerReceiver(mUiReceiver, filter);
		
		mApkReceiver = new MyPakChanger();
		IntentFilter filter1 = new IntentFilter();
		filter1.addAction(Intent.ACTION_PACKAGE_ADDED);
		filter1.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter1.addAction(Intent.ACTION_PACKAGE_REPLACED);
		filter1.addDataScheme("package");
		mActivity.registerReceiver(mApkReceiver, filter1);
		
	}
	@Override
	public void onDestroy() {
//		TonghuLog.i(TAG, "ThLockScrrenFragement's ondetch");
		if (mUiReceiver != null) {
			mActivity.unregisterReceiver(mUiReceiver);
		}

		if (mApkReceiver != null) {
			mActivity.unregisterReceiver(mApkReceiver);
		}
//		mLeConstant.mServiceLocalLockAmsDataList = mLocalDataList;	//useful?
		super.onDestroy();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 Log.i(TAG, "ThLockScreenFragment's oncreateView");
		// TonghuLog.i(TAG,"oncreateview-----"+getActivity().getClass().getName());
		if(allView!=null){
			if(mLocalDataList!=null){
				return allView;
			}
		}
		allView = inflater.inflate(R.layout.fragment_lockscreen1, container,
				false);
		mLoadingView = allView.findViewById(R.id.loading);
		mcurerntView = mLoadingView;
		
		mGridView = (GridView) allView.findViewById(R.id.gridview);
		mEmptyView = (ImageButton) allView.findViewById(R.id.bt_getmore);
		
		int width = DeviceInfo.getInstance(mActivity).getWidthPixels();
		int x = (width-dp2px(28))/3;
		if(width >= 720){
			x = (width -dp2px(54))/3;
		}
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(x>240?240:x, x>240?400:(int)(x*1.55));
//		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(x, (int)(x*1.6));
		if(width >= 720){
			params.setMargins(dp2px(10), dp2px(3), 0, 0);
		}else{
			params.setMargins(dp2px(5), dp2px(3), 0, 0);
		}
		mEmptyView.setLayoutParams(params);
		
		mEmptyView.setScaleType(ScaleType.CENTER);
		
		mEmptyView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((ClassicFragmentActivity) mActivity).changeLockView();
			}
		});
		init();
		return allView;
	}


	// to get local screeen data
	private void init() {
		
		new Thread(new Runnable() {
			public void run() {
				getLocalData();
				mHandler.removeMessages(MSG_GETLOCALDATE);
				mHandler.sendMessage(mHandler.obtainMessage(MSG_GETLOCALDATE));
			}
		}).start();
	}

	// change to the showing view.
	private void changeView(int type) {
		switch (type) {
		case VIEW_LOCALDATE:
			mEmptyView.setVisibility(View.GONE);
			mLoadingView.setVisibility(View.GONE);
			mGridView.setVisibility(View.VISIBLE);
			mcurerntView = mGridView;
			break;
		case VIEW_GETMORE:
			mLoadingView.setVisibility(View.GONE);
			mGridView.setVisibility(View.INVISIBLE);
			mEmptyView.setVisibility(View.VISIBLE);
			mcurerntView = mEmptyView;
			break;
		default:
			break;
		}
	}

	// get local date
	private void getLocalData() {
		PackageManager packageManager = mActivity.getPackageManager();
		SharedPreferences sp_preview = mActivity.getSharedPreferences(
				SP_LOCKSCREEN_PREVIEW_URL, 0);
		if (mLocalDataList == null) {
			mLocalDataList = new ArrayList<AmsApplication>();
		} else {
			mLocalDataList.clear();
		}
		// List<ResolveInfo> mList;
		// Intent in = new Intent();
		// in.setAction(Intent.ACTION_MAIN);
		// in.addCategory("android.service.fakelock");
		// mList = packageManager.queryIntentActivities(in,
		// PackageManager.GET_META_DATA);

		// List<ResolveInfo> mFakeList = packageManager.queryIntentActivities(
		// new Intent("android.service.fakelock"),
		// PackageManager.GET_META_DATA);
		// mList.addAll(mFakeList);
		// add jingling's lockscreen

		// List<ApplicationInfo> installedApplications = packageManager
		// .getInstalledApplications(0);
		// for (ApplicationInfo applicationInfo : installedApplications) {
		// String packageName = applicationInfo.packageName;
		//
		// if (packageName.contains("com.qigame")) {
		//
		// AmsApplication data = new AmsApplication();
		// data.setPackage_name(packageName);
		// String applable = "";
		// try {
		// applable = packageManager.getApplicationLabel(
		// packageManager.getApplicationInfo(packageName, 0))
		// .toString();
		// } catch (NameNotFoundException e) {
		// e.printStackTrace();
		// }
		// if (applable == null)
		// // applable =
		// // (resolveInfo.loadLabel(packageManager)).toString();
		// applable = "";
		// data.setAppName(applable);
		// String filepath = sp_preview.getString(packageName, null);
		// if (filepath != null) {
		// data.thumbpaths = new String[1];
		// data.thumbpaths[0] = filepath;
		// data.setIsNative(false);
		// } else {
		// data.setIsNative(true);
		// // mRefreshFlag = true;
		// Drawable thumb = null;
		//
		// String preViewName = "lock_screen_preview";
		// Context mFriendContext = null;
		// try {
		// mFriendContext = mActivity.createPackageContext(packageName,
		// Context.CONTEXT_IGNORE_SECURITY);
		// } catch (NameNotFoundException e1) {
		// e1.printStackTrace();
		// continue;
		// }
		// thumb = Utilities.findDrawableByResourceName(preViewName,
		// mFriendContext);
		// if(thumb == null){
		// try {
		// thumb = packageManager.getApplicationIcon(packageName);
		// } catch (NameNotFoundException e) {
		// thumb = mActivity.getResources().getDrawable(
		// R.drawable.lemagicdownload_push_app_icon_def);
		// e.printStackTrace();
		// }
		// if (thumb == null) {
		// thumb = mActivity.getResources().getDrawable(
		// R.drawable.lemagicdownload_push_app_icon_def);
		// }
		// }
		// data.setIsAlien(true);
		// data.setpreviewResId(thumb);
		// }
		// mLocalDataList.add(data);
		// // TonghuLog.i(TAG,
		// // "the jinglin' size is " + mLocalDataList.size());
		// }
		// }

		int listSize = mLockAPKList.size();
		for (int i = 0; i < listSize; i++) {
			ResolveInfo resolveInfo = mLockAPKList.get(i);
			AmsApplication data = new AmsApplication();
			String pkgname = resolveInfo.activityInfo.applicationInfo.packageName;
			data.setPackage_name(pkgname);
			String applable = "";
			CharSequence applicationLabel = packageManager.getApplicationLabel(
					resolveInfo.activityInfo.applicationInfo);
			if(applicationLabel==null){
				applable = (resolveInfo.loadLabel(packageManager)).toString();
			}else{
				applable = applicationLabel.toString();
			}
			data.setAppName(applable);
			String filepath = sp_preview.getString(pkgname, null);
//			if (filepath != null) {
//				data.thumbpaths = new String[1];
//				data.thumbpaths[0] = filepath;
//				data.setIsNative(false);
//			} else {
//				data.setIsNative(true);
//				// mRefreshFlag = true;
//				Drawable thumb = null;
//
//				String preViewName = "lock_screen_preview";
//				Context mFriendContext = null;
//				try {
//					mFriendContext = mActivity.createPackageContext(pkgname,
//							Context.CONTEXT_IGNORE_SECURITY);
//				} catch (NameNotFoundException e1) {
//					e1.printStackTrace();
//					continue;
//				}
//				thumb = Utilities.findDrawableByResourceName(preViewName,
//						mFriendContext);
//				if (thumb == null) {
//					try {
//						thumb = packageManager.getApplicationIcon(pkgname);
//					} catch (NameNotFoundException e) {
//						thumb = mActivity.getResources().getDrawable(
//								R.drawable.lemagicdownload_push_app_icon_def);
//						e.printStackTrace();
//					}
//					if (thumb == null) {
//						thumb = mActivity.getResources().getDrawable(
//								R.drawable.lemagicdownload_push_app_icon_def);
//					}
//				}
//				
//				data.setIsAlien(true);
//				data.setpreviewResId(thumb);
//			}
			data.setIsNative(true);
			Drawable thumb = null;

			String preViewName = "lock_screen_preview";
			Context mFriendContext = null;
			try {
				mFriendContext = mActivity.createPackageContext(pkgname,
						Context.CONTEXT_IGNORE_SECURITY);
			} catch (NameNotFoundException e1) {
				e1.printStackTrace();
				continue;
			}
			thumb = Utilities.findDrawableByResourceName(preViewName,
					mFriendContext);
			if (thumb == null) {
				try {
					thumb = packageManager.getApplicationIcon(pkgname);
				} catch (NameNotFoundException e) {
					thumb = mActivity.getResources().getDrawable(
							R.drawable.lemagicdownload_push_app_icon_def);
					e.printStackTrace();
				}
				if (thumb == null) {
					thumb = mActivity.getResources().getDrawable(
							R.drawable.lemagicdownload_push_app_icon_def);
				}
			}
			
			data.setIsAlien(true);
			data.setpreviewResId(thumb);
			mLocalDataList.add(data);

		}
//		TonghuLog.i(TAG,
//				"the mlocaldatalist's size is:" + mLocalDataList.size());
	}

	private class MyGridViewAdpater extends BaseAdapter {
		private String mCurrentLockScrren;

		public MyGridViewAdpater() {
			super();
			mCurrentLockScrren = getCurrentLockScreenPak(mActivity);
		}

		@Override
		public void notifyDataSetChanged() {
			// TonghuLog.i(TAG,
			// "notifydatasetchange and the currentLockScreen is" +
			// getCurrentLockScreenPak(mActivity));
			mCurrentLockScrren = getCurrentLockScreenPak(mActivity);
			super.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mLocalDataList.size();
		}

		@Override
		public Object getItem(int position) {
			return mLocalDataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.local_theme_item,
						parent, false);
			}
			final TextView wallpaperview = (TextView) convertView
					.findViewById(R.id.textname);
			final ImageView image = (ImageView) convertView
					.findViewById(R.id.textpic);
			final ImageView current = (ImageView) convertView
					.findViewById(R.id.current);

			AmsApplication mLocalData = mLocalDataList.get(position);
			if (!mLocalData.getIsNative()) {
				if (mLocalData.thumbpaths != null) {
					String filePath = mLocalData.thumbpaths[0];
					File icon = new File(filePath);
					image.setImageURI(Uri.fromFile(icon));
				}
			} else {
				image.setScaleType(ImageView.ScaleType.FIT_CENTER);
				image.setScaleType(ImageView.ScaleType.FIT_XY);
				image.setImageDrawable((mLocalData.getpreviewResId()).get(0));
			}
//			TonghuLog.i(TAG, "the mCurrentLockScreen is " + mCurrentLockScrren);

			if (mLocalData.getPackage_name().equals(mCurrentLockScrren)) {
				current.setVisibility(View.VISIBLE);
				LEJPConstant.getInstance().mCurrentLockscreen = mCurrentLockScrren;
			} else {
				current.setVisibility(View.INVISIBLE);
			}
			wallpaperview.setText(mLocalData.getAppName());

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					if (!isFastDoubleClick()) {
						startLockscreenDetailActivity(position);
					}
				}
			});
			return convertView;
		}

		private void startLockscreenDetailActivity(int position) {
			mLeConstant.mServiceLocalLockAmsDataList = mLocalDataList;
			Intent intent = new Intent(getActivity(),
					DetailClassicActivityLock.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("EXTRA", position);
			intent.putExtra("TYPEINDEX", TYPE_LOCAL);
			startActivityForResult(intent, START_FOR_DETAIL);
		}

	}

	private class MyUIReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if ("lockscreen".equals(intent.getStringExtra("type"))) {
				if(mGrapAdapter!=null){
					mGrapAdapter.notifyDataSetChanged();
				}
			}
		}
	}

	private class MyPakChanger extends BroadcastReceiver {


		@Override
		public void onReceive(Context context, Intent intent) {
//			PackageManager pm = mActivity.getPackageManager();
//			String action = intent.getAction();
//			String packageName = intent.getData().getSchemeSpecificPart();
//			ComponentName component = pm.getLaunchIntentForPackage(packageName)
//					.getComponent();
//			ActivityInfo a = pm.getActivityInfo(component, 0);
//			if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
//				if (LEJPConstant.getInstance().mCurrentLockscreen
//						.equals(packageName)) {
//					LEJPConstant.getInstance().mCurrentLockscreen = "";
//				}
//			}
//			boolean isLock = pm.getLaunchIntentForPackage(packageName)
//					.getCategories().contains(action);
//			TonghuLog.i(TAG,
//					"categorys is "
//							+ pm.getLaunchIntentForPackage(packageName)
//									.getCategories());
//			TonghuLog.i(TAG, "isLock:" + isLock);
//			if (isLock) {
//				new Thread(new Runnable() {
//					@Override
//					public void run() {
//						getLocalData();
//						mHandler.removeMessages(MSG_PACKAGECHANGE);
//						mHandler.sendMessage(mHandler
//								.obtainMessage(MSG_PACKAGECHANGE));
//					}
//				}).start();
//			}
//			String action = intent.getAction();
//			final String packageName = intent.getData().getSchemeSpecificPart();
//			if (Intent.ACTION_PACKAGE_REMOVED.equals(action)
//					|| Intent.ACTION_PACKAGE_ADDED.equals(action)) {
//
//				checkThemeAPK(packageName, action);
//			}
			String action = intent.getAction();
			final String packageName = intent.getData().getSchemeSpecificPart();
			if (Intent.ACTION_PACKAGE_REMOVED.equals(action)
					|| Intent.ACTION_PACKAGE_ADDED.equals(action)) {

				checkThemeAPK(packageName, action);
			}
		}

		private void checkThemeAPK(final String mPkgName, String action) {
			if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
				initThemeList();
				if (mLockAPKList != null) {
					int listSize = mLockAPKList.size();
					for (int j = 0; j < listSize; j++) {
						ResolveInfo resolveInfo = mLockAPKList.get(j);
						if (resolveInfo.activityInfo.applicationInfo.packageName
								.equals(mPkgName)) {
//							TonghuLog.i(TAG,"add a new apk name is :"+mPkgName);
							new Thread(new Runnable() {
								@Override
								public void run() {
//									Log.i(TAG,"tonghu --- get a new package"+mPkgName);
									getLocalData();
//									Log.i(TAG,"the message is null?"+mHandler.obtainMessage(MSG_PACKAGECHANGE)==null?"yes":"no");
//									Message message = new Message();
//									message.what = MSG_PACKAGECHANGE;
//									mHandler.sendMessage(message);
									mHandler.removeMessages(MSG_PACKAGECHANGE);
									mHandler.sendMessage(
											mHandler.obtainMessage(MSG_PACKAGECHANGE));
//									Log.i(TAG,"send a getPackgeChanger message");
								}
							}).start();
						}
					}
				}
			} else {
				checkDelete(mPkgName);
			}
		}

		private void checkDelete(String mPkgName) {
			if (mLocalDataList != null
					&& mLocalDataList.size() != 0) {
				int listsize = mLocalDataList.size();
				for (int i = 0; i < listsize; i++) {
					AmsApplication mdata = mLocalDataList.get(i);
					String pkgName = mdata.getPackage_name();
					if (pkgName != null && pkgName.equals(mPkgName)) {
//						TonghuLog.i(TAG,"remove a  apk name is :"+mPkgName);
						
						mLocalDataList.remove(mdata);
//						mLeConstant.mServiceLocalLockAmsDataList.remove(mdata);
//						mHandler.removeMessages(MSG_PACKAGECHANGE);
						mHandler.sendMessageDelayed(
								mHandler.obtainMessage(MSG_PACKAGECHANGE), 0);
						break;
					}
				}
			}
		}

	}

	private void initThemeList() {
		PackageManager manager = mActivity.getPackageManager();
		
		final Intent bmainIntent = new Intent(Intent.ACTION_MAIN);
		bmainIntent.addCategory("android.service.famelock");
		mLockAPKList = manager.queryIntentActivities(bmainIntent, 0);
		Intent selfIntent1 = new Intent("android.service.lock");
		mLockAPKList.addAll(manager.queryIntentActivities(selfIntent1, 0));
		Intent selfIntent2 = new Intent("android.service.fakelock");
		mLockAPKList.addAll(manager.queryIntentActivities(selfIntent2, 0));
	}

	// private void checkThemeAPK(String mPkgName, String action) {
	// if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
	// initThemeList();
	// if (mLockAPKList != null) {
	// int listSize = mLockAPKList.size();
	// for (int j = 0; j < listSize; j++) {
	// ResolveInfo resolveInfo = mLockAPKList.get(j);
	// if (resolveInfo.activityInfo.applicationInfo.packageName
	// .equals(mPkgName)) {
	// break;
	// }
	// }
	// }
	// removeTheUninstallThemeApk(mPkgName);
	// }
	// }

	// private void notifyDetail(String packageName) {
	// Intent intent = new Intent(
	// "com.lenovo.lejingpin.lockscreen.LocalLockScreenFragment.lockdatachange");
	// mActivity.sendBroadcast(intent);
	// }
	public static String getCurrentLockScreenPak(Context mActivity) {
		ContentResolver resolver = mActivity.getContentResolver();
		int on_off = -1;
		try {
			on_off = Settings.System.getInt(resolver, "lock_screen_on_off");
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
			return "";
		}
		if (on_off == 1 || on_off == 2) {
			return Settings.System.getString(resolver,
					LOCK_SETTING_PACKAGE_NAME);
		}
		return "";
	}

	private boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 800) {
			return true;
		}
		lastClickTime = time;
		return false;
	}
	
	private int dp2px(float dipValue){
	     final float scale=mActivity.getResources().getDisplayMetrics().density;
	     return (int)(dipValue*scale+0.5f);
	}

}
