package com.lenovo.lejingpin.hw.ui;

import java.io.IOException;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParserException;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.customizer.Reaper;
import com.lenovo.lejingpin.LEJPConstant;
import com.lenovo.lejingpin.hw.content.data.HwConstant;
import com.lenovo.lejingpin.hw.lcapackageinstaller.utils.LcaInstallerUtils;
import com.lenovo.lejingpin.hw.ui.RecommendLocalAppInfo.Status;
import com.lenovo.lejingpin.magicdownloadremain.AppComments;
import com.lenovo.lejingpin.magicdownloadremain.AppDetails;
import com.lenovo.lejingpin.settings.LejingpingSettingsValues;
import com.lenovo.lejingpin.share.download.AppDownloadUrl;
import com.lenovo.lejingpin.share.download.DownloadConstant;
import com.lenovo.lejingpin.share.download.DownloadHandler;
import com.lenovo.lejingpin.share.download.DownloadInfo;
import com.lenovo.lejingpin.share.download.Downloads;
import com.lenovo.lejingpin.share.download.LDownloadManager;
import com.lenovo.lejingpin.share.util.Utilities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TabHost.OnTabChangeListener;

public class PushAppDetialActivity  extends TabActivity {
	private String TAG = "PushAppDetialActivity";

	private ImageView mDetailIconView;
	private TextView mDetailNameView;
	private TextView mDetailSizeView;
	private RatingBar mDetailStarView;
	private TextView mDetailResView;
	private TextView mDetailPayView;
	private TextView mDetailDownlaodView;
	private TabHost mDetailTabHost;
	
	private String mDetailPackageName;
	private String mDetailVersionCode;
	private String mDetailVersionName;
	private String mDetailAppName;
	private String mDetailAppSize;
	private String mDetailAppStar;
	private String mDetailIconUrl;
	private Status mDetailStatus;
	private String mDetailCategory;
	
	private String mDetailLocalId;
	private String mDetailPositon;
	
	private PushAppDetailReceiver mDetailReceiver;
	
	
	private static final String ACTIVITY_DETAILS = AppDetails.class.getName();
	private static final String ACTIVITY_COMMENTS = AppComments.class.getName();
	
	public static final String EXTRA_PACKAGE_NAME = "com.lenovo.leos.hw.PACKAGE_NAME";
	public static final String EXTRA_VERSION_CODE = "com.lenovo.leos.hw.VERSION_CODE";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
	    final ActionBar bar = getActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        int titleId = getResources().getSystem().getIdentifier("action_bar_title", "id", "android");  
        TextView titleView = (TextView) findViewById(titleId);  
        titleView.setTextColor(Color.WHITE); 
        
		initDetialValue();
		initLayout();
		mDetailReceiver = new PushAppDetailReceiver(this);
		mDetailReceiver.registerBroadReceiver();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mDetailReceiver!=null){
			this.unregisterReceiver(mDetailReceiver);
		}
	}
	
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId() == android.R.id.home){
			finish();
		}
		return true;
	}

	private void initDetialValue(){
		Intent intent = getIntent();
		mDetailPackageName = intent.getStringExtra(HwConstant.EXTRA_PACKAGENAME); 
		mDetailVersionCode  = intent.getStringExtra(HwConstant.EXTRA_VERSION);
		mDetailAppName = intent.getStringExtra(HwConstant.EXTRA_APPNAME);
		mDetailAppSize = intent.getStringExtra(HwConstant.EXTRA_APPSIZE);
		mDetailAppStar  = intent.getStringExtra(HwConstant.EXTRA_APPSTAR);
		mDetailIconUrl = intent.getStringExtra(HwConstant.EXTRA_APPICONURL);
		String status = intent.getStringExtra(HwConstant.EXTRA_STATUS);
		mDetailStatus = Status.parseStatus(status);
		mDetailVersionName = intent.getStringExtra(HwConstant.EXTRA_VERSION_NAME);
		
		mDetailCategory = intent.getStringExtra(HwConstant.EXTRA_CATEGORY);
		
		mDetailLocalId = intent.getStringExtra(HwConstant.EXTRA_LOCAL_ID);
		mDetailPositon = intent.getStringExtra(HwConstant.EXTRA_PUSH_POSITION);
	}
	
	private void initLayout(){
		this.setContentView(R.layout.push_app_detial_layout);
		mDetailIconView = (ImageView) this.findViewById(R.id.detail_icon);
		mDetailNameView = (TextView) this.findViewById(R.id.detail_name);
		mDetailSizeView = (TextView) this.findViewById(R.id.detail_size);
		mDetailStarView = (RatingBar) this.findViewById(R.id.detail_star);
		mDetailResView = (TextView) this.findViewById(R.id.detail_res);
		mDetailDownlaodView = (TextView) this.findViewById(R.id.detail_download);
		mDetailDownlaodView.setEnabled(true);
		if(mDetailStatus.equals(Status.DOWNLOADING)){
			mDetailDownlaodView.setText(R.string.download_pause);
		}else if(mDetailStatus.equals(Status.PAUSE)){
			mDetailDownlaodView.setText(R.string.download_resume);
		}else if(mDetailStatus.equals(Status.UNINSTALL)){
			mDetailDownlaodView.setText(R.string.app_detail_install);
		}else if(mDetailStatus.equals(Status.INSTALL)){
			mDetailDownlaodView.setText(R.string.app_detail_run);
		}
		
		if(LejingpingSettingsValues.previewDownloadValue(this)){
			
			ImageReader.loadImage(this, mDetailIconUrl, new ImageReader.OnImageLoadListener(){
				@Override
				public void onLoadComplete(Drawable img) {
					// TODO Auto-generated method stub
					mDetailIconView.setImageDrawable(img);
				}
				
			});
		}
		
		mDetailDownlaodView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View paramView) {
				if(mDetailStatus.equals(Status.DOWNLOADING)){
					DownloadInfo info = new DownloadInfo();
					info.setPackageName(mDetailPackageName);
					info.setVersionCode(mDetailVersionCode);
					LDownloadManager.getDefaultInstance(PushAppDetialActivity.this).pauseTask(info);
				}else if(mDetailStatus.equals(Status.UNDOWNLOAD)
						|| mDetailStatus.equals(Status.PAUSE)){
					newDoloadOrResumeGameApp(mDetailStatus);
				}else if(mDetailStatus.equals(Status.UNINSTALL)){
					installApp();
				}else if(mDetailStatus.equals(Status.INSTALL)){
					runApp();
				}
			}
		});
		
		mDetailNameView.setText(mDetailAppName);
		mDetailSizeView.setText(getString(R.string.detail_size) +String.format("%1$s", calculateSize(mDetailAppSize)));
		mDetailStarView.setRating(Float.valueOf(mDetailAppStar));
		
		setupTabs();
	}
	
	private void newDoloadOrResumeGameApp(final Status status){
		if(LejingpingSettingsValues.wlanDownloadValue(this) && Util.isMobileNetWork(this)){
			LejingpingSettingsValues.popupWlanDownloadDialog(this);
		}else{
			startDownload(status);
		}
	}
	private void startDownload(Status status){
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(PushAppDetialActivity.this, R.string.download_sd_error_1,
					Toast.LENGTH_SHORT).show();
			return;
		}else{
			boolean isEnough = Util.getInstance().spaceIsEnough(Long.parseLong(mDetailAppSize));
			Log.d(TAG,"downloadApp >> isEnough : "+isEnough);
			if(!isEnough){
				Toast.makeText(PushAppDetialActivity.this, R.string.download_sdcard_notexists,
						Toast.LENGTH_SHORT).show();
				return;
			}
		}
		String network = Util.getConnectType(this);
		
		if("other".equals(network)){
			Toast.makeText(this, R.string.error_network_state, Toast.LENGTH_SHORT).show();
		} else {
			if(status.equals(Status.PAUSE)){
				
				DownloadInfo info = new DownloadInfo();
				info.setPackageName(mDetailPackageName);
				info.setVersionCode(mDetailVersionCode);
				LDownloadManager.getDefaultInstance(PushAppDetialActivity.this).resumeTask(info);
				
			}else if(status.equals(Status.UNDOWNLOAD)){
				
				notifyDownloadApp();
				// yangmao add start 0107
				if(!TextUtils.isEmpty(mDetailLocalId) && !TextUtils.isEmpty(mDetailPositon)){
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("lcaid", mDetailLocalId);
					map.put("position", mDetailPositon);
					
					Reaper.processReaper(this,
							Reaper.REAPER_EVENT_CATEGORY_LEJINGPIN,
							Reaper.REAPER_EVENT_ACTION_LEJINGPIN_APPRECOMDOWN, map,
							Reaper.REAPER_NO_INT_VALUE);
				}
			}
		}
	}
	
	private void notifyDownloadApp() {
		AppDownloadUrl downurl = new AppDownloadUrl();
		downurl.setDownurl(DownloadConstant.TYPE_DOWNLOAD_ACTION);
		downurl.setVersionName(mDetailVersionName);
		downurl.setPackage_name(mDetailPackageName);
		downurl.setVersion_code(mDetailVersionCode);
		downurl.setApp_name(mDetailAppName);
		downurl.setIconUrl(mDetailIconUrl);
		downurl.setCategory(DownloadConstant.CATEGORY_RECOMMEND_APP | DownloadConstant.CATEGORY_LENOVO_LCA);
		downurl.setMimeType(DownloadConstant.MIMETYPE_APK);
		sendMessage(DownloadHandler.getInstance(this),DownloadConstant.MSG_DOWN_LOAD_URL, downurl);
		
		

		// start download
		Intent intent1 = new Intent(HwConstant.ACTION_APP_START_DOWNLOAD);
		intent1.putExtra(EXTRA_PACKAGE_NAME, mDetailPackageName);
		intent1.putExtra(EXTRA_VERSION_CODE, mDetailVersionCode);
		sendBroadcast(intent1);

		Log.d(TAG, "startDownload>>>>>>>>>pkg=" + mDetailPackageName + ",vcode=" + mDetailVersionCode);
	}
	
	private void sendMessage(Handler handler, int what, Object obj){
		if (handler != null) {
			Message msg = new Message();
			msg.obj = obj;
			msg.what = what;
			handler.sendMessage(msg);
		}
	}
	
	private void installApp(){
		DownloadInfo downloadInfo = LDownloadManager.getDefaultInstance(this).getDownloadInfo(new DownloadInfo(mDetailPackageName, mDetailVersionCode));
		LcaInstallerUtils.installApplication(this, downloadInfo);
	}
	
	private void runApp() {
		try {
			Intent intent = getPackageManager().getLaunchIntentForPackage(mDetailPackageName);
			if(intent != null) {
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			} else {
				Toast.makeText(this, R.string.app_detail_run_error, Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			Toast.makeText(this, R.string.app_detail_run_error, Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
	
	private String calculateSize(String size) {
		if(size == null || size.length() == 0) {
			return null;
		}
		float sizeInt = Float.parseFloat(size);
		if(sizeInt > 1048576 || sizeInt == 1048576) {
			return String.format("%.2f", sizeInt / 1048576) + "M";
		} else if(sizeInt > 1024 || sizeInt == 1024) {
			return Math.round(sizeInt / 1024) + "K";
		} else {
			return Math.round(sizeInt) + "B";
		}
	}
	
	private void setupTabs(){
		ColorStateList colors = null;
		XmlResourceParser xpp = getResources().getXml(R.color.push_app_detial_tab_title_selector);
		try {
				colors = ColorStateList.createFromXml(getResources(), xpp);
		    } catch (XmlPullParserException e) {
			} catch (IOException e) {
		}
		LinearLayout detailsLinearLayout = new LinearLayout(this);
		LinearLayout commentsLinearLayout = new LinearLayout(this);
		
		LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		ll.gravity = 0x11;
		ll.topMargin = 8;
		ll.bottomMargin = 8;
		ll.leftMargin = getResources().getDimensionPixelSize(R.dimen.detail_comment_tab_margin);
		TextView detailsTitle = new TextView(this);
		detailsTitle.setText(getResources().getString(R.string.detail_tab_details));
		detailsTitle.setTextSize(18);
		detailsTitle.setTextColor(colors);
		
		TextView commentsTitle = new TextView(this);
		commentsTitle.setText(getResources().getString(R.string.detail_tab_comment));
		commentsTitle.setTextSize(18);
		commentsTitle.setTextColor(colors);
		
		detailsLinearLayout.addView(detailsTitle, ll);
		commentsLinearLayout.addView(commentsTitle, ll);
		
		
		Intent appDetail = new Intent(this, AppDetails.class);
		appDetail.putExtra(HwConstant.EXTRA_PACKAGENAME, mDetailPackageName);
		appDetail.putExtra(HwConstant.EXTRA_VERSION, mDetailVersionCode);
		appDetail.putExtra(HwConstant.EXTRA_CATEGORY, mDetailCategory);
		mDetailTabHost = (TabHost) findViewById(android.R.id.tabhost);
		Log.d(TAG, "mDetailTabHost : "+mDetailTabHost);
		mDetailTabHost.addTab(mDetailTabHost.newTabSpec(ACTIVITY_DETAILS).setIndicator(detailsLinearLayout)
				.setContent(appDetail));
		Intent comments = new Intent(this, AppComments.class);
		comments.putExtra(HwConstant.EXTRA_PACKAGENAME, mDetailPackageName);
		comments.putExtra(HwConstant.EXTRA_VERSION, mDetailVersionCode);
		comments.putExtra(HwConstant.EXTRA_CATEGORY, mDetailCategory);//HwConstant.CATEGORY_APP_RECOMMEND
		mDetailTabHost.addTab(mDetailTabHost.newTabSpec(ACTIVITY_COMMENTS).setIndicator(commentsLinearLayout)
				.setContent(comments));
		
		final TabWidget tw = mDetailTabHost.getTabWidget();
		for (int i = 0; i < tw.getChildCount(); i++){
			View v = tw.getChildAt(i);
			if (mDetailTabHost.getCurrentTab() == i) {
				v.setBackgroundResource(R.drawable.app_detial_selected_tab);
			} else {
				v.setBackgroundResource(R.drawable.app_detial_unselected_tab);
			}
		}
		
		mDetailTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				for (int i = 0; i < tw.getChildCount(); i++) {
					View v = tw.getChildAt(i);
					if (mDetailTabHost.getCurrentTab() == i) {
						v.setBackgroundResource(R.drawable.app_detial_selected_tab);
					} else {
						v.setBackgroundResource(R.drawable.app_detial_unselected_tab);
					}
				}
			}
		});
	}
	
	private class PushAppDetailReceiver extends BroadcastReceiver{
		private Context mContext;
		
		public PushAppDetailReceiver(Context context){
			mContext = context;
		}
		
		public void registerBroadReceiver(){
			IntentFilter downloadfilter = new IntentFilter();
			downloadfilter.addAction(DownloadConstant.ACTION_DOWNLOAD_STATE_CHANGED);
			downloadfilter.addAction(DownloadConstant.ACTION_APK_FAILD_DOWNLOAD);
			downloadfilter.addAction(DownloadConstant.ACTION_DOWNLOAD_DELETE);
			downloadfilter.addAction(DownloadConstant.ACTION_DOWNLOAD_INSTALL_UNINSTALL);
			downloadfilter.addAction(HwConstant.ACTION_PACKAGE_ADDED);
			mContext.registerReceiver(this, downloadfilter);
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent!=null){
				String action = intent.getAction();
				Log.d(TAG,"onReceive >> action : "+action);
				if(!TextUtils.isEmpty(action)){
					if(action.equals(DownloadConstant.ACTION_DOWNLOAD_STATE_CHANGED)){
						String pkgName = intent.getStringExtra(DownloadConstant.EXTRA_PACKAGENAME);
						String vcode = intent.getStringExtra(DownloadConstant.EXTRA_VERSION);
						if(!TextUtils.isEmpty(pkgName) && pkgName.equals(mDetailPackageName)
								&& !TextUtils.isEmpty(vcode) && vcode.equals(mDetailVersionCode)){
							mDetailStatus = Status.parseStatus(intent.getStringExtra(HwConstant.EXTRA_STATUS));
						}
					}else if(action.equals(DownloadConstant.ACTION_APK_FAILD_DOWNLOAD)
							|| action.equals(DownloadConstant.ACTION_DOWNLOAD_DELETE)){
						String pkgName = intent.getStringExtra(DownloadConstant.EXTRA_PACKAGENAME);
						String vcode = intent.getStringExtra(DownloadConstant.EXTRA_VERSION);
						if(!TextUtils.isEmpty(pkgName) && pkgName.equals(mDetailPackageName)
								&& !TextUtils.isEmpty(vcode) && vcode.equals(mDetailVersionCode)){
							mDetailStatus =  Status.UNDOWNLOAD ;
						}
					}else if(action.equals(DownloadConstant.ACTION_DOWNLOAD_INSTALL_UNINSTALL)){
						
						String pkgName = intent.getStringExtra(DownloadConstant.EXTRA_PACKAGENAME);
						String vcode = intent.getStringExtra(DownloadConstant.EXTRA_VERSION);
						if(!TextUtils.isEmpty(pkgName) && pkgName.equals(mDetailPackageName)
								&& !TextUtils.isEmpty(vcode) && vcode.equals(mDetailVersionCode)){
							boolean bInstalled = intent.getBooleanExtra(HwConstant.EXTRA_RESULT, true);
							mDetailStatus = bInstalled? Status.INSTALL: Status.UNINSTALL;
						}
					}else if(HwConstant.ACTION_PACKAGE_ADDED.equals(action)){
						String pkgName = intent.getStringExtra("pkname");
						int vcode = intent.getIntExtra("vcode", -1);
						if(!TextUtils.isEmpty(pkgName) && pkgName.equals(mDetailPackageName)
								&& Integer.parseInt(mDetailVersionCode)== vcode){
							mDetailStatus = Status.INSTALL;
						}
					}
					Log.d(TAG,"onReceive >> mDetailStatus : "+mDetailStatus.name());
					switch (mDetailStatus) {
						case DOWNLOADING:
							mDetailDownlaodView.setText(R.string.download_pause);
							break;
						case UNINSTALL:
							mDetailDownlaodView.setText(R.string.app_detail_install);
							break;
						case PAUSE:
							mDetailDownlaodView.setText(R.string.download_resume);
							break;
						case UNDOWNLOAD:
							mDetailDownlaodView.setText(R.string.download_download);
							break;
						case INSTALL:
							mDetailDownlaodView.setText(R.string.app_detail_run);
							break;
						default:
							mDetailDownlaodView.setText(R.string.download_download);
							break;
					}
				}
			}
		}
		
	}
}
