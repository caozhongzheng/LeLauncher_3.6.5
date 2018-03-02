package com.lenovo.lejingpin.hw.content.data;



public class ReCommendsApp{

	private int id;
	private String packageName;
	private String appName;
	private String target;
	private String isPay;
	private String appPrice;
	private String iconAddress;
	private String discount;
	private String pubDate;
	private String version;
	private String versionCode;
	private String startLevel;
	private String appSize;
	private String dataFrom;
	private String lcaid="";
	private String downloadCount = "0";
	
	private String delete;
	private String collect;
	
	private String favorites="0";
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getIsPay() {
		return isPay;
	}
	public void setIsPay(String isPay) {
		this.isPay = isPay;
	}
	public String getAppPrice() {
		return appPrice;
	}
	public void setAppPrice(String appPrice) {
		this.appPrice = appPrice;
	}
	public String getIconAddress() {
		return iconAddress;
	}
	public void setIconAddress(String iconAddress) {
		this.iconAddress = iconAddress;
	}
	public String getDiscount() {
		return discount;
	}
	public void setDiscount(String discount) {
		this.discount = discount;
	}
	public String getPubDate() {
		return pubDate;
	}
	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}
	public String getStartLevel() {
		return startLevel;
	}
	public void setStartLevel(String startLevel) {
		this.startLevel = startLevel;
	}
	public String getAppSize() {
		return appSize;
	}
	public void setAppSize(String appSize) {
		this.appSize = appSize;
	}
	
	public String getDataFrom() {
		return dataFrom;
	}
	public void setDataFrom(String dataFrom) {
		this.dataFrom = dataFrom;
	}
	public String getLcaid() {
		return lcaid;
	}
	public void setLcaid(String lcaid) {
		this.lcaid = lcaid;
	}
	
	
	public String getDelete() {
		return delete;
	}
	public void setDelete(String delete) {
		this.delete = delete;
	}
	public String getCollect() {
		return collect;
	}
	public void setCollect(String collect) {
		this.collect = collect;
	}
	public String getFavorites() {
		return favorites;
	}
	public void setFavorites(String favorites) {
		this.favorites = favorites;
	}
	
	public String getDownloadCount() {
		return downloadCount;
	}
	public void setDownloadCount(String downloadCount) {
		this.downloadCount = downloadCount;
	}
	public String toString(){
		StringBuilder builder = new StringBuilder(100);
		builder.append("AppName");
		builder.append(" : ");
		builder.append(this.getAppName());
		builder.append(",getPackageName");
		builder.append(" : ");
		builder.append(this.getPackageName());
		builder.append(",getVersionCode");
		builder.append(" : ");
		builder.append(this.getVersionCode());
		builder.append(",getAppPrice");
		builder.append(" : ");
		builder.append(this.getAppPrice());
		builder.append(",getAppSize");
		builder.append(" : ");
		builder.append(this.getAppSize());
		builder.append(",getDiscount");
		builder.append(" : ");
		builder.append(this.getDiscount());
		builder.append(",getIconAddress");
		builder.append(" : ");
		builder.append(this.getIconAddress());
		builder.append(",getIsPay");
		builder.append(" : ");
		builder.append(this.getIsPay());
		builder.append(",getPubDate");
		builder.append(" : ");
		builder.append(this.getPubDate());
		builder.append(",getStartLevel");
		builder.append(" : ");
		builder.append(this.getStartLevel());
		builder.append(",getTarget");
		builder.append(" : ");
		builder.append(this.getTarget());
		builder.append(",getLcaid");
		builder.append(" : ");
		builder.append(this.getLcaid());
		builder.append(",downloadcount");
		builder.append(" : ");
		builder.append(this.getDownloadCount());
		return builder.toString();
	}
	@Override
	public boolean equals(Object other){
		if(other!=null){
			String otherPackageName = ((ReCommendsApp)other).getPackageName();
			String otherAppName = ((ReCommendsApp)other).getAppName();
			String pkName = getPackageName();
			String name = getAppName();
			
			if(name.equalsIgnoreCase(otherAppName)){
				return true;
			}else if(pkName.equals(otherPackageName)){
				return true;
			}else {
				return false;
			}
		}
		
		return false;
	}
	
}
