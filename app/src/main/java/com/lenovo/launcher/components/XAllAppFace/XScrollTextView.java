package com.lenovo.launcher.components.XAllAppFace;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

import com.lenovo.launcher.components.XAllAppFace.slimengine.IDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.NormalDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;
import com.lenovo.launcher2.customizer.Debug.R5;
import com.lenovo.launcher2.customizer.Utilities;

public class XScrollTextView extends XTextView{
	
//	float textX2 = 0;
	boolean mDrawNext = false;
//	protected String toDraw2 = "";
	protected Shader shader1;
	protected Shader shader2;
	Shader composeShader;
	boolean mDebug = false;

	public XScrollTextView(XContext context, String text, RectF rect) {
		super(context, text, rect);
		// TODO Auto-generated constructor stub
		
		mClipRect.set(0, 0, getWidth(), getHeight());
		
	}
	
	public void scrollToPage(int destPage, float offsetX, int  pageCount, boolean isLoop) {
		
		setText((destPage + 1) + "");
		mCurrentBitmap = mArrayList.get(destPage);
//		textX = localRect.width() / 2f + getWidth() * (offsetX);
		mCurrentX = (int)(getWidth() * (offsetX));
		int nextPage = -1;
		if (offsetX > 0)
		{
			if (destPage == 0)
			{
				if (isLoop)
				{
//					toDraw2 = (pageCount) + "";
//					textX2 = textX - getWidth();
					mNextBitmap = mArrayList.get(pageCount - 1);
					nextPage = pageCount - 1;
					mNextX = mCurrentX - (int)getWidth();
					mDrawNext = true;
				}
				else
				{
//					textX = localRect.width() / 2f + getWidth() * (offsetX / 2);
					mCurrentX = (int)(getWidth() * (offsetX))/2;
					mDrawNext = false;
				}
			}
			else
			{
//				toDraw2 = (destPage) + "";
//				textX2 = textX - getWidth();
				mNextBitmap = mArrayList.get(destPage - 1);
				nextPage = destPage - 1;
				mNextX = mCurrentX - (int)getWidth();
				mDrawNext = true;
			}
		}		
		else
		{
			if (destPage == pageCount -1)
			{
				if (isLoop)
				{
//					toDraw2 = "1";
//					textX2 = textX + getWidth();
					mNextBitmap = mArrayList.get(0);
					nextPage = 0;
					mNextX = mCurrentX + (int)getWidth();
					mDrawNext = true;
				}
				else
				{
//					textX = localRect.width() / 2f + getWidth() * (offsetX / 2);
					mCurrentX = (int)(getWidth() * (offsetX))/2;
					mDrawNext = false;
				}
			}
			else
			{
//				toDraw2 = (destPage + 2) + "";
//				textX2 = textX + getWidth();
				mNextBitmap = mArrayList.get(destPage + 1);
				mNextX = mCurrentX + (int)getWidth();
				nextPage = destPage + 1;
				mDrawNext = true;
			}
		}
		
		if (mDebug)R5.echo("destPage = " + destPage + "offsetX = " + offsetX
				+ "mCurrentX = " + mCurrentX + "mNextX = " + mNextX
				+ "mDrawNext = " + mDrawNext 
				+ "nextPage = " + nextPage);
		invalidate();
	}
	
	public void resize(RectF rect) {
		super.resize(rect);
		if (mClipRect != null)
		{
			mClipRect.set(mHideLen, 0, getWidth() - mHideLen, getHeight());
			mShadowLen = (int)getWidth() / 4;
			shader1 = new LinearGradient(0, 0, mShadowLen, 0, 0xFF000000, 0, Shader.TileMode.CLAMP);
			shader2 = new LinearGradient(getWidth() - mShadowLen, 0, getWidth(), 0, 0, 0xFF000000, Shader.TileMode.CLAMP);
			composeShader = new ComposeShader(shader1, shader2, PorterDuff.Mode.XOR);
			mBackRect.set(0, 0, getWidth(), getHeight());
		}
	};
	
	RectF mClipRect = new RectF();
	RectF mBackRect = new RectF();
	int mShadowLen;
	
	@Override
	public void onDraw(IDisplayProcess c) {
	    /*** RK_ID: TEXT_MARQUEE.  AUT: zhaoxy . DATE: 2013-01-22 . START***/
	    if (isEmpty()) {
            return;
        }
	            
        c.save();
        c.clipRect(mClipRect);
	    
//		mPaint.setColor(0xffffffff);
//        if (Utilities.isLightColor(getTextColor())) {
//            mPaint.setShadowLayer(SHADOW_LARGE_RADIUS, 0.0f, 0, SHADOW_LARGE_COLOUR);
//        } else {
//            mPaint.setShadowLayer(1.0f, 0.0f, 0, SHADOW_LARGE_COLOUR);
//        }
//        
//        mPaint.setColor(textSetColor);
//        c.drawText(toDraw, textX, textBaseY, mPaint);
//        if (mDrawNext)
//        {
//        	c.drawText(toDraw2, textX2, textBaseY, mPaint);
//        }
        c.drawBitmap(mCurrentBitmap, mCurrentX, 0, mPaint);
        if (mDrawNext)
        {
        	c.drawBitmap(mNextBitmap, mNextX, 0, mPaint);
        }    
        
        c.restore();
                
//        mPaint.setShadowLayer(0, 0, 0, 0xffffffff);
////        new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN)
//        mFadePaint.setShader(shader1);
//        mFadePaint.setXfermode(mXfermode);
//        mFadePaint.setStyle(Style.FILL);
//        c.drawRect(new RectF(0, 0, mShadowLen, getHeight()), mFadePaint);
//        
//        mFadePaint.setShader(shader2);
//        mFadePaint.setXfermode(mXfermode);
//        mFadePaint.setStyle(Style.FILL);
//        c.drawRect(new RectF(getWidth() - mShadowLen, 0, getWidth(), getHeight()), mFadePaint);
//        mFadePaint.setShader(composeShader);
//        mFadePaint.setXfermode(mXfermode);
//        mFadePaint.setStyle(Style.FILL);
//        c.drawRect(new RectF(0, 0, getWidth(), getHeight()), mFadePaint);
        
//        if (mTextBGDrawable != null) {
//            mPaint.setColor(bubbleColor);
//            mPaint.setStyle(Style.FILL);
//            Xfermode temp = mPaint.getXfermode();
//            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
//            c.drawDrawable(mTextBGDrawable, mBackRect);
//            mPaint.setXfermode(temp);
//        }
        
//        mPaint.setColor(bubbleColor);
//        mPaint.setStyle(Style.FILL);
//        Xfermode temp = mPaint.getXfermode();
//        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
//        c.drawRoundRect(mBackRect, mCornerRadius, mCornerRadius, mPaint);
//        mPaint.setXfermode(temp);

	}
	
	private Drawable mTextBGDrawable;
	
    public void setTextBackgroundDrawable(Drawable d) {
    	mTextBGDrawable = d;
    }
    
    private ArrayList<Bitmap> mArrayList = new ArrayList<Bitmap>();
    private int mHideLen = 10;
//    private int mCurrentPage;
    private Bitmap mCurrentBitmap;
    private int mCurrentX;
    private Bitmap mNextBitmap;
    private int mNextX;
        
    public Bitmap getNumSnapshot(int num) {
    	String str = num + "";
	    int width = (int)localRect.width();
        int height = (int)localRect.height();
        if( width <= 0 || height <= 0 ){
            return null;
        }
        
        Bitmap face = Bitmap.createBitmap( width, height, Config.ARGB_8888);
        
        NormalDisplayProcess tmpProc = new NormalDisplayProcess();

        tmpProc.beginDisplay(face);
        
        mPaint.setColor(0xffffffff);
        if (Utilities.isLightColor(getTextColor())) {
            mPaint.setShadowLayer(SHADOW_LARGE_RADIUS, 0.0f, 0, SHADOW_LARGE_COLOUR);
        } else {
            mPaint.setShadowLayer(1.0f, 0.0f, 0, SHADOW_LARGE_COLOUR);
        }
        mPaint.setColor(textSetColor);
        float f = (float)width/2f;
        tmpProc.drawText(str, f, textBaseY, mPaint);
        tmpProc.getCanvas().setBitmap(null);
        tmpProc.endDisplay();
        
        return face;
	}
    
    public void generateSnapBitmap(int pageCount){
    	while (mArrayList.size() < pageCount)
    	{
    		mArrayList.add(getNumSnapshot(mArrayList.size() + 1));
    	}
    }
	
}
