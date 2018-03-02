package com.lenovo.lejingpin.share.ams;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.lenovo.lejingpin.share.ams.RegistClientInfoRequest.ClientInfo;
import com.lenovo.lejingpin.share.ams.RegistClientInfoRequest.RegistClientInfoResponse;
import com.lenovo.lejingpin.share.net.CacheManager;
import com.lenovo.lejingpin.share.net.NetworkHttpRequest;
import com.lenovo.lejingpin.share.net.NetworkHttpRequest.HttpReturn;
import com.lenovo.lejingpin.share.net.RequestManager;
import com.lenovo.lejingpin.share.net.RequestManager.ILeHttpCallback;
import com.lenovo.lsf.lds.PsServerInfo;

public class AmsSession {
	public static String sAmsRequestHost = null;
	private static final String TAG = "xujing3";

	private AmsSession(){
		
	}
	
	public interface AmsCallback {
		public void onResult(AmsRequest request, int code, byte[] bytes);
	}

	public static void init(final Context context, final AmsCallback callback,
			final int width, final int height) {
		new Thread() {

			public void run() {
				int code = -1;
				WifiManager wifi = (WifiManager) context
						.getSystemService("wifi");
				if (wifi != null) {
					WifiInfo info = wifi.getConnectionInfo();
					ConnectivityManager mConnMgr = (ConnectivityManager) context
							.getSystemService(Context.CONNECTIVITY_SERVICE);
					NetworkInfo infoM = mConnMgr
							.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
					boolean isMobile = (infoM != null && infoM.isConnected());
					if ((info != null) || (isMobile)) {
						String wifiMac = null;
						if (info != null) {
							wifiMac = info.getMacAddress();
						}
						if ((wifiMac != null && wifiMac.length() != 0)
								|| isMobile) {
							code = initAms(context, width, height);
						}
					}
				}
				callback.onResult(null, code, null);
			}
		}.start();
	}

	public static int initAms(final Context context, int width, int height) {
		boolean mVisiable = false;
		final SharedPreferences preferences = context.getSharedPreferences(
				"Ams", Context.MODE_PRIVATE);
		sAmsRequestHost = PsServerInfo.queryServerUrl(context, AmsRequest.SID);
		Log.i(TAG, "AmsSession.initAms, QueryServerUrl : " + sAmsRequestHost);
		if (preferences.getString("ClientId", null) != null
				&& preferences.getString("Pa", null) != null) {
			// sAmsRequestHost = preferences.getString("AmsRequestHost", null);
			RegistClientInfoResponse.getClientInfo().setClientId(
					preferences.getString("ClientId", null));
			RegistClientInfoResponse.getClientInfo().setPa(
					preferences.getString("Pa", null));
			return 200;
		} else {
			// TODO Auto-generated method stub
			Editor editor = preferences.edit();
			// sAmsRequestHost = PsServerInfo.queryServerUrl(context,
			// AmsRequest.SID);
			// editor.putString("AmsRequestHost", sAmsRequestHost);
			if (sAmsRequestHost != null) {
				NetworkHttpRequest request = new NetworkHttpRequest();
				RegistClientInfoRequest clientInfoRequest = new RegistClientInfoRequest(
						context);
				clientInfoRequest.setData(width, height);
				HttpReturn httpReturn = request.executeHttpGet(context,
						clientInfoRequest.getUrl(), mVisiable);
				RegistClientInfoResponse registResponse = new RegistClientInfoResponse();
				ClientInfo info = RegistClientInfoResponse.getClientInfo();
				if (httpReturn.code == 200) {
					registResponse.parseFrom(httpReturn.bytes);
					editor.putString("ClientId", info.getClientId());
					editor.putString("Pa", info.getPa());
					editor.commit();
					Log.i(TAG,
							"RegistClientiInfoSuccess---clientid="
									+ info.getClientId() + "----------pa="
									+ info.getPa());

				} else if (httpReturn.code == 401) {
					mVisiable = true;
					clientInfoRequest = new RegistClientInfoRequest(context);
					clientInfoRequest.setData(width, height);
					httpReturn = request.executeHttpGet(context,
							clientInfoRequest.getUrl(), mVisiable);
					if (httpReturn.code == 200) {
						registResponse.parseFrom(httpReturn.bytes);
						editor.putString("ClientId", info.getClientId());
						editor.putString("Pa", info.getPa());
						editor.commit();
						Log.i(TAG,
								"RegistClientiInfoSuccess 2---clientid="
										+ info.getClientId() + "----------pa="
										+ info.getPa());
					}
				} else {
					// Log.i("AmsSession", "RegistClientiInfoErrorData---" + new
					// String(httpReturn.bytes));
					registResponse.parseFrom(httpReturn.bytes);
					// Log.i("AmsSession", "RegistClientiInfoError---" +
					// info.getError());
				}
				if (RegistClientInfoResponse.getClientId() != null) {
					return 200;
				}
			}
			return 0;

		}
	}

	public static void init(final Context context, final AmsCallback callback) {
		new Thread() {
			public void run() {
				int code = initAms(context);
				// if(code == 200) {
				// code = SnsSession.initSns(context);
				// }
				callback.onResult(null, code, null);
			}
		}.start();
	}

	public static int initAms(final Context context) {
		boolean mVisiable = false;
		sAmsRequestHost = PsServerInfo.queryServerUrl(context, AmsRequest.SID);
		final SharedPreferences preferences = context.getSharedPreferences(
				"Ams", Context.MODE_PRIVATE);
		if (preferences.getString("ClientId", null) != null
				&& preferences.getString("Pa", null) != null) {
			// sAmsRequestHost = preferences.getString("AmsRequestHost", null);
			RegistClientInfoResponse.getClientInfo().setClientId(
					preferences.getString("ClientId", null));
			RegistClientInfoResponse.getClientInfo().setPa(
					preferences.getString("Pa", null));
			return 200;
		} else {
			// TODO Auto-generated method stub
			Editor editor = preferences.edit();
			Log.i(TAG, "QueryServerUrl=" + sAmsRequestHost);
			if (sAmsRequestHost != null) {
				NetworkHttpRequest request = new NetworkHttpRequest();
				HttpReturn httpReturn = request.executeHttpGet(context,
						new RegistClientInfoRequest(context).getUrl(),
						mVisiable);
				RegistClientInfoResponse registResponse = new RegistClientInfoResponse();
				ClientInfo info = RegistClientInfoResponse.getClientInfo();
				if (httpReturn.code == 200) {
					registResponse.parseFrom(httpReturn.bytes);
					editor.putString("ClientId", info.getClientId());
					editor.putString("Pa", info.getPa());
					editor.commit();
					Log.i(TAG,
							"RegistClientiInfoSuccess 3 ---clientid="
									+ info.getClientId() + "----------pa="
									+ info.getPa());

				} else if (httpReturn.code == 401) {
					mVisiable = true;
					httpReturn = request.executeHttpGet(context,
							new RegistClientInfoRequest(context).getUrl(),
							mVisiable);
					if (httpReturn.code == 200) {
						registResponse.parseFrom(httpReturn.bytes);
						editor.putString("ClientId", info.getClientId());
						editor.putString("Pa", info.getPa());
						editor.commit();
						Log.i(TAG,
								"RegistClientiInfoSuccess 4---clientid="
										+ info.getClientId() + "----------pa="
										+ info.getPa());
					}
				} else {
					Log.i(TAG, "RegistClientiInfoErrorData---"
							+ new String(httpReturn.bytes));
					registResponse.parseFrom(httpReturn.bytes);
					Log.i(TAG, "RegistClientiInfoError---" + info.getError());
				}
				if (RegistClientInfoResponse.getClientId() != null) {
					return 200;
				}
			}
			return 0;

		}
	}

	public static void executeOnCurrentThread(final Context context,
			final AmsRequest request, final AmsCallback callback) {
		final String sUrl = request.getUrl();
		final String sPostData = request.getPost();
		int sMode = request.getHttpMode();
		String sPriority = request.getPriority();
		Log.d(TAG, "AmsSession >> execute >> sPriority=" + sPriority
				+ ", sMode=" + sMode + ", sPostData=" + sPostData);
		if (sMode == 0) {
			// get
			if ("low".equals(sPriority)) {
				AmsNetworkHandler.executeHttpGet(context, sUrl,
						new ILeHttpCallback() {
							public void onReturn(int code, byte[] bytes) {
								if (code == 200) {
									callback.onResult(request, code, bytes);
									CacheManager.writeCacheData(context, sUrl,
											bytes);
								} else {
									callback.onResult(request, code, null);
								}
							}
						});

			} else if ("high".equals(sPriority)) {
				AmsNetworkHandler.executeHttpGet(context, request,
						new ILeHttpCallback() {

							public void onReturn(int code, byte[] data) {
								// TODO Auto-generated method stub
								callback.onResult(request, code, data);
							}
						});
			}
		}
	}

	public static void execute(final Context context, final AmsRequest request,
			final AmsCallback callback) {
		String sUrl = request.getUrl();
		final String sPostData = request.getPost();
		int sMode = request.getHttpMode();
		String sPriority = request.getPriority();
		Log.d(TAG, "AmsSession >> execute >> sPriority=" + sPriority
				+ ", sMode=" + sMode + ", sPostData=" + sPostData);
		if (sMode == 0) {
			// get
			if ("low".equals(sPriority)) {
				RequestManager.executeHttpGet(context, sUrl,
						new ILeHttpCallback() {

							public void onReturn(int code, byte[] data) {
								// TODO Auto-generated method stub
								callback.onResult(request, code, data);
							}
						});
			} else if ("high".equals(sPriority)) {
				new Thread() {

					public void run() {
						AmsNetworkHandler.executeHttpGet(context, request,
								new ILeHttpCallback() {

									public void onReturn(int code, byte[] data) {
										// TODO Auto-generated method stub
										callback.onResult(request, code, data);
									}
								});
					}
				}.start();
			}
		} else if (sMode == 1) {
			// post
			if ("low".equals(sPriority)) {
				RequestManager.executeHttpPost(context, sUrl, sPostData,
						new ILeHttpCallback() {

							public void onReturn(int code, byte[] data) {
								// TODO Auto-generated method stub
								callback.onResult(request, code, data);
							}
						});
			} else if ("high".equals(sPriority)) {
				new Thread() {

					public void run() {
						AmsNetworkHandler.executeHttpPost(context, request,
								sPostData, new ILeHttpCallback() {

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

	// zhanglz1
	public static void execute(final Context context,
			final WallpaperRequest request, final AmsCallback callback) {
		String sUrl = request.getUrl();
		final String sPostData = request.getPost();
		int sMode = request.getHttpMode();
		String sPriority = request.getPriority();
		Log.d(TAG, "AmsSession >> execute >> sPriority=" + sPriority
				+ ", sMode=" + sMode + ", sPostData=" + sPostData + ".request="
				+ request);

		if (sMode == 0) {
			// get
			if ("low".equals(sPriority)) {
				RequestManager.executeHttpGet(context, sUrl,
						new ILeHttpCallback() {

							public void onReturn(int code, byte[] data) {
								// TODO Auto-generated method stub
								callback.onResult(request, code, data);
							}
						});
			} else if ("high".equals(sPriority)) {
				new Thread() {

					public void run() {
						AmsNetworkHandler.executeHttpGet(context, request,
								new ILeHttpCallback() {

									public void onReturn(int code, byte[] data) {
										// TODO Auto-generated method stub
										callback.onResult(request, code, data);
									}
								});
					}
				}.start();
			}
		} else if (sMode == 1) {
			// post
			if ("low".equals(sPriority)) {
				RequestManager.executeHttpPost(context, sUrl, sPostData,
						new ILeHttpCallback() {

							public void onReturn(int code, byte[] data) {
								// TODO Auto-generated method stub
								callback.onResult(request, code, data);
							}
						});
			} else if ("high".equals(sPriority)) {
				new Thread() {

					public void run() {
						AmsNetworkHandler.executeHttpPost(context, request,
								sPostData, new ILeHttpCallback() {

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
