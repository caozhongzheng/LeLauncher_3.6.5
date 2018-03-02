package com.lenovo.launcher.components.XAllAppFace;

public interface XScrollDropTarget extends XDropTarget {

    // drag sroller
    void scrollLeft();

    void scrollRight();

    /**
     * The touch point has entered the scroll area; a scroll is imminent.
     * This event will only occur while a drag is active.
     *
     * @param direction The scroll direction
     */
    boolean onEnterScrollArea(int x, int y, int direction);

    /**
     * The touch point has left the scroll area.
     * NOTE: This may not be called, if a drop occurs inside the scroll area.
     */
    boolean onExitScrollArea();

    boolean isScrollEnabled();

    int getScrollWidth();
    
    int getScrollLeftPadding();
    
}
