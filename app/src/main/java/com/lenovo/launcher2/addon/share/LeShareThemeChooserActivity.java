package com.lenovo.launcher2.addon.share;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.customizer.GlobalDefine;
import com.lenovo.launcher2.customizer.Reaper;
import com.lenovo.launcher2.customizer.Utilities;
import com.lenovo.launcher2.addon.share.LelauncherAppsShare.Item;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.lejingpin.LEJPConstant;
import com.lenovo.lejingpin.ThemeFragment.LocalAdapter;
import com.lenovo.lejingpin.network.AmsApplication;


/**
 * Author : Liuyg1@lenovo.com
 * */
public class LeShareThemeChooserActivity extends Activity {

	private static final String mimeType = "application/vnd.android.package-archive";
	private List<Object> mThemes = new ArrayList<Object>();
	private String sDefaultAndroidTheme;
	private GridView mLocalGrid;
	private TextView emptyView;
	private static final int MSG_GETLOCALLIST = 101;
	private static final int MSG_GETOTHERLOCALLIST = 102;
	private ArrayList<AmsApplication> mLocalDataList; //local data
	private ArrayList<AmsApplication> mFirstLocalDataList; //local data
	private ArrayList<AmsApplication> mSelectedLocalDataList; 
	private LocalAdapter mLocalAdapter;
	public static final String SP_CURRENT = "CURRENT";
	private ArrayList<Parcelable> mFileList;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
	
		mFileList = new ArrayList<Parcelable>();
		mSelectedLocalDataList = new ArrayList<AmsApplication>();
		setContentView(R.layout.share_theme_chooser);
		
		//test by dining add custom title
				TextView title = (TextView)findViewById(R.id.dialog_title);
				title.setText(R.string.desktop_share_theme_text);
				ImageView icon = (ImageView)findViewById(R.id.dialog_icon);
				icon.setImageResource(R.drawable.dialog_title_back_arrow);
				
				//test by dining
                
				icon.setOnClickListener(new View.OnClickListener() {

				            @Override
				            public void onClick(View view) {
				                finish();
				            }
				});
						
		sDefaultAndroidTheme = SettingsValue.getDefaultAndroidTheme(this);
		getDataFromLocal(true);
		mLocalGrid = (GridView) findViewById(R.id.local_theme);
		mLocalGrid.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
					int maPosition = mLocalGrid.getFirstVisiblePosition();
					int mbPosition = mLocalGrid.getLastVisiblePosition();
					Log.d("liuyg1", "aaa---onScroll---firstVisibliepositon===="+maPosition+" lastVisiblieposiont===="+mbPosition);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
					int totalItemCount) {
			}
		});
		emptyView = (TextView)findViewById(R.id.empty_textview);

		LocalAdapter localAdapter = new LocalAdapter(mLocalDataList);
		mLocalGrid.setAdapter(localAdapter);

		final Button leSendBtn = (Button) findViewById(R.id.addfinish);//R.id.btn_leshare
		leSendBtn.setText(R.string.send_leshare);
		leSendBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!LeShareUtils.isInstalledQiezi(LeShareThemeChooserActivity.this)){
					LeShareUtils.showInstallDialog(LeShareThemeChooserActivity.this,false);
					return;
				}else if(!LeShareUtils.isInstalledRightQiezi(LeShareThemeChooserActivity.this)){
					LeShareUtils.showInstallDialog(LeShareThemeChooserActivity.this,true);
					return;
				}
            	if(mFileList.size()==0){
            		Toast.makeText(LeShareThemeChooserActivity.this, getString(R.string.share_no_app), Toast.LENGTH_SHORT).show();
            		return;
            	}
        		final Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        		shareIntent.setType(mimeType);
        		shareIntent.setPackage("com.lenovo.anyshare");
//        		ComponentName  mComponentName = new ComponentName("com.lenovo.anyshare", "com.lenovo.anyshare.apexpress.ApDiscoverActivity"); 
//        		shareIntent.setComponent(mComponentName);
        		PackageManager pm = getPackageManager();
        		ResolveInfo reInfo = pm.resolveActivity(shareIntent, 0);
        		if(reInfo!=null){
        			shareIntent.setClassName(reInfo.activityInfo.packageName, reInfo.activityInfo.name);
        		}
        		shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, mFileList);
        		shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
        		try{	
        			startActivity(shareIntent);
        		}catch(Exception e){
        			Toast.makeText(LeShareThemeChooserActivity.this, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
        		}
				Reaper.processReaper(LeShareThemeChooserActivity.this, 
                        "Share", 
          				"ToThemeQiezi",
          				"mFileList.size()", 
          				Reaper.REAPER_NO_INT_VALUE );
        		finish();
			}
		});
		Button otherSendBtn = (Button) findViewById(R.id.canceladd);//R.id.btn_other
		otherSendBtn.setText(R.string.btn_cancel);
		otherSendBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
			}
		);
		/*** RK_ID:RK_FONT AUT:zhanglz1@lenovo.com. DATE: 2013-02-21 S ***/
        Typeface tf = SettingsValue.getFontStyle(LeShareThemeChooserActivity.this);
		if (tf != null && tf !=otherSendBtn.getTypeface() ) {
			leSendBtn.setTypeface(tf);
			otherSendBtn.setTypeface(tf);
			emptyView.setTypeface(tf);
		}
		/*** RK_ID:RK_FONT AUT:zhanglz1@lenovo.com. DATE: 2013-02-21 E ***/
		
		if(!SettingsValue.isRotationEnabled(this)){
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

	}
	 LEJPConstant mLeConstant = LEJPConstant.getInstance();
    @Override
    public void onResume(){
    	super.onResume();
        Log.d("liuyg1", "---onResume---");
    	if(mLeConstant.mThemeNeedRefresh && mLeConstant.mIsThemeDeleteFlag){
    		mLocalAdapter.notifyDataSetChanged();
    		mLeConstant.mThemeNeedRefresh = false;
    		mLeConstant.mIsThemeDeleteFlag = false;
	if ( mLeConstant.mServiceLocalThemeAmsDataList != null && mLeConstant.mServiceLocalThemeAmsDataList.size() == 0){ 
        		if(emptyView != null && mLocalGrid != null){
        			 Log.d("liuyg1", "---emptyView---");
    				emptyView.setVisibility(View.VISIBLE);
    				mLocalGrid.setVisibility(View.GONE);
        		}
        	}
    	}
    }
//@Override
//	protected void onRestart() {
//		// TODO Auto-generated method stub
//		super.onRestart();
//		Log.d("liuyg1","ShareThemeChooserActivity onRestart");
//	}
//	@Override
//	protected void onResume() {
//		// TODO Auto-generated method stub
//		super.onResume();
//		Log.d("liuyg1","ShareThemeChooserActivity onResume");
//	}
//	@Override
//	protected void onPause() {
//		// TODO Auto-generated method stub
//		super.onPause();
//		Log.d("liuyg1","ShareThemeChooserActivity onPause");
//	}
//	@Override
//	protected void onDestroy() {
//		// TODO Auto-generated method stub
//		super.onDestroy();
//		Log.d("liuyg1","ShareThemeChooserActivity onDestroy");
//	}
//	@Override
//	public void onBackPressed() {
//		// TODO Auto-generated method stub
//		super.onBackPressed();
//		Log.d("liuyg1","ShareThemeChooserActivity onBackPressed");
//	}
//	@Override
//	public void finish() {
//		// TODO Auto-generated method stub
//		super.finish();
//		Log.d("liuyg1","ShareThemeChooserActivity finish");
//	}

	//    public ArrayList<Parcelable> getItems() {
//    	
//
//        PackageManager packageManager = getPackageManager();
//        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
////        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//        final List<ResolveInfo> infolist = packageManager.queryIntentActivities(mainIntent, 0);
//        Collections.sort(infolist, new ResolveInfo.DisplayNameComparator(packageManager));
//        for(AmsApplication ams : mSelectedLocalDataList){
//			Log.d("liuyg1", "mSelectedLocalDataList"+ams.getPackage_name());
//        	String packageName = ams.getPackage_name();
//        	for (int i = 0; i < infolist.size(); i++) {
//        		ResolveInfo info = infolist.get(i);
//        		if (info.activityInfo.packageName.equals(packageName)){
//        			Item item = new Item(this, packageManager, info, false);
////        			Log.d("liuyg1", "info.activityInfo.packageName"+info.activityInfo.packageName);
////        			Log.d("liuyg1", "item.sourceDir"+item.sourceDir);
//        			File file = new File(item.sourceDir);
//        			Uri uri = Uri.fromFile(file);
//        			list.add(uri);
//        			break;
//        		}
//        		
//        	}
//        }
//    }
	private Handler mInitHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_GETLOCALLIST:
//				getLocalTheme();
//				showGridViewLocalContent();
            	initLocalThemeList();
            	getLocalTheme(0);
            	showGridViewLocalContent();
				break;
			case MSG_GETOTHERLOCALLIST:
//				lastloadItems();
				lastloadItems(mCurLoadingLocalLineIndex); 
				break;
			default:{
				break;
			}
			}
		}
	};
    private void lastloadItems(int lineIndex) {
    	Log.e("liuyg1","======= lastloadItems: lineIndex = " + lineIndex);
        Log.e("liuyg1","lastloadItems <F2><F2><F2><F2><F2><F2><F2><F2><F2><F2>");
        
        /***RK_ID:RK_REFRESH AUT:liuyg1@lenovo.com.DATE: 2013-1-18. S***/
        getLocalTheme(lineIndex);
        /***RK_ID:RK_REFRESH AUT:liuyg1@lenovo.com.DATE: 2013-1-18. E***/
        mLoadFlag = (mLocalDataList.size() > 0);
    	Log.d("liuyg1", "========= mLoadFlag ====  "+mLoadFlag);
    	if (!mLoadFlag) {
    		emptyView.setVisibility(View.VISIBLE);
    		Log.d("liuyg1", "emptyView.setVisibility(View.VISIBLE);  ");
    		mLocalGrid.setVisibility(View.GONE);
    	} else {
    		Log.d("liuyg1", "emptyView.setVisibility(View.INVISIBLE);");
    		emptyView.setVisibility(View.GONE);
    		mLocalGrid.setVisibility(View.VISIBLE);
    	}
        mLocalAdapter = new LocalAdapter(mLocalDataList);
        mLocalGrid.setAdapter(mLocalAdapter);
        mLocalAdapter.addMoreContent();
        
        /***RK_ID:RK_REFRESH AUT:liuyg1@lenovo.com.DATE: 2013-1-18. S***/
        if(mThemes != null && mThemes.size() > maxloadnum * (lineIndex + 1)){
        	mCurLoadingLocalLineIndex++;
			mInitHandler.removeMessages(MSG_GETOTHERLOCALLIST);
			mInitHandler.sendMessageDelayed(mInitHandler.obtainMessage(MSG_GETOTHERLOCALLIST), 100);
        }
        /***RK_ID:RK_REFRESH AUT:liuyg1@lenovo.com.DATE: 2013-1-18. E***/
    }
	
	private void getDataFromLocal(boolean isFirstTime){
		Log.e("liuyg1","========= getDataFromLocal"+isFirstTime+"falag="+LEJPConstant.getInstance().mThemeNeedRefresh);
		if(!isFirstTime && !LEJPConstant.getInstance().mThemeNeedRefresh){
			return;
		}
		SharedPreferences sp = getSharedPreferences(SP_CURRENT, 0);
		LEJPConstant.getInstance().mCurrentTheme = sp.getString("current_theme", "");
		mInitHandler.removeMessages(MSG_GETLOCALLIST);
		//mInitHandler.sendMessageDelayed(mInitHandler.obtainMessage(MSG_GETLOCALLIST), 100);
		mInitHandler.sendMessage(mInitHandler.obtainMessage(MSG_GETLOCALLIST));
		Log.e("liuyg1","========= getDataFromLocal sendDealy");
		LEJPConstant.getInstance().mThemeNeedRefresh = false;
		if(LEJPConstant.getInstance().mCurrentTheme.equals("")){
			LEJPConstant.getInstance().mCurrentTheme = getCurThemePackageName();
		}
	}
	private int maxloadnum = 2;//3
	private void getLocalTheme(int lineIndex){
    	Log.e("liuyg1","========= getLocalTheme lineIndex="+ lineIndex);
        if(0 == lineIndex){
        	if(mLocalDataList == null){
    		    mLocalDataList = new ArrayList<AmsApplication>();
    		}else{
    		    mLocalDataList.clear();
    		}
    	/*	if(mFirstLocalDataList == null){
    		    mFirstLocalDataList = new ArrayList<AmsApplication>();
    		}else{
    		    mFirstLocalDataList.clear();
    		}*/
        }

        if(mThemes != null && mThemes.size() > 0){
        	int totalSize = mThemes.size();
        	int endIndex = ((maxloadnum*lineIndex + maxloadnum) < totalSize) ? (maxloadnum*lineIndex + maxloadnum) : totalSize;
        	Log.e("liuyg1","========= getLocalTheme endIndex="+ endIndex);
        	for (int i = maxloadnum*lineIndex; i < endIndex; i++) {
    			try {
    				Object arawInfo = mThemes.get(i);
    			} catch (Exception e) {
    				return;
    			}
    			Object rawInfo = mThemes.get(i);
    			AmsApplication data = new AmsApplication();
    			data.setIsNative(true);
    			if (rawInfo instanceof Integer) {
    				continue;
//    				// get the preview images for version_WW
//    				String defaultTheme = SettingsValue.getDefaultThemeValue(this);
//    				// Log.e("liuyg1","========= getLocalTheme default theme="+defaultTheme);
//    				
//    					/*
//    					 * PK_ID:THEME ADPTER ALL DEVICES AUTH:GECN1 DATE:2012-12-7 * S
//    					 */
//    					data.setpreviewResId(getResources().getDrawable(R.drawable.themepreview));
//    					data.setpreviewResId(getResources().getDrawable(R.drawable.themepreview_1));
//    					data.setpreviewResId(getResources().getDrawable(R.drawable.themepreview_2));
//    					/*
//    					 * PK_ID:THEME ADPTER ALL DEVICES AUTH:GECN1 DATE:2012-12-7 * E
//    					 */
//    				
//    				/* RK_VERSION_WW dining 2012-10-22 E */
//    				data.setPackage_name(defaultTheme);
//    				data.setAppName(getString(R.string.theme_settings_default_theme));
    			} else if (rawInfo instanceof String) {
    				String[] previewImages = null;
    				String pkgName = rawInfo.toString();
    				String previewNameString = "config_theme_previews";
    				Context mFriendContext = null;
    				try {
    					mFriendContext = this.createPackageContext(pkgName, Context.CONTEXT_IGNORE_SECURITY);
    				} catch (Exception e) {
    					e.printStackTrace();
    					continue;
    				}
    				data.setPackage_name(pkgName);

    				String label = null;
    				int strID = mFriendContext.getResources().getIdentifier("app_name", "string", pkgName);

    				if (pkgName.equals(SettingsValue.getDefaultThemeValue(this))) {
    					label = this.getString(R.string.theme_settings_default_theme);
    				} else {
    					if (strID == 0) {
    						label = this.getString(R.string.unknow_theme_name);
    					} else {
    						label = mFriendContext.getString(strID);
    					}
    				}

    				data.setAppName(label);
    				
    				/* PK_ID:LOAD THEME PRVIEW FROME LOCAL CACHE  AUTH:liuyg1 DATE:2013-1-22	* S */
    				Log.d("liuyg1","=========== getLocalTheme pkgName = " + pkgName);
    				SharedPreferences sp = this.getSharedPreferences(SP_THEME_LOCAL_PREVIEW, 0);
    				boolean bReLoadPreview = false;
    				if(sp.contains(pkgName)){
    					String preview_files = sp.getString(pkgName, "");
    					Log.d("liuyg1","=========== getLocalTheme prview files: " + preview_files);
    					String[] files = preview_files.split(",");
                                        int ai=0;
    					for(String f : files){
    						File file = new File(f);
    						if(file.exists()){
                                                        if(ai==0){
    							Drawable pic =  Drawable.createFromPath(f);
    							data.setpreviewResId(pic);
                                                        }
                                                        ai++;
    						}else{
    							bReLoadPreview = true;
							}
    					}
    				}

					if((sp.contains(pkgName) && bReLoadPreview) || !sp.contains(pkgName))
					{
    				previewImages = LeShareUtils.findStringArrayByResourceName(previewNameString, mFriendContext);
    				if (previewImages == null) {
    					String previewName = "themepreview";
    					Log.d("thempaged","ThemePagedView-->syncSingleThemePage():previewName1= " + previewName);
    					Log.d("liuyg1","getLocalTheme: previewImages is null! ");
    					Drawable brawInfo = LeShareUtils.findDrawableByResourceName("themepreview", mFriendContext);
    					if (brawInfo == null) {
    						brawInfo = this.getResources().getDrawable(R.drawable.ic_launcher_theme_shortcut);
    					}
    					data.setpreviewResId(brawInfo);
    				} else {
    					Log.d("liuyg1","getLocalTheme: previewImages is not null, length =  " + previewImages.length);
    					String previews = "";
    					for (int j = 0; j < previewImages.length; j++) {
    						String previewName = previewImages[j];
    						Log.d("liuyg1","getLocalTheme: previewImages j = " + j + ", previewName = " + previewName);
    						Drawable arawInfo = LeShareUtils.findDrawableByResourceName(previewName, mFriendContext);
    						if (arawInfo == null) {
    							arawInfo = this.getResources().getDrawable(R.drawable.ic_launcher_theme_shortcut);
    						}
    						String previewFile = pkgName + "_" + previewName + ".png";
    						String preview = savePreviewDrawableToFile(arawInfo, previewFile);
    						if(!previews.equals("")){
    							previews += "," ;
    						}
    						previews += preview;
    						data.setpreviewResId(arawInfo);
    					}
    					saveLocalPreviewInfo(pkgName, previews);
    				}
    				}
    				/* PK_ID:LOAD THEME PRVIEW FROME LOCAL CACHE  AUTH:liuyg1 DATE:2013-1-22	* E */
    			}
    			Log.d("liuyg1","mLocalDataList.add(data);");
    			mLocalDataList.add(data);

    		}
        }
    }
    private static String getLocalCachePath(){
		return Environment.getExternalStorageDirectory() + "/IdeaDesktop/LeJingpin/local_themes";
	}
    private static String getLocalPathToSave(String fileName){
        //download icons by liuyg1
        String path = getLocalCachePath();
        String filePath = "";
        File dir = new File(path);
        if(!dir.exists()){
        	dir.mkdir(); 
        }
        filePath = dir.getPath() + "/";
        filePath += fileName;
		return filePath;
	}
    private String savePreviewDrawableToFile(Drawable drawable, String fileName){
    	String path = getLocalPathToSave(fileName);
    	File f = new File(path);
    	if(f.exists()){
    		return path;
    	}else{
    		if(drawable != null){
	    		Bitmap bmp = ((BitmapDrawable)drawable).getBitmap();
	    		final ByteArrayOutputStream os = new ByteArrayOutputStream();
	    		CompressFormat format;
	    		if(path.toLowerCase().endsWith("png")){
	    			format = Bitmap.CompressFormat.PNG;
	    		}else{
	    			format = Bitmap.CompressFormat.JPEG;
	    		}
	    		bmp.compress(format, 100, os);
	    		  
	    		File tmpFile = new File(path + ".tmp");
	    		FileOutputStream fos;
	    		try {
	    			fos = new FileOutputStream(tmpFile);
	    			fos.write(os.toByteArray());
	    			fos.close();
	    		} catch (FileNotFoundException e1) {
	    			e1.printStackTrace();
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}
	    		tmpFile.renameTo(new File(path));
	    		return path;
    		}else{
    			return null;
    		}
    	}
	}
	public final static String SP_THEME_LOCAL_PREVIEW = "local_themepreview _url";
	private final static String EXCLUDED_SETTING_KEY = "exclude_from_backup";
 
    private static void addExcludeSettingKey(SharedPreferences sp){
    	if(!sp.contains(EXCLUDED_SETTING_KEY)){
    		SharedPreferences.Editor editor = sp.edit();
    		editor.putBoolean(EXCLUDED_SETTING_KEY, true).commit();
    	}
    }
    private void saveLocalPreviewInfo(String pkgname, String previewFiles){
	    	SharedPreferences sp = getSharedPreferences(SP_THEME_LOCAL_PREVIEW, 0);
	    	addExcludeSettingKey(sp);
	    	SharedPreferences.Editor editor = sp.edit();
	    	editor.putString(pkgname, previewFiles);
	    	editor.commit();
    }
//	private void getLocalTheme(){
//		initLocalThemeList();
//		if(mLocalDataList == null){
//			mLocalDataList = new ArrayList<AmsApplication>();
//		}else{
//			mLocalDataList.clear();
//		}
////		if(mSelectedLocalDataList == null){
////			mSelectedLocalDataList = new ArrayList<AmsApplication>();
////		}else{
////			mSelectedLocalDataList.clear();
////		}
//		
//		if(mFirstLocalDataList == null){
//			mFirstLocalDataList = new ArrayList<AmsApplication>();
//		}else{
//			mFirstLocalDataList.clear();
//		}
//
//		Log.d("liuyg1","========= getLocalTheme size="+mThemes.size());
//		for (int i = 0; i < maxloadnum; i++) {
//			try{
//				Object arawInfo = mThemes.get(i);
//			}catch(Exception e){
//				return;
//			}
//			Object rawInfo = mThemes.get(i);
//			AmsApplication data = new AmsApplication();
//			data.setIsNative(true);
//			if (rawInfo instanceof Integer) {
//				continue;
////				//get the preview images for version_WW
////				String defaultTheme = SettingsValue.getDefaultThemeValue(this);
////				//Log.e("liuyg1","========= getLocalTheme default theme="+defaultTheme);
////				if(GlobalDefine.getVerisonWWConfiguration(this)){
////					/*PK_ID:THEME ADPTER ALL DEVICES AUTH:GECN1 DATE:2012-12-7 S*/
////					data.setpreviewResId(getResources().getDrawable(R.drawable.themepreview_ww));
////					data.setpreviewResId(getResources().getDrawable(R.drawable.themepreview_1_ww));
////					data.setpreviewResId(getResources().getDrawable(R.drawable.themepreview_2_ww));
////					/*PK_ID:THEME ADPTER ALL DEVICES AUTH:GECN1 DATE:2012-12-7 E*/
////				}else{
////					/*PK_ID:THEME ADPTER ALL DEVICES AUTH:GECN1 DATE:2012-12-7 S*/
////					data.setpreviewResId(getResources().getDrawable(R.drawable.themepreview));
////					data.setpreviewResId(getResources().getDrawable(R.drawable.themepreview_1));
////					data.setpreviewResId(getResources().getDrawable(R.drawable.themepreview_2));
////					/*PK_ID:THEME ADPTER ALL DEVICES AUTH:GECN1 DATE:2012-12-7 S*/
////				}
////				/*RK_VERSION_WW  dining 2012-10-22 E*/
////				data.setPackage_name(defaultTheme);
////				data.setAppName(getString(R.string.theme_settings_default_theme));
//			}
//			else if (rawInfo instanceof String) {
//				String[] previewImages = null;
//				String pkgName = rawInfo.toString();
//				String previewNameString = "config_theme_previews";
//				Context  mFriendContext = null;
//				try{
//					mFriendContext = createPackageContext(pkgName, Context.CONTEXT_IGNORE_SECURITY);
//				} catch (Exception e) {
//					e.printStackTrace();
//					continue;
//				}
//				data.setPackage_name(pkgName);
//				Log.d("liuyg1","========= pkgName ="+pkgName);
//				String label = null;
//				int strID = mFriendContext.getResources().getIdentifier("app_name", "string", pkgName);
//
//				if (pkgName.equals(SettingsValue.getDefaultThemeValue(this))) {
//					label = getString(R.string.theme_settings_default_theme);
//				} else {
//					if (strID == 0) {
//						label = getString(R.string.unknow_theme_name);
//					} else {
//						label = mFriendContext.getString(strID);
//					}
//				}
//
//				data.setAppName(label);
//				previewImages = Utilities.findStringArrayByResourceName(previewNameString, mFriendContext);
//				if (previewImages == null) {
//					String previewName = "themepreview";
//					Log.d("thempaged", "ThemePagedView-->syncSingleThemePage():previewName1= " + previewName);
//					Drawable brawInfo = Utilities.findDrawableByResourceName("themepreview", mFriendContext);
//					if (brawInfo == null ) {
//						brawInfo = getResources().getDrawable(R.drawable.ic_launcher_theme_shortcut);
//					}
//					data.setpreviewResId(brawInfo);
//				} else {
//					for (int j = 0; j <  previewImages.length; j++) {
//						String previewName = previewImages[j];
//						Drawable arawInfo = Utilities.findDrawableByResourceName(previewName, mFriendContext);
//						if (arawInfo == null) {
//							arawInfo = getResources().getDrawable(R.drawable.ic_launcher_theme_shortcut);
//						}
//						data.setpreviewResId(arawInfo);
//					}
//				}
//			}
//			mFirstLocalDataList.add(data);
//		}
//		Log.e("liuyg1","========= getLocalTheme size end="+mThemes.size());
//	}
    private int mCurLoadingLocalLineIndex = 0;
	private void initLocalThemeList() {
		mThemes.clear();
		mCurLoadingLocalLineIndex = 0;
		String defaultTheme = SettingsValue.getDefaultThemeValue(this);
		if (defaultTheme.equals(sDefaultAndroidTheme)) {
			// get the preview images for version_WW
			
			mThemes.add(R.drawable.themepreview);
			
		} else {
			// if (!defaultTheme.equals(sDefaultAndroidTheme)) {
			mThemes.add(defaultTheme);
		}
		List<ResolveInfo> installedSkins = LeShareUtils.findActivitiesForSkin(this);
		if (installedSkins != null) {
			for (ResolveInfo skin : installedSkins) {
				mThemes.add(skin.activityInfo.packageName);
			}
		}
		Log.d("liuyg1","====== initLocalThemeList: theme size = "+mThemes.size());
	}
//	private void getLocalThemeSecond(){
//		List<Object> lastlist = mThemes.size() > maxloadnum ? mThemes.subList(maxloadnum, mThemes.size()) : null;
//		if(lastlist != null && lastlist.size() > 0){
//			mLocalDataList.addAll(mFirstLocalDataList);
//			Log.e("liuyg1","========= getLocalThemeSecond size="+lastlist.size());
//			for(int j=0;j<lastlist.size();j++){
//				Object rawInfo = lastlist.get(j);
//				AmsApplication data = new AmsApplication();
//				data.setIsNative(true);
//
//				if (rawInfo instanceof Integer) {
//					continue;
//					
////					//get the preview images for version_WW
////					String defaultTheme = SettingsValue.getDefaultThemeValue(this);
////					Log.e("liuyg1","========= getLocalTheme default theme="+defaultTheme);
////					if(GlobalDefine.getVerisonWWConfiguration(this)){
////						/*PK_ID:THEME ADPTER ALL DEVICES AUTH:GECN1 DATE:2012-12-7 S*/
////						data.setpreviewResId(getResources().getDrawable(R.drawable.themepreview_ww));
////						data.setpreviewResId(getResources().getDrawable(R.drawable.themepreview_1_ww));
////						data.setpreviewResId(getResources().getDrawable(R.drawable.themepreview_2_ww));
////						/*PK_ID:THEME ADPTER ALL DEVICES AUTH:GECN1 DATE:2012-12-7 E*/
////					}else{
////						/*PK_ID:THEME ADPTER ALL DEVICES AUTH:GECN1 DATE:2012-12-7 S*/
////						data.setpreviewResId(getResources().getDrawable(R.drawable.themepreview));
////						data.setpreviewResId(getResources().getDrawable(R.drawable.themepreview_1));
////						data.setpreviewResId(getResources().getDrawable(R.drawable.themepreview_2));
////						/*PK_ID:THEME ADPTER ALL DEVICES AUTH:GECN1 DATE:2012-12-7 S*/
////					}
////					/*RK_VERSION_WW  dining 2012-10-22 E*/
////					data.setPackage_name(defaultTheme);
////					data.setAppName(getString(R.string.theme_settings_default_theme));
//				}
//				else if (rawInfo instanceof String) {
//					String[] previewImages = null;
//					String pkgName = rawInfo.toString();
//					String previewNameString = "config_theme_previews";
//					Context  mFriendContext = null;
//					try{
//						mFriendContext = createPackageContext(pkgName, Context.CONTEXT_IGNORE_SECURITY);
//					} catch (Exception e) {
//						e.printStackTrace();
//						continue;
//					}
//					data.setPackage_name(pkgName);
//					Log.d("liuyg1","========= pkgName ="+pkgName);
//					String label = null;
//					int strID = mFriendContext.getResources().getIdentifier("app_name", "string", pkgName);
//					if (pkgName.equals(SettingsValue.getDefaultThemeValue(this))) {
//						label = getString(R.string.theme_settings_default_theme);
//					} else {
//						if (strID == 0) {
//							label = getString(R.string.unknow_theme_name);
//						} else {
//							label = mFriendContext.getString(strID);
//						}
//					}
//					Log.d("liuyg1","========= label ="+label);
//					data.setAppName(label);
//					previewImages = Utilities.findStringArrayByResourceName(previewNameString, mFriendContext);
//					if (previewImages == null) {
//						String previewName = "themepreview";
//						Log.d("thempaged", "ThemePagedView-->syncSingleThemePage():previewName1= " + previewName);
//						Drawable brawInfo = Utilities.findDrawableByResourceName("themepreview", mFriendContext);
//						if (brawInfo == null ) {
//							brawInfo = getResources().getDrawable(R.drawable.ic_launcher_theme_shortcut);
//						}
//						data.setpreviewResId(brawInfo);
//					} else {
//						for (int ij = 0; ij <  previewImages.length; ij++) {
//							String previewName = previewImages[ij];
//							Drawable arawInfo = Utilities.findDrawableByResourceName(previewName, mFriendContext);
//							if (arawInfo == null) {
//								arawInfo = getResources().getDrawable(R.drawable.ic_launcher_theme_shortcut);
//							}
//							data.setpreviewResId(arawInfo);
//						}  
//					}
//				}
//				mLocalDataList.add(data);
//			}
//		}
//		Log.e("liuyg1","========= getsencondLocalTheme size end="+mThemes.size());
//	}
    private boolean mLoadFlag = false;
    private void showGridViewLocalContent() {
    	Log.d("liuyg1", "========= showGridViewLocalContent ==== current line is "+mCurLoadingLocalLineIndex);
    	mLoadFlag = (mLocalDataList.size() > 0);
    	Log.d("liuyg1", "========= mLoadFlag ====  "+mLoadFlag);
    	if (!mLoadFlag) {
    		emptyView.setVisibility(View.VISIBLE);
    		Log.d("liuyg1", "emptyView.setVisibility(View.VISIBLE);  ");
    		mLocalGrid.setVisibility(View.GONE);
    	} else {
    		Log.d("liuyg1", "emptyView.setVisibility(View.INVISIBLE);");
    		emptyView.setVisibility(View.GONE);
    		mLocalGrid.setVisibility(View.VISIBLE);
    	}
    	mLocalAdapter = new LocalAdapter(mLocalDataList);
    	mLocalGrid.setAdapter(mLocalAdapter);
    	if(mThemes != null && mThemes.size() > maxloadnum * (mCurLoadingLocalLineIndex + 1)){
    		mCurLoadingLocalLineIndex++;
    		mInitHandler.removeMessages(MSG_GETOTHERLOCALLIST);
    		mInitHandler.sendMessageDelayed(mInitHandler.obtainMessage(MSG_GETOTHERLOCALLIST), 100);//200);
    	}
    } 
//	private void lastloadItems() {
//		getLocalThemeSecond();
//		mLocalAdapter = new LocalAdapter(mLocalDataList);
//		mLocalGrid.setAdapter(mLocalAdapter);
//		mLocalAdapter.addMoreContent();
//	}
//	private boolean mLoadFlag = false;
//	private void showGridViewLocalContent() {
//		Log.d("liuyg1", "========= showGridViewLocalContent ==== local");
//		mLoadFlag = (mFirstLocalDataList.size() > 0);
//		if (!mLoadFlag) {
//			emptyView.setVisibility(View.VISIBLE);
//			mLocalGrid.setVisibility(View.GONE);
//		} else {
//			emptyView.setVisibility(View.GONE);
//			mLocalGrid.setVisibility(View.VISIBLE);
//		}
//		mLocalAdapter = new LocalAdapter(mFirstLocalDataList);
//		mLocalGrid.setAdapter(mLocalAdapter);
//		Log.e("liuyg1","========= getLocalTheme size="+mThemes.size());
//		if(mThemes != null && mThemes.size() > maxloadnum){
//			mInitHandler.removeMessages(MSG_GETOTHERLOCALLIST);
//			mInitHandler.sendMessageDelayed(mInitHandler.obtainMessage(MSG_GETOTHERLOCALLIST), 200);
//		}
//	}
	//    private void showGridViewContent(){
	//        if(mDataList != null && mDataList.size() == 0){
	//            showErrorView(1);
	//            return;
	//        }
	//        replaceView();
	//        if(mAppsAdapter == null)
	//        mAppsAdapter = new AppsAdapter();
	//        mGrid.setAdapter(mAppsAdapter);
	//        mAppsAdapter.addMoreContent(); 
	//    }
	private String getCurThemePackageName(){
		SharedPreferences sp = getSharedPreferences(SettingsValue.PERFERENCE_NAME, 4);
		String curTheme = sp.getString(SettingsValue.PREF_THEME, null);
		LEJPConstant.getInstance().mCurrentTheme = curTheme;
		//Log.i(TAG, "Negative click! isNetworkEnabled ========"+curTheme);
		return curTheme;
	}
	public class LocalAdapter extends BaseAdapter {
		private ArrayList<AmsApplication> mAdapterDataList; //local data
		private LayoutInflater mLi ;
		 PackageManager packageManager;
		 List<ResolveInfo> infolist;
		public LocalAdapter(ArrayList<AmsApplication> items) {
			mAdapterDataList = items; //local data
			 mLi = LayoutInflater.from(LeShareThemeChooserActivity.this);
			 packageManager = getPackageManager();
			 new Thread(){
				 public void run() {
			         Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
			         mainIntent.addCategory(SettingsValue.THEME_PACKAGE_CATEGORY);
			         infolist = packageManager.queryIntentActivities(mainIntent, 0);
			         Collections.sort(infolist, new ResolveInfo.DisplayNameComparator(packageManager));
				 };
			 }.start();

		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			final int pos = position;
			// TextView wallpaperview;
			if (convertView == null) {
				convertView = mLi.inflate(R.layout.local_theme_item, parent,
						false);
			}
			final TextView nameView = (TextView) convertView
					.findViewById(R.id.textname);
			final ImageView image = (ImageView) convertView
					.findViewById(R.id.textpic);
			final ImageView current = (ImageView) convertView
					.findViewById(R.id.current);

			final AmsApplication mLocalData = mAdapterDataList.get(position);
			//Log.e(TAG, "====getView===== position: " + LEJPConstant.getInstance().mCurrentTheme);
			//data.thumbpaths = (new String[]{map_preview.get(key)}
			//String filePath = iconUrl[0];

			//Log.e(TAG, "====getView===== pakcagename: " + mLocalData.getPackage_name()+" cur theme package"+mCurrentTheme+"app name"+mLocalData.getAppName());

			if(mSelectedLocalDataList.size()>0&&mSelectedLocalDataList.contains(mLocalData)){
				current.setVisibility(View.VISIBLE);
			}else{
				current.setVisibility(View.INVISIBLE);
			}

			if(!mLocalData.getIsNative()){
				String filePath = mLocalData.thumbpaths[0];
				File icon = new File(filePath);
				image.setImageURI(Uri.fromFile(icon));
			}else{
				image.setScaleType(ImageView.ScaleType.FIT_CENTER);
				image.setScaleType(ImageView.ScaleType.FIT_XY);
				image.setImageDrawable((mLocalData.getpreviewResId()).get(0));

			}
			nameView.setText(mLocalData.getAppName());
			//Log.e(TAG, "====getView===== position: " + mLocalData.getAppName());

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//					Log.d("c", "startWallpaperDetailActivity");
					//					startWallpaperDetailActivity(type_local, pos);
					if(current.getVisibility()==View.VISIBLE){
						current.setVisibility(View.INVISIBLE);
						String packageName = mLocalData.getPackage_name();
						for (int i = 0; i < infolist.size(); i++) {
							ResolveInfo info = infolist.get(i);
							if (info.activityInfo.packageName.equals(packageName)){
								Item item = new Item(LeShareThemeChooserActivity.this, packageManager, info, false);
								File file = new File(item.sourceDir);
								Uri uri = Uri.fromFile(file);
								mFileList.remove(uri);
								mSelectedLocalDataList.remove(mLocalData);
								break;
							}
						}
						
					}else{
						current.setVisibility(View.VISIBLE);
						String packageName = mLocalData.getPackage_name();
						Log.d("liuyg1","packageName"+packageName);
						for (int i = 0; i < infolist.size(); i++) {
							ResolveInfo info = infolist.get(i);
							if (info.activityInfo.packageName.equals(packageName)){
								Item item = new Item(LeShareThemeChooserActivity.this, packageManager, info, false);
								File file = new File(item.sourceDir);
								Log.d("liuyg1","file"+item.sourceDir);
								Uri uri = Uri.fromFile(file);
								mFileList.add(uri);
								mSelectedLocalDataList.add(mLocalData);
								break;
							}
						}
					}
				}
			});
			// added by liuyg1 end
			return convertView;
		}
		public void addMoreContent() {
			notifyDataSetChanged();
		}
		public final int getCount() {
			//Log.e(TAG,"getCount **** mLocalDataList ==========sieze======="+mLocalDataList.size());
			return (mAdapterDataList == null) ? 0 :mAdapterDataList.size();
		}
		public final Object getItem(int position) {
			return mAdapterDataList.get(position);
		}
		public final long getItemId(int position) {
			return position;
		}
	}
}
