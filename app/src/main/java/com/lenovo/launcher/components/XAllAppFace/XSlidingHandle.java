package com.lenovo.launcher.components.XAllAppFace;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.res.Resources;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.slimengine.BaseDrawableGroup;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;

public class XSlidingHandle extends BaseDrawableGroup {

    private XContext mContext;

    private DrawableItem mItemTop;
    private DrawableItem mItemBottom;

    private Drawable mDragUpBg;
    private Drawable mDragDownBg;

    private ValueAnimator firstShowAnim = null;
    private ValueAnimator secondShowAnim = null;
    private final int ANIM_DURATION = 350;
    private final int ANIM_FIRST_DELAY = 800;

    public enum State {
        UP, DOWN
    }

    public XSlidingHandle(XContext context) {
        super(context);

        mContext = context;
        init();
    }

    private void init() {
        Resources res = mContext.getResources();
        mDragUpBg = res.getDrawable(R.drawable.xscreen_arrow_up);
        mDragDownBg = res.getDrawable(R.drawable.xscreen_arrow_down);

        mItemTop = new DrawableItem(mContext);
        mItemTop.setBackgroundDrawable(mDragUpBg);
        addItem(mItemTop);
        mItemBottom = new DrawableItem(mContext);
        mItemBottom.setBackgroundDrawable(mDragUpBg);
        addItem(mItemBottom);
    }

    @Override
    public void resize(RectF rect) {
        super.resize(rect);

        layoutChildren(mDragUpBg);
    }

    private void layoutChildren(Drawable d) {
        int width = d.getIntrinsicWidth();
        int height = d.getIntrinsicHeight();
        float relativeX = (getWidth() - width) / 2;
        float relativeY = mContext.getResources().getDimensionPixelSize(R.dimen.screen_handle_padding_bottom);

        mItemTop.resize(new RectF(0, 0, width, height));
        mItemTop.setRelativeX(relativeX);
        mItemTop.setRelativeY(getHeight() - 2 * height - relativeY);

        mItemBottom.resize(new RectF(0, 0, width, height));
        mItemBottom.setRelativeX(relativeX);
        mItemBottom.setRelativeY(getHeight() - height - relativeY);
    }

    void updateDrawable(State state) {
        if (state == State.UP) {
            mItemTop.setBackgroundDrawable(mDragUpBg);
            mItemBottom.setBackgroundDrawable(mDragUpBg);
        } else if (state == State.DOWN) {
            mItemTop.setBackgroundDrawable(mDragDownBg);
            mItemBottom.setBackgroundDrawable(mDragDownBg);
        }

        layoutChildren(mDragUpBg);
    }

    void animate() {
        firstShowAnim = initAnimation(firstShowAnim, mItemTop, 0);
        secondShowAnim = initAnimation(secondShowAnim, mItemBottom, 1);

        getXContext().getRenderer().injectAnimation(firstShowAnim, false);
        getXContext().getRenderer().injectAnimation(secondShowAnim, false);
    }

    private ValueAnimator initAnimation(ValueAnimator anim, final DrawableItem item, int index) {
        if (anim != null) {
            getXContext().getRenderer().ejectAnimation(anim);
        }

        anim = ValueAnimator.ofFloat(0.5f, 2.0f, 1.0f);
        anim.setDuration(ANIM_DURATION);
        anim.setStartDelay(ANIM_DURATION * index + ANIM_FIRST_DELAY);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Matrix m = item.getMatrix();
                Float value = (Float) (animation.getAnimatedValue());
                m.setScale(value, value, item.localRect.centerX(), item.localRect.centerY());
                item.updateMatrix(m);
            }
        });

        return anim;
    }

}
