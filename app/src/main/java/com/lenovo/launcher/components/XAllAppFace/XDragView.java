package com.lenovo.launcher.components.XAllAppFace;

import android.animation.Keyframe;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
//import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
//import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;

//import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IController;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;
//import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem.OnVisibilityChangeListener;

public class XDragView extends DrawableItem {
    
    private XDragLayer mDragLayer = null;
    private Bitmap mBitmap;
    private int mRegistrationX;
    private int mRegistrationY;
    private Rect mDragRegion = null;
    private ValueAnimator mBubbleScaleAnim = null;
    private ValueAnimator mBubbleExtraScaleAnim = null;
    private float mDragBubbleScale = 1f;
    private float mDragExtraScale = 1f;
    private EffectAnimController mAnimController = null;
    private float mDragPosX = 0, mDragPosY = 0;
    private float mDragPathX = 0;
    private float mDragPathY = 0;
    private boolean effectAnim = true;
    private final float initialScale = 1.1f;
    
    private boolean mHasDrawn = false;
    private float mOffsetX = 0.0f;
    private float mOffsetY = 0.0f;

    /**
     * Construct the drag view.
     * <p>
     * The registration point is the point inside our view that the touch events
     * should be centered upon.
     * 
     * @param mRoot
     *            The MainSurface instance
     * @param bitmap
     *            The view that we're dragging around. We scale it up when we
     *            draw it.
     * @param registrationX
     *            The x coordinate of the registration point.
     * @param registrationY
     *            The y coordinate of the registration point.
     */
    public XDragView(XLauncher mContext, Bitmap bitmap, int registrationX, int registrationY, int left, int top, int width, int height) {
        super((XContext) mContext.getMainView());
        mDragLayer = mContext.getDragLayer();
        
//        final Resources res = mContext.getResources();
//        final int dragScale = res.getInteger(R.integer.config_dragViewExtraPixels);
        
        Matrix scale = new Matrix();
        final float scaleFactor = initialScale;//(float)( (width + dragScale) / width);
        if (scaleFactor != 1.0f) {
            scale.setScale(scaleFactor, scaleFactor);
        }
        
        //final int offsetX = res.getDimensionPixelSize(R.dimen.dragViewOffsetX);
        //final int offsetY = res.getDimensionPixelSize(R.dimen.dragViewOffsetY);
        
        mBitmap = Bitmap.createBitmap(bitmap, left, top, width, height, scale, true);
        resize(new RectF(0, 0, mBitmap.getWidth(), mBitmap.getHeight()));
        setDragRegion(new Rect(0, 0, width, height));
        
        // The point in our scaled bitmap that the touch events are located
        mRegistrationX = registrationX;
        mRegistrationY = registrationY;

        setAlpha(.5f);
        
        // visible listen always
        this.wantKnowVisibleState(true);
        this.setOnVisibilityChangeListener( new OnVisibilityChangeListener() {
			
			@Override
			public void onVisibilityChange(DrawableItem who, boolean visible) {
				if (mAnimController != null) {
					if (!visible) {
						unregisterIController(mAnimController);
					} else {
						registerIController(mAnimController);
					}
				}
			}
		});
    }
    
    final RectF rectTemp = new RectF();
    
    @Override
    public void onDraw(IDisplayProcess c) {
    	/*
        if (false) {
            // for debugging
            rectTemp.set(localRect);
            rectTemp.offsetTo(0, 0);
            Paint p = new Paint();
            p.setStyle(Paint.Style.FILL);
            p.setColor(0xaaffffff);
            c.drawRoundRect(rectTemp, 0, 0, getPaint());
        }*/
        mHasDrawn = true;
        getPaint().setAlpha(this.finalAlphaInt);
        if (mBitmap != null) {
            c.drawBitmap(mBitmap, 0, 0, getPaint());
        }
    }
    
    public void setEffectAnimEnable(boolean effectAnim) {
        this.effectAnim = effectAnim;
    }
    
    @Override
    public boolean onDown(MotionEvent e) {
        super.onDown(e);
        return true;
    }
    
    @Override
    public boolean onLongPress(MotionEvent e) {
        super.onLongPress(e);
        return true;
    }
    
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return true;
    }
    
    public int getDragRegionLeft() {
        return mDragRegion.left;
    }
    
    public int getDragRegionTop() {
        return mDragRegion.top;
    }
    
    public int getDragRegionWidth() {
        return mDragRegion.width();
    }
    
    public int getDragRegionHeight() {
        return mDragRegion.height();
    }
    
    public void setDragRegion(Rect r) {
        mDragRegion = r;
    }
    
    public Rect getDragRegion() {
        return mDragRegion;
    }
    
    /**
     * Create a window containing this view and show it.
     * 
     * @param touchX
     *            the x coordinate the user touched in DragLayer coordinates
     * @param touchY
     *            the y coordinate the user touched in DragLayer coordinates
     */
    public void show(int touchX, int touchY, boolean effectAnim) {
        this.effectAnim = effectAnim;
        mDragLayer.addItem(this);
        setRelativeX(touchX - mRegistrationX);
        setRelativeY(touchY - mRegistrationY);
        
        // EffectAnim
        mDragPosX = getRelativeX();
        mDragPosY = getRelativeY();
        mAnimController = new EffectAnimController();
        registerIController(mAnimController);
        mAnimController.startDargEffectAnim();

        if (mBubbleScaleAnim != null) {
            getXContext().getRenderer().ejectAnimation(mBubbleScaleAnim);
        }
        // 2.75个周期
//        final float maxRadian = (float) Math.PI * 5.5f;
//        final float damp = maxRadian / 2f;
//        mBubbleScaleAnim = ValueAnimator.ofFloat(0f, maxRadian);
//        mBubbleScaleAnim.setInterpolator(new LinearInterpolator());
//        mBubbleScaleAnim.setDuration(500);
//        mBubbleScaleAnim.addUpdateListener(new AnimatorUpdateListener() {
//            private int count = 0;
//
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                count++;
//                if (count < 4) {
//                    return;
//                }
//                final float step = (Float) animation.getAnimatedValue();
//                mDragBubbleScale = (float) (1f + 0.3f * FloatMath.cos(step) * Math.pow(Math.E, -step / damp));
//            }
//        });
//        getXContext().getRenderer().injectAnimation(mBubbleScaleAnim, false);

        Keyframe kf0 = Keyframe.ofFloat(0, 1.0f);
        Keyframe kf1 = Keyframe.ofFloat(0.43f, 1.15f);
        Keyframe kf2 = Keyframe.ofFloat(0.71f, 1.07f);
        Keyframe kf3 = Keyframe.ofFloat(1, initialScale);
        PropertyValuesHolder values = PropertyValuesHolder.ofKeyframe("scale", kf0, kf1, kf2, kf3);

        final float offsetX = (mBitmap.getWidth() - mDragRegion.width())/2f;
        final float offsetY = (mBitmap.getHeight() - mDragRegion.height());

//        mBubbleScaleAnim = ValueAnimator.ofFloat(0f, 1.15f, 1.07f, 1.1f);
        mBubbleScaleAnim = ValueAnimator.ofPropertyValuesHolder(values);
        mBubbleScaleAnim.setInterpolator(new LinearInterpolator());
        mBubbleScaleAnim.setDuration(350);
        mBubbleScaleAnim.addUpdateListener(new AnimatorUpdateListener() {
            private int count = 0;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                count++;
                if (count < 4) {
                    return;
                }
                final float step = (Float) animation.getAnimatedValue();
                mDragBubbleScale = step / initialScale;
//                android.util.Log.i("Test", "time ====" + animation.getCurrentPlayTime() + "   scale ==== "
//                        + mDragBubbleScale);

                final float value = (step - initialScale) / 2f;

                final int deltaX = (int) ((value * mDragRegion.width() + offsetX) - mOffsetX);
                final int deltaY = (int) ((value * mDragRegion.height() + offsetY) - mOffsetY);

                mOffsetX += deltaX;
                mOffsetY += deltaY;

                if (getParent() == null) {
                    animation.cancel();
                } else {
                    setRelativeX(getRelativeX() - deltaX);
                    setRelativeY(getRelativeY() - deltaY);
                    invalidate();
                }
            }
        });
        getXContext().getRenderer().injectAnimation(mBubbleScaleAnim, false);

        invalidate();
    }
    
    /**
     * Move the window containing this view.
     * 
     * @param touchX
     *            the x coordinate the user touched in DragLayer coordinates
     * @param touchY
     *            the y coordinate the user touched in DragLayer coordinates
     */
    void move(int touchX, int touchY) {
        setRelativeX(touchX - mRegistrationX - mOffsetX);
        setRelativeY(touchY - mRegistrationY - mOffsetY);
        mDragPosX = getRelativeX();
        mDragPosY = getRelativeY();
        invalidate();
    }
    
    void remove() {
        if (mAnimController != null) {
            mAnimController.stopDargEffectAnim();
            unregisterIController(mAnimController);
            mAnimController = null;
        }
        getXContext().post(new Runnable() {
            @Override
            public void run() {
                mDragLayer.removeItem(XDragView.this);
                invalidate();
            }
        });
    }
    
    void scale(float scale, boolean animate) {
    	if (animate) {
    		if (mBubbleExtraScaleAnim != null) {
    			getXContext().getRenderer().ejectAnimation(mBubbleExtraScaleAnim);
    		}
    		mBubbleExtraScaleAnim = ValueAnimator.ofFloat(mDragExtraScale, scale);
    		mBubbleExtraScaleAnim.setInterpolator(new LinearInterpolator());
    		mBubbleExtraScaleAnim.setDuration(200);
    		mBubbleExtraScaleAnim.addUpdateListener(new AnimatorUpdateListener() {
    			private int count = 0;
    			@Override
    			public void onAnimationUpdate(ValueAnimator animation) {
    				final float step = (Float) animation.getAnimatedValue();
    				mDragExtraScale = step;
    			}
    		});
    		getXContext().getRenderer().injectAnimation(mBubbleExtraScaleAnim, false);
        } else {
        	mDragExtraScale = scale;
        }
    	invalidate();
    }
    
    private class EffectAnimController implements IController {
        
        private boolean dragEffectAnimActivate = false;
        
        public void startDargEffectAnim() {
            dragEffectAnimActivate = true;
            mDragPathX = mDragPosX;
            mDragPathY = mDragPosY;
        }
        
        public void stopDargEffectAnim() {
            dragEffectAnimActivate = false;
            mDragPathX = mDragPosX;
            mDragPathY = mDragPosY;
        }
        
        @Override
        public void update(long timeDelta) {
            if (dragEffectAnimActivate) {
                float delta = mDragPosX - mDragPathX;
                mDragPathX += delta * .2f;
                delta = mDragPosY - mDragPathY;
                mDragPathY += delta * .2f;
                float mSkewX = 0;
                float mSkewY = 0;
                
                if (effectAnim) {
                    mSkewX = (mDragPosX - mDragPathX) / getWidth();
                    mSkewY = (mDragPosY - mDragPathY) / getHeight();
                    if (mSkewY > .7f) {
                        mSkewY = .7f;
                    } else if (mSkewY < -.7f) mSkewY = -.7f;
                }
                
                Matrix m = getMatrix();
                final float cenX = localRect.centerX();
                final float ceny = localRect.centerY();
                m.setScale((1 - mSkewY) * mDragBubbleScale, (1 + mSkewY) * mDragBubbleScale, cenX, ceny);
                if (effectAnim) m.postSkew(mSkewX, 0, getRelativeX() + mRegistrationX, getRelativeY() + mRegistrationY);
                m.postScale(mDragExtraScale, mDragExtraScale, getRelativeX() + mRegistrationX, getRelativeY() + mRegistrationY);
                updateMatrix(m);
                
                invalidate();
            }
        }
    }
    
    public Bitmap getBitmap()
    {
        return mBitmap;
    };
    
    public boolean hasDrawn() {
        return mHasDrawn;
    }

    float getScale() {
        return initialScale;
    }
}
