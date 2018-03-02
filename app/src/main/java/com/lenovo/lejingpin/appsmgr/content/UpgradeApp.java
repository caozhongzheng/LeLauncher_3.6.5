package com.lenovo.lejingpin.appsmgr.content;

import java.io.Serializable;

import com.lenovo.lejingpin.share.download.AppDownloadUrl.Callback;
import com.lenovo.lejingpin.share.download.Downloads;

public class UpgradeApp implements Serializable {
	private static final long serialVersionUID = 1L;
	private String packageName;
	private String versionCode;
	private String appName;
	private String iconAddr;
	private int category;
	private String appSize;
	private String appStar;
	private String appPay;
	private String versionName;
	private String appIspay;
	private String appDesc;
	private String appSnapShot;
	private long currentBytes = 0;
	private long totalBytes = 0;
	private int control = Downloads.Impl.CONTROL_PAUSED;
	private boolean isReDownload = false;
	private boolean appUpdateIgnore = false;
	private Callback callBack;

	private Status appStatus;

	private String oldAppName;
	private String oldAppVersionName;

	public UpgradeApp() {
		appStatus = Status.UNDOWNLOAD;
	}

	public static enum Status {
		UNDOWNLOAD("undownload"), DOWNLOADING("downloading"), UNINSTALL(
				"uninstall"), UNUPDATE("unupdate"), PAUSE("pause"), INSTALL(
				"install"), DOWNLOAD_CLICK("download_click");

		private Status(String value) {
			this.value = value;
		}

		private String value;

		public String value() {
			return value;
		}

		public static Status parseStatus(String value) {
			for (Status status : Status.values()) {
				if (status.value.equals(value)) {
					return status;
				}
			}
			return null;
		}
	}
	
	public void setCallback(Callback ck){
		callBack = ck;
	}
	
	public Callback getCallback(){
		return callBack;
	}
	
	public void setUpdateIgnore(boolean ignore){
		this.appUpdateIgnore = ignore;
	}
	public boolean getUpdateIgnore(){
		return this.appUpdateIgnore;
	}
	
	public void setReDownloadFlag(boolean flag){
		this.isReDownload = flag;
	}
	
	public boolean getReDownloadFlag(){
		return this.isReDownload;
	}
	
	public void setControl(int control){
		this.control = control;
	}
	
	public int getControl(){
		return this.control;
	}
	
	public long getCurrentBytes(){
		return this.currentBytes;
	}
	
	public void setCurrentBytes(long bytes){
		this.currentBytes = bytes;
	}
	
	public long getTotalBytes(){
		return this.totalBytes;
	}
	
	public void setTotalBytes(long bytes){
		this.totalBytes = bytes;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getIconAddr() {
		return iconAddr;
	}

	public void setIconAddr(String iconAddr) {
		this.iconAddr = iconAddr;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public String getAppSize() {
		return appSize;
	}

	public void setAppSize(String appSize) {
		this.appSize = appSize;
	}

	public String getAppStar() {
		return appStar;
	}

	public void setAppStar(String appStar) {
		this.appStar = appStar;
	}

	public String getAppPay() {
		return appPay;
	}

	public void setAppPay(String appPay) {
		this.appPay = appPay;
	}

	public String getVersionName() {
		if (versionName != null && !versionName.isEmpty()
				&& !versionName.equals("null")) {
			return versionName;
		} else {
			if (versionCode != null && !versionCode.isEmpty()) {
				versionName = versionCode;
				return versionName;
			} else {
				versionName = "";
				return "";
			}
		}
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getAppIspay() {
		return appIspay;
	}

	public void setAppIspay(String appIspay) {
		this.appIspay = appIspay;
	}

	public String getAppDesc() {
		return appDesc;
	}

	public void setAppDesc(String appDesc) {
		this.appDesc = appDesc;
	}

	public String getAppSnapShot() {
		return appSnapShot;
	}

	public void setAppSnapShot(String appSnapShot) {
		this.appSnapShot = appSnapShot;
	}

	public Status getStatus() {
		return appStatus;
	}

	public void setStatus(Status appStatus) {
		this.appStatus = appStatus;
	}

	public String getOldName() {
		return oldAppName;
	}

	public void setOldName(String oldAppName) {
		this.oldAppName = oldAppName;
	}

	public String getOldVersion() {
		return oldAppVersionName;
	}

	public void setOldVersion(String oldAppVersionName) {
		this.oldAppVersionName = oldAppVersionName;
	}

	@Override
	public boolean equals(Object other) {
		String otherPackageName = ((UpgradeApp) other).getPackageName();
		String otherVersionCode = ((UpgradeApp) other).getVersionCode();
//		String packageName = this.getPackageName();
//		String versionCode = this.getVersionCode();
		if (packageName.equals(otherPackageName)
				&& versionCode.equals(otherVersionCode)) {
			return true;
		} else {
			return false;
		}
	}
}
