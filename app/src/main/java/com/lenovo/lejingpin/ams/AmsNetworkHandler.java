package com.lenovo.lejingpin.ams;

import android.content.Context;
import android.util.Log;

import com.lenovo.lejingpin.ams.RegistClientInfoRequest.ClientInfo;
import com.lenovo.lejingpin.ams.RegistClientInfoRequest.RegistClientInfoResponse;
import com.lenovo.lejingpin.net.NetworkHttpRequest;
import com.lenovo.lejingpin.net.NetworkHttpRequest.HttpReturn;
import com.lenovo.lejingpin.net.RequestManager.ILeHttpCallback;
import com.lenovo.lsf.lds.PsServerInfo;

public class AmsNetworkHandler {
	private static boolean mVisiable = false;

	private AmsNetworkHandler(){}
	
	public static void executeHttpGet(final Context context,String contentType,String url,
			final ILeHttpCallback response) {
		// TODO Auto-generated method stub
		NetworkHttpRequest request = new NetworkHttpRequest();
		if (url.startsWith("null")) {
			url = PsServerInfo.queryServerUrl(context, AmsRequest.SID)
					+ url.substring(4);
		}
		NetworkHttpRequest.HttpReturn ret = request.executeHttpGet(context,contentType,
				url, mVisiable);
		if (ret.code == 308) {
			registClientInfo(context, request, response);
			ret = request.executeHttpGet(context, contentType,url, mVisiable);
			if (ret.code == 308) {
				response.onReturn(ret.code, ret.bytes);
			}
		}
		if (ret.code == 401) {
			mVisiable = true;
			ret = request.executeHttpGet(context, contentType,url, mVisiable);
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
		NetworkHttpRequest.HttpReturn ret = request.executeHttpGet(context,null,
				url, mVisiable);
		if (ret.code == 308) {
			registClientInfo(context, request, response);
			ret = request.executeHttpGet(context, null,url, mVisiable);
			if (ret.code == 308) {
				response.onReturn(ret.code, ret.bytes);
			}
		}
		if (ret.code == 401) {
			mVisiable = true;
			ret = request.executeHttpGet(context, null,url, mVisiable);
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
		Log.i("HawaiiLog============", registurl);
		if (registurl.startsWith("null")) {
			registurl = PsServerInfo.queryServerUrl(context, AmsRequest.SID)
					+ registurl.substring(4);
		}
		HttpReturn httpReturn = request.executeHttpGet(context,null, registurl,
				mVisiable);
		RegistClientInfoResponse registResponse = new RegistClientInfoResponse();
		ClientInfo info = RegistClientInfoResponse.getClientInfo();
		if (httpReturn.code == 200) {
			registResponse.parseFrom(httpReturn.bytes);
			Log.i("HawaiiLog",
					"RegistClientiInfoSuccess---clientid=" + info.getClientId()
							+ "----------pa=" + info.getPa());
		} else if (httpReturn.code == 401) {
			mVisiable = true;
			httpReturn = request.executeHttpGet(context, null,registurl, mVisiable);
			if (httpReturn.code == 200) {
				registResponse.parseFrom(httpReturn.bytes);
			}
		} else {
			Log.i("HawaiiLog", "RegistClientiInfoErrorData---"
					+ new String(httpReturn.bytes));
			registResponse.parseFrom(httpReturn.bytes);
			Log.i("HawaiiLog", "RegistClientiInfoError---" + info.getError());
		}
		if (RegistClientInfoResponse.getClientId() == null) {
			String error = RegistClientInfoResponse.getError();
			response.onReturn(httpReturn.code, error != null ? error.getBytes()
					: "Network connection error, please retry".getBytes());
		}

	}
}
