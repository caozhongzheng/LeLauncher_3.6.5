package com.lenovo.lejingpin.share.ams;

import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.lenovo.lejingpin.share.download.DownloadConstant;
import com.lenovo.lejingpin.share.net.ToolKit;

public class WallpaperRequest implements AmsRequest {
	private int mStartIndex;
	private int mCount;
	private String mQueryType;
	private int mPrice;
	private Context mContext;
	private String mRes;
	private String mPackageName = null;
	private String mVersionCode = null;
	private String mTypeCode;
	private String mTopType;
	private String mOrder;
	private String mPromotionId;
	private String mIsPay;
	private String mMoreTime;
	private boolean mIsForDownloadNum = false;

	public WallpaperRequest(Context context) {
		mContext = context;
	}

	public void setData(int startIndex, int count, String res, int price,
			String type, String moreTime) {
		mStartIndex = startIndex;
		mCount = count;
		mQueryType = type;
		mRes = res;
		mPrice = price;
		mMoreTime = moreTime;
	}

	public void setData(int startIndex, int count, String queryType,
			String isPay, String typecode, String packageName,
			String versionCode, String toptype, String order, String promotionid) {
		mStartIndex = startIndex;
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

	/** RK_ID: RK_DOWNLOAD_COUNT . AUT: zhanglz1 . DATE: 2012-10-18 . S **/
	public void setData(String packagename, String versioncode,
			boolean isForDownloadNum) {
		mPackageName = packagename;
		mVersionCode = versioncode;
		mIsForDownloadNum = isForDownloadNum;
	}

	/** RK_ID: RK_DOWNLOAD_COUNT . AUT: zhanglz1 . DATE: 2012-10-18 . E **/
	public String getUrl() {
		/*** RK_ID: MEM_OPT. AUT: zhaoxy . DATE: 2012-09-26 . START ***/
		StringBuffer url = new StringBuffer(DownloadConstant.HOST_WALLPAPER);
		// String url=HwConstant.HOST_WALLPAPER;
		/** RK_ID: RK_DOWNLOAD_COUNT . AUT: zhanglz1 . DATE: 2012-10-18 . S **/
		/** RK_ID: RK_BUGFIX_171707 . AUT: zhanglz1 . DATE: 2012-10-19 . S **/
		if (!mIsForDownloadNum) {

			url.append("?method=a")
					// .append("&res=").append(mRes)
					.append("&s=").append(mStartIndex).append("&t=")
					.append(mCount).append("&f=id&a=desc")
			// .append("&res=").append(res)
			;
		} else if (mIsForDownloadNum) {
			url.append("?method=a&wid=").append(mPackageName);
			// WallpaperAction.mIsForDownloadNum = false;
		}
		/** RK_ID: RK_BUGFIX_171707 . AUT: zhanglz1 . DATE: 2012-10-19 . E **/
		/** RK_ID: RK_DOWNLOAD_COUNT . AUT: zhanglz1 . DATE: 2012-10-18 . E **/
		url.append("&time=").append(new Date().getTime());
		Log.i("zdx", "WallpaperinfoRequest, url=" + url.toString());
		return url.toString();
		/*** RK_ID: MEM_OPT. AUT: zhaoxy . DATE: 2012-09-26 . END ***/
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

	/*** RK_ID:RK_BUGFIX_171707 AUT:zhanglz1@lenovo.com. DATE:2012-10-30.S ***/
	public boolean getIsForDownloadNum() {
		// TODO Auto-generated method stub
		return mIsForDownloadNum;
	}

	/*** RK_ID:RK_BUGFIX_171707 AUT:zhanglz1@lenovo.com. DATE:2012-10-30.E ***/

	public static final class WallpaperResponse implements AmsResponse {
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
			try {
				JSONObject jsonObject = new JSONObject(sCategory);
				if (jsonObject.has("endpage"))
					mIsFinish = jsonObject.getInt("endpage") == 0 ? true
							: false;

				JSONArray jsArray = jsonObject.getJSONArray("wallpapers");// 获取JSONArray
				int length = jsArray.length();
				if (length != 0) {
					for (int i = 0; i < length; i++) {

						JSONObject jsonObject2 = jsArray.getJSONObject(i);
						Application application = new Application();
						application.setAppId(jsonObject2.getInt("id"));
						application
								.setAppName(ToolKit
										.convertErrorData(jsonObject2
												.getString("name")));
						application.setDownload_count(jsonObject2
								.getInt("downloadcount"));// ?
						application.setDetail(ToolKit
								.convertErrorData(jsonObject2
										.getString("detail")));
						application.setRes(ToolKit.convertErrorData(jsonObject2
								.getString("res")));
						application
								.setApp_price(jsonObject2.getString("price"));
						application.setApp_size(jsonObject2.getString("size"));
						application.setAuther(ToolKit
								.convertErrorData(jsonObject2
										.getString("auther")));
						application
								.setBgType(ToolKit.convertErrorData(jsonObject2
										.getString("type")));
						application
								.setUrl(DownloadConstant.HOST_EXTRA_WALLPAPER
										+ ToolKit.convertErrorData(jsonObject2
												.getString("url")));
						application
								.setIconUrl(DownloadConstant.HOST_EXTRA_WALLPAPER
										+ ToolKit.convertErrorData(jsonObject2
												.getString("iconurl")));
						application.setUpload_time(ToolKit
								.convertErrorData(jsonObject2
										.getString("uploadtime")));
						application.setApp_versioncode(ToolKit
								.convertErrorData(jsonObject2.getString("id")));
						application.setPackage_name(ToolKit
								.convertErrorData(jsonObject2.getString("id")));
						application
								.setPreviewAddr(DownloadConstant.HOST_EXTRA_WALLPAPER
										+ ToolKit.convertErrorData(jsonObject2
												.getString("previewurl")));
						mApplications.add(application);
					}
				}
			} catch (Exception e) {
				mIsSuccess = false;
				e.printStackTrace();
			}
		}

	}

	public static final class Application implements Serializable {
		private static final long serialVersionUID = 3830870434843595687L;

		private int id;
		private String name; // 图片名称
		private int downloadCount;// 下载次数
		private String size;// 大小
		private String price;// 价格
		private String app_versioncode;
		private String package_name;

		// 新增
		private String type; // 类型
		private String res;// 分辨率
		private String auther;// 作者
		private String uploadTime;// 上传时间
		private String detail;// 上传时间
		private String url;// 图片下载地址
		private String iconUrl;// 图片icon地址
		private String previewAddr;// 图片预览图地址

		public Application() {
			id = 0;
			name = "";
			downloadCount = 0;
			size = "0";
			price = "0";
			type = "";
			res = "";
			auther = "";
			uploadTime = null;
			detail = "";
			url = "";
			iconUrl = "";
			app_versioncode = "";
			package_name = "";
			previewAddr = "";
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

		public String getApp_price() {
			return price;
		}

		public void setApp_price(String price) {
			this.price = price;
		}

		public String getApp_size() {
			return size;
		}

		public void setApp_size(String size) {
			this.size = size;
		}

		public String getAppName() {
			return name;
		}

		public void setAppName(String appName) {
			this.name = appName;
		}

		public int getAppId() {
			return id;
		}

		public void setAppId(int id) {
			this.id = id;
		}

		public int getDownload_count() {
			return downloadCount;
		}

		public void setDownload_count(int downloadCount) {
			this.downloadCount = downloadCount;
		}

		public String getBgType() {
			return type;
		}

		public void setBgType(String bgType) {
			this.type = bgType;
		}

		public String getRes() {
			return res;
		}

		public void setRes(String res) {
			this.res = res;
		}

		public String getAuther() {
			return auther;
		}

		public void setAuther(String auther) {
			this.auther = auther;
		}

		public String getUpload_time() {
			return uploadTime;
		}

		public void setUpload_time(String upload_time) {
			this.uploadTime = upload_time;
		}

		public String getDetail() {
			return detail;
		}

		public void setDetail(String detail) {
			this.detail = detail;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getIconUrl() {
			return iconUrl;
		}

		public void setIconUrl(String iconUrl) {
			this.iconUrl = iconUrl;
		}

		public String getPreviewAddr() {
			return previewAddr;
		}

		public void setPreviewAddr(String previewAddr) {
			this.previewAddr = previewAddr;
		}
	}

}
