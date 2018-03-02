package com.lenovo.lejingpin.network;

public interface AmsRequest {
	public static final String SID = "rapp001";
	public static final String RID = "appstore.lps.lenovo.com";

	public String getUrl();

	public String getPost();

	public int getHttpMode();

	public String getPriority();
	/***RK_ID:RK_BUGFIX_171707 AUT:zhanglz1@lenovo.com. DATE:2012-10-30.S***/       
	public boolean getIsForDownloadNum();
	/***RK_ID:RK_BUGFIX_171707 AUT:zhanglz1@lenovo.com. DATE:2012-10-30.E***/       

}
