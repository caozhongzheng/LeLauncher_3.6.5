package com.lenovo.launcher.components.XAllAppFace.slimengine;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.SurfaceHolder;
import android.view.View;


public interface IDisplayProcess {

	public void setup(SurfaceHolder holder);
	
	public boolean beginDisplay(SurfaceHolder holder);
	
	public void beginDisplay(Bitmap bitmap);

	public boolean beginDisplay(Canvas holder);
	
	public void endDisplay();
		
	public boolean clipRect(RectF clip);
	
	public void drawBitmap(Bitmap bitmap, float left, float top, Paint p);
	
	public int save();
	
	public void restore();
	
	public void concat(Matrix m);
	
	public void translate(float left, float top);

	public void drawRoundRect(RectF rectF, float rx, float ry,
			Paint mPaint);

	/*** RK_ID: TEXT_MARQUEE.  AUT: zhaoxy . DATE: 2013-01-22 . START***/
	public void drawRect(RectF rectF, Paint mPaint);

	public void drawRect(float left, float top, float right, float bottom, Paint paint);
	/*** RK_ID: TEXT_MARQUEE.  AUT: zhaoxy . DATE: 2013-01-22 . END***/

	public void drawText(String toDraw, float f, float textBaseY, Paint mPaint);

	public void drawDrawable(android.graphics.drawable.Drawable d);
		
    public void drawDrawable(android.graphics.drawable.Drawable d, RectF Rect);

    public void drawBitmap(Bitmap bitmap, Matrix m, Paint p);

	public void drawView(View v, Matrix m, RectF rect);

	public void drawBitmap(Bitmap bitmap, Rect src, Rect dst, Paint paint);
	
	public Canvas getCanvas();
}
