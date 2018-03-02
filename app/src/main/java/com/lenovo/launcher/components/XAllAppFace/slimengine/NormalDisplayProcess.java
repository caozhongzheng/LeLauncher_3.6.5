package com.lenovo.launcher.components.XAllAppFace.slimengine;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.SurfaceHolder;
import android.view.View;

//import com.lenovo.launcher.components.XAllAppFace.XClipDrawable;

public class NormalDisplayProcess implements IDisplayProcess {

	private Canvas mCanvas;
	private SurfaceHolder mTarget;
	public static PaintFlagsDrawFilter paintFilter = new PaintFlagsDrawFilter(
			0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
//	public static PorterDuffXfermode duffXfermode = new PorterDuffXfermode(
//			PorterDuff.Mode.CLEAR);
//	private Paint eraser = new Paint();
	
	public NormalDisplayProcess(Canvas c){
		this();
		mCanvas = c;
	}

	public NormalDisplayProcess() {
		mTarget = null;
//		eraser.setXfermode(duffXfermode);
//		eraser.setStyle(Style.STROKE);
	}

	@Override
	public void setup(SurfaceHolder holder) {
		mTarget = holder;
	}

	@Override
	public boolean beginDisplay(SurfaceHolder holder) {
		// Paint p = new Paint();
		// p.setXfermode(duffXfermode);
		// p.setStyle(Style.STROKE);

		mCanvas = mTarget.lockCanvas();

		if (mCanvas == null) {
			return false;
		}

		mCanvas.setDrawFilter(paintFilter);
//		mCanvas.drawPaint(eraser);
		return true;
	}

	@Override
	public void beginDisplay(Bitmap bitmap) {
		if (mCanvas == null) {
			mCanvas = new Canvas();
			mCanvas.setBitmap(bitmap);
		}
	}

	@Override
	public void endDisplay() {
		// if (mTarget != null) {
		// mTarget.unlockCanvasAndPost(mCanvas);
		// }
		if (mCanvas != null) {
			mCanvas = null;
		}
	}

	@Override
	public boolean clipRect(RectF clip) {
		return mCanvas.clipRect(clip);
	}

	@Override
	public void drawBitmap(Bitmap bitmap, float left, float top, Paint p) {
		if ((bitmap != null && bitmap.isRecycled()) || bitmap == null ){
			return;
		}
		if (p != null) {
		    p.setDither( true );
        }
		mCanvas.drawBitmap(bitmap, left, top, p);
	}

	@Override
	public int save() {
		return mCanvas.save();
	}

	@Override
	public void restore() {
		mCanvas.restore();
	}

	@Override
	public void concat(Matrix m) {
		mCanvas.concat(m);
	}

	@Override
	public void translate(float left, float top) {
		mCanvas.translate(left, top);
	}

	@Override
	public void drawRoundRect(RectF rectF, float rx, float ry, Paint mPaint) {
		mCanvas.drawRoundRect(rectF, rx, ry, mPaint);
	}

	/*** RK_ID: TEXT_MARQUEE.  AUT: zhaoxy . DATE: 2013-01-22 . START***/
	@Override
	public void drawRect(RectF rectF, Paint mPaint) {
	    mCanvas.drawRect(rectF, mPaint);
	}

	@Override
    public void drawRect(float left, float top, float right, float bottom, Paint paint) {
        mCanvas.drawRect(left, top, right, bottom, paint);
    }
	/*** RK_ID: TEXT_MARQUEE.  AUT: zhaoxy . DATE: 2013-01-22 . END***/

	@Override
	public void drawText(String toDraw, float f, float textBaseY, Paint mPaint) {
		mCanvas.drawText(toDraw, f, textBaseY, mPaint);
	}

	@Override
    public void drawDrawable(android.graphics.drawable.Drawable d) {
	    d.draw(mCanvas);
	}

	@Override
	public void drawDrawable(android.graphics.drawable.Drawable d, RectF Rect) {
		int w = (int) Rect.width();// d.getIntrinsicWidth();
		int h = (int) Rect.height();// d.getIntrinsicHeight();
		d.setBounds(0, 0, w, h);
		mCanvas.translate(Rect.left, Rect.top);
		d.draw(mCanvas);
		mCanvas.translate(-Rect.left, -Rect.top);
	}

//	@Override
//	public void drawObject(XClipDrawable d, RectF localRect) {
//		d.draw(mCanvas);
//	}

	@Override
	public boolean beginDisplay(Canvas canvas) {
		mCanvas = canvas;
		mCanvas.setDrawFilter(paintFilter);
		return mCanvas != null;
	}

	@Override
	public void drawBitmap(Bitmap bitmap, Matrix m, Paint p) {
		if ((bitmap != null && bitmap.isRecycled()) || bitmap == null ){
			return;
		}

		mCanvas.drawBitmap(bitmap, m, p);
	}

	@Override
	public void drawView(View v, Matrix m, RectF rect) {

		if (v != null) {
			if (m != null) {
				mCanvas.concat(m);
			}
			mCanvas.save();

//			v.offsetLeftAndRight( 30 );
//			v.offsetTopAndBottom( 30 );
			v.draw(mCanvas);
//			v.offsetLeftAndRight( -30 );
//			v.offsetTopAndBottom( -30 );

			mCanvas.restore();
		}
	}

	@Override
	public void drawBitmap(Bitmap bitmap, Rect src, Rect dst, Paint paint) {
		if ((bitmap != null && bitmap.isRecycled()) || bitmap == null ){
			return;
		}

		mCanvas.drawBitmap(bitmap, src, dst, paint);
	}

	@Override
	public Canvas getCanvas() {
		return mCanvas;
	}

}
