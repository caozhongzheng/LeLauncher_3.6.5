package com.lenovo.launcher.components.XAllAppFace.slimengine;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Pattern;

import com.lenovo.launcher2.customizer.Debug.R5;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManagerImpl;

public class XContentMate {

	private String mName = "NULL";

	private View mTarget;
	private ExchangeManager exchangee;

	ConcurrentLinkedQueue<Runnable> runnableList;

	EventHandler eventWorker = null;

	boolean animatorJustUpdated = false;

	boolean sRenderHasStarted = false;

	private byte sState = RenderState.RUNNING;

	public static PaintFlagsDrawFilter paintFilter = new PaintFlagsDrawFilter(
			0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
	public static PorterDuffXfermode duffXfermode = new PorterDuffXfermode(
			PorterDuff.Mode.CLEAR);

	IDisplayProcess mDisProc = null;

	private boolean mNeedDattach;

	public static boolean mBuildVersionUp403 = isUp403();
    private boolean mSeniorDevice;
    private static final String[] MODEL_SENIOR = { "K900", "SCH-N719", };
	
	private static boolean isUp403() {
		// Private Class to display only CPU devices in the directory listing
		class CpuFilter implements FileFilter {
			@Override
			public boolean accept(File pathname) {
				// Check if filename is "cpu", followed by a single digit number
				if (Pattern.matches("cpu[0-9]", pathname.getName())) {
					return true;
				}
				return false;
			}
		}
		int coreCount = 1;
		try {
			// Get directory containing CPU info
			File dir = new File("/sys/devices/system/cpu/");
			// Filter to only list the devices we care about
			File[] files = dir.listFiles(new CpuFilter());
			// Return the number of cores (virtual CPU devices)
			coreCount = files.length;
		} catch (Exception e) {
			// Default to return 1 core
			coreCount = 1;
		}
		if (coreCount == 1) {
			return false;
		} else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
			return true;
		} else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
			if (Build.VERSION.RELEASE.compareToIgnoreCase("4.0.3") > 0
					|| (coreCount > 1)) {
				return true;
			}
		}
		return false;
	}

	public final class RenderState {
		public static final byte RUNNING = 1;
		public static final byte PAUSED = 2;
	}

	private boolean mDataReady = false;

	public final class RenderMode {
		public static final byte MODE_PERSIST = 0;
		public static final byte MODE_ONCE = 1;
	}

	public void setRenderTarget(final View targetHolder) {
		if (mTarget != null) {
			trimLocalMemory();
			mTarget = null;
		}

		if (targetHolder != null) {
			mTarget = targetHolder;
			invalidate();
		}
	}

	public XContentMate(String name) {
        String model = Build.MODEL;
        android.util.Log.i("Test", "device model ==" + model);

        mSeniorDevice = false;
        for (int i = 0; i < MODEL_SENIOR.length; i++) {
            if (model.contains(MODEL_SENIOR[i])) {
                mSeniorDevice = true;
                break;
            }
        }
		mName = name;

		init();
	}

	public boolean isDataReady() {
		return this.mDataReady;
	}

	public synchronized void start() {

		if (sRenderHasStarted) {
			return;
		} else {
			run();
			sRenderHasStarted = true;
		}
	}

	private void init() {

		mNeedDattach = false;

		runnableList = new ConcurrentLinkedQueue<Runnable>();

		eventWorker = new EventHandler("EventHandler : " + mName);

	}

	public void setExchangee(ExchangeManager exchangeManager) {
		this.exchangee = exchangeManager;
	}

	public boolean isPaused() {
		return sState == RenderState.PAUSED;
	}

	public void clearAndDettach() {
	    R5.echo("clearAndDettach");

		// renderer
		mNeedDattach = true;
		
		invalidate();

		// reset state
		sRenderHasStarted = false;
		
		mTarget = null;
		exchangee = null;
		runnableList.clear();
	}

	public void invalidate() {		
		if (mTarget != null) {
			((XContextContent) mTarget).requestInvalidate();
		}
	}
	
	public void invalidateAtOnce() {
		if (mTarget != null) {
			((XContextContent) mTarget).invalidate();
		}
	}

	private static final int FPS_LIMIT = 60;
	private static final long FRAME_INTERNAL = 1000L / FPS_LIMIT;
	private long lastRequestTime = 0l;

	// long[] aaa = new long[5];
	// int index = 0;
	// long temp = 0;
	private long gap = 0;

	public long getFraction() {
		return gap;
	}

	private void saveFPS() {
		long currentTime = SystemClock.uptimeMillis();
		gap = currentTime - lastRequestTime + 1;
		// if (index == aaa.length) {
		// temp = 0;
		// for (int i = 0; i < aaa.length; i++) {
		// temp += aaa[i];
		// }
		// temp = temp / 5;
		// if (temp <= 0) {
		// temp = 1;
		// }
		// // android.util.Log.i("V", currentTime + "  , " + gap + " , "
		// // + FRAME_INTERNAL + " , " + (FRAME_INTERNAL - gap));
		// Log.i("fps", "FPS: " + (1000 / gap));
		//
		// index = 0;
		// }
		//
		// aaa[index] = gap;
		// index++;

		// if (gap < FRAME_INTERNAL) {
		// try {
		// Thread.sleep(FRAME_INTERNAL - gap);
		// } catch (InterruptedException e) {
		// }
		// }

		lastRequestTime = currentTime;
	}

	public void postRunnableOnUi(Runnable runnable) {
		runnableList.add(runnable);
		invalidate();
	}

	public boolean removeRunnable(Runnable runnable) {
		return runnableList.remove(runnable);
	}

	public void injectAnimation(ValueAnimator animation, long delay) {
		if(animation == null){
			return;
		}
		
		animation.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				invalidate();
			}

		});

		animation.addListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {

				invalidate();
			}

			@Override
			public void onAnimationCancel(Animator animation) {
			}
		});
		animation.start();
		return;
	}

	public void injectAnimation(ValueAnimator animation, boolean nice) {
		injectAnimation( animation, 0 );
	}

	public void ejectAnimation(ValueAnimator animation) {
		if(animation != null){
			animation.cancel();
		}
	}

	public void handleMotionEvent() {
		MotionEvent[] eventQ = exchangee.getEventQueque();
		if (eventQ != null) {
			int i, j;
			i = j = exchangee.getEventPointer() + 1;
			int len = eventQ.length;
			for (; i < len; i++) {
				if (eventQ[i] != null) {
					exchangee.onTouchEvent(eventQ[i]);
					eventQ[i] = null;
				}
			}

			if (i >= len) {
				for (i = 0; i < j; i++) {
					if (eventQ[i] != null) {
						exchangee.onTouchEvent(eventQ[i]);
						eventQ[i] = null;
					}
				}
			}
		}
	}

	public ExchangeManager getExchangee() {
		return exchangee;
	}

	public void run() {
		if (eventWorker != null) {
			eventWorker.start();
		}
	}

	final class EventHandler extends HandlerThread {

		private Handler mHandler = null;
		private Handler mHandler2 = null;
		boolean mRunning = true;
		boolean mNeedRepeat = false;
		private Object mEventLock = new Object();
		private static final int MSG_WAIT = 0;
		private static final int MSG_HANDLE_EVENT = 1;

		public void wakeupEventHandler() {/*
			if (mRunning) {
				return;
			}
			synchronized (mEventLock) {
				mEventLock.notify();
				mRunning = true;
			}
		*/}

		public void waitForSignal() {/*
			if (!mRunning) {
				return;
			}
			synchronized (mEventLock) {
				try {
					mRunning = false;
					mEventLock.wait();
				} catch (InterruptedException e) {
				}
			}
		*/}

		public EventHandler(String name) {
			super(name);
		}

//		public boolean mNeedWait = false;

		void handleEvent() {/*
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					mNeedRepeat = false;
					handleMotionEvent();

					//if (!mBuildVersionUp403) {
					if (!mSeniorDevice) {
						SystemClock.sleep(20L);
					}

					if (mNeedDattach) {
						Looper.myLooper().quit();
						return;
					}

					if (!mNeedRepeat) {
						if (!mHandler.hasMessages(MSG_WAIT)) {
							mHandler.sendEmptyMessageDelayed(MSG_WAIT, 1000L);
						}
					} else {
						mHandler.removeMessages(MSG_WAIT);
					}

					if (mNeedWait) {
						mNeedWait = false;
						waitForSignal();
					}

					handleEvent();
				}
			});
		*/}

		public void run() {
			android.os.Process
					.setThreadPriority(Process.THREAD_PRIORITY_DISPLAY);
			Looper.prepare();
//			mHandler = new Handler(Looper.myLooper()) {
//				@Override
//				public void handleMessage(Message msg) {
//					super.handleMessage(msg);
//					if (msg.what == MSG_WAIT) {
//						if (!mNeedRepeat) {
//							mNeedWait = true;
//						}
//					} else {
//						handleEvent();
//					}
//				}
//			};
//			mHandler.sendEmptyMessage(MSG_HANDLE_EVENT);
			mHandler = new Handler(Looper.myLooper()){
				
			};
			mHandler2 = new Handler(Looper.myLooper()){
				
			};
			Looper.loop();

		}
	}

	/**
	 * ValueAnimator running thread.
	 * */
	private final class AnimationThread extends HandlerThread {

		private ConcurrentLinkedQueue<ValueAnimator> runningAnimList = new ConcurrentLinkedQueue<ValueAnimator>();

		private static final int MSG_CANCEL_ANIMATION = 0;
		private static final int MSG_LOOK_THROUGH = 1;
		private static final int MSG_QUIT_HANDER = 2;

		private Handler mExchangeeHandler = null;

		private boolean isActive = false;

		// public int runningAnimatorCount = 0;

		public AnimationThread(String name) {
			super(name);
		}

	}

	public void trimLocalMemory() {
		WindowManagerImpl mImpl = null;
		try {
			Method method = WindowManagerImpl.class.getMethod("getDefault",
					null);
			mImpl = (WindowManagerImpl) method.invoke(WindowManagerImpl.class,
					null);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		if (mImpl != null) {
			mImpl.trimLocalMemory();
		} else {
			try {
				Class c;
				c = Class.forName("android.view.WindowManagerGlobal");
				Method getInstanceMethod = c.getMethod("getInstance", null);
				Object mImpl4_2 = getInstanceMethod.invoke(c, null);
				Method trimMethod = mImpl4_2.getClass().getMethod(
						"trimLocalMemory", null);
				trimMethod.invoke(mImpl4_2, null);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	public Handler getEventHandler() {
//		return eventWorker.mHandler;
		return ((XContextContent) mTarget).getExtraHandler();
	}
	
	public Handler getEventHandler2(){
		return eventWorker.mHandler2;
	}
}
