package com.lenovo.launcher.components.XAllAppFace;

import android.graphics.Matrix;
import android.graphics.RectF;

import com.lenovo.launcher.components.XAllAppFace.AppContentView.PageDrawAdapter;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IDisplayProcess;

public abstract class FlatDrawAdapter implements PageDrawAdapter {
    private int mPageCount = -1;
    private Matrix matrix;
    private RectF localRect;
    private boolean isLoop = false;
    
    FlatDrawAdapter(Matrix m, RectF rect, int count, boolean loop) {
        matrix = m;
        localRect = rect;
        
        setup(count, loop);
    }

    public void setup(int count, boolean loop) {
        mPageCount = count;
        isLoop = loop;
    }

    @Override
    public void drawPage(IDisplayProcess canvas, int page, float offsetX, float offsetY) {

        if (page > -1 && page < mPageCount) {
            canvas.save();
            if (matrix != null && !matrix.isIdentity()) {
                canvas.concat(matrix);
            }
            canvas.translate(localRect.left, localRect.top);
            canvas.clipRect(localRect);

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

    public abstract void itemSlide(IDisplayProcess canvas, int page, float offsetX, boolean currPage);
}
