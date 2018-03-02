package com.lenovo.lejingpin.share.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.UUID;

import com.lenovo.lejingpin.share.net.CacheProvider.CacheFiles;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

public class CacheManager {
	// private static final String CACHE_DIR = "/.LeDownload/";
	// private static final String CACHE_DIR2 = "/.LeDownload";
	private static final String CACHE_DIR = "/.IdeaDesktop/LeDownload/";
	private static final String CACHE_DIR2 = "/.IdeaDesktop/LeDownload";
	
	private CacheManager(){
		
	}

	public static String readCacheData(Context context, String url) {
		Cursor cursor = CacheUtils.query(context, url, "");
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				String fileName = cursor.getString(cursor
						.getColumnIndex(CacheFiles.FILENAME));
				cursor.close();
				long time = System.currentTimeMillis();
				CacheUtils.update(context, time + "", url, "");
				return fileName;
			}
			cursor.close();
		}
		return null;
	};

	public static String readCacheData(Context context, String url, String post) {
		Cursor cursor = CacheUtils.query(context, url, post);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				String fileName = cursor.getString(cursor
						.getColumnIndex(CacheFiles.FILENAME));
				cursor.close();
				long time = System.currentTimeMillis();
				CacheUtils.update(context, time + "", url, post);
				return fileName;
			}
			cursor.close();
		}
		return null;
	};

	public static byte[] readCacheData(String url) {
		if (url != null
				&& url.length() > 0
				&& Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
			FileInputStream fis = null;
			try {
				String fileName = Environment.getExternalStorageDirectory()
						+ CACHE_DIR + URLEncoder.encode(url);
				File file = new File(fileName);
				if (file.exists()) {
					// Log.i("zdx",
					// "Not download from server, read from cache----fileName="
					// + fileName);
					fis = new FileInputStream(file);
					byte[] bytes = new byte[fis.available()];
					fis.read(bytes);
					return bytes;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		// Log.i("zdx","++++++++++++++++++++++++++readCacheData is null, url:"+
		// url);
		return null;
	};

	public static Boolean writeCacheData(Context context, String url,
			byte[] bytes) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			try {
				String dirName = Environment.getExternalStorageDirectory()
						+ CACHE_DIR;
				String cacheFileName = dirName + URLEncoder.encode(url);
				File dir = new File(dirName);
				if (!dir.exists()) {
					dir.mkdir();
				}
				File cacheFile = new File(cacheFileName);
				if (!cacheFile.exists()) {
					Log.d("CacheManager", "write to cache, fileName="
							+ cacheFileName);
					cacheFile.createNewFile();
					dir.createNewFile();
					FileOutputStream fos = new FileOutputStream(cacheFileName);
					fos.write(bytes);
					fos.close();
					// CacheUtils.insert(context, url, cacheFileName, time + "",
					// "");
					return true;
				}
			} catch (Exception e) {
				Log.d("CacheManager", "write to cache failed :" + e.toString());
			}
		} else {
			Log.d("CacheManager", "write to cache failed, cdcard not exist.");
		}
		return false;
	};

	public static Boolean writeCacheData(Context context, String url,
			byte[] bytes, String post) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			try {
				// UUID uuid = UUID.randomUUID();
				String fileName = Environment.getExternalStorageDirectory()
						+ CACHE_DIR2;
				String cacheFileName = Environment
						.getExternalStorageDirectory()
						+ CACHE_DIR
						+ URLEncoder.encode(url);
				// + uuid.toString().replaceAll("-", "");
				File file = new File(fileName);
				if (!file.exists()) {
					file.mkdir();
				}
				File cacheFile = new File(cacheFileName);
				cacheFile.createNewFile();
				file.createNewFile();
				FileOutputStream fos = new FileOutputStream(cacheFileName);
				fos.write(bytes);
				fos.close();
				// long time = System.currentTimeMillis();
				// CacheUtils.insert(context, url, cacheFileName, time + "",
				// post);
				return true;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	};

	public static Boolean removeIconCacheData(String url) {
		if (url != null
				&& url.length() > 0
				&& Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
			try {
				String fileName = Environment.getExternalStorageDirectory()
						+ CACHE_DIR + URLEncoder.encode(url);
				File file = new File(fileName);
				if (file.exists()) {
					Log.i("zdx",
							"CacheManager.removeIconCacheData, remove icon from cache >> file : "
									+ fileName);
					file.delete();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
