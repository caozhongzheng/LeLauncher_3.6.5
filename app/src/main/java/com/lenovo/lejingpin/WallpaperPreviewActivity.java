package com.lenovo.lejingpin;

import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;

import android.widget.ImageView.ScaleType;

import android.widget.Gallery;
import com.lenovo.launcher.R;

import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;


import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import android.app.Activity;
import android.app.Application;
import android.view.Window;

import android.util.Log;

import android.graphics.drawable.Drawable;

import android.content.Context;
import android.content.Intent;
 
import android.os.Bundle;
import android.graphics.Rect;

import android.view.View;

import android.widget.Button;

import com.lenovo.lejingpin.network.WallpaperResponse.ApplicationData;

import java.io.File;
import android.widget.HorizontalScrollView;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import android.util.DisplayMetrics;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.view.View.OnClickListener;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;

import android.app.WallpaperManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.lenovo.launcher2.customizer.Reaper;
import com.lenovo.launcher2.customizer.SettingsValue;


import android.view.WindowManager;


public class WallpaperPreviewActivity extends Activity {
    private static String TAG = "WallpaperPreviewActivity";
    public static final String LELAUNCHER_CLASS_NAME = "com.lenovo.launcher.components.XAllAppFace.XLauncher";

    private LayoutInflater mInflater;
    //引导图片资源
    private Gallery galleryView;
    private Button mdetail_classic_install;
    private WindowManager mWM;
    private ImageView detail_classic_item_image;
    private ImageView detail_classic_item_remark;

    private LEJPConstant mLeConstant = LEJPConstant.getInstance();

    Handler mHandler = new Handler();

    private View mOverlay;
    private View mOverlayLeft;
    private View mOverlayRight;
    private View mCustomView;
    private ImageView previewImage;
    private int type; 
    private int resid; 
    private String filepath; 
    private DisplayMetrics metrics;
    private int phoneHeight;
    private int phoneWidth;
    private int mcurpos;
    private ApplicationData mcurdata; 
    private int mwallpaperSize;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.android_wallpaper_preview);
        
        Intent intent = this.getIntent();

        type = intent.getIntExtra("TYPEINDEX",0);
        filepath = intent.getStringExtra("EXTRAPATH");
        resid = intent.getIntExtra("EXTRARESID",-1);
        mcurpos = intent.getIntExtra("CURINDEX",-1);
        restoreState(savedInstanceState); 
        try{
        mwallpaperSize = mLeConstant.mServiceLocalWallPaperDataList.size()-1;
        }catch(Exception e){
            return;
        }

        previewImage = (ImageView) findViewById(R.id.wv1);
//        HorizontalScrollView hori = (HorizontalScrollView)findViewById(R.id.horiscrollview);
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        phoneHeight = metrics.heightPixels;
        phoneWidth = metrics.widthPixels;
        mInflater = LayoutInflater.from(this);
        mHandler.postDelayed(new Runnable() {
            public void run() {
                InitOverlay();
            }},0);

        setBigPreview(type);
        if(!SettingsValue.isRotationEnabled(WallpaperPreviewActivity.this)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("FIRSTLOAD",mcurpos);
        Log.e("yumin0829"," onSaveInstance 00000000  onCLick "+mcurpos);
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    	metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        phoneHeight = metrics.heightPixels;
        phoneWidth = metrics.widthPixels;
    	setBigPreview(type);
    }
    
    private void restoreState(Bundle savedState) {
        if(savedState == null) return;
        mcurpos = savedState.getInt("FIRSTLOAD",-1);
		// yangbin5 start, fix bug ACE-399
		List<ApplicationData> list = mLeConstant.mServiceLocalWallPaperDataList;
		if (list == null) {
			finish();
			return;
		}
		mcurdata = list.get(mcurpos);
		// yangbin5 end
        if(  mcurdata.getIsNative()){
            filepath = mcurdata.getPackage_name();
            resid = mcurdata.getpreviewdrawableresid();
            type = 0;
        }else{
            filepath = mcurdata.getUrl();
            type = 1;
        }
        Log.e("yumin0829"," restoreState 00000000  onCLick "+mcurpos);
    }
    private void applyNextWallpaper(){
        int size = mLeConstant.mServiceLocalWallPaperDataList.size()-1;
        Log.e("yumina"," next 00000000  onCLick "+size+" curpos ======"+mcurpos);
        if(mcurpos >= size  || mcurpos+1== size){
        return;
        }else{
        mcurpos = mcurpos+1;
        Log.e("yumina"," next 22222222 onCLick "+size+" curpos ======"+mcurpos);
        if(mcurpos == size -1 ){
            mOverlayRight.setVisibility(View.INVISIBLE); 
        }
        mcurdata = mLeConstant.mServiceLocalWallPaperDataList.get(mcurpos); 
        if(mcurdata.getIsDynamic() == 1){
            applyNextWallpaper();
        }else{
        if(  mcurdata.getIsNative()){
            filepath = mcurdata.getPackage_name();
            resid = mcurdata.getpreviewdrawableresid();
            type = 0;
        }else{
            filepath = mcurdata.getUrl();
            type = 1;
        }
        setBigPreview(type);
            if(mOverlayLeft.getVisibility() == View.INVISIBLE){
                mOverlayLeft.setVisibility(View.VISIBLE); 
            }
        }
        }
    }

    private void applyPreWallpaper(){
        int size = mLeConstant.mServiceLocalWallPaperDataList.size()-1;
        Log.e("yumina"," next <F2><F2><F2><F2><F2> onCLick "+size+" curpos ======"+mcurpos);
        if(mcurpos == 0 )return;
        mcurpos = mcurpos-1;
        if(mcurpos == 0 ){
            mOverlayLeft.setVisibility(View.INVISIBLE); 
        }
        mcurdata = mLeConstant.mServiceLocalWallPaperDataList.get(mcurpos); 
        if(mcurdata.getIsDynamic() == 1){
            applyPreWallpaper();
        }else{
        if(  mcurdata.getIsNative()){
            filepath = mcurdata.getPackage_name();
            resid = mcurdata.getpreviewdrawableresid();
            type = 0;
        }else{
            filepath = mcurdata.getUrl();
            type = 1;
        }
        setBigPreview(type);
            if(mOverlayRight.getVisibility() == View.INVISIBLE){
                mOverlayRight.setVisibility(View.VISIBLE); 
            }
        }
    }
    public void applyWallpaper(){
        Log.e("yumina"," <F2><F2><F2><F2><F2> onCLick ");
        SharedPreferences sp = getSharedPreferences("CURRENT", 0);
        SharedPreferences.Editor editor = sp.edit();
        addExcludeSettingKey(sp);
        if(type == 0){
            Toast.makeText(this,R.string.theme_applying_wallpaper_message,Toast.LENGTH_SHORT).show();
            try {
                WallpaperManager wpm = (WallpaperManager) this.getSystemService(Context.WALLPAPER_SERVICE);
                wpm.setResource(resid);
            } catch (IOException e) {
                Toast.makeText(this,R.string.wallpaper_insall_fail_toast, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to set wallpaper: " + e);
                return;
            }
        }else{
            Toast.makeText(this, R.string.theme_applying_wallpaper_message, Toast.LENGTH_SHORT).show();
            File inFile = null;
            try{
            inFile = new File(filepath);
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
        editor.putString("current_wallpaper", filepath).commit();
        mLeConstant.mIsClickOtherFlag = false;

        Reaper.processReaper(this, "LeJingpin", "LocalPaperApply", filepath, Reaper.REAPER_NO_INT_VALUE);
        Toast.makeText(this,R.string.wallpaper_insall_success, Toast.LENGTH_SHORT).show();
        handleGetMessage(MSG_FINISH);
    }
    private void setBigPreview(int index){
        if(index == 0){//native
            Drawable d = getResources().getDrawable(resid);
            if(d != null){
	            Rect rect = d.getBounds();
	            int h = rect.height();
	            int w = rect.width();
	            int bh =  d.getIntrinsicHeight();
	            int bw =	d.getIntrinsicWidth();
	            Log.e("yumina"," <F2><F2><F2><F2><F2> sr"+phoneHeight+"drawwidht="+bw+" drawheight="+bh+"w=="+w+"h=="+h);
	
	            int wallh = phoneHeight;
	            int wallw = phoneHeight*bw/bh;
	            previewImage.setImageDrawable(d);
	            previewImage.setScaleType( ImageView.ScaleType.CENTER_CROP);
	            if(SettingsValue.isCurrentPortraitOrientation(getApplicationContext())){
	            	previewImage.setLayoutParams(new LinearLayout.LayoutParams(wallw,wallh));
	            	return;
	            }
	            previewImage.setLayoutParams(new LinearLayout.LayoutParams(wallw*9/5,wallh));
            }
        }
        else if(index == 1){//download
            Bitmap b = BitmapFactory.decodeFile(filepath);
            if(b != null){
	            int bw = b.getWidth();
	            int bh = b.getHeight();
	            int wallw = 0;
	            int wallh = 0;
	            wallh = phoneHeight;
	            wallw = phoneHeight*bw/bh;
	            Log.e("yumina","phoneHeight="+phoneHeight+" phoneWidth="+phoneWidth+"bh="+bh+"bw="+bw+"wallh=="+wallh+" wallw="+wallw);
	            previewImage.setImageBitmap(b);
	            previewImage.setScaleType( ImageView.ScaleType.CENTER_CROP);
	            if(SettingsValue.isCurrentPortraitOrientation(getApplicationContext())){
	            	previewImage.setLayoutParams(new LinearLayout.LayoutParams(wallw,wallh));
	            	return;
	            }
	            previewImage.setLayoutParams(new LinearLayout.LayoutParams(wallw*9/5,wallh));
            }
        }
    }
    private void InitOverlay() {
        mWM = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mOverlay = mInflater.inflate(R.layout.overlay_setwallpaper, null);
        mOverlayLeft = mInflater.inflate(R.layout.overlay_left, null);
        mOverlayRight = mInflater.inflate(R.layout.overlay_right, null);
        mOverlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {//local objects
                applyWallpaper();
            }
        });
        mOverlayLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {//local objects
                applyPreWallpaper();
            }
        });
        mOverlayRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {//local objects
                applyNextWallpaper();
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
//            240,
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);
        lp.gravity = Gravity.BOTTOM | Gravity.CENTER;
        mWM.addView(mOverlay, lp);

        WindowManager.LayoutParams lpLeft = new WindowManager.LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);
        //lpLeft.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        lpLeft.gravity = Gravity.LEFT | Gravity.BOTTOM;
        mWM.addView(mOverlayLeft, lpLeft);

        WindowManager.LayoutParams lpRight = new WindowManager.LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);
        //lpRight.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        lpRight.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        mWM.addView(mOverlayRight, lpRight);
        //handleGetMessage(MSG_SHOW);
        if(mcurpos == mwallpaperSize -1 ){
            mOverlayRight.setVisibility(View.INVISIBLE); 
        }
        if(mcurpos == 0 ){
            mOverlayLeft.setVisibility(View.INVISIBLE); 
        }
        boolean flag = false;
        for(int i= mcurpos+1;i<mwallpaperSize;i++){
            mcurdata = mLeConstant.mServiceLocalWallPaperDataList.get(i); 
            if(mcurdata != null && mcurdata.getIsDynamic() == 0){
                flag = true;
                break;
            }
        }
        if(!flag){
            mOverlayRight.setVisibility(View.INVISIBLE); 
        }

    }
    private static final int MSG_SHOW= 100;
    private static final int MSG_FINISH= 200;


    private Handler mInitHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_FINISH:
                Intent intent = new Intent();
                intent.setClassName(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF,LELAUNCHER_CLASS_NAME);
                startActivity(intent);
                break;
            case MSG_SHOW:
                mOverlayLeft.setVisibility(View.VISIBLE);
                mOverlayRight.setVisibility(View.VISIBLE);
                break;
            default:
            	break;
            }
        }
    };

   private final static String EXCLUDED_SETTING_KEY = "exclude_from_backup";
   private  void addExcludeSettingKey(SharedPreferences sp){
        if(!sp.contains(EXCLUDED_SETTING_KEY)){
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean(EXCLUDED_SETTING_KEY, true).commit();
        }
    }

    private void handleGetMessage(int msgID ) {
        mInitHandler.removeMessages(msgID);
        mInitHandler.sendEmptyMessageDelayed(msgID,1000);
    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
        	Intent mIntent = new Intent();  
            mIntent.putExtra("currentpos", mcurpos);  
            Log.i("XXXX", "ondestroy: setResult "+ mIntent);
            this.setResult(Activity.RESULT_OK, mIntent);  
            Log.i("XXXX", "ondestroy: setResult over");
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onDestroy() {
        try{
        if(mWM != null){
        mWM.removeView(mOverlay);
        mWM.removeView(mOverlayLeft);
        mWM.removeView(mOverlayRight);
        }
        }catch(Exception e){
            Log.e(TAG,"onDestroy error for mWM overlay");
        }
        super.onDestroy();
    }
    
}
