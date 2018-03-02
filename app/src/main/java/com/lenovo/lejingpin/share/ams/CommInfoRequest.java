package com.lenovo.lejingpin.share.ams;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.lenovo.lejingpin.share.ams.RegistClientInfoRequest.RegistClientInfoResponse;
import com.lenovo.lejingpin.share.net.ToolKit;
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
		mStartIndex = startIndex + 1;
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
		String url = AmsSession.sAmsRequestHost
				+ "ams/3.0/queryappreviewlist.htm" + "?pn=" + mPkgName + "&l="
				+ PsDeviceInfo.getLanguage(mContext) + "&si=" + mStartIndex
				+ "&c=" + mCount + "&vc=" + mVersionCode + "&pa="
				+ RegistClientInfoResponse.getPa() + "&clientid="
				+ RegistClientInfoResponse.getClientId() + "&ci=" + mCommenteid;
		Log.i("zdx", "CommInfoRequest.url:" + url);
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

	public static final class CommInfoResponse implements AmsResponse {
		private ArrayList<CommInfo> mCommInfos = new ArrayList<CommInfo>();
		private boolean mIsFinish = false;
		private boolean mIsSuccess = true;

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

		public void parseFrom(byte[] bytes) {
			String sComment = new String(bytes);
			try {
				JSONObject jsonObject = new JSONObject(sComment);
				// String allCount = jsonObject.getString("totalCount");

				JSONArray jsArray = jsonObject.getJSONArray("dataList");
				if (jsonObject.has("endpage"))
					mIsFinish = jsonObject.getInt("endpage") == 0 ? true
							: false;
				if (jsArray.length() != 0) {
					for (int i = 0; i < jsArray.length(); i++) {
						JSONObject jsonObject2 = jsArray.getJSONObject(i);
						CommInfo info = new CommInfo();
						info.setComment_id(jsonObject2.getString("comment_id"));
						info.setCount(jsonObject2.getString("count"));
						info.setApp_version(jsonObject2
								.getString("app_version"));
						info.setIcon_addr(jsonObject2.getString("icon_addr"));
						info.setApptype_id(jsonObject2.getString("apptype_id"));
						info.setComment_date(ToolKit
								.convertErrorData(jsonObject2
										.getString("comment_date")));
						info.setComment_context(jsonObject2
								.getString("comment_context"));
						info.setAppName(jsonObject2.getString("appname"));
						info.setGrade(ToolKit.convertErrorData(jsonObject2
								.getString("grade")));
						info.setGrade_id(jsonObject2.getString("grade_id"));
						info.setUser_id(jsonObject2.getString("user_id"));
						info.setUser_nick(jsonObject2.getString("user_nick"));
						info.setFace_url(jsonObject2.getString("face_url"));
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
		private String comment_id;
		private String count;
		private String app_version;
		private String icon_addr;
		private String apptype_id;
		private String comment_date;
		private String comment_context;
		private String appName;
		private String grade;
		private String grade_id;
		private String user_id;
		private String user_nick;
		private String face_url;

		public CommInfo() {
			comment_id = "";
			count = "0";
			app_version = "";
			icon_addr = "";
			apptype_id = "";
			comment_date = "0";
			comment_context = "";
			grade = "0";
			appName = "";
			user_id = "";
			user_nick = "";
			face_url = "";
			grade_id = "";
		}

		public String getIcon_addr() {
			return icon_addr;
		}

		public void setIcon_addr(String icon_addr) {
			this.icon_addr = icon_addr;
		}

		public String getCount() {
			return count;
		}

		public void setCount(String count) {
			this.count = count;
		}

		public String getApptype_id() {
			return apptype_id;
		}

		public void setApptype_id(String apptype_id) {
			this.apptype_id = apptype_id;
		}

		public String getAppName() {
			return appName;
		}

		public void setAppName(String appName) {
			this.appName = appName;
		}

		public String getGrade_id() {
			return grade_id;
		}

		public void setGrade_id(String grade_id) {
			this.grade_id = grade_id;
		}

		public String getComment_id() {
			return comment_id;
		}

		public void setComment_id(String comment_id) {
			this.comment_id = comment_id;
		}

		public String getApp_version() {
			return app_version;
		}

		public void setApp_version(String app_version) {
			this.app_version = app_version;
		}

		public String getComment_date() {
			return comment_date;
		}

		public void setComment_date(String comment_date) {
			this.comment_date = comment_date;
		}

		public String getComment_context() {
			return comment_context;
		}

		public void setComment_context(String comment_context) {
			this.comment_context = comment_context;
		}

		public String getGrade() {
			return grade;
		}

		public void setGrade(String grade) {
			this.grade = grade;
		}

		public String getUser_id() {
			return user_id;
		}

		public void setUser_id(String user_id) {
			this.user_id = user_id;
		}

		public String getUser_nick() {
			return user_nick;
		}

		public void setUser_nick(String user_nick) {
			this.user_nick = user_nick;
		}

		public String getFace_url() {
			return face_url;
		}

		public void setFace_url(String face_url) {
			this.face_url = face_url;
		}

	}

	/*** RK_ID:RK_BUGFIX_171707 AUT:zhanglz1@lenovo.com. DATE:2012-10-30.S ***/
	@Override
	public boolean getIsForDownloadNum() {
		// TODO Auto-generated method stub
		return false;
	}
	/*** RK_ID:RK_BUGFIX_171707 AUT:zhanglz1@lenovo.com. DATE:2012-10-30.E ***/

}
