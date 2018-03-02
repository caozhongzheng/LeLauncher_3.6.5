package com.lenovo.launcher2.addon.share;


import java.net.URLDecoder;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class SUSHttpRequest {
	private static final String TAG = "SUSHttpRequest";
	private static final int DEFAULTTIMEOUT = 20000;
	
	public String getUrl() {
        String url="http://susapi.lenovomm.com/adpserver/GetVIByPNFNorUser?SDKVer=0&ReqType=normal&PrjName=lenovo_phone&Lang=zh&PackageName=com.lenovo.anyshare&AppVerCode=1&ChannelKey=LELAUNCHER";
        return url;
    }

	public String executeHttpGet(Context context, String url){
		try {
			Log.i(TAG, "SUSHttpRequest >> executeHttpGet >> url : "+url);
			HttpParams params = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(params, DEFAULTTIMEOUT);
			HttpConnectionParams.setSoTimeout(params, DEFAULTTIMEOUT);
			HttpClientParams.setRedirecting(params, true);
			DefaultHttpClient httpClient = new DefaultHttpClient(params);
			HttpGet httpget = new HttpGet(url);

			HttpResponse response = httpClient.execute(httpget);
			if( response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				String resultStr = EntityUtils.toString(response.getEntity());
				return resultStr;
			}

		} catch (Exception e) {
			Log.i(TAG, "SUSHttpRequest >> http get exception, "+e.toString());
		}

		return null;
	}
	
	public void parseFrom(String param) {
		 Log.i(TAG,"SUSHttpRequest >> "+ param);
	     String jsonObjectStr = null;
		 String resultion = null;
		 if( null != param && param.length() > 0 ){
			 	JSONObject jsonObject = null;
				try {
					jsonObject = new JSONObject(param);
					resultion = jsonObject.getString("RES");
					if("SUCCESS".equals(resultion)){
						//VersionCode
						jsonObjectStr = jsonObject.getString("VerCode");
						String mVersionCode = (null!=jsonObjectStr && jsonObjectStr.length()>0)?URLDecoder.decode(jsonObjectStr):null;
						Log.i(TAG,"SUSHttpRequest >> versionCode: "+ mVersionCode);
						//Download Url
						jsonObjectStr = jsonObject.getString("DownloadURL");
						String mUrl = (null!=jsonObjectStr && jsonObjectStr.length()>0)?URLDecoder.decode(jsonObjectStr):null;
						Log.i(TAG,"SUSHttpRequest >> url: "+ mUrl);
						return;
					}else if("LATESTVERSION".equals(resultion) ||
							 "NOTFOUND".equals(resultion)){
                        //Not found apk
					}else if("EXCEPTION".equals(resultion)){
                        //Network error
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
	}
        
}
