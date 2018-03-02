package com.lenovo.launcher.components.XAllAppFace;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.FloatMath;

import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.NormalDisplayProcess;
import com.lenovo.launcher2.customizer.Debug.R5;
import com.lenovo.launcher2.customizer.LauncherPersonalSettings;

	public class NormalDrawAdapter extends XpFlatDrawAdapter {
		final float MY_PI = 3.141592653589793f;
		final float MIN_SLIDE = 0.001f;
		 //private static final float degreeY = 80.0f;
       private final int z = 580;
		final Camera mCamera = new Camera();
		final Matrix mMatrix = new Matrix();

        private static final float fadeOutX_8 = 0.8f;
        private static final float minStepAlpha = 1f / 255;
        private static final float a_8 = 1f / (fadeOutX_8 - 1);
        private static final float b_8 = -a_8;

        private static final float fadeOutX_6 = 0.5f;
        private static final float a_6 = 1f / (fadeOutX_6 - 1);
        private static final float b_6 = -a_6;

		public NormalDrawAdapter( XPagedView target ) {
			super( target );
		}
		@Override
		public void itemSlide(IDisplayProcess canvas, int page, float offsetX,
				boolean currPage) {
            final String value = mTarget.getSlideValue();
            if( LauncherPersonalSettings.SLIDEEFFECT_NORMAL.equals(value) ){
            	itemNormalSlide(canvas, page, offsetX, currPage);
            }else if (LauncherPersonalSettings.SLIDEEFFECT_CHARIOT.equals(value)) {
                itemChariotSlide(canvas, page, offsetX, currPage);
            } else if (LauncherPersonalSettings.SLIDEEFFECT_WAVE_2.equals(value)) {
                itemWave2Slide(canvas, page, offsetX, currPage);
            } else if (LauncherPersonalSettings.SLIDEEFFECT_WILD.equals(value)) {
                itemWildSlide(canvas, page, offsetX, currPage);
            } else if (LauncherPersonalSettings.SLIDEEFFECT_BULLDOZE.equals(value)) {
                itemBullDozeSlide(canvas, page, offsetX, currPage);
            } else if (LauncherPersonalSettings.SLIDEEFFECT_WAVE.equals(value)) {
                itemWaveSlide(canvas, page, offsetX, currPage);
            } else if (LauncherPersonalSettings.SLIDEEFFECT_ROTATE.equals(value)) {
                itemZRotateSlide(canvas, page, offsetX, currPage);
            } else if (LauncherPersonalSettings.SLIDEEFFECT_BOUNCE.equals(value)) {
                itemBounceSlide(canvas, page, offsetX, currPage);
            } else if (LauncherPersonalSettings.SLIDEEFFECT_SCALE.equals(value)) {
                itemScaleSlide(canvas, page, offsetX, currPage);
            } else if (LauncherPersonalSettings.SLIDEEFFECT_CUBE.equals(value)) {
                itemCubeSlide(canvas, page, offsetX, currPage);
            } else if (LauncherPersonalSettings.SLIDEEFFECT_SNAKE.equals(value)) {
                itemSnakeSlide(canvas, page, offsetX, currPage);
            } else if (LauncherPersonalSettings.SLIDEEFFECT_WORM.equals(value)) {
                 itemWormSlide(canvas, page, offsetX, currPage);
            } else {
                itemNormalSlide(canvas, page, offsetX, currPage);
//                itemBezierSlide(canvas, page, offsetX, currPage);
//                itemCosSlide(canvas, page, offsetX, currPage);
            }
		}

		private void itemChariotSlide(IDisplayProcess canvas, int page,
				float offsetX, boolean currPage) {
			float ratio = Math.abs(offsetX) * 2;
			if (ratio > 1.0f) {
				ratio = 1.0f;
			}
			boolean noNeedEffect = false;
			if (ratio < MIN_SLIDE) {
				noNeedEffect = true;
			}

			float d = Math.round(pageWidth * (offsetX + 1));

			final float R = pageWidth < pageHeight ? pageWidth * (0.37f) : pageHeight * (0.37f);
			final float centerX = -pageWidth / 2;
			final float centerY = pageHeight / 2;

			final int startIndex = page * cellCountX * cellCountY;
			final int endIndex = Math.min(startIndex + cellCountX
					* cellCountY, itemCount);
			if (startIndex == endIndex) {
				return;
			}
			final float sectorDegree = 360f / (endIndex - startIndex);
			final float rotateCycle = 360f * (Math.abs(offsetX) - 0.5f)
					* (Math.signum(offsetX));

			float alpha = getFadeAlpha8(offsetX);
			for (int i = startIndex; i < endIndex; i++) {
				DrawableItem item = getCurrentItemByIndex(i);
				if (item == null)
					continue;
				item.setTouchable(currPage);
				Matrix m = item.getMatrix();
				m.reset();

				if (noNeedEffect) {
					m.setTranslate(d, 0);
				} else {
					float degTarget = (i - startIndex) * sectorDegree;
					float radian = -(90 + degTarget) * ratio;
					m.setRotate(radian, item.localRect.centerX(),
							item.localRect.centerY());

					float angle = degTarget * MY_PI / 180f;
					float itemX = item.localRect.centerX();
					float itemY = item.localRect.centerY();
					float targetX = (float) ((R * FloatMath.cos(angle)
							+ centerX - itemX) * ratio);
					float targetY = (float) ((centerY - R
							* FloatMath.sin(angle) - itemY) * ratio);
					m.postTranslate(targetX, targetY);

					if (Math.abs(offsetX) > 0.5f) {
						m.postRotate(rotateCycle, centerX, centerY);
					}
					m.postTranslate(d, 0);
				}

				if (Math.abs(item.getAlpha() - alpha) >= minStepAlpha) {
                    item.setAlpha(alpha);
                }
				item.updateMatrix(m);
				item.draw(canvas);
			}
		}

		public void itemWave2Slide(IDisplayProcess canvas, int page,
				float offsetX, boolean currPage) {
			final float d = Math.round(pageWidth * (offsetX + 1));

			final int startIndex = page * cellCountX * cellCountY;
			final int endIndex = Math.min(startIndex + cellCountX
					* cellCountY, itemCount);

			final float wave2Padding = 60;
			final float py = pageHeight - wave2Padding;
			float scaleY = 1 - Math.abs(offsetX) / 2;

			float alpha = getFadeAlpha8(offsetX);

			for (int i = startIndex; i < endIndex; i++) {
				DrawableItem item = getCurrentItemByIndex( i );
				if (item == null)
					continue;
				item.setTouchable(currPage);
				// item.setVisibility(true);
				Matrix m = item.getMatrix();
				m.reset();

				m.setScale(1, scaleY, 0, py);
				m.postTranslate(d, 0);

				item.updateMatrix(m);
				if (Math.abs(item.getAlpha() - alpha) >= minStepAlpha) {
                    item.setAlpha(alpha);
                }
				item.draw(canvas);
			}
		}

		public void itemWildSlide(IDisplayProcess canvas, int page,
				float offsetX, boolean currPage) {
		    float px = (-offsetX / 2) * pageWidth;
		    float py = (pageHeight + 20);
		    float degrees = offsetX * 30;
		    float dy = offsetX * pageWidth / 3.8f;

		    Bitmap pageBitmap = mTarget.getSnapBitmap(page);
            if (pageBitmap != null && Math.abs(offsetX) > 0.001) {
                int dd = Math.round(pageWidth * offsetX);
                mMatrix.reset();

                mMatrix.setRotate(degrees, px, py);
                mMatrix.postTranslate(dd, 0);
                mMatrix.postTranslate(0, -dy);
                canvas.drawBitmap(pageBitmap, mMatrix, mTarget.getPaint());

                return;
            } else if (!mTarget.hasChild(page)) {
                return;
            }

			final float d = Math.round(pageWidth * (offsetX + 1));

			final int startIndex = page * cellCountX * cellCountY;
			final int endIndex = Math.min(startIndex + cellCountX
					* cellCountY, itemCount);

			for (int i = startIndex; i < endIndex; i++) {
				DrawableItem item = getCurrentItemByIndex(i);
				if (item == null)
					continue;
				item.setTouchable(currPage);
				Matrix m = item.getMatrix();
				m.reset();

				m.setRotate(degrees, px, py);
				m.postTranslate(d, dy);

				item.updateMatrix(m);
				item.draw(canvas);
			}

		}

		public void itemBullDozeSlide(IDisplayProcess canvas, int page,
				float offsetX, boolean currPage) {
		    float sx = 1 - Math.abs(offsetX);
		    float px_left = sx * pageWidth;
		    float px_right = offsetX * pageWidth;

		    Bitmap pageBitmap = mTarget.getSnapBitmap(page);
            if (pageBitmap != null && Math.abs(offsetX) > 0.001) {
                int dd = Math.round(pageWidth * offsetX);
                mMatrix.reset();

                if (offsetX > 0.0f) {
                    mMatrix.setScale(sx, 1, px_right, 0);
                } else {
                    mMatrix.setScale(sx, 1, px_left, 0);
                }
                mMatrix.preTranslate(dd, 0);
                canvas.drawBitmap(pageBitmap, mMatrix, mTarget.getPaint());

                return;
            } else if (!mTarget.hasChild(page)) {
                return;
            }

			final float d = Math.round(pageWidth * (offsetX + 1));

			final int startIndex = page * cellCountX * cellCountY;
			final int endIndex = Math.min(startIndex + cellCountX
					* cellCountY, itemCount);

			float alpha = getFadeAlpha8(offsetX);

			for (int i = startIndex; i < endIndex; i++) {
				DrawableItem item = getCurrentItemByIndex(i);
				if (item == null)
					continue;
				item.setTouchable(currPage);
				Matrix m = item.getMatrix();
				m.reset();

				if (offsetX > 0.0f) {
					m.setScale(sx, 1, px_right, 0);
				} else {
					m.setScale(sx, 1, px_left, 0);
				}
				m.preTranslate(d, 0);

				item.updateMatrix(m);
				if (Math.abs(item.getAlpha() - alpha) >= minStepAlpha) {
                    item.setAlpha(alpha);
                }
				item.draw(canvas);
			}
		}

		public void itemWaveSlide(IDisplayProcess canvas, int page,
				float offsetX, boolean currPage) {
		    float scale = 1.0f - Math.abs(offsetX) / 2;
		    float px = (0.5f + offsetX) * pageWidth;
		    float py = pageHeight / 2;
            float translate = -offsetX * pageWidth / 4;

            Bitmap pageBitmap = mTarget.getSnapBitmap(page);
            if (pageBitmap != null && Math.abs(offsetX) > 0.001) {
                int dd = Math.round(pageWidth * offsetX);
                mMatrix.reset();

                mMatrix.setScale(scale, scale, px, py);
                mMatrix.postTranslate(translate, 0);
                mMatrix.preTranslate(dd, 0);

                canvas.drawBitmap(pageBitmap, mMatrix, mTarget.getPaint());

                return;
            } else if (!mTarget.hasChild(page)) {
                return;
            }

			final float d = Math.round(pageWidth * (offsetX + 1));

			final int startIndex = page * cellCountX * cellCountY;
			final int endIndex = Math.min(startIndex + cellCountX
					* cellCountY, itemCount);

			float alpha = getFadeAlpha8(offsetX);

			for (int i = startIndex; i < endIndex; i++) {
				DrawableItem item = getCurrentItemByIndex(i);
				if (item == null)
					continue;
				item.setTouchable(currPage);
				Matrix m = item.getMatrix();
				m.reset();

				m.setScale(scale, scale, px, py);
				m.postTranslate(translate, 0);
				m.preTranslate(d, 0);

				item.updateMatrix(m);
				if (Math.abs(item.getAlpha() - alpha) >= minStepAlpha) {
                    item.setAlpha(alpha);
                }
				item.draw(canvas);
			}
		}

		public void itemZRotateSlide(IDisplayProcess canvas, int page,
				float offsetX, boolean currPage) {
		    final Camera camera = mCamera;
		    final float centerX = pageWidth / 2.0f + offsetX * pageWidth;
		    final float centerY = pageHeight / 2.0f;
		    float degrees = 30 * (-offsetX);
		    float translate = -offsetX * pageWidth / 8f;

            Bitmap pageBitmap = mTarget.getSnapBitmap(page);
            if (pageBitmap != null && Math.abs(offsetX) > 0.001) {
                int dd = Math.round(pageWidth * offsetX);
                mMatrix.reset();

                camera.save();
                camera.rotateY(degrees);
                camera.getMatrix(mMatrix);
                camera.restore();
                mMatrix.preTranslate(-centerX + dd, -centerY);
                mMatrix.postTranslate(centerX + translate, centerY);

                canvas.drawBitmap(pageBitmap, mMatrix, mTarget.getPaint());

                return;
            } else if (!mTarget.hasChild(page)) {
                return;
            }

			final float d = Math.round(pageWidth * (offsetX + 1));

			final int startIndex = page * cellCountX * cellCountY;
			final int endIndex = Math.min(startIndex + cellCountX
					* cellCountY, itemCount);


			for (int i = startIndex; i < endIndex; i++) {
				DrawableItem item = getCurrentItemByIndex(i);
				if (item == null)
					continue;
				item.setTouchable(currPage);
				// item.setVisibility(true);
				Matrix m = item.getMatrix();
				m.reset();

				camera.save();
				camera.rotateY(degrees);
				camera.getMatrix(m);
				camera.restore();
				m.preTranslate(-centerX + d, -centerY);
				m.postTranslate(centerX + translate, centerY);

				item.updateMatrix(m);
				item.draw(canvas);
			}
		}

		public void itemBounceSlide(IDisplayProcess canvas, int page,
				float offsetX, boolean currPage) {
			final float d = Math.round(pageWidth * (offsetX + 1));

			final int startIndex = page * cellCountX * cellCountY;
			final int endIndex = Math.min(startIndex + cellCountX
					* cellCountY, itemCount);

			float translate = pageHeight * offsetX;
			if (offsetX > 0.0f) {
				translate *= -1;
			}

			float alpha = getFadeAlpha8(offsetX);

			for (int i = startIndex; i < endIndex; i++) {
				DrawableItem item = getCurrentItemByIndex(i);
				if (item == null)
					continue;
				item.setTouchable(currPage);
				Matrix m = item.getMatrix();
				m.reset();

				m.setTranslate(0, translate);
				m.preTranslate(d, 0);

				item.updateMatrix(m);
				if (Math.abs(item.getAlpha() - alpha) >= minStepAlpha) {
                    item.setAlpha(alpha);
                }
				item.draw(canvas);
			}
		}

		public void itemScaleSlide(IDisplayProcess canvas, int page,
				float offsetX, boolean currPage) {
			final float d = Math.round(pageWidth * (offsetX + 1));

			final int startIndex = page * cellCountX * cellCountY;
			final int endIndex = Math.min(startIndex + cellCountX
					* cellCountY, itemCount);

			float ratio = 1 - Math.abs(offsetX);
			float px;
			float translate = d;
			if (offsetX > 0.0f) {
				px = pageWidth;
				translate = d - offsetX * pageWidth;
			} else {
				px = 0.0f;
			}

			float alpha = getFadeAlpha8(offsetX);

			for (int i = startIndex; i < endIndex; i++) {
				DrawableItem item = getCurrentItemByIndex(i);
				if (item == null)
					continue;
				item.setTouchable(currPage);
				Matrix m = item.getMatrix();
				m.reset();

				m.setScale(ratio, ratio, px, 0);
				m.preTranslate(translate, 0);

				item.updateMatrix(m);
				if (Math.abs(item.getAlpha() - alpha) >= minStepAlpha) {
                    item.setAlpha(alpha);
                }
				item.draw(canvas);
			}
		}

		public void itemCubeSlide(IDisplayProcess canvas, int page,
				float offsetX, boolean currPage) {
            final float radius = pageHeight / 3f;
            final Camera camera = mCamera;

            float degree = 0f;// degreeY * offsetX;

            if (offsetX > 0) {
                float dInside = (1 - offsetX) * pageWidth;
                float beta = (float) (Math.atan(z / dInside));
                float alpha = (float) (Math.asin(Math.sin(beta) * (1 - offsetX)));
                degree = (float) ((beta - alpha) * 180 / Math.PI);
            } else if (offsetX < 0) {
                float dInside = (1 + offsetX) * pageWidth;
                float beta = (float) (Math.atan(z / dInside));
                float alpha = (float) (Math.asin(Math.sin(beta) * (1 + offsetX)));
                degree = (float) ((alpha - beta) * 180 / Math.PI);
            }

            Bitmap pageBitmap = mTarget.getSnapBitmap(page);
            if (pageBitmap != null && Math.abs(offsetX) > 0.001) {
                int dd = Math.round(pageWidth * offsetX);
                mMatrix.reset();

                camera.save();
                camera.rotateY(degree);
                camera.getMatrix(mMatrix);
                camera.restore();
                if (degree > 0) {
                    mMatrix.preTranslate(0, -radius);
                    mMatrix.postTranslate(dd, radius);
                } else {
                    mMatrix.preTranslate(-pageWidth, -radius);
                    mMatrix.postTranslate(dd + pageWidth, radius);
                }

                canvas.drawBitmap(pageBitmap, mMatrix, mTarget.getPaint());

                return;
            } else if (!mTarget.hasChild(page)) {
                return;
            }

			int startIndex = page * cellCountX * cellCountY;
			int endIndex = Math.min(startIndex + cellCountX * cellCountY,
					itemCount);

			float d = Math.round(pageWidth * offsetX);
			float alpha = getFadeAlpha8(offsetX);

			for (int i = startIndex; i < endIndex; i++) {
				DrawableItem item = getCurrentItemByIndex(i);
				if (item == null)
					continue;
				item.setTouchable(currPage);
				Matrix m = item.getMatrix();
				m.reset();

				camera.save();
				camera.rotateY(degree);
				camera.getMatrix(m);
				camera.restore();
				if (degree > 0) {
					m.preTranslate(pageWidth, -radius);
					m.postTranslate(d, radius);
				} else {
					m.preTranslate(0, -radius);
					m.postTranslate(d + pageWidth, radius);
				}

				item.updateMatrix(m);
				if (Math.abs(item.getAlpha() - alpha) >= minStepAlpha) {
                    item.setAlpha(alpha);
                }
				item.draw(canvas);
			}
		}
				
		private boolean needDraw(int offsetWidth, DrawableItem item){
            if (!(mTarget.getParent() instanceof XWorkspace)) {
                return true;
            }

			if (item instanceof XCell)
			{
				XCell cell = (XCell)item;
//				if (cell.getDrawingTarget() == null)
//				{
//					return false;
//				}
				
				if (cell.getDrawingTarget() instanceof XViewContainer)
				{
					return true;
				}
			}
			
			if (offsetWidth == 0)
			{
			    /*** RK_ID: LELAUNCHER-953. AUT: zhaoxy12. DATE: 2013-12-5 . START***/
//				return true;
			    /*** RK_ID: LELAUNCHER-953. AUT: zhaoxy12. DATE: 2013-12-5 . END***/
			}
			else
			{
				int itemWidth = (int)item.getWidth();
				int left = (int)item.getRelativeX() + offsetWidth;
				int right = (int)left + itemWidth;
				
				if ((left > 0 && left < pageWidth) || (right > 0 && right < pageWidth))
				{
					return true;
				}
			}
                        
            return false;
        }
		
		public void itemNormalSlide(IDisplayProcess canvas, int page,
				float offsetX, boolean currPage) {
			Bitmap pageBitmap  = mTarget.getSnapBitmap(page);			
			if (pageBitmap != null && Math.abs(offsetX) > 0.001)
			{
				int dd = Math.round(pageWidth * offsetX);
				Rect rect = new Rect(dd, 0, dd + (int)pageWidth, (int)mTarget.getHeight());
				canvas.drawBitmap(pageBitmap, null, rect, mTarget.getPaint());
				return;
			}
			else if (!mTarget.hasChild(page))
			{
				return;
			}
						
			int startIndex = page * cellCountX * cellCountY;
			int endIndex = Math.min(startIndex + cellCountX * cellCountY,
					itemCount);

			int d = Math.round(pageWidth * (offsetX + 1));
//			R5.echo("d = " + d);

			float alpha = getFadeAlpha8(offsetX);
			for (int i = startIndex; i < endIndex; i++) {
				DrawableItem item = getCurrentItemByIndex(i);
				if (item == null)
					continue;
				item.setTouchable(currPage);
                if (!needDraw(d, item))
                {
                    continue;
                }
				Matrix m = item.getMatrix();
				m.reset();
				m.setTranslate(d, 0);

				item.updateMatrix(m);
                if (mTarget.getParent() instanceof XFolder) {
                    if (Math.abs(item.getAlpha() - alpha) >= minStepAlpha) {
                        item.setAlpha(alpha);
                    }
                }
				item.draw(canvas);
			}
		}

    public void itemBezierSlide(IDisplayProcess canvas, int page, float offsetX, boolean currPage) {
        int startIndex = page * cellCountX * cellCountY;
        int endIndex = Math.min(startIndex + cellCountX * cellCountY, itemCount);

        float d = Math.round(pageWidth * (offsetX + 1));
        final int gap = 0;
        final int y1 = (int) (pageHeight / 2 - gap);

        float b = FloatMath.sin(Math.abs(offsetX) * MY_PI);
        final float scaleStep = 1f / 4f / (cellCountX - 1);

        for (int i = startIndex; i < endIndex; i++) {
            DrawableItem item = getCurrentItemByIndex(i);
            if (item == null)
                continue;

            float scaleGap;
            if (Math.signum(offsetX) == -1) {
                scaleGap = scaleStep * (i % cellCountX);
            } else {
                scaleGap = scaleStep * (cellCountX - 1 - (i % cellCountX));
            }
            float scale = 1 - scaleGap * b;

            int x = (int) (item.getRelativeX() + item.getWidth() / 2 + pageWidth);
            float u = x / pageWidth / 2f;
            if (Math.signum(offsetX) == 1) {
                u += 0.5f;
            }

            int y0 = (int) (item.getRelativeY() * 2 + item.getHeight() - y1);
            int y2 = y0;

            int z1 = (y0 - 2 * y1 + y2) / 2;
            int z2 = -y0 + y1;
            int z3 = (y0 + y1) / 2;

            int yTarget = (int) (u * u * z1 + u * z2 + z3);
            float dy = (yTarget - item.getRelativeY()) * b;

            item.setTouchable(currPage);
            Matrix m = item.getMatrix();
            m.reset();

            m.setScale(scale, scale, item.localRect.centerX(), item.localRect.centerY());
            m.postTranslate(0, dy);
            m.postTranslate(d, 0);

            item.updateMatrix(m);
            item.draw(canvas);
        }
    }

    private void itemSnakeSlide(IDisplayProcess canvas, int page, float offsetX, boolean currPage) {
        final float d = Math.round(pageWidth * (offsetX + 1));

        final int startIndex = page * cellCountX * cellCountY;
        final int endIndex = Math.min(startIndex + cellCountX * cellCountY, itemCount);

        final int originalCellWidth = mTarget.getCellWidth();
        final int widthGap = 0;//mTarget.getWidthGap();
        final int originalCellHeight = mTarget.getCellHeight();
        final int heightGap = mTarget.getHeightGap();
        final int currentPage = mTarget.getCurrentPage();
        final int vPadding = mTarget.getVPadding();

        // 计算水平滑行距离
        final int itemWidth = originalCellWidth + widthGap;
        final int hIntrinsicSpace = itemWidth * (cellCountX - 1);

        // 垂直距离
        final int vIntrinsicSpace = originalCellHeight + heightGap;

        // 每个图标的行驶总距离
        final int totalSpace = (hIntrinsicSpace + vIntrinsicSpace) * cellCountY;

        int vSpace = (int) (localRect.height() - vPadding);
        int vFreeSpace = vSpace - (cellCountY * originalCellHeight);
        int intrinsicHGap = cellCountY > 1 ? vFreeSpace / (cellCountY - 1) : 0;

        float heightGapAdjust;
        if (intrinsicHGap > heightGap) {
            heightGapAdjust = (intrinsicHGap - heightGap) * (cellCountY - 1);
        } else {
            heightGapAdjust = Math.min(intrinsicHGap, 0);
        }
        // 因为mHeightGap的原因，可能会产生页码之间的页码高度差和vIntrinsicSpace不相等
        // 计算页码高度差
        int pageDiffer = (int) (originalCellHeight + vPadding + heightGapAdjust);

        // 因为在item从自己页游到另一页的时候，可能会造成残留阴影，做出的调整
        // 不属于算法之内的数据，debug时应该设置为0
        int padding = mTarget.getXContext().getResources()
                .getDimensionPixelSize(com.lenovo.launcher.R.dimen.snake_animation_padding);
        final int yDirtyAdjust = Math.abs(intrinsicHGap) + padding;

        float alpha = getFadeAlpha8(offsetX);

        for (int i = endIndex - 1; i >= startIndex; i--) {
            DrawableItem item = getCurrentItemByIndex(i);
            if (item == null) {
                continue;
            }
            item.setTouchable(currPage);
            Matrix m = item.getMatrix();
            m.reset();

            int index = i - startIndex;
            int cellX = index % cellCountX;
            int cellY = index / cellCountX;

            if (offsetX < 0.0f && page == currentPage) {
                final float currentSpace = totalSpace * Math.abs(offsetX);
                rightDirectLeftCurrent(m, cellX, cellY, currentSpace, hIntrinsicSpace,
                        vIntrinsicSpace, yDirtyAdjust, itemWidth);
                m.preTranslate(pageWidth, 0);
            } else if (offsetX > 0.0f && page != currentPage) {
                final float currentSpace = totalSpace * Math.abs(1.0f - offsetX);
                rightDirectLeftNotCurrent(m, cellX, cellY, currentSpace, hIntrinsicSpace,
                        vIntrinsicSpace, yDirtyAdjust, itemWidth, pageDiffer);
                m.preTranslate(pageWidth, -pageHeight);
            } else if (offsetX > 0.0f && page == currentPage) {
                final float currentSpace = totalSpace * Math.abs(offsetX);
                leftDirectRightCurrent(m, cellX, cellY, currentSpace, hIntrinsicSpace,
                        vIntrinsicSpace, yDirtyAdjust, itemWidth);
                m.preTranslate(pageWidth, 0);
            } else if (offsetX < 0.0f && page != currentPage) {
                final float currentSpace = totalSpace * Math.abs(1.0f + offsetX);
                leftDirectRightNotCurrent(m, cellX, cellY, currentSpace, hIntrinsicSpace,
                        vIntrinsicSpace, yDirtyAdjust, itemWidth, pageDiffer);
                m.preTranslate(pageWidth, pageHeight);
            } else {
                m.preTranslate(d, 0);
            }

            item.updateMatrix(m);
            if (Math.abs(item.getAlpha() - alpha) >= minStepAlpha) {
                item.setAlpha(alpha);
            }
            item.draw(canvas);
        }

    }

		// 从左向右滑动，非当前页
		private void leftDirectRightNotCurrent(Matrix m, int cellX, int cellY,
				float currentSpace, int hIntrinsicSpace, int vIntrinsicSpace,
				int yDirtyAdjust, int itemWidth, int pageDiffer) {
			int xTotalTranslate = 0;
			int yTotalTranslate = 0;

			// 分别读取最左端和最右端的边缘差值
			int leftEdge = (cellX) * itemWidth;
			int rightEdge = (cellCountX - 1 - cellX) * itemWidth;

			// 如果mCellCountY - cellY是奇数，移动方向则和offSetX一致，
			// 那么在向左移动过程中，初始水平距离是向cellX = 0做减法
			int startHSpace;
			if (cellY > 0 && cellY % 2 == 1) {
				startHSpace = leftEdge;
			} else {
				startHSpace = rightEdge;
			}

			if (currentSpace > startHSpace) {
				int leftSpace = (int) (currentSpace - startHSpace);

				int yTranslate = leftSpace
						/ (hIntrinsicSpace + vIntrinsicSpace);
				yTotalTranslate += (-yTranslate + 1) * vIntrinsicSpace
						- pageDiffer;

				int currentCellY = -yTranslate + cellY;

				int xTranslate = leftSpace
						% (hIntrinsicSpace + vIntrinsicSpace);
				if (xTranslate > vIntrinsicSpace) {
					// 首先去除垂直距离
					xTranslate -= vIntrinsicSpace;
					currentCellY -= 1;
					// 计算当前水平滑行方向
					// 和之前一样"偶数向右，奇数向左"
					if ((cellCountY + currentCellY) > 0 && (cellCountY + currentCellY) % 2 == 1) {
						xTotalTranslate += rightEdge - xTranslate;
					} else {
						xTotalTranslate += xTranslate - leftEdge;
					}

					yTotalTranslate += -vIntrinsicSpace;
					// 底端有残留
					if (currentCellY > -1) {
						yTotalTranslate += yDirtyAdjust;
					}
				} else {
					// 当前已经到了最上层，不再向上移动，而是水平移动
					if (currentCellY == 0) {
						if (cellCountY % 2 == 0) {
							// 在最右
							xTotalTranslate += vIntrinsicSpace - xTranslate
									+ rightEdge;
						} else {
							// 最左
							xTotalTranslate += xTranslate - vIntrinsicSpace
									- leftEdge;
						}
						yTotalTranslate += -vIntrinsicSpace;
					} else {
						// 底端有残留
						if (currentCellY > 0) {
							yTotalTranslate += yDirtyAdjust;
						}
						// 直接垂直向上移动
						yTotalTranslate += -xTranslate;
						// 偶数在最右，奇数在最左
						if ((cellCountY + currentCellY) % 2 == 1) {
							xTranslate = -leftEdge;
						} else {
							xTranslate = rightEdge;
						}
						xTotalTranslate += xTranslate;
					}
				}
			} else {
				if (cellY % 2 == 1) {
					xTotalTranslate += -currentSpace;
				} else {
					xTotalTranslate += currentSpace;
				}
                // 底端有残留
                if (cellY == 0) {
                    yTotalTranslate += yDirtyAdjust;
                }
			}

			m.setTranslate(xTotalTranslate, yTotalTranslate);
		}

		// 从左向右滑动，当前页
		private void leftDirectRightCurrent(Matrix m, int cellX, int cellY,
				float currentSpace, int hIntrinsicSpace, int vIntrinsicSpace,
				int yDirtyAdjust, int itemWidth) {
			int xTotalTranslate = 0;
			int yTotalTranslate = 0;

			// 分别读取最左端和最右端的边缘差值
			int leftEdge = (cellX) * itemWidth;
			int rightEdge = (cellCountX - 1 - cellX) * itemWidth;

			// 如果cellY是偶数，移动方向则和offSetX一致，
			// 那么在向右移动过程中，初始水平距离是向cellX = mCellCountX - 1做减法
			int startHSpace;
			if (cellY > 0 && cellY % 2 == 1) {
				startHSpace = leftEdge;
			} else {
				startHSpace = rightEdge;
			}

			if (currentSpace > startHSpace) {
				int leftSpace = (int) (currentSpace - startHSpace);

				int yTranslate = leftSpace
						/ (hIntrinsicSpace + vIntrinsicSpace);
				yTotalTranslate += -yTranslate * vIntrinsicSpace;

				int currentCellY = -yTranslate + cellY;

				int xTranslate = leftSpace
						% (hIntrinsicSpace + vIntrinsicSpace);
				if (xTranslate > vIntrinsicSpace) {
					// 首先去除垂直距离
					xTranslate -= vIntrinsicSpace;
					currentCellY -= 1;
					// 计算当前水平滑行方向
					// 和之前一样"偶数向右，奇数向左"
					if (currentCellY > 0 && currentCellY % 2 == 1) {
						xTotalTranslate += rightEdge - xTranslate;
					} else {
						xTotalTranslate += xTranslate - leftEdge;
					}

					yTotalTranslate += -vIntrinsicSpace;
					// 上峰有残留
					if (currentCellY <= -1) {
						yTotalTranslate += -yDirtyAdjust;
					}
				} else {
					// 当前已经到了最上层，不再向上移动，而是向右移动
					if (currentCellY == 0) {
						xTotalTranslate += xTranslate + rightEdge;
					} else {
						// 上峰有残留
						if (currentCellY <= 0) {
							yTotalTranslate += -yDirtyAdjust;
						}
						// 直接垂直向上移动
						yTotalTranslate += -xTranslate;
						// 偶数在最右，奇数在最左
						if (currentCellY % 2 == 1) {
							xTranslate = -leftEdge;
						} else {
							xTranslate = rightEdge;
						}
						xTotalTranslate += xTranslate;
					}
				}
			} else {
				if (cellY % 2 == 1) {
					xTotalTranslate += -currentSpace;
				} else {
					xTotalTranslate += currentSpace;
				}
			}

			m.setTranslate(xTotalTranslate, yTotalTranslate);
		}

		// 从右向左滑动，非当前页
		private void rightDirectLeftNotCurrent(Matrix m, int cellX, int cellY,
				float currentSpace, int hIntrinsicSpace, int vIntrinsicSpace,
				int yDirtyAdjust, int itemWidth, int pageDiffer) {
			int xTotalTranslate = 0;
			int yTotalTranslate = 0;

			// 分别读取最左端和最右端的边缘差值
			int leftEdge = (cellX) * itemWidth;
			int rightEdge = (cellCountX - 1 - cellX) * itemWidth;

			// 如果mCellCountY - cellY是奇数，移动方向则和offSetX一致，
			// 那么在offsetX > 0过程中，初始水平距离是向cellX = mCellCountX - 1做减法
			int startHSpace;
			if (cellY % 2 == 0) {
				startHSpace = leftEdge;
			} else {
				startHSpace = rightEdge;
			}

			if (currentSpace > startHSpace) {
				int leftSpace = (int) (currentSpace - startHSpace);

				int yTranslate = leftSpace
						/ (hIntrinsicSpace + vIntrinsicSpace);
				yTotalTranslate += (yTranslate - 1) * vIntrinsicSpace
						+ pageDiffer;

				int currentCellY = yTranslate + cellY;
				// 因为这里会有已经游走的图标的残留，在屏幕顶部
				if (currentCellY < (cellCountY - 1)) {
					yTotalTranslate += -yDirtyAdjust;
				}

				int xTranslate = leftSpace
						% (hIntrinsicSpace + vIntrinsicSpace);
				if (xTranslate > vIntrinsicSpace) {
					// 首先去除垂直距离
					xTranslate -= vIntrinsicSpace;
					currentCellY += 1;
					// 计算当前水平滑行方向
					// 和之前一样"偶数向左，奇数向右"
					if ((cellCountY - currentCellY) % 2 == 0) {
						xTotalTranslate += rightEdge - xTranslate;
					} else {
						xTotalTranslate += xTranslate - leftEdge;
					}

					yTotalTranslate += vIntrinsicSpace;
				} else {
					// 当前已经到了最底层，不再向下移动，而是水平移动
					if (currentCellY == (cellCountY - 1)) {
						// 移动到最右端
						xTotalTranslate += vIntrinsicSpace - xTranslate
								+ rightEdge;
						yTotalTranslate += vIntrinsicSpace;
					} else {
						// 直接垂直向下移动
						yTotalTranslate += xTranslate;
						// 偶数在最左，奇数在最右
						if ((cellCountY - currentCellY) % 2 == 0) {
							xTranslate = -leftEdge;
						} else {
							xTranslate = rightEdge;
						}
						xTotalTranslate += xTranslate;
					}
				}
			} else {
				if (cellY % 2 == 0) {
					xTotalTranslate += -currentSpace;
				} else {
					xTotalTranslate += currentSpace;
				}
                // 上峰有残留 fix bug 18458
                if (cellY == (cellCountY - 1)) {
                    yTotalTranslate += -yDirtyAdjust;
                }
			}

			m.setTranslate(xTotalTranslate, yTotalTranslate);
		}

		// 从右向左滑动，当前页
		private void rightDirectLeftCurrent(Matrix m, int cellX, int cellY,
				float currentSpace, int hIntrinsicSpace, int vIntrinsicSpace,
				int yDirtyAdjust, int itemWidth) {
			int xTotalTranslate = 0;
			int yTotalTranslate = 0;

			// 分别读取最左端和最右端的边缘差值
			int leftEdge = (cellX) * itemWidth;
			int rightEdge = (cellCountX - 1 - cellX) * itemWidth;

			// 如果cellY是偶数，移动方向则和offSetX一致，
			// 那么在向左移动过程中，初始水平距离是向cellX = 0做减法
			int startHSpace;
			if (cellY % 2 == 0) {
				startHSpace = leftEdge;
			} else {
				startHSpace = rightEdge;
			}

			if (currentSpace > startHSpace) {
				int leftSpace = (int) (currentSpace - startHSpace);

				int yTranslate = leftSpace
						/ (hIntrinsicSpace + vIntrinsicSpace);
				yTotalTranslate += yTranslate * vIntrinsicSpace;

				int currentCellY = yTranslate + cellY;
				// 因为这里会有已经游走的图标的残留，在屏幕底部
				if (currentCellY >= cellCountY) {
					yTotalTranslate += yDirtyAdjust;
				}

				int xTranslate = leftSpace
						% (hIntrinsicSpace + vIntrinsicSpace);
				if (xTranslate > vIntrinsicSpace) {
					// 首先去除垂直距离
					xTranslate -= vIntrinsicSpace;
					currentCellY += 1;
					// 计算当前水平滑行方向
					// 和之前一样"偶数向左，奇数向右"
					if (currentCellY % 2 == 0) {
						xTotalTranslate += rightEdge - xTranslate;
					} else {
						xTotalTranslate += xTranslate - leftEdge;
					}

					yTotalTranslate += vIntrinsicSpace;
					// 因为这里会有已经游走的图标的残留，在屏幕底部
					// 所以多向下移动一部分
					if (currentCellY == cellCountY) {
						yTotalTranslate += yDirtyAdjust;
					}
				} else {
					// 当前已经到了最底层，不再向下移动，而是水平移动
					if (currentCellY == (cellCountY - 1)) {
						if (cellCountY % 2 == 0) {
							// 总行数偶数个，水平向右，和起始行方向相反
							xTotalTranslate += xTranslate + rightEdge;
						} else {
							// 奇数向左
							xTotalTranslate += -xTranslate - leftEdge;
						}
					} else {
						// 直接垂直向下移动
						yTotalTranslate += xTranslate;
						// 偶数在最左，奇数在最右
						if (currentCellY % 2 == 0) {
							xTranslate = -leftEdge;
						} else {
							xTranslate = rightEdge;
						}
						xTotalTranslate += xTranslate;
					}
				}
			} else {
				if (cellY % 2 == 0) {
					xTotalTranslate += -currentSpace;
				} else {
					xTotalTranslate += currentSpace;
				}
			}

			m.setTranslate(xTotalTranslate, yTotalTranslate);
		}

        public void itemWormSlide(IDisplayProcess canvas, int page, float offsetX, boolean currPage) {
            final float pageWidth = mTarget.getWidth();
            int startIndex = page * cellCountX * cellCountY;
            int endIndex = Math.min(startIndex + cellCountX * cellCountY, itemCount);

            final float finalOffset = currPage ? offsetX : (offsetX > 0.0f ? offsetX - 1 : offsetX + 1);
            float dx, ts, input, d;

            float alpha = getFadeAlpha6(offsetX);

            for (int i = startIndex; i < endIndex; i++) {
                DrawableItem item = getCurrentItemByIndex(i);
                if (item == null) continue;
                item.setTouchable(currPage);
                Matrix m = item.getMatrix();
                m.reset();
                if (finalOffset > 0) {
                    dx = currPage ? -item.localRect.right : -item.localRect.right + pageWidth;
                    ts = dx / pageWidth * .3f;
                    if (finalOffset > ts) {
                        input = (finalOffset - ts) / (1 - ts);
                        input = .5f + .5f * FloatMath.sin(-MY_PI / 2 + MY_PI * input);
                    } else {
                        input = 0f;
                    }
                    d = Math.round(pageWidth * (input + (currPage ? 1 : 0)));
                } else {
                    dx = currPage ? item.localRect.left + pageWidth : item.localRect.left + pageWidth + pageWidth;
                    ts = dx / pageWidth * .3f;
                    if (finalOffset < -ts) {
                        input = (finalOffset + ts) / (ts - 1);
                        input = -.5f - .5f * FloatMath.sin(-MY_PI / 2 + MY_PI * input);
                    } else {
                        input = 0f;
                    }
                    d = Math.round(pageWidth * (input + (currPage ? 1 : 2)));
                }
                m.setTranslate(d, 0);

                item.updateMatrix(m);
                if (Math.abs(item.getAlpha() - alpha) >= minStepAlpha) {
                    item.setAlpha(alpha);
                }
                item.draw(canvas);
            }
        }

        public void itemCosSlide(IDisplayProcess canvas, int page, float offsetX, boolean currPage) {
            int startIndex = page * cellCountX * cellCountY;
            int endIndex = Math.min(startIndex + cellCountX * cellCountY, itemCount);

            float d = Math.round(pageWidth * (offsetX + 1));
            float spx = offsetX < 0 ? 0 : -pageWidth;
            float spy = pageHeight * .5f;

            float alpha = getFadeAlpha8(offsetX);
            float scale = .75f + .25f * FloatMath.cos(offsetX * 2 * MY_PI);

            for (int i = startIndex; i < endIndex; i++) {
                DrawableItem item = getCurrentItemByIndex(i);
                if (item == null)
                    continue;
                item.setTouchable(currPage);
                Matrix m = item.getMatrix();
                m.reset();
                m.setScale(scale, scale, spx, spy);
                m.postTranslate(d, 0);

                item.updateMatrix(m);
                if (Math.abs(item.getAlpha() - alpha) >= minStepAlpha) {
                    item.setAlpha(alpha);
                }
                item.draw(canvas);
            }
        }

        private float getFadeAlpha6(float input) {
            if (Math.abs(input) > fadeOutX_6) {
                return a_6 * Math.abs(input) + b_6;
            }
            return 1f;
        }

        private float getFadeAlpha8(float input) {
            if (Math.abs(input) > fadeOutX_8) {
                return a_8 * Math.abs(input) + b_8;
            }
            return 1f;
        }

		@Override
		public void reset() {

		}

	}
