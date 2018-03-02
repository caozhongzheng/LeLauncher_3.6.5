package com.lenovo.lejingpin.share.net;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.lenovo.lejingpin.share.net.CacheProvider.CacheFiles;

public class CacheUtils {
	private final static String TAG = "CacheUtils";
	
	private CacheUtils(){
		
	}

	public static void insert(Context context, String url, String fileName,
			String time, String post) {
		ContentValues values = new ContentValues();
		values.put(CacheFiles.URL, url);
		values.put(CacheFiles.FILENAME, fileName);
		values.put(CacheFiles.LASTACCESSTIME, time);
		values.put(CacheFiles.POSTDATA, post);
		ContentResolver resolver = context.getContentResolver();
		try {
			resolver.insert(CacheFiles.CONTENT_URI, values);
		} catch (Exception e) {
			Log.d(TAG, "Has exception when insert values to cache db ", e);
		}
	}

	public static void delete(Context context, String url, String postdata) {
		String where = "(" + CacheFiles.URL + "='" + url + "' and "
				+ CacheFiles.POSTDATA + "='" + postdata + "')";
		ContentResolver resolver = context.getContentResolver();
		resolver.delete(CacheFiles.CONTENT_URI, where, null);
	}

	public static void update(Context context, String time, String url,
			String postdata) {
		ContentValues values = new ContentValues();
		values.put(CacheFiles.LASTACCESSTIME, time);
		String where = "(" + CacheFiles.URL + "='" + url + "' and "
				+ CacheFiles.POSTDATA + "='" + postdata + "')";
		ContentResolver resolver = context.getContentResolver();
		resolver.update(CacheFiles.CONTENT_URI, values, where, null);
	}

	public static Cursor query(Context context, String url, String postdata) {
		ContentResolver resolver = context.getContentResolver();
		return resolver.query(CacheFiles.CONTENT_URI, null, CacheFiles.URL
				+ "=? and " + CacheFiles.POSTDATA + "=?", new String[] { url,
				postdata }, null);
	}

	public static Cursor query(Context context) {
		ContentResolver resolver = context.getContentResolver();
		return resolver.query(CacheFiles.CONTENT_URI, null, null, null, null);
	}

}
