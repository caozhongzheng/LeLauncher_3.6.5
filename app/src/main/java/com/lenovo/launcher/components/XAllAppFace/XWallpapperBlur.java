package com.lenovo.launcher.components.XAllAppFace;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.lenovo.launcher.components.XAllAppFace.XWallpaperPagedView.WallpaperOffsetInterpolator;
import com.lenovo.launcher.components.XAllAppFace.slimengine.BaseDrawableGroup;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;
import com.lenovo.launcher2.customizer.Debug.R5;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.senior.utilities.Utilities;

public class XWallpapperBlur extends DrawableItem {
    
    private XDragLayer root;
    private final WallpaperManager mWallpaperManager;
    private XWallpaperPagedView mWallpaperPagedView;

    private static final float SCALE_WALLPAPER = 1.4f;
    float topWallPaper = 0;
    boolean fullScreenFlag = false;
    float mInput;

    private Drawable wallPaperBitmap;
    private boolean enable = true;
    private boolean blurActivite = false;
    private boolean showBlur = false;
    private final Paint blurPaint = new Paint();
    private Bitmap blurBitmap;
    private int mDisplayWidth;
    private int mDisplayHeight;
    
    public XWallpapperBlur(XContext context, XDragLayer root) {
        super(context);
        this.root = root;
        mWallpaperManager = WallpaperManager.getInstance(context.getContext());
//        mWallpaperPagedView = (XWallpaperPagedView) ((XLauncher) context.getContext()).getWorkspace().getPagedView();

        XLauncher xLauncher = (XLauncher) (mContext.getContext());
        Display display = xLauncher.getWindowManager().getDefaultDisplay(); // bug 10371.
        mDisplayWidth = display.getWidth();
        mDisplayHeight = display.getHeight();
    }

    public void setWallpaperPagedView(XWallpaperPagedView mWallpaperPagedView) {
        this.mWallpaperPagedView = mWallpaperPagedView;
    }

    public void buildBlurBitmap() {
        currentThread = System.nanoTime();
        new BlurThread(currentThread).start();
    }

    private final Object mLock = new Object();
    private long currentThread = -1;

    private class BlurThread extends Thread {
        private long id = -1;
        public BlurThread(long id) {
            this.id = id;
            setName("blur_thread");
        }
        @Override
        public void run() {
            synchronized (mLock) {
                if (this.id != currentThread) {
                    android.util.Log.d("Blur", "I am old task!");
                    return;
                }
                blurActivite = false;
                wallPaperBitmap = mWallpaperManager.getDrawable();
                clearBlurBitmap();
                blurBitmap = Bitmap.createBitmap(wallPaperBitmap.getIntrinsicWidth(), wallPaperBitmap.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                // long take = Utilities.newInstance().blur(((BitmapDrawable) wallPaperBitmap).getBitmap(), blurBitmap, 50);
                long take = Utilities.fastBlur(((BitmapDrawable) wallPaperBitmap).getBitmap(), blurBitmap, 70);

                android.util.Log.d("Blur", "buildBlurBitmap end take " + take + " w = " + blurBitmap.getWidth() + " h = " + blurBitmap.getHeight());
                blurActivite = true;
            }
        }
    }

    /**
     * @return check Whether to rebuild.
     */
    public boolean checkLiveWallpaper() {
        android.app.WallpaperInfo info = mWallpaperManager.getWallpaperInfo();
        if (info != null) {
            //动态壁纸
            if (enable) {
                //如果模糊开启，关掉返回，并且不build
                enable = false;
                synchronized (mLock) {
                    clearBlurBitmap();
                    wallPaperBitmap = null;
                }
                invalidate();
                return true;
            }
        } else {
            if (!enable) {
                enable = true;
                invalidate();
                return true;
            }
        }
        return false;
    }

    public boolean isEnable() {
        return enable;
    }

    public void show(boolean backmost) {
        if (this.root != null) {
            this.root.addItem(this);
            this.resize(new RectF(0, 0, getParent().getWidth(), getParent().getHeight()));
            if (backmost) {
                this.root.bringChildToBack(this);
            } else {
                this.root.bringChildToFront(this);
            }
            /*** RK_ID: HIGH_QUALITY_EFFECTS. AUT: zhaoxy. DATE: 2013-10-16 . START***/
            showBlur = SettingsValue.ENABLE_HIGH_QUALITY_EFFECTS;
            /*** RK_ID: HIGH_QUALITY_EFFECTS. AUT: zhaoxy. DATE: 2013-10-16 . END***/
        }
    }

    public void hide() {
        BaseDrawableGroup parent = (BaseDrawableGroup) getParent();
        if (parent == null) {
            return;
        }
        parent.removeItem(this, false);
        reuse();
        showBlur = false;
        invalidate();
    }

    private void clearBlurBitmap() {
        blurActivite = false;
        if (blurBitmap != null && !blurBitmap.isRecycled()) {
            blurBitmap.recycle();
        }
        blurBitmap = null;
    }

    public void setBlurEnable(boolean enable) {
        showBlur = enable;
    }

    public void updateFolderAnim(float input) {
        mInput = input;
    }

    protected int getCurrentLeft() {
        return getLeft(mWallpaperPagedView.mWallpaperOffset.getCurrX());
    }

    private int getLeft(float wallpaperOffset) {
        if (wallPaperBitmap == null) {
            wallPaperBitmap = mWallpaperManager.getDrawable();
        }
        float delta = wallPaperBitmap.getIntrinsicWidth() - mDisplayWidth;
        float offsetX = delta * wallpaperOffset;
        int left = -(int) (offsetX + .5f);

        return left;
    }

    private int getTop() {

        int barHeight = 0;

        if (!SettingsValue.hasExtraTopMargin()) {
            XLauncher xLauncher = (XLauncher) (mContext.getContext());
            Rect rect1 = new Rect();
            xLauncher.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect1);
            barHeight = rect1.top;
        }

        float delta = wallPaperBitmap.getIntrinsicHeight() - mDisplayHeight;

        float offsetY = (float) (delta * mWallpaperPagedView.mWallpaperOffset.getFinalY());

        int top = -(int) (barHeight + offsetY + .5f);

        return top;
    }

    public boolean checkX(float globalX) {
        if (wallPaperBitmap == null) {
            wallPaperBitmap = mWallpaperManager.getDrawable();
        }
        float wallPaperWidth = wallPaperBitmap.getIntrinsicWidth();
        float leftOut = getCurrentLeft();
        float right = wallPaperWidth + leftOut - globalX;
        right *= SCALE_WALLPAPER;
        if (right < getWidth() / 2) {
            return false;
        }
        float left = -leftOut + globalX;
        left *= SCALE_WALLPAPER;
        if (left < getWidth() / 2) {
            return false;
        }
        return true;
    }

    public boolean checkY(float globalY) {
        if (wallPaperBitmap == null) {
            wallPaperBitmap = mWallpaperManager.getDrawable();
        }
        float wallPaperHeight = wallPaperBitmap.getIntrinsicHeight();
        float topOut = getTop();
        float top = -topOut + globalY;
        top *= SCALE_WALLPAPER;
        if (top < getHeight() / 2) {
            return false;
        }
        float bottom = wallPaperHeight + topOut - globalY;
        bottom *= SCALE_WALLPAPER;
        if (bottom < getHeight() / 2) {
            return false;
        }
        return true;
    }

    @Override
    public void resize(RectF rect) {
        super.resize(rect);
        XLauncher xLauncher = (XLauncher) getXContext().getContext();
        fullScreenFlag = xLauncher.isCurrentWindowFullScreen();
        Display display = xLauncher.getWindowManager().getDefaultDisplay(); // bug 10371.
        mDisplayWidth = display.getWidth();
        mDisplayHeight = display.getHeight();
    }

    @Override
    public void onDraw(IDisplayProcess canvas) {
        if (!enable || wallPaperBitmap == null)
            return;

        final float offsetX = getCurrentLeft();
        
        XLauncher xLauncher = (XLauncher) getXContext().getContext();
        int barHeight = 0;
        if (!SettingsValue.hasExtraTopMargin()) {
            barHeight = xLauncher.getStatusBarHeight();
        }
        if (fullScreenFlag) {
            float delta = 0;
            delta = (wallPaperBitmap.getIntrinsicHeight() - mDisplayHeight) * mWallpaperPagedView.mWallpaperOffset.getFinalY();
            topWallPaper = - delta - barHeight * (1 - 1f / SCALE_WALLPAPER) * mInput;
        } else {
            topWallPaper = getTop();
        }

        wallPaperBitmap.setBounds(0, 0, wallPaperBitmap.getIntrinsicWidth(), wallPaperBitmap.getIntrinsicHeight());

        canvas.save();
        canvas.translate(offsetX, topWallPaper);
        if (showBlur) {
            if (blurActivite && blurBitmap != null && !blurBitmap.isRecycled()) {
                blurPaint.setAlpha((int) (255 * mInput));
                canvas.drawDrawable(wallPaperBitmap);
                canvas.drawBitmap(blurBitmap, 0, 0, blurPaint);
            }
        } else {
            canvas.drawDrawable(wallPaperBitmap);
        }
        canvas.restore();
    }

}
