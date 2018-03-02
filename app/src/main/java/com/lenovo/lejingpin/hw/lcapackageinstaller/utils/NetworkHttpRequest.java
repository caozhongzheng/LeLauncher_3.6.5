package com.lenovo.lejingpin.hw.lcapackageinstaller.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

/**
 * HTTP request toolkit class
 * 
 */
public class NetworkHttpRequest{
	private static final String TAG = "LcaPackageInstaller";
	
	public class HttpReturn {
		public int code;
		public String body;
		
		public HttpReturn(){
			code = -1;
			body = "";
		}
	}
	
	private static final int DEFAULTTIMEOUT = 10000;
	
	public HttpReturn executeHttpGet(Context context, String url){
		HttpReturn ret = new HttpReturn();
	
		Log.i(TAG, "URL: " + url);
		
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE); 
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LcaPackageInstaller");
		wl.acquire(); 
		
		try {
			HttpParams params = new BasicHttpParams(); 
			HttpConnectionParams.setConnectionTimeout(params, DEFAULTTIMEOUT);   
			HttpConnectionParams.setSoTimeout(params, DEFAULTTIMEOUT);
			HttpClientParams.setRedirecting(params, true); 
			
			DefaultHttpClient httpClient = new DefaultHttpClient(params);
			HttpGet httpget = new HttpGet(url);
			httpget.addHeader("Cookie", "lpsust=" + NetworkHandler.st
					+ ";clientid=" + NetworkHandler.clientId);
			HttpResponse response = httpClient.execute(httpget);
			int code = response.getStatusLine().getStatusCode();
			String body = EntityUtils.toString(response.getEntity());
			httpClient.getConnectionManager().shutdown();
			
			Log.i(TAG, "ResponseCode: "+code);
			Log.i(TAG, "Responsebody: "+body);
			
			ret.code = code;
			ret.body = body;
		} catch (Exception e) {
			e.printStackTrace();
		}	

		wl.release(); 
		
		return ret;
	}
	
	public HttpReturn executeHttpPost(Context context, String url, String post){
		HttpReturn ret = new HttpReturn();

		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE); 
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LcaPackageInstaller");
		wl.acquire(); 
		
		try {
			HttpParams params = new BasicHttpParams(); 
			HttpConnectionParams.setConnectionTimeout(params, DEFAULTTIMEOUT);   
			HttpConnectionParams.setSoTimeout(params, DEFAULTTIMEOUT);
			HttpClientParams.setRedirecting(params, true); 
			
			DefaultHttpClient httpClient = new DefaultHttpClient(params);
			HttpPost httppost = new HttpPost(url);
			httppost.addHeader("Cookie", "lpsust=" + NetworkHandler.st
					+ ";ClientId=" + NetworkHandler.clientId);
			httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");
			StringEntity reqEntity = new StringEntity(post);
			httppost.setEntity(reqEntity);
	        
			HttpResponse response = httpClient.execute(httppost);
			int code = response.getStatusLine().getStatusCode();
			String body = EntityUtils.toString(response.getEntity());
			httpClient.getConnectionManager().shutdown();

			Log.i(TAG, "ResponseCode: "+code);
			Log.i(TAG, "Responsebody: "+body);
			
			ret.code = code;
			ret.body = body;
		}catch (Exception e) {
			e.printStackTrace();
		}	

		wl.release(); 
		
		return ret;
	}

}
