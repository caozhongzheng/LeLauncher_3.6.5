/*
 * Copyright (C) 2012
 *
 * AUT: liuli liuli0120@use.com.cn DATE: 2012-01-10
 * when user want to replace icon with letheme package icons
 * start this activity for preview
 */

package com.lenovo.launcher2.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.ListPreference;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;
import com.lenovo.launcher2.customizer.Constants;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.Utilities;

public class LethemeImagesActivity extends Activity {
    // maps for spinner, key is label of theme package, value is package name
    private HashMap<CharSequence, String> mPkgMaps = new HashMap<CharSequence, String>();
//    private static final String THEME_CATEGORY = "android.intent.category.THEMESKIN";
    private boolean mIsFolder = false;
    private AppsAdapter mGridAdapter;

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.theme_image_preview_layout);
        setTitle(R.string.theme_image_preview_title);

        if (getIntent() != null)
            mIsFolder = getIntent().getBooleanExtra(Constants.EXTRA_FOLDER, false);
        RelativeLayout relative = (RelativeLayout) findViewById(R.id.relative_letheme);
        relative.setVisibility(mIsFolder ? View.GONE : View.VISIBLE);

        mGridAdapter = new AppsAdapter(this);

        if (!mIsFolder) {
            // init spinner
            Spinner themeSpinner = (Spinner) findViewById(R.id.spinner_theme);
            final ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this,
                    android.R.layout.simple_spinner_item);
            findActivitiesForSkin(adapter);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            themeSpinner.setAdapter(adapter);
            themeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    CharSequence key = adapter.getItem(position);
                    String pkgName = mPkgMaps.get(key);
                    /*RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2012-07-04 . S*/
                    String androidTheme = SettingsValue.getDefaultAndroidTheme(LethemeImagesActivity.this);
                    if (androidTheme.equals(pkgName)) {
                    	initGridView(LethemeImagesActivity.this, mGridAdapter);
                    	return;
                    }
                    /*RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2012-07-04 . E*/
                    try {
                        final Context friendContext = createPackageContext(pkgName, Context.CONTEXT_IGNORE_SECURITY);
                        initGridView(friendContext, mGridAdapter);
                    } catch (NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }

        // init grid view
//        LauncherApplication app = (LauncherApplication) getApplicationContext();
        if (mIsFolder) {
            initGridView(mGridAdapter);
//        } else {
//            initGridView(app.mLauncherContext.getFriendContext(), mGridAdapter);
        }

        GridView grid = (GridView) findViewById(R.id.allIconGrid);
        grid.setAdapter(mGridAdapter);
        grid.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = getIntent();
                i.putExtra(Constants.CHANGE_ICON_KEY, mGridAdapter.getItem(position));
                setResult(RESULT_OK, i);
                finish();
            }
        });
    }

    private void initGridView(Context friendContext, AppsAdapter adapter) {
        if (friendContext == null)
            return;

        // get all launcher activity info
        PackageManager manager = getPackageManager();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        final List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
        adapter.clear();

        if (apps != null) {
            final int count = apps.size();
            for (int i = 0; i < count; i++) {
                ResolveInfo info = apps.get(i);

                // find its icon from friend theme packages
                Bitmap friendBitmap = Utilities.retrieveCustomIconFromFile(info.activityInfo, friendContext, null);
                if (friendBitmap != null)
                    adapter.add(friendBitmap);
                
                /*RK_ID: RK_LELAUNCHER . AUT: zhanggx1 . DATE: 2012-02-29 . S*/
//                Bitmap hotseatBitmap = Utilities.retrieveHotseatIconFromFile(info.activityInfo, friendContext, null);
//                if (hotseatBitmap != null)
//                    adapter.add(hotseatBitmap);
                /*RK_ID: RK_LELAUNCHER . AUT: zhanggx1 . DATE: 2012-02-29 . E*/
            }
        } // end if

        adapter.notifyDataSetChanged();
    }

    private void findActivitiesForSkin(ArrayAdapter<CharSequence> adapter) {
        final PackageManager packageManager = getPackageManager();

//        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
//        mainIntent.addCategory(THEME_CATEGORY);
//
//        final List<ResolveInfo> apps = packageManager.queryIntentActivities(mainIntent, 0);
        final List<ResolveInfo> apps = Utilities.findActivitiesForSkin(this);
        if (apps != null) {
            int count = apps.size();
            mPkgMaps.clear();

            /*RK_ID_USE_THEMEVALUE_ICON dining 2012-11-28 S*/
            //if the value is false, don't show the default theme value
            boolean bUse = SettingsValue.getUseDefaultThemeIconValue(this);
            if(bUse){
                /*RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2012-07-04 . S*/
                String defaultTheme = SettingsValue.getDefaultThemeValue(this);
                String defaultLabel = getString(R.string.theme_settings_default_theme);
                adapter.add(defaultLabel);
                mPkgMaps.put(defaultLabel, defaultTheme);
            }
            /*RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2012-07-04 . E*/
            
            /*RK_ID_USE_THEMEVALUE_ICON dining 2012-11-28 E*/
            for (int i = 0; i < count; i++) {
                ResolveInfo app = apps.get(i);
                /*RK_ID: RK_INBULID_THEME_EXCLUDE . AUT: GEECN1 . DATE: 2012-11-09 . S*/
                if(app.activityInfo.packageName.equals(getPackageName())){
                	continue;
                }
                /*RK_ID: RK_INBULID_THEME_EXCLUDE . AUT: GECN1 . DATE: 2012-11-09 . S*/
                CharSequence label = app.loadLabel(packageManager);
                adapter.add(label);

                mPkgMaps.put(label, app.activityInfo.applicationInfo.packageName);
            }
        } // end if (apps != null)
    }

    private void initGridView(AppsAdapter adapter) {
//        final PackageManager packageManager = getPackageManager();

//        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
//        mainIntent.addCategory(THEME_CATEGORY);
//
//        final List<ResolveInfo> apps = packageManager.queryIntentActivities(mainIntent, 0);
        final List<ResolveInfo> apps = Utilities.findActivitiesForSkin(this);
        if (apps != null) {
            int count = apps.size();
            for (int i = 0; i < count; i++) {
                ResolveInfo app = apps.get(i);
                String packageName = app.activityInfo.packageName;

                // get friend context
                try {
                    Context friendContext = this.createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY);
                    Bitmap friendBitmap = Utilities.findBitmapById(this.getResources(),
                            R.drawable.portal_ring_inner_holo, friendContext);
                    if (friendBitmap != null)
                        adapter.add(friendBitmap);
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } // end if (apps != null)

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {

        if (mPkgMaps != null) {
            mPkgMaps.clear();
            mPkgMaps = null;
        }

        if (mGridAdapter != null) {
            mGridAdapter.clear();
            mGridAdapter = null;
        }
        super.onDestroy();
    }

    private class AppsAdapter extends BaseAdapter {
        private final int mGridIconWidth;
        private ArrayList<Bitmap> mGridBitmaps = new ArrayList<Bitmap>();

        public AppsAdapter(Context context) {
            mGridIconWidth = context.getResources().getDimensionPixelSize(R.dimen.letheme_image_adapter_layout_width);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView i;

            if (convertView == null) {
                i = new ImageView(LethemeImagesActivity.this);
                i.setScaleType(ImageView.ScaleType.FIT_CENTER);
                i.setLayoutParams(new GridView.LayoutParams(mGridIconWidth, mGridIconWidth));
            } else {
				if (convertView instanceof ImageView)
					i = (ImageView) convertView;
				else
					i = null;
            }

            Bitmap b = mGridBitmaps.get(position);
            if(i!=null) i.setImageBitmap(b);

            return i;
        }

        public final int getCount() {
            return mGridBitmaps.size();
        }

        public final Bitmap getItem(int position) {
            return mGridBitmaps.get(position);
        }

        public final long getItemId(int position) {
            return position;
        }

        protected final void clear() {
            mGridBitmaps.clear();
        }

        protected final int add(Bitmap b) {
            mGridBitmaps.add(b);
            return getCount();
        }
    }
}
