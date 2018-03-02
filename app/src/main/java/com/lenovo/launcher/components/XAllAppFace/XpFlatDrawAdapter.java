package com.lenovo.launcher.components.XAllAppFace;

import android.graphics.Matrix;
import android.graphics.RectF;

import com.lenovo.launcher.components.XAllAppFace.XPagedView.PageDrawAdapter;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IDisplayProcess;

public abstract class XpFlatDrawAdapter implements PageDrawAdapter {

	protected XPagedView mTarget;

	private boolean hasSetup = false;

	protected int mPageCount = -1;
	protected Matrix matrix;
	protected RectF localRect;
	protected boolean isLoop = false;

	protected float pageWidth = 0;
	protected float pageHeight = 0;

	protected int cellCountX = 0;
	protected int cellCountY = 0;

	protected int itemCount = 0;

	public XpFlatDrawAdapter(XPagedView target) {

		mTarget = target;

		updateValues();
	}

	public void updateValues() {

		mPageCount = mTarget.getPageCount();
		isLoop = mTarget.isLoop();

		matrix = mTarget.getMatrix();
		localRect = new RectF(mTarget.localRect);

		pageWidth = mTarget.getWidth();
		pageHeight = mTarget.getHeight();

		cellCountX = mTarget.getCellCountX();
		cellCountY = mTarget.getCellCountY();

		itemCount = mTarget.getChildCount();
	}

	protected DrawableItem getCurrentItemByIndex(int index) {
		return mTarget.getChildAt(index);
	}

	@Override
	public void drawPage(IDisplayProcess canvas, int page, float offsetX,
			float offsetY) {

		updateValues();

		if (page > -1 && page < mPageCount) {
			canvas.save();
			if (matrix != null && !matrix.isIdentity()) {
				canvas.concat(matrix);
			}
//			canvas.clipRect(localRect);
			canvas.translate(localRect.left, localRect.top);

			itemSlide(canvas, page, offsetX, true);

			if (offsetX != 0f && mPageCount > 1) {
				int pageOffset = 0;
				if (offsetX < 0) {
					if (page + 1 < mPageCount) {
						pageOffset = 1;
						++page;
					} else {
						if (isLoop) {
							pageOffset = 1;
							page = 0;
						} else {
							canvas.restore();
							return;
						}
					}
				}
				if (offsetX > 0) {
					if (page - 1 > -1) {
						pageOffset = -1;
						--page;
					} else {
						if (isLoop) {
							pageOffset = -1;
							page = mPageCount - 1;
						} else {
							canvas.restore();
							return;
						}
					}
				}

				itemSlide(canvas, page, offsetX + pageOffset, false);
			}

			canvas.restore();
		}

	}

	public abstract void itemSlide(IDisplayProcess canvas, int page,
			float offsetX, boolean currPage);
}
