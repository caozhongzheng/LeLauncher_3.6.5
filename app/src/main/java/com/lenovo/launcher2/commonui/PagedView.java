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

package com.lenovo.launcher2.commonui;

import java.util.ArrayList;
import java.util.Random;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.ActionMode;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.Scroller;

import android.graphics.PaintFlagsDrawFilter;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.customizer.LauncherPersonalSettings;
import com.lenovo.launcher2.customizer.SettingsValue;


/**
 * An abstraction of the original Workspace which supports browsing through a
 * sequential list of "pages"
 */
public abstract class PagedView extends ViewGroup {
    private static final String TAG = "PagedView";
    private static final boolean DEBUG = false;
    protected static final int INVALID_PAGE = -1;

    // the min drag distance for a fling to register, to prevent random page shifts
    private static final int MIN_LENGTH_FOR_FLING = 25;
    // The min drag distance to trigger a page shift (regardless of velocity)
    /* AUT: liuli liuli0120@use.com.cn DATE: 2012-01-12 START */
    // bug : Bug 149703
    //modify zhanglq 2012-03-06
    private static final int MIN_LENGTH_FOR_MOVE = 50;// original is 200;
    /* AUT: liuli liuli0120@use.com.cn DATE: 2012-01-12 END */

    private static final int PAGE_SNAP_ANIMATION_DURATION = 550;
    protected static final float NANOTIME_DIV = 1000000000.0f;

    private static final float OVERSCROLL_ACCELERATE_FACTOR = 2;
    private static final float OVERSCROLL_DAMP_FACTOR = 0.14f;
    private static final int MINIMUM_SNAP_VELOCITY = 2200;
    private static final int MIN_FLING_VELOCITY = 250;
    private static final float RETURN_TO_ORIGINAL_PAGE_THRESHOLD = 0.33f;
    // The page is moved more than halfway, automatically move to the next page on touch up.
    private static final float SIGNIFICANT_MOVE_THRESHOLD = 0.4f;

    // the velocity at which a fling gesture will cause us to snap to the next page
    /* AUT: zhanglq zhanglq@bj.cobellink.com DATE: 2012-03-12 START */
    protected int mSnapVelocity = 200;// original is 500;
    /* AUT: zhanglq zhanglq@bj.cobellink.com DATE: 2012-03-12 END */

    protected float mDensity;
    protected float mSmoothingTime;
    protected float mTouchX;

    protected boolean mFirstLayout = true;

    protected int mCurrentPage;
    protected int mNextPage = INVALID_PAGE;
    protected int mMaxScrollX;
    protected Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    private float mDownMotionX;
    protected float mLastMotionX;
    protected float mLastMotionXRemainder;
    protected float mLastMotionY;
    protected float mTotalMotionX;
    private int mLastScreenCenter = -1;
    private int[] mChildOffsets;
    private int[] mChildRelativeOffsets;
    private int[] mChildOffsetsWithLayoutScale;

    protected final static int TOUCH_STATE_REST = 0;
    protected final static int TOUCH_STATE_SCROLLING = 1;
    protected final static int TOUCH_STATE_PREV_PAGE = 2;
    protected final static int TOUCH_STATE_NEXT_PAGE = 3;
    /** RK_ID: RK_MAGIC_GESTRUE . AUT: zhanglz1 . DATE: 2012-09-27 . S**/
    protected final static int TOUCH_APPLIST_SWIPE_UP_GESTURE = 4;
    protected final static int TOUCH_WORKSPACE_SWIPE_UP_GESTURE = 5;
    protected final static int TOUCH_WORKSPACE_SWIPE_DOWN_GESTURE = 6;
    protected final static int TOUCH_APPLIST_SWIPE_DOWN_GESTURE = 7;
    protected final static int TOUCH_WORKSPACE_DOUBLE_SWIPE_UP_GESTURE = 8;
    protected final static int TOUCH_EMPTY_WORKSPACE_DOUBLE_CLICK_GESTURE = 9;
    protected boolean isWallpaperShowing = false;
    /** RK_ID: RK_MAGIC_GESTRUE . AUT: zhanglz1 . DATE: 2012-09-27 . E**/

    protected final static float ALPHA_QUANTIZE_LEVEL = 0.0001f;
    /* add by xingqx 05.18 start */
    public final static int PAGE_MOVE_NONE = 0;
    public final static int PAGE_MOVE_TO_FIRST = 1;
    public final static int PAGE_MOVE_TO_END = 2;
    public int mPageMoveState = PAGE_MOVE_NONE;
    /* add by xingqx 05.18 end */

    protected int mTouchState = TOUCH_STATE_REST;
    protected boolean mForceScreenScrolled = false;

    protected OnLongClickListener mLongClickListener;

    protected boolean mAllowLongPress = true;

    protected int mTouchSlop;
    private int mPagingTouchSlop;
    private int mMaximumVelocity;
    private int mMinimumWidth;
    protected int mPageSpacing;
    protected int mPageLayoutPaddingTop;
    protected int mPageLayoutPaddingBottom;
    protected int mPageLayoutPaddingLeft;
    protected int mPageLayoutPaddingRight;
    protected int mPageLayoutWidthGap;
    protected int mPageLayoutHeightGap;
    protected int mCellCountX = 0;
    protected int mCellCountY = 0;
    protected boolean mCenterPagesVertically;
    protected boolean mAllowOverScroll = true;
    protected int mUnboundedScrollX;
    protected int[] mTempVisiblePagesRange = new int[2];

    // mOverScrollX is equal to mScrollX when we're within the normal scroll range. Otherwise
    // it is equal to the scaled overscroll position. We use a separate value so as to prevent
    // the screens from continuing to translate beyond the normal bounds.
    protected int mOverScrollX;

    // parameter that adjusts the layout to be optimized for pages with that scale factor
    protected float mLayoutScale = 1.0f;

    protected static final int INVALID_POINTER = -1;

    protected int mActivePointerId = INVALID_POINTER;

    private PageSwitchListener mPageSwitchListener;

    protected ArrayList<Boolean> mDirtyPageContent;
    private boolean mDirtyPageAlpha = true;

    // choice modes
    protected static final int CHOICE_MODE_NONE = 0;
    protected static final int CHOICE_MODE_SINGLE = 1;
    // Multiple selection mode is not supported by all Launcher actions atm
    protected static final int CHOICE_MODE_MULTIPLE = 2;

    protected int mChoiceMode;
    private ActionMode mActionMode;

    // If true, syncPages and syncPageItems will be called to refresh pages
    protected boolean mContentIsRefreshable = true;

    // If true, modify alpha of neighboring pages as user scrolls left/right
    protected boolean mFadeInAdjacentScreens = true;

    // It true, use a different slop parameter (pagingTouchSlop = 2 * touchSlop) for deciding
    // to switch to a new page
    protected boolean mUsePagingTouchSlop = true;

    // If true, the subclass should directly update mScrollX itself in its computeScroll method
    // (SmoothPagedView does this)
    protected boolean mDeferScrollUpdate = false;

    protected boolean mIsPageMoving = false;

    // All syncs and layout passes are deferred until data is ready.
    protected boolean mIsDataReady = false;

    // Scrolling indicator
    private ValueAnimator mScrollIndicatorAnimator;
    private ImageView mScrollIndicator;
    private int mScrollIndicatorPaddingLeft;
    private int mScrollIndicatorPaddingRight;
    private boolean mHasScrollIndicator = true;
    protected static final int sScrollIndicatorFadeInDuration = 150;
    protected static final int sScrollIndicatorFadeOutDuration = 650;
    protected static final int sScrollIndicatorFlashDuration = 650;

    // If set, will defer loading associated pages until the scrolling settles
    private boolean mDeferLoadAssociatedPagesUntilScrollCompletes;
    
    
    /*AUT: zhanglq@bj.cobellink.com DATA 2012-03-01 start*/
    public boolean mCloseCling;
    /*AUT: zhanglq@bj.cobellink.com DATA 2012-03-01 end*/

    public interface PageSwitchListener {
        void onPageSwitch(View newPage, int newPageIndex);
    }

    public PagedView(Context context) {
        this(context, null);
    }

    public PagedView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagedView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mChoiceMode = CHOICE_MODE_NONE;

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.PagedView, defStyle, 0);
        
        //test by dining for sonar
        //setPageSpacing(a.getDimensionPixelSize(R.styleable.PagedView_pageSpacing, 0));
        mPageSpacing = a.getDimensionPixelSize(R.styleable.PagedView_pageSpacing, 0);
        int count = getChildCount();
        if (count == 0) {
            mChildOffsets = null;
            mChildRelativeOffsets = null;
            mChildOffsetsWithLayoutScale = null;
         
        }
        //end test
        mPageLayoutPaddingTop = a.getDimensionPixelSize(
                R.styleable.PagedView_pageLayoutPaddingTop, 0);
        mPageLayoutPaddingBottom = a.getDimensionPixelSize(
                R.styleable.PagedView_pageLayoutPaddingBottom, 0);
        mPageLayoutPaddingLeft = a.getDimensionPixelSize(
                R.styleable.PagedView_pageLayoutPaddingLeft, 0);
        mPageLayoutPaddingRight = a.getDimensionPixelSize(
                R.styleable.PagedView_pageLayoutPaddingRight, 0);
        mPageLayoutWidthGap = a.getDimensionPixelSize(
                R.styleable.PagedView_pageLayoutWidthGap, 0);
        mPageLayoutHeightGap = a.getDimensionPixelSize(
                R.styleable.PagedView_pageLayoutHeightGap, 0);
        mScrollIndicatorPaddingLeft =
            a.getDimensionPixelSize(R.styleable.PagedView_scrollIndicatorPaddingLeft, 0);
        mScrollIndicatorPaddingRight =
            a.getDimensionPixelSize(R.styleable.PagedView_scrollIndicatorPaddingRight, 0);
        a.recycle();

        setHapticFeedbackEnabled(false);
        /** AUT: henryyu1986@163.com DATE: 2011-12-20 */
        setStaticTransformationsEnabled(true);
        /** AUT: henryyu1986@163.com DATE: 2011-12-20 */
        init();
        glob = new GlobeHelper();
    }

    /**
     * Initializes various states for this workspace.
     */
    private void init() {
        mDirtyPageContent = new ArrayList<Boolean>();
        mDirtyPageContent.ensureCapacity(32);
        mScroller = new Scroller(getContext(), new ScrollInterpolator());
        mCurrentPage = 0;
        mCenterPagesVertically = true;

        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
        mPagingTouchSlop = configuration.getScaledPagingTouchSlop();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        mDensity = getResources().getDisplayMetrics().density;
    }

    public void setPageSwitchListener(PageSwitchListener pageSwitchListener) {
        mPageSwitchListener = pageSwitchListener;
        if (mPageSwitchListener != null) {
            mPageSwitchListener.onPageSwitch(getPageAt(mCurrentPage), mCurrentPage);
        }
    }

    /**
     * Called by subclasses to mark that data is ready, and that we can begin loading and laying
     * out pages.
     */
    protected void setDataIsReady() {
        mIsDataReady = true;
    }
    protected boolean isDataReady() {
        return mIsDataReady;
    }

    /**
     * Returns the index of the currently displayed page.
     *
     * @return The index of the currently displayed page.
     */
    public int getCurrentPage() {
        return mCurrentPage;
    }

    public int getPageCount() {
        return getChildCount();
    }

    public View getPageAt(int index) {
        return getChildAt(index);
    }

    protected int indexToPage(int index) {
        return index;
    }

    /**
     * Updates the scroll of the current page immediately to its final scroll position.  We use this
     * in CustomizePagedView to allow tabs to share the same PagedView while resetting the scroll of
     * the previous tab page.
     */
    protected void updateCurrentPageScroll() {
        int newX = getChildOffset(mCurrentPage) - getRelativeChildOffset(mCurrentPage);
        scrollTo(newX, 0);
        mScroller.setFinalX(newX);
    }

    /**
     * Sets the current page.
     */
    public void setCurrentPage(int currentPage) {
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
            scrollEnd();
        }
        // don't introduce any checks like mCurrentPage == currentPage here-- if we change the
        // the default
        if (getChildCount() == 0) {
            return;
        }

        mCurrentPage = Math.max(0, Math.min(currentPage, getPageCount() - 1));
        updateCurrentPageScroll();
        
        notifyPageSwitchListener();
        invalidate();
    }
    /** AUT: henryyu1986@163.com DATE: 2012-1-12 S */
    public String slideEffectValue;
    /** AUT: henryyu1986@163.com DATE: 2012-1-12 E */

    protected void notifyPageSwitchListener() {
        if (mPageSwitchListener != null) {
            mPageSwitchListener.onPageSwitch(getPageAt(mCurrentPage), mCurrentPage);
        }
    }

    Random mRandom = new Random();
    protected void pageBeginMoving() {
        if (!mIsPageMoving) {
            mIsPageMoving = true;
            /*AUT zhanglq@bj.cobellink.com data 2012-05-18 start*/
            scrollY = 0;
            oldy = 0;
	         /*AUT zhanglq@bj.cobellink.com data 2012-05-18 end*/
            onPageBeginMoving();
            /** AUT: henryyu1986@163.com DATE: 2012-1-12 S */
            String value = getSlideEffectValue();
            if(value.equals(LauncherPersonalSettings.SLIDEEFFECT_RANDOM)) {
//                int randomN = Math.abs(mRandom.nextInt()) % LauncherPersonalSettings.SLIDEEFFECT_ARRAY.length ;
                int randomN = mRandom.nextInt(LauncherPersonalSettings.SLIDEEFFECT_ARRAY.length);
                slideEffectValue = LauncherPersonalSettings.SLIDEEFFECT_ARRAY[randomN];
            } else {
            	slideEffectValue = getSlideEffectValue();
            }
    		/** AUT: henryyu1986@163.com DATE: 2012-1-12 E */
            transitAnim(true);
        }
    }

    protected void pageEndMoving() {
        if (mIsPageMoving) {
            mIsPageMoving = false;
            transitAnim(false);
            /*AUT zhanglq@bj.cobellink.com data 2012-05-18 start*/
            scrollY = 0;
            oldy = 0;
	         /*AUT zhanglq@bj.cobellink.com data 2012-05-18 end*/
            onPageEndMoving();
        }
    }

    protected boolean isPageMoving() {
        return mIsPageMoving;
    }

    // a method that subclasses can override to add behavior
    protected void onPageBeginMoving() {
        /** RK_ID: RK_MAGIC_GESTRUE . AUT: zhanglz1 . DATE: 2012-09-27 . S**/
    	if(!isWallpaperShowing){
    	      showScrollingIndicator(false);
    	}
        /** RK_ID: RK_MAGIC_GESTRUE . AUT: zhanglz1 . DATE: 2012-09-27 . E**/

    }

    // a method that subclasses can override to add behavior
    protected void onPageEndMoving() {
      hideScrollingIndicator(false);
    }

    /**
     * Registers the specified listener on each page contained in this workspace.
     *
     * @param l The listener used to respond to long clicks.
     */
    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        mLongClickListener = l;
        final int count = getPageCount();
        for (int i = 0; i < count; i++) {
            getPageAt(i).setOnLongClickListener(l);
        }
    }

    @Override
    public void scrollBy(int x, int y) {
        scrollTo(mUnboundedScrollX + x, mScrollY + y);
    }

    @Override
    public void scrollTo(int x, int y) {
        mUnboundedScrollX = x;
        /*modify zhanglq@bj.cobellink.com DATA 2012-03-01 start*/
//        if (x < 0) {
//            super.scrollTo(0, y);
//            if (mAllowOverScroll) {
//                overScroll(x);
//            }
//        } else if (x > mMaxScrollX) {
//            super.scrollTo(mMaxScrollX, y);
//            if (mAllowOverScroll) {
//                overScroll(x - mMaxScrollX);
//            }
//        } else {
//            super.scrollTo(x, y);
//        }
        mOverScrollX = x;
        super.scrollTo(x, y);
        /*modify zhanglq@bj.cobellink.com DATA 2012-03-01 end*/
        mTouchX = x;
        mSmoothingTime = System.nanoTime() / NANOTIME_DIV;
    }

    // we moved this functionality to a helper function so SmoothPagedView can reuse it
    protected boolean computeScrollHelper() {
        if (mScroller.computeScrollOffset()) {
            // Don't bother scrolling if the page does not need to be moved
            if (mScrollX != mScroller.getCurrX() || mScrollY != mScroller.getCurrY()) {
                mDirtyPageAlpha = true;
                scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            }
            /*AUT zhanglq@bj.cobellink.com DATA 2012-06-13 start*/
            else if(mScroller.getCurrX() == mScroller.getFinalX()){
            	mScroller.forceFinished(true);
            	scrollEnd();
            }
            /*AUT zhanglq@bj.cobellink.com DATA 2012-06-13 end*/
            invalidate();
            return true;
        } 
        else 
        {
            if (mScroll)
            {
                scrollEnd();
            }
            if (mNextPage != INVALID_PAGE) {
                mDirtyPageAlpha = true;
                mCurrentPage = Math.max(0, Math.min(mNextPage, getPageCount() - 1));
                mNextPage = INVALID_PAGE;
                notifyPageSwitchListener();
    
                // Load the associated pages if necessary
                if (mDeferLoadAssociatedPagesUntilScrollCompletes) {
                    loadAssociatedPages(mCurrentPage);
                    mDeferLoadAssociatedPagesUntilScrollCompletes = false;
                }
    
                // We don't want to trigger a page end moving unless the page has settled
                // and the user has stopped scrolling
                if (mTouchState == TOUCH_STATE_REST) {
                    pageEndMoving();
                }
    
                // Notify the user when the page changes
                if (AccessibilityManager.getInstance(getContext()).isEnabled()) {
                    AccessibilityEvent ev =
                        AccessibilityEvent.obtain(AccessibilityEvent.TYPE_VIEW_SCROLLED);
                    ev.getText().add(getCurrentPageDescription());
                    sendAccessibilityEventUnchecked(ev);
                }
                
                return true;
            }
        }
        return false;
    }

    @Override
    public void computeScroll() {
        computeScrollHelper();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!mIsDataReady) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException("Workspace can only be used in EXACTLY mode.");
        }

        /* Allow the height to be set as WRAP_CONTENT. This allows the particular case
         * of the All apps view on XLarge displays to not take up more space then it needs. Width
         * is still not allowed to be set as WRAP_CONTENT since many parts of the code expect
         * each page to have the same width.
         */
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int maxChildHeight = 0;

        final int verticalPadding = mPaddingTop + mPaddingBottom;
        final int horizontalPadding = mPaddingLeft + mPaddingRight;


        // The children are given the same width and height as the workspace
        // unless they were set to WRAP_CONTENT
        if (DEBUG) Log.d(TAG, "PagedView.onMeasure(): " + widthSize + ", " + heightSize);
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            // disallowing padding in paged view (just pass 0)
            final View child = getPageAt(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();

            int childWidthMode;
            if (lp.width == LayoutParams.WRAP_CONTENT) {
                childWidthMode = MeasureSpec.AT_MOST;
            } else {
                childWidthMode = MeasureSpec.EXACTLY;
            }

            int childHeightMode;
            if (lp.height == LayoutParams.WRAP_CONTENT) {
                childHeightMode = MeasureSpec.AT_MOST;
            } else {
                childHeightMode = MeasureSpec.EXACTLY;
            }

            final int childWidthMeasureSpec =
                MeasureSpec.makeMeasureSpec(widthSize - horizontalPadding, childWidthMode);
            final int childHeightMeasureSpec =
                MeasureSpec.makeMeasureSpec(heightSize - verticalPadding, childHeightMode);

            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            maxChildHeight = Math.max(maxChildHeight, child.getMeasuredHeight());
            if (DEBUG) Log.d(TAG, "\tmeasure-child" + i + ": " + child.getMeasuredWidth() + ", "
                    + child.getMeasuredHeight());
        }

        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = maxChildHeight + verticalPadding;
        }

        setMeasuredDimension(widthSize, heightSize);

        // We can't call getChildOffset/getRelativeChildOffset until we set the measured dimensions.
        // We also wait until we set the measured dimensions before flushing the cache as well, to
        // ensure that the cache is filled with good values.
        invalidateCachedOffsets();
        updateScrollingIndicatorPosition();

        if (childCount > 0) {
            mMaxScrollX = getChildOffset(childCount - 1) - getRelativeChildOffset(childCount - 1);
        } else {
            mMaxScrollX = 0;
        }
    }

    protected void scrollToNewPageWithoutMovingPages(int newCurrentPage) {
        int newX = getChildOffset(newCurrentPage) - getRelativeChildOffset(newCurrentPage);
        int delta = newX - mScrollX;

        final int pageCount = getChildCount();
        for (int i = 0; i < pageCount; i++) {
            View page = (View) getPageAt(i);
            page.setX(page.getX() + delta);
        }
        setCurrentPage(newCurrentPage);
    }

    // A layout scale of 1.0f assumes that the pages, in their unshrunken state, have a
    // scale of 1.0f. A layout scale of 0.8f assumes the pages have a scale of 0.8f, and
    // tightens the layout accordingly
    public void setLayoutScale(float childrenScale) {
        mLayoutScale = childrenScale;
        invalidateCachedOffsets();

        // Now we need to do a re-layout, but preserving absolute X and Y coordinates
        int childCount = getChildCount();
        float childrenX[] = new float[childCount];
        float childrenY[] = new float[childCount];
        for (int i = 0; i < childCount; i++) {
            final View child = getPageAt(i);
            childrenX[i] = child.getX();
            childrenY[i] = child.getY();
        }
        
        
        // Trigger a full re-layout (never just call onLayout directly!)
        int widthSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY);
        int heightSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY);
        requestLayout();
        measure(widthSpec, heightSpec);
        layout(mLeft, mTop, mRight, mBottom);
        for (int i = 0; i < childCount; i++) {
            final View child = getPageAt(i);
            child.setX(childrenX[i]);
            child.setY(childrenY[i]);
        }

        // Also, the page offset has changed  (since the pages are now smaller);
        // update the page offset, but again preserving absolute X and Y coordinates
        scrollToNewPageWithoutMovingPages(mCurrentPage);
    }

    public void setPageSpacing(int pageSpacing) {
        mPageSpacing = pageSpacing;
        invalidateCachedOffsets();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (!mIsDataReady) {
            return;
        }

        if (DEBUG) Log.d(TAG, "PagedView.onLayout()");
        final int verticalPadding = mPaddingTop + mPaddingBottom;
        final int childCount = getChildCount();
        int childLeft = 0;
        if (childCount > 0) {
            if (DEBUG) Log.d(TAG, "getRelativeChildOffset(): " + getMeasuredWidth() + ", "
                    + getChildWidth(0));
            childLeft = getRelativeChildOffset(0);

            // Calculate the variable page spacing if necessary
            if (mPageSpacing < 0) {
                setPageSpacing(((right - left) - getChildAt(0).getMeasuredWidth()) / 2);
            }
        }

        for (int i = 0; i < childCount; i++) {
            final View child = getPageAt(i);
            if (child.getVisibility() != View.GONE) {
                final int childWidth = getScaledMeasuredWidth(child);
                final int childHeight = child.getMeasuredHeight();
                int childTop = mPaddingTop;
                if (mCenterPagesVertically) {
                    childTop += ((getMeasuredHeight() - verticalPadding) - childHeight) / 2;
                }

                if (DEBUG) Log.d(TAG, "\tlayout-child" + i + ": " + childLeft + ", " + childTop);
                child.layout(childLeft, childTop,
                        childLeft + child.getMeasuredWidth(), childTop + childHeight);
                childLeft += childWidth + mPageSpacing;
            }
        }

        if (mFirstLayout && mCurrentPage >= 0 && mCurrentPage < getChildCount()) {
            setHorizontalScrollBarEnabled(false);
            int newX = getChildOffset(mCurrentPage) - getRelativeChildOffset(mCurrentPage);
            scrollTo(newX, 0);
            mScroller.setFinalX(newX);
            setHorizontalScrollBarEnabled(true);
            mFirstLayout = false;
        }

        if (mFirstLayout && mCurrentPage >= 0 && mCurrentPage < getChildCount()) {
            mFirstLayout = false;
        }
    }

    protected void forceUpdateAdjacentPagesAlpha() {
        mDirtyPageAlpha = true;
        updateAdjacentPagesAlpha();
    }

    protected void updateAdjacentPagesAlpha() {
        if (mFadeInAdjacentScreens) {
            if (mDirtyPageAlpha || (mTouchState == TOUCH_STATE_SCROLLING) || !mScroller.isFinished()) {
                int screenWidth = getMeasuredWidth() - mPaddingLeft - mPaddingRight;
                int halfScreenSize = screenWidth / 2;
                int screenCenter = mScrollX + halfScreenSize + mPaddingLeft;
                final int childCount = getChildCount();
                for (int i = 0; i < childCount; ++i) {
                    View layout = (View) getPageAt(i);
                    int childWidth = getScaledMeasuredWidth(layout);
                    int halfChildWidth = (childWidth / 2);
                    int childCenter = getChildOffset(i) + halfChildWidth;

                    // On the first layout, we may not have a width nor a proper offset, so for now
                    // we should just assume full page width (and calculate the offset according to
                    // that).
                    if (childWidth <= 0) {
                        childWidth = screenWidth;
                        childCenter = (i * childWidth) + (childWidth / 2);
                    }

                    int d = halfChildWidth;
                    int distanceFromScreenCenter = childCenter - screenCenter;
                    if (distanceFromScreenCenter > 0) {
                        if (i > 0) {
                            d += getScaledMeasuredWidth(getPageAt(i - 1)) / 2;
                        } else {
                            continue;
                        }
                    } else {
                        if (i < childCount - 1) {
                            d += getScaledMeasuredWidth(getPageAt(i + 1)) / 2;
                        } else {
                            continue;
                        }
                    }
                    d += mPageSpacing;

                    // Preventing potential divide-by-zero
                    d = Math.max(1, d);

                    float dimAlpha = (float) (Math.abs(distanceFromScreenCenter)) / d;
                    dimAlpha = Math.max(0.0f, Math.min(1.0f, (dimAlpha * dimAlpha)));
                    float alpha = 1.0f - dimAlpha;

                    if (alpha < ALPHA_QUANTIZE_LEVEL) {
                        alpha = 0.0f;
                    } else if (alpha > 1.0f - ALPHA_QUANTIZE_LEVEL) {
                        alpha = 1.0f;
                    }

                    // Due to the way we're setting alpha on our children in PagedViewCellLayout,
                    // this optimization causes alpha to not be properly updated sometimes (repro
                    // case: in xlarge mode, swipe to second page in All Apps, then click on "My
                    // Apps" tab. the page will have alpha 0 until you swipe it). Removing
                    // optimization fixes the issue, but we should fix this in a better manner
                    //if (Float.compare(alpha, layout.getAlpha()) != 0) {
                        layout.setAlpha(alpha);
                    //}
                }
                mDirtyPageAlpha = false;
            }
        }
    }
	
    protected void screenScrolled(int screenCenter) {
        if (isScrollingIndicatorEnabled()) {
            updateScrollingIndicator();
        }
        if (mFadeInAdjacentScreens) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child != null) {
                    float scrollProgress = getScrollProgress(screenCenter, child, i);
                    float alpha = 1 - Math.abs(scrollProgress);
                    child.setAlpha(alpha);
                    child.invalidate();
                }
            }
            invalidate();
        }
    }
/*
    @Override
    protected void onViewAdded(View child) {
        super.onViewAdded(child);

        // This ensures that when children are added, they get the correct transforms / alphas
        // in accordance with any scroll effects.
        mForceScreenScrolled = true;
        invalidate();
        invalidateCachedOffsets();
    }
    */

    protected void invalidateCachedOffsets() {
        int count = getChildCount();
        if (count == 0) {
            mChildOffsets = null;
            mChildRelativeOffsets = null;
            mChildOffsetsWithLayoutScale = null;
            return;
        }

        mChildOffsets = new int[count];
        mChildRelativeOffsets = new int[count];
        mChildOffsetsWithLayoutScale = new int[count];
        for (int i = 0; i < count; i++) {
            mChildOffsets[i] = -1;
            mChildRelativeOffsets[i] = -1;
            mChildOffsetsWithLayoutScale[i] = -1;
        }
    }

    protected int getChildOffset(int index) {
        int[] childOffsets = Float.compare(mLayoutScale, 1f) == 0 ?
                mChildOffsets : mChildOffsetsWithLayoutScale;

        if (childOffsets != null && childOffsets[index] != -1) {
            return childOffsets[index];
        } else {
            if (getChildCount() == 0)
                return 0;

            int offset = getRelativeChildOffset(0);
            for (int i = 0; i < index; ++i) {
                offset += getScaledMeasuredWidth(getPageAt(i)) + mPageSpacing;
            }
            if (childOffsets != null) {
                childOffsets[index] = offset;
            }
            return offset;
        }
//    	if (getChildCount() == 0)
//            return 0;
//
//        int offset = getRelativeChildOffset(0);
//        for (int i = 0; i < index; ++i) {
//            offset += getScaledMeasuredWidth(getPageAt(i)) + mPageSpacing;
//        }
//        return offset;
    }

    protected int getRelativeChildOffset(int index) {
    	/*RK_ID: RK_HAWAII . AUT: Andy . DATE: 2012-08-8 . S*/
    	if (mChildRelativeOffsets != null && mChildRelativeOffsets.length <= index){
    		index = mChildRelativeOffsets.length - 1;
    	}
    	/*RK_ID: RK_HAWAII . AUT: Andy . DATE: 2012-08-8 . E*/
        if (mChildRelativeOffsets != null && mChildRelativeOffsets[index] != -1) {
            return mChildRelativeOffsets[index];
        } else {
            final int padding = mPaddingLeft + mPaddingRight;
            final int offset = mPaddingLeft +
                    (getMeasuredWidth() - padding - getChildWidth(index)) / 2;
            if (mChildRelativeOffsets != null) {
                mChildRelativeOffsets[index] = offset;
            }
            return offset;
        }
    }

    protected int getScaledRelativeChildOffset(int index) {
        final int padding = mPaddingLeft + mPaddingRight;
        final int offset = mPaddingLeft + (getMeasuredWidth() - padding -
                getScaledMeasuredWidth(getPageAt(index))) / 2;
        return offset;
    }

    protected int getScaledMeasuredWidth(View child) {
        // This functions are called enough times that it actually makes a difference in the
        // profiler -- so just inline the max() here
        final int measuredWidth = child.getMeasuredWidth();
        final int minWidth = mMinimumWidth;
        final int maxWidth = (minWidth > measuredWidth) ? minWidth : measuredWidth;
        return (int) (maxWidth * mLayoutScale + 0.5f);
    }
    
    protected int getScaledMeasuredHeight(View child) {
        // This functions are called enough times that it actually makes a difference in the
        // profiler -- so just inline the max() here
        final int measuredHeight = child.getMeasuredHeight();
        final int minHeight = 0;
        final int maxHeight = (minHeight > measuredHeight) ? minHeight : measuredHeight;
        return (int) (maxHeight * mLayoutScale + 0.5f);
    }

    protected void getVisiblePages(int[] range) {
        final int pageCount = getChildCount();
        if (pageCount > 0) {
            final int pageWidth = getScaledMeasuredWidth(getPageAt(0));
            final int screenWidth = getMeasuredWidth();
            int x = getScaledRelativeChildOffset(0) + pageWidth;
            int leftScreen = 0;
            int rightScreen = 0;
            while (x <= mScrollX && leftScreen < pageCount - 1) {
                leftScreen++;
                x += getScaledMeasuredWidth(getPageAt(leftScreen)) + mPageSpacing;
            }
            rightScreen = leftScreen;
            while (x < mScrollX + screenWidth && rightScreen < pageCount - 1) {
                rightScreen++;
                x += getScaledMeasuredWidth(getPageAt(rightScreen)) + mPageSpacing;
            }
            range[0] = leftScreen;
            range[1] = rightScreen;
        } else {
            range[0] = -1;
            range[1] = -1;
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int halfScreenSize = getMeasuredWidth() / 2;
        // mOverScrollX is equal to mScrollX when we're within the normal scroll range. Otherwise
        // it is equal to the scaled overscroll position.
        int screenCenter;
        if (mFadeInAdjacentScreens)
        {
            screenCenter = mScrollX + halfScreenSize;
        }
        else
        {
            screenCenter = mOverScrollX + halfScreenSize;
        }

        if (screenCenter != mLastScreenCenter || mForceScreenScrolled) {
            screenScrolled(screenCenter);
            updateAdjacentPagesAlpha();
            mLastScreenCenter = screenCenter;
            mForceScreenScrolled = false;
        }

        // Find out which screens are visible; as an optimization we only call draw on them
        final int pageCount = getChildCount();
        if (pageCount > 0) {
            getVisiblePages(mTempVisiblePagesRange);
            final int leftScreen = mTempVisiblePagesRange[0];
            final int rightScreen = mTempVisiblePagesRange[1];
            if (leftScreen != -1 && rightScreen != -1) {
                final long drawingTime = getDrawingTime();
                // Clip to the bounds
                canvas.save();
                canvas.clipRect(mScrollX, mScrollY, mScrollX + mRight - mLeft,
                        mScrollY + mBottom - mTop);

                for (int i = rightScreen; i >= leftScreen; i--) {
                    drawChild(canvas, getPageAt(i), drawingTime);
                }
                canvas.restore();
            }
        }
    }

    /*@Override
    public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
        int page = indexToPage(indexOfChild(child));
        if (page != mCurrentPage || !mScroller.isFinished()) {
        	 *//***RK_ID:RK_BUGFIX_172993 AUT:zhanglz1@lenovo.com.DATE:2012-12-11. S***//*        
        	当组里的某个子视图需要被定位在屏幕的某个矩形范围时，调用此方法。重载此方法的ViewGroup可确认以下几点：
          　　* 子项目将是组里的直系子项
          　　* 矩形将在子项目的坐标体系中
      　　重载此方法的ViewGroup应该支持以下几点：
          　　* 若矩形已经是可见的，则没有东西会改变
          　　* 为使矩形区域全部可见，视图将可以被滚动显示
      　　参数
      　　child        发出请求的子视图
      　　rectangle    子项目坐标系内的矩形，即此子项目希望在屏幕上的定位
      　　immediate   设为true，则禁止动画和平滑移动滚动条
         返回值
            进行了滚动操作的这个组（group），是否处理此操作。
        	//此处修改后为android源码，不知因何原因改之前将判断条件去掉 导致bug172993
        	if(((XLauncher) mContext) != null && !((XLauncher) mContext).isWorkspaceLocked()){
			    snapToPage(page);
        	}
			 *//***RK_ID:RK_BUGFIX_172993 AUT:zhanglz1@lenovo.com.DATE:2012-12-11. S***//*        
			return true;
        }
        return false;
    }*/

    @Override
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        int focusablePage;
        if (mNextPage != INVALID_PAGE) {
            focusablePage = mNextPage;
        } else {
            focusablePage = mCurrentPage;
        }
        View v = getPageAt(focusablePage);
        if (v != null) {
            return v.requestFocus(direction, previouslyFocusedRect);
        }
        return false;
    }

    @Override
    public boolean dispatchUnhandledMove(View focused, int direction) {
        if (direction == View.FOCUS_LEFT) {
            if (getCurrentPage() > 0) {
                snapToPage(getCurrentPage() - 1);
                return true;
            }
        } else if (direction == View.FOCUS_RIGHT) {
            if (getCurrentPage() < getPageCount() - 1) {
                snapToPage(getCurrentPage() + 1);
                return true;
            }
        }
        return super.dispatchUnhandledMove(focused, direction);
    }

    @Override
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        /*** fixbug 169243 . AUT: zhaoxy . DATE: 2012-08-20. START ***/
        if (mCurrentPage >= 0 && mCurrentPage < getPageCount()) {
            getPageAt(mCurrentPage).addFocusables(views, direction);
            if (direction == View.FOCUS_LEFT) {
                if (mCurrentPage > 0) {
                    getPageAt(mCurrentPage - 1).addFocusables(views, direction);
                }
            } else if (direction == View.FOCUS_RIGHT) {
                if (mCurrentPage < getPageCount() - 1) {
                    getPageAt(mCurrentPage + 1).addFocusables(views, direction);
                }
            }
        }
        /*** fixbug 169243 . AUT: zhaoxy . DATE: 2012-08-20. END ***/
    }

    /**
     * If one of our descendant views decides that it could be focused now, only
     * pass that along if it's on the current page.
     *
     * This happens when live folders requery, and if they're off page, they
     * end up calling requestFocus, which pulls it on page.
     */
    @Override
    public void focusableViewAvailable(View focused) {
        View current = getPageAt(mCurrentPage);
        View v = focused;
        while (true) {
            if (v.equals(current)){
                super.focusableViewAvailable(focused);
                return;
            }
            if (v == this) {
                return;
            }
            ViewParent parent = v.getParent();
            if (parent instanceof View) {
                v = (View)v.getParent();
            } else {
                return;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        if (disallowIntercept) {
            // We need to make sure to cancel our long press if
            // a scrollable widget takes over touch events
            final View currentPage = getPageAt(mCurrentPage);
            currentPage.cancelLongPress();
        }
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    /**
     * Return true if a tap at (x, y) should trigger a flip to the previous page.
     */
    protected boolean hitsPreviousPage(float x, float y) {
        return (x < getRelativeChildOffset(mCurrentPage) - mPageSpacing);
    }

    /**
     * Return true if a tap at (x, y) should trigger a flip to the next page.
     */
    protected boolean hitsNextPage(float x, float y) {
        return  (x > (getMeasuredWidth() - getRelativeChildOffset(mCurrentPage) + mPageSpacing));
    }

    /*XMUI S*/
    public final static int TOUCH_MODE_CHILEDMODE = 1;
    public final static int TOUCH_MODE_LAUNCHERMODE = 2;
    public final static int TOUCH_MODE_FREEMODE = 0;
    public final static int TOUCH_MODE_CHILD_SCALE = 3;
    
    public int touchMode = TOUCH_MODE_FREEMODE;
    
    public boolean isTouchLauncherMode(){
    	return (touchMode == TOUCH_MODE_FREEMODE) || (touchMode == TOUCH_MODE_LAUNCHERMODE);
    }
    
    public boolean isTouchChiledMode(){
    	return (touchMode == TOUCH_MODE_FREEMODE) || (touchMode == TOUCH_MODE_CHILEDMODE);
    }
    
    public void setTouchMode(int mode){
    	touchMode = mode;
    }    
    /*XMUI E*/
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
    	/*RK_ID: RK_HAWAII . AUT: Andy . DATE: 2012-08-22 . S*/
    	//if (HawaiiHelp.sIsHawaiiPageEnabled && this instanceof AppsCustomizePagedView && mCurrentPage == 0 
    	//		&& !HawaiiHelp.isHawaiiPageScrollEnabled) {
    	//	return false;
    	//}
    	/*RK_ID: RK_HAWAII . AUT: Andy . DATE: 2012-08-22 . E*/
        /*
         * This method JUST determines whether we want to intercept the motion.
         * If we return true, onTouchEvent will be called and we do the actual
         * scrolling there.
         */
        acquireVelocityTrackerAndAddMovement(ev);

        // Skip touch handling if there are no pages to swipe
        if (getChildCount() <= 0) return super.onInterceptTouchEvent(ev);

        /*
         * Shortcut the most recurring case: the user is in the dragging
         * state and he is moving his finger.  We want to intercept this
         * motion.
         */
        final int action = ev.getAction();
        if ((action == MotionEvent.ACTION_MOVE) &&
                (mTouchState == TOUCH_STATE_SCROLLING)) {
            return true;
        }

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE: {
                /*
                 * mIsBeingDragged == false, otherwise the shortcut would have caught it. Check
                 * whether the user has moved far enough from his original down touch.
                 */
                if (mActivePointerId != INVALID_POINTER) {
                    determineScrollingStart(ev);
                    break;
                }
                // if mActivePointerId is INVALID_POINTER, then we must have missed an ACTION_DOWN
                // event. in that case, treat the first occurence of a move event as a ACTION_DOWN
                // i.e. fall through to the next case (don't break)
                // (We sometimes miss ACTION_DOWN events in Workspace because it ignores all events
                // while it's small- this was causing a crash before we checked for INVALID_POINTER)
            }

            case MotionEvent.ACTION_DOWN: {
                final float x = ev.getX();
                final float y = ev.getY();
                // Remember location of down touch
                mDownMotionX = x;
                mLastMotionX = x;
                mLastMotionY = y;
                mLastMotionXRemainder = 0;
                mTotalMotionX = 0;
                mActivePointerId = ev.getPointerId(0);
                mAllowLongPress = true;

                /*
                 * If being flinged and user touches the screen, initiate drag;
                 * otherwise don't.  mScroller.isFinished should be false when
                 * being flinged.
                 */
//                final int xDist = Math.abs(mScroller.getFinalX() - mScroller.getCurrX());
                /*** fixbug 165582 . AUT: zhangliqiang . DATE: 2012-06-04. START***/
                //final boolean finishedScrolling = (mScroller.isFinished() || xDist < mTouchSlop);
                final boolean finishedScrolling = mScroller.isFinished();
                /*** fixbug 165582 . AUT: zhangliqiang . DATE: 2012-06-04. END***/
                if (finishedScrolling) {
                    mTouchState = TOUCH_STATE_REST;
                    mScroller.abortAnimation();
                    scrollEnd();
                } else {
                    mTouchState = TOUCH_STATE_SCROLLING;
                }

                // check if this can be the beginning of a tap on the side of the pages
                // to scroll the current page
                if (mTouchState != TOUCH_STATE_PREV_PAGE && mTouchState != TOUCH_STATE_NEXT_PAGE) {
                    if (getChildCount() > 0) {
                        if (hitsPreviousPage(x, y)) {
                            mTouchState = TOUCH_STATE_PREV_PAGE;
                        } else if (hitsNextPage(x, y)) {
                            mTouchState = TOUCH_STATE_NEXT_PAGE;
                        }
                    }
                }
                
                
                
                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mTouchState = TOUCH_STATE_REST;
                mAllowLongPress = false;
                mActivePointerId = INVALID_POINTER;
                releaseVelocityTracker();
                
                                
                break;

            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                releaseVelocityTracker();
                
                
                
                break;
            default:
                break;
        }

        /*
         * The only time we want to intercept motion events is if we are in the
         * drag mode.
         */
        return mTouchState != TOUCH_STATE_REST;
    }

    protected void animateClickFeedback(View v, final Runnable r) {
        // animate the view slightly to show click feedback running some logic after it is "pressed"
        ObjectAnimator anim = (ObjectAnimator) AnimatorInflater.
                loadAnimator(mContext, R.anim.paged_view_click_feedback);
        anim.setTarget(v);
        anim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationRepeat(Animator animation) {
                r.run();
            }
        });
        anim.start();
    }

    protected void determineScrollingStart(MotionEvent ev) {
        determineScrollingStart(ev, 1.0f);
    }

    /*
     * Determines if we should change the touch state to start scrolling after the
     * user moves their touch point too far.
     */
    protected void determineScrollingStart(MotionEvent ev, float touchSlopScale) {
        /*
         * Locally do absolute value. mLastMotionX is set to the y value
         * of the down event.
         */
        final int pointerIndex = ev.findPointerIndex(mActivePointerId);
        if (pointerIndex == -1) {
            return;
        }
        final float x = ev.getX(pointerIndex);
        final float y = ev.getY(pointerIndex);
        final int xDiff = (int) Math.abs(x - mLastMotionX);
        final int yDiff = (int) Math.abs(y - mLastMotionY);

        final int touchSlop = Math.round(touchSlopScale * mTouchSlop);
        boolean xPaged = xDiff > mPagingTouchSlop;
        boolean xMoved = xDiff > touchSlop;
        boolean yMoved = yDiff > touchSlop;

        if (xMoved || xPaged || yMoved) {
            if (mUsePagingTouchSlop ? xPaged : xMoved) {
				// XMUI
				
				    /** RK_ID: RK_MAGIC_GESTRUE . AUT: zhanglz1 . DATE: 2012-09-27 . S**/
					if (xMoved && isTouchLauncherMode()&& xDiff > yDiff) {
						mTouchState = TOUCH_STATE_SCROLLING;
						mTotalMotionX += Math.abs(mLastMotionX - x);
						mLastMotionX = x;
						mLastMotionXRemainder = 0;
						mTouchX = mScrollX;
						mSmoothingTime = System.nanoTime() / NANOTIME_DIV;
						pageBeginMoving();
					
				    /** RK_ID: RK_MAGIC_GESTRUE . AUT: zhanglz1 . DATE: 2012-09-27 . E**/

				}
            }
            // Either way, cancel any pending longpress
            cancelCurrentPageLongPress();
        }
    }

    protected void cancelCurrentPageLongPress() {
        if (mAllowLongPress) {
            mAllowLongPress = false;
            // Try canceling the long press. It could also have been scheduled
            // by a distant descendant, so use the mAllowLongPress flag to block
            // everything
            final View currentPage = getPageAt(mCurrentPage);
            if (currentPage != null) {
                currentPage.cancelLongPress();
            }
        }
    }

    protected float getScrollProgress(int screenCenter, View v, int page) {
        final int halfScreenSize = getMeasuredWidth() / 2;

        int totalDistance = getScaledMeasuredWidth(v) + mPageSpacing;
        int delta = screenCenter - (getChildOffset(page) -
                getRelativeChildOffset(page) + halfScreenSize);

        float scrollProgress = delta / (totalDistance * 1.0f);
        scrollProgress = Math.min(scrollProgress, 1.0f);
        scrollProgress = Math.max(scrollProgress, -1.0f);
        return scrollProgress;
    }

    // This curve determines how the effect of scrolling over the limits of the page dimishes
    // as the user pulls further and further from the bounds
    private float overScrollInfluenceCurve(float f) {
        f -= 1.0f;
        return f * f * f + 1.0f;
    }

    protected void acceleratedOverScroll(float amount) {
        int screenSize = getMeasuredWidth();

        // We want to reach the max over scroll effect when the user has
        // over scrolled half the size of the screen
        float f = OVERSCROLL_ACCELERATE_FACTOR * (amount / screenSize);

        if (f == 0) return;

        // Clamp this factor, f, to -1 < f < 1
        if (Math.abs(f) >= 1) {
            f /= Math.abs(f);
        }

        int overScrollAmount = (int) Math.round(f * screenSize);
        if (amount < 0) {
            mOverScrollX = overScrollAmount;
            mScrollX = 0;
        } else {
            mOverScrollX = mMaxScrollX + overScrollAmount;
            mScrollX = mMaxScrollX;
        }
        invalidate();
    }

    protected void dampedOverScroll(float amount) {
        int screenSize = getMeasuredWidth();

        float f = (amount / screenSize);

        if (f == 0) return;
        f = f / (Math.abs(f)) * (overScrollInfluenceCurve(Math.abs(f)));

        // Clamp this factor, f, to -1 < f < 1
        if (Math.abs(f) >= 1) {
            f /= Math.abs(f);
        }

        int overScrollAmount = (int) Math.round(OVERSCROLL_DAMP_FACTOR * f * screenSize);
        if (amount < 0) {
            mOverScrollX = overScrollAmount;
            mScrollX = 0;
        } else {
            mOverScrollX = mMaxScrollX + overScrollAmount;
            mScrollX = mMaxScrollX;
        }
        invalidate();
    }

    protected void overScroll(float amount) {
        dampedOverScroll(amount);
    }

    protected float maxOverScroll() {
        // Using the formula in overScroll, assuming that f = 1.0 (which it should generally not
        // exceed). Used to find out how much extra wallpaper we need for the over scroll effect
        float f = 1.0f;
        f = f / (Math.abs(f)) * (overScrollInfluenceCurve(Math.abs(f)));
        return OVERSCROLL_DAMP_FACTOR * f;
    }

    /* AUT: liuli liuli0120@use.com.cn DATE: 2011-12-20 START */
    // whether paged view loop slide
    public boolean mSlideLoop = false;
    /* AUT: liuli liuli0120@use.com.cn DATE: 2011-12-20 END */

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Skip touch handling if there are no pages to swipe
        if (getChildCount() <= 0) return super.onTouchEvent(ev);

        acquireVelocityTrackerAndAddMovement(ev);

        final int action = ev.getAction();

        switch (action & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
            mPageMoveState = PAGE_MOVE_NONE;
            /*
             * If being flinged and user touches, stop the fling. isFinished
             * will be false if being flinged.
             */
            if (!mScroller.isFinished()) {
                mScroller.abortAnimation();
                scrollEnd();
            }

            // Remember where the motion event started
            mDownMotionX = mLastMotionX = ev.getX();
            mLastMotionXRemainder = 0;
            mTotalMotionX = 0;
            mActivePointerId = ev.getPointerId(0);
            if (mTouchState == TOUCH_STATE_SCROLLING) {
                pageBeginMoving();
            }
            break;

        case MotionEvent.ACTION_MOVE:
            mPageMoveState = PAGE_MOVE_NONE;
            if (mTouchState == TOUCH_STATE_SCROLLING) {
                // Scroll to follow the motion event
                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                final float x = ev.getX(pointerIndex);
                final float deltaX = mLastMotionX + mLastMotionXRemainder - x;

                mTotalMotionX += Math.abs(deltaX);

                // Only scroll and update mLastMotionX if we have moved some discrete amount.  We
                // keep the remainder because we are actually testing if we've moved from the last
                // scrolled position (which is discrete).
                if (Math.abs(deltaX) >= 1.0f) {
                    mTouchX += deltaX;
                    mSmoothingTime = System.nanoTime() / NANOTIME_DIV;
                    if (!mDeferScrollUpdate) {
                        scrollBy((int) deltaX, 0);
                        if (DEBUG) Log.d(TAG, "onTouchEvent().Scrolling: " + deltaX);
                    } else {
                        invalidate();
                    }
                    mLastMotionX = x;
                    mLastMotionXRemainder = deltaX - (int) deltaX;
                    /*AUT zhanglq@bj.cobellink.com data 2012-05-18 start*/
                    float y = ev.getY();
                    if(oldy != 0){
                    	float deltaY = y - oldy;
                    	scrollY += deltaY;
                    }
                    oldy = y;
                    /*AUT zhanglq@bj.cobellink.com data 2012-05-18 end*/
                } else {
                    awakenScrollBars();
                }
            } else {
                determineScrollingStart(ev);
            }
            break;

        case MotionEvent.ACTION_UP:
            mPageMoveState = PAGE_MOVE_NONE;
            if (mTouchState == TOUCH_STATE_SCROLLING) {
                final int activePointerId = mActivePointerId;
                final int pointerIndex = ev.findPointerIndex(activePointerId);
                
                final float x = ev.getX(pointerIndex);
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int velocityX = (int) velocityTracker.getXVelocity(activePointerId);
                final int deltaX = (int) (x - mDownMotionX);
                final int pageWidth = getScaledMeasuredWidth(getPageAt(mCurrentPage));
                boolean isSignificantMove = Math.abs(deltaX) > pageWidth *
                        SIGNIFICANT_MOVE_THRESHOLD;
                final int snapVelocity = mSnapVelocity;

                mTotalMotionX += Math.abs(mLastMotionX + mLastMotionXRemainder - x);

                boolean isFling = mTotalMotionX > MIN_LENGTH_FOR_FLING &&
                        Math.abs(velocityX) > snapVelocity;

                // In the case that the page is moved far to one direction and then is flung
                // in the opposite direction, we use a threshold to determine whether we should
                // just return to the starting page, or if we should skip one further.
                boolean returnToOriginalPage = false;                
                /* AUT: liuli liuli0120@use.com.cn DATE: 2012-01-12 START */
                // bug : Bug 149703
                //modify zhanglq 2012-03-06
                /*
                if (Math.abs(deltaX) > pageWidth * RETURN_TO_ORIGINAL_PAGE_THRESHOLD &&
                        Math.signum(velocityX) != Math.signum(deltaX) && isFling) {
                    returnToOriginalPage = true;
                }
                */
                
                int finalPage;
                // We give flings precedence over large moves, which is why we short-circuit our
                // test for a large move if a fling has been registered. That is, a large
                // move to the left and fling to the right will register as a fling to the right.
                if (((isSignificantMove && deltaX > 0 && !isFling) ||
                        (isFling && velocityX > 0)) && mCurrentPage > 0) {
                    finalPage = returnToOriginalPage ? mCurrentPage : mCurrentPage - 1;
                    if ((mCurrentPage == getChildCount() - 1) && deltaX < 0) {
                        Log.i("Test0111", "this is last page, cannot move because deltaX < 0, so returnToOriginalPage");
                        finalPage = mCurrentPage;
                    }
                    Log.i("Test0111", "============case 1  finalPage =  " + finalPage + "    and mCurrentPage = " + mCurrentPage);
                    snapToPageWithVelocity(finalPage, velocityX);
                    /*RK_ID: RK_HAWAII . AUT: Andy . DATE: 2012-07-18 . S*/
                   // if (HawaiiHelp.sIsHawaiiPageEnabled && this instanceof AppsCustomizePagedView && finalPage == 0) {
                    //	Intent intent = new Intent("com.lenovo.leos.hw_launcher.ACTION_SLIDE_IN");
                    //	getContext().sendBroadcast(intent);
                    //}
                    /*RK_ID: RK_HAWAII . AUT: Andy . DATE: 2012-07-18 . E*/
                } else if (((isSignificantMove && deltaX < 0 && !isFling) ||
                        (isFling && velocityX < 0)) &&
                        mCurrentPage < getChildCount() - 1) {
                    finalPage = returnToOriginalPage ? mCurrentPage : mCurrentPage + 1;
                    if ((mCurrentPage == 0) && deltaX > 0) {
                        Log.i("Test0111", "this is first page, cannot move because deltaX > 0, so returnToOriginalPage");
                        finalPage = mCurrentPage;
                    }
                    Log.i("Test0111", "===============case 2  finalPage =  " + finalPage + "    and mCurrentPage = " + mCurrentPage);
                    snapToPageWithVelocity(finalPage, velocityX);
                    /*RK_ID: RK_HAWAII . AUT: Andy . DATE: 2012-08-3 . S*/
                    //if (HawaiiHelp.sIsHawaiiPageEnabled && this instanceof AppsCustomizePagedView && mCurrentPage == 0 && finalPage == 1) {
                    //	Intent intent = new Intent("com.lenovo.leos.hw_launcher.ACTION_CLOSE_ACTIVITY");
                    //	getContext().sendBroadcast(intent);
                   // }
                    /*RK_ID: RK_HAWAII . AUT: Andy . DATE: 2012-08-3 . E*/
                } else {
                	/*modify zhanglq@bj.cobellink.com DATA 2012-03-01 start*/
						if(((isSignificantMove && deltaX < 0 && !isFling) || (isFling && velocityX < 0))
                            && mCurrentPage == getChildCount() - 1){
                		     mCloseCling = true;
                	   }
                    if (!mSlideLoop) {
                        /*AUT:xingqx, xingqx@lenovo.com DATE:2012-05-18 start*/
                        //if(this instanceof AppsCustomizePagedView ) {
                         //   if(mCurrentPage == 0 && deltaX > 0) {
                         //       mPageMoveState = PAGE_MOVE_TO_FIRST;
                        //    } else if(mCurrentPage == getChildCount() - 1 && deltaX < 0) {
                         //       mPageMoveState = PAGE_MOVE_TO_END;
                         //   }
                       // }
                        /*AUT:xingqx, xingqx@lenovo.com DATE:2012-05-18 end*/
                        snapToDestination();
                    }
                    else if (((isSignificantMove && deltaX > 0 && !isFling) || (isFling && velocityX > 0))
                            && mCurrentPage == 0) {
                    	finalPage = returnToOriginalPage ? mCurrentPage : getChildCount() - 1;
                        int newX = getWidth()*(getChildCount()-1);
                        final int sX = getWidth()*(getChildCount()-1)+mUnboundedScrollX+getWidth();
                        final int delta = newX-sX;
                        snapToPage(finalPage, sX,delta, PAGE_SNAP_ANIMATION_DURATION);
                    } else if (((isSignificantMove && deltaX < 0 && !isFling) || (isFling && velocityX < 0))
                            && mCurrentPage == getChildCount() - 1) {
                    	finalPage = returnToOriginalPage ? mCurrentPage : 0;
                        int newX = 0;
                        final int sX = mUnboundedScrollX-getWidth()*(getChildCount()-1)-getWidth();
                        final int delta = newX - sX;
                        snapToPage(finalPage, sX,delta, PAGE_SNAP_ANIMATION_DURATION);
                    } else {
                        snapToDestination();
                    }
                    /*modify zhanglq@bj.cobellink.com DATA 2012-03-01 end*/
                    
                    /* AUT: liuli liuli0120@use.com.cn DATE: 2012-01-12 END */
                }
            } else if (mTouchState == TOUCH_STATE_PREV_PAGE) {
                // at this point we have not moved beyond the touch slop
                // (otherwise mTouchState would be TOUCH_STATE_SCROLLING), so
                // we can just page
                int nextPage = Math.max(0, mCurrentPage - 1);
                if (nextPage != mCurrentPage) {
                    snapToPage(nextPage);
                } else {
                    snapToDestination();
                }
            } else if (mTouchState == TOUCH_STATE_NEXT_PAGE) {
                // at this point we have not moved beyond the touch slop
                // (otherwise mTouchState would be TOUCH_STATE_SCROLLING), so
                // we can just page
                int nextPage = Math.min(getChildCount() - 1, mCurrentPage + 1);
                if (nextPage != mCurrentPage) {
                    snapToPage(nextPage);
                } else {
                    snapToDestination();
                }
            /** RK_ID: RK_MAGIC_GESTRUE . AUT: zhanglz1 . DATE: 2012-09-27 . S**/
			} else {
				onUnhandledTap(ev);
			}
            mTouchState = TOUCH_STATE_REST;
            mActivePointerId = INVALID_POINTER;
            releaseVelocityTracker();
            break;

        case MotionEvent.ACTION_CANCEL:
            if (mTouchState == TOUCH_STATE_SCROLLING) {
                /* AUT: liuli liuli0120@use.com.cn DATE: 2012-01-12 START */
                // bug : Bug 149703
//                snapToDestination();
                snapForActionCancel(ev);
                /* AUT: liuli liuli0120@use.com.cn DATE: 2012-01-12 END */
            }
            mTouchState = TOUCH_STATE_REST;
            mActivePointerId = INVALID_POINTER;
            releaseVelocityTracker();
            break;

        case MotionEvent.ACTION_POINTER_UP:
            onSecondaryPointerUp(ev);
            break;
            
        default:
            break;
        }

        return true;
    }

    /* AUT: liuli liuli0120@use.com.cn DATE: 2012-01-12 START */
    private void snapForActionCancel(MotionEvent ev) {
        Log.i("Test0111", "snapForActionCancel");

        final int activePointerId = mActivePointerId;
        final int pointerIndex = ev.findPointerIndex(activePointerId);
        final float x = ev.getX(pointerIndex);
        final VelocityTracker velocityTracker = mVelocityTracker;
        velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
        int velocityX = (int) velocityTracker.getXVelocity(activePointerId);
        final int deltaX = (int) (x - mDownMotionX);
        boolean isSignificantMove = Math.abs(deltaX) > MIN_LENGTH_FOR_MOVE;
        final int snapVelocity = mSnapVelocity;

        mTotalMotionX += Math.abs(mLastMotionX + mLastMotionXRemainder - x);

        // In the case that the page is moved far to one direction and then is flung
        // in the opposite direction, we use a threshold to determine whether we should
        // just return to the starting page, or if we should skip one further.
        boolean returnToOriginalPage = false;
        /*
        final int pageWidth = getScaledMeasuredWidth(getPageAt(mCurrentPage));
        if (Math.abs(deltaX) > pageWidth * RETURN_TO_ORIGINAL_PAGE_THRESHOLD
                && Math.signum(velocityX) != Math.signum(deltaX)) {
            returnToOriginalPage = true;
        }
        */

        boolean isFling = mTotalMotionX > MIN_LENGTH_FOR_FLING && Math.abs(velocityX) > snapVelocity;

        Log.i("Test0111", "isSignificantMove = " + isSignificantMove + "   and deltaX = " + deltaX);
        Log.i("Test0111", "isFling = " + isFling + "    and mTotalMotionX = " + mTotalMotionX);
        Log.i("Test0111", "velocityX = " + velocityX + "    and snapVelocity = " + snapVelocity);

        int finalPage;
        // We give flings precedence over large moves, which is why we short-circuit our
        // test for a large move if a fling has been registered. That is, a large
        // move to the left and fling to the right will register as a fling to the right.
        if (((isSignificantMove && deltaX > 0 && !isFling) || (isFling && velocityX > 0)) && mCurrentPage > 0) {
            finalPage = returnToOriginalPage ? mCurrentPage : mCurrentPage - 1;
            snapToPageWithVelocity(finalPage, velocityX);
        } else if (((isSignificantMove && deltaX < 0 && !isFling) || (isFling && velocityX < 0))
                && mCurrentPage < getChildCount() - 1) {
            finalPage = returnToOriginalPage ? mCurrentPage : mCurrentPage + 1;
            snapToPageWithVelocity(finalPage, velocityX);
        } else {
            if (!mSlideLoop)
                snapToDestination();
            else if (((isSignificantMove && deltaX > 0 && !isFling) || (isFling && velocityX > 0)) && mCurrentPage == 0) {
                finalPage = returnToOriginalPage ? mCurrentPage : getChildCount() - 1;
                snapToPageWithVelocity(finalPage, velocityX);
            } else if (((isSignificantMove && deltaX < 0 && !isFling) || (isFling && velocityX < 0))
                    && mCurrentPage == getChildCount() - 1) {
                finalPage = returnToOriginalPage ? mCurrentPage : 0;
                snapToPageWithVelocity(finalPage, velocityX);
            } else {
                snapToDestination();
            }
        }
    }

    /* AUT: liuli liuli0120@use.com.cn DATE: 2012-01-12 END */

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if ((event.getSource() & InputDevice.SOURCE_CLASS_POINTER) != 0) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_SCROLL: {
                    // Handle mouse (or ext. device) by shifting the page depending on the scroll
                    final float vscroll;
                    final float hscroll;
                    if ((event.getMetaState() & KeyEvent.META_SHIFT_ON) != 0) {
                        vscroll = 0;
                        hscroll = event.getAxisValue(MotionEvent.AXIS_VSCROLL);
                    } else {
                        vscroll = -event.getAxisValue(MotionEvent.AXIS_VSCROLL);
                        hscroll = event.getAxisValue(MotionEvent.AXIS_HSCROLL);
                    }
                    if (hscroll != 0 || vscroll != 0) {
                        if (hscroll > 0 || vscroll > 0) {
                            scrollRight();
                        } else {
                            scrollLeft();
                        }
                        return true;
                    }
                }
            }
        }
        return super.onGenericMotionEvent(event);
    }

    private void acquireVelocityTrackerAndAddMovement(MotionEvent ev) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);
    }

    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >>
                MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            // TODO: Make this decision more intelligent.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mLastMotionX = mDownMotionX = ev.getX(newPointerIndex);
            mLastMotionY = ev.getY(newPointerIndex);
            mLastMotionXRemainder = 0;
            mActivePointerId = ev.getPointerId(newPointerIndex);
            if (mVelocityTracker != null) {
                mVelocityTracker.clear();
            }
        }
    }

    protected void onUnhandledTap(MotionEvent ev) {}

    @Override
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        int page = indexToPage(indexOfChild(child));
        if (page >= 0 && page != getCurrentPage() && !isInTouchMode()) {
            snapToPage(page);
        }
    }

    protected int getChildIndexForRelativeOffset(int relativeOffset) {
        final int childCount = getChildCount();
        int left;
        int right;
        for (int i = 0; i < childCount; ++i) {
            left = getRelativeChildOffset(i);
            right = (left + getScaledMeasuredWidth(getPageAt(i)));
            if (left <= relativeOffset && relativeOffset <= right) {
                return i;
            }
        }
        return -1;
    }

    protected int getChildWidth(int index) {
        // This functions are called enough times that it actually makes a difference in the
        // profiler -- so just inline the max() here
        /* RK_ID: RK_SETTING. AUT: liuli1 . DATE: 2012-03-19 . START */
        if (index >= getPageCount() || getPageAt(index) == null) {
            return mMinimumWidth;
        }
        /* RK_ID: RK_SETTING. AUT: liuli1 . DATE: 2012-03-19 . END */
        final int measuredWidth = getPageAt(index).getMeasuredWidth();
        final int minWidth = mMinimumWidth;
        return (minWidth > measuredWidth) ? minWidth : measuredWidth;
    }

    int getPageNearestToCenterOfScreen() {
        int minDistanceFromScreenCenter = Integer.MAX_VALUE;
        int minDistanceFromScreenCenterIndex = -1;
        int screenCenter = mScrollX + (getMeasuredWidth() / 2);
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; ++i) {
            View layout = (View) getPageAt(i);
            int childWidth = getScaledMeasuredWidth(layout);
            int halfChildWidth = (childWidth / 2);
            int childCenter = getChildOffset(i) + halfChildWidth;
            int distanceFromScreenCenter = Math.abs(childCenter - screenCenter);
            if (distanceFromScreenCenter < minDistanceFromScreenCenter) {
                minDistanceFromScreenCenter = distanceFromScreenCenter;
                minDistanceFromScreenCenterIndex = i;
            }
        }
        return minDistanceFromScreenCenterIndex;
    }

    protected void snapToDestination() {
        snapToPage(getPageNearestToCenterOfScreen(), PAGE_SNAP_ANIMATION_DURATION);
    }

    private static class ScrollInterpolator implements Interpolator {
        public ScrollInterpolator() {
        }

        public float getInterpolation(float t) {
            t -= 1.0f;
            return t*t*t*t*t + 1;
        }
    }

    // We want the duration of the page snap animation to be influenced by the distance that
    // the screen has to travel, however, we don't want this duration to be effected in a
    // purely linear fashion. Instead, we use this method to moderate the effect that the distance
    // of travel has on the overall snap duration.
    float distanceInfluenceForSnapDuration(float f) {
        f -= 0.5f; // center the values about 0.
        f *= 0.3f * Math.PI / 2.0f;
        return (float) Math.sin(f);
    }

    protected void snapToPageWithVelocity(int whichPage, int velocity) {
        whichPage = Math.max(0, Math.min(whichPage, getChildCount() - 1));
        int halfScreenSize = getMeasuredWidth() / 2;

        if (DEBUG) Log.d(TAG, "snapToPage.getChildOffset(): " + getChildOffset(whichPage));
        if (DEBUG) Log.d(TAG, "snapToPageWithVelocity.getRelativeChildOffset(): "
                + getMeasuredWidth() + ", " + getChildWidth(whichPage));
        final int newX = getChildOffset(whichPage) - getRelativeChildOffset(whichPage);
        int delta = newX - mUnboundedScrollX;
        int duration = 0;

        if (Math.abs(velocity) < MIN_FLING_VELOCITY) {
            // If the velocity is low enough, then treat this more as an automatic page advance
            // as opposed to an apparent physical response to flinging
            snapToPage(whichPage, PAGE_SNAP_ANIMATION_DURATION);
            return;
        }

        // Here we compute a "distance" that will be used in the computation of the overall
        // snap duration. This is a function of the actual distance that needs to be traveled;
        // we keep this value close to half screen size in order to reduce the variance in snap
        // duration as a function of the distance the page needs to travel.
        float distanceRatio = Math.min(1f, 1.0f * Math.abs(delta) / (2 * halfScreenSize));
        float distance = halfScreenSize + halfScreenSize *
                distanceInfluenceForSnapDuration(distanceRatio);

        velocity = Math.abs(velocity);
        velocity = Math.max(MINIMUM_SNAP_VELOCITY, velocity);

        // we want the page's snap velocity to approximately match the velocity at which the
        // user flings, so we scale the duration by a value near to the derivative of the scroll
        // interpolator at zero, ie. 5. We use 4 to make it a little slower.
        duration = 4 * Math.round(1000 * Math.abs(distance / velocity));

        snapToPage(whichPage, delta, duration);
    }

    protected void snapToPage(int whichPage) {
        snapToPage(whichPage, PAGE_SNAP_ANIMATION_DURATION);
    }

    protected void snapToPage(int whichPage, int duration) {
        whichPage = Math.max(0, Math.min(whichPage, getPageCount() - 1));

        if (DEBUG) Log.d(TAG, "snapToPage.getChildOffset(): " + getChildOffset(whichPage));
        if (DEBUG) Log.d(TAG, "snapToPage.getRelativeChildOffset(): " + getMeasuredWidth() + ", "
                + getChildWidth(whichPage));
        int newX = getChildOffset(whichPage) - getRelativeChildOffset(whichPage);
        final int sX = mUnboundedScrollX;
        final int delta = newX - sX;
        snapToPage(whichPage, delta, duration);
    }

    protected void snapToPage(int whichPage, int delta, int duration) {
        mNextPage = whichPage;

        View focusedChild = getFocusedChild();
        if (focusedChild != null && whichPage != mCurrentPage &&
                focusedChild == getPageAt(mCurrentPage)) {
            focusedChild.clearFocus();
        }

        pageBeginMoving();
        awakenScrollBars(duration);
        if (duration == 0) {
            duration = Math.abs(delta);
        }

        if (!mScroller.isFinished()) 
        {
            mScroller.abortAnimation();
            scrollEnd();
        }
        mScroller.startScroll(mUnboundedScrollX, 0, delta, 0, duration);
        scrollStart();
        // Load associated pages immediately if someone else is handling the scroll, otherwise defer
        // loading associated pages until the scroll settles
        if (mDeferScrollUpdate) {
            loadAssociatedPages(mNextPage);
        } else {
            mDeferLoadAssociatedPagesUntilScrollCompletes = true;
        }
        notifyPageSwitchListener();
        invalidate();
    }
    
    /*AUT: zhanglq@bj.cobellink.com DATA 2012-03-01 start*/
    protected void snapToPage(int whichPage, int scrollX, int delta, int duration) {
        mNextPage = whichPage;

        View focusedChild = getFocusedChild();
        if (focusedChild != null && whichPage != mCurrentPage &&
                focusedChild == getPageAt(mCurrentPage)) {
            focusedChild.clearFocus();
        }

        pageBeginMoving();
        awakenScrollBars(duration);
        if (duration == 0) {
            duration = Math.abs(delta);
        }

        if (!mScroller.isFinished()) 
        {
            mScroller.abortAnimation();
            scrollEnd();
        }
        mScroller.startScroll(scrollX, 0, delta, 0, duration);
        scrollStart();
        // Load associated pages immediately if someone else is handling the scroll, otherwise defer
        // loading associated pages until the scroll settles
        if (mDeferScrollUpdate) {
            loadAssociatedPages(mNextPage);
        } else {
            mDeferLoadAssociatedPagesUntilScrollCompletes = true;
        }
        notifyPageSwitchListener();
        invalidate();
    }
    /*AUT: zhanglq@bj.cobellink.com DATA 2012-03-01 end*/

    public void scrollLeft() {
        if (mScroller.isFinished()) {
            if (mCurrentPage > 0) snapToPage(mCurrentPage - 1);
        } else {
            if (mNextPage > 0) snapToPage(mNextPage - 1);
        }
    }

    public void scrollRight() {
        if (mScroller.isFinished()) {
            if (mCurrentPage < getChildCount() -1) snapToPage(mCurrentPage + 1);
        } else {
            if (mNextPage < getChildCount() -1) snapToPage(mNextPage + 1);
        }
    }

    public int getPageForView(View v) {
        int result = -1;
        if (v != null) {
            ViewParent vp = v.getParent();
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                if (vp == getPageAt(i)) {
                    return i;
                }
            }
        }
        return result;
    }

    /**
     * @return True is long presses are still allowed for the current touch
     */
    public boolean allowLongPress() {
        return mAllowLongPress;
    }

    /**
     * Set true to allow long-press events to be triggered, usually checked by
     * {@link Launcher} to accept or block dpad-initiated long-presses.
     */
    public void setAllowLongPress(boolean allowLongPress) {
        mAllowLongPress = allowLongPress;
    }

    public static class SavedState extends BaseSavedState {
        int currentPage = -1;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentPage = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(currentPage);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    protected void loadAssociatedPages(int page) {
        loadAssociatedPages(page, false);
    }
    protected void loadAssociatedPages(int page, boolean immediateAndOnly) {
        if (mContentIsRefreshable) {
            final int count = getChildCount();
            if (page < count) {
                int lowerPageBound = getAssociatedLowerPageBound(page);
                int upperPageBound = getAssociatedUpperPageBound(page);
                if (DEBUG) Log.d(TAG, "loadAssociatedPages: " + lowerPageBound + "/"
                        + upperPageBound);
                for (int i = 0; i < count; ++i) {
                    if ((i != page) && immediateAndOnly) {
                        continue;
                    }
                    Page layout = (Page) getPageAt(i);
                    final int childCount = layout.getPageChildCount();
                    if (lowerPageBound <= i && i <= upperPageBound) {
                        if (mDirtyPageContent.get(i)) {
                            syncPageItems(i, (i == page) && immediateAndOnly);
                            mDirtyPageContent.set(i, false);
                        }
                    } else {
                        if (childCount > 0) {
                            layout.removeAllViewsOnPage();
                        }
                        mDirtyPageContent.set(i, true);
                    }
                }
            }
        }
    }

    protected int getAssociatedLowerPageBound(int page) {
        return Math.max(0, page - 1);
    }
    protected int getAssociatedUpperPageBound(int page) {
        final int count = getChildCount();
        return Math.min(page + 1, count - 1);
    }

    protected void startChoiceMode(int mode, ActionMode.Callback callback) {
        if (isChoiceMode(CHOICE_MODE_NONE)) {
            mChoiceMode = mode;
            mActionMode = startActionMode(callback);
        }
    }

    public void endChoiceMode() {
        if (!isChoiceMode(CHOICE_MODE_NONE)) {
            mChoiceMode = CHOICE_MODE_NONE;
            resetCheckedGrandchildren();
            if (mActionMode != null) mActionMode.finish();
            mActionMode = null;
        }
    }

    protected boolean isChoiceMode(int mode) {
        return mChoiceMode == mode;
    }

    protected ArrayList<Checkable> getCheckedGrandchildren() {
        ArrayList<Checkable> checked = new ArrayList<Checkable>();
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; ++i) {
            Page layout = (Page) getPageAt(i);
            final int grandChildCount = layout.getPageChildCount();
            for (int j = 0; j < grandChildCount; ++j) {
                final View v = layout.getChildOnPageAt(j);
                if (v instanceof Checkable && ((Checkable) v).isChecked()) {
                    checked.add((Checkable) v);
                }
            }
        }
        return checked;
    }

    /**
     * If in CHOICE_MODE_SINGLE and an item is checked, returns that item.
     * Otherwise, returns null.
     */
    protected Checkable getSingleCheckedGrandchild() {
        if (mChoiceMode != CHOICE_MODE_MULTIPLE) {
            final int childCount = getChildCount();
            for (int i = 0; i < childCount; ++i) {
                Page layout = (Page) getPageAt(i);
                final int grandChildCount = layout.getPageChildCount();
                for (int j = 0; j < grandChildCount; ++j) {
                    final View v = layout.getChildOnPageAt(j);
                    if (v instanceof Checkable && ((Checkable) v).isChecked()) {
                        return (Checkable) v;
                    }
                }
            }
        }
        return null;
    }

    protected void resetCheckedGrandchildren() {
        // loop through children, and set all of their children to _not_ be checked
        final ArrayList<Checkable> checked = getCheckedGrandchildren();
        for (int i = 0; i < checked.size(); ++i) {
            final Checkable c = checked.get(i);
            c.setChecked(false);
        }
    }

    /**
     * This method is called ONLY to synchronize the number of pages that the paged view has.
     * To actually fill the pages with information, implement syncPageItems() below.  It is
     * guaranteed that syncPageItems() will be called for a particular page before it is shown,
     * and therefore, individual page items do not need to be updated in this method.
     */
    public abstract void syncPages();

    /**
     * This method is called to synchronize the items that are on a particular page.  If views on
     * the page can be reused, then they should be updated within this method.
     */
    public abstract void syncPageItems(int page, boolean immediate);

    protected void invalidatePageData() {
        invalidatePageData(-1, false);
    }
    protected void invalidatePageData(int currentPage) {
        invalidatePageData(currentPage, false);
    }
    protected void invalidatePageData(int currentPage, boolean immediateAndOnly) {
        if (!mIsDataReady) {
            return;
        }

        if (mContentIsRefreshable) {
            // Force all scrolling-related behavior to end
            mScroller.forceFinished(true);
            scrollEnd();
            mNextPage = INVALID_PAGE;

            // Update all the pages
            syncPages();

            // We must force a measure after we've loaded the pages to update the content width and
            // to determine the full scroll width
            measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY));

            // Set a new page as the current page if necessary
            if (currentPage > -1) {
                setCurrentPage(Math.min(getPageCount() - 1, currentPage));
            }

            // Mark each of the pages as dirty
            final int count = getChildCount();
            mDirtyPageContent.clear();
            for (int i = 0; i < count; ++i) {
                mDirtyPageContent.add(true);
            }

            // Load any pages that are necessary for the current window of views
            loadAssociatedPages(mCurrentPage, immediateAndOnly);
            mDirtyPageAlpha = true;
            updateAdjacentPagesAlpha();
            requestLayout();
        }
    }

    protected ImageView getScrollingIndicator() {
        // We use mHasScrollIndicator to prevent future lookups if there is no sibling indicator
        // found
        if (mHasScrollIndicator && mScrollIndicator == null) {
            ViewGroup parent = (ViewGroup) getParent();
            /*** fixbug 4429  . AUT: zhaoxy . DATE: 2013-01-04. START***/
            if (parent == null) {
                return null;
            }
            /*** fixbug 4429  . AUT: zhaoxy . DATE: 2013-01-04. END***/
            mScrollIndicator = (ImageView) (parent.findViewById(R.id.paged_view_indicator));
            mHasScrollIndicator = mScrollIndicator != null;
            if (mHasScrollIndicator
            		/*RK_ID: RK_HOME_POINT . S*/
            		&& SettingsValue.useAndroidStyle(mContext, SettingsValue.SettingsType.HOTSEAT_STYLE)
            		/*RK_ID: RK_HOME_POINT . E*/
            		) {
                mScrollIndicator.setVisibility(View.VISIBLE);
            }
        }
        /*RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2012-01-30 . S*/
        if (mScrollIndicator != null) {
        	/*LauncherApplication app = (LauncherApplication)mContext.getApplicationContext();
	    	Drawable b = app.mLauncherContext.getDrawable(R.drawable.hotseat_scrubber_holo);*/
        	//not use theme for now zhanglz1 120312
	    	Drawable b = mContext.getResources().getDrawable(R.drawable.hotseat_scrubber_holo);
	        mScrollIndicator.setImageDrawable(b);
        }
        /*RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2012-01-30 . E*/
        return mScrollIndicator;
    }

    protected boolean isScrollingIndicatorEnabled() {
        return !com.lenovo.launcher2.customizer.Utilities.isScreenLarge(mContext);
    }

    Runnable hideScrollingIndicatorRunnable = new Runnable() {
        @Override
        public void run() {
            hideScrollingIndicator(false);
        }
    };
    protected void flashScrollingIndicator(boolean animated) {
        removeCallbacks(hideScrollingIndicatorRunnable);
        showScrollingIndicator(!animated);
        postDelayed(hideScrollingIndicatorRunnable, sScrollIndicatorFlashDuration);
    }

    protected void showScrollingIndicator(boolean immediately) {
    	
    	
    	
        if (getChildCount() <= 1) return;
        /*** fixbug 169800 . AUT: zhaoxy . DATE: 2012-08-30. START***/
        //if (HawaiiHelp.sIsHawaiiPageEnabled && this instanceof AppsCustomizePagedView) {
        //    int numPages = getChildCount();
        //    int numHawaii = ((AppsCustomizePagedView) this).getNumHawaiiPage();
        //    numPages -= numHawaii;
         //   if (numPages <= 1) return;
       // }
        /*** fixbug 169800 . AUT: zhaoxy . DATE: 2012-08-30. END***/
        if (!isScrollingIndicatorEnabled()) return;
        // liuli1 , fix bug : 168641
       // if (this instanceof AppsCustomizePagedView && this.getVisibility() != VISIBLE)
       //     return;

        getScrollingIndicator();
        if (mScrollIndicator != null) {
            // Fade the indicator in
            updateScrollingIndicatorPosition();
            /*RK_ID: RK_HOME_POINT . S*/
            if (SettingsValue.useAndroidStyle(mContext, SettingsValue.SettingsType.HOTSEAT_STYLE)) {
            /*RK_ID: RK_HOME_POINT . E*/
	            mScrollIndicator.setVisibility(View.VISIBLE);
	            if (mScrollIndicatorAnimator != null) {
	                mScrollIndicatorAnimator.cancel();
	            }
	            if (immediately) {
	                mScrollIndicator.setAlpha(1f);
	            } else {
	                mScrollIndicatorAnimator = ObjectAnimator.ofFloat(mScrollIndicator, "alpha", 1f);
	                mScrollIndicatorAnimator.setDuration(sScrollIndicatorFadeInDuration);
	                mScrollIndicatorAnimator.start();
	            }
            }
            
            
        } 
    }

    protected void cancelScrollingIndicatorAnimations() {
        if (mScrollIndicatorAnimator != null) {
            mScrollIndicatorAnimator.cancel();
        }
    }

    protected void hideScrollingIndicator(boolean immediately) {
    	
    	    	
        // fix bug 165033, by zhanggx1
//        if (getChildCount() <= 1) return;
        if (!isScrollingIndicatorEnabled()) return;

        getScrollingIndicator();
        if (mScrollIndicator != null) {
            // Fade the indicator out
            updateScrollingIndicatorPosition();
            cancelScrollingIndicatorAnimations();
            if (mScrollIndicatorAnimator != null) {
                mScrollIndicatorAnimator.cancel();
            }
            if (immediately) {
                mScrollIndicator.setVisibility(View.GONE);
                mScrollIndicator.setAlpha(0f);
            } else {
                mScrollIndicatorAnimator = ObjectAnimator.ofFloat(mScrollIndicator, "alpha", 0f);
                mScrollIndicatorAnimator.setDuration(sScrollIndicatorFadeOutDuration);
                mScrollIndicatorAnimator.addListener(new AnimatorListenerAdapter() {
                    private boolean cancelled = false;

                    @Override
                    public void onAnimationCancel(android.animation.Animator animation) {
                        cancelled = true;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (!cancelled) {
                            mScrollIndicator.setVisibility(View.GONE);
                        }
                    }
                });
                mScrollIndicatorAnimator.start();
            }
        }
        
        
    }

    /**
     * To be overridden by subclasses to determine whether the scroll indicator should stretch to
     * fill its space on the track or not.
     */
    protected boolean hasElasticScrollIndicator() {
        return true;
    }

    private void updateScrollingIndicator() {
        if (getChildCount() <= 1) return;
        if (!isScrollingIndicatorEnabled()) return;

        
        getScrollingIndicator();
        if (mScrollIndicator != null) {
            updateScrollingIndicatorPosition();
        }
    }

    private void updateScrollingIndicatorPosition() {
        if (!isScrollingIndicatorEnabled()) return;
        if (mScrollIndicator == null) return;
        int numPages = getChildCount();
        int pageWidth = getMeasuredWidth();
        int lastChildIndex = Math.max(0, getChildCount() - 1);
        int maxScrollX = getChildOffset(lastChildIndex) - getRelativeChildOffset(lastChildIndex);
        int trackWidth = pageWidth - mScrollIndicatorPaddingLeft - mScrollIndicatorPaddingRight;
        int indicatorWidth = mScrollIndicator.getMeasuredWidth() -
                mScrollIndicator.getPaddingLeft() - mScrollIndicator.getPaddingRight();

        float offset = Math.max(0f, Math.min(1f, (float) getScrollX() / maxScrollX));
        /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. START***/
        if (numPages == 0) {
            numPages = 1;
        }
        /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. END***/
        /* RK_ID: RK_HAWAII. AUT: liuli1 . DATE: 2012-06-01 . START */
        //if (HawaiiHelp.sIsHawaiiPageEnabled && this instanceof AppsCustomizePagedView) {
        //    AppsCustomizeTabHost tabHost = ((AppsCustomizePagedView) this).getTabHost();
        //    if (tabHost != null && tabHost.getCurrentTabTag().equals(AppsCustomizeTabHost.APPS_TAB_TAG)) {
        //        int numHawaii = ((AppsCustomizePagedView) this).getNumHawaiiPage();
         //       numPages -= numHawaii;
                /*RK_ID: RK_HAWAII . AUT: Andy . DATE: 2012-07-18 . S*/
         //       if (numPages == 0) {
         //       	numPages = 1;
         //       }
                /*RK_ID: RK_HAWAII . AUT: Andy . DATE: 2012-07-18 . E*/
        //        int hawaiiScrollX = getChildOffset(numHawaii) - getRelativeChildOffset(numHawaii);
         //       maxScrollX -= hawaiiScrollX;
        //        offset = Math.max(0f, Math.min(1f, (float) (getScrollX()-hawaiiScrollX) / maxScrollX));
         //   } else if (tabHost != null && tabHost.getCurrentTabTag().equals(AppsCustomizeTabHost.HAWAII_TAB_TAG)) {
        //        numPages = Integer.MAX_VALUE;
       //         offset = 0.0f;
        //    }
        //}
        /* RK_ID: RK_HAWAII. AUT: liuli1 . DATE: 2012-06-01 . END */
        int indicatorSpace = trackWidth / numPages;
        int indicatorPos = (int) (offset * (trackWidth - indicatorSpace)) + mScrollIndicatorPaddingLeft;
        if (hasElasticScrollIndicator()) {
            if (mScrollIndicator.getMeasuredWidth() != indicatorSpace) {
                mScrollIndicator.getLayoutParams().width = indicatorSpace;
                mScrollIndicator.requestLayout();
            }
        } else {
            int indicatorCenterOffset = indicatorSpace / 2 - indicatorWidth / 2;
            indicatorPos += indicatorCenterOffset;
        }
        mScrollIndicator.setTranslationX(indicatorPos);
        mScrollIndicator.invalidate();
        
        /* AUT: liuli liuli0120@use.com.cn DATE: 2012-01-17 START */
//        if (mHomePoint != null && this instanceof AppsCustomizePagedView) {
//            int appPages = ((AppsCustomizePagedView)this).getAppPages();
//            boolean isAppPage = mCurrentPage < appPages;
//            int current = isAppPage ? mCurrentPage : mCurrentPage - appPages;
//            mHomePoint.setCurrentPoint(current);
//        } else
        /* AUT: liuli liuli0120@use.com.cn DATE: 2012-01-17 END */

        
    }

    public void showScrollIndicatorTrack() {
    }

    public void hideScrollIndicatorTrack() {
    }

    /* Accessibility */
    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setScrollable(true);
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setScrollable(true);
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            event.setFromIndex(mCurrentPage);
            event.setToIndex(mCurrentPage);
            event.setItemCount(getChildCount());
        }
    }

    protected String getCurrentPageDescription() {
        int page = (mNextPage != INVALID_PAGE) ? mNextPage : mCurrentPage;
        return String.format(mContext.getString(R.string.default_scroll_format),
                page + 1, getChildCount());
    }

    @Override
    public boolean onHoverEvent(android.view.MotionEvent event) {
        return true;
    }
    
    /*RK_ID: RK_HOME_SCREEN . AUT: zhanggx1 . DATE: 2011-12-19 . PUR: for leos scroller . S*/
    /**
     * Initialize the homePoint
     * @author zhanggx1
     * @date 2011-12-20
     */
   /* public void resetHomePoint(boolean showDockDivider, boolean showWholeWidget) {
        resetHomePoint(this.getChildCount(), this.mCurrentPage, showDockDivider, showWholeWidget);
    }
    
    public void resetHomePoint(int pageCount, int currentPage, boolean showDockDivider, boolean showWholeWidget) {
    	ViewGroup parent = (ViewGroup)getParent();
    	*//*** fixbug 168042 . AUT: zhaoxy . DATE: 2012-08-30. START***//*
        //final View qsbDivider = (ImageView) parent.findViewById(R.id.qsb_divider);
    	final View qsbDivider = (ImageView) parent.findViewById(-1);
        *//*** fixbug 168042 . AUT: zhaoxy . DATE: 2012-08-30. END***//*
        final View dockDivider = (ImageView) parent.findViewById(R.id.dock_divider);        
               
        RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2012-02-03 . S
        LauncherApplication app = (LauncherApplication) mContext.getApplicationContext();
        Drawable b = app.mLauncherContext.getDrawable(R.drawable.hotseat_track_holo);
        if (qsbDivider != null) {
        	((ImageView)qsbDivider).setImageDrawable(b);
        }
        if (dockDivider != null) {
        	((ImageView)dockDivider).setImageDrawable(b);
        }
        RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2012-02-03 . E
        
        boolean showHomePoint = !SettingsValue.useAndroidStyle(mContext, 
                SettingsValue.SettingsType.HOTSEAT_STYLE);
        
        setHomePointVisibility(showWholeWidget);
    }
    */
    public void setHomePointVisibility(boolean visible) {
    	ViewGroup parent = (ViewGroup)getParent();
    	final View qsbDivider = (ImageView) parent.findViewById(-1);
        final View dockDivider = (ImageView) parent.findViewById(R.id.dock_divider);        
        
        
        boolean showHomePoint = !SettingsValue.useAndroidStyle(mContext, 
                SettingsValue.SettingsType.HOTSEAT_STYLE);
        
        if (qsbDivider != null) {
        	qsbDivider.setVisibility(!visible || showHomePoint ? View.GONE : View.VISIBLE);
        }
        if (dockDivider != null) {
        	dockDivider.setVisibility(!visible || showHomePoint ? View.GONE : View.VISIBLE);
        }
        
    }
    
    @Override
    protected void onViewAdded(View child) {
    	super.onViewAdded(child);
    	
        // This ensures that when children are added, they get the correct transforms / alphas
        // in accordance with any scroll effects.
        mForceScreenScrolled = true;
        invalidate();
        invalidateCachedOffsets();    	    	
    	
    }
    
    @Override
    protected void onViewRemoved(View child) {
    	
    	super.onViewRemoved(child);
    }
    
    private int[] getScrollPosition() {
    	int[] result = new int[2];
    	int count = getChildCount();
    	int scrollX = getScrollX();
//    	if (this instanceof AppsCustomizePagedView) {        
//        	int appPages = ((AppsCustomizePagedView)this).getAppPages();
//            boolean isAppPage = mCurrentPage < appPages;
//            count = isAppPage ? appPages : getChildCount() - appPages;
//            
//            int lastAppIndex = Math.max(0, appPages - 1);//change by xingqx 2012.03.31 old is appPages
//            int maxAppScrollX = getChildOffset(lastAppIndex) - getRelativeChildOffset(lastAppIndex);
//            scrollX = isAppPage ? scrollX : scrollX - maxAppScrollX;
//    	}
    	
    	int lastChildIndex = Math.max(0, count - 1);
        int maxScrollX = getChildOffset(lastChildIndex) - getRelativeChildOffset(lastChildIndex);
        
        result[0] = scrollX;
        result[1] = maxScrollX;
        return result;
    }
    /*RK_ID: RK_HOME_SCREEN . AUT: zhanggx1 . DATE: 2011-12-19 . PUR: for leos scroller . E*/
    
    /** AUT: henryyu1986@163.com DATE: 2011-12-20 */
    protected boolean getScaleTransformation(View child, Transformation t)
    {
	    final int screenLeftX = 0;
	    final int screenRightX = (int)(getWidth() *mLayoutScale);
	    final int childLeft = child.getLeft() - mScrollX ;
	    final int childWidth = (int)(child.getWidth() * mLayoutScale);
	    final int childRight = childLeft + childWidth  ;
	    View lastchild = getChildAt(getChildCount() - 1);
	    int leftEdge = lastchild.getLeft();
	    
	    //int scaleSpace;            
        
        //scaleSpace = (child.getWidth() - childWidth)/2;

	    if(childLeft < screenLeftX && childRight > screenLeftX)
	    {
		    final float ratio = ((float)childRight - screenLeftX) / childWidth;
		    t.setAlpha(ratio);	
		    Matrix matrix = t.getMatrix();
		    matrix.setScale(ratio, ratio);
		    return true;
	    }else if(childRight > screenRightX && childLeft < screenRightX) 
	    {
		    final float ratio = 1.0f - ((float)childRight - screenRightX) / childWidth;
		    t.setAlpha(ratio);
		    Matrix matrix = t.getMatrix();
		    matrix.setScale(ratio, ratio);
		    return true;
	    }else if (getScrollX() < 0 && mSlideLoop) { // we are moving between 1 and the last screen
		    int left = getScrollX() + childWidth;
		    final float ratio = 1.0f - (float)left / childWidth;
		    t.setAlpha(ratio);	
		    Matrix matrix = t.getMatrix();
		    matrix.setScale(ratio, ratio);
		    return true;
	    } else if (getScrollX() > child.getLeft() && mSlideLoop){ // we are moving between 1 and the last screen
		    int left = childWidth - getScrollX() + leftEdge;	
		    final float ratio =1.0f -  (float)left / childWidth;
		    t.setAlpha(ratio);
		    Matrix matrix = t.getMatrix();
		    matrix.setScale(ratio, ratio);
		    return true;
	    }

	    return false;
    }

    protected boolean getBounceTransformation(View child, Transformation t)
    {
	    int screenLeftX = 0;
	    int screenRightX = getWidth();
	    int childLeft = child.getLeft() - getScrollX();
	    int childWidth = child.getWidth();
	    int childHeight = child.getHeight();
	    int childRight = childLeft + childWidth;
	    View lastchild = getChildAt(getChildCount() - 1);
	    int leftEdge = lastchild.getLeft();

	    if (childLeft < screenLeftX && childRight > screenLeftX) {
		    t.getMatrix().postTranslate(0, (float) childLeft * childHeight / childWidth);
		    return true;
	    } else if (childRight > screenRightX && childLeft < screenRightX) {
		    t.getMatrix().postTranslate(0, -(float) childLeft * childHeight / childWidth);
		    return true;
	    } else if (getScrollX() < 0 && mSlideLoop) { // we are moving between 1 and the last screen
		    int left = getScrollX() + child.getWidth();
		    t.getMatrix().postTranslate(0, -(float) left* childHeight / childWidth);
		    return true;
	    } else if (getScrollX() > child.getLeft() && mSlideLoop){ // we are moving between 1 and the last screen
		    int left = childWidth - getScrollX() + leftEdge;
		    t.getMatrix().postTranslate(0, -(float) left* childHeight / childWidth);
		    return true;
	   }
	    return true;
    }

    private Camera mCamera = new Camera();
    protected boolean getZRotateTransformation(View child, Transformation t)
    {
	    final int screenLeftX = 0;
	    final int screenRightX = (int)(getWidth() *mLayoutScale);
        final int childLeft = child.getLeft() - getScrollX();
        final int childWidth = (int)(child.getWidth() * mLayoutScale);
        final int childHeight = (int)(child.getHeight() * mLayoutScale);
	    final int childRight = childLeft + childWidth  ;
	    final int toDegrees = 90;
	    View lastchild = getChildAt(getChildCount() - 1);
	    int leftEdge = lastchild.getLeft();
	    //int scaleSpace;            
        
        //scaleSpace = (child.getWidth() - childWidth)/2;

	    if(childLeft < screenLeftX && childRight > screenLeftX)
	    {
		    final float ratio = ((float)childRight - screenLeftX) / childWidth;
		    float degrees = toDegrees * (1 - ratio);

		    final float centerX = childWidth / 2.0f;
		    final float centerY = childHeight / 2.0f;
		    final Camera camera = mCamera;
		    final Matrix matrix = t.getMatrix();
		    camera.save();
		    camera.rotateY(degrees);
		    camera.getMatrix(matrix);
		    camera.restore();
		    matrix.preTranslate(-centerX, -centerY);
		    matrix.postTranslate(centerX, centerY);
		    return true;
	    }else if(childRight > screenRightX && childLeft < screenRightX) 
	    {
		    final float ratio = 1.0f - ((float)childRight - screenRightX) / childWidth;
		    float degrees = toDegrees * (ratio - 1);

		    final float centerX = childWidth / 2.0f;
		    final float centerY = childHeight / 2.0f;
		    final Camera camera = mCamera;
		    final Matrix matrix = t.getMatrix();
		    camera.save();
		    camera.rotateY(degrees);
		    camera.getMatrix(matrix);
		    camera.restore();
		    matrix.preTranslate(-centerX, -centerY);
		    matrix.postTranslate(centerX, centerY);
		    return true;
	    } else if (getScrollX() < 0 && mSlideLoop) { // we are moving between 1 and the last screen

		    int left = getScrollX() + child.getWidth();
		    final float ratio = ((float)left) / childWidth;
		    float degrees = toDegrees * ratio;

		    final float centerX = childWidth / 2.0f;
		    final float centerY = childHeight / 2.0f;
		    final Camera camera = mCamera;
		    final Matrix matrix = t.getMatrix();
		    camera.save();
		    camera.rotateY(degrees);
		    camera.getMatrix(matrix);
		    camera.restore();
		    matrix.preTranslate(-centerX, -centerY);
		    matrix.postTranslate(centerX, centerY);
		    return true;

	    } else if (getScrollX() > child.getLeft() && mSlideLoop){ // we are moving between 1 and the last screen
		    int left = childWidth - getScrollX() + leftEdge;
		    final float ratio = 1.0f - (float)left/ childWidth;
		    float degrees = toDegrees * (ratio - 1);
		    final float centerX = childWidth / 2.0f;
		    final float centerY = childHeight / 2.0f;
		    final Camera camera = mCamera;
		    final Matrix matrix = t.getMatrix();
		    camera.save();
		    camera.rotateY(degrees);
		    camera.getMatrix(matrix);
		    camera.restore();
		    matrix.preTranslate(-centerX, -centerY);
		    matrix.postTranslate(centerX, centerY);
		    return true;
	   }

	    return false;
    }
    /** modify:zhanglq, zhanglq@bj.cobellink.com DATE 2012-1-18 start*/
    protected boolean getBullDozeTransformation(View child , Transformation t)
    {
	    final int screenLeftX = 0;
	    final int screenRightX = (int)(getWidth() *mLayoutScale);

	    final int childLeft = child.getLeft() - getScrollX();
	    final int childWidth = (int)(child.getWidth() * mLayoutScale);
	    final int childRight = childLeft + childWidth;
	    View lastchild = getChildAt(getChildCount() - 1);
	    int leftEdge = lastchild.getLeft();
	    int scaleSpace;            
        
        scaleSpace = (child.getWidth() - childWidth)/2;

	    if (childLeft < screenLeftX && childRight > screenLeftX) {
	     // modified by liuli1
		    t.getMatrix().setScale((float) (childRight - screenLeftX) / childWidth, 1, childWidth-1 + scaleSpace, 0);
		    return true;
	    } else if (childRight > screenRightX && childLeft < screenRightX) {
		    t.getMatrix().setScale((float) (screenRightX - childLeft) / childWidth, 1, scaleSpace, 0);
		    return true;
	    }else if (getScrollX() < 0 && mSlideLoop) { // we are moving between 1 and the last screen
	        int right = -getScrollX();
		    t.getMatrix().setScale((float) (right - screenLeftX) / childWidth, 1, childWidth-1 + scaleSpace, 0);
		    return true;
	    } else if (getScrollX() > child.getLeft() && mSlideLoop){ // we are moving between 1 and the last screen
		    int left = childWidth - getScrollX() + leftEdge;
		    t.getMatrix().setScale((float) (screenRightX - left) / childWidth, 1, scaleSpace, 0);
		    return true;
	    }

	    return true;
    }
    /** modify:zhanglq, zhanglq@bj.cobellink.com DATE 2012-1-18 start*/
    protected boolean getRollTransformation(View child ,Transformation t) {
	  final int screenLeftX = 0;
                final int screenRightX = getWidth();

                final int childLeft = child.getLeft() - getScrollX();
                final int childWidth = child.getWidth();
                final int childHeight = child.getHeight();
                final int childRight = childLeft + childWidth;
		View lastchild = getChildAt(getChildCount() - 1);
		int leftEdge = lastchild.getLeft();

                if ((childLeft < screenLeftX && childRight > screenLeftX) ||
                                (childRight > screenRightX && childLeft < screenRightX)) {
                        t.getMatrix().setRotate((float) childLeft / childWidth * 100, childWidth / 2f, childHeight / 2f);
		}else if (getScrollX() < 0) { // we are moving between 1 and the last screen
			int left = getScrollX() + childWidth;
                        t.getMatrix().setRotate((float) -left/ childWidth * 100, childWidth / 2f, childHeight / 2f);
			return true;
		} else if (getScrollX() > child.getLeft()){ // we are moving between 1 and the last screen
			int left = childWidth - getScrollX() + leftEdge;
			t.getMatrix().setRotate((float) left/ childWidth * 100, childWidth / 2f, childHeight / 2f);
			return true;
		}

                return true;
    }

    protected boolean getWildTransformation(View child, Transformation t) {
	    final int screenLeftX = 0;
	    final int screenRightX = getWidth();

	    final int childLeft = child.getLeft() - getScrollX();
	    final int childWidth = child.getWidth();
	    final int childRight = childLeft + childWidth;
	    View lastchild = getChildAt(getChildCount() - 1);
	    int leftEdge = lastchild.getLeft();

	    /*if ((childLeft < screenLeftX && childRight > screenLeftX) ||
			    (childRight > screenRightX && childLeft < screenRightX)) {
		    t.getMatrix().setRotate(-(float) childLeft / childWidth * 45, childWidth / 2f, -100);
		    // TODO 完美的wildmill动画需要动态调整高度
	    }*/
	    // modified by liuli1
        if (childLeft < screenLeftX && childRight > screenLeftX) {
            t.getMatrix().setRotate(-(float) childLeft / childWidth * 30, childWidth-childLeft/2f, -20);
            t.getMatrix().postTranslate(0, -childLeft/3.8f);
            return true;
        } else if (childRight > screenRightX && childLeft < screenRightX) {
            t.getMatrix().setRotate(-(float) childLeft / childWidth * 30, childWidth-childLeft/2f, -20);
            t.getMatrix().postTranslate(0, -childLeft/3.8f);
            return true;
        } else if (getScrollX() < 0 && mSlideLoop) { // we are moving between 1 and the last screen
		    int left = -getScrollX() - childWidth;
            t.getMatrix().setRotate(-(float) left / childWidth * 30, childWidth - left/2, -20);
		    t.getMatrix().postTranslate(0, -left/3.8f);
		    return true;
	    } else if (getScrollX() > child.getLeft() && mSlideLoop){ // we are moving between 1 and the last screen
		    int left = childWidth - getScrollX() + leftEdge;
		    t.getMatrix().setRotate((float) -left/ childWidth * 30, childWidth-left / 2f, -20);
		    t.getMatrix().postTranslate(0, -left/3.8f);
		    return true;
	    }

	    return true;
    }

    // modified by zhanglq
    protected boolean getCubeTransformation(View child, Transformation t) {
        final int screenLeftX = 0;
        final int screenRightX = (int)(getWidth() *mLayoutScale);
        final int childLeft = child.getLeft() - getScrollX();
        final int childWidth = (int)(child.getWidth() * mLayoutScale);

        final int childHeight = (int)(child.getHeight() * mLayoutScale);
        final int childRight = childLeft + childWidth;
        final float degreeY = 80.0f;
        View lastchild = getChildAt(getChildCount() - 1);
        int leftEdge = lastchild.getLeft();
        int scaleSpace;
        scaleSpace = (child.getWidth() - childWidth)/2;
        if (childWidth == 0) {
            return false;
        }
        if (childLeft < screenLeftX && childRight > screenLeftX) {
            Matrix matrix = t.getMatrix();
            Camera camera = new Camera();
            camera.save();
//            float z = 360.0f * (1.0f - (-(float) childLeft / (float) childWidth));
//            if (z >= 360.0f / 2.0f) {
//                z = 360.0f - z;
//            }
//            camera.translate(0.0f, 0.0f, z);
            camera.rotateY(degreeY * childLeft / childWidth);
            camera.getMatrix(matrix);
            camera.restore();
            matrix.preTranslate(-childWidth - scaleSpace, -(float) childHeight / 2);
            matrix.postTranslate(childWidth + scaleSpace, (float) childHeight / 2);
            return true;
        } else if (childRight > screenRightX && childLeft < screenRightX) {
            Matrix matrix = t.getMatrix();
            Camera camera = new Camera();
            camera.save();
//            float z = 360.0f * (1.0f - ((float) childLeft / (float) childWidth));
//            if (z >= 360.0f / 2.0f) {
//                z = 360.0f - z;
//            }
//            camera.translate(0.0f, 0.0f, z);
            camera.rotateY(degreeY * childLeft / childWidth);
            camera.getMatrix(matrix);
            camera.restore();
            matrix.preTranslate(-scaleSpace, -(float) childHeight / 2);
            matrix.postTranslate(scaleSpace, (float) childHeight / 2);
            return true;
        } else if (getScrollX() < 0 && mSlideLoop) { // we are moving between 1 and the last
                                       // screen
            int left = getScrollX() + childWidth;
            Matrix matrix = t.getMatrix();
            Camera camera = new Camera();
            camera.save();
//            float z = 360.0f * (1.0f - ((float) left / (float) childWidth));
//            if (z >= 360.0f / 2.0f) {
//                z = 360.0f - z;
//            }
//            camera.translate(0.0f, 0.0f, z);
            camera.rotateY(-degreeY * left / childWidth);
            camera.getMatrix(matrix);
            camera.restore();
            matrix.preTranslate(-childWidth, -(float) childHeight / 2);
            matrix.postTranslate(childWidth, (float) childHeight / 2);
            return true;
        } else if (getScrollX() > child.getLeft() && mSlideLoop) { // we are moving between 1
                                                     // and the last screen
            int left = childWidth - getScrollX() + leftEdge;
            Matrix matrix = t.getMatrix();
            Camera camera = new Camera();
            camera.save();
//            float z = 360.0f * (1.0f - ((float) left / (float) childWidth));
//            if (z >= 360.0f / 2.0f) {
//                z = 360.0f - z;
//            }
//            camera.translate(0.0f, 0.0f, z);
            camera.rotateY(degreeY * left / childWidth);
            camera.getMatrix(matrix);
            camera.restore();
            matrix.preTranslate(0, -(float) childHeight / 2);
            matrix.postTranslate(0, (float) childHeight / 2);
            return true;
        }
        return true;
    }

    //// modified by liuli1
    protected boolean getWaveTransformation(View child ,Transformation t)
 {
        final int screenLeftX = 0;
        final int screenRightX = (int)(getWidth() *mLayoutScale);
        final int childLeft = child.getLeft() - getScrollX();
        final int childWidth = (int)(child.getWidth() * mLayoutScale);
        final int childHeight = (int)(child.getHeight() * mLayoutScale);
        final int childRight = childLeft + childWidth;
        View lastchild = getChildAt(getChildCount() - 1);
        int leftEdge = lastchild.getLeft();
        int scaleSpace;            
        
        scaleSpace = (child.getWidth() - childWidth)/2;

        if (childLeft < screenLeftX && childRight > screenLeftX) {
            float scale = 1.0f + ((float) childLeft / (float) (2 * childWidth));
            float translate = -scale * childLeft / 3;
            Matrix matrix = t.getMatrix();
            matrix.setScale(scale, scale, childWidth / 2.0f + scaleSpace, childHeight / 2.0f);
            matrix.postTranslate(translate, 0);
            return true;
        } else if (childRight > screenRightX && childLeft < screenRightX) {
            float scale = 1.0f - ((float) childLeft / (float) (2 * childWidth));
            float translate = -scale * childLeft / 3;
            Matrix matrix = t.getMatrix();
            matrix.setScale(scale, scale, childWidth / 2.0f + scaleSpace, childHeight / 2.0f);
            matrix.postTranslate(translate, 0);
            return true;
        } else if (getScrollX() < 0 && mSlideLoop) { // we are moving between 1 and the last screen
            int left = -getScrollX()-childWidth;
            float scale = 1.0f + ((float) left / (float) (2 * childWidth));
            float translate = -scale * left / 3;
            Matrix matrix = t.getMatrix();
            matrix.setScale(scale, scale, getWidth() / 2.0f, getHeight() / 2.0f);
            matrix.postTranslate(translate, 0);
            return true;
        } else if (getScrollX() > child.getLeft() && mSlideLoop) { // we are moving between 1
                                                     // and the last screen
            int left = childWidth - getScrollX() + leftEdge;
            Camera camera = new Camera();
            camera.save();
            camera.translate(0,
                    -((float) (childHeight * 0.5) * (left - screenLeftX) / (float) childHeight),
                    left);
            camera.getMatrix(t.getMatrix());
            camera.restore();
            return true;
        }

        return true;

    }     

    // added by liuli1
    private int wave2Padding = Integer.MIN_VALUE;
    protected boolean getWave2Transformation(View child, Transformation t) {
        if (wave2Padding == Integer.MIN_VALUE) {
            wave2Padding = mContext.getResources().getDimensionPixelSize(R.dimen.page_view_wave2_padding);
        }

        final int screenLeftX = 0;
        final int screenRightX = getWidth();

        final int childLeft = child.getLeft() - getScrollX();
        final int childWidth = child.getWidth();
        final int childRight = childLeft + childWidth;
        View lastchild = getChildAt(getChildCount() - 1);
        int leftEdge = lastchild.getLeft();

        if (childLeft < screenLeftX && childRight > screenLeftX) {
            t.getMatrix().setScale(1, (float) (childRight - screenLeftX) / (childWidth * 2) + 0.5f, 0, child.getHeight()-wave2Padding);
            return true;
        } else if (childRight > screenRightX && childLeft < screenRightX) {
            t.getMatrix().setScale(1, (float) (screenRightX - childLeft) / (childWidth * 2) + 0.5f, 0, child.getHeight()-wave2Padding);
            return true;
        } else if (getScrollX() < 0 && mSlideLoop) {
            // we are moving between 1 and the last screen
            int right = -getScrollX();
            t.getMatrix().setScale(1, (float) (right - screenLeftX) / (childWidth * 2) + 0.5f, 0, child.getHeight()-wave2Padding);
            return true;
        } else if (getScrollX() > child.getLeft() && mSlideLoop) {
            // we are moving between 1 and the last screen
            int left = childWidth - getScrollX() + leftEdge;
            t.getMatrix().setScale(1, (float) (screenRightX - left) / (childWidth * 2) + 0.5f, 0, child.getHeight()-wave2Padding);
            return true;
        }

        return true;

    }
    protected boolean getScaleTransformation(View child, Canvas canvas, int page)
    {
	    final int screenLeftX = 0;
        final int screenRightX = (int)(getWidth() *mLayoutScale);
        final int childLeft = child.getLeft() - mScrollX ;
        final int childWidth = (int)(child.getWidth() * mLayoutScale);
	    final int childRight = childLeft + childWidth  ;
	    View lastchild = getChildAt(getChildCount() - 1);
	    int leftEdge = lastchild.getLeft();
	    int rightEdge = lastchild.getLeft() + lastchild.getMeasuredWidth();
	    int scaleSpace;
        scaleSpace = (child.getWidth() - childWidth)/2;

	    if(childLeft < screenLeftX && childRight > screenLeftX)
	    {
		    final float ratio = ((float)childRight - screenLeftX) / childWidth;
//		    t.setAlpha(ratio);	
//		    Matrix matrix = t.getMatrix();
                    /*PK_ID:PREVIEW SCREEN NOT DISPLAY CORRECT AUTH:GECN1@LENOVO.COM DATE: 2012-11-30 S*/
//		    child.setAlpha(ratio);
                    /*PK_ID:PREVIEW SCREEN NOT DISPLAY CORRECT AUTH:GECN1@LENOVO.COM DATE: 2012-11-30 E*/
		    Matrix matrix = canvas.getMatrix();
		    matrix.setScale(ratio, ratio, page*childWidth + scaleSpace, 0);
		    canvas.concat(matrix);
		    return true;
	    }else if(childRight > screenRightX && childLeft < screenRightX) 
	    {
		    final float ratio = 1.0f - ((float)childRight - screenRightX) / childWidth;
//		    t.setAlpha(ratio);
//		    Matrix matrix = t.getMatrix();
                    /*PK_ID:PREVIEW SCREEN NOT DISPLAY CORRECT AUTH:GECN1@LENOVO.COM DATE: 2012-11-30 S*/
//		    child.setAlpha(ratio);
                    /*PK_ID:PREVIEW SCREEN NOT DISPLAY CORRECT AUTH:GECN1@LENOVO.COM DATE: 2012-11-30 E*/
		    Matrix matrix = canvas.getMatrix();
		    matrix.setScale(ratio, ratio, page*childWidth + scaleSpace, 0);
		    canvas.concat(matrix);
		    return true;
	    }else if (getScrollX() < 0 && mSlideLoop) { // we are moving between 1 and the last screen
		    int left = getScrollX() + childWidth;
		    final float ratio = 1.0f - (float)left / childWidth;
//		    t.setAlpha(ratio);	
//		    Matrix matrix = t.getMatrix();
                    /*PK_ID:PREVIEW SCREEN NOT DISPLAY CORRECT AUTH:GECN1@LENOVO.COM DATE: 2012-11-30 S*/
//		    child.setAlpha(ratio);
                    /*PK_ID:PREVIEW SCREEN NOT DISPLAY CORRECT AUTH:GECN1@LENOVO.COM DATE: 2012-11-30 E*/
		    Matrix matrix = canvas.getMatrix();
		    matrix.setScale(ratio, ratio, rightEdge - childWidth, 0);
		    canvas.concat(matrix);
		    return true;
	    } else if (getScrollX() > child.getLeft() && mSlideLoop){ // we are moving between 1 and the last screen
		    int left = childWidth - getScrollX() + leftEdge;	
		    final float ratio =1.0f -  (float)left / childWidth;
//		    t.setAlpha(ratio);
//		    Matrix matrix = t.getMatrix();
                    /*PK_ID:PREVIEW SCREEN NOT DISPLAY CORRECT AUTH:GECN1@LENOVO.COM DATE: 2012-11-30 S*/
//		    child.setAlpha(ratio);
                    /*PK_ID:PREVIEW SCREEN NOT DISPLAY CORRECT AUTH:GECN1@LENOVO.COM DATE: 2012-11-30 E*/
		    Matrix matrix = canvas.getMatrix();
		    matrix.setScale(ratio, ratio);
		    canvas.concat(matrix);
		    return true;
	    }

	    return false;
    }

    protected boolean getBounceTransformation(View child, Canvas canvas)
    {
	    int screenLeftX = 0;
	    int screenRightX = getWidth();
	    int childLeft = child.getLeft() - getScrollX();
	    int childWidth = child.getWidth();
	    int childHeight = child.getHeight();
	    int childRight = childLeft + childWidth;
	    View lastchild = getChildAt(getChildCount() - 1);
	    int leftEdge = lastchild.getLeft();

	    if (childLeft < screenLeftX && childRight > screenLeftX) {
	    	Matrix matrix = new Matrix();
	    	matrix.postTranslate(0, (float) childLeft * childHeight / childWidth);
//		    t.getMatrix().postTranslate(0, (float) childLeft * childHeight / childWidth);
		    canvas.concat(matrix);
		    return true;
	    } else if (childRight > screenRightX && childLeft < screenRightX) {
	    	Matrix matrix = new Matrix();
	    	matrix.postTranslate(0, -(float) childLeft * childHeight / childWidth);
//		    t.getMatrix().postTranslate(0, -(float) childLeft * childHeight / childWidth);
	    	canvas.concat(matrix);
		    return true;
	    } else if (getScrollX() < 0 && mSlideLoop) { // we are moving between 1 and the last screen
		    int left = getScrollX() + child.getWidth();
		    Matrix matrix = new Matrix();
	    	matrix.postTranslate(0, -(float) left* childHeight / childWidth);
//		    t.getMatrix().postTranslate(0, -(float) left* childHeight / childWidth);
	    	canvas.concat(matrix);
		    return true;
	    } else if (getScrollX() > child.getLeft() && mSlideLoop){ // we are moving between 1 and the last screen
		    int left = childWidth - getScrollX() + leftEdge;
		    Matrix matrix = new Matrix();
	    	matrix.postTranslate(0, -(float) left* childHeight / childWidth);
//		    t.getMatrix().postTranslate(0, -(float) left* childHeight / childWidth);
	    	canvas.concat(matrix);
		    return true;
	   }
	    return true;
    }

    protected boolean getZRotateTransformation(View child, Canvas canvas, int page)
    {
        final int screenLeftX = 0;
        final int screenRightX = (int)(getWidth() *mLayoutScale);
        final int childLeft = child.getLeft() - getScrollX();
        final int childWidth = (int)(child.getWidth() * mLayoutScale);
        final int childHeight = (int)(child.getHeight() * mLayoutScale);
        final int childRight = childLeft + childWidth  ;
        final int toDegrees = 90;
        View lastchild = getChildAt(getChildCount() - 1);
        int leftEdge = lastchild.getLeft();
        int rightEdge = lastchild.getLeft() + lastchild.getMeasuredWidth();
        int scaleSpace;            
        
        scaleSpace = (child.getWidth() - childWidth)/2;
	    

	    if(childLeft < screenLeftX && childRight > screenLeftX)
	    {
		    final float ratio = ((float)childRight - screenLeftX) / childWidth;
		    float degrees = toDegrees * (1 - ratio);

		    final float centerX = (float)((page + 0.5) * childWidth) + scaleSpace;
		    final float centerY = childHeight / 2.0f;
		    final Camera camera = mCamera;
//		    final Matrix matrix = t.getMatrix();
		    final Matrix matrix = canvas.getMatrix();
		    camera.save();
		    camera.rotateY(degrees);
		    camera.getMatrix(matrix);
		    camera.restore();
		    matrix.preTranslate(-centerX, -centerY);
		    matrix.postTranslate(centerX , centerY);
		    canvas.concat(matrix);
		    return true;
	    }else if(childRight > screenRightX && childLeft < screenRightX) 
	    {
		    final float ratio = 1.0f - ((float)childRight - screenRightX) / childWidth;
		    float degrees = toDegrees * (ratio - 1);

		    final float centerX = (float)((page + 0.5) * childWidth) + scaleSpace;
		    final float centerY = childHeight / 2.0f;
		    final Camera camera = mCamera;
//		    final Matrix matrix = t.getMatrix();
		    final Matrix matrix = canvas.getMatrix();
		    camera.save();
		    camera.rotateY(degrees);
		    camera.getMatrix(matrix);
		    camera.restore();
		    matrix.preTranslate(-centerX, -centerY);
		    matrix.postTranslate(centerX, centerY);
		    canvas.concat(matrix);
		    return true;
	    } else if (getScrollX() < 0 && mSlideLoop) { // we are moving between 1 and the last screen

		    int left = getScrollX() + child.getWidth();
		    final float ratio = ((float)left) / childWidth;
		    float degrees = toDegrees * ratio;

		    final float centerX = (rightEdge + childWidth * 4) / 2.0f;
		    final float centerY = childHeight / 2.0f;
		    final Camera camera = mCamera;
//		    final Matrix matrix = t.getMatrix();
		    final Matrix matrix = canvas.getMatrix();
		    camera.save();
		    camera.rotateY(degrees);
		    camera.getMatrix(matrix);
		    camera.restore();
		    matrix.preTranslate(-centerX, -centerY);
		    matrix.postTranslate(centerX, centerY);
		    canvas.concat(matrix);
		    return true;

	    } else if (getScrollX() > child.getLeft() && mSlideLoop){ // we are moving between 1 and the last screen
		    int left = childWidth - getScrollX() + leftEdge;
		    final float ratio = 1.0f - (float)left/ childWidth;
		    float degrees = toDegrees * (ratio - 1);
		    final float centerX = childWidth / 2.0f;
		    final float centerY = childHeight / 2.0f;
		    final Camera camera = mCamera;
//		    final Matrix matrix = t.getMatrix();
		    final Matrix matrix = canvas.getMatrix();
		    camera.save();
		    camera.rotateY(degrees);
		    camera.getMatrix(matrix);
		    camera.restore();
		    matrix.preTranslate(-centerX, -centerY);
		    matrix.postTranslate(centerX, centerY);
		    canvas.concat(matrix);
		    return true;
	   }

	    return false;
    }
    /** modify:zhanglq, zhanglq@bj.cobellink.com DATE 2012-1-18 start*/
    protected boolean getBullDozeTransformation(View child , Canvas canvas, int page)
    {
	    final int screenLeftX = 0;
	    final int screenRightX = (int)(getWidth() *mLayoutScale);

	    final int childLeft = child.getLeft() - getScrollX();
	    final int childWidth = (int)(child.getWidth() * mLayoutScale);
	    final int childRight = childLeft + childWidth;
	    View lastchild = getChildAt(getChildCount() - 1);
	    int leftEdge = lastchild.getLeft();
	    int scaleSpace;	        
	    
	    scaleSpace = (child.getWidth() - childWidth)/2;
	    
	    if (childLeft < screenLeftX && childRight > screenLeftX) {
	     // modified by liuli1
//	    	t.getMatrix().setScale((float) (childRight - screenLeftX) / childWidth, 1, childWidth-1, 0);
	    	Matrix matrix = canvas.getMatrix();
	    	matrix.setScale((float) (childRight - screenLeftX) / childWidth , 1, (page + 1) * childWidth + scaleSpace, 0);
	    	canvas.concat(matrix);
		    return true;
	    } else if (childRight > screenRightX && childLeft < screenRightX) {
//	    	t.getMatrix().setScale((float) (screenRightX - childLeft) / childWidth, 1);
	    	Matrix matrix = canvas.getMatrix();
	    	matrix.setScale((float) (screenRightX - childLeft) / childWidth, 1, page * childWidth + scaleSpace, 0);
	    	canvas.concat(matrix);
		    return true;
	    }else if (getScrollX() < 0 && mSlideLoop) { // we are moving between 1 and the last screen
	        int right = -getScrollX();
//	    	t.getMatrix().setScale((float) (right - screenLeftX) / childWidth, 1, childWidth-1, 0);
	        Matrix matrix = canvas.getMatrix();
	        matrix.setScale((float) (right - screenLeftX) / childWidth, 1, (page + 1) * childWidth + scaleSpace, 0);
	    	canvas.concat(matrix);
		    return true;
	    } else if (getScrollX() > child.getLeft() && mSlideLoop){ // we are moving between 1 and the last screen
		    int left = childWidth - getScrollX() + leftEdge;
//		    t.getMatrix().setScale((float) (screenRightX - left) / childWidth, 1);
		    Matrix matrix = canvas.getMatrix();
		    matrix.setScale((float) (screenRightX - left) / childWidth, 1, page * childWidth + scaleSpace, 0);
	    	canvas.concat(matrix);
		    return true;
	    }

	    return true;
    }    
       
    protected boolean getWildTransformation(View child, Canvas canvas, int page) {
	    final int screenLeftX = 0;
	    final int screenRightX = (int)(getWidth() *mLayoutScale);

	    final int childLeft = child.getLeft() - getScrollX();
	    final int childWidth = (int)(child.getWidth() * mLayoutScale);
	    final int childRight = childLeft + childWidth;
	    View lastchild = getChildAt(getChildCount() - 1);
	    int leftEdge = lastchild.getLeft();
	    //int rightEdge = lastchild.getLeft() + lastchild.getMeasuredWidth();
	    int scaleSpace;            
        
        scaleSpace = (child.getWidth() - childWidth)/2;

	    /*if ((childLeft < screenLeftX && childRight > screenLeftX) ||
			    (childRight > screenRightX && childLeft < screenRightX)) {
		    t.getMatrix().setRotate(-(float) childLeft / childWidth * 45, childWidth / 2f, -100);
		    // TODO 完美的wildmill动画需要动态调整高度
	    }*/
	    // modified by liuli1
        if (childLeft < screenLeftX && childRight > screenLeftX) {
        	Matrix matrix = canvas.getMatrix();
        	matrix.setRotate(-(float) childLeft / childWidth * 30, (page * childWidth + scaleSpace + (childWidth-childLeft/2f)) , -20);
            matrix.postTranslate(0, -childLeft/3.8f);
        	canvas.concat(matrix);
            return true;
        } else if (childRight > screenRightX && childLeft < screenRightX) {
        	Matrix matrix = canvas.getMatrix();
        	matrix.setRotate(-(float) childLeft / childWidth * 30, ((page)* childWidth + scaleSpace + (childWidth-childLeft/2f)), -20);
        	matrix.postTranslate(0, -(childLeft)/3.8f);
        	canvas.concat(matrix);
            return true;
        } else if (getScrollX() < 0 && mSlideLoop) { // we are moving between 1 and the last screen
		    int left = -getScrollX() - childWidth;
		    Matrix matrix = canvas.getMatrix();
        	matrix.setRotate(-(float) left / childWidth * 30, childWidth - left/2, -500);
//            t.getMatrix().setRotate(-(float) left / childWidth * 30, childWidth - left/2, -20);
//		      t.getMatrix().postTranslate(0, -left/3.8f);
        	canvas.concat(matrix);
		    return true;
	    } else if (getScrollX() > child.getLeft() && mSlideLoop){ // we are moving between 1 and the last screen
		    int left = childWidth - getScrollX() + leftEdge;
		    Matrix matrix = canvas.getMatrix();
        	matrix.setRotate((float) -left/ childWidth * 30,  childWidth - left/2, -20);
//		      t.getMatrix().setRotate((float) -left/ childWidth * 30, childWidth-left / 2f, -20);
//		      t.getMatrix().postTranslate(0, -left/3.8f);
        	canvas.concat(matrix);
		    return true;
	    }

	    return true;
    }

    // modified by zhanglq
    protected boolean getCubeTransformation(View child, Canvas canvas, int page) {
        final int screenLeftX = 0;
        final int screenRightX = (int)(getWidth() *mLayoutScale);
        final int childLeft = child.getLeft() - getScrollX();
        final int childWidth = (int)(child.getWidth() * mLayoutScale);

        final int childHeight = (int)(child.getHeight() * mLayoutScale);
        final int childRight = childLeft + childWidth;
        final float degreeY = 80.0f;
        View lastchild = getChildAt(getChildCount() - 1);
        int leftEdge = lastchild.getLeft();
        int rightEdge = lastchild.getLeft() + lastchild.getMeasuredWidth();
        int scaleSpace;
        scaleSpace = (child.getWidth() - childWidth)/2;
        int dx;
        
        if (childWidth == 0) {
            return false;
        }
        if (childLeft < screenLeftX && childRight > screenLeftX) {
            //Matrix matrix = t.getMatrix();
        	Matrix matrix = canvas.getMatrix();
            Camera camera = new Camera();
            camera.save();
//            float z = 360.0f * (1.0f - (-(float) childLeft / (float) childWidth));
//            if (z >= 360.0f / 2.0f) {
//                z = 360.0f - z;
//            }
//            camera.translate(0.0f, 0.0f, z);
            camera.rotateY(degreeY * childLeft / childWidth);
            camera.getMatrix(matrix);
            camera.restore();
            dx = (page + 1)*childWidth + scaleSpace;
            matrix.preTranslate(-dx, -(float) childHeight / 2);
            matrix.postTranslate(dx, (float) childHeight / 2);
            canvas.concat(matrix);
            return true;
        } else if (childRight > screenRightX && childLeft < screenRightX) {
        	//Matrix matrix = t.getMatrix();
        	Matrix matrix = canvas.getMatrix();
            Camera camera = new Camera();
            camera.save();
//            float z = 360.0f * (1.0f - ((float) childLeft / (float) childWidth));
//            if (z >= 360.0f / 2.0f) {
//                z = 360.0f - z;
//            }
//            camera.translate(0.0f, 0.0f, z);
            camera.rotateY(degreeY * childLeft / childWidth);
            camera.getMatrix(matrix);
            camera.restore();
            dx = page*childWidth + scaleSpace;
            matrix.preTranslate(-dx, -(float) childHeight / 2);
            matrix.postTranslate(dx, (float) childHeight / 2);
            canvas.concat(matrix);
            return true;
        } else if (getScrollX() < 0 && mSlideLoop) { // we are moving between 1 and the last
                                       // screen
            int left = getScrollX() + childWidth;
            //Matrix matrix = t.getMatrix();
            Matrix matrix = canvas.getMatrix();
            Camera camera = new Camera();
            camera.save();
//            float z = 360.0f * (1.0f - ((float) left / (float) childWidth));
//            if (z >= 360.0f / 2.0f) {
//                z = 360.0f - z;
//            }
//            camera.translate(0.0f, 0.0f, z);
            camera.rotateY(-degreeY * left / childWidth);
            camera.getMatrix(matrix);
            camera.restore();
            matrix.preTranslate(-rightEdge, -(float) childHeight / 2);
            matrix.postTranslate(rightEdge, (float) childHeight / 2);
            canvas.concat(matrix);
            return true;
        } else if (getScrollX() > child.getLeft() && mSlideLoop) { // we are moving between 1
                                                     // and the last screen
            int left = childWidth - getScrollX() + leftEdge;
            //Matrix matrix = t.getMatrix();
            Matrix matrix = canvas.getMatrix();
            Camera camera = new Camera();
            camera.save();
//            float z = 360.0f * (1.0f - ((float) left / (float) childWidth));
//            if (z >= 360.0f / 2.0f) {
//                z = 360.0f - z;
//            }
//            camera.translate(0.0f, 0.0f, z);
            camera.rotateY(degreeY * left / childWidth);
            camera.getMatrix(matrix);
            camera.restore();
            matrix.preTranslate(0, -(float) childHeight / 2);
            matrix.postTranslate(0, (float) childHeight / 2);
            canvas.concat(matrix);
            return true;
        }
        return true;
    }

    //// modified by liuli1
    protected boolean getWaveTransformation(View child ,Canvas canvas, int page)
 {
        final int screenLeftX = 0;
        final int screenRightX = (int)(getWidth() *mLayoutScale);
        final int childLeft = child.getLeft() - getScrollX();
        final int childWidth = (int)(child.getWidth() * mLayoutScale);
        final int childHeight = (int)(child.getHeight() * mLayoutScale);
        final int childRight = childLeft + childWidth;
        View lastchild = getChildAt(getChildCount() - 1);
        int leftEdge = lastchild.getLeft();
        int rightEdge = lastchild.getLeft() + lastchild.getMeasuredWidth();
        int scaleSpace;            
        
        scaleSpace = (child.getWidth() - childWidth)/2;

        if (childLeft < screenLeftX && childRight > screenLeftX) {
            float scale = 1.0f + ((float) childLeft / (float) (2 * childWidth));
            float translate = -scale * childLeft / 3;
//            Matrix matrix = t.getMatrix();
            Matrix matrix = canvas.getMatrix();
            matrix.setScale(scale, scale, (float)((page + 0.5) *childWidth + scaleSpace), childHeight/ 2.0f);
            matrix.postTranslate(translate, 0);
            canvas.concat(matrix);
            return true;
        } else if (childRight > screenRightX && childLeft < screenRightX) {
            float scale = 1.0f - ((float) childLeft / (float) (2 * childWidth));
            float translate = -scale * childLeft / 3;
//            Matrix matrix = t.getMatrix();
            Matrix matrix = canvas.getMatrix();
            matrix.setScale(scale, scale, (float)((page + 0.5) *childWidth + scaleSpace), childHeight / 2.0f);
            matrix.postTranslate(translate, 0);
            canvas.concat(matrix);
            return true;
        } else if (getScrollX() < 0 && mSlideLoop) { // we are moving between 1 and the last screen
            int left = -getScrollX()-childWidth;
            float scale = 1.0f + ((float) left / (float) (2 * childWidth));
            float translate = -scale * left / 3;
//            Matrix matrix = t.getMatrix();
            Matrix matrix = canvas.getMatrix();
            matrix.setScale(scale, scale, rightEdge - (childWidth / 2.0f), getHeight() / 2.0f);
            matrix.postTranslate(translate, 0);
            canvas.concat(matrix);
            return true;
        } else if (getScrollX() > child.getLeft() && mSlideLoop) { // we are moving between 1
                                                     // and the last screen
        	int left = childWidth - getScrollX() + leftEdge;
		    final float scale = (float) (screenRightX - left) / (childWidth * 2) + 0.5f;
//		    Matrix matrix = t.getMatrix();
            Matrix matrix = canvas.getMatrix();
            matrix.setScale(scale, scale, 0, getHeight() / 2.0f);
            canvas.concat(matrix);
            return true;
        }

        return true;

    }     

    // added by liuli1
    protected boolean getWave2Transformation(View child, Canvas canvas) {
        if (wave2Padding == Integer.MIN_VALUE) {
            wave2Padding = mContext.getResources().getDimensionPixelSize(R.dimen.page_view_wave2_padding);
        }

        final int screenLeftX = 0;
        final int screenRightX = getWidth();

        final int childLeft = child.getLeft() - getScrollX();
        final int childWidth = child.getWidth();
        final int childRight = childLeft + childWidth;
        View lastchild = getChildAt(getChildCount() - 1);
        int leftEdge = lastchild.getLeft();

        if (childLeft < screenLeftX && childRight > screenLeftX) {
        	Matrix matrix = canvas.getMatrix();
        	matrix.setScale(1, (float) (childRight - screenLeftX) / (childWidth * 2) + 0.5f, 0, child.getHeight()-wave2Padding);
//            t.getMatrix().setScale(1, (float) (childRight - screenLeftX) / (childWidth * 2) + 0.5f, 0, child.getHeight()-wave2Padding);
        	canvas.concat(matrix);
            return true;
        } else if (childRight > screenRightX && childLeft < screenRightX) {
        	Matrix matrix = canvas.getMatrix();
        	matrix.setScale(1, (float) (screenRightX - childLeft) / (childWidth * 2) + 0.5f, 0, child.getHeight()-wave2Padding);
//            t.getMatrix().setScale(1, (float) (screenRightX - childLeft) / (childWidth * 2) + 0.5f, 0, child.getHeight()-wave2Padding);
        	canvas.concat(matrix);
            return true;
        } else if (getScrollX() < 0 && mSlideLoop) {
            // we are moving between 1 and the last screen
            int right = -getScrollX();
            Matrix matrix = canvas.getMatrix();
        	matrix.setScale(1, (float) (right - screenLeftX) / (childWidth * 2) + 0.5f, 0, child.getHeight()-wave2Padding);
//            t.getMatrix().setScale(1, (float) (right - screenLeftX) / (childWidth * 2) + 0.5f, 0, child.getHeight()-wave2Padding);
        	canvas.concat(matrix);
            return true;
        } else if (getScrollX() > child.getLeft() && mSlideLoop) {
            // we are moving between 1 and the last screen
            int left = childWidth - getScrollX() + leftEdge;
            Matrix matrix = canvas.getMatrix();
        	matrix.setScale(1, (float) (screenRightX - left) / (childWidth * 2) + 0.5f, 0, child.getHeight()-wave2Padding);
//            t.getMatrix().setScale(1, (float) (screenRightX - left) / (childWidth * 2) + 0.5f, 0, child.getHeight()-wave2Padding);
        	canvas.concat(matrix);
            return true;
        }

        return true;

    }
	public  boolean getChildDynamicTransformation(View child, Canvas canvas, int page) {


    	if(slideEffectValue == null) {
    		slideEffectValue = getSlideEffectValue();
    	}
    	if(!slideEffectValue.equals(LauncherPersonalSettings.SLIDEEFFECT_SCALE)) {
    		child.setAlpha(1.0f);
    	}
    	if(slideEffectValue.equals(LauncherPersonalSettings.SLIDEEFFECT_SCALE))
    		return getScaleTransformation(child , canvas, page);
    	else if(slideEffectValue.equals(LauncherPersonalSettings.SLIDEEFFECT_BULLDOZE))
    		return getBullDozeTransformation(child, canvas, page);
    	else if(slideEffectValue.equals(LauncherPersonalSettings.SLIDEEFFECT_BOUNCE))
    		return getBounceTransformation(child, canvas);
        /*
         *cancel by xingqx for bug on roll
        else if(slideEffectValue.equals(LauncherPersonalSettings.SLIDEEFFECT_ROLL))
            return getRollTransformation(child, t);*/
    	else if(slideEffectValue.equals(LauncherPersonalSettings.SLIDEEFFECT_WILD))
    		return getWildTransformation(child, canvas, page);
    	else if(slideEffectValue.equals(LauncherPersonalSettings.SLIDEEFFECT_CUBE))
    		return getCubeTransformation(child, canvas, page);
    	else if(slideEffectValue.equals(LauncherPersonalSettings.SLIDEEFFECT_WAVE))
    		return getWaveTransformation(child, canvas, page);
        /* RK_ID: RK_SLIDEEFFECT. AUT: liuli1 . DATE: 2012-04-10 . START */
        else if (slideEffectValue.equals(LauncherPersonalSettings.SLIDEEFFECT_WAVE_2))
            return getWave2Transformation(child, canvas);
        /* RK_ID: RK_SLIDEEFFECT. AUT: liuli1 . DATE: 2012-04-10 . END */
    	else if(slideEffectValue.equals(LauncherPersonalSettings.SLIDEEFFECT_ROTATE))
    		return getZRotateTransformation(child, canvas, page);
    	else if(slideEffectValue.equals(LauncherPersonalSettings.SLIDEEFFECT_NORMAL))
    		return false;
     
    	return false;
    }
    protected boolean isPageMovingForDynamic(){
		if (/*(isPageMoving() || isTransitAnimator) && */slideEffectValue != null){
			for (int i = 0; i < LauncherPersonalSettings.SLIDEEFFECT_ARRAY.length; i++) {
				if (slideEffectValue.equals(LauncherPersonalSettings.SLIDEEFFECT_ARRAY[i])) {
					if (!(slideEffectValue.equals(LauncherPersonalSettings.SLIDEEFFECT_CYLINDER) ||
							slideEffectValue.equals(LauncherPersonalSettings.SLIDEEFFECT_SPHERE))) {
						return true;
					}
				}
			}
		}
		return false;
	}


    @Override
    public  boolean getChildStaticTransformation(View child, Transformation t) {

    	if(slideEffectValue == null) {
    		slideEffectValue = getSlideEffectValue();
    	}
    	if(slideEffectValue.equals(LauncherPersonalSettings.SLIDEEFFECT_SCALE))
    		return getScaleTransformation(child , t);
    	else if(slideEffectValue.equals(LauncherPersonalSettings.SLIDEEFFECT_BULLDOZE))
    		return getBullDozeTransformation(child, t);
    	else if(slideEffectValue.equals(LauncherPersonalSettings.SLIDEEFFECT_BOUNCE))
    		return getBounceTransformation(child,t);
        /*
         *cancel by xingqx for bug on roll
        else if(slideEffectValue.equals(LauncherPersonalSettings.SLIDEEFFECT_ROLL))
            return getRollTransformation(child, t);*/
    	else if(slideEffectValue.equals(LauncherPersonalSettings.SLIDEEFFECT_WILD))
    		return getWildTransformation(child, t);
    	else if(slideEffectValue.equals(LauncherPersonalSettings.SLIDEEFFECT_CUBE))
    		return getCubeTransformation(child, t);
    	else if(slideEffectValue.equals(LauncherPersonalSettings.SLIDEEFFECT_WAVE))
    		return getWaveTransformation(child, t);
        /* RK_ID: RK_SLIDEEFFECT. AUT: liuli1 . DATE: 2012-04-10 . START */
        else if (slideEffectValue.equals(LauncherPersonalSettings.SLIDEEFFECT_WAVE_2))
            return getWave2Transformation(child, t);
        /* RK_ID: RK_SLIDEEFFECT. AUT: liuli1 . DATE: 2012-04-10 . END */
    	else if(slideEffectValue.equals(LauncherPersonalSettings.SLIDEEFFECT_ROTATE))
    		return getZRotateTransformation(child, t);
    	else if(slideEffectValue.equals(LauncherPersonalSettings.SLIDEEFFECT_NORMAL))
    		return false;
     
    	return false;
    }
    
    public String getSlideEffectValue() {return LauncherPersonalSettings.SLIDEEFFECT_NORMAL;}
    /** AUT: henryyu1986@163.com DATE: 2011-12-20 */
    public void setSlideEffectValue(String value){slideEffectValue = value;}
    
    /** AUT zhanglq@bj.cobellink.com DATA 2012-05-16 START*/
	float[] src = new float[8];
	float[] dst = new float[8];

	public void slideeffect(Canvas canvas) {
                 canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));

		int halfScreenSize = getMeasuredWidth() / 2;
		int screenCenter;
		if (mFadeInAdjacentScreens) {
			screenCenter = mScrollX + halfScreenSize;
		} else {
			screenCenter = mOverScrollX + halfScreenSize;
		}
		if (screenCenter != mLastScreenCenter || mForceScreenScrolled) {
			screenScrolled(screenCenter);
			updateAdjacentPagesAlpha();
			mLastScreenCenter = screenCenter;
			mForceScreenScrolled = false;
		}
		
		final int pageCount = getChildCount();
        if (pageCount > 0) {
            getVisiblePages(mTempVisiblePagesRange);
            final int leftScreen = mTempVisiblePagesRange[0];
            final int rightScreen = mTempVisiblePagesRange[1];
            if (leftScreen != -1 && rightScreen != -1) {
        		canvas.save();
        		canvas.translate(mPaddingLeft + getScrollX(), mPaddingTop);
        		canvas.clipRect(0, 0, getWidth(), getHeight());
                for (int i = rightScreen; i >= leftScreen; i--) {
                	drawChildByBitmap(canvas, i);
                }
                if(leftScreen == rightScreen && mSlideLoop && (leftScreen == 0 || leftScreen == pageCount-1)){
                	drawChildByBitmap(canvas, getChildCount()-1-leftScreen);
                }
                canvas.restore();
            }
        }
	}
	
	public void slideeffect(Canvas canvas, View view) {
                 canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
		int halfScreenSize = getMeasuredWidth() / 2;
		int screenCenter;
		if (mFadeInAdjacentScreens) {
			screenCenter = mScrollX + halfScreenSize;
		} else {
			screenCenter = mOverScrollX + halfScreenSize;
		}
		if (screenCenter != mLastScreenCenter || mForceScreenScrolled) {
			screenScrolled(screenCenter);
			updateAdjacentPagesAlpha();
			mLastScreenCenter = screenCenter;
			mForceScreenScrolled = false;
		}
		
		final int childCount = getChildCount();
        if (childCount > 0) {
        	getVisiblePages(mTempVisiblePagesRange);
            final int leftScreen = mTempVisiblePagesRange[0];
            final int rightScreen = mTempVisiblePagesRange[1];
            if (leftScreen != -1 && rightScreen != -1) {
            	final long drawingTime = getDrawingTime();
            	
                for (int i = rightScreen; i >= leftScreen; i--) {
                	View child = getPageAt(i);                	
                	canvas.save();
                	canvas.clipRect(mScrollX, mScrollY, mScrollX + mRight - mLeft,
                			mScrollY + mBottom - mTop);
                	getChildDynamicTransformation(child, canvas, i);
                	drawChild(canvas, child, drawingTime);
                	canvas.restore();
                }
                
                if (!mSlideLoop)
                	return;
                
                boolean fastDraw = mTouchState != TOUCH_STATE_SCROLLING && mNextPage == INVALID_PAGE;
                if (fastDraw) {
                } else {}
            }
        }
	}
	
	private void drawChildByBitmap(Canvas canvas, int page) {
		View currentView = getChildAt(page);
		Bitmap currentFace = currentView.getDrawingCache();
		if (currentFace == null) {
			currentView.buildDrawingCache();
			currentFace = currentView.getDrawingCache();
		}
		int targetWidth = currentFace.getWidth();
		int targetHeight = currentFace.getHeight();
		float currentOffseth = getOffseth(page);
		float offsetV = getOffsetV();
		float inputValueTemp = this.inputValue;

		int childWidth;
		int childHeight;
		if (currentView instanceof PagedViewGridLayout) {
			childWidth = targetWidth;
			childHeight = targetHeight;
		} else {
			childWidth = ((ViewGroup) currentView).getChildAt(0).getWidth();
			childHeight = ((ViewGroup) currentView).getChildAt(0).getHeight();
		}
		int childCellWidth = childWidth / mCellCountX;
		int childCellHeight = childHeight / mCellCountY;
		int childPaddingLeft = (targetWidth - childWidth) / 2;
		int childPaddingTop = (targetHeight - childHeight) / 2;
		
		int x,y;
		y = childPaddingTop;
		for (int j = 0; j < mCellCountY; j++) {
		    x = childPaddingLeft;
			for (int i = 0; i < mCellCountX; i++) {
				canvas.save();
				canvas.translate(x,y);
				Rect currentFaceRect = new Rect(0, 0, childCellWidth,
						childCellHeight);
				Rect smallCurrentFaceRect = new Rect(x, y, x + childCellWidth, y + childCellHeight);
				if (slideEffectValue
						.equals(LauncherPersonalSettings.SLIDEEFFECT_CYLINDER)) {
					cylinder(i, j, childCellWidth, childCellHeight,
							childPaddingLeft, childPaddingTop, canvas,
							currentOffseth, inputValueTemp, true);

				} else if (slideEffectValue
						.equals(LauncherPersonalSettings.SLIDEEFFECT_SPHERE)) {
					sphere(i, j, childCellWidth, childCellHeight,
							childPaddingLeft, childPaddingTop, canvas,
							currentOffseth, offsetV, inputValueTemp, true);
	        	       }
				canvas.drawBitmap(currentFace, smallCurrentFaceRect,
						currentFaceRect, paint);
				canvas.restore();
				x = x + childCellWidth;
			}
			
			y = y + childCellHeight;
		}
	}

	Paint paint = new Paint();
	public void cylinder(int i, int j, int cellWidth, int cellHeight,int childPaddingLeft,int childPaddingTop,
			Canvas canvas, float offsetH,float inputValue,boolean isCurrentPage) {
		Matrix localMatrix = new Matrix();
		int k = 4;
		int width = (cellWidth*mCellCountX);
		int i1 = width >> 1;
		int i2 = getHeight() >> 1;
        float PI = (float) Math.PI;
		float f1 = width * 0.65F;
		float f2 = PI * f1;
		float f4 = f2 - width;
		float f6 = f4 / k / 2.0F;
		
		float srcLeft = i * cellWidth;
		float srcTop = j * cellHeight;
		float srcRight = (i + 1) * cellWidth;
		float srcBottom = (j + 1) * cellHeight;
		this.src[0] = srcLeft;
		this.src[1] = srcTop;
		this.src[2] = srcRight;
		this.src[3] = srcTop;
		this.src[4] = srcRight;
		this.src[5] = srcBottom;
		this.src[6] = srcLeft;
		this.src[7] = srcBottom;
		float f11 = -offsetH * PI;
		int i3 = width / k;
		float f12 = (i * 2 + 1) * f6;
		float f18 = i3 * i + f12;
		float f14 = f18 / f2;
		float f15 = (1.0F + f14) * PI + f11;
		float f19 = (float)cellWidth;
		float f20 = ((f18 + f19) / f2 + 1.0F) * PI + f11;
		float f21 = FloatMath.sin(f15) * f1 + f1;
		float f23 = f21 + width;
		float f24 = FloatMath.sin(f20) * f1 + f1;
		float f26 = f24 + width;
		float f27 = FloatMath.cos(f15) * f1 + f1;
		float f28 = FloatMath.cos(f20) * f1 + f1;
		float f30 = (f27 - f1) * width / f23;
		float f32 = f30 + i1;
		float f34 = (f28 - f1) * width / f26;
		float f36 = f34 + i1;
		float f39 = srcTop - i2;
		float f40 = width * f39 / f23;
		float f42 = f40 + i2;
		float f46 = width * f39 / f26;
		float f48 = f46 + i2;
		float f51 = srcBottom - i2;
		float f52 = width * f51 / f23;
		float f54 = f52 + i2;
		float f58 = width * f51 / f26;
		float f60 = f58 + i2;
		
		this.dst[0] = f32;
		this.dst[1] = f42;
		this.dst[2] = f36;
		this.dst[3] = f48;
		this.dst[4] = f36;
		this.dst[5] = f60;
		this.dst[6] = f32;
		this.dst[7] = f54;		
		int pointCount = this.src.length >> 1;
                
		float f69 = Math.abs(offsetH);
		float f70 = (1.0F - f69) * 5.0F;
		float f71 = Math.min(1.0F, f70);
		float f72 = (FloatMath.sin(f15) + 1.0F) / 2.0F;
		float f73 = 1.0F - f72 + 0.3F;
		float f74 = Math.min(1.0F, f73);
		float f75 = f71 * f74;
		float value = inputValue;
		if(isOpen && isCurrentPage){			
			for (int a = 0; a < pointCount*2; a++) {
				//dst[a] = src[a] * (1 - value) + dst[a] * value;
			}
		}else if(!isOpen){
			for (int a = 0; a < pointCount*2; a++) {
				dst[a] = src[a] * (1 - value) + dst[a] * value;
			}
		}
		localMatrix.setPolyToPoly(src, 0,
				dst, 0, pointCount);
		localMatrix.preTranslate(srcLeft, srcTop);
		
		localMatrix.preScale(1.0f, 0.80f+0.2f*(1-inputValue));

		float f67 = -srcLeft;
		float f68 = -srcTop;
		localMatrix.postTranslate(f67, f68);

		paint.setAlpha((int) (f75 * 255));
		canvas.concat(localMatrix);
	}
	
	public void cylinder(View child, Transformation t, float offsetH,float inputValue,boolean isCurrentPage) {}
	
	GlobeHelper glob;
    float oldy;
    float scrollY;
	public void sphere(int i, int j, int cellWidth, int cellHeight,int childPaddingLeft,int childPaddingTop,
			Canvas canvas, float offsetH,float offsetV,float inputValue,boolean isCurrentPage) {
		Matrix localMatrix = new Matrix();

        float angle_offset_h = -offsetH*180;
        float angle_offset_v = -offsetV*90.0f;
        float srcLeft = i * cellWidth;
		float srcTop = j * cellHeight;
		float srcRight = (i + 1) * cellWidth;
		float srcBottom = (j + 1) * cellHeight;
		this.src[0] = srcLeft;
		this.src[1] = srcTop;
		this.src[2] = srcRight;
		this.src[3] = srcTop;
		this.src[4] = srcRight;
		this.src[5] = srcBottom;
		this.src[6] = srcLeft;
		this.src[7] = srcBottom;

		float radius =  cellWidth*mCellCountX*0.97f/2.0f;
		float depth = 500f;
		glob.initValue(cellWidth*mCellCountX/2.0f-radius, cellHeight*mCellCountY/2.0f-radius, radius, depth,1000.0f, mCellCountX*2, mCellCountY, 45.0f);
		glob.setTargetDimension(cellWidth, cellHeight*0.6f);
		glob.setOffset(angle_offset_h, angle_offset_v);
		dst = glob.getRect(i, j);

		int pointCount = this.src.length >> 1;
		if(isOpen && isCurrentPage){			
			for (int a = 0; a < pointCount*2; a++) {
				//dst[a] = src[a] * (1 - inputValue) + dst[a] * inputValue;
			}
		}else if(!isOpen){
			for (int a = 0; a < pointCount*2; a++) {
				dst[a] = src[a] * (1 - inputValue) + dst[a] * inputValue;
			}
		}
		localMatrix.setPolyToPoly(src, 0,
				dst, 0, pointCount);
		localMatrix.preTranslate(srcLeft, srcTop);
		
//		localMatrix.preScale(1.0f, 0.6f+0.4f*(1-inputValue));
		
		float f67 = -srcLeft;
		float f68 = -srcTop;
		localMatrix.postTranslate(f67, f68);
		
		int k = 4;
		int n = getWidth();
		int m = i;
        float pi = (float) Math.PI;
		float f1 = n * 0.65F;
		float f2 = pi * f1;
		float f4 = f2 - n;
		float f6 = f4 / k / 2.0F;
		
		float f11 = -offsetH * pi;
		int i3 = n / k;
		float f12 = (m * 2 + 1) * f6;
		float f18 = i3 * m + f12;
		float f14 = f18 / f2;
		float f15 = (1.0F + f14) * pi + f11;
		
		float f69 = Math.abs(offsetH);
		float f70 = (1.0F - f69) * 5.0F;
		float f71 = Math.min(1.0F, f70);
		float f72 = (FloatMath.sin(f15) + 1.0F) / 2.0F;
		float f73 = 1.0F - f72 + 0.3F;
		float f74 = Math.min(1.0F, f73);
		float f75 = f71 * f74;

		paint.setAlpha((int)(255*f75));
		canvas.concat(localMatrix);
	}

	public void sphere(View child, Transformation t, float offsetH,float offsetV,float inputValue,boolean isCurrentPage) {}

	public float getOffseth(int page) {
		int i = getScrollX();
		int j = page;
		int k = getChildCount();
		int m = k - 1;
		int i1 = k - 1;
		int i2 = getWidth();
		int i3 = i1 * i2;
		if ((j == m) && (i < 0) && m > 0) {
			int n = getWidth() * k;
			i += n;
		}
		if (j == 0 && (i > i3) && m > 0) {
			int i4 = getWidth() * k;
			i -= i4;
		}
		float f9 = i - getChildAt(page).getLeft();
		float f12 = f9 / (float)getWidth();
		if(f12 > 1.0f){
			f12 = f12 - (getChildCount());
		}else if(f12 < -1.0f){
			f12 = f12 + (getChildCount());
		}
		return Math.max(f12, -1.0F);
	}
	public float getOffsetV(){
		float v = scrollY/(getHeight()/2.0f);
		if(v > 1){
			v = 1;
		}else if(v < -1){
			v = -1;
		}
		return v;
	}
	
	ValueAnimator transitAnimator = new ValueAnimator();
	ValueAnimator verticalAnimator = new ValueAnimator();
	public float inputValue = 0;
	public boolean isTransitAnimator;
	private boolean isOpen;
	public void transitAnim(final boolean isBegin){
		if(!(slideEffectValue != null && (slideEffectValue.equals(LauncherPersonalSettings.SLIDEEFFECT_CYLINDER)
				|| slideEffectValue.equals(LauncherPersonalSettings.SLIDEEFFECT_SPHERE))))
			return;
		if(transitAnimator.isStarted())transitAnimator.cancel();
		if(verticalAnimator.isStarted())verticalAnimator.cancel();
		int duration = 0;
		if(isBegin){			
			transitAnimator = ValueAnimator.ofFloat(inputValue,1.0f);
			isOpen = true;
			duration = 1;
		}else{
			transitAnimator = ValueAnimator.ofFloat(inputValue,0.0f);
			isOpen = false;
			duration = 150;
			/*
			verticalAnimator = ValueAnimator.ofFloat(scrollY,0.0f);
			verticalAnimator.setDuration(150);
			verticalAnimator.setInterpolator(new DecelerateInterpolator());
			verticalAnimator.addListener(new AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animation) {
					
				}
				@Override
				public void onAnimationRepeat(Animator animation) {
					
				}
				@Override
				public void onAnimationEnd(Animator animation) {
					scrollY = 0;
					oldy = 0;
				}
				@Override
				public void onAnimationCancel(Animator animation) {
					scrollY = 0;
					oldy = 0;
				}
			});
			verticalAnimator.addUpdateListener(new AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					scrollY = (Float) animation.getAnimatedValue();
				}
			});
			verticalAnimator.start();
			*/
		}
		
		transitAnimator.setDuration(duration);
		transitAnimator.setInterpolator(new DecelerateInterpolator());
		transitAnimator.addListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
				isTransitAnimator = true;
			}
			@Override
			public void onAnimationRepeat(Animator animation) {
				
			}
			@Override
			public void onAnimationEnd(Animator animation) {
				isTransitAnimator = false;
				
			}
			@Override
			public void onAnimationCancel(Animator animation) {
				isTransitAnimator = false;
			}
		});
		transitAnimator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				inputValue = (Float) animation.getAnimatedValue();
				//if(PagedView.this instanceof AppsCustomizePagedView){
				//	AppsCustomizePagedView appsCustomizePagedView = (AppsCustomizePagedView) PagedView.this;
				//	appsCustomizePagedView.invalidate();
				
			}
		});
		transitAnimator.start();
	}

	protected boolean isPageMovingForCell(){
		if ((isPageMoving() || isTransitAnimator) && slideEffectValue != null
				&& (slideEffectValue.equals(
						LauncherPersonalSettings.SLIDEEFFECT_CYLINDER) ||
						slideEffectValue.equals(
						LauncherPersonalSettings.SLIDEEFFECT_SPHERE))){
			
 	      /***RK_ID:RK_BUGFIX_172088 AUT:zhanglz1@lenovo.com.DATE:2012-11-01. E***/        
		}
		return false;
	}
	
	class GlobeHelper {

		/** The value PI as a float. (180 degrees) */
		public static final float PI = (float) Math.PI;
		/** A value to multiply a degree value by, to convert it to radians. */
		public static final float DEG_TO_RAD = PI / 180.0f;
		/** A value to multiply a radian value by, to convert it to degrees. */
		public static final float RAD_TO_DEG = 180.0f / PI;
		private float radius = 500.0f;
		private float center_x = 250.0f;
		private float center_y = 250.0f;
		//private float center_z = 260.0f;
		/**
		 * Warp
		 */
		private int lon_count = 8;
		/**
		 * Latitude
		 */
		private int lat_count = 4;

		// Temp
		private float ANGLE_LAT_PADDING = 22.5f;
		private float ANGLE_LON_PADDING = 22.5f;
		/**
		 * Angle between the two warp
		 */
		float angle_pre_lon = 360.0f / lon_count;
		/**
		 * Angle between the two latitude
		 */
		float angle_pre_lat = (180.0f - ANGLE_LAT_PADDING * 2.0f) / (lat_count - 1);

		float angle_offset_h = 0;
		float angle_offset_v = 0;

		float target_width = 0;
		float target_height = 0;
		float angle_half_of_target_width = 0;
		float angle_half_of_target_height = 0;
		float pers = 0;

		/**
		 * This model and as the pieces in order to reduce the amount of
		 * computation, perspective projection plane chosen as the screen, point
		 * of view chosen as the z-axis outside the screen coordinates(0, 0,
		 * cameraZ).
		 * 
		 * @param x
		 *            Ball bounding rectangle of the upper-left corner of the vertices x
		 * @param y
		 *            Ball bounding rectangle of the upper-left vertex y
		 * @param radius
		 *            Ball radius
		 * @param sphereCenterZ
		 *            The depth of the center of the sphere from the screen positive
		 * @param cameraZ
		 *            Z values ​​of the point of view, the point of view in the screen positive
		 * @param lon_count
		 *            The number of spherical warp
		 * @param lat_count
		 *            The number of spherical latitude
		 * @param angleOfLatPadding
		 *            Latitude up and down padding angle
		 */
		public GlobeHelper() {

		}
		
		public void initValue(float x, float y, float radius, float sphereCenterZ,
				float cameraZ, int lon_count, int lat_count,
				float angleOfLatPadding){
			this.radius = radius;
			this.center_x = x + radius;
			this.center_y = y + radius;
			//this.center_z = sphereCenterZ;
			this.lon_count = lon_count;
			this.lat_count = lat_count;
			this.angle_pre_lon = 360.0f / lon_count;
			this.ANGLE_LON_PADDING = angle_pre_lon / 2.0f;
			this.ANGLE_LAT_PADDING = angleOfLatPadding;
			this.angle_pre_lat = (180.0f - ANGLE_LAT_PADDING * 2.0f)
					/ (lat_count - 1);
			this.pers = cameraZ;
		}

		public void setOffset(float horizontal, float vertical) {
			angle_offset_h = horizontal;
			angle_offset_v = vertical;
		}

		public void setTargetDimension(float width, float height) {
			this.target_width = width;
			this.target_height = height;
			this.angle_half_of_target_width = width / radius / 2;
			this.angle_half_of_target_height = height / radius / 2;
		}

		public float[] getRect(int lon, int lat) {
			float[] result = new float[8];
			float temp_radius = radius;
			float temp_x = 0;
			float temp_y = 0;
			float temp_z = 0;
			float temp_zz = 0;
			float temp_angle_to_z = 0;
			float temp_angle_to_y = 0;
			float co = (float) FloatMath.cos(angle_offset_v * DEG_TO_RAD);
			float si = (float) FloatMath.sin(angle_offset_v * DEG_TO_RAD);
			float angle_to_z = (ANGLE_LON_PADDING + lon * angle_pre_lon - 90f + angle_offset_h)
					* DEG_TO_RAD;
			float angle_to_y = (ANGLE_LAT_PADDING + lat * angle_pre_lat)
					* DEG_TO_RAD;
			// left top
			temp_angle_to_y = angle_to_y - angle_half_of_target_height;
			temp_angle_to_z = angle_to_z - angle_half_of_target_width;
			temp_x = (float) (temp_radius * FloatMath.sin(temp_angle_to_y) * FloatMath
					.sin(temp_angle_to_z));
			temp_y = (float) (temp_radius * FloatMath.cos(temp_angle_to_y));
			temp_z = (float) (temp_radius * FloatMath.sin(temp_angle_to_y) * FloatMath
					.cos(temp_angle_to_z));
			temp_zz = (float) (co * temp_z - si * temp_y);
			temp_y = (float) (si * temp_z + co * temp_y);
			temp_x = temp_x / (1 - temp_zz / pers);
			temp_y = temp_y / (1 - temp_zz / pers);
			result[0] = temp_x + center_x;
			result[1] = center_y - temp_y;
			// right top
			temp_angle_to_z = angle_to_z + angle_half_of_target_width;
			temp_x = (float) (temp_radius * FloatMath.sin(temp_angle_to_y) * FloatMath
					.sin(temp_angle_to_z));
			temp_y = (float) (temp_radius * FloatMath.cos(temp_angle_to_y));
			temp_z = (float) (temp_radius * FloatMath.sin(temp_angle_to_y) * FloatMath
					.cos(temp_angle_to_z));
			temp_zz = (float) (co * temp_z - si * temp_y);
			temp_y = (float) (si * temp_z + co * temp_y);
			temp_x = temp_x / (1 - temp_zz / pers);
			temp_y = temp_y / (1 - temp_zz / pers);
			result[2] = temp_x + center_x;
			result[3] = center_y - temp_y;
			// right bottom
			temp_angle_to_y = angle_to_y + angle_half_of_target_height;
			temp_x = (float) (temp_radius * FloatMath.sin(temp_angle_to_y) * FloatMath
					.sin(temp_angle_to_z));
			temp_y = (float) (temp_radius * FloatMath.cos(temp_angle_to_y));
			temp_z = (float) (temp_radius * FloatMath.sin(temp_angle_to_y) * FloatMath
					.cos(temp_angle_to_z));
			temp_zz = (float) (co * temp_z - si * temp_y);
			temp_y = (float) (si * temp_z + co * temp_y);
			temp_x = temp_x / (1 - temp_zz / pers);
			temp_y = temp_y / (1 - temp_zz / pers);
			result[4] = temp_x + center_x;
			result[5] = center_y - temp_y;
			// left bottom
			temp_angle_to_z = angle_to_z - angle_half_of_target_width;
			temp_x = (float) (temp_radius * FloatMath.sin(temp_angle_to_y) * FloatMath
					.sin(temp_angle_to_z));
			temp_y = (float) (temp_radius * FloatMath.cos(temp_angle_to_y));
			temp_z = (float) (temp_radius * FloatMath.sin(temp_angle_to_y) * FloatMath
					.cos(temp_angle_to_z));
			temp_zz = (float) (co * temp_z - si * temp_y);
			temp_y = (float) (si * temp_z + co * temp_y);
			temp_x = temp_x / (1 - temp_zz / pers);
			temp_y = temp_y / (1 - temp_zz / pers);
			result[6] = temp_x + center_x;
			result[7] = center_y - temp_y;
			return result;
		}

	}
	/** AUT zhanglq@bj.cobellink.com DATA 2012-05-16 END*/
	
	boolean mScroll;
	
	public void scrollStart(){
	    mScroll = true;
    }
	
	public void scrollEnd(){
	    mScroll = false;
	}
	
	protected float getLayoutScale() {
		return mLayoutScale;
	}
}
