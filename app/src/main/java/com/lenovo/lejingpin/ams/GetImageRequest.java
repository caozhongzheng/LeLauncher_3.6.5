package com.lenovo.lejingpin.ams;

import java.io.ByteArrayInputStream;
import java.util.zip.GZIPInputStream;

import android.graphics.drawable.Drawable;

public class GetImageRequest implements AmsRequest {
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
			ByteArrayInputStream bs = null;
			GZIPInputStream input = null;
			try {
				input = new GZIPInputStream(new ByteArrayInputStream(bytes));
				mDrawable = Drawable.createFromStream(input, null);
			} catch (Exception e) {
				//e.printStackTrace();
				bs = new ByteArrayInputStream(bytes);
				mDrawable = Drawable.createFromStream(bs, null);
			}finally{
				try {
					if(input!=null){
						input.close();
					}
					if(bs!=null){
						bs.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
//			try {
//				ByteArrayInputStream bs = new ByteArrayInputStream(bytes);
//				mDrawable = Drawable.createFromStream(bs, null);
//			} catch(Throwable e) {
//				Log.i("HawaiiLog", "GetImageResponse can't create drawable !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//				mDrawable = null;
//			}
		}
	}

}
