package com.lenovo.lejingpin.hw.lcapackageinstaller.utils;

import java.io.StringWriter;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.lenovo.lejingpin.hw.lcapackageinstaller.LcaInstallerActivity;
import com.lenovo.lsf.util.PsDeviceInfo;

public class NetworkHandler {
	// private static final String tag = "LcaPackageInstaller";

	private static final String SID = "rapp001";
	private static final String RID = "appstore.lps.lenovo.com";

	public static String[] systemInfo = null;

	public static String clientId = null;
	public static String st = null;
	public static String pa = null;
	
	private NetworkHandler(){
		
	}

	public interface IHttpResponse {
		public void onResult(int code, String body);
	}

	public static String clientInfoXML(Context context) {
		StringWriter sw = new StringWriter();
		try {
			XmlPullParserFactory factory;
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);

			XmlSerializer xs = factory.newSerializer();
			xs.setOutput(sw);
			xs.startDocument("UTF-8", false);
			xs.startTag(null, "ClientInfo");

			xs.startTag(null, "DeviceVendor");
			xs.text(PsDeviceInfo.getDeviceVendor(context));
			xs.endTag(null, "DeviceVendor");

			xs.startTag(null, "DeviceFamily");
			xs.text(PsDeviceInfo.getDeviceFamily(context));
			xs.endTag(null, "DeviceFamily");

			xs.startTag(null, "DeviceModel");
			xs.text(PsDeviceInfo.getDeviceModel(context));
			xs.endTag(null, "DeviceModel");

			xs.startTag(null, "DeviceIdType");
			xs.text(PsDeviceInfo.getDeviceidType(context));
			xs.endTag(null, "DeviceIdType");

			xs.startTag(null, "DeviceId");
			xs.text(PsDeviceInfo.getDeviceId(context));
			xs.endTag(null, "DeviceId");

			xs.startTag(null, "OsName");
			xs.text("Leos");
			xs.endTag(null, "OsName");

			xs.startTag(null, "OsApiLevel");
			xs.text(PsDeviceInfo.getLeosApiLevel(context));
			xs.endTag(null, "OsApiLevel");

			xs.startTag(null, "ClientVersion");
			xs.text(PsDeviceInfo.getAppstoreVersion(context));
			xs.endTag(null, "ClientVersion");

			xs.startTag(null, "Lang");
			xs.text(PsDeviceInfo.getLanguage(context));
			xs.endTag(null, "Lang");

			xs.endTag(null, "ClientInfo");
			xs.endDocument();
			xs.flush();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return sw.toString();
	}

//	private static String getClientId(Context context) {
//		st = PsAuthenServiceL.getStData(context, RID, false);
//		String url = PsServerInfo.queryServerUrl(context, SID)
//				+ "ams/3.0/registclientinfo.do"
//				+ "?dv="
//				+ android.os.Build.MANUFACTURER
//				+ "&db="
//				+ android.os.Build.BRAND.replace(" ", "%20")
//				+ "&dm="
//				+ android.os.Build.MODEL.replace(" ", "%20")
//				+ "&l="
//				+ PsDeviceInfo.getLanguage(context)
//				+ "&os=android"
//				+ "&ov="
//				+ android.os.Build.VERSION.RELEASE
//				+ "&ol="
//				+ android.os.Build.VERSION.SDK
//				+ "&so="
//				+ ((TelephonyManager) (context
//						.getSystemService(Context.TELEPHONY_SERVICE)))
//						.getSimOperator() + "&r="
//				+ LcaInstallerActivity.getScreenWidth() + "*"
//				+ LcaInstallerActivity.getScreenHeight() + "&dit="
//				+ PsDeviceInfo.getDeviceidType(context) + "&di="
//				+ PsDeviceInfo.getDeviceId(context) + "&cv="
//				+ PsDeviceInfo.getAppstoreVersion(context) + "&s="
//				+ PsDeviceInfo.getSource(context) + "&st="
//				+ PsAuthenServiceL.getStData(context, RID, false);
//
//		NetworkHttpRequest request = new NetworkHttpRequest();
//		NetworkHttpRequest.HttpReturn ret = request
//				.executeHttpGet(context, url);
//		if (ret.code == 200) {
//			try {
//				JSONObject jsonObject = new JSONObject(ret.body);
//				clientId = jsonObject.getString("clientid");
//				pa = jsonObject.getString("pa");
//				return clientId;
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				return null;
//			}
//		} else {
//			return null;
//		}
//	}

//	public static void executeHttpGet(final Context context, final String url,
//			final IHttpResponse response) {
//		if (st == null) {
////			st = PsAuthenServiceL.getStData(context, RID, true);
////			if (st.startsWith("USS")) {
////				response.onResult(-1, st);
////			} else {
////				executeHttpGet(context, url, response);
////			}
//			PsAuthenServiceL.getStData(context, RID, new OnAuthenListener() {
//				public void onFinished(boolean ret, String data) {
//					if (ret) {
//						st = data;
//						executeHttpGet(context, url, response);
//					} else {
//						st = null;
//						response.onResult(-1, "neterr");
//					}
//				}
//			}, true);
//		} else {
//			if (clientId == null) {
//				clientId = getClientId(context);
//				if (clientId == null) {
//					response.onResult(-1, "");
//				}
//			}
//			NetworkHttpRequest request = new NetworkHttpRequest();
//			NetworkHttpRequest.HttpReturn ret = request.executeHttpGet(context,
//					url);
//
//			if (ret.code == 0) {
//				response.onResult(ret.code, ret.body);
//			} else {
//				if (ret.code == 308) {
//					clientId = getClientId(context);
//					if (clientId == null) {
//						response.onResult(-1, "");
//					} else {
//						executeHttpGet(context, url, response);
//					}
//				} else if (ret.code == 401) {
//					st = PsAuthenServiceL.getStData(context, RID, true);
//					if (st == null || st.startsWith("USS")) {
//						response.onResult(-1, "");
//					} else {
//						executeHttpGet(context, url, response);
//					}
//				} else {
//					response.onResult(ret.code, ret.body);
//				}
//			}
//		}
//	}

//	public static void executeHttpGetD(final Context context, final String url,
//			final IHttpResponse response) {
//		if (st == null) {
//			st = PsAuthenServiceL.getStData(context, RID, true);
//			if (st == null || st.startsWith("USS")) {
//				response.onResult(-1, st);
//			} else {
//				executeHttpGetD(context, url, response);
//			}
////			PsAuthenServiceL.getStData(context, RID, new OnAuthenListener() {
////				public void onFinished(boolean ret, String data) {
////					if (ret) {
////						st = data;
////						executeHttpGet(context, url, response);
////					} else {
////						st = null;
////						response.onResult(-1, "neterr");
////					}
////				}
////			}, true);
//		} else {
//			if (clientId == null) {
//				clientId = getClientId(context);
//				if (clientId == null) {
//					response.onResult(-1, "");
//				}
//			}
//			NetworkHttpRequest request = new NetworkHttpRequest();
//			NetworkHttpRequest.HttpReturn ret = request.executeHttpGet(context,
//					url);
//
//			if (ret.code == 0) {
//				response.onResult(ret.code, ret.body);
//			} else {
//				if (ret.code == 308) {
//					clientId = getClientId(context);
//					if (clientId == null) {
//						response.onResult(-1, "");
//					} else {
//						executeHttpGetD(context, url, response);
//					}
//				} else if (ret.code == 401) {
//					st = PsAuthenServiceL.getStData(context, RID, true);
//					if (st == null || st.startsWith("USS")) {
//						response.onResult(-1, "");
//					} else {
//						executeHttpGetD(context, url, response);
//					}
//				} else {
//					response.onResult(ret.code, ret.body);
//				}
//			}
//		}
//	}
//	
//	public static void executeHttpPost(final Context context, final String url,
//			final String post, final IHttpResponse response) {
//		if (clientId == null) {
//			clientId = getClientId(context);
//			if (clientId == null) {
//				response.onResult(-1, "");
//			}
//		}
//		if (st == null) {
//			st = PsAuthenServiceL.getStData(context, RID, true);
//			if (st == null || st.startsWith("USS")) {
//				response.onResult(-1, st);
//			} else {
//				executeHttpPost(context, url, post, response);
//			}
////			PsAuthenServiceL.getStData(context, RID, new OnAuthenListener() {
////				public void onFinished(boolean ret, String data) {
////					if (ret) {
////						st = data;
////						executeHttpPost(context, url, post, response);
////					} else {
////						st = null;
////						response.onResult(-1, data);
////					}
////				}
////			}, true);
//		} else {
//			NetworkHttpRequest request = new NetworkHttpRequest();
//			NetworkHttpRequest.HttpReturn ret = request.executeHttpGet(context,
//					url);
//			if (ret.code == 0) {
//				response.onResult(ret.code, ret.body);
//			} else {
//				if (ret.code == 308) {
//					clientId = getClientId(context);
//					if (clientId == null) {
//						response.onResult(-1, "");
//					} else {
//						executeHttpGet(context, url, response);
//					}
//				} else if (ret.code == 401) {
//					st = PsAuthenServiceL.getStData(context, RID, true);
//					if (st == null || st.startsWith("USS")) {
//						response.onResult(-1, "");
//					} else {
//						executeHttpGet(context, url, response);
//					}
//				} else {
//					response.onResult(ret.code, ret.body);
//				}
//			}
//		}
//	}
}
