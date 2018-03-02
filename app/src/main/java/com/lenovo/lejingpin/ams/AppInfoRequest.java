package com.lenovo.lejingpin.ams;

import java.io.Serializable;
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

public class AppInfoRequest implements AmsRequest {
	private String mPkgName;
	private Context mContext;
	private String mVersionCode;

	public AppInfoRequest(Context context) {
		mContext = context;
	}

	public void setData(String pkgName, String versionCode) {
		mPkgName = pkgName;
		mVersionCode = versionCode;
	}

	public String getUrl() {
		// TODO Auto-generated method stub
		return AmsSession.sAmsRequestHost + "ams/3.0/queryappinfo.htm" + "?pn="
				+ mPkgName + "&l=" + PsDeviceInfo.getLanguage(mContext)
				+ "&vc=" + mVersionCode + "&pa="
				+ RegistClientInfoResponse.getPa();
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

	public static final class AppInfoResponse implements AmsResponse {
		private ArrayList<Snapshot> mArrayList;
		private ArrayList<Application> mHistoryList;
		private boolean mIsSuccess = true;
		private AmsErrorMsg mErrorMsg = new AmsErrorMsg();

		public boolean getIsSuccess() {
			return mIsSuccess;
		}

		public AmsErrorMsg getErrorMsg() {
			return mErrorMsg;
		}

		private AppInfo mAppInfo = new AppInfo();

		public AppInfo getAppInfo() {
			return mAppInfo;
		}

		public void parseFrom(byte[] bytes) {
			// TODO Auto-generated method stub
			String sAppInfo = new String(bytes);
			mArrayList = new ArrayList<Snapshot>();
			mHistoryList = new ArrayList<Application>();
			Log.i("HawaiiLog", "AppInfoRequestReturnJsonData=" + sAppInfo);
			try {
				JSONObject jsobj1 = new JSONObject(sAppInfo);
				if (jsobj1.has("code")) {
					mErrorMsg.setErrorCode(jsobj1.getString("code"));
					mErrorMsg.setErrorMsg(jsobj1.getString("detail"));
					mIsSuccess = false;
				} else {
					JSONObject jsobj = new JSONObject(jsobj1.getString("data"));
					mAppInfo.setCommentCount(ToolKit.convertErrorData(jsobj
							.getString("comment_count")));
					mAppInfo.setAppPrice(ToolKit.convertErrorData(jsobj
							.getString("app_price")));
					mAppInfo.setAppVersionCode(ToolKit.convertErrorData(jsobj
							.getString("app_versioncode")));
					mAppInfo.setExistTestlca(jsobj.getString("exist_testlca"));
					mAppInfo.setPackageName(jsobj.getString("package_name"));
					mAppInfo.setAppVersion(jsobj.getString("app_version"));
					mAppInfo.setIconAddr(jsobj.getString("icon_addr"));
					mAppInfo.setStarLevel(ToolKit.convertErrorData(jsobj
							.getString("star_level")));
					mAppInfo.setDownloadrate(ToolKit.convertErrorData(jsobj
							.getString("downloadrate")));
					mAppInfo.setAuthor(jsobj.getString("author"));
					mAppInfo.setAppName(jsobj.getString("appname"));
					mAppInfo.setIspay(ToolKit.convertErrorData(jsobj
							.getString("ispay")));
					mAppInfo.setAppPublishDate(ToolKit.convertErrorData(jsobj
							.getString("app_publishdate")));
					mAppInfo.setShowName(jsobj.getString("show_name"));
					mAppInfo.setTypeName(jsobj.getString("type_name"));
					mAppInfo.setAppAddr(jsobj.getString("app_addr"));
					mAppInfo.setDownloadCount(jsobj.getString("download_count"));
					JSONArray historyArray = jsobj
							.getJSONArray("history_version");
					if (historyArray.length() != 0) {
						for (int i = 0; i < historyArray.length(); i++) {
							JSONObject jsonObject = historyArray
									.getJSONObject(i);
							Application application = new Application();
							application.setTarget(jsonObject
									.getString("target"));
							application.setApp_price(ToolKit
									.convertErrorData(jsonObject
											.getString("app_price")));
							application.setPackage_name(jsonObject
									.getString("package_name"));
							application.setApp_size(ToolKit
									.convertErrorData(jsonObject
											.getString("app_size")));
							application.setApp_version(jsonObject
									.getString("app_version"));
							application.setIcon_addr(jsonObject
									.getString("icon_addr"));
							application.setStar_level(ToolKit
									.convertErrorData(jsonObject
											.getString("star_level")));
							application.setApp_publishdate(ToolKit
									.convertErrorData(jsonObject
											.getString("app_publishdate")));
							application.setAppName(jsonObject
									.getString("appname"));
							application.setIsPay(ToolKit
									.convertErrorData(jsonObject
											.getString("ispay")));
							application.setDiscount(jsonObject
									.getString("discount"));
							application.setApp_versioncode(ToolKit
									.convertErrorData(jsonObject
											.getString("app_versioncode")));
							application.setAddv(ToolKit
									.convertErrorData(jsonObject
											.getString("addv")));
							mHistoryList.add(application);
						}
					}
					mAppInfo.setHistoryList(mHistoryList);
					mAppInfo.setAppAbstract(jsobj.getString("app_abstract"));
					mAppInfo.setAppSize(ToolKit.convertErrorData(jsobj
							.getString("app_size")));
					mAppInfo.setAddv(ToolKit.convertErrorData(jsobj
							.getString("addv")));
					if (jsobj.has("overflow_amount"))
						mAppInfo.setAmount(jsobj.getString("overflow_amount"));
					if (jsobj.has("sms_support"))
						mAppInfo.setSmsSupport(jsobj.getString("sms_support"));
					JSONArray snapArray = jsobj.getJSONArray("snaplist");
					if (snapArray.length() != 0) {
						for (int i = 0; i < snapArray.length(); i++) {
							JSONObject jsonObject = snapArray.getJSONObject(i);
							Snapshot snapshot = new Snapshot();
							snapshot.setAppimgPath(jsonObject
									.getString("appimg_path"));
							mArrayList.add(snapshot);
						}
					}
					mAppInfo.setSnapList(mArrayList);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				mAppInfo = null;
				mIsSuccess = false;
				e.printStackTrace();
			}
		}
	}

	public static final class AppInfo implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2849118180085223494L;
		private String commentCount;
		private String appPrice;
		private String appVersionCode;
		private String existTestlca;
		private String packageName;
		private String appVersion;
		private String iconAddr;
		private String starLevel;
		private String downloadrate;
		private String author;
		private String appname;
		private String ispay;
		private String appPublishDate;
		private String showName;
		private String appAbstract;
		private String appSize;
		private ArrayList<Snapshot> snapList;
		private ArrayList<Application> historyList;
		private String typeName;
		private String addv;
		private String amount;
		private String smsSupport;
		private String appAddr;
		private String downloadCount;

		public AppInfo() {
			commentCount = "0";
			appPrice = "0";
			appVersionCode = "0";
			existTestlca = "";
			packageName = "";
			appVersion = "";
			iconAddr = "";
			starLevel = "0";
			downloadrate = "0";
			author = "";
			appname = "";
			ispay = "";
			appPublishDate = "0";
			showName = "";
			appAbstract = "";
			appSize = "0";
			snapList = new ArrayList<AppInfoRequest.Snapshot>();
			historyList = new ArrayList<CategoryRequest.Application>();
			typeName = "";
			addv = "0";
			amount = "";
			smsSupport = "";
			downloadCount = "0";
		}

		public String getCommentCount() {
			return commentCount;
		}

		public void setCommentCount(String commentCount) {
			this.commentCount = commentCount;
		}


		public String getDownloadCount() {
			return downloadCount;
		}

		public void setDownloadCount(String downloadCount) {
			this.downloadCount = downloadCount;
		}

		public String getAppPrice() {
			return appPrice;
		}

		public void setAppPrice(String appPrice) {
			this.appPrice = appPrice;
		}

		public String getAppVersionCode() {
			return appVersionCode;
		}

		public void setAppVersionCode(String appVersionCode) {
			this.appVersionCode = appVersionCode;
		}

		public String getExistTestlca() {
			return existTestlca;
		}

		public void setExistTestlca(String existTestlca) {
			this.existTestlca = existTestlca;
		}

		public String getPackageName() {
			return packageName;
		}

		public void setPackageName(String packageName) {
			this.packageName = packageName;
		}

		public String getAppVersion() {
			return appVersion;
		}

		public void setAppVersion(String appVersion) {
			this.appVersion = appVersion;
		}

		public String getIconAddr() {
			return iconAddr;
		}

		public void setIconAddr(String iconAddr) {
			this.iconAddr = iconAddr;
		}

		public String getStarLevel() {
			return starLevel;
		}

		public void setStarLevel(String starLevel) {
			this.starLevel = starLevel;
		}

		public String getDownloadrate() {
			return downloadrate;
		}

		public void setDownloadrate(String downloadrate) {
			this.downloadrate = downloadrate;
		}

		public String getAuthor() {
			return author;
		}

		public void setAuthor(String author) {
			this.author = author;
		}

		public String getAppname() {
			return appname;
		}

		public void setAppName(String appName) {
			this.appname = appName;
		}

		public String getIspay() {
			return ispay;
		}

		public void setIspay(String ispay) {
			this.ispay = ispay;
		}

		public String getAppPublishDate() {
			return appPublishDate;
		}

		public void setAppPublishDate(String appPublishDate) {
			this.appPublishDate = appPublishDate;
		}

		public String getShowName() {
			return showName;
		}

		public void setShowName(String showName) {
			this.showName = showName;
		}

		public String getAppAbstract() {
			return appAbstract;
		}

		public void setAppAbstract(String appAbstract) {
			this.appAbstract = appAbstract;
		}

		public String getAppSize() {
			return appSize;
		}

		public void setAppSize(String appSize) {
			this.appSize = appSize;
		}

		public ArrayList<Snapshot> getSnapList() {
			return snapList;
		}

		public void setSnapList(ArrayList<Snapshot> snapList) {
			this.snapList = snapList;
		}

		public ArrayList<Application> getHistoryList() {
			return historyList;
		}

		public void setHistoryList(ArrayList<Application> historyList) {
			this.historyList = historyList;
		}

		public String getTypeName() {
			return typeName;
		}

		public void setTypeName(String typeName) {
			this.typeName = typeName;
		}

		public String getAddv() {
			return addv;
		}

		public void setAddv(String addv) {
			this.addv = addv;
		}

		public String getAmount() {
			return amount;
		}

		public void setAmount(String amount) {
			this.amount = amount;
		}

		public String getSmsSupport() {
			return smsSupport;
		}

		public void setSmsSupport(String smsSupport) {
			this.smsSupport = smsSupport;
		}

		public String getAppAddr() {
			return appAddr;
		}

		public void setAppAddr(String appAddr) {
			this.appAddr = appAddr;
		}
		
		

	}

	public static final class Snapshot implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2411364007052038474L;
		private String appimgPath;

		public Snapshot() {
			appimgPath = "";
		}

		public String getAppimgPath() {
			return appimgPath;
		}

		public void setAppimgPath(String appimgPath) {
			this.appimgPath = appimgPath;
		}
		
		@Override
		public String toString() {
			return appimgPath;
		}
	}

	public static final class AmsErrorMsg implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3722992473276039361L;
		private String errorCode;
		private String errorMsg;

		public String getErrorCode() {
			return errorCode;
		}

		public void setErrorCode(String errorCode) {
			this.errorCode = errorCode;
		}

		public String getErrorMsg() {
			return errorMsg;
		}

		public void setErrorMsg(String errorMsg) {
			this.errorMsg = errorMsg;
		}

	}
}