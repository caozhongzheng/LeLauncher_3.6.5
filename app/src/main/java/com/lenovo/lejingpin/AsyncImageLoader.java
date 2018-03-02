package com.lenovo.lejingpin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import com.lenovo.lejingpin.network.AmsRequest;
import com.lenovo.lejingpin.network.AmsSession;
import com.lenovo.lejingpin.network.AppInfoRequest5;
import com.lenovo.lejingpin.network.DeviceInfo;
import com.lenovo.lejingpin.network.AmsSession.AmsSessionCallback;
import com.lenovo.lejingpin.network.AppInfoRequest5.AppInfo;
import com.lenovo.lejingpin.network.AppInfoRequest5.AppInfoResponse5;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class AsyncImageLoader {
	private Context mcontext;
        private String TAG = "ImageLoader";
	private int mtype;
	private boolean misstore = false;
    public AsyncImageLoader(Context context,int type) {  
        mcontext = context;
        mtype = type;
        misstore = CreateImagebaseStoreFolder(context,type);
    }  
    public interface ImageCallback {  
        public void imageLoaded(final View image ,Drawable imageDrawable, int position,int j);  
    }  
    public interface ImagePathCallback {  
        public void ImagePathLoaded(String []imageUrls,int position,final ImageView image );  
    } 
    public void loadDrawable(final View image ,final String imageUrl,final int j, final int position ,final ImageCallback imageCallback) {
        new Thread() {  
            @Override  
            public void run() {  
                Drawable drawable = loadImageFromUrl(mcontext,mtype,getDownloadPath(mtype),imageUrl);  
                imageCallback.imageLoaded(image,drawable, position,j);   
            }  
        }.start();  
    } 
    
    public Drawable loadImageFromUrl(Context context,int type,String dirs,String url) {  
        URL m;  
        InputStream is = null;  
        Drawable d = null ;
        String filename =null;
        String filepath = null;
        if(misstore){
        	if(url.startsWith(getDownloadPath(type))){
        		filepath = url;
        	}else{
        	if(type==1||type==2){
        		filename = url.substring(url.lastIndexOf("/") + 1);
        		filename= filename.substring(0,filename.lastIndexOf("?"));
        	}else if(type==0){
        		filename = url.substring(url.lastIndexOf("wallpaper") + 1).replace("/", "-");
                    if(filename.contains(":")){
                       filename = filename.replace(":","");
                    }
        	}
	         	filepath = getDownloadPath(type)+"/"+filename;
        	}
	        File f1 = new File(filepath);
	        if(f1.exists()){
	        	d = Drawable.createFromPath(filepath);
	        	if(d!=null){
	        		return d;
	        	}
	        }
	        byte[] bs = new byte[1024];
	        try {  
	            m = new URL(url);  
	            is = (InputStream) m.getContent();  
	            File f = new File(filepath);
	            f.createNewFile();
	            int len = 0;
	            FileOutputStream os = new FileOutputStream(f);
	            while ((len = is.read(bs)) != -1) {
	                os.write(bs, 0, len);
	                os.flush();
	            }
	            d = Drawable.createFromPath(filepath);
	            if(d==null)
	            	d = Drawable.createFromStream(is, "src");
	            if(is!=null)
	            	is.close();
	            if(os!=null)
	            	os.close();
	        } catch (MalformedURLException e1) {  
	            e1.printStackTrace();  
	        		Log.d(TAG,"d00000!"+e1);
	        } catch (IOException e) {  
	        		Log.d(TAG,"d11111!"+e);
	            e.printStackTrace();  
	        }  
        }else{
        	try {  
                m = new URL(url);  
                is = (InputStream) m.getContent();  
                d = Drawable.createFromStream(is, "src");
                if(is!=null)
        			is.close();
            } catch (MalformedURLException e1) {  
                e1.printStackTrace();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }
        return d;  
    }
	private static String getDownloadPath(int type){
		String strdir =null;
		switch(type){
		case 0:
			strdir ="/wallpapers";
			break;
		case 1:
			strdir = "/themes";
			break;
		case 2:
			strdir = "/locks";
			break;
		default:
			break;
		}
		return LEJPConstant.getDownloadPath() + strdir;
	}
	private boolean CreateImagebaseStoreFolder(Context context,int type) {
		String status = Environment.getExternalStorageState();
		if (!Environment.MEDIA_MOUNTED.equals(status)) {
			return false;
		}
		File f = new File(getDownloadPath(type));
//		if(f == null) {
//			return false;
//		}
		if(!f.exists()) {
			if (!f.mkdirs()) {
				return false;
			}
		}
		return true;
	}
    public void requestAppInfo(final ImageView image ,final Context context,final int position,final String pkgname,final String vercode,
    		final ImagePathCallback imagepathcallback){
        DeviceInfo deviceInfo = DeviceInfo.getInstance(context);
        AmsSession.init(context, new AmsSessionCallback(){
        public void onResult(AmsRequest request, int code, byte[] bytes) {
                if(code!=200){
                }else{
                	getAppInfo(image,context,position,pkgname,vercode,imagepathcallback);
                }
        	}
        },deviceInfo.getWidthPixels(),deviceInfo.getHeightPixels(), deviceInfo.getDensityDpi());
        
    }
    private void getAppInfo(final ImageView image ,final Context context,final int position,final String package_name,final String version_code,
    		final ImagePathCallback imagepathcallback){
        AppInfoRequest5 infoRequest = new AppInfoRequest5(context);
        infoRequest.setData(package_name, version_code);
        AmsSession.execute(context, infoRequest, new AmsSessionCallback(){
            public void onResult(AmsRequest request, int code, byte[] bytes) {
            boolean success= false;
                if( code == 200 ){
                    success = true;
                    if(bytes != null) {
                        AppInfoResponse5 infoResponse = new AppInfoResponse5();
                        infoResponse.parseFrom(bytes);
                        boolean successResponse= infoResponse.getIsSuccess();
                        if(successResponse){
                            AppInfo responseApp = infoResponse.getAppInfo();
                            final String firstSnapPath = responseApp.getSnapList().toString().replace("[", "").replace("]", "");
                            imagepathcallback.ImagePathLoaded(firstSnapPath.split(","),position,image); 
                        }
                    }
                }
            }
        });
    }
}
