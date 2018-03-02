package com.lenovo.launcher.components.XAllAppFace;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.VelocityTracker;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.slimengine.BaseDrawableGroup;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;

public class XSlidingDrawer extends BaseDrawableGroup {

    private static final String TAG = "XSlidingDrawer";
    private XContext mContext;

    private XSlidingHandle mHandle;
    private XScreenContentTabHost mContent;

    private boolean mLocked;
    private boolean mAnimating;
    private boolean mTracking;
    private boolean mExpanded;

    // original top offset
    private float mTopOffset = -1f;
    private float mTouchDelta;
    private float mDragRegionHeight;

    private VelocityTracker mVelocityTracker;
    private final int mVelocityUnits;
    private static final int VELOCITY_UNITS = 1000;

    private boolean mAllowSingleTap;

    private final int mTapThreshold;
    private static final int TAP_THRESHOLD = 6;

    // allow tap, if slower than max velocity, it may be tap.
    private final int mMaximumTapVelocity;
    private static final float MAXIMUM_TAP_VELOCITY = 100.0f;

    // x or y max velocity
    private final int mMaximumMinorVelocity;
    private static final float MAXIMUM_MINOR_VELOCITY = 150.0f;

    // hypo max velocity
    private final int mMaximumMajorVelocity;
    private static final float MAXIMUM_MAJOR_VELOCITY = 200.0f;

    // when click handle, toggle the drawable with or without anim.
    private boolean mAnimateOnClick;

    private final int mMaximumAcceleration;
    private static final float MAXIMUM_ACCELERATION = 2000.0f;

    // animate info.
    private float mAnimatedVelocity;
    private float mAnimatedAcceleration;
    private float mAnimationPosition;

    private long mAnimationLastTime;
    private long mCurrentAnimationTime;
    private static final int ANIMATION_FRAME_DURATION = 1000 / 60;

    private static final int EXPANDED_FULL_OPEN = -10001;
    private static final int COLLAPSED_FULL_CLOSED = -10002;

    private final Handler mHandler = new XSlidingHandler();
    private static final int MSG_ANIMATE = 1000;

    private static final String PREFS_KEY = "xscreen_mng_view_prefs";
    private static final String SLIDING_HANDLE_CLOSE_KEY = "handle_close_with_anim";
    private static final String SLIDING_HANDLE_OPEN_KEY = "handle_open_with_anim";

    private OnDrawerOpenListener mOnDrawerOpenListener;
    private OnDrawerCloseListener mOnDrawerCloseListener;
    private OnDrawerScrollListener mOnDrawerScrollListener;

    /**
     * Callback invoked when the drawer is opened.
     */
    static interface OnDrawerOpenListener {
        /**
         * Invoked when the drawer becomes fully open.
         */
        public void onDrawerOpened();
    }

    /**
     * Callback invoked when the drawer is closed.
     */
    static interface OnDrawerCloseListener {
        /**
         * Invoked when the drawer becomes fully closed.
         */
        public void onDrawerClosed();
    }

    /**
     * Callback invoked when the drawer is scrolled.
     */
    public static interface OnDrawerScrollListener {
        /**
         * Invoked when the user starts dragging/flinging the drawer's handle.
         */
        public void onScrollStarted();

        /**
         * Invoked when the user stops dragging/flinging the drawer's handle.
         */
        public void onScrollEnded();
    }

    public XSlidingDrawer(XContext context) {
        super(context);
        mContext = context;

        final float density = context.getResources().getDisplayMetrics().density;
        mVelocityUnits = (int) (VELOCITY_UNITS * density + 0.5f);

        mAllowSingleTap = true;
        mTapThreshold = (int) (TAP_THRESHOLD * density + 0.5f);
        mMaximumTapVelocity = (int) (MAXIMUM_TAP_VELOCITY * density + 0.5f);

        mMaximumMinorVelocity = (int) (MAXIMUM_MINOR_VELOCITY * density + 0.5f);
        mMaximumMajorVelocity = (int) (MAXIMUM_MAJOR_VELOCITY * density + 0.5f);

        mAnimateOnClick = true;
        mMaximumAcceleration = (int) (MAXIMUM_ACCELERATION * density + 0.5f);

        mHandle = new XSlidingHandle(mContext);
        mContent = new XScreenContentTabHost(mContext, new RectF(0, 0, 0, 0), new RectF(0, 0, 0, 0));
    }

    @Override
    public void resize(RectF rect) {
        super.resize(rect);

        Resources res = mContext.getResources();
        float width = this.getWidth();
        mTopOffset = res.getDimensionPixelSize(R.dimen.screen_sliding_buffer_height);

        float dragRegionHeight = res.getDimensionPixelSize(R.dimen.screen_handle_height);
        mDragRegionHeight = dragRegionHeight;
        float dragRegionWidth = res.getDimensionPixelSize(R.dimen.xscreen_mng_drag_region_width);

        if (mHandle != null) {
            mHandle.resize(new RectF(0, 0, dragRegionWidth, dragRegionHeight));
            mHandle.setRelativeX((rect.width() - dragRegionWidth) / 2);
            mHandle.setRelativeY(mTopOffset);
        }

        if (mContent != null) {
            final float widgetHeight = res.getDimensionPixelSize(R.dimen.screen_host_widget_height);
            mContent.resize(new RectF(0, mTopOffset + dragRegionHeight, width, rect.bottom),
                    new RectF(0, 0, width, widgetHeight));
        }
    }

    void setupHandleAndContent(XDragController dragController, boolean addState) {
        mHandle.setOnClickListener(new XDrawerToggler());
        mHandle.updateDrawable(XSlidingHandle.State.UP);

        mContent.setup((XLauncher) mContext.getContext(), dragController, addState);

        addItem(mHandle);
        addItem(mContent);
    }
    public void setContentPage(int[] page){
    	if (mContent != null) {
    		mContent.setCurrentPage(page);
    	}
    }
    public int[] getCurrentPage(){
    	int[] current = new int[] {0, 0};
    	if (mContent != null) {
    		return mContent.getCurrentPage();
    	}
		return current;
	}
    public int[] getPageCount(){
    	int[] count = new int[] {0, 0};
    	if(mContent != null) {
    		return mContent.getPageCount();
    	}
		return count;
	}
    @Override
    public void onDraw(IDisplayProcess c) {
        Paint p = getPaint();
        final int oldColor = p.getColor();
        p.setColor(0xcc000000);
        c.drawRect(0, mTopOffset + mDragRegionHeight, getWidth(), getHeight(), p);
        p.setColor(oldColor);

        super.onDraw(c);
    }

    @Override
    public boolean onDown(MotionEvent event) {
        Log.v(TAG, "onDown   lock === " + mLocked);
        if (mLocked) {
            return false;
        }

        float y = event.getY();

        if (!mTracking && !mContext.getExchangee().checkHited(mHandle, event.getX(), y)) {
            return super.onDown(event);
        }

        mTracking = true;
        prepareContent();

        if (mOnDrawerScrollListener != null) {
            mOnDrawerScrollListener.onScrollStarted();
        }

        final float top = getRelativeY();
        mTouchDelta = y - top;
        prepareTracking(top);

        mVelocityTracker.addMovement(event);
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY, float previousX, float previousY) {
        Log.v(TAG, "onScroll   lock === " + mLocked);
        if (mLocked) {
            return false;
        }

        if (mTracking) {
            mVelocityTracker.addMovement(e2);
            moveHandle((int) (e2.getY() - mTouchDelta));
        }
        return mTracking || mAnimating || super.onScroll(e1, e2, distanceX, distanceY, previousX, previousY);
    }

    @Override
    public boolean onFingerUp(MotionEvent event) {
        if (mLocked) {
            return true;
        }

        eventUpOrCancel(event);
        return mTracking || mAnimating || super.onFingerUp(event);
    }

    @Override
    public boolean onFingerCancel(MotionEvent event) {
        if (mLocked) {
            return true;
        }

        eventUpOrCancel(event);
        return mTracking || mAnimating || super.onFingerCancel(event);
    }

    @Override
    public void onTouchCancel( MotionEvent e ) {
        if (mLocked) {
            super.onTouchCancel( e );
            return;
        }

        if (mTracking) {
            if (mExpanded) {
                animateClose(getRelativeY());
            } else {
                animateOpen(getRelativeY());
            }
        }

        super.onTouchCancel( e );
    }

    private void eventUpOrCancel(MotionEvent event) {
        if (mTracking) {
            mVelocityTracker.addMovement(event);

            final VelocityTracker velocityTracker = mVelocityTracker;
            velocityTracker.computeCurrentVelocity(mVelocityUnits);

            float yVelocity = velocityTracker.getYVelocity();
            float xVelocity = velocityTracker.getXVelocity();

            boolean negative = yVelocity < 0;
            if (xVelocity < 0) {
                xVelocity = -xVelocity;
            }
            if (xVelocity > mMaximumMinorVelocity) {
                xVelocity = mMaximumMinorVelocity;
            }

            float velocity = (float) Math.hypot(xVelocity, yVelocity);
            if (negative) {
                velocity = -velocity;
            }

            float top = getRelativeY();

            if (Math.abs(velocity) < mMaximumTapVelocity) {
                if ((mExpanded && top < mTapThreshold + getMinRelativeY())
                        || (!mExpanded && top > getMaxRelativeY() - mTapThreshold)) {

                    if (mAllowSingleTap) {
                        getXContext().post(new Runnable() {
                            @Override
                            public void run() {
                                getXContext().playSoundEffect(SoundEffectConstants.CLICK);

                            }
                        });

                        if (mExpanded) {
                            animateClose(top);
                        } else {
                            animateOpen(top);
                        }
                    } else {
                        performFling(top, velocity, false);
                    }

                } else {
                    performFling(top, velocity, false);
                }
            } else {
                performFling(top, velocity, false);
            }
        }
    }

    private float getMaxRelativeY() {
    	float parentHeight = getParent() == null ? this.getHeight() : getParent().getHeight();
    	float handlerHeight = mHandle == null ? 0 : mHandle.getHeight();
        return parentHeight - mTopOffset - handlerHeight;
    }

    private float getMinRelativeY() {
    	float parentHeight = getParent() == null ? this.getHeight() : getParent().getHeight();
        return parentHeight - this.getHeight();
    }

    private void animateClose(float position) {
        prepareTracking(position);
        performFling(position, mMaximumAcceleration, true);
    }

    private void animateOpen(float position) {
        prepareTracking(position);
        performFling(position, -mMaximumAcceleration, true);
    }

    private void performFling(float position, float velocity, boolean always) {
        mAnimationPosition = position;
        mAnimatedVelocity = velocity;

        Log.i(TAG, "velocity === " + velocity + "    position === " + position);

        if (mExpanded) {
            if (always
                    || (velocity > mMaximumMajorVelocity || (position > getMinRelativeY()
                            + mHandle.getHeight() && velocity > -mMaximumMajorVelocity))) {
                // We are expanded and are now going to animate away.
                mAnimatedAcceleration = mMaximumAcceleration;
                if (velocity < 0) {
                    mAnimatedVelocity = 0;
                }
            } else {
                // We are expanded, but they didn't move sufficiently to cause
                // us to retract. Animate back to the expanded position.
                mAnimatedAcceleration = -mMaximumAcceleration;
                if (velocity > 0) {
                    mAnimatedVelocity = 0;
                }
            }
        } else {
            if (!always
                    && (velocity > mMaximumMajorVelocity || (position > getMaxRelativeY()
                            + mHandle.getHeight() && velocity > -mMaximumMajorVelocity))) {
                // We are collapsed, but they didn't move sufficiently to cause
                // us to retract. Animate back to the collapsed position.
                mAnimatedAcceleration = mMaximumAcceleration;
                if (velocity < 0) {
                    mAnimatedVelocity = 0;
                }
            } else {
                // We are collapsed, and they moved enough to allow us to expand.
                mAnimatedAcceleration = -mMaximumAcceleration;
                if (velocity > 0) {
                    mAnimatedVelocity = 0;
                }
            }
        }

        long now = SystemClock.uptimeMillis();
        mAnimationLastTime = now;
        mCurrentAnimationTime = now + ANIMATION_FRAME_DURATION;
        mAnimating = true;
        mHandler.removeMessages(MSG_ANIMATE);
        Log.i(TAG, "start animate   ===== " + mCurrentAnimationTime);
        mHandler.sendMessageAtTime(mHandler.obtainMessage(MSG_ANIMATE), mCurrentAnimationTime);
        stopTracking();
    }

    private void prepareTracking(float position) {
        mTracking = true;
        mVelocityTracker = VelocityTracker.obtain();
        boolean opening = !mExpanded;
        if (opening) {
            mAnimatedAcceleration = mMaximumAcceleration;
            mAnimatedVelocity = mMaximumMajorVelocity;
            mAnimationPosition = getMaxRelativeY();
            moveHandle((int) mAnimationPosition);
//            mAnimating = true;
            mHandler.removeMessages(MSG_ANIMATE);
            long now = SystemClock.uptimeMillis();
            mAnimationLastTime = now;
            mCurrentAnimationTime = now + ANIMATION_FRAME_DURATION;
            mAnimating = true;
        } else {
            if (mAnimating) {
                mAnimating = false;
                mHandler.removeMessages(MSG_ANIMATE);
            }
            moveHandle((int) position);
        }
    }

    private void moveHandle(int position) {
    	if (getParent() == null) {
    		return;
    	}
        if (position == EXPANDED_FULL_OPEN) {
            setRelativeY(getMinRelativeY());
            getParent().invalidate();
        } else if (position == COLLAPSED_FULL_CLOSED) {
            setRelativeY(getMaxRelativeY());
            getParent().invalidate();
        } else {
            if (position < getMinRelativeY()) {
                position = (int) (getMinRelativeY() + 0.5f);
            } else if (position > getMaxRelativeY()) {
                position = (int) (getMaxRelativeY() + 0.5f);
            }

            setRelativeY(position);
            getParent().invalidate();
        }
    }

    private void prepareContent() {
        if (mAnimating) {
            return;
        }

        final DrawableItem content = mContent;
        content.setVisibility(true);
    }

    private void stopTracking() {
        mTracking = false;

        if (mOnDrawerScrollListener != null) {
            mOnDrawerScrollListener.onScrollEnded();
        }

        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private void doAnimation() {
        if (mAnimating) {
            incrementAnimation();
            if (mAnimationPosition >= getMaxRelativeY() + mTopOffset) {
                mAnimating = false;
                Log.i(TAG, "END animate   ===== " + mCurrentAnimationTime);
                closeDrawer();
            } else if (mAnimationPosition < getMinRelativeY() - mTopOffset) {
                mAnimating = false;
                Log.i(TAG, "END animate   ===== " + mCurrentAnimationTime);
                openDrawer();
            } else {
                Log.v(TAG, "doAnimation ==== v ==== " + mAnimatedVelocity);
                Log.v(TAG, "doAnimation ==== a ==== " + mAnimatedAcceleration);
                moveHandle((int) mAnimationPosition);
                mCurrentAnimationTime += ANIMATION_FRAME_DURATION;
                mHandler.sendMessageAtTime(mHandler.obtainMessage(MSG_ANIMATE),
                        mCurrentAnimationTime);
            }
        }
    }

    private void incrementAnimation() {
        long now = SystemClock.uptimeMillis();
        float t = (now - mAnimationLastTime) / 1000.0f; // ms -> s
        final float position = mAnimationPosition;
        final float v = mAnimatedVelocity; // px/s
        final float a = mAnimatedAcceleration; // px/s/s
        mAnimationPosition = position + (v * t) + (0.5f * a * t * t); // px
        mAnimatedVelocity = v + (a * t); // px/s
        mAnimationLastTime = now; // ms
    }

    void toggle() {
        if (!mExpanded) {
            openDrawer();
        } else {
            closeDrawer();
        }

        invalidate();
    }

    void animateToggle() {
        if (!mExpanded) {
            animateOpen();
        } else {
            animateClose();
        }
    }

    void open() {
        openDrawer();
        invalidate();
    }

    void close() {
        closeDrawer();
        invalidate();
    }

    void animateClose() {
        prepareContent();
        final OnDrawerScrollListener scrollListener = mOnDrawerScrollListener;
        if (scrollListener != null) {
            scrollListener.onScrollStarted();
        }

        animateClose(getRelativeY());

        if (scrollListener != null) {
            scrollListener.onScrollEnded();
        }
    }

    void animateOpen() {
        prepareContent();
        final OnDrawerScrollListener scrollListener = mOnDrawerScrollListener;
        if (scrollListener != null) {
            scrollListener.onScrollStarted();
        }

        animateOpen(getRelativeY());

        if (scrollListener != null) {
            scrollListener.onScrollEnded();
        }
    }

    private void closeDrawer() {
        moveHandle(COLLAPSED_FULL_CLOSED);
//        mContent.setVisibility(false);

        if (mHandle != null) {
            mHandle.updateDrawable(XSlidingHandle.State.UP);
        }

        if (!mExpanded) {
            return;
        }

        mExpanded = false;

        if (mOnDrawerCloseListener != null) {
            Log.v(TAG, "onDrawerClosed  ===== ");
            mOnDrawerCloseListener.onDrawerClosed();

            // save to shared pref.
            // when started handle open next time, no animation.
            setHandleWithoutAnim(SLIDING_HANDLE_OPEN_KEY);
        }

    }

    private void openDrawer() {
        moveHandle(EXPANDED_FULL_OPEN);
        mContent.setVisibility(true);

        if (mHandle != null) {
            mHandle.updateDrawable(XSlidingHandle.State.DOWN);
        }

        if (mExpanded) {
            return;
        }

        mExpanded = true;

        if (mOnDrawerOpenListener != null) {
            Log.v(TAG, "onDrawerOpened  ===== ");
            mOnDrawerOpenListener.onDrawerOpened();

            // save to shared pref.
            // when started handle closed next time, no animation.
            setHandleWithoutAnim(SLIDING_HANDLE_CLOSE_KEY);
        }
    }

    /**
     * Sets the listener that receives a notification when the drawer starts or ends
     * a scroll. A fling is considered as a scroll. A fling will also trigger a
     * drawer opened or drawer closed event.
     *
     * @param onDrawerScrollListener The listener to be notified when scrolling
     *        starts or stops.
     */
    public void setOnDrawerScrollListener(OnDrawerScrollListener onDrawerScrollListener) {
        mOnDrawerScrollListener = onDrawerScrollListener;
    }

    /**
     * Sets the listener that receives a notification when the drawer becomes open.
     *
     * @param onDrawerOpenListener The listener to be notified when the drawer is opened.
     */
    void setOnDrawerOpenListener(OnDrawerOpenListener onDrawerOpenListener) {
        mOnDrawerOpenListener = onDrawerOpenListener;
    }

    /**
     * Sets the listener that receives a notification when the drawer becomes close.
     *
     * @param onDrawerCloseListener The listener to be notified when the drawer is closed.
     */
    void setOnDrawerCloseListener(OnDrawerCloseListener onDrawerCloseListener) {
        mOnDrawerCloseListener = onDrawerCloseListener;
    }

    XDragSource getContent() {
        return mContent;
    }

    void unlock() {
        mLocked = false;
    }

    void lock() {
        mLocked = true;
    }

    boolean isOpened() {
        return mExpanded;
    }

    /**
     * Indicates whether the drawer is scrolling or flinging.
     *
     * @return True if the drawer is scroller or flinging, false otherwise.
     */
    boolean isMoving() {
        return mTracking || mAnimating;
    }

    private boolean isHandleWithAnim(String key) {
        SharedPreferences prefs = mContext.getContext().getSharedPreferences(PREFS_KEY,
                Context.MODE_PRIVATE);
        return prefs.getBoolean(key, true);
    }

    private void setHandleWithoutAnim(String key) {
        SharedPreferences prefs = mContext.getContext().getSharedPreferences(PREFS_KEY,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, false);
        editor.commit();
    }

    void animHandle(boolean isOpen) {
        String key = isOpen ? SLIDING_HANDLE_OPEN_KEY : SLIDING_HANDLE_CLOSE_KEY;
        if (mHandle != null && isHandleWithAnim(key)) {
            mHandle.animate();
        }
    }

    void updateContent() {
        if (mContent != null) {
            Log.i(TAG, "mContent.updateAllItems() #$%^#$&Q%$@#");
            mContent.updateAllItems();
        }
    }

    void setRelativeY(boolean state) {
        float relativeY = state ? getMinRelativeY() : getMaxRelativeY();
        setRelativeY(relativeY);
    }

    @Override
    public void clean() {
        super.clean();

        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }

        if (mHandle != null) {
            mHandle.destory();
            mHandle = null;
        }

        if (mContent != null) {
            mContent.destory();
            mContent = null;
        }
    }

    private class XDrawerToggler implements DrawableItem.OnClickListener {

        @Override
        public void onClick(DrawableItem item) {
            if (mLocked) {
                return;
            }
            if (mAnimateOnClick) {
                animateToggle();
            } else {
                toggle();
            }
        }

    }

    private class XSlidingHandler extends Handler {
        public void handleMessage(Message m) {
            switch (m.what) {
            case MSG_ANIMATE:
                doAnimation();
                break;
            default:
            	break;
            }
        }
    }
    
    //dooba edit
    void resetState() {
    	if (mContent != null) {
    		mContent.resetState();
    	}
    }

    void reset() {
        if (mContent != null) {
            mContent.reset();
        }
    }
    
    void addWidgetContent() {
    	if (mContent != null) {
    		mContent.addWidgetContent();
    	}
    }
    
    void onConfigurationChange() {
    	if (mContent != null) {
    		mContent.onConfigurationChange();
    	}
    }

}
