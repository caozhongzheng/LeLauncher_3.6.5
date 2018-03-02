package com.lenovo.lejingpin.share.download;

import android.os.Parcel;
import android.os.Parcelable;

public class DownloadInfo implements Parcelable {
	private String id;
	private String packageName;
	private String versionCode;
	private String versionName;
	private String appName;
	private String appSize;
	private String iconAddr;
	private String downloadUrl;
	private String installPath;
	private long currentBytes;
	private long totalBytes;
	private String mimeType;
	/**
	 * downloadStatus run status and error status
	 */
	private int downloadStatus;
	/**
	 * wifi wait status
	 */
	private int wifistatus;
	private int progress;
	
	//xujing3 added
	private int control;
	
	public void setControl(int control){
		this.control = control;
	}
	
	public int getControl(){
		return this.control;
	}

	// zdx modify
	private int category = DownloadConstant.CATEGORY_ERROR;

	public int getCategory() {
		return category;
	}

	public void setCategory(int ca) {
		category = ca;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mtype) {
		mimeType = mtype;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public int getWifistatus() {
		return wifistatus;
	}

	public void setWifistatus(int wifistatus) {
		this.wifistatus = wifistatus;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public int getDownloadStatus() {
		return downloadStatus;
	}

	public void setDownloadStatus(int downloadStatus) {
		this.downloadStatus = downloadStatus;
	}

	public String getInstallPath() {
		return installPath;
	}

	public void setInstallPath(String installPath) {
		this.installPath = installPath;
	}

	public long getTotalBytes() {
		return totalBytes;
	}

	public void setTotalBytes(long totalBytes) {
		this.totalBytes = totalBytes;
	}

	public long getCurrentBytes() {
		return currentBytes;
	}

	public void setCurrentBytes(long currentBytes) {
		this.currentBytes = currentBytes;
	}

	public DownloadInfo() {
	}

	public DownloadInfo(Parcel source) {
		this.id = source.readString();
		this.packageName = source.readString();
		this.versionName = source.readString();
		this.appSize = source.readString();
		this.installPath = source.readString();
		this.iconAddr = source.readString();
		this.appName = source.readString();
		this.versionCode = source.readString();
		this.downloadUrl = source.readString();
		this.currentBytes = source.readLong();
		this.totalBytes = source.readLong();
		this.downloadStatus = source.readInt();
		this.flag = source.readInt();
		this.progress = source.readInt();
		// zdx modify
		this.category = source.readInt();
		//xujing added
		this.control = source.readInt();
	}

	public DownloadInfo(String pkgName, String versionCode) {
		this.packageName = pkgName;
		this.versionCode = versionCode;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.id);
		dest.writeString(this.packageName);
		dest.writeString(this.versionName);
		dest.writeString(this.appSize);
		dest.writeString(this.installPath);
		dest.writeString(this.iconAddr);
		dest.writeString(this.appName);
		dest.writeString(this.versionCode);
		dest.writeString(this.downloadUrl);
		dest.writeLong(this.currentBytes);
		dest.writeLong(this.totalBytes);
		dest.writeInt(this.downloadStatus);
		dest.writeInt(this.flag);
		dest.writeInt(this.progress);
		// zdx modify
		dest.writeInt(this.category);
		dest.writeInt(this.control);

	}

	public String getPackageName() {
		return packageName;
	}

	public DownloadInfo setPackageName(String packageName) {
		this.packageName = packageName;
		return this;
	}

	public String getAppSize() {
		return appSize;
	}

	public void setAppSize(String appSize) {
		this.appSize = appSize;
	}

	public String getIconAddr() {
		return iconAddr;
	}

	public void setIconAddr(String iconAddr) {
		this.iconAddr = iconAddr;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getVersionCode() {
		return versionCode;
	}

	public DownloadInfo setVersionCode(String versionCode) {
		this.versionCode = versionCode;
		return this;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public static Parcelable.Creator<DownloadInfo> getCreator() {
		return CREATOR;
	}

	public static final Parcelable.Creator<DownloadInfo> CREATOR = new Parcelable.Creator<DownloadInfo>() {
		public DownloadInfo createFromParcel(Parcel source) {
			return new DownloadInfo(source);
		}

		public DownloadInfo[] newArray(int size) {
			return new DownloadInfo[size];
		}
	};

	/**
	 * flag value:FLAG_START and FLAG_CONFIG
	 */
	private int flag;
	/**
	 * Immediately download
	 */
	public static final int FLAG_START = 0;
	/**
	 * Add download queue use wifi download.
	 */
	public static final int FLAG_WIFI = 1;

	/**
	 * Download Action status
	 */
	public static final int DELETE = 0;
	public static final int PAUSE = 1;
	public static final int CONTINUE = 2;
	public static final int CLEARALL = 3;
	public static final int START = 4;
	public static final int REDOWNLOAD = 5;

	@Override
	public String toString() {
		return "DownloadInfo [appName=" + appName + ", appSize=" + appSize
				+ ", currentBytes=" + currentBytes + ", downloadStatus="
				+ downloadStatus + ", downloadUrl=" + downloadUrl + ", flag="
				+ flag + ", iconAddr=" + iconAddr + ", id=" + id
				+ ", installPath=" + installPath + ", packageName="
				+ packageName + ", progress=" + progress + ", totalBytes="
				+ totalBytes + ", versionCode=" + versionCode
				+ ", versionName=" + versionName + ", wifistatus=" + wifistatus
				+ "]";
	}

	/**
	 * Add download queue run status
	 */
	public static final int DOWNLOAD_READY = 190;
	public static final int DOWNLOAD_RUN = 192;
	public static final int DOWNLOAD_PAUSE = 193;
	public static final int DOWNLOAD_COMPLETE = 200;

	/**
	 * This is the errorCode for the HTTP error
	 */
	public static final int DOWNLOAD_ERROR_HTTP = -1;
	/**
	 * This download couldn't be completed because no external storage device
	 * was found or the SD card is full
	 */
	public static final int DOWNLOAD_ERROR_SDCARD = -2;

	/**
	 * 
	 */
	public static final int DOWNLOAD_WAIT_WIFI = -3;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((packageName == null) ? 0 : packageName.hashCode());
		result = prime * result
				+ ((versionCode == null) ? 0 : versionCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DownloadInfo other = (DownloadInfo) obj;
		if (packageName == null) {
			if (other.packageName != null)
				return false;
		} else if (!packageName.equals(other.packageName))
			return false;
		if (versionCode == null) {
			if (other.versionCode != null)
				return false;
		} else if (!versionCode.equals(other.versionCode))
			return false;
		return true;
	}

}
