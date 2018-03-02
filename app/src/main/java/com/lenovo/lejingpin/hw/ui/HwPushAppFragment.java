package com.lenovo.lejingpin.hw.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.commonui.LeAlertDialog;
import com.lenovo.launcher2.customizer.Reaper;

import com.lenovo.lejingpin.hw.content.data.DownloadAppInfo;
import com.lenovo.lejingpin.hw.content.data.HwConstant;
import com.lenovo.lejingpin.hw.content.db.HWDBUtil;
import com.lenovo.lejingpin.settings.LejingpingSettingsValues;
import com.lenovo.lejingpin.share.download.DownloadExpandableActivity;
import com.lenovo.lejingpin.hw.lcapackageinstaller.utils.LcaInstallerUtils;
import com.lenovo.lejingpin.hw.ui.RecommendLocalAppInfo.Status;
import com.lenovo.lejingpin.hw.utils.AppSettings;
import com.lenovo.lejingpin.hw.utils.ColorUtil;

import com.lenovo.lejingpin.share.download.AppDownloadUrl;
import com.lenovo.lejingpin.share.download.AppDownloadUrl.Callback;
import com.lenovo.lejingpin.share.download.DownloadConstant;
import com.lenovo.lejingpin.share.download.DownloadHandler;
import com.lenovo.lejingpin.share.download.DownloadInfo;
import com.lenovo.lejingpin.share.download.LDownloadManager;

public class HwPushAppFragment extends Fragment implements
		LoaderCallbacks<List<RecommendLocalAppInfo>>, Callback{
	public static final String TAG = "HwPushAppFragment";
	public static final int MSG_UPGRADE = 1;

	private static final String ACTION_HAWAII_DESTROY = "com.lenovo.leos.hw.action_destroy";
	private static final String LINK_APPSTORE = "http://www.lenovomm.com/appstore/html/index.html";
	private Context mContext;
	private List<RecommendLocalAppInfo> mApps;
	private List<RecommendLocalAppInfo> mTopApps;
	private List<RecommendLocalAppInfo> mContentApps;
	private CopyOnWriteArrayList<RecommendLocalAppInfo> mDownloadingApps;
	private UiReceiver mUiReceiver;

	private static boolean mIsShowFTip = false;
	// yangmao add start
	private Drawable mDefIcon;

	private AlertDialog mFTip;
	private View mEmptyView;
	private ListView mListView;
	private View mLoading;

	private AppGallery mTopAppGallery;
	private static final int TOP_GALLERY_CHANGE_BANNER = 2;
	private boolean mTouch = false;

	private LeAlertDialog mEnableDialog;

	// yangmao add start

	public static final Uri CONENT_DOWNLOAD_URI = Uri
			.parse("content://com.lenovo.lejingpin.hw.content.download/download/");
	public static final String EXTRA_PACKAGE_NAME = "com.lenovo.leos.hw.PACKAGE_NAME";
	public static final String EXTRA_VERSION_CODE = "com.lenovo.leos.hw.VERSION_CODE";

	// yangmao add end

	public static boolean isStartVersionUpdateFlag = false;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what){
			case TOP_GALLERY_CHANGE_BANNER:
				if (mTopAppGallery != null){
					mTopAppGallery.setSoundEffectsEnabled(false);
					mTopAppGallery.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
				}
				break;
				default:
					Log.d(TAG, "handleMessage default.");
			}
		}
	};

	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("yangmao_install", "oncreate");
		mHandler.post(new InitParamterRunnable());
	}

	class InitParamterRunnable implements Runnable {
		@Override
		public void run() {
			initParameter();
		}
	}

	class SetUpView implements Runnable {
		private View root;

		public SetUpView(View rootView) {
			root = rootView;
		}

		@Override
		public void run() {
			setupView(root);
			getLoaderManager().initLoader(0, null, HwPushAppFragment.this);
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView...");
		View root = inflater.inflate(R.layout.apps, container, false);
		
		//release old 
		mEmptyView = null;
		
		if (!Util.getInstance().isNetworkEnabled(getActivity())) {
			showNetWorkEnable(R.string.app_name);
			changeToEmptyState(root, R.string.network_close_body, false);
		} else {
			startTopAppAutoFilp();
			SetUpView setview = new SetUpView(root);
			mHandler.post(setview);
		}
		
		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d(TAG, "onActivityCreated...");
		super.onActivityCreated(savedInstanceState);
	}

	private int getScreenWidth() {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	private void initParameter() {
		setHasOptionsMenu(true);
		IntentFilter filter = new IntentFilter();
		filter.addAction(HwConstant.ACTION_DOWNLOAD_STATE);
		filter.addAction(DownloadConstant.ACTION_APK_FAILD_DOWNLOAD);
		filter.addAction(DownloadConstant.ACTION_DOWNLOAD_STATE_CHANGED);
		filter.addAction(DownloadExpandableActivity.ACTION_DOWNLOAD_DELETE);
		filter.addAction(HwConstant.ACTION_APP_START_DOWNLOAD);
		filter.addAction(HwConstant.ACTION_REQUEST_APP_INFO);
		filter.addAction(HwConstant.ACTION_PACKAGE_ADDED);
		filter.addAction(DownloadConstant.ACTION_APK_PARSE_OR_INSTALL_FAILED);
		filter.addAction(DownloadConstant.ACTION_DOWNLOAD_RESUME);
		mUiReceiver = new UiReceiver(mContext);
		mContext.registerReceiver(mUiReceiver, filter);
		// yangmao add start 0107
		Reaper.processReaper(mContext, Reaper.REAPER_EVENT_CATEGORY_LEJINGPIN,
				Reaper.REAPER_EVENT_ACTION_LEJINGPIN_RECOMMAPPENTRY,
				Reaper.REAPER_NO_LABEL_VALUE, Reaper.REAPER_NO_INT_VALUE);
		// yangmao add end 0107

	}

	private void setupView(View view) {
		Resources r = getResources();
		int iconSize = (int) r.getDimension(R.dimen.app_icon_size);
		mDefIcon = r.getDrawable(R.drawable.lepush_app_icon_def);
		mDefIcon.setBounds(0, 0, iconSize, iconSize);

		ViewGroup root = (ViewGroup) view;
		View pushAppViewStup = ((ViewStub) root
				.findViewById(R.id.push_app_fragment)).inflate();
		mLoading = pushAppViewStup.findViewById(R.id.page_loading);
		mListView = (ListView) pushAppViewStup.findViewById(R.id.app_pager);
		AppListAdapter appListAdapter = new AppListAdapter();
		initHeadAndFootView();
		mListView.setAdapter(appListAdapter);

	}

	private void showNetWorkEnable(int title) {
		mEnableDialog = new LeAlertDialog(getActivity(),
				R.style.Theme_LeLauncher_Dialog_Shortcut);
		mEnableDialog.setLeTitle(title);
		mEnableDialog.setLeMessage(R.string.confirm_network_open);
		mEnableDialog.setLePositiveButton(getActivity().getResources()
				.getString(R.string.rename_action),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						startConfirm();
						getActivity().finish();
					}
				});
		mEnableDialog.setLeNegativeButton(getActivity().getResources()
				.getString(R.string.cancel_action),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						mEnableDialog.dismiss();
					}
				});

		mEnableDialog.show();

	}

	public void startConfirm() {
		Intent intent = new Intent();
		intent.setClass(getActivity(),
				com.lenovo.launcher2.settings.SeniorSettings.class);
		startActivity(intent);
	}

	private void initHeadAndFootView() {
		View headView = LayoutInflater.from(mContext).inflate(
				R.layout.top_app_item_layout, null);
		headView.setVisibility(View.GONE);
		headView.setTag("app_head_view");
		View footView = LayoutInflater.from(mContext).inflate(
				R.layout.app_item_foot_layout, null);
		footView.setTag("app_foot_view");
		footView.setVisibility(View.GONE);
		initHeadView(headView);
		initFootView(footView);
		mListView.addHeaderView(headView);
		mListView.addFooterView(footView);
	}

	private void initHeadView(View paramView) {
		mTopAppGallery = (AppGallery) paramView.findViewById(R.id.gallery_app);
		final LinearLayout radioLinearLayout = (LinearLayout) paramView
				.findViewById(R.id.home_advs_gallery_mark);
		LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		ll.gravity = Gravity.RIGHT | Gravity.CENTER;
		ll.leftMargin = 5;
		ll.rightMargin = 5;
		ImageView first = new ImageView(getActivity());
		first.setId(0);
		first.setImageResource(R.drawable.dot_press);
		first.setClickable(false);
		radioLinearLayout.addView(first, ll);

		for (int i = 1; i < 5; i++) {
			ImageView rb = new ImageView(getActivity());
			rb.setId(i);
			rb.setImageResource(R.drawable.dot_default);
			rb.setClickable(false);
			radioLinearLayout.addView(rb, ll);
		}
		mTopAppGallery
				.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> paramAdapterView,
							View paramView, int paramInt, long paramLong) {
						int size = mTopApps.size();
						showAppDetail(mTopApps.get(paramInt % size));

					}
				});
		

		mTopAppGallery.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN) {
					if (action == MotionEvent.ACTION_DOWN) {
						mTouch = true;
					} else if (action == MotionEvent.ACTION_UP) {
						mTouch = false;
					}
				}
				return false;
			}

		});
		mTopAppGallery
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						int size = mTopApps.size();
						int index = arg2 % size;
						if (radioLinearLayout != null) {
							ImageView now = (ImageView) radioLinearLayout
									.getChildAt(index);
							now.setImageResource(R.drawable.dot_press);
							for (int i = 0; i < radioLinearLayout
									.getChildCount(); i++) {
								if (i != index) {
									ImageView previous = (ImageView) radioLinearLayout
											.getChildAt(i);
									previous.setImageResource(R.drawable.dot_default);
								}
							}
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {

					}

				});
		TopAppListAdapter adapter = new TopAppListAdapter();
		mTopAppGallery.setAdapter(adapter);
	}

	private void initFootView(View footView) {
		Button button = (Button) footView.findViewById(R.id.app_store_button);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String storePkName = "com.lenovo.leos.appstore";
				boolean appStore = isInstalled(storePkName);
				if (appStore) {
					runApp(storePkName);
				} else {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri
							.parse(LINK_APPSTORE)));
				}
			}
		});
	}

	private void changeToEmptyState(View parent, final int stringId,
			boolean isDisplayButton) {
		Log.i(TAG, "changeToEmptyState...");
		if (mEmptyView == null) {
			if (parent != null) {

				mEmptyView = ((ViewStub) parent.findViewById(R.id.empty_stub))
						.inflate();
			} else {
				mEmptyView = ((ViewStub) getView()
						.findViewById(R.id.empty_stub)).inflate();
			}
		} else {
			mEmptyView.setVisibility(View.VISIBLE);
		}
		Button emptyButton = (Button) mEmptyView.findViewById(R.id.empty_btn);
		if (stringId == R.string.confirm_network_open) {
			emptyButton.setText(R.string.d_switch_ok);
		} else if (stringId == R.string.hw_ui_widget_get_spere_list_error) {
			emptyButton.setText(R.string.push_app_refresh);
		}
		if (isDisplayButton) {
			emptyButton.setVisibility(View.VISIBLE);
			emptyButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View paramView) {
					if (stringId == R.string.confirm_network_open) {
						Intent intent = new Intent();
						intent.setClass(
								getActivity(),
								com.lenovo.launcher2.settings.SeniorSettings.class);
						startActivity(intent);
						getActivity().finish();
					} else if (stringId == R.string.grid_empty_error) {
						requestLoadApps();
					}
				}

			});
		} else {
			emptyButton.setVisibility(View.GONE);
		}
		TextView text = (TextView) mEmptyView.findViewById(R.id.empty_text);
		CharSequence text_string = getText(stringId);

		text.setText(Html.fromHtml(text_string.toString()));
		text.setMovementMethod(LinkMovementMethod.getInstance());
	}

	public static Drawable findDrawableByResourceName(String name,
			Context context) {
		if (context == null)
			return null;
		try {
			int resID = context.getResources().getIdentifier(name, "drawable",
					context.getPackageName());
			if (resID == 0) {
				return null;
			}
			Drawable drawable = context.getResources().getDrawable(resID);
			return drawable;
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void startActivity(Intent intent) {
		try {
			super.startActivity(intent);
		} catch (Exception e) {
			Toast.makeText(mContext, R.string.activity_not_found,
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public Loader<List<RecommendLocalAppInfo>> onCreateLoader(int id,
			Bundle args) {
		Log.d(TAG, "onCreateLoader....");
		return new AppLoader(mContext);
	}

	@Override
	public void onLoadFinished(Loader<List<RecommendLocalAppInfo>> loader,
			List<RecommendLocalAppInfo> data) {
		mApps = data;

		if (mApps != null && !mApps.isEmpty()) {
			if (mTopApps != null) {
				mTopApps.clear();
			} else {
				mTopApps = new ArrayList<RecommendLocalAppInfo>();
			}
			if (mContentApps != null) {
				mContentApps.clear();
			} else {
				mContentApps = new ArrayList<RecommendLocalAppInfo>();
			}
			if (mDownloadingApps != null) {
				mDownloadingApps.clear();
			} else {
				mDownloadingApps = new CopyOnWriteArrayList<RecommendLocalAppInfo>();
			}

			int size = mApps.size();
			if (size > 5) {
				mTopApps.addAll(mApps.subList(0, 5));
				mContentApps.addAll(mApps.subList(5, size - 1));
			}
		}
		mLoading.setVisibility(View.GONE);
		mListView.findViewWithTag("app_head_view").setVisibility(View.VISIBLE);
		mListView.findViewWithTag("app_foot_view").setVisibility(View.VISIBLE);

		if (data == null || data.isEmpty()) {
			if (loader instanceof AppLoader) {
				AppLoader l = (AppLoader) loader;
				boolean result = l.getResult();
				changeToEmptyState(null, result ? R.string.list_empty_1
						: R.string.grid_empty_error, !result ? true : false);
			}
		} else {
			mHandler.post(new RefreshPagetRunnable());
			if (mEmptyView != null) {
				mEmptyView.setVisibility(View.GONE);
			}
			if (mIsShowFTip)
				showFTip();
		}
	}

	@Override
	public void onLoaderReset(Loader<List<RecommendLocalAppInfo>> arg0) {
		if (mApps != null) {
			mApps.clear();
		}
		Log.d(TAG, "onLoaderReset....");
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, "onStart......");
	}

	@Override
	public void onPause() {
		Log.d(TAG, "onPause......");
		if (mTopAutoFilpThread != null) {
			mTopAutoFilpThread.threadPause();
		}
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume......");
		if (mTopAutoFilpThread != null) {
			mTopAutoFilpThread.threadResume();
		}
		// mHandler.post(new RefreshPagetRunnable());
	}

	class RefreshPagetRunnable implements Runnable {

		@Override
		public void run() {
			refreshPages();
		}
	}

	@Override
	public void onStop() {
		Log.d(TAG, "onStop......");
		super.onStop();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		Log.d(TAG, "onHiddenChanged >> hidden : " + hidden);
		super.onHiddenChanged(hidden);
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy......");
		mHandler.removeMessages(TOP_GALLERY_CHANGE_BANNER);
		if (mApps != null) {
			mApps.clear();
			mApps = null;
		}
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			LDownloadManager.getDefaultInstance(mContext).deleteAllTask();
		}
		if (null != mUiReceiver) {
			mContext.unregisterReceiver(mUiReceiver);
		}

//		if (mPushAppContentObserver != null) {
//			mPushAppContentObserver.releasePushAppContentObserver();
//		}

		notifyLauncher();

		super.onDestroy();
	}

	private void newDoloadGameApp(final RecommendLocalAppInfo app) {
		if(LejingpingSettingsValues.wlanDownloadValue(mContext) && Util.isMobileNetWork(mContext)){
			LejingpingSettingsValues.popupWlanDownloadDialog(mContext);
		}else{
			downloadApp(app);
		}
	}

	private void downloadApp(final RecommendLocalAppInfo app) {
		
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(mContext, R.string.download_sd_error_1,
					Toast.LENGTH_SHORT).show();
			return;
		}else{
			boolean isEnough = Util.getInstance().spaceIsEnough(Long.parseLong(app.getAppSize()));
			Log.d(TAG,"downloadApp >> isEnough : "+isEnough);
			if(!isEnough){
				Toast.makeText(mContext, R.string.download_sdcard_notexists,
						Toast.LENGTH_SHORT).show();
				return;
			}
		}

		String network = Util.getConnectType(mContext);

		if ("other".equals(network)) {
			Toast.makeText(mContext, R.string.error_network_state,
					Toast.LENGTH_SHORT).show();
		} else {
			if (!mDownloadingApps.contains(app)) {

				mDownloadingApps.add(app);
			}
			notifyDownloadApp(app.packageName, app.versionCode, app.name,
					app.iconUrl, app.getVersion());
			String position = app.postion;
			Log.i("yangmao_repaer", "position is:" + position);
			// yangmao add start 0107
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("lcaid", app.lcaId);
			map.put("position", position);

			Reaper.processReaper(mContext,
					Reaper.REAPER_EVENT_CATEGORY_LEJINGPIN,
					Reaper.REAPER_EVENT_ACTION_LEJINGPIN_APPRECOMDOWN, map,
					Reaper.REAPER_NO_INT_VALUE);
		}
	}
	
	private void notifyDownloadApp(String pkg, String vcode, String appName,String iconurl,String versionName){
	// notify content manager to start download
		Log.d(TAG, "startDownload>>>>>>>>>pkg=" + pkg + ",vcode=" + vcode+" ; versionName : "+versionName+" ; iconurl : "+iconurl);
//		Intent intent = new Intent();
//		intent.setAction(HwConstant.ACTION_REQUEST_APP_DOWNLOAD);
//		intent.putExtra("package_name", pkg);
//		intent.putExtra("version_code", vcode);
//		intent.putExtra("app_name", appName);
//		intent.putExtra("from", "hw");// mCollect);
//		intent.putExtra("version_name", versionName);
//		intent.putExtra("app_iconurl", iconurl);
//		
//		//yangmao add new
//		intent.putExtra("wallpaper_url", "");
//		intent.putExtra("category", DownloadConstant.CATEGORY_RECOMMEND_APP);
//		
//		mContext.sendBroadcast(intent);

		AppDownloadUrl downurl = new AppDownloadUrl();
		downurl.setDownurl(DownloadConstant.TYPE_DOWNLOAD_ACTION);
		downurl.setVersionName(versionName);
		downurl.setPackage_name(pkg);
		downurl.setVersion_code(vcode);
		downurl.setApp_name(appName);
		downurl.setIconUrl(iconurl);
		downurl.setCallback(HwPushAppFragment.this);
		downurl.setCategory(DownloadConstant.CATEGORY_RECOMMEND_APP | DownloadConstant.CATEGORY_LENOVO_LCA);
		downurl.setMimeType(DownloadConstant.MIMETYPE_APK);
		sendMessage(DownloadHandler.getInstance(mContext),DownloadConstant.MSG_DOWN_LOAD_URL, downurl);
		

		// start download
		Intent intent1 = new Intent(HwConstant.ACTION_APP_START_DOWNLOAD);
		intent1.putExtra(EXTRA_PACKAGE_NAME, pkg);
		intent1.putExtra(EXTRA_VERSION_CODE, vcode);
		mContext.sendBroadcast(intent1);
	}
	
	class DownloadCallbackRunnable implements Runnable{

		private String pk;
		private String vc;
		
		public DownloadCallbackRunnable(String pkName,String vcode){
			pk = pkName;
			vc = vcode;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			DownloadHandler.getInstance(mContext).registerDownloadCallback(pk, vc, HwPushAppFragment.this);
		}
		
	}
	
	private void sendMessage(Handler handler, int what, Object obj){
		if (handler != null) {
			Message msg = new Message();
			msg.obj = obj;
			msg.what = what;
			handler.sendMessage(msg);
		}
	}

	private AppThread mTopAutoFilpThread;

	private void startTopAppAutoFilp() {
		Log.d(TAG, "startTopAppAutoFilp...");
		if (mTopAutoFilpThread == null) {
			mTopAutoFilpThread = new AppThread();
			mTopAutoFilpThread.start();
		}
	}

	private class AppThread extends Thread {
		private boolean isRun = true;
		private boolean isWait = false;

		@Override
		public void run() {
			super.run();
			while (isRun) {
				try {
					synchronized (this) {
						while (isWait) {
							wait();
						}
					}
					if (mTouch) {
						mTouch = false;
						Thread.sleep(5000);
					}
					if (mTopApps != null) {
						int topSize = mTopApps.size();
						if (topSize != 0) {
							if (mTopAppGallery != null) {
								int i = mTopAppGallery
										.getSelectedItemPosition();
								i++;
								int selectIndex = i % topSize;
								Message msg = mHandler.obtainMessage(
										TOP_GALLERY_CHANGE_BANNER, selectIndex,
										0);
								mHandler.sendMessage(msg);
							}
						}
					}
					Thread.sleep(3000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		public synchronized void threadPause() {
			isWait = true;
		}

		public synchronized void threadResume() {
			isWait = false;
			notify();
		}
	}

	private static class HeadAppViewHold {
		RelativeLayout appContainer;
		ImageView icon;
		View itemBg;
		TextView appName;
		RatingBar star;
		LinearLayout descLinear;
		TextView introduction;
		TextView feature;
		DownloadButton dowloadTextView;
		TextView downloadCountTextView;
	}

	private class TopAppListAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return mTopApps != null ? Integer.MAX_VALUE : 0;
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View contentView, ViewGroup parent) {
			int appSize = mTopApps.size();
			if (mTopApps == null || appSize == 0) {
				return contentView;
			}
			View view = contentView;
			HeadAppViewHold headHold;
			if (view == null) {
				view = LayoutInflater.from(mContext).inflate(R.layout.app_item,
						parent, false);
				headHold = new HeadAppViewHold();
				headHold.appContainer = (RelativeLayout) view
						.findViewById(R.id.app_container);
				headHold.icon = (ImageView) view.findViewById(R.id.app_icon);
				headHold.itemBg = view.findViewById(R.id.app_shader);
				headHold.appName = (TextView) view.findViewById(R.id.app_name);
				headHold.star = (RatingBar) view.findViewById(R.id.app_star);
				headHold.descLinear = (LinearLayout) view
						.findViewById(R.id.app_desc);
				headHold.introduction = (TextView) view
						.findViewById(R.id.app_introduction);
				headHold.feature = ((TextView) view
						.findViewById(R.id.app_feature));
				headHold.dowloadTextView = (DownloadButton) view
						.findViewById(R.id.app_download);
				headHold.downloadCountTextView = (TextView) view
						.findViewById(R.id.app_download_count);
				view.setTag(headHold);
			} else {
				headHold = (HeadAppViewHold) view.getTag();
			}
			int index = position % appSize;
			final RecommendLocalAppInfo topApp = mTopApps.get(index);
			if (topApp != null) {
				topApp.postion = "1," + index;
				headHold.icon.setImageDrawable(topApp.icon == null ? mDefIcon
						: topApp.icon);

				headHold.itemBg
						.setBackgroundDrawable(topApp.iconGradientDrawable);

				headHold.appName.setText(topApp.name);
				headHold.star.setRating(Float.valueOf(topApp.star));

				headHold.descLinear.setVisibility(View.VISIBLE);
				setAppIntroduction(topApp, headHold.introduction);

				if (topApp.collect.contains("10")) {
					headHold.feature.setText(R.string.app_feature_10);
				} else if (topApp.collect.contains("11")
						|| topApp.collect.contains("14")) {
					headHold.feature.setText(R.string.app_feature_11_14);
				} else if (topApp.collect.contains("12")
						|| topApp.collect.contains("13")) {
					headHold.feature.setText(R.string.app_feature_12_13);
				} else {
					headHold.feature.setText(R.string.app_feature_others);
				}
				final boolean installed = isInstalled(topApp.packageName);
				final DownloadButton downloadTV = headHold.dowloadTextView;
				if (topApp.status == null) {
					downloadTV.setVisibility(View.GONE);
				} else {
					downloadTV.setVisibility(View.VISIBLE);
					downloadTV.setTag(topApp.packageName + topApp.versionCode);
					if (topApp.status.equals(Status.UNDOWNLOAD)
							|| topApp.downloadDelete) {
						downloadTV.setText(R.string.app_detail_download);
					} else if (topApp.status.equals(Status.PAUSE)
							|| topApp.status.equals(Status.DOWNLOADING)) {
						downloadTV.setText(topApp.downLoadProgress + "%");
						if (mDownloadingApps != null
								&& !mDownloadingApps.contains(topApp)){
							mHandler.post(new DownloadCallbackRunnable(topApp.packageName,topApp.versionCode));
							mDownloadingApps.add(topApp);
						}
					} else if (topApp.status.equals(Status.UNINSTALL)) {
						downloadTV.setText(R.string.app_detail_install);
					}
					downloadTV.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							String text = null;
							if (v instanceof TextView) {
								text = (String) ((TextView) v).getText();
							}
							if (installed) {
								runApp(topApp.packageName);
							} else if (text.equals(mContext.getResources()
									.getString(R.string.app_detail_install))) {
								DownloadInfo i = LDownloadManager
										.getDefaultInstance(mContext)
										.getDownloadInfo(
												new DownloadInfo(
														topApp.packageName,
														topApp.versionCode));
								if (i != null) {
									LcaInstallerUtils.installApplication(
											mContext, i);
								}
							} else if (topApp.status.equals(Status.UNDOWNLOAD)) {
								newDoloadGameApp(topApp);
							} else if (topApp.status.equals(Status.DOWNLOADING)
									|| topApp.status.equals(Status.PAUSE)) {
								enterDownloadManagerActivity();
							}
						}
					});

				}
				if (installed) {
					downloadTV.setText(R.string.app_detail_run);
				}

				final ImageView app_icon = headHold.icon;
				final View shaderView = headHold.itemBg;
				if (topApp.icon == null && LejingpingSettingsValues.previewDownloadValue(mContext)) {// "1".equals(AppSettings.isLoadPicture)
											// &&
					ImageReader.loadImage(mContext, topApp.iconUrl,
							new ImageReader.OnIconLoadListener() {
								@Override
								public void onLoadComplete(Drawable img,
										int[] maxColor) {
									if (img != null) {
										topApp.icon = img;
										topApp.iconGradientDrawable = getItemBg(maxColor);
										app_icon.setImageDrawable(img);
										shaderView
												.setBackgroundDrawable(topApp.iconGradientDrawable);
									} else {
										Log.d(TAG, "get icon  error.");
									}
								}
							});
				}

				if (!TextUtils.isEmpty(topApp.downloadCount)) {
					headHold.downloadCountTextView
							.setText(topApp.downloadCount);
				}
			}

			return view;
		}

		private Drawable getItemBg(int[] rgb) {
			if (rgb != null) {
				Log.d(TAG, "icon color,[rgb]=" + Arrays.toString(rgb));
				Drawable bg = ColorUtil.getGradientDrawable(rgb);
				return bg;
			}
			return null;
		}

	}

	private void runApp(String pkName) {
		try {
			Intent intent = mContext.getPackageManager()
					.getLaunchIntentForPackage(pkName);
			if (intent != null) {
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	private boolean isInstalled(String pkName) {
		try {
			if (mContext.getPackageManager().getPackageInfo(pkName, 0) != null) {
				return true;
			}
		} catch (NameNotFoundException e) {
			// e.printStackTrace();
		}
		return false;
	}

	private void enterDownloadManagerActivity() {
		Intent intent = new Intent(mContext, DownloadExpandableActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(intent);
	}

	private class AppContentCache {
		private View baseView;
		private RelativeLayout appContainerA;
		private RelativeLayout appContainerB;
		private ImageView iconA;
		private ImageView iconB;
		private View itemBgA;
		private View itemBgB;
		private RatingBar starA;
		private RatingBar starB;
		private TextView appNameA;
		private TextView appNameB;
		private TextView dowloadTextViewA;
		private TextView dowloadTextViewB;
		private TextView downloadCountTextViewA;
		private TextView downloadCountTextViewB;

		public AppContentCache(View v) {
			this.baseView = v;
		}

		public RelativeLayout getAppContainerA() {
			if (appContainerA == null) {
				appContainerA = (RelativeLayout) baseView
						.findViewById(R.id.app_container_1);
			}
			return appContainerA;
		}

		public RelativeLayout getAppContainerB() {
			if (appContainerB == null) {
				appContainerB = (RelativeLayout) baseView
						.findViewById(R.id.app_container_2);
			}
			return appContainerB;
		}

		public ImageView getIconA() {
			if (iconA == null) {
				iconA = (ImageView) baseView.findViewById(R.id.app_icon_1);
			}
			return iconA;
		}

		public ImageView getIconB() {
			if (iconB == null) {
				iconB = (ImageView) baseView.findViewById(R.id.app_icon_2);
			}
			return iconB;
		}

		public View getItemBgA() {
			if (itemBgA == null) {
				itemBgA = baseView.findViewById(R.id.app_shader_1);
			}
			return itemBgA;
		}

		public View getItemBgB() {
			if (itemBgB == null) {
				itemBgB = baseView.findViewById(R.id.app_shader_2);
			}
			return itemBgB;
		}

		public RatingBar getStarA() {
			if (starA == null) {
				starA = (RatingBar) baseView.findViewById(R.id.app_star_1);
			}
			return starA;
		}

		public RatingBar getStarB() {
			if (starB == null) {
				starB = (RatingBar) baseView.findViewById(R.id.app_star_2);
			}
			return starB;
		}

		public TextView getAppNameA() {
			if (appNameA == null) {
				appNameA = (TextView) baseView.findViewById(R.id.app_name_1);
			}
			return appNameA;
		}

		public TextView getAppNameB() {
			if (appNameB == null) {
				appNameB = (TextView) baseView.findViewById(R.id.app_name_2);
			}
			return appNameB;
		}

		public TextView getDowloadTextViewA() {
			if (dowloadTextViewA == null) {
				dowloadTextViewA = (TextView) baseView
						.findViewById(R.id.app_download_1);
			}
			return dowloadTextViewA;
		}

		public TextView getDowloadTextViewB() {
			if (dowloadTextViewB == null) {
				dowloadTextViewB = (TextView) baseView
						.findViewById(R.id.app_download_2);
			}
			return dowloadTextViewB;
		}

		public TextView getDownloadCountTextViewA() {
			if (downloadCountTextViewA == null) {
				downloadCountTextViewA = (TextView) baseView
						.findViewById(R.id.app_download_count_1);
			}
			return downloadCountTextViewA;
		}

		public TextView getDownloadCountTextViewB() {
			if (downloadCountTextViewB == null) {
				downloadCountTextViewB = (TextView) baseView
						.findViewById(R.id.app_download_count_2);
			}
			return downloadCountTextViewB;
		}

	}

	private class AppListAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return mContentApps == null ? 0 : mContentApps.size() / 2;
		}

		@Override
		public Object getItem(int paramInt) {
			return mContentApps != null ? mContentApps.get(paramInt) : null;
		}

		@Override
		public long getItemId(int paramInt) {
			return paramInt;
		}

		@Override
		public View getView(int paramInt, View paramView,
				ViewGroup paramViewGroup) {
			if (mContentApps == null)
				return paramView;
			View view = paramView;
			AppContentCache contentCache;
			if (view == null) {
				view = LayoutInflater.from(mContext).inflate(
						R.layout.app_item_layout, paramViewGroup, false);
				contentCache = new AppContentCache(view);
				int width = getScreenWidth();
				LayoutParams params = new LayoutParams(width / 2,
						LayoutParams.WRAP_CONTENT);
				contentCache.getAppContainerA().setLayoutParams(params);
				contentCache.getAppContainerB().setLayoutParams(params);
				view.setTag(contentCache);
			} else {
				contentCache = (AppContentCache) view.getTag();
			}

			int appSize = mContentApps.size();
			int app_1_index = paramInt * 2;
			int app_2_index = paramInt * 2 + 1;
			if (app_1_index >= appSize || app_2_index >= appSize) {
				return view;
			}
			final RecommendLocalAppInfo app_1 = mContentApps.get(paramInt * 2);
			final RecommendLocalAppInfo app_2 = mContentApps
					.get(paramInt * 2 + 1);
			if (app_1 != null) {
				app_1.postion = (paramInt + 2) + ",1";
				contentCache.getIconA().setImageDrawable(
						app_1.icon == null ? mDefIcon : app_1.icon);
				contentCache.getIconA().setTag(app_1.iconUrl);
				contentCache.getItemBgA().setBackgroundDrawable(
						app_1.iconGradientDrawable);
				contentCache.getAppNameA().setText(app_1.name.trim());
				contentCache.getStarA().setRating(Float.valueOf(app_1.star));
				final boolean installed = isInstalled(app_1.packageName);
				final TextView downloadTextView = contentCache
						.getDowloadTextViewA();
				downloadTextView.setTag(app_1.packageName + app_1.versionCode);
				if (app_1.status == null) {
					downloadTextView.setVisibility(View.GONE);
				} else {
					downloadTextView.setVisibility(View.VISIBLE);
					if (app_1.status.equals(Status.UNDOWNLOAD)
							|| app_1.downloadDelete) {
						downloadTextView.setText(R.string.app_detail_download);
					} else if (app_1.status.equals(Status.PAUSE)
							|| app_1.status.equals(Status.DOWNLOADING)) {
						downloadTextView.setText(app_1.downLoadProgress + "%");
						if (mDownloadingApps != null
								&& !mDownloadingApps.contains(app_1)) {
							mHandler.post(new DownloadCallbackRunnable(app_1.packageName,app_1.versionCode));
							mDownloadingApps.add(app_1);
						}
					} else if (app_1.status.equals(Status.UNINSTALL)) {
						downloadTextView.setText(R.string.app_detail_install);
					}
					downloadTextView
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									Log.d(TAG,
											"app_1.status : "
													+ app_1.status.name());
									boolean installed_1 = isInstalled(app_1.packageName);
									String text = null;
									if (v instanceof TextView) {
										text = (String) ((TextView) v)
												.getText();
									}
									if (installed_1) {
										runApp(app_1.packageName);
									} else if (text
											.equals(mContext
													.getResources()
													.getString(
															R.string.app_detail_install))) {
										DownloadInfo i = LDownloadManager
												.getDefaultInstance(mContext)
												.getDownloadInfo(
														new DownloadInfo(
																app_1.packageName,
																app_1.versionCode));
										if (i != null) {
											LcaInstallerUtils
													.installApplication(
															mContext, i);
										}
									} else if (app_1.status
											.equals(Status.UNDOWNLOAD)) {
										newDoloadGameApp(app_1);
									} else if (app_1.status
											.equals(Status.DOWNLOADING)
											|| app_1.status
													.equals(Status.PAUSE)) {
										enterDownloadManagerActivity();
									}
								}
							});
				}
				if (installed) {
					downloadTextView.setText(R.string.app_detail_run);
				}
				contentCache.getAppContainerA().setOnClickListener(
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								showAppDetail(app_1);
							}
						});
				if (app_1.icon == null && LejingpingSettingsValues.previewDownloadValue(mContext)) {// "1".equals(AppSettings.isLoadPicture)
											// &&
					ImageReader.loadImage(mContext, app_1.iconUrl,
							new ImageReader.OnIconLoadListener() {
								@Override
								public void onLoadComplete(Drawable img,
										int[] maxColor) {
									ImageView app_icon_1 = (ImageView) mListView
											.findViewWithTag(app_1.iconUrl);
									if (app_icon_1 != null && img != null) {
										app_icon_1.setImageDrawable(img);
										app_1.icon = img;
									}
								}
							});
				}

				if (!TextUtils.isEmpty(app_1.downloadCount)) {
					contentCache.getDownloadCountTextViewA().setText(
							app_1.downloadCount);
				}
			}
			if (app_2 != null) {
				app_2.postion = (paramInt + 2) + ",2";
				contentCache.getIconB().setImageDrawable(
						app_2.icon == null ? mDefIcon : app_2.icon);
				contentCache.getIconB().setTag(app_2.iconUrl);
				contentCache.getItemBgB().setBackgroundDrawable(
						app_2.iconGradientDrawable);
				contentCache.getAppNameB().setText(app_2.name.trim());
				contentCache.getStarB().setRating(Float.valueOf(app_2.star));
				final boolean installed = isInstalled(app_2.packageName);
				final TextView downloadTextView = contentCache
						.getDowloadTextViewB();
				downloadTextView.setTag(app_2.packageName + app_2.versionCode);
				if (app_2.status == null) {
					downloadTextView.setVisibility(View.GONE);
				} else {
					downloadTextView.setVisibility(View.VISIBLE);
					if (app_2.status.equals(Status.UNDOWNLOAD)
							|| app_2.downloadDelete) {
						downloadTextView.setText(R.string.app_detail_download);
					} else if (app_2.status.equals(Status.PAUSE)
							|| app_2.status.equals(Status.DOWNLOADING)) {
						downloadTextView.setText(app_2.downLoadProgress + "%");
						if (mDownloadingApps != null
								&& !mDownloadingApps.contains(app_2)) {
							mHandler.post(new DownloadCallbackRunnable(app_2.packageName,app_2.versionCode));
							mDownloadingApps.add(app_2);
						}
					} else if (app_2.status.equals(Status.UNINSTALL)) {
						downloadTextView.setText(R.string.app_detail_install);
					}
					downloadTextView
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									Log.d(TAG,
											"app_2.status : "
													+ app_2.status.name());
									boolean installed_2 = isInstalled(app_2.packageName);
									String text = null;
									if (v instanceof TextView) {
										text = (String) ((TextView) v)
												.getText();
									}
									if (installed_2) {
										runApp(app_2.packageName);
									} else if (text
											.equals(mContext
													.getResources()
													.getString(
															R.string.app_detail_install))) {
										DownloadInfo i = LDownloadManager
												.getDefaultInstance(mContext)
												.getDownloadInfo(
														new DownloadInfo(
																app_2.packageName,
																app_2.versionCode));
										if (i != null) {
											LcaInstallerUtils
													.installApplication(
															mContext, i);
										}
									} else if (app_2.status
											.equals(Status.UNDOWNLOAD)) {
										newDoloadGameApp(app_2);
									} else if (app_2.status
											.equals(Status.DOWNLOADING)
											|| app_2.status
													.equals(Status.PAUSE)) {
										enterDownloadManagerActivity();
									}
								}
							});
				}
				if (installed) {
					downloadTextView.setText(R.string.app_detail_run);
				}
				contentCache.getAppContainerB().setOnClickListener(
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								showAppDetail(app_2);
							}
						});
				if (app_2.icon == null && LejingpingSettingsValues.previewDownloadValue(mContext) ) {// "1".equals(AppSettings.isLoadPicture)
											// &&
					ImageReader.loadImage(mContext, app_2.iconUrl,
							new ImageReader.OnIconLoadListener() {
								@Override
								public void onLoadComplete(Drawable img,
										int[] maxColor) {
									ImageView app_icon_2 = (ImageView) mListView
											.findViewWithTag(app_2.iconUrl);
									if (app_icon_2 != null && img != null) {
										app_2.icon = img;
										app_icon_2.setImageDrawable(img);
									}
								}
							});
				}
				if (!TextUtils.isEmpty(app_2.downloadCount)) {
					contentCache.getDownloadCountTextViewB().setText(
							app_2.downloadCount);
				}
			}

			return view;
		}
	}
	
	private TextView getDownloadTextViewByTag(String tag) {
		return (TextView) mListView.findViewWithTag(tag);
	}

	private class UiReceiver extends BroadcastReceiver {
		private Context mContext;

		public UiReceiver(Context c) {
			mContext = c;
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.d(TAG, "onReceive >> action : " + action);
			if (HwConstant.ACTION_DOWNLOAD_STATE.equals(action)) {
				String pkg = intent
						.getStringExtra(HwConstant.EXTRA_PACKAGENAME);
				String vcode = intent.getStringExtra(HwConstant.EXTRA_VERSION);
				Status status = Status.parseStatus(intent
						.getStringExtra(HwConstant.EXTRA_STATUS));// 下载的状态
				int progress = intent.getIntExtra(HwConstant.EXTRA_PROGRESS, 0);
				RecommendLocalAppInfo info = findAppFromDownloading(pkg, vcode);
				if (info != null &&  !info.downloadDelete) {
					if (status.equals(Status.UNINSTALL)) {
						info.downloadTextValue = context
								.getString(R.string.app_detail_install);
						info.downLoadProgress = progress;
						info.status = Status.UNINSTALL;
					} else if (status.equals(Status.UNDOWNLOAD)) {
						info.status = Status.UNDOWNLOAD;
						info.downloadTextValue = context
								.getString(R.string.app_detail_download);
					} else {
						info.downLoadProgress = progress;
						info.status = status;
						info.downloadTextValue = progress + "%";
					}
					TextView downloadView = getDownloadTextViewByTag(pkg + vcode);
					if (downloadView != null) {
						downloadView.setEnabled(true);
						downloadView.setText(info.downloadTextValue);
				}
				}
			} else if (DownloadConstant.ACTION_APK_FAILD_DOWNLOAD
					.equals(action)) {
				String pkg = intent
						.getStringExtra(DownloadConstant.EXTRA_PACKAGENAME);
				String vcode = intent
						.getStringExtra(DownloadConstant.EXTRA_VERSION);
				int categry = intent.getIntExtra(
						DownloadConstant.EXTRA_CATEGORY, -1);
				String app_name = intent
						.getStringExtra(DownloadConstant.EXTRA_APPNAME);
				int result = intent.getIntExtra(DownloadConstant.EXTRA_RESULT,
						DownloadConstant.FAILD_DOWNLOAD_OTHER_ERROR);
				if (categry != -1
						&& DownloadConstant.CATEGORY_RECOMMEND_APP == DownloadConstant.getDownloadCategory(categry)) {
					TextView downloadView = getDownloadTextViewByTag(pkg
							+ vcode);
					if (downloadView != null) {
						RecommendLocalAppInfo info = findAppFromDownloading(
								pkg, vcode);
						if (info != null) {
							info.status = Status.UNDOWNLOAD;
						}
						downloadView.setEnabled(true);
						downloadView.setText(R.string.detail_download);
						String errorString = "\n";
						if (result == DownloadConstant.FAILD_DOWNLOAD_NO_ENOUGHT_SPACE) {
							errorString = errorString
									+ mContext.getResources().getString(
											R.string.download_sdcard_notexists);
						} else if (result == DownloadConstant.FAILD_DOWNLOAD_NETWORK_ERROR) {
							errorString = errorString
									+ mContext.getResources().getString(
											R.string.download_net_error);
						}
						Toast.makeText(
								mContext,
								mContext.getResources()
										.getString(
												R.string.downloadcompleted_error_app_title,
												app_name)
										+ errorString, Toast.LENGTH_SHORT)
								.show();
					}
				}

			} else if (DownloadConstant.ACTION_DOWNLOAD_STATE_CHANGED
					.equals(action)) {
				// TODO
				String pkgName = intent.getStringExtra(DownloadConstant.EXTRA_PACKAGENAME);
				String vcode = intent.getStringExtra(DownloadConstant.EXTRA_VERSION);
				Status status = Status.parseStatus(intent.getStringExtra(HwConstant.EXTRA_STATUS));
				RecommendLocalAppInfo info = findAppFromDownloading(pkgName, vcode);
				if(info!=null && Status.DOWNLOADING.equals(status)){
					DownloadHandler.getInstance(mContext).registerDownloadCallback(pkgName, vcode, HwPushAppFragment.this);
				}
			} else if (DownloadExpandableActivity.ACTION_DOWNLOAD_DELETE
					.equals(action)) {
				String pkg = intent
						.getStringExtra(HwConstant.EXTRA_PACKAGENAME);
				String vcode = intent.getStringExtra(HwConstant.EXTRA_VERSION);
				TextView downloadView = getDownloadTextViewByTag(pkg + vcode);
				RecommendLocalAppInfo info = findAppFromDownloading(pkg, vcode);
				Log.d(TAG, "pkg : "+pkg+" ; vcode : "+vcode+" ; info : "+info+" ; downloadView : "+downloadView);
				if (downloadView != null) {
					downloadView.setEnabled(true);
					downloadView.setText(R.string.detail_download);
				}
				if (info != null) {
					info.downloadDelete = true;
					info.status = Status.UNDOWNLOAD;
				}

			} else if (HwConstant.ACTION_SETTING_ICON_UNUSE.equals(action)) {
				String set_icon_unuse = intent.getStringExtra("set_icon_unuse");
				Log.i(TAG, " onReceive --> set_upgrade: " + set_icon_unuse);
				if (set_icon_unuse != null) {// &&
												// !AppSettings.isLoadPicture.equals(set_icon_unuse)
					AppSettings.isLoadPicture = set_icon_unuse;
					if ("1".equals(set_icon_unuse)) {
						refreshPages();
					}
				}
			} else if (HwConstant.ACTION_APP_START_DOWNLOAD.equals(action)) {
				String pkg = intent
						.getStringExtra(PushAppDetialActivity.EXTRA_PACKAGE_NAME);
				String vcode = intent
						.getStringExtra(PushAppDetialActivity.EXTRA_VERSION_CODE);
				addAppToDownloadSet(pkg, vcode);
				RecommendLocalAppInfo info = findAppFromDownloading(pkg, vcode);
				TextView downloadView = getDownloadTextViewByTag(pkg + vcode);
				Log.d(TAG,
						"AppDetialActivity.ACTION_APP_START_DOWNLOAD >> info : "
								+ info);
				if (info != null) {
					info.downloadDelete = false;
					if (info.status.equals(Status.UNDOWNLOAD)) {
						if (downloadView != null) {
							downloadView.setEnabled(false);
							downloadView.setText(R.string.downlaod_preparation);
						}
					}
				}
			}
			if (HwConstant.ACTION_PACKAGE_ADDED.equals(action)) {
				String pkgName = intent.getStringExtra("pkname");
				int vcode = intent.getIntExtra("vcode", -1);
				TextView downloadView = getDownloadTextViewByTag(pkgName
						+ vcode);
				if (downloadView != null) {
					downloadView.setText(R.string.app_detail_run);
				}
				RecommendLocalAppInfo info = findAppFromDownloading(pkgName,
						String.valueOf(vcode));
				if (info != null) {
					mDownloadingApps.remove(info);
				}
			} else if (DownloadConstant.ACTION_APK_PARSE_OR_INSTALL_FAILED
					.equals(action)) {
				String pkg = intent
						.getStringExtra(DownloadConstant.EXTRA_PACKAGENAME);
				String vcode = intent
						.getStringExtra(DownloadConstant.EXTRA_VERSION);
				int categry = intent.getIntExtra(
						DownloadConstant.EXTRA_CATEGORY, -1);
				if (categry != -1
						&& DownloadConstant.CATEGORY_RECOMMEND_APP == DownloadConstant.getDownloadCategory(categry)) {
					RecommendLocalAppInfo info = findAppFromDownloading(pkg,
							vcode);
					if (info != null) {
						info.status = Status.UNDOWNLOAD;
						if (mDownloadingApps != null) {
							mDownloadingApps.remove(info);
						}
						TextView downloadView = getDownloadTextViewByTag(pkg
								+ vcode);
						if (downloadView != null) {
							downloadView.setText(R.string.app_detail_download);
						}
						DownloadInfo d = new DownloadInfo();
						d.setPackageName(pkg);
						d.setVersionCode(vcode);
						LDownloadManager.getDefaultInstance(context)
								.deleteTask(d);
					}
				}
			}else if(DownloadConstant.ACTION_DOWNLOAD_RESUME.equals(action)){
				String pkg = intent.getStringExtra(HwConstant.EXTRA_PACKAGENAME);
				String vcode = intent.getStringExtra(HwConstant.EXTRA_VERSION);
				RecommendLocalAppInfo info = findAppFromDownloading(pkg,vcode);
				if(info!=null){
					DownloadHandler.getInstance(mContext).registerDownloadCallback(pkg, vcode, HwPushAppFragment.this);
				}
			}
		}
	}

	private void setAppIntroduction(RecommendLocalAppInfo app,
			TextView introduction) {
		// Log.d(TAG, "setAppIntroduction >> app=" + app.name + ", row=" +
		// app.row + ", item=" + item);
		DownloadAppInfo appInfo = HWDBUtil.queryAppInfo(mContext,
				app.packageName, app.versionCode);
		if (introduction != null && appInfo != null) {
			introduction.setText(appInfo.getAppAbstract());
		}
	}

	private void showAppDetail(RecommendLocalAppInfo app) {
		Log.d(TAG, "showAppDetail >> app : " + app);
		Intent intent = new Intent(mContext, PushAppDetialActivity.class);
		intent.putExtra(HwConstant.EXTRA_PACKAGENAME, app.packageName);
		intent.putExtra(HwConstant.EXTRA_VERSION, app.versionCode);
		intent.putExtra(HwConstant.EXTRA_APPNAME, app.name);
		intent.putExtra(HwConstant.EXTRA_APPSTAR, app.star);
		intent.putExtra(HwConstant.EXTRA_APPICONURL, app.iconUrl);
		intent.putExtra(HwConstant.EXTRA_APPSIZE, app.getAppSize());
		intent.putExtra(HwConstant.EXTRA_LOCAL_ID, app.getLcaId());
		intent.putExtra(HwConstant.EXTRA_VERSION_NAME, app.getVersion());
		intent.putExtra(HwConstant.EXTRA_PUSH_POSITION, app.postion);
		intent.putExtra(HwConstant.EXTRA_CATEGORY,
				HwConstant.CATEGORY_APP_RECOMMEND);
		if (isInstalled(app.packageName)) {
			intent.putExtra(HwConstant.EXTRA_STATUS, Status.INSTALL.value());
		} else {
			intent.putExtra(HwConstant.EXTRA_STATUS, app.getStatus().value());
		}
		intent.putExtra(HwConstant.EXTRA_CATEGORY,
				HwConstant.CATEGORY_APP_RECOMMEND);

		startActivity(intent);
	}

	private void showFTip() {
		if (mFTip == null) {
			mFTip = new AlertDialog.Builder(mContext)//
					.setCancelable(false)//
					.setTitle(R.string.d_switch_t)//
					.setIcon(android.R.drawable.ic_dialog_alert)//
					.setMessage(R.string.dialog_spere_update)//
					.setPositiveButton(R.string.dialog_confirm,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									mIsShowFTip = false;
									dialog.dismiss();
									com.lenovo.lejingpin.hw.content.util.SharePreferenceUtil
											.getInstance(mContext)
											.setSpereUpdateState(2);
								}
							}).create();
		}
		mFTip.show();
	}

	private void refreshPages() {
		Log.d(TAG, "refreshPages ..");
		if (mApps != null && mApps.size() != 0) {
			((BaseAdapter) ((HeaderViewListAdapter) mListView.getAdapter())
					.getWrappedAdapter()).notifyDataSetChanged();

			if (mTopApps != null && mTopApps.size() != 0) {
				if (mTopAppGallery != null) {
					((BaseAdapter) mTopAppGallery.getAdapter())
							.notifyDataSetChanged();
				}
			}
		}
	}

	private RecommendLocalAppInfo findAppFromDownloading(String pkg,
			String vcode) {
		RecommendLocalAppInfo info = null;
		if (mDownloadingApps != null && !mDownloadingApps.isEmpty()) {
			for (RecommendLocalAppInfo app : mDownloadingApps) {
				if (app.packageName.equals(pkg)
						&& app.versionCode.equals(vcode)) {
					info = app;
					break;
				}
			}
		}
		return info;
	}

	private void addAppToDownloadSet(String pkg, String vcode) {
		if (mApps != null && !mApps.isEmpty()) {
			for (RecommendLocalAppInfo app : mApps) {
				if (app.packageName.equals(pkg)
						&& app.versionCode.equals(vcode)) {
					if (mDownloadingApps != null
							&& !mDownloadingApps.contains(app)) {
						mDownloadingApps.add(app);
					}
					break;
				}
			}
		}
	}

	private void notifyLauncher() {
		mContext.sendBroadcast(new Intent(ACTION_HAWAII_DESTROY));
	}

	private void requestLoadApps() {
		Log.d(TAG, "requestLoadApps >>> mApps=" + mApps);
		if (mApps == null || mApps.isEmpty()) {
			getLoaderManager().restartLoader(0, null, this);
		} else {
			onLoadFinished(null, mApps);
		}
		if (mEmptyView != null)
			mEmptyView.setVisibility(View.GONE);
		mLoading.setVisibility(View.VISIBLE);
	}

	@Override
	public void doCallback(DownloadInfo dInfo) {
		// TODO Auto-generated method stub
		if (dInfo != null) {
			String pkg = dInfo.getPackageName();
			String vcode = dInfo.getVersionCode();
			int progress = dInfo.getProgress();
			Log.d(TAG, "doCallback >> pkg : "+pkg+" ; vcode : "+vcode+"; progress : "+progress);
			Intent intent = new Intent();
			intent.setAction(HwConstant.ACTION_DOWNLOAD_STATE);
			intent.putExtra(HwConstant.EXTRA_PACKAGENAME, pkg);
			intent.putExtra(HwConstant.EXTRA_VERSION, vcode);
			intent.putExtra(HwConstant.EXTRA_PROGRESS, progress);
			if (dInfo.getDownloadStatus() == 192) {
				intent.putExtra(HwConstant.EXTRA_STATUS,
						Status.DOWNLOADING.value());
			} else if (dInfo.getDownloadStatus() == 193) {
				intent.putExtra(HwConstant.EXTRA_STATUS,
						Status.PAUSE.value());
			} else if (dInfo.getDownloadStatus() == 200) {
				intent.putExtra(HwConstant.EXTRA_STATUS,
						Status.UNINSTALL.value());
			} else {
				intent.putExtra(HwConstant.EXTRA_STATUS,
						Status.UNDOWNLOAD.value());
			}
			mContext.sendBroadcast(intent);
		}
	}
}
