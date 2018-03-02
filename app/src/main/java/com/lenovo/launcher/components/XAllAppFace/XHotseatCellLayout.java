package com.lenovo.launcher.components.XAllAppFace;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import com.lenovo.launcher.components.XAllAppFace.XFolderIcon.FolderRingAnimator;
import com.lenovo.launcher.components.XAllAppFace.slimengine.BaseDrawableGroup;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;
import com.lenovo.launcher.components.XAllAppFace.utilities.Utilities;
import com.lenovo.launcher2.commoninterface.FolderInfo;
import com.lenovo.launcher2.commoninterface.IconCache;
import com.lenovo.launcher2.commoninterface.ItemInfo;
import com.lenovo.launcher2.commoninterface.LauncherSettings;
import com.lenovo.launcher2.commoninterface.ShortcutInfo;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.Debug.R5;
import com.lenovo.launcher.R;

public class XHotseatCellLayout extends BaseDrawableGroup {
    protected static final String TAG = "XHotseatCellLayout";

    private int CHILD_COUNT = 5;

    private int mCellHeight;
    private int mCellWidth;
    private int[] mWidthGap;
    private int[] mMinWidthGap;
//    private int mHeightGap;
//    private int mCellPaddingLeft;
//    private int mCellPaddingRight;

    private int mIconSize;

    private XLauncher mxLauncher;
    private ValueAnimator mRotateAnimator;
    private Camera mCamera = new Camera();
    private Runnable mUpdateDbRunnable = null;
    int max_cellx; // don't change that one
    private int paddingLeft; 
    private int paddingRight ;
    private int paddingTop ;
    private int paddingBottom ;
    private LauncherApplication mLA;
    private int phoneindex =-1;
    //private Drawable bottomShadow = null;
    private ArrayList<DrawableItem> sFailedADDHotseatItems = new ArrayList<DrawableItem>();
    public XHotseatCellLayout(XContext context, XLauncher xLauncher) {
        super(context);

        mxLauncher = xLauncher;
        Resources res = context.getResources();
    	mLA = (LauncherApplication) mxLauncher.getApplicationContext();
        // background
        setBackgroundDrawable(mLA.mLauncherContext.getDrawable(R.drawable.hotseat_bg_panel));

        // padding
        paddingLeft = res.getDimensionPixelSize(R.dimen.button_bar_width_left_padding);
        paddingRight = res.getDimensionPixelSize(R.dimen.button_bar_width_right_padding);
        paddingTop = res.getDimensionPixelSize(R.dimen.button_bar_height_top_padding);
        paddingBottom = res.getDimensionPixelSize(R.dimen.button_bar_height_bottom_padding);
        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        max_cellx = CHILD_COUNT= res.getInteger(R.integer.hotseat_cellx_count);
        //Log.d("liuyg123","XHotseatCellLayout constructing");
        mMinWidthGap = new int[max_cellx+1];
        mWidthGap = new int[max_cellx+1];
        
     /*   bottomShadow = getXContext().getResources().getDrawable(
        		R.drawable.statusbar_home_bottom_bg);*/
        
        refreshChildrenCount();
        init(context, xLauncher);
        

    }
    public void refreshChildrenCount(){
        childCount = getChildCount();
    }
    public void setChildrenCount(int i){
        childCount = i;
    }
    private void init(XContext context, XLauncher xLauncher) {
        Resources res = context.getResources();
        phoneindex = context.getContext().getResources().getInteger(R.integer.config_machine_type);
        // dimens
        mCellHeight = res.getDimensionPixelSize(R.dimen.hotseat_cell_height);
        Log.d("liuyg1234","XHotseatCellLayout mCellHeight"+mCellHeight);
        mCellWidth = res.getDimensionPixelSize(R.dimen.hotseat_cell_width);
		for (int i = 1; i < max_cellx+1; i++) {
			mWidthGap[i] = res.getDimensionPixelSize(R.dimen.hotseat_width_gap);
		}
//        mCellPaddingLeft = res.getDimensionPixelSize(R.dimen.app_icon_padding_left);
//        mCellPaddingRight = res.getDimensionPixelSize(R.dimen.app_icon_padding_right);

        mIconSize = res.getDimensionPixelSize(R.dimen.app_icon_size);
 /*RK_ID:RK_SINGLE_LAYER liuyg1 2013-6-13. S*/
        int childCount = XLauncherModel.getHotseatChildCount(xLauncher);
        Log.d("liuyg1234","XHotseatCellLayout init"+childCount);
 /*RK_ID:RK_SINGLE_LAYER liuyg1 2013-6-13. E*/
        for (int i = 0; i < childCount; i++) {
        	//Log.d("liuyg123","XHotseatCellLayout init"+i);
        	
        	 DrawableItem item = new XShortcutIconView(context);
//            XIconDrawable icon = new XPressIconDrawable(context, null);
            addItem(item);
//            icon.resize(new RectF(0, 0, mIconSize, mIconSize));
        }
    }

    @Override
    public void resize(RectF rect) {
    	super.resize(rect);
    	//Log.d("liuyg123","resize ");
   	
    	setBackgroundDrawable(mLA.mLauncherContext.getDrawable(R.drawable.hotseat_bg_panel));
    	refreshChildrenCount();
    	resizeLayout();
    }
    private int childCount;

    public void resizeLayout() {
        //Resources res = getXContext().getResources();
        float totalWidth = getWidth();
  locationPrepare();
        int count = getChildCount();
        if(count>max_cellx){
        	return;
        }
        //Log.d("liuyg123","childCount ===================="+count);
      //  Log.d("liuyg123","totalWidth ===================="+totalWidth);
        if(count>0){
        	int padding = (int)(getWidth()- count*mCellWidth)/count;
        	paddingRight = paddingLeft = padding/2;
        	//Log.i("00", "11111==getWidth()="+getWidth() +"=childCount="+count +"=mCellWidth ="+mCellWidth);
        }
      //  Log.d("liuyg123","paddingRight ===================="+paddingRight);
      //  Log.d("liuyg123","paddingLeft ===================="+paddingLeft);
        final float hSpace = totalWidth - paddingLeft - paddingRight;
        if(count>1){
        	mWidthGap[count] = (int) ((hSpace - mCellWidth * (count)) / (count -1));
        	mMinWidthGap[count] = (int) ((hSpace - mCellWidth * (count)) / (count +1));
        }
//        float totalHeight = getHeight();
//        final float vSpace = totalHeight - paddingTop - paddingBottom;
		for (int i = 0; i < count; i++) {
			DrawableItem item = (DrawableItem) getChildAt(i);
			if (item == null) {
				continue;
			}
//			Drawable bg = item.getBackgroundDrawable();

         int width, height;
         width = mCellWidth;
         height = mCellHeight;
//            if (bg == null) {
//                width = (int)item.getWidth();
//                height = (int)item.getHeight();
//            } else{
//                width = bg.getIntrinsicWidth();
//                height = bg.getIntrinsicHeight();
//            }
//            
////            R5.echo("resizeLayout item = " + i + "width = " + width + "height = " + height);
//
//            if (i == 2) {
//                width = Math.min(width, (int) item.getWidth());
//                height = Math.min(height, (int) item.getHeight());
//            }
//            width = Math.min(width, m);
           if(hasLocationPrepared&& p[count][i]!=null){
    			int x = p[count][i].x;
    			int y = p[count][i].y;
    			item.setRelativeX(x);
    			item.setRelativeY(y);
            }else{
            	int[] location = getRelativeX(count,i, width, height);
            	item.setRelativeX(location[0]);
    			item.setRelativeY(location[1]);
            }

			ItemInfo tmpinfo = (ItemInfo) item.getTag();
			if (tmpinfo != null) {
				tmpinfo.cellX = i;
				item.setTag(tmpinfo);
			}
        }
       // locationPrepare();
    }
//bugfix 17646
    public void validateHotseat(){
    	ArrayList<ItemInfo> hotseatItems = XLauncherModel.getHotsetItems();
    	if(hotseatItems!=null&&hotseatItems.size()<=0){
    		return;
    	}
//    	Log.d("liuyg1234","hotseatItems.size() = "+hotseatItems.size());
    	boolean needBind = false;
		for (int i = 0; i < getChildCount(); i++) {
			DrawableItem item = getChildAt(i);
			if (item == null) {
				Log.d("liuyg1234","item == null index="+i);
				removeItem(i);
				needBind = true;
				i--;
				continue;
			}
			
			Object oi = item.getTag();
			if(oi == null){
				needBind = true;
				removeItem(i);
				i--;
				
			}
			else if(oi instanceof FolderInfo){
				FolderInfo fi = (FolderInfo)oi;
				if(fi.contents.size() <=0){
					Log.d("liuyg1234","fi.contents.size() <=0   i="+i);
					needBind = true;
					removeItem(i);
					i--;
                    XLauncherModel.deleteItemFromDatabase(mxLauncher, fi);
                    mxLauncher.removeFolder(fi);
				}
			}
			
		}
		if(needBind){
			for (int i = 0; i < getChildCount(); i++) {
				DrawableItem item = getChildAt(i);
				if (item == null) {
					continue;
				}
				ItemInfo tmpinfo = (ItemInfo) item.getTag();
				if(tmpinfo==null){
					continue;
				}
				tmpinfo.cellX = i;
				tmpinfo.screen = i;
				item.setTag(tmpinfo);
				XLauncherModel.updateItemInDatabase(mxLauncher, tmpinfo);
				}
			Log.d("liuyg1234","sFailedADDHotseatItems.size() = "+sFailedADDHotseatItems.size());
			for (int i = 0; i < sFailedADDHotseatItems.size(); i++) {

				DrawableItem item = (DrawableItem) sFailedADDHotseatItems.get(i);
				if(item==null){
					Log.d("liuyg1234","hotseatItems.get(i)"+i+" tmpinfo==null ");
					continue;
				}
				ItemInfo tmpinfo = (ItemInfo) item.getTag();
				if(tmpinfo==null){
					continue;
				}
				tmpinfo.cellX = getChildCount();
				tmpinfo.screen = getChildCount();
				item.setTag(tmpinfo);
				XLauncherModel.updateItemInDatabase(mxLauncher, tmpinfo);
				addItem(item);
				Log.d("liuyg1234","sFailedADDHotseatItems.get(i)"+i+" tmpinfo.cellX = "+tmpinfo.cellX);
			}
			Log.d("liuyg1234","childcount = "+getChildCount());
			resizeLayout();	
		}else{
			for (int i = 0; i < getChildCount(); i++) {
				DrawableItem item = getChildAt(i);
				if (item == null) {
					continue;
				}
				ItemInfo tmpinfo = (ItemInfo) item.getTag();
				if(tmpinfo==null){
					continue;
				}
				Log.d("liuyg1234","i="+i+" tmpinfo.cellX = "+tmpinfo.cellX);
				tmpinfo.cellX = i;
				
				tmpinfo.screen = i;
				item.setTag(tmpinfo);
				XLauncherModel.updateItemInDatabase(mxLauncher, tmpinfo);
			}
			resizeLayout();

		}
    }
    public void bindInfo(ItemInfo item, IconCache iconCache) {
    	
    	Log.d("liuyg1234","bindInfo item.cellx"+item.cellX);
        if (item instanceof ShortcutInfo) {
            final ShortcutInfo info = (ShortcutInfo) item;
            Bitmap bitmap = info.getIcon(iconCache, true);
            final XDragLayer dragLayer = mxLauncher.getDragLayer();

            OnClickListener l = new OnClickListener() {

                @Override
                public void onClick(DrawableItem item) {
                    XWorkspace workspace = mxLauncher.getWorkspace();
                    if (workspace != null
                            && workspace.filterLeLauncherShortcut(mxLauncher, info.intent)) {
                        return;
                    }
                    int[] pos = new int[2];
                    dragLayer.getLocationInDragLayer(item, pos);
                    final Intent intent = info.intent;
                    //test by liuli 2013-08-6
                    int windowTop = ((XLauncherView) getXContext()).getWindowTop();
                    pos[1] += windowTop;
                    Log.i(TAG, "pos[0] === " + pos[0] + "     pos[1]====" + pos[1]);

                    intent.setSourceBounds(new Rect(pos[0], pos[1], pos[0] + (int) item.getWidth(),
                            pos[1] + (int) item.getHeight()));
                    intent.putExtra("STATE","HOTSEAT");
                    
                    //test by dining 2013-07-26
                    final int[] measured = new int[2];
                    measured[0] = (int) item.getWidth();
                    measured[1] = (int) item.getHeight();
                    
                    /*RK_ID:RK_SD_APPS zhangdxa 2013-4-15. S***/
                    //mxLauncher.startActivity(info.intent);
                	if( (info.itemType == LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT) &&
                		(info.uri != null )){
                		Uri uri = Uri.parse(info.uri);
                		Intent intentView = new Intent(Intent.ACTION_VIEW, uri);
                		mxLauncher.startActivitySafely(intentView, null);
//                		mxLauncher.getModel().getUsageStatsMonitor().add(intentView);
                	}else{
                		//test by dining 2013-07-26
                		//for snooby using the ext-function to start activity
                		boolean bResult = false;
                		bResult = mxLauncher.startActivitySafely(info.intent, measured, null);
                		if(!bResult){
                			mxLauncher.startActivitySafely(info.intent, null);
                		}
                		//mxLauncher.startActivitySafely(info.intent, null);
                		//end test
                		
//                		mxLauncher.getModel().getUsageStatsMonitor().add(info.intent);
                	}
                	/*RK_ID:RK_SD_APPS zhangdxa 2013-4-15. E***/
                }

            };
            setChildBitmap(info, bitmap, item.cellX, l, mxLauncher);
//            markCellsForView(item.cellX, 1, 1, true);
            /** RK_ID: RK_NEWFEATURE_SHOW_MISSED_NUMBER. AUT: zhanglz1 DATE: 2013-05-06 S */
            showmissed();
            /** RK_ID: RK_NEWFEATURE_SHOW_MISSED_NUMBER. AUT: zhanglz1 DATE: 2013-05-06 E */

        } else if (item instanceof FolderInfo) {
            final FolderInfo info = (FolderInfo) item;
            XFolderIcon newFolder = XFolderIcon.obtain(getXContext(), (XLauncher) getXContext()
                    .getContext(), info);

            // Hide folder title in the hotseat
           // newFolder.setTextVisible(false);
            newFolder.setOnLongClickListener(mxLauncher);

            removeItem(item.cellX);
            addItem(newFolder, item.cellX);
           // newFolder.rmBlackShadow();

//            markCellsForView(item.cellX, 1, 1, true);
        } 
 /*RK_ID:RK_SINGLE_LAYER liuyg1 2013-6-13. S*/
//        else if (item instanceof LeosItemInfo) {
//            LauncherApplication la = (LauncherApplication) mxLauncher.getApplicationContext();
//            Drawable b = la.mLauncherContext.getDrawable(R.drawable.dock_add_button_icon);
//
//            final LeosItemInfo itemInfo = (LeosItemInfo) item;
//            OnClickListener listener = new DrawableItem.OnClickListener() {
//
//                @Override
//                public void onClick(DrawableItem item) {
//                    if (mxLauncher != null) {
//                        itemInfo.intent = getDockAddIntent(itemInfo.cellX, itemInfo.cellY);
//                        mxLauncher.addHotseatShortcutsForLeos(item);
//                    }
//                }
//            };
//            setChildDrawable(item, b, item.cellX, listener, null);
//            markCellsForView(item.cellX, 1, 1, false);
//        } 
 /*RK_ID:RK_SINGLE_LAYER liuyg1 2013-6-13. E*/ 
        showShadow(mIconSize);
    }

	private void showShadow(int size) {
		for (int i = 0; i < getChildCount(); i++) {
			DrawableItem view = getChildAt(i);
			if (view == null) {
				continue;
			}
			if (view instanceof XShortcutIconView) {
					((XShortcutIconView) view).showShadow(size);
			}else if(view instanceof XFolderIcon){
				((XFolderIcon) view).showShadow(size);
			}
		}
	}
	private void setChildBitmap(ItemInfo info, Bitmap bitmap, int index, OnClickListener listener, OnLongClickListener onLongClickListener) {

        Drawable d;
        // change icon size
//        if (bitmap.getWidth() > mIconSize || bitmap.getHeight() > mIconSize) {
//            Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, mIconSize, mIconSize, true);
//            d = new BitmapDrawable(getXContext().getResources(), newBitmap);
//        } else {
            d = new BitmapDrawable(getXContext().getResources(), bitmap);
//        }
Log.d("liuyg123","setChildBitmap"+index);
        setChildDrawable(info, d, index, listener, onLongClickListener);
    }

    public void setChildDrawable(ItemInfo info, Drawable d, int index, OnClickListener listener, OnLongClickListener onLongClickListener) {
    	CHILD_COUNT = getChildCount();
    	if (index >= CHILD_COUNT){
    		RectF rect = getViewRect(info.cellX+1,info.cellX);
    		ShortcutInfo newinfo = (ShortcutInfo)info;
    		XShortcutIconView item = new XShortcutIconView(newinfo, rect, mContext);
            item.setOnLongClickListener(onLongClickListener);
            item.getTextView().setOnClickListener(listener);
            item.getIconDrawable().setOnClickListener(listener);
            item.setTag(info);
    		sFailedADDHotseatItems.add(item);
    		return;
    	}
          

        XShortcutIconView item = null;
        DrawableItem child = getChildAt(index);

//        if (!(child instanceof XShortcutIconView)) {
            removeItem(info.cellX);
            Log.d("liuyg123","setChildDrawable CHILD_COUNT="+CHILD_COUNT);
            Log.d("liuyg123","setChildDrawable index="+index);
            RectF rect = getViewRect(CHILD_COUNT,info.cellX);
            ShortcutInfo newinfo = (ShortcutInfo)info;
            Log.d("liuyg123","setChildDrawable newinfo.title="+newinfo.title);
       	 info.container = LauncherSettings.Favorites.CONTAINER_HOTSEAT;
       	 item = new XShortcutIconView(newinfo, rect, mContext);
//            item = new XPressIconDrawable(getXContext(), null);
            addItem(item, index);
//        } else {
//            item = (XShortcutIconView) child;
//        }
        
        if (item != null) {
            setChildContent(item, d, index);
            item.setOnLongClickListener(onLongClickListener);
            item.getTextView().setOnClickListener(listener);
            item.getIconDrawable().setOnClickListener(listener);
            item.setTag(info);
            Log.d("liuyg123","setChildDrawable"+index);
        }
    }

    private void setChildContent(DrawableItem item, Bitmap bitmap, int index) {
        Drawable d;
        // change icon size
//        if (bitmap.getWidth() > mIconSize || bitmap.getHeight() > mIconSize) {
//            Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, mIconSize, mIconSize, true);
//            d = new BitmapDrawable(getXContext().getResources(), newBitmap);
//        } else {
            d = new BitmapDrawable(getXContext().getResources(), bitmap);
//        }

        setChildContent(item, d, index);
    }

    private void setChildContent(DrawableItem item, Drawable d, int index) {
//        int width = d.getIntrinsicWidth();
//        int height = d.getIntrinsicHeight();

//        width = Math.min(width, mIconSize);
//        height = Math.min(height, mIconSize);
    		float totalHeight = getHeight();
    		if(totalHeight>0){
    			float vSpace = totalHeight - paddingTop - paddingBottom;
    			int toY = (int) ((vSpace - mCellHeight) / 2);
    			mRecHight =(int)totalHeight-toY -paddingTop;
    			if(mRecHight>totalHeight){
    				mRecHight = (int)totalHeight;
    			}
    			Log.d("liuyg12345","setChildContent Item mRecHight "+mRecHight);
    		}else{
    			mRecHight = mCellHeight;
    		}
    		mRecHight = mCellHeight;
        item.resize(new RectF(0, 0, mCellWidth, mRecHight));
//        item.setBackgroundDrawable(d);
        centerItem(index, mCellWidth, mCellHeight);
    }

    void centerItem(int index, int width, int height) {
    	int count = getChildCount();
        if (index >= count||count>max_cellx)
            return;

//        if (width > mIconSize) {
//            Log.w("liuyg123", "centerItem   width == " + width);
//            width = mIconSize;
//        }
//        if (height > mIconSize) {
//            Log.w("liuyg123", "centerItem   height == " + height);
//            height = mIconSize;
//        }

        DrawableItem item = (DrawableItem) getChildAt(index);
		if (item != null) {
			if(hasLocationPrepared && p[count][index]!=null){
    			int x = p[count][index].x;
    			int y = p[count][index].y;
    			item.setRelativeX(x);
    			item.setRelativeY(y);
            }else{
            	int[] location = getRelativeX(count,index, width, height);
            	item.setRelativeX(location[0]);
    			item.setRelativeY(location[1]);
            }
			
			ItemInfo tmpinfo = (ItemInfo) item.getTag();
			if (tmpinfo != null) {
				tmpinfo.cellX = index;
				item.setTag(tmpinfo);
			}
			//Log.d("00", "centerItem left=" + item.getRelativeX() + "==index=" + index);
		}
    }

    public DrawableItem getChildAt(int cellX, int cellY) {
        int index = cellX;
        if (index >= CHILD_COUNT)
            return null;

        return getChildAt(index);
    }
    @Override
    public void onDraw(IDisplayProcess c) {
    	
    /*	RectF rectBottom = new RectF(0, (int)(localRect.height()-bottomShadow.getIntrinsicHeight()), (int)(0+localRect.width()), 
    			(int)localRect.height());
    	c.drawDrawable(bottomShadow, rectBottom);*/
    	
    	// OPT. chengliang
//        for (int i = 0; i < CHILD_COUNT; i++) {
//            DrawableItem item = getChildAt(i);
//            if (item == null || !(item instanceof XShortcutIconView)) {
//                continue;
//            }
//            Drawable bg = item.getBackgroundDrawable();
//
//            if (bg != null) {
////                item.updateFinalAlpha();
////                float alpha = item.getFinalAlpha();
////                bg.setAlpha((int) (255 * alpha));
//            }
//            
//        }
        for (int i = 0; i < mFolderOuterRings.size(); i++) {
            FolderRingAnimator fra = mFolderOuterRings.get(i);

            // Draw outer ring
            Drawable d = FolderRingAnimator.sSharedOuterRingDrawable;
            int width = (int) fra.getOuterRingSize();
            int height = width;
            int[] mTempLocation = new int[2];
//            cellToPoint(fra.mCellX, fra.mCellY, mTempLocation);
            if(fra.mFolderIcon.isPreviewBgVisible()){
            	fra.mFolderIcon.setBackgroundVisible(false);
            }
            mTempLocation[0] = (int)fra.mFolderIcon.getRelativeX();
            mTempLocation[1] = (int)fra.mFolderIcon.getIconDrawable().getRelativeY();
            R5.echo("mTempLocation[0] = " + mTempLocation[0] + "mTempLocation[1] = " + mTempLocation[1]);
            int centerX = mTempLocation[0] + mCellWidth / 2;
            int centerY = mTempLocation[1] + fra.sPreviewSize / 2;
            R5.echo("centerX = " + centerX + "centerY = " + centerY);
//            Log.d("liuyg123456","centerX = " + centerX + "centerY = " + centerY);
            c.save();
            /*RK_ID: RK_FOLDER_ICON . AUT: zhanggx1 . DATE: 2012-02-28 . S*/
//          canvas.translate(centerX - width / 2, centerY - width / 2);
            c.translate(centerX - width / 2, centerY - width / 2);
            /*RK_ID: RK_FOLDER_ICON . AUT: zhanggx1 . DATE: 2012-02-28 . E*/
            d.setBounds(0, 0, width, height);
            d.draw(c.getCanvas());
            c.restore();

            // Draw inner ring
            d = FolderRingAnimator.sSharedInnerRingDrawable;
            width = (int) fra.getInnerRingSize();
            height = width;
//            cellToPoint(fra.mCellX, fra.mCellY, mTempLocation);
//
//            centerX = mTempLocation[0] + mCellWidth / 2;
//            centerY = mTempLocation[1] + mCellHeight / 2;
            c.save();
            /*RK_ID: RK_FOLDER_ICON . AUT: zhanggx1 . DATE: 2012-02-28 . S*/
//            canvas.translate(centerX - width / 2, centerY - width / 2);
            c.translate(centerX - width / 2, centerY - width / 2 );
            /*RK_ID: RK_FOLDER_ICON . AUT: zhanggx1 . DATE: 2012-02-28 . E*/
            d.setBounds(0, 0, width, height);
            d.draw(c.getCanvas());
            c.restore();
        }
        super.onDraw(c);
    }
 /*RK_ID:RK_SINGLE_LAYER liuyg1 2013-6-13. S*/
    public int getCurrentOrientation(){
        return getXContext().getResources().getConfiguration().orientation;
}
 /*RK_ID:RK_SINGLE_LAYER liuyg1 2013-6-13. E*/
    @Override
    public boolean addItem(DrawableItem item, int index) {
    	Log.d("liuyg123","ADD Item "+index);
        boolean res = super.addItem(item, index);
        if(!res){
        	sFailedADDHotseatItems.add(item);
        }
        if (item instanceof XFolderIcon) {
        	final XFolderIcon view = (XFolderIcon) item;
        	view.setShowShadow(true);
        }
//        int width = (int) item.getWidth();
//        if (width == 0) {
//            R5.echo("addItem resize index = " + index);
//            float totalHeight = getHeight();
     //       Log.d("liuyg12345","ADD Item totalHeight= "+totalHeight);
      //      Log.d("liuyg12345","ADD Item mCellHeight= "+mCellHeight);
//			float vSpace = totalHeight - paddingTop - paddingBottom;
//			int toY = (int) ((vSpace - mCellHeight) / 2);
//			mRecHight =(int)totalHeight-toY -paddingTop;
//			if(mRecHight>totalHeight){
//				int iconDrawablePadding = mContext.getResources().getDimensionPixelOffset(R.dimen.app_icon_drawable_padding);
//				mRecHight = (int)totalHeight - iconDrawablePadding;
//			}
//            Log.d("liuyg12345","ADD Item mRecHight "+mRecHight);
//            mRecHight = (int)totalHeight;
            mRecHight = mCellHeight;
            item.resize(new RectF(0, 0, mCellWidth, mRecHight));
//        }

        centerItem(index, (int) item.getWidth(), (int) item.getHeight());
        CHILD_COUNT = getChildCount();
		/*if (item instanceof XFolderIcon) {

			final XFolderIcon view = (XFolderIcon) item;
			view.setTextVisible(true);
			view.setShowShadow(true);
			view.resize(new RectF(view.localRect.left, view.localRect.top,
				                                       view.localRect.right, view.localRect.bottom
				                                       + (mIconSize * 1 * 1.0f) / (7 * 1.0f)));

		}
*/
        return res;
    }

//    @Override
//    public boolean onDown(MotionEvent event) {
//        boolean res = false;
//        XContext context = getXContext();
//        final float x = event.getX();
//        final float y = event.getY();
//
//        for (int i = 0; i < CHILD_COUNT; i++) {
//            DrawableItem item = getChildAt(i);
//            if (item == null) {
//                continue;
//            }
//
//            Object o = item.getTag();
//            if ((o == null || !(o instanceof ShortcutInfo)))
//                continue;
//
//            if (context.getExchangee().checkHited(item, x, y)) {
//                // fix bug 11971
// /*RK_ID:RK_SINGLE_LAYER liuyg1 2013-6-13. S*/
//            //    if (i != XHotseat.sAllAppsButtonRank) {
//                    item.setAlpha(0.6f);
//                    clickItem = item;
//            //    }
//              //  res = true;
// /*RK_ID:RK_SINGLE_LAYER liuyg1 2013-6-13. E*/
//                break;
//            }
//        }
//
//        return super.onDown(event) || res;
//    }
//    
//    DrawableItem clickItem = null;
//    
//    @Override
//    public boolean onFingerUp(MotionEvent e) {
//    	// TODO Auto-generated method stub
//        if (clickItem != null)
//        {
//	        clickItem.setAlpha(1f);
//	    	clickItem = null;
//        }
//    	return super.onFingerUp(e);
//    }

    public void refreshIconStyle(IconCache iconCache, boolean onlyBitmap) {
        for (int i = 0; i < CHILD_COUNT; i++) {
            DrawableItem item = getChildAt(i);
            if (item == null) {
                continue;
            }

            Object o = item.getTag();
            if (o == null || !(o instanceof ItemInfo))
                continue;

            final ItemInfo info = (ItemInfo) o;

            refreshItem(getXContext().getContext(), item, info, iconCache, onlyBitmap);
        }
    }

    public void refreshItem(Context context, DrawableItem target, ItemInfo info,
            IconCache iconCache, boolean onlyBitmap) {

        if (context == null || target == null || info == null) {
            return;
        }

        if (info.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION) {
            final ShortcutInfo sInfo = (ShortcutInfo) info;
            final XIconDrawable view = (XIconDrawable) target;
            if (onlyBitmap)
                sInfo.setIcon(null);
            setChildContent(view, sInfo.getIcon(iconCache, true), sInfo.cellX);
        } else if (info.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT) {
            final ShortcutInfo sInfo = (ShortcutInfo) info;
            final XIconDrawable view = (XIconDrawable) target;
            if (onlyBitmap)
                Utilities.retrieveIcon(context, sInfo);
            setChildContent(view, sInfo.getIcon(iconCache, true), sInfo.cellX);
        }
        /*RK_ID:RK_SD_APPS zhangdxa 2013-4-15. S***/
        else if(info.itemType == LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT){
        	 final ShortcutInfo sInfo = (ShortcutInfo) info;
             final XIconDrawable view = (XIconDrawable) target;
             if (onlyBitmap){
                 //Utilities.retrieveIcon(context, sInfo);
            	 sInfo.setIcon(null);
             }
             Bitmap bitmap = sInfo.getIcon(iconCache, true);
             setChildContent(view, bitmap, sInfo.cellX);
        }
        /*RK_ID:RK_SD_APPS zhangdxa 2013-4-15. E***/
        if (info instanceof FolderInfo) {
            final XFolderIcon view = (XFolderIcon) target;
            XFolder folder = view.mFolder;

            folder.refreshIconCache(iconCache, onlyBitmap);
            view.invalidate();
        }

    }
    
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        boolean isValid = checkRect(e1, e2);
        if (isValid) {
//            switchHotseat(e1, e2);
        }
        return super.onFling(e1, e2, velocityX, velocityY);
    }

    private void switchHotseat(MotionEvent e1, MotionEvent e2) {
        if (mRotateAnimator != null) {
            getXContext().getRenderer().ejectAnimation(mRotateAnimator);
        }

        final int tiv = 400;
        final float centerX = getWidth() / 2.0f; // mChildren.getWidth()
        final float centerY = getHeight() + 0.1f / 2.0f;

        if (e1.getX() < e2.getX()) {
            mRotateAnimator = ValueAnimator.ofInt(-120, 0);
        } else {
            mRotateAnimator = ValueAnimator.ofInt(120, 0);
        }

        mRotateAnimator.setDuration(tiv);
        mRotateAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        mRotateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final int degrees = (Integer) animation.getAnimatedValue();

                final Camera camera = mCamera;
                final Matrix matrix = getMatrix();
                matrix.reset();

                camera.save();
                camera.translate(0.0f, 0.0f, 30f);
                camera.rotateY(degrees);
                camera.translate(0.0f, 0.0f, -30f);
                camera.getMatrix(matrix);
                camera.restore();

                matrix.preTranslate(-centerX, -centerY);
                matrix.postTranslate(centerX, centerY);

                updateMatrix(matrix);
            }
        });

        mRotateAnimator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Matrix m = getMatrix();
                m.reset();
                updateMatrix(m);

//                // databases
//                updateItemInDatabase();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });

        switchHotseatChildren();
        updateDb();
        getXContext().getRenderer().injectAnimation(mRotateAnimator, false);
    }

    private void updateDb() {
        final android.view.View v = mxLauncher.getMainView();

        if (mUpdateDbRunnable != null && v != null) {
            v.removeCallbacks(mUpdateDbRunnable);
        } else {
            mUpdateDbRunnable = new Runnable() {

                @Override
                public void run() {
                    // databases
                    updateItemInDatabase();
                }

            };
        }

        if (v != null) {
            v.postDelayed(mUpdateDbRunnable, 5000);
        }

    }

    @Override
    public void clean() {
        super.clean();
        if (mUpdateDbRunnable != null) {
            if (mxLauncher.getMainView() != null) {
                mxLauncher.getMainView().removeCallbacks(mUpdateDbRunnable);
            }
            mUpdateDbRunnable = null;
        }

    }

    protected void updateItemInDatabase() {
        for (int index = 0; index < CHILD_COUNT; index++) {
                DrawableItem item = getChildAt(index);
                if (item == null)
                    continue;

                Object o = item.getTag();
                if (o instanceof ShortcutInfo || o instanceof FolderInfo)
                    XLauncherModel.updateItemInDatabase(mxLauncher, (ItemInfo) o);
        }
    }

    protected void switchHotseatChildren() {
        for (int index = 0; index < CHILD_COUNT / 2; index++) {
            switchTag(index, CHILD_COUNT - index - 1);
        }
    }

    private void switchTag(int a, int b) {
        DrawableItem itemA = getChildAt(a);
        DrawableItem itemB = getChildAt(b);
        if (itemA == null || itemB == null) {
            return;
        }

        Object oA = itemA.getTag();
        Object oB = itemB.getTag();
        if (!(oA instanceof ItemInfo) || !(oB instanceof ItemInfo)) {
            return;
        }

        final ItemInfo infoA = (ItemInfo) oA;
        final ItemInfo infoB = (ItemInfo) oB;

        int screen = infoA.screen;
        infoA.screen = infoB.screen;
        infoB.screen = screen;

        int cellX = infoA.cellX;
        infoA.cellX = infoB.cellX;
        infoB.cellX = cellX;

        /*** fixbug 11090  . AUT: zhaoxy . DATE: 2013-04-24. START***/
//        IconCache iconCache = ((LauncherApplication) mxLauncher.getApplicationContext())
//                .getIconCache();
//        bindInfo(infoA, iconCache);
//        bindInfo(infoB, iconCache);
        exchangeChildren(a, b);
        centerItem(a, (int) itemB.getWidth(), (int) itemB.getHeight());
        centerItem(b, (int) itemA.getWidth(), (int) itemA.getHeight());
//        markCellsForView(infoA.cellX, 1, 1, !(infoA instanceof LeosItemInfo));
//        markCellsForView(infoB.cellX, 1, 1, !(infoB instanceof LeosItemInfo));
        /*** fixbug 11090  . AUT: zhaoxy . DATE: 2013-04-24. END***/
    }

    private boolean checkRect(MotionEvent e1, MotionEvent e2) {
    	if (e1 == null || e2 == null) {
    		return false;
    	}
        // v direction should be in this rect
        float top = getParent().localRect.top;
        float bottom = top + this.getWidth();
        float y1 = e1.getY();
        float y2 = e2.getY();

        // horizontal direction should be larger than this cell width
        int width = mCellWidth;
        int height = mCellHeight;

        return y1 > top && y1 < bottom && y2 > top && y2 < bottom && Math.abs(y1 - y2) < height
                && Math.abs(e1.getX() - e2.getX()) > width;
    }

    // ///////////////////////////////////////////
    // interface from ics
    // ///////////////////////////////////////////

    /**
     * Create an intent of an add icon in the cell(cellX, cellY), that contains the forwarded activity information
     * @author zhanggx1
     * @date 2011-12-15
     * @param cellX
     * @param cellY
     * @return Intent
     */
 /*RK_ID:RK_SINGLE_LAYER liuyg1 2013-6-13. S*/
//    Intent getDockAddIntent(int cellX, int cellY) {
//        Intent intent = new Intent("android.intent.action.PICK_SHORTCUT");
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        //bugfix 12633
//        ArrayList<String> addedPackageAndClassName = getAddedHotseatPackageAndClassName(cellX, cellY);
//        intent.putStringArrayListExtra("APPCLASSNAME", addedPackageAndClassName);
//        intent.putExtra("CELL_X", cellX);
//        intent.putExtra("CELL_Y", cellY);
//        return intent;
//    }
 /*RK_ID:RK_SINGLE_LAYER liuyg1 2013-6-13. E*/

    /**
     * Get the names list of classes which has been in the Hotseat area.
     * @author zhanggx1
     * @date 2011-12-16
     * @param cellX
     * @param cellY
     * @return ArrayList<String>
     */
    private ArrayList<String> getAddedHotseatPackageAndClassName(int cellX, int cellY) {
        ArrayList<String> packageAndClassNameList = new ArrayList<String>();
        ArrayList<ShortcutInfo> contents = null;
        Object obj;
        DrawableItem view;
        Intent intent;
        for (int i = 0; i < CHILD_COUNT; i++) {
            for (int k = 0; k < 1; k++) {
                // exclude the current clicked item
                if (i == cellX && k == cellY) {
                    continue;
                }
                view = getChildAt(i, k);
                if (view == null || view.getTag() == null) {
                    continue;
                }
                obj = view.getTag();
                if (obj instanceof ShortcutInfo) {
                    intent = ((ShortcutInfo) obj).intent;
                    if (intent != null && intent.getComponent() != null) {
                        packageAndClassNameList.add(intent.getComponent().getPackageName()+
                        		intent.getComponent().getClassName());
                    }
                } else if (obj instanceof FolderInfo) {
                    contents = ((FolderInfo) obj).contents;
                    // collect all the shortcut info in the folder
                    if (contents != null && contents.size() > 0) {
                        for (ShortcutInfo s : contents) {
                            intent = ((ShortcutInfo) s).intent;
                            if (intent != null && intent.getComponent() != null) {
                                packageAndClassNameList.add(intent.getComponent().getPackageName()+
                                		intent.getComponent().getClassName());
                            }
                        }
                    }
                } else {
                    continue;
                }
            }
        }
        return packageAndClassNameList;
    }
 /*RK_ID:RK_SINGLE_LAYER liuyg1 2013-6-13. S*/
//    public int findNearestCellIndex(int pixelX,int pixelY,int[] result){
//        final int[] bestXY = result != null ? result : new int[2];
//        for (int i = 0; i < getChildCount(); i++) {
//            DrawableItem item = (DrawableItem) getChildAt(i);
//            if (item != null) {
//                if(item.localRect.contains(pixelX,pixelY)){
//                    bestXY[0] = i;
//                }
////                Log.d("liuyg123","findNearestCellIndex item.localRect====="+item.localRect);
//            }
//        }
//        return -1;
//    }
 /*RK_ID:RK_SINGLE_LAYER liuyg1 2013-6-13. E*/
//    int[] findNearestArea(int pixelX, int pixelY, int minSpanX, int minSpanY, int spanX, int spanY,
//            View ignoreView, boolean ignoreOccupied, int[] result, int[] resultSpan,
//            boolean[][] occupied) {
//        lazyInitTempRectStack();
//        occupied = mOccupied;    
//        Log.d("liuyg123","findNearestArea  mOccupied[0]=="+mOccupied[0].length+" mOccupied ===="+mOccupied.length);
//        // mark space take by ignoreView as available (method checks if ignoreView is null)
////        markCellsAsUnoccupiedForView(ignoreView, occupied);
// /*RK_ID:RK_SINGLE_LAYER liuyg1 2013-6-13. S*/
//        for (int i = 0; i < getChildCount(); i++) {
//            DrawableItem item = (DrawableItem) getChildAt(i);
//            if (item != null) {
////                Log.d("liuyg123"," findNearestArea x====="+item.getGlobalX()+" x2===="+getGlobalX2()+" rect====="+item.localRect+" relatviex="+item.getRelativeX());
//            }
//        }
// /*RK_ID:RK_SINGLE_LAYER liuyg1 2013-6-13. E*/
//
//        // For items with a spanX / spanY > 1, the passed in point (pixelX, pixelY) corresponds
//        // to the center of the item, but we are searching based on the top-left cell, so
//        // we translate the point over to correspond to the top-left.
//        pixelX -= (mCellWidth + mWidthGap) * (spanX - 1) / 2f;
//        pixelY -= (mCellHeight) * (spanY - 1) / 2f;
//
//        // Keep track of best-scoring drop area
//        final int[] bestXY = result != null ? result : new int[2];
//        double bestDistance = Double.MAX_VALUE;
//        final Rect bestRect = new Rect(-1, -1, -1, -1);
//        final Stack<Rect> validRegions = new Stack<Rect>();
//
//        final int countX = getChildCount();
//        final int countY = 1;
//        if(countX==0){
//        	bestXY[0] = 0;
//        	bestXY[1] = 0;
//        	return bestXY;
//}
//        if (minSpanX <= 0 || minSpanY <= 0 || spanX <= 0 || spanY <= 0 ||
//                spanX < minSpanX || spanY < minSpanY) {
//        	Log.d("liuyg123","findNearestArea bestxy");
//            return bestXY;
//        }
//
//        for (int y = 0; y < countY - (minSpanY - 1); y++) {
//            inner:
//            for (int x = 0; x < countX - (minSpanX - 1); x++) {
//                int ySize = -1;
//                int xSize = -1;
//                if (ignoreOccupied) {
//                    // First, let's see if this thing fits anywhere
//                    for (int i = 0; i < minSpanX; i++) {
//                        for (int j = 0; j < minSpanY; j++) {
//                            if (occupied[x + i][y + j]) {
//                                continue inner;
//                            }
//                        }
//                    }
//                    xSize = minSpanX;
//                    ySize = minSpanY;
//
//                    // We know that the item will fit at _some_ acceptable size, now let's see
//                    // how big we can make it. We'll alternate between incrementing x and y spans
//                    // until we hit a limit.
//                    boolean incX = true;
//                    boolean hitMaxX = xSize >= spanX;
//                    boolean hitMaxY = ySize >= spanY;
//                    while (!(hitMaxX && hitMaxY)) {
//                        if (incX && !hitMaxX) {
//                            for (int j = 0; j < ySize; j++) {
//                                if (x + xSize > countX -1 || occupied[x + xSize][y + j]) {
//                                    // We can't move out horizontally
//                                    hitMaxX = true;
//                                }
//                            }
//                            if (!hitMaxX) {
//                                xSize++;
//                            }
//                        } else if (!hitMaxY) {
//                            for (int i = 0; i < xSize; i++) {
//                                if (y + ySize > countY - 1 || occupied[x + i][y + ySize]) {
//                                    // We can't move out vertically
//                                    hitMaxY = true;
//                                }
//                            }
//                            if (!hitMaxY) {
//                                ySize++;
//                            }
//                        }
//                        hitMaxX |= xSize >= spanX;
//                        hitMaxY |= ySize >= spanY;
//                        incX = !incX;
//                    }
//                    incX = true;
//                    hitMaxX = xSize >= spanX;
//                    hitMaxY = ySize >= spanY;
//                }
//                final int[] cellXY = mTmpXY;
//                cellToCenterPoint(x, y, cellXY);
//
//                // We verify that the current rect is not a sub-rect of any of our previous
//                // candidates. In this case, the current rect is disqualified in favour of the
//                // containing rect.
//                if(mTempRectStack == null || mTempRectStack.isEmpty() ){
//                	 bestXY[0] = -1;
//                     bestXY[1] = -1;
//                }else {
//                Rect currentRect = mTempRectStack.pop();
//                currentRect.set(x, y, x + xSize, y + ySize);
//                boolean contained = false;
//                for (Rect r : validRegions) {
//                    if (r.contains(currentRect)) {
//                        contained = true;
//                        break;
//                    }
//                }
//                validRegions.push(currentRect);
//                double distance = Math.sqrt(Math.pow(cellXY[0] - pixelX, 2)
//                        + Math.pow(cellXY[1] - pixelY, 2));
//
//                if ((distance <= bestDistance && !contained) ||
//                        currentRect.contains(bestRect)) {
//                    bestDistance = distance;
//                    bestXY[0] = x;
//                    bestXY[1] = y;
//                    if (resultSpan != null) {
//                        resultSpan[0] = xSize;
//                        resultSpan[1] = ySize;
//                    }
//                    bestRect.set(currentRect);
//                }
//                }
//            }
//        }
//        // re-mark space taken by ignoreView as occupied
////        markCellsAsOccupiedForView(ignoreView, occupied);
//
//        // Return -1, -1 if no suitable location found
//        if (bestDistance == Double.MAX_VALUE) {
//            bestXY[0] = -1;
//            bestXY[1] = -1;
//        }
//        recycleTempRects(validRegions);
//        return bestXY;
//    }
    
    private final Stack<Rect> mTempRectStack = new Stack<Rect>();
    private void lazyInitTempRectStack() {
    	CHILD_COUNT = getChildCount();
        if (mTempRectStack.isEmpty()) {
            for (int i = 0; i < CHILD_COUNT; i++) {
                mTempRectStack.push(new Rect());
            }
        }
    }

    private void recycleTempRects(Stack<Rect> used) {
        while (!used.isEmpty()) {
            mTempRectStack.push(used.pop());
        }
    }
    
    private final int[] mTmpXY = new int[2];
    
    void cellToCenterPoint(int cellX, int cellY, int[] result) {
        regionToCenterPoint(cellX, cellY, 1, 1, result);
    }
    
    void regionToCenterPoint(int cellX, int cellY, int spanX, int spanY, int[] result) {
        final int hStartPadding = (int)getRelativeX();//getPaddingLeft();
        final int vStartPadding = (int)getRelativeY();//getPaddingTop();
        result[0] = hStartPadding + cellX * (mCellWidth + mWidthGap[CHILD_COUNT]) +
                (spanX * mCellWidth + (spanX - 1) * mWidthGap[CHILD_COUNT]) / 2;
        result[1] = vStartPadding + cellY * (mCellHeight + 0) +
                (spanY * mCellHeight + (spanY - 1) * 0) / 2;
    }
    
    public boolean[][] mOccupied; /*= new boolean[CHILD_COUNT][1];*/
    
//    public void markCellsForView(int cellX, int spanX, int spanY, boolean value) {
//    	Log.d("liuyg1234","markCellsForView");
//    	CHILD_COUNT = getChildCount();
//        if (cellX < 0) return;
//        mOccupied = new boolean[CHILD_COUNT][1];
//        for (int x = cellX; x < cellX + spanX && x < CHILD_COUNT; x++) {
//                mOccupied[x][0] = value;
//        }
//    }

    protected void checkAndAddShortcut(HashSet<ComponentName> componentNames,
            ArrayList<DrawableItem> childrenToRemove,boolean byClassname) {
        for (int i = 0; i < CHILD_COUNT; i++) {
            DrawableItem item = getChildAt(i);
            if (item == null) {
                continue;
            }

            Object tag = item.getTag();
            checkAndAddShortcut(tag, item, componentNames, childrenToRemove,byClassname);
        }

    }
    private void checkAndAddShortcut(Object tag, DrawableItem item,HashSet<ComponentName> ComponentNames,
            ArrayList<DrawableItem> childrenToRemove,boolean byClassname) {
	 if(byClassname){
		 checkAndAddShortcutByClassname(tag,item,ComponentNames,childrenToRemove);
	 }else{
		 HashSet<String> packages = new HashSet<String>();
         for (ComponentName componentName : ComponentNames) {
       	  packages.add(componentName.getPackageName());
         }
		 checkAndAddShortcut(tag,item,packages,childrenToRemove);
	 }
 
 }
    private void checkAndAddShortcutByClassname(Object tag, DrawableItem item,
			HashSet<ComponentName> componentNames,
			ArrayList<DrawableItem> childrenToRemove) {
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
                             XLauncherModel.deleteItemFromDatabase(mxLauncher, info);
                             childrenToRemove.add(item);
                         }
                     }
				} else if (info.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION) {
					for (ComponentName componentName : componentNames) {
						if (componentName.equals(name)) {
							XLauncherModel.deleteItemFromDatabase(mxLauncher,
									info);
							childrenToRemove.add(item);
						}
					}
				}
			}
             // we need delete it if action is Intent.ACTION_VIEW,
             // because if action is null, when added to workspace it was set to
             // this value.
             else if (Intent.ACTION_VIEW.equals(intent.getAction())
                     && info.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT
                     && name != null) {
                 Log.i("00", "this shortcut info's action is Intent.ACTION_VIEW, ComponentName = "
                         + name);
                 HashSet<String> packages = new HashSet<String>();
                 for (ComponentName componentName : componentNames) {
               	 	packages.add(componentName.getPackageName());
                 }
                 for (String packageName : packages) {
                     if (packageName.equals(name.getPackageName())) {
                         XLauncherModel.deleteItemFromDatabase(mxLauncher, info);
                         childrenToRemove.add(item);
                     }
                 }
             }
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
                 XLauncherModel.deleteItemFromDatabase(mxLauncher, info);
                 childrenToRemove.add(item);
             }

             for (ShortcutInfo folderItem : appsToRemoveFromFolder) {
                 info.remove(folderItem);
                 XLauncherModel.deleteItemFromDatabase(mxLauncher, folderItem);
             }
         }		
	}
	protected void checkAndAddShortcut(Object tag, DrawableItem item, HashSet<String> packages,
            ArrayList<DrawableItem> childrenToRemove) {
        if (tag instanceof ShortcutInfo) {
            final ShortcutInfo info = (ShortcutInfo) tag;
            final Intent intent = info.intent;
            final ComponentName name = intent.getComponent();

            if (Intent.ACTION_MAIN.equals(intent.getAction()) && name != null) {
                for (String packageName : packages) {
                    if (packageName.equals(name.getPackageName())) {
                        XLauncherModel.deleteItemFromDatabase(mxLauncher, info);
                        childrenToRemove.add(item);
                    }
                }
            }
            // we need delete it if action is Intent.ACTION_VIEW,
            // because if action is null, when added to workspace it was set to
            // this value.
            else if (Intent.ACTION_VIEW.equals(intent.getAction())
                    && info.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT
                    && name != null) {
                Log.i("liuyg123", "this shortcut info's action is Intent.ACTION_VIEW, ComponentName = "
                        + name);
                for (String packageName : packages) {
                    if (packageName.equals(name.getPackageName())) {
                        XLauncherModel.deleteItemFromDatabase(mxLauncher, info);
                        childrenToRemove.add(item);
                    }
                }
            }
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
                XLauncherModel.deleteItemFromDatabase(mxLauncher, info);
                childrenToRemove.add(item);
            }

            for (ShortcutInfo folderItem : appsToRemoveFromFolder) {
                info.remove(folderItem);
                XLauncherModel.deleteItemFromDatabase(mxLauncher, folderItem);
            }
        }
    }
    void bindDragFolder(XFolderIcon icon, FolderInfo fInfo) {
        icon.reuse();
        icon.resetPressedState();
        icon.getIconDrawable().getMatrix().reset();
        //changed by zhanglz1 for folder shadow in hotseat
        icon.resize(new RectF(0, 0, mCellWidth, mCellHeight));
        icon.updateInfoContanier(fInfo);
//        icon.setTextVisible(true);
        // Hide folder title in the hotseat
//        icon.setTextVisible(false);
        icon.setOnLongClickListener(mxLauncher);
//        removeItem(fInfo.cellX);
        icon.rmBlackShadow();
        addItem(icon, fInfo.cellX);
//        markCellsForView(fInfo.cellX, 1, 1, true);
        showShadow(mIconSize);
    }

    void switchDragFolder(XFolderIcon view, int[] temp, FolderInfo fInfo) {
        // must remove first
        removeViewWithoutClean(temp[0], view);

        view.resetPressedState();
        view.setVisibility(true);

        removeItem(fInfo.cellX);
        addItem(view, fInfo.cellX);
//        markCellsForView(fInfo.cellX, 1, 1, true);
    }

    private void removeViewWithoutClean(int index, DrawableItem cell) {
        removeItemWithoutClean(index);
        if (index >= 0) {
        	
        	  RectF rect = getViewRect(getChildCount(),index);
              ShortcutInfo newinfo = (ShortcutInfo)cell.getTag();
              newinfo.container = LauncherSettings.Favorites.CONTAINER_HOTSEAT;
         	 DrawableItem item = new XShortcutIconView(newinfo, rect, mContext);
//            XIconDrawable icon = new XPressIconDrawable(getXContext(), null);
            addItem(item, index);
        }

        if (cell != null && cell.getTag() instanceof ItemInfo){
            mxLauncher.removePopupWindow((ItemInfo) cell.getTag());
        }
    }

//    @Override
//    public void onTouchCancel() {
//        super.onTouchCancel();
//        if (clickItem != null)
//        {
//	        clickItem.setAlpha(1f);
//	    	clickItem = null;
//        }
//        resetPressedState();
//    }
//
//    @Override
//    public void resetPressedState() {
//        super.resetPressedState();
//
//        for (int i = 0; i < CHILD_COUNT; i++) {
//            DrawableItem item = getChildAt(i);
//            if (item != null) {
//                item.resetPressedState();
//            }
//        }
//    }
    /** RK_ID: RK_NEWFEATURE_SHOW_MISSED_NUMBER. AUT: zhanglz1 DATE: 2013-05-06 S */
    private class ShowmissedTask extends AsyncTask<Void, Void, Integer> {
		@Override
		protected Integer doInBackground(Void... arg0) {	
			try {
				Utilities.getMissedNumFirsttime((XLauncher) getXContext().getContext());
				return 0;
			} catch (Exception e) {
				// TODO: handle exception
				return 0;
			}
			
		}
		
		@Override
	    protected void onPostExecute(Integer result) {
			for (int i = 0; i < CHILD_COUNT; i++) {
				DrawableItem item = getChildAt(i);
				if (item == null || !(item instanceof XShortcutIconView)) {
					continue;
				}
				Object oi = item.getTag();
				if (oi instanceof ItemInfo) {
					final ItemInfo info = (ItemInfo) oi;
					if (Utilities.getShowMissedNum(info, Utilities.HAS_NEW_MSG,
							Utilities.missedNum[Utilities.HAS_NEW_MSG]) > 0) {// 有未读短信
						((XShortcutIconView) item)
								.showTipForNewAdded(Utilities.missedNum[Utilities.HAS_NEW_MSG]);
					} else if (Utilities.getShowMissedNum(info,
							Utilities.HAS_NEW_CALL,
							Utilities.missedNum[Utilities.HAS_NEW_CALL]) > 0) {// 有未接来电
						((XShortcutIconView) item)
								.showTipForNewAdded(Utilities.missedNum[Utilities.HAS_NEW_CALL]);
					}else{
						((XShortcutIconView) item).dismissTip();
					}
				}
			}
		}
	}
	public void showmissed() {
		new ShowmissedTask().execute(new Void[]{});

	}
    /** RK_ID: RK_NEWFEATURE_SHOW_MISSED_NUMBER. AUT: zhanglz1 DATE: 2013-05-06 E */
	public int[] getInfoFromIndex(int cellIndex, int[] res) {
		if (res == null || res.length < 3) {
			res = new int[3];
		}

		int sum = CHILD_COUNT * 1;
		res[0] = cellIndex / sum;
		res[1] = cellIndex % CHILD_COUNT;
		res[2] = (cellIndex % sum) / CHILD_COUNT;

		return res;

	}
	public int[] getRelativeXY(int cellX, int cellY, int[] loc) {
	    if (loc == null) {
	        loc = new int[2];
        }
	    loc[0] = (int) (cellX * mCellWidth - getWidth());
	    loc[1] = cellY * mCellHeight;
	    return loc;
	}
/*RK_ID:RK_SINGLE_LAYER zhanglz 2013-6-26. S*/
/*RK_ID:RK_SINGLE_LAYER liuyg1 2013-6-26. S*/
//	public int[] findTargetArea(int touchX, int touchY) {
//		final int[] bestXY = new int[2];
//		CHILD_COUNT = getChildCount();
//		if(CHILD_COUNT==0){
//			bestXY[0] = 0;
//			bestXY[1] = 0;
//			return bestXY;
//		}
//		 Resources res = getXContext().getResources();
// 		int wkPaddingLeft = res.getDimensionPixelSize(
//                 R.dimen.workspace_screen_padding_left);
// 		int wkPaddingRight = res.getDimensionPixelSize(
//                 R.dimen.workspace_screen_padding_right);
//        int paddingLeft = res.getDimensionPixelSize(R.dimen.workspace_pageview_left_padding);
//        int paddingRight = res.getDimensionPixelSize(R.dimen.workspace_pageview_left_padding);
//        int with = (int)getWidth() - wkPaddingLeft -wkPaddingRight -paddingLeft-paddingRight;
//        int gap = with/CHILD_COUNT;
//       // Log.d("liuyg1234", "gap"+gap);
//        for (int index = 0; index < CHILD_COUNT; index++) {
//               int x = (index+1)*gap+wkPaddingLeft+paddingLeft;
//               if(x>=touchX){
//            	   bestXY[0]=index;
//            	   break;
//               }
//        }
//        
////		if(bestXY[0]==CHILD_COUNT){
////			bestXY[0] = CHILD_COUNT-1;
////		}
//		//Log.d("liuyg123", "findTargetArea bestx"+bestXY[0]);
//		return bestXY;
//	}
	public int findTargetIndex(int touchX, int targetX) {
		int target = targetX;

		DrawableItem item = getChildAt(targetX);
		if (item == null) {
			return targetX;
		}
		if(touchX>(item.getRelativeX()+mCellWidth/2.0f)){
			target = targetX+1;
		}

		//Log.d("liuyg123", "findTargetIndex"+target);
		return target;
	}

/*RK_ID:RK_SINGLE_LAYER liuyg1 2013-6-26. E*/

	public Point[][] p;
    private boolean hasLocationPrepared =false;
    private int lastOcupation = Configuration.ORIENTATION_PORTRAIT;
    private int lastWith = 0;
   
	public void locationPrepare() {
		Configuration config = mContext.getResources().getConfiguration();
		if(hasLocationPrepared&&SettingsValue.getCurrentMachineType( mxLauncher ) == -1){
			return;
		}
		int thisWith = (int)getWidth();
		if (!hasLocationPrepared||lastOcupation!=config.orientation||lastWith!=thisWith) {
			Log.d("liuyg123","!hasLocationPrepared||lastOcupation!=config.orientation ||lastWith!=thisWith");
			lastOcupation = config.orientation;
			lastWith = thisWith;
//			if(getChildCount()<=0){
//				Log.d("liuyg123","locationPrepare() return");
//				return;
//			}
			 int width=mCellWidth;
			 int height=mCellHeight;
//			for (int i = 0; i < getChildCount(); i++) {
//				DrawableItem item = (DrawableItem) getChildAt(i);
//				if (item == null) {
//					continue;
//				}
//				Drawable bg = item.getBackgroundDrawable();
//
//	            if (bg == null) {
//	                width = (int)item.getWidth();
//	                height = (int)item.getHeight();
//	            } else{
//	                width = bg.getIntrinsicWidth();
//	                height = bg.getIntrinsicHeight();
//	            }
//	            break;
//			}
			
			p = new Point[max_cellx + 1][max_cellx + 1];
			for (int j = 0; j <= max_cellx; j++) {// 总数
				for (int i = 0; i < j; i++) {// 第几个
					int[] location = getRelativeX(j,i, width, height);
					p[j][i] = new Point(location[0], location[1]);
					/*Log.i("00", "==cellcount==" + j + "==index=" + i
							+ "==relativeX=" + location[0] + "relativeY="
							+ location[1]);*/
				}
			}
			hasLocationPrepared = true;
		}

	}
	public int[] findTargetCell(int childcount,int touchX, int touchY) {
		final int[] bestXY = new int[2];
		//childcount = getChildCount();
		bestXY[0] = 0;
		bestXY[1] = 0;
		if(childcount<=1){
			return bestXY;
		}
		if(childcount>max_cellx){
			bestXY[0] = max_cellx;
			return bestXY;
		}
		locationPrepare();
//		 Resources res = getXContext().getResources();
//		int wkPaddingLeft = res.getDimensionPixelSize(
//                R.dimen.workspace_screen_padding_left);
//		int wkPaddingRight = res.getDimensionPixelSize(
//                R.dimen.workspace_screen_padding_right);
//       int paddingLeft = res.getDimensionPixelSize(R.dimen.workspace_pageview_left_padding);
//       int paddingRight = res.getDimensionPixelSize(R.dimen.workspace_pageview_left_padding);
//       int with = (int)getWidth() - wkPaddingLeft -wkPaddingRight -paddingLeft-paddingRight;
//        int gap = with/childcount;
		
		int gap = p[childcount][1].x-p[childcount][0].x-mIconSize;
//		 Log.d("liuyg12345","gap="+gap);
        for (int index = 0; index < childcount; index++) {
//               int x = (index+1)*gap+wkPaddingLeft+paddingLeft;
//               if(x>=touchX){
//            	   bestXY[0]=index;
//            	   break;
//               }
 //       	Log.d("liuyg12345","mCellWidth="+mCellWidth);
   //     	Log.d("liuyg12345","mIconSize="+mIconSize);
        	
       	 Log.d("liuyg12345","p[childcount][index].x="+p[childcount][index].x+"index="+index);
        	int leftx = p[childcount][index].x-gap/2;
        	int rightx = p[childcount][index].x +mIconSize+ gap/2;
      //  	 Log.d("liuyg12345","leftx="+leftx);
       // 	 Log.d("liuyg12345","rightx="+rightx);
        	if(index == 0){
        		if(touchX<leftx){
        			bestXY[0] = index;
        			break;
        		}
        	}
        	if(index == childcount-1){
        		if(touchX>rightx){
        			bestXY[0] = index;
        			break;
        		}
        	}
        	if( touchX >= leftx&&touchX<=rightx){
         	   bestXY[0] = index;
         	   break;	
        	}

        }
  //  	   Log.d("liuyg12345","bestX="+bestXY[0]);
		return bestXY;
	}
	public boolean isEnd = false;
	public boolean animateChildToPosition(final DrawableItem childAt, final Point point,
			int duration, int delay) {
		if(childAt == null) return false;
		if(point == null) return false;
		isEnd =false; 
		// TODO Auto-generated method stub
		final float deltaX = point.x - childAt.getRelativeX();
		final float deltaY = point.y - childAt.getRelativeY();
		

		final ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
		anim.setDuration(duration);
		anim.setStartDelay(delay);
		anim.setInterpolator(new AccelerateDecelerateInterpolator());
		anim.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
				childAt.setRelativeX(point.x);
				childAt.setRelativeY(point.y);
				childAt.getMatrix().postTranslate(-deltaX, -deltaY);
			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {
            	isEnd = true;
			}

			@Override
			public void onAnimationCancel(Animator animation) {
            	isEnd = true;

			}
		});

		anim.addUpdateListener(new AnimatorUpdateListener() {

			private float cursorLast = 0f;

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				final float value = (Float) animation.getAnimatedValue();
				final float step = value - cursorLast;

				final float stepX = deltaX * step;
				final float stepY = deltaY * step;
            	//Log.i("00", "=step ="+step);

				childAt.getMatrix().postTranslate(stepX, stepY);
				//fromCell.updateMatrix();
				cursorLast = (Float) animation.getAnimatedValue();
			}
		});

		getXContext().getRenderer().injectAnimation(anim, false);
		return isEnd;
	}
	public void setChildren(ArrayList<DrawableItem> temps) {
		// TODO Auto-generated method stub
		int length = Math.min(temps.size(), getChildCount());
		for (int i = 0; i < length; i++) {
			addItemWithoutFilter(temps.get(i),i);
		}
	}
   private int[] getRelativeX(int count,int index, int width, int height) {
    //	CHILD_COUNT = getChildCount();
	   //Log.i("00", "=====index==="+index+"===count="+count);
	   int pageGap = 0;
	   if(mxLauncher!=null&&mxLauncher.getWorkspace()!=null&&mxLauncher.getWorkspace().getPagedView()!=null){
//		   pageGap = mxLauncher.getWorkspace().getPagedView().getPageGap();
		     int paddingPhone = mContext.getResources().getDimensionPixelSize( R.dimen.workspace_divider_padding_left);
		        int paddingPad = getXContext().getResources().getDimensionPixelOffset(R.dimen.workspace_pageview_left_padding);
		        if( phoneindex != -1 ){
		        	pageGap = paddingPad * 2;
		        }else{
		        	pageGap = paddingPhone * 2;
		        }
		   
//		   Log.d("liuyg","pageGap===================="+pageGap);
		  
	   }
	   if (index >= count){
		   return null;
	   }
        int toX; 

//        if (width > mIconSize) {
//          //  Log.w("liuyg", "getRelativeX   width == " + width);
//            width = mIconSize;
//        }
//        if (height > mIconSize) {
//          //  Log.w("liuyg", "getRelativeX   height == " + height);
//            height = mIconSize;
//        }

            Resources res = getXContext().getResources();
            int CellCount = XLauncherModel.getCellCountX();
        if(CellCount ==0)CellCount =4;
            int occupyWith ;
            occupyWith = (int)getWidth() -pageGap;
            int cellWith = occupyWith/CellCount;
            occupyWith = occupyWith*count/CellCount;
            int cellgap = (cellWith- mCellWidth)/2;
            int edgeWith = (int) ((getWidth()-occupyWith)/2);
            edgeWith = edgeWith + cellgap;
//            occupyWith = (int)getWidth() -2*edgeWith;
//            if(count>1){
//            	mWidthGap[count] = (int) ((occupyWith - mCellWidth * (count)) / (count -1));
//            	mMinWidthGap[count] = (int) ((occupyWith - mCellWidth * (count)) / (count +1));
//            }
//            int toX = edgeWith + index * (mCellWidth + mWidthGap[count]) 
//            		+ mCellPaddingLeft
//                    + drawablePadding;
//            if(count==1){
//            	toX = ((int)getWidth()-mIconSize)/2;
//            }else{
            	toX= edgeWith + index * cellWith; 
//            }
            int paddingTop = res.getDimensionPixelSize(R.dimen.button_bar_height_top_padding);
            int paddingBottom = res.getDimensionPixelSize(R.dimen.button_bar_height_bottom_padding);
    		float totalHeight = getHeight();
    		if(totalHeight>0){
    			float vSpace = totalHeight - paddingTop - paddingBottom;
    			int toY = (int) ((vSpace - mCellHeight) / 2);
    			mRecHight =(int)totalHeight-toY -paddingTop;
    			if(mRecHight>totalHeight){
    				int iconDrawablePadding = mContext.getResources().getDimensionPixelOffset(R.dimen.app_icon_drawable_padding);
    				mRecHight = (int)totalHeight - iconDrawablePadding;
    			}
    		}else{
    			mRecHight = mCellHeight;
    		}
           
//            Log.d("liuyg123","mCellHeight ="+mCellHeight);
        	int[] location = new int[2];
    		location[0] = toX;
    		location[1] = 0;
//    		 Log.d("liuyg123","location[1] ="+location[1]);
    		return location;
   }

	public int getIconSize() {
		// TODO Auto-generated method stub
		return mIconSize;
	}
	public void setHasLocationPrepared(boolean b) {
		// TODO Auto-generated method stub
		hasLocationPrepared = b;
	}
    private ArrayList<FolderRingAnimator> mFolderOuterRings = new ArrayList<FolderRingAnimator>();

	private int mRecHight;
    
    public void showFolderAccept(FolderRingAnimator fra) {
        mFolderOuterRings.add(fra);
    }

    public void hideFolderAccept(FolderRingAnimator fra) {
        if (mFolderOuterRings.contains(fra)) {
            mFolderOuterRings.remove(fra);
        }
        invalidate();
    }
	public RectF getViewRect(int count,int cellX) {
		Log.d("liuyg123","count ="+count+"cellx = "+cellX);
//		Log.d("liuyg123","p="+p);
		locationPrepare();
//		if(mxLauncher!=null&&mxLauncher.getWorkspace()!=null&&mxLauncher.getWorkspace().getPagedView()!=null){
//        mCellWidth = mxLauncher.getWorkspace().getPagedView().getCellWidth();
//        mCellHeight = mxLauncher.getWorkspace().getPagedView().getCellHeight();
       
//		}
		if(count>max_cellx){
			count = max_cellx;
		}
		
        float left = p[count][cellX].x;
        float top = p[count][cellX].y;
//		float totalHeight = getHeight();
//		mRecHight = (int)getHeight();
        mRecHight = mCellHeight;
//		if(totalHeight>mCellHeight){
//			float vSpace = totalHeight - paddingTop - paddingBottom;
//			int toY = (int) ((vSpace - mCellHeight) / 2);
//			mRecHight =(int)vSpace-toY -paddingTop;
//		}else{
//			mRecHight = mCellHeight;
//		}
        RectF rect = new RectF(left, top, left + mCellWidth, top + mRecHight);
		return rect;
	}
	public ArrayList<FolderRingAnimator> getFolderOuterRings() {
		// TODO Auto-generated method stub
		return mFolderOuterRings;
	}
	
    public void clearNewBgAndSetNum(String componentName, int num){
    	int count = getChildCount();
    	ItemInfo info;
    	for (int i = 0; i < count; i++) {
			DrawableItem item = (DrawableItem) getChildAt(i);
			if (item == null) {
				continue;
			}
			
			if (item instanceof XShortcutIconView)
			{
				info = (ItemInfo)item.getTag();
				if (info instanceof ShortcutInfo)
	            {
	                ShortcutInfo shortcutInfo = (ShortcutInfo)info;                
	                
	                ComponentName component = shortcutInfo.intent.getComponent();
	                if (component != null && componentName.equals(component.flattenToString()))
	                {
	                    shortcutInfo.updateInfo(num);
	                    ((XShortcutIconView)item).showTipForNewAdded(shortcutInfo.mNewString);
	                                    
	                    invalidate();	                        
	                }                
	                
	            }
			}
    	}
    }
}