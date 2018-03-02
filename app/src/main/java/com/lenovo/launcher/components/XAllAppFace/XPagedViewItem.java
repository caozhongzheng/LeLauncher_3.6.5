package com.lenovo.launcher.components.XAllAppFace;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.RectF;
import android.util.FloatMath;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.lenovo.launcher.components.XAllAppFace.ClipableItem.IFragAction;
import com.lenovo.launcher.components.XAllAppFace.XPagedView.PageSwitchListener;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;
import com.lenovo.launcher.components.XAllAppFace.utilities.Debug.D2;
import com.lenovo.launcher.components.XAllAppFace.utilities.Debug.R2;
import com.lenovo.launcher.components.XAllAppFace.utilities.Utilities;
import com.lenovo.launcher2.commoninterface.ItemInfo;
import com.lenovo.launcher2.customizer.Debug.R5;
import com.lenovo.launcher2.weather.widget.ChineseCalendar;

public class XPagedViewItem implements PageSwitchListener {
	//
	public static final float INVALID_CELL_SCALE = -1;
	private float sCellWidthInPixel = INVALID_CELL_SCALE;
	private float sCellHeightInPixel = INVALID_CELL_SCALE;

	// the item id in our pagedView or consider as index
	private long mID = -1;

	private ItemInfo mInfo;

	DrawableItem mDrawingTarget;

	boolean isCombo = false;

	// our cells of this item
	private XCell[] mCells;

	private XContext mXContext;
	private XPagedView parent;

	boolean isMoving = false;
	public float realTargetRelativeX;
	public float realTargetRelativeY;

	public XPagedViewItem(XContext context, DrawableItem drawingTarget,
			ItemInfo info) {

		this.mXContext = context;

		mDrawingTarget = drawingTarget;

		mInfo = info;
	}

	public void onAttach(XPagedView host, float cellXWidth, float cellYHeight) {

		this.parent = host;
		setCellWidth(cellXWidth);
		setCellHeight(cellYHeight);

		initCells();

		// extra handle for different drawing targets
		slotDrawingTarget(host);
		
		this.isCombo = mCells.length > 1;
	}

	private void slotDrawingTarget(XPagedView host) {

		if (mDrawingTarget instanceof ClipableItem) {
			((ClipableItem) mDrawingTarget).onSlot(mInfo, new RectF(0, 0,
					mInfo.spanX * sCellWidthInPixel, mInfo.spanY
							* sCellHeightInPixel), host, this);
		}
	}

	private void initCells() {

		// may be not a clipable item
		if (!(mDrawingTarget instanceof ClipableItem)) {
			// resize the item to rect of one cell
			mDrawingTarget.resize(new RectF(0, 0, sCellWidthInPixel,
					sCellHeightInPixel));
		}

//		mInfo.spanX = (int) FloatMath.ceil(mDrawingTarget.getWidth()
//				/ sCellWidthInPixel);
//		mInfo.spanY = (int) FloatMath.ceil(mDrawingTarget.getHeight()
//				/ sCellHeightInPixel);

		if (mCells == null) {
			mCells = new XCell[mInfo.spanX * mInfo.spanY];
		}

		android.util.Log.i("xpage", "spanX , spanY  : " + mInfo.spanX + " , "
				+ mInfo.spanY);

		for (int i = 0; i < mCells.length; ++i) {
			final RectF r = new RectF(0, 0, sCellWidthInPixel,
					sCellHeightInPixel);
			mCells[i] = new XCell(mXContext, r, mDrawingTarget);
			mCells[i].onAttach(sCellWidthInPixel, sCellHeightInPixel);
			mCells[i].setCellXY(i % mInfo.spanX, i / mInfo.spanX);
			//
			mCells[i].setRelativeX((mInfo.cellX + mCells[i].getCellX())
					* sCellWidthInPixel);
			mCells[i].setRelativeY((mInfo.cellY + mCells[i].getCellY())
					* sCellHeightInPixel);

			mCells[i].setDrawingTarget(mDrawingTarget);
			mCells[i].setContainerId(mID);
			/*** RK_ID: TEST_TOUCH. AUT: zhaoxy . DATE: 2013-02-18 . START ***/
			if (i == 0) {
				mDrawingTarget.setPrent(mCells[i]);
			}
			/*** RK_ID: TEST_TOUCH. AUT: zhaoxy . DATE: 2013-02-18 . END ***/
		}

	}

	public void updateItemInfo() {
		// TODO only data, no position
	}

	public void updateItemInfo(ItemInfo info) {
		mInfo = info;

		initCells();
	}

	public void setId(long idToSet) {
		mID = idToSet;
	}

	public long getId() {
		return mID;
	}

	public XCell[] getCells() {

		return mCells;
	}

	public ItemInfo getInfo() {
		return mInfo;
	}

	public void setCellWidth(float cellWidth) {
		sCellWidthInPixel = cellWidth;
	}

	public void setCellHeight(float cellHeight) {
		sCellHeightInPixel = cellHeight;
	}

	public float getCellWidth() {
		return sCellWidthInPixel;
	}

	public float getCellHeight() {
		return sCellHeightInPixel;
	}

	@Override
	public void onUpdatePage(int pageCount, int currentPage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSwitching(int from, int to, float percentage) {
		if (mDrawingTarget != null) {

			if (mDrawingTarget instanceof IFragAction) {
				((IFragAction) mDrawingTarget).onFragScrolling(this, from, to);
			}

			mDrawingTarget.resetPressedState();
		}
	}

	@Override
	public void onPageBeginMoving(int currentPage) {

		if (mDrawingTarget != null) {

			if (mDrawingTarget instanceof IFragAction) {
				((IFragAction) mDrawingTarget).onFragBeginMove(this,
						currentPage);
			}

//			mDrawingTarget.resetPressedState();

		}
	}

	@Override
	public void onPageEndMoving(int currentPage) {
		if (mDrawingTarget != null) {
			if (mDrawingTarget instanceof IFragAction) {
				((IFragAction) mDrawingTarget).onFragEndMove(this, currentPage);
			}
		}
	}

	public DrawableItem getDrawingTarget() {
		return mDrawingTarget;
	}

	protected void setDrawingTarget(DrawableItem itemToDraw) {
		// mDrawingTarget.clean();
		mDrawingTarget = itemToDraw;
	}

	public void onRemoveFromPage(int currentPage) {
		if (mDrawingTarget instanceof IFragAction) {
			((IFragAction) mDrawingTarget).onFragBeginMove(this, currentPage);
		}
	}

	public void onMoving(XPagedViewItem itemMoving, int screen, int cellX,
			int cellY) {
		if (mDrawingTarget instanceof IFragAction) {
			((IFragAction) mDrawingTarget).onMovingTo(this, screen, cellX,
					cellY);
		}
	}

	public void setTag(Object o) {
		if (mDrawingTarget != null) {
			mDrawingTarget.setTag(o);
		}
	}

	// for animating
	private boolean mAnimatingToPosition = false;

	public void setAnimatingToPostion(boolean animating) {
		mAnimatingToPosition = animating;
	}

	public boolean isAnimating() {
		return mAnimatingToPosition;
	}

	public ItemInfo moveToTargetPosition(int screen, int cellX, int cellY,
			int duration, int delay, Runnable taskToSchedule,
			boolean[][] occupied, boolean animate) {

		isMoving = true;

		// android.util.Log.i("order", this + "in ----- 5");
		final ItemInfo originInfo = new ItemInfo(mInfo);
		final int fromSpanX = originInfo.spanX;
		final int fromSpanY = originInfo.spanY;

		/*
		 * // 判断指定位置是否有足够空位 int[] cellXY = new int[2]; cellXY[0] = cellX;
		 * cellXY[1] = cellY; if (itemTarget != null && itemTarget.getId() !=
		 * mID) { D2.echo("false detected pending ....  " + mInfo); return
		 * false; }
		 */

		// 判断指定位置是否有足够空位
		int[] cellXY = new int[2];
		cellXY[0] = cellX;
		cellXY[1] = cellY;
		// if (!parent.findCellForSpanThatIntersectsIgnoring(cellXY, fromSpanX,
		// fromSpanY, -1, -1, mInfo, occupied)) {
		// R5.echo("moveItemToLocation2 no space");
		// return originInfo;
		// }
		// if(!parent.findCellForSpanRightHere(cellX, cellY, originInfo.spanX,
		// originInfo.spanY, occupied)){
		// return originInfo;
		// }

		final int newCellGroupIndex00 = parent.getCellIndex(screen, cellX,
				cellY);
		// 目标位置各cell在child数组中index
		final int[] toArray = new int[originInfo.attachedIndexArray.length];
		// 原位置各cell在child数组中index
		int[] fromArray = originInfo.attachedIndexArray.clone();
		
		if (XPagedView.DEBUG_ANIM_TO)R5.echo("attachedIndexArray fromArray = " + fromArray[0]);

		// 目标位置数组赋值
		int count = 0;
		for (int j = 1; j <= fromSpanY; j++) {
			final int slotPosition = (j - 1) * parent.getCellCountX()
					+ newCellGroupIndex00;
			for (int i = 1; i <= fromSpanX; i++) {
				toArray[count++] = slotPosition + i - 1;
			}
		}

		// arrange orientation for unit
		int cellMinX = mInfo.cellX;
		int cellMinY = mInfo.cellY;

		int[] tmpArrayTo = toArray.clone();

		boolean l = false, r = false, t = false, b = false;

		if (cellX < cellMinX) {
			l = true;
		}
		if (cellX > cellMinX) {
			r = true;
		}
		if (cellY < cellMinY) {
			t = true;
		}
		if (cellY > cellMinY) {
			b = true;
		}
		
		int scrollOffset = 0;
		boolean needOffset = false;
		    
		if (fromArray[0] > 255 - parent.mCellCountX * parent.mCellCountY
		        || toArray[0] > 255 - parent.mCellCountX * parent.mCellCountY)
		{
		    needOffset = true;
		    scrollOffset = fromArray[0] - (fromArray[0] % (parent.mCellCountX * parent.mCellCountY));
		    if (XPagedView.DEBUG_ANIM_TO)R5.echo("scrollOffset = " + scrollOffset);    
		    for (int i = 0; i < fromArray.length; i++) {
		        fromArray[i] = fromArray[i] - scrollOffset;
		        toArray[i] = toArray[i] - scrollOffset;
		    }
		}

		// left
		if (l && !(r || t || b)) {
			if (XPagedView.DEBUG_ANIM_TO)
				android.util.Log.i("orderX", "left");
		}
		// right
		else if (r && !(l || t || b)) {
			if (XPagedView.DEBUG_ANIM_TO)
				android.util.Log.i("orderX", "right");
			Utilities.newInstance().convert(fromArray, Utilities.RIGHT,
					fromSpanX, fromSpanY);
			Utilities.newInstance().convert(toArray, Utilities.RIGHT,
					fromSpanX, fromSpanY);
		}
		// top
		else if (t && !(l || r || b)) {
			if (XPagedView.DEBUG_ANIM_TO)
				android.util.Log.i("orderX", "top");
		}
		// bottom
		else if (b && !(l || r || t)) {
			if (XPagedView.DEBUG_ANIM_TO)
				android.util.Log.i("orderX", "bottom");
			Utilities.newInstance().convert(fromArray, Utilities.BOTTOM,
					fromSpanX, fromSpanY);
			Utilities.newInstance().convert(toArray, Utilities.BOTTOM,
					fromSpanX, fromSpanY);
		}
		// l - t
		else if ((l && t) && !(r || b)) {
			if (XPagedView.DEBUG_ANIM_TO)
				android.util.Log.i("orderX", "l - t");
			Utilities.newInstance().convert(fromArray, Utilities.LEFT_TOP,
					fromSpanX, fromSpanY);
			Utilities.newInstance().convert(toArray, Utilities.LEFT_TOP,
					fromSpanX, fromSpanY);
		}
		// l - b
		else if ((l && b) && !(r || t)) {
			if (XPagedView.DEBUG_ANIM_TO)
				android.util.Log.i("orderX", "l - b");

			Utilities.newInstance().convert(fromArray, Utilities.LEFT_BOTTOM,
					fromSpanX, fromSpanY);
			Utilities.newInstance().convert(toArray, Utilities.LEFT_BOTTOM,
					fromSpanX, fromSpanY);
			// return true;
		}
		// r - t
		else if ((r && t) && !(l || b)) {
			if (XPagedView.DEBUG_ANIM_TO)
				android.util.Log.i("orderX", "r - t");
			Utilities.newInstance().convert(fromArray, Utilities.RIGHT_TOP,
					fromSpanX, fromSpanY);
			Utilities.newInstance().convert(toArray, Utilities.RIGHT_TOP,
					fromSpanX, fromSpanY);
			// return true;
		}
		// r - b
		else if ((r && b) && !(l || t)) {
			if (XPagedView.DEBUG_ANIM_TO)
				android.util.Log.i("orderX", "r - b");
			Utilities.newInstance().convert(fromArray, Utilities.RIGHT_BOTTOM,
					fromSpanX, fromSpanY);
			Utilities.newInstance().convert(toArray, Utilities.RIGHT_BOTTOM,
					fromSpanX, fromSpanY);
		}
		else
		{
		    R5.echo("animate error");
		}
		
        if (needOffset)
        {
            for (int i = 0; i < fromArray.length; i++) {
                fromArray[i] = fromArray[i] + scrollOffset;
                toArray[i] = toArray[i] + scrollOffset;
            }
        }

		int[] loc = new int[2];
		int[] tmp = new int[2];
		int[] fromInfo = new int[3];
		int[] toInfo = new int[3];	
		
		for (int i = 0; i < fromArray.length; i++) {
			// R5.echo("from = " + fromArray[i] + "to " + toArray[i]);
			final XCell fromCell = (XCell) parent.getChildAt(fromArray[i]);
			XCell toCell = (XCell) parent.getChildAt(toArray[i]);
			
			if (XPagedView.DEBUG_ANIM_TO)R5.echo("i = " + i + "fromArray = " + fromArray[i] + "toArray = " + toArray[i]);

			if (fromCell == null || toCell == null) {
			    R5.echo("foudcell errror");
				continue;
			}

			parent.getInfoFromIndex(fromArray[i], fromInfo);
			parent.getInfoFromIndex(toArray[i], toInfo);
			parent.getRelativeXY(fromInfo[1], fromInfo[2], loc);
			parent.getRelativeXY(toInfo[1], toInfo[2], tmp);

			parent.markCellsForView(fromInfo[1], fromInfo[2], 1, 1,
					fromInfo[0], false);
			parent.markCellsForView(toInfo[1], toInfo[2], 1, 1, toInfo[0], true);
			parent.exchangeChildren(fromArray[i], toArray[i]);
			
			toCell.setRelativeX(loc[0]);
			toCell.setRelativeY(loc[1]);
			fromCell.setRelativeX(tmp[0]);
			fromCell.setRelativeY(tmp[1]);
			fromCell.setVisibility(true);

			//add by zhanggx1.s for moving an object from one page to another one.
			int screenFrom = fromInfo[0];
			int screenTo = toInfo[0];			
			if (screenFrom > screenTo) {
				loc[0] += (screenFrom - screenTo) * parent.getWidth();
			} else if (screenFrom < screenTo) {
				tmp[0] += (screenTo - screenFrom) * parent.getWidth();
			}
			
			if (XPagedView.DEBUG_ANIM_TO)R5.echo("screenFrom = " + screenFrom + "screenTo = " + screenTo);
			//add by zhanggx1.e
			parent.refreshBitmapCache(screenFrom);
			parent.refreshBitmapCache(screenTo);
			
			if (animate) {
                animationItemToRelative(fromCell, loc, tmp, duration, delay, taskToSchedule);
                parent.noNeedGenerateBitmapCache(screenTo);
            }
		}

		mInfo.screen = screen;
		mInfo.cellX = cellX;
		mInfo.cellY = cellY;
		mInfo.attachedIndexArray = tmpArrayTo;

		if (mDrawingTarget instanceof XViewContainer) {
			XViewContainer container = (XViewContainer) mDrawingTarget;
			container.onMovingTo(this, screen, cellX, cellY);
			if (XPagedView.DEBUG_ANIM_TO)
				R5.echo("container.onMovingTo cellX = " + cellX + "cellY = "
						+ cellY);
		}

		// android.util.Log.i("order", this + "in ----- 6");
		return originInfo;
	}

	private void animationItemToRelative(final XCell fromCell,
			final int[] fromRelative, final int[] toRelative, int duration,
			int timeToDelay, final Runnable taskToSchedule) {

		final float deltaX = toRelative[0] - fromRelative[0];
		final float deltaY = toRelative[1] - fromRelative[1];

		fromCell.getHackingMatrix().postTranslate(-deltaX, -deltaY);

		final ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
		anim.setDuration(duration);
		anim.setStartDelay(timeToDelay);
		anim.setInterpolator(new AccelerateDecelerateInterpolator());
		anim.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				if (taskToSchedule != null) {
					taskToSchedule.run();
				}
				
				parent.generateBitmapCacheAll();				
			}

			@Override
			public void onAnimationCancel(Animator animation) {
			}
		});

		anim.addUpdateListener(new AnimatorUpdateListener() {

			private float cursorLast = 0f;

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				final float value = (Float) animation.getAnimatedValue();
				final float step = value - cursorLast;
				final float stepX = deltaX * step;
				final float stepY = deltaY * step;

				fromCell.getHackingMatrix().postTranslate(stepX, stepY);

				cursorLast = (Float) animation.getAnimatedValue();
			}
		});

		mXContext.getRenderer().injectAnimation(anim, false);

	}
	
	public void OnVisibilityChange(boolean visible){
		if(mDrawingTarget != null){
			if( mDrawingTarget instanceof XViewContainer ){
				final XViewContainer c = ((XViewContainer)mDrawingTarget);
				if(c.getMyScreen() == c.getCurrentScreen()){
					c.manageVisibility(XViewContainer.VISIBILITY_SHOW_VIEW, null);
				}else{
					c.manageVisibility(XViewContainer.VISIBILITY_SHOW_NONE, null);
				}
			}
		}
	}
	
	// no param
	protected void resize() {
		if (mDrawingTarget == null) {
			return;
		}

		final RectF r = new RectF(0, 0, mInfo.spanX * sCellWidthInPixel,
				mInfo.spanY * sCellHeightInPixel);
		mDrawingTarget.resize(r);
	}
}
