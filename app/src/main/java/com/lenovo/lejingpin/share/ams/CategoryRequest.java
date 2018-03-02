package com.lenovo.lejingpin.share.ams;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.lenovo.lejingpin.share.ams.RegistClientInfoRequest.RegistClientInfoResponse;
import com.lenovo.lejingpin.share.net.ToolKit;
import com.lenovo.lsf.util.PsDeviceInfo;

public class CategoryRequest implements AmsRequest {
	private static final String TAG = "zdx";
	private String mTypeCode;
	private int mStartIndex;
	private int mCount;
	private String mIsPay;
	private String mQueryType;
	private Context mContext;
	private String mPackageName;
	private String mTopType;
	private String mVersionCode;
	private String mOrder;
	private String mPromotionId;

	public CategoryRequest(Context context) {
		mContext = context;
	}

	public void setData(int startIndex, int count, String queryType,
			String isPay, String typecode, String packageName,
			String versionCode, String toptype, String order, String promotionid) {
		mStartIndex = startIndex + 1;
		mCount = count;
		mQueryType = queryType;
		if (isPay == null) {
			mIsPay = "";
		} else {
			mIsPay = isPay;
		}
		mTypeCode = typecode;
		mPackageName = packageName;
		mVersionCode = versionCode;
		mTopType = toptype;
		if (order == null) {
			mOrder = "";
		} else {
			mOrder = order;
		}
		mPromotionId = promotionid;
	}

	public String getUrl() {
		// TODO Auto-generated method stub
		String url = AmsSession.sAmsRequestHost + "ams/3.0/getapplist.htm"
				+ "?l=" + PsDeviceInfo.getLanguage(mContext) + "&si="
				+ mStartIndex + "&c=" + mCount + "&qt=" + mQueryType
				+ "&ispay=" + mIsPay + "&pa="
				+ RegistClientInfoResponse.getPa() + "&clientid="
				+ RegistClientInfoResponse.getClientId();
		if (mQueryType.equals(RequestContentType.CLASSIFICATION)
				|| mQueryType.equals(RequestContentType.SPECIAL_SUBJECT)) {
			url = url + "&typecode=" + mTypeCode + "&order=" + mOrder;
		} else if (mQueryType.equals(RequestContentType.HISTORY)
				|| mQueryType.equals(RequestContentType.ATTENTION)) {
			url = url + "&pn=" + mPackageName + "&vc=" + mVersionCode;
		} else if (mQueryType.equals(RequestContentType.RANKING)
				|| mQueryType.equals(RequestContentType.ATTENTION)) {
			url = url + "&tt=" + mTopType;
		} else if (mQueryType.equals(RequestContentType.PROMOTION_PLAN)) {
			url = url + "&typecode=" + mTypeCode + "&proid=" + mPromotionId;
		}
		Log.i(TAG, "CategoryRequest.url:" + url);
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

	public static final class CategoryResponse implements AmsResponse {
		private ArrayList<Application> mApplications = new ArrayList<Application>();
		private ArrayList<Category> mCategories = new ArrayList<Category>();
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

		public Category getItem(int i) {
			return mCategories.get(i);
		}

		public int getItemCount() {
			return mCategories.size();
		}

		public ArrayList<Category> getItemList() {
			return mCategories;
		}

		public boolean isFinish() {
			return mIsFinish;
		}

		public void parseFrom(byte[] bytes) {
			// TODO Auto-generated method stub
			String sCategory = new String(bytes);
			Log.i(TAG, "CategoryRequestReturnJsonData=" + sCategory);
			try {
				JSONObject jsonObject = new JSONObject(sCategory);
				String dataType = jsonObject.getString("datatype");
				if (jsonObject.has("endpage"))
					mIsFinish = jsonObject.getInt("endpage") == 0 ? true
							: false;
				Log.i(TAG, "endpage=" + mIsFinish);
				if (dataType.equals("applist")) {
					JSONArray jsArray = jsonObject.getJSONArray("datalist");
					if (jsArray.length() != 0) {
						for (int i = 0; i < jsArray.length(); i++) {
							JSONObject jsonObject2 = jsArray.getJSONObject(i);
							Application application = new Application();
							application.setTarget(jsonObject2
									.getString("target"));
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
							application.setAppName(jsonObject2
									.getString("appname"));
							application.setIsPay(ToolKit
									.convertErrorData(jsonObject2
											.getString("ispay")));
							application.setDiscount(jsonObject2
									.getString("discount"));
							application.setApp_versioncode(ToolKit
									.convertErrorData(jsonObject2
											.getString("app_versioncode")));
							application.setAddv(ToolKit
									.convertErrorData(jsonObject2
											.getString("addv")));
							mApplications.add(application);
						}
					}
				} else if (dataType.equals("typelist")) {
					JSONArray jsArray = jsonObject.getJSONArray("datalist");
					if (jsArray.length() != 0) {
						for (int i = 0; i < jsArray.length(); i++) {
							JSONObject jsonObject2 = jsArray.getJSONObject(i);
							Category category = new Category();
							category.setTarget(jsonObject2.getString("target"));
							// category.setHasChild(jsonObject2.getString("haschild"));
							category.setType_name(jsonObject2
									.getString("type_name"));
							category.setApp_count(ToolKit
									.convertErrorData(jsonObject2
											.getString("app_count")));
							category.setAppType_id(jsonObject2
									.getString("apptype_id"));
							category.setIcon_addr(jsonObject2
									.getString("icon_addr"));
							category.setTypeCode(jsonObject2
									.getString("type_code"));
							mCategories.add(category);
						}
					}
				} else if (dataType.equals("speciallist")) {
					JSONArray jsArray = jsonObject.getJSONArray("datalist");
					if (jsArray.length() != 0) {
						for (int i = 0; i < jsArray.length(); i++) {
							JSONObject jsonObject2 = jsArray.getJSONObject(i);
							Category category = new Category();
							category.setTarget(jsonObject2.getString("target"));
							category.setHasChild(ToolKit
									.convertErrorData(jsonObject2
											.getString("haschild")));
							category.setAppType_id(jsonObject2
									.getString("special_id"));
							category.setType_name(jsonObject2
									.getString("special_name"));
							category.setIcon_addr(jsonObject2
									.getString("icon_addr"));
							category.setApp_count(ToolKit
									.convertErrorData(jsonObject2
											.getString("app_count")));
							category.setTypeCode(jsonObject2
									.getString("special_code"));
							mCategories.add(category);
						}
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				mIsSuccess = false;
				e.printStackTrace();
			}
		}

	}

	public static final class Category implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1844558768632289299L;
		private String target;
		private String hasChild;
		private String type_name;
		private String app_count;
		private String appType_id;
		private String icon_addr;
		private String typeCode;

		public Category() {
			target = "";
			hasChild = "0";
			type_name = "";
			app_count = "0";
			appType_id = "";
			icon_addr = "";
			typeCode = "";
		}

		public String getTarget() {
			return target;
		}

		public void setTarget(String target) {
			this.target = target;
		}

		public String getHasChild() {
			return hasChild;
		}

		public void setHasChild(String hasChild) {
			this.hasChild = hasChild;
		}

		public String getType_name() {
			return type_name;
		}

		public void setType_name(String type_name) {
			this.type_name = type_name;
		}

		public String getApp_count() {
			return app_count;
		}

		public void setApp_count(String app_count) {
			this.app_count = app_count;
		}

		public String getAppType_id() {
			return appType_id;
		}

		public void setAppType_id(String appType_id) {
			this.appType_id = appType_id;
		}

		public String getIcon_addr() {
			return icon_addr;
		}

		public void setIcon_addr(String icon_addr) {
			this.icon_addr = icon_addr;
		}

		public String getTypeCode() {
			return typeCode;
		}

		public void setTypeCode(String typeCode) {
			this.typeCode = typeCode;
		}
	}

	public static final class Application implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3830870434843595687L;
		private String target;
		private String app_price;
		private String package_name;
		private String app_size;
		private String app_publishdate;
		private String icon_addr;
		private String star_level;
		private String appName;
		private String isPay;
		private String discount;
		private String app_version;
		private String app_versioncode;
		private String appId;
		private String lcaId;
		private String addv;
		private String app_status;
		private String pay_status;
		private String download_count;
		private String comment_count;

		public Application() {
			target = "";
			app_price = "0";
			package_name = "";
			app_size = "0";
			app_publishdate = "0";
			icon_addr = "";
			star_level = "0";
			appName = "";
			isPay = "";
			discount = "";
			app_version = "";
			app_versioncode = "0";
			appId = "";
			lcaId = "";
			addv = "";
			app_status = "";
			pay_status = "";
			download_count = "";
			comment_count = "";
		}

		public String getTarget() {
			return target;
		}

		public void setTarget(String target) {
			this.target = target;
		}

		public String getApp_price() {
			return app_price;
		}

		public void setApp_price(String app_price) {
			this.app_price = app_price;
		}

		public String getPackage_name() {
			return package_name;
		}

		public void setPackage_name(String package_name) {
			this.package_name = package_name;
		}

		public String getApp_size() {
			return app_size;
		}

		public void setApp_size(String app_size) {
			this.app_size = app_size;
		}

		public String getApp_publishdate() {
			return app_publishdate;
		}

		public void setApp_publishdate(String app_publishdate) {
			this.app_publishdate = app_publishdate;
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

		public String getAppName() {
			return appName;
		}

		public void setAppName(String appName) {
			this.appName = appName;
		}

		public String getIsPay() {
			return isPay;
		}

		public void setIsPay(String isPay) {
			this.isPay = isPay;
		}

		public String getDiscount() {
			return discount;
		}

		public void setDiscount(String discount) {
			this.discount = discount;
		}

		public String getApp_version() {
			return app_version;
		}

		public void setApp_version(String app_version) {
			this.app_version = app_version;
		}

		public String getApp_versioncode() {
			return app_versioncode;
		}

		public void setApp_versioncode(String app_versioncode) {
			this.app_versioncode = app_versioncode;
		}

		public String getAppId() {
			return appId;
		}

		public void setAppId(String appId) {
			this.appId = appId;
		}

		public String getLcaId() {
			return lcaId;
		}

		public void setLcaId(String lcaId) {
			this.lcaId = lcaId;
		}

		public String getAddv() {
			return addv;
		}

		public void setAddv(String addv) {
			this.addv = addv;
		}

		public String getApp_status() {
			return app_status;
		}

		public void setApp_status(String app_status) {
			this.app_status = app_status;
		}

		public String getPay_status() {
			return pay_status;
		}

		public void setPay_status(String pay_status) {
			this.pay_status = pay_status;
		}

		public String getDownload_count() {
			return download_count;
		}

		public void setDownload_count(String download_count) {
			this.download_count = download_count;
		}

		public String getComment_count() {
			return comment_count;
		}

		public void setComment_count(String comment_count) {
			this.comment_count = comment_count;
		}

	}

	public static final class RequestContentType {
		public static final String RECOMMEND = "rec";
		public static final String RANKING = "top";
		public static final String LATEST = "new";
		public static final String PROMOTION_PLAN = "prm";
		public static final String HISTORY = "his";
		public static final String ATTENTION = "att";
		public static final String HASDOWNLOAD = "hd";
		public static final String VIP = "v";
		public static final String CLASSIFICATION = "t";
		public static final String HOT_SEARCH = "hs";
		public static final String SPECIAL_SUBJECT = "sp";
		public static final String TOPTYPE_ALL = "all";
		public static final String TOPTYPE_WEEK = "w";
		public static final String TOPTYPE_MONTH = "m";
		public static final int GAME_PARENTID = 1;
		public static final int APP_PARENTID = 2;
		public static final int CONTENT_PARENTID = 3;
		public static final String SHARE_TYPE_EMAIL = "email";
		public static final String SHARE_TYPE_SMS = "sms";
		public static final String GAME_TYPECODE = "yx";
		public static final String CONTENT_TYPECODE = "nr";
		public static final String APP_TYPECODE = "rj";
		public static final String SPECIAL_TYPECODE = "zt";
		public static final String BRAND_TYPECODE = "pp";
		public static final String OVERSEAS_TYPECODE = "hw";
		public static final String SORT_DOWNLOAD_PARTITION = "d";
		public static final String SORT_DATE_PARTITION = "t";
		public static final String SORT_GOOD_PARTITION = "s";
		public static final String SORT_RECOMMAND_PARTITION = "hw";
		public static final String SORT_DOWNLOAD_CATEGORY = "d";
		public static final String SORT_GOOD_CATEGORY = "s";
		public static final String SORT_DATE_CATEGORY = "t";

		public static final String APP_STATUS_PULL_ON = "1";
		public static final String APP_STATUS_PULL_DOWN = "2";
		public static final String APP_PAY_STATUS_PAYING = "W";
		public static final String APP_PAY_STATUS_SUCCESS = "T";
		public static final String APP_PAY_STATUS_FAIL = "F";
		public static final String APP_PAY_STATUS_NOT_EXIST = "N";

		// zdx modify
		public static final String THEME_TYPECODE = "840";
		public static final String LOCK_TYPECODE = "821";
		public static final String WALLPAPER_TYPECODE = "841";
		public static final String SCENE_TYPECODE = "842";
	}

	/*** RK_ID:RK_BUGFIX_171707 AUT:zhanglz1@lenovo.com. DATE:2012-10-30.S ***/
	@Override
	public boolean getIsForDownloadNum() {
		// TODO Auto-generated method stub
		return false;
	}
	/*** RK_ID:RK_BUGFIX_171707 AUT:zhanglz1@lenovo.com. DATE:2012-10-30.E ***/

}
