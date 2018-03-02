package com.lenovo.lejingpin.network;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.util.Log;

import com.lenovo.lejingpin.network.RegistClientInfoRequest5.RegistClientInfoResponse5;
import com.lenovo.lejingpin.network.ToolKit;
import com.lenovo.lejingpin.network.AmsApplication;

/*
 * LPS-AppStore-API-D-A41(ams5.0)
 */
public class CategoryRequest5 implements AmsRequest {
	private static final String TAG = "CategoryRequest5";
	private Context mContext;
	private int mStartIndex;
	private int mCount;
	private String mCode;
	
	public CategoryRequest5(Context context) {
		mContext = context;
	}

	public void setData(int startIndex, int count, String code) {
		mStartIndex = startIndex + 1;
		mCount = count;
		mCode = code;
	}

	public String getUrl() {
		String url = AmsSession.sAmsRequestHost 
				+ "ams/api/applist"
				+ "?si="+ mStartIndex 
				+ "&c=" + mCount 
				+ "&lt=subject"
				+ "&code="+mCode
				+ "&pa=" + RegistClientInfoResponse5.getPa()
				+ "&clientid=" + RegistClientInfoResponse5.getClientId();
		Log.i(TAG,"CategoryRequest5.url:"+ url);
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

	public static final class CategoryResponse5 implements AmsResponse {
		private ArrayList<AmsApplication> mApplications = new ArrayList<AmsApplication>();
		private boolean mIsSuccess = true;
                private String mRetStatus;

		public boolean getIsSuccess() {
			return mIsSuccess;
		}
		public String getReturnStatus() {
			return mRetStatus;
		}

		public AmsApplication getApplicationItem(int i) {
			return mApplications.get(i);
		}

		public int getApplicationItemCount() {
			return mApplications.size();
		}

		public ArrayList<AmsApplication> getApplicationItemList() {
			return mApplications;
		}

		public void parseFrom(byte[] bytes) {
			// TODO Auto-generated method stub
			String sCategory = new String(bytes);
                        mRetStatus = sCategory;
			//Log.i(TAG, "CategoryResponse5.parseFrom, Return JsonData=" + sCategory);
                        if("not_regist".equals(sCategory)){
			mIsSuccess = false;
                            return;
                        }
			try {
				JSONObject jsonObject = new JSONObject(sCategory);
				JSONArray jsArray = jsonObject.getJSONArray("datalist");
			//Log.i(TAG, "<F6><F6><F6><F6> CategoryResponse5.parseFrom, Return JsonData=" +jsArray);
				for (int i = 0; i < jsArray.length(); i++) {
					JSONObject jsonObject2 = jsArray.getJSONObject(i);
					AmsApplication application = new AmsApplication();
                                        application.setAuther(ToolKit.convertErrorData(jsonObject2.getString("developerName")));
                                        application.setDownload_count(jsonObject2
                                                                .getString("downloadCount"));//?

					application.setApp_price(ToolKit.convertErrorData(jsonObject2.getString("price")));
					application.setPackage_name(jsonObject2.getString("packageName"));
					application.setApp_size(ToolKit.convertErrorData(jsonObject2.getString("size")));
					application.setApp_version(jsonObject2.getString("version"));
					application.setIcon_addr(jsonObject2.getString("iconAddr"));
					application.setStar_level(ToolKit.convertErrorData(jsonObject2.getString("averageStar")));
					application.setApp_publishdate(ToolKit.convertErrorData(jsonObject2.getString("publishDate")));
					application.setAppName(jsonObject2.getString("name"));
					//yangmao add it for lock for test
					application.setApp_Addr(jsonObject2.getString("appAddr"));
					//yangmao add it for lock for test
					application.setIsPay(ToolKit.convertErrorData(jsonObject2.getString("ispay")));
					application.setDiscount(jsonObject2.getString("discount"));
					application.setApp_versioncode(ToolKit.convertErrorData(jsonObject2.getString("versioncode")));
                                        application.setthumbpaths(jsonObject2.getString("snap1"),jsonObject2.getString("snap2"),jsonObject2.getString("snap3"));
                                        application.setIsPath(true);
					mApplications.add(application);
		        }
					
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				mIsSuccess = false;
				e.printStackTrace();
			Log.i(TAG, "CategoryResponse5.parseFrom, Return JsonData=" +e);
			}
		}

	}

	public static final class RequestContentType {        
        public static final String THEME_TYPECODE = "840";
        public static final String LOCK_TYPECODE = "821";
        public static final String WALLPAPER_TYPECODE = "841";
        public static final String SCENE_TYPECODE = "842";
	}

	/***RK_ID:RK_BUGFIX_171707 AUT:zhanglz1@lenovo.com. DATE:2012-10-30.S***/       
	@Override
	public boolean getIsForDownloadNum() {
		// TODO Auto-generated method stub
		return false;
	}
	/***RK_ID:RK_BUGFIX_171707 AUT:zhanglz1@lenovo.com. DATE:2012-10-30.E***/  

}
