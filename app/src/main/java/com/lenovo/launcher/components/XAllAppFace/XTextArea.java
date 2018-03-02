package com.lenovo.launcher.components.XAllAppFace;

import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;

public class XTextArea extends DrawableItem {

    private String mText = "";
    protected String toDraw = "";
    protected int textSetColor;
    private FontMetrics fm = new FontMetrics();
    float textBaseY = 0;
    float textX = 0;
    /*** RK_ID: TEXT_MARQUEE.  AUT: zhaoxy . DATE: 2013-01-22 . START***/
    protected TextUtils.TruncateAt where = TextUtils.TruncateAt.END;
    private static final String DOT = "...";
    protected float edge = 0;
	private float fadingEdgeLength = 0;
    protected boolean needCut = false;
    private float textWidth = 0;
    protected Shader shader;
    private final RectF mClipRect;
    /*** RK_ID: TEXT_MARQUEE.  AUT: zhaoxy . DATE: 2013-01-22 . END***/
    
    protected final Paint mPaint = new Paint();
    
    public XTextArea(XContext context, String text, RectF rect) {
        super(context);
        if (rect == null) {
            rect = new RectF();
        }
        mClipRect = new RectF();
        if (rect.width() > 0 && rect.height() > 0) {
            this.resize(rect);
        }
        mPaint.setAntiAlias(true);
        if (text == null) {
            text = "";
        }
//        setTextSize(rect.height() - mPaddingTop - mPaddingBottom);
        setText(text);
    }
    
    public boolean isEmpty() {
        return mText == null || mText.equals("");
    }
    
    public void setTextSize(float size) {
        if (size > 0) {
            mPaint.setTextSize(size);
            mPaint.getFontMetrics(fm);
            float fontHeight = fm.bottom - fm.top;
            float bottom = 0;

//            if (Float.compare(localRect.height() - mPaddingTop - mPaddingBottom, (float) 0.0) <= 0) {
//                bottom = localRect.top + mPaddingTop + mPaddingBottom + (int) Math.ceil(fontHeight);
//                localRect.bottom = bottom;
//            }

            bottom = localRect.top + mPaddingTop + mPaddingBottom+ (int) Math.ceil(fontHeight);
            if(bottom  >localRect.bottom  ){
                localRect.bottom = bottom;
                mClipRect.set(0, 0, getWidth(), getHeight());
            }

            updateText();
            invalidate();
        }
    }
    
    
    

    @Override
    public void setBackgroundDrawable(Drawable d) {
        // TODO Auto-generated method stub
        super.setBackgroundDrawable(d);
        if(d !=null ){
            int width = d.getIntrinsicWidth();
            int height = d.getIntrinsicHeight();
            if((width ) > (localRect.right -localRect.left)){
                localRect.right = localRect.left + width;
                mClipRect.set(0, 0, getWidth(), getHeight());
            }
            if((height) > (localRect.bottom -localRect.top)){
                localRect.bottom = localRect.top + height;
                mClipRect.set(0, 0, getWidth(), getHeight());
            }
        }
    }

    /*** RK_ID: TEXT_MARQUEE.  AUT: zhaoxy . DATE: 2013-01-22 . START***/
    public float getTextWidth() {
        return textWidth;
    }
    /*** RK_ID: TEXT_MARQUEE.  AUT: zhaoxy . DATE: 2013-01-22 . END***/
    
    public void setTextAlign(Align align) {
        mPaint.setTextAlign(align);
    }

    /*** RK_ID: TEXT_MARQUEE.  AUT: zhaoxy . DATE: 2013-01-22 . START***/
    public void setEllipsize(TextUtils.TruncateAt where) {
        if (where == TextUtils.TruncateAt.MARQUEE) {
            this.where = where;
            this.fadingEdgeLength = 10f;
            shader = new LinearGradient(edge, 0, edge + fadingEdgeLength, 0, 0, 0xFF000000, Shader.TileMode.CLAMP);
        } else {
            this.fadingEdgeLength = 0f;
        }
    }

    public void setFadingEdgeLength(int length) {
        this.fadingEdgeLength = length;
        shader = new LinearGradient(edge, 0, edge + fadingEdgeLength, 0, 0, 0xFF000000, Shader.TileMode.CLAMP);
    }
    /*** RK_ID: TEXT_MARQUEE.  AUT: zhaoxy . DATE: 2013-01-22 . END***/
    
    public void setTextColor(int color) {
        textSetColor = color;
        invalidate();
    }
    
    public int getTextColor() {
        return textSetColor;
    }

    public void setText(String text) {
        if (text == null) {
            text = "";
        }
        mText = text;
        updateText();
        invalidate();
    }
    
    @Override
    public void resize(RectF rect) {
        super.resize(rect);
        mClipRect.set(0, 0, getWidth(), getHeight());
        updateText();
        invalidate();
    }
    
    private void updateText() {
        toDraw = mText;
        toDraw.trim();
        int maxWidth = (int) (localRect.width() - mPaddingLeft - mPaddingRight);
        /*** RK_ID: TEXT_MARQUEE.  AUT: zhaoxy . DATE: 2013-01-22 . START***/
        textWidth = mPaint.measureText(toDraw);
        if (textWidth > maxWidth) {
            float max;
            if (where == TextUtils.TruncateAt.MARQUEE) {
                max = maxWidth;
            } else {
                max = maxWidth - mPaint.measureText(DOT);
            }
            float[] widths = new float[toDraw.length()];
            int count = mPaint.getTextWidths(toDraw, widths);
            textWidth = 0f;
            for (int i = 0; i < count; i++) {
                textWidth += widths[i];
                if (textWidth >= max) {
                    textWidth -= widths[i];
                    count = i;
                    break;
                }
            }
            if (where == TextUtils.TruncateAt.MARQUEE) {
                if (count < toDraw.length()) {
//                    textWidth = mPaint.measureText(toDraw.substring(0, count));
                    count++;
                    //add by zhanggx1 for bug 20982
                    String subStr = toDraw.substring(0, count);
                    float subWidth = mPaint.measureText(subStr);
                    while (subWidth == 0f && count < toDraw.length()) {
                    	count++;
                    	subStr = toDraw.substring(0, count);
                    }
                    //add by zhanggx1 for bug 20982
                    toDraw = subStr;
                    needCut = true;
                } else {
                    needCut = false;
//                    toDraw = toDraw.substring(0, count);
//                    textWidth = mPaint.measureText(toDraw);
                }
            } else {
                toDraw = new StringBuffer(toDraw.substring(0, count)).append(DOT).toString();
                textWidth = mPaint.measureText(toDraw);
            }
        } else {
            needCut = false;
        }
        float fontHeight = fm.bottom - fm.top;
        textBaseY = localRect.height() - (localRect.height() - fontHeight) / 2 - fm.bottom;
        final Align align = mPaint.getTextAlign();
        switch (align) {
            case CENTER:
                if (needCut) {
                    textX = (localRect.width() - textWidth + mPaint.measureText(toDraw)) / 2;
                    edge = (localRect.width() + textWidth) / 2f;
                    shader = new LinearGradient(edge, 0, edge + fadingEdgeLength, 0, 0, 0xFF000000, Shader.TileMode.CLAMP);
                } else {
                    textX = localRect.width() / 2f;
                }
                break;
            case LEFT:
                textX = mPaddingLeft;
                edge = mPaddingLeft + textWidth;
                break;
            case RIGHT:
                textX = getWidth() - mPaddingRight;
                edge = textX;
                break;
            default:
            	break;
        }
        /*** RK_ID: TEXT_MARQUEE.  AUT: zhaoxy . DATE: 2013-01-22 . END***/
    }

    public String getText() {
        return this.mText;
    }
    
    public String getDrawText() {
        return this.toDraw;
    }

    /*** RK_ID: TEXT_MARQUEE.  AUT: zhaoxy . DATE: 2013-01-22 . START***/
    protected final Paint mFadePaint = new Paint();
    protected static final Xfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
    /*** RK_ID: TEXT_MARQUEE.  AUT: zhaoxy . DATE: 2013-01-22 . END***/
    
    @Override
    public void onDraw(IDisplayProcess c) {
        if (isEmpty()) {
            return;
        }
        
        c.clipRect(mClipRect);
        /* RK_ID: RK_ALLAPPS. AUT: liuli1 . DATE: 2012-11-13 . START */
        mPaint.setColor(textSetColor);
        int alpha = textSetColor >>> 24;
        alpha = (int) (alpha * getFinalAlpha());
        mPaint.setAlpha(alpha);
        /* RK_ID: RK_ALLAPPS. AUT: liuli1 . DATE: 2012-11-13 . END */
        c.drawText(toDraw, textX, textBaseY, mPaint);
        alpha = (int) (255 * getFinalAlpha());
        alpha = alpha << 24;
        int shadowc = 0xffffffff & alpha;
        mPaint.setShadowLayer(0, 0, 0, shadowc);
        /*** RK_ID: TEXT_MARQUEE.  AUT: zhaoxy . DATE: 2013-01-22 . START***/
        if (where == TextUtils.TruncateAt.MARQUEE && needCut) {
            mFadePaint.setShader(shader);
            mFadePaint.setXfermode(mXfermode);
            mFadePaint.setStyle(Style.FILL);
            c.drawRect(new RectF(edge, 0, getWidth(), getHeight()), mFadePaint);
        }
        /*** RK_ID: TEXT_MARQUEE.  AUT: zhaoxy . DATE: 2013-01-22 . END***/
    }
    
    @Override
    public void setAlpha(float alpha) {
        super.setAlpha(alpha);
        if (alpha >= 0f && alpha <= 1f) {
            mPaint.setAlpha((int) (alpha * 255));
        }
    }

    @Override
    public void updateFinalAlpha() {
        super.updateFinalAlpha();
        mPaint.setAlpha((int) (getFinalAlpha() * 255));
    }
    
}
