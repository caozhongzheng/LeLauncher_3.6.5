
package com.lenovo.lejingpin;

import com.lenovo.launcher.R;

 
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import android.app.LocalActivityManager;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.view.PagerTitleStrip;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
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
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.widget.AbsListView;
import android.view.WindowManager;
import android.graphics.PixelFormat;

import android.widget.LinearLayout;

import android.view.View.OnTouchListener;
import  android.view.MotionEvent;

import android.os.AsyncTask;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.widget.Button;

import android.net.Uri;
import java.util.Date;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayInputStream;
import java.lang.ref.SoftReference;
import java.util.zip.GZIPInputStream;
import java.util.HashMap;

import java.util.Random;


import android.graphics.Typeface;

import com.lenovo.launcher2.commonui.LeAlertDialog;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.settings.SeniorSettings;



import android.view.WindowManager.LayoutParams;
import com.lenovo.launcher2.settings.MoreSettings;


import android.widget.RadioGroup;
import android.widget.RadioButton;

import com.lenovo.lejingpin.AsyncImageLoader.ImageCallback;
import com.lenovo.lejingpin.AsyncImageLoader.ImagePathCallback;

import com.lenovo.lejingpin.Holder;
import com.lenovo.lejingpin.hw.ui.Util;
import com.lenovo.lejingpin.network.WallpaperResponse;
import com.lenovo.lejingpin.network.NetworkHttpRequest;
import com.lenovo.lejingpin.network.NetworkHttpRequest.AmsCallback;
import com.lenovo.lejingpin.network.NetworkHttpRequest.HttpReturn;
import com.lenovo.lejingpin.network.WallpaperResponse.ApplicationData;
import com.lenovo.lejingpin.network.CategoryRequest5;
import com.lenovo.lejingpin.network.CategoryRequest5.CategoryResponse5;
import com.lenovo.lejingpin.network.AmsSession;
import com.lenovo.lejingpin.network.AmsSession.AmsSessionCallback;
import com.lenovo.lejingpin.network.AmsRequest;
import com.lenovo.lejingpin.network.AmsApplication;


 
public class GetMoreThemeFragment extends Fragment{
 
    public static final String HOST_WALLPAPER = "http://launcher.lenovo.com/boutique/app";

    public static final String THEME_TYPECODE = "840";


    //页卡内容
    private ViewPager mPager;
    // Tab页面列表
    private List listViews; 
    // 动画图片
    private ImageView cursor;
    // 页卡头标
    private RadioButton t1,t2,t3,t4;
    private RadioGroup mToolBar;

    // 动画图片偏移量
    private int offset = 0;
    // 当前页卡编号
    private int mCurrentPageItem = 0;
    // 动画图片宽度
    private int bmpW;
    private final static String TAG = "GetMoreThemeFragment";
    private TabHost mTabHost;
    private HashMap<String, Drawable> mIconList =
            new HashMap<String, Drawable>();
    private HashMap<String, SoftReference<Drawable>> mHotIconList =
            new HashMap<String, SoftReference<Drawable>>();
    private HashMap<String, SoftReference<Drawable>> mNewIconList =
            new HashMap<String, SoftReference<Drawable>>();

    private ArrayList<String> mNewIconUrlList = new ArrayList<String>();
    private ArrayList<String> mHotIconUrlList = new ArrayList<String>();


    int mNum;

    private View view1;
    private View view2;
    private View view3;
    private View view2_land;
    private View view3_land;
    private GridView mGrid;
    private GridView mGridNew;
    private GridView mGridHot;
    private View AllView;

    private ProgressBar progressBarV;
    private TextView loading_textV;
    private Button loading_refresh;

    private ProgressBar progressBarVHot;
    private TextView loading_textVHot;
    private Button loading_refreshHot;

    private int mStartIndex = 0;
    private int mNewStartIndex = 0;
    private int mHotStartIndex = 0;
    private int mCount = 15;
    private int mNewCount = 15;
    private int mHotCount = 15;
    private  ArrayList<AmsApplication> mDataList;
    private  ArrayList<AmsApplication> mSubDataList;
    private  ArrayList<AmsApplication> mNewDataList;
    private  ArrayList<AmsApplication> mNewSubDataList;
    private  ArrayList<AmsApplication> mHotDataList;
    private  ArrayList<AmsApplication> mHotSubDataList;
    private static final int MSG_GETLIST = 100;
    private static final int MSG_CHECKNETWORK = 101;

    private LayoutInflater mLi ;

    private AppsAdapter mAppsAdapter;
    private AppsNewAdapter mAppsNewAdapter;
    private AppsHotAdapter mAppsHotAdapter;
//    private WindowManager mWM;
    private TextView mOverlay;
    private View startEmptyView;
    private View startNewEmptyView;
    private View startHotEmptyView;
    private MyPagerAdapter mpAdapter;
    private Drawable bottom;
    private Drawable nobottom;
    private int categorytype;
    private int mOrientation;   //screen orientation
    private int mLastNewOrientation;
    private int mLastHotOrientation;

    LEJPConstant mLeConstant = LEJPConstant.getInstance();

    private AsyncImageLoader asyncImageLoader;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         
        mLi = LayoutInflater.from(getActivity());
        mIconList.clear();
        mNewIconList.clear();
        mHotIconList.clear();
        mNewIconUrlList.clear();
        mHotIconUrlList.clear();
        asyncImageLoader = new AsyncImageLoader(getActivity(),1);
        categorytype = 0;
        mOrientation = this.getResources().getConfiguration().orientation;
        mLastNewOrientation = mOrientation;
        mLastHotOrientation = mOrientation;
        Log.d(TAG, "---onCreate---new");
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    	if(newConfig.orientation != mOrientation){
    		Log.i(TAG,"====== onConfigurationChanged: old Orientation is "+mOrientation
    				+", new Orientation is "+newConfig.orientation);
    		mOrientation = newConfig.orientation;
    		if(mPager == null) return;
            if(mpAdapter != null){
            	initPageView(mCurrentPageItem);
            	initViewPager(mCurrentPageItem);
                reloadCurrentPageData(mCurrentPageItem);
            }
    	}
    }
    
    private void reloadCurrentPageData(int pageItem){
    	if(pageItem == 0){
    		mNewStartIndex = 0;
    		getDataFromNetwork(0);
            startNewEmptyView.setVisibility(View.INVISIBLE);
			mGridNew.setAdapter(mAppsNewAdapter);
			mGridNew.setVisibility(View.VISIBLE);
			mAppsNewAdapter.addMoreContent();
        }else if(pageItem == 1){
        	mHotStartIndex = 0;
        	getDataFromNetwork(1);
            startHotEmptyView.setVisibility(View.INVISIBLE);
			mGridHot.setAdapter(mAppsHotAdapter);
			mGridHot.setVisibility(View.VISIBLE);
			mAppsHotAdapter.addMoreContent();
        }
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	if(this.getResources().getConfiguration().orientation != mOrientation){
    		mCurrentPageItem = mPager.getCurrentItem();
    		initPageView(mCurrentPageItem);
        	initViewPager(mCurrentPageItem);
    		getDataFromNetwork(mCurrentPageItem);
    		mOrientation = this.getResources().getConfiguration().orientation;
    	}
    }
    
    
    private int phoneWidth;
    public String getRegUrl() {
        String post = LEJPConstant.getInstance().getPostRegist(getActivity());
        StringBuffer url = new StringBuffer(HOST_WALLPAPER);
        url.append("?").append(post);
        //url.append("?regist=");
        Log.i("zdx", "reg WallpaperinfoRequest, url=" + url.toString());
        return url.toString();
    }

    public String getUrl(int type){
        switch (type) {
            case 2:
            return getWallpaperTypeUrl();
            case 0:
            return getNewUrl();
            case 1:
            return getHotUrl();
        }
        return getRecommendUrl();
    }
    public String getWallpaperTypeUrl(){
        StringBuffer url = new StringBuffer(HOST_WALLPAPER);
        url.append("?gettypes=1");
        Log.i("zdx", "type WallpaperinfoRequest, url=" + url.toString());
        return url.toString();
    }
    public String getWallpaperTypeFirstChildPicUrl(String typeid){
        StringBuffer url = new StringBuffer(HOST_WALLPAPER);
        url.append("?type=").append(typeid).append("&s=0&t=1");
        Log.i("zdx", "type WallpaperinfoRequest, url=" + url.toString());
        return url.toString();
    }
    public String getRecommendUrl() {
        StringBuffer url = new StringBuffer(HOST_WALLPAPER);
        url.append("?method=a")
        .append("&s=").append(mStartIndex).append("&t=").append(mCount)
        .append("&f=id&a=asc");
        url.append("&time=").append(new Date().getTime());
        Log.i("zdx", "recom WallpaperinfoRequest, url=" + url.toString());
        return url.toString();
    }
    public String getHotUrl() {
        StringBuffer url = new StringBuffer(HOST_WALLPAPER);
        String model = android.os.Build.MODEL;
        model = model.replace(" ","");
        String devinfo = model+android.os.Build.VERSION.RELEASE;
        url.append("?device=").append(devinfo)
        .append("&f=downloadCount2")
        .append("&a=desc")
        .append("&c=").append(THEME_TYPECODE)
        .append("&s=").append(mHotStartIndex).append("&t=").append(mCount)
        .append("&time=").append(new Date().getDate());


        Log.i("zdx", "hot WallpaperinfoRequest, url=" + url.toString());
        return url.toString();
    }
    public String getNewUrl() {
        StringBuffer url = new StringBuffer(HOST_WALLPAPER);
         String model = android.os.Build.MODEL;
        model = model.replace(" ","");
        String devinfo = model+android.os.Build.VERSION.RELEASE;
  //      String devinfo = "LenovoS8204.2.1";
        url.append("?device=").append(devinfo)
        .append("&f=publishDate")
        .append("&a=desc")
        .append("&c=").append(THEME_TYPECODE)
        .append("&s=").append(mNewStartIndex).append("&t=").append(mCount)
        .append("&time=").append(new Date().getDate());
        Log.i("zdx", "new WallpaperinfoRequest, url=" + url.toString());
        return url.toString();
    }

    private void startWallpaperTypeActivity(String title,String type){
         Intent intent = new Intent(getActivity(), ShowWallpaperTypeActivity.class);
         intent.putExtra("TITLE",title);
         intent.putExtra("TYPE",type);
         startActivity(intent);
    }


    private Handler mInitHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_GETLIST:
                    showGridViewContent();
                break;
                case MSG_CHECKNETWORK:
                    if( categorytype == 0 && mNewDataList == null){ 
                    showErrorView(categorytype,0);
                    }
                    if( categorytype == 1 && mHotDataList == null){ 
                    showErrorView(categorytype,0);
                    }
                break;
                default:
                	break;
            }
        }
    };
     private void hideInitLoadView(int type){
        Log.e(TAG,"hideInitLoadView curtype="+type);
        if(type == 0){
            mGridNew.setVisibility(View.VISIBLE);
            startNewEmptyView.setVisibility(View.INVISIBLE);
        }
        else if(type == 1){
            mGridHot.setVisibility(View.VISIBLE);
            startHotEmptyView.setVisibility(View.INVISIBLE);
        }
    }


    private boolean mLoadFlag = false;
    private boolean mLoadNewFlag = false;
    private boolean mLoadHotFlag = false;

	private void showGridViewContent() {
		if (mPager == null)
			return;
		int curtype = categorytype;//mPager.getCurrentItem();
		Log.e(TAG, "showGridViewContent curtype=" + curtype);
		if (curtype == 1) {// hot
			if (mHotStartIndex == 0) {
				if (mHotDataList != null && mHotDataList.size() == 0) {
					Log.e(TAG, "hot 6666showGridViewContent curtype=" + curtype);
					showErrorView(categorytype, 1);
					startHotEmptyView.setVisibility(View.VISIBLE);
					return;
				}

				mGridHot.setAdapter(mAppsHotAdapter);
				if (mLoadHotFlag) {
					// mLoadHotFlag = true;
					startHotEmptyView.setVisibility(View.VISIBLE);
					mGridHot.setVisibility(View.INVISIBLE);
				} else {
					startHotEmptyView.setVisibility(View.INVISIBLE);
					mGridHot.setVisibility(View.VISIBLE);
				}
			} else {
				mAppsHotAdapter.addMoreContent();
			}
		} else {
			if (mNewDataList != null && mNewDataList.size() == 0) {
				Log.i(TAG,"mNewDataList no data");
				showErrorView(categorytype, 1);
				startNewEmptyView.setVisibility(View.VISIBLE);
				return;
			}
			if (mNewDataList == null)
				return;
			Log.i(TAG,"mNewStartIndex is "+mNewStartIndex);
			if (mNewStartIndex == 0) {
				mGridNew.setAdapter(mAppsNewAdapter);
				if (mLoadNewFlag) {
					// mLoadNewFlag = true;
					Log.i(TAG,"empty view  show");
					startNewEmptyView.setVisibility(View.VISIBLE);
					mGridNew.setVisibility(View.INVISIBLE);
				} else {
					Log.i(TAG,"empty view  hide");
					startNewEmptyView.setVisibility(View.INVISIBLE);
					mGridNew.setVisibility(View.VISIBLE);
				}
			} else {
				mAppsNewAdapter.addMoreContent();
			}
		}
	}
    private void showErrorView(int type ,int flag){
		try {
			if (type == 0) {
				progressBarV.setVisibility(View.GONE);
				CharSequence text = getText(R.string.le_list_empty);
				if (flag == 1) {
					text = getText(R.string.le_list_empty);
				} else {
					text = getText(R.string.grid_empty_error);
				}
				loading_textV.setText(text.toString());
				loading_refresh.setVisibility(View.VISIBLE);
			} else if (type == 1) {
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

    /** get the Joson data. */
    private void getDataFromNetworkOld(final int type){
        new Thread() {
            public void run() {
                NetworkHttpRequest request = new NetworkHttpRequest();
                String mtypes = "wallpapers";
                if(type == 0){
                    mtypes ="types";
                }else{
                    mtypes ="wallpapers";
                }
                final WallpaperResponse response = new WallpaperResponse();
                response.setResponseType(mtypes);
                /*HttpReturn httpReturn = */request.executeHttpGet(getActivity(),getUrl(type),
                new AmsCallback(){
                    public void onResult(int code, byte[] bytes) {
                        Log.i(TAG,"AppInfoAction.requestAppInfo, AmsSession.init >> result code:"+ code);
                        if(code==200){
                            if(bytes!=null && bytes.length!=0){
                                response.parseFrom(bytes);
                                if(type == 0){
/*
                                if( mStartIndex == 0){
                                    mDataList = response.getApplicationItemList();
                        Log.i(TAG,"AppInfoAction.requestAppInfo, AmsSession.init >> result datasize:"+ mDataList.size());
                                }else{
                                    mSubDataList = response.getApplicationItemList();
                                    mDataList.addAll(mSubDataList);
                                }
                                }else if(type == 2){
                                if( mHotStartIndex == 0){
                                    mHotDataList = response.getApplicationItemList();
                                }else{
                                    mHotSubDataList = response.getApplicationItemList();
                                    mHotDataList.addAll(mHotSubDataList);
                                }
                                }else if(type == 1){
                                if( mNewStartIndex == 0){
                                    mNewDataList = response.getApplicationItemList();
                                    Log.e(TAG,"posiint ================sieze======="+mNewDataList);
                                }else{
                                    mNewSubDataList = response.getApplicationItemList();
                                    mNewDataList.addAll(mNewSubDataList);
                                    Log.e(TAG,"posiint ================sieze======="+mNewDataList);
                                }
                                */}
                                mInitHandler.sendEmptyMessage(MSG_GETLIST);
                                //Log.e(TAG,"posiint ================sieze======="+mDataList.size());
                            }
                        }else{
                        }
                    }
                });
            }
        }.start();
    }
    private void getDataFromNetwork(final int type){
        Log.i(TAG, "SpecialAppListAction.getSpecialAppList");
        categorytype = type;
        new Thread() {
            public void run() {

        NetworkHttpRequest netrequest = new NetworkHttpRequest();
        /*HttpReturn httpReturn = */netrequest.executeHttpGet(getActivity(),getUrl(type),
                 new AmsCallback(){
                public void onResult(int code, byte[] bytes) {

                Log.i(TAG, "SpecialAppListAction.pList, AmsSession.execute >> result code:"+ code );
                if( code == 200 ){
                    if(bytes != null) {
                        CategoryResponse5 response = new CategoryResponse5();
                        response.parseFrom(bytes);
                        boolean successResponse = response.getIsSuccess();
                        Log.i(TAG," SpecialstAction.getSpecialAppList,response success :"+ successResponse);
                        if(successResponse){
                            if(type == 1){
                                if( mHotStartIndex == 0){
                                    mHotDataList = response.getApplicationItemList();
                                    Log.e(TAG,"posiint ================sieze======="+mHotDataList.size());
                                }else{
                                    mHotSubDataList = response.getApplicationItemList();
                                    mHotDataList.addAll(mHotSubDataList);
                                }
                            }else if(type == 0){
                                if( mNewStartIndex == 0){
                                    mNewDataList = response.getApplicationItemList();
                                    Log.e(TAG,"posiint ================sieze======="+mNewDataList.size());
                                }else{
                                    mNewSubDataList = response.getApplicationItemList();
                                    mNewDataList.addAll(mNewSubDataList);
                                    Log.e(TAG,"posiint ================sieze======="+mNewDataList);
                                }

                            /*mDataList = response.getApplicationItemList();
                            if(mDataList!=null && !mDataList.isEmpty()){
                                mInitHandler.removeMessages(MSG_GETLIST);
                                mInitHandler.sendMessageDelayed(mInitHandler.obtainMessage(MSG_GETLIST), 0);
                            }*/
                            } 
                            mInitHandler.sendEmptyMessage(MSG_GETLIST);
                        }else{
                            String retStr = response.getReturnStatus();
                            Log.i(TAG," SpecialstAction.getSpecialAppList,response success :"+ retStr);
                            if(retStr != null && retStr.equals("not_regist")){
                               registerTheDevice(type);
                            }
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
    public void registerTheDevice(final int type){
       new Thread() {
            public void run() {

//        String post = LEJPConstant.getInstance().getPostRegist(getActivity());
        NetworkHttpRequest netrequest = new NetworkHttpRequest();
        /*HttpReturn httpReturn = */netrequest.executeHttpGet(getActivity(),getRegUrl(),
                 new AmsCallback(){
                public void onResult(int code, byte[] bytes) {

                Log.i(TAG, "registerTheDevide, AmsSession.execute >> result code:"+ code );
                if( code == 200 ){
                    if(bytes != null) {
                         String sCategory = new String(bytes);
                         Log.i(TAG, "registerTheDevice, AmsSession.execute >> result code:"+ sCategory );
                         if("success".equals(sCategory)){
                             getDataFromNetwork(type);
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

    /** get the icon preview. */
    public void loadImg(final String url, final OnImgLoadListener callback ) {
        if(TextUtils.isEmpty(url)) return;
        AsyncTask.execute(new Runnable() {
            public void run() {
                NetworkHttpRequest request = new NetworkHttpRequest();
                /*HttpReturn httpReturn = */request.executeHttpGet(getActivity(),url,
                new AmsCallback(){
                    public void onResult(int code, byte[] bytes) {
                        if(bytes == null) return;
                        Drawable drawable = null;
                        ByteArrayInputStream bs = null;
                        GZIPInputStream input = null;
                        try {
                            input = new GZIPInputStream(new ByteArrayInputStream(bytes));
                            drawable = Drawable.createFromStream(input, null);
                        } catch (Exception e) {
                            bs = new ByteArrayInputStream(bytes);
                            drawable = Drawable.createFromStream(bs, null);
                        } finally {
                            try {
                                if (input != null) {
                                    input.close();
                                }
                                if (bs != null) {
                                    bs.close();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        callback.onLoadComplete(drawable);
                    }
                });
            }
        });
    }
    public static interface OnImgLoadListener {
        void onLoadComplete(Drawable img);
    }
/*
    private void getFirstChildIconUrlById(final int id){
        new Thread() {
            public void run() {
                NetworkHttpRequest request = new NetworkHttpRequest();
                final WallpaperResponse response = new WallpaperResponse();
                response.setResponseType("wallpapers");
                final String iconUrl = getWallpaperTypeFirstChildPicUrl(String.valueOf(id));
                HttpReturn httpReturn = request.executeHttpGet(getActivity(),iconUrl,
                new AmsCallback(){
                    public void onResult(int code, byte[] bytes) {
                        Log.i(TAG,"aaaaaaaaaaaAppIon.requestAppInfo, @@@@@@@@@2222AmsSession.init >> result code:"+ code);
                   }
               });
            }
        }.start();
    }

*/
    public class MoreHolder {
    	public LinearLayout mimg;
    	public Drawable mdb;
    	public MoreHolder(LinearLayout img,Drawable db){
    		mimg = img;
    		mdb = db;
    	}
    }
    public class AppsAdapter extends BaseAdapter {
        public AppsAdapter() {
        }
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
            	MoreHolder holder = (MoreHolder)msg.obj;
            	holder.mimg.setBackgroundDrawable(holder.mdb);
            	hideInitLoadView(0);
            }
        };
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mLi.inflate(R.layout.wallpaper_types_item, parent, false);
            }
/*
            final TextView wallpaperview = (TextView) convertView.findViewById(R.id.textname);
            final LinearLayout image = (LinearLayout) convertView.findViewById(R.id.textpic);

            final ApplicationData mdata = mDataList.get(position);
            wallpaperview.setText(mdata.name);
            
            //new Thread() {
                //public void run() {
            AsyncTask.execute(new Runnable() {
                public void run() {

                    NetworkHttpRequest request = new NetworkHttpRequest();
                    final WallpaperResponse response = new WallpaperResponse();
                    response.setResponseType("wallpapers");
                    final String iconUrl = getWallpaperTypeFirstChildPicUrl(String.valueOf(mdata.getAppId()));
                    HttpReturn httpReturn = request.executeHttpGet(getActivity(),iconUrl,
                    new AmsCallback(){
                        public void onResult(int code, byte[] bytes) {
                        Log.i(TAG,"AppInfoAction.requestAppInfo, @@@@@@@@@2222AmsSession.init >> result code:"+ code);
                        if(code==200){
                            if(bytes!=null && bytes.length!=0){
                                response.parseFrom(bytes);
                                ArrayList<ApplicationData> mchildDataList = response.getApplicationItemList();
                                if(mchildDataList.size() == 0) return;
                                final ApplicationData mchilddata = mchildDataList.get(0);
                                final String childiconUrl = mchilddata.iconUrl;
                                //Log.e(TAG,"childiconurl ================sieze==name====="+childiconUrl);
                                final String pkgName = mchilddata.getPackage_name();
                                if( mIconList.containsKey(pkgName)){
                                    if(getActivity() != null){
                                                getActivity().runOnUiThread(new Runnable() {
                                                    public void run() {

                                    image.setBackgroundDrawable(mIconList.get(pkgName));
                                    }
                                    });
                                    }
                                }else if(!TextUtils.isEmpty(childiconUrl)) {
                	            	asyncImageLoader.loadDrawable(image,childiconUrl,0,0,new ImageCallback() {  
                	                    public void imageLoaded(View imageview,Drawable imageDrawable, int position,int j) { 
            		                    	Message message = handler.obtainMessage(0, new MoreHolder((LinearLayout)imageview,imageDrawable));  
            		                        handler.sendMessage(message);
                                            mIconList.put(pkgName, imageDrawable);
                                       }
                                    });
                                }else{
                                    image.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.magicdownload_push_app_icon_def));
                                }
            }}}});
            }});
            //}}.start();
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startWallpaperTypeActivity(mdata.name,String.valueOf(mdata.getAppId()));
                }
            });
*/
            return convertView;
        }
        public void addMoreContent() {
            notifyDataSetChanged();
        }
        public final int getCount() {
            //Log.e(TAG,"posiint ================sieze======="+mDataList.size());
            return (mDataList == null) ? 0 : mDataList.size();
        }
        public final Object getItem(int position) {
            return mDataList.get(position);
        }
        public final long getItemId(int position) {
            return position;
        }
    }
    public class AppsHotAdapter extends BaseAdapter {
        public AppsHotAdapter() {
        }
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
            	Holder holder = (Holder)msg.obj;
            	//holder.mimg.setBackgroundDrawable(holder.mdb);
            	holder.mimg.setImageDrawable(holder.mdb);
//            	hideInitLoadView(0);
            }
        };
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                if(phoneWidth<= 480){
                convertView = mLi.inflate(R.layout.theme_item, parent, false);
                }else{
                convertView = mLi.inflate(R.layout.theme_480item, parent, false);
                }
            }
            final int pos = position;
            try{
            final TextView wallpaperview = (TextView) convertView.findViewById(R.id.textname);
            final ImageView image = (ImageView) convertView.findViewById(R.id.textpic);

            final AmsApplication mdata = mHotDataList.get(position);
            wallpaperview.setText(mdata.getAppName());
            Log.e(TAG,"posiint ================sieze==name====="+mdata.getAppName());
            final String pkgName = mdata.getPackage_name();
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
                    final String []paths =  mdata.thumbpaths;
                    asyncImageLoader.loadDrawable(image,paths[0],0,pos,new ImageCallback() {
                        public void imageLoaded(final View image,Drawable imageDrawable, int position,int j) {
                            mHotIconList.put(mHotDataList.get(position).getPackage_name(), 
                                           new SoftReference<Drawable>(imageDrawable));
                            Message message = handler.obtainMessage(0, new Holder((ImageView)image,imageDrawable));
                            handler.sendMessage(message);
                        }
                    });

                }
            }
            }catch(Exception e){
                Log.d(TAG,"startWallpaperDetailActivity"+e);
            }
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("c","startWallpaperDetailActivity");
                    startWallpaperDetailActivity(1,pos,mHotDataList);
                }
            });
            return convertView;
        }
        public void addMoreContent() {
            notifyDataSetChanged();
        }
        public final int getCount() {
            return (mHotDataList == null) ? 0 : mHotDataList.size();
        }
        public final Object getItem(int position) {
            return mHotDataList.get(position);
        }
        public final long getItemId(int position) {
            return position;
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
            	//hideInitLoadView(0);
            }
        };
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                if(phoneWidth<= 480){
                convertView = mLi.inflate(R.layout.theme_item, parent, false);
                }else{
                convertView = mLi.inflate(R.layout.theme_480item, parent, false);
                }
            }
            final int pos = position;
            try{
            final TextView wallpaperview = (TextView) convertView.findViewById(R.id.textname);
            final ImageView image = (ImageView) convertView.findViewById(R.id.textpic);

            final AmsApplication mdata = mNewDataList.get(position);
            wallpaperview.setText(mdata.getAppName());
            final String pkgName = mdata.getPackage_name();
            Log.e(TAG,"posiint ================sieze==pkgnamename====="+pkgName);
            boolean flag = false;
            if(mNewIconList.containsKey(pkgName)){
                 Drawable tmppic = mNewIconList.get(pkgName).get();
                if(tmppic != null){
                    image.setImageDrawable(mNewIconList.get(pkgName).get());
                }else{
                    flag = true;
                    mNewIconUrlList.remove(pkgName);
                }

            }
            if(!mNewIconList.containsKey(pkgName) || flag){
                if(mNewIconUrlList.contains(pkgName)){
                }else{
                    mNewIconUrlList.add(pkgName);
                    image.setImageResource(R.drawable.lemagicdownload_push_app_icon_def);
                    final String []paths =  mdata.thumbpaths;
                    asyncImageLoader.loadDrawable(image,paths[0],0,pos,new ImageCallback() {
                        public void imageLoaded(final View image,Drawable imageDrawable, int position,int j) {
                            mNewIconList.put(mNewDataList.get(position).getPackage_name(),
                                           new SoftReference<Drawable>(imageDrawable));
                            Message message = handler.obtainMessage(0, new Holder((ImageView)image,imageDrawable));
                            handler.sendMessage(message);
                            }
                    });
                }
            }
            }catch(Exception e){
                Log.d(TAG,"startWallpaperDetailActivity"+e);
            }
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("c","startWallpaperDetailActivity");
                    startWallpaperDetailActivity(1,pos,mNewDataList);
                }
            });
            return convertView;
        }
        public void addMoreContent() {
            notifyDataSetChanged();
        }
        public final int getCount() {
            return (mNewDataList == null) ? 0 : mNewDataList.size();
        }
        public final Object getItem(int position) {
            return mNewDataList.get(position);
        }
        public final long getItemId(int position) {
            return position;
        }
    }

    private void startWallpaperDetailActivity(int type,int pos,ArrayList<AmsApplication> DataList){
        Intent intent = new Intent(getActivity(), DetailClassicActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("EXTRA",pos);
        intent.putExtra("TYPEINDEX",type);
        intent.putExtra("TYPEDATA",DataList);
        startActivity(intent);

   }
    private void showNetworkClosedText(View view){
    	TextView text = (TextView) view.findViewById(R.id.network_close_text);
		CharSequence text_string = getText(R.string.network_close_body);
		text.setText(Html.fromHtml(text_string.toString()));
		text.setMovementMethod(LinkMovementMethod.getInstance());
		text.setVisibility(View.VISIBLE);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
    	Log.i(TAG,"onCreateView");
        if(AllView == null){
        AllView = inflater.inflate(R.layout.fragment_getmore_theme, container, false);
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
   		    dialog.show();
   			return AllView;
	   }else{
	        getDataFromNetwork(0);
	        categorytype = 0;
	        mAppsNewAdapter = new AppsNewAdapter();
	        bottom = getActivity().getResources().getDrawable(R.drawable.tab_selected_holo_line);
	        nobottom = getActivity().getResources().getDrawable(R.drawable.tab_selected_holo_line_short_no);
	        DisplayMetrics metrics = new DisplayMetrics();
	        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
	        phoneWidth = metrics.widthPixels;
	   }
        view2 = mLi.inflate(R.layout.getmore_theme_new, null);
        view2_land = mLi.inflate(R.layout.getmore_theme_new, null);
        if(mOrientation == Configuration.ORIENTATION_LANDSCAPE){
        	mGridNew = (GridView) view2_land.findViewById(R.id.getmore_theme_new);
            startNewEmptyView = (View) view2_land.findViewById(R.id.empty);
        }else{
        	mGridNew = (GridView) view2.findViewById(R.id.getmore_theme_new);
            startNewEmptyView = (View) view2.findViewById(R.id.empty);
        }
        progressBarV = (ProgressBar) startNewEmptyView.findViewById(R.id.progressing);
        loading_textV = (TextView) startNewEmptyView.findViewById(R.id.loading_text);
        loading_refresh = (Button) startNewEmptyView.findViewById(R.id.refresh_button);
        loading_refresh.setOnClickListener(new OnClickListener() {
             public void onClick(View v) {
                 if( "other".equals( getConnectType()))
                     return;
                 getDataFromNetwork(0);
             }
         });


        mGridNew.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
                    //int maPosition = mGrid.getFirstVisiblePosition();
                    //int mbPosition = mGridNew.getLastVisiblePosition();
                    if(mAppsNewAdapter != null ){
                        if( mGridNew.getLastVisiblePosition() == mAppsNewAdapter.getCount() -1){
                            mNewStartIndex = mNewStartIndex+mNewCount;
                            getDataFromNetwork(0);
                        }
                    }
                    //Log.d(TAG, "aaa---onScroll---n+ lastVisiblieposiont===="+mbPosition);
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                    int totalItemCount) {
                //Log.d(TAG, "ddd-new--firstVisibliep==="+firstVisibleItem+" Visibliconu=="+visibleItemCount+" totalitemcount--------"+totalItemCount);
            }
        });
        view3 = mLi.inflate(R.layout.getmore_theme_hot, null);
        view3_land = mLi.inflate(R.layout.getmore_theme_hot, null);
        if(mOrientation == Configuration.ORIENTATION_LANDSCAPE){
        	mGridHot = (GridView) view3_land.findViewById(R.id.getmore_theme_hot);
            startHotEmptyView = (View) view3_land.findViewById(R.id.empty);
        }else{
        	mGridHot = (GridView) view3.findViewById(R.id.getmore_theme_hot);
            startHotEmptyView = (View) view3.findViewById(R.id.empty);
        }
        progressBarVHot = (ProgressBar) startHotEmptyView.findViewById(R.id.progressing);
        loading_textVHot = (TextView) startHotEmptyView.findViewById(R.id.loading_text);
        loading_refreshHot = (Button) startHotEmptyView.findViewById(R.id.refresh_button);
        loading_refreshHot.setOnClickListener(new OnClickListener() {
             public void onClick(View v) {
                 if( "other".equals( getConnectType()))
                     return;
                 getDataFromNetwork(1);
             }
         });

        mGridHot.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
                    //int maPosition = mGrid.getFirstVisiblePosition();
                    //int mbPosition = mGridHot.getLastVisiblePosition();
                    if(mAppsHotAdapter != null ){
                        if( mGridHot.getLastVisiblePosition() == mAppsHotAdapter.getCount() -1){
                            //mHotStartIndex = mHotStartIndex+mHotCount;
                            //getDataFromNetwork(1);
                        }
                    }
                    //Log.d(TAG, "aaa---onScroll---n+ lastVisiblieposiont===="+mbPosition);
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                    int totalItemCount) {
                //Log.d(TAG, "ddd-hot--firstVisibliep==="+firstVisibleItem+" Visibliconu=="+visibleItemCount+" totalitemcount--------"+totalItemCount);
            }
        });

        InitImageView();
        InitBottomBarView(); 
        initViewPager(0);
        }
        return AllView;
    }
    
    private void initPageView(int pageItem){
    	Log.i(TAG,"=========initPageView:  pageItem is "+pageItem);
    	view2 = mLi.inflate(R.layout.getmore_theme_new, null);
    	view2_land = mLi.inflate(R.layout.getmore_theme_new, null);
    	if(mOrientation == Configuration.ORIENTATION_LANDSCAPE){
        	mGridNew = (GridView) view2_land.findViewById(R.id.getmore_theme_new);
            startNewEmptyView = (View) view2_land.findViewById(R.id.empty);
        }else{
        	mGridNew = (GridView) view2.findViewById(R.id.getmore_theme_new);
            startNewEmptyView = (View) view2.findViewById(R.id.empty);
        }
        progressBarV = (ProgressBar) startNewEmptyView.findViewById(R.id.progressing);
        loading_textV = (TextView) startNewEmptyView.findViewById(R.id.loading_text);
        loading_refresh = (Button) startNewEmptyView.findViewById(R.id.refresh_button);
        loading_refresh.setOnClickListener(new OnClickListener() {
             public void onClick(View v) {
                 if( "other".equals( getConnectType()))
                     return;
                 getDataFromNetwork(0);
             }
         });


        mGridNew.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
                    //int maPosition = mGrid.getFirstVisiblePosition();
                    //int mbPosition = mGridNew.getLastVisiblePosition();
                    if(mAppsNewAdapter != null ){
                        if( mGridNew.getLastVisiblePosition() == mAppsNewAdapter.getCount() -1){
                            mNewStartIndex = mNewStartIndex+mNewCount;
                            getDataFromNetwork(0);
                        }
                    }
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                    int totalItemCount) {
            }
        });
        view3 = mLi.inflate(R.layout.getmore_theme_hot, null);
        view3_land = mLi.inflate(R.layout.getmore_theme_hot, null);
        if(mOrientation == Configuration.ORIENTATION_LANDSCAPE){
        	mGridHot = (GridView) view3_land.findViewById(R.id.getmore_theme_hot);
            startHotEmptyView = (View) view3_land.findViewById(R.id.empty);
        }else{
        	mGridHot = (GridView) view3.findViewById(R.id.getmore_theme_hot);
            startHotEmptyView = (View) view3.findViewById(R.id.empty);
        }
        progressBarVHot = (ProgressBar) startHotEmptyView.findViewById(R.id.progressing);
        loading_textVHot = (TextView) startHotEmptyView.findViewById(R.id.loading_text);
        loading_refreshHot = (Button) startHotEmptyView.findViewById(R.id.refresh_button);
        loading_refreshHot.setOnClickListener(new OnClickListener() {
             public void onClick(View v) {
                 if( "other".equals( getConnectType()))
                     return;
                 getDataFromNetwork(1);
             }
         });

        mGridHot.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
                    if(mAppsHotAdapter != null ){
                        if( mGridHot.getLastVisiblePosition() == mAppsHotAdapter.getCount() -1){
                            //mHotStartIndex = mHotStartIndex+mHotCount;
                            //getDataFromNetwork(1);
                        }
                    }
                    //Log.d(TAG, "aaa---onScroll---n+ lastVisiblieposiont===="+mbPosition);
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                    int totalItemCount) {
            }
        });

        InitImageView();
        InitBottomBarView(); 
    }
    
    
 /*   private void InitOverlay() {
        mOverlay = (TextView) mLi.inflate(R.layout.overlay_getmore_text, null);
        mOverlay.setVisibility(View.INVISIBLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT);
        mWM.addView(mOverlay, lp);
    }*/
/*
    private void initLoadView() {
        loadView = mLi.inflate(R.layout.init_loading,null);
        loadView.setVisibility(View.INVISIBLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT);
        mWM.addView(loadView, lp);
        loadView.setVisibility(View.VISIBLE);
    }


    private void showOverLay(){
        mOverlay.setVisibility(View.VISIBLE);
    }
    private void hideOverLay(){
        mOverlay.setVisibility(View.GONE);
    }
*/

      /**
      * 初始化头标
    */
    private void InitBottomBarView() {
         mToolBar = (RadioGroup) AllView.findViewById(R.id.toolBar);

         //t1 = (RadioButton) mToolBar.findViewById(R.id.text1);
         t2 = (RadioButton) mToolBar.findViewById(R.id.text2);
         t3 = (RadioButton) mToolBar.findViewById(R.id.text3);
         //t1.setText(R.string.theme_store_tab_category);
         t2.setText(R.string.theme_store_tab_latest);
         t3.setText(R.string.theme_store_tab_hot);
         Typeface tf = SettingsValue.getFontStyle(getActivity());
         if (tf != null ){
             t2.setTypeface(tf);
             t3.setTypeface(tf);
         }

         if(mCurrentPageItem == 0){
        	 t2.setChecked(true);
	         //t1.setCompoundDrawablesWithIntrinsicBounds(null,null,null,bottom);
	         t2.setCompoundDrawablesWithIntrinsicBounds(null,null,null,bottom);
	         t3.setCompoundDrawablesWithIntrinsicBounds(null,null,null,nobottom);
         }else{
        	 t3.setChecked(true);
        	 t3.setCompoundDrawablesWithIntrinsicBounds(null,null,null,bottom);
	         t2.setCompoundDrawablesWithIntrinsicBounds(null,null,null,nobottom);
         }
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
/*
                case R.id.text1:
                    mPager.setCurrentItem(0);
                    t1.setCompoundDrawablesWithIntrinsicBounds(null,null,null,bottom);
                    t2.setCompoundDrawablesWithIntrinsicBounds(null,null,null,nobottom);
                    t3.setCompoundDrawablesWithIntrinsicBounds(null,null,null,nobottom);
                    break;
*/
                case R.id.text2:
                	Log.i(TAG, "======= mSwitchButtonListener cur item is"+0);
                    mPager.setCurrentItem(0);
                    t2.setCompoundDrawablesWithIntrinsicBounds(null,null,null,bottom);
                    t3.setCompoundDrawablesWithIntrinsicBounds(null,null,null,nobottom);
                    break;
                case R.id.text3:
                	Log.i(TAG, "======= mSwitchButtonListener cur item is"+1);
                    mPager.setCurrentItem(1);
                    t3.setCompoundDrawablesWithIntrinsicBounds(null,null,null,bottom);
                    t2.setCompoundDrawablesWithIntrinsicBounds(null,null,null,nobottom);
                    break;
                default:
                   	break;
            }

        }
    };
    /**
      * 初始化ViewPager
    */
    private void initViewPager(int itemIndex) {
        mPager = (ViewPager) AllView.findViewById(R.id.vPager);
        listViews = new ArrayList();
        mpAdapter = new MyPagerAdapter();
        listViews.add(view2);
        listViews.add(view2_land);
        listViews.add(view3);
        listViews.add(view3_land);
        mPager.setAdapter(mpAdapter);
        Log.i(TAG,"====== InitViewPager: setCurrentItem "+itemIndex);
        mPager.setCurrentItem(itemIndex);
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }

    private void replaceView(){
        mpAdapter.destroyItem(mPager, 0,null);
        listViews.add(view2);
        listViews.add(view2_land);
        listViews.add(view3);
        listViews.add(view3_land);
        mpAdapter.instantiateItem(mPager, 0);
        mPager.setAdapter(mpAdapter);
//        mPager.setCurrentItem(0);
        int id = mToolBar.getCheckedRadioButtonId();
        switch(id){
            case R.id.text2:
            		Log.i(TAG,"====== replaceView: setCurrentItem "+0);
                    mPager.setCurrentItem(0);
                    break;
                case R.id.text3:
                	Log.i(TAG,"====== replaceView: setCurrentItem "+1);
                    mPager.setCurrentItem(1);
                    break;
            default:
                 break;

         }
    }

     
    /**
      * 初始化动画
    */
    private void InitImageView() {
        cursor = (ImageView) AllView.findViewById(R.id.cursor);
        bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.tab_selected_holo_line)
                .getWidth();// 获取图片宽度
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;// 获取分辨率宽度
        offset = (screenW / 3 - bmpW) / 2;// 计算偏移量
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
    }
     
    /**
      * ViewPager适配器
    */
    final int pageNum = 2; 
    public class MyPagerAdapter extends PagerAdapter {
        public MyPagerAdapter() {
        }
        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
        	int index = -1;   //index of the view in listviews
        	if(arg1 == 1){
        		if(mOrientation == Configuration.ORIENTATION_LANDSCAPE){
           		 	index = 3;
           	 	}else{
           	 		index = 2;
           	 	}
        	}else if(arg1 == 0){
        		if(mOrientation == Configuration.ORIENTATION_LANDSCAPE){
           		 	index = 1;
           	 	}else{
           	 		index = 0;
           	 	}
        	}
        	
        	if(listViews.get(index) instanceof View){
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
//        	Log.i(TAG,"====instantiateItem arg1 = "+arg1);
        	int index = -1;   //index of the view in listviews
            try {
            	if(arg1 == 1){
            		if(mOrientation == Configuration.ORIENTATION_LANDSCAPE){
               		 	index = 3;
               	 	}else{
               	 		index = 2;
               	 	}
            	}else if(arg1 == 0){
            		if(mOrientation == Configuration.ORIENTATION_LANDSCAPE){
               		 	index = 1;
               	 	}else{
               	 		index = 0;
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
    /**
      * 页卡切换监听
    */
    public class MyOnPageChangeListener implements OnPageChangeListener {
 
        int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
        int two = one * 2;// 页卡1 -> 页卡3 偏移量
 
        @Override
        public void onPageSelected(int arg0) {
        	Log.e("XXXX","onPageSelected arg0 = "+arg0); 
			mCurrentPageItem = arg0;
			if (arg0 == 1) {
				if (mAppsHotAdapter == null) {
					mAppsHotAdapter = new AppsHotAdapter();
					getDataFromNetwork(1);
				} else {
					Log.e("XXXX","mLastHotOrientation = "+mLastHotOrientation + 
                			", mOrientation = "+mOrientation);
					if (mLastHotOrientation != mOrientation) {
		                reloadCurrentPageData(mCurrentPageItem);
						mLastHotOrientation = mOrientation;
					}else{
						startHotEmptyView.setVisibility(View.INVISIBLE);
						mGridHot.setVisibility(View.VISIBLE);
						mGridHot.setAdapter(mAppsHotAdapter);
						mAppsHotAdapter.addMoreContent();
					}
				}
			} else if (arg0 == 0) {
				if (mAppsNewAdapter == null) {
					mAppsNewAdapter = new AppsNewAdapter();
					getDataFromNetwork(0);
				} else {
					Log.e("XXXX","mLastNewOrientation = "+mLastNewOrientation + 
                			", mOrientation = "+mOrientation);
					if (mLastNewOrientation != mOrientation) {
		                reloadCurrentPageData(mCurrentPageItem);
						mLastNewOrientation = mOrientation;
					}else{
						startNewEmptyView.setVisibility(View.INVISIBLE);
						mGridNew.setVisibility(View.VISIBLE);
						mGridNew.setAdapter(mAppsNewAdapter);
						mAppsNewAdapter.addMoreContent();
					}
				}
			}
            switch (arg0) {
            case 0:
                Log.d(TAG, "---0---");
                t2.setChecked(true);
                break;
            case 1:
                Log.d(TAG, "---1---");
                t3.setChecked(true);
                break;
            case 2:
                Log.d(TAG, "---2---");
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
    public String getConnectType() {
        ConnectivityManager mConnMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mConnMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo infoM = mConnMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if(info != null && info.isConnected()) {
            return "wifi";
        } else if(infoM != null && infoM.isConnected()) { return "mobile"; }
            return "other";
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
