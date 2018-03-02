package com.lenovo.lejingpin.ams;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.lenovo.lejingpin.ams.RegistClientInfoRequest.ClientInfo;
import com.lenovo.lejingpin.ams.RegistClientInfoRequest.RegistClientInfoResponse;
import com.lenovo.lejingpin.net.CacheManager;
import com.lenovo.lejingpin.net.NetworkHttpRequest;
import com.lenovo.lejingpin.net.NetworkHttpRequest.HttpReturn;
import com.lenovo.lejingpin.net.RequestManager;
import com.lenovo.lejingpin.net.RequestManager.ILeHttpCallback;
import com.lenovo.lsf.lds.PsServerInfo;

public class AmsSession {
	public static String sAmsRequestHost = null;
	public static String sAmsRequestLeJingpinHost = null;
	
	private AmsSession(){}

	public interface AmsCallback {
		public void onResult(AmsRequest request, int code, byte[] bytes);
	}

	public static void init(final Context context, final AmsCallback callback, final int width, final int height){
		new Thread() {
			public void run(){
				int code = -1;
				WifiManager wifi = (WifiManager)context.getSystemService("wifi");
			    WifiInfo info = wifi.getConnectionInfo();
			    ConnectivityManager mConnMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
			    NetworkInfo infoM = mConnMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			    boolean isMobile = (infoM!=null && infoM.isConnected());
			    if((info!=null)||(isMobile)){
			    	String wifiMac = null;
			    	if(info!= null){
			    		wifiMac = info.getMacAddress();
			    	}
			    	if((wifiMac!=null && wifiMac.length()!=0)||isMobile){
			    		code = initAms(context, width, height);
			    	}
			    }
				callback.onResult(null, code, null);
			}
		}.start();
	}

	public static int initAms(final Context context, int width, int height) {
		boolean mVisiable = false;
		try{
			// fix bug 13513
			sAmsRequestHost = PsServerInfo.queryServerUrl(context, AmsRequest.SID);
		}catch(Exception e){
			e.printStackTrace();
		}
		sAmsRequestLeJingpinHost = "http://launcher.lenovo.com/boutique";
		final SharedPreferences preferences = context.getSharedPreferences("Ams", Context.MODE_PRIVATE);
		if(preferences.getString("ClientId", null) != null && preferences.getString("Pa", null) != null) {
			// sAmsRequestHost = preferences.getString("AmsRequestHost", null);
			RegistClientInfoResponse.getClientInfo().setClientId(preferences.getString("ClientId", null));
			RegistClientInfoResponse.getClientInfo().setPa(preferences.getString("Pa", null));
			return 200;
		} else {
			// TODO Auto-generated method stub
			Editor editor = preferences.edit();
			// sAmsRequestHost = PsServerInfo.queryServerUrl(context,
			// AmsRequest.SID);
			// editor.putString("AmsRequestHost", sAmsRequestHost);
			Log.i("HawaiiLog", "QueryServerUrl=" + sAmsRequestHost);
			if(sAmsRequestHost != null) {
				NetworkHttpRequest request = new NetworkHttpRequest();
				RegistClientInfoRequest clientInfoRequest = new RegistClientInfoRequest(context);
				clientInfoRequest.setData(width, height);
				HttpReturn httpReturn = request.executeHttpGet(context, null,clientInfoRequest.getUrl(), mVisiable);
				RegistClientInfoResponse registResponse = new RegistClientInfoResponse();
				ClientInfo info = RegistClientInfoResponse.getClientInfo();
				if(httpReturn.code == 200) {
					registResponse.parseFrom(httpReturn.bytes);
					editor.putString("ClientId", info.getClientId());
					editor.putString("Pa", info.getPa());
					editor.commit();
					Log.i("HawaiiLog", "RegistClientiInfoSuccess---clientid=" + info.getClientId() + "----------pa=" + info.getPa());

				} else if(httpReturn.code == 401) {
					mVisiable = true;
					clientInfoRequest = new RegistClientInfoRequest(context);
					clientInfoRequest.setData(width, height);
					httpReturn = request.executeHttpGet(context, null,clientInfoRequest.getUrl(), mVisiable);
					if(httpReturn.code == 200) {
						registResponse.parseFrom(httpReturn.bytes);
						editor.putString("ClientId", info.getClientId());
						editor.putString("Pa", info.getPa());
						editor.commit();
					}
				} else {
					Log.i("HawaiiLog", "RegistClientiInfoErrorData---" + new String(httpReturn.bytes));
					registResponse.parseFrom(httpReturn.bytes);
					Log.i("HawaiiLog", "RegistClientiInfoError---" + info.getError());
				}
				if(RegistClientInfoResponse.getClientId() != null) { return 200; }
			}
			return 0;

		}
	}

	public static void init(final Context context, final AmsCallback callback) {
		new Thread() {
			public void run() {
				int code = initAms(context);
//				if(code == 200) {
					// code = SnsSession.initSns(context);
//				}
				callback.onResult(null, code, null);
			}
		}.start();
	}

	public static int initAms(final Context context) {
		boolean mVisiable = false;
		sAmsRequestHost = PsServerInfo.queryServerUrl(context, AmsRequest.SID);
		final SharedPreferences preferences = context.getSharedPreferences("Ams", Context.MODE_PRIVATE);
		if(preferences.getString("ClientId", null) != null && preferences.getString("Pa", null) != null) {
			// sAmsRequestHost = preferences.getString("AmsRequestHost", null);
			RegistClientInfoResponse.getClientInfo().setClientId(preferences.getString("ClientId", null));
			RegistClientInfoResponse.getClientInfo().setPa(preferences.getString("Pa", null));
			return 200;
		} else {
			// TODO Auto-generated method stub
			Editor editor = preferences.edit();
			Log.i("HawaiiLog", "QueryServerUrl=" + sAmsRequestHost);
			if(sAmsRequestHost != null) {
				NetworkHttpRequest request = new NetworkHttpRequest();
				HttpReturn httpReturn = request.executeHttpGet(context, null,new RegistClientInfoRequest(context).getUrl(), mVisiable);
				RegistClientInfoResponse registResponse = new RegistClientInfoResponse();
				ClientInfo info = RegistClientInfoResponse.getClientInfo();
				if(httpReturn.code == 200) {
					registResponse.parseFrom(httpReturn.bytes);
					editor.putString("ClientId", info.getClientId());
					editor.putString("Pa", info.getPa());
					editor.commit();
					Log.i("HawaiiLog", "RegistClientiInfoSuccess---clientid=" + info.getClientId() + "----------pa=" + info.getPa());

				} else if(httpReturn.code == 401) {
					mVisiable = true;
					httpReturn = request.executeHttpGet(context, null,new RegistClientInfoRequest(context).getUrl(), mVisiable);
					if(httpReturn.code == 200) {
						registResponse.parseFrom(httpReturn.bytes);
						editor.putString("ClientId", info.getClientId());
						editor.putString("Pa", info.getPa());
						editor.commit();
					}
				} else {
					Log.i("HawaiiLog", "RegistClientiInfoErrorData---" + new String(httpReturn.bytes));
					registResponse.parseFrom(httpReturn.bytes);
					Log.i("HawaiiLog", "RegistClientiInfoError---" + info.getError());
				}
				if(RegistClientInfoResponse.getClientId() != null) { return 200; }
			}
			return 0;

		}
	}

	public static void executeOnCurrentThread(final Context context, final AmsRequest request, final AmsCallback callback) {
		final String sUrl = request.getUrl();
		final String sPostData = request.getPost();
		int sMode = request.getHttpMode();
		String sPriority = request.getPriority();
		Log.d("HawaiiLog", "AmsSession >> execute >> sPriority=" + sPriority + ", sMode=" + sMode + ", sPostData=" + sPostData);
		if(sMode == 0) {
			// get
			if("low".equals(sPriority)) {
				AmsNetworkHandler.executeHttpGet(context, null,sUrl, new ILeHttpCallback() {
					public void onReturn(int code, byte[] bytes) {
						if(code == 200) {
							callback.onResult(request,code, bytes);
							CacheManager.writeCacheData(context, sUrl, bytes);
						} else {
							callback.onResult(request,code, null);
						}
					}
				});

			} else if("high".equals(sPriority)) {
				AmsNetworkHandler.executeHttpGet(context, request, new ILeHttpCallback() {

					public void onReturn(int code, byte[] data) {
						// TODO Auto-generated method stub
						callback.onResult(request, code, data);
					}
				});
			}
		}
	}

	public static void execute(final Context context, final AmsRequest request, final AmsCallback callback) {
		String sUrl = request.getUrl();
		final String sPostData = request.getPost();
		int sMode = request.getHttpMode();
		String sPriority = request.getPriority();
		Log.d("HawaiiLog", "AmsSession >> execute >> sPriority=" + sPriority + ", sMode=" + sMode + ", sPostData=" + sPostData);
		if(sMode == 0) {
			// get
			if("low".equals(sPriority)) {
				RequestManager.executeHttpGet(context, sUrl, new ILeHttpCallback() {

					public void onReturn(int code, byte[] data) {
						// TODO Auto-generated method stub
						callback.onResult(request, code, data);
					}
				});
			} else if("high".equals(sPriority)) {
				new Thread() {

					public void run() {
						AmsNetworkHandler.executeHttpGet(context, request, new ILeHttpCallback() {

							public void onReturn(int code, byte[] data) {
								// TODO Auto-generated method stub
								callback.onResult(request, code, data);
							}
						});
					}
				}.start();
			}
		} else if(sMode == 1) {
			// post
			if("low".equals(sPriority)) {
				RequestManager.executeHttpPost(context, sUrl, sPostData, new ILeHttpCallback() {

					public void onReturn(int code, byte[] data) {
						// TODO Auto-generated method stub
						callback.onResult(request, code, data);
					}
				});
			} else if("high".equals(sPriority)) {
				new Thread() {

					public void run() {
						AmsNetworkHandler.executeHttpPost(context, request, sPostData, new ILeHttpCallback() {

							public void onReturn(int code, byte[] data) {
								// TODO Auto-generated method stub
								callback.onResult(request, code, data);
							}
						});
					}
				}.start();

			}
		}
	}
}
