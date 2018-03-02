package com.lenovo.lejingpin.ams;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lenovo.lejingpin.ams.RegisterClient5.RegisterClient5Response;
import com.lenovo.lejingpin.ams.SpereCommendRequest5.Application;
import com.lenovo.lejingpin.net.ToolKit;
import com.lenovo.lsf.util.PsDeviceInfo;

import android.content.Context;
import android.util.Log;

public class AppTypeRequest implements AmsRequest{
	private static String TAG = "AppTypeRequest";
	
	private Context mContext;
	private int mStartIndex;
	private int mCount;
	private String mListType;
	private String mCategory;
	private String mCode;
	
	private String at;
	
	public AppTypeRequest(Context context){
		mContext = context;
	}
	
	public void setData(int startIndex,int count,String listtype , String cg,String code){
		this.mStartIndex = startIndex;
		this.mCount = count;
		this.mListType = listtype;
		this.mCategory = cg;
		this.mCode = code;
	}
	

	@Override
	public String getUrl(){
		//for sonar modify
		//String l = PsDeviceInfo.getLanguage(mContext);
		// TODO Auto-generated method stub
//		String lelauncherUrl = AmsSession5.sLeLauncherHost+"applist?";
		String amsUrl = AmsSession5.sAmsRequestHost+"ams/api/applist?";
		
		return amsUrl+"si="
		+mStartIndex+"&c="+mCount+"&lt="+mListType+"&cg="+mCategory+"&code="+mCode+"&pa="+RegisterClient5Response.getPa();
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
	
	
	public static final class AppTypeRsponse implements AmsResponse {

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
			
			Log.i("AppTypeRsponse", "++++++++++++++++++++++AppTypeRsponseJSONData=" + sCategory+"=********");
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
//							application.setLcaId(jsonObject2.getString("lcaid"));
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

}
