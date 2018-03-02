package com.lenovo.lejingpin.magicdownloadremain;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;


import com.lenovo.launcher.R;
import com.lenovo.launcher2.customizer.SettingsValue;

import com.lenovo.lejingpin.hw.content.data.DownloadAppInfo;
import com.lenovo.lejingpin.hw.content.data.HwConstant;
import com.lenovo.lejingpin.hw.content.db.HWDBUtil;
import com.lenovo.lejingpin.magicdownloadremain.AppDownloadControl.OnImgLoadListener;
import com.lenovo.lejingpin.settings.LejingpingSettings;
import com.lenovo.lejingpin.settings.LejingpingSettingsValues;




import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class AppDetails extends Activity {
	private static final String TAG = "zdx";
	
	private GandalfFlipper mGandalfFlipper;
	private Context mContext = null;
	private static final int MSG_GETINFO = 100;
	private AppInfoReceiver mInfoReceiver;
	
	private String mPackageName = null;
	private String mVersionCode = null;
	private String mCategory = null;
	
	private ScrollView mScrollView;
	private LinearLayout loadingV;
	private ProgressBar progressBarV;
	private TextView loading_textV;
	private Button loading_refresh;
	
	private static final int MSG_GETLIST = 100;
	private DownloadAppInfo mApp;
	Handler mInitHandler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
	        switch (msg.what) {
	        case MSG_GETINFO:
	        	getAppInfo();
	            break;
	        default:
	        	break;
	        }
	    }
	};
	
	//------Get App Info------
	private class AppInfoReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			
			if((HwConstant.ACTION_REQUEST_APP_INFO + "_COMPLETE").equals(action)) {
				String pkg = intent.getStringExtra("package_name");
				String vcode = intent.getStringExtra("version_code");
				Boolean result = intent.getBooleanExtra("result", true);
				Log.d(TAG, "AppDetails.onReceive, action : " + action+", result:"+ result);
				DownloadAppInfo appInfo = null;
				if( result ){
					if (!mCategory.equals(HwConstant.CATEGORY_WALLPAPER_STRING)) {
						Log.i("yangmao_move", "getappInfo");
						appInfo = HWDBUtil.queryAppInfo(mContext, pkg, vcode);
						Log.i("yangmao_move", "appInfo is:"+appInfo);
					} else {
						//appInfo = HWDBUtil.queryAppInfoBG(mContext, pkg, vcode);

					}
					if( appInfo != null ){
						Log.i("yangmao_move", "updateDetailView");
						updateDetailView(appInfo);
					}	
					else 
						showEmptyView();
				}else
					showErrorView();
				
			}else if((HwConstant.ACTION_HAWAII_SEARCH_APP_INFO + "_COMPLETE_NEW").equals(action)) {
				//String pkg = intent.getStringExtra("package_name");
				//String vcode = intent.getStringExtra("version_code");
				Boolean result = intent.getBooleanExtra("result", true);
				Log.d(TAG, "AppDetails.onReceive, action : " + action+", result:"+ result);
				DownloadAppInfo appInfo =(DownloadAppInfo)intent.getSerializableExtra("hawaii_search_appinfo");
				if( result ){
					
					if( appInfo != null )
						updateDetailView(appInfo);
					else 
						showEmptyView();
				}else
					showErrorView();
				
			}
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		if(SettingsValue.getCurrentMachineType(this)==-1){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		Intent intent = getIntent();
		mPackageName = intent.getStringExtra(HwConstant.EXTRA_PACKAGENAME);
		mVersionCode = intent.getStringExtra(HwConstant.EXTRA_VERSION);
	    mCategory = intent.getStringExtra(HwConstant.EXTRA_CATEGORY);
		if (this.getIntent().getSerializableExtra(
				HwConstant.EXTRA_INFO) instanceof DownloadAppInfo) {
			mApp = (DownloadAppInfo) this.getIntent()
					.getSerializableExtra( HwConstant.EXTRA_INFO);
		}
		
		Log.i(TAG,"AppDetails.onCreate(), pkg:"+ mPackageName+", versioncode:"+ mVersionCode);
		setupView();
		handleGetMessage(MSG_GETINFO);
	}
	
	private void setupView() {
		setContentView(R.layout.magicdownload_app_detail_description);
		loadingV = (LinearLayout) findViewById(R.id.loading);
		progressBarV = (ProgressBar) findViewById(R.id.progressing);
		loading_textV = (TextView) findViewById(R.id.loading_text);
		loading_refresh = (Button) findViewById(R.id.refresh_button);
		loading_refresh.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		    	handleGetMessage(MSG_GETLIST);
		    }
		});
		
		mScrollView = (ScrollView)findViewById(R.id.scroll);
				
		//------Get App Info------
		IntentFilter filter = new IntentFilter();
		filter.addAction(HwConstant.ACTION_REQUEST_APP_INFO + "_COMPLETE");
		filter.addAction(HwConstant.ACTION_HAWAII_SEARCH_APP_INFO + "_COMPLETE_NEW");
		mInfoReceiver = new AppInfoReceiver();
		registerReceiver(mInfoReceiver, filter);
	}
	
	public void onDestroy(){		
		unregisterReceiver(mInfoReceiver);
		super.onDestroy();	
	}

    private void handleGetMessage(int msgID ) {
    	mInitHandler.removeMessages(msgID);
    	mInitHandler.sendEmptyMessage(msgID);
    }
    
	private void getAppInfo() {
		//zhanglz1
		if (mCategory.equals( HwConstant.CATEGORY_WALLPAPER_STRING)) {
			if (mApp != null) {
				Log.i(TAG, "AppDetail.getAppInfo()>>mApp != null");
				updateDetailView(mApp);
				return;

			}
		}
		Log.i(TAG, "AppDetail.getAppInfo(), category:"+ mCategory+", pkg:"+ mPackageName);
		if (!TextUtils.isEmpty(mPackageName)
				&& !TextUtils.isEmpty(mVersionCode)) {
			DownloadAppInfo appInfo = null;
			
			if (mCategory.equals(HwConstant.CATEGORY_HAWAII_SEARCH_APP)) {
				requestAppInfo_hawaiisearch();
			} else {

				if (!mCategory.equals(HwConstant.CATEGORY_WALLPAPER_STRING)) {
					appInfo = HWDBUtil.queryAppInfo(mContext, mPackageName,
							mVersionCode);
				} else {
//					appInfo = HWDBUtil.queryAppInfoBG(mContext, mPackageName,
//							mVersionCode);
				}
				Log.i("zdx", "AppDetail.getAppInfo(), appInfo:" + appInfo);
				if (appInfo != null)
					updateDetailView(appInfo);
				else
					requestAppInfo();
			}
			
		}

		else
			showErrorView();

	}
    //------Get App Info------
    //yangmao add new 
	private void requestAppInfo_hawaiisearch() {
		Log.i(TAG, "===requestAppInfo_hawaiisearch()===");
		showLoadView();
		Intent hawaii_search_intent = new Intent();
		hawaii_search_intent.setAction(HwConstant.ACTION_HAWAII_SEARCH_APP_INFO);
		hawaii_search_intent.putExtra("package_name", mPackageName);
		hawaii_search_intent.putExtra("version_code", mVersionCode);
		mContext.sendBroadcast(hawaii_search_intent);
	}
	private void requestAppInfo() {
		showLoadView();
		Intent intent = new Intent();
		intent.setAction(HwConstant.ACTION_REQUEST_APP_INFO);
		intent.putExtra("package_name", mPackageName);
		intent.putExtra("version_code", mVersionCode);
		mContext.sendBroadcast(intent);
	}
	
	private void showLoadView(){
		mScrollView.setVisibility(View.GONE);
		loadingV.setVisibility(View.VISIBLE);
		loading_textV.setText(R.string.detail_loading);
		loading_refresh.setVisibility(View.GONE);
	}
	
	private void showErrorView(){
		mScrollView.setVisibility(View.GONE);
		loadingV.setVisibility(View.VISIBLE);
		loading_textV.setText(R.string.detail_loading_falied);
		loading_refresh.setVisibility(View.VISIBLE);
		progressBarV.setVisibility(View.GONE);
	}
	private void showEmptyView(){
		mScrollView.setVisibility(View.GONE);
		loadingV.setVisibility(View.VISIBLE);
		loading_textV.setText(R.string.detail_loading_empty);
		loading_refresh.setVisibility(View.VISIBLE);
		progressBarV.setVisibility(View.GONE);
	}
	
	private void showListView(DownloadAppInfo app){
		loadingV.setVisibility(View.GONE);
		mScrollView.setVisibility(View.VISIBLE);
		//Log.i(TAG,"<zdx>************************************snap view: "+  app.getFirstSnapPath());
		mGandalfFlipper = (GandalfFlipper) findViewById(R.id.gallery);
		String firstSnapPath = null;
		if (!mCategory.equals(HwConstant.CATEGORY_WALLPAPER_STRING)) {
			firstSnapPath = app.getFirstSnapPath();
		}else{
			//firstSnapPath = app.getPreviewAddr();
		}
		
		if( firstSnapPath != null ){
		     mGandalfFlipper.setAdapter(new SnapshotAdapter(this, firstSnapPath.split(",")));
		}
		TextView version = (TextView) findViewById(R.id.detail_version);
		version.setText(mContext.getString(R.string.detail_version) + app.getAppVersion());
		
		((TextView) findViewById(R.id.detail_desc)).setText(app.getAppAbstract());
			//zhanglz1-s
//        if(mCategory.equals(HwConstant.CATEGORY_WALLPAPER_STRING)){
//    		((TextView) findViewById(R.id.detail_desc)).setText( app.getFavorites()+
//    				this.getResources().getString(R.string.detail_auther)+app.getAuther());
//    		version.setVisibility(View.GONE);
//		}else
//			version.setVisibility(View.VISIBLE);
		//zhanglz1-end
}
	
	private void updateDetailView(DownloadAppInfo app) {
		if(app == null) {
			showErrorView();
			return;
		}
		showListView(app);
	}

	private class SnapshotAdapter extends BaseAdapter {
		private final Drawable[] mSnapshotList;
		private final String[] mSnapPathshotList;
		private final Context mContext;
		public SnapshotAdapter(Context context, String[] snapshotPathList) {
			this.mContext = context;
			this.mSnapPathshotList = snapshotPathList;
			//Log.d(TAG, "init SnapshotAdapter, img size=" + snapshotPathList.length);
			//for( int i = 0; i< mSnapPathshotList.length; i++){
			//	Log.i(TAG,"+++++++++++++++++++path:"+ mSnapPathshotList[i]);
			//}
			this.mSnapshotList = new Drawable[mSnapPathshotList.length];
		}

		@Override
		public int getCount() {
			return mSnapPathshotList.length;
		}

		@Override
		public Object getItem(int position) {
			return mSnapPathshotList[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ImageView view;
			//Log.d(TAG, "snapshotAdapter getview at position : " + position);
			if(convertView == null) {
				view = new ImageView(mContext);
				view.setScaleType(ScaleType.FIT_XY);
				int width = mContext.getResources().getDimensionPixelSize(R.dimen.snap_image_width);
				int height = mContext.getResources().getDimensionPixelSize(R.dimen.snap_image_height);
				view.setLayoutParams(new Gallery.LayoutParams(width, height));
				view.setImageResource(R.drawable.magicdownload_detail_snapshot_def);
			} else {
				view = (ImageView) convertView;
			}
			
			if(LejingpingSettingsValues.previewDownloadValue(mContext)) {
				if(mSnapshotList[position] == null) {
					AppDownloadControl.loadImg(getItem(position).toString().trim(), new OnImgLoadListener() {
						public void onLoadComplete(final Drawable img) {
							//Log.d(TAG, "onLoadComplete at postion : " + position + ", viewIsShown=" + 
						    //    view.isShown() + ", img=" + img);
							if(img == null){
								return;
							}
							mSnapshotList[position] = img;
							runOnUiThread(new Runnable() {
								public void run() {
									view.setImageDrawable(img);
								}
							});
	
						}
					},mContext);
				} else {
					view.setImageDrawable(mSnapshotList[position]);
				}
			}
			return view;
		}
	}

}
