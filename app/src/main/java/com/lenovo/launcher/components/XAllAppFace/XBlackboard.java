package com.lenovo.launcher.components.XAllAppFace;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.FloatMath;
import android.util.Log;

import com.lenovo.launcher.components.XAllAppFace.slimengine.BaseDrawableGroup;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IController;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;

/**
 * 黑板
 * @author zhaoxy
 *
 */
public class XBlackboard extends DrawableItem {

    private BaseDrawableGroup classroom;
    private float targetAlpha = 1;
    private float targetTempAlpha = 1;
    private float currentAlpha = 1;
    private AlphaController mAlphaController;

    public XBlackboard(XContext context, BaseDrawableGroup classroom, float targetAlpha) {
        super(context);
        this.classroom = classroom;
        this.targetAlpha = targetAlpha;
        this.mAlphaController = new AlphaController();
    }

    public void setClassroom(BaseDrawableGroup classroom) {
        hide();
        this.classroom = classroom;
    }
    
    @Override
    public void setAlpha(float alpha) {
        this.targetAlpha = alpha;
        if (getParent() != null) {
            super.setAlpha(targetAlpha);
            invalidate();
        }
    }

    public void setTargetAlpha(float alpha) {
        this.targetAlpha = alpha;
    }

    private void setSuperAlpha(float alpha) {
        super.setAlpha(alpha);
    }
    
    public void show() {
        show(false);
    }
    
    public void show(boolean toFront) {
    	show(toFront, true);
    }

    public void show(boolean toFront, boolean anim) {
        if (this.classroom != null) {
        	if (anim) {
                setSuperAlpha(0);
        	}
            targetTempAlpha = targetAlpha;
            this.classroom.addItem(this);
            this.resize(new RectF(0, 0, getParent().getWidth(), getParent().getHeight()));
            if (toFront) {
                this.classroom.bringChildToFront(this);
            } else {
                this.classroom.bringChildToBack(this);
            }
            if (anim) {
                mAlphaController.startAlphaAnim();
            } else {
            	setSuperAlpha(targetTempAlpha);
            }
        }
    }

    public void hide() {
        if (this.classroom != null) {
            targetTempAlpha = 0;
            mAlphaController.startAlphaAnim();
        }
    }

    @Override
    public void onDraw(IDisplayProcess c) {
        if (getAlpha() > 0) {
            getPaint().setColor(Color.BLACK);
            getPaint().setStyle(Paint.Style.FILL);
            updateFinalAlpha();
            c.drawRect(0, 0, getWidth(), getHeight(), getPaint());
        }
    }

    private class AlphaController implements IController {

        protected boolean alphaAnimActivate = false;
        
        protected void startAlphaAnim() {
            registerIController(this);
            alphaAnimActivate = true;
        }
        
        protected void stopAlphaAnim() {
            alphaAnimActivate = false;
            unregisterIController(this);
        }

        @Override
        public void update(long timeDelta) {
            if (alphaAnimActivate) {
                float delta = targetTempAlpha - getAlpha();
                if (Math.abs(delta) < 0.0001f) {
                    setSuperAlpha(targetTempAlpha);
                    stopAlphaAnim();
                } else {
                    setSuperAlpha(getAlpha() + delta * .135f);
                }
                invalidate();
            }
        }
        
    }

}
