package com.lenovo.launcher.components.XAllAppFace;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.XPagedView.PageSwitchListener;
import com.lenovo.launcher.components.XAllAppFace.slimengine.BaseDrawableGroup;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;
import com.lenovo.launcher2.commoninterface.Alarm;
import com.lenovo.launcher2.commoninterface.OnAlarmListener;
import com.lenovo.launcher2.customizer.Debug.R5;

public class XPagedViewIndicator extends BaseDrawableGroup implements
		PageSwitchListener, XDropTarget {

	public XPagedViewIndicator(XContext context, RectF region) {
		super(context);		
		resize(region);
		mApp = (LauncherApplication) mContext.getContext().getApplicationContext();

		initData();
	}

	@Override
	public void onPageSwitching(int from, int to, float percentage) {
		if (isVisible()) {
			changePageFromTo(from, to, percentage);
		}
		
//		if (mPageTextView != null && mPageTextView.isVisible())
//		{
//			mPageTextView.scrollToPage(to, percentage, mPageCount, xPagedView.isLoop());
//		}
	}
    @Override
    public void resize(RectF rect) {
    	int width = (int)getWidth();
    	super.resize(rect);
    	int newWidth = (int)getWidth();
    	if (width != newWidth)
    	{
    		calculateGap();
    	}
//    	mIndicator.setRelativeX((getWidth() - mIndicator.getWidth()) / 2);
    }
	/*** fixbug AUT: zhaoxy . DATE: 2012-12-10 . START ***/
	// boolean firstInit = true;

	@Override
	public void onUpdatePage(int pageCount, int currentPage) {
		initIndicator(pageCount, currentPage);
		if (mPageTextView != null)
		{
			mPageTextView.generateSnapBitmap(pageCount);
		}
		mPageCount = pageCount;
	}
	
	/*** fixbug AUT: zhaoxy . DATE: 2012-12-10 . END ***/

	public void setShowingState(boolean show) {
		setVisibility(show);
	}

//	public PageIndicator getIndicators() {
//		return mIndicator;
//	}

	@Override
	public void onPageBeginMoving(int currentPage) {
	}

	@Override
	public void onPageEndMoving(int currentPage) {
	}
	
//	@Override
//	public void clean() {
//		mIndicator.clean();
//		super.clean();
//	}
	
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
    	if (mDebug)R5.echo("XPagedViewIndicator onSingleTapUp");
        if (xPagedView != null)
        {
            int i = getPageBuyPoint((int)e.getX());
            xPagedView.scrollToPage(i);
        }        
                
        return super.onSingleTapUp(e);
    }
        
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
            float distanceY, float previousX, float previousY) {
    	if (mDebug)R5.echo("XPagedViewIndicator onScroll");
        if (xPagedView != null && mState == STATE_ENTER)
        {
//          xPagedView.onScroll(e1, e2, distanceX * factor, distanceY);
        	scrollToPageByX(e2.getX());
            return true;
        } 
        
        return false;
    }
    
    private void scrollToPageByX(float x){
    	xPagedView.setIndicatorScroll(true);
    	int i = getPageBuyPointWhenEnter((int)x);
      
      	float offsetX = -(x - mEnterPadding - i * ( mEnterPointWidth + mEnterPointGap ))/ (mEnterPointWidth + mEnterPointGap);
      
//      	if (mDebug)R5.echo("i = " + i + "offsetX = " + offsetX);
      	xPagedView.scrollToPage(i, offsetX);
//      	mPageTextView.scrollToPage(i, offsetX, mPageCount, xPagedView.isLoop());
//      	updateScrollingIndicatorPosition(xPagedView.mCurrentPage, xPagedView.mOffsetX);
    }
    
    private int mPageCount;
    private XPagedView xPagedView;
    private static int ANIMATION_TIME = 200;
//    private float factor;
    private boolean mDebug = true;
    public void setPagedView(XPagedView pagedView) {
        xPagedView = pagedView;
    }
    
    @Override
    public boolean onFingerUp(MotionEvent e) {
    	if (mDebug)R5.echo("XPagedViewIndicator onFingerUp");
    	setExtraTouch(false);
        startNormalAnimation();
        return super.onFingerUp(e);
    }
    
//    @Override
//    public void onTouchCancel() {
//    	R5.echo("XPagedViewIndicator onTouchCancel");
//        if (xPagedView != null)
//        {
//            xPagedView.setIndicatorScroll(false);
//            xPagedView.onTouchCancel();
//        } 
//        
//        startNormalAnimation();
//        super.onTouchCancel();
//    }
    
    //add by zhanggx1 on 2013-07-03.s
//    public void setSingleIndicatorVisible(boolean visible) {
//    	if (mIndicator != null) {
//    		mIndicator.setSingleIndicatorVisible(visible);
//    	}
//    }
    //add by zhanggx1 on 2013-07-03.e
    
	@Override
	public boolean isDropEnabled() {
		// TODO Auto-generated method stub
		return isVisible() && isEnterEnabel();
	}

	@Override
	public void onDrop(XDragObject dragObject) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDragEnter(XDragObject dragObject) {
		// TODO Auto-generated method stub
		if (mDebug)R5.echo("XPagedViewIndicator onDragEnter");		
	}

	@Override
	public void onDragOver(XDragObject dragObject) {
		// TODO Auto-generated method stub
		if (mDebug)R5.echo("XPagedViewIndicator onDragOver x = " + dragObject.x + "y = " + dragObject.y);
//		mDragViewVisualCenter = getDragViewVisualCenter(dragObject.x, dragObject.y, dragObject.xOffset, dragObject.yOffset,
//                dragObject.dragView, mDragViewVisualCenter);		
		
		mDragViewVisualCenter[0] = dragObject.x;
		mDragViewVisualCenter[1] = dragObject.y;
		
        if (mPageTextView.isVisible())
        {
        	showPageCount();
        } 
        else
        {
        	if(!mPageScrollAlarm.alarmPending() && xPagedView != null) {
        		
        		if (mDebug)R5.echo("setOnAlarmListener");

            	mPageScrollAlarm.setOnAlarmListener(new OnAlarmListener(){

					@Override
					public void onAlarm(Alarm alarm) {
						// TODO Auto-generated method stub
						if (mDebug)R5.echo("onAlarm");
						startPageTextVisibleAnimation();
			            
			            showPageCount();
					}
                	
                });
            	mPageScrollAlarm.setAlarm(PAGE_SCROLL_TIMEOUT);
            }
        }
	}

	@Override
	public void onDragExit(XDragObject dragObject) {
		// TODO Auto-generated method stub
		if (mPageTextView.isVisible())
		{
			startPageTextInVisibleAnimation();
		}
		if (mDebug)R5.echo("cancelAlarm");
		mPageScrollAlarm.cancelAlarm();
	}

	@Override
	public XDropTarget getDropTargetDelegate(XDragObject dragObject) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean acceptDrop(XDragObject dragObject) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void getHitRect(Rect outRect) {
		// TODO Auto-generated method stub
		outRect.set(0, 0, (int) getWidth(), (int) getHeight());
	}

	@Override
	public void getLocationInDragLayer(int[] loc) {
		// TODO Auto-generated method stub
		((XLauncher) mContext.getContext()).getDragLayer().getLocationInDragLayer(this, loc);
	}

	@Override
	public int getLeft() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTop() {
		// TODO Auto-generated method stub
		return 0;
	}
	
    private float[] getDragViewVisualCenter(int x, int y, int xOffset, int yOffset,
            XDragView dragView, float[] recycle) {
        float res[];
        if (recycle == null) {
            res = new float[2];
        } else {
            res = recycle;
        }

        // First off, the drag view has been shifted in a way that is not represented in the
        // x and y values or the x/yOffsets. Here we account for that shift.
//        x += mXContext.getResources().getDimensionPixelSize(R.dimen.dragViewOffsetX);
//        y += mXContext.getResources().getDimensionPixelSize(R.dimen.dragViewOffsetY);

        // These represent the visual top and left of drag view if a dragRect was provided.
        // If a dragRect was not provided, then they correspond to the actual view left and
        // top, as the dragRect is in that case taken to be the entire dragView.
        // R.dimen.dragViewOffsetY.
        int left = x - xOffset;
        int top = y - yOffset;
        		
        // In order to find the visual center, we shift by half the dragRect
        res[0] = left + dragView.getDragRegion().width() / 2;
        res[1] = top + dragView.getDragRegion().height() / 2;

        return res;
    }
    
    private float[] mDragViewVisualCenter = new float[2];
    private XScrollTextView mPageTextView;
    
    private final Alarm mPageScrollAlarm = new Alarm();
    static int PAGE_SCROLL_TIMEOUT = 300;
    
    public void setTextView(XScrollTextView textView){
    	mPageTextView = textView;
    }
    
    public void showPageCount(){
    	int i = getPageBuyPoint((int)mDragViewVisualCenter[0]);
        xPagedView.scrollToPage(i);        
    }
    
//    public void startEnterAnimation(){
//    	mIndicator.startEnterAnimation();
//    }
    


	final private LauncherApplication mApp;

	public int homePointWidth;
	private int homePointHeight;
	private int homePointGap;
	private int mOrignHomePointGap;
	private int indicator_padding_top=0;
	private int mEnterHomePointHeight;
	private boolean mEnterEnable = false;
	
	public int getHomePointGap() {
		return homePointGap;
	}

	private void initData() {
		mNormalPointWidth = homePointWidth = mApp.mLauncherContext.getDimensionPixel(
				R.dimen.home_point_width, R.dimen.def__home_point_width);
		homePointHeight = mApp.mLauncherContext.getDimensionPixel(
				R.dimen.home_point_height, R.dimen.def__home_point_height);
		mOrignHomePointGap = mApp.mLauncherContext.getDimensionPixel(
				R.dimen.home_point_gap, R.dimen.def__home_point_gap);
		indicator_padding_top = mApp.mLauncherContext.getDimensionPixel(
				R.dimen.workspace_indicator_top, R.dimen.workspace_indicator_top);	
				
		homePointGap = mNormalPointGap = getGap();
		mOrignEnterPointGap = mContext.getResources().getDimensionPixelSize( R.dimen.xpage_indicator_enter_gap);
		
		mEnterHeight = mSmallHeight = mContext.getResources().getDimensionPixelSize( R.dimen.xpage_indicator_enter_height);
//    	mEnterWidth = 3;  
        mBigHeight = homePointHeight > 2*mSmallHeight ? 2*mSmallHeight : homePointHeight;
	}

	private int currentPage = 0;

	//private IndicatableItem travelor = null;
	private ArrayList<IndicatableItem> indicators = new ArrayList<IndicatableItem>();

	public int getCurrentPage() {
		return currentPage;
	}

//	public void addIndicatorCell(int count) {
//		//for bug 12943
////		initIndicator();
//
//		for (int i = 0; i < count; i++) {
//		    addOneIndicatorCell(i);
//		}
//		//add by zhanggx1 on 2013-07-03.s
//        this.setVisibility(mSingleVisible || indicators.size() > 1);
//        //add by zhanggx1 on 2013-07-03.e
//	}
	
	public void addOneIndicatorCell(int i) {
        
        final float pointX = mPadding + homePointWidth * indicators.size();
        final RectF r = new RectF(homePointGap * i + pointX, indicator_padding_top,
                homePointGap * i + pointX + homePointWidth, indicator_padding_top + homePointHeight);
        IndicatableItem ir = new ThemeStyleIndicator(getXContext(), mApp.mLauncherContext,
                r, homePointWidth, homePointHeight);
        ir.resize(r);
        ir.page = i;
        this.addItem((DrawableItem) ir);
        indicators.add(ir);
    }

	public void changePageFromTo(int from, int to, float deltaTravel) {
		if (indicators == null) {
			return;
		}
		int len = indicators.size();
		if (from >= len || to >= len || from < 0 || to < 0) {
			return;
		}

		if (from == to) {
			resetTo(from);
			return;
		}
		
		boolean useAnim = true;
		if (mState != STATE_NORMAL)
		{
			useAnim = false;
		}

		 android.util.Log.i("page", "update page from : " + from + " to " +
		 to);
		try {
			if(indicators.get(from) != null){
				indicators.get(from).onHide(useAnim);
			}
			//if (travelor != null) {
				// travelFromTo(from, to, deltaTravel);
			//} else {
				if(indicators.get(to) != null){
					indicators.get(to).onShow(useAnim);
				}
			//}
		} catch (Exception e) {
		}

        /* RK_ID: RK_ALLAPPS. AUT: liuli1 . DATE: 2013-06-03 . START */
        // fix bug 14113
        if (len > to) {
            for (int i = 0; i < len; i++) {
                if (i != to) {
                    indicators.get(i).onHide(true);
                }
            }
        }
        /* RK_ID: RK_ALLAPPS. AUT: liuli1 . DATE: 2013-06-03 . END */
	}

	/*** fixbug AUT: zhaoxy . DATE: 2012-12-10 . START ***/
	public void initIndicator(int pageCount, int currentPage) {
		if (indicators == null) {
			return;
		}
		
		int i;
		int size = indicators.size();
		if (size > pageCount)
		{
		    for (i = size - 1; i > pageCount - 1; i--)
		    {
		        indicators.remove(i);
		        removeItem(i);
		    }		    
		}
		else if (size < pageCount)
		{
		    for (i = size; i < pageCount; i++)
		    {
		        addOneIndicatorCell(i);
		    }		    
		}
		//add by zhanggx1 on 2013-07-03.s
        this.setVisibility(mSingleVisible || indicators.size() > 1);
        //add by zhanggx1 on 2013-07-03.e
				
		calculateGap();
//		indicators.clear();
//		clearAllItems();
//		addIndicatorCell(pageCount);
		
		resetTo(currentPage);		
	}
	/*** fixbug AUT: zhaoxy . DATE: 2012-12-10 . END ***/

	public void resetTo(int toPage) {
		if (toPage < 0 || indicators == null) {
			return;
		}

		android.util.Log.i("page", "reset to page : " + toPage);
		
		boolean useAnim = true;
		if (mState != STATE_NORMAL)
		{
			useAnim = false;
		}
		int len = indicators.size();
		if (len > toPage) {
			for (int i = 0; i < len; i++) {
				if (i != toPage) {
					indicators.get(i).onHide(useAnim);
				} else {
					indicators.get(i).onShow(useAnim);
				}
			}
		}
	}
	
	public void updateTheme(){
		initData();
		if (mEnterEnable){
			setEnterEnabel();
		}
		calculateGap();
		reArrangeChild();
	}
	
	@Override
	public void clean() {
		if (indicators != null) {
			int count = indicators.size();
			for (int i = 0; i < count; i++){
		    	IndicatableItem item = indicators.get(i);
		        item.clean();
		    }
			indicators.clear();
//			indicators = null;
		}
		super.clean();
	}
	
    private void reArrangeChild() {    	
        if (indicators == null)
        {
            return;
        }
        
//        if (mDebug)R5.echo("homePointWidth = " + homePointWidth + "homePointGap = " + homePointGap + "mPadding = " + mPadding);
                
        int num = indicators.size();
        for (int i = 0; i < num; i++){
            IndicatableItem item = indicators.get(i);
//            item.updateTheme(mOrignHomePointWidth, homePointHeight, i == 0);
            
            float left = mPadding + (homePointGap + homePointWidth) * i;
            final RectF r = new RectF(left, indicator_padding_top,
                    left + homePointWidth, indicator_padding_top + homePointHeight);
            item.resize(r);
        }
        
    }
    
    private int getGap() {        
        if (indicators == null)
        {
            return mOrignHomePointGap;
        }
        
        int num = indicators.size();
        int gap;
        int width =  (mOrignHomePointGap + mNormalPointWidth ) * num + mOrignHomePointGap;
        
        if (width > getWidth() && num > 1)
        {
            gap = (int)(getWidth() - mNormalPointWidth * num) / (num + 1);
//            if (gap < 0)
//            {
//                gap = 0;   
//            }            
        }
        else
        {
            gap = mOrignHomePointGap;
        }
        
        return gap;
    }
    
    public void calculateGap() {
        int oldGap = homePointGap;
        int oldWidth = mShowWidth;
        int oldPadding = mPadding;
        
        homePointGap = mNormalPointGap = getGap();
        if (mDebug)R5.echo("mNormalPointGap = " + mNormalPointGap);
    	mShowWidth =  (homePointGap + homePointWidth ) * indicators.size() - homePointGap;
//		if (xPagedView != null)
//		{
//		    factor =  - xPagedView.getWidth() * xPagedView.getPageCount()  / mShowWidth;
//		}
        mPadding = (int)(getWidth() - mShowWidth) / 2 ;
        
        if (oldGap != homePointGap || oldWidth != mShowWidth || oldPadding != mPadding)
        {
            reArrangeChild();
        }
    }
    
    //add by zhanggx1 on 2013-07-03.s
  	private boolean mSingleVisible = true;
    public void setSingleIndicatorVisible(boolean visible) {
      	mSingleVisible = visible;
      	this.setVisibility(mSingleVisible || indicators.size() > 1);
    }
    //add by zhanggx1 on 2013-07-03.e

    public boolean isSingleVisible() {
        return mSingleVisible;
    }
    
	@Override
	public boolean onLongPress(MotionEvent e) {
    	if (!mEnterEnable)
    	{
    		return false;
    	}
    	if (mDebug)R5.echo("onLongPress");
    	setExtraTouch(true);
    	startPageTextVisibleAnimation();		
		interruptLongPressed();
		startEnterAnimation();
		int i = getPageBuyPointWhenEnter((int)e.getX());
        xPagedView.scrollToPage(i); 
                
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public void onTouchCancel( MotionEvent e ) {
		// TODO Auto-generated method stub
		if (mDebug)R5.echo("XPagedViewIndicator onTouchCancel");
		setExtraTouch(false);
		startNormalAnimation();
		super.onTouchCancel( e );
	}
	
	@Override
	public boolean onFingerCancel(MotionEvent e) {
		// TODO Auto-generated method stub
		if (mDebug)R5.echo("XPagedViewIndicator onFingerCancel");
		startNormalAnimation();
		return super.onFingerCancel(e);
	}
	
	private int mEnterPointGap;
	private int mOrignEnterPointGap;
	private int mEnterPadding;
	private int mEnterShowWidth;
	private int mPadding = 0;
	private int mShowWidth = 0;
	private int mNormalPointGap;
	private ValueAnimator mAnimator;
	
	private static final int STATE_NORMAL = 0;
	private static final int STATE_NORMAL_TO_ENTER = 1;
	static final int STATE_ENTER = 2;
	private static final int STATE_ENTER_TO_NORMAL = 3;
	private static final int STATE_WAIT_TO_ENTER = 4;
	private static final int STATE_WAIT_TO_NORMAL = 5;
	
	private int mState = STATE_NORMAL;
	
	private int mEnterPointWidth;
	private int mNormalPointWidth;
	
	private void startEnterDeployAnimation() {    	
		calculateEnterGap();
		showIndicatorsEnterState(true);
		
        if (mAnimator != null) {
        	mAnimator.cancel();
        }
        
        showIndicatorsNormalState(false);
        
        mAnimator = ValueAnimator.ofFloat(0f, 1f);
        mAnimator.setDuration(ANIMATION_TIME);
        mAnimator.setInterpolator(new DecelerateInterpolator(2.5f));
        mAnimator.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                final float percent = (Float) animation.getAnimatedValue();
                updateGapAndWidth(percent);
//                setIndicatorsNormalAlpha((int)((1 - 4 * percent) * 255));
                updateEnterPosition(percent);
            }
        });
        mAnimator.addListener(new AnimatorListenerAdapter() {
                        
            @Override
            public void onAnimationEnd(Animator animation) {
//            	startEnterIndicatorInvisbleAnmation();
            	if (mState == STATE_WAIT_TO_NORMAL)
            	{
            		mState = STATE_ENTER_TO_NORMAL;
        			startNormalLineShortenAnimation();
            	}
            	else
            	{
            		mState = STATE_ENTER;
            		//所有的页码都显示为正常。
            		indicators.get(xPagedView.getCurrentPage()).onHide(false);
            		updateScrollingIndicatorPosition(xPagedView.getCurrentPage(), 0);
            	} 
            }
        });        
       
        mContext.getRenderer().injectAnimation(mAnimator, false);
    }
	
	private void setIndicatorsAlpha(float alpha){
        int num = indicators.size();
        for (int i = 0; i < num; i++){
            IndicatableItem item = indicators.get(i);
            item.setAlpha(alpha);
        }
	}
	
//	private void setIndicatorsNormalAlpha(int alpha){
//		int num = indicators.size();
//
//        if (alpha <= 0)
//        {
//        	showIndicatorsNormalState(false);
//        	return;
//        }
//        
//        if (alpha > 255)
//        {
//        	alpha = 255;
//        }
//        
//        showIndicatorsNormalState(true);
//        
//        if (alpha >= 0f && alpha <= 1.0001f) {
//        	for (int i = 0; i < num; i++){
//                IndicatableItem item = indicators.get(i);
//                item.setNormalAlpha(alpha);
//            }
//        }        
//	}	
		
	private void showIndicatorsNormalState(boolean state){
        int num = indicators.size();
        for (int i = 0; i < num; i++){
            IndicatableItem item = indicators.get(i);
            item.showNormalState(state);
        }
	}
	
	private void showIndicatorsEnterState(boolean state){
        int num = indicators.size();
        for (int i = 0; i < num; i++){
            IndicatableItem item = indicators.get(i);
            item.showEnterState(state);
        }
	}
	
//	private void startEnterIndicatorInvisbleAnmation() {
//        if (mAnimator != null) {
//        	mAnimator.cancel();
//        }
//        mAnimator = ValueAnimator.ofFloat(0f, 1f);
//        mAnimator.setDuration(100);
//        mAnimator.addUpdateListener(new AnimatorUpdateListener() {
//            public void onAnimationUpdate(ValueAnimator animation) {
//                final float percent = 1 - (Float) animation.getAnimatedValue();
//                setIndicatorsAlpha(percent);
//            }
//        });
//        mAnimator.addListener(new AnimatorListenerAdapter() {
//                        
//            @Override
//            public void onAnimationEnd(Animator animation) {
//            	startEnterLineVisbleAnmation();
//            }
//        });        
//       
//        mContext.getRenderer().injectAnimation(mAnimator, false);
//    }	
	
//	private void startEnterLineVisbleAnmation() {
//        if (mAnimator != null) {
//        	mAnimator.cancel();
//        }
//        
//        showIndicatorsNormalState(false);
//        
//        mAnimator = ValueAnimator.ofFloat(0f, 1f);
//        mAnimator.setDuration(100);
//        mAnimator.addUpdateListener(new AnimatorUpdateListener() {
//            public void onAnimationUpdate(ValueAnimator animation) {
//                final float percent = (Float) animation.getAnimatedValue();
//                setIndicatorsAlpha(percent);
//            }
//        });
//        mAnimator.addListener(new AnimatorListenerAdapter() {
//                        
//            @Override
//            public void onAnimationEnd(Animator animation) {
//            	startEnterLineExtentAnmation();
//            }
//        });        
//       
//        mContext.getRenderer().injectAnimation(mAnimator, false);
//    }	
	
//	private void startEnterLineExtentAnmation() {
//        if (mAnimator != null) {
//        	mAnimator.cancel();
//        }
//                
//        mAnimator = ValueAnimator.ofFloat(0f, 1f);
//        mAnimator.setDuration(100);
//        mAnimator.addUpdateListener(new AnimatorUpdateListener() {
//            public void onAnimationUpdate(ValueAnimator animation) {
//                final float percent = (Float) animation.getAnimatedValue();
//                int num = indicators.size();
//                for (int i = 0; i < num; i++){
//                    IndicatableItem item = indicators.get(i);
//                    item.updateEnterPosition(percent);
//                }
//            }
//        });
//        mAnimator.addListener(new AnimatorListenerAdapter() {
//                        
//            @Override
//            public void onAnimationEnd(Animator animation) {
//            	if (mState == STATE_WAIT_TO_NORMAL)
//            	{
//            		mState = STATE_ENTER_TO_NORMAL;
//        			startNormalLineShortenAnimation();
//            	}
//            	else
//            	{
//            		mState = STATE_ENTER;
//            	}            	
//            }
//        });        
//       
//        mContext.getRenderer().injectAnimation(mAnimator, false);
//    }
	
    public void startEnterAnimation(){
    	if (!mEnterEnable)
    	{
    		return;
    	}
    	
		if (mState == STATE_NORMAL_TO_ENTER || mState == STATE_ENTER || mState == STATE_WAIT_TO_ENTER)
		{
			return;
		}
		else if (mState == STATE_ENTER_TO_NORMAL)
		{
			//等动画做完，然后再去做。
			mState = STATE_WAIT_TO_ENTER;			
		}
		else if (mState == STATE_WAIT_TO_NORMAL)
		{
			mState = STATE_NORMAL_TO_ENTER;
		}
		else if (mState == STATE_NORMAL)
		{
			mState = STATE_NORMAL_TO_ENTER;
			startEnterDeployAnimation();
		}
    }
    
    private void calculateEnterGap() {        
        if (indicators == null)
        {
            return;
        }
                
        int num = indicators.size();
        
        if (num > 1)
        {
//        	mPadding = (int)(getParent().getWidth() / num - homePointWidth) / 2;
//        	R5.echo("mPadding = " + mPadding);
//        	mEnterPointGap = (int)(getParent().getWidth() - 2 * mPadding - homePointWidth * num) / (num - 1);
        	if (mOrignEnterPointGap > homePointGap)
        	{
        		mEnterPointGap = mOrignEnterPointGap;
        	}
        	else
        	{
        		mEnterPointGap = homePointGap;
        	}
        	
        	mEnterPointWidth = ((int)getWidth() - mEnterPointGap) / num - mEnterPointGap;
        	
        	R5.echo("mOrignEnterPointGap = " + mOrignEnterPointGap + "homePointGap = " + homePointGap
        			+ "mEnterPointWidth = " + mEnterPointWidth);
        	
        	mEnterShowWidth =  (mEnterPointGap + mEnterPointWidth ) * indicators.size() - mEnterPointGap;
            mEnterPadding = (int)(getWidth() - mEnterShowWidth) / 2 ;            
        }
//        else
//        {
//        	mEnterPointGap = mOrignHomePointGap;
//        }        
        
        return;
    }
    
    public int getHomePointHeight(){
    	return homePointHeight;
    }
	
    public int getTouchHomePointHeight(){
    	return mEnterHomePointHeight;
    }
    
    public void setEnterEnabel(){
    	mEnterEnable = true;
    	mEnterHomePointHeight = mContext.getResources().getDimensionPixelSize( R.dimen.xpage_indicator_enter_touch_height);;
    	indicator_padding_top = (mEnterHomePointHeight - homePointHeight) / 2;	
//    	indicator_padding_top = (mEnterHomePointHeight - homePointHeight) / 2;
    }
    
    public boolean isEnterEnabel(){
    	return mEnterEnable;
    }
    
    public void startNormalAnimation(){        
    	if (!mEnterEnable)
    	{
    		return;
    	}
    	
		if (mPageTextView.isVisible())
		{
			startPageTextInVisibleAnimation();
		}
		if (mState == STATE_ENTER_TO_NORMAL || mState == STATE_NORMAL || mState == STATE_WAIT_TO_NORMAL)
		{
			return;
		}
		else if (mState == STATE_NORMAL_TO_ENTER)
		{
			//等动画做完，然后再去做。
			mState = STATE_WAIT_TO_NORMAL;			
		}
		else if (mState == STATE_WAIT_TO_ENTER)
		{
			mState = STATE_ENTER_TO_NORMAL;
		}
		else if (mState == STATE_ENTER)
		{
			mState = STATE_ENTER_TO_NORMAL;
	        if (xPagedView != null)
	        {
				//显示对应的页码
				indicators.get(xPagedView.getCurrentPage()).onShow(false);
	            xPagedView.setIndicatorScroll(false);
	            xPagedView.scrollToPosition();
	        } 
			startNormalLineShortenAnimation();
		}
    }
    
    private void startNormalLineShortenAnimation() {
    	if (mAnimator != null) {
        	mAnimator.cancel();
        }   	
    	                
        mAnimator = ValueAnimator.ofFloat(0f, 1f);
        mAnimator.setDuration(ANIMATION_TIME);
        mAnimator.setInterpolator(new AccelerateInterpolator(2.5f));
        mAnimator.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                final float percent = 1 - (Float) animation.getAnimatedValue();                
                updateGapAndWidth(percent);
//                setIndicatorsNormalAlpha((int)((1 - 4 * percent) * 255));
                updateEnterPosition(percent);
            }
        });
        mAnimator.addListener(new AnimatorListenerAdapter() {
                        
            @Override
            public void onAnimationEnd(Animator animation) {
//            	startNormalLineInVisbleAnmation();
            	showIndicatorsEnterState(false);
            	showIndicatorsNormalState(true);
            	if (mState == STATE_WAIT_TO_ENTER)
            	{
            		mState = STATE_NORMAL_TO_ENTER;
            		startEnterDeployAnimation();
            	}
            	else
            	{
            		mState = STATE_NORMAL;
            	} 
            }
        });        
       
        mContext.getRenderer().injectAnimation(mAnimator, false);
//    	mState = STATE_NORMAL;
//    	calculateGap();
//    	homePointWidth = mOrignHomePointWidth;
//        int num = indicators.size();
//        for (int i = 0; i < num; i++){
//            IndicatableItem item = indicators.get(i);
//            item.showNormalState(true);
//        }
    }
    
//    private void startNormalLineInVisbleAnmation() {
//        if (mAnimator != null) {
//        	mAnimator.cancel();
//        }
//                
//        mAnimator = ValueAnimator.ofFloat(0f, 1f);
//        mAnimator.setDuration(100);
//        mAnimator.addUpdateListener(new AnimatorUpdateListener() {
//            public void onAnimationUpdate(ValueAnimator animation) {
//                final float percent = 1 - (Float) animation.getAnimatedValue();
//                setIndicatorsAlpha(percent);
//            }
//        });
//        mAnimator.addListener(new AnimatorListenerAdapter() {
//                        
//            @Override
//            public void onAnimationEnd(Animator animation) {
//            	startNormalIndicatorVisbleAnmation();
//            }
//        });        
//       
//        mContext.getRenderer().injectAnimation(mAnimator, false);
//    }
//    
//    private void startNormalIndicatorVisbleAnmation() {
//        if (mAnimator != null) {
//        	mAnimator.cancel();
//        }
//        
//        showIndicatorsNormalState(true);
//        
//        mAnimator = ValueAnimator.ofFloat(0f, 1f);
//        mAnimator.setDuration(100);
//        mAnimator.addUpdateListener(new AnimatorUpdateListener() {
//            public void onAnimationUpdate(ValueAnimator animation) {
//                final float percent = (Float) animation.getAnimatedValue();
//                setIndicatorsAlpha(percent);
//            }
//        });
//        mAnimator.addListener(new AnimatorListenerAdapter() {
//                        
//            @Override
//            public void onAnimationEnd(Animator animation) {
//            	startNomalGatherAnimation();
//            }
//        });        
//       
//        mContext.getRenderer().injectAnimation(mAnimator, false);
//    }	
//	
//	private void startNomalGatherAnimation() {
//		calculateEnterGap();
//        if (mAnimator != null) {
//        	mAnimator.cancel();
//        }
//        mAnimator = ValueAnimator.ofFloat(1f, 0f);
//        mAnimator.setDuration(100);
//        mAnimator.addUpdateListener(new AnimatorUpdateListener() {
//            public void onAnimationUpdate(ValueAnimator animation) {
//                final float percent = (Float) animation.getAnimatedValue();
//                updateGapAndWidth(percent);
//            }
//        });
//        mAnimator.addListener(new AnimatorListenerAdapter() {
//                        
//            @Override
//            public void onAnimationEnd(Animator animation) {
//            	if (mState == STATE_WAIT_TO_ENTER)
//            	{
//            		mState = STATE_NORMAL_TO_ENTER;
//            		startEnterDeployAnimation();
//            	}
//            	else
//            	{
//            		mState = STATE_NORMAL;
//            	} 
//            }
//        });        
//       
//        mContext.getRenderer().injectAnimation(mAnimator, false);
//    }
	
	private void updateGapAndWidth(float percent)
	{   
		int gap = (int)((mEnterPointGap - mNormalPointGap) * percent) + mNormalPointGap;
        int width = (int)((mEnterPointWidth - mNormalPointWidth) * percent) + mNormalPointWidth;
        if (gap != homePointGap || width != homePointWidth)
        {
            homePointGap = gap;
            homePointWidth = width;
            mShowWidth =  (homePointGap + homePointWidth ) * indicators.size() - homePointGap;
//    		if (xPagedView != null)
//    		{
//    		    factor =  - xPagedView.getWidth() * xPagedView.getPageCount()  / mShowWidth;
//    		}
            mPadding = (int)(getWidth() - mShowWidth) / 2 ;
            R5.echo("mPadding = " + mPadding);
                        
            reArrangeChild();
            
            if (indicators != null)
            {
//                localRect.right = localRect.left + (homePointWidth + homePointGap) * indicators.size() - homePointGap; 
//                setRelativeX((getParent().getWidth() - getWidth()) / 2);
            }
        }
	}
	
	private int getPageBuyPoint(int x){
		int i = (int)((x - mPadding ) *  mPageCount / mShowWidth);
		if (i < 0)
		{
			i = 0;
		}
		else if (i > mPageCount - 1)
		{
			i = mPageCount - 1;
		}
		
		return i;
	}
    
	private int getPageBuyPointWhenEnter(int x){
		int i = (int)((x - mEnterPadding ) *  mPageCount / mEnterShowWidth);
		if (i < 0)
		{
			i = 0;
		}
		else if (i > mPageCount - 1)
		{
			i = mPageCount - 1;
		}
		
		return i;
	}
	
	private ValueAnimator mTextViewVisibleAnimator;
	private ValueAnimator mTextViewInVisbleAnimator;
	
	private void startPageTextVisibleAnimation() {    	
		mPageTextView.setVisibility(true);
		mPageTextView.setText((xPagedView.getCurrentPage() + 1) + "");
		R5.echo("mPageTextView setText " + xPagedView.getCurrentPage());
        if (mTextViewInVisbleAnimator != null) {
        	mTextViewInVisbleAnimator.cancel();
        }
        
        if (mTextViewVisibleAnimator != null && mTextViewVisibleAnimator.isRunning())
        {
        	return;
        }
        
        mTextViewVisibleAnimator = ValueAnimator.ofFloat(0f, 1f);
        mTextViewVisibleAnimator.setDuration(ANIMATION_TIME);
        mTextViewVisibleAnimator.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                final float percent = (Float) animation.getAnimatedValue();
                mPageTextView.setAlpha(percent);
            }
        });
        mTextViewVisibleAnimator.addListener(new AnimatorListenerAdapter() {
                        
            @Override
            public void onAnimationEnd(Animator animation) {

            }
        });        
       
        mContext.getRenderer().injectAnimation(mTextViewVisibleAnimator, false);
    }
	
	private void startPageTextInVisibleAnimation() {
        if (mTextViewVisibleAnimator != null) {
        	mTextViewVisibleAnimator.cancel();
        }
        
        if (mTextViewInVisbleAnimator != null && mTextViewInVisbleAnimator.isRunning())
        {
        	return;
        }
        
        mTextViewInVisbleAnimator = ValueAnimator.ofFloat(1f, 0f);
        mTextViewInVisbleAnimator.setDuration(ANIMATION_TIME);
        mTextViewInVisbleAnimator.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                final float percent = (Float) animation.getAnimatedValue();
//                R5.echo("mPageTextView.setAlpha = " + percent);
                mPageTextView.setAlpha(percent);
            }
        });
        mTextViewInVisbleAnimator.addListener(new AnimatorListenerAdapter() {
                        
            @Override
            public void onAnimationEnd(Animator animation) {
            	R5.echo("mPageTextView.setVisibility false");
            	mPageTextView.setVisibility(false);	
            }
        });        
       
        mContext.getRenderer().injectAnimation(mTextViewInVisbleAnimator, false);
    }
	
	private int mTransX;
	public void updateScrollingIndicatorPosition(int page, float offsetX) {
		int left = mPadding + (homePointGap + homePointWidth) * (page);
				
		mTransX = left + mIndicatorPadding + (int)((- offsetX) * mEnterWidth);
	}
	
	@Override
	public void onDraw(IDisplayProcess c) {
		// TODO Auto-generated method stub
		super.onDraw(c);
		
		if (mState == STATE_ENTER)
		{
			RectF rect = new RectF();
			rect.set(mTransX, indicator_padding_top + mEnterDrawY, mTransX + mEnterWidth, indicator_padding_top + mEnterDrawY + mEnterHeight);
			c.drawDrawable(ThemeStyleIndicator.getShowingDrawable(), rect);
		}
	}
	
	int mEnterDrawX = 0;
	int mEnterDrawY = 0;
	int mEnterWidth = 0;
	int mEnterHeight = 0;
	int mIndicatorPadding = 0;
//	int mSmallWidth = 0;
//	int mBigWidth = 0;
	int mSmallHeight = 0;
	int mBigHeight = 0;
	RectF mEnterDrawRect = new RectF();
	
    public void updateEnterPosition(float percent){
    	mEnterWidth = (int)(homePointWidth - mIndicatorPadding * 2);
//    	mEnterDrawX = (int)((homePointWidth - mEnterWidth) * .5f);
    	mEnterDrawX = mIndicatorPadding;
    	mEnterHeight = (int)((mSmallHeight - mBigHeight) * percent) + mBigHeight;
    	mEnterDrawY = (int)((homePointHeight - mEnterHeight) * .5f);
    	
    	mEnterDrawRect.set(mEnterDrawX, mEnterDrawY, mEnterDrawX + mEnterWidth, mEnterDrawY + mEnterHeight);
    }
    
    public RectF getEnterDrawRect(){
    	return mEnterDrawRect;
    }
    
    public int getState(){
    	return mState;
    }
    
    RectF extraTouchRegionForPagedView;
    
//	@Override
//	public boolean onDown(MotionEvent e) {
////		R5.echo("onDown " + this);
//		setExtraTouch(true);				
//		return super.onDown(e);
//	}
	
	private void setExtraTouch(boolean extra){
    	if (!mEnterEnable)
    	{
    		return;
    	}
    	
		if (extra)
		{
			if( extraTouchRegionForPagedView == null || extraTouchRegionForPagedView.isEmpty()){
				DisplayMetrics m = getXContext().getContext().getResources().getDisplayMetrics();
				extraTouchRegionForPagedView = new RectF( 0, 0, m.widthPixels, m.heightPixels );
			}
			
			setExtraTouchBounds( extraTouchRegionForPagedView );
			desireTouchEvent(true);
		}
		else
		{
			resetTouchBounds();
			desireTouchEvent(false);
		}
	}
}