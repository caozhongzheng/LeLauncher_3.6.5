package com.lenovo.lejingpin.share.download;

//zdx modify
public class AppDownloadUrl {
	private String downurl;
	private String body;
	private String package_name;
	private String version_code;
	private String app_name;

	// zdx modify
	private String app_iconurl;
	private int category;
	private String mime_type;
	private AppDownloadUrl.Callback mCallback;
	
	//xujing3 add
	private String version_name;

	public AppDownloadUrl() {
	}

	public AppDownloadUrl(String name, String code, String bd, String url,
			String from) {
		downurl = url;
		body = bd;
		package_name = name;
		version_code = code;
	}
	
	public interface Callback {
		void doCallback(DownloadInfo info);
	}
	
	public void setCallback(AppDownloadUrl.Callback ck){
		mCallback = ck;
	}
	
	public AppDownloadUrl.Callback getCallback(){
		return mCallback;
	}
	
	public String getVersionName() {
		return this.version_name;
	}

	public void setVersionName(String versionName) {
		this.version_name = versionName;
	}

	public String getDownurl() {
		return downurl;
	}

	public void setDownurl(String downurl) {
		this.downurl = downurl;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getPackage_name() {
		return package_name;
	}

	public void setPackage_name(String package_name) {
		this.package_name = package_name;
	}

	public String getVersion_code() {
		return version_code;
	}

	public void setVersion_code(String version_code) {
		this.version_code = version_code;
	}

	public String getApp_name() {
		return app_name;
	}

	public void setApp_name(String app_name) {
		this.app_name = app_name;
	}

	// zdx modify
	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public String getIconUrl() {
		return app_iconurl;
	}

	public void setIconUrl(String iconurl) {
		this.app_iconurl = iconurl;
	}

	public String getMimeType() {
		return mime_type;
	}

	public void setMimeType(String mimeType) {
		this.mime_type = mimeType;
	}

}
