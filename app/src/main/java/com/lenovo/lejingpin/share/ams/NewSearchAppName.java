package com.lenovo.lejingpin.share.ams;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lenovo.lejingpin.share.ams.RegistClientInfoRequest.RegistClientInfoResponse;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class NewSearchAppName implements AmsRequest {

	private Context mContext;
	private String mKeyWord;
	private String si = "0";
	private static final String TAG = "HawaiiLog";

	public NewSearchAppName(Context context) {
		mContext = context;
	}

	public void setData(String keyword) {

		mKeyWord = keyword;

	}

	public void setStartIndex(int startindex) {
		si = Integer.toString(startindex * 20);
	}

	/*
	 * @Override public String getUrl() { // TODO Auto-generated method stub
	 * String kw = ""; try { kw = URLEncoder.encode(mKeyWord, "UTF-8"); } catch
	 * (UnsupportedEncodingException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); }
	 * 
	 * String url =null; try { // ams1001834efbe248cc4f95adfec556f3e50af6 url =
	 * AmsSession.sAmsRequestHost + "ams/3.0/appopensearch.do" + "?type=" +
	 * URLEncoder.encode("1", "UTF-8") +
	 * "&clientid="+URLEncoder.encode(RegistClientInfoResponse.getClientId(),
	 * "UTF-8") + "&si="+ URLEncoder.encode(si, "UTF-8") +"&cnt="+
	 * URLEncoder.encode("20", "UTF-8") +"&kw="+ kw + "&from="+17016 +"&stf=" +
	 * URLEncoder.encode("appname", "UTF-8") +"&srf="+URLEncoder.encode(
	 * "uuid,lcaid,appid,apppack,appname,publishdate,ispay,appprice,appversion,appsize,appaddr,iconaddr,addv,star,download,click,appversioncode"
	 * , "UTF-8") +"&order="+URLEncoder.encode("score+desc,click+desc", "UTF-8")
	 * +"&hl="+0 +"&pa="+RegistClientInfoResponse.getPa();
	 * 
	 * } catch (UnsupportedEncodingException e) { // TODO Auto-generated catch
	 * block e.printStackTrace(); }
	 * 
	 * // String url ; // url = AmsSession.sAmsRequestHost +
	 * "ams/3.0/appopensearch.do" // + "?type=" + 2 +
	 * "&clientid="+RegistClientInfoResponse.getClientId()+ // "&si="+ 0
	 * +"&cnt="+ 10+"&kw="+ kw + "&stf=" + "appname"+ // "&srf="+
	 * "uuid,lcaid,appid,apppack,appname,publishdate,ispay,appprice,appversion,appsize,appaddr,iconaddr,addv,star,download,click"
	 * // +"&order="+"score+desc,click+desc"; // // Log.i("yangmao",
	 * "get url is:"+url); return url;
	 * 
	 * }
	 */

	/*
	 * 正确的
	 * 
	 * @Override public String getUrl() { // TODO Auto-generated method stub
	 * String kw = ""; try { kw = URLEncoder.encode(mKeyWord, "UTF-8"); } catch
	 * (UnsupportedEncodingException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } String url =null; try { //
	 * ams1001834efbe248cc4f95adfec556f3e50af6 url = AmsSession.sAmsRequestHost
	 * + "ams/3.0/appopensearch.do"
	 * 
	 * //+ "clientid="+URLEncoder.encode(RegistClientInfoResponse.getClientId(),
	 * "UTF-8") //+
	 * "&clientid="+URLEncoder.encode("ams1001834efbe248cc4f95adfec556f3e50af6",
	 * "UTF-8") + "?si="+ URLEncoder.encode(si, "UTF-8") +"&cnt="+
	 * URLEncoder.encode("20", "UTF-8") +"&kw="+ kw + "&from="+17016 +"&stf=" +
	 * URLEncoder.encode("appname", "UTF-8") +"&srf="+URLEncoder.encode(
	 * "lcaid,appid,apppack,appname,publishdate,ispay,appprice,appversion,appsize,appaddr,iconaddr,addv,star,download,click,appversioncode"
	 * , "UTF-8") +"&order="+URLEncoder.encode("score+desc,click+desc", "UTF-8")
	 * +"&hl="+0 +"&ispay="+0;
	 * 
	 * // +"&pa="+RegistClientInfoResponse.getPa();
	 * 
	 * } catch (UnsupportedEncodingException e) { // TODO Auto-generated catch
	 * block e.printStackTrace(); } // String url ; // url =
	 * AmsSession.sAmsRequestHost + "ams/3.0/appopensearch.do" // + "?type=" + 2
	 * + "&clientid="+RegistClientInfoResponse.getClientId()+ // "&si="+ 0
	 * +"&cnt="+ 10+"&kw="+ kw + "&stf=" + "appname"+ // "&srf="+
	 * "uuid,lcaid,appid,apppack,appname,publishdate,ispay,appprice,appversion,appsize,appaddr,iconaddr,addv,star,download,click"
	 * // +"&order="+"score+desc,click+desc"; //
	 * 
	 * Log.i(TAG, "get url is:"+url); return url;
	 * 
	 * }
	 */

	@Override
	public String getUrl() {
		// TODO Auto-generated method stub
		String kw = "";
		try {
			kw = URLEncoder.encode(mKeyWord, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String url = null;
		try {

			url = AmsSession.sAmsRequestHost
					+ "ams/3.0/appopensearch.do"
					+ "?si="
					+ URLEncoder.encode(si, "UTF-8")
					+ "&cnt="
					+ URLEncoder.encode("20", "UTF-8")
					+ "&kw="
					+ kw
					+ "&from="
					+ 17016
					+ "&stf="
					+ URLEncoder.encode("appname", "UTF-8")
					+ "&srf="
					+ URLEncoder
							.encode("lcaid,appid,apppack,appname,publishdate,ispay,appprice,appversion,appsize,appaddr,iconaddr,addv,star,download,click,appversioncode",
									"UTF-8") + "&order="
					+ URLEncoder.encode("score+desc,click+desc", "UTF-8")
					+ "&hl=" + 0 + "&ispay=" + 0;

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i(TAG, "get url is:" + url);
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

	public static final class NewSearchResponse implements AmsResponse {
		private ArrayList<Application> mApplications = new ArrayList<Application>();

		private boolean mIsFinish = false;
		private boolean mIsSuccess = true;

		private boolean clientIdExpired = false;

		public boolean getIsExpired() {
			return clientIdExpired;
		}

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

		@Override
		public void parseFrom(byte[] bytes) {
			// TODO Auto-generated method stub
			String sCategory = new String(bytes);
			Log.i(TAG, "NewSearchAppName=" + sCategory);
			try {
				JSONObject jsonObject = new JSONObject(sCategory);

				if (!jsonObject.getBoolean("is_success")) {
					Log.i(TAG, "is not true");
					mIsSuccess = false;
					JSONObject result_error = jsonObject.getJSONObject("error");
					String error_code = result_error.getString("code");
					if (error_code.equals("-1")) {
						clientIdExpired = true;
					}
				}

				JSONObject result = jsonObject.getJSONObject("result");
				Log.i("xiaoyangmao",
						"the number found is:" + result.getString("numFound"));
				JSONArray jsArray = result.getJSONArray("docs");
				if (jsonObject.has("endpage"))
					mIsFinish = jsonObject.getInt("endpage") == 0 ? true
							: false;
				if (jsArray.length() != 0) {
					for (int i = 0; i < jsArray.length(); i++) {
						JSONObject jsonObject2 = jsArray.getJSONObject(i);
						Application application = new Application();
						application
								.setAppName(jsonObject2.getString("appname"));
						application.setIcon_addr(jsonObject2
								.getString("iconaddr"));
						application.setApp_size(jsonObject2
								.getString("appsize"));
						application.setLcaId(jsonObject2.getString("lcaid"));
						application.setPackage_name(jsonObject2
								.getString("apppack"));
						application.setApp_versioncode(jsonObject2
								.getString("appversioncode"));
						application
								.setStar_level(jsonObject2.getString("star"));
						application.setIsPay(jsonObject2.getString("ispay"));
						application.setApp_price(jsonObject2
								.getString("appprice"));
						mApplications.add(application);

						// yangmao add new to zdx interface

						// RecommendLocalAppInfo application_ym = new
						// RecommendLocalAppInfo();
						// ReCommendsApp application_ym = new ReCommendsApp();
						// application_ym.setAppName(jsonObject2.getString("appname"));
						// application_ym.setIconAddress(jsonObject2.getString("iconaddr"));
						// application_ym.setAppSize(jsonObject2.getString("appsize"));
						// application_ym.setLcaid(jsonObject2.getString("lcaid"));
						// application_ym.setPackageName(jsonObject2.getString("apppack"));
						// application_ym.setVersionCode(jsonObject2.getString("appversioncode"));
						// application_ym.setStaLevel(jsonObject2.getString("star"));
						// application_ym.setIsPay(jsonObject2.getString("ispay"));
						// application_ym.setAppPrice(jsonObject2.getString("appprice"));
						// mApplications_ym.add(application_ym);

					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				mIsSuccess = false;
				e.printStackTrace();
			}
		}
	}

	public static final class AppSearchOrderType {
		public static final String SORT_DOWNLOAD = "down";
		public static final String SORT_TIME = "time";
		public static final String SORT_STAR = "star";
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
		// yangmao add
		private String type_name;

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
			type_name = "";

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

		public String getType_name() {
			return type_name;
		}

		public void setType_name(String type_name) {
			this.type_name = type_name;
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
