package com.lenovo.launcher2.commonui;

import com.lenovo.launcher.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ListAdapter;

public class ShortcutGridView extends GridView {
	
	private float mverticalSpacing = 0;


	public ShortcutGridView(Context context, AttributeSet attrs) {
		this(context,attrs,com.android.internal.R.attr.gridViewStyle);
	}

	public ShortcutGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ShortcutGridView, defStyle, 0);
		mverticalSpacing = a.getDimension(R.styleable.ShortcutGridView_verticalSpacing, 0);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        ListAdapter adpter = getAdapter();
        int mItemCount = adpter== null ? 0 : adpter.getCount();
        final int count = Math.min(4*getNumColumns(),mItemCount);
       
        if (mItemCount > 0) {
            final View child = adpter.getView(0, null, this);
            AbsListView.LayoutParams p = (AbsListView.LayoutParams)child.getLayoutParams();
            if (p == null) {
                p = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 0);
                child.setLayoutParams(p);
            }

            int childHeightSpec = getChildMeasureSpec(
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), 0, p.height);
            int childWidthSpec = getChildMeasureSpec(
                    MeasureSpec.makeMeasureSpec(getNumColumns(), MeasureSpec.EXACTLY), 0, p.width);
            child.measure(childWidthSpec, childHeightSpec);
            int childHeight = child.getMeasuredHeight();
            int ourSize =  getListPaddingTop() + getListPaddingBottom();
            
            final int numColumns = getNumColumns();
            for (int i = 0; i < count; i += numColumns) {
                ourSize += childHeight;
                if (i + numColumns <= count) {
                    ourSize +=  mverticalSpacing;
                }
                if (ourSize >= heightSize) {
                    ourSize = heightSize;
                    break;
                }
            }
            heightSize = ourSize ;
        }
        setMeasuredDimension(widthSize, heightSize);
       		
	  }

}
