package com.lenovo.lejingpin.network;

import java.io.Serializable;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.util.Log;

import com.lenovo.lejingpin.network.RegistClientInfoRequest5.RegistClientInfoResponse5;
import com.lenovo.lejingpin.network.ToolKit;

/*
 * LPS-AppStore-API-D-A49(ams5.0)
 */
public class AppInfoRequest5 implements AmsRequest {
	private static final String TAG = "zdx";
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
		String url = AmsSession.sAmsRequestHost 
				+ "ams/api/appinfo" 
		        + "?pn=" + mPkgName 
				+ "&vc=" + mVersionCode
				+ "&woi=0"
				+ "&pa=" + RegistClientInfoResponse5.getPa()
				+ "&clientid=" + RegistClientInfoResponse5.getClientId();
		Log.i("zdx","AppInfoRequest5.url:"+ url);
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

	public static final class AppInfoResponse5 implements AmsResponse {
		private ArrayList<Snapshot> mArrayList;
		private boolean mIsSuccess = true;
		public boolean getIsSuccess() {
			return mIsSuccess;
		}

		private AppInfo mAppInfo = new AppInfo();

		public AppInfo getAppInfo() {
			return mAppInfo;
		}

		public void parseFrom(byte[] bytes) {
			// TODO Auto-generated method stub
			String sAppInfo = new String(bytes);
			mArrayList = new ArrayList<Snapshot>();
			Log.i(TAG, "AppInfoResponse5.parseFrom, Return JsonData=" + sAppInfo);
			try {
				    JSONObject jsobj2 = new JSONObject(sAppInfo);
				    JSONObject jsobj = new JSONObject(jsobj2.getString("appInfo"));
				    mAppInfo.setComment_count(ToolKit.convertErrorData(jsobj
							.getString("commentsNum")));
					mAppInfo.setApp_price(ToolKit.convertErrorData(jsobj
							.getString("price")));
					mAppInfo.setApp_versioncode(ToolKit.convertErrorData(jsobj
							.getString("versioncode")));
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
					mAppInfo.setApp_abstract(jsobj.getString("description"));
					mAppInfo.setApp_size(ToolKit.convertErrorData(jsobj
							.getString("size")));
					JSONArray snapArray = jsobj.getJSONArray("snapList");
					for (int k = 0; k < snapArray.length(); k++) {
						JSONObject jsonObject = snapArray.getJSONObject(k);
						Snapshot snapshot = new Snapshot();
						snapshot.setAppimg_path(jsonObject.getString("APPIMG_PATH"));
						mArrayList.add(snapshot);
					}
					mAppInfo.setSnapList(mArrayList);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				mAppInfo = null;
				mIsSuccess = false;
				e.printStackTrace();
			}
		}
	}

	public static final class AppInfo implements Serializable {
		private static final long serialVersionUID = -2849118180085223494L;
		private String comment_count;
		private String app_price;
		private String app_versioncode;
		private String package_name;
		private String app_version;
		private String icon_addr;
		private String star_level;
		private String author;
		private String appname;
		private String ispay;
		private String app_publishdate;
		private String app_abstract;
		private String app_size;
		private ArrayList<Snapshot> snapList;

		public AppInfo() {
			comment_count = "0";
			app_price = "0";
			app_versioncode = "0";
			package_name = "";
			app_version = "";
			icon_addr = "";
			star_level = "0";
			author = "";
			appname = "";
			ispay = "";
			app_publishdate = "0";
			app_abstract = "";
			app_size = "0";
			snapList = new ArrayList<AppInfoRequest5.Snapshot>();
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
	}

	public static final class Snapshot implements Serializable {
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
	/***RK_ID:RK_BUGFIX_171707 AUT:zhanglz1@lenovo.com. DATE:2012-10-30.S***/       
	@Override
	public boolean getIsForDownloadNum() {
		// TODO Auto-generated method stub
		return false;
	}
	/***RK_ID:RK_BUGFIX_171707 AUT:zhanglz1@lenovo.com. DATE:2012-10-30.E***/  
}
