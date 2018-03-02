package com.lenovo.lejingpin.ams;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.lenovo.lejingpin.ams.RegistClientInfoRequest.RegistClientInfoResponse;
import com.lenovo.lsf.account.PsAuthenServiceL;
import com.lenovo.lsf.util.PsDeviceInfo;

public class GetAppDownLoadUrlRequest implements AmsRequest {
	private Context mContext;
	private String mPackageName;
	private String mVersionCode;

	public GetAppDownLoadUrlRequest(Context context) {
		mContext = context;
	}

	public void setData(String pkgName, String versionCode) {
		mPackageName = pkgName;
		mVersionCode = versionCode;
	}

	public String getUrl() {
		// TODO Auto-generated method stub
		return AmsSession.sAmsRequestHost + "ams/3.0/appdownaddress.do" + "?l="
				+ PsDeviceInfo.getLanguage(mContext) + "&pn=" + mPackageName
				+ "&vc=" + mVersionCode;
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

	public static class GetAppDownLoadUrlResponse implements AmsResponse {
		private String mDownLoadUrl = "";
		private String mPayBody = "";
		private Context mContext;
		private boolean mIsSuccess = true;

		public boolean getIsSuccess() {
			return mIsSuccess;
		}
		public GetAppDownLoadUrlResponse(Context context) {
			mContext = context;
		}

		public String getDownLoadUrl() {
			return mDownLoadUrl;
		}

		public String getPayBody() {
			return mPayBody;
		}

		public void parseFrom(byte[] bytes) {
			String sDownLoad = new String(bytes);
			Log.i("AppStore", sDownLoad);
			try {
				JSONObject jsonObject = new JSONObject(sDownLoad);
				if (jsonObject.has("downurl")) {
					mDownLoadUrl = jsonObject.getString("downurl");
					mDownLoadUrl += ("&clientid="
							+ RegistClientInfoResponse.getClientId()
							+ "&lpsust=" + PsAuthenServiceL.getStData(mContext,
							AmsRequest.RID, false));
				} else if (jsonObject.has("body")) {
					mPayBody = jsonObject.getString("body");
				}
			} catch (JSONException e) {
				// TODO: handle exception
				mIsSuccess = false;
				e.printStackTrace();
			}
		}
	}
}
