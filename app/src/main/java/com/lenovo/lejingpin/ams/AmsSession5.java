package com.lenovo.lejingpin.ams;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.lenovo.lejingpin.ams.RegisterClient5.ClientInfo;
import com.lenovo.lejingpin.ams.RegistClientInfoRequest.RegistClientInfoResponse;
import com.lenovo.lejingpin.ams.RegisterClient5.RegisterClient5Response;
import com.lenovo.lejingpin.net.CacheManager;
import com.lenovo.lejingpin.net.NetworkHttpRequest;
import com.lenovo.lejingpin.net.NetworkHttpRequest5.HttpReturn;
import com.lenovo.lejingpin.net.NetworkHttpRequest5;
import com.lenovo.lejingpin.net.RequestManager;
import com.lenovo.lejingpin.net.RequestManager.ILeHttpCallback;
import com.lenovo.lsf.lds.PsServerInfo;

public class AmsSession5 {
	public static String sAmsRequestHost = null;
	public static String sLeLauncherHost = null;

	private AmsSession5(){}
	
	public interface AmsCallback {
		public void onResult(AmsRequest request, int code, byte[] bytes);
	}

	public static void init(final Context context, final AmsCallback callback, final int width, final int height) {
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
			sAmsRequestHost = PsServerInfo.queryServerUrl(context, AmsRequest.SID);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		sLeLauncherHost = "http://launcher.lenovo.com/boutique/";
		final SharedPreferences preferences = context.getSharedPreferences("Ams", Context.MODE_PRIVATE);
		if(preferences.getString("ClientId5", null) != null && preferences.getString("Pa5", null) != null) {
			// sAmsRequestHost = preferences.getString("AmsRequestHost", null);
			RegisterClient5Response.getClientInfo().setClientId(preferences.getString("ClientId5", null));
			RegisterClient5Response.getClientInfo().setPa(preferences.getString("Pa5", null));
			return 200;
		} else {
			// TODO Auto-generated method stub
			Editor editor = preferences.edit();
			// sAmsRequestHost = PsServerInfo.queryServerUrl(context,
			// AmsRequest.SID);
			// editor.putString("AmsRequestHost", sAmsRequestHost);
			Log.i("HawaiiLog", "QueryServerUrl=" + sAmsRequestHost);
			if(sAmsRequestHost != null) {
				NetworkHttpRequest5 request = new NetworkHttpRequest5();
				//RegistClientInfoRequest clientInfoRequest = new RegistClientInfoRequest(context);
				RegisterClient5  registerclient5 = new RegisterClient5(context);
				//clientInfoRequest.setData(width, height);
				registerclient5.setData(width, height);
				//HttpReturn httpReturn = request.executeHttpGet(context, null,clientInfoRequest.getUrl(), mVisiable);
				String url = registerclient5.getUrl();
				String post = registerclient5.getPost();
				HttpReturn httpReturn = request.executeHttpPost(context, url, post, 0, false);
				RegisterClient5Response registResponse = new RegisterClient5Response();
				ClientInfo info = RegisterClient5Response.getClientInfo();
				if(httpReturn.code == 200) {
					registResponse.parseFrom(httpReturn.bytes);
					editor.putString("ClientId5", info.getClientId());
					editor.putString("Pa5", info.getPa());
					editor.commit();
					Log.i("HawaiiLog", "RegistClientiInfo5Success---clientid5=" + info.getClientId() + "----------pa5=" + info.getPa());

				} else if(httpReturn.code == 401) {
					mVisiable = true;
//					clientInfoRequest = new RegistClientInfoRequest(context);
//					clientInfoRequest.setData(width, height);
					registerclient5 = new RegisterClient5(context);
					registerclient5.setData(width, height);
					//httpReturn = request.executeHttpGet(context, null,clientInfoRequest.getUrl(), mVisiable);
					String url_1 = registerclient5.getUrl();
					String post_1 = registerclient5.getPost();
					httpReturn = request.executeHttpPost(context, url_1, post_1, 0, false);
					if(httpReturn.code == 200) {
						registResponse.parseFrom(httpReturn.bytes);
						editor.putString("ClientId5", info.getClientId());
						editor.putString("Pa5", info.getPa());
						editor.commit();
					}
				} else {
					Log.i("HawaiiLog", "RegistClientiInfo5ErrorData---" + new String(httpReturn.bytes));
					registResponse.parseFrom(httpReturn.bytes);
					Log.i("HawaiiLog", "RegistClientiIn5foError---" + info.getError());
				}
				if(RegisterClient5Response.getClientId() != null) { return 200; }
			}
			return 0;

		}
	}

	public static void init(final Context context, final AmsCallback callback) {
		new Thread() {
			public void run() {
				int code = initAms(context);
				if(code == 200) {
					// code = SnsSession.initSns(context);
				}
				callback.onResult(null, code, null);
			}
		}.start();
	}

	public static int initAms(final Context context) {
		boolean mVisiable = false;
		sAmsRequestHost = PsServerInfo.queryServerUrl(context, AmsRequest.SID);
		final SharedPreferences preferences = context.getSharedPreferences("Ams", Context.MODE_PRIVATE);
		if(preferences.getString("ClientId5", null) != null && preferences.getString("Pa5", null) != null) {
			// sAmsRequestHost = preferences.getString("AmsRequestHost", null);
			RegisterClient5Response.getClientInfo().setClientId(preferences.getString("ClientId5", null));
			RegisterClient5Response.getClientInfo().setPa(preferences.getString("Pa5", null));
			return 200;
		} else {
			// TODO Auto-generated method stub
			Editor editor = preferences.edit();
			Log.i("HawaiiLog", "QueryServerUrl=" + sAmsRequestHost);
			if(sAmsRequestHost != null) {
				NetworkHttpRequest5 request = new NetworkHttpRequest5();
				RegisterClient5  registerclient5 = new RegisterClient5(context);
				String url = registerclient5.getUrl();
				String post = registerclient5.getPost();
				
				HttpReturn httpReturn = request.executeHttpPost(context, url, post, 0, false);
				RegisterClient5Response registResponse = new RegisterClient5Response();
				ClientInfo info = RegisterClient5Response.getClientInfo();
				if(httpReturn.code == 200) {
					registResponse.parseFrom(httpReturn.bytes);
					editor.putString("ClientId5", info.getClientId());
					editor.putString("Pa5", info.getPa());
					editor.commit();
					Log.i("HawaiiLog", "RegistClientiInfo5Success---clientid5=" + info.getClientId() + "----------pa5=" + info.getPa());

				} else if(httpReturn.code == 401) {
					mVisiable = true;
					httpReturn = request.executeHttpPost(context, url, post, 0, false);
					if(httpReturn.code == 200) {
						registResponse.parseFrom(httpReturn.bytes);
						editor.putString("ClientId5", info.getClientId());
						editor.putString("Pa5", info.getPa());
						editor.commit();
					}
				} else {
					Log.i("HawaiiLog", "RegistClientiInfo5ErrorData---" + new String(httpReturn.bytes));
					registResponse.parseFrom(httpReturn.bytes);
					Log.i("HawaiiLog", "RegistClientiInfo5Error---" + info.getError());
				}
				if(RegisterClient5Response.getClientId() != null) { return 200; }
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
				AmsNetworkHandler5.executeHttpGet(context, null,sUrl, new ILeHttpCallback() {
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
				AmsNetworkHandler5.executeHttpGet(context, request, new ILeHttpCallback() {

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
						AmsNetworkHandler5.executeHttpGet(context, request, new ILeHttpCallback() {

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
						AmsNetworkHandler5.executeHttpPost(context, request, sPostData, new ILeHttpCallback() {

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
