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

import android.view.View.OnTouchListener;

import android.graphics.drawable.Drawable;
import java.util.ArrayList;
import android.widget.BaseAdapter;

import android.widget.ImageView;

import android.content.Intent;


import java.io.ByteArrayInputStream;
import java.util.zip.GZIPInputStream;
import java.util.HashMap;

import android.widget.AbsListView;
import android.view.WindowManager;
import android.graphics.PixelFormat;

import android.widget.AbsListView.OnScrollListener;

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

import java.io.Serializable;
import java.lang.ref.SoftReference;

import android.util.DisplayMetrics;

import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.GridView;
import android.util.Log;

/**
 * Demonstrates the use of custom animations in a FragmentTransaction when
 * pushing and popping a stack.
 */
public class ShowWallpaperTypeActivity extends Activity{

    public static final String HOST_WALLPAPER = "http://launcher.lenovo.com/launcher/lezhuomian.php";

    private String TAG = "ShowWallpaperTypeActivity";
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

    private View startNewEmptyView;

    private ProgressBar progressBarVNew;
    private TextView loading_textVNew;
    private Button loading_refreshNew;

    LEJPConstant mLeConstant = LEJPConstant.getInstance();

    private ApplicationData detailData;
    private static final int MSG_GETLIST = 100;
    private static final int MSG_CHECKNETWORK = 101;
    private AsyncImageLoader asyncImageLoader;
    private int phoneWidth = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.getmore_wallpaper_category_item);
        mLi = LayoutInflater.from(this);
        asyncImageLoader = new AsyncImageLoader(this,0);
        Intent intent = getIntent();
        String title = intent.getStringExtra("TITLE");
        setTitle(title);
        mPaperType = intent.getStringExtra("TYPE");
        setupView();
        mHotIconList.clear();

        getDataFromNetwork();
        mAppsAdapter = new AppsAdapter();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        phoneWidth = metrics.widthPixels;
    }
    private void setupView(){
        startNewEmptyView = (View)findViewById(R.id.empty);
        progressBarVNew = (ProgressBar) startNewEmptyView.findViewById(R.id.progressing);
        loading_textVNew = (TextView) startNewEmptyView.findViewById(R.id.loading_text);
        loading_refreshNew = (Button) startNewEmptyView.findViewById(R.id.refresh_button);
        loading_refreshNew.setOnClickListener(new OnClickListener() {
             public void onClick(View v) {
                 if( "other".equals( getConnectType()))
                     return;
                 getDataFromNetwork();
             }
         });



        mContentGrid = (GridView)findViewById(R.id.getmore_wallpaper_item);
        mContentGrid.setOnTouchListener(new OnTouchListener() {
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
                            Log.e(TAG,"onTouch DOWN getY 111111111111===========");
                            updownflag = false;
                        }else{
                            Log.e(TAG,"onTouch DOWN getY 222222222222===========");
                            updownflag = true;
                        }
                    }
                }
                return false;
            }
        });




        mContentGrid.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
                    //int maPosition = mGrid.getFirstVisiblePosition();
                    //int mbPosition = mContentGrid.getLastVisiblePosition();
                    if(mAppsAdapter != null ){
                        if( mContentGrid.getLastVisiblePosition() == mAppsAdapter.getCount() -1){
                            mStartIndex = mStartIndex+mCount;
                            getDataFromNetwork();
                        }
                    }
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                    int totalItemCount) {
            }
        });
    }
    private boolean updownflag = false;
    private    int ya = 0;
    private     int yb = 0;


    public String getWallpaperTypeChildUrl(String typeid){
        StringBuffer url = new StringBuffer(HOST_WALLPAPER);
        url.append("?type=").append(typeid)
        .append("&s=").append(mStartIndex).append("&t=").append(mCount);
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
                default:
                	break;

            }
        }
    };
    private void showErrorView(int flag){
        try{
        progressBarVNew.setVisibility(View.GONE);
        CharSequence text = getText(R.string.le_list_empty);
        if(flag == 1){
        text = getText(R.string.le_list_empty);
        }else{
        text = getText(R.string.grid_empty_error);
        }
        loading_textVNew.setText(text.toString());
        loading_refreshNew.setVisibility(View.VISIBLE);
        }catch(Exception e){
        }
    }

    private void showGridViewContent(){
        //hideOverLay();
        if( mStartIndex == 0){
            startNewEmptyView.setVisibility(View.INVISIBLE);
            mContentGrid.setVisibility(View.VISIBLE);
            mContentGrid.setAdapter(mAppsAdapter);
        }else{
            mAppsAdapter.addMoreContent();
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
                /*HttpReturn httpReturn = */request.executeHttpGet(ShowWallpaperTypeActivity.this,getWallpaperTypeChildUrl(mPaperType),
                new AmsCallback(){
                    public void onResult(int code, byte[] bytes) {
                        Log.i(TAG,"AppInfoAction.requestAppInfo, AmsSession.init >> result code:"+ code);
                        if(code==200){
                            if(bytes!=null && bytes.length!=0){
                                response.parseFrom(bytes);
                                if( mStartIndex == 0){
                                    mDataList = response.getApplicationItemList();
                                }else{
                                    mSubDataList = response.getApplicationItemList();
                                    mDataList.addAll(mSubDataList);
                                }
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
                convertView = mLi.inflate(R.layout.lewallpaper_item, parent, false);
            }
            final TextView wallpaperview = (TextView) convertView.findViewById(R.id.textname);
            final ImageView image = (ImageView) convertView.findViewById(R.id.textpic);

            final int pos = position;
            try{
            final ApplicationData mdata = mDataList.get(position);
            wallpaperview.setText(mdata.name);
            //Log.e(TAG,"posiint ================sieze==name====="+mdata.name);
            final String pkgName = mdata.getPackage_name();
            boolean flag = false;
            if(mHotIconList.containsKey(pkgName)){
                       Drawable tmppic = mHotIconList.get(pkgName).get();
                       if(tmppic != null){
                            image.setImageDrawable(mHotIconList.get(pkgName).get());
                       }else{
                           flag = true;
                       }
            }
            if(flag || !mHotIconList.containsKey(pkgName)){
                if(updownflag){
                image.setImageResource(R.drawable.le_wallpaper_magicdownload_push_app_icon_def);
                }

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
            }catch(Exception e){
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

        mLeConstant.mServiceWallPaperDataList = DataList;
        Intent intent = new Intent(this, DetailClassicActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("EXTRA",pos);
        intent.putExtra("TYPEINDEX",type);
        startActivity(intent);

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

}
