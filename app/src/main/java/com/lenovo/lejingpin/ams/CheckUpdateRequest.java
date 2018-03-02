	package com.lenovo.lejingpin.ams;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.lenovo.lejingpin.ams.RegistClientInfoRequest.RegistClientInfoResponse;
import com.lenovo.lsf.util.PsDeviceInfo;

public class CheckUpdateRequest implements AmsRequest {
	private String mPackageName;
	private String mVersionCode;
	private Context mContext;

	public CheckUpdateRequest(Context context) {
		mContext = context;
	}

	public void setData(String packageName, String versionCode) {
		mPackageName = packageName;
		mVersionCode = versionCode;
	}

	public String getUrl() {
		// TODO Auto-generated method stub
		return AmsSession.sAmsRequestHost + "ams/3.0/checkupdate.do" + "?l="
				+ PsDeviceInfo.getLanguage(mContext) + "&pn=" + mPackageName
				+ "&vc=" + mVersionCode + "&dv="
				+ android.os.Build.MANUFACTURER + "&dm="
				+ android.os.Build.MODEL.replace(" ", "%20") + "&os=android"
				+ "&ov=" + android.os.Build.VERSION.RELEASE + "&pa="
				+ RegistClientInfoResponse.getPa();
		// return AmsSession.sAmsRequestHost + "ams/3.0/checkupdate.do" + "?l="
		// + PsDeviceInfo.getLanguage(mContext) + "&pn=" + mPackageName
		// + "&vc=" + mVersionCode + "&dv=lenovo&dm=3GW100&os=Leos&ov=2.0"
		// + "&pa=" + RegistClientInfoResponse.getPa();
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

	public static final class CheckUpdateResponse implements AmsResponse {
		private UpdateInfo mInfo = new UpdateInfo();
		private boolean mIsSuccess = true;

		public boolean getIsSuccess() {
			return mIsSuccess;
		}

		public UpdateInfo getUpdateInfo() {
			return mInfo;
		}

		public void parseFrom(byte[] bytes) {
			// TODO Auto-generated method stub
			String data = new String(bytes);
			Log.i("HawaiiLog", "CheckUpdateResponseJsonData=" + data);
			try {
				JSONObject jsonData = new JSONObject(data);
				mInfo.setUpdate(jsonData.getString("update"));
				if (jsonData.has("level"))
					mInfo.setLevel(jsonData.getString("level"));
				if (jsonData.has("app_versioncode"))
					mInfo.setAppVersioncode(jsonData
							.getString("app_versioncode"));
				if (jsonData.has("app_name"))
					mInfo.setAppName(jsonData.getString("app_name"));
				if (jsonData.has("app_version"))
					mInfo.setAppVersion(jsonData.getString("app_version"));
				if (jsonData.has("update_content"))
					mInfo.setUpdateContent(jsonData.getString("update_content"));
				if (jsonData.has("file_url"))
					mInfo.setFileUrl(jsonData.getString("file_url"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				mInfo = null;
				mIsSuccess = false;
				e.printStackTrace();
			}
		}
	}

	public static final class UpdateInfo implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3622958960493098659L;
		private String update;
		private String level;
		private String appVersionCode;
		private String appName;
		private String appVersion;
		private String updateContent;
		private String fileUrl;

		public UpdateInfo() {
			this.update = "0";
			this.level = "0";
			this.appVersionCode = "0";
			this.appName = "";
			this.appVersion = "";
			this.updateContent = "";
			this.fileUrl = "";
		}

		public String getUpdate() {
			return update;
		}

		public void setUpdate(String update) {
			this.update = update;
		}

		public String getLevel() {
			return level;
		}

		public void setLevel(String level) {
			this.level = level;
		}

		public String getAppVersioncode() {
			return appVersionCode;
		}

		public void setAppVersioncode(String appVersioncode) {
			this.appVersionCode = appVersioncode;
		}

		public String getAppName() {
			return appName;
		}

		public void setAppName(String appName) {
			this.appName = appName;
		}

		public String getAppVersion() {
			return appVersion;
		}

		public void setAppVersion(String appVersion) {
			this.appVersion = appVersion;
		}

		public String getUpdateContent() {
			return updateContent;
		}

		public void setUpdateContent(String updateContent) {
			this.updateContent = updateContent;
		}

		public String getFileUrl() {
			return fileUrl;
		}

		public void setFileUrl(String fileUrl) {
			this.fileUrl = fileUrl;
		}

	}

}
