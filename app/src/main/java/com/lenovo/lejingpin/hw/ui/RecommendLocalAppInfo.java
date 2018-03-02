package com.lenovo.lejingpin.hw.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;


import com.lenovo.lejingpin.hw.content.data.DownloadAppInfo;
import com.lenovo.lejingpin.hw.content.data.ReCommendsApp;

public class RecommendLocalAppInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	Drawable icon;
	Drawable iconGradientDrawable;
	Drawable greyIcon;
	String iconUrl;

	String lcaId;
	String packageName;
	String versionCode;
	String name;
	String apkLocalPath;
	String collect;
	String downloadCount;
	String star;
	String downloadTextValue;
	boolean downloadDelete;
	String postion;

	Status status = null;

	public int downLoadProgress = 0;
	int row = -1;

	boolean isUpdate;
	public boolean isFavorites = false;
	
	
	//yangmao add search_move 1225	
	private int category;
	private String appSize;
	private String appPrice;
	private String starLevel;
	private String appVerison;

	
	public RecommendLocalAppInfo() {
		
	}
	
	public RecommendLocalAppInfo(ReCommendsApp app) {
		this.lcaId = app.getLcaid();
		this.iconUrl = app.getIconAddress();
		this.packageName = app.getPackageName();
		this.versionCode = app.getVersionCode();
		this.name = app.getAppName();
		this.isFavorites = !app.getFavorites().equals("0");
		this.collect = app.getCollect();
		this.downloadCount = app.getDownloadCount();
		this.star = app.getStartLevel();
		this.appVerison = app.getVersion();
		this.appSize = app.getAppSize();
	}

//yangmao add for search_move start 1225
	public RecommendLocalAppInfo(DownloadAppInfo app) {

		this.iconUrl = app.getIcon_addr();

		this.packageName = app.getPackageName();
		this.versionCode = app.getVersionCode();
		this.name = app.getAppName();
		this.appSize = app.getAppSize();
		this.appPrice = String.valueOf(app.getAppPrice());
		this.starLevel = app.getStarLevel();
		this.appVerison = app.getAppVersion();

		this.status = Status.UNDOWNLOAD;
		this.isFavorites = false;

		// zhanglz1
//		this.collect = app.getCollect();
		
	}

//yangmao add for search_move end 1225



	public static enum Status {
		UNDOWNLOAD("undownload"), //
		DOWNLOADING("downloading"), //
		UNINSTALL("uninstall"), //
		UNUPDATE("unupdate"), //
		PAUSE("pause"),
		
		//yangmao add 1214 move
		
		INSTALL("install"), 
		DOWNLOAD_CLICK("download_click");
		
		
		private Status(String value) {
			this.value = value;
		}

		private String value;

		public String value() {
			return value;
		}

		public static Status parseStatus(String value) {
			for (Status status : Status.values()) {
				if(status.value.equals(value)) {
					return status;
				}
			}
			return null;
		}
	}

	public static String write2whereClause(Status[] status) {
		String where = "(";
		for (int i = 0; i < status.length; i++) {
			where += "\"" + status[i].value + "\"";
			if(i != status.length - 1) //
				where += ", ";
		}
		where += ")";
		return where;
	}

	static byte[] flattenBitmap(Bitmap bitmap) {
		// Try go guesstimate how much space the icon will take when serialized
		// to avoid unnecessary allocations/copies during the write.
		int size = bitmap.getWidth() * bitmap.getHeight() * 4;
		ByteArrayOutputStream out = new ByteArrayOutputStream(size);
		try {
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
			return out.toByteArray();
		} catch (IOException e) {
			Log.w("Favorite", "Could not write icon");
			return null;
		}
	}

	@Override
	public String toString() {
		if(status!=null){
			
			return "RecommendApp(title=" + name + ", progress=" + downLoadProgress + ",postion=" + postion + ",lcaid=" + lcaId+", status ="+status.name()+")";
		}else{
			return "RecommendApp(title=" + name + ", progress=" + downLoadProgress + ",postion=" + postion + ",lcaid=" + lcaId+")";
		}
	}
	
	
	
	
	
	
	
	//yangmao add start 1214 move
	
	
	
	public void setAppName(String app_name) {

		this.name = app_name;

	}

	public void setPackageName(String package_name) {

		this.packageName = package_name;
	}

	public void setVersionCode(String version_code) {

		this.versionCode = version_code;
	}

	public void setIconAddress(String icon_address) {
		this.iconUrl = icon_address;
	}
	
	
	public void setAppVersion (String appVerison){
		this.appVerison = appVerison;
	}
	
	public String getAppName() {
		return name;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getVersionCode() {
		return versionCode;
	}
	
	
	public String getIconAddress() {
		return iconUrl;
	}
	
	
	public String getAppSize() {
		return appSize;
	}

	public String getAppPrice() {
		return appPrice;
	}

	public String getStarLevel() {
		return starLevel;
	}
	

	public String getVersion() {
		return appVerison;
	}

	

	
	

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable ic) {
		icon = ic;
	}

	public String getLcaId() {
		return lcaId;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status s) {
		status = s;
	}

	public void setLocalPath(String path) {
		apkLocalPath = path;
	}

	// zdx modify
	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	// zhanglz1
	public String getCollect() {
		return collect;
	}

	public void setCollect(String collect) {
		this.collect = collect;
	}
	
	

	public Drawable getGreyIcon() {
		return greyIcon;
	}

	public void setGreyIcon(Drawable greyIcon) {
		this.greyIcon = greyIcon;
	}

	@Override
	public boolean equals(Object o) {
		String pkName = ((RecommendLocalAppInfo)o).packageName;
		String vcode = ((RecommendLocalAppInfo)o).versionCode;
		if(pkName!=null && pkName.equals(this.packageName) && vcode!=null && vcode.equals(this.versionCode)){
			return true;
		}else{
			return false;
		}
	}

	
	
	
}
