package com.lenovo.launcher2.commoninterface;

import java.util.Timer;
import java.util.TimerTask;

import com.lenovo.launcher2.customizer.Debug;
import com.lenovo.launcher2.customizer.Debug.R2;
import com.lenovo.launcher2.customizer.WaitableTask;

/**
 * waitable task
 * */

public class ReliableWaitingThread extends Thread {

	// private int mPriority = Thread.NORM_PRIORITY - 1;
	private WaitableTask mTask = null;
	private int cycleLimit = 0;
	
	private Timer timer = null;
	private TimerTask timerTask = null;

	public ReliableWaitingThread(String name, WaitableTask taskToWait,
			int priority) {
		super(name);
		mTask = taskToWait;
		// mPriority = priority;
		cycleLimit = (int) (mTask.mLimitTime / mTask.mClickTime);
		setPriority(priority);
	}
	
	private Object mLock = new Object();

	public void run() {

		super.run();

		mTask.onPreparing();
		mTask.onTaskStarted();

		while (mTask != null && mTask.isTaskProcessing) {
			R2.echo("Waiting for task mTask.isTaskProcessing= " + mTask.isTaskProcessing);
			timer = new Timer();
			timerTask = new TimerTask() {

				@Override
				public void run() {
					try {
//						android.util.Log.i( "WOW", "in mLock. notify all" );
						interrupt();
					} catch (Exception e) {
					}
				}
			};

			timer.schedule(timerTask, 1500L);

			synchronized (mLock) {
				try {
//					android.util.Log.i( "WOW", "in mLock. wait" );
					mLock.wait();
				} catch (InterruptedException e) {
				}
			}

			try {

				if (Debug.MAIN_DEBUG_SWITCH)
					R2.echo("Waiting for task : " + mTask.getName());

				mTask.onCycleCheck();
				if (Debug.MAIN_DEBUG_SWITCH)
					R2.echo("Touch 5 " + mTask.mClickTime);

			} catch (Throwable e) {
				e.printStackTrace();
				mTask.isTaskProcessOK = false;
			}
			
//			android.util.Log.i( "WOW", "SOSOSOSO" );
		}
		

		if (Debug.MAIN_DEBUG_SWITCH)
			R2.echo("Touch external ");

		if (mTask.isTaskProcessOK) {
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("Write success -****************************!" + getName());
			mTask.onTaskSucceed();
		} else {
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("Write Unsuccess ****************************!" + getName());
			mTask.onTaskFailed();
		}

		mTask.onTaskFinished();
	}

}