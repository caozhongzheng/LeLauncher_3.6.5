package com.lenovo.lejingpin.magicdownloadremain;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;


import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;



import com.lenovo.launcher.R;

import com.lenovo.launcher2.customizer.Reaper;
import com.lenovo.launcher2.customizer.SettingsValue;

import com.lenovo.lejingpin.ams.NewSearchAppName;
import com.lenovo.lejingpin.hw.content.data.DownloadAppInfo;
import com.lenovo.lejingpin.hw.content.data.HwConstant;



import com.lenovo.lejingpin.hw.ui.RecommendLocalAppInfo;
import com.lenovo.lejingpin.hw.ui.RecommendLocalAppInfo.Status;
import com.lenovo.lejingpin.magicdownloadremain.AppDownloadControl.OnImgLoadListener;
import com.lenovo.lejingpin.settings.LejingpingSettingsValues;
import com.lenovo.lejingpin.share.download.DownloadConstant;
import com.lenovo.lejingpin.share.download.LDownloadManager;


public class HawaiiSearchAppInfoDialogFragment extends TabActivity{
	private static final String TAG = "zdx";
	private Context mContext;
	//private AppInfoReceiver mInfoReceiver;
	private DownloadStateReceiver mDownloadReceiver;
	//private PackageReceiver mPackageReceiver;

	private RecommendLocalAppInfo mListApp = null;
	
	private String HawaiiSearch_mPackageName;
	private String HawaiiSearch_mVersionCode;	
	private String HawaiiSearch_mAppName;
	private String HawaiiSearch_mIconaddr;
	private NewSearchAppName.Application Hawaii_search_app;
	
	TabHost mTabHost = null;
	
	private static final String ACTIVITY_DETAILS = AppDetails.class.getName();
	private static final String ACTIVITY_COMMENTS = AppComments.class.getName();
	
	private static final int MSG_GETINFO = 100;
	AppDownloadControl mAppDownloadController;
	//private static ConnectivityManager mConnectManager ;
	private  String mCategory = null;

	/*private Handler mInitHandler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
	        switch (msg.what) {
	        case MSG_GETINFO:
	          	getAppInfo();
	            break;
	        }
	    }
	};
	//------Get App Info------
	private class AppInfoReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if( mListApp != null )
				return;
			String action = intent.getAction();
			Log.d(TAG, "AppInfoDialogFragment.onReceive, action : " + action);
			if((HwConstant.ACTION_REQUEST_APP_INFO + "_COMPLETE").equals(action)) {
				String pkg = intent.getStringExtra("package_name");
				String vcode = intent.getStringExtra("version_code");
				boolean result = intent.getBooleanExtra("result", true);
				DownloadAppInfo appInfo = null;
				if( result){
				    if ( !mCategory.equals(HwConstant.CATEGORY_WALLPAPER_STRING)) {
					    appInfo = HWDBUtil.queryAppInfo(mContext, pkg, vcode);
				    } else {
					    appInfo = HWDBUtil.queryAppInfoBG(mContext, pkg, vcode);
				    }
				}
				if(appInfo != null) {
					mListApp = new RecommendLocalAppInfo(appInfo);
					updateHeaderView();
				}else{
					((Activity)mContext).finish();
				}
			}
		}
	}*/
	
	public HawaiiSearchAppInfoDialogFragment() {
		
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "HawaiiAppInfoDialogFragment.onCreate....");
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		setContentView( R.layout.magicdownload_app_detail_header);
		mContext = this;
		//mConnectManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		//LDownloadManager.getDefaultInstance(this);
		
		if(SettingsValue.getCurrentMachineType(this)==-1){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		//ActionBar actionBar = getActionBar();
		//actionBar.setDisplayHomeAsUpEnabled(true);
		//actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
		
		//------Get App Info------
		//IntentFilter filter = new IntentFilter();
		//filter.addAction(HwConstant.ACTION_REQUEST_APP_INFO + "_COMPLETE");
		//mInfoReceiver = new AppInfoReceiver();
		//registerReceiver(mInfoReceiver, filter);
		
		IntentFilter downloadfilter = new IntentFilter();
		downloadfilter.addAction(DownloadConstant.ACTION_DOWNLOAD_STATE_CHANGED);
		downloadfilter.addAction(DownloadConstant.ACTION_APK_FAILD_DOWNLOAD);
		downloadfilter.addAction(DownloadConstant.ACTION_DOWNLOAD_DELETE);
		downloadfilter.addAction(DownloadConstant.ACTION_DOWNLOAD_INSTALL_UNINSTALL);
		mDownloadReceiver = new DownloadStateReceiver(this);

		registerReceiver(mDownloadReceiver, downloadfilter);
		
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		mFilter.addDataScheme("package");
		registerReceiver(mDownloadReceiver, mFilter);
		

		/*mPackageReceiver = new PackageReceiver();
		IntentFilter packageIntentFilter = new IntentFilter();
		packageIntentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
		packageIntentFilter.addDataScheme("package");
		registerReceiver(mPackageReceiver, packageIntentFilter);*/
		
		Intent intent = getIntent();
		
		mCategory = intent.getStringExtra(HwConstant.EXTRA_CATEGORY);
		Hawaii_search_app =  (NewSearchAppName.Application)intent.getSerializableExtra("Hawaii_Search_app");
		HawaiiSearch_mPackageName = intent.getStringExtra("Hawaii_Search_packagename");
		HawaiiSearch_mVersionCode  = intent.getStringExtra("Hawaii_Search_versioncode");	
		HawaiiSearch_mAppName = intent.getStringExtra("Hawaii_Search_Appname");
		HawaiiSearch_mIconaddr = intent.getStringExtra("Hawaii_Search_Iconaddr");
		
		
		
		
		
		
        
		/*
		if( mListApp == null){
		    if (intent.getSerializableExtra(HwConstant.EXTRA_INFO) instanceof RecommendLocalAppInfo) {
		    	mListApp = (RecommendLocalAppInfo)intent.getSerializableExtra( HwConstant.EXTRA_INFO);
		    }
		}
		
		if( mListApp == null)
		    mListApp = AppDownloadControl.findApp(this, mPackageName, mVersionCode);

        Log.i(TAG," AppInfoDialogFragment, category:"+ mCategory +", package:"+ mPackageName);
        
        */
		
        //initTitle( mCategory );
		initTitle();
		
		setupTabs();
		
		updateHeaderView();
		
		
		//if( mListApp == null)
		//    handleGetMessage(MSG_GETINFO);		
	}
	
    /*private void handleGetMessage(int msgID ) {
    	mInitHandler.removeMessages(msgID);
    	mInitHandler.sendEmptyMessage(msgID);
    }
    private void getAppInfo(){
		if(!TextUtils.isEmpty(mPackageName) && !TextUtils.isEmpty(mVersionCode)) {
			DownloadAppInfo appInfo ;
			if(!mCategory.equals(HwConstant.CATEGORY_WALLPAPER_STRING)){
				appInfo = HWDBUtil.queryAppInfo(mContext, mPackageName, mVersionCode);
			}else{
				appInfo = HWDBUtil.queryAppInfoBG(mContext, mPackageName, mVersionCode);
			}
			if(appInfo != null) {
				mListApp = new RecommendLocalAppInfo(appInfo);
				updateHeaderView();
			} else {
				requestAppInfo();
			}
		}		
	}*/
	
	public void onDestroy(){
		super.onDestroy();	
		//if( mInfoReceiver != null)
		//    unregisterReceiver(mInfoReceiver);
		if(null != mDownloadReceiver)
			unregisterReceiver(mDownloadReceiver);
		//if(null != mPackageReceiver) 
		//	unregisterReceiver(mPackageReceiver);
	}

	private void  updateHeaderView() {
//		if( mListApp == null )
//			return;
		final TextView download = (TextView) findViewById(R.id.detail_download);
		download.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				
				//AppDownloadControl.prepareDownload(Hawaii_search_app, mContext);
				v.setEnabled(false);
				v.postDelayed(new Runnable() {
					public void run() {
						Log.i("yangmao", "fragment yangmao add the tag to slove fast click");
						v.setEnabled(true);
					}
				}, 1500);
				AppDownloadControl.prepareDownloadBySearch(Hawaii_search_app, mContext);
				
				String btn_text =(String)download.getText();
				Log.i("yangmao", "the HawaiiSearchAppInfo btn_text is:"+btn_text);
				if(btn_text.equals(getString(R.string.download_download))){					
					
					/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-1-5 S*/
					//Reaper.processReaper(mContext,Hawaii_search_app.getAppId(), getNetworkType(),HawaiiSearch_mPackageName, HawaiiSearch_mVersionCode);
					Reaper.processReaper( mContext, 
                     	   Reaper.REAPER_EVENT_CATEGORY_APPLIST, 
       					   Reaper.REAPER_EVENT_ACTION_APPLIST_SEARCH_DOWNLOAD,
       					   HawaiiSearch_mPackageName, 
       					   Reaper.REAPER_NO_INT_VALUE );
					/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-1-5 S*/
					
					
					//Reaper.reportSearch_Hawaii_DownlodEvent(mContext,Hawaii_search_app.getAppId(), getNetworkType(),HawaiiSearch_mPackageName, HawaiiSearch_mVersionCode);
				}
				
			}
		});
		//AppDownloadControl.drawDownloadState(mListApp, download);
		
		Status status = AppDownloadControl.queryDownloadStatus(mContext,Hawaii_search_app.getPackage_name(),Hawaii_search_app.getApp_versioncode());
		
		if(Hawaii_search_app.getApp_status().equals(getString(R.string.download_installed))){
			Log.i("yangmao_detail", "get run");
			status = Status.INSTALL;
		}
		
		AppDownloadControl.drawDownloadState_hawaiiSearch(status, download);

		((TextView) findViewById(R.id.detail_name)).setText(HawaiiSearch_mAppName);
		
		final ImageView iconView = (ImageView) findViewById(R.id.detail_icon);
		/*
		if(mListApp.getIcon() == null) {
			mListApp.setIcon( AppDownloadControl.getAppIcon(this, mListApp.getPackageName(), mListApp.getVersionCode()));
		}
		Drawable icon = mListApp.getIcon() ;
		if(icon!=null){
			iconView.setImageBitmap(((BitmapDrawable) icon).getBitmap());
		}
		*/
		String iconUrl = HawaiiSearch_mIconaddr;
		if(!TextUtils.isEmpty( iconUrl )) {
			final int mIconWidth= (int) this.getResources().getDimension(R.dimen.magicdownload_app_icon_size);
			final int mIconHeight = (int) this.getResources().getDimension(R.dimen.magicdownload_app_icon_size);
			iconView.setImageResource(R.drawable.push_app_icon_def);
			if(LejingpingSettingsValues.previewDownloadValue(mContext)) {
				AppDownloadControl.loadImg(iconUrl, new OnImgLoadListener() {
					public void onLoadComplete(final Drawable img) {
						//Log.i(TAG,"--------------------------------------load icon image:"+ img);
						if(img == null) return;
						runOnUiThread(new Runnable() {
							public void run() {
								img.setBounds(0, 0, mIconWidth, mIconHeight);
								iconView.setImageDrawable(img);
							}
						});
					}
				}, mContext);
			}
		}

		RatingBar star = (RatingBar) findViewById(R.id.detail_star);
		star.setRating(Float.parseFloat(Hawaii_search_app.getStar_level()));

		TextView ispay = (TextView) findViewById(R.id.detail_pay);
		if( Float.valueOf( Hawaii_search_app.getApp_price()) > 0) {
			ispay.setText(Hawaii_search_app.getApp_price() + getResources().getString(R.string.detail_price_yuan));
		}

		TextView size = (TextView) findViewById(R.id.detail_size);
	
//		if(mCategory.equals(HwConstant.CATEGORY_WALLPAPER_STRING)){
//			star.setVisibility(View.GONE);
//			TextView downloadtimes = (TextView) findViewById(R.id.detail_res);
//			downloadtimes.setVisibility(View.VISIBLE);
//			downloadtimes.setText(getResources().getString(R.string.detail_res)+mListApp.getCollect());
//		}
//        Log.i(TAG,"+++++++++++++++++22++++size:"+ mListApp.getAppSize());

		//String appSize = calculateSize( Hawaii_search_app.getApp_size());
		//size.setText(mContext.getString(R.string.detail_size) +appSize);
		size.setText(mContext.getString(R.string.detail_size) + Hawaii_search_app.getApp_size());
        //ZHANGLZ1
	}
	private String calculateSize(String size){
		if (size == null || size.length() == 0 ) {
			return null;
		}
		float sizeInt = Float.parseFloat(size);
		if(sizeInt > 1048576){
			return String.format("%.2f", sizeInt/1048576) + " M";
		} else if(sizeInt > 1024){
			//return Math.round( sizeInt/1024 ) + " K";
			return String.format("%.1f", sizeInt/1024)+" K";
		} else{
			return Math.round( sizeInt ) + " B";
		}
	}
	//------Get App Info------
	/*private void requestAppInfo() {
		Log.i(TAG,"AppInfoDialogFragment.requestAppInfo from network...");
		Intent intent = new Intent();
		if (!mCategory.equals( HwConstant.CATEGORY_WALLPAPER_STRING) ){
			intent.setAction(HwConstant.ACTION_REQUEST_APP_INFO);
		} else {
			intent.setAction(HwConstant.ACTION_TYPE_TEST_BG);
		}
		intent.setAction(HwConstant.ACTION_REQUEST_APP_INFO);
		intent.putExtra("package_name", mPackageName);
		intent.putExtra("version_code", mVersionCode);
		mContext.sendBroadcast(intent);
	}*/

	private void setupTabs() { 
    	ColorStateList colors = null;
		XmlResourceParser xpp = getResources().getXml(R.color.magicdownload_category_button_text_color);
		try {
				colors = ColorStateList.createFromXml(getResources(), xpp);
				//this.setTextColor(colors);
		    } catch (XmlPullParserException e) {
			} catch (IOException e) {
		}
		LinearLayout detailsLinearLayout = new LinearLayout(this);
		LinearLayout commentsLinearLayout = new LinearLayout(this);
		
		commentsLinearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
		detailsLinearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
		
		LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		//ll.gravity = 0x11;
//		ll.gravity = Gravity.CENTER | Gravity.CENTER_HORIZONTAL;
		ll.topMargin = 8;
		ll.bottomMargin = 8;
		
		//ll.leftMargin = getResources().getDimensionPixelSize(R.dimen.detail_comment_tab_margin);
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
		
		
		/*
		Intent appDetail = new Intent(this, AppDetails.class);
		appDetail.putExtra(HwConstant.EXTRA_PACKAGENAME, mPackageName);
		appDetail.putExtra(HwConstant.EXTRA_VERSION, mVersionCode);
		appDetail.putExtra(HwConstant.EXTRA_CATEGORY, mCategory);
		DownloadAppInfo app = new DownloadAppInfo(mListApp);
		appDetail.putExtra(HwConstant.EXTRA_INFO, app);
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.addTab(mTabHost.newTabSpec(ACTIVITY_DETAILS).setIndicator(detailsLinearLayout)
				.setContent(appDetail));
		//mTabHost.setCurrentTab(0);
		
		Intent comments = new Intent(this, AppComments.class);
		comments.putExtra(HwConstant.EXTRA_PACKAGENAME, mPackageName);
		comments.putExtra(HwConstant.EXTRA_VERSION, mVersionCode);
		comments.putExtra(HwConstant.EXTRA_CATEGORY, mCategory);
		mTabHost.addTab(mTabHost.newTabSpec(ACTIVITY_COMMENTS).setIndicator(commentsLinearLayout)
				.setContent(comments));
				
		*/
		//yangmao change the hawaii data
		
		

		
		Intent appDetail = new Intent(this, AppDetails.class);
		appDetail.putExtra(HwConstant.EXTRA_PACKAGENAME, HawaiiSearch_mPackageName);
		appDetail.putExtra(HwConstant.EXTRA_VERSION, HawaiiSearch_mVersionCode);
		appDetail.putExtra(HwConstant.EXTRA_CATEGORY, mCategory);
		DownloadAppInfo app = new DownloadAppInfo(Hawaii_search_app);
		appDetail.putExtra(HwConstant.EXTRA_INFO, app);
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.addTab(mTabHost.newTabSpec(ACTIVITY_DETAILS).setIndicator(detailsLinearLayout)
				.setContent(appDetail));
		//mTabHost.setCurrentTab(0);
		
		Intent comments = new Intent(this, AppComments.class);
		comments.putExtra(HwConstant.EXTRA_PACKAGENAME, HawaiiSearch_mPackageName);
		comments.putExtra(HwConstant.EXTRA_VERSION, HawaiiSearch_mVersionCode);
		comments.putExtra(HwConstant.EXTRA_CATEGORY, mCategory);
		mTabHost.addTab(mTabHost.newTabSpec(ACTIVITY_COMMENTS).setIndicator(commentsLinearLayout)
				.setContent(comments));
		
		
		
		
		//zhanglz1-s
//        if( mCategory.equals(HwConstant.CATEGORY_WALLPAPER_STRING) ){
//        	detailsLinearLayout.setPadding(getResources().getDimensionPixelSize(R.dimen.detail_detail_tab_margin_left), 
//        			detailsLinearLayout.getPaddingTop(),
//        			detailsLinearLayout.getPaddingRight(), 
//        			detailsLinearLayout.getPaddingBottom());
//			commentsLinearLayout.setVisibility(View.GONE);
//		}
		//zhanglz1-end
		final TabWidget tw = mTabHost.getTabWidget();
		tw.setBackgroundResource(R.drawable.magicdownload_arwen_group_bg);
		for (int i = 0; i < tw.getChildCount(); i++) {
			// set background image
			View v = tw.getChildAt(i);
			v.setBackgroundDrawable(getResources().getDrawable(R.drawable.magicdownload_category_btn_bg));
			if (mTabHost.getCurrentTab() == i) {
				v.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.magicdownload_tab_selected));
			} else {
				v.setBackgroundDrawable(null);
			}
		}
		
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				for (int i = 0; i < tw.getChildCount(); i++) {
					View v = tw.getChildAt(i);
					if (mTabHost.getCurrentTab() == i) {
						v.setBackgroundDrawable(getResources().getDrawable(R.drawable.magicdownload_tab_selected));
					} else {
						v.setBackgroundDrawable(null);
					}
				}
			}
		});
	}
	
	
	class DownloadStateReceiver extends BroadcastReceiver {
		private Context mContext;
		public DownloadStateReceiver(Context c) {
			mContext = c;
		}
		@Override
		public void onReceive(Context context, Intent intent) {
			
			Log.i("yangmao", "HawaiiSearchAppInfo---------DownloadStateReceiver-------------");
			String action = intent.getAction();
			
			Status s = null;
			//yangmao add for move delete change
		    if(DownloadConstant.ACTION_DOWNLOAD_STATE_CHANGED.equals(action)) {
				s = Status.parseStatus(intent.getStringExtra(HwConstant.EXTRA_STATUS));
			
				
				
				
//				mListApp.downLoadProgress = intent.getIntExtra(HwConstant.EXTRA_PROGRESS, 0);
//				mListApp.setLocalPath(intent.getStringExtra(HwConstant.EXTRA_INSTALLPATH));
			} else  if( ( DownloadConstant.ACTION_APK_FAILD_DOWNLOAD.equals(action)) ||
					   ( DownloadConstant.ACTION_DOWNLOAD_DELETE.equals(action)) ){
				s =  Status.UNDOWNLOAD ;
				
				
			} else if ( DownloadConstant.ACTION_DOWNLOAD_INSTALL_UNINSTALL.equals(action)){
				boolean bInstalled = intent.getBooleanExtra(HwConstant.EXTRA_RESULT, true);
				s = bInstalled? Status.INSTALL: Status.UNINSTALL;
				
				
			} else if(action.equals(Intent.ACTION_PACKAGE_REMOVED)){
				 s = Status.UNDOWNLOAD;
			}
		    
		    
		    
		    
		    //mListApp.setStatus( s );
			TextView downloadView = (TextView) findViewById(R.id.detail_download);
			
			if( downloadView == null) return;
			
			
			
//		    AppDownloadControl.drawDownloadState(mListApp, downloadView);
		
			switch (s) {

			case DOWNLOADING:
				downloadView.setText(R.string.download_pause);
				break;
			case UNINSTALL:
				downloadView.setText(R.string.app_detail_install);
				break;
			case PAUSE:
				downloadView.setText(R.string.download_resume);
				break;
			case UNDOWNLOAD:
				downloadView.setText(R.string.download_download);
				break;
			case INSTALL:
				downloadView.setText(R.string.download_installed);
				break;
			default:
				downloadView.setText(R.string.download_download);
				break;
			}
			
		    
		    
		}
	}
	
	
	
	/*class PackageReceiver extends BroadcastReceiver{
		public static final String TAG = "zdx";
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.i(TAG,"AppInfoDialogFragment.PackageReceiver.onReceive(), action:"+ action );
			if ( Intent.ACTION_PACKAGE_ADDED.equals(action) ) {
				String pkgName = intent.getData().getSchemeSpecificPart();
				if( pkgName.equals(mPackageName) ){
					((Activity)mContext).finish();
				}
			}
		}
	}*/
	
	
	public void initTitle(){	
		int strTitleId = R.string.category_hwaii_search_app;
		setTitle( strTitleId );
	}
	
	 /*public boolean onOptionsItemSelected(MenuItem item) {
		  switch (item.getItemId()) {
		  case android.R.id.home:
			  finish();
			  break;
		  }
		  return super.onOptionsItemSelected(item);
	}*/
	 
	 
	/*private static String getNetworkType() {
		NetworkInfo info = mConnectManager.getActiveNetworkInfo();

		String nettype = "";
		if (info != null) {
			nettype = info.getTypeName();
		}
		return nettype;
	}*/
	 
	 
	 
}
