package com.lenovo.launcher.components.XAllAppFace;

import java.util.ArrayList;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;


import com.lenovo.launcher.components.XAllAppFace.XDropTarget.XDragObject;
import com.lenovo.launcher.components.XAllAppFace.slimengine.BaseDrawableGroup;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;
import com.lenovo.launcher2.commoninterface.ItemInfo;
import com.lenovo.launcher2.commoninterface.LauncherSettings;
import com.lenovo.launcher2.commonui.LauncherAppWidgetHostView;
import com.lenovo.launcher2.customizer.SettingsValue;

public class XDragLayer extends BaseDrawableGroup {
    
    private static final String TAG = "XDragLayer";
    private XContext mContext;
    private XDragController mXDragController;
    
    final float[] coordTemp = new float[2];
    private RectF mHitRect = new RectF();
    
    /*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-5-30. S***/
    private int mXDown, mYDown;
    // Variables relating to resizing widgets
    private final ArrayList<AppWidgetResizeFrame> mResizeFrames =
            new ArrayList<AppWidgetResizeFrame>();
    private AppWidgetResizeFrame mCurrentResizeFrame;
    /*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-5-30. E***/
    
    private DrawableItem mDropView = null;
    private int mPage;

    private int[] mDropViewPos = new int[2];
    private float mDropViewScale;
    private float mDropViewAlpha;

    private int mDropViewWidth;
    private int mDropViewHeight;
    private int mDropViewRelativeY;

    // Variables relating to animation of views after drop
    private ValueAnimator mDropAnim = null;
    private static final int ADJACENT_SCREEN_DROP_DURATION = 300;
    private static final int ICON_INTO_FOLDER_DELAY = 100;
    private static final int ICON_INTO_FOLDER_DURATION = 200;

    private ValueAnimator mPendulumAnimIcon = null;
    private final float A_LARGE = 8.0f;
    private float mPendulumLastValue = 0f;
    private int mPendulumPage = -1;

    private final int HOTSEAT_DELAY = 400;
    private int mAnimPendulumDelay;

    public XDragLayer(XContext context, RectF region) {
        super(context);
        this.mContext = context;
        this.resize(region);
    }
    
    public void setup(XDragController controller) {
        mXDragController = controller;
    }

    /**
     * 求viewItem左上角在draglayer中的相对坐标
     * @param viewItem
     * @param loc
     */
    public void getLocationInDragLayer(DrawableItem viewItem, int[] loc) {
        loc[0] = (int) viewItem.getRelativeX();
        loc[1] = (int) viewItem.getRelativeY();
        getDescendantCoordRelativeToSelf(viewItem, loc);
    }

    /**
     * 求descendant局部坐标系中的坐标coord在draglayer中的相对坐标
     *
     * @param descendant The descendant to which the passed coordinate is relative.
     * @param coord The coordinate that we want mapped.
     */
    public void getDescendantCoordRelativeToSelf(DrawableItem descendant, int[] coord) {
        coordTemp[0] = coord[0];
        coordTemp[1] = coord[1];
        Matrix mGlobalMatrix = new Matrix();
        descendant.getInvertMatrix().invert(mGlobalMatrix);
        mGlobalMatrix.mapPoints(coordTemp);
        coord[0] = (int) coordTemp[0];
        coord[1] = (int) coordTemp[1];
    }
    
    /**
     * Determine the rect of the descendant in this DragLayer's coordinates
     *
     * @param descendant The descendant whose coordinates we want to find.
     * @param r The rect into which to place the results.
     * @return The factor by which this descendant is scaled relative to this DragLayer.
     */
    public void getDescendantRectRelativeToSelf(DrawableItem descendant, RectF r) {
        int[] coord = new int[2];
        coord[0] = (int) descendant.getRelativeX();
        coord[1] = (int) descendant.getRelativeY();
        getDescendantCoordRelativeToSelf(descendant, coord);
        r.set(coord[0], coord[1],
                coord[0] + descendant.getWidth(), coord[1] + descendant.getHeight());
    }
    
    private boolean isEventOverFolder(XFolder folder, MotionEvent ev) {
        getDescendantRectRelativeToSelf(folder, mHitRect);
        if (mHitRect.contains((int) ev.getX(), (int) ev.getY())) {
            return true;
        }
        return false;
    }
    
    private boolean interceptTouch = false;
    
    @Override
    public boolean onDown(MotionEvent e) {
        /*PK_ID: Remove Out of Folder Toucher Area AUTH:GECN1 DATE:2013-05-27 S*/
//        RectF hitRect = new RectF();
//        XFolder currentFolder = ((XLauncherView) getXContext()).getWorkspace().getOpenFolder();
//        if (currentFolder != null) {
//            getDescendantRectRelativeToSelf(currentFolder, hitRect);
////            if (!isEventOverFolder(currentFolder, e)) {
////                Log.d("click", "--------------------onDown");
////                ((XLauncher) getXContext().getContext()).closeFolder();
////                interceptTouch = true;
////                return true;
////            }
//        }
        /*PK_ID: Remove Out of Folder Toucher Area AUTH:GECN1 DATE:2013-05-27 E*/
    	
    	/*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-5-30. S***/
        int x = (int) e.getX();
        int y = (int) e.getY();
        for (AppWidgetResizeFrame child: mResizeFrames) {
        	if (mContext.getExchangee().checkHited(child, x, y)) {
        		Log.i("zdx1","\nXDragLayer.onDown()-----------x: "+x+" , y: "+y);
                if (child.beginResizeIfPointInRegion(x - (int)child.getRelativeX(), y - (int)child.getRelativeY())) {
                	mCurrentResizeFrame = child;
                    mXDown = x;
                    mYDown = y;
                    //requestDisallowInterceptTouchEvent(true);
                    return true;
                }
            }
        }
        clearAllResizeFrames();
        /*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-5-30. E***/
        
        if (mXDragController.onDown(e)) {
            return true;
        }
        return super.onDown(e);
    }
    
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (interceptTouch) {
            interceptTouch = false;
            return true;
        }
        return super.onSingleTapUp(e);
    }
    
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY, float previousX, float previousY) {
        if (interceptTouch) {
            interceptTouch = false;
            return true;
        }
        
        /*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-5-30. S***/
        if (mCurrentResizeFrame != null) {
            int x1 = (int) e1.getX();
            int y1 = (int) e1.getY();
            int x2 = (int) e2.getX();
            int y2 = (int) e2.getY();
            Log.i("zdx1","\n XDragLayer.onScroll*******x1:"+ x1+", y1:"+ y1 +", x2:"+ x2 +", y2:"+y2);
            mCurrentResizeFrame.visualizeResizeForDelta(x2 - x1, y2 - y1);
            return true;
        }
        /*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-5-30. E***/
        
        if (mXDragController.onScroll(e1, e2, distanceX, distanceY)) {
            return true;
        }
        return super.onScroll(e1, e2, distanceX, distanceY, previousX, previousY);
}
    
    @Override
    public boolean onFingerUp(MotionEvent e) {
        /*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-5-30. S***/
        if (mCurrentResizeFrame != null) {
            int x = (int) e.getX();
            int y = (int) e.getY();            
            Log.i("zdx1","\n XDragLayer.onFingerUp----x:"+ x +", y:"+ y);
            mCurrentResizeFrame.commitResizeForDelta(x - mXDown, y - mYDown);
            mCurrentResizeFrame = null;
            return true;
        }
        /*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-5-30. E***/
        
        if (mXDragController.onFingerUp(e)) {
            return true;
        }
        return super.onFingerUp(e);
    }
    
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (interceptTouch) {
            interceptTouch = false;
            return true;
        }
        
        /*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-5-30. S***/
        if (mCurrentResizeFrame != null) {
            int x1 = (int) e1.getX();
            int y1 = (int) e1.getY();
            int x2 = (int) e2.getX();
            int y2 = (int) e2.getY();
            Log.i("zdx1","\n XDragLayer.onFling*******x1:"+ x1+", y1:"+ y1 +", x2:"+ x2 +", y2:"+y2);
            mCurrentResizeFrame.visualizeResizeForDelta(x2 - x1, y2 - y1);
            return true;
        }
        /*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-5-30. E***/
        
    	if (mXDragController.isDragging()) {
            return true;
        }
        return super.onFling(e1, e2, velocityX, velocityY);
    }
    
    @Override
    public void onTouchCancel( MotionEvent e ) {
        mXDragController.onTouchCancel();
        /*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-5-30. S***/
        Log.i("zdx1","\n XDragLayer.onTouchCancel");
        clearAllResizeFrames();
        /*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-5-30. E***/
        super.onTouchCancel( e );
    }
    
    public void checkDragViewToFront() {
        final int count = getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            DrawableItem item = getChildAt(i);
            if (item instanceof XDragView) {
                bringChildToFront(item);
                return;
            }
        }
    }

    public void clearDragView() {
        final int count = getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            DrawableItem item = getChildAt(i);
            if (item instanceof XDragView) {
                ((XDragView) item).remove();
            }
        }
    }
    public void resizeDragLayer(){
        XLauncher l = (XLauncher) getXContext().getContext();
        WindowManager wm = (WindowManager)l
                   .getSystemService(Context.WINDOW_SERVICE);
        Display dl = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        dl.getMetrics(metrics);

        RectF rect =  new RectF(0, 0, metrics.widthPixels, metrics.heightPixels);
//        android.util.Log.i("Test", "widthPixesl===== " + metrics.widthPixels+" height="+metrics.heightPixels);
        super.resize(rect);

        //final boolean flag = l.isCurrentWindowFullScreen();

        int size = getChildCount();
        for (int index = 0; index < size; index++) {
            DrawableItem item = getChildAt(index);
                item.resize(rect);
        }
    }

    @Override
    public void resize(RectF rect) {
//        android.util.Log.i("Test", "old ===== " + localRect.right + " , " + localRect.bottom);
//        android.util.Log.i("Test", "resize ===== " + rect.right + " , " + rect.bottom);
        super.resize(rect);

        XLauncher l = (XLauncher) getXContext().getContext();
        final boolean flag = l.isCurrentWindowFullScreen();
        
        //add by zhanggx1 for new layout.s
        final boolean hasExtraTopMargin = SettingsValue.hasExtraTopMargin();
        //final int extraTopMargin = SettingsValue.getExtraTopMargin();
        //add by zhanggx1 for new layout.e

        int size = getChildCount();
        for (int index = 0; index < size; index++) {
            DrawableItem item = getChildAt(index);
            if (item instanceof XDragView) {
                RectF r = item.localRect;
                r.offset(0, (flag || hasExtraTopMargin) ? l.getStatusBarHeight() : 0);
            } else if (item instanceof XFolder) {
                float y = 0;
                float h = rect.height();
                if ((flag || hasExtraTopMargin)) {
                    y = l.getStatusBarHeight();
                    h -= l.getStatusBarHeight();
                }
                RectF r = item.localRect;
                r.set(0, y, rect.width(), h);
                item.resize(r);
            } else if (item instanceof XBlackboard) {
                item.resize(rect);
            } else if (item instanceof AppWidgetResizeFrame){
            	Log.i("zdx1","XDragLayer.resize, AppWidgetResizeFrame");
            	AppWidgetResizeFrame appWidgetResizeFrame = (AppWidgetResizeFrame)item;
            	appWidgetResizeFrame.snapToWidget(false);
            }
        } // end for
//        if (mDropView != null) {
//            Log.i(TAG, "NOTICE====NOTICE===" + flag);
            mDropViewRelativeY = ((!flag && !hasExtraTopMargin) ? -l.getStatusBarHeight() : 0);
//        }
    }

    void bringDragViewToFront() {
        int size = getChildCount();
        for (int index = 0; index < size; index++) {
            DrawableItem item = getChildAt(index);
            if (item instanceof XDragView) {
                bringChildToFront(item);
                break;
            }
        }
    }

    void cleanupFolderMsg(long container) {
        if (container == LauncherSettings.Favorites.CONTAINER_DESKTOP
                || container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
            return;
        }
        XLauncher launcher = (XLauncher) getXContext().getContext();
        launcher.getWorkspace().cleanupAddToFolder();
    }
    
    /*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-5-30. S***/
    public static class LayoutParams extends FrameLayout.LayoutParams {
        public int x, y;
        public boolean customPosition = false;

        /**
         * {@inheritDoc}
         */
        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getWidth() {
            return width;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getHeight() {
            return height;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getX() {
            return x;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getY() {
            return y;
        }
    }

    public void clearAllResizeFrames() {
    	mCurrentResizeFrame = null;
        if (mResizeFrames.size() > 0) {
        	Log.i("zdx1","XDragLayer.clearAllResizeFrames()---count:"+mResizeFrames.size());
            for (AppWidgetResizeFrame frame: mResizeFrames) {
                removeItem(frame);
            }
            mResizeFrames.clear();
            invalidate();
        }        
    }
    public void addResizeFrame(XLauncher xLauncher ,ItemInfo itemInfo, LauncherAppWidgetHostView widgetView) {
    	Log.i("zdx1","XDragLayer.addResizeFrame******");
        AppWidgetResizeFrame resizeFrame = new AppWidgetResizeFrame(mContext, 
        		xLauncher, this, itemInfo, widgetView);
        addItem(resizeFrame);
        mResizeFrames.add(resizeFrame);
        resizeFrame.snapToWidget(false);
    }

    @Override
    public boolean onFingerCancel(MotionEvent e) {
    	Log.i("zdx1","XDragLayer.onFingerCancel");
        if (mCurrentResizeFrame != null) {
            int x = (int) e.getX();
            int y = (int) e.getY();            
            mCurrentResizeFrame.commitResizeForDelta(x - mXDown, y - mYDown);
            mCurrentResizeFrame = null;
            return true;
        }
        super.onFingerCancel(e);
        return true;
    }
    @Override
   	public boolean onLongPress(MotionEvent e) {
        if (mCurrentResizeFrame != null) {
           	Log.i("zdx1","\nXDragLayer.onLongPress********mCurrentResizeFrame != null");
           	int x = (int) e.getX();
            int y = (int) e.getY();            
            mCurrentResizeFrame.visualizeResizeForDelta(x - mXDown, y - mYDown);
            return true;
        }
       	Log.i("zdx1","XDragLayer.onLongPress*******");
        return super.onLongPress(e);
   	}
    /*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-5-30. E***/
    
    /**
     * 求descendant局部坐标系中的坐标coord在draglayer中的相对坐标
     *
     * @param descendant The descendant to which the passed coordinate is relative.
     * @param coord The coordinate that we want mapped.
     */
    public void getDescendantCoordRelativeToSelf(float[] coord, DrawableItem descendant) {
        coordTemp[0] = coord[0];
        coordTemp[1] = coord[1];
        Matrix mGlobalMatrix = new Matrix();
        descendant.getInvertMatrix().invert(mGlobalMatrix);
        mGlobalMatrix.mapPoints(coordTemp);
        coord[0] = coordTemp[0];
        coord[1] = coordTemp[1];
    }

    /**
     * for drag view back to home (workspace or hotseat)
     */
    public void animateViewIntoPosition(XDragView dragView, final DrawableItem item, int duration,
            final Runnable onFinishAnimationRunnable, int padding, float scale) {
        int[] loc = new int[2];
        getLocationInDragLayer(dragView, loc);
        Log.i(TAG, "from === (" + loc[0] + " , " + loc[1] + " )");

        int[] target = new int[2];
        getLocationInDragLayer(item, target);
        final DrawableItem source = item.getParent() != null ? item.getParent().getParent() : null;

        retireveXCoord(item, source, padding, target);

        Log.i(TAG, "target === (" + target[0] + " , " + target[1] + " )");

        mDropViewPos[0] = loc[0];
        mDropViewPos[1] = loc[1];
        mDropViewScale = dragView.getScale();
        mDropViewAlpha = dragView.getAlpha();

        mDropViewWidth = (int) item.getWidth();
        mDropViewHeight = (int) item.getHeight();

        item.setVisibility(false);
        item.setAlpha(0f);

        if (duration == 0) {
            duration = ADJACENT_SCREEN_DROP_DURATION;
        }

        Runnable onCompleteRunnable = new Runnable() {
            public void run() {
                item.setVisibility(true);
                if (source instanceof XWorkspace)
                {
                	((XWorkspace)source).getPagedView().generateBitmapCacheAll();
                }
                // hotseat alpha animate didn't work.
                int duration = /*item.getParent() instanceof XHotseatCellLayout ? 0 : 6*/0;

                ValueAnimator oa = ValueAnimator.ofFloat(0.0f, 1.0f);
                oa.setDuration(duration);
                oa.addListener(new android.animation.AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(android.animation.Animator animation) {
                        if (onFinishAnimationRunnable != null) {
                            onFinishAnimationRunnable.run();
                        }
                        Log.i(TAG, " alpha anim END ... ");

                        mDropView = null;
                        mPage = -1;
                    }
                });
                oa.addUpdateListener(new android.animation.ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator a) {
                        Float alpha = (Float) a.getAnimatedValue();
                        Log.i(TAG, "animateViewIntoPosition~~~  alpha === " + alpha);
                        item.setAlpha(alpha);
                        if (item.getParent() != null) {
                            item.getParent().invalidate();
                        }
                    }
                });
                oa.start();
            }
        };
        animateViewIntoPosition(dragView, loc[0], loc[1], target[0], target[1], scale / mDropViewScale,
                onCompleteRunnable, duration);

    }

    private void retireveXCoord(DrawableItem item, DrawableItem source, int padding, int[] target) {
        if (padding != 0) {
            // this is not current page.
            // so matrix is not correct, we calculate x-coordinate it again.
            target[0] = (int) (getLocationXInDragLayer(item) + padding);
        } else {
            XLauncher launcher = (XLauncher) getXContext().getContext();
            XFolder f = launcher.getAnimateFolder();

            if (f != null && source instanceof XWorkspace) {
                // when folder close animation is running, workspace matrix is not current either,
                // so cannot get x-coordinate by use matrix.
                Log.i(TAG, "folder is animating, re-caculate x-coordinate.");
                target[0] = (int) (getLocationXInDragLayer(item) + this.getWidth());
            } else if (target[0] < 0 && source instanceof XWorkspace) {
                // bug 422, matrix is not reliable.
                if (item instanceof XCell) {
                    ItemInfo info = (ItemInfo) ((XCell) item).getDrawingTarget().getTag();
                    if (info.screen == launcher.getWorkspace().getCurrentPage()) {
                        Log.i(TAG, "retrieve x-coordinate ." + target[0]);
                        target[0] = (int) (getLocationXInDragLayer(item) + this.getWidth());
                    }
                }
            } else if (source instanceof XFolder && item instanceof XCell) {
                f = (XFolder) source;
                float minRelativeX = f.getKuang().localRect.left;
                ItemInfo info = (ItemInfo) ((XCell) item).getDrawingTarget().getTag();
                if (target[0] < minRelativeX && info.screen == f.getPagedView().getCurrentPage()) {
                    Log.i(TAG, "xfolder anim back,  x-coordinate error ." + target[0]);
                    target[0] = (int) (getLocationXInDragLayer(item) + f.getPagedView().getWidth());
                }
            }
        }
    }

    private void animateViewIntoPosition(final XDragView dragView, final int fromX, final int fromY,
            final int toX, final int toY, final float finalScale, final Runnable onCompleteRunnable,
            int duration) {
        if (mDropAnim != null) {
            mDropAnim.cancel();
        }
        mDropAnim = new ValueAnimator();
        mDropAnim.setInterpolator(new DecelerateInterpolator(1.5f));

        mDropAnim.setDuration(duration);
        mDropAnim.setFloatValues(0.0f, 1.0f);
        mDropAnim.removeAllUpdateListeners();
        mDropAnim.addUpdateListener(new android.animation.ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                final float percent = (Float) animation.getAnimatedValue();

                mDropViewPos[0] = fromX + (int) Math.round(((toX - fromX) * percent));
                mDropViewPos[1] = fromY + (int) Math.round(((toY - fromY) * percent));
//                Log.v(TAG, "mDropViewPos === (" + mDropViewPos[0] + " , " + mDropViewPos[1] + " )");
                mDropViewScale = finalScale * percent + (1 - percent);
//                Log.v(TAG, "mDropViewScale ===" + mDropViewScale);
                mDropViewAlpha = 1.0f * percent + dragView.getAlpha() * (1 - percent);

                if (mAnimPendulumDelay > 0) {
//                    Log.d(TAG, "current translate time ==" + animation.getCurrentPlayTime());
                    mAnimPendulumDelay -= animation.getCurrentPlayTime();
                }

                XDragLayer.this.invalidate();
            }
        });
        mDropAnim.addListener(new android.animation.AnimatorListenerAdapter() {
            public void onAnimationEnd(android.animation.Animator animation) {
                if (onCompleteRunnable != null) {
                    onCompleteRunnable.run();
                }
            }

            @Override
            public void onAnimationStart(android.animation.Animator animation) {
                mDropView = dragView;
                XDragLayer.this.removeItem(dragView);
                mDropView.setRelativeX(0f);
                mDropView.setRelativeY(0f);
            }
        });
        mDropAnim.start();
    }

    /**
     * for drag view back to a closed folder.
     */
    private void animateViewIntoFolder(final XDragView dragView, final XFolderIcon folder, int duration,
            final Runnable runnable, final XCell item, final int padding, final int folderPadding, final int iconPaddingTop) {
        int[] loc = new int[2];
        getLocationInDragLayer(dragView, loc);
        Log.i(TAG, "from === (" + loc[0] + " , " + loc[1] + " )");

        final int[] target = new int[2];
        getLocationInDragLayer(folder, target);

        XLauncher launcher = (XLauncher) getXContext().getContext();
        final boolean scaleAnim = folder.mInfo.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT
                || folder.mInfo.screen == launcher.getWorkspace().getCurrentPage();

        // folder matrix is not correct, we calculate x/y-coordinate it not use matrix.
        target[0] = (int) (getLocationXInDragLayer(folder) + folderPadding);
        target[1] = (int) getLocationYInDragLayer(folder);

        if (folder.mInfo.container == LauncherSettings.Favorites.CONTAINER_DESKTOP
                && folder.mInfo.screen == launcher.getWorkspace().getCurrentPage()) {
            // folder is in current page of workspace.
            target[0] += getWidth();
        }

        // because drag view width > folder width, and height
        Log.d(TAG, "dragView width = " + dragView.getWidth() / dragView.getScale());
        target[0] -= (dragView.getWidth() / dragView.getScale() - folder.getWidth()) / 2;
        target[1] -= iconPaddingTop;
        if (folder.mInfo.container == LauncherSettings.Favorites.CONTAINER_DESKTOP) {
            target[1] += XShortcutIconView.getIconPaddingTop();
        } else if (folder.mInfo.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
            target[1] += XShortcutIconView.getHotseatIconPaddingTop();
        }

        Log.i(TAG, "target === (" + target[0] + " , " + target[1] + " )");

        mDropViewPos[0] = loc[0];
        mDropViewPos[1] = loc[1];
        mDropViewScale = dragView.getScale();
        mDropViewAlpha = dragView.getAlpha();

        mDropViewWidth = (int) dragView.getWidth();
        mDropViewHeight = (int) dragView.getHeight();

        if (duration == 0) {
            duration = ADJACENT_SCREEN_DROP_DURATION;
        }

        Runnable onCompleteRunnable = new Runnable() {
            public void run() {
                item.setVisibility(true);
                folder.invalidate();

                if (!scaleAnim) {
                    Log.i(TAG, "folderPadding != 0, is not current page .. ");
                    if (runnable != null) {
                        runnable.run();
                    }

                    mDropView = null;
                    mPage = -1;
                    mAnimPendulumDelay = 0;
                    return;
                }

                startFolderScaleAnim(item, folder, padding, dragView, iconPaddingTop, runnable, target);
            }
        };
        animateViewIntoPosition(dragView, loc[0], loc[1], target[0], target[1], 1.0f / mDropViewScale,
                onCompleteRunnable, duration);
    }

    private void startFolderScaleAnim(XCell item, XFolderIcon folder, int padding, XDragView dragView,
            int iconPaddingTop, final Runnable runnable, final int[] target) {
        // icon final end coordinate.
        final int[] end = new int[2];
        getLocationWithExtMatrix(item, end);
        final float foldScale = folder.getFolderScale();
        end[0] += padding * foldScale;

        // we keep icon the same position.
        end[0] -= (dragView.getWidth() / dragView.getScale() - item.getWidth()) * foldScale / 2;
        end[1] -= (iconPaddingTop - XShortcutIconView.getFolderIconPaddingTop()) * foldScale;

        Log.i(TAG, "end === (" + end[0] + " , " + end[1] + " )");

        final float finalScale = foldScale / dragView.getScale();
        final float initScale = 1.0f / dragView.getScale();

        ValueAnimator oa = ValueAnimator.ofFloat(0.0f, 1.0f);
        oa.setDuration(ICON_INTO_FOLDER_DURATION);
        // wait folder close animate end.
        oa.setStartDelay(ICON_INTO_FOLDER_DELAY);
        oa.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                if (runnable != null) {
                    runnable.run();
                }

                mDropView = null;
                mPage = -1;
                mAnimPendulumDelay = 0;
            }
        });
        oa.addUpdateListener(new android.animation.ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator a) {
                final float percent = (Float) a.getAnimatedValue();

                mDropViewPos[0] = target[0] + (int) Math.round(((end[0] - target[0]) * percent));
                mDropViewPos[1] = target[1] + (int) Math.round(((end[1] - target[1]) * percent));

                mDropViewScale = percent * finalScale + (1 - percent) * initScale;
//                Log.v(TAG, "animateViewIntoFolder~~~  mDropViewScale === " + mDropViewScale);
                XDragLayer.this.invalidate();
            }
        });
        oa.start();
    }

    private void getLocationWithExtMatrix(XCell viewItem, int[] loc) {
        loc[0] = (int) viewItem.getRelativeX();
        loc[1] = (int) viewItem.getRelativeY();

        coordTemp[0] = loc[0];
        coordTemp[1] = loc[1];
        Matrix extM = new Matrix();
        viewItem.getExtraEffectMatrix().invert(extM);

        Matrix m = viewItem.getInvertMatrix();
        m.postConcat(extM);

        Matrix globalMatrix = new Matrix();
        m.invert(globalMatrix);

        globalMatrix.mapPoints(coordTemp);
        loc[0] = (int) coordTemp[0];
        loc[1] = (int) coordTemp[1];
    }

    private float getLocationXInDragLayer(DrawableItem view) {
        float relativeX = view.getRelativeX();
        DrawableItem viewParent = view.getParent();
        while (viewParent != null && viewParent != this) {
            final DrawableItem parent = viewParent;
            relativeX += parent.getRelativeX();
            viewParent = parent.getParent();
        }
        return relativeX;
    }

    private float getLocationYInDragLayer(DrawableItem view) {
        float relativeY = view.getRelativeY();
        DrawableItem viewParent = view.getParent();
        while (viewParent != null && viewParent != this) {
            final DrawableItem parent = viewParent;
            relativeY += parent.getRelativeY();
            viewParent = parent.getParent();
        }
        return relativeY;
    }

    @Override
    public void onDraw(IDisplayProcess c) {
        super.onDraw(c);

        if (mDropView != null) {
            XContext xcontext = getXContext();
            if (xcontext instanceof XLauncherView) {
                XLauncherView view = (XLauncherView) getXContext();
                XWorkspace xworkspace = view.getWorkspace();
                if (xworkspace != null && xworkspace.getCurrentPage() != mPage) {
                    Log.e(TAG, "workspace snap, not draw dropView..");
                    return;
                }
            }

            c.save();
            c.translate(mDropViewPos[0], mDropViewPos[1] + mDropViewRelativeY);
//            Log.v(TAG, "draw~~~  mDropViewPos[0] === " + mDropViewPos[0]);
            c.translate((mDropViewScale - 1) * mDropViewWidth / 2, (mDropViewScale - 1) * mDropViewHeight / 2);
//            Log.v(TAG, "draw~~~  (mDropViewScale - 1) * mDropViewWidth / 2 === " + (mDropViewScale - 1) * mDropViewWidth / 2);

            mDropView.setScaleX(mDropViewScale);
            mDropView.setScaleY(mDropViewScale);

            mDropView.setAlpha(mDropViewAlpha);
            mDropView.draw(c);
            c.restore();
        }
    }

    public void initPageBeforeAnim() {
        XContext xcontext = getXContext();
        if (xcontext instanceof XLauncherView) {
            XLauncherView view = (XLauncherView) getXContext();
            XWorkspace xworkspace = view.getWorkspace();
            if (xworkspace != null) {
                mPage = xworkspace.getCurrentPage();
            }
        }

    }

    /**
     * drop an icon to position.
     * @param container The icon's original container. folder, hotseat and workspace.
     */
    public void animDropIntoPosition(final XDragView dragView, final DrawableItem item, int screen,
            DrawableItem source, long container) {
        if (dragView == null || !dragView.hasDrawn()) {
            return;
        }

        int w = 0;
        initPageBeforeAnim();

        if (source instanceof XWorkspace) {
            final XPagedView xPagedView = ((XWorkspace) source).getPagedView();

            int currentScreen = xPagedView.getCurrentPage();
            if (screen != currentScreen) {
                w = (int) (xPagedView.getWidth() * (screen - currentScreen) + getWidth());
            }
            final int width = w;

            getXContext().post(new Runnable() {

                @Override
                public void run() {
                    animateViewIntoPosition(dragView, item, 0, null, width, 1.0f);
                }
            });

        } else if (source instanceof XHotseat) {
            getXContext().post(new Runnable() {

                @Override
                public void run() {
                    animateViewIntoPosition(dragView, item, 0, null, 0, 1.0f);
                }
            });

        } else if (source instanceof XFolder) {
            XFolder folder = (XFolder) source;
            final XFolderIcon folderIcon = folder.getXFolderIcon();
            final XCell cell = (XCell) item;

            int folderPadding = 0;
            boolean scaleAnim = true;
            if (folder.mInfo.container == LauncherSettings.Favorites.CONTAINER_DESKTOP) {
                XLauncher xlauncher = (XLauncher) getXContext().getContext();
                int folderScreen = folder.mInfo.screen;
                int currentPage = xlauncher.getWorkspace().getCurrentPage();

                if (folderScreen != currentPage) {
                    folderPadding = (int) ((folderScreen - currentPage + 1) * getWidth());
                    scaleAnim = false;
                }
            }

            final int top = getIconPaddingTopByContainer(container);
            mAnimPendulumDelay = ADJACENT_SCREEN_DROP_DURATION
                    + (scaleAnim ? ICON_INTO_FOLDER_DURATION + ICON_INTO_FOLDER_DELAY : 0);
            Log.i(TAG, "mAnimPendulumDelay =====" + mAnimPendulumDelay);
            final int p = folderPadding;

            getXContext().post(new Runnable() {

                @Override
                public void run() {
                    animateViewIntoFolder(dragView, folderIcon, 0, null, cell, 0, p, top);
                }
            });
        }

    }

    private int getIconPaddingTopByContainer(long container) {
        int top = 0;

        if (container == LauncherSettings.Favorites.CONTAINER_DESKTOP) {
            top = (int) XShortcutIconView.getIconPaddingTop();
        } else if (container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
            top = (int) XShortcutIconView.getHotseatIconPaddingTop();
        } else {
            top = (int) XShortcutIconView.getFolderIconPaddingTop();
        }

        return top;
    }

    public void showPendulumAnim(XDragObject dragInfo, DrawableItem target) {
        if (!(target instanceof XWorkspace || target instanceof XHotseat)) {
            return;
        }

        XLauncher launcher = (XLauncher) getXContext().getContext();
        if (launcher != null) {
            XFolder animateFolder = launcher.getAnimateFolder();
            if (animateFolder != null) {
                animateFolder.stopAnim();
            }
        }

        int delay = Math.max(mAnimPendulumDelay, 0);
        if (dragInfo.dragSource instanceof XHotseat || target instanceof XHotseat) {
//            delay += HOTSEAT_DELAY;
        } else if (dragInfo.dragSource instanceof XScreenContentTabHost) {
            delay += HOTSEAT_DELAY;
        }
        Log.i(TAG, "pendulum delay =====" + delay);

        mPendulumAnimIcon = initPendulumAnim(dragInfo, mPendulumAnimIcon, 750, delay, true);
        getXContext().getRenderer().injectAnimation(mPendulumAnimIcon, false);
    }

    public void cancelPendulumAnim() {
        if (mPendulumAnimIcon != null && mPendulumAnimIcon.isStarted()) {
            Log.i(TAG, "end pendulum delay anim ~~~ ");
            mPendulumAnimIcon.end();
        }
    }

    private ValueAnimator initPendulumAnim(final Object dragInfo, ValueAnimator anim, long duration,
            int delay, final boolean isIcon) {
        if (anim != null) {
            getXContext().getRenderer().ejectAnimation(anim);
        }

        XLauncher launcher = (XLauncher) getXContext().getContext();
        final XWorkspace workspace = launcher.getWorkspace();

//        final int screen = workspace.getCurrentPage();
        final int freq = 3;
        final float decay = 2.5f;

        anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setDuration(duration);
        anim.setStartDelay(delay);
        anim.setInterpolator(new LinearInterpolator());
        anim.removeAllUpdateListeners();
        anim.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float time = (Float) animation.getAnimatedValue();
                float rotation = (float) (Math.sin(freq * time * Math.PI) / Math.exp(decay * time));
//                Log.i(TAG, "PendulumAnim ~~~ rotation~~~" + rotation + "mPendulumLastValue = "
//                        + mPendulumLastValue);
                float dy = animOtherViews(workspace.getPagedView(), mPendulumPage, rotation - mPendulumLastValue,
                        isIcon, null);
                animHotseatView(rotation, isIcon, null, dy);

                mPendulumLastValue = rotation;
            }
        });

        anim.removeAllListeners();
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator anim) {
            	animEnd = false;
                Log.i(TAG, "PendulumAnim ~~~ START~~~" + isIcon);

                // set page touchable false
                mPendulumPage = workspace.getCurrentPage();
                workspace.getPagedView().noNeedGenerateBitmapCache(mPendulumPage);
                workspace.setTouchable(false);
                
            }

            @Override
            public void onAnimationRepeat(Animator anim) {
            }

            @Override
            public void onAnimationEnd(Animator anim) {
            	animEnd = true;
                Log.i(TAG, "PendulumAnim ~~~ END~~~" + isIcon);
                float dy = animOtherViews(workspace.getPagedView(), mPendulumPage, -mPendulumLastValue, isIcon, null);
                animHotseatView(0f, isIcon, null, dy);

                // restore page touchable true
                workspace.getPagedView().generateBitmapCacheAll();
                workspace.setTouchable(true);
                mPendulumLastValue = 0;
                
            }

            @Override
            public void onAnimationCancel(Animator anim) {
                Log.i(TAG, "PendulumAnim ~~~ CANCEL~~~" + isIcon);
            }
        });

        return anim;
    }

    private void animHotseatView(float rotation, boolean isIcon, Object dragInfo, float dy) {
        if (isIcon) {
            XLauncher launcher = (XLauncher) getXContext().getContext();
            launcher.animHotseatView(A_LARGE * rotation, dragInfo, dy);
        }
    }

    private float animOtherViews(XPagedView xPagedView, int screen, float value, boolean isIcon,
            Object dragInfo) {
        float dy = 0f;
        if (xPagedView != null) {
            int cellX = xPagedView.getCellCountX();
            int cellY = xPagedView.getCellCountY();

            for (int j = 0; j < cellY; j++) {
                for (int i = 0; i < cellX; i++) {
                    XPagedViewItem item = xPagedView.findPageItemAt(screen, i, j);
                    if (item == null) {
                        continue;
                    }

                    DrawableItem target = item.getDrawingTarget();
                    if (target == null) {
                        continue;
                    }

                    ItemInfo info = item.getInfo();
                    int spanX = info.spanX;
                    int spanY = info.spanY;

                    if (spanX == 1 && spanY == 1) {
                        dy = animSmallOne(value + mPendulumLastValue, target, isIcon);
                    } else if (isIcon && info.cellX == i && info.cellY == j) {
                        dy = animLargeOne(value, target, info);
                    }
                }// end for i
            } // end for j
        } // end for xPagedView
        return dy;
    }

    private float animSmallOne(float degrees, DrawableItem target, boolean isIcon) {
        DrawableItem item = target;
        if (item == null) {
            return 0f;
        }

        Matrix m = item.getMatrix();
        m.reset();
        m.setRotate(A_LARGE * degrees, target.localRect.centerX(), target.getHeight() / 10f);
        item.updateMatrix(m);
        return target.getHeight() / 10f;
    }
    
    private boolean animEnd = false;

    private float animLargeOne(final float degrees, final DrawableItem target, ItemInfo info) {
        if (target instanceof XViewContainer) {
            final float degree = 3f * degrees;
            XViewContainer container = (XViewContainer) target;
            container.extraMatrix().postRotate(degree, target.localRect.centerX(),
                    target.localRect.centerY() / 10f);
            if (degrees == 0 || animEnd) {
                container.extraMatrix(null);
            }
            return target.localRect.centerY() / 10f;
        }
        return 0f;
    }
    
    public boolean isResizeWidget(){
    	return !mResizeFrames.isEmpty();
    }

    /*** fixbug LELAUNCHER-398. AUT: zhaoxy. DATE: 2013-10-17 . START***/
    public void cleanDragView(){
        mDropView = null;
        mPage = -1;
        this.invalidate();
    }
    /*** fixbug LELAUNCHER-398. AUT: zhaoxy. DATE: 2013-10-17 . END***/
    
    public boolean isPendRunning(){
        if (mPendulumAnimIcon != null && (mPendulumAnimIcon.isStarted() || mPendulumAnimIcon.isRunning()))
    	{
    		return true;
    	}
    	return false;
    }

}
