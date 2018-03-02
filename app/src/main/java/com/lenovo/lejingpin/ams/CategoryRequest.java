package com.lenovo.lejingpin.ams;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.lenovo.lejingpin.ams.RegistClientInfoRequest.RegistClientInfoResponse;
import com.lenovo.lejingpin.net.ToolKit;
import com.lenovo.lsf.util.PsDeviceInfo;

public class CategoryRequest implements AmsRequest {
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
				+ RegistClientInfoResponse.getPa();
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

		public int getApplicationItemCount(){
			if(mApplications!=null){
				return mApplications.size();
			}else{
				return 0;
			}
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
			Log.i("HawaiiLog", "CategoryRequestReturnJsonData=" + sCategory);
			try {
				JSONObject jsonObject = new JSONObject(sCategory);
				String dataType = jsonObject.getString("datatype");
				if (jsonObject.has("endpage"))
					mIsFinish = jsonObject.getInt("endpage") == 0 ? true
							: false;
				Log.i("HawaiiLog", "endpage=" + mIsFinish);
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
							category.setTypeName(jsonObject2
									.getString("type_name"));
							category.setAppCount(ToolKit
									.convertErrorData(jsonObject2
											.getString("app_count")));
							category.setAppTypeId(jsonObject2
									.getString("apptype_id"));
							category.setIconAddr(jsonObject2
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
							category.setAppTypeId(jsonObject2
									.getString("special_id"));
							category.setTypeName(jsonObject2
									.getString("special_name"));
							category.setIconAddr(jsonObject2
									.getString("icon_addr"));
							category.setAppCount(ToolKit
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
		private String typeName;
		private String appCount;
		private String appTypeId;
		private String iconAddr;
		private String typeCode;

		public Category() {
			target = "";
			hasChild = "0";
			typeName = "";
			appCount = "0";
			appTypeId = "";
			iconAddr = "";
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

		public String getTypeName() {
			return typeName;
		}

		public void setTypeName(String typeName) {
			this.typeName = typeName;
		}

		public String getAppCount() {
			return appCount;
		}

		public void setAppCount(String appCount) {
			this.appCount = appCount;
		}

		public String getAppTypeId() {
			return appTypeId;
		}

		public void setAppTypeId(String appTypeId) {
			this.appTypeId = appTypeId;
		}

		public String getIconAddr() {
			return iconAddr;
		}

		public void setIconAddr(String iconAddr) {
			this.iconAddr = iconAddr;
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
		private String appPrice;
		private String packageName;
		private String appSize;
		private String appPublishDate;
		private String iconAddr;
		private String starLevel;
		private String appName;
		private String isPay;
		private String discount;
		private String appVersion;
		private String appVersioncode;
		private String appId;
		private String lcaId;
		private String addv;
		private String appStatus;
		private String payStatus;
		private String downloadCount;
		private String commentCount;

		public Application() {
			target = "";
			appPrice = "0";
			packageName = "";
			appSize = "0";
			appPublishDate = "0";
			iconAddr = "";
			starLevel = "0";
			appName = "";
			isPay = "";
			discount = "";
			appVersion = "";
			appVersioncode = "0";
			appId = "";
			lcaId = "";
			addv = "";
			appStatus = "";
			payStatus = "";
			downloadCount = "";
			commentCount = "";
		}

		public String getTarget() {
			return target;
		}

		public void setTarget(String target) {
			this.target = target;
		}

		public String getApp_price() {
			return appPrice;
		}

		public void setApp_price(String app_price) {
			this.appPrice = app_price;
		}

		public String getPackageName() {
			return packageName;
		}

		public void setPackage_name(String package_name) {
			this.packageName = package_name;
		}

		public String getApp_size() {
			return appSize;
		}

		public void setApp_size(String app_size) {
			this.appSize = app_size;
		}

		public String getApp_publishdate() {
			return appPublishDate;
		}

		public void setApp_publishdate(String app_publishdate) {
			this.appPublishDate = app_publishdate;
		}

		public String getIcon_addr() {
			return iconAddr;
		}

		public void setIcon_addr(String icon_addr) {
			this.iconAddr = icon_addr;
		}

		public String getStar_level() {
			return starLevel;
		}

		public void setStar_level(String star_level) {
			this.starLevel = star_level;
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
			return appVersion;
		}

		public void setApp_version(String app_version) {
			this.appVersion = app_version;
		}

		public String getAppVersionCode() {
			return appVersioncode;
		}

		public void setApp_versioncode(String app_versioncode) {
			this.appVersioncode = app_versioncode;
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
			return appStatus;
		}

		public void setApp_status(String app_status) {
			this.appStatus = app_status;
		}

		public String getPay_status() {
			return payStatus;
		}

		public void setPay_status(String pay_status) {
			this.payStatus = pay_status;
		}

		public String getDownload_count() {
			return downloadCount;
		}

		public void setDownload_count(String download_count) {
			this.downloadCount = download_count;
		}

		public String getComment_count() {
			return commentCount;
		}

		public void setComment_count(String comment_count) {
			this.commentCount = comment_count;
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
	}

}
