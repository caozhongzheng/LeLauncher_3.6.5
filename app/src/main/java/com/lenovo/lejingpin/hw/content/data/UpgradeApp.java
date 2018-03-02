package com.lenovo.lejingpin.hw.content.data;

public class UpgradeApp {
	
	private String packageName;
	private String versionCode;
	private int state;
	//0:no update or install
	//1:update
	//2:to install 
	//3:update and no install
	private String mpath;	
	
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
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public void setDownloadApkPath(String path)
	{
		mpath = path;
	}
	public String getDownloadApkPath()
	{
		return mpath;
	}

	@Override
	public boolean equals(Object other) {
		
		String otherPackageName = ((UpgradeApp)other).getPackageName();
		String otherVersionCode = ((UpgradeApp)other).getVersionCode();
		
		if(packageName.equals(otherPackageName) && versionCode.equals(otherVersionCode)){
			return true;
		}else{
			return false;
		}
	}
	
	public String toString() {
		return "pkgName="+packageName+", vCode="+versionCode+", status="+state;
	}
}
