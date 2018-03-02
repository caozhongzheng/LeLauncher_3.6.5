package com.lenovo.lejingpin.hw.lcapackageinstaller.utils;

import java.io.StringWriter;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;

import com.lenovo.lejingpin.hw.lcapackageinstaller.utils.NetworkHandler.IHttpResponse;

public class NetworkUtils {
	
	private NetworkUtils(){
		
	}

	public interface INetworkResult {
		void onError(String data);

		void onSuccess(String data);
	}

	private static final String SID = "rapp001";

	public static String createAppXML(String appId, String versionCode,
			String isLiteVersion) {
		StringWriter sw = new StringWriter();
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);

			XmlSerializer xs = factory.newSerializer();
			xs.setOutput(sw);
			xs.startDocument("UTF-8", false);
			xs.startTag(null, "UserInstallApp");
			xs.startTag(null, "AppId");
			xs.text(appId);
			xs.endTag(null, "AppId");
			xs.startTag(null, "VersionCode");
			xs.text(versionCode);
			xs.endTag(null, "VersionCode");
			xs.startTag(null, "IsLiteVersion");
			xs.text(isLiteVersion);
			xs.endTag(null, "IsLiteVersion");
			xs.endTag(null, "UserInstallApp");
			xs.endDocument();
			xs.flush();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return sw.toString();
	}

//	public static void checkInstallPermission(final Context context,
//			final String appId, final String versionCode,
//			final String isLiteVersion, final String authCode,
//			final INetworkResult result) {
//		if (result == null) {
//			return;
//		}
//
//		//new Thread() {
//		//@Override
//			//public void run() {
//				String url = PsServerInfo.queryServerUrl(context, SID)
//						+ "ams/3.0/ispermiseinstall.do" + "?ac=" + authCode
//						+ "&appid=" + appId + "&vc=" + versionCode + "&l="
//						+ PsDeviceInfo.getLanguage(context);
//				NetworkHandler.executeHttpGet(context, url,
//						new IHttpResponse() {
//							public void onResult(int code, String body) {
//								if (code == 200) {							
//										try {
//											JSONObject jbody = new JSONObject(body);
//											String ispermise = jbody.getString("isPermise");
//											String license = jbody.getString("license");
//											if(Boolean.parseBoolean(ispermise)){
//												result.onSuccess(license);
//											} else {
//												result.onError(body);
//											}
//										} catch (JSONException e) {
//											result.onError(body);
//											e.printStackTrace();
//										}
//								} else {
//									result.onError(body);
//								}
//							}
//						});
//			//}
//		//}.start();
//	}

//	public static void notifyServerInstall(final Context context, final String appId,
//			final String versionCode, final String isLiteVersion,
//			final INetworkResult result) {
//		new Thread() {
//			@Override
//			public void run() {
//				String url = PsServerInfo.queryServerUrl(context, SID)
//						+ "ams/3.0/uploadinstallinfo.do?appid=" + appId
//						+ "&vc=" + versionCode + "&l="
//						+ PsDeviceInfo.getLanguage(context) + "&pa="
//						+ NetworkHandler.pa;
//				NetworkHandler.executeHttpGetD(context, url,
//						new IHttpResponse() {
//							public void onResult(int code, String body) {
//								if (code == 200) {
//									if (result != null)
//										result.onSuccess("");
//								} else {
//									if (result != null)
//										result.onError(body);
//								}
//							}
//						});
//			}
//		}.start();
//	}
}
