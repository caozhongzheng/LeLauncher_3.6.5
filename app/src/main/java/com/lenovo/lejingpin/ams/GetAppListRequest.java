package com.lenovo.lejingpin.ams;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lenovo.lejingpin.ams.RegisterClient5.RegisterClient5Response;

import android.util.Log;

public class GetAppListRequest implements AmsRequest {
	private int mStartIndex;
	private int mCount;
	private String mListType;
	private String mQueryType;
	private String mCategory;
	private String mPayment;
	private String mPeriod;
	private String mOrder;
	
	public void setData(int si,int count,String lt,String qt,String c,String py,String p,String order){
		mStartIndex = si;
		mCount = count;
		mListType = lt;
		mQueryType = qt;
		mCategory = c;
		mPayment = py;
		mPeriod = p;
		mOrder= order;
	}

	@Override
	public String getUrl() {
		String url = AmsSession.sAmsRequestHost+"ams/api/applistfor3rdparty?"
		+"si="+mStartIndex+"&c="+mCount+"&lt="+mListType+"&qt="+mQueryType+"&cg="+mCategory
		+"&py="+mPayment+"&p="+mPeriod+"&order="+mOrder+"&clientid="+RegisterClient5Response.getClientId();
		// TODO Auto-generated method stub
		return url;
	}

	@Override
	public String getPost() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getHttpMode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getPriority() {
		// TODO Auto-generated method stub
		return "high";
	}
	
	public static final class GetAppListResponse implements AmsResponse{
		private ArrayList<Application> mApplications = new ArrayList<Application>();
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
		
		@Override
		public void parseFrom(byte[] bytes) {
			String sCategory = new String(bytes);
			Log.i("HawaiiLog", "GetAppListResponseReturnJsonData=" + sCategory);
			try {
				JSONObject jsonObject = new JSONObject(sCategory);
				String dataType = jsonObject.getString("datatype");
				if (dataType.equals("applist")) {
					JSONArray jsArray = jsonObject.getJSONArray("datalist");
					if(jsArray!=null){
						int size = jsArray.length();
						for(int i=0;i<size;i++){
							
							JSONObject jsonObject2 = jsArray.getJSONObject(i);
							Application application = new Application();
							application.setAppName(jsonObject2.getString("name"));
							application.setApp_size(jsonObject2.getString("size"));
							application.setPackage_name(jsonObject2.getString("packageName"));
							application.setApp_version(jsonObject2.getString("version"));
							application.setIsPay(jsonObject2.getString("ispay"));
							application.setIcon_addr(jsonObject2.getString("iconAddr"));
							application.setApp_versioncode(jsonObject2.getString("versioncode"));
							application.setStar_level(jsonObject2.getString("averageStar"));
							application.setAddv(jsonObject2.getString("addv"));
							application.setApp_price(jsonObject2.getString("price"));
							application.setApp_publishdate(jsonObject2.getString("publishDate"));
							application.setDownload_count(jsonObject2.getString("downloadCount"));
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

}
