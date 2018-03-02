package com.lenovo.launcher.components.XAllAppFace;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.appwidget.AppWidgetProviderInfo;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.slimengine.BaseDrawableGroup;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;
import com.lenovo.launcher2.commoninterface.ItemInfo;
import com.lenovo.launcher2.commonui.LauncherAppWidgetHostView;

/*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-5-30. */
public class AppWidgetResizeFrame extends BaseDrawableGroup {

    private XContext mContext;
    private XLauncher mLauncher;
    private XDragLayer mDragLayer;
    private XWorkspace mWorkspace;
    private XPagedView mPagedView;
    private XLauncherView mMainSurface;
    private LauncherAppWidgetHostView mWidgetView;
    private ItemInfo mItemInfo;
    
    private DrawableItem mBackgroundHandle = null;
    private XIconDrawable mLeftHandle = null;
    private XIconDrawable mRightHandle = null;
    private XIconDrawable mTopHandle = null;
    private XIconDrawable mBottomHandle = null;

    private boolean mLeftBorderActive;
    private boolean mRightBorderActive;
    private boolean mTopBorderActive;
    private boolean mBottomBorderActive;
    
    private int mLeftBitmapHeight;
    private int mRightBitmapWidth;
    private int mRightBitmapHeight;
    private int mTopBitmapWidth;
    private int mBottomBitmapWidth;
    private int mBottomBitmapHeight;

    private int mBaselineWidth;
    private int mBaselineHeight;
    private int mBaselineX;
    private int mBaselineY;
    private int mResizeMode;

    private int mRunningHInc;
    private int mRunningVInc;
    private int mMinHSpan;
    private int mMinVSpan;
    private int mDeltaX;
    private int mDeltaY;

    private int mBackgroundPadding;
    private int mTouchTargetWidth;
    
    private int mWidgetPaddingLeft;
    private int mWidgetPaddingRight;
    private int mWidgetPaddingTop;
    private int mWidgetPaddingBottom;
    
    private int mExpandability[] = new int[4];

    final int SNAP_DURATION = 150;
    final int BACKGROUND_PADDING = /*15*/20;
    final int BACKGROUND_PADDING_TOUCH = 30;
    final float DIMMED_HANDLE_ALPHA = 0f;
    final float RESIZE_THRESHOLD = 0.66f;
    final int PADDING_FRAME = 4;

    public static final int LEFT = 0;
    public static final int TOP = 1;
    public static final int RIGHT = 2;
    public static final int BOTTOM = 3;

    private XDragLayer.LayoutParams mLp = null;
    
    private int mFramePadding;

    public AppWidgetResizeFrame(XContext context,  XLauncher xLauncher,
            XDragLayer dragLayer, ItemInfo info, LauncherAppWidgetHostView widgetView ) {
        super(context);
        Log.i("zdx1","\nAppWidgetResizeFrame.AppWidgetResizeFrame in");
        mContext = context;
        mLauncher = xLauncher;
        mDragLayer = dragLayer;
        mWorkspace = xLauncher.getWorkspace();
        mPagedView = mWorkspace.getPagedView();
        mMainSurface = (XLauncherView)mLauncher.getMainView();
        mItemInfo = info;
        mWidgetView = widgetView;
        mResizeMode = mWidgetView.getAppWidgetInfo().resizeMode;
        Log.i("zdx1","resizeMode:"+ mResizeMode +", hostview width:"+ mWidgetView.getWidth() +
        		", hostview height:"+ mWidgetView.getHeight());

        final float density = mContext.getResources().getDisplayMetrics().density;
        mBackgroundPadding = (int) Math.ceil(density * BACKGROUND_PADDING);
        //mTouchTargetWidth = 2 * mBackgroundPadding;
        mTouchTargetWidth = 2* ((int) Math.ceil(density * BACKGROUND_PADDING_TOUCH));
        mFramePadding = (int)Math.ceil(density * PADDING_FRAME);
        Log.i("zdx1","mFramePadding:"+mFramePadding);

        Drawable bgDrawable= mContext.getResources().getDrawable(R.drawable.widget_resize_frame_holo);
        mBackgroundHandle = new DrawableItem(mContext);       
        mBackgroundHandle.setBackgroundDrawable(bgDrawable);

        Bitmap leftBitmap = ((BitmapDrawable)(mContext.getResources().getDrawable(
        		R.drawable.widget_resize_handle_left))).getBitmap();
        mLeftHandle = new XIconDrawable(mContext, leftBitmap);
        mLeftHandle.setFillMode(XIconDrawable.FILLMODE_WRAPSRC);
    
        Bitmap rightBitmap = ((BitmapDrawable)(mContext.getResources().getDrawable(
        		R.drawable.widget_resize_handle_right))).getBitmap();
        mRightHandle = new XIconDrawable(mContext, rightBitmap);        
        mRightHandle.setFillMode(XIconDrawable.FILLMODE_WRAPSRC);

        Bitmap topBitmap = ((BitmapDrawable)(mContext.getResources().getDrawable(
        		R.drawable.widget_resize_handle_top))).getBitmap();
        mTopHandle = new XIconDrawable(mContext, topBitmap);        
        mTopHandle.setFillMode(XIconDrawable.FILLMODE_WRAPSRC);
        
        Bitmap bottomBitmap = ((BitmapDrawable)(mContext.getResources().getDrawable(
        		R.drawable.widget_resize_handle_bottom))).getBitmap();
        mBottomHandle = new XIconDrawable(mContext, bottomBitmap);        
        mBottomHandle.setFillMode(XIconDrawable.FILLMODE_WRAPSRC);
        
        mLeftBitmapHeight = leftBitmap.getHeight();
        mRightBitmapWidth = rightBitmap.getWidth();
        mRightBitmapHeight = rightBitmap.getHeight();
        mTopBitmapWidth = topBitmap.getWidth();
        mBottomBitmapWidth = bottomBitmap.getWidth();
        mBottomBitmapHeight = bottomBitmap.getHeight();
        
        //int[] location = new int[2];
        //mDragLayer.getLocationInDragLayer(mItem, location);
        //mBaselineX = (float)location[0];
        //mBaselineY = (float)location[1];
        //mBaselineWidth = mItem.getWidth();
        //mBaselineHeight = mItem.getHeight();
        getBaseline();

        refreshDirectionHandle();
        
        addItem( mBackgroundHandle );
        addItem( mLeftHandle );
        addItem( mRightHandle );
        addItem( mTopHandle );
        addItem( mBottomHandle );
        
        /*Rect p = AppWidgetHostView.getDefaultPaddingForWidget(mLauncher,
        		mWidgetView.getAppWidgetInfo().provider, null);
        mWidgetPaddingLeft = p.left;
        mWidgetPaddingTop = p.top;
        mWidgetPaddingRight = p.right;
        mWidgetPaddingBottom = p.bottom;*/
        mWidgetPaddingLeft = 0;
        mWidgetPaddingTop = 0;
        mWidgetPaddingRight = 0;
        mWidgetPaddingBottom = 0;
        Log.i("zdx1","Widgt padding: left:"+ mWidgetPaddingLeft +", right:"+ mWidgetPaddingRight +
        		", top:"+ mWidgetPaddingTop +", bottom:"+ mWidgetPaddingBottom);

        if (mResizeMode == AppWidgetProviderInfo.RESIZE_HORIZONTAL) {
            mTopHandle.setVisibility(false);
            mBottomHandle.setVisibility(false);
        } else if (mResizeMode == AppWidgetProviderInfo.RESIZE_VERTICAL) {
            mLeftHandle.setVisibility(false);
            mRightHandle.setVisibility(false);
        }
        mContext.bringContentViewToFront();
     
        Log.i("zdx1","AppWidgetResizeFrame.AppWidgetResizeFrame out--mBackgroundPadding:"+mBackgroundPadding);
        Log.i("zdx1","AppWidgetResizeFrame.AppWidgetResizeFrame out--mTouchTargetWidth:"+mTouchTargetWidth);

    }

    public boolean beginResizeIfPointInRegion(int x, int y) {
    	Log.i("zdx1","AppWidgetResizeFrame.beginResizeIfPointInRegion in. x:"+x+", y:"+y);
        boolean horizontalActive = (mResizeMode & AppWidgetProviderInfo.RESIZE_HORIZONTAL) != 0;
        boolean verticalActive = (mResizeMode & AppWidgetProviderInfo.RESIZE_VERTICAL) != 0;
        mLeftBorderActive = (x < mTouchTargetWidth) && horizontalActive;
        mRightBorderActive = (x > getWidth() - mTouchTargetWidth) && horizontalActive;
        mTopBorderActive = (y < mTouchTargetWidth) && verticalActive;
        mBottomBorderActive = (y > getHeight() - mTouchTargetWidth) && verticalActive;
        Log.i("zdx1","AppWidgetResizeFrame.beginResizeIfPointInRegion, mLeftBorderActive:"+
                mLeftBorderActive +", mRightBorderActive:"+mRightBorderActive+
        		", mTopBorderActive:"+mTopBorderActive +", mBottomBorderActive:"+mBottomBorderActive);

        boolean anyBordersActive = mLeftBorderActive || mRightBorderActive
                || mTopBorderActive || mBottomBorderActive;

        mRunningHInc = 0;
        mRunningVInc = 0;

        if (anyBordersActive) {
            mLeftHandle.setAlpha(mLeftBorderActive ? 1.0f : DIMMED_HANDLE_ALPHA);
            mRightHandle.setAlpha(mRightBorderActive ? 1.0f :DIMMED_HANDLE_ALPHA);
            mTopHandle.setAlpha(mTopBorderActive ? 1.0f : DIMMED_HANDLE_ALPHA);
            mBottomHandle.setAlpha(mBottomBorderActive ? 1.0f : DIMMED_HANDLE_ALPHA);
        }
        mPagedView.getExpandabilityArrayForView(mItemInfo , mExpandability);
        Log.i("zdx1","AppWidgetResizeFrame.beginResizeIfPointInRegion out---hit widget:"+ anyBordersActive);

        mDeltaX = 0;
        mDeltaY = 0;
        return anyBordersActive;
    }

    /**
     *  Here we bound the deltas such that the frame cannot be stretched beyond the extents
     *  of the CellLayout, and such that the frame's borders can't cross.
     */
    public void updateDeltas(int deltaX, int deltaY) {
        if (mLeftBorderActive) {
            mDeltaX = Math.max(-mBaselineX, deltaX); 
            mDeltaX = Math.min(mBaselineWidth - 2 * mTouchTargetWidth, mDeltaX);
        } else if (mRightBorderActive) {
            mDeltaX = Math.min((int)mDragLayer.getWidth() - (mBaselineX + mBaselineWidth), deltaX);
            mDeltaX = Math.max(-mBaselineWidth + 2 * mTouchTargetWidth, mDeltaX);
        }

        if (mTopBorderActive) {
            mDeltaY = Math.max(-mBaselineY, deltaY);
            mDeltaY = Math.min(mBaselineHeight - 2 * mTouchTargetWidth, mDeltaY);
        } else if (mBottomBorderActive) {
            mDeltaY = Math.min((int)mDragLayer.getHeight() - (mBaselineY + mBaselineHeight), deltaY);
            mDeltaY = Math.max(-mBaselineHeight + 2 * mTouchTargetWidth, mDeltaY);
        }
    }

    /**
     *  Based on the deltas, we resize the frame, and, if needed, we resize the widget.
     */
    public void visualizeResizeForDelta(int deltaX, int deltaY) {
    	Log.i("zdx1","AppWidgetResizeFrame.visualizeResizeForDelta--deltaX:"+deltaX+", deltaY:"+deltaY );
        updateDeltas(deltaX, deltaY);
        
        if (mLeftBorderActive) {
            mLp.x = mBaselineX + mDeltaX;
            mLp.width = mBaselineWidth - mDeltaX;
        } else if (mRightBorderActive) {
            mLp.width = mBaselineWidth + mDeltaX;
        }

        if (mTopBorderActive) {
            mLp.y = mBaselineY + mDeltaY;
            mLp.height = mBaselineHeight - mDeltaY;
        } else if (mBottomBorderActive) {
            mLp.height = mBaselineHeight + mDeltaY;
        }
        
        try {
            resizeWidgetIfNeeded();
        }catch (Exception e) {
            e.printStackTrace();
        }
        resize(new RectF(mLp.x, mLp.y, mLp.x+mLp.width, mLp.y+mLp.height));
        mBackgroundHandle.resize(new RectF(0, 0, mLp.width, mLp.height));
        if(mLeftBorderActive){
        	mLeftHandle.setRelativeX(0);
        	mLeftHandle.setRelativeY(1.0f*(mLp.height-mLeftBitmapHeight)/2);
        }else if(mRightBorderActive){
        	mRightHandle.setRelativeX(mLp.width-mRightBitmapWidth);
        	mRightHandle.setRelativeY(1.0f*(mLp.height-mRightBitmapHeight)/2);
        }else if(mTopBorderActive){
        	mTopHandle.setRelativeX(1.0f*(mLp.width-mTopBitmapWidth)/2);
        	mTopHandle.setRelativeY(0);
        }else if(mBottomBorderActive){
        	mBottomHandle.setRelativeX(1.0f*(mLp.width-mBottomBitmapWidth)/2);
        	mBottomHandle.setRelativeY(mLp.height-mBottomBitmapHeight);
        }
        
        Log.i("zdx1","visualizeResizeForDelta--mLp.x:"+ mLp.x +", mLp.y:"+ mLp.y +", width:"+ mLp.width +", height:"+ mLp.height);
        invalidate();
    }

    /**
     *  Based on the current deltas, we determine if and how to resize the widget.
     */
    private void resizeWidgetIfNeeded() {
    	Log.i("zdx1","AppWidgetResizeFrame.resizeWidgetIfNeeded()");
    	ItemInfo infoOld = new ItemInfo(mItemInfo);

        int xThreshold = mPagedView.getCellWidth() + mPagedView.getWidthGap();
        int yThreshold = mPagedView.getCellHeight() + mPagedView.getHeightGap();
        float hSpanIncF = 1.0f * mDeltaX / xThreshold - mRunningHInc;
        float vSpanIncF = 1.0f * mDeltaY / yThreshold - mRunningVInc;

        int hSpanInc = 0;
        int vSpanInc = 0;
        int cellXInc = 0;
        int cellYInc = 0;

        if (Math.abs(hSpanIncF) > RESIZE_THRESHOLD) {
            hSpanInc = Math.round(hSpanIncF);
        }
        if (Math.abs(vSpanIncF) > RESIZE_THRESHOLD) {
            vSpanInc = Math.round(vSpanIncF);
        }

        if (hSpanInc == 0 && vSpanInc == 0){ 
        	return;
        }

        final AppWidgetProviderInfo info = mWidgetView.getAppWidgetInfo();
        int[] result = getMinResizeSpanForWidget(info, null);        
        mMinHSpan = result[0];
        mMinVSpan = result[1];    

        // For each border, we bound the resizing based on the minimum width, and the maximum
        // expandability.
        if (mLeftBorderActive) {
            cellXInc = Math.max(-mExpandability[LEFT], hSpanInc);
            cellXInc = Math.min(mItemInfo.spanX - mMinHSpan, cellXInc);
            hSpanInc *= -1;
            hSpanInc = Math.min(mExpandability[LEFT], hSpanInc);
            hSpanInc = Math.max(-(mItemInfo.spanX - mMinHSpan), hSpanInc);
            mRunningHInc -= hSpanInc;
        } else if (mRightBorderActive) {
            hSpanInc = Math.min(mExpandability[RIGHT], hSpanInc);
            hSpanInc = Math.max(-(mItemInfo.spanX - mMinHSpan), hSpanInc);
            mRunningHInc += hSpanInc;
        }

        if (mTopBorderActive) {
            cellYInc = Math.max(-mExpandability[TOP], vSpanInc);
            cellYInc = Math.min(mItemInfo.spanY - mMinVSpan, cellYInc);
            vSpanInc *= -1;
            vSpanInc = Math.min(mExpandability[TOP], vSpanInc);
            vSpanInc = Math.max(-(mItemInfo.spanY - mMinVSpan), vSpanInc);
            mRunningVInc -= vSpanInc;
        } else if (mBottomBorderActive) {
            vSpanInc = Math.min(mExpandability[BOTTOM], vSpanInc);
            vSpanInc = Math.max(-(mItemInfo.spanY - mMinVSpan), vSpanInc);
            mRunningVInc += vSpanInc;
        }

        // Update the widget's dimensions and position according to the deltas computed above
        if (mLeftBorderActive || mRightBorderActive) {
        	mItemInfo.spanX += hSpanInc;
            mItemInfo.cellX += cellXInc;
        }

        if (mTopBorderActive || mBottomBorderActive) {
            mItemInfo.spanY += vSpanInc;
            mItemInfo.cellY += cellYInc;
        }
        
      	int x = mItemInfo.cellX * (mPagedView.getCellWidth() + mPagedView.getWidthGap());
      	int y= mItemInfo.cellY * (mPagedView.getCellHeight() + mPagedView.getHeightGap());
      	y = y + (int)mWorkspace.getPagedViewGlobalY2();
      	int width = mItemInfo.spanX * mPagedView.getCellWidth() + 
           		(mItemInfo.spanX - 1) * mPagedView.getWidthGap();
        int height = mItemInfo.spanY * mPagedView.getCellHeight() + 
           		(mItemInfo.spanY - 1) * mPagedView.getHeightGap();
        
        Log.i("zdx1","   resizeWidgetIfNeeded(), new info, cellX:"+ mItemInfo.cellX +", cellY:"+ mItemInfo.cellY+
        		", spanX:"+ mItemInfo.spanX +", spanY:"+ mItemInfo.spanY + 
        		", x:"+ x+", y:"+ y +", width:"+ width +", height:"+ height);

        // Update the expandability array, as we have changed the widget's size.
        mPagedView.getExpandabilityArrayForView(mItemInfo,  mExpandability); 

        //RectF rectF = new RectF(x,y,x+width, y+height);
        //mItem.resize(rectF);
        //mItem.invalidate();
        //mPagedView.markCellsAsUnoccupiedForView(mItemInfo.screen, mItemInfo);
        //mPagedView.markCellsAsOccupiedForView(mItemInfo.screen, mItemInfo);

        mWorkspace.addInScreenNewItem(new XViewContainer(mMainSurface, 
        		mItemInfo.spanX * mWorkspace.getPagedView().getCellWidth(), 
        		mItemInfo.spanY * mWorkspace.getPagedView().getCellHeight(), mWidgetView), 
        		infoOld, mItemInfo);
    }

    /**
     * This is the final step of the resize. Here we save the new widget size and position
     * to LauncherModel and animate the resize frame.
     */
    public void commitResizeForDelta(int deltaX, int deltaY) {
    	Log.i("zdx1","AppWidgetResizeFrame.commitResizeForDelta---deltaX:"+ deltaX +", deltaY:"+ deltaY);
        visualizeResizeForDelta(deltaX, deltaY);

        XLauncherModel.resizeItemInDatabase(mContext.getContext(), mItemInfo, mItemInfo.cellX, mItemInfo.cellY,
        		mItemInfo.spanX, mItemInfo.spanY);

        // Once our widget resizes (hence the post), we want to snap the resize frame to it
        mContext.post(new Runnable() {
            public void run() {
                snapToWidget(true);
            }
        });
    }

    public void snapToWidget(boolean animate) {
    	Log.i("zdx1","\n^^^^^^^^^^^^^^^^^^AppWidgetResizeFrame.snapToWidget*******"+ animate);
    	if( mLp == null){
    	    mLp = new XDragLayer.LayoutParams((int)getWidth(),(int)getHeight());
    	}

        getBaseline();
        
        /*boolean bFullScreen = mLauncher.isCurrentWindowFullScreen();
        Log.i("zdx1","AppWidgetResizeFrame.snapToWidget, FullScreen: "+bFullScreen +", status bar:"+ SettingsValue.getStatusBarHeight(mLauncher));
        Log.i("zdx1","SettingsValue.hasExtraTopMargin()---"+SettingsValue.hasExtraTopMargin());
        //modify by zhanggx1 for new layout. old is "if (bFullScreen)"
        if((SettingsValue.hasExtraTopMargin())
        		|| bFullScreen){
            // there is no status bar, so its newY should be reduced
        	mBaselineY = mBaselineY + SettingsValue.getStatusBarHeight(mLauncher);
        }*/
        
        Log.i("zdx1","AppWidgetResizeFrame.snapToWidget--in draglayer, x: "+ mBaselineX +", y:"+ mBaselineY +
        		", width:"+ mBaselineWidth +", height:"+ mBaselineHeight);

        mLp.width = (int)mBaselineWidth;
        mLp.height = (int)mBaselineHeight;
        mLp.x = (int)mBaselineX;
        mLp.y = (int)mBaselineY;
        
        if (!animate) {
            /*mLp.width = (int)mBaselineWidth;
            mLp.height = (int)mBaselineHeight;
            mLp.x = (int)mBaselineX;
            mLp.y = (int)mBaselineY;*/
            mLeftHandle.setAlpha(1.0f);
            mRightHandle.setAlpha(1.0f);
            mTopHandle.setAlpha(1.0f);
            mBottomHandle.setAlpha(1.0f);
            Log.i("zdx1","AppWidgetResizeFrame.snapToWidget--lp.x:"+ mLp.x +", lp.y:"+ mLp.y +
            		", width:"+mLp.width +", height:"+ mLp.height);
            resize(new RectF(mLp.x, mLp.y, mLp.x+mLp.width, mLp.y+mLp.height));
        } else {
            PropertyValuesHolder width = PropertyValuesHolder.ofFloat("width", mLp.width, mBaselineWidth);
            PropertyValuesHolder height = PropertyValuesHolder.ofFloat("height", mLp.height,
            		mBaselineHeight);
            PropertyValuesHolder x = PropertyValuesHolder.ofFloat("x", mLp.x, mBaselineX);
            PropertyValuesHolder y = PropertyValuesHolder.ofFloat("y", mLp.y, mBaselineY);
            ObjectAnimator oa = ObjectAnimator.ofPropertyValuesHolder(mLp, width, height, x, y);
            ObjectAnimator leftOa = ObjectAnimator.ofFloat(mLeftHandle, "alpha", 1.0f);
            ObjectAnimator rightOa = ObjectAnimator.ofFloat(mRightHandle, "alpha", 1.0f);
            ObjectAnimator topOa = ObjectAnimator.ofFloat(mTopHandle, "alpha", 1.0f);
            ObjectAnimator bottomOa = ObjectAnimator.ofFloat(mBottomHandle, "alpha", 1.0f);
            oa.addUpdateListener(new AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    refreshDirectionHandle();
                    mLeftHandle.setAlpha(1.0f);
                    mRightHandle.setAlpha(1.0f);
                    mTopHandle.setAlpha(1.0f);
                    mBottomHandle.setAlpha(1.0f);                  
                	invalidate();
                }
            });
            AnimatorSet set = new AnimatorSet();
            if (mResizeMode == AppWidgetProviderInfo.RESIZE_VERTICAL) {
                set.playTogether(oa, topOa, bottomOa);
            } else if (mResizeMode == AppWidgetProviderInfo.RESIZE_HORIZONTAL) {
                set.playTogether(oa, leftOa, rightOa);
            } else {
                set.playTogether(oa, leftOa, rightOa, topOa, bottomOa);
            }

            set.setDuration(SNAP_DURATION);
            set.start();
        }
    }
    
    int[] getMinResizeSpanForWidget(AppWidgetProviderInfo info, int[] spanXY) {
        return mLauncher.getSpanForWidget(info.provider,info.minResizeWidth, info.minResizeHeight,
        		spanXY);
    }
    
    private void getBaseline(){
      	mBaselineX = mItemInfo.cellX * (mPagedView.getCellWidth() + mPagedView.getWidthGap());
      	mBaselineY = mItemInfo.cellY * (mPagedView.getCellHeight() + mPagedView.getHeightGap());
      	mBaselineWidth = mItemInfo.spanX * mPagedView.getCellWidth() + 
           		(mItemInfo.spanX - 1) * mPagedView.getWidthGap();
        mBaselineHeight = mItemInfo.spanY * mPagedView.getCellHeight() + 
           		(mItemInfo.spanY - 1) * mPagedView.getHeightGap();
        
        Log.i("zdx1","    AppWidgetResizeFrame.getBaseline, x: "+ mBaselineX +", y:"+ mBaselineY +
        		", width:"+ mBaselineWidth +", height:"+ mBaselineHeight);
        
      	mBaselineX = mBaselineX - mBackgroundPadding + 
      			mWidgetPaddingLeft  + mFramePadding +/* (int)mPagedView.getGlobalX2()
      			+*/ (int)mWorkspace.getPagedViewGlobalX2();
      	mBaselineY = mBaselineY - mBackgroundPadding + 
      			mWidgetPaddingTop+ (int)mWorkspace.getPagedViewGlobalY2();
        mBaselineWidth = mBaselineWidth + 2 * mBackgroundPadding - 
      			mWidgetPaddingLeft - mWidgetPaddingRight - mFramePadding*2;
        mBaselineHeight = mBaselineHeight + 2 * mBackgroundPadding -
        		mWidgetPaddingTop - mWidgetPaddingBottom;
        			
        Log.i("zdx1","******mWorkspace.getGlobalY2():"+ mWorkspace.getPagedViewGlobalY2());
        Log.i("zdx1","******mWorkspace.getGlobalX2():"+ mWorkspace.getPagedViewGlobalX2());
        
        Log.i("zdx1","    AppWidgetResizeFrame.getBaseline--has padding, x: "+ mBaselineX +", y:"+ mBaselineY +
                		", width:"+ mBaselineWidth +", height:"+ mBaselineHeight);
    }

    private void refreshDirectionHandle(){
    	Log.i("zdx1","AppWidgetResizeFrame.refreshDirectionHandle-- x: "+ mBaselineX +", y:"+ mBaselineY +
           		", width:"+ mBaselineWidth +", height:"+ mBaselineHeight);
    	
    	resize(new RectF(mBaselineX, mBaselineY, mBaselineX+mBaselineWidth, mBaselineY+mBaselineHeight));
        mBackgroundHandle.resize(new RectF(0,0, mBaselineWidth, mBaselineHeight));
        
        mLeftHandle.setRelativeX(0);
        mLeftHandle.setRelativeY(1.0f*(mBaselineHeight-mLeftBitmapHeight)/2);

        mRightHandle.setRelativeX(mBaselineWidth- mRightBitmapWidth);
        mRightHandle.setRelativeY(1.0f*(mBaselineHeight-mRightBitmapHeight)/2);
        
        mTopHandle.setRelativeX(1.0f*(mBaselineWidth-mTopBitmapWidth)/2);
        mTopHandle.setRelativeY(0);
        
        mBottomHandle.setRelativeX(1.0f*(mBaselineWidth-mBottomBitmapWidth)/2);
        mBottomHandle.setRelativeY(mBaselineHeight-mBottomBitmapHeight);
    }
  
}

