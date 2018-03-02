package com.lenovo.lejingpin.share.ams;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.lenovo.lejingpin.share.ams.RegistClientInfoRequest.ClientInfo;
import com.lenovo.lejingpin.share.ams.RegistClientInfoRequest.RegistClientInfoResponse;
import com.lenovo.lejingpin.share.net.NetworkHttpRequest;
import com.lenovo.lejingpin.share.net.NetworkHttpRequest.HttpReturn;
import com.lenovo.lejingpin.share.net.RequestManager.ILeHttpCallback;
import com.lenovo.lsf.lds.PsServerInfo;

public class AmsNetworkHandler {
	private static boolean mVisiable = false;
	
	private AmsNetworkHandler(){
		
	}

	public static void executeHttpGet(final Context context, String url,
			final ILeHttpCallback response) {
		// TODO Auto-generated method stub
		NetworkHttpRequest request = new NetworkHttpRequest();
		if (url.startsWith("null")) {
			url = PsServerInfo.queryServerUrl(context, AmsRequest.SID)
					+ url.substring(4);
		}
		NetworkHttpRequest.HttpReturn ret = request.executeHttpGet(context,
				url, mVisiable);
		if (ret.code == 308) {
			registClientInfo(context, request, response);
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

	public static void executeHttpPost(final Context context, String url,
			final String post, final ILeHttpCallback response) {
		// TODO Auto-generated method stub
		NetworkHttpRequest request = new NetworkHttpRequest();
		if (url.startsWith("null")) {
			url = PsServerInfo.queryServerUrl(context, AmsRequest.SID)
					+ url.substring(4);
		}
		NetworkHttpRequest.HttpReturn ret = request.executeHttpPost(context,
				url, post, 1, mVisiable);
		if (ret.code == 308) {
			registClientInfo(context, request, response);
			ret = request.executeHttpPost(context, url, post, 1, mVisiable);
			if (ret.code == 308) {
				response.onReturn(ret.code, ret.bytes);
			}
		}
		if (ret.code == 401) {
			mVisiable = true;
			ret = request.executeHttpPost(context, url, post, 1, mVisiable);
		}
		response.onReturn(ret.code, ret.bytes);
	}

	public static void executeHttpGet(final Context context,
			final AmsRequest amsRequest, final ILeHttpCallback response) {
		// TODO Auto-generated method stub
		String url = amsRequest.getUrl();
		NetworkHttpRequest request = new NetworkHttpRequest();
		if (url.startsWith("null")) {
			url = PsServerInfo.queryServerUrl(context, AmsRequest.SID)
					+ url.substring(4);
		}
		NetworkHttpRequest.HttpReturn ret = request.executeHttpGet(context,
				url, mVisiable);
		if (ret.code == 308) {
			registClientInfo(context, request, response);
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

	public static void executeHttpPost(final Context context,
			final AmsRequest amsRequest, final String post,
			final ILeHttpCallback response) {
		// TODO Auto-generated method stub
		String url = amsRequest.getUrl();
		NetworkHttpRequest request = new NetworkHttpRequest();
		if (url.startsWith("null")) {
			url = PsServerInfo.queryServerUrl(context, AmsRequest.SID)
					+ url.substring(4);
		}
		NetworkHttpRequest.HttpReturn ret = request.executeHttpPost(context,
				url, post, 1, mVisiable);
		if (ret.code == 308) {
			registClientInfo(context, request, response);
			ret = request.executeHttpPost(context, url, post, 1, mVisiable);
			if (ret.code == 308) {
				response.onReturn(ret.code, ret.bytes);
			}
		}
		if (ret.code == 401) {
			mVisiable = true;
			ret = request.executeHttpPost(context, url, post, 1, mVisiable);
		}
		response.onReturn(ret.code, ret.bytes);
	}

	public static void registClientInfo(Context context,
			NetworkHttpRequest request, ILeHttpCallback response) {
		String registurl = new RegistClientInfoRequest(context).getUrl();
		if (registurl.startsWith("null")) {
			registurl = PsServerInfo.queryServerUrl(context, AmsRequest.SID)
					+ registurl.substring(4);
		}
		HttpReturn httpReturn = request.executeHttpGet(context, registurl,
				mVisiable);
		RegistClientInfoResponse registResponse = new RegistClientInfoResponse();
		ClientInfo info = RegistClientInfoResponse.getClientInfo();

		// zdx modify
		final SharedPreferences preferences = context.getSharedPreferences(
				"Ams", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();

		if (httpReturn.code == 200) {
			registResponse.parseFrom(httpReturn.bytes);
			Log.i("AmsNetworkHandler", "RegistClientiInfoSuccess---clientid="
					+ info.getClientId() + "----------pa=" + info.getPa());

			editor.putString("ClientId", info.getClientId());
			editor.putString("Pa", info.getPa());
			editor.commit();

		} else if (httpReturn.code == 401) {
			mVisiable = true;
			httpReturn = request.executeHttpGet(context, registurl, mVisiable);
			if (httpReturn.code == 200) {
				registResponse.parseFrom(httpReturn.bytes);
				Log.i("AmsNetworkHandler",
						"RegistClientiInfoSuccess 2---clientid="
								+ info.getClientId() + "----------pa="
								+ info.getPa());
				editor.putString("ClientId", info.getClientId());
				editor.putString("Pa", info.getPa());
				editor.commit();
			}
		} else {
			Log.i("AmsNetworkHandler", "RegistClientiInfoErrorData---"
					+ new String(httpReturn.bytes));
			registResponse.parseFrom(httpReturn.bytes);
			Log.i("AmsNetworkHandler",
					"RegistClientiInfoError---" + info.getError());
		}
		if (RegistClientInfoResponse.getClientId() == null) {
			String error = RegistClientInfoResponse.getError();
			response.onReturn(httpReturn.code, error != null ? error.getBytes()
					: "Network connection error, please retry".getBytes());
		}

	}
}
