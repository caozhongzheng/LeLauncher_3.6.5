package com.lenovo.launcher2.addon.search;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;

public class SearchListView extends ListView {
    
    OnSearchListTouchListener mOnSearchListTouchListener;
    
    public SearchListView(Context context) {
        super(context);
    }
    
    public SearchListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public SearchListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override  
    public boolean onTouchEvent(MotionEvent event) {
        if (mOnSearchListTouchListener != null)
        {
            mOnSearchListTouchListener.onTouch(event);
        }
        
        return super.onTouchEvent(event);  
    }  
    
    public interface OnSearchListTouchListener {
        public void onTouch(MotionEvent event);
    }
    
    public void setOnTouchListener(OnSearchListTouchListener l) {
        mOnSearchListTouchListener = l;
    }
    
}
