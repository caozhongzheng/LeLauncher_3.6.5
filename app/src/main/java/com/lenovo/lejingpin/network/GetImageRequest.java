package com.lenovo.lejingpin.network;

import java.io.ByteArrayInputStream;

import android.graphics.drawable.Drawable;
import android.util.Log;

public class GetImageRequest implements AmsRequest {
	private static final String TAG = "zdx";
	private String mImageUrl;

	public void setData(String imageUrl) {
		mImageUrl = imageUrl;
	}

	public String getUrl() {
		// TODO Auto-generated method stub
		if (mImageUrl.contains("http:")) {
			return mImageUrl;
		}
		return AmsSession.sAmsRequestHost + mImageUrl;
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
		return "low";
	}

	public static final class GetImageResponse implements AmsResponse {
		private Drawable mDrawable = null;

		public Drawable getDrawable(){
			return mDrawable;
		}

		public void parseFrom(byte[] bytes){
			if (bytes == null){
				mDrawable = null;
				return;
			}
			
			try {
				ByteArrayInputStream bs = new ByteArrayInputStream(bytes);
				mDrawable = Drawable.createFromStream(bs, null);
			} catch(Exception e) {
				Log.i(TAG, "GetImageResponse can't get drawable !!!!!!!!!!!");
				mDrawable = null;
			}
		}
	}
	/***RK_ID:RK_BUGFIX_171707 AUT:zhanglz1@lenovo.com. DATE:2012-10-30.S***/       
	@Override
	public boolean getIsForDownloadNum() {
		// TODO Auto-generated method stub
		return false;
	}
	/***RK_ID:RK_BUGFIX_171707 AUT:zhanglz1@lenovo.com. DATE:2012-10-30.E***/  

}
