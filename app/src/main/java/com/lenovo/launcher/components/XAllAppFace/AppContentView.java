package com.lenovo.launcher.components.XAllAppFace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.slimengine.BaseDrawableGroup;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IController;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem.OnVisibilityChangeListener;
import com.lenovo.launcher.components.XAllAppFace.utilities.Utilities;
import com.lenovo.launcher.components.XAllAppFace.utilities.Debug.D2;
import com.lenovo.launcher2.commoninterface.ApplicationInfo;
import com.lenovo.launcher2.commoninterface.HiddenApplist;
import com.lenovo.launcher2.commoninterface.UsageStatsMonitor;
import com.lenovo.launcher2.customizer.Debug.R2;
import com.lenovo.launcher2.customizer.LauncherPersonalSettings;
import com.lenovo.launcher2.customizer.SettingsValue;


public class AppContentView extends BaseDrawableGroup implements XDropTarget, XDragSource {

	public interface PageSwitchListener {

		void onUpdatePage(int pageCount);

		void onPageSwitching(int from, int to, float percentage);
	}
	
	/*** RK_ID: RK_SLIDEEFFECT  AUT: zhaoxy . DATE: 2012-12-10 . START***/
	public interface PageDrawAdapter {
	    /**
	     * @param canvas
	     * @param page 当前页码
	     * @param offsetX -1f~1f，表示当前页的横向偏移量, -1f表示完全偏移到上一屏, 1f表示偏移到下一屏
	     */
		public void drawPage(IDisplayProcess canvas, int page, float offsetX, float offsetY);
		public void reset();
	}
	/*** RK_ID: RK_SLIDEEFFECT  AUT: zhaoxy . DATE: 2012-12-10 . END***/
	
	private static final int MSG_NEXG_PAGE = 100;
//	private static HandlerThread mAnimThread = new HandlerThread(
//			"Anim-Thread");
//	static {
//		mAnimThread.start();
//	}
//
//	Handler mAnimHandler = new Handler(mAnimThread.getLooper()) {
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//
//			if (msg.what == MSG_NEXG_PAGE) {
//				nextPage();
//			}
//		}
//	};

	private PageSwitchListener pageSwitcher;

	private static final String TAG = "AppContentView";
	private static final int ORI_RIGHT = 1;
	private static final int ORI_NONE = 0;
	private static final int ORI_LEFT = -1;
	private static final int MaxCellCountX = 4;
	private static final int MaxCellCountY = 6;
	private int mCellWidth = -1;
	private int mCellCountX = 4;
	private int mCellCountY = 5;
	private final int mDefaultCellCountX;
	private int mDefaultCellCountY;
	private int mCellHeight = -1;
	private int mOriginalCellWidth;
    private int mOriginalCellHeight;
    private int mOriginalWidthGap;
    private int mOriginalHeightGap;
    private int mWidthGap;
    private int mHeightGap;
    private int mMaxGap;
    /* RK_ID: RK_SETTINGS. AUT: liuli1 . DATE: 2012-04-18 . START */
    private int mMaxYGap;
    /* RK_ID: RK_SETTINGS. AUT: liuli1 . DATE: 2012-04-18 . END */

	private int mPageCount = -1;
	private int mCurrentPage = -1;

	private float mOffsetX = 0;
	private float mOffsetXTarget = 0;

	/*** RK_ID: RK_SLIDEEFFECT  AUT: zhaoxy . DATE: 2012-12-10 . START***/
	private float mOffsetY = 0;
	private float mOffsetYTarget = 0;
	
	private float rect2ball = 0;
	private float rect2ballTarget = 0;
	/*** RK_ID: RK_SLIDEEFFECT  AUT: zhaoxy . DATE: 2012-12-10 . END***/

	/*** RK_ID: DRAG. AUT: zhaoxy . DATE: 2013-02-19 . START ***/
	private XDragController mDragController;
	/*** RK_ID: DRAG. AUT: zhaoxy . DATE: 2013-02-19 . END ***/

	ArrayList<ApplicationInfo> infos = new ArrayList<ApplicationInfo>();
	
	private ArrayList<ApplicationInfo> mAppRecentTasks = new ArrayList<ApplicationInfo>();
    private ArrayList<ApplicationInfo> mAppRunningTasks = new ArrayList<ApplicationInfo>();

    /*** RK_ID: RK_SLIDEEFFECT  AUT: zhaoxy . DATE: 2012-12-10 . START***/
	private static final long OffsetXAnimDuration = 700;
	private int mDurationExtra = 0;
	private static final long OffsetYAnimDuration = 400;
	/*** RK_ID: RK_SLIDEEFFECT  AUT: zhaoxy . DATE: 2012-12-10 . END***/
	private boolean isLoop = true;

    private boolean isPageMoving = false;
	private int currOrientation = 0;
	
	private PageDrawAdapter mPageDrawAdapter;
	private PageAnimController mAnimController;
	private XContext mContext;
//	private Launcher mLauncher;
	private ActivityManager mActivityManager;
	private String mCurrentTag = AppContentTabHost.APPS_TAB_TAG;
	private boolean mEditMode = false;
	private String sPrefAppListSlide;

	private DrawableItem mDraggingItemOrigin;
	private DrawableItem mDraggingItemShadow;

	private final Vibrator mVibrator;
	private static final int VIBRATE_DURATION = 35;

	private boolean isDataReady = false;
	private Resources resources;
	//marked by dining 2013-07-20 the string com.lenovo.xlauncher need not to remove or change
	private static final String[] EXCEPTION_RUNNING_LIST = {
        "com.lenovo.launcher",
        "com.android.settings",
        "com.lenovo.leos.widgets.weather",
        "com.lenovo.leos.hw",
        "com.lenovo.xlauncher"
    };
	private int mphoneindex =-1;
	public void setPageSwitchListener(PageSwitchListener pl) {
		this.pageSwitcher = pl;
	}

	public AppContentView(RectF rect, XContext context) {
		super( context );
		isDataReady = false;
        // setup default cell parameters
		resources = context.getResources();
		mphoneindex = SettingsValue.getCurrentMachineType(context.getContext());
/*kangwei3
        mOriginalCellWidth = mCellWidth = resources.getDimensionPixelSize(R.dimen.apps_customize_cell_width);
        mOriginalCellHeight = mCellHeight = resources.getDimensionPixelSize(R.dimen.apps_customize_cell_height);
        mOriginalWidthGap = mWidthGap = resources.getDimensionPixelSize(R.dimen.apps_customize_pageLayoutWidthGap);
        mOriginalHeightGap = mHeightGap = resources.getDimensionPixelSize(R.dimen.apps_customize_pageLayoutHeightGap);
        mPaddingTop = resources.getDimensionPixelSize(R.dimen.apps_customize_pageLayoutPaddingTop);
        mPaddingBottom = resources.getDimensionPixelSize(R.dimen.apps_customize_pageLayoutPaddingBottom);
        mPaddingLeft = resources.getDimensionPixelSize(R.dimen.apps_customize_pageLayoutPaddingLeft);
        mPaddingRight = resources.getDimensionPixelSize(R.dimen.apps_customize_pageLayoutPaddingRight);
        mMaxGap = resources.getDimensionPixelSize(R.dimen.apps_customize_max_gap);
        mMaxYGap = resources.getDimensionPixelSize(R.dimen.apps_customize_max_y_gap);
  */      
		resize(rect);
		mAnimController = new PageAnimController();
		registerIController(mAnimController);
		
//		this.setController(mAnimController);
		mContext = context;
		mActivityManager = (ActivityManager)mContext.getContext().getSystemService(Context.ACTIVITY_SERVICE);
		mVibrator = (Vibrator) mContext.getContext().getSystemService(Context.VIBRATOR_SERVICE);

        /* RK_ID: RK_SETTINGS. AUT: liuli1 . DATE: 2012-04-18 . START */
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext.getContext());
        mDefaultCellCountX = sp.getInt(SettingsValue.CELLX_COUNT_OF_ALLAPP, Integer.MAX_VALUE);
        mDefaultCellCountY = sp.getInt(SettingsValue.CELLY_COUNT_OF_ALLAPP, Integer.MAX_VALUE);
        /* RK_ID: RK_SETTINGS. AUT: liuli1 . DATE: 2012-04-18 . END */

        // visible listen always
        this.wantKnowVisibleState(true);
        this.setOnVisibilityChangeListener( new OnVisibilityChangeListener() {
			
			@Override
			public void onVisibilityChange(DrawableItem who, boolean visible) {
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
        if(mphoneindex == 0){//7 inch
            mCellCountX = 6;
            mCellCountY = 4;
        }else if(mphoneindex == 1){//10 inch
            mCellCountX = 8;
            mCellCountY = 6;
        }else{
            mCellCountX = Math.max(maxCellCountX, estimateCellHSpan(width));
            mCellCountY = Math.min(maxCellCountY, estimateCellVSpan(height));
        }
    }
	
	public int getPaddingBottomForIndicator(){
		return (int) (getHeight() - mPaddingBottom - 5);
	}
	
    /**
     * Estimates the number of cells that the specified width would take up.
     */
    public int estimateCellHSpan(int width) {
        // We don't show the next/previous pages any more, so we use the full width, minus the
        // padding
        int availWidth = width - (mPaddingLeft + mPaddingRight);

        // We know that we have to fit N cells with N-1 width gaps, so we just juggle to solve for N
        int n = Math.max(1, (availWidth + mWidthGap) / (mCellWidth + mWidthGap));

        // We don't do anything fancy to determine if we squeeze another row in.
        return n;
    }

    /**
     * Estimates the number of cells that the specified height would take up.
     */
    public int estimateCellVSpan(int height) {
        // The space for a page is the height - top padding (current page) - bottom padding (current
        // page)
        int availHeight = height - (mPaddingTop + mPaddingBottom);

        // We know that we have to fit N cells with N-1 height gaps, so we juggle to solve for N
        int n = Math.max(1, (availHeight + mHeightGap) / (mCellHeight + mHeightGap));

        // We don't do anything fancy to determine if we squeeze another row in.
        return n;
    }
	
	public void setPageDrawAdapter(PageDrawAdapter mDrawAdapter) {
        if (mDrawAdapter != this.mPageDrawAdapter) {
            this.mPageDrawAdapter = mDrawAdapter;
            this.mPageDrawAdapter.reset();
            if (mAnimController != null) {
                mAnimController.setSphereSlide(false);
            }
        }
	}

	/*** fixbug 6869  . AUT: zhaoxy . DATE: 2013-01-31. START***/
	public void setDataIsReady() {
	    mDefaultCellCountY = Integer.MAX_VALUE;
	}
	/*** fixbug 6869  . AUT: zhaoxy . DATE: 2013-01-31. END***/
    /*** fixbug 7638 . AUT: zhaoxy . DATE: 2013-02-26. START ***/
    private int mTempHeight = -1;
    /*** fixbug 7638 . AUT: zhaoxy . DATE: 2013-02-26. END ***/

	public void resize(RectF rect) {
        mOriginalCellWidth = mCellWidth = resources.getDimensionPixelSize(R.dimen.apps_customize_cell_width);
        mOriginalCellHeight = mCellHeight = resources.getDimensionPixelSize(R.dimen.apps_customize_cell_height);
        mOriginalWidthGap = mWidthGap = resources.getDimensionPixelSize(R.dimen.apps_customize_pageLayoutWidthGap);
        mOriginalHeightGap = mHeightGap = resources.getDimensionPixelSize(R.dimen.apps_customize_pageLayoutHeightGap);
        mPaddingTop = resources.getDimensionPixelSize(R.dimen.apps_customize_pageLayoutPaddingTop);
        mPaddingBottom = resources.getDimensionPixelSize(R.dimen.apps_customize_pageLayoutPaddingBottom);
        mPaddingLeft = resources.getDimensionPixelSize(R.dimen.apps_customize_pageLayoutPaddingLeft);
        mPaddingRight = resources.getDimensionPixelSize(R.dimen.apps_customize_pageLayoutPaddingRight);
        mMaxGap = resources.getDimensionPixelSize(R.dimen.apps_customize_max_gap);
        mMaxYGap = resources.getDimensionPixelSize(R.dimen.apps_customize_max_y_gap);

		if (rect != null && rect.width() > 0 && rect.height() > 0) {
            /*** fixbug 7638 . AUT: zhaoxy . DATE: 2013-02-26. START ***/
            if (mTempHeight > 0) {
                if (mTempHeight != rect.height()) {
                    mDefaultCellCountY = Integer.MAX_VALUE;
                    mTempHeight = (int) rect.height();
                }
            } else {
                mTempHeight = (int) rect.height();
            }
            /*** fixbug 7638 . AUT: zhaoxy . DATE: 2013-02-26. END ***/
			this.localRect.set(rect);
			setInvertMatrixDirty();

            /* RK_ID: RK_TASKKILLBAR. AUT: liuli1 . DATE: 2012-11-30 . START */
            mHeightGap = mOriginalHeightGap;
            calculateCellCount((int) rect.width(), (int) rect.height(), MaxCellCountX,
                    MaxCellCountY);
            /* RK_ID: RK_TASKKILLBAR. AUT: liuli1 . DATE: 2012-11-30 . END */
            
            /* RK_ID: RK_SETTINGS. AUT: liuli1 . DATE: 2012-04-18 . START */
            if (mDefaultCellCountX == Integer.MAX_VALUE && mDefaultCellCountY == Integer.MAX_VALUE) {
                // first initialize
                mDefaultCellCountY = mCellCountY;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext.getContext()).edit();
                        editor.putInt(SettingsValue.CELLX_COUNT_OF_ALLAPP, mCellCountX);
                        editor.putInt(SettingsValue.CELLY_COUNT_OF_ALLAPP, mCellCountY);
                        try {
                            editor.apply();
                        } catch (AbstractMethodError unused) {
                            editor.commit();
                        }
                    }
                }, "commitCellCount").start();
            } else {
                initCellXAndY();
            }
            /* RK_ID: RK_SETTINGS. AUT: liuli1 . DATE: 2012-04-18 . END */
            
            int numWidthGaps = mCellCountX - 1;
            int numHeightGaps = mCellCountY - 1;
            if(mphoneindex!=-1){
                mWidthGap = resources.getDimensionPixelSize(R.dimen.apps_customize_pageLayoutWidthGap);
                mHeightGap = resources.getDimensionPixelSize(R.dimen.apps_customize_pageLayoutHeightGap);
            }else{
	            mOriginalWidthGap = -1;
	            if (mOriginalWidthGap < 0 || mOriginalHeightGap < 0) {
	            	int hSpace = (int) (rect.width() - mPaddingLeft - mPaddingRight);
	                int vSpace = (int) (rect.height() - mPaddingTop - mPaddingBottom);
	                int hFreeSpace = hSpace - (mCellCountX * mOriginalCellWidth);
	                int vFreeSpace = vSpace - (mCellCountY * mOriginalCellHeight);
	                mWidthGap = Math.min(mMaxGap, numWidthGaps > 0 ? (hFreeSpace / numWidthGaps) : 0);
	                mHeightGap = Math.min(mMaxYGap,numHeightGaps > 0 ? (vFreeSpace / numHeightGaps) : 0);
	            } else {
	                mWidthGap = mOriginalWidthGap;
	                mHeightGap = mOriginalHeightGap;
	            }
            }
//			this.setClipRect(new RectF(rect.left - rect.width(), rect.top, rect.right + rect.width(), rect.bottom));
            Log.e("kt","mWidthGap  ===================="+mWidthGap);
			/*** fixbug 7065  AUT: zhaoxy . DATE: 2013-02-05 . START***/
			updatePageCount();
			/*** fixbug 7065  AUT: zhaoxy . DATE: 2013-02-05 . END***/
			updateLayoutAfterResize();
			// Log.d(TAG, "mRect = " + mRect.toString() + " mCellWidth = " +
			// mCellWidth + " mCellHeight = " + mCellHeight);
			calculateGlobalTouchRect();
			//setPageDrawAdapter(new SphereDrawAdapter());
			//setPageDrawAdapter(new CubeDrawAdapter());
		}
	}
	
	/*** RK_ID: DRAG. AUT: zhaoxy . DATE: 2013-02-19 . START ***/
    public void setup(XDragController mDragController) {
        this.mDragController = mDragController;
        this.mDragController.addDropTarget(this);
    }
    /*** RK_ID: DRAG. AUT: zhaoxy . DATE: 2013-02-19 . END ***/

	private void setChildVisible(int page, boolean visibility) {
	    int startIndex = page * mCellCountX * mCellCountY;
        int endIndex = Math.min(startIndex + mCellCountX * mCellCountY, getChildCount());
        this.setChildVisible(startIndex, endIndex, visibility);
	}
	
	private void setChildVisible(int startIndex, int endIndex, boolean visibility) {
//	    List<DrawableItem> items = getChildren();
//	    int size = items.size();
	    if (getChildCount() > 0) {
            if (startIndex > -1 && endIndex >= startIndex && endIndex < getChildCount()) {
                for (int i = startIndex; i < endIndex; i++) {
                    getChildAt(i).setVisibility(visibility);
                }
            }
        }
	}

	public void setApps(ArrayList<ApplicationInfo> infos) {
		if (infos == null)
			return;

		this.infos = infos;
		reloadData(AppContentTabHost.APPS_TAB_TAG);
		isDataReady = true;
	}

	public boolean isDataReady() {
	    return isDataReady;
	}
	
	public void reloadData(String tab) {
		mEditMode = false;
		reloadEditData(tab);
	}
	
	public void reloadEditData(String tab) {
		updatePageCount(tab);
		invalateData(tab);
	}
	
	private void updateLayoutAfterResize() {
	    final int countPrePage = mCellCountX * mCellCountY;
        final float screenWidth = getWidth();
        float left, top;
        for (int i = 0; i < getChildCount(); i++) {
            final XIconView iconView = (XIconView) getChildAt(i);
            if (iconView == null) {
                continue;
            }
            final int mCellX = i % mCellCountX;
            final int mCellY = (i % countPrePage) / mCellCountX;
            left = mPaddingLeft + (mCellWidth + mWidthGap) * mCellX - screenWidth;
            top = mPaddingTop + (mCellHeight + mHeightGap) * mCellY;
            iconView.setRelativeX(left);
            iconView.setRelativeY(top);
            Matrix m = iconView.getMatrix();
            m.reset();
            iconView.updateMatrix(m);
        }
	}
	
	private void clearAllNewBg(Context context, ApplicationInfo info, XIconView iconView){
        if (info.mNewAdd != 1)
        {
            return;
        }               
        
        ((XLauncher)mContext.getContext()).clearAndShowNewBg(info.componentName.flattenToString());                 
	}

	private synchronized void invalateData(String tab) {
	    final int countPrePage = mCellCountX * mCellCountY;
	    final float screenWidth = getWidth();
	    float left, top;
	    
	    clearAllItems();
	    
	    mCurrentTag = tab;
	    
	    List<ApplicationInfo> items = null;
		if (AppContentTabHost.APPTASK_TAB_TAG.equals(tab)){
			items = mAppRecentTasks;
		} else if (AppContentTabHost.APPS_TAB_TAG.equals(tab)) {
        	items = infos;
        } else if (AppContentTabHost.APPRUNNING_TAB_TAG.equals(tab)) {
        	items = mAppRunningTasks;
        }

        final int iconsize = SettingsValue.getIconSizeValueNew(mContext.getContext());
        int size = items != null ? items.size() : 0;
		for (int i = 0; i < size; i++) {
			final ApplicationInfo info = items.get(i);
			int mCellX = i % mCellCountX;
            int mCellY = (i % countPrePage) / mCellCountX;
			final int index = i;

			left = mPaddingLeft + (mCellWidth + mWidthGap) * mCellX - screenWidth;
			top = mPaddingTop + (mCellHeight + mHeightGap) * mCellY;
            RectF rect = new RectF(left, top, left + mCellWidth, top + mCellHeight);
			final XIconView iconView = new XIconView(info, rect, mContext);			
			
			iconView.setOnClickListener(new DrawableItem.OnClickListener() {
				@Override
				public void onClick(DrawableItem item) {
				    clearAllNewBg(mContext.getContext(), iconView.getLocalInfo(), iconView);

				    clearAppToPostionView();
				    if (mOffsetX != 0) {
				        if (!mAnimController.isOffsetXAnimStart()) {
				            resetAnim();
                        }
                        return;
                    }
					if (!mEditMode) {
						if (mContext.getContext() instanceof XLauncher) {
						    ((XLauncher)mContext.getContext()).getMainView().post(new Runnable() {
                                public void run() {
							((XLauncher)mContext.getContext()).startActivitySafely(info.intent, null);
//						    ((XLauncher)mContext.getContext()).getModel().getUsageStatsMonitor().add(info.intent);
						}
                            });
						}
					    return;
					}
					if (AppContentTabHost.APPRUNNING_TAB_TAG.equals(mCurrentTag)) {
						mActivityManager.killBackgroundProcesses(info.componentName.getPackageName());
//						removeItem(item);
						if (index < mAppRunningTasks.size()) {
						    mAppRunningTasks.remove(index);
						}
						updateRunningPageCountWithoutCalculate();
						invalateData(mCurrentTag);
//						arrangeChildren(mCurrentPage);
//						if(mContent.getChildrenLayout().getChildCount() == 0 && getChildCount() > 1){
//							removeView(mContent);
//						}
//                   /*** RK_ID: TASK_TAB.  AUT: zhaoxy . DATE: 2012-08-13 . START***/
//						updateKillBar(false);
						return;
					}
					if (info.flags != 0 && mEditMode) {
					    mContext.post(new Runnable() {
                            @Override
                            public void run() {
						Intent intent = new Intent(Intent.ACTION_DELETE);//Intent.ACTION_VIEW); 
					    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					    intent.setData(Uri.parse("package:" + info.componentName.getPackageName()));
					    (mContext.getContext()).startActivity(intent);
                            }
                        });
					}
				}				
			});
			
			iconView.setOnLongClickListener(new DrawableItem.OnLongClickListener() {				
				@Override
				public boolean onLongClick(DrawableItem item) {
				    clearAllNewBg(mContext.getContext(), iconView.getLocalInfo(), iconView);
				    clearAppToPostionView();
				    /*** RK_ID: DRAG. AUT: zhaoxy . DATE: 2013-02-19 . START ***/
				    if (mOffsetX != 0) {
				        mLongPressed = false;
				        resetAnim();
						return true;
					}
					if (!mEditMode) {
					mEditMode = true;
					startEditMode(!AppContentTabHost.APPRUNNING_TAB_TAG.equals(mCurrentTag));
				}
					final int[] mTempXY = new int[2];
					((XLauncher) mContext.getContext()).getDragLayer().getLocationInDragLayer(iconView, mTempXY);
                    final int dragLayerX = mTempXY[0];
                    final int dragLayerY = mTempXY[1];
			
					mDraggingMode = true;
					mDraggingItemOrigin = item;
					mDraggingItemOrigin.setVisibility(false);
					
					iconView.getIconDrawable().setAlpha(1);					
                    mDragController.startDrag(iconView.getSnapshot(1f), dragLayerX, dragLayerY, AppContentView.this, iconView.getTag(), 0, null, null);
                    iconView.getIconDrawable().setAlpha(.6f);
                    
                    /*** RK_ID: DRAG. AUT: zhaoxy . DATE: 2013-02-19 . END ***/
                    return true;
						}
					});

            /* RK_ID: RK_ALLAPPS. AUT: liuli1 . DATE: 2012-11-13 . START */
            iconView.setTag(info);
            Utilities.iconSizeChange(iconView.getIconDrawable(), iconsize);
            /* RK_ID: RK_ALLAPPS. AUT: liuli1 . DATE: 2012-11-13 . END */
            
            if (mEditMode) {
				iconView.startEditMode(!AppContentTabHost.APPRUNNING_TAB_TAG.equals(tab));
			} else {
				iconView.stopEditMode();
			}

			this.addItem(iconView);
//			iconView.enableCache();
		}
		
		invalidate();
	}

	private synchronized void updatePageCount(String tab) {
		ArrayList<ApplicationInfo> items = null;
		if (AppContentTabHost.APPTASK_TAB_TAG.equals(tab)){
//		    setLoop(false);
    		mAppRecentTasks.clear();
    		/*** RK_ID: TASK_TAB.  AUT: zhaoxy . DATE: 2012-08-15 . START***/
    		ArrayList<ApplicationInfo> appRecentList = getRecentTasks(mCellCountX * mCellCountY);
    		if (appRecentList != null && !appRecentList.isEmpty()) {
    		    for (ApplicationInfo info : appRecentList) {
    		        mAppRecentTasks.add(info);
    		    }
    		}
    		items = mAppRecentTasks;
        } else if (AppContentTabHost.APPS_TAB_TAG.equals(tab)) {
//            setLoop(true);
        	items = infos;
        } else if (AppContentTabHost.APPRUNNING_TAB_TAG.equals(tab)) {
//            setLoop(false);
        	ArrayList<RunningAppProcessInfo> appRunningList = (ArrayList<RunningAppProcessInfo>) mActivityManager.getRunningAppProcesses();
    		mAppRunningTasks.clear();
    		for(ApplicationInfo info : infos){
    			for(RunningAppProcessInfo runningInfo : appRunningList){
    				if(runningInfo.processName.equals(info.componentName.getPackageName())){
    				    /*** RK_ID: TASK_TAB.  AUT: zhaoxy . DATE: 2012-08-14 . START***/
    					if(filterRunningApp(info)){
    					/*** RK_ID: TASK_TAB.  AUT: zhaoxy . DATE: 2012-08-14 . END***/
    						mAppRunningTasks.add(info);
    						Log.i("zlq","packageName = " + info.componentName.getPackageName());
    					}
    				}
    			}
    		}
        	items = mAppRunningTasks;
        }
    		
        int size = items != null ? items.size() : 0;
		mPageCount = (int) Math.ceil((float) size
				/ (mCellCountX * mCellCountY));
		if (mPageCount >= 0) {
			if (mCurrentPage >= mPageCount) {
				mCurrentPage = mPageCount - 1;
			}
			if (mCurrentPage < 0) {
				mCurrentPage = 0;
			}

			// R2
			if (pageSwitcher != null) {
				pageSwitcher.onUpdatePage(mPageCount);
				pageSwitcher.onPageSwitching(mCurrentPage, mCurrentPage, 0);
			}
			// 2R
		}

        /* RK_ID: RK_SLIDEEFFECT. AUT: liuli1 . DATE: 2012-12-06 . START */
        if (normalAdapter != null) {
            normalAdapter.setup(mPageCount, isLoop);
        }
        /* RK_ID: RK_SLIDEEFFECT. AUT: liuli1 . DATE: 2012-12-06 . END */
	}
	
	/*** RK_ID: CELL_DENSITY  AUT: zhaoxy . DATE: 2012-12-10 . START***/
	public void appsCellYChanged() {
	    resize(localRect);
        updatePageCount();
        invalateData();
	}
	
    private void initCellXAndY() {
        String yValue = SettingsValue.getAppListCellY(mContext.getContext());
        int y = mDefaultCellCountY == Integer.MAX_VALUE ? mCellCountY : mDefaultCellCountY;
        if (yValue.equals("NORMAL")) {
            mCellCountY = y;
        } else if (yValue.equals("SPARSE")) {
            mCellCountY = y - 1;
        } else if (yValue.equals("DENSE")) {
            mCellCountY = y + 1;
        }
        /*** RK_ID: TASK_TAB.  AUT: zhaoxy . DATE: 2012-08-13 . START***/
        if (AppContentTabHost.APPRUNNING_TAB_TAG.equals(mCurrentTag)) {
            if (mCellCountY > 4) {
                --mCellCountY;
            }
        }
        /*** RK_ID: TASK_TAB.  AUT: zhaoxy . DATE: 2012-08-13 . END***/
        if (this.mPageDrawAdapter != null) {
            this.mPageDrawAdapter.reset();
        }
    }
    /*** RK_ID: CELL_DENSITY  AUT: zhaoxy . DATE: 2012-12-10 . END***/

	private void updateRunningPageCountWithoutCalculate() {
		ArrayList<ApplicationInfo> items = null;
		setLoop(false);    	
    	items = mAppRunningTasks;
    		
		mPageCount = (int) Math.ceil((float) items.size()
				/ (mCellCountX * mCellCountY));
		if (mPageCount > 0) {
			if (mCurrentPage >= mPageCount) {
				mCurrentPage = mPageCount - 1;
			}
			if (mCurrentPage < 0) {
				mCurrentPage = 0;
			}

			// R2
			if (pageSwitcher != null) {
				pageSwitcher.onUpdatePage(mPageCount);
				pageSwitcher.onPageSwitching(mCurrentPage, mCurrentPage, 0);
			}
			// 2R
		}

        /* RK_ID: RK_SLIDEEFFECT. AUT: liuli1 . DATE: 2012-12-06 . START */
        if (normalAdapter != null) {
            normalAdapter.setup(mPageCount, isLoop);
        }
        /* RK_ID: RK_SLIDEEFFECT. AUT: liuli1 . DATE: 2012-12-06 . END */
	}
	
	private boolean filterRunningApp(ApplicationInfo info) {
        if (info == null || info.flags == 0) {
            return false;
        }
        for (int i = 0; i < EXCEPTION_RUNNING_LIST.length; i++) {
            if (EXCEPTION_RUNNING_LIST[i].equals(info.componentName.getPackageName())) {
                return false;
            }
        }
        return true;
    }
	
	private ArrayList<ApplicationInfo> getRecentTasks(int max) {
        if (max <= 0) {
            return null;
        }
//        if (mContext.getContext() instanceof XLauncher 
//        		&& ((XLauncher)mContext.getContext()).getModel().getUsageStatsMonitor() != null) {
//        	((XLauncher)mContext.getContext()).getModel().getUsageStatsMonitor().updateCatch();
//        } 
        ArrayList<ApplicationInfo> temp = (ArrayList<ApplicationInfo>) infos.clone();
        /*** RK_ID: TASK_TAB.  AUT: zhaoxy . DATE: 2012-10-25 . START***/
        UsageStatsMonitor.getRecentTasks(temp, max, PreferenceManager.getDefaultSharedPreferences(mContext.getContext()).getLong(UsageStatsMonitor.KEY_LAST_BOOT_TIME, 0));
        /*** RK_ID: TASK_TAB.  AUT: zhaoxy . DATE: 2012-10-25 . END***/
        return temp;
    }

	private void invalateData() {
	    invalateData(mCurrentTag);
	}

	private void updatePageCount() {
		updatePageCount(mCurrentTag);
	}

	/* RK_ID: RK_ALLAPPS. AUT: liuli1 . DATE: 2012-11-13 . START */
	public void updateForTheme(List<ApplicationInfo> apps) {
//        List<DrawableItem> childrens = getChildren();

        if (getChildCount() > 0) {
//            int size = childrens.size();
            for (int i = 0; i < getChildCount(); i++) {
                DrawableItem item = getChildAt(i);

                if (item instanceof XIconView) {
                    XIconView iconView = (XIconView) item;
                    ApplicationInfo oldApp = iconView.getTag();

                    ApplicationInfo newApp = findApplicationInfo(apps, oldApp.componentName);
                    if (newApp != null) {

                        infos.remove(iconView.getTag());
                        infos.add(newApp);
                    }
                }
            }
        }

        clearAllItems();
        invalateData();
    }

    private ApplicationInfo findApplicationInfo(List<ApplicationInfo> apps, ComponentName name) {
        if (apps == null || name == null) {
            return null;
        }

        int appSize = apps.size();
        for (int index = 0; index < appSize; index++) {
            ApplicationInfo info = apps.get(index);

            if (info.componentName.equals(name)) {
                return info;
            }
        }

        return null;
    }

    public void updateForText() {
//        List<DrawableItem> childrens = getChildren();

        if (getChildCount() > 0) {
//            int size = childrens.size();
            for (int i = 0; i < getChildCount(); i++) {
                DrawableItem item = getChildAt(i);

                if (item instanceof XIconView) {
                    XIconView iconView = (XIconView) item;
                    iconView.updateIconText();
                }
            }
        }

        invalidate();
    }
    /* RK_ID: RK_ALLAPPS. AUT: liuli1 . DATE: 2012-11-13 . END */

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY, float previousX, float previousY) {
	    clearAppToPostionView();
	    
		if ((mEditMode && mDraggingMode) || (mLongPressed && mEditMode)) {
		    /*** RK_ID: DRAG. AUT: zhaoxy . DATE: 2013-02-19 . START ***/
			//super.onScroll(e1, e2, distanceX, distanceY);
			if(D2.DEBUG)
				D2.echo("Returned HERE !");
			return true;
			/*** RK_ID: DRAG. AUT: zhaoxy . DATE: 2013-02-19 . END ***/
		} else if (!mLongPressed) {
            mOffsetXTarget -= distanceX / getWidth();
            /*** RK_ID: RK_SLIDEEFFECT AUT: zhaoxy . DATE: 2012-12-10 . START ***/
            mOffsetYTarget += distanceY / getWidth();
            if (Math.abs(mOffsetYTarget) > 1) {
                mOffsetYTarget = Math.signum(mOffsetYTarget);
            }
            rect2ballTarget = 1.0f;
            /*** RK_ID: RK_SLIDEEFFECT AUT: zhaoxy . DATE: 2012-12-10 . END ***/
            mAnimController.startTouchAnim();

            if (!isPageMoving) {
                isPageMoving = true;
                onPageBeginMoving();
            }

            return true;
		}
	    
		return true;
	}
	
	@Override
	public void draw(IDisplayProcess canvas) {
	    
	    updateFinalAlpha();

        if (mPageDrawAdapter == null) {
            if (normalAdapter == null)
                normalAdapter = new NormalDrawAdapter(getMatrix(), localRect, mPageCount, isLoop);
            setPageDrawAdapter(normalAdapter);
        }

	    if (mPageDrawAdapter != null) {
	    	mPageDrawAdapter.drawPage(canvas, mCurrentPage, mOffsetX, mOffsetY);
		}
	}
	
	public DrawableItem getDraggingItem(boolean origin){
		return origin ? mDraggingItemOrigin : mDraggingItemShadow;
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		
        
        //D2
//kangwei3
//        setExtraTouchBounds(new RectF(0, 0, 2000, 2000));
//        desireTouchEvent(true);
		
	    /*** RK_ID: RK_SLIDEEFFECT  AUT: zhaoxy . DATE: 2012-12-10 . START***/
	    mAnimController.stopOffsetYAnim();
		mAnimController.stopOffsetXAnim();
		/*** RK_ID: RK_SLIDEEFFECT  AUT: zhaoxy . DATE: 2012-12-10 . END***/
		mAnimController.startTouchAnim();
		return super.onDown(e);
	}
		
	@Override
	public boolean onFingerUp(MotionEvent e) {
        
        //D2
//kangwei3
//        resetTouchBounds();
        desireTouchEvent(false);
        
	    /*** RK_ID: RK_SLIDEEFFECT  AUT: zhaoxy . DATE: 2012-12-10 . START***/
	    if (isOnFling) {
	        isOnFling = false;
	        mOffsetY = mOffsetYTarget = 0;
	        mAnimController.stopTouchAnim();
	        rect2ballTarget = 0.0f;
        } else {
            if (mOffsetX < -.5f) {
                currOrientation = ORI_LEFT;
            } else if (mOffsetX > .5f) {
                currOrientation = ORI_RIGHT;
            } else {
                currOrientation = ORI_NONE;
            }
            switch (currOrientation) {
            case ORI_RIGHT:
                // R2.echo("ORI_RIGHT velocityX" + velocityX);
                // isPageMoving = true;
                if (mCurrentPage - 1 > -1) {
                    --mCurrentPage;
                    mOffsetX -= 1f;

                    // R2
                    if (pageSwitcher != null) {
                        pageSwitcher.onPageSwitching(mCurrentPage + 1, mCurrentPage, mOffsetX);
                    }
                    // 2R

                } else if (isLoop && mPageCount > 1) {
                    mCurrentPage = mPageCount - 1;
                    mOffsetX -= 1f;
                    // R2
                    if (pageSwitcher != null) {
                        pageSwitcher.onPageSwitching(0, mCurrentPage, mOffsetX);
                    }
                    // 2R
                }
                mAnimController.stopTouchAnim();
                /*** RK_ID: RK_SLIDEEFFECT AUT: zhaoxy . DATE: 2012-12-10 . START ***/
                mAnimController.startOffsetXAnim(OffsetXAnimDuration + mDurationExtra, false);
                mAnimController.startOffsetYAnim(OffsetYAnimDuration);
                /*** RK_ID: RK_SLIDEEFFECT AUT: zhaoxy . DATE: 2012-12-10 . END ***/
                if (!isPageMoving) {
                    isPageMoving = true;
                    onPageBeginMoving();
                }
                break;
            case ORI_LEFT:
                // R2.echo("ORI_LEFT velocityX" + velocityX);
                // isPageMoving = true;
                if (mCurrentPage + 1 < mPageCount) {
                    ++mCurrentPage;
                    mOffsetX += 1f;

                    // R2
                    if (pageSwitcher != null) {
                        pageSwitcher.onPageSwitching(mCurrentPage - 1, mCurrentPage, mOffsetX);
                    }
                    // 2R
                } else if (isLoop && mPageCount > 1) {
                    // R2
                    if (pageSwitcher != null) {
                        pageSwitcher.onPageSwitching(mCurrentPage, 0, mOffsetX);
                    }
                    // 2R
                    mCurrentPage = 0;
                    mOffsetX += 1f;

                }
                mAnimController.stopTouchAnim();
                mAnimController.startOffsetXAnim(OffsetXAnimDuration + mDurationExtra, false);
                mAnimController.startOffsetYAnim(OffsetYAnimDuration);
                if (!isPageMoving) {
                    isPageMoving = true;
                    onPageBeginMoving();
                }
                break;
            case ORI_NONE:
                if (!mAnimController.isOffsetXAnimStart()) {
                    mAnimController.startOffsetXAnim(OffsetXAnimDuration + mDurationExtra, false);
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
            invalidate();
        }
	    /*** RK_ID: RK_SLIDEEFFECT  AUT: zhaoxy . DATE: 2012-12-10 . END***/
	    
	    R2.echo("AppContentView isPageMoving false");
	    
	    //R2
	    mDraggingMode = false;
	    mLongPressed = false;
	    //2R
	    
	    return super.onFingerUp(e);
	}
    
    @Override
    public void onTouchCancel( MotionEvent e ) {
        //D2
//kangwei3
//        resetTouchBounds();
        desireTouchEvent(false);

        mLongPressed = false;
        resetAnim();

        super.onTouchCancel( e );
    }

    public void resetAnim() {
        resetAnim(false);
    }

    public void resetAnim(boolean now) {
        mAnimController.stopTouchAnim();
        if (now) {
            mAnimController.stopOffsetYAnim();
            mAnimController.stopOffsetXAnim();
            mOffsetX = mOffsetXTarget = 0;
            mOffsetY = mOffsetYTarget = 0;
        } else {
            /*** RK_ID: RK_SLIDEEFFECT  AUT: zhaoxy . DATE: 2012-12-10 . START***/
            if (!mAnimController.isOffsetXAnimStart()) {
                mAnimController.startOffsetXAnim(OffsetXAnimDuration + mDurationExtra, false);
            }
            if (!mAnimController.isOffsetYAnimStart()) {
                mAnimController.startOffsetYAnim(OffsetYAnimDuration);
            }
            if (!isPageMoving) {
                isPageMoving = true;
                onPageBeginMoving();
            }
            /*** RK_ID: RK_SLIDEEFFECT  AUT: zhaoxy . DATE: 2012-12-10 . END***/
        }
    }
    
    public boolean isLoop() {
        return isLoop;
    }

    public void setLoop(boolean isLoop) {
        this.isLoop = isLoop;

        /* RK_ID: RK_SLIDEEFFECT. AUT: liuli1 . DATE: 2012-12-06 . START */
        if (normalAdapter != null) {
            normalAdapter.setup(mPageCount, isLoop);
        }
        /* RK_ID: RK_SLIDEEFFECT. AUT: liuli1 . DATE: 2012-12-06 . END */
    }
    
    private boolean isOnFling = false;
	   
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        switch (currOrientation) {
            case ORI_RIGHT:
//                R2.echo("ORI_RIGHT velocityX" + velocityX);
//                isPageMoving = true;
                if (mCurrentPage - 1 > -1) {
                    --mCurrentPage;
                    mOffsetX -= 1f;
                    isOnFling = true;
                    
                    // R2
                    if (pageSwitcher != null) {
                        pageSwitcher.onPageSwitching(mCurrentPage + 1, mCurrentPage, mOffsetX);
                    }
                    // 2R
                    
                } else if (isLoop && mPageCount > 1) {
                    mCurrentPage = mPageCount - 1;
                    mOffsetX -= 1f;
                    isOnFling = true;
                    // R2
                    if (pageSwitcher != null) {
                        pageSwitcher.onPageSwitching(0, mCurrentPage, mOffsetX);
                    }
                    // 2R
                }
                mAnimController.stopTouchAnim();
                /*** RK_ID: RK_SLIDEEFFECT  AUT: zhaoxy . DATE: 2012-12-10 . START***/
                mAnimController.startOffsetXAnim(OffsetXAnimDuration + mDurationExtra, false);
                mAnimController.startOffsetYAnim(OffsetYAnimDuration);
                /*** RK_ID: RK_SLIDEEFFECT  AUT: zhaoxy . DATE: 2012-12-10 . END***/
                if (!isPageMoving) {
                    isPageMoving = true;
                    onPageBeginMoving();
                }
                break;
            case ORI_LEFT:
//                R2.echo("ORI_LEFT velocityX" + velocityX);
//                isPageMoving = true;
                if (mCurrentPage + 1 < mPageCount) {
                    ++mCurrentPage;
                    mOffsetX += 1f;
                    isOnFling = true;
                    
                    // R2
                    if (pageSwitcher != null) {
                        pageSwitcher.onPageSwitching(mCurrentPage - 1, mCurrentPage, mOffsetX);
                    }
                    // 2R
                } else if (isLoop && mPageCount > 1) {
                    // R2
                    if (pageSwitcher != null) {
                        pageSwitcher.onPageSwitching(mCurrentPage, 0, mOffsetX);
                    }
                    // 2R
                    mCurrentPage = 0;
                    mOffsetX += 1f;
                    isOnFling = true;
                    
                }
                mAnimController.stopTouchAnim();
                mAnimController.startOffsetXAnim(OffsetXAnimDuration + mDurationExtra, false);
                mAnimController.startOffsetYAnim(OffsetYAnimDuration);
                if (!isPageMoving) {
                    isPageMoving = true;
                    onPageBeginMoving();
                }
                break;
            case ORI_NONE:
                break;
        }
        invalidate();
        return false;
    }

    /* RK_ID: RK_SLIDEEFFECT. AUT: liuli1 . DATE: 2012-12-11 . START */
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
            LauncherPersonalSettings.SLIDEEFFECT_BOUNCE, LauncherPersonalSettings.SLIDEEFFECT_SCALE,
            LauncherPersonalSettings.SLIDEEFFECT_SWEEP, LauncherPersonalSettings.SLIDEEFFECT_WORM};

    private void randomSlideValue() {

        String s = SettingsValue.getAppListSlideValue(mContext.getContext());

        if (s.equals(LauncherPersonalSettings.SLIDEEFFECT_RANDOM)) {
            int randomN = mRandom.nextInt(SLIDEEFFECT_ARRAY.length);
            s = SLIDEEFFECT_ARRAY[randomN];
            if (!s.equals(this.sPrefAppListSlide)) {
                initPageDrawAdapter(s);
            }
        }
    }

    private void initPageDrawAdapter(String value) {
        this.sPrefAppListSlide = value;
        if (LauncherPersonalSettings.SLIDEEFFECT_SPHERE.equals(this.sPrefAppListSlide)) {
            if (sphereAdapter == null) {
                sphereAdapter = new SphereDrawAdapter(true);
            }
            sphereAdapter.resetSphereOrCylinder(true);
            setPageDrawAdapter(sphereAdapter);
            if (mAnimController != null) {
                mAnimController.setSphereSlide(true);
            }
            mDurationExtra = 0;
        } else if (LauncherPersonalSettings.SLIDEEFFECT_CYLINDER.equals(this.sPrefAppListSlide)) {
            if (sphereAdapter == null) {
                sphereAdapter = new SphereDrawAdapter(false);
            }
            sphereAdapter.resetSphereOrCylinder(false);
            setPageDrawAdapter(sphereAdapter);
            if (mAnimController != null) {
                mAnimController.setSphereSlide(true);
            }
            mDurationExtra = 0;
        } else if (LauncherPersonalSettings.SLIDEEFFECT_SWEEP.equals(this.sPrefAppListSlide)) {
            if (sweepAdapter == null) {
                sweepAdapter = new SweepDrawAdapter();
            }
            setPageDrawAdapter(sweepAdapter);
            for (int i = 0; i < getChildCount(); i++) {
                final XIconView item = (XIconView) getChildAt(i);
                item.setAlpha(1f);
                item.setTouchable(false);
            }
            mDurationExtra = 500;
        } else if (LauncherPersonalSettings.SLIDEEFFECT_CHARIOT.equals(this.sPrefAppListSlide)
                || LauncherPersonalSettings.SLIDEEFFECT_WAVE_2.equals(this.sPrefAppListSlide)
                || LauncherPersonalSettings.SLIDEEFFECT_WILD.equals(this.sPrefAppListSlide)
                || LauncherPersonalSettings.SLIDEEFFECT_BULLDOZE.equals(this.sPrefAppListSlide)
                || LauncherPersonalSettings.SLIDEEFFECT_WAVE.equals(this.sPrefAppListSlide)
                || LauncherPersonalSettings.SLIDEEFFECT_ROTATE.equals(this.sPrefAppListSlide)
                || LauncherPersonalSettings.SLIDEEFFECT_BOUNCE.equals(this.sPrefAppListSlide)
                || LauncherPersonalSettings.SLIDEEFFECT_SCALE.equals(this.sPrefAppListSlide)
                || LauncherPersonalSettings.SLIDEEFFECT_NORMAL.equals(this.sPrefAppListSlide)
                || LauncherPersonalSettings.SLIDEEFFECT_SNAKE.equals(this.sPrefAppListSlide)
                || LauncherPersonalSettings.SLIDEEFFECT_CUBE.equals(this.sPrefAppListSlide)
                || LauncherPersonalSettings.SLIDEEFFECT_WORM.equals(this.sPrefAppListSlide)) {
            if (normalAdapter == null)
                normalAdapter = new NormalDrawAdapter(getMatrix(), localRect, mPageCount, isLoop);
            setPageDrawAdapter(normalAdapter);
            for (int i = 0; i < getChildCount(); i++) {
                final XIconView item = (XIconView) getChildAt(i);
                item.setAlpha(1f);
                item.setTouchable(false);
            }
            if (LauncherPersonalSettings.SLIDEEFFECT_WORM.equals(this.sPrefAppListSlide)) {
                mDurationExtra = 500;
            } else {
                mDurationExtra = 0;
            }
        }
    }

    /* RK_ID: RK_SLIDEEFFECT. AUT: liuli1 . DATE: 2012-12-11 . END */
    
    public void updateSlideValue() {
        final String s = SettingsValue.getAppListSlideValue(mContext.getContext());
//        if (!s.equals(this.sPrefAppListSlide)) {
            initPageDrawAdapter(s);
//        }
    }
    
	/**
	 * Sphere Page Animation.
	 *
	 */
	private class SphereDrawAdapter implements PageDrawAdapter {

	    private boolean isSphereOrCylinder = true;
	    /**
	     * 前后页切换alpha临界点
	     */
	    private static final float PageAlphaCritical1 = 0.15f;
	    /**
	     * 球面变平面后半屏alpha临界点
	     */
	    private static final float PageAlphaCritical2 = 0.6f;
	    public static final float PI = (float) Math.PI;
	    private Camera mCamera = new Camera();
	    
	    /**
	     * 经线
	     */
	    private int lon_count = mCellCountX << 1;
	    /**
	     * 纬线
	     */
	    private int lat_count = mCellCountY;
	    
	    float angle_offset_h = 0;
	    float angle_offset_v = 0;
	    
	    private float center_x = 250f;
	    private float center_y = 250f;

	    private float ANGLE_LAT_PADDING = 22.5f;
	    private float ANGLE_LON_PADDING = 22.5f;
	    /**
	     * 两根经线间的夹角
	     */
	    float angle_pre_lon = 360.0f / lon_count;
	    /**
	     * 两根纬线间的夹角
	     */
	    float angle_pre_lat = (180 - ANGLE_LAT_PADDING * 2) / (lat_count - 1);

		int countPrePage;
		float radius;
		float pageWidth, pageHeight;
		
		public SphereDrawAdapter(boolean isSphereOrCylinder) {
		    this.isSphereOrCylinder = isSphereOrCylinder;
		    reset();
		}

        public void resetSphereOrCylinder(boolean isSphereOrCylinder) {
            this.isSphereOrCylinder = isSphereOrCylinder;
            reset();
        }

        @Override
        public void reset() {
			countPrePage = mCellCountX * mCellCountY;
			radius = getWidth() * 0.5f;
            lon_count = mCellCountX << 1;
            lat_count = mCellCountY;
            angle_pre_lon = 360.0f / lon_count;
            ANGLE_LON_PADDING = angle_pre_lon / 2;
            ANGLE_LAT_PADDING = 45f;
            angle_pre_lat = (180 - ANGLE_LAT_PADDING * 2) / (lat_count - 1);
            center_x = -radius;
            center_y = getHeight() * 0.5f;
		}

		@Override
		public void drawPage(IDisplayProcess canvas, int page, float offsetX, float offsetY) {
			if (page > -1 && page < mPageCount) {
				canvas.save();
				final Matrix matrix = AppContentView.this.getMatrix();
				if (matrix != null && !matrix.isIdentity()) {
	            	canvas.concat(matrix);
                }
//kangwei3
//	            canvas.clipRect(localRect);
	            canvas.translate(localRect.left, localRect.top);
	            pageWidth = getWidth();
	            pageHeight = getHeight();
	            radius = pageWidth * 0.618033989f;
	            center_x = pageWidth * .5f;
	            center_y = getHeight() * 0.5f;
	            final float targetX = pageWidth / 2 - mCellWidth / 2.0f;
	            final float targetY = pageHeight / 2 - mCellHeight / 2.0f;
	            float d = Math.round(pageWidth * (offsetX + 1));
	            
	            angle_offset_h = offsetX * 180;
	            angle_offset_v = offsetY * 90;
	            
	            float angle_to_z, angle_to_xz;
	            
	            int startIndex = page * countPrePage;
	            int endIndex = Math.min(startIndex + countPrePage, getChildCount());
	            for (int i = startIndex; i < endIndex; i++) {
	                XIconView item = (XIconView) getChildAt(i);
	                if (item == null) continue;
	                item.setTouchable(true);
	                Matrix m = item.getMatrix();
	                m.reset();
	                
	                angle_to_z = ANGLE_LON_PADDING + (float)((i % mCellCountX) * angle_pre_lon) - 90f + angle_offset_h;
	                angle_to_xz = 90f - ANGLE_LAT_PADDING -(float)(i % countPrePage / mCellCountX) * angle_pre_lat;
	                getMatrix(angle_to_z, angle_to_xz, m, rect2ball);

                    if (Math.abs(angle_to_z) > 90 && Math.abs(angle_to_z) < 270) {
                        float alpha = Math.abs((180 - Math.abs(angle_to_z)) * .8f / 90);
	                	if (alpha < .1f) {
	                		alpha = .1f;
						}
	                	if (Math.abs(offsetX) < PageAlphaCritical1) {
	                		alpha *= Math.abs(offsetX) / PageAlphaCritical1;
						}
	                	item.setAlpha(alpha);
	                } else {
	                	item.setAlpha(1f);
					}
	                
                    if (isSphereOrCylinder) {
                        m.preTranslate(d + (targetX - item.getRelativeX() - d) * rect2ball, (targetY - item.getRelativeY()) * rect2ball);
                    } else {
                        m.preTranslate(d + (targetX - item.getRelativeX() - d) * rect2ball, 0);
                    }
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
	                    XIconView item = (XIconView) getChildAt(i);
	                    if (item == null) continue;
	                    item.setTouchable(false);
	                    Matrix m = item.getMatrix();
		                m.reset();
		                
		                angle_to_z = ANGLE_LON_PADDING + (mCellCountX + i % mCellCountX) * angle_pre_lon - 90f + angle_offset_h;
	                    angle_to_xz = 90 - ANGLE_LAT_PADDING - (i % countPrePage / mCellCountX) * angle_pre_lat;
		                getMatrix(angle_to_z, angle_to_xz, m, rect2ball);

		                if (Math.abs(angle_to_z) > 90 && Math.abs(angle_to_z) < 270) {
		                	float alpha = Math.abs((180 - Math.abs(angle_to_z)) * .8f / 90);
		                	if (alpha < .1f) {
		                		alpha = .1f;
							}
		                	if (Math.abs(offsetX) < PageAlphaCritical1) {
		                		alpha *= Math.abs(offsetX) / PageAlphaCritical1;
							}
		                	if (rect2ball > PageAlphaCritical2) {
		                	    alpha *= (rect2ball - PageAlphaCritical2) / (1 - PageAlphaCritical2);
                            } else {
                                alpha = 0;
							}
		                	item.setAlpha(alpha);
		                } else {
		                    float alpha = 1;
		                    if (rect2ball > PageAlphaCritical2) {
                                alpha *= (rect2ball - PageAlphaCritical2) / (1 - PageAlphaCritical2);
                            } else {
                                alpha = 0;
                            }
		                	item.setAlpha(alpha);
						}
		                
		                if (isSphereOrCylinder) {
		                    m.preTranslate(d + (targetX - item.getRelativeX() - d) * rect2ball, (targetY - item.getRelativeY()) * rect2ball);
		                } else {
		                    m.preTranslate(d + (targetX - item.getRelativeX() - d) * rect2ball, 0);
		                }
		                m.postScale(0.5f * rect2ball + 0.5f, 0.5f * rect2ball + 0.5f, center_x, center_y);
		                item.updateMatrix(m);
		                item.draw(canvas);
	                }
	            }
	            canvas.restore();
			}
		}
		
        public Matrix getMatrix(float angle_to_z, float angle_to_xz, Matrix result, float input) {

			if (result == null) {
                result = new Matrix();
			}
            mCamera.save();
            mCamera.translate(0.0f, 0.0f, radius);
            if (isSphereOrCylinder) mCamera.rotateX(angle_offset_v * input);
            mCamera.rotateY(angle_to_z * input);
            if (isSphereOrCylinder) mCamera.rotateX(angle_to_xz * input);
            mCamera.translate(0.0f, 0.0f, -radius);
            mCamera.getMatrix(result);
            result.preTranslate(-center_x, -center_y);
            result.postTranslate(center_x, center_y);
            mCamera.restore();
			return result;
		}
		
	}

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
                    XIconView item = (XIconView) getChildAt(i);
                    if (item == null) continue;
                    item.setTouchable(true);
                    Matrix m = item.getMatrix();
                    m.reset();
                    if (mOffsetX < 0) {
                        dx = length(item.localRect.right, pageHeight - item.localRect.bottom);
                    } else {
                        dx = length(pageWidth + item.localRect.left, item.localRect.top);
                    }
                    float ts = dx / ds * (1f - animDuration);
                    float input = 0f;
                    if (Math.abs(mOffsetX) > ts) {
                        if (Math.abs(mOffsetX) - ts <= animDuration) {
                            input = (Math.abs(mOffsetX) - ts) / animDuration;
                        } else {
                            input = 1f;
                        }
                    } else {
                        input = 0f;
                    }
                    mCamera.save();
                    mCamera.rotateY(-180 * input);
                    final float si = FloatMath.sin(PI * input);
                    mCamera.rotateZ(90 * si * Math.signum(mOffsetX));
                    mCamera.getMatrix(m); 
                    mCamera.restore();
                    float xadjust = rotatePointOffsetX * si;
                    float yadjust = rotatePointOffsetY * si;
                    m.preTranslate(-item.localRect.centerX() + xadjust, -item.localRect.centerY() + yadjust);
                    m.postTranslate(pageWidth + item.localRect.centerX() - xadjust, item.localRect.centerY() - yadjust);
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
                        XIconView item = (XIconView) getChildAt(i);
                        if (item == null) continue;
                        item.setTouchable(false);
                        Matrix m = item.getMatrix();
                        m.reset();
                        if (mOffsetX < 0) {
                            dx = length(item.localRect.right, pageHeight - item.localRect.bottom);
                        } else {
                            dx = length(pageWidth + item.localRect.left, item.localRect.top);
                        }
                        float ts = dx / ds * (1f - animDuration);
                        float input = 0f;
                        if (Math.abs(mOffsetX) > ts) {
                            if (Math.abs(mOffsetX) - ts <= animDuration) {
                                    input = (Math.abs(mOffsetX) - ts) / animDuration;
                            } else {
                                input = 1f;
                            }
                        } else {
                            input = 0f;
                        }
                        mCamera.save();
                        mCamera.rotateY(-180 * (1 - input));
                        final float si = FloatMath.sin(PI * input);
                        mCamera.rotateZ(-90 * si * Math.signum(mOffsetX));
                        mCamera.getMatrix(m);
                        mCamera.restore();
                        float xadjust = rotatePointOffsetX * si;
                        float yadjust = rotatePointOffsetY * si;
                        m.preTranslate(-item.localRect.centerX() + xadjust, -item.localRect.centerY() + yadjust);
                        m.postTranslate(pageWidth + item.localRect.centerX() - xadjust, item.localRect.centerY() - yadjust);
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
    /* RK_ID: RK_SLIDEEFFECT. AUT: liuli1 . DATE: 2012-12-06 . START */
    private SphereDrawAdapter sphereAdapter;
    private NormalDrawAdapter normalAdapter;
    private SweepDrawAdapter sweepAdapter;

    private class NormalDrawAdapter extends FlatDrawAdapter {
        final float MY_PI = 3.141592653589793f;
        final float MIN_SLIDE = 0.001f;
        private static final float degreeY = 80.0f;
        final Camera mCamera = new Camera();

        public NormalDrawAdapter(Matrix m, RectF rect, int count, boolean loop) {
            super(m, rect, count, loop);
        }

        @Override
        public void itemSlide(IDisplayProcess canvas, int page, float offsetX, boolean currPage) {
            if (LauncherPersonalSettings.SLIDEEFFECT_CHARIOT.equals(sPrefAppListSlide)) {
                itemChariotSlide(canvas, page, offsetX, currPage);
            } else if (LauncherPersonalSettings.SLIDEEFFECT_WAVE_2.equals(sPrefAppListSlide)) {
                itemWave2Slide(canvas, page, offsetX, currPage);
            } else if (LauncherPersonalSettings.SLIDEEFFECT_WILD.equals(sPrefAppListSlide)) {
                itemWildSlide(canvas, page, offsetX, currPage);
            } else if (LauncherPersonalSettings.SLIDEEFFECT_BULLDOZE.equals(sPrefAppListSlide)) {
                itemBullDozeSlide(canvas, page, offsetX, currPage);
            } else if (LauncherPersonalSettings.SLIDEEFFECT_WAVE.equals(sPrefAppListSlide)) {
                itemWaveSlide(canvas, page, offsetX, currPage);
            } else if (LauncherPersonalSettings.SLIDEEFFECT_ROTATE.equals(sPrefAppListSlide)) {
                itemZRotateSlide(canvas, page, offsetX, currPage);
            } else if (LauncherPersonalSettings.SLIDEEFFECT_BOUNCE.equals(sPrefAppListSlide)) {
                itemBounceSlide(canvas, page, offsetX, currPage);
            } else if (LauncherPersonalSettings.SLIDEEFFECT_SCALE.equals(sPrefAppListSlide)) {
                itemScaleSlide(canvas, page, offsetX, currPage);
            } else if (LauncherPersonalSettings.SLIDEEFFECT_CUBE.equals(sPrefAppListSlide)) {
                itemCubeSlide(canvas, page, offsetX, currPage);
            } else if (LauncherPersonalSettings.SLIDEEFFECT_SNAKE.equals(sPrefAppListSlide)) {
                itemSnakeSlide(canvas, page, offsetX, currPage);
            } else if (LauncherPersonalSettings.SLIDEEFFECT_WORM.equals(sPrefAppListSlide)) {
                itemWormSlide(canvas, page, offsetX, currPage);
            } else {
                itemNormalSlide(canvas, page, offsetX, currPage);
            }
        }

        private void itemChariotSlide(IDisplayProcess canvas, int page, float offsetX, boolean currPage) {
            float ratio = Math.abs(offsetX) * 2;
            if (ratio > 1.0f) {
                ratio = 1.0f;
            }
            boolean noNeedEffect = false;
            if (ratio < MIN_SLIDE) {
                noNeedEffect = true;
            }

                final float pageWidth = getWidth();
                float d = Math.round(pageWidth * (offsetX + 1));

            final float R = pageWidth * (0.37f);
            final float centerX = -pageWidth / 2;
            final float centerY = getHeight() / 2;

            final int startIndex = page * mCellCountX * mCellCountY;
            final int endIndex = Math.min(startIndex + mCellCountX * mCellCountY, getChildCount());
            if (startIndex == endIndex) {
                return;
            }
            final float sectorDegree = 360.0f / (endIndex - startIndex);
            final float rotateCycle = 360 * (Math.abs(offsetX) - 0.5f) * (Math.signum(offsetX));

            for (int i = startIndex; i < endIndex; i++) {
                DrawableItem item = getChildAt(i);
                if (item == null) continue;
                item.setTouchable(currPage);
                Matrix m = item.getMatrix();
                m.reset();

                if (noNeedEffect) {
                    m.setTranslate(d, 0);
                } else {
                float degTarget = (i - startIndex) * sectorDegree;
                float radian = -(90 + degTarget) * ratio;
                m.setRotate(radian, item.localRect.centerX(), item.localRect.centerY());

                float angle = degTarget * MY_PI / 180f;
                float itemX = item.localRect.centerX();
                float itemY = item.localRect.centerY();
                float targetX = (float) ((R * FloatMath.cos(angle) + centerX - itemX) * ratio);
                float targetY = (float) ((centerY - R * FloatMath.sin(angle) - itemY) * ratio);
                m.postTranslate(targetX, targetY);

                if (Math.abs(offsetX) > 0.5f) {
                    m.postRotate(rotateCycle, centerX, centerY);
                }
                m.postTranslate(d, 0);
                }

                item.updateMatrix(m);
                item.draw(canvas);
            }
        }

        public void itemWave2Slide(IDisplayProcess canvas, int page, float offsetX, boolean currPage) {
            final float pageWidth = getWidth();
            final float d = Math.round(pageWidth * (offsetX + 1));
            final float pageHeight = getHeight();

            final int startIndex = page * mCellCountX * mCellCountY;
            final int endIndex = Math.min(startIndex + mCellCountX * mCellCountY, getChildCount());

            final float wave2Padding = 60;
            final float py = pageHeight - wave2Padding;
            float scaleY = 1 - Math.abs(offsetX) / 2;

            for (int i = startIndex; i < endIndex; i++) {
                DrawableItem item = getChildAt(i);
                if (item == null) continue;
                item.setTouchable(currPage);
//                item.setVisibility(true);
                Matrix m = item.getMatrix();
                m.reset();

                m.setScale(1, scaleY, 0, py);
                m.postTranslate(d, 0);

                item.updateMatrix(m);
                item.draw(canvas);
            }
        }

        public void itemWildSlide(IDisplayProcess canvas, int page, float offsetX, boolean currPage) {
            final float pageWidth = getWidth();
            final float d = Math.round(pageWidth * (offsetX + 1));

            final int startIndex = page * mCellCountX * mCellCountY;
            final int endIndex = Math.min(startIndex + mCellCountX * mCellCountY, getChildCount());

            float px = (-offsetX / 2) * pageWidth;
            float degrees = -offsetX * 30;
            float dy = -offsetX * pageWidth / 3.8f;

            for (int i = startIndex; i < endIndex; i++) {
                DrawableItem item = getChildAt(i);
                if (item == null) continue;
                item.setTouchable(currPage);
//                item.setVisibility(true);
                Matrix m = item.getMatrix();
                m.reset();

                m.setRotate(degrees, px, -20);
                m.postTranslate(d, dy);

                item.updateMatrix(m);
                item.draw(canvas);
            }

        }

        public void itemBullDozeSlide(IDisplayProcess canvas, int page, float offsetX, boolean currPage) {
            final float pageWidth = getWidth();
            final float d = Math.round(pageWidth * (offsetX + 1));

            final int startIndex = page * mCellCountX * mCellCountY;
            final int endIndex = Math.min(startIndex + mCellCountX * mCellCountY, getChildCount());

            float sx = 1 - Math.abs(offsetX);
            float px_left = sx * pageWidth;
            float px_right = offsetX * pageWidth;

            for (int i = startIndex; i < endIndex; i++) {
                DrawableItem item = getChildAt(i);
                if (item == null) continue;
                item.setTouchable(currPage);
//                item.setVisibility(true);
                Matrix m = item.getMatrix();
                m.reset();

                if (offsetX > 0.0f) {
                    m.setScale(sx, 1, px_right, 0);
                } else {
                    m.setScale(sx, 1, px_left, 0);
                }
                m.preTranslate(d, 0);

                item.updateMatrix(m);
                item.draw(canvas);
            }
        }

        public void itemWaveSlide(IDisplayProcess canvas, int page, float offsetX, boolean currPage) {
            final float pageWidth = getWidth();
            final float d = Math.round(pageWidth * (offsetX + 1));

            final int startIndex = page * mCellCountX * mCellCountY;
            final int endIndex = Math.min(startIndex + mCellCountX * mCellCountY, getChildCount());

            float scale = 1.0f - Math.abs(offsetX) / 2;
            float px = (0.5f + offsetX) * pageWidth;
            float py = getHeight() / 2;
            float translate = -offsetX * pageWidth / 4;

            for (int i = startIndex; i < endIndex; i++) {
                DrawableItem item = getChildAt(i);
                if (item == null) continue;
                item.setTouchable(currPage);
//                item.setVisibility(true);
                Matrix m = item.getMatrix();
                m.reset();

                m.setScale(scale, scale, px, py);
                m.postTranslate(translate, 0);
                m.preTranslate(d, 0);

                item.updateMatrix(m);
                item.draw(canvas);
            }
        }

        public void itemZRotateSlide(IDisplayProcess canvas, int page, float offsetX, boolean currPage) {
            final float pageWidth = getWidth();
            final float d = Math.round(pageWidth * (offsetX + 1));

            final int startIndex = page * mCellCountX * mCellCountY;
            final int endIndex = Math.min(startIndex + mCellCountX * mCellCountY, getChildCount());

            final Camera camera = mCamera;
            final float centerX = pageWidth / 2.0f + offsetX * pageWidth;
            final float centerY = getHeight() / 2.0f;
            float degrees = 90 * (-offsetX);

            for (int i = startIndex; i < endIndex; i++) {
                DrawableItem item = getChildAt(i);
                if (item == null) continue;
                item.setTouchable(currPage);
//                item.setVisibility(true);
                Matrix m = item.getMatrix();
                m.reset();

                camera.save();
                camera.rotateY(degrees);
                camera.getMatrix(m);
                camera.restore();
                m.preTranslate(-centerX + d, -centerY);
                m.postTranslate(centerX, centerY);

                item.updateMatrix(m);
                item.draw(canvas);
            }
        }

        public void itemBounceSlide(IDisplayProcess canvas, int page, float offsetX, boolean currPage) {
            final float pageWidth = getWidth();
            final float d = Math.round(pageWidth * (offsetX + 1));

            final int startIndex = page * mCellCountX * mCellCountY;
            final int endIndex = Math.min(startIndex + mCellCountX * mCellCountY, getChildCount());

            float translate = getHeight() * offsetX;
            if (offsetX > 0.0f) {
                translate *= -1;
            }

            for (int i = startIndex; i < endIndex; i++) {
                DrawableItem item = getChildAt(i);
                if (item == null) continue;
                item.setTouchable(currPage);
//                item.setVisibility(true);
                Matrix m = item.getMatrix();
                m.reset();

                m.setTranslate(0, translate);
                m.preTranslate(d, 0);

                item.updateMatrix(m);
                item.draw(canvas);
            }
        }

        public void itemScaleSlide(IDisplayProcess canvas, int page, float offsetX, boolean currPage) {
            final float pageWidth = getWidth();
            final float d = Math.round(pageWidth * (offsetX + 1));

            final int startIndex = page * mCellCountX * mCellCountY;
            final int endIndex = Math.min(startIndex + mCellCountX * mCellCountY, getChildCount());

            float ratio = 1 - Math.abs(offsetX);
            float px;
            float translate = d;
            if (offsetX > 0.0f) {
                px = pageWidth;
                translate = d - offsetX * pageWidth;
            } else {
                px = 0.0f;
            }

            for (int i = startIndex; i < endIndex; i++) {
                DrawableItem item = getChildAt(i);
                if (item == null) continue;
                item.setTouchable(currPage);
//                item.setVisibility(true);
                Matrix m = item.getMatrix();
                m.reset();

                m.setScale(ratio, ratio, px, 0);
                m.preTranslate(translate, 0);

                item.updateMatrix(m);
                item.draw(canvas);
            }
        }

        public void itemCubeSlide(IDisplayProcess canvas, int page, float offsetX, boolean currPage) {
            final float pageWidth = getWidth();
            final float radius = getWidth() / 2;
            int startIndex = page * mCellCountX * mCellCountY;
            int endIndex = Math.min(startIndex + mCellCountX * mCellCountY, getChildCount());
            final Camera camera = mCamera;

            float degree = degreeY * offsetX;
//            android.util.Log.d("Cube", "page = " + page + " offsetX = " + offsetX);
            float d = Math.round(pageWidth * offsetX);
            for (int i = startIndex; i < endIndex; i++) {
                DrawableItem item = getChildAt(i);
                if (item == null) continue;
                item.setTouchable(currPage);
//                item.setVisibility(true);
                Matrix m = item.getMatrix();
                m.reset();

                camera.save();
                camera.rotateY(degree);
                camera.getMatrix(m);
                camera.restore();
                if (degree > 0) {
                    m.preTranslate(pageWidth, -radius);
                    m.postTranslate(d, radius);
                } else {
                    m.preTranslate(0, -radius);
                    m.postTranslate(d + pageWidth, radius);
                }

                item.updateMatrix(m);
                item.draw(canvas);
            }
        }

        public void itemNormalSlide(IDisplayProcess canvas, int page, float offsetX, boolean currPage) {
            final float pageWidth = getWidth();
            int startIndex = page * mCellCountX * mCellCountY;
            int endIndex = Math.min(startIndex + mCellCountX * mCellCountY, getChildCount());

            float d = Math.round(pageWidth * (offsetX + 1));
            for (int i = startIndex; i < endIndex; i++) {
                final DrawableItem item = getChildAt(i);
                if (item == null || !item.isVisible()) continue;
                item.setTouchable(currPage);
//              item.setVisibility(true);
                final Matrix m = item.getMatrix();
                m.reset();
//              android.util.Log.i("anim", "here : " + value);
                m.setTranslate(d, 0);
                item.updateMatrix(m);
                item.draw(canvas);
            }
        }

        private void itemSnakeSlide(IDisplayProcess canvas, int page, float offsetX, boolean currPage) {
            final float pageWidth = getWidth();
            final float pageHeight = getHeight();
            final float d = Math.round(pageWidth * (offsetX + 1));

            final int startIndex = page * mCellCountX * mCellCountY;
            final int endIndex = Math.min(startIndex + mCellCountX * mCellCountY, getChildCount());

            // 计算水平滑行距离
            final int itemWidth = (mOriginalCellWidth + mWidthGap);
            final int hIntrinsicSpace = itemWidth * (mCellCountX - 1);

            // 垂直距离
            final int vIntrinsicSpace = mOriginalCellHeight + mHeightGap;

            // 每个图标的行驶总距离
            final int totalSpace = (hIntrinsicSpace + vIntrinsicSpace) * mCellCountY;

            int vSpace = (int) (localRect.height() - mPaddingTop - mPaddingBottom);
            int vFreeSpace = vSpace - (mCellCountY * mOriginalCellHeight);
            int intrinsicHGap = vFreeSpace / (mCellCountY - 1);

            // 因为mHeightGap的原因，可能会产生页码之间的页码高度差和vIntrinsicSpace不相等
            // 计算页码高度差
            int pageDiffer = (int) (localRect.height() - vIntrinsicSpace * (mCellCountY - 1));

            // 因为在item从自己页游到另一页的时候，可能会造成残留阴影，做出的调整
            // 不属于算法之内的数据，debug时应该设置为0
            final int yDirtyAdjust = Math.abs(intrinsicHGap) + 100;

            for (int i = endIndex - 1; i >= startIndex; i--) {
                DrawableItem item = getChildAt(i);
                if (item == null) continue;
                item.setTouchable(currPage);
                Matrix m = item.getMatrix();
                m.reset();

                int index = i - startIndex;
                int cellX = index % mCellCountX;
                int cellY = index / mCellCountX;

                if (offsetX < 0.0f && page == mCurrentPage) {
                    final float currentSpace = totalSpace * Math.abs(offsetX);
                    rightDirectLeftCurrent(m, cellX, cellY, currentSpace, hIntrinsicSpace,
                            vIntrinsicSpace, yDirtyAdjust, itemWidth);
                    m.preTranslate(pageWidth, 0);
                } else if (offsetX > 0.0f && page != mCurrentPage) {
                    final float currentSpace = totalSpace * Math.abs(1.0f - offsetX);
                    rightDirectLeftNotCurrent(m, cellX, cellY, currentSpace, hIntrinsicSpace,
                            vIntrinsicSpace, yDirtyAdjust, itemWidth, pageDiffer);
                    m.preTranslate(pageWidth, -pageHeight);
                } else if (offsetX > 0.0f && page == mCurrentPage) {
                    final float currentSpace = totalSpace * Math.abs(offsetX);
                    leftDirectRightCurrent(m, cellX, cellY, currentSpace, hIntrinsicSpace,
                            vIntrinsicSpace, yDirtyAdjust, itemWidth);
                    m.preTranslate(pageWidth, 0);
                } else if (offsetX < 0.0f && page != mCurrentPage) {
                    final float currentSpace = totalSpace * Math.abs(1.0f + offsetX);
                    leftDirectRightNotCurrent(m, cellX, cellY, currentSpace, hIntrinsicSpace,
                            vIntrinsicSpace, yDirtyAdjust, itemWidth, pageDiffer);
                    m.preTranslate(pageWidth, pageHeight);
                } else {
                    m.preTranslate(d, 0);
                }

                item.updateMatrix(m);
                item.draw(canvas);
            }

        }

        // 从左向右滑动，非当前页
        private void leftDirectRightNotCurrent(Matrix m, int cellX, int cellY, float currentSpace,
                int hIntrinsicSpace, int vIntrinsicSpace, int yDirtyAdjust, int itemWidth,
                int pageDiffer) {
            int xTotalTranslate = 0;
            int yTotalTranslate = 0;

            // 分别读取最左端和最右端的边缘差值
            int leftEdge = (cellX) * itemWidth;
            int rightEdge = (mCellCountX - 1 - cellX) * itemWidth;

            // 如果mCellCountY - cellY是奇数，移动方向则和offSetX一致，
            // 那么在向左移动过程中，初始水平距离是向cellX = 0做减法
            int startHSpace;
            if (Math.abs(cellY) % 2 == 1) {
                startHSpace = leftEdge;
            } else {
                startHSpace = rightEdge;
            }

            if (currentSpace > startHSpace) {
                int leftSpace = (int) (currentSpace - startHSpace);

                int yTranslate = leftSpace / (hIntrinsicSpace + vIntrinsicSpace);
                yTotalTranslate += (-yTranslate + 1) * vIntrinsicSpace - pageDiffer;

                int currentCellY = -yTranslate + cellY;

                int xTranslate = leftSpace % (hIntrinsicSpace + vIntrinsicSpace);
                if (xTranslate > vIntrinsicSpace) {
                    // 首先去除垂直距离
                    xTranslate -= vIntrinsicSpace;
                    currentCellY -= 1;
                    // 计算当前水平滑行方向
                    // 和之前一样"偶数向右，奇数向左"
                    if (Math.abs(mCellCountY + currentCellY) % 2 == 1) {
                        xTotalTranslate += rightEdge - xTranslate;
                    } else {
                        xTotalTranslate += xTranslate - leftEdge;
                    }

                    yTotalTranslate += -vIntrinsicSpace;
                    // 底端有残留
                    if (currentCellY > -1) {
                        yTotalTranslate += yDirtyAdjust;
                    }
                } else {
                    // 当前已经到了最上层，不再向上移动，而是水平移动
                    if (currentCellY == 0) {
                        if (mCellCountY % 2 == 0) {
                            // 在最右
                            xTotalTranslate += vIntrinsicSpace - xTranslate + rightEdge;
                        } else {
                            // 最左
                            xTotalTranslate += xTranslate - vIntrinsicSpace - leftEdge;
                        }
                        yTotalTranslate += -vIntrinsicSpace;
                    } else {
                        // 底端有残留
                        if (currentCellY > 0) {
                            yTotalTranslate += yDirtyAdjust;
                        }
                        // 直接垂直向上移动
                        yTotalTranslate += -xTranslate;
                        // 偶数在最右，奇数在最左
                        if (Math.abs(mCellCountY + currentCellY) % 2 == 1) {
                            xTranslate = -leftEdge;
                        } else {
                            xTranslate = rightEdge;
                        }
                        xTotalTranslate += xTranslate;
                    }
                }
            } else {
                if (Math.abs(cellY) % 2 == 1) {
                    xTotalTranslate += -currentSpace;
                } else {
                    xTotalTranslate += currentSpace;
                }
            }

            m.setTranslate(xTotalTranslate, yTotalTranslate);
        }

        // 从左向右滑动，当前页
        private void leftDirectRightCurrent(Matrix m, int cellX, int cellY, float currentSpace,
                int hIntrinsicSpace, int vIntrinsicSpace, int yDirtyAdjust, int itemWidth) {
            int xTotalTranslate = 0;
            int yTotalTranslate = 0;

            // 分别读取最左端和最右端的边缘差值
            int leftEdge = (cellX) * itemWidth;
            int rightEdge = (mCellCountX - 1 - cellX) * itemWidth;

            // 如果cellY是偶数，移动方向则和offSetX一致，
            // 那么在向右移动过程中，初始水平距离是向cellX = mCellCountX - 1做减法
            int startHSpace;
            if (Math.abs(cellY) % 2 == 1) {
                startHSpace = leftEdge;
            } else {
                startHSpace = rightEdge;
            }

            if (currentSpace > startHSpace) {
                int leftSpace = (int) (currentSpace - startHSpace);

                int yTranslate = leftSpace / (hIntrinsicSpace + vIntrinsicSpace);
                yTotalTranslate += -yTranslate * vIntrinsicSpace;

                int currentCellY = -yTranslate + cellY;

                int xTranslate = leftSpace % (hIntrinsicSpace + vIntrinsicSpace);
                if (xTranslate > vIntrinsicSpace) {
                    // 首先去除垂直距离
                    xTranslate -= vIntrinsicSpace;
                    currentCellY -= 1;
                    // 计算当前水平滑行方向
                    // 和之前一样"偶数向右，奇数向左"
                    if (Math.abs(currentCellY) % 2 == 1) {
                        xTotalTranslate += rightEdge - xTranslate;
                    } else {
                        xTotalTranslate += xTranslate - leftEdge;
                    }

                    yTotalTranslate += -vIntrinsicSpace;
                    // 上峰有残留
                    if (currentCellY <= -1) {
                        yTotalTranslate += -yDirtyAdjust;
                    }
                } else {
                    // 当前已经到了最上层，不再向上移动，而是向右移动
                    if (currentCellY == 0) {
                        xTotalTranslate += xTranslate + rightEdge;
                    } else {
                        // 上峰有残留
                        if (currentCellY <= 0) {
                            yTotalTranslate += -yDirtyAdjust;
                        }
                        // 直接垂直向上移动
                        yTotalTranslate += -xTranslate;
                        // 偶数在最右，奇数在最左
                        if (Math.abs(currentCellY) % 2 == 1) {
                            xTranslate = -leftEdge;
                        } else {
                            xTranslate = rightEdge;
                        }
                        xTotalTranslate += xTranslate;
                    }
                }
            } else {
                if (Math.abs(cellY) % 2 == 1) {
                    xTotalTranslate += -currentSpace;
                } else {
                    xTotalTranslate += currentSpace;
                }
            }

            m.setTranslate(xTotalTranslate, yTotalTranslate);
        }

        // 从右向左滑动，非当前页
        private void rightDirectLeftNotCurrent(Matrix m, int cellX, int cellY, float currentSpace,
                int hIntrinsicSpace, int vIntrinsicSpace, int yDirtyAdjust, int itemWidth,
                int pageDiffer) {
            int xTotalTranslate = 0;
            int yTotalTranslate = 0;

            // 分别读取最左端和最右端的边缘差值
            int leftEdge = (cellX) * itemWidth;
            int rightEdge = (mCellCountX - 1 - cellX) * itemWidth;

            // 如果mCellCountY - cellY是奇数，移动方向则和offSetX一致，
            // 那么在offsetX > 0过程中，初始水平距离是向cellX = mCellCountX - 1做减法
            int startHSpace;
            if (cellY % 2 == 0) {
                startHSpace = leftEdge;
            } else {
                startHSpace = rightEdge;
            }

            if (currentSpace > startHSpace) {
                int leftSpace = (int) (currentSpace - startHSpace);

                int yTranslate = leftSpace / (hIntrinsicSpace + vIntrinsicSpace);
                yTotalTranslate += (yTranslate - 1) * vIntrinsicSpace + pageDiffer;

                int currentCellY = yTranslate + cellY;
                // 因为这里会有已经游走的图标的残留，在屏幕顶部
                if (currentCellY < (mCellCountY - 1)) {
                    yTotalTranslate += -yDirtyAdjust;
                }

                int xTranslate = leftSpace % (hIntrinsicSpace + vIntrinsicSpace);
                if (xTranslate > vIntrinsicSpace) {
                    // 首先去除垂直距离
                    xTranslate -= vIntrinsicSpace;
                    currentCellY += 1;
                    // 计算当前水平滑行方向
                    // 和之前一样"偶数向左，奇数向右"
                    if ((mCellCountY - currentCellY) % 2 == 0) {
                        xTotalTranslate += rightEdge - xTranslate;
                    } else {
                        xTotalTranslate += xTranslate - leftEdge;
                    }

                    yTotalTranslate += vIntrinsicSpace;
                } else {
                    // 当前已经到了最底层，不再向下移动，而是水平移动
                    if (currentCellY == (mCellCountY - 1)) {
                        // 移动到最右端
                        xTotalTranslate += vIntrinsicSpace - xTranslate + rightEdge;
                        yTotalTranslate += vIntrinsicSpace;
                    } else {
                        // 直接垂直向下移动
                        yTotalTranslate += xTranslate;
                        // 偶数在最左，奇数在最右
                        if ((mCellCountY - currentCellY) % 2 == 0) {
                            xTranslate = -leftEdge;
                        } else {
                            xTranslate = rightEdge;
                        }
                        xTotalTranslate += xTranslate;
                    }
                }
            } else {
                if (cellY % 2 == 0) {
                    xTotalTranslate += -currentSpace;
                } else {
                    xTotalTranslate += currentSpace;
                }
            }

            m.setTranslate(xTotalTranslate, yTotalTranslate);
        }

        // 从右向左滑动，当前页
        private void rightDirectLeftCurrent(Matrix m, int cellX, int cellY, float currentSpace,
                int hIntrinsicSpace, int vIntrinsicSpace, int yDirtyAdjust, int itemWidth) {
            int xTotalTranslate = 0;
            int yTotalTranslate = 0;

            // 分别读取最左端和最右端的边缘差值
            int leftEdge = (cellX) * itemWidth;
            int rightEdge = (mCellCountX - 1 - cellX) * itemWidth;

            // 如果cellY是偶数，移动方向则和offSetX一致，
            // 那么在向左移动过程中，初始水平距离是向cellX = 0做减法
            int startHSpace;
            if (cellY % 2 == 0) {
                startHSpace = leftEdge;
            } else {
                startHSpace = rightEdge;
            }

            if (currentSpace > startHSpace) {
                int leftSpace = (int) (currentSpace - startHSpace);

                int yTranslate = leftSpace / (hIntrinsicSpace + vIntrinsicSpace);
                yTotalTranslate += yTranslate * vIntrinsicSpace;

                int currentCellY = yTranslate + cellY;
                // 因为这里会有已经游走的图标的残留，在屏幕底部
                if (currentCellY >= mCellCountY) {
                    yTotalTranslate += yDirtyAdjust;
                }

                int xTranslate = leftSpace % (hIntrinsicSpace + vIntrinsicSpace);
                if (xTranslate > vIntrinsicSpace) {
                    // 首先去除垂直距离
                    xTranslate -= vIntrinsicSpace;
                    currentCellY += 1;
                    // 计算当前水平滑行方向
                    // 和之前一样"偶数向左，奇数向右"
                    if (currentCellY % 2 == 0) {
                        xTotalTranslate += rightEdge - xTranslate;
                    } else {
                        xTotalTranslate += xTranslate - leftEdge;
                    }

                    yTotalTranslate += vIntrinsicSpace;
                    // 因为这里会有已经游走的图标的残留，在屏幕底部
                    // 所以多向下移动一部分
                    if (currentCellY == mCellCountY) {
                        yTotalTranslate += yDirtyAdjust;
                    }
                } else {
                    // 当前已经到了最底层，不再向下移动，而是水平移动
                    if (currentCellY == (mCellCountY - 1)) {
                        if (mCellCountY % 2 == 0) {
                            // 总行数偶数个，水平向右，和起始行方向相反
                            xTotalTranslate += xTranslate + rightEdge;
                        } else {
                            // 奇数向左
                            xTotalTranslate += -xTranslate - leftEdge;
                        }
                    } else {
                        // 直接垂直向下移动
                        yTotalTranslate += xTranslate;
                        // 偶数在最左，奇数在最右
                        if (currentCellY % 2 == 0) {
                            xTranslate = -leftEdge;
                        } else {
                            xTranslate = rightEdge;
                        }
                        xTotalTranslate += xTranslate;
                    }
                }
            } else {
                if (cellY % 2 == 0) {
                    xTotalTranslate += -currentSpace;
                } else {
                    xTotalTranslate += currentSpace;
                }
            }

            m.setTranslate(xTotalTranslate, yTotalTranslate);
        }

        public void itemWormSlide(IDisplayProcess canvas, int page, float offsetX, boolean currPage) {
            final float pageWidth = getWidth();
            int startIndex = page * mCellCountX * mCellCountY;
            int endIndex = Math.min(startIndex + mCellCountX * mCellCountY, getChildCount());

            float dx, ts, input, d;
            for (int i = startIndex; i < endIndex; i++) {
                DrawableItem item = getChildAt(i);
                if (item == null) continue;
                item.setTouchable(currPage);
                Matrix m = item.getMatrix();
                m.reset();
                if (mOffsetX > 0) {
                    dx = currPage ? -item.localRect.right : -item.localRect.right + pageWidth;
                    ts = dx / pageWidth * .3f;
                    if (mOffsetX > ts) {
                        input = (mOffsetX - ts) / (1 - ts);
                        input = .5f + .5f * FloatMath.sin(-MY_PI / 2 + MY_PI * input);
                    } else {
                        input = 0f;
                    }
                    d = Math.round(pageWidth * (input + (currPage ? 1 : 0)));
                } else {
                    dx = currPage ? item.localRect.left + pageWidth : item.localRect.left + pageWidth + pageWidth;
                    ts = dx / pageWidth * .3f;
                    if (mOffsetX < -ts) {
                        input = (mOffsetX + ts) / (ts - 1);
                        input = -.5f - .5f * FloatMath.sin(-MY_PI / 2 + MY_PI * input);
                    } else {
                        input = 0f;
                    }
                    d = Math.round(pageWidth * (input + (currPage ? 1 : 2)));
                }
                m.setTranslate(d, 0);
                item.updateMatrix(m);
                item.draw(canvas);
            }
        }

        @Override
        public void reset() {
            
        }

    }

    /* RK_ID: RK_SLIDEEFFECT. AUT: liuli1 . DATE: 2012-12-06 . END */
    
    private class PageAnimController implements IController {
        
        private boolean offsetXAnimActivate = false;
        private boolean once, inertia = false;
        private boolean offsetYAnimActivate = false;
        private boolean pageTouchAnimActivate = false;
        private boolean isSphereSlide = false;
        private DecelerateInterpolator mDInterpolator;
        private AccelerateDecelerateInterpolator mADInterpolator;
        private long mOffsetXAnimDuration = 0, mOffsetXAnimPlayTime = 0;
        private float s_x, s0_x;
        private long mOffsetYAnimDuration = 0, mOffsetYAnimPlayTime = 0;
        private float s_y, s0_y;
        
        public PageAnimController() {
            mDInterpolator = new DecelerateInterpolator(2.5f);
            mADInterpolator = new AccelerateDecelerateInterpolator();
        }
        
        public void startOffsetXAnim(long durationMillis, boolean inertia) {
            this.inertia = inertia;
            if (inertia && Math.abs(mOffsetX) > .4f) {
                mDInterpolator = new DecelerateInterpolator(3.5f);
                if (mOffsetX > 0) {
                    mOffsetXTarget = -.05f;
                } else {
                    mOffsetXTarget = .05f;
                }
                once = true;
            } else {
                mDInterpolator = new DecelerateInterpolator(2.5f);
                mOffsetXTarget = 0;
                once = false;
            }
            offsetXAnimActivate = true;
            mOffsetXAnimDuration = durationMillis;
            mOffsetXAnimPlayTime = 0;
            s0_x = mOffsetX;
            s_x = mOffsetXTarget - mOffsetX;

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
            s_y = - mOffsetY;

        }
        
        public void stopOffsetYAnim() {
            offsetYAnimActivate = false;
        }
        
        public boolean isOffsetYAnimStart() {
            return offsetYAnimActivate;
        }
        
        public void startTouchAnim() {
            pageTouchAnimActivate = true;
        }
        
        public void stopTouchAnim() {
            pageTouchAnimActivate = false;
        }

        public void setSphereSlide(boolean isSphereSlide) {
            this.isSphereSlide = isSphereSlide;
        }

        @Override
        public void update(long timeDelta) {

            if (offsetXAnimActivate) {
                mOffsetXAnimPlayTime += timeDelta;
                float step = 0;
                if (inertia && !once) {
                    step = s_x * mADInterpolator.getInterpolation((float) mOffsetXAnimPlayTime / (float) mOffsetXAnimDuration);
                } else {
                    step = s_x * mDInterpolator.getInterpolation((float) mOffsetXAnimPlayTime / (float) mOffsetXAnimDuration);
                }
                mOffsetX = s0_x + step;
                if (inertia && once && Math.abs(mOffsetX - mOffsetXTarget) < 0.000925926f) {
                    mOffsetXTarget = 0;
                    once = false;
                    mOffsetXAnimDuration = 150;
                    mOffsetXAnimPlayTime = 0;
                    s0_x = mOffsetX;
                    s_x = mOffsetXTarget - mOffsetX;
                }
                if (Math.abs(mOffsetX - mOffsetXTarget) < 0.000925926f || mOffsetXAnimPlayTime >= mOffsetXAnimDuration) {
                    mOffsetX = mOffsetXTarget;
                    stopOffsetXAnim();
                    isPageMoving = false;
                    onPageEndMoving();
                }
                invalidate();
            }
            if (offsetYAnimActivate) {
                mOffsetYAnimPlayTime += timeDelta;
                if (mOffsetYAnimPlayTime >= mOffsetYAnimDuration) {
                    mOffsetY = 0f;
                    mOffsetYTarget = 0f;
                    stopOffsetYAnim();

                } else {
                    float step = s_y * mDInterpolator.getInterpolation((float) mOffsetYAnimPlayTime / (float) mOffsetYAnimDuration);
                    mOffsetY = s0_y + step;
                    mOffsetYTarget = mOffsetY;
                }
                invalidate();
            }
            if (pageTouchAnimActivate) {
                float delta = mOffsetXTarget - mOffsetX;
                mOffsetX += delta * .4f;
                mOffsetY += (mOffsetYTarget - mOffsetY) * .8f;
//                if (Math.abs(delta) < 0.002f) {
//                    mOffsetXTarget = mOffsetX;
//                    stopTouchAnim();
//                }
                invalidate();
                if (mOffsetX > 1f) {
                    if (mCurrentPage - 1 > -1) {

						// R2
						if (pageSwitcher != null) {
							pageSwitcher.onPageSwitching(mCurrentPage,
									mCurrentPage - 1, mOffsetX);
						}
						// 2R

                        --mCurrentPage;
                    } else if (isLoop) {
                        mCurrentPage = mPageCount - 1;
						// R2
						if (pageSwitcher != null) {
							pageSwitcher.onPageSwitching(0, mCurrentPage,
									mOffsetX);
						}
						// 2R
                    }
                    mOffsetX -= 1f;
                    mOffsetXTarget -= 1f;
                } else if (mOffsetX < -1) {
                    if (mCurrentPage + 1 < mPageCount) {

						// R2
						if (pageSwitcher != null) {
							pageSwitcher.onPageSwitching(mCurrentPage,
									mCurrentPage + 1, mOffsetX);
						}
						// 2R

                        ++mCurrentPage;
                    } else if (isLoop) {

						// R2
						if (pageSwitcher != null) {
							pageSwitcher.onPageSwitching(mCurrentPage, 0,
									mOffsetX);
						}
						// 2R
                        mCurrentPage = 0;
                    }
                    mOffsetX += 1f;
                    mOffsetXTarget += 1f;
                }
                currOrientation = (int) Math.signum(delta);
            }

            if (isSphereSlide) {
                if (pageTouchAnimActivate /*&& Math.abs(mOffsetX) < 0.4f*/) {
                    if (Math.abs(mOffsetX) < 0.4f) {
                        float delta = rect2ballTarget - rect2ball;
                        if (Math.abs(delta) < 0.002f) {
                            rect2ball = rect2ballTarget;
                            return;
                        }
                        float temp = (Math.abs(mOffsetX)) / 0.4f * (rect2ballTarget - rect2ball) + rect2ball;
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

//	public void setup(Launcher launcher) {
//		mLauncher = launcher;		
//	}

    private ConcurrentLinkedQueue<Runnable> idleTaskList = new ConcurrentLinkedQueue<Runnable>();

	public void addApps(final List<ApplicationInfo> list) {
	    if (isDraggingMode()) {
	        idleTaskList.add(new Runnable() {
                @Override
                public void run() {
                    addAppsWithoutInvalidate(list);
                    updatePageCount();
                }
            });
	        return;
        }
		addAppsWithoutInvalidate(list);
		updatePageCount();
		invalateData();		
	}
	
	private void addAppsWithoutInvalidate(List<ApplicationInfo> list) {
        // We add it in place, in alphabetical order
        int count = list.size();
        AllAppSortHelper mSortHelper = null;
        if (mContext.getContext() instanceof XLauncher) {
        	mSortHelper = ((XLauncher)mContext.getContext()).getSortHelper();
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext.getContext());
        int sortmodevalue = preferences.getInt(SettingsValue.KEY_APPLIST_CURRENT_SORTMODE, 0);
        int sortmod = sortmodevalue >> 4;
        boolean isSortByDes = (sortmodevalue & 0xf) != SettingsValue.SORT_BY_ASC;
        /*** RK_ID: APP_SORT. AUT: zhaoxy . DATE: 2012-05-09. END***/
        for (int i = 0; i < count; ++i) {
            ApplicationInfo info = list.get(i);
            /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. START***/
            if (info.hidden) {
                if (HiddenApplist.DEBUG) Log.d(HiddenApplist.TAG, "add pass " + info.componentName.flattenToShortString());
                continue;
            }
            /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. END***/
            /*** RK_ID: APP_SORT. AUT: zhaoxy . DATE: 2012-05-09. START***/
            if (mSortHelper.checkCannotDrag(info)) {
				info.canDrag = false;
			} else {
			    info.canDrag = true;
			}

            int index = 0;
            switch (sortmod) {
            case -1:
            case 3:
            case 4:
                index = Collections.binarySearch(infos, info, mSortHelper.getComparator(AllAppSortHelper.HISTORY_COMPARATOR));
                break;
            case 1:
                index = Collections.binarySearch(infos, info, mSortHelper.getComparator(AllAppSortHelper.NAME_COMPARATOR));
                break;
            case 2:
                if (isSortByDes) {
                    index = Collections.binarySearch(infos, info, mSortHelper.getComparator(AllAppSortHelper.FIRST_INSTALL_COMPARATOR_DES));
                } else {
                    index = Collections.binarySearch(infos, info, mSortHelper.getComparator(AllAppSortHelper.FIRST_INSTALL_COMPARATOR_ASC));
                }
                break;
            case 0:
            default:
                index = Collections.binarySearch(infos, info, mSortHelper.getComparator(AllAppSortHelper.REGULAR_COMPARATOR));
                break;
            }
            if (index < 0) {
                Log.d("addAppsWithoutInvalidate", "case 0 add insert index = " + (-(index + 1)));
                infos.add(-(index + 1), info);
            }
            /*** RK_ID: APP_SORT. AUT: zhaoxy . DATE: 2012-05-09. END***/
        }
    }

	public void removeApps(final List<ApplicationInfo> list) {
	    if (isDraggingMode()) {
            idleTaskList.add(new Runnable() {
                @Override
                public void run() {
                    removeAppsWithoutInvalidate(list);
                    updatePageCount();
                }
            });
            return;
        }
		removeAppsWithoutInvalidate(list);
		updatePageCount();
		invalateData();
	}
	
	private void removeAppsWithoutInvalidate(List<ApplicationInfo> list) {
        // loop through all the apps and remove apps that have the same component
        int length = list.size();
        for (int i = 0; i < length; ++i) {
            ApplicationInfo info = list.get(i);
            int removeIndex = findAppByComponent(infos, info);
            if (removeIndex > -1) {
				infos.remove(removeIndex);
            }
        }
    }
	
	private int findAppByComponent(List<ApplicationInfo> list, ApplicationInfo item) {
        ComponentName removeComponent = item.intent.getComponent();
        int length = list.size();
        for (int i = 0; i < length; ++i) {
            ApplicationInfo info = list.get(i);
            if (info.intent.getComponent().equals(removeComponent)) {
                return i;
            }
        }
        return -1;
    }

	public void updateApps(final List<ApplicationInfo> list) {
	    if (isDraggingMode()) {
            idleTaskList.add(new Runnable() {
                @Override
                public void run() {
                    removeAppsWithoutInvalidate(list);
                    addAppsWithoutInvalidate(list);
                    updatePageCount();
                }
            });
            return;
        }
		removeAppsWithoutInvalidate(list);
        addAppsWithoutInvalidate(list);
        updatePageCount();
		invalateData();		
	}

	public void hideApps(List<ApplicationInfo> list) {
//		long s = System.currentTimeMillis();
        updateHideInfo(list);
        updatePageCount();
        invalateData();		
	}
	
	private void updateHideInfo(List<ApplicationInfo> list) {
        // loop through all the apps and update apps that have the same component
        ApplicationInfo temp = null;
        for (int i = list.size() - 1; i >= 0; --i) {
            temp = list.get(i);
            if (temp.hidden) {
                int removeIndex = findAppByComponent(infos, temp);
                if (removeIndex > -1) {
                    infos.remove(removeIndex);
                    list.remove(i);
                }
            }
        }
        addAppsWithoutInvalidate(list);
    }
	
	public ArrayList<ApplicationInfo> getApps() {
		return infos;
	}
	
	public void startEditMode(final boolean delete) {
//		List<DrawableItem> childrens = getChildren();
		mEditMode = true;
		mVibrator.vibrate(VIBRATE_DURATION);

        if (getChildCount() > 0) {
//            int size = childrens.size();
            for (int i = 0; i < getChildCount(); i++) {
                DrawableItem item = getChildAt(i);

                if (item instanceof XIconView) {
                    XIconView iconView = (XIconView) item;
                    iconView.startEditMode(delete);
                }
            }
        }
        
        ((XApplistView) getParent()).startEditMode();
	}
	
	public void stopEditMode() {

//		List<DrawableItem> childrens = getChildren();
		mEditMode = false;
		updateSlideValue();

		//sync sort and stop edit mode
		ArrayList<ApplicationInfo> tmpInfos = new ArrayList<ApplicationInfo>();
        if (getChildCount() > 0) {
//            int size = childrens.size();
            for (int i = 0; i < getChildCount(); i++) {
                DrawableItem item = getChildAt(i);

                if (item instanceof XIconView) {
                	
                    XIconView iconView = (XIconView) item;
                    //R2 -- order
                    tmpInfos.add(iconView.getLocalInfo());
                    //2R
                    iconView.stopEditMode();
                }
            }
            
            //R2
            //sync to db
			infos = tmpInfos;
			if (sortedByDragging) {
				SharedPreferences preferences = PreferenceManager
						.getDefaultSharedPreferences(mContext.getContext());
				int sortmodevalue = preferences.getInt(
						SettingsValue.KEY_APPLIST_CURRENT_SORTMODE, 0);
				int sortmod = sortmodevalue >> 4;
				if (sortmod >= 0) {
					preferences
							.edit()
							.putInt(SettingsValue.KEY_APPLIST_CURRENT_SORTMODE,
									AllAppSortHelper.HISTORY_COMPARATOR)
							.apply();
                }

				if (mContext.getContext() instanceof XLauncher) {
					((XLauncher)mContext.getContext()).getAllAppThread().setAction(
							AllApplicationsThread.ACTION_SYNC_TODB, infos);
				}
				sortedByDragging = false;
			}
            //2R
        }
        
        
        invalidate();
        
	}
	
	public boolean isEditMode() {
		return mEditMode;
	}

	//R2
	public boolean isDraggingMode(){
		return mDraggingMode;
	}
	//2R

    /* RK_ID: RK_TASKKILLBAR. AUT: liuli1 . DATE: 2012-11-30 . START */
    public void killRunningTask() {
        for (int i = mAppRunningTasks.size() - 1; i >= 0; i--) {
            ApplicationInfo info = mAppRunningTasks.get(i);
            mActivityManager.killBackgroundProcesses(info.componentName.getPackageName());
        }

        ArrayList<RunningAppProcessInfo> appRunningList = (ArrayList<RunningAppProcessInfo>) mActivityManager
                .getRunningAppProcesses();
        for (int i = appRunningList.size() - 1; i >= 0; i--) {
            RunningAppProcessInfo runningInfo = appRunningList.get(i);
            if (filterRunningApp(runningInfo)) {
                mActivityManager.killBackgroundProcesses(runningInfo.processName);
            }
        }

        updateRunningTask();
    }

    private boolean filterRunningApp(RunningAppProcessInfo info) {
        if (info == null) {
            return false;
        }
        for (int i = 0; i < EXCEPTION_RUNNING_LIST.length; i++) {
            if (EXCEPTION_RUNNING_LIST[i].equals(info.processName)) {
                return false;
            }
        }
        return true;
    }

    public void updateRunningTask() {
        updatePageCount();
        if (isEditMode()) {
            mEditMode = false;
        }

        invalateData();
    }

    public void setCurrentTab(String tab) {
        mCurrentTag = tab;
    }
    /* RK_ID: RK_TASKKILLBAR. AUT: liuli1 . DATE: 2012-11-30 . END */

    //R2
    private boolean mDraggingMode = false;
    private boolean mLongPressed = false;
    
    public boolean onLongPress(MotionEvent e) {

        //D2
//kangwei3
//        resetTouchBounds();
        desireTouchEvent(false);
    	if(mAnimController != null){
    		mAnimController.stopTouchAnim();
    	}
        mLongPressed = true;
    	boolean res = super.onLongPress(e);
    	return res;
    }
    
    private void onPageBeginMoving() {
        Log.d("Moving", "Begin");
        randomSlideValue();
        
        for(int i = 0 ; i< getChildCount(); i ++){
        	final DrawableItem item = getChildAt(i);
        	if(item != null ){
        		getChildAt(i).resetPressedState();
        	}
        }
    }
    
    private void onPageEndMoving() {
        Log.d("Moving", "End");

        if(isEditMode()){
        	mPageDrawAdapter = null;
        	updateSlideValue();
        	if (mDraggingMode && mLastDragObjectBeforeScrool != null) {
                if (sortingOntheWay
                        || lockCheckAfterScroll
                        || !sortAnimators.isEmpty()) {
                    return;
                }

                /*** RK_ID: DRAG. AUT: zhaoxy . DATE: 2013-03-04 . START ***/
                if(mDraggingItemOrigin != null) {
                    if (!((XIconView) mDraggingItemOrigin).getLocalInfo().canDrag) {
                        return;
                    }
                }
                /*** RK_ID: DRAG. AUT: zhaoxy . DATE: 2013-03-04 . END ***/
                
                mHandler.removeMessages(MSG_CHECK_TARGET);
                mHandler.removeMessages(MSG_SORT);
                Message msg = Message.obtain();
                msg.what = MSG_CHECK_TARGET;
                msg.obj = mLastDragObjectBeforeScrool;
                msg.arg1 = getChildIndex(mDraggingItemOrigin);
                mHandler.sendMessageDelayed(msg,
                        CHECK_TARGET_DELAY);
            }
        	mLastDragObjectBeforeScrool = null;
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
    
	public void scrollToLeft(long duration) {
		initPageDrawAdapter(LauncherPersonalSettings.SLIDEEFFECT_NORMAL);
		
		if (mAnimController == null) {
			return;
		}

        if (mCurrentPage - 1 > -1) {
            mCurrentPage--;
            mOffsetX = -1;
        } else {
            if (isLoop) {
                mCurrentPage = mPageCount - 1;
                mOffsetX = -1;
            } else {
                return;
            }
        }
		
		if (pageSwitcher != null) {
			pageSwitcher.onPageSwitching(mCurrentPage, mCurrentPage, 0);
		}
		
		mAnimController.startOffsetXAnim(duration, false);
		if (!isPageMoving) {
            isPageMoving = true;
            onPageBeginMoving();
        }
	}

	public void scrollToRight(long duration) {
		initPageDrawAdapter(LauncherPersonalSettings.SLIDEEFFECT_NORMAL);
		
		if (mAnimController == null) {
			return;
		}

		if (mCurrentPage + 1 < mPageCount) {
		    mCurrentPage ++;
		    mOffsetX = 1;
		} else {
		    if (isLoop) {
                mCurrentPage = 0;
                mOffsetX = 1;
            } else {
                return;
            }
		}
		
		if (pageSwitcher != null) {
			pageSwitcher.onPageSwitching(mCurrentPage, mCurrentPage, 0);
		}
		
		mAnimController.startOffsetXAnim(duration, false);
		if (!isPageMoving) {
            isPageMoving = true;
            onPageBeginMoving();
        }
	}
	
	private static final int CHECK_TARGET_DELAY = 100;
	private static final int SORT_DELAY = 16;
	private static final int SCROLL_DUARATION = 1000;
	private static final int LOCK_AFTER_SCROLL_DUARATION = 500;
	private static final int ANIM_TO_LOCATION_DELAY = 10;
	private static final int SORT_ANIMATION_DELAY = 250;
	private static final int SCROLL_RANGE = 40;
	
	private static final int MSG_SORT = 1;
	private static final int MSG_CHECK_TARGET = 2;
	private static final int MSG_UNLOCK_SCROLL_AFTER_DELAY = 3;
	private static final int MSG_RUN_IDLE_TASK = 4;
	private static final int MSG_SCROLL_DELAY = 5;
	
	private boolean sortingOntheWay = false;
	private boolean lockCheckAfterScroll = false;
	private boolean sortedByDragging = false;
	
	private int currentVisibleIconListRangeStart = 0;
	private int currentVisibleIconListRangeEnd = 0;
	private XDragObject mLastDragObjectBeforeScrool = null;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_SORT:
//				android.util.Log.i("sort1", "now sort: (" + msg.arg1 + " to "
//						+ msg.arg2 + ")");
			    final int currentPageListStart = currentVisibleIconListRangeStart;
                final int currentPageListEnd = currentVisibleIconListRangeEnd;

				sortingOntheWay = true;
				
				final int from = msg.arg1;
				final int to = msg.arg2;
				boolean leftToRight = from > to;
				XIconView a = null;
				XIconView b = null;
				XIconView tmp = null;
				int count = 0;  
				if (leftToRight) {
					for (int i = from; i > to; i--) {
						if (i == from) {
							a = (XIconView) mDraggingItemOrigin;
						} else {
							a = tmp == null ? (XIconView) getChildAt(i) : tmp;
						}
						
						b = (XIconView) getChildAt(i - 1);
						//add by xingqx ,sometime is null
						if(a == null || b == null) {
							return;
						}
						tmp = new XIconView(b);

						final RectF tmpRect = tmp.localRect;
						final int cursor = i;
						final XIconView fa = a;
						final XIconView fb = b;
						if (i > currentPageListEnd - 1) {
						    fb.setRelativeX(fa.localRect.left);
						    fb.setRelativeY(fa.localRect.top);
						    Matrix m = fb.getMatrix();
						    m.reset();
						    fb.updateMatrix(m);
						    
                            if (cursor != from && cursor != to) {
                                fa.setRelativeX(tmpRect.left);
                                fa.setRelativeY(tmpRect.top);
                            }

                            moveChildToIndex(fb, cursor);
                            moveChildToIndex(fa, cursor - 1);
                        } else {
                            animateIconView(b, a.localRect.left, a.localRect.top, count * ANIM_TO_LOCATION_DELAY, 
                                    new Runnable() {
                                @Override
                                public void run() {
                                    if(cursor != from && cursor != to){
                                        fa.setRelativeX(tmpRect.left);
                                        fa.setRelativeY(tmpRect.top);
                                    }
                                    
                                    moveChildToIndex( fb, cursor);
                                    moveChildToIndex( fa, cursor - 1);										
                                }
                            });
                            count ++;
                        }
					}

				} else {
					for (int i = from; i < to; i++) {
						a = (tmp == null) ? (XIconView) getChildAt(i) : tmp;
						
						b = (XIconView) getChildAt(i + 1);
						//add by xingqx ,sometime is null
						if(a == null || b == null) {
							return;
						}
						tmp = new XIconView(b);

						final RectF tmpRect = tmp.localRect;
						final int cursor = i;
						final XIconView fa = a;
						final XIconView fb = b;
						if (i < currentPageListStart) {
                            fb.setRelativeX(fa.localRect.left);
                            fb.setRelativeY(fa.localRect.top);
                            Matrix m = fb.getMatrix();
                            m.reset();
                            fb.updateMatrix(m);

                            if (cursor != from && cursor != to) {
                                fa.setRelativeX(tmpRect.left);
                                fa.setRelativeY(tmpRect.top);
                            }

                            moveChildToIndex(fb, cursor);
                            moveChildToIndex(fa, cursor + 1);
                        } else {
                            animateIconView(b, a.localRect.left, a.localRect.top,
                                    count * ANIM_TO_LOCATION_DELAY , 
                                    new Runnable() {
                                @Override
                                public void run() {
                                    if( cursor != from && cursor != to ){
                                        fa.setRelativeX(tmpRect.left);
                                        fa.setRelativeY(tmpRect.top);
                                    }
                                    
                                    moveChildToIndex(fb, cursor );
                                    moveChildToIndex(fa, cursor + 1);										
                                }
                            });
                            count ++;
                        }

					}
				}
				
//				animateIconView(mDraggingItemOrigin, tmp.localRect.left, 
//						tmp.localRect.top, (count + 1) * ANIM_TO_LOCATION_DELAY, 
//						new Runnable() {
//							@Override
//							public void run() {
//				moveChildToIndex(mDraggingItemOrigin, to);
//							}
//						});

				mDraggingItemOrigin.setRelativeX(tmp.localRect.left);
				mDraggingItemOrigin.setRelativeY(tmp.localRect.top);
				moveChildToIndex(mDraggingItemOrigin, to);
				
				tmp = null;
				sortingOntheWay = false;
				break;

			case MSG_CHECK_TARGET:
				//MotionEvent e = (MotionEvent) msg.obj;
				final XDragObject mDragObject = (XDragObject) msg.obj;
				int startIndex = msg.arg1;
				int endIndex = startIndex;
				float[] points = new float[2];
				currentVisibleIconListRangeStart = mCurrentPage * mCellCountX * mCellCountY;
				currentVisibleIconListRangeEnd = Math.min(currentVisibleIconListRangeStart + 
						mCellCountX * mCellCountY, getChildCount());
				boolean findout = false;
              f:for (int i = currentVisibleIconListRangeStart; i < currentVisibleIconListRangeEnd; i++) {
					DrawableItem itemTmp = getChildAt(i);
					Matrix mx = itemTmp.getInvertMatrix();
					//points[0] = e.getX();
					//points[1] = e.getY();
					points[0] = mDragObject.x;
                    points[1] = mDragObject.y;
					
					if(checkScroll(points)){
					    mLastDragObjectBeforeScrool = mDragObject;
						return;
					}
					
					mx.mapPoints(points);

					if (itemTmp.localRect.contains(points[0], points[1])) {
						
						endIndex = i;

						if (endIndex == startIndex) {
							continue;
						}else{
							Message sortMessage = Message.obtain();
							sortMessage.what = MSG_SORT;
							sortMessage.arg1 = startIndex;
							sortMessage.arg2 = endIndex;
							
							removeMessages(MSG_SORT);
							sendMessageDelayed(sortMessage, SORT_DELAY);
							findout = true;
						}
						
						break f;
					}
				}

				if (!findout) {
                    if (mCurrentPage == getPageCount() - 1) {
                        endIndex = currentVisibleIconListRangeEnd - 1;
                        if (endIndex == startIndex) {
                            break;
                        }
                        DrawableItem itemTmp = getChildAt(endIndex);
                        Matrix mx = itemTmp.getInvertMatrix();
                        points[0] = mDragObject.x;
                        points[1] = mDragObject.y;
                        mx.mapPoints(points);
                        if(points[1] > itemTmp.localRect.bottom || (points[0] > itemTmp.localRect.right && points[1] > itemTmp.localRect.top)) {
                            Message sortMessage = Message.obtain();
                            sortMessage.what = MSG_SORT;
                            sortMessage.arg1 = startIndex;
                            sortMessage.arg2 = endIndex;

                            removeMessages(MSG_SORT);
                            sendMessageDelayed(sortMessage, SORT_DELAY);
                        }
                    }
                }

				break;
			case MSG_RUN_IDLE_TASK:
			    if (sortAnimators.isEmpty()) {
			        if (idleTaskList != null && !idleTaskList.isEmpty()) {
			            while (idleTaskList != null && !idleTaskList.isEmpty()) {
			                idleTaskList.poll().run();
			            }
			            invalateData();
			        }
			    } else {
			        sendEmptyMessageDelayed(MSG_RUN_IDLE_TASK, 300);
			    }
			    break;
			case MSG_UNLOCK_SCROLL_AFTER_DELAY:
				lockCheckAfterScroll = false;
				break;
			case MSG_SCROLL_DELAY:
                switch (msg.arg1) {
                    case ORI_LEFT:
                        scrollToLeft(SCROLL_DUARATION);
                        break;
                    case ORI_RIGHT:
                        scrollToRight(SCROLL_DUARATION);
                        break;
                    default:
                    	break;
                }
                break;
            default:
            	break;
			}
		};
	};

	private boolean checkScroll(float[] points) {
		boolean needScroll = false;
		if (mPageCount < 2) {
            return needScroll;
        }
        if (mDragController.getLastDragTarget() != null
                && points[0] - getParent().localRect.left < SCROLL_RANGE) {
//			android.util.Log.i("scroll", "touch scroll to left .");
//			scrollToLeft(SCROLL_DUARATION);
			if (!mHandler.hasMessages(MSG_SCROLL_DELAY)) {
                Message msg = mHandler.obtainMessage(MSG_SCROLL_DELAY, ORI_LEFT, 0);
                mHandler.sendMessage(msg);
                needScroll = true;
            }
		} else if (getParent().localRect.right - points[0] < SCROLL_RANGE) {
//			android.util.Log.i("scroll", "touch scroll to Right .");
            if (!mHandler.hasMessages(MSG_SCROLL_DELAY)) {
                Message msg = mHandler.obtainMessage(MSG_SCROLL_DELAY, ORI_RIGHT, 0);
                mHandler.sendMessage(msg);
                needScroll = true;
            }
		} else {
            mHandler.removeMessages(MSG_SCROLL_DELAY);
            needScroll = false;
        }

		if( needScroll ){
			lockCheckAfterScroll = true;
			mHandler.sendEmptyMessageDelayed(MSG_UNLOCK_SCROLL_AFTER_DELAY, LOCK_AFTER_SCROLL_DUARATION);
		}
		return needScroll;
	}
	
	private ConcurrentHashMap<DrawableItem, ValueAnimator> sortAnimators =
			new ConcurrentHashMap<DrawableItem, ValueAnimator>();

	private synchronized void animateIconView(final DrawableItem child, final float toX,
			final float toY, final int delay, final Runnable workAfterAnimation) {
		final float fromX = child.localRect.left;
		final float fromY = child.localRect.top;
		
		final float deltaX = toX - fromX;
		final float deltaY = toY - fromY;

		final ValueAnimator anim = ValueAnimator.ofFloat(0, 1);

		anim.setDuration( SORT_ANIMATION_DELAY );
		anim.setStartDelay(delay);
		sortAnimators.put(child, anim);
		anim.addListener(new AnimatorListener() {

				@Override
				public void onAnimationStart(Animator animation) {
				}

				@Override
				public void onAnimationRepeat(Animator animation) {
				}

				@Override
				public void onAnimationEnd(Animator animation) {
				if( workAfterAnimation != null ){
					workAfterAnimation.run();
				}
					sortAnimators.remove(child);
//				android.util.Log.i("anim", "now sort animators : " + sortAnimators);
				if( sortAnimators.isEmpty() ){
					sortingOntheWay = false;
				}
				}

				@Override
				public void onAnimationCancel(Animator animation) {
				}
			});

		anim.addUpdateListener(new AnimatorUpdateListener() {

				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					Float value = (Float) animation.getAnimatedValue();
				child.setRelativeX( fromX + value * deltaX);
				child.setRelativeY( fromY + value * deltaY);
				}
			});

		getXContext().getRenderer().injectAnimation(anim, true);

			sortedByDragging = true;
		}

	public int getPageCount(){
		return mPageCount;
	}

	// 2R
	private Drawable mPositionBg = null;
	
    public void setAppToPosition(ComponentName componentName)
    {
        int index = findAppByComponent(infos, componentName);
        int numCells = mCellCountX * mCellCountY;
        int screen = index / numCells;
//        mCurrentPage = screen;
//        updateAllItemsTouchable();
        setCurrentPage(screen);
                
        XIconView icon = (XIconView)getChildAt(index);
        if (mPositionBg == null) {
            LauncherApplication app = (LauncherApplication)mContext.getContext().getApplicationContext();
            mPositionBg = app.mLauncherContext.getDrawable(R.drawable.apps_list_locate_bg);         
        }
        if (icon != null) {
            icon.setBackgroundDrawable(mPositionBg);
        }
        
        mAppToPositionView = icon;
                    
        invalidate();
    }  
    
    XIconView mAppToPositionView;
    
    public void clearAppToPostionView() {
        
        if (mAppToPositionView != null)
        {
            mAppToPositionView.setBackgroundDrawable(null);
//            mAppToPositionView.setBackgroundResource(R.drawable.focusable_view_bg);
            mAppToPositionView.invalidate();
            mAppToPositionView = null;
//            Exception e = new Exception();
//            e.printStackTrace();
        }
            
        return;
    }
    
    public void setCurrentPage(int currentPage) {
        if (currentPage >= 0 && currentPage < getPageCount()) {
            int oldPage = mCurrentPage;
            mCurrentPage = currentPage;
            updateAllItemsTouchable();
            if (oldPage != mCurrentPage && pageSwitcher != null) {
                pageSwitcher.onPageSwitching(mCurrentPage,
                        mCurrentPage, 0);
            }
            
            invalidate();
        }
    }
    
    /**
     * 仅保留当前屏应用响应touch事件，其他屏应用不接收事件
     */
    private void updateAllItemsTouchable() {
        final int countPrePage = mCellCountX * mCellCountY;
        final int startIndex = mCurrentPage * countPrePage;
        final int endIndex = Math.min(startIndex + countPrePage, getChildCount());
        for (int i = 0; i < getChildCount(); i++) {
            XIconView item = (XIconView) getChildAt(i);
            if (item == null) continue;
            if (i >= startIndex && i < endIndex) {
                item.setTouchable(true);
            } else {
                item.setTouchable(false);
            }
        }
    }
    
    private int findAppByComponent(List<ApplicationInfo> list, ComponentName componentName) {
        int length = list.size();
        for (int i = 0; i < length; ++i) {
            ApplicationInfo info = list.get(i);
            if (info.intent.getComponent().equals(componentName)) {
                return i;
            }
        }
        return -1;
    }
    
    public void setIconTextBackgroundEnable(boolean enable) {
        for (int i = 0; i < getChildCount(); i++) {
            final DrawableItem item = getChildAt(i);
            if (item instanceof XIconView) {
                ((XIconView) item).setBackgroundEnable(enable);
            }
        }
    }

    /*** RK_ID: DRAG. AUT: zhaoxy . DATE: 2013-02-19 . START ***/
    private static final boolean DEBUG_DRAG = true;
    private static final String TAG_DEBUG_DRAG = "DEBUG_DRAG";
    
    @Override
    public boolean isDropEnabled() {
        return getParent().isVisible();
    }

    @Override
    public void onDrop(XDragObject dragObject) {
        if (DEBUG_DRAG) Log.d(TAG_DEBUG_DRAG, "onDrop   AppContentView");
        mHandler.removeMessages(MSG_CHECK_TARGET);
        mHandler.removeMessages(MSG_SORT);
        if( mDraggingItemOrigin != null ){
            mDraggingItemOrigin.setVisibility( true );
        }
    }

    @Override
    public void onDragEnter(XDragObject dragObject) {
        if (DEBUG_DRAG) Log.d(TAG_DEBUG_DRAG, "onDragEnter   AppContentView");
    }

    @Override
    public void onDragOver(XDragObject dragObject) {
        //if (DEBUG_DRAG) Log.d(TAG_DEBUG_DRAG, "onDragOver   AppContentView x = " + dragObject.x + " y = " + dragObject.y);
        if (mDraggingMode) {
            if (sortingOntheWay
                    || lockCheckAfterScroll
                    || !sortAnimators.isEmpty()) {
                return;
            }

            mHandler.removeMessages(MSG_CHECK_TARGET);
            mHandler.removeMessages(MSG_SORT);
            Message msg = Message.obtain();
            msg.what = MSG_CHECK_TARGET;
            msg.obj = dragObject;
            msg.arg1 = getChildIndex(mDraggingItemOrigin);
            mHandler.sendMessageDelayed(msg,
                    CHECK_TARGET_DELAY);
        }
    }

    @Override
    public void onDragExit(XDragObject dragObject) {
        if (DEBUG_DRAG) Log.d(TAG_DEBUG_DRAG, "onDragExit   AppContentView");
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
        outRect.set(0, 0, (int) getWidth(), (int) getHeight());
    }

    @Override
    public void getLocationInDragLayer(int[] loc) {
        ((XLauncher) mContext.getContext()).getDragLayer().getLocationInDragLayer(this, loc);
    }

    @Override
    public int getLeft() {
        return (int) getRelativeX();
    }

    @Override
    public int getTop() {
        return (int) getRelativeY();
    }

    @Override
    public void onDropCompleted(DrawableItem target, XDragObject d, boolean success) {
        if (DEBUG_DRAG) Log.d(TAG_DEBUG_DRAG, "onDropCompleted   AppContentView");
        
        /*RK_ID:RK_BUG_10896,zhangdxa 2013-4-10. S***/
        mDraggingMode = false;
        mLongPressed = false;
        /*RK_ID:RK_BUG_10896,zhangdxa 2013-4-10. E***/
        

        if( mDraggingItemOrigin != null ){
            mDraggingItemOrigin.setVisibility( true );
        }

        if (!idleTaskList.isEmpty()) {
            mHandler.removeMessages(MSG_RUN_IDLE_TASK);
            mHandler.sendEmptyMessage(MSG_RUN_IDLE_TASK);
        }
    }
    /*** RK_ID: DRAG. AUT: zhaoxy . DATE: 2013-02-19 . END ***/
    
    public int getWidthGap(){
        return mWidthGap;
    }   
    
    public void clearNewBgAndSetNum(String componentName, int num)
    {
        int index = findAppByComponent(infos, componentName);
                
        XIconView icon = (XIconView)getChildAt(index);
        
		if( icon != null) {
	        showAppFlag(icon, num);
		}
                    
        invalidate();
    }
    
    /**
     * Debug only
     */
    private void dumpChildLayout() {
        Log.d("dumpChildLayout", "================= dumpChildLayout ============");
        for (int i = 0; i < getChildCount(); i++) {
            StringBuffer toprint = new StringBuffer();
            XIconView child = (XIconView) getChildAt(i);
            if (child != null) {
                toprint.append(i).append(". ").append(" title = ").append(child.getTag().title).append(child.dumpLayoutInfo());
                Log.d("dumpChildLayout", toprint.toString());
            }
        }
    }
    
    public void showAppFlag(String pkgName, int num) {
        int index = findAppByComponent(infos, pkgName);                        
        XIconView icon = (XIconView)getChildAt(index);
        if (icon == null)
        {
            return;
        }
        
        if (icon.getTag().mNewAdd != 1)
        {
            showAppFlag(icon, num);
        }        
    }
    
    public void showAppFlag(XIconView icon, int num) {
        ApplicationInfo info = icon.getTag();
        info.updateInfo(num);
        icon.showTipForNewAdded(info.mNewString);
                    
        invalidate();
    }
    
    public void showAppFlag(XIconView icon, String num) {
        ApplicationInfo info = icon.getTag();
        info.updateInfo(num);
        icon.showTipForNewAdded(info.mNewString);
                    
        invalidate();
    }
    
    private int findAppByComponent(List<ApplicationInfo> list, String pkgName) {
        int length = list.size();
        for (int i = 0; i < length; ++i) {
            ApplicationInfo info = list.get(i);
            if (info.intent.getComponent().flattenToString().equals(pkgName)) {
                return i;
            }
        }
        return -1;
    }
}
