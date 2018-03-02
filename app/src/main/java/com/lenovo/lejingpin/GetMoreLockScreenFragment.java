
package com.lenovo.lejingpin;


import com.lenovo.launcher.R;
import com.lenovo.launcher2.commonui.LeAlertDialog;
import com.lenovo.launcher2.customizer.Reaper;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.settings.SeniorSettings;

 
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.LocalActivityManager;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.net.Uri;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
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
import android.widget.Toast;
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

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.widget.Button;

import android.widget.ProgressBar;

import java.util.Date;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayInputStream;
import java.lang.ref.SoftReference;
import java.util.zip.GZIPInputStream;
import java.util.HashMap;


import android.view.WindowManager.LayoutParams;

import android.widget.RadioGroup;
import android.widget.RadioButton;

import com.lenovo.lejingpin.AsyncImageLoader.ImageCallback;


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


 
public class GetMoreLockScreenFragment extends Fragment{
 
    public static final String HOST_WALLPAPER = "http://launcher.lenovo.com/boutique/app";

    public static final String LOCK_TYPECODE = "821";


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
    private final static String TAG = "GetMoreLockScreenFragment";
    private TabHost mTabHost;
    private HashMap<String, Drawable> mIconList =
            new HashMap<String, Drawable>();
    private HashMap<String, Drawable> mHotIconList =
            new HashMap<String, Drawable>();
    private HashMap<String, Drawable> mNewIconList =
            new HashMap<String, Drawable>();
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

    private ProgressBar progressBarVHot;
    private TextView loading_textVHot;
    private Button loading_refreshHot;

    private View AllView;
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
    //private WindowManager mWM;
    private TextView mOverlay;
    private View startEmptyView;
    private View startNewEmptyView;
    private View startHotEmptyView;
    private MyPagerAdapter mpAdapter;
    private Drawable bottom;
    private Drawable nobottom;
    private int categorytype;
    private int phoneWidth;

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
        asyncImageLoader = new AsyncImageLoader(getActivity(),2);
        Log.d(TAG, "---onCreate---new");        
        categorytype = 0;
//        getDataFromNetwork(0);
//        mAppsNewAdapter = new AppsNewAdapter();
//        bottom = getActivity().getResources().getDrawable(R.drawable.tab_selected_holo_line_short);
//        nobottom = getActivity().getResources().getDrawable(R.drawable.tab_selected_holo_line_short_no);
        
//        Reaper.processReaper(getActivity(), Reaper.REAPER_EVENT_CATEGORY_LEJINGPIN,
//				Reaper.REAPER_EVENT_NET_LOCK_NAME,
//				Reaper.REAPER_NO_LABEL_VALUE, Reaper.REAPER_NO_INT_VALUE);
         
    }
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

        url.append("?device=").append(model+android.os.Build.VERSION.RELEASE)
        .append("&f=downloadCount2")
        .append("&a=desc")
        .append("&c=").append(LOCK_TYPECODE)
        .append("&s=").append(mHotStartIndex).append("&t=").append(mCount)
        .append("&random=").append(new Random().nextInt(Integer.MAX_VALUE));
        Log.i("zdx", "hot WallpaperinfoRequest, url=" + url.toString());
        return url.toString();
    }
    public String getNewUrl() {
        StringBuffer url = new StringBuffer(HOST_WALLPAPER);
        String model = android.os.Build.MODEL;
        model = model.replace(" ","");

        url.append("?device=").append(model+android.os.Build.VERSION.RELEASE)
        .append("&f=publishDate")
        .append("&a=desc")
        .append("&c=").append(LOCK_TYPECODE)
        .append("&s=").append(mNewStartIndex).append("&t=").append(mCount)
        .append("&random=").append(new Random().nextInt(Integer.MAX_VALUE));
        Log.i("zdx", "new WallpaperinfoRequest, url=" + url.toString());
        return url.toString();
    }

/*    private void startWallpaperTypeActivity(String title,String type){
         Intent intent = new Intent(getActivity(), ShowWallpaperTypeActivity.class);
         intent.putExtra("TITLE",title);
         intent.putExtra("TYPE",type);
         startActivity(intent);
    }*/


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
        if(type == 2){
            //mGrid.setVisibility(View.VISIBLE);
            //startEmptyView.setVisibility(View.INVISIBLE);
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


    private boolean mLoadFlag = false;
    private boolean mLoadNewFlag = false;
    private boolean mLoadHotFlag = false;
    private void showGridViewContent(){
    	if(mPager == null) return;
        int curtype =  mPager.getCurrentItem();
        Log.e(TAG,"showGridViewContent curtype="+curtype);
        //hideOverLay();
/*
        if(curtype == 0){
        if( mStartIndex == 0){
            if(!mLoadFlag){
            mLoadFlag = true;
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

        }else */if(curtype == 1){//hot
        if( mHotStartIndex == 0){
            if(mHotDataList != null && mHotDataList.size() == 0){
                showErrorView(categorytype,1);
                startHotEmptyView.setVisibility(View.VISIBLE);
                return;
            }

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
          if(mNewDataList != null && mNewDataList.size() == 0){
                showErrorView(categorytype,1);
                startNewEmptyView.setVisibility(View.VISIBLE);
                return;
            }

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
                request.executeHttpGet(getActivity(),getUrl(type),
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
                                    Log.i(TAG, " get hot step that getApplicationItemList ");
                                }else{
                                    mHotSubDataList = response.getApplicationItemList();
                                    mHotDataList.addAll(mHotSubDataList);
                                    Log.i(TAG, " get hot step this add mNewSubDataList ");
                                }
                            }else if(type == 0){
                                if( mNewStartIndex == 0){
                                    mNewDataList = response.getApplicationItemList();
                                    Log.i(TAG, " get new step that getApplicationItemList ");
                                    
                                }else{
                                    mNewSubDataList = response.getApplicationItemList();
                                    mNewDataList.addAll(mNewSubDataList);
                                    Log.i(TAG, " get new step this add mNewSubDataList ");
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
        Handler hothandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
            	Holder holder = (Holder)msg.obj;
            	holder.mimg.setBackgroundDrawable(holder.mdb);
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
            //Log.e(TAG,"posiint ================sieze==name====="+mdata.name);
            final String pkgName = mdata.getPackage_name();
            
			if( mHotIconList.containsKey(pkgName)){
            //if(mdata.getthumbdrawable()!=null){
                            image.setImageDrawable(mHotIconList.get(pkgName));
/*
            	if(getActivity() != null){
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            image.setImageDrawable(mHotIconList.get(pkgName));
                        }
                    });
                }*/
            }else{
                image.setImageResource(R.drawable.lemagicdownload_push_app_icon_def);
                    final String []paths =  mdata.thumbpaths;
                    Log.e(TAG," patsh length        "+paths.length);
                    image.setImageURI(Uri.parse(paths[0]));
//                    final String picpaths[] = mHotDataList.get(pos).thumbpaths;
                                asyncImageLoader.loadDrawable(image,paths[0],0,pos,new ImageCallback() {
                                public void imageLoaded(final View image,Drawable imageDrawable, int position,int j) {
                                mHotIconList.put(mHotDataList.get(position).getPackage_name(), imageDrawable);
                                Message message = hothandler.obtainMessage(0, new Holder((ImageView)image,imageDrawable));
                                hothandler.sendMessage(message);
                                }
                        });


/*
        		if(!TextUtils.isEmpty(mdata.iconUrl)) {
        			asyncImageLoader.loadDrawable(image,mdata.iconUrl,0,0,new ImageCallback() {  
	                    public void imageLoaded(View imageview,Drawable imageDrawable, int position,int j) { 
	                    	Message message = handler.obtainMessage(0, new Holder((ImageView)imageview,imageDrawable));  
	                        handler.sendMessage(message);
	                        mHotIconList.put(pkgName, imageDrawable);
                       }
                    });
	       		}else{
	       			image.setImageResource(R.drawable.magicdownload_push_app_icon_def);
	       		}
*/
            }
            }catch(Exception e){
                Log.d(TAG,"startWallpaperDetailActivity"+e);
            }
             convertView.setOnClickListener(new OnClickListener() {
                 @Override
                 public void onClick(View v) {
                	Log.d("c","startWallpaperDetailActivity");
                    //startWallpaperDetailActivity(2,pos,mHotDataList);
                	startWallpaperDetailActivity(2,pos,mHotDataList);
                	
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
	            Log.e(TAG,"AppsNewAdapter getview posiint ================sieze==name====="+mdata.getAppName());
	            final String pkgName = mdata.getPackage_name();
	            Log.e(TAG,"AppsNewAdapter getview posiint ================sieze==pkgnamename====="+pkgName);
	            
	            if(mNewIconList.containsKey(pkgName)){
	            	image.setImageDrawable(mNewIconList.get(pkgName));
	            }else {
	                 image.setImageResource(R.drawable.lemagicdownload_push_app_icon_def);
	                    final String []paths =  mdata.thumbpaths;
	//                    final String picpaths[] = mNewDataList.get(pos).thumbpaths;
	                                asyncImageLoader.loadDrawable(image,paths[0],0,pos,new ImageCallback() {
	                                public void imageLoaded(final View image,Drawable imageDrawable, int position,int j) {
	                                mNewIconList.put(mNewDataList.get(position).getPackage_name(), imageDrawable);
	                                Message message = handler.obtainMessage(0, new Holder((ImageView)image,imageDrawable));
	                                handler.sendMessage(message);
	                                }
	                        });
	
	/*
	            	if(!TextUtils.isEmpty(mdata.iconUrl)) {
		            	asyncImageLoader.loadDrawable(image,mdata.iconUrl,0,pos,new ImageCallback() {  
		                    public void imageLoaded(View image,Drawable imageDrawable, int postion,int j) { 
		                    	Log.d("d","postion="+postion);
		                    	mNewIconList.put(pkgName, imageDrawable);
		                    	Message message = handler.obtainMessage(0, new Holder((ImageView)image,imageDrawable));  
		                        handler.sendMessage(message);
		                    }
		    			});
	            	}else{
		       			image.setImageResource(R.drawable.magicdownload_push_app_icon_def);
		       		}
	*/
	            }
            }catch(Exception e){
                Log.d(TAG,"getview cat exception "+e);
            }
             convertView.setOnClickListener(new OnClickListener() {
                 @Override
                 public void onClick(View v) {
                	Log.d("c","startWallpaperDetailActivity");
                    startWallpaperDetailActivity(2,pos,mNewDataList);
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

    private void startWallpaperDetailActivity(int type,int pos,ArrayList<AmsApplication> DataList){

        //mLeConstant.mServiceLockAmsDataList = DataList;
        Intent intent = new Intent(getActivity(), DetailClassicActivityLock.class);
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
		        AllView = inflater.inflate(R.layout.fragment_getmore_theme, container, false);		        
		        if(!Util.getInstance().isNetworkEnabled(getActivity())){
		   		    LeAlertDialog dialog = new LeAlertDialog(getActivity(), R.style.Theme_LeLauncher_Dialog_Shortcut);
		   		    dialog.setLeTitle(R.string.lejingpin_settings_title);
		   		    dialog.setLeMessage(getActivity().getText(R.string.confirm_network_open));
		   		    dialog.setLeNegativeButton(getActivity().getText(R.string.cancel_action), new DialogInterface.OnClickListener() {
		   		        public void onClick(DialogInterface dialog, int which) {
		   		        	dialog.dismiss();
		   		            Toast.makeText(getActivity(), R.string.version_update_toast, Toast.LENGTH_SHORT).show();
		   		        }
		   		    });
		
		   		    dialog.setLePositiveButton(getActivity().getText(R.string.rename_action), new DialogInterface.OnClickListener() {
		   		        public void onClick(DialogInterface dialog, int which) {
		   		        	Intent intent = new Intent();
		   		            intent.setClass(getActivity(), SeniorSettings.class);
		   		            startActivity(intent);
		   		            getActivity().finish();
		   		        }
		   		    });
		   		    dialog.show();
		   			return AllView;
			   }else{
			        getDataFromNetwork(0);
			        mAppsNewAdapter = new AppsNewAdapter();
			        bottom = getActivity().getResources().getDrawable(R.drawable.tab_selected_holo_line_short);
			        nobottom = getActivity().getResources().getDrawable(R.drawable.tab_selected_holo_line_short_no);			   
			        DisplayMetrics metrics = new DisplayMetrics();
			        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
			        phoneWidth = metrics.widthPixels;
			   
			   }
		        
		        
		        //mWM = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
		        view2 = mLi.inflate(R.layout.getmore_wallpaper_new, null);
		        mGridNew = (GridView) view2.findViewById(R.id.getmore_wallpaper_new);
		        startNewEmptyView = (View) view2.findViewById(R.id.empty);
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
		                            mHotStartIndex = mHotStartIndex+mHotCount;
		                            getDataFromNetwork(1);
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
/*    private void InitOverlay() {
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

         t2.setChecked(true);
         //t1.setCompoundDrawablesWithIntrinsicBounds(null,null,null,bottom);
         t2.setCompoundDrawablesWithIntrinsicBounds(null,null,null,bottom);
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
/*
                case R.id.text1:
                    mPager.setCurrentItem(0);
                    t1.setCompoundDrawablesWithIntrinsicBounds(null,null,null,bottom);
                    t2.setCompoundDrawablesWithIntrinsicBounds(null,null,null,nobottom);
                    t3.setCompoundDrawablesWithIntrinsicBounds(null,null,null,nobottom);
                    break;
*/
                case R.id.text2:
                    mPager.setCurrentItem(0);
                    t2.setCompoundDrawablesWithIntrinsicBounds(null,null,null,bottom);
                    t3.setCompoundDrawablesWithIntrinsicBounds(null,null,null,nobottom);
                    break;
                case R.id.text3:
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
    private void InitViewPager() {
        mPager = (ViewPager) AllView.findViewById(R.id.vPager);
        listViews = new ArrayList();
//        mpAdapter = new MyPagerAdapter(listViews);
        mpAdapter = new MyPagerAdapter();
//        listViews.add(view1);
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
        listViews.add(view2);
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
        	if(listViews.get(arg1) instanceof View){
             ((ViewPager) arg0).removeView(((View)listViews.get(arg1)));
        	}
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
        	if(listViews.get(arg1) instanceof View){
        		((ViewPager) arg0).addView(((View)listViews.get(arg1)), 0);
        	}
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
//            Animation animation = null;
            if(arg0 != 0){
/*
                if(arg0 == 1){
                    if(mAppsNewAdapter == null){
                        mAppsNewAdapter = new AppsNewAdapter();
                    }
                }
*/
                if(arg0 == 1){
                    if(mAppsHotAdapter == null){
                        mAppsHotAdapter = new AppsHotAdapter();
                    }
                Log.d(TAG, "---getdataFromenewwork- arg="+arg0+" datalist="+mHotDataList);
                    if(mHotDataList==null){
                    getDataFromNetwork(1);
                    }
                }
            }else{
                if(arg0 == 0){
                    if(mAppsNewAdapter == null){
                    	mAppsNewAdapter = new AppsNewAdapter();
                    }
                Log.d(TAG, "yymm---getdataFromenewwork- arg="+arg0+" datalist="+mNewDataList);
                	
                    if(mNewDataList==null){                    	
                    	getDataFromNetwork(0);
                    }else{
                    	
                    	if(startNewEmptyView.getVisibility()==View.VISIBLE ){
                            Log.i(TAG, "ym find the reason");
                            mGridNew.setAdapter(mAppsNewAdapter);
                    		startNewEmptyView.setVisibility(View.INVISIBLE);
                            mGridNew.setVisibility(View.VISIBLE);
                    	}
                    }
                }
            }
            
            
            switch (arg0) {
            case 0:
                Log.d(TAG, "---0---");
                //mTabHost.setCurrentTab(0);  
                t2.setChecked(true);
//                if (currIndex == 1) {
//                    animation = new TranslateAnimation(one, 0, 0, 0);
//                } else if (currIndex == 2) {
//                    animation = new TranslateAnimation(two, 0, 0, 0);
//                }
                break;
            case 1:
                Log.d(TAG, "---1---");
                t3.setChecked(true);
                //mTabHost.setCurrentTab(1);
//                if (currIndex == 0) {
//                    animation = new TranslateAnimation(offset, one, 0, 0);
//                } else if (currIndex == 2) {
//                    animation = new TranslateAnimation(two, one, 0, 0);
//                }
                break;
            case 2:
                Log.d(TAG, "---2---");
                t3.setChecked(true);
                //mTabHost.setCurrentTab(2);
//                if (currIndex == 0) {
//                    animation = new TranslateAnimation(offset, two, 0, 0);
//                } else if (currIndex == 1) {
//                    animation = new TranslateAnimation(one, two, 0, 0);
//                }
                break;
                
             default:
            	 break;
            }
            currIndex = arg0;
            //animation.setFillAfter(true);// True:图片停在动画结束位置
            //animation.setDuration(300);
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
