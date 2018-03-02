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
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.Window;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.service.wallpaper.WallpaperService;
import android.app.WallpaperManager;
import android.app.WallpaperInfo;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.SpinnerAdapter;
import android.widget.ArrayAdapter;

import android.view.MotionEvent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;




import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.app.TabActivity;
import android.widget.Toast;

import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.lenovo.lejingpin.appsmgr.content.UpgradeUtil;
import com.lenovo.lejingpin.share.download.DownloadConstant;
import com.lenovo.lejingpin.share.download.DownloadExpandableActivity;
import com.lenovo.lejingpin.share.download.LDownloadManager;
import com.lenovo.lejingpin.share.util.Utilities;
import com.lenovo.lejingpin.hw.ui.HwLocalAppFragment;
import com.lenovo.lejingpin.hw.ui.HwPushAppFragment;
import com.lenovo.lejingpin.settings.LejingpingSettings;

import android.util.Log;
import android.os.SystemClock;
import android.graphics.Typeface;

import android.view.MenuInflater;

import android.os.Handler;

import android.app.ActionBar;
import android.view.ViewGroup.LayoutParams;

import android.graphics.Color;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import com.lenovo.launcher2.commonui.LeAlertDialog;
import com.lenovo.launcher2.customizer.SettingsValue;

import com.lenovo.launcher2.customizer.GlobalDefine;
import android.app.ActionBar.OnNavigationListener;

import com.lenovo.launcher2.customizer.Reaper;
import com.lenovo.launcher2.settings.SeniorSettings;
/**
 * Demonstrates the use of custom animations in a FragmentTransaction when
 * pushing and popping a stack.
 */
public class ClassicFragmentActivity extends Activity {

    private LEJPConstant mLeConstant = LEJPConstant.getInstance();

    private String TAG = "FragmentActivity";
    private RadioButton t1,t2,t3,t4;
    private RadioGroup mToolBar;
    private ViewPager mViewPager;
    private Fragment recommendFragment;
    private Fragment networkRecommendFragment;
    private Fragment themeFragment;
    private Fragment wallpaperFragment;
    private Fragment networkwallpaperFragment;
    private Fragment networkthemeFragment;
    private Fragment lockscreenFragment;
    private Handler mInitHandler = new Handler(); 
    private int mcurrpos = 0;
    private View loadView;
    private boolean regflag = false;
    private boolean isWWVersion = false;
    private boolean isTDVersion = false;
    private Context mContext;
    private View mCustomView;
    private TextView  mAction_bar_switch;
    private DownloadReceiver mDownloadReceiver;
    TextView mDownloadCountView;
    private String entryTitle = null;
    
    //add by hwf
    private Fragment networklockscreenFragment;
    
    //yangmao add
    

    View showPopview;
    private int horizontalOffsetSecond ;
    
    ListAdapter adapter;
    ListPopupWindow mListPopupWindow;
	int items_title[] = {R.string.tab_download,R.string.tab_settings};
	int items_img[] = {R.drawable.ic_download_manage_title,R.drawable.lejingpin_actionbar_setting};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        isWWVersion = GlobalDefine.getVerisonWWConfiguration(this);
        isTDVersion = GlobalDefine.getVerisonCMCCTDConfiguration(this);
        Log.e(TAG,"WWVersion: "+isWWVersion+", TDVersion:"+isTDVersion);
        
        //xujing modified
//        Utilities.mIsMainActivityActive = true;
        Utilities.setDownloadActive(true, this.getClass().getName());

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        mContext = ClassicFragmentActivity.this;
        LEJPConstant.getInstance().mLeMainActivityonResumeFlag = true;
        
      	setContentView(R.layout.activity_classic);
       
        final ActionBar bar = getActionBar();
        bar.setDisplayOptions(  ActionBar.DISPLAY_USE_LOGO | ActionBar.DISPLAY_SHOW_HOME |  ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
/*
        int titleId = getResources().getSystem().getIdentifier(  
                        "action_bar_title", "id", "android");  
        TextView titleView = (TextView) findViewById(titleId);  
        titleView.setTextColor(Color.WHITE);  
  */          
        mListPopupWindow = new ListPopupWindow(this);           
        adapter = new AppAdapter(this);
        mListPopupWindow.setAdapter(adapter); 
        
        Intent intent = this.getIntent();
        if(intent != null ){
            boolean flag = intent.hasExtra("EXTRA");
    	    Log.i("yumina","onSaveInstanceState extra="+flag);
            if(!flag){  
                mcurrpos = 1; 
                if(t2 != null){
                	t2.setChecked(true);}
            }else{
            mcurrpos = intent.getIntExtra("EXTRA",0);
    	    Log.i("yumina","onSaveInstanceState extra="+mcurrpos);
            if(mcurrpos == 0){
            	setTitle(R.string.network_app);
            }else if(mcurrpos == -1){
            	 setTitle(R.string.local_app);
            }else if(mcurrpos == 1 ){   //local theme
                setTitle(R.string.local_theme);
            }else if(mcurrpos == 11 ){  // network theme
                setTitle(R.string.network_theme);
            }else if(mcurrpos == 2 ){   //local wallpaper
                setTitle(R.string.local_wallpaper);
            }else if(mcurrpos == 12 ){  //network wallpaper
                setTitle(R.string.network_wallpaper);
            }/*else if(mcurrpos == 3){
            }*/
            //add by hwf
            else if(mcurrpos == 3){
            	//local lockscreen
            	setTitle(R.string.local_lock);
            }else if(mcurrpos == 13){
            	//net lockscreen
            	setTitle(R.string.network_lock);
            }
            entryTitle = getTitle().toString();
            }
        }
        curLiveWallPkgName = getCurrentLiveWallpaperPkgName();
        registerDownloadReceiver();
        //final long waitTime = SystemClock.uptimeMillis();
        this.mInitHandler.postDelayed(this.registerService,300);
        //Log.d(TAG, " classic waited ="+ (SystemClock.uptimeMillis()-waitTime) + "ms for previous step");
        
    }
    
    Runnable registerService = new Runnable(){
        public void run()
        {
        	Log.i("XXX","--------- activity: mcurrpos = "+mcurrpos);
            addSearchFragment(mcurrpos); 
		    /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: mohl DATE: 2013-1-11 S */
		    Reaper.processReaper(ClassicFragmentActivity.this, "LeJingpin", "LeJingpinEntry",
			Reaper.REAPER_NO_LABEL_VALUE, Reaper.REAPER_NO_INT_VALUE);
		    /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: AUT: mohl DATE: 2013-1-11 E */
	/*
		    if(!isWWVersion){
	            mToolBar.setOnCheckedChangeListener(mSwitchButtonListener);
		    }
	*/
        }
    };
     @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String title = item.getTitle().toString();
        Log.e("yumina","the language change id="+getTitle()+" title ="+title+" curpos="+mcurrpos+" entryTitle="+entryTitle) ;
        if(entryTitle == null) {
            finish();
            return true;
        }
             
        if(mMenu == null) return true;
        if(mcurrpos == 0 || mcurrpos == -1){
        	if(item.getItemId() == android.R.id.home ){
            if (entryTitle.equals(getTitle())){
                finish();
            }else{
            if(getString(R.string.local_app).equals(getTitle())){
                  /* setTitle(R.string.network_app);
                   item.setTitle(R.string.tab_local);
                   mcurrpos = 0;*/
                   addSearchFragment(0);
            }else{
                   setTitle(R.string.local_app);
                   mMenu.findItem(R.id.action_switch).setTitle(R.string.tab_network);
                   mcurrpos = -1;
                   addSearchFragment(-1);
            }
            }
            }
            else if(getString(R.string.tab_local).equals(title)){
            Log.e("yumina","0 net Uirecevier for the language change id="+getTitle()+" title ="+title+" curpos="+mcurrpos) ;
                   setTitle(R.string.local_app);
                   item.setTitle(R.string.tab_network);
                   mcurrpos = -1;
                   addSearchFragment(-1);
            }
            else if(getString(R.string.tab_network).equals(title)){
            Log.e("yumina","-1 local Uirecevier for the language change id="+getTitle()+" title ="+title+" curpos="+mcurrpos) ;
                  /* setTitle(R.string.network_app);
                   item.setTitle(R.string.tab_local);
                   mcurrpos = 0;*/
                   addSearchFragment(0);
            }
//            if(getString(R.string.local_app).equals(getTitle())){
               // mMenu.findItem(R.id.action_download).setVisible(true);
                mMenu.findItem(R.id.action_sort).setVisible(true);
//            }else{
                //mMenu.findItem(R.id.action_download).setVisible(true);
//            	mMenu.findItem(R.id.action_sort).setVisible(true);
//            }
        } else 
        if(mcurrpos == 1 || mcurrpos == 11){
        if(item.getItemId() == android.R.id.home ){
            if (entryTitle.equals(getTitle())){
                finish();
            }else{
            if(getString(R.string.local_theme).equals(getTitle())){
               setTitle(R.string.network_wallpaper);
               mMenu.findItem(R.id.action_switch).setTitle(R.string.tab_local);
               addSearchFragment(12);
            }else{
               setTitle(R.string.local_theme);
               mMenu.findItem(R.id.action_switch).setTitle(R.string.tab_network);
               addSearchFragment(1);
            }
            }

        }
        else if(getString(R.string.tab_local).equals(title)){
               setTitle(R.string.local_theme);
               item.setTitle(R.string.tab_network);
               addSearchFragment(1);
        }
        else if(getString(R.string.tab_network).equals(title)){
               setTitle(R.string.network_theme);
               item.setTitle(R.string.tab_local);
               addSearchFragment(11);
        }
            if(mMenu != null){
        	mMenu.findItem(R.id.action_sort).setVisible(false);
            }
        }

        else if(mcurrpos == 2 || mcurrpos == 12){
        if(item.getItemId() == android.R.id.home ){
            if (entryTitle.equals(getTitle())){
                finish();
            }else{
            if(getString(R.string.local_wallpaper).equals(getTitle())){
               setTitle(R.string.network_wallpaper);
               mMenu.findItem(R.id.action_switch).setTitle(R.string.tab_local);
               addSearchFragment(12); 
            }else{
               setTitle(R.string.local_wallpaper);
               mMenu.findItem(R.id.action_switch).setTitle(R.string.tab_network);
               addSearchFragment(2); 
            }
            }
        }
        else if(getString(R.string.tab_local).equals(title)){
               setTitle(R.string.local_wallpaper);
               mMenu.findItem(R.id.action_switch).setTitle(R.string.tab_network);
               addSearchFragment(2); 
        }
        else if(getString(R.string.tab_network).equals(title)){
               setTitle(R.string.network_wallpaper);
               mMenu.findItem(R.id.action_switch).setTitle(R.string.tab_local);
               addSearchFragment(12); 
        }
        
            if(mMenu != null){
       	    mMenu.findItem(R.id.action_sort).setVisible(false);
            }
        }

        //add by hwf
        else 
            if(mcurrpos == 3 || mcurrpos == 13){
            	changeLockView(item, title);
            }
        invalidateOptionsMenu();
        return true;
    }
     
    //add by tonghu
	private void changeLockView(MenuItem item, String title) {
		if(item.getItemId() == android.R.id.home ){
            if (entryTitle.equals(getTitle())){
                finish();
            }else{
            if(getString(R.string.local_lock).equals(getTitle())){
               setTitle(R.string.network_lock);
               mMenu.findItem(R.id.action_switch).setTitle(R.string.tab_local);
               addSearchFragment(13);
            }else{
               setTitle(R.string.local_lock);
               mMenu.findItem(R.id.action_switch).setTitle(R.string.tab_network);
               addSearchFragment(3);
            }
            }

        } else if (getString(R.string.tab_local).equals(title)) {
			setTitle(R.string.local_lock);
			item.setTitle(R.string.tab_network);
			addSearchFragment(3);
		} else if (getString(R.string.tab_network).equals(title)) {
			setTitle(R.string.network_lock);
			item.setTitle(R.string.tab_local);
			addSearchFragment(13);
		}
		if (mMenu != null) {
			mMenu.findItem(R.id.action_sort).setVisible(false);
		}
	}
	
	//add by hwf.only for get more lockscreen button
	public void changeLockView() {
//		TonghuLog.i(TAG, "change view");
		MenuItem menuItem = mMenu.findItem(R.id.action_switch);
		
		String title = menuItem.getTitle().toString();
		
		if (getString(R.string.tab_network).equals(title)) {
			setTitle(R.string.network_lock);
			menuItem.setTitle(R.string.tab_local);
			addSearchFragment(13);
			if (mMenu != null) {
				mMenu.findItem(R.id.action_sort).setVisible(false);
			}
		}
		invalidateOptionsMenu();
		
	}

    private void entryDownloadManagerActivity(){
    	Log.d(TAG,"----------entryDownloadManagerActivity----------");
		 Intent downloadIntent = new Intent(this,DownloadExpandableActivity.class);
		 downloadIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 try {
			 this.startActivity(downloadIntent);
		 } catch (Exception e) {
			 Toast.makeText(this, R.string.activity_not_found,
			 Toast.LENGTH_SHORT).show();
		 }
    }
    private void refresDownloadCount(int count){
    	if(mDownloadCountView == null)
    		return;
    	if(0 == count){
    		mDownloadCountView.setVisibility(View.GONE);
    	}else{
    		mDownloadCountView.setVisibility(View.VISIBLE);
    		mDownloadCountView.setText(String.valueOf(count));
    		mDownloadCountView.setTextColor(0xFFFFFFFF);
    	}
    }
    
   /* private void setDMListener(MenuItem menuItem){
    	if(menuItem == null || menuItem.getActionView() == null)
    		return;
    	
    	mDownloadCountView = (TextView)menuItem.getActionView().findViewById(R.id.download_count);
    	refresDownloadCount(LDownloadManager.getDefaultInstance(this).getAllDownloadCount());

    	menuItem.getActionView().setOnClickListener(
    			new OnClickListener(){
					@Override
					public void onClick(View arg0) {
						Log.d(TAG,"----------setDMListener----onClick------");
						entryDownloadManagerActivity();
					}
    			}
    	);
    }*/
    
    
    
    
	private void setPopUpWindowListener(MenuItem menuItem) {
		
		if (menuItem == null || menuItem.getActionView() == null){
			return;
		}
			
		showPopview = (View) menuItem.getActionView().findViewById(R.id.lejingpin_actionbar_more);

		menuItem.getActionView().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				int popWidth = getResources().getDimensionPixelSize(R.dimen.popupwindow_width);
				Log.i("popupwidth", "popWidth is:"+popWidth);
				
				mListPopupWindow.setAnchorView(showPopview);				
				mListPopupWindow.setWidth(dip2px(mContext,popWidth));
				mListPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.lejingpin_actionbar_popupwindow_bg));				
				int horizontalOffset = (arg0.getWidth()-dip2px(mContext,popWidth))/2-dip2px(mContext,10);							
				horizontalOffsetSecond = horizontalOffset;
				mListPopupWindow.setHorizontalOffset(horizontalOffset);
				int verticalOffset = (getActionBar().getHeight()-showPopview.getHeight())/2;
				mListPopupWindow.setVerticalOffset(verticalOffset);				
				mListPopupWindow.setModal(true);
				mListPopupWindow.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						
//						if(arg2==0){
//							mListPopupWindow.dismiss();
//							startSearch();							
//						}
						if(arg2==0){
							entryDownloadManagerActivity();
							mListPopupWindow.dismiss();
						}else if(arg2 ==1){
							startLejingpinSettings();
							mListPopupWindow.dismiss();
						}
						
					}
				});

				mListPopupWindow.show();

			}
		});
	}
    
    
    
    
	private class DownloadReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
//				refresDownloadCount(LDownloadManager.getDefaultInstance(context).getAllDownloadCount());
		}
	}
    
    private void registerAppUpgradeReceiver() {
    	mDownloadReceiver = new DownloadReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(DownloadConstant.ACTION_DOWNLOAD_STATE_CHANGED);
		filter.addAction(DownloadConstant.ACTION_DOWNLOAD_DELETE);
		mContext.registerReceiver(mDownloadReceiver, filter);
    }
    
    
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        Log.i(TAG, "onMenuOpened");
        
        if(menu.findItem(R.id.action_sort).isVisible()){        
	        setPopUpWindowListener(menu.findItem(R.id.action_sort));
	        showPopMenu();
        }
        return false;// if true, return system menu
    }
    
      @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("yumina", "onCreateOptionsMenu >> mcurrpos : "+mcurrpos);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.lejingpin_actionbar, menu);
        //add by hwf
        if(mcurrpos == -1 || mcurrpos == 1 || mcurrpos == 2 || mcurrpos == 3){   //local theme
            menu.findItem(R.id.action_switch).setTitle(R.string.tab_network);
        }else if(mcurrpos == 0 || mcurrpos == 11 || mcurrpos == 12 ||mcurrpos ==13){  // network theme
            menu.findItem(R.id.action_switch).setTitle(R.string.tab_local);

        }
        menu.findItem(R.id.action_divider).setEnabled(false);
        int phoneindex = SettingsValue.getCurrentMachineType(ClassicFragmentActivity.this);
        if(phoneindex != -1){
            if(mcurrpos == 1 ){   //local theme
            menu.findItem(R.id.action_switch).setVisible(false);   //hide network theme for tmp
            }
        }
        if(isWWVersion || isTDVersion){
        	menu.findItem(R.id.action_switch).setVisible(false);   //hide network theme and wallpaper
        }

                  
        //setDMListener(menu.findItem(R.id.action_download));
        setPopUpWindowListener(menu.findItem(R.id.action_sort));
        mMenu = menu;       
        return true;
    }
    public Menu mMenu;
    public void onSort(MenuItem item) {
//        int mSortMode = item.getItemId();
        // Request a call to onPrepareOptionsMenu so we can change the sort icon
        invalidateOptionsMenu();
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	Log.i(TAG,"onPrepareOptionsMenu,title:" + getTitle()+" curpos ="+mcurrpos);
    	
        if(mcurrpos == 2 || mcurrpos == 12){
       // menu.findItem(R.id.action_search).setVisible(false);
        if(getString(R.string.local_wallpaper).equals(getTitle())){
            //menu.findItem(R.id.action_download).setVisible(false);
        	menu.findItem(R.id.action_sort).setVisible(false);
            menu.findItem(R.id.action_switch).setTitle(R.string.tab_network);
        }else{
            //menu.findItem(R.id.action_download).setVisible(true);
        	menu.findItem(R.id.action_sort).setVisible(true);
            menu.findItem(R.id.action_switch).setTitle(R.string.tab_local);
        }
        }
        if(mcurrpos == 1 || mcurrpos == 11){
        //menu.findItem(R.id.action_search).setVisible(false);
        if(getString(R.string.local_theme).equals(getTitle())){
            //menu.findItem(R.id.action_download).setVisible(false);
        	menu.findItem(R.id.action_sort).setVisible(false);
            menu.findItem(R.id.action_switch).setTitle(R.string.tab_network);
        }else{
            //menu.findItem(R.id.action_download).setVisible(true);
        	menu.findItem(R.id.action_sort).setVisible(true);
            menu.findItem(R.id.action_switch).setTitle(R.string.tab_local);
        }
        }
        int phoneindex = SettingsValue.getCurrentMachineType(ClassicFragmentActivity.this);
        if(phoneindex != -1){
            if(mcurrpos == 1 ){   //local theme
            menu.findItem(R.id.action_switch).setVisible(false);
            }
        }
        if(isWWVersion || isTDVersion){
        	menu.findItem(R.id.action_switch).setVisible(false);   //hide network theme and wallpaper
        }
        if(mcurrpos == 0 || mcurrpos == -1){
//        	if(getString(R.string.local_app).equals(getTitle())){
//                menu.findItem(R.id.action_download).setVisible(true);
//            }else{
//                menu.findItem(R.id.action_download).setVisible(true);
//            }
        }
        //add by hwf
        if(mcurrpos == 3 || mcurrpos == 13){
            // menu.findItem(R.id.action_search).setVisible(false);
             if(getString(R.string.local_lock).equals(getTitle())){
                 //menu.findItem(R.id.action_download).setVisible(false);
             	menu.findItem(R.id.action_sort).setVisible(false);
                 menu.findItem(R.id.action_switch).setTitle(R.string.tab_network);
             }else{
                 //menu.findItem(R.id.action_download).setVisible(true);
             	menu.findItem(R.id.action_sort).setVisible(true);
                 menu.findItem(R.id.action_switch).setTitle(R.string.tab_local);
             }
             }
        return super.onPrepareOptionsMenu(menu);
    }






    private UiReceiver mUiReceiver;
    private void registerDownloadReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);
        filter.addAction(Intent.ACTION_WALLPAPER_CHANGED );
        mUiReceiver = new UiReceiver();
        registerReceiver(mUiReceiver, filter);
        regflag = true;
    }
    private class UiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_LOCALE_CHANGED.equals(action)) {
               Log.e("yumina","Uirecevier for the language change");
               finish();
            }
            else if (Intent.ACTION_WALLPAPER_CHANGED.equals(action)) {
               Log.e("yumin0523","Uirecevier for the wallpaper change click flag="+getOtherClickStatus());
               if(getOtherClickStatus()){
            	   if(checkOrientation()){
	                   setCurrentWallpaperOther();
	                   finish();
            	   }else{
            		   resetOtherOrientation();
	                   setCurrentWallpaperOther();
	                   finish();
            	   }
               }
            }
        }
    }

    private boolean getOtherClickStatus(){
        boolean flag = false;
        flag = mLeConstant.mIsClickOtherFlag ;
        return flag;
    }
    private boolean checkOrientation(){
    	int orientation = this.getResources().getConfiguration().orientation;
    	return (orientation ==  mLeConstant.mOtherWallpaperOrientation) ? true : false;
    }
    private void resetOtherOrientation(){
    	mLeConstant.mOtherWallpaperOrientation =  this.getResources().getConfiguration().orientation;
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.e("yumin0523","onResume curlivepkgname="+getCurrentLiveWallpaperPkgName()+" oldCur="+curLiveWallPkgName+" clickstatus="+getOtherClickStatus());
        if(getOtherClickStatus()){ 
            if(curLiveWallPkgName == null){
                if(getCurrentLiveWallpaperPkgName() != null){
                    setCurrentWallpaperOther();
                    finish();
                }
            }else{
                if(!(curLiveWallPkgName.equals(getCurrentLiveWallpaperPkgName()))){
                    setCurrentWallpaperOther();
                    finish();
                }
            }
        }
    }
    public void setCurrentWallpaperOther(){
        SharedPreferences sp = getSharedPreferences("CURRENT", 0);
        SharedPreferences.Editor editor = sp.edit();
        Log.e("yumin0523"," aaaaaaaaaa setCurrentWallpaper packagename");
        editor.putString("current_wallpaper", "OTHER").commit();
   }

    @Override
    public void onPause() {
        curLiveWallPkgName = getCurrentLiveWallpaperPkgName();
        Log.e("yumin0523","onResume<F2><F2><F2><F2><F2><F2><F2><F2> curlivewallpkgname="+getCurrentLiveWallpaperPkgName());
        super.onPause();
   }
   private String curLiveWallPkgName = null;
   private String getCurrentLiveWallpaperPkgName(){
        String pkgname = null;
        WallpaperManager  mWallpaperManager = WallpaperManager.getInstance(this);
        WallpaperInfo mWallpaperInfo = mWallpaperManager.getWallpaperInfo();
        if(mWallpaperInfo != null){
             //pkgname = mWallpaperInfo.getPackageName();
            try{
            pkgname = mWallpaperInfo.getComponent().getClassName();
            }catch(Exception e){
                return null;
            }

        }
        return pkgname;
    }

    public boolean showDialog() {
        if (!isNetworkEnabled()){
        DialogFragment newFragment = MyAlertDialogFragment.newInstance(
                R.string.app_name);
        newFragment.setCancelable(false);
        newFragment.show(getFragmentManager(), "dialog");
        }
        return !isNetworkEnabled();
    }

    public static class MyAlertDialogFragment extends DialogFragment {

        public static MyAlertDialogFragment newInstance(int title) {
            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("title", title);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int title = getArguments().getInt("title");

            LeAlertDialog alertDialog = new LeAlertDialog(getActivity(), R.style.Theme_LeLauncher_Dialog_Shortcut);
            alertDialog.setLeTitle(title);
            alertDialog.setLeMessage(R.string.confirm_network_open);
            alertDialog.setLePositiveButton(getActivity().getText(R.string.rename_action),
               new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int whichButton) {
                       ((ClassicFragmentActivity)getActivity()).doPositiveClick();
                   }
               }
               );
            alertDialog.setLeNegativeButton(getActivity().getText(R.string.cancel_action),
                   new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int whichButton) {
                           ((ClassicFragmentActivity)getActivity()).doNegativeClick();
                       }
                   });
            return alertDialog; 
        }
    }
    public void doPositiveClick() {
        Log.i("FragmentAlertDialog", "Positive click!");
        startConfirm();
        finish();
    }

    public void doNegativeClick() {
        Log.i("FragmentAlertDialog", "Negative click!");
        Toast.makeText(this, R.string.version_update_toast, Toast.LENGTH_SHORT).show();
        finish();
    }



    private void startConfirm() {
        Intent intent = new Intent();
        intent.setClass(this, com.lenovo.launcher2.settings.SeniorSettings.class);
        startActivity(intent);
    }

    private boolean isNetworkEnabled(){
        SharedPreferences sp = getSharedPreferences(SettingsValue.PERFERENCE_NAME, 4);
        boolean isNetworkEnabled = sp.getBoolean(SettingsValue.PREF_NETWORK_ENABLER, true);
        Log.i(TAG, "Negative click! isNetworkEnabled ========"+isNetworkEnabled);
        return isNetworkEnabled;
    }
    
    private void startLeAppStore(){
    	String storePkName = "com.lenovo.leos.appstore";
		boolean appStore = isInstalled(storePkName);
		if (appStore) {
			runApp(storePkName);
		} else {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri
					.parse("http://www.lenovomm.com/appstore/html/index.html")));
		}
    }
    
	private void runApp(String pkName) {
		try {
			Intent intent = mContext.getPackageManager()
					.getLaunchIntentForPackage(pkName);
			if (intent != null) {
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	private boolean isInstalled(String pkName) {
		try {
			if (mContext.getPackageManager().getPackageInfo(pkName, 0) != null) {
				return true;
			}
		} catch (NameNotFoundException e) {
			// e.printStackTrace();
		}
		return false;
	}

    private void addSearchFragment(int type) {
        if (!LEJPConstant.getInstance().mLeMainActivityonResumeFlag ){
            Log.e(TAG,"addSearchFragment error 111111 F2><F2><F2><F2><F2><F2><F2><F2><F2>");
            return;
        }
/*
        ft.setCustomAnimations(R.animator.fragment_open_enter,
                R.animator.fragment_open_exit,
                R.animator.fragment_close_enter,
                R.animator.fragment_close_exit);
*/
        if (type == TAB_INDEX_RECOMMEND){
        	startLeAppStore();
     /*       if (networkRecommendFragment == null ){
            	networkRecommendFragment = new HwPushAppFragment();
            }
            ft.replace(R.id.fragment_relative,networkRecommendFragment);
            ft.addToBackStack(null);
            try{
            ft.commitAllowingStateLoss();
            }catch(Exception e){
            Log.e(TAG,"addSearchFragment error 222222 F2><F2><F2><F2><F2><F2><F2><F2><F2>");
            }*/
        }
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        if(type == -1){
        	if(recommendFragment==null){
        		recommendFragment = new  HwLocalAppFragment();
        	}
        	ft.replace(R.id.fragment_relative,recommendFragment );
            ft.addToBackStack(null);
            try{
            ft.commitAllowingStateLoss();
            }catch(Exception e){
            }
        }
        if ( type == TAB_INDEX_THEME  ){
            if (themeFragment == null ){
                themeFragment = new ThemeFragment(type);
            }
            ft.replace(R.id.fragment_relative,themeFragment );
            ft.addToBackStack(null);
            try{
            ft.commitAllowingStateLoss();
            }catch(Exception e){
            }
       }
       if ( type == 11  ){
           if (networkthemeFragment == null ){
               networkthemeFragment = new GetMoreThemeFragment();
           }
           ft.replace(R.id.fragment_relative,networkthemeFragment );
           ft.addToBackStack(null);
           try{
           ft.commitAllowingStateLoss();
           }catch(Exception e){
           }
       }
       if ( type == 2 ){// local wallpaper fragment
			if (wallpaperFragment == null) {
				// yangbin5 start
				String deviceModel = android.os.Build.MODEL;
				if (deviceModel.contains("B8080")) {
					wallpaperFragment = new BladeClassicFragment();
				}else {
					wallpaperFragment = new ClassicFragment();
				}
				// yangbin5 end
			}
           ft.replace(R.id.fragment_relative,wallpaperFragment );
           ft.addToBackStack(null);
           try{
           ft.commitAllowingStateLoss();
           }catch(Exception e){
           }
       }
       if (type == 12   ){
           if (networkwallpaperFragment == null ){
               networkwallpaperFragment = new AndroidWallpaperFragment();
           }
           ft.replace(R.id.fragment_relative,networkwallpaperFragment );
           ft.addToBackStack(null);
           try{
           ft.commitAllowingStateLoss();
           }catch(Exception e){
           }
       }
       //add by hwf
       if(type == 3){
    	   if (lockscreenFragment == null ){
    		   lockscreenFragment = new LockScreenFragment();
           }
           ft.replace(R.id.fragment_relative,lockscreenFragment );
           ft.addToBackStack(null);
           try{
           ft.commitAllowingStateLoss();
           }catch(Exception e){
           }
       }
        if(type == 13){
    	   if (networklockscreenFragment == null ){
    		   networklockscreenFragment = new GetMoreLockScreenFragment();
           }
           ft.replace(R.id.fragment_relative,networklockscreenFragment );
           ft.addToBackStack(null);
           try{
           ft.commitAllowingStateLoss();
           }catch(Exception e){
           }
        }
        gatherData(type);
    }

    private void gatherData(int pos){
        String gatherType = "RecommPagerEntry";
        switch(pos){
        case 1:
            gatherType = "LocalThemeEntry";
            break;
        case 11:
            gatherType = "RecommThemeEntry";
            break;
        case 2:
            gatherType = "LocalPagerEntry";
            break;
        case 12:
            gatherType = "RecommPagerEntry";
            break;
        case 3:
            gatherType = "LocalLKScreenEntry";
            break;
        case 13:
            gatherType = "RecommLKScreenEntry";
            break;
            
        default:
            break;
        }
        Reaper.processReaper(this,"LeJingpin",gatherType,
        Reaper.REAPER_NO_LABEL_VALUE, Reaper.REAPER_NO_INT_VALUE );
    }

                                                                 
    /** Used both by {@link ActionBar} and {@link ViewPagerAdapter} */
    private static final int TAB_INDEX_RECOMMEND = 0;
    private static final int TAB_INDEX_THEME = 1;
    private static final int TAB_INDEX_WALLPAPER = 2;
    private static final int TAB_INDEX_LOCKSCREEN = 3;
    @Override
    public void onBackPressed() {
        if(entryTitle == null) {
            finish();
        }
        else if(mcurrpos == 0 || mcurrpos == -1){
            if (entryTitle.equals(getTitle()) ){
                finish();
            }else{
            if(getString(R.string.local_app).equals(getTitle())){
              /*     setTitle(R.string.network_app);
                if(mMenu != null)
                mMenu.findItem(R.id.action_switch).setTitle(R.string.tab_local);
                   mcurrpos = 0;*/
                   addSearchFragment(0);
            }else{
                   setTitle(R.string.local_app);
                   mMenu.findItem(R.id.action_switch).setTitle(R.string.tab_network);
                   mcurrpos = -1;
                   addSearchFragment(-1);
            }
            }

        }
        else if(mcurrpos == 1 || mcurrpos == 11){
            if (entryTitle.equals(getTitle())){
                finish();
            }else{
            if(getString(R.string.local_theme).equals(getTitle())){
               setTitle(R.string.network_wallpaper);
               if(mMenu != null){
               mMenu.findItem(R.id.action_switch).setTitle(R.string.tab_local);
               mMenu.findItem(R.id.action_sort).setVisible(true);
               }
               addSearchFragment(12);
            }else{
               setTitle(R.string.local_theme);
               if(mMenu != null){
               mMenu.findItem(R.id.action_switch).setTitle(R.string.tab_network);
               mMenu.findItem(R.id.action_sort).setVisible(false);
               }
               addSearchFragment(1);
            }
            }
        }
        else if(mcurrpos == 2 || mcurrpos == 12){
            if (entryTitle.equals(getTitle())){
                finish();
            }else{
            if(getString(R.string.local_wallpaper).equals(getTitle())){
               setTitle(R.string.network_wallpaper);
               if(mMenu != null){
               mMenu.findItem(R.id.action_switch).setTitle(R.string.tab_local);
               mMenu.findItem(R.id.action_sort).setVisible(true);
               }
               addSearchFragment(12);
            }else{
               setTitle(R.string.local_wallpaper);
               if(mMenu != null){
               mMenu.findItem(R.id.action_switch).setTitle(R.string.tab_network);
               mMenu.findItem(R.id.action_sort).setVisible(false);
               }
               addSearchFragment(2);
            }
            }
        }
        
        //add by hwf
        else if(mcurrpos == 3 || mcurrpos == 13){
            if (entryTitle.equals(getTitle())){
                finish();
            }else{
            if(getString(R.string.local_lock).equals(getTitle())){
               setTitle(R.string.network_lock);
               if(mMenu != null){
               mMenu.findItem(R.id.action_switch).setTitle(R.string.tab_local);
               mMenu.findItem(R.id.action_sort).setVisible(true);
               }
               addSearchFragment(13);
            }else{
               setTitle(R.string.local_lock);
               if(mMenu != null){
               mMenu.findItem(R.id.action_switch).setTitle(R.string.tab_network);
               mMenu.findItem(R.id.action_sort).setVisible(false);
               }
               addSearchFragment(3);
            }
            }
        }
    }
    public void onClickGetMoreButton(View v) {
        Intent intent = new Intent(this, GetMoreFragmentActivity.class);
        if(mcurrpos == 1 || mcurrpos == 11 ){   //local theme
            intent.putExtra("CALLFROM",TAB_INDEX_THEME);
        }else if(mcurrpos == 2 || mcurrpos == 12){   //local wallpaper
            intent.putExtra("CALLFROM",TAB_INDEX_WALLPAPER);
        }
        startActivity(intent);
    }
    public void finish(){
        super.finish();
        LEJPConstant.getInstance().mLeMainActivityonResumeFlag = false;
        //xujing modified
//        Utilities.mIsMainActivityActive = false;
        Utilities.setDownloadActive(false, this.getClass().getName());
        Utilities.killLejingpinProcess();
    }
    @Override
    public void onDestroy() {
        Log.e(TAG,"onDestroy<F2><F2><F2><F2><F2><F2><F2><F2><F2>");
        unregisterReceiver(mUiReceiver);
        super.onDestroy();
    }

    Runnable killService = new Runnable(){
        public void run()
        {
            Log.e(TAG,"killProcess<F2><F2><F2><F2><F2><F2><F2><F2><F2>");
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    };
    
    
    public void startLejingpinSettings(){
    	Intent mIntent = new Intent(this, LejingpingSettings.class);   	
    	startActivity(mIntent);
    	
    }
    
    
    public  void startSearch(){
    	
		try {

			Intent mIntent = new Intent("android.intent.action.SEARCH_ACTIVITY_NEW");
			mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(mIntent);
			//finish();

		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, R.string.activity_not_found,Toast.LENGTH_SHORT).show();

		} catch (SecurityException e) {
			Toast.makeText(this, R.string.activity_not_found,Toast.LENGTH_SHORT).show();

		}
    }
    
    
	public class AppAdapter extends BaseAdapter {
	private Context mContext;
	
	public AppAdapter(Context context){
		this.mContext = context;
		
	} 

	
	@Override
	public int getCount() {
		return items_title.length;
		 
	}

	
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	



	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub		
		convertView = LayoutInflater.from(mContext).inflate(R.layout.row, parent,false);		
		TextView  name = (TextView) convertView.findViewById(R.id.row_item);		
		name.setText(getResources().getString(items_title[position]));		
		name.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(items_img[position]), null, null,null);
		mListPopupWindow.getListView().setDivider(getResources().getDrawable(R.drawable.lejingpin_actionbar_diver));

		mListPopupWindow.getListView().setOnKeyListener(
			new OnKeyListener() {
				@Override
				public boolean onKey(View arg0, int keycode,KeyEvent arg2) {
					// TODO Auto-generated method stub
					Log.i(TAG, "listpopupwindow get the onkey down action");
					if (keycode == KeyEvent.KEYCODE_MENU&& mListPopupWindow.isShowing()) {
						mListPopupWindow.dismiss();
					}
					return false;
				}
			});

		name.setCompoundDrawablePadding(10);
		return convertView;
	}
	
	
}
	
	public static int dip2px(Context context, float dipValue){ 
		final float scale = context.getResources().getDisplayMetrics().density; 
		return (int)(dipValue * scale + 0.5f); 
	} 
    
    
	
	
	private void showPopMenu(){
		
		
		if(mListPopupWindow!=null && mListPopupWindow.isShowing()){
			mListPopupWindow.dismiss();
			return;
		}
		
		if(mListPopupWindow==null){
			return;
		}
		
		int popWidth = getResources().getDimensionPixelSize(R.dimen.popupwindow_width);
		Log.i("popupwidth", "popWidth is:"+popWidth);
		mListPopupWindow.setAnchorView(showPopview);				
		mListPopupWindow.setWidth(dip2px(mContext,popWidth));
		mListPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.lejingpin_actionbar_popupwindow_bg));				
		
		if(horizontalOffsetSecond==0){
			
			int onClickViewWidth = showPopview.getWidth()+dip2px(mContext, 20);
			
			horizontalOffsetSecond = (onClickViewWidth-dip2px(mContext,popWidth))/2-dip2px(mContext,10);
			
			mListPopupWindow.setHorizontalOffset(horizontalOffsetSecond);
		}else{
			mListPopupWindow.setHorizontalOffset(horizontalOffsetSecond);
		}
				
		int verticalOffset = (getActionBar().getHeight()-showPopview.getHeight())/2;
		mListPopupWindow.setVerticalOffset(verticalOffset);				
		mListPopupWindow.setModal(true);	

		mListPopupWindow.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				

					
				if(arg2==0){
					entryDownloadManagerActivity();
					mListPopupWindow.dismiss();
				}else if(arg2 ==1){
					startLejingpinSettings();
					mListPopupWindow.dismiss();
				}
				
			}
		});

		mListPopupWindow.show();
	}
	
    
}
