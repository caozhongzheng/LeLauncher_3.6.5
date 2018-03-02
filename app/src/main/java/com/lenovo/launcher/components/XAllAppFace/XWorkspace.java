package com.lenovo.launcher.components.XAllAppFace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.animation.TimeInterpolator;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region.Op;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lenovo.algorithm.reorder.All_Z_Reorder;
import com.lenovo.algorithm.reorder.Reorder;
import com.lenovo.algorithm.reorder.Reorder.SwapItem;
import com.lenovo.algorithm.reorder.Reorder.Type;
import com.lenovo.algorithm.reorder.Z_Reorder;
import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.XDragController.XDragListener;
import com.lenovo.launcher.components.XAllAppFace.XFolderIcon.FolderRingAnimator;
import com.lenovo.launcher.components.XAllAppFace.XPagedView.PageSwitchListener;
import com.lenovo.launcher.components.XAllAppFace.slimengine.BaseDrawableGroup;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;
import com.lenovo.launcher.components.XAllAppFace.utilities.Utilities;
import com.lenovo.launcher2.commoninterface.Alarm;
import com.lenovo.launcher2.commoninterface.ApplicationInfo;
import com.lenovo.launcher2.commoninterface.FolderInfo;
import com.lenovo.launcher2.commoninterface.HolographicOutlineHelper;
import com.lenovo.launcher2.commoninterface.IconCache;
import com.lenovo.launcher2.commoninterface.ItemInfo;
import com.lenovo.launcher2.commoninterface.LauncherAppWidgetInfo;
import com.lenovo.launcher2.commoninterface.LauncherService;
import com.lenovo.launcher2.commoninterface.LauncherSettings;
import com.lenovo.launcher2.commoninterface.LenovoWidgetViewInfo;
import com.lenovo.launcher2.commoninterface.OnAlarmListener;
import com.lenovo.launcher2.commoninterface.ShortcutInfo;
import com.lenovo.launcher2.commonui.LauncherAppWidgetHostView;
import com.lenovo.launcher2.customizer.Debug.R5;
import com.lenovo.launcher2.customizer.SettingsValue;

public class XWorkspace extends BaseDrawableGroup implements XScrollDropTarget, XDragSource, XDragListener {
    public enum State { NORMAL, SPRING_LOADED, SMALL, CUSTOM, SCR_MGR };
    private State mState = State.NORMAL;
    private XWallpaperPagedView xPagedView;
    XContext mXContext;
    
    private XScrollTextView mPageTextView;
    private int mPageTextViewWidth;
    
    private XPagedViewIndicator xPageIndicator;
    
    private int mDefaultPage;
    
    private int mCountX = 4;
    private int mCountY = 5; 
    private int mPage = 9;
    public Toast mToast =null;
       
//    private final int[] mTmpXY = new int[2];
    /**
     * Target drop area calculated during last acceptDrop call.
     */
    private int[] mTargetCell = new int[2];
    private final int[] mTmpPoint = new int[2];
    private final PointF mTmpPointF = new PointF();
    private int mCellWidth;
    private int mCellHeight;
    private int mWidthGap;
    private int mHeightGap;

    private final WallpaperManager mWallpaperManager;
    private final int[] mTempPoint = new int[2];
    private int phoneindex =-1;
    
    /*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-6-27. */
    private float mPagedViewGlobalY2;
    private float mPagedViewGlobalX2;
    /*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-6-27. */

	public XWorkspace(XContext context) {
		super(context);
		mXContext = context;
		mWallpaperManager = WallpaperManager.getInstance(mXContext.getContext());
	}
	
	public XWorkspace(XContext context, RectF region) {
        super(context);
                
        mXContext = context;
        phoneindex = mXContext.getContext().getResources().getInteger(R.integer.config_machine_type);
        mWallpaperManager = WallpaperManager.getInstance(mXContext.getContext());
        xPagedView = new XWallpaperPagedView(context, new RectF(region));
        xPagedView.setStageEnabled(true);
        xPagedView.setDeviceType(phoneindex);
        xPagedView.setScrollBackEnable(true);
        
        xPageIndicator = new XPagedViewIndicator(getXContext(), new RectF(0, 0, 0, 0));
        xPageIndicator.setSingleIndicatorVisible(false);
        xPagedView.addPageSwitchListener(xPageIndicator);
        xPageIndicator.setPagedView(xPagedView);
        xPageIndicator.setEnterEnabel();
        
        mPageTextViewWidth = mContext.getResources().getDimensionPixelSize(R.dimen.xpage_text_width);
        mPageTextView = new XScrollTextView(getXContext(), "",new RectF(0, 0, mPageTextViewWidth, mPageTextViewWidth));
        mPageTextView.setVisibility(false);
//        mPageTextView.enableCache();
        Drawable d = mContext.getResources().getDrawable(R.drawable.xpage_text_background);
        mPageTextView.setBackgroundDrawable(d);
        float textSize = (float)mPageTextViewWidth*3f/4f;
        mPageTextView.setTextSize(textSize);
        xPageIndicator.setTextView(mPageTextView);
        
        resize(region);
    }
	
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
	    onWallpaperTap(e);
	    return super.onSingleTapUp(e);
	}

	protected void onWallpaperTap(MotionEvent ev) {
        final int[] position = mTempPoint;

        int pointerIndex = ev.getActionIndex();
        position[0] = (int) ev.getX(pointerIndex);
        position[1] = (int) ev.getY(pointerIndex);

        mWallpaperManager.sendWallpaperCommand(mXContext.getWindowToken(),
                ev.getAction() == MotionEvent.ACTION_UP
                        ? WallpaperManager.COMMAND_TAP : WallpaperManager.COMMAND_SECONDARY_TAP,
                position[0], position[1], 0, null);
    }

	@Override
	public void resize(RectF rect) {
	    /*** fixbug LELAUNCHER-475. AUT: zhaoxy. DATE: 2013-10-21 . START***/
	    XShortcutIconView.dimenDirty = true;
	    /*** fixbug LELAUNCHER-475. AUT: zhaoxy. DATE: 2013-10-21 . END***/
		super.resize(rect);
//        int hotseat_height = mXContext.getContext().getResources().getDimensionPixelSize(
//                R.dimen.button_bar_height_plus_padding);

		//add for quick drag mode by sunzq3, begin;
		int homePointMarginTop = 0;
		int homePointMarginBottom = getXContext().getResources().getDimensionPixelOffset(R.dimen.home_point_margin_bottom);
		//add for quick drag mode by sunzq3, end;
		
		if( xPageIndicator != null ){
			//add for quick drag mode by sunzq3, begin;
			int homePointHeight = xPageIndicator.getTouchHomePointHeight();
			//add for quick drag mode by sunzq3, end;
			//modify for quick drag mode by sunzq3, begin;
			xPageIndicator.resize(new RectF(0, 0, getWidth(), homePointHeight));
			xPageIndicator.setRelativeY( rect.height() - homePointHeight - homePointMarginBottom);
            RectF rectf = null;
            float pageHeight = rect.height()
	                - homePointHeight - homePointMarginTop - homePointMarginBottom;
            

            if(phoneindex!=-1){
              //modify for quick drag mode by sunzq3, end;
            	if( getXContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ){
            		xPagedView.setPageGapEnable( false );
            	}else{
            		xPagedView.setPageGapEnable( true );
            	}
            	
    			int left = getXContext().getResources().getDimensionPixelOffset(R.dimen.workspace_pageview_left_padding);
                int top =  getXContext().getResources().getDimensionPixelOffset(R.dimen.workspace_pageview_top_padding);
//                int height =  getXContext().getResources().getDimensionPixelOffset(R.dimen.workspace_screen_height);
//                int width =  getXContext().getResources().getDimensionPixelOffset(R.dimen.workspace_screen_width);
                left = 0;
            	rectf =  new RectF(left,top,rect.width()-left,pageHeight);
            	//modify for quick drag mode by sunzq3, begin;
            	//xPagedView.setExtraTouchBounds(new RectF(0, 0, getWidth()-xPageIndicator.getIndicators().homePointWidth,getHeight()));
            	//xPagedView.desireTouchEvent(true);
            	//modify for quick drag mode by sunzq3, end;
//            	xPageIndicator.setRelativeY( rectf.bottom);
            }else{
//            	xPageIndicator.setRelativeY( rect.height() - xPageIndicator.getIndicators().homePointHeight - homePointMarginTop);
            	
            	rectf =new RectF(0, 0, rect.width(), pageHeight);
//            	android.util.Log.i("dooba", "+++++++++++ homePointH: " + xPageIndicator.getIndicators().homePointHeight + "======>page Height: " + pageHeight);
            }
            
            // update new gap method
            int paddingPhone = mXContext.getResources().getDimensionPixelSize( R.dimen.workspace_divider_padding_left);
            int paddingPad = getXContext().getResources().getDimensionPixelOffset(R.dimen.workspace_pageview_left_padding);
            if( phoneindex != -1 ){
            	xPagedView.setPageGap( paddingPad * 2 );     	
            }else{
            	xPagedView.setPageGap( paddingPhone * 2 );
            }
            
            Log.d("PAD0516","rect.width()=++++i resize XPageview++++++="+rectf);
                    errorStatus = -1;
                    xPagedView.resize(rectf);
                    //added by yumina for check the current status
                    if(getCurrentOrientation() == Configuration.ORIENTATION_PORTRAIT){
                        if(rectf.width() > rectf.height()){
                            Log.d("PAD0516","rect.width()= port error need to do++++=");
                            errorStatus = 0;
                        }
                    }else{
                        if(rectf.width() < rectf.height()){
                             Log.d("PAD0516","rect.width()= land error need to do++++=");
                            errorStatus = 1; 
                        }
                    }

//		    xPagedView.resize(new RectF(0, 0, rect.width(), rect.height()
//                - xPageIndicator.getIndicators().homePointHeight));
		}
		

        float left = (xPagedView.getWidth() - mPageTextViewWidth ) /2 ;
        float top = (xPagedView.getHeight() - mPageTextViewWidth ) /2 ;
        
        mPageTextView.resize(new RectF(left, top, left + mPageTextViewWidth, top + mPageTextViewWidth));
		
        mCellWidth = xPagedView.getCellWidth();
        mCellHeight = xPagedView.getCellHeight();
//        mWidthGap = xPagedView.getWidthGap();
//        mHeightGap = xPagedView.getHeightGap();  
        mWidthGap = 0;
        mHeightGap = 0;
        
        invalidate();
    }
    private int errorStatus = -1;
    public int getOrientationStatus(){
        return errorStatus;
    }

    public int getCurrentOrientation(){
        return mXContext.getContext().getResources().getConfiguration().orientation;
    }
    public int getCurrentPage() {
        return xPagedView.getCurrentPage();
    }

    /*** RK_ID: APPS_CATEGORY. AUT: zhaoxy . DATE: 2013-03-21 . START ***/
    public int getCellCountPreScreen() {
        return mCountX * mCountY;
    }

    public int getPageCount() {
        return xPagedView.getPageCount();
    }

    public int getPageCellCountX() {
        return xPagedView.getCellCountX();
    }

    public int getPageCellCountY() {
        return xPagedView.getCellCountY();
    }
    /*** RK_ID: APPS_CATEGORY. AUT: zhaoxy . DATE: 2013-03-21 . END ***/

    public State getState() {
        return mState;
    }
    
    public void setWorkspaceState( State state){
    	
    	if (xPagedView != null && state == State.NORMAL) {
    		xPagedView.bringStageToFront();
    	}
    	
    	mState = state;
    }
        
    void addInScreen(XViewContainer child, LauncherAppWidgetInfo info) {

        removePagedViewItem( info );
        XPagedViewItem itemToAdd = new XPagedViewItem(mXContext, child, info);
	
        xPagedView.addPagedViewItem(itemToAdd);
        Context c = mXContext.getContext();
        if (c instanceof XLauncher) {
            XLauncher xlauncher = (XLauncher) c;
            child.setTag(info);
//            child.setTag(child.getParasiteView().getTag());
            child.setOnLongClickListener(xlauncher);
        }
        
        /*** RK_ID: Occupied.  AUT: zhaoxy . DATE: 2013-01-30 . START***/
//        xPagedView.markCellsForView(x, y, spanX, spanY, screen, true);
        /*** RK_ID: Occupied.  AUT: zhaoxy . DATE: 2013-01-30 . END***/
    }   
        
    public void init() {
				
		SharedPreferences preferrences =
        		mXContext.getContext().getSharedPreferences("DefaultPage", Context.MODE_PRIVATE);
        mDefaultPage = preferrences.getInt("DefaultPage", 1);
        
        mPage = LauncherService.getInstance().mScreenCount;
        mCountX = XLauncherModel.getCellCountX();
        mCountY = XLauncherModel.getCellCountY();
        
        Resources res = mXContext.getContext().getResources();
                       
        final int widthGap = res.getDimensionPixelOffset(R.dimen.workspace_width_gap);
        final int heightGap = res.getDimensionPixelOffset(R.dimen.workspace_height_gap);
//        final int width = res.getDimensionPixelOffset(R.dimen.workspace_cell_width);
 //       final int height = res.getDimensionPixelOffset(R.dimen.workspace_cell_height);     
        
        mMaxDistanceForFolderCreation = (mMaxDistanceForFolderCreationFactor * res.getDimensionPixelSize(R.dimen.app_icon_size));

        xPagedView.setWidthGap(widthGap);
        xPagedView.setHeightGap(heightGap);
        
        mXPagedViewListener  = new XPagedViewListener();
        xPagedView.addPageSwitchListener(mXPagedViewListener);
        
        mTmpOccupied = new boolean[mCountX][mCountY];
        mSavedOccupied = new boolean[mCountX][mCountY];
        
        // use new gap method
        int paddingPhone = mXContext.getResources().getDimensionPixelSize( R.dimen.workspace_divider_padding_left);
        int paddingPad = getXContext().getResources().getDimensionPixelOffset(R.dimen.workspace_pageview_left_padding);
        if( phoneindex != -1 ){
        	xPagedView.setPageGap( paddingPad * 2 );        	
        }else{
        	xPagedView.setPageGap( paddingPhone * 2 );
        }
        xPagedView.setup(mPage, mCountX, mCountY); 
        xPagedView.setCurrentPage(mDefaultPage);
                
        mCellWidth = xPagedView.getCellWidth();
        mCellHeight = xPagedView.getCellHeight();
//        mWidthGap = xPagedView.getWidthGap();
//        mHeightGap = xPagedView.getHeightGap();
        mWidthGap = 0;
        mHeightGap = 0;
        mDragCell[0] = mDragCell[1] = -1;
        addItem(xPagedView);
        
        addItem(xPageIndicator);
        
        addItem(mPageTextView);
        
        initOutlineAnims();
	}
    
    public void setCurrentPage(int page) {
    	xPagedView.setCurrentPage(page);
    }
    
    public void addInScreen(final ShortcutInfo info, IconCache iconCache, boolean insert) {
//    	xPagedView.removePageItem(info);
        XPagedViewItem itemToRemove = xPagedView.findPageItemAt(info.screen, info.cellX, info.cellY);
        R5.echo("itemToRemove = " + itemToRemove);
        xPagedView.removePagedViewItem(itemToRemove, false, false);

        XPagedViewItem itemToAdd = getPagedViewItem(info);
        xPagedView.addPagedViewItem(itemToAdd);
        
/*        final int iconsize = SettingsValue.getIconSizeValueNew(getXContext()
				.getContext());
        DrawableItem drawableTarget = itemToAdd.getDrawingTarget();
        if (drawableTarget instanceof XShortcutIconView) {
			final XShortcutIconView view = (XShortcutIconView) drawableTarget;
			XIconDrawable iconDrawable = view
					.getIconDrawable();
			Utilities.iconSizeChange(iconDrawable,iconsize);
			iconDrawable.setShowWorkspaceShadow(true);
		}*/
       DrawableItem drawableTarget = itemToAdd.getDrawingTarget();
		if (drawableTarget instanceof XShortcutIconView) {
			final XShortcutIconView view = (XShortcutIconView) drawableTarget;
			XIconDrawable iconDrawable = view
					.getIconDrawable();
			iconDrawable.setShowWorkspaceShadow(true,getXContext().getContext());
			if (Utilities.getShowMissedNum(info, Utilities.HAS_NEW_MSG,
					Utilities.missedNum[Utilities.HAS_NEW_MSG]) > 0) {// 有未读短信
				view.showTipForNewAdded(Utilities.missedNum[Utilities.HAS_NEW_MSG]);
			} else if (Utilities.getShowMissedNum(info, Utilities.HAS_NEW_CALL,
					Utilities.missedNum[Utilities.HAS_NEW_CALL]) > 0) {// 有未接来电
				view.showTipForNewAdded(Utilities.missedNum[Utilities.HAS_NEW_CALL]);
			} else {
				view.dismissTip();
			}
		}
        /*** RK_ID: Occupied.  AUT: zhaoxy . DATE: 2013-01-30 . START***/
//        xPagedView.markCellsForView(info.cellX, info.cellY, info.spanX, info.spanY, info.screen, true);
        /*** RK_ID: Occupied.  AUT: zhaoxy . DATE: 2013-01-30 . END***/
    }
    
    public XPagedViewItem getPagedViewItem(final ShortcutInfo info)
    {
        final float screenWidth = getWidth();
        float left = mPaddingLeft + (mCellWidth + mWidthGap) * info.cellX - screenWidth;
        float top = mPaddingTop + (mCellHeight + mHeightGap) * info.cellY;
        RectF rect = new RectF(left, top, left + mCellWidth, top + mCellHeight);
        
        XShortcutIconView iconD = new XShortcutIconView(info, rect, mXContext);
        final XDragLayer dragLayer = ((XLauncherView) getXContext()).getDragLayer();
        //iconD.setOnClickListener(new OnClickListener() {
        OnClickListener clicker = new OnClickListener(){ 

            @Override
            public void onClick(DrawableItem item) {
                /* RK_ID: RK_SHORTCUT . AUT: liuli1 . DATE: 2012-02-28 . START */
                if (filterLeLauncherShortcut(mXContext.getContext(), info.intent)) {
                    return;
                }
                /* RK_ID: RK_SHORTCUT . AUT: liuli1 . DATE: 2012-02-28 . END */

                int[] pos = new int[2];
                final Intent intent = info.intent;
                //test by liuli 2013-08-06
                Log.i(TAG, "pos[0] === " + pos[0] + "     pos[1]====" + pos[1]);

                // fix JIRA bug : LeLauncher-1057 modified for touch target.
				try {
					if (item instanceof XPressedTextView) {
						XShortcutIconView parent = (XShortcutIconView) item
								.getParent();
						if (parent != null) {
							XPressIconDrawable iconDrawable = (XPressIconDrawable) parent
									.getIconDrawable();
							item = iconDrawable;
							dragLayer.getLocationInDragLayer(iconDrawable, pos);
						}
					} else {
						dragLayer.getLocationInDragLayer(item, pos);
					}
				} catch (Exception e) {
					dragLayer.getLocationInDragLayer(item, pos);
				}
                
                intent.setSourceBounds(new Rect(pos[0], pos[1], pos[0] + (int) item.getWidth(),
                		pos[1] + (int) item.getHeight()));
                int windowTop = ((XLauncherView) getXContext()).getWindowTop();
                pos[1] += windowTop;
                //test by dining 2013-07-26
                final int[] measured = new int[2];
                measured[0] = (int) item.getWidth();
                measured[1] = (int) item.getHeight();
                
                if (mXContext.getContext() instanceof XLauncher) {
//                    mXContext.post(new Runnable() {
//                        public void run() {
                        	/*RK_ID:RK_SD_APPS zhangdxa 2013-4-15. S***/
                        	if( (info.itemType == LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT) &&
                        		(info.uri != null )){
                        		Uri uri = Uri.parse(info.uri);
                        		Intent intentView = new Intent(Intent.ACTION_VIEW, uri);
                        		((XLauncher) mXContext.getContext()).startActivitySafely(intentView, null);
//                        		((XLauncher) getXContext().getContext()).getModel().getUsageStatsMonitor().add(intentView);
                        	}else{
                        		//test by dining 2013-07-26
                        		//for snooby using the ext-function to start activity
                        		boolean bResult = false;
                        		bResult = ((XLauncher) mXContext.getContext()).startActivitySafely(info.intent, measured, null);
                        		if(!bResult){
                                    ((XLauncher) mXContext.getContext()).startActivitySafely(info.intent, null);
                        		}
                        		//end test
//                                ((XLauncher) getXContext().getContext()).getModel().getUsageStatsMonitor().add(info.intent);
                        	}
                        	/*RK_ID:RK_SD_APPS zhangdxa 2013-4-15. E***/
//                        }
//                    });
                    
                    if (info.mNewAdd == 1)
                    {
                        ((XLauncher)mXContext.getContext()).clearAndShowNewBg(info.intent.getComponent().flattenToString());
                    }
                }
            }
            
        };
        //added by yumina for the hot rect too big in the pad 2013-07-12
        iconD.getTextView().setOnClickListener(clicker);
        iconD.getIconDrawable().setOnClickListener(clicker);

        Context c = mXContext.getContext();
        if (c instanceof XLauncher) {
            XLauncher xlauncher = (XLauncher) c;
            iconD.setOnLongClickListener(xlauncher);
        }
         
        return new XPagedViewItem(mXContext, iconD, info);
    }
    
    public void addInScreen(XViewContainer container, ItemInfo info) {
    	//for widget display issue
    	xPagedView.removePageItem(info);
    	
    	XPagedViewItem itemToAdd = new XPagedViewItem(mXContext, container, info);

        xPagedView.addPagedViewItem(itemToAdd);

        Context c = mXContext.getContext();
        if (c instanceof XLauncher) {
            XLauncher xlauncher = (XLauncher) c;
            container.setTag(info);
            container.setOnLongClickListener(xlauncher);
        }
        /*** RK_ID: Occupied.  AUT: zhaoxy . DATE: 2013-01-30 . START***/
//        xPagedView.markCellsForView(info.cellX, info.cellY, info.spanX, info.spanY, info.screen, true);
        /*** RK_ID: Occupied.  AUT: zhaoxy . DATE: 2013-01-30 . END***/
    }
    
    /*** RK_ID: XFOLDER.  AUT: zhaoxy . DATE: 2013-01-22 . START***/
    public XPagedViewItem addInScreen(FolderInfo info) {
    	xPagedView.removePageItem(info);
    	
        XFolderIcon newFolder = XFolderIcon.obtain(mXContext, (XLauncher) mXContext.getContext(), info);
        newFolder.resize(new RectF(0, 0, mCellWidth, mCellHeight));
        Context c = mXContext.getContext();
        if (c instanceof XLauncher) {
            XLauncher xlauncher = (XLauncher) c;
            newFolder.setOnLongClickListener(xlauncher);
        }
        XPagedViewItem itemToAdd = new XPagedViewItem(mXContext, newFolder, info);
        xPagedView.addPagedViewItem(itemToAdd);
        /*** RK_ID: Occupied.  AUT: zhaoxy . DATE: 2013-01-30 . START***/
//        xPagedView.markCellsForView(info.cellX, info.cellY, 1, 1, info.screen, true);
        /*** RK_ID: Occupied.  AUT: zhaoxy . DATE: 2013-01-30 . END***/
        Log.d("XFOLDER", newFolder.toString());
        
        final int iconsize = SettingsValue.getIconSizeValueNew(getXContext()
				.getContext());
        DrawableItem drawableTarget = itemToAdd.getDrawingTarget();
        if (drawableTarget instanceof XFolderIcon) {
        	final XFolderIcon view = (XFolderIcon) drawableTarget;
//        	DrawableItem iconDrawableFolderIcon = view
//					.getIconDrawable();
//			Utilities.iconSizeChange(iconDrawableFolderIcon,iconsize);
			//把folder里面的icon缩小
			XFolder folder =  view.mFolder;
			Utilities.updateIconSizeinWorkspace(folder.getItemIDMap(), iconsize);
			//newFolder.setShowWorkspaceShadow(true,getXContext().getContext());

        }
        drawableTarget.invalidate();
        return itemToAdd;
    }
    /*** RK_ID: XFOLDER.  AUT: zhaoxy . DATE: 2013-01-22 . END***/
        
    public XPagedView getPagedView() {
    	return xPagedView;
    }
    
    public void setDefaultPage(int defaultPage){
    	this.mDefaultPage = defaultPage;
    	SharedPreferences preferrences =
                mXContext.getContext().getSharedPreferences("DefaultPage", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferrences.edit();
            editor.putInt("DefaultPage",defaultPage);
            editor.commit();
        //add by zhanggx1.s
        ((XLauncher)mXContext.getContext()).autoReorder();
        //add by zhanggx1.e
    }
    
    public int getDefaultPage(){
    	return this.mDefaultPage;
    }
    
    int[] findNearestVacantArea(int screen, int pixelX, int pixelY,
            int spanX, int spanY, ItemInfo ignoreView, int[] recycle) {
        return findNearestArea(
                screen, pixelX, pixelY, spanX, spanY, ignoreView, true, recycle);
    }
    
    int[] findNearestArea(int screen, int pixelX, int pixelY, int spanX, int spanY, ItemInfo ignoreView,
            boolean ignoreOccupied, int[] result) {
        return xPagedView.findNearestArea(screen, pixelX, pixelY, spanX, spanY,
                spanX, spanY, ignoreView, ignoreOccupied, result, null);
    }            
        
    public void setLoop(boolean isLoop) {
		final XPagedView appView = xPagedView;
		if (appView != null) {
			appView.setLoop(isLoop);
		}
	}
    
    public void setIconTextBackgroundEnable(boolean enable) {
		final XPagedView appView = xPagedView;
		if (appView != null) {
			appView.setIconTextBackgroundEnable(enable);
		}
	}

    public void updateSlideValue() {
        if (xPagedView != null) {
            xPagedView.updateSlideValue();
        }
    }

    public int findRectTopByCellY(DrawableItem item, int cellY) {
        XDragLayer dragLayer = ((XLauncherView) getXContext()).getDragLayer();
        int[] coord = new int[2];
        dragLayer.getDescendantCoordRelativeToSelf(item, coord);
        return coord[1];
//        return (int) (localRect.top + mPaddingTop + (mCellHeight + mHeightGap) * cellY + xPagedView.localRect.top);
    }

    public int findRectLeftByCellX(DrawableItem item, int cellX) {
        XDragLayer dragLayer = ((XLauncherView) getXContext()).getDragLayer();
        int[] coord = new int[2];
        dragLayer.getDescendantCoordRelativeToSelf(item, coord);
        return coord[0];
//        final float screenWidth = getWidth();
//        return (int) (mPaddingLeft + (mCellWidth + mWidthGap) * cellX);
    }

    public void removePagedViewItem(ItemInfo item) {
        if (xPagedView != null) {
            xPagedView.removePageItem(item);
            xPagedView.invalidate();
        }
    }

    DrawableItem getDragView() {
        return mDragView != null ? mDragView.getDrawingTarget() : null;
    }
    
    //move item to position
    public boolean moveItemToPosition(ItemInfo itemInfo, ItemInfo newInfo){
    	XPagedViewItem itemToMove = xPagedView.findPageItemAt(itemInfo.screen, itemInfo.cellX, itemInfo.cellY);
    	if(itemToMove == null){
    		return false;
    	}
    	
    	if( itemInfo instanceof LauncherAppWidgetInfo){
    		android.util.Log.i("tag", "update tag : " 
    				+ itemToMove + " from : " +
    				itemToMove.getInfo().cellX + " , " + itemToMove.getInfo().cellY + " , "
    				+ itemToMove.getInfo().screen +" : to "+ newInfo.cellX +" "+ newInfo.cellY +" "+ newInfo.screen);
//    		Object tag = ((LauncherAppWidgetInfo)newInfo).hostView.getTag();
//    		if( tag != null ){
//                itemToMove.getDrawingTarget().setOnLongClickListener(
//                        (XLauncher) (mXContext.getContext()));
//                itemToMove.getDrawingTarget().setTag(tag);
//    		}
    		itemToMove.getDrawingTarget().setOnLongClickListener(
                  (XLauncher) (mXContext.getContext()));
    	}
    	itemToMove.getDrawingTarget().setTag(newInfo);
    	
    	
    	return xPagedView.moveItemToPosition(itemToMove, newInfo.cellX, newInfo.cellY, newInfo.screen, 0, 0, null);
    }
//    public boolean moveItemToPosition(ItemInfo itemInfo, ItemInfo newInfo){
//    	XPagedViewItem itemToMove = xPagedView.findPageItemAt(itemInfo.screen, itemInfo.cellX, itemInfo.cellY);
//    	if(itemToMove == null){
//    		return false;
//    	}
//    	
//    	if( itemInfo instanceof LauncherAppWidgetInfo ){
//    		android.util.Log.i("tag", "update tag : " 
//    				+ itemToMove + " from : " +
//    				itemToMove.getInfo().cellX + " , " + itemToMove.getInfo().cellY + " , "
//    				+ itemToMove.getInfo().screen +" : to "+ newInfo.cellX +" "+ newInfo.cellY +" "+ newInfo.screen);
////    		Object tag = ((LauncherAppWidgetInfo)newInfo).hostView.getTag();
////    		if( tag != null ){
//                itemToMove.getDrawingTarget().setOnLongClickListener(
//                        (XLauncher) (mXContext.getContext()));
////                itemToMove.getDrawingTarget().setTag(tag);
////    		}
//    	}
//    	itemToMove.getDrawingTarget().setTag(newInfo);
//    	
//    	
//    	return xPagedView.moveItemToLocation(itemToMove, newInfo.cellX, newInfo.cellY, newInfo.screen, 0, 0, null);
//    }

    public void refreshIconStyle(IconCache iconCache, boolean onlyBitmap) {
        if (xPagedView != null) {
            xPagedView.refreshIconCache(iconCache, onlyBitmap);
            xPagedView.invalidate();
            this.invalidate();
        }
    }

    // for lelauncher shortcut, effect setting and icon style
    public boolean filterLeLauncherShortcut(Context context, Intent intent) {
        if(intent != null){
            if(SettingsValue.ACTION_WORKSPACE_EFFECT.equals(intent.getAction())){
                return changeWorkspaceEffect(context);
            }else if(SettingsValue.ACTION_WORKSPACE_APPS_CATEGORY.equals(intent.getAction())){
                final XLauncher launcher = ((XLauncher)mXContext.getContext());
                launcher.getMainView().post(new Runnable() {
                    
                    @Override
                    public void run() {
                        launcher.classifyAppsWithBehavior();
                        
                    }
                });
                return true;
                
            }
        }
        return false;
    }

    private boolean changeWorkspaceEffect(final Context context) {
        // change workspace slide effect
        String current = SettingsValue.getWorkspaceSlideValue(context);
        Resources res = context.getResources();
        final String[] effects = res.getStringArray(R.array.pref_slide_effect_choices);
        String[] effectValues = res.getStringArray(R.array.pref_slide_effect_values);

        // find the current effect index
        int i = 0;
        for (; i < effectValues.length; i++) {
            if (effectValues[i].equals(current))
                break;
        }

        // then change it to the next one
        final int next = (i + 1 < effectValues.length) ? i + 1 : 0;
        final String newEffect = effectValues[next];
        SettingsValue.setWorkspaceSlide(newEffect);
        updateSlideValue();
//        mCustomTabHost.notifyDataChanged(MenuInterface.TAG_EFFECT, null);

        // save profile to prefs
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
                        context).edit();
                // we hope not be restore this file.
                editor.putString(SettingsValue.PREF_WORKSPACE_SLIDE, newEffect);
                try {
                    editor.apply();
                } catch (AbstractMethodError unused) {
                    editor.commit();
                }
            }
        }, "ChangeEffectFromShortcut").start();

        final String s = context.getString(R.string.effect_shortcut);
        getXContext().post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(context, s + " " + effects[next], Toast.LENGTH_SHORT).show();
            }
        });
//        SettingsValue.checkShowToast(newEffect, context);
        return true;
    }

    public void setAllWidgetVisible(boolean visible)
    {
    	xPagedView.setStageVisibility( visible );

    	/*
        XPagedViewItem item;
        XViewContainer container;
        for (Long itemId : xPagedView.mItemIDMap.keySet()) {
            item =  xPagedView.mItemIDMap.get(itemId);
            if (item.getDrawingTarget() instanceof XViewContainer)
            {
                container = (XViewContainer)item.getDrawingTarget();
                if (!visible)
                {
                    container.manageVisibility(XViewContainer.VISIBILITY_SHOW_NONE);
                }
                else if (xPagedView.mCurrentPage == container.getMyScreen())
                {
                    container.manageVisibility(XViewContainer.VISIBILITY_SHOW_VIEW);
                }
             }
        }
                
    */}

    /*** RK_ID: XFOLDER. AUT: zhaoxy . DATE: 2013-01-22 . START ***/
    ArrayList<DrawableItem> getWorkspaceAndHotseatChildren() {
        return xPagedView.getSourceItems(null);
    }

    /**
     * @return The open folder on the current screen, or null if there is none
     */
    public XFolder getOpenFolder() {
        XDragLayer dragLayer = ((XLauncherView) getXContext()).getDragLayer();
        int count = dragLayer.getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            DrawableItem child = dragLayer.getChildAt(i);
            if (child instanceof XFolder) {
                XFolder folder = (XFolder) child;
                if (folder.getInfo().opened)
                    return folder;
            }
        }
        return null;
    }

    /*** RK_ID: APPS_CATEGORY. AUT: zhaoxy . DATE: 2013-03-21 . START ***/
    public XFolderIcon getFolderIconByTitle(String title) {
        if (title == null || title.equals("")) return null;
        ArrayList<DrawableItem> children = getWorkspaceAndHotseatChildren();
        for (DrawableItem item : children) {
            if (item instanceof XFolderIcon) {
                XFolderIcon f = (XFolderIcon) item;
                if (title.equals(f.mInfo.title)) {
                    return f;
                }
            }
        }
        if (mDragView != null) {
            DrawableItem item = mDragView.getDrawingTarget();
            if (item != null && item instanceof XFolderIcon) {
                XFolderIcon f = (XFolderIcon) item;
                if (title.equals(f.mInfo.title)) {
                    return f;
                }
            }
        }
        return null;
    }
    /*** RK_ID: APPS_CATEGORY. AUT: zhaoxy . DATE: 2013-03-21 . END ***/

    public XFolder getFolderForTag(Object tag) {
        ArrayList<DrawableItem> children = getWorkspaceAndHotseatChildren();
        for (DrawableItem item : children) {
            if (item instanceof XFolder) {
                XFolder f = (XFolder) item;
                if (f.getInfo() == tag && f.getInfo().opened) {
                    return f;
                }
            }
        }
        return null;
    }
    
    public DrawableItem getDrawableItemForTag(Object tag) {
        ArrayList<DrawableItem> children = getWorkspaceAndHotseatChildren();
        for (DrawableItem item : children) {
            if (item.getTag() == tag) {
                return item;
            }
        }
        return null;
    }
    /*** RK_ID: XFOLDER. AUT: zhaoxy . DATE: 2013-01-22 . END ***/
    
    void updateShortcutsThoroughly(ArrayList<ApplicationInfo> apps) {
        cleanDragData();
        
        if(xPageIndicator != null){
        	//modify for quick drag mode by sunzq3, begin;
    		xPageIndicator.updateTheme();
    		//modify for quick drag mode by sunzq3, end;
	    }
        
        resize(localRect);
        
    	int pageCnt = xPagedView.getPageCount();
    	int cellXCnt = xPagedView.getCellCountX();
    	int cellYCnt = xPagedView.getCellCountY();
    	
    	for (int screen = 0; screen < pageCnt; screen++) {
    		for (int cellX = 0; cellX < cellXCnt; cellX++) {
    			for (int cellY = 0; cellY < cellYCnt; cellY++) {
    				XPagedViewItem item = xPagedView.findPageItemAt(screen, cellX, cellY);
    				if (item == null) {
    					continue;
    				}
    				DrawableItem drawableTarget = item.getDrawingTarget();
    				if (drawableTarget == null
    						|| !(drawableTarget instanceof XShortcutIconView
    								|| drawableTarget instanceof XFolderIcon)) {
    					continue;
    				}
    				ItemInfo itemInfo = item.getInfo();
    				if (itemInfo == null) {
    					continue;
    				}
    				((XLauncher)mXContext.getContext()).reloadAnIcon(apps, itemInfo, drawableTarget);
    			}
    		}
    	}
    	
    	invalidate();
    }
    
    public void setWindowToken(IBinder windowToken){
        xPagedView.setWindowToken(windowToken);
    }
    
    XPagedViewListener mXPagedViewListener;
    public class XPagedViewListener implements PageSwitchListener {

        @Override
        public void onPageBeginMoving(int currentPage) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onPageEndMoving(int currentPage) {
            // TODO Auto-generated method stub
        	XLauncher.setScreen(currentPage);
        }

        @Override
        public void onPageSwitching(int from, int to, float percentage) {
            // TODO Auto-generated method stub
            XLauncher.setScreen(to);            
        }

        @Override
        public void onUpdatePage(int pageCount, int currentPage) {
            // TODO Auto-generated method stub
            
        }


    }

    @Override
    public void setVisibility(boolean visible) {
        if (!visible) {
            xPagedView.resetOffset();
        }
    	
    	xPagedView.setViewStageVisibility(visible);
    	xPagedView.setVisibility(visible);
    	super.setVisibility(visible);
    }
    
    public void addNewScreen() {
    	xPagedView.addNewScreen();
    	
    	SharedPreferences preferrences =
        		mXContext.getContext().getSharedPreferences("DefaultPage", Context.MODE_PRIVATE);
        mDefaultPage = preferrences.getInt("DefaultPage", 1);
        
        mPage = LauncherService.getInstance().mScreenCount;
    }
    
    public void draw(IDisplayProcess canvas, int page) {
        xPagedView.draw(canvas, page);
        xPagedView.resetChildrenMatrix(page);
    }
    
    @Override
    public void onDraw(IDisplayProcess c) {
        final Paint paint = mDragOutlinePaint;
//        for (int i = 0; i < mDragOutlines.length; i++) {
//            final float alpha = mDragOutlineAlphas[i];
//            if (alpha > 0) {
//                 /*RK_ID: Jelly_Bean . AUT: yumina . DATE: 2012-08-16 . S*/
//                final Rect r = mDragOutlines[i];
//                final Bitmap b = (Bitmap) mDragOutlineAnims[i].getTag();
//                paint.setAlpha((int)(alpha + .5f));
//                c.drawBitmap(b, null, r, paint);
////                R5.echo("draw alpha = " + alpha + "r = " + r.toString());
//                /*RK_ID: Jelly_Bean . AUT: yumina . DATE: 2012-08-16 . E*/
//            }
//        }

        if (mDragOutlinesNewDraw && mDragOutline != null && !mDragOutline.isRecycled()) {
//            paint.setAlpha((int)255);
//            mDragOutlinePaint.setColor(arg0)
            mDragOutlinePaint.setAlpha(128);
            c.drawBitmap(mDragOutline, null, mDragOutlinesNew, paint);
//            mDragOutlinePaint.setStyle(Paint.Style.STROKE);
//            mDragOutlinePaint.setStrokeWidth(4);
//            final int outlineColor = mXContext.getContext().getResources().getColor(android.R.color.holo_blue_light);
//            mDragOutlinePaint.setColor(outlineColor);
//            mDragOutlinePaint.setAlpha(128);
////            c.getCanvas().drawRect(mDragOutlinesNew, paint);
//            RectF r = new RectF(mDragOutlinesNew);
//            c.drawRoundRect(r, (float)20, (float)20, paint);
        }
        
     // The folder outer / inner ring image(s)
        for (int i = 0; i < mFolderOuterRings.size(); i++) {
            FolderRingAnimator fra = mFolderOuterRings.get(i);

            // Draw outer ring
            Drawable d = FolderRingAnimator.sSharedOuterRingDrawable;
            int width = (int) fra.getOuterRingSize();
            int height = width;
            cellToPoint(fra.mCellX, fra.mCellY, mTempLocation);
            
//            R5.echo("mTempLocation[0] = " + mTempLocation[0] + "mTempLocation[1] = " + mTempLocation[1]);

            int centerX = mTempLocation[0] + mCellWidth / 2;
            int centerY = mTempLocation[1] + fra.sPreviewSize / 2;

            c.save();
            /*RK_ID: RK_FOLDER_ICON . AUT: zhanggx1 . DATE: 2012-02-28 . S*/
//          canvas.translate(centerX - width / 2, centerY - width / 2);
            c.translate(centerX - width / 2, centerY - width / 2 + fra.sPreviewPaddingTop);
            /*RK_ID: RK_FOLDER_ICON . AUT: zhanggx1 . DATE: 2012-02-28 . E*/
            d.setBounds(0, 0, width, height);
            
//            R5.echo("sSharedOuterRingDrawable width = " + width);
            
            d.draw(c.getCanvas());
            c.restore();

            // Draw inner ring
            d = FolderRingAnimator.sSharedInnerRingDrawable;
            width = (int) fra.getInnerRingSize();
            height = width;

            
//            R5.echo("sSharedInnerRingDrawable width = " + width + "sSharedInnerRingDrawable = " + d
//            		+ "isVisible = " + d.isVisible() + "alpha = ");
            d.setAlpha(255);
            c.save();
            /*RK_ID: RK_FOLDER_ICON . AUT: zhanggx1 . DATE: 2012-02-28 . S*/
//            canvas.translate(centerX - width / 2, centerY - width / 2);
            c.translate(centerX - width / 2, centerY - width / 2 + fra.sPreviewPaddingTop);
            /*RK_ID: RK_FOLDER_ICON . AUT: zhanggx1 . DATE: 2012-02-28 . E*/
            d.setBounds(0, 0, width, height);
            d.draw(c.getCanvas());
            c.restore();
        }
        
        if (Float.compare(xPagedView.mOffsetX, 0) != 0)
        {
        	xPageIndicator.updateScrollingIndicatorPosition(xPagedView.mCurrentPage, xPagedView.mOffsetX);
        }
                
        super.onDraw(c);
        
		if (mPageTextView != null && mPageTextView.isVisible())
		{
			mPageTextView.scrollToPage(xPagedView.mCurrentPage, xPagedView.mOffsetX, xPagedView.getPageCount(), xPagedView.isLoop());
		}
    }
    
    public void setWidgetVisible(boolean visible)
    {
        XPagedViewItem item;
        XViewContainer container;
            
        Set<Map.Entry<Long, XPagedViewItem>> set = xPagedView.mItemIDMap.entrySet();
        for (Map.Entry<Long, XPagedViewItem> map : set) {
            if(map == null) {
                continue;
            }
            item = map.getValue();
            if (item.getDrawingTarget() instanceof XViewContainer)
            {
                container = (XViewContainer)item.getDrawingTarget();

                if((visible || getCurrentPage() == container.getMyScreen())&& container.getVisibleTag() != XViewContainer.VISIBILITY_SHOW_NONE_ALL)
                {
                    container.manageVisibilityDirect(XViewContainer.VISIBILITY_SHOW_SHADOW, null);
                }
//                else if ()
//                {
//                    R2.echo("getCurrentPage() = " + getCurrentPage() + "container  " + container.getMyScreen());
//                    container.manageVisibility(XViewContainer.VISIBILITY_SHOW_VIEW, null);
//                }
                else
                {
                    container.manageVisibility(XViewContainer.VISIBILITY_SHOW_NONE, null);
                }
             }
        }
                
    }
    
    public void removeScreenAt(int index) {
    	xPagedView.removeScreenAt(index);
//    	
//    	SharedPreferences preferrences =
//        		mXContext.getContext().getSharedPreferences("DefaultPage", Context.MODE_PRIVATE);
//        mDefaultPage = preferrences.getInt("DefaultPage", 1);
        
//        mPage = LauncherService.getInstance().mScreenCount;
    }
    
    public void moveToDefaultScreen(boolean animate) {
    	int page = mDefaultPage;
    	setCurrentPage(page);
    	/*
//        if (!isSmall()) {
            if (animate) {
            	
            	//fix bug 166236
//            	if(page != xPagedView.getCurrentPage()){
//            		xPagedView.snapToPage(page);
//            	}
            	
                // fix bug 170260
                setCurrentPage(page);
            } else {
                setCurrentPage(page);
            }
            */
//        }
    }

    private static final boolean DEBUG_DRAG = true;
    private static final String TAG_DEBUG_DRAG = "DEBUG_DRAG";
    private static final boolean DEBUG_REORDER = true;
    /**
     * ItemInfo for the cell that is currently being dragged
     */
    private ItemInfo mDragInfo;
    private XDragController mDragController;
    private XViewContainer mDraggingWidget;
    
    public void setup(XDragController mDragController) {
        mDragController.addDropTarget(this);
        mDragController.addDropTarget(xPageIndicator);
        this.mDragController = mDragController;
        mDragController.addDragListener(this);
    }
    
	void startDrag(XLauncher xLauncher, ItemInfo cellInfo) {
		if (cellInfo == null || xPagedView == null) {
	        return;
        }
		
		xPagedView.setEnableEffect( false );
		
		if (DEBUG_REORDER)R5.echo("startDrag screen = " + cellInfo.screen + "cellX = " + cellInfo.cellX + "celly = " + cellInfo.cellY);
		XPagedViewItem cell = xPagedView.findPageItemAt(cellInfo.screen, cellInfo.cellX, cellInfo.cellY);
		if (cell == null) {
            return;
        }
		DrawableItem target = cell.getDrawingTarget();
		if (target == null) {
	        return;
        }
        xLauncher.setLauncherWindowStatus(true);
		
		mDragInfo = cellInfo;
//		target.setVisibility(false);
		xPagedView.markCellsAsUnoccupiedForView(getCurrentPage(), cellInfo);
		boolean effectAnim = false;
        if (target instanceof XViewContainer) {
            XViewContainer widget = ((XViewContainer) target);
            widget.manageVisibility(XViewContainer.VISIBILITY_SHOW_NONE_ALL, null);
            mDraggingWidget = widget;
//            effectAnim = false;
        }
//        dragHasRemoved = false;
        mDragView = cell;
        if (target instanceof XShortcutIconView)
        {
            ((XShortcutIconView) target).getIconDrawable().setAlpha(1);
//            R5.echo("getDragOutlineByShortcutInfo rect = " + ((XShortcutIconView) target).localRect
//            		+ "iconD drawable rect = " + ((XShortcutIconView) target).mIconDrawable.localRect);
        } 
        
        beginDragShared(target, this, effectAnim);
        if (cellInfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_FOLDER
        		|| cellInfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET
        		|| cellInfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_COMMEND_APPWIDGET
        		|| cellInfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_LEOSWIDGET
        		|| cellInfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION) {
            xPagedView.removePagedViewItem(cell, false, false);
            /*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-6-27. S***/
            if(cellInfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET){
            	mPagedViewGlobalY2 = xPagedView.getGlobalY2();
            	mPagedViewGlobalX2 = xPagedView.getPageGap()/2;
            }
            /*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-6-27. E***/
        } else {
            xPagedView.removePagedViewItem(cell);
        }             
       
//		beginDragShared(target, this, effectAnim);
		saveTmpCell();		
	}
	
	public void beginDragShared(DrawableItem child, XDragSource source, boolean effectAnim) {
		((XLauncher) mXContext.getContext()).getDragLayer().getLocationInDragLayer(child, mTmpPoint);
		final int dragLayerX = mTmpPoint[0];
        final int dragLayerY = mTmpPoint[1];

        Bitmap b = null;
        if( child instanceof XViewContainer ){
        	b = ((XViewContainer)child).getSnapshot(1f, true); 
        }else{
        	b = child.getSnapshot( 1f );
        }
        if( b == null ){
        	return;
        }
        
        final Canvas canvas = new Canvas();
        mDragOutline = createDragOutline(b, canvas, 0, b.getWidth(), b.getHeight(), null);
        xPageIndicator.startEnterAnimation();
        mDragController.startDrag(b, dragLayerX, dragLayerY, source, child.getTag(), 0, null, null, effectAnim);
	}

    @Override
    public void onDropCompleted(DrawableItem target, XDragObject d, boolean success) {
    	
        if (DEBUG_DRAG) Log.d(TAG_DEBUG_DRAG, "Workspace onDropCompleted");
        if (DEBUG_REORDER)R5.echo("Workspace onDropCompleted  " + success);
        if (success) {
            boolean single = SettingsValue.getSingleLayerValue(getXContext().getContext());
            //test by liuli new deletebar
            if (single && target instanceof XDeleteDropTarget
                    && (/*mDragInfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION
                    || */mDragInfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_FOLDER)) {
                // this is uninstall option, do not remove here.
                Log.i("XWorkspace", "onDropCompleted~~ single is  ====" + single);

                if ((mDragInfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_FOLDER && ((FolderInfo) mDragInfo).contents
                        .size() > 0)
                        /*|| mDragInfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION*/) {
                    xPagedView.addPagedViewItem(mDragView);
                    DrawableItem last = mDragView.getDrawingTarget();
                    if (last != null) {
                        last.resetPressedState();
                    }

                    animDragviewIntoPosition(d.dragView, mDragInfo);

                }
            }

            XLauncher launcher = (XLauncher) getXContext().getContext();
            launcher.getDragLayer().showPendulumAnim(d, target);
        }
        else {
//            revertTempState();
        	if (mDragInfo != null) {
        	    cleanupReorder(true);
//        		XPagedViewItem cell = xPagedView.findPageItemAt(mDragInfo.screen, mDragInfo.cellX, mDragInfo.cellY);
//        		if (cell == null) {
//        			return;
//        		}
//        		DrawableItem last = cell.getDrawingTarget();
//        		if (last == null) {
//        			return;
//        		}
//        		last.setVisibility(true);
//        		xPagedView.markCellsAsOccupiedForView(getCurrentPage(), mDragInfo);
        	    if (xPagedView != null && mDragView != null)
        	    {
            	    xPagedView.addPagedViewItem(mDragView);
                    DrawableItem last = mDragView.getDrawingTarget();
                    if (last != null) {
                        last.resetPressedState();
                    }
        	    }
                if (target != null) {
                    animDragviewIntoPosition(d.dragView, mDragInfo);
                }
        		mDragInfo = null;
        	}
        }
        clearDragOutlines(true);
        mDragInfo = null;
        mDragView = null;
//        mReorderAlarm.cancelAlarm();
//        mReorderAlarm.setOnAlarmListener(null);
        
        xPageIndicator.startNormalAnimation();
      //add by zhanggx1 for reordering on 2013-11-13 . s
        if (target instanceof XHotseat
        		|| target instanceof XFolder) {
            ((XLauncher)getXContext().getContext()).autoReorder();
        }
        //add by zhanggx1 for reordering on 2013-11-13 . e
    }

    void animDragviewIntoPosition(final XDragView dragView, ItemInfo dragInfo) {
        final XLauncher xlauncher = (XLauncher) getXContext().getContext();
        final int screen = dragInfo.screen;

        final DrawableItem item = xPagedView.getChildAt(screen, dragInfo.cellX, dragInfo.cellY);
        if( ((XCell)item).getDrawingTarget() != null && ((XCell)item).getDrawingTarget() instanceof XViewContainer ){
        	// TODO XXX widget animation to position
        }else{
        	item.setVisibility(false);
        	xPagedView.noNeedGenerateBitmapCache(screen);
	        xlauncher.getDragLayer().animDropIntoPosition(dragView, item, screen, XWorkspace.this,
	                LauncherSettings.Favorites.CONTAINER_DESKTOP);
        }
    }

    @Override
    public boolean isDropEnabled() {
        return isVisible() && getAlpha() > .8f;
    }

    @Override
    public void onDrop(XDragObject dragObject) {
        if (DEBUG_DRAG) Log.d(TAG_DEBUG_DRAG, "Workspace onDrop");
        if (DEBUG_REORDER)R5.echo("onDrop");
        if (dragObject == null
        		|| dragObject.dragInfo == null
        		|| dragObject.dragInfo instanceof XScreenMngView.PreviewInfo) {
        	return;
        }        
        
        /*** fixbug 12707 . AUT: zhaoxy . DATE: 2013-05-21. START***/
        cancelcloseFolderDelayed();
        /*** fixbug 12707 . AUT: zhaoxy . DATE: 2013-05-21. END***/

        if (dragObject.dragSource != this) {
            final int[] touchXY = mTmpPoint;
            touchXY[0] = dragObject.x;
            touchXY[1] = dragObject.y;
            onDropExternal(touchXY, dragObject.dragInfo, dragObject);
        } else if (mDragInfo != null) {
            final int[] touchXY = mTmpPoint;
            /* RK_ID: DRAGCONTROLLER. AUT: liuli1 . DATE: 2013-05-14 . START */
//            touchXY[0] = dragObject.x;
//            touchXY[1] = dragObject.y;
            touchXY[0] = (int) mDragViewVisualCenter[0];
            touchXY[1] = (int) mDragViewVisualCenter[1];
            /* RK_ID: DRAGCONTROLLER. AUT: liuli1 . DATE: 2013-05-14 . END */
            ItemInfo info = (ItemInfo) dragObject.dragInfo;
            int spanX = info.spanX;
            int spanY = info.spanY;
            final long container = LauncherSettings.Favorites.CONTAINER_DESKTOP;
            final int screen = getCurrentPage();
            //int[] resultSpan = new int[2];
//            XPagedViewItem child = null;        
//            if (mDragInfo != null)
//            {
//                child = xPagedView.findPageItemAt(mDragInfo.screen, mDragInfo.cellX, mDragInfo.cellY);
//            }            
            
            switch (info.itemType) {
            case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
            case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
            case LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT:
                if (info instanceof ShortcutInfo) {

                    info = new ShortcutInfo((ShortcutInfo) info);
                    
                    // Came from all apps -- make a copy                                        
//                    mTargetCell = findNearestArea(screen, touchXY[0], touchXY[1], spanX, spanY, null, false, mTargetCell);
//                    
//                    if (DEBUG_REORDER)R5.echo("false mTargetCell[0] =" + mTargetCell[0] + "mTargetCell[1]" + mTargetCell[1]);
                    if (createUserFolderIfNecessary(container, mTargetCell, false, null, info)) {
                        revertTempState();
//                        commitTempPlacement();
//                        completeAndClearReorderHintAnimations();
//                        setItemPlacementDirty(false);
                        mSaved = false;
                        mOccuSaved = false;
                        if (DEBUG_REORDER)R5.echo("createUserFolderIfNecessary");
                      //add by zhanggx1 for removing on 2013-11-13 . s
                        ((XLauncher)getXContext().getContext()).autoReorder();
                        //add by zhanggx1 for removing on 2013-11-13 . e
                        return;
                    }

                    if (addToExistingFolderIfNecessary(screen, mTargetCell, false, info, dragObject)) {
                        revertTempState();
//                        commitTempPlacement();
//                        completeAndClearReorderHintAnimations();
//                        setItemPlacementDirty(false);
                        mSaved = false;
                        mOccuSaved = false;
                        if (DEBUG_REORDER)R5.echo("addToExistingFolderIfNecessary");
                      //add by zhanggx1 for removing on 2013-11-13 . s
                        ((XLauncher)getXContext().getContext()).autoReorder();
                        //add by zhanggx1 for removing on 2013-11-13 . e
                        return;
                    } 
                         
//                    mTargetCell = createArea(touchXY[0],
//                            touchXY[1], spanX, spanY, spanX, spanY,
//                            mDragView, mTargetCell, resultSpan, MODE_ON_DROP);
//                    
//                    mTargetCell = findNearestArea(screen, touchXY[0], touchXY[1], spanX, spanY, null, true, mTargetCell);
                    createAreaOnDrop();
                    if (mTargetCell == null
                    		|| mTargetCell[0] < 0
                    		|| mTargetCell[1] < 0) {
                        if (DEBUG_REORDER)R5.echo("mTargetCell null");
                    	return;
                    }
                    
                    if (DEBUG_REORDER)R5.echo("mDragInfo.cellX = " + mDragInfo.cellX + "mDragInfo.cellY = " + mDragInfo.cellY + "mDragInfo.screen = " + mDragInfo.screen);
                    if (mDragInfo.cellX == mTargetCell[0] && mDragInfo.cellY == mTargetCell[1] && mDragInfo.screen == screen)
                    {
                        if (DEBUG_REORDER)R5.echo("mDragInfo null");
//                        DrawableItem last = mDragView.getDrawingTarget();
//                        if (last != null) {
//                            last.setVisibility(true);
//                        }
//                        
//                        xPagedView.markCellsAsOccupiedForView(getCurrentPage(), mDragInfo);
                        xPagedView.addPagedViewItem(mDragView);
                        DrawableItem last = mDragView.getDrawingTarget();
                        if (last != null) {
                            last.resetPressedState();
                        }
                        mDragInfo = null;
//                        dragHasRemoved = true;                        
                        return;
                    }

                    if (DEBUG_REORDER)R5.echo("mTargetCell[0] =" + mTargetCell[0] + "mTargetCell[1]" + mTargetCell[1]);
                    info.container = container;
                    info.cellX = mTargetCell[0];
                    info.cellY = mTargetCell[1];
                    info.screen = screen;
                    addInScreen((ShortcutInfo) info, ((LauncherApplication) getXContext().getContext().getApplicationContext()).getIconCache(), false);
                    XLauncherModel.addOrMoveItemInDatabase(this.getXContext().getContext(), info, info.container, info.screen, info.cellX, info.cellY);
                }
                break;
            case LauncherSettings.Favorites.ITEM_TYPE_FOLDER:
                if (info instanceof FolderInfo) {
                    FolderInfo fInfo = (FolderInfo) info;
//                    FolderInfo fInfo = newInfo.copy();
//                    mTargetCell = createArea(touchXY[0],
//                            touchXY[1], spanX, spanY, spanX, spanY,
//                            mDragView, mTargetCell, resultSpan, MODE_ON_DROP);
//                    
//                    mTargetCell = findNearestArea(screen, touchXY[0], touchXY[1], spanX, spanY,
//                            null, true, mTargetCell);
                    createAreaOnDrop();

                    if (mTargetCell == null
                    		|| mTargetCell[0] < 0
                    		|| mTargetCell[1] < 0) {
                    	return;
                    }
                    
                    if (mDragInfo.cellX == mTargetCell[0] && mDragInfo.cellY == mTargetCell[1] && mDragInfo.screen == screen)
                    {
//                        DrawableItem last = mDragView.getDrawingTarget();
//                        if (last != null) {
//                            last.setVisibility(true);
//                        }
//                        
//                        xPagedView.markCellsAsOccupiedForView(getCurrentPage(), mDragInfo);
                        xPagedView.addPagedViewItem(mDragView);
                        DrawableItem last = mDragView.getDrawingTarget();
                        if (last != null) {
                            last.resetPressedState();
                        }
                        mDragInfo = null;
//                        dragHasRemoved = true;
                        return;
                    }
                    
                    fInfo.container = container;
                    fInfo.cellX = mTargetCell[0];
                    fInfo.cellY = mTargetCell[1];
                    fInfo.screen = screen;
                    ((XLauncher) mXContext.getContext()).updateFolder(fInfo);
//                    fInfo.clearListener();

                    final DrawableItem view = mDragView.getDrawingTarget();
                    if (view != null) {
                        view.reuse();
                        view.resetPressedState();
                        XPagedViewItem itemToAdd = new XPagedViewItem(mXContext, view, fInfo);
                        xPagedView.addPagedViewItem(itemToAdd);
                    }

//                    addInScreen(fInfo);
                    XLauncherModel.addOrMoveItemInDatabase(this.getXContext().getContext(), fInfo,
                            fInfo.container, fInfo.screen, fInfo.cellX, fInfo.cellY);
                }
                break;
            case LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET:
            /*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. S***/
            case LauncherSettings.Favorites.ITEM_TYPE_COMMEND_APPWIDGET:
            /*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. E***/
				if (info instanceof LauncherAppWidgetInfo) {
//                    removePagedViewItem(info);				    

                    LauncherAppWidgetInfo widgetInfo = (LauncherAppWidgetInfo) info;
                    final LauncherAppWidgetInfo newInfo = widgetInfo.copy();
                    
//                    mTargetCell = createArea((int) mDragViewVisualCenter[0],
//                            (int) mDragViewVisualCenter[1], spanX, spanY, spanX, spanY,
//                            mDragView, mTargetCell, resultSpan, MODE_ON_DROP);
//                    R5.echo("createArea mTargetCell[0] = " + mTargetCell[0] + "mTargetCell[1] = " + mTargetCell[1]);
                    createAreaOnDrop();
//                    R5.echo("createArea mTargetCell[0] = " + mTargetCell[0] + "mTargetCell[1] = " + mTargetCell[1]);
                    
//                    mTargetCell = findNearestArea(screen, (int) mDragViewVisualCenter[0],
//                            (int) mDragViewVisualCenter[1], spanX, spanY,
//                            null, true, mTargetCell);
//                    
//                    R5.echo("findNearestArea mTargetCell[0] = " + mTargetCell[0] + "mTargetCell[1] = " + mTargetCell[1]);

                    boolean foundCell = mTargetCell[0] >= 0 && mTargetCell[1] >= 0;
                    
                    if (mDragInfo.cellX == mTargetCell[0] && mDragInfo.cellY == mTargetCell[1] && mDragInfo.screen == screen)
                    {
//                        DrawableItem last = mDragView.getDrawingTarget();
//                        if (last != null) {
//                            last.setVisibility(true);
//                        }
//                        
//                        xPagedView.markCellsAsOccupiedForView(getCurrentPage(), mDragInfo);
                        xPagedView.addPagedViewItem(mDragView);
                        DrawableItem last = mDragView.getDrawingTarget();
                        if (last != null) {
                            last.resetPressedState();
                        }
                        mDragInfo = null;
//                        dragHasRemoved = true;
                        mDraggingWidget = null;
                        
                        /*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-6-27. S***/
                        if ( newInfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET &&
                           	 newInfo.container != LauncherSettings.Favorites.CONTAINER_HOTSEAT  ) {
                        	Log.i("zdx1","1 call resizeWidget");
                                resizeWidget(newInfo, (XViewContainer)last);
                        }
                        /*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-6-27. E***/
                        return;
                    }
                    
//                    removeDragView();                   
                    
                    if (foundCell) {
                        newInfo.container = container;
                        newInfo.cellX = mTargetCell[0];
                        newInfo.cellY = mTargetCell[1];
                        newInfo.screen = screen;

                        Context c = mXContext.getContext();
                        if (c instanceof XLauncher) {
                            XLauncher xlauncher = (XLauncher) c;
                            xlauncher.getMainView().post(new Runnable() {

                                @Override
                                public void run() {
                                    XViewContainer view = new XViewContainer(mXContext,
                                            newInfo.spanX * getPagedView().getCellWidth(),
                                            newInfo.spanY * getPagedView().getCellHeight(),
                                            mDraggingWidget.getParasiteView());

                                    addInScreen(view, newInfo);
                                    view.manageVisibility(XViewContainer.VISIBILITY_SHOW_VIEW, null);
                                    mDraggingWidget = null;
                                    
                                    /*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-6-27. S***/
                                    if ( newInfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET &&
                                    	 newInfo.container != LauncherSettings.Favorites.CONTAINER_HOTSEAT  ) {
                                    	Log.i("zdx1","2 call resizeWidget");
                                         resizeWidget(newInfo, view);
                                    }
                                    /*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-6-27. E***/
                                }

                            });
                        }

                        XLauncherModel.addOrMoveItemInDatabase(this.getXContext().getContext(),
                                newInfo, newInfo.container, newInfo.screen, newInfo.cellX,
                                newInfo.cellY);
                    }
                    else
                    {
                        mDraggingWidget = null;
                    }
                    
                    

                    
                }
                break;
            case LauncherSettings.Favorites.ITEM_TYPE_LEOSWIDGET:
                if (info instanceof LenovoWidgetViewInfo) {
//                    removePagedViewItem(info);
                    
                    LenovoWidgetViewInfo widgetInfo = (LenovoWidgetViewInfo) info;
                    final LenovoWidgetViewInfo newInfo = widgetInfo.copy();
//                    mTargetCell = createArea((int) mDragViewVisualCenter[0],
//                            (int) mDragViewVisualCenter[1], spanX, spanY, spanX, spanY,
//                            mDragView, mTargetCell, resultSpan, MODE_ON_DROP);
                    createAreaOnDrop();
//                    R5.echo("createArea mTargetCell[0] = " + mTargetCell[0] + "mTargetCell[1] = " + mTargetCell[1]);
                    
//                    mTargetCell = findNearestArea(screen, (int) mDragViewVisualCenter[0],
//                            (int) mDragViewVisualCenter[1], spanX, spanY,
//                        null, true, mTargetCell);
//                    
//                    R5.echo("findNearestArea mTargetCell[0] = " + mTargetCell[0] + "mTargetCell[1] = " + mTargetCell[1]);

                    boolean foundCell = mTargetCell[0] >= 0 && mTargetCell[1] >= 0;
                    
                    if (mDragInfo.cellX == mTargetCell[0] && mDragInfo.cellY == mTargetCell[1] && mDragInfo.screen == screen)
                    {
//                        DrawableItem last = mDragView.getDrawingTarget();
//                        if (last != null) {
//                            last.setVisibility(true);
//                        }
//                        
//                        xPagedView.markCellsAsOccupiedForView(getCurrentPage(), mDragInfo);
                        R5.echo("xPagedView addPagedViewItem");
                        xPagedView.addPagedViewItem(mDragView);
                        DrawableItem last = mDragView.getDrawingTarget();
                        if (last != null) {
                            last.resetPressedState();
                        }
                        mDragInfo = null;
                        mDragInfo = null;
//                        dragHasRemoved = true;
                        mDraggingWidget = null;
                        return;
                    }
                    
//                    removeDragView();
                    if (foundCell) {
                        newInfo.container = container;
                        newInfo.cellX = mTargetCell[0];
                        newInfo.cellY = mTargetCell[1];
                        newInfo.screen = screen;

                        Context c = mXContext.getContext();
                        if (c instanceof XLauncher) {
                            XLauncher xlauncher = (XLauncher) c;
                            xlauncher.getMainView().post(new Runnable() {

                                @Override
                                public void run() {
                                    XViewContainer view = new XViewContainer(mXContext,
                                            newInfo.spanX * getPagedView().getCellWidth(),
                                            newInfo.spanY * getPagedView().getCellHeight(),
                                            mDraggingWidget.getParasiteView());

                                    addInScreen(view, newInfo);
                                    view.manageVisibility(XViewContainer.VISIBILITY_SHOW_VIEW, null);
                                    mDraggingWidget = null;
                                }

                            });
                        }

                        XLauncherModel.sLeosWidgets.remove(widgetInfo);
                        XLauncherModel.sLeosWidgets.add(newInfo);
                        XLauncherModel.addOrMoveItemInDatabase(this.getXContext().getContext(),
                                newInfo, newInfo.container, newInfo.screen, newInfo.cellX,
                                newInfo.cellY);
                    }
                    else
                    {
                        mDraggingWidget = null;
                    }
                }
                break;
            default:
                throw new IllegalStateException("Unknown item type: " + info.itemType);
        }

        }
        
        if(mToast != null){
            mHandler.post(new Runnable() {
                
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    mToast.cancel();
                    mToast = null;
                }
            });
        }
        if(mShowToast != null){
            mShowToast = null;
        }
        mSaved = false;
        mOccuSaved = false;
        
      //add by zhanggx1 for reordering on 2013-11-13 . s
        ((XLauncher)getXContext().getContext()).autoReorder();
        //add by zhanggx1 for reordering on 2013-11-13 . e
    }

    private ShowToast mShowToast;
    
    private boolean willAddToExistingUserFolder(int[] targetCell, ItemInfo newinfo, float distance) {
        if (distance > mMaxDistanceForFolderCreation) return false;
        XPagedViewItem item = xPagedView.findPageItemAt(getCurrentPage(), targetCell[0], targetCell[1]);

        DrawableItem dropOverView = null;
        if (item != null) {
            dropOverView = item.getDrawingTarget();
        }

        if (dropOverView instanceof XFolderIcon) {
            XFolderIcon fi = (XFolderIcon) dropOverView;
            if (fi.acceptDrop(newinfo)) {
                return true;
            }else{
            	if((newinfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION || newinfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT
            			|| newinfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT)){
                    if(mShowToast == null){
                        mShowToast = new ShowToast(R.string.application_existed_in_folder);
                    }else{
                        mShowToast.setMessageId(R.string.application_existed_in_folder);
                    }
                    mXContext.post(mShowToast);
                }

            }
        }
        return false;
    }
    
    
    private class ShowToast implements Runnable{
        private int id = 0;
        public ShowToast(int resId){
            id = resId;
        }
        private void setMessageId(int resId){
            id = resId;
        }
        @Override
        public void run() {
            if(mToast ==null){
                mToast = Toast.makeText(mXContext.getContext(), mXContext.getContext().getResources().getString(id), 0);
            }else{
                mToast.setText(id);
            }
            mToast.show();            
        }
        
    }
    private boolean addToExistingFolderIfNecessary(int screen, int[] targetCell, boolean external, ItemInfo info, XDragObject d) {
        XPagedViewItem item = xPagedView.findPageItemAt(screen, targetCell[0], targetCell[1]);

        DrawableItem dropOverView = null;
        if (item != null) {
            dropOverView = item.getDrawingTarget();
        }

        if (dropOverView instanceof XFolderIcon) {
            XFolderIcon fi = (XFolderIcon) dropOverView;
            if (fi.acceptDrop(info)) {
                // if the drag started here, we need to remove it from the workspace
//                if (!external) {
//                    XPagedViewItem newView = xPagedView.findPageItemAt(info.screen,
//                            info.cellX, info.cellY);
//                    xPagedView.removePagedViewItem(newView);
//                }

                fi.onDrop(d);
                return true;
            }
        }
        return false;
    }

    private boolean willCreateUserFolder(ItemInfo newinfo, int[] targetCell, float distance) {
        if (distance > mMaxDistanceForFolderCreation) return false;
        XPagedViewItem item = xPagedView.findPageItemAt(getCurrentPage(), targetCell[0], targetCell[1]);
        DrawableItem dropOverView = null;
        if (item != null) {
            dropOverView = item.getDrawingTarget();
        }

        boolean hasntMoved = false;
        if (newinfo == null) {
            return false;
        }
        hasntMoved = (newinfo.cellX == targetCell[0] && newinfo.cellY == targetCell[1]) && (getCurrentPage() == newinfo.screen);

        if (dropOverView == null || hasntMoved) {
            return false;
        }
	if (newinfo instanceof ApplicationInfo) {
		// Came from all apps -- make a copy
		newinfo = new ShortcutInfo((ApplicationInfo) newinfo);
	}
        boolean aboveShortcut = (dropOverView.getTag() instanceof ShortcutInfo);
        boolean willBecomeShortcut = (newinfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION ||
                newinfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT
                || newinfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT);
        if(aboveShortcut && willBecomeShortcut){
            if(((ShortcutInfo )dropOverView.getTag()).equalsIgnorePosition((ShortcutInfo)newinfo)){
                
                if(mShowToast == null){
                    mShowToast = new ShowToast(R.string.application_not_create_folder);
                }else{
                    mShowToast.setMessageId(R.string.application_not_create_folder);
                }
                mXContext.post(mShowToast);
                return false;
            }
        }

        return (aboveShortcut && willBecomeShortcut);
    }

    private boolean createUserFolderIfNecessary(long container, int[] targetCell, boolean external,
            Runnable postAnimationRunnable, ItemInfo newinfo) {
        final int screen = getCurrentPage();
        XPagedViewItem v = xPagedView.findPageItemAt(screen, targetCell[0],
                targetCell[1]);

        boolean hasntMoved = false;
        if (newinfo == null) {
            return false;
        }
        hasntMoved = (newinfo.cellX == targetCell[0] && newinfo.cellY == targetCell[1]) && (getCurrentPage() == newinfo.screen);
        XPagedViewItem newView;
        
        if (external)
        {
            newView = getPagedViewItem((ShortcutInfo)newinfo);
        }
        else
        {
//            newView = xPagedView.findPageItemAt(newinfo.screen,
//                newinfo.cellX, newinfo.cellY);
            newView = mDragView;
        }
        

        if (v == null || hasntMoved || newView == null)
            return false;
        boolean aboveShortcut = (v.getDrawingTarget().getTag() instanceof ShortcutInfo);
        boolean willBecomeShortcut = (newinfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION 
                || newinfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT
                || newinfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT);

        if (aboveShortcut && willBecomeShortcut 
                && newView.getDrawingTarget().getTag() instanceof ShortcutInfo 
                && v.getDrawingTarget().getTag() instanceof ShortcutInfo) {
            /*** fixbug 9715  . AUT: zhaoxy . DATE: 2013-03-27. START***/
            ShortcutInfo sourceInfo = new ShortcutInfo((ShortcutInfo) newView.getDrawingTarget().getTag());
            ShortcutInfo destInfo = new ShortcutInfo((ShortcutInfo) v.getDrawingTarget().getTag());
            /*** fixbug 9715  . AUT: zhaoxy . DATE: 2013-03-27. END***/
            /*** fixbug 10143  . AUT: zhaoxy . DATE: 2013-03-28. START***/
            if (sourceInfo.equalsIgnorePosition(destInfo)) {
                return false;
            }
            /*** fixbug 10143  . AUT: zhaoxy . DATE: 2013-03-28. END***/

            // if the drag started here, we need to remove it from the workspace
            if (!external) {
                xPagedView.removePagedViewItem(newView);
            }

//            Rect folderLocation = new Rect();
//            float scale = mLauncher.getDragLayer().getDescendantRectRelativeToSelf(v,
//                    folderLocation);
            xPagedView.removePagedViewItem(v);

            Context c = mXContext.getContext();
            if (c instanceof XLauncher) {
                XLauncher xlauncher = (XLauncher) c;
                FolderInfo fi = xlauncher.addFolder(container, screen, targetCell[0],
                        targetCell[1]);
                destInfo.screen = 0;
                destInfo.cellX = -1;
                destInfo.cellY = -1;
                destInfo.container = fi.id;
                sourceInfo.screen = 0;
                sourceInfo.cellX = -1;
                sourceInfo.cellY = -1;
                sourceInfo.container = fi.id;

                // If the dragView is null, we can't animate
                boolean animate = false;// dragView != null;
                if (animate) {
//                fi.performCreateAnimation(destInfo, v, sourceInfo, dragView, folderLocation, scale,
//                        postAnimationRunnable);
                } else {
                    fi.add(destInfo);
                    fi.add(sourceInfo);
                }
            }
            return true;
        }
        return false;
    }

    private void onDropExternal(final int[] touchXY, final Object dragInfo, XDragObject d) {
    	// dooba edit s
    	if (!(dragInfo instanceof ItemInfo)) {
    		onDropIconPkg(touchXY, dragInfo, d);
    		return;
    	}
    	// dooba edit e
    	
        ItemInfo info = (ItemInfo) dragInfo;		
        
        addExternalItemInfo(touchXY, info, false, d);
        
//        mTargetCell = findNearestArea(screen, touchXY[0], touchXY[1], spanX, spanY, null, true, mTargetCell);
//        addInScreen(info, iconCache, insert)
        
    }

    private static final int TIMEOUT_OPEN_FOLDER = 1000;
    private static final int TIMEOUT_CLOSE_FOLDER = 500;
    private static final int MSG_OPEN_FOLDER = 100;
    private static final int MSG_CLOSE_FOLDER = 200;
    private static final int MSG_REVERT_TEMP_STATE = 300;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_OPEN_FOLDER:
                    final XFolderIcon fi = (XFolderIcon) msg.obj;
                    ((XLauncher) mXContext.getContext()).openFolder(fi);
                    if (mFolderRingAnimator != null) {
                        mFolderRingAnimator.closeAnimation();
                        mFolderRingAnimator = null;
                    }
                    break;
                case MSG_CLOSE_FOLDER:
                    ((XLauncher) mXContext.getContext()).closeFolder();
                    break;
                case MSG_REVERT_TEMP_STATE:
//                    mXContext.getRenderer().getEventHandler().post(new Runnable(){
//
//                        @Override
//                        public void run() {
                            revertTempState();
//                        }
//                        
//                    }
//                    );
                    
                    mXContext.getRenderer().invalidate();
                    break;
                default:
                	break;
            }
        };
    };

    public void closeFolderDelayed() {
        if (getOpenFolder() != null && !mHandler.hasMessages(MSG_CLOSE_FOLDER)) {
            mHandler.sendEmptyMessageDelayed(MSG_CLOSE_FOLDER, TIMEOUT_CLOSE_FOLDER);
        }
    }

    /*** fixbug 12707 . AUT: zhaoxy . DATE: 2013-05-21. START***/
    public void cancelcloseFolderDelayed() {
        mHandler.removeMessages(MSG_CLOSE_FOLDER);
    }
    /*** fixbug 12707 . AUT: zhaoxy . DATE: 2013-05-21. END***/
    
    @Override
    public void onDragEnter(XDragObject dragObject) {
        if (DEBUG_DRAG) Log.d(TAG_DEBUG_DRAG, "Workspace onDragEnter");
        saveTmpCell();
        dragObject.dragView.setEffectAnimEnable(false);
        
        if (dragObject.dragSource instanceof AppContentView)
        {
            ShortcutInfo info = new ShortcutInfo((ApplicationInfo) dragObject.dragInfo);
            mDragOutline = getDragOutlineByShortcutInfo(info);
        }
        else if (dragObject.dragSource instanceof XFolder)
        {   
            ShortcutInfo info = (ShortcutInfo)dragObject.dragInfo ;
            mDragOutline = getDragOutlineByShortcutInfo(info);
        }
        else if (dragObject.dragSource instanceof XHotseat && dragObject.dragInfo instanceof ShortcutInfo)
        { 
        	ShortcutInfo info = (ShortcutInfo)dragObject.dragInfo ;
            mDragOutline = getDragOutlineByShortcutInfo(info);
        }
        else if (dragObject.dragSource instanceof XHotseat && dragObject.dragInfo instanceof FolderInfo)
        { 
//            Bitmap b = dragObject.dragView.getBitmap();
//            mDragOutline = getDragOutlineByDrawableBitmap(b);
        	FolderInfo info = (FolderInfo)dragObject.dragInfo ;
            mDragOutline = getDragOutlineByFolderInfo(info);
        }
        else if (dragObject.dragSource instanceof XScreenContentTabHost && dragObject.dragInfo instanceof XScreenShortcutInfo)
        {   
            ShortcutInfo info = new ShortcutInfo((XScreenShortcutInfo)dragObject.dragInfo);
            mDragOutline = getDragOutlineByShortcutInfo(info);
        }
//        else if (dragObject.dragSource instanceof XScreenContentTabHost && dragObject.dragInfo instanceof LenovoWidgetViewInfo)
//        {   
//            LenovoWidgetViewInfo lenovoWidget = (LenovoWidgetViewInfo)dragObject.dragInfo;
//            
//            lenovoWidget.spanX = lenovoWidget.minWidth;
//            lenovoWidget.spanY = lenovoWidget.minHeight;
//            
//            View v;
//            XLauncher xLauncher = (XLauncher)mXContext.getContext();
//            if (lenovoWidget.packageName.equals(xLauncher.getPackageName())) {
//                v = com.lenovo.launcher2.gadgets.GadgetUtilities.fetchView(
//                        xLauncher, lenovoWidget.componentName);
//            } else {
//                Log.d("liuyg1", "getLeosWidgetViewToWorkspace ");
//                v = (View) xLauncher.getLeosWidgetViewToWorkspace(lenovoWidget);
//            }
//            
//            mDragOutline = createDragOutline(v, new Canvas(), 0);
//        }
        else if (dragObject.dragSource instanceof XScreenContentTabHost && dragObject.dragInfo instanceof SimpleItemInfo)
        {   
            Bitmap b = dragObject.dragView.getBitmap();
            mDragOutline = getDragOutlineByDrawableBitmap(b);
        }
        else if (dragObject.dragSource == this)
        {
//            final Canvas canvas = new Canvas();
//            Bitmap b = dragObject.dragView.getBitmap();
//            mDragOutline = createDragOutline(b, canvas, 0, b.getWidth(), b.getHeight(), null);
        }  
        else if (dragObject.dragSource != this )
        {
//            if (!(dragObject.dragInfo instanceof ItemInfo)) {
//                return;
//            }
//            
//            final ItemInfo info = (ItemInfo) dragObject.dragInfo;

            
            Bitmap b = dragObject.dragView.getBitmap();
            final int bitmapPadding = HolographicOutlineHelper.MAX_OUTER_BLUR_RADIUS;
            int width = b.getWidth();
            int height = b.getHeight();
            
            final Canvas canvas = new Canvas();
                        
            mDragOutline = createDragOutline(b, canvas, bitmapPadding, width, height, null);           
        }
        
        xPageIndicator.startEnterAnimation();
        
//        mDragOutlinesNewDraw = true;        
    }

    @Override
    public void onDragOver(XDragObject dragObject) {
		if (dragObject == null || dragObject.dragView == null || folderAnimInput > 0.01f) {
			return;
		}
    	// dooba edit s
    	if (!(dragObject.dragInfo instanceof ItemInfo)) {
    		return;
    	}
    	// dooba edit e
    	
        final ItemInfo info = (ItemInfo) dragObject.dragInfo;
        int spanX = info.spanX;
        int spanY = info.spanY;
        if (info instanceof LenovoWidgetViewInfo && dragObject.dragSource instanceof XScreenContentTabHost) {
			spanX = ((LenovoWidgetViewInfo)info).minWidth;
			spanY = ((LenovoWidgetViewInfo)info).minHeight;
		} else if (info.itemType == SimpleItemInfo.ACTION_TYPE_CREATE_WIDGET) {
			spanX = ((SimpleItemInfo)info).spanXY[0];
			spanY = ((SimpleItemInfo)info).spanXY[1];
		}
//        final int[] cellXY = mTargetCell;
//        cellXY[0] = dragObject.x / xPagedView.getCellWidth();
//        cellXY[1] = dragObject.y / xPagedView.getCellHeight();
        /* RK_ID: DRAGCONTROLLER. AUT: liuli1 . DATE: 2013-05-14 . START */
//        mDragViewVisualCenter[0] = dragObject.x;
//        mDragViewVisualCenter[1] = dragObject.y;
        mDragViewVisualCenter = getDragViewVisualCenter(dragObject.x, dragObject.y, dragObject.xOffset, dragObject.yOffset,
                dragObject.dragView, mDragViewVisualCenter);
        /* RK_ID: DRAGCONTROLLER. AUT: liuli1 . DATE: 2013-05-14 . END */
//        XPagedViewItem child = null;        
//        if (mDragInfo != null)
//        {
//            child = xPagedView.findPageItemAt(mDragInfo.screen, mDragInfo.cellX, mDragInfo.cellY);
//        }
                
        mTargetCell = xPagedView.findNearestArea(getCurrentPage(), (int) mDragViewVisualCenter[0],
                (int) mDragViewVisualCenter[1], spanX, spanY, mTargetCell);
        
//        R5.echo("mDragViewVisualCenter = " + mDragViewVisualCenter[0] + "         " + mDragViewVisualCenter[1]
//        		+ "mTargetCell = " + mTargetCell[0] + "              " + mTargetCell[1]);

        
        setCurrentDropOverCell(mTargetCell[0], mTargetCell[1]);
        
        float targetCellDistance = getDistanceFromCell(
                mDragViewVisualCenter[0], mDragViewVisualCenter[1], mTargetCell);

        final XPagedViewItem dragOverView = xPagedView.findPageItemAt(mTargetCell[0],
                mTargetCell[1]);

        manageFolderFeedback(info, mTargetCell,
                targetCellDistance, dragOverView);                       
                
        boolean nearestDropOccupied = isNearestDropLocationOccupied((int)
                /* RK_ID: DRAGCONTROLLER. AUT: liuli1 . DATE: 2013-05-14 . START */
//                dragObject.x, (int) dragObject.y, info.spanX,
                mDragViewVisualCenter[0], (int) mDragViewVisualCenter[1], spanX,
                /* RK_ID: DRAGCONTROLLER. AUT: liuli1 . DATE: 2013-05-14 . END */
                spanY, mDragView, mTargetCell);
        
        if (DEBUG_REORDER)R5.echo("nearestDropOccupied = " + nearestDropOccupied);
        
        if (!nearestDropOccupied) {
            visualizeDropLocation(mDragView, mDragOutline,
                    (int) mDragViewVisualCenter[0], (int) mDragViewVisualCenter[1],
                    mTargetCell[0], mTargetCell[1], spanX, spanY, false,
                    null, null);
        }
        else if ((mDragMode == DRAG_MODE_NONE || mDragMode == DRAG_MODE_REORDER) &&
                !mReorderAlarm.alarmPending() && (mLastReorderX != mTargetCell[0] ||
                mLastReorderY != mTargetCell[1])) {

            // Otherwise, if we aren't adding to or creating a folder and there's no pending
            // reorder, then we schedule a reorder
            mReorderAlarmListener = new ReorderAlarmListener(mDragViewVisualCenter,
                    spanX, spanY, spanX, spanY, dragObject.dragView, mDragView);
            mReorderAlarm.setOnAlarmListener(mReorderAlarmListener);
            mReorderAlarm.setAlarm(REORDER_TIMEOUT);
        }
        
        if (mDragMode == DRAG_MODE_CREATE_FOLDER || mDragMode == DRAG_MODE_ADD_TO_FOLDER ||
                !nearestDropOccupied) {
            revertTempState();
        }
        
    }

    // This is used to compute the visual center of the dragView. This point is then
    // used to visualize drop locations and determine where to drop an item. The idea is that
    // the visual center represents the user's interpretation of where the item is, and hence
    // is the appropriate point to use when determining drop location.
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
        
		int wkPaddingTop = mContext.getResources().getDimensionPixelSize(
                R.dimen.workspace_screen_padding_top);

        // In order to find the visual center, we shift by half the dragRect
        res[0] = left + dragView.getDragRegion().width() / 2 - localRect.left;
        res[1] = top + dragView.getDragRegion().height() / 2 - wkPaddingTop;

        return res;
    }

    @Override
    public void onDragExit(XDragObject dragObject) {
        if (DEBUG_DRAG) Log.d(TAG_DEBUG_DRAG, "Workspace onDragExit");
        boolean clearDragOutline = true;
        if (dragObject.dragSource == this && !dragObject.dragComplete)
        {
            clearDragOutline = false;
        }
        doDragExit(!dragObject.dragComplete, clearDragOutline);
    }
    
    public void doDragExit(boolean revertNow, boolean clearDragOutline) {
        if (DEBUG_REORDER)R5.echo("doDragExit revertNow = " + revertNow + "clearDragOutline = " + clearDragOutline);
        cleanupReorder(true);
        cleanupFolderCreation();        
        cleanupAddToFolder();
        mDragMode = DRAG_MODE_NONE;
        clearDragOutlines(clearDragOutline);
        onResetScrollArea();
        if (revertNow)
        {
            revertTempState();
        }
        else
        {
            mHandler.removeMessages(MSG_REVERT_TEMP_STATE);
            mHandler.sendEmptyMessage(MSG_REVERT_TEMP_STATE);
        }
        mSaved = false;
        mOccuSaved = false;
        
        // Invalidate the drag data
        mDragCell[0] = -1;
        mDragCell[1] = -1;
//        mDragOutlineAnims[mDragOutlineCurrent].cancel();
//        mDragOutlineCurrent = (mDragOutlineCurrent + 1) % mDragOutlineAnims.length;
//        
//        mDragOutlineAnims[mDragOutlineCurrent].setTag(mDragOutline);
//        mDragOutlineAnims[mDragOutlineCurrent].animateOut();
//        mDragOutlineCurrent = (mDragOutlineCurrent + 1) % mDragOutlines.length;
    }

    @Override
    public XDropTarget getDropTargetDelegate(XDragObject dragObject) {
        return null;
    }

    @Override
    public boolean acceptDrop(XDragObject dragObject) {
    	mHandler.removeMessages(MSG_REVERT_TEMP_STATE);
	   // dooba edit s
    	if (dragObject.dragInfo instanceof List) {
    		return true;
    	} else if (!(dragObject.dragInfo instanceof ItemInfo)
    			|| dragObject.dragInfo instanceof XScreenMngView.PreviewInfo || folderAnimInput > 0.01f) {
    		return false;
    	}
    	// dooba edit e        
        final ItemInfo info = (ItemInfo) dragObject.dragInfo;
//        final int[] cellXY = mTargetCell;
                
//        boolean foundCell = mTargetCell[0] >= 0 && mTargetCell[1] >= 0;

//        cellXY[0] = dragObject.x / xPagedView.getCellWidth();
//        cellXY[1] = dragObject.y / xPagedView.getCellHeight();
        
//        if (mDragInfo != null) {
//            if (mDragInfo.screen == getCurrentPage() 
//                    && mDragInfo.cellX == cellXY[0] && mDragInfo.cellY == cellXY[1]
//                    //当前的区域内不要被别的占了。    
//                    && xPagedView.isCellparamValid(mDragInfo.cellX, mDragInfo.cellX, mDragInfo.spanX, mDragInfo.spanY)
//                    ) {
//                R5.echo("same return false");
//                return false;
//            }
//        }

//        final int[] touchXY = mTmpPoint;   
        
        int spanX = info.spanX;
        int spanY = info.spanY;
        if (info instanceof LenovoWidgetViewInfo && dragObject.dragSource instanceof XScreenContentTabHost) {
            spanX = ((LenovoWidgetViewInfo)info).minWidth;
            spanY = ((LenovoWidgetViewInfo)info).minHeight;
        } else if (info.itemType == SimpleItemInfo.ACTION_TYPE_CREATE_WIDGET) {
            spanX = ((SimpleItemInfo)info).spanXY[0];
            spanY = ((SimpleItemInfo)info).spanXY[1];
        }
        
        mDragViewVisualCenter = getDragViewVisualCenter(dragObject.x, dragObject.y, dragObject.xOffset, dragObject.yOffset,
                dragObject.dragView, mDragViewVisualCenter);
        
        mTargetCell = findNearestArea(getCurrentPage(), (int)mDragViewVisualCenter[0], (int)mDragViewVisualCenter[1], spanX, spanY, null, false, mTargetCell);
        float distance = getDistanceFromCell(mDragViewVisualCenter[0],
                mDragViewVisualCenter[1], mTargetCell);
        if (DEBUG_REORDER)R5.echo("acceptDrop cellXY[0] = " + mTargetCell[0] + "cellXY[1] = " + mTargetCell[1]
                                   + "info.spanX = " + info.spanX + "info.spanY = " + info.spanY);
        if (willAddToExistingUserFolder(mTargetCell, info, distance)) {
            if (DEBUG_REORDER)R5.echo("acceptDrop willAddToExistingUserFolder");
            return true;
        }

        if (willCreateUserFolder(info, mTargetCell, distance)) {
            if (DEBUG_REORDER)R5.echo("acceptDrop willCreateUserFolder");
            return true;
        }
        
        try
        {
            mTargetCell = createArea((int) mDragViewVisualCenter[0],
                (int) mDragViewVisualCenter[1], spanX, spanY, spanX, spanY,
                mDragView, mTargetCell, null, MODE_ACCEPT_DROP);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        if (DEBUG_REORDER)R5.echo("acceptDrop mTargetCell[0] = " + mTargetCell[0] + "mTargetCell[1] = " + mTargetCell[1]);
        if (mTargetCell[0] == -1 || mTargetCell[1] == -1)
        {
        	((XLauncher) mXContext.getContext()).showOutOfSpaceMessage();
        	revertTempState();
            return false;
        }
        
        //zhanghong5 此处不能如此修改，因为findCellForSpan中会修改mOccupied数据，导致结果不正确。
//        ItemInfo ignoreInfo = null;
//        if (dragObject.dragSource == this)
//        {
//            ignoreInfo = info;
//        }  
//        
//        // Don't accept the drop if there's no room for the item
//        if (!getPagedView().findCellForSpan(mTargetCell, info.spanX, info.spanY, getCurrentPage(), ignoreInfo)) {
//            ((XLauncher) mXContext.getContext()).showOutOfSpaceMessage();
//            mSaved = false;
//            mOccuSaved = false;
//            return false;
//        }
        return true;
    }

    @Override
    public void getHitRect(Rect outRect) {
    	XDragLayer dragLayer = ((XLauncherView) getXContext()).getDragLayer();
        outRect.set(0, 0, (int) dragLayer.getWidth(), (int) (getHeight() - xPageIndicator.getTouchHomePointHeight()));
    }

    @Override
    public void getLocationInDragLayer(int[] loc) {
        ((XLauncher) mXContext.getContext()).getDragLayer().getLocationInDragLayer(this, loc);
        loc[0] = 0;
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
    public void onDragStart(XDragSource source, Object info, int dragAction) {
    	
    	// exchange layer
    	getXContext().bringContentViewToFront();
    }

    @Override
    public void onDragEnd() {
    	
    	if(mState == State.NORMAL && getOpenFolder() == null){
    		xPagedView.bringStageToFront();
    	}
    	
		if( xPagedView != null ){
			xPagedView.setEnableEffect( true );
		}
    }
    
    public void changeScreenOrder(final int fromIndex, final int toIndex) {
    	if (xPagedView != null) {
    		xPagedView.changeScreenOrder(fromIndex, toIndex);
    	}
    }

    @Override
    public void scrollLeft() {
        doDragExit(true, false);
    	if (SettingsValue.isWorkspaceLoop(mXContext.getContext()) 
    			|| xPagedView.getCurrentPage() != 0) {
            xPagedView.scrollToLeft(SettingsValue.SCROLL_DURATION);
    	}        

        Context c = mXContext.getContext();
        if (c instanceof XLauncher) {
            XLauncher launcher = (XLauncher) c;
            launcher.dismissQuickActionWindow();
        }
    }

    @Override
    public void scrollRight() {
        doDragExit(true, false);
    	if (SettingsValue.isWorkspaceLoop(mXContext.getContext())
    			|| xPagedView.getCurrentPage() != xPagedView.getPageCount() - 1) {
            xPagedView.scrollToRight(SettingsValue.SCROLL_DURATION);
    	}        

        Context c = mXContext.getContext();
        if (c instanceof XLauncher) {
            XLauncher launcher = (XLauncher) c;
            launcher.dismissQuickActionWindow();
        }
    }

    @Override
    public boolean onEnterScrollArea(int x, int y, int direction) {
        boolean result = false;
        XLauncher l = (XLauncher) getXContext().getContext();
        if (!isTouchable() || l == null || l.isFolderAnimating()) {
            return false;
        }
        final int page = xPagedView.getPageCount();
        if (page > 1) {
            result = true;
        }

        return result;
    }

    @Override
    public boolean onExitScrollArea() {
        return true;
    }

    private void onResetScrollArea() {
    }

    @Override
    public boolean isScrollEnabled() {
        return true;
    }

    @Override
    public int getScrollWidth() {
        XDragLayer dragLayer = ((XLauncherView) getXContext()).getDragLayer();
        return (int) dragLayer.localRect.width();
    }
    
    boolean isNearestDropLocationOccupied(int pixelX, int pixelY, int spanX, int spanY,
            XPagedViewItem dragView, int[] result) {
        result = xPagedView.findNearestArea(xPagedView.getCurrentPage(), pixelX, pixelY, spanX, spanY, result);
        getViewsIntersectingRegion(result[0], result[1], spanX, spanY, dragView, null,
                mIntersectingViews);
        return !mIntersectingViews.isEmpty();
    }
    
    private void getViewsIntersectingRegion(int cellX, int cellY, int spanX, int spanY,
            XPagedViewItem dragView, Rect boundingRect, ArrayList<XPagedViewItem> intersectingViews) {
        if (boundingRect != null) {
            boundingRect.set(cellX, cellY, cellX + spanX, cellY + spanY);
        }
        intersectingViews.clear();
        Rect r0 = new Rect(cellX, cellY, cellX + spanX, cellY + spanY);
        Rect r1 = new Rect();

        XPagedViewItem item;
        ItemInfo info;
        
        Set<Map.Entry<Long, XPagedViewItem>> set = null;
        synchronized ( xPagedView.mItemIDMap ) {
        	set = ((HashMap<Long, XPagedViewItem>)xPagedView.mItemIDMap.clone()).entrySet();			
		}
        
        try {
            for (Map.Entry<Long, XPagedViewItem> map : set) {
                item = map.getValue();
                if (dragView == item)
                {
                    continue;
                }
                info = item.getInfo();
                if (info.screen != getCurrentPage())
                {
                    continue;
                }
                r1.set(info.tmpCellX, info.tmpCellY, info.tmpCellX + info.spanX, info.tmpCellY + info.spanY);
                if (Rect.intersects(r0, r1)) {
                    mIntersectingViews.add(item);
                    if (boundingRect != null) {
                        boundingRect.union(r1);
                    }
                }
            }
        } catch (Exception e) {
            Log.v("xworkapce","get next error");
        }
    }
    
    private ArrayList<XPagedViewItem> mIntersectingViews = new ArrayList<XPagedViewItem>();
    private final Alarm mReorderAlarm = new Alarm();
    private int mLastReorderX = -1;
    private int mLastReorderY = -1;
    static int REORDER_TIMEOUT = 300;
    private float[] mDragViewVisualCenter = new float[2];
    private int[] mDirectionVector = new int[2];
    int[] mPreviousReorderDirection = new int[2];
    private static final int INVALID_DIRECTION = -100;
    private static final int DRAG_MODE_NONE = 0;
    private static final int DRAG_MODE_CREATE_FOLDER = 1;
    private static final int DRAG_MODE_ADD_TO_FOLDER = 2;
    private static final int DRAG_MODE_REORDER = 3;
    private int mDragMode = DRAG_MODE_NONE;
    private static final int REORDER_ANIMATION_DURATION = 150;
    int[] mTempLocation = new int[2];
    boolean[][] mTmpOccupied;
    boolean[][] mSavedOccupied;
    private Rect mOccupiedRect = new Rect();
    private ReorderAlarmListener mReorderAlarmListener;
    
    class ReorderAlarmListener implements OnAlarmListener {
        float[] dragViewCenter;
        int minSpanX, minSpanY, spanX, spanY;
        XDragView dragView;
        XPagedViewItem child;
//        private Runnable mRunnable = new Runnable(){
//
//            @Override
//            public void run() {
//                // TODO Auto-generated method stub
//                int[] resultSpan = new int[2];
//                mTargetCell = findNearestArea(getCurrentPage(), (int) mDragViewVisualCenter[0],
//                        (int) mDragViewVisualCenter[1], spanX, spanY, null, false, mTargetCell);
//                mLastReorderX = mTargetCell[0];
//                mLastReorderY = mTargetCell[1];
//
//                try
//                {
//                mTargetCell = createArea((int) mDragViewVisualCenter[0],
//                    (int) mDragViewVisualCenter[1], minSpanX, minSpanY, spanX, spanY,
//                    child, mTargetCell, resultSpan, MODE_DRAG_OVER);
//                }
//                catch (Exception e)
//                {
//                    e.printStackTrace();
//                }
//
//                if (mTargetCell[0] < 0 || mTargetCell[1] < 0) {
//                    revertTempState();
//                } else {
//                    setDragMode(DRAG_MODE_REORDER);
//                }
//
//                boolean resize = resultSpan[0] != spanX || resultSpan[1] != spanY;
//                visualizeDropLocation(child, mDragOutline,
//                    (int) mDragViewVisualCenter[0], (int) mDragViewVisualCenter[1],
//                    mTargetCell[0], mTargetCell[1], resultSpan[0], resultSpan[1], resize,
//                    null, null);
//            }
//            
//        };

        public ReorderAlarmListener(float[] dragViewCenter, int minSpanX, int minSpanY, int spanX,
                int spanY, XDragView dragView, XPagedViewItem child) {
            this.dragViewCenter = dragViewCenter;
            this.minSpanX = minSpanX;
            this.minSpanY = minSpanY;
            this.spanX = spanX;
            this.spanY = spanY;
            this.child = child;
            this.dragView = dragView;
        }

        public void onAlarm(Alarm alarm) {
//            mXContext.getRenderer().getEventHandler().post(mRunnable
//            );
//            
//            mXContext.getRenderer().invalidate();
            int[] resultSpan = new int[2];
            mTargetCell = findNearestArea(getCurrentPage(), (int) mDragViewVisualCenter[0],
                    (int) mDragViewVisualCenter[1], spanX, spanY, null, false, mTargetCell);
            mLastReorderX = mTargetCell[0];
            mLastReorderY = mTargetCell[1];

            try
            {
            mTargetCell = createArea((int) mDragViewVisualCenter[0],
                (int) mDragViewVisualCenter[1], minSpanX, minSpanY, spanX, spanY,
                child, mTargetCell, resultSpan, MODE_DRAG_OVER);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            if (mTargetCell[0] < 0 || mTargetCell[1] < 0) {
                revertTempState();
            } else {
                setDragMode(DRAG_MODE_REORDER);
            }

            boolean resize = resultSpan[0] != spanX || resultSpan[1] != spanY;
            visualizeDropLocation(child, mDragOutline,
                (int) mDragViewVisualCenter[0], (int) mDragViewVisualCenter[1],
                mTargetCell[0], mTargetCell[1], resultSpan[0], resultSpan[1], resize,
                null, null);
        }
        
//        public void removeRunnable() {
//            mXContext.getRenderer().getEventHandler().removeCallbacks(mRunnable
//            );            
//        }
    }
    
    public static final int MODE_DRAG_OVER = 0;
    public static final int MODE_ON_DROP = 1;
    public static final int MODE_ON_DROP_EXTERNAL = 2;
    public static final int MODE_ACCEPT_DROP = 3;
    private static final boolean DESTRUCTIVE_REORDER = false;

    
    int[] createArea(int pixelX, int pixelY, int minSpanX, int minSpanY, int spanX, int spanY,
            XPagedViewItem dragView, int[] result, int resultSpan[], int mode) {
        // First we determine if things have moved enough to cause a different layout
        if (DEBUG_REORDER)R5.echo("createArea pixelX = " + pixelX + "pixelY = " + pixelY 
                + "minSpanX = " + minSpanX + "minSpanY = " + minSpanY + "spanX = " + spanX
                + "spanY = " + spanY + "dragView = " + dragView + " mode = " + mode);
        
//        result = findNearestArea(getCurrentPage(), pixelX, pixelY, spanX, spanY, null, false, result);

        if (resultSpan == null) {
            resultSpan = new int[2];
        }

        // When we are checking drop validity or actually dropping, we don't recompute the
        // direction vector, since we want the solution to match the preview, and it's possible
        // that the exact position of the item has changed to result in a new reordering outcome.
        if ((mode == MODE_ON_DROP || mode == MODE_ON_DROP_EXTERNAL || mode == MODE_ACCEPT_DROP)
               && mPreviousReorderDirection[0] != INVALID_DIRECTION) {
            mDirectionVector[0] = mPreviousReorderDirection[0];
            mDirectionVector[1] = mPreviousReorderDirection[1];
            // We reset this vector after drop
            if (mode == MODE_ON_DROP || mode == MODE_ON_DROP_EXTERNAL) {
                mPreviousReorderDirection[0] = INVALID_DIRECTION;
                mPreviousReorderDirection[1] = INVALID_DIRECTION;
            }
        } else {
            getDirectionVectorForDrop(pixelX, pixelY, spanX, spanY, dragView, mDirectionVector);
            mPreviousReorderDirection[0] = mDirectionVector[0];
            mPreviousReorderDirection[1] = mDirectionVector[1];
        }

        ItemConfiguration swapSolution = simpleSwap(pixelX, pixelY, minSpanX, minSpanY,
                 spanX,  spanY, mDirectionVector, dragView,  true,  new ItemConfiguration());

        // We attempt the approach which doesn't shuffle views at all
        ItemConfiguration noShuffleSolution = findConfigurationNoShuffle(pixelX, pixelY, minSpanX,
                minSpanY, spanX, spanY, dragView, new ItemConfiguration());

        ItemConfiguration finalSolution = null;
        if (swapSolution.isSolution && swapSolution.area() >= noShuffleSolution.area()) {
            finalSolution = swapSolution;
        } else if (noShuffleSolution.isSolution) {
            finalSolution = noShuffleSolution;
        }

        boolean foundSolution = true;
        if (!DESTRUCTIVE_REORDER) {
            setUseTempCoords(true);
        }

        if (finalSolution != null) {
            result[0] = finalSolution.dragViewX;
            result[1] = finalSolution.dragViewY;
            resultSpan[0] = finalSolution.dragViewSpanX;
            resultSpan[1] = finalSolution.dragViewSpanY;

            // If we're just testing for a possible location (MODE_ACCEPT_DROP), we don't bother
            // committing anything or animating anything as we just want to determine if a solution
            // exists
            if (mode == MODE_DRAG_OVER || mode == MODE_ON_DROP || mode == MODE_ON_DROP_EXTERNAL 
                    || mode == MODE_ACCEPT_DROP) {
                if (!DESTRUCTIVE_REORDER) {
                    copySolutionToTempState(finalSolution, dragView);
                }
                setItemPlacementDirty(true);
                animateItemsToSolution(finalSolution, dragView, mode == MODE_ON_DROP);

                if (!DESTRUCTIVE_REORDER &&
                        (mode == MODE_ON_DROP || mode == MODE_ON_DROP_EXTERNAL)) {
                    commitTempPlacement();
                    completeAndClearReorderHintAnimations();
                    setItemPlacementDirty(false);
                } else {
                    beginOrAdjustHintAnimations(finalSolution, dragView,
                            REORDER_ANIMATION_DURATION);
                }
            }
        } else {
            if (DEBUG_REORDER)R5.echo("finalSolution = null");
            if (!DESTRUCTIVE_REORDER &&
                    (mode == MODE_ON_DROP || mode == MODE_ON_DROP_EXTERNAL)) {
                commitTempPlacement();
                completeAndClearReorderHintAnimations();
                setItemPlacementDirty(false);
            }
            foundSolution = false;
            result[0] = result[1] = resultSpan[0] = resultSpan[1] = -1;
        }

        if ((mode == MODE_ON_DROP || !foundSolution) && !DESTRUCTIVE_REORDER) {
            setUseTempCoords(false);
        }

//        mChildren.requestLayout();
        return result;
    }
    
    /* This seems like it should be obvious and straight-forward, but when the direction vector
    needs to match with the notion of the dragView pushing other views, we have to employ
    a slightly more subtle notion of the direction vector. The question is what two points is
    the vector between? The center of the dragView and its desired destination? Not quite, as
    this doesn't necessarily coincide with the interaction of the dragView and items occupying
    those cells. Instead we use some heuristics to often lock the vector to up, down, left
    or right, which helps make pushing feel right.
    */
    private void getDirectionVectorForDrop(int dragViewCenterX, int dragViewCenterY, int spanX,
            int spanY, XPagedViewItem dragView, int[] resultDirection) {
        int[] targetDestination = new int[2];

        findNearestArea(getCurrentPage(), dragViewCenterX, dragViewCenterY, spanX, spanY, null, false, targetDestination);
        Rect dragRect = new Rect();
        regionToRect(targetDestination[0], targetDestination[1], spanX, spanY, dragRect);
        dragRect.offset(dragViewCenterX - dragRect.centerX(), dragViewCenterY - dragRect.centerY());

        Rect dropRegionRect = new Rect();
        getViewsIntersectingRegion(targetDestination[0], targetDestination[1], spanX, spanY,
                dragView, dropRegionRect, mIntersectingViews);

        int dropRegionSpanX = dropRegionRect.width();
        int dropRegionSpanY = dropRegionRect.height();

        regionToRect(dropRegionRect.left, dropRegionRect.top, dropRegionRect.width(),
                dropRegionRect.height(), dropRegionRect);

        int deltaX = (dropRegionRect.centerX() - dragViewCenterX) / spanX;
        int deltaY = (dropRegionRect.centerY() - dragViewCenterY) / spanY;

        if (dropRegionSpanX == mCountX || spanX == mCountX) {
            deltaX = 0;
        }
        if (dropRegionSpanY == mCountY || spanY == mCountY) {
            deltaY = 0;
        }

        if (deltaX == 0 && deltaY == 0) {
            // No idea what to do, give a random direction.
            resultDirection[0] = 1;
            resultDirection[1] = 0;
        } else {
            computeDirectionVector(deltaX, deltaY, resultDirection);
        }
    }
    
    void regionToRect(int cellX, int cellY, int spanX, int spanY, Rect result) {
        final int hStartPadding = (int)localRect.left;
        final int vStartPadding = (int)localRect.top;
        final int left = hStartPadding + cellX * (mCellWidth + mWidthGap);
        final int top = vStartPadding + cellY * (mCellHeight + mHeightGap);
        result.set(left, top, left + (spanX * mCellWidth + (spanX - 1) * mWidthGap),
                top + (spanY * mCellHeight + (spanY - 1) * mHeightGap));
    }
    
    /*
     * Returns a pair (x, y), where x,y are in {-1, 0, 1} corresponding to vector between
     * the provided point and the provided cell
     */
    private void computeDirectionVector(float deltaX, float deltaY, int[] result) {
        double angle = Math.atan(((float) deltaY) / deltaX);

        result[0] = 0;
        result[1] = 0;
        if (Math.abs(Math.cos(angle)) > 0.5f) {
            result[0] = (int) Math.signum(deltaX);
        }
        if (Math.abs(Math.sin(angle)) > 0.5f) {
            result[1] = (int) Math.signum(deltaY);
        }
    }
    
    private class ItemConfiguration {
        HashMap<XPagedViewItem, CellAndSpan> map = new HashMap<XPagedViewItem, CellAndSpan>();
        boolean isSolution = false;
        int dragViewX, dragViewY, dragViewSpanX, dragViewSpanY;

        int area() {
            return dragViewSpanX * dragViewSpanY;
        }
    }
    
    private class CellAndSpan {
        int x, y;
        int spanX, spanY;

        public CellAndSpan(int x, int y, int spanX, int spanY) {
            this.x = x;
            this.y = y;
            this.spanX = spanX;
            this.spanY = spanY;
        }
    }
    
    ItemConfiguration simpleSwap(int pixelX, int pixelY, int minSpanX, int minSpanY, int spanX,
            int spanY, int[] direction, XPagedViewItem dragView, boolean decX, ItemConfiguration solution) {
        // Copy the current state into the solution. This solution will be manipulated as necessary.
        copyCurrentStateToSolution(solution, false, dragView);
        // Copy the current occupied array into the temporary occupied array. This array will be
        // manipulated as necessary to find a solution.
        if (!mOccuSaved)
        {
            mOccuSaved = true;
            copyOccupiedArray(mSavedOccupied);
        }   
        copyOccupiedArray(mTmpOccupied, mSavedOccupied);

        // We find the nearest cell into which we would place the dragged item, assuming there's
        // nothing in its way.
        int result[] = new int[2];
        result = findNearestArea(getCurrentPage(), pixelX, pixelY, spanX, spanY, null, false, result);

        boolean success = false;
        // First we try the exact nearest position of the item being dragged,
        // we will then want to try to move this around to other neighbouring positions
        success = rearrangementExists(result[0], result[1], spanX, spanY, direction, dragView,
                solution);

        if (!success) {
            // We try shrinking the widget down to size in an alternating pattern, shrink 1 in
            // x, then 1 in y etc.
//            if (spanX > minSpanX && (minSpanY == spanY || decX)) {
//                return simpleSwap(pixelX, pixelY, minSpanX, minSpanY, spanX - 1, spanY, direction,
//                        dragView, false, solution);
//            } else if (spanY > minSpanY) {
//                return simpleSwap(pixelX, pixelY, minSpanX, minSpanY, spanX, spanY - 1, direction,
//                        dragView, true, solution);
//            }
            solution.isSolution = false;
        } else {
            solution.isSolution = true;
            solution.dragViewX = result[0];
            solution.dragViewY = result[1];
            solution.dragViewSpanX = spanX;
            solution.dragViewSpanY = spanY;
        }
        return solution;
    }
    
    private void beginOrAdjustHintAnimations(ItemConfiguration solution, XPagedViewItem dragView, int delay) {
//        int childCount = mChildren.getChildCount();
//        for (int i = 0; i < childCount; i++) {
//            View child = mChildren.getChildAt(i);
//            if (child == dragView) continue;
//            CellAndSpan c = solution.map.get(child);
//            LayoutParams lp = (LayoutParams) child.getLayoutParams();
//            if (c != null) {
//                ReorderHintAnimation rha = new ReorderHintAnimation(child, lp.cellX, lp.cellY,
//                        c.x, c.y, c.spanX, c.spanY);
//                R5.echo("ReorderHintAnimation lp.cellX =" + lp.cellX + "lp.cellY = " + lp.cellY + "c.x =" +c.x + "c.y =" + c.y);
//                rha.animate();
//            }
//        }
        
        XPagedViewItem item;
        ItemInfo info;
        Set<Map.Entry<Long, XPagedViewItem>> set = xPagedView.mItemIDMap.entrySet();
        for (Map.Entry<Long, XPagedViewItem> map : set) {
            item = map.getValue();
            if (dragView == item)
            {
                continue;
            }
            info = item.getInfo();
            if (info.screen != getCurrentPage())
            {
                continue;
            }
            
            CellAndSpan c = solution.map.get(item);
            if (c != null) {
                
                if (info.cellX != c.x || info.cellY != c.y)
                {
                    
//                    xPagedView.removePagedViewItem(item);
//                    info.cellX = c.x;
//                    info.cellY = c.y;
//                    xPagedView.addPagedViewItem(item);
                    if (DEBUG_REORDER)R5.echo("ReorderHintAnimation lp.cellX =" + info.cellX + "lp.cellY = " + info.cellY + "c.x =" +c.x + "c.y =" + c.y);
                }
            }
            
        }
    }
    
    public void setUseTempCoords(boolean useTempCoords) {
//        int childCount = mChildren.getChildCount();
//        for (int i = 0; i < childCount; i++) {
//            LayoutParams lp = (LayoutParams) mChildren.getChildAt(i).getLayoutParams();
//            lp.useTmpCoords = useTempCoords;
//        }
    }
    
    ItemConfiguration findConfigurationNoShuffle(int pixelX, int pixelY, int minSpanX, int minSpanY,
            int spanX, int spanY, XPagedViewItem dragView, ItemConfiguration solution) {
        int[] result = new int[2];
        int[] resultSpan = new int[2];
//        findNearestVacantArea(pixelX, pixelY, minSpanX, minSpanY, spanX, spanY, null, result,
//                resultSpan);
        
        result = findNearestVacantArea(getCurrentPage(), pixelX, pixelY, spanX, spanY, null, result);
        resultSpan[0] = spanX;
        resultSpan[1] = spanY;
        if (result[0] >= 0 && result[1] >= 0) {
            copyCurrentStateToSolution(solution, false, dragView);
            solution.dragViewX = result[0];
            solution.dragViewY = result[1];
            solution.dragViewSpanX = resultSpan[0];
            solution.dragViewSpanY = resultSpan[1];
            solution.isSolution = true;
        } else {
            solution.isSolution = false;
        }
        return solution;
    }
    
    private boolean rearrangementExists(int cellX, int cellY, int spanX, int spanY, int[] direction,
            XPagedViewItem ignoreView, ItemConfiguration solution) {
        // Return early if get invalid cell positions
        if (cellX < 0 || cellY < 0) return false;

        mIntersectingViews.clear();
        mOccupiedRect.set(cellX, cellY, cellX + spanX, cellY + spanY);

        // Mark the desired location of the view currently being dragged.
        if (ignoreView != null) {
            CellAndSpan c = solution.map.get(ignoreView);
            if (c != null) {
                c.x = cellX;
                c.y = cellY;
            }
        }
        Rect r0 = new Rect(cellX, cellY, cellX + spanX, cellY + spanY);
        Rect r1 = new Rect();
        ItemInfo info;
        for (XPagedViewItem child: solution.map.keySet()) {
            if (child == ignoreView) continue;
            info = child.getInfo();
            if (info.screen != getCurrentPage())
            {
                continue;
            }
            CellAndSpan c = solution.map.get(child);
//            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            r1.set(c.x, c.y, c.x + c.spanX, c.y + c.spanY);
            if (Rect.intersects(r0, r1)) {
//                if (!lp.canReorder) {
//                    return false;
//                }
                mIntersectingViews.add(child);
            }
        }

        // First we try to find a solution which respects the push mechanic. That is, 
        // we try to find a solution such that no displaced item travels through another item
        // without also displacing that item.
        if (attemptPushInDirection(mIntersectingViews, mOccupiedRect, direction, ignoreView,
                solution)) {
            if (DEBUG_REORDER)R5.echo("attemptPushInDirection");
            return true;
        }

        // Next we try moving the views as a block, but without requiring the push mechanic.
        if (addViewsToTempLocation(mIntersectingViews, mOccupiedRect, direction, false, ignoreView,
                solution)) {
            if (DEBUG_REORDER)R5.echo("addViewsToTempLocation block");
            return true;
        }

        // Ok, they couldn't move as a block, let's move them individually
        for (XPagedViewItem v : mIntersectingViews) {
            if (!addViewToTempLocation(v, mOccupiedRect, direction, solution)) {
                return false;
            }
        }
        if (DEBUG_REORDER)R5.echo("addViewToTempLocation");
        return true;
    }
    
    // This method tries to find a reordering solution which satisfies the push mechanic by trying
    // to push items in each of the cardinal directions, in an order based on the direction vector
    // passed.
    private boolean attemptPushInDirection(ArrayList<XPagedViewItem> intersectingViews, Rect occupied,
            int[] direction, XPagedViewItem ignoreView, ItemConfiguration solution) {
        if ((Math.abs(direction[0]) + Math.abs(direction[1])) > 1) {
            // If the direction vector has two non-zero components, we try pushing 
            // separately in each of the components.
            int temp = direction[1];
            direction[1] = 0;
            if (addViewsToTempLocation(intersectingViews, occupied, direction, true,
                    ignoreView, solution)) {
                return true;
            }
            direction[1] = temp;
            temp = direction[0];
            direction[0] = 0;
            if (addViewsToTempLocation(intersectingViews, occupied, direction, true,
                    ignoreView, solution)) {
                return true;
            }
            // Revert the direction
            direction[0] = temp;

            // Now we try pushing in each component of the opposite direction
            direction[0] *= -1;
            direction[1] *= -1;
            temp = direction[1];
            direction[1] = 0;
            if (addViewsToTempLocation(intersectingViews, occupied, direction, true,
                    ignoreView, solution)) {
                return true;
            }

            direction[1] = temp;
            temp = direction[0];
            direction[0] = 0;
            if (addViewsToTempLocation(intersectingViews, occupied, direction, true,
                    ignoreView, solution)) {
                return true;
            }
            // revert the direction
            direction[0] = temp;
            direction[0] *= -1;
            direction[1] *= -1;
            
        } else {
            // If the direction vector has a single non-zero component, we push first in the
            // direction of the vector
            if (addViewsToTempLocation(intersectingViews, occupied, direction, true,
                    ignoreView, solution)) {
                return true;
            }

            // Then we try the opposite direction
            direction[0] *= -1;
            direction[1] *= -1;
            if (addViewsToTempLocation(intersectingViews, occupied, direction, true,
                    ignoreView, solution)) {
                return true;
            }
            // Switch the direction back
            direction[0] *= -1;
            direction[1] *= -1;
            
            // If we have failed to find a push solution with the above, then we try 
            // to find a solution by pushing along the perpendicular axis.

            // Swap the components
            int temp = direction[1];
            direction[1] = direction[0];
            direction[0] = temp;
            if (addViewsToTempLocation(intersectingViews, occupied, direction, true,
                    ignoreView, solution)) {
                return true;
            }

            // Then we try the opposite direction
            direction[0] *= -1;
            direction[1] *= -1;
            if (addViewsToTempLocation(intersectingViews, occupied, direction, true,
                    ignoreView, solution)) {
                return true;
            }
            // Switch the direction back
            direction[0] *= -1;
            direction[1] *= -1;

            // Swap the components back
            temp = direction[1];
            direction[1] = direction[0];
            direction[0] = temp;
        }
        return false;
    }
    
    private boolean addViewsToTempLocation(ArrayList<XPagedViewItem> views, Rect rectOccupiedByPotentialDrop,
            int[] direction, boolean push, XPagedViewItem dragView, ItemConfiguration currentState) {
        if (views.size() == 0) return true;

        boolean success = false;
        Rect boundingRect = null;
        // We construct a rect which represents the entire group of views passed in
        for (XPagedViewItem v: views) {
            CellAndSpan c = currentState.map.get(v);
            if (boundingRect == null) {
                boundingRect = new Rect(c.x, c.y, c.x + c.spanX, c.y + c.spanY);
            } else {
                boundingRect.union(c.x, c.y, c.x + c.spanX, c.y + c.spanY);
            }
        }

        @SuppressWarnings("unchecked")
        ArrayList<XPagedViewItem> dup = (ArrayList<XPagedViewItem>) views.clone();
        // We try and expand the group of views in the direction vector passed, based on
        // whether they are physically adjacent, ie. based on "push mechanics".
        while (push && addViewInDirection(dup, boundingRect, direction, mTmpOccupied, dragView,
                currentState)) {
        }

        // Mark the occupied state as false for the group of views we want to move.
        for (XPagedViewItem v: dup) {
            CellAndSpan c = currentState.map.get(v);
            markCellsForView(c.x, c.y, c.spanX, c.spanY, mTmpOccupied, false);
        }

        boolean[][] blockOccupied = new boolean[boundingRect.width()][boundingRect.height()];
        int top = boundingRect.top;
        int left = boundingRect.left;
        // We mark more precisely which parts of the bounding rect are truly occupied, allowing
        // for tetris-style interlocking.
        for (XPagedViewItem v: dup) {
            CellAndSpan c = currentState.map.get(v);
            markCellsForView(c.x - left, c.y - top, c.spanX, c.spanY, blockOccupied, true);
        }

        markCellsForRect(rectOccupiedByPotentialDrop, mTmpOccupied, true);

        if (push) {
            findNearestAreaInDirection(boundingRect.left, boundingRect.top, boundingRect.width(),
                    boundingRect.height(), direction, mTmpOccupied, blockOccupied, mTempLocation);
        } else {
            findNearestArea(boundingRect.left, boundingRect.top, boundingRect.width(),
                    boundingRect.height(), direction, mTmpOccupied, blockOccupied, mTempLocation);
        }

        // If we successfuly found a location by pushing the block of views, we commit it
        if (mTempLocation[0] >= 0 && mTempLocation[1] >= 0) {
            int deltaX = mTempLocation[0] - boundingRect.left;
            int deltaY = mTempLocation[1] - boundingRect.top;
            for (XPagedViewItem v: dup) {
                CellAndSpan c = currentState.map.get(v);
                c.x += deltaX;
                c.y += deltaY;
            }
            success = true;
        }

        // In either case, we set the occupied array as marked for the location of the views
        for (XPagedViewItem v: dup) {
            CellAndSpan c = currentState.map.get(v);
            markCellsForView(c.x, c.y, c.spanX, c.spanY, mTmpOccupied, true);
        }
        return success;
    }
    
    // This method looks in the specified direction to see if there is an additional view
    // immediately adjecent in that direction
    private boolean addViewInDirection(ArrayList<XPagedViewItem> views, Rect boundingRect, int[] direction,
            boolean[][] occupied, XPagedViewItem dragView, ItemConfiguration currentState) {
        boolean found = false;

//        int childCount = mChildren.getChildCount();
        Rect r0 = new Rect(boundingRect);
        Rect r1 = new Rect();

        int deltaX = 0;
        int deltaY = 0;
        if (direction[1] < 0) {
            r0.set(r0.left, r0.top - 1, r0.right, r0.bottom);
            deltaY = -1;
        } else if (direction[1] > 0) {
            r0.set(r0.left, r0.top, r0.right, r0.bottom + 1);
            deltaY = 1;
        } else if (direction[0] < 0) {
            r0.set(r0.left - 1, r0.top, r0.right, r0.bottom);
            deltaX = -1;
        } else if (direction[0] > 0) {
            r0.set(r0.left, r0.top, r0.right + 1, r0.bottom);
            deltaX = 1;
        }

//        for (int i = 0; i < childCount; i++) {
//            View child = mChildren.getChildAt(i);
//            if (views.contains(child) || child == dragView) continue;
//            CellAndSpan c = currentState.map.get(child);
//
//            LayoutParams lp = (LayoutParams) child.getLayoutParams();
//            r1.set(c.x, c.y, c.x + c.spanX, c.y + c.spanY);
//            if (Rect.intersects(r0, r1)) {
//                if (!lp.canReorder) {
//                    return false;
//                }
//                boolean pushed = false;
//                for (int x = c.x; x < c.x + c.spanX; x++) {
//                    for (int y = c.y; y < c.y + c.spanY; y++) {
//                        boolean inBounds = x - deltaX >= 0 && x -deltaX < mCountX
//                                && y - deltaY >= 0 && y - deltaY < mCountY;
//                        if (inBounds && occupied[x - deltaX][y - deltaY]) {
//                            pushed = true;
//                        }
//                    }
//                }
//                if (pushed) {
//                    views.add(child);
//                    boundingRect.union(c.x, c.y, c.x + c.spanX, c.y + c.spanY);
//                    found = true;
//                }
//            }
//        }
        
        XPagedViewItem item;
        ItemInfo info;
        Set<Map.Entry<Long, XPagedViewItem>> set = xPagedView.mItemIDMap.entrySet();
        for (Map.Entry<Long, XPagedViewItem> map : set) {
            item = map.getValue();
            if (dragView == item)
            {
                continue;
            }
            info = item.getInfo();
            if (info.screen != getCurrentPage())
            {
                continue;
            }
            
            if (views.contains(item))
            {
                continue;
            }
            CellAndSpan c = currentState.map.get(item);

//            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            r1.set(c.x, c.y, c.x + c.spanX, c.y + c.spanY);
            if (Rect.intersects(r0, r1)) {
//                if (!lp.canReorder) {
//                    return false;
//                }
                boolean pushed = false;
                for (int x = c.x; x < c.x + c.spanX; x++) {
                    for (int y = c.y; y < c.y + c.spanY; y++) {
                        boolean inBounds = x - deltaX >= 0 && x -deltaX < mCountX
                                && y - deltaY >= 0 && y - deltaY < mCountY;
                        if (inBounds && occupied[x - deltaX][y - deltaY]) {
                            pushed = true;
                        }
                    }
                }
                if (pushed) {
                    views.add(item);
                    boundingRect.union(c.x, c.y, c.x + c.spanX, c.y + c.spanY);
                    found = true;
                }
            }
        }
        return found;
    }
    
    private int[] findNearestAreaInDirection(int cellX, int cellY, int spanX, int spanY, 
            int[] direction,boolean[][] occupied,
            boolean blockOccupied[][], int[] result) {
        // Keep track of best-scoring drop area
        final int[] bestXY = result != null ? result : new int[2];
        bestXY[0] = -1;
        bestXY[1] = -1;
        float bestDistance = Float.MAX_VALUE;

        // We use this to march in a single direction
        if ((direction[0] != 0 && direction[1] != 0) ||
                (direction[0] == 0 && direction[1] == 0)) {
            return bestXY;
        }

        // This will only incrememnet one of x or y based on the assertion above
        int x = cellX + direction[0];
        int y = cellY + direction[1];
        while (x >= 0 && x + spanX <= mCountX && y >= 0 && y + spanY <= mCountY) {

            boolean fail = false;
            for (int i = 0; i < spanX; i++) {
                for (int j = 0; j < spanY; j++) {
                    if (occupied[x + i][y + j] && (blockOccupied == null || blockOccupied[i][j])) {
                        fail = true;                    
                    }
                }
            }
            if (!fail) {
                float distance = (float)
                        Math.sqrt((x - cellX) * (x - cellX) + (y - cellY) * (y - cellY));
                if (Float.compare(distance,  bestDistance) < 0) {
                    bestDistance = distance;
                    bestXY[0] = x;
                    bestXY[1] = y;
                }
            }
            x += direction[0];
            y += direction[1];
        }
        return bestXY;
    }
    
    private void copyCurrentStateToSolution(ItemConfiguration solution, boolean temp, XPagedViewItem dragView) {
//        int childCount = mChildren.getChildCount();
//        for (int i = 0; i < childCount; i++) {
//            View child = mChildren.getChildAt(i);
//            LayoutParams lp = (LayoutParams) child.getLayoutParams();
//            CellAndSpan c;
//            if (temp) {
//                c = new CellAndSpan(lp.tmpCellX, lp.tmpCellY, lp.cellHSpan, lp.cellVSpan);
//            } else {
//                c = new CellAndSpan(lp.cellX, lp.cellY, lp.cellHSpan, lp.cellVSpan);
//            }
//            solution.map.put(child, c);
//        }
        
        XPagedViewItem item;
        ItemInfo info;
        Set<Map.Entry<Long, XPagedViewItem>> set = xPagedView.mItemIDMap.entrySet();
        for (Map.Entry<Long, XPagedViewItem> map : set) {
            item = map.getValue();
            info = item.getInfo();
            if (!mSaved)
            {
                info.tmpCellX = info.cellX;
                info.tmpCellY = info.cellY;
            }
            
            if (dragView == item)
            {
                continue;
            }
            
            if (info.screen != getCurrentPage())
            {
                continue;
            }
            CellAndSpan c;
            
            if (temp) {
                //todo
//                c = new CellAndSpan(lp.tmpCellX, lp.tmpCellY, lp.cellHSpan, lp.cellVSpan);
                c = new CellAndSpan(info.cellX, info.cellY, info.spanX, info.spanY);
            } else {
//                c = new CellAndSpan(info.cellX, info.cellY, info.spanX, info.spanY);
                c = new CellAndSpan(info.tmpCellX, info.tmpCellY, info.spanX, info.spanY);
            }
            
            solution.map.put(item, c);
        }
        
        mSaved = true;
    }
    
    private void copyOccupiedArray(boolean[][] occupied) {
        for (int i = 0; i < mCountX; i++) {
            for (int j = 0; j < mCountY; j++) {
                occupied[i][j] = xPagedView.mOccupied[getCurrentPage()][i][j];
            }
        }
    }
    
    private void copyOccupiedArray(boolean[][] occupied, boolean[][] fromOccupied) {
        for (int i = 0; i < mCountX; i++) {
            for (int j = 0; j < mCountY; j++) {
                occupied[i][j] = fromOccupied[i][j];
            }
        }
    }
    
    private void markCellsForView(int cellX, int cellY, int spanX, int spanY, boolean[][] occupied,
            boolean value) {
        if (cellX < 0 || cellY < 0) return;
        for (int x = cellX; x < cellX + spanX && x < mCountX; x++) {
            for (int y = cellY; y < cellY + spanY && y < mCountY; y++) {
                occupied[x][y] = value;
//                R5.echo("markCellsForView x = " + x + "y =" + y + "value = " + value);
            }
        }
    }
    
    private void markCellsForRect(Rect r, boolean[][] occupied, boolean value) {
        markCellsForView(r.left, r.top, r.width(), r.height(), occupied, value);
    }
    
    /**
     * Find a vacant area that will fit the given bounds nearest the requested
     * cell location, and will also weigh in a suggested direction vector of the
     * desired location. This method computers distance based on unit grid distances,
     * not pixel distances.
     *
     * @param cellX The X cell nearest to which you want to search for a vacant area.
     * @param cellY The Y cell nearest which you want to search for a vacant area.
     * @param spanX Horizontal span of the object.
     * @param spanY Vertical span of the object.
     * @param direction The favored direction in which the views should move from x, y
     * @param exactDirectionOnly If this parameter is true, then only solutions where the direction
     *        matches exactly. Otherwise we find the best matching direction.
     * @param occoupied The array which represents which cells in the CellLayout are occupied
     * @param blockOccupied The array which represents which cells in the specified block (cellX,
     *        cellY, spanX, spanY) are occupied. This is used when try to move a group of views. 
     * @param result Array in which to place the result, or null (in which case a new array will
     *        be allocated)
     * @return The X, Y cell of a vacant area that can contain this object,
     *         nearest the requested location.
     */
    private int[] findNearestArea(int cellX, int cellY, int spanX, int spanY, int[] direction,
            boolean[][] occupied, boolean blockOccupied[][], int[] result) {
        // Keep track of best-scoring drop area
        final int[] bestXY = result != null ? result : new int[2];
        float bestDistance = Float.MAX_VALUE;
        int bestDirectionScore = Integer.MIN_VALUE;

        final int countX = mCountX;
        final int countY = mCountY;

        for (int y = 0; y < countY - (spanY - 1); y++) {
            inner:
            for (int x = 0; x < countX - (spanX - 1); x++) {
                // First, let's see if this thing fits anywhere
                for (int i = 0; i < spanX; i++) {
                    for (int j = 0; j < spanY; j++) {
                        if (occupied[x + i][y + j] && (blockOccupied == null || blockOccupied[i][j])) {
                            continue inner;
                        }
                    }
                }

                float distance = (float)
                        Math.sqrt((x - cellX) * (x - cellX) + (y - cellY) * (y - cellY));
                int[] curDirection = mTmpPoint;
                computeDirectionVector(x - cellX, y - cellY, curDirection);
                // The direction score is just the dot product of the two candidate direction
                // and that passed in.
                int curDirectionScore = direction[0] * curDirection[0] +
                        direction[1] * curDirection[1];
                boolean exactDirectionOnly = false;
                boolean directionMatches = direction[0] == curDirection[0] &&
                        direction[1] == curDirection[1];
                if ((directionMatches || !exactDirectionOnly) &&
                        Float.compare(distance,  bestDistance) < 0 || (Float.compare(distance,
                        bestDistance) == 0 && curDirectionScore > bestDirectionScore)) {
                    bestDistance = distance;
                    bestDirectionScore = curDirectionScore;
                    bestXY[0] = x;
                    bestXY[1] = y;
                }
            }
        }

        // Return -1, -1 if no suitable location found
        if (bestDistance == Float.MAX_VALUE) {
            bestXY[0] = -1;
            bestXY[1] = -1;
        }
        return bestXY;
    }
    
    private void animateItemsToSolution(ItemConfiguration solution, XPagedViewItem dragView, boolean
            commitDragView) {

        boolean[][] occupied = DESTRUCTIVE_REORDER ? xPagedView.mOccupied[getCurrentPage()] : mTmpOccupied;
        if (DEBUG_REORDER)R5.echo("mTmpOccupied cleared");
        for (int i = 0; i < mCountX; i++) {
            for (int j = 0; j < mCountY; j++) {
                occupied[i][j] = false;
            }
        }

//        int childCount = mChildren.getChildCount();
//        for (int i = 0; i < childCount; i++) {
//            View child = mChildren.getChildAt(i);
//            if (child == dragView) continue;
//            CellAndSpan c = solution.map.get(child);
//            if (c != null) {
//                animateChildToPosition(child, c.x, c.y, REORDER_ANIMATION_DURATION, 0,
//                        DESTRUCTIVE_REORDER, false);
//                markCellsForView(c.x, c.y, c.spanX, c.spanY, occupied, true);
//            }
//        }
        
        XPagedViewItem item;
        ItemInfo info;
        Rect r0 = new Rect();
        if (mDragInfo != null)
        {
            r0.set(mDragInfo.cellX, mDragInfo.cellY, mDragInfo.cellX + mDragInfo.spanX, mDragInfo.cellY + mDragInfo.spanY);
        }
//        Rect r1 = new Rect();
        HashMap<Long, XPagedViewItem> clone = null;
        synchronized (xPagedView.mItemIDMap) {
			clone = (HashMap<Long, XPagedViewItem>)xPagedView.mItemIDMap.clone();
		}
       
        Set<Map.Entry<Long, XPagedViewItem>> set = clone.entrySet();
        // now test
        HashMap<CellAndSpan, XPagedViewItem> movingPairs = 
        		new HashMap<XWorkspace.CellAndSpan, XPagedViewItem>();
        
        //DEBUG
//        android.util.Log.i( "D2", "\n begin -------------------------------------" );
        for (Map.Entry<Long, XPagedViewItem> map : set) {
        	
            item = map.getValue();
            if (dragView == item)
            {
                continue;
            }
            info = item.getInfo();
            if (info.screen != getCurrentPage())
            {
                continue;
            }            
            
            CellAndSpan c = solution.map.get(item);
            if (c != null) {
//                if (!dragHasRemoved)
//                {
//                    r1.set(c.x, c.y, c.x + c.spanX, c.y + c.spanY);
//                    if (Rect.intersects(r0, r1))
//                    {
//                        if (DEBUG_REORDER)R5.echo("dragHasRemoved = " + true);
//                        xPagedView.removePagedViewItem(mDragView);
//                        dragHasRemoved = true;
//                    }
//                }                
                
                
                if (info.cellX !=  c.x || info.cellY != c.y)
                {
                    if (DEBUG_REORDER)R5.echo("animateChildToPosition child = " + item + "fromX  = " + info.cellX  + "fromY = " + info.cellY  + "fromScreen = " + info.screen
                            + "cellX =" + c.x + "cellY = " + c.y + "c.spanX = " + c.spanX + "c.spanY" + c.spanY);
//                    markCellsForView(info.cellX, info.cellX, c.spanX, c.spanY, occupied, false);
//                    animateChildToPosition(item, c.x, c.y, REORDER_ANIMATION_DURATION, 0,
//                            DESTRUCTIVE_REORDER, false);
                    
                    // now test
                    movingPairs.put(c, item);
                 }   
                markCellsForView(c.x, c.y, c.spanX, c.spanY, occupied, true);
                
            }
        }
        
        //D2
    	//DEBUG
		// arrange orientation for unit
 //       android.util.Log.i( "D2", "now we size is : " + movingPairs.size() );
        int control = 0;
        int limit = movingPairs.size();
		while (!movingPairs.isEmpty() && control < limit) {
			Set<Entry<CellAndSpan, XPagedViewItem>> entries = movingPairs.entrySet();
			Iterator<Entry<CellAndSpan, XPagedViewItem>> itr = entries.iterator();
			List<CellAndSpan> toRemove = new ArrayList<CellAndSpan>();

			while (itr.hasNext()) {
				final CellAndSpan c = itr.next().getKey();
//				android.util.Log.i("D2", "it is : "
//						+ movingPairs.get(c).getId());
				final XPagedViewItem itemLocal = movingPairs.get(c);
				if (xPagedView.hasSpaceForItemAt(itemLocal,
						itemLocal.getInfo().screen, c.x, c.y)) {
//					android.util.Log.i("D2",
//							"has space for : " + itemLocal.getId());
					animateChildToPosition(itemLocal, getCurrentPage(), c.x, c.y,
							REORDER_ANIMATION_DURATION, 0, DESTRUCTIVE_REORDER,
							false);
					//FIXME
					markCellsForView(c.x, c.y, c.spanX, c.spanY, occupied, true);
					//FIXME
//					android.util.Log.i("D2", "Over Moving .now remove :  " + itemLocal.getId());
					toRemove.add(c);
				}
				else
					R5.echo("NO space for : " + itemLocal.getId() + "  .. PENDINg ................");

			}
			
			control ++;
			
			for(CellAndSpan cin : toRemove){
				movingPairs.remove(cin);
				control = 0;
				limit = movingPairs.size();
			}		
			
		}
		
	    if (movingPairs.size() != 0)
        {
            R5.echo("animateItemsToSolution failure size = " + movingPairs.size());
        }
    	
//    	android.util.Log.i( "D2", " END -------------------------------------\n" );
    	//2D
    	
//        mOccupiedForMove = mTmpOccupied.clone();
//        if (commitDragView) {
//            markCellsForView(solution.dragViewX, solution.dragViewY, solution.dragViewSpanX,
//                    solution.dragViewSpanY, occupied, true);
//        }
    }
    
    //add by zhanggx1 for reordering all pages on 2013-11-20. s
    public boolean animateChildToPosition(final XPagedViewItem child, int screen, int cellX, int cellY, int duration,
            int delay, boolean permanent, boolean adjustOccupied) {
          return animateChildToPosition(child, screen, cellX, cellY, duration,
                  delay, permanent, adjustOccupied, null);
    }
    
    public boolean animateChildToPosition(final XPagedViewItem child, int screen, int cellX, int cellY, int duration,
            int delay, boolean permanent, boolean adjustOccupied, Runnable taskRunnable) {
          return xPagedView.moveItemToPosition(child, cellX, cellY, screen, duration, delay, taskRunnable, mTmpOccupied, true);
    }
    //add by zhanggx1 for reordering all pages on 2013-11-20. e
    
    private boolean addViewToTempLocation(XPagedViewItem v, Rect rectOccupiedByPotentialDrop,
            int[] direction, ItemConfiguration currentState) {
        CellAndSpan c = currentState.map.get(v);
        boolean success = false;
        markCellsForView(c.x, c.y, c.spanX, c.spanY, mTmpOccupied, false);
        markCellsForRect(rectOccupiedByPotentialDrop, mTmpOccupied, true);

        findNearestArea(c.x, c.y, c.spanX, c.spanY, direction, mTmpOccupied, null, mTempLocation);

        if (mTempLocation[0] >= 0 && mTempLocation[1] >= 0) {
            c.x = mTempLocation[0];
            c.y = mTempLocation[1];
            success = true;

        }
        markCellsForView(c.x, c.y, c.spanX, c.spanY, mTmpOccupied, true);
        return success;
    }
    
    private void copySolutionToTempState(ItemConfiguration solution, XPagedViewItem dragView) {
        for (int i = 0; i < mCountX; i++) {
            for (int j = 0; j < mCountY; j++) {
                mTmpOccupied[i][j] = false;
            }
        }
        
        if (DEBUG_REORDER)R5.echo("copySolutionToTempState mTmpOccupied cleared");

        XPagedViewItem item;
        ItemInfo info;
        Set<Map.Entry<Long, XPagedViewItem>> set = xPagedView.mItemIDMap.entrySet();
        for (Map.Entry<Long, XPagedViewItem> map : set) {
            item = map.getValue();
            if (dragView == item)
            {
                continue;
            }
            info = item.getInfo();
            if (info.screen != getCurrentPage())
            {
                continue;
            }
            CellAndSpan c = solution.map.get(item);
            if (c != null) {   
//                info.cellX = c.x;
//                info.cellY = c.y;
                markCellsForView(c.x, c.y, c.spanX, c.spanY, mTmpOccupied, true);
            }
        }
        
        markCellsForView(solution.dragViewX, solution.dragViewY, solution.dragViewSpanX,
                solution.dragViewSpanY, mTmpOccupied, true);
    }
    private boolean mItemPlacementDirty = false;
    void setItemPlacementDirty(boolean dirty) {
        mItemPlacementDirty = dirty;
    }
    boolean isItemPlacementDirty() {
        return mItemPlacementDirty;
    }
    
    private void commitTempPlacement() {
        if (DEBUG_REORDER)R5.echo("commitTempPlacement");
        //FIXME  -- START
        for (int i = 0; i < mCountX; i++) {
            for (int j = 0; j < mCountY; j++) {
                xPagedView.mOccupied[getCurrentPage()][i][j] = mTmpOccupied[i][j];
            }
        }
        //FIXME  -- END
        
//        XPagedViewItem item;
//        ItemInfo info;
//        for (Long itemId : xPagedView.mItemIDMap.keySet()) {
//            item =  xPagedView.mItemIDMap.get(itemId);
//            info = item.getInfo();
//            if (info.screen != getCurrentPage())
//            {
//                continue;
//            }
//            CellAndSpan c = solution.map.get(item);
//            if (c != null) {                
//                markCellsForView(c.x, c.y, c.spanX, c.spanY, mTmpOccupied, true);
//            }
//        }
//        
//        int childCount = mChildren.getChildCount();
//        for (int i = 0; i < childCount; i++) {
//            View child = mChildren.getChildAt(i);
//            LayoutParams lp = (LayoutParams) child.getLayoutParams();
//            ItemInfo info = (ItemInfo) child.getTag();
//            // We do a null check here because the item info can be null in the case of the
//            // AllApps button in the hotseat.
//            if (info != null) {
//                info.cellX = lp.cellX = lp.tmpCellX;
//                info.cellY = lp.cellY = lp.tmpCellY;
//                info.spanX = lp.cellHSpan;
//                info.spanY = lp.cellVSpan;
//            }
//        }
        mSaved = false;
        mOccuSaved = false;
        updateItemLocationsInDatabase();
    }
    
    void updateItemLocationsInDatabase() {
        int container = LauncherSettings.Favorites.CONTAINER_DESKTOP;
        
        XPagedViewItem item;
        ItemInfo info;
        Set<Map.Entry<Long, XPagedViewItem>> set = xPagedView.mItemIDMap.entrySet();
        for (Map.Entry<Long, XPagedViewItem> map : set) {
            item = map.getValue();
            info = item.getInfo();
            if (info.screen != getCurrentPage())
            {
                continue;
            }
            //if (info != null) {
                
                XLauncherModel.modifyItemInDatabase(mXContext.getContext(), info, container, getCurrentPage(), info.cellX,
                        info.cellY, info.spanX, info.spanY);
                
//                if (info != mDragInfo && )
//                {
//                    
//                }
                
            //}
        }
        
    }
    
//    boolean dragHasRemoved = false;
    XPagedViewItem mDragView;
    
//    public void removeDragView() {
//        if (DEBUG_REORDER)R5.echo("removeDragView " + dragHasRemoved);
//        if (dragHasRemoved)
//        {
////            xPagedView.removePagedViewItem(mDragView);
//            dragHasRemoved = false;
//        }
//        else
//        {
//            if (mTargetCell != null && mTargetCell[0] >= 0 && mTargetCell[1] >= 0) {
////                removePagedViewItem(mDragInfo);
//            } else {
////                XPagedViewItem cell = xPagedView.findPageItemAt(mDragInfo.screen, mDragInfo.cellX, mDragInfo.cellY);
////                if (cell == null) {
////                    return;
////                }
////                DrawableItem last = cell.getDrawingTarget();
////                if (last == null) {
////                    return;
////                }
////                last.setVisibility(true);
//                xPagedView.addPagedViewItem(mDragView);
//                DrawableItem last = mDragView.getDrawingTarget();
//                if (last != null) {
//                    last.resetPressedState();
//                }
//            }                   
//            mDragInfo = null;
//        }            
//    }

    public void removeItems(ArrayList<ApplicationInfo> apps) {

        final HashSet<ComponentName> ComponentNames = new HashSet<ComponentName>();
        final int appCount = apps.size();
        
        /*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. S***/
        for (int i = 0; i < appCount; i++) {
        	if( apps.get(i).componentName != null){
        		ComponentName componentNames = new ComponentName(apps.get(i).componentName.getPackageName()
        				,apps.get(i).componentName.getClassName());
        		ComponentNames.add(componentNames);
        	}
        }
        if( ComponentNames.size() <= 0 ){
        	return;
        }
        /*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. E***/

        // Avoid ANRs by treating each screen separately
        //remove的时候 shortcut按包名全部删除 applicaion按类名删除
        getXContext().post(new Runnable() {
            public void run() {
                final ArrayList<XPagedViewItem> childrenToRemove = new ArrayList<XPagedViewItem>();
                childrenToRemove.clear();
                
                xPagedView.removeItems(ComponentNames, childrenToRemove,true);
                //old
           //     xPagedView.removeItems(ComponentNames, childrenToRemove,false);

                int childCount = childrenToRemove.size();
                int pageCount = xPagedView.getPageCount();
                boolean[] screensToRemove = new boolean[pageCount];
                //add by zhanggx1 for refresh mng view.s
    			List<Integer> screenList = new ArrayList<Integer>();
    			//add by zhanggx1 for refresh mng view.e
                for (int j = 0; j < childCount; j++) {
                    XPagedViewItem child = childrenToRemove.get(j);

                    // Note: We can not remove the view directly from CellLayoutChildren as this
                    // does not re-mark the spaces as unoccupied.
                    xPagedView.removePagedViewItem(child);
                    if (child instanceof XDropTarget) {
                        mDragController.removeDropTarget((XDropTarget) child);
                    }

                    int screen = child.getInfo().screen;                    
                    if (screen < pageCount) {
                        screensToRemove[screen] = true;
                        //add by zhanggx1 for refresh mng view.s
                        if (!screenList.contains(screen)) {
                        	screenList.add(screen);
                        }                        
        				//add by zhanggx1 for refresh mng view.e
                    }
                }
              //add by zhanggx1 for refresh mng view.s
                ((XLauncher)getXContext().getContext()).refreshMngViewOnUpdateWorkspace(screenList);
              //add by zhanggx1 for refresh mng view.e

                if (childCount > 0) {
                    xPagedView.invalidate();
                    Context c = getXContext().getContext();
                    if (c instanceof XLauncher) {
                        updateWorkspaceThumb((XLauncher)c, screensToRemove);                      
                    }
                } // end childCount > 0
            }
        }); // end Runnable.

    }
    
    private void cleanupReorder(boolean cancelAlarm) {
        // Any pending reorders are canceled
        if (cancelAlarm) {
            mReorderAlarm.cancelAlarm();
            mReorderAlarm.setOnAlarmListener(null);
            if (mReorderAlarmListener != null)
            {
//                mReorderAlarmListener.removeRunnable();
                mReorderAlarmListener = null;
            }            
        }
        mLastReorderX = -1;
        mLastReorderY = -1;        
    }
    
  //add for quick drag mode by sunzq3, begin;
    public XPagedViewIndicator getPageIndicator()
	{
		return xPageIndicator;
	}
  //add for quick drag mode by sunzq3, end;
    
    void setDragMode(int dragMode) {
        if (dragMode != mDragMode) {
            if (dragMode == DRAG_MODE_NONE) {
//                cleanupAddToFolder();
                // We don't want to cancel the re-order alarm every time the target cell changes
                // as this feels to slow / unresponsive.
                cleanupReorder(false);
                cleanupFolderCreation();
                cleanupAddToFolder();
            } else if (dragMode == DRAG_MODE_ADD_TO_FOLDER) {
                cleanupReorder(true);
                cleanupFolderCreation();
            } else if (dragMode == DRAG_MODE_CREATE_FOLDER) {
                cleanupAddToFolder();
                cleanupReorder(true);
            } else if (dragMode == DRAG_MODE_REORDER) {
                cleanupAddToFolder();
                cleanupFolderCreation();
            }
            mDragMode = dragMode;
        }
    }
    
    private void cleanupFolderCreation() {
        if (DEBUG_REORDER)R5.echo("cleanupFolderCreation");
        if (mDragFolderRingAnimator != null) {
            mDragFolderRingAnimator.toNaturalState();
        }
        mFolderCreationAlarm.cancelAlarm();
//        mXContext.getRenderer().getEventHandler().removeCallbacks(mFolderCreationRunnable);
//        mHandler.removeMessages(MSG_OPEN_FOLDER);
//        mHandler.removeMessages(MSG_CLOSE_FOLDER);
    }
    
    void revertTempState() {
        if (!isItemPlacementDirty() || DESTRUCTIVE_REORDER) return;
//        final int count = mChildren.getChildCount();
//        for (int i = 0; i < count; i++) {
//            View child = mChildren.getChildAt(i);
//            LayoutParams lp = (LayoutParams) child.getLayoutParams();
//            if (lp.tmpCellX != lp.cellX || lp.tmpCellY != lp.cellY) {
//                lp.tmpCellX = lp.cellX;
//                lp.tmpCellY = lp.cellY;
//                animateChildToPosition(child, lp.cellX, lp.cellY, REORDER_ANIMATION_DURATION,
//                        0, false, false);
//            }
//        }
        XPagedViewItem item;
        ItemInfo info;
        HashMap<Long, XPagedViewItem> clone = null;
        synchronized ( xPagedView.mItemIDMap ) {
        	clone = (HashMap<Long, XPagedViewItem>)xPagedView.mItemIDMap.clone();			
		}
        Set<Map.Entry<Long, XPagedViewItem>> set = clone.entrySet();
        
        // now test
        ArrayList<XPagedViewItem> movingPairs = new ArrayList<XPagedViewItem>();
        
        for (Map.Entry<Long, XPagedViewItem> map : set) {
            item = map.getValue();
//            if (dragView == item)
//            {
//                continue;
//            }
            info = item.getInfo();
            if (info.screen != getCurrentPage())
            {
                continue;
            }
                
            if (info.cellX !=  info.tmpCellX || info.cellY != info.tmpCellY)
            {        
                
                if (DEBUG_REORDER)R5.echo("animateChildToPosition child = " + item + "screen = " + info.screen + "fromX  = " + info.cellX  + "fromY = " + info.cellY
                        + "cellX =" + info.tmpCellX + "cellY = " + info.tmpCellY + "c.spanX = " + info.spanX + "c.spanY" + info.spanY);
//                    markCellsForView(info.cellX, info.cellX, c.spanX, c.spanY, occupied, false);
//                animateChildToPosition(item, info.tmpCellX, info.tmpCellY, REORDER_ANIMATION_DURATION, 0,
//                        DESTRUCTIVE_REORDER, false);
             // now test
               movingPairs.add(item);
                
            }   

//            markCellsForView(c.x, c.y, c.spanX, c.spanY, occupied, true);
        }
        
        // test revert
        int control = 0;
        int limit = movingPairs.size();
		while (!movingPairs.isEmpty() && control < limit) {
			// arrange orientation for unit
			List<XPagedViewItem> toRemove = new ArrayList<XPagedViewItem>();
			for (XPagedViewItem itemLocal : movingPairs) {
				ItemInfo infoLocal = itemLocal.getInfo();
				if (xPagedView.hasSpaceForItemAt(itemLocal, infoLocal.screen,
						infoLocal.tmpCellX, infoLocal.tmpCellY)) {
					animateChildToPosition(itemLocal, infoLocal.screen, infoLocal.tmpCellX,
							infoLocal.tmpCellY, REORDER_ANIMATION_DURATION, 0,
							DESTRUCTIVE_REORDER, false);
					toRemove.add(itemLocal);
//					D2.echo("in 1");
				}
				
//				D2.echo("in 2");
			}

			control ++;
			
			for (XPagedViewItem cin : toRemove) {
				movingPairs.remove(cin);
				control = 0;
				limit = movingPairs.size();
			}						
			
//			D2.echo("in 3");
		}
		
		if (movingPairs.size() != 0)
        {
            R5.echo("animateItemsToSolution failure size = " + movingPairs.size());
        }
        
        if (DEBUG_REORDER)R5.echo("need revertTempState");
        
        completeAndClearReorderHintAnimations();
        setItemPlacementDirty(false);
    }
    
    protected void updateWorkspaceThumb(XLauncher launcher, boolean[] screensToRemove) {
        int length = screensToRemove.length;
        for (int i = 0; i < length; i++) {
            if (screensToRemove[i])
                launcher.updateWorkspaceThumb(i);
        }
    }
    
    private void completeAndClearReorderHintAnimations() {
//        for (ReorderHintAnimation a: mShakeAnimators.values()) {
//            a.completeAnimationImmediately();
//        }
//        mShakeAnimators.clear();
    }
    
    boolean mSaved = false;
    boolean mOccuSaved = false;    
    
    void setCurrentDropOverCell(int x, int y) {
        if (x != mDragOverX || y != mDragOverY) {
            mDragOverX = x;
            mDragOverY = y;
            if (DEBUG_REORDER)R5.echo("mDragOverX = " + mDragOverX + "mDragOverY = " + mDragOverY);
            setDragMode(DRAG_MODE_NONE);
        }
    }
    
    private int mDragOverX = -1;
    private int mDragOverY = -1;

    @Override
    public int getScrollLeftPadding() {
        // TODO Auto-generated method stub
//        return (int)localRect.left;
    	return 0;
    }
    
    public float getDistanceFromCell(float x, float y, int[] cell) {
        cellToCenterPoint(cell[0], cell[1], mTmpPoint);
        float distance = (float) Math.sqrt( Math.pow(x - mTmpPoint[0], 2) +
                Math.pow(y - mTmpPoint[1], 2));
//        R5.echo("x = " + x + "y = " + y + "distance = " + distance);
        
        return distance;
    }
    
    int mCellX;
    int mCellY;
    class FolderCreationAlarmListener implements OnAlarmListener {


        public FolderCreationAlarmListener(int cellX, int cellY) {
            mCellX = cellX;
            mCellY = cellY;
        }

        public void onAlarm(Alarm alarm) {
//            mXContext.getRenderer().getEventHandler().post(mFolderCreationRunnable);
//            mXContext.getRenderer().invalidate();
            if (mDragFolderRingAnimator == null) {
                mDragFolderRingAnimator = new FolderRingAnimator(mXContext, null);
            }
            mDragFolderRingAnimator.setCell(mCellX, mCellY);
            mDragFolderRingAnimator.setWorkspace(XWorkspace.this);
            mDragFolderRingAnimator.animateToAcceptState();
            showFolderAccept(mDragFolderRingAnimator);
//            clearDragOutlines();
            clearDragOutlines(false);
            setDragMode(DRAG_MODE_CREATE_FOLDER);
        }
    }
    
//    private Runnable mFolderCreationRunnable = new Runnable(){
//
//        @Override
//        public void run() {
//            if (mDragFolderRingAnimator == null) {
//                mDragFolderRingAnimator = new FolderRingAnimator(mXContext, null);
//            }
//            mDragFolderRingAnimator.setCell(mCellX, mCellY);
//            mDragFolderRingAnimator.setWorkspace(XWorkspace.this);
//            mDragFolderRingAnimator.animateToAcceptState();
//            showFolderAccept(mDragFolderRingAnimator);
////            clearDragOutlines();
//            clearDragOutlines(false);
//            setDragMode(DRAG_MODE_CREATE_FOLDER);
//        }
//        
//    };
    
    private void manageFolderFeedback(ItemInfo info, int[] targetCell, float distance, XPagedViewItem dragOverView) {
        boolean userFolderPending = willCreateUserFolder(info, targetCell, distance);

        if (mDragMode == DRAG_MODE_NONE && userFolderPending &&
                !mFolderCreationAlarm.alarmPending()) {
//            mFolderCreationAlarm.setOnAlarmListener(new
//                    FolderCreationAlarmListener(targetLayout, targetCell[0], targetCell[1]));
//            mFolderCreationAlarm.setAlarm(FOLDER_CREATION_TIMEOUT);
            if (DEBUG_REORDER)R5.echo("onDragOver willCreateUserFolder = true");
            mFolderCreationAlarm.setOnAlarmListener(new
                    FolderCreationAlarmListener(targetCell[0], targetCell[1]));
            mFolderCreationAlarm.setAlarm(FOLDER_CREATION_TIMEOUT);
//            setDragMode(DRAG_MODE_CREATE_FOLDER);
            return;
        }
 
//        boolean willAddToFolder =
//                willAddToExistingUserFolder(info, targetLayout, targetCell, distance);
//
//        if (willAddToFolder && mDragMode == DRAG_MODE_NONE) {
//            mDragOverFolderIcon = ((FolderIcon) dragOverView);
//            mDragOverFolderIcon.onDragEnter(info);
//            if (targetLayout != null) {
//                targetLayout.clearDragOutlines();
//            }
//            setDragMode(DRAG_MODE_ADD_TO_FOLDER);
//            return;
//        }
        
        boolean willAddToFolder = willAddToExistingUserFolder(targetCell, info, distance);
        if (willAddToFolder) {
//            clearDragOutlines();
            clearDragOutlines(false);

            if (DEBUG_REORDER)R5.echo("onDragOver willAddToExistingUserFolder = true");
            XPagedViewItem item = xPagedView.findPageItemAt(getCurrentPage(), targetCell[0], targetCell[1]);
            DrawableItem dropOverView = null;
            if (item != null) {
                dropOverView = item.getDrawingTarget();
            }
            XFolderIcon fi = (XFolderIcon) dropOverView;
            if (mDragMode == DRAG_MODE_NONE)
            {
                showAddToFolder(targetCell[0], targetCell[1], fi);
            }
            
            if (!mHandler.hasMessages(MSG_OPEN_FOLDER)) {
                mHandler.sendEmptyMessageDelayed(MSG_CLOSE_FOLDER, TIMEOUT_CLOSE_FOLDER);
                android.os.Message msg = mHandler.obtainMessage(MSG_OPEN_FOLDER, fi);
                mHandler.sendMessageDelayed(msg, TIMEOUT_OPEN_FOLDER);
            } else {
                //do nothing
//                mHandler.removeMessages(MSG_OPEN_FOLDER);
//                android.os.Message msg = mHandler.obtainMessage(MSG_OPEN_FOLDER, fi);
//                mHandler.sendMessageDelayed(msg, TIMEOUT_OPEN_FOLDER);
            }
            setDragMode(DRAG_MODE_ADD_TO_FOLDER);
            
        } else {
            
            
            
            mHandler.removeMessages(MSG_OPEN_FOLDER);
            if (getOpenFolder() != null && !mHandler.hasMessages(MSG_CLOSE_FOLDER)) {
                mHandler.sendEmptyMessageDelayed(MSG_CLOSE_FOLDER, TIMEOUT_CLOSE_FOLDER);
            }
        }
        
        if (mDragMode == DRAG_MODE_ADD_TO_FOLDER && !willAddToFolder) {
            setDragMode(DRAG_MODE_NONE);
        }
        if (mDragMode == DRAG_MODE_CREATE_FOLDER && !userFolderPending) {
            setDragMode(DRAG_MODE_NONE);
        }

        return;
        /*RK_ID: Jelly_Bean . AUT: yumina . DATE: 2012-08-16 . E*/
    }
        
    float mMaxDistanceForFolderCreation;
    float mMaxDistanceForFolderCreationFactor = 0.45f;
    
    protected void cleanupAddToFolder() {
//        if (mDragOverFolderIcon != null) {
//            mDragOverFolderIcon.onDragExit(null);
//            mDragOverFolderIcon = null;
//        }
        if (DEBUG_REORDER)R5.echo("cleanupAddToFolder");
        mHandler.removeMessages(MSG_OPEN_FOLDER);
        if (mFolderRingAnimator != null) {
            mFolderRingAnimator.animateToNaturalState();
        }
//        mHandler.removeMessages(MSG_CLOSE_FOLDER);
    }
    
    void cellToCenterPoint(int cellX, int cellY, int[] result) {
        regionToCenterPoint(cellX, cellY, 1, 1, result);
    }
    
    void regionToCenterPoint(int cellX, int cellY, int spanX, int spanY, int[] result) {
//        final int hStartPadding = (int)localRect.left;
//        final int vStartPadding = (int)localRect.top;
//        final int hStartPadding = 0;
//        final int vStartPadding = 0;
        final int hStartPadding = (int)xPagedView.localRect.left + xPagedView.getPageGap()/2;
        final int vStartPadding = (int)xPagedView.localRect.top;
        result[0] = hStartPadding + cellX * (mCellWidth + mWidthGap) +
                (spanX * mCellWidth + (spanX - 1) * mWidthGap) / 2;
        result[1] = vStartPadding + cellY * (mCellHeight + mHeightGap) +
                (spanY * mCellHeight + (spanY - 1) * mHeightGap) / 2;
    }

    private void saveTmpCell() {
      if (mSaved)
      {
          return;
      }
      XPagedViewItem item;
      ItemInfo info;
      Set<Map.Entry<Long, XPagedViewItem>> set = xPagedView.mItemIDMap.entrySet();
      for (Map.Entry<Long, XPagedViewItem> map : set) {
          item = map.getValue();
          info = item.getInfo();
//          if (info.screen != getCurrentPage())
//          {
//              continue;
//          }
          
          info.tmpCellX = info.cellX;
          info.tmpCellY = info.cellY;
      }
      
      mSaved = true;
  }
    private final int[] mDragCell = new int[2];
    
    void visualizeDropLocation(XPagedViewItem v, Bitmap dragOutline, int originX, int originY, int cellX,
            int cellY, int spanX, int spanY, boolean resize, Point dragOffset, Rect dragRegion) {
        final int oldDragCellX = mDragCell[0];
        final int oldDragCellY = mDragCell[1];

//        if (dragOutline == null || v == null) {
        if (v == null && dragOutline == null) {
            R5.echo("visualizeDropLocation return");
            return;
        }

        if (cellX != oldDragCellX || cellY != oldDragCellY) {
            mDragCell[0] = cellX;
            mDragCell[1] = cellY;
            // Find the top left corner of the rect the object will occupy            
            final int[] topLeft = mTmpPoint;
//            cellToPoint(cellX, cellY, topLeft);

//            int left = topLeft[0];
//            int top = topLeft[1];
//            int left = cellX * mCellWidth;
//            int top = cellY * mCellHeight;
            
//            /*RK_ID:RK_PAD yumina 2013-6-15. S***/
//            int leftpadding = getXContext().getResources().getDimensionPixelOffset(R.dimen.workspace_pageview_left_padding);
//            int toppadding =  getXContext().getResources().getDimensionPixelOffset(R.dimen.workspace_pageview_top_padding);
//            /*RK_ID:RK_PAD yumina 2013-6-15. E***/
            
            cellToPoint(cellX, cellY, mTempLocation);

            int left = mTempLocation[0];
            int top = mTempLocation[1];
            
            if (v != null && dragOffset == null) {
                // When drawing the drag outline, it did not account for margin offsets
                // added by the view's parent.
//                MarginLayoutParams lp = (MarginLayoutParams) v.getLayoutParams();
//                left += lp.leftMargin;
//                top += lp.topMargin;

                // Offsets due to the size difference between the View and the dragOutline.
                // There is a size difference to account for the outer blur, which may lie
                // outside the bounds of the view.
//                top += (v.getCellHeight() - dragOutline.getHeight()) / 2;
                // We center about the x axis
//                left += (mCellWidth * spanX) + ((spanX - 1) * mWidthGap);
            } else {
//                if (dragOffset != null && dragRegion != null) {
//                    // Center the drag region *horizontally* in the cell and apply a drag
//                    // outline offset
//                    left += dragOffset.x + ((mCellWidth * spanX) + ((spanX - 1) * mWidthGap)
//                             - dragRegion.width()) / 2;
//                    top += dragOffset.y;
//                } else {
//                    // Center the drag outline in the cell
//                    left += ((mCellWidth * spanX) + ((spanX - 1) * mWidthGap)
//                            - dragOutline.getWidth()) / 2;
//                    top += ((mCellHeight * spanY) + ((spanY - 1) * mHeightGap)
//                            - dragOutline.getHeight()) / 2;
//                }
            }
            
//            final int oldIndex = mDragOutlineCurrent;
//            mDragOutlineAnims[oldIndex].cancel();
//            mDragOutlineCurrent = (oldIndex + 1) % mDragOutlines.length;
//            
//            mDragOutlineAnims[mDragOutlineCurrent].setTag(dragOutline);
//            mDragOutlineAnims[mDragOutlineCurrent].animateOut();
//            mDragOutlineCurrent = (mDragOutlineCurrent + 1) % mDragOutlines.length;
//            
//            
//            R5.echo("mDragOutlineCurrent = " + mDragOutlineCurrent);
//            Rect r = mDragOutlines[mDragOutlineCurrent];
//            if (v != null) {
//                r.set(left, top, (int)(left + v.getCellWidth() * spanX), (int)(top + v.getCellHeight() * spanY));
//            }
//            else
//            {
//                r.set(left, top, (int)(left + mCellWidth * spanX), (int)(top + mCellHeight * spanY));
//            }
            
//            mDragOutlinesNew.set(left + 10, top + 10, (int)(left + v.getCellWidth() * spanX - 10), (int)(top + v.getCellHeight() * spanY - 10));
            if (resize) {
//                cellToRect(cellX, cellY, spanX, spanY, r);
            }

//            R5.echo("visualizeDropLocation r = " + r.toString());
//            mDragOutlineAnims[mDragOutlineCurrent].setTag(dragOutline);
//            mDragOutlineAnims[mDragOutlineCurrent].animateIn();
            if (v != null) {
                mDragOutlinesNew.set(left, top, (int)(left + v.getCellWidth() * spanX), (int)(top + v.getCellHeight() * spanY));
            }
            else
            {
                mDragOutlinesNew.set(left, top, (int)(left + mCellWidth * spanX), (int)(top + mCellHeight * spanY));
            }
            R5.echo("visualizeDropLocation r = " + mDragOutlinesNew.toString());
            
            mDragOutlinesNewDraw = true; 
        }
    }
//    private Rect[] mDragOutlines = new Rect[4];
    private Rect mDragOutlinesNew = new Rect();
    boolean mDragOutlinesNewDraw = false;
//    private float[] mDragOutlineAlphas = new float[mDragOutlines.length];
//    private InterruptibleInOutAnimator[] mDragOutlineAnims =
//            new InterruptibleInOutAnimator[mDragOutlines.length];
//    private int mDragOutlineCurrent = 0;
    private final Paint mDragOutlinePaint = new Paint();
    private Bitmap mDragOutline = null;
    private final HolographicOutlineHelper mOutlineHelper = new HolographicOutlineHelper();
    
//    private Bitmap createDragOutline(XPagedViewItem v, Canvas canvas, int padding) {
//        final int outlineColor = mXContext.getContext().getResources().getColor(android.R.color.holo_blue_light);
//        final Bitmap b = Bitmap.createBitmap(
//                (int)v.getCellWidth() + padding, (int)v.getCellHeight() + padding, Bitmap.Config.ARGB_8888);
//
//        canvas.setBitmap(b);
//        drawDragView(v, canvas, padding, true);
//        mOutlineHelper.applyMediumExpensiveOutlineWithBlur(b, canvas, outlineColor, outlineColor);
//        canvas.setBitmap(null);
//        return b;
//    }
    private final Rect mTempRect = new Rect();
    public void updateIconSizeValue() {
        if (xPagedView != null) {
          //  xPagedView.updateIconSizeValue();
            final int iconsize = SettingsValue.getIconSizeValueNew(getXContext()
    				.getContext());
        	Utilities.updateIconSizeinWorkspace(xPagedView.mItemIDMap, iconsize);
        }
    }
        
    private final Alarm mFolderCreationAlarm = new Alarm();
    static int FOLDER_CREATION_TIMEOUT = 90;
    private FolderRingAnimator mDragFolderRingAnimator = null;
    private ArrayList<FolderRingAnimator> mFolderOuterRings = new ArrayList<FolderRingAnimator>();
    
    public void showFolderAccept(FolderRingAnimator fra) {
        if (DEBUG_REORDER)R5.echo("showFolderAccept");
        mFolderOuterRings.add(fra);
    }

    public void hideFolderAccept(FolderRingAnimator fra) {
        if (DEBUG_REORDER)R5.echo("hideFolderAccept");
        if (mFolderOuterRings.contains(fra)) {
            mFolderOuterRings.remove(fra);
        }
        invalidate();
    }
    
    /**
     * Given a cell coordinate, return the point that represents the upper left corner of that cell
     *
     * @param cellX X coordinate of the cell
     * @param cellY Y coordinate of the cell
     *
     * @param result Array of 2 ints to hold the x and y coordinate of the point
     */
    void cellToPoint(int cellX, int cellY, int[] result) {
        final int hStartPadding = (int)xPagedView.localRect.left + xPagedView.getPageGap()/2;
        final int vStartPadding = (int)xPagedView.localRect.top;
        result[0] = hStartPadding + cellX * (mCellWidth);
//        result[0] = hStartPadding + cellX * (mCellWidth + mWidthGap);
//        result[1] = vStartPadding + cellY * (mCellHeight + mHeightGap);
        result[1] = vStartPadding + cellY * (mCellHeight);
    }
    
    public void clearDragOutlines(boolean clearDragOutline) {
        mDragOutlinesNewDraw = false;
        
        if (clearDragOutline) {
            mXContext.post(new Runnable() {
                @Override
                public void run() {
                    if (mDragOutline != null) {
                        mDragOutline.recycle();
                        mDragOutline = null;
                    }
                }
            });
        }
        
        // Invalidate the drag data
        mDragCell[0] = -1;
        mDragCell[1] = -1;
    }
    
    //dooba edit s   
    private void onDropIconPkg(final int[] touchXY, final Object dragInfo, XDragObject d) {
    	if (!(dragInfo instanceof List<?>)) {
    		return;
    	}
    	
    	if (((XLauncher)mXContext.getContext()).showIconPkgDialog(touchXY, d)) {
    		return;
    	}
    	List<XScreenShortcutInfo> list = (List<XScreenShortcutInfo>)dragInfo;
    	
        final long container = LauncherSettings.Favorites.CONTAINER_DESKTOP;
        final int screen = getCurrentPage();
//        int[] resultSpan = new int[2];
        boolean createFolder = true;
//        mTargetCell = createArea(touchXY[0],
//                touchXY[1], 1, 1, 1, 1,
//                null, mTargetCell, resultSpan, MODE_ON_DROP);
                            
        mTargetCell = findNearestArea(screen, touchXY[0], touchXY[1], 1, 1,
                null, true, mTargetCell);
        if (mTargetCell == null || mTargetCell[0] < 0 || mTargetCell[1] < 0) {
        	return;
        }
        ((XLauncher)mXContext.getContext()).addIconPkgFromMngView(list, 
    			container, screen, mTargetCell[0], mTargetCell[1], createFolder);
    }
    
    public void addExternalItemInfo(int[] touchXY, ItemInfo info, boolean notDrop, XDragObject d) {
    	int spanX = info.spanX;
        int spanY = info.spanY;
        final long container = LauncherSettings.Favorites.CONTAINER_DESKTOP;
        final int screen = getCurrentPage();
        //int[] resultSpan = new int[2];
        boolean foundCell;
                
        switch (info.itemType) {
            case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
            case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
            case LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT:
                if (info.container == -1 && info instanceof ApplicationInfo) {
                    // Came from all apps -- make a copy
                    info = new ShortcutInfo((ApplicationInfo) info);
                }
                
                if (info.container == -1 && info instanceof XScreenShortcutInfo) {
    				info = new ShortcutInfo((XScreenShortcutInfo)info);
    			}

                if (info.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT && info instanceof ShortcutInfo) {
                    info = new ShortcutInfo((ShortcutInfo) info);

                    ShortcutInfo shortcutInfo = (ShortcutInfo)info;
                    ComponentName componentName = shortcutInfo.intent.getComponent();
                                          
                    if (componentName != null)
                    {
                        String str = Settings.System.getString((mXContext.getContext()).getContentResolver(),"NEWMSG_" + componentName.flattenToString());
                        if (str != null && !str.isEmpty())
                        {
                            shortcutInfo.updateInfo(str);
                        }                                          
                    }                    
                }
                
                if (notDrop) {				
    				mTargetCell = findNearestVacantArea(screen, touchXY[0], touchXY[1], spanX, spanY, null, null);
    			} else {
//                    mTargetCell = findNearestArea(screen, touchXY[0], touchXY[1], spanX, spanY, null, false, mTargetCell);

                    if (createUserFolderIfNecessary(container, mTargetCell, true, null, info)) {
                    	//add by zhanggx1 for removing on 2013-11-13 . s
                        ((XLauncher)getXContext().getContext()).autoReorder();
                        //add by zhanggx1 for removing on 2013-11-13 . e
                        return;
                    }
                    if (addToExistingFolderIfNecessary(screen, mTargetCell, true, info, d)) {
                    	//add by zhanggx1 for removing on 2013-11-13 . s
                        ((XLauncher)getXContext().getContext()).autoReorder();
                        //add by zhanggx1 for removing on 2013-11-13 . e
                        return;
                    }
                    
                    createAreaOnDrop();
//                    mTargetCell = createArea(touchXY[0],
//                            touchXY[1], spanX, spanY, spanX, spanY,
//                            null, mTargetCell, resultSpan, MODE_ON_DROP);
//
//                    mTargetCell = findNearestArea(screen, touchXY[0], touchXY[1], spanX, spanY, null, true, mTargetCell);
    			}
                    if (mTargetCell == null || mTargetCell[0] < 0 || mTargetCell[1] < 0) {
                    	((XLauncher) mXContext.getContext()).showOutOfSpaceMessage();
                    	return;
                    }
                    
                    info.screen = screen;
                    info.cellX = mTargetCell[0];
                    info.cellY = mTargetCell[1];
                    info.spanX = 1;
                    info.spanY = 1;
                    XLauncherModel.addOrMoveItemInDatabase(this.getXContext().getContext(), info, container, screen, info.cellX, info.cellY);
                    addInScreen((ShortcutInfo) info, ((LauncherApplication) getXContext().getContext().getApplicationContext()).getIconCache(), false);
                break;
            case LauncherSettings.Favorites.ITEM_TYPE_FOLDER:
                if (info instanceof FolderInfo) {
                    FolderInfo fInfo = (FolderInfo) info;
//                    FolderInfo fInfo = newInfo.copy();
                    if (notDrop) {				
        				mTargetCell = findNearestVacantArea(screen, touchXY[0], touchXY[1], spanX, spanY, null, null);
        			} else {
//	                    mTargetCell = createArea(touchXY[0],
//	                            touchXY[1], spanX, spanY, spanX, spanY,
//	                            null, mTargetCell, resultSpan, MODE_ON_DROP);
//	                                        
//	                    mTargetCell = findNearestArea(screen, touchXY[0], touchXY[1], spanX, spanY,
//	                            null, true, mTargetCell);
        			    createAreaOnDrop();
        			}
                    if (mTargetCell == null || mTargetCell[0] < 0 || mTargetCell[1] < 0) {
                    	((XLauncher) mXContext.getContext()).showOutOfSpaceMessage();
                    	return;
                    }
                    fInfo.container = container;
                    fInfo.cellX = mTargetCell[0];
                    fInfo.cellY = mTargetCell[1];
                    fInfo.screen = screen;
//                    fInfo.clearListener();
//                    addInScreen(fInfo);
                    final DrawableItem view = ((XLauncher) mXContext.getContext()).getHotseat()
                            .getDragView();
                    if (view != null && view instanceof XFolderIcon) {
                        final XFolderIcon icon = ((XFolderIcon) view);
                        icon.resetPressedState();
                        icon.updateInfoContanier(fInfo);
                        icon.resize(new RectF(0, 0, mCellWidth, mCellHeight));

//                        DrawableItem iconDrawableFolderIcon = icon.getIconDrawable();
//                        final int iconsize = SettingsValue.getIconSizeValueNew(getXContext()
//                                .getContext());
//                        Utilities.iconSizeChange(iconDrawableFolderIcon,iconsize);
                        
                        XPagedViewItem itemToAdd = new XPagedViewItem(mXContext, icon, fInfo);
                        xPagedView.addPagedViewItem(itemToAdd);

                        icon.setVisibility(true);
                        icon.invalidate();
                    }

                    ((XLauncher) mXContext.getContext()).updateFolder(fInfo);
                    XLauncherModel.addOrMoveItemInDatabase(this.getXContext().getContext(), fInfo,
                            fInfo.container, fInfo.screen, fInfo.cellX, fInfo.cellY);
                    ((XLauncher) mXContext.getContext()).addFolder(fInfo);
                }
                break;
              //for edit mode by zhanggx1 . s
            case LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET:
            /*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. S***/
            case LauncherSettings.Favorites.ITEM_TYPE_COMMEND_APPWIDGET:
            /*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. E***/
				if (info instanceof LauncherAppWidgetInfo) {   

                    LauncherAppWidgetInfo widgetInfo = (LauncherAppWidgetInfo) info;
                    final LauncherAppWidgetInfo newInfo = widgetInfo.copy();
                    
                    if (notDrop) {				
//        				mTargetCell = findNearestVacantArea(screen, touchXY[0], touchXY[1], spanX, spanY, null, null);
                    	mTargetCell = getPagedView().findFirstVacantCell(screen, spanX, spanY);
        			} else {
//	                    mTargetCell = createArea(touchXY[0],
//	                            touchXY[1], spanX, spanY, spanX, spanY,
//	                            mDragView, mTargetCell, resultSpan, MODE_ON_DROP);
//	                    
//	                    mTargetCell = findNearestArea(screen, touchXY[0], touchXY[1], spanX, spanY,
//	                            null, true, mTargetCell);
        			    createAreaOnDrop();
        			}

                    foundCell = mTargetCell != null && mTargetCell[0] >= 0 && mTargetCell[1] >= 0;
                    
                    if (foundCell) {
                        newInfo.container = container;
                        newInfo.cellX = mTargetCell[0];
                        newInfo.cellY = mTargetCell[1];
                        newInfo.screen = screen;

                        Context c = mXContext.getContext();
                        if (c instanceof XLauncher) {
                            XLauncher xlauncher = (XLauncher) c;
                            xlauncher.getMainView().post(new Runnable() {

                                @Override
                                public void run() {
                                    XViewContainer view = new XViewContainer(mXContext,
                                            newInfo.spanX * getPagedView().getCellWidth(),
                                            newInfo.spanY * getPagedView().getCellHeight(),
                                            newInfo.hostView);

                                    addInScreen(view, newInfo);
                                }

                            });
                        }

                        XLauncherModel.addOrMoveItemInDatabase(this.getXContext().getContext(),
                                newInfo, newInfo.container, newInfo.screen, newInfo.cellX,
                                newInfo.cellY);
                    } else {
                    	((XLauncher) mXContext.getContext()).showOutOfSpaceMessage();
                    }
                }
                break;
            case LauncherSettings.Favorites.ITEM_TYPE_LEOSWIDGET:
                if (info instanceof LenovoWidgetViewInfo) {
//                    removePagedViewItem(info);
                    
                    LenovoWidgetViewInfo widgetInfo = (LenovoWidgetViewInfo) info;
                    final LenovoWidgetViewInfo newInfo = widgetInfo.copy();                    
                    
                    spanX = newInfo.minWidth;
            		spanY = newInfo.minHeight;
            		if (notDrop) {				
//        				mTargetCell = findNearestVacantArea(screen, touchXY[0], touchXY[1], spanX, spanY, null, null);
            			mTargetCell = getPagedView().findFirstVacantCell(screen, spanX, spanY);
        			} else {
//	                    mTargetCell = createArea(touchXY[0],
//	                            touchXY[1], spanX, spanY, spanX, spanY,
//	                            mDragView, mTargetCell, resultSpan, MODE_ON_DROP);
//	                    
//	                    mTargetCell = findNearestArea(screen, touchXY[0], touchXY[1], spanX, spanY,
//	                        null, true, mTargetCell);
        			    createAreaOnDrop();
        			}

                    foundCell = mTargetCell != null && mTargetCell[0] >= 0 && mTargetCell[1] >= 0;
                    
                    if (foundCell) {
                    	// 如果是天气WIDGET，弹出皮肤选择框
//                		if (widgetInfo.className
//    					        .equals(GadgetUtilities.WEATHERMAGICWIDGETVIEWHELPER)) {
//                			((XLauncher)mXContext.getContext()).resetAddInfo();
//                			((XLauncher)mXContext.getContext()).setPendingObjectPos(mTargetCell[0], mTargetCell[1]);
//                			
//                			Intent i = new Intent(
//                					WeatherUtilites.ACTION_ADD_LENOVO_WEATHER_WIDGET_ACTIVITY);
//                			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                			try {
//                				((XLauncher) mXContext.getContext()).startActivity(i);
//                			} catch (Exception e) {
//                				e.printStackTrace();
//                			}
//                			return;
//                		}
                		
                        newInfo.container = container;
                        newInfo.cellX = mTargetCell[0];
                        newInfo.cellY = mTargetCell[1];
                        newInfo.screen = screen;

                        Context c = mXContext.getContext();
                        if (c instanceof XLauncher) {
                            final XLauncher xlauncher = (XLauncher) c;
                            xlauncher.getMainView().post(new Runnable() {

                                @Override
                                public void run() {
                                	xlauncher.addLeosWidgetViewToWorkspace(newInfo);
                                }
                            });
                        }
                    } else {
                    	((XLauncher) mXContext.getContext()).showOutOfSpaceMessage();
                    }

                }
                break;
            case SimpleItemInfo.ACTION_TYPE_ADD_SHORTCUT:
            	if (notDrop) {				
    				mTargetCell = findNearestVacantArea(screen, touchXY[0], touchXY[1], spanX, spanY, null, null);
    			} else {
//                    mTargetCell = createArea(touchXY[0],
//                            touchXY[1], spanX, spanY, spanX, spanY,
//                            null, mTargetCell, resultSpan, MODE_ON_DROP);
//                                        
//                    mTargetCell = findNearestArea(screen, touchXY[0], touchXY[1], spanX, spanY,
//                            null, true, mTargetCell);
    			    createAreaOnDrop();
    			}
                if (mTargetCell == null || mTargetCell[0] < 0 || mTargetCell[1] < 0) {
                    ((XLauncher) mXContext.getContext()).showOutOfSpaceMessage();
                    return;
                }
            	SimpleItemInfo simpleInfo = (SimpleItemInfo)info;
				((XLauncher)mXContext.getContext()).resetPendingInfoBeforePick();
				((XLauncher)mXContext.getContext()).setPendingObjectPos(mTargetCell[0], mTargetCell[1]);
				if(simpleInfo.filterSpecialShortcut()){
					Intent i = new Intent();
					ComponentName c = simpleInfo.intent.getComponent();
					if(c != null){
						i.setComponent(c);
						i.setAction(Intent.ACTION_MAIN);
						((XLauncher)mXContext.getContext()).completeAddSpecialShortcut(i);
					}
				}else{
					((XLauncher)mXContext.getContext()).startActivityForResult(simpleInfo.intent, XLauncher.REQUEST_CREATE_SHORTCUT);
				}
				break;
			case SimpleItemInfo.ACTION_TYPE_ADD_FOLDER:
				if (notDrop) {				
    				mTargetCell = findNearestVacantArea(screen, touchXY[0], touchXY[1], spanX, spanY, null, null);
    			} else {
//                    mTargetCell = createArea(touchXY[0],
//                            touchXY[1], spanX, spanY, spanX, spanY,
//                            null, mTargetCell, resultSpan, MODE_ON_DROP);
//                                        
//                    mTargetCell = findNearestArea(screen, touchXY[0], touchXY[1], spanX, spanY,
//                            null, true, mTargetCell);
    			    createAreaOnDrop();
    			}
                if (mTargetCell == null || mTargetCell[0] < 0 || mTargetCell[1] < 0) {
                	((XLauncher) mXContext.getContext()).showOutOfSpaceMessage();
                	return;
                }
				((XLauncher)mXContext.getContext()).resetPendingInfoBeforePick();
				((XLauncher)mXContext.getContext()).setPendingObjectPos(mTargetCell[0], mTargetCell[1]);
				((XLauncher)mXContext.getContext()).workspacePickApplication(true);
				break;
			case SimpleItemInfo.ACTION_TYPE_ADD_OTHER_WIDGET:
				((XLauncher)mXContext.getContext()).resetAddInfo();
				((XLauncher)mXContext.getContext()).setPendingObjectPos(notDrop ? null : touchXY);
				((XLauncher)mXContext.getContext()).pickupOtherWidgets();
				break;
			case SimpleItemInfo.ACTION_TYPE_CREATE_WIDGET:
				final AppWidgetProviderInfo widgetInfo = ((SimpleItemInfo)info).widgetProviderInfo; 
				spanX = ((SimpleItemInfo)info).spanXY[0];
				spanY = ((SimpleItemInfo)info).spanXY[1];
                if (notDrop) {
                	mTargetCell = getPagedView().findFirstVacantCell(getCurrentPage(), spanX, spanY);
//                	if (mTargetCell == null) {
//                		mTargetCell = findNearestVacantArea(screen, touchXY[0], touchXY[1], spanX, spanY, null, null);
//                	}
    			} else {
//                    mTargetCell = createArea(touchXY[0],
//                            touchXY[1], spanX, spanY, spanX, spanY,
//                            mDragView, mTargetCell, resultSpan, MODE_ON_DROP);
//                    
//                    mTargetCell = findNearestArea(screen, touchXY[0], touchXY[1], spanX, spanY,
//                            null, true, mTargetCell);
    			    createAreaOnDrop();
    			}

                foundCell = mTargetCell != null && mTargetCell[0] >= 0 && mTargetCell[1] >= 0;
                
                if (foundCell) { 
                	int[] newTouchXY = new int[2];
                	newTouchXY[0] = (int)(mTargetCell[0] * getPagedView().getCellWidth() + 1);
                	newTouchXY[1] = (int)(mTargetCell[1] * getPagedView().getCellHeight() + 1);
    				((XLauncher)mXContext.getContext()).addAppWidgetFromSlidingBar(widgetInfo, LauncherSettings.Favorites.CONTAINER_DESKTOP,
    	                    this.getCurrentPage(), mTargetCell, newTouchXY);
                } else {
                	((XLauncher) mXContext.getContext()).showOutOfSpaceMessage();
                }				
				break;
            //for edit mode by zhanggx1 . e
            default:
                throw new IllegalStateException("Unknown item type: " + info.itemType);
        }
      //add by zhanggx1 for removing on 2013-11-13 . s
        ((XLauncher)getXContext().getContext()).autoReorder();
        //add by zhanggx1 for removing on 2013-11-13 . e
    }
    //dooba edit e 

    /*** RK_ID: RK_FULLSCREEN_FOLDER  AUT: zhaoxy . DATE: 2012-05-23 . START***/
    float folderAnimInput = 0;
    public void updateFolderAnim(float input, float scale) {
        folderAnimInput = input;
        if (xPagedView != null) {
            xPagedView.updateFolderAnim(folderAnimInput, scale);
        }
    }
    /*** RK_ID: RK_FULLSCREEN_FOLDER  AUT: zhaoxy . DATE: 2012-05-23 . END***/
    
    public void clearNewBgAndSetNum(String componentName, int num){
        XPagedViewItem item;
        ItemInfo info;
        Set<Map.Entry<Long, XPagedViewItem>> set = xPagedView.mItemIDMap.entrySet();
        for (Map.Entry<Long, XPagedViewItem> map : set) {
            item = map.getValue();
            info = item.getInfo();
            if (info instanceof ShortcutInfo)
            {
                ShortcutInfo shortcutInfo = (ShortcutInfo)info;                
                
                ComponentName component = shortcutInfo.intent.getComponent();
                if (component != null && componentName.equals(component.flattenToString()))
                {
                    shortcutInfo.updateInfo(num);
                    ((XShortcutIconView)item.getDrawingTarget()).showTipForNewAdded(shortcutInfo.mNewString);
                    xPagedView.refreshBitmapCache(shortcutInfo.screen);               
                    invalidate();
                        
                }                
                
            }
//            else if (info instanceof FolderInfo)
//            {
//                XFolderIcon folderIcon = (XFolderIcon)item.getDrawingTarget();
//                folderIcon.mFolder.clearNewBgAndSetNum(componentName, num);
//            }
        }
    }
    
    public int[] getDropTargetCellXY(int[] touchXY, int spanX, int spanY, int screen) {
//    	int[] resultSpan = new int[2];
    	int[] targetCell = new int[2];
//    	targetCell = createArea(touchXY[0],
//                touchXY[1], spanX, spanY, spanX, spanY,
//                mDragView, targetCell, resultSpan, MODE_ON_DROP);
        
    	targetCell = findNearestArea(screen, touchXY[0], touchXY[1], spanX, spanY,
                null, true, targetCell);
        return targetCell;
    }
        
    private TimeInterpolator mEaseOutInterpolator;
    private void initOutlineAnims(){
        // When dragging things around the home screens, we show a green outline of
        // where the item will land. The outlines gradually fade out, leaving a trail
        // behind the drag path.
        // Set up all the animations that are used to implement this fading.
//        for (int i = 0; i < mDragOutlines.length; i++) {
//            mDragOutlines[i] = new Rect(-1, -1, -1, -1);
//        }
//        
//        final Resources res = mXContext.getContext().getResources();
//        final int duration = res.getInteger(R.integer.config_dragOutlineFadeTime);
//        final float fromAlphaValue = 0;
//        final float toAlphaValue = (float)res.getInteger(R.integer.config_dragOutlineMaxAlpha);
//        Arrays.fill(mDragOutlineAlphas, fromAlphaValue);
//        
//        mEaseOutInterpolator = new DecelerateInterpolator(2.5f);
//        
//        for (int i = 0; i < mDragOutlineAnims.length; i++) {
//            final InterruptibleInOutAnimator anim =
//                new InterruptibleInOutAnimator(duration, fromAlphaValue, toAlphaValue, mXContext);
//            anim.getAnimator().setInterpolator(mEaseOutInterpolator);
//            final int thisIndex = i;
//            anim.getAnimator().addUpdateListener(new AnimatorUpdateListener() {
//                public void onAnimationUpdate(ValueAnimator animation) {
//                    final Bitmap outline = (Bitmap)anim.getTag();
//
//                    // If an animation is started and then stopped very quickly, we can still
//                    // get spurious updates we've cleared the tag. Guard against this.
//                    if (outline == null) {
////                        if (false) {
////                            Object val = animation.getAnimatedValue();
////                            Log.d(TAG, "anim " + thisIndex + " update: " + val +
////                                     ", isStopped " + anim.isStopped());
////                        }
//                        // Try to prevent it from continuing to run
//                        animation.cancel();
//                    } else {
//                        /*RK_ID: Jelly_Bean . AUT: yumina . DATE: 2012-08-16 . S*/
//                        mDragOutlineAlphas[thisIndex] = (Float) animation.getAnimatedValue();
////                        R5.echo("mDragOutlineAlphas = " + mDragOutlineAlphas[thisIndex]);
//                        mXContext.getRenderer().invalidate();
////                        CellLayout.this.invalidate(mDragOutlines[thisIndex]);
//                        /*RK_ID: Jelly_Bean . AUT: yumina . DATE: 2012-08-16 . E*/
//                    }
//                }
//            });
//            // The animation holds a reference to the drag outline bitmap as long is it's
//            // running. This way the bitmap can be GCed when the animations are complete.
//            anim.getAnimator().addListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    if ((Float) ((ValueAnimator) animation).getAnimatedValue() == 0f) {
//                        anim.setTag(null);
//                    }
//                }
//            });
//            mDragOutlineAnims[i] = anim;            
//        }
    }
    
    /**
     * Returns a new bitmap to be used as the object outline, e.g. to visualize the drop location.
     * Responsibility for the bitmap is transferred to the caller.
     */
    private Bitmap createDragOutline(Bitmap orig, Canvas canvas, int padding, int w, int h,
            Paint alphaClipPaint) {
        final int outlineColor = mXContext.getContext().getResources().getColor(android.R.color.holo_blue_light);
        final Bitmap b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(b);

        Rect src = new Rect(0, 0, orig.getWidth(), orig.getHeight());
        float scaleFactor = Math.min((w - padding) / (float) orig.getWidth(),
                (h - padding) / (float) orig.getHeight());
        int scaledWidth = (int) (scaleFactor * orig.getWidth());
        int scaledHeight = (int) (scaleFactor * orig.getHeight());
        Rect dst = new Rect(0, 0, scaledWidth, scaledHeight);

        // center the image
        dst.offset((w - scaledWidth) / 2, (h - scaledHeight) / 2);

        canvas.drawBitmap(orig, src, dst, null);
        
        mOutlineHelper.applyMediumExpensiveOutlineWithBlur(b, canvas, outlineColor, outlineColor, alphaClipPaint);
        canvas.setBitmap(null);

        return b;
    }
        
    private Bitmap createDragOutline(Bitmap orig, Canvas canvas, int topPadding, int widthPadding, int w, int h,
            Paint alphaClipPaint, float scaleFactor) {
        final int outlineColor = mXContext.getContext().getResources().getColor(android.R.color.holo_blue_light);
        final Bitmap b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(b);
        
        Rect src = new Rect(0, 0, orig.getWidth(), orig.getHeight());

        int scaledWidth = (int) (scaleFactor * orig.getWidth());
        int scaledHeight = (int) (scaleFactor * orig.getHeight());
        Rect dst = new Rect(widthPadding, topPadding, widthPadding + scaledWidth, topPadding + scaledHeight);

        canvas.drawBitmap(orig, src, dst, null);
        
        mOutlineHelper.applyMediumExpensiveOutlineWithBlur(b, canvas, outlineColor, outlineColor, alphaClipPaint);
        canvas.setBitmap(null);

        return b;
    }
    
    private Bitmap getDragOutlineByShortcutInfo(ShortcutInfo info) {
        final float screenWidth = getWidth();
        
        float left = mPaddingLeft + (mCellWidth + mWidthGap) * info.cellX - screenWidth;
        float top = mPaddingTop + (mCellHeight + mHeightGap) * info.cellY;
        RectF rect = new RectF(left, top, left + mCellWidth, top + mCellHeight);
        ShortcutInfo newinfo = new ShortcutInfo(info);
        newinfo.container = LauncherSettings.Favorites.CONTAINER_DESKTOP;
        XShortcutIconView iconD = new XShortcutIconView(newinfo, rect, mXContext);
        iconD.resize(iconD.localRect);
        
//        R5.echo("getDragOutlineByShortcutInfo rect = " + iconD.localRect
//        		+ "iconD drawable rect = " + iconD.mIconDrawable.localRect);
        Bitmap b = iconD.getSnapshot(1f);
        final Canvas canvas = new Canvas();

        final int outlineColor = mXContext.getContext().getResources().getColor(android.R.color.holo_blue_light);
        mOutlineHelper.applyMediumExpensiveOutlineWithBlur(b, canvas, outlineColor, outlineColor, null);
        return b;
    }
    
    private Bitmap getDragOutlineByFolderInfo(FolderInfo info) {
//        final float screenWidth = getWidth();
        
//        float left = mPaddingLeft + (mCellWidth + mWidthGap) * info.cellX - screenWidth;
//        float top = mPaddingTop + (mCellHeight + mHeightGap) * info.cellY;
//        RectF rect = new RectF(left, top, left + mCellWidth, top + mCellHeight);
        FolderInfo newinfo = info.clone();
        newinfo.container = LauncherSettings.Favorites.CONTAINER_DESKTOP;
        XFolderIcon newFolder = XFolderIcon.obtain(mXContext, (XLauncher) mXContext.getContext(), newinfo);
        newFolder.resize(new RectF(0, 0, mCellWidth, mCellHeight));
        
        Bitmap b = newFolder.getSnapshot(1f);
        final Canvas canvas = new Canvas();

        final int outlineColor = mXContext.getContext().getResources().getColor(android.R.color.holo_blue_light);
        mOutlineHelper.applyMediumExpensiveOutlineWithBlur(b, canvas, outlineColor, outlineColor, null);
        return b;
    }
    
    private Bitmap getDragOutlineByDrawableBitmap(Bitmap b) {        
        final Canvas canvas = new Canvas();        
        final int iconsize = SettingsValue.getIconSizeValueNew(getXContext()
                .getContext());

        int paddingWidth = (mCellWidth - iconsize) / 2;
//        int app_icon_padding_top = mXContext.getResources().getDimensionPixelOffset(R.dimen.app_icon_padding_top);
        int app_icon_size = mContext.getResources().getDimensionPixelSize(R.dimen.app_icon_size);
        int app_icon_padding_top = mCellHeight / 2 - app_icon_size / 2;

        return  createDragOutline(b, canvas, app_icon_padding_top, paddingWidth, mCellWidth, mCellHeight, null, (float)iconsize / b.getWidth());
    }
    
    private Bitmap createDragOutline(View v, Canvas canvas, int padding) {
        final int outlineColor = mXContext.getResources().getColor(android.R.color.holo_blue_light);
        final Bitmap b = Bitmap.createBitmap(
                v.getWidth() + padding, v.getHeight() + padding, Bitmap.Config.ARGB_8888);

        canvas.setBitmap(b);
        drawDragView(v, canvas, padding, true);
        mOutlineHelper.applyMediumExpensiveOutlineWithBlur(b, canvas, outlineColor, outlineColor);
        canvas.setBitmap(null);
        return b;
    }
    
    /**
     * Draw the View v into the given Canvas.
     *
     * @param v the view to draw
     * @param destCanvas the canvas to draw on
     * @param padding the horizontal and vertical padding to use when drawing
     */
    private void drawDragView(View v, Canvas destCanvas, int padding, boolean pruneToDrawable) {
        final Rect clipRect = mTempRect;
        v.getDrawingRect(clipRect);

        boolean textVisible = false;

        destCanvas.save();
        if (v instanceof TextView && pruneToDrawable) {
            Drawable d = ((TextView) v).getCompoundDrawables()[1];
            clipRect.set(0, 0, d.getIntrinsicWidth() + padding, d.getIntrinsicHeight() + padding);
            destCanvas.translate(padding / 2.0f, padding / 2.0f);
            d.draw(destCanvas);
        } else {           
            destCanvas.translate(-v.getScrollX() + padding / 2, -v.getScrollY() + padding / 2);
            destCanvas.clipRect(clipRect, Op.REPLACE);
            v.draw(destCanvas);
        }
        destCanvas.restore();
    }
    
    /*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-6-7. S*/
    public void addInScreenNewItem(XViewContainer container, ItemInfo oldInfo, ItemInfo newInfo) {
    	Log.i("zdx1","   XWorkspace.addInScreenOtherItem-------call removePageItem");
    	xPagedView.removePageItem(oldInfo);
    	XPagedViewItem itemToAdd = new XPagedViewItem(mXContext, container, newInfo);
    	Log.i("zdx1","   XWorkspace.addInScreenOtherItem--------call addPagedViewItem---");
        xPagedView.addPagedViewItem(itemToAdd);
        Context c = mXContext.getContext();
        if (c instanceof XLauncher) {
            XLauncher xlauncher = (XLauncher) c;
            container.setTag(newInfo);
            container.setOnLongClickListener(xlauncher);
        }
    }
    /*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-6-7. E*/

    public boolean findVacantCellXY(int[] start) {
        if (xPagedView != null) {
             return  xPagedView.findVacantCellXY(start, getPageCount() - 1);
        }
        return false;
    }
    

    void updateShortcuts(ArrayList<ApplicationInfo> apps, IconCache iconCache) {
    	updateShortcuts(apps, iconCache, null, false);
    }
    void updateShortcuts(ArrayList<ApplicationInfo> apps, IconCache iconCache,Set<ApplicationInfo>updateApps) {
    	updateShortcuts(apps, iconCache, updateApps, false);
    }
    void updateShortcuts(ArrayList<ApplicationInfo> apps, IconCache iconCache,Set<ApplicationInfo>updateApps,boolean isCommendShortcut) {

        int pageCnt = xPagedView.getPageCount();
        int cellXCnt = xPagedView.getCellCountX();
        int cellYCnt = xPagedView.getCellCountY();
        
        //add by zhanggx1 for refresh mng view.s
		List<Integer> screenList = new ArrayList<Integer>();
		//add by zhanggx1 for refresh mng view.e

        for (int screen = 0; screen < pageCnt; screen++) {
            for (int cellX = 0; cellX < cellXCnt; cellX++) {
                for (int cellY = 0; cellY < cellYCnt; cellY++) {
                    XPagedViewItem item = xPagedView.findPageItemAt(screen, cellX, cellY);
                    if (item == null) {
                        continue;
                    }
                    DrawableItem drawableTarget = item.getDrawingTarget();
                    if (drawableTarget == null
                            || !(drawableTarget instanceof XShortcutIconView || drawableTarget instanceof XFolderIcon)) {
                        continue;
                    }
                    Object itemInfo = drawableTarget.getTag();
                    if (itemInfo == null || !(itemInfo instanceof ItemInfo)) {
                        continue;
                    }
                    
                    //add by zhanggx1 for refresh mng view.s
                    ItemInfo info = (ItemInfo)itemInfo;
                    if (!screenList.contains(info.screen)) {
                    	screenList.add(info.screen);
                    }
                    //add by zhanggx1 for refresh mng view.e

                    updateAnIcon(drawableTarget, (ItemInfo) itemInfo, apps, iconCache,updateApps,isCommendShortcut);
                }
            }
        }
        ((XLauncher)mXContext.getContext()).refreshMngViewOnUpdateWorkspace(screenList);
    }
    void deleteShortcuts(ArrayList<ApplicationInfo> apps, IconCache iconCache) {

        int pageCnt = xPagedView.getPageCount();
        int cellXCnt = xPagedView.getCellCountX();
        int cellYCnt = xPagedView.getCellCountY();

        for (int screen = 0; screen < pageCnt; screen++) {
            for (int cellX = 0; cellX < cellXCnt; cellX++) {
                for (int cellY = 0; cellY < cellYCnt; cellY++) {
                    XPagedViewItem item = xPagedView.findPageItemAt(screen, cellX, cellY);
                    if (item == null) {
                        continue;
                    }
                    DrawableItem drawableTarget = item.getDrawingTarget();
                    if (drawableTarget == null
                            || !(drawableTarget instanceof XShortcutIconView || drawableTarget instanceof XFolderIcon)) {
                        continue;
                    }
                    Object itemInfo = drawableTarget.getTag();
                    if (itemInfo == null || !(itemInfo instanceof ItemInfo)) {
                        continue;
                    }
                    deleteItemWhenSDCardUninstall(item, (ItemInfo)itemInfo,apps);
                }
            }
        }
    }
    private void deleteItemWhenSDCardUninstall(XPagedViewItem item,ItemInfo itemInfo,ArrayList<ApplicationInfo> apps){
    	 if (itemInfo instanceof ShortcutInfo) {
    		 if(itemInfo.itemType != LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT){
    			 return;
    		 }
    		 ShortcutInfo info = (ShortcutInfo) itemInfo;
    		 final Intent intent = info.intent;
             final ComponentName name = intent.getComponent();
             if(name == null) return;
             final int appCount = apps.size();
             for (int k = 0; k < appCount; k++) {
                 ApplicationInfo app = apps.get(k);
                 if (app.componentName.getPackageName().equals(name.getPackageName())) {
                	 xPagedView.removePagedViewItem(item);
                	 XLauncherModel.deleteItemFromDatabase(getXContext().getContext(), info);
                	 break;
                 }
             }
    	 }else if (itemInfo instanceof FolderInfo) {

             FolderInfo info = (FolderInfo) itemInfo;
             final ArrayList<ShortcutInfo> appsToRemoveFromFolder = new ArrayList<ShortcutInfo>();
             Iterator<ShortcutInfo> it = info.contents.iterator();
             ShortcutInfo s=null;
             while(it.hasNext()){
            	 s= it.next();
        		 if(s.itemType != LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT){
        			 return;
        		 }
        		 final ComponentName name = s.intent.getComponent();
        		 if(name ==null){
        			 continue;
        		 }
        		 final int appCount = apps.size();
        		 for (int k = 0; k < appCount; k++) {
                     ApplicationInfo app = apps.get(k);
                     if (app.componentName.getPackageName().equals(name.getPackageName())) {
                    	 appsToRemoveFromFolder.add(s);
                    	 break;
                     }
                 }
             }
             
             for (ShortcutInfo i : appsToRemoveFromFolder) {
                 info.remove(i);
                 XLauncherModel.deleteItemFromDatabase(getXContext().getContext(), i);
             }
    	 }
    	
    }
    private void updateAnIcon(DrawableItem drawableTarget, ItemInfo itemInfo,
            ArrayList<ApplicationInfo> apps, IconCache iconCache,Set<ApplicationInfo>updateApps,boolean isCommendShortcut) {
        if (itemInfo instanceof ShortcutInfo) {
            ShortcutInfo info = (ShortcutInfo) itemInfo;
            // We need to check for ACTION_MAIN otherwise getComponent() might
            // return null for some shortcuts (for instance, for shortcuts to
            // web pages.)
            final Intent intent = info.intent;
            final XShortcutIconView shortcutView = (XShortcutIconView) drawableTarget;
            final ComponentName name = intent.getComponent();
            if (info.itemType != LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT && name != null) {
                final int appCount = apps.size();
                for (int k = 0; k < appCount; k++) {
                    ApplicationInfo app = apps.get(k);
                    if (app.componentName.equals(name)) {
                        // update this one.
                        Log.i("XWorkspace", "updateAnIcon info ~~~~" + info);
                        if(isCommendShortcut && info.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION){
                        	info.setIcon(null);
                        	info.uri= XLauncherModel.getCommandAppDownloadUri(name.getPackageName(), info.title.toString());
                        	info.itemType = LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT;
                        }else if(!isCommendShortcut){
                        	info.setIcon(app.iconBitmap);
                        	info.uri =null;
                        	info.itemType = LauncherSettings.Favorites.ITEM_TYPE_APPLICATION;
                        }
                        if(updateApps != null){
                        	updateApps.add(app);
                        }

                        shortcutView.applyFromShortcutInfo(info, iconCache);
                        XLauncherModel.updateItemInDatabase(getXContext().getContext(), info);
                        break;
                    }
                }
            }
        } else if (itemInfo instanceof FolderInfo) {
            final XFolderIcon folderIcon = (XFolderIcon) drawableTarget;
            FolderInfo info = (FolderInfo) itemInfo;
            XFolder folder = folderIcon.mFolder;

            int iconSize = info.contents.size();
            for (int index = 0; index < iconSize; index++) {
                ShortcutInfo childInfo = info.contents.get(index);

                XPagedViewItem item = folder.findPageItemAt(childInfo.screen, childInfo.cellX,
                        childInfo.cellY);
                if (item == null) {
                    continue;
                }
                DrawableItem target = item.getDrawingTarget();
                if (target == null || !(target instanceof XShortcutIconView)) {
                    continue;
                }
                ItemInfo shortInfo = item.getInfo();
                if (shortInfo == null) {
                    continue;
                }

                updateAnIcon(target, shortInfo, apps, iconCache,updateApps,isCommendShortcut);
            }

            folder.invalidate();
            folderIcon.invalidate();
        }

    }
    
    void cleanDragData() {
        if (mDragFolderRingAnimator != null)
        {
//            hideFolderAccept(mDragFolderRingAnimator);
            mDragFolderRingAnimator.closeAnimation();
            mDragFolderRingAnimator = null;
        }
        
        if (mFolderRingAnimator != null) {
            mFolderRingAnimator.closeAnimation();
            mFolderRingAnimator = null;
        }
    }
    
    /*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-6-27. S***/
    private void resizeWidget(ItemInfo info, XViewContainer view) {
    	Log.i("zdx1","******XQuickActionWindowInfo.resizeWidget*******");
        // follow drag layer's solution
    	/* RK_ID: BUG21347 . AUT: SHENCHAO1 . DATE: 2013-08-22 . S */
    	if(view.getParasiteView() instanceof FavoriteWidgetView){
    		return;
    	}
    	/* RK_ID: BUG21347 . AUT: SHENCHAO1 . DATE: 2013-08-22 . E*/
    	
        final LauncherAppWidgetHostView hostView = (LauncherAppWidgetHostView) view.getParasiteView();
        AppWidgetProviderInfo pinfo = hostView.getAppWidgetInfo();
        if (pinfo == null) {
            return;
        } else if (pinfo.resizeMode == AppWidgetProviderInfo.RESIZE_NONE) {
        	return;
        }
        
        Log.i("zdx1","******XQuickActionWindowInfo.resizeWidget*******resizeMode:"+pinfo.resizeMode );
        pinfo.minResizeWidth = pinfo.minResizeHeight = 40;
        pinfo.resizeMode = AppWidgetProviderInfo.RESIZE_BOTH;
        
        Log.i("zdx1","******XQuickActionWindowInfo.resizeCell*******"+pinfo.provider);
        final ItemInfo itemInfo = info;
        //((XLauncher)mXContext.getContext()).getMainView().post(new Runnable() {
        //	 public void run() {
             	 Log.i("zdx1","******XQuickActionWindowInfo.resizeCell*******run()***");
                 XDragLayer dragLayer = ((XLauncher)mXContext.getContext()).getDragLayer();
                 dragLayer.addResizeFrame(((XLauncher)mXContext.getContext()), itemInfo, hostView);
       //      }
       //});
    }
    
    public float getPagedViewGlobalY2(){
        return mPagedViewGlobalY2;	
    }
    public float getPagedViewGlobalX2(){
    	return mPagedViewGlobalX2;
    }
    /*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-6-27. E***/

    private static final String TAG = "XWorkspace";

    
    FolderRingAnimator mFolderRingAnimator = null;
    public void showAddToFolder(int cellX, int cellY, XFolderIcon fi) {
        if (mFolderRingAnimator == null) {
            mFolderRingAnimator = new FolderRingAnimator(mXContext, fi);
        }
        mFolderRingAnimator.closeAnimation();
        mFolderRingAnimator.setFolderIcon(fi);
        mFolderRingAnimator.setCell(cellX, cellY);
        mFolderRingAnimator.setWorkspace(XWorkspace.this);
        mFolderRingAnimator.animateToAcceptState();
        showFolderAccept(mFolderRingAnimator);
    }

    void createAreaOnDrop() {
        if (!DESTRUCTIVE_REORDER ){
            commitTempPlacement();
            completeAndClearReorderHintAnimations();
            setItemPlacementDirty(false);
        }                    
        return;
    }

    public void buildBlurBitmap() {
        xPagedView.buildBlurBitmap();
    }

    public void clearBlurBitmap() {
        xPagedView.clearBlurBitmap();
    }
    
    public void setBlurEnable(boolean enable) {
        xPagedView.setBlurEnable(enable);
    }

    @Override
    public boolean onFingerCancel(MotionEvent e) {
    	XLauncher launcher = (XLauncher) getXContext().getContext();
    	if(launcher != null && launcher.getXScreenMngView() != null) {
    	    launcher.getXScreenMngView().cancelDragForWorkspace();
    	}
    	return super.onFingerCancel(e);
    }
    void addDraggingViewBack(ShortcutInfo info) {
        addInScreen(info,
                ((LauncherApplication) getXContext().getContext().getApplicationContext())
                        .getIconCache(), false);
    }
    
    
    public void filterApplicationExsitedInWorkspace(
			final List<ShortcutInfo> apps) {
    	final List<ShortcutInfo> toRemove = new ArrayList<ShortcutInfo>();
		final HashMap<String, ShortcutInfo> old = new HashMap<String, ShortcutInfo>();
		
		final Iterator<ShortcutInfo> itApps = apps.iterator();		
		final Collection<XPagedViewItem> pageValues = xPagedView.mItemIDMap.values();
		Iterator<XPagedViewItem> itWork;
		
		ShortcutInfo app = null;
		XPagedViewItem item = null;
		DrawableItem drawableTarget = null;
		Object itemInfo = null;		
		String key = null;
		while (itApps.hasNext()) {
			app = itApps.next();
			if (app.intent == null || app.intent.getComponent() == null) {
				continue;
			}
			
			key = app.intent.getComponent().flattenToShortString();
			if (old.containsKey(key)) {
				android.util.Log.i("dooba", "----------111------duplicate-xxxx-------" + key);
				toRemove.add(app);
				continue;
			}
			old.put(key, app);
			
			itWork = pageValues.iterator();
			while (itWork.hasNext()) {
				item = itWork.next();
				drawableTarget = item.getDrawingTarget();
				if (drawableTarget == null
						|| !(drawableTarget instanceof XShortcutIconView || drawableTarget instanceof XFolderIcon)) {
					continue;
				}
				itemInfo = drawableTarget.getTag();
				if (itemInfo == null) {
					continue;
				}
				if (itemInfo instanceof ShortcutInfo) {
					ShortcutInfo info = (ShortcutInfo) itemInfo;
					if (info.itemType != LauncherSettings.Favorites.ITEM_TYPE_APPLICATION) {
						continue;
					}
					final Intent intent = info.intent;
					final ComponentName name = intent.getComponent();
					if (name == null) {
						continue;
					}
					if (name.equals(app.intent.getComponent())) {
						android.util.Log.i("dooba", "----------11111------duplicate--------" + info.title);
						toRemove.add(app);
						break;
					}
				} else if (itemInfo instanceof FolderInfo) {
					FolderInfo info = (FolderInfo) itemInfo;
					int iconSize = info.contents.size();
					boolean found = false;
					for (int index = 0; index < iconSize; index++) {
						ShortcutInfo childInfo = info.contents.get(index);
						if (childInfo.itemType != LauncherSettings.Favorites.ITEM_TYPE_APPLICATION) {
						    continue;
						}
						final Intent intent = childInfo.intent;
						final ComponentName name = intent.getComponent();
						if (name == null) {
							continue;
						}
						if (name.equals(app.intent.getComponent())) {
							android.util.Log.i("dooba", "----------11111------duplicate--------" + info.title);
							toRemove.add(app);
							found = true;
							break;
						}
					}
					if (found) {
						break;
					}
				}
			}
		}
		apps.removeAll(toRemove);
	}
    
    
    
    
    
    
	public List<ApplicationInfo> filterApplicationExsitedInWorkspace(
			final ArrayList<ApplicationInfo> apps,IconCache iconCache) {
		LinkedList<ApplicationInfo> result = new LinkedList<ApplicationInfo>(apps);
		final HashMap<String, ApplicationInfo> old = new HashMap<String, ApplicationInfo>();		
		final LinkedList<ApplicationInfo> toRemove = new LinkedList<ApplicationInfo>();
		
		final Iterator<ApplicationInfo> itApps = result.iterator();		
		final Collection<XPagedViewItem> pageValues = xPagedView.mItemIDMap.values();
		Iterator<XPagedViewItem> itWork = null;
		
		ApplicationInfo app = null;
		XPagedViewItem item = null;
		DrawableItem drawableTarget = null;
		Object itemInfo = null;
		String key;
		
		while (itApps.hasNext()) {
			app = itApps.next();
			if (app.componentName == null) {
				continue;
			}
			key = app.componentName.flattenToShortString();
			if (old.containsKey(key)) {
				android.util.Log.i("dooba", "----------2222------duplicate-xxxx-------" + key);
				toRemove.add(app);
				continue;
			}
			old.put(key, app);
			
			itWork = pageValues.iterator();
			while (itWork.hasNext()) {
				item = itWork.next();
				drawableTarget = item.getDrawingTarget();
				if (drawableTarget == null
						|| !(drawableTarget instanceof XShortcutIconView || drawableTarget instanceof XFolderIcon)) {
					continue;
				}
				itemInfo = drawableTarget.getTag();
				if (itemInfo == null) {
					continue;
				}
				if (itemInfo instanceof ShortcutInfo) {
					ShortcutInfo info = (ShortcutInfo) itemInfo;
					
					if (info.itemType != LauncherSettings.Favorites.ITEM_TYPE_APPLICATION) {
						continue;
					}
					
					final Intent intent = info.intent;
					final ComponentName name = intent.getComponent();
					if (name == null) {
						continue;
					}					
					if (name.equals(app.componentName)) {
						final XShortcutIconView shortcutView = (XShortcutIconView) drawableTarget;
						info.setIcon(app.iconBitmap);
						shortcutView.applyFromShortcutInfo(info, iconCache);
                        XLauncherModel.updateItemInDatabase(getXContext().getContext(), info);
                        android.util.Log.i("dooba", "----------22222------duplicate--------" + info.title);
						toRemove.add(app);
						break;
					}
				} else if (itemInfo instanceof FolderInfo) {
					final XFolderIcon folderIcon = (XFolderIcon) drawableTarget;
					XFolder folder = folderIcon.mFolder;
					FolderInfo info = (FolderInfo) itemInfo;
					int iconSize = info.contents.size();
					boolean found = false;
					for (int index = 0; index < iconSize; index++) {
						ShortcutInfo childInfo = info.contents.get(index);
						if (childInfo.itemType != LauncherSettings.Favorites.ITEM_TYPE_APPLICATION) {
						    continue;
						}
						final Intent intent = childInfo.intent;
						final ComponentName name = intent.getComponent();
						if (name == null) {
							continue;
						}
						if (name.equals(app.intent.getComponent())) {
							XPagedViewItem pageViewItem = folder.findPageItemAt(childInfo.screen, childInfo.cellX, childInfo.cellY);
							if (pageViewItem == null) {
								continue;
							}
							DrawableItem target = pageViewItem.getDrawingTarget();
							if (target == null|| !(target instanceof XShortcutIconView)) {
								continue;
							}
							final XShortcutIconView shortcutView = (XShortcutIconView) target;
							childInfo.setIcon(app.iconBitmap);
							shortcutView.applyFromShortcutInfo(childInfo, iconCache);
	                        XLauncherModel.updateItemInDatabase(getXContext().getContext(), childInfo);
	                        android.util.Log.i("dooba", "----------22222------duplicate--------" + childInfo.title);
							toRemove.add(app);
							found = true;
							break;
						}
					}
					if (found) {
						break;
					}
				}
			}
		}
		result.removeAll(toRemove);
		return result;
	}

	public boolean findCellXYNextScreen(int[] cell, ShortcutInfo item) {
		boolean hasVacantCell = findVacantCellXY(cell);
		boolean addInNextScreen = false;
		if (hasVacantCell && !(cell[0] == 0 && cell[1] == 0)) {
			int pageCnt = getPagedView().getPageCount();
			if(cell[2] < pageCnt-1){
				cell[2] = cell[2] +1;
			}else{
				if (pageCnt < SettingsValue.getLauncherScreenMaxCount(this
						.getXContext().getContext())) {
					getPagedView().addNewScreen();
					cell[2] = getPagedView().getPageCount() - 1;
				}
			}
			addInNextScreen = true;

		}else if(hasVacantCell){
			return true;
		}
		if (addInNextScreen) {
			cell[0] = 0;
			cell[1] = 0;
			return true;
		} else {
			return findCellWithinXshortcutOrFolderIcon(cell, item, hasVacantCell);
		}
	}

	public boolean findCellXY(int[] cell, final ShortcutInfo item) {
		boolean hasVacantCell = findVacantCellXY(cell);

		return hasVacantCell ? hasVacantCell : findCellWithinXshortcutOrFolderIcon(cell,
				item, hasVacantCell);
	}

	private boolean findCellWithinXshortcutOrFolderIcon(int[] cell, final ShortcutInfo item,
			boolean hasVacantCell) {
		boolean ret = true;
		boolean abrot = false;
		if (!hasVacantCell) {
			XPagedView pagedView = getPagedView();
			int pageCount = pagedView.getPageCount();
			int cellXCnt = pagedView.getCellCountX();
			int cellYCnt = pagedView.getCellCountY();
			for (int screen = pageCount - 1; screen >= 0; screen--) {
				for (int cellY = cellYCnt - 1; cellY >= 0; cellY--) {
					for (int cellX = cellXCnt - 1; cellX >= 0; cellX--) {
						XPagedViewItem pagedViewItem = pagedView
								.findPageItemAt(screen, cellX, cellY);
						if (pagedViewItem == null) {
							cell[0] = cellX;
							cell[1] = cellY;
							cell[2] = screen;
							ret = true;
							return ret;
						}
						DrawableItem drawableTarget = pagedViewItem
								.getDrawingTarget();
						if (drawableTarget != null) {
							if ((drawableTarget.getTag()) instanceof ShortcutInfo) {
								cell[0] = cellX;
								cell[1] = cellY;
								cell[2] = screen;
								ret = false;
								abrot = true;
								break;
							}
							if ((drawableTarget) instanceof XFolderIcon) {
								XFolderIcon fi = (XFolderIcon) drawableTarget;
								if (fi.acceptDrop(item)) {
									cell[0] = cellX;
									cell[1] = cellY;
									cell[2] = screen;
									ret = false;
									abrot = true;
									break;
								} else {
									continue;
								}

							} else {
								continue;
							}

						}
					}
					if (abrot) {
						break;
					}
				}
				if (abrot) {
					break;
				}
			}
			return hasVacantCell;
		}
		return ret;

    }
	
	/* RK_ID: RK_LOCATE_APP. AUT: zhangdxa DATE: 2013-8-6 S*/
	public LocateItem getItemByComponent(ComponentName cname){
    	String componentName = cname.flattenToString();
        Set<Map.Entry<Long, XPagedViewItem>> set = xPagedView.mItemIDMap.entrySet();
        for (Map.Entry<Long, XPagedViewItem> map : set) {
        	if( map == null){
        		continue;
        	}
        	XPagedViewItem item = map.getValue();
            ItemInfo itemInfo = item.getInfo();
            if ( (itemInfo instanceof ShortcutInfo) && 
            	 (itemInfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION)){
                ShortcutInfo shortcutInfo = (ShortcutInfo)itemInfo;                
                ComponentName component = shortcutInfo.intent.getComponent();
                if (component != null && componentName.equals(component.flattenToString())) {
                	LocateItem locateItem = new LocateItem(itemInfo, null);
                	return locateItem;
                }                
            }else if( itemInfo instanceof FolderInfo ){
            	 FolderInfo folderInfo = (FolderInfo) itemInfo;
                 Iterator<ShortcutInfo> it = folderInfo.contents.iterator();
                 while(it.hasNext()){
                	 ShortcutInfo shortcutInfo = it.next();      
                	 if ( shortcutInfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION){      
                        ComponentName component = shortcutInfo.intent.getComponent();
                        if (component != null && componentName.equals(component.flattenToString())) {
                        	LocateItem locateItem = new LocateItem(shortcutInfo, itemInfo);
                        	return locateItem;
                        }                
                    }
                }
            }
        }
        return null;
    }
	/* RK_ID: RK_LOCATE_APP. AUT: zhangdxa DATE: 2013-8-6 E*/
	
	
	public void ReorderItemCurrentScreen(){
		ArrayList<XPagedViewItem> items= xPagedView.getChildrenAt(getCurrentPage());
		SwapItem empty_Item = new SwapItem();
		SwapItem stone_Item = new SwapItem(Type.stone);
		Iterator<XPagedViewItem> it = items.iterator();
		SwapItem [][] occupied = new SwapItem [mCountY][mCountX];
		
		//init array
		for(int x = 0; x<mCountY; x++){
			for(int y = 0;y<mCountX;y++){
				occupied[x][y] = empty_Item;
			}
		}
		
		//inflate items 
		XPagedViewItem item = null;
		while(it.hasNext()){
			item = it.next();
			ItemInfo  itemInfo= item.getInfo();
			if(itemInfo instanceof ShortcutInfo || itemInfo instanceof FolderInfo){
				occupied[itemInfo.cellY][itemInfo.cellX] = new SwapItem(Type.chessman, item);
			}
			if(itemInfo instanceof LenovoWidgetViewInfo || itemInfo instanceof LauncherAppWidgetInfo){
				if(itemInfo.spanX ==1 && itemInfo.spanY ==1){
					occupied[itemInfo.cellY][itemInfo.cellX] = new SwapItem(Type.chessman, item);
				}else{
					for(int x = itemInfo.cellY ; x<itemInfo.cellY+itemInfo.spanY;x++ ){
						for(int y = itemInfo.cellX ;y <itemInfo.cellX + itemInfo.spanX;y++){
							occupied[x][y] = stone_Item;
						}
					}
				}
			}
		}
		
		//reorder
		Reorder r = new Reorder();
		Log.d("gecn1", r.printorder(occupied));
		r.setReorderAlgorithm( new Z_Reorder());
		r.reorder(occupied);
		for(int x = 0; x<mCountY; x++){
			for(int y = 0;y<mCountX;y++){
				item = (XPagedViewItem) occupied[x][y].item;
				animateChildToPosition(item, getCurrentPage(), y,x,
						REORDER_ANIMATION_DURATION, 0, DESTRUCTIVE_REORDER,
						false);
			}
		}
		Log.d("gecn1", "/n=======================================/n");
		Log.d("gecn1", r.printorder(occupied));
		
	}

	 /** RK_ID: RK_NEWFEATURE_SHOW_MISSED_NUMBER. AUT: zhanglz1 DATE: 2013-05-06 S */
    private class ShowmissedTask extends AsyncTask<Void, Void, Integer> {
		@Override
		protected Integer doInBackground(Void... arg0) {	
			try {
				com.lenovo.launcher.components.XAllAppFace.utilities.Utilities.getMissedNumFirsttime((XLauncher)getXContext().getContext());
				return 0;
			} catch (Exception e) {
				// TODO: handle exception
				return 0;
			}
			
		}
		
		@Override
	    protected void onPostExecute(Integer result) {
			for (int screen = 0; screen < getPageCount(); screen++) {
		        for (int cellX = 0; cellX < getPageCellCountX(); cellX++) {
		            for (int cellY = 0; cellY < getPageCellCountY(); cellY++) {
		                XPagedViewItem item = xPagedView.findPageItemAt(screen, cellX, cellY);
		                if (item == null) {
		                    continue;
		                }
		                DrawableItem drawableTarget = item.getDrawingTarget();
		                if (drawableTarget == null
		                        || !(drawableTarget instanceof XShortcutIconView || drawableTarget instanceof XFolderIcon)) {
		                    continue;
		                }
		                Object itemInfo = drawableTarget.getTag();
		                if (itemInfo == null || !(itemInfo instanceof ItemInfo)) {
		                    continue;
		                }
		                ItemInfo info = (ItemInfo)itemInfo;
		                 if (drawableTarget instanceof XShortcutIconView) {
		                 
		                	 if (com.lenovo.launcher.components.XAllAppFace.utilities.Utilities.getShowMissedNum(info, com.lenovo.launcher.components.XAllAppFace.utilities.Utilities.HAS_NEW_MSG,
		 							com.lenovo.launcher.components.XAllAppFace.utilities.Utilities.missedNum[com.lenovo.launcher.components.XAllAppFace.utilities.Utilities.HAS_NEW_MSG]) > 0) {// 有未读短信
		 						((XShortcutIconView) drawableTarget)
		 								.showTipForNewAdded(com.lenovo.launcher.components.XAllAppFace.utilities.Utilities.missedNum[com.lenovo.launcher.components.XAllAppFace.utilities.Utilities.HAS_NEW_MSG]);
		 					} else if (com.lenovo.launcher.components.XAllAppFace.utilities.Utilities.getShowMissedNum(info,
		 							com.lenovo.launcher.components.XAllAppFace.utilities.Utilities.HAS_NEW_CALL,
		 							com.lenovo.launcher.components.XAllAppFace.utilities.Utilities.missedNum[com.lenovo.launcher.components.XAllAppFace.utilities.Utilities.HAS_NEW_CALL]) > 0) {// 有未接来电
		 						((XShortcutIconView) drawableTarget)
		 								.showTipForNewAdded(com.lenovo.launcher.components.XAllAppFace.utilities.Utilities.missedNum[com.lenovo.launcher.components.XAllAppFace.utilities.Utilities.HAS_NEW_CALL]);
		 					}else{
		 						((XShortcutIconView) drawableTarget).dismissTip();
		 					}
		                 }
		               
		            }
		        }
		    }
		}
	}
	public void showmissed() {
		new ShowmissedTask().execute(new Void[]{});
	}

	public void updateMissedView(int type, int number) {
		// TODO Auto-generated method stub
		if (type == 0)
			Utilities.missedNum[0] = number;
		else if (type == 1)
			Utilities.missedNum[1] = number;
		showmissed();
	}
    //add by zhanggx1 for reordering all pages. s
    /**
     * 取得所也页面的单元格占用情况
     * @return
     */
    private SwapItem[][][] getAllOccupiedInfo() {
    	final int defaultScreen = getDefaultPage();
    	Collection<XPagedViewItem> items= xPagedView.mItemIDMap.values();
		SwapItem empty_Item = new SwapItem();
		SwapItem stone_Item = new SwapItem(Type.stone);
		Iterator<XPagedViewItem> it = items.iterator();
		final int pageCnt = getPageCount();
		SwapItem[][][] occupied = new SwapItem[pageCnt][mCountX][mCountY];
		//init array
		for (int screen = 0; screen < pageCnt; screen++) {
			SwapItem tmpItem = screen == defaultScreen ? stone_Item : empty_Item;
			for (int y = 0; y < mCountY; y++) {
				for (int x = 0; x < mCountX; x++) {
					occupied[screen][x][y] = tmpItem;
				}
			}
		}		
		//inflate items 
		XPagedViewItem item = null;
		while (it.hasNext()) {
			item = it.next();
			ItemInfo itemInfo= item.getInfo();
			if (itemInfo.screen < 0
					|| itemInfo.screen >= pageCnt
					|| itemInfo.cellX < 0
					|| itemInfo.cellX >= mCountX
					|| itemInfo.cellY < 0
					|| itemInfo.cellY >= mCountY
					|| itemInfo.screen == defaultScreen) {
				continue;
			}
			if (itemInfo instanceof ShortcutInfo || itemInfo instanceof FolderInfo) {
				occupied[itemInfo.screen][itemInfo.cellX][itemInfo.cellY] = new SwapItem(Type.chessman, item);
			}
			if (itemInfo instanceof LenovoWidgetViewInfo || itemInfo instanceof LauncherAppWidgetInfo) {
				if (itemInfo.spanX == 1 && itemInfo.spanY == 1) {
					occupied[itemInfo.screen][itemInfo.cellX][itemInfo.cellY] = new SwapItem(Type.chessman, item);
				} else {
					int maxX = Math.min(mCountX, itemInfo.cellX + itemInfo.spanX);
					int maxY = Math.min(mCountY, itemInfo.cellY + itemInfo.spanY);
					for (int y = itemInfo.cellY; y < maxY; y++) {
						for (int x = itemInfo.cellX ; x < maxX; x++) {
							occupied[itemInfo.screen][x][y] = stone_Item;
						}
					}
				}
			}
		}
		return occupied;
    }
    
    /**
     * 多页Z字整理
     */
    public void reorderItemAllScreen(){
    	int pageCnt = getPageCount();
    	int taskNum = pageCnt * mCountY * mCountX;
    	final WholeOrderTask orderTask = new WholeOrderTask(taskNum);
    	final OrderRunnable countEnd  = new OrderRunnable(orderTask);
    	mOrderPendingList.add(orderTask);
    	
    	SwapItem[][][] occupied = getAllOccupiedInfo();
		
		//reorder
		Reorder r = new Reorder();
//		Log.d("dooba", r.printAllOrder(occupied));
		r.setReorderAlgorithm( new All_Z_Reorder());
		boolean hasMove = r.reorderAll(occupied);//默认是不跨页的
		
		if (!hasMove) {
			orderTask.resetTask();
			return;
		}
		
//		Log.d("dooba", "/n=======================================/n");
//		Log.d("dooba", r.printAllOrder(occupied));
		
		XPagedViewItem item;
		ItemInfo info;
		
		for (int screen = 0; screen < pageCnt; screen++) {
			for (int cellY = 0; cellY < mCountY; cellY++) {
				for (int cellX = 0; cellX < mCountX; cellX++) {
					item = (XPagedViewItem)occupied[screen][cellX][cellY].item;
					if (item == null) {
						orderTask.removeATask();
						continue;
					}
					info = item.getInfo();
					if (info != null
							&& (info.screen != screen
							|| info.cellX != cellX
							|| info.cellY != cellY)) {
						if (info instanceof ShortcutInfo) {
						android.util.Log.i("dooba", "--------reorderAll-------------" + ((ShortcutInfo)info).title);
						}
						if (xPagedView.mOccupied[screen][cellX][cellY]) {
							orderTask.removeATask();
							continue;
						}
						animateChildToPosition(item, screen, cellX, cellY,
								REORDER_ANIMATION_DURATION, 0, DESTRUCTIVE_REORDER,
								false, countEnd);
						XLauncherModel.addOrMoveItemInDatabase(mContext.getContext(),
								info, info.container, screen, cellX, cellY);
					} else {
						orderTask.removeATask();
					}
				}
			}
		}
	}
	private Alarm mAutoReorderAlarm = new Alarm();
	private OnAlarmListener mAutoReorderListener = new OnAlarmListener() {		
		@Override
		public void onAlarm(Alarm alarm) {
			reorderItemAllScreen();
		}
	};
	
	public void autoReorder() {
		mAutoReorderAlarm.cancelAlarm();
		mAutoReorderAlarm.setOnAlarmListener(mAutoReorderListener);
		mAutoReorderAlarm.setAlarm(500L);
	}
	
	public boolean isReordering() {
		return mOrderPendingList.size() != 0;
	}
	
	public void setReorderingChangedListener(ReorderingChangedListener l) {
		mReorderChangedListener = l;
	}
	
	interface ReorderingChangedListener {
		void onReorderEnd();
	}
	
	private ReorderingChangedListener mReorderChangedListener;
	private static final ArrayList<WholeOrderTask> mOrderPendingList = new ArrayList<WholeOrderTask>();
	
	private class WholeOrderTask {
		private int mTaskNum = 0;
		
		public WholeOrderTask(int taskNum) {
			mTaskNum = taskNum;
		}
		
		public void removeATask() {
			if (mTaskNum <= 0) {
				return;
			}
			mTaskNum--;
			if (mTaskNum == 0) {
				resetTask();
			}
		}
		
		public void resetTask() {
			mTaskNum = 0;
			mOrderPendingList.remove(this);
			if (mReorderChangedListener != null) {
				mReorderChangedListener.onReorderEnd();
			}
		}
	}
	private static class OrderRunnable implements Runnable {
		private WholeOrderTask mOrderTask;
		
		public OrderRunnable(WholeOrderTask orderTask) {
			mOrderTask = orderTask;
		}		
		@Override
		public void run() {
			if (mOrderTask != null) {
				mOrderTask.removeATask();
			}
		}
	}
}
