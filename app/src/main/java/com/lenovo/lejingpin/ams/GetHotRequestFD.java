package com.lenovo.lejingpin.ams;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;


import com.lenovo.lsf.util.PsDeviceInfo;

public class GetHotRequestFD implements AmsRequest {
	private Context mContext;
	private static final String tag  = "yangmao";
	public GetHotRequestFD(Context context) {
		mContext = context;
	}

	public String getUrl() {
		// TODO Auto-generated method stub
//		return AmsSession.sAmsRequestHost + "ams/3.0/keywords.htm" + "?l="
//				+ PsDeviceInfo.getLanguage(mContext) + "&pa="
//				+ RegistClientInfoResponse.getPa()+"&c=100";
		
		//return "http://launcher.test.surepush.cn:8080/hawaii/keyword?l=zh-CN&si=20&c=20";
		return  "http://launcher.lenovo.com/hawaii/keyword?l=zh-CN&si=0&c=48";

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

	public static final class GetHotResponse implements AmsResponse {
		private ArrayList<HotLable> mItems = new ArrayList<HotLable>();
		
		private ArrayList<String> mItemsString = new ArrayList<String>();
		
		private boolean mIsSuccess = true;

		public boolean getIsSuccess() {
			return mIsSuccess;
		}

		public HotLable getItem(int i) {
			return mItems.get(i);
		}

		public int getItemCount() {
			return mItems.size();
		}

		public ArrayList<HotLable> getItemList() {
			return mItems;
		}
		
		//yangmao add it for save-ram 0425
		
		public ArrayList<String> getItemListString() {
			return mItemsString;
		}
		
		
		
		public void parseFrom(byte[] bytes) {
			// TODO Auto-generated method stub
			String data = new String(bytes);
			Log.i(tag, data);
			try {
				JSONObject jsonData = new JSONObject(data);
				
				JSONArray array = jsonData.getJSONArray("data");
				if (array.length() != 0) {
					for (int i = 0; i < array.length(); i++) {
						JSONObject jsonObject = array.getJSONObject(i);
//						HotLable hotLable = new HotLable();
//						hotLable.setKeyname(jsonObject.getString("keyname"));
//						hotLable.setHotlevel(jsonObject.getString("hotlevel"));
//						mItems.add(hotLable);
						mItemsString.add(jsonObject.getString("keyname"));
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				mIsSuccess = false;
				e.printStackTrace();
			}

		}
	}

	public static final class HotLable implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1505085902782464372L;
		private String keyname;
		private String hotlevel;

		public HotLable() {
			keyname = "";
			hotlevel = "0";
		}

		public String getKeyname() {
			return keyname;
		}

		public void setKeyname(String keyname) {
			this.keyname = keyname;
		}

		public String getHotlevel() {
			return hotlevel;
		}

		public void setHotlevel(String hotlevel) {
			this.hotlevel = hotlevel;
		}

	}

}
