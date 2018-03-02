
package com.lenovo.lejingpin;
import android.widget.GridView;
import android.content.Context;

import android.util.AttributeSet;
import android.util.Log;
import android.widget.ScrollView;
import android.view.MotionEvent;


public class MyScrollView extends ScrollView {
    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyle) {
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
			// if (this.getCount() == this.getLastVisiblePosition()+1 &&
			// !isRecored) {
			// }
			Log.e("LeGrid", "down ==========last=");

			break;

		case MotionEvent.ACTION_UP:
			scrollAndFocus();
			int subViewWidth = getChildAt(getChildCount() - 1).getBottom();
			// int sub = getChildAt(getChildCount() - 1).getHeight();

			int x = getScrollY();

			// subViewWidth - y - getHeight() == 0则表示滚动到了最右边
			int aa = subViewWidth - x - getHeight();
			Log.e("LeGrid", "up ==========lastaaaaaaaaaaaaaaa=" + aa
					+ "subWidth=" + subViewWidth);
			Log.e("LeGrid", "up ==========lastaaaaaaaaaaaaaaa="
					+ (x + getHeight()) + "subHeight=" + getHeight());

			mLastY = getScrollY();// 赋值给mLastY
			Log.e("LeGrid", "==========y1=" + mLastY + "y2==="
					+ (getChildAt(0).getHeight() - getHeight()));
			if (mLastY == (getChildAt(0).getHeight() - getHeight())) {
				Log.e("LeGrid", "::::::::::::::::::))))up ==========last=");
			}

			Log.e("LeGrid", "up ==========x1==" + (getScrollY() + getHeight())
					+ " x2=" + computeVerticalScrollRange());
			if (getScrollY() + getHeight() >= computeVerticalScrollRange()) {
				Log.d("LeGrid", "------滚动到最下方------");
			} else {
				Log.d("LeGrid", "没有到最下方");
			}
			break;
		case MotionEvent.ACTION_MOVE:
			// int tempY = (int) event.getY();
			Log.e("LeGrid", "move ==========last=");
			break;
		default:
			break;

		}
		return super.onTouchEvent(event);
	}

private int  mLastY;//赋值给mLastY
     private void scrollAndFocus() {
        boolean handled = true;

        int height = getHeight();
        int containerTop = getScrollY();
        int containerBottom = containerTop + height;
        boolean up = true;

        Log.e("LeGrid","up ==========height="+getHeight()+"scrolly="+getScrollY()+" bottom="+containerBottom);

    }

/*

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
*/
}
