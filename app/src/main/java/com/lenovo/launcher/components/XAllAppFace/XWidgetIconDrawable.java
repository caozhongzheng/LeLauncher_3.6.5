package com.lenovo.launcher.components.XAllAppFace;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;
import com.lenovo.launcher2.customizer.Debug;

public class XWidgetIconDrawable extends DrawableItem {

	private Drawable mDrawable;
	private Matrix mDrawMatrix = null;
	private Matrix mMatrix;
	private boolean onLongPressed = false;
	
	private ScaleType mScaleType;
	private float mDrawableWidth = 0.0f;
	private float mDrawableHeight = 0.0f;
	private RectF mTempSrc = new RectF();
    private RectF mTempDst = new RectF();
    
    private Bitmap mDrawableCache = null;
	
	public XWidgetIconDrawable(XContext context, Drawable icon) {
		super(context);		
		mScaleType  = ScaleType.FIT_CENTER;
		mMatrix = new Matrix();
		
		mDrawable = icon;
		if (icon != null) {
			mDrawableWidth = icon.getIntrinsicWidth();
			mDrawableHeight = icon.getIntrinsicHeight();
		}
		this.localRect = new RectF(0, 0, mDrawableWidth, mDrawableHeight);		
		 invalidate();
	}
	
	@Override
	public void onDraw(IDisplayProcess canvas) {
		 // OPT. NO NEED
		super.onDraw(canvas);

        if (mDrawable == null) {
            return; // couldn't resolve the URI
        }

        if (mDrawableWidth == 0 || mDrawableHeight == 0) {
            return;     // nothing to draw (empty bounds)
        }
        
        if (Float.compare(alpha, 1.0f) == 0) {
        	drawContent(canvas);
    	} else {
    		drawCache(canvas);
    	}
	}
	
	private void drawContent(final IDisplayProcess canvas) {
		if (mDrawMatrix == null && mPaddingTop == 0 && mPaddingLeft == 0) {
			mDrawable.draw(canvas.getCanvas());
		} else {
			int saveCount = canvas.getCanvas().getSaveCount();
            canvas.save();
            
            canvas.translate(mPaddingLeft, mPaddingTop);

            if (mDrawMatrix != null) {
                canvas.concat(mDrawMatrix);
            }
            mDrawable.draw(canvas.getCanvas());
            canvas.getCanvas().restoreToCount(saveCount);
		}
		if (mDrawableCache != null && !mDrawableCache.isRecycled()) {
    		mDrawableCache.recycle();
		}
    	mDrawableCache = null;
	}
	
	private void drawCache(final IDisplayProcess canvas) {
		if (mDrawableCache == null || mDrawableCache.isRecycled()) {
			mDrawableCache = renderDrawableToBitmap(mDrawable, 0, 0);
		}
		if (mDrawableCache == null) {
			drawContent(canvas);
		} else {
			canvas.drawBitmap(mDrawableCache, mDrawMatrix, getPaint());
		}
	}
	
	public void setIconDrawable(Drawable icon) {
		mDrawable = icon;
		mDrawable = icon;
		if (icon != null) {
			mDrawableWidth = icon.getIntrinsicWidth();
			mDrawableHeight = icon.getIntrinsicHeight();
		}
		configureBounds();
		invalidate();
	}

    /* RK_ID: RK_TOUCHEVENT. AUT: liuli1 . DATE: 2012-12-21 . START */
    @Override
	public void resize(RectF rect) {
		super.resize(rect);
		
		configureBounds();

		invalidate();
    }

    @Override
    public boolean onFingerUp(MotionEvent e) {
        resetPressedState();
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
    
    @Override
    public void clean() {
    	mDrawable = null;
    	if (mDrawableCache != null && !mDrawableCache.isRecycled()) {
    		mDrawableCache.recycle();
		}
    	mDrawableCache = null;
    	super.clean();
    }
	
	public void releaseLongPressed() {
    	onLongPressed = false;
    	resetPressedState();
	}
	
	private float drawX = 0, drawY = 0;	
	public float getBitmapX(){
	    return drawX;
	}
	
    public float getBitmapY(){
        return drawY;
    }
    
	@Override
	public boolean onDown(MotionEvent e) {
	    super.onDown(e);
	    setAlpha(.6f);
	    return true;
	}
    
    @Override
    public void resetPressedState() {
    	if (!onLongPressed) {
    	    setAlpha(1.0f);
    	}
    	if (isPressed()) {
            setPressed(false);
        }
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
    
    public void setScaleType(ScaleType scaleType) {
        if (scaleType == null) {
            throw new NullPointerException();
        }

        if (mScaleType != scaleType) {
            mScaleType = scaleType;

            configureBounds();
            invalidate();
        }
    }
    
    private void configureBounds() {
        if (mDrawable == null) {
            return;
        }

        int dwidth = (int)mDrawableWidth;
        int dheight = (int)mDrawableHeight;

        int vwidth = (int)(localRect.width() - mPaddingLeft - mPaddingRight);
        int vheight = (int)(localRect.height() - mPaddingTop - mPaddingBottom);

        boolean fits = (dwidth < 0 || vwidth == dwidth) &&
                       (dheight < 0 || vheight == dheight);

        if (dwidth <= 0 || dheight <= 0 || ScaleType.FIT_XY == mScaleType) {
            /* If the drawable has no intrinsic size, or we're told to
                scaletofit, then we just fill our entire view.
            */
            mDrawable.setBounds(0, 0, vwidth, vheight);
            mDrawMatrix = null;
        } else {
            // We need to do the scaling ourself, so have the drawable
            // use its native size.
            mDrawable.setBounds(0, 0, dwidth, dheight);

            if (ScaleType.MATRIX == mScaleType) {
                // Use the specified matrix as-is.
                if (mMatrix.isIdentity()) {
                    mDrawMatrix = null;
                } else {
                    mDrawMatrix = mMatrix;
                }
            } else if (fits) {
                // The bitmap fits exactly, no transform needed.
                mDrawMatrix = null;
            } else if (ScaleType.CENTER == mScaleType) {
                // Center bitmap in view, no scaling.
                mDrawMatrix = mMatrix;
                mDrawMatrix.setTranslate((int) ((vwidth - dwidth) * 0.5f + 0.5f),
                                         (int) ((vheight - dheight) * 0.5f + 0.5f));
            } else if (ScaleType.CENTER_CROP == mScaleType) {
                mDrawMatrix = mMatrix;

                float scale;
                float dx = 0, dy = 0;

                if (dwidth * vheight > vwidth * dheight) {
                    scale = (float) vheight / (float) dheight; 
                    dx = (vwidth - dwidth * scale) * 0.5f;
                } else {
                    scale = (float) vwidth / (float) dwidth;
                    dy = (vheight - dheight * scale) * 0.5f;
                }

                mDrawMatrix.setScale(scale, scale);
                mDrawMatrix.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f));
            } else if (ScaleType.CENTER_INSIDE == mScaleType) {
                mDrawMatrix = mMatrix;
                float scale;
                float dx;
                float dy;
                
                if (dwidth <= vwidth && dheight <= vheight) {
                    scale = 1.0f;
                } else {
                    scale = Math.min((float) vwidth / (float) dwidth,
                            (float) vheight / (float) dheight);
                }
                
                dx = (int) ((vwidth - dwidth * scale) * 0.5f + 0.5f);
                dy = (int) ((vheight - dheight * scale) * 0.5f + 0.5f);

                mDrawMatrix.setScale(scale, scale);
                mDrawMatrix.postTranslate(dx, dy);
            } else {
                // Generate the required transform.
                mTempSrc.set(0, 0, dwidth, dheight);
                mTempDst.set(0, 0, vwidth, vheight);
                
                mDrawMatrix = mMatrix;
                mDrawMatrix.setRectToRect(mTempSrc, mTempDst, scaleTypeToScaleToFit(mScaleType));
            }
        }
    }
    
    private static final Matrix.ScaleToFit[] sS2FArray = {
        Matrix.ScaleToFit.FILL,
        Matrix.ScaleToFit.START,
        Matrix.ScaleToFit.CENTER,
        Matrix.ScaleToFit.END
    };

    private static Matrix.ScaleToFit scaleTypeToScaleToFit(ScaleType st)  {
        // ScaleToFit enum to their corresponding Matrix.ScaleToFit values
        return sS2FArray[st.nativeInt - 1];
    }
    
    public Matrix getImageMatrix() {
        if (mDrawMatrix == null) {
            return new Matrix(Matrix.IDENTITY_MATRIX);
        }
        return mDrawMatrix;
    }

    public void setImageMatrix(Matrix matrix) {
        // collaps null and identity to just null
        if (matrix != null && matrix.isIdentity()) {
            matrix = null;
        }
        
        // don't invalidate unless we're actually changing our matrix
        if (matrix == null && !mMatrix.isIdentity() ||
                matrix != null && !mMatrix.equals(matrix)) {
            mMatrix.set(matrix);
            configureBounds();
            invalidate();
        }
    }
    
    private Bitmap renderDrawableToBitmap(Drawable d, int x, int y) {
    	Bitmap bitmap = null;
        if (d != null) {
        	int w = d.getIntrinsicWidth();
        	int h = d.getIntrinsicHeight();
        	try {
        	    bitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        	} catch (OutOfMemoryError e) {
        		Debug.printException("renderDrawableToBitmap---------", e);
        	}
        	if (bitmap == null) {
        		return null;
        	}
            Canvas c = new Canvas(bitmap);
            Rect oldBounds = d.copyBounds();
            d.setBounds(x, y, x + w, y + h);
            d.draw(c);
            d.setBounds(oldBounds);
            c.setBitmap(null);
        }
        return bitmap;
    }
    
    /**
     * Options for scaling the bounds of an image to the bounds of this view.
     */
    public enum ScaleType {
        /**
         * Scale using the image matrix when drawing. The image matrix can be set using
         * {@link ImageView#setImageMatrix(Matrix)}. From XML, use this syntax:
         * <code>android:scaleType="matrix"</code>.
         */
        MATRIX      (0),
        /**
         * Scale the image using {@link Matrix.ScaleToFit#FILL}.
         * From XML, use this syntax: <code>android:scaleType="fitXY"</code>.
         */
        FIT_XY      (1),
        /**
         * Scale the image using {@link Matrix.ScaleToFit#START}.
         * From XML, use this syntax: <code>android:scaleType="fitStart"</code>.
         */
        FIT_START   (2),
        /**
         * Scale the image using {@link Matrix.ScaleToFit#CENTER}.
         * From XML, use this syntax:
         * <code>android:scaleType="fitCenter"</code>.
         */
        FIT_CENTER  (3),
        /**
         * Scale the image using {@link Matrix.ScaleToFit#END}.
         * From XML, use this syntax: <code>android:scaleType="fitEnd"</code>.
         */
        FIT_END     (4),
        /**
         * Center the image in the view, but perform no scaling.
         * From XML, use this syntax: <code>android:scaleType="center"</code>.
         */
        CENTER      (5),
        /**
         * Scale the image uniformly (maintain the image's aspect ratio) so
         * that both dimensions (width and height) of the image will be equal
         * to or larger than the corresponding dimension of the view
         * (minus padding). The image is then centered in the view.
         * From XML, use this syntax: <code>android:scaleType="centerCrop"</code>.
         */
        CENTER_CROP (6),
        /**
         * Scale the image uniformly (maintain the image's aspect ratio) so
         * that both dimensions (width and height) of the image will be equal
         * to or less than the corresponding dimension of the view
         * (minus padding). The image is then centered in the view.
         * From XML, use this syntax: <code>android:scaleType="centerInside"</code>.
         */
        CENTER_INSIDE (7);
        
        ScaleType(int ni) {
            nativeInt = ni;
        }
        final int nativeInt;
    }
}
