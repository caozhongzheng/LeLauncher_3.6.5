package com.lenovo.lejingpin.ams;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.lenovo.lejingpin.ams.AppInfoRequest.Snapshot;
import com.lenovo.lejingpin.ams.RegistClientInfoRequest.RegistClientInfoResponse;
import com.lenovo.lsf.util.PsDeviceInfo;

public class GetSnapshotUriRequest implements AmsRequest {
	private String mPkgName;
	private Context mContext;
	private String mVersionCode;

	public GetSnapshotUriRequest(Context context) {
		mContext = context;
	}

	public void setData(String pkgName, String versionCode) {
		mPkgName = pkgName;
		mVersionCode = versionCode;
	}

	public String getUrl() {
		// TODO Auto-generated method stub
		return AmsSession.sAmsRequestHost + "ams/3.0/snapurilist.htm" + "?pn="
				+ mPkgName + "&l=" + PsDeviceInfo.getLanguage(mContext)
				+ "&vc=" + mVersionCode + "&pa="
				+ RegistClientInfoResponse.getPa();
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

	public static final class GetSnapshotUriResponse implements AmsResponse {
		private ArrayList<Snapshot> mSnapshots = new ArrayList<Snapshot>();
		private boolean mIsSuccess = true;

		public boolean getIsSuccess() {
			return mIsSuccess;
		}
		public Snapshot getItem(int i) {
			return mSnapshots.get(i);
		}

		public int getItemCount() {
			return mSnapshots.size();
		}

		public ArrayList<Snapshot> getItemList() {
			return mSnapshots;
		}

		public void parseFrom(byte[] bytes) {
			// TODO Auto-generated method stub
			String sSnapshot = new String(bytes);
			Log.i("AppStore", sSnapshot);
			try {
				JSONObject jsonObject = new JSONObject(sSnapshot);
				JSONArray jsArray = new JSONArray(jsonObject.getString("data"));
				if (jsArray.length() != 0) {
					for (int i = 0; i < jsArray.length(); i++) {
						Snapshot snapshot = new Snapshot();
						snapshot.setAppimgPath(jsArray.getString(i));
						mSnapshots.add(snapshot);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				mIsSuccess = false;
				e.printStackTrace();
			}

		}

	}

}
