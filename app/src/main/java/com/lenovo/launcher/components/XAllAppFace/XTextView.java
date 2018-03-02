package com.lenovo.launcher.components.XAllAppFace;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.text.TextUtils;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.Utilities;

public class XTextView extends XTextArea {

	static final float CORNER_RADIUS = 8.0f;
	private float mCornerRadius;
	private boolean enableBackground = false;
    static final float SHADOW_LARGE_RADIUS = 4.0f;
    static final float SHADOW_SMALL_RADIUS = 1.75f;
    static final float SHADOW_Y_OFFSET = 2.0f;
    static final int SHADOW_LARGE_COLOUR = 0xDD000000;
    static final int SHADOW_SMALL_COLOUR = 0xCC000000;
    
	private int bubbleColor;
	private int textview_background_offset;
    private float mBubbleColorAlpha;

	public XTextView(XContext context, String text, RectF rect) {
		super(context, text, rect);
		Resources res = context.getResources();
		setPadding(1, 2, 1, 2);
		localRect.bottom += mPaddingTop + mPaddingBottom;
		mPaint.setTextAlign(Align.CENTER);
		final float scale = res.getDisplayMetrics().density;
		mCornerRadius = CORNER_RADIUS * scale;
		setTextColor(SettingsValue.getIconTextStyleValue(context.getContext()));        
		bubbleColor = context.getResources().getColor(R.color.bubble_dark_background);
		textview_background_offset = context.getResources().getDimensionPixelOffset(R.dimen.textview_background_offset);
        mBubbleColorAlpha = Color.alpha(bubbleColor) / 255.0f;
        /*** RK_ID: TEXT_MARQUEE.  AUT: zhaoxy . DATE: 2013-01-22 . START***/
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        /*** RK_ID: TEXT_MARQUEE.  AUT: zhaoxy . DATE: 2013-01-22 . END***/
	}

    /* RK_ID: RK_ALLAPPS. AUT: liuli1 . DATE: 2012-11-13 . START */
    protected void setTextColor(Context c) {
        setTextColor(SettingsValue.getIconTextStyleValue(c));
    }
    
    protected void setBackgroundEnable(boolean enable) {
        enableBackground = enable;
        invalidate();
    }
    /* RK_ID: RK_ALLAPPS. AUT: liuli1 . DATE: 2012-11-13 . END */

	@Override
	public void onDraw(IDisplayProcess c) {
	    /*** RK_ID: TEXT_MARQUEE.  AUT: zhaoxy . DATE: 2013-01-22 . START***/
	    if (isEmpty()) {
            return;
        }
		mPaint.setColor(0xffffffff);
		int alpha = 0xffffffff >>> 24;
		alpha = (int) (alpha * getFinalAlpha());
		mPaint.setAlpha(alpha);
        if (Utilities.isLightColor(getTextColor())) {
            alpha = SHADOW_LARGE_COLOUR >>> 24;
            alpha = (int) (alpha * getFinalAlpha());
            alpha = alpha << 24;
            int shadowc = SHADOW_LARGE_COLOUR | 0xff000000;
            shadowc &= alpha;
            mPaint.setShadowLayer(SHADOW_LARGE_RADIUS, 0.0f, 0, shadowc);
        } else {
            alpha = SHADOW_LARGE_COLOUR >>> 24;
            alpha = (int) (alpha * getFinalAlpha());
            alpha = alpha << 24;
            int shadowc = SHADOW_LARGE_COLOUR | 0xff000000;
            shadowc &= alpha;
            mPaint.setShadowLayer(1.0f, 0.0f, 0, shadowc);
        }
        super.onDraw(c);
        float textWidth = getTextWidth() + textview_background_offset;
        if (enableBackground) {
            mPaint.setColor(bubbleColor);
            alpha = bubbleColor >>> 24;
            alpha = (int) (alpha * getFinalAlpha());
            mPaint.setAlpha(alpha);
            mPaint.setStyle(Style.FILL);
            Xfermode temp = mPaint.getXfermode();
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
            c.drawRoundRect(new RectF(localRect.width() / 2 - textWidth / 2 - mPaddingLeft, 0, localRect.width() / 2 + textWidth / 2 + mPaddingRight, localRect.height()), mCornerRadius, mCornerRadius, mPaint);
            mPaint.setXfermode(temp);
        }
        /*** RK_ID: TEXT_MARQUEE.  AUT: zhaoxy . DATE: 2013-01-22 . END***/
	}
}
