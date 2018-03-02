package com.lenovo.launcher.components.XAllAppFace;

import java.util.ArrayList;

import android.graphics.RectF;
import android.view.MotionEvent;
import android.widget.TabHost;

import com.lenovo.launcher.components.XAllAppFace.slimengine.BaseDrawableGroup;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;

public class XTabWidget extends BaseDrawableGroup {
	
	private OnTabSelectionChanged mSelectionChangedListener;
//	private Drawable tabSelector;
	private XContext mContext;
	private int tempSpace = 0;//90;
	
	private int mSelectedTab = -1;
	private int mChildrenGap = 0;

	public XTabWidget(XContext c) {
		super(c);
	    this.mContext = c;
	    final float scale = c.getResources().getDisplayMetrics().density;
	    tempSpace = (int) (tempSpace * scale);
	}
	
	public DrawableItem getChildTabViewAt(int index) {
        return getChildAt(index);
    }
	
	public boolean addItem(DrawableItem item) {
		boolean ret = super.addItem(item);
		int widgetCnt = getChildCount();
    	float widgetSingleWidth = widgetCnt == 0 ? 0 : ((getWidth() - tempSpace - 2 * widgetCnt * mChildrenGap) / widgetCnt);
    	float widgetHeight = getHeight() - mPaddingTop - mPaddingBottom;
    	
//    	final List<DrawableItem> items = getChildren();
    	for (int i = 0; i < getChildCount(); i++) {
    		DrawableItem child = getChildAt(i);
    		float left = i * widgetSingleWidth + this.mChildrenGap * (2 * i + 1);
    		RectF rectF = new RectF(left, mPaddingTop, left + widgetSingleWidth, mPaddingTop + widgetHeight);
    		child.resize(rectF);
    	}
		return ret;
	}

	@Override
	public void resize(RectF rect) {
	    super.resize(rect);
	    int widgetCnt = getChildCount();
        float widgetSingleWidth = widgetCnt == 0 ? 0 : ((getWidth() - tempSpace - 2 * widgetCnt * mChildrenGap) / widgetCnt);
        float widgetHeight = getHeight() - mPaddingTop - mPaddingBottom;
        
//    	List<DrawableItem> items = getChildren();
    	for (int i = 0; i < getChildCount(); i++) {
    		DrawableItem child = getChildAt(i);
    		float left = i * widgetSingleWidth + this.mChildrenGap * (2 * i + 1);
    		RectF rectF = new RectF(left, mPaddingTop, left + widgetSingleWidth, mPaddingTop + widgetHeight);
//          RectF rectF = new RectF(left, 0, left + widgetSingleWidth, child.getHeight());
    		child.resize(rectF);
    	}
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
//		int tabIndex = getTabIndex(e);
//		if (tabIndex == -1) {
//			return false;
//		}
//		mSelectionChangedListener.onTabSelectionChanged(tabIndex, true);
		return super.onDown(e);
	}
	
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		super.onSingleTapUp(e);
		
		int tabIndex = getTabIndex(e);
		if (tabIndex == -1) {
			return false;
		}
		if (tabIndex != mSelectedTab) {
		    mSelectionChangedListener.onTabSelectionChanged(tabIndex, false);
		}
		return true;
	}
	
	private int getTabIndex(MotionEvent e) {
//		List<DrawableItem> items = getChildren();
//		int widgetCnt = getChildCount();
		ArrayList<DrawableItem> hitedChildren = checkHitedItem(e);
		if( hitedChildren.isEmpty() ){
			return -1;
		}
		DrawableItem hited = checkHitedItem(e).get(0);
    	for (int i = 0; i < getChildCount(); i++) {
    		DrawableItem child = getChildAt(i);
    		if (hited == child) {
    			return i;
    		}
//    		if (e.getX() >= child.getGlobalX() 
//    				&& e.getX() < child.getGlobalX() + child.getWidth()
//    				&& e.getY() >= child.getGlobalY()
//    				&& e.getY() < child.getGlobalY() + child.getHeight()) {
//    			return i;
//    		}
    	}
    	return -1;
	}

    /**
     * Let {@link TabHost} know that the user clicked on a tab indicator.
     */
    static interface OnTabSelectionChanged {
        /**
         * Informs the TabHost which tab was selected. It also indicates
         * if the tab was clicked/pressed or just focused into.
         *
         * @param tabIndex index of the tab that was selected
         * @param clicked whether the selection changed due to a touch/click
         * or due to focus entering the tab through navigation. Pass true
         * if it was due to a press/click and false otherwise.
         */
        void onTabSelectionChanged(int tabIndex, boolean clicked);
    }
    
    void setTabSelectionListener(OnTabSelectionChanged listener) {
        mSelectionChangedListener = listener;
    }
    
    public void focusCurrentTab(int index) {
        final int oldTab = mSelectedTab;

        // set the tab
        setCurrentTab(index);

        // change the focus if applicable.
        if (oldTab != index) {
//            getChildTabViewAt(index).requestFocus();
        }
    }
    
    public void setCurrentTab(int index) {
        if (index < 0 || index >= this.getChildCount() || index == mSelectedTab) {
            return;
        }

//        if (mSelectedTab != -1) {
//            getChildTabViewAt(mSelectedTab).setSelected(false);
//        }
        mSelectedTab = index;
//        List<DrawableItem> items = getChildren();
//        int widgetCnt = getChildCount();
        for (int i = 0; i < getChildCount(); i++) {
            DrawableItem child = getChildAt(i);
            if (i == mSelectedTab) {
                child.setSelected(true);
            } else {
                child.setSelected(false);
            }
        }
//        getChildTabViewAt(mSelectedTab).setSelected(true);
//        mStripMoved = true;

//        if (isShown()) {
//            sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED);
//        }
    }
    
//    public void setAlpha(int alpha) {
//        R2.echo(this + "setAlpha =" + alpha);
//        super.setAlpha(alpha);
//    }
    
    public void setChildrenGap(int gap) {
    	mChildrenGap = gap;
    	int widgetCnt = getChildCount();
    	if (widgetCnt > 0) {
    		resize(localRect);
    	}
    }
    
}
