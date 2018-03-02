package com.lenovo.lejingpin.ams;

import android.content.Context;
import android.util.Log;

import com.lenovo.lejingpin.ams.RegisterClient5.ClientInfo;
import com.lenovo.lejingpin.ams.RegisterClient5.RegisterClient5Response;
import com.lenovo.lejingpin.net.NetworkHttpRequest5.HttpReturn;
import com.lenovo.lejingpin.net.NetworkHttpRequest5;
import com.lenovo.lejingpin.net.RequestManager.ILeHttpCallback;
import com.lenovo.lsf.lds.PsServerInfo;

public class AmsNetworkHandler5 {
	private static boolean mVisiable = false;
	
	private AmsNetworkHandler5(){};

	public static void executeHttpGet(final Context context,String contentType,String url,
			final ILeHttpCallback response) {
		// TODO Auto-generated method stub
		NetworkHttpRequest5 request = new NetworkHttpRequest5();
		if (url.startsWith("null")) {
			url = PsServerInfo.queryServerUrl(context, AmsRequest.SID)
					+ url.substring(4);
		}
		NetworkHttpRequest5.HttpReturn ret = request.executeHttpGet(context,contentType,
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
		NetworkHttpRequest5 request = new NetworkHttpRequest5();
		if (url.startsWith("null")) {
			url = PsServerInfo.queryServerUrl(context, AmsRequest.SID)
					+ url.substring(4);
		}
		NetworkHttpRequest5.HttpReturn ret = request.executeHttpPost(context,
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
		NetworkHttpRequest5 request = new NetworkHttpRequest5();
		if (url.startsWith("null")) {
			url = PsServerInfo.queryServerUrl(context, AmsRequest.SID)
					+ url.substring(4);
		}
		NetworkHttpRequest5.HttpReturn ret = request.executeHttpGet(context,null,
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
		NetworkHttpRequest5 request = new NetworkHttpRequest5();
		if (url.startsWith("null")) {
			url = PsServerInfo.queryServerUrl(context, AmsRequest.SID)
					+ url.substring(4);
		}
		NetworkHttpRequest5.HttpReturn ret = request.executeHttpPost(context,
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
			NetworkHttpRequest5 request, ILeHttpCallback response) {
		RegisterClient5 registerclient = new RegisterClient5(context);
		String registurl = registerclient.getUrl();
		String post = registerclient.getPost();
		Log.i("HawaiiLog============", registurl);
		if (registurl.startsWith("null")) {
			registurl = PsServerInfo.queryServerUrl(context, AmsRequest.SID)
					+ registurl.substring(4);
		}
//		HttpReturn httpReturn = request.executeHttpGet(context,null, registurl,
//				mVisiable);
		HttpReturn httpReturn = request.executeHttpPost(context, registurl, post, 0, false);
		RegisterClient5Response registResponse = new RegisterClient5Response();
		ClientInfo info = RegisterClient5Response.getClientInfo();
		if (httpReturn.code == 200) {
			registResponse.parseFrom(httpReturn.bytes);
			Log.i("HawaiiLog",
					"RegistClientiInfoSuccess---clientid=" + info.getClientId()
							+ "----------pa=" + info.getPa());
		} else if (httpReturn.code == 401) {
			mVisiable = true;
			httpReturn = request.executeHttpPost(context, registurl, post, 0, false);
			if (httpReturn.code == 200) {
				registResponse.parseFrom(httpReturn.bytes);
			}
		} else {
			Log.i("HawaiiLog", "RegistClientiInfoErrorData---"
					+ new String(httpReturn.bytes));
			registResponse.parseFrom(httpReturn.bytes);
			Log.i("HawaiiLog", "RegistClientiInfoError---" + info.getError());
		}
		if (RegisterClient5Response.getClientId() == null) {
			String error = RegisterClient5Response.getError();
			response.onReturn(httpReturn.code, error != null ? error.getBytes()
					: "Network connection error, please retry".getBytes());
		}

	}
}
