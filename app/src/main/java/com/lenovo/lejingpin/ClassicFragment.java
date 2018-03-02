
package com.lenovo.lejingpin;


import com.lenovo.launcher.R;

 
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.LocalActivityManager;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.view.PagerTitleStrip;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ProgressBar;

import android.widget.Button;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import android.view.View.OnClickListener;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;


import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


import android.view.View.OnTouchListener;
import 	android.view.MotionEvent;

import android.os.AsyncTask;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.app.Fragment;
import java.util.Date;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.zip.GZIPInputStream;
import java.util.HashMap;


import android.view.WindowManager;
import android.graphics.PixelFormat;

import android.view.Gravity;

import android.view.WindowManager.LayoutParams;

import android.widget.RadioGroup;
import android.widget.RadioButton;
import com.lenovo.lejingpin.AsyncImageLoader.ImageCallback;
import com.lenovo.lejingpin.Holder;
import com.lenovo.lejingpin.hw.content.data.HwConstant;

import com.lenovo.lejingpin.network.WallpaperResponse;
import com.lenovo.lejingpin.network.NetworkHttpRequest;
import com.lenovo.lejingpin.network.NetworkHttpRequest.AmsCallback;
import com.lenovo.lejingpin.network.NetworkHttpRequest.HttpReturn;
import com.lenovo.lejingpin.network.WallpaperResponse.ApplicationData;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.service.wallpaper.WallpaperService;

import android.app.WallpaperManager;
import android.app.WallpaperInfo;
import android.os.SystemClock;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import com.lenovo.launcher2.customizer.SettingsValue;

import com.lenovo.launcher2.customizer.Utilities;
import com.lenovo.launcher2.customizer.GlobalDefine;
import com.lenovo.launcher2.customizer.Reaper;
public class ClassicFragment extends Fragment{
 
    public final static String SP_WALLPAPER_URL = "download_wallpaper_url";
	public final static String SP_WALLPAPER_ICON_URL = "download_icon_url";
	public final static String SP_WALLPAPER_PREVIEW_URL = "download_preview_url";
	public final static String SP_WALLPAPER_NAME = "download_wallpaper_name";
	public final static String SP_CURRENT = "CURRENT";
	private final static String EXCLUDED_SETTING_KEY = "exclude_from_backup";
    private ArrayList<Integer> mThumbs;
    private ArrayList<Integer> mImages;
    private ArrayList<String> mImagesname;

    private final static String TAG = "ClassicFragment";
    private static final int MSG_GETLIST = 100;
    private static final int MSG_NO_MORE = 101;
    private static final int MSG_GETLOCALLIST = 102;
    private static final int MSG_DELAY_CREATEVIEW = 103;


    private ViewPager mPager;
    private MyPagerAdapter mpAdapter;

    private List listViews;

    private LocalAdapter mLocalAdapter;
    private GridView mLocalGrid;
    LEJPConstant mLeConstant = LEJPConstant.getInstance();
    private ArrayList<ApplicationData> mLocalDataList; //local data


    private final int type_local = 10;
    private int curtype = 0;
    private List<ResolveInfo> mLiveWallApkList;

    private HashMap<String, SoftReference<Drawable>> mLocalIconList =
           new HashMap<String, SoftReference<Drawable>>();
 
    int mNum;
    private int mStartIndex = 0;
    private int mCount = 20;
    private int phoneWidth;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         
        mLocalIconList.clear();
        mLi = LayoutInflater.from(getActivity());
        Log.d(TAG, "---onCreate---");

        registerDownloadReceiver();
        this.mInitHandler.post(this.registerService);
        updateSharedPreference();
		mOrientation = this.getResources().getConfiguration().orientation;
		}
    private void updateSharedPreference(){
    	LocalDataManager mgr = LocalDataManager.getInstance(getActivity());
   	 	mgr.loadSharedPreferencesFromFile(SP_WALLPAPER_URL, SP_WALLPAPER_URL);
   	 	mgr.loadSharedPreferencesFromFile(SP_WALLPAPER_ICON_URL, SP_WALLPAPER_ICON_URL);
   	 	mgr.loadSharedPreferencesFromFile(SP_WALLPAPER_PREVIEW_URL, SP_WALLPAPER_PREVIEW_URL);
   	 	mgr.loadSharedPreferencesFromFile(SP_WALLPAPER_NAME, SP_WALLPAPER_NAME);
   	 	//mgr.loadSharedPreferencesFromFile(SP_CURRENT, SP_CURRENT);
    }

    Runnable registerService = new Runnable(){
        public void run()
        {
            findWallpapers(); 
            DisplayMetrics metrics = new DisplayMetrics();
            if(getActivity() == null) return;
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            phoneWidth = metrics.widthPixels;

        }
    };

    @Override
    public void onResume(){
    	super.onResume();
    	Log.d(TAG, "---onResume---@#$%@#$^%$^ need refresh="+mLeConstant.mWallpapaerNeedRefresh+"isdelet ="+mLeConstant.mIsWallpaperDeleteFlag);
    	isOnClick = false;
    	int currentOrientation = this.getResources().getConfiguration().orientation;
    	if(currentOrientation != mOrientation){
    		mInitHandler.removeMessages(MSG_DELAY_CREATEVIEW);
            mInitHandler.sendMessage(mInitHandler.obtainMessage(MSG_DELAY_CREATEVIEW, 100));
            mOrientation = currentOrientation;
    	}
    	
    	if(mLeConstant.mWallpapaerNeedRefresh || mLeConstant.mIsWallpaperDeleteFlag){
                getDataFromLocal(false);
    		mLeConstant.mWallpapaerNeedRefresh = false;
    		mLeConstant.mIsWallpaperDeleteFlag = false;
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        if(getActivity() == null) return;
        getActivity().unregisterReceiver(mUiReceiver);
        getActivity().unregisterReceiver(mAPKReceiver);
    }

    int mOrientation;
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    	Log.e("XXXX","=====new orientation is "+newConfig.orientation+ ", mOrientation = "+mOrientation);
    	if(newConfig.orientation != mOrientation){
    		mInitHandler.removeMessages(MSG_DELAY_CREATEVIEW);
            mInitHandler.sendMessage(mInitHandler.obtainMessage(MSG_DELAY_CREATEVIEW, 100));
            mOrientation = newConfig.orientation;
    	}
    }
    
    private Handler mInitHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_GETLOCALLIST:
            	getLocalWallpaper();
            	showGridViewLocalContent();
                break;
            case MSG_DELAY_CREATEVIEW:
               delayCreateView();
               break;
            case MSG_NO_MORE:
               emptyView.setVisibility(View.GONE);
               break;
            default:
            	break;
            }
        }
    };
    private UiReceiver mUiReceiver;
    private APKReceiver mAPKReceiver;
    private class APKReceiver extends BroadcastReceiver {
        private Context mContext;
        public APKReceiver(Context c) {
            mContext = c;
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            final String packageName = intent.getData().getSchemeSpecificPart();
            Log.e(TAG,"onRecevieeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee="+packageName);
            if(Intent.ACTION_PACKAGE_REMOVED.equals(action) || Intent.ACTION_PACKAGE_ADDED.equals(action)){
                checkThemeAPK(packageName,action);
            }
        }
    }

    private void initLiveWallList(){
        if(getActivity() == null) return;
        final Intent bmainIntent = new Intent("action.com.vlife.wallpaper.SET_WALLPAPER_OUTSIDE",null);
        //final Intent bmainIntent = new Intent();
        //bmainIntent.addCategory("android.intent.category.LENOVO_SET_WALLPAPER");
        bmainIntent.addCategory("com.vlife.lenovo.intent.category.VLIFE_SET_WALLPAPER");
        mLiveWallApkList = getActivity().getPackageManager().queryIntentServices(bmainIntent, 0);
        Log.e(TAG,"onRecevieeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee name="+mLiveWallApkList.size());
    }

    private void checkThemeAPK(String mPkgName,String action){
        Log.e(TAG,"onRecevieeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee name="+mPkgName);
        if(Intent.ACTION_PACKAGE_ADDED.equals(action)){
        if(mPkgName != null){
            //if(mPkgName.contains("com.vlife.wallpaper") || mPkgName.contains("com.handpet.wallpaper")){
            if(mPkgName.contains("com.vlife.lenovo.wallpaper.res")){
            }else{
                return;
            }
        }else{
            return;
        }
        initLiveWallList();
        if (mLiveWallApkList != null) {
        int listSize = mLiveWallApkList.size();
        for (int j = 0; j < listSize; j++) {
            ResolveInfo resolveInfo = mLiveWallApkList.get(j);
            try{
            if (mPkgName.equals(resolveInfo.serviceInfo.packageName))
            {
                mInitHandler.removeMessages(MSG_GETLOCALLIST);
                mInitHandler.sendMessageDelayed(mInitHandler.obtainMessage(MSG_GETLOCALLIST), 0);
                break;
            }
            }catch(Exception e){
                continue;
            }
        }
        }
        }
        else{//uninstall livewallpaper apk
            removeTheUninstallLiveWallpaperApk(mPkgName);
        }
    }
    private void removeTheUninstallLiveWallpaperApk(String mPkgName){
        if (mLeConstant.mServiceLocalWallPaperDataList != null && mLeConstant.mServiceLocalWallPaperDataList.size() != 0){
            int listsize = mLeConstant.mServiceLocalWallPaperDataList.size();
            for(int i=0;i<listsize;i++){
                ApplicationData mdata = mLeConstant.mServiceLocalWallPaperDataList.get(i);
                String pkgName = mdata.getPackage_name();
                if(pkgName != null && pkgName.equals(mPkgName)){
                    Log.e(TAG,"uninstall the theme pkgname="+mPkgName);
                    mLeConstant.mServiceLocalWallPaperDataList.remove(mdata);
              	    mLeConstant.mWallpapaerNeedRefresh = true;
    		    mLeConstant.mIsWallpaperDeleteFlag = true;
                    mInitHandler.removeMessages(MSG_GETLOCALLIST);
                    mInitHandler.sendMessageDelayed(mInitHandler.obtainMessage(MSG_GETLOCALLIST), 0);
                    break;
                }
            }
        }
    }

    private void registerDownloadReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction("refresh");
        mUiReceiver = new UiReceiver(getActivity());
        getActivity().registerReceiver(mUiReceiver, filter);

        final IntentFilter afilter = new IntentFilter();
        afilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        afilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        afilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        afilter.addDataScheme("package");
        mAPKReceiver = new APKReceiver(getActivity());
        getActivity().registerReceiver(mAPKReceiver, afilter);
    }
    private class UiReceiver extends BroadcastReceiver {
		private Context mContext;

		public UiReceiver(Context c) {
			mContext = c;
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			final String type = intent.getStringExtra("type");
			if (action.equals("refresh") && type.equals("wallpaper")) {
				Log.e("mohl", "======= refresh the wallpaper");
				LEJPConstant.getInstance().mWallpapaerNeedRefresh = true;
				getDataFromLocal(false);
				if (mLocalAdapter == null) {
					mLocalAdapter = new LocalAdapter();
				}
				mLocalAdapter.notifyDataSetChanged();
			}
		}
    }
    

    private void showGridViewLocalContent() {
	Log.e("mohl", "========= showGridViewLocalContent ==== local");
	if (mStartIndex == 0) {
            replaceView();
            if(mLocalAdapter == null){
                mLocalAdapter = new LocalAdapter();
            }
            if(mLocalGrid == null) return;
	    mLocalGrid.setAdapter(mLocalAdapter);
	} else {
            if(mLocalAdapter == null){
                mLocalAdapter = new LocalAdapter();
            }
 	    mLocalAdapter.addMoreContent();
        }
    }
    private void getDataFromLocal(boolean isFirstTime){
    	Log.e("mohl","========= getDataFromLocal");
    	if(!isFirstTime && !LEJPConstant.getInstance().mWallpapaerNeedRefresh){
    		Log.e("mohl","========= getDataFromLocal: no need to refresh");
    		return;
    	}
        if(getActivity() == null) return;
    	SharedPreferences sp = getActivity().getSharedPreferences(SP_CURRENT, 0);
    	LEJPConstant.getInstance().mCurrentWallpaper = sp.getString("current_wallpaper", "");
    	mInitHandler.removeMessages(MSG_GETLOCALLIST);
        mInitHandler.sendMessageDelayed(mInitHandler.obtainMessage(MSG_GETLOCALLIST), 100);
        LEJPConstant.getInstance().mWallpapaerNeedRefresh = false;
    }
    
	private void getLocalWallpaper(){
		Log.e("mohl","========= getLocalWallpaper");
		if(mLocalDataList == null){
			mLocalDataList = new ArrayList<ApplicationData>();
		}else{
			mLocalDataList.clear();
		}
		if (getActivity() == null)
			return;

		getDefaultLocalWallpaper();
		getCustomedLocalWallpaper();
		getInstalledLiveWallpaper();
		SharedPreferences sp_url = getActivity().getSharedPreferences(
				SP_WALLPAPER_URL, 0);
		SharedPreferences sp_icon = getActivity().getSharedPreferences(
				SP_WALLPAPER_ICON_URL, 0);
		SharedPreferences sp_preview = getActivity().getSharedPreferences(
				SP_WALLPAPER_PREVIEW_URL, 0);
		SharedPreferences sp_name = getActivity().getSharedPreferences(
				SP_WALLPAPER_NAME, 0);
		Map<String, String> map_url = (Map<String, String>)sp_url.getAll();
		Map<String, String> map_icon = (Map<String, String>)sp_icon.getAll();
		Map<String, String> map_preview = (Map<String, String>)sp_preview.getAll();
		Map<String, String> map_name = (Map<String, String>)sp_name.getAll();
		
		Iterator<Entry<String,String>> itor = map_url.entrySet().iterator();
		if(itor!=null){
			while(itor.hasNext()){
				Entry<String,String> e = itor.next();
				if(e!=null){
					String key = e.getKey();
					if(key!=null && key.equals(EXCLUDED_SETTING_KEY)){
						continue;
					}
					if(!isDigital(key)){
						continue;
                    }
					String value = e.getValue();
					//added by mohl 2013-2-25; FIX BUG ID:7383 If user delete wallpaper files, 
					//the lejingpin data should be updated
					if(null == value){
						continue;
					}
					File wallpaper = new File(value);
					if(wallpaper == null || !wallpaper.exists()){
						String status = Environment.getExternalStorageState();
						if (status.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
							DetailClassicActivity.deleteLocalItem(getActivity(), key, 10);
							DetailClassicActivity.deleteDownloadDataFlag(getActivity(), key, 10);
							DetailClassicActivity.deleteDownloadData(getActivity(), key, key);
						} 	
						continue;
					}
					if(null == map_preview.get(key)){
						continue;
					}
					File preview = new File(map_preview.get(key));
					if(preview == null || !preview.exists()){
						DetailClassicActivity.deleteLocalItem(getActivity(), key, 10);
						DetailClassicActivity.deleteDownloadDataFlag(getActivity(), key, 10);
						DetailClassicActivity.deleteDownloadData(getActivity(), key, key);
						continue;
					}
					if(null == map_icon.get(key)){
						continue;
					}
					//end
					ApplicationData data = new ApplicationData();
					data.setPackage_name(key);
					data.setUrl(value);
					data.setPreviewAddr(map_preview.get(key));
					File icon = new File(map_icon.get(key));
					if(icon == null || !icon.exists()){
						data.setIconUrl(map_preview.get(key));
					}else{
						data.setIconUrl(map_icon.get(key));
					}
					data.setAppName(map_name.get(key));
					mLocalDataList.add(data);
				}
			}
		}
		
        mLocalDataList.add(getOtherLocalWallpaper());

	}
   private boolean isDigital(String str){
        boolean flag = true;
        try
        {
            Integer.parseInt(str);
        }
        catch(NumberFormatException ex)
        {
            flag = false; 
            return flag;
            //不是数字
        }
        return flag;
    }
	
	private void getInstalledLiveWallpaper() {
		if (getActivity() == null)
			return;
		initLiveWallList();
		if (mLiveWallApkList != null) {
			int listSize = mLiveWallApkList.size();
			Log.e(TAG,
					" 0205 getInstalledLiveWallpaper 000000000000000000the listsize ="
							+ listSize);
			for (int j = 0; j < listSize; j++) {
				ResolveInfo resolveInfo = mLiveWallApkList.get(j);
				try {
					String mPkgName = resolveInfo.serviceInfo.packageName;
					// if(mPkgName.contains("com.vlife.wallpaper") ||
					// mPkgName.contains("com.handpet.wallpaper")){

					SharedPreferences sp_url = getActivity()
							.getSharedPreferences(SP_WALLPAPER_URL, 0);
					SharedPreferences sp_preview = getActivity()
							.getSharedPreferences(SP_WALLPAPER_PREVIEW_URL, 0);
					SharedPreferences sp_name = getActivity()
							.getSharedPreferences(SP_WALLPAPER_NAME, 0);
					String appname = sp_name.getString(mPkgName, null);
					String previewaddr = sp_preview.getString(mPkgName, null);
					String url = sp_url.getString(mPkgName, null);

					// Log.e(TAG,"getInstalledLiveWallpaper 000000000000000000the pkgname ="+mPkgName+" appname====="+appname);
					if (appname != null) {
						ApplicationData data = new ApplicationData();
						data.setPackage_name(mPkgName);
						data.setPreviewAddr(previewaddr);
						data.setAppName(appname);
						data.setUrl(url);
						data.setIsDynamic(1);
						data.setIsNative(false);
						mLocalDataList.add(data);
					} else {
						ApplicationData data = new ApplicationData();
						data.setPackage_name(mPkgName);
						data.setUrl(null);
						data.setAppName(resolveInfo.loadLabel(
								getActivity().getPackageManager()).toString());
						// Log.e(TAG,"getInstalledLiveWallpaper the pkgname ="+data.getAppName()+" pkgname="+mPkgName);
						// data.setthumbdrawable(new
						// SoftReference<Drawable>(getThumbnailFromApk(mPkgName)));
						data.setIsDynamic(1);
						data.setIsNative(false);
						mLocalDataList.add(data);
					}
					// }
				} catch (Exception e) {
					Log.e(TAG,
							"getInstalledLiveWallpaper xxxi error the pkgname ="
									+ e);
					continue;
				}
			}
		}
	}
    private Drawable getThumbnailFromApk(String mPkgName){
                Log.e(TAG,"getInstalledLiveWallpaper the pkgname ="+mPkgName);
        Drawable thumbnail=getActivity().getResources().getDrawable(R.drawable.le_wallpaper_magicdownload_push_app_icon_def);
        //String previewNameString = "livewallpaper";
        String previewNameString = "thumbnail";
        Context mFriendContext = null;
        try {
            mFriendContext = getActivity().createPackageContext(mPkgName, Context.CONTEXT_IGNORE_SECURITY);
        } catch (Exception e) {
            e.printStackTrace();
            return thumbnail;
        }

        if(mFriendContext != null ){
            thumbnail = Utilities.findDrawableByResourceName(previewNameString, mFriendContext);
        }
        return thumbnail;
	}
	
    private void getDefaultLocalWallpaper(){
		int size = mImages.size();
		try {
			for (int i = 0; i < size; i++) {

				ApplicationData data = new ApplicationData();
				data.setIsNative(true);
				data.setPackage_name(mImagesname.get(i));
				data.setAppName(getString(R.string.pick_wallpaper));
				data.setthumbdrawableresid(mThumbs.get(i));
				data.setpreviewdrawableresid(mImages.get(i));
				mLocalDataList.add(data);
			}
		} catch (Exception e) {
		}
    }

	private void getCustomedLocalWallpaper() {
		final long workspaceWaitTime = SystemClock.uptimeMillis();
		loadCustomedWallpaperFromXMLFile();
		String path = "/system/etc/localwallpaper/";
		int size = customedWallpaper.size();
		for (int i = 0; i < size; i++) {
			String packageName = customedWallpaper.get(i);

			ApplicationData data = new ApplicationData();
			data.setPackage_name(packageName);
			data.setIsDelete(true);
			data.setAppName(getString(R.string.pick_wallpaper));
			data.setUrl(path + packageName + ".jpg");
			data.setPreviewAddr(path + packageName + "_small.jpg");
			mLocalDataList.add(data);
		}
		Log.d(TAG, "waited onCreate ="
				+ (SystemClock.uptimeMillis() - workspaceWaitTime)
				+ "ms for previous step to finish binding");
	}

    private ApplicationData getOtherLocalWallpaper(){

        ApplicationData data = new ApplicationData();
        data.setIsNative(true);
        data.setPackage_name("other_wallpaper");
        data.setAppName(getString(R.string.other_wallpaper_name));
        data.setthumbdrawableresid(R.drawable.more_wallpaper);
        return data;
    }



	private void findWallpapers() {
		mThumbs = new ArrayList<Integer>(24);
		mImages = new ArrayList<Integer>(24);
		mImagesname = new ArrayList<String>(24);
		if (getActivity() == null)
			return;

		final Resources resources = getActivity().getResources();
		// Context.getPackageName() may return the "original" package name,
		// com.android.launcher2; Resources needs the real package name,
		// com.android.launcher. So we ask Resources for what it thinks the
		// package name should be.
		final String packageName = resources
				.getResourcePackageName(R.array.wallpapers);

		/* RK_VERSION_WW dining 2012-10-25 S */
		addWallpapers(resources, packageName, R.array.wallpapers);
		/* RK_VERSION_WW dining 2012-10-25 E */
		addWallpapers(resources, packageName, R.array.extra_wallpapers);
	}

    private void addWallpapers(Resources resources, String packageName, int list) {
        final String[] extras = resources.getStringArray(list);
                for (String extra : extras) {
                    Log.e(TAG,"extra ====================="+extra+" packageNmae="+packageName);
                    int res = resources.getIdentifier(extra, "drawable", packageName);
                    if (res != 0) {
                        final int thumbRes = resources.getIdentifier(extra + "_small",
                                "drawable", packageName);

                        if (thumbRes != 0) {
                            mThumbs.add(thumbRes);
                            mImages.add(res);
                            mImagesname.add(extra);
                            // Log.d(TAG, "add: [" + packageName + "]: " + extra + " (" + res + ")");
                        }
                    }
                }

    }
    private static ArrayList<String> customedWallpaper = new ArrayList<String>();

//    public static final String APPFILE = "/data/localwallpaper/customed_wallpaper.xml";
    public void  loadCustomedWallpaperFromXMLFile(){
        final Resources res = getActivity().getResources();
        final String APPFILE = res.getString(R.string.app_file_path);
        if(null == APPFILE){
        	Log.e(TAG,"loadCustomedWallpaperFromXMLFile: app file path is null");
        	return;
        }
        File appFile = new File(APPFILE);
        if (!appFile.exists()) {
            Log.e(TAG,"the file is not exists **********************");
            return ;
        }
        FileInputStream fileStream = null;
        try{
            fileStream = new FileInputStream(appFile);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ;
        }
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder dbu = null;
        try {
            dbu = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException pce) {
        	Log.e(TAG,"newDocumentBuilder error: "+pce.toString());
//            System.out.println(pce.toString());
            return ;
        }
        Document doc = null;
        try {
            doc = dbu.parse(fileStream);
        } catch (Exception e) {
            e.printStackTrace();
            return ;
        }
        customedWallpaper.clear();

        NodeList n1 = doc.getElementsByTagName("wallpaper");
        int n = n1.getLength();
            Log.e(TAG,"the file is not exists in getAppOrderFromXMLFile *************xxn=*********"+n);
        for (int i = 0; i < n; i++) {
            Node my_node = n1.item(i);
            String className = my_node.getAttributes().item(0).getNodeValue();
            //Log.e(TAG,"appwidget pname="+className);
            customedWallpaper.add(className);
        }
    }
	private String getDownloadPath(){
		return LEJPConstant.getDownloadPath() + "/wallpapers";
	}

    private View view0;
    private View emptyView;
    private View view1;
    private View AllView = null;
    private View loadView = null;
    private LayoutInflater mLi ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        Log.e(TAG,"onCreateView");
        if(AllView == null){
            AllView = mLi.inflate(R.layout.android_wallpaper_local, null);
            mInitHandler.removeMessages(MSG_DELAY_CREATEVIEW);
            mInitHandler.sendMessage(mInitHandler.obtainMessage(MSG_DELAY_CREATEVIEW, 100));
        }
        return AllView;
    }
    private void delayCreateView(){
        view0 = mLi.inflate(R.layout.localwallpaper, null);
        loadView = mLi.inflate(R.layout.init_loading,null);
        mLocalGrid = (GridView) view0.findViewById(R.id.local_wallpaper);
        InitViewPager(); 
        this.mInitHandler.post(this.runStartInitService);
       
    }

    Runnable runStartInitService = new Runnable(){
        public void run()
        {
            getDataFromLocal(true);
        }
    };


    private void InitViewPager() {
        mPager = (ViewPager) AllView.findViewById(R.id.vPager);
        listViews = new ArrayList();
        mpAdapter = new MyPagerAdapter();

        listViews.add(loadView);
        mPager.setAdapter(mpAdapter);
    }
    private void replaceView(){
        if(mpAdapter == null) return;
        mpAdapter.destroyItem(mPager, 0,null);
        listViews.clear();
        listViews.add(view0);
        mpAdapter.instantiateItem(mPager, 0);
        mPager.setAdapter(mpAdapter);
        Log.e(TAG,"replaceview listviewsize= "+listViews.size());
    }
    public class MyPagerAdapter extends PagerAdapter {
        public MyPagerAdapter() {
        }
        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(((View)listViews.get(arg1)));
        }
        @Override
        public void finishUpdate(View arg0) {
        }
        @Override
        public int getCount() {
            //Log.e(TAG,"getouCount 2222222222222222 size======="+listViews.size());
            return listViews.size();
        }
        @Override
        public Object instantiateItem(View arg0, int arg1) {
            try {

                ((ViewPager) arg0).addView(((View)listViews.get(arg1)), 0);
            } catch (Exception e) {


            }
            return  listViews.get(arg1);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }
    }
    
    /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: mohl DATE: 2013-1-7 S*/
    private void gatherData(int pos){
		if(null == getActivity()){
			return;
		}
    	switch(pos){
    	case 0:
            Reaper.processReaper(getActivity(), 
             	   "LeJingpin", 
     			   "LocalPaperEntry",
     			   Reaper.REAPER_NO_LABEL_VALUE, 
     			   Reaper.REAPER_NO_INT_VALUE );
    		break;
    	case 1:
    		Reaper.processReaper(getActivity(), 
              	   "LeJingpin", 
      			   "RecommPaperEntry",
      			   Reaper.REAPER_NO_LABEL_VALUE, 
      			   Reaper.REAPER_NO_INT_VALUE );
    		break;
    	default:
    		break;
    	}
    }
    /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: mohl DATE: 2013-1-7 E*/
    

    public class LocalAdapter extends BaseAdapter {
        public LocalAdapter() {
        }
	public View getView(int position, View convertView, ViewGroup parent) {
		final int pos = position;
		// TextView wallpaperview;
		if (convertView == null) {
			if (phoneWidth <= 480) {
				convertView = mLi.inflate(R.layout.local_wallpaper_480item,
						parent, false);
			} else {
				convertView = mLi.inflate(R.layout.local_wallpaper_item,
						parent, false);
//                Log.e("PAD0802","aaaaaaaaaaaaaaaaaaaaaaa="+SettingsValue.getCurrentMachineType(getActivity()));
				if (SettingsValue.getCurrentMachineType(getActivity()) ==0) {
					int screenWidth = getActivity().getWindowManager()
							.getDefaultDisplay().getWidth();
					int width;
					int height;
					if (SettingsValue.isCurrentPortraitOrientation(getActivity())) {
						width = (screenWidth - 14) / 3;
						height = width * 328/394;
						convertView.setLayoutParams(new AbsListView.LayoutParams(width,height));
					} else
						width = (screenWidth - 24) / 5;
						height = width * 328/394;
						convertView.setLayoutParams(new AbsListView.LayoutParams(width,height));
				}
			}
		}
	    final TextView wallpaperview = (TextView) convertView.findViewById(R.id.textname);
	    final ImageView image = (ImageView) convertView.findViewById(R.id.textpic);
	    final ImageView current = (ImageView) convertView.findViewById(R.id.current);
	    ApplicationData mLocalData = mLocalDataList.get(position);
//	    Log.e("mohl", "====11111111getView===== position: " + mLocalData.getIsNative());
		if (!mLocalData.getIsNative()) {
			String iconUrl = mLocalData.previewAddr;
//			Log.e("mohl", "====11111111getView===== position dynamic=: "
//					+ mLocalData.isDynamic + "get=" + iconUrl);
			if (mLocalData.isDynamic == 1 && iconUrl == "") {
				SoftReference<Drawable> thumb = mLocalData
						.getthumbdrawable();
				if (thumb == null || (/*thumb != null && */thumb.get() == null)) {
					String mPkgName = mLocalData.getPackage_name();
					Drawable tmpdraw = getThumbnailFromApk(mPkgName);
					mLocalData.setthumbdrawable(new SoftReference<Drawable>(tmpdraw));
					image.setImageDrawable(tmpdraw);
//					Log.e("mohl", "====11111111getView===== position: "+ position);
				} else {
					image.setImageDrawable(thumb.get());
//					Log.e("mohl", "====222222222getView===== position: "+ position);
				}
			} else{
				/*	String path = getDownloadPath();
					Log.e("mohl", "====getView===== path not exist: " + path); // 提示，但不返回null，否则会引起崩溃
					File dir = new File(path);
					if (!dir.exists()) {
						Log.e("mohl", "====getView===== path not exist: "+ path); // 提示，但不返回null，否则会引起崩溃
						Toast.makeText(getActivity(), R.string.file_access_fail, Toast.LENGTH_LONG).show();
						getActivity().finish();
						return convertView;
					}*/
					if (LEJPConstant.getInstance().mCurrentWallpaper.equals(mLocalData.getUrl())) {
						// 为当前壁纸
						current.setVisibility(View.VISIBLE);
					} else {
						current.setVisibility(View.INVISIBLE);
					}
                   
//					Log.e("0322", "==== 333333333333333 getView===== iconlist not exist: " );
                    if(iconUrl != null && !iconUrl.equals("")){
                    	File f = new File(iconUrl);
                    	if(!f.exists()){
                    		return convertView;
                    	}
                        String pname = mLocalData.getPackage_name();
                        if( mLocalIconList.containsKey(pname)){
                        Drawable tmppic = mLocalIconList.get(pname).get();
                        if(tmppic != null){
//                        	Log.e("0322", "====getView===== soft not null: " );
                            image.setImageDrawable(mLocalIconList.get(pname).get());
                        }else{
//                        	Log.e("0322", "====getView===== soft null: " );
                        	Drawable d = Drawable.createFromPath(iconUrl);
                        	image.setImageDrawable(d);
                        	mLocalIconList.put(pname,new SoftReference<Drawable>(d));
                        }
                    }else{
//                    	Log.e("0322", "====getView===== iconlist not exist: " );
                        Drawable d = Drawable.createFromPath(iconUrl);
                        image.setImageDrawable(d);
                        mLocalIconList.put(pname,new SoftReference<Drawable>(d));
                    }
                    }
                }
                if(mLocalData.isDynamic == 1 ){
                	if (LEJPConstant.getInstance().mCurrentWallpaper.equals(mLocalData.getPackage_name())) {
					// 为当前壁纸
                		current.setVisibility(View.VISIBLE);
                	} else {
                		current.setVisibility(View.INVISIBLE);
                	}
                }
               	wallpaperview.setVisibility(View.INVISIBLE);
            }else{//other wallpaper && native wallpaper
//            	Log.e("mohl", "====aaaaaaaaaagetView===== position: " + position);
                if("other_wallpaper".equals(mLocalData.getPackage_name())){
                	wallpaperview.setVisibility(View.VISIBLE);
                }else{
                	wallpaperview.setVisibility(View.INVISIBLE);
                }
                String pname = mLocalData.getPackage_name();
                if( mLocalIconList.containsKey(pname)){
                    Drawable tmppic = mLocalIconList.get(pname).get();
                    if(tmppic != null){
//                        Log.e("0322", "native ====getView===== soft not null: " );
                        image.setImageDrawable(mLocalIconList.get(pname).get());
                    }else{
//                        Log.e("0322", "native ====getView===== soft null: " );
                        Drawable d = getResources().getDrawable(mLocalData.getthumbdrawableresid());
                        image.setImageDrawable(d);
                        mLocalIconList.put(pname,new SoftReference<Drawable>(d));
                    }
                }else{
                    Log.e("0322", "====getView===== iconlist not exist: " );
                    Drawable d = getResources().getDrawable(mLocalData.getthumbdrawableresid());
                    image.setImageDrawable(d);
                    mLocalIconList.put(pname,new SoftReference<Drawable>(d));
                }
                //image.setImageResource(mLocalData.getthumbdrawableresid());
                //Log.e("mohl","=======derawable== getDefaultLocalWallpaper"+mLocalData.getthumbdrawable().get());
                if (LEJPConstant.getInstance().mCurrentWallpaper.equals(mLocalData.getPackage_name())) {
                	// 为当前壁纸
                	current.setVisibility(View.VISIBLE);
                } else {
                	current.setVisibility(View.INVISIBLE);
                }
                if("other_wallpaper".equals(mLocalData.getPackage_name())){
                if ("OTHER".equals(LEJPConstant.getInstance().mCurrentWallpaper)) {
                	current.setVisibility(View.VISIBLE);
                }
                }
                wallpaperview.setText(mLocalData.getAppName());
            }
			convertView.setTag(mLocalData.getPackage_name());

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d("c", "startWallpaperDetailActivity");
					if(isOnClick){
						return;
					}
					Object tag = v.getTag();
					if (tag instanceof String) {
						String name = (String) tag;
						if ("other_wallpaper".equals(name)) {
							setOtherWallpaperOrientation();
							startWallpaper();
						} else {
							startWallpaperDetailActivity(type_local, pos);
						}
					}
					isOnClick = true;
				}
			});	
			// added by mohl end
			return convertView;
        }
        public void addMoreContent() {
            notifyDataSetChanged();
        }
        public final int getCount() {
        		return (mLocalDataList == null) ? 0 :mLocalDataList.size();
        }
        public final Object getItem(int position) {
        		return mLocalDataList.get(position);
        }
        public final long getItemId(int position) {
            return position;
        }
    }
    public boolean isOnClick = false;
    private void startWallpaperDetailActivity(int type,int pos){
        Intent intent = null;
        mLeConstant.mServiceLocalWallPaperDataList = mLocalDataList;
        intent = new Intent(getActivity(), DetailClassicActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("EXTRA",pos);
        intent.putExtra("TYPEINDEX",type);
        startActivity(intent);
    }
    public void startWallpaper() {
        final Intent pickWallpaper = new Intent(Intent.ACTION_SET_WALLPAPER);
        Intent chooser = Intent.createChooser(pickWallpaper,
                getText(R.string.chooser_wallpaper));
        try{
        startActivity(chooser);
        mLeConstant.mIsClickOtherFlag = true;
        }catch(Exception e){
        }
    }
    private void setOtherWallpaperOrientation(){
    	LEJPConstant mLeConstant = LEJPConstant.getInstance();
    	mLeConstant.mOtherWallpaperOrientation =  this.getResources().getConfiguration().orientation;
    }
    private void addExcludeSettingKey(SharedPreferences sp){
        if(!sp.contains("exclude_from_backup")){
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean(EXCLUDED_SETTING_KEY, true).commit();
        }
    }
    

    public String getConnectType() {
        ConnectivityManager mConnMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mConnMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo infoM = mConnMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if(info != null && info.isConnected()) {
            return "wifi";
        } else if(infoM != null && infoM.isConnected()) { return "mobile"; }
            return "other";
    }

}
