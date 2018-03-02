package com.lenovo.launcher.components.XAllAppFace;

import android.graphics.Point;
import android.graphics.RectF;

import com.lenovo.launcher.components.XAllAppFace.AppContentView.PageSwitchListener;
import com.lenovo.launcher.components.XAllAppFace.slimengine.BaseDrawableGroup;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;

public class AppContentIndicator extends BaseDrawableGroup implements
		PageSwitchListener {

	private PageIndicator mIndicator;

//	RectF mRegion;
	Point mAnchor;

	private XContext mContext;

	public AppContentIndicator(XContext context, RectF region) {
		super( context );
		android.util.Log.i("region", "region : " + region);
		mContext = context;
		mIndicator = new PageIndicator(context, region);
		addItem(mIndicator);
//		mRegion = region;
	}
	
	@Override
	public void resize(RectF rect) {
	    super.resize(rect);
	    mIndicator.resize(rect);
	}

	@Override
	public void onPageSwitching(int from, int to, float percentage) {
		if (mIndicator.isVisible()) {
			mIndicator.changePageFromTo(from, to, percentage);
		}
	}

	/*** fixbug  AUT: zhaoxy . DATE: 2012-12-10 . START***/
//	boolean firstInit = true;

	@Override
	public void onUpdatePage(int pageCount) {
		// TODO Auto-generated ethod stub
//		if (firstInit) {
//		removeItem(mIndicator);
//			mIndicator.addIndicatorCell(pageCount);
			mIndicator.initIndicator(pageCount, 0);
        mIndicator.setRelativeX((getWidth() - mIndicator.getWidth()) / 2);
//		mIndicator.setRelativeY(mRegion.bottom);
//		addItem(mIndicator);
		mContext.getRenderer().invalidate();

//			firstInit = false;
//		}
		}

	/*** fixbug  AUT: zhaoxy . DATE: 2012-12-10 . END***/

	public void setShowingState(boolean show) {
		mIndicator.setVisibility(show);
	}
	
	public PageIndicator getIndicators(){
	    return mIndicator;
   }
}