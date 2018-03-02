package com.lenovo.lejingpin.ams;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class AppPageContentRequest implements AmsRequest{
	
	private static String TAG = "AppPageContentRequest";
	
	private int mAtId;
	
	public void setData(int atId){
		mAtId = atId;
	}

	@Override
	public String getUrl() {
		// TODO Auto-generated method stub
		return AmsSession5.sAmsRequestHost+"ams/api/allpagecontents?at_id="+mAtId;
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
	
	public static final class AppPageContentResponse implements AmsResponse{
		
		private ArrayList<AppContent> appContents;
		
		private boolean isSuccess;
		
		
		public ArrayList<AppContent> getAppContents(){
			if(appContents==null){
				appContents = new ArrayList<AppContent>();
			}
			return appContents;
		}
		
		public boolean isSuccess(){
			return isSuccess;
		}
		
		@Override
		public void parseFrom(byte[] bytes) {
			String appPageContent = new String(bytes);
			Log.d(TAG, "AppPageContentResponse >> parseFrom >> appPageContent :  "+appPageContent);
			try{
				JSONObject jsonObject = new JSONObject(appPageContent);
				JSONArray jsArray = jsonObject.getJSONArray("menus");
				if(jsArray!=null){
					int len = jsArray.length();
					if(len!=0){
						
						for(int i=0;i<len;i++){
							AppContent content = new AppContent();
							JSONObject jsonObject2 = jsArray.getJSONObject(i);
							JSONObject menu = jsonObject2.getJSONObject("menu");
							Log.d(TAG,"============================================================================");
							content.setMenuName(menu.getString("name"));
							content.setmMenuCode(menu.getString("code"));
							JSONArray elements = jsonObject2.getJSONArray("elements");
							if(elements!=null){
								int eLen = elements.length();
								for(int j=0;j<eLen;j++){
									JSONObject element = elements.getJSONObject(j);
									JSONArray  contents = element.getJSONArray("Contents");
									if(contents!=null){
										int contentLen = contents.length();
										for(int k=0;k<contentLen;k++){
											JSONObject  jsonObjectc = contents.getJSONObject(k);
											int itemType =  jsonObjectc.getInt("itemType");
											if(itemType==3){
												content.setmCode(jsonObjectc.getString("code"));
												content.setmContentName(jsonObjectc.getString("name"));
												content.setmItemType(String.valueOf(itemType));
											}
										}
									}
								}
							}
							Log.d(TAG,"============================================================================");
							getAppContents().add(content);
						}
					}
				}
				isSuccess = true;
			}catch(Exception e){
				isSuccess = false;
				e.printStackTrace();
			}
			
		}
	}
	
	public static final class AppContent{
		
		private String menuName;
		private String mMenuCode;
		private String mContentName;
		private String mCode;
		private String mItemType;
		public String getMenuName() {
			return menuName;
		}
		public void setMenuName(String menuName) {
			this.menuName = menuName;
		}
		public String getmContentName() {
			return mContentName;
		}
		public void setmContentName(String mContentName) {
			this.mContentName = mContentName;
		}
		public String getmCode() {
			return mCode;
		}
		public void setmCode(String mCode) {
			this.mCode = mCode;
		}
		public String getmItemType() {
			return mItemType;
		}
		public void setmItemType(String mItemType) {
			this.mItemType = mItemType;
		}
		public String getmMenuCode() {
			return mMenuCode;
		}
		public void setmMenuCode(String mMenuCode) {
			this.mMenuCode = mMenuCode;
		}
		@Override
		public String toString() {
			
			return "menuName : "+this.menuName+" ; mMenuCode : "+this.mMenuCode+" ; mContentName : "+this.mContentName+" ;  mCode : "+mCode+" ; mItemType : "+mItemType;
		}
		
		
		
	}
	
}
