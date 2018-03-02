package com.lenovo.launcher.components.XAllAppFace;

import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.XWidgetIconDrawable.ScaleType;
import com.lenovo.launcher.components.XAllAppFace.slimengine.BaseDrawableGroup;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;
import com.lenovo.launcher2.addleoswidget.LenovoWidgetsProviderInfo;
import com.lenovo.launcher2.commoninterface.IconCache;
import com.lenovo.launcher2.customizer.FastBitmapDrawable;

public class XWidgetTextView extends BaseDrawableGroup {
    XWidgetIconDrawable mIconDrawable;
    DrawableItem mIconBg;
    XTextArea mTextView;
    XTextArea mSpanTextView;

    private XContext mContext;
    
    private int mWidthGap;
    private int mHeightGap;
    
    private float mImageWidth;
    private float mImageHeight = -1;
    private int mImagePadding;
    
    private int mTextSize;
    private int mTextPaddingTop;
    private int mTextPaddingHori;
    
    private static final float TEXT_WIDTH_SCALE = 0.7f;
    
    private Drawable mWidgetBg = null;
    private Drawable mWidgetDrawable = null;

    public XWidgetTextView(XContext context, RectF rect) {
        super(context);
        this.setTouchable(false);
        
        if (rect == null || rect.width() <= 0 || rect.height() <= 0) {
        	return;
        }
        resize(rect);

        mContext = context;
        mWidthGap = mContext.getResources().getDimensionPixelOffset(R.dimen.xscreen_mng_tab_widgets_width_gap);
        mHeightGap = mContext.getResources().getDimensionPixelOffset(R.dimen.xscreen_mng_tab_widgets_height_gap);
        
        mImagePadding = mContext.getResources().getDimensionPixelOffset(R.dimen.xscreen_mng_tab_widgets_image_padding);
        
        mTextSize = mContext.getResources().getDimensionPixelOffset(R.dimen.xscreen_mng_tab_widgets_text_size);
        mTextPaddingTop = mContext.getResources().getDimensionPixelOffset(R.dimen.xscreen_mng_tab_widgets_text_padding_top);
        mTextPaddingHori = mContext.getResources().getDimensionPixelOffset(R.dimen.xscreen_mng_tab_widgets_text_padding_horizontal);
        
        mWidgetBg = mContext.getResources().getDrawable(R.drawable.xscreen_tab_content_bg);
        
        mImageWidth = getWidth() - 2.0f * (mWidthGap + mImagePadding);
    }

    private void layoutChildren() {
        initAttrTextView(mTextView);
        
        mIconDrawable.setRelativeX(mWidthGap + mImagePadding);
        mIconDrawable.setRelativeY(getHeight() - mHeightGap - mImagePadding - mImageHeight);
        
        mTextView.setRelativeX(mWidthGap + mTextPaddingHori);
        mTextView.setRelativeY(mTextPaddingTop);

        if (mSpanTextView != null) {
            initAttrTextView(mSpanTextView);
            mSpanTextView.setRelativeX(getWidth() - mWidthGap - mTextPaddingHori - mSpanTextView.getWidth());
            mSpanTextView.setRelativeY(mTextPaddingTop);
        }
    }

    void setup(LenovoWidgetsProviderInfo item) {
    	mIconBg = new DrawableItem(mContext);
    	mIconBg.resize(new RectF(mWidthGap, 0, getWidth() - mWidthGap, getHeight() - mHeightGap));
    	mIconBg.setBackgroundDrawable(mWidgetBg);
    	mIconBg.setTouchable(false);
    	
        float textWidth = (mIconBg.getWidth() - mTextPaddingHori * 2) * TEXT_WIDTH_SCALE;
        initLable(item.appName, textWidth);
        initSpan(item.x + " X " + item.y, textWidth);

        if (mImageHeight == -1) {
    		mImageHeight = mIconBg.getHeight() - 2 * mImagePadding - mTextView.getHeight() - mTextPaddingTop;
    	}
        mWidgetDrawable = item.icon;
        initIcon(mWidgetDrawable);
        
        layoutChildren();
        
        addItem(mIconBg);
        addItem(mIconDrawable);
        addItem(mTextView);
        addItem(mSpanTextView);
    }
    
    void setup(AppWidgetProviderInfo item, int[] spanXY) {
    	mIconBg = new DrawableItem(mContext);
    	mIconBg.resize(new RectF(mWidthGap, 0, getWidth() - mWidthGap, getHeight() - mHeightGap));
    	mIconBg.setBackgroundDrawable(mWidgetBg);
    	mIconBg.setTouchable(false);
    	
        float textWidth = (mIconBg.getWidth() - mTextPaddingHori * 2) * TEXT_WIDTH_SCALE;
        initLable(item.label, textWidth);
        initSpan(spanXY[0] + " X " + spanXY[1], textWidth);

        mWidgetDrawable = getWidgetDrawable(item);
        initIcon(mWidgetDrawable);
//        initIcon(null);
        
        layoutChildren();
        
        addItem(mIconBg);
        addItem(mIconDrawable);
        addItem(mTextView);
        addItem(mSpanTextView);
    }
    
    Drawable getWidgetDrawable(AppWidgetProviderInfo info) {
    	Drawable drawable = null;
    	String packageName = info.provider.getPackageName();
        if (mImageHeight == -1) {
    		mImageHeight = mIconBg.getHeight() - 2 * mImagePadding - mTextView.getHeight() - mTextPaddingTop;
    	}
        if (info.previewImage != 0) {
            try {
				drawable = mContext.getContext().getPackageManager().getDrawable(packageName, info.previewImage, null);
			} catch (OutOfMemoryError e) {
				//TODO add default error face here
			}
        }
        
        if (drawable == null) {       	
        	try {
                if (info.icon > 0) drawable = mContext.getContext().getPackageManager().getDrawable(packageName, info.icon, null);
            } catch (Resources.NotFoundException e) {}
        	if (drawable == null) drawable = mContext.getResources().getDrawable(R.drawable.ic_launcher_application);
        }
    	return drawable;
    }

    private void initSpan(String string, float textWidth) {
        mSpanTextView = new XTextArea(mContext, string, new RectF(0, 0,
                mIconBg.getWidth() - 2 * mTextPaddingHori - textWidth, mTextSize));
        mSpanTextView.setPadding(0, 0, 2, 0);
        mSpanTextView.enableCache();
        mSpanTextView.setTextAlign(Align.RIGHT);
        mSpanTextView.setTouchable(false);
    }

    private void initLable(String name, float textWidth) {
        mTextView = new XTextArea(mContext, name, new RectF(0, 0, textWidth, mTextSize));
        mTextView.enableCache();
        mTextView.setTextAlign(Align.LEFT);
        mTextView.setTouchable(false);
    }

    void initIcon(Drawable icon) {
    	if (mImageHeight == -1) {
    		mImageHeight = mIconBg.getHeight() - 2 * mImagePadding - mTextView.getHeight() - mTextPaddingTop;
    	}
        mIconDrawable = new XWidgetIconDrawable(mContext, icon);
        mIconDrawable.setScaleType(ScaleType.CENTER_INSIDE);
        mIconDrawable.setTouchable(true);
        mIconDrawable.resize(new RectF(0, 0, mImageWidth, mImageHeight));
    }

    void setup(int icon, int label, int type) {
        Resources res = mContext.getResources();
        mIconBg = new DrawableItem(mContext);
    	mIconBg.resize(new RectF(mWidthGap, 0, getWidth() - mWidthGap, getHeight() - mHeightGap));
    	mIconBg.setBackgroundDrawable(mWidgetBg);
    	mIconBg.setTouchable(false);

        float textWidth = 0;
        String labelStr = res.getString(label);
        if (type == SimpleItemInfo.ACTION_TYPE_ADD_OTHER_WIDGET) {
        	Paint paint = new Paint();
        	paint.setTextSize(mTextSize);
        	textWidth = paint.measureText(labelStr);
        } else {
        	textWidth = (mIconBg.getWidth() - mTextPaddingHori * 2) * TEXT_WIDTH_SCALE;
        }
        initLable(labelStr, textWidth);

        if (type == SimpleItemInfo.ACTION_TYPE_ADD_FOLDER) {
            initSpan("1 X 1", textWidth);
        }
        
        mWidgetDrawable = res.getDrawable(icon);
        initIcon(mWidgetDrawable);

        layoutChildren();
        
        addItem(mIconBg);
        addItem(mIconDrawable);
        addItem(mTextView);
        if (type == SimpleItemInfo.ACTION_TYPE_ADD_FOLDER) {
            addItem(mSpanTextView);
        }
    }
    
    void setup(SimpleItemInfo info, IconCache cache, PackageManager pm) {
    	if (info.resolveInfo == null) {
    		return;
    	}
        
        mIconBg = new DrawableItem(mContext);
    	mIconBg.resize(new RectF(mWidthGap, 0, getWidth() - mWidthGap, getHeight() - mHeightGap));
    	mIconBg.setBackgroundDrawable(mWidgetBg);
    	mIconBg.setTouchable(false);
    	
        float textWidth = (mIconBg.getWidth() - mTextPaddingHori * 2) * TEXT_WIDTH_SCALE;
        initLable(info.resolveInfo.loadLabel(pm).toString(), textWidth);
        initSpan(info.spanX + " X " + info.spanY, textWidth);

        ComponentName componentName = new ComponentName(info.resolveInfo.activityInfo.packageName,
        		info.resolveInfo.activityInfo.name);
        mWidgetDrawable = new FastBitmapDrawable(cache.getIcon(componentName, info.resolveInfo, null));
        initIcon(mWidgetDrawable);
//        initIcon(null);
        
        layoutChildren();
        
        addItem(mIconBg);
        addItem(mIconDrawable);
        addItem(mTextView);
        addItem(mSpanTextView);
    }

    void initAttrTextView(XTextArea view) {
        // copy from XTextView.java
//        view.setPadding(10, 2, 10, 2);
        view.setTextSize(mTextSize);
        view.setTextColor(0xffffffff);
    }
    
    public XWidgetIconDrawable getIconDrawable() {
    	return mIconDrawable;
    }

    void layoutIconDrawable(Drawable icon, Object info, XScreenContentTabHost host) {
        if (mIconDrawable != null) {
            if (icon != null && mWidgetDrawable != icon) {
            	mWidgetDrawable = icon;
            }

            mIconDrawable.setIconDrawable(icon);
            mIconDrawable.setRelativeX(mWidthGap + mImagePadding);
            mIconDrawable.setRelativeY(getHeight() - mHeightGap - mImagePadding - mImageHeight);
        }
    }

	@Override
	public void clean() {
		mWidgetDrawable = null;
		clearAllItems();		
		super.clean();
	}
}
