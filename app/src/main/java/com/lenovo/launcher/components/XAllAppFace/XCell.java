package com.lenovo.launcher.components.XAllAppFace;

import java.util.Random;

import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.view.MotionEvent;
import android.view.Window;

import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;
import com.lenovo.launcher.components.XAllAppFace.utilities.Debug.D2;
import com.lenovo.launcher2.customizer.Debug.R5;

public class XCell extends DrawableItem {

	private float cellWidth = XPagedViewItem.INVALID_CELL_SCALE;
	private float cellHeight = XPagedViewItem.INVALID_CELL_SCALE;

	public static final long CONTAINER_NO_ID = -1;
	private long containerId = CONTAINER_NO_ID;
	public boolean isInvalid = true;
	private DrawableItem mDrawingTarget = null;

	private int cellX = 0;
	private int cellY = 0;
	public int screen = 0;

	private int mCurrentPage = 0;

	public int offsetPixelX = 0;
	public int offsetPixelY = 0;

	private RectF clipRectF = null;

	private RectF drawingRect = null;

	public XCell(XContext context, RectF rect, DrawableItem drawingTarget) {

		super(context);

		resize(rect);
		mDrawingTarget = drawingTarget;

		disableCache();
		// enableCache();

		if (mDrawingTarget != null && mDrawingTarget instanceof ClipableItem)
			mDrawingTarget.disableCache();
	}

	@Override
	public void draw(IDisplayProcess c) {

		if (isInvalid || mDrawingTarget == null)
			return;

		if (!mDrawingTarget.isVisible()) {
			return;
		}

		super.draw(c);
	}
	
	private Matrix extraEffectMatrix;
	public void setExtraEffectMatrix(Matrix extraEffect){
	    if(extraEffectMatrix == null){
            extraEffectMatrix = new Matrix();
        }
		extraEffectMatrix.set(extraEffect);
	}
	
	public Matrix getExtraEffectMatrix(){
		if(extraEffectMatrix == null){
			extraEffectMatrix = new Matrix();
		}
		return extraEffectMatrix;
	}
	
	/** Do not USE ! */
	private Matrix hackingMatrix;
	
	/** Do not USE ! */
	public void setHackingMatrix(Matrix extraEffect1){
		hackingMatrix = new Matrix(extraEffect1);
	}
	
	/** Do not USE ! */
	public Matrix getHackingMatrix(){
		if(hackingMatrix == null){
			hackingMatrix = new Matrix();
		}
		return hackingMatrix;
	}

	@Override
	public void onDraw(IDisplayProcess c) {
		c.save();
		
		if( extraEffectMatrix != null && !extraEffectMatrix.isIdentity() ){
			c.concat( extraEffectMatrix );
		}
		
		if( hackingMatrix != null && !hackingMatrix.isIdentity() ){
			c.concat( hackingMatrix );
		}

		if (mDrawingTarget instanceof ClipableItem) {
			
			if (drawingRect == null) {
				drawingRect = new RectF(offsetPixelX, offsetPixelY,
						(offsetPixelX + cellWidth), (offsetPixelY + cellHeight));
			}

			final ClipableItem targetItem = (ClipableItem) mDrawingTarget;
//			android.util.Log.i("D2", "drawingRect. : " + drawingRect.toString()
//			+ ", cellWH: (" + cellWidth + "," + cellHeight + ")");
			
			targetItem.onSetDrawingRectF(drawingRect);
//			targetItem.onSetClipRectF(clipRectF);
//			targetItem.updateFinalAlpha();
			targetItem.setAlpha(getFinalAlpha());
			targetItem.draw(c);
		} else if (mDrawingTarget instanceof DrawableItem) {
			mDrawingTarget.setAlpha(getFinalAlpha());
			mDrawingTarget.draw(c);
		}
		
		// For Debug
		if(XPagedView.DEBUG_CELL){
			c.save();
			XPagedView host = ((XPagedView) getParent());
			int[] loc = new int[3];
			int index = host.getChildIndex(this);
			host.getInfoFromIndex(index, loc);
			boolean occupy = !host.isCellparamValid(loc[1], loc[2], 1, 1);
			Paint p = new Paint();
			p.setColor(Color.RED);
			p.setStyle(Style.STROKE);
			p.setTextSize(25f);
			c.getCanvas().drawText(" cid : " + containerId + "", 10f, 30f, p);

			c.getCanvas().drawText(" rx : " + getRelativeX() + "", 10f,
					getHeight() / 3, p);
			c.getCanvas().drawText(" ry : " + getRelativeY() + "", 10f,
					getHeight() / 2, p);
			Random rs = new Random();
			p.setColor(Color.rgb(rs.nextInt(250), rs.nextInt(250),
					rs.nextInt(250)));
			RectF r = new RectF(localRect);
			r.offsetTo(0f, 0f);
			p.setStrokeWidth(4f);
			c.drawRect(r, p);

			if (occupy) {
				p.setStyle(Style.FILL);
				p.setColor(Color.argb(100, 0, 0, 200));
				c.drawRect(r, p);
			}
			c.restore();
		}
	

		c.restore();
	}
	
	public void setDrawingTarget(DrawableItem itemToDraw){
//		mDrawingTarget.clean();
		mDrawingTarget = itemToDraw;
	}

	public void onAttach(float cellWidth, float cellHeight) {
		this.cellWidth = cellWidth;
		this.cellHeight = cellHeight;
	}

	public void setCellXY(int cellX, int cellY) {
		// in paged view item
		offsetPixelX = (int) (cellX * this.cellWidth);
		offsetPixelY = (int) (cellY * this.cellHeight);

//		clipRectF = new RectF(getRelativeX(), getRelativeY(), getRelativeX()
//				+ localRect.width(), getRelativeY() + localRect.height());

		this.cellX = cellX;
		this.cellY = cellY;

		isInvalid = false;
	}

	public int getCellX() {
		return cellX;
	}

	public int getCellY() {
		return cellY;
	}

	public long getContainerId() {
		return containerId;
	}

	public void setContainerId(long container) {
		containerId = container;
	}

	public DrawableItem getDrawingTarget() {
		return mDrawingTarget;
	}
	
	@Override
	public void setInvertMatrixDirty() {
		super.setInvertMatrixDirty();
		if (mDrawingTarget != null) {
			mDrawingTarget.setInvertMatrixDirty();
		}
	}
	
	@Override
	public void setPaint(Paint paint) {
		super.setPaint(paint);
		if (mDrawingTarget != null) {
			mDrawingTarget.setPaint(paint);
		}
	}

	@Override
	public boolean onDown(MotionEvent e) {

	    if (mDrawingTarget == null) {
            return false;
        }
	    super.onDown(e);
		return mDrawingTarget.onDown(e);
	}

	@Override
	public boolean onFingerCancel(MotionEvent e) {
	    if (mDrawingTarget == null) {
            return false;
        }
	    super.onFingerCancel(e);
		return mDrawingTarget.onFingerCancel(e);
	}

	@Override
	public boolean onFingerUp(MotionEvent e) {
	    if (mDrawingTarget == null) {
            return false;
        }
	    super.onFingerUp(e);
		return mDrawingTarget.onFingerUp(e);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
	    if (mDrawingTarget == null) {
            return false;
        }
	    super.onFling(e1, e2, velocityX, velocityY);
		return mDrawingTarget.onFling(e1, e2, velocityX, velocityY);
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
	    if (mDrawingTarget == null) {
            return false;
        }
	    super.onSingleTapUp(e);
		return mDrawingTarget.onSingleTapUp(e);
	}

	@Override
	public boolean onShowPress(MotionEvent e) {
	    if (mDrawingTarget == null) {
            return false;
        }
	    super.onShowPress(e);
		return mDrawingTarget.onShowPress(e);
	}

	@Override
    public boolean onLongPress(MotionEvent e) {
        if (mDrawingTarget == null) {
            return false;
        }
        super.onLongPress(e);
        return mDrawingTarget.onLongPress(e);
    }

    public boolean isEmpty() {
        return mDrawingTarget == null;
    }

    @Override
    public void onTouchCancel( MotionEvent e ) {
        if (mDrawingTarget == null) {
            return;
        }
        super.onTouchCancel( e );
	    mDrawingTarget.onTouchCancel( e );
	}
    
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY, float previousX, float previousY) {
		if (mDrawingTarget == null) {
			return false;
		}
		super.onScroll(e1, e2, distanceX, distanceY, previousX, previousY);
		return mDrawingTarget.onScroll(e1, e2, distanceX, distanceY, previousX, previousY);
	}
    
//    @Override
//    public void setPressed(boolean pressed) {
//    	if(mDrawingTarget != null){
//    		mDrawingTarget.setPressed(pressed);
//    	}
//    }
    

    @Override
    public void clean() {    	
//		if (mDrawingTarget != null) {
//			mDrawingTarget.clean();
//		}
		super.clean();
	}
    
	public void setTouchable(boolean touchable) {
	    super.setTouchable(touchable);
	    if (mDrawingTarget != null)
	    {
	    	mDrawingTarget.setTouchable(touchable);
	    }
	}
}
