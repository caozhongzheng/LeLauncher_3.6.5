package com.lenovo.lejingpin.hw.content.data;

import java.io.Serializable;




import com.lenovo.lejingpin.ams.NewSearchAppName;
import com.lenovo.lejingpin.hw.ui.RecommendLocalAppInfo;



import android.graphics.drawable.Drawable;

public class DownloadAppInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String appName;
	private String packageName;
	private Drawable icon;
	private String starLevel;
	private boolean isPay;
	private float appPrice;
	private String appAbstract;
	private String versionCode;
	private String appVersion;
	private String appCommentCount;
	
	private String firstSnapPath;
	
	private String target;
	private String iconAddr;
	private String appPublishDate;
	private String discount;
	
	private boolean isDownloading;
	private String appSize;
	private int downloadProgress;
	private String downloadCount;
	
	//yangmao add for search_move 1225 start
	private String appPublishName;
	
	//yangmao add for search_move 1225 end
	public DownloadAppInfo(){}
	
	public DownloadAppInfo(final String app, final String pkg) {
		appName = app;
		packageName = pkg;
	}
	
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public String getStarLevel() {
		return starLevel;
	}
	public void setStarLevel(String starLevel) {
		this.starLevel = starLevel;
	}
	public boolean isPay() {
		return isPay;
	}
	public void setPay(boolean isPay) {
		this.isPay = isPay;
	}
	public float getAppPrice() {
		return appPrice;
	}
	public void setAppPrice(float appPrice) {
		this.appPrice = appPrice;
	}
	public String getAppAbstract() {
		return appAbstract;
	}
	public void setAppAbstract(String appAbstract) {
		this.appAbstract = appAbstract;
	}
	public String getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}
	public String getAppVersion() {
		return appVersion;
	}
	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getIconAddr() {
		return iconAddr;
	}
	public void setIconAddr(String iconAddr) {
		this.iconAddr = iconAddr;
	}
	public String getAppPublishdate() {
		return appPublishDate;
	}
	public void setAppPublishDate(String appPublishdate) {
		this.appPublishDate = appPublishdate;
	}
	public String getDiscount() {
		return discount;
	}
	public void setDiscount(String discount) {
		this.discount = discount;
	}
	public boolean isDownloading() {
		return isDownloading;
	}
	public void setDownloading(boolean isDownloading) {
		this.isDownloading = isDownloading;
	}
	public String getAppSize() {
		return appSize;
	}
	public void setAppSize(String appSize) {
		this.appSize = appSize;
	}
	public int getDownloadProgress() {
		return downloadProgress;
	}
	public void setDownloadProgress(int downloadProgress) {
		this.downloadProgress = downloadProgress;
	}

	public String getFirstSnapPath() {
		return firstSnapPath;
	}

	public void setFirstSnapPath(String firstSnapPath) {
		this.firstSnapPath = firstSnapPath;
	}
	
	public String getAppCommentCount() {
		return appCommentCount;
	}
	public void setAppCommentCount(String appCommentCount) {
		this.appCommentCount = appCommentCount;
	}

	public String getDownloadCount() {
		return downloadCount;
	}

	public void setDownloadCount(String downloadCount) {
		this.downloadCount = downloadCount;
	}
	
	//yangmao add for search_move 1225 start
	
	public String getIcon_addr(){
		return iconAddr;
	}
	
	
	public String getAppPublishName() {
		return appPublishName;
	}
	public void setAppPublishName(String appPublishName) {
		this.appPublishName = appPublishName;
	}
	

	
	public DownloadAppInfo(NewSearchAppName.Application hawaiiApp) {
		appName = hawaiiApp.getAppName();  
		packageName = hawaiiApp.getPackage_name();
		versionCode = hawaiiApp.getApp_versioncode();
		appVersion = hawaiiApp.getApp_version();
	    iconAddr = hawaiiApp.getIcon_addr();
	    appSize = hawaiiApp.getApp_size();
		appPrice = Float.valueOf( hawaiiApp.getApp_price());
		starLevel = hawaiiApp.getStar_level();
		//zhanglz1
//		target = app.getTarget();
//		favorites = app.getFavorites();
//		auther = app.getAuther();
//        collect = app.getCollect();
//        previewAddr = app.getPreviewAddr();

	}
	
	
	public DownloadAppInfo(RecommendLocalAppInfo app) {
		appName = app.getAppName();  
		packageName = app.getPackageName();
		versionCode = app.getVersionCode();
		appVersion = app.getVersion();
	    iconAddr = app.getIconAddress();
	    appSize = app.getAppSize();
		appPrice = Float.valueOf( app.getAppPrice());
		starLevel = app.getStarLevel();
		
	}
	
	//yangmao add for search_move 1225 end
	
    
}
