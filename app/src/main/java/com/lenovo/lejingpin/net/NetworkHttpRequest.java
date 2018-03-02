package com.lenovo.lejingpin.net;

import java.util.Arrays;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;

import com.lenovo.lejingpin.ams.AmsRequest;
import com.lenovo.lejingpin.ams.RegistClientInfoRequest.RegistClientInfoResponse;
import com.lenovo.lsf.account.PsAuthenServiceL;

/**
 * HTTP request toolkit class
 * 
 */
public class NetworkHttpRequest {
	private static final String TAG = "HawaiiLog";

	public class HttpReturn {
		public int code;
		public byte[] bytes;

		public HttpReturn() {
			code = -1;
			bytes = new byte[0];
		}
	}

	private static final int DEFAULTTIMEOUT = 20000;

	public HttpReturn executeHttpGet(Context context, String contentType,String url,
			boolean visiable) {
		HttpReturn ret = new HttpReturn();
		long startTime = System.currentTimeMillis();
		try {
			Log.i(TAG, "NetworkHttpRequest >> executeHttpGet >> url : "+url);
			Log.i(TAG, "Request start time:" + startTime);
			HttpParams params = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(params, DEFAULTTIMEOUT);
			HttpConnectionParams.setSoTimeout(params, DEFAULTTIMEOUT);
			HttpClientParams.setRedirecting(params, true);
			DefaultHttpClient httpClient = new DefaultHttpClient(params);
			HttpGet httpget = new HttpGet(url);
			if (RegistClientInfoResponse.getClientId() != null){
				httpget.addHeader(
						"Cookie",
						"lpsust="
								+ PsAuthenServiceL.getStData(context,
										AmsRequest.RID, visiable)
								+ ";clientid="
								+ RegistClientInfoResponse.getClientId());
			}
			if(contentType!=null){
				httpget.addHeader("Accept-Encoding", contentType);
			}
			HttpResponse response = httpClient.execute(httpget);
			int code = response.getStatusLine().getStatusCode();
			byte[] bytes = EntityUtils.toByteArray(response.getEntity());
			httpClient.getConnectionManager().shutdown();

			Log.i(TAG, "ResponseCode: " + code);
			Log.i(TAG, "Responsebody: " + Arrays.toString(bytes));

			ret.code = code;
			ret.bytes = bytes;
		} catch (Exception e) {
			Log.i(TAG, "http get exception, "+e.toString());
		}
		long endTime = System.currentTimeMillis();
		Log.i(TAG, "Request end time:" + endTime);
		Log.i(TAG, "Request time consuming = " + (endTime - startTime)/1000+"sec");
		return ret;
	}

	public HttpReturn executeHttpPost(Context context, String url, String post,
			int requestFrom, boolean visiable) {
		HttpReturn ret = new HttpReturn();
		long startTime = System.currentTimeMillis();
		try {
			Log.i(TAG, "NetworkHttpRequest >> executeHttpPost >> url : "+url);
			Log.i(TAG, "NetworkHttpRequest >> executeHttpPost >> post : "+post);
			Log.i(TAG, "Request start time:" + startTime);
			HttpParams params = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(params, DEFAULTTIMEOUT);
			HttpConnectionParams.setSoTimeout(params, DEFAULTTIMEOUT);
			HttpClientParams.setRedirecting(params, true);

			DefaultHttpClient httpClient = new DefaultHttpClient(params);
			HttpPost httppost = new HttpPost(url);
			httppost.addHeader("Content-Type",
					"application/x-www-form-urlencoded");
			if (requestFrom == 1) {
				httppost.addHeader(
						"Cookie",
						"lpsust="
								+ PsAuthenServiceL.getStData(context,
										AmsRequest.RID, visiable)
								+ ";clientid="
								+ RegistClientInfoResponse.getClientId());
			} else {
				/*httppost.addHeader(
						"Cookie",
						"lpsust="
								+ PsAuthenServiceL.getStData(context,
										SnsRequest.RID, visiable)
								+ ";clientid="
								+ SnsRegistClientInfoResponse.getClientId());*/
			}
			StringEntity reqEntity = new StringEntity(post, HTTP.UTF_8);
			httppost.setEntity(reqEntity);

			HttpResponse response = httpClient.execute(httppost);
			int code = response.getStatusLine().getStatusCode();
			byte[] bytes = EntityUtils.toByteArray(response.getEntity());
			httpClient.getConnectionManager().shutdown();

			Log.i(TAG, "ResponseCode: " + code);
			Log.i(TAG, "Responsebody: " + Arrays.toString(bytes));

			ret.code = code;
			ret.bytes = bytes;
		} catch (Exception e) {
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		Log.i(TAG, "Request end time:" + endTime);
		Log.i(TAG, "Request time consuming = " + (endTime - startTime));
		return ret;
	}
}
