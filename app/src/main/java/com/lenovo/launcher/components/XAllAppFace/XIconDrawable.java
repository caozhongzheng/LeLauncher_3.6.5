package com.lenovo.launcher.components.XAllAppFace;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;

import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;
import com.lenovo.launcher2.commoninterface.HolographicOutlineHelper;
import com.lenovo.launcher2.customizer.ShadowUtilites;
import com.lenovo.launcher2.customizer.TipsUtilities;
import com.lenovo.launcher2.customizer.Debug.R5;
import com.lenovo.launcher2.customizer.TipsUtilities.TipPoint;
import com.lenovo.launcher2.customizer.Utilities;

public class XIconDrawable extends DrawableItem {

	Bitmap iconBitmap;
	public static final int LEFT = 0x1000;
	public static final int RIGHT = 0x100;
	public static final int CENTER_HORIZONTAL = 0x1100;
	private static final int MASK_HORIZONTAL = 0x1100;

	public static final int TOP = 0x10;
    public static final int BOTTOM = 0x1;
    public static final int CENTER_VERTICAL = 0x11;
    private static final int MASK_VERTICAL = 0x11;

    private int alignMode = CENTER_HORIZONTAL | CENTER_VERTICAL;
    private Matrix extraMatrix = new Matrix();
    private Matrix extraMatrixCopy = new Matrix();

    /**
     * 图片尺寸的绘制方式. 填充整个容器.
     */
    public static final boolean FILLMODE_FILLCONTAINER = true;
    /**
     * 图片尺寸的绘制方式. 保持原图片尺寸.
     */
    public static final boolean FILLMODE_WRAPSRC = false;
    
    private boolean fillMode = FILLMODE_FILLCONTAINER;
    
//	private final Paint mBlurPaint = new Paint();
//	private final Paint mHolographicPaint = new Paint();
//	private static final BlurMaskFilter sThickOuterBlurMaskFilter;
//	private int[] mTempOffset = new int[2];
//	private static final MaskFilter sCoarseClipTable = TableMaskFilter.CreateClipTable(0, 200);
	//Bitmap blackshadow;
	private boolean enableWorkspaceShadow = false;
	public XIconDrawable(XContext context, Bitmap icon) {
		super(context);
		iconBitmap = icon;	
		float iconWidth = 0.0f;
		float iconHeight = 0.0f;
		if (iconBitmap != null) {
			iconWidth = iconBitmap.getWidth();
			iconHeight = iconBitmap.getHeight();
		}
		this.localRect = new RectF(0, 0, iconWidth, iconHeight);
//        mBlurPaint.setFilterBitmap(true);
//        mBlurPaint.setAntiAlias(true);
//        mHolographicPaint.setFilterBitmap(true);
//        mHolographicPaint.setAntiAlias(true);
		
		 invalidate();
	}

//	@Override
//	public Bitmap createFace() {
//		return iconBitmap;
//	}
	
    /**
     * 设置图片尺寸的绘制方式
     * @param fillmode One of {@link #FILLMODE_FILLCONTAINER} or {@link #FILLMODE_WRAPSRC}. <br/>default is {@link #FILLMODE_FILLCONTAINER}.
     */
    public void setFillMode(boolean fillmode) {
        this.fillMode = fillmode;
        updateAlign();
    }
    
    /**
     * 设置图片的对齐方式，仅在 FillMode = {@link #FILLMODE_WRAPSRC}下生效
     * @param alignMode default is {@link #CENTER_HORIZONTAL} | {@link #CENTER_VERTICAL}
     * @see #LEFT
     * @see #RIGHT
     * @see #CENTER_HORIZONTAL
     * @see #TOP
     * @see #BOTTOM
     * @see #CENTER_VERTICAL
     */
    public void setAlignMode(int alignMode) {
        if (alignMode > 0x1111) {
            throw new IllegalStateException("alignMode must be less than 0x1111");
        }
        this.alignMode = alignMode;
        updateAlign();
    }
    
    private void updateAlign() {
        if (iconBitmap != null && !iconBitmap.isRecycled()) {
            drawX = 0;
            drawY = 0;
            if (fillMode) {
                float scaleX = 1f, scaleY = 1f;
                scaleX = getWidth() / iconBitmap.getWidth();
                scaleY = getHeight() / iconBitmap.getHeight();
                extraMatrix.setScale(scaleX, scaleY);
            } else {
                int horizontal = alignMode & MASK_HORIZONTAL;
                switch (horizontal) {
                    case CENTER_HORIZONTAL:
                        drawX = (localRect.width() - iconBitmap.getWidth()) * .5f;
                        break;
                    case LEFT:
                        drawX = 0;
                        break;
                    case RIGHT:
                        drawX = localRect.width() - iconBitmap.getWidth();
                        break;
                    default:
                    	break;
                }
                
                int vertical = alignMode & MASK_VERTICAL;
                switch (vertical) {
                    case CENTER_VERTICAL:
                        drawY = (localRect.height() - iconBitmap.getHeight()) * .5f;
                        break;
                    case TOP:
                        drawY = 0;
                        break;
                    case BOTTOM:
                        drawY = localRect.height() - iconBitmap.getHeight();
                        break;
                    default:
                    	break;
                }
                
                extraMatrix.setTranslate(drawX, drawY);
            }
            extraMatrixCopy.set(extraMatrix);
        }
    }
	@Override
	public void onDraw(IDisplayProcess c) {
		 if (enableWorkspaceShadow) {
				Bitmap shadow = ShadowUtilites.shadows[ShadowUtilites.SHORTCUT_ICON_DRAWABLE];
				if ( shadow!= null && !shadow.isRecycled()) {
			        float shadowLeft = ((int)(localRect.width())>>1)
							- (shadow.getWidth() >> 1);
					float shadowTop = ((int)(localRect.height())>>1)
							- (shadow.getHeight() >> 1);
					extraMatrixCopy.setTranslate(shadowLeft, shadowTop);
						c.drawBitmap(
								shadow,
								shadowLeft,shadowTop,
								getPaint());
				}
			}

		 // OPT. NO NEED
//	    if (iconBitmap != null && !iconBitmap.isRecycled()) {
            c.drawBitmap(iconBitmap, extraMatrix, getPaint());
//        }
	   
	    /** RK_ID: RK_NEWFEATURE_SHOW_MISSED_NUMBER. AUT: zhanglz1 DATE: 2013-05-06 S */
		if (enableTip) {
			if (mTipBg != null && !mTipBg.isRecycled() && mTipPoint != null) {
				c.drawBitmap(mTipBg, mTipPoint.x, mTipPoint.y, getPaint());
			}
		}
	    /** RK_ID: RK_NEWFEATURE_SHOW_MISSED_NUMBER. AUT: zhanglz1 DATE: 2013-05-06 E */
		
	}
	public void setIconBitmap(Bitmap icon) {
		iconBitmap = icon;
		 /*
		float iconWidth = 0.0f;
		float iconHeight = 0.0f;
		if (iconBitmap != null) {
			iconWidth = iconBitmap.getWidth();
			iconHeight = iconBitmap.getHeight();
		}*/
//		resize(new RectF(0, 0, iconWidth, iconHeight));
		updateAlign();
		invalidate();
	}


	
//	public Bitmap getOuterBlur() {
//	    if (iconBitmap != null) {
//	        Bitmap glow = Bitmap.createBitmap((int) getParent().getWidth(), (int) getParent().getHeight(), Bitmap.Config.ARGB_8888);
//	        mBlurPaint.setMaskFilter(sThickOuterBlurMaskFilter);
//            Bitmap temp = iconBitmap.extractAlpha(mBlurPaint, mTempOffset);
//            // Use the clip table to make the glow heavier closer to the outline
//            mHolographicPaint.setMaskFilter(sCoarseClipTable);
//            mHolographicPaint.setAlpha(150);
//            mHolographicPaint.setColor(0xff33b5e5);
//            Canvas c = new Canvas(glow);
//            c.drawBitmap(temp, mTempOffset[0] + getRelativeX(), mTempOffset[1] + getRelativeY(), mHolographicPaint);
//            temp.recycle();
//            c.setBitmap(null);
//            return glow;
//        }
//	    return null;
//	}

    /* RK_ID: RK_TOUCHEVENT. AUT: liuli1 . DATE: 2012-12-21 . START */
    @Override
	public void resize(RectF rect) {
		super.resize(rect);
		
		updateAlign();

		invalidate();
    }
    
//    @Override
//    public void setPressed(boolean pressed) {
//    	super.setPressed(pressed);
//    	if(pressed){
//    		this.setAlpha(0.6f);    		
//    	}else{
//    		this.setAlpha(1f);
//    	}
//    }

//    @Override
//    public void resetPressedState() {
//    	R5.echo("resetPressedState" + this);
//        this.setAlpha(1.0f);
//        super.resetPressedState();
//    }

    @Override
    public boolean onFingerUp(MotionEvent e) {
//        resetPressedState();
        super.onFingerUp(e);
        return true;
    }

    @Override
    public boolean onShowPress(MotionEvent e) {
        super.onShowPress(e);
        return true;
    }

    @Override
    public boolean onFingerCancel(MotionEvent e) {
        super.onFingerCancel(e);
        return true;
    }
    /* RK_ID: RK_TOUCHEVENT. AUT: liuli1 . DATE: 2012-12-21 . END */
	
    //RK_TOUCH_SOUND dining 20130826 
    public boolean onSingleTapUp(MotionEvent e) {
		super.onSingleTapUp(e);
		return true;
	}
  //RK_TOUCH_SOUND dining 20130826 END
	public void recycleBitmap() {
		if (iconBitmap != null) {
			iconBitmap.recycle();
			iconBitmap = null;
		}
		super.clean();
	}

    /** RK_ID: RK_NEWFEATURE_SHOW_MISSED_NUMBER. AUT: zhanglz1 DATE: 2013-05-06 S */
	private Bitmap mTipBg = null;
	private TipPoint mTipPoint = null;
	private boolean enableTip = false;

	public void showTipForNewAdded(int num) {
		if (num > 0) {
			enableTip = true;
			if (mTipBg != null && !mTipBg.isRecycled()) {
				mTipBg.recycle();
			}
			mTipBg = TipsUtilities.getTipDrawable(num, getXContext());
			float width = mTipBg.getWidth();
			float height = mTipBg.getHeight();
			float parentRightX = this.getWidth();
			float childTopY = 0;
			float childRight = this.getRelativeX()
					+ this.getWidth();
			mTipPoint = TipsUtilities.getTipDrawableRelativeParentPosition(parentRightX,
			        childRight, childTopY, width, height,
					getXContext());
			invalidate();
		}
	}
	public void dismissTip() {
        enableTip = false;
        if (mTipBg != null && !mTipBg.isRecycled()) {
            mTipBg.recycle();
            mTipBg = null;
        }
        mTipPoint = null;
        invalidate();
    }
    /** RK_ID: RK_NEWFEATURE_SHOW_MISSED_NUMBER. AUT: zhanglz1 DATE: 2013-05-06 S */
	
	public void releaseLongPressed() {		
	}
	
	private float drawX = 0, drawY = 0;	
	public float getBitmapX(){
	    return drawX;
	}
	
    public float getBitmapY(){
        return drawY;
    }
    
	public void setShowWorkspaceShadow(boolean b,Context c) {
		enableWorkspaceShadow = b;
		/*if (blackshadow == null && SettingsValue.isShadow(c)) {
	      blackshadow = ShadowUtilites.createGlowingOutline(
							mContext.getContext(), null, mTempCanvas,
							mPressedGlowColor, mPressedOutlineColor);
		}*/
	}
	/*public void resetStandardBgForShadow(){
		blackshadow = null;
	}*/
	
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		super.onDown(e);
		return true;
	}
}
