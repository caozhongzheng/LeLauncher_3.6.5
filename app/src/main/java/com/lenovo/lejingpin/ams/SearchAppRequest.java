package com.lenovo.lejingpin.ams;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.lenovo.lejingpin.ams.CategoryRequest.Application;
import com.lenovo.lejingpin.ams.RegistClientInfoRequest.RegistClientInfoResponse;
import com.lenovo.lejingpin.net.ToolKit;
import com.lenovo.lsf.util.PsDeviceInfo;

public class SearchAppRequest implements AmsRequest {
	private int mStartIndex;
	private int mCount;
	private Context mContext;
	private String mKeyWord;
	private String mOrder;

	public SearchAppRequest(Context context) {
		mContext = context;
	}

	public void setData(int startIndex, int count, String keyword, String order) {
		mStartIndex = startIndex+1;
		mCount = count;
		mKeyWord = keyword;
		if (order == null) {
			mOrder = "";
		} else {
			mOrder = order;
		}
	}

	public String getUrl() {
		// TODO Auto-generated method stub
		String str = "";
		try {
			str = URLEncoder.encode(mKeyWord, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String url = AmsSession.sAmsRequestHost + "ams/3.0/appsearchlist.do"
				+ "?l=" + PsDeviceInfo.getLanguage(mContext) + "&k=" + str
				+ "&si=" + mStartIndex + "&c=" + mCount + "&pa="
				+ RegistClientInfoResponse.getPa() + "&order=" + mOrder;
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

	public static final class SearchAppResponse implements AmsResponse {
		private ArrayList<Application> mApplications = new ArrayList<Application>();
		private boolean mIsFinish = false;
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

		public boolean isFinish() {
			return mIsFinish;
		}

		public void parseFrom(byte[] bytes) {
			// TODO Auto-generated method stub
			String sCategory = new String(bytes);
			Log.i("HawaiiLog", "AppSearchRequestReturnJsonData=" + sCategory);
			try {
				JSONObject jsonObject = new JSONObject(sCategory);
				JSONArray jsArray = jsonObject.getJSONArray("datalist");
				if (jsonObject.has("endpage"))
					mIsFinish = jsonObject.getInt("endpage") == 0 ? true
							: false;
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
						application
								.setAddv(ToolKit.convertErrorData(jsonObject2
										.getString("addv")));
						mApplications.add(application);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				mIsSuccess = false;
				e.printStackTrace();
			}
		}
	}

	public static final class AppSearchOrderType {
		public static final String SORT_DOWNLOAD = "down";
		public static final String SORT_TIME = "time";
		public static final String SORT_STAR = "star";
	}
}
