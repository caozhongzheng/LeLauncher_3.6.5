package com.lenovo.launcher.components.XAllAppFace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.lenovo.launcher.components.XAllAppFace.slimengine.BaseDrawableGroup;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.ExchangeManager;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IController;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.LGestureDetector;
import com.lenovo.launcher.components.XAllAppFace.slimengine.NormalDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;
import com.lenovo.launcher.components.XAllAppFace.utilities.Debug.D2;
import com.lenovo.launcher.components.XAllAppFace.utilities.Debug.R2;
import com.lenovo.launcher.components.XAllAppFace.utilities.Utilities;
import com.lenovo.launcher2.commoninterface.FolderInfo;
import com.lenovo.launcher2.commoninterface.IconCache;
import com.lenovo.launcher2.commoninterface.ItemInfo;
import com.lenovo.launcher2.commoninterface.LauncherAppWidgetInfo;
import com.lenovo.launcher2.commoninterface.LauncherSettings;
import com.lenovo.launcher2.commoninterface.LenovoWidgetViewInfo;
import com.lenovo.launcher2.commoninterface.ShortcutInfo;
import com.lenovo.launcher2.customizer.Debug.R5;
import com.lenovo.launcher2.customizer.LauncherPersonalSettings;
import com.lenovo.launcher2.customizer.SettingsValue;

public class XPagedView extends BaseDrawableGroup{
	
	//test
	public static final boolean DEBUG_CELL = false;
	public static final boolean DEBUG_ITEM = false;
	public static final boolean DEBUG_ANIM_TO = false;

	public interface PageSwitchListener {

		void onUpdatePage(int pageCount, int currentPage);

		void onPageSwitching(int from, int to, float percentage);

		void onPageBeginMoving(int currentPage);

		void onPageEndMoving(int currentPage);
	}

	public interface PageDrawAdapter {
		/**
		 * @param canvas
		 * @param page
		 *            当前页码
		 * @param offsetX
		 *            -1f~1f，表示当前页的横向偏移量, -1f表示完全偏移到上一屏, 1f表示偏移到下一屏
		 */
		public void drawPage(IDisplayProcess canvas, int page, float offsetX, float offsetY);

		public void reset();

	}

	protected ConcurrentLinkedQueue<PageSwitchListener> pageSwitchers;

	protected static final int ORI_RIGHT = 1;
	protected static final int ORI_NONE = 0;
	protected static final int ORI_LEFT = -1;
	private static final int MaxCellCountX = 4;
	private static final int MaxCellCountY = 6;
	protected int mCellWidth = -1;
	protected int mCellCountX = 4;
	protected int mCellCountY = 5;
	private int mCellHeight = -1;
	private int mOriginalCellWidth = 0;
	private int mOriginalCellHeight = 0;
	private int mOriginalWidthGap = 0;
	private int mOriginalHeightGap = 0;
	private int mWidthGap = 0;
	private int mHeightGap = 0;
	private int mMaxGap = 0;
	private int mMaxYGap = 0;

	protected int mPageCount = -1;
	protected int mCurrentPage = -1;

	protected float mOffsetX = 0;
	protected float mOffsetXTarget = 0;

	protected float mOffsetY = 0;
	protected float mOffsetYTarget = 0;

	private float rect2ball = 0;
	private float rect2ballTarget = 0;

	protected static long OffsetXAnimDuration = 275;//600;
	protected static final long OffsetYAnimDuration = 400;

	private boolean isLoop = true;

	protected boolean isPageMoving = false;

    protected int currOrientation = 0;

	/*private*/protected PageDrawAdapter mPageDrawAdapter = null;
	protected PageAnimController mAnimController;
	private boolean mEditMode = false;

	private final Vibrator mVibrator;
	private static final int VIBRATE_DURATION = 35;

	public final HashMap<Long, XPagedViewItem> mItemIDMap = new HashMap<Long, XPagedViewItem>();
	private int mPagedViewItemCount = 0;
	private long mPagedViewItemId = 0;
	
	private int _gapBetweenPage = 0;
	private boolean _gapEnable = true;
	/** Set the page between pages */
	public void setPageGap( int gap ){
//		if( hasSetup ){
//			throw new RuntimeException( "need call before setup." );
//		}
		_gapEnable = true;
		_gapBetweenPage = gap;
	}
	
	public void setPageGapEnable( boolean enable ){
		_gapEnable = enable;
	}
	
	public int getPageGap(){
		if( !_gapEnable ){
			return 0;
		}
		return _gapBetweenPage;
	}
	
	private boolean _configurationChange = false;
	private boolean _onscrollflag = false;

	public void configurationChange(final boolean $configurationChange) {
		_configurationChange = $configurationChange;
		_onscrollflag = $configurationChange;
	}
	
	public boolean configurationChange(){
		return _configurationChange;
	}

	// for view container stage
	private FrameLayoutEx mStage;
	private boolean enableStage = false;
	private boolean enableEffect = true;
	protected float density = 1.5f;
	private XContext mXContext;
	private int mphoneindex =-1;
	public void adjustPageOffset(long millisTime){
		mAnimController.startOffsetXAnim( millisTime );
		mAnimController.startOffsetYAnim( millisTime );
	}
	
	public void setStageEnabled( boolean enable ){
		enableStage = enable;
	}
	public void setDeviceType( int phoneindex ){
		mphoneindex = phoneindex;
	}
	public boolean isPageMoving(){
		return isPageMoving;
	}
	
	public void setStageVisibility( final boolean visible ){
		mStage.post(new Runnable() {
			
			@Override
			public void run() {
				mStage.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
			}
		});
	}
	
	
	FrameLayoutEx getStage(){
		return mStage;
	}
	
	public void setViewStageVisibility(boolean show) {
		if (!enableStage) {
			return;
		}

		if (show) {
			mStage.post(new Runnable() {
				@Override
				public void run() {
					mStage.setVisibility(View.VISIBLE);
				}
			});
		} else {
			mStage.post(new Runnable() {
				@Override
				public void run() {
					mStage.setVisibility(View.INVISIBLE);
				}
			});
		}
	}
	
	
	private float distanceX = 0f;
	private float distanceY = 0f;
	private float vx = 0f;
	private float vy = 0f;
	
	private MotionEvent lastMoveEvent = null;
	
	public class FrameLayoutEx extends FrameLayout{
		
		public void setLongPressState(boolean happen){
			getXContext().stealLongPress(happen);
			getXContext().setLongPressStateExternal(happen);
		}

		public FrameLayoutEx(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
		}

		public FrameLayoutEx(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		public FrameLayoutEx(Context context) {
			super(context);
		}
		
	}

	public void settleStage(final RectF rect ) {

		if (!enableStage) {
			return;
		}
		
		density = getXContext().getResources().getDisplayMetrics().density;

		if (mStage == null) {
			
			mStage = new FrameLayoutEx(getXContext().getContext()) {

				boolean grabScroll = false;
				VelocityTracker mVelocityTracker;

				@Override
				public boolean dispatchTouchEvent(MotionEvent e) {
					
//					if(XContext.blockAllEvent()){
//						return true;
//					}
					
					if( e.getAction() == MotionEvent.ACTION_CANCEL && mXContext.isGrabScrollState()){
						e.setAction( MotionEvent.ACTION_UP );
					}
					
						
					if (mVelocityTracker == null) {
						mVelocityTracker = VelocityTracker.obtain();
					}
					mVelocityTracker.addMovement(e);

					if (e.getAction() == MotionEvent.ACTION_DOWN) {
						lastMoveEvent = MotionEvent.obtain(e);
						getXContext().setGrabScroll(false);
						grabScroll = false;
					}

					if (e.getAction() == MotionEvent.ACTION_UP
							|| e.getAction() == MotionEvent.ACTION_CANCEL) {
						lastMoveEvent = null;
						mVelocityTracker.recycle();
						mVelocityTracker = VelocityTracker.obtain();
					}

//					android.util.Log.i( "wow", "grabScroll check BF: " + grabScroll );
					if (e.getAction() == MotionEvent.ACTION_MOVE && !grabScroll) {

						// record event
						if (lastMoveEvent != null) {
							distanceX = e.getX() - lastMoveEvent.getX();
							distanceY = e.getY() - lastMoveEvent.getY();
						}

						lastMoveEvent = MotionEvent.obtain(e);

						//
						final VelocityTracker velocityTracker = mVelocityTracker;
						velocityTracker.computeCurrentVelocity(1000,
								ViewConfiguration.getMaximumFlingVelocity());
						final float velocityX = velocityTracker.getXVelocity();
						final float velocityY = velocityTracker.getYVelocity();

						boolean grab1 = Math.abs(distanceY) > 5f * density;
						boolean grab2 = Math.abs(velocityX) < Math
								.abs(velocityY) && Math.abs(velocityX) < 3500;

						grabScroll = ( grab2 && grab1);
//						android.util.Log.i( "wow", "grabScroll check AF: " + grabScroll + " , g1 : " + grab1
//								+ " , g2 : " + grab2);
//						android.util.Log.i( "wow", " g2 is : " + Math.abs(velocityX) * 1.3f + " , "
//								+ Math.abs(velocityX));
					}

					// check distance
					
					if (e.getPointerCount() == 1)
					{
						if (grabScroll) {
							R5.echo("setGrabScroll");
							getXContext().setGrabScroll(true);
							super.dispatchTouchEvent(e);
							return false;
						}else{
	/*						if(cancelFirstMove){
								getXContext().bringContentViewToFront();
							}else{
								bringStageToFront();
							}*/
						}
					}
					else
					{
						getXContext().setGrabScroll(false);
					}

					return super.dispatchTouchEvent(e);
				}
				
				
				@Override
				public boolean onTouchEvent(MotionEvent event) {
					if( event.getAction() == MotionEvent.ACTION_CANCEL ||
							event.getAction() == MotionEvent.ACTION_UP ){
						for( int i = 0 ; i < getChildCount() - 1 ; i ++ ){
							this.getChildAt( i ).clearFocus();
							this.getChildAt( i ).clearAnimation();
						}
					}
					return super.onTouchEvent(event);
				}

			};
			
			mStage.setLongClickable(false);
			mStage.setHapticFeedbackEnabled(false);
			
			// test
			if( DEBUG_CELL ){
				mStage.setBackgroundColor( Color.argb(100, 200, 0, 0));
			}

		} else {
			getXContext().post(new Runnable() {
				@Override
				public void run() {
					getXContext().removeView(mStage);
				}
			});
		}

		getXContext().post(new Runnable() {
			@Override
			public void run() {
				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
						(int)localRect.width() - mPaddingLeft - mPaddingRight,
						(int)localRect.height() - mPaddingTop - mPaddingBottom);
				params.leftMargin = (int) (getGlobalX2());
				params.topMargin = (int) (getGlobalY2());
				if(DEBUG_CELL)
					android.util.Log.i("page", "mStage slot at : " + params.leftMargin
							+ " , " + params.topMargin);
				getXContext().addView(mStage, params);
				getXContext().requestLayout();
			}
		});
	}
	
	void hackAlignStageForOffset( float x, float y){
		if( mStage != null ){
			mStage.setTranslationX( x );
			mStage.setTranslationY( y );
			mStage.invalidate();
		}
	}

    public void bringStageToFront() {
        if (mStage != null) {
            getXContext().post(new Runnable() {
                @Override
                public void run() {
                    getXContext().bringChildToFront(mStage);
                    getXContext().requestLayout();
                }
            });
        }
	}

	@Override
	public void setRelativeX(float x) {
		super.setRelativeX(x);
		settleStage(localRect);
	}
	
	@Override
	public void setRelativeY(float y) {
		super.setRelativeY(y);
		settleStage(localRect);
	}

	public void addPageSwitchListener(PageSwitchListener pl) {
		if (this.pageSwitchers == null) {
			this.pageSwitchers = new ConcurrentLinkedQueue<XPagedView.PageSwitchListener>();
		}

		pageSwitchers.add(pl);
	}

	public boolean removePageSwitchListener(PageSwitchListener pl) {
		if (pageSwitchers == null) {
			return false;
		}
		return this.pageSwitchers.remove(pl);
	}

	public XPagedView(XContext context , RectF pageRect ) {
		super(context);

		disableCache();
		mXContext = context;
		resize(pageRect);
		mAnimController = new PageAnimController();
		registerIController(mAnimController);

		// this.setController(mAnimController);
		mVibrator = (Vibrator) getXContext().getContext()
				.getSystemService(Context.VIBRATOR_SERVICE);
		if("S960".equals(SystemProperties.get("ro.product.name","unknow")))  //如果是S960项目，则会走进if分支
        {
            OffsetXAnimDuration = 150;
        }
	}

	private boolean needCacheViewContainer = true;
	
	public boolean needCacheViewContainer(){
		return needCacheViewContainer;
	}
	
	private boolean hasSetup = false;
	public void setup(int screen, int cellCountX, int cellCountY) {
		hasSetup = true;
		if(DEBUG_CELL)
			android.util.Log.i("resize", "now set up . " + this);
        mPageCount = screen;
        mCellCountX = cellCountX;
        mCellCountY = cellCountY;
        
        mOccupied = new boolean [mPageCount][mCellCountX][mCellCountY];
        mHasWidget = new boolean [mPageCount];
        mHasChild = new boolean [mPageCount];
        
        mCellWidth = ((int) localRect.width() - (int)(_gapEnable ? _gapBetweenPage : 0f)) / mCellCountX;
        mCellHeight = (int) localRect.height() / mCellCountY;
        mOriginalCellWidth = mCellWidth;
        mOriginalCellHeight = mCellHeight;

        final int cellSpaceCount = mPageCount * mCellCountX * mCellCountY;

        for (int i = 0; i < cellSpaceCount; i++) {
            XCell cell = new XCell(getXContext(), null, null);
            addItem(cell, i);
            cell = null;
        }
        
        if (this.mPageDrawAdapter != null) {
            this.mPageDrawAdapter.reset();
        }

        if(enableStage){
            settleStage(localRect);
        }
        
        updateIndicator();
        
        // visible listen always
        this.wantKnowVisibleState(true);
        this.setOnVisibilityChangeListener( new OnVisibilityChangeListener() {
			
			@Override
			public void onVisibilityChange(DrawableItem who, boolean visible) {
				if (!visible) {
					mAnimController.stopTouchAnim();
					mAnimController.stopOffsetXAnim();
					needCacheViewContainer = false;

				}
				else {
					needCacheViewContainer = true;

					ArrayList<XPagedViewItem> items = getChildrenAt( mCurrentPage - 1);
					
					for (XPagedViewItem item : items) {
						if (item.getDrawingTarget() instanceof XViewContainer) {
							((XViewContainer) (item.getDrawingTarget())).manageVisibility(
									XViewContainer.VISIBILITY_SHOW_SHADOW, null);
						}
					}
				}
				
				if (mAnimController != null) {
					if (!visible) {
						unregisterIController(mAnimController);
					} else {
						registerIController(mAnimController);
					}
				}
			}
		});
    }

	public void calculateCellCount(int width, int height, int maxCellCountX, int maxCellCountY) {
		mCellCountX = Math.min(maxCellCountX, estimateCellHSpan(width));
		mCellCountY = Math.min(maxCellCountY, estimateCellVSpan(height));
	}

	/**
	 * Estimates the number of cells that the specified width would take up.
	 */
	public int estimateCellHSpan(int width) {
		// We don't show the next/previous pages any more, so we use the full
		// width, minus the
		// padding
		int availWidth = width - (mPaddingLeft + mPaddingRight);

		// We know that we have to fit N cells with N-1 width gaps, so we just
		// juggle to solve for N
		int n = Math.max(1, (availWidth + mWidthGap) / (mCellWidth + mWidthGap));

		// We don't do anything fancy to determine if we squeeze another row in.
		return n;
	}

	/**
	 * Estimates the number of cells that the specified height would take up.
	 */
	public int estimateCellVSpan(int height) {
		// The space for a page is the height - top padding (current page) -
		// bottom padding (current
		// page)
		int availHeight = height - (mPaddingTop + mPaddingBottom);

		// We know that we have to fit N cells with N-1 height gaps, so we
		// juggle to solve for N
		int n = Math.max(1, (availHeight + mHeightGap) / (mCellHeight + mHeightGap));

		// We don't do anything fancy to determine if we squeeze another row in.
		return n;
	}

	public void setPageDrawAdapter(PageDrawAdapter mDrawAdapter) {
		if (mDrawAdapter != this.mPageDrawAdapter) {
			this.mPageDrawAdapter = mDrawAdapter;
			if (mAnimController != null) {
				mAnimController.setSphereSlide(false);
			}
		}
	}
	
	protected void updateCellSpanSpace(){
	    int oldCellWidth = mCellWidth;
	    int oldCellHeight = mCellHeight;
		
        mCellWidth = ((int) localRect.width()  - (int)(_gapEnable ? _gapBetweenPage : 0f))/ mCellCountX;
        mCellHeight = (int) localRect.height() / mCellCountY;
        mOriginalCellWidth = mCellWidth;
        mOriginalCellHeight = mCellHeight;

        calculateGlobalTouchRect();
        
        if (mCellWidth != oldCellWidth || mCellHeight != oldCellHeight)
        {
            updateLayoutAfterResize();
        }
	}
	
    private void updateLayoutAfterResize() {
        final int cellSpaceCount = mPageCount * mCellCountX * mCellCountY;

        for (int i = 0; i < cellSpaceCount; i++) {
            XCell cell = (XCell)getChildAt(i);
            if (cell == null) {
                continue;
            }
            
            cell.setRelativeX( (i % mCellCountX ) * mCellWidth + (_gapEnable ? _gapBetweenPage / 2 : 0f));
            cell.setRelativeY( ((i%(mCellCountX * mCellCountY)) / mCellCountX) * mCellHeight);

            cell.setRelativeX(cell.getRelativeX() - localRect.width());
            Matrix m = cell.getMatrix();
            m.reset();
            cell.updateMatrix(m);   
        }
    }
    
    private void resetScrollState(){
		
		mOffsetX = 0;
		mOffsetY = 0;
		mOffsetXTarget = 0;
		mOffsetYTarget = 0;
		isPageMoving = false;
		
    }

	public void resize(RectF rect) {
		
		resetScrollState();
		
		this.localRect.set(rect);
        if (enableStage && mStage != null) {
            LayoutParams params = (LayoutParams) mStage.getLayoutParams();
            if (params == null) {
                settleStage(this.localRect);
            } else {
            	params.width = (int) localRect.width();
            	params.height = (int) localRect.height();
            	params.leftMargin = (int) getGlobalX2();
                params.topMargin = (int) getGlobalY2();
                mStage.setLayoutParams(params);
            }
        } else if (enableStage && mStage == null) {
            settleStage(this.localRect);
        }

		calculateGlobalTouchRect();
		
		updateCellSpanSpace();
		
		//resize children
		if (_configurationChange) {
			for (final XPagedViewItem item : ((HashMap<Long, XPagedViewItem>) (mItemIDMap
					.clone())).values()) {
				if (item != null && item.getDrawingTarget() != null) {
					removePagedViewItem(item);
					addPagedViewItem(item);
					
					if( item.getDrawingTarget() instanceof XViewContainer ){
						((XViewContainer)item.getDrawingTarget()).unfreezingDrawingMode();
					}
					item.resize();
				}
			}
			_configurationChange = false;
		}
		setInvertMatrixDirty();
		
		if(normalAdapter != null){
			normalAdapter.updateValues();
			normalAdapter.reset();
		}
		
		if(sphereAdapter != null){
			sphereAdapter.reset();
		}
	}

	synchronized void updatePageCount() {
	    int oldCount = mPageCount;
		int size = Math.max(getChildCount(), 0);
		mPageCount = (int) Math.ceil((float) size / (mCellCountX * mCellCountY));
		if (mPageCount == oldCount)
		{
		    return;
		}
		
		if (mPageCount >= 0) {
			if (mCurrentPage >= mPageCount) {
				mCurrentPage = mPageCount - 1;
			}
			if (mCurrentPage < 0) {
				mCurrentPage = 0;
			}
			updateIndicator();
		}

		/* RK_ID: RK_SLIDEEFFECT. AUT: liuli1 . DATE: 2012-12-06 . START */
//		if (normalAdapter != null) {
//			normalAdapter.setup( this );
//		}
//		
//		if(mPageDrawAdapter != null){
//			mPageDrawAdapter.setup( this );
//		}
		/* RK_ID: RK_SLIDEEFFECT. AUT: liuli1 . DATE: 2012-12-06 . END */
	}

	private void setChildVisible(int page, boolean visibility) {
		int startIndex = page * mCellCountX * mCellCountY;
		int endIndex = Math.min(startIndex + mCellCountX * mCellCountY, getChildCount());
		this.setChildVisible(startIndex, endIndex, visibility);
	}

	private void setChildVisible(int startIndex, int endIndex, boolean visibility) {
		// List<DrawableItem> items = getChildren();
		// int size = items.size();
		if (getChildCount() > 0) {
			if (startIndex > -1 && endIndex >= startIndex && endIndex < getChildCount()) {
				for (int i = startIndex; i < endIndex; i++) {
					getChildAt(i).setVisibility(visibility);
				}
			}
		}
	}

	private boolean mScrollReset = false;
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY, float previousX, float previousY) {
		
		if( mXContext.isGrabScrollState() ){
			resetOffset();
			return false;
		}
        
		if (mDebug)R5.echo("onScroll" + this + "distanceX = " + distanceX);
//     if(!mScrollReset && (Math.abs(distanceY) > 10 || Math.abs(distanceX) > 10)){
	    if(!mScrollReset){
	        if (mDebug)R5.echo("onScroll resetPressedState" + this);
	        mScrollReset = true;
            resetPressedState(true);
        }
	    
	    if (gestureProcess)
	    {
	        return true;
	    }
	    
		// do not begin while wrong direction
		if(!isPageMoving && Math.abs(distanceY) > LGestureDetector.mScrollYfactor * Math.abs(distanceX)){
			if (D2.DEBUG) {
				D2.echo("return here '");
				R2.printStack("D2");
			}
			return true;
		}
		
//		if (!isPageMoving && distanceX < (LGestureDetector.mToucheSlop + 3)
		if (!isPageMoving && getParent() instanceof XWorkspace) {
			distanceX = previousX;
		}

		mOffsetXTarget -= distanceX / getWidth();
		if (isScrollHalfBack()) {
		    mOffsetXTarget += distanceX / getWidth() / 2.0f;
		}			

		/*** RK_ID: RK_SLIDEEFFECT AUT: zhaoxy . DATE: 2012-12-10 . START ***/
		mOffsetYTarget += distanceY / getHeight();
		if (Math.abs(mOffsetYTarget) > 1) {
			mOffsetYTarget = Math.signum(mOffsetYTarget);
		}

		if (!isPageMoving) {
//			R5.echo("distanceX =" + distanceX);
			isPageMoving = true;
			onPageBeginMoving();
		}
				
		rect2ballTarget = 1.0f;
		mAnimController.startTouchAnim();
                 
        //Log.e("yumin0913","onScrol22222222222222211"+_onscrollflag);
		if(_onscrollflag){
                    resetOffset();
		//_onscrollflag = false; 
                }

		/*** RK_ID: RK_SLIDEEFFECT AUT: zhaoxy . DATE: 2012-12-10 . END ***/
		// Log.d("event", "currPage = " + mCurrentPage + " offset = " +
		// mOffsetX);
		invalidateAtOnce();
				
		return true;
	}

    private boolean isScrollHalfBack() {
        // when loop, not half back.
        // when scroll back disabled, not half back.
        if (isLoop || !enableScrollBack) {
            return false;
        }
        // Log.i("Test", "distanceX ======= current page === " + mCurrentPage);
        // Log.i("Test", "distanceX ======= current offsetx === " + mOffsetXTarget);

        int nextPage = -1;
        if (mOffsetXTarget > 0 && mOffsetXTarget < 1f) {
            nextPage = mCurrentPage - 1;
        } else if (mOffsetXTarget > -1f && mOffsetXTarget < 0) {
            nextPage = mCurrentPage + 1;
        }

//        Log.i("Test", "distanceX =======nextPage === " + nextPage);
        if (nextPage < 0 || nextPage >= getPageCount()) {
            return true;
        }
        return false;
    }

    @Override
	public void draw(IDisplayProcess canvas) {
        if (!isVisible()) {
            return;
        }

		updateFinalAlpha();

        if (mPageDrawAdapter == null) {
            if (normalAdapter == null)
                normalAdapter = new NormalDrawAdapter(this);
            setPageDrawAdapter(normalAdapter);
        }

//		if (mPageDrawAdapter == null) {
//			if (sphereAdapter == null) {
//				sphereAdapter = new SphereDrawAdapter(false, this);
//			}
//			sphereAdapter.resetSphereOrCylinder(false);
//			setPageDrawAdapter(sphereAdapter);
//			if (mAnimController != null) {
//				mAnimController.setSphereSlide(true);
//			}
//		}

		if (mPageDrawAdapter != null) {
			mPageDrawAdapter.drawPage(canvas, mCurrentPage, mOffsetX, mOffsetY);
		}
		
		//debug
		if (DEBUG_CELL) {
			Paint p = new Paint();
			p.setStrokeWidth(3f);
			p.setStyle(Style.STROKE);
			p.setColor(Color.CYAN);
			canvas.drawRect(localRect, p);
		}
	}
	private static final int DEFAULT_SLIDE_MODE_SLOT = 460;

	@Override
	public boolean onDown(MotionEvent e) {
//		R5.echo("onDown " + this);
	 _onscrollflag = false;
	    mScrollReset = false;
		if( extraTouchRegionForPagedView == null || extraTouchRegionForPagedView.isEmpty()){
			DisplayMetrics m = getXContext().getContext().getResources().getDisplayMetrics();
			extraTouchRegionForPagedView = new RectF( 0, 0, m.widthPixels, m.heightPixels );
		}
		setExtraTouchBounds( extraTouchRegionForPagedView );
		desireTouchEvent(true);

		mAnimController.stopOffsetYAnim();
		mAnimController.stopOffsetXAnim();
//		mAnimController.startTouchAnim();
		/*** RK_ID: XFOLDER. AUT: zhaoxy . DATE: 2013-01-22 . START ***/
		super.onDown(e);
		
		return true;
		/*** RK_ID: XFOLDER. AUT: zhaoxy . DATE: 2013-01-22 . END ***/
	}

	@Override
	public boolean onFingerUp(MotionEvent e) {
		_onscrollflag = false; 
		resetTouchBounds();
		desireTouchEvent(false);

	    /*** RK_ID: RK_SLIDEEFFECT  AUT: zhaoxy . DATE: 2012-12-10 . START***/
        if (isOnFling) {
            mOffsetY = mOffsetYTarget = 0;
            mAnimController.stopTouchAnim();
            rect2ballTarget = 0.0f;
        } else {
            if (mDebug)R5.echo("onFingerUp currOrientation = " + currOrientation + "mOffsetX = " + mOffsetX);
            scrollToPosition();
        }
        /*** RK_ID: RK_SLIDEEFFECT  AUT: zhaoxy . DATE: 2012-12-10 . END***/

//		mDraggingMode = false;
//		mLongPressed = false;
        
        //for stage switch
        if(mLongPressHappened){
        	bringStageToFront();
        	mLongPressHappened = false;
        }
        
        isOnFling = false;
		return super.onFingerUp(e);
	}
		
//	@Override
//	public void onTouchCancel( MotionEvent e ) {
//		
//		if( e != null ){
//			android.util.Log.i( "touch", "now touch cancel : " + e.getAction() );
//		}
//		
////kangwei3
//		if(mphoneindex==-1){
//	        resetTouchBounds();
//	        desireTouchEvent(false);
//		}
////		bringStageToFront();
//		if (mDebug)R5.echo("XPagedView onTouchCancel");
//		
//		resetAnim();
////		scrollToPosition();
//	    super.onTouchCancel( e );
//	}

	public void resetAnim() {
		if (!mAnimController.isOffsetXAnimStart()) {
			mAnimController.startOffsetXAnim(  OffsetXAnimDuration);
		}
		if (!mAnimController.isOffsetYAnimStart()) {
			mAnimController.startOffsetYAnim(  OffsetYAnimDuration);
		}
		mAnimController.stopTouchAnim();
		
		invalidateAtOnce();

		super.onTouchCancel( null );
	}

	public boolean isLoop() {
		return isLoop;
	}

	public void setLoop(boolean isLoop) {
		this.isLoop = isLoop;

		if (mPageDrawAdapter instanceof XpFlatDrawAdapter) {
			((XpFlatDrawAdapter) mPageDrawAdapter).updateValues();
		}
	}
	
	private boolean isOnFling = false;

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		
//		android.util.Log.i( "wow", "onFling in pagedView : " + mXContext.isGrabScrollState() );
		if( mXContext.isGrabScrollState() ){
			return false;
		}
		
		//D2
//		float durationAccVX = velocityX;
//		D2.echo("This duration is : " + durationAccVX);
//		float vx = Math.abs(velocityX);
//		vx/39.37f
        if (gestureProcess)
        {
            return true;
        }
        
        if ((Math.abs(velocityX) < LGestureDetector.NormalFlingVelocity())) {
            return true;
        }
        
	    int old = currOrientation;
	    currOrientation = (int) Math.signum(velocityX);

        // added by liuli1
        if (currOrientation == ORI_LEFT && mOffsetX > 0.001) {
            Log.i(TAG, "not snapToRight~~~~~~~~offsetX =====" + mOffsetX);
            currOrientation = ORI_NONE;
        } else if (currOrientation == ORI_RIGHT && mOffsetX < -0.001) {
            Log.i(TAG, "not snapToLeft~~~~~~~~offsetX =====" + mOffsetX);
            currOrientation = ORI_NONE;
        }
	    
	    if (mDebug)R5.echo("onFling currOrientation = " + currOrientation + "velocityX = " + velocityX 
	            + "mCurrentPage = " + mCurrentPage + "mOffsetX = " + mOffsetX + "old currOrientation = " +old);
	    		
		isOnFling = true;
		scrollByOrientation();
		
		invalidate();
       /*** RK_ID: XFOLDER. AUT: zhaoxy . DATE: 2013-01-22 . START ***/
		return true;
       /*** RK_ID: XFOLDER. AUT: zhaoxy . DATE: 2013-01-22 . END ***/
	}

    Random mRandom = new Random();
    static final String[] SLIDEEFFECT_ARRAY = { LauncherPersonalSettings.SLIDEEFFECT_CUBE,
            LauncherPersonalSettings.SLIDEEFFECT_SPHERE,
            LauncherPersonalSettings.SLIDEEFFECT_CYLINDER,
            LauncherPersonalSettings.SLIDEEFFECT_NORMAL,
            LauncherPersonalSettings.SLIDEEFFECT_CHARIOT,
            LauncherPersonalSettings.SLIDEEFFECT_WAVE_2, LauncherPersonalSettings.SLIDEEFFECT_WILD,
            LauncherPersonalSettings.SLIDEEFFECT_BULLDOZE,
            LauncherPersonalSettings.SLIDEEFFECT_ROTATE, LauncherPersonalSettings.SLIDEEFFECT_WAVE,
            LauncherPersonalSettings.SLIDEEFFECT_SNAKE,
            LauncherPersonalSettings.SLIDEEFFECT_BOUNCE,
            LauncherPersonalSettings.SLIDEEFFECT_SCALE,
            LauncherPersonalSettings.SLIDEEFFECT_SWEEP,
            LauncherPersonalSettings.SLIDEEFFECT_WORM 
            };

    private static final String TAG = "XPagedView.java";

    private void randomSlideValue() {
        String s = SettingsValue.getWorkspaceSlideValue(getXContext().getContext());

        if (s.equals(LauncherPersonalSettings.SLIDEEFFECT_RANDOM)) {
            int randomN = mRandom.nextInt(SLIDEEFFECT_ARRAY.length);
            s = SLIDEEFFECT_ARRAY[randomN];
            if (!s.equals(this.mPrefSlide)) {
                initPageDrawAdapter(s);
            }
        }
    }

    private void initPageDrawAdapter(String value) {
        this.mPrefSlide = value;
        if (LauncherPersonalSettings.SLIDEEFFECT_SPHERE.equals(value)) {
            if (sphereAdapter == null) {
                sphereAdapter = new SphereDrawAdapter(true, this);
            }
            sphereAdapter.resetSphereOrCylinder(true);
            setPageDrawAdapter(sphereAdapter);
            if (mAnimController != null) {
                mAnimController.setSphereSlide(true);
            }
            // mDurationExtra = 0;
        } else if (LauncherPersonalSettings.SLIDEEFFECT_CYLINDER.equals(value)) {
            if (sphereAdapter == null) {
                sphereAdapter = new SphereDrawAdapter(false, this);
            }
            sphereAdapter.resetSphereOrCylinder(false);
            setPageDrawAdapter(sphereAdapter);
            if (mAnimController != null) {
                mAnimController.setSphereSlide(true);
            }
            // mDurationExtra = 0;
        } else if (LauncherPersonalSettings.SLIDEEFFECT_SWEEP.equals(value)) {
            if (sweepAdapter == null) {
                sweepAdapter = new SweepDrawAdapter();
            }
            setPageDrawAdapter(sweepAdapter);
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                final DrawableItem item = getChildAt(i);
                if (item == null)
                    continue;
                item.setAlpha(1f);
                item.setTouchable(false);
            }
            // mDurationExtra = 500;
        } else if (LauncherPersonalSettings.SLIDEEFFECT_CHARIOT.equals(value)
                || LauncherPersonalSettings.SLIDEEFFECT_WAVE_2.equals(value)
                || LauncherPersonalSettings.SLIDEEFFECT_WILD.equals(value)
                || LauncherPersonalSettings.SLIDEEFFECT_BULLDOZE.equals(value)
                || LauncherPersonalSettings.SLIDEEFFECT_WAVE.equals(value)
                || LauncherPersonalSettings.SLIDEEFFECT_ROTATE.equals(value)
                || LauncherPersonalSettings.SLIDEEFFECT_BOUNCE.equals(value)
                || LauncherPersonalSettings.SLIDEEFFECT_SCALE.equals(value)
                || LauncherPersonalSettings.SLIDEEFFECT_NORMAL.equals(value)
                || LauncherPersonalSettings.SLIDEEFFECT_SNAKE.equals(value)
                || LauncherPersonalSettings.SLIDEEFFECT_CUBE.equals(value)
                || LauncherPersonalSettings.SLIDEEFFECT_WORM.equals(value)) {
            if (normalAdapter == null)
                normalAdapter = new NormalDrawAdapter(this);// (matrix, localRect, mPageCount,
                                                            // isLoop);
            setPageDrawAdapter(normalAdapter);
            for (int i = 0; i < getChildCount(); i++) {
                final DrawableItem item = getChildAt(i);
                if (item == null)
                    continue;
                item.setAlpha(1f);
                item.setTouchable(false);
            }
            // if (LauncherPersonalSettings.SLIDEEFFECT_WORM.equals(this.sPrefAppListSlide)) {
            // mDurationExtra = 500;
            // } else {
            // mDurationExtra = 0;
            // }
        }

//		if (sphereAdapter == null) {
//			sphereAdapter = new SphereDrawAdapter(false, this);
//		}
//		sphereAdapter.resetSphereOrCylinder(false);
//		setPageDrawAdapter(sphereAdapter);
//		if (mAnimController != null) {
//			mAnimController.setSphereSlide(true);
//		}
    }

	/* RK_ID: RK_SLIDEEFFECT. AUT: liuli1 . DATE: 2012-12-11 . END */

    private String mPrefSlide;
	public void updateSlideValue() {
	    if (enableEffect) {
        final String s = SettingsValue.getWorkspaceSlideValue(getXContext().getContext());
        initPageDrawAdapter(s);
        }
	}
	
	public boolean isDefaultSlideMode(){
		return LauncherPersonalSettings.SLIDEEFFECT_NORMAL.equals(mPrefSlide);
	}

	public boolean isPageSlideMode(){
		return !(LauncherPersonalSettings.SLIDEEFFECT_CYLINDER.equals(mPrefSlide) ||
				LauncherPersonalSettings.SLIDEEFFECT_SPHERE.equals(mPrefSlide) ||
				LauncherPersonalSettings.SLIDEEFFECT_SWEEP.equals(mPrefSlide) ||
				LauncherPersonalSettings.SLIDEEFFECT_CHARIOT.equals(mPrefSlide) ||
				LauncherPersonalSettings.SLIDEEFFECT_SNAKE.equals( mPrefSlide ) ||
				LauncherPersonalSettings.SLIDEEFFECT_WORM.equals( mPrefSlide ));
	}

	private SphereDrawAdapter sphereAdapter;
	private NormalDrawAdapter normalAdapter;
	

    void resetOffset() {
        if (mAnimController != null) {
            mAnimController.stopTouchAnim();
            mAnimController.stopOffsetYAnim();
            mAnimController.stopOffsetXAnim();
        }
        mOffsetX = mOffsetXTarget = 0;
        mOffsetY = mOffsetYTarget = 0;
    }
	/* RK_ID: RK_SLIDEEFFECT. AUT: liuli1 . DATE: 2012-12-06 . END */

    private SweepDrawAdapter sweepAdapter;

    private class SweepDrawAdapter implements PageDrawAdapter {

        private static final float animDuration = .55f;
        private static final float rotatePointOffsetX = 50;
        private static final float rotatePointOffsetY = 70;
        int countPrePage;
        float pageWidth, pageHeight;
        private Camera mCamera = new Camera();
        private static final float PI = (float) Math.PI;

        public SweepDrawAdapter() {
            reset();
        }

        @Override
        public void drawPage(IDisplayProcess canvas, int page, float offsetX, float offsetY) {
            if (page > -1 && page < mPageCount) {
                canvas.save();
                final Matrix matrix = getMatrix();
                if (matrix != null && !matrix.isIdentity()) {
                    canvas.concat(matrix);
                }
//kangwei3
//                canvas.clipRect(localRect);
                canvas.translate(localRect.left, localRect.top);
                pageWidth = getWidth();
                pageHeight = getHeight();
                float ds = length((pageWidth - mCellWidth), (pageHeight - mCellHeight));
                float dx;
                int startIndex = page * countPrePage;
                int endIndex = Math.min(startIndex + countPrePage, getChildCount());
                for (int i = startIndex; i < endIndex; i++) {
                    DrawableItem item = getChildAt(i);
                    if (item == null)
                        continue;
                    item.setTouchable(true);
                    Matrix m = item.getMatrix();
                    m.reset();
                    if (offsetX < 0) {
                        dx = length(item.localRect.right, pageHeight - item.localRect.bottom);
                    } else {
                        dx = length(pageWidth + item.localRect.left, item.localRect.top);
                    }
                    float ts = dx / ds * (1f - animDuration);
                    float input = 0f;
                    if (Math.abs(offsetX) > ts) {
                        if (Math.abs(offsetX) - ts <= animDuration) {
                            input = (Math.abs(offsetX) - ts) / animDuration;
                        } else {
                            input = 1f;
                        }
                    } else {
                        input = 0f;
                    }
                    mCamera.save();
                    mCamera.rotateY(-180 * input);
                    final float si = FloatMath.sin(PI * input);
                    mCamera.rotateZ(90 * si * Math.signum(offsetX));
                    mCamera.getMatrix(m);
                    mCamera.restore();
                    float xadjust = rotatePointOffsetX * si;
                    float yadjust = rotatePointOffsetY * si;
                    m.preTranslate(-item.localRect.centerX() + xadjust, -item.localRect.centerY()
                            + yadjust);
                    m.postTranslate(pageWidth + item.localRect.centerX() - xadjust,
                            item.localRect.centerY() - yadjust);
                    item.setAlpha(input < .5f ? 1f - input * 2f : 0);
                    item.updateMatrix(m);
                    item.draw(canvas);
                }

                if (Math.abs(offsetX) > 0.001f) {
                    if (offsetX < 0) {
                        if (page + 1 < mPageCount) {
                            ++page;
                        } else {
                            if (isLoop) {
                                page = 0;
                            } else {
                                canvas.restore();
                                return;
                            }
                        }
                    }
                    if (offsetX > 0) {
                        if (page - 1 > -1) {
                            --page;
                        } else {
                            if (isLoop) {
                                page = mPageCount - 1;
                            } else {
                                canvas.restore();
                                return;
                            }
                        }
                    }
                    startIndex = page * mCellCountX * mCellCountY;
                    endIndex = Math.min(startIndex + mCellCountX * mCellCountY, getChildCount());

                    for (int i = startIndex; i < endIndex; i++) {
                        DrawableItem item = getChildAt(i);
                        if (item == null)
                            continue;
                        item.setTouchable(false);
                        Matrix m = item.getMatrix();
                        m.reset();
                        if (offsetX < 0) {
                            dx = length(item.localRect.right, pageHeight - item.localRect.bottom);
                        } else {
                            dx = length(pageWidth + item.localRect.left, item.localRect.top);
                        }
                        float ts = dx / ds * (1f - animDuration);
                        float input = 0f;
                        if (Math.abs(offsetX) > ts) {
                            if (Math.abs(offsetX) - ts <= animDuration) {
                                input = (Math.abs(offsetX) - ts) / animDuration;
                            } else {
                                input = 1f;
                            }
                        } else {
                            input = 0f;
                        }
                        mCamera.save();
                        mCamera.rotateY(-180 * (1 - input));
                        final float si = FloatMath.sin(PI * input);
                        mCamera.rotateZ(-90 * si * Math.signum(offsetX));
                        mCamera.getMatrix(m);
                        mCamera.restore();
                        float xadjust = rotatePointOffsetX * si;
                        float yadjust = rotatePointOffsetY * si;
                        m.preTranslate(-item.localRect.centerX() + xadjust,
                                -item.localRect.centerY() + yadjust);
                        m.postTranslate(pageWidth + item.localRect.centerX() - xadjust,
                                item.localRect.centerY() - yadjust);
                        item.setAlpha(input > .5f ? input * 2f - 1f : 0);
                        item.updateMatrix(m);
                        item.draw(canvas);
                    }
                }

                canvas.restore();
            }
        }

        /**
         * Returns the euclidian distance from (0,0) to (x,y)
         */
        public float length(float x, float y) {
            return FloatMath.sqrt(x * x + y * y);
        }

        @Override
        public void reset() {
            countPrePage = mCellCountX * mCellCountY;
        }

    }

    protected class PageAnimController implements IController {

		protected boolean offsetXAnimActivate = false;
		private boolean offsetYAnimActivate = false;
		protected boolean pageTouchAnimActivate = false;
		private boolean isSphereSlide = false;
		protected DecelerateInterpolator mInterpolator;
		protected long mOffsetXAnimDuration = 0, mOffsetXAnimPlayTime = 0;
		protected float s_x, s0_x;
		protected long mOffsetYAnimDuration = 0, mOffsetYAnimPlayTime = 0;
		protected float s_y, s0_y;

		public PageAnimController() {
			mInterpolator = new DecelerateInterpolator(1.5f);//(2.5f);
		}

		public void startOffsetXAnim(long durationMillis) {
			offsetXAnimActivate = true;
			mOffsetXAnimDuration = durationMillis;
			mOffsetXAnimPlayTime = 0;
//			android.util.Log.i( "touch", " touch Start offset xanim : " + s0_x + " , " + mOffsetX );
			s0_x = mOffsetX;
			s_x = -mOffsetX;

		}

		public void stopOffsetXAnim() {
			currOrientation = ORI_NONE;
			offsetXAnimActivate = false;
		}

		public boolean isOffsetXAnimStart() {
			return offsetXAnimActivate;
		}

		public void startOffsetYAnim(long durationMillis) {
			offsetYAnimActivate = true;
			mOffsetYAnimDuration = durationMillis;
			mOffsetYAnimPlayTime = 0;
			s0_y = mOffsetY;
			s_y = -mOffsetY;

		}

		public void stopOffsetYAnim() {
			offsetYAnimActivate = false;
		}

		public boolean isOffsetYAnimStart() {
			return offsetYAnimActivate;
		}

		public void startTouchAnim() {
			if (mDebug)R5.echo("startTouchAnim");
			pageTouchAnimActivate = true;
		}

		public void stopTouchAnim() {
			if (mDebug)R5.echo("stopTouchAnim");
			pageTouchAnimActivate = false;
		}

		public void setSphereSlide(boolean isSphereSlide) {
			this.isSphereSlide = isSphereSlide;
		}

		@Override
		public void update(long timeDelta) {
			
//			D2.echo("Enter update !  ...");

			if (offsetXAnimActivate) {
				mOffsetXAnimPlayTime += timeDelta;
				float step = s_x
						* mInterpolator.getInterpolation((float) mOffsetXAnimPlayTime
								/ (float) mOffsetXAnimDuration);
//				android.util.Log.i( "R5", "AND moffsetX : " + s0_x + " , " + step );
				mOffsetX = s0_x + step;
				if (mDebug)R5.echo("update offsetXAnimActivate mOffsetX = " + mOffsetX + "         mCurentPage = " + mCurrentPage);
				if (Math.abs(mOffsetX) <  IGNORE_OFFSET
						|| mOffsetXAnimPlayTime >= mOffsetXAnimDuration) {
					mOffsetX = 0f;
					mOffsetXTarget = 0f;
					stopOffsetXAnim();
					isPageMoving = false;
					onPageEndMoving();
				} else {
					mOffsetXTarget = mOffsetX;
				}
				
				invalidate();
			}
			if (offsetYAnimActivate) {
				mOffsetYAnimPlayTime += timeDelta;
				if (Math.abs(mOffsetY) <  IGNORE_OFFSET
						|| mOffsetYAnimPlayTime >= mOffsetYAnimDuration) {
					mOffsetY = 0f;
					mOffsetYTarget = 0f;
					stopOffsetYAnim();

				} else {
					float step = s_y
							* mInterpolator.getInterpolation((float) mOffsetYAnimPlayTime
									/ (float) mOffsetYAnimDuration);
					mOffsetY = s0_y + step;
					mOffsetYTarget = mOffsetY;
				}
				
				invalidate();
			}
			if (pageTouchAnimActivate) {
				float delta = mOffsetXTarget - mOffsetX;
				if (mIndicatorScroll)
				{
	                mOffsetX = mOffsetXTarget;
	                mOffsetY = mOffsetYTarget;
				}
				else
				{
				    mOffsetX += delta * SCROLL_X_FACTOR;
				    mOffsetY += (mOffsetYTarget - mOffsetY) * SCROLL_Y_FACTOR;
				}

				// if (Math.abs(delta) < 0.002f) {
				// mOffsetXTarget = mOffsetX;
				// stopTouchAnim();
				// }
//				invalidate();
				checkOffsetToNormal();
				currOrientation = (int) Math.signum(delta);
				if (mDebug)R5.echo("update pageTouchAnimActivate currOrientation = " + currOrientation + "mOffsetX = " + mOffsetX + "         mCurentPage = " + mCurrentPage);
                if (!isScrollBack(mOffsetX)) {
                    stopTouchAnim();
                    mOffsetX = 0f;
                    mOffsetXTarget = 0f;
                    stopOffsetXAnim();
                    isPageMoving = false;
                    onPageEndMoving();
                }
			}
			if (isSphereSlide) {
				if (pageTouchAnimActivate /* && Math.abs(mOffsetX) < 0.4f */) {
					if (Math.abs(mOffsetX) < 0.4f) {
						float delta = rect2ballTarget - rect2ball;
						if (Math.abs(delta) < 0.002f) {
							rect2ball = rect2ballTarget;
							return;
						}
						float temp = (Math.abs(mOffsetX)) / 0.4f * (rect2ballTarget - rect2ball)
								+ rect2ball;
						rect2ball += delta * .2f;
						rect2ball = rect2ball > temp ? rect2ball : temp;
					} else {
						float delta = rect2ballTarget - rect2ball;
						if (Math.abs(delta) < 0.002f) {
							rect2ball = rect2ballTarget;
							return;
						}
						rect2ball += delta * .6f;
					}
				} else {
					float absX = Math.abs(mOffsetX);
					if (absX < 0.3f) {
						rect2ball = absX / 0.3f;
					} else {
						rect2ball = 1f;
					}
				}
			}
		}
	}
	
	public float getRect2BallRateOrTarget( boolean isTarget ){
		return isTarget ? rect2ballTarget : rect2ball;
	}
	
	private boolean enableScrollBack = false;
	
	public void setScrollBackEnable(boolean enable) {
	    enableScrollBack = enable;
	}

    private boolean isScrollBack(float offsetx) {
        if (enableScrollBack || isLoop) {
            return true;
        }

        int nextPage = -1;
        if (mOffsetX > 0 && mOffsetX < 1f) {
            nextPage = mCurrentPage - 1;
        } else if (mOffsetX > -1f && mOffsetX < 0) {
            nextPage = mCurrentPage + 1;
        }
        if (nextPage >= 0 && nextPage < getPageCount()) {
            return true;
        }
        return false;
    }

    public void startEditMode(final boolean delete) {
		// List<DrawableItem> childrens = getChildren();
		mEditMode = true;
		mVibrator.vibrate(VIBRATE_DURATION);

		if (getChildCount() > 0) {
			// int size = childrens.size();
			for (int i = 0; i < getChildCount(); i++) {
				DrawableItem item = getChildAt(i);

				// if (item instanceof XIconView) {
				// XIconView iconView = (XIconView) item;
				// iconView.startEditMode(delete);
				// }
			}
		}
	}

	public void stopEditMode() {
		// List<DrawableItem> childrens = getChildren();
		mEditMode = false;
		updateSlideValue();

		// sync sort and stop edit mode
		//ArrayList<ApplicationInfo> tmpInfos = new ArrayList<ApplicationInfo>();
		if (getChildCount() > 0) {
			// int size = childrens.size();
			for (int i = 0; i < getChildCount(); i++) {
				DrawableItem item = getChildAt(i);

				// if (item instanceof XIconView) {
				//
				// XIconView iconView = (XIconView) item;
				// // R2 -- order
				// tmpInfos.add(iconView.getLocalInfo());
				// // 2R
				// iconView.stopEditMode();
				// }
			}
		}

		invalidate();

	}

	public boolean isEditMode() {
		return mEditMode;
	}

//	public boolean isDraggingMode() {
//		return mDraggingMode;
//	}

//	private boolean mDraggingMode = false;
//	private boolean mLongPressed = false;

//	public boolean onLongPress(MotionEvent e) {
//		boolean res = super.onLongPress(e);
//		mLongPressed = true;
//		return res;
//	}
	
	

	RectF extraTouchRegionForPagedView;
	
	protected void onPageBeginMoving() {
		
//		android.util.Log.i( "wow", "on Page Begin Moving . " );
		
		if (pageSwitchers != null)
			for (PageSwitchListener pl : pageSwitchers) {
				pl.onPageBeginMoving(mCurrentPage);
			}
		if (enableEffect) randomSlideValue();
	}

	protected void onPageEndMoving() {
	    if (mDebug)R5.echo("onPageEndMoving");
		if (pageSwitchers != null)
			for (PageSwitchListener pl : pageSwitchers) {
				pl.onPageEndMoving(mCurrentPage);
			}

//		if (isEditMode()) {
		mPageDrawAdapter = null;
		updateSlideValue();
		updateAllItemsTouchable();
//		}
		
	}

	public void scrollToLeft(long duration) {
		initPageDrawAdapter(LauncherPersonalSettings.SLIDEEFFECT_NORMAL);

		if (mAnimController == null) {
			return;
		}
		mOffsetX = -1;
		
		final int oldPage = mCurrentPage;
				
		if (mCurrentPage - 1 < 0) {
			mCurrentPage = mPageCount - 1;
		} else {
			mCurrentPage--;
		}

		if (pageSwitchers != null)
			for (PageSwitchListener pl : pageSwitchers) {
				pl.onPageSwitching(oldPage, mCurrentPage, 0);
			}

		if (!isPageMoving) {
			isPageMoving = true;
			if (pageSwitchers != null) {
				for (PageSwitchListener pl : pageSwitchers) {
					pl.onPageBeginMoving(mCurrentPage);
				}
			}
		}
		mAnimController.startOffsetXAnim(duration);
	}

	public void scrollToRight(long duration) {
		initPageDrawAdapter(LauncherPersonalSettings.SLIDEEFFECT_NORMAL);

		if (mAnimController == null) {
			return;
		}
		mOffsetX = 1;

		final int oldScreen = mCurrentPage;
		if (mCurrentPage == mPageCount - 1) {
			mCurrentPage = 0;
		} else {
			mCurrentPage++;
		}

		if (pageSwitchers != null) {
			for (PageSwitchListener pl : pageSwitchers) {
				pl.onPageSwitching(oldScreen, mCurrentPage, 0);
			}
		}

        if (!isPageMoving) {
            isPageMoving = true;
            if (pageSwitchers != null) {
                for (PageSwitchListener pl : pageSwitchers) {
                    pl.onPageBeginMoving(mCurrentPage);
                }
            }
        }
        mAnimController.startOffsetXAnim(duration);
	}

	public int getPageViewItemCount() {
	    return mItemIDMap.size();
	}

	public int getPageCount() {
		return mPageCount;
	}

	public void setCurrentPage(int currentPage) {  
		if (currentPage >= 0 && currentPage < getPageCount()) {
		    int lastPage = mCurrentPage;
			mCurrentPage = currentPage;
			updateAllItemsTouchable();
			
			if (pageSwitchers != null) {
				for (PageSwitchListener pl : pageSwitchers) {
					pl.onPageSwitching(lastPage, mCurrentPage, 1);
					try{
						pl.onPageEndMoving( mCurrentPage );
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
			
			invalidate();
		}
	}

	private void updateAllItemsTouchable() {
		final int countPrePage = mCellCountX * mCellCountY;
		final int startIndex = mCurrentPage * countPrePage;
		final int endIndex = Math.min(startIndex + countPrePage, getChildCount());
		for (int i = 0; i < getChildCount(); i++) {
			XCell item = (XCell) getChildAt(i);
			if (item == null)
				continue;
			if (i >= startIndex && i < endIndex) {
				item.setTouchable(true);
			} else {
				item.setTouchable(false);
			}
		}
	}

	public void setIconTextBackgroundEnable(boolean enable) {
//		for (int i = 0; i < getChildCount(); i++) {
//			final DrawableItem item = getChildAt(i);
//			if (item instanceof XCell) {
//				// ((XCell) item).setBackgroundEnable(enable);
//			}
//		}
	    LauncherApplication la = (LauncherApplication) getXContext().getContext().getApplicationContext();
	    refreshIconCache(la.getIconCache(), false);
	}

	@Override
	public boolean addItem(DrawableItem item) {
		if (item == null || !(item instanceof XCell)) {
			throw new RuntimeException("Only XCell child supported!");
		}
		
		item.setRelativeX( (getChildCount()% mCellCountX ) * mCellWidth + (_gapEnable ? _gapBetweenPage / 2 : 0f));
		item.setRelativeY( ((getChildCount()%(mCellCountX * mCellCountY)) / mCellCountX) * mCellHeight);

		item.setRelativeX(item.getRelativeX() - localRect.width());
		boolean res = super.addItem(item);
		if (res) {
			updatePageCount();
		}
		return res;
	}

	@Override
	public boolean addItem(DrawableItem item, int index) {

		if (item == null || !(item instanceof XCell)) {
			throw new RuntimeException("Only XCell child supported!");
		}
		
		item.setRelativeX( (index % mCellCountX ) * mCellWidth + (_gapEnable ? _gapBetweenPage / 2 : 0f));
		item.setRelativeY( ((index%(mCellCountX * mCellCountY)) / mCellCountX) * mCellHeight);

		item.setRelativeX(item.getRelativeX() - localRect.width());
		boolean res = super.addItem(item, index);
		if (res) {
			updatePageCount();
		}
		return res;
	}

	public boolean addPagedViewItem(XPagedViewItem itemToAdd) {
		
		final ItemInfo info = itemToAdd.getInfo();

		if (info == null) {
			return false;
		}

		// FIXME : Debug
		if(DEBUG_ITEM)
			android.util.Log.i("page", "info is : screen " + info.screen + "cellX : " + info.cellX
				+ " cellY : " + info.cellY);
		// FIXME : Debug
		
		// generate cells
		itemToAdd.onAttach(this, mCellWidth, mCellHeight);

		final XCell[] cells = itemToAdd.getCells();
		final int cellCount = cells.length;

		final int spanX = info.spanX;
		
		final int spanY = info.spanY;
		final int cellBeginIndex = getCellIndex(info.screen, info.cellX, info.cellY);
		final int[] vacants = new int[cellCount];
		int count = 0;
		if(DEBUG_ITEM){
			android.util.Log.i("xpage", "cellBeginIndex is : " + cellBeginIndex);
			android.util.Log.i("xpage", "now vacants is : ");
		}
		for (int j = 1; j <= spanY; j++) {
			final int slotPosition = (j - 1) * mCellCountX + cellBeginIndex;
			if(DEBUG_ITEM){
				android.util.Log.i("xpage", "slotPosition is : " + slotPosition);
			}
			for (int i = 1; i <= spanX; i++) {
				// if (getChildAt(slotPosition) != null
				// || ((XCell) getChildAt(slotPosition)).getContainerId() !=
				// XCell.CONTAINER_NO_ID) {
				// return false;
				// }
             //change by xingqx 
				if(count < vacants.length) {
				    vacants[count] = slotPosition + i - 1;
				}				
				count++;
			}
		}

		if (DEBUG_ITEM) {
			// debug
			int m = 0;
			for (Integer value : vacants) {
				android.util.Log.i("xpage", (m++) + " : " + value);
			}
			android.util.Log.i("xpage", " ================== ");
		}
		boolean res = true;

		final long newId = generatePagedViewItemId();
		itemToAdd.setId(newId);
		mItemIDMap.put(newId, itemToAdd);

		++mPagedViewItemCount;

		for (int i = 0; i < vacants.length; i++) {
			removeItem(vacants[i]);
			cells[i].screen = info.screen;
			res &= addItem(cells[i], vacants[i]);
			cells[i].setContainerId(itemToAdd.getId());
		}

		info.attachedIndexArray = vacants;
		/*** RK_ID: Occupied.  AUT: zhaoxy . DATE: 2013-01-30 . START***/
		markCellsForView(info.cellX, info.cellY, info.spanX, info.spanY, info.screen, true);
		/*** RK_ID: Occupied.  AUT: zhaoxy . DATE: 2013-01-30 . END***/

		if (itemToAdd.getDrawingTarget() instanceof XViewContainer)
		{
			addPageSwitchListener(itemToAdd);
		}
		
		updatePageCount();
		
		mHasChild[info.screen] = true;
		if (itemToAdd.getDrawingTarget() instanceof XViewContainer && info.screen < mHasWidget.length)
		{
			mHasWidget[info.screen] = true;
		}
				
		refreshBitmapCache(info.screen);

		return res;
	}

	public long generatePagedViewItemId() {

		return ++mPagedViewItemId;
	}

	public int getCellIndex(int screen, int cellX, int cellY) {
		return mCellCountX * mCellCountY * screen + cellY * mCellCountX + cellX;
	}

	@Override
	public DrawableItem getChildAt(int index) {
		DrawableItem item = super.getChildAt(index);
		if (item == null) {
			return null;
		} else {
			return item;
		}
	}
	
    public DrawableItem getChildAt(int screen, int cellX, int cellY) {
        int index = getCellIndex(screen, cellX, cellY);
        return getChildAt(index);
    }
    
    public DrawableItem getChildAt(int cellX, int cellY) {
        int index = getCellIndex(mCurrentPage, cellX, cellY);
        return getChildAt(index);
    }

	public boolean removePagedViewItem(XPagedViewItem itemToRemove) {
		return removePagedViewItem(itemToRemove, false, false);
	}
	
	public boolean removePagedViewItem(XPagedViewItem itemToRemove, boolean audoAdjust, boolean animate) {
	    return removePagedViewItem(itemToRemove, audoAdjust, true, animate);
	}

	public boolean removePagedViewItem(XPagedViewItem itemToRemove, boolean audoAdjust, boolean destory, boolean animate) {
		if (itemToRemove == null) {
			return true;
		}

		final DrawableItem paraDrawingItem = itemToRemove.getDrawingTarget();
		if (paraDrawingItem instanceof XViewContainer) {
			
			if( _configurationChange ){
				((XViewContainer) paraDrawingItem).getParasiteView().setTranslationY( XViewContainer.GONE );
			}
			
			getXContext().post(new Runnable() {
				@Override
				public void run() {
					mStage.removeView(((XViewContainer) paraDrawingItem)
							.getParasiteView());
					mStage.invalidate();
					mStage.requestLayout();
				}
			});
			//add by zhanggx1 for memory.s
			((XViewContainer) paraDrawingItem).unregisterReceiver();
			//add by zhanggx1 for memory.e
		}

		final ItemInfo info = itemToRemove.getInfo();

		if (info == null) {
			return false;
		}

		final int[] cellIndexArray = info.attachedIndexArray;
		
		int[] fromInfo = new int[3];        
	                        
		for (int i = 0; i < cellIndexArray.length; i++) {
			final int position = cellIndexArray[i];
//             XCell oldCell = (XCell)getChildAt( position );
//             DrawableItem item = oldCell.getDrawingTarget();
//             if(item != null){
//                     item.clean();
//                     oldCell.setDrawingTarget(null);
//             }

			XCell oldCell = (XCell)getChildAt( position );
			if(oldCell != null && itemToRemove.getId() == oldCell.getContainerId())
			{
    			final XCell cell = new XCell(getXContext(), null, null);

    			removeItem(position, destory);
//    			mOccupied[info.screen][info.cellX][info.cellY] = false;
    			getInfoFromIndex(cellIndexArray[i], fromInfo);
    			markCellsForView(fromInfo[1], fromInfo[2], 1, 1, fromInfo[0], false);
    			addItem(cell, position);
			}
			else
			{
			    R5.echo("don't remove");
			}
			updatePageCount();
		}
		
		//todo
//		markCellsForView(info.cellX, info.cellY, info.spanX, info.spanY, info.screen, false);
		
		// handle not fill
		if (audoAdjust) {
			if (cellIndexArray.length > 1) {
				throw new RuntimeException("Only single .. supported now.");
			}
			
			refreshPageItems(cellIndexArray[0] + 1, animate);
		}

		removePageSwitchListener(itemToRemove);
		mItemIDMap.remove(itemToRemove.getId());
		boolean checkWidget = false;
		if (itemToRemove.getDrawingTarget() instanceof XViewContainer)
		{
			mHasWidget[info.screen] = false;
			checkWidget = true;
		}
		
		mHasChild[info.screen] = false;
		
		Set<Map.Entry<Long, XPagedViewItem>> set = mItemIDMap.entrySet();
		XPagedViewItem item;
        ItemInfo tempInfo;
        for (Map.Entry<Long, XPagedViewItem> map : set) {
            item = map.getValue();
            tempInfo = item.getInfo();
            if (tempInfo.screen == info.screen )
            {
            	mHasChild[info.screen] = true;
            	if (!checkWidget)
            	{
            		break;
            	}
            	
            	if (item.getDrawingTarget() instanceof XViewContainer)
            	{
	            	mHasWidget[info.screen] = true;
	            	break;
            	}
            }	            
        }
				
        refreshBitmapCache(info.screen);

//		android.util.Log.i("xpage", "removed item : " + itemToRemove);

		return true;
	}
	
	public void refreshPageItems(int from, boolean animate){
	    for( int i = from; i< getChildCount(); i ++ ){
            final XCell cell = (XCell)getChildAt(i);
            final XPagedViewItem item = mItemIDMap.get(cell.getContainerId());
			if (item != null) {
				// move item to new position
				final int[] res = new int[3];
				getInfoFromIndex(i - 1, res);
				moveItemToPosition(item, res[1], res[2], res[0], 300, i * 10,
						null, null, animate);
			}
        }
	    
	}		

	public int[] getRelativeXY(int cellX, int cellY, int[] loc) {
	    if (loc == null) {
	        loc = new int[2];
        }
	    loc[0] = (int) (cellX * mCellWidth - getWidth() +  (int)(_gapEnable ? _gapBetweenPage / 2 : 0f));
	    loc[1] = cellY * mCellHeight;
	    return loc;
	}

	public boolean moveItemToPosition(final XPagedViewItem itemToMove, final int cellX,
            final int cellY, final int screen, int duration, int timeToDelay, boolean[][] occupied){
        return moveItemToPosition(itemToMove, cellX, cellY, screen, duration,
                timeToDelay, null, occupied, true);
    }

	public boolean moveItemToPosition(final XPagedViewItem itemToMove, final int cellX,
            final int cellY, final int screen, int duration, int timeToDelay, boolean[][] occupied, boolean animate){
		return moveItemToPosition(itemToMove, cellX, cellY, screen, duration,
				timeToDelay, null, occupied, animate);
	}
	
	public boolean moveItemToPosition(final XPagedViewItem itemToMove, final int cellX,
            final int cellY, final int screen, int duration, int timeToDelay,
            final Runnable taskToSchedule, boolean[][] occupied, boolean animate){
		 if( itemToMove != null ){
			 
			 //FIXME
			 Runnable r = null;
			 final DrawableItem drawingTarget = itemToMove.getDrawingTarget();
			 if(drawingTarget instanceof XViewContainer){
				 r = new Runnable() {
					 @Override
					 public void run() {
					     R5.echo("XViewContainer manageVisibility");
					     if(itemToMove.getInfo().screen == getCurrentPage()){
					    	 ((XViewContainer) drawingTarget).manageVisibility(XViewContainer.VISIBILITY_SHOW_VIEW, null);
						 }
						 if (taskToSchedule != null)
						 {
						     taskToSchedule.run();
						 }
					 }
				 };
				//add by zhanggx1 for removing on 2013-11-13 . s
			 } else {
				 r = new Runnable() {
					 @Override
					 public void run() {
						 if (taskToSchedule != null) {
						     taskToSchedule.run();
						 }
					 }
				 };
			 }
			 itemToMove.moveToTargetPosition(screen, cellX, cellY, duration, timeToDelay, r, occupied, animate);
			 ItemInfo info = itemToMove.getInfo();
			 markCellsAsUnoccupiedForView(info.screen, info);
			 markCellsForView(cellX, cellY, info.spanX, info.spanY, screen, true);
			 return true;
		 }
		 return false;
	}

    public XPagedViewItem findPageItemAt(int cellX, int cellY) {
        return findPageItemAt(mCurrentPage, cellX, cellY);        
    }

    public XPagedViewItem findPageItemAt(int screen, int cellX, int cellY) {
		return findPageItemAt(getCellIndex(screen, cellX, cellY));
	}
    
    public XPagedViewItem findPageItemAt(int index) {
        final DrawableItem child = getChildAt(index);
        if (child == null) {
            return null;
        }

        final XCell cell = (XCell) child;
        final XPagedViewItem container = mItemIDMap.get(cell.getContainerId());

        if (container == null) {
            return null;
        } else {
            return (XPagedViewItem) container;
        }
    }

    public boolean removePageItem(ItemInfo info) {
        return removePageItem(info, false);
    }

    public boolean removePageItem(ItemInfo info, boolean autoAdjust) {
        return removePageItem(info, autoAdjust, true);
    }

    public boolean removePageItem(ItemInfo info, boolean autoAdjust, boolean destory) {
        XPagedViewItem itemToRemove = findPageItemAt(info.screen, info.cellX, info.cellY);
        return removePagedViewItem(itemToRemove, autoAdjust, destory);
    }

    public void refreshIconCache(IconCache iconCache, boolean bitmap) {
        int pageCnt = getPageCount();
        int cellXCnt = getCellCountX();
        int cellYCnt = getCellCountY();

        for (int screen = 0; screen < pageCnt; screen++) {
            for (int cellX = 0; cellX < cellXCnt; cellX++) {
                for (int cellY = 0; cellY < cellYCnt; cellY++) {
                    XPagedViewItem item = findPageItemAt(screen, cellX, cellY);
                    if (item == null) {
                        continue;
                    }
                    DrawableItem drawableTarget = item.getDrawingTarget();
                    if (drawableTarget == null
                            || !(drawableTarget instanceof XShortcutIconView || drawableTarget instanceof XFolderIcon)) {
                        continue;
                    }
                    ItemInfo itemInfo = item.getInfo();
                    if (itemInfo == null) {
                        continue;
                    }

                    Utilities.refreshItem(getXContext().getContext(), drawableTarget, itemInfo,
                            iconCache, bitmap);
                } // end cellY
            } // end cellX
        } // end screen
    }
   public void removeItems(HashSet<ComponentName> componentNames, ArrayList<XPagedViewItem> childrenToRemove,boolean byClassname) {
        int pageCnt = getPageCount();
        int cellXCnt = getCellCountX();
        int cellYCnt = getCellCountY();

        for (int screen = 0; screen < pageCnt; screen++) {
            for (int cellX = 0; cellX < cellXCnt; cellX++) {
                for (int cellY = 0; cellY < cellYCnt; cellY++) {
                    XPagedViewItem item = findPageItemAt(screen, cellX, cellY);
                    if (item == null) {
                        continue;
                    }
                    DrawableItem drawableTarget = item.getDrawingTarget();
                    if (drawableTarget == null) {
                        continue;
                    }
                    Object itemInfo = drawableTarget.getTag();
                    if (itemInfo == null) {
                        continue;
                    }

                    checkAndAddItem(item, itemInfo, componentNames, childrenToRemove,byClassname);

                } // end cellY
            } // end cellX
        } // end screen
    }
	 private void checkAndAddItem(XPagedViewItem view, Object tag, HashSet<ComponentName> ComponentNames,
	            ArrayList<XPagedViewItem> childrenToRemove,boolean byClassname) {
		 if(byClassname){
			 checkAndAddItemByClassname(view,tag,ComponentNames,childrenToRemove);
		 }else{
			 HashSet<String> packages = new HashSet<String>();
             for (ComponentName componentName : ComponentNames) {
           	  packages.add(componentName.getPackageName());
             }
			 checkAndAddItem(view,tag,packages,childrenToRemove);
		 }
	 
	 }
    private void checkAndAddItemByClassname(XPagedViewItem view, Object tag,
			HashSet<ComponentName> componentNames,
			ArrayList<XPagedViewItem> childrenToRemove) {
		// TODO Auto-generated method stub
    	final Context xlauncher = getXContext().getContext();
//      final AppWidgetManager widgets = AppWidgetManager.getInstance(xlauncher);

      if (tag instanceof ShortcutInfo) {
          final ShortcutInfo info = (ShortcutInfo) tag;
          final Intent intent = info.intent;
          final ComponentName name = intent.getComponent();
          if (Intent.ACTION_MAIN.equals(intent.getAction()) && name != null) {
        	  if(info.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT){
        		  HashSet<String> packages = new HashSet<String>();
                  for (ComponentName componentName : componentNames) {
                	  packages.add(componentName.getPackageName());
                  }
        		  for (String packageName : packages) {
                      if (packageName.equals(name.getPackageName())) {
                          XLauncherModel.deleteItemFromDatabase(xlauncher, info);
                          childrenToRemove.add(view);
                      }
                  }
        	  }else if(info.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION){
        		  for (ComponentName componentName : componentNames) {
                      if (componentName.equals(name)) {
                          XLauncherModel.deleteItemFromDatabase(xlauncher, info);
                          childrenToRemove.add(view);
                      }
                  }
        	  }
              
          }
          /* RK_ID: RK_QUICKACTION. AUT: liuli1 . DATE: 2012-05-08 . START */
          // fix bug 164421
          // we need delete it if action is Intent.ACTION_VIEW,
          // because if action is null, when added to workspace it was set to
          // this value.
          else if (Intent.ACTION_VIEW.equals(intent.getAction())
                  && info.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT
                  && name != null) {
              Log.i(TAG, "this shortcut info's action is Intent.ACTION_VIEW, ComponentName = "
                      + name);
              HashSet<String> packages = new HashSet<String>();
              for (ComponentName componentName : componentNames) {
            	  packages.add(componentName.getPackageName());
              }
              for (String packageName : packages) {
                  if (packageName.equals(name.getPackageName())) {
                      XLauncherModel.deleteItemFromDatabase(xlauncher, info);
                      childrenToRemove.add(view);
                  }
              }
          }
          /* RK_ID: RK_QUICKACTION. AUT: liuli1 . DATE: 2012-05-08 . END */
      } else if (tag instanceof FolderInfo) {
          final FolderInfo info = (FolderInfo) tag;
          final ArrayList<ShortcutInfo> contents = info.contents;
          final int contentsCount = contents.size();
          final ArrayList<ShortcutInfo> appsToRemoveFromFolder = new ArrayList<ShortcutInfo>();

          for (int k = 0; k < contentsCount; k++) {
              final ShortcutInfo appInfo = contents.get(k);
              final Intent intent = appInfo.intent;
              final ComponentName name = intent.getComponent();
              if (/*Intent.ACTION_MAIN.equals(intent.getAction()) && */name != null) {

            	  if(appInfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT){
            		  HashSet<String> packages = new HashSet<String>();
                      for (ComponentName componentName : componentNames) {
                    	  packages.add(componentName.getPackageName());
                      }
                      for (String packageName : packages) {
                          if (packageName.equals(name.getPackageName())) {
                              appsToRemoveFromFolder.add(appInfo);
                          }
                      }
            	  }else if(appInfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION){
            		  for (ComponentName componentName : componentNames) {
                          if (componentName.equals(name)) {
                              appsToRemoveFromFolder.add(appInfo);
                          }
                      }
            	  }
              }
          }

          if (appsToRemoveFromFolder.size() == contentsCount) {
              XLauncherModel.deleteItemFromDatabase(xlauncher, info);
              childrenToRemove.add(view);
          }
          
          for (ShortcutInfo item : appsToRemoveFromFolder) {
              info.remove(item);
              XLauncherModel.deleteItemFromDatabase(xlauncher, item);
          }
          /*** fixbug 165340 . AUT: zhaoxy . DATE: 2012-05-28. END***/
      } else if (tag instanceof LauncherAppWidgetInfo) {
          final LauncherAppWidgetInfo info = (LauncherAppWidgetInfo) tag;
          final AppWidgetProviderInfo provider = info.hostView == null ? null : info.hostView.getAppWidgetInfo();
          if (provider != null) {
        	  HashSet<String> packages = new HashSet<String>();
              for (ComponentName componentName : componentNames) {
            	  packages.add(componentName.getPackageName());
              }
              for (String packageName : packages) {
                  if (packageName.equals(provider.provider.getPackageName())) {
                      XLauncherModel.deleteItemFromDatabase(xlauncher, info);
                      childrenToRemove.add(view);
                  }
              }
          }
      }

      /* RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-15 START */
      else if (tag instanceof LenovoWidgetViewInfo) {
          final LenovoWidgetViewInfo info = (LenovoWidgetViewInfo) tag;
          HashSet<String> packages = new HashSet<String>();
          for (ComponentName componentName : componentNames) {
        	  packages.add(componentName.getPackageName());
          }
          for (String packageName : packages) {
              if (packageName.equals(info.getPackageName())) {
                  XLauncherModel.deleteItemFromDatabase(xlauncher, info);
                  XLauncherModel.sLeosWidgets.remove(info);
                  childrenToRemove.add(view);
              }
          }
      }
      /* RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-15 end */
  }

	private void checkAndAddItem(XPagedViewItem view, Object tag, HashSet<String> packages,
            ArrayList<XPagedViewItem> childrenToRemove) {
        final Context xlauncher = getXContext().getContext();
//        final AppWidgetManager widgets = AppWidgetManager.getInstance(xlauncher);

        if (tag instanceof ShortcutInfo) {
            final ShortcutInfo info = (ShortcutInfo) tag;
            final Intent intent = info.intent;
            final ComponentName name = intent.getComponent();

            if (Intent.ACTION_MAIN.equals(intent.getAction()) && name != null) {
                for (String packageName : packages) {
                    if (packageName.equals(name.getPackageName())) {
                        XLauncherModel.deleteItemFromDatabase(xlauncher, info);
                        childrenToRemove.add(view);
                    }
                }
            }
            /* RK_ID: RK_QUICKACTION. AUT: liuli1 . DATE: 2012-05-08 . START */
            // fix bug 164421
            // we need delete it if action is Intent.ACTION_VIEW,
            // because if action is null, when added to workspace it was set to
            // this value.
            else if (Intent.ACTION_VIEW.equals(intent.getAction())
                    && info.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT
                    && name != null) {
                Log.i(TAG, "this shortcut info's action is Intent.ACTION_VIEW, ComponentName = "
                        + name);
                for (String packageName : packages) {
                    if (packageName.equals(name.getPackageName())) {
                        XLauncherModel.deleteItemFromDatabase(xlauncher, info);
                        childrenToRemove.add(view);
                    }
                }
            }
            /* RK_ID: RK_QUICKACTION. AUT: liuli1 . DATE: 2012-05-08 . END */
        } else if (tag instanceof FolderInfo) {
            final FolderInfo info = (FolderInfo) tag;
            final ArrayList<ShortcutInfo> contents = info.contents;
            final int contentsCount = contents.size();
            final ArrayList<ShortcutInfo> appsToRemoveFromFolder = new ArrayList<ShortcutInfo>();

            for (int k = 0; k < contentsCount; k++) {
                final ShortcutInfo appInfo = contents.get(k);
                final Intent intent = appInfo.intent;
                final ComponentName name = intent.getComponent();

                if (/*Intent.ACTION_MAIN.equals(intent.getAction()) && */name != null) {
                    for (String packageName : packages) {
                        if (packageName.equals(name.getPackageName())) {
                            appsToRemoveFromFolder.add(appInfo);
                        }
                    }
                }
            }

            if (appsToRemoveFromFolder.size() == contentsCount) {
                XLauncherModel.deleteItemFromDatabase(xlauncher, info);
                childrenToRemove.add(view);
            }

            for (ShortcutInfo item : appsToRemoveFromFolder) {
                info.remove(item);
                XLauncherModel.deleteItemFromDatabase(xlauncher, item);
            }
            /*** fixbug 165340 . AUT: zhaoxy . DATE: 2012-05-28. END***/
        } else if (tag instanceof LauncherAppWidgetInfo) {
            final LauncherAppWidgetInfo info = (LauncherAppWidgetInfo) tag;
            final AppWidgetProviderInfo provider = info.hostView == null ? null : info.hostView.getAppWidgetInfo();
            if (provider != null) {
                for (String packageName : packages) {
                    if (packageName.equals(provider.provider.getPackageName())) {
                        XLauncherModel.deleteItemFromDatabase(xlauncher, info);
                        childrenToRemove.add(view);
                    }
                }
            }
        }

        /* RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-15 START */
        else if (tag instanceof LenovoWidgetViewInfo) {
            final LenovoWidgetViewInfo info = (LenovoWidgetViewInfo) tag;
            for (String packageName : packages) {
                if (packageName.equals(info.getPackageName())) {
                    XLauncherModel.deleteItemFromDatabase(xlauncher, info);
                    XLauncherModel.sLeosWidgets.remove(info);
                    childrenToRemove.add(view);
                }
            }
        }
        /* RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-15 end */
    }

    void setChildrenAlpha(int index, float alpha) {
        final DrawableItem child = getChildAt(index);
        if (child == null) {
            return;
        }

        final XCell cell = (XCell) child;
        final XPagedViewItem container = mItemIDMap.get(cell.getContainerId());

        if (container == null) {
            return;
        } else {
            child.setAlpha(alpha);
        }
    }

    public void dumpChildren() {
		if (getChildCount() != -1) {
			for (int i = 0; i < getChildCount(); i++) {
				try {
					XCell cell = (XCell) getChildAt(i);
					android.util.Log.i("xpage",
							i + "  : " + cell.getDrawingTarget());
					android.util.Log.i("xpage", "cell is : " + cell.getContainerId());
				} catch (Exception e) {
					android.util.Log.i("xpage", i + "  : " + "NONE.");
				}
			}
		}
	}

	public int getCellCountX() {
		return mCellCountX;
	}

	public int getCellCountY(){
		return mCellCountY;
	}
	
	public int getCellWidth(){
		return mCellWidth;
	}
	
	public int getCellHeight(){
		return mCellHeight;
	}
	
	public int getWidthGap() {
		return mWidthGap;
	}
	
	public int getHeightGap() {
		return mHeightGap;
	}
	
	public void setWidthGap(int widthGap) {
		mWidthGap = widthGap;
		
	}
	
	public void setHeightGap(int heightGap) {
		mHeightGap = heightGap;
	}

    public int getCurrentPage() {
        return mCurrentPage;
    }

    public int getVPadding() {
        return mPaddingTop + mPaddingBottom;
    }

    public String getSlideValue() {
        return mPrefSlide;
    }

	/*** RK_ID: XFOLDER.  AUT: zhaoxy . DATE: 2013-01-22 . START***/
    /**
     * @return get a list copy of DrawingTarget in all of the PagedViewItems.
     */
    public ArrayList<DrawableItem> getSourceItems(ArrayList<DrawableItem> store) {
        if (store == null) {
            store = new ArrayList<DrawableItem>();
        } else {
            store.clear();
        }
        Iterator iter = mItemIDMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            XPagedViewItem val = (XPagedViewItem) entry.getValue();
            final DrawableItem item = val.getDrawingTarget();
            if (item != null && item.isVisible()) {
                store.add(item);
            }
        }
        return store;
    }

    public ArrayList<DrawableItem> getSourceItemsByOrder(ArrayList<DrawableItem> store, int count) {
        if (store == null) {
            store = new ArrayList<DrawableItem>();
        } else {
            store.clear();
        }
        int found = 0;
        if (count < 0) count = mItemIDMap.size();
        for (int i = 0; i < getChildCount(); i++) {
            if (found >= count) break;
            XCell cell = (XCell) getChildAt(i);
            if (cell != null && cell.getDrawingTarget() != null) {
                store.add(cell.getDrawingTarget());
                found++;
            }
        }
        return store;
    }
    /*** RK_ID: XFOLDER.  AUT: zhaoxy . DATE: 2013-01-22 . END***/
    
    public boolean[][][] mOccupied;
    
    public boolean findCellForSpan(int[] cellXY, int spanX, int spanY, int screen, ItemInfo ignoreInfo) {
        if (screen >= mOccupied.length) {
            Log.w(TAG, "findCellForSpan, no this screen, index = " + screen);
            return false;
        }
        return findCellForSpanThatIntersectsIgnoring(cellXY, spanX, spanY, -1, -1, ignoreInfo, mOccupied[screen]);
    }
    
    /**
     * 判断第screen屏cellXY位置是否能放下指定跨度spanX和spanY的item.
     * @param cellXY 指定位置
     * @param spanX
     * @param spanY
     * @param screen
     * @return
     */
    public boolean findCellForSpanRightHere(int cellX, int cellY, int spanX, int spanY, int screen) {
        if (cellX < 0 || cellX + spanX > mCellCountX ||
                cellY < 0 || cellY + spanY > mCellCountY) {
            return false;
        }
        final boolean occupied[][] = mOccupied[screen];
        int startX = cellX;
        int endX = cellX + spanX - 1;
        int startY = cellY;
        int endY = cellY + spanY - 1;
        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                if (occupied[x][y]) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean findCellForSpanRightHere(int cellX, int cellY, int spanX, int spanY, boolean[][] occupied) {
        if (cellX < 0 || cellX + spanX > mCellCountX ||
                cellY < 0 || cellY + spanY > mCellCountY) {
            return false;
        }

        int startX = cellX;
        int endX = cellX + spanX - 1;
        int startY = cellY;
        int endY = cellY + spanY - 1;
        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                if (occupied[x][y]) {
                    return false;
                }
            }
        }
        return true;
    }
    

	public boolean hasSpaceForItemAt(XPagedViewItem item, int screen, int cx, int cy) {
		
		if(item == null){
			return false;
		}
		ItemInfo info = item.getInfo();
		if(info.cellX == cx && info.cellY == cy && screen == info.screen){
//			android.util.Log.i( "D2", "return space 1" );
			return true;
		}
		
		boolean[][] occupied = mOccupied[screen].clone();
		markCellsAsUnOccupiedForView(info, occupied);
		
		final long id = item.getId();
		
//		android.util.Log.i( "D2", "   1              fffffffffffffff" );
		
        int startX = cx;
        int endX = cx + info.spanX - 1;
        int startY = cy;
        int endY = cy + info.spanY - 1;
        
//        if(lastCheckBeginX == startX 
//        		&& lastCheckBeginY == startY 
//        		&& lastCheckEndX == endX
//        		&& lastCheckEndY == endY){
//        	return false;
//        }
        
//        if(startX == endX && endX == 0 && startY == endY && startY == 0){
//        	return false;
//        }
        if(D2.DEBUG)
        	D2.echo("begin is : (" + startX + " , " + startY + ") , end is : (" + endX + " , " + endY + ")");
        for (int y = startY; y <= endY; y++) {
//    		android.util.Log.i( "D2", "   2             fffffffffffffff" );
            for (int x = startX; x <= endX; x++) {
//            	boolean a , b;
//            	a = occupied[x][y];
            	XPagedViewItem localItem =  findPageItemAt(screen, x, y);
/*            	android.util.Log.i("D2", "be is : o[" + x + "][" + y+ "]  is : " 
            	+ a + "   , id same : " + (localItem==null ? null : localItem.getId()));*/
                if (occupied[x][y] && (localItem != null && localItem.getId() != id)) {
                	D2.echo("now no space for item . " + item.getInfo());
                	markCellsAsOccupiedForView(info, occupied);
                    return false;
                }
            }
        }
        
        markCellsAsOccupiedForView(info, occupied);
		android.util.Log.i( "D2", "return space 2" );
		
		return true;
	}
    
    /**
     * The superset of the above two methods
     */
    boolean findCellForSpanThatIntersectsIgnoring(int[] cellXY, int spanX, int spanY,
            int intersectX, int intersectY, ItemInfo ignoreInfo, boolean occupied[][]) {
        // mark space take by ignoreView as available (method checks if ignoreView is null)
        markCellsAsUnOccupiedForView(ignoreInfo, occupied);

        boolean foundCell = false;
        while (true) {
            int startX = 0;
            if (intersectX >= 0) {
                startX = Math.max(startX, intersectX - (spanX - 1));
            }
            int endX = mCellCountX - (spanX - 1);
            if (intersectX >= 0) {
                endX = Math.min(endX, intersectX + (spanX - 1) + (spanX == 1 ? 1 : 0));
            }
            int startY = 0;
            if (intersectY >= 0) {
                startY = Math.max(startY, intersectY - (spanY - 1));
            }
            int endY = mCellCountY - (spanY - 1);
            if (intersectY >= 0) {
                endY = Math.min(endY, intersectY + (spanY - 1) + (spanY == 1 ? 1 : 0));
            }

            for (int y = startY; y < endY && !foundCell; y++) {
                inner:
                for (int x = startX; x < endX; x++) {
                    for (int i = 0; i < spanX; i++) {
                        for (int j = 0; j < spanY; j++) {
                            if (occupied != null && occupied[x + i][y + j]) {
                                // small optimization: we can skip to after the column we just found
                                // an occupied cell
                                x += i;
                                continue inner;
                            }
                        }
                    }
                    if (cellXY != null) {
                        cellXY[0] = x;
                        cellXY[1] = y;
                    }
                    foundCell = true;
                    break;
                }
            }
            if (intersectX == -1 && intersectY == -1) {
                break;
            } else {
                // if we failed to find anything, try again but without any requirements of
                // intersecting
                intersectX = -1;
                intersectY = -1;
                continue;
            }
        }

        // re-mark space taken by ignoreView as occupied
        markCellsAsOccupiedForView(ignoreInfo, occupied);
        return foundCell;
    }
    
    public void markCellsForView(int cellX, int cellY, int spanX, int spanY, int screen,
            boolean value) {
        if (cellX < 0 || cellY < 0 || screen >= getPageCount() || screen >= mOccupied.length) return;
        for (int x = cellX; x < cellX + spanX && x < mCellCountX; x++) {
            for (int y = cellY; y < cellY + spanY && y < mCellCountY; y++) {
                mOccupied[screen][x][y] = value;
//                R5.echo("XPagedView markCellsForView x = " + x + "y =" + y + "value = " + value);
            }
        }
    }
        
	public int[] getInfoFromIndex(int cellIndex, int[] res) {
		if (res == null || res.length < 3) {
			res = new int[3];
		}

		int sum = mCellCountX * mCellCountY;
		res[0] = cellIndex / sum;
		res[1] = cellIndex % mCellCountX;
		res[2] = (cellIndex % sum) / mCellCountX;

		return res;

	}
    
    public ArrayList<Point> findVacantCellNumber(int page) {

        ArrayList<Point> mPointCell = new ArrayList<Point>();
        mPointCell.clear();
        boolean available = false;
        for (int y = 0; y < mCellCountY; y++) {
            for (int x = 0; x < mCellCountX; x++) {
            	try {
                    available = mOccupied[page][x][y];
            	
                    if (!available) {
                        mPointCell.add(new Point(x, y));
                    }
                }catch(Exception e) {
            		Log.v("arrayindexoutofbounds","===========");
            	}
            }
        }
        return mPointCell;
    }
    
    
    
    private final int[] mTmpXY = new int[2];
    
    public int[] findNearestArea(int screen, int pixelX, int pixelY, int minSpanX, int minSpanY, int spanX, int spanY,
            ItemInfo info, boolean ignoreOccupied, int[] result, int[] resultSpan){
        return findNearestArea(pixelX, pixelY, minSpanX, minSpanY, spanX, spanY,
                info, ignoreOccupied, result, resultSpan, mOccupied[screen]);
    }
    
    public int[] findNearestArea(int pixelX, int pixelY, int minSpanX, int minSpanY, int spanX, int spanY,
            ItemInfo info, boolean ignoreOccupied, int[] result, int[] resultSpan, boolean[][] occupied) {
        lazyInitTempRectStack();
        // mark space take by ignoreView as available (method checks if ignoreView is null)
        markCellsAsUnoccupiedForView(info, occupied);
        
        // For items with a spanX / spanY > 1, the passed in point (pixelX, pixelY) corresponds
        // to the center of the item, but we are searching based on the top-left cell, so
        // we translate the point over to correspond to the top-left.
        pixelX -= (mCellWidth + mWidthGap) * (spanX - 1) / 2f;
        pixelY -= (mCellHeight + mHeightGap) * (spanY - 1) / 2f;
        
        // Keep track of best-scoring drop area
        final int[] bestXY = result != null ? result : new int[2];
        double bestDistance = Double.MAX_VALUE;
        final Rect bestRect = new Rect(-1, -1, -1, -1);
        final Stack<Rect> validRegions = new Stack<Rect>();

        final int countX = mCellCountX;
        final int countY = mCellCountY;

        if (minSpanX <= 0 || minSpanY <= 0 || spanX <= 0 || spanY <= 0 ||
                spanX < minSpanX || spanY < minSpanY) {
            return bestXY;
        }
        
        if (minSpanY < 1 || minSpanX < 1)
        {
            Exception e = new Exception();
            e.printStackTrace();
        }

        for (int y = 0; y < countY - (minSpanY - 1); y++) {
            inner:
            for (int x = 0; x < countX - (minSpanX - 1); x++) {
                int ySize = -1;
                int xSize = -1;
                if (ignoreOccupied) {
                    // First, let's see if this thing fits anywhere
                    for (int i = 0; i < minSpanX; i++) {
                        for (int j = 0; j < minSpanY; j++) {
                        	if((occupied.length <= (x + i))|| (occupied[0].length <= (y + j))) {
                        		continue;
                        	}
                            if (occupied[x + i][y + j]) {
                                continue inner;
                            }
                        }
                    }
                    xSize = minSpanX;
                    ySize = minSpanY;

                    // We know that the item will fit at _some_ acceptable size, now let's see
                    // how big we can make it. We'll alternate between incrementing x and y spans
                    // until we hit a limit.
                    boolean incX = true;
                    boolean hitMaxX = xSize >= spanX;
                    boolean hitMaxY = ySize >= spanY;
                    while (!(hitMaxX && hitMaxY)) {
                        if (incX && !hitMaxX) {
                            for (int j = 0; j < ySize; j++) {
                            	if((occupied.length <= (x + xSize))|| (occupied[0].length <= (y + j))) {
                            		continue;
                            	}
                                if (x + xSize > countX -1 || occupied[x + xSize][y + j]) {
                                    // We can't move out horizontally
                                    hitMaxX = true;
                                }
                            }
                            if (!hitMaxX) {
                                xSize++;
                            }
                        } else if (!hitMaxY) {
                            for (int i = 0; i < xSize; i++) {
                            	if((occupied.length <= (x + i))|| (occupied[0].length <= (y + ySize))) {
                            		continue;
                            	}
                                if (y + ySize > countY - 1 || occupied[x + i][y + ySize]) {
                                    // We can't move out vertically
                                    hitMaxY = true;
                                }
                            }
                            if (!hitMaxY) {
                                ySize++;
                            }
                        }
                        hitMaxX |= xSize >= spanX;
                        hitMaxY |= ySize >= spanY;
                        incX = !incX;
                    }
                    incX = true;
                    hitMaxX = xSize >= spanX;
                    hitMaxY = ySize >= spanY;
                }
                final int[] cellXY = mTmpXY;
                cellToCenterPoint(x, y, cellXY);

                // We verify that the current rect is not a sub-rect of any of our previous
                // candidates. In this case, the current rect is disqualified in favour of the
                // containing rect.
                Rect currentRect = null;
                try
                {
                    currentRect = mTempRectStack.pop();
                }
                catch (Exception e) {
                	R5.echo("minSpanY = " + minSpanY + "minSpanX = " + minSpanX + "countX = " + countX + "countY = " + countY + "x = " + x + "y = " + y);
                	
                	// re-mark space taken by ignoreView as occupied
                    markCellsAsOccupiedForView(info, occupied);

                    // Return -1, -1 if no suitable location found
                    if (bestDistance == Double.MAX_VALUE) {
                        bestXY[0] = -1;
                        bestXY[1] = -1;
                    }
                    return bestXY;
                    
                }
                currentRect.set(x, y, x + xSize, y + ySize);
                boolean contained = false;
                for (Rect r : validRegions) {
                    if (r.contains(currentRect)) {
                        contained = true;
                        break;
                    }
                }
                validRegions.push(currentRect);
                double distance = Math.sqrt(Math.pow(cellXY[0] - pixelX, 2)
                        + Math.pow(cellXY[1] - pixelY, 2));

                if ((distance <= bestDistance && !contained) ||
                        currentRect.contains(bestRect)) {
                    bestDistance = distance;
                    bestXY[0] = x;
                    bestXY[1] = y;
                    if (resultSpan != null) {
                        resultSpan[0] = xSize;
                        resultSpan[1] = ySize;
                    }
                    bestRect.set(currentRect);
                }
            }
        }
        // re-mark space taken by ignoreView as occupied
        markCellsAsOccupiedForView(info, occupied);

        // Return -1, -1 if no suitable location found
        if (bestDistance == Double.MAX_VALUE) {
            bestXY[0] = -1;
            bestXY[1] = -1;
        }
        recycleTempRects(validRegions);
        return bestXY;
    }
    
    public int[] findNearestArea(int screen,
            int pixelX, int pixelY, int spanX, int spanY, int[] result) {
        return findNearestArea(screen, pixelX, pixelY, spanX, spanY, spanX, spanY, null, false, result, null);
    }
    
    private final Stack<Rect> mTempRectStack = new Stack<Rect>();
    private void lazyInitTempRectStack() {
        if (mTempRectStack.isEmpty()) {
            for (int i = 0; i < mCellCountX * mCellCountY; i++) {
                mTempRectStack.push(new Rect());
            }
        }        
//        R5.echo("mTempRectStack = " + mTempRectStack.size());
    }

    private void recycleTempRects(Stack<Rect> used) {
        while (!used.isEmpty()) {
            mTempRectStack.push(used.pop());
        }
    }
    
    public void markCellsAsUnoccupiedForView(int screen, ItemInfo info) {
        if (info == null/* || view.getParent() != mChildren*/) return;
//        XCellLayout.XLayoutParams lp = (XLayoutParams) view.getLayoutParams();
        markCellsForView(info.cellX, info.cellY, info.spanX, info.spanY, screen, false);
    }
        
    public void markCellsAsUnoccupiedForView(ItemInfo info, boolean occupied[][]) {
        if (info == null/* || view.getParent() != mChildren*/) return;
        markCellsForView(info.cellX, info.cellY, info.spanX, info.spanY, occupied, false);
    }
    
//    public void markCellsAsOccupiedForView(int screen, View view) {
//        if (view == null /*|| view.getParent() != mChildren*/) return;
//        LayoutParams lp = (LayoutParams) view.getLayoutParams();
//        markCellsForView(lp.cellX, lp.cellY, lp.cellHSpan, lp.cellVSpan, screen, true);
//    }
    
    public void markCellsAsOccupiedForView(int screen, ItemInfo info) {
        if (info == null) return;
        markCellsForView(info.cellX, info.cellY, info.spanX, info.spanY, screen, true);
    }
    
    public void markCellsAsOccupiedForView(ItemInfo info, boolean occupied[][]) {
        if (info == null) return;
        markCellsForView(info.cellX, info.cellY, info.spanX, info.spanY, occupied, true);
    }
    
    public void markCellsAsUnOccupiedForView(ItemInfo info, boolean occupied[][]) {
        if (info == null) return;
        markCellsForView(info.cellX, info.cellY, info.spanX, info.spanY, occupied, false);
    }
    
    public void markCellsForView(int cellX, int cellY, int spanX, int spanY, boolean occupied[][], boolean isoccupied) {
        if (cellX < 0 || cellY < 0) return;
        if (occupied == null || cellX + spanX > occupied.length
                || cellY + spanY > occupied[0].length) {
            Log.w(TAG, "cellX or cellY is out of occupied length...");
            return;
        }
        for (int x = 0; x < spanX; x++) {
            for (int y = 0; y < spanY; y++) {
                occupied[cellX + x][cellY + y] = isoccupied;
            }
        }
    }
    
    void cellToCenterPoint(int cellX, int cellY, int[] result) {
        regionToCenterPoint(cellX, cellY, 1, 1, result);
    }
    
    void regionToCenterPoint(int cellX, int cellY, int spanX, int spanY, int[] result) {
        final int hStartPadding = (int)getRelativeX() + (int)(_gapEnable ? _gapBetweenPage / 2 : 0f);//getPaddingLeft();
        final int vStartPadding = (int)getRelativeY();//getPaddingTop();
        result[0] = hStartPadding + cellX * (mCellWidth + mWidthGap) +
                (spanX * mCellWidth + (spanX - 1) * mWidthGap) / 2;
        result[1] = vStartPadding + cellY * (mCellHeight + mHeightGap) +
                (spanY * mCellHeight + (spanY - 1) * mHeightGap) / 2;
    }
    
    /*RK_ID: RK_SCREEN_MNG . AUT: zhanggx1 . DATE: 2013-02-01 . S*/
    public void addNewScreen(int index) { 

		// new implementation
		final int singlePageCellNum = mCellCountX * mCellCountY;

		// modify following paged view item
		final Iterator<XPagedViewItem> items = mItemIDMap.values().iterator();
		while (items.hasNext()) {
			final XPagedViewItem item = items.next();
			final ItemInfo info = item.getInfo();
			if (info.screen >= index) {
				info.screen++;
				final int len = info.attachedIndexArray.length;
				for (int p = 0; p < len; p++) {
					try {
					    info.attachedIndexArray[p] += singlePageCellNum;
					}catch(Exception e){
						Log.v("outofbounds","=================");
						
					}
				}

				// view container
				DrawableItem drawItem = item.getDrawingTarget();
				if (item.getDrawingTarget() instanceof XViewContainer) {
					((XViewContainer) drawItem)
							.setMyScreen(item.getInfo().screen);
				}
			}
		}
    	
		// fill cells
		final int cellCursorLeft = getCellIndex(index, 0, 0);
		for (int i = 0; i < singlePageCellNum; i++) {
			XCell cell = new XCell(getXContext(), null, null);
			addItem(cell, cellCursorLeft + i);
			cell = null;
		}

		if (mOccupied.length > 0) {
		// manage occupied space
		final boolean[][][] copyOcc = mOccupied.clone();
		final ArrayList<boolean[][]> ls = new ArrayList<boolean[][]>((Arrays.asList(copyOcc)));
		final boolean[][] newVacants = new boolean[mCellCountX][mCellCountY];
		ls.add(index, newVacants);
		mOccupied = ls.toArray(new boolean[0][][]);
		
/*		mOccupied = new boolean[mPageCount][mCellCountX][mCellCountY];
		System.arraycopy(copyOcc, 0, mOccupied, 0, index);
		System.arraycopy(newVacants, 0, mOccupied, index, newVacants.length);
		System.arraycopy(copyOcc, index, mOccupied, index + newVacants.length,
				copyOcc.length - index);*/
        } else {
            mOccupied = new boolean[1][mCellCountX][mCellCountY];
        }
		
		// update page count
		updatePageCount();
		int currPage = mCurrentPage;
    	if (index <= currPage) {
    		setCurrentPage(++currPage);
    	}
    	
    	updateDataAddNewScreen(index);

		// reset scroller
		if (this.mPageDrawAdapter != null) {
			this.mPageDrawAdapter.reset();
		}

		// notify our listeners
		updateIndicator();
    }
    
    public void addNewScreen() {
    	addNewScreen(getPageCount());
    }
    
    public void removeScreenAt(int index) {

		// new implementation
		// remove page item in range
		ArrayList<XPagedViewItem> pageItemList = getChildrenAt(index);
//		android.util.Log.i("xpage1", index + " page item list : " + pageItemList);
		for(XPagedViewItem item : pageItemList){
			removePagedViewItem( item );
		}
		
		// remove cell in range
		final int singlePageCellNum = mCellCountX * mCellCountY;
		final int cellCursorLeft = getCellIndex(index, 0, 0);
//		android.util.Log.i("xpage1", "it is 1 : " + getChildCount());
		Collection<DrawableItem> tmpList = new ArrayList<DrawableItem>();
		for (int i = 0; i < singlePageCellNum; i++) {
			int position = cellCursorLeft + i;
			tmpList.add(getChildAt(position));
		}
		
		removeItems(tmpList);
		
//		android.util.Log.i("xpage1", "it is 2: " + getChildCount());
		
		// update page info
		for (int i = index + 1; i < mPageCount; i++) {
			List<XPagedViewItem> l = getChildrenAt(i);
			if (l != null && !l.isEmpty()) {
				for (XPagedViewItem item : l) {
					item.getInfo().screen--;
					int len = item.getInfo().attachedIndexArray.length;
					for(int p = 0; p< len; p ++){
						item.getInfo().attachedIndexArray[p] -= singlePageCellNum;
					}
					
					// view container
					DrawableItem drawItem = item.getDrawingTarget();
					if(item.getDrawingTarget() instanceof XViewContainer){
						((XViewContainer)drawItem).setMyScreen( item.getInfo().screen );
					}
					
				}
			}
		}
		
		// update page count
    	int currPage = mCurrentPage;
		updatePageCount();
    	if (index <= currPage) {
    		setCurrentPage(--currPage);
    	}


		// reset scroller
		if (this.mPageDrawAdapter != null) {
			this.mPageDrawAdapter.reset();
		}

		// update occupied array
		ArrayList<boolean[][]> list = new ArrayList<boolean[][]>(
				Arrays.asList(mOccupied.clone()));
		if (list.size() > index) {
			list.remove(index);
		}
		if (list.isEmpty()) {
		    mOccupied = new boolean [mPageCount][mCellCountX][mCellCountY];
        } else {
            mOccupied = list.toArray(new boolean[1][][]);
        }
				
		updateDataRemoveScreenAt(index);

		// notify our listeners
		updateIndicator();

    }
    
    private void reset() {
    	mPagedViewItemCount = 0;
    	mPagedViewItemId = 0;
    	mStage = null;
    	
        mItemIDMap.clear();    	
    	clearAllItems();
    	reuse();
    }
    
    public ArrayList<XPagedViewItem> getChildrenAt(int screen) {
    	ArrayList<XPagedViewItem> ret = new ArrayList<XPagedViewItem>();
    	
    	if (mItemIDMap.isEmpty()) {
    		return ret;
    	}
    	Iterator<XPagedViewItem> items = mItemIDMap.values().iterator();
    	while (items.hasNext()) {
    		XPagedViewItem item = items.next();
    		ItemInfo info = item.getInfo();
    		if (info.screen == screen) {
    			ret.add(item);
    		}
    	}
    	return ret;
    }
    
    public int getChildCountAt(int screen) {
    	return getChildrenAt(screen).size();
    }
    
    public void draw(IDisplayProcess canvas, int page) {

        updateFinalAlpha();

        if (mPageDrawAdapter == null) {
            if (sphereAdapter == null) {
                sphereAdapter = new SphereDrawAdapter(false, this);
            }
            sphereAdapter.resetSphereOrCylinder(false);
            setPageDrawAdapter(sphereAdapter);
            if (mAnimController != null) {
                mAnimController.setSphereSlide(true);
            }
        }

        if (mPageDrawAdapter != null) {
            rect2ball = 0;
            mPageDrawAdapter.drawPage(canvas, page, 0, 0);
        }
    }

    /*** fixbug . AUT: zhaoxy . DATE: 2013-03-22 . START ***/
    public void resetChildrenMatrix(int page) {
        if (page > -1 && page < mPageCount) {
            final int countPrePage = mCellCountX * mCellCountY;
            final int startIndex = page * countPrePage;
            final int endIndex = Math.min(startIndex + countPrePage, getChildCount());
            for (int i = startIndex; i < endIndex; i++) {
                DrawableItem item = getChildAt(i);
                if (item == null)
                    continue;
                item.setTouchable(true);
                Matrix m = item.getMatrix();
                m.reset();
                item.updateMatrix(m);
            }
        }
    }
    /*** fixbug . AUT: zhaoxy . DATE: 2013-03-22 . END ***/
    
    private boolean isCellparamValid(int cellX, int cellY) {
        return (0 <= cellX && cellX < mCellCountX) && (0 <= cellY && cellY < mCellCountY);
    }
    
    public boolean isCellparamValid(int cellX, int cellY, int spanX, int spanY) {
        
        if (cellX < 0 || cellY < 0) return false;
        for (int x = cellX; x < cellX + spanX && x < mCellCountX; x++) {
            for (int y = cellY; y < cellY + spanY && y < mCellCountY; y++) {
                if (mOccupied[getCurrentPage()][x][y])
                    return false;
            }
        }
        
        return true;
    }

    public boolean moveItemToPosition(final XPagedViewItem child, int cellX, int cellY, final int screen) {
    	
    	Runnable r = null;
    	final DrawableItem drawingTarget = child.getDrawingTarget();
    	if(drawingTarget instanceof XViewContainer){
    		r = new Runnable() {
				@Override
				public void run() {
					if(getCurrentPage() == child.getInfo().screen){
						((XViewContainer) drawingTarget).manageVisibility(XViewContainer.VISIBILITY_SHOW_VIEW, null);
					}
				}
			};
    	}
    	
    	int len = mOccupied.length;
    	final int newScreen = screen >= len ? len - 1 : screen;
    	
        return moveItemToPosition(child, cellX, cellY, newScreen, 500, 10, r, mOccupied[newScreen], false);
    }
    
//    public boolean moveChildToPosition(final XPagedViewItem child, int cellX, int cellY, int screen, boolean[][] occupied) {
//        if (clc.indexOfChild(child) != -1 && isCellparamValid(cellX, cellY) && !occupied[cellX][cellY]) {
//            if (child == null) {
//                return false;
//            }
//            final ItemInfo info = (ItemInfo) child.getInfo();
//
//            /* RK_ID: RK_CELLLAYOUT. AUT: liuli1 . DATE: 2012-10-31 . START */
//            if (info == null || !isCellparamValid(info.cellX, info.cellY)) {
//                return false;
//            }
//            /* RK_ID: RK_CELLLAYOUT. AUT: liuli1 . DATE: 2012-10-31 . END */            
//            this.removePagedViewItem(child);
//            info.cellX = cellX;
//            info.cellY = cellY;
//            info.screen = screen;
//            addPagedViewItem(child);
////            if (child.getDrawingTarget() instanceof XViewContainer)
////            {
////                XViewContainer container = (XViewContainer)child.getDrawingTarget();
////                container.onMovingTo(child, screen, cellX, cellY);
////                R5.echo("container.onMovingTo cellX = " + cellX + "cellY = " + cellY);
////            }
//            invalidate();
//            return true;
//        }
//        return false;
//    }
    
    public void changeScreenOrder(final int fromIndex, final int toIndex) { 	
    	if (fromIndex == toIndex) {
    		return;
    	}
    	boolean fromIsGreater = fromIndex > toIndex;
    	
    	ArrayList<XPagedViewItem> children = getChildrenAt(fromIndex);
    	for (int i = 0; i < children.size(); i++) {
    		XPagedViewItem view = children.get(i);
    		removePagedViewItem(view, false, false);
    	}
    	
    	int oldCurrent = mCurrentPage;
    	if (fromIsGreater) {
    		for (int screen = fromIndex - 1; screen >= toIndex; screen--) {
    			ArrayList<XPagedViewItem> childrenTmp = getChildrenAt(screen);
    			
    	    	for (int j = 0; j < childrenTmp.size(); j++) {
    	    		XPagedViewItem view = childrenTmp.get(j);
    	    		ItemInfo info = view.getInfo();
    	    		moveItemToPosition(view, info.cellX, info.cellY, screen + 1);
    	    	}
        	}
    		if (oldCurrent >= toIndex && oldCurrent < fromIndex) {
    			setCurrentPage(oldCurrent + 1);
    		}
    		
    	} else {
    		for (int screen = fromIndex + 1; screen <= toIndex; screen++) {
    			ArrayList<XPagedViewItem> childrenTmp = getChildrenAt(screen);
    	    	for (int j = 0; j < childrenTmp.size(); j++) {
    	    		XPagedViewItem view = childrenTmp.get(j);
    	    		ItemInfo info = view.getInfo();
    	    		moveItemToPosition(view, info.cellX, info.cellY, screen - 1);
    	    	}
        	}
    		if (oldCurrent <= toIndex && oldCurrent > fromIndex) {
    			setCurrentPage(oldCurrent - 1);
    		}
    	}
    	for (int i = 0; i < children.size(); i++) {
			XPagedViewItem view = children.get(i);
			view.getInfo().screen = toIndex;
			addPagedViewItem(view);
    	}
    	if (oldCurrent == fromIndex) {
    		setCurrentPage(toIndex);
    	}
    	
    	updateDrawData();
    	invalidate();
    }

    public void resetSlideAdapter() {
        initPageDrawAdapter(LauncherPersonalSettings.SLIDEEFFECT_NORMAL);
    }
    
    public void setEnableEffect(boolean enable) {
        if (enable) {
            enableEffect = true;
            updateSlideValue();
            initPageDrawAdapter(mPrefSlide);
        } else {
            enableEffect = false;
            updateSlideValue();
            initPageDrawAdapter(LauncherPersonalSettings.SLIDEEFFECT_NORMAL);
        }
    }

    @Override
    public void clean() {
    	if (!mItemIDMap.isEmpty()) {
    		Iterator<XPagedViewItem> items = mItemIDMap.values().iterator();
        	while (items.hasNext()) {
        		XPagedViewItem item = items.next();
        		item.mDrawingTarget.clean();
        	}
    	}
    	
    	if (mStage != null) {
	    	mStage.removeAllViews();
	    	mStage.requestLayout();
    	}
		super.clean();
	}
    
    
    protected boolean mLongPressHappened = false;
    @Override
    public boolean onLongPress(MotionEvent e) {
        resetTouchBounds();
        desireTouchEvent(false);

    	if( mAnimController != null ){
    		 mAnimController.stopTouchAnim();
    	}
    	getXContext().bringContentViewToFront();
    	mLongPressHappened = true;
    	
    	return super.onLongPress(e);
    }
    
    void updateIndicator() {
        if (pageSwitchers != null) {
            for (PageSwitchListener pl : pageSwitchers) {
                pl.onUpdatePage(mPageCount, this.mCurrentPage);
            }
        }
    }
    
    synchronized void updatePageCount(int size) {
        mPageCount = (int) Math.ceil((float) size / (mCellCountX * mCellCountY));
        if (mPageCount >= 0) {
            if (mCurrentPage >= mPageCount) {
                mCurrentPage = mPageCount - 1;
            }
            if (mCurrentPage < 0) {
                mCurrentPage = 0;
            }
            updateIndicator();
        }

        /* RK_ID: RK_SLIDEEFFECT. AUT: liuli1 . DATE: 2012-12-06 . START */
//      if (normalAdapter != null) {
//          normalAdapter.setup( this );
//      }
//      
//      if(mPageDrawAdapter != null){
//          mPageDrawAdapter.setup( this );
//      }
        /* RK_ID: RK_SLIDEEFFECT. AUT: liuli1 . DATE: 2012-12-06 . END */
    }
    
    //add by zhanggx1 2013-04-17
    @Override
    public void clearAllItems() {
    	if (mItemIDMap != null) {
    	    mItemIDMap.clear();
    	}
    	if (pageSwitchers != null) {
    	    pageSwitchers.clear();
    	}
    	
    	if (mStage != null) {
	    	mStage.removeAllViews();
	    	mStage.requestLayout();
    	}
    	super.clearAllItems();
    }
    
    @Override
    public void setVisibility(boolean visibility) {
    	super.setVisibility(visibility);
    	
    	desireTouchEvent(false);
    	resetPressedState();
    }

    public void hideViewContainerCurrentPage() {
        int currentPage = getCurrentPage();
        ArrayList<XPagedViewItem> children = getChildrenAt(currentPage);
        for (int i = 0; i < children.size(); i++) {
            XPagedViewItem child = children.get(i);
            if (child != null && child.getDrawingTarget() instanceof XViewContainer) {
                ((XViewContainer) child.getDrawingTarget()).manageVisibility(XViewContainer.VISIBILITY_SHOW_SHADOW, null);
            }
        }
    }

    public void showViewContainerCurrentPage() {
        int currentPage = getCurrentPage();
        ArrayList<XPagedViewItem> children = getChildrenAt(currentPage);
        for (int i = 0; i < children.size(); i++) {
            XPagedViewItem child = children.get(i);
            if (child != null && child.getDrawingTarget() instanceof XViewContainer) {
                ((XViewContainer) child.getDrawingTarget()).manageVisibility(XViewContainer.VISIBILITY_SHOW_VIEW, null);
            }
        }
    }
    
    /*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-5-30. S***/
    public void getExpandabilityArrayForView(ItemInfo info, int[] expandability) {
    	Log.i("zdx1","XPagedView.getExpandabilityArrayForView--"+ info);
    	if( info == null || expandability == null){
    		return;
    	}
    	
    	boolean flag;

        expandability[AppWidgetResizeFrame.LEFT] = 0;
        for (int x = info.cellX - 1; x >= 0; x--) {
            flag = false;
            for (int y = info.cellY; (y < info.cellY + info.spanY )&& (y < mCellCountY); y++) {
                if (mOccupied[info.screen][x][y]) flag = true;
            }
            if (flag) break;
            expandability[AppWidgetResizeFrame.LEFT]++;
        }

        expandability[AppWidgetResizeFrame.TOP] = 0;
        for (int y = info.cellY - 1; y >= 0; y--)  {
            flag = false;
            for (int x = info.cellX; (x < info.cellX + info.spanX)&& (x < mCellCountX); x++) {
                if (mOccupied[info.screen][x][y]) flag = true;
            }
            if (flag) break;
            expandability[AppWidgetResizeFrame.TOP]++;
        }

        expandability[AppWidgetResizeFrame.RIGHT] = 0;
        for (int x = info.cellX + info.spanX; x <mCellCountX; x++) {
            flag = false;
            for (int y = info.cellY; y < info.cellY + info.spanY; y++) {
                if (mOccupied[info.screen][x][y]) flag = true;
            }
            if (flag) break;
            expandability[AppWidgetResizeFrame.RIGHT]++;
        }

        expandability[AppWidgetResizeFrame.BOTTOM] = 0;
        for (int y = info.cellY + info.spanY; y < mCellCountY; y++) {
            flag = false;
            for (int x = info.cellX; x < info.cellX + info.spanX; x++) {
                if (mOccupied[info.screen][x][y]) flag = true;
            }
            if (flag) break;
            expandability[AppWidgetResizeFrame.BOTTOM]++;
        }
       
        Log.i("zdx1","XPagedView.getExpandabilityArrayForView out");
    }
    /*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-5-30. E***/
        
    boolean mIndicatorScroll;
    public void setIndicatorScroll(boolean scroll) {
        mIndicatorScroll = scroll;
    }
    
    public int[] findFirstVacantCell(int page, int spanX, int spanY) {
        boolean available = false;
        for (int y = 0; y < mCellCountY; y++) {
            for (int x = 0; x < mCellCountX; x++) {
                available = mOccupied[page][x][y];
                if (!available && (x + spanX <= mCellCountX) && (y + spanY <= mCellCountY)) {
                	for (int k = y; k < y + spanY; k++) {
                		for (int m = x; m < x + spanX; m++) {
                			available |= mOccupied[page][m][k];
                		}
                	}
                    if (!available) {
                    	return new int[]{x, y};
                    }
                }
            }
        }
        return null;
    }

    private void checkOffsetToNormal(){
        while (mOffsetX > 1f) {
            if (mCurrentPage > 0) {

                // R2
                if (pageSwitchers != null) {
                    for (PageSwitchListener pl : pageSwitchers) {
                        pl.onPageSwitching(mCurrentPage, mCurrentPage - 1, mOffsetX);
                    }
                }
                // 2R

                --mCurrentPage;
            } else if (isLoop) {
                mCurrentPage = mPageCount - 1;
                // R2
                if (pageSwitchers != null) {
                    for (PageSwitchListener pl : pageSwitchers) {
                        pl.onPageSwitching(0, mCurrentPage, mOffsetX);
                    }
                }
                // 2R
            }
            mOffsetX -= 1f;
            mOffsetXTarget -= 1f;
        }

        while (mOffsetX < -1) {
            if (mCurrentPage + 1 < mPageCount) {

                // R2
                if (pageSwitchers != null) {
                    for (PageSwitchListener pl : pageSwitchers) {
                        pl.onPageSwitching(mCurrentPage, mCurrentPage + 1, mOffsetX);
                    }
                }
                // 2R

                ++mCurrentPage;
            } else if (isLoop) {

                // R2
                if (pageSwitchers != null) {
                    for (PageSwitchListener pl : pageSwitchers) {
                        pl.onPageSwitching(mCurrentPage, 0, mOffsetX);
                    }
                }
                // 2R
                mCurrentPage = 0;
            }
            mOffsetX += 1f;
            mOffsetXTarget += 1f;
        }
    }
    
    private void getOrientationFromOffset(){
        if (mOffsetX < -0.5f) {
            currOrientation = ORI_LEFT;
        } else if (mOffsetX > 0.5f) {
            currOrientation = ORI_RIGHT;
        } else {
//          if(currOrientation == 0){
//              currOrientation = ORI_NONE;
//          }else{
//              currOrientation = Math.signum(mOffsetX) > 0 ? ORI_RIGHT : ORI_LEFT;
//            }
            currOrientation = ORI_NONE;
        }
    }
    
    private void scrollByOrientation(){
        switch (currOrientation) {
        case ORI_RIGHT:
            if (mCurrentPage  > 0) {
                --mCurrentPage;
                mOffsetX -= 1f;

                if (pageSwitchers != null) {
                    for (PageSwitchListener pl : pageSwitchers) {
                        pl.onPageSwitching(mCurrentPage + 1, mCurrentPage, mOffsetX);
                    }
                }

            } else if (isLoop && mPageCount > 1) {
                mCurrentPage = mPageCount - 1;
                mOffsetX -= 1f;
                if (pageSwitchers != null) {
                    for (PageSwitchListener pl : pageSwitchers) {
                        pl.onPageSwitching(0, mCurrentPage, mOffsetX);
                    }
                }
            }
            mAnimController.stopTouchAnim();
            if (!isPageMoving) {
                    isPageMoving = true;
                onPageBeginMoving();
            }
            mAnimController.startOffsetXAnim(  OffsetXAnimDuration);
            mAnimController.startOffsetYAnim(  OffsetYAnimDuration);

            break;
        case ORI_LEFT:
            if (mCurrentPage + 1 < mPageCount) {
                ++mCurrentPage;
                mOffsetX += 1f;

                if (pageSwitchers != null) {
                    for (PageSwitchListener pl : pageSwitchers) {
                        pl.onPageSwitching(mCurrentPage - 1, mCurrentPage, mOffsetX);
                    }
                }
            } else if (isLoop && mPageCount > 1) {
                if (pageSwitchers != null) {
                    for (PageSwitchListener pl : pageSwitchers) {
                        pl.onPageSwitching(mCurrentPage, 0, mOffsetX);
                    }
                }
                mCurrentPage = 0;
                mOffsetX += 1f;
            }
            mAnimController.stopTouchAnim();
            if (!isPageMoving) {
                isPageMoving = true;
                onPageBeginMoving();
            }
            mAnimController.startOffsetXAnim(  OffsetXAnimDuration);
            mAnimController.startOffsetYAnim(  OffsetYAnimDuration);

            break;
        case ORI_NONE:
                if (!mAnimController.isOffsetXAnimStart()) {
                    mAnimController.startOffsetXAnim(OffsetXAnimDuration);
                }
                if (!mAnimController.isOffsetYAnimStart()) {
                    mAnimController.startOffsetYAnim(OffsetYAnimDuration);
                }
                mOffsetY = mOffsetYTarget = 0;
                mAnimController.stopTouchAnim();
                rect2ballTarget = 0.0f;
            break;
        default:
            break;
        }
    }
    
    void scrollToPosition(){
    	checkOffsetToNormal();
        getOrientationFromOffset();
        scrollByOrientation();
        invalidate();
    }
    
    private boolean mDebug = false;
    boolean gestureProcess = false;

    public void moveWidgets() {
        if (mStage != null) {
            LayoutParams params = (LayoutParams) mStage.getLayoutParams();
            if (params != null) {
                params.topMargin = (int) getGlobalY2();
                mStage.setLayoutParams(params);
            }
        } // end if
    }
    
    //dooba add
    public void clearAllItemsNotDestroy() {
    	if (mItemIDMap != null) {
    	    mItemIDMap.clear();
    	}
    	if (pageSwitchers != null) {
    	    pageSwitchers.clear();
    	}
    	
    	if (mStage != null) {
	    	mStage.removeAllViews();
	    	mStage.requestLayout();
    	}
    	super.clearAllItemsNotDestroy();
    }
    
    @Override
    public void resetPressedState(boolean clearPrePressed) {
    	super.resetPressedState(clearPrePressed);
        Iterator iter = mItemIDMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            XPagedViewItem val = (XPagedViewItem) entry.getValue();
            final DrawableItem item = val.getDrawingTarget();
            if (item != null && item.isVisible()) {
            	item.resetPressedState(clearPrePressed);
            }
        }
    }
    
    public static float SCROLL_X_FACTOR = 1f; 
    public static float SCROLL_Y_FACTOR = 1f;
    
    public void lockViewContainerDrawingModeToForcedForCurrentPage( byte mode ){
    	lockViewContainerDrawingModeToForced(mode, mCurrentPage);
    }
    
    public void unlockViewContainerDrawingModeForCurrentPage(){
    	unlockViewContainerDrawingMode( mCurrentPage );
    }
    
    public void lockViewContainerDrawingModeToForced( byte mode , int screen ){
    	final ArrayList<XPagedViewItem> currentPageItems = getChildrenAt( screen );
    	for( XPagedViewItem item : currentPageItems ){
    		if( item != null && item.getDrawingTarget() != null && item.getDrawingTarget() instanceof XViewContainer ){
    			((XViewContainer) item.getDrawingTarget()).freezingDrawingModeTo( mode );
    		}
    	}
    }
    
    public void unlockViewContainerDrawingMode( int screen ){
    	final ArrayList<XPagedViewItem> currentPageItems = getChildrenAt( screen );
    	for( XPagedViewItem item : currentPageItems ){
    		if( item != null && item.getDrawingTarget() != null && item.getDrawingTarget() instanceof XViewContainer ){
    			((XViewContainer) item.getDrawingTarget()).unfreezingDrawingMode();
    		}
    	}
    }
    
    private FrameLayout.LayoutParams mStageParams = null;
    public void hideStage(){
        if( enableStage && mStage != null && mStage.getParent() != null ){
            mStageParams = (LayoutParams) mStage.getLayoutParams();
            ((android.view.ViewGroup) mStage.getParent()).removeView(mStage);
        }
    }
    
    public void restoreStage(){
        if( enableStage && mStage != null && mStageParams != null 
                && mStage.getParent() == null){
            getXContext().addView( mStage , mStageParams );
            getXContext().requestLayout();
            getXContext().invalidate();
        }
    }
        
    //add by zhanggx1 for bug launcher242 on 2013-09-12.s
    @Override
    public void onTouchCancel(MotionEvent e) {
		resetTouchBounds();
		desireTouchEvent(false);
    	resetAnim();
    	super.onTouchCancel(e);
    }
    //add by zhanggx1 for bug launcher242 on 2013-09-12.e
    
    @Override
    public boolean onFingerCancel(MotionEvent e) {
		resetTouchBounds();
		desireTouchEvent(false);
    	resetAnim();
    	return super.onFingerCancel(e);
    }
    public void scrollToPage(int destPage){
    	int currentScreen = mCurrentPage;
    	if(destPage > currentScreen){
    		long duration = 30L;
    		for( int i = 0; i < destPage - currentScreen; i++){
    			if( i == destPage - currentScreen - 1 ){
    				duration = 300L;
    			}
    		    scrollToRight(duration);
    		}
    	}else if( destPage < currentScreen){
    		long duration = 30L;
    		for( int i = 0; i < currentScreen - destPage; i++){
    			if( i == currentScreen - destPage - 1 ){
    				duration = 300L;
    			}
    		    scrollToLeft(duration);
    		}
    	}
    }
    
    public boolean scrollToPage(int destPage, float offsetX) {
		mOffsetXTarget = offsetX;
		
        if (mCurrentPage != destPage)
        {
			if (pageSwitchers != null) {
	            for (PageSwitchListener pl : pageSwitchers) {
	                pl.onPageSwitching(mCurrentPage, destPage, mOffsetX);
	            }
	        }
			
			mCurrentPage = destPage;
        }
        
		if (isScrollHalfBack()) {
		    mOffsetXTarget = mOffsetXTarget / 2.0f;
		}

//		mOffsetYTarget += distanceY / getHeight();
//		if (Math.abs(mOffsetYTarget) > 1) {
//			mOffsetYTarget = Math.signum(mOffsetYTarget);
//		}

		if (!isPageMoving) {
			isPageMoving = true;
			onPageBeginMoving();
		}
		
		rect2ballTarget = 1.0f;
		mAnimController.startTouchAnim();
		return true;
	}
    
    private static final float IGNORE_OFFSET = 0.000925926f;
    
    @Override
    protected ArrayList<DrawableItem> checkHitedItem(MotionEvent e) {
		ArrayList<DrawableItem> itemRes = new ArrayList<DrawableItem>();
		if (e != null) {
			for (int i = 0; i < items.size(); i++) {
	            final XCell item = (XCell)items.get(i);
	            if (item != null && item.getDrawingTarget() != null) {
	                ExchangeManager exchange = getXContext().getExchangee();
					if (item.getDrawingTarget().isTouchable() && exchange != null && exchange.checkHited(item, e.getX(),
								e.getY())) {

						if (item.isDesiredTouchEventItem()) {

							itemRes.clear();
							itemRes.add(item);
							return itemRes;
						}
						itemRes.add(item);
					}
				}
			}
		}

        return itemRes;
    }
    
    private HashMap<Integer, Bitmap> mCache = new HashMap<Integer, Bitmap>();
    private boolean mEnableBitmapCache = false;
    private boolean[] mHasWidget;
    private boolean[] mHasChild;
    private static int GENERATE_BITMAP = 1;
    private Handler mGenerateBitmapHandler = new Handler(){
    	@Override
        public void handleMessage(Message msg) {
    		if (msg.what == GENERATE_BITMAP){
    			generateBitmapCache();
    		}
    	}
    };
	
    private Bitmap generateSnapBitmap(int page)
    {       	
    	R5.echo("generateSnapBitmap page = " + page);
        Bitmap bitmap = Bitmap.createBitmap((int)getWidth(), (int)getHeight(), Bitmap.Config.ARGB_8888);
        	                
        IDisplayProcess tmpProc = new NormalDisplayProcess();
        
        tmpProc.beginDisplay(bitmap);
        boolean oldVisible;
        
        int startIndex = page * mCellCountX * mCellCountY;
		int endIndex = Math.min(startIndex + mCellCountX * mCellCountY,
				getChildCount());

		int d = Math.round(getWidth());
		for (int i = startIndex; i < endIndex; i++) {
			DrawableItem item = getChildAt(i);
			if (item == null)
				continue;

			Matrix m = item.getMatrix();
			m.reset();
			m.setTranslate(d, 0);

			item.updateMatrix(m);
			
			item.setAlpha(1f);
			oldVisible = item.isVisible();
			if (!oldVisible)
			{
				R5.echo("generateSnapBitmap invisible");
				item.setVisibility(true);
			}
			item.draw(tmpProc);
			if (!oldVisible)
			{
				item.setVisibility(false);
			}
		}        

        tmpProc.endDisplay();
        return bitmap;
    }    
        
    public void enableBitmapCache(){
    	R5.echo("enableBitmapCache");
    	mEnableBitmapCache = true;
    	generateBitmapCache();   	
    }
    
    private void generateBitmapCache(){
    	XLauncher xlauncher = (XLauncher)mContext.getContext();
    	if (!xlauncher.isWorkspaceNormalState()
    			|| isVisible() == false
    			|| getParent().isVisible() == false)
    	{
    		mGenerateBitmapHandler.sendEmptyMessageDelayed(GENERATE_BITMAP, 1000);
    		return;
    	}
    	
    	updateFinalAlpha();
    	if (getFinalAlpha() < 1)
    	{
    		R5.echo("getFinalAlpha < 1");
    		mGenerateBitmapHandler.sendEmptyMessageDelayed(GENERATE_BITMAP, 1000);
    		return;
    	}
    	
    	int end = getPageCount();
        for (int i = 0; i < end; i++) {
        	if (!mHasWidget[i] && mHasChild[i] && mCache.get(i) == null)
			{
	        	Bitmap bitmap = generateSnapBitmap(i);
	        	mCache.put(i, bitmap);
			}
        }    	
    }
    
    public Bitmap getSnapBitmap(int page)
    {
    	return mCache.get(page);
    }
    
    public void refreshBitmapCache(int screen){
    	if (mEnableBitmapCache)
    	{
    		mCache.remove(screen);
			if (!mHasWidget[screen] && mHasChild[screen])
			{
				mGenerateBitmapHandler.removeMessages(GENERATE_BITMAP);
				mGenerateBitmapHandler.sendEmptyMessage(GENERATE_BITMAP);                       
			}
    	}  	
    }
    
    public void generateBitmapCacheAll(){
    	if (mEnableBitmapCache)
    	{
    		mGenerateBitmapHandler.removeMessages(GENERATE_BITMAP);
			mGenerateBitmapHandler.sendEmptyMessage(GENERATE_BITMAP);    
    	}  	
    }
    
    public void noNeedGenerateBitmapCache(int screen){
    	if (mEnableBitmapCache)
    	{
    		mGenerateBitmapHandler.removeMessages(GENERATE_BITMAP);
    	}  	
    }
    
    private void updateDataAddNewScreen(int index) {     	
		if (mHasWidget.length > 0) {
			int i;
			final boolean[] copy = mHasWidget;
			final boolean[] copy1 = mHasChild;
			mHasWidget = new boolean[mPageCount];
			mHasChild = new boolean[mPageCount];
			for (i = 0; i < index; i++)
			{
				mHasWidget[i] = copy[i];
				mHasChild[i] = copy1[i];
			}
			
			mHasWidget[index] = false;
			
			for (i = mPageCount - 1; i > index; i++)
			{
				mHasWidget[i] = copy[i - 1];
				mHasChild[i] = copy1[i - 1];
				Bitmap bitmap = mCache.remove(i - 1);
				if (bitmap != null)
				{
					mCache.put(i, bitmap);
				}
			}
			
        } else {
        	mHasWidget = new boolean[1];
        	mHasChild = new boolean[1];
        }
    }
    
    private void updateDataRemoveScreenAt(int index) {	
		
		if (mHasWidget.length > 0) {
			int i;
			final boolean[] copy = mHasWidget;
			final boolean[] copy1 = mHasChild;
			mHasWidget = new boolean[mPageCount];
			mHasChild = new boolean[mPageCount];
			for (i = 0; i < index; i++)
			{
				mHasWidget[i] = copy[i];
				mHasChild[i] = copy1[i];
			}
			
			mCache.remove(index);
			for (i = index; i < mPageCount; i++)
			{
				mHasWidget[i] = copy[i + 1];
				mHasChild[i] = copy1[i + 1];
				Bitmap bitmap = mCache.remove(i + 1);
				if (bitmap != null)
				{
					mCache.put(i, bitmap);
				}
			}
			
        } else {
        	mHasWidget = new boolean[mPageCount];
        	mHasChild = new boolean[mPageCount];
        }
    }
    
    public boolean hasChild(int screen){
    	if (mEnableBitmapCache)
    	{
    		return mHasChild[screen];
    	}
    	else
    	{
    		return true;
    	}
    }
    
    private void updateDrawData(){
    	for (int i = 0; i < mPageCount; i++)
    	{
    		mHasWidget[i] = false;
    		mHasChild[i] = false;
    	}
    	
    	XPagedViewItem item;
        ItemInfo info;
        Set<Map.Entry<Long, XPagedViewItem>> set = mItemIDMap.entrySet();
        for (Map.Entry<Long, XPagedViewItem> map : set) {
            item = map.getValue();
            info = item.getInfo();
            mHasChild[info.screen] = true;
            if (item.getDrawingTarget() instanceof XViewContainer)
            {
            	mHasWidget[info.screen] = true;
            }
        }
    }
    
    public void refreshBitmapCacheCurrent(){
    	refreshBitmapCache(mCurrentPage); 	
    }
}
