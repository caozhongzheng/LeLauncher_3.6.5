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

public class AppInfoRequest5 implements AmsRequest {
	private String mPkgName;
	private Context mContext;
	private String mVersionCode;

	public AppInfoRequest5(Context context) {
		mContext = context;
	}

	public void setData(String pkgName, String versionCode) {
		mPkgName = pkgName;
		mVersionCode = versionCode;
	}

	public String getUrl() {
		// TODO Auto-generated method stub
		return AmsSession.sAmsRequestHost + "ams/api/appinfo" 
				+ "?pn="+ mPkgName 
				+ "&vc=" + mVersionCode
				+"&woi=0";
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

	public static final class AppInfoResponse5 implements AmsResponse {
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
			Log.i("HawaiiLog", "AppInfoRequestReturnJsonData5=" + sAppInfo);
			try {
				JSONObject jsobj1 = new JSONObject(sAppInfo);
				if (jsobj1.has("code")) {
					mErrorMsg.setErrorCode(jsobj1.getString("code"));
					mErrorMsg.setErrorMsg(jsobj1.getString("detail"));
					mIsSuccess = false;
				} else {
					JSONObject jsobj = new JSONObject(jsobj1.getString("appInfo"));
//					mAppInfo.setComment_count(ToolKit.convertErrorData(jsobj
//							.getString("comment_count")));
					mAppInfo.setApp_price(ToolKit.convertErrorData(jsobj
							.getString("price")));
					mAppInfo.setApp_versioncode(ToolKit.convertErrorData(jsobj
							.getString("versioncode")));
					//mAppInfo.setExist_testlca(jsobj.getString("exist_testlca"));
					mAppInfo.setPackage_name(jsobj.getString("packageName"));
					mAppInfo.setApp_version(jsobj.getString("version"));
					mAppInfo.setIcon_addr(jsobj.getString("iconAddr"));
					mAppInfo.setStar_level(ToolKit.convertErrorData(jsobj
							.getString("averageStar")));
					mAppInfo.setAuthor(jsobj.getString("developerName"));
					mAppInfo.setAppname(jsobj.getString("name"));
					mAppInfo.setIspay(ToolKit.convertErrorData(jsobj
							.getString("ispay")));
					mAppInfo.setApp_publishdate(ToolKit.convertErrorData(jsobj
							.getString("publishDate")));
					mAppInfo.setHistoryList(mHistoryList);
					mAppInfo.setApp_abstract(jsobj.getString("description"));
					mAppInfo.setApp_size(ToolKit.convertErrorData(jsobj
							.getString("size")));
//					mAppInfo.setAddv(ToolKit.convertErrorData(jsobj
//							.getString("addv")));
					if (jsobj.has("overflow_amount"))
						mAppInfo.setAmount(jsobj.getString("overflow_amount"));
					if (jsobj.has("sms_support"))
						mAppInfo.setSmsSupport(jsobj.getString("sms_support"));
					JSONArray snapArray = jsobj.getJSONArray("snapList");
					if (snapArray.length() != 0) {
						for (int i = 0; i < snapArray.length(); i++) {
							JSONObject jsonObject = snapArray.getJSONObject(i);
							Snapshot snapshot = new Snapshot();
							snapshot.setAppimg_path(jsonObject
									.getString("APPIMG_PATH"));
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
		private String comment_count;
		private String app_price;
		private String app_versioncode;
		private String exist_testlca;
		private String package_name;
		private String app_version;
		private String icon_addr;
		private String star_level;
		private String downloadrate;
		private String author;
		private String appname;
		private String ispay;
		private String app_publishdate;
		private String show_name;
		private String app_abstract;
		private String app_size;
		private ArrayList<Snapshot> snapList;
		private ArrayList<Application> historyList;
		private String typeName;
		private String addv;
		private String amount;
		private String smsSupport;
		private String appAddr;

		public AppInfo() {
			comment_count = "0";
			app_price = "0";
			app_versioncode = "0";
			exist_testlca = "";
			package_name = "";
			app_version = "";
			icon_addr = "";
			star_level = "0";
			downloadrate = "0";
			author = "";
			appname = "";
			ispay = "";
			app_publishdate = "0";
			show_name = "";
			app_abstract = "";
			app_size = "0";
			snapList = new ArrayList<AppInfoRequest5.Snapshot>();
			historyList = new ArrayList<CategoryRequest.Application>();
			typeName = "";
			addv = "0";
			amount = "";
			smsSupport = "";
		}

		public String getComment_count() {
			return comment_count;
		}

		public void setComment_count(String comment_count) {
			this.comment_count = comment_count;
		}

		public String getApp_price() {
			return app_price;
		}

		public void setApp_price(String app_price) {
			this.app_price = app_price;
		}

		public String getApp_versioncode() {
			return app_versioncode;
		}

		public void setApp_versioncode(String app_versioncode) {
			this.app_versioncode = app_versioncode;
		}

		public String getExist_testlca() {
			return exist_testlca;
		}

		public void setExist_testlca(String exist_testlca) {
			this.exist_testlca = exist_testlca;
		}

		public String getPackage_name() {
			return package_name;
		}

		public void setPackage_name(String package_name) {
			this.package_name = package_name;
		}

		public String getApp_version() {
			return app_version;
		}

		public void setApp_version(String app_version) {
			this.app_version = app_version;
		}

		public String getIcon_addr() {
			return icon_addr;
		}

		public void setIcon_addr(String icon_addr) {
			this.icon_addr = icon_addr;
		}

		public String getStar_level() {
			return star_level;
		}

		public void setStar_level(String star_level) {
			this.star_level = star_level;
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

		public void setAppname(String appname) {
			this.appname = appname;
		}

		public String getIspay() {
			return ispay;
		}

		public void setIspay(String ispay) {
			this.ispay = ispay;
		}

		public String getApp_publishdate() {
			return app_publishdate;
		}

		public void setApp_publishdate(String app_publishdate) {
			this.app_publishdate = app_publishdate;
		}

		public String getShow_name() {
			return show_name;
		}

		public void setShow_name(String show_name) {
			this.show_name = show_name;
		}

		public String getApp_abstract() {
			return app_abstract;
		}

		public void setApp_abstract(String app_abstract) {
			this.app_abstract = app_abstract;
		}

		public String getApp_size() {
			return app_size;
		}

		public void setApp_size(String app_size) {
			this.app_size = app_size;
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
		private String appimg_path;

		public Snapshot() {
			appimg_path = "";
		}

		public String getAppimg_path() {
			return appimg_path;
		}

		public void setAppimg_path(String appimg_path) {
			this.appimg_path = appimg_path;
		}
		
		@Override
		public String toString() {
			return appimg_path;
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