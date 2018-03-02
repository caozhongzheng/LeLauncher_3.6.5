package com.lenovo.launcher.components.XAllAppFace;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.MotionEvent;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.SimpleItemInfo;
import com.lenovo.launcher.components.XAllAppFace.slimengine.BaseDrawableGroup;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;
import com.lenovo.launcher2.commoninterface.IconCache;
import com.lenovo.launcher2.commoninterface.ItemInfo;

public class XIconTextView extends BaseDrawableGroup {
    DrawableItem mIconBg;
    XIconDrawable mIconDrawable;
    XTextArea mTextView;
    DrawableItem mMark;
    
    private ItemInfo mInfo;

    private XContext mContext;
    
    private float mWidthGap;
    private float mHeightGap;
    
    private int mImageSize;
    private int mImagePaddingTop;
    private int mImagePaddingLeft;
    private int mImagePaddingRight;
    private int mImagePaddingBottom;
    
    private int mTextSize;
    private int mTextWidthOffset;
    private int mTextPaddingBottom;
    private int mBgWidth;
    private int mBgHeight;
    
    private Bitmap mIconBitmap;

    public XIconTextView(XContext context, RectF rect) {
        super(context);
        
        if (rect == null || rect.width() <= 0 || rect.height() <= 0) {
        	return;
        }
        resize(rect);
        
        mContext = context;
        
        Resources res = context.getResources();
        mImageSize = res.getDimensionPixelSize(R.dimen.app_icon_size);
        mImagePaddingLeft = res.getDimensionPixelOffset(R.dimen.xscreen_mng_tab_apps_image_padding_left);
        mImagePaddingTop = res.getDimensionPixelOffset(R.dimen.xscreen_mng_tab_apps_image_padding_top);
        mImagePaddingRight = res.getDimensionPixelOffset(R.dimen.xscreen_mng_tab_apps_image_padding_right);
        mImagePaddingBottom = res.getDimensionPixelOffset(R.dimen.xscreen_mng_tab_apps_image_padding_bottom);
        
        mTextSize = res.getDimensionPixelSize(R.dimen.xscreen_mng_tab_apps_text_size);
        mTextPaddingBottom = res.getDimensionPixelOffset(R.dimen.xscreen_mng_tab_apps_text_padding_bottom);
        mTextWidthOffset = res.getDimensionPixelOffset(R.dimen.xscreen_mng_tab_apps_text_width_offset);
        
        mBgWidth = mImageSize + mImagePaddingLeft + mImagePaddingRight;
        mBgHeight = mImageSize + mImagePaddingTop + mImagePaddingBottom;
        
        mWidthGap = (rect.width() - mBgWidth) / 2.0f;
        mHeightGap = rect.height() - mBgHeight;
    }
    
    public XIconTextView(XScreenShortcutInfo info, RectF rect, IconCache cache, XContext context) {
    	this(context, rect);
    	
        mInfo = info;
        setTouchable(false);
        
        mIconBg = new DrawableItem(mContext);
        mIconBg.resize(new RectF(mWidthGap, 0, getWidth() - mWidthGap, getHeight() - mHeightGap));
//        mIconBg.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.xscreen_tab_content_app_bg));
        
        mIconBitmap = info.getIcon(cache, false);
        mIconDrawable = new XIconDrawable(mContext, mIconBitmap) {
        	private boolean onLongPressed = false;
            @Override
        	public boolean onDown(MotionEvent e) {
        	    super.onDown(e);
        	    mIconDrawable.setAlpha(.6f);
        	    return true;
        	}
            
            @Override
            public void resetPressedState() {
            	if (!onLongPressed) {
            	    mIconDrawable.setAlpha(1.0f);
            	}
            	if (isPressed()) {
                    setPressed(false);
                }
            }
            
            @Override
            public boolean onFingerUp(MotionEvent e) {
            	resetPressedState();
            	return super.onFingerUp(e);
            }
            
            @Override
            public void onTouchCancel(MotionEvent e) {
            	resetPressedState();
            	super.onTouchCancel(e);
            }
            
            @Override
            public boolean onLongPress(MotionEvent e) {
            	onLongPressed = true;
            	return super.onLongPress(e);
            }
            
            @Override
            public void releaseLongPressed() {
            	onLongPressed = false;
            	resetPressedState();
            	super.releaseLongPressed();
            }
        };
        mIconDrawable.setFillMode(XIconDrawable.FILLMODE_WRAPSRC);
        mIconDrawable.setTouchable(true);

        mTextView = new XTextArea(mContext, info.title.toString(), new RectF(0, 0,
        		mBgWidth - mTextWidthOffset, mTextSize));
        mTextView.setTouchable(false);
        
        Drawable mark = mContext.getResources().getDrawable(R.drawable.xscreen_item_checked);
        mMark = new DrawableItem(mContext);
        mMark.setBackgroundDrawable(mark);
        mMark.resize(new RectF(0, 0, mark.getIntrinsicWidth(), mark.getIntrinsicHeight()));
        mMark.setVisibility(info.checked);

        layoutChildren();
        addItem(mIconBg);
        addItem(mIconDrawable);
        addItem(mTextView);
        addItem(mMark);
    }
    
    @Override
	public void resize(RectF rect) {
		super.resize(rect);
    }

    void setup(SimpleItemInfo info, IconCache cache, PackageManager pm) {
    	mInfo = info;
    	if (info == null || info.resolveInfo == null) {
    		return;
    	}
    	setTouchable(false);
    	
    	mIconBg = new DrawableItem(mContext);
        mIconBg.resize(new RectF(mWidthGap, 0, getWidth() - mWidthGap, getHeight() - mHeightGap));
//        mIconBg.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.xscreen_tab_content_app_bg));
    	
        // get bitmap
        ComponentName componentName = new ComponentName(info.resolveInfo.activityInfo.packageName,
        		info.resolveInfo.activityInfo.name);
        mIconBitmap = cache.getIcon(componentName, info.resolveInfo, null);

        mIconDrawable = new XIconDrawable(mContext, mIconBitmap) {
        	private boolean onLongPressed = false;
            @Override
        	public boolean onDown(MotionEvent e) {
        	    super.onDown(e);
        	    mIconDrawable.setAlpha(.6f);
        	    return true;
        	}
            
            @Override
            public void resetPressedState() {
            	if (!onLongPressed) {
            	    mIconDrawable.setAlpha(1.0f);
            	}
            	if (isPressed()) {
                    setPressed(false);
                }
            }
            
            @Override
            public boolean onLongPress(MotionEvent e) {
            	onLongPressed = true;
            	return super.onLongPress(e);
            }
            
            @Override
            public void releaseLongPressed() {
            	onLongPressed = false;
            	resetPressedState();
            	super.releaseLongPressed();
            }
        };
        mIconDrawable.setFillMode(XIconDrawable.FILLMODE_WRAPSRC);
        mIconDrawable.setTouchable(true);

        mTextView = new XTextArea(mContext, info.resolveInfo.loadLabel(pm).toString(), new RectF(0, 0,
        		mBgWidth - mTextWidthOffset, mTextSize));
        mTextView.setTouchable(false);
        
        layoutChildren();
        
        addItem(mIconBg);
        addItem(mIconDrawable);
        addItem(mTextView);        
    }
    
    public void setChecked(boolean checked) {
    	if (mMark != null) {
    		mMark.setVisibility(checked);
    	}
    }

    private void layoutChildren() {
    	mIconDrawable.resize(new RectF(0, 0, mImageSize, mImageSize));
        mIconDrawable.setRelativeX(mWidthGap + mImagePaddingLeft);
        mIconDrawable.setRelativeY(mImagePaddingTop);
    	
        initAttrTextView(mTextView);
        mTextView.setRelativeX(mWidthGap + mTextWidthOffset / 2.0f);
        mTextView.setRelativeY(getHeight() - mHeightGap - mTextPaddingBottom - mTextView.getHeight());
        
        if (mMark != null) {
        	mMark.setRelativeX(getWidth() - mMark.getWidth());
        	mMark.setRelativeY(0);
        }
    }

    void initAttrTextView(XTextArea view) {
        // copy from XTextView.java
        view.setTextAlign(Align.CENTER);
        view.setEllipsize(TextUtils.TruncateAt.END);
        view.setTextSize(mTextSize);
        view.setTextColor(0xffffffff);
    }
    
    public ItemInfo getInfo() {
    	return mInfo;
    }
    
    public XIconDrawable getIconDrawable() {
    	return mIconDrawable;
    }    
	
	@Override
	public void clean() {
		mIconBitmap = null;
		clearAllItems();
		super.clean();
	}
}
