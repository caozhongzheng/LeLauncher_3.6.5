package com.lenovo.lejingpin.network;

import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.Date;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lenovo.launcher.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.telephony.TelephonyManager;
import android.util.Log;

	
public final class WallpaperResponse implements AmsResponse {
    private ArrayList<ApplicationData> mApplications = new ArrayList<ApplicationData>();
    private boolean mIsFinish = false;
    private boolean mIsSuccess = true;
    private String mTypes = "wallpapers";
    public static final String HOST_EXTRA_WALLPAPER ="http://launcher.lenovo.com/launcher/";
    private int phoneWidth;
    private int mDataIndex = 0;

    public void setResponseType(String types,int width){ 
        mTypes = types;
        phoneWidth =  width;
    }
    public void setResponseType(String types){ 
        mTypes = types;
    }
    public void setDataIndex(int index){
        mDataIndex = index;
    }
    public boolean getIsSuccess() {
        return mIsSuccess;
    }
    public ApplicationData getApplicationItem(int i) {
	return mApplications.get(i);
    }
    public int getApplicationItemCount() {
	return mApplications.size();
    }

    public ArrayList<ApplicationData> getApplicationItemList() {
	return mApplications;
    }

    public boolean isFinish() {
	return mIsFinish;
    }
		
    public void parseFrom(byte[] bytes) {
	String sCategory = new String(bytes);
	try{
	   JSONObject jsonObject = new JSONObject(sCategory);
	   if (jsonObject.has("endpage"))
		   	mIsFinish = jsonObject.getInt("endpage") == 0 ? true: false;
	        JSONArray jsArray = jsonObject.getJSONArray(mTypes);//获取JSONArray
	        int length = jsArray.length();
                Log.e("WallpaperResponse","length================="+length+"type="+mTypes+" 2phoneWidth="+phoneWidth*2);
	        if (length != 0) {
		    for (int i = 0; i < length; i++) {
		        JSONObject jsonObject2 = jsArray.getJSONObject(i);
		        ApplicationData application = new ApplicationData();
                if(mTypes.equals("wallpapers")){
                	if(jsonObject2.has("id")){
                		application.setAppId(jsonObject2.getInt("id"));
                	}
                	application.setViewPosition(mDataIndex+i);
                	application.setAppName(ToolKit.convertErrorData(jsonObject2.getString("name")));
                	application.setPackage_name(ToolKit.convertErrorData(jsonObject2.getString("id")));
                    application.setIsDynamic(jsonObject2.getInt("dynamic"));
                    if(jsonObject2.getInt("dynamic") == 1){
                    	application.setPackage_name(ToolKit.convertErrorData(jsonObject2.getString("packagename")));
                    }
                    application.setDownload_count(jsonObject2.getInt("rank"));//?
                    //application.setApp_size(jsonObject2.getString("size"));
                    application.setAuther(ToolKit.convertErrorData(jsonObject2.getString("author")));
                    application.setBgType(ToolKit.convertErrorData(jsonObject2.getString("source")));
                    //application.setUrl(ToolKit.convertErrorData(jsonObject2.getString("url")));
                    JSONObject jsobject = jsonObject2.getJSONObject("fobjs"); 
                    application.setPreviewAddr(ToolKit.convertErrorData(jsobject.getString("_394x328")));
                    application.setIconUrl(ToolKit.convertErrorData(jsobject.getString("_960x800")));
                    if(jsonObject2.getInt("dynamic") != 1){
                    int width = phoneWidth*2;
                    if(width<= 960){
                    	application.setUrl(ToolKit.convertErrorData(jsobject.getString("_960x800")));
                    }else if(width > 960 && width <= 1440){
                    	application.setUrl(ToolKit.convertErrorData(jsobject.getString("_1440x1280")));
                    }else if(width > 1440){
                    	application.setUrl(ToolKit.convertErrorData(jsobject.getString("_2160x1920")));
                    }
                    }else{
                    	application.setUrl(ToolKit.convertErrorData(jsobject.getString("_960x800")));
                    }
                            //960x800,1440x1280 ,2160x1920
                }else if(mTypes.equals("specials") || mTypes.equals("cate")){
                	application.setAppName(ToolKit.convertErrorData(jsonObject2.getString("nm")));
                	if(jsonObject2.has("id")){
                		application.setPackage_name(ToolKit.convertErrorData(jsonObject2.getString("id")));
                	}
                	application.setPreviewAddr(jsonObject2.getString("ic"));
                }
                mApplications.add(application);
		    }
	        }
	}catch(Exception e){
	    mIsSuccess = false;
        Log.e("WallpaperResponse","length=========================error"+e);
	    e.printStackTrace();
	}
    }
		
	public static class ApplicationData implements Serializable {
		private static final long serialVersionUID = 3830870434843595687L;
	
		private int id;
		public String name; //图片名称
		private int downloadCount;//下载次数
		private String size;//大小
		private String price;//价格
		private String app_versioncode;
		private String package_name;
		private int mViewPosition;
		
		//新增
		private String type; // 类型
		private String res;// 分辨率
		private String auther;//作者
		private String uploadTime;//上传时间
		private String detail;//上传时间
		private String url;//图片下载地址
		public String iconUrl;//图片icon地址            
		public String previewAddr;//图片预览图地址           
		   
		private transient SoftReference<Drawable> thumbdrawable; //thumbnail  
		private transient SoftReference<Drawable> predrawable; //preview 
		private transient SoftReference<Drawable> icondrawable; //icon
		private int thumbresid; //thumbnail 
		private int previewresid; // thumbnail
		private boolean isnative;
		private boolean isdelete;
		public int isDynamic;
		public ApplicationData() {
			id = 0;
			name = "";
			downloadCount = 0;
			size = "0";
			price = "0";
			type = "";
			res = "";
			auther = "";
			uploadTime = null;
			detail = "";
			url = "";
			iconUrl = null;
			app_versioncode = "";
			package_name = "";
			previewAddr = "";
			thumbdrawable = null;
			predrawable = null;
			icondrawable = null;
			isnative = false;
			isdelete = false;
			thumbresid = 0; // thumbnail
			previewresid = 0; // thumbnail
			isDynamic = 0;
		}
	     private String calculateSize(String size){
             if (size == null || size.length() == 0 ) {
                     return null;
             }
             float sizeInt = Float.parseFloat(size);
             if(sizeInt > 1048576){
                     return String.format("%.2f", sizeInt/1048576) + " M";
             } else if(sizeInt > 1024){
                     return String.format("%.1f", sizeInt/1024)+" K";
             } else{
                     return Math.round( sizeInt ) + " B";
             }
     }
		public String getApp_versioncode() {
			return app_versioncode;
		}

		public void setApp_versioncode(String app_versioncode) {
			this.app_versioncode = app_versioncode;
		}
		public String getPackage_name() {
			return package_name;
		}

		public void setPackage_name(String package_name) {
			this.package_name = package_name;
		}

		public String getApp_price() {
			return price;
		}

		public void setApp_price(String price) {
			this.price = price;
		}

		public String getApp_size() {
			return calculateSize(size);
		}

		public void setApp_size(String size) {
			this.size = size;
		}

		public String getAppName() {
			return name;
		}

		public void setAppName(String appName) {
			this.name = appName;
		}

		public int getAppId() {
			return id;
		}

		public void setAppId(int id) {
			this.id = id;
		}

		public int getDownload_count() {
			return downloadCount;
		}
		public void setDownload_count(int downloadCount) {
			this.downloadCount = downloadCount;
		}
		
		public String getBgType() {
			return type;
		}
		public void setBgType(String bgType) {
			this.type = bgType;
		}
		
		public String getRes() {
			return res;
		}
		public void setRes(String res) {
			this.res = res;
		}
		
		public String getAuther() {
			return auther;
		}
		public void setAuther(String auther) {
			this.auther = auther;
		}
		
		public String getUpload_time() {
			return uploadTime;
		}
		public void setUpload_time(String upload_time) {
			this.uploadTime = upload_time;
		}
		
		public String getDetail() {
			return detail;
		}
		public void setDetail(String detail) {
			this.detail = detail;
		}

		public void setIsNative(boolean ispath) {
			this.isnative = ispath;
		}

		public boolean getIsNative() {
			return isnative;
		}

		public void setIsDelete(boolean ispath) {
			this.isdelete = ispath;
		}

		public boolean getIsDelete() {
			return isdelete;
		}

		public void setIsDynamic(int ispath) {
			this.isDynamic = ispath;
		}

		public int getIsDynamic() {
			return isDynamic;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public String getIconUrl() {
			return iconUrl;
		}
		public void setIconUrl(String iconUrl) {
			this.iconUrl = iconUrl;
		}
		public String getPreviewAddr() {
			return previewAddr;
		}
		public void setPreviewAddr(String previewAddr) {
			this.previewAddr = previewAddr;
		}
		public SoftReference<Drawable> getthumbdrawable() {
			return thumbdrawable;
		}
		public void setthumbdrawable(SoftReference<Drawable> thumb) {
			this.thumbdrawable = thumb;
		}
		public int getthumbdrawableresid() {
			return thumbresid;
		}
		public void setthumbdrawableresid(int resid) {
			this.thumbresid = resid;
		}
		public int getpreviewdrawableresid() {
			return previewresid;
		}
		public void setpreviewdrawableresid(int resid) {
			this.previewresid = resid;
		}
		public SoftReference<Drawable> getPriviewDrawable()
		{
			return predrawable;
		}
		public void SetPriviewDrawable(SoftReference<Drawable> drawable)
		{
			this.predrawable = drawable;
		}

		public static Bitmap convertDrawable2BitmapByCanvas(Drawable drawable) {
			Bitmap bitmap = Bitmap.createBitmap(
							170,
							150,
							drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
									: Bitmap.Config.RGB_565);
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, 170, 150);
			drawable.draw(canvas);
			return bitmap;
		}
		public void setViewPosition(int index){
			mViewPosition = index;
        }
        public int getViewPosition(){
            return mViewPosition;
        }
	}
}



