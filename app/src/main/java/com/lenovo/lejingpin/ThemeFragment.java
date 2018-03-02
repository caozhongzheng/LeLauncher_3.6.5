
package com.lenovo.lejingpin;

import com.lenovo.launcher.R;

 
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ProgressBar;
import android.net.Uri;

import android.widget.Button;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import android.view.View.OnClickListener;

import android.content.SharedPreferences;


import android.view.View.OnTouchListener;
import 	android.view.MotionEvent;

import android.os.AsyncTask;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.app.Fragment;
import java.util.Date;

import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import java.util.HashMap;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import java.lang.ref.SoftReference;


import com.lenovo.lejingpin.AsyncImageLoader.ImageCallback;
import com.lenovo.lejingpin.AsyncImageLoader.ImagePathCallback;
import com.lenovo.lejingpin.Holder;
import com.lenovo.lejingpin.network.CategoryRequest5;
import com.lenovo.lejingpin.network.CategoryRequest5.CategoryResponse5;
import com.lenovo.lejingpin.network.AmsSession;
import com.lenovo.lejingpin.network.AmsSession.AmsSessionCallback;
import com.lenovo.lejingpin.network.AmsRequest;
import com.lenovo.lejingpin.network.DeviceInfo;
import com.lenovo.lejingpin.network.WallpaperResponse;
import com.lenovo.lejingpin.network.NetworkHttpRequest;
import com.lenovo.lejingpin.network.NetworkHttpRequest.AmsCallback;
import com.lenovo.lejingpin.network.NetworkHttpRequest.HttpReturn;
import com.lenovo.lejingpin.network.WallpaperResponse.ApplicationData;
import com.lenovo.lejingpin.network.AmsApplication;
 
import android.graphics.Typeface;

import com.lenovo.launcher2.customizer.SettingsValue;

import com.lenovo.launcher2.customizer.Utilities;
import com.lenovo.launcher2.customizer.GlobalDefine;


import android.os.SystemClock;

import com.lenovo.launcher2.customizer.Reaper;
public class ThemeFragment extends Fragment{
 
    public static final String THEME_TYPECODE = "840";
    public static final String LOCK_TYPECODE = "821";

    public static final String HOST_WALLPAPER = "http://launcher.lenovo.com/launcher/lezhuomian.php";
    public static final String TEST_HOST_WALLPAPER = "http://launcher.lenovo.com/boutique/app";
	public final static String SP_THEME_URL = "download_theme_url";
	public final static String SP_THEME_ICON_URL = "download_themeicon_url";
	public final static String SP_THEME_PREVIEW_URL = "download_themepreview_url";
	public final static String SP_THEME_NAME = "download_theme_name";
	public final static String SP_CURRENT = "CURRENT";
	public final static String SP_THEME_LOCAL_PREVIEW = "local_themepreview _url";
	private final static String EXCLUDED_SETTING_KEY = "exclude_from_backup";
    //页卡内容
    private ViewPager mPager;
    // Tab页面列表
    private List listViews; 
    // 动画图片
    private ImageView cursor;
    // 页卡头标
    private RadioButton t1,t2,t3,t4;
    private RadioGroup mToolBar;
    private Matrix matrix3 = new Matrix();
    private Matrix matrix4 = new Matrix();

    // 动画图片偏移量
    private int offset = 0;
    // 当前页卡编号
    private int currIndex = 0;
    // 动画图片宽度
    private int bmpW;
    private final static String TAG = "ThemeFragment";
    private static final int MSG_GETLIST = 100;
    private static final int MSG_CHECKNETWORK = 101;
    private static final int MSG_GETLOCALLIST = 102;
    private static final int MSG_GETOTHERLOCALLIST = 103;
    private static final int MSG_DELAY_CREATEVIEW = 104;
    private MyPagerAdapter mpAdapter;
    private LocalAdapter mLocalAdapter;
    private View loadView;
    private GridView mLocalGrid;
    private HashMap<String, SoftReference<Drawable>> mThemeIconList =
            new HashMap<String, SoftReference<Drawable>>();
/*
    private HashMap<String, Drawable> mThemeIconList =
            new HashMap<String, Drawable>();

*/

    private int maxloadnum = 1;//3;
    private List<Object> mThemes = new ArrayList<Object>();
    private String sDefaultAndroidTheme;

    private List<ResolveInfo> mThemeApkList;
    
    LEJPConstant mLeConstant = LEJPConstant.getInstance();
    private ArrayList<AmsApplication> mLocalDataList; //local data
    private ArrayList<AmsApplication> mFirstLocalDataList; //local data
    private  ArrayList<AmsApplication> mDataList; 

//	private int type = 1;
	private final int type_local = 11;

    int mNum;
    private int mcurtype = 1;
    private int mStartIndex = 0;
    private int mCount = 20;
    private AsyncImageLoader asyncImageLoader;
    
    private Drawable bottom;
    private Drawable nobottom;
    
    private int mCurLoadingLocalLineIndex = 0;
    
    private boolean isReadyForDetail = false;
    public ThemeFragment(int type){
        mcurtype = type;
    }
    public ThemeFragment(){
    }
    /** Called when the activity is first created. */
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         

        mLi = LayoutInflater.from(getActivity());
        Log.d(TAG, "---onCreate---");
        asyncImageLoader = new AsyncImageLoader(getActivity(),1); 
        mThemeIconList.clear();
        //{getDataFromNetwork(true);
        //requestSpecialAppList();
/*
        if (curtype >1 ){
            requestSpecialAppList();
        }else{
            getDataFromLocal(true);
        }
*/
        //mLocalAdapter = new LocalAdapter();
        initThemeList();
        //loadView = (LinearLayout)getActivity().findViewById(R.id.loading);
        final long workspaceWaitTime = SystemClock.uptimeMillis();
        sDefaultAndroidTheme = SettingsValue.getDefaultAndroidTheme(getActivity());
        //bottom = getActivity().getResources().getDrawable(R.drawable.tab_selected_holo_line);
        //nobottom = getActivity().getResources().getDrawable(R.drawable.tab_selected_holo_line_short_no);
        registerDownloadReceiver();
        //this.mInitHandler.post(this.registerService);
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        phoneWidth = metrics.widthPixels;
        Log.e("yumina0330","phonewidth ==================="+phoneWidth);

       Log.d(TAG, "waited onCreate ="
                            + (SystemClock.uptimeMillis()-workspaceWaitTime)
                            + "ms for previous step to finish binding");
       mOrientation = this.getResources().getConfiguration().orientation;
    }

    private int phoneWidth;

    Runnable registerService = new Runnable(){
        public void run()
        {
            registerDownloadReceiver();
        }
    };
    private void initLocalThemeList() {
		mThemes.clear();
		mCurLoadingLocalLineIndex = 0;
		if (getActivity() == null)
			return;
		String defaultTheme = SettingsValue.getDefaultThemeValue(getActivity());
		if (defaultTheme.equals(sDefaultAndroidTheme)) {
			// get the preview images for version_WW
			
			mThemes.add(R.drawable.themepreview);
			
		} else {
			// if (!defaultTheme.equals(sDefaultAndroidTheme)) {
			mThemes.add(defaultTheme);
		}
		List<ResolveInfo> installedSkins = Utilities.findActivitiesForSkin(getActivity());
		if (installedSkins != null) {
			for (ResolveInfo skin : installedSkins) {
				mThemes.add(skin.activityInfo.packageName);
			}
		}
//		Log.d("mohl","====== initLocalThemeList: theme size = "+mThemes.size());
	}
    
    private void getLocalTheme(int lineIndex){
//    	Log.e("mohl","========= getLocalTheme lineIndex="+ lineIndex);
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
//        	Log.e("mohl","========= getLocalTheme endIndex="+ endIndex);
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
    				// get the preview images for version_WW
    				String defaultTheme = SettingsValue.getDefaultThemeValue(getActivity());
    				// Log.e("mohl","========= getLocalTheme default theme="+defaultTheme);
    				
    					/*
    					 * PK_ID:THEME ADPTER ALL DEVICES AUTH:GECN1 DATE:2012-12-7 * S
    					 */
    					data.setpreviewResId(getActivity().getResources().getDrawable(R.drawable.themepreview));
    					data.setpreviewResId(getActivity().getResources().getDrawable(R.drawable.themepreview_1));
//    					data.setpreviewResId(getActivity().getResources().getDrawable(R.drawable.themepreview_2));
    					/*
    					 * PK_ID:THEME ADPTER ALL DEVICES AUTH:GECN1 DATE:2012-12-7 * E
    					 */
    				
    				/* RK_VERSION_WW dining 2012-10-22 E */
    				data.setPackage_name(defaultTheme);
    				data.setAppName(getString(R.string.theme_settings_default_theme));
    			} else if (rawInfo instanceof String) {
    				String[] previewImages = null;
    				String pkgName = rawInfo.toString();
    				String previewNameString = "config_theme_previews";
    				Context mFriendContext = null;
    				try {
    					mFriendContext = getActivity().createPackageContext(pkgName, Context.CONTEXT_IGNORE_SECURITY);
    				} catch (Exception e) {
    					e.printStackTrace();
    					continue;
    				}
    				data.setPackage_name(pkgName);

    				String label = null;
    				int strID = mFriendContext.getResources().getIdentifier("app_name", "string", pkgName);

    				if (pkgName.equals(SettingsValue.getDefaultThemeValue(getActivity()))) {
    					label = getActivity().getString(R.string.theme_settings_default_theme);
    				} else {
    					if (strID == 0) {
    						label = getActivity().getString(R.string.unknow_theme_name);
    					} else {
                                            try{ 
    						label = mFriendContext.getString(strID);
                                            }catch(Exception e){
    						label = getActivity().getString(R.string.unknow_theme_name);
                                            }
    					}
    				}

    				data.setAppName(label);
    				
    				/* PK_ID:LOAD THEME PRVIEW FROME LOCAL CACHE  AUTH:MOHL DATE:2013-1-22	* S */
    				Log.d("mohl","=========== getLocalTheme pkgName = " + pkgName);
    				SharedPreferences sp = getActivity().getSharedPreferences(SP_THEME_LOCAL_PREVIEW, 0);
    				boolean bReLoadPreview = false;
					if (sp.contains(pkgName)) {
						String preview_files = sp.getString(pkgName, "");
						Log.d("mohl","=========== getLocalTheme prview files: "+ preview_files);
						String[] files = preview_files.split(",");
						int ai = 0;
						for (String f : files) {
							File file = new File(f);
							if (file.exists()) {
								if (ai == 0) {
									Drawable pic = Drawable.createFromPath(f);
									data.setpreviewResId(pic);
								}
								ai++;
							} else {
								bReLoadPreview = true;
							}
						}
					}

					if((sp.contains(pkgName) && bReLoadPreview) || !sp.contains(pkgName))
					{
    				previewImages = Utilities.findStringArrayByResourceName(previewNameString, mFriendContext);
    				if (previewImages == null) {
    					String previewName = "themepreview";
    					Log.d("thempaged","ThemePagedView-->syncSingleThemePage():previewName1= " + previewName);
    					Log.d("mohl","getLocalTheme: previewImages is null! ");
    					Drawable brawInfo = Utilities.findDrawableByResourceName("themepreview", mFriendContext);
    					if (brawInfo == null) {
    						brawInfo = getActivity().getResources().getDrawable(R.drawable.ic_launcher_theme_shortcut);
    					}
    					data.setpreviewResId(brawInfo);
    				} else {
    					Log.d("mohl","getLocalTheme: previewImages is not null, length =  " + previewImages.length);
    					String previews = "";
    					for (int j = 0; j < previewImages.length; j++) {
    						String previewName = previewImages[j];
    						Log.d("mohl","getLocalTheme: previewImages j = " + j + ", previewName = " + previewName);
    						Drawable arawInfo = Utilities.findDrawableByResourceName(previewName, mFriendContext);
    						if (arawInfo == null) {
    							arawInfo = getActivity().getResources().getDrawable(R.drawable.ic_launcher_theme_shortcut);
    						}
    						String previewFile = pkgName + "_" + previewName + ".png";
//    						String preview = savePreviewDrawableToFile(arawInfo, previewFile);
    	                                        String preview = getLocalPathToSave(previewFile);
                                                savePicInThread(arawInfo,previewFile);
    						if(!previews.equals("")){
    							previews += "," ;
    						}
    						previews += preview;
    						data.setpreviewResId(arawInfo);
    					}
    					saveLocalPreviewInfo(pkgName, previews);
    				}
    				}
    				/* PK_ID:LOAD THEME PRVIEW FROME LOCAL CACHE  AUTH:MOHL DATE:2013-1-22	* E */
    			}
    			mLocalDataList.add(data);
    		}
        }
    }
    
    private static String getLocalCachePath(){
		return LEJPConstant.getDownloadPath() + "/local_themes";
	}
    
    private static String getLocalPathToSave(String fileName){
        //download icons by mohl
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
    
    private void savePicInThread(final Drawable drawable,final String file){
        new Thread() {
            public void run() {
                savePreviewDrawableToFile(drawable, file);
            }
        }.start();
    }

    private String savePreviewDrawableToFile(Drawable drawable, String fileName){
    	String path = getLocalPathToSave(fileName);
    	File f = new File(path);
    	if(f.exists()){
//    		return path;    //fix bug 12798
    		f.delete();
    	}
    	
//    	else{
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
//    	}
	}
    
    private static void addExcludeSettingKey(SharedPreferences sp){
    	if(!sp.contains(EXCLUDED_SETTING_KEY)){
    		SharedPreferences.Editor editor = sp.edit();
    		editor.putBoolean(EXCLUDED_SETTING_KEY, true).commit();
    	}
    }
    
    private void saveLocalPreviewInfo(String pkgname, String previewFiles){
    	if(getActivity() != null){
    		Log.e("mohl","@@@@@@ saveLocalPreviewInfo: pkg = "+pkgname+", previewfiles = "+previewFiles);
	    	SharedPreferences sp = getActivity().getSharedPreferences(SP_THEME_LOCAL_PREVIEW, 0);
	    	addExcludeSettingKey(sp);
	    	SharedPreferences.Editor editor = sp.edit();
	    	editor.putString(pkgname, previewFiles);
	    	editor.commit();
    	}
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	isOnClick = false;
        Log.d(TAG, "---onResume---");
        Log.d("XXXX", "---Local theme onResume---");
        
        int currentOrientation = this.getResources().getConfiguration().orientation;
        if(currentOrientation != mOrientation){
    		mInitHandler.removeMessages(MSG_DELAY_CREATEVIEW);
            mInitHandler.sendMessage(mInitHandler.obtainMessage(MSG_DELAY_CREATEVIEW, 100));
            mOrientation = currentOrientation;
    	}
        
    	if(mLeConstant.mThemeNeedRefresh && mLeConstant.mIsThemeDeleteFlag){
                if(mLocalAdapter == null) return;
    		mLocalAdapter.notifyDataSetChanged();
    		mLeConstant.mThemeNeedRefresh = false;
    		mLeConstant.mIsThemeDeleteFlag = false;
	if (currIndex == 0 && mLeConstant.mServiceLocalThemeAmsDataList != null && mLeConstant.mServiceLocalThemeAmsDataList.size() == 0){ 
        		if(emptyView != null && mLocalGrid != null){
    				emptyView.setVisibility(View.VISIBLE);
    				mLocalGrid.setVisibility(View.INVISIBLE);
        		}
        	}
    	}
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "---onDestrop---");
    }


    public String getUrl_old() {
        StringBuffer url = new StringBuffer(HOST_WALLPAPER);
        url.append("?r=1")
        .append("&s=").append(mStartIndex).append("&t=").append(mCount)
        .append("&f=id&a=asc");
        url.append("&time=").append(new Date().getTime());
        Log.i("zdx", "WallpaperinfoRequest, url=" + url.toString());
        return url.toString();
    }
    public String getUrl() {

        StringBuffer url = new StringBuffer(TEST_HOST_WALLPAPER);
        //StringBuffer url = new StringBuffer(HOST_WALLPAPER);

        String model = android.os.Build.MODEL;
        model = model.replace(" ","");
        String devinfo = model+android.os.Build.VERSION.RELEASE;
        url.append("?device=").append(devinfo)
        .append("&f=publishDate")
        .append("&a=desc")
        .append("&c=").append(THEME_TYPECODE)
        .append("&s=").append(mStartIndex).append("&t=").append(mCount);
        Log.i("zdx", "WallpaperinfoRequest, url=" + url.toString());
        return url.toString();
    }
    public String getRegUrl() {
        String post = LEJPConstant.getInstance().getPostRegist(getActivity()); 
        StringBuffer url = new StringBuffer(TEST_HOST_WALLPAPER);
        url.append("?").append(post);
        //url.append("?regist=");
        Log.i("zdx", "WallpaperinfoRequest, url=" + url.toString());
        return url.toString();
    }
    private Handler mInitHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_GETLOCALLIST:
            	initLocalThemeList();
            	getLocalTheme(0);
            	showGridViewLocalContent();
            	break;
            case MSG_GETOTHERLOCALLIST:
                lastloadItems(mCurLoadingLocalLineIndex); 
            	break;
            case MSG_GETLIST:
                showGridViewContent();
                break;
            case MSG_CHECKNETWORK:
                if(mDataList == null)
                showErrorView(0);
                break;
            case MSG_DELAY_CREATEVIEW:
               delayCreateView(); 
                break;
            default:
               	break;
            }
        }
    };
    private void lastloadItems(int lineIndex) {
    	Log.e("mohl","======= lastloadItems: lineIndex = " + lineIndex);
        Log.e(TAG,"lastloadItems <F2><F2><F2><F2><F2><F2><F2><F2><F2><F2>");
        
        /***RK_ID:RK_REFRESH AUT:mohl@lenovo.com.DATE: 2013-1-18. S***/
        getLocalTheme(lineIndex);
        /***RK_ID:RK_REFRESH AUT:mohl@lenovo.com.DATE: 2013-1-18. E***/
        
        mLocalAdapter = new LocalAdapter(mLocalDataList);
        mLocalGrid.setAdapter(mLocalAdapter);
        mLocalAdapter.addMoreContent();
        
        /***RK_ID:RK_REFRESH AUT:mohl@lenovo.com.DATE: 2013-1-18. S***/
        if(mThemes != null && mThemes.size() > maxloadnum * (lineIndex + 1)){
        	mCurLoadingLocalLineIndex++;
			mInitHandler.removeMessages(MSG_GETOTHERLOCALLIST);
			mInitHandler.sendMessageDelayed(mInitHandler.obtainMessage(MSG_GETOTHERLOCALLIST), 100);
        }
        /***RK_ID:RK_REFRESH AUT:mohl@lenovo.com.DATE: 2013-1-18. E***/
        if(mThemes != null && mThemes.size() == maxloadnum * (lineIndex + 1)){
        	isReadyForDetail = true;
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().unregisterReceiver(mUiReceiver);
        getActivity().unregisterReceiver(mAPKReceiver);
    }


    
    private UiReceiver mUiReceiver;
    private APKReceiver mAPKReceiver;
    private void registerDownloadReceiver(){
        final long workspaceWaitTime = SystemClock.uptimeMillis();
        final Intent bmainIntent = new Intent(Intent.ACTION_MAIN, null);
        bmainIntent.addCategory(SettingsValue.THEME_PACKAGE_CATEGORY);
        mThemeApkList = getActivity().getPackageManager().queryIntentActivities(bmainIntent, 0);
       Log.d(TAG, "waited onCreate ="+ (SystemClock.uptimeMillis()-workspaceWaitTime)+ "ms for previous step g");
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
    private class APKReceiver extends BroadcastReceiver {
        private Context mContext;
        public APKReceiver(Context c) {
            mContext = c;
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            final String packageName = intent.getData().getSchemeSpecificPart();
            Log.e(TAG,"onRecevieeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
            if(Intent.ACTION_PACKAGE_REMOVED.equals(action) || Intent.ACTION_PACKAGE_ADDED.equals(action)){
            
            Log.e(TAG,"onRecevieeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
                checkThemeAPK(packageName,action);
            }
        }
    }
    private void initThemeList(){
        final Intent bmainIntent = new Intent(Intent.ACTION_MAIN, null);
        bmainIntent.addCategory(SettingsValue.THEME_PACKAGE_CATEGORY);
        mThemeApkList = getActivity().getPackageManager().queryIntentActivities(bmainIntent, 0);
    }
    private void checkThemeAPK(String mPkgName,String action){
            Log.e(TAG,"onRecevieeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee name="+mPkgName);
        if(Intent.ACTION_PACKAGE_ADDED.equals(action)){
            initThemeList();
        if (mThemeApkList != null) {
        int listSize = mThemeApkList.size();
        for (int j = 0; j < listSize; j++) {
            ResolveInfo resolveInfo = mThemeApkList.get(j);
            if (resolveInfo.activityInfo.applicationInfo.packageName.equals(mPkgName))
            {
                mInitHandler.removeMessages(MSG_GETLOCALLIST);
                mInitHandler.sendMessageDelayed(mInitHandler.obtainMessage(MSG_GETLOCALLIST), 0);
                break;
            }
        }
        }
        }
        else{//uninstall theme apk
            removeTheThemeInThread(mPkgName);
        }
    }
    private void removeTheThemeInThread(final String mPkgName){
        new Thread() {
            public void run() {
                removeTheUninstallThemeApk(mPkgName);
            }
        }.start();
    }

    private void removeTheUninstallThemeApk(String mPkgName){
	if (mLeConstant.mServiceLocalThemeAmsDataList != null && mLeConstant.mServiceLocalThemeAmsDataList.size() != 0){ 
	    int listsize = mLeConstant.mServiceLocalThemeAmsDataList.size(); 
            for(int i=0;i<listsize;i++){
                AmsApplication mdata = mLeConstant.mServiceLocalThemeAmsDataList.get(i);
                String pkgName = mdata.getPackage_name();
                if(pkgName != null && pkgName.equals(mPkgName)){
                    Log.e(TAG,"uninstall the theme pkgname="+mPkgName);
                    mLeConstant.mServiceLocalThemeAmsDataList.remove(mdata);
                    break;
                }
            }
        }
       
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
			if (action.equals("refresh") && type.equals("theme")) {
				Log.e("mohl","======= refresh the theme");
				LEJPConstant.getInstance().mThemeNeedRefresh = true;
				if(currIndex == 0){
					getDataFromLocal(false);
				}
				if(mLocalAdapter != null){
					mLocalAdapter.notifyDataSetChanged();
				}
			}
		}
    }
    private boolean mLoadFlag = false;
    private void showGridViewLocalContent() {
        Log.e("mohl", "========= showGridViewLocalContent ==== current line is "+mCurLoadingLocalLineIndex);
        if(mPager == null) return;
        final int curtype = mPager.getCurrentItem();
        replaceView(0);
        if (curtype == 0) {
	    if (mStartIndex == 0) {
			mLocalAdapter = new LocalAdapter(mLocalDataList);
			mLocalGrid.setAdapter(mLocalAdapter);
			if(mThemes != null && mThemes.size() > maxloadnum * (mCurLoadingLocalLineIndex + 1)){
			mCurLoadingLocalLineIndex++;
			mInitHandler.removeMessages(MSG_GETOTHERLOCALLIST);
			mInitHandler.sendMessageDelayed(mInitHandler.obtainMessage(MSG_GETOTHERLOCALLIST), 100);//200);
			}else{
				isReadyForDetail = true;
			}
	    } else {
	    	mLocalAdapter.addMoreContent();
	    }
        }
    }
    private void showGridViewContent(){
/*
        if(mDataList != null && mDataList.size() == 0){
            showErrorView(1);
            return;
        }
        replaceView(1);
        if(mAppsAdapter == null)
        mAppsAdapter = new AppsAdapter();
        mGrid.setAdapter(mAppsAdapter);
        mAppsAdapter.addMoreContent(); 
*/
    }
    private void showErrorView(int flag){
        try{
        progressBarV.setVisibility(View.GONE);
        CharSequence text = getText(R.string.le_list_empty);
        if(flag == 1){
        text = getText(R.string.le_list_empty);
        }else{
        text = getText(R.string.grid_empty_error);
        }
        loading_textV.setText(text.toString());
        loading_refresh.setVisibility(View.VISIBLE);
        }catch(Exception e){
        }

    }
   private void getDataFromLocal(boolean isFirstTime){
        if(getActivity() == null) return;
        Log.e("mohl","========= getDataFromLocal"+isFirstTime+"falag="+LEJPConstant.getInstance().mThemeNeedRefresh);
        if(!isFirstTime && !LEJPConstant.getInstance().mThemeNeedRefresh){
	   		return;
	   	}
        if(getActivity() == null) return;
    	SharedPreferences sp = getActivity().getSharedPreferences(SP_CURRENT, 0);
    	LEJPConstant.getInstance().mCurrentTheme = sp.getString("current_theme", "");
    	mInitHandler.removeMessages(MSG_GETLOCALLIST);
        //mInitHandler.sendMessageDelayed(mInitHandler.obtainMessage(MSG_GETLOCALLIST), 100);
        mInitHandler.sendMessage(mInitHandler.obtainMessage(MSG_GETLOCALLIST));
        LEJPConstant.getInstance().mThemeNeedRefresh = false;
        if(LEJPConstant.getInstance().mCurrentTheme.equals("")){
        	LEJPConstant.getInstance().mCurrentTheme = getCurThemePackageName();
        }
    }
    
    
    private AmsApplication getDefaultLocalTheme(){
	AmsApplication data = new AmsApplication();
        data.setIsNative(true);
       	String defaultTheme = SettingsValue.getDefaultThemeValue(getActivity());
		Log.e("mohl","========= getLocalTheme"+defaultTheme);
	data.setPackage_name(defaultTheme);
        data.setAppName(getString(R.string.theme_settings_default_theme));
	if(LEJPConstant.getInstance().mCurrentTheme.equals("")){
	    LEJPConstant.getInstance().mCurrentTheme = defaultTheme;
        }
    return data;    
    }

	private void getLocalThemeOld(){
		if(mLocalDataList == null){
			mLocalDataList = new ArrayList<AmsApplication>();
		}else{
			mLocalDataList.clear();
		}
		mLocalDataList.add(getDefaultLocalTheme());
		SharedPreferences sp_install = getActivity().getSharedPreferences("InstallStatus", 0);
		SharedPreferences sp_url = getActivity().getSharedPreferences(
				SP_THEME_URL, 0);
		SharedPreferences sp_icon = getActivity().getSharedPreferences(
				SP_THEME_ICON_URL, 0);
		SharedPreferences sp_preview = getActivity().getSharedPreferences(
				SP_THEME_PREVIEW_URL, 0);
		SharedPreferences sp_name = getActivity().getSharedPreferences(
				SP_THEME_NAME, 0);
		Map<String, String> map_install = (Map<String, String>)sp_install.getAll();
		Map<String, String> map_url = (Map<String, String>)sp_url.getAll();
		Map<String, String> map_icon = (Map<String, String>)sp_icon.getAll();
		Map<String, String> map_preview = (Map<String, String>)sp_preview.getAll();
		Map<String, String> map_name = (Map<String, String>)sp_name.getAll();
		for(String key : map_url.keySet()){//循环取得key
			if(key.equals(EXCLUDED_SETTING_KEY)){
				continue;
			}
			if(map_install.keySet().contains(key)){
				AmsApplication data = new AmsApplication();
				data.setPackage_name(key);
	            data.thumbpaths = map_preview.get(key).split(",");
				data.setIcon_addr(map_icon.get(key));
				data.setAppName(map_name.get(key));
				mLocalDataList.add(data);
			}
		}
	}

    private View view0;
    private View emptyView;
    private View view1;
    private View AllView = null;
    private LayoutInflater mLi ;
    private ProgressBar progressBarV;
    private TextView loading_textV;
    private Button loading_refresh;

    private int mLastY=0;
    private void initLoadView(){
         loadView = mLi.inflate(R.layout.init_loading,null);
         progressBarV = (ProgressBar) loadView.findViewById(R.id.progressing);
         loading_textV = (TextView) loadView.findViewById(R.id.loading_text);
         loading_refresh = (Button) loadView.findViewById(R.id.refresh_button);
    }
   

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView ---onScroll---firstVisiblieposi");
		final long workspaceWaitTime = SystemClock.uptimeMillis();
		if (AllView == null) {
			Log.e("mohl","==== onCreateView  AllView is null");
			isReadyForDetail  = false;     //added by mohl for bug 11103; 2013-4-16
			AllView = inflater.inflate(R.layout.fragment_theme, container, false);
			/*
			 * view0 = inflater.inflate(R.layout.localtheme, null); 
			 * mLocalGrid = (GridView) view0.findViewById(R.id.local_theme); 
			 * emptyView = (TextView)view0.findViewById(R.id.empty_textview); 
			 * view1 = inflater.inflate(R.layout.network_theme, null); 
			 * mGrid = (GridView) view1.findViewById(R.id.network_theme);
			 */
			mInitHandler.removeMessages(MSG_DELAY_CREATEVIEW);
			mInitHandler.sendMessage(mInitHandler.obtainMessage(
					MSG_DELAY_CREATEVIEW, 100));
		}else{
			isReadyForDetail  = true;     //added by mohl for bug 11103; 2013-4-16
		}
//		isReadyForDetail  = false;     //added by mohl for bug8840; 2013-3-26
		Log.d(TAG, "waited onCreateView ="
				+ (SystemClock.uptimeMillis() - workspaceWaitTime)
				+ "ms for previous step to finish binding");
		Log.e(TAG, "onViewCreated ThmemFragment datalist========end");
		return AllView;
	}
    private void delayCreateView(){
        initLoadView();
        view0 = mLi.inflate(R.layout.localtheme, null);
        mLocalGrid = (GridView) view0.findViewById(R.id.local_theme);
        InitViewPager();
        this.mInitHandler.post(this.runStartInitService);
    }
    Runnable runStartInitService = new Runnable(){
        public void run()
        {
            getDataFromLocal(true);
        }
    };

    int mOrientation;
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    	if(newConfig.orientation != mOrientation){
    		mInitHandler.removeMessages(MSG_DELAY_CREATEVIEW);
            mInitHandler.sendMessage(mInitHandler.obtainMessage(MSG_DELAY_CREATEVIEW, 100));
            mOrientation = newConfig.orientation;
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
     			   "LocalThemeEntry",
     			   Reaper.REAPER_NO_LABEL_VALUE, 
     			   Reaper.REAPER_NO_INT_VALUE );
    		break;
    	case 1:
    		Reaper.processReaper(getActivity(), 
              	   "LeJingpin", 
      			   "RecommThemeEntry",
      			   Reaper.REAPER_NO_LABEL_VALUE, 
      			   Reaper.REAPER_NO_INT_VALUE );
    		break;
    	default:
    		break;
    	}
    }
    /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: mohl DATE: 2013-1-7 E*/
    
    GridView mGrid;
    TextView tv2 ;
    private void InitViewPager() {
        mPager = (ViewPager) AllView.findViewById(R.id.vPager);
        listViews = new ArrayList();
        mpAdapter = new MyPagerAdapter();
        listViews.add(loadView);
        mPager.setAdapter(mpAdapter);
    }
    private void replaceView(int index){
         
        mpAdapter.destroyItem(mPager, index,null); 
        listViews.clear();
        listViews.add(view0);

        mpAdapter.instantiateItem(mPager, index); 
        mPager.setAdapter(mpAdapter);
        Log.e(TAG,"replaceView 11111111111111111111111 size="+listViews.size());
    }
    /**
      * ViewPager适配器
    */
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
            //((ViewPager) arg0).addView(((View)listViews.get(arg1)), 0);
            //return listViews.get(arg1);
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
    
    private void startWallpaperDetailActivity(int type, int pos) {
        if (type == 1) {
	    mLeConstant.mServiceThemeAmsDataList = mDataList;
	} else if (type == type_local) {
        /*    if(mLocalDataList.size() == 0){
		mLeConstant.mServiceLocalThemeAmsDataList = mFirstLocalDataList;
            }else{
		mLeConstant.mServiceLocalThemeAmsDataList = mLocalDataList;
            }*/
		mLeConstant.mServiceLocalThemeAmsDataList = mLocalDataList;
	}
	Intent intent = new Intent(getActivity(), DetailClassicActivity.class);
	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	intent.putExtra("EXTRA", pos);
	intent.putExtra("TYPEINDEX", type);
	startActivity(intent);
    }

    private String getCurThemePackageName(){
        SharedPreferences sp = getActivity().getSharedPreferences(SettingsValue.PERFERENCE_NAME, 4);
        String curTheme = sp.getString(SettingsValue.PREF_THEME, "DEFAULT THEME");
        LEJPConstant.getInstance().mCurrentTheme = curTheme;
        //Log.i(TAG, "Negative click! isNetworkEnabled ========"+curTheme);
        return curTheme;
   }

   public class LocalAdapter extends BaseAdapter {
        private ArrayList<AmsApplication> mAdapterDataList; //local data
        public LocalAdapter(ArrayList<AmsApplication> items) {
            mAdapterDataList = items; //local data
        }

		public View getView(int position, View convertView, ViewGroup parent) {
			final int pos = position;
			// TextView wallpaperview;
			if (convertView == null) {
                            if(phoneWidth <= 480 ){
				convertView = mLi.inflate(R.layout.local_theme_item, parent,false);
                            }else{
				convertView = mLi.inflate(R.layout.local_theme_480item, parent,false);
                            }
			}
			final TextView wallpaperview = (TextView) convertView
					.findViewById(R.id.textname);
			final ImageView image = (ImageView) convertView
					.findViewById(R.id.textpic);
			final ImageView current = (ImageView) convertView
					.findViewById(R.id.current);

			AmsApplication mLocalData = mAdapterDataList.get(position);
			//Log.e(TAG, "====getView===== position: " + LEJPConstant.getInstance().mCurrentTheme);
                        //data.thumbpaths = (new String[]{map_preview.get(key)}
			//String filePath = iconUrl[0];
                     
                        String mCurrentTheme = getCurThemePackageName();
			//Log.e(TAG, "====getView===== pakcagename: " + mLocalData.getPackage_name()+" cur theme package"+mCurrentTheme+"app name"+mLocalData.getAppName());
		
			if(mCurrentTheme.equals(mLocalData.getPackage_name())){
				//为当前壁纸
                current.setVisibility(View.VISIBLE);
			}else{
				current.setVisibility(View.INVISIBLE);
			}
			
			if (!mLocalData.getIsNative()) {
				String filePath = mLocalData.thumbpaths[0];
				Log.e(TAG, "====getView===== filePath: " + filePath);
				File icon = new File(filePath);
				image.setImageURI(Uri.fromFile(icon));
			} else {
//				image.setScaleType(ImageView.ScaleType.FIT_CENTER);
//				image.setScaleType(ImageView.ScaleType.FIT_XY);
				image.setScaleType(ImageView.ScaleType.CENTER_CROP);
				try {
					image.setImageDrawable((mLocalData.getpreviewResId()).get(0));
				} catch (Exception e) {
					// image.setBackgroundDrawable((mLocalData.getpreviewResId()).get(0));
					Drawable a = getActivity().getResources().getDrawable(R.drawable.lemagicdownload_push_app_icon_def);
					image.setImageDrawable(a);
				}

			}
			wallpaperview.setText(mLocalData.getAppName());
			//Log.e(TAG, "====getView===== position: " + mLocalData.getAppName());

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(isOnClick){
						return;
					}
					isOnClick = true;
					Log.d("c", "startWallpaperDetailActivity");
					if(isReadyForDetail){
						startWallpaperDetailActivity(type_local, pos);
					}
				}
			});
			// added by mohl end
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
   public boolean isOnClick = false;

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
