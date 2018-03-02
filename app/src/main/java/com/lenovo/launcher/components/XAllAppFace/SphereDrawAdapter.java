package com.lenovo.launcher.components.XAllAppFace;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.graphics.RectF;

import com.lenovo.launcher.components.XAllAppFace.XPagedView.PageDrawAdapter;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IDisplayProcess;

/**
 * Sphere Page Animation.
 * 
 */
public class SphereDrawAdapter implements PageDrawAdapter {

	private XPagedView mTarget;
	private boolean isSphereOrCylinder = true;

	private int mCellCountX;
	private int mCellCountY;
	private int mPageCount;
	private Matrix matrix;
	private RectF localRect;

	private int mCellCount;

	private int mCellWidth;
	private int mCellHeight;

	private boolean isLoop;

	/**
	 * 前后页切换alpha临界点
	 */
	private static final float PageAlphaCritical1 = 0.15f;
	/**
	 * 球面变平面后半屏alpha临界点
	 */
	private static final float PageAlphaCritical2 = 0.6f;
	public static final float PI = (float) Math.PI;
	private Camera mCamera = new Camera();

	/**
	 * 经线
	 */
	private int lon_count;
	/**
	 * 纬线
	 */
	private int lat_count;

	float angle_offset_h = 0;
	float angle_offset_v = 0;

	private float center_x = 250f;
	private float center_y = 250f;

	private float ANGLE_LAT_PADDING = 22.5f;
	private float ANGLE_LON_PADDING = 22.5f;
	/**
	 * 两根经线间的夹角
	 */
	float angle_pre_lon;
	/**
	 * 两根纬线间的夹角
	 */
	float angle_pre_lat;

	int countPrePage;
	float radius;
	float pageWidth, pageHeight;

	public SphereDrawAdapter(boolean isSphereOrCylinder, XPagedView target) {
		this.isSphereOrCylinder = isSphereOrCylinder;

		mTarget = target;

		reset();
	}

	public void resetSphereOrCylinder(boolean isSphereOrCylinder) {
		this.isSphereOrCylinder = isSphereOrCylinder;
		reset();
	}

	@Override
	public void reset() {
		mCellCountX = mTarget.getCellCountX();
		mCellCountY = mTarget.getCellCountY();
		mCellCount = mTarget.getChildCount();
		mPageCount = mTarget.getPageCount();
		matrix = mTarget.getMatrix();
		localRect = mTarget.localRect;

		pageWidth = mTarget.getWidth();
		pageHeight = mTarget.getHeight();
		mCellWidth = mTarget.getCellWidth();
		mCellHeight = mTarget.getCellHeight();
		isLoop = mTarget.isLoop();

		countPrePage = mCellCountX * mCellCountY;
		radius = pageWidth < pageHeight ? pageWidth * .618033989f : pageHeight * .618033989f;
		lon_count = mCellCountX << 1;
		lat_count = mCellCountY;
		angle_pre_lon = (float)(360f / lon_count);
		ANGLE_LON_PADDING = angle_pre_lon / 2;
		ANGLE_LAT_PADDING = 45f;
		angle_pre_lat = (180 - ANGLE_LAT_PADDING * 2) / (lat_count - 1);
		center_x = pageWidth * 0.5f;
		center_y = pageHeight * 0.5f;
	}

	@Override
	public void drawPage(IDisplayProcess canvas, int page, float offsetX,
			float offsetY) {

		final float rect2ball = mTarget.getRect2BallRateOrTarget(false);

		if (page > -1 && page < mPageCount) {
			canvas.save();
			if (matrix != null && !matrix.isIdentity()) {
				canvas.concat(matrix);
			}
//			canvas.clipRect(localRect);
			canvas.translate(localRect.left, localRect.top);
			final float targetX = (float)(pageWidth / 2 - mCellWidth / 2);
			final float targetY = (float)(pageHeight / 2 - mCellHeight / 2);
			float d = Math.round(pageWidth * (offsetX + 1));

			angle_offset_h = offsetX * 180;
			angle_offset_v = offsetY * 90;

			float angle_to_z, angle_to_xz;

			int startIndex = page * countPrePage;
			int endIndex = Math.min(startIndex + countPrePage, mCellCount);
			for (int i = startIndex; i < endIndex; i++) {
				XCell item = (XCell) mTarget.getChildAt(i);
				if (item == null)
					continue;
				item.setTouchable(true);
				Matrix m = item.getMatrix();
				m.reset();

				angle_to_z = ANGLE_LON_PADDING + (i % mCellCountX) * angle_pre_lon - 90f
						+ angle_offset_h;
				angle_to_xz = 90 - ANGLE_LAT_PADDING - (i % countPrePage / mCellCountX)
						* angle_pre_lat;
				getMatrix(angle_to_z, angle_to_xz, m, rect2ball);

				if (Math.abs(angle_to_z) > 90 && Math.abs(angle_to_z) < 270) {
					float alpha = Math.abs((180 - Math.abs(angle_to_z)) * .8f / 90);
					if (alpha < .1f) {
						alpha = .1f;
					}
					if (Math.abs(offsetX) < PageAlphaCritical1) {
						alpha *= Math.abs(offsetX) / PageAlphaCritical1;
					}
					item.setAlpha(alpha);
				} else {
					item.setAlpha(1f);
				}

				if (isSphereOrCylinder) {
					m.preTranslate(d + (targetX - item.getRelativeX() - d) * rect2ball,
							(targetY - item.getRelativeY()) * rect2ball);
				} else {
					m.preTranslate(d + (targetX - item.getRelativeX() - d) * rect2ball, 0);
				}
				item.updateMatrix(m);
				item.draw(canvas);
			}

			if (Math.abs(offsetX) > 0.001f) {
				if (offsetX < 0) {
					if (page + 1 < mPageCount) {
						++page;
					} else {
						if (isLoop) {
							page = 0;
						} else {
							canvas.restore();
							return;
						}
					}
				}
				if (offsetX > 0) {
					if (page - 1 > -1) {
						--page;
					} else {
						if (isLoop) {
							page = mPageCount - 1;
						} else {
							canvas.restore();
							return;
						}
					}
				}
				startIndex = page * mCellCountX * mCellCountY;
				endIndex = Math.min(startIndex + mCellCountX * mCellCountY, mCellCount);

				for (int i = startIndex; i < endIndex; i++) {
					XCell item = (XCell) mTarget.getChildAt(i);
					if (item == null)
						continue;
					item.setTouchable(false);
					Matrix m = item.getMatrix();
					m.reset();

					angle_to_z = ANGLE_LON_PADDING + (mCellCountX + i % mCellCountX)
							* angle_pre_lon - 90f + angle_offset_h;
					angle_to_xz = 90 - ANGLE_LAT_PADDING - (i % countPrePage / mCellCountX)
							* angle_pre_lat;
					getMatrix(angle_to_z, angle_to_xz, m, rect2ball);

					if (Math.abs(angle_to_z) > 90 && Math.abs(angle_to_z) < 270) {
						float alpha = Math.abs((180 - Math.abs(angle_to_z)) * .8f / 90);
						if (alpha < .1f) {
							alpha = .1f;
						}
						if (Math.abs(offsetX) < PageAlphaCritical1) {
							alpha *= Math.abs(offsetX) / PageAlphaCritical1;
						}
						if (rect2ball > PageAlphaCritical2) {
							alpha *= (rect2ball - PageAlphaCritical2)
									/ (1 - PageAlphaCritical2);
						} else {
							alpha = 0;
						}
						item.setAlpha(alpha);
					} else {
						float alpha = 1;
						if (rect2ball > PageAlphaCritical2) {
							alpha *= (rect2ball - PageAlphaCritical2)
									/ (1 - PageAlphaCritical2);
						} else {
							alpha = 0;
						}
						item.setAlpha(alpha);
					}

					if (isSphereOrCylinder) {
						m.preTranslate(d + (targetX - item.getRelativeX() - d) * rect2ball,
								(targetY - item.getRelativeY()) * rect2ball);
					} else {
						m.preTranslate(d + (targetX - item.getRelativeX() - d) * rect2ball, 0);
					}
					m.postScale(0.5f * rect2ball + 0.5f, 0.5f * rect2ball + 0.5f, center_x,
							center_y);
					item.updateMatrix(m);
					item.draw(canvas);
				}
			}
			canvas.restore();
		}
	
	}

	public Matrix getMatrix(float angle_to_z, float angle_to_xz, Matrix result,
			float input) {

		if (result == null) {
			result = new Matrix();
		}
		mCamera.save();
		mCamera.translate(0.0f, 0.0f, radius);
		if (isSphereOrCylinder)
			mCamera.rotateX(angle_offset_v * input);
		mCamera.rotateY(angle_to_z * input);
		if (isSphereOrCylinder)
			mCamera.rotateX(angle_to_xz * input);
		mCamera.translate(0.0f, 0.0f, -radius);
		mCamera.getMatrix(result);
		result.preTranslate(-center_x, -center_y);
		result.postTranslate(center_x, center_y);
		mCamera.restore();
		return result;
	}

}