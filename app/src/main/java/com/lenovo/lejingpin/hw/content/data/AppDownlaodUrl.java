package com.lenovo.lejingpin.hw.content.data;

public class AppDownlaodUrl {
	private String downurl;
	private String body;
	private String packageName; 
	private String versionCode;
	private String appName;
	private String mFrom;
	
	//zdx modify
		private String app_iconurl;
		private String category;
		private String mime_type;
	
	
	public AppDownlaodUrl()
	{
	}
	public AppDownlaodUrl(String name ,String code ,String bd,String url,String from)
	{
		downurl = url;
		body = bd;
		packageName = name;
		versionCode = code;
		mFrom = from;
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
	public String getmFrom() {
		return mFrom;
	}
	public void setmFrom(String mFrom) {
		this.mFrom = mFrom;
	}
	
	
	
	
	//zdx modify
		public String getCategory() {
			return category;
		}
		public void setCategory(String category) {
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
