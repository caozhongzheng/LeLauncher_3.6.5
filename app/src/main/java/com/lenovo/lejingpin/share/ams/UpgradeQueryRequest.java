package com.lenovo.lejingpin.share.ams;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.lenovo.lejingpin.share.ams.CategoryRequest.Application;
import com.lenovo.lejingpin.share.ams.RegistClientInfoRequest.RegistClientInfoResponse;
import com.lenovo.lejingpin.share.net.ToolKit;
import com.lenovo.lsf.util.PsDeviceInfo;

/*
 * LPS-AppStore-API-D-A07
 */
public class UpgradeQueryRequest implements AmsRequest {
	private ArrayList<Application> mQueryUpdataList;
	private Context mContext;

	public UpgradeQueryRequest(Context context) {
		mContext = context;
	}

	public void setData(ArrayList<Application> arrayList) {
		this.mQueryUpdataList = arrayList;
	}

	public String getUrl() {
		// TODO Auto-generated method stub
		return AmsSession.sAmsRequestHost + "ams/3.0/queryupgrade.do";
	}

	public String getPost() {
		// TODO Auto-generated method stub
		String sQueryData = "[";
		if (mQueryUpdataList == null || mQueryUpdataList.size() == 0) {
			String postString = "pa=" + RegistClientInfoResponse.getPa()
					+ "&clientid=" + RegistClientInfoResponse.getClientId()
					+ "&l=" + PsDeviceInfo.getLanguage(mContext) + "&data=[]";

			return postString;
		} else {
			for (int i = 0; i < mQueryUpdataList.size(); i++) {
				Application application = mQueryUpdataList.get(i);
				if (i != mQueryUpdataList.size() - 1) {
					sQueryData = sQueryData + "{\"packagename\":\""
							+ application.getPackage_name()
							+ "\",\"versioncode\":"
							+ application.getApp_versioncode() + "},";
				} else {
					sQueryData = sQueryData + "{\"packagename\":\""
							+ application.getPackage_name()
							+ "\",\"versioncode\":"
							+ application.getApp_versioncode() + "}]";
				}
			}
		}
		return "pa=" + RegistClientInfoResponse.getPa() + "&l="
				+ PsDeviceInfo.getLanguage(mContext) + "&data=" + sQueryData;
	}

	public int getHttpMode() {
		// TODO Auto-generated method stub
		return 1;
	}

	public String getPriority() {
		// TODO Auto-generated method stub
		return "high";
	}

	public static final class UpgradeQueryResponse implements AmsResponse {
		private ArrayList<Application> mApplications = new ArrayList<Application>();
		private ArrayList<Application> mUnExistAppList = new ArrayList<Application>();
		private boolean mIsSuccess = true;

		public boolean getIsSuccess() {
			return mIsSuccess;
		}

		public Application getApplicationItem(int i) {
			return mApplications.get(i);
		}

		public int getApplicationItemCount() {
			return mApplications.size();
		}

		public ArrayList<Application> getApplicationItemList() {
			return mApplications;
		}

		public ArrayList<Application> getUnExistAppList() {
			return mUnExistAppList;
		}

		public void parseFrom(byte[] bytes) {
			// TODO Auto-generated method stub
			String sCategory = new String(bytes);
			Log.i("zdx", "QueryUpgradeRequestReturnJsonData=" + sCategory);
			try {
				JSONObject jsonObject = new JSONObject(sCategory);
				JSONArray jsArray = jsonObject.getJSONArray("dataList");
				if (jsArray.length() != 0) {
					for (int i = 0; i < jsArray.length(); i++) {
						JSONObject jsonObject2 = jsArray.getJSONObject(i);
						Application application = new Application();
						application.setTarget(jsonObject2.getString("target"));
						application.setApp_price(ToolKit
								.convertErrorData(jsonObject2
										.getString("app_price")));
						application.setPackage_name(jsonObject2
								.getString("package_name"));
						application.setApp_size(ToolKit
								.convertErrorData(jsonObject2
										.getString("app_size")));
						application.setApp_version(jsonObject2
								.getString("app_version"));
						application.setIcon_addr(jsonObject2
								.getString("icon_addr"));
						application.setStar_level(ToolKit
								.convertErrorData(jsonObject2
										.getString("star_level")));
						application.setApp_publishdate(ToolKit
								.convertErrorData(jsonObject2
										.getString("app_publishdate")));
						application
								.setAppName(jsonObject2.getString("appname"));
						application.setIsPay(ToolKit
								.convertErrorData(jsonObject2
										.getString("ispay")));
						application.setDiscount(jsonObject2
								.getString("discount"));
						application.setApp_versioncode(ToolKit
								.convertErrorData(jsonObject2
										.getString("app_versioncode")));
						mApplications.add(application);

					}
				}
				JSONArray jsArray1 = jsonObject.getJSONArray("existList");
				if (jsArray1.length() != 0) {
					for (int i = 0; i < jsArray1.length(); i++) {
						JSONObject jsonObject3 = jsArray1.getJSONObject(i);
						Application application = new Application();
						application.setPackage_name(jsonObject3
								.getString("package_name"));
						application.setApp_version(jsonObject3
								.getString("app_versioncode"));
						mUnExistAppList.add(application);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
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
