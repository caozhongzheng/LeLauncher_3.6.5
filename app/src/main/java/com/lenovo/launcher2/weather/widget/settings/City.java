package com.lenovo.launcher2.weather.widget.settings;

import android.util.Log;

public class City {
	private String mzhcityname;
	private String mtwcityname;
	private String mencityname;
	private String mcityname;
	private String mcityid;
	private String mcityprovince;
	public City(String zhcityname ,String cityname,
			String twcityname ,String encityname,
			String cityid,String cityprovince){
		mtwcityname = twcityname;
		mencityname = encityname;
		mzhcityname = zhcityname;
		mcityname = cityname;
		mcityid = cityid;
		mcityprovince = cityprovince;
	}
	public String getzhcity(){
		return mzhcityname;
	}
	public String gettwcity(){
		return mtwcityname;
	}
	public String getencity(){
		return mencityname;
	}
	public String getcityid(){
		return mcityid;
	}
	public String getcityname(){
		return mcityname;
	}
	public String getcityprovince(){
		return mcityprovince;
	}
	public void tostring()
	{
		Log.d("c","mtwcityname="+mtwcityname+
				"mencityname="+mencityname+
				"mzhcityname="+mzhcityname+
				"mcityname="+mcityname);
	}
}
