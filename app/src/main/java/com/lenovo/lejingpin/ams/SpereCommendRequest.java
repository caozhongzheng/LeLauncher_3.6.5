package com.lenovo.lejingpin.ams;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.lenovo.lejingpin.ams.RegistClientInfoRequest.RegistClientInfoResponse;
import com.lenovo.lejingpin.net.ToolKit;
import com.lenovo.lsf.util.PsDeviceInfo;

public class SpereCommendRequest implements AmsRequest {
	private int mStartIndex;
	private int mCount;
	private String mQueryType;
	private String mIsPay;
	private Context mContext;

	public SpereCommendRequest(Context context){
		mContext = context;
	}
	
	public void setData(int startIndex,int count,String type,boolean isPay){
		mStartIndex = startIndex +1;
		mCount = count;
		mQueryType = type;
		if(isPay){
			mIsPay = "true";
		}else{
			mIsPay = "false";
		}
	}
	public String getUrl() {
		 TelephonyManager telephonyManager = (TelephonyManager)mContext.getSystemService("phone");
		 String deviceId = telephonyManager.getDeviceId();
		 
		String url = AmsSession.sAmsRequestHost + "ams/3.0/getsperecommendapplist.htm"// getsperecommendapplist - 
					+ "?l=" + PsDeviceInfo.getLanguage(mContext) + "&si="
					+ String.valueOf(mStartIndex) + "&c=" + String.valueOf(mCount) + "&qt=" + mQueryType
					+ "&ispay=" + mIsPay + "&pa="
					+ RegistClientInfoResponse.getPa()
					+"&did="+deviceId;
		Log.i("SpereCommendResponse", "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++url=" + url);
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
	
	public static final class SpereCommendResponse implements AmsResponse {
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
			String sCategory = new String(bytes);
			
			Log.i("SpereCommendResponse", "++++++++++++++++++++++SpereCommendRequestReturnJsonData=" + sCategory+"=********");
			try{
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
							application.setAppPrice(ToolKit
									.convertErrorData(jsonObject2
											.getString("app_price")));
							application.setPackageName(jsonObject2
									.getString("package_name"));
							application.setAppSize(ToolKit
									.convertErrorData(jsonObject2
											.getString("app_size")));
							application.setAppVersion(jsonObject2
									.getString("app_version"));
							application.setIconAddr(jsonObject2
									.getString("icon_addr"));
							application.setStarLevel(ToolKit
									.convertErrorData(jsonObject2
											.getString("star_level")));
							application.setAppPublishDate(ToolKit
									.convertErrorData(jsonObject2
											.getString("app_publishdate")));
							application.setAppName(jsonObject2
									.getString("appname"));
							application.setIsPay(ToolKit
									.convertErrorData(jsonObject2
											.getString("ispay")));
							application.setDiscount(jsonObject2
									.getString("discount"));
							application.setAppVersionCode(ToolKit
									.convertErrorData(jsonObject2
											.getString("app_versioncode")));
							application.setAddv(ToolKit
									.convertErrorData(jsonObject2
											.getString("addv")));
							application.setLcaId(jsonObject2.getString("lcaid"));
							boolean isContainDataSource = jsonObject2.has("data_source");
							if(isContainDataSource){
								application.setDataSource(jsonObject2.getString("data_source"));
							}
							mApplications.add(application);
						}
					}
				}
			}catch(Exception e){
				mIsSuccess = false;
				e.printStackTrace();
			}
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
		private String appVersionCode;
		private String appId;
		private String lcaId;
		private String addv;
		private String appStatus;
		private String payStatus;
		private String downloadCount;
		private String commentCount;
		private String dataSource;

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
			appVersionCode = "0";
			appId = "";
			lcaId = "";
			addv = "";
			appStatus = "";
			payStatus = "";
			downloadCount = "";
			commentCount = "";
			dataSource = "";
		}

		public String getTarget() {
			return target;
		}

		public void setTarget(String target) {
			this.target = target;
		}

		public String getAppPrice() {
			return appPrice;
		}

		public void setAppPrice(String appPrice) {
			this.appPrice = appPrice;
		}

		public String getPackageName() {
			return packageName;
		}

		public void setPackageName(String packageName) {
			this.packageName = packageName;
		}

		public String getAppSize() {
			return appSize;
		}

		public void setAppSize(String appSize) {
			this.appSize = appSize;
		}

		public String getAppPublishdate() {
			return appPublishDate;
		}

		public void setAppPublishDate(String appPublishDate) {
			this.appPublishDate = appPublishDate;
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

		public String getAppVersion() {
			return appVersion;
		}

		public void setAppVersion(String appVersion) {
			this.appVersion = appVersion;
		}

		public String getAppVersionCode() {
			return appVersionCode;
		}

		public void setAppVersionCode(String appVersionCode) {
			this.appVersionCode = appVersionCode;
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

		public String getAppStatus() {
			return appStatus;
		}

		public void setAppStatus(String appStatus) {
			this.appStatus = appStatus;
		}

		public String getPayStatus() {
			return payStatus;
		}

		public void setPayStatus(String payStatus) {
			this.payStatus = payStatus;
		}

		public String getDownloadCount() {
			return downloadCount;
		}

		public void setDownloadCount(String downloadCount) {
			this.downloadCount = downloadCount;
		}

		public String getCommentCount() {
			return commentCount;
		}

		public void setCommentCount(String commentCount) {
			this.commentCount = commentCount;
		}

		public String getDataSource() {
			return dataSource;
		}

		public void setDataSource(String dataSource) {
			this.dataSource = dataSource;
		}
		
		

	}

}
