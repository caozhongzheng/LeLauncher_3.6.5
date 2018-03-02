package com.lenovo.launcher2.customizer;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.slimengine.NormalDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;

public class TipsUtilities {

	public  static float  getOneNumerTipDrawableWidth (XContext context){
		final Drawable d = context.getResources().getDrawable(R.drawable.notify_background);
        Rect padding = new Rect();
        d.getPadding(padding);
		return d.getIntrinsicWidth() -padding.left-padding.right;
		
	}
    public static float getOneNumerTipDrawableWidth(XContext context, int size ){
        return size *getOneNumerTipDrawableWidth(context);

    }

    public static Bitmap  getTipDrawable(int num,XContext context) {
        Bitmap mTipBg = null;
        if (num > 0) {
            mTipBg = getTipDrawable(String.valueOf(num), context);
        }
        return mTipBg;
    }

    public static Bitmap getTipDrawable(String str, XContext context) {
        Bitmap mTipBg = null;
        if (str != null && !str.isEmpty()) {
            StringBuffer print = new StringBuffer();
            print.append(str);
            final Drawable d = context.getResources().getDrawable(R.drawable.notify_background);
            Rect padding = new Rect();
            d.getPadding(padding);
            int textSize =(d.getIntrinsicWidth() -padding.left-padding.right);
            Paint p = new Paint();
            p.setColor(Color.WHITE);
            p.setTextSize(textSize);
            p.setAntiAlias(true);
            p.setTextAlign(Align.CENTER);
            FontMetrics fm = new FontMetrics();
            p.getFontMetrics(fm);
            
            float textWidth = p.measureText(print.toString());
            float textHeight = fm.bottom-fm.top;
            
            Log.d("tippadding", "string = " + str +"     textWidth  = " +textWidth);
            Log.d("tippadding", "padding.top  = " + padding.top);
            Log.d("tippadding", "padding.bottom  = " + padding.bottom);
            Log.d("tippadding", "padding.left  = " + padding.left);
            Log.d("tippadding", "padding.right  = " + padding.right);
            Log.d("tippadding", "Width   = " + d.getIntrinsicWidth());
            Log.d("tippadding", "height   = " + d.getIntrinsicHeight());

            int tipWidth,tipHeight;
            tipWidth = padding.left + (int) textWidth + padding.right;
            tipHeight = d.getIntrinsicHeight();//padding.top + (int) textHeight + padding.bottom;

            if(tipWidth <d.getIntrinsicWidth()){
                tipWidth = d.getIntrinsicWidth();
            }
//            if(tipHeight < d.getIntrinsicHeight()){
//                tipHeight = d.getIntrinsicHeight();
//            }
            Log.d("tip", "tipWidth ="+ tipWidth);

            float textX = tipWidth / 2f;
            float textY = tipHeight - (tipHeight - textHeight) / 2 - fm.bottom;

            mTipBg = Bitmap.createBitmap(tipWidth, tipHeight, Config.ARGB_8888);
            NormalDisplayProcess c = new NormalDisplayProcess();
            c.beginDisplay(mTipBg);
            c.drawDrawable(d, new RectF(0, 0, tipWidth, tipHeight));
            c.drawText(print.toString(), textX, textY, p);
            c.endDisplay();
        }

        return mTipBg;
    }

    public  static TipPoint getTipDrawableRelativeParentPosition(float parentRightX,float childRightX,float childTopY
            ,float tipWidth,float tipHeight,XContext context){

        return getTipDrawableRelativeParentPosition(parentRightX, childRightX, childTopY
                , tipWidth, tipHeight, context, 1);
    }


    public  static TipPoint getTipDrawableRelativeParentPosition(float parentRightX,float childRightX,float childTopY
            ,float tipWidth,float tipHeight,XContext context, int size){
        TipPoint p = new TipPoint();
        float mOneNumberTipDrawableWidth = getOneNumerTipDrawableWidth(context, size);
        float tipTranslateX = Math.max(mOneNumberTipDrawableWidth/4f, parentRightX- childRightX);
        float tipTranslateY  =(childTopY-tipHeight/3f);
        tipTranslateY = tipTranslateY>0f ? tipTranslateY:0;
        p.x = parentRightX - (tipWidth - mOneNumberTipDrawableWidth/4 + tipTranslateX);
        p.y = tipTranslateY;
        return p;
    }

    public static  class TipPoint{
        public float x ;
        public float y;
    }
    private TipsUtilities(){

    }
}
