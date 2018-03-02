package com.lenovo.lejingpin.share.net;

import android.content.Context;

public class RequestManager {
	private static RequestController controller;

	private RequestManager(){
		
	}
	public interface ILeHttpCallback {
		void onReturn(int code, byte[] data);
	};

	public static void executeHttpGet(Context context, String url,
			ILeHttpCallback callback) {
		byte[] bytes = CacheManager.readCacheData(url);
		if (bytes != null && bytes.length > 0) {
			callback.onReturn(200, bytes);
		} else {
			// Log.i("zdx","**********************Download image from server*******");
			controller = RequestController.getInstance(context);
			controller.addRequestToQueue(url, callback);
		}
	}

	public static void executeHttpPost(Context context, String url,
			String postdata, ILeHttpCallback callback) {
		/*
		 * String fileName = URLEncoder.encode(url); //String fileName =
		 * CacheManager.readCacheData(context, url, postdata); File file = new
		 * File(fileName); if (file.exists()) { //if (fileName != null) {
		 * FileInputStream fis = null; try { fis = new FileInputStream(new
		 * File(fileName)); byte[] bytes = new byte[fis.available()];
		 * fis.read(bytes); callback.onReturn(-1, bytes); } catch
		 * (FileNotFoundException e) { callback.onReturn(-1, null);
		 * e.printStackTrace(); } catch (IOException e) { callback.onReturn(-1,
		 * null); e.printStackTrace(); }finally{ try { if(fis!=null)
		 * fis.close(); } catch (IOException e) { // TODO Auto-generated catch
		 * block e.printStackTrace(); } } } else {
		 */
		controller = RequestController.getInstance(context);
		controller.addRequestToQueue(url, postdata, callback);
		// }
	}
}
