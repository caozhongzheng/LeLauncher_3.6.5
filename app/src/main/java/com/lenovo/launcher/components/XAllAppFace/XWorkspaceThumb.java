package com.lenovo.launcher.components.XAllAppFace;

import java.util.ArrayList;

import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.FloatMath;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.slimengine.BaseDrawableGroup;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IController;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem.OnVisibilityChangeListener;
import com.lenovo.launcher2.commoninterface.ApplicationInfo;
import com.lenovo.launcher2.commoninterface.ItemInfo;
import com.lenovo.launcher2.commoninterface.LauncherSettings;

public class XWorkspaceThumb extends BaseDrawableGroup implements XScrollDropTarget {
    private XPagedView xPagedView;
    private XLauncher mXLauncher;

    private static final int CELL_X_COUNT = 5;
    private static final int CELL_Y_COUNT = 1;
    private static final String TAG = "XWorkspaceThumb";
    private EffectAnimController mEffectAnimController;

    private static final int PAGE_PADDING = 20;
    private static final int SCROLL_DURATION = 300;

    private boolean mInScrollArea;
//    private int mScrollDirection;
    private Drawable mLeftHoverDrawable;
    private Drawable mRightHoverDrawable;
    private RectF mLeftRect;
    private RectF mRightRect;
    private int translateY = 0;
    private int iconHeight = 0;

//    private stati

    public XWorkspaceThumb(XContext context, RectF rect) {
        super(context);
        final float density = context.getResources().getDisplayMetrics().density;
        int padding = (int) Math.ceil(density * PAGE_PADDING);
        xPagedView = new XPagedView(context, new RectF(this.localRect.left + padding,
                this.localRect.top, this.localRect.right - padding, this.localRect.bottom));
        addItem(xPagedView);

        mXLauncher = (XLauncher) context.getContext();
        
        mEffectAnimController = new EffectAnimController();
        
        // visible listen always
        this.wantKnowVisibleState(true);
        this.setOnVisibilityChangeListener( new OnVisibilityChangeListener() {
			
			@Override
			public void onVisibilityChange(DrawableItem who, boolean visible) {
				if (mEffectAnimController != null) {
					if (!visible) {
						unregisterIController(mEffectAnimController);
					} else {
						registerIController(mEffectAnimController);
					}
				}
			}
		});
    }

    public void init(XContext context) {
        xPagedView.clearAllItems();

        XWorkspace workspace = mXLauncher.getWorkspace();
        Resources res = mXLauncher.getResources();

        // calculate screen count
        int cellCount = workspace.getPagedView().getPageCount();
        int pageCount = (int) Math.ceil((float) cellCount / (CELL_X_COUNT * CELL_Y_COUNT));

        Log.i(TAG, "pageCount === " + pageCount);
        mInScrollArea = (pageCount > 1);
        xPagedView.setup(pageCount, CELL_X_COUNT, CELL_Y_COUNT);
        xPagedView.setCurrentPage(0);
        xPagedView.setEnableEffect(false);
        xPagedView.setLoop(false);
        xPagedView.setScrollBackEnable(false);

        ArrayList<Bitmap> bitmaps = getWorkspaceThumb(workspace);

        for (int index = 0; index < cellCount; index++) {
            Bitmap currentPage = bitmaps.get(index);
            ItemInfo info = new ItemInfo();
            info.screen = index / CELL_X_COUNT;
            info.cellX = index % CELL_X_COUNT;
            info.cellY = 0;
            info.spanX = info.spanY = 1;

            final BaseDrawableGroup target = new BaseDrawableGroup(context);

            final XIconDrawable icon = new XIconDrawable(context, currentPage);
            icon.setBackgroundDrawable(res.getDrawable(R.drawable.allapps_thumb_bg));
            icon.setTouchable(false);

            layoutIcon(icon);

//            final int cellIndex = index;
            target.addItem(icon);
//            target.setOnClickListener(new DrawableItem.OnClickListener() {
//
//                @Override
//                public void onClick(DrawableItem item) {
////                    growIcon(cellIndex, icon);
////                    growHalfIcon(cellIndex);
//                }
//            });

            XPagedViewItem itemToAdd = new XPagedViewItem(context, target, info);
            xPagedView.addPagedViewItem(itemToAdd);
        }

        // center xPagedView
        if (pageCount == 1) {
            int width = (int) xPagedView.getWidth();
            int intrinsicWidth = xPagedView.getCellWidth() * cellCount;
            final float density = context.getResources().getDisplayMetrics().density;
            int padding = (int) Math.ceil(density * PAGE_PADDING);
            xPagedView.setRelativeX(padding + (width - intrinsicWidth) / 2);
            Log.i(TAG, "new relativeX === " + xPagedView.getRelativeX());
        } else if (pageCount > 1) {
            final float density = context.getResources().getDisplayMetrics().density;
            int padding = (int) Math.ceil(density * PAGE_PADDING);
            xPagedView.setRelativeX(this.localRect.left + padding);
        }

        xPagedView.invalidate();

    }

    protected void growHalfIcon(int cellIndex) {
        final float totalHeight = xPagedView.getHeight();
        int screen = xPagedView.getCurrentPage();

        for (int cellX = 0; cellX < CELL_X_COUNT; cellX++) {
            final XIconDrawable otherIcon = (XIconDrawable) findIconByIndex(screen, cellX);
            if (otherIcon == null) {
                continue;
            }

            if (Math.abs(cellIndex - cellX) > 1) {
                float height = otherIcon.getHeight();
                otherIcon.setRelativeY(totalHeight - height);
//                Log.i(TAG, "others totalHeight - height ===" + (totalHeight - height));

                Matrix m = otherIcon.getMatrix();
                m.reset();
                otherIcon.updateMatrix(m);

            } else if (Math.abs(cellIndex - cellX) == 1) {

                ValueAnimator growHalfAnimator = ValueAnimator.ofFloat(1.0f, 1.3f);
                growHalfAnimator.setDuration(250);
                growHalfAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

                growHalfAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        final float scale = (Float) animation.getAnimatedValue();

//                        int height = (int) ((1.5f - scale) * otherIcon.getHeight());
//                        Log.i(TAG, "left/right scale = " + scale + "    height ===" + height);
//                        otherIcon.setRelativeY(height);

                        final Matrix matrix = otherIcon.getMatrix();
                        matrix.reset();
                        matrix.setScale(scale, scale, otherIcon.localRect.centerX(), otherIcon.localRect.bottom);
                        otherIcon.updateMatrix(matrix);
                    }
                });

                getXContext().getRenderer().injectAnimation(growHalfAnimator, false);
            }
        }

    }

    protected void growIcon(final int cellIndex, final XIconDrawable icon) {
//        final float totalHeight = xPagedView.getHeight();

        ValueAnimator growAnimator = ValueAnimator.ofFloat(1.25f, 1.5f);
        growAnimator.setDuration(250);
        growAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        growAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float scale = (Float) animation.getAnimatedValue();

//                float height = scale * icon.getHeight();
//                Log.i(TAG, "scale === " + scale + "    icon height====" + height);
//                Log.i(TAG, "this icon totalHeight - height ===" + (totalHeight - height));
//                icon.setRelativeY(totalHeight - height);

                final Matrix matrix = icon.getMatrix();
                matrix.reset();
                matrix.setScale(scale, scale, icon.localRect.centerX(), icon.localRect.bottom);
                icon.updateMatrix(matrix);
            }
        });

        getXContext().getRenderer().injectAnimation(growAnimator, false);
    }

    protected DrawableItem findIconByIndex(int screen, int index) {
        XPagedViewItem item = xPagedView.findPageItemAt(screen, index, 0);
        if (item == null) {
            return null;
        }
        DrawableItem drawableTarget = item.getDrawingTarget();
        if (drawableTarget == null || !(drawableTarget instanceof BaseDrawableGroup)) {
            return null;
        }

        return ((BaseDrawableGroup) drawableTarget).getChildAt(0);
    }

    private void layoutIcon(XIconDrawable icon) {
        float totalHeight = xPagedView.getHeight();
        float height = icon.getHeight();
        Log.i(TAG, "totalHeight === " + totalHeight + "    icon height====" + height);
        if (translateY == 0) {
            translateY = (int) (totalHeight - height);
        }
        if (iconHeight == 0) {
            iconHeight = (int) height;
        }
        icon.setRelativeY(totalHeight - height);

        float totalWidth = xPagedView.getCellWidth();
        float width = icon.getWidth();
        icon.setRelativeX((totalWidth - width) / 2.0f);
    }

    private ArrayList<Bitmap> getWorkspaceThumb(XWorkspace workspace) {
        int length = workspace.getPagedView().getPageCount();
        ArrayList<Bitmap> thumbs = new ArrayList<Bitmap>();

        workspace.setWidgetVisible(true);

        int height = (int) (xPagedView.getCellHeight() * 0.66f - 10);
        int gap = getXContext().getResources().getDimensionPixelSize(R.dimen.allapps_thumb_h_gap);
        int bgPadding = getXContext().getResources().getDimensionPixelSize(R.dimen.allapps_thumb_bg_padding);
        int width = xPagedView.getCellWidth() - gap + bgPadding;

        for (int i = 0; i < length; i++) {
            Bitmap bitmap = mXLauncher.getSnapBitmap(width, height, i, 10, 10);
            thumbs.add(bitmap);
        }
        workspace.setWidgetVisible(false);

        return thumbs;
    }

    @Override
    public void resize(RectF rect) {
        super.resize(rect);

        final float density = getXContext().getResources().getDisplayMetrics().density;
        int padding = (int) Math.ceil(density * PAGE_PADDING);
        xPagedView.resize(new RectF(padding, 0, rect.width() - padding, rect.height() - 6));
    }
    
    private float mDragPosX = 0, mDragPosY = 0;
    private static final boolean DEBUG_DRAG = true;
    private static final String TAG_DEBUG_DRAG = "DEBUG_DRAG";
    
    private class EffectAnimController implements IController {
    	
    	private static final float MAX_SCALE_DELTA = 0.3f;
    	private static final int ENTER_TIMEOUT = 1500;
    	private int enter_waittime = 0;
        
        private boolean dragEffectAnimActivate = false;
        
        private int targetScreen = -1;
        
        public void startDargEffectAnim() {
            dragEffectAnimActivate = true;
            targetScreen = -1;
            enter_waittime = 0;
        }
        
        public void stopDargEffectAnim() {
            dragEffectAnimActivate = false;
            targetScreen = -1;
            enter_waittime = 0;
        }
        
        @Override
        public void update(long timeDelta) {
            if (dragEffectAnimActivate) {

            	final float enterY = xPagedView.getHeight() / 4;
            	final float scaleEnter = mDragPosY < enterY ? mDragPosY / enterY : 1f;
            	final int cellWidth = (int) (xPagedView.getWidth() / CELL_X_COUNT);
                int screen = xPagedView.getCurrentPage();
                int leftPadding = (int) xPagedView.getRelativeX();
				for (int cellX = 0; cellX < CELL_X_COUNT; cellX++) {
					final XIconDrawable otherIcon = (XIconDrawable) findIconByIndex(screen, cellX);
					if (otherIcon == null) {
						continue;
					}
					final float distanceX = Math.abs(mDragPosX - leftPadding- cellWidth / 2f - cellWidth * cellX);
					final int maxDistanceX = cellWidth << 1;
					float scale = 1.0f;
					if (distanceX < maxDistanceX) {
	                    scale = 1 + (MAX_SCALE_DELTA - MAX_SCALE_DELTA * FloatMath.sqrt(distanceX / maxDistanceX)) * scaleEnter;
                    }
					final Matrix matrix = otherIcon.getMatrix();
					matrix.reset();
					matrix.setScale(scale, scale, otherIcon.localRect.centerX(), otherIcon.localRect.bottom);
					otherIcon.updateMatrix(matrix);
				}

                int currtarget = findIconIndexByPosition(mDragPosX, mDragPosY);
                final int max = mXLauncher.getWorkspace().getPageCount();
                if (currtarget < 0 || currtarget >= max) {
                    enter_waittime = 0;
                    return;
                }
				if (targetScreen != currtarget) {
				    targetScreen = currtarget;
				    enter_waittime = 0;
                } else {
                    enter_waittime += timeDelta;
                    if (enter_waittime > ENTER_TIMEOUT) {
                        if (DEBUG_DRAG) Log.d(TAG_DEBUG_DRAG, "Time out!!! screen = " + currtarget);
                        stopDargEffectAnim();
                        ((XApplistView) getParent()).stopEditMode();
                        mXLauncher.getWorkspace().setCurrentPage(currtarget);
                        mXLauncher.showWorkspace(true);
                        mXLauncher.setLauncherWindowStatus(true);
                    }
                }
                
                invalidate();
            }
        }

    }
    
    private int findIconIndexByPosition(float relativeX, float relativeY) {
        int screen = xPagedView.getCurrentPage();

        if (relativeX < xPagedView.getRelativeX()) {
            return Integer.MIN_VALUE;
        } else if (relativeX > (xPagedView.getRelativeX() + xPagedView.getWidth())) {
            return Integer.MAX_VALUE;
        }

        int index = (int) ((relativeX - xPagedView.getRelativeX()) * CELL_X_COUNT / xPagedView
                .getWidth()) + screen * CELL_X_COUNT;
        Log.i(TAG, "relativeX ==== " + relativeX + "    &&&&&&    index =====" + index);
        return index;
    }

    @Override
    public boolean isDropEnabled() {
        return getParent().isVisible();
    }

    @Override
    public void onDrop(XDragObject dragObject) {
    	if (DEBUG_DRAG) Log.d(TAG_DEBUG_DRAG, "Thumb onDrop");
        int screen = findIconIndexByPosition(mDragPosX, mDragPosY);
        final int max = mXLauncher.getWorkspace().getPageCount();
        if (screen < 0 || screen >= max) {
            return;
        }
        Log.d("DEBUG_DRAG", "screen = " + screen);
    	mXLauncher.completeAddApplication(((ApplicationInfo) dragObject.dragInfo).intent, LauncherSettings.Favorites.CONTAINER_DESKTOP, screen, -1, -1);
        updateThumb(screen);
    }

    public void updateThumb(int screen) {
        XWorkspace workspace = mXLauncher.getWorkspace();
        workspace.setWidgetVisible(true);

        int height = (int) (xPagedView.getCellHeight() * 0.66f - 10);
        int gap = getXContext().getResources().getDimensionPixelSize(R.dimen.allapps_thumb_h_gap);
        int bgPadding = getXContext().getResources().getDimensionPixelSize(R.dimen.allapps_thumb_bg_padding);
        int width = xPagedView.getCellWidth() - gap + bgPadding;
        Bitmap currentPage = mXLauncher.getSnapBitmap(width, height, screen, 10, 10);

        workspace.setWidgetVisible(false);

        XContext context = xPagedView.getXContext();

        final XIconDrawable icon = new XIconDrawable(context, currentPage);
        icon.setBackgroundDrawable(mXLauncher.getResources().getDrawable(R.drawable.allapps_thumb_bg));
        icon.setTouchable(false);

        layoutIcon(icon);

        XPagedViewItem item = xPagedView.findPageItemAt(screen / CELL_X_COUNT, screen
                % CELL_X_COUNT, 0);
        if (item != null) {
            DrawableItem drawableTarget = item.getDrawingTarget();
            if (drawableTarget != null && (drawableTarget instanceof BaseDrawableGroup)) {
                final BaseDrawableGroup target = (BaseDrawableGroup) drawableTarget;
                target.clearAllItems();
                target.addItem(icon);
            }
        }
    }

    @Override
    public void onDragEnter(XDragObject dragObject) {
        if (DEBUG_DRAG) Log.d(TAG_DEBUG_DRAG, "Thumb Enter");
        if (mEffectAnimController != null) {
	        registerIController(mEffectAnimController);
	        mEffectAnimController.startDargEffectAnim();
        }
    }

    @Override
    public void onDragOver(XDragObject dragObject) {
    	mDragPosX = dragObject.x;
    	mDragPosY = dragObject.y;
    }

    @Override
    public void onDragExit(XDragObject dragObject) {
        if (DEBUG_DRAG) Log.d(TAG_DEBUG_DRAG, "Thumb Exit");
    	if (mEffectAnimController != null) {
    		mEffectAnimController.stopDargEffectAnim();
	        unregisterIController(mEffectAnimController);
        }
    	int screen = xPagedView.getCurrentPage();
    	for (int cellX = 0; cellX < CELL_X_COUNT; cellX++) {
			final XIconDrawable otherIcon = (XIconDrawable) findIconByIndex(screen, cellX);
			if (otherIcon == null) {
				continue;
			}
			final Matrix matrix = otherIcon.getMatrix();
			matrix.reset();
			otherIcon.updateMatrix(matrix);
		}
    	invalidate();
    }

    @Override
    public XDropTarget getDropTargetDelegate(XDragObject dragObject) {
        return null;
    }

    @Override
    public boolean acceptDrop(XDragObject dragObject) {
        return true;
    }

    @Override
    public void getHitRect(Rect outRect) {
        outRect.set(0, 0, (int) getWidth(), (int) getHeight());
    }

    @Override
    public void getLocationInDragLayer(int[] loc) {
        mXLauncher.getDragLayer().getLocationInDragLayer(this, loc);
    }

    @Override
    public int getLeft() {
        return (int) getRelativeX();
    }

    @Override
    public int getTop() {
        return (int) getRelativeY();
    }

    @Override
    public void scrollLeft() {
        int screen = xPagedView.getCurrentPage();
        xPagedView.scrollToLeft(SCROLL_DURATION);
        resizeChildrenAtScreen(screen);
    }

    @Override
    public void scrollRight() {
        int screen = xPagedView.getCurrentPage();
        xPagedView.scrollToRight(SCROLL_DURATION);
        resizeChildrenAtScreen(screen);
    }

    private void resizeChildrenAtScreen(int screen) {
        for (int i = 0; i < CELL_X_COUNT; i++) {
            DrawableItem icon = findIconByIndex(screen, i);

            if (icon == null) {
                continue;
            }

            final Matrix matrix = icon.getMatrix();
            matrix.reset();
            icon.updateMatrix(matrix);
        }
    }

    @Override
    public boolean onEnterScrollArea(int x, int y, int direction) {
        boolean result = false;
        final int page = xPagedView.getCurrentPage()
                + (direction == XDragController.SCROLL_LEFT ? -1 : 1);
        if (0 <= page && page < xPagedView.getPageCount()) {
//            mInScrollArea = true;
//            mScrollDirection = direction;
            result = true;
        }

        initDrawableAndRect();
        return result;
    }

    private void initDrawableAndRect() {
        if (mLeftHoverDrawable == null) {
            mLeftHoverDrawable = getXContext().getResources().getDrawable(
                    R.drawable.allapps_hover_left_holo);
        }
        if (mRightHoverDrawable == null) {
            mRightHoverDrawable = getXContext().getResources().getDrawable(
                    R.drawable.allapps_hover_right_holo);
        }
        if (mLeftRect == null) {
            int height = mLeftHoverDrawable.getIntrinsicHeight();
            int width = mLeftHoverDrawable.getIntrinsicWidth();
            int left = (int) (xPagedView.getRelativeX() - width) / 2;
            int top = (int) (translateY + (iconHeight - height) / 2);
            mLeftRect = new RectF(left, top, width + left, height + top);
        }
        if (mRightRect == null) {
            int height = mRightHoverDrawable.getIntrinsicHeight();
            int width = mRightHoverDrawable.getIntrinsicWidth();
            int totalWidth = (int) this.getWidth();
            int right = (int) (totalWidth - (totalWidth - xPagedView.getRelativeX()
                    - xPagedView.getWidth() - width) / 2);
            int top = (int) (translateY + (iconHeight - height) / 2);
            mRightRect = new RectF(right - width, top, right, height + top);
        }
    }

    @Override
    public boolean onExitScrollArea() {
//        mInScrollArea = false;
//        mScrollDirection = XDragController.SCROLL_NONE;
        return true;
    }

    @Override
    public boolean isScrollEnabled() {
        return true;
    }

    @Override
    public int getScrollWidth() {
        return (int) this.localRect.width();
    }

    @Override
    public void onDraw(IDisplayProcess c) {
        super.onDraw(c);

        if (mInScrollArea) {
            initDrawableAndRect();
            int screen = xPagedView.getCurrentPage();
            int count = xPagedView.getPageCount();
            int left = screen - 1;
            if (left >= 0 && left < count) {
                c.drawDrawable(mLeftHoverDrawable, mLeftRect);
            }
            int right = screen + 1;
            if (right >= 0 && right < count) {
                c.drawDrawable(mRightHoverDrawable, mRightRect);
            }
        }
    }

    @Override
    public int getScrollLeftPadding() {
        return 0;
    }

}
