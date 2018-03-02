/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lenovo.lejingpin;

import com.lenovo.launcher.R;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.content.Intent;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.os.AsyncTask;

import android.app.ActionBar;

import android.graphics.drawable.Drawable;
import java.util.ArrayList;
import android.widget.BaseAdapter;

import android.widget.ScrollView;
import android.widget.ListView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.content.Intent;
import android.view.View.OnTouchListener;

import java.util.Date;


import android.view.Gravity;

import android.view.WindowManager.LayoutParams;

import android.util.DisplayMetrics;
import java.util.Random;



import android.os.Parcelable;
import java.util.List;

import android.support.v4.view.PagerAdapter;


import java.io.ByteArrayInputStream;
import java.util.zip.GZIPInputStream;
import java.util.HashMap;

import android.widget.AbsListView;
import android.view.WindowManager;
import android.graphics.PixelFormat;

import android.widget.AbsListView.OnScrollListener;
import android.graphics.Color;


import android.view.LayoutInflater;
import android.view.MotionEvent;

import com.lenovo.lejingpin.AsyncImageLoader.ImageCallback;
import com.lenovo.lejingpin.AsyncImageLoader.ImagePathCallback;

import com.lenovo.lejingpin.Holder;
import com.lenovo.lejingpin.network.WallpaperResponse;
import com.lenovo.lejingpin.network.NetworkHttpRequest;
import com.lenovo.lejingpin.network.NetworkHttpRequest.AmsCallback;
import com.lenovo.lejingpin.network.NetworkHttpRequest.HttpReturn;
import com.lenovo.lejingpin.network.WallpaperResponse.ApplicationData;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.widget.Button;

import android.widget.ProgressBar;

import android.content.Context;
import android.content.res.Configuration;

import java.io.Serializable;
import java.lang.ref.SoftReference;

import android.view.Menu;
import android.view.MenuItem;

import com.lenovo.launcher2.customizer.SettingsValue;

import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.GridView;
import android.util.Log;

/**
 * Demonstrates the use of custom animations in a FragmentTransaction when
 * pushing and popping a stack.
 */
public class ShowWallpaperSpecialActivity extends Activity{

    public static final String HOST_WALLPAPER = "http://launcher.lenovo.com/wp/speciallist";

    private String TAG = "ShowSpecialActivity";
    private GridView mContentGrid;
    private int mStartIndex = 0;
    private int mCount = 18;
    private String mPaperType;
    private  ArrayList<ApplicationData> mDataList;
    private  ArrayList<ApplicationData> mSubDataList;
    private HashMap<String, SoftReference<Drawable>> mHotIconList =
            new HashMap<String, SoftReference<Drawable>>();

    private AppsAdapter mAppsAdapter;
    private LayoutInflater mLi ;

    private static final int MSG_NO_MORE = 102;

    private WindowManager mWM;
    private TextView mOverlay;

    private View startNewEmptyView;
    private ListView mListView;
    private ScrollView scrollview;
    private View loadView;
    private ImageView thumbnailView;
    private TextView textView;
    private TextView nameView;

    private boolean mNoMoreFlag = false;

    private ViewPager mPager;
    private MyPagerAdapter mpAdapter;
    private View view0;
    private int phoneWidth;


    private List listViews;

    private ProgressBar progressBarV;
    private TextView loading_textV;
    private Button loading_refresh;

    LEJPConstant mLeConstant = LEJPConstant.getInstance();
    private String thumbUrl;

    private ApplicationData detailData;
    private static final int MSG_GETLIST = 100;
    private static final int MSG_CHECKNETWORK = 101;
    private AsyncImageLoader asyncImageLoader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        final ActionBar bar = getActionBar();
        bar.setDisplayOptions(  ActionBar.DISPLAY_USE_LOGO | ActionBar.DISPLAY_SHOW_HOME |  ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);

/*
        int titleId = getResources().getSystem().getIdentifier(    
                        "action_bar_title", "id", "android");
        TextView titleView = (TextView) findViewById(titleId);   
        titleView.setTextColor(Color.WHITE);   
*/

        setContentView(R.layout.android_wallpaper_local);
        mLi = LayoutInflater.from(this);
        mWM = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        asyncImageLoader = new AsyncImageLoader(this,0);
        Intent intent = getIntent();
        String title = intent.getStringExtra("TITLE");
        setTitle(title);
        mPaperType = intent.getStringExtra("TYPE");
        thumbUrl =  intent.getStringExtra("THUMBURL");

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        phoneWidth = metrics.widthPixels;

        setupView();
        mHotIconList.clear();

        getDataFromNetwork();

    }
    private void initLoadView(){
        Log.e(TAG,"initLoadView");

        loadView = mLi.inflate(R.layout.init_loading,null);
        //loadView = mLi.inflate(R.layout.init_loading,null);
        progressBarV = (ProgressBar) loadView.findViewById(R.id.progressing);
        loading_textV = (TextView) loadView.findViewById(R.id.loading_text);
        loading_refresh = (Button) loadView.findViewById(R.id.refresh_button);
        loading_refresh.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if( "other".equals( getConnectType()))
                    return;
                getDataFromNetwork();
             }
         });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home ){
            finish();
        }
        return true;
    }

    int mOrientation;
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    	Log.e(TAG,"=====new orientation is "+newConfig.orientation+ ", mOrientation = "+mOrientation);
    	if(newConfig.orientation != mOrientation){
    		hideOverLay();
    		setupView();
    		mStartIndex = 0;
    		mCount = 18;
    		getDataFromNetwork();
    		mOrientation = newConfig.orientation;
    	}
    }
    @Override
    public void onResume(){
    	super.onResume();
    	Log.d(TAG, "---onResume---@#$%@#$^%$^ need refresh="+mLeConstant.mWallpapaerNeedRefresh+"isdelet ="+mLeConstant.mIsWallpaperDeleteFlag);
    	int currentOrientation = this.getResources().getConfiguration().orientation;
    	if(currentOrientation != mOrientation){
    		setupView();
    		getDataFromNetwork();
    		mOrientation = currentOrientation;
    	}
    }
    private void setupView(){
        view0 = mLi.inflate(R.layout.android_wallpaper_special_item, null);

        mContentGrid = (GridView)view0.findViewById(R.id.getmore_wallpaper_item);
        thumbnailView = (ImageView)view0.findViewById(R.id.special_thumbnail);

        textView = (TextView)view0.findViewById(R.id.tv_title);
        nameView = (TextView)view0.findViewById(R.id.tv_title1);
        scrollview = (ScrollView)view0.findViewById(R.id.android_scroll);
        //scrollview.requestChildFocus(thumbnailView,null);
        scrollview.fling(0);

        initLoadView();
        InitViewPager();
        final LinearLayout linearlayout = (LinearLayout)view0.findViewById(R.id.layout);
        scrollview.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    int mLastY = scrollview.getScrollY();//赋值给mLastY
                    Log.e(TAG,"mbPostion aaaa"+mLastY+"  diff height="+ (linearlayout.getHeight()-scrollview.getHeight()-mLastY));

                    if(linearlayout.getHeight()-scrollview.getHeight() - mLastY == 0){
                        //TODO
                        if(mNoMoreFlag ){
                            showOverLay();
                            return true;
                        }
                        mStartIndex = mStartIndex+mCount;
                        mCount = 12;
                        getDataFromNetwork();
                    }else{
                        Log.e(TAG,"mbPostion no =================="+mLastY+" AppsAdatercount="+ mAppsAdapter.getCount());
                    }
                }
                return false;
            }
        });
        InitOverlay();

    }
     private void InitViewPager() {
        mPager = (ViewPager) findViewById(R.id.vPager);
        listViews = new ArrayList();
        mpAdapter = new MyPagerAdapter();
        listViews.add(loadView);
        mPager.setAdapter(mpAdapter);
    }
    private void replaceView(){
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

                         

    public String getWallpaperTypeChildUrl(String typeid){
        StringBuffer url = new StringBuffer(HOST_WALLPAPER);
        url.append("?sid=").append(typeid)
        .append("&s=").append(mStartIndex).append("&t=").append(mCount)
        .append("&time=").append(new Date().getDate());


//        .append("&f=uploadtime")
//        .append("&a=desc");

        Log.e(TAG, "WallpaperinfoRequest, url=" + url.toString());
        return url.toString();
    }
    private Handler mInitHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_GETLIST:
                    showGridViewContent();
                break;
                case MSG_CHECKNETWORK:
                    if( mDataList == null){
                        showErrorView(0);
                    }
                break;
                case MSG_NO_MORE:
                    hideOverLay();
                break;
                default:
                	break;

            }
        }
    };
    private void showErrorView(int flag){
        try{
        progressBarV.setVisibility(View.GONE);
        scrollview.setVisibility(View.VISIBLE);
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

    private void showGridViewContent(){
        if( mStartIndex == 0){
            replaceView();
            if(mAppsAdapter == null){
                mAppsAdapter = new AppsAdapter();
            }
            mContentGrid.setAdapter(mAppsAdapter);
        }else{
            if(mAppsAdapter == null){
                mAppsAdapter = new AppsAdapter();
            }
            mAppsAdapter.addMoreContent();
        }
        if (SettingsValue.getCurrentMachineType(this) == -1 ){
            setSpeicalThumbnail(); 
        }
    }


    /** get the Joson data. */
    private void getDataFromNetwork(){
        new Thread() {
            public void run() {
                NetworkHttpRequest request = new NetworkHttpRequest();
                String mtypes = "wallpapers";
                final WallpaperResponse response = new WallpaperResponse();
                response.setResponseType(mtypes,phoneWidth);
                request.executeHttpGet(ShowWallpaperSpecialActivity.this,getWallpaperTypeChildUrl(mPaperType),
                new AmsCallback(){
                    public void onResult(int code, byte[] bytes) {
                        Log.i(TAG,"AppInfoAction.requestAppInfo, AmsSession.init >> result code:"+ code);
                        if(code==200){
                            if(bytes!=null && bytes.length!=0){
                                response.parseFrom(bytes);
                                if( mStartIndex == 0){
                                    mDataList = response.getApplicationItemList();
                                    Log.i(TAG,"AppInfoAction.requestAppInfo, AmsSession.init size:"+ mDataList.size());
                                }else{
                                    mSubDataList = response.getApplicationItemList();
                                    Log.i(TAG,"AppInfoAction.requestAppInfo, mStartIndex = "+mStartIndex+
                                    		", mSubDataList size is "+mSubDataList.size());
                                    if(mSubDataList.size() == 0){
                                        mNoMoreFlag = true;
                                        return;
                                    }

                                    mDataList.addAll(mSubDataList);
                                }
                              
                                //parseDataList(mDataList);
                                mInitHandler.sendEmptyMessage(MSG_GETLIST);
                            }   
                        }else{ 
                           //error
                            mInitHandler.removeMessages(MSG_CHECKNETWORK);
                            mInitHandler.sendMessageDelayed(mInitHandler.obtainMessage(MSG_CHECKNETWORK), 2000);
                        }
                    }
                });
            }
        }.start();
    }

    public void setSpeicalThumbnail(){ 

         thumbnailView.setScaleType(ImageView.ScaleType.FIT_XY);
         thumbnailView.setLayoutParams(new LinearLayout.LayoutParams(phoneWidth,200*phoneWidth/480));

        if(!TextUtils.isEmpty(thumbUrl)) {
            asyncImageLoader.loadDrawable(thumbnailView,thumbUrl,0,0,new ImageCallback() {
                public void imageLoaded(View image,final Drawable imageDrawable, int postion,int j) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            thumbnailView.setImageDrawable(imageDrawable);
                        }
                        });
                    }
                });
        }else{
            thumbnailView.setImageResource(R.drawable.le_wallpaper_magicdownload_push_app_icon_def);
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
                 if(phoneWidth <= 480 ){
                convertView = mLi.inflate(R.layout.lewallpaper_480item, parent, false);
                }else{
                convertView = mLi.inflate(R.layout.lewallpaper_item, parent, false);
					if (SettingsValue.getCurrentMachineType(getApplicationContext()) ==0) {
						int screenWidth = getWindowManager()
								.getDefaultDisplay().getWidth();
						int scale = 328 / 394;//图片宽高比
						int width;
						int height;
						if (SettingsValue.isCurrentPortraitOrientation(getApplicationContext())) {
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

            final ApplicationData mdata = mDataList.get(position);
            wallpaperview.setText(mdata.name);
            //Log.e(TAG,"posiint ================sieze======="+position);
            final String pkgName = mdata.getPackage_name();
            final int pos = position;
            boolean flag = false;
            if(mHotIconList.containsKey(pkgName) && mHotIconList.get(pkgName) != null){
                       Drawable tmppic = mHotIconList.get(pkgName).get();
                       if(tmppic != null){
                            image.setImageDrawable(mHotIconList.get(pkgName).get());
                       }else{
                           flag = true;
                       }
            }
            if(flag || !mHotIconList.containsKey(pkgName)){
            	if(!TextUtils.isEmpty(mdata.previewAddr)) {
	            	asyncImageLoader.loadDrawable(image,mdata.previewAddr,0,pos,new ImageCallback() {  
	                    public void imageLoaded(View image,Drawable imageDrawable, int postion,int j) { 
	                    	Log.d("d","postion="+postion);
	                    	mHotIconList.put(mDataList.get(postion).getPackage_name(), new SoftReference<Drawable>(imageDrawable));
	                    	Message message = handler.obtainMessage(0, new Holder((ImageView)image,imageDrawable));  
	                        handler.sendMessage(message);
	                    }
	    			});
            	}else{
	       			image.setImageResource(R.drawable.le_wallpaper_magicdownload_push_app_icon_def);
	       		}
            }
             convertView.setOnClickListener(new OnClickListener() {
                 @Override
                 public void onClick(View v) {
                	Log.d("c","startWallpaperDetailActivity");
                    startWallpaperDetailActivity(0,pos,mDataList);
                 }
             });
            return convertView;
        }
        public void addMoreContent() {
            notifyDataSetChanged();
        }
        public final int getCount() {
                    //Log.e(TAG,"mbPostion =================="+mDataList.size());
        return mDataList.size();
        }
        public final Object getItem(int position) {
            return mDataList.get(position);
        }
        public final long getItemId(int position) {
            return position;
        }
    }
    private void startWallpaperDetailActivity(int type,int pos,ArrayList<ApplicationData> DataList){


        Intent intent = new Intent(this, DetailClassicActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("EXTRA",pos);
        intent.putExtra("TYPEINDEX",type);
        intent.putExtra("TYPEDATA",DataList);
        startActivity(intent);
    }
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


    public String getConnectType() {
        ConnectivityManager mConnMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mConnMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo infoM = mConnMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if(info != null && info.isConnected()) {
            return "wifi";
        } else if(infoM != null && infoM.isConnected()) { return "mobile"; }
            return "other";
    }
    @Override
    public void onDestroy() {
        mWM.removeView(mOverlay);
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
