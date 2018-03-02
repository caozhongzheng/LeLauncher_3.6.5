package com.lenovo.launcher.components.XAllAppFace;

import java.util.Random;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Process;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;
import com.lenovo.launcher2.customizer.Utilities;

public class XLoading extends DrawableItem {

    private static final boolean DBG = true;
    private static final String TAG = "XLoading";

    private Bitmap img;
    private int side = 0;
    private boolean mActivate = false;

    private Matrix m = new Matrix();

    public XLoading(XContext c) {
    	super(c);
    	
        try {
                BitmapDrawable d = (BitmapDrawable) Utilities.findDrawableByResourceName("anim_load", c.getContext());
                img = d.getBitmap();
                final int w = img.getWidth();
                side = w > side ? w : side;
        } catch (Exception e) {
            if (DBG) Log.d(TAG, e.getMessage());
        }
        resize(new RectF(0, 0, side, side));
    }

    @Override
    public void onDraw(IDisplayProcess c) {
        if (mActivate) {
            c.drawBitmap(img, m, getPaint());
        }
    }
    
    public boolean isStart() {
        return mActivate;
    }

    public void reset() {
        if (DBG) Log.d(TAG, "reset");
        m.reset();
    }

    public void start() {
        if (DBG) Log.d(TAG, "start");
        if (mAnimThread != null) {
            mAnimThread.cancel();
            mAnimThread = null;
        }
        
        mAnimThread = new AnimThread();
        mAnimThread.start();

        mActivate = true;
    }

    public void stop() {
        if (DBG) Log.d(TAG, "stop");
        mActivate = false;
        if (mAnimThread != null) {
            mAnimThread.cancel();
            mAnimThread = null;
        }
    }
    
    private AnimThread mAnimThread = null;
    
    private class AnimThread extends Thread {
        private static final byte FRAME_TIME = 30;
        private static final byte DELTA = 7;
        private volatile boolean isRun = false;
        private long startTime = 0;
        private long endTime = 0;
        private long interval = 0;
        private int MAX_DEG = 7200;
        private int mDegOffset = 0;
        
        public AnimThread() {
            long tag = System.currentTimeMillis();
            tag -= tag / 1000 * 1000;
            this.setName("XLoadingAnim-" + tag);
        }
        
        @Override
        public void run() {
//            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            while (isRun) {
                try {
                    if (endTime == 0) {
                        endTime = System.currentTimeMillis();
                    }
                    startTime = System.currentTimeMillis();
                    interval = startTime - endTime;
                    if (interval < 5 && interval > 500) {
                        endTime = System.currentTimeMillis();
                        continue;
                    }
                    
                    mDegOffset += DELTA;
                    if (mDegOffset > MAX_DEG) {
                        isRun = false;
                        continue;
                    }
                    m.setRotate(mDegOffset, side >> 1, side >> 1);
                    invalidate();
                    
                    endTime = System.currentTimeMillis();
                    if ((endTime - startTime) < FRAME_TIME) {
                        Thread.sleep(FRAME_TIME + startTime - endTime);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        
        @Override
        public synchronized void start() {
            isRun = true;
            endTime = System.currentTimeMillis();
            mDegOffset = 0;
            super.start();
        }

        public synchronized void cancel() {
            isRun = false;
        }
    }

}
