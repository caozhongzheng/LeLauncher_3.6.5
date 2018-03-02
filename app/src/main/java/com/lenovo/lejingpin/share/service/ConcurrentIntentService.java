package com.lenovo.lejingpin.share.service;

import java.util.concurrent.ConcurrentHashMap;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

public abstract class ConcurrentIntentService extends Service {
	protected abstract void onHandleIntent(Intent intent);

	private boolean mbRedelivery;
	private ConcurrentHashMap<Intent, MyAsyncTask> mmapIntent2AsyncTask;

	public ConcurrentIntentService() {
		mmapIntent2AsyncTask = new ConcurrentHashMap<Intent, MyAsyncTask>(32);
	}

	public void setIntentRedelivery(boolean enabled) {
		mbRedelivery = enabled;
	}

	public void cancel() {
		for (MyAsyncTask task : mmapIntent2AsyncTask.values()) {
			task.cancel(true);
		}
		mmapIntent2AsyncTask.clear();
		stopSelf();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		if (mmapIntent2AsyncTask.containsKey(intent)) {
			return;
		}
		MyAsyncTask task = new MyAsyncTask();
		mmapIntent2AsyncTask.put(intent, task);
		task.execute(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		onStart(intent, startId);
		return mbRedelivery ? START_REDELIVER_INTENT : START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private class MyAsyncTask extends AsyncTask<Intent, Void, Void> {

		@Override
		protected Void doInBackground(Intent... its) {
			final int nCount = its.length;
			for (int i = 0; i < nCount; i++) {
				Intent it = its[i];
				mmapIntent2AsyncTask.remove(it);
				onHandleIntent(it);
			}
			return null;
		}

		@Override
		protected void onCancelled() {
			if (mmapIntent2AsyncTask.isEmpty())
				stopSelf();
		}

		@Override
		protected void onPostExecute(Void result) {
			if (mmapIntent2AsyncTask.isEmpty())
				stopSelf();
		}
	}

}
