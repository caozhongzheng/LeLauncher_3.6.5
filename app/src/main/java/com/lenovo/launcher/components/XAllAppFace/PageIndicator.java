package com.lenovo.launcher.components.XAllAppFace;

import java.util.ArrayList;

import android.content.res.ColorStateList;
import android.graphics.RectF;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.slimengine.BaseDrawableGroup;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;

public class PageIndicator extends BaseDrawableGroup {

	private XContext mContext;
	final private LauncherApplication mApp;

	public int homePointTextSize;
	public int homePointWidth;
	public int homePointHeight;
	public int homePointGap;
	public int mOrignHomePointGap;
	public ColorStateList color;
	private int indicator_padding_top=0;
	public int getHomePointGap() {
		return homePointGap;
	}

	public PageIndicator(XContext context, RectF region) {
		super(context);
		
		resize(region);
//		indicators = new ArrayList<IndicatableItem>();

		mContext = context;
		mApp = (LauncherApplication) mContext.getContext().getApplicationContext();

		initIndicator();
	}

	private void initIndicator() {

		homePointTextSize = mContext.getResources().getDimensionPixelOffset(
				R.dimen.home_point_text_size);

		color = mApp.mLauncherContext.getColor(R.color.home_point_text_color,
				R.color.def__home_point_text_color);
		homePointWidth = mApp.mLauncherContext.getDimensionPixel(
				R.dimen.home_point_width, R.dimen.def__home_point_width);
		homePointHeight = mApp.mLauncherContext.getDimensionPixel(
				R.dimen.home_point_height, R.dimen.def__home_point_height);
		mOrignHomePointGap = mApp.mLauncherContext.getDimensionPixel(
				R.dimen.home_point_gap, R.dimen.def__home_point_gap);
		indicator_padding_top= mApp.mLauncherContext.getDimensionPixel(
				R.dimen.workspace_indicator_top, R.dimen.workspace_indicator_top);
		
		homePointGap = getGap();
	}

	private int currentPage = 0;

	//private IndicatableItem travelor = null;
	private ArrayList<IndicatableItem> indicators = new ArrayList<IndicatableItem>();

	public int getCurrentPage() {
		return currentPage;
	}

	public void addIndicatorCell(int count) {
		//for bug 12943
//		initIndicator();

		for (int i = 0; i < count; i++) {
		    addOneIndicatorCell(i);
		}
		//add by zhanggx1 on 2013-07-03.s
        this.setVisibility(mSingleVisible || indicators.size() > 1);
        //add by zhanggx1 on 2013-07-03.e
	}
	
	public void addOneIndicatorCell(int i) {
        
        final float pointX = homePointWidth * indicators.size();
        final RectF r = new RectF(homePointGap * i + pointX, indicator_padding_top,
                homePointGap * i + pointX + homePointWidth, homePointHeight);
        IndicatableItem ir = new ThemeStyleIndicator(getXContext(), mApp.mLauncherContext,
                r, homePointWidth, homePointHeight);
        ir.page = i;
        this.addItem((DrawableItem) ir);
        if (indicators != null) {
            indicators.add(ir);
        }
    }

	public void changePageFromTo(int from, int to, float deltaTravel) {
		if (indicators == null) {
			return;
		}
		int len = indicators.size();
		if (from >= len || to >= len || from < 0 || to < 0) {
			return;
		}

		if (from == to) {
			resetTo(from);
			return;
		}

//		 android.util.Log.i("page", "update page from : " + from + " to " +
//		 to);
		try {
			if(indicators.get(from) != null){
				indicators.get(from).onHide(true);
			}
			//if (travelor != null) {
				// travelFromTo(from, to, deltaTravel);
			//} else {
				if(indicators.get(to) != null){
					indicators.get(to).onShow(true);
				}
			//}
		} catch (Exception e) {
		}

        /* RK_ID: RK_ALLAPPS. AUT: liuli1 . DATE: 2013-06-03 . START */
        // fix bug 14113
        if (len > to) {
            for (int i = 0; i < len; i++) {
                if (i != to) {
                    indicators.get(i).onHide(true);
                }
            }
        }
        /* RK_ID: RK_ALLAPPS. AUT: liuli1 . DATE: 2013-06-03 . END */
	}

	/*** fixbug AUT: zhaoxy . DATE: 2012-12-10 . START ***/
	public void initIndicator(int pageCount, int currentPage) {
		if (indicators == null) {
			return;
		}
		
		int i;
		int size = indicators.size();
		if (size > pageCount)
		{
		    for (i = size - 1; i > pageCount - 1; i--)
		    {
		        indicators.remove(i);
		        removeItem(i);
		    }		    
		}
		else if (size < pageCount)
		{
		    for (i = size; i < pageCount; i++)
		    {
		        addOneIndicatorCell(i);
		    }		    
		}
		//add by zhanggx1 on 2013-07-03.s
        this.setVisibility(mSingleVisible || indicators.size() > 1);
        //add by zhanggx1 on 2013-07-03.e
				
		calculateGap();
//		indicators.clear();
//		clearAllItems();
//		addIndicatorCell(pageCount);
		
		resetTo(currentPage);		
	}
	/*** fixbug AUT: zhaoxy . DATE: 2012-12-10 . END ***/

	public void resetTo(int toPage) {
		if (toPage < 0 || indicators == null) {
			return;
		}

//		android.util.Log.i("page", "reset to page : " + toPage);

		int len = indicators.size();
		if (len > toPage) {
			for (int i = 0; i < len; i++) {
				if (i != toPage) {
					indicators.get(i).onHide(true);
				} else {
					indicators.get(i).onShow(true);
				}
			}
		}
	}
	
	public void updateTheme(){
	    initIndicator();
		
		int count = indicators.size();
		
		float wholeWidth = localRect.left + localRect.right;
		float allPointsWidth = (homePointWidth + homePointGap) * count - homePointGap;
		localRect.left = (wholeWidth - allPointsWidth) / 2;
		localRect.right = localRect.left + allPointsWidth; 
		resize(localRect);
		
		reArrangeChild();
	}
	
	@Override
	public void clean() {
		if (indicators != null) {
			int count = indicators.size();
			for (int i = 0; i < count; i++){
		    	IndicatableItem item = indicators.get(i);
		        item.clean();
		    }
			indicators.clear();
			indicators = null;
		}
		super.clean();
	}
	
    private void reArrangeChild() {    	
        if (indicators == null)
        {
            return;
        }
                
        int num = indicators.size();
        for (int i = 0; i < num; i++){
            IndicatableItem item = indicators.get(i);
            item.updateTheme(homePointWidth, homePointHeight, i == 0);
            
            final float pointX = homePointWidth * i;
            float left = homePointGap * i + pointX;
            final RectF r = new RectF(left, 0,
                    left + homePointWidth, homePointHeight);
            item.resize(r);
        }
        
    }
    
    private int getGap() {        
        if (indicators == null)
        {
            return mOrignHomePointGap;
        }
        
        int num = indicators.size();
        int gap;
        int width =  (mOrignHomePointGap + homePointWidth ) * num - mOrignHomePointGap;
        
        if (getParent() != null && width > getParent().getWidth() && num > 1)
        {
            gap = (int)(getParent().getWidth() - homePointWidth * num) / (num - 1);
//            if (gap < 0)
//            {
//                gap = 0;   
//            }            
        }
        else
        {
            gap = mOrignHomePointGap;
        }
        
        return gap;
    }
    
    public void calculateGap() {
        int gap = getGap();
        if (gap != homePointGap)
        {
            homePointGap = gap;
            reArrangeChild();
        }

        if (indicators != null)
        {
            localRect.right = localRect.left + (homePointWidth + homePointGap) * indicators.size() - homePointGap; 
        }
    }
    
    //add by zhanggx1 on 2013-07-03.s
  	private boolean mSingleVisible = true;
    public void setSingleIndicatorVisible(boolean visible) {
      	mSingleVisible = visible;
      	this.setVisibility(mSingleVisible || indicators.size() > 1);
    }
    //add by zhanggx1 on 2013-07-03.e
}
