package com.lenovo.lejingpin.network;

import java.io.Serializable;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import android.content.Context;
import android.util.Log;
import com.lenovo.lsf.util.PsDeviceInfo;

/*
 * LPS-AppStore-API-D-A36(ams5.0)
 */
public class RegistClientInfoRequest5 implements AmsRequest {
	private Context mContext;
	private int mWidth ;
	private int mHeight ;
	private int mDensityDpi;

	public RegistClientInfoRequest5(Context context) {
		mContext = context;
	}

	public void setData(int width, int height, int densityDpi) {
		this.mWidth = width;
		this.mHeight = height;
		this.mDensityDpi = densityDpi;
	}

	public String getUrl() {
		// TODO Auto-generated method stub
		return AmsSession.sAmsRequestHost + "ams/api/register";
	}

	public String getPost() {
		// TODO Auto-generated method stub
		String post = null;
        try {
            post = new JSONStringer().object()
                    .key("deviceManufacturer").value("lenovo"/*android.os.Build.MANUFACTURER*/)
                    .key("deviceBrand").value(android.os.Build.BRAND)
                    .key("deviceModel").value(android.os.Build.MODEL)
                    .key("lang").value("zh-CN")
                    .key("os").value("android")
                    .key("osVersion").value(android.os.Build.VERSION.RELEASE)
                    .key("sdkVersion").value(android.os.Build.VERSION.SDK)
                    .key("horizontalResolution").value(mWidth)
                    .key("verticalResolution").value(mHeight)
                    .key("dpi").value(mDensityDpi)
                    .key("deviceIdType").value("imei")
                    .key("deviceId").value(PsDeviceInfo.getDeviceId(mContext))
                    .key("clientVersion").value(PsDeviceInfo.getAppstoreVersion(mContext))
                    .key("packageName").value(PsDeviceInfo.getSource(mContext))
                    .endObject().toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("zdx", "post = " + post);
        return post;
	}

	public int getHttpMode() {
		// TODO Auto-generated method stub
		return 1;
	}

	public String getPriority() {
		// TODO Auto-generated method stub
		return "high";
	}

	public static final class RegistClientInfoResponse5 implements AmsResponse {
		private static ClientInfo sClientInfo = new ClientInfo();;
		private boolean mIsSuccess = true;

		public boolean getIsSuccess() {
			return mIsSuccess;
		}

		public static ClientInfo getClientInfo() {
			return sClientInfo;
		}

		public static String getPa() {
			return sClientInfo.getPa();
		}

		public static String getClientId() {
			return sClientInfo.getClientId();
		}

		public static String getError() {
			return sClientInfo.getError();
		}

		public void parseFrom(byte[] bytes) {
			// TODO Auto-generated method stub
			String sClientMes = new String(bytes);
			Log.i("zdx", "RegistClientiInfoRequest, ReturnJsonData="
					+ sClientMes);
			try {
				JSONObject jsonObject = new JSONObject(sClientMes);
				if (jsonObject.has("clientid")) {
					sClientInfo.setClientId(jsonObject.getString("clientid"));
					sClientInfo.setPa(jsonObject.getString("pa").replace(" ",
							"%20"));
				} else if (jsonObject.has("error")) {
					sClientInfo.setError(jsonObject.getString("error"));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				mIsSuccess = false;
				e.printStackTrace();
			}
		}
	}

	public static final class ClientInfo implements Serializable {
		private static final long serialVersionUID = -238155975566085485L;
		private String clientId;
		private String pa;
		private String error;

		public String getClientId() {
			return clientId;
		}

		public void setClientId(String clientId) {
			this.clientId = clientId;
		}

		public String getPa() {
			return pa;
		}

		public void setPa(String pa) {
			this.pa = pa;
		}

		public String getError() {
			return error;
		}

		public void setError(String error) {
			this.error = error;
		}

	}

	/***RK_ID:RK_BUGFIX_171707 AUT:zhanglz1@lenovo.com. DATE:2012-10-30.S***/       
	@Override
	public boolean getIsForDownloadNum() {
		// TODO Auto-generated method stub
		return false;
	}
	/***RK_ID:RK_BUGFIX_171707 AUT:zhanglz1@lenovo.com. DATE:2012-10-30.E***/  
}
