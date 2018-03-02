
package com.lenovo.lejingpin;


 
import com.lenovo.launcher.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.LocalActivityManager;
import android.app.TabActivity;
import android.content.Context;
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
import android.view.View;
import android.view.Window;
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
import android.text.TextUtils;
import android.widget.AbsListView;
import android.view.WindowManager;
import android.graphics.PixelFormat;

import android.widget.LinearLayout;
import android.widget.ListView;

import android.view.View.OnTouchListener;
import  android.view.MotionEvent;

import java.util.Random;

import android.os.AsyncTask;
import android.net.Uri;

import android.view.Gravity;
import java.util.Date;
import android.graphics.drawable.Drawable;
import android.widget.Button;

import android.widget.ProgressBar;

import java.io.ByteArrayInputStream;
import java.lang.ref.SoftReference;
import java.util.zip.GZIPInputStream;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;


import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.view.WindowManager.LayoutParams;

import android.widget.RadioGroup;
import android.widget.RadioButton;


import android.graphics.Typeface;
import com.lenovo.launcher2.customizer.SettingsValue;


import com.lenovo.lejingpin.AndroidWallpaperFragment.ViewHolder;
import com.lenovo.lejingpin.AsyncImageLoader.ImageCallback;

import com.lenovo.lejingpin.Holder;
import com.lenovo.lejingpin.network.WallpaperResponse;
import com.lenovo.lejingpin.network.NetworkHttpRequest;
import com.lenovo.lejingpin.network.NetworkHttpRequest.AmsCallback;
import com.lenovo.lejingpin.network.NetworkHttpRequest.HttpReturn;
import com.lenovo.lejingpin.network.WallpaperResponse.ApplicationData;

 
public class ShowWallpaperTypeFragment extends Fragment{
 
    public static final String HOST_WALLPAPER = "http://launcher.lenovo.com/wp/type";
    private static final String HOST_WALLPAPER_NOTYPE = "http://launcher.lenovo.com/wp/";

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
    // 动画图片宽度
    private int bmpW;
    private final static String TAG = "ShowWallpaperFragment";
    private TabHost mTabHost;
    private HashMap<String, SoftReference<Drawable>> mIconList =
            new HashMap<String, SoftReference<Drawable>>();
    private HashMap<String, SoftReference<Drawable>> mHotIconList =
            new HashMap<String, SoftReference<Drawable>>();
    private HashMap<String, SoftReference<Drawable>> mNewIconList =
            new HashMap<String, SoftReference<Drawable>>();
    int mNum;

    private View view1;
    private View view2;
    private View view3;
    private ListView mList;
    private GridView mGridNew;
    private GridView mGridHot;

    private ProgressBar progressBarV;
    private TextView loading_textV;
    private Button loading_refresh;

    private ProgressBar progressBarVNew;
    private TextView loading_textVNew;
    private Button loading_refreshNew;

    private ProgressBar progressBarVHot;
    private TextView loading_textVHot;
    private Button loading_refreshHot;

    private View AllView;
    private int mStartIndex = 0;
    private int mNewStartIndex = 0;
    private int mHotStartIndex = 0;
    private int mCount = 10;
    private int mNewCount = 18;
    private int mHotCount = 18;
    private  ArrayList<ApplicationData> mDataList;
    private  ArrayList<ApplicationData> mSubDataList;
    private  ArrayList<ApplicationData> mNewDataList;
    private  ArrayList<ApplicationData> mNewSubDataList;
    private  ArrayList<ApplicationData> mHotDataList;
    private  ArrayList<ApplicationData> mHotSubDataList;
    private HashMap<Integer, ApplicationData> mNewWallpaperHashmap =
            new HashMap<Integer, ApplicationData>();
    private HashMap<Integer, ApplicationData> mHotWallpaperHashmap =
            new HashMap<Integer, ApplicationData>();
    private ArrayList<String> mLoadingList = new ArrayList<String>();    //online data
    private static final int MSG_GETHOTLIST = 99;
    private static final int MSG_GETLIST = 100;
    private static final int MSG_CHECKNETWORK = 101;
    private static final int MSG_NO_MORE = 102;
    private static final int MSG_REFRESH_VIEW = 103;
    private static final int MSG_GET_NEW_PREVIEW = 104;
    private static final int MSG_GET_HOT_PREVIEW = 105;

    private LayoutInflater mLi ;
    private ArrayList<String> mIconUrlList = new ArrayList<String>();
    private ArrayList<String> mHotIconUrlList = new ArrayList<String>();


    private AppsAdapter mAppsAdapter;
    private AppsNewAdapter mAppsNewAdapter;
    private AppsHotAdapter mAppsHotAdapter;
    private WindowManager mWM;
    private TextView mOverlay;
    private View startEmptyView;
    private View startNewEmptyView;
    private View startHotEmptyView;
    private MyPagerAdapter mpAdapter;
    private int categorytype = -1;;
    private int categoryHottype = -1;
    private int mCurrentNewLoadingPos = 0;
    private int mCurrentHotLoadingPos = 0;
    private boolean mStopGetPreviewFlag = false;

    private String mPaperType;
    private Drawable bottom;
    private Drawable nobottom;

    LEJPConstant mLeConstant = LEJPConstant.getInstance();
    private int phoneWidth;

    private int mOrientation;
    private int mLastNewOrientation;
    private int mLastHotOrientation;
    int mCurrentPageItem = 0;
    
    private AsyncImageLoader asyncImageLoader;
    public ShowWallpaperTypeFragment(String type){
       mPaperType = type;
    }
    public ShowWallpaperTypeFragment(){
    }


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         
        mLi = LayoutInflater.from(getActivity());
        mNewWallpaperHashmap.clear();
        mHotWallpaperHashmap.clear();
        mLoadingList.clear();    //online data
        mIconList.clear();
        mNewIconList.clear();
        mHotIconList.clear();
        mIconUrlList.clear();
        mHotIconUrlList.clear();
        asyncImageLoader = new AsyncImageLoader(getActivity(),0);
        Log.d(TAG, "---onCreate---new");
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        phoneWidth = metrics.widthPixels;

        getDataFromNetwork(0, 0);
        mAppsNewAdapter = new AppsNewAdapter();
        bottom = getActivity().getResources().getDrawable(R.drawable.tab_selected_holo_line_short);
        nobottom = getActivity().getResources().getDrawable(R.drawable.tab_selected_holo_line_short_no);
        
        mOrientation = this.getResources().getConfiguration().orientation;
        mLastNewOrientation = mOrientation;
        mLastHotOrientation = mOrientation;
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    	Log.i(TAG,"== onConfigurationChanged");
    	Log.e("XXXX","=====showwallpapertypefragment new orientation is "+newConfig.orientation+ ", mOrientation = "+mOrientation);
    	if(newConfig.orientation != mOrientation){
    		mCurrentPageItem = mPager.getCurrentItem();
    		Log.i("XXXX","=====showwallpapertypefragment mCurrentPageItem = "+mCurrentPageItem);
    		delayCreateView(mCurrentPageItem);
    		if(mCurrentPageItem == 0){
    			getDataFromNetwork(0, mNewStartIndex);
    		}else if(mCurrentPageItem == 1){
    			getDataFromNetwork(1, mHotStartIndex);
    		}
    		mOrientation = newConfig.orientation;
    	}
    }
    
    private void stopGetPreview(){
    	int item = mPager.getCurrentItem();
    	if(item == 0){ //new
//    		mInitHandler.removeMessages(MSG_GET_PREVIEW);
    	}else if(item == 2){//hot
    		mInitHandler.removeMessages(MSG_GET_NEW_PREVIEW);
    	}
    }
    
    @Override
    public void onPause() {
    	mStopGetPreviewFlag = true;
    	stopGetPreview();
        super.onPause();
    }
    
    @Override
    public void onResume() {
    	Log.i("mohl","== ShowWallpaperTypeFragment onresume");
        super.onResume();
        if(mStopGetPreviewFlag){
        	mStopGetPreviewFlag = false;
        	int item = mPager.getCurrentItem();
        	if(item == 0){
        		if (mAppsNewAdapter != null) {
						getDataFromNetwork(0, mNewStartIndex);
				}
        	}else if(item == 1){
        		if (mAppsHotAdapter != null) {
					getDataFromNetwork(0, mHotStartIndex);
			}
        	}
        	
        }
    }
    
    public String getWallpaperTypeChildUrl(String typeid){
        StringBuffer url = new StringBuffer(HOST_WALLPAPER);
        url.append("type=").append(typeid)
        .append("&s=").append(mStartIndex).append("&t=").append(mCount);
//        .append("&f=uploadtime")
//        .append("&a=desc");

        Log.e(TAG, "WallpaperinfoRequest, url=" + url.toString());
        return url.toString();
    }

    public String getUrl(int type){
        switch (type) {
            case 0:
            return getNewUrl();
            case 1:
            return getHotUrl();
            case 2:
            return getSpecialUrl();
            case 3:
            return getTotalCountUrl();
        }
        return getRecommendUrl();
    }
    
    public String getWallpaperTypeUrl(){
        StringBuffer url = new StringBuffer(HOST_WALLPAPER);
        url.append("?gettypes=1");
        Log.i(TAG, "WallpaperinfoRequest, url=" + url.toString());
        return url.toString();
    }
    public String getWallpaperTypeFirstChildPicUrl(String typeid){
        StringBuffer url = new StringBuffer(HOST_WALLPAPER);
        url.append("?type=").append(typeid).append("&s=0&t=1");
        Log.i(TAG, "WallpaperinfoRequest, url=" + url.toString());
        return url.toString();
    }
    public String getSpecialUrl() {
        StringBuffer url = new StringBuffer(HOST_WALLPAPER);
        url.append("?type=").append(mPaperType)
        .append("&s=").append(mStartIndex).append("&t=").append(mCount);
        Log.i(TAG, "WallpaperinfoRequest, url=" + url.toString());
        return url.toString();
    }

    public String getRecommendUrl() {
        StringBuffer url = new StringBuffer(HOST_WALLPAPER);
        url.append("?method=a")
        .append("&s=").append(mStartIndex).append("&t=").append(mCount)
        .append("&f=id&a=asc");
        url.append("&time=").append(new Date().getDate());
        Log.i(TAG, "WallpaperinfoRequest, url=" + url.toString());
        return url.toString();
    }
    public String getHotUrl() {
        StringBuffer url = new StringBuffer(HOST_WALLPAPER);
        url.append("?type=").append(mPaperType)
        .append("&s=").append(mHotStartIndex).append("&t=").append(mHotCount)
        .append("&sort=hot")
        .append("&time=").append(new Date().getDate());

        Log.i(TAG, "WallpaperinfoRequest, url=" + url.toString());
        return url.toString();
    }
    public String getNewUrl() {
        StringBuffer url = new StringBuffer(HOST_WALLPAPER);
        url.append("?type=").append(mPaperType)
        .append("&s=").append(mNewStartIndex).append("&t=").append(mNewCount)
        .append("&sort=new")
        .append("&time=").append(new Date().getDate());
        Log.i(TAG, "getNew url WallpaperinfoRequest, url=" + url.toString());
        return url.toString();
    }
    public String getTotalCountUrl(){
      	 StringBuffer url = new StringBuffer(HOST_WALLPAPER_NOTYPE);
         url.append("count?type=").append(mPaperType);
         Log.i(TAG, "getTotalCountUrl, url=" + url.toString());
         return url.toString();
    }
    
    private void startWallpaperTypeActivity(String title,String type){
         Intent intent = new Intent(getActivity(), ShowWallpaperSpecialActivity.class);
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
                case MSG_GETHOTLIST:
                    showHotGridViewContent();
                break;
                case MSG_CHECKNETWORK:
                    if( categorytype == 2 && mDataList == null){
                    showErrorView(categorytype,0);
                    }
                    if( categorytype == 0 && mNewDataList == null){
                    showErrorView(categorytype,0);
                    }
                    if( categoryHottype == 1 && mHotDataList == null){
                    showErrorView(categoryHottype,0);
                    }
                break;
                case MSG_NO_MORE:
                    hideOverLay();
                break;
                case MSG_REFRESH_VIEW:
                	if(mPager.getCurrentItem() == 0){
                		mAppsNewAdapter.addMoreContent();
                	}else if(mPager.getCurrentItem() == 1){
                		mAppsHotAdapter.addMoreContent();
                	}
                	break;
                case MSG_GET_NEW_PREVIEW:
                	if(mPager.getCurrentItem() == 0){
                		getPreviewImgOneByOne(0);
                	}
                	break;
                case MSG_GET_HOT_PREVIEW:
                	if(mPager.getCurrentItem() == 1){
                		getPreviewImgOneByOne(1);
                	}
                	break;
                default:
                	break;

            }
        }
    };
    private void hideInitLoadView(int type){
        Log.e(TAG,"hideInitLoadView curtype="+type);
        if(type == 2){
            mList.setVisibility(View.VISIBLE);
            startEmptyView.setVisibility(View.INVISIBLE);
        }
        else if(type == 0){
            mGridNew.setVisibility(View.VISIBLE);
            startNewEmptyView.setVisibility(View.INVISIBLE);
        }
        else if(type == 1){
            mGridHot.setVisibility(View.VISIBLE);
            startHotEmptyView.setVisibility(View.INVISIBLE);
        }
    }

    private void showErrorView(int type ,int flag){
		try {
			if (type == 2) {
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


    private void showGridViewContent(){
        //Log.e(TAG,"showGridView==="+categorytype+"mNewDataList  ="+mNewDataList+"mHotDataList  ="+mHotDataList);
        if(mNewDataList == null) return;

        if( mNewStartIndex == 0){
                startNewEmptyView.setVisibility(View.INVISIBLE);
                //getNewDrawableDataFromNetWorkStep();
                mGridNew.setAdapter(mAppsNewAdapter);
            }else{
                //getNewSubDrawableDataFromNetWorkStep();
                mAppsNewAdapter.addMoreContent();
            }
    }
    private void showHotGridViewContent(){
        //Log.e(TAG,"showHotGridView==="+categorytype+"mNewDataList  ="+mNewDataList+"mHotDataList  ="+mHotDataList);
        if(mHotDataList == null) return;
        if( mHotStartIndex == 0){
            mGridHot.setAdapter(mAppsHotAdapter);
            startHotEmptyView.setVisibility(View.INVISIBLE);
            mGridHot.setVisibility(View.VISIBLE);
        }else{
            mAppsHotAdapter.addMoreContent();
        }
    }
    private void getDataFromNetwork(final int type, final int beginIndex){
        Log.e("yumin0429","getDataFromNetwor  beginIndex="+beginIndex+" newstartindex ="+mNewStartIndex);
        getWallpaperTotalCount(type);
        new Thread() {
            public void run() {
                NetworkHttpRequest request = new NetworkHttpRequest();
                String mtypes = "wallpapers";
                if(type == 2){
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
                                if(type == 2){
	                                if( mStartIndex == 0){
	                                    mDataList = response.getApplicationItemList();
	                                    Log.i(TAG,"requestAppInfo, AmsSession.init >> result datasize:"+ mDataList.size());
	                                }else{
	                                    mDataList = response.getApplicationItemList();
	                                    Log.i(TAG,"requestAppInfo, AmsSession.init >> result datasize:"+ mDataList.size());
	                                    if(mDataList.size() == 0){
	                                        mNoMoreNewFlag = true;
	                                        return;
	                                    }
	
	                                }
                                }else if(type == 1){
	                                if( mHotStartIndex == 0){
	                                    mHotDataList = response.getApplicationItemList();
	                                    mHotSubDataList = response.getApplicationItemList();
	                                }else{
	                                    mHotSubDataList = response.getApplicationItemList();
	                                    if(mHotSubDataList.size() == 0){
	                                        mNoMoreHotFlag = true;
	                                        return;
	                                    }
	                                }
	                                encloseWallpaperHashmap(mHotSubDataList);
                                }else if(type == 0){
	                                if( mNewStartIndex == 0){
	                                    mNewDataList = response.getApplicationItemList();
	                                    mNewSubDataList = response.getApplicationItemList();
	                                }else{
	                                    mNewSubDataList = response.getApplicationItemList();
		                                    if(mNewSubDataList.size() == 0){
		                                    mNoMoreNewFlag = true;
		                                    return;
	                                    }
	                                }
	                                encloseWallpaperHashmap(mNewSubDataList);
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

	private void encloseWallpaperHashmap(ArrayList<ApplicationData> mList) {
		if(mPager == null){
			return;
		}
		final int type = this.mPager.getCurrentItem();
		if (getActivity() != null) {
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					if(type == 0){
						if (mNewStartIndex == 0) {
							if ((mNewSubDataList == null)|| (mNewSubDataList.isEmpty())) {
								showErrorView(0, 1);
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
					}else if(type == 1){
						//hot 
						if (mHotStartIndex == 0) {
							if ((mHotSubDataList == null)|| (mHotSubDataList.isEmpty())) {
								showErrorView(1, 1);
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
		case 0:
			if(mCurrentNewLoadingPos >= mNewSubDataList.size() || mCurrentNewLoadingPos < 0){
				mInitHandler.removeMessages(MSG_GET_NEW_PREVIEW);
				return;
			}
			appdata = mNewSubDataList.get(mCurrentNewLoadingPos);
			break;
		case 1:
			if(mCurrentHotLoadingPos >= mHotSubDataList.size() || mCurrentHotLoadingPos < 0){
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
							if(preview_type == 0){
								mNewIconList.put(pkgName, new SoftReference<Drawable>(imageDrawable));
								if (mNewStartIndex != 0 && null == mNewWallpaperHashmap.get(mdata.getViewPosition())) {
									if(mNewDataList != null)
										mNewDataList.add(mdata);
								}
								mNewWallpaperHashmap.put(mdata.getViewPosition(), mdata);
								mCurrentNewLoadingPos++;
								mInitHandler.removeMessages(MSG_GET_NEW_PREVIEW);
								if (!mStopGetPreviewFlag) {
									mInitHandler.sendEmptyMessageDelayed(MSG_GET_NEW_PREVIEW, 100);
									mInitHandler.sendEmptyMessage(MSG_REFRESH_VIEW);
								}
							}else if(preview_type == 1){
								if(mHotIconList!=null){
									mHotIconList.put(pkgName, new SoftReference<Drawable>(imageDrawable));
								}
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
			if(preview_type == 1){
				Log.i(TAG,"imageLoaded hot wallpaper");
				if(mHotIconList!=null){
					Log.i(TAG,"imageLoaded hot wallpaper: mHotIconList size "+mHotIconList.size());
					mHotIconList.put(pkgName, mNewIconList.get(pkgName));
				}
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
	}
    
    /** get the Joson data. */
    private void getDataFromNetwork(final int type){
        if(type == 0) categorytype = 0;
        if(type == 1) categoryHottype = 1;
        new Thread() {
            public void run() {
                NetworkHttpRequest request = new NetworkHttpRequest();
                String mtypes = "wallpapers";
                if(type == 3){
                    mtypes ="types";
                }else{
                    mtypes ="wallpapers";
                }
                final WallpaperResponse response = new WallpaperResponse();
                response.setResponseType(mtypes,phoneWidth);
                /*HttpReturn httpReturn = */request.executeHttpGet(getActivity(),getUrl(type),
                new AmsCallback(){
                    public void onResult(int code, byte[] bytes) {
                        Log.i(TAG,"AppInfoAction.requestAppInfo, AmsSession.init >> result code:"+ code);
                        if(code==200){
                            if(bytes!=null && bytes.length!=0){
                                response.parseFrom(bytes);
                                if(type == 2){
                                if( mStartIndex == 0){
                                    mDataList = response.getApplicationItemList();
                        Log.i(TAG,"AppInfoAction.requestAppInfo, AmsSession.init >> result datasize:"+ mDataList.size());
                                }else{
                                    mSubDataList = response.getApplicationItemList();
                                    if(mSubDataList.size() == 0){
                                        mNoMoreSpecialFlag = true;
                                        return;
                                    }
                                    mDataList.addAll(mSubDataList);
                                }
                                }else if(type == 1){
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
                                mInitHandler.sendEmptyMessage(MSG_GETHOTLIST);
                                }else if(type == 0){
                                if( mNewStartIndex == 0){
                                    mNewDataList = response.getApplicationItemList();
                                }else{
                                    mNewSubDataList = response.getApplicationItemList();
                                    if(mNewSubDataList.size() == 0){
                                    mNoMoreFlag = true;
                                    return;
                                    }
                                    mNewSubStepIndex=0;
                                    mNewDataList.addAll(mNewSubDataList);
                                }
                                Log.e(TAG,"posiint ================sieze======="+mNewDataList.size());
                                mInitHandler.sendEmptyMessage(MSG_GETLIST);
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
	private void getWallpaperTotalCount(int type) {
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
											Log.i("XXX","get wallpaper total count result reponse:"+ response);
											JSONObject jsonObject;
											try {
												jsonObject = new JSONObject(response);
												Iterator it = jsonObject.keys();  
									            while (it.hasNext()) {  
									                String key = (String) it.next();  
									                Log.i("XXX","get wallpaper total count: key = "+key);
									                if("data".equals(key)){
									                	JSONObject value = jsonObject.getJSONObject(key);
									                	mTotalCount = value.getInt("count");
									                	Log.i("XXX","get wallpaper total count: mTotalCount = "+mTotalCount);
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
    
    public class AppsAdapter extends BaseAdapter {
        public AppsAdapter() {
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
                convertView = mLi.inflate(R.layout.android_wallpaper_item, parent, false);
            }
            final TextView wallpaperview = (TextView) convertView.findViewById(R.id.textname);
            final TextView wallpaperview1 = (TextView) convertView.findViewById(R.id.textname1);
            final ImageView image = (ImageView) convertView.findViewById(R.id.special_thumbnail);

            final int pos = position;
            final ApplicationData mdata = mDataList.get(position);
            final String pkgName = mdata.getPackage_name();
            wallpaperview.setText(mdata.name);
            wallpaperview1.setText(mdata.name);
             if(mIconList.containsKey(pkgName)){
                Drawable tmppic = mIconList.get(pkgName).get();
                if(tmppic != null){
                    image.setImageDrawable(mIconList.get(pkgName).get());
                }else{
                    asyncImageLoader.loadDrawable(image,mdata.previewAddr,0,pos,new ImageCallback() {
                        public void imageLoaded(final View aimage,final Drawable imageDrawable, int postion,int j) {
                        Log.e(TAG,"handle message setImageDrawable pos="+pos+" preaddr ="+mdata.previewAddr+" pkgname="+pkgName);
                        mIconList.put(pkgName, new SoftReference<Drawable>(imageDrawable));
                        Message message = handler.obtainMessage(0, new Holder((ImageView)image, imageDrawable));
                        handler.sendMessage(message);
                    }
                });
                }
            }else{
                asyncImageLoader.loadDrawable(image,mdata.previewAddr,0,pos,new ImageCallback() {
                    public void imageLoaded(final View aimage,final Drawable imageDrawable, int postion,int j) {
                        Log.e(TAG," Iconlist not ImageDrawable pos="+pos+" preaddr ="+mdata.previewAddr+" pkgname="+pkgName);
                        mIconList.put(pkgName, new SoftReference<Drawable>(imageDrawable));
                        Message message = handler.obtainMessage(0, new Holder((ImageView)image, imageDrawable));
                        handler.sendMessage(message);
                    }
                });
            }
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startWallpaperTypeActivity(mdata.name,String.valueOf(mdata.getAppId()));
                }
            });
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


    class ViewHolder{
            private ImageView image ;
        };
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
         /*   ViewHolder holder;
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
                }
            }*/
//        	ViewHolder holder;
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
//				holder = new ViewHolder();
//				holder.image = (ImageView) convertView
//						.findViewById(R.id.textpic);
//				convertView.setTag(holder);
//			} else {
//				holder = (ViewHolder) convertView.getTag();
			}
//			final ImageView image = holder.image;
            final ImageView image = (ImageView) convertView.findViewById(R.id.textpic);
			if (mHotDataList != null) {
				if (position >= mHotDataList.size()) {
					image.setImageResource(R.drawable.le_wallpaper_magicdownload_push_app_icon_def);
				}
			}
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
						getPreviewImgFromNetWork(mdata, null, 1);
					}
				}
			}
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
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
    int mNewStepIndex = 0;
    private void getNewDrawableDataFromNetWorkStep(){
        Log.i(TAG,"get draw begin        LoopLoadDrawable :"+mNewStepIndex+" datalist size="+mNewDataList.size());
        if(mNewStepIndex == mNewDataList.size()){
        }else{
            final ApplicationData mdata = mNewDataList.get(mNewStepIndex);
            final String pkgName = mdata.getPackage_name();
            
            if(!mNewIconList.containsKey(pkgName)){
                //final String iconUrl = mIconUrlList.get(curStepIndex);
                final String iconUrl = mdata.getPreviewAddr();
                if(!TextUtils.isEmpty(iconUrl)) {
                    asyncImageLoader.loadDrawable(null, iconUrl,0,0,new ImageCallback() {
                        public void imageLoaded(View imageview,Drawable imageDrawable, int position,int j) {
                        	Log.e(TAG,"0308  222222@@@esult code:"+ iconUrl+" pkgname  curindex ="+mNewStepIndex);
                        	mNewIconList.put(pkgName, new SoftReference<Drawable>(imageDrawable));
                            mNewStepIndex++;
                            startLoopLoadNewDrawable();
                        }
                    });
                }
            }
        }
    }
    private void startLoopLoadNewDrawable(){
        //Log.i(TAG," requeset startLoopLoadDrawable executeHttpGet beyond 3s then agin:");
        getNewDrawableDataFromNetWorkStep();
        if(getActivity() != null){
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    mAppsNewAdapter.addMoreContent();
                }
            });
        }
    }
    int mNewSubStepIndex = 0;
    private boolean mNewSubFlag = true;
    private boolean mNoMoreFlag = false;
    private boolean mNoMoreNewFlag = false;
    private boolean mNoMoreHotFlag = false;
    private boolean mNoMoreSpecialFlag = false;
    private void getNewSubDrawableDataFromNetWorkStep(){
        Log.i(TAG,"get draw begin        LoopLoadDrawable :"+mNewSubStepIndex+" datalist size="+mNewSubDataList.size());
        if(mNewSubStepIndex == mNewSubDataList.size()){
            mNewSubFlag = true;
            
        }else{
            if(mNewSubStepIndex >= mNewSubDataList.size()){ 
            mNewSubFlag = true;
            return;
            }
            final ApplicationData mdata = mNewSubDataList.get(mNewSubStepIndex);
            final String pkgName = mdata.getPackage_name();

            if(!mNewIconList.containsKey(pkgName) || (mNewIconList.containsKey(pkgName) && mNewIconList.get(pkgName).get() == null)){
                //final String iconUrl = mIconUrlList.get(curStepIndex);
                final String iconUrl = mdata.getPreviewAddr();
                if(!TextUtils.isEmpty(iconUrl)) {
                    mNewSubFlag =false;
                    asyncImageLoader.loadDrawable(null, iconUrl,0,0,new ImageCallback() {
                        public void imageLoaded(View imageview,Drawable imageDrawable, int position,int j) {
                        	Log.e(TAG,"0308   code:"+ iconUrl+" pkgname  curindex ="+mNewSubStepIndex+" pkgName ="+pkgName);
                            mNewIconList.put(pkgName, new SoftReference<Drawable>(imageDrawable));
                            mNewSubStepIndex++;
                            startLoopLoadNewSubDrawable();
                        }
                    });
                }
            }else{
                mNewSubFlag =false;
                mNewSubStepIndex++;
                startLoopLoadNewSubDrawable();
            }
        }
    }
    private void startLoopLoadNewSubDrawable(){
//        Log.i(TAG," requeset startLoopLoadDrawable executeHttpGet beyond 3s then agin:");
        getNewSubDrawableDataFromNetWorkStep();
        if(getActivity() != null){
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    mAppsNewAdapter.addMoreContent();
                }
            });
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
        	Log.i(TAG,"== AppsNewAdapter getview position "+position);
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
			if (mNewDataList != null) {
				if (position >= mNewDataList.size()) {
					image.setImageResource(R.drawable.le_wallpaper_magicdownload_push_app_icon_def);
				}
			}
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
		                getPreviewImgFromNetWork(mdata,null, 0);
	                }
	            }
          }
            convertView.setOnClickListener(new OnClickListener() {
                 @Override
                 public void onClick(View v) {
                	 int p = findDataInList(mdata, mNewDataList);
                	 ArrayList<ApplicationData> newDataList;
                	 if(p > mNewCount){
                		 newDataList = new ArrayList<ApplicationData>(mNewDataList.subList(p - mNewCount, p+1));
                		 p = newDataList.indexOf(mdata);
                	 }else{
                		 newDataList = mNewDataList;
                	 }
                	 mInitHandler.removeMessages(MSG_GET_NEW_PREVIEW);
                	 if(p > -1){
                		 startWallpaperDetailActivity(0, p,newDataList);
                	 }else{
                		 Toast.makeText(getActivity(), R.string.wallpaper_open_detail_error, Toast.LENGTH_SHORT).show();
                		 updownflag = true;
                	 }
                 }
             });
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
    private int findDataInList(ApplicationData data, ArrayList<ApplicationData> list){
    	if(data == null || list == null) return -1;
    	for(ApplicationData adata : list){
    		if(adata.getPackage_name().equals(data.getPackage_name())){
    			return list.indexOf(adata);
    		}
    	}
    	return -1;
    }
    private void startWallpaperDetailActivity(int type,int pos,ArrayList<ApplicationData> DataList){

        //mLeConstant.mServiceWallPaperDataList = DataList;
        Intent intent = new Intent(getActivity(), DetailClassicActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("EXTRA",pos);
        intent.putExtra("TYPEINDEX",type);
        intent.putExtra("TYPEDATA",DataList);
        startActivity(intent);

   }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        if(AllView == null){
	        AllView = inflater.inflate(R.layout.android_wallpaper_getmorefragment, container, false);
	        mWM = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
	        delayCreateView(mCurrentPageItem);
        }
        return AllView;
    }
    
    private boolean isCreateDone = true; 
    private void delayCreateView(int currentItem){
    	Log.i(TAG,"delayCreateView: begin, currentItem is "+currentItem);
    	if(!isCreateDone) return;
    	isCreateDone = false;
        view2 = mLi.inflate(R.layout.getmore_wallpaper_new, null);
        mGridNew = (GridView) view2.findViewById(R.id.getmore_wallpaper_new);
        startNewEmptyView = (View) view2.findViewById(R.id.empty);
        progressBarVNew = (ProgressBar) startNewEmptyView.findViewById(R.id.progressing);
        loading_textVNew = (TextView) startNewEmptyView.findViewById(R.id.loading_text);
        loading_refreshNew = (Button) startNewEmptyView.findViewById(R.id.refresh_button);
        loading_refreshNew.setOnClickListener(new OnClickListener() {
             public void onClick(View v) {
                 if( "other".equals( getConnectType()))
                     return;
                 getDataFromNetwork(0, 0);
             }
         });
        mGridNew.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
              /*  if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
                    //int maPosition = mGrid.getFirstVisiblePosition();
                    int mbPosition = mGridNew.getLastVisiblePosition();
                    if(mNoMoreFlag ){
                        if( mGridNew.getLastVisiblePosition() == mAppsNewAdapter.getCount() -1){
                        showOverLay();
                        return;
                        }
                    }
                    if(mAppsNewAdapter != null ){
                        if (mNewSubFlag){ 
                        if( mGridNew.getLastVisiblePosition() == mAppsNewAdapter.getCount() -1){
                            mNewStartIndex = mNewStartIndex+mNewCount;
                            mNewCount = 12;
                            getDataFromNetwork(0);
                        }
                        }else{
                            Log.d(TAG, "hold on wait for monent <F2><F2><F2><F2><F2><F2><F2>--onScrollsiblieposiont====");
                        }
                        Log.d(TAG, "aa--onScroll---lastVisiblieposiont===="+mbPosition+" getcount="+mAppsNewAdapter.getCount());
                    }
                }*/
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
						getDataFromNetwork(0, mNewStartIndex);
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
        view3 = mLi.inflate(R.layout.getmore_wallpaper_hot, null);
        mGridHot = (GridView) view3.findViewById(R.id.getmore_wallpaper_hot);
        startHotEmptyView = (View) view3.findViewById(R.id.empty);
        progressBarVHot = (ProgressBar) startHotEmptyView.findViewById(R.id.progressing);
        loading_textVHot = (TextView) startHotEmptyView.findViewById(R.id.loading_text);
        loading_refreshHot = (Button) startHotEmptyView.findViewById(R.id.refresh_button);
        loading_refreshHot.setOnClickListener(new OnClickListener() {
             public void onClick(View v) {
                 if( "other".equals( getConnectType()))
                     return;
                 getDataFromNetwork(1, 0);
             }
         });
        mGridHot.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
             /*   if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
                    //int maPosition = mGrid.getFirstVisiblePosition();
                    if(mNoMoreHotFlag ){
                        if( mGridHot.getLastVisiblePosition() == mAppsHotAdapter.getCount() -1){
                            showOverLay();
                            return;
                        }
                    }
                    //int mbPosition = mGridHot.getLastVisiblePosition();
                    if(mAppsHotAdapter != null ){
                        if( mGridHot.getLastVisiblePosition() == mAppsHotAdapter.getCount() -1){
                            mHotStartIndex = mHotStartIndex+mHotCount;
                            mHotCount = 12;
                            getDataFromNetwork(1);
                        }
                    }
                    //Log.d(TAG, "aaa---onScroll---n+ lastVisiblieposiont===="+mbPosition);
                }*/
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
							getDataFromNetwork(1, mHotStartIndex);
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
        mGridNew.setOnTouchListener(new OnTouchListener() {
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


//        InitBottomBarView(); 
//        InitViewPager(0);
        InitTopBarView(currentItem);
        InitViewPager(currentItem);
        InitOverlay();
        isCreateDone = true;
        Log.i(TAG,"delayCreateView: end, currentItem is "+currentItem);
    }
    
    private boolean updownflag = false;
    private    int ya = 0;
    private     int yb = 0;
    private void InitOverlay() {
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
    }
    private void showOverLay(){
        mOverlay.setVisibility(View.VISIBLE);
        mInitHandler.removeMessages(MSG_NO_MORE);
        mInitHandler.sendMessageDelayed(mInitHandler.obtainMessage(MSG_NO_MORE), 2000);
    }
    private void hideOverLay(){
        mOverlay.setVisibility(View.GONE);
    }

    private void InitTopBarView(int currentItem){
    	mToolBar = (RadioGroup) AllView.findViewById(R.id.toolBar);

        t1 = (RadioButton) mToolBar.findViewById(R.id.text1);
        t2 = (RadioButton) mToolBar.findViewById(R.id.text2);
        t3 = (RadioButton) mToolBar.findViewById(R.id.text3);
        t1.setText(R.string.theme_store_tab_latest);
        t2.setText(R.string.theme_store_tab_hot);
        t3.setText(R.string.theme_store_tab_category);

        if(currentItem == 1){
        	t2.setChecked(true);
            t1.setCompoundDrawablesWithIntrinsicBounds(null,null,null,nobottom);
            t2.setCompoundDrawablesWithIntrinsicBounds(null,null,null,bottom);
            t3.setCompoundDrawablesWithIntrinsicBounds(null,null,null,nobottom);
        }else{
        	t1.setChecked(true);
            t1.setCompoundDrawablesWithIntrinsicBounds(null,null,null,bottom);
            t2.setCompoundDrawablesWithIntrinsicBounds(null,null,null,nobottom);
            t3.setCompoundDrawablesWithIntrinsicBounds(null,null,null,nobottom);
        }
        
        mToolBar.setOnCheckedChangeListener(mSwitchButtonListener);
        LinearLayout top_bar = (LinearLayout)AllView.findViewById(R.id.linearLayout0);
        top_bar.setVisibility(View.VISIBLE);
    }

      /**
      * 初始化头标
    */
    private void InitBottomBarView() {
         mToolBar = (RadioGroup) AllView.findViewById(R.id.toolBar);

         t1 = (RadioButton) mToolBar.findViewById(R.id.text1);
         t2 = (RadioButton) mToolBar.findViewById(R.id.text2);
         t3 = (RadioButton) mToolBar.findViewById(R.id.text3);
         t1.setText(R.string.theme_store_tab_latest);
         t2.setText(R.string.theme_store_tab_hot);
         t3.setText(R.string.theme_store_tab_category);
         /*Typeface tf = SettingsValue.getFontStyle(getActivity());
         if (tf != null ){
             t1.setTypeface(tf);
             t2.setTypeface(tf);
             t3.setTypeface(tf);
         }*/


         t1.setChecked(true);
         t1.setCompoundDrawablesWithIntrinsicBounds(null,null,null,bottom);
         t2.setCompoundDrawablesWithIntrinsicBounds(null,null,null,nobottom);
         t3.setCompoundDrawablesWithIntrinsicBounds(null,null,null,nobottom);
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
                	Log.i(TAG,"=== mSwitchButtonListener: t1 ");
                    mPager.setCurrentItem(0);
                    t1.setCompoundDrawablesWithIntrinsicBounds(null,null,null,bottom);
                    t2.setCompoundDrawablesWithIntrinsicBounds(null,null,null,nobottom);
                    t3.setCompoundDrawablesWithIntrinsicBounds(null,null,null,nobottom);
                    break;
                case R.id.text2:
                	Log.i(TAG,"=== mSwitchButtonListener: t2 ");
                    mPager.setCurrentItem(1);
                    t2.setCompoundDrawablesWithIntrinsicBounds(null,null,null,bottom);
                    t1.setCompoundDrawablesWithIntrinsicBounds(null,null,null,nobottom);
                    t3.setCompoundDrawablesWithIntrinsicBounds(null,null,null,nobottom);
                    break;
                case R.id.text3:
                	Log.i(TAG,"=== mSwitchButtonListener: t3 ");
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
    /**
      * 初始化ViewPager
    */
    private void InitViewPager(int curItem) {
        mPager = (ViewPager) AllView.findViewById(R.id.vPager);
        listViews = new ArrayList();
//        mpAdapter = new MyPagerAdapter(listViews);
        mpAdapter = new MyPagerAdapter();
//        listViews.add(loadView);

        listViews.add(view2);
        listViews.add(view3);
        //listViews.add(view1);
        mPager.setAdapter(mpAdapter);
        mPager.setCurrentItem(curItem);
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());

    }

     
     
    /**
      * ViewPager适配器
    */
    public class MyPagerAdapter extends PagerAdapter {
/*
        public List mListViews;
 
        public MyPagerAdapter(List mListViews) {
             this.mListViews = mListViews;
        }
*/
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
            return listViews.size();
        }
 
        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(((View)listViews.get(arg1)), 0);
            return listViews.get(arg1);
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
        	Log.i(TAG,"=== onPageSelected : "+arg0);
            if(arg0 != 2){
                if(arg0 == 0){
                    if(mAppsNewAdapter == null){
                    	mAppsNewAdapter = new AppsNewAdapter();
                    	if(mCurrentNewLoadingPos < 0){
                        	getDataFromNetwork(arg0, 0);
                        }else{
                        	getDataFromNetwork(arg0, mCurrentNewLoadingPos);
                        }
                    }else{
                    	if (mLastNewOrientation != mOrientation) {
							delayCreateView(0);
							mLastNewOrientation = mOrientation;
						}else{
							startNewEmptyView.setVisibility(View.INVISIBLE);
							mGridNew.setVisibility(View.VISIBLE);
							mGridNew.setAdapter(mAppsNewAdapter);
							mAppsNewAdapter.addMoreContent();
							if(mNewDataList == null){
								getDataFromNetwork(arg0, 0);
							}else if(mCurrentNewLoadingPos < mNewDataList.size() - 1){
								getDataFromNetwork(arg0, mCurrentNewLoadingPos);
							}
						}
					}
				} else if (arg0 == 1) {
					if (mAppsHotAdapter == null) {
						mAppsHotAdapter = new AppsHotAdapter();
						if (mCurrentHotLoadingPos < 0) {
							getDataFromNetwork(arg0, 0);
						} else {
							getDataFromNetwork(arg0, mCurrentHotLoadingPos);
						}
					} else {
						if (mLastHotOrientation != mOrientation) {
							delayCreateView(1);
							mLastHotOrientation = mOrientation;
						} else {
							startHotEmptyView.setVisibility(View.INVISIBLE);
							mGridHot.setVisibility(View.VISIBLE);
							mGridHot.setAdapter(mAppsHotAdapter);
							mAppsHotAdapter.addMoreContent();
							if(mHotDataList == null){
								getDataFromNetwork(arg0, 0);
							}else if(mCurrentHotLoadingPos < mHotDataList.size() - 1){
								getDataFromNetwork(arg0, mCurrentHotLoadingPos);
							}
						}
					}
				}
			}
            switch (arg0) {
            case 0:
                Log.d(TAG, "---0---");
                //mTabHost.setCurrentTab(0);  
                t1.setChecked(true);
                break;
            case 1:
                Log.d(TAG, "---1---");
                t2.setChecked(true);
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
        if(mWM != null)
        mWM.removeView(mOverlay);
    }
}
