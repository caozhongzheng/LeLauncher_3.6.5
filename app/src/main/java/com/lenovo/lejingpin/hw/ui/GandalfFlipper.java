package com.lenovo.lejingpin.hw.ui;


import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;

import com.lenovo.launcher.R;

/**
 * It's a Gallery with indicator since 4/15 2011
 * 
 * @author lizhi 
 */
public class GandalfFlipper extends LinearLayout {
	
	private Gallery mGallery;  
	private LinearLayout mLinearLayout;
	private Adapter mAdapter;
	
	public GandalfFlipper(Context context) {
		this(context,null);
	}
	
	public GandalfFlipper(Context context, AttributeSet attrs) {
		super(context, attrs);
		initGandalfFlipper();
	}
	
	/**
	 * init the GandalfFlipper
	 */
	private void  initGandalfFlipper() {
		this.setOrientation(LinearLayout.VERTICAL);
		mGallery = new Gallery(this.getContext());
		mGallery.setSpacing(10);
		
		mLinearLayout = new LinearLayout(this.getContext());
		LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 30);
		//ll.topMargin = 5;
		ll.gravity = Gravity.CENTER;
		this.addView(mGallery);
		this.addView(mLinearLayout,ll);
	}
	
	public void setAdapter(SpinnerAdapter adapter) {
		this.mAdapter = adapter;
		mGallery.setAdapter(adapter);
		if(adapter.getCount()>2){
			mGallery.setSelection(1);
		}
		initLinerLayout();
		initGallery();
		
	}
	
	public Adapter getAdapter(){
		
		return mAdapter;
	}
	
	private void initGallery() {
		mGallery.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
            	syncViewflipperLinerLayout(arg2);
            }

            @Override
			public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
	}
	/**
	 * make the mGrellery and the indicator synchronizing
	 */
	public void syncViewflipperLinerLayout(int child) {
		

		
		ImageView now = (ImageView) mLinearLayout.getChildAt(child);
		now.setImageResource(R.drawable.dot_focus_ym);
		
		// recover previous imageview
		for (int i = 0; i < mLinearLayout.getChildCount(); i++) {
			if (i != child) {
				ImageView previous = (ImageView) mLinearLayout.getChildAt(i);
				previous.setImageResource(R.drawable.dot_ym);
			}
		}
	}
	private void initLinerLayout() {
		mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
		mLinearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
		
		LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		ll.gravity = 0x11;
		//ll.topMargin = 5;
		ll.leftMargin = 10;
		ImageView first = new ImageView(this.getContext());
		first.setImageResource(R.drawable.dot_focus_ym);
		mLinearLayout.addView(first, ll);
		for (int i = 1; i < this.mAdapter.getCount(); i++) {
			ImageView after = new ImageView(this.getContext());
			after.setImageResource(R.drawable.dot_ym);
			mLinearLayout.addView(after, ll);

		}
		if(this.mAdapter.getCount() == 0) {
			mLinearLayout.removeView(first);
		}
	}
		
	public Gallery getmGallery() {
		return mGallery;
	}
	public void setmGallery(Gallery mGallery) {
		this.mGallery = mGallery;
	}
}
