package com.lenovo.launcher.components.XAllAppFace;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher2.customizer.Debug.R5;


public class XDragController {
    
    private XLauncher mLauncher;
    private InputMethodManager mInputMethodManager;
    
    private XDropTarget.XDragObject mDragObject;
    
    private XDropTarget mLastDropTarget;

    private static final int SCROLL_OUTSIDE_ZONE = 0;
    private static final int SCROLL_WAITING_IN_ZONE = 1;

    public static final int SCROLL_NONE = -1;
    public static final int SCROLL_LEFT = 0;
    public static final int SCROLL_RIGHT = 1;

    /** the area at the edge of the screen that makes the workspace go left
     *   or right while you're dragging.
     */
    int mScrollZone;
    private int mDistanceSinceScroll = 0;
    private int mScrollState = SCROLL_OUTSIDE_ZONE;
    
    static int SCROLL_DELAY = 500;
    static int RESCROLL_DELAY = 550;
    private ScrollRunnable mScrollRunnable = new ScrollRunnable();
    private XScrollDropTarget mDragScroller;
    private Handler mHandler;

    /** Whether or not we're dragging. */
    private boolean mDragging = false;
    
    public boolean isDragging() {
    	return mDragging;
    }

    /** X coordinate of the down event. */
    private float mMotionDownX;

    /** Y coordinate of the down event. */
    private float mMotionDownY;
    
    private float mLastTouchLoacl[] = new float[2];
    private float mLastTouchGlobal[] = new float[2];

    // temporaries to avoid gc thrash
    private Rect mRectTemp = new Rect();
    private final int[] mCoordinatesTemp = new int[2];

    private float mTmpPoint[] = new float[2];
    private RectF mDragLayerRect = new RectF();
    
    private final Vibrator mVibrator;
    private static final int VIBRATE_DURATION = 35;
    /* RK_ID: RK_DRAGCONTROLLER. AUT: liuli1 . DATE: 2013-04-27 . START */
    private static final String TAG = "DragController";
    /* RK_ID: RK_DRAGCONTROLLER. AUT: liuli1 . DATE: 2013-04-27 . END */

    /** Who can receive drop events */
    private ArrayList<XDropTarget> mDropTargets = new ArrayList<XDropTarget>();

    private ConcurrentLinkedQueue<XDragListener> mListeners = new ConcurrentLinkedQueue<XDragListener>();

    /**
     * Interface to receive notifications when a drag starts or stops
     */
    public interface XDragListener {

        /**
         * A drag has begun
         * 
         * @param source An object representing where the drag originated
         * @param info The data associated with the object that is being dragged
         * @param dragAction The drag action: either {@link DragController#DRAG_ACTION_MOVE}
         *        or {@link DragController#DRAG_ACTION_COPY}
         */
        void onDragStart(XDragSource source, Object info, int dragAction);

        /**
         * The drag has ended
         */
        void onDragEnd();
    }


    /**
     * Used to create a new DragLayer from XML.
     *
     * @param context The application's context.
     */
    public XDragController(XLauncher launcher) {
        mLauncher = launcher;
        mVibrator = (Vibrator) launcher.getSystemService(Context.VIBRATOR_SERVICE);

        mHandler = new Handler();
        mScrollZone = launcher.getResources().getDimensionPixelSize(R.dimen.scroll_zone);
    }
    
    /**
     * Clamps the position to the drag layer bounds.
     */
    private float[] getClampedDragLayerPos(float x, float y) {
        mTmpPoint[0] = x;
        mTmpPoint[1] = y;
        mDragLayerRect.set(mLauncher.getDragLayer().localRect);
        mDragLayerRect.offsetTo(0, 0);
        mLauncher.getDragLayer().getInvertMatrix().mapPoints(mTmpPoint);
        mTmpPoint[0] -= mLauncher.getDragLayer().getRelativeX();
        mTmpPoint[1] -= mLauncher.getDragLayer().getRelativeY();
        mTmpPoint[0] = Math.max(0, Math.min(mTmpPoint[0], mDragLayerRect.right - 1));
        mTmpPoint[1] = Math.max(0, Math.min(mTmpPoint[1], mDragLayerRect.bottom - 1));
        return mTmpPoint;
    }
    
    public boolean onDown(MotionEvent e) {
        final float[] dragLayerPos = getClampedDragLayerPos(e.getX(), e.getY());
        // Remember where the motion event started
        mMotionDownX = dragLayerPos[0];
        mMotionDownY = dragLayerPos[1];
        return false;
    }
    
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (!mDragging) {
            return false;
        }
        final float[] dragLayerPos = getClampedDragLayerPos(e2.getX(), e2.getY());
        handleMoveEvent((int) dragLayerPos[0], (int) dragLayerPos[1]);
        /* AUT: liuli liuli0120@use.com.cn DATE: 2012-01-05 START */
        // when user move the dragging item, we dismiss quick action dialog
        dismissPopupWindow((int) dragLayerPos[0], (int) dragLayerPos[1]);
        /* AUT: liuli liuli0120@use.com.cn DATE: 2012-01-05 END */

        return true;
    }
    
    public boolean onFingerUp(MotionEvent e) {
        if (!mDragging) {
            return false;
        }
        final float[] dragLayerPos = getClampedDragLayerPos(e.getX(), e.getY());
        final float dragLayerX = dragLayerPos[0];
        final float dragLayerY = dragLayerPos[1];
        handleMoveEvent((int) dragLayerX, (int) dragLayerY);
        if (mDragging) {
            drop(dragLayerX, dragLayerY);
        }
        endDrag();
        return true;
    }
    
    public void onTouchCancel() {
        cancelDrag();
        clearScrollRunnable();
    }
    
    private void handleMoveEvent(int x, int y) {
        /* RK_ID: RK_DRAGCONTROLLER. AUT: liuli1 . DATE: 2013-04-27 . START */
        if (mDragObject == null || mDragObject.dragView == null) {
            android.util.Log.w(TAG, "handleMoveEvent error   /// " + mDragging);
            return;
        }
        /* RK_ID: RK_DRAGCONTROLLER. AUT: liuli1 . DATE: 2013-04-27 . END */
        mDragObject.dragView.move(x, y);

        mLastTouchGlobal[0] = x;
        mLastTouchGlobal[1] = y;

        // Drop on someone?
        final int[] coordinates = mCoordinatesTemp;
        XDropTarget dropTarget = findDropTarget(x, y, coordinates);
        mDragObject.x = coordinates[0];
        mDragObject.y = coordinates[1];
        if (dropTarget != null) {
            XDropTarget delegate = dropTarget.getDropTargetDelegate(mDragObject);
            if (delegate != null) {
                dropTarget = delegate;
            }
            
            if (mLastDropTarget != dropTarget) {
                if (mLastDropTarget != null) {
                    clearScrollRunnable();
                    mLastDropTarget.onDragExit(mDragObject);
                }
                dropTarget.onDragEnter(mDragObject);
            }
            dropTarget.onDragOver(mDragObject);

            scroll(dropTarget, x, y);
        } else {
            if (mLastDropTarget != null) {
                clearScrollRunnable();
                mLastDropTarget.onDragExit(mDragObject);
            }
        }
        mLastDropTarget = dropTarget;
    }

    private void scroll(XDropTarget dropTarget, int x, int y) {
        if (!(dropTarget instanceof XScrollDropTarget)) {
            return;
        }

        mDragScroller = (XScrollDropTarget) dropTarget;

        boolean inDeleteRegion = false;
        // After a scroll, the touch point will still be in the scroll region.
        // Rather than scrolling immediately, require a bit of twiddling to scroll again
        final int slop = ViewConfiguration.get(mLauncher).getScaledWindowTouchSlop();
        mDistanceSinceScroll += Math.sqrt(Math.pow(mLastTouchLoacl[0] - x, 2)
                + Math.pow(mLastTouchLoacl[1] - y, 2));
        mLastTouchLoacl[0] = x;
        mLastTouchLoacl[1] = y;
        final int delay = mDistanceSinceScroll < slop ? RESCROLL_DELAY : SCROLL_DELAY;

        if (!inDeleteRegion && x < (mDragScroller.getScrollLeftPadding() + mScrollZone)) {
            if (mScrollState == SCROLL_OUTSIDE_ZONE) {
                mScrollState = SCROLL_WAITING_IN_ZONE;
                if (mDragScroller.onEnterScrollArea(x, y, SCROLL_LEFT)) {
                    mScrollRunnable.setDirection(SCROLL_LEFT);
                    mHandler.postDelayed(mScrollRunnable, delay);
                } else {
                    clearScrollRunnable();
                }
            }
        } else if (!inDeleteRegion && x > mDragScroller.getScrollWidth() - mScrollZone - mDragScroller.getScrollLeftPadding()) {
            if (mScrollState == SCROLL_OUTSIDE_ZONE) {
                mScrollState = SCROLL_WAITING_IN_ZONE;
                if (mDragScroller.onEnterScrollArea(x, y, SCROLL_RIGHT)) {
                    mScrollRunnable.setDirection(SCROLL_RIGHT);
                    mHandler.postDelayed(mScrollRunnable, delay);
                } else {
                    clearScrollRunnable();
                }
            }
        } else {
            clearScrollRunnable();
        }
    }

    private void clearScrollRunnable() {
        mHandler.removeCallbacks(mScrollRunnable);
        if (mScrollState == SCROLL_WAITING_IN_ZONE) {
            mScrollState = SCROLL_OUTSIDE_ZONE;
            mScrollRunnable.setDirection(SCROLL_RIGHT);
            if (mDragScroller != null) {
                mDragScroller.onExitScrollArea();
            }
        }
    }
    
    public void scaleDragView(float scale, boolean animate) {
    	if (mDragObject.dragView != null) {
    		mDragObject.dragView.scale(scale, animate);
        }
    }

    private class ScrollRunnable implements Runnable {
        private int mDirection;

        ScrollRunnable() {
        }

        public void run() {
            if (mDragScroller != null) {
                if (mDirection == SCROLL_LEFT) {
                    mDragScroller.scrollLeft();
                } else {
                    mDragScroller.scrollRight();
                }
                mScrollState = SCROLL_OUTSIDE_ZONE;
                mDistanceSinceScroll = 0;
                mDragScroller.onExitScrollArea();

                if (isDragging()) {
                    forceMoveEvent();
                }
            }
        }

        void setDirection(int direction) {
            mDirection = direction;
        }
    }

    public void forceMoveEvent() {
        if (mDragging) {
            handleMoveEvent((int) mLastTouchGlobal[0], (int) mLastTouchGlobal[1]);
        }
    }

    /* AUT: xingqx xingqx@lenovo.com DATE: 2012-03-14 START */
    private void dismissPopupWindow(int x, int y) {
        final int MOVE_X_MIN = mLauncher.getResources().getDimensionPixelSize(
                R.dimen.quick_aciton_x_move_min);
        final int MOVE_Y_MIN = mLauncher.getResources().getDimensionPixelSize(
                R.dimen.quick_aciton_y_move_min);
        if (Math.abs(x - mMotionDownX) > MOVE_X_MIN
                || Math.abs(y - mMotionDownY) > MOVE_Y_MIN) {
            mLauncher.dismissQuickActionWindow();
        }
    }

    /* AUT: xingqx xingqx@lenovo.com DATE: 2012-03-14 END */
    
    private void drop(float x, float y) {
        final int[] coordinates = mCoordinatesTemp;
        final XDropTarget dropTarget = findDropTarget((int) x, (int) y, coordinates);
        mDragObject.x = coordinates[0];
        mDragObject.y = coordinates[1];
        boolean accepted = false;
        if (dropTarget != null) {
            mDragObject.dragComplete = true;
            dropTarget.onDragExit(mDragObject);
            if (dropTarget.acceptDrop(mDragObject)) {
                dropTarget.onDrop(mDragObject);
                accepted = true;
            }
            else
            {
                R5.echo("acceptDrop false");
            }
        }
        mDragObject.dragSource.onDropCompleted((DrawableItem) dropTarget, mDragObject, accepted);
    }
    
    private void endDrag() {
        if (mDragging) {
            mDragging = false;
            clearScrollRunnable();
            mLauncher.dismissQuickWindowIfMove();
            for (XDragListener listener : mListeners) {
                listener.onDragEnd();
            }
            if (mDragObject.dragView != null) {
                mDragObject.dragView.remove();
                mDragObject.dragView = null;
            }
        }
        
        mLastDropTarget = null;
    }
    
    /**
     * Stop dragging without dropping.
     */
    public void cancelDrag() {
        if (mDragging) {
            if (mLastDropTarget != null) {
                mLastDropTarget.onDragExit(mDragObject);
            }
            mDragObject.cancelled = true;
            mDragObject.dragComplete = true;
            mDragObject.dragSource.onDropCompleted(null, mDragObject, false);
        }
        endDrag();
    }
    
    /**
     * Starts a drag.
     *
     * @param v The DrawableItem that is being dragged
     * @param bmp The bitmap that represents the view being dragged
     * @param source An object representing where the drag originated
     * @param dragInfo The data associated with the object that is being dragged
     * @param dragAction The drag action: either {@link #DRAG_ACTION_MOVE} or
     *        {@link #DRAG_ACTION_COPY}
     * @param dragRegion Coordinates within the bitmap b for the position of item being dragged.
     *          Makes dragging feel more precise, e.g. you can clip out a transparent border
     */
    public void startDrag(DrawableItem v, Bitmap bmp, XDragSource source, Object dragInfo, int dragAction,
            Rect dragRegion) {
        
    }

    /**
     * Starts a drag.
     *
     * @param b The bitmap to display as the drag image.  It will be re-scaled to the
     *          enlarged size.
     * @param dragLayerX The x position in the DragLayer of the left-top of the bitmap.
     * @param dragLayerY The y position in the DragLayer of the left-top of the bitmap.
     * @param source An object representing where the drag originated
     * @param dragInfo The data associated with the object that is being dragged
     * @param dragAction The drag action: either {@link #DRAG_ACTION_MOVE} or
     *        {@link #DRAG_ACTION_COPY}
     * @param dragRegion Coordinates within the bitmap b for the position of item being dragged.
     *          Makes dragging feel more precise, e.g. you can clip out a transparent border
     */
    public void startDrag(Bitmap b, int dragLayerX, int dragLayerY,
            XDragSource source, Object dragInfo, int dragAction, Point dragOffset, Rect dragRegion) {
        startDrag(b, dragLayerX, dragLayerY, source, dragInfo, dragAction, dragOffset, dragRegion, true);
    }

    /**
     * Starts a drag.
     *
     * @param b The bitmap to display as the drag image.  It will be re-scaled to the
     *          enlarged size.
     * @param dragLayerX The x position in the DragLayer of the left-top of the bitmap.
     * @param dragLayerY The y position in the DragLayer of the left-top of the bitmap.
     * @param source An object representing where the drag originated
     * @param dragInfo The data associated with the object that is being dragged
     * @param dragAction The drag action: either {@link #DRAG_ACTION_MOVE} or
     *        {@link #DRAG_ACTION_COPY}
     * @param dragRegion Coordinates within the bitmap b for the position of item being dragged.
     *          Makes dragging feel more precise, e.g. you can clip out a transparent border
     * @param effectAnim effectAnim
     */
    public void startDrag(Bitmap b, int dragLayerX, int dragLayerY,
            XDragSource source, Object dragInfo, int dragAction, Point dragOffset, Rect dragRegion, boolean effectAnim) {
        if (!isDragging()) {
            mLauncher.getDragLayer().clearDragView();
        }

        XLauncherModel.setDatabaseDirty(true);

        mLauncher.getDragLayer().cancelPendulumAnim();

        for (XDragListener listener : mListeners) {
            listener.onDragStart(source, dragInfo, dragAction);
        }
        
        final int registrationX = (int) (mMotionDownX - dragLayerX);
        final int registrationY = (int) (mMotionDownY - dragLayerY);

        final int dragRegionLeft = dragRegion == null ? 0 : dragRegion.left;
        final int dragRegionTop = dragRegion == null ? 0 : dragRegion.top;
        
        mDragging = true;

        mDragObject = new XDropTarget.XDragObject();
        
        mDragObject.dragComplete = false;
        mDragObject.xOffset = (int) (mMotionDownX - (dragLayerX + dragRegionLeft));
        mDragObject.yOffset = (int) (mMotionDownY - (dragLayerY + dragRegionTop));
        mDragObject.dragSource = source;
        mDragObject.dragInfo = dragInfo;
        
        vibrate();
        
        final XDragView dragView = mDragObject.dragView = new XDragView(mLauncher, b, registrationX,
                registrationY, 0, 0, b.getWidth(), b.getHeight());
        
        if (dragRegion != null) {
            dragView.setDragRegion(new Rect(dragRegion));
        }

        dragView.show((int) mMotionDownX, (int) mMotionDownY, effectAnim);
        handleMoveEvent((int) mMotionDownX, (int) mMotionDownY);
        dragView.interruptLongPressed();
    }
    
    private void vibrate() {
        try {
            mVibrator.vibrate(VIBRATE_DURATION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private XDropTarget findDropTarget(int x, int y, int[] dropCoordinates) {
        final Rect r = mRectTemp;
        final ArrayList<XDropTarget> dropTargets = mDropTargets;
        final int count = dropTargets.size();
        for (int i=count-1; i>=0; i--) {
            XDropTarget target = dropTargets.get(i);
            if (!target.isDropEnabled())
                continue;
            
            target.getHitRect(r);
            
            // Convert the hit rect to DragLayer coordinates
            target.getLocationInDragLayer(dropCoordinates);
            r.offset(dropCoordinates[0], dropCoordinates[1]);

            mDragObject.x = x;
            mDragObject.y = y;
            
            if (r.contains(x, y)) {
                XDropTarget delegate = target.getDropTargetDelegate(mDragObject);
                if (delegate != null) {
                    target = delegate;
                    target.getLocationInDragLayer(dropCoordinates);
                }

                // Make dropCoordinates relative to the DropTarget
                dropCoordinates[0] = x - dropCoordinates[0];
                dropCoordinates[1] = y - dropCoordinates[1];

                return target;
            }
        }
        return null;
    }
    
    /**
     * Sets the drag listner which will be notified when a drag starts or ends.
     */
    public void addDragListener(XDragListener l) {
        mListeners.add(l);
    }

    /**
     * Remove a previously installed drag listener.
     */
    public void removeDragListener(XDragListener l) {
        mListeners.remove(l);
    }

    /**
     * Add a DropTarget to the list of potential places to receive drop events.
     */
    public void addDropTarget(XDropTarget target) {
        mDropTargets.add(target);
    }

    /**
     * Don't send drop events to <em>target</em> any more.
     */
    public void removeDropTarget(XDropTarget target) {
        mDropTargets.remove(target);
    }
    
    public boolean containsDragListener(XDragListener listener) {
    	return mListeners.contains(listener);
    }

    /* RK_ID: RK_DRAGCONTROLLER. AUT: liuli1 . DATE: 2013-06-06 . START */
    XDropTarget getLastDragTarget() {
        return mLastDropTarget;
    }
    /* RK_ID: RK_DRAGCONTROLLER. AUT: liuli1 . DATE: 2013-06-06 . END */
    
    XDragSource getDraggingObjectSource() {
    	if (!isDragging() || mDragObject == null) {
    		return null;
    	}
    	return mDragObject.dragSource;
    }

}
