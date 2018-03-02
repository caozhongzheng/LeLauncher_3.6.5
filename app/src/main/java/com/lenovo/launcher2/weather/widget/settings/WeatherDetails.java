package com.lenovo.launcher2.weather.widget.settings;

import java.io.Serializable;

import android.net.Uri;
import android.provider.BaseColumns;

public class WeatherDetails implements Serializable {
	//test by dining 2013-06-24 launcher2->xlauncher2
	public static final String AUTHORITY = "com.lenovo.launcher2.weather.widget.settings";
	private static final String TAG = "WeatherDetails";
	public String mcityId;
	public String mcityName;
	public String mcityStatus;
	public String mcityStatus1;
	public String mcityStatus2;
	public String mcityDirection;
	public String mcityDirection1;
	public String mcityDirection2;
	public String mcityPower;
	public String mcityTemperature;
	public String mcityTemperature1;
	public String mcityTemperature2;
	public String mcityZwx;
	public String mcityZwxL;
	public String mcityZwxS;
	public String mcityKtk;
	public String mcityKtkL;
	public String mcityKtkS;
	public String mcityPollution;
	public String mcityPollutionL;
	public String mcityPollutionS;
	public String mcityXcz;
	public String mcityXczL;
	public String mcityXczS;
	public String mcityChy;
	public String mcityChyL;
	public String mcityLastupdate;
	public String mcityDate;
	
	public WeatherDetails() {
		super();
	}

	public WeatherDetails(String cityId, String cityName, String cityStatus,
			String cityStatus1,String cityStatus2,String cityDirection,
			String cityDirection1, String cityDirection2,String cityPower,
			String cityTemperature,String cityTemperature1,String cityTemperature2,
			String cityZwx,String cityZwxL,String cityZwxS,
			String cityKtk,String cityKtkL,String cityKtkS,
			String cityPollution,String cityPollutionL,String cityPollutionS,
			String cityXcz,String cityXczL,String cityXczS,
			String cityChy,String cityChyL,String cityLastupdate,String cityDate) {
		this.mcityId = cityId;
		this.mcityName = cityName;
		this.mcityStatus = cityStatus;
		this.mcityStatus1 = cityStatus1;
		this.mcityStatus2 = cityStatus2;
		this.mcityDirection = cityDirection;
		this.mcityDirection1 = cityDirection1;
		this.mcityDirection2 = cityDirection2;
		
		this.mcityPower = cityPower;
		this.mcityTemperature = cityTemperature;
		this.mcityTemperature1 = cityTemperature1;
		this.mcityTemperature2 = cityTemperature2;
		
		this.mcityZwx = cityZwx;
		this.mcityZwxL = cityZwxL;
		this.mcityZwxS = cityZwxS;
		this.mcityKtk = cityKtk;
		this.mcityKtkL = cityKtkL;
		this.mcityKtkS = cityKtkS;
		this.mcityPollution = cityPollution;
		this.mcityPollutionL = cityPollutionL;
		this.mcityPollutionS = cityPollutionS;
		this.mcityXcz = cityXcz;
		this.mcityXczL = cityXczL;
		this.mcityXczS = cityXczS;
		this.mcityChy = cityChy;
		this.mcityChyL = cityChyL;
		this.mcityLastupdate = cityLastupdate;
		this.mcityDate = cityDate;
	}
	public static class WeatherDetailsColumns implements BaseColumns {
		/*
		 * Column definitions
		 */
		public static final String CITYID = "cityid";
		public static final String CITYNAME = "cityname";
		public static final String CITYSTATUS = "citystatus";
		public static final String CITYSTATUS1 = "citystatus1";
		public static final String CITYSTATUS2 = "citystatus2";
		public static final String CITYDIRECTION = "citydirection";
		public static final String CITYDIRECTION1 = "citydirection1";
		public static final String CITYDIRECTION2 = "citydirection2";
		public static final String CITYPOWER = "citypower";
		public static final String CITYTEMPERATURE = "citytemperature";
		public static final String CITYTEMPERATURE1 = "citytemperature1";
		public static final String CITYTEMPERATURE2 = "citytemperature2";
		public static final String CITYZWX = "cityzwx";
		public static final String CITYZWXL = "cityzwxl";
		public static final String CITYZWXS = "cityzwxs";
		public static final String CITYKTK = "cityktk";
		public static final String CITYKTKL = "cityktkl";
		public static final String CITYKTKS = "cityktks";
		public static final String CITYPOLLUTION = "citypollution";
		public static final String CITYPOLLUTIONL = "citypollutionl";
		public static final String CITYPOLLUTIONS = "citypollutions";
		public static final String CITYXCZ = "cityxcz";
		public static final String CITYXCZL = "cityxczl";
		public static final String CITYXCZS = "cityxczs";
		public static final String CITYCHY = "citychy";
		public static final String CITYCHYL = "citychyl";
		public static final String CITYLASTUPDATE = "citylastupdate";
		public static final String CITYDATE = "citydate";
		/*
		 * URI definitions
		 */
		public static final String TABLE_NAME = "weather_widget_details";
		private static final String SCHEME = "content://";
		private static final String PATH_WEATHER = "/weather_widget_details";
		public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_WEATHER);
		/*
		 * MIME type definitions
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/weather_widget_details";
	}
}
