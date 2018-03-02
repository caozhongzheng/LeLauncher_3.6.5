package com.lenovo.launcher.components.XAllAppFace.slimengine;


import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.lenovo.launcher2.customizer.Debug.R5;


public class DrawableItem implements Callback{

    private static final boolean DEBUG = false;
    private static final String TAG = "L3";
	private Bitmap faceToDraw;
	private Paint paint;

    /*** MODIFYBY: zhaoxy . DATE: 2012-11-19 . START***/
    protected int mPaddingLeft = 0;
    protected int mPaddingRight = 0;
    protected int mPaddingTop = 0;
    protected int mPaddingBottom = 0;

    protected int maxWidth = Integer.MAX_VALUE;
    protected int maxHeight = Integer.MAX_VALUE;
    protected int minWidth = 0;
    protected int minHeight = 0;
    /*** MODIFYBY: zhaoxy . DATE: 2012-11-19 . END***/

    /*** RK_ID: TEST_TOUCH.  AUT: zhaoxy . DATE: 2013-02-18 . START***/
	private DrawableItem parent;
	/*** RK_ID: TEST_TOUCH.  AUT: zhaoxy . DATE: 2013-02-18 . END***/
	private RectF globalTouchRect = new RectF();
	public RectF localRect = new RectF();
	private RectF clipRect = new RectF();
	private Matrix matrix = new Matrix();
	private Matrix globalToLocalMatrix = new Matrix();
	private final Matrix tempM = new Matrix();
	private boolean invertMatrixDirty = false;

	protected boolean touched;

	protected boolean removeTag = false;
	private boolean isVisible = true;
	private boolean isBackgroundVisible = true;
	protected boolean cacheDirtyTag = true;
	private boolean dirtyTag = true;
	protected boolean isRecycled = false;

	private boolean caculatingRect = false;

    private boolean touchable = true;
    
    private boolean enableCache = false;
    private OnClickListener mOnClickListener;
    private OnLongClickListener mOnLongClickListener;

	protected float alpha = 1.0f;
	protected float finalAlpha = 1.0f;
	protected int finalAlphaInt = 255;
	private boolean bgAlphaDirty = false;

	public void setBgAlphaDirty() {
	    bgAlphaDirty = true;
	}

	private boolean needPromoteDrawingOrder = false;

	protected final XContext mContext;
	
	
	public XContext getXContext(){
		return mContext;
	}
	
	// extra touch rect
	private RectF mExtraTouchBounds;
	public void setExtraTouchBounds( RectF extraTouchBounds ){
		mExtraTouchBounds = new RectF( extraTouchBounds );
		
		if(parent != null){
			RectF tmpTouchBounds = new RectF(parent.localRect);
			tmpTouchBounds.union(extraTouchBounds);
			parent.setExtraTouchBounds( tmpTouchBounds );
		}
	}
	
	public void resetTouchBounds(){
		if(mExtraTouchBounds != null)
			mExtraTouchBounds.setEmpty();
		if(parent != null){
			parent.resetTouchBounds();
		}
		mExtraTouchBounds = null;
	}
	
	public RectF getExtraTouchBounds(){
		return mExtraTouchBounds;
	}
	
	public boolean hasExtraTouchBounds(){
		return mExtraTouchBounds != null && !mExtraTouchBounds.isEmpty();
	}

	// focus
	private boolean desiredTouchEvent = false;
	public boolean isDesiredTouchEventItem(){
		return desiredTouchEvent;
	}
	public void desireTouchEvent( boolean desire ){
		desiredTouchEvent = desire;
		if( parent != null ){
			parent.desireTouchEvent(desire);
		}
	}
	//
	
	// tag for listening visible change action
	private boolean wantKnowVisibleState = false;
	protected boolean wantKnowVisibleState(){
		return wantKnowVisibleState;
	} 
	protected void wantKnowVisibleState(boolean wanted){
		wantKnowVisibleState = wanted;
	}
	
	public DrawableItem(XContext context) {
		mContext = context;

		paint = new Paint();
	}

	public void promoteDrawingOrder(boolean promote) {
//		android.util.Log.i("Z", "promote drawing order for : " + this
//				+ " , value is : " + promote);
		needPromoteDrawingOrder = promote;

		// if( promote ){
		// sNeedCheckBringToFront = true;
		// }
	}

	public boolean needPromoteDrawingOrder() {
		return needPromoteDrawingOrder;
	}
    
    public boolean isCached() {
        return this.enableCache;
    }
    
    public void enableCache() {
        enableCache = true;
        setCacheDirty(true);
        updateFinalAlpha();
        setBgAlphaDirty();
    }
    
    public void disableCache() {
        enableCache = false;
        updateFinalAlpha();
        if (faceToDraw != null) {
            if (!faceToDraw.isRecycled()) {
                faceToDraw.recycle();
            }
            faceToDraw = null;
        }
        setBgAlphaDirty();
    }
	
	public boolean isCacheDirty() {
		return cacheDirtyTag;
	}

	public void setCacheDirty(boolean dirtyTag) {
		this.cacheDirtyTag = dirtyTag;
	}
	
	public void setInvertMatrixDirty() {
	    this.invertMatrixDirty = true;
	}
	
	public boolean isInvertMatrixDirty() {
	    return this.invertMatrixDirty;
	}
	
    public Matrix getInvertMatrix() {
        Matrix resMatrix = this.globalToLocalMatrix;
        if (invertMatrixDirty) {
            resMatrix = new Matrix();
            this.getInvMatrixRecursive(this, resMatrix);
            this.matrix.invert(tempM);
            resMatrix.postConcat(tempM);
            this.globalToLocalMatrix = resMatrix;
            invertMatrixDirty = false;
        }
        return resMatrix;
    }
	
    private DrawableItem getInvMatrixRecursive(DrawableItem current, Matrix currentMatrix) {
        if (current.getParent() != null) {
            if (current.getParent().isInvertMatrixDirty()) {
                DrawableItem res = this.getInvMatrixRecursive(current.getParent(), currentMatrix);
                if (!res.getMatrix().isIdentity()) {
                    res.getMatrix().invert(tempM);
                    currentMatrix.postConcat(tempM);
                }
                res.globalToLocalMatrix.set(currentMatrix);
                res.invertMatrixDirty = false;
                currentMatrix.postTranslate(-res.getRelativeX(), -res.getRelativeY());
            } else {
                Matrix parentLocalToGlobal = current.getParent().getInvertMatrix();
                currentMatrix.postConcat(parentLocalToGlobal);
                currentMatrix.postTranslate(-current.getParent().getRelativeX(), -current.getParent().getRelativeY());
            }
        }
        return current;
    }

	public Matrix getMatrix() {
		return matrix;
	}

	public void setRemoveTag(boolean removeTag) {
		this.removeTag = removeTag;
	}

	public boolean isNeedRemove() {
		return this.removeTag;
	}

	public void setFace(Bitmap faceToSet) {
		if (faceToSet != null) {
			enableCache();
			setCacheDirty( false );
            if (faceToDraw != null && !faceToDraw.isRecycled()) {
                faceToDraw.recycle();
                faceToDraw = null;
            }
			faceToDraw = faceToSet;
		}
	}

	public void updateMatrix(Matrix matrix) {

		// android.util.Log.i("matrix", "will set matrix to " + this.toString()
		// + "  , globalTouchRect:  " + globalTouchRect.flattenToString());

		this.matrix.set(matrix);
		this.matrix.invert(globalToLocalMatrix);
		this.setInvertMatrixDirty();
		//this.setDirty(true);
		//calculateGlobalTouchRect();

		// android.util.Log.i("matrix", "After set matrix to " + this.toString()
		// + "  , globalTouchRect:  " + globalTouchRect.flattenToString());

	}

	public void setPaint(Paint paint) {
		this.paint = paint;
	}
	
	public Paint getPaint() {
		return this.paint;
	}

	public boolean isVisible() {
		return this.isVisible;
	}

	public void setVisibility(boolean visibility) {
		this.isVisible = visibility;
		
		if(mVisibilityListener != null){
			mVisibilityListener.onVisibilityChange(this, visibility);
		}
	}
	
	public void setTouchable(boolean touchable) {
	    this.touchable = touchable;
	}
	
	public boolean isTouchable() {
	    return this.touchable;
	}

	public boolean isTouched() {
		return touched;
	}

	public Bitmap getCurrentFace() {
		return faceToDraw;
	}

	public RectF getClipRect() {
		return clipRect;
	}

	public void setClipRect(RectF r) {
		clipRect = new RectF(r);
	}
	
	public float getRelativeX() {
	    return localRect.left;
	}
	
	public float getRelativeY() {
	    return localRect.top;
	}
	
	public float getWidth() {
	    return localRect.width();
	}
	
	public float getHeight() {
	    return localRect.height();
	}

	public void setRelativeX(float x) {
	    this.localRect.offsetTo(x, this.localRect.top);
//		calculateGlobalTouchRect();
	    setInvertMatrixDirty();
	}
	
	public void setRelativeY(float y) {
        this.localRect.offsetTo(this.localRect.left, y);
//		calculateGlobalTouchRect();
        setInvertMatrixDirty();
    }

	public void offsetRelative(float deltaX, float deltaY) {
	    this.localRect.offset(deltaX, deltaY);
	    setInvertMatrixDirty();
	}

//	public void setDrawingFace(Bitmap face) {
//		Bitmap tmpBmp = faceToDraw;
//		faceToDraw = face;
//		calculateGlobalTouchRect();
//		if (tmpBmp != null && !tmpBmp.isRecycled()) {
//			// tmpBmp.recycle();
//			tmpBmp = null;
//		}
//	}

	public RectF getGlobalTouchRect() {
		return globalTouchRect;
	}

	public float getGlobalX() {
		return globalTouchRect != null ? globalTouchRect.left : 0.0f;
	}

	public float getGlobalX2(){
		float x = getRelativeX();
		if(getParent() != null){
			x += getParent().getRelativeX();
		}
		
		return x;
	}
	
	public float getGlobalY2(){
		float y = getRelativeY();
		if(getParent() != null){
			y += getParent().getRelativeY();
		}
		
		return y;
	}
	
	
	public float getGlobalY() {
		return globalTouchRect != null ? globalTouchRect.top : 0.0f;
	}

	public void calculateGlobalTouchRect() {

		if (caculatingRect) {
//			android.util.Log.i("rd", "caculating rect return");
			return;
		}

		caculatingRect = true;

		if (globalTouchRect == null) {
			globalTouchRect = new RectF();
		}

		boolean faceNull = faceToDraw == null;
		globalTouchRect.left = parent != null ? parent.getGlobalTouchRect().left + localRect.left : 0f;
		globalTouchRect.top = parent != null ? parent.getGlobalTouchRect().top + localRect.top : 0f;
		globalTouchRect.right = faceNull ? globalTouchRect.left + localRect.width()
				: globalTouchRect.left + faceToDraw.getWidth();
		globalTouchRect.bottom = faceNull ? globalTouchRect.top + localRect.height()
				: globalTouchRect.top + faceToDraw.getHeight();
		if (matrix != null) {
			matrix.mapRect(globalTouchRect);
		}

		caculatingRect = false;
	}

	public void setTouched(boolean touched) {
		this.touched = touched;
	}

	/*** RK_ID: TEST_TOUCH.  AUT: zhaoxy . DATE: 2013-02-18 . START***/
	public void setPrent(DrawableItem prent) {
		this.parent = prent;

		if (parent != null) {
		    setInvertMatrixDirty();
			calculateGlobalTouchRect();
			this.clipRect = parent.getClipRect();
		}
	}

	public DrawableItem getParent() {
		return this.parent;
	}
	/*** RK_ID: TEST_TOUCH.  AUT: zhaoxy . DATE: 2013-02-18 . END***/

	protected Bitmap createFace() {
		
		int width = (int)localRect.width();
		int height = (int)localRect.height();
		if( width <= 0 || height <= 0 ){
			return null;
		}
		
		Bitmap face = Bitmap.createBitmap( width, height, Config.ARGB_8888);
		
		NormalDisplayProcess tmpProc = new NormalDisplayProcess();

		tmpProc.beginDisplay(face);

		/*** RK_ID: fixbug.  AUT: zhaoxy . DATE: 2013-02-18 . START***/
        float alphaTemp = this.alpha;
        this.alpha = 1f;
        this.finalAlpha = 1f;
        if (paint != null)
            paint.setAlpha(255);
        onDraw(tmpProc);
        this.alpha = alphaTemp;
		/*** RK_ID: fixbug.  AUT: zhaoxy . DATE: 2013-02-18 . END***/
		Bitmap res = Bitmap.createBitmap(face);
		face.recycle();
		face = null;
		setCacheDirty(false);
		return res;
	}

   /*** RK_ID: XFOLDER. AUT: zhaoxy . DATE: 2013-01-22 . START ***/
	public Bitmap getSnapshot(float scale) {
	    int width = (int)localRect.width();
        int height = (int)localRect.height();
        if( width <= 0 || height <= 0 ){
            return null;
        }
        
        Bitmap face = Bitmap.createBitmap( width, height, Config.ARGB_8888);
        
        NormalDisplayProcess tmpProc = new NormalDisplayProcess();

        tmpProc.beginDisplay(face);
        
        /*** RK_ID: fixbug.  AUT: zhaoxy . DATE: 2013-02-18 . START***/
        //the alpha of paint do not need to store, it will refresh each updateFinalAlpha() invoke.
//        int alpha = 0;
//        if (paint != null) {
//            alpha = paint.getAlpha();
//        }
            paint.setAlpha((int) (255 * this.alpha));
            if (isBackgroundVisible && mBGDrawable != null) {
                mBGDrawable.setBounds(0, 0, (int)localRect.width(), (int)localRect.height());
                mBGDrawable.draw(tmpProc.getCanvas());
//                tmpProc.drawDrawable(mBGDrawable, localRect);
            }
            onDraw(tmpProc);
//        if (paint != null) {
//            paint.setAlpha(alpha);
//        }
        /*** RK_ID: fixbug.  AUT: zhaoxy . DATE: 2013-02-18 . END***/
        
        if (scale > 0.9999 && scale < 1.0001)
        {
            R5.echo("getSnapshot 1");
            tmpProc.getCanvas().setBitmap(null);
            tmpProc.endDisplay();
            return face;
        }
        
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap res = Bitmap.createBitmap(face, 0, 0, width, height, matrix,
                true);
        
        tmpProc.getCanvas().setBitmap(null);
        tmpProc.endDisplay();
        face.recycle();
        face = null;
        
        return res;
	}
   /*** RK_ID: XFOLDER. AUT: zhaoxy . DATE: 2013-01-22 . END ***/

	public void onDraw(IDisplayProcess c) {
	}

	public void draw(IDisplayProcess c) {

		if (!isVisible()) {
			return;
		}
		
		updateMatrix();

		if (enableCache) {
            if (faceToDraw == null) {
			faceToDraw = createFace();
//			calculateGlobalTouchRect();
            } else {
                if (isCacheDirty()) {
                    if (!faceToDraw.isRecycled()) {
                        faceToDraw.recycle();
                        faceToDraw = null;
                    }
                    faceToDraw = createFace();
                }
		}

		if (faceToDraw != null && matrix != null) {
                c.save();
                if (!matrix.isIdentity()) {
                    c.concat(matrix);
                }
                    /*if (parent != null) {
				c.translate(parent.localRect.left, parent.localRect.top);
                    }*/
//				c.clipRect(localRect);
				if (DEBUG) Log.d(TAG, "draw paint alpha = " + paint.getAlpha());
				
				updateFinalAlpha();
             /* RK_ID: RK_BACKGROUND. AUT: liuli1 . DATE: 2012-11-16 . START */
				if (isBackgroundVisible && mBGDrawable != null) {
		            c.drawDrawable(mBGDrawable, localRect);
		        }
				/* RK_ID: RK_BACKGROUND. AUT: liuli1 . DATE: 2012-11-16 . END */

				if (DEBUG) {
                    paint.setColor(0xffff0000);
                    paint.setAlpha(100);
                    paint.setStyle(android.graphics.Paint.Style.STROKE);
                    c.drawRect(localRect, paint);
                    paint.setAlpha(this.finalAlphaInt);
                    paint.setStyle(Style.FILL);
                }

				c.drawBitmap(faceToDraw, localRect.left, localRect.top, paint);
				
				
/*				c.save();
				paint.setStyle(Style.STROKE);
				paint.setColor(Color.RED);
				paint.setAlpha(100);
				c.drawRect(localRect, paint);
				paint.setStyle(Style.FILL);
				if(hasExtraTouchBounds()){
					paint.setAlpha(100);
					paint.setColor(Color.GREEN);
					paint.setStyle(Style.FILL);
					c.drawRect(mExtraTouchBounds, paint);
				}
				c.restore();*/
				
                c.restore();
            }
        } else {
                c.save();
                if (matrix != null && !matrix.isIdentity()) {
                    c.concat(matrix);
                }
                /*if (parent != null) {
                c.translate(parent.localRect.left, parent.localRect.top);
                }*/
//                c.clipRect(localRect);

                updateFinalAlpha();
                /* RK_ID: RK_BACKGROUND. AUT: liuli1 . DATE: 2012-11-16 . START */
                if (mBGDrawable != null) {
                    c.drawDrawable(mBGDrawable, localRect);
                }
                /* RK_ID: RK_BACKGROUND. AUT: liuli1 . DATE: 2012-11-16 . END */

                if (DEBUG) {
                    paint.setColor(0xffff0000);
                    paint.setAlpha(100);
                    paint.setStyle(android.graphics.Paint.Style.STROKE);
                    c.drawRect(localRect, paint);
                    paint.setAlpha(this.finalAlphaInt);
                    paint.setStyle(android.graphics.Paint.Style.FILL);
                }

                c.translate(localRect.left, localRect.top);
                onDraw(c);
                
/*				c.save();
				paint.setStyle(Style.STROKE);
				paint.setColor(Color.RED);
				paint.setAlpha(100);
				c.drawRect(localRect, paint);
				paint.setStyle(Style.FILL);
				if(hasExtraTouchBounds()){
					paint.setColor(Color.GREEN);
					paint.setAlpha(100);
					paint.setStyle(Style.FILL);
					c.drawRect(mExtraTouchBounds, paint);
				}
				c.restore();*/
                 
                c.restore();
        }


	}
	
	public boolean isRecycled() {
	    return this.isRecycled;
	}

	public void clean() {
	    this.isRecycled = true;

       /*** RK_ID: XFOLDER. AUT: zhaoxy . DATE: 2013-01-22 . START ***/
	    parent = null;
       /*** RK_ID: XFOLDER. AUT: zhaoxy . DATE: 2013-01-22 . END ***/
//		if (faceToDraw != null && !faceToDraw.isRecycled()) {
//			faceToDraw.recycle();
//		}
//		faceToDraw = null;
//		globalTouchRect = null;
//		matrix = null;
//		paint = null;
	}
	
	public void destory() {
	    clean();
	}

   /*** RK_ID: XFOLDER. AUT: zhaoxy . DATE: 2013-01-22 . START ***/
	public void reuse() {
	    this.isRecycled = false;
	}
   /*** RK_ID: XFOLDER. AUT: zhaoxy . DATE: 2013-01-22 . END ***/
	
//	public IController setController(IController controller) {
//        IController oldController = this.controller;
//        this.controller = controller;
//        return oldController;
//    }
	
//	public IController getController() {
//        return controller;
//    }
	
//	public void updateItem(long timeDelta) {
//        if (controller != null){
//            controller.update(timeDelta);
//        }
//    }
	
	/*** RK_ID: RK_SLIDEEFFECT  AUT: zhaoxy . DATE: 2012-12-10 . START***/
//	public int getAlpha() {
//	    if (paint != null) {
//	        return paint.getAlpha();
//	    } else {
//	        return 255;
//	    }
//	}
	/*** RK_ID: RK_SLIDEEFFECT  AUT: zhaoxy . DATE: 2012-12-10 . END***/

    /*public void setAlpha(int alpha) {
//        R2.echo(this + "before setAlpha" + alpha);
        if (paint != null) {
//            R2.echo(this + "setAlpha       " + alpha);
            paint.setAlpha(alpha);
            invalidate(!enableCache);
        }
        else
        {
            R2.echo("setAlpha failure because mTabHost null");
        }
    }*/
    
    public void setAlpha(float alpha) {
        if (alpha >= 0f && alpha <= 1.0001f) {
            this.alpha = alpha;
//            invalidate(!enableCache);
        }
    }
    
    public float getAlpha() {
        return this.alpha;
    }

    private boolean enableExtraAlpha = false;
    private float extraAlpha = 1f;
    
    public void setExtraAlphaEnable(boolean enable) {
        enableExtraAlpha = enable;
    }

    public void setExtraAlpha(float extraAlpha) {
        this.extraAlpha = extraAlpha;
    }

    public float getParentAlpha() {
        return parent == null ? 1f : parent.getFinalAlpha();
    }

    public void updateFinalAlpha() {
        if (enableExtraAlpha) {
            this.finalAlpha = getParentAlpha() * alpha * extraAlpha;
        } else {
            this.finalAlpha = getParentAlpha() * alpha;
        }
        /*** RK_ID: fixbug.  AUT: zhaoxy . DATE: 2013-02-18 . START***/
        final int newAlpha = (int) (255 * this.finalAlpha);
        if (this.finalAlphaInt != newAlpha || bgAlphaDirty) {
            this.finalAlphaInt = newAlpha;
            if (mBGDrawable != null) {
                mBGDrawable.setAlpha(this.finalAlphaInt);
            }
        }
        paint.setAlpha(this.finalAlphaInt);
        /*** RK_ID: fixbug.  AUT: zhaoxy . DATE: 2013-02-18 . END***/
    }
    
    public float getFinalAlpha() {
        return this.finalAlpha;
    }

	public boolean onDown(MotionEvent e) {
//	    R5.echo("onDown " + this);
		if (!isTouchable()) {
			return false;
		}
		
//		setPressed(true);
		delayCheckForTap();
		return false;
	}

	public boolean onShowPress(MotionEvent e) {
		if (!isTouchable()) {
			return false;
		}

//		setPressed(true);
		delayCheckForTap();
	    return false;
	}
	
	public boolean onSingleTapUp(MotionEvent e) {
		if (!isTouchable()) {
			return false;
		}
		
       invokeOnClickListener();
		return false;
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY, float previousX, float previousY) {
		if (!isTouchable()) {
			return false;
		}
		
        if(Math.abs(distanceY) > 20 || Math.abs(distanceX) > 20){
            resetPressedState(true);
        }
        				
		return false;
	}
		
	public boolean onLongPress(MotionEvent e) {
		if (!isTouchable()) {
			return false;
		}
		return invokeOnLongClickListener();
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (!isTouchable()) {
			return false;
		}
		
		return false;
	}

	public boolean onFingerUp(MotionEvent e) {
		if (!isTouchable()) {
			return false;
		}
		
	    /* RK_ID: RK_BACKGROUND. AUT: liuli1 . DATE: 2012-11-16 . START */
		checkForDoTap();			
		return false;
		/* RK_ID: RK_BACKGROUND. AUT: liuli1 . DATE: 2012-11-16 . END */
	}
	
	public void onTouchCancel( MotionEvent e ) {
		if (!isTouchable()) {
			return;
		}
		resetPressedState(true);
	}
	
	public boolean onDoubleTapped( MotionEvent e, DrawableItem item){
		return false;
	}

	public void invalidate() {
	    invalidate(true);
	}

	public void invalidate(boolean cached) {
	    if (enableCache && cached) {
            setCacheDirty(true);
        }
		mContext.getRenderer().invalidate();
	}
	
	public void invalidateAtOnce() {
	    if (enableCache) {
            setCacheDirty(true);
        }
		mContext.getRenderer().invalidateAtOnce();
	}
	
	public void interruptLongPressed() {
		mContext.getExchangee().interruptLongPress();
	}
	
	public static void echo(String msg) {
	    Log.d(TAG, msg);
	}

	public float mRotationY = 0;
	public float mRotationX = 0;
	float mPivotY = 400f;
	float mPivotX = 240;
	float mScaleX = 1f;
	float mScaleY = 1f;
	float mRotation = 0f;
	float mTranslationX = 0f;
	float mTranslationY = 0f;
	boolean mMatrixDirty = false;
	
    private Camera mCamera = null;
    private Matrix matrix3D = null;
    
    public void setRotationX(float rotationX) {
        if (mRotationX != rotationX) {
            mRotationX = rotationX;
            mMatrixDirty = true;
        }
    }
	
    public void setRotationY(float rotationY) {
        
        if (mRotationY != rotationY) {
            mRotationY = rotationY; // force another invalidation with the new orientation
            mMatrixDirty = true;         
        }
    }
    
    public void setRotation(float rotation) {
        if (mRotation != rotation) {
            mRotation = rotation;
            mMatrixDirty = true;
        }
    }
    
    public void setScaleX(float scaleX) {
        if (mScaleX != scaleX) {
            mScaleX = scaleX;
            mMatrixDirty = true;
        }
    }
    
    public void setScaleY(float scaleY) {
        if (mScaleY != scaleY) {
            mScaleY = scaleY;
            mMatrixDirty = true;
        }
    }
    
    boolean mPivotSet = false;
    
    public void updateMatrix() {
        if (mMatrixDirty) {
            if (!mPivotSet) {
                mPivotX = (localRect.right - localRect.left) / 2f;
                mPivotY = (localRect.bottom - localRect.top) / 2f;
//                R2.echo("mPivotX = " + mPivotX + "mPivotY = " + mPivotY);                
                mPivotSet = true;
            }
            
            matrix.reset();
            if (!nonzero(mRotationX) && !nonzero(mRotationY)) {
                matrix.setTranslate(mTranslationX, mTranslationY);
                matrix.preRotate(mRotation, mPivotX, mPivotY);
                matrix.preScale(mScaleX, mScaleY, mPivotX, mPivotY);
            } else {
                if (mCamera == null) {
                    mCamera = new Camera();
                    matrix3D = new Matrix();
                }
                mCamera.save();
                matrix3D.reset();
                matrix.preScale(mScaleX, mScaleY, mPivotX, mPivotY);
                mCamera.rotate(mRotationX, mRotationY, -mRotation);
                mCamera.getMatrix(matrix3D);
                matrix3D.preTranslate(-mPivotX, -mPivotY);
                matrix3D.postTranslate(mPivotX + mTranslationX,
                        mPivotY + mTranslationY);
                matrix.postConcat(matrix3D);
                mCamera.restore();
            }
            mMatrixDirty = false;
            invalidate(false);
        }
    }
    
    private static final float NONZERO_EPSILON = .001f;
    
    private static boolean nonzero(float value) {
        return (value < -NONZERO_EPSILON || value > NONZERO_EPSILON);
    }

    /* RK_ID: RK_BACKGROUND. AUT: liuli1 . DATE: 2012-11-16 . START */
    private Drawable mBGDrawable;
    private Object mTag;

    int mPrivateFlags;
    private int[] mDrawableState = null;
    static final ThreadLocal<Rect> sThreadLocal = new ThreadLocal<Rect>();

    public void setPadding(int left, int top, int right, int bottom) {
        boolean changed = false;

        // Common case is there are no scroll bars.
        if (mPaddingLeft != left) {
            changed = true;
            mPaddingLeft = left;
        }
        if (mPaddingTop != top) {
            changed = true;
            mPaddingTop = top;
        }
        if (mPaddingRight != right) {
            changed = true;
            mPaddingRight = right;
        }
        if (mPaddingBottom != bottom) {
            changed = true;
            mPaddingBottom = bottom;
        }

        if (changed) {
            invalidate();// requestLayout();
        }
    }

    public Drawable getBackgroundDrawable() {
        return mBGDrawable;
    }

    public void setBackgroundDrawable(Drawable d) {
        if (d == mBGDrawable) {
            return;
        }

        /*
         * Regardless of whether we're setting a new background or not, we want to clear the
         * previous drawable.
         */
        if (mBGDrawable != null) {
            mBGDrawable.setCallback(null);
        }

        if (d != null) {
            Rect padding = sThreadLocal.get();
            if (padding == null) {
                padding = new Rect();
                sThreadLocal.set(padding);
            }
            if (d.getPadding(padding)) {
                setPadding(padding.left, padding.top, padding.right, padding.bottom);
            }

            d.setCallback(this);
            if (d.isStateful()) {
                d.setState(getDrawableState());
            }
            mBGDrawable = d;
            mBGDrawable.setAlpha(this.finalAlphaInt);

        } else {
            /* Remove the background */
            mBGDrawable = null;
        }

        invalidate();
    }
    
    public void setBackgroundVisible(boolean visible) {
        isBackgroundVisible = visible;
    }
    
    public boolean isBackgroundVisible() {
        return isBackgroundVisible;
    }

    public void setTag(Object o) {
        mTag = o;
    }

    public Object getTag() {
        return mTag;
    }
    /* RK_ID: RK_BACKGROUND. AUT: liuli1 . DATE: 2012-11-16 . END */
    
//    public void setMatrix(Matrix matrix)
//    {
//        this.matrix = matrix;
//        invalidate();
//    }
    
    //dooba
  	public void resize(RectF rect) {
  		if (rect != null && rect.width() > 0 && rect.height() > 0) {
			this.localRect.set(rect);                        
			//this.setClipRect(new RectF(rect.left - rect.width(), rect.top, rect.right + rect.width(), rect.bottom));
			// Log.d(TAG, "mRect = " + mRect.toString() + " mCellWidth = " +
			// mCellWidth + " mCellHeight = " + mCellHeight);
			//calculateGlobalTouchRect();
			//setPageDrawAdapter(new SphereDrawAdapter());
			setInvertMatrixDirty();
			invalidate();
		}
  	}
  	
	public interface OnScrollListener{
		public void onScroll( DrawableItem item, MotionEvent e1, MotionEvent e2, float dx, float dy );
	}
	
	public interface OnDragListener{
		public void onDragStart(DrawableItem item, MotionEvent lastEvent);
		public void onDragging(DrawableItem item, MotionEvent e1, MotionEvent e2, float dx, float dy);
		public void onDragExit(DrawableItem item, MotionEvent lastEvent);
	}

  	public interface OnClickListener {
    	public void onClick(DrawableItem item);
    }
    
    public void setOnClickListener(OnClickListener l) {
    	mOnClickListener = l;
    }
    
    private void invokeOnClickListener() {
//		mContext.post(new Runnable() {
//            @Override
//            public void run() {
                if (mOnClickListener != null) {
                    mOnClickListener.onClick(DrawableItem.this);
                }
//            }
//        });
    }
    
    public interface OnVisibilityChangeListener{
    	void onVisibilityChange( DrawableItem who, boolean visible );
    }
    
    private OnVisibilityChangeListener mVisibilityListener;
    
    public void setOnVisibilityChangeListener( OnVisibilityChangeListener listener ){
    	mVisibilityListener = listener;
    }
    
    public OnVisibilityChangeListener getOnVisibilityChangeListener(){
    	return mVisibilityListener;
    }
    
    public interface OnLongClickListener {
    	public boolean onLongClick(DrawableItem item);
    }
    
    public void setOnLongClickListener(OnLongClickListener l) {
    	mOnLongClickListener = l;
    }
    
    private boolean invokeOnLongClickListener() {
    	if (mOnLongClickListener != null) {
    		return mOnLongClickListener.onLongClick(this);
    	}
    	return false;
    }

  	/* RK_ID: RK_BACKGROUND. AUT: liuli1 . DATE: 2012-11-16 . START */
    public void resetPressedState() {    	
    	resetPressedState(false);
    }
    
    public void resetPressedState(boolean clearPrePressed) {
    	if (clearPrePressed)
    	{
    		mPrivateFlags &= ~ItemStateSet.PREPRESSED;
    	}
        removeTapCallback();
        if (isPressed()) {
            setPressed(false);
        }
    }

    public void setPressed(boolean pressed) {                
        if (pressed) {
            mPrivateFlags |= ItemStateSet.PRESSED;
        } else {
            mPrivateFlags &= ~ItemStateSet.PRESSED;
        }
        refreshDrawableState();
    }

    protected boolean isPressed() {
        return (mPrivateFlags & ItemStateSet.PRESSED) == ItemStateSet.PRESSED;
    }

    private void refreshDrawableState() {
        mPrivateFlags |= ItemStateSet.DRAWABLE_STATE_DIRTY;
        drawableStateChanged();
        invalidate();
    }

    private void drawableStateChanged() {
        Drawable d = mBGDrawable;
        if (d != null && d.isStateful()) {
            d.setState(getDrawableState());
        }
    }

    private final int[] getDrawableState() {
        if ((mDrawableState != null) && ((mPrivateFlags & ItemStateSet.DRAWABLE_STATE_DIRTY) == 0)) {
            return mDrawableState;
        } else {
            mDrawableState = onCreateDrawableState(0);
            mPrivateFlags &= ~ItemStateSet.DRAWABLE_STATE_DIRTY;
            return mDrawableState;
        }
    }

    private int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState;

        int privateFlags = mPrivateFlags;

        int viewStateIndex = 0;
        if ((privateFlags & ItemStateSet.PRESSED) != 0)
            viewStateIndex |= ItemStateSet.VIEW_STATE_PRESSED;
        if ((privateFlags & ItemStateSet.SELECTED) != 0)
            viewStateIndex |= ItemStateSet.VIEW_STATE_SELECTED;

        drawableState = ItemStateSet.VIEW_STATE_SETS[viewStateIndex];

        if (extraSpace == 0) {
            return drawableState;
        }

        final int[] fullState;
        if (drawableState != null) {
            fullState = new int[drawableState.length + extraSpace];
            System.arraycopy(drawableState, 0, fullState, 0, drawableState.length);
        } else {
            fullState = new int[extraSpace];
        }

        return fullState;
    }

    public void setSelected(boolean selected) {
        if (((mPrivateFlags & ItemStateSet.SELECTED) != 0) != selected) {
            mPrivateFlags = (mPrivateFlags & ~ItemStateSet.SELECTED)
                    | (selected ? ItemStateSet.SELECTED : 0);

            if (!selected) {
                resetPressedState();
            }
            refreshDrawableState();
        }
    }

    public boolean isSelected() {
        return (mPrivateFlags & ItemStateSet.SELECTED) != 0;
    }

    @Override
    public void invalidateDrawable(Drawable arg0) {
        if (bgAlphaDirty) {
            bgAlphaDirty = false;
            return;
        }
        invalidate();
    }

    @Override
    public void scheduleDrawable(Drawable arg0, Runnable arg1, long arg2) {

    }

    @Override
    public void unscheduleDrawable(Drawable arg0, Runnable arg1) {

    }

    public boolean onFingerCancel(MotionEvent e) {
        if (!isTouchable()) {
            return false;
        }
        resetPressedState();
        return false;

    }

    /* RK_ID: RK_BACKGROUND. AUT: liuli1 . DATE: 2012-11-16 . END */
    
    public void registerIController(IController controller) {
		if(mContext != null && mContext.getExchangee() != null 
								&& mContext.getExchangee().getDrawingPass() != null){
            mContext.getExchangee().getDrawingPass().registerIController(controller);
        }
    }
    
    public void unregisterIController(IController controller) {
        if(mContext != null && mContext.getExchangee() != null 
								&& mContext.getExchangee().getDrawingPass() != null){
            mContext.getExchangee().getDrawingPass().unregisterIController(controller);
        }
    }

    /**
     * Debug Only.
     */
    public String dumpLayoutInfo() {
        StringBuffer toprint = new StringBuffer();
        toprint.append(" l = ").append(localRect.left)
        .append(" t = ").append(localRect.top)
        .append(" w = ").append(localRect.width())
        .append(" h = ").append(localRect.height())
        ;
        return toprint.toString();
    }

    public String dumpMatrix() {
        StringBuffer rt = new StringBuffer("\n");
        StringBuffer dump = new StringBuffer("dumpMatrix {");
        DrawableItem target = this;
        while (target != null) {
            dump.append(rt).append(rt).append(target.getClass().getSimpleName());
            dump.append(rt).append("xy( ").append(target.getRelativeX()).append(", ").append(target.getRelativeY()).append(" )");
            dump.append(rt).append("width = ").append(target.getWidth()).append(" height = ").append(target.getHeight());
            dump.append(rt).append("matrix = ").append(target.matrix);

            rt.append("  ");
            target = target.getParent();
        }
        dump.append("\n}");
        return dump.toString();
    }

    private final class CheckForTap implements Runnable {
        public void run() {
        	mPrivateFlags &= ~ItemStateSet.PREPRESSED;
            setPressed(true);
        }
    }
    
    private void removeTapCallback() { 	
        if (mPendingCheckForTap != null) {
            mContext.removeCallbacks(mPendingCheckForTap);
        }
    }
    
    private void delayCheckForTap() {
    	mPrivateFlags |= ItemStateSet.PREPRESSED;
    	
        if (mPendingCheckForTap == null) {
            mPendingCheckForTap = new CheckForTap();
        }
        mContext.postDelayed(mPendingCheckForTap, TAP_TIMEOUT);
        
        return;
    }
    
    private CheckForTap mPendingCheckForTap = null;
    private static final int TAP_TIMEOUT = 280;
    
    private UnsetPressedState mUnsetPressedState;
    private final class UnsetPressedState implements Runnable {
        public void run() {
            setPressed(false);
        }
    }
    
    private void checkForDoTap() {        
    	boolean prepressed = (mPrivateFlags & ItemStateSet.PREPRESSED) != 0;
    	
    	mPrivateFlags &= ~ItemStateSet.PREPRESSED;
    	if (prepressed && !isPressed())
		{    		
			removeTapCallback();
			setPressed(true);
            if (mUnsetPressedState == null) {
                mUnsetPressedState = new UnsetPressedState();
            }
			mContext.postDelayed(mUnsetPressedState,
                    ViewConfiguration.getPressedStateDuration());
		}
		else
		{
			resetPressedState();
		}
        
        return;
    }    
}
