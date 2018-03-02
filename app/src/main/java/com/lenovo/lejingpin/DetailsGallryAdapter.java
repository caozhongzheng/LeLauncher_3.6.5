package com.lenovo.lejingpin;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import java.io.File;
import java.lang.ref.SoftReference;

import android.os.Environment;

import com.lenovo.launcher2.customizer.Utilities;


import android.net.Uri;

import android.util.Log;

import com.lenovo.lejingpin.AsyncImageLoader;
import com.lenovo.lejingpin.LEJPConstant;
import com.lenovo.launcher.R;
import com.lenovo.lejingpin.AsyncImageLoader.ImageCallback;
import com.lenovo.lejingpin.AsyncImageLoader.ImagePathCallback;
import com.lenovo.lejingpin.network.AmsApplication;
import com.lenovo.lejingpin.network.WallpaperResponse.ApplicationData;

public class DetailsGallryAdapter extends BaseAdapter { 
	LEJPConstant mleconstant;
        private String TAG = "GalleryAdapter";
	private LayoutInflater mInflater;
	private int mtypeindex;
        private boolean flag = false;
	private int mcurrpos;
	ImageView detail_classic_item_image;
	ImageView detail_classic_item_remark;
	private AsyncImageLoader masyncImageLoader;
	private Context mcontext;
	private boolean mIsFhd = false;
	public DetailsGallryAdapter(Context context ,LEJPConstant leconstant,AsyncImageLoader asyncImageLoader)
	{
		mcontext = context;
		mleconstant = leconstant;
		masyncImageLoader = asyncImageLoader;
	    mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	public DetailsGallryAdapter(Context context ,LEJPConstant leconstant,AsyncImageLoader asyncImageLoader,boolean isFhd)
	{
		mcontext = context;
		mleconstant = leconstant;
		masyncImageLoader = asyncImageLoader;
	    mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    mIsFhd = isFhd;
	}
	@Override  
	public int getCount() {  
        try{
        if(mtypeindex == 0 ){
        	return mleconstant.mServiceWallPaperDataList.size();
        } else if(mtypeindex == 10){
                Log.e("yumina0227","flag========"+flag);
                if(flag){
        	return mleconstant.mServiceLocalWallPaperDataList.size();
                }else{
        	return mleconstant.mServiceLocalWallPaperDataList.size()-1;
                }
        }else if(mtypeindex == 1 ){
        	return mleconstant.mServiceThemeAmsDataList.size();
        }else if(mtypeindex == 11){
        	return mleconstant.mServiceLocalThemeAmsDataList.size();
        }else if(mtypeindex == 2){
        	return mleconstant.mServiceLockAmsDataList.size();
        }else if(mtypeindex == 12){
        	return mleconstant.mServiceLocalLockAmsDataList.size();
        }
        }catch(Exception e){
            return 0;
        }
        return 0;
	}  
	public void setOutSideFlag(boolean bfalg)
	{
                Log.e("yumina0227","flag===set====="+bfalg);
		flag = bfalg;
	}
	public void setTypeIndex(int typeindex)
	{
		mtypeindex = typeindex;
	}
	public void setCurSelectpos(int selectpos)
	{
		mcurrpos = selectpos;
	}
	@Override  
	public Object getItem(int position) {  
        try{
        if(mtypeindex == 0 || mtypeindex == 10){
        	return mleconstant.mServiceWallPaperDataList.get(position);  
        }else if(mtypeindex == 10){
        	return mleconstant.mServiceLocalWallPaperDataList.get(position);  
        }else if(mtypeindex ==1 ){
        	return mleconstant.mServiceThemeAmsDataList.get(position);  
        }else if(mtypeindex ==11){
        	return mleconstant.mServiceLocalThemeAmsDataList.get(position);  
        }else if(mtypeindex == 2){
        	return mleconstant.mServiceLockAmsDataList.get(position);  
        }else if(mtypeindex == 12){
        	return mleconstant.mServiceLocalLockAmsDataList.get(position);  
        }
        }catch(Exception e){
            return 0;
        }
        return 0;
	}  
	@Override  
	public long getItemId(int position) {  
		return 0;  
	}  
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	Holder holder = (Holder)msg.obj;
        	holder.mimg.setImageDrawable(holder.mdb);
        }
    };
    @Override  
    public View getView(int position, View convertView, ViewGroup parent) {  
    	FrameLayout detailclassicitemview = null;
    	final int pos = position;
	if (convertView == null) {
            if(mtypeindex == 0 || mtypeindex == 10){
            	if(mIsFhd){
            		detailclassicitemview = (FrameLayout)mInflater.inflate(R.layout.detail_classic_wallpaperitem_blade, null);
            	}else{
            		
            		detailclassicitemview = (FrameLayout)mInflater.inflate(R.layout.detail_classic_wallpaperitem, null);
            	}
            }else{
            	if(mIsFhd){
            		detailclassicitemview = (FrameLayout)mInflater.inflate(R.layout.detail_classic_item_blade, null);
            	}else{
            		
            		detailclassicitemview = (FrameLayout)mInflater.inflate(R.layout.detail_classic_item, null);
            	}
            }
	    detail_classic_item_image = (ImageView)detailclassicitemview.findViewById(R.id.detail_classic_item_image);
	    detail_classic_item_remark = (ImageView)detailclassicitemview.findViewById(R.id.detail_classic_item_remark);
	}
	else{
		if(convertView instanceof FrameLayout){
			detailclassicitemview = (FrameLayout)convertView;
		}
	}
        if(mtypeindex == 0 && (mleconstant.mServiceWallPaperDataList.size()-1 >= pos) ){
            final ApplicationData appdata = mleconstant.mServiceWallPaperDataList.get(pos);
            final SoftReference <Drawable> softdb = appdata.getthumbdrawable();
            if(softdb==null){
        	detail_classic_item_image.setImageResource(R.drawable.le_wallpaper_magicdownload_push_app_icon_def);
    	   	masyncImageLoader.loadDrawable(detail_classic_item_image,appdata.previewAddr, 0,0,new ImageCallback() {  
                    public void imageLoaded(View image,Drawable imageDrawable, int postion,int j) {  
                        try{
                    	mleconstant.mServiceWallPaperDataList.get(pos).setthumbdrawable(new SoftReference <Drawable>(imageDrawable));
                    	if(image instanceof ImageView){
	                    	Message message = handler.obtainMessage(0, new Holder((ImageView)image,imageDrawable));  
	                        handler.sendMessage(message);
                    	}
                        }catch(Exception e){
                            Log.e(TAG," a exception e==================="+e); 
                        }
                    }  
                });  
        	}else{
        		Drawable db = softdb.get();
        		if(db==null){
            		detail_classic_item_image.setImageResource(R.drawable.le_wallpaper_magicdownload_push_app_icon_def);
        			masyncImageLoader.loadDrawable(detail_classic_item_image,appdata.previewAddr, 0,0,new ImageCallback() {  
                        public void imageLoaded(View image,Drawable imageDrawable, int postion,int j) {  
                        try{
                        	mleconstant.mServiceWallPaperDataList.get(pos).setthumbdrawable(new SoftReference <Drawable>(imageDrawable));
                        	if(image instanceof ImageView){
                        		Message message = handler.obtainMessage(0, new Holder((ImageView)image,imageDrawable));  
                        		handler.sendMessage(message);
                        	}
                        }catch(Exception e){
                            Log.e(TAG," b exception e==================="+e); 
                        }
                        }  
                    });  
        		}else
        			detail_classic_item_image.setImageDrawable(db);
        	}
        }else if(mtypeindex == 1 && (mleconstant.mServiceThemeAmsDataList.size()-1 >= pos) ){
        	final AmsApplication Amsdata = mleconstant.mServiceThemeAmsDataList.get(pos);
        	if(!Amsdata.getIsPath()){
        		detail_classic_item_image.setImageResource(R.drawable.lemagicdownload_push_app_icon_def);
        		masyncImageLoader.requestAppInfo(detail_classic_item_image,mcontext,position,Amsdata.getPackage_name(),Amsdata.getApp_versioncode(),
            			new ImagePathCallback() {
            		public void ImagePathLoaded(String []imageUrls,int position,final ImageView imag){
            			if(imageUrls!=null){
	            			mleconstant.mServiceThemeAmsDataList.get(position).thumbpaths = imageUrls;
	            			mleconstant.mServiceThemeAmsDataList.get(position).setIsPath(true);
	            			masyncImageLoader.loadDrawable(imag,imageUrls[0],0,pos,new ImageCallback() {  
			                    public void imageLoaded(View image,Drawable imageDrawable, int postion,int j) { 
			                    	Log.d("d","postion="+postion);
                                                try{
			                    	mleconstant.mServiceThemeAmsDataList.get(postion).thumbdrawable=new SoftReference <Drawable>(imageDrawable);
			                    	if(image instanceof ImageView){
			                    		Message message = handler.obtainMessage(0, new Holder((ImageView)image,imageDrawable));  
			                    		handler.sendMessage(message);
			                    	}
                        }catch(Exception e){
                            Log.e(TAG," a exception e==================="+e); 
                        }
			                    }
	            			});
            			}
            		}
            	});
            }else {
	        	if(Amsdata.thumbdrawable==null){
                    try{
	        		detail_classic_item_image.setImageResource(R.drawable.lemagicdownload_push_app_icon_def);
	    	   		masyncImageLoader.loadDrawable(detail_classic_item_image,Amsdata.thumbpaths[0],0,0,new ImageCallback() {  
	                    public void imageLoaded(View image,Drawable imageDrawable, int postion,int j) {  
	                    	mleconstant.mServiceThemeAmsDataList.get(pos).thumbdrawable= new SoftReference <Drawable>(imageDrawable);
	                    	if(image instanceof ImageView){
	                    		Message message = handler.obtainMessage(0, new Holder((ImageView)image,imageDrawable));  
	                    		handler.sendMessage(message); 
	                    	}
	                   }  
	                });  
                        }catch(Exception e){
                        }
	        	}else{
	        		Drawable db = Amsdata.thumbdrawable.get();
	        		if(db==null){
	        			detail_classic_item_image.setImageResource(R.drawable.lemagicdownload_push_app_icon_def);
		    	   		masyncImageLoader.loadDrawable(detail_classic_item_image,Amsdata.thumbpaths[0],0,0,new ImageCallback() {  
		                    public void imageLoaded(View image,Drawable imageDrawable, int postion,int j) {  
                                        try{
		                    	mleconstant.mServiceThemeAmsDataList.get(pos).thumbdrawable= new SoftReference <Drawable>(imageDrawable);
		                    	if(image instanceof ImageView){
		                    		Message message = handler.obtainMessage(0, new Holder((ImageView)image,imageDrawable));  
		                    		handler.sendMessage(message); 
		                    	}	
                        }catch(Exception e){
                            Log.e(TAG," c exception e==================="+e); 
                        }
		                   }  
		                });  
	        		}
	        		else
	        			detail_classic_item_image.setImageDrawable(db);
	        	}
            }
        }
        //yangmao add for lock
        
        else if(mtypeindex == 2 && (mleconstant.mServiceLockAmsDataList.size()-1 >= pos) ){
        	final AmsApplication Amsdata = mleconstant.mServiceLockAmsDataList.get(pos);
        	if(!Amsdata.getIsPath()){
        		detail_classic_item_image.setImageResource(R.drawable.lemagicdownload_push_app_icon_def);
        		masyncImageLoader.requestAppInfo(detail_classic_item_image,mcontext,position,Amsdata.getPackage_name(),Amsdata.getApp_versioncode(),
            			new ImagePathCallback() {
            		public void ImagePathLoaded(String []imageUrls,int position,final ImageView imag){
            			if(imageUrls!=null){
	            			mleconstant.mServiceLockAmsDataList.get(position).thumbpaths = imageUrls;
	            			mleconstant.mServiceLockAmsDataList.get(position).setIsPath(true);
	            			masyncImageLoader.loadDrawable(imag,imageUrls[0],0,pos,new ImageCallback() {  
			                    public void imageLoaded(View image,Drawable imageDrawable, int postion,int j) { 
			                    	Log.d("d","postion="+postion);
                                                try{
			                    	mleconstant.mServiceLockAmsDataList.get(postion).thumbdrawable=new SoftReference <Drawable>(imageDrawable);
			                    	if(image instanceof ImageView){
			                    		Message message = handler.obtainMessage(0, new Holder((ImageView)image,imageDrawable));  
			                    		handler.sendMessage(message);
			                    	}
                        }catch(Exception e){
                            Log.e(TAG," a exception e==================="+e); 
                        }
			                    }
	            			});
            			}
            		}
            	});
            }else {
	        	if(Amsdata.thumbdrawable==null){
                    try{
	        		detail_classic_item_image.setImageResource(R.drawable.lemagicdownload_push_app_icon_def);
	    	   		masyncImageLoader.loadDrawable(detail_classic_item_image,Amsdata.thumbpaths[0],0,0,new ImageCallback() {  
	                    public void imageLoaded(View image,Drawable imageDrawable, int postion,int j) {  
	                    	mleconstant.mServiceLockAmsDataList.get(pos).thumbdrawable= new SoftReference <Drawable>(imageDrawable);
	                    	if(image instanceof ImageView){
	                    		Message message = handler.obtainMessage(0, new Holder((ImageView)image,imageDrawable));  
	                    		handler.sendMessage(message); 
	                    	}
	                   }  
	                });  
                        }catch(Exception e){
                        }
	        	}else{
	        		Drawable db = Amsdata.thumbdrawable.get();
	        		if(db==null){
	        			detail_classic_item_image.setImageResource(R.drawable.lemagicdownload_push_app_icon_def);
		    	   		masyncImageLoader.loadDrawable(detail_classic_item_image,Amsdata.thumbpaths[0],0,0,new ImageCallback() {  
		                    public void imageLoaded(View image,Drawable imageDrawable, int postion,int j) {  
                                        try{
		                    	mleconstant.mServiceLockAmsDataList.get(pos).thumbdrawable= new SoftReference <Drawable>(imageDrawable);
		                    	if(image instanceof ImageView){
		                    		Message message = handler.obtainMessage(0, new Holder((ImageView)image,imageDrawable));  
		                    		handler.sendMessage(message); 
		                    	}	
                        }catch(Exception e){
                            Log.e(TAG," c exception e==================="+e); 
                        }
		                   }  
		                });  
	        		}
	        		else
	        			detail_classic_item_image.setImageDrawable(db);
	        	}
            }
        }
        
        /*else if(mtypeindex == 2 && (mleconstant.mServiceLockAmsDataList.size()-1 >= pos) ){
        	final AmsApplication Amsdata = mleconstant.mServiceLockAmsDataList.get(pos);
        	if(!Amsdata.getIsPath()){
        		detail_classic_item_image.setImageResource(R.drawable.lemagicdownload_push_app_icon_def);
        		masyncImageLoader.requestAppInfo(detail_classic_item_image,mcontext,position,Amsdata.getPackage_name(),Amsdata.getApp_versioncode(),
            			new ImagePathCallback() {
            		public void ImagePathLoaded(String []imageUrls,int position,final ImageView imag){
            			mleconstant.mServiceLockAmsDataList.get(position).thumbpaths = imageUrls;
            			mleconstant.mServiceLockAmsDataList.get(position).setIsPath(true);
            			masyncImageLoader.loadDrawable(imag,imageUrls[0],0,pos,new ImageCallback() {  
		                    public void imageLoaded(View image,Drawable imageDrawable, int postion,int j) { 
		                    	Log.d("d","postion="+postion);
                                        try{
		                    	mleconstant.mServiceLockAmsDataList.get(postion).thumbdrawable=new SoftReference <Drawable>(imageDrawable);
		                    	Message message = handler.obtainMessage(0, new Holder((ImageView)image,imageDrawable));  
		                        handler.sendMessage(message);
                        }catch(Exception e){
                            Log.e(TAG," d exception e==================="+e); 
                        }
		                    }
            			});
            		}
            	});
            }else{
	        	if(Amsdata.thumbdrawable==null){
	        		detail_classic_item_image.setImageResource(R.drawable.lemagicdownload_push_app_icon_def);
	    	   		masyncImageLoader.loadDrawable(detail_classic_item_image,Amsdata.thumbpaths[0],0,0,new ImageCallback() {  
	                    public void imageLoaded(View image,Drawable imageDrawable, int postion,int j) {  
                            try{
	                    	mleconstant.mServiceLockAmsDataList.get(pos).thumbdrawable= new SoftReference <Drawable>(imageDrawable);
	                    	Message message = handler.obtainMessage(0, new Holder((ImageView)image,imageDrawable));  
	                        handler.sendMessage(message); 
                        }catch(Exception e){
                            Log.e(TAG," e exception e==================="+e); 
                        }
	                   }  
	                });  
	        	}else{
	        		Drawable db = Amsdata.thumbdrawable.get();
	        		if(db==null){
	        			detail_classic_item_image.setImageResource(R.drawable.lemagicdownload_push_app_icon_def);
		    	   		masyncImageLoader.loadDrawable(detail_classic_item_image,Amsdata.thumbpaths[0],0,0,new ImageCallback() {  
		                    public void imageLoaded(View image,Drawable imageDrawable, int postion,int j) {  
                                        try{
		                    	mleconstant.mServiceLockAmsDataList.get(pos).thumbdrawable= new SoftReference <Drawable>(imageDrawable);
		                    	Message message = handler.obtainMessage(0, new Holder((ImageView)image,imageDrawable));  
		                        handler.sendMessage(message); 
                        }catch(Exception e){
                            Log.e(TAG," e exception e==================="+e); 
                        }
		                   }  
		                });  
	        		}
	        		else
	        			detail_classic_item_image.setImageDrawable(db);
	        	}
            }
        }*/
        else if(mtypeindex == 11 && (mleconstant.mServiceLocalThemeAmsDataList.size()-1 >= pos) ){
            final AmsApplication Amsdata = mleconstant.mServiceLocalThemeAmsDataList.get(pos);
             if(!Amsdata.getIsNative()){
             String filePath = Amsdata.thumbpaths[0];
             Log.e("yumina","filepaht================"+filePath);
                        File icon = new File(filePath);
                        detail_classic_item_image.setImageURI(Uri.fromFile(icon));
             }else{
//                        detail_classic_item_image.setScaleType(ImageView.ScaleType.FIT_CENTER);
//                        detail_classic_item_image.setScaleType(ImageView.ScaleType.FIT_XY);

                        detail_classic_item_image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        detail_classic_item_image.setImageDrawable((Amsdata.getpreviewResId()).get(0));
             }

             }
        
        //yangmao add for lock
        
        else if(mtypeindex == 12 && (mleconstant.mServiceLocalLockAmsDataList.size()-1 >= pos) ){
            final AmsApplication Amsdata = mleconstant.mServiceLocalLockAmsDataList.get(pos);
             if(!Amsdata.getIsNative()){
             String filePath = Amsdata.thumbpaths[0];
             Log.e("yumina","filepaht================"+filePath);
                        File icon = new File(filePath);
                        detail_classic_item_image.setImageURI(Uri.fromFile(icon));
             }else{
                        //detail_classic_item_image.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        //detail_classic_item_image.setScaleType(ImageView.ScaleType.FIT_XY);

                        detail_classic_item_image.setImageDrawable((Amsdata.getpreviewResId()).get(0));
             }

             }
        
        /*else if(mtypeindex == 12){//local lockscreen
        }else if(mtypeindex == 12 && (mleconstant.mServiceLocalLockAmsDataList.size()-1 >= pos) ){
            final AmsApplication Amsdata = mleconstant.mServiceLocalLockAmsDataList.get(pos);
             if(!Amsdata.getIsNative()){
             String filePath = Amsdata.thumbpaths[0];
             //Log.e("yumina","filepaht================"+filePath);
                        File icon = new File(filePath);
                        detail_classic_item_image.setImageURI(Uri.fromFile(icon));
             }else{
                 if(Amsdata.getpreviewResId() == null){
             String filePath = Amsdata.thumbpaths[0];
             //Log.e("yumina","filepaht================"+filePath);
                        File icon = new File(filePath);
                        detail_classic_item_image.setImageURI(Uri.fromFile(icon));
                 }else{
                        detail_classic_item_image.setImageDrawable((Amsdata.getpreviewResId()).get(0));
                 }
             }
        }*/else if(mtypeindex == 10 && (mleconstant.mServiceLocalWallPaperDataList.size()-1 >= pos) ){
            final ApplicationData appdata = mleconstant.mServiceLocalWallPaperDataList.get(pos);

			if (!appdata.getIsNative()) {
				String iconUrl = appdata.previewAddr;
				Log.e("yumina", "filepaht 1================" + iconUrl);
				if (appdata.isDynamic == 1) {
					if (iconUrl == null || iconUrl.equals("")) {
						SoftReference<Drawable> thumb = appdata.getthumbdrawable();
						if (thumb == null || thumb.get() == null) {
							String mPkgName = appdata.getPackage_name();
							Drawable tmpdraw = getThumbnailFromApk(mPkgName);
							appdata.setthumbdrawable(new SoftReference<Drawable>(tmpdraw));
							detail_classic_item_image.setImageDrawable(tmpdraw);
						} else {
							detail_classic_item_image.setImageDrawable(appdata.getthumbdrawable().get());
						}
					} else {
						File icon = new File(iconUrl);
						Log.e("yumina", "filepaht 2======icon uri:" + Uri.fromFile(icon));
//						detail_classic_item_image.setImageURI(Uri.fromFile(icon));
						Drawable tmpdraw = Drawable.createFromPath(iconUrl);
						appdata.setthumbdrawable(new SoftReference<Drawable>(tmpdraw));
						detail_classic_item_image.setImageDrawable(tmpdraw);
					}
				} else {
					//File icon = new File(iconUrl);
					Log.e("yumina", "filepaht 3================" + iconUrl);
//					detail_classic_item_image.setImageURI(Uri.fromFile(icon));
					Drawable tmpdraw = Drawable.createFromPath(iconUrl);
					appdata.setthumbdrawable(new SoftReference<Drawable>(tmpdraw));
					detail_classic_item_image.setImageDrawable(tmpdraw);
				}
			} else {
				detail_classic_item_image.setImageResource(appdata.getthumbdrawableresid());
			}

        }
    	if(mcurrpos==position){
                if(mtypeindex == 0 || mtypeindex == 10){
            detail_classic_item_remark.setImageResource(R.drawable.frame_gallery_thumb_selected);
                }else{
    	//	detail_classic_item_remark.setBackgroundColor(0x99000000);
            detail_classic_item_remark.setImageResource(R.drawable.frame_agallery_thumb_selected);
            }

    	}else{
    		detail_classic_item_remark.setBackgroundColor(0x00000000);
    	}
        return detailclassicitemview; 
	}

    private class Holder{
    	public ImageView mimg;
    	public Drawable mdb;
    	public Holder(ImageView img,Drawable db){
    		mimg = img;
    		mdb = db;
    	}
    }
    private Drawable getThumbnailFromApk(String mPkgName){
        Log.e(TAG,"getInstalledLiveWallpaper the pkgname ="+mPkgName);
        Drawable thumbnail=mcontext.getResources().getDrawable(R.drawable.le_wallpaper_magicdownload_push_app_icon_def);
        String previewNameString = "thumbnail";
        Context mFriendContext = null;
        try {
            mFriendContext = mcontext.createPackageContext(mPkgName, Context.CONTEXT_IGNORE_SECURITY);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,"222222getInstalledLiveWallpaper the pkgname ="+mPkgName);
            return thumbnail;
        }

        if(mFriendContext != null ){
            thumbnail = Utilities.findDrawableByResourceName(previewNameString, mFriendContext);
                Log.e(TAG,"555555555555tInstalledLiveWallpaper the pkgname ="+thumbnail);
        }
        return thumbnail;
    }

}
