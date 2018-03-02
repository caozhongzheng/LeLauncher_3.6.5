package com.lenovo.lejingpin;


import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import android.content.ComponentName;
import com.lenovo.lejingpin.AsyncImageLoader.ImageCallback;
import com.lenovo.lejingpin.AsyncImageLoader.ImagePathCallback;

import com.lenovo.lejingpin.network.NetworkHttpRequest;
import com.lenovo.lejingpin.network.NetworkHttpRequest.AmsCallback;
import com.lenovo.lejingpin.network.NetworkHttpRequest.HttpReturn;
import com.lenovo.lejingpin.network.WallpaperResponse.ApplicationData;
import com.lenovo.lejingpin.network.AmsApplication;

import android.service.wallpaper.WallpaperService;
import android.app.WallpaperManager;
import android.app.WallpaperInfo;
import org.xmlpull.v1.XmlPullParserException;

import com.lenovo.launcher2.commonui.LeAlertDialog;
import com.lenovo.launcher2.customizer.Utilities;


import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.provider.Settings;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.pm.ApplicationInfo;
import android.graphics.Paint;
import android.graphics.Color;

import android.text.method.LinkMovementMethod;


import android.content.DialogInterface;

import android.app.Dialog;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.provider.Settings;
import android.widget.Toast;
import android.view.Window;

import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.graphics.Typeface;
import android.text.Spanned;
import android.text.style.StyleSpan;
import 	android.text.util.Linkify;
import  	android.text.style.ClickableSpan;

import android.os.RemoteException;


import com.lenovo.lejingpin.magicdownloadremain.MagicDownloadControl;
import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;

import android.widget.ImageView.ScaleType;

import android.widget.Gallery;

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
import com.lenovo.lejingpin.network.GetImageRequest;
import com.lenovo.lejingpin.network.AppInfoRequest5;
import com.lenovo.lejingpin.network.AppInfoRequest5.AppInfoResponse5;
import com.lenovo.lejingpin.network.AppInfoRequest5.AppInfo;
import com.lenovo.lejingpin.settings.LejingpingSettingsValues;
import com.lenovo.lejingpin.share.download.AppDownloadUrl;
import com.lenovo.lejingpin.share.download.DownloadConstant;
import com.lenovo.lejingpin.share.download.DownloadHandler;
import com.lenovo.lejingpin.share.download.DownloadInfo;
import com.lenovo.lejingpin.share.download.Downloads;
import com.lenovo.lejingpin.share.download.LDownloadManager;
import com.lenovo.lejingpin.hw.lcapackageinstaller.utils.LcaInstallerUtils;
import com.lenovo.lejingpin.hw.ui.Util;


import com.lenovo.launcher.R;
import com.lenovo.lejingpin.DetailClassicViewPagerAdapter;
import com.lenovo.lejingpin.DetailsGallryAdapter;
import com.lenovo.lejingpin.Holder;
import com.lenovo.lejingpin.ViewPagerLayout;
import com.lenovo.lejingpin.hw.content.data.HwConstant;

import com.lenovo.launcher2.customizer.Reaper;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.settings.SeniorSettings;
import com.lenovo.lejingpin.share.download.AppDownloadUrl.Callback;

public class DetailClassicActivity extends Activity implements OnClickListener, OnPageChangeListener, Callback{
    private static String TAG = "DetailClassicActivity";
    private static final int MSG_FINISH = 101;
    private static final int REFRESH_PROGRESS = 102;
    private static final int REFRESH_PROGRESSBAR_BG = 103;
    public static final String EXTRA_THEME_VALUE = "theme_value";
    public static final String ACTION_SHOW_THEME_DLG = "action.show_theme_dlg";
    public static final String LELAUNCHER_PACKAGE_NAME = "com.lenovo.launcher";
    public static final String LELAUNCHER_CLASS_NAME = "com.lenovo.launcher.components.XAllAppFace.XLauncher";
    private static final String LOCK_SETTING_PACKAGE_NAME = "lock_setting_package_name";
    private static final String LOCK_SETTING_CLASS_NAME = "lock_setting_class_name";
    private static final String LOCK_SCREEN_ON_OFF = "lock_screen_on_off";

    public static final String ACTION_DOWNLOAD_DELETE = "com.lenovo.lejingpin.hw.ACTION_DOWNLOAD_DELETE";
    List<ResolveInfo> mList;

    public final static String SP_CURRENT = "CURRENT";
    public final static String VLIFE_MAIN_PACKAGE = "com.vlife.lenovo.wallpaper";
    public final static String ANDROID_WALLPAPER_DOWNLOADURL = "http://www.lenovomm.com/appstore/psl/com.androidesk";
    public final static String LOVE_WALLPAPER_DOWNLOADURL = "http://www.lenovomm.com/appstore/psl/com.lovebizhi.wallpaper";
    public final static String MOXIU_WALLPAPER_DOWNLOADURL = "http://www.lenovomm.com/appstore/psl/com.moxiu.wallpaper";

    private ArrayList<String> downloadPkgList = new ArrayList<String>();

    private ViewPager vp;
    private DetailClassicViewPagerAdapter mDetailClassicViewPagerAdapter;
    private final int SIZE = 10;
    private LEJPConstant mLeConstant = LEJPConstant.getInstance();
    //引导图片资源
    private Gallery gridview;
    //底部小店图片
    private ImageView[] dots ;
    private DetailsGallryAdapter mDetailsGallryAdapter;
    //记录当前选中位置
    private int currentIndex;
    private int mcurrpos = 1;
//    private TextView mdetail_classic_title;
    private TextView mAction_bar_title;
    private TextView mdetail_classic_auther;
    private TextView mdetail_classic_num;
    private TextView mdetail_classic_size;
    private TextView mdetail_classic_link;
    private MyProgressBar mdetail_classic_download;
    private TextView mdetail_classic_waiting;
    private Button mdetail_classic_install;
    private Button mdetail_classic_apply;
    private Button mdetail_classic_edit;
    private RelativeLayout mdetail_classic_layout;
    private LinearLayout livewall_layout;
    private int mtypeindex;
    private String livewallPkgName;
    private boolean liveoutflag = false;
    private int mdot =0;
    private int mDownloadProgressLevel = 0;
    // classname for the locksreen
    private String mClassName;
    private String mPackageName;
    private String mVersionCode;
	private String mIconAddr;
    private String mPreviewAddr;
    private String mAppName;
    private String mAuther;
    private String mAppSize;
    private String mDownloadCount;
    private String mDownloadUrl;
    private UiReceiver mUiReceiver;
    private String mfirstSnapPath;
    private int mDownloadStatus = -1;
    private String mFileName;
    private String mInstallPath;
    private AsyncImageLoader asyncImageLoader;
	private ApplicationData mCurrentAppData;
	private ViewPagerLayout mviewpagerlayouts[];
	private ActionBar mActionBar;
	private MenuItem mActionItem;
	public final static String SP_LOCAL_WALLPAPER = "local_wallpaper";
	public final static String SP_WALLPAPER_URL = "download_wallpaper_url";
	public final static String SP_WALLPAPER_ICON_URL = "download_icon_url";
	public final static String SP_WALLPAPER_PREVIEW_URL = "download_preview_url";
	public final static String SP_WALLPAPER_NAME = "download_wallpaper_name";
	public final static String SP_THEME_URL = "download_theme_url";
	public final static String SP_THEME_ICON_URL = "download_themeicon_url";
	public final static String SP_THEME_PREVIEW_URL = "download_themepreview_url";
	public final static String SP_THEME_LOCAL_PREVIEW = "local_themepreview _url";
	public final static String SP_THEME_NAME = "download_theme_name";
	public final static String SP_LOCKSCREEN_URL = "download_lockscreen_url";
	public final static String SP_LOCKSCREEN_ICON_URL = "download_lockscreen_icon_url";
	public final static String SP_LOCKSCREEN_PREVIEW_URL = "download_lockscreenpreview_url";
	public final static String SP_LOCKSCREEN_NAME = "download_icon_name";
	public final static int REQUEST_CODE_WALLPAPER_PREVIEW = 1;
	public final static int REQUEST_CODE_DELETE_DYNAMIC_WALLPAPER = 2;
	private final static long MIN_DOWNLOAD_SIZE = 10*1024*1024;
	
	private int mOrientation;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		// yangmao add 0121 for fix download-problem
		LDownloadManager.getDefaultInstance(this);
		mLeConstant.mLemActivityonResumeFlag = true;
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        mActionBar = getActionBar();
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_USE_LOGO | ActionBar.DISPLAY_SHOW_HOME |  ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);

        //int titleId = getResources().getSystem().getIdentifier("action_bar_title", "id", "android");
        //TextView titleView = (TextView) findViewById(titleId);
        //titleView.setTextColor(Color.WHITE);

        setContentView(R.layout.detail_classic_activity);
        registerDownloadReceiver();
        registerLocaleChangeReceiver();
        downloadPkgList.clear();
        
        Intent intent = this.getIntent();
        mcurrpos = intent.getIntExtra("EXTRA",0);
        mtypeindex = intent.getIntExtra("TYPEINDEX",0);
        livewallPkgName = intent.getStringExtra("PACKAGENAME");
        if( livewallPkgName != null ){
            calledFromOutSide(livewallPkgName);
       	    if ( mLeConstant.mServiceLocalWallPaperDataList.size() == 0){
                finish();
                return;
            }
        }
        
        if(intent.getSerializableExtra("TYPEDATA") != null){
            if(mtypeindex == 0 ){
                mLeConstant.mServiceWallPaperDataList = (ArrayList<ApplicationData>)intent.getSerializableExtra("TYPEDATA");
            }if(mtypeindex == 1 ){
                mLeConstant.mServiceThemeAmsDataList = (ArrayList<AmsApplication>)intent.getSerializableExtra("TYPEDATA");
            }
        }

        Log.e(TAG,"tmypeindex ======================"+mtypeindex+" curpos="+mcurrpos); 
        initAllView();
        if(!SettingsValue.isRotationEnabled(DetailClassicActivity.this)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        mOrientation = this.getResources().getConfiguration().orientation;;
    }
    
    private void initAllView(){
        mdetail_classic_size = (TextView)findViewById(R.id.detail_classic_size);
        mdetail_classic_link = (TextView)findViewById(R.id.detail_classic_link);
        mdetail_classic_auther = (TextView)findViewById(R.id.detail_classic_auther);
        mdetail_classic_num = (TextView)findViewById(R.id.detail_classic_num);
        mdetail_classic_waiting = (TextView)findViewById(R.id.detail_classic_waiting);
        mdetail_classic_install = (Button)findViewById(R.id.detail_classic_install);
        mdetail_classic_apply = (Button)findViewById(R.id.detail_classic_apply);
        mdetail_classic_edit = (Button)findViewById(R.id.detail_classic_edit);
        livewall_layout = (LinearLayout)findViewById(R.id.livewallpaper_layout);
        mdetail_classic_layout = (RelativeLayout)findViewById(R.id.detail_classic_layout);
        mdetail_classic_download = (MyProgressBar)findViewById(R.id.detail_classic_download);

        vp = (ViewPager) findViewById(R.id.detail_classic_viewpager);
        vp.setVisibility(View.GONE);
        asyncImageLoader = new AsyncImageLoader(this,mtypeindex); 
        mDetailClassicViewPagerAdapter = new DetailClassicViewPagerAdapter(DetailClassicActivity.this,
        		asyncImageLoader,mLeConstant,mtypeindex);
        if(mtypeindex == 10){
           if ( mLeConstant.mServiceLocalWallPaperDataList == null)
           return;
        }

        if(mtypeindex == 0 || mtypeindex == 10){//recommend wallpaper
                mdetail_classic_size.setVisibility(View.GONE);
        	ApplicationData data =null;
			if (mtypeindex == 0) {
				if (mLeConstant.mServiceWallPaperDataList == null){
					return;
				}
				data = mLeConstant.mServiceWallPaperDataList.get(mcurrpos);
			} else{
				data = mLeConstant.mServiceLocalWallPaperDataList.get(mcurrpos);
			}
			mCurrentAppData = data;
			mIconAddr = data.getIconUrl();   //added by mohl
			Log.e(TAG,"tmypeindex ======mIconAddr==============="+mIconAddr); 
            mPreviewAddr = data.getPreviewAddr();
            Log.e(TAG,"tmypeindex ======================"+mPreviewAddr); 
           
            mAuther = data.getAuther();
            mAppSize = data.getApp_size();
            mDownloadCount = String.valueOf(data.getDownload_count());
            mDownloadUrl = data.getUrl();
            Log.e(TAG,"tmypeindex ===========mDownloadUrl==========="+mDownloadUrl); 
            mPackageName = data.getPackage_name();
            mVersionCode = data.getApp_versioncode();
			mAppName = data.getAppName();
			loadNativePreview(1);
			if (mtypeindex == 0) {
				mdetail_classic_install.setVisibility(View.GONE);
			}
            if(mtypeindex == 10){
                if(data.getIsNative() || SettingsValue.getCurrentMachineType(this) > -1){
            	mdetail_classic_install.setVisibility(View.VISIBLE);
                livewall_layout.setVisibility(View.GONE);
                }else{
            	mdetail_classic_install.setVisibility(View.GONE);
                livewall_layout.setVisibility(View.VISIBLE);
                mdetail_classic_edit.setText(R.string.edit_wallpaper);
                }
            }
            changeTheDownloadButStatus();
        }else if(mtypeindex == 1){//recommend theme
        	AmsApplication data =null;
            try{
        		data = mLeConstant.mServiceThemeAmsDataList.get(mcurrpos);
            }catch(Exception e){
                if(data == null) return;
            }
            if(data == null) return;
            mAppName = data.getAppName();
            mAuther = data.getAuther();;
            mAppSize = data.getApp_size();
            mDownloadCount = String.valueOf(data.getDownload_count());
            mPackageName = data.getPackage_name();
            mVersionCode = data.getApp_versioncode();
            if(data.getIsPath()){
            	loadPreview(data.thumbpaths);
            }else{
            	requestAppInfo(mPackageName,mVersionCode);
            }
            mdetail_classic_install.setVisibility(View.GONE);
            mdetail_classic_edit.setVisibility(View.GONE);
            changeTheDownloadButStatus();
		}else if(mtypeindex == 11){
			AmsApplication data = null;
			try {
				data = mLeConstant.mServiceLocalThemeAmsDataList.get(mcurrpos);
			} catch (Exception e) {
				Log.e(TAG, "catch error return <F2><F2><F2><F2><F2>;");
				mcurrpos = 0;
                                if(mLeConstant == null) return;
				if( mLeConstant.mServiceLocalThemeAmsDataList == null) return;
				data = mLeConstant.mServiceLocalThemeAmsDataList.get(0);
				if (data == null)
					return;
			}

			mAppName = data.getAppName();
			mPackageName = data.getPackage_name();
			if (data.getIsNative()) {
				loadNativePreview(2);//modified by mohl for bug 11806,the last preview is not suitable
			} else {
				loadPreview(data.thumbpaths);
			}
        }
        if(mtypeindex == 10 || mtypeindex == 11){
			if (mtypeindex == 10)
				mdetail_classic_install.setText(R.string.apply_wallpaper);
			if (mtypeindex == 11)
				mdetail_classic_install.setText(R.string.apply_themes);
			mdetail_classic_layout.setVisibility(View.GONE);
        }
        if(mtypeindex == 0 || mtypeindex == 1 || mtypeindex == 2){
        	Log.e("mohl","!!!!!!!!!1 registerDownloadCallback");
        	DownloadHandler.getInstance(this).registerDownloadCallback(mPackageName, mVersionCode, this);
        }
        mdetail_classic_install.setOnClickListener(new OnClickListener() {
    	    @Override
    	    public void onClick(View v) {//local objects
               Log.e(TAG," apply new The download status ==========="+mDownloadStatus+" packagenma="+mPackageName);
                applyDownloadData();
    	    }
        });
        //added for the livewall begin 20130227
        mdetail_classic_apply.setOnClickListener(new OnClickListener() {
    	    @Override
    	    public void onClick(View v) {//local objects
               Log.e(TAG," apply new The download status ==========="+mDownloadStatus+" packagenma="+mPackageName);
                applyDownloadData();
    	    }
        });
        mdetail_classic_edit.setOnClickListener(new OnClickListener() {
    	    @Override
    	    public void onClick(View v) {//local objects
               Log.e(TAG," apply new The download status ==========="+mDownloadStatus+" packagenma="+mPackageName);
				if (mCurrentAppData.getIsDynamic() == 1) {
					editCurLiveWallpaper(true);
				} else {
					editCurLiveWallpaper(false);
				}
    	    }
        });
        //added for the livewall end 20130227
        
        mdetail_classic_download.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				boolean pressFlag = false;
				int action = arg1.getAction();
				switch(action){
					case MotionEvent.ACTION_DOWN:
						Log.d("mohl", "mdetail_classic_download onTouch: aciton_down");
						if(1 != mdetail_classic_download.getDownloadStatus() && !pressFlag){ //downloading
							mdetail_classic_download.setBackgroundResource(R.drawable.button_progress);
							mdetail_classic_download.postInvalidate();
						}
						break;
					case MotionEvent.ACTION_UP:
						mdetail_classic_download.setBackgroundResource(R.drawable.button_nomal);
						mdetail_classic_download.postInvalidate();
						break;
					default:
						mdetail_classic_download.setBackgroundResource(R.drawable.button_nomal);
						mdetail_classic_download.postInvalidate();
						break;
				}
				return false;
			}
        	
        });
        mdetail_classic_download.setOnClickListener(mDownloadClickListener);
        mdetail_classic_num.setText(getString(R.string.download_count) + mDownloadCount);
        mdetail_classic_size.setText(getString(R.string.app_size) + mAppSize);
        mdetail_classic_link = (TextView)findViewById(R.id.detail_classic_link);

        LinearLayout downloadLayout = (LinearLayout) findViewById(R.id.detail_classic_android);

        String aa = getString(R.string.auther);
        String bb = getString(R.string.auther);
        if(mCurrentAppData != null ){
            String type = mCurrentAppData.getBgType();
            if(type != null && type.contains("D") ){
                mdetail_classic_auther.setText(getString(R.string.auther) + mAuther);
            }else {
            if( type != null && type.contains("A")){
                bb = getString(R.string.love_wallpaper);
            }
            if( type != null && type.contains("Z")){
                bb = getString(R.string.download_androidesk);
            }
            if( type != null && type.contains("M")){
                bb = getString(R.string.moxiu_wallpaper);
            }
            Log.e(TAG," new The download status type==========="+type+" aa lenght ="+aa.length()+ "bb ==="+bb.length());
            SpannableString ss = new SpannableString(aa+bb);
            ss.setSpan(new StyleSpan(Typeface.BOLD), aa.length(), ss.length(),
                   Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new ClickableSpan(){
            @Override
            public void onClick(View widget)
            {
                showDownloadWallpaperAPK();
            }
            }, aa.length(), ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mdetail_classic_auther.setText(ss);
            mdetail_classic_auther.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }else{
             downloadLayout.setVisibility(View.INVISIBLE);
             mdetail_classic_auther.setText(getString(R.string.auther) + mAuther);
        }

        //初始化Adapter
        //绑定回调
        vp.setOnPageChangeListener(this);
        gridview = (Gallery) findViewById(R.id.detail_classic_grid);

        mDetailsGallryAdapter = new DetailsGallryAdapter(this,mLeConstant,asyncImageLoader);
        mDetailsGallryAdapter.setTypeIndex(mtypeindex);
        if (liveoutflag){ 
            mDetailsGallryAdapter.setOutSideFlag(true);
        }
        mDetailsGallryAdapter.setCurSelectpos(mcurrpos);
        gridview.setAdapter(mDetailsGallryAdapter);
        gridview.setSelection(mcurrpos);
		gridview.setOnItemClickListener(mGridviewClickListener);
        //初始化底部小点
    }

    private OnClickListener mDownloadClickListener = new OnClickListener() {
    	 public void onClick(View v) {
             Log.e(TAG," new The download status ==========="+mDownloadStatus+" packagenma="+mPackageName);
             switch(mDownloadStatus){
             case 1:
            	 cancelDownloadData();
                 mDownloadStatus = -1;
                 mdetail_classic_download.setDownloadStatus(mDownloadStatus);
                 mDownloadProgressLevel = 0;
                 mInitHandler.sendEmptyMessage(REFRESH_PROGRESS);
                 removeDownloadingStatus(DetailClassicActivity.this, mtypeindex, mPackageName);
            	 break;
             case 2:
            	 installDownLoadApk();
            	 break;
             case -1:
            	 if(!isNetworkAvaiable()){
            		   Toast.makeText(DetailClassicActivity.this, R.string.error_network_state,
  								Toast.LENGTH_SHORT).show();
            		   return;
            	   }
            	   if(!Util.getInstance().isNetworkEnabled(DetailClassicActivity.this)){
  	           		    LeAlertDialog dialog = new LeAlertDialog(DetailClassicActivity.this, R.style.Theme_LeLauncher_Dialog_Shortcut);
  	           		    dialog.setLeTitle(R.string.lejingpin_settings_title);
  	           		    dialog.setLeMessage(DetailClassicActivity.this.getText(R.string.confirm_network_open));
  	           		    dialog.setLeNegativeButton(DetailClassicActivity.this.getText(R.string.cancel_action), new DialogInterface.OnClickListener() {
  	           		        public void onClick(DialogInterface dialog, int which) {
  	           		        	dialog.dismiss();
  	           		            Toast.makeText(DetailClassicActivity.this, R.string.version_update_toast, Toast.LENGTH_SHORT).show();
  	           		        }
  	           		    });
  	
  	           		    dialog.setLePositiveButton(DetailClassicActivity.this.getText(R.string.rename_action), new DialogInterface.OnClickListener() {
  	           		        public void onClick(DialogInterface dialog, int which) {
  	           		        	Intent intent = new Intent();
  	           		            intent.setClass(DetailClassicActivity.this, SeniorSettings.class);
  	           		            startActivity(intent);
  	           		            dialog.dismiss();
  	           		        }
  	           		    });
  	           		    dialog.show();
  	           			return;
            	   }
            	   try{
                    //check wifi status
                    if(LEJPConstant.getInstance().isNeedConfirmDownload(DetailClassicActivity.this)){
                    	LejingpingSettingsValues.popupWlanDownloadDialog(DetailClassicActivity.this);
                    }else{
  	                    boolean sdCardExist = Environment.getExternalStorageState()
  								.equals(android.os.Environment.MEDIA_MOUNTED);
  	                    boolean sdCardEnough = (getAvailableExternalMemorySize() > MIN_DOWNLOAD_SIZE) ? true : false;
  	                    if(!(sdCardExist && sdCardEnough)){
  	                    	Toast.makeText(DetailClassicActivity.this, R.string.download_sdcard_not_available,
  									Toast.LENGTH_SHORT).show();
  	                    	return;
  	                    }
						if (mtypeindex == 0) {
							mFileName = "";
							if (mCurrentAppData != null
									&& mCurrentAppData.isDynamic == 1) {
								if (hasLeWallPaperInstalled()) {
									DownloadThumbnails(mPackageName);
								} else {
									showDownloadDialog();
									return;
								}
							} else {
								DownloadThumbnails(false); // 壁纸一开始下载就保存信息，成功以后再写入壁纸地址
							}
						} else if (mtypeindex == 1 || mtypeindex == 2) {
							mFileName = "";
							DownloadThumbnails(mPackageName);
						}
						downloadDataByUrl();
						mDownloadStatus = 1; // change the download to cancel;
						mdetail_classic_download
								.setDownloadStatus(mDownloadStatus);
						mDownloadProgressLevel = 0;
						mInitHandler.sendEmptyMessage(REFRESH_PROGRESS);
						setDownloadingStatus(mPackageName, mDownloadStatus);
						// 保存配置文件信息
					}
                    }catch(Exception e){
                    	Log.e(TAG," new The download status ==========="+mDownloadStatus+" exeption="+e);
                        return;
                    }
            	 break;
             case 0:
            	 applyDownloadData();	
            	 break;
             default:
            	 break;
             }
        } 
          	  
    };
    
    private OnItemClickListener mGridviewClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v,
				int position, long id) {
			if (mcurrpos == position)
				return;
			mcurrpos = position;
			mdetail_classic_waiting.setVisibility(View.VISIBLE);
			vp.setVisibility(View.GONE);
			initDots(0);
			if (mtypeindex == 0) {
				final ApplicationData appdata = mLeConstant.mServiceWallPaperDataList
						.get(position);
				resetDownloadInfo(appdata);
				loadPreview(new String[] { appdata.getPreviewAddr() });
				setTitle(appdata.getAppName());
				String aa = getString(R.string.auther);
				String bb = getString(R.string.auther);
				if (mCurrentAppData != null) {
					String type = mCurrentAppData.getBgType();
					if (type != null && type.contains("D")) {
						mdetail_classic_auther.setText(getString(R.string.auther)+ mAuther);
					} else {
						if (type != null && type.contains("A")) {
							bb = getString(R.string.love_wallpaper);
						}
						if (type != null && type.contains("Z")) {
							bb = getString(R.string.download_androidesk);
						}
						if (type != null && type.contains("M")) {
							bb = getString(R.string.moxiu_wallpaper);
						}
						Log.e(TAG," new The download status type==========="+ type + " aa lenght ="
										+ aa.length() + "bb ==="+ bb.length());
						SpannableString ss = new SpannableString(aa + bb);
						ss.setSpan(new StyleSpan(Typeface.BOLD), aa.length(), ss.length(),
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						ss.setSpan(new ClickableSpan() {
							@Override
							public void onClick(View widget) {
								showDownloadWallpaperAPK();
							}
						}, aa.length(), ss.length(),
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						mdetail_classic_auther.setText(ss);
						mdetail_classic_auther.setMovementMethod(LinkMovementMethod
										.getInstance());
					}
				} else {
					mdetail_classic_auther.setText(getString(R.string.auther) + mAuther);
				}

        	     mdetail_classic_num.setText(getString(R.string.download_count)+String.valueOf(appdata.getDownload_count()));
        	     mdetail_classic_size.setText("  " + getString(R.string.app_size)+appdata.getApp_size());
        	     changeTheDownloadButStatus();
        	    
            }else if(mtypeindex == 1/*||mtypeindex == 2*/){
           	AmsApplication Amsdata =null;
            		Amsdata = mLeConstant.mServiceThemeAmsDataList.get(position);
                resetDownloadInfo(Amsdata);
	                if(Amsdata.getIsPath())
	                		loadPreview(Amsdata.thumbpaths);
	                else
	                	requestAppInfo(Amsdata.getPackage_name(),Amsdata.getApp_versioncode());
                 setTitle(Amsdata.getAppName());
        	     mdetail_classic_auther.setText(getString(R.string.auther)+Amsdata.getAuther());
        	     mdetail_classic_num.setText(getString(R.string.download_count)+String.valueOf(Amsdata.getDownload_count()));
        	     mdetail_classic_size.setText(getString(R.string.app_size)+Amsdata.getApp_size());
        	     changeTheDownloadButStatus();
           }else if(mtypeindex == 10){
        	   final ApplicationData appdata = mLeConstant.mServiceLocalWallPaperDataList.get(position);
				resetDownloadInfo(appdata);
				loadNativePreview(1);
				setTitle(appdata.getAppName());
				updateOptionsMenuStatus();
				mdetail_classic_auther.setVisibility(View.INVISIBLE);
				mdetail_classic_num.setVisibility(View.INVISIBLE);
				mdetail_classic_size.setVisibility(View.INVISIBLE);
				if (appdata.getIsNative() || SettingsValue.getCurrentMachineType(DetailClassicActivity.this) > -1) {
					mdetail_classic_install.setVisibility(View.VISIBLE);
					livewall_layout.setVisibility(View.GONE);
				} else {
	            	mdetail_classic_install.setVisibility(View.GONE);
	                livewall_layout.setVisibility(View.VISIBLE);
	                mdetail_classic_edit.setText(R.string.edit_wallpaper);
				}
           }
           else if (mtypeindex == 11) {
				AmsApplication Amsdata = null;
				try {
					((DetailClassicViewPagerAdapter) vp.getAdapter())
							.clearHashmap();
					Amsdata = mLeConstant.mServiceLocalThemeAmsDataList
							.get(position);
				} catch (Exception e) {
					return;
				}
				resetDownloadInfo(Amsdata);
				if (!Amsdata.getIsNative()) {
					loadPreview(Amsdata.thumbpaths);
				} else {
					loadNativePreview(2);
				}
				setTitle(Amsdata.getAppName());
				updateOptionsMenuStatus();
			}
			changeTheDownloadButStatus();
			Log.i(TAG, "className ============== packagename====="
					+ mPackageName);
			mDetailsGallryAdapter.setCurSelectpos(position);
			mDetailsGallryAdapter.notifyDataSetChanged();
		}
    };
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    	if(newConfig.orientation != mOrientation){
    		initAllView();
            mOrientation = newConfig.orientation;
    	}
    }
   
    private boolean isSDCardAvailable(){
    	return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }
    
    private long getAvailableExternalMemorySize() {
        if (isSDCardAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            Log.e("mohl","========= getAvailableExternalMemorySize: "+availableBlocks * blockSize);
            return availableBlocks * blockSize;
        } else {
            return -1;
        }
    }
    
    private void calledFromOutSide(String mPkgName){
        final Intent bmainIntent = new Intent("action.com.vlife.wallpaper.SET_WALLPAPER_OUTSIDE",null);
        bmainIntent.addCategory("com.vlife.lenovo.intent.category.VLIFE_SET_WALLPAPER");
        List<ResolveInfo> mLiveWallApkList;
        mLiveWallApkList = getPackageManager().queryIntentServices(bmainIntent, 0);
        Log.e(TAG,"calledFromOutSide name="+mPkgName);
        		mLeConstant.mServiceLocalWallPaperDataList = new  ArrayList<ApplicationData>();

        if (mLiveWallApkList != null) {
        int listSize = mLiveWallApkList.size();
        Log.e(TAG,"calledFromOutSide listsize ="+listSize);
        for (int j = 0; j < listSize; j++) {
            ResolveInfo resolveInfo = mLiveWallApkList.get(j);
            try{
            Log.e(TAG,"calledFromOutSide resolveinfo name ="+resolveInfo.serviceInfo.packageName);
            if (mPkgName.equals(resolveInfo.serviceInfo.packageName))
            {
                ApplicationData data = new ApplicationData();
                data.setPackage_name(mPkgName);
                data.setUrl(null);
                data.setAppName(resolveInfo.loadLabel(getPackageManager()).toString());
                Log.e(TAG,"getInstalledLiveWallpaper the pkgname ="+data.getAppName()+" pkgname="+mPkgName);
                data.setthumbdrawable(new SoftReference<Drawable>(getThumbnailFromApk(mPkgName))); 
                data.setIsDynamic(1);
                data.setIsNative(false);
                mLeConstant.mServiceLocalWallPaperDataList.add(data);
                liveoutflag = true;
                break;
            }
            }catch(Exception e){
            }
        }
        }
    }
    
    private void startWallpaperPreview(int type,String path,int resid){
        Intent intent = null;
        intent = new Intent(this, WallpaperPreviewActivity.class);
        intent.putExtra("TYPEINDEX",type);
        intent.putExtra("EXTRAPATH",path);
        intent.putExtra("EXTRARESID",resid);
        intent.putExtra("CURINDEX",mcurrpos);
        startActivityForResult(intent, 1);
    }
    
    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
    	Log.i("XXXX", "onActivityResult: data"+data);
    	if(data == null) return;
        int curpos = data.getIntExtra("currentpos", mcurrpos);  
        if(resultCode == Activity.RESULT_OK){
        	switch(requestCode){
        	case REQUEST_CODE_WALLPAPER_PREVIEW:
        		mcurrpos = curpos;
            	final ApplicationData appdata = mLeConstant.mServiceLocalWallPaperDataList
    					.get(mcurrpos);
            	resetDownloadInfo(appdata);
            	mDetailsGallryAdapter.setCurSelectpos(mcurrpos);
    			mDetailsGallryAdapter.notifyDataSetChanged();
    			setTitle(appdata.getAppName());
    			if(appdata.getIsNative()){
    				loadNativePreview(1);
    				updateOptionsMenuStatus();
    			}else{
    				loadPreview(new String[] { appdata.getPreviewAddr() });
    			}
        		break;
        	case REQUEST_CODE_DELETE_DYNAMIC_WALLPAPER:
        		deleteLocalItem(DetailClassicActivity.this, mPackageName, mtypeindex);
		        deleteDownloadDataFlag(DetailClassicActivity.this, mPackageName, mtypeindex);
        		break;
        	}
        	
        }
    }   

    private Drawable getThumbnailFromApk(String mPkgName){
                Log.e(TAG,"getInstalledLiveWallpaper the pkgname ="+mPkgName);
        Drawable thumbnail=getResources().getDrawable(R.drawable.le_wallpaper_magicdownload_push_app_icon_def);
        String previewNameString = "thumbnail";
        Context mFriendContext = null;
        try {
            mFriendContext = createPackageContext(mPkgName, Context.CONTEXT_IGNORE_SECURITY);
        } catch (Exception e) {
            e.printStackTrace();
                Log.e(TAG,"222222getInstalledLiveWallpaper the pkgname ="+mPkgName);
            return thumbnail;
        }

        if(mFriendContext != null ){
            thumbnail = Utilities.findDrawableByResourceName(previewNameString, mFriendContext);
                Log.e(TAG,"555555555555tInstalledLiveWallpaper the pkgname ="+thumbnail);
        }
        return thumbnail;
    }
    
    private boolean isNetworkAvaiable(){
    	if(DownloadConstant.CONNECT_TYPE_OTHER == DownloadConstant.getConnectType(DetailClassicActivity.this)){
    		return false;
    	}
    	return true;
    }
    
    public boolean isNetworkAllowed(){
        SharedPreferences sp = getSharedPreferences(SettingsValue.PERFERENCE_NAME, 4);
        boolean isNetworkEnabled = sp.getBoolean(SettingsValue.PREF_NETWORK_ENABLER, true);
        return isNetworkEnabled;
    }
    
    private View mCustomView;
	@Override
	protected void onStart() {
		super.onStart();

        // Configure several action bar elements that will be toggled by display options.
		setTitle(mAppName);
/*
		mAction_bar_title.setText(mAppName);
        ActionBar.LayoutParams lp =  (android.app.ActionBar.LayoutParams) mCustomView.getLayoutParams();
        lp.gravity = Gravity.CENTER;
        mActionBar.setCustomView(mCustomView, lp);
*/
	}
	
	@Override 
	protected void onResume(){
		super.onResume();
		Log.d("mohl","========== onResume =========");
		isOnclick = false;
		checkCurrentLiveWall(true);
        Log.e(TAG,"onResume<F2><F2><F2><F2><F2><F2><F2><F2><F2><F2><F2><F2><F2><F2><F2>");
		boolean flag = getDownloadingStatus(mPackageName);
		if(!flag){
			mDownloadStatus = -1;
			if (downloadPkgList.contains(mPackageName)) {
				downloadPkgList.remove(mPackageName);
			} 
			changeTheDownloadButStatus();
		}
	        if(mtypeindex == 0 || mtypeindex == 10){
              	    if (mCurrentAppData == null ){ 
                        Log.e(TAG,"onResume error exception ");
                        finish();
                    }
                }
	}
    
	private LocaleReceiver mLocaleReceiver;
    private void registerLocaleChangeReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);
        filter.addAction("com.lenovo.intent.action.SET_LE_WALLPAPER");
        mLocaleReceiver = new LocaleReceiver();
        registerReceiver(mLocaleReceiver, filter);
    }
    private boolean editflag = false;
    private class LocaleReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_LOCALE_CHANGED.equals(action)) {
               Log.e("yumina","Uirecevier for the language change");
               finish();
            }
            else if("com.lenovo.intent.action.SET_LE_WALLPAPER".equals(action)){
                String mtype = intent.getStringExtra("type");
            String packageName = mCurrentAppData.getPackage_name();
            String mloadUrl = mCurrentAppData.getUrl();
            editflag = true;
            Log.e("yumin0419","action =====0428  =mtype============"+mtype+" name==="+packageName+" mloadUrl="+mloadUrl);
            installLiveWallpaper(packageName);
            if(mCurrentAppData != null && mCurrentAppData.isDynamic == 1){
            setCurrentWallpaper(packageName);
            }
            if(mCurrentAppData != null && mCurrentAppData.isDynamic == 0){
            setCurrentWallpaper(mloadUrl);
            }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    	super.onCreateOptionsMenu(menu); 
    	if(mtypeindex == 10 || mtypeindex == 11 || mtypeindex == 12){
    		String strdelete = getString(R.string.delete_icon_tip);
	        mActionItem = menu.add(strdelete);
	        mActionItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	        mActionItem.setIcon(R.drawable.delete_profile_lejingpin);
	        updateOptionsMenuStatusNew();
    	}
    	return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        updateOptionsMenuStatusNew();
        return super.onPrepareOptionsMenu(menu);
    }

    private void updateOptionsMenuStatus(){
        invalidateOptionsMenu();
    }
    private void updateOptionsMenuStatusNew(){
        if(mtypeindex == 12 ){
    	    if( LEJPConstant.getInstance().mCurrentLockscreen.equals(mPackageName)){
    		mActionItem.setVisible(false);
            }else{
    	        mActionItem.setVisible(true);
            }
    	}
    	else if(mtypeindex == 10 ){
            if(liveoutflag){ 
    		mActionItem.setVisible(false);
                return;
            }
            if(mCurrentAppData == null) return;//for vlife call from outside
    	    if(LEJPConstant.getInstance().mCurrentWallpaper.equals(mDownloadUrl)){
    		mActionItem.setVisible(false);
            }
            else if(  mCurrentAppData.getIsNative()){
    	        mActionItem.setVisible(false);
            }else{
    	        mActionItem.setVisible(true);
            }
            if(/*mCurrentAppData != null && */mCurrentAppData.isDynamic == 1){
    	        if(LEJPConstant.getInstance().mCurrentWallpaper.equals(mPackageName)){
    	            mActionItem.setVisible(false);
                }
            }
             if(mCurrentAppData.getIsDelete()){
                mActionItem.setVisible(false);
            }
        }
        else if(mtypeindex == 11){
            boolean isDefault = mPackageName.equals(SettingsValue.getDefaultThemeValue(this));
            boolean isSystemApp = isSystemApp(mPackageName);
            boolean isInbulidTheme = mPackageName.equals(getPackageName());
    	    Log.e(TAG,"==== mCurrentTheme flag1====== " +isDefault+" flag2====="+isSystemApp +" flag3="+isInbulidTheme); 
            if(!isDefault && !isSystemApp && !isInbulidTheme){
    	        mActionItem.setVisible(true);
            }else {
    	        mActionItem.setVisible(false);
            }
    	    if ( LEJPConstant.getInstance().mCurrentTheme.equals(mPackageName)){
    		mActionItem.setVisible(false);
            }
        }
    	//Log.e(TAG,"==== mCurrentActionItem end visible flag="+mActionItem.isVisible()); 
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home ){
            finish();
            return true;
        }
        String strdelete = getString(R.string.delete_icon_tip);
    	if(item.getTitle().equals(strdelete)){
    		boolean willFinish = false;
    		int newCurrpos = -1;
    		ApplicationData appData = null;
    		if(mtypeindex == 10){
                 if(mCurrentAppData != null && mCurrentAppData.isDynamic == 1){
//    		        deleteLocalItem(DetailClassicActivity.this, mPackageName, mtypeindex);
//    		        deleteDownloadDataFlag(DetailClassicActivity.this, mPackageName, mtypeindex);
					uninstallLocalApp(mCurrentAppData.getPackage_name());
                    return true;
                }
    		    deleteLocalItem(DetailClassicActivity.this, mPackageName, mtypeindex);
    		    deleteDownloadDataFlag(DetailClassicActivity.this, mPackageName, mtypeindex);
//    		    cancelDownloadData();
    		    deleteDownloadData(DetailClassicActivity.this, mPackageName, mPackageName);
    			Log.e("mohl","=== onOptionsItemSelected=== mcurrpos="+mcurrpos);
    			int wallpapaerSize = mLeConstant.mServiceLocalWallPaperDataList.size() -1;
    			int endPos = wallpapaerSize - 1;
    			if(mcurrpos < /*mLeConstant.mServiceLocalWallPaperDataList.size() -1*/endPos){
    				newCurrpos = mcurrpos;
    				gridview.setSelection(mcurrpos);
    			}else if(mcurrpos == /*mLeConstant.mServiceLocalWallPaperDataList.size() -1*/endPos){
    				newCurrpos = mcurrpos-1;
    				gridview.setSelection(mcurrpos-1);
    			}
    			mLeConstant.mServiceLocalWallPaperDataList.remove(mcurrpos);
    			mLeConstant.mWallpapaerNeedRefresh = true;
    			mLeConstant.mIsWallpaperDeleteFlag = true;
    			if(mLeConstant.mServiceLocalWallPaperDataList.size() == 0){
    				willFinish = true;
    			}else{
	    			mcurrpos = newCurrpos;
                                try{
	    			appData = mLeConstant.mServiceLocalWallPaperDataList.get(newCurrpos);
                                }catch(Exception e){
                                    return true;
                                }
	    			if(appData.getIsNative()){
	    				loadNativePreview(1);
	    			}else{
	    				loadPreview(new String[]{appData.getPreviewAddr()});
	    			}
	    			setTitle(appData.getAppName());
	    			resetDownloadInfo(appData);
	    			getDownloadDataFlag(10, appData.getPackage_name());
	    			updateOptionsMenuStatus();
					if (appData.getIsNative() || SettingsValue.getCurrentMachineType(this) > -1) {
						mdetail_classic_install.setVisibility(View.VISIBLE);
						livewall_layout.setVisibility(View.GONE);
					} else {
						mdetail_classic_install.setVisibility(View.GONE);
						livewall_layout.setVisibility(View.VISIBLE);
						mdetail_classic_edit.setText(R.string.edit_wallpaper);
					}

    			}
    		mDetailsGallryAdapter.setCurSelectpos(newCurrpos);
    		mDetailsGallryAdapter.notifyDataSetChanged();
    		}else if(mtypeindex == 11 || mtypeindex == 12){
    			PackageManager pm = getPackageManager();  
    			List<PackageInfo> appinstalled = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS); 
    			for(PackageInfo info: appinstalled){
    				if(info.packageName.endsWith(mPackageName)){
    					uninstallLocalApp(mPackageName);
    				}
    			}
                }
    		if(willFinish){
    			finish();
    		}
    		
    	}
    	return true;
    }
    
    private void uninstallLocalApp(String packageName) {	
        //Log.i(TAG,"UpgradeAppListActivity.uninstallLocalApp");
    	Intent intent = new Intent(Intent.ACTION_DELETE);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("package:" + packageName));
        startActivityForResult(intent, 2);
    }
    public boolean isOnclick = false;
    public void clickToPreview(View v){
    	if(isOnclick)
    		return;
        if(mtypeindex == 10){//only local wallpaper
        if(null != mCurrentAppData && mCurrentAppData.getIsDynamic() == 0 ){
            if(  mCurrentAppData.getIsNative()){
                startWallpaperPreview(0,mCurrentAppData.getPackage_name(),mCurrentAppData.getpreviewdrawableresid());
            }else{
                startWallpaperPreview(1,mCurrentAppData.getUrl(),-1);
            }
        }
        }
        isOnclick = true;
       	Log.d(TAG,"strings[i] onlickkkkkkkkkkkkkkkk<F2><F2><F2><F2><F2>=");
    }
    
    private void loadPreview(String[] strings)
    {
        final int size = strings.length;
        int count = 0;
        if(size<=0)
        	return;
        for(int i=0;i<size;i++){
        	if(!TextUtils.isEmpty(strings[i])){
                    count++;
    		}
    	}
        if(count == 3){       //modified by mohl for bug 12636,caused by bug 12594
        	count = count -1;
        }
//        count = count -1;  ////modified by mohl for bug 12594,the last preview is not suitable
        Log.e("mohl","=== loadPreview: preview count = " + count);
        if(count<=0)
        	return;
        mviewpagerlayouts = new ViewPagerLayout[count];
        for(int i=0;i<count;i++){
        	if(!TextUtils.isEmpty(strings[i])){
        		Log.d("cc","strings[i]="+strings[i]);
		    	mviewpagerlayouts[i] = new ViewPagerLayout(this);
    		}
    	}
        initDots(count);
//        initDots(count - 1); //modified by mohl for bug 11806,the last preview is not suitable
        mdetail_classic_waiting.setVisibility(View.GONE);
        mDetailClassicViewPagerAdapter.setPreviewInfo(mviewpagerlayouts);
        mDetailClassicViewPagerAdapter.setPosition(mcurrpos);
        vp.setAdapter(mDetailClassicViewPagerAdapter);
        vp.setVisibility(View.VISIBLE);
    }
    private void loadNativePreview(int count)
    {
        mviewpagerlayouts = new ViewPagerLayout[count];
        for(int i=0;i<count;i++){
            mviewpagerlayouts[i] = new ViewPagerLayout(this);
        }
        initDots(count);
        mdetail_classic_waiting.setVisibility(View.GONE);
        mDetailClassicViewPagerAdapter.setPreviewInfo(mviewpagerlayouts);
        mDetailClassicViewPagerAdapter.setPosition(mcurrpos);
        vp.setAdapter(mDetailClassicViewPagerAdapter);
        vp.setVisibility(View.VISIBLE);
    }

    private static final int MSG_DELAY_ONRESUME= 100;


    private Handler mInitHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_DELAY_ONRESUME:
                //changeStatusForDelayOnResume();
                break;
            case MSG_FINISH:
	        Intent intent = new Intent();
	        //intent.setClassName(LELAUNCHER_PACKAGE_NAME,LELAUNCHER_CLASS_NAME);
                intent.setComponent(getLeLauncherComponentName());
	        startActivity(intent);
                break;
            case REFRESH_PROGRESS:
            	mdetail_classic_download.setProgress(mDownloadProgressLevel);
            	mdetail_classic_download.postInvalidate();
            	break;
            case REFRESH_PROGRESSBAR_BG:
            	mdetail_classic_download.postInvalidate();
            	break;
            default:
            	break;
            }
        }
    };
    private void killLejingpin(){ 
         Log.i("PAD1021","****************************killLejing");

 com.lenovo.lejingpin.share.util.Utilities.setDownloadActive(false, "com.lenovo.lejingpin.ClassicFragmentActivity");
 com.lenovo.lejingpin.share.util.Utilities.killLejingpinProcessNow();
    }
    private ComponentName getLeLauncherComponentName(){
        final PackageManager packageManager = getPackageManager();
        Intent a = packageManager.getLaunchIntentForPackage(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF);
        Log.e(TAG,"className ======================="+a.toString()+""+a.getComponent());
        return a.getComponent();
    }


    private void delayOnResume(){
        mInitHandler.removeMessages(MSG_DELAY_ONRESUME);
        mInitHandler.sendMessageDelayed(mInitHandler.obtainMessage(MSG_DELAY_ONRESUME), 2000);
    }


    private void changeTheDownloadButStatus(){
        Log.e(TAG,"changeTheDownloadButStatus mPackageName=========="+mPackageName+" mdownloadstatus===="+mDownloadStatus);
        if(mPackageName == null) return;
                if( mtypeindex == 0 && mCurrentAppData != null && mCurrentAppData.isDynamic == 1 ) {
			if (getApkInstalledFlag(mPackageName)) {
                    Log.e(TAG,"11111apply changeTheDownloadButStatus isDynamic=========="+mCurrentAppData.isDynamic);
//					mdetail_classic_download.setText(R.string.data_apply);
                    mDownloadStatus = 0;
                    mdetail_classic_download.setDownloadStatus(mDownloadStatus);
                    mDownloadProgressLevel = 0;
                    mInitHandler.sendEmptyMessage(REFRESH_PROGRESS);
                    return;
                }
		        if (getDownloadDataFlag(mtypeindex, mPackageName) && !getApkInstalledFlag(mPackageName)) {// already download
                    Log.e(TAG,"2222apply changeTheDownloadButStatus isDynamic=========="+mCurrentAppData.isDynamic);
//					mdetail_classic_download.setText(R.string.le_download_install);
                    mDownloadStatus = 2;
                    mdetail_classic_download.setDownloadStatus(mDownloadStatus);
                    mDownloadProgressLevel = 0;
                    mInitHandler.sendEmptyMessage(REFRESH_PROGRESS);
                    return;
                 }
        if(getDownloadingStatus(mPackageName)){
        	if(mDownloadStatus == 1){
//        		mdetail_classic_download.setText(R.string.btn_cancel);
        		mdetail_classic_download.setDownloadStatus(mDownloadStatus);
                mDownloadProgressLevel = 0;
                mInitHandler.sendEmptyMessage(REFRESH_PROGRESS);
        	}
        	return;
        }
			if (downloadPkgList.contains(mPackageName)) {
                    Log.e(TAG,"33333apply changeTheDownloadButStatus isDynamic=========="+mCurrentAppData.isDynamic);
				mDownloadStatus = 1; // change the download to cancel;
//				mdetail_classic_download.setText(R.string.btn_cancel);
			} else {
                    Log.e(TAG,"44444apply changeTheDownloadButStatus isDynamic=========="+mCurrentAppData.isDynamic);
//				mdetail_classic_download.setText(R.string.download_download);
				mDownloadStatus = -1;
             }
			mdetail_classic_download.setDownloadStatus(mDownloadStatus);
            mDownloadProgressLevel = 0;
            mInitHandler.sendEmptyMessage(REFRESH_PROGRESS);
             return;
		}
        if(getDownloadingStatus(mPackageName)){
        	if(mDownloadStatus == 1){
//        		mdetail_classic_download.setText(R.string.btn_cancel);
        		mdetail_classic_download.setDownloadStatus(mDownloadStatus);
                mDownloadProgressLevel = 0;
                mInitHandler.sendEmptyMessage(REFRESH_PROGRESS);
        	}
        	return;
        }

		if (getDownloadDataFlag(mtypeindex, mPackageName)) {// already download
//			mdetail_classic_download.setText(R.string.data_apply);
			mDownloadStatus = 0;
			mdetail_classic_download.setDownloadStatus(mDownloadStatus);
            mDownloadProgressLevel = 0;
            mInitHandler.sendEmptyMessage(REFRESH_PROGRESS);
		} else {
			if (downloadPkgList.contains(mPackageName)) {
				Log.e("mohl","==== downloadPkgList contains "+mPackageName);
				mDownloadStatus = 1; // change the download to cancel;
//				mdetail_classic_download.setText(R.string.btn_cancel);
			} else {
//				mdetail_classic_download.setText(R.string.download_download);
				mDownloadStatus = -1;
			}
			mdetail_classic_download.setDownloadStatus(mDownloadStatus);
            mDownloadProgressLevel = 0;
            mInitHandler.sendEmptyMessage(REFRESH_PROGRESS);
			return;
		}

		if (mtypeindex == 1 || mtypeindex == 2) {
			if (getApkInstalledFlag(mPackageName)) {
//				mdetail_classic_download.setText(R.string.data_apply);
				mDownloadStatus = 0;
			} else {
//				mdetail_classic_download.setText(R.string.le_download_install);
				mDownloadStatus = 2;
			}
			mdetail_classic_download.setDownloadStatus(mDownloadStatus);
            mDownloadProgressLevel = 0;
            mInitHandler.sendEmptyMessage(REFRESH_PROGRESS);
		}
    }
    private void cancelDownloadData(){
    	Log.e("mohl"," cancelDownloadData: mPackageName="+mPackageName+", mVersionCode="+mVersionCode
    			+", mInstallPath="+mInstallPath+", mAppName="+mAppName+", mDownloadUrl="+mDownloadUrl);
    	DownloadInfo downloadInfo;
    	if(mtypeindex == 0){
    		downloadInfo = new DownloadInfo(mPackageName, mPackageName);
    	}else{
    		downloadInfo = new DownloadInfo(mPackageName, mVersionCode);
    	}
		downloadInfo.setInstallPath(mInstallPath);
		downloadInfo.setAppName(mAppName);
		downloadInfo.setDownloadUrl(mDownloadUrl);
		Log.i("yangmao_download","start deleteTask");
		LDownloadManager.getDefaultInstance(this).deleteTask(downloadInfo);
//		notifyDelete(downloadInfo.getPackageName(), downloadInfo.getVersionCode(), mAppName, ACTION_DOWNLOAD_DELETE);
    }
/*    private void notifyDelete(String packageName, String versionCode,String appName ,String action) {
        Intent intent = new Intent(action);
		intent.putExtra(WidgetConstant.EXTRA_PACKAGENAME, packageName);
		intent.putExtra(WidgetConstant.EXTRA_VERSION, versionCode);
		intent.putExtra(WidgetConstant.EXTRA_APPNAME, appName);
		sendBroadcast(intent);
		Log.d(TAG, "DownloadExpandableActivity >>> notifyDelete >>> action : " + action);
    }*/
    public static  void deleteDownloadData(Context context, String pkgname, String versionCode){
    	Log.d("mohl","deleteDownloadData =====pkgname = "+pkgname+", versionCode = "+versionCode);
    	//test by dining 2013-06-24 lejingpin->xlejingpin
		Uri content_uri = Uri.parse("content://com.lenovo.lejingpin.share.download/download");
		Cursor cursor = null ;
		try {
			cursor = context.getContentResolver().query(content_uri, 
				  null, "pkgname = ? and versioncode = ?", new String[] { pkgname, String.valueOf(versionCode) }, null);
			cursor.moveToFirst();
			int _id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
			context.getContentResolver().delete(content_uri, "_id = ? ", new String[]{_id+""});
		}catch(Exception e){
		  	Log.e(TAG,"deleteDownloadData: delete data from database error!!!");
		}
    }

    private boolean getApkInstalledFlag(String pkgname) {
        Log.e(TAG,"getApkInstalledFlag pkgname ==========="+pkgname);
        if(pkgname == null) return false;
        final SharedPreferences preferences = getSharedPreferences("InstallStatus", 0);
        return preferences.getBoolean(pkgname, false);
    }
    private void setApkInstalledFlag(String pkgname) {
        final SharedPreferences preferences = getSharedPreferences("InstallStatus", 0);
        final SharedPreferences.Editor editor = preferences.edit();
		addExcludeSettingKey(preferences);
		Log.e("yumina0226", "========install set the pkgname true= " + pkgname);
		editor.putBoolean(pkgname, true);
		editor.commit();
		copySharedPreferencesToSDCard(DetailClassicActivity.this, "InstallStatus", "InstallStatus");
    }

    private final static String EXCLUDED_SETTING_KEY = "exclude_from_backup";
    private static void addExcludeSettingKey(SharedPreferences sp){
    	if(!sp.contains(EXCLUDED_SETTING_KEY)){
    		SharedPreferences.Editor editor = sp.edit();
    		editor.putBoolean(EXCLUDED_SETTING_KEY, true).commit();
    	}
    }

   private void editCurLiveWallpaper(boolean flag){
       if(!hasLeWallPaperInstalled()){
           showDownloadDialog();
           return;
       }else{
           if ( hasEditLeWallpaperInstalled()){
               Intent intent = new Intent();
               oldLiveCurName = getCurrentWallpaper();
               if(flag){
               intent.setPackage(mPackageName);
               intent.addCategory("com.vlife.lenovo.intent.category.VLIFE_SET_WALLPAPER");
               intent.putExtra("core_package_name", VLIFE_MAIN_PACKAGE);
               intent.putExtra("vlife_action", "edit");
               startService(intent);
               }else{
                   intent.setAction("action.com.vlife.EDIT_STATIC_WALLPAPER");
                   intent.putExtra("image_path",mCurrentAppData.getUrl());
                   intent.setPackage(VLIFE_MAIN_PACKAGE);
                   intent.putExtra("data","wallpaper");
                   startActivity(intent);
                   Log.e(TAG,"edit curreunt wall url="+mCurrentAppData.getUrl());
               }
           }else{
               showDownloadEditDialog();
           }
       }
   }
   private String oldLiveCurName;
   private boolean oldLiveWallFlag = false;
   private void applyDownloadData(){
    	SharedPreferences sp = getSharedPreferences(SP_CURRENT, 0);
    	SharedPreferences.Editor editor = sp.edit();
    	addExcludeSettingKey(sp);
        if(mtypeindex == 0 || mtypeindex == 10){
            if(mCurrentAppData != null && mCurrentAppData.isDynamic == 1){
                if(!hasLeWallPaperInstalled()){
                    showDownloadDialog();
                    return;
                }
            }
        }
        if(mtypeindex == 0){//recommend wallpaper


                LEJPConstant.getInstance().mIsClickOtherFlag = false;
        	String curPkg = mPackageName;
        	SharedPreferences wallpaper_sp = getSharedPreferences(SP_WALLPAPER_URL, 0);
    		String url = wallpaper_sp.getString(curPkg, "");
    		Log.i("mohl", "current wallpaper: " + curPkg + ", url:"+url);
    		if(!url.equals("")){
	            installWallpaper(url);
	            LEJPConstant.getInstance().mWallpapaerNeedRefresh = true;
	            editor.putString("current_wallpaper", url).commit();
    		}
            if(mCurrentAppData != null && mCurrentAppData.isDynamic == 1){
                oldLiveCurName = sp.getString("current_wallpaper","");
	            LEJPConstant.getInstance().mCurrentWallpaper = mCurrentAppData.getPackage_name();
	            editor.putString("current_wallpaper", mCurrentAppData.getPackage_name()).commit();
            }else{
            	if(!url.equals("")){
	            	LEJPConstant.getInstance().mCurrentWallpaper = url;
		            editor.putString("current_wallpaper", url).commit();
            	}
            }
            copySharedPreferencesToSDCard(DetailClassicActivity.this, SP_CURRENT, SP_CURRENT);
        }else if( mtypeindex == 1 || mtypeindex == 11){//recommend theme || local theme
            installAndApplyTheme();
            LEJPConstant.getInstance().mCurrentTheme = mPackageName;
            LEJPConstant.getInstance().mThemeNeedRefresh = true;
            editor.putString("current_theme", mPackageName).commit();
            copySharedPreferencesToSDCard(DetailClassicActivity.this, "current_theme", "current_theme");
            Intent i = new Intent("refresh");
   		    i.putExtra("type", "theme");
            sendBroadcast(i);
        }else if( mtypeindex == 2 || mtypeindex == 12){//recommend lockscreen || local theme
            mClassName = findLiveWallpapers(mPackageName); 
            Log.i(TAG, "className ==============="+mClassName+" packagename====="+mPackageName);
            installAndLockScreen();
            LEJPConstant.getInstance().mCurrentLockscreen = mPackageName;
            LEJPConstant.getInstance().mLockscreenNeedRefresh = true;
            editor.putString("current_lockscreen", mPackageName).commit();
            copySharedPreferencesToSDCard(DetailClassicActivity.this, "current_lockscreen", "current_lockscreen");
            Intent i = new Intent("refresh");
   		    i.putExtra("type", "lockscreen");
            sendBroadcast(i);
        }else if(mtypeindex == 10){//local wallpaper
            LEJPConstant.getInstance().mWallpapaerNeedRefresh = true;
            LEJPConstant.getInstance().mIsClickOtherFlag = false;
            if(!mCurrentAppData.getIsNative()){
				if (mCurrentAppData != null && mCurrentAppData.isDynamic == 1) {
					oldLiveCurName = sp.getString("current_wallpaper", "");
					LEJPConstant.getInstance().mCurrentWallpaper = mCurrentAppData.getPackage_name();
					editor.putString("current_wallpaper",mCurrentAppData.getPackage_name()).commit();
				} else {
					LEJPConstant.getInstance().mCurrentWallpaper = mDownloadUrl;
					editor.putString("current_wallpaper", mDownloadUrl).commit();
				}
	            installWallpaper(mDownloadUrl);
            }else{
	            LEJPConstant.getInstance().mCurrentWallpaper = mCurrentAppData.getPackage_name();
	            installNativeWallpaper(mCurrentAppData.getPackage_name()); 
	            editor.putString("current_wallpaper",mCurrentAppData.getPackage_name()).commit();
            }
            copySharedPreferencesToSDCard(DetailClassicActivity.this, SP_CURRENT, SP_CURRENT);
   		    Intent i = new Intent("refresh");
   		    i.putExtra("type", "wallpaper");
            sendBroadcast(i);
         }
    }

   public static void setCurrentWallpaper(Context context, String wallpaper_name){
	   SharedPreferences sp = context.getSharedPreferences(SP_CURRENT, 0);
   	   SharedPreferences.Editor editor = sp.edit();
   	   addExcludeSettingKey(sp);
       Log.e("DetailClassicActivity","setCurrentWallpaper packagename="+wallpaper_name);
   	   editor.putString("current_wallpaper", wallpaper_name).commit();
   	   copySharedPreferencesToSDCard(context, SP_CURRENT, SP_CURRENT);
   }
   public static void clearCurrentWallpaper(Context context){
	   SharedPreferences sp = context.getSharedPreferences(SP_CURRENT, 0);
   	   SharedPreferences.Editor editor = sp.edit();
       Log.e("DetailClassicActivity","clear currentWallpaper packagename=");
   	   editor.remove("current_wallpaper").commit();
   	   copySharedPreferencesToSDCard(context, SP_CURRENT, SP_CURRENT);
   }
   
	private void installDownLoadApk() {
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	if (sdCardExist) {
            if(mCurrentAppData != null && mCurrentAppData.isDynamic == 1){
                LcaInstallerUtils.installApplication(this, mFileName,
                		DownloadConstant.CATEGORY_LIVE_WALLPAPER | DownloadConstant.CATEGORY_LENOVO_APK,mPackageName,mVersionCode);
                return;
            }
			Log.i(TAG, "== installDownLoadApk: mPackageName = " + mPackageName
					+ ", mVersionCode = " + mVersionCode + ", InstallPath = "
					+ mFileName + ", mAppName = " + mAppName
					+ ", mDownloadUrl = " + mDownloadUrl);
			DownloadInfo downloadInfo = new DownloadInfo(mPackageName, mVersionCode);
			downloadInfo.setInstallPath(mFileName);
			downloadInfo.setAppName(mAppName);
			downloadInfo.setDownloadUrl(mDownloadUrl);
//			downloadInfo.setCategory("nowallpaper");
			downloadInfo.setCategory(DownloadConstant.CATEGORY_LENOVO_APK);
			LcaInstallerUtils.installApplication(this, downloadInfo);
		} else{
			Toast.makeText(this, R.string.check_sdcard_status_result, Toast.LENGTH_SHORT).show();
		}
			/*else {
			File file = new File("storage/sdcard0/TestDownload/download/uc.apk");
			Uri fileUri = Uri.fromFile(file);
			Log.i(TAG, "LcaInstallerService, not install directly...uri:"
					+ fileUri);
			Intent intent = new Intent("android.intent.action.VIEW");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(fileUri,
					"application/vnd.android.package-archive");
			try {
				startActivity(intent);
			} catch (Exception e) {
				Log.e("THEME",
						"LcaInstallerService, not install directly...uri:" + e);
			}
		}*/
	}

	private void installNativeWallpaper(String picname) {
		Log.d(TAG, "installNativeWallpaper: " + picname);
		int size = mLeConstant.mServiceLocalWallPaperDataList.size();
		for (int i = 0; i < size; i++) {
			ApplicationData data = mLeConstant.mServiceLocalWallPaperDataList
					.get(i);
			if (data.getIsNative()) {
				if (data.getPackage_name().equals(picname)) {
					Toast.makeText(this,
							R.string.theme_applying_wallpaper_message,
							Toast.LENGTH_SHORT).show();
					try {
						WallpaperManager wpm = (WallpaperManager) this
								.getSystemService(Context.WALLPAPER_SERVICE);
						wpm.setResource(data.getpreviewdrawableresid());

					} catch (IOException e) {
						Toast.makeText(this,
								R.string.wallpaper_insall_fail_toast,
								Toast.LENGTH_SHORT).show();
						Log.e(TAG, "Failed to set wallpaper: " + e);
						return;
					}
					Toast.makeText(DetailClassicActivity.this,
							R.string.wallpaper_insall_success,
							Toast.LENGTH_SHORT).show();
					
					/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: mohl DATE: * 2013-1-7 S */
					Reaper.processReaper(this, "LeJingpin", "LocalPaperApply",
							data.getAppName(),
							Reaper.REAPER_NO_INT_VALUE);
					/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: mohl DATE: * 2013-1-7 E */
					/*Intent intent = new Intent();
					intent.setClassName(LELAUNCHER_PACKAGE_NAME,
							LELAUNCHER_CLASS_NAME);
					startActivity(intent);
					 */handleGetMessage(MSG_FINISH);
				}
			}
		}
	}
    private void setLeWallpaper(){
        if(VLIFE_MAIN_PACKAGE.equals(getCurrentLiveWallpaperPkgName())) return;
        List<ResolveInfo> list = getPackageManager().queryIntentServices(
                new Intent(WallpaperService.SERVICE_INTERFACE),
                PackageManager.GET_META_DATA);
        for (ResolveInfo resolveInfo : list) {
            if (VLIFE_MAIN_PACKAGE.equals(resolveInfo.serviceInfo.packageName)){

                Intent intent = new Intent(WallpaperService.SERVICE_INTERFACE);
                intent.setClassName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name);

        try {
            WallpaperManager.getInstance(this).getIWallpaperManager().setWallpaperComponent(intent.getComponent());
        } catch (Exception e) {
           Toast.makeText(this, R.string.wallpaper_insall_fail_toast, Toast.LENGTH_SHORT).show();
           Log.w(TAG, "Failure setting wallpaper", e);
        }
        }
    }
    }

    private String getCurrentWallpaper(){
        SharedPreferences sp = getSharedPreferences(SP_CURRENT, 0);
//        SharedPreferences.Editor editor = sp.edit();
        String  curName = sp.getString("current_wallpaper","");
        return curName;
    }

    private String getCurrentLiveWallpaperPkgName(){
        String pkgname = null;
        WallpaperManager  mWallpaperManager = WallpaperManager.getInstance(this);
        WallpaperInfo mWallpaperInfo = mWallpaperManager.getWallpaperInfo();
        if(mWallpaperInfo != null){
            pkgname = mWallpaperInfo.getPackageName();
        }
        return pkgname;
    }

   public void setCurrentWallpaper(String wallpaper_name){
	   SharedPreferences sp = getSharedPreferences(SP_CURRENT, 0);
   	   SharedPreferences.Editor editor = sp.edit();
   	   Log.e("DetailClassicActivity"," aaaaaaaaaa setCurrentWallpaper packagename="+wallpaper_name);
   	   editor.putString("current_wallpaper", wallpaper_name).commit();
   	   copySharedPreferencesToSDCard(DetailClassicActivity.this, SP_CURRENT, SP_CURRENT);
   }
    private void startLiveWallpaper(String pkgName){
        setLeWallpaper();
        setCurrentWallpaper(pkgName);
        Log.w(TAG, " 030999999999999999999e setting wallpaper editflag ="+editflag);
        if (!editflag){ 
        Intent intent = new Intent();
        intent.setPackage(pkgName);
        intent.addCategory("com.vlife.lenovo.intent.category.VLIFE_SET_WALLPAPER");
        intent.putExtra("core_package_name",VLIFE_MAIN_PACKAGE);
        startService(intent);
        editflag = false;
        }
    }
    private void installLiveWallpaper(String packageName){
        if(packageName == null) return;
        int bb =  getPackageManager().checkPermission("android.permission.SET_WALLPAPER_COMPONENT",getPackageName());
        Log.w(TAG, " 03088888888888 Failure setting wallpaper bb ======="+bb+" packagename ="+getPackageName()+" flag="+editflag);
        //if( !isSystemApp("com.lenovo.launcher") ) {
        if( bb == PackageManager.PERMISSION_DENIED ) {
            if (!editflag){ 
            Intent intent = new Intent();
            intent.setPackage(packageName);
            intent.addCategory("com.vlife.lenovo.intent.category.VLIFE_SET_WALLPAPER");
            intent.putExtra("core_package_name",VLIFE_MAIN_PACKAGE);
            startService(intent);
            editflag = false;
            }
            if(!VLIFE_MAIN_PACKAGE.equals(getCurrentLiveWallpaperPkgName())){ 
            Intent liveIntent = new Intent("android.service.wallpaper.LIVE_WALLPAPER_CHOOSER");
            try {
                oldLiveWallFlag = true;
                Toast.makeText(this, R.string.click_lewallpaper, Toast.LENGTH_LONG).show();
                startActivity(liveIntent);
            }catch(Exception e){
                Toast.makeText(this, R.string.wallpaper_insall_fail_toast, Toast.LENGTH_SHORT).show();
            }
            return;
            }
        }
        Toast.makeText(this, R.string.theme_applying_wallpaper_message, Toast.LENGTH_SHORT).show();
        startLiveWallpaper(packageName);
        //Toast.makeText(DetailClassicActivity.this,R.string.wallpaper_insall_success, Toast.LENGTH_SHORT).show();
	/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: mohl DATE: * 2013-1-7 S */
	Reaper.processReaper(this, "LeJingpin", "LocalPaperApply",
			mCurrentAppData.getAppName(),
			Reaper.REAPER_NO_INT_VALUE);
	/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: mohl DATE: * 2013-1-7 E */
        handleGetMessage(MSG_FINISH); 
        Log.i(TAG, "install live wallpaper className ==============="+packageName );
    }
    private void checkCurrentLiveWall(boolean flag){
		if (oldLiveWallFlag) {
			oldLiveWallFlag = false;
			if (!VLIFE_MAIN_PACKAGE.equals(getCurrentLiveWallpaperPkgName())) {
				LEJPConstant.getInstance().mCurrentWallpaper = oldLiveCurName;
				SharedPreferences sp = getSharedPreferences(SP_CURRENT, 0);
				SharedPreferences.Editor editor = sp.edit();
				addExcludeSettingKey(sp);
				editor.putString("current_wallpaper", oldLiveCurName).commit();
				copySharedPreferencesToSDCard(DetailClassicActivity.this, SP_CURRENT, SP_CURRENT);
			} else {
				if (flag) {
					Toast.makeText(this,
							R.string.theme_applying_wallpaper_message,
							Toast.LENGTH_SHORT).show();
					handleGetMessage(MSG_FINISH);
				}
			}
		}
    }
    private void installWallpaper(String filename) {
        Log.i(TAG, "wallpaper className ==============="+filename+" curdata=========="+mCurrentAppData);
        if(mCurrentAppData != null && mCurrentAppData.isDynamic == 1){
                 Log.i(TAG, "live wallpaper className ==============="+mCurrentAppData.getPackage_name());
                installLiveWallpaper(mCurrentAppData.getPackage_name());
                return;
        }else{
        Toast.makeText(this, R.string.theme_applying_wallpaper_message, Toast.LENGTH_SHORT).show();
        File inFile = null;
        try{
        inFile = new File(filename);
        }catch(Exception e){
            return;
        }
        FileInputStream inStream = null;
        try {
            inStream = new FileInputStream(inFile);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, R.string.wallpaper_insall_fail_toast, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            WallpaperManager wpm = (WallpaperManager)this.getSystemService(
                            Context.WALLPAPER_SERVICE);
            
            wpm.setStream(inStream);
        } catch (IOException e) {
            Toast.makeText(this, R.string.wallpaper_insall_fail_toast, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return;

        }
        }
		/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: mohl DATE: * 2013-1-7 S */
		Reaper.processReaper(this, "LeJingpin", "LocalPaperApply",
				mCurrentAppData.getAppName(),
				Reaper.REAPER_NO_INT_VALUE);
		/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: mohl DATE: * 2013-1-7 E */
        Toast.makeText(DetailClassicActivity.this,R.string.wallpaper_insall_success, Toast.LENGTH_SHORT).show();
        handleGetMessage(MSG_FINISH); 
    }
    private void installAndApplyTheme(){
        Intent intent = new Intent(ACTION_SHOW_THEME_DLG);
        intent.putExtra(EXTRA_THEME_VALUE, mPackageName);
        Log.i(TAG,"start  apply theme"+mPackageName);
        //intent.setClassName(LELAUNCHER_PACKAGE_NAME,LELAUNCHER_CLASS_NAME);
        intent.setComponent(getLeLauncherComponentName());
        startActivity(intent);
        
      	/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: mohl DATE: 2013-1-7 S*/
        Reaper.processReaper( this, 
        	   "LeJingpin", 
			   "LocalThemeApply",
			   mPackageName, 
			   Reaper.REAPER_NO_INT_VALUE );
    killLejingpin(); 
        /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: mohl DATE: 2013-1-7 E*/     
    }

    private String findLiveWallpapers(String mPkgName) {
        String mClsName = null;
        PackageManager mPackageManager = getPackageManager();
        mList = mPackageManager.queryIntentActivities(
                new Intent("android.service.lock"),
                PackageManager.GET_META_DATA);

        List<ResolveInfo> mFakeList = mPackageManager.queryIntentActivities(
                new Intent("android.service.fakelock"),
                PackageManager.GET_META_DATA);

        mList.addAll(mFakeList);
        int listSize = mList.size();
        for (int i = 0; i < listSize; i++) {
            ResolveInfo resolveInfo = mList.get(i);
            if (resolveInfo.activityInfo.applicationInfo.packageName.equals(mPkgName))
            {
                mClsName = resolveInfo.activityInfo.name;
                return mClsName;
            }
        }
        return mClsName;
    }

    private void installAndLockScreen(){
        Settings.System.putInt(getContentResolver(),LOCK_SCREEN_ON_OFF, 1);
        Toast.makeText(this, R.string.theme_set_screenlock_success, Toast.LENGTH_SHORT).show();
        Settings.System.putString(getContentResolver(), LOCK_SETTING_PACKAGE_NAME, mPackageName);
        Settings.System.putString(getContentResolver(), LOCK_SETTING_CLASS_NAME, mClassName);
      	/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: mohl DATE: 2013-1-7 S*/
        Reaper.processReaper( this, 
        	   "LeJingpin", 
			   "LocalLKScreenApply",
			   mPackageName, 
			   Reaper.REAPER_NO_INT_VALUE );
        /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: mohl DATE: 2013-1-7 E*/  

    }
    
    private boolean getDownloadingStatus(String pkgname){
    	final SharedPreferences preferences = getSharedPreferences("DownloadingStatus", 0);
    	int status = preferences.getInt(pkgname, -9);
    	if(status == -9){
    		Log.d("mohl","========= getDownloadingStatus: false, pkgname = "+pkgname);
    		return false;
    	}else{
    		mDownloadStatus = status;
    		Log.d("mohl","========= getDownloadingStatus: true, pkgname = "+pkgname);
    		return true;
    	}
    }
    
	private void setDownloadingStatus(String pkgname, int status) {
		SharedPreferences sp = getSharedPreferences("DownloadingStatus", 0);
		SharedPreferences.Editor editor = sp.edit();
		addExcludeSettingKey(sp);
		editor.putInt(pkgname, status).commit();
	}

    public static void removeDownloadingStatus(Context context, int type, String pkgname){
    	Log.e("mohl","====== removeDownloadingStatus: type = "+type+", pkgname = "+pkgname);
    	final SharedPreferences preferences = context.getSharedPreferences("DownloadingStatus", 0);
    	final SharedPreferences.Editor editor = preferences.edit();
    	addExcludeSettingKey(preferences);
    	editor.remove(pkgname).commit();	
    }
    
    public static String getDownloadPkgType(Context context, String pkgname){
    	final SharedPreferences preferences = context.getSharedPreferences("DOWNLOAD", 0);
        String keytheme = "themepkgname";
        String keylock = "lockpkgname";
        String keywallpaper = "wallpaperpkgname";
        String listtheme = preferences.getString(keytheme,"");
        String listlock = preferences.getString(keylock, "");
        String listwallpaper = preferences.getString(keywallpaper, "");
        Log.e("DetailClassicActivity","getDownloadDataFlag listwallppaer="+listwallpaper+" contains flag"+isContains(listwallpaper, pkgname));
        if(listtheme != null && isContains(listtheme, pkgname)){
            return "theme";
        }else if(listlock != null && isContains(listlock, pkgname)){
        	return "lockscreen";
        }else if(listwallpaper != null && isContains(listwallpaper, pkgname)){
        	return "wallpaper";
        }else{
        	return "";
        }
    }
    private static boolean isContains(String list, String item){
    	if(list.equals("") || item.equals("")){
    		return false;
    	}
    	if(list.contains(item)){
    		String[] pkgs = list.split(",");
    		for(int pos = 0; pos < pkgs.length; pos ++){
    			if(item.equals(pkgs[pos])){
    				return true;
    			}
    		}
    		return false;
    	}else{
    		return false;
    	}
    }
    
    private boolean getDownloadDataFlag(int type,String pkgname) {
        final SharedPreferences preferences = getSharedPreferences("DOWNLOAD", 0);
        String listname;
        String keyvalue = "themepkgname";
        if(type == 1 || type == 11){//theme
            keyvalue = "themepkgname";
        }else if(type == 2 || type == 12){//lock
            keyvalue = "lockpkgname";
        }else if(type == 0 || type == 10){//wallpaper
            keyvalue = "wallpaperpkgname";
        }
        listname = preferences.getString(keyvalue,"");
        if(listname != null){
            if(isContains(listname, pkgname)){
            	mFileName = preferences.getString(pkgname,"");
                Log.e(TAG,"pkgname true======================="+pkgname);
                return true;
            }
        }
                Log.e(TAG,"pkgname false======================="+pkgname);
        return false;
    }
    public static void setDownloadDataFlag(Context context, int type,String pkgname,String filename) {
        final SharedPreferences preferences = context.getSharedPreferences("DOWNLOAD", 0);
        final SharedPreferences.Editor editor = preferences.edit();
        addExcludeSettingKey(preferences);
        String keyvalue = "themepkgname";
        if(type == 1 || type == 11){//theme
            keyvalue = "themepkgname";
        }else if(type == 2 || type == 12){//lock
            keyvalue = "lockpkgname";
        }else if(type == 0 || type == 10){//wallpaper
            keyvalue = "wallpaperpkgname";
        }
        Log.e("DetailClassicActivity","setDownloadDataFlag type="+type+"      pkgname="+pkgname);
        String oldname = preferences.getString(keyvalue,"");
        if(!isContains(oldname, pkgname)){
	        editor.putString(keyvalue, oldname+","+pkgname);
	        editor.commit();
        }
        editor.putString(pkgname, filename);
        editor.commit();
        copySharedPreferencesToSDCard(context, "DOWNLOAD", "DOWNLOAD");
    }
    public static void deleteDownloadDataFlag(Context context, String pkgname, int type) {
        final SharedPreferences preferences = context.getSharedPreferences("DOWNLOAD", 0);
        final SharedPreferences.Editor editor = preferences.edit();
        addExcludeSettingKey(preferences);
        String keyvalue = "themepkgname";
        if(type == 1 || type == 11){//theme
            keyvalue = "themepkgname";
        }else if(type == 2 || type == 12){//lock
            keyvalue = "lockpkgname";
        }else if(type == 0 || type == 10){//wallpaper
            keyvalue = "wallpaperpkgname";
        }
        String oldname = preferences.getString(keyvalue,"");
        Log.e("DetailClassicActivity","==== deleteDownloadDataFlag: oldname = "+oldname+", pkgname = "+pkgname);
        if(isContains(oldname, pkgname)){
        	String newname = "";
        	int pos  = oldname.indexOf(pkgname);
        	int endpos = pos + pkgname.length();
        	if(pos == 1){
        		newname = oldname.substring(endpos);
        	}else if(pos > 1){
        		newname = oldname.substring(0, pos-1);
        		if(endpos != oldname.length()){
        			newname += oldname.substring(endpos);
        		}
        	}
	        editor.putString(keyvalue, newname);
	        editor.commit();
	        editor.remove(pkgname);
	        editor.commit();
	        copySharedPreferencesToSDCard(context, "DOWNLOAD", "DOWNLOAD");
        }
        
    }


    private void handleGetMessage(int msgID ) {
                 killLejingpin();
        mInitHandler.removeMessages(msgID);
        mInitHandler.sendEmptyMessageDelayed(msgID,500);

    }

    private void showDownloadDialog(){
    	LeAlertDialog DownloadDialog = new LeAlertDialog(DetailClassicActivity.this, R.style.Theme_LeLauncher_Dialog_Shortcut);
//        .setIconAttribute(android.R.attr.alertDialogIcon)
    	DownloadDialog.setLeTitle(R.string.application_name);
    	DownloadDialog.setLeMessage(R.string.download_msg);
    	DownloadDialog.setLePositiveButton(this.getText(android.R.string.ok), 
    			new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, final int whichButton) {
                startDownloadLeWallpaper();
            }
        });
    	DownloadDialog.setLeNegativeButton(this.getText(android.R.string.cancel), 
    			new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        DownloadDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                public void onDismiss(DialogInterface dialog) {
                }
        });

        DownloadDialog.show();
    }
    private void showDownloadEditDialog(){
        LeAlertDialog DownloadDialog = new LeAlertDialog(DetailClassicActivity.this, R.style.Theme_LeLauncher_Dialog_Shortcut);
//        .setIconAttribute(android.R.attr.alertDialogIcon)
        DownloadDialog.setLeTitle(R.string.application_name);
        DownloadDialog.setLeMessage(R.string.download_edit_msg);
        DownloadDialog.setLePositiveButton(this.getText(android.R.string.ok),
                        new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, final int whichButton) {
                startDownloadLeWallpaper();
            }
        });
        DownloadDialog.setLeNegativeButton(this.getText(android.R.string.cancel),
                        new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        DownloadDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                public void onDismiss(DialogInterface dialog) {
                }
        });

        DownloadDialog.show();
    }

    private void startDownloadLeWallpaper(){
        //String mAddress="http://www.vlifepaper.com/pubapk/[Lenovo]VLife_Stage_0217_2_88_2510_number682.apk";
        //String mAddress= "http://launcher.lenovo.com/launcher/data/attachment/app/voiceSearch.apk";
        //String mAddress= "http://launcher.lenovo.com/launcher/data/attachment/app/VLife_Stage_0217_2_88_2510_number682.apk";
        //sendBroadcastToDownload("Lewallpaper","214",getString(R.string.le_wallpaper),mAddress,"livewallpaper");
        String mAddress= "http://launcher.lenovo.com/launcher/data/attachment/app/Lewallpaper.apk";
        //sendBroadcastToDownload("Lewallpaper","214",getString(R.string.le_wallpaper),mAddress,DownloadConstant.CATEGORY_COMMON_APP);


        LauncherApplication app = (LauncherApplication) getApplicationContext();


        MagicDownloadControl.downloadFromCommon (app,
                                    VLIFE_MAIN_PACKAGE,
                                    "214",
                                    //HwConstant.CATEGORY_LELAUNCHER_SERVER_APK+"",
                                    DownloadConstant.CATEGORY_COMMON_APP,
                                    getString(R.string.le_wallpaper),
                                    null,
                                    mAddress,
                                    HwConstant.MIMETYPE_APK,
                                    "true",
                                    "true");


    }
    private void showDownloadWallpaperAPK(){
    	if(LEJPConstant.getInstance().isNeedConfirmDownload(DetailClassicActivity.this)){
            LejingpingSettingsValues.popupWlanDownloadDialog(DetailClassicActivity.this);
            return;
        }
        LeAlertDialog DownloadDialog = new LeAlertDialog(DetailClassicActivity.this, R.style.Theme_LeLauncher_Dialog_Shortcut);
//        .setIconAttribute(android.R.attr.alertDialogIcon)
        String str = null;
        if(mCurrentAppData != null ){
            String type = mCurrentAppData.getBgType();
            if( type != null && type.contains("Z")){
                  str =      getString(R.string.android_wallpaper);
            }else if( type != null && type.contains("M")){
                 str=                   getString(R.string.moxiu_wallpaper);
            }else if( type != null && type.contains("A")){
                    str=                getString(R.string.love_wallpaper);
            }
            if (hasWallPaperApkInstalled(type)){
                DownloadDialog.setLeMessage(getString(R.string.download_reconfirm_msg));
            }else{
                DownloadDialog.setLeMessage(getString(R.string.download_confirm_msg)+str+"?");
            }
        }
        DownloadDialog.setLeTitle(R.string.application_name);
        DownloadDialog.setLePositiveButton(this.getText(android.R.string.ok),
                        new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, final int whichButton) {
                startDownloadAndroidWallpaper();
            }
        });
        DownloadDialog.setLeNegativeButton(this.getText(android.R.string.cancel),
                        new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        DownloadDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                public void onDismiss(DialogInterface dialog) {
                }
        });

        DownloadDialog.show();
    }
    private boolean hasWallPaperApkInstalled(String type){
        String packageName = null;
        if( type != null && type.contains("Z")){
            packageName = "com.androidesk";
        }else if( type != null && type.contains("A")){
            packageName = "com.lovebizhi.wallpaper";
        }else if( type != null && type.contains("M")){
            packageName = "com.moxiu.wallpaper";
        }
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.setPackage(packageName);
        final PackageManager packageManager = getPackageManager();
        List<ResolveInfo> apps = null;
        apps = packageManager.queryIntentActivities(mainIntent, 0);
        Log.e(TAG,"type ============"+type+" size=============="+apps.size());
        if(apps.size() > 0) return true;
        return false;
    }

    private void startDownloadAndroidWallpaper(){

        LauncherApplication app = (LauncherApplication) getApplicationContext();
        if(mCurrentAppData != null ){
            String type = mCurrentAppData.getBgType();
            if( type != null && type.contains("Z")){
                MagicDownloadControl.downloadFromCommon (app,
                                    "com.androidesk",
                                    "214",
                                    DownloadConstant.CATEGORY_COMMON_APP,
                                    getString(R.string.android_wallpaper),
                                    null,
                                    ANDROID_WALLPAPER_DOWNLOADURL,
                                    HwConstant.MIMETYPE_APK,
                                    "true",
                                    "true");
					Reaper.processReaper(this, "LeJingpin", "DownloadAndroideskNum",
                                                        "",
							Reaper.REAPER_NO_INT_VALUE);
            }
            if( type != null && type.contains("M")){
                MagicDownloadControl.downloadFromCommon (app,
                                    "com.moxiu.wallpaper",
                                    "214",
                                    DownloadConstant.CATEGORY_COMMON_APP,
                                    getString(R.string.moxiu_wallpaper),
                                    null,
                                    MOXIU_WALLPAPER_DOWNLOADURL,
                                    HwConstant.MIMETYPE_APK,
                                    "true",
                                    "true");
                                        Reaper.processReaper(this, "LeJingpin", "DownloadMoxiuWallpaperNum",
                                                        "",
                                                        Reaper.REAPER_NO_INT_VALUE);
            }

            if( type != null && type.contains("A")){
                MagicDownloadControl.downloadFromCommon (app,
                                    "com.lovebizhi.wallpaper",
                                    "214",
                                    DownloadConstant.CATEGORY_COMMON_APP,
                                    getString(R.string.love_wallpaper),
                                    null,
                                    LOVE_WALLPAPER_DOWNLOADURL,
                                    HwConstant.MIMETYPE_APK,
                                    "true",
                                    "true");
					Reaper.processReaper(this, "LeJingpin", "DownloadLoveWallpaperNum",
                                                        "",
							Reaper.REAPER_NO_INT_VALUE);
            }
        }
    }

    private boolean hasLeWallPaperInstalled(){
        //if(true) return true;
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory("com.vlife.lenovo.intent.category.VLIFE_LIVE_WALLPAPER");
        final PackageManager packageManager = getPackageManager();
        List<ResolveInfo> apps = null;
        apps = packageManager.queryIntentActivities(mainIntent, 0);
        if(apps.size() > 0) return true;
        return false;

    }
    private boolean hasEditLeWallpaperInstalled(){
        try{
        PackageManager packageManager = getPackageManager();
        PackageInfo packInfo = packageManager.getPackageInfo(VLIFE_MAIN_PACKAGE,0);
        String version = packInfo.versionName;
        Log.e(TAG,"le wallpaper version name is ="+version);
        if("2.9".equals(version)){
            return false;
        }else{
            return true;
        }
        }catch(Exception e){
        return false;
        }
    }
    public String getWallpaperDownloadCountUrl(int wallpaperid){
        StringBuffer url = new StringBuffer("http://launcher.lenovo.com/wp/statistics?");
        url.append("wallpaperid=").append(wallpaperid);
        Log.i(TAG, "Wallpaper type , url=" + url.toString());
        return url.toString();
    }

    private void sendWallpaperDownloadCount(final int wallpaperid){
        new Thread() {
            public void run() {
                NetworkHttpRequest request = new NetworkHttpRequest();
                request.executeHttpGet(DetailClassicActivity.this,getWallpaperDownloadCountUrl(wallpaperid),
                new AmsCallback(){
                    public void onResult(int code, byte[] bytes) {
                    }
                });
            }
        }.start();
    }

	private void downloadDataByUrl() {
		int category = -1;
		String appIconUrl = "";
		if (mtypeindex == 0) {// recommend wallpaper
			ApplicationData data = mLeConstant.mServiceWallPaperDataList
					.get(mcurrpos);
			sendWallpaperDownloadCount(data.getAppId());
			// mDownloadUrl = data.getUrl();
			Log.i("mohl", "data geturl is:" + data.getUrl());
			mPackageName = data.getPackage_name();
			// mohl modify from
			// mVersionCode = data.getApp_versioncode();
			// to
			mVersionCode = data.getPackage_name();
			// end modify; there is no version code for wallpaper, but it will
			// cause download other error
			// if it's null. so set "mVersionCode = data.getPackage_name()"
			mAppName = data.getAppName();
			if (data.isDynamic == 1) {
				category = DownloadConstant.CATEGORY_LIVE_WALLPAPER	| DownloadConstant.CATEGORY_LENOVO_APK;
			} else {
				category = DownloadConstant.CATEGORY_WALLPAPER;
			}
			appIconUrl = getDownloadInfo(SP_WALLPAPER_PREVIEW_URL, mPackageName);
			DownloadInfo downloadinfo = new DownloadInfo(mPackageName,
					mVersionCode);
			downloadinfo.setDownloadUrl(mDownloadUrl);
			// LDownloadManager.getDefaultInstance(DetailClassicActivity.this).deleteTask(downloadinfo);
			/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: mohl DATE: * 2013-1-7 S */
			Reaper.processReaper(this, "LeJingpin", "RecommPaperDown",
					mAppName, Reaper.REAPER_NO_INT_VALUE);
			/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: mohl DATE: * 2013-1-7 E */
		} else if (mtypeindex == 1/* || mtypeindex == 2 */) {
			AmsApplication data = null;
			data = mLeConstant.mServiceThemeAmsDataList.get(mcurrpos);
			category = DownloadConstant.CATEGORY_THEME
					| DownloadConstant.CATEGORY_LENOVO_LCA;
			appIconUrl = getDownloadInfo(SP_THEME_PREVIEW_URL, mPackageName);
			mDownloadUrl = null;
			mPackageName = data.getPackage_name();
			mVersionCode = data.getApp_versioncode();
			mAppName = data.getAppName();
			if (appIconUrl.contains(",")) {
				int index = appIconUrl.indexOf(",");
				appIconUrl = appIconUrl.substring(0, index);
			}
		}
		Log.e("mohl", "9999999999 downloadDataByUrl: pkgname = " + mPackageName
				+ ", mVersionCode = " + mVersionCode + ", mAppName = "
				+ mAppName + ", appIconUrl = " + appIconUrl
				+ ", mDownloadUrl = " + mDownloadUrl + ", category = "
				+ category);
		sendBroadcastToDownload(mPackageName, mVersionCode, mAppName,
				appIconUrl, mDownloadUrl, category);

	}
    
    @Override
   	public void doCallback(final DownloadInfo info) {
       	Log.i("mohl","-------doCallback!!!!");
   		int status = info.getDownloadStatus();
   		boolean downloadingFlag = false;
   		switch(status){
   			case Downloads.STATUS_SUCCESS:
   				break;
   			case Downloads.STATUS_RUNNING_PAUSED:
   				break;
   			case Downloads.STATUS_RUNNING:
   				downloadingFlag = true;
   				break;
   			case Downloads.STATUS_INSTALL:
   				break;
   			default:
   				break;
   		}
   		if(downloadingFlag){
   		String pkg = info.getPackageName();
	   	if(pkg.equals(mPackageName)){
			long current = info.getCurrentBytes();
			long total = info.getTotalBytes();
			if(total != 0){
		  		mDownloadProgressLevel = (int)(current * 10000/ total);
		   		mInitHandler.sendEmptyMessage(REFRESH_PROGRESS);
			}
			Log.i(TAG,"-------doCallback, packagename:" + info.getPackageName() + ",version:" + info.getVersionCode()
		   				+",download progress level:"+mDownloadProgressLevel);
	   		}
   		}
   	}

    private void sendBroadcastToDownload(String packageName,String versionCode,
            String appName, String iconUrl, String downloadurl, int category ) {

    Log.i(TAG, "packageName , versionCode , appName >>>"+packageName+";"+versionCode+";"+appName+" url="+mDownloadUrl);
  /*  Intent intent = new Intent();
    intent.setAction(HwConstant.ACTION_REQUEST_APP_DOWNLOAD);
    intent.putExtra("package_name", packageName);
    intent.putExtra("version_code", versionCode);
    intent.putExtra("app_name", appName);
    intent.putExtra("app_iconurl", iconUrl);                  
    intent.putExtra("download_url",downloadurl );
    intent.putExtra("category", category);
    sendBroadcast(intent);*/
    AppDownloadUrl downurl = new AppDownloadUrl();
	if (downloadurl != null) {
		downurl.setDownurl(downloadurl);
	} else {
		downurl.setDownurl(DownloadConstant.TYPE_DOWNLOAD_ACTION);
	}
	downurl.setPackage_name(packageName);
	downurl.setVersion_code(versionCode);
	downurl.setApp_name(appName);
	downurl.setIconUrl(iconUrl);
	downurl.setCategory(category);
	if (DownloadConstant.CATEGORY_WALLPAPER == category) {
		downurl.setMimeType(DownloadConstant.MIMETYPE_WALLPAPER);
	} else {
		downurl.setMimeType(DownloadConstant.MIMETYPE_APK);
	}
	downurl.setCallback(this);
	sendMessage(DownloadHandler.getInstance(this),
			DownloadConstant.MSG_DOWN_LOAD_URL, downurl);
    
    /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: mohl DATE: * 2013-1-7 S */
    if(category==(DownloadConstant.CATEGORY_THEME | DownloadConstant.CATEGORY_LENOVO_LCA)){
		Reaper.processReaper(this, "LeJingpin", "RecommThemeDown",
				packageName,
				Reaper.REAPER_NO_INT_VALUE);
    }else if(category==DownloadConstant.CATEGORY_LOCKSCREEN){
		Reaper.processReaper(this, "LeJingpin", "RecommLKScreenDown",
				packageName,
				Reaper.REAPER_NO_INT_VALUE);
    }
    /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: mohl DATE: * 2013-1-7 E */
}
    
    private void sendMessage(Handler handler, int what, Object obj) {
		if (handler != null) {
			Message msg = new Message();
			msg.obj = obj;
			msg.what = what;
			handler.sendMessage(msg);
		}
	}
    
    private void registerDownloadReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(HwConstant.ACTION_DETAIL_PREVIEW_URL_DOWNLOAD);
		filter.addAction(DownloadConstant.ACTION_DOWNLOAD_STATE_CHANGED);
		filter.addAction(DownloadConstant.ACTION_DOWNLOAD_INSTALL_UNINSTALL);
		filter.addAction(DownloadConstant.ACTION_DOWNLOAD_DELETE);
		filter.addAction(DownloadConstant.ACTION_APK_FAILD_DOWNLOAD);
        filter.addAction(mLeConstant.APK_INSTALL_ACTION);
        filter.addAction(mLeConstant.APK_UNINSTALL_ACTION);
        mUiReceiver = new UiReceiver(this);
        registerReceiver(mUiReceiver, filter);
    }
    private class UiReceiver extends BroadcastReceiver {
        private Context mContext;

        public UiReceiver(Context c) {
             mContext = c;
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "onReceive the download status action="+action);
            if(DownloadConstant.ACTION_DOWNLOAD_STATE_CHANGED.equals(action)) {

                Log.i(TAG, "111111111111");
                String pkg = intent.getStringExtra(DownloadConstant.EXTRA_PACKAGENAME);
                if(VLIFE_MAIN_PACKAGE.equals(pkg)){
                    Log.i(TAG, "avoid the lewallpaper 111111111111");
                    return;
                }
                String vcode = intent.getStringExtra(DownloadConstant.EXTRA_VERSION);
                String installPath = intent.getStringExtra(DownloadConstant.EXTRA_INSTALLPATH);
                Log.i(TAG, "installPath is:"+installPath);
                mInstallPath = installPath;

                String status = intent.getStringExtra(DownloadConstant.EXTRA_STATUS);// 下载的状态
//                String apkLocalPath = intent.getStringExtra(HwConstant.EXTRA_INSTALLPATH);
//                int progress = intent.getIntExtra(HwConstant.EXTRA_PROGRESS, 0);// 下载进度
//				String category = intent.getStringExtra(HwConstant.EXTRA_CATEGORY);
                int cat = intent.getIntExtra(DownloadConstant.EXTRA_CATEGORY, DownloadConstant.CATEGORY_ERROR);
                int category = DownloadConstant.getDownloadCategory(cat);
                
                String appName = intent.getStringExtra(HwConstant.EXTRA_APPNAME);
				Log.i(TAG, "status is:" + status + ", category = " + category);
                if(status.equals("downloading")){
                    if(mtypeindex == 1 || mtypeindex == 2 || 
                  (( DownloadConstant.CATEGORY_LIVE_WALLPAPER == category) && mtypeindex == 0)){
                        if(!downloadPkgList.contains(pkg)){
				Log.i(TAG, "status is:" + status + ", category = " + category+" donwloadpkg add pkg="+pkg);
                            downloadPkgList.add(pkg);
                        }
                    }
                }
                if(status.equals("uninstall")){

                    DownloadInfo downloadInfo ;
                    downloadInfo = new DownloadInfo(pkg,vcode);
                    downloadInfo.setInstallPath(installPath);
                    downloadInfo.setAppName(appName);
					Log.i(TAG, "uninstall status pkgName ===============" + mPackageName
							+ " installPath=" + installPath);
					// downloadInfo.setDownloadUrl(url);
					mFileName = installPath;

					setDownloadDataFlag(DetailClassicActivity.this, mtypeindex, pkg, mFileName);
					removeDownloadingStatus(DetailClassicActivity.this, mtypeindex, pkg);
					downloadPkgList.remove(pkg);
					if (mtypeindex == 0) {// recommend wallpaper
                        if( category == DownloadConstant.CATEGORY_LIVE_WALLPAPER){
					                       
						if (mPackageName != null && mPackageName.equals(pkg)) {
							mDownloadStatus = 2;
//							mdetail_classic_download.setText(R.string.le_download_install);
							mdetail_classic_download.setDownloadStatus(mDownloadStatus);
		                    mDownloadProgressLevel = 0;
		                    mInitHandler.sendEmptyMessage(REFRESH_PROGRESS);
						}
                        }else{
						if (mPackageName != null && mPackageName.equals(pkg)) {
							mDownloadStatus = 0;
//							mdetail_classic_download.setText(R.string.data_apply);
							mdetail_classic_download.setDownloadStatus(mDownloadStatus);
		                    mDownloadProgressLevel = 0;
		                    mInitHandler.sendEmptyMessage(REFRESH_PROGRESS);
						}
                      }
					} else if (mtypeindex == 1/* || mtypeindex == 2*/) {
						Log.i(TAG, "2222pkgName ===============" + mPackageName
								+ " installPath=" + installPath
								+ " pkgname========" + pkg);
						if (mPackageName != null && mPackageName.equals(pkg)) {
							mDownloadStatus = 2;
//							mdetail_classic_download.setText(R.string.le_download_install);
							mdetail_classic_download.setDownloadStatus(mDownloadStatus);
		                    mDownloadProgressLevel = 0;
		                    mInitHandler.sendEmptyMessage(REFRESH_PROGRESS);
						}
					}
					Log.i(TAG, "pkgName ===============" + pkg+""+category);
					if(category == DownloadConstant.CATEGORY_WALLPAPER ||
						category == DownloadConstant.CATEGORY_LIVE_WALLPAPER){
						Log.i(TAG, "pkgName ===============" + pkg);
						saveDownloadInfo(DetailClassicActivity.this, SP_WALLPAPER_URL, pkg, installPath);
						backupLocalWallpaperPreview(DetailClassicActivity.this, pkg);
						copySharedPreferencesToSDCard(context, SP_WALLPAPER_URL, SP_WALLPAPER_URL);
					}
				}
			} else if (DownloadConstant.ACTION_APK_FAILD_DOWNLOAD.equals(action)) {
				String pkg = intent.getStringExtra(DownloadConstant.EXTRA_PACKAGENAME);
//				String vcode = intent.getStringExtra(WidgetConstant.EXTRA_VERSION);
//				String app_name = intent.getStringExtra(WidgetConstant.EXTRA_APPNAME);
//	    		int category = intent.getIntExtra(DownloadConstant.EXTRA_CATEGORY, DownloadConstant.CATEGORY_ERROR);
	    		int result = intent.getIntExtra(DownloadConstant.EXTRA_RESULT, DownloadConstant.FAILD_DOWNLOAD_OTHER_ERROR);
				//下载失败  删除配置文件相应项
				if (VLIFE_MAIN_PACKAGE.equals(pkg)) {
					Log.i(TAG, "avoid the lewallpaper 111111111111");
					return;
				}
				if (downloadPkgList.contains(pkg)) {
					Log.i(TAG, "status is:" + action + " donwload failed remove pkg=" + pkg + " type="
							+ mtypeindex);
					downloadPkgList.remove(pkg);
				}
				if (mtypeindex == 1/* || mtypeindex == 2*/) {
					if (mPackageName != null && mPackageName.equals(pkg)) {
						mDownloadStatus = -1;
//						mdetail_classic_download.setText(R.string.download_download);
						mdetail_classic_download.setDownloadStatus(mDownloadStatus);
						mDownloadProgressLevel = 0;
						mInitHandler.sendEmptyMessage(REFRESH_PROGRESS);
						removeDownloadingStatus(DetailClassicActivity.this, mtypeindex, pkg);
					}
				} else if (mtypeindex == 0) {
					mDownloadStatus = -1;
//					mdetail_classic_download.setText(R.string.download_download);
					mdetail_classic_download.setDownloadStatus(mDownloadStatus);
					mDownloadProgressLevel = 0;
					mInitHandler.sendEmptyMessage(REFRESH_PROGRESS);
					//删除已预先下载好的壁纸icon，preview,以及配置文件信息
                    if(mCurrentAppData != null && mCurrentAppData.isDynamic == 0){
                    	deleteLocalItem(DetailClassicActivity.this, pkg, 10);
                    	removeDownloadingStatus(DetailClassicActivity.this, mtypeindex, pkg);
                    }else{
                    	removeDownloadingStatus(DetailClassicActivity.this, mtypeindex, pkg);
                    }
				} 
//				Toast.makeText(DetailClassicActivity.this, R.string.check_sdcard_status_result,
//						Toast.LENGTH_SHORT).show();
				switch(result){
		    		case DownloadConstant.FAILD_DOWNLOAD_NO_ENOUGHT_SPACE:
		    			Toast.makeText(context, R.string.failed_download_no_enough_space,
								Toast.LENGTH_SHORT).show();
		    			break;
		    		case DownloadConstant.FAILD_DOWNLOAD_NETWORK_ERROR:
		    			Toast.makeText(context, R.string.failed_download_network_error,
								Toast.LENGTH_SHORT).show();
		    			break;
		    		case DownloadConstant.FAILD_DOWNLOAD_OTHER_ERROR:
		    			Toast.makeText(context, R.string.failed_download_other_error,
								Toast.LENGTH_SHORT).show();
		    			break;
		    		default:
		    				break;
				}
//			else if (HwConstant.ACTION_DETAIL_PREVIEW_URL_DOWNLOAD.equals(action)) {
//				loadPreview(intent.getStringExtra("firstSnapPath").split(","));
//				
//			} 
			}else if (mLeConstant.APK_INSTALL_ACTION.equals(action)) {// for the message delayed
				String pkg = intent.getStringExtra("pkgname");
				Log.e(TAG, "========resetDownloadInfo: pkg= " + pkg	+ " mPackageName=" + mPackageName);
				if (mtypeindex == 1 /*|| mtypeindex == 2*/) {
					if (pkg != null && pkg.equals(mPackageName)) {
//						mdetail_classic_download.setText(R.string.data_apply);
						mDownloadStatus = 0;
						mdetail_classic_download.setDownloadStatus(mDownloadStatus);
		                mDownloadProgressLevel = 0;
		                mInitHandler.sendEmptyMessage(REFRESH_PROGRESS);
					}
				}else if(mtypeindex == 0){
                    if(mCurrentAppData != null && mCurrentAppData.isDynamic == 1){
					if (pkg != null && pkg.equals(mPackageName)) {
//						mdetail_classic_download.setText(R.string.data_apply);
						mDownloadStatus = 0;
						mdetail_classic_download.setDownloadStatus(mDownloadStatus);
		                mDownloadProgressLevel = 0;
		                mInitHandler.sendEmptyMessage(REFRESH_PROGRESS);
                        setApkInstalledFlag(mPackageName); 
    			        mLeConstant.mWallpapaerNeedRefresh = true;
    			        mLeConstant.mIsWallpaperDeleteFlag = true;
    			        Log.e(TAG, "========install set the flag true= " );
					}
                    }
                }
				if(mtypeindex == 1){
					LEJPConstant.getInstance().mThemeNeedRefresh = true;
				}/*else if(mtypeindex == 2){
					LEJPConstant.getInstance().mLockscreenNeedRefresh = true;
				}*/
//				DownloadThumbnails(pkg);
		}else if(mLeConstant.APK_UNINSTALL_ACTION.equals(action)) {
				String pkg = intent.getStringExtra("pkgname");
				if (mtypeindex == 11 || mtypeindex == 12 || mtypeindex == 10) {
					Log.e("mohl", "========APK_UNINSTALL_ACTION: pkg= " + pkg
							+ " mPackageName=" + mPackageName);
					if (pkg != null) {
						//deleteLocalApk(DetailClassicActivity.this, pkg);
                                                deleteLocalApkInThread(pkg);
					}
					boolean willFinish = false;
					int listSize = 0;
					int newCurrpos = -1;
					AmsApplication amsData = null;
					ApplicationData amswallData = null;
					if (mtypeindex == 11) {
						listSize = mLeConstant.mServiceLocalThemeAmsDataList
								.size();
					}/* else if (mtypeindex == 12) {
					} */else {
						listSize = mLeConstant.mServiceLocalWallPaperDataList
								.size() - 1;// remove the other wall
					}
					Log.e("mohl", "========APK_UNINSTALL_ACTION: listsize= "+ listSize + " mcurrpos=========" + mcurrpos);
					if (listSize <= 0) {
						finish();
						return;
					}
					if (listSize == 1) {
						mcurrpos = 0;
					}
					if (listSize > 1) {
						if (mcurrpos >= listSize) {
							mcurrpos = listSize - 1;
						}
					}
					Log.e("mohl", "========APK_UNINSTALL_ACTION: listsize= " + listSize + " mcurrpos=========" + mcurrpos);
					gridview.setSelection(mcurrpos);
					if (mtypeindex == 11) {
						mLeConstant.mThemeNeedRefresh = true;
						mLeConstant.mIsThemeDeleteFlag = true;
						((DetailClassicViewPagerAdapter)vp.getAdapter()).clearHashmap();
						amsData = mLeConstant.mServiceLocalThemeAmsDataList
								.get(mcurrpos);
//						loadNativePreview(3);
						loadNativePreview(2); //modified by mohl for bug 11806,the last preview is not suitable
					}/* else if (mtypeindex == 12) {
					}*/ else if (mtypeindex == 10) {
						amswallData = mLeConstant.mServiceLocalWallPaperDataList.get(mcurrpos);
						loadNativePreview(1);
					}
					Log.e("mohl","========APK_UNINSTALL_ACTION: aaaaaaaaaaaaaaa index="+ mtypeindex);
					if (mtypeindex != 10) {
						//if (mAction_bar_title != null && amsData != null)
						//	mAction_bar_title.setText(amsData.getAppName());
	    			    setTitle(amsData.getAppName());
						resetDownloadInfo(amsData);
//						if (amsData != null)
							getDownloadDataFlag(mtypeindex,amsData.getPackage_name());
					} else {
						Log.e("mohl","========APK_UNINSTALL_ACTION: aaaaaaaaaaaaaaa ");
                      if(amswallData.getIsNative() || SettingsValue.getCurrentMachineType(DetailClassicActivity.this) > -1){
                mdetail_classic_install.setVisibility(View.VISIBLE);
                livewall_layout.setVisibility(View.GONE);
                }else{
                mdetail_classic_install.setVisibility(View.GONE);
                livewall_layout.setVisibility(View.VISIBLE);
                mdetail_classic_edit.setText(R.string.edit_wallpaper);
                                }

	    			    setTitle(amswallData.getAppName());
						resetDownloadInfo(amswallData);
//						resetDownloadInfo(amswallData);
					}
					updateOptionsMenuStatus();
					mDetailsGallryAdapter.setCurSelectpos(mcurrpos);
					mDetailsGallryAdapter.notifyDataSetChanged();
				}
			}else if(DownloadConstant.ACTION_DOWNLOAD_RESUME.equals(action)){
					if(mPackageName != null && mVersionCode != null){
						Log.i("mohl", "APK_UNINSTALL_ACTION:-----------ACTION_DOWNLOAD_RESUME-----------------");
						DownloadHandler.getInstance(context).registerDownloadCallback(
								mPackageName, mVersionCode, DetailClassicActivity.this);
					}
					return;
			}
        }
    }
	
    private void deleteLocalApkInThread(final String pkg){
        new Thread() {
            public void run() {
		deleteLocalApk(DetailClassicActivity.this, pkg);
            }
        }.start();
    }
	public static void deleteLocalApk(Context context, String apk) {
		Log.e("mohl", "========= receive msg: apk uninstalled!!! " + apk);
		final SharedPreferences preferences = context.getSharedPreferences("DOWNLOAD", 0);
		String apknames = preferences.getString("themepkgname", "");
		if (!apknames.equals("") && isContains(apknames, apk)) {
			deleteLocalItem(context, apk, 11);
			deleteDownloadDataFlag(context, apk, 11);
		} else {
			apknames = preferences.getString("lockpkgname", "");
			if (!apknames.equals("") && isContains(apknames, apk)) {
				deleteLocalItem(context, apk, 12);
				deleteDownloadDataFlag(context, apk, 12);
			}
		}
				apknames = preferences.getString("wallpaperpkgname", "");
			if (!apknames.equals("") && isContains(apknames, apk)) {
				deleteLocalItem(context, apk, 10);
				deleteDownloadDataFlag(context, apk, 10);
			}
		/* PK_ID:DELETE LOCAL THEME PRVIEW DATA  AUTH:MOHL DATE:2013-1-22	* S */
		SharedPreferences sp_local_preview = context.getSharedPreferences(
				SP_THEME_LOCAL_PREVIEW, 0); 
		if(sp_local_preview!=null){
			SharedPreferences.Editor editor = sp_local_preview.edit();
			editor.remove(apk).commit(); 
		}
		/* PK_ID:DELETE LOCAL THEME PRVIEW DATA  AUTH:MOHL DATE:2013-1-22	* E */
	}
	
	    /** get the icon preview. */

	private void initDots(int size) {
		mdot = size;
	    LinearLayout ll = (LinearLayout) findViewById(R.id.detail_classic_ll);
	    dots = new ImageView[3];
	    //循环取得小点图片
	    for (int i = 0; i < mdot; i++) {
	        dots[i] = (ImageView) ll.getChildAt(i);
	        dots[i].setVisibility(View.VISIBLE);
	        dots[i].setEnabled(true);//都设为灰色
	        dots[i].setOnClickListener(this);
	        dots[i].setTag(i);//设置位置tag，方便取出与当前位置对应
	    }
	    for(int i = mdot;i<3;i++){
	    	dots[i] = (ImageView) ll.getChildAt(i);
	        dots[i].setVisibility(View.GONE);
	    }
	    currentIndex = 0;
	    dots[currentIndex].setEnabled(false);//设置为白色，即选中状态
            if( mtypeindex != 1 && mtypeindex != 11){
	        ll.setVisibility(View.INVISIBLE);
            }else{
	        ll.setVisibility(View.VISIBLE);
            }
	}

    /**
     *设置当前的引导页 
     */
    private void setCurView(int position)
    {
        if (position < 0 || position >= mdot) {
            return;
        }
        vp.setCurrentItem(position);
    }

    /**
     *这只当前引导小点的选中 
     */
    private void setCurDot(int positon)
    {
        if (positon < 0 || positon > mdot - 1 || currentIndex == positon) {
            return;
        }

        dots[positon].setEnabled(false);
        dots[currentIndex].setEnabled(true);

        currentIndex = positon;
    }

    //当滑动状态改变时调用
    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub
        
    }

    //当当前页面被滑动时调用
    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub
        
    }

    //当新的页面被选中时调用
    @Override
    public void onPageSelected(int arg0) {
        //设置底部小点选中状态
        setCurDot(arg0);
    }

    @Override
    public void onClick(View v) {
        int position = (Integer)v.getTag();
        setCurView(position);
        setCurDot(position);
    }
    private void resetDownloadInfo(ApplicationData data){
		Log.e("mohl", "========resetDownloadInfo: app id = " +data.getAppId());
		mCurrentAppData = data;
		mIconAddr = data.getIconUrl();
		mPreviewAddr = data.getPreviewAddr();
        mDownloadUrl = data.getUrl();
        mPackageName = data.getPackage_name();
        mVersionCode = data.getApp_versioncode();
        mAppName = data.getAppName();
    }
    private void resetDownloadInfo(AmsApplication data){
    	if(data!=null){
			mPackageName = data.getPackage_name();
			mVersionCode = data.getApp_versioncode();
			mAppName = data.getAppName();
    	}
    }
    private void requestAppInfo(final String pkgname,final String vercode){
        DeviceInfo deviceInfo = DeviceInfo.getInstance(this);
        AmsSession.init(this, new AmsSessionCallback(){
        public void onResult(AmsRequest request, int code, byte[] bytes) {
                Log.i(TAG,"UpgradeAppInfoAction.requestAppInfo, AmsSession.init >> result code:"+ code);
                if(code!=200){
                	//sendIntentForAppInfoFinished(mContext,false,mPackageName,mVersionCode);
                }else{
                	getAppInfo(DetailClassicActivity.this,pkgname,vercode);
                }
        	}
        },deviceInfo.getWidthPixels(),deviceInfo.getHeightPixels(), deviceInfo.getDensityDpi());
    }
    private void getAppInfo(final Context context,final String package_name,final String version_code){
        AppInfoRequest5 infoRequest = new AppInfoRequest5(this);
        infoRequest.setData(package_name, version_code);
        AmsSession.execute(context, infoRequest, new AmsSessionCallback(){
            public void onResult(AmsRequest request, int code, byte[] bytes) {
            Log.i(TAG,"UpgradeAppInfoAction.getAppInfo,AmsSession.execute >> result code:"+ code);
            boolean success= false;
                if( code == 200 ){
                    success = true;
                    if(bytes != null) {
                        AppInfoResponse5 infoResponse = new AppInfoResponse5();
                        infoResponse.parseFrom(bytes);
                        boolean successResponse= infoResponse.getIsSuccess();
                        Log.i(TAG,"UpgradeAppInfoAction.getAppInfo >> response success : :"+ successResponse);
                        if(successResponse){
                            AppInfo responseApp = infoResponse.getAppInfo();
                            final String firstSnapPath = responseApp.getSnapList().toString().replace("[", "").replace("]", "");
                            Intent intent = new Intent(HwConstant.ACTION_DETAIL_PREVIEW_URL_DOWNLOAD);
                            intent.putExtra("firstSnapPath", firstSnapPath);
                            DetailClassicActivity.this.sendBroadcast(intent);
                        }
                    }
                }
            }

         });
    }
	
	public static String getLocationToSave(String url, int type){
        //download icons by mohl
        String path = LEJPConstant.getDownloadPath();
        String subpath = "";
        if(type == 0 || type == 10){
        	subpath = "/wallpapers";
        }else if(type == 1 || type == 11){
        	subpath = "/themes";
        }else if(type == 2 || type == 12){
        	subpath = "/locks";
        }
        path += subpath;
        String filePath = "";
        File dir = new File(path);
        if(!dir.exists()){
        	dir.mkdir(); 
        }
        filePath = dir.getPath() + "/";
        String filename = "";
    	if(type==1||type==2){
    		filename = url.substring(url.lastIndexOf("/") + 1);
    		filename= filename.substring(0,filename.lastIndexOf("?"));
    	}else if(type==0){
    		filename = url.substring(url.lastIndexOf("wallpaper") + 1).replace("/", "-");
    		if(filename.contains(":")){
                filename = filename.replace(":","");
             }
    	}
        filePath += filename;
		return filePath;
	}
	
	public static void saveDownloadInfo(Context context, String sharedpreferenceName, String key, String value) {
		SharedPreferences sp = context.getSharedPreferences(sharedpreferenceName, 0);
		SharedPreferences.Editor editor = sp.edit();
		addExcludeSettingKey(sp);
		editor.putString(key, value).commit();
	}
	
	public String getDownloadInfo(String sharedpreferenceName, String key) {
		SharedPreferences sp = getSharedPreferences(sharedpreferenceName, 0);
		return sp.getString(key, "");
	}
	
	private void saveDownloadInfo(ApplicationData data,String iconLocalpath,String localPreviewpath) {
			Log.e("mohl", "=========save : id = " + data.getAppId());
			Log.e("mohl", "=========save : url = " + data.getUrl());
			Log.e("mohl", "=========save : icon = " + data.getIconUrl());
			Log.e("mohl", "=========save : preview = " + data.getPreviewAddr());
			SharedPreferences sp_icon = getSharedPreferences(
					SP_WALLPAPER_ICON_URL, 0);
			SharedPreferences sp_preview = getSharedPreferences(
					SP_WALLPAPER_PREVIEW_URL, 0);
			SharedPreferences sp_name = getSharedPreferences(
					SP_WALLPAPER_NAME, 0);
			String strId = data.getPackage_name();
			SharedPreferences.Editor editor = sp_icon.edit();
			
			editor = sp_preview.edit();
			addExcludeSettingKey(sp_preview);
			editor.putString(strId, localPreviewpath).commit();
			copySharedPreferencesToSDCard(DetailClassicActivity.this, SP_WALLPAPER_PREVIEW_URL, SP_WALLPAPER_PREVIEW_URL);
			editor = sp_name.edit();
			addExcludeSettingKey(sp_name);
			editor.putString(strId, data.getAppName()).commit();
			copySharedPreferencesToSDCard(DetailClassicActivity.this, SP_WALLPAPER_NAME, SP_WALLPAPER_NAME);
	}
	
	private void saveDownloadInfo(AmsApplication data, String previewAddr, int type) {
		SharedPreferences sp_url = null;
//		SharedPreferences sp_icon = null;
		SharedPreferences sp_preview = null;
		SharedPreferences sp_name = null;
		if(type == 1){ //theme
			sp_url = getSharedPreferences(
					SP_THEME_URL, 0);
//			sp_icon = getSharedPreferences(
//					SP_THEME_ICON_URL, 0);
			sp_preview = getSharedPreferences(
					SP_THEME_PREVIEW_URL, 0);
			sp_name = getSharedPreferences(
					SP_THEME_NAME, 0);
		}else if(type == 2){ //lockscreen
			sp_url = getSharedPreferences(
					SP_LOCKSCREEN_URL, 0);
//			sp_icon = getSharedPreferences(
//					SP_LOCKSCREEN_ICON_URL, 0);
			sp_preview = getSharedPreferences(
					SP_LOCKSCREEN_PREVIEW_URL, 0);
			sp_name = getSharedPreferences(
					SP_LOCKSCREEN_NAME, 0);	
		}
		String strId = data.getPackage_name();
		Log.e("mohl", "&&&&&&&&&&&&  savedownloadinfo: fileName = " + mFileName+" pkgname="+strId);
		if(sp_url!=null){
			SharedPreferences.Editor editor = sp_url.edit();
			addExcludeSettingKey(sp_url);
			editor.putString(strId, mFileName).commit();
		}
			// editor = sp_icon.edit();
			// editor.putString(strId, data.getIcon_addr()).commit();
		if(sp_preview!=null){
			SharedPreferences.Editor editor = sp_preview.edit();
			addExcludeSettingKey(sp_preview);
			editor.putString(strId, previewAddr).commit();
		}
		if(sp_name!=null){
			SharedPreferences.Editor editor = sp_name.edit();
			addExcludeSettingKey(sp_name);
			editor.putString(strId, data.getAppName()).commit();
		}
}
	
	public static void deleteLocalItem(Context context, String packageName, int type){
		Log.e("mohl", "&&&&&&&&&&&&  deleteLocalItem: packageName = " + packageName +
				"; type = " + type);
		SharedPreferences sp_url = null;
		SharedPreferences sp_icon = null;
		SharedPreferences sp_preview = null;
		SharedPreferences sp_name = null;
		if(type == 10){
			sp_url = context.getSharedPreferences(
					SP_WALLPAPER_URL, 0);
			sp_icon = context.getSharedPreferences(
					SP_WALLPAPER_ICON_URL, 0);
			sp_preview = context.getSharedPreferences(
					SP_WALLPAPER_PREVIEW_URL, 0);
			sp_name = context.getSharedPreferences(
					SP_WALLPAPER_NAME, 0);
			LEJPConstant.getInstance().mWallpapaerNeedRefresh = true;
		}else if(type == 11){ //theme
			sp_url = context.getSharedPreferences(
					SP_THEME_URL, 0);
			sp_icon = context.getSharedPreferences(
					SP_THEME_ICON_URL, 0);
			sp_preview = context.getSharedPreferences(
					SP_THEME_PREVIEW_URL, 0);
			sp_name = context.getSharedPreferences(
					SP_THEME_NAME, 0);
			LEJPConstant.getInstance().mThemeNeedRefresh = true;
		}else if(type == 12){ //lockscreen
			sp_url = context.getSharedPreferences(
					SP_LOCKSCREEN_URL, 0);
			sp_icon = context.getSharedPreferences(
					SP_LOCKSCREEN_ICON_URL, 0);
			sp_preview = context.getSharedPreferences(
					SP_LOCKSCREEN_PREVIEW_URL, 0);
			sp_name = context.getSharedPreferences(
					SP_LOCKSCREEN_NAME, 0);	
			LEJPConstant.getInstance().mLockscreenNeedRefresh = true;
		}
		String key = packageName;
		if(sp_url!=null){
			SharedPreferences.Editor editor = sp_url.edit();
			if (!sp_url.getString(key, "").equals("")) {
				new File(sp_url.getString(key, "")).delete();
			}
			editor.remove(key).commit();
			if(type == 10) 
				copySharedPreferencesToSDCard(context, SP_WALLPAPER_URL, SP_WALLPAPER_URL);
		}
		if(sp_icon!=null){
			SharedPreferences.Editor editor = sp_icon.edit();
			if (!sp_icon.getString(key, "").equals("")) {
				new File(sp_icon.getString(key, "")).delete();
			}
			editor.remove(key).commit();
			if(type == 10) 
				copySharedPreferencesToSDCard(context, SP_WALLPAPER_ICON_URL, SP_WALLPAPER_ICON_URL);
		}
		if(sp_preview!=null){
			SharedPreferences.Editor editor = sp_preview.edit();
			if (!sp_preview.getString(key, "").equals("")) {
				new File(sp_preview.getString(key, "")).delete();
			}
			editor.remove(key).commit();
			if(type == 10) 
				copySharedPreferencesToSDCard(context, SP_WALLPAPER_PREVIEW_URL, SP_WALLPAPER_PREVIEW_URL);
		}
		if(sp_name!=null){
			SharedPreferences.Editor editor = sp_name.edit();
			editor.remove(key).commit();
			if(type == 10) 
				copySharedPreferencesToSDCard(context, SP_WALLPAPER_NAME, SP_WALLPAPER_NAME);
		}
	}
	
	public static void deleteLocalPreview(Context context, String packageName, int type){
		Log.e("mohl", "&&&&&&&&&&&&  deleteLocalPreview: packageName = " + packageName +
				"; type = " + type);
		SharedPreferences sp_preview = null;
		if(type == 10){
			sp_preview = context.getSharedPreferences(
					SP_WALLPAPER_PREVIEW_URL, 0);
		}else if(type == 11){ //theme
			sp_preview = context.getSharedPreferences(
					SP_THEME_LOCAL_PREVIEW, 0);
		}
		String key = packageName;
		if(sp_preview!=null){
			SharedPreferences.Editor editor = sp_preview.edit();
			if (!sp_preview.getString(key, "").equals("")) {
				new File(sp_preview.getString(key, "")).delete();
			}
			editor.remove(key).commit();
			if(type == 10)
				copySharedPreferencesToSDCard(context, SP_WALLPAPER_PREVIEW_URL, SP_WALLPAPER_PREVIEW_URL);
		}
	}
	
    public void requestDownloadApp(final Context context,final String app_name,final String package_name,
                        final String version_code){
        DeviceInfo deviceInfo = DeviceInfo.getInstance(this);
        AmsSession.init(context, new AmsSessionCallback(){
                public void onResult(AmsRequest request, int code, byte[] bytes) {
                    Log.i(TAG, "DownloadAppAction.requestDownloadApp, AmsSession.init >> result code:"+ code);
                    if(code!=200){
                    }else{
                        getDownloadApp(context,app_name,package_name,version_code);
                }
            }
            //modify for ams5.0     
            //},deviceInfo.getWidthPixels(),deviceInfo.getHeightPixels());
        },deviceInfo.getWidthPixels(),deviceInfo.getHeightPixels(), deviceInfo.getDensityDpi());
    }
    private void getDownloadApp(final Context context,final String app_name,final String package_name,final String version_code){
/*
        GetAppDownLoadUrlRequest downloadRequest = new GetAppDownLoadUrlRequest(context);
        downloadRequest.setData(package_name, version_code);
        AmsSession.execute(context, downloadRequest, new AmsSessionCallback(){
            public void onResult(AmsRequest request, int code, byte[] bytes) {
                Log.i(TAG, "DownloadAppAction.getDownloadApp, AmsSession.execute >> result code : "+code );
                if(code==200){
                    if(bytes!=null){
                        GetAppDownLoadUrlResponse response = new GetAppDownLoadUrlResponse(context);
                        response.parseFrom(bytes);
                        boolean successResponse = response.getIsSuccess();
                        Log.i(TAG, "DownloadAppAction.getDownloadApp >> response success :"+ successResponse);
                        if(successResponse){
                            mDownloadUrl = response.getDownLoadUrl();
                        }
                    }
                }
            }
        });
*/
    }

	private void DownloadThumbnails(boolean flag) {
		if (mtypeindex == 0 && !flag) {
			if (TextUtils.isEmpty(mPreviewAddr)) {
				return;
			}
			LEJPConstant.getInstance().mWallpapaerNeedRefresh = true;
			// preview
			String iconLocalPath = null;// Do not save the icon pic
			String previewPath = "";
			for (int pos = 0; pos < mviewpagerlayouts.length; pos++) {
				if (pos > 0)
					previewPath += ",";

				Drawable preview = mviewpagerlayouts[pos].mimage.getDrawable();
				String path = saveDrawableToFile(preview,
						mCurrentAppData.getPreviewAddr(), mtypeindex);
				previewPath += path;
				Log.e(TAG, "previewPath ===================" + previewPath);
			}
			// save to sharedPreference
			saveDownloadInfo(mCurrentAppData, iconLocalPath, previewPath);

		}
	}

    private void DownloadThumbnails(String pkg){
    	Log.e(TAG,"DownloadThumbnails ====pkg = "+ pkg + ", mtypeindex = "+mtypeindex);
        if(pkg == null) return;
        
        if( mtypeindex == 0) {
            ApplicationData appData = null;
            LEJPConstant.getInstance().mWallpapaerNeedRefresh = true;
            for(int i=0;i<mLeConstant.mServiceWallPaperDataList.size();i++){
                appData = mLeConstant.mServiceWallPaperDataList.get(i);
                if(pkg.equals(appData.getPackage_name())){
                    break;
                }
            }
            String previewPath = "";
            if(appData == null) return;
            SoftReference<Drawable> thumblist = appData.getPriviewDrawable();
            if (thumblist != null ){
                Drawable preview = thumblist.get();
                String path = saveDrawableToFile(preview, appData.getPreviewAddr(), mtypeindex);
                previewPath += path;
            }
            saveDownloadInfo(appData, null,previewPath);
        }
        else if( mtypeindex == 1 || mtypeindex ==2) {
            AmsApplication appData = null;
            if(mtypeindex == 1){
                for(int i=0;i<mLeConstant.mServiceThemeAmsDataList.size();i++){
                    appData = mLeConstant.mServiceThemeAmsDataList.get(i);
                    if(pkg.equals(appData.getPackage_name())){
                        break;
                    }
                }
            }
            String name = pkg + "." + mVersionCode;
            //preview
            String previewPath = "";
            if(appData == null) return;
            SoftReference<Drawable> []thumblist = appData.thumbdrawables;
            
            try{
            if(appData.thumbdrawables.length != appData.thumbpaths.length){
            	return;
            }}catch(Exception e){
            	return;
            }
            for(int pos = 0; pos < thumblist.length; pos++){
            	Log.e(TAG, "DownloadThumbnails: pos = " + pos);
                if(pos > 0) previewPath +=",";
                String url = appData.thumbpaths[pos];
                if(url == null || url.equals("")){
                	continue;
                }
                Drawable preview = null;
                if(thumblist[pos] != null){
                	preview = thumblist[pos].get();
                }
                String path = saveDrawableToFile(preview, url, mtypeindex);
                previewPath += path;
            }
            Log.e(TAG,"newpreviewPath ===="+mtypeindex+" name====="+name+" path======="+previewPath+
            		", themeflag =========="+LEJPConstant.getInstance().mThemeNeedRefresh+" lockflag"+ LEJPConstant.getInstance().mLockscreenNeedRefresh);
            saveDownloadInfo(appData, previewPath, mtypeindex);
        }
    }
    private String saveDrawableToFile(Drawable drawable, String fileName, int type){
    	String path = getLocationToSave(fileName, type);
    	File f = new File(path);
    	if(f.exists()){
    		Log.e("mohl","==44444== saveDrawableToFile: file = "+path);
    		return path;
    	}else{
    		if(drawable != null){
	    		Bitmap bmp = ((BitmapDrawable)drawable).getBitmap();
	    		final ByteArrayOutputStream os = new ByteArrayOutputStream();
	    		CompressFormat format;
	    		if(fileName.toLowerCase().endsWith("png")){
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
    			asyncImageLoader.loadDrawable(null,fileName,-1, 0, new ImageCallback() {  
			            public void imageLoaded(View img,Drawable imageDrawable, int position,int j) { 
//			            	if(imageDrawable != null){
//			            	}
			            }  
			        }); 
    			return path;
    		}
    	}
	}
    
    public static void backupLocalWallpaperPreview(Context context, String key){
    	Log.i("mohl","===backupLocalWallpaperPreview: key = "+key);
    	SharedPreferences sp_preview = context.getSharedPreferences(SP_WALLPAPER_PREVIEW_URL, 0);
    	if(sp_preview != null){
			String previewFile = sp_preview.getString(key, "");
			String oldpath = LEJPConstant.getDownloadPath() + "/wallpapers";
			String newpath = LEJPConstant.getDownloadPath()
					+ "/local_wallpapers";
			File dir = new File(newpath);
			if (!dir.exists()) {
				dir.mkdir();
			}
			if (previewFile.startsWith(oldpath)) {
				String newFile = previewFile.replace(oldpath, "");
				newpath = newpath + newFile;
			} else {
				return;
			}
			File f = new File(previewFile);
			if (f.exists()) {
				Log.i("mohl", "backupLocalWallpaperPreview preview src exist ");
				try {
					copyFile(f, new File(newpath));
					SharedPreferences.Editor editor = sp_preview.edit();
					editor.putString(key, newpath).commit();
					copySharedPreferencesToSDCard(context,
							SP_WALLPAPER_PREVIEW_URL, SP_WALLPAPER_PREVIEW_URL);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.i("mohl", "backupLocalWallpaperPreview previewfile ioException:"+e.getMessage());
					e.printStackTrace();
				}
			}
    	}
    	SharedPreferences sp_icon = context.getSharedPreferences(SP_WALLPAPER_ICON_URL, 0);
    	if(sp_icon != null){
    		String iconFile = sp_icon.getString(key, "");
    		if(!iconFile.equals("")){
				String oldpath = LEJPConstant.getDownloadPath() + "/wallpapers";
				String newpath = LEJPConstant.getDownloadPath() + "/local_wallpapers";
				File dir = new File(newpath);
				if (!dir.exists()) {
					dir.mkdir();
				}
				if (iconFile.startsWith(oldpath)) {
					String newFile = iconFile.replace(oldpath, "");
					newpath = newpath + newFile;
				}else{
					return;
				}
				File f = new File(iconFile);
				if (f.exists()) {
					try {
						copyFile(f, new File(newpath));
						SharedPreferences.Editor editor = sp_icon.edit();
						editor.putString(key, newpath).commit();
						copySharedPreferencesToSDCard(context,
								SP_WALLPAPER_ICON_URL,
								SP_WALLPAPER_ICON_URL);
					} catch (IOException e) {
						Log.i("mohl", "backupLocalWallpaperPreview iconfile ioException:"+e.getMessage());
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
    		}
    	}
    }

    public static void copyFile(File oldf, File newf) throws IOException {
       InputStream in = new FileInputStream(oldf);
       OutputStream out = new FileOutputStream(newf);
       byte[] buf = new byte[1024];
       int len;
       while((len = in.read(buf)) > 0){
    	   out.write(buf);
       }
       in.close();
       out.close();
    }
    
     public boolean isSystemApp(String pkgName) {
                boolean ret = false;
                try {
                        ApplicationInfo ai = getPackageManager().getApplicationInfo(pkgName, 0);
                        ret = (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
                        return ret;
                } catch (Exception e) {
                      e.printStackTrace();
                        return false;
                }
    }

     private static boolean copySharedPreferencesToSDCard(Context context, String spName, String filename){
    	 Log.i("mohl","=== copySharedPreferencesToSDCard detailClassicActivity");
    	 LocalDataManager mgr = LocalDataManager.getInstance(context);
    	 boolean res = mgr.writeDataToSDCard(spName, filename);
    	 return res;
     }
     
    @Override
    public void onDestroy() {
        checkCurrentLiveWall(false);
        if(mDownloadStatus == 1){
			setDownloadingStatus(mPackageName, mDownloadStatus);
        }
		
        Log.e(TAG,"onDestroy<F2><F2><F2><F2><F2><F2><F2><F2><F2><F2><F2><F2><F2><F2><F2>");
        mLeConstant.mLemActivityonResumeFlag = false;
        this.unregisterReceiver(mLocaleReceiver);
        this.unregisterReceiver(mUiReceiver);
        //android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }


}
