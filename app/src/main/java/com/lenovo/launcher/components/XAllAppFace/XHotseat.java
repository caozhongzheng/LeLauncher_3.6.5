package com.lenovo.launcher.components.XAllAppFace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.XDragController.XDragListener;
import com.lenovo.launcher.components.XAllAppFace.XFolderIcon.FolderRingAnimator;
import com.lenovo.launcher.components.XAllAppFace.slimengine.BaseDrawableGroup;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;
import com.lenovo.launcher.components.XAllAppFace.utilities.Utilities;
import com.lenovo.launcher2.commoninterface.Alarm;
import com.lenovo.launcher2.commoninterface.ApplicationInfo;
import com.lenovo.launcher2.commoninterface.FolderInfo;
import com.lenovo.launcher2.commoninterface.IconCache;
import com.lenovo.launcher2.commoninterface.ItemInfo;
import com.lenovo.launcher2.commoninterface.LauncherSettings;
import com.lenovo.launcher2.commoninterface.LeosItemInfo;
import com.lenovo.launcher2.commoninterface.OnAlarmListener;
import com.lenovo.launcher2.commoninterface.ShortcutInfo;
import com.lenovo.launcher2.customizer.Debug.R5;
import com.lenovo.launcher2.customizer.SettingsValue;

public class XHotseat extends BaseDrawableGroup implements XDropTarget, XDragSource, XDragListener{

    private XLauncher mXLauncher;
    private XHotseatCellLayout mContent;
    private LauncherApplication mLA;

    private int mCellCountX = 5;
    private int mCellCountY = 1;
    public final boolean mIsAllowFolder = true;
    public boolean mIsFolderAccept = false;
    public static int mMaxCount = 5;
    public XHotseat(XLauncherView context, RectF rectF) {
        super(context);
        this.localRect = rectF;
        mXContext = context;

        mXLauncher = (XLauncher) context.getContext();
        mLA = (LauncherApplication) mXLauncher.getApplicationContext();
        
        mMaxCount = mCellCountX= mLA.getResources().getInteger(R.integer.hotseat_cellx_count);

        init(context);
        tempchildcount = mContent.getChildCount();
    }

    @Override
    public void resize(RectF rect) {
        super.resize(rect);
        mContent.resize(new RectF(0, 0, rect.width(), rect.height()));
    }

    private void init(XContext context) {
        // add a icon group.
        mContent = new XHotseatCellLayout(context, mXLauncher);
        addItem(mContent);
//        resetLayout(context);
        
    }

    
//    private void resetLayout(XContext context) {
////        mContent.clearAllItems();
// /*RK_ID:RK_SINGLE_LAYER liuyg1 2013-6-13. S*/
////        Drawable icon = mLA.getResources().getDrawable(R.drawable.all_apps_button_icon);
////        OnClickListener listener = new OnClickListener() {
////            @Override
////            public void onClick(DrawableItem item) {
////                if (mXLauncher != null) {
////                    mXLauncher.onClickAllAppsButton(item);
////                }
////            }
////        };
////        mContent.setChildDrawable(null, icon, sAllAppsButtonRank, listener, null);
// /*RK_ID:RK_SINGLE_LAYER liuyg1 2013-6-13. E*/ 
////       mContent.markCellsForView(2, 1, 1, true);
//    }

	public void bindInfo(ItemInfo item, IconCache iconCache) {
        mContent.bindInfo(item, iconCache);
    }
//bug17646
    public void validateHotseat(){
    	mContent.validateHotseat();
    }
    public void removeView(DrawableItem cell) {
        int index = mContent.removeItem(cell);
//        if (index >= 0) {
//            XIconDrawable icon = new XIconDrawable(getXContext(), null);
////        icon.setOnLongClickListener(mXLauncher);
// /*RK_ID:RK_SINGLE_LAYER liuyg1 2013-6-13. S*/
//            //mContent.addItem(icon, index);
// /*RK_ID:RK_SINGLE_LAYER liuyg1 2013-6-13. E*/
//        }

        if (cell != null && cell.getTag() instanceof ItemInfo){
            mXLauncher.removePopupWindow((ItemInfo) cell.getTag());
        }
        DrawableItem item = mContent.getChildAt(index);
//        mContent.bindInfo(item, mXLauncher.getIconCache());
        if(item!=null){
        	item.setVisibility(true);
        	  //Log.d("liuyg123"," removeView item.setVisibility(true);");
        }
    }

    public void refreshIconStyle(IconCache iconCache, boolean onlyBitmap) {
        if (mContent != null) {
            mContent.refreshIconStyle(iconCache, onlyBitmap);
            for (int i = 0; i < mCellCountX; i++) {
    			for (int k = 0; k < mCellCountY; k++) {
    				DrawableItem view = mContent.getChildAt(i, k);
    				if (view == null) {
    					continue;
    				}
    				if (view instanceof XShortcutIconView) {
    						((XShortcutIconView) view).showShadow(mContent
    								.getIconSize());
    				}else if(view instanceof XFolderIcon){
    					((XFolderIcon) view).showShadow(mContent
								.getIconSize());
    				}
    			}
    		}
        }
    }

    // ///////////////////////////////////////////
    // interface from ics
    // ///////////////////////////////////////////

//    static final int sAllAppsButtonRank = 2;

    /* Get the orientation invariant order of the item in the hotseat for persistence. */
    public int getOrderInHotseat(int x, int y) {
        return x;
    }

    /**
     * Add add icons in the each blank hotseat cell
     * @author zhanggx1
     * @date 2011-12-14
     */
 /*RK_ID:RK_SINGLE_LAYER liuyg1 2013-6-13. S*/
//    public void initDockAddIcons() {
//        for (int cellX = 0; cellX < mCellCountX; cellX++) {
//            for (int cellY = 0; cellY < mCellCountY; cellY++) {
//                createDockAddIcon(cellX, cellY);
//            }
//        }
//    }
 /*RK_ID:RK_SINGLE_LAYER liuyg1 2013-6-13. E*/
    
//    public static boolean isAllAppsButtonRank(int rank) {
//        return rank == sAllAppsButtonRank;
//    }

//    void createDockAddIcon(int cellX, int cellY) {
 /*RK_ID:RK_SINGLE_LAYER liuyg1 2013-6-13. S*/
//    	 resetLayout(mXContext);
//        if (cellX == sAllAppsButtonRank) {
//            return;
//        }
//        DrawableItem child = mContent.getChildAt(cellX, cellY);
//        Object object = child.getTag();
//        if (object != null) {
//            return;
//        }
//
//        Drawable b = mLA.mLauncherContext.getDrawable(R.drawable.dock_add_button_icon);
//
//        // make a LeosItemInfo object to be the tag
//        LeosItemInfo shortCut = createDockAddShortcutInfo(cellX, cellY);
//        OnClickListener listener = new DrawableItem.OnClickListener() {
//            @Override
//            public void onClick(DrawableItem item) {
//                if (mXLauncher != null) {
//                    LeosItemInfo tag = (LeosItemInfo) item.getTag();
//                    tag.intent = mContent.getDockAddIntent(tag.cellX, tag.cellY);
//                    mXLauncher.addHotseatShortcutsForLeos(item);
//                }
//            }
//        };
//
//        mContent.setChildDrawable(shortCut, b, cellX, listener, null);
//        mContent.markCellsForView(cellX, 1, 1, false);
 /*RK_ID:RK_SINGLE_LAYER liuyg1 2013-6-13. E*/
 //   }

    /**
     * Create the Tag of the add icon in the cell(cellX, cellY)
     * @author zhanggx1
     * @date 2011-12-15
     * @param cellX
     * @param cellY
     * @return LeosItemInfo
     */
    LeosItemInfo createDockAddShortcutInfo(int cellX, int cellY) {
        LeosItemInfo shortCut = new LeosItemInfo();
        shortCut.screen = cellX;
        shortCut.cellX = cellX;
        shortCut.cellY = cellY;
        shortCut.container = LauncherSettings.Favorites.CONTAINER_HOTSEAT;
        shortCut.spanX = 1;
        shortCut.spanY = 1;
        return shortCut;
    }

    public XHotseatCellLayout getLayout() {
        return mContent;
    }
    
    public void changeHotseatThemes(ArrayList<ApplicationInfo> apps) { 
    	ItemInfo info;
        DrawableItem view;
        
        for (int i = 0; i < mCellCountX; i++) {
            for (int k = 0; k < mCellCountY; k++) {
                view = mContent.getChildAt(i, k);
                if (view == null) {
                    continue;
                } 
 /*RK_ID:RK_SINGLE_LAYER liuyg1 2013-6-13. S*/
//                if (i == sAllAppsButtonRank) {
//                	Drawable icon = mLA.mLauncherContext.getDrawable(R.drawable.all_apps_button_icon);
//                	int width = icon.getIntrinsicWidth();
//                    int height = icon.getIntrinsicHeight();
//                    
//                    width = Math.min(width, (int) view.getWidth());
//                    height = Math.min(height, (int) view.getHeight());
//                    view.resize(new RectF(0, 0, width, height));
//                    view.setBackgroundDrawable(icon);
//                    centerItem(sAllAppsButtonRank, width, height);
//                    
//                	continue;
//                }
 /*RK_ID:RK_SINGLE_LAYER liuyg1 2013-6-13. E*/
                if (view.getTag() == null) {
                	continue;
                }
                info = (ItemInfo)view.getTag();
                if (info instanceof LeosItemInfo) {
                	Drawable icon = mLA.mLauncherContext.getDrawable(R.drawable.dock_add_button_icon);;
                	int width = (int)view.getWidth();
                    int height = (int)view.getHeight();
                    
                    view.resize(new RectF(0, 0, width, height));
                    view.setBackgroundDrawable(icon);
                    centerItem(info.cellX, width, height);
                    
                	continue;
                }
                mXLauncher.reloadAnIcon(apps, info, view);
              //test by dining 2013-07-05  reset icon shadow
                if(view instanceof XShortcutIconView){
                	if(mContent != null){
                		((XShortcutIconView) view).showShadow(mContent.getIconSize());
                	}
                }else if(view instanceof XFolderIcon){
                	if(mContent != null){
					((XFolderIcon) view).showShadow(mContent
							.getIconSize());
                	}
				}
            }
        }
        mContent.setBackgroundDrawable(mLA.mLauncherContext.getDrawable(R.drawable.hotseat_bg_panel));
        invalidate();
    }
    
    public void centerItem(int index, int width, int height) {
    	mContent.centerItem(index, width, height);
    }
    
    public void initForTheme() {
 /*RK_ID:RK_SINGLE_LAYER liuyg1 2013-6-13. S*/
//    	Drawable icon = mLA.mLauncherContext.getDrawable(R.drawable.all_apps_button_icon);
//    	int width = icon.getIntrinsicWidth();
//        int height = icon.getIntrinsicHeight();
//        
//        DrawableItem view = mContent.getChildAt(sAllAppsButtonRank, 0);
//        width = Math.min(width, (int) view.getWidth());
//        height = Math.min(height, (int) view.getHeight());
//
//        view.resize(new RectF(0, 0, width, height));
//        view.setBackgroundDrawable(icon);
//        centerItem(sAllAppsButtonRank, width, height);
 /*RK_ID:RK_SINGLE_LAYER liuyg1 2013-6-13. E*/
        
    	mContent.setBackgroundDrawable(mLA.mLauncherContext.getDrawable(R.drawable.hotseat_bg_panel));
    	invalidate();
    }
    
    private boolean willCreateFolder(DrawableItem cell, ItemInfo newinfo) {
        if (cell == null)
        {
            return true;
        }
                             
        boolean aboveShortcut = (newinfo instanceof ShortcutInfo);
        boolean willBecomeShortcut = (cell.getTag() instanceof ShortcutInfo);

        if (aboveShortcut && willBecomeShortcut) {
            ShortcutInfo sourceInfo = (ShortcutInfo) newinfo;
            ShortcutInfo destInfo = (ShortcutInfo) cell.getTag();
            if (sourceInfo.equalsIgnorePosition(destInfo)) {
                return false;
            }            
        }
        return true;
    }
    public static final boolean DESTRUCTIVE_REORDER = false;
    @Override
    public boolean acceptDrop(XDragObject dragObject) {
        waitAnimationEnd();
    	if (dragObject.dragInfo instanceof SimpleItemInfo
    			|| dragObject.dragInfo instanceof XScreenMngView.PreviewInfo) {
    		Log.d("liuyg123","acceptDrop not accept");
//    		if (mShowToast == null) {
//                mShowToast = new ShowToast(R.string.cannot_be_placed_on_hotseat);
//            } else {
//                mShowToast.setMessageId(R.string.cannot_be_placed_on_hotseat);
//            }
//            mXContext.post(mShowToast);
    		return false;
    	}
    	
    	if (!(dragObject.dragInfo instanceof ItemInfo)) {
//    		if (mShowToast == null) {
//                mShowToast = new ShowToast(R.string.cannot_be_placed_on_hotseat);
//            } else {
//                mShowToast.setMessageId(R.string.cannot_be_placed_on_hotseat);
//            }
//            mXContext.post(mShowToast);
    		return false;
    	}    	

        final int[] touchXY = new int[] { dragObject.x, dragObject.y };
 /*RK_ID:RK_SINGLE_LAYER liuyg1 2013-6-13. S*/
        mTargetCell = mContent.findTargetCell(mContent.getChildCount(),touchXY[0], touchXY[1]);
//        mTargetCell[0] = mContent.findNearestCellIndex(touchXY[0], touchXY[1],mTargetCell);
        if(dragObject.dragSource==this&&mContent.getChildCount() <= mMaxCount){
        	return true;
        }                    
        
       // Log.d("liuyg123","acceptDrop  cellx=="+mTargetCell[0]+" celly ===="+mTargetCell[1]);
        boolean retVal = true;
      
        DrawableItem acell = (DrawableItem)mContent.getChildAt(mTargetCell[0], mTargetCell[1]);
                if ( acell != null && acell.getTag() instanceof LeosItemInfo && ((LeosItemInfo)acell.getTag()).screen == -1 )
        {//Log.d("liuyg123","acell != null && acell.getTag() instanceof LeosItemInfo && ((LeosItemInfo)acell.getTag()).screen == -1 acceptDrop false");
            retVal = false;
        }
        /*RK_ID:RK_SINGLE_LAYER liuyg1 2013-6-13. E*/  
        else
        {
            ItemInfo info = (ItemInfo) dragObject.dragInfo;
            switch (info.itemType) {
            case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
            case LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT:
                //因为app拖拽至文件夹暂未完成，先进行如下处理。
                DrawableItem cell = (DrawableItem)mContent.getChildAt(mTargetCell[0], mTargetCell[1]);
                if (cell instanceof XFolderIcon)
                {
                    XFolderIcon fi = (XFolderIcon) cell;
                    retVal = fi.acceptDrop(info);             
                }
                else
                {
                	//Log.e("liuyg123","acceptDrop bbbbbbbbbbbbbbbbbbbb111111");
                	//return true;
      
//                    return willCreateFolder(cell, info);
                }
                break;
            case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
            	 retVal = false;
                 break;
            case LauncherSettings.Favorites.ITEM_TYPE_FOLDER:
            	//Log.d("liuyg123","acceptDrop ITEM_TYPE_FOLDER");
            	retVal = false;
            	if(mIsAllowFolder){
            		if (mContent.getChildCount() < mMaxCount)
            		{
            			Log.d("liuyg123","acceptDrop folder yes");
            			retVal = true;
            		}
            	}
                break;
            case LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET:
            case LauncherSettings.Favorites.ITEM_TYPE_LEOSWIDGET:
            /*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. S***/
            case LauncherSettings.Favorites.ITEM_TYPE_COMMEND_APPWIDGET:
            /*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. E***/
                retVal = false;
                break;
            default:
                throw new IllegalStateException("Unknown item type: " + info.itemType);
            }
          
        }
        if(!retVal){
        	return false;
        }
        if(dragObject.dragSource!=this){
        	if(checkFolderFeedback(dragObject.x,(ItemInfo) dragObject.dragInfo, mTargetCell)){
        		return retVal;
        	}
        }
       
        if(dragObject.dragSource!=this&&mContent.getChildCount()>=mMaxCount){
//        	if (mShowToast == null) {
//        		mShowToast = new ShowToast(R.string.cannot_be_placed_on_full_hotseat);
//        	} else {
//        		mShowToast.setMessageId(R.string.cannot_be_placed_on_full_hotseat);
//        	}
//        	mXContext.post(mShowToast);
        	return false;
        }

        if(mContent.getChildCount() >= mMaxCount){
        	Log.d("liuyg123","acell == null || mContent.getChildCount() >= mMaxCount acceptDrop false ");
//        	if (mShowToast == null) {
//        		mShowToast = new ShowToast(R.string.cannot_be_placed_on_full_hotseat);
//        	} else {
//        		mShowToast.setMessageId(R.string.cannot_be_placed_on_full_hotseat);
//        	}
//        	mXContext.post(mShowToast);
        	return false;
        }




       
        
//        if (!retVal) {
//        	if (mShowToast == null) {
//                mShowToast = new ShowToast(R.string.cannot_be_placed_on_hotseat);
//            } else {
//                mShowToast.setMessageId(R.string.cannot_be_placed_on_hotseat);
//            }
//            mXContext.post(mShowToast);
//        }
      
        R5.echo("acceptDrop retVal =" + retVal);
        return retVal;
    }

    @Override
    public XDropTarget getDropTargetDelegate(XDragObject dragObject) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void getHitRect(Rect outRect) {
        // TODO Auto-generated method stub
        outRect.set(0, 0, (int) getWidth(), (int) getHeight());
    }

    @Override
    public int getLeft() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void getLocationInDragLayer(int[] loc) {
        // TODO Auto-generated method stub
        ((XLauncher) mXContext.getContext()).getDragLayer().getLocationInDragLayer(this, loc);
    }

    @Override
    public int getTop() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean isDropEnabled() {
        return isVisible() && getAlpha() > .5f;//bug599
    }

    @Override
    public void onDragEnter(XDragObject dragObject) {
    	 isOnDropComplete = false;
    	 isAccept = true;
    	 lastTargetX = -1;
    	 if(dragObject.dragSource!=this){
    		 BEGIN_REORDER_DURATION = 0;
    	 }else{
    		 BEGIN_REORDER_DURATION = 160;
    	 }
        ((XLauncher) getXContext().getContext()).getWorkspace().closeFolderDelayed();
        /*** fixbug 12707 . AUT: zhaoxy . DATE: 2013-05-21. START***/
        closeFolderDelayed();
        /*** fixbug 12707 . AUT: zhaoxy . DATE: 2013-05-21. END***/

        if(dragObject.dragSource!=this){
            if(acceptDropWhenDragEnter(dragObject)){
            }else{
            	isAccept = false;
            	return;
            }
        	Log.d("liuyg123","onDragEnter 从workspace to hotseat");
            isOnDrop = false;
  
            if(mContent.getChildCount() < mMaxCount){
            	if(mIsAllowFolder){
            		int pixelX =dragObject.x;
            		int pixelY=dragObject.y;
            		mTargetCell = mContent.findTargetCell(tempchildcount,pixelX,pixelY);
            		mEmptyCell = mTargetCell;

            		int cellWidth = mXContext.getResources().getDimensionPixelSize(R.dimen.hotseat_cell_width);
            		Log.d("liuyg123","onDragEnter cellWidth = "+ cellWidth);
            		Log.d("liuyg123","onDragEnter mTargetCell[0] = "+ mTargetCell[0]);
            		final ItemInfo info = (ItemInfo) dragObject.dragInfo;
            		if(checkFolderFeedback(dragObject.x, info, mTargetCell)){
            			isAccept = false;
            			lastTargetX = mTargetCell[0];
            			return;
            		}
            	}
            	//	Log.i("00", "==ondragenter=from workspace=target="+mTargetCell[0]+"=empty="+mEmptyCell[0]);
            	tempchildcount++;
            	if(tempchildcount > mContent.getChildCount()+1){
            		tempchildcount = mContent.getChildCount()+1;
            	}
            	if(tempchildcount>mMaxCount){
            		tempchildcount = mMaxCount;
            	}
            	if(mDragInfo !=null) mDragInfo.cellX = -1;
            	/*if (mTargetCell[0] != mEmptyCell[0]
        					|| mTargetCell[1] != mEmptyCell[1]) {
            	 */
            	// for bug 14814 
            	mBeginMoveTime = 0; 
            	//for bug 14814
            	mReorderAlarm.cancelAlarm();
            	mReorderAlarm.setOnAlarmListener(mReorderAlarmListener);
            	mReorderAlarm.setAlarm(BEGIN_REORDER_DURATION);// 150
            	//}
            }
            
        }else{
           // Log.d("00","onDragEnter 从hotseat按起 或 从hotseat按起后出去又进来"+"==hasDragOut="+hasDragOut
           // 		+"tempchildcount"+tempchildcount);

            //记住这个child 做拖出去动画的时候，如果这个child不为空 那么rm掉这个childe
			if (hasDragOut) {
				tempchildcount++;

				if (tempchildcount > mContent.getChildCount() + 1) {
					tempchildcount = mContent.getChildCount() + 1;
				}
				if (tempchildcount > mMaxCount) {
					tempchildcount = mMaxCount;
				}
				int pixelX = dragObject.x;
				int pixelY = dragObject.y;
				mTargetCell = mContent.findTargetCell(tempchildcount, pixelX,
						pixelY);
				mEmptyCell[0] = -1;
    			//Log.i("00", "==ondragenter=from hotseat==out and in==target="+mTargetCell[0]+"=empty="+mEmptyCell[0]);

				/*
				 * if (mTargetCell[0] != mEmptyCell[0] || mTargetCell[1] !=
				 * mEmptyCell[1]) {
				 */
				// for bug 14814
				/*mBeginMoveTime = 0;
				// for bug 14814
				mReorderAlarm.cancelAlarm();
				mReorderAlarm.setOnAlarmListener(mReorderAlarmListener);
				mReorderAlarm.setAlarm(BEGIN_REORDER_DURATION);// 150
*/			}
		//	Log.d("00","end===onDragEnter 从hotseat按起 或 从hotseat按起后出去又进来"+"==hasDragOut="+hasDragOut
          //  		+"tempchildcount"+tempchildcount);
		}
    }
    private boolean acceptDropWhenDragEnter(XDragObject dragObject) {
    	if (dragObject.dragInfo instanceof SimpleItemInfo
    			|| dragObject.dragInfo instanceof XScreenMngView.PreviewInfo) {
    		//   		Log.d("liuyg123","onDragEnter not accept");
    		if (mShowToast == null) {
    			mShowToast = new ShowToast(R.string.cannot_be_placed_on_hotseat);
    		} else {
    			mShowToast.setMessageId(R.string.cannot_be_placed_on_hotseat);
    		}
    		mXContext.post(mShowToast);
    		return false;
    	}

    	if (!(dragObject.dragInfo instanceof ItemInfo)) {
    		if (mShowToast == null) {
    			mShowToast = new ShowToast(R.string.cannot_be_placed_on_hotseat);
    		} else {
    			mShowToast.setMessageId(R.string.cannot_be_placed_on_hotseat);
    		}
    		mXContext.post(mShowToast);
    		return false;
    	}    	

    	final int[] touchXY = new int[] { dragObject.x, dragObject.y };
    	Log.d("liuyg123","onDragEnter   x="+dragObject.x+" y========"+dragObject.y+"mMaxCount ="+mMaxCount);
    	mTargetCell = mContent.findTargetCell(mContent.getChildCount(),touchXY[0], touchXY[1]);



    	DrawableItem acell = (DrawableItem)mContent.getChildAt(mTargetCell[0], mTargetCell[1]);
    	boolean retVal = true;
    	if ( acell != null && acell.getTag() instanceof LeosItemInfo && ((LeosItemInfo)acell.getTag()).screen == -1 )
    	{
    		//Log.d("liuyg123","onDragEnter acell != null && acell.getTag() instanceof LeosItemInfo && ((LeosItemInfo)acell.getTag()).screen == -1 acceptDrop false");
    		retVal = false;
    	}

    	else
    	{
    		ItemInfo info = (ItemInfo) dragObject.dragInfo;
    		switch (info.itemType) {
    		case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
    		case LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT:
    			// 	Log.d("liuyg123","onDragEnter ITEM_TYPE_APPLICATION");
    			break;
    		case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
    			retVal = false;
    			break;    
    		case LauncherSettings.Favorites.ITEM_TYPE_FOLDER:
    			// 	Log.d("liuyg123","onDragEnter ITEM_TYPE_FOLDER");
    			retVal = false;
    			if(mIsAllowFolder){
    				if (mContent.getChildCount() < mMaxCount)
    				{
    					Log.d("liuyg123","acceptDrop folder yes");
    					retVal = true;
    				}else{
    					if (mShowToast == null) {
    						mShowToast = new ShowToast(R.string.cannot_be_placed_on_full_hotseat);
    					} else {
    						mShowToast.setMessageId(R.string.cannot_be_placed_on_full_hotseat);
    					}
    					mXContext.post(mShowToast);
    					return false;
    				}
    			}
    			break;
    		case LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET:
    		case LauncherSettings.Favorites.ITEM_TYPE_LEOSWIDGET:
    		case LauncherSettings.Favorites.ITEM_TYPE_COMMEND_APPWIDGET:
    			retVal = false;
    			break;
    		default:
    			throw new IllegalStateException("Unknown item type: " + info.itemType);
    		}

    	}
		if (!retVal) {
			if (mShowToast == null) {
				mShowToast = new ShowToast(R.string.cannot_be_placed_on_hotseat);
				Log.d("liuyg123","!checkFolderFeedback(pixelX, info, mTargetCell) mShowToast == null cannot_be_placed_on_hotseat");
			}
			else {
				mShowToast.setMessageId(R.string.cannot_be_placed_on_hotseat);
			}
			mXContext.post(mShowToast);
			return false;
		}
    	int pixelX =dragObject.x;
    	final ItemInfo info = (ItemInfo) dragObject.dragInfo;
    	if(!checkFolderFeedback(pixelX, info, mTargetCell)){
    		if(mContent.getChildCount() >= mMaxCount){
    			if (mShowToast == null) {
    				mShowToast = new ShowToast(R.string.cannot_be_placed_on_full_hotseat);
    			} else {
    				mShowToast.setMessageId(R.string.cannot_be_placed_on_full_hotseat);
    			}
    			mXContext.post(mShowToast);
    			return false; 
    		}	 
    	}else{
    		isAccept = false;
    		return true;
    	}

    	isAccept = true;
    	return true;
    }
	/* public boolean findAndSetEmptyCells(ItemInfo item) {
        int[] emptyCell = new int[2];
            if (mContent.findCellForSpan(emptyCell, item.spanX, item.spanY, item.screen, null)) {
                item.cellX = emptyCell[0];
                item.cellY = emptyCell[1];
                mContent.addItem(null, item.cellX);
                return true;
        }else{
        	return false;
        }
    }*/
    private boolean hasDragOut = false;
    private boolean inandout = false;
    @Override
    public void onDragExit(XDragObject dragObject) {
    	 Log.d("liuyg123","onDragExit begin");
     	if(!isAccept){
    		Log.d("liuyg123","onDragExit return"+isAccept);
    		tempchildcount = mContent.getChildCount();
    		cleanupAddToFolder();
    		return;
    	}
		if(mIsFolderAccept){
			cleanupAddToFolder();
			return;
		}
		else{
			cleanupAddToFolder();
		}
        if(dragObject.dragComplete){ 
            if(dragObject.dragSource == this){//在里面来回拖
            //    Log.d("00","onDragExit in hot seat="+(dragObject.dragSource == this));
                
            }else{//拖进来
              //  Log.d("00","onDragExit from workspace="+(dragObject.dragSource == this));
                tempchildcount++;
            }
        }else /*if(dragObject.dragSource == this)*/{ //拖出去
        	hasDragOut = true;
          //  Log.d("00","onDragExit from hotseat to workspace");
            tempchildcount--;
            if (dragObject.dragSource == this) {//拖出去再拖进来
            	inandout = false;
    			/*if (mTargetCell[0] != mEmptyCell[0]
    					|| mTargetCell[1] != mEmptyCell[1]) {
    				*/
    	//		Log.i("00", "==ondragexit===from hotseat==target="+mTargetCell[0]+"=empty="+mEmptyCell[0]);
    	//		Log.i("00", "==ondragexit==from hotseat==target==tempchildcount="+tempchildcount+"=childrencount="+mContent.getChildCount());
    			//mTargetCell[0] = mMaxCount;
    				// for bug 14814 
    				 mBeginMoveTime = 0; 
    				 //for bug 14814
    				mReorderAlarm.cancelAlarm();
    				mReorderAlarm.setOnAlarmListener(mReorderAlarmListener);
    				mReorderAlarm.setAlarm(BEGIN_REORDER_DURATION);// 150
    			//}
    		}else{//拖进来再拖出去
                if(dragObject.dragSource != this && mContent.getChildCount() == mMaxCount){
                	tempchildcount++;
                	return;
                }
    			inandout = true;

				if (mReorderAlarm.alarmPending()) {
					 mBeginMoveTime = 0; 
					mReorderAlarm.cancelAlarm();
					mReorderAlarm.setOnAlarmListener(mReorderAlarmListener);
					mReorderAlarm.setAlarm(BEGIN_REORDER_DURATION
							+ REORDER_ANIMATION_DURATION);// 150
				} else {
					 mBeginMoveTime = 0; 
					mReorderAlarm.cancelAlarm();
					mReorderAlarm.setOnAlarmListener(mReorderAlarmListener);
					mReorderAlarm.setAlarm(BEGIN_REORDER_DURATION);
				}
    		}
        }

        Log.d("liuyg123","onDragExit end");
    }
    
/*RK_ID:RK_SINGLE_LAYER zhanglz 2013-6-26. S*/
    private Alarm mReorderAlarm = new Alarm();
    private static final int REORDER_ANIMATION_DURATION = 150;
    private static int BEGIN_REORDER_DURATION = 160;//    		50;
    private int[] mEmptyCell = new int[2];
    private long mBeginMoveTime = 0;
    private long mMoveDuration = 0;
    OnAlarmListener mReorderAlarmListener = new OnAlarmListener() {
        public void onAlarm(Alarm alarm) {
        	//调换各个屏幕单元格的位置
            if(!isOnDrop && !isOnDropComplete)
            realTimeReorder(mTargetCell);
        }
    };
	private void realTimeReorder(int[] target) {
//        float delayAmount = 30;
//        int delay = 50;
		int delay = 0;
		for (int i = 0; i < mContent.getFolderOuterRings().size(); i++) {
			FolderRingAnimator fra = mContent.getFolderOuterRings().get(i);
			fra.closeAnimation();
		}

		if (mXLauncher.isFolderAnimating()) {
			return;
		}
		int childcount = mContent.getChildCount();
	//	Log.i("00", "realTimeReorder==tempchildcount="+tempchildcount+"==childcount=="+childcount);

		if (tempchildcount > childcount + 1) {
			tempchildcount = childcount + 1;
		}else if(tempchildcount < childcount - 1){
			tempchildcount = childcount - 1;
		}
		if(tempchildcount > mMaxCount){
            return;
		}
//		if(mFolderRingAnimator!=null){
//			mFolderRingAnimator.toNaturalState();
//		}
//		logAllItems();
		 //for bug 14814
		mBeginMoveTime = System.currentTimeMillis();
		// for bug 14814
		
		if (tempchildcount > childcount || mEmptyCell[0]==-1) {// 拖进来
		//	Log.i("00", "=拖进来==");
			//界面上有三个 但temp和childcoun都是4
			ArrayList<DrawableItem> tempchildren = getTempchildrenByLocation(tempchildcount-1);
//			logAllItems(tempchildren);
			for (int i = 0; i < tempchildren.size(); i++) {
				if (i < target[0]) {
					mContent.animateChildToPosition(tempchildren.get(i),
							mContent.p[tempchildcount][i],
							REORDER_ANIMATION_DURATION, delay);
				} else {
					if(i+1<tempchildcount && i<tempchildren.size())
					mContent.animateChildToPosition(tempchildren.get(i),
							mContent.p[tempchildcount][i + 1],
							REORDER_ANIMATION_DURATION, delay);
				}
				mEmptyCell[0] = target[0];
				mEmptyCell[1] = target[1];
			//	delay += delayAmount;
			}
			if (mDragInfo != null
					&& mContent.getChildAt(mDragInfo.cellX) != null
					&& mContent.p[tempchildcount][target[0]] != null){
				mContent.getChildAt(mDragInfo.cellX).setRelativeX(
						mContent.p[tempchildcount][target[0]].x);
				if(mContent.getChildAt(mDragInfo.cellX).getTag() instanceof ItemInfo){
					ItemInfo tmpinfo = (ItemInfo)mContent.getChildAt(mDragInfo.cellX).getTag();
					tmpinfo.container = LauncherSettings.Favorites.CONTAINER_HOTSEAT;
				}
			}
		} else if (tempchildcount < childcount) {//拖出去
		//	Log.i("00", "拖出去=");

			ArrayList<DrawableItem> tempchildren = getTempchildrenByLocation(tempchildcount+1);
			logAllItems(tempchildren);

			for (int i = 0; i < tempchildren.size(); i++) {
				mContent.animateChildToPosition(tempchildren.get(i),
						mContent.p[tempchildcount][i],
						REORDER_ANIMATION_DURATION, delay);
			//	delay += delayAmount;
			}
			if (mDragInfo != null
					&& mContent.p[tempchildcount][target[0]] != null) {
				mContent.getChildAt(mDragInfo.cellX).setRelativeX(0);
				if(mContent.getChildAt(mDragInfo.cellX).getTag() instanceof ItemInfo){
					ItemInfo tmpinfo = (ItemInfo)mContent.getChildAt(mDragInfo.cellX).getTag();
					tmpinfo.container = LauncherSettings.Favorites.CONTAINER_HOTSEAT;
				}
			}
		}else if(tempchildcount == mContent.getChildCount()){//在里面拖来脱去
			if(hasDragOut&& target[0]==mEmptyCell[0]){
				
				if(inandout){
				   // Log.i("00", "===in and out==");
					//拖进来又拖出来
				    //现在mcontent.getchildcout 为3 tempchildcount也为3 界面显示为3个
					for (int i = 0; i < childcount; i++) {
						if (i < mEmptyCell[0]) { // 拖点前面的cell
							if (mContent.animateChildToPosition(
									mContent.getChildAt(i),
									mContent.p[tempchildcount][i],
									REORDER_ANIMATION_DURATION, delay)) {
							}
						} else if (i >= mEmptyCell[0]) {// 拖点后面的cell
							if (mEmptyCell[0] == 0 && i == 0) {
								if (mContent.animateChildToPosition(
										mContent.getChildAt(i),
										mContent.p[tempchildcount][i],
										REORDER_ANIMATION_DURATION, delay)) {
								}
							} else {
								if (mContent.animateChildToPosition(
										mContent.getChildAt(i),
										mContent.p[tempchildcount][i],
										REORDER_ANIMATION_DURATION, delay)) {

								}

							}
						}
						// delay += delayAmount;
					}
					if (mDragInfo != null && mContent.getChildAt(mDragInfo.cellX)!=null) {
						mContent.getChildAt(mDragInfo.cellX).setRelativeX(0);
						if(mContent.getChildAt(mDragInfo.cellX).getTag() instanceof ItemInfo){
							ItemInfo tmpinfo = (ItemInfo)mContent.getChildAt(mDragInfo.cellX).getTag();
							tmpinfo.container = LauncherSettings.Favorites.CONTAINER_HOTSEAT;
						}
					}
			}else if(target[0]==mEmptyCell[0]){//拖出去又拖进来
				    //现在mcontent.getchildcout 为4 tempchildcount也为4 但界面显示为3个
					//需要临时的把child搞一下 
					// 最后location对就行
			//	    Log.i("00", "===out and in==");
					ArrayList<DrawableItem> tempchildren = getTempchildrenByLocation(tempchildcount -1);
					logAllItems(tempchildren);
					for (int i = 0; i < tempchildren.size(); i++) {
						if (i < target[0]) {
								if (mContent.animateChildToPosition(
										tempchildren.get(i),
										mContent.p[tempchildcount][i],
										REORDER_ANIMATION_DURATION, delay)) {

								}
                         
						} else {
							
								if (mContent.animateChildToPosition(tempchildren.get(i),
										mContent.p[tempchildcount][i + 1],
										REORDER_ANIMATION_DURATION, delay)) {
								}
								
						}
						mEmptyCell[0] = target[0];
						mEmptyCell[1] = target[1];
						//	delay += delayAmount;
					}
					if (mDragInfo != null
							&& mContent.getChildAt(mDragInfo.cellX) != null
							&& mContent.p[tempchildcount][target[0]] != null) {
						mContent.getChildAt(mDragInfo.cellX).setRelativeX(
								mContent.p[tempchildcount][target[0]].x);
						if(mContent.getChildAt(mDragInfo.cellX).getTag() instanceof ItemInfo){
							ItemInfo tmpinfo = (ItemInfo)mContent.getChildAt(mDragInfo.cellX).getTag();
							tmpinfo.container = LauncherSettings.Favorites.CONTAINER_HOTSEAT;
						}
					}
				}
				
			} 
		else {//往前后拖
		//		Log.i("00", "=往前后拖==");
				ArrayList<DrawableItem> tempchildren = getTempchildrenByLocation(tempchildcount);
				for (int i = 0; i < tempchildren.size(); i++) {
					if (i < target[0]) {
						    mContent.animateChildToPosition(tempchildren.get(i),
								mContent.p[tempchildcount][i],
								REORDER_ANIMATION_DURATION, delay);

					} else {
						    mContent.animateChildToPosition(tempchildren.get(i),
								mContent.p[tempchildcount][i+1],
								REORDER_ANIMATION_DURATION, delay);
					}
					mEmptyCell[0] = target[0];
					mEmptyCell[1] = target[1];
					//delay += delayAmount;
				}
				if (mDragInfo != null&&mContent.getChildAt(mDragInfo.cellX)!=null&&mContent.p[tempchildcount][target[0]]!=null) {
					mContent.getChildAt(mDragInfo.cellX).setRelativeX(
							mContent.p[tempchildcount][target[0]].x);
					if(mContent.getChildAt(mDragInfo.cellX).getTag() instanceof ItemInfo){
						ItemInfo tmpinfo = (ItemInfo)mContent.getChildAt(mDragInfo.cellX).getTag();
						tmpinfo.container = LauncherSettings.Favorites.CONTAINER_HOTSEAT;
					}
				}
			}
		}
		mMoveDuration = REORDER_ANIMATION_DURATION + delay*(mContent.getChildCount()+1)
				- (System.currentTimeMillis() - mBeginMoveTime);
		Log.d("liuyg123","realTimeReorder end");
	}
	
	private ArrayList<DrawableItem> getTempchildrenByLocation(int tempchildrencount) {
		// TODO Auto-generated method stub
		ArrayList<DrawableItem> tempItems = new ArrayList<DrawableItem>();
		boolean[] hasSet = new boolean[mContent.getChildCount()];
		for (int i = 0; i < mContent.getChildCount(); i++) {
			DrawableItem drawbleitem = mContent.getChildAt(i);
			tempItems.add(drawbleitem);
			hasSet[i] = false;
		}
		if(mDragInfo !=null&&mDragInfo.cellX<tempItems.size()&&mDragInfo.cellX>-1){
		//	Log.i("00", "====mDragInfo==="+mDragInfo.cellX);
			tempItems.remove(mDragInfo.cellX);
		}
//		logAllItems(tempItems);
		/*int oldsize = tempItems.size();
		int toremove = -1;
		for (int j = 0; j < mContent.getChildCount(); j++) {
			if (mDragInfo != null && mDragInfo.cellX == j) {
				Log.i("00", "set==mDragInfo.cellX=" + j);
				continue;
			}

			DrawableItem childtemp = (DrawableItem) mContent.getChildAt(j);
            int childX = (int) childtemp.getRelativeX();
			for (int i = 0; i <tempchildrencount; i++) {
				// 根据x得到relativex,根据relativeX得到该child;
				// DrawableItem child = (DrawableItem) mContent.getChildAt(i);
				int toX = mContent.p[tempchildrencount][i].x;
				Log.i("00", "set==i="+i+"===tox = "+toX+"==j="+j+"===childX==="+childX);

				if (childX == toX) {
					// j1 and j2 has the same relativeX
						
						Log.i("00", "set==i="+i+"==j="+j+"===tox = "+toX);

						if(mDragInfo!=null && i>=mDragInfo.cellX && i>0 && mDragInfo.cellX>-1&&i>=tempItems.size()){
							Log.i("00", "set==mDragInfo!=null="+"==mDragInfo.cellX="+mDragInfo.cellX);
							tempItems.set(i-1, childtemp);
						}else {
							tempItems.set(i, childtemp);
						}
						temptoreal.put(i, j);
						hasSet[i] = true;
				}
			}
		}*/
		/*if(toremove!=-1){
			tempItems.remove(toremove);
		//	temptoreal.remove(toremove);
		}*/
		logAllItems(tempItems);
		return tempItems;
	}
    /**
     * 判断单元格到底是往前调还是后调
     * @param v1  目标
     * @param v2  源
     * @return
     */
    boolean readingOrderGreaterThan(int[] v1, int[] v2) {
    	if ( v1[1] > v2[1]
    		    || v1[1] == v2[1] && v1[0] > v2[0]) {
    		return true;
    	} else {
    		return false;
    	}
    }
/*RK_ID:RK_SINGLE_LAYER zhanglz 2013-6-26. E*/
    public int tempchildcount = 4;
    public int lastTargetX = -1;
    @Override
    public void onDragOver(XDragObject dragObject) {
        final ItemInfo info = (ItemInfo) dragObject.dragInfo;
        int pixelX =dragObject.x;
		int pixelY=dragObject.y;
		if(tempchildcount>mMaxCount){
			Log.d("liuyg123","onDragOver tempchildcount>mMaxCount) tempchildcount="+tempchildcount);
			return;
		}
        mTargetCell = mContent.findTargetCell(tempchildcount,pixelX,pixelY);
        if(!isAccept){
        	Log.d("liuyg123","onDragOver isAccept="+isAccept);
        	if(checkFolderFeedback(dragObject.x, info, mTargetCell)){
        		isAccept = false;
        		Log.d("liuyg123","checkFolderFeedback(dragObject.x, info, mTargetCell) true return");
        		return;
        	}else{
        		cleanupAddToFolder();
        		if(acceptDropWhenDragEnter(dragObject)){
        			Log.d("liuyg123","acceptDropWhenDragEnter(dragObject) true");
        			onDragEnter(dragObject);
        		}

        		if(!isAccept){
        			isAccept = false;
        			Log.d("liuyg123","acceptDropWhenDragEnter(dragObject) false return");
        			return;
        		}
        	}
        }
		if(mContent.getChildCount() == mMaxCount && dragObject.dragSource != this){
			return;
		}
		if(mTargetCell[0]!=lastTargetX){

			lastTargetX = mTargetCell[0];
//			Log.d("liuyg123","onDragOver mTargetCell ="+mTargetCell[0]); 
//			Log.d("liuyg123","onDragOver mEmptyCell ="+mEmptyCell[0]); 
			if (mTargetCell[0] != mEmptyCell[0]
					/*||mTargetCell[0]==((ItemInfo) dragObject.dragInfo).cellX*/) {//for what?
				/*if(hasDragOut && tempchild !=null){
				return;
			}*/
				//			Log.d("liuyg123","mTargetCell[0] != mEmptyCell[0] mTargetCell[0].x"+ mTargetCell[0]);
				// for bug 14814
				mBeginMoveTime = 0;
				// for bug 14814
				mReorderAlarm.cancelAlarm();
				mReorderAlarm.setOnAlarmListener(mReorderAlarmListener);
				mReorderAlarm.setAlarm(BEGIN_REORDER_DURATION);// 150
			}
		}
    }
	private Object mLock = new Object();
	private boolean isOnDrop = false;
	private boolean isAccept = true;
    @Override
    public void onDrop(XDragObject dragObject) {
        isOnDrop = true;
    	if (dragObject.dragInfo instanceof SimpleItemInfo
    			|| dragObject.dragInfo instanceof XScreenMngView.PreviewInfo) {
    		return;
    	}
        if (!(dragObject.dragInfo instanceof ItemInfo)) {
    		return;
    	}
        waitAnimationEnd();
        ((XLauncher) getXContext().getContext()).getWorkspace().cancelcloseFolderDelayed();
        cleanupAddToFolder();
        if (dragObject.dragSource != this) {
            final int[] touchXY = new int[] { dragObject.x, dragObject.y };
            onDropExternal(touchXY, dragObject.dragInfo, dragObject);
        } else if (mDragInfo != null) {
            final int[] touchXY = new int[] { dragObject.x, dragObject.y };
            onDropExternal(touchXY, dragObject.dragInfo, dragObject);
        }
        /*** fixbug 12707 . AUT: zhaoxy . DATE: 2013-05-21. START***/
        if(mToast != null){
            mToast.cancel();
            mToast = null;
        }
        if(mShowToast != null){
            mShowToast = null;
        }
        /*** fixbug 12707 . AUT: zhaoxy . DATE: 2013-05-21. END***/
    //    Log.d("00","onDrop exit");
    }
    private boolean isOnDropComplete = false;
    @Override
    public void onDropCompleted(DrawableItem target, XDragObject d,
            boolean success) {
		isOnDropComplete = true;
		Log.d("liuyg123", "onDropCompleted start" + System.currentTimeMillis());
		waitAnimationEnd();
		// Log.d("00","onDropCompleted start until animation end"+System.currentTimeMillis());

		tempchildcount = mContent.getChildCount();
		// resetItemsByLocation();
		// logAllItems();

		// TODO Auto-generated method stub
		if (success) {
			boolean single = SettingsValue.getSingleLayerValue(getXContext()
					.getContext());
			if (single
					&& target instanceof XDeleteDropTarget
					&& (mDragInfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION || mDragInfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_FOLDER)) {
				// this is uninstall option, do not remove here.

				if ((mDragInfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_FOLDER && ((FolderInfo) mDragInfo).contents
						.size() > 0)
						|| mDragInfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION) {
					if (mDragInfo != null) {
						final DrawableItem drawbleitem = mContent
								.getChildAt(mDragInfo.cellX);
						if (drawbleitem == null) {
							// Log.e("liuyg123","drawbleitem == null");
							return;
						}
						// mContent.getChildAt(mDragInfo.cellX).setVisibility(true);
						mContent.resizeLayout();
						if (d.dragView.hasDrawn()
								&& mDragInfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_FOLDER) {
							animDragviewIntoPosition(d.dragView, mDragInfo);
						}
					}
					//add for quick drag mode by sunzq3, begin;
					mXLauncher.getWorkspace().getPageIndicator().startNormalAnimation();
			        //add for quick drag mode by sunzq3, end;
					return;
				}
			}
        	
			if (mDragInfo != null) {
				// DrawableItem cell =
				// (DrawableItem)mContent.getChildAt(mTempCell[0],
				// mTempCell[1]);
				if (mDragInfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_FOLDER) {
					if (target != this) {// 从hotseat拖拽到workspace
						// 解决 左右拖动后 拖出去 错误的item消失问题

						int rmIndextemp3 = mTempCell[0];
						// Log.d("00","==从hotseat拖拽到workspace=rmIndex="+rmIndex+"==rmIndextemp="+rmIndextemp
						// +"=rmIndextemp2="+rmIndextemp2+"=rmIndextemp3="+rmIndextemp3);
						mContent.removeItemWithoutClean(rmIndextemp3);
						// mContent.removeItem(rmIndextemp3);
						// removeViewWithoutClean(mContent.getChildAt(rmIndextemp3));
						for (int i = 0; i < mContent.getChildCount(); i++) {
							DrawableItem drawbleitem = mContent.getChildAt(i);
							if (drawbleitem == null) {
								continue;
							}

							Object o = drawbleitem.getTag();
							if (o == null) {
								continue;
							}
							ItemInfo tmpinfo = (ItemInfo) drawbleitem.getTag();
							tmpinfo.cellX = i;
							tmpinfo.screen = i;
							drawbleitem.setTag(tmpinfo);
							// XLauncherModel.updateItemInDatabase(mXLauncher,
							// tmpinfo);
						}
						mContent.resizeLayout();
						new Thread(new Runnable() {
							@Override
							public void run() {
								updateChildDatabase();
							}
						}).start();
					}


				} else {
					/* RK_ID:RK_SINGLE_LAYER liuyg1 2013-6-13. S */
					if (target != this) {// 从hotseat拖拽到workspace
						// 解决 左右拖动后 拖出去 错误的item消失问题

						int rmIndextemp3 = mTempCell[0];
						// Log.d("00","==从hotseat拖拽到workspace=rmIndex="+rmIndex+"==rmIndextemp="+rmIndextemp
						// +"=rmIndextemp2="+rmIndextemp2+"=rmIndextemp3="+rmIndextemp3);

						mContent.removeItemWithoutClean(rmIndextemp3);
						Log.e("liuyg123",
								"mContent.removeItemWithoutClean(rmIndextemp3)");
						for (int i = 0; i < mContent.getChildCount(); i++) {
							DrawableItem drawbleitem = mContent.getChildAt(i);
							if (drawbleitem == null) {
								continue;
							}

							Object o = drawbleitem.getTag();
							if (o == null) {
								continue;
							}
							ItemInfo tmpinfo = (ItemInfo) drawbleitem.getTag();
							tmpinfo.cellX = i;
							tmpinfo.screen = i;
							drawbleitem.setTag(tmpinfo);
							// mContent.resizeLayout();
							// XLauncherModel.updateItemInDatabase(mXLauncher,
							// tmpinfo);
						}
						mContent.resizeLayout();
						new Thread(new Runnable() {
							@Override
							public void run() {
								updateChildDatabase();
							}
						}).start();
					}
            	    	

  //              	Log.e("liuyg123","at last mContent.resizeLayout");
//                	mXLauncher.getHotseat().createDockAddIcon(mTempCell[0], mTempCell[1]);
				}

				/* RK_ID:RK_SINGLE_LAYER liuyg1 2013-6-13. E */
			}

			mXLauncher.getDragLayer().showPendulumAnim(d, target);
		} else {
			if (mDragInfo != null) {
//				cleanupReorder(true);
				DrawableItem cell = (DrawableItem) mContent.getChildAt(
						mDragInfo.cellX, mDragInfo.cellY);
				if (cell == null) {
					return;
				}
				cell.setVisibility(true);
				// Log.e("liuyg123"," onDropCompleted cell.setVisibility(true);");
				// mDragInfo = null;
			}
			mContent.resizeLayout();
			mContent.setHasLocationPrepared(false);//for bug 261
		}
		for (int i = 0; i < mContent.getChildCount(); i++) {
			DrawableItem drawbleitem = mContent.getChildAt(i);
			drawbleitem.setVisibility(true);
			// if(mContent.getChildAt(i).getTag() instanceof ItemInfo){
			// ItemInfo tmpinfo = (ItemInfo)mContent.getChildAt(i).getTag();
			// tmpinfo.container = LauncherSettings.Favorites.CONTAINER_HOTSEAT;
			// }
		}
        if (!success && target != null) {
            animDragviewIntoPosition(d.dragView, mDragInfo);
        }
        tempchildcount = mContent.getChildCount();
    	hasDragOut =false;
        inandout= false;
        reCheck();
        mDragInfo = null;
        
      //add for quick drag mode by sunzq3, begin;
        ((XLauncherView)mXContext).getWorkspace().getPageIndicator().startNormalAnimation();
      //add for quick drag mode by sunzq3, end;
        Log.d("00","onDropCompleted exit");
    }

    void animDragviewIntoPosition(final XDragView dragView, ItemInfo dragInfo) {
        mContent.resizeLayout();
        final DrawableItem item = mContent.getChildAt(dragInfo.cellX, dragInfo.cellY);
        item.setVisibility(false);
        mXLauncher.getDragLayer().animDropIntoPosition(dragView, item, 0, XHotseat.this,
                LauncherSettings.Favorites.CONTAINER_HOTSEAT);
    }

    private void reCheck() {
		// TODO Auto-generated method stub
    	boolean toResize = false;
		for(int i = 0;i<mContent.getChildCount();i++){
			for(int j = i+1;j<mContent.getChildCount();j++){
				if(mContent.getChildAt(i).getRelativeX() == mContent.getChildAt(j).getRelativeX()){
					mContent.removeItem(j);
					toResize = true;
				}
			}
		}
    	if(toResize)
		mContent.resizeLayout();
	}

	private void removeViewWithoutClean(DrawableItem cell) {
    	//Log.d("liuyg123","removeViewWithoutClean begin");
        int index = mContent.removeItemWithoutClean(cell);
//        if (index >= 0) {
//            XIconDrawable icon = new XIconDrawable(getXContext(), null);
//            mContent.addItem(icon, index);
//        }
//    	int index = mContent.removeItem(cell);
        DrawableItem item = mContent.getChildAt(index);
//      mContent.bindInfo(item, mXLauncher.getIconCache());
        if(item!=null){
        	item.setVisibility(true);
        	//Log.d("liuyg123"," removeViewWithoutClean item.setVisibility(true);"+index);
        }
      if (cell != null && cell.getTag() instanceof ItemInfo){
            mXLauncher.removePopupWindow((ItemInfo) cell.getTag());
        }
      //Log.d("liuyg123","removeViewWithoutClean end");
    }

    @Override
    public void onDragEnd() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onDragStart(XDragSource source, Object info, int dragAction) {
        // TODO Auto-generated method stub
    }
    
    public void startDrag(ItemInfo cellInfo) {
        isOnDrop = false;
    	hasDragOut =false;
    	inandout = false;


        if (cellInfo == null) {
            return;
        }
        DrawableItem cell = (DrawableItem)mContent.getChildAt(cellInfo.cellX, cellInfo.cellY);
        if (cell == null) {
            return;
        }
//        DrawableItem target = cell.getDrawingTarget();
//        if (target == null) {
//            return;
//        }
        mXLauncher.setLauncherWindowStatus(true);
        
        mDragInfo = cellInfo;
        mTempCell[0] = cellInfo.cellX;
        mTempCell[1] = cellInfo.cellY;
        
        if (cell instanceof XShortcutIconView)
        {
//            cell.getBackgroundDrawable().setAlpha(1);
            cell.resetPressedState();
        }
    	mEmptyCell[0] = cellInfo.cellX;
        mEmptyCell[1] = cellInfo.cellY;
        tempchildcount = mContent.getChildCount();
        beginDragShared(cell, this);
        cell.setVisibility(false);
    
    }
    private ItemInfo mDragInfo;
    private XDragController mDragController;
    private final int[] mTmpPoint = new int[2];
    private int[] mTempCell = new int[2];
    XContext mXContext;
    
    public void beginDragShared(DrawableItem child, XDragSource source) {
        ((XLauncher) mXContext.getContext()).getDragLayer().getLocationInDragLayer(child, mTmpPoint);
        final int dragLayerX = mTmpPoint[0];
        final int dragLayerY = mTmpPoint[1];
    	child.setAlpha(1f);
    	Bitmap b = null;
    	if(child instanceof XShortcutIconView){

    	}
        if( child instanceof XViewContainer ){
        	b = ((XViewContainer)child).getSnapshot(1f, true); 
        }else{
        	b = child.getSnapshot( 1f);
        }
        if( b == null ){
        	return;
        }
        //add for quick drag mode by sunzq3, begin;
        ((XLauncherView)mXContext).getWorkspace().getPageIndicator().startEnterAnimation();
        //add for quick drag mode by sunzq3, end;
        mDragController.startDrag(b, dragLayerX, dragLayerY, source, child.getTag(), 0, null, null, false);
    }
    
    public void setup(XDragController mDragController) {
        mDragController.addDropTarget(this);
        this.mDragController = mDragController;
    }
    private void waitAnimationEnd(){
//    	long currentTime = System.currentTimeMillis();
//  		long duration = mBeginMoveTime == 0 ? this.mMoveDuration : (currentTime - mBeginMoveTime);
//		final long delayTime = duration > mMoveDuration ? 0 : (mMoveDuration - duration);		
//		if (delayTime > 0) {
//  			synchronized(mLock) {
//  				try {
//  					mLock.wait(delayTime);
//  				} catch (InterruptedException e) {
//  					// TODO Auto-generated catch block
//  					e.printStackTrace();
//  				}
//  			}
//  		}		
    }
    private int[] mTargetCell = new int[2];
    
	private void onDropExternal(final int[] touchXY, final Object dragInfo,
			XDragObject d) {
		waitAnimationEnd();
		logAllItems();
		if (d.dragSource == this)
			resetItemsByLocation();
		// mContent.refreshChildrenCount();
		// mContent.resizeLayout();

		ItemInfo info = (ItemInfo) dragInfo;
		final long container = LauncherSettings.Favorites.CONTAINER_HOTSEAT;
		int screen = 0;
		switch (info.itemType) {
		case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
		case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
		case LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT:
			// if (info instanceof ApplicationInfo) {
			//
			// // Came from all apps -- make a copy
			// info = new ShortcutInfo((ApplicationInfo) info);
			// mTargetCell = mContent.findTargetArea(touchXY[0], touchXY[1]);
			// // if (createUserFolderIfNecessary(container, mTargetCell, true,
			// null, info)) {
			// // return;
			// // }
			//
			// if (addToExistingFolderIfNecessary(mTargetCell, false, info)) {
			// return;
			// }
			//
			// info.cellX = mTargetCell[0];
			// info.cellY = mTargetCell[1];
			// R5.echo("cellX = " + info.cellX + "cellY =" + info.cellY);
			// screen = info.cellX;
			//
			// info.spanX = 1;
			// info.spanY = 1;
			// IconCache iconCache = ((LauncherApplication)
			// mXLauncher.getApplicationContext())
			// .getIconCache();
			// mContent.bindInfo(info, iconCache);
			// XLauncherModel.addOrMoveItemInDatabase(this.getXContext().getContext(),
			// info, container, screen, info.cellX, info.cellY);
			// }
			// else
			if (info instanceof ShortcutInfo || info instanceof ApplicationInfo) {
				// info = new ShortcutInfo((ShortcutInfo) info);
				if (d.dragSource == this) {// hotseat里面互相拖
					boolean isAddtoFolder = false;
					mTargetCell = mContent.findTargetCell(
							mContent.getChildCount(), touchXY[0], touchXY[1]);
					if (addToExistingFolderIfNecessary(touchXY[0], mTargetCell,
							false, info, d)) {
						isAddtoFolder = true;
						Log.d("liuyg123", "addToExistingFolderIfNecessary");
					}
					// Log.e("liuyg123","d.dragSource==this");
					// info =
					// (ItemInfo)mContent.getChildAt(mTempCell[0]).getTag();
					for (int i = 0; i < mContent.getChildCount(); i++) {
						DrawableItem drawbleitem = mContent.getChildAt(i);
						if (drawbleitem == null) {
							// Log.e("liuyg123","drawbleitem == null");
							continue;
						}

						Object o = drawbleitem.getTag();
						if (o == null) {
							// Log.e("liuyg123","o == null || !(o instanceof FolderInfo)) && i != XHotseat.sAllAppsButtonRank");
							continue;
						}

						ItemInfo tmpinfo = (ItemInfo) drawbleitem.getTag();
						if (isAddtoFolder && info.equals(tmpinfo)) {
							mContent.removeItem(i);
							continue;
						}
						tmpinfo.cellX = i;
						tmpinfo.screen = i;
						tmpinfo.container = LauncherSettings.Favorites.CONTAINER_HOTSEAT;
						drawbleitem.setTag(tmpinfo);
						// drawbleitem.getMatrix().reset();
						// drawbleitem.setAlpha(1f);
						// drawbleitem.reuse();
						drawbleitem.setRelativeX(mContent.p[mContent
								.getChildCount()][i].x);
						mContent.getChildAt(i).setVisibility(true);
						// Log.d("liuyg123","XLauncherModel.updateItemInDatabase(mXLauncher, tmpinfo)"+tmpinfo.cellX+tmpinfo.toString());
						// Log.d("liuyg123","XLauncherModel~~~"+drawbleitem.getRelativeX());
						// XLauncherModel.updateItemInDatabase(mXLauncher,
						// tmpinfo);
					}
				 mContent.resizeLayout();
					new Thread(new Runnable() {
						@Override
						public void run() {
							updateChildDatabase();
						}
					}).start();
				} else {// 从workspace拖拽到hotseat
					/*
					 * mContent.refreshChildrenCount(); mContent.resizeLayout();
					 */
					// Log.i("liuyg123", "===从workspace拖拽到hotseat===");
					mTargetCell = mContent.findTargetCell(
							mContent.getChildCount(), touchXY[0], touchXY[1]);

					Log.d("liuyg123", "ondrop external mTargetCell ="
							+ mTargetCell[0] + "=mContent.getChildCount()="
							+ mContent.getChildCount() + "==tempchountt=="
							+ tempchildcount);
					// if (createUserFolderIfNecessary(container, mTargetCell,
					// true, null, info)) {
					// return;
					// }
					if (addToExistingFolderIfNecessary(touchXY[0], mTargetCell,
							false, info, d)) {
						Log.d("liuyg123", "addToExistingFolderIfNecessary");
						mContent.resizeLayout();
						break;
					}
					info.cellX = mContent.findTargetIndex(touchXY[0],
							mTargetCell[0]);
					Log.d("liuyg123", "ondrop external info.cellX  ="
							+ info.cellX);

					info.cellY = mTargetCell[1];
					screen = info.cellX;
					info.spanX = 1;
					info.spanY = 1;
					IconCache iconCache = ((LauncherApplication) mXLauncher
							.getApplicationContext()).getIconCache();
					for (int i = 0; i < mContent.getChildCount(); i++) {
						DrawableItem drawbleitem = mContent.getChildAt(i);
						if (drawbleitem == null) {
							// Log.e("liuyg123","drawbleitem == null");
							continue;
						}

						Object o = drawbleitem.getTag();
						if (o == null) {
							// Log.e("liuyg123","o == null || !(o instanceof ShortcutInfo)) && i != XHotseat.sAllAppsButtonRank");
							continue;
						}

						ItemInfo tmpinfo = (ItemInfo) drawbleitem.getTag();
						if (i >= info.cellX) {
							tmpinfo.cellX = i + 1;
							tmpinfo.screen = i + 1;
						} else {
							tmpinfo.cellX = i;
							tmpinfo.screen = i;
						}
						tmpinfo.container = LauncherSettings.Favorites.CONTAINER_HOTSEAT;
						drawbleitem.setTag(tmpinfo);
						drawbleitem.setVisibility(true);

						// Log.d("liuyg123","XLauncherModel.updateItemInDatabase(mXLauncher, tmpinfo)"+tmpinfo.cellX);
						// XLauncherModel.updateItemInDatabase(mXLauncher,
						// tmpinfo);
					}
					int childcount = mContent.getChildCount() + 1;
					if (mContent.getChildCount() >= mMaxCount) {
						break;
					}
					RectF rect = mContent.getViewRect(childcount, info.cellX);
					// Log.d("liuyg123","hotseat new rect rect.width="+rect.width()+"rect.height="+rect.height());
					ShortcutInfo newinfo = (ShortcutInfo) info;
					newinfo.container = LauncherSettings.Favorites.CONTAINER_DESKTOP;
					// DrawableItem item = new XPressIconDrawable(getXContext(),
					// null);
					info.container = LauncherSettings.Favorites.CONTAINER_HOTSEAT;
					DrawableItem item = new XShortcutIconView(newinfo, rect,
							mXContext);
					item.setTag(info);
					mContent.addItem(item, info.cellX);
					mContent.bindInfo(info, iconCache);
					mContent.resizeLayout();
					new Thread(new Runnable() {
						@Override
						public void run() {
							updateChildDatabase();
						}
					}).start();
					XLauncherModel.addOrMoveItemInDatabase(getXContext()
							.getContext(), info, container, screen, info.cellX,
							info.cellY);
				}

				/* RK_ID:RK_SINGLE_LAYER liuyg1 2013-6-13. E */

			}
			break;
		case LauncherSettings.Favorites.ITEM_TYPE_FOLDER:
			if (info instanceof FolderInfo) {
				FolderInfo fInfo = (FolderInfo) info;
				Log.d("liuyg123", "foldertype mTargetCell[0]=="
						+ mTargetCell[0] + "mTargetCell[1]" + mTargetCell[1]);
				if (d.dragSource == this) {
					// info =
					// (ItemInfo)mContent.getChildAt(mTempCell[0]).getTag();

					for (int i = 0; i < mContent.getChildCount(); i++) {
						DrawableItem drawbleitem = mContent.getChildAt(i);
						if (drawbleitem == null) {
							Log.e("liuyg123", "drawbleitem == null");
							continue;
						}

						Object o = drawbleitem.getTag();
						if (o == null) {
							Log.e("liuyg123",
									"o == null || !(o instanceof FolderInfo)) && i != XHotseat.sAllAppsButtonRank");
							continue;
						}

						ItemInfo tmpinfo = (ItemInfo) drawbleitem.getTag();
						tmpinfo.cellX = i;
						tmpinfo.screen = i;
						tmpinfo.container = LauncherSettings.Favorites.CONTAINER_HOTSEAT;
						drawbleitem.setTag(tmpinfo);
						Log.d("liuyg123",
								"XLauncherModel.updateItemInDatabase(mXLauncher, tmpinfo)"
										+ tmpinfo.cellX);
						//XLauncherModel.updateItemInDatabase(mXLauncher, tmpinfo);
					}

				} else {
					Log.d("liuyg123", "foldertype mTargetCell[0]=="
							+ mTargetCell[0] + "mTargetCell[1]"
							+ mTargetCell[1]);
					fInfo.container = container;
					fInfo.cellX = mContent.findTargetIndex(touchXY[0],
							mTargetCell[0]);
					fInfo.cellY = mTargetCell[1];
					screen = fInfo.cellX;
					fInfo.spanX = 1;
					fInfo.spanY = 1;
					fInfo.screen = screen;
					for (int i = 0; i < mContent.getChildCount(); i++) {
						DrawableItem drawbleitem = mContent.getChildAt(i);
						if (drawbleitem == null) {
							// Log.e("liuyg123","drawbleitem == null");
							continue;
						}

						Object o = drawbleitem.getTag();
						if (o == null) {
							// Log.e("liuyg123","o == null");
							continue;
						}

						ItemInfo tmpinfo = (ItemInfo) drawbleitem.getTag();
						if (i >= info.cellX) {
							tmpinfo.cellX = i + 1;
							tmpinfo.screen = i + 1;
						} else {
							tmpinfo.cellX = i;
							tmpinfo.screen = i;
						}
						drawbleitem.setTag(tmpinfo);
						// Log.d("liuyg123","XLauncherModel.updateItemInDatabase(mXLauncher, tmpinfo)"+tmpinfo.cellX);
						// XLauncherModel.updateItemInDatabase(mXLauncher,
						// tmpinfo);
					}
				}
				/* RK_ID:RK_SINGLE_LAYER liuyg1 2013-6-13. E */
				DrawableItem view = null;
				// if (d.dragSource == this) {
				// view = getDragView();
				// if (view != null) {
				// mContent.switchDragFolder((XFolderIcon)view, mTempCell,
				// fInfo);
				// }
				// } else {
				view = mXLauncher.getWorkspace().getDragView();
				if (view != null) {
					view.setTouchable(true);
					mContent.bindDragFolder((XFolderIcon) view, fInfo);
					// Log.d("liuyg123","onDropExternal bindDragFolder");
				}
				// }

				((XLauncher) mXContext.getContext()).updateFolder(fInfo);

				mContent.resizeLayout();
				XLauncherModel.addOrMoveItemInDatabase(this.getXContext()
						.getContext(), fInfo, fInfo.container, fInfo.screen,
						fInfo.cellX, fInfo.cellY);
				new Thread(new Runnable() {
					@Override
					public void run() {
						updateChildDatabase();
					}
				}).start();
			}
			break;
		default:
			throw new IllegalStateException("Unknown item type: "
					+ info.itemType);
		}
		tempchildcount = mContent.getChildCount();
		hasDragOut = false;
		inandout = false;
		logAllItems();
		Log.e("liuyg123", "onDropExternal exit");
	}

	private void resetItemsByLocation() {
		// TODO Auto-generated method stub
		
		
		 ArrayList<DrawableItem> tempitems = new ArrayList<DrawableItem> ();
		 boolean[] hasDouble = new boolean[mContent.getChildCount()];
		 boolean[] toSet = new boolean[mContent.getChildCount()];
		 boolean isAnimaitionError = false;
			for (int i = 0; i < mContent.getChildCount(); i++) {
				DrawableItem drawbleitem = mContent.getChildAt(i);
				tempitems.add(drawbleitem);
				hasDouble[i] = false;
				toSet[i] = false;

			}
			for (int i = 0; i < mContent.getChildCount(); i++) {
				DrawableItem drawbleitemX = mContent.getChildAt(i);
				if (drawbleitemX == null) {
					continue;
				}
				int oldtoX = (int) drawbleitemX.getRelativeX();
				for (int j = i+1; j < mContent.getChildCount(); j++) {
					DrawableItem drawbleitemJ = mContent.getChildAt(j);
					if (drawbleitemJ == null) {
						continue;
					}
					int newtoX = (int) drawbleitemJ.getRelativeX();

					if(newtoX == oldtoX){
						hasDouble[i] = true;
						isAnimaitionError = true;
					}
				}
			}
			if(isAnimaitionError){
			Log.d("liuyg123","isAnimaitionError = "+isAnimaitionError);
			return;
		}
	//	int nowheretogo = -1;
		for (int i = 0; i < mContent.getChildCount(); i++) {
			DrawableItem drawbleitem = mContent.getChildAt(i);

			if (drawbleitem == null) {
				continue;
			}
			Object o = drawbleitem.getTag();
			if (o == null) {
				continue;
			}
			ItemInfo tmpinfo = (ItemInfo) drawbleitem.getTag();

			int toX = (int) drawbleitem.getRelativeX();
			Log.d("liuyg123","resetItemsByLocation toX"+toX);
			int alreadySet = -1;
			for (int j = 0; j < mContent.getChildCount(); j++) {
				if (toX == mContent.p[mContent.getChildCount()][j].x) {
					//解决从第四个拖到第另个后结果不对的问题
				//	mContent.moveChildToIndex(drawbleitem, j);
					Log.i("liuyg123", "resetItemsByLocation ===hasdoubel[i]="+ hasDouble[i]);
                    if(i== mEmptyCell[0] && mDragInfo!=null && hasDouble[i]){
    					//Log.i("00","=a=i="+i+"==j="+j);
                    	tmpinfo.cellX = j;
    					tmpinfo.screen = j;
               		tmpinfo.container = LauncherSettings.Favorites.CONTAINER_HOTSEAT;
    					drawbleitem.setTag(tmpinfo);
    					tempitems.set(mDragInfo.cellX, drawbleitem);
    					alreadySet = mDragInfo.cellX;
                    }else{
					//Log.i("00","resetItemsByLocation ==i="+i+"==j="+j);
					tmpinfo.cellX = j;
					tmpinfo.screen = j;
           		tmpinfo.container = LauncherSettings.Favorites.CONTAINER_HOTSEAT;
					drawbleitem.setTag(tmpinfo);
					tempitems.set(j, drawbleitem);
					alreadySet = j;
                    }
				}else if(j>0 && (mContent.getChildCount() >1)&& (toX == mContent.p[mContent.getChildCount()-1][j-1].x)){
					if(alreadySet == -1){
						tmpinfo.cellX = j-1;
						tmpinfo.screen = j-1;
						tmpinfo.container = LauncherSettings.Favorites.CONTAINER_HOTSEAT;
						drawbleitem.setTag(tmpinfo);
						drawbleitem.setRelativeX(mContent.p[mContent.getChildCount()][j-1].x);
						tempitems.set(j-1, drawbleitem);
					}else{
					if(i < alreadySet ) {
				    	tmpinfo.cellX = j-1;
						tmpinfo.screen = j-1;
						tmpinfo.container = LauncherSettings.Favorites.CONTAINER_HOTSEAT;
						drawbleitem.setTag(tmpinfo);
						drawbleitem.setRelativeX(mContent.p[mContent.getChildCount()][j-1].x);
						tempitems.set(j-1, drawbleitem);
				    }else if (i >= alreadySet){
				    	tmpinfo.cellX = j;
						tmpinfo.screen = j;
						tmpinfo.container = LauncherSettings.Favorites.CONTAINER_HOTSEAT;
						drawbleitem.setTag(tmpinfo);
						drawbleitem.setRelativeX(mContent.p[mContent.getChildCount()][j].x);
						tempitems.set(j, drawbleitem);
				    }
					}
						//Log.i("00","=else=i="+i+"==j="+j);
				//	nowheretogo = i;
//修改上次滚动后 没有set为新的x的bug
					toSet[j] = true;
					/*tmpinfo.cellX = j;
					tmpinfo.screen = -1;
					drawbleitem.setTag(tmpinfo);
					drawbleitem.setRelativeX(mContent.p[mContent.getChildCount()][j].x);
					tempitems.set(j, drawbleitem);*/
				}
			}
		}
		/*DrawableItem drawbleitem = mContent.getChildAt(nowheretogo);
		for(int i =0;i<toSet.length;i++){
			if(toSet[i] && nowheretogo>-1 && hasDragOut){
				drawbleitem.setRelativeX(mContent.p[mContent.getChildCount()][i].x);
				tempitems.set(i,drawbleitem);
			}
		}*/
		for (int i = 0; i < tempitems.size(); i++) {
			DrawableItem drawbleitemX = tempitems.get(i);
			if (drawbleitemX == null) {
				continue;
			}
			int oldtoX = (int) drawbleitemX.getRelativeX();
			for (int j = i+1; j < tempitems.size(); j++) {
				DrawableItem drawbleitemJ = tempitems.get(j);
				if (drawbleitemJ == null) {
					continue;
				}
				int newtoX = (int) drawbleitemJ.getRelativeX();

				if(newtoX <= oldtoX){
					return;
				}
			}
		}
		logAllItems(tempitems);
		mContent.setChildren(tempitems);
	}
	/*** fixbug 12707 . AUT: zhaoxy . DATE: 2013-05-21. START***/
    private ShowToast mShowToast;
    public Toast mToast = null;

    private class ShowToast implements Runnable {
        private int id = 0;

        public ShowToast(int resId) {
            id = resId;
        }

        private void setMessageId(int resId) {
            id = resId;
        }

        @Override
        public void run() {
            if (mToast == null) {
                mToast = Toast.makeText(mXContext.getContext(), mXContext.getContext().getResources().getString(id), 0);
            } else {
                mToast.setText(id);
            }
            mToast.show();
        }

    }

    private boolean willCreateUserFolder(ItemInfo newinfo, int[] targetCell) {
        if (!mContent.mOccupied[targetCell[0]][targetCell[1]]) {
            return false;
        }

        DrawableItem cell = (DrawableItem) mContent.getChildAt(targetCell[0], targetCell[1]);

        boolean aboveShortcut = (cell.getTag() instanceof ShortcutInfo);
        boolean willBecomeShortcut = (newinfo instanceof ShortcutInfo);
        if (((ShortcutInfo) cell.getTag()).equalsIgnorePosition((ShortcutInfo) newinfo)) {

            if (mShowToast == null) {
                mShowToast = new ShowToast(R.string.application_not_create_folder);
            } else {
                mShowToast.setMessageId(R.string.application_not_create_folder);
            }
            mXContext.post(mShowToast);

        }
        return (aboveShortcut && willBecomeShortcut);
    }
    /*** fixbug 12707 . AUT: zhaoxy . DATE: 2013-05-21. END***/

    private boolean createUserFolderIfNecessary(long container, int[] targetCell, boolean external,
            Runnable postAnimationRunnable, ItemInfo newinfo) {
//        if (!mContent.mOccupied[targetCell[0]][targetCell[1]])
//        {
//            return false;
//        }
        
        DrawableItem cell = (DrawableItem)mContent.getChildAt(targetCell[0], targetCell[1]);
               

        boolean aboveShortcut = (newinfo instanceof ShortcutInfo);
        boolean willBecomeShortcut = (cell.getTag() instanceof ShortcutInfo);

        if (aboveShortcut && willBecomeShortcut) {
            ShortcutInfo sourceInfo = (ShortcutInfo) newinfo;
            ShortcutInfo destInfo = (ShortcutInfo) cell.getTag();
            if (sourceInfo.equalsIgnorePosition(destInfo)) {
                return false;
            }
            // if the drag started here, we need to remove it from the workspace
//            if (!external) {
//                getParentCellLayoutForView(mDragInfo.cell).removeView(mDragInfo.cell);
//            }

//            Rect folderLocation = new Rect();
//            float scale = mLauncher.getDragLayer().getDescendantRectRelativeToSelf(v,
//                    folderLocation);
//            xPagedView.removePagedViewItem(v);
            //把原来的从数据库删除。

            Context c = mXContext.getContext();
            if (c instanceof XLauncher) {
                XLauncher xlauncher = (XLauncher) c;
                FolderInfo fi = xlauncher.addFolder(container, targetCell[0], targetCell[0],
                        targetCell[1]);
                destInfo.screen = 0;
                destInfo.cellX = -1;
                destInfo.cellY = -1;
                destInfo.container = LauncherSettings.Favorites.CONTAINER_HOTSEAT;
                sourceInfo.screen = 0;
                sourceInfo.cellX = -1;
                sourceInfo.cellY = -1;
                sourceInfo.container = LauncherSettings.Favorites.CONTAINER_HOTSEAT;

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
    
    public void addInHotseat(FolderInfo info) {
        IconCache iconCache = ((LauncherApplication) mXLauncher.getApplicationContext())
          .getIconCache();
          
        mContent.bindInfo(info, iconCache);
//        Context c = mXContext.getContext();
//        if (c instanceof XLauncher) {
//            XLauncher xlauncher = (XLauncher) c;
//            newFolder.setOnLongClickListener(xlauncher);
//        }
        return ;
    }

    /*** fixbug 12707 . AUT: zhaoxy . DATE: 2013-05-21. START***/
    private boolean willAddToExistingUserFolder(int[] targetCell, ItemInfo newinfo) {
        DrawableItem cell = (DrawableItem) mContent.getChildAt(mTargetCell[0], mTargetCell[1]);
        if (cell instanceof XFolderIcon) {
            XFolderIcon fi = (XFolderIcon) cell;
            if (fi.acceptDrop(newinfo)) {
                return true;
            } else {
                if ((newinfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION || newinfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT
                        || newinfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT)) {
                    if (mShowToast == null) {
                        mShowToast = new ShowToast(R.string.application_existed_in_folder);
                    } else {
                        mShowToast.setMessageId(R.string.application_existed_in_folder);
                    }
                    mXContext.post(mShowToast);
                }

            }
        }
        return false;
    }
    /*** fixbug 12707 . AUT: zhaoxy . DATE: 2013-05-21. END***/
    private boolean addToExistingFolderIfNecessary(int x,int[] targetCell, boolean external, ItemInfo info, XDragObject d) {
    	
    	R5.echo("xhotseat manageFolderFeedback");
    	Log.d("liuyg123","manageFolderFeedback");
    	 int pixelX = x;
 		int cellWidth = mXContext.getResources().getDimensionPixelSize(R.dimen.hotseat_cell_width);
 		if(mTargetCell[0]<mContent.getChildCount()){
 			if(mContent.getChildAt(mTargetCell[0])!=null){
 				int targetX = (int)mContent.getChildAt(mTargetCell[0]).getRelativeX();
 				if(targetX<=pixelX&&pixelX<targetX+cellWidth){
                    return addToExistingFolderIfNecessary(mTargetCell, false, info, d);
 				}
 				
 			}
 			
 		}
 		return false;
       
    }

    private boolean addToExistingFolderIfNecessary(int[] targetCell, boolean external, ItemInfo info, XDragObject d) {
        DrawableItem cell = (DrawableItem)mContent.getChildAt(mTargetCell[0], mTargetCell[1]);
        if (cell instanceof XFolderIcon) {
            XFolderIcon fi = (XFolderIcon) cell;
            // if the drag started here, we need to remove it from the workspace
//            if (!external) {
//                XPagedViewItem newView = xPagedView.findPageItemAt(info.screen,
//                        info.cellX, info.cellY);
//                xPagedView.removePagedViewItem(newView);
//            }
            if (fi.acceptDrop(info)) {
                fi.onDrop(d);
                return true;
            }
        }
        return false;
    }

    
    /*** RK_ID: APPS_CATEGORY. AUT: GECN1 . DATE: 2013-03-30 . START ***/
    public XFolderIcon getFolderIconByTitle(String title) {
        if (title == null || title.equals("")) return null;
        
        DrawableItem item;
        for (int i = 0; i < mCellCountX; i++) {
            for (int k = 0; k < mCellCountY; k++) {
                item = mContent.getChildAt(i, k);
                if (item instanceof XFolderIcon) {
                    XFolderIcon f = (XFolderIcon) item;
                    if (title.equals(f.mInfo.title)) {
                        return f;
                    }
                }
            }
        }
        return null;
    }
    /*** RK_ID: APPS_CATEGORY. AUT: GECN1 . DATE: 2013-03-30 . END ***/
    
    public void removeItems(ArrayList<ApplicationInfo> apps) {
        final HashSet<ComponentName> ComponentNames = new HashSet<ComponentName>();
        final int appCount = apps.size();
        for (int i = 0; i < appCount; i++) {
        	if( apps.get(i).componentName != null){
        		ComponentName componentNames = new ComponentName(apps.get(i).componentName.getPackageName()
        				,apps.get(i).componentName.getClassName());
        		ComponentNames.add(componentNames);
        	}
        }

        getXContext().post(new Runnable() {
            // Avoid ANRs by treating each screen separately
            @Override
            public void run() {
                final ArrayList<DrawableItem> childrenToRemove = new ArrayList<DrawableItem>();
                childrenToRemove.clear();

                if (mContent != null) {
                    mContent.checkAndAddShortcut(ComponentNames, childrenToRemove,true);
                  //old
                    //mContent.checkAndAddShortcut(ComponentNames, childrenToRemove,false);
                }

                int childCount = childrenToRemove.size();
                for (int j = childCount-1; j >=0; j--) {
                    DrawableItem child = childrenToRemove.get(j);

                    // Note: We can not remove the view directly from CellLayoutChildren as this
                    // does not re-mark the spaces as unoccupied.
                    ItemInfo deleteInfo = (ItemInfo)child.getTag();
                    if(deleteInfo!=null){
                    	for (int i = deleteInfo.cellX+1; i <mContent.getChildCount(); i++) {
                    		DrawableItem drawbleitem = mContent.getChildAt(i);
                    		if (drawbleitem == null) {
                    			//Log.e("liuyg123","drawbleitem == null");
                    			continue;
                    		}

                    		Object o = drawbleitem.getTag();
                    		if ((o == null/* || !(o instanceof ShortcutInfo)*/)){
                    			//Log.e("liuyg123","o == null || !(o instanceof ShortcutInfo)) && i != XHotseat.sAllAppsButtonRank");
                    			continue;
                    		}
                    		ItemInfo tmpinfo = (ItemInfo)drawbleitem.getTag();   
                    		tmpinfo.cellX=tmpinfo.cellX-1;
                    		tmpinfo.screen = tmpinfo.screen-1;
                    		//Log.e("liuyg123","onDropCompleted XLauncherModel.updateItemInDatabase(mXLauncher, tmpinfo)"+tmpinfo.cellX);
                    		XLauncherModel.updateItemInDatabase(mXLauncher, tmpinfo);
                    	}
                    }
                    removeView(child);
            		mContent.refreshChildrenCount();
                    mContent.resizeLayout();
//                    createDockAddIcon(cellX, cellY);
                    if (child instanceof XDropTarget) {
                        mDragController.removeDropTarget((XDropTarget) child);
                    }
                }

                if (childCount > 0) {
                    invalidate();
                }
            }
        });
    }

    DrawableItem getDragView() {
        return mContent.getChildAt(mTempCell[0], mTempCell[1]);
    }
    
	/*RK_ID:RK_SD_APPS zhangdxa 2013-4-15. S***/
    public void refreshItem(Context context, DrawableItem target, ItemInfo info,
            IconCache iconCache, boolean onlyBitmap) {
        mContent.refreshItem(context, target, info, iconCache, onlyBitmap);
    }
	/*RK_ID:RK_SD_APPS zhangdxa 2013-4-15. E***/
    /** RK_ID: RK_NEWFEATURE_SHOW_MISSED_NUMBER. AUT: zhanglz1 DATE: 2013-05-06 S */
    public ArrayList<DrawableItem> getChildren(){
    	ArrayList<DrawableItem> children = new ArrayList<DrawableItem>();
    	 DrawableItem item;
         for (int i = 0; i < mCellCountX; i++) {
             for (int k = 0; k < mCellCountY; k++) {
                 item = mContent.getChildAt(i, k);
         		children.add(item);
             }
         }
		return children;
    }
    // type 0 missed message,1 missed call
	public void updateMissedView(int type, int number) {
//		ArrayList<DrawableItem> children = getChildren();
		if (type == 0)
			Utilities.missedNum[0] = number;
		else if (type == 1)
			Utilities.missedNum[1] = number;
		mContent.showmissed();
	}
    /** RK_ID: RK_NEWFEATURE_SHOW_MISSED_NUMBER. AUT: zhanglz1 DATE: 2013-05-06 E */

	/*** fixbug 12707 . AUT: zhaoxy . DATE: 2013-05-21. START***/
    private static final int TIMEOUT_OPEN_FOLDER = 1000;
    private static final int TIMEOUT_CLOSE_FOLDER = 500;
    private static final int MSG_OPEN_FOLDER = 100;
    private static final int MSG_CLOSE_FOLDER = 200;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
            case MSG_OPEN_FOLDER:
            	cleanupAddToFolder();
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
            default:
                break;
            }
        };
    };

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

    public void closeFolderDelayed() {
        if (getOpenFolder() != null && !mHandler.hasMessages(MSG_CLOSE_FOLDER)) {
            mHandler.sendEmptyMessageDelayed(MSG_CLOSE_FOLDER, TIMEOUT_CLOSE_FOLDER);
        }
    }

    public void cancelcloseFolderDelayed() {
        mHandler.removeMessages(MSG_CLOSE_FOLDER);
    }

    protected void cleanupAddToFolder() {
    	 mIsFolderAccept = false;
        mHandler.removeMessages(MSG_OPEN_FOLDER);
        mHandler.removeMessages(MSG_CLOSE_FOLDER);
        if (mFolderRingAnimator != null) {
            mFolderRingAnimator.animateToNaturalState();
        }
        mFolderRingAnimator = null;
    }

    FolderRingAnimator mFolderRingAnimator = null;
    public void showAddToFolder(int cellX, int cellY, XFolderIcon fi) {
    	mIsFolderAccept = true;
        if (mFolderRingAnimator == null) {
            mFolderRingAnimator = new FolderRingAnimator(mXContext, fi);
        }
        mFolderRingAnimator.closeAnimation();
        mFolderRingAnimator.setFolderIcon(fi);
        mFolderRingAnimator.setCell(cellX, cellY);
        mFolderRingAnimator.setHotseat(XHotseat.this);
        mFolderRingAnimator.animateToAcceptState();
        mContent.showFolderAccept(mFolderRingAnimator);
    }
    public void hideFolderAccept(FolderRingAnimator fra) {
    	mContent.hideFolderAccept(fra);
    }
    
    private boolean checkFolderFeedback(int x,ItemInfo info, int[] targetCell) {
    	R5.echo("xhotseat checkFolderFeedback");
    	Log.d("liuyg123","checkFolderFeedback");
    	if(info.itemType==LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT){
    		return false;
    	}
    	 int pixelX = x;
 		int cellWidth = mXContext.getResources().getDimensionPixelSize(R.dimen.hotseat_cell_width);
 		if(mTargetCell[0]<mContent.getChildCount()){
 			if(mContent.getChildAt(mTargetCell[0])!=null){
 				int targetX = (int)mContent.getChildAt(mTargetCell[0]).getRelativeX();
 				if(targetX<=pixelX&&pixelX<targetX+cellWidth){
    	          return manageFolderFeedback(info,targetCell);
 				}
 				
 			}
 			
 		}
 		if(mIsFolderAccept) {
 			cleanupAddToFolder();
        }
 		Log.d("liuyg123","checkFolderFeedback false");
 		return false;
    }
    private boolean manageFolderFeedback(ItemInfo info, int[] targetCell) {

        boolean willAddToFolder = willAddToExistingUserFolder(targetCell, info);
        if (willAddToFolder) {
            DrawableItem dropOverView = (DrawableItem) mContent.getChildAt(targetCell[0], targetCell[1]);


            XFolderIcon fi = (XFolderIcon) dropOverView;
            if(!mIsFolderAccept) {
            	showAddToFolder(targetCell[0], targetCell[1], fi);
            }
            if (!mHandler.hasMessages(MSG_OPEN_FOLDER)) {
                mHandler.sendEmptyMessageDelayed(MSG_CLOSE_FOLDER, TIMEOUT_CLOSE_FOLDER);
                android.os.Message msg = mHandler.obtainMessage(MSG_OPEN_FOLDER, fi);
                if(fi!=null&&fi.mInfo!=null&&fi.mInfo.contents!=null&&fi.mInfo.contents.size()!=0){
                	 mHandler.sendMessageDelayed(msg, TIMEOUT_OPEN_FOLDER);	
                }
//                Log.d("liuyg1234","manageFolderFeedback fi!=null&&fi.mInfo!=null&&fi.mInfo.contents!=null&&fi.mInfo.contents.size()!=0");
            } else {
                // do nothing
                // mHandler.removeMessages(MSG_OPEN_FOLDER);
                // android.os.Message msg =
                // mHandler.obtainMessage(MSG_OPEN_FOLDER, fi);
                // mHandler.sendMessageDelayed(msg, TIMEOUT_OPEN_FOLDER);
            }
        return true;   
        } else {
        	 R5.echo("xhotseat cleanupAddToFolder");
     		if(mIsFolderAccept) {
     			cleanupAddToFolder();
            }
            mHandler.removeMessages(MSG_OPEN_FOLDER);
            if (getOpenFolder() != null && !mHandler.hasMessages(MSG_CLOSE_FOLDER)) {
                mHandler.sendEmptyMessageDelayed(MSG_CLOSE_FOLDER, TIMEOUT_CLOSE_FOLDER);
            }
        }

        return false;
        /* RK_ID: Jelly_Bean . AUT: yumina . DATE: 2012-08-16 . E */
    }
    /*** fixbug 12707 . AUT: zhaoxy . DATE: 2013-05-21. END***/

    public void animViews(float rotation, Object dragInfo,float dy) {
        int size = mContent.getChildCount();

        for (int index = 0; index < size; index++) {
            if (dragInfo instanceof ItemInfo) {
                ItemInfo itemInfo = (ItemInfo) dragInfo;
                if (itemInfo.cellX == index
                        && itemInfo.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
                    continue;
                }
            }

            DrawableItem item = mContent.getChildAt(index, 0);
            Matrix m = item.getMatrix();
            m.reset();
            if(dy == 0f){
            	dy = item.getHeight() / 10f;
            }
            m.setRotate(rotation, item.localRect.centerX(), dy);
            item.updateMatrix(m);
        }
    }
    
    public void animReset() {
        int size = mContent.getChildCount();

        for (int index = 0; index < size; index++) {
            DrawableItem item = mContent.getChildAt(index, 0);
            Matrix m = item.getMatrix();
            m.reset();
            item.updateMatrix(m);
        }
    }
    public void refreshLocationPrepareForConfigeChange(){
    	if(mContent!=null){
//    		mContent.setHasLocationPrepared(false);
    		//mContent.locationPrepare();
    	}
    }
    private void logAllItems(){
    	/*for(int i = 0;i<mContent.getChildCount();i++){
    		ItemInfo info = (ItemInfo)mContent.getChildAt(i).getTag();
    		if(info instanceof ShortcutInfo){
    			Log.i("00", "children==i="+i+"=relativeX="+mContent.getChildAt(i).getRelativeX()+"==intent="+((ShortcutInfo)info).intent);
    		}else if(info instanceof ApplicationInfo){
    			Log.i("00", "children==i="+i+"=relativeX="+mContent.getChildAt(i).getRelativeX()+"==intent="+((ApplicationInfo)info).intent);
    		}else{
    			if(info == null)
    				Log.i("00", "children==i="+i+"=relativeX="+mContent.getChildAt(i).getRelativeX()
        					+"==null=");
    			else
    				Log.i("00", "children==i="+i+"=relativeX="+mContent.getChildAt(i).getRelativeX()
    					+"==other="+info.getClass());

    		}
    	}*/
    }
	private void logAllItems(ArrayList<DrawableItem> list){
    	/*for(int i = 0;i<list.size();i++){
    		ItemInfo info = (ItemInfo)list.get(i).getTag();
    		if(info instanceof ShortcutInfo){
    			Log.i("00", "temp==i="+i+"=relativeX="+list.get(i).getRelativeX()+"==intent="+((ShortcutInfo)info).intent);
    		}else if(info instanceof ApplicationInfo){
    			Log.i("00", "temp==i="+i+"=relativeX="+list.get(i).getRelativeX()+"==intent="+((ApplicationInfo)info).intent);
    		}else{
    			if(info == null)
    				Log.i("00", "temp==i="+i+"=relativeX="+list.get(i).getRelativeX()
        					+"==null=");
    			else
    				Log.i("00", "temp==i="+i+"=relativeX="+list.get(i).getRelativeX()
    					+"==other="+info.getClass());
    		}
    	}*/
    }

    // fix bug 18175
    void removeFolder(int cellX) {
    	DrawableItem drawbleitem = mContent.getChildAt(cellX);
    	if (drawbleitem != null) {
    		if(drawbleitem instanceof XFolderIcon){
    			FolderInfo fInfo = (FolderInfo)drawbleitem.getTag();
    			if(fInfo.contents.size()>0){
    				return;
    			}
    		}else {
    			return;
    		}
    	}
        mContent.removeItem(cellX);
        allItemsChangeIndex(cellX, -1);
    }

    void addFolder(FolderInfo fInfo, IconCache iconCache) {
        allItemsChangeIndex(fInfo.cellX, 1);
//        mContent.bindInfo(fInfo, iconCache);
        XFolderIcon newFolder = XFolderIcon.obtain(getXContext(), (XLauncher) getXContext()
                .getContext(), fInfo);
        Log.i("00", "=====add folder==");
        // Hide folder title in the hotseat
        //newFolder.setTextVisible(false);
        newFolder.setOnLongClickListener(mXLauncher);
        mContent.addItem(newFolder, fInfo.cellX);
        mContent.resizeLayout();
    }

    private void allItemsChangeIndex(int cellX, int j) {
        for (int i = cellX; i < mContent.getChildCount(); i++) {
            DrawableItem drawbleitem = mContent.getChildAt(i);
            if (drawbleitem == null) {
                continue;
            }

            Object o = drawbleitem.getTag();
            if (o == null || !(o instanceof ShortcutInfo)) {
                continue;
            }

            ItemInfo tmpinfo = (ItemInfo) drawbleitem.getTag();
            tmpinfo.cellX = tmpinfo.cellX + j;
            tmpinfo.screen = tmpinfo.screen + j;
            drawbleitem.setTag(tmpinfo);
            XLauncherModel.updateItemInDatabase(mXLauncher, tmpinfo);
        }
    }
    
    
    /*PK_ID:UPDATE SHORTCUT ICON AUTH :GECN1 DATE:2013-7-22 S*/
    void updateShortcuts(ArrayList<ApplicationInfo> apps, IconCache iconCache) {
    	updateShortcuts(apps, iconCache, null, false);
    }
    void updateShortcuts(ArrayList<ApplicationInfo> apps, IconCache iconCache,Set<ApplicationInfo>updateApps) {
    	updateShortcuts(apps, iconCache, updateApps, false);
    }
    void updateShortcuts(ArrayList<ApplicationInfo> apps, IconCache iconCache,Set<ApplicationInfo>updateApps,boolean isCommendShortcut) {
    	for (int i = 0; i < mContent.getChildCount(); i++) {
			DrawableItem item = mContent.getChildAt(i);
			if(item ==null){
				continue;
			}
			Object itemInfo = item.getTag();
            if (itemInfo == null || !(itemInfo instanceof ItemInfo)) {
                continue;
            }
            updateAnIcon(item, (ItemInfo) itemInfo, apps, iconCache,updateApps,isCommendShortcut);
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
            final ComponentName name = intent.getComponent();
            if (info.itemType != LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT && name != null) {
                final int appCount = apps.size();
                for (int k = 0; k < appCount; k++) {
                    ApplicationInfo app = apps.get(k);
                    if (app.componentName.equals(name)) {
                    	  Log.i("XWorkspace", "updateAnIcon info ~~~~" + info);
                          if(isCommendShortcut && info.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION){
                          	info.setIcon(null);
                          	info.uri= XLauncherModel.getCommandAppDownloadUri(name.getPackageName(), info.title.toString());
                          	info.itemType = LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT;
                          }else if(!isCommendShortcut){
                          	info.setIcon(app.iconBitmap);
                          	info.uri = null;
                          	info.itemType = LauncherSettings.Favorites.ITEM_TYPE_APPLICATION;
                          }
                          if(updateApps != null){
                          	updateApps.add(app);
                          }
                          if (drawableTarget instanceof XShortcutIconView) {
                        	  XShortcutIconView shortcutView = (XShortcutIconView) drawableTarget;
                        	  shortcutView.applyFromShortcutInfo(info, iconCache);
                          }else if(drawableTarget instanceof XShortcutIconView){
                        	  mContent.refreshItem(getXContext().getContext(), drawableTarget, info, iconCache, true);
                          }
                        
                        XLauncherModel.updateItemInDatabase(getXContext().getContext(), info);
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
    
    
    void deleteShortcuts(ArrayList<ApplicationInfo> apps, IconCache iconCache) {


    	for (int i = 0; i < mContent.getChildCount(); i++) {
			DrawableItem item = mContent.getChildAt(i);
			if(item ==null){
				continue;
			}
			Object itemInfo = item.getTag();
            if (itemInfo == null || !(itemInfo instanceof ItemInfo)) {
                continue;
            }
            deleteItemWhenSDCardUninstall(item, (ItemInfo)itemInfo,apps);
		}
    }
    private void deleteItemWhenSDCardUninstall(DrawableItem item,ItemInfo itemInfo,ArrayList<ApplicationInfo> apps){
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
                	 removeView(item);
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
    
    
    public void filterApplicationExsitedInHotseat(List<ShortcutInfo> apps){
    	final List<ShortcutInfo> toRemove = new ArrayList<ShortcutInfo>();
		final HashMap<String, ShortcutInfo> old = new HashMap<String, ShortcutInfo>();		
		final Iterator<ShortcutInfo> itApps = apps.iterator();
		
		ShortcutInfo app = null;
		DrawableItem item = null;
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
			
			for (int i = 0; i < mContent.getChildCount(); i++) {
				item = mContent.getChildAt(i);
				if(item ==null){
					continue;
				}
				itemInfo = item.getTag();
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
    
    
    public List<ApplicationInfo> filterApplicationExsitedInHotseat(List<ApplicationInfo> apps,IconCache iconCache){
    	LinkedList<ApplicationInfo> result = new LinkedList<ApplicationInfo>(apps);
		final HashMap<String, ApplicationInfo> old = new HashMap<String, ApplicationInfo>();		
		final LinkedList<ApplicationInfo> toRemove = new LinkedList<ApplicationInfo>();		
		final Iterator<ApplicationInfo> itApps = result.iterator();
		
		ApplicationInfo app = null;
		DrawableItem item = null;
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
			
			for (int i = 0; i < mContent.getChildCount(); i++) {
				item = mContent.getChildAt(i);
				if(item == null){
					continue;
				}
				itemInfo = item.getTag();
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
						final XShortcutIconView shortcutView = (XShortcutIconView) item;
						info.setIcon(app.iconBitmap);
						shortcutView.applyFromShortcutInfo(info, iconCache);
                        XLauncherModel.updateItemInDatabase(getXContext().getContext(), info);
                        android.util.Log.i("dooba", "----------22222------duplicate--------" + info.title);
						toRemove.add(app);
						break;
					}
				} else if (itemInfo instanceof FolderInfo) {
					final XFolderIcon folderIcon = (XFolderIcon) item;
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
    /*PK_ID:UPDATE SHORTCUT ICON AUTH :GECN1 DATE:2013-7-22 S*/

    void addDraggingViewBack(ShortcutInfo info) {
        mContent.getChildAt(info.cellX).setVisibility(true);
        mContent.resizeLayout();
        mContent.invalidate();
    }

    /* RK_ID: RK_LOCATE_APP. AUT: zhangdxa DATE: 2013-8-6 S*/
	public DrawableItem getChildrenItem(int cellX) {
		if(mContent.getChildCount()<=cellX){
			return null;
		}else{
			return mContent.getChildAt(cellX);
		}
	}
	public LocateItem getItemByComponent(ComponentName cname){
		String componentName = cname.flattenToString();
    	for (int i = 0; i < mContent.getChildCount(); i++) {
			DrawableItem item = mContent.getChildAt(i);
			if(item == null){
				continue;
			}
			Object objectItem = item.getTag();
            if (objectItem == null || !(objectItem instanceof ItemInfo)) {
                continue;
            }
            ItemInfo itemInfo = (ItemInfo)objectItem;
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
	
	public int getIconCount() {
	    return mContent.getChildCount();
	}
	private void updateChildDatabase() {
		for (int i = 0; i < mContent.getChildCount(); i++) {
			DrawableItem drawbleitem = mContent.getChildAt(i);
			if (drawbleitem == null) {
				continue;
			}

			Object o = drawbleitem.getTag();
			if (o == null) {
				continue;
			}
			ItemInfo tmpinfo = (ItemInfo) drawbleitem.getTag();
			XLauncherModel.updateItemInDatabase(mXLauncher, tmpinfo);
		}
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
    }
    
    public void clearNewBgAndSetNum(String componentName, int num){
    	if (mContent != null)
    	{
    		mContent.clearNewBgAndSetNum(componentName, num);
    	}
    }
    
    public void removeItemByInfo(ShortcutInfo deleteInfo) {
    	if (deleteInfo == null) {
    		return;
    	}
    	DrawableItem child = mContent.getChildAt(deleteInfo.cellX, deleteInfo.cellY);
    	if (child == null) {
    		return;
    	}
    	XLauncherModel.deleteItemFromDatabase(getXContext().getContext(), deleteInfo);
    	
    	for (int i = deleteInfo.cellX + 1; i < mContent.getChildCount(); i++) {
    		DrawableItem drawbleitem = mContent.getChildAt(i);
    		if (drawbleitem == null) {
    			continue;
    		}

    		Object o = drawbleitem.getTag();
    		if ((o == null)){
    			continue;
    		}
    		ItemInfo tmpinfo = (ItemInfo)drawbleitem.getTag();   
    		tmpinfo.cellX = tmpinfo.cellX-1;
    		tmpinfo.screen = tmpinfo.screen-1;
    		XLauncherModel.updateItemInDatabase(mXLauncher, tmpinfo);
    	}
        removeView(child);
		mContent.refreshChildrenCount();
        mContent.resizeLayout();
        if (child instanceof XDropTarget) {
            mDragController.removeDropTarget((XDropTarget) child);
        }
    }
}
