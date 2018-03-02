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

public class SpereCommendRequest5 implements AmsRequest {
	private int mStartIndex;
	private int mCount;
	private String mQueryType;
	private String mIsPay;
	private Context mContext;

	public SpereCommendRequest5(Context context){
		mContext = context;
	}
	
	public void setData(int startIndex,int count,String type,boolean isPay){
		mStartIndex = startIndex +1;
		mCount = count;
		mQueryType = type;
		if(isPay){
			mIsPay = "paid";
		}else{
			mIsPay = "free";
		}
	}
	public String getUrl() {
		 //TelephonyManager telephonyManager = (TelephonyManager)this.mContext.getSystemService("phone");
		// String deviceId = telephonyManager.getDeviceId();
		 
		 String lelauncherUrl = AmsSession5.sLeLauncherHost+"recommender";
		// String amsUrl = AmsSession5.sAmsRequestHost + "ams/api/recommender";
		String url = lelauncherUrl
					+ "?si="+ String.valueOf(mStartIndex) 
					+ "&c=" + String.valueOf(mCount) 
					+ "&qt=" + mQueryType
					+ "&p=" + mIsPay 
					+ "&woi=0";
		Log.i("SpereCommendResponse5", "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++url=" + url);
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
	
	public static final class SpereCommendResponse5 implements AmsResponse {
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
			
			Log.i("SpereCommendResponse5", "++++++++++++++++++++++SpereCommendResponse5JSONData=" + sCategory+"=********");
			try{
				JSONObject jsonObject = new JSONObject(sCategory);
				String dataType = jsonObject.getString("datatype");
				if (jsonObject.has("endpage"))
					mIsFinish = jsonObject.getInt("endpage") == 0 ? true
							: false;
				Log.i("HawaiiLog", "endpage=" + mIsFinish);
				if (dataType.equals("applist")) {
					JSONArray jsArray = jsonObject.getJSONArray("dataList");
					if (jsArray.length() != 0) {
						for (int i = 0; i < jsArray.length(); i++) {
							JSONObject jsonObject2 = jsArray.getJSONObject(i);
							Application application = new Application();
//							application.setTarget(jsonObject2
//									.getString("target"));
							application.setApp_price(ToolKit
									.convertErrorData(jsonObject2
											.getString("price")));
							application.setPackage_name(jsonObject2
									.getString("packageName"));
							application.setApp_size(ToolKit
									.convertErrorData(jsonObject2
											.getString("size")));
							application.setApp_version(jsonObject2
									.getString("version"));
							application.setIcon_addr(jsonObject2
									.getString("iconAddr"));
							application.setStar_level(ToolKit
									.convertErrorData(jsonObject2
											.getString("averageStar")));
							application.setApp_publishdate(ToolKit
									.convertErrorData(jsonObject2
											.getString("publishDate")));
							application.setAppName(jsonObject2
									.getString("name"));
							application.setIsPay(ToolKit
									.convertErrorData(jsonObject2
											.getString("ispay")));
							application.setDiscount(jsonObject2
									.getString("discount"));
							application.setApp_versioncode(ToolKit
									.convertErrorData(jsonObject2
											.getString("versioncode")));
							
//							application.setAddv(ToolKit
//									.convertErrorData(jsonObject2
//											.getString("addv")));
							application.setLcaId(jsonObject2.getString("lcaid"));
							boolean isContainDownloadCount = jsonObject2.has("downloadCount");
							if(isContainDownloadCount){
								application.setDownload_count(jsonObject2.getString("downloadCount"));
							}
							boolean isContainDataSource = jsonObject2.has("dataSource");
							if(isContainDataSource){
								application.setData_source(jsonObject2.getString("dataSource"));
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
		private String data_source;

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
			data_source = "";
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

		public String getData_source() {
			return data_source;
		}

		public void setData_source(String data_source) {
			this.data_source = data_source;
		}
		
		

	}

}
