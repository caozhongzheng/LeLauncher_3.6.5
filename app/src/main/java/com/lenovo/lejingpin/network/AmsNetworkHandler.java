package com.lenovo.lejingpin.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

//import com.lenovo.launcher2.magicdownload.ams.RegistClientInfoRequest.ClientInfo;
//import com.lenovo.launcher2.magicdownload.ams.RegistClientInfoRequest.RegistClientInfoResponse;
import com.lenovo.lejingpin.network.RegistClientInfoRequest5.RegistClientInfoResponse5;
import com.lenovo.lejingpin.network.DeviceInfo;
import com.lenovo.lejingpin.network.NetworkHttpRequest;
import com.lenovo.lejingpin.network.NetworkHttpRequest.HttpReturn;
import com.lenovo.lsf.lds.PsServerInfo;

public class AmsNetworkHandler {
	private static boolean mVisiable = false;
	private static final String TAG = "AmsNetworkHandler";
           public interface AmsNetCallback {
                public void onReturn( int code, byte[] bytes);
        }


    private AmsNetworkHandler(){};
	public static void executeHttpGet(final Context context, String url,
			final AmsNetCallback response) {
		// TODO Auto-generated method stub
		NetworkHttpRequest request = new NetworkHttpRequest();
		if (url.startsWith("null")) {
			url = PsServerInfo.queryServerUrl(context, AmsRequest.SID)
					+ url.substring(4);
		}
		NetworkHttpRequest.HttpReturn ret = request.executeHttpGet(context,
				url, mVisiable);
		if (ret.code == 308) {
			//modify for ams5.0
			//registClientInfo(context, request, response);
			registClientInfo5(context, request, response);
			ret = request.executeHttpGet(context, url, mVisiable);
			if (ret.code == 308) {
				response.onReturn(ret.code, ret.bytes);
			}
		}
		if (ret.code == 401) {
			mVisiable = true;
			ret = request.executeHttpGet(context, url, mVisiable);
		}
		response.onReturn(ret.code, ret.bytes);
	}

          //modify for ams5.0
        public static void executeHttpGet(final Context context,
                        final AmsRequest amsRequest, final AmsNetCallback response) {
                // TODO Auto-generated method stub
                String url = amsRequest.getUrl();
                NetworkHttpRequest request = new NetworkHttpRequest();
                if (url.startsWith("null")) {
                        url = PsServerInfo.queryServerUrl(context, AmsRequest.SID)
                                        + url.substring(4);
                }
                NetworkHttpRequest.HttpReturn ret = request.executeHttpGet(context,
                                url, mVisiable);
                Log.e(TAG,"executeHttpGet      retcode================="+ret.code);
                if (ret.code == 308) {
                        //modify for ams5.0
                        //registClientInfo(context, request, response);
                        registClientInfo5(context, request, response);
                        url = amsRequest.getUrl();
                        ret = request.executeHttpGet(context, url, mVisiable);
                        if (ret.code == 308) {
                                response.onReturn(ret.code, ret.bytes);
                        }
                }
                if (ret.code == 401) {
                        mVisiable = true;
                        ret = request.executeHttpGet(context, url, mVisiable);
                }
                Log.e(TAG,"2222executeHttpGet      retcode================="+ret.code);
                response.onReturn(ret.code, ret.bytes);
        }

	public static void registClientInfo5(Context context,
			NetworkHttpRequest request, AmsNetCallback response) {
		RegistClientInfoRequest5 clientInfoRequest = new RegistClientInfoRequest5(context);
		String registurl = clientInfoRequest.getUrl();
		if (registurl.startsWith("null")) {
			registurl = PsServerInfo.queryServerUrl(context, AmsRequest.SID)
					+ registurl.substring(4);
		}
		DeviceInfo deviceInfo = DeviceInfo.getInstance(context);
		clientInfoRequest.setData(deviceInfo.getWidthPixels(), deviceInfo.getHeightPixels(), 
				deviceInfo.getDensityDpi());
		HttpReturn httpReturn = request.executeHttpPost(context, registurl,
				clientInfoRequest.getPost(), 0, mVisiable);
		RegistClientInfoResponse5 registResponse = new RegistClientInfoResponse5();
		RegistClientInfoRequest5.ClientInfo  info = RegistClientInfoRequest5.RegistClientInfoResponse5.getClientInfo();		
		
		//zdx modify
		final SharedPreferences preferences = context.getSharedPreferences("Ams", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		
		if (httpReturn.code == 200) {
			registResponse.parseFrom(httpReturn.bytes);
			Log.i(TAG,"registClientInfo5---success clientid=" + info.getClientId()+ "----------pa=" + info.getPa());
			
			editor.putString("ClientId", info.getClientId());
			editor.putString("Pa", info.getPa());
			editor.commit();
			
		} else if (httpReturn.code == 401) {
			mVisiable = true;
			httpReturn = request.executeHttpPost(context, registurl,
					clientInfoRequest.getPost(), 0, mVisiable);
			if (httpReturn.code == 200) {
				registResponse.parseFrom(httpReturn.bytes);
				Log.i(TAG,"registClientInfo5 2---success clientid=" + info.getClientId()+ "----------pa=" + info.getPa());
				editor.putString("ClientId", info.getClientId());
				editor.putString("Pa", info.getPa());
				editor.commit();
			}
		} else {
			Log.i(TAG, "registClientInfo5, ErrorData---"+ new String(httpReturn.bytes));
			registResponse.parseFrom(httpReturn.bytes);
			Log.i(TAG, "registClientInfo5, Error---" + info.getError());
		}
		if (RegistClientInfoResponse5.getClientId() == null) {
			String error = RegistClientInfoResponse5.getError();
			response.onReturn(httpReturn.code, error != null ? error.getBytes()
					: "Network connection error, please retry".getBytes());
		}

	}
	
}
