/*
 * Copyright (C) 2011
 *
 * AUT: liuli liuli0120@use.com.cn DATE: 2011-12-20
 * add view to display theme preview image
 */

package com.lenovo.launcher2.settings;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.commonui.PagedView;
import com.lenovo.launcher2.commonui.PagedViewGridLayout;
import com.lenovo.launcher2.customizer.Debug;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.Utilities;

public class ThemePagedView extends PagedView implements OnClickListener {
    private final LayoutInflater mLayoutInflater;
    /*PK_ID:THEME ADPTER ALL DEVICES AUTH:GECN1 DATE:2012-12-7 S*/
    private boolean mIsThemeDataReady = true;
    /*PK_ID:THEME ADPTER ALL DEVICES AUTH:GECN1 DATE:2012-12-7 E*/

    // Dimens
    private int mContentWidth;
    private int mContentHeight;
    private int mThemeCountX, mThemeCountY;
    private int mThemeWidthGap, mThemeHeightGap;
    /*** RK_ID: SCREEN_SUPPORT.  AUT: zhaoxy . DATE: 2012-06-14 . START***/
    private int ON_BITMAP_LEFT_PADDING = 70;
    private int ON_BITMAP_RIGHT_PADDING = 140;
    /*** RK_ID: SCREEN_SUPPORT.  AUT: zhaoxy . DATE: 2012-06-14 . END***/

    // child of views
    private LinearLayout mScrollIndicator;
    private ValueAnimator mScrollIndicatorAnimator;
    private ImageView mLightBar;
    /*** RK_ID: SCREEN_SUPPORT.  AUT: zhaoxy . DATE: 2012-06-14 . START***/
    /*PK_ID:THEME ADPTER ALL DEVICES AUTH:GECN1 DATE:2012-12-7 S*/
//    private Drawable on;
    /*PK_ID:THEME ADPTER ALL DEVICES AUTH:GECN1 DATE:2012-12-7 E*/
    /*** RK_ID: SCREEN_SUPPORT.  AUT: zhaoxy . DATE: 2012-06-14 . END***/

    // indicator dimens
    private int sIndicatorWidth;
    private int mLightbarLeft;

    private int mNumPages;
    private int sCurrentPage = 0;
    private String mCurrentTheme;
    private String sDefaultAndroidTheme;
    /*** fixbug 167086 . AUT: zhaoxy . DATE: 2012-07-10. START***/
    private int lastPage = 0;
    /*** fixbug 167086 . AUT: zhaoxy . DATE: 2012-07-10. END***/

    private ArrayList<Object> mThemes;
    
    private Context mFriendContext = null;
    
    private static final int[] DEFAULT_PREVIEWS = new int[] {R.drawable.themepreview,
    	R.drawable.themepreview_1,
    	R.drawable.themepreview_2};
    /*RK_VERSION_WW  dining 2012-10-22 S*/
    /*
    private static final int[] DEFAULT_PREVIEWS_WW = new int[] {R.drawable.themepreview_ww,
    	R.drawable.themepreview_1_ww,
    	R.drawable.themepreview_2_ww};
    	*/
    /*RK_VERSION_WW  dining 2012-10-22 E*/
    public ThemePagedView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ThemePagedView, 0, 0);
        mThemeWidthGap = a.getDimensionPixelSize(R.styleable.ThemePagedView_previewCellWidthGap, 6);
        mThemeHeightGap = a.getDimensionPixelSize(R.styleable.ThemePagedView_previewCellHeightGap, 12);
        mThemeCountX = a.getInt(R.styleable.ThemePagedView_previewCountX, 3);
        mThemeCountY = a.getInt(R.styleable.ThemePagedView_previewCountY, 2);
        a.recycle();

        mLayoutInflater = LayoutInflater.from(context);

        mThemes = new ArrayList<Object>();
        sDefaultAndroidTheme = SettingsValue.getDefaultAndroidTheme(context);
    }

    /*PK_ID:THEME ADPATER ALL DEVICES AUTH:GECN1 DATE:2012-12-7 S*/
    @Override
	public void setDataIsReady() {
		// TODO Auto-generated method stub
    	super.setDataIsReady();
    	mIsThemeDataReady = true;
	}

	@Override
	public boolean isDataReady() {
		// TODO Auto-generated method stub
		return mIsThemeDataReady && super.isDataReady();
	}
	
	public void setDataDirty(){
		mIsThemeDataReady = false;
	}
    /*PK_ID:THEME ADPATER ALL DEVICES AUTH:GECN1 DATE:2012-12-7 S*/
    public void setup(int style, int w, int h, int wGap, int hGap, Callbacks ts) {
        if (style == SettingsValue.THEME_SETTING_STYLE_SINGLE) {
            lastPage = getCurrentPage();
        }
    	removeAllViews();
    	
        mThemeCountX = w;
        mThemeCountY = h;
        mThemeWidthGap = wGap;
        mThemeHeightGap = hGap;
        mCallbacks = new WeakReference<Callbacks>(ts);
        /*** RK_ID: SCREEN_SUPPORT.  AUT: zhaoxy . DATE: 2012-06-14 . START***/
        //Resources resources = mContext.getResources();
    /*PK_ID:THEME ADPTER ALL DEVICES AUTH:GECN1 DATE:2012-12-7 S*/
//        int imageWidth = resources.getDimensionPixelSize(R.dimen.theme_preview_image_width);
//        int imageHeight = resources.getDimensionPixelSize(R.dimen.theme_preview_iamge_height);
//        on = resources.getDrawable(R.drawable.mytheme_theme_on);
//        if (on != null) {
//            ON_BITMAP_LEFT_PADDING = imageWidth - on.getIntrinsicWidth();
//            ON_BITMAP_RIGHT_PADDING = imageHeight - on.getIntrinsicHeight();
//        }
    /*PK_ID:THEME ADPTER ALL DEVICES AUTH:GECN1 DATE:2012-12-7 E/
        /*** RK_ID: SCREEN_SUPPORT.  AUT: zhaoxy . DATE: 2012-06-14 . END***/

        if (style == SettingsValue.THEME_SETTING_PREVIEW) {
        	// init data map
            mThemes.clear();
            String defaultTheme = SettingsValue.getDefaultThemeValue(mContext);
            if (defaultTheme.equals(sDefaultAndroidTheme)) {
            	 /*RK_VERSION_WW  dining 2012-10-22 S*/
                //get the preview images for version_WW
            	mThemes.add(R.drawable.themepreview);
            	/*
            	if(GlobalDefine.getVerisonWWConfiguration(mContext)){
            		mThemes.add(R.drawable.themepreview_ww);
            	}else{
                    mThemes.add(R.drawable.themepreview);
            	}
            	*/
            	/*RK_VERSION_WW  dining 2012-10-22 E*/
            } else {
            	mThemes.add(defaultTheme);
            }
            List<ResolveInfo> installedSkins = Utilities.findActivitiesForSkin(getContext());
            if (installedSkins != null) {
	            for (ResolveInfo skin : installedSkins) {
	                mThemes.add(skin.activityInfo.packageName);
	            }
            }
            mNumPages = (int) Math.ceil(mThemes.size() / (float) (mThemeCountX * mThemeCountY));
            /*** fixbug 167086 . AUT: zhaoxy . DATE: 2012-07-10. START***/
            if (lastPage >= mNumPages) {
                lastPage = mNumPages - 1;
            }
            sCurrentPage = lastPage;
            /*** fixbug 167086 . AUT: zhaoxy . DATE: 2012-07-10. END***/
        } else if (style == SettingsValue.THEME_SETTING_STYLE_SINGLE) {
        	String clickTheme = getTag().toString();
        	if (clickTheme.equals(sDefaultAndroidTheme)) {
        		mNumPages = DEFAULT_PREVIEWS.length;
        	} else {
        		String[] previewImages = null;
        		/*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-5 S*/
        		String  previewName="config_theme_previews";
        		if(mFriendContext.getPackageName().equals(mContext.getPackageName())){
        			previewName = previewName + SettingsValue.getInbuildThemePostfix();
        		}
        		/*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-5 S*/
        		previewImages = Utilities.findStringArrayByResourceName(previewName, mFriendContext);
        		if (previewImages == null) {
        			mNumPages = 1;
        		} else {
        			mNumPages = (int) Math.ceil(previewImages.length / (float) (mThemeCountX * mThemeCountY));
        		}
        	}
            /*** fixbug 167086 . AUT: zhaoxy . DATE: 2012-07-10. START***/
            sCurrentPage = 0;
            /*** fixbug 167086 . AUT: zhaoxy . DATE: 2012-07-10. END***/
        }

        mCurrentTheme = SettingsValue.getThemeValue(getContext());
        //sCurrentPage = 0; fixbug 167086
        mScrollIndicator = null;
        mLightBar = null;
    /*PK_ID:THEME ADPTER ALL DEVICES AUTH:GECN1 DATE:2012-12-7 S*/
        setDataDirty();
        if(!this.isDataReady()  ){
       	 requestLayout();
        }
    /*PK_ID:THEME ADPTER ALL DEVICES AUTH:GECN1 DATE:2012-12-7 E*/
    }
    
     /*PK_ID:THEME ADPTER ALL DEVICES AUTH:GECN1 DATE:2012-12-7 S*/
    protected void onDataReady(int width,int height){
    	//在此方法中我们来获取GridLayout的height &width
    	//此时应该注意onDataReady方法是在onMeasure方法中调用的，否则获取的width &height为0
    	//而且invalidatePageData()方法必须在onDataReady中调用
    	
    	//获取GridLayout的width &height
    	
//    	mSpacingGridLayout.setGap(mPageLayoutWidthGap, mPageLayoutHeightGap);
//    	mSpacingGridLayout.setPadding(mPageLayoutPaddingLeft, mPageLayoutPaddingTop,
//                mPageLayoutPaddingRight, mPageLayoutPaddingBottom);
//    	
    	Log.d("themepreviewsize", "ThemePagedView-->onDataReady excute ");
       	mContentWidth = width;
       	mContentHeight  = height;
      	 Log.d("profile", "--------------------------------------mContentHeight = "+  mContentHeight);

    	invalidatePageData(sCurrentPage);
    	
        setCurrentPage(sCurrentPage);
        updateScrollingIndicator(sCurrentPage, true);
    }
    /*PK_ID:THEME ADPTER ALL DEVICES AUTH:GECN1 DATE:2012-12-7 E*/

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
    /*PK_ID:THEME ADPTER ALL DEVICES AUTH:GECN1 DATE:2012-12-7 S*/

        if(!isDataReady() ){
    	   setDataIsReady();
    	   int height = MeasureSpec.getSize(heightMeasureSpec)-(int)mContext.getResources().getDimension(R.dimen.theme_button_bg_height);
    	   setMeasuredDimension(width, height);
    	   onDataReady(width, height);
        }
    /*PK_ID:THEME ADPTER ALL DEVICES AUTH:GECN1 DATE:2012-12-7 E*/
       
    }

    @Override
    public void syncPageItems(int page, boolean immediate) {
        final Callbacks callbacks = mCallbacks.get();
        if (callbacks == null) {
            return;
        }
        if (callbacks.getStyle() == SettingsValue.THEME_SETTING_STYLE_SINGLE) {
        	syncSingleThemePage(page, immediate);
        } else {
        	syncPreviewPage(page, immediate);
        }
    }

    private void syncSingleThemePage(int page, boolean immediate) {
    	boolean isDefault = sDefaultAndroidTheme.equals(getTag().toString());
    	if (mFriendContext == null && !isDefault) {
    		return;
    	}
        int numItemsPerPage = mThemeCountX * mThemeCountY;

        int imageWidth = getContext().getResources().getDimensionPixelSize(R.dimen.theme_single_image_width);
        int imageHeight = getContext().getResources().getDimensionPixelSize(R.dimen.theme_single_image_height);

        // Prepare single theme item to load
        int offset = page * numItemsPerPage;
                
        PagedViewGridLayout layout = (PagedViewGridLayout) getPageAt(page);
        layout.setColumnCount(layout.getCellCountX());
        layout.removeAllViews();

        
        if (isDefault) {
        	for (int i = offset; i < Math.min(offset + numItemsPerPage, DEFAULT_PREVIEWS.length); ++i) {
        		int index = i - offset;
        		/*RK_VERSION_WW  dining 2012-10-22 S*/
        		//get the preview images for version_WW
        		int previewName = DEFAULT_PREVIEWS[i];
        		/*
            	if(GlobalDefine.getVerisonWWConfiguration(mContext)){
            		previewName = DEFAULT_PREVIEWS_WW[i];
            	}else{
            		previewName = DEFAULT_PREVIEWS[i];
            	}
            	*/
            	/*RK_VERSION_WW  dining 2012-10-22 E*/
	            Drawable rawInfo = getResources().getDrawable(previewName);
	            int ix = index % mThemeCountX;
	            int iy = index / mThemeCountX;
	            addToSingleView(rawInfo, imageWidth, imageHeight, ix, iy, layout);
	        }
        	layout.createHardwareLayer();
            invalidate();
            forceUpdateAdjacentPagesAlpha();
            return;
        }
        
        String[] previewImages = null;
        /*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-5 S*/
        String previewNameString = "config_theme_previews";
        boolean inbulidTheme = false;
        String inbuildPostfix =  SettingsValue.getInbuildThemePostfix();
        if(mFriendContext.getPackageName().equals(mContext.getPackageName())){
        	previewNameString =previewNameString +inbuildPostfix;
        	inbulidTheme = true;
        }
        previewImages = Utilities.findStringArrayByResourceName(previewNameString, mFriendContext);
        if (previewImages == null) {
        	int ix = 0;
        	int iy = 0;
        	String previewName = "themepreview";
        	if(inbulidTheme){
        		previewName+=inbuildPostfix;
        	}
        	Log.d("thempaged", "ThemePagedView-->syncSingleThemePage():previewName1= " + previewName);
        	Drawable rawInfo = Utilities.findDrawableByResourceName("themepreview", mFriendContext);
        	if (rawInfo == null) {
        		rawInfo = getContext().getResources().getDrawable(R.drawable.ic_launcher_theme_shortcut);
        	}
        	addToSingleView(rawInfo, imageWidth, imageHeight, ix, iy, layout);
        } else {        
        	for (int i = offset; i < Math.min(offset + numItemsPerPage, previewImages.length); ++i) {
        		int index = i - offset;
	        	String previewName = previewImages[i];
	            Drawable rawInfo = Utilities.findDrawableByResourceName(previewName, mFriendContext);
	            if (rawInfo == null) {
	        		rawInfo = getContext().getResources().getDrawable(R.drawable.ic_launcher_theme_shortcut);
	        	}
	            int ix = index % mThemeCountX;
	            int iy = index / mThemeCountX;
	            addToSingleView(rawInfo, imageWidth, imageHeight, ix, iy, layout);
	        }
        }

        layout.createHardwareLayer();
        invalidate();
        forceUpdateAdjacentPagesAlpha();
    }
    
    private void addToSingleView(Drawable rawInfo, 
    		int imageWidth, 
    		int imageHeight, 
    		int ix,
    		int iy,
    		PagedViewGridLayout parent) {
    	final ImageView previewImage = new ImageView(getContext());
        previewImage.setLayoutParams(new LayoutParams(imageWidth, imageHeight));
        previewImage.setBackgroundDrawable(rawInfo);

        // Layout each preview
        
        GridLayout.LayoutParams lp = new GridLayout.LayoutParams(GridLayout.spec(iy, GridLayout.CENTER), GridLayout
                .spec(ix, GridLayout.TOP));
        lp.width = imageWidth;
        lp.height = imageHeight;
        lp.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);
        if (ix > 0)
            lp.leftMargin = mThemeWidthGap;
        if (iy > 0) {
            lp.topMargin = mThemeHeightGap;
        } else {
        	lp.topMargin = 0;
        }
        parent.addView(previewImage, lp);
    }

    private void syncPreviewPage(int page, boolean immediate) {
    	Resources fRes;
    	AssetManager am = null;
    	
        int numItemsPerPage = mThemeCountX * mThemeCountY;

        // Calculate the dimensions of each cell we are giving to each theme
        ArrayList<Object> items = new ArrayList<Object>();
        int offset = page * numItemsPerPage;
        for (int i = offset; i < Math.min(offset + numItemsPerPage, mThemes.size()); ++i) {
            items.add(mThemes.get(i));
        }

        PagedViewGridLayout layout = (PagedViewGridLayout) getPageAt(page);
        layout.setColumnCount(layout.getCellCountX());
        layout.removeAllViews();
        
        int cellWidth =( mContentWidth-  (mThemeCountX-1 ) * mThemeWidthGap-mPageLayoutPaddingLeft-mPageLayoutPaddingRight ) /mThemeCountX;
        int cellHeight= (mContentHeight-(mThemeCountY-1) * mThemeHeightGap-mPageLayoutPaddingTop-mPageLayoutPaddingBottom )/mThemeCountY;

        Resources res = getContext().getResources();

        for (int i = 0; i < items.size(); i++) {
            Object rawInfo = items.get(i);
            final LinearLayout preview = (LinearLayout) mLayoutInflater.inflate(R.layout.theme_preview, layout, false);
            final ImageView previewImage = (ImageView) preview.findViewById(R.id.theme_preview);
           /*PK_ID:THEME ADPTER ALL DEVICES AUTH:GECN1 DATE:2012-12-7 S*/
            final ImageView SelectedThemeImage = (ImageView) preview.findViewById(R.id.theme_selected);
            /*PK_ID:THEME ADPTER ALL DEVICES AUTH:GECN1 DATE:2012-12-7 E*/
            final TextView themeLabel = (TextView) preview.findViewById(R.id.theme_label);
            preview.setOnClickListener(this);

            if (rawInfo instanceof Integer) {
                boolean isOn = mCurrentTheme.equals(sDefaultAndroidTheme);

                /*RK_VERSION_WW  dining 2012-10-22 S*/
                //get the preview images for version_WW
                setImageLayer(res.getDrawable(R.drawable.themepreview), previewImage, isOn,SelectedThemeImage);
                /*
            	if(GlobalDefine.getVerisonWWConfiguration(mContext)){
                    
            		setImageLayer(res.getDrawable(R.drawable.themepreview_ww), previewImage, isOn,SelectedThemeImage);
                   
            	}else{
                    
                    setImageLayer(res.getDrawable(R.drawable.themepreview), previewImage, isOn,SelectedThemeImage);
                    
            	}
            	*/
            	/*RK_VERSION_WW  dining 2012-10-22 E*/
                String label = res.getString(R.string.theme_settings_default_theme);
                themeLabel.setText(label);

                preview.setTag(sDefaultAndroidTheme);
            } else if (rawInfo instanceof String) {
                try {
                	final String friendPkg = rawInfo.toString();
                	am = new AssetManager();
                	String srcDir = getContext().getPackageManager().getApplicationInfo(friendPkg, 0).sourceDir;
                    am.addAssetPath(srcDir);
                    
                	fRes = new Resources(am, res.getDisplayMetrics(), res.getConfiguration());
                    boolean isOn = mCurrentTheme.equals(friendPkg);
                    
                    if (!isOn) {
                    	isOn = mCurrentTheme.equals(sDefaultAndroidTheme)
                    			&& friendPkg.equals(SettingsValue.getDefaultThemeValue(getContext()));
                    }
                    int resID =0;
                    String themPreviewName = "themepreview";
                    if(friendPkg.equals(mContext.getPackageName())){
                    	resID = fRes.getIdentifier(themPreviewName+SettingsValue.getInbuildThemePostfix(), "drawable", friendPkg);
                    }else{
                    	resID = fRes.getIdentifier(themPreviewName, "drawable", friendPkg);
                    }
                    
                    Drawable d = null;
                    if (resID == 0) {
                    	d = res.getDrawable(R.drawable.ic_launcher_theme_shortcut);
                    } else {
                        d = fRes.getDrawable(resID);
                    }
                    /*PK_ID:THEME ADPTER ALL DEVICES AUTH:GECN1 DATE:2012-12-7 S*/
                    setImageLayer(d, previewImage, isOn,SelectedThemeImage);
                   /*PK_ID:THEME ADPTER ALL DEVICES AUTH:GECN1 DATE:2012-12-7 E*/
                    if (mThemes.indexOf(friendPkg) == 0) {
                    	String label = res.getString(R.string.theme_settings_default_theme);
                        themeLabel.setText(label);
                    } else {
                        int strID = fRes.getIdentifier("app_name", "string", friendPkg);
                        String s = null;
                        if (strID == 0) {
                        	s = res.getString(R.string.unknow_theme_name);
                        } else {
                        	s = fRes.getString(strID);
                        }                                            
                        themeLabel.setText(s); 
                    }
                    
                    preview.setTag(friendPkg);
                    am.close();
                    am = null;
                    fRes = null;
                } catch (NameNotFoundException e) {
                	Debug.printException("ThemePagedView->syncPreviewPage create friendContext failed.", e);
//                    e.printStackTrace();
                }
            }

            // Layout each preview
            int ix = i % mThemeCountX;
            int iy = i / mThemeCountX;
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams(GridLayout.spec(iy, GridLayout.LEFT), GridLayout
                    .spec(ix, GridLayout.TOP));
            /*RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2011-01-30 . S*/
//            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;//old: cellWidth;
//            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;//old: cellHeight;
//          if (ix > 0)
           /*PK_ID:THEME ADPTER ALL DEVICES AUTH:GECN1 DATE:2012-12-7 S*/
            if (ix == 0) {
            	lp.leftMargin = 0;
            } else {
            	lp.leftMargin = mThemeWidthGap;
            }
          
          lp.width = cellWidth;
          lp.height = cellHeight;
             /*PK_ID:THEME ADPTER ALL DEVICES AUTH:GECN1 DATE:2012-12-7 E*/
            /*RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2011-01-30 . E*/                
            if (iy > 0)
                lp.topMargin = mThemeHeightGap;
            lp.setGravity(Gravity.TOP | Gravity.LEFT);
            layout.addView(preview, lp);
        }

        layout.createHardwareLayer();
        invalidate();
        forceUpdateAdjacentPagesAlpha();

    }
    /*PK_ID:THEME ADPTER ALL DEVICES AUTH:GECN1 DATE:2012-12-7 S*/
    private void setImageLayer(Drawable d, ImageView previewImage, boolean isCurrent,ImageView themeSelectedView) {
        Drawable bg = getContext().getResources().getDrawable(R.drawable.singletheme_bg);

        LayerDrawable ld = null;
        if (isCurrent) {
            Drawable[] array = new Drawable[2];
            array[0] = bg;
            array[1] = d;
            themeSelectedView.setVisibility(View.VISIBLE);

            ld = new LayerDrawable(array);
            ld.setLayerInset(0, 1, 1, 1, 1);
            ld.setLayerInset(1, 0, 0, 0, 0);
            /*** RK_ID: SCREEN_SUPPORT.  AUT: zhaoxy . DATE: 2012-06-14 . START***/
//            ld.setLayerInset(2, ON_BITMAP_LEFT_PADDING, ON_BITMAP_RIGHT_PADDING, 0, 0);
            /*** RK_ID: SCREEN_SUPPORT.  AUT: zhaoxy . DATE: 2012-06-14 . END***/
        } else {
            Drawable[] array = new Drawable[2];
            array[0] = bg;
            array[1] = d;
            themeSelectedView.setVisibility(View.GONE);
            ld = new LayerDrawable(array);
            ld.setLayerInset(0, 1, 1, 1, 1);
            ld.setLayerInset(1, 0, 0, 0, 0);
        }

        if (d != null)
            previewImage.setBackgroundDrawable(ld);
    }
    /*PK_ID:THEME ADPTER ALL DEVICES AUTH:GECN1 DATE:2012-12-7E*/

    @Override
    public void syncPages() {
        removeAllViews();

        Context context = getContext();
        for (int j = 0; j < mNumPages; ++j) {
            PagedViewGridLayout layout = new PagedViewGridLayout(context, mThemeCountX, mThemeCountY);
            setupPage(layout);
            addView(layout, new PagedViewGridLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }
    }

    @Override
    protected void snapToPage(int whichPage, int delta, int duration) {
        super.snapToPage(whichPage, delta, duration);
        updateScrollingIndicator(whichPage, false);
    }

    protected void updateScrollingIndicator(int page, boolean immediately) {
        if (getChildCount() < 1)
            return;

        getScrollingIndicator2();
        if (mLightBar != null) {
            // Fade the indicator in
            updateScrollingIndicatorPosition(page);
            if (mScrollIndicatorAnimator != null) {
                mScrollIndicatorAnimator.cancel();
            }
            if (immediately) {
                mLightBar.setAlpha(1f);
            } else {
                mScrollIndicatorAnimator = ObjectAnimator.ofFloat(mLightBar, "alpha", 1f);
                mScrollIndicatorAnimator.setDuration(sScrollIndicatorFadeInDuration);
                mScrollIndicatorAnimator.start();
            }
        }

    }

    private View getScrollingIndicator2() {
    	int indicatorPadding = getContext().getResources().getDimensionPixelSize(R.dimen.indicator_padding);
        if (mScrollIndicator == null) {
            ViewGroup parent = (ViewGroup) getParent();
            mScrollIndicator = (LinearLayout) (parent.findViewById(R.id.themes_paged_view_indicator));
            mScrollIndicator.removeAllViews();

            for (int i = 0; i < mNumPages; ++i) {
                final ImageView iv = new ImageView(getContext());
                iv.setImageResource(R.drawable.setting_dotindicator_normalbar);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
                lp.leftMargin = indicatorPadding;
                lp.rightMargin = indicatorPadding;
                lp.gravity = Gravity.CENTER_HORIZONTAL;
                mScrollIndicator.addView(iv, lp);
            }

            mScrollIndicator.setVisibility(View.VISIBLE);
        }

        if (mLightBar == null) {
            ViewGroup parent = (ViewGroup) getParent();
            mLightBar = (ImageView) parent.findViewById(R.id.indicator_lightbar);

            int left = 0;
            int size = mScrollIndicator.getChildCount();
            int center = mContentWidth / 2;
            sIndicatorWidth = ((ImageView) mScrollIndicator.getChildAt(0)).getDrawable().getIntrinsicWidth()
            		+ 2 * indicatorPadding;
            int ix = size / 2;
            int iy = size % 2;

            if (iy == 0) {
                // ou shu
                left = center - ix * sIndicatorWidth + indicatorPadding;
            } else if (iy == 1) {
                // ji shu
                left = center - ix * sIndicatorWidth - sIndicatorWidth / 2 + indicatorPadding;
            }
            mLightbarLeft = left;
        }

        return mScrollIndicator;
    }

    private void updateScrollingIndicatorPosition(int page) {
        mLightBar.setAlpha(0f);
        /*PK_ID:INDICATOR_POSITION_DISPLAY AUTH:GECN1 DATE:2012-12-11 S*/
        mLightBar.setTranslationX(sIndicatorWidth * page + mLightbarLeft);
        /*PK_ID:INDICATOR_POSITION_DISPLAY AUTH:GECN1 DATE:2012-12-11 S*/    }

    private void setupPage(PagedViewGridLayout layout) {
        layout.setPadding(mPageLayoutPaddingLeft, mPageLayoutPaddingTop, mPageLayoutPaddingRight,
                mPageLayoutPaddingBottom);

        int widthSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.AT_MOST);
        int heightSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.AT_MOST);

        // modify by liuli1, fix bug 171285
//        mContentWidth = getResources().getDisplayMetrics().widthPixels;
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        mContentWidth = Math.min(width, height);

        layout.setMinimumWidth(mContentWidth);
        layout.measure(widthSpec, heightSpec);
    }

    @Override
    public void onClick(View v) {
    	String label = null;
        String s = v.getTag().toString();
        final Callbacks ballbacks = mCallbacks.get();
        if (ballbacks == null) {
            return;
        }
        if (s.equals(sDefaultAndroidTheme)) {
        	mFriendContext = null;
            setTag(s);
            ballbacks.setStyle(SettingsValue.THEME_SETTING_STYLE_SINGLE, getContext()
                    .getString(R.string.theme_settings_default_theme));
        } else {
            try {
            	mFriendContext = getContext().createPackageContext(s, Context.CONTEXT_IGNORE_SECURITY);
                int strID = mFriendContext.getResources().getIdentifier("app_name", "string", s);
            	
                if (s.equals(SettingsValue.getDefaultThemeValue(getContext()))) {
                	label = getContext().getString(R.string.theme_settings_default_theme);
                } else {
                	if (strID == 0) {
                		label = getContext().getString(R.string.unknow_theme_name);
                    } else {
                        label = mFriendContext.getString(strID);
                    }
                }

                setTag(s);
                ballbacks.setStyle(SettingsValue.THEME_SETTING_STYLE_SINGLE, label);
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        } // end if-else
    }

    private WeakReference<Callbacks> mCallbacks;

    public interface Callbacks {
        public int getStyle();

        public void setStyle(int style, String label);
    }

    public void clearResources() {
    	mFriendContext = null;
        this.mThemes.clear();
        mThemes = null;

        this.removeAllViews();
        mCallbacks.clear();
        mCallbacks = null;
    }

}
