package com.lenovo.launcher.components.XAllAppFace;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import android.os.SystemClock;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;
import com.lenovo.launcher2.commoninterface.ItemInfo;
import com.lenovo.launcher2.commoninterface.LauncherService;
import com.lenovo.launcher2.customizer.Debug.R5;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.senior.utilities.Utilities;

public class XWallpaperPagedView extends XPagedView{
    
    private IBinder mWindowToken;
    /*** RK_ID: RK_FULLSCREEN_FOLDER  AUT: zhaoxy . DATE: 2012-05-23 . START***/
    public boolean isdraw = false;
    /*** RK_ID: RK_FULLSCREEN_FOLDER  AUT: zhaoxy . DATE: 2012-05-23 . END***/
    public boolean enableScroll = true;
    private static final String CELLLAYOUT_COUNT = "com.lenovo.launcher2.celllayoutCount";
    private static boolean mDebug = false;
    public XWallpaperPagedView(XContext context, RectF pageRect) {
        super(context, pageRect);
        mWallpaperManager = WallpaperManager.getInstance(context.getContext());
        mWallpaperOffset = new WallpaperOffsetInterpolator();
        if (!LauncherApplication.isScreenLarge()) {
            retrieveWorkspaceExtraSettings();
        }
        
        setLoop(SettingsValue.isWorkspaceLoop(context.getContext()));
        setWallpaperDimension();
        caculateData();
    }
    
    @Override
    public void draw(IDisplayProcess canvas) {
        if (mScroll || mUpdateWallpaperOffsetImmediately)
        {
//            R2.echo("XWallpaperPagedView draw mPage"  + mCurrentPage);
            if (Float.compare(mOffsetX, 0) != 0)
            {
            	computeScrollHelper(canvas);
            	syncWallpaperOffsetWithScroll();
                mUpdateWallpaperOffsetImmediately = true;
                updateWallpaperOffsets();
            } 
            
            super.draw(canvas);
        }
        else
        {            
            /*** RK_ID: RK_FULLSCREEN_FOLDER  AUT: zhaoxy . DATE: 2012-05-23 . START***/
            //dranWallpaperAnimation(canvas);
            /*** RK_ID: RK_FULLSCREEN_FOLDER  AUT: zhaoxy . DATE: 2012-05-23 . END***/
            super.draw(canvas); 
        }        
    }
    
    int mWallpaperWidth;
    int mWallpaperHeight;
    WallpaperOffsetInterpolator mWallpaperOffset;
    boolean mUpdateWallpaperOffsetImmediately = true;
    private int mDisplayWidth;
    private int mDisplayHeight;
    private int mWallpaperTravelWidth;
    private final WallpaperManager mWallpaperManager;
    private static final float WALLPAPER_SCREENS_SPAN = 2f;
    private boolean mIsStaticWallpaper;
    private float mWallpaperScrollRatio = 1.0f;
    protected float mLayoutScale = 1.0f;
    
    class WallpaperOffsetInterpolator {
        float mFinalHorizontalWallpaperOffset = 0.0f;
        float mFinalVerticalWallpaperOffset = 0.5f;
        float mHorizontalWallpaperOffset = 0.0f;
        float mVerticalWallpaperOffset = 0.5f;
        long mLastWallpaperOffsetUpdateTime;
        boolean mIsMovingFast;
        boolean mOverrideHorizontalCatchupConstant;
        float mHorizontalCatchupConstant = 0.35f;
        float mVerticalCatchupConstant = 0.35f;

        public WallpaperOffsetInterpolator() {
        }

        public void setOverrideHorizontalCatchupConstant(boolean override) {
            mOverrideHorizontalCatchupConstant = override;
        }

        public void setHorizontalCatchupConstant(float f) {
            mHorizontalCatchupConstant = f;
        }

        public void setVerticalCatchupConstant(float f) {
            mVerticalCatchupConstant = f;
        }

        public boolean computeScrollOffset() {
            if (Float.compare(mHorizontalWallpaperOffset, mFinalHorizontalWallpaperOffset) == 0 &&
                    Float.compare(mVerticalWallpaperOffset, mFinalVerticalWallpaperOffset) == 0) {
                mIsMovingFast = false;
                if (mDebug)R5.echo("computeScrollOffset false mHorizontalWallpaperOffset = " + mHorizontalWallpaperOffset
                        + "mFinalHorizontalWallpaperOffset = " + mFinalHorizontalWallpaperOffset);
                return false;
            }
            boolean isLandscape = mDisplayWidth > mDisplayHeight;

            long currentTime = System.currentTimeMillis();
            long timeSinceLastUpdate = currentTime - mLastWallpaperOffsetUpdateTime;
            timeSinceLastUpdate = Math.min((long) (1000/30f), timeSinceLastUpdate);
            timeSinceLastUpdate = Math.max(1L, timeSinceLastUpdate);

            float xdiff = Math.abs(mFinalHorizontalWallpaperOffset - mHorizontalWallpaperOffset);
            if (!mIsMovingFast && xdiff > 0.07) {
                mIsMovingFast = true;
            }

            float fractionToCatchUpIn1MsHorizontal;
            if (mOverrideHorizontalCatchupConstant) {
                fractionToCatchUpIn1MsHorizontal = mHorizontalCatchupConstant;
            } else if (mIsMovingFast) {
                fractionToCatchUpIn1MsHorizontal = isLandscape ? 0.5f : 0.75f;
            } else {
                // slow
                fractionToCatchUpIn1MsHorizontal = isLandscape ? 0.27f : 0.5f;
            }
            float fractionToCatchUpIn1MsVertical = mVerticalCatchupConstant;

            fractionToCatchUpIn1MsHorizontal /= 33f;
            fractionToCatchUpIn1MsVertical /= 33f;

            final float UPDATE_THRESHOLD = 0.00001f;
            float hOffsetDelta = mFinalHorizontalWallpaperOffset - mHorizontalWallpaperOffset;
            float vOffsetDelta = mFinalVerticalWallpaperOffset - mVerticalWallpaperOffset;
            boolean jumpToFinalValue = Math.abs(hOffsetDelta) < UPDATE_THRESHOLD &&
                Math.abs(vOffsetDelta) < UPDATE_THRESHOLD;

            // Don't have any lag between workspace and wallpaper on non-large devices
//            if (!LauncherApplication.isScreenLarge() || jumpToFinalValue) {
            if (jumpToFinalValue) {
                mHorizontalWallpaperOffset = mFinalHorizontalWallpaperOffset;
                mVerticalWallpaperOffset = mFinalVerticalWallpaperOffset;
            } else {
                float percentToCatchUpVertical =
                    Math.min(1.0f, timeSinceLastUpdate * fractionToCatchUpIn1MsVertical);
                float percentToCatchUpHorizontal =
                    Math.min(1.0f, timeSinceLastUpdate * fractionToCatchUpIn1MsHorizontal);
                mHorizontalWallpaperOffset += percentToCatchUpHorizontal * hOffsetDelta;
                mVerticalWallpaperOffset += percentToCatchUpVertical * vOffsetDelta;
            }

            mLastWallpaperOffsetUpdateTime = System.currentTimeMillis();
            return true;
        }

        public float getCurrX() {
            return mHorizontalWallpaperOffset;
        }

        public float getFinalX() {
            return mFinalHorizontalWallpaperOffset;
        }

        public float getCurrY() {
            return mVerticalWallpaperOffset;
        }

        public float getFinalY() {
            return mFinalVerticalWallpaperOffset;
        }

        public void setFinalX(float x) {
            mFinalHorizontalWallpaperOffset = Math.max(0f, Math.min(x, 1.0f));
        }

        public void setFinalY(float y) {
            mFinalVerticalWallpaperOffset = Math.max(0f, Math.min(y, 1.0f));
        }

        public void jumpToFinal() {
            mHorizontalWallpaperOffset = mFinalHorizontalWallpaperOffset;
            mVerticalWallpaperOffset = mFinalVerticalWallpaperOffset;
        }
    }
    
    protected void setWallpaperDimension() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        /*modify zhanglq@bj.cobellink.com DATA 2012-06-12 start*/
        //fix bug 165949
        //mLauncher.getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        XLauncher xLauncher = (XLauncher) (mContext.getContext());
        if (LauncherApplication.isScreenLarge()) {
            xLauncher.getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics); // bug 10371.
        } else {
            xLauncher.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics); // bug 10371.
        }
        /*modify zhanglq@bj.cobellink.com DATA 2012-06-12 end*/
        
//        int naviHeight = Utilities.getNavigationBarHeight(xLauncher);
        int height = displayMetrics.heightPixels/* - naviHeight*/;
                
        final int maxDim = Math.max(displayMetrics.widthPixels, height);
        final int minDim = Math.min(displayMetrics.widthPixels, height);
        R5.echo("maxDim = " + maxDim + "minDim = " + minDim);
        
        
        // We need to ensure that there is enough extra space in the wallpaper for the intended
        // parallax effects
        
        //test for stella,check the launcher value for pad ,if is -1, means stella is a Phone
        final int phoneindex = mContext.getResources().getInteger(R.integer.config_machine_type);
		if(phoneindex == -1){
			R5.echo("setWallpaperDimension check phoneindex = " + phoneindex );
		}
		
        if (LauncherApplication.isScreenLarge() && phoneindex != -1) {
            mWallpaperWidth = (int) (maxDim * wallpaperTravelToScreenWidthRatio(maxDim, minDim));
            mWallpaperHeight = (int)(maxDim * wallpaperTravelToScreenHeightRatio(maxDim, minDim));
        } else {
            mWallpaperWidth = Math.max((int) (minDim * WALLPAPER_SCREENS_SPAN), maxDim);
            mWallpaperHeight = maxDim;
        }
        R5.echo("mWallpaperHeight == " + mWallpaperHeight
                + "    &&&   mWallpaperWidth === " + mWallpaperWidth);
        new Thread("setWallpaperDimension") {
            public void run() {
                // SystemClock.sleep(50L);
                mWallpaperManager.suggestDesiredDimensions(mWallpaperWidth, mWallpaperHeight);
            }
        }.start();
    }
    
    // As a ratio of screen height, the total distance we want the parallax effect to span
    // horizontally
    private float wallpaperTravelToScreenWidthRatio(int width, int height) {
        float aspectRatio = width / (float) height;

        // At an aspect ratio of 16/10, the wallpaper parallax effect should span 1.5 * screen width
        // At an aspect ratio of 10/16, the wallpaper parallax effect should span 1.2 * screen width
        // We will use these two data points to extrapolate how much the wallpaper parallax effect
        // to span (ie travel) at any aspect ratio:

        final float ASPECT_RATIO_LANDSCAPE = 16/10f;
        final float ASPECT_RATIO_PORTRAIT = 10/16f;
        final float WALLPAPER_WIDTH_TO_SCREEN_RATIO_LANDSCAPE = 1.5f;
        final float WALLPAPER_WIDTH_TO_SCREEN_RATIO_PORTRAIT = 1.2f;

        // To find out the desired width at different aspect ratios, we use the following two
        // formulas, where the coefficient on x is the aspect ratio (width/height):
        //   (16/10)x + y = 1.5
        //   (10/16)x + y = 1.2
        // We solve for x and y and end up with a final formula:
        final float x =
            (WALLPAPER_WIDTH_TO_SCREEN_RATIO_LANDSCAPE - WALLPAPER_WIDTH_TO_SCREEN_RATIO_PORTRAIT) /
            (ASPECT_RATIO_LANDSCAPE - ASPECT_RATIO_PORTRAIT);
        final float y = WALLPAPER_WIDTH_TO_SCREEN_RATIO_PORTRAIT - x * ASPECT_RATIO_PORTRAIT;
        return x * aspectRatio + y;
    }
    
    // As a ratio of screen height, the total distance we want the parallax effect to span
    // vertically
    private float wallpaperTravelToScreenHeightRatio(int width, int height) {
        return 1.1f;
    }
    
    private void syncWallpaperOffsetWithScroll() {
//        final boolean enableWallpaperEffects = isHardwareAccelerated();
        /* AUT: liuli liuli0120@use.com.cn DATE: 2012-02-01 START */
        // to enable wallpaper offset, enableWallpaperEffects always be false in test device
        if (SettingsValue.isWallpaperSlideEnabled(mContext.getContext())) {
            if (mDebug)R5.echo("setFinalX = " + wallpaperOffsetForCurrentScroll(mCurrentPage, mOffsetX));
            mWallpaperOffset.setFinalX(wallpaperOffsetForCurrentScroll(mCurrentPage, mOffsetX));
//            R2.echo("setFinalX = " + wallpaperOffsetForCurrentScroll());
        }
        /* AUT: liuli liuli0120@use.com.cn DATE: 2012-02-01 END */
    }
    
    private float wallpaperOffsetForCurrentScroll(int page, float offsetX) {
        // The wallpaper travel width is how far, from left to right, the wallpaper will move
        // at this orientation. On tablets in portrait mode we don't move all the way to the
        // edges of the wallpaper, or otherwise the parallax effect would be too strong.
        int wallpaperTravelWidth = mWallpaperWidth;
        if (LauncherApplication.isScreenLarge()) {
            wallpaperTravelWidth = mWallpaperTravelWidth;
        }

        // Set wallpaper offset steps (1 / (number of screens - 1))
        mWallpaperManager.setWallpaperOffsetSteps(1.0f / (mPageCount - 1), 1.0f);

        // For the purposes of computing the scrollRange and overScrollOffset, we assume
        // that mLayoutScale is 1. This means that when we're in spring-loaded mode,
        // there's no discrepancy between the wallpaper offset for a given page.
        float layoutScale = mLayoutScale;
        mLayoutScale = 1f;
        int scrollRange = getScrollRange();

        // Again, we adjust the wallpaper offset to be consistent between values of mLayoutScale
        
        float mOffset;
//        mOffset = mCurrentPage - mOffsetX;
        mOffset = page - offsetX;
        if (mOffset > (mPageCount - 1))
        {
            mOffset = mPageCount - 1;
        }
        
        float adjustedScrollX = Math.max(0, mOffset);
//        R2.echo("mCurrentPage = " + mCurrentPage + "mOffsetX = " + mOffsetX );
        adjustedScrollX *= mWallpaperScrollRatio;
        mLayoutScale = layoutScale;
        
        float scrollProgress = 0.0f;//16380
        if(scrollRange!=0)
        	scrollProgress = adjustedScrollX / (float) scrollRange;
        float offsetInDips = wallpaperTravelWidth * scrollProgress +
            (mWallpaperWidth - wallpaperTravelWidth) / 2.0f; // center it
        float offset = offsetInDips / (float) mWallpaperWidth;
        
        offset = Math.max(0f, Math.min(offset, 1.0f));
        return offset;
    }
    
    // The range of scroll values for Workspace
    private int getScrollRange() {
        return mPageCount - 1;
    }
    
    protected void updateWallpaperOffsets() {
        boolean updateNow = false;
        boolean keepUpdating = true;
        if (mUpdateWallpaperOffsetImmediately) {
            updateNow = true;
            keepUpdating = false;
            mWallpaperOffset.jumpToFinal();
            mUpdateWallpaperOffsetImmediately = false;
            if (mDebug)R5.echo("mUpdateWallpaperOffsetImmediately");
        } else {
            updateNow = keepUpdating = mWallpaperOffset.computeScrollOffset();
            if (mDebug) R5.echo("updateNow = " + updateNow);
        }
        if (updateNow) {
            if (mWindowToken != null) {
            	if (enableScroll) {
	                mWallpaperManager.setWallpaperOffsets(mWindowToken,
	                        mWallpaperOffset.getCurrX(), mWallpaperOffset.getCurrY());
	                
	                if (mDebug) R5.echo("setWallpaperOffsets x = " + mWallpaperOffset.getCurrX() 
	                        + " y = " +  mWallpaperOffset.getCurrY());
            	}
            }
        }
        if (keepUpdating) {
            if (mDebug) R5.echo("keepUpdating");
            invalidate();
        }
    }
    
    public void setWindowToken(IBinder windowToken){
        mWindowToken = windowToken;
    }
    
    private Drawable wallPaperBitmap;
    private boolean blurActivite = false;
    private boolean showBlur = false;
    private final Paint blurPaint = new Paint();
    private Bitmap blurBitmap;
//    public boolean mSlideLoop = false;
    boolean mScroll = false;
//    protected int previousOrientation = 0;
    
    boolean mDrawWallpaper = false;
    protected void computeScrollHelper(IDisplayProcess canvas) {
        if(mPageCount == 1 
                || !mIsStaticWallpaper 
                || !SettingsValue.isWallpaperSlideEnabled(mContext.getContext()) 
                || getWidth() == 0 
                || !isLoop()) 
            return;
                
        float alpha = 0;
        mDrawWallpaper = false;
        float left = 0;
        
        //if( wallPaperBitmap == null ){
        //    wallPaperBitmap = mWallpaperManager.getDrawable();
        //}  
        
//        int orientattion;
//        if (currOrientation == 0)
//        {
//            orientattion = previousOrientation;
//        }
//        else
//        {
//            orientattion = previousOrientation = currOrientation;
//        }
//        
//        if (mOffsetX > 0 && mCurrentPage == 0 && orientattion > 0) {
//            left = getLastLeft();
//            alpha = mOffsetX;
//            draw = true;
//        } else if (mOffsetX < 0 && mCurrentPage == mPageCount - 1 && orientattion > 0) {
//            left = getLastLeft();
//            alpha = 1 + mOffsetX;
//            draw = true;
//        } else if (mOffsetX < 0 && mCurrentPage == mPageCount - 1 && orientattion < 0) {
//        	left = getFirstLeft();
//            alpha = -mOffsetX;
//            draw = true;
//        } else if (mOffsetX > 0 && mCurrentPage == 0 && orientattion < 0) {
//        	left = getFirstLeft();
//            alpha = 1 - mOffsetX;
//            draw = true;
//        }
        
        if (mOffsetX > 0 && mCurrentPage == 0) {
            left = getLastLeft();
            alpha = mOffsetX;
            mDrawWallpaper = true;
        } else if (mOffsetX < 0 && mCurrentPage == mPageCount - 1) {
            left = getFirstLeft();
            alpha = -mOffsetX;
            mDrawWallpaper = true;
        } 
//        else
//        {
//            R5.echo("no draw");
//        }
        
//        R5.echo("draw alpha = " + alpha + "mCurrentPage = " + mCurrentPage + "mOffsetX = " + mOffsetX + "currOrientation = " + currOrientation);
            
        if (mDrawWallpaper)
        {
            if( wallPaperBitmap == null ){
                wallPaperBitmap = mWallpaperManager.getDrawable();
            }     
                  
            if (mDebug) R5.echo("mWallpaperManager.getDrawable() width = " + mWallpaperManager.getDrawable().getIntrinsicWidth()
                    + "height = " + mWallpaperManager.getDrawable().getIntrinsicHeight());
            
            float top = getTop();
            
            RectF rect = new RectF(left, top, left + mWallpaperWidth, top + mWallpaperHeight);            
            canvas.save();
//            R5.echo("draw alpha = " + alpha + "mCurrentPage = " + mCurrentPage + "mOffsetX = " + mOffsetX);
            int intAlpha = (int)(alpha * 255);
            wallPaperBitmap.setAlpha(intAlpha);
            canvas.drawDrawable(wallPaperBitmap, rect);
            canvas.restore();
        }
    }

    /*** RK_ID: RK_FULLSCREEN_FOLDER  AUT: zhaoxy . DATE: 2012-05-23 . START***/
    @Override
    public void resize(RectF rect) {
        super.resize(rect);
        XLauncher xLauncher = (XLauncher) getXContext().getContext();
//        float top = getRelativeY() + (getParent() != null ? getParent().getRelativeY() : 0);
//        if (xLauncher.isCurrentWindowFullScreen()) {
//            topWallPaper = -top - xLauncher.getStatusBarHeight() * (1 - 1f / mScaleFolderAnimExtra);
//        } else {
//            topWallPaper = -top - xLauncher.getStatusBarHeight();
//        }
        fullScreenFlag = xLauncher.isCurrentWindowFullScreen();
        mInput = 1;
    }

    float topWallPaper = 0;
    boolean fullScreenFlag = false;
    private float mScaleFolderAnimExtra = 1f;
    float mInput;

    public void updateFolderAnim(float input, float scale) {
        mScaleFolderAnimExtra = scale;
        mInput = input;
//        XLauncher xLauncher = (XLauncher) getXContext().getContext();
//        float top = getRelativeY() + (getParent() != null ? getParent().getRelativeY() : 0);
//        if (fullScreenFlag) {
//            topWallPaper = -top - xLauncher.getStatusBarHeight() * (1 - 1f / mScaleFolderAnimExtra) * input;
//        } else {
//            topWallPaper = -top - xLauncher.getStatusBarHeight();
//        }
    }
    
    public void buildBlurBitmap() {
        blurActivite = false;
        if (wallPaperBitmap == null) {
            wallPaperBitmap = mWallpaperManager.getDrawable();
        }
        clearBlurBitmap();
        blurBitmap = Bitmap.createBitmap(wallPaperBitmap.getIntrinsicWidth(), wallPaperBitmap.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//        long take = Utilities.newInstance().blur(((BitmapDrawable) wallPaperBitmap).getBitmap(), blurBitmap, 50);
        long take = Utilities.fastBlur(((BitmapDrawable) wallPaperBitmap).getBitmap(), blurBitmap, 20);
        
        android.util.Log.d("Blur", "buildBlurBitmap end take " + take);
        blurActivite = true;
    }
    
    public void clearBlurBitmap() {
        if (blurBitmap != null && !blurBitmap.isRecycled()) {
            blurBitmap.recycle();
        }
        blurBitmap = null;
    }

    public void setBlurEnable(boolean enable) {
        showBlur = enable;
    }

    private void dranWallpaperAnimation(IDisplayProcess canvas) {
        if (!isdraw) {
            return;
        }
        if (wallPaperBitmap == null) {
            wallPaperBitmap = mWallpaperManager.getDrawable();
        }
        
        XLauncher xLauncher = (XLauncher) getXContext().getContext();
        if (fullScreenFlag) {
//            topWallPaper = getTop() - xLauncher.getStatusBarHeight() * (1 - 1f / mScaleFolderAnimExtra) * mInput;
        	float top = getParent() != null ? getParent().getRelativeY() : 0;
        	float delta = 0;
        	delta = (wallPaperBitmap.getIntrinsicHeight() - mDisplayHeight) * mWallpaperOffset.getFinalY();
        	topWallPaper = -top - delta - xLauncher.getStatusBarHeight() * (1 - 1f / mScaleFolderAnimExtra) * mInput;
        } else {
            topWallPaper = getTop();
        }
        
        wallPaperBitmap.setBounds(0, 0, wallPaperBitmap.getIntrinsicWidth(), wallPaperBitmap.getIntrinsicHeight());

        canvas.save();
        canvas.translate(getCurrentLeft(), topWallPaper);
        if (showBlur) {
            if (blurActivite && blurBitmap != null && !blurBitmap.isRecycled()) {
                blurPaint.setAlpha((int) (255 * mInput));
                canvas.drawDrawable(wallPaperBitmap);
                canvas.drawBitmap(blurBitmap, 0, 0, blurPaint);
            }
        } else {
            canvas.drawDrawable(wallPaperBitmap);
        }
        canvas.restore();
    }
    /*** RK_ID: RK_FULLSCREEN_FOLDER  AUT: zhaoxy . DATE: 2012-05-23 . END***/

    protected void onPageBeginMoving() {
        super.onPageBeginMoving();
        mIsStaticWallpaper = mWallpaperManager.getWallpaperInfo() == null;
        mScroll = true;
    }
    
    protected void onPageEndMoving() {
        super.onPageEndMoving();
//        mXContext.post(new Runnable() {
//            @Override
//            public void run() {
                mUpdateWallpaperOffsetImmediately = true;
                syncWallpaperOffsetWithScroll();        
                updateWallpaperOffsets();
                mScroll = false;
                if(wallPaperBitmap != null) {                       
                    wallPaperBitmap.setCallback(null);
                    wallPaperBitmap = null;                     
                }
//            }           
//        });
        
    }

    @Override
    public boolean removePageItem(ItemInfo info) {
        boolean res = super.removePageItem(info);

        Context c = mContext.getContext();
        if (c instanceof XLauncher) {
            XLauncher launcher = (XLauncher) c;
            launcher.removePopupWindow(info);
        }

        return res;
    }

    @Override
    public boolean removePagedViewItem(XPagedViewItem itemToRemove) {
        boolean res = super.removePagedViewItem(itemToRemove);

        Context c = mContext.getContext();
        if (c instanceof XLauncher) {
            XLauncher launcher = (XLauncher) c;
            launcher.removePopupWindow(itemToRemove.getInfo());
        }

        return res;
    }

    /* RK_ID: RK_WALLPAPER. AUT: liuli1 . DATE: 2013-04-27 . START */
    @Override
    public void setCurrentPage(int currentPage) {
        super.setCurrentPage(currentPage);
        if (mDebug)R5.echo("setCurrentPage = " + currentPage);
        mUpdateWallpaperOffsetImmediately = true;
        syncWallpaperOffsetWithScroll();        
        updateWallpaperOffsets();
    }

    @Override
    public void scrollToLeft(long duration) {
        onPageBeginMoving();
        super.scrollToLeft(duration);
    }

    @Override
    public void scrollToRight(long duration) {
        onPageBeginMoving();
        super.scrollToRight(duration);
    }
    /* RK_ID: RK_WALLPAPER. AUT: liuli1 . DATE: 2013-04-27 . END */
    
    public void enableScrollWhenSetCurrentPage(boolean enabled) {
    	enableScroll = enabled;
    	if (mWindowToken != null
    			&& enableScroll) {
            mWallpaperManager.setWallpaperOffsets(mWindowToken,
                    mWallpaperOffset.getCurrX(), mWallpaperOffset.getCurrY());
            if (mDebug)R5.echo("enableScrollWhenSetCurrentPage setWallpaperOffsets x = " + mWallpaperOffset.getCurrX() 
                    + " y = " +  mWallpaperOffset.getCurrY());
            
        }
    }
    private boolean gestureEnable = true;    
    
    public static final String ACTION_GESTURE_SCROLL_UP = "com.lenovo.launcher.gesture.Intent.ACTION_SCROLL_UP";
    public static final String ACTION_GESTURE_SCROLL_DOWN = "com.lenovo.launcher.gesture.Intent.ACTION_SCROLL_DOWN";
    public static final String ACTION_GESTURE_DOUBLE_CLICK = "com.lenovo.launcher.gesture.Intent.ACTION_DOUBLE_CLICK";
    
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
    		float distanceY, float previousX, float previousY) {
    	boolean handled = false;
    	boolean ret = super.onScroll(e1, e2, distanceX, distanceY, previousX, previousY);
    	
    	if( gestureEnable ){
    		final DisplayMetrics m = getXContext().getContext().getResources().getDisplayMetrics();
    		if (Math.abs(distanceX) < 5 * m.density && Math.abs(distanceY) > Math.abs(distanceX)) {
    			if (distanceY < 0 && distanceY < -12 * m.density) {
    				gestureEnable = false;
    				handled = true;
    				if( !isPageMoving ){
    					getXContext().getContext().sendBroadcast(new Intent(ACTION_GESTURE_SCROLL_DOWN));
    					gestureProcess = true;
    				}
    			} else if (distanceY > 0 && distanceY > 10 * m.density) {
    				gestureEnable = false;
    				handled = true;
    				if( !isPageMoving ){
    					getXContext().getContext().sendBroadcast(new Intent(ACTION_GESTURE_SCROLL_UP));
    					gestureProcess = true;
    				}
    			}}
    	}
    	handled =  ret || handled;
    	return handled;
    }
    
    
    @Override
	public boolean onDoubleTapped(MotionEvent e, DrawableItem itemTapped) {
		for (int i = 0; i < getChildCount(); i++) {
			final DrawableItem child = getChildAt(i);
			if (getXContext().getExchangee().checkHited(child, e.getX(),
					e.getY())) {
				if (child instanceof XCell) {
					if (((XCell) child).getDrawingTarget() != null) {
						return true;
					}
				}
				return true;
			}
		}

//		android.util.Log.i("touch", "double tapped." + itemTapped);
//		getXContext().getContext().sendBroadcast(
//				new Intent(ACTION_GESTURE_DOUBLE_CLICK));
		
//		((XLauncher)getXContext().getContext()).doubleClick();
		((XLauncher)getXContext().getContext()).getLauncherHandler().sendEmptyMessage(XLauncher.MSG_ID_SHOW_RECENT);

		return true;
	}
    
    
    public void setGestureEnable( boolean enable ){
    	gestureEnable = enable;
    }
    
    @Override
    public boolean onDown(MotionEvent e) {
    	gestureEnable = true;
    	gestureProcess = false;
    	return super.onDown(e);
    }
    private boolean findLastOccupiedCellXY(int[] page_cellXY,int page){
        if (page < 0) {
            return false;
        }
        page = (page > getPageCount() - 1)?getPageCount() - 1:page;
        boolean occupied = false;
        
        int x=0;int y =0;
        for (y = mCellCountY - 1; y >= 0; y--) {
            for (x = mCellCountX - 1; x >= 0; x--) {
            	occupied = mOccupied[page][x][y];
            	if(occupied){
            		page_cellXY[0] = page;
            		page_cellXY[1] =x;
            		page_cellXY[2] = y;
            		return true;
            	}
            }
            if ((x+1) == 0 && y == 0 && page >0) {
            	page = page-1;
            	x = mCellCountX-1;y=mCellCountY;
            }
        }
        
    	return false;
    }
    
	public boolean findVacantCellXY(int[] start, int startPageIndex) {
		int[] page_cellXY = new int[3];
		boolean occupied = findLastOccupiedCellXY(page_cellXY, startPageIndex);
		if (occupied) {
			int x = page_cellXY[1];
			int y = page_cellXY[2];
			int page = page_cellXY[0];
			if (x == mCellCountX - 1 && y == mCellCountY - 1) {
				if (page_cellXY[0] == getPageCount() - 1) {
					// last cell is occupied,
					// if last page, add new one.
					int pageCnt = getPageCount();
				    if(pageCnt < SettingsValue.getLauncherScreenMaxCount(mContext.getContext())){
				    	addNewScreen();
			        }else{
			        	return false;
			        }
				}
				start[0] = 0;
				start[1] = 0;
				start[2] = page + 1;
				


			} else {
				// we think previous cell is vacant.
				// so x and y coordinate both + 1.
				start[0] = x < mCellCountX - 1 ? x + 1 : 0;
				start[1] = x < mCellCountX - 1 ? y : y + 1;
				start[2] = page;
				

			}
			return true;
		}else{
			start[0] = 0;
			start[1] = 0;
			start[2] = 0;
			return true;
		}
	}
    
//    public void findVacantCellXY(int[] start, int page,boolean addInNextScreen) {
//        if (page > getPageCount() - 1 || page < 0) {
//            return;
//        }
//
//        boolean occupied = false;
//
//        for (int y = mCellCountY - 1; y >= 0; y--) {
//            for (int x = mCellCountX - 1; x >= 0; x--) {
//                occupied = mOccupied[page][x][y];
//                Log.d("test", "===========page =" + page +"begin\n");
//                if (!occupied) {
//                    if (x == 0 && y == 0) {
//                        // first cell is not occupied, so we continue to find.
//                        findVacantCellXY(start, page - 1,addInNextScreen);
//                    } else {
//                        continue;
//                    }
//                } else {
//                    findVacantCellXY(start, x, y, page,addInNextScreen);
//                    return;
//                }
//                Log.d("test", "===========page =" + page +"end\n");
//            } // end for x
//        } // end for y
//
//    }
//
//    private void findVacantCellXY(int[] start, int x, int y, int page,boolean addInNextScreen) {
//
//        if (addInNextScreen  || (x == mCellCountX - 1 && y == mCellCountY - 1)) {
//            if (page == getPageCount() - 1) {
//                // last cell is occupied,
//                // if last page, add new one.
//                addNewScreen();
//            }
//            start[0] = 0;
//            start[1] = 0;
//            start[2] = page+1;
//
//        } else {
//            // we think previous cell is vacant.
//            // so x and y coordinate both + 1.
//            start[0] = x < mCellCountX - 1 ? x + 1 : 0;
//            start[1] = x < mCellCountX - 1 ? y : y + 1;
//            start[2] = page;
//        }
//    }

    @Override
    public void addNewScreen() {
        super.addNewScreen();

        int pageCnt = getPageCount();

        SharedPreferences preferrences = getXContext().getContext().getSharedPreferences(
                CELLLAYOUT_COUNT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferrences.edit();
        editor.putInt(CELLLAYOUT_COUNT, pageCnt);
        editor.commit();

        LauncherService.getInstance().mScreenCount = pageCnt;
        
        //add by zhanggx1 for refresh mng view.s
        XLauncher xLauncher = (XLauncher) (mContext.getContext());
        xLauncher.refreshMngViewOnAddScreen();
        //add by zhanggx1 for refresh mng view.e
    }

    void retrieveWorkspaceExtraSettings(){
        SharedPreferences prf = PreferenceManager.getDefaultSharedPreferences(getXContext().getContext());
        float offsetWallpaperX = prf.getFloat( "wallpaper_offset_x", 0);
        float offsetWallpaperY = prf.getFloat( "wallpaper_offset_y", 0);
        mWallpaperOffset.setFinalX( offsetWallpaperX );
        mWallpaperOffset.setFinalY( offsetWallpaperY );
        if (mDebug)R5.echo("retrieveWorkspaceExtraSettings setFinalX = " + offsetWallpaperX + "setFinalY = " + offsetWallpaperY);
    }
    
    public boolean onFingerUp(MotionEvent e) {
        gestureProcess = false;
        return super.onFingerUp(e);
    }   
    
    private int getLastLeft(){
        if (mDebug)R5.echo(" wallpaperOffsetForCurrentScroll(mPageCount-1, 0) = " +  wallpaperOffsetForCurrentScroll(mPageCount-1, 0));
        return getLeft(wallpaperOffsetForCurrentScroll(mPageCount-1, 0));
    }
    
    private int getFirstLeft(){
        if (mDebug)R5.echo(" wallpaperOffsetForCurrentScroll(0, 0) = " +  wallpaperOffsetForCurrentScroll(0, 0));
        return getLeft(wallpaperOffsetForCurrentScroll(0, 0));
    }
    
    protected int getCurrentLeft(){        
        return getLeft(mWallpaperOffset.getCurrX());
    }
    
    private int getLeft(float wallpaperOffset){    	
    	if (wallPaperBitmap == null) {
        	 wallPaperBitmap = mWallpaperManager.getDrawable();
        }
    	float delta = wallPaperBitmap.getIntrinsicWidth() - mDisplayWidth;
        float offsetX = delta * wallpaperOffset;
        float globeX = getParent().getGlobalX();
        int left = -(int)(offsetX + .5f + globeX);
        
        if (mDebug)R5.echo("left = " + left + "offsetX = " + offsetX
        		+ "globeX = " + globeX);
        
        return left;
//          left = -getGlobalTouchRect().left;
    }
    
    protected int getTop(){
        if (wallPaperBitmap == null) {
            wallPaperBitmap = mWallpaperManager.getDrawable();
        }
        
        int barHeight = 0;
        
		if (!SettingsValue.hasExtraTopMargin())
		{
			XLauncher xLauncher = (XLauncher) (mContext.getContext());
	        Rect rect1 = new Rect();
	        xLauncher.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect1);
	        barHeight = rect1.top;
		}    	
        
//        float top = -localRect.top - rect1.top;
        float delta = wallPaperBitmap.getIntrinsicHeight() - mDisplayHeight;
        
        float offsetY = (float)(delta * mWallpaperOffset.getFinalY());
        float globeY = getParent().getGlobalY();
        
        int top = - (int)(globeY + barHeight + offsetY  + .5f);
        
        if (mDebug)R5.echo("top = " + top + " globeY =  " +  globeY 
        		+ "barHeight = " + barHeight
        		+ "offsetY = " + offsetY);
        
        return top;
    }
    
    private boolean oriflag = true;
	public void configurationChange(final boolean $configurationChange) {
		super.configurationChange(true);
		//super.configurationChange($configurationChange);
		if (mDebug)R5.echo("configurationChange");
        oriflag = $configurationChange;
		setWallpaperDimension();
		caculateData();
		mUpdateWallpaperOffsetImmediately = true;
        syncWallpaperOffsetWithScroll();
        updateWallpaperOffsets();
	}
	
	private void caculateData() {
		XLauncher xLauncher = (XLauncher) (mContext.getContext());
		
		Display display = xLauncher.getWindowManager().getDefaultDisplay(); // bug 10371.

        mDisplayWidth = display.getWidth();
        mDisplayHeight = display.getHeight();
        mWallpaperTravelWidth = (int) (mDisplayWidth *
                wallpaperTravelToScreenWidthRatio(mDisplayWidth, mDisplayHeight));
                
        if (mDebug)R5.echo("mDisplayWidth = " + mDisplayWidth + "mDisplayHeight = " + mDisplayHeight + "mWallpaperTravelWidth = " + mWallpaperTravelWidth);
//        if (wallPaperBitmap == null) {
//            wallPaperBitmap = mWallpaperManager.getDrawable();
//        }
	}
	
	protected WallpaperOffsetInterpolator getWallpaperOffset() {
	    return this.mWallpaperOffset;
	}
}
