package com.lenovo.lejingpin.share.ams;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.lenovo.lejingpin.share.ams.RegistClientInfoRequest.RegistClientInfoResponse;
import com.lenovo.lsf.account.PsAuthenServiceL;
import com.lenovo.lsf.util.PsDeviceInfo;

public class GetAppDownLoadUrlRequest implements AmsRequest {
	private Context mContext;
	private String mPackageName;
	private String mVersionCode;
	private final static String TAG = "xujing3";

	public GetAppDownLoadUrlRequest(Context context) {
		mContext = context;
	}

	public void setData(String pkgName, String versionCode) {
		mPackageName = pkgName;
		mVersionCode = versionCode;
	}

	public String getUrl() {
		// TODO Auto-generated method stub
		String url = AmsSession.sAmsRequestHost + "ams/3.0/appdownaddress.do"
				+ "?l=" + PsDeviceInfo.getLanguage(mContext) + "&pn="
				+ mPackageName + "&vc=" + mVersionCode;
		Log.i(TAG, "GetAppDownLoadUrlRequest, url:" + url);
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
			Log.d(TAG, "parseFrom:" + sDownLoad);
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

	/*** RK_ID:RK_BUGFIX_171707 AUT:zhanglz1@lenovo.com. DATE:2012-10-30.S ***/
	@Override
	public boolean getIsForDownloadNum() {
		// TODO Auto-generated method stub
		return false;
	}
	/*** RK_ID:RK_BUGFIX_171707 AUT:zhanglz1@lenovo.com. DATE:2012-10-30.E ***/
}
