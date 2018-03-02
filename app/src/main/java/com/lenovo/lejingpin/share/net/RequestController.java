package com.lenovo.lejingpin.share.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;
import android.os.Process;

import com.lenovo.lejingpin.share.ams.AmsNetworkHandler;
import com.lenovo.lejingpin.share.net.RequestManager.ILeHttpCallback;

public class RequestController implements Runnable {
	private static RequestController sInstance = null;
	private final BlockingQueue<HttpRequest> mRequests = new LinkedBlockingQueue<HttpRequest>();
	// private final Thread mThread;
	private final Context mContext;
	private boolean mThreadEndFlag = true;

	private ExecutorService executorService = Executors.newFixedThreadPool(5);

	protected RequestController(Context _context) {
		mContext = _context;
		// mThread = new Thread(this);
		// mThread.start();
	}

	public synchronized static RequestController getInstance(Context _context) {
		if (sInstance == null) {
			sInstance = new RequestController(_context);
		}
		return sInstance;
	}

	public void run() {
		Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
		while (true) {
			HttpRequest request;
			try {
				mThreadEndFlag = true;
				request = mRequests.take();
			} catch (InterruptedException e) {
				continue;
			}
			request.runnable.run();
			// while (mThreadEndFlag) {
			// }
		}
	}

	public void addRequestToQueue(final String url,
			final ILeHttpCallback callback) {
		executorService.submit(new Runnable() {
			public void run() {
				AmsNetworkHandler.executeHttpGet(mContext, url,
						new ILeHttpCallback() {
							public void onReturn(int code, byte[] bytes) {
								mThreadEndFlag = false;
								if (code == 200) {
									callback.onReturn(code, bytes);
									CacheManager.writeCacheData(mContext, url,
											bytes);
								} else {
									callback.onReturn(code, null);
								}
							}
						});
			}
		});
	}

	public void addRequestToQueue(final String url, final String postdata,
			final ILeHttpCallback callback) {
		put(url, postdata, new Runnable() {
			public void run() {
				String fileName = URLEncoder.encode(url);
				// String fileName = CacheManager.readCacheData(mContext, url,
				// postdata);
				File file = new File(fileName);
				if (file.exists()) {
					// if (fileName != null) {
					FileInputStream fis;
					try {
						fis = new FileInputStream(new File(fileName));
						byte[] bytes = new byte[fis.available()];
						fis.read(bytes);
						mThreadEndFlag = false;
						callback.onReturn(-1, bytes);
					} catch (FileNotFoundException e) {
						mThreadEndFlag = false;
						callback.onReturn(-1, null);
						e.printStackTrace();
					} catch (IOException e) {
						mThreadEndFlag = false;
						callback.onReturn(-1, null);
						e.printStackTrace();
					}
				} else {
					AmsNetworkHandler.executeHttpPost(mContext, url, postdata,
							new ILeHttpCallback() {
								public void onReturn(int code, byte[] bytes) {
									if (code == 200) {
										CacheManager.writeCacheData(mContext,
												url, bytes, postdata);
									}
									mThreadEndFlag = false;
									if (code == 200) {
										callback.onReturn(code, bytes);
									} else {
										callback.onReturn(code, null);
									}
								}
							});
				}
			}
		});
	}

	private void put(String url, Runnable runnable) {
		try {
			HttpRequest request = new HttpRequest();
			request.runnable = runnable;
			request.url = url;
			mRequests.add(request);
		} catch (IllegalStateException ie) {
			throw new Error(ie);
		}
	}

	private void put(String url, String postdata, Runnable runnable) {
		try {
			HttpRequest request = new HttpRequest();
			request.runnable = runnable;
			request.postData = postdata;
			request.url = url;
			mRequests.add(request);
		} catch (IllegalStateException ie) {
			throw new Error(ie);
		}
	}

	private static class HttpRequest {
		public Runnable runnable;
		public String url;
		public String postData;
	}
}
