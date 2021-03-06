package com.lenovo.lejingpin.share.ams;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.lenovo.lsf.account.PsAuthenServiceL;
import com.lenovo.lsf.util.PsDeviceInfo;

public class RegistClientInfoRequest implements AmsRequest {
	private Context mContext;
	private int mWidth;
	private int mHeight;

	public RegistClientInfoRequest(Context context) {
		mContext = context;
	}

	public void setData(int width, int height) {
		this.mWidth = width;
		this.mHeight = height;
	}

	public String getUrl() {
		// TODO Auto-generated method stub
		String url = AmsSession.sAmsRequestHost
				+ "ams/3.0/registclientinfo.do"
				+ "?dv="
				+ android.os.Build.MANUFACTURER
				+ "&db="
				+ android.os.Build.BRAND
				+ "&dm="
				// + android.os.Build.MODEL.replace(" ", "%20")
				+ android.os.Build.MODEL.substring(android.os.Build.MODEL
						.lastIndexOf(" ") + 1)
				+ "&l="
				+ PsDeviceInfo.getLanguage(mContext)
				+ "&os=android"
				+ "&ov="
				+ android.os.Build.VERSION.RELEASE
				+ "&ol="
				+ android.os.Build.VERSION.SDK
				+ "&so="
				+ ((TelephonyManager) (mContext
						.getSystemService(Context.TELEPHONY_SERVICE)))
						.getSimOperator() + "&r=" + mWidth + "*" + mHeight
				+ "&dit=" + PsDeviceInfo.getDeviceidType(mContext) + "&di="
				+ PsDeviceInfo.getDeviceId(mContext) + "&cv="
				+ PsDeviceInfo.getAppstoreVersion(mContext) + "&s="
				+ PsDeviceInfo.getSource(mContext) + "&st="
				+ PsAuthenServiceL.getStData(mContext, RID, false);
		Log.i("zdx", "RegistClientInfoRequest.url:" + url);
		return url;
	}

	public String getPost() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getHttpMode() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getPriority() {
		// TODO Auto-generated method stub
		return "high";
	}

	public static final class RegistClientInfoResponse implements AmsResponse {
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
			Log.i("RegistClientiInfoRequest",
					"RegistClientiInfoRequestReturnJsonData=" + sClientMes);
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
		/**
		 * 
		 */
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

	/*** RK_ID:RK_BUGFIX_171707 AUT:zhanglz1@lenovo.com. DATE:2012-10-30.S ***/
	@Override
	public boolean getIsForDownloadNum() {
		// TODO Auto-generated method stub
		return false;
	}
	/*** RK_ID:RK_BUGFIX_171707 AUT:zhanglz1@lenovo.com. DATE:2012-10-30.E ***/
}
