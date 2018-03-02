package com.lenovo.launcher.components.XAllAppFace;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

public class XGradientDrawable {

    int[] mColors;
    float[] mPositions;
    int mRadius;

    private final Paint mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF mRect = new RectF();
    private boolean mRectIsDirty;

    int mWidth = -1;
    int mHeight = -1;

    private int mLevel;
    private boolean mUseLevel;

    final boolean setLevel(int level) {
        if (mLevel != level) {
            mLevel = level;

            mRectIsDirty = true;
            return true;
        }
        return false;
    }

    final int getLevel() {
        return mLevel;
    }

    void setup(int startColor, int centerColor, int endColor, float centerY, int radius) {
        mColors = new int[3];
        mColors[0] = startColor;
        mColors[1] = centerColor;
        mColors[2] = endColor;

        mPositions = new float[3];
        mPositions[0] = 0.0f;
        // Since 0.5f is default value, try to take the one that isn't 0.5f
        mPositions[1] = centerY;
        mPositions[2] = 1f;

        mRadius = radius;
    }

    void setSize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    void setUseLevel(boolean useLevel) {
        mUseLevel = useLevel;
        mRectIsDirty = true;
    }

    void draw(Canvas canvas) {
        if (!ensureValidRect()) {
            // nothing to draw
            return;
        }

        // remember the alpha values, in case we temporarily overwrite them
        // when we modulate them with mAlpha
        final int prevFillAlpha = mFillPaint.getAlpha();
        // compute the modulate alpha values
        final int currFillAlpha = modulateAlpha(prevFillAlpha);
        /*
         * if we're not using a layer, apply the dither/filter to our individual paints
         */
        mFillPaint.setAlpha(currFillAlpha);
        mFillPaint.setDither(true);

        if (mRadius > 0.0f) {
            // since the caller is only giving us 1 value, we will force
            // it to be square if the rect is too small in one dimension
            // to show it. If we did nothing, Skia would clamp the rad
            // independently along each axis, giving us a thin ellipse
            // if the rect were very wide but not very tall
            float rad = mRadius;
            float r = Math.min(mRect.width(), mRect.height()) * 0.5f;
            if (rad > r) {
                rad = r;
            }
            canvas.drawRoundRect(mRect, rad, rad, mFillPaint);
        } else {
            canvas.drawRect(mRect, mFillPaint);
        }

        mFillPaint.setAlpha(prevFillAlpha);
    }

    private boolean ensureValidRect() {
        if (mRectIsDirty) {
            mRectIsDirty = false;

            float inset = 0;

            mRect.set(inset, inset, mWidth - inset, mHeight - inset);

            final int[] colors = mColors;
            if (colors != null) {
                RectF r = mRect;
                float x0, x1, y0, y1;

                final float level = mUseLevel ? (float) getLevel() / 10000.0f : 1.0f;
                x0 = r.left;
                y0 = r.top;
                x1 = x0;
                y1 = level * r.bottom;

                mFillPaint.setShader(new LinearGradient(x0, y0, x1, y1, colors, mPositions,
                        Shader.TileMode.CLAMP));
            }
        }
        return !mRect.isEmpty();
    }

    private int modulateAlpha(int alpha) {
        int scale = 0xFF + (0xFF >> 7);
        return alpha * scale >> 8;
    }

}
