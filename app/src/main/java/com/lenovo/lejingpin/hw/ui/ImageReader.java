package com.lenovo.lejingpin.hw.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.lenovo.lejingpin.ams.AmsRequest;
import com.lenovo.lejingpin.ams.AmsSession;
import com.lenovo.lejingpin.ams.AmsSession.AmsCallback;
import com.lenovo.lejingpin.ams.GetImageRequest;
import com.lenovo.lejingpin.ams.GetImageRequest.GetImageResponse;
import com.lenovo.lejingpin.hw.utils.ColorUtil;

public class ImageReader {
	private static final String TAG = "ImageReader";
	private static List<String> mLoading;
	private static Handler mHandler;
	
	private ImageReader(){};

	static {
		mLoading = new ArrayList<String>();
		mHandler = new Handler();
	}

	public static void loadImage(final Context context, final String url, final OnImageLoadListener callback) {
		if (TextUtils.isEmpty(url)) {
			callback.onLoadComplete(null);
			return;
		}

		boolean isloading = mLoading.contains(url);
		Log.d(TAG, "loadImage >> isLoading=" + isloading + ", url=" + url);
		if (!isloading) {
			mLoading.add(url);
			AsyncTask.execute(new Runnable() {
				public void run() {
					try {
						GetImageRequest imageRequest = new GetImageRequest();
						imageRequest.setData(url);
						AmsSession.execute(context, imageRequest, new AmsCallback() {
							public void onResult(AmsRequest request, int code, final byte[] bytes) {
								Log.d(TAG, "loadImage >> onResult >> bytes=" + (bytes == null ? null : bytes.length));
								mLoading.remove(url);

								GetImageResponse i = new GetImageResponse();
								i.parseFrom(bytes);
								publishResult(callback, i.getDrawable());
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						mLoading.remove(url);
					}
				}
			});
		}
	}

	public static void loadImage(final Context context, final String url, final OnIconLoadListener callback) {
		if (TextUtils.isEmpty(url)) {
			callback.onLoadComplete(null, null);
			return;
		}

		boolean isloading = mLoading.contains(url);
		Log.d(TAG, "loadImage >> isLoading=" + isloading + ", url=" + url);
		if (!isloading) {
			mLoading.add(url);
			AsyncTask.execute(new Runnable() {
				public void run() {
					try {
						GetImageRequest imageRequest = new GetImageRequest();
						imageRequest.setData(url);
						AmsSession.execute(context, imageRequest, new AmsCallback() {
							public void onResult(AmsRequest request, int code, final byte[] bytes) {
								Log.d(TAG, "loadImage >> onResult >> bytes=" + (bytes == null ? null : bytes.length));
								mLoading.remove(url);

								GetImageResponse i = new GetImageResponse();
								i.parseFrom(bytes);
								Drawable drawable = i.getDrawable();
								if(drawable!=null){
									publishResult(callback, i.getDrawable(), getColor(i.getDrawable()));
								}else{
									Log.d(TAG, "get drawable error .");
								}
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						mLoading.remove(url);
					}
				}
			});
		}
	}

	private static void publishResult(final OnImageLoadListener callback, final Drawable drawable) {
		mHandler.post(new Runnable() {
			public void run() {
				Log.d(TAG, "publishResult >> drawable:" + drawable);
				callback.onLoadComplete(drawable);
			}
		});
	}

	private static void publishResult(final OnIconLoadListener callback, final Drawable drawable, final int[] color) {
		mHandler.post(new Runnable() {
			public void run() {
				Log.d(TAG, "publishResult >> drawable:" + drawable);
				callback.onLoadComplete(drawable, color);
			}
		});
	}

	private static int[] getColor(Drawable img) {
		return ColorUtil.getAppIconMainColor(((BitmapDrawable) img).getBitmap());
	}

	public interface OnImageLoadListener {
		void onLoadComplete(Drawable img);
	}

	public interface OnIconLoadListener {
		void onLoadComplete(Drawable img, int[] maxColor);
	}
}
