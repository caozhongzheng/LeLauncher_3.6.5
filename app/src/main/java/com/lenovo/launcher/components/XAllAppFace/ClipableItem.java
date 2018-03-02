package com.lenovo.launcher.components.XAllAppFace;

import android.graphics.Rect;
import android.graphics.RectF;

import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;
import com.lenovo.launcher2.commoninterface.ItemInfo;

public abstract class ClipableItem extends DrawableItem {

	protected ItemInfo mInfo;
	protected float mSlotX, mSlotY;
	protected int mScreen = 0;
	protected int mCurrentScreen = mScreen;
	protected RectF containerRegion = new RectF();

	
	protected XPagedView mHost = null;

	protected Rect drawRect = new Rect();
	protected Rect targetRect = null;
	
	protected RectF clipRectF = null;
	
	protected boolean firstAttach = false;

	public ClipableItem(XContext context) {
		super(context);
	}

	public void onSlot(ItemInfo info, RectF region, XPagedView host, XPagedViewItem itemToSlot) {
		
		if( host.getStage() == null ){
			throw new RuntimeException( "NULL.STAGE.NOT.INIT." );
		}
		
		firstAttach = true;

		mInfo = info;
		mScreen = info.screen;

		containerRegion.set(region);

		mSlotX = info.cellX * (region.width() / info.spanX);
		mSlotY = info.cellY * (region.height() / info.spanY);

		mHost = host;

	}

	public void onSetDrawingRectF(RectF r) {

		if (r != null) {
			drawRect.left = (int) r.left;
			drawRect.top = (int) r.top;
			drawRect.right = (int) r.right;
			drawRect.bottom = (int) r.bottom;
			targetRect = new Rect(0, 0, drawRect.width(), drawRect.height());
		}
	}
	
//	public void onSetClipRectF(RectF clipRF){
//		if(clipRF != null){
//			clipRectF = new RectF(clipRF);
//		}
//	}

	public interface IFragAction {

		public void onFragBeginMove(XPagedViewItem itemMoving, int currentPage);

		public void onFragScrolling(XPagedViewItem itemMoving, int from, int to);

		public void onFragEndMove(XPagedViewItem itemMoving, int currentPage);
		
		public void onMovingTo( XPagedViewItem itemMoving, int screen, int cellX, int cellY );
		
	}
	
	@Override
	public float getParentAlpha() {
	    return 1f;
	}

	public int getCurrentScreen(){
		return mCurrentScreen;
	}

	public int getMyScreen(){
		return mScreen;
	}
	
	public void setMyScreen( int screen ){
		mScreen = screen;
	}
}
