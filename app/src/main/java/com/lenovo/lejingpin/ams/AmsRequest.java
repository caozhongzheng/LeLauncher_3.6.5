package com.lenovo.lejingpin.ams;

public interface AmsRequest {
	public static final String SID = "rapp001";
	public static final String RID = "appstore.lps.lenovo.com";

	public String getUrl();

	public String getPost();

	public int getHttpMode();

	public String getPriority();

}
