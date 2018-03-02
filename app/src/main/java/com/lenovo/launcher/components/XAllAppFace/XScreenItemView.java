package com.lenovo.launcher.components.XAllAppFace;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.slimengine.BaseDrawableGroup;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;

public class XScreenItemView extends BaseDrawableGroup {
    
    private XIconDrawable mThumbnailDrawable;
    private XIconDrawable mDelDrawable;
    private XIconDrawable mHomeDrawable;
    private XIconDrawable mMiddleDrawable;
    
    private XContext mContext;
    private XScreenMngView.PreviewInfo infoLocal = null;
//    private static final float DEL_SCALE_FACTOR = 1.5f;
    
    private static int mWidthGap = -1;
    private static int mHeightGap = -1;
    private int mDeleteMargin;
    private int mHomeMarginBottom;
    
    public XScreenItemView(XScreenItemView iconView){
    	this(iconView.infoLocal, iconView.localRect, iconView.mContext);
    }
        
	public XScreenItemView(XScreenMngView.PreviewInfo info, RectF rect, XContext context) {
        super(context);
        if (rect == null) {
        	return;
        }
        mContext = context;
        
        infoLocal = info;
        
        if (mWidthGap == -1) {
            mWidthGap = context.getResources().getDimensionPixelOffset(R.dimen.xscreen_mng_width_gap);
        }
        if (mHeightGap == -1) {
            mHeightGap = context.getResources().getDimensionPixelOffset(R.dimen.xscreen_mng_height_gap);
        }
        mDeleteMargin = context.getResources().getDimensionPixelOffset(R.dimen.xscreen_mng_del_margin);
        mHomeMarginBottom = context.getResources().getDimensionPixelOffset(R.dimen.xscreen_mng_home_margin_bottom);
                
        mThumbnailDrawable = new XIconDrawable(context, info.mThumbnail);
        mThumbnailDrawable.resize(new RectF(0, 0, rect.width() - mWidthGap * 2, rect.height() - mHeightGap));///??        
        mThumbnailDrawable.setTouchable(true);        
        addItem(mThumbnailDrawable);
        
        if (info.mMiddleBitmap != null) {
        	mMiddleDrawable = new XIconDrawable(context, info.mMiddleBitmap);
        	mMiddleDrawable.resize(new RectF(0, 0, info.mMiddleBitmap.getWidth(), info.mMiddleBitmap.getHeight()));        
        	mMiddleDrawable.setTouchable(false);        
            addItem(mMiddleDrawable);
        }
        
        if (info.mDelBitmap != null) {
	        mDelDrawable = new XIconDrawable(context, info.mDelBitmap) {
	        	@Override
	        	public boolean onSingleTapUp(MotionEvent e) {
	        		super.onSingleTapUp(e);
	        		return true;
	        	}
	        };
	        mDelDrawable.setFillMode(XIconDrawable.FILLMODE_WRAPSRC);
	        
	        mDelDrawable.localRect.right = info.mDelBitmap.getWidth() + 2 * mDeleteMargin;
	        mDelDrawable.localRect.bottom = info.mDelBitmap.getHeight() + 2 * mDeleteMargin;
	        
//            mDelDrawable.setRelativeX(mWidthGap);
	        mDelDrawable.setRelativeX(localRect.width() - mWidthGap - mDelDrawable.getWidth());
            
	        mDelDrawable.setTouchable(true);        
	        addItem(mDelDrawable);
        }
        
        if (info.mHomeBitmap != null) {
        	mHomeDrawable = new XIconDrawable(context, info.mHomeBitmap) {
	        	@Override
	        	public boolean onSingleTapUp(MotionEvent e) {
	        		super.onSingleTapUp(e);
	        		return true;
	        	}
	        };
	        mHomeDrawable.setFillMode(XIconDrawable.FILLMODE_WRAPSRC);
	        mHomeDrawable.localRect.right = rect.width() - 2 * mWidthGap;
	        mHomeDrawable.localRect.bottom = getHomeDrawableHeight(info.mHomeBitmap, mHomeMarginBottom);
        	mHomeDrawable.setRelativeX((rect.width() - 2 * mWidthGap - mHomeDrawable.getWidth()) / 2);
        	mHomeDrawable.setRelativeY(rect.height() - mHeightGap - mHomeDrawable.getHeight());
        	mHomeDrawable.setTouchable(true);        
	        addItem(mHomeDrawable);
        }
        
        if (rect.width() > 0 && rect.height() > 0) {
			resize(rect);
        }
        
        setTag(info);
    }
	
	@Override
	public void resize(RectF rect) {
		super.resize(rect);
		
		mThumbnailDrawable.resize(new RectF(0, 0, rect.width() - 2 * mWidthGap, rect.height() - mHeightGap));
		mThumbnailDrawable.setRelativeX(mWidthGap);
		
		if (mMiddleDrawable != null) {
			mMiddleDrawable.resize(new RectF(0, 0, infoLocal.mMiddleBitmap.getWidth(), infoLocal.mMiddleBitmap.getHeight()));
			mMiddleDrawable.setRelativeX(mThumbnailDrawable.getRelativeX()
					+ (mThumbnailDrawable.getWidth() - mMiddleDrawable.getWidth()) / 2.0f);
			mMiddleDrawable.setRelativeY(mThumbnailDrawable.getRelativeY()
					+ (mThumbnailDrawable.getHeight() - mMiddleDrawable.getHeight()) / 2.0f);
		}
		
		if (mDelDrawable != null) {
			mDelDrawable.resize(new RectF(0, 0, mDelDrawable.getWidth(), mDelDrawable.getHeight()));
//			mDelDrawable.setRelativeX(mWidthGap);
			mDelDrawable.setRelativeX(localRect.width() - mWidthGap - mDelDrawable.getWidth());
        }
        
        if (mHomeDrawable != null) {
        	mHomeDrawable.resize(new RectF(0, 0, rect.width() - 2 * mWidthGap, mHomeDrawable.getHeight()));
        	mHomeDrawable.setRelativeX((rect.width() - 2 * mWidthGap - mHomeDrawable.getWidth()) / 2);
        	mHomeDrawable.setRelativeY(rect.height() - mHeightGap - mHomeDrawable.getHeight());
//        	mHomeDrawable.resize(new RectF(0,
//        			rect.height() - mHeightGap - mHomeDrawable.getHeight(),
//        			rect.width() - 2 * mWidthGap,
//        			rect.height() - mHeightGap));
        }
    }
	
	@Override
	public boolean onDown(MotionEvent e) {
	    super.onDown(e);
	    mThumbnailDrawable.setAlpha(.6f);
	    return false;
	}
	
	@Override
    public void resetPressedState() {
		mThumbnailDrawable.setAlpha(1f);
	    super.resetPressedState();
    }
    
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        super.onSingleTapUp(e);
        return true;
    }
    
    @Override
    public void onTouchCancel(MotionEvent e) {
    	resetPressedState();
    	super.onTouchCancel(e);
    }
    
    private XScreenMngView.PreviewInfo mTag;

    protected void setTag(XScreenMngView.PreviewInfo o) {
        mTag = o;
    }

    public XScreenMngView.PreviewInfo getTag() {
        return mTag;
    }

    @Override
    public boolean onFingerUp(MotionEvent e) {
        // fix bug , moving drag item to another page, up, pressed state not reset.
        resetPressedState();
        return super.onFingerUp(e);
    }
    
    @Override
    public void clean() {
        mContext = null;
        if (mThumbnailDrawable != null) {
        	mThumbnailDrawable.clean();
        }
        if (mHomeDrawable != null) {
        	mHomeDrawable.clean();
        }
        if (mDelDrawable != null) {
        	mDelDrawable.clean();
        }
        if (mMiddleDrawable != null) {
        	mMiddleDrawable.clean();
        }
        mThumbnailDrawable = null;
        mHomeDrawable = null;
        mDelDrawable = null;
        mMiddleDrawable = null;
        super.clean();
    }
	
	public XScreenMngView.PreviewInfo getLocalInfo(){
		return infoLocal;
	}
	
	public XIconDrawable getThumbnailDrawable() {
		return mThumbnailDrawable;
	}
	
	public XIconDrawable getDelDrawable() {
		return mDelDrawable;
	}
	
	public XIconDrawable getHomeDrawable() {
		return mHomeDrawable;
	}
	
	public void resetHomeDrawable(boolean editMode) {
    	mHomeDrawable.setIconBitmap(infoLocal.mHomeBitmap);
    	
    	mHomeDrawable.setFillMode(XIconDrawable.FILLMODE_WRAPSRC);
    	mHomeDrawable.localRect.left = 0;
    	mHomeDrawable.localRect.top = 0;
        mHomeDrawable.localRect.right = localRect.width() - 2 * mWidthGap;
        mHomeDrawable.localRect.bottom = getHomeDrawableHeight(infoLocal.mHomeBitmap, mHomeMarginBottom);
        
        mHomeDrawable.resize(new RectF(0, 0, localRect.width() - 2 * mWidthGap, mHomeDrawable.getHeight()));
        
    	mHomeDrawable.setRelativeX((localRect.width() - 2 * mWidthGap - mHomeDrawable.getWidth()) / 2);
    	mHomeDrawable.setRelativeY(localRect.height() - mHeightGap - mHomeDrawable.getHeight());
    	
    	mHomeDrawable.setVisibility(!editMode);
	}
	
	public void resetThumbDrawable() {
		resetThumbDrawable(infoLocal.mThumbnail);
	}
	
	public void resetThumbDrawable(Bitmap thumbnail) {
		infoLocal.mThumbnail = thumbnail;
		mThumbnailDrawable.setIconBitmap(thumbnail);
        mThumbnailDrawable.resize(new RectF(0, 0, localRect.width() - 2 * mWidthGap, localRect.height() - mHeightGap));
        mThumbnailDrawable.setRelativeX(mWidthGap);
	}
	
	public void resetAllItems(boolean editMode) {
		final XScreenMngView.PreviewInfo info = infoLocal;
        
        mThumbnailDrawable.setIconBitmap(info.mThumbnail);
        mThumbnailDrawable.resize(new RectF(0, 0, localRect.width() - 2 * mWidthGap, localRect.height() - mHeightGap));
        mThumbnailDrawable.setRelativeX(mWidthGap);
        
        if (info.mMiddleBitmap == null) {
        	if (mMiddleDrawable != null) {
        		removeItem(mMiddleDrawable);
        		mMiddleDrawable = null;
        	}
        } else {
        	if (mMiddleDrawable == null) {
        		mMiddleDrawable = new XIconDrawable(mContext, info.mMiddleBitmap);    	        
    	        mMiddleDrawable.setTouchable(false);        
    	        addItem(mMiddleDrawable);
        	} else {
        		mMiddleDrawable.setIconBitmap(info.mMiddleBitmap);
        	}
        	mMiddleDrawable.resize(new RectF(0, 0, infoLocal.mMiddleBitmap.getWidth(), infoLocal.mMiddleBitmap.getHeight()));
			mMiddleDrawable.setRelativeX(mThumbnailDrawable.getRelativeX()
					+ (mThumbnailDrawable.getWidth() - mMiddleDrawable.getWidth()) / 2.0f);
			mMiddleDrawable.setRelativeY(mThumbnailDrawable.getRelativeY()
					+ (mThumbnailDrawable.getHeight() - mMiddleDrawable.getHeight()) / 2.0f);
        }
        
        if (info.mDelBitmap == null) {
        	if (mDelDrawable != null) {
        		removeItem(mDelDrawable);
        		mDelDrawable = null;
        	}
        } else {
        	if (mDelDrawable == null) {
        		mDelDrawable = new XIconDrawable(mContext, info.mDelBitmap) {
    	        	@Override
    	        	public boolean onSingleTapUp(MotionEvent e) {
    	        		super.onSingleTapUp(e);
    	        		return true;
    	        	}
    	        };
    	        
    	        mDelDrawable.setTouchable(true);        
    	        addItem(mDelDrawable);
        	} else {
        		mDelDrawable.setIconBitmap(info.mDelBitmap);
        	}
        	mDelDrawable.setFillMode(XIconDrawable.FILLMODE_WRAPSRC);
	        
	        mDelDrawable.localRect.right = info.mDelBitmap.getWidth() + 2 * mDeleteMargin;
	        mDelDrawable.localRect.bottom = info.mDelBitmap.getHeight() + 2 * mDeleteMargin;
	        mDelDrawable.localRect.left = 0;
	        mDelDrawable.localRect.top = 0;
	       
	        mDelDrawable.resize(new RectF(0, 0, mDelDrawable.getWidth(), mDelDrawable.getHeight()));
//            mDelDrawable.setRelativeX(mWidthGap);
	        mDelDrawable.setRelativeX(localRect.width() - mWidthGap - mDelDrawable.getWidth());
            mDelDrawable.setRelativeY(0);
            
            mDelDrawable.setVisibility(!editMode);
        }
        
        if (info.mHomeBitmap == null) {
        	if (mHomeDrawable != null) {
        		removeItem(mHomeDrawable);
        		mHomeDrawable = null;
        	}
        } else {
        	if (mHomeDrawable == null) {
        		mHomeDrawable = new XIconDrawable(mContext, info.mHomeBitmap) {
    	        	@Override
    	        	public boolean onSingleTapUp(MotionEvent e) {
    	        		super.onSingleTapUp(e);
    	        		return true;
    	        	}
    	        };
    	        mHomeDrawable.setTouchable(true);        
    	        addItem(mHomeDrawable);
        	} else {
        		mHomeDrawable.setIconBitmap(info.mHomeBitmap);
        	}
        	mHomeDrawable.setFillMode(XIconDrawable.FILLMODE_WRAPSRC);
        	mHomeDrawable.localRect.left = 0;
        	mHomeDrawable.localRect.top = 0;
            mHomeDrawable.localRect.right = localRect.width() - 2 * mWidthGap;
            mHomeDrawable.localRect.bottom = getHomeDrawableHeight(infoLocal.mHomeBitmap, mHomeMarginBottom);
            
            mHomeDrawable.resize(new RectF(0, 0, localRect.width() - 2 * mWidthGap, mHomeDrawable.getHeight()));
            
        	mHomeDrawable.setRelativeX((localRect.width() - 2 * mWidthGap - mHomeDrawable.getWidth()) / 2);
        	mHomeDrawable.setRelativeY(localRect.height() - mHeightGap - mHomeDrawable.getHeight());
        	
        	mHomeDrawable.setVisibility(!editMode);
        }
	}
	
	public static int getHomeDrawableHeight(Bitmap bitmap, int padding) {
		if (bitmap == null) {
			return 0;
		}
		int ret = bitmap.getHeight() + 2 * padding;
		return ret;
	}
	
	@Override
	public void onDraw(IDisplayProcess c) {
//		Paint p = this.getPaint();
//		int color = Color.argb(130 , (int)(255 * mTest), (int)(255 * mTest), (int)(255 * mTest));
//		p.setColor(color);
//		c.drawRect(localRect, p);
		
		super.onDraw(c);
	}
	
	public static int getWidthGap(Context context) {
		if (mWidthGap == -1) {
			mWidthGap = context.getResources().getDimensionPixelOffset(R.dimen.xscreen_mng_width_gap);
		}
		return mWidthGap;
	}
	
	public static int getHeightGap(Context context) {
		if (mHeightGap == -1) {
			mHeightGap = context.getResources().getDimensionPixelOffset(R.dimen.xscreen_mng_height_gap);
		}
		return mHeightGap;
	}
}
