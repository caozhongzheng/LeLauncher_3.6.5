
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
import android.content.DialogInterface;
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
import android.widget.FrameLayout;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import java.util.Random;

import android.widget.Button;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import android.view.View.OnClickListener;

import org.xmlpull.v1.XmlPullParserException;

import android.view.KeyEvent;

import java.io.IOException;


import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


import android.view.Display;
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

import android.widget.ImageView;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.zip.GZIPInputStream;
import java.util.HashMap;


import android.widget.RadioGroup;
import android.widget.RadioButton;
import com.lenovo.lejingpin.AsyncImageLoader.ImageCallback;
import com.lenovo.lejingpin.Holder;
import com.lenovo.lejingpin.hw.content.data.HwConstant;

import android.widget.AdapterView;
import android.view.GestureDetector;


import com.lenovo.lejingpin.hw.ui.AppGallery;
import com.lenovo.lejingpin.hw.ui.Util;

import android.widget.Gallery;
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

import android.widget.ImageView.ScaleType;


import java.util.ArrayList;  
import java.util.List;  
import java.util.concurrent.Executors;  
import java.util.concurrent.ScheduledExecutorService;  
import java.util.concurrent.TimeUnit;  

import android.widget.ListView;


import android.view.Gravity;

import android.view.WindowManager.LayoutParams;


import android.view.WindowManager;
import android.graphics.PixelFormat;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;

import com.lenovo.launcher2.commonui.LeAlertDialog;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.Utilities;

import com.lenovo.launcher2.customizer.GlobalDefine;
import com.lenovo.launcher2.customizer.Reaper;
import com.lenovo.launcher2.settings.MoreSettings;
public class AndroidWallpaperFragment extends Fragment{
 
    //public static final String HOST_WALLPAPER = "http://launcher.lenovo.com/launcher/lezhuomian.php";
    public static final String HOST_WALLPAPER = "http://launcher.lenovo.com/wp/";
	public final static String SP_WALLPAPER_URL = "download_wallpaper_url";
	public final static String SP_WALLPAPER_ICON_URL = "download_icon_url";
	public final static String SP_WALLPAPER_PREVIEW_URL = "download_preview_url";
	public final static String SP_WALLPAPER_NAME = "download_wallpaper_name";
	public final static String SP_CURRENT = "CURRENT";
	private final static String EXCLUDED_SETTING_KEY = "exclude_from_backup";

    private final static String TAG = "AndroidFragment";
    private static final int MSG_GET_HOT_LIST = 98;
    private static final int MSG_GET_NEW_LIST = 99;
    private static final int MSG_GETLIST = 100;
    private static final int MSG_CHECKNETWORK = 101;
    private static final int MSG_NO_MORE = 102;
    private static final int MSG_DELAY_CREATEVIEW = 103;
    private static final int MSG_GETDRAWABLE = 104;
    private static final int MSG_GETWAIT = 105;
    private static final int MSG_GET_AUTO = 106;
    private static final int MSG_GET_LEAK = 107;
    private static final int MSG_GETSPECIAL = 108;
    private static final int MSG_REFRESH_VIEW = 109;
    private static final int MSG_GET_NEW_PREVIEW = 120;
    private static final int MSG_GET_HOT_PREVIEW = 121;


    private int curStepIndex = 0;
    private View loadView;
    private View AllView;
    private HashMap<String, SoftReference<Drawable>> mIconList =
            new HashMap<String, SoftReference<Drawable>>();

    private HashMap<String, SoftReference<Drawable>> mHotIconList =
            new HashMap<String, SoftReference<Drawable>>();
    private HashMap<String, SoftReference<Drawable>> mNewIconList =
            new HashMap<String, SoftReference<Drawable>>();
    private HashMap<String, SoftReference<Drawable>> mPreviewList =
            new HashMap<String, SoftReference<Drawable>>();
    private ArrayList<Integer> idList = new ArrayList<Integer>();
    private ArrayList<String> mIconUrlList = new ArrayList<String>();
    private ArrayList<String> mHotIconUrlList = new ArrayList<String>();
    private HashMap<String, Drawable> mDetailIconList =
           new HashMap<String, Drawable>();

    LEJPConstant mLeConstant = LEJPConstant.getInstance();
    private ArrayList<ApplicationData> mSpecialDataList; //local data
    private ArrayList<ApplicationData> mDataList;    //online data
    private ArrayList<ApplicationData> mSubDataList;

    private AppsNewAdapter mAppsNewAdapter;
    private AppsHotAdapter mAppsHotAdapter;
    private  ArrayList<ApplicationData> mNewDataList;
    private  ArrayList<ApplicationData> mNewSubDataList;
    private  ArrayList<ApplicationData> mHotDataList;
    private  ArrayList<ApplicationData> mHotSubDataList;
    private View view2;
    private View view2_land;
    private View view3;
    private View view3_land;
    private GridView mGridNew;
    private GridView mGridHot;
    private HashMap<Integer, ApplicationData> mNewWallpaperHashmap =
            new HashMap<Integer, ApplicationData>();
    private HashMap<Integer, ApplicationData> mHotWallpaperHashmap =
            new HashMap<Integer, ApplicationData>();
    private ArrayList<String> mLoadingList = new ArrayList<String>();    //online data
     
    private boolean mNoMoreFlag = false;
    private boolean mNoMoreHotFlag = false;
    private boolean mNoMoreNewFlag = false;

    private ProgressBar progressBarVNew;
    private TextView loading_textVNew;
    private Button loading_refreshNew;

    private ProgressBar progressBarVHot;
    private TextView loading_textVHot;
    private Button loading_refreshHot;


    private int mNewStartIndex = 0;
    private int mHotStartIndex = 0;
    private int mNewCount = 18;
    private int mHotCount = 18;
    private boolean updownflag = false;
    private    int ya = 0;
    private     int yb = 0;


    private View startEmptyView;
    private View startNewEmptyView;
    private View startHotEmptyView;


    private final int type_local = 10;
    private int flingtime = 4;

    private int mCategorytype = -1;

    private final int specialNum = 5;
    private WindowManager mWM;
    private TextView mOverlay;
 
    private ViewPager mPager;
    private MyPagerAdapter mpAdapter;
    private MyAdapter specialAdapter;

    private List listViews;

    int mNum;
    private int mStartIndex = 0;
    private int mCount = 8;
    private AsyncImageLoader asyncImageLoader;
    private int mCurrentNewLoadingPos = 0;
    private int mCurrentHotLoadingPos = 0;
    private boolean mStopGetPreviewFlag = false;
    private int mCurrentNewPos = 0;
    private final int PREVIEW_TYPE_NEW = 0;
    private final int PREVIEW_TYPE_CATEGORY = 1;
    private final int PREVIEW_TYPE_HOT = 2;
    
    private int mOrientation;
    private int mLastCategoryOrientation;
    private int mLastNewOrientation;
    private int mLastHotOrientation;
    private int mCurrentPageItem = 1;
    private final int MAX_WALLPAPER_CATEGORY = 23; 
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         
       final long waitTime = SystemClock.uptimeMillis();

        mDetailIconList.clear();
        idList.clear();
        mNewWallpaperHashmap.clear();
        mHotWallpaperHashmap.clear();
//        setDrawablePkgNameList .clear();
        mLoadingList.clear();    //online data
        
        mNewIconList.clear();
        mHotIconList.clear();
        mIconUrlList.clear();
        mHotIconUrlList.clear();

        mLi = LayoutInflater.from(getActivity());
        Log.d(TAG, "---onCreate---");
        asyncImageLoader = new AsyncImageLoader(getActivity(),0);
        mWM = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        mOrientation = this.getResources().getConfiguration().orientation;
        mLastCategoryOrientation = mOrientation;
        mLastNewOrientation = mOrientation;
        mLastHotOrientation = mOrientation;
        Log.d(TAG, "waited onCreate ="+ (SystemClock.uptimeMillis()-waitTime) + "ms for previous step to finish binding");
        
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    	Log.e("XXXX","=====new orientation is "+newConfig.orientation+ ", mOrientation = "+mOrientation);
    	if(newConfig.orientation != mOrientation){
    		mOrientation = newConfig.orientation;
            if(mPager == null) return;
            if(mCurrentPageItem == 1){
            	hideOverLay();
            	mNoMoreHotFlag = false;
            	if(mDataList != null){
            		mStartIndex = mDataList.size();//homeLayout.getChildCount() - 1;
            	}else{
            		mStartIndex = 0;
            	}
            }
            if(mpAdapter != null){
            	delayCreateView(); 
                setCurrentPage(mCurrentPageItem);
                reloadCurrentPageData(mCurrentPageItem);
            }
    	}
    }
    
    private void reloadCurrentPageData(int pageItem){
    	mInitHandler.removeMessages(MSG_GET_NEW_PREVIEW);
    	mInitHandler.removeMessages(MSG_GET_HOT_PREVIEW);
    	if(pageItem == 0){
        	if((mCurrentNewLoadingPos - mNewCount) > 0){
        		mCurrentNewLoadingPos = mCurrentNewLoadingPos - mNewCount;
        	}else{
        		mCurrentNewLoadingPos = 0;
        	}
        	Log.i("XXXX","reloadCurrentPageData:  mCurrentNewLoadingPos = "+mCurrentNewLoadingPos);
            mInitHandler.sendEmptyMessage(MSG_GET_NEW_PREVIEW);
            startNewEmptyView.setVisibility(View.INVISIBLE);
			mGridNew.setAdapter(mAppsNewAdapter);
			mGridNew.setVisibility(View.VISIBLE);
			mAppsNewAdapter.addMoreContent();
        }else if(pageItem == 2){
        	if((mCurrentHotLoadingPos - mHotCount) > 0){
        		mCurrentHotLoadingPos = mCurrentHotLoadingPos - mHotCount;
        	}else{
        		mCurrentHotLoadingPos = 0;
        	}
        	Log.i("XXXX","reloadCurrentPageData:  mCurrentHotLoadingPos = "+mCurrentHotLoadingPos);
            mInitHandler.sendEmptyMessage(MSG_GET_HOT_PREVIEW);
            startHotEmptyView.setVisibility(View.INVISIBLE);
			mGridHot.setAdapter(mAppsHotAdapter);
			mGridHot.setVisibility(View.VISIBLE);
			mAppsHotAdapter.addMoreContent();
        }else if(pageItem == 1){    //for category wallpaper page, reload data from network
        	if(mSpecialDataList == null || mSpecialDataList.size() == 0 
        			|| mDataList == null || mDataList.size() == 0){
        		mInitHandler.post(runStartInitService);
        	}else{
        		curStepIndex = 0;
        		mStepGetFlag = false;
        		mInitHandler.sendEmptyMessage(MSG_GETSPECIAL);
        		mInitHandler.sendEmptyMessage(MSG_GETLIST);
        	}
        }
    }
 
    private Handler mInitHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_NO_MORE:
                hideOverLay();
                break;
            case MSG_GET_NEW_LIST:
                showNewGridViewContent();
                break;
            case MSG_GET_HOT_LIST:
                showHotGridViewContent();
                break;
            case MSG_GETLIST:
                showGridViewContent();
                break;
            case MSG_GETSPECIAL:
                showSpecialView();
                break;
            case MSG_CHECKNETWORK:
                 Log.e(TAG,"category type ============="+mCategorytype);
                switch(mCategorytype){
                case 1:
                	if(mDataList == null){
                		showErrorView(1, 0);
                	}
                	break;
                case 0:
                	if(mNewDataList == null){
                		showErrorView(0, 0);
                	}
                	break;
                case 2:
                	if(mHotDataList == null){
                		showErrorView(2, 0);
                	}
                	break;
                default:
                	break;
                }
                if(mSpecialDataList == null)
                banner.setVisibility(View.INVISIBLE);
                break;
            case MSG_DELAY_CREATEVIEW:
               delayCreateView();
               setCurrentPage(1);
               mInitHandler.post(runStartInitService);
               break;
            case MSG_GETDRAWABLE:
//            	if(mPager.getCurrentItem() == PREVIEW_TYPE_CATEGORY)
            	Log.i("XXXX","=== handle msg MSG_GETDRAWABLE, current item is "+mPager.getCurrentItem()
            			+", mStartIndex = "+mStartIndex);
            	getDrawableDataFromNetWorkStep();
                break;
            case MSG_GETWAIT:
                getWaitFromNetWorkStep();
                break;
            case MSG_GET_AUTO:
                mTouch = false;
                break;
            case MSG_GET_LEAK:
                Log.e(TAG,"the position GET_Leak Auto <F2><F2><F2><F2> uppppppppppp=");
                mTouch = false;
                break;
            case MSG_REFRESH_VIEW:
            	if(mPager.getCurrentItem() == PREVIEW_TYPE_NEW && mAppsNewAdapter != null){
            		mAppsNewAdapter.addMoreContent();
            	}else if(mPager.getCurrentItem() == PREVIEW_TYPE_HOT && mAppsHotAdapter != null){
            		mAppsHotAdapter.addMoreContent();
            	}
            	break;
            case MSG_GET_NEW_PREVIEW:
            	getPreviewImgOneByOne(PREVIEW_TYPE_NEW);
            	break;
            case MSG_GET_HOT_PREVIEW:
            	getPreviewImgOneByOne(PREVIEW_TYPE_HOT);
            	break;
            default:
            	break;
            }
        }
    };
    private void showGridViewContent(){
    	Log.i("XXXX","===== showGridViewContent =========");
        if (mStartIndex ==0){
            if((mDataList == null) || mDataList.isEmpty()){
                 startHotEmptyView.setVisibility(View.VISIBLE);
                 return;
            }
//            replaceView();       
        }
        mStartIndex = mDataList.size();
        replaceView(); 
        getDrawableDataFromNetWorkStep();
    }
    private void showHotGridViewContent(){
    	Log.i("XXXX","===== showHotGridViewContent =========");
		if (mHotStartIndex == 0) {
			if ((mHotDataList == null) || mHotDataList.isEmpty()) {
				startHotEmptyView.setVisibility(View.VISIBLE);
				return;
			}
			mGridHot.setAdapter(mAppsHotAdapter);
			startHotEmptyView.setVisibility(View.INVISIBLE);
			mGridHot.setVisibility(View.VISIBLE);
		} else {
			mAppsHotAdapter.addMoreContent();
		}
//        mPager.setCurrentItem(2);
    }
    private void showNewGridViewContent(){
    	Log.i("XXXX","===== showNewGridViewContent =====mNewStartIndex = "+mNewStartIndex);
		if (mNewStartIndex == 0) {
			if ((mNewDataList == null) || mNewDataList.isEmpty()) {
				startNewEmptyView.setVisibility(View.VISIBLE);
				return;
			}
			Log.i("XXXX", "===== startNewEmptyView set INVISIBLE");
			startNewEmptyView.setVisibility(View.INVISIBLE);
			mGridNew.setAdapter(mAppsNewAdapter);
		} else {
			mAppsNewAdapter.addMoreContent();
		}
//        mPager.setCurrentItem(0);
    }
    private void showErrorView(int type ,int flag){
		try {
			if (type == 1) {
				progressBarV.setVisibility(View.GONE);
				CharSequence text = getText(R.string.le_list_empty);
				if (flag == 1) {
					text = getText(R.string.le_list_empty);
				} else {
					text = getText(R.string.grid_empty_error);
				}
				loading_textV.setText(text.toString());
				loading_refresh.setVisibility(View.VISIBLE);
			} else if (type == 0) {
				progressBarVNew.setVisibility(View.GONE);
				CharSequence text = getText(R.string.le_list_empty);
				if (flag == 1) {
					text = getText(R.string.le_list_empty);
				} else {
					text = getText(R.string.grid_empty_error);
				}
				loading_textVNew.setText(text.toString());
				loading_refreshNew.setVisibility(View.VISIBLE);
			} else if (type == 2) {
				progressBarVHot.setVisibility(View.GONE);
				CharSequence text = getText(R.string.le_list_empty);
				if (flag == 1) {
					text = getText(R.string.le_list_empty);
				} else {
					text = getText(R.string.grid_empty_error);
				}
				loading_textVHot.setText(text.toString());
				loading_refreshHot.setVisibility(View.VISIBLE);
			}
		} catch (Exception e) {
		}
    }

    private void startWallpaperTypeActivity(String title,String type){
    	Log.i("XXXX","========= startWallpaperTypeActivity: title = "+title+", type = "+type);
         Intent intent = new Intent(getActivity(), ShowWallpaperTypeDetailActivity.class);
         intent.putExtra("CALLFROM",0);
         intent.putExtra("TITLE",title);
         intent.putExtra("TYPE",type);
         startActivity(intent);
    }
    private void startWallpaperSpecialActivity(String title,String type,String thumbUrl){
         Intent intent = new Intent(getActivity(), ShowWallpaperSpecialActivity.class);
         intent.putExtra("TITLE",title);
         intent.putExtra("TYPE",type);
         intent.putExtra("THUMBURL",thumbUrl);
         startActivity(intent);
    }
    private void startWallpaperDetailActivity(int type,int pos,ArrayList<ApplicationData> DataList){

        Intent intent = new Intent(getActivity(), DetailClassicActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("EXTRA",pos);
        intent.putExtra("TYPEINDEX",type);
        intent.putExtra("TYPEDATA",DataList);
        startActivity(intent);

   }



    public boolean getSpecialValue(){
        if (SettingsValue.getCurrentMachineType(getActivity()) != -1){
            if (!SettingsValue.isCurrentPortraitOrientation(getActivity())){
                return true;
            }
        }
        return false;
    }

    public String getWallpaperTypeUrl(){
        StringBuffer url = new StringBuffer(HOST_WALLPAPER);
        if(getSpecialValue()) mCount = 8;
        url.append("cate/list?")
        .append("s=").append(mStartIndex).append("&t=").append(mCount)
        .append("&time=").append(new Date().getDate());
        if (SettingsValue.getCurrentMachineType(getActivity()) != -1){
            url.append("&dynamic=0");
        }
        Log.i("XXXX", "getWallpaperTypeUrl: url=" + url.toString()+", mStartIndex = "+mStartIndex
        		+", mCount = "+mCount);
        Log.i(TAG, "Wallpaper type , url=" + url.toString());
        return url.toString();
    }
    public String getWallpaperSpecialUrl(){
        StringBuffer url = new StringBuffer(HOST_WALLPAPER);
        url.append("special?")
        .append("s=").append(0).append("&t=").append(specialNum)
        .append("&time=").append(new Date().getDate());

        Log.i(TAG, " Speicail WallpaperinfoRequest, url=" + url.toString());
        return url.toString();
    }
       public String getHotUrl() {
        StringBuffer url = new StringBuffer(HOST_WALLPAPER);
        url.append("hot?")
        .append("&s=").append(mHotStartIndex).append("&t=").append(mHotCount)
        .append("&time=").append(new Date().getDate());
        if (SettingsValue.getCurrentMachineType(getActivity()) != -1){
            url.append("&dynamic=0");
        }


        Log.i(TAG, "get hot WallpaperinfoRequest, url=" + url.toString());
        return url.toString();
    }
    public String getNewUrl() {
        StringBuffer url = new StringBuffer(HOST_WALLPAPER);
        url.append("new?")
        .append("&s=").append(mNewStartIndex).append("&t=").append(mNewCount)
        .append("&time=").append(new Date().getDate());
        if (SettingsValue.getCurrentMachineType(getActivity()) != -1){
            url.append("&dynamic=0");
        }
        Log.i(TAG, "getNew url WallpaperinfoRequest, url=" + url.toString());
        return url.toString();
    }

    public String getTotalCountUrl(){
   	 StringBuffer url = new StringBuffer(HOST_WALLPAPER);
        url.append("count");
        Log.i(TAG, "getTotalCountUrl, url=" + url.toString());
        return url.toString();
   }
    
    public String getUrl(int type){
        switch (type) {
            case 1:
            return getWallpaperTypeUrl();
            case 0:
            return getNewUrl();
            case 2:
            return getHotUrl();
            case 3:
            return getTotalCountUrl();
        }
        return getNewUrl();
    }

    private void getSpicailDataFromNetwork(){
         new Thread() {
            public void run() {
                NetworkHttpRequest request = new NetworkHttpRequest();
                String mtypes = "specials";
                final WallpaperResponse response = new WallpaperResponse();
                response.setResponseType(mtypes,phoneWidth);
                request.executeHttpGet(getActivity(),getWallpaperSpecialUrl(),
                new AmsCallback(){
                    public void onResult(int code, byte[] bytes) {
                        Log.i(TAG,"get Special data AmsSession.init >> result code:"+ code);
                        if(code==200){
                            if(bytes!=null && bytes.length!=0){
                                response.parseFrom(bytes);
                                mSpecialDataList = response.getApplicationItemList();
                                Log.i(TAG,"specials result datasize:"+ mSpecialDataList.size());
                                if(mSpecialDataList.size() != 0)
                                mInitHandler.sendEmptyMessage(MSG_GETSPECIAL);
                            }
                        }else{
                            mInitHandler.removeMessages(MSG_CHECKNETWORK);
                            mInitHandler.sendMessageDelayed(mInitHandler.obtainMessage(MSG_CHECKNETWORK), 2000);

                        }
                    }
                });
            }
        }.start();
    }

    private void getDataFromNetwork(final int type, final int beginIndex){
    	Log.i("XXXX","=============getDataFromNetwork: type = "+type+", begin = "+beginIndex);
        Log.e("yumin0429","getDataFromNetwor  beginIndex="+beginIndex+" newstartindex ="+mNewStartIndex);
        getWallpaperTotalCount();
        mCategorytype = type;
        new Thread() {
            public void run() {
                NetworkHttpRequest request = new NetworkHttpRequest();
                String mtypes = "wallpapers";
                if(type == 1){
                    mtypes ="cate";
                }else{
                    mtypes ="wallpapers";
                }
                final WallpaperResponse response = new WallpaperResponse();
                response.setResponseType(mtypes,phoneWidth);
                response.setDataIndex(beginIndex);
                request.executeHttpGet(getActivity(),getUrl(type),
                new AmsCallback(){
                    public void onResult(int code, byte[] bytes) {
                        Log.i(TAG,"AppInfoAction.requestAppInfo, AmsSession.init >> result code:"+ code);
                        if(code==200){
                            if(bytes!=null && bytes.length!=0){
                                response.parseFrom(bytes);
                                if(type == 1){
	                                if( mStartIndex == 0){
	                                    mDataList = response.getApplicationItemList();
	                                    Log.i(TAG,"requestAppInfo, AmsSession.init >> result datasize:"+ mDataList.size());
	                                    //parseDataList(mDataList);
	                                    Log.i("XXXX","===mStartIndex 0==getDataFromNetwork: mDataList size = "+mDataList.size());
	                                }else{
	                                    mDataList = response.getApplicationItemList();
	                                    Log.i(TAG,"requestAppInfo, AmsSession.init >> result datasize:"+ mDataList.size());
	                                    curStepIndex = 0;
	                                    //mDataList.addAll(response.getApplicationItemList());
	                                    Log.i("XXXX","=============getDataFromNetwork: mDataList size = "+mDataList.size());
	                                    if(mDataList.size() == 0){
	                                        mNoMoreFlag = true;
	                                        return;
	                                    }
	
	                                }
                                }else if(type == 2){
	                                if( mHotStartIndex == 0){
	                                    mHotDataList = response.getApplicationItemList();
	                                    mHotSubDataList = response.getApplicationItemList();
	                                }else{
	                                    mHotSubDataList = response.getApplicationItemList();
	                                    if(mHotSubDataList.size() == 0){
	                                        mNoMoreHotFlag = true;
	                                        return;
	                                    }
//	                                    mHotDataList.addAll(mHotSubDataList);
	                                }
	                                encloseWallpaperHashmap(mHotSubDataList, mCategorytype);
                                }else if(type == 0){
	                                if( mNewStartIndex == 0){
	                                    mNewDataList = response.getApplicationItemList();
	                                    mNewSubDataList = response.getApplicationItemList();
	                                    Log.i("XXXX","===mNewStartIndex 0==getDataFromNetwork: mNewDataList size = "+mNewDataList.size());
	                                }else{
	                                    mNewSubDataList = response.getApplicationItemList();
	                                    Log.i("XXXX","========getDataFromNetwork: mNewSubDataList size = "+mNewSubDataList.size());
		                                if(mNewSubDataList.size() == 0){
		                                    mNoMoreNewFlag = true;
		                                    return;
	                                    }
	                                }
	                                encloseWallpaperHashmap(mNewSubDataList, mCategorytype);
                                }
                                
                            }
                        }else{
                            mInitHandler.removeMessages(MSG_CHECKNETWORK);
                            mInitHandler.sendMessageDelayed(mInitHandler.obtainMessage(MSG_CHECKNETWORK), 2000);

                        }
                    }
                });
            }
        }.start();
    }

	private void encloseWallpaperHashmap(ArrayList<ApplicationData> mList, int pagetype) {
		final int type = pagetype;
		Log.i("XXXX","====encloseWallpaperHashmap: type = "+type);
		if (getActivity() != null) {
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					if(type == PREVIEW_TYPE_NEW){
						if (mNewStartIndex == 0) {
							if ((mNewSubDataList == null)|| (mNewSubDataList.isEmpty())) {
								showErrorView(mCategorytype, 1);
								startNewEmptyView.setVisibility(View.VISIBLE);
								return;
							}
							startNewEmptyView.setVisibility(View.INVISIBLE);
							mGridNew.setAdapter(mAppsNewAdapter);
						}
						// load preview image one by one
						mCurrentNewLoadingPos = 0;
						int currentViewPos = mNewStartIndex + mCurrentNewLoadingPos;
						int currentLastPos = mNewStartIndex + mNewCount;
						while (null != mNewWallpaperHashmap.get(currentViewPos)
								&& (currentViewPos < currentLastPos)) {
							mCurrentNewLoadingPos++;
							currentViewPos++;
						}
						mInitHandler.sendEmptyMessage(MSG_GET_NEW_PREVIEW);
					}else if(type == PREVIEW_TYPE_HOT){
						//hot 
						if (mHotStartIndex == 0) {
							if ((mHotSubDataList == null)|| (mHotSubDataList.isEmpty())) {
								showErrorView(mCategorytype, 1);
								startHotEmptyView.setVisibility(View.VISIBLE);
								return;
							}
							startHotEmptyView.setVisibility(View.INVISIBLE);
							mGridHot.setAdapter(mAppsHotAdapter);
						}
						// load preview image one by one
						mCurrentHotLoadingPos = 0;
						int currentViewPos = mHotStartIndex + mCurrentHotLoadingPos;
						int currentLastPos = mHotStartIndex + mHotCount;
						while (null != mHotWallpaperHashmap.get(currentViewPos)
								&& (currentViewPos < currentLastPos)) {
							mCurrentHotLoadingPos++;
							currentViewPos++;
						}
						mInitHandler.sendEmptyMessage(MSG_GET_HOT_PREVIEW);
					}
				}
			});
		}
	}
	
	private void getPreviewImgOneByOne(int type){
		ApplicationData appdata = null;
		switch(type){
		case PREVIEW_TYPE_NEW:
			if(mNewSubDataList == null || mCurrentNewLoadingPos >= mNewSubDataList.size() || mCurrentNewLoadingPos < 0){
				mInitHandler.removeMessages(MSG_GET_NEW_PREVIEW);
				return;
			}
			appdata = mNewSubDataList.get(mCurrentNewLoadingPos);
			break;
		case PREVIEW_TYPE_HOT:
			if(mHotSubDataList == null || mCurrentHotLoadingPos >= mHotSubDataList.size() || mCurrentHotLoadingPos < 0){
				mInitHandler.removeMessages(MSG_GET_HOT_PREVIEW);
				return;
			}
			appdata = mHotSubDataList.get(mCurrentHotLoadingPos);
			break;
		default:
			break;
		}
		if(appdata != null){
			getPreviewImgFromNetWork(appdata,null, type);
		}
	}
	
	private void getPreviewImgFromNetWork(ApplicationData data, ImageView image, int type){
		final String pkgName = data.getPackage_name();
		final String previewUrl = data.previewAddr;
		final ApplicationData mdata = data;
		final int preview_type = type;
		if (!mNewIconList.containsKey(pkgName)) {
			if (!mIconUrlList.contains(pkgName)) {
				mIconUrlList.add(pkgName);
			}
			asyncImageLoader.loadDrawable(image, previewUrl, 0, 0,
					new ImageCallback() {
						public void imageLoaded(final View aimage,
								final Drawable imageDrawable, int postion, int j) {
							if(preview_type == PREVIEW_TYPE_NEW){
								mNewIconList.put(pkgName, new SoftReference<Drawable>(imageDrawable));
								if (mNewStartIndex != 0 && null == mNewWallpaperHashmap.get(mdata.getViewPosition())) {
                                                                        if(mNewDataList != null){
									mNewDataList.add(mdata);
                                                                        }
								}
								mNewWallpaperHashmap.put(mdata.getViewPosition(), mdata);
								mCurrentNewLoadingPos++;
								mInitHandler.removeMessages(MSG_GET_NEW_PREVIEW);
								if (!mStopGetPreviewFlag) {
									mInitHandler.sendEmptyMessageDelayed(MSG_GET_NEW_PREVIEW, 100);
									mInitHandler.sendEmptyMessage(MSG_REFRESH_VIEW);
								}
							}else if(preview_type == PREVIEW_TYPE_HOT){
								mHotIconList.put(pkgName, new SoftReference<Drawable>(imageDrawable));
								if (mHotStartIndex != 0 && null == mHotWallpaperHashmap.get(mdata.getViewPosition())) {
                                                                        if(mHotDataList != null){
									mHotDataList.add(mdata);
                                                                        }
								}
								mHotWallpaperHashmap.put(mdata.getViewPosition(), mdata);
								mCurrentHotLoadingPos++;
								mInitHandler.removeMessages(MSG_GET_HOT_PREVIEW);
								if (!mStopGetPreviewFlag) {
									mInitHandler.sendEmptyMessageDelayed(MSG_GET_HOT_PREVIEW, 100);
									mInitHandler.sendEmptyMessage(MSG_REFRESH_VIEW);
								}
							}
						}
					});
		}else{
			asyncImageLoader.loadDrawable(image, previewUrl, 0, 0,
					new ImageCallback() {
				public void imageLoaded(final View aimage,
						final Drawable imageDrawable, int postion, int j) {
					if(preview_type == PREVIEW_TYPE_NEW){
						mCurrentNewLoadingPos++;
						mInitHandler.removeMessages(MSG_GET_NEW_PREVIEW);
						if (!mStopGetPreviewFlag) {
							mInitHandler.sendEmptyMessageDelayed(MSG_GET_NEW_PREVIEW, 100);
							mInitHandler.sendEmptyMessage(MSG_REFRESH_VIEW);
						}
					}else if(preview_type == PREVIEW_TYPE_HOT){
						mCurrentHotLoadingPos++;
						mInitHandler.removeMessages(MSG_GET_HOT_PREVIEW);
						if (!mStopGetPreviewFlag) {
							mInitHandler.sendEmptyMessageDelayed(MSG_GET_HOT_PREVIEW, 100);
							mInitHandler.sendEmptyMessage(MSG_REFRESH_VIEW);
						}
					}
				}});
		}
	}
	
	
    /** get the Joson data. */
    private void getDataFromNetwork(final int type){
    	Log.i("XXXX","========= getDataFromNetwork: type = "+type);
    	getWallpaperTotalCount();
        mCategorytype = type;
        if(type == 1){
        	if(mDataList != null && mDataList.size() >= MAX_WALLPAPER_CATEGORY){
        		mNoMoreFlag = true;
        		return;
        	}
        }
        new Thread() {
            public void run() {
                NetworkHttpRequest request = new NetworkHttpRequest();
                String mtypes = "wallpapers";
                if(type == 1){
                    mtypes ="cate";
                }else{
                    mtypes ="wallpapers";
                }
                final WallpaperResponse response = new WallpaperResponse();
                response.setResponseType(mtypes,phoneWidth);
                request.executeHttpGet(getActivity(),getUrl(type),
                new AmsCallback(){
                    public void onResult(int code, byte[] bytes) {
                        Log.i(TAG,"AppInfoAction.requestAppInfo, AmsSession.init >> result code:"+ code);
                        if(code==200){
                            if(bytes!=null && bytes.length!=0){
                                response.parseFrom(bytes);
                                if(type == 1){
                                if( mStartIndex == 0){
                                    mDataList = response.getApplicationItemList();
                                    Log.i(TAG,"requestAppInfo, AmsSession.init >> result datasize:"+ mDataList.size());
                                    Log.i("XXXX","===mStartIndex 0==getDataFromNetwork 1: mDataList size = "+mDataList.size());
                                    //parseDataList(mDataList);
                                }else{
                                   /* mDataList = response.getApplicationItemList();
                                    Log.i(TAG,"requestAppInfo, AmsSession.init >> result datasize:"+ mDataList.size());
                                    curStepIndex = 0;
                                    //mDataList.addAll(response.getApplicationItemList());
                                    if(mDataList.size() == 0){
                                        mNoMoreFlag = true;
                                        return;
                                    }*/
                                	curStepIndex = mDataList.size();
                                	mSubDataList = response.getApplicationItemList();
                                    Log.i(TAG,"requestAppInfo, AmsSession.init >> result datasize:"+ mDataList.size());
                                    Log.i("XXXX","====getDataFromNetwork 1: mDataList size = "+mDataList.size());
                                    if(mSubDataList.size() == 0){
                                        mNoMoreFlag = true;
                                        return;
                                    }
                                    mDataList.addAll(mSubDataList);
                                    mStartIndex = mDataList.size();
                                }
                                mInitHandler.sendEmptyMessage(MSG_GETLIST);
                                }else if(type == PREVIEW_TYPE_HOT){
                                if( mHotStartIndex == 0){
                                    mHotDataList = response.getApplicationItemList();
                                }else{
                                    mHotSubDataList = response.getApplicationItemList();
                                    if(mHotSubDataList.size() == 0){
                                        mNoMoreHotFlag = true;
                                        return;
                                    }
                                    mHotDataList.addAll(mHotSubDataList);
                                }
                                mInitHandler.sendEmptyMessage(MSG_GET_HOT_LIST);
                                }else if(type == PREVIEW_TYPE_NEW){
                                if( mNewStartIndex == 0){
                                    mNewDataList = response.getApplicationItemList();
                                    Log.e(TAG,"fist time  ================sieze======="+mNewDataList.size());
                                }else{
                                    mNewSubDataList = response.getApplicationItemList();
                                    if(mNewSubDataList.size() == 0){
                                    mNoMoreNewFlag = true;
                                    return;
                                    }
                                    mNewDataList.addAll(mNewSubDataList);
                                }
                                Log.e(TAG,"new  ================sieze======="+mNewDataList.size());
                                mInitHandler.sendEmptyMessage(MSG_GET_NEW_LIST);
                                }
                            }
                        }else{
                            //mInitHandler.removeMessages(MSG_CHECKNETWORK);
                            mInitHandler.sendMessageDelayed(mInitHandler.obtainMessage(MSG_CHECKNETWORK), 2000);

                        }
                    }
                });
            }
        }.start();
    }
    
    
	private int mTotalCount = 0;
	private void getWallpaperTotalCount() {
		if (0 == mTotalCount) {
			new Thread() {
				public void run() {
					NetworkHttpRequest request = new NetworkHttpRequest();
					request.executeHttpGet(getActivity(), getUrl(3),
							new AmsCallback() {
								public void onResult(int code, byte[] bytes) {
									Log.i(TAG,"get wallpaper total count result code:"+ code);
									if (code == 200) {
										if (bytes != null && bytes.length != 0) {
											String response = new String(bytes);
											Log.i(TAG,"get wallpaper total count result reponse:"+ response);
											JSONObject jsonObject;
											try {
												jsonObject = new JSONObject(response);
												Iterator it = jsonObject.keys();  
									            while (it.hasNext()) {  
									                String key = (String) it.next();  
									                Log.i(TAG,"get wallpaper total count: key = "+key);
									                if("data".equals(key)){
									                	JSONObject value = jsonObject.getJSONObject(key);
									                	mTotalCount = value.getInt("count");
									                }
									            }
											} catch (JSONException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
												Log.e(TAG,"getWallpaperTotalCount JSONException:"+e.getMessage());
											}
											  
										}
									}
								}
							});
				}
			}.start();
		}
	}
    
    private boolean mStepGetFlag = false;
    private void getDrawableDataFromNetWorkStep(){
//        Log.i(TAG,"get draw begin        LoopLoadDrawable :"+curStepIndex+" datalist size="+mDataList.size());
        Log.i("XXXX","====getDrawableDataFromNetWorkStep: curStepIndex = " + curStepIndex);
        if(getActivity() == null){
        	return;
        }
        if(mDataList == null){
        	return;
        }
		int screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        if(!getSpecialValue()){
        if(curStepIndex >= mDataList.size()){
            //mStartIndex = mStartIndex+mCount;
            mStepGetFlag = true;
            Log.i(TAG,"get draw  dataFromNetworkStpe   end :"+curStepIndex+" datalist size="+mDataList.size());
        }else{
            final String iconUrl = mDataList.get(curStepIndex).getPreviewAddr();
            final String packageName = mDataList.get(curStepIndex).getPackage_name();
            final ApplicationData mdata = mDataList.get(curStepIndex);
            
            View convertViewItem = mLi.inflate(R.layout.android_wallpaper_listitem, null);
            View convertViewItem_land = mLi.inflate(R.layout.android_wallpaper_listitem, null);
            if(phoneWidth <= 480){
                convertViewItem = mLi.inflate(R.layout.android_wallpaper_480listitem, null);
                convertViewItem_land = mLi.inflate(R.layout.android_wallpaper_480listitem, null);
            }
			final TextView wallpaperview = (mOrientation == Configuration.ORIENTATION_LANDSCAPE) ? 
					(TextView) convertViewItem_land.findViewById(R.id.textname)	: 
						(TextView) convertViewItem.findViewById(R.id.textname);
			final LinearLayout image = (mOrientation == Configuration.ORIENTATION_LANDSCAPE) ? 
					(LinearLayout) convertViewItem_land.findViewById(R.id.textpic) : 
						(LinearLayout) convertViewItem.findViewById(R.id.textpic);
			final TextView wallpaperview1 = (mOrientation == Configuration.ORIENTATION_LANDSCAPE) ? 
					(TextView) convertViewItem_land.findViewById(R.id.textname1) : 
						(TextView) convertViewItem.findViewById(R.id.textname1);
			final LinearLayout image1 = (mOrientation == Configuration.ORIENTATION_LANDSCAPE) ? 
					(LinearLayout) convertViewItem_land.findViewById(R.id.textpic1) : 
						(LinearLayout) convertViewItem.findViewById(R.id.textpic1);
            	if (SettingsValue.getCurrentMachineType(getActivity()) == 0) {
					int scale = 328 / 394;// 图片宽高比
					int width = (screenWidth - 24) / 2;
					int height = width * 328/394;
					LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(
							width, height);
					ll.setMargins(8, 0, 0, 0);
					image.setLayoutParams(ll);
					image1.setLayoutParams(ll);
				}

			image.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startWallpaperTypeActivity(mdata.name,packageName);
                }
            });
            if(!mPreviewList.containsKey(packageName) || 
                (mPreviewList.containsKey(packageName) && mPreviewList.get(packageName).get() == null)){
                if(!TextUtils.isEmpty(iconUrl)) {
                    asyncImageLoader.loadDrawable(null, iconUrl,0,0,new ImageCallback() {
                        public void imageLoaded(View imageview,final Drawable imageDrawable, int position,int j) {
                            mPreviewList.put(packageName, new SoftReference<Drawable>(imageDrawable));
                            Log.i(TAG,"0308  222222@@@esult code:"+ iconUrl+" pkgname  curindex ="+curStepIndex);
                            if(getActivity() != null){
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        wallpaperview.setText(mdata.name);
                                        image.setBackgroundDrawable(imageDrawable);
                                    }
                                });
                            }
                        }
                    });
                }
            }else{
                wallpaperview.setText(mdata.name);
                image.setBackgroundDrawable(mPreviewList.get(packageName).get());
            }
            if( curStepIndex+1 ==  mDataList.size()){
                //image1.setVisibility(View.INVISIBLE);
                image1.setAlpha(0);
                image1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //startWallpaperTypeActivity(mdata1.name,packageName1);
                }
                });
            }else{
            final String iconUrl1 = mDataList.get(curStepIndex+1).getPreviewAddr();
            final String packageName1 = mDataList.get(curStepIndex+1).getPackage_name();
            final ApplicationData mdata1 = mDataList.get(curStepIndex+1);
            image1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startWallpaperTypeActivity(mdata1.name,packageName1);
                }
            });
            if(!mPreviewList.containsKey(packageName1) ||
                (mPreviewList.containsKey(packageName1) && mPreviewList.get(packageName1).get() == null)){
                if(!TextUtils.isEmpty(iconUrl1)) {
                    asyncImageLoader.loadDrawable(null, iconUrl1,0,0,new ImageCallback() {
                        public void imageLoaded(View imageview,final Drawable imageDrawable1, int position,int j) {
                            mPreviewList.put(packageName1, new SoftReference<Drawable>(imageDrawable1));
                            Log.i(TAG,"0309  222222@@@esult code:"+ iconUrl1+" pkgname  curindex ="+curStepIndex);
                            if(getActivity() != null){
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        wallpaperview1.setText(mdata1.name);
                                        image1.setBackgroundDrawable(imageDrawable1);
                                    }
                                });
                            }
                        }
                    });
                }
            }else{
                wallpaperview1.setText(mdata1.name);
                image1.setBackgroundDrawable(mPreviewList.get(packageName1).get());
            }
            }
            homeLayout.addView(convertViewItem);
            curStepIndex+=2;
            startLoopLoadDrawable();
	}
		} else {
			Log.i("XXXX",
					"getDrawableDataFromNetWorkStep : getSpecialValue is true");
			if (curStepIndex >= mDataList.size()) {
				// mStartIndex = mStartIndex+mCount;
				mStepGetFlag = true;
				Log.i(TAG, "get draw  dataFromNetworkStpe   end :"+ curStepIndex + " datalist size=" + mDataList.size());
			} else {
				final String iconUrl = mDataList.get(curStepIndex).getPreviewAddr();
				final String packageName = mDataList.get(curStepIndex).getPackage_name();
				final ApplicationData mdata = mDataList.get(curStepIndex);

				View convertViewItem = mLi.inflate(R.layout.android_wallpaper_listitem, null);
				if (phoneWidth <= 480) {
					convertViewItem = mLi.inflate(R.layout.android_wallpaper_480listitem, null);
				}
				final TextView wallpaperview = (TextView) convertViewItem
						.findViewById(R.id.textname);
				final LinearLayout image = (LinearLayout) convertViewItem
						.findViewById(R.id.textpic);
				final TextView wallpaperview1 = (TextView) convertViewItem
						.findViewById(R.id.textname1);
				final LinearLayout image1 = (LinearLayout) convertViewItem
						.findViewById(R.id.textpic1);
				final TextView wallpaperview2 = (TextView) convertViewItem
						.findViewById(R.id.textname2);
				final LinearLayout image2 = (LinearLayout) convertViewItem
						.findViewById(R.id.textpic2);
				final TextView wallpaperview3 = (TextView) convertViewItem
						.findViewById(R.id.textname3);
				final LinearLayout image3 = (LinearLayout) convertViewItem
						.findViewById(R.id.textpic3);
				if (SettingsValue.getCurrentMachineType(getActivity()) == 0) {
					int scale = 328 / 394;// 图片宽高比
					int width = (screenWidth - 35) / 4;//35为间隔空白处像素值
					int height = width * 328/394;
					LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(
							width, height);
					ll.setMargins(7, 0, 0, 0);
					image.setLayoutParams(ll);
					image1.setLayoutParams(ll);
					image2.setLayoutParams(ll);
					image3.setLayoutParams(ll);
				}

				image.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						startWallpaperTypeActivity(mdata.name, packageName);
					}
				});
				if (!mPreviewList.containsKey(packageName)|| (mPreviewList.containsKey(packageName)
						&& mPreviewList.get(packageName).get() == null)) {
					if (!TextUtils.isEmpty(iconUrl)) {
						asyncImageLoader.loadDrawable(null, iconUrl, 0, 0,
								new ImageCallback() {
									public void imageLoaded(View imageview,final Drawable imageDrawable,
											int position, int j) {
										mPreviewList.put(packageName, new SoftReference<Drawable>(imageDrawable));
										Log.i(TAG, "0308  222222@@@esult code:"+ iconUrl+ " pkgname  curindex ="
												+ curStepIndex);
										if (getActivity() != null) {
											getActivity().runOnUiThread(
													new Runnable() {
														public void run() {
															wallpaperview.setText(mdata.name);
															image.setBackgroundDrawable(imageDrawable);
														}
													});
										}
									}
								});
					}
				} else {
					wallpaperview.setText(mdata.name);
					image.setBackgroundDrawable(mPreviewList.get(packageName).get());
				}
				if (curStepIndex + 1 == mDataList.size()) {
					// image1.setVisibility(View.INVISIBLE);
					image1.setAlpha(0);
					image1.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// startWallpaperTypeActivity(mdata1.name,packageName1);
						}
					});
					image2.setAlpha(0);
					image2.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// startWallpaperTypeActivity(mdata1.name,packageName1);
						}
					});
					image3.setAlpha(0);
					image3.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// startWallpaperTypeActivity(mdata1.name,packageName1);
						}
					});
				} else {
					final String iconUrl1 = mDataList.get(curStepIndex + 1)
							.getPreviewAddr();
					final String packageName1 = mDataList.get(curStepIndex + 1)
							.getPackage_name();
					final ApplicationData mdata1 = mDataList
							.get(curStepIndex + 1);
					image1.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							startWallpaperTypeActivity(mdata1.name,
									packageName1);
						}
					});
					if (!mPreviewList.containsKey(packageName1) || (mPreviewList.containsKey(packageName1) 
							&& mPreviewList.get(packageName1).get() == null)) {
						if (!TextUtils.isEmpty(iconUrl1)) {
							asyncImageLoader.loadDrawable(null, iconUrl1, 0, 0,
									new ImageCallback() {
										public void imageLoaded(View imageview,final Drawable imageDrawable1,
												int position, int j) {
											mPreviewList.put(packageName1,
											new SoftReference<Drawable>(imageDrawable1));
											Log.i(TAG,"0309  222222@@@esult code:" + iconUrl1 + " pkgname  curindex ="
															+ curStepIndex);
											if (getActivity() != null) {
												getActivity().runOnUiThread(
														new Runnable() {
															public void run() {
																wallpaperview1.setText(mdata1.name);
																image1.setBackgroundDrawable(imageDrawable1);
															}
														});
											}
										}
									});
						}
					} else {
						wallpaperview1.setText(mdata1.name);
						image1.setBackgroundDrawable(mPreviewList.get(packageName1).get());
					}
					if (curStepIndex + 2 == mDataList.size()) {
						// image1.setVisibility(View.INVISIBLE);
						image2.setAlpha(0);
						image2.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								// startWallpaperTypeActivity(mdata1.name,packageName1);
							}
						});
						image3.setAlpha(0);
						image3.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								// startWallpaperTypeActivity(mdata1.name,packageName1);
							}
						});
					} else {
						final String iconUrl2 = mDataList.get(curStepIndex + 2)
								.getPreviewAddr();
						final String packageName2 = mDataList.get(
								curStepIndex + 2).getPackage_name();
						final ApplicationData mdata2 = mDataList
								.get(curStepIndex + 2);
						image2.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								startWallpaperTypeActivity(mdata2.name,
										packageName2);
							}
						});
						if (!mPreviewList.containsKey(packageName2)
								|| (mPreviewList.containsKey(packageName2) && mPreviewList
										.get(packageName2).get() == null)) {
							if (!TextUtils.isEmpty(iconUrl2)) {
								asyncImageLoader.loadDrawable(null, iconUrl2, 0, 0, new ImageCallback() {
											public void imageLoaded(View imageview,	final Drawable imageDrawable2,
													int position, int j) {
												mPreviewList.put(packageName2, 
														new SoftReference<Drawable>(imageDrawable2));
												Log.i(TAG,"0309  222222@@@esult code:"+ iconUrl2+ " pkgname  curindex ="
																+ curStepIndex);
												if (getActivity() != null) {
													getActivity().runOnUiThread(
														new Runnable() {
															public void run() {
																wallpaperview2.setText(mdata2.name);
																image2.setBackgroundDrawable(imageDrawable2);
															}
													});
												}
											}
										});
							}
						} else {
							wallpaperview2.setText(mdata2.name);
							image2.setBackgroundDrawable(mPreviewList.get(
									packageName2).get());
						}
						if (curStepIndex + 3 == mDataList.size()) {
							// image1.setVisibility(View.INVISIBLE);
							image3.setAlpha(0);
							image3.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									// startWallpaperTypeActivity(mdata1.name,packageName1);
								}
							});
						} else {
							final String iconUrl3 = mDataList.get(
									curStepIndex + 3).getPreviewAddr();
							final String packageName3 = mDataList.get(
									curStepIndex + 3).getPackage_name();
							final ApplicationData mdata3 = mDataList
									.get(curStepIndex + 3);
							image3.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									startWallpaperTypeActivity(mdata3.name,
											packageName3);
								}
							});
							if (!mPreviewList.containsKey(packageName3)
									|| (mPreviewList.containsKey(packageName3) && mPreviewList
											.get(packageName3).get() == null)) {
								if (!TextUtils.isEmpty(iconUrl3)) {
									asyncImageLoader.loadDrawable(null,iconUrl3, 0, 0,
											new ImageCallback() {
												public void imageLoaded(View imageview,final Drawable imageDrawable3,
														int position, int j) {
													mPreviewList.put(packageName3,new SoftReference<Drawable>(imageDrawable3));
													Log.i(TAG,"0309  222222@@@esult code:"+ iconUrl3
													+ " pkgname  curindex ="+ curStepIndex);
													if (getActivity() != null) {
														getActivity().runOnUiThread(
															new Runnable() {
																public void run() {
																	wallpaperview3.setText(mdata3.name);
																	image3.setBackgroundDrawable(imageDrawable3);
																}
															});
													}
												}
											});
								}
							} else {
								wallpaperview3.setText(mdata3.name);
								image3.setBackgroundDrawable(mPreviewList.get(
										packageName3).get());
							}
						}
					}
				}
				homeLayout.addView(convertViewItem);
				curStepIndex += 4;
				startLoopLoadDrawable();
			}
		}
	}

    private void startWaitLoadDrawable(){
        mInitHandler.removeMessages(MSG_GETWAIT);
        mInitHandler.sendMessageDelayed(mInitHandler.obtainMessage(MSG_GETWAIT), 3000);       
    }
    private void getWaitFromNetWorkStep(){
        Log.i(TAG," requeset executeHttpGet beyond 3s then agin:");
        curStepIndex++;
        startLoopLoadDrawable();
    }
    private void startLoopLoadDrawable(){
        Log.i(TAG," requeset startLoopLoadDrawable executeHttpGet beyond 3s then agin:");
        mInitHandler.removeMessages(MSG_GETDRAWABLE);
        //mInitHandler.sendMessage(mInitHandler.obtainMessage(MSG_GETDRAWABLE));
        mInitHandler.sendMessageDelayed(mInitHandler.obtainMessage(MSG_GETDRAWABLE), 200);
        //getDrawableDataFromNetWorkStep();
/*
        if(getActivity() != null){
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    mAndroidAppsAdapter.addMoreContent(); 
                }
            });
        }
*/

    }
    private String getDownloadPath(){
        return LEJPConstant.getDownloadPath() + "/wallpapers";
    }
	

    private View view0;
    private View emptyView;
    private View view1;
    private LayoutInflater mLi ;
    private ProgressBar progressBarV;
    private TextView loading_textV;
    private Button loading_refresh;
    private FrameLayout banner;
    private int phoneWidth;

    private void initLoadView(){
        Log.e(TAG,"initLoadView");
         loadView = mLi.inflate(R.layout.init_loading,null);
         progressBarV = (ProgressBar) loadView.findViewById(R.id.progressing);
         loading_textV = (TextView) loadView.findViewById(R.id.loading_text);
         loading_refresh = (Button) loadView.findViewById(R.id.refresh_button);
         loading_refresh.setOnClickListener(new OnClickListener() {
             public void onClick(View v) {
                 if( "other".equals( getConnectType()))
                     return;
                 getSpicailDataFromNetwork();
                 getDataFromNetwork(1);
             }
         });
    }
    private void showNetworkClosedText(View view){
    	TextView text = (TextView) view.findViewById(R.id.network_close_text_wallpaper);
		CharSequence text_string = getText(R.string.network_close_body);
		text.setText(Html.fromHtml(text_string.toString()));
		text.setMovementMethod(LinkMovementMethod.getInstance());
		text.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        Log.e(TAG,"onCreateView");
        if(AllView == null){
        
			AllView = mLi.inflate(R.layout.android_wallpaper_viewpager, null);
			
            if(!Util.getInstance().isNetworkEnabled(getActivity())){
       		    LeAlertDialog dialog = new LeAlertDialog(getActivity(), R.style.Theme_LeLauncher_Dialog_Shortcut);
       		    dialog.setLeTitle(R.string.lejingpin_settings_title);
       		    dialog.setLeMessage(getActivity().getText(R.string.confirm_network_open));
       		    dialog.setLeNegativeButton(getActivity().getText(R.string.cancel_action), new DialogInterface.OnClickListener() {
       		        public void onClick(DialogInterface dialog, int which) {
       		        	dialog.dismiss();
       		            Toast.makeText(getActivity(), R.string.version_update_toast, Toast.LENGTH_SHORT).show();
       		            showNetworkClosedText(AllView);
       		        }
       		    });

       		    dialog.setLePositiveButton(getActivity().getText(R.string.goto_settings), new DialogInterface.OnClickListener() {
       		        public void onClick(DialogInterface dialog, int which) {
       		        	Intent intent = new Intent();
       		            intent.setClass(getActivity(), MoreSettings.class);
       		            startActivity(intent);
       		            getActivity().finish();
       		        }
       		    });
				dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						dialog.dismiss();
						Toast.makeText(getActivity(), R.string.version_update_toast, Toast.LENGTH_SHORT).show();
						showNetworkClosedText(AllView);
					}
				});
       		    dialog.show();
       			return AllView;
    	   }else{
            mInitHandler.removeMessages(MSG_DELAY_CREATEVIEW);
            mInitHandler.sendMessage(mInitHandler.obtainMessage(MSG_DELAY_CREATEVIEW, 100));
    	   }
        }
        return AllView; 
    }
    private void delayCreateView(){
        initLoadView();
        InitOverlay(); 

		view1 = mLi.inflate(R.layout.android_wallpaper_category, null);
        banner = (FrameLayout) view1.findViewById(R.id.banner_layout);
        mScrollView = (ScrollView) view1.findViewById(R.id.android_scroll);
        mScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        homeLayout = (LinearLayout)view1.findViewById(R.id.layout);

        mScrollView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    Log.e(TAG,"the position scrollview action up  ");
                    final int mLastY = mScrollView.getScrollY();//赋值给mLastY
                    if(homeLayout.getHeight()-mScrollView.getHeight() - mLastY == 0){
                        //TODO
                        if(mNoMoreFlag ){
                            showOverLay();
                            return false;
                        }
                        if( mStepGetFlag){ 
                         mStepGetFlag = false;

//                        mStartIndex = mStartIndex+mCount;
//                        Log.e("XXXX","mScrollView  OnTouchListener: mStartIndex = "+mStartIndex);
                        //getDrawableDataFromNetWorkStep();
                        getDataFromNetwork(1);
                        }
                    }else{
                    }
                }
                return false;
            }
        });
        
		view2 = mLi.inflate(R.layout.getmore_wallpaper_new, null);
		view2_land = mLi.inflate(R.layout.getmore_wallpaper_new, null);
	
        if(mOrientation == Configuration.ORIENTATION_LANDSCAPE){
        	mGridNew = (GridView) view2_land.findViewById(R.id.getmore_wallpaper_new);
            startNewEmptyView = (View) view2_land.findViewById(R.id.empty);
        }else{
        	mGridNew = (GridView) view2.findViewById(R.id.getmore_wallpaper_new);
            startNewEmptyView = (View) view2.findViewById(R.id.empty);
        }
        progressBarVNew = (ProgressBar) startNewEmptyView.findViewById(R.id.progressing);
        loading_textVNew = (TextView) startNewEmptyView.findViewById(R.id.loading_text);
        loading_refreshNew = (Button) startNewEmptyView.findViewById(R.id.refresh_button);
        loading_refreshNew.setOnClickListener(new OnClickListener() {
             public void onClick(View v) {
            	 Log.i("XXXX","======= getConnectType: "+getConnectType());
                 if( "other".equals( getConnectType()))
                     return;
                 getDataFromNetwork(PREVIEW_TYPE_NEW, 0);
             }
        });
        mGridNew.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
                    int maPosition = mGridNew.getFirstVisiblePosition();
                    int mbPosition = mGridNew.getLastVisiblePosition();
                    if(mNoMoreNewFlag){
                        if( mbPosition == mAppsNewAdapter.getCount() -1){
	                        showOverLay();
	                        return;
                        }
                    }
					if (mAppsNewAdapter != null) {
						mNewCount = mbPosition - maPosition + 1;
						mNewStartIndex = maPosition;
						while(null != mNewWallpaperHashmap && null != mNewWallpaperHashmap.get(mNewStartIndex)){
							mNewStartIndex++;
							if(mNewStartIndex > mbPosition)
								break;
						}
						if(mNewStartIndex <= mbPosition){
							getDataFromNetwork(PREVIEW_TYPE_NEW, mNewStartIndex);
						}
					}
                    
                }else if(scrollState == OnScrollListener.SCROLL_STATE_FLING || scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                	mCurrentNewLoadingPos = -1;
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                    int totalItemCount) {
            }
        });
        mGridNew.setOnTouchListener(new OnTouchListener() {
           @Override
           public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_UP){
                    ya = 0;
                    yb = 0;
                }
                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    if(ya == 0){
                        ya = (int)event.getY();
                    }
                    else if(ya != 0 && yb == 0){
                        yb = (int)event.getY();
                        if(yb - ya >= 0){
                            updownflag = false;
                        }else{
                            updownflag = true;
                        }
                    }
                }
                return false;
            }
        });

		view3 = mLi.inflate(R.layout.getmore_wallpaper_hot, null);
		view3_land = mLi.inflate(R.layout.getmore_wallpaper_hot, null);
        if(mOrientation == Configuration.ORIENTATION_LANDSCAPE){
            mGridHot = (GridView) view3_land.findViewById(R.id.getmore_wallpaper_hot);
            startHotEmptyView = (View) view3_land.findViewById(R.id.empty);
        }else{
            mGridHot = (GridView) view3.findViewById(R.id.getmore_wallpaper_hot);
            startHotEmptyView = (View) view3.findViewById(R.id.empty);
        }
        progressBarVHot = (ProgressBar) startHotEmptyView.findViewById(R.id.progressing);
        loading_textVHot = (TextView) startHotEmptyView.findViewById(R.id.loading_text);
        loading_refreshHot = (Button) startHotEmptyView.findViewById(R.id.refresh_button);
        loading_refreshHot.setOnClickListener(new OnClickListener() {
             public void onClick(View v) {
            	 Log.i("XXXX","======= getConnectType: "+getConnectType());
                 if( "other".equals( getConnectType()))
                     return;
                 getDataFromNetwork(PREVIEW_TYPE_HOT, 0);
             }
         });
        mGridHot.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            	if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
                    int maPosition = mGridHot.getFirstVisiblePosition();
                    int mbPosition = mGridHot.getLastVisiblePosition();
                    if(mNoMoreHotFlag){
                        if( mbPosition == mAppsHotAdapter.getCount() -1){
	                        showOverLay();
	                        return;
                        }
                    }
					if (mAppsHotAdapter != null) {
						mHotCount = mbPosition - maPosition + 1;
						mHotStartIndex = maPosition;
						while(null != mHotWallpaperHashmap && null != mHotWallpaperHashmap.get(mHotStartIndex)){
							mHotStartIndex++;
							if(mHotStartIndex > mbPosition)
								break;
						}
						if(mHotStartIndex <= mbPosition){
							getDataFromNetwork(PREVIEW_TYPE_HOT, mHotStartIndex);
						}
					}
                    
                }else if(scrollState == OnScrollListener.SCROLL_STATE_FLING || scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                	mCurrentHotLoadingPos = -1;
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                    int totalItemCount) {
                //    Log.e(TAG,"mGridHot onScroll get scrollx ==============="+firstVisibleItem);
            }
        });
          mGridHot.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_UP){
                    ya = 0;
                    yb = 0;
                }
                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    //Log.e(TAG,"onTouch DOWN getY 000000000000=========yb=="+event.getY());
                    if(ya == 0){
                        ya = (int)event.getY();
                    }
                    else if(ya != 0 && yb == 0){
                        yb = (int)event.getY();
                         //Log.e(TAG,"onTouch DOWN getY aaaaaaaaaaaa==========yb="+yb+" ya======="+ya);
                        if(yb - ya >= 0){
                            //Log.e(TAG,"onTouch DOWN getY 111111111111===========");
                            updownflag = false;
                        }else{
                            //Log.e(TAG,"onTouch DOWN getY 222222222222===========");
                            updownflag = true;
                        }
                    }
                }
                return false;
            }
        }); 

          
        try{
	        initDataView();  
	        InitTopBarView(); 
	        InitViewPager(); 
	        DisplayMetrics metrics = new DisplayMetrics();
			getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
			phoneWidth = metrics.widthPixels;
			Log.e("yumina0330", "phonewidth ===================" + phoneWidth);
	        mScrollView.requestChildFocus(viewPager,null);
        }catch(Exception e){
        	Log.i(TAG,"delayCreateView Exception: "+e.getMessage());
        }
    	
    }

 /*   Runnable runStartInitService = new Runnable(){
        public void run()
        {
            getSpicailDataFromNetwork();
            getDataFromNetwork(1);
        }
    };*/
    Runnable runStartInitService = new Runnable(){
        public void run()
        {
        	Log.e("XXXX","========= runStartInitService: mCurrentPageItem = "+mCurrentPageItem);
        	if(mCurrentPageItem == 1){
        		curStepIndex = 0;
	            getSpicailDataFromNetwork();
	            getDataFromNetwork(1);
        	}else if(mCurrentPageItem == 0){
        		getDataFromNetwork(PREVIEW_TYPE_NEW, mNewStartIndex);
        	}else if(mCurrentPageItem == 2){
        		getDataFromNetwork(PREVIEW_TYPE_HOT, mHotStartIndex);
        	}
        }
    };
    
    private void InitTopBarView(int currentItem) {
        if(currentItem == 1){
	        t2.setChecked(true);
	        t1.setCompoundDrawablesWithIntrinsicBounds(null,null,null,nobottom);
	        t2.setCompoundDrawablesWithIntrinsicBounds(null,null,null,bottom);
	        t3.setCompoundDrawablesWithIntrinsicBounds(null,null,null,nobottom);
        }else if(currentItem == 0){
        	t1.setChecked(true);
        	t1.setCompoundDrawablesWithIntrinsicBounds(null,null,null,bottom);
	        t2.setCompoundDrawablesWithIntrinsicBounds(null,null,null,nobottom);
	        t3.setCompoundDrawablesWithIntrinsicBounds(null,null,null,nobottom);
        }else if(currentItem == 2){
        	t3.setChecked(true);
	        t1.setCompoundDrawablesWithIntrinsicBounds(null,null,null,nobottom);
	        t2.setCompoundDrawablesWithIntrinsicBounds(null,null,null,nobottom);
	        t3.setCompoundDrawablesWithIntrinsicBounds(null,null,null,bottom);
        }
    }
    
    private void InitViewPager() {
    	if(mPager != null)
    		mPager = null;
        mPager = (ViewPager) AllView.findViewById(R.id.vPager);
        mPager.setOffscreenPageLimit(3);
        listViews = new ArrayList();
        mpAdapter = new MyPagerAdapter();

        listViews.add(view2);
        listViews.add(view2_land);
        listViews.add(loadView);
        listViews.add(view3);
        listViews.add(view3_land);
        mPager.setAdapter(mpAdapter);
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }
    
    private void setCurrentPage(int currentItem){
    	Log.i("XXXX","======= setCurrentPage: currentItem = "+currentItem);
    	mPager.setCurrentItem(currentItem);
    	InitTopBarView(currentItem);
    }
    
     /**
      * 初始化头标
    */
    private Drawable bottom;
    private Drawable nobottom;
    private RadioButton t1,t2,t3,t4;
    private RadioGroup mToolBar;



    private void InitTopBarView() {
        bottom = getActivity().getResources().getDrawable(R.drawable.tab_selected_holo_line_short);
        nobottom = getActivity().getResources().getDrawable(R.drawable.tab_selected_holo_line_short_no);

        mToolBar = (RadioGroup) AllView.findViewById(R.id.toolBar);

        t1 = (RadioButton) mToolBar.findViewById(R.id.text1);
        t2 = (RadioButton) mToolBar.findViewById(R.id.text2);
        t3 = (RadioButton) mToolBar.findViewById(R.id.text3);
        t1.setText(R.string.theme_store_tab_latest);
        t2.setText(R.string.theme_store_tab_category);
        t3.setText(R.string.theme_store_tab_hot);
        t3.setVisibility(View.VISIBLE);

//        t2.setChecked(true);
//        t1.setCompoundDrawablesWithIntrinsicBounds(null,null,null,nobottom);
//        t2.setCompoundDrawablesWithIntrinsicBounds(null,null,null,bottom);
//        t3.setCompoundDrawablesWithIntrinsicBounds(null,null,null,nobottom);
        mToolBar.setOnCheckedChangeListener(mSwitchButtonListener);
        LinearLayout top_bar = (LinearLayout)AllView.findViewById(R.id.linearLayout0);
        top_bar.setVisibility(View.VISIBLE);
    }
    
    
    /**
     * 头标点击监听
    */
    private RadioGroup.OnCheckedChangeListener mSwitchButtonListener = new RadioGroup.OnCheckedChangeListener() {
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.text1:
                	Log.e("XXXX","mSwitchButtonListener : item == 0");
                	stopGetPreview();
                    mPager.setCurrentItem(0);
                    t1.setCompoundDrawablesWithIntrinsicBounds(null,null,null,bottom);
                    t2.setCompoundDrawablesWithIntrinsicBounds(null,null,null,nobottom);
                    t3.setCompoundDrawablesWithIntrinsicBounds(null,null,null,nobottom);
                    break;
                case R.id.text2:
                	Log.e("XXXX","mSwitchButtonListener : item == 1");
                	Log.e("XXXX","mLastCategoryOrientation = "+mLastCategoryOrientation + 
                			", mOrientation = "+mOrientation);
                    mPager.setCurrentItem(1);
                    if (mLastCategoryOrientation != mOrientation) {
	                    delayCreateView();
						setCurrentPage(mCurrentPageItem);
                    }
                    if(mSpecialDataList == null || mSpecialDataList.size() == 0 
                			|| mDataList == null || mDataList.size() == 0){
                		mInitHandler.post(runStartInitService);
                	}else{
                		Log.e("XXXX","mSwitchButtonListener : reset the curStepIndex!!!!!!");
                		if(mDataList != null){
                    		mStartIndex = mDataList.size();
                    		Log.e("XXXX","mStartIndex = "+mStartIndex);
                    	}else{
                    		mStartIndex = 0;
                    		Log.e("XXXX","mStartIndex is 0");
                    	}
                		mInitHandler.sendEmptyMessage(MSG_GETSPECIAL);
                		if(homeLayout.getChildCount() > 1){
//                			curStepIndex = (mDataList != null) ? mDataList.size() : 0;
                			homeLayout.setVisibility(View.VISIBLE);
                		}else{
                			curStepIndex = 0;
                			mInitHandler.sendEmptyMessage(MSG_GETLIST);
                		}
                	}
                    t2.setCompoundDrawablesWithIntrinsicBounds(null,null,null,bottom);
                    t1.setCompoundDrawablesWithIntrinsicBounds(null,null,null,nobottom);
                    t3.setCompoundDrawablesWithIntrinsicBounds(null,null,null,nobottom);
                    break;
            case R.id.text3:
            		Log.e("XXXX","mSwitchButtonListener : item == 2");
            		stopGetPreview();
                    mPager.setCurrentItem(2);
                    t3.setCompoundDrawablesWithIntrinsicBounds(null,null,null,bottom);
                    t1.setCompoundDrawablesWithIntrinsicBounds(null,null,null,nobottom);
                    t2.setCompoundDrawablesWithIntrinsicBounds(null,null,null,nobottom);
                    break;
            default:
                   	break;
            }

        }
    };

    private void replaceView(){
    	Log.e("XXXX","========== replaceView======");
        mpAdapter.destroyItem(mPager, 1,null);
        listViews.clear();
        listViews.add(view2);
        listViews.add(view2_land);
        listViews.add(view1);
        listViews.add(view3);
        listViews.add(view3_land);
        mpAdapter.instantiateItem(mPager, 1);
        mPager.setAdapter(mpAdapter);
        int id = mToolBar.getCheckedRadioButtonId();
        switch(id){
            case R.id.text1:
                    mPager.setCurrentItem(0);
                    break;
                case R.id.text2:
                    mPager.setCurrentItem(1);
                    break;
            case R.id.text3:
                    mPager.setCurrentItem(2);
                 break;
            default:
                 break;

         }
        Log.e(TAG,"replaceview listviewsize= "+listViews.size());
    }
    
    final int pageNum = 3; 
    public class MyPagerAdapter extends PagerAdapter {
        public MyPagerAdapter() {
        }
        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
//        	Log.i("XXXX","====MyPagerAdapter destroy item: arig1 = "+arg1);
        	int index = -1;   //index of the view in listviews
        	if(arg1 == 1){
        		index = 2;
        	}else if(arg1 == 0){
        		if(mOrientation == Configuration.ORIENTATION_LANDSCAPE){
           		 	index = 1;
           	 	}else{
           	 		index = 0;
           	 	}
        	}else if(arg1 == 2){
        		if(mOrientation == Configuration.ORIENTATION_LANDSCAPE){
           		 	index = 4;
           	 	}else{
           	 		index = 3;
           	 	}
        	}
        	 
        	if(listViews.get(index) instanceof View){
//        		Log.i("XXXX","====MyPagerAdapter destroy item: index = "+index);
        		((ViewPager) arg0).removeView(((View)listViews.get(index)));
        	}
        }
        @Override
        public void finishUpdate(View arg0) {
        }
        @Override
        public int getCount() {
        	return pageNum;
        }
        @Override
        public Object instantiateItem(View arg0, int arg1) {
//        	Log.i("XXXX","====MyPagerAdapter instantiateItem: arig1 = "+arg1);
        	int index = -1;   //index of the view in listviews
            try {
            	if(arg1 == 1){
            		index = 2;
            	}else if(arg1 == 0){
            		if(mOrientation == Configuration.ORIENTATION_LANDSCAPE){
               		 	index = 1;
               	 	}else{
               	 		index = 0;
               	 	}
            	}else if(arg1 == 2){
            		if(mOrientation == Configuration.ORIENTATION_LANDSCAPE){
               		 	index = 4;
               	 	}else{
               	 		index = 3;
               	 	}
            	}
            	if(listViews.get(index) instanceof View){
//            		Log.i("XXXX","====MyPagerAdapter instantiateItem: index = "+index);
            		((ViewPager) arg0).addView(((View)listViews.get(index)), 0);
            	}
            } catch (Exception e) {


            }
            return  listViews.get(index);
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

    private LinearLayout homeLayout;
    private View mConvertViewItem;
    private View mconvertViewItem_land;
    private ScrollView mScrollView;
    private AppGallery viewPager;
    private List<ImageView> imageViews; // 滑动的图片集合  
 
    private String[] titles; // 图片标题  
    private int[] imageResId; // 图片ID  
    private List<ImageView> dots; // 图片标题正文的那些点  
 
    private TextView tv_title;  
    private int currentItem = 0; // 当前图片的索引号  
    private boolean mTouch = false;
    private int oldPosition = 0;  
 
    // An ExecutorService that can schedule commands to run after a given delay,  
    // or to execute periodically.  
    private ScheduledExecutorService scheduledExecutorService;  

 
    // 切换当前显示的图片  
    private Handler flinghandler = new Handler() {  
        public void handleMessage(android.os.Message msg) {  
                //viewPager.setSelection(currentItem);// 切换当前显示的图片  
    if(getActivity() != null){
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                try{
                Log.e(TAG,"onkey down gallery begin");
                if(onDestroyFlag) return; 
                if( viewPager !=null){
                    viewPager.setSoundEffectsEnabled(false);
                    viewPager.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
                }
                }catch(Exception e){
                    Log.e(TAG,"onkey down error gallery");
                }
            }
            });
        }

        };  
    };  


    public void initDataView() {  
        dots = new ArrayList<ImageView>();  
        dots.add((ImageView) view1.findViewById(R.id.v_dot0));  
        dots.add((ImageView) view1.findViewById(R.id.v_dot1));  
        dots.add((ImageView) view1.findViewById(R.id.v_dot2));  
        dots.add((ImageView) view1.findViewById(R.id.v_dot3));  
        dots.add((ImageView) view1.findViewById(R.id.v_dot4));  
 
        tv_title = (TextView) view1.findViewById(R.id.tv_title);  
 
        viewPager = (AppGallery) view1.findViewById(R.id.gallery_app);  
        // 设置一个监听器，当ViewPager中的页面改变时调用  
        viewPager.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> paramAdapterView,View paramView, int paramInt, long paramLong) {
                int size = mSpecialDataList.size();
                int position = paramInt % size;
                ApplicationData mdata = mSpecialDataList.get(position);
                Log.e("yumina","the position is <F2><F2><F2><F2> ="+position+""+mdata.getAppId());
                
                    //startWallpaperTypeActivity(mdata.name,String.valueOf(mdata.getAppId()));
                startWallpaperSpecialActivity(mdata.name,mdata.getPackage_name(),mdata.previewAddr);
            }
        });
        viewPager.setOnTouchListener(new OnTouchListener(){
            @Override
            public boolean onTouch(View arg0, MotionEvent event) {
                int action = event.getAction();
                if(action == MotionEvent.ACTION_DOWN){
                    mTouch = true;
                    Log.e(TAG,"the position is <F2><F2><F2><F2> DOWNNNNNNNNNNNn=");
                    delayLeakStartAuto();
                }else if(action == MotionEvent.ACTION_UP){
                    Log.e(TAG,"the position is <F2><F2><F2><F2> uppppppppppp=");
                    delayScheduledExecutorService();
                    
                }
                return false;
            }
        });


        viewPager.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                //int size = imageResId.length;
                int size = mSpecialDataList.size();
                int position = arg2 % size;
                currentItem = position;  
                tv_title.setText(mSpecialDataList.get(position).name);  
                dots.get(oldPosition).setImageResource(R.drawable.dot_default);  
                dots.get(position).setImageResource(R.drawable.dot_press);  
                oldPosition = position;  
                Log.e("yumina","the position is <F2><F2><F2><F2> ="+position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }

        });

    }  
    private void showSpecialView(){
        if(specialAdapter == null){
            specialAdapter = new MyAdapter();
            banner.setVisibility(View.VISIBLE);
        }
        viewPager.setAdapter(specialAdapter);// 设置填充ViewPager页面的适配器  
        if (!SettingsValue.isCurrentPortraitOrientation(getActivity())) {
        viewPager.setSelection(1);
        }

    }
    private boolean overlayflag = false;
    private void InitOverlay() {
        try{
        mOverlay = (TextView) mLi.inflate(R.layout.overlay_getmore_text, null);
        mOverlay.setVisibility(View.INVISIBLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
            LayoutParams.FILL_PARENT,
            LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT);
        lp.gravity = Gravity.BOTTOM | Gravity.CENTER;
        mWM.addView(mOverlay, lp);
        overlayflag = true;
        }catch(Exception e){
        }
    }
    private void showOverLay(){
        mOverlay.setVisibility(View.VISIBLE);
        mInitHandler.removeMessages(MSG_NO_MORE);
        mInitHandler.sendMessageDelayed(mInitHandler.obtainMessage(MSG_NO_MORE), 1500);
    }
    private void hideOverLay(){
        mOverlay.setVisibility(View.GONE);
    }

    private void delayLeakStartAuto(){
        Log.e(TAG,"the position Leak Auto <F2><F2><F2><F2> uppppppppppp=");
        mInitHandler.removeMessages(MSG_GET_LEAK);
        mInitHandler.sendMessageDelayed(mInitHandler.obtainMessage(MSG_GET_LEAK), 2000);
    }
    private void delayScheduledExecutorService(){
        mInitHandler.removeMessages(MSG_GET_LEAK);
        mInitHandler.removeMessages(MSG_GET_AUTO);
        mInitHandler.sendMessageDelayed(mInitHandler.obtainMessage(MSG_GET_AUTO), 2000);
/*
        mInitHandler.postDelayed(new Runnable() {
            public void run() {
                Log.e(TAG,"posted delay 2000 then auto fling  run<F2><2><F2>");
                mTouch = false;
            }
        },2000);
*/
    }

    private void stopGetPreview(){
    	if(mPager == null) return;
    	int item = mPager.getCurrentItem();
//    	Log.i("XXXX","========= stopGetPreview: cur item = "+item);
    	if(item == 0){ //new
    		mInitHandler.removeMessages(MSG_GET_NEW_PREVIEW);
    	}else if(item == 2){//hot
    		mInitHandler.removeMessages(MSG_GET_HOT_PREVIEW);
    	}else if(item == 1){
    		mInitHandler.removeMessages(MSG_GETDRAWABLE);
    	}
    }
    @Override
    public void onPause() {
    	Log.d("XXX","==== onPause");
    	mStopGetPreviewFlag = true;
    	stopGetPreview();
        if(mGalleryHandler != null )
        mGalleryHandler.removeCallbacks(runnable); 
        Log.e(TAG,"on Pause then auto fling  run<F2><2><F2>");
        super.onPause();
    }
    @Override
    public void onResume() {
        //scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        // 当Activity显示出来后，每两秒钟切换一次图片显示  
       // scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, flingtime, TimeUnit.SECONDS);
        //flingtime = 2;
        super.onResume();
        if(this.getResources().getConfiguration().orientation != mOrientation){
    		mOrientation = this.getResources().getConfiguration().orientation;
            if(mPager == null) return;
            mCurrentPageItem = mPager.getCurrentItem();
            if(mpAdapter != null){
            	delayCreateView();
                setCurrentPage(mCurrentPageItem);
                mInitHandler.post(runStartInitService);
            }
    	}else{
    		if(mPager == null) return;
    		int item = mPager.getCurrentItem();
        	if(item == 0){ //new
        		mInitHandler.sendEmptyMessage(MSG_GET_NEW_PREVIEW);
        	}else if(item == 2){//hot
        		mInitHandler.sendEmptyMessage(MSG_GET_HOT_PREVIEW);
        	}else if(item == 1){
            	mInitHandler.sendEmptyMessage(MSG_GETDRAWABLE);
        	}
    	}
    		
        if(mGalleryHandler != null )
        mGalleryHandler.postDelayed(runnable, 5000);//每两秒执行一次runnable.
        Log.e(TAG,"on Resume then auto fling  run<F2><2><F2>");
        // 当Activity不可见的时候停止切换  
        mStopGetPreviewFlag = false;
        Log.d("XXX","==== onPause");
    }
	
    Handler mGalleryHandler=new Handler();
    Runnable runnable=new Runnable() {
    @Override
    public void run() {
        // TODO Auto-generated method stub
        //要做的事情
        flingGallery();
        mGalleryHandler.postDelayed(this, 5000);
    }
    };

	
	private void flingGallery_Old() {
		if (null != viewPager) {
			synchronized (viewPager) {
				if (!mTouch) {
					currentItem = (currentItem + 1) % specialNum;
					viewPager.setSoundEffectsEnabled(false);
					viewPager.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
				}
			}
		}
	}

    private void flingGallery() {
        if(getActivity() != null){
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    try{
                        Log.e(TAG,"onkey down gallery begin");
                        if( viewPager !=null){
			    currentItem = (currentItem + 1) % specialNum;
                            viewPager.setSoundEffectsEnabled(false);
                            if(onDestroyFlag) return;
                            viewPager.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
                        }
                    }catch(Exception e){
                        Log.e(TAG,"onkey down error gallery");
                    }
                }
            });
        }
    }

	
/*
    @Override 
    public void onStart() {  
        Log.e(TAG,"on Start then auto fling  run<F2><2><F2>");
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();  
        // 当Activity显示出来后，每两秒钟切换一次图片显示  
        scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, flingtime, TimeUnit.SECONDS);  
        flingtime = 5;
        super.onStart();  
    }  
 
    @Override 
    public void onStop() {  
        // 当Activity不可见的时候停止切换  
        scheduledExecutorService.shutdown();  
        super.onStop();  
    }  
*/
     /**  
     * 换行切换任务  
     *   
     * @author Administrator  
     *   
     */ 
    private class ScrollTask implements Runnable {  
 
        public void run() {  
            synchronized (viewPager) {  
                if(!mTouch){
                    currentItem = (currentItem + 1) % specialNum;  
//                    flinghandler.obtainMessage().sendToTarget(); // 通过Handler切换图片  
                    flinghandler.sendMessageDelayed(flinghandler.obtainMessage(),2000);
                    Log.e("yumina","the sendToTarget setImageview position is <F2><F2><F2><F2> ="+currentItem);
                }else{
                    Log.e("yumina"," shutdown the schedultExecut the position is <F2><F2><F2><F2> ="+currentItem);
                    //scheduledExecutorService.shutdown();  
                }
            }  
        }  
    }  

    /**  
     * 填充ViewPager页面的适配器  
     *   
     *   
    */ 
    public class MyAdapter extends BaseAdapter {
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Holder holder = (Holder)msg.obj;
                holder.mimg.setImageDrawable(holder.mdb);
            }
        };


        public MyAdapter() {
        }

        public int getCount() {
            return Integer.MAX_VALUE;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = null;
            if (convertView == null) {
                if(getActivity() == null)return null;
                convertView = new ImageView(getActivity());

                imageView = (ImageView) convertView;
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setLayoutParams(new Gallery.LayoutParams(phoneWidth,200*phoneWidth/480));
                if(getSpecialValue()){ 
                    imageView.setLayoutParams(new Gallery.LayoutParams(phoneWidth/2,100*phoneWidth/480));
                }
//                imageView.setLayoutParams(new Gallery.LayoutParams(300,150));
                //imageView.setLayoutParams(new Gallery.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            } else {
            	if(convertView instanceof ImageView){
            		imageView = (ImageView) convertView;
            	}
            }
            final int pos = position%specialNum;
            final ApplicationData mdata = mSpecialDataList.get(pos);
//            final String childiconUrl = mdata.getPreviewAddr();
            final String pkgName = mdata.getPackage_name();
            final ImageView image = imageView;
            Log.e(TAG,"positon ======="+position+" pkgName =========="+pkgName);

            boolean flag = false;
            //tv_title.setText(mdata.name);
            //imageView.setImageResource(imageResId[position%imageResId.length]);
            if(mIconList.containsKey(pkgName)){
                Drawable tmppic = mIconList.get(pkgName).get();
                if(tmppic != null){
                    image.setImageDrawable(mIconList.get(pkgName).get());
                }else{
                    flag = true;
                }
            }
            if (flag || !mIconList.containsKey(pkgName)){
                asyncImageLoader.loadDrawable(image,mdata.previewAddr,0,pos,new ImageCallback() {
                    public void imageLoaded(final View aimage,final Drawable imageDrawable, int postion,int j) {
                        mIconList.put(pkgName, new SoftReference<Drawable>(imageDrawable));
                        Message message = handler.obtainMessage(0, new Holder((ImageView)image, imageDrawable));
                        handler.sendMessage(message);
                    }
                });
            }
            return image;
        }
    }
     public class AppsNewAdapter extends BaseAdapter {
        public AppsNewAdapter() {
        }
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Holder holder = (Holder)msg.obj;
                holder.mimg.setImageDrawable(holder.mdb);
            }
        };
        public View getView(int position, View convertView, ViewGroup parent) {
        	if (convertView == null) {
                if(phoneWidth <= 480 ){
                convertView = mLi.inflate(R.layout.android_wallpaper_480gridviewitem, parent, false);
              }else{
            	convertView = mLi.inflate(R.layout.android_wallpaper_gridviewitem, parent, false);
					if (SettingsValue.getCurrentMachineType(getActivity()) ==0) {
						int screenWidth = getActivity().getWindowManager()
								.getDefaultDisplay().getWidth();
						int scale = 328 / 394;//图片宽高比
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
          final ImageView image = (ImageView) convertView.findViewById(R.id.textpic);
          if(mNewDataList == null || position >= mNewDataList.size()){
          	image.setImageResource(R.drawable.le_wallpaper_magicdownload_push_app_icon_def);
          }
          if(mNewWallpaperHashmap != null){
	          final ApplicationData mdata = mNewWallpaperHashmap.get(position);
	          if(mdata != null){
		            final String pkgName = mdata.getPackage_name();
		            boolean flag = false;
		            if(mNewIconList.containsKey(pkgName)){
		                Drawable tmppic = mNewIconList.get(pkgName).get();
		                if(tmppic != null){
		                    image.setImageDrawable(mNewIconList.get(pkgName).get());
		                }else{
		                    flag = true;
		                    mIconUrlList.remove(pkgName);
		                    mNewIconList.remove(pkgName);
		                }
		            }
		            if(!mNewIconList.containsKey(pkgName) || flag){
		                if(mIconUrlList.contains(pkgName)){
		                }else{
			                mIconUrlList.add(pkgName);
			                if(updownflag){
			                	image.setImageResource(R.drawable.le_wallpaper_magicdownload_push_app_icon_def);
			                }
			                getPreviewImgFromNetWork(mdata,null, PREVIEW_TYPE_NEW);
		                }
		            }
	          }
            convertView.setOnClickListener(new OnClickListener() {
                 @Override
                 public void onClick(View v) {
					if (mNewDataList == null) return;
					int p = findDataInList(mdata, mNewDataList);
					ArrayList<ApplicationData> newDataList;
					if (p > mNewCount) {
						newDataList = new ArrayList<ApplicationData>(
								mNewDataList.subList(p - mNewCount, p + 1));
						p = newDataList.indexOf(mdata);
					} else {
						newDataList = mNewDataList;
					}
					mInitHandler.removeMessages(MSG_GET_NEW_PREVIEW);
					if (p > -1) {
						startWallpaperDetailActivity(0, p, newDataList);
					} else {
						Toast.makeText(getActivity(),
								R.string.wallpaper_open_detail_error,
								Toast.LENGTH_SHORT).show();
						updownflag = true;
					}
				}
             });
          }
            return convertView;
        }
        public void addMoreContent() {
            notifyDataSetChanged();
        }
        public final int getCount() {
        	return mTotalCount;
        }
        public final Object getItem(int position) {
            return mNewDataList.get(position);
        }
        public final long getItemId(int position) {
            return position;
        }
    }
        class ViewHolder{
            private ImageView image ;
        };
        
	private int findDataInList(ApplicationData data,
			ArrayList<ApplicationData> list) {
		if (data == null || list == null)
			return -1;
		for (ApplicationData adata : list) {
			if (adata.getPackage_name().equals(data.getPackage_name())) {
				return list.indexOf(adata);
			}
		}
		return -1;
	}

    public class AppsHotAdapter extends BaseAdapter {
        public AppsHotAdapter() {
        }
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Holder holder = (Holder)msg.obj;
                holder.mimg.setImageDrawable(holder.mdb);
            }
        };
        public View getView(int position, View convertView, ViewGroup parent) {
        /*    ViewHolder holder;
            if (convertView == null) {
                  if(phoneWidth <= 480 ){
                convertView = mLi.inflate(R.layout.android_wallpaper_480gridviewitem, parent, false);
                }else{
                convertView = mLi.inflate(R.layout.android_wallpaper_gridviewitem, parent, false);
                }

                holder = new ViewHolder();
                holder.image = (ImageView) convertView.findViewById(R.id.textpic);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            final ImageView image = holder.image;

            final ApplicationData mdata = mHotDataList.get(position);
            Log.e("0322","posiint ================sieze==name====="+position+" updownflag===="+updownflag);
            final String pkgName = mdata.getPackage_name();
            final int pos = position;
            boolean flag = false;
            if( mHotIconList.containsKey(pkgName)){
                Drawable tmppic = mHotIconList.get(pkgName).get();
                if(tmppic != null){
                    image.setImageDrawable(mHotIconList.get(pkgName).get());
                }else{
                    flag = true;
                    mHotIconUrlList.remove(pkgName);
                }
            }
            if(flag || !mHotIconList.containsKey(pkgName)){
                if(mHotIconUrlList.contains(pkgName)){
                }else{
                mHotIconUrlList.add(pkgName);
                if(updownflag){
                image.setImageResource(R.drawable.le_wallpaper_magicdownload_push_app_icon_def);
                }
                if(!TextUtils.isEmpty(mdata.previewAddr)) {
                    asyncImageLoader.loadDrawable(image,mdata.previewAddr,0,0,new ImageCallback() {
                        public void imageLoaded(View imageview,Drawable imageDrawable, int position,int j) {
                            Message message = handler.obtainMessage(0, new Holder((ImageView)imageview,imageDrawable));
                            handler.sendMessage(message);
                            mHotIconList.put(pkgName, new SoftReference<Drawable>(imageDrawable));
                        }
                    });
                }else{
                    image.setImageResource(R.drawable.le_wallpaper_magicdownload_push_app_icon_def);
                }
            }*/
	        	ViewHolder holder;
	            if (convertView == null) {
					if (phoneWidth <= 480) {
						convertView = mLi.inflate(R.layout.android_wallpaper_480gridviewitem, parent, false);
					} else {
						convertView = mLi.inflate(R.layout.android_wallpaper_gridviewitem, parent, false);
					if (SettingsValue.getCurrentMachineType(getActivity()) ==0) {
						int screenWidth = getActivity().getWindowManager()
								.getDefaultDisplay().getWidth();
						int scale = 328 / 394;//图片宽高比
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

					holder = new ViewHolder();
					holder.image = (ImageView) convertView
							.findViewById(R.id.textpic);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				final ImageView image = holder.image;

				if (mHotDataList == null || position >= mHotDataList.size()) {
					image.setImageResource(R.drawable.le_wallpaper_magicdownload_push_app_icon_def);
				}
				
				if(mHotWallpaperHashmap != null){
					final ApplicationData mdata = mHotWallpaperHashmap.get(position);
					// final int pos = position;
					if (mdata != null) {
						final String pkgName = mdata.getPackage_name();
						boolean flag = false;
						if (mHotIconList.containsKey(pkgName)) {
							Drawable tmppic = mHotIconList.get(pkgName).get();
							if (tmppic != null) {
								image.setImageDrawable(mHotIconList.get(pkgName).get());
							} else {
								flag = true;
								mIconUrlList.remove(pkgName);
								mHotIconList.remove(pkgName);
							}
						}
						if (!mHotIconList.containsKey(pkgName) || flag) {
							if (mIconUrlList.contains(pkgName)) {
							} else {
								mIconUrlList.add(pkgName);
								if (updownflag) {
									image.setImageResource(R.drawable.le_wallpaper_magicdownload_push_app_icon_def);
								}
								getPreviewImgFromNetWork(mdata, null, PREVIEW_TYPE_HOT);
							}
						}
					}
		        convertView.setOnClickListener(new OnClickListener() {
	                @Override
	                public void onClick(View v) {
	                	if(mHotDataList == null) return;
						int p = findDataInList(mdata, mHotDataList);
						ArrayList<ApplicationData> hotDataList;
						if (p > mHotCount) {
							hotDataList = new ArrayList<ApplicationData>(mHotDataList.subList(p - mHotCount, p + 1));
							p = hotDataList.indexOf(mdata);
						} else {
							hotDataList = mHotDataList;
						}
						mInitHandler.removeMessages(MSG_GET_HOT_PREVIEW);
						if (p > -1) {
							startWallpaperDetailActivity(0, p, hotDataList);
						} else {
							Toast.makeText(getActivity(), R.string.wallpaper_open_detail_error, Toast.LENGTH_SHORT).show();
							updownflag = true;
						}
	                }
	            });
			}
            return convertView;
        }
        public void addMoreContent() {
            notifyDataSetChanged();
        }
        public final int getCount() {
        	return mTotalCount;
        }
        public final Object getItem(int position) {
            return mHotDataList.get(position);
        }
        public final long getItemId(int position) {
            return position;
        }
    }
    /**  
     * 当ViewPager中页面的状态发生改变时调用  
     *   
     * @author Administrator  
     *   
     */ 
     public class MyOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
        	Log.e("XXXX","======== onPageSelected: "+arg0);
            Animation animation = null;
            mCurrentPageItem = arg0;
            if(arg0 != 1){
	            if (arg0 == 0) {
					if (mAppsNewAdapter == null) {
						Log.e("XXXX","mAppsNewAdapter is null , mCurrentNewLoadingPos = "+mCurrentNewLoadingPos);
						mAppsNewAdapter = new AppsNewAdapter();
						if (mCurrentNewLoadingPos < 0) {
							getDataFromNetwork(arg0, 0);
						} else {
							getDataFromNetwork(arg0, mCurrentNewLoadingPos);
						}
					} else {
						Log.e("XXXX","mLastNewOrientation = "+mLastNewOrientation + 
	                			", mOrientation = "+mOrientation);
						if (mLastNewOrientation != mOrientation) {
							delayCreateView();
							reloadCurrentPageData(mCurrentPageItem);
							mLastNewOrientation = mOrientation;
						}else{
							startNewEmptyView.setVisibility(View.INVISIBLE);
							mGridNew.setVisibility(View.VISIBLE);
							mGridNew.setAdapter(mAppsNewAdapter);
							mAppsNewAdapter.addMoreContent();
						}
					}
				} else if (arg0 == 2) {
					if (mAppsHotAdapter == null) {
						Log.e("XXXX","mAppsHotAdapter is null , mCurrentHotLoadingPos = "+mCurrentHotLoadingPos);
						mAppsHotAdapter = new AppsHotAdapter();
						if (mCurrentHotLoadingPos < 0) {
							getDataFromNetwork(arg0, 0);
						} else {
							getDataFromNetwork(arg0, mCurrentHotLoadingPos);
						}
					} else {
						Log.e("XXXX","mLastHotOrientation = "+mLastHotOrientation + 
	                			", mOrientation = "+mOrientation);
						if (mLastHotOrientation != mOrientation) {
							delayCreateView();
							reloadCurrentPageData(mCurrentPageItem);
							mLastHotOrientation = mOrientation;
						}else{
							startHotEmptyView.setVisibility(View.INVISIBLE);
							mGridHot.setVisibility(View.VISIBLE);
							mGridHot.setAdapter(mAppsHotAdapter);
							mAppsHotAdapter.addMoreContent();
						}
					}
				}
	            mInitHandler.removeMessages(MSG_GETDRAWABLE);
            }else{
            	mInitHandler.removeMessages(MSG_GET_NEW_PREVIEW);
            	mInitHandler.removeMessages(MSG_GET_HOT_PREVIEW);
            	mInitHandler.sendMessageDelayed(mInitHandler.obtainMessage(MSG_GETDRAWABLE), 200);
            }
            switch (arg0) {
            case 0:
                t1.setChecked(true);
                break;
            case 1:
                t2.setChecked(true);
                break;
            case 2:
                t3.setChecked(true);
                break;
            default:
               	break;
            }
        }
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }
    private boolean onDestroyFlag = false;
    @Override
    public void onDestroy() {
        onDestroyFlag = true;
        if(mGalleryHandler != null )
        mGalleryHandler.removeCallbacks(runnable); 
        //if (scheduledExecutorService != null){
        //            flinghandler.removeMessages(flinghandler.obtainMessage());
        //scheduledExecutorService.shutdownNow();
        Log.e(TAG,"on Destroy F2><2><F2>");
        //}
        super.onDestroy();
        try{
        if(mWM != null){
        if(overlayflag) 
        mWM.removeView(mOverlay);
        }
        }catch(Exception e){
            Log.e(TAG,"onDestroy error for mWM overlay");
        }
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
