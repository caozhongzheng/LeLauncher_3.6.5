package com.lenovo.launcher.components.XAllAppFace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.XDragController.XDragListener;
import com.lenovo.launcher.components.XAllAppFace.XScreenPagedView.OnFocusCellChangedListener;
import com.lenovo.launcher.components.XAllAppFace.slimengine.BaseDrawableGroup;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.NormalDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;
import com.lenovo.launcher2.commoninterface.Alarm;
import com.lenovo.launcher2.commoninterface.FolderInfo;
import com.lenovo.launcher2.commoninterface.ItemInfo;
import com.lenovo.launcher2.commoninterface.LauncherAppWidgetInfo;
import com.lenovo.launcher2.commoninterface.LauncherService;
import com.lenovo.launcher2.commoninterface.LauncherSettings;
import com.lenovo.launcher2.commoninterface.LenovoWidgetViewInfo;
import com.lenovo.launcher2.commoninterface.OnAlarmListener;
import com.lenovo.launcher2.commoninterface.ShortcutInfo;
import com.lenovo.launcher2.commonui.LauncherAppWidgetHost;
import com.lenovo.launcher2.commonui.LeAlertDialog;
import com.lenovo.launcher2.customizer.Reaper;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.Debug.R5;

public class XScreenMngView extends BaseDrawableGroup implements
		DrawableItem.OnClickListener, DrawableItem.OnLongClickListener,
		XDragSource, XDragListener, XScrollDropTarget {
	
    private XScreenPagedView xPagedView;
    private XContext mXContext;
    private XLauncher mXLauncher;
    
    private int mCountX = 3;
    private int mCountY = 1;
    private static final int MAX_CELLY = 3;
    private static final int MIN_CELLY = 1;
    
    private LauncherService mLauncherService;
    private ArrayList<PreviewInfo> previews;
    
    private int MAX_CELLCOUNT = 9;
    private boolean mEnableClickHomePage;
    private int mNumPages;
    
    public static final String CELLLAYOUT_COUNT = "com.lenovo.launcher2.celllayoutCount";
    private static final String CHANGE_HOMEPAGE = "CHANGE_HOMEPAGE";
    
    private static final String ACT_REMOVE_SCREEN = "delImage";
    private static final String ACT_ADD_SCREEN = "addImage";
    private static final String ACT_PREVIEW_SCREEN = "previewImage";
    private static final String ACT_SET_HOME = "homeImage";
    
    private Bitmap mEmptyBitmap = null;
    private Bitmap mAddBitmap = null;
    private Bitmap mAddPressedBitmap = null;
    private Bitmap mAddMarkBitmap = null;
    private Bitmap mDelBitmap = null;
    private Bitmap mHomeBitmap = null;
    private Bitmap mNotHomeBitmap = null;
    private Drawable mCurrentBg = null;
    private Drawable mFullBg = null;
    
    private boolean isClickDelButton;
    private boolean isFinishDel = true;
    private LeAlertDialog mAlertDialog;
    
    private int[] mEmptyCell = new int[3];
    
    private XDragController mDragController;
    private final int[] mTmpPoint = new int[2];
    
    private boolean currentDragIsDefaultPage;
    private int currentDragViewOldIndex;
    private DrawableItem mCurrentDragView;
    private boolean isonDrop;
    boolean isCurrentDrag = true;
    
    private Alarm mReorderAlarm = new Alarm();
    private Alarm mOnExitAlarm = new Alarm();
    private Alarm mAddNewItemAlarm = new Alarm();
    private Alarm mIntoWorkspaceAlarm = new Alarm();
    private int[] mTargetCell = new int[3];
    private int[] mPreviousTargetCell = new int[3];
    private static final int REORDER_ANIMATION_DURATION = 230;
    private static final int BEGIN_REORDER_DURATION = 150;
    
    private static final float SCALE_RATE = 1.00f;
    private static final float ORIGINAL_SCALE_RATE = 1.0f;
    
    // temporary solution
    private static final int SET_LONG_CLICK_LISTENER = 1000;
    private Alarm mLongClickAlarm = new Alarm();
    private int mPagePaddingLeft;
    private int mPagePaddingTop;
    private int mPagePaddingRight;
    private int mPagePaddingBottom;
    private int mWorkspaceCurr = -1;
//    private int mImageWidth = 0;
//    private int mImageHeight = 0;
    private int mThumbGap = 0;
    
    private int mDragRegionHeight;
    private DrawableItem mDragBtn;
    private XPagedViewIndicator xPageIndicator;
    private boolean isDragExit = false;

    public enum State {
        NORMAL, ADDED
    };

    private State mState = State.NORMAL;
    // tmp edition s
    private XSlidingDrawer mSlidingDrawer;
 // tmp edition e
    private ValueAnimator mAlphaAnimator;
    private ValueAnimator mDisappearAnimator;

    private float mWorkspaceScale;
    private float mWorkspaceTranX;
    private static final String TAG = "XScreenMngView";

    private ArrayList<Bitmap> mSnapList;
    private long mInitTime = -1;
    private HashMap<Long, Runnable> mPendingRunningList = new HashMap<Long, Runnable>();
    
    private Toast mRemoveMainMsg;
    private Toast mRemoveNotEmptyMsg;
    
    private long mBeginMoveTime = 0;
    private long mMoveDuration = 0;

	public XScreenMngView(XContext context, RectF region) {
        super(context);
                
        mXContext = context;
        mXLauncher = (XLauncher) context.getContext();
        mLauncherService = mXLauncher.getLauncherService();
        
        initConstants(region);
        
        resize(region);
        
        init();
    }
	
	private void initConstants(final RectF region) {
		mPagePaddingLeft = mXLauncher.getResources().getDimensionPixelOffset(R.dimen.xscreen_mng_padding_left);
        mPagePaddingTop = mXLauncher.getResources().getDimensionPixelOffset(R.dimen.xscreen_mng_padding_top);
        mPagePaddingRight = mXLauncher.getResources().getDimensionPixelOffset(R.dimen.xscreen_mng_padding_right);
        mPagePaddingBottom = mXLauncher.getResources().getDimensionPixelOffset(R.dimen.xscreen_mng_padding_bottom);
        mDragRegionHeight = mXLauncher.getResources().getDimensionPixelSize(R.dimen.xscreen_mng_drag_region_height);
        mThumbGap = mXContext.getResources().getDimensionPixelOffset(R.dimen.xscreen_mng_height_gap);
        
        //for bug 20121, 如果CELL的宽度不是个能整除mCountX的数的话，在经过很多计算后，会有1像素的偏差，导致滑动前和滑动后的位置不一样，给人晃动的视觉现象
        int size = ((int)region.width() - mPagePaddingLeft - mPagePaddingRight);
        int factor = size % mCountX;
        if (factor != 0) {
        	size -= factor;
        	int extraPadding = (int)region.width() - size;
        	mPagePaddingLeft = extraPadding / 2;
        	mPagePaddingRight = extraPadding - mPagePaddingLeft;
        }
        //for bug 20121
	}
	
	@Override
	public void resize(RectF rect) {
		super.resize(rect);
		
		initConstants(rect);
		
		resizePagedView(rect);

		// tmp edition s
        if (mSlidingDrawer != null && !mSlidingDrawer.isRecycled()) {
            final float right = rect.width();
            mSlidingDrawer.resize(new RectF(0, 0, right, getSlidingDrawerHeight()));
            mSlidingDrawer.setRelativeY(isAddState());
        }
     // tmp edition e
    }
	
	/**
	 * Initialize this XScreenMngView in the constructor
	 */
    public void init() {
    	//读取最大屏幕数
        final SharedPreferences preferences = mXLauncher.getSharedPreferences("com.lenovo.launcher2.PreviewScreenPagedView", Context.MODE_PRIVATE);
        MAX_CELLCOUNT = SettingsValue.getLauncherScreenMaxCount(mXLauncher);
        mEnableClickHomePage = preferences.getBoolean(CHANGE_HOMEPAGE, true);
	}
    
    /**
	 * Set the XScreenMngView every time
	 */
    public void setup(int curpage, XDragController dragController, ArrayList<Bitmap> list, boolean anim){
    	if (mInitTime == -1) {
    		mInitTime = System.currentTimeMillis();
    	}
    	//清除所有的ITEM
    	clearAllItems();
    	reuse();
    	    	
    	previews = new ArrayList<PreviewInfo>();
    	
    	mSnapList = list;
    	mAddBitmap = ((BitmapDrawable)(mXLauncher.getResources().getDrawable(R.drawable.preview_addscreen))).getBitmap();
        if(SettingsValue.isBlade8Pad()){
    	mAddBitmap = ((BitmapDrawable)(mXLauncher.getResources().getDrawable(R.drawable.preview_addscreen_8))).getBitmap();
        }
    	mAddMarkBitmap = ((BitmapDrawable)(mXLauncher.getResources().getDrawable(R.drawable.preview_addscreen_middle))).getBitmap();
    	mAddPressedBitmap = ((BitmapDrawable)(mXLauncher.getResources().getDrawable(R.drawable.preview_addscreen_light))).getBitmap();
        mDelBitmap = ((BitmapDrawable)(mXLauncher.getResources().getDrawable(R.drawable.preview_del_btn))).getBitmap();
        mHomeBitmap = ((BitmapDrawable)(mXLauncher.getResources().getDrawable(R.drawable.preview_home_btn_light))).getBitmap();
        mNotHomeBitmap = ((BitmapDrawable)(mXLauncher.getResources().getDrawable(R.drawable.preview_home_btn))).getBitmap();        
        mCurrentBg = mXLauncher.getResources().getDrawable(R.drawable.preview_border_light);
        mFullBg = mXLauncher.getResources().getDrawable(R.drawable.preview_border_full);
        //读取WORKSPACE的页数，若已到最大页，则不添加加号项，否则添加加号项
        int pageCnt = mXLauncher.getWorkspace().getPagedView().getPageCount();
		int cnt = (pageCnt == MAX_CELLCOUNT) ? pageCnt : (pageCnt + 1);
    	
    	if (mDragController == null) {
    	    mDragController = dragController;    	    
    	}
    	
    	//计算xPagedView的高度值，若为编辑页，高度为1个CELL高度，否则为3个CELL高度
    	float pageHeight = getPagedViewHeight();

    	mCountY = isAddState() ? MIN_CELLY : MAX_CELLY;
        xPagedView = new XScreenPagedView(mXContext,
                new RectF(0, 0, 
                localRect.width() - mPagePaddingRight - mPagePaddingLeft, 
                pageHeight), mXLauncher.getWorkspace().getPageCount() - 1);
    	xPagedView.setStageEnabled(false);
    	    	
    	xPageIndicator = new XPagedViewIndicator(getXContext(), new RectF(0, 0, 0, 0));
    	//若为编辑态，设置单屏页码可见；否则为不可见
    	if (isAddState()) {
    	   xPagedView.addItemSwitchListener(xPageIndicator);
    	   xPageIndicator.setSingleIndicatorVisible(true);
    	} else {
           xPagedView.addPageSwitchListener(xPageIndicator);
           xPageIndicator.setSingleIndicatorVisible(false);
    	}
    	xPageIndicator.setPagedView(xPagedView);
    	
    	
    	int page = cnt / (mCountX * mCountY);
        mNumPages = (cnt % (mCountX * mCountY)) == 0 ? page : ++page;
        
        //设置屏幕管理页不循环滚动
        xPagedView.setEnableEffect(false);
        xPagedView.setLoop(false);
        xPagedView.setScrollBackEnable(true);
        xPagedView.resetSlideAdapter();
        
    	xPagedView.setup(mNumPages, mCountX, mCountY);
    	//设置是否可以滑单个单元格
        /* RK_ID: RK_SCREENMANAGER. AUT: liuli1 . DATE: 2013-05-09 . START */
        xPagedView.setStepMode(isAddState());
        /* RK_ID: RK_SCREENMANAGER. AUT: liuli1 . DATE: 2013-05-09 . END */
        addItem(xPagedView);
        
        // initialize page indicator
        addItem(xPageIndicator);
                
        // initialize the sliding bar
        // tmp edition
        mSlidingDrawer = new XSlidingDrawer(mXContext);
        addItem(mSlidingDrawer);
     // tmp edition e

        resize(localRect);
        
        // tmp edition s
        mSlidingDrawer.setupHandleAndContent(mDragController, (!anim || this.isAddState()));
                
        if (!mSlidingDrawer.isRecycled()) {
            if (isAddState()) {
                mSlidingDrawer.open();
            } else {
                mSlidingDrawer.close();
            }

            mSlidingDrawer.animHandle(isAddState());
        }

        final XDrawerManager drawerManager = new XDrawerManager();
        mSlidingDrawer.setOnDrawerOpenListener(drawerManager);
        mSlidingDrawer.setOnDrawerCloseListener(drawerManager);
        mSlidingDrawer.setOnDrawerScrollListener(drawerManager);
     // tmp edition e

        // analyze data
        analyzeData(pageCnt, cnt);
    	
        // invalidate data
        //计算屏幕管理的当前页，即为桌面的当前页整除屏幕管理的每页格数
    	int current = curpage / (mCountX * mCountY);
    	invalidatePageData(Math.max(0, current), null);
    	
    	//屏幕编辑页，需要设置初始的屏幕选中页以及监听器
    	if (isAddState()) {
    		xPagedView.setCurrentPage(Math.max(0, current));
    		xPagedView.setOnFocusCellChangedListener(onFocusCellChangedListener);
            xPagedView.setCurrentFocusCell(curpage, true);
    	} else {
    		xPagedView.setOnFocusCellChangedListener(null);
    		xPagedView.setCurrentPage(Math.max(0, current));
    	}
    	
    	mDragController.addDropTarget(this);
    	mDragController.addDragListener(this);
    	
    	handlePendingList();
    	
    	//This is a temporary solution for long click
        //设置屏幕管理页的长按事件，若在初始时就设置上，进入时会直接响应事件
        mLongClickAlarm.cancelAlarm();
        mLongClickAlarm.setOnAlarmListener(mOnLongClickAlarmListener);
        mLongClickAlarm.setAlarm(SET_LONG_CLICK_LISTENER);
	}
    
    /**
     * analyze the data
     * @param workspacePageCnt  the page count of the workspace
     * @param itemCount  the count of the items in the xPagedView
     */
    private void analyzeData(int workspacePageCnt, int itemCount) {
    	// for bug 12016
    	if (mAddBitmap == null) {
			mAddBitmap = ((BitmapDrawable)(mXLauncher.getResources().getDrawable(R.drawable.preview_addscreen))).getBitmap();
        if(SettingsValue.isBlade8Pad()){
    	mAddBitmap = ((BitmapDrawable)(mXLauncher.getResources().getDrawable(R.drawable.preview_addscreen_8))).getBitmap();
        }
		}
    	if (mAddPressedBitmap == null) {
    	    mAddPressedBitmap = ((BitmapDrawable)(mXLauncher.getResources().getDrawable(R.drawable.preview_addscreen_light))).getBitmap();
    	}
    	if (mAddMarkBitmap == null) {
    		mAddMarkBitmap = ((BitmapDrawable)(mXLauncher.getResources().getDrawable(R.drawable.preview_addscreen_middle))).getBitmap();
    	}
    	if (mDelBitmap == null) {
    	    mDelBitmap = ((BitmapDrawable)(mXLauncher.getResources().getDrawable(R.drawable.preview_del_btn))).getBitmap();
    	}
    	if (mHomeBitmap == null) {
    	    mHomeBitmap = ((BitmapDrawable)(mXLauncher.getResources().getDrawable(R.drawable.preview_home_btn_light))).getBitmap();
        }
    	if (mNotHomeBitmap == null) {
    	    mNotHomeBitmap = ((BitmapDrawable)(mXLauncher.getResources().getDrawable(R.drawable.preview_home_btn))).getBitmap();
    	}
    	
    	previews.clear();
    	for (int i = 0; i < itemCount; i++) {
    		PreviewInfo info = new PreviewInfo();
    		info.mDelBitmap = mDelBitmap;
    		info.mHomeBitmap = i == mXLauncher.getWorkspace().getDefaultPage() ? mHomeBitmap : mNotHomeBitmap;
    		try {
            	info.mThumbnail = mSnapList.get(i);
    		} catch (ArrayIndexOutOfBoundsException e) {
        		info.mThumbnail = null;
			} catch (IndexOutOfBoundsException e) {
				info.mThumbnail = null;
			}
    		previews.add(info);
    	}
    	//设置+号页的数据
    	if (workspacePageCnt != MAX_CELLCOUNT) {
	    	PreviewInfo last = previews.get(workspacePageCnt);
	    	last.mDelBitmap = null;
	    	last.mHomeBitmap = null;
	    	last.mThumbnail = mAddBitmap;
	    	last.mMiddleBitmap = mAddMarkBitmap;
    	}
    }
    
    public class PreviewInfo extends ItemInfo{
    	public Bitmap mThumbnail;
    	public Bitmap mDelBitmap;
    	public Bitmap mHomeBitmap;
    	public Bitmap mMiddleBitmap;
    	
		PreviewInfo(){
			super();
		}
		public PreviewInfo(PreviewInfo info){
			super(info);
			mThumbnail = info.mThumbnail;
			mDelBitmap = info.mDelBitmap;
			mHomeBitmap = info.mHomeBitmap;
			mMiddleBitmap = info.mMiddleBitmap;
		}
		public PreviewInfo makePreviewInfo(){
			return new PreviewInfo(this);
		}
	}
    
    /**
     * Invalidate the data after analyzing the data
     * @param currPage  the current page number of the xPagedView
     */
    private void invalidatePageData(int currPage, HashMap<Integer, XPagedViewItem> model) {
    	if (previews.isEmpty()) {
    		return;
    	}
        int startIndex = 0;
        int endIndex = previews.size();
        
        for (int i = startIndex; i < endIndex; i++) {
        	XPagedViewItem itemToAdd = createXPagedViewItem(i, model);
        	if (itemToAdd == null) {
        		return;
        	}
        	xPagedView.addPagedViewItem(itemToAdd);
        }        
        invalidate();    	
    }
    
    /**
     * Create a XPagedViewItem according to a PreviewInfo item
     * @param i  The position in the previews
     * @return XPagedViewItem 
     */
    private XPagedViewItem createXPagedViewItem(int i, HashMap<Integer, XPagedViewItem> model) {
    	int numCells = mCountX * mCountY;        
        int cellCount = mXLauncher.getWorkspace().getPagedView().getPageCount();
        XPagedViewItem itemToAdd = null;
        
        if (previews.isEmpty()) {
        	return null;
        }
        
    	PreviewInfo info = previews.get(i);
    	info.screen = i / numCells;        	
    	int index = i % numCells;
    	info.cellX = index % mCountX;
    	info.cellY = index / mCountX;
    	  
    	//创建一个VIEW
    	if (model == null) {
	        RectF rect = new RectF(0, 0, this.getCellWidth(), this.getCellHeight());
	        
	    	XScreenItemView preview = new XScreenItemView(info, rect, mXContext);
	    	XIconDrawable previewImage = preview.getThumbnailDrawable();
	    	XIconDrawable delImage = preview.getDelDrawable();
	    	XIconDrawable homeImage = preview.getHomeDrawable();
	    	
	    	preview.setTag(info);
	    	
	    	// This is the plus item
	    	if (i == previews.size() - 1 && cellCount < MAX_CELLCOUNT) {
	    		previewImage.setBackgroundDrawable(null);
	    		previewImage.setTag(ACT_ADD_SCREEN);
	    		previewImage.setOnClickListener(this);
	    		
	    		itemToAdd = new XPagedViewItem(mXContext, preview, info);
	            return itemToAdd;
	    	}
	    	
	    	// This this a common item
	    	previewImage.setOnClickListener(this);
	    	previewImage.setTag(ACT_PREVIEW_SCREEN);
	    	//若当前格的INDEX等于桌面的当前页，看此页是否已满，满了显示红框，否则显示黄框
	    	if(i == mXLauncher.getWorkspace().getCurrentPage()) {   
	    		ArrayList<Point> emptyPoint = mXLauncher.getWorkspace().getPagedView().findVacantCellNumber(i);
	    		previewImage.setBackgroundDrawable((emptyPoint == null || emptyPoint.size() <= 0)
	    				? mFullBg : mCurrentBg);
	    	} else {
	    		previewImage.setBackgroundDrawable(getDefaultBg());
	    	}
	    	
	    	if (delImage != null) {
	        	delImage.setOnClickListener(this);
	        	delImage.setTag(ACT_REMOVE_SCREEN);
	        	delImage.setVisibility(!isAddState() 
	        			&& (!SettingsValue.getSingleLayerValue(mXLauncher)
	        			|| isRemovablePage(i)));
	    	}
	    	
	    	if (homeImage != null) {
	        	homeImage.setOnClickListener(this);
	        	homeImage.setTag(ACT_SET_HOME);
	        	homeImage.setVisibility(!isAddState());
	    	}
	    	            
	    	itemToAdd = new XPagedViewItem(mXContext, preview, info);
    	} else {
    		itemToAdd = model.get(i);
    		if (itemToAdd == null) {
    			return null;
    		}
    		XScreenItemView preview = (XScreenItemView)itemToAdd.getDrawingTarget();
	    	XIconDrawable delImage = preview.getDelDrawable();
	    	XIconDrawable homeImage = preview.getHomeDrawable();
	    	if (delImage != null) {
	        	delImage.setVisibility(!isAddState() 
	        			&& (!SettingsValue.getSingleLayerValue(mXLauncher)
	        			|| isRemovablePage(i)));
	    	}
	    	
	    	if (homeImage != null) {
	        	homeImage.setVisibility(!isAddState());
	    	}
    	}
    	return itemToAdd;
    }

	@Override
	public void onClick(final DrawableItem v) {
		
		if (isClickDelButton || !isFinishDel) {
			return;
		}
		
		if (xPagedView.mItemIDMap.size() < mXLauncher.getWorkspace().getPageCount()) {
			return;
		}
		
		Object tag = v.getTag();
		if (tag == null) {
			return;
		}
		PreviewInfo info = null;
		XScreenItemView view = null;
		
		if (tag instanceof PreviewInfo) {
			info = (PreviewInfo)tag;
			view = (XScreenItemView)v;
		} else if (v == mDragBtn) {
			//点击《《时，切换屏幕管理和屏幕编辑
			setScreenState(isAddState() ? State.NORMAL : State.ADDED);
			getXContext().post(new Runnable() {

				@Override
				public void run() {
					changeState();
				}				
			});
			
			return;
	    } else if (tag instanceof String) {
	    	if (v.getParent() == null || v.getParent().getTag() == null) {
	    		return;
	    	}
	    	view = (XScreenItemView)v.getParent();
			info = (PreviewInfo)(view.getTag());
	    } else {
            return;
        }
		
		final XWorkspace workspace = mXLauncher.getWorkspace();
		int cellx = mEmptyCell[0] = info.cellX;
		int celly = mEmptyCell[1] = info.cellY;
		int screen = mEmptyCell[2] = info.screen;
		
		final int index = (cellx + celly * mCountX) + screen * (mCountX * mCountY);		

		if (tag instanceof String && tag.equals(ACT_ADD_SCREEN)) {
			getXContext().post(new Runnable() {

				@Override
				public void run() {
					addScreen();
				}
				
			});
			
			/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-3-26 S*/
            Reaper.processReaper( mXLauncher, 
               	   Reaper.REAPER_EVENT_CATEGORY_SCREEN, 
       			   Reaper.REAPER_EVENT_ACTION_SCREEN_SCREENADD,
       			   Reaper.REAPER_NO_LABEL_VALUE,
       			   Reaper.REAPER_NO_INT_VALUE );
           /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-3-26 E*/
		} else if (tag instanceof String && tag.equals(ACT_REMOVE_SCREEN)) {
			//点击删除屏幕按钮的响应事件
			if (isAddState()) {
				return;
			}
			final XScreenItemView tmpView = view;
			getXContext().post(new Runnable() {

				@Override
				public void run() {
					onClickDelButton(tmpView, workspace);
				}				
			});

		} else if (tag instanceof String && tag.equals(ACT_SET_HOME)) {
			//点击设置主屏按钮的响应事件
			if (isAddState()) {
				return;
			}
			if (mEnableClickHomePage) {
				getXContext().post(new Runnable() {

					@Override
					public void run() {
						//先设置桌面的主屏，再更新屏幕管理中的主屏符号
						workspace.setDefaultPage(index);
						changeHomePage();
					}				
				});
				
			}
		} else if (tag instanceof String && tag.equals(ACT_PREVIEW_SCREEN)) {
			getXContext().post(new Runnable() {

				@Override
				public void run() {
					//点击一个屏幕，直接进入该页
					workspace.setCurrentPage(index);
					mXLauncher.closePreviewScreen();
				}				
			});
						
		}

	}
	
	/**
	 * The Action after the delete button clicked
	 * @param delView  The view to be removed
	 * @param workspace  XWorkspace
	 * @param index  the index in the xPagedView
	 */
	private void onClickDelButton(final XScreenItemView view, final XWorkspace workspace) {
		PreviewInfo pi = (PreviewInfo)view.getTag();
		int index = mCountX * mCountY * pi.screen + mCountX * pi.cellY + pi.cellX;
		if (index != workspace.getDefaultPage()) {
//			int size = workspace.getPagedView().getChildCountAt(index);
			//There are sth. in the view to be removed.
			if (!isRemovablePage(index)) {
				if (isFinishDel) {
//					if (SettingsValue.getSingleLayerValue(mXLauncher)) {
//						// 非空不得删除
//						getXContext().removeCallbacks(mRemoveNotEmptyMsgRunnable);
//						getXContext().post(mRemoveNotEmptyMsgRunnable);
//					} else {
						//若屏幕里有内容，弹框确认是否删屏
						popupDeleteDialog(view, workspace);
//					}
				}
			} else {
				if (isFinishDel) {
					isFinishDel = false;
					onDelScreenClick(view, workspace);
				}
			}
		} else {
			// 主屏不得删除
			getXContext().removeCallbacks(mRemoveMainMsgRunnable);
			getXContext().post(mRemoveMainMsgRunnable);		
		}
	}
	
	/**
	 * Pop up the confirm dialog
	 * @param delView
	 * @param workspace
	 * @param index
	 */
	private void popupDeleteDialog(final XScreenItemView view, final XWorkspace workspace) {
		mXLauncher.getMainView().post(new Runnable() {
			@Override
			public void run() {
				if (mAlertDialog != null) {
					mAlertDialog.dismiss();
					mAlertDialog = null;
				}
				isClickDelButton = true;
				mAlertDialog = new LeAlertDialog(mXLauncher,
						R.style.Theme_LeLauncher_Dialog_Shortcut);
				mAlertDialog.setTitle(R.string.menu_screen_setting);
				mAlertDialog.setLeMessage(mXLauncher
						.getString(R.string.prompt_delet_screen));
				mAlertDialog.setLePositiveButton(
						mXLauncher.getString(R.string.delet_confirm),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								isFinishDel = false;
								onDelScreenClick(view, workspace);
							}
						});
				mAlertDialog.setLeNegativeButton(
						mXLauncher.getString(R.string.delet_cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								isFinishDel = true;
							}
						});
				mAlertDialog.show();
				// fix bug 168398
				mAlertDialog
						.setOnDismissListener(new OnDismissListener() {
							@Override
							public void onDismiss(DialogInterface dialog) {
								isClickDelButton = false;
							}
						});
				
			}
			
		});
	}
	
	/**
	 * 设置屏幕管理页每个格的主屏符号
	 */
	private void changeHomePage() {
		int defaultPage = mXLauncher.getWorkspace().getDefaultPage();
		for (PreviewInfo info : previews) {
        	int newIndex = (info.cellX + info.cellY * mCountX) + info.screen * (mCountX * mCountY);
        	XPagedViewItem item = xPagedView.findPageItemAt(info.screen, info.cellX, info.cellY);
        	if (item == null) {
        		continue;
        	}
    		XScreenItemView view = (XScreenItemView)(item.getDrawingTarget());
    		if (view.getHomeDrawable() == null) {
    			info.mHomeBitmap = null;
    			continue;
    		}
        	if (newIndex == defaultPage) {
        		info.mHomeBitmap = mHomeBitmap;
        	} else {
        		info.mHomeBitmap = mNotHomeBitmap;
        	}
        	view.resetHomeDrawable(isAddState());
        }
		invalidate();
	}

	@Override
	public boolean onLongClick(final DrawableItem v) {
		//屏幕编辑页不相应长按事件 || 屏幕管理页的格数小于3时，不响应长按事件
		if (xPagedView.mItemIDMap.size() < 3 || isAddState()) {
		    return true;
		}
		
		if (xPagedView.mItemIDMap.size() < mXLauncher.getWorkspace().getPageCount()) {
			return true;
		}
				
		Object tag = v.getTag();
		if (tag == null 
				|| !(tag instanceof String)
				|| !ACT_PREVIEW_SCREEN.equals(tag.toString())
				|| v.getParent() == null
				|| v.getParent().getTag() == null) {
		    return true;
		}
		getXContext().post(new Runnable() {

			@Override
			public void run() {
				mThumbHandler.removeMessages(0);
				doLongClick(v);
			}
			
		});		
        return true;
	}
	
	private void doLongClick(DrawableItem v) {
		XWorkspace workspace = mXLauncher.getWorkspace();

		PreviewInfo item = (PreviewInfo) (v.getParent().getTag());

		// 开始拖动屏幕单元格
		beginDragShared(v.getParent());

		int cellx = item.cellX;
		int celly = item.cellY;
		int screen = item.screen;

		currentDragViewOldIndex = (cellx + celly * mCountX) + screen * mCountX
				* mCountY;
		if (currentDragViewOldIndex == workspace.getDefaultPage()) {
			currentDragIsDefaultPage = true;
		}

		// 删除被拖动的单元个的信息
		if (currentDragViewOldIndex >= mSnapList.size()
				|| currentDragViewOldIndex >= previews.size()) {
			return;
		}
//		mSnapList.remove(currentDragViewOldIndex);

		mCurrentDragView = v.getParent();
		mEmptyCell[0] = cellx;
		mEmptyCell[1] = celly;
		mEmptyCell[2] = screen;

		// Remove the clicked item
		xPagedView.removePageItem(item, false);
		previews.remove(currentDragViewOldIndex);

		// Remove the plus item
		// 删除加号页信息
		if (workspace.getPagedView().getPageCount() < MAX_CELLCOUNT) {
			xPagedView.removePageItem(previews.get(previews.size() - 1), false);
			previews.remove(previews.size() - 1);
		}
	}
	
	@Override
	public void clean() {
		if (mInitTime != -1) {
			mPendingRunningList.put(mInitTime, new Runnable() {
				@Override
				public void run() {
					destoryMngView(true);				
				}
				
			});
			return;
		}
		destoryMngView(true);
		
		super.clean();
		
		Runtime.getRuntime().freeMemory();
	}
	
	private void destoryMngView(boolean recycleThumbs) {
        mCurrentDragView = null;
        
        mThumbHandler.removeMessages( 0 ); 
		
		if (previews.isEmpty()) {
			return;
		}
		
		if (mAlphaAnimator != null) {
            getXContext().getRenderer().ejectAnimation(mAlphaAnimator);
        }
		if (mDisappearAnimator != null) {
            getXContext().getRenderer().ejectAnimation(mDisappearAnimator);
        }
				
        for (PreviewInfo info : previews) {
        	if (recycleThumbs
        		&& info.mThumbnail != null
        			&& info.mThumbnail != mAddBitmap
        			&& info.mThumbnail != mAddPressedBitmap) {
        		info.mThumbnail.recycle();
        	}
        	info.mThumbnail = null;
        	info.mDelBitmap = null;
        	info.mHomeBitmap = null;
        }
        
        if (mEmptyBitmap != null && !mEmptyBitmap.isRecycled()) {
        	mEmptyBitmap.recycle();
        }
        	
        mDelBitmap = null;
        mHomeBitmap = null;
        mNotHomeBitmap = null;
        mAddBitmap = null;
        mAddPressedBitmap = null;
        mAddMarkBitmap = null;
        mEmptyBitmap = null;
//        mDefaultBg = null;
        mCurrentBg = null;
        mFullBg = null;
        
        previews.clear();
        
        if (recycleThumbs && mSnapList != null) {
            mSnapList.clear();
        }
        
        if (xPageIndicator != null) {
	        xPageIndicator.clearAllItems();
	        xPageIndicator.clean();
        }
        
        if (xPagedView != null) {
	        xPagedView.clearAllItems();
	        xPagedView.clean();
        }

        if (mSlidingDrawer != null) {
            mSlidingDrawer.clean();
            mSlidingDrawer = null;
        }
        
        clearAllItems();
		
		mDragController.removeDropTarget(this);
		mDragController.removeDragListener(this);
		
		mXLauncher.getMainView().post(new Runnable() {
			@Override
			public void run() {
				if (mAlertDialog != null) {
					mAlertDialog.dismiss();
					mAlertDialog = null;
				}
			}
		});
		
		getXContext().removeCallbacks(mRemoveNotEmptyMsgRunnable);
		getXContext().removeCallbacks(mRemoveMainMsgRunnable);
		mRemoveMainMsg = null;
		mRemoveNotEmptyMsg = null;
	}
	
	private void onDelScreenClick(final XScreenItemView view, XWorkspace workspace){
		PreviewInfo pi = view.getLocalInfo();
		int index = mCountX * mCountY * pi.screen + mCountX * pi.cellY + pi.cellX;
		
		//fix bug 169391 DATA 2012-08-22 S
		if(mSnapList == null || mSnapList.size() == 0 || index >= mSnapList.size()){
			isFinishDel = true;
			mXLauncher.closePreviewScreen();
			return;
		}
		//fix bug 169391 DATA 2012-08-22 E
		//操作桌面
		removeWorkspaceScreen(index, workspace);
		
		//将被删屏幕的截图删除
		mSnapList.remove(index);
		
//		if(index <= workspace.getCurrentPage()){
//			if(workspace.getCurrentPage() > 0){
//				workspace.setCurrentPage(workspace.getCurrentPage() -1);
//			}
//		}
		// 若被删的屏幕小于等于主屏，主屏的INDEX值减1
		if(index <= workspace.getDefaultPage()){
			if(workspace.getDefaultPage() > 0){							
				workspace.setDefaultPage(workspace.getDefaultPage()-1);
			}
		}
		
		removeScreenItem(view);
		
		//存入最新的屏幕数
		int pageCnt = workspace.getPagedView().getPageCount();
		SharedPreferences preferrences =
				mXLauncher.getSharedPreferences(CELLLAYOUT_COUNT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferrences.edit();
        editor.putInt(CELLLAYOUT_COUNT, pageCnt);
        editor.commit();
            
        mLauncherService.mScreenCount = pageCnt;
	}
	
	/**
	 * Remove a screen of the XWorkspace
	 * @param index   The index of the screen to be removed
	 * @param workspace  XWorkspace
	 */
	private void removeWorkspaceScreen(final int index, final XWorkspace workspace) {
		//删除桌面的某个屏幕前，先要删除该屏幕中的所有元素
		ArrayList<XPagedViewItem> items = workspace.getPagedView().getChildrenAt(index);		
    	for (int i = 0; i < items.size(); i++){
    		XPagedViewItem item = items.get(i);
    		ItemInfo info = item.getInfo();
    		
    		if(info instanceof FolderInfo){
    			FolderInfo folderInfo = (FolderInfo)info;
    			mXLauncher.removeFolder(folderInfo);
    			workspace.removePagedViewItem(info);
    			XLauncherModel.deleteFolderContentsFromDatabase(mXLauncher, folderInfo);
    		}else if(info instanceof ShortcutInfo){ 
    			workspace.removePagedViewItem(info);
    			XLauncherModel.deleteItemFromDatabase(mXLauncher, info);
    		}else if(info instanceof LauncherAppWidgetInfo){
    			workspace.removePagedViewItem(info);
                XLauncherModel.deleteItemFromDatabase(mXLauncher, info);

                final LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) info;
                final LauncherAppWidgetHost appWidgetHost = mXLauncher.getAppWidgetHost();
                if (appWidgetHost != null) {
                    new Thread("deleteAppWidgetId") {
                        public void run() {
                            appWidgetHost.deleteAppWidgetId(launcherAppWidgetInfo.appWidgetId);
                        }
                    }.start();
                }
    		}else{
    			workspace.removePagedViewItem(info);
    			XLauncherModel.deleteItemFromDatabase(mXLauncher, info);
    		}
    	}
    	//删除桌面的屏幕本身
    	workspace.removeScreenAt(index);
    	//更改屏幕管理的滚动界限
    	
        // 被删的屏幕之后的所有屏幕中的数据的screen值（操作数据库）
    	new Thread("removeItem") {
            public void run() {
            	LauncherApplication la = (LauncherApplication)mXLauncher.getApplicationContext();
                la.getLauncherProvider().transferScreens(index);
            }
        }.start();
	}
	
	public int getHomeHeight(){
	    if (mHomeBitmap == null) {
	        mHomeBitmap = ((BitmapDrawable)(mXLauncher.getResources().getDrawable(R.drawable.preview_home_btn_light))).getBitmap();
	    }
	    int marginBottom = mXContext.getResources().getDimensionPixelOffset(R.dimen.xscreen_mng_home_margin_bottom);
	    return XScreenItemView.getHomeDrawableHeight(mHomeBitmap, marginBottom);
	}
	
	public float getCellWidth(){
        return ((localRect.width() - mPagePaddingRight - mPagePaddingLeft) / mCountX);
    }
	
	public float getCellHeight(){
        return ((localRect.height() - mPagePaddingTop - mPagePaddingBottom - mDragRegionHeight) / MAX_CELLY);
    }
	
	/**
	 * 添加屏幕管理里的单元格
	 * @return
	 */
	private XScreenItemView addScreenItem() {
		int pageCnt = mXLauncher.getWorkspace().getPagedView().getPageCount(); 
		int numCells = mCountX * mCountY;
		
		int newCnt = pageCnt < MAX_CELLCOUNT ? previews.size() + 1 : previews.size();
    	int page = newCnt / numCells;
    	
    	int oldPage = mNumPages;
        mNumPages = (newCnt % numCells) == 0 ? page : ++page;
        
        //temp solution s      	
      	if (mNumPages > oldPage) {
      		xPagedView.addNewScreen();
      	}
      	//temp solution e
       	
      	//将最后的那个加号改成空白页
      	if (previews.size() <= 0) {
      		return null;
      	}
       	PreviewInfo lastInfo = previews.get(previews.size() - 1);
       	lastInfo.mDelBitmap = mDelBitmap;
       	lastInfo.mHomeBitmap = mNotHomeBitmap;
       	lastInfo.mMiddleBitmap = null;
		try{
			lastInfo.mThumbnail = mSnapList.get(mSnapList.size() - 1);
		}catch (ArrayIndexOutOfBoundsException e) {
			lastInfo.mThumbnail = null;
		}catch (IndexOutOfBoundsException e) {
			lastInfo.mThumbnail = null;
		}
		XPagedViewItem lastItem = xPagedView.findPageItemAt(lastInfo.screen, lastInfo.cellX, lastInfo.cellY);
		XScreenItemView lastView = (XScreenItemView)(lastItem.getDrawingTarget());
		
		float[] addRelativePos = null;		
		addRelativePos = getDragLayerXY(lastView);
    	addRelativePos[0] = getLastItemRelativeX();

		lastView.resetAllItems(isAddState());
		
		XIconDrawable lastPreviewImage = lastView.getThumbnailDrawable();
    	XIconDrawable lastDelImage = lastView.getDelDrawable();
    	XIconDrawable lastHomeImage = lastView.getHomeDrawable();
    	
    	lastPreviewImage.setOnClickListener(this);
    	lastPreviewImage.setTag(ACT_PREVIEW_SCREEN);
    	lastPreviewImage.setOnLongClickListener(this);
//    	if(previews.size() - 1 == mXLauncher.getWorkspace().getCurrentPage()) {        		
//    		lastPreviewImage.setBackgroundDrawable(mCurrentBg);
//    	} else {
//    		lastPreviewImage.setBackgroundDrawable(getDefaultBg());
//    	}
    	resetItemsBackgroud();
    	
    	lastDelImage.setOnClickListener(this);
    	lastDelImage.setTag(ACT_REMOVE_SCREEN);
    	
    	int index = lastInfo.screen * mCountX * mCountY + mCountX * lastInfo.cellY + lastInfo.cellX;
    	lastDelImage.setVisibility(!isAddState() 
    			&& (!SettingsValue.getSingleLayerValue(mXLauncher)
    			|| isRemovablePage(index)));
    	
    	lastHomeImage.setOnClickListener(this);
    	lastHomeImage.setTag(ACT_SET_HOME);
    	lastView.setVisibility(true);
		
    	//若桌面的页数还不满最大值，在最后加入加号    	
    	XScreenItemView addView = null;
    	if (pageCnt < MAX_CELLCOUNT) {
    		addView = addPlusView();
    	}
    	
    	if (isAddState() && addView != null) {    		
        	addDummyPlusView(addView, addRelativePos);
        	addView.setVisibility(false);
        }
    	
    	/* RK_ID: RK_SCREENMANAGER. AUT: liuli1 . DATE: 2013-05-09 . START */
        xPagedView.updateScrollLimit(mXLauncher.getWorkspace().getPageCount() - 1);
        /* RK_ID: RK_SCREENMANAGER. AUT: liuli1 . DATE: 2013-05-09 . END */
        
    	//设置屏幕管理的当前页        
    	int currentPage = (pageCnt - 1) / (mCountX * mCountY);    	
        if (isAddState()) {
        	setCurrentFocusPage(mXLauncher.getWorkspace().getCurrentPage());
        } else {
        	xPagedView.setCurrentPage(currentPage);
        }
        
        invalidate(); 
        
        //作添加动画
        if (isAddState()) {
        	animateEditAddScreenItem(lastView);
        } else {
        	animatePreviewAddScreenItem(lastView, addView);
        }
                
        return lastView;
	}
	
	/**
	 * 删除屏幕管理中的某个单元格
	 * @param index
	 */
	private void removeScreenItem(XScreenItemView view) {
		if (previews.isEmpty()) {
			isFinishDel = true;
			return;
		}
		PreviewInfo pi = view.getLocalInfo();
		if (previews.indexOf(pi) < 0) {
			isFinishDel = true;
        	return;
        }
		int pageCnt = mXLauncher.getWorkspace().getPagedView().getPageCount(); 
		int numCells = mCountX * mCountY;
		boolean addPlus = (pageCnt == (this.MAX_CELLCOUNT - 1));
		
		int newCnt = addPlus ? previews.size() : previews.size() - 1;
    	int page = newCnt / numCells;
    	
    	int oldPageCnt = mNumPages;
        mNumPages = (newCnt % numCells) == 0 ? page : ++page;
        
        animateRemoveScreenItem(view, addPlus, oldPageCnt);
	}
	
	/**
	 * 删除屏幕管理相ying項
	 * @param viewItem
	 * @param addPlus
	 * @param oldPageCnt
	 */
	private void actRemoveScreenItem(final XScreenItemView viewItem, final boolean addPlus, final int oldPageCnt) {
		// Remove Item
       	PreviewInfo lastInfo = viewItem.getLocalInfo();
       	xPagedView.removePageItem(lastInfo, true);
        previews.remove(lastInfo);
        
        if (lastInfo.mThumbnail != null && lastInfo.mThumbnail != mEmptyBitmap) {
        	lastInfo.mThumbnail.recycle();
        }
        lastInfo.mThumbnail = null;
        lastInfo.mDelBitmap = null;
        lastInfo.mHomeBitmap = null;
        
        // add ADD ITEM
        if (addPlus) {
        	if (mXLauncher.getWorkspace().getPageCount() == MAX_CELLCOUNT - 1
        			&& getItemIndex(lastInfo) != MAX_CELLCOUNT - 1) {
	        	getXContext().postDelayed(new Runnable() {        		
	
					@Override
					public void run() {
						addPlusView();
					}
	        	}, 400L);
        	} else {
        		addPlusView();
        	}
        }
        
        // Set current item
        resetItemsBackgroud();
        
        //temp solution s      	
      	if (mNumPages < oldPageCnt) {
      		xPagedView.removeScreenAt(oldPageCnt - 1);
      	}
      	//temp solution e
      	
      	/* RK_ID: RK_SCREENMANAGER. AUT: liuli1 . DATE: 2013-05-09 . START */
        xPagedView.updateScrollLimit(mXLauncher.getWorkspace().getPageCount() - 1);
        /* RK_ID: RK_SCREENMANAGER. AUT: liuli1 . DATE: 2013-05-09 . END */
        
        // set current page
        if (isAddState()) {
        	setCurrentFocusPage(mXLauncher.getWorkspace().getCurrentPage());
        } else if (mNumPages < oldPageCnt && xPagedView.getCurrentPage() >= xPagedView.getPageCount()) {            
        	xPagedView.setCurrentPage(xPagedView.getCurrentPage() - 1);
        }
        
        invalidate();
        isFinishDel = true;
	}
	
	/**
	 * 删除动画
	 * @param viewItem
	 * @param addPlus
	 * @param oldPageCnt
	 */
	private void animateRemoveScreenItem(final XScreenItemView viewItem, final boolean addPlus, final int oldPageCnt) {
       	if (viewItem == null) {
       		isFinishDel = true;
       		return;
       	}
       	
      //由于框架结构导致不画第二页的内容，所以用真实的ADDVIEW做动画不显示，只能做个假的
		final int[] tmpAddPoint = new int[2];
       	tmpAddPoint[0] = (int) (viewItem.getWidth() / 2);
       	tmpAddPoint[1] = (int) (viewItem.getHeight() / 2);
		mXLauncher.getDragLayer().getDescendantCoordRelativeToSelf(viewItem, tmpAddPoint);
		final int dragLayerX = tmpAddPoint[0];
        final int dragLayerY = tmpAddPoint[1];
		
        final Bitmap tmpBitmap = viewItem.getSnapshot(1);
        
        viewItem.setVisibility(false);
        actRemoveScreenItem(viewItem, addPlus, oldPageCnt);
		
		// 创建所点击图标的副本
		final DrawableItem tmpSnap = new DrawableItem(getXContext());
		tmpSnap.setBackgroundDrawable(new BitmapDrawable(tmpBitmap));
		
		final int width = tmpBitmap.getWidth();
		final int height = tmpBitmap.getHeight();
		
		// 将副本添加到应用的位置，与应用重叠
		tmpSnap.resize(new RectF(0, 0, width, height));
		tmpSnap.setRelativeX(dragLayerX - width / 2);
		tmpSnap.setRelativeY(dragLayerY - height / 2);		
		mXLauncher.getDragLayer().addItem(tmpSnap, 0);		
		
		final float pointX = tmpSnap.localRect.centerX();
       	final float pointY = tmpSnap.localRect.centerY();
       	
		ValueAnimator anim = ValueAnimator.ofFloat(1f, 0f);
		anim.setDuration(500L);
		anim.setStartDelay(0L);
		anim.setInterpolator(new DecelerateInterpolator(2));
		
		anim.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {				
			}

			@Override
			public void onAnimationEnd(Animator animation) {
                Matrix matrix = tmpSnap.getMatrix();
				
				matrix.reset();
				matrix.setScale(0, 0, pointX, pointY);
				
				tmpSnap.setAlpha(0f);
				
				mXLauncher.getDragLayer().removeItem(tmpSnap);
				tmpSnap.invalidate();
				tmpBitmap.recycle();
			}

			@Override
			public void onAnimationCancel(Animator animation) {
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}
		});
		
		anim.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				final float value = (Float) animation.getAnimatedValue();				
				Matrix matrix = tmpSnap.getMatrix();
				
				matrix.reset();
				matrix.setScale(value, value, pointX, pointY);
				
				tmpSnap.setAlpha(value);
			}
		});
		getXContext().getRenderer().injectAnimation(anim, false);
	}
	
	/**
	 * 屏幕管理添加动画
	 * @param viewItem
	 * @param addItem
	 */
	private void animatePreviewAddScreenItem(final XScreenItemView viewItem, final XScreenItemView addItem) {
       	if (viewItem == null) {
       		return;
       	}
       	
       	boolean realAddAnim = true;
       	
       	final float pointX = viewItem.localRect.centerX();
       	final float pointY = viewItem.localRect.centerY();
		
       	DrawableItem tmpSnap = null;
       	Bitmap tmpBitmap = null;
       	
        float addX = 0;
        float addY = 0;
        if (addItem != null) {
        	addX = (viewItem.getLocalInfo().cellX - addItem.getLocalInfo().cellX) * xPagedView.getCellWidth();
            addY = (viewItem.getLocalInfo().cellY - addItem.getLocalInfo().cellY) * xPagedView.getCellHeight();
        	if (addItem.getLocalInfo().screen > viewItem.getLocalInfo().screen) {
        		addX -= xPagedView.getWidth();
        		realAddAnim = false;
        		
        		//由于框架结构导致不画第二页的内容，所以用真实的ADDVIEW做动画不显示，只能做个假的
        		final float[] tmpAddPoint = getDragLayerXY(viewItem);
        		final float dragLayerX = tmpAddPoint[0];
                final float dragLayerY = tmpAddPoint[1];
        		
                tmpBitmap = addItem.getSnapshot(1);
        		
        		// 创建所点击图标的副本
        		tmpSnap = new DrawableItem(getXContext());
        		tmpSnap.setBackgroundDrawable(new BitmapDrawable(tmpBitmap));
        		
        		final int width = tmpBitmap.getWidth();
        		final int height = tmpBitmap.getHeight();
        		
        		// 将副本添加到应用的位置，与应用重叠
        		tmpSnap.resize(new RectF(0, 0, width, height));
        		tmpSnap.setRelativeX(dragLayerX - width / 2);
        		tmpSnap.setRelativeY(dragLayerY - height / 2);
        		mXLauncher.getDragLayer().addItem(tmpSnap);
        		
        		addX = -addX;
        		addY = -addY;
        	}
        }
        final float deltaAddX = addX;
        final float deltaAddY = addY;
        
        final DrawableItem snap = tmpSnap;
        final Bitmap bitmap = tmpBitmap;
        final boolean isRealAdd = realAddAnim;
       	
		ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
		anim.setDuration(500L);
		anim.setStartDelay(0);
		anim.setInterpolator(new DecelerateInterpolator(2));
		
		anim.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
				viewItem.setAlpha(0.0f);
				if (addItem != null) {
					
					if (isRealAdd) {
						Matrix addMatrix = addItem.getMatrix();
						addMatrix.reset();
						
						addMatrix.postTranslate(deltaAddX, deltaAddY);
					} else {
						Matrix addMatrix = snap.getMatrix();
						addMatrix.reset();
						
						addMatrix.postTranslate(0, 0);
					}
				}
			}

			@Override
			public void onAnimationEnd(Animator animation) {
                Matrix matrix = viewItem.getMatrix();
				
				matrix.reset();
				matrix.setScale(1, 1, pointX, pointY);
				
				viewItem.setAlpha(1f);
				
				if (addItem != null) {
					if (isRealAdd) {
						Matrix addMatrix = addItem.getMatrix();
						addMatrix.reset();					
						addMatrix.postTranslate(0, 0);
					} else {
						Matrix addMatrix = snap.getMatrix();
						addMatrix.reset();					
						addMatrix.postTranslate(deltaAddX, deltaAddY);
						
						mXLauncher.getDragLayer().removeItem(snap);
						snap.invalidate();
						bitmap.recycle();
					}					
				}
			}

			@Override
			public void onAnimationCancel(Animator animation) {
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}
		});
		
		anim.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				final float value = (Float) animation.getAnimatedValue();				
				Matrix matrix = viewItem.getMatrix();
				
				matrix.reset();
				matrix.setScale(value, value, pointX, pointY);
				
				viewItem.setAlpha(value);
				
				if (addItem != null) {
					if (isRealAdd) {
						Matrix addMatrix = addItem.getMatrix();
						addMatrix.reset();					
						addMatrix.postTranslate((1 - value) * deltaAddX, (1 - value) * deltaAddY);
					} else {
						Matrix addMatrix = snap.getMatrix();
						addMatrix.reset();					
						addMatrix.postTranslate(value * deltaAddX, value * deltaAddY);
					}
				}
			}
		});
		getXContext().getRenderer().injectAnimation(anim, false);
	}
	
	/**
	 * 取得某个VIEW中心点在DragLayer中的坐标
	 * @param viewItem
	 * @return
	 */
	public float[] getDragLayerXY(final DrawableItem viewItem) {
		final float[] tmpAddPoint = new float[2];
	   	tmpAddPoint[0] = viewItem.getWidth() / 2.0f;
	   	tmpAddPoint[1] = viewItem.getHeight() / 2.0f;
		mXLauncher.getDragLayer().getDescendantCoordRelativeToSelf(tmpAddPoint, viewItem);
		return tmpAddPoint;
	}
	
	/**
	 * 点击屏幕编辑的加号时，加号位置不动，只能隐藏真的加号，添加一个假的加号在屏幕上
	 * @param addItem
	 * @param addPos
	 */
	private void addDummyPlusView(final XScreenItemView addItem, final float[] addPos) {
        Bitmap tmpBitmap = null;
       	
        if (addItem != null) {
        	final float dragLayerX = addPos[0];
            final float dragLayerY = addPos[1];
    		
            tmpBitmap = addItem.getSnapshot(1);
    		
    		// 创建所点击图标的副本
            final XIconDrawable snap = new XIconDrawable(getXContext(), tmpBitmap);
    		
    		final int width = tmpBitmap.getWidth();
    		final int height = tmpBitmap.getHeight();
    		
    		// 将副本添加到应用的位置，与应用重叠
//    		snap.resize(new RectF(0, 0, width, height));
    		final float relativeX = dragLayerX - width / 2.0f;
    		final float relativeY = dragLayerY - height / 2.0f;
    		
    		snap.setRelativeX(relativeX);
    		snap.setRelativeY(relativeY);
    		mXLauncher.getDragLayer().addItem(snap);
    		mSnap = snap;
    		
            final Bitmap bitmap = tmpBitmap;
    		
    		getXContext().postDelayed(new Runnable() {

				@Override
				public void run() {
					if (addItem != null) {
						mXLauncher.getDragLayer().removeItem(snap);
						snap.invalidate();
						
						mSnap = null;
						
						bitmap.recycle();
						
						addItem.setVisibility(true);
					}
				}
    			
    		}, 750L);
        }
	}
	
	/**
	 * 屏幕编辑添加动画
	 * @param viewItem
	 * @param addItem
	 */
	private void animateEditAddScreenItem(final XScreenItemView viewItem) {
       	if (viewItem == null) {
       		return;
       	}
       	
       	final float pointX = viewItem.localRect.centerX();
       	final float pointY = viewItem.localRect.centerY();
       	
		ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
		anim.setDuration(500L);
		anim.setStartDelay(0);
		anim.setInterpolator(new DecelerateInterpolator(2));
		
		anim.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
				viewItem.setAlpha(0.0f);
			}

			@Override
			public void onAnimationEnd(Animator animation) {
                Matrix matrix = viewItem.getMatrix();
				
				matrix.reset();
				matrix.setScale(1, 1, pointX, pointY);
				
				viewItem.setAlpha(1f);
			}

			@Override
			public void onAnimationCancel(Animator animation) {
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}
		});
		
		anim.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				final float value = (Float) animation.getAnimatedValue();				
				Matrix matrix = viewItem.getMatrix();
				
				matrix.reset();
				matrix.setScale(value, value, pointX, pointY);
				
				viewItem.setAlpha(value);
			}
		});
		getXContext().getRenderer().injectAnimation(anim, false);
	}
	
	/**
	 * 设置屏幕管理中所有单元个的背景
	 */
	private void resetItemsBackgroud() {
		int current = mXLauncher.getWorkspace().getCurrentPage();
		resetItemsBackgroud(current);
	}
	
	/**
	 * 设置屏幕管理中所有单元个的背景
	 */
	private void resetItemsBackgroud(int current) {
//		R2.printStack("dooba");
        for (PreviewInfo oldCurInfo : previews) {
        	int newIndex = (oldCurInfo.cellX + oldCurInfo.cellY * mCountX) + oldCurInfo.screen * (mCountX * mCountY);
        	XPagedViewItem oldCurItem = xPagedView.findPageItemAt(oldCurInfo.screen, oldCurInfo.cellX, oldCurInfo.cellY);
        	if (oldCurItem == null) {
        		continue;
        	}
    		XScreenItemView oldCurView = (XScreenItemView)(oldCurItem.getDrawingTarget());
    		oldCurView.setScaleX(ORIGINAL_SCALE_RATE);
			oldCurView.setScaleY(ORIGINAL_SCALE_RATE);
			
    		if (oldCurInfo.mHomeBitmap == null) {
    			oldCurView.resetThumbDrawable(mAddBitmap);
    			oldCurView.getThumbnailDrawable().setBackgroundDrawable(null);
    			continue;
    		}
    		//若格数等于桌面的当前屏，判断其是否已满，满了显示红框，否则显示黄框
        	if (newIndex == current) {
        		ArrayList<Point> emptyPoint = mXLauncher.getWorkspace().getPagedView().findVacantCellNumber(current);
        		oldCurView.getThumbnailDrawable().setBackgroundDrawable((emptyPoint == null || emptyPoint.size() <= 0)
        				? mFullBg : mCurrentBg);
        	} else {
        		oldCurView.getThumbnailDrawable().setBackgroundDrawable(getDefaultBg());
        	}
        }
	}

	@Override
	public void onDragStart(XDragSource source, Object info, int dragAction) {
	}

	private XDragObject mDragObject = null;
	@Override
	public void onDragEnd() {
	    if (!this.isVisible()) {
	        return;
	    }
		getXContext().post(new Runnable() {

			@Override
			public void run() {
				doDropEnd();
				
			}
			
		});
	}
	
	private void doDropEnd() {
		//若拖动源来自屏幕编辑页下面的WIDGET，应用等
		if (mDragObject != null && mDragObject.dragSource == this.mSlidingDrawer.getContent()) {
			int current = mXLauncher.getWorkspace().getCurrentPage();
			// 判断拖动最终经过的屏幕INDEX与桌面的当前屏是否相等，不等的话，重设桌面的当前屏
			if (current != this.mWorkspaceCurr && this.mWorkspaceCurr != -1) {
				mXLauncher.getWorkspace().setCurrentPage(mWorkspaceCurr);
			}
			// 屏幕编辑时，将拖动最终经过的屏幕滑到中间，若最终经过的屏幕是加号，重设单元格背景；
			// 屏幕管理时，重设单元格背景
			if (isAddState()) {
				setCurrentFocusPage(mWorkspaceCurr);
				// 校正添加页的焦点
				int screen = (mTargetCell[0] + mTargetCell[1] * mCountX)
						+ mTargetCell[2] * mCountX * mCountY;
				if (screen == previews.size() - 1
						&& mXLauncher.getWorkspace().getPageCount() < MAX_CELLCOUNT) {
					resetItemsBackgroud();
				}
			} else {
				resetItemsBackgroud();
			}
		}		
		
		// 拖动取消时
		if (mDragObject != null && !this.isonDrop) {
			// 若是拖动源为屏幕管理本身，即调换屏幕的位置时，执行onDrop
			if (mDragObject.dragSource == this && isVisible()) {
				onDrop(mDragObject);
			}			
			// tmp edition e
			mOnExitAlarm.setOnAlarmListener(mOnExitAlarmListener);
			mOnExitAlarm.setAlarm(0);// ON_EXIT_CLOSE_DELAY
			mDragObject = null;
		} else if (mCurrentDragView == null) {
			// tmp edition s
			// 拖动源为屏幕编辑下面的应用/WIDGET，但中途无故取消了，则走下列逻辑
			if ((mDragObject != null
					&& mDragObject.dragSource instanceof XScreenContentTabHost) || !(isonDrop || isDragExit)) {				
				if (mDragObject != null) {
					mDragController.cancelDrag();
					mDragObject = null;
				}
			} else {
				// tmp edition e
				// mXLauncher.closePreviewScreen();
			}
		}

		isonDrop = false;
		isDragExit = false;
	}

	@Override
	public void onDropCompleted(DrawableItem target, XDragObject d,
			boolean success) {
//		if (!success) {
//            // The drag failed, we need to return the item to the folder
//            //mFolderIcon.onDrop(d);
//
//            // We're going to trigger a "closeFolder" which may occur before this item has
//            // been added back to the folder -- this could cause the folder to be deleted
//            if (mOnExitAlarm.alarmPending()) {
//                mSuppressFolderDeletion = true;
//            }
//        }

        if (target != this) {
            if (mOnExitAlarm.alarmPending()) {            	
                mOnExitAlarm.cancelAlarm();
                completeDragExit();
            }
        }
      //add for quick drag mode by sunzq3, begin;
        ((XLauncherView)mContext).getWorkspace().getPageIndicator().startNormalAnimation();
      //add for quick drag mode by sunzq3, end;
        invalidate();
	}

	@Override
	public boolean isDropEnabled() {
		return isVisible();
	}
	
	/**
	 * Check the target cell is valid or not.
	 */
	private void checkTheTargetCell() {
		int targetIndex = mTargetCell[2] * mCountX * mCountY  + mTargetCell[1] * mCountX  + mTargetCell[0];
		XPagedViewItem testItem = xPagedView.findPageItemAt(mTargetCell[2], mTargetCell[0], mTargetCell[1]);
		if ((testItem != null && testItem.getDrawingTarget() != null)
				|| targetIndex >= previews.size()) {
			for (int screen = 0; screen < mNumPages; screen++) {
				for (int i = 0; i < mCountY; i++) {
					for (int j = 0; j < mCountX; j++) {
						XPagedViewItem root = xPagedView.findPageItemAt(
								screen, j, i);
						if (root == null
								|| (root != null && root.getDrawingTarget() == null)) {
							mTargetCell[0] = j;
							mTargetCell[1] = i;
							mTargetCell[2] = screen;
							xPagedView.setCurrentPage(screen);
							screen = mNumPages;
							i = mCountY;
							break;
						}
					}
				}
			}
		}
	}

	@Override
	public void onDrop(final XDragObject d) {
		final XWorkspace workspace = mXLauncher.getWorkspace();
		isonDrop = true;
		
		//拖动源为屏幕管理本身，即调换屏幕的位置时
		if (d.dragInfo instanceof PreviewInfo) {
			getXContext().post(new Runnable() {
				@Override
				public void run() {
					onDropFromItself(workspace);					
				}
				
			});
			
		}
		// tmp edition s
		//拖动源为屏幕编辑下面的应用/WIDGE时，即拖动添加应用/WIDGET时
		else if (d.dragSource == this.mSlidingDrawer.getContent()) {
			getXContext().post(new Runnable() {

				@Override
				public void run() {
					onDropFromSlidingBar(d, workspace);					
				}
				
			});
			
		}
		// tmp edition e
		//拖动桌面上的应用/WIDGET到屏幕管理时
		else {
			getXContext().post(new Runnable() {

				@Override
				public void run() {
					onDropFromWorkspace(d, workspace);
					
				}
				
			});
			
		}
	}
	
	/**
	 * 设置桌面的主屏页/当前页
	 * @param index
	 * @param workspace
	 */
	private void setWorkspaceDefaultPage(final int index, final XWorkspace workspace) {
		 mXContext.postDelayed(new Runnable() {

				@Override
				public void run() {					
					
					workspace.changeScreenOrder(currentDragViewOldIndex, index);

					if (currentDragIsDefaultPage) {
						currentDragIsDefaultPage = false;
						workspace.setDefaultPage(index);
					} else if (currentDragViewOldIndex < workspace.getDefaultPage()
							&& index >= workspace.getDefaultPage()) {
						workspace.setDefaultPage(
								workspace.getDefaultPage() - 1);
					} else if (currentDragViewOldIndex > workspace.getDefaultPage()
							&& index <= workspace.getDefaultPage()) {
						workspace.setDefaultPage(
								workspace.getDefaultPage() + 1);
					}
					changeHomePage();
					
					xPagedView.resetAnim();
					
					if (mOnRefreshOnAddScreenAlarm != null) {
			        	mOnRefreshOnAddScreenAlarm.setAlarm(0L);
			        }
			        
			        if (mOnUpdateScreenAlarm != null) {
			        	mOnUpdateScreenAlarm.setAlarm(0L);
			        }        
			        mOnUpdateScreenAlarm = null;
					mOnRefreshOnAddScreenAlarm = null;
					
					xPagedView.setTouchable(true);
					
				}
	        	
	        }, 200);
	}
	
	/**
	 * 调换屏幕的位置
	 * @param workspace
	 */
	private Object mLock = new Object();
	/**
	 * @param workspace
	 */
	private void onDropFromItself(final XWorkspace workspace) {
		if (mCurrentDragView == null) {
			return;
		}
		
		//for bug 14814 //防止移动的动画未执行完就落下，取得的落点不为空
		long currentTime = System.currentTimeMillis();
		long duration = mBeginMoveTime == 0 ? this.mMoveDuration : (currentTime - mBeginMoveTime);
		final long delayTime = duration > mMoveDuration ? 0 : (mMoveDuration - duration);		
		if (delayTime > 0) {
			synchronized(mLock) {
				try {
					mLock.wait(delayTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}		
		//for bug 14814		
		
		//检查落点是否有效，无效的话，重新找落点
		checkTheTargetCell();
		
		xPagedView.setTouchable(false);
		
		PreviewInfo si = (PreviewInfo) mCurrentDragView.getTag();
		si.cellX = mTargetCell[0];
		si.cellY = mTargetCell[1];
		si.screen = mTargetCell[2];
		
        int tmpIndex = (si.cellX + si.cellY * mCountX) + mTargetCell[2] * mCountX * mCountY;
		
		if (tmpIndex > previews.size()) {				
			tmpIndex = previews.size();
		}
		final int index = tmpIndex;
		
		int[] result = new int[3];
		
		xPagedView.getInfoFromIndex(index, result);
		si.cellX = mTargetCell[0] = result[1];
		si.cellY = mTargetCell[1] = result[2];
		si.screen = mTargetCell[2] = result[0];
		
        //将拖动的屏幕单元个重新加上
		XPagedViewItem itemToAdd = new XPagedViewItem(mXContext, mCurrentDragView, si);
		xPagedView.addPagedViewItem(itemToAdd);
		
		if (index >= previews.size()) {
			previews.add(si);
		} else {
			previews.add(index, si);
		}

//		if (index >= mSnapList.size()) {
//			mSnapList.add(si.mThumbnail);
//		} else {
//			mSnapList.add(index, si.mThumbnail);
//		}

		// 拖动屏幕落下后，判断是否加+号屏
		if (previews.size() < MAX_CELLCOUNT) {	 
	        addPlusView(previews.size());
	    }
		
		mCurrentDragView.onTouchCancel( null );
		invalidate();
		
		// 调换各个屏幕上的数据的screen值（操作数据库）
		new Thread("changeScreenOrder") {
            public void run() {
            	LauncherApplication la = (LauncherApplication)mXLauncher.getApplicationContext();
                la.getLauncherProvider().changeScreenOrder(currentDragViewOldIndex, index);
            }
        }.start(); 
        
        setWorkspaceDefaultPage(index, workspace);
	}
	
	/**
	 * 拖动屏幕编辑上的应用到屏幕单元格上
	 * @param d
	 * @param workspace
	 */
	private void onDropFromSlidingBar(final XDragObject d, final XWorkspace workspace) {
		//取消换屏/加屏/进入桌面的操作
		mReorderAlarm.cancelAlarm();
		mAddNewItemAlarm.cancelAlarm();
		mIntoWorkspaceAlarm.cancelAlarm();
		int screen = mWorkspaceCurr;
		
		mDragObject = d;
		
		if (d.dragInfo == null) {
			mDragController.cancelDrag();				
			return;
		}
		
		// dooba edit
		//屏幕编辑页的多选操作（暂时未使用）
		if (d.dragInfo instanceof List) {
			ArrayList<Point> emptyPoint = workspace.getPagedView().findVacantCellNumber(screen);
			//判断桌面屏幕上的空格数是否够落所有的选中应用（此处是否不太妥当，万一是添加文件夹呢）
			if (emptyPoint == null || emptyPoint.size() < 1) {
				mXLauncher.showOutOfSpaceMessage();				
				mDragController.cancelDrag();
				return;
			}
			
			workspace.setCurrentPage(mWorkspaceCurr);
			
			d.x = emptyPoint.get(0).x;
			d.y = emptyPoint.get(0).y;
			
			workspace.onDrop(d);
			
			//刷新屏幕管理的缩略图
			onAddSimpleInfo(mWorkspaceCurr);
			return;
		}
		
		ItemInfo info = (ItemInfo) d.dragInfo;
		int spanX = info.spanX;
		int spanY = info.spanY;
		if (info instanceof LenovoWidgetViewInfo) {
			spanX = ((LenovoWidgetViewInfo)info).minWidth;
			spanY = ((LenovoWidgetViewInfo)info).minHeight;
		} else if (info.itemType == SimpleItemInfo.ACTION_TYPE_CREATE_WIDGET) {
			spanX = ((SimpleItemInfo)info).spanXY[0];
			spanY = ((SimpleItemInfo)info).spanXY[1];
		}
		if (spanX < 0) {
			spanX = 1;
		}
		if (spanY < 0) {
			spanY = 1;
		}
		
		//根据拖动的物体的SPANX， SPANY判断屏幕是否有空间
		int[] findXY = workspace.getPagedView().findFirstVacantCell(screen, spanX, spanY);
		if (findXY == null || findXY[0] < 0 || findXY[1] < 0) {
			mXLauncher.showOutOfSpaceMessage();
			mDragController.cancelDrag();
			return;
		}
		
		workspace.setCurrentPage(mWorkspaceCurr);
		
		int dropPosX = (int)(findXY[0] * workspace.getPagedView().getCellWidth() + 1);
		int dropPosY = (int)(findXY[1] * workspace.getPagedView().getCellHeight() + 1);
		
		//若拖动的物体为文件夹/其它小部件/快捷方式，单独处理
		if (info instanceof SimpleItemInfo) {
			SimpleItemInfo simpleInfo = (SimpleItemInfo)info;				
			handleSimpleItemInfo(simpleInfo, mWorkspaceCurr, findXY, new int[]{dropPosX, dropPosY});
			return;
		}
		
		// 如果是天气WIDGET，弹出皮肤选择框
//		if (info instanceof LenovoWidgetViewInfo
//				&& ((LenovoWidgetViewInfo) info).className
//						.equals(GadgetUtilities.WEATHERMAGICWIDGETVIEWHELPER)) {
//			Intent i = new Intent(
//					WeatherUtilites.ACTION_ADD_LENOVO_WEATHER_WIDGET_ACTIVITY);
//			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			try {
//				mXLauncher.resetAddInfo();
//				mXLauncher.setPendingObjectPos(findXY[0], findXY[1]);
//				mXLauncher.startActivity(i);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			return;
//		}
		
		d.x = dropPosX;
		d.y = dropPosY;
		
		workspace.addExternalItemInfo(new int[]{dropPosX, dropPosY}, info, true, d);
		
		onAddSimpleInfo(mWorkspaceCurr);
	}
	
	/**
	 * 拖动桌面上的应用到屏幕管理，现在未使用
	 * @param d
	 * @param workspace
	 */
	private void onDropFromWorkspace(final XDragObject d, final XWorkspace workspace) {
		mDragObject = d;
		mReorderAlarm.cancelAlarm();
		mAddNewItemAlarm.cancelAlarm();
			
		int screen = mWorkspaceCurr;
		ItemInfo info = d.dragInfo == null ? null :((ItemInfo) d.dragInfo);
		if (info == null) {
			mDragController.cancelDrag();
			mXLauncher.closePreviewScreen();
			return;
		}
		
		int[] findXY = workspace.findNearestVacantArea(screen, d.x, d.y, info.spanX, info.spanY, null, null);
		if (findXY == null || findXY[0] < 0 || findXY[1] < 0) {
			mXLauncher.showOutOfSpaceMessage();
			mXLauncher.closePreviewScreen();
			mDragController.cancelDrag();
			return;
		}
		
		workspace.setCurrentPage(mWorkspaceCurr);
		
		d.x = (int)(findXY[0] * workspace.getPagedView().getCellWidth() + 1);
		d.y = (int)(findXY[1] * workspace.getPagedView().getCellHeight() + 1);
		
		workspace.onDrop(d);
		
		mXLauncher.closePreviewScreen();
	}
	
	
	private void updateThumb( int index){
		updateThumb( index , true );
	}
	
	/**
	 * 更新某个屏幕单元个的缩略图
	 * @param index
	 */
	private void updateThumb(int index, boolean withwidget ) {
        XWorkspace workspace = mXLauncher.getWorkspace();
        workspace.setWidgetVisible(true);
        
        int height = getHomeHeight()/* + marginBottom * 2*/;
        int extTop = mXLauncher.getResources().getDimensionPixelOffset(R.dimen.xscreen_mng_thumbnail_margin_top);              
        Bitmap newBitmap = mXLauncher.getSnapBitmap(getCellWidth() - 2 * XScreenItemView.getWidthGap(mXLauncher), 
        		getCellHeight() - XScreenItemView.getHeightGap(mXLauncher) - height - extTop, index, height, extTop);

        workspace.setWidgetVisible(false);

        int numCells = mCountX * mCountY;
    	int screen = index / numCells;        	
    	int addIndex = index % numCells;
    	int cellX = addIndex % mCountX;
    	int cellY = addIndex / mCountX;
    	
    	//若屏幕管理页面被关闭，不用更新了
    	if (!isVisible()
                || mSnapList == null
                || mSnapList.isEmpty()
                || previews.isEmpty()
    			|| index < 0
    			|| index >= mSnapList.size()) {
    		return;
    	}
    	Bitmap bimp = mSnapList.remove(index);    	
    	mSnapList.add(index, newBitmap);
    	
    	if (bimp != null && bimp != mEmptyBitmap) {
    		bimp.recycle();
    		bimp = null;
    	}
    	
    	if (!isVisible()
    			|| previews == null
    			|| index >= previews.size()) {
    		return;
    	}
    	previews.get(index).mThumbnail = newBitmap;
    	
    	XPagedViewItem item = xPagedView.findPageItemAt(screen, cellX, cellY);
		if (item == null
				|| (!withwidget && item.getDrawingTarget() != null && item.getDrawingTarget() instanceof XViewContainer)) {
    		return;
    	}
		XScreenItemView view = (XScreenItemView)(item.getDrawingTarget());
		if (view == null) {
			return;
		}

		view.resetThumbDrawable();
		
		//若为屏幕编辑界面，更新界面；若为屏幕管理页，直接更新单元格的背景
		int focusCellIndex = xPagedView.getCurrentFocusCell();
		if (focusCellIndex >= mXLauncher.getWorkspace().getPageCount()) {
			focusCellIndex = mXLauncher.getWorkspace().getPageCount() - 1;
		} else if (focusCellIndex < 0) {
			focusCellIndex = 0;
		}
		if (isAddState()) {
			if (index == focusCellIndex) {
				ArrayList<Point> emptyPoint = mXLauncher.getWorkspace().getPagedView().findVacantCellNumber(index);
				view.getThumbnailDrawable().setBackgroundDrawable((emptyPoint == null || emptyPoint.size() <= 0)
						? mFullBg : mCurrentBg);
			} else {
				view.getThumbnailDrawable().setBackgroundDrawable(getDefaultBg());
				XPagedViewItem focusCell = xPagedView.findPageItemAt(focusCellIndex);
				if (focusCell != null && focusCell.getDrawingTarget() != null) {
					ArrayList<Point> emptyPoint = mXLauncher.getWorkspace().getPagedView().findVacantCellNumber(focusCellIndex);
					((XScreenItemView)focusCell.getDrawingTarget())
					        .getThumbnailDrawable().setBackgroundDrawable((emptyPoint == null || emptyPoint.size() <= 0)
							? mFullBg : mCurrentBg);
				}
			}
		} else {
			if (index == mXLauncher.getWorkspace().getCurrentPage()){
				ArrayList<Point> emptyPoint = mXLauncher.getWorkspace().getPagedView().findVacantCellNumber(index);
				view.getThumbnailDrawable().setBackgroundDrawable((emptyPoint == null || emptyPoint.size() <= 0)
						? mFullBg : mCurrentBg);
			}else{
				view.getThumbnailDrawable().setBackgroundDrawable( getDefaultBg() );
			}
			if (view.getDelDrawable() != null) {
				view.getDelDrawable().setVisibility(!SettingsValue.getSingleLayerValue(mXLauncher)
						|| isRemovablePage(index));
			}
		}
    }
	

	@Override
	public void onDragEnter(XDragObject dragObject) {
		isDragExit = false;
		isonDrop = false;
		
		if (dragObject.dragSource != this 
				&& dragObject.dragInfo != null
				&& dragObject.dragView != null
				&& dragObject.dragInfo instanceof ItemInfo) {
			ItemInfo info = (ItemInfo)dragObject.dragInfo;
			//拖动WIDGET进入屏幕管理/屏幕编辑时，缩小拖动物体的大小
			if (info.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET
					|| info.itemType == LauncherSettings.Favorites.ITEM_TYPE_LEOSWIDGET
					/*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. S***/
					|| info.itemType == LauncherSettings.Favorites.ITEM_TYPE_COMMEND_APPWIDGET
					/*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. E***/) {
				float maxWidth = getCellWidth();
				float maxHeight = getCellHeight();
				float viewWidth = dragObject.dragView.getWidth();
				float viewHeight = dragObject.dragView.getHeight();
				float factor = 1.0f;
				if (viewWidth > maxWidth || viewHeight > maxHeight) {
					float factorX = maxWidth / viewWidth;
					float factorY = maxHeight / viewHeight;
					factor = factorX < factorY ? factorX : factorY;
				}
				
				mDragController.scaleDragView(factor, false);
			}
		}
		mPreviousTargetCell[0] = -1;
        mPreviousTargetCell[1] = -1;
        mOnExitAlarm.cancelAlarm();
		
	}

	@Override
	public void onDragOver(final XDragObject d) {
		getXContext().post(new Runnable() {

			@Override
			public void run() {
				doDragOver(d);				
			}
			
		});
	}
	
	private void doDragOver(XDragObject d) {
		final XWorkspace workspace = mXLauncher.getWorkspace();
		int currPage = xPagedView.getCurrentPage();
		float[] r = getDragPoint(d.x, d.y, null);//getDragViewVisualCenter(d.x, d.y, d.xOffset, d.yOffset, d.dragView, null);
		int[] targetCell = new int[2];
		//找到当前手指所在的屏幕单元格，由于有可能是滑动了部分页，所以得到的targetCell是相对于currPage的，cellX在-3到3之间
		targetCell = xPagedView.findNearestArea(currPage, (int) r[0], (int) r[1], 1, 1, targetCell);
		//校正所选单元个的screen, cellX, cellY
		mTargetCell[0] = targetCell[0] >= 0 ? targetCell[0] % mCountX : (targetCell[0] + mCountX) % mCountX;
		mTargetCell[1] = targetCell[1];
		mTargetCell[2] = targetCell[0] >= 0 ? (currPage + targetCell[0] / mCountX) 
				: (currPage + (targetCell[0] - mCountX) / mCountX);
//		currPage = mTargetCell[2];
		
		if(d.dragInfo instanceof PreviewInfo){
			// 调换屏幕的顺序时
			isCurrentDrag = true;
			if (mTargetCell[0] != mPreviousTargetCell[0] 
					|| mTargetCell[1] != mPreviousTargetCell[1]
					|| mTargetCell[2] != mPreviousTargetCell[2]) {
				//for bug 14814
				mBeginMoveTime = 0;
				//for bug 14814
				
				mReorderAlarm.cancelAlarm();
				mReorderAlarm.setOnAlarmListener(mReorderAlarmListener);
				mReorderAlarm.setAlarm(BEGIN_REORDER_DURATION);//150
				mPreviousTargetCell[0] = mTargetCell[0];
				mPreviousTargetCell[1] = mTargetCell[1];
				mPreviousTargetCell[2] = mTargetCell[2];
			}
			
		//拖动物为其它应用/WIDGET时
		} else {			
			if (mTargetCell[0] == mPreviousTargetCell[0] 
					&& mTargetCell[1] == mPreviousTargetCell[1]
					&& mTargetCell[2] == mPreviousTargetCell[2]) {
				return;
			}
			mAddNewItemAlarm.cancelAlarm();
			mIntoWorkspaceAlarm.cancelAlarm();
			
			mPreviousTargetCell[0] = mTargetCell[0];
			mPreviousTargetCell[1] = mTargetCell[1];
			mPreviousTargetCell[2] = mTargetCell[2];
			
			isCurrentDrag = false;
			
			int count = workspace.getPagedView().getPageCount();
			int cellCntPerPage = mCountX * mCountY;
			
			boolean addItem = false;
			
			int screen = (mTargetCell[0] + mTargetCell[1]
					* mCountX)
					+ mTargetCell[2] * cellCntPerPage;
			
			// 遍历所有的屏幕单元格，重设屏幕单元格的背景
			for (int page = 0; page < xPagedView.getPageCount(); page++) {
				for (int i = 0; i < mCountX; i++) {
					for (int j = 0; j < mCountY; j++) {
						XPagedViewItem root = xPagedView.findPageItemAt(page, i, j);
						if (root == null || root.getDrawingTarget() == null || root.getInfo() == null) {
							continue;
						}
						XScreenItemView itemView = (XScreenItemView)root.getDrawingTarget();
						XIconDrawable view = itemView.getThumbnailDrawable();
						if (view == null) {
							continue;
						}
						int index = page * cellCntPerPage + j * mCountX + i;
						// 若单元格等于手指所指的格
						if (screen == index 
								|| screen >= MAX_CELLCOUNT && index == MAX_CELLCOUNT - 1
								|| screen < 0 && index == 0) {	
							//若单元个等于+号屏
							if (index == previews.size() - 1 && count < MAX_CELLCOUNT) {
								addItem = true;
//								mImageWidth = (int)view.getWidth();
//								mImageHeight = (int)view.getHeight();
								
								//设置单元格的图片和缩放大小
								itemView.resetThumbDrawable(mAddPressedBitmap);
								itemView.setScaleX(SCALE_RATE);
								itemView.setScaleY(SCALE_RATE);
							} else {
								itemView.setScaleX(SCALE_RATE);
								itemView.setScaleY(SCALE_RATE);
								
								//在某屏悬停1.5S后，直接进入该屏
								if (screen >= 0 && screen < MAX_CELLCOUNT) {
									mIntoWorkspaceAlarm.setOnAlarmListener(mOnIntoWorkspaceListener);
									mIntoWorkspaceAlarm.setAlarm(SettingsValue.WAIT_TO_ENTER_DELAY);
								}
								
								int screen1 = (mTargetCell[0] + mTargetCell[1]
										* mCountX)
										+ mTargetCell[2] * cellCntPerPage;
								if (screen1 > count - 1) {
									screen1 = count - 1;
								} else if (screen1 < 0) {
									screen1 = 0;
								}
															
								int spanX = 1;
								int spanY = 1;
								if (d.dragInfo instanceof ItemInfo) {
									spanX = ((ItemInfo)d.dragInfo).spanX;
									spanY = ((ItemInfo)d.dragInfo).spanY;
									if (((ItemInfo)d.dragInfo).itemType == LauncherSettings.Favorites.ITEM_TYPE_LEOSWIDGET) {
										spanX = ((LenovoWidgetViewInfo)d.dragInfo).minWidth;
										spanY = ((LenovoWidgetViewInfo)d.dragInfo).minHeight;
									} else if (((ItemInfo)d.dragInfo).itemType == SimpleItemInfo.ACTION_TYPE_CREATE_WIDGET) {
										spanX = ((SimpleItemInfo)d.dragInfo).spanXY[0];
										spanY = ((SimpleItemInfo)d.dragInfo).spanXY[1];
									}
								}
								//判断手指所在屏是否已满，满了背景设红框，否则设黄框
								int[] findXY = workspace.findNearestVacantArea(screen1, d.x, d.y, spanX, spanY, null, null);
								if (findXY == null || findXY[0] < 0 || findXY[1] < 0) {
									view.setBackgroundDrawable(mFullBg);
								} else {
									view.setBackgroundDrawable(mCurrentBg);
								}							
							}
						} else {
							//设置非手所指屏幕的背景及大小
							itemView.setScaleX(ORIGINAL_SCALE_RATE);
							itemView.setScaleY(ORIGINAL_SCALE_RATE);
							
							if (index == previews.size() - 1 && count < MAX_CELLCOUNT) {
								view.setBackgroundDrawable(null);
								itemView.resetThumbDrawable(mAddBitmap);
							} else {
								view.setBackgroundDrawable(getDefaultBg());
							}
						}					
					}
				}
			}
						
			// If it's over the plus view, add a new view.
			//若悬停在+号屏上1.5秒，添加屏幕
			if (addItem) {
				mAddNewItemAlarm.setOnAlarmListener(mOnAddItemListener);
				mAddNewItemAlarm.setAlarm(SettingsValue.WAIT_TO_ENTER_DELAY);
			}
			
			if (screen > count - 1) {
				screen = count - 1;
			} else if (screen < 0) {
				screen = 0;
			}
			//记录有效的高亮屏INDEX（INDEX为加号时，INDEX--）
			mWorkspaceCurr = screen;
		}
	}

	@Override
	public void onDragExit(XDragObject dragObject) {
		if (!dragObject.dragComplete) {			
			if (dragObject.dragSource == this) {
				mDragObject = dragObject;
			} else {
				if (mSlidingDrawer == null) {
					return;
				}
				// tmp edition s
				if (dragObject.dragSource == this.mSlidingDrawer.getContent()) {
					mDragObject = dragObject;
				}
				// tmp edition e
                mOnExitAlarm.setOnAlarmListener(mOnExitAlarmListener);
                mOnExitAlarm.setAlarm(0);//ON_EXIT_CLOSE_DELAY
                
                mAddNewItemAlarm.cancelAlarm();
                mIntoWorkspaceAlarm.cancelAlarm();
			}
        }
        mReorderAlarm.cancelAlarm();
		
	}

	@Override
	public XDropTarget getDropTargetDelegate(XDragObject dragObject) {
		return null;
	}

	@Override
	public boolean acceptDrop(XDragObject dragObject) {
		return true;
	}

	@Override
	public void getHitRect(Rect outRect) {
		RectF rect = xPagedView.localRect;
		outRect.set((int)rect.left, (int)rect.top, (int)rect.right, (int)rect.bottom);
	}

	@Override
	public void getLocationInDragLayer(int[] loc) {
		mXLauncher.getDragLayer().getLocationInDragLayer(this, loc);
	}

	@Override
    public int getLeft() {
        return (int) getRelativeX();
    }

    @Override
    public int getTop() {
        return (int) getRelativeY();
    }
	
	private void beginDragShared(DrawableItem child) {
		((XLauncher) mXContext.getContext()).getDragLayer().getLocationInDragLayer(child, mTmpPoint);
		final int dragLayerX = mTmpPoint[0];
        final int dragLayerY = mTmpPoint[1];  
        Bitmap bitmap = child.getSnapshot(1f);
        mDragController.startDrag(bitmap, dragLayerX, dragLayerY, this, child.getTag(), SettingsValue.DRAG_ACTION_MOVE, null, null, false);
	}
	
	OnAlarmListener mOnExitAlarmListener = new OnAlarmListener() {
        public void onAlarm(Alarm alarm) {
            completeDragExit();
        }
    };
    
    /**
     * 悬停+号1.5秒后的操作
     */
    OnAlarmListener mOnAddItemListener = new OnAlarmListener() {
        public void onAlarm(Alarm alarm) {
        	int cellCntPerPage = mCountX * mCountY;
			int currPage = mTargetCell[2];
        	int screen = (mTargetCell[0] + mTargetCell[1]
					* mCountX)
					+ currPage * cellCntPerPage;
        	
        	final XWorkspace workspace = mXLauncher.getWorkspace();        	
			
        	// 添加新屏（包括操作桌面和屏幕管理格子）
        	addScreen();
        	XPagedViewItem xvi = xPagedView.findPageItemAt(mXLauncher.getWorkspace().getPagedView().getPageCount() - 1);
        	XScreenItemView addScreenItem = xvi == null ? null : (XScreenItemView)xvi.getDrawingTarget();
			if (addScreenItem != null) {
				//若为屏幕编辑页，将新屏滚动到中间；若为屏幕管理页，设置屏幕单元格的背景
				if (isAddState()) {
					XScreenMngView.this.setCurrentFocusPage(screen);
				} else {
					addScreenItem.getThumbnailDrawable().setBackgroundDrawable(mCurrentBg);
					
					addScreenItem.setScaleX(SCALE_RATE);
					addScreenItem.setScaleY(SCALE_RATE);
				}
			}
			
			int count = workspace.getPagedView().getPageCount();
			if (screen > count - 1) {
				screen = count - 1;
			} else if (screen < 0) {
				screen = 0;
			}			
			mWorkspaceCurr = screen;
        }
    };
    
    /**
     * 悬停普通屏1.5秒后的操作
     */
    OnAlarmListener mOnIntoWorkspaceListener = new OnAlarmListener() {
        public void onAlarm(Alarm alarm) {
            //add for quick drag mode by sunzq3, begin;
              ((XLauncherView)mContext).getWorkspace().getPageIndicator().startEnterAnimation();
            //add for quick drag mode by sunzq3, end;
        	int cellCntPerPage = mCountX * mCountY;
			int currPage = mTargetCell[2];//xPagedView.getCurrentPage();
        	int screen = (mTargetCell[0] + mTargetCell[1]
					* mCountX)
					+ currPage * cellCntPerPage;
        	
        	final XWorkspace workspace = mXLauncher.getWorkspace();			
			int count = workspace.getPagedView().getPageCount();
			if (screen > count - 1) {
				screen = count - 1;
			} else if (screen < 0) {
				screen = 0;
			}			
			mWorkspaceCurr = screen;
			//重设桌面的当前屏为拖动最终经过的屏
			workspace.setCurrentPage(mWorkspaceCurr);
			mXLauncher.setLauncherWindowStatus(true);
			//退出屏幕管理，进入桌面
			mXLauncher.closePreviewScreen(false);
        }
    };
    
    public void completeDragExit() {
        mCurrentDragView = null;
    }
    
    private float[] getDragPoint(int x, int y, float[] recycle) {
        float res[];
        if (recycle == null) {
            res = new float[2];
        } else {
            res = recycle;
        }

        // These represent the visual top and left of drag view if a dragRect was provided.
        // If a dragRect was not provided, then they correspond to the actual view left and
        // top, as the dragRect is in that case taken to be the entire dragView.
        // R.dimen.dragViewOffsetY.
        res[0] = x - mPaddingLeft;
        res[1] = y - mPaddingRight;

        return res;
    }
    
    /**
     * 拖动屏幕管理单元格的操作
     */
    OnAlarmListener mReorderAlarmListener = new OnAlarmListener() {
        public void onAlarm(Alarm alarm) {
        	//调换各个屏幕单元格的位置
            realTimeReorder(mEmptyCell, mTargetCell);
        }
    };
    
    /**
     * //调换各个屏幕单元格的位置
     * @param empty  源
     * @param target  目标
     */
    private void realTimeReorder(int[] empty, int[] target) {
        boolean wrap;
        boolean wrapScreen;
        int startX;
        int endX;
        int startY;
        int endY;
        int startScreen;
        int delay = 50;
        float delayAmount = 30;
        
        if (empty[0] == target[0] && empty[1] == target[1] && empty[2] == target[2]) {
        	return;
        }
        
        //for bug 14814
        mBeginMoveTime = System.currentTimeMillis();
        //for bug 14814
        
        if (readingOrderGreaterThan(target, empty)) {
        	wrap = empty[0] >= mCountX - 1;
            wrapScreen = empty[0] >= mCountX - 1 && (empty[1] >= mCountY -1);
            startScreen = wrapScreen ? empty[2] + 1 : empty[2];
            
			for (int screen = startScreen; screen <= target[2]; screen++) {
				startY = screen == empty[2] ? (wrap ? empty[1] + 1 : empty[1]) : 0;
				endY = screen < target[2] ? mCountY - 1 : target[1];
				
				for (int y = startY; y <= endY; y++) {
					startX = (screen == empty[2] && y == empty[1]) ? empty[0] + 1 : 0;
					endX = screen < target[2] ? mCountX - 1 : (y < target[1] ? mCountX - 1 : target[0]);
					
					for (int x = startX; x <= endX; x++) {
						if (xPagedView == null) {
							return;
						}
						XPagedViewItem v = xPagedView.findPageItemAt(
								screen, x, y);
						if (v == null) {
							continue;
						}
						if (xPagedView.moveItemToPosition(v, empty[0],
								empty[1], empty[2],
								REORDER_ANIMATION_DURATION, delay, null)) {
							empty[0] = x;
							empty[1] = y;
							empty[2] = screen;
							delay += delayAmount;
							delayAmount *= 0.9;
						}
					}
				}
			}
        } else {
        	wrap = empty[0] == 0;
            wrapScreen = empty[0] == 0 && empty[1] == 0;
            startScreen = wrapScreen ? empty[2] - 1 : empty[2];
            
			for (int screen = startScreen; screen >= target[2]; screen--) {
				startY = screen == empty[2] ? (wrap ? empty[1] - 1 : empty[1]) : mCountY - 1;
				endY = screen > target[2] ? 0 : target[1];
				
				for (int y = startY; y >= endY; y--) {
					startX = (screen == empty[2] && y == empty[1]) ? empty[0] - 1 : mCountX - 1;
					endX = screen > target[2] ? 0 : (y > target[1] ? 0 : target[0]);
					
					for (int x = startX; x >= endX; x--) {
						if (xPagedView == null) {
							return;
						}
						XPagedViewItem v = xPagedView.findPageItemAt(
								screen, x, y);
						if (v == null) {
							continue;
						}
						if (xPagedView.moveItemToPosition(v, empty[0],
								empty[1], empty[2],
								REORDER_ANIMATION_DURATION, delay, null)) {
							empty[0] = x;
							empty[1] = y;
							empty[2] = screen;
							delay += delayAmount;
							delayAmount *= 0.9;
						}
					}
				}
			}
        }
        //fix bug 14814
        mMoveDuration = REORDER_ANIMATION_DURATION + delay - (System.currentTimeMillis() - mBeginMoveTime);
        //fix bug 14814
    }
    
    /**
     * 判断单元格到底是往前调还是后调
     * @param v1  目标
     * @param v2  源
     * @return
     */
    boolean readingOrderGreaterThan(int[] v1, int[] v2) {
    	if (v1[2] > v2[2]
    			|| v1[2] == v2[2] && v1[1] > v2[1]
    		    || v1[2] == v2[2] && v1[1] == v2[1] && v1[0] > v2[0]) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    /**
     * 在最后添加+号单元格
     */
	private XScreenItemView addPlusView() {
		int pageCnt = mXLauncher.getWorkspace().getPagedView().getPageCount(); 
		return addPlusView(pageCnt);
	}
    
	/**
	 * 在最后添加+号单元格
	 * @param pageCnt
	 */
	private XScreenItemView addPlusView(int pageCnt) { 
		int numCells = mCountX * mCountY;
		
		PreviewInfo info = new PreviewInfo();
    	info.mDelBitmap = null;
    	info.mHomeBitmap = null;
    	info.mThumbnail = mAddBitmap;
    	info.mMiddleBitmap = mAddMarkBitmap;
    	info.screen = pageCnt / numCells;        	
    	int addIndex = pageCnt % numCells;
    	info.cellX = addIndex % mCountX;
    	info.cellY = addIndex / mCountX;
    	
    	previews.add(info);
    	
    	RectF rect = new RectF(0, 0, this.getCellWidth(), this.getCellHeight());
    	XScreenItemView preview = new XScreenItemView(info, rect, mXContext);
    	XIconDrawable previewImage = preview.getThumbnailDrawable();
    	
    	preview.setTag(info);
    	previewImage.setBackgroundDrawable(null);
		previewImage.setTag(ACT_ADD_SCREEN);
		previewImage.setOnClickListener(this);
		
		XPagedViewItem itemToAdd = new XPagedViewItem(mXContext, preview, info);
        xPagedView.addPagedViewItem(itemToAdd);
        
        return preview;
	}
	
	/**
	 * 屏幕单元格的长按点击事件
	 */
	OnAlarmListener mOnLongClickAlarmListener = new OnAlarmListener() {
        public void onAlarm(Alarm alarm) {
            setLongClick(XScreenMngView.this);
        }
    };
    
    private void setLongClick(OnLongClickListener l) {
	if (previews.isEmpty()) {
		return;
	}
	for (PreviewInfo info : previews) {
		XPagedViewItem item = xPagedView.findPageItemAt(info.screen, info.cellX, info.cellY);
	if (item == null) {
		continue;
	}
	XScreenItemView view = (XScreenItemView)(item.getDrawingTarget());
		if (view == null || view.getHomeDrawable() == null) {
			continue;
		}
	view.getThumbnailDrawable().setOnLongClickListener(l);
}
    }
    
    /**
     * 添加屏幕的操作
     */
    private void addScreen() {
    	final XWorkspace workspace = mXLauncher.getWorkspace();
    	//桌面添加屏幕
    	workspace.addNewScreen();
		
        //保存屏幕数
//		int pageCnt = workspace.getPagedView().getPageCount();
//		SharedPreferences preferrences = mXLauncher.getSharedPreferences(
//				CELLLAYOUT_COUNT, Context.MODE_PRIVATE);
//		SharedPreferences.Editor editor = preferrences.edit();
//		editor.putInt(CELLLAYOUT_COUNT, pageCnt);
//		editor.commit();
//
//		mLauncherService.mScreenCount = pageCnt;			

		//添加屏幕管理页的单元格
//		return refreshWhenAddScreen();
    }
    
    @Override
	public boolean onFingerUp(MotionEvent e) {
    	//取消屏幕选中态
    	if (previews == null) {
    		return super.onFingerUp(e);
    	}
    	ArrayList<PreviewInfo> infos = (ArrayList<PreviewInfo>)previews.clone();
    	for (PreviewInfo info : infos) {
        	XPagedViewItem item = xPagedView.findPageItemAt(info.screen, info.cellX, info.cellY);
        	if (item == null) {
        		continue;
        	}
    		XScreenItemView view = (XScreenItemView)(item.getDrawingTarget());
    		if (view == null) {
    			continue;
    		}
    		view.onTouchCancel( e );
        }
    	infos.clear();
    	infos = null;
		return super.onFingerUp(e);
    }
    
    @Override
    public boolean onFingerCancel(MotionEvent e) {
//    	if (xPagedView != null) {
//    		xPagedView.resetOffset();
//    	}
    	return super.onFingerCancel(e);
    }
    
    
    //////////////////////////////////////////Edit Mode
    /**
     * 设置屏幕的状态（NORMAL： 屏幕管理 ； ADD： 屏幕编辑）
     * @param state
     */
    public void setScreenState(State state) {
        mState = state;        
    }
    public State getScreenState() {
        return mState;        
    }

    private class XDrawerManager implements XSlidingDrawer.OnDrawerOpenListener,
            XSlidingDrawer.OnDrawerCloseListener, XSlidingDrawer.OnDrawerScrollListener {

        @Override
        public void onDrawerClosed() {
            mState = State.NORMAL;
            changeState();
        }

        @Override
        public void onDrawerOpened() {
            mState = State.ADDED;
            changeState();
        }

        @Override
        public void onScrollStarted() {
        	if (mInitTime == -1) {
        		mInitTime = System.currentTimeMillis();
        	}
        	if (xPagedView != null) {
        		xPagedView.setTouchable(false);
        	}
        }

        @Override
        public void onScrollEnded() {
        	if (xPagedView != null) {
        		xPagedView.setTouchable(true);
        	}
        }

    }

    private float getSlidingDrawerHeight() {
        float handleHeight = getXContext().getResources().getDimensionPixelSize(
                R.dimen.screen_handle_height);
        return this.getCellHeight() * (MAX_CELLY - 1) + handleHeight + getTopOffset();
    }

    private float getTopOffset() {
        float topOffset = getXContext().getResources().getDimensionPixelSize(
                R.dimen.screen_sliding_buffer_height);
        return topOffset;
    }

    /**
     * 屏幕编辑-->屏幕管理时内容区的ALPHA变化
     */
    private void animPageView() {
        if (mAlphaAnimator != null) {
            getXContext().getRenderer().ejectAnimation(mAlphaAnimator);
        }

        mAlphaAnimator = ValueAnimator.ofFloat(0.2f, 1.0f);
        mAlphaAnimator.setDuration(250);
        mAlphaAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        mAlphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float alpha = (Float) animation.getAnimatedValue();
                setPageViewItemAlpha(alpha);
            }
        });

        mAlphaAnimator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                setPageViewItemAlpha(0.2f);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setPageViewItemAlpha(1f);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });

        getXContext().getRenderer().injectAnimation(mAlphaAnimator, false);
    }

    /**
     * 更新标签卡区的内容
     */
    void updateTabContent() {
    	// tmp edition s
        if (mSlidingDrawer != null && !mSlidingDrawer.isRecycled()) {
            mSlidingDrawer.updateContent();
        }
    	// tmp edition e
    }
    
    private void setPageViewItemAlpha(float alpha, int screen) {
    	if (xPagedView == null) {
    		return;
    	}

        for (int cellY = MIN_CELLY; cellY < MAX_CELLY; cellY++) {
            for (int cellX = 0; cellX < mCountX; cellX++) {

                int index = xPagedView.getCellIndex(screen, cellX, cellY);
                xPagedView.setChildrenAlpha(index, alpha);
            }
        }
    }

    private void setPageViewItemAlpha(float alpha) {
    	setPageViewItemAlpha(alpha, xPagedView.getCurrentPage());
    }

    private float getPagedViewHeight() {    	
    	return isAddState() ? this.getCellHeight() : this.getCellHeight() * MAX_CELLY;
    }
    
    public boolean isAddState() {
    	return mState == State.ADDED;
    }
    
    private int getItemIndex(ItemInfo info) {
    	return info.screen * mCountX * mCountY + mCountX * info.cellY + info.cellX;
    }
    
    /**
     * 重新组装界面
     */
    private void doChange(boolean useOldData) {
        final XWorkspace workspace = mXLauncher.getWorkspace();
        
        if (xPagedView == null) {
        	return;
        }
        
        HashMap<Integer, XPagedViewItem> model = null;
        
        if (useOldData) {
	        model = new HashMap<Integer, XPagedViewItem>();
	        Iterator<XPagedViewItem> items = xPagedView.mItemIDMap.values().iterator();
	        
	    	while (items.hasNext()) {
	    		XPagedViewItem item = items.next();
	    		ItemInfo info = item.getInfo();
	    		model.put(getItemIndex(info) , item);
	    	}
	    	xPagedView.clearAllItemsNotDestroy();
        } else {
        	xPagedView.clearAllItems();
        	xPagedView.reuse();
        	
        	// analyze data
        	previews.clear();
        	int pageCnt = mXLauncher.getWorkspace().getPagedView().getPageCount();
    		int cnt = (pageCnt == MAX_CELLCOUNT) ? pageCnt : (pageCnt + 1);
            analyzeData(pageCnt, cnt);
        }
    	
    	if (xPageIndicator != null) {
    		xPagedView.setItemSwitchListenerUsable(false);
	    	if (isAddState()) {
	    		xPagedView.removePageSwitchListener(xPageIndicator);
	    		
	     	    xPagedView.addItemSwitchListener(xPageIndicator);
	     	    xPageIndicator.setSingleIndicatorVisible(true);
	     	} else {
	     		xPagedView.removeItemSwitchListener(xPageIndicator);
	     		
	     		xPagedView.addPageSwitchListener(xPageIndicator);
	     		xPageIndicator.setSingleIndicatorVisible(false);
	     	}
    	}
    	xPagedView.setStepMode(isAddState());
        
        int pageCnt = workspace.getPagedView().getPageCount();
		int cnt = (pageCnt == MAX_CELLCOUNT) ? pageCnt : (pageCnt + 1);

    	mCountY = isAddState() ? MIN_CELLY : MAX_CELLY;
    	
    	int page = cnt / (mCountX * mCountY);
        mNumPages = (cnt % (mCountX * mCountY)) == 0 ? page : ++page;
        
    	xPagedView.setup(mNumPages, mCountX, mCountY);
    	
        resizePagedView(localRect);
        
        final int current = Math.max(0, (workspace.getCurrentPage()) / (mCountX * mCountY));
        
    	invalidatePageData(current, model);    	

    	//若为屏幕编辑页，设置将桌面的当前屏滚动到中间
    	if (isAddState()) {
    		xPagedView.setCurrentPage(current);    		
    		setCurrentFocusPage(workspace.getCurrentPage());
    		xPagedView.setOnFocusCellChangedListener(onFocusCellChangedListener);
    		xPagedView.setItemSwitchListenerUsable(true);
    	} else {
    		xPagedView.setOnFocusCellChangedListener(null);
    		xPagedView.setCurrentPage(current);
    		if (useOldData) {
    		    animPageView();
    		}
    		mXContext.postDelayed(new Runnable() {
				@Override
				public void run() {
					if( mSlidingDrawer != null) {
					    mSlidingDrawer.resetState();
					}
				}    			
    		}, 500L);
    	}
    	handlePendingList();
    	
    	//This is a temporary solution for long click
        //设置屏幕管理页的长按事件，若在初始时就设置上，进入时会直接响应事件
        mLongClickAlarm.cancelAlarm();
        if (useOldData) {
            mLongClickAlarm.setOnAlarmListener(mOnLongClickAlarmListener);
            mLongClickAlarm.setAlarm(SET_LONG_CLICK_LISTENER);
        }
        
        xPagedView.setTouchable(true);
    }
    
    /**
     * 切换屏幕管理与屏幕编辑
     */
    private void changeState() {
    	if (mInitTime == -1) {
    		mInitTime = System.currentTimeMillis();
    	}
    	if (isAddState()) {
    		this.disappearPageView();    		
    		return;
    	}
    	
    	doChange(true);
    }

	@Override
	public void scrollLeft() {
		xPagedView.scrollToLeft(SettingsValue.SCROLL_DURATION);
	}

	@Override
	public void scrollRight() {
		xPagedView.scrollToRight(SettingsValue.SCROLL_DURATION);
	}

	@Override
	public boolean onEnterScrollArea(int x, int y, int direction) {
		if (xPagedView.getPageCount() <= 1) {
			return false;
		}
		boolean result = false;
        final int page = xPagedView.getCurrentPage()
                + (direction == XDragController.SCROLL_LEFT ? -1 : 1);
        if (0 <= page && page < xPagedView.getPageCount()) {
            result = true;
        }

        /* RK_ID: RK_SCREENMANAGER. AUT: liuli1 . DATE: 2013-05-09 . START */
        if (isAddState()) {
            int max = mXLauncher.getWorkspace().getPagedView().getPageCount();
            int current = xPagedView.getCurrentFocusCell();
            int next = current + (direction == XDragController.SCROLL_LEFT ? -1 : 1);
            if (next >= 0 && current <= max) {
                result = true;
            }
        }
        /* RK_ID: RK_SCREENMANAGER. AUT: liuli1 . DATE: 2013-05-09 . END */

		return result;
	}

	@Override
	public boolean onExitScrollArea() {
		return true;
	}

	@Override
	public boolean isScrollEnabled() {
		return xPagedView.getPageCount() > 1;
	}

	@Override
	public int getScrollWidth() {
		return (int)getWidth();
	}
	
	public void onAddSimpleInfo(int index) {
		onAddSimpleInfo(100L, index);
	}
	
	/**
	 * 刷新选中态屏幕的缩略图
	 * @param delay
	 */
	public void onAddSimpleInfo(long delay, int index) {
		final long delayTime = delay;
		mWorkspaceCurr = mXLauncher.getWorkspace().getCurrentPage();
		final int itemIndex = (index <= 0 && index >= previews.size())
				? mWorkspaceCurr : index;
		mXContext.postDelayed(new Runnable(){
			@Override
			public void run() {
				updateThumb(itemIndex);
			}
		}, delayTime);
	}

	@Override
	public int getScrollLeftPadding() {
		return mPagePaddingLeft + XScreenItemView.getWidthGap(getXContext().getContext());
	}
	
	/**
	 * 处理来自于屏幕编辑的文件夹/其它小部件/快捷方式
	 * @param simpleInfo
	 */
	private void handleSimpleItemInfo(SimpleItemInfo simpleInfo, int screen, int[] findXY, int[] touchXY) {
		switch (simpleInfo.actionType) {
		case SimpleItemInfo.ACTION_TYPE_ADD_SHORTCUT:
			mXLauncher.resetPendingInfoBeforePick();
			if(simpleInfo.filterSpecialShortcut()){
				Intent i = new Intent();
				ComponentName c = simpleInfo.intent.getComponent();
				if(c != null){
					i.setComponent(c);
					i.setAction(Intent.ACTION_MAIN);
					mXLauncher.completeAddSpecialShortcut(i);
				}
			}else{
				mXLauncher.startActivityForResult(simpleInfo.intent, XLauncher.REQUEST_CREATE_SHORTCUT);
			}
			
			break;
		case SimpleItemInfo.ACTION_TYPE_ADD_FOLDER:
			mXLauncher.resetPendingInfoBeforePick();
			mXLauncher.workspacePickApplication(true);
			break;
		case SimpleItemInfo.ACTION_TYPE_ADD_OTHER_WIDGET:
			mXLauncher.resetAddInfo();
			mXLauncher.pickupOtherWidgets();
			break;
		case SimpleItemInfo.ACTION_TYPE_CREATE_WIDGET:
			final AppWidgetProviderInfo widgetInfo = simpleInfo.widgetProviderInfo;				
			mXLauncher.addAppWidgetFromSlidingBar(widgetInfo, LauncherSettings.Favorites.CONTAINER_DESKTOP,
                    screen, findXY, touchXY);
			break;
		default:
			break;
		}
	}
	
	/**
	 * 设置屏幕选中单元格，使其移到正中
	 * @param pageIndex
	 */
	private void setCurrentFocusPage(int pageIndex) {
		setCurrentFocusPage(pageIndex, false);
	}
	
	/**
	 * 设置屏幕选中单元格，使其移到正中
	 * @param pageIndex
	 */
	private void setCurrentFocusPage(int pageIndex, boolean immediate) {
		xPagedView.setCurrentFocusCell(pageIndex, immediate);
	}
	
	/**
	 * 屏幕选中单元个发生变化的监听器
	 */
	private OnFocusCellChangedListener onFocusCellChangedListener = new OnFocusCellChangedListener() {

		@Override
		public void onFocusCellChanged(int oldIndex, final int newIndex) {
			//若为屏幕管理页，不响应
			if (!isAddState()) {
				return;
			}			
			
			//设置旧的选中单元格的参数
			if (oldIndex >= 0 && oldIndex < previews.size()) {
				PreviewInfo info = previews.get(oldIndex);
				XPagedViewItem item = xPagedView.findPageItemAt(info.screen, info.cellX, info.cellY);
				if (item != null) {
					XScreenItemView itemView = (XScreenItemView)(item.getDrawingTarget());
					//若旧的选中单元格为+号，去掉背景图，否则设置普通单元格
					itemView.getThumbnailDrawable().setBackgroundDrawable(info.mHomeBitmap == null ? null : getDefaultBg());
				}
			}
			
			if (mWorkspaceCurr != oldIndex
					&& mWorkspaceCurr >= 0
					&& mWorkspaceCurr < previews.size()) {
				PreviewInfo info = previews.get(mWorkspaceCurr);
				XPagedViewItem item = xPagedView.findPageItemAt(info.screen, info.cellX, info.cellY);
				if (item != null) {
					XScreenItemView itemView = (XScreenItemView)(item.getDrawingTarget());
					//若旧的选中单元格为+号，去掉背景图，否则设置普通单元格
					itemView.getThumbnailDrawable().setBackgroundDrawable(info.mHomeBitmap == null ? null : getDefaultBg());
				}
				
			}
			
			//设置新的选中单元格的参数
			if (newIndex >= 0 && newIndex < previews.size()) {
				//若新的选中单元格为加号，设置其前面的单元格
				final int index = (mXLauncher.getWorkspace().getPageCount() < MAX_CELLCOUNT && newIndex == previews.size() -1)
						? newIndex -1 : newIndex;
				
				mWorkspaceCurr = index;
				
				PreviewInfo info = previews.get(index);
				XPagedViewItem item = xPagedView.findPageItemAt(info.screen, info.cellX, info.cellY);
				if (item != null) {
					XScreenItemView itemView = (XScreenItemView)(item.getDrawingTarget());
					//根据新的焦点屏是否已满，设置单元格的背景为红框或黄框
					ArrayList<Point> emptyPoint = mXLauncher.getWorkspace().getPagedView().findVacantCellNumber(index);
					itemView.getThumbnailDrawable().setBackgroundDrawable((emptyPoint == null || emptyPoint.size() <= 0)
	        				? mFullBg : mCurrentBg);
				}
				
				// 设置桌面的当前屏
				mXContext.post(new Runnable() {
					@Override
					public void run() {						
				        mXLauncher.getWorkspace().setCurrentPage(index);
					}
				});
			}
		}
	};
	
	/**
	 * 屏幕管理--> 屏幕编辑时的内容区的ALPHA变化
	 */
	private void disappearPageView() {
        if (mDisappearAnimator != null) {
            getXContext().getRenderer().ejectAnimation(mDisappearAnimator);
        }

        mDisappearAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        mDisappearAnimator.setDuration(150);
        mDisappearAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        mDisappearAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float alpha = (Float) animation.getAnimatedValue();
                setPageViewItemAlpha(1 - alpha);
            }
        });

        mDisappearAnimator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                setPageViewItemAlpha(1.0f);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setPageViewItemAlpha(0.0f);
                doChange(true);
//                mHandler.removeCallbacks(doChangeRunnable);
//                mHandler.post(doChangeRunnable);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });

        getXContext().getRenderer().injectAnimation(mDisappearAnimator, false);
    }
	
	private void handlePendingList() {
		if (mPendingRunningList == null) {
			return;
		}
		if (mPendingRunningList.isEmpty()) {
			mInitTime = -1;
		}
				
		if (!mPendingRunningList.containsKey(mInitTime)) {
			return;
		}
		Runnable runnable = mPendingRunningList.get(mInitTime);
		mXContext.post(runnable);
		mPendingRunningList.remove(mInitTime);
		mPendingRunningList.clear();
		mInitTime = -1;
	}
	
	private void resizePagedView(RectF rect) {
		if (xPagedView != null) {
			xPagedView.resize(new RectF(mPagePaddingLeft, 
					mPagePaddingTop, 
					rect.width() - mPagePaddingRight, 
					mPagePaddingTop + getPagedViewHeight()));
		}
		
		if( xPageIndicator != null ){
			//modify for quick drag mode by sunzq3, begin;
			xPageIndicator.resize(new RectF(0, 0, getWidth(), xPageIndicator.getHomePointHeight()));			
			xPageIndicator.setRelativeY(mPagePaddingTop + getPagedViewHeight() - mThumbGap
					+ (mThumbGap + mPagePaddingBottom - xPageIndicator.getHomePointHeight()) / 2.0f);
			//modify for quick drag mode by sunzq3, end;
		}
	}
	
	private Drawable getDefaultBg() {
		Drawable bg = mXLauncher.getResources().getDrawable(R.drawable.preview_border);
		return bg;
	}
	
	private Runnable mRemoveMainMsgRunnable = new Runnable() {
		@Override
		public void run() {
			if (mRemoveMainMsg == null) {
				mRemoveMainMsg = Toast.makeText(mXLauncher, R.string.home_delet_prompt,
					Toast.LENGTH_SHORT);
			} else{
				mRemoveMainMsg.setText(R.string.home_delet_prompt);
			}
			mRemoveMainMsg.show();			
		}				
	};
	
	private Runnable mRemoveNotEmptyMsgRunnable = new Runnable() {
		@Override
		public void run() {
			if (mRemoveNotEmptyMsg == null) {
				mRemoveNotEmptyMsg = Toast.makeText(mXLauncher, R.string.not_blank_delet_prompt,
					Toast.LENGTH_SHORT);
			} else{
				mRemoveNotEmptyMsg.setText(R.string.not_blank_delet_prompt);
			}
			mRemoveNotEmptyMsg.show();			
		}				
	};

	private void setContentCurrentPage(int[] page) {
		if (mSlidingDrawer != null) {
			mSlidingDrawer.setContentPage(page);
		}
	}

	private int[] getContentCurrentPage() {
		int[] current = new int[] {0, 0};
		if (mSlidingDrawer != null) {
			return mSlidingDrawer.getCurrentPage();
		}
		return current;
	}
	
	private int[] getContentPageCount() {
		int[] count = new int[] {0, 0};
		if (mSlidingDrawer != null) {
			return mSlidingDrawer.getPageCount();
		}
		return count;
	}

    void resetPagedView() {
        if (xPagedView != null) {
            xPagedView.resetAnim();
        }
        if(mSlidingDrawer!=null) {
            mSlidingDrawer.reset();
        }
    }

    void setWorkspaceScale(XWorkspace workspace, float previewScreenWidth, float previewScreenHeight) {
        float paddingY = getXContext().getResources().getDimensionPixelOffset(
                R.dimen.xscreen_mng_thumbnail_margin_top);

        float newHeight = workspace.getHeight() + workspace.getTop();
        float scaleW = previewScreenWidth / (workspace.getWidth() * 1.0f);
        float scaleH = (previewScreenHeight - paddingY - getHomeHeight()) / (newHeight * 1.0f);

        mWorkspaceScale = Math.min(scaleW, scaleH);
        mWorkspaceTranX = (previewScreenWidth - mWorkspaceScale * workspace.getWidth()) / 2;
        Log.i(TAG, "mWorkspaceScale ====" + mWorkspaceScale + "    mWorkspaceTranX ==="
                + mWorkspaceTranX);
    }

    int[] initTargetCellCoord(XWorkspace workspace, int[] targetCell, int spanX, int spanY) {
        int cellWidth = workspace.getPagedView().getCellWidth();
        int cellHeight = workspace.getPagedView().getCellHeight();
        float centerX = cellWidth * (spanX / 2f + targetCell[0]);
        float centerY = cellHeight * (spanY / 2f + targetCell[1]);
        Log.i(TAG, "info.spanX === " + spanX + "    info.spanY ====" + spanY);
        Log.i(TAG, "centerX === " + centerX + "    centerY ====" + centerY);

        int widthGap = XScreenItemView.getWidthGap(getXContext().getContext());
        float paddingX = mPagePaddingLeft + xPagedView.getCellWidth() + widthGap + mWorkspaceTranX;
        float paddingY = getXContext().getResources().getDimensionPixelOffset(
                R.dimen.xscreen_mng_thumbnail_margin_top);
        Log.i(TAG, "paddingX === " + paddingX);

        int targetXY[] = new int[2];
        targetXY[0] = (int) (centerX * mWorkspaceScale + paddingX);
        targetXY[1] = (int) (centerY * mWorkspaceScale + paddingY) + SettingsValue.getExtraTopMargin();

        Log.i(TAG, "targetX === " + targetXY[0] + "    targetY ====" + targetXY[1]);
        return targetXY;
    }
    
    /**
     * 判断一个屏幕是否可以删除
     * @param index
     * @return
     */
    private boolean isRemovablePage(int index) {
		// 删除桌面的某个屏幕前，先要删除该屏幕中的所有元素
		ArrayList<XPagedViewItem> items = mXLauncher.getWorkspace().getPagedView()
				.getChildrenAt(index);
		if (items.size() > 0) {
			return false;
		}
//		for (int i = 0; i < items.size(); i++) {
//			XPagedViewItem item = items.get(i);
//			ItemInfo info = item.getInfo();
//
//			if (info.itemType != LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET
//					&& info.itemType != LauncherSettings.Favorites.ITEM_TYPE_COMMEND_APPWIDGET
//					&& info.itemType != LauncherSettings.Favorites.ITEM_TYPE_LEOSWIDGET) {
//                return false;
//			}
//		}
		return true;
    }
    
    private XIconDrawable mSnap;
    /**
     * 退出屏幕管理时，将当前页的截图设置成空，方便与桌面的放大操作配合
     */
    public void setEmptyThumb() {
    	if (mSnap != null) {
    		mSnap.setVisibility(false);
    	}
    	
    	int index = mXLauncher.getWorkspace().getCurrentPage();
    	if (previews == null || index >= previews.size()) {
    		return;
    	}
        previews.get(index).mThumbnail = mEmptyBitmap;
    	
        if (xPagedView == null) {
        	return;
        }
    	XPagedViewItem item = xPagedView.findPageItemAt(index);
    	if (item == null
    			|| item.getDrawingTarget() == null) {
    		return;
    	}
		XScreenItemView view = (XScreenItemView)(item.getDrawingTarget());

		view.resetThumbDrawable();
		
		if (mSlidingDrawer != null) {
		    mSlidingDrawer.setVisibility(false);
		}
    }
    
    /**
     * 刷新所有截图，主要用于屏幕管理/编辑页面的横竖屏切换
     */
    void refreshThumbs() {
    	refreshThumbs( 1200L);
    }
    
    void refreshThumbs(long delay) {
    	try {
			if (mXLauncher.getWorkspace().getPagedView() != null
					&& (isVisible() || mXLauncher.isAnimPreviewScreen())
					&& !mXLauncher.isAnimCloseScreen()) {
				for (int i = 0; i < mXLauncher.getWorkspace().getPagedView().getPageCount(); i++) {
					final int page = i;
					getXContext().postDelayed(new Runnable() {

						@Override
						public void run() {
							updateThumb( page, false );
							invalidate();
						}
					}, delay);
				}
				
				counter = 0;
				mThumbHandler.removeMessages( 0 );
				mThumbHandler.sendEmptyMessageDelayed( 0, 2000L );
			}
		} catch (Exception e) {
		}
    }
    
    /**
     * 加载小部件页。
     */
    void addWidgetContent() {
    	if (mSlidingDrawer != null) {
    		mSlidingDrawer.addWidgetContent();
    	}
    }
    
    /**
     * 屏幕编辑时，当前页的最后一个子项的中心点X坐标
     * @return
     */
    private float getLastItemRelativeX() {
    	float result = (mPagePaddingLeft + (mCountX - 1 + 0.5f) * getCellWidth());
    	return result;
    }
    
    /**
     * 若拖动应用/小部件到桌面时，手指的按压动作异常取消时，取消此次的拖动动作
     */
    public void cancelDragForWorkspace() {
    	if (mDragController != null) {
    		XDragSource source = mDragController.getDraggingObjectSource();
    		if (source == null || !(source instanceof XScreenContentTabHost)) {
    			return;
    		}
    		mDragController.onTouchCancel();
    	}
    }
    
    /**
     * 屏幕横竖屏切换后调用的代码
     */
    public void onConfigurationChange() {
	    setLongClick(null);
	
    	int currentPage = xPagedView.getCurrentPage();
    	int[] currentContentPage = getContentCurrentPage();
    	
    	refreshThumbs();
    	
    	mAddBitmap = ((BitmapDrawable)(mXLauncher.getResources().getDrawable(R.drawable.preview_addscreen))).getBitmap();
        if(SettingsValue.isBlade8Pad()){
    	mAddBitmap = ((BitmapDrawable)(mXLauncher.getResources().getDrawable(R.drawable.preview_addscreen_8))).getBitmap();
        }    	
		mAddMarkBitmap = ((BitmapDrawable)(mXLauncher.getResources().getDrawable(R.drawable.preview_addscreen_middle))).getBitmap();
    	mAddPressedBitmap = ((BitmapDrawable)(mXLauncher.getResources().getDrawable(R.drawable.preview_addscreen_light))).getBitmap();
        mDelBitmap = ((BitmapDrawable)(mXLauncher.getResources().getDrawable(R.drawable.preview_del_btn))).getBitmap();
        mHomeBitmap = ((BitmapDrawable)(mXLauncher.getResources().getDrawable(R.drawable.preview_home_btn_light))).getBitmap();
        mNotHomeBitmap = ((BitmapDrawable)(mXLauncher.getResources().getDrawable(R.drawable.preview_home_btn))).getBitmap();        
        mCurrentBg = mXLauncher.getResources().getDrawable(R.drawable.preview_border_light);
        mFullBg = mXLauncher.getResources().getDrawable(R.drawable.preview_border_full);
        
        if (mCurrentDragView != null) {        	
//        	if (mSnapList.size() != mXLauncher.getWorkspace().getPageCount()) {
//        		PreviewInfo info = (PreviewInfo)mCurrentDragView.getTag();
//        	    mSnapList.add(this.currentDragViewOldIndex, info.mThumbnail);
//        	}
        	
    		mDragObject = null;
    		mCurrentDragView = null;
        	mDragController.cancelDrag();        	
        }
        
        doChange(false);
                
        if( mSlidingDrawer != null) {
		    mSlidingDrawer.onConfigurationChange();
		}
        
        if (isAddState()) {
        	int[] contentPageCount = getContentPageCount();
        	for (int i = 0; i < contentPageCount.length; i++) {
        		if (currentContentPage[i] >= contentPageCount[i]) {
        			currentContentPage[i] = contentPageCount[i] - 1;
            	}
        	}
        	setContentCurrentPage(currentContentPage);
        } else {
        	int pageCount = xPagedView.getPageCount();
        	if (currentPage >= pageCount) {
        		currentPage = pageCount - 1;
        	}
        	xPagedView.setCurrentPage(currentPage);
        }
                
        mLongClickAlarm.cancelAlarm();
        mLongClickAlarm.setOnAlarmListener(mOnLongClickAlarmListener);
        mLongClickAlarm.setAlarm(SET_LONG_CLICK_LISTENER);
    }
    
    public int getPageCount() {
    	return xPagedView == null ? 0 : xPagedView.getPageCount();
    }
    
    private Alarm mOnRefreshOnAddScreenAlarm = null;
    OnAlarmListener mOnRefreshOnAddScreenListener = new OnAlarmListener() {
        public void onAlarm(Alarm alarm) {
        	doRefreshOnAddScreen();
        }
    };
    
    private void doRefreshOnAddScreen() {
    	if (isAddState()) {
    		mXLauncher.getWorkspace().setCurrentPage(mXLauncher.getWorkspace().getPageCount() - 1);
        }
    	
    	int imageWidth = (int)(getCellWidth() - XScreenItemView.getWidthGap(mXLauncher) * 2);
    	int imageHeight = (int)(getCellHeight() - XScreenItemView.getHeightGap(mXLauncher) * 2);
    	if (mEmptyBitmap == null) {
			mEmptyBitmap = Bitmap.createBitmap(
					imageWidth, imageHeight, Config.ARGB_8888);
		}
		mSnapList.add(mEmptyBitmap);
    	addScreenItem();
    }
    
    public boolean isPageDragging() {
    	if (mSnapList == null || previews == null) {
    		return false;
    	}
    	return mSnapList.size() != previews.size();
    }
    
    public void refreshOnAddScreen() {
    	boolean isDragging = isPageDragging();
    	if (isDragging) {
    		mOnRefreshOnAddScreenAlarm = new Alarm();
	    	mOnRefreshOnAddScreenAlarm.cancelAlarm();
	    	mOnRefreshOnAddScreenAlarm.setOnAlarmListener(mOnRefreshOnAddScreenListener);
	    	return;
    	}
    	doRefreshOnAddScreen();
    }
    
    
    private Alarm mOnUpdateScreenAlarm  = null;
    OnAlarmListener mOnUpdateScreenListener = new OnAlarmListener() {
        public void onAlarm(Alarm alarm) {
        	refreshThumbs(500L);
        }
    };
    
    public void refreshOnUpdateWorkspace(List<Integer> screenList) {
    	if (screenList == null) {
    		return;
    	}
    	try {
    		boolean isDragging = isPageDragging();
        	if (isDragging) {
	        	mOnUpdateScreenAlarm = new Alarm();
	        	mOnUpdateScreenAlarm.cancelAlarm();
	        	mOnUpdateScreenAlarm.setOnAlarmListener(mOnUpdateScreenListener);
	        	return;
        	}
        	
        	refreshThumbs(500L);
		} catch (Exception e) {
		}
    }
    
    
    //------------for resize update 
    
    final static int COUNT = 6;
    int counter = 0;
    private Handler mThumbHandler = new Handler(){
    	public void handleMessage(android.os.Message msg) {
    		// we expect msg.what is screen index multi MSG_MULTI_FACTOR
//			synchronized (mSnapList) {
//				android.util.Log.i( "ME", "MIAO WU2 . " + mSnapList);
    		counter ++;
    		if( counter > COUNT ){
    			removeMessages( 0 );
    			return;
    		}
				if (mSnapList != null && !mSnapList.isEmpty()) {
					final XWorkspace workspace = mXLauncher.getWorkspace();
					workspace.setWidgetVisible(true);
					final int extTop = mXLauncher.getResources().getDimensionPixelOffset(
							R.dimen.xscreen_mng_thumbnail_margin_top);
					final float newHeight = workspace.getHeight() + workspace.getTop();
					final float previewScreenWidth = getCellWidth() - 2
							* XScreenItemView.getWidthGap(mXLauncher);
					final float previewScreenHeight = getCellHeight()
							- XScreenItemView.getHeightGap(mXLauncher) - getHomeHeight() - extTop;
					final float scaleW = previewScreenWidth / (workspace.getWidth() * 1.0f);
					final float scaleH = previewScreenHeight / (newHeight * 1.0f);
					final float scale = Math.min(scaleW, scaleH);
					final float tranX = (previewScreenWidth - scale * workspace.getWidth()) / 2;
					final float tranY = extTop;
					
					try {
						for (final Bitmap bp : mSnapList) {
							if (bp.isMutable() && !bp.isRecycled()) {
								NormalDisplayProcess p = new NormalDisplayProcess();
								bp.eraseColor(0); 
								p.beginDisplay(bp);
								p.getCanvas().save();
								p.getCanvas().translate((int) tranX, (int) tranY);
								p.getCanvas().scale(scale, scale);
								mXLauncher.getWorkspace().draw(p, mSnapList.indexOf(bp));
								p.getCanvas().restore();
								p.endDisplay();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

//				mSnapList.notifyAll();
//			}
    		
			if( mSnapList != null && mSnapList.isEmpty() ){
				this.removeMessages( 0 );
			}else{
				if( mThumbHandler.hasMessages( 0 ) ){
					mThumbHandler.removeMessages( 0 );
				}
				mThumbHandler.sendEmptyMessageDelayed( 0, 800L );
			}
			mXContext.invalidate();
    	};
    };
    
    
    public boolean onDown(MotionEvent e) {
    	mThumbHandler.removeMessages( 0 );
    	return super.onDown(e);
    };
    
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    	mThumbHandler.sendEmptyMessageDelayed( 0, 800L );
    	return super.onFling(e1, e2, velocityX, velocityY);
    }
    
    
}
