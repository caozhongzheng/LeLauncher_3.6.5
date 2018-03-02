package com.lenovo.lejingpin.network;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import com.lenovo.launcher.R;
import android.util.Log;
import java.util.ArrayList;



import java.io.Serializable;
import java.lang.ref.SoftReference;

public final class AmsApplication implements Serializable {
     private String calculateSize(String size){
                if (size == null || size.length() == 0 ) {
                        return null;
                }
                float sizeInt = Float.parseFloat(size);
                if(sizeInt > 1048576){
                        return String.format("%.2f", sizeInt/1048576) + " M";
                } else if(sizeInt > 1024){
                        //return Math.round( sizeInt/1024 ) + " K";
                        return String.format("%.1f", sizeInt/1024)+" K";
                } else{
                        return Math.round( sizeInt ) + " B";
                }
        }

     	private static final long serialVersionUID = 1L;
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
		private String app_status;
		private String pay_status;
		private String download_count;
		private String comment_count;
		//yangmao add it for locktest
		private String app_Addr;
		public transient SoftReference<Drawable> []thumbdrawables = null; //thumbnail
		public transient SoftReference<Drawable> thumbdrawable; //thumbnail
        public String []thumbpaths = null; //thumbnail    
        public ArrayList<Drawable> previewResId = null; //thumbnail    
        private String auther;
        private boolean ispath;
        private boolean isnative;
        private boolean isalien;
		public AmsApplication() {
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
			app_status = "";
			pay_status = "";
			download_count = "";
			app_Addr = "";
			comment_count = "";
                        auther = "";
                        ispath = false;
                        isnative = false;
                        isalien = false;
                        thumbdrawable = null;
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
                  public String getAuther() {
                        return auther;
                }
                public void setAuther(String auther) {
                        this.auther = auther;
                }


		public String getApp_size() {
			return calculateSize(app_size);
		}

		public void setApp_size(String app_size) {
			this.app_size = app_size;
		}

		public String getApp_publishdate() {
			return app_publishdate;
		}
		
		//yangmao add for locktest
		public void setApp_Addr(String app_addr) {
			this.app_Addr = app_addr;
		}

		public String getApp_Addr() {
			return app_Addr;
		}
		//yangmao add for locktest

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
	    public SoftReference<Drawable>[] getthumbdrawables() {
               return thumbdrawables;
		}
		public void setthumbdrawables(SoftReference<Drawable> []thumb) {
		       this.thumbdrawables = thumb;
		}
        public SoftReference<Drawable> getthumbdrawable() {
              return thumbdrawable;
        }
        public void setthumbdrawable(SoftReference<Drawable> thumb) {
                this.thumbdrawable = thumb;
        }
        public String[] getthumbpaths() {
            return thumbpaths;
		}
		public void setthumbpaths(String path1,String path2,String path3) {
                    int i=0;
                    //Log.e("AmsApp","set thumbpath cell =========="+i+" path1="+path1+"path2="+path2+"path3="+path3);
                    if(path1 != null && path1.length() != 0) i++;
                    if(path2 != null && path2.length() != 0) i++;
                    if(path3 != null && path3.length() != 0) i++;
                    //Log.e("AmsApp","cell =========="+i+" path1="+path1+"path2="+path2+"path3="+path3+" lenght="+path3.length());
		    this.thumbpaths = new String[i];
                   if(path1 != null && path1.length() != 0) 
		this.thumbpaths[0] = path1;
                   if(path2 != null && path2.length() != 0) 
		    this.thumbpaths[1] = path2;
                   if(path3 != null && path3.length() != 0) 
		    this.thumbpaths[2] = path3;
                    /*for(int j =0;j<thumbpaths.length;j++){
                    Log.e("AmsApp","cell =========="+thumbpaths[j]);
                    }*/
		}
                public void setpreviewResId(Drawable resid){
                    if(previewResId == null){
                        previewResId = new ArrayList<Drawable>();
                    }
                    previewResId.add(resid);
                }
                public void setreplacePreviewResId(Drawable resid){
                    if(previewResId == null){
                        previewResId = new ArrayList<Drawable>();
                    }else{
                        previewResId.clear();
                    }
                    previewResId.add(resid);
                }
                public ArrayList<Drawable> getpreviewResId(){
                    return previewResId;
                }
		public void setIsPath(boolean ispath)
		{
			this.ispath = ispath;
		}
		public boolean getIsPath()
		{
			return ispath;
		}
		public void setIsNative(boolean ispath)
		{
			this.isnative = ispath;
		}
		public boolean getIsNative()
		{
			return isnative;
		}
		public void setIsAlien(boolean ispath)
		{
			this.isalien = ispath;
		}
		public boolean getIsAlien()
		{
			return isalien;
		}
        public Drawable getDetailDrawable(Context context,Drawable thumb)
        {
                if(thumb==null)
                	thumb = context.getResources().getDrawable(R.drawable.lemagicdownload_push_app_icon_def);
                return new BitmapDrawable(convertDrawable2BitmapByCanvas(thumb));
        }
        public static Bitmap convertDrawable2BitmapByCanvas(Drawable drawable) {
                Bitmap bitmap = Bitmap
                .createBitmap(
                                170,
                                150,
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565);
                Canvas canvas = new Canvas(bitmap);
                // canvas.setBitmap(bitmap);
                drawable.setBounds(0, 0, 170,
                                150);
                drawable.draw(canvas);
                return bitmap;
        }

}
