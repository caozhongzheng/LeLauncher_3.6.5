package com.lenovo.launcher.components.XAllAppFace;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;

public class XScreenPagedView extends XPagedView {

    private static final String TAG = "XScreenPagedView";

//    private XLauncher mLauncher;
    private boolean isStepMode = true;

    private int mCurrentFocusCell = -1;
    private float mCurrentOffsetX;

    private float firstStep = 0.33f;
    private float secondStep = 0.66f;
    private static final float devi = 0.003f;

    private int PAGE_CELL_COUNT;
    private int maxFocusCell;
    private int minFocusCell;

    private boolean mFling = false;
    private boolean mDown = false;
    
    /*RK_ID: RK_SCREEN_EDIT . AUT: zhanggx1 . DATE: 2013-05-09 . S*/
    private OnFocusCellChangedListener onFocusCellChangedListener = null;
    protected ConcurrentLinkedQueue<PageSwitchListener> itemSwitchers;
    private boolean mItemIndicatorEnable = true;
    private static final long DURATION_OFFSET_X = 300L;
    /*RK_ID: RK_SCREEN_EDIT . AUT: zhanggx1 . DATE: 2013-05-09 . S*/

    public XScreenPagedView(XContext context, RectF pageRect, int maxFocus) {
        super(context, pageRect);

        mAnimController = new ScreenPageAnimController();
        registerIController(mAnimController);

//        mLauncher = (XLauncher) context.getContext();
        updateScrollLimit(maxFocus);
    }

    @Override
    public void setup(int screen, int cellCountX, int cellCountY) {
        super.setup(screen, cellCountX, cellCountY);

        firstStep = mCellWidth / this.localRect.width();
        secondStep = 2 * firstStep;
        PAGE_CELL_COUNT = cellCountX * cellCountY;
    }

    @Override
    public void setCurrentPage(int currentPage) {
        super.setCurrentPage(currentPage);

        Log.i(TAG, "setCurrentPage   currentPage==== " + currentPage);
        mCurrentOffsetX = 0.0f;
        getFocusCellByOffset();
    }

    @Override
    public boolean addPagedViewItem(XPagedViewItem itemToAdd) {
        boolean res = super.addPagedViewItem(itemToAdd);

//        updateScrollLimit();
        if (isStepMode) {
        	updateIndicator();
        }
        return res;
    }
    
    @Override
    public boolean removePagedViewItem(XPagedViewItem itemToRemove, boolean audoAdjust, boolean animate) {
    	boolean res = super.removePagedViewItem(itemToRemove, audoAdjust, animate);
    	if (isStepMode) {
        	updateIndicator();
        }
    	return res;
    }

    @Override
    public void draw(IDisplayProcess canvas) {
        if (!isStepMode) {
            super.draw(canvas);
        } else {

            if (!isVisible()) {
                return;
            }

            updateFinalAlpha();

            int page = mCurrentPage;
            Matrix matrix = getMatrix();
            float offsetX = mOffsetX;

            drawPage(canvas, page, matrix, offsetX);
        }

    }

    private void drawPage(IDisplayProcess canvas, int page, Matrix matrix, float offsetX) {
        if (page > -1 && page < mPageCount) {
            canvas.save();
            if (matrix != null && !matrix.isIdentity()) {
                canvas.concat(matrix);
            }
//          canvas.clipRect(localRect);
            canvas.translate(localRect.left, localRect.top);

            itemSlide(canvas, page, offsetX, true);

            if (offsetX != 0f && mPageCount > 1) {
                int pageOffset = 0;
                if (offsetX < 0) {
                    if (page + 1 < mPageCount) {
                        pageOffset = 1;
                        ++page;
                    } else {
                        if (isLoop()) {
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
                        if (isLoop()) {
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

    private void itemSlide(IDisplayProcess canvas, int page, float offsetX, boolean currPage) {
        int cellCountX = getCellCountX();
        int cellCountY = getCellCountY();

        int startIndex = page * cellCountX * cellCountY;
        int endIndex = Math.min(startIndex + cellCountX * cellCountY, getChildCount());

        float d = Math.round(getWidth() * (offsetX + 1));

        for (int i = startIndex; i < endIndex; i++) {
            DrawableItem item = getChildAt(i);
            if (item == null)
                continue;
            item.setTouchable(true);//currPage);
            Matrix m = item.getMatrix();
            m.reset();
            m.setTranslate(d, 0);
            item.updateMatrix(m);
            item.draw(canvas);
        }

    }

    void updateScrollLimit(int maxFocus) {
        maxFocusCell = maxFocus;
        minFocusCell = 0;
//        updateIndicator();
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (!isStepMode) {
            return super.onSingleTapUp(e);
        }

        if (getChildCount() > 0) {
            ArrayList<DrawableItem> itemS = checkHitedItem(e);
            for (int i = itemS.size() - 1; i > -1; i--) {
                final DrawableItem item = itemS.get(i);
                if (item != null && item.isVisible()) {
                    if (item.onSingleTapUp(e)) {
                        if (lastTouchedItem != null && lastTouchedItem != item) {
                            lastTouchedItem.onTouchCancel( e );
                        }

                        lastTouchedItem = item;
                        return true;
                    }
                }
            }
        }

        return false;
    }

	@Override
	protected ArrayList<DrawableItem> checkHitedItem(MotionEvent e) {
		ArrayList<DrawableItem> itemRes = new ArrayList<DrawableItem>();
		if (e != null) {

			final int size = getChildCount();
			for (int i = 0; i < size; i++) {
				final DrawableItem item = getChildAt(i);
				if (item != null) {
					if (getXContext().getExchangee().checkHited(item, e.getX(),
							e.getY(), false)) {
						itemRes.add(item);
					}
				}
			}
		}

		return itemRes;
	}

    void setStepMode(boolean isStep) {
        isStepMode = isStep;

        if (!isStep) {
            // fix bug 13736.
            if (mAnimController.isOffsetXAnimStart()) {
                mAnimController.stopOffsetXAnim();
            }
            mCurrentOffsetX = 0.0f;
            invalidateForOffsetX(1);
        }
    }

    private void invalidateForOffsetX(long duration) {
        if (mAnimController.isOffsetXAnimStart()) {
            mAnimController.stopOffsetXAnim();
        }
        mAnimController.startOffsetXAnim(duration);
        
        if (mAnimController.isOffsetYAnimStart()) {
        	mAnimController.stopOffsetYAnim();
        }
        mAnimController.startOffsetYAnim(OffsetYAnimDuration);
        
        mOffsetY = mOffsetYTarget = 0;
        mAnimController.stopTouchAnim();
    }

    @Override
    public boolean onDown(MotionEvent e) {
        mDown = true;
        return super.onDown(e);
    }

    @Override
    public boolean onFingerUp(MotionEvent e) {
    	android.util.Log.i( "wow", "onFingerUp   in PagedView . grab is : " + getXContext().isGrabScrollState() );
    	if( getXContext().isGrabScrollState() ){
    		return false;
    	}
    	
        Log.i(TAG, "onFingerUp  isStepMode ==== " + isStepMode + "   mFling = " + mFling);
        Log.i(TAG, "onFingerUp  mDown ==== " + mDown);

        if (isStepMode && !mFling && mDown) {
            Log.i(TAG, "onFingerUp  not from  fling ==== " + mOffsetX);

            float sign = Math.signum(mOffsetX);
            float abs = Math.abs(mOffsetX);

            if (abs < firstStep) {
                setCurrentOffsetX(abs, 0f, firstStep, sign);
            } else if (Math.abs(mOffsetX) < secondStep) {
                setCurrentOffsetX(abs, firstStep, secondStep, sign);
            } else {
                setCurrentOffsetX(abs, secondStep, 1f, sign);
            }
        }

		mFling = false;
		mDown = false;
		
		// from super.
		resetTouchBounds();
		desireTouchEvent(false);
		
		if (!mAnimController.isOffsetXAnimStart()) {
			mAnimController.startOffsetXAnim(OffsetXAnimDuration);
		}
		if (!mAnimController.isOffsetYAnimStart()) {
			mAnimController.startOffsetYAnim(OffsetYAnimDuration);
		}
		mOffsetY = mOffsetYTarget = 0;
		mAnimController.stopTouchAnim();
		
        //for stage switch
        if(mLongPressHappened){
        	bringStageToFront();
        	mLongPressHappened = false;
        }
        
        boolean superRet = fingerUpFromBaseGroup(e);
        invalidateAtOnce();

        return superRet;
    }

    private boolean fingerUpFromBaseGroup(MotionEvent e) {
	    resetPressedState();
        
        if (getChildCount() > 0) {
            ArrayList<DrawableItem> itemS = checkHitedItem(e);
            /*** RK_ID: fixbug.  AUT: zhaoxy . DATE: 2013-02-18 . START***/
            if (itemS.isEmpty()) {
                if (lastTouchedItem != null) {
                    lastTouchedItem.onTouchCancel( e );
                }
                lastTouchedItem = null;
                return true;
            }
            /*** RK_ID: fixbug.  AUT: zhaoxy . DATE: 2013-02-18 . END***/
            for (int i = itemS.size() - 1; i > -1; i--) {
                final DrawableItem item = itemS.get(i);
                if (item != null && item.isVisible() && item.isTouchable()) {
                    if (item.onFingerUp(e)) {
                        if (lastTouchedItem != null && lastTouchedItem != item) {
                            lastTouchedItem.onTouchCancel( e );
                        }
                        lastTouchedItem = item;
                        return true;
                    }
                }
            }
        }
		return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    	
    	android.util.Log.i( "wow", "onFling now in PagedView . grab is : " + getXContext().isGrabScrollState() );
    	if( getXContext().isGrabScrollState() ){
    		return false;
    	}
    	
        if (!isStepMode) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        float x = e1.getX() - e2.getX();
        Log.i(TAG, "onFling  **** distance === " + (int) (x / mCellWidth));
        Log.i(TAG, "onFling  **** mCurrentFocusCell === " + mCurrentFocusCell);

        boolean res = true;

        int oldIndex = mCurrentFocusCell;
        switch (currOrientation) {
        case ORI_RIGHT:
            if (mCurrentFocusCell > minFocusCell) {
                mCurrentFocusCell -= (1 - (int) (x / mCellWidth));
                if (mCurrentFocusCell < minFocusCell) {
                    mCurrentFocusCell = minFocusCell;
                }

                getOffsetByFocusCell();
//                Log.i(TAG, "onFling  **** ORI_RIGHT   mCurrentOffsetX === " + mCurrentOffsetX);
                regulateOffsetXAndPage();

                mAnimController.stopTouchAnim();
                mAnimController.startOffsetXAnim(OffsetXAnimDuration);
                mAnimController.startOffsetYAnim(OffsetYAnimDuration);
                if (!isPageMoving) {
                    isPageMoving = true;
                    onPageBeginMoving();
                }
            }

            mFling = true;
            break;

        case ORI_LEFT:
            if (mCurrentFocusCell < maxFocusCell) {
                // we can move to left again
                // calculate offsetX
                mCurrentFocusCell += (1 + (int) (x / mCellWidth));
                if (mCurrentFocusCell > maxFocusCell) {
                    mCurrentFocusCell = maxFocusCell;
                }

                Log.i(TAG, "onFling  *********** new focus cell === " + mCurrentFocusCell);
                getOffsetByFocusCell();
                regulateOffsetXAndPage();

                mAnimController.stopTouchAnim();
                mAnimController.startOffsetXAnim(OffsetXAnimDuration);
                mAnimController.startOffsetYAnim(OffsetYAnimDuration);
                if (!isPageMoving) {
                    isPageMoving = true;
                    onPageBeginMoving();
                }

            }
            mFling = true;
            break;

        default:
            break;
        }

        /*RK_ID: RK_SCREEN_EDIT . AUT: zhanggx1 . DATE: 2013-05-09 . S*/
        if (oldIndex != mCurrentFocusCell) {
        	invokeOnFocusCellChanged(oldIndex, mCurrentFocusCell);
        }
        /*RK_ID: RK_SCREEN_EDIT . AUT: zhanggx1 . DATE: 2013-05-09 . E*/
        
        invalidate();
        return res;
    }

    private void setCurrentOffsetX(float offSetx, float minStep, float maxStep, float sign) {
        float left = offSetx - minStep;
        float right = maxStep - offSetx;

        if (left > right) {
            mCurrentOffsetX = maxStep * sign;
        } else {
            mCurrentOffsetX = minStep * sign;
        }

        getFocusCellByOffset();
    }

    private void getFocusCellByOffset() {
        float abs = Math.abs(mCurrentOffsetX);
        Log.i(TAG, "mCurrentOffsetX ===" + abs);

        int offset = 0;
        if (abs < devi) {
            offset = 0;
        } else if (Math.abs(abs - firstStep) < devi) {
            offset = mCurrentOffsetX > 0 ? 1 : -1;
        } else if (Math.abs(abs - secondStep) < devi) {
            offset = mCurrentOffsetX > 0 ? 2 : -2;
        } else {
            offset = mCurrentOffsetX > 0 ? 3 : -3;
        }

        int oldIndex = mCurrentFocusCell;
        mCurrentFocusCell = mCurrentPage * PAGE_CELL_COUNT + (PAGE_CELL_COUNT - 1) / 2 - offset;
        /*RK_ID: RK_SCREEN_EDIT . AUT: zhanggx1 . DATE: 2013-05-09 . S*/
        if (oldIndex != mCurrentFocusCell) {
        	invokeOnFocusCellChanged(oldIndex, mCurrentFocusCell);
        }
        /*RK_ID: RK_SCREEN_EDIT . AUT: zhanggx1 . DATE: 2013-05-09 . E*/
    }

    private void getOffsetByFocusCell() {
        mCurrentOffsetX = (mCurrentPage * PAGE_CELL_COUNT + (PAGE_CELL_COUNT - 1) / 2 - mCurrentFocusCell)
                * firstStep;
    }

    private class ScreenPageAnimController extends PageAnimController {

        @Override
        public void startOffsetXAnim(long durationMillis) {
            if (!isStepMode) {
                super.startOffsetXAnim(durationMillis);
                return;
            }

            offsetXAnimActivate = true;
            mOffsetXAnimDuration = durationMillis;
            mOffsetXAnimPlayTime = 0;
            s0_x = mOffsetX;
            s_x = mCurrentOffsetX - mOffsetX;
        }

        @Override
        public void update(long timeDelta) {
            if (!isStepMode) {
                super.update(timeDelta);
                return;
            }

            if (offsetXAnimActivate) {
                mOffsetXAnimPlayTime += timeDelta;
                float step = s_x
                        * mInterpolator.getInterpolation((float) mOffsetXAnimPlayTime
                                / (float) mOffsetXAnimDuration);
                mOffsetX = s0_x + step;
                if (Math.abs(mCurrentOffsetX - mOffsetX) < 0.000925926f
                        || mOffsetXAnimPlayTime >= mOffsetXAnimDuration) {
                    mOffsetX = mCurrentOffsetX;
                    mOffsetXTarget = mCurrentOffsetX;
                    stopOffsetXAnim();
                    isPageMoving = false;
                    onPageEndMoving();
                } else {
                    mOffsetXTarget = mOffsetX;
                }
                invalidate();
            }

            if (pageTouchAnimActivate) {
                float delta = mOffsetXTarget - mOffsetX;
                mOffsetX += delta * .4f;
                mOffsetY += (mOffsetYTarget - mOffsetY) * .8f;
                invalidate();
                if (mOffsetX > 1f || Math.abs(mOffsetX - 1) < devi) {
                    if (mCurrentPage - 1 > -1) {

                        // R2
                        if (pageSwitchers != null) {
                            for (PageSwitchListener pl : pageSwitchers) {
                                pl.onPageSwitching(mCurrentPage, mCurrentPage - 1, mOffsetX);
                            }
                        }
                        // 2R

                        --mCurrentPage;
                    }
                    mOffsetX -= 1f;
                    mOffsetXTarget -= 1f;
                } else if (mOffsetX < -1 || Math.abs(mOffsetX + 1) < devi) {
                    if (mCurrentPage + 1 < mPageCount) {

                        // R2
                        if (pageSwitchers != null) {
                            for (PageSwitchListener pl : pageSwitchers) {
                                pl.onPageSwitching(mCurrentPage, mCurrentPage + 1, mOffsetX);
                            }
                        }
                        // 2R

                        ++mCurrentPage;
                    }
                    mOffsetX += 1f;
                    mOffsetXTarget += 1f;
                }
                currOrientation = (int) Math.signum(delta);

                isScrollToEdge();
            }
        }

    }

    private void isScrollToEdge() {
//        int focusCell = mCurrentPage * PAGE_CELL_COUNT + (PAGE_CELL_COUNT - 1) / 2
//                - (int) (mOffsetX / firstStep);
        getFocusCellByOffset();
        int focusCell = mCurrentFocusCell + (int) ((mCurrentOffsetX - mOffsetX) / firstStep);
        Log.i(TAG, "focusCell ====" + focusCell + "    mCurrentFocusCell =====" + mCurrentFocusCell);
        Log.i(TAG, "mCurrentOffsetX===="+ mCurrentOffsetX + "     mOffsetX ====" + mOffsetX);

        int oldIndex = mCurrentFocusCell;
        if ((mOffsetX > mCurrentOffsetX && focusCell == 0) || (focusCell < 0) || (mCurrentFocusCell < 0)) {
            mAnimController.stopTouchAnim();

            mCurrentFocusCell = 0;
            getOffsetByFocusCell();

            mOffsetX = mCurrentOffsetX;
            mOffsetXTarget = mCurrentOffsetX;
            mAnimController.stopOffsetXAnim();
            isPageMoving = false;
        } else if ((mOffsetX < mCurrentOffsetX && focusCell == maxFocusCell) || (focusCell > maxFocusCell)
                || (mCurrentFocusCell > maxFocusCell)) {
            mAnimController.stopTouchAnim();

            mCurrentFocusCell = maxFocusCell;
            getOffsetByFocusCell();

            mOffsetX = mCurrentOffsetX;
            mOffsetXTarget = mCurrentOffsetX;
            mAnimController.stopOffsetXAnim();
            isPageMoving = false;
        }
        /*RK_ID: RK_SCREEN_EDIT . AUT: zhanggx1 . DATE: 2013-05-09 . S*/
        if (oldIndex != mCurrentFocusCell) {
        	invokeOnFocusCellChanged(oldIndex, mCurrentFocusCell);
        }
        /*RK_ID: RK_SCREEN_EDIT . AUT: zhanggx1 . DATE: 2013-05-09 . E*/
    }

    int getCurrentFocusCell() {
        return mCurrentFocusCell;
    }

    void setCurrentFocusCell(int index) {
        setCurrentFocusCell(index, false);
    }

    void setCurrentFocusCell(int index, boolean immediately) {
        Log.i(TAG, "setCurrentFocusCell   index==== " + index);

        if (index >= 0 && index <= maxFocusCell) {
            int oldIndex = mCurrentFocusCell;
            mCurrentFocusCell = index;
            
            getOffsetByFocusCell();
            regulateOffsetXAndPage();

            invalidateForOffsetX(immediately ? 1 : DURATION_OFFSET_X);

            /*RK_ID: RK_SCREEN_EDIT . AUT: zhanggx1 . DATE: 2013-05-09 . S*/
            if (oldIndex != mCurrentFocusCell) {
                invokeOnFocusCellChanged(oldIndex, mCurrentFocusCell);
            }
            /*RK_ID: RK_SCREEN_EDIT . AUT: zhanggx1 . DATE: 2013-05-09 . E*/
        }
    }

    private void regulateOffsetXAndPage() {
        if (mCurrentOffsetX > 1f || Math.abs(mCurrentOffsetX - 1) < devi) {
            mAnimController.stopOffsetYAnim();
            mAnimController.stopOffsetXAnim();

            if (mCurrentPage - 1 > -1) {
                --mCurrentPage;
            }

            mOffsetX -= 1f;
            mOffsetXTarget -= 1f;
            mCurrentOffsetX -= 1f;
        } else if (mCurrentOffsetX < -1 || Math.abs(mCurrentOffsetX + 1) < devi) {
            mAnimController.stopOffsetYAnim();
            mAnimController.stopOffsetXAnim();

            if (mCurrentPage + 1 < mPageCount) {
                ++mCurrentPage;
            }
            mOffsetX += 1f;
            mOffsetXTarget += 1f;
            mCurrentOffsetX += 1f;
        }
    }
    
    /*RK_ID: RK_SCREEN_EDIT . AUT: zhanggx1 . DATE: 2013-05-09 . S*/
    interface OnFocusCellChangedListener {
    	void onFocusCellChanged(int oldIndex, int newIndex);
    }
    
    void setOnFocusCellChangedListener(OnFocusCellChangedListener l) {
    	onFocusCellChangedListener = l;
    }
    
    private void invokeOnFocusCellChanged(int oldIndex, int newIndex) {
    	int checkOldIndex = oldIndex >= this.mItemIDMap.size() || oldIndex < 0 ? 0 : oldIndex;
    	if (onFocusCellChangedListener != null) {
    		onFocusCellChangedListener.onFocusCellChanged(checkOldIndex, newIndex);
    	}
    	if (mItemIndicatorEnable && itemSwitchers != null) {
			for (PageSwitchListener pl : itemSwitchers) {
				pl.onPageSwitching(checkOldIndex, newIndex, 0);
			}
		}
    }
    /*RK_ID: RK_SCREEN_EDIT . AUT: zhanggx1 . DATE: 2013-05-09 . E*/

    @Override
    public int[] findNearestArea(int screen, int pixelX, int pixelY, int spanX, int spanY,
            int[] result) {
        if (!isStepMode) {
            return super.findNearestArea(screen, pixelX, pixelY, spanX, spanY, result);
        }

        int offset = (int) (mCurrentOffsetX / firstStep);
        int[] res = super.findNearestArea(screen, pixelX, pixelY, spanX, spanY, result);
        res[0] = res[0] - offset;

        Log.i(TAG, "findNearestArea   offset=====" + offset);
        Log.i(TAG, "res[0] === " + res[0] + "  ^^^^^^    screen ==== " + screen);

        return res;
    }

    @Override
    public void scrollToLeft(long duration) {
        if (!isStepMode) {
            super.scrollToLeft(duration);
        } else {
            setCurrentFocusCell(mCurrentFocusCell - 1);
        }
    }

    @Override
    public void scrollToRight(long duration) {
        if (!isStepMode) {
            super.scrollToRight(duration);
        } else {
            setCurrentFocusCell(mCurrentFocusCell + 1);
        }
    }

    @Override
    public void resetAnim() {
        super.resetAnim();

        if (isStepMode && !mFling && mDown) {

            Log.i(TAG, "resetAnim  not from  fling ==== " + mOffsetX);

            float sign = Math.signum(mOffsetX);
            float abs = Math.abs(mOffsetX);

            if (abs < firstStep) {
                setCurrentOffsetX(abs, 0f, firstStep, sign);
            } else if (Math.abs(mOffsetX) < secondStep) {
                setCurrentOffsetX(abs, firstStep, secondStep, sign);
            } else {
                setCurrentOffsetX(abs, secondStep, 1f, sign);
            }
        }

        mFling = false;
        mDown = false;

        resetTouchBounds();
        desireTouchEvent(false);
    }

    ///////////////////dooba
    @Override
    void updateIndicator() {
    	super.updateIndicator();
    	if (mItemIndicatorEnable && itemSwitchers != null) {
            for (PageSwitchListener pl : itemSwitchers) {
                pl.onUpdatePage(this.mItemIDMap.size(), this.mCurrentFocusCell);
            }
        }
    }
    
    public void addItemSwitchListener(PageSwitchListener pl) {
		if (this.itemSwitchers == null) {
			this.itemSwitchers = new ConcurrentLinkedQueue<XPagedView.PageSwitchListener>();
		}

		itemSwitchers.add(pl);
	}

	public boolean removeItemSwitchListener(PageSwitchListener pl) {
		if (itemSwitchers == null) {
			return false;
		}
		return this.itemSwitchers.remove(pl);
	}
	
	public void setItemSwitchListenerUsable(boolean enable) {
		mItemIndicatorEnable = enable;
		if (mItemIndicatorEnable && itemSwitchers != null) {
			for (PageSwitchListener pl : itemSwitchers) {
                pl.onUpdatePage(this.mItemIDMap.size(), this.mCurrentFocusCell);
            }
		}
	}
}
