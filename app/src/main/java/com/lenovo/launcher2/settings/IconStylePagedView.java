/*
 * Copyright (C) 2011
 *
 * AUT: liuli liuli0120@use.com.cn DATE: 2011-12-20
 * add view to display theme preview image
 */

package com.lenovo.launcher2.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.customizer.Debug;
import com.lenovo.launcher2.customizer.FastBitmapDrawable;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.Utilities;

public class IconStylePagedView extends GridView {
    private final Context mContext;

    private GridLayout mIconPages;

    private ArrayList<Drawable> mSingleIconStyleImages;
    /* RK_ID: ICONSTYLE. AUT: liuli1 . DATE: 2012-03-07 . START */
    private String[] mEffectsName;
    private int prefCurrentIconStyle = Integer.MIN_VALUE;
    /* RK_ID: ICONSTYLE. AUT: liuli1 . DATE: 2012-03-07 . END */
    private Drawable nothing = null;

	private List<Drawable> mDrawables;
	
	private boolean hasThemeIconBg = false;
	private List<String> mRealName = null;
	private IconStyleAdapter mIconStyleAdapter;
    public IconStylePagedView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        mSingleIconStyleImages = new ArrayList<Drawable>();
        mRealName = new ArrayList<String>();
        nothing = mContext.getResources().getDrawable(R.drawable.nothing);
    }

    public void setup(GridLayout iconPages, List<Drawable> drawables,  int defaultStyle) {
    	mIconPages = iconPages;
    	mDrawables = drawables;
        prefCurrentIconStyle = defaultStyle;

        updateIconPages();

        mSingleIconStyleImages.clear();
        
        mRealName.clear();
        mEffectsName = this.getResources().getStringArray(R.array.pref_icon_style_choices);
        List<String> list2 = new ArrayList<String>(Arrays.asList(mEffectsName));
    	mRealName.addAll(list2);
        
        Context currThemeContext = null;
        try {
        	String currentTheme = SettingsValue.getThemeValue(getContext());
        	currThemeContext = getContext().createPackageContext(currentTheme, Context.CONTEXT_IGNORE_SECURITY);
        } catch (NameNotFoundException e) {
        	Debug.printException("cannot find the theme -->", e);
        	currThemeContext = null;
        }
        
        /*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-5 S*/
		String themeIconBgName = SettingsValue.THEME_ICON_BG_NAME;
		if(currThemeContext != null && currThemeContext.getPackageName().equals(mContext.getPackageName())){
			themeIconBgName +=SettingsValue.getInbuildThemePostfix();
		}
		/*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-5 E*/
        
        Drawable themeIconBg = Utilities.findDrawableByResourceName(themeIconBgName, currThemeContext);
        if (themeIconBg != null) {
        	hasThemeIconBg = true;
//        	mSingleIconStyleImages.add(themeIconBg);
        	mRealName.add(1, getResources().getString(R.string.theme_icon_bg_title));        	
        }
        for (int i = 0; i < Utilities.ICON_STYLE_COUNT; i++) {
        	/***RK_ID:RK_CHANGE_ICON_SIZE AUT:zhanglz1@lenovo.com. DATE:2013-01-14.S***/       
            Bitmap b = Utilities.getIconStyleImage(i, nothing, mContext);
            /***RK_ID:RK_CHANGE_ICON_SIZE AUT:zhanglz1@lenovo.com. DATE:2013-01-14.E***/ 
            FastBitmapDrawable fbd = new FastBitmapDrawable(b);
            mSingleIconStyleImages.add(fbd);
        }
        if (hasThemeIconBg) {
        	if (mSingleIconStyleImages.size() > 0) {
        		Drawable sample = mSingleIconStyleImages.get(0);
            	int height = sample.getIntrinsicHeight();
            	int themeIconBgH = themeIconBg.getIntrinsicHeight();
            	if (themeIconBgH != height) {
            		int width = sample.getIntrinsicWidth();
            		/*RK_ID: FOR_CHANGE_DRAWABLE_TO_BITMAP. AUT:shenchao1. DATE: 2013-04-26. S*/
//            		Bitmap d = Utilities.drawableToBitmap(themeIconBg);
            		BitmapDrawable bd = (BitmapDrawable) themeIconBg;
            		Bitmap d = bd.getBitmap();
            		/*RK_ID: FOR_CHANGE_DRAWABLE_TO_BITMAP. AUT:shenchao1. DATE: 2013-04-26. E*/
            		themeIconBg = new FastBitmapDrawable(Bitmap.createScaledBitmap(d, width, height, true));
            		d.recycle();
            		d = null;
            	}
        	}
        	mSingleIconStyleImages.add(0, themeIconBg);        	
        }
        mIconStyleAdapter = new IconStyleAdapter(mContext, mSingleIconStyleImages, mRealName);
        IconStylePagedView.this.setAdapter(mIconStyleAdapter);
        mIconStyleAdapter.addMoreContent();
        
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
//        setDataIsReady();

        setMeasuredDimension(width, height);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /*** AUT: zhaoxy . DATE: 2012-04-05 . START***/
    public void reset() {
    	int size = mIconPages.getChildCount();
    	Resources resource = getResources();
        for(int i = 0; i < size; i++) {
            View icon = mIconPages.getChildAt(i);
            BitmapDrawable res = new BitmapDrawable(resource, Utilities.createIconBitmap(mDrawables.get(i), -1, mContext));
            icon.setBackgroundDrawable(res);
        }
		prefCurrentIconStyle = -1;
//		invalidatePageData();
    }
    /*** AUT: zhaoxy . DATE: 2012-04-05 . END***/


    private boolean updateIconPages() {
        if (prefCurrentIconStyle == SettingsValue.THEME_ICON_BG_INDEX) {
            int size = mIconPages.getChildCount();
            Resources resource = getResources();

            if (mThemeBgBitmap[0] == null && mThemeBgBitmap[1] == null)
                getThemeIconBg(mContext);

            for (int i = 0; i < size; i++) {
                View icon = mIconPages.getChildAt(i);
                BitmapDrawable res = new BitmapDrawable(resource, Utilities.createIconBitmap(
                        mDrawables.get(i), mThemeBgBitmap[0], mThemeBgBitmap[1], mThemeBgBitmap[2],
                        mContext));
                icon.setBackgroundDrawable(res);
            }
        }
        return prefCurrentIconStyle == SettingsValue.THEME_ICON_BG_INDEX;
    }

    private Bitmap[] mThemeBgBitmap = new Bitmap[] { null, null, null };

    private void getThemeIconBg(Context context) {
        Context friendContext = null;
        try {
            String currentTheme = SettingsValue.getThemeValue(getContext());
            friendContext = getContext().createPackageContext(currentTheme,
                    Context.CONTEXT_IGNORE_SECURITY);
        } catch (NameNotFoundException e) {
            Debug.printException("cannot find the theme -->", e);
            friendContext = null;
        }

        // follow SettingsValue setThemeIconBg()
        if (friendContext == null) {
            mThemeBgBitmap[0] = null;
            mThemeBgBitmap[1] = null;
            mThemeBgBitmap[2] = null;
        }

        /*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-01 S*/
    	String themeIconBgName = SettingsValue.THEME_ICON_BG_NAME;
    	String themeIconFgName = SettingsValue.THEME_ICON_FG_NAME;
    	String themeIconMaskName = SettingsValue.THEME_ICON_MASK_NAME;
    	String postfixName = SettingsValue.getInbuildThemePostfix();
    	if(friendContext != null && friendContext.getPackageName().equals("com.lenovo.launcher")){
    		themeIconBgName  += postfixName;
    		themeIconFgName +=postfixName;
    		themeIconMaskName +=postfixName;
		}
    	/*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-01 E*/

        Drawable drawable = Utilities.findDrawableByResourceName(themeIconBgName,
                friendContext);
        if (drawable == null) {
            mThemeBgBitmap[0] = null;
            mThemeBgBitmap[1] = null;
            mThemeBgBitmap[2] = null;
        } else {
            mThemeBgBitmap[0] = Utilities.createBitmap(drawable, 0, 0, friendContext);
            drawable = Utilities.findDrawableByResourceName(themeIconFgName,
                    friendContext);
            mThemeBgBitmap[1] = Utilities.createBitmap(drawable, 0, 0, friendContext);
            drawable = Utilities.findDrawableByResourceName(themeIconMaskName,
                    friendContext);
            mThemeBgBitmap[2] = Utilities.createBitmap(drawable, 0, 0, friendContext);
        }
    }

    public int getIndex() {
        return prefCurrentIconStyle;
    }

    public void resetIndex() {
        prefCurrentIconStyle = -1;
    }

    public void destory() {
        if (mIconPages != null) {
            mIconPages.removeAllViews();
            mIconPages = null;
        }
        if (mSingleIconStyleImages != null) {
            mSingleIconStyleImages.clear();
            mSingleIconStyleImages = null;
        }
        if (mDrawables != null) {
            mDrawables.clear();
            mDrawables = null;
        }
        if (mRealName != null) {
            mRealName.clear();
            mRealName = null;
        }
        if (mThemeBgBitmap != null) {
            int size = mThemeBgBitmap.length;
            for (int i = 0; i < size; i++) {
                if (mThemeBgBitmap[i] != null && !mThemeBgBitmap[i].isRecycled()) {
                    mThemeBgBitmap[i].recycle();
                    mThemeBgBitmap[i] = null;
                }
            }
        }
    }

    public void cleanup() {
    	if (mSingleIconStyleImages != null) {
    		mSingleIconStyleImages.clear();
    		mSingleIconStyleImages = null;
    	}
    	if (mDrawables != null) {
    		mDrawables.clear();
    		mDrawables = null;
    	}
    }
    
    
    public class IconStyleAdapter extends BaseAdapter{
    	private ArrayList<Drawable> mAdapterList; 
    	List<String> mName;
    	 private Context mcontext;

    	public IconStyleAdapter(Context context,ArrayList<Drawable> items,List<String> name) {
            mAdapterList = items; //local data
            mName = name;
            mcontext = context;
        }
		@Override
		public int getCount() {
			return (mAdapterList == null) ? 0 :mAdapterList.size();
		}

		@Override
		public Object getItem(int position) {
			return mAdapterList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			if (convertView == null) {
        		convertView = View.inflate(mcontext, R.layout.icon_preview, null);
        	}
			ImageView mImageView = (ImageView) convertView.findViewById(R.id.style_icon);
			mImageView.setImageDrawable(mAdapterList.get(position));
			
			TextView mTextView = (TextView) convertView.findViewById(R.id.style_icon_name);
			mTextView.setText(mName.get(position+1));
			ImageView mSelectIcon = (ImageView) convertView.findViewById(R.id.select_icon);
			Log.i("shen","position is " + position + " and prefCurrentIconStyle is " + prefCurrentIconStyle);
						
			if(!hasThemeIconBg){
				if(position == prefCurrentIconStyle ){
					mSelectIcon.setVisibility(View.VISIBLE);
				}else{
					mSelectIcon.setVisibility(View.INVISIBLE);
				}
				if(position == 0){
					if(prefCurrentIconStyle == -1 ){
						mSelectIcon.setVisibility(View.VISIBLE);
					}else{
						mSelectIcon.setVisibility(View.INVISIBLE);
					}
				}
				
				
				convertView.setTag(position);
			}else{
				if(position == 0){
					convertView.setTag(-2);
					if(prefCurrentIconStyle == -2 ){
						mSelectIcon.setVisibility(View.VISIBLE);
					}else{
						mSelectIcon.setVisibility(View.INVISIBLE);
					}
				}else{
					convertView.setTag(position - 1);
					if(position == 1){
						if(prefCurrentIconStyle == -1 ){
							mSelectIcon.setVisibility(View.VISIBLE);
						}else{
							mSelectIcon.setVisibility(View.INVISIBLE);
						}
					}else{
						if(position == (prefCurrentIconStyle + 1)){
							mSelectIcon.setVisibility(View.VISIBLE);
						}else{
							mSelectIcon.setVisibility(View.INVISIBLE);
						}
					}
				}
			}
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Log.i("shen","0: prefCurrentIconStyle is " + prefCurrentIconStyle);
					int size = mIconPages.getChildCount();					
			    	prefCurrentIconStyle = (Integer) v.getTag();
			    	Log.i("shen","0: prefCurrentIconStyle is " + prefCurrentIconStyle);
			    	Resources resource = getResources();
			    	/*RK_ID: RK_ICONSTYLE_SET . AUT: shenchao1. DATE: 2013-01-09 . S*/
			    	if(prefCurrentIconStyle == 0){
			    	    for(int i = 0; i < size; i++) {
			                View icon = mIconPages.getChildAt(i);
			                BitmapDrawable res = new BitmapDrawable(resource, Utilities.createIconBitmap(mDrawables.get(i), -1, mContext));
			                icon.setBackgroundDrawable(res);
			            }
			    		prefCurrentIconStyle = -1;
			    	}
			    	else{
			    		if (!updateIconPages()) {
			    			for(int i = 0; i < size; i++) {
			    				View icon = mIconPages.getChildAt(i);
			    				BitmapDrawable res = new BitmapDrawable(resource, Utilities.createIconBitmap(mDrawables.get(i), prefCurrentIconStyle, mContext));
			    				icon.setBackgroundDrawable(res);
			    			}
			    		}
			    	}
			    	addMoreContent();
				}
			});

			return convertView;
		}
		
		 public void addMoreContent() {
	         notifyDataSetChanged();
	        }
    }
}
