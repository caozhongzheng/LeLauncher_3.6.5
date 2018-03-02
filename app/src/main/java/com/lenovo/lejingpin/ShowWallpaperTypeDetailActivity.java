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

import android.view.MotionEvent;

import android.graphics.Color;
import android.app.ActionBar;


import android.graphics.Typeface;
import com.lenovo.launcher2.customizer.SettingsValue;


import android.app.TabActivity;

import android.view.Menu;
import android.view.MenuItem;

import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;

/**
 * Demonstrates the use of custom animations in a FragmentTransaction when
 * pushing and popping a stack.
 */
//public class ShowWallpaperTypeDetailActivity extends Activity implements View.OnClickListener{
public class ShowWallpaperTypeDetailActivity extends Activity {
    int mStackLevel = 1;

    private String TAG = "ShowMoreActivity";
    private RadioButton t1,t2,t3,t4;
    private RadioGroup mToolBar;
    private ViewPager mViewPager;
    private int mCallFromIndex = 0;
    private Fragment mMoreThemeFragment;
    private Fragment mMoreWallpaperFragment;
    private Fragment mMoreLockscreenFragment;
    private String mPaperType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        final ActionBar bar = getActionBar();
        bar.setDisplayOptions(  ActionBar.DISPLAY_USE_LOGO | ActionBar.DISPLAY_SHOW_HOME |  ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);

/*
        int titleId = getResources().getSystem().getIdentifier(
                        "action_bar_title", "id", "android");
        TextView titleView = (TextView) findViewById(titleId);
        titleView.setTextColor(Color.WHITE);
*/

        setContentView(R.layout.android_wallpaper_showdetail);
/*
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new ViewPagerAdapter(getFragmentManager()));

*/
//        mViewPager.setCurrentItem(0, false /* smoothScroll */);
        Intent intent = getIntent();
        String title = intent.getStringExtra("TITLE");
        setTitle(title);
        mPaperType = intent.getStringExtra("TYPE");

        Log.e(TAG,"mPaperType ======================"+mPaperType);
//        mCallFromIndex = intent.getIntExtra("CALLFROM", 2);
        addSearchFragment();
//        InitBottomBarView(); 
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home ){
            finish();
        }
        return true;
    }


    private void addSearchFragment() {
        // In order to take full advantage of "fragment deferred start", we need to create the
        // search fragment after all other fragments are created.
        // The other fragments are created by the ViewPager on the first onMeasure().
        // We use the first onLayout call, which is after onMeasure().

        // Just return if the fragment is already created, which happens after configuration
        // changes.
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (mMoreWallpaperFragment == null ){
                mMoreWallpaperFragment =  new ShowWallpaperTypeFragment(mPaperType);
            }
            ft.replace(R.id.fragment_relative,mMoreWallpaperFragment );
            ft.addToBackStack(null);
            ft.commit();
    }


    /** Used both by {@link ActionBar} and {@link ViewPagerAdapter} */
    //private static final int TAB_INDEX_MORE_RECOMMEND = 0;
/*

    private static final int TAB_INDEX_MORE_THEME = 0;
    private static final int TAB_INDEX_MORE_WALLPAPER = 1;
    private static final int TAB_INDEX_MORE_LOCKSCREEN = 2;

    private static final int TAB_INDEX_MORE_COUNT = 3;

    public class ViewPagerAdapter extends FragmentPagerAdapter {
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
               // case TAB_INDEX_MORE_RECOMMEND:
                 //   mMoreRecommendFragment = new GetMoreFragment();
                   // return mMoreRecommendFragment ;
                case TAB_INDEX_MORE_THEME:
                    mMoreThemeFragment = new GetMoreThemeFragment();
                    return mMoreThemeFragment;
                case TAB_INDEX_MORE_WALLPAPER:
                    //mMoreWallpaperFragment =  new GetMoreFragment();
                    return mMoreWallpaperFragment;
                case TAB_INDEX_MORE_LOCKSCREEN:
                    mMoreLockscreenFragment =  new GetMoreLockScreenFragment();
                   return  mMoreLockscreenFragment;
            }
            throw new IllegalStateException("No fragment at position " + position);
        }

        @Override
        public int getCount() {
            return TAB_INDEX_MORE_COUNT;
        }
    }


     private Fragment getFragmentAt(int position) {
        switch (position) {
            case TAB_INDEX_DIALER:
                return ragment;
            case TAB_INDEX_CALL_LOG:
                return mCallLogFragment;
            case TAB_INDEX_FAVORITES:
                return mPhoneFavoriteFragment;
            default:
                throw new IllegalStateException("Unknown fragment index: " + position);
        }
    }
    private void InitBottomBarView() {
         mToolBar = (RadioGroup) findViewById(R.id.toolBar);
         mToolBar.setOnCheckedChangeListener(mSwitchButtonListener);
         mToolBar.setVisibility(View.GONE);

         t1 = (RadioButton) findViewById(R.id.bar_toolbar_theme);
         t2 = (RadioButton) findViewById(R.id.bar_toolbar_wallpaper);
         t3 = (RadioButton) findViewById(R.id.bar_toolbar_lockscreen);
         Typeface tf = SettingsValue.getFontStyle(this);
         if (tf != null ){
             t1.setTypeface(tf);
             t2.setTypeface(tf);
             t3.setTypeface(tf);
         }


         if(mCallFromIndex == 1){
         t1.setChecked(true);
         }else if(mCallFromIndex == 2){
         t2.setChecked(true);
         }else if(mCallFromIndex == 3){
         t3.setChecked(true);
         }
    }
    public void onClick(View v) {
        Log.e(TAG,"onClick view======="+v);
    }
     public void onClickGetMoreButton(View v) {
        Log.d(TAG, "---onClickGetMoreButton---");

    }
    
    private RadioGroup.OnCheckedChangeListener mSwitchButtonListener = new RadioGroup.OnCheckedChangeListener() {
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.bar_toolbar_recommend:
                    break;
                case R.id.bar_toolbar_theme:
                     setTitle(R.string.more_theme);
                     //mViewPager.setCurrentItem(TAB_INDEX_MORE_THEME, false);
                    addSearchFragment(1);

                    break;
                case R.id.bar_toolbar_wallpaper:
                     setTitle(R.string.more_wallpaper);
                     //mViewPager.setCurrentItem(TAB_INDEX_MORE_WALLPAPER, false);
                    addSearchFragment(2);
                    break;
                case R.id.bar_toolbar_lockscreen:
                     setTitle(R.string.more_lock);
                     //mViewPager.setCurrentItem(TAB_INDEX_MORE_LOCKSCREEN, false);
                    addSearchFragment(3);
                    break;
            }
        }
    };


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("level", mStackLevel);
    }
*/
    @Override
    public void onBackPressed() {
        finish();
    }
    @Override
    public void onDestroy() {
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }

}
