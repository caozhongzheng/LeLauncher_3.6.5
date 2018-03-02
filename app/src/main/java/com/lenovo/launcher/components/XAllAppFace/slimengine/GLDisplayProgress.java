package com.lenovo.launcher.components.XAllAppFace.slimengine;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.SurfaceHolder;
import android.view.View;

public class GLDisplayProgress implements IDisplayProcess {

	public GLDisplayProgress() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setup(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean beginDisplay(SurfaceHolder holder) {
		return false;

	}

	@Override
	public void endDisplay() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean clipRect(RectF clip) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void drawBitmap(Bitmap bitmap, float left, float top, Paint p) {
		// TODO Auto-generated method stub

	}

	@Override
	public int save() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void restore() {
		// TODO Auto-generated method stub

	}

	@Override
	public void concat(Matrix m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void translate(float left, float top) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beginDisplay(Bitmap bitmap) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawRoundRect(RectF rectF, float rx, float ry, Paint mPaint) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawText(String toDraw, float f, float textBaseY, Paint mPaint) {
		// TODO Auto-generated method stub

	}

    @Override
    public void drawDrawable(android.graphics.drawable.Drawable d) {
    }

	@Override
	public void drawDrawable(android.graphics.drawable.Drawable d, RectF Rect) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean beginDisplay(Canvas holder) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void drawBitmap(Bitmap bitmap, Matrix m, Paint p) {
		// TODO Auto-generated method stub
	}


	@Override
	public void drawBitmap(Bitmap bitmap, Rect src, Rect dst, Paint paint) {
		// TODO Auto-generated method stub

	}

	@Override
	public Canvas getCanvas() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void drawView(View v, Matrix m, RectF rect) {
		// TODO Auto-generated method stub
		
	}

	/*** RK_ID: TEXT_MARQUEE.  AUT: zhaoxy . DATE: 2013-01-22 . START***/
    @Override
    public void drawRect(RectF rectF, Paint mPaint) {
    }

    @Override
    public void drawRect(float left, float top, float right, float bottom, Paint paint) {
    }
    /*** RK_ID: TEXT_MARQUEE.  AUT: zhaoxy . DATE: 2013-01-22 . END***/

}
