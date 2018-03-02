package com.lenovo.lejingpin.network;

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

/**
 * HTTP request toolkit class
 * 
 */
public class NetworkHttpRequest {
	private static final String TAG = "NetworkHttpRequest";
        public interface AmsCallback {
                public void onResult( int code, byte[] bytes);
        }

	public class HttpReturn {
		public int code;
		public byte[] bytes;

		public HttpReturn() {
			code = -1;
			bytes = new byte[0];
		}
	}

/*
         public String getUrl() {
            StringBuffer url = new StringBuffer(HOST_WALLPAPER);

                 url.append("?method=a")
                .append("&s=").append(mStartIndex).append("&t=").append(mCount)
                .append("&f=id&a=asc")
                url.append("&time=").append(new Date().getTime());
                Log.i("zdx", "WallpaperinfoRequest, url=" + url.toString());
                return url.toString();
        }

*/
	private static final int DEFAULTTIMEOUT = 20000;

	public HttpReturn executeHttpGet(Context context, String url,final AmsCallback callback){
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
                        //httpget.addHeader("Accept-Encoding","gzip");

/*
			if (RegistClientInfoResponse.getClientId() != null) {
				httpget.addHeader(
						"Cookie",
						"lpsust="
								+ PsAuthenServiceL.getStData(context,
										AmsRequest.RID, visiable)
								+ ";clientid="
								+ RegistClientInfoResponse.getClientId());
			}
*/
			HttpResponse response = httpClient.execute(httpget);
			int code = response.getStatusLine().getStatusCode();
			byte[] bytes = EntityUtils.toByteArray(response.getEntity());
			httpClient.getConnectionManager().shutdown();

			Log.i(TAG, "ResponseCode: " + code);

			ret.code = code;
			ret.bytes = bytes;
		} catch (Exception e) {
			Log.i(TAG, "http get exception, "+e.toString());
		}
                callback.onResult( ret.code,ret.bytes);
		long endTime = System.currentTimeMillis();
		Log.i(TAG, "Request end time:" + endTime);
		Log.i(TAG, "Request time consuming = " + (endTime - startTime)/1000+"sec");
		return ret;
	}
         //modify for ams5.0
        public HttpReturn executeHttpGet(Context context, String url,
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
                        //ams3.0
                        /*if (RegistClientInfoResponse.getClientId() != null) {
                                httpget.addHeader(
                                                "Cookie",
                                                "lpsust="+ PsAuthenServiceL.getStData(context,AmsRequest.RID, visiable)
                                                 + ";clientid=" + RegistClientInfoResponse.getClientId());
                        } */

                        HttpResponse response = httpClient.execute(httpget);
                        int code = response.getStatusLine().getStatusCode();
                        byte[] bytes = EntityUtils.toByteArray(response.getEntity());
                        httpClient.getConnectionManager().shutdown();

                        Log.i(TAG, "NetworkHttpRequest >> executeHttpGet >>ResponseCode: " + code);
                        Log.i(TAG, "NetworkHttpRequest >> executeHttpGet >>Responsebody: " + Arrays.toString(bytes));

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

      //modify for ams5.0
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
                                //ams5.0
                                httppost.addHeader("Content-Type", "application/octet-stream");
                                StringEntity reqEntity = new StringEntity(post, HTTP.UTF_8);
                                httppost.setEntity(reqEntity);

                                HttpResponse response = httpClient.execute(httppost);
                                int code = response.getStatusLine().getStatusCode();
                                byte[] bytes = EntityUtils.toByteArray(response.getEntity());
                                httpClient.getConnectionManager().shutdown();

                                Log.i(TAG, "ResponseCode: " + code);

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
        public HttpReturn executeHttpPost(Context context, String url, String post,
                        int requestFrom, boolean visiable,final AmsCallback callback) {
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
                                //ams5.0
                                httppost.addHeader("Content-Type", "application/octet-stream");
                                StringEntity reqEntity = new StringEntity(post, HTTP.UTF_8);
                                httppost.setEntity(reqEntity);

                                HttpResponse response = httpClient.execute(httppost);
                                int code = response.getStatusLine().getStatusCode();
                                byte[] bytes = EntityUtils.toByteArray(response.getEntity());
                                httpClient.getConnectionManager().shutdown();

                                Log.i(TAG, "ResponseCode: " + code);

                                ret.code = code;
                                ret.bytes = bytes;
                        } catch (Exception e) {
                                e.printStackTrace();
                        }
                        long endTime = System.currentTimeMillis();
                         callback.onResult( ret.code,ret.bytes);
                        Log.i(TAG, "Request end time:" + endTime);
                        Log.i(TAG, "Request time consuming = " + (endTime - startTime));
                        return ret;
        }



}
