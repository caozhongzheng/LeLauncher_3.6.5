package com.lenovo.lejingpin.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.lenovo.lejingpin.network.RegistClientInfoRequest5.RegistClientInfoResponse5;
import com.lenovo.lejingpin.network.NetworkHttpRequest;
import com.lenovo.lejingpin.network.NetworkHttpRequest.HttpReturn;
import com.lenovo.lejingpin.network.AmsNetworkHandler.AmsNetCallback; 
import com.lenovo.lsf.lds.PsServerInfo;


public class AmsSession {
	public static String sAmsRequestHost = null;
	private static final String TAG = "AmsSession";

	private AmsSession(){};
	
	public interface AmsSessionCallback {
		public void onResult(AmsRequest request, int code, byte[] bytes);
	}


	//for ams5.0
    public static void init(final Context context, final AmsSessionCallback callback, final int width,
            final int height, final int densityDpi) {
        new Thread() {

            public void run() {
                int code = -1;
                WifiManager wifi = (WifiManager) context.getSystemService("wifi");
                WifiInfo info = wifi.getConnectionInfo();
                ConnectivityManager mConnMgr = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo infoM = mConnMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                boolean isMobile = (infoM != null && infoM.isConnected());
                if((info != null) ||(isMobile)){
			    	String wifiMac = null;
			    	if(info!= null){
			    		wifiMac = info.getMacAddress();
			    	}
			    	if((wifiMac!=null && wifiMac.length()!=0)||isMobile){
			    		code = initAms(context, width, height, densityDpi);
			    	}
			    }
                callback.onResult(null, code, null);
            }
        }.start();
    }

    //for ams5.0
    protected static int initAms(Context context, int width, int height, int densityDpi) {
    	boolean mVisiable = false;
		final SharedPreferences preferences = context.getSharedPreferences("Ams", Context.MODE_PRIVATE);
		sAmsRequestHost = PsServerInfo.queryServerUrl(context, AmsRequest.SID);
		Log.i("zdx","AmsSession.initAms, QueryServerUrl : "+ sAmsRequestHost);
		if(preferences.getString("ClientId", null) != null && preferences.getString("Pa", null) != null) {
			RegistClientInfoResponse5.getClientInfo().setClientId(preferences.getString("ClientId", null));
			RegistClientInfoResponse5.getClientInfo().setPa(preferences.getString("Pa", null));
			return 200;
		} else {
			// TODO Auto-generated method stub
			Editor editor = preferences.edit();
			if(sAmsRequestHost != null) {
				NetworkHttpRequest request = new NetworkHttpRequest();
				RegistClientInfoRequest5 clientInfoRequest = new RegistClientInfoRequest5(context);
				clientInfoRequest.setData(width, height, densityDpi);
				HttpReturn httpReturn = request.executeHttpPost(context, clientInfoRequest.getUrl(),
	                    clientInfoRequest.getPost(), 0, mVisiable);
				RegistClientInfoResponse5 registResponse = new RegistClientInfoResponse5();
				RegistClientInfoRequest5.ClientInfo info = RegistClientInfoRequest5.RegistClientInfoResponse5.getClientInfo();
				
				Log.i(TAG,"******************AmsSession.initAms,httpReturn.code:"+httpReturn.code);
				if(httpReturn.code == 200) {
					registResponse.parseFrom(httpReturn.bytes);
					editor.putString("ClientId", info.getClientId());
					editor.putString("Pa", info.getPa());
					editor.commit();
					Log.i(TAG, "RegistClientInfoRequest5.ClientInfo---clientid=" + info.getClientId() + "------pa=" + info.getPa());

				} else if(httpReturn.code == 401) {
					mVisiable = true;
					clientInfoRequest = new RegistClientInfoRequest5(context);
					clientInfoRequest.setData(width, height, densityDpi);
					httpReturn = request.executeHttpPost(context, clientInfoRequest.getUrl(),
		                    clientInfoRequest.getPost(), 0, mVisiable);
					if(httpReturn.code == 200) {
						registResponse.parseFrom(httpReturn.bytes);
						editor.putString("ClientId", info.getClientId());
						editor.putString("Pa", info.getPa());
						editor.commit();
						Log.i(TAG, "RegistClientInfoRequest5.ClientInfo--2---clientid=" + info.getClientId() + "----------pa=" + info.getPa());
					}
				} else {
					registResponse.parseFrom(httpReturn.bytes);
				}
				if(RegistClientInfoResponse5.getClientId() != null){ return 200; }
			}
			return 0;
		}
    }
    
     //ams5.0
    public static void execute(final Context context, final AmsRequest request, final AmsSessionCallback callback) {
                //String sUrl = request.getUrl();
                final String sPostData = request.getPost();
                int sMode = request.getHttpMode();
                String sPriority = request.getPriority();
                Log.d(TAG, "AmsSession >> execute >> sPriority=" + sPriority + ", sMode=" + sMode + ", sPostData=" + sPostData);
                        // get
                                new Thread() {

                                        public void run() {
                                                AmsNetworkHandler.executeHttpGet(context, request, new AmsNetCallback() {

                                                        public void onReturn(int code, byte[] data) {
                                                                // TODO Auto-generated method stub
                                                                callback.onResult(request, code, data);
                                                        }
                                                });
                                        }
                                }.start();
    }

}
