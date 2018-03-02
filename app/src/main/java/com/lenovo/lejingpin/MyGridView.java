
package com.lenovo.lejingpin;

import android.widget.GridView;
import android.content.Context;

import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.view.MotionEvent;


public class MyGridView extends GridView {
    public MyGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyGridView(Context context) {
        super(context);
    }

    public MyGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
/*
    @Override  
    public boolean onInterceptTouchEvent(MotionEvent ev) {   
        //return super.onInterceptTouchEvent(ev);   
        return false;   
    }   
*/
    public boolean onTouchEvent(MotionEvent event) {

                        switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                                //if (this.getCount() == this.getLastVisiblePosition()+1 && !isRecored) {
                                //}
                               Log.e("LeGrid","grid down ==========last="+getLastVisiblePosition());
                                break;

                        case MotionEvent.ACTION_UP:
                               Log.e("LeGrid","grid up ==========last="+getLastVisiblePosition());
                                break;
                                    case MotionEvent.ACTION_MOVE:
                                //int tempY = (int) event.getY();
                               Log.e("LeGrid","grid move ==========last="+getLastVisiblePosition());
                                break;
                         default:
                        	 break;


        }
              return super.onTouchEvent(event);
    }




    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
