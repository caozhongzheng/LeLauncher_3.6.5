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

public class CommInfoRequest implements AmsRequest {
	private String mPkgName;
	private int mStartIndex;
	private int mCount;
	private Context mContext;
	private String mVersionCode;
	private String mCommenteid;

	public CommInfoRequest(Context context) {
		mContext = context;
	}

	public void setData(String pkgName, String versionCode, int startIndex,
			int count, String commenteid) {
		mPkgName = pkgName;
		mStartIndex = startIndex+1;
		mCount = count;
		mVersionCode = versionCode;
		if (commenteid == null) {
			mCommenteid = "";
		} else {
			mCommenteid = commenteid;
		}
	}

	public String getUrl() {
		// TODO Auto-generated method stub
		return AmsSession.sAmsRequestHost + "ams/3.0/queryappreviewlist.htm"
				+ "?pn=" + mPkgName + "&l="
				+ PsDeviceInfo.getLanguage(mContext) + "&si=" + mStartIndex
				+ "&c=" + mCount + "&vc=" + mVersionCode + "&pa="
				+ RegistClientInfoResponse.getPa() + "&ci=" + mCommenteid;
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

	public static final class CommInfoResponse implements AmsResponse {
		private ArrayList<CommInfo> mCommInfos = new ArrayList<CommInfo>();
		private boolean mIsFinish = false;
		private boolean mIsSuccess = true;
		private String mAllCount = null;

		public boolean getIsSuccess() {
			return mIsSuccess;
		}

		public CommInfo getItem(int i) {
			return mCommInfos.get(i);
		}

		public int getItemCount() {
			return mCommInfos.size();
		}

		public ArrayList<CommInfo> getItemList() {
			return mCommInfos;
		}

		public boolean isFinish() {
			return mIsFinish;
		}
		
		public String getAllCount(){
			return mAllCount;
		}

		public void parseFrom(byte[] bytes) {
			String sComment = new String(bytes);
			Log.i("HawaiiLog", "CommInfoRequestReturnJsonData=" + sComment);
			try {
				JSONObject jsonObject = new JSONObject(sComment);
				String allCount = jsonObject.getString("allcount");
				Log.i("HawaiiLog", "CommInfoRequestReturnJsonData >> 1111111111allCount : " + mAllCount);
				if("0".equals(allCount)){
					mAllCount = allCount;
				}
				Log.i("HawaiiLog", "CommInfoRequestReturnJsonData >> c : " + mAllCount);
				
				JSONArray jsArray = jsonObject.getJSONArray("dataList");
				if (jsonObject.has("endpage"))
					mIsFinish = jsonObject.getInt("endpage") == 0 ? true
							: false;
				if (jsArray.length() != 0) {
					for (int i = 0; i < jsArray.length(); i++) {
						JSONObject jsonObject2 = jsArray.getJSONObject(i);
						CommInfo info = new CommInfo();
						info.setCommentId(jsonObject2.getString("comment_id"));
						info.setCount(jsonObject2.getString("count"));
						info.setAppVersion(jsonObject2
								.getString("app_version"));
						info.setIconAddr(jsonObject2.getString("icon_addr"));
						info.setApptypeId(jsonObject2.getString("apptype_id"));
						info.setCommentDate(ToolKit
								.convertErrorData(jsonObject2
										.getString("comment_date")));
						info.setCommentContext(jsonObject2
								.getString("comment_context"));
						info.setAppName(jsonObject2.getString("appname"));
						info.setGrade(ToolKit.convertErrorData(jsonObject2
								.getString("grade")));
						info.setGradeId(jsonObject2.getString("grade_id"));
						info.setUserId(jsonObject2.getString("user_id"));
						info.setUserNick(jsonObject2.getString("user_nick"));
						info.setFaceUrl(jsonObject2.getString("face_url"));
						mCommInfos.add(info);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				mIsSuccess = false;
				e.printStackTrace();
			}

		}
	}

	public static final class CommInfo implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3421758003318847262L;
		private String commentId;
		private String count;
		private String appVersion;
		private String iconAddr;
		private String apptypeId;
		private String commentDate;
		private String commentContext;
		private String appName;
		private String grade;
		private String gradeId;
		private String userId;
		private String userNick;
		private String faceUrl;

		public CommInfo() {
			commentId = "";
			count = "0";
			appVersion = "";
			iconAddr = "";
			apptypeId = "";
			commentDate = "0";
			commentContext = "";
			grade = "0";
			appName = "";
			userId = "";
			userNick = "";
			faceUrl = "";
			gradeId = "";
		}

		public String getIconAddr() {
			return iconAddr;
		}

		public void setIconAddr(String iconAddr) {
			this.iconAddr = iconAddr;
		}

		public String getCount() {
			return count;
		}

		public void setCount(String count) {
			this.count = count;
		}

		public String getApptypeId() {
			return apptypeId;
		}

		public void setApptypeId(String apptypeId) {
			this.apptypeId = apptypeId;
		}

		public String getAppName() {
			return appName;
		}

		public void setAppName(String appName) {
			this.appName = appName;
		}

		public String getGradeId() {
			return gradeId;
		}

		public void setGradeId(String gradeId) {
			this.gradeId = gradeId;
		}

		public String getCommentId() {
			return commentId;
		}

		public void setCommentId(String commentId) {
			this.commentId = commentId;
		}

		public String getAppVersion() {
			return appVersion;
		}

		public void setAppVersion(String appVersion) {
			this.appVersion = appVersion;
		}

		public String getCommentDate() {
			return commentDate;
		}

		public void setCommentDate(String commentDate) {
			this.commentDate = commentDate;
		}

		public String getCommentContext() {
			return commentContext;
		}

		public void setCommentContext(String commentContext) {
			this.commentContext = commentContext;
		}

		public String getGrade() {
			return grade;
		}

		public void setGrade(String grade) {
			this.grade = grade;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public String getUserNick() {
			return userNick;
		}

		public void setUserNick(String userNick) {
			this.userNick = userNick;
		}

		public String getFaceUrl() {
			return faceUrl;
		}

		public void setFaceUrl(String faceUrl) {
			this.faceUrl = faceUrl;
		}

	}

}
