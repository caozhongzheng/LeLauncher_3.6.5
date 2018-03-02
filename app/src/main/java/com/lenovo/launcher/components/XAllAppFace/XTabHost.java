package com.lenovo.launcher.components.XAllAppFace;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.slimengine.BaseDrawableGroup;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;
import com.lenovo.launcher2.customizer.SettingsValue;

public class XTabHost extends BaseDrawableGroup {
	
	private XTabWidget mTabWidget;
	
	private BaseDrawableGroup mTabContent;
    private List<TabSpec> mTabSpecs = new ArrayList<TabSpec>(2);
    /**
     * This field should be made private, so it is hidden from the SDK.
     * {@hide}
     */
    protected int mCurrentTab = -1;
    private DrawableItem mCurrentView = null;
    private RectF mTabWidgetRect;
    private RectF mTabContentRect;
    private int textSetColor = 0xffffffff;
    
    private OnTabChangeListener mOnTabChangeListener;
    private float mDrawablePadding;
    
    private static final int TABWIDGET_TEXT_SIZE = 22;
    
    public XTabHost(XContext context, RectF wholeRectF, RectF tabwidgetRect, RectF tabContentRect) {
    	super(context);
    	if (wholeRectF != null && wholeRectF.width() > 0 && wholeRectF.height() > 0) {
//			localRect = wholeRectF;
			resize(wholeRectF);
		}
		textSetColor = SettingsValue.getIconTextStyleValue(getXContext().getContext());
		final float scale = context.getResources().getDisplayMetrics().density;
		mDrawablePadding = scale * 2f;
		mTabWidgetRect = tabwidgetRect;
		mTabContentRect = tabContentRect;
		setup();
	}    
    
    public XTabHost(XContext context, RectF wholeRectF, RectF tabwidgetRect) {
    	this(context, wholeRectF, tabwidgetRect, null);
    	mTabContentRect = new RectF(0, tabwidgetRect.height(), wholeRectF.width(), wholeRectF.height());    	
	}
    
    public void resize(RectF rect, RectF rectWidget) {
		super.resize(rect);
		mTabWidgetRect = rectWidget;
		mTabWidget.resize(rectWidget);
		
		mTabContentRect = new RectF(0, rectWidget.height(), rect.width(), rect.height());
		mTabContent.resize(mTabContentRect);
	}
    
    public Context getContext() {
    	return getXContext().getContext();
    }
	
	private void setup() {
		mTabWidget = new XTabWidget(getXContext());
		LauncherApplication app = (LauncherApplication) getContext().getApplicationContext();
        Drawable tabWidgetBg = app.mLauncherContext.getDrawable(R.drawable.tab_area_background);
        mTabWidget.setBackgroundDrawable(tabWidgetBg);
		if (mTabWidgetRect != null && mTabWidgetRect.width() > 0 && mTabWidgetRect.height() > 0) {
//			mTabWidget.localRect = mTabWidgetRect;
			mTabWidget.resize(mTabWidgetRect);
		}
		mTabWidget.setTabSelectionListener(new XTabWidget.OnTabSelectionChanged() {
            public void onTabSelectionChanged(int tabIndex, boolean clicked) {
                setCurrentTab(tabIndex);
                if (clicked) {
//                    mTabContent.requestFocus(View.FOCUS_FORWARD);
                }
            }
        });
		addItem(mTabWidget);
		
		mTabContent = new BaseDrawableGroup(getXContext());
		if (mTabContentRect != null && mTabContentRect.width() > 0 && mTabContentRect.height() > 0) {
			mTabContent.resize(mTabContentRect);
			setInvertMatrixDirty();
		}
		addItem(mTabContent);
		
		mCurrentTab = -1;
        mCurrentView = null;
	}
	
	public XTabWidget getTabWidget() {
		return mTabWidget;
	}
	
	/**
     * Get a new {@link TabSpec} associated with this tab host.
     * @param tag required tag of tab.
     */
    public TabSpec newTabSpec(String tag) {
        return new TabSpec(tag);
    }
    
    public BaseDrawableGroup getContent() {
    	return mTabContent;
    }
	
	/**
     * Register a callback to be invoked when the selected state of any of the items
     * in this list changes
     * @param l
     * The callback that will run
     */
    public void setOnTabChangedListener(OnTabChangeListener l) {
        mOnTabChangeListener = l;
    }

    private void invokeOnTabChangeListener() {
        if (mOnTabChangeListener != null) {
            mOnTabChangeListener.onTabChanged(getCurrentTabTag());
        }
    }

    /**
     * Interface definition for a callback to be invoked when tab changed
     */
    public interface OnTabChangeListener {
        void onTabChanged(String tabId);
    }
    
    public int getCurrentTab() {
        return mCurrentTab;
    }

    public String getCurrentTabTag() {
        if (mCurrentTab >= 0 && mCurrentTab < mTabSpecs.size()) {
            return mTabSpecs.get(mCurrentTab).getTag();
        }
        return null;
    }

    public DrawableItem getCurrentTabView() {
        if (mCurrentTab >= 0 && mCurrentTab < mTabSpecs.size()) {
            return mTabWidget.getChildTabViewAt(mCurrentTab);
        }
        return null;
    }
    
    public class TabSpec {

        private String mTag;
        private IndicatorStrategy mIndicatorStrategy;
        private ContentStrategy mContentStrategy;

        private TabSpec(String tag) {
            mTag = tag;
        }
        
        public String getTag() {
            return mTag;
        }
        
        /**
         * Specify a label as the tab indicator.
         */
        public TabSpec setIndicator(String label) {
            mIndicatorStrategy = new LabelIndicatorStrategy(label);
            return this;
        }

//        /**
//         * Specify a label and icon as the tab indicator.
//         */
//        public TabSpec setIndicator(String label, Drawable icon) {
//            mIndicatorStrategy = new LabelAndIconIndicatorStrategy(label, icon);
//            return this;
//        }

        /**
         * Specify a view as the tab indicator.
         */
        public TabSpec setIndicator(DrawableItem view) {
            mIndicatorStrategy = new ViewIndicatorStrategy(view);
            return this;
        }
        
        public TabSpec setContent(TabContentFactory contentFactory) {
            mContentStrategy = new FactoryContentStrategy(mTag, contentFactory);
            return this;
        }
    }
    
    private static interface IndicatorStrategy {

        /**
         * Return the view for the indicator.
         */
    	DrawableItem createIndicatorView();
    }

    /**
     * Specifies what you do to manage the tab content.
     */
    private static interface ContentStrategy {

        /**
         * Return the content view.  The view should may be cached locally.
         */
    	DrawableItem getContentView();

        /**
         * Perhaps do something when the tab associated with this content has
         * been closed (i.e make it invisible, or remove it).
         */
        void tabClosed();
    }
    
    public interface TabContentFactory {
        /**
         * Callback to make the tab contents
         *
         * @param tag
         *            Which tab was selected.
         * @return The view to display the contents of the selected tab.
         */
    	DrawableItem createTabContent(String tag);
    }
    
    /**
     * How to create a tab indicator by specifying a view.
     */
    private class ViewIndicatorStrategy implements IndicatorStrategy {

        private final DrawableItem mView;

        private ViewIndicatorStrategy(DrawableItem view) {
            mView = view;
        }

        public DrawableItem createIndicatorView() {
            return mView;
        }
    }
    
    private class LabelIndicatorStrategy implements IndicatorStrategy {

        private final String mLabel;

        private LabelIndicatorStrategy(String label) {
            mLabel = label;
        }

        public DrawableItem createIndicatorView() {
            final XTextArea tv = new XTextArea(getXContext(), mLabel, new RectF());
            tv.setTextAlign(Align.CENTER);
            tv.setTextColor(0xffffffff);
            tv.setTextSize(TABWIDGET_TEXT_SIZE);
            LauncherApplication app = (LauncherApplication) getContext().getApplicationContext();
            Drawable tabSelector = app.mLauncherContext.getDrawable(R.drawable.tab_widget_indicator_selector);
            tv.setBackgroundDrawable(tabSelector);
            tv.enableCache();
//            mTabWidget.addItem(tv);
            return tv;
        }
    }
        
    private class FactoryContentStrategy implements ContentStrategy {
        private DrawableItem mTabContent;
        private final String mTag;
        private TabContentFactory mFactory;

        public FactoryContentStrategy(String tag, TabContentFactory factory) {
            mTag = tag;
            mFactory = factory;
        }

        public DrawableItem getContentView() {
            if (mTabContent == null) {
                mTabContent = mFactory.createTabContent(mTag);
            }
            mTabContent.setVisibility(true);
            invalidate();
            return mTabContent;
        }

        public void tabClosed() {
            mTabContent.setVisibility(false);
            invalidate();            
        }
    }
    
    public void setCurrentTab(int index) {
        if (index < 0 || index >= mTabSpecs.size()) {
            return;
        }

        if (index == mCurrentTab) {
            return;
        }

        // notify old tab content
        if (mCurrentTab != -1) {
            mTabSpecs.get(mCurrentTab).mContentStrategy.tabClosed();
        }

        mCurrentTab = index;
        final XTabHost.TabSpec spec = mTabSpecs.get(index);

        // Call the tab widget's focusCurrentTab(), instead of just
        // selecting the tab.
        mTabWidget.focusCurrentTab(mCurrentTab);

        // tab content
        mCurrentView = spec.mContentStrategy.getContentView();

        if (mCurrentView.getParent() == null) {
//        	mCurrentView.localRect = mTabContent.localRect;
            mTabContent.addItem(mCurrentView);
//            mCurrentView.//
        }

//        if (!mTabWidget.hasFocus()) {
//            // if the tab widget didn't take focus (likely because we're in touch mode)
//            // give the current tab content view a shot
//            mCurrentView.requestFocus();
//        }

        //mTabContent.requestFocus(View.FOCUS_FORWARD);
        invokeOnTabChangeListener();
    }
    
    /**
     * Add a tab.
     * @param tabSpec Specifies how to create the indicator and content.
     */
    public void addTab(TabSpec tabSpec) {

        if (tabSpec.mIndicatorStrategy == null) {
            throw new IllegalArgumentException("you must specify a way to create the tab indicator.");
        }

        if (tabSpec.mContentStrategy == null) {
            throw new IllegalArgumentException("you must specify a way to create the tab content");
        }
        DrawableItem tabIndicator = tabSpec.mIndicatorStrategy.createIndicatorView();
//        tabIndicator.setOnKeyListener(mTabKeyListener);

        // If this is a custom view, then do not draw the bottom strips for
        // the tab indicators.
//        if (tabSpec.mIndicatorStrategy instanceof ViewIndicatorStrategy) {
//            mTabWidget.setStripEnabled(false);
//        }

        mTabWidget.addItem(tabIndicator);
        mTabSpecs.add(tabSpec);

        if (mCurrentTab == -1) {
            setCurrentTab(0);
        }
    }


    /**
     * Removes all tabs from the tab widget associated with this tab host.
     */
    public void clearAllTabs() {
        mTabWidget.clearAllItems();
        setup();
        mTabContent.clearAllItems();
        mTabSpecs.clear();
//        requestLayout();
        invalidate();
    }
    
    public void setCurrentTabByTag(String tag) {
        int i;
        for (i = 0; i < mTabSpecs.size(); i++) {
            if (mTabSpecs.get(i).getTag().equals(tag)) {
                setCurrentTab(i);
                break;
            }
        }
    }

private class XTabCell extends DrawableItem {
        
        private final Paint mPaint = new Paint();
        private FontMetrics fm = new FontMetrics();
        private static final String DOT = "...";
        private String mLabel = "";
        private String toDraw = "";
        private Bitmap mIcon;
        
        public XTabCell(XContext context, String label, Bitmap icon) {
        	super(context);
        	
            this.mLabel = label;
            this.mIcon = icon;
            mPaint.setTextAlign(Align.CENTER);
            mPaint.setAntiAlias(true);
}
        
        public void resize(RectF rect) {
            if (rect != null && rect.width() > 0 && rect.height() > 0) {
                this.resize(rect);
                updateText();
                this.invalidate();
            }
        }
        
        private void updateText() {
            toDraw = mLabel;
            toDraw.trim();
            int maxWidth = (int) (localRect.width() - mPaddingLeft - mPaddingRight);
            float textWidth = mPaint.measureText(toDraw);
            if (textWidth > maxWidth) {
                float max = maxWidth - mPaint.measureText(DOT);
                float[] widths = new float[toDraw.length()];
                int count = mPaint.getTextWidths(toDraw, widths);
                textWidth = 0f;
                for (int i = 0; i < count; i++) {
                    textWidth += widths[i];
                    if (textWidth > max) {
                        count = i;
                        break;
                    }
                }
                toDraw = new StringBuffer(toDraw.substring(0, count)).append(DOT).toString();
            }
        }
        
        @Override
        public void onDraw(IDisplayProcess c) {
            if (mIcon != null) {
                mPaint.getFontMetrics(fm);
                float fontHeight = fm.bottom - fm.top;
                float mTop = (getHeight() - mIcon.getHeight() - mDrawablePadding - fontHeight) / 2;
                float mLeft = (getWidth() - mIcon.getWidth()) / 2;
                c.drawBitmap(mIcon, mLeft, mTop, mPaint);
                mTop = getHeight() - mTop - fm.bottom;
                mPaint.setColor(textSetColor);
                c.drawText(toDraw, getWidth() / 2, mTop, mPaint);
            } else {
                mPaint.getFontMetrics(fm);
                float fontHeight = fm.bottom - fm.top;
                float mTop = (getHeight() + fontHeight) / 2 - fm.bottom;
                mPaint.setColor(textSetColor);
                c.drawText(toDraw, getWidth() / 2, mTop, mPaint);
            }
        }
    }
}
