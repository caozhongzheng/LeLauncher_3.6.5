
package com.lenovo.lejingpin;


 
import com.lenovo.launcher.R;

import java.util.ArrayList;
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
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
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
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.AbsListView;
import android.view.WindowManager;
import android.graphics.PixelFormat;

import android.widget.LinearLayout;

import android.view.View.OnTouchListener;
import  android.view.MotionEvent;

import android.os.AsyncTask;
import android.net.Uri;

import java.util.Date;
import android.graphics.drawable.Drawable;
import android.widget.Button;

import android.widget.ProgressBar;

import java.io.ByteArrayInputStream;
import java.lang.ref.SoftReference;
import java.util.zip.GZIPInputStream;
import java.util.HashMap;


import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.view.WindowManager.LayoutParams;

import android.widget.RadioGroup;
import android.widget.RadioButton;


import android.graphics.Typeface;
import com.lenovo.launcher2.customizer.SettingsValue;



import com.lenovo.lejingpin.AsyncImageLoader.ImageCallback;

import com.lenovo.lejingpin.Holder;
import com.lenovo.lejingpin.network.WallpaperResponse;
import com.lenovo.lejingpin.network.NetworkHttpRequest;
import com.lenovo.lejingpin.network.NetworkHttpRequest.AmsCallback;
import com.lenovo.lejingpin.network.NetworkHttpRequest.HttpReturn;
import com.lenovo.lejingpin.network.WallpaperResponse.ApplicationData;

 
public class GetMoreFragment extends Fragment{
 
    public static final String HOST_WALLPAPER = "http://launcher.lenovo.com/launcher/lezhuomian.php";

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
    private int currIndex = 0;
    // 动画图片宽度
    private int bmpW;
    private final static String TAG = "GetMoreFragment";
    private TabHost mTabHost;
    private HashMap<String, Drawable> mIconList =
            new HashMap<String, Drawable>();
    private HashMap<String, SoftReference<Drawable>> mHotIconList =
            new HashMap<String, SoftReference<Drawable>>();
    private HashMap<String, SoftReference<Drawable>> mNewIconList =
            new HashMap<String, SoftReference<Drawable>>();
    int mNum;

    private View view1;
    private View view2;
    private View view3;
    private GridView mGrid;
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
    private static final int MSG_GETLIST = 100;
    private static final int MSG_CHECKNETWORK = 101;

    private LayoutInflater mLi ;

    private AppsAdapter mAppsAdapter;
    private AppsNewAdapter mAppsNewAdapter;
    private AppsHotAdapter mAppsHotAdapter;
    private WindowManager mWM;
    private TextView mOverlay;
    private View startEmptyView;
    private View startNewEmptyView;
    private View startHotEmptyView;
    private MyPagerAdapter mpAdapter;
    private Drawable bottom;
    private Drawable nobottom;
    private int categorytype;


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
        asyncImageLoader = new AsyncImageLoader(getActivity(),0);
        Log.d(TAG, "---onCreate---new");
        getDataFromNetwork(0);
        mAppsAdapter = new AppsAdapter();
        bottom = getActivity().getResources().getDrawable(R.drawable.tab_selected_holo_line_short);
        nobottom = getActivity().getResources().getDrawable(R.drawable.tab_selected_holo_line_short_no);
         
    }
    public String getUrl(int type){
        switch (type) {
            case 0:
            return getWallpaperTypeUrl();
            case 1:
            return getNewUrl();
            case 2:
            return getHotUrl();
        }
        return getRecommendUrl();
    }
    public String getWallpaperTypeUrl(){
        StringBuffer url = new StringBuffer(HOST_WALLPAPER);
        String model = android.os.Build.MODEL;
        model = model.replace(" ","");

        url.append("?gettypes=1").append("&device=").append(model);
        Log.i(TAG, "WallpaperinfoRequest, url=" + url.toString());
        return url.toString();
    }
    public String getWallpaperTypeFirstChildPicUrl(String typeid){
        StringBuffer url = new StringBuffer(HOST_WALLPAPER);
        url.append("?type=").append(typeid).append("&s=0&t=1");
        Log.i(TAG, "WallpaperinfoRequest, url=" + url.toString());
        return url.toString();
    }
    public String getRecommendUrl() {
        StringBuffer url = new StringBuffer(HOST_WALLPAPER);
        url.append("?method=a")
        .append("&s=").append(mStartIndex).append("&t=").append(mCount)
        .append("&f=id&a=asc");
        url.append("&time=").append(new Date().getTime());
        Log.i(TAG, "WallpaperinfoRequest, url=" + url.toString());
        return url.toString();
    }
    public String getHotUrl() {
        StringBuffer url = new StringBuffer(HOST_WALLPAPER);
        String model = android.os.Build.MODEL;
        model = model.replace(" ","");
        url.append("?f=downloadnumber").append("&device=").append(model)
        .append("&a=desc")
        .append("&s=").append(mHotStartIndex).append("&t=").append(mHotCount);
        Log.i(TAG, "WallpaperinfoRequest, url=" + url.toString());
        return url.toString();
    }
    public String getNewUrl() {
        String model = android.os.Build.MODEL;
        model = model.replace(" ","");
        StringBuffer url = new StringBuffer(HOST_WALLPAPER);
        url.append("?f=uploadtime").append("&device=").append(model)
        .append("&a=desc")
        .append("&s=").append(mNewStartIndex).append("&t=").append(mNewCount);
        Log.i(TAG, "WallpaperinfoRequest, url=" + url.toString());
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
                    if( categorytype == 0 && mDataList == null){
                    showErrorView(categorytype,0);
                    }
                    if( categorytype == 1 && mNewDataList == null){
                    showErrorView(categorytype,0);
                    }
                    if( categorytype == 2 && mHotDataList == null){
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
            mGrid.setVisibility(View.VISIBLE);
            startEmptyView.setVisibility(View.INVISIBLE);
        }
        else if(type == 1){
            mGridNew.setVisibility(View.VISIBLE);
            startNewEmptyView.setVisibility(View.INVISIBLE);
        }
        else if(type == 2){
            mGridHot.setVisibility(View.VISIBLE);
            startHotEmptyView.setVisibility(View.INVISIBLE);
        }
    }

    private void showErrorView(int type ,int flag){
        try{
        if(type == 0){
        progressBarV.setVisibility(View.GONE);
        CharSequence text = getText(R.string.le_list_empty);
        if(flag == 1){
        text = getText(R.string.le_list_empty);
        }else{
        text = getText(R.string.grid_empty_error);
        }
        loading_textV.setText(text.toString());
        loading_refresh.setVisibility(View.VISIBLE);
        }else if(type == 1){
        progressBarVNew.setVisibility(View.GONE);
        CharSequence text = getText(R.string.le_list_empty);
        if(flag == 1){
        text = getText(R.string.le_list_empty);
        }else{
        text = getText(R.string.grid_empty_error);
        }
        loading_textVNew.setText(text.toString());
        loading_refreshNew.setVisibility(View.VISIBLE);
        }else if(type == 2){
        progressBarVHot.setVisibility(View.GONE);
        CharSequence text = getText(R.string.le_list_empty);
        if(flag == 1){
        text = getText(R.string.le_list_empty);
        }else{
        text = getText(R.string.grid_empty_error);
        }
        loading_textVHot.setText(text.toString());
        loading_refreshHot.setVisibility(View.VISIBLE);
        }
        }catch(Exception e){
        }

    }


    private boolean mLoadFlag = false;
    private boolean mLoadNewFlag = false;
    private boolean mLoadHotFlag = false;
    private void showGridViewContent(){
        if(mPager == null) return;
        int curtype =  mPager.getCurrentItem();
        Log.e(TAG,"showGridViewContent curtype="+curtype);
        //hideOverLay();
        if(curtype == 0){
        if( mStartIndex == 0){
            if(mLoadFlag){
            //mLoadFlag = true;
            startEmptyView.setVisibility(View.VISIBLE);
            mGrid.setVisibility(View.INVISIBLE);
            }else{
            startEmptyView.setVisibility(View.INVISIBLE);
            mGrid.setVisibility(View.VISIBLE);
            }
            mGrid.setAdapter(mAppsAdapter);
            }else{
            mAppsAdapter.addMoreContent();
            }
        }else if(curtype == 2){//hot
        if( mHotStartIndex == 0){
            mGridHot.setAdapter(mAppsHotAdapter);
            if(mLoadHotFlag){
            //mLoadHotFlag = true;
            startHotEmptyView.setVisibility(View.VISIBLE);
            mGridHot.setVisibility(View.INVISIBLE);
            }else{
            startHotEmptyView.setVisibility(View.INVISIBLE);
            mGridHot.setVisibility(View.VISIBLE);
            }
        }else{
            mAppsHotAdapter.addMoreContent();
        }
        }else{
        if(mNewDataList == null) return;
        if( mNewStartIndex == 0){
            mGridNew.setAdapter(mAppsNewAdapter);
            if(mLoadNewFlag){
            //mLoadNewFlag = true;
            startNewEmptyView.setVisibility(View.VISIBLE);
            mGridNew.setVisibility(View.INVISIBLE);
            }else{
            startNewEmptyView.setVisibility(View.INVISIBLE);
            mGridNew.setVisibility(View.VISIBLE);
            }
        }else{
            mAppsNewAdapter.addMoreContent();
        }
        }
    }
    /** get the Joson data. */
    private void getDataFromNetwork(final int type){
        categorytype = type;
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
                                }else{
                                    mNewSubDataList = response.getApplicationItemList();
                                    mNewDataList.addAll(mNewSubDataList);
                                }
                                }
                                mInitHandler.sendEmptyMessage(MSG_GETLIST);
                                //Log.e(TAG,"posiint ================sieze======="+mDataList.size());
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
            	//hideInitLoadView(0);
            }
        };
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mLi.inflate(R.layout.wallpaper_types_item, parent, false);
            }
            final TextView wallpaperview = (TextView) convertView.findViewById(R.id.textname);
            final LinearLayout image = (LinearLayout) convertView.findViewById(R.id.textpic);

            final ApplicationData mdata = mDataList.get(position);
            wallpaperview.setText(mdata.name);
            final String childiconUrl = mdata.previewAddr;
            final String pkgName = String.valueOf(mdata.getAppId());
            Log.i(TAG,"0307   @@@@@@@@@2222AmsSession.init >> result code:"+ childiconUrl+" pkgname ="+pkgName);
            
            //new Thread() {
                //public void run() {
            //mohl add for concurrent.RejectExecutionException
            try{
            AsyncTask.execute(new Runnable() {
                public void run() {

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
                                    image.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.le_wallpaper_magicdownload_push_app_icon_def));
                                }
            }});
            }catch(Exception e){
            	Log.e(TAG, "AsyncTask,execute error: "+e.getMessage());
            }
            //}}.start();
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
            if (convertView == null) {
                convertView = mLi.inflate(R.layout.lewallpaper_item, parent, false);
            }
            final TextView wallpaperview = (TextView) convertView.findViewById(R.id.textname);
            final ImageView image = (ImageView) convertView.findViewById(R.id.textpic);

            final ApplicationData mdata = mHotDataList.get(position);
            wallpaperview.setText(mdata.name);
            //Log.e(TAG,"posiint ================sieze==name====="+mdata.name);
            final String pkgName = mdata.getPackage_name();
            final int pos = position;
			if( mHotIconList.containsKey(pkgName)){
            //if(mdata.getthumbdrawable()!=null){
            	//if(getActivity() != null){
                  //  getActivity().runOnUiThread(new Runnable() {
                    //    public void run() {
                       Drawable tmppic = mHotIconList.get(pkgName).get();
                       if(tmppic != null){
                            image.setImageDrawable(mHotIconList.get(pkgName).get());
                       }else{
                	Log.d(TAG,"<F2><F2><F2><F2><F2><F2><F2><F2><F2><F2><F2>startWallpaperDetailActivity");
                         //image.setImageURI(Uri.parse(mdata.previewAddr));
        			asyncImageLoader.loadDrawable(image,mdata.previewAddr,0,0,new ImageCallback() {  
	                    public void imageLoaded(View imageview,Drawable imageDrawable, int position,int j) { 
	                    	Message message = handler.obtainMessage(0, new Holder((ImageView)imageview,imageDrawable));  
	                        handler.sendMessage(message);
	                        mHotIconList.put(pkgName, new SoftReference<Drawable>(imageDrawable));
                       }
                    });
                       }
                      //  }
                    //});
             //   }
            }else{
        		if(!TextUtils.isEmpty(mdata.previewAddr)) {
                         //image.setImageURI(Uri.parse(mdata.previewAddr));
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
             convertView.setOnClickListener(new OnClickListener() {
                 @Override
                 public void onClick(View v) {
                	Log.d("c","startWallpaperDetailActivity");
                    startWallpaperDetailActivity(0,pos,mHotDataList);
                 }
             });
            return convertView;
        }
        public void addMoreContent() {
            notifyDataSetChanged();
        }
        public final int getCount() {
            //Log.e(TAG,"posiint ================sieze======="+mDataList.size());
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
            }
        };
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mLi.inflate(R.layout.lewallpaper_item, parent, false);
            }
            final TextView wallpaperview = (TextView) convertView.findViewById(R.id.textname);
            final ImageView image = (ImageView) convertView.findViewById(R.id.textpic);

            final ApplicationData mdata = mNewDataList.get(position);
            wallpaperview.setText(mdata.name);
            //Log.e(TAG,"posiint ================sieze==name====="+mdata.name);
            final String pkgName = mdata.getPackage_name();
            final int pos = position;
            if(mNewIconList.containsKey(pkgName)){
                       Drawable tmppic = mNewIconList.get(pkgName).get();
                if(tmppic != null){
            	image.setImageDrawable(mNewIconList.get(pkgName).get());
                }else{
	            	asyncImageLoader.loadDrawable(image,mdata.previewAddr,0,pos,new ImageCallback() {  
	                    public void imageLoaded(View image,Drawable imageDrawable, int postion,int j) { 
	                    	Log.d("d","postion="+postion);
	                    	mNewIconList.put(pkgName, new SoftReference<Drawable>(imageDrawable));
	                    	Message message = handler.obtainMessage(0, new Holder((ImageView)image,imageDrawable));  
	                        handler.sendMessage(message);
	                    }
	    			});
                }
            }else {
            	if(!TextUtils.isEmpty(mdata.previewAddr)) {
	            	asyncImageLoader.loadDrawable(image,mdata.previewAddr,0,pos,new ImageCallback() {  
	                    public void imageLoaded(View image,Drawable imageDrawable, int postion,int j) { 
	                    	Log.d("d","postion="+postion);
	                    	mNewIconList.put(pkgName, new SoftReference<Drawable>(imageDrawable));
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
                    startWallpaperDetailActivity(0,pos,mNewDataList);
                 }
             });
            return convertView;
        }
        public void addMoreContent() {
            notifyDataSetChanged();
        }
        public final int getCount() {
            //Log.e(TAG,"posiint ================sieze======="+mNewDataList.size());
            return (mNewDataList == null) ? 0 : mNewDataList.size();
        }
        public final Object getItem(int position) {
            return mNewDataList.get(position);
        }
        public final long getItemId(int position) {
            return position;
        }
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
    private int mLastY=0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        if(AllView == null){
        AllView = inflater.inflate(R.layout.fragment_getmore, container, false);
        mWM = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);

         
        view1 = mLi.inflate(R.layout.getmore_wallpaper_category, null);
        mGrid = (GridView) view1.findViewById(R.id.getmore_wallpaper_category);
        startEmptyView = (View) view1.findViewById(R.id.empty);
        progressBarV = (ProgressBar) startEmptyView.findViewById(R.id.progressing);
        loading_textV = (TextView) startEmptyView.findViewById(R.id.loading_text);
        loading_refresh = (Button) startEmptyView.findViewById(R.id.refresh_button);
        loading_refresh.setOnClickListener(new OnClickListener() {
             public void onClick(View v) {
                 if( "other".equals( getConnectType()))
                     return;
                 getDataFromNetwork(0);
             }
         });
      /*  final MyScrollView categoryScrollView  = (MyScrollView) view1.findViewById(R.id.scrollview_getmore_category);
        categoryScrollView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_UP){
                        if(mLastY == categoryScrollView.getScrollY()){
                        	Log.d("LeGrid", "aaa---onScroll---firstVisibliepositon====naaaaaaaaaaaaaaaaaaaab%%%%%%%%%%%%%%%%");
                        }else{
                            mLastY = categoryScrollView.getScrollY();
                        }
                    }
                    return false;
                }
            });*/
        mGrid.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
                    //int maPosition = mGrid.getFirstVisiblePosition();
/*
                    int mbPosition = mGrid.getLastVisiblePosition();
                    if(mAppsAdapter != null ){
                        if( mGrid.getLastVisiblePosition() == mAppsAdapter.getCount() -1){
                            //to be modify
                            mStartIndex = mStartIndex+mCount;
                            getDataFromNetwork(0);
                            //showOverLay();
                        }
                    }
*/
                    //Log.d(TAG, "aaa---onScroll---firstVisibliepositon====isiblieposiont===="+mbPosition);
                }
                /*
                if(scrollState == OnScrollListener.SCROLL_STATE_FLING){
                    int maPosition = mGrid.getFirstVisiblePosition();
                    int mbPosition = mGrid.getLastVisiblePosition();
                    Log.d(TAG, "bbb---onScroll---firstVisibliepositon===="+maPosition+" lastVisiblieposiont===="+mbPosition);
                }
                if(scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    int maPosition = mGrid.getFirstVisiblePosition();
                    int mbPosition = mGrid.getLastVisiblePosition();
                    Log.d(TAG, "ccc---onScroll---firstVisibliepositon===="+maPosition+" lastVisiblieposiont===="+mbPosition);
                }*/
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                    int totalItemCount) {
                //Log.d(TAG, "ddd-recommend---firstVisibliep==="+firstVisibleItem+" Visibliconu=="+visibleItemCount+" totalitemcount--------"+totalItemCount);
            }
        });
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
                 getDataFromNetwork(1);
             }
         });
     /*   final MyScrollView newScrollView  = (MyScrollView) view2.findViewById(R.id.scrollview_getmore_new);
        newScrollView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_UP){
                        if(mLastY == newScrollView.getScrollY()){
                        	Log.d("LeGrid", "aaa---onScroll---firstVisibliepositon====naaaaaaaaaaaaaaaaaaaab%%%%%%%%%%%%%%%%");
                        }else{
                            mLastY = newScrollView.getScrollY();
                        }
                    }
                    return false;
                }
            });*/
        mGridNew.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
                    //int maPosition = mGrid.getFirstVisiblePosition();
                    //int mbPosition = mGridNew.getLastVisiblePosition();
                    if(mAppsNewAdapter != null ){
                        if( mGridNew.getLastVisiblePosition() == mAppsNewAdapter.getCount() -1){
                            mNewStartIndex = mNewStartIndex+mNewCount;
                            getDataFromNetwork(1);
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
                 getDataFromNetwork(2);
             }
         });

      /*  final MyScrollView hotScrollView  = (MyScrollView) view3.findViewById(R.id.scrollview_getmore_hot);
        hotScrollView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_UP){
                        if(mLastY == hotScrollView.getScrollY()){
                        	Log.d("LeGrid", "aaa---onScroll---firstVisibliepositon====naaaaaaaaaaaaaaaaaaaab%%%%%%%%%%%%%%%%");
                        }else{
                            mLastY = hotScrollView.getScrollY();
                        }
                    }
                    return false;
                }
            }); */
        mGridHot.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
                    //int maPosition = mGrid.getFirstVisiblePosition();
                    //int mbPosition = mGridHot.getLastVisiblePosition();
                    if(mAppsHotAdapter != null ){
                        if( mGridHot.getLastVisiblePosition() == mAppsHotAdapter.getCount() -1){
                            mHotStartIndex = mHotStartIndex+mHotCount;
                            getDataFromNetwork(2);
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
        InitViewPager();
        //InitOverlay();
        }
        return AllView;
    }
    private void InitOverlay() {
        mOverlay = (TextView) mLi.inflate(R.layout.overlay_getmore_text, null);
        mOverlay.setVisibility(View.INVISIBLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT);
        mWM.addView(mOverlay, lp);
    }
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

*/
    private void showOverLay(){
        mOverlay.setVisibility(View.VISIBLE);
    }
    private void hideOverLay(){
        mOverlay.setVisibility(View.GONE);
    }


      /**
      * 初始化头标
    */
    private void InitBottomBarView() {
         mToolBar = (RadioGroup) AllView.findViewById(R.id.toolBar);

         t1 = (RadioButton) mToolBar.findViewById(R.id.text1);
         t2 = (RadioButton) mToolBar.findViewById(R.id.text2);
         t3 = (RadioButton) mToolBar.findViewById(R.id.text3);
         t1.setText(R.string.theme_store_tab_category);
         t2.setText(R.string.theme_store_tab_latest);
         t3.setText(R.string.theme_store_tab_hot);
         Typeface tf = SettingsValue.getFontStyle(getActivity());
         if (tf != null ){
             t1.setTypeface(tf);
             t2.setTypeface(tf);
             t3.setTypeface(tf);
         }


         t1.setChecked(true);
         t1.setCompoundDrawablesWithIntrinsicBounds(null,null,null,bottom);
         t2.setCompoundDrawablesWithIntrinsicBounds(null,null,null,nobottom);
         t3.setCompoundDrawablesWithIntrinsicBounds(null,null,null,nobottom);
         mToolBar.setOnCheckedChangeListener(mSwitchButtonListener);
    }
    /**
     * 头标点击监听
    */
    private RadioGroup.OnCheckedChangeListener mSwitchButtonListener = new RadioGroup.OnCheckedChangeListener() {
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.text1:
                    mPager.setCurrentItem(0);
                    t1.setCompoundDrawablesWithIntrinsicBounds(null,null,null,bottom);
                    t2.setCompoundDrawablesWithIntrinsicBounds(null,null,null,nobottom);
                    t3.setCompoundDrawablesWithIntrinsicBounds(null,null,null,nobottom);
                    break;
                case R.id.text2:
                    mPager.setCurrentItem(1);
                    t2.setCompoundDrawablesWithIntrinsicBounds(null,null,null,bottom);
                    t1.setCompoundDrawablesWithIntrinsicBounds(null,null,null,nobottom);
                    t3.setCompoundDrawablesWithIntrinsicBounds(null,null,null,nobottom);
                    break;
                case R.id.text3:
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
    private void InitViewPager() {
        mPager = (ViewPager) AllView.findViewById(R.id.vPager);
        listViews = new ArrayList();
//        mpAdapter = new MyPagerAdapter(listViews);
        mpAdapter = new MyPagerAdapter();
        listViews.add(view1);
//        listViews.add(loadView);

        listViews.add(view2);
        listViews.add(view3);
        mPager.setAdapter(mpAdapter);
        mPager.setCurrentItem(0);
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }
    private void replaceView(){
        mpAdapter.destroyItem(mPager, 0,null);
        //listViews.remove(loadView);
        listViews.add(view1);
        mpAdapter.instantiateItem(mPager, 0);
        //mpAdapter.notifyDataSetChanged();
        mPager.setAdapter(mpAdapter);
        mPager.setCurrentItem(0);
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
        //cursor.setImageMatrix(matrix);// 设置动画初始位置
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
            Animation animation = null;
            if(arg0 != 0){
                if(arg0 == 1){
                    if(mAppsNewAdapter == null){
                        mAppsNewAdapter = new AppsNewAdapter();
                    }
                }
                if(arg0 == 2){
                    if(mAppsHotAdapter == null){
                        mAppsHotAdapter = new AppsHotAdapter();
                    }
                }
                getDataFromNetwork(arg0);
            }
            switch (arg0) {
            case 0:
                Log.d(TAG, "---0---");
                //mTabHost.setCurrentTab(0);  
                t1.setChecked(true);
                if (currIndex == 1) {
                    animation = new TranslateAnimation(one, 0, 0, 0);
                } else if (currIndex == 2) {
                    animation = new TranslateAnimation(two, 0, 0, 0);
                }
                break;
            case 1:
                Log.d(TAG, "---1---");
                t2.setChecked(true);
                //mTabHost.setCurrentTab(1);
                if (currIndex == 0) {
                    animation = new TranslateAnimation(offset, one, 0, 0);
                } else if (currIndex == 2) {
                    animation = new TranslateAnimation(two, one, 0, 0);
                }
                break;
            case 2:
                Log.d(TAG, "---2---");
                t3.setChecked(true);
                //mTabHost.setCurrentTab(2);
                if (currIndex == 0) {
                    animation = new TranslateAnimation(offset, two, 0, 0);
                } else if (currIndex == 1) {
                    animation = new TranslateAnimation(one, two, 0, 0);
                }
                break;
            default:
            	break;
            }
            currIndex = arg0;
            if(animation!=null){
				animation.setFillAfter(true);// True:图片停在动画结束位置
				animation.setDuration(300);
            }
            //cursor.startAnimation(animation);
            Log.e(TAG," mPager.onpageSelected index========"+currIndex);
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
        //mWM.removeView(mOverlay);
    }
}
