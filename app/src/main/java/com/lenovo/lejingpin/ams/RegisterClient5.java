package com.lenovo.lejingpin.ams;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.lenovo.lsf.util.PsDeviceInfo;

public class RegisterClient5 implements AmsRequest {
	private Context mContext;
	private int mWidth ;
	private int mHeight;
	private int densityDpi;

	public RegisterClient5(Context context) {
		mContext = context;
	}

	public void setData(int width, int height) {
		this.mWidth = width;
		this.mHeight = height;
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager wm = (WindowManager)mContext.getApplicationContext().getSystemService("window");
		wm.getDefaultDisplay().getMetrics(metrics);
		this.densityDpi = metrics.densityDpi;
	}
	
	private String getHawaii2VersionName(){
		PackageManager packageManager = mContext.getPackageManager();
		PackageInfo packInfo;
		String version = "";
		try {
			packInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
			version = packInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return version;
	}
	
	private String getDensity(){
		DisplayMetrics dm = mContext.getApplicationContext().getResources().getDisplayMetrics();
		return  String.valueOf(dm.density);
	}

	public String getUrl() {
		// TODO Auto-generated method stub
		String lelauncherUrl  =  AmsSession5.sLeLauncherHost+"registclientinfo";
		//String amsUrl = AmsSession5.sAmsRequestHost+ "ams/api/register";
		return lelauncherUrl;
	}

	public String getPost() {
		String post  = null;
		try {
			post = new JSONStringer().object()
			.key("deviceManufacturer").value(android.os.Build.MANUFACTURER)
			.key("deviceBrand").value(android.os.Build.BRAND)
			.key("deviceModel").value(android.os.Build.MODEL)
			.key("lang").value(PsDeviceInfo.getLanguage(mContext))
			.key("os").value("android")
			.key("osVersion").value(android.os.Build.VERSION.RELEASE)
			.key("sdkVersion").value(android.os.Build.VERSION.SDK)
			.key("simoperator1").value("")
			.key("simoperator2").value("")
			.key("phoneNumber1").value("")
			.key("phoneNumber2").value("")
			.key("horizontalResolution").value(mWidth)
			.key("verticalResolution").value(mHeight)
			.key("dpi").value(this.densityDpi)
			.key("deviceIdType").value(PsDeviceInfo.getDeviceidType(mContext))
			.key("deviceId").value(PsDeviceInfo.getDeviceId(mContext))
			.key("clientVersion").value(getHawaii2VersionName())
			.key("packageName").value(mContext.getPackageName())
			.key("st").value("")
			.key("latitude").value("")
			.key("longitude").value("")
			.key("channel").value("17016")
			.key("cpu").value(android.os.Build.CPU_ABI)
			.key("od").value(android.os.Build.VERSION.SDK_INT)
			.key("density").value(getDensity())
			.endObject()
			.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.d("RegisterClient5", "RegisterClient5 >> getPost >> post : "+post);
		return post;
	}

	public int getHttpMode() {
		// TODO Auto-generated method stub
		return 1;
	}

	public String getPriority() {
		// TODO Auto-generated method stub
		return "high";
	}

	public static final class RegisterClient5Response implements AmsResponse {
		private static ClientInfo sClientInfo = new ClientInfo();;
		private boolean mIsSuccess = true;

		public boolean getIsSuccess() {
			return mIsSuccess;
		}

		public static ClientInfo getClientInfo() {
			return sClientInfo;
		}

		public static String getPa() {
			return sClientInfo.getPa();
		}

		public static String getClientId() {
			return sClientInfo.getClientId();
		}

		public static String getError() {
			return sClientInfo.getError();
		}

		public void parseFrom(byte[] bytes) {
			// TODO Auto-generated method stub
			String sClientMes = new String(bytes);
			Log.i("HawaiiLog", "RegisterClient5RequestReturnJsonData5="
					+ sClientMes);
			try {
				JSONObject jsonObject = new JSONObject(sClientMes);
				if (jsonObject.has("clientid")) {
					sClientInfo.setClientId(jsonObject.getString("clientid"));
					sClientInfo.setPa(jsonObject.getString("pa").replace(" ",
							"%20"));
				} else if (jsonObject.has("error")) {
					sClientInfo.setError(jsonObject.getString("error"));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				mIsSuccess = false;
				e.printStackTrace();
			}
		}
	}

	public static final class ClientInfo implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -238155975566085485L;
		private String clientId;
		private String pa;
		private String error;

		public String getClientId() {
			return clientId;
		}

		public void setClientId(String clientId) {
			this.clientId = clientId;
		}

		public String getPa() {
			return pa;
		}

		public void setPa(String pa) {
			this.pa = pa;
		}

		public String getError() {
			return error;
		}

		public void setError(String error) {
			this.error = error;
		}

	}

}
