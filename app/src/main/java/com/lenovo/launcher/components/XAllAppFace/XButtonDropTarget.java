package com.lenovo.launcher.components.XAllAppFace;

import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;

import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;

public class XButtonDropTarget extends DrawableItem implements XDropTarget {

    public XButtonDropTarget(XContext context) {
        super(context);
    }

    // Drag & Drop start
    private static final boolean DEBUG_DRAG = true;
    private static final String TAG_DEBUG_DRAG = "DEBUG_DRAG";
    
    @Override
    public boolean isDropEnabled() {
        return true;
    }
    
    @Override
    public boolean onDown(MotionEvent e) {
        super.onDown(e);
        return true;
    }
    
    @Override
    public boolean onFingerUp(MotionEvent e) {
        super.onFingerUp(e);
        return true;
    }
    
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY, float previousX, float previousY) {
        super.onScroll(e1, e2, distanceX, distanceY, previousX, previousY);
        return true;
    }
    
    @Override
    public boolean onLongPress(MotionEvent e) {
        super.onLongPress(e);
        return true;
    }
    
    @Override
    public boolean onFingerCancel(MotionEvent e) {
        super.onFingerCancel(e);
        return true;
    }
    
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        super.onFling(e1, e2, velocityX, velocityY);
        return true;
    }
    
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        super.onSingleTapUp(e);
        return true;
    }
    
    @Override
    public boolean onShowPress(MotionEvent e) {
        super.onShowPress(e);
        return true;
    }

    @Override
    public void onDrop(XDragObject dragObject) {
        if (DEBUG_DRAG) Log.d(TAG_DEBUG_DRAG, "onDrop   XButtonDropTarget");
    }

    @Override
    public void onDragEnter(XDragObject dragObject) {
        if (DEBUG_DRAG) Log.d(TAG_DEBUG_DRAG, "onDragEnter   XButtonDropTarget");
    }

    @Override
    public void onDragOver(XDragObject dragObject) {
        if (DEBUG_DRAG) Log.d(TAG_DEBUG_DRAG, "onDragOver   XButtonDropTarget x = " + dragObject.x + " y = " + dragObject.y);
    }

    @Override
    public void onDragExit(XDragObject dragObject) {
        if (DEBUG_DRAG) Log.d(TAG_DEBUG_DRAG, "onDragExit   XButtonDropTarget");
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
        ((XLauncher) getXContext().getContext()).getDragLayer().getLocationInDragLayer(this, loc);
    }

    @Override
    public int getLeft() {
        return (int) getRelativeX();
    }

    @Override
    public int getTop() {
        return (int) getRelativeY();
    }
    
 // Drag & Drop end
    
}
