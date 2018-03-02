package com.lenovo.launcher2.customizer;
import com.lenovo.launcher2.customizer.Debug.R2;

public abstract class WaitableTask extends Thread {
	public boolean isTaskProcessOK = false;
	public boolean isTaskProcessing = false;
	public long mClickTime = 3000L;
	public long mLimitTime = 3 * 60 * 1000L;

	public void onTaskStarted() {
		isTaskProcessOK = false;
		isTaskProcessing = true;
	};

	public void onTaskFinished() {
		isTaskProcessing = false;
	};

	public void onTaskFailed() {
		isTaskProcessOK = false;
	};

	public void onTaskSucceed() {
		isTaskProcessOK = true;
	};

	public void onCycleCheck() {

	}

	public void onPreparing() {
		if (Debug.MAIN_DEBUG_SWITCH)
			R2.echo("START WaitableThread+++++++++++++++++++++++++++++++++++++++. "
					+ getName());
		this.start();
	}

	public WaitableTask(String name) {
		super(name);
	}
}