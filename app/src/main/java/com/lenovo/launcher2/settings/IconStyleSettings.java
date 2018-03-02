/*
 * Copyright (C) 2011
 *
 * AUT: liuli liuli0120@use.com.cn DATE: 2011-12-20
 * add activity for theme settings
 */

package com.lenovo.launcher2.settings;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

import com.lenovo.launcher.R;

import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;
import com.lenovo.launcher2.commonui.LeDialog;
import com.lenovo.launcher2.commonui.LeProcessDialog;
import com.lenovo.launcher2.customizer.Reaper;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.Utilities;

public class IconStyleSettings extends Activity implements OnClickListener {
    private static final String TAG = "IconStyleSettings";
    
    private LeProcessDialog mProgressDlg;
    
    private IconStylePagedView mPages;
    private GridLayout mIconPages;


    private Button mButtonRight;
    private Button mButtonCancel;
    private ArrayList<Drawable> mDrawables;
    private Context mContext;
    
	/*RK_ID: ICON_SETTINGS. AUT: SHENCHAO1. 2012-12-26 S.*/
	 private TextView mDialogTitle;
	/*RK_ID: ICON_SETTINGS. AUT: SHENCHAO1. 2012-12-26 E. */

    private BroadcastReceiver mIconStyleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (SettingsValue.ACTION_FOR_LAUNCHER_FINISH.equals(action)) {
                if (mProgressDlg != null && mProgressDlg.isShowing())
                    mProgressDlg.dismiss();
                if (mPages.getIndex() != -1) {
                    Toast.makeText(context, R.string.icon_style_success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, R.string.icon_style_clear_success, Toast.LENGTH_SHORT)
                            .show();
                }
                finish();
            }
        }
    };

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        mContext = this;
        
        /*RK_ID: ICON_SETTINGS. AUT: SHENCHAO1. 2013-01-09 S.*/
     //   getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.icon_style_settings);

     //   mActionBar = getActionBar();
//        initActionBar(getString(R.string.icon_settings_title));
        mDialogTitle = (TextView) findViewById(R.id.dialog_title);
        mDialogTitle.setText(R.string.icon_settings_title);
        ImageView icon = (ImageView)findViewById(R.id.dialog_icon);
		icon.setImageResource(R.drawable.dialog_title_back_arrow);
		
		//test by dining
				
		icon.setOnClickListener(new View.OnClickListener() {

		            @Override
		            public void onClick(View view) {
		                finish();
		            }
		        });
		
        /*RK_ID: ICON_SETTINGS. AUT: SHENCHAO1. 2013-01-09 E.*/


        mPages = (IconStylePagedView) findViewById(R.id.icon_style);
        mIconPages = (GridLayout) findViewById(R.id.show_icon);
        /*RK_ID: ICON_SETTINGS. AUT: SHENCHAO1. 2013-01-09 S.*/
//        mButtonLeft = (Button) findViewById(R.id.buttonLeft);
//        mButtonLeft.setTag("left");
//        mButtonLeft.setOnClickListener(this);
        /*RK_ID: ICON_SETTINGS. AUT: SHENCHAO1. 2013-01-09 E.*/
        mButtonRight = (Button) findViewById(R.id.addfinish);
        mButtonRight.setText(R.string.icon_style_done);
        mButtonRight.setTag("right");
        mButtonRight.setOnClickListener(this);
        mButtonCancel = (Button) findViewById(R.id.canceladd);
        mButtonCancel.setText(R.string.icon_style_cancel);
        mButtonCancel.setTag("cancel");
        mButtonCancel.setOnClickListener(this);

        initContent();

        // Register intent receivers
        IntentFilter filter = new IntentFilter(SettingsValue.ACTION_FOR_LAUNCHER_FINISH);
        registerReceiver(mIconStyleReceiver, filter);
    }

//    private void initActionBar(String label) {
//    	/*RK_ID: ICON_SETTINGS. AUT: SHENCHAO1. 2013-01-09 S.*/
////        if (mActionBar == null)
////            return;
////        mActionBar.setTitle(label);
//    	/*RK_ID: ICON_SETTINGS. AUT: SHENCHAO1. 2013-01-09 S.*/
//    }

    private void initContent() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mDrawables = new ArrayList<Drawable>();
        /*** AUT:zhaoxy . DATE:2012-02-28 . START***/
        // int i = 0;
        int style = SettingsValue.getIconStyleIndex(this);
        /* RK_ID: RK_LELAUNCHER_STYLE . AUT: zhanggx1 . DATE: 2012-02-22 . S */
        /* RK_ID: RK_ICON_STYLE . AUT: shenchao1 . DATE: 2013-01-15 . S */
        int appLayoutWidth = dm.widthPixels;
        int appIconSize = getResources().getDimensionPixelSize(R.dimen.icon_style_app_icon_size);
        int appMarginSize = getResources().getDimensionPixelSize(R.dimen.icon_style_layout_margin);       
        int appIconGap = (appLayoutWidth - 4 * appIconSize - 2 * appMarginSize) / 4;
        /* RK_ID: RK_ICON_STYLE . AUT: shenchao1 . DATE: 2013-01-15 . S */
        int[] icons = { R.drawable.icon_app_1, R.drawable.ic_launcher_theme_shortcut, R.drawable.icon_app_3,
                R.drawable.icon_app_4 };
        mIconPages.removeAllViews();
        for (int i = 0; i < 4; i++) {
            // int ix = i; //old is i%5
            // int iy = 0; //old is i/5
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams(GridLayout.spec(0,
                    GridLayout.CENTER), GridLayout.spec(i, GridLayout.CENTER));
            lp.setGravity(Gravity.CENTER);
//            if (i > 0)
            	/* RK_ID: RK_ICONSET_STYLE . AUT: shanchao1 . DATE: 201-01-08 . S */
                lp.leftMargin = appIconGap / 2;// (int) (THEME_GRID_WIDTHGAP * dm.density);
                lp.rightMargin = appIconGap / 2;
                /* RK_ID: RK_ICONSET_STYLE . AUT: shanchao1 . DATE: 201-01-08 . E */
            ImageView iv = new ImageView(this);
            lp.width = appIconSize;
            lp.height = appIconSize;
//        	Drawable d = info.applicationInfo.loadIcon(mLauncher.getPackageManager());
            Drawable d = getResources().getDrawable(icons[i]);
            mDrawables.add(d);
            iv.setBackgroundDrawable(new BitmapDrawable(getResources(), Utilities.createIconBitmap(
                    d, style, this)));
            mIconPages.addView(iv, lp);
            /*** AUT:zhaoxy . DATE:2012-02-28 . END***/
        }
        /* RK_ID: RK_LELAUNCHER_STYLE . AUT: zhanggx1 . DATE: 2012-02-22 . E */
        mPages.setup(mIconPages, mDrawables, style);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mIconStyleReceiver);

        if (mProgressDlg != null && mProgressDlg.isShowing()) {
            mProgressDlg.dismiss();
            mProgressDlg = null;
        }

        // views
//        mActionBar = null;
        mPages.destory();
//        mPages.removeAllViews();
        mPages = null;
        mIconPages.removeAllViews();
        mIconPages = null;
//        mButtonLeft = null;
        mButtonRight = null;
        mButtonCancel = null;

        mDrawables.clear();
        mDrawables = null;
        
        if (this.mPages != null) {
        	mPages.cleanup();
        	mPages = null;
        }
        System.gc();
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();

    }

    private void initProgressDialog() {
        if (mProgressDlg == null) {
            /*mProgressDlg = new Dialog(this,R.style.Theme_LeLauncher_ProgressDialog);
            mProgressLayout = (LinearLayout) this.getLayoutInflater().inflate(
                    R.layout.apply_progressbar, null);
            TextView tv = (TextView) mProgressLayout.findViewById(R.id.progress_msg);
            tv.setText(R.string.icon_style_apply);
            mProgressDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDlg.setContentView(mProgressLayout);

            Window window = mProgressDlg.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            window.setGravity(Gravity.CENTER);
            mProgressDlg.setCancelable(false);*/
        	mProgressDlg = new LeProcessDialog(mContext);
        	if(mProgressDlg != null){
        		mProgressDlg.setLeMessage(R.string.icon_style_apply);
        	}
        }
    }

    private void setProgressMsg(int index) {
        initProgressDialog();

        if(mProgressDlg != null){
        	if (index != -1) {
        	    mProgressDlg.setLeMessage(R.string.icon_style_apply);
        	}else{
        		mProgressDlg.setLeMessage(R.string.icon_style_clear_progress_msg);
        	}
        }
        /*if (mProgressLayout == null) {
            return;
        }
        TextView tv = (TextView) mProgressLayout.findViewById(R.id.progress_msg);

        if (index != -1) {
            tv.setText(R.string.icon_style_apply);
        } else {
            tv.setText(R.string.icon_style_clear_progress_msg);
        }*/
    }

    private class SwtichSkinRunnable implements Runnable {
        @Override
        public void run() {
            int sPrefCurrentIconStyle = mPages.getIndex();
            Intent i = new Intent(SettingsValue.ACTION_ICON_STYLE_INDEX_CHANGED);
            i.putExtra(SettingsValue.PREF_ICON_BG_STYLE, sPrefCurrentIconStyle);
            /*** AUT:zhaoxy . DATE:2012-03-01 . START***/
            // move writing files to the launcher receiver for bug 170992
//            SettingsValue.setIconStyleIndex(sPrefCurrentIconStyle);
//            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
//                    IconStyleSettings.this.getApplicationContext()).edit();
//            editor.putInt(SettingsValue.PREF_ICON_BG_STYLE, sPrefCurrentIconStyle);
//            try {
//                editor.apply();
//            } catch (AbstractMethodError unused) {
//                editor.commit();
//            }

            try {
                LauncherApplication app = (LauncherApplication) getApplicationContext();
                app.mIconCache.flush();
//                sendBroadcast(new Intent(SettingsValue.ACTION_ICON_STYLE_INDEX_CHANGED));
                sendBroadcast(i);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                /* RK_ID: FIX BUG 171021 . AUT: yumina . DATE: 2012-10-08 . S */
                if (!SettingsValue.isVersionLargerThan41()) {//Build.VERSION.RELEASE.equals(SettingsValue.VERSION_CODE)){
                    finish();
                }
                /* RK_ID: FIX BUG 171021 . AUT: yumina . DATE: 2012-10-08 . E */
            }
        }
    }

    @Override
    public void onClick(View v) {
        String t = (String) v.getTag();
        if ("left".equals(t)) {
            mPages.reset();
        } else if ("right".equals(t)) {
            /*** AUT:zhaoxy . DATE:2012-03-02 . START***/
            // check index first
            int sPrefCurrentIconStyle = mPages.getIndex();
            if (sPrefCurrentIconStyle == Integer.MIN_VALUE
                    || sPrefCurrentIconStyle >= Utilities.ICON_STYLE_COUNT
                    || sPrefCurrentIconStyle < SettingsValue.THEME_ICON_BG_INDEX) {
                Log.w(TAG, "invalid style index" + sPrefCurrentIconStyle);
                finish();
                return;
            } else if (SettingsValue.getIconStyleIndex(this) == sPrefCurrentIconStyle) {
                // icon style not changed, so finish it.
                finish();
                return;
            }

            initProgressDialog();
            if (mProgressDlg != null && !mProgressDlg.isShowing()) {
                setProgressMsg(mPages.getIndex());
                mProgressDlg.show();
            }
            new Thread(new SwtichSkinRunnable()).start();
        } else {
            finish();
        }

    }

    /* RK_ID: RK_ICON . AUT: zhanggx1 . DATE: 2012-02-17 . S */
    public void onBackPressed() {
        if (mProgressDlg != null && mProgressDlg.isShowing()) {
            return;
        }
        super.onBackPressed();
    }
    /* RK_ID: RK_ICON . AUT: zhanggx1 . DATE: 2012-02-17 . E */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {   
    	super.onConfigurationChanged(newConfig);
    	initContent();
    }
}
