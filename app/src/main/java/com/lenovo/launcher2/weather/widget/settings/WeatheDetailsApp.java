package com.lenovo.launcher2.weather.widget.settings;

import java.io.Serializable;

import android.net.Uri;
import android.provider.BaseColumns;

public class WeatheDetailsApp {
	private static final String TAG = "WeatherDetails";
	public String mcityId;
	public String mcityName;
	public String mcityRegionName;
	public String mcityDate;
	public String mcityGMTDate;
	public int mcityTemperature;
	public String mcityWind;
	public int mcityHumidity;
	public String mcitySunrise;
	public String mcitySunset;
	public String mcityCloth;
	public String mcityCold;
	public String mcityComfort;
	public String mcityUV;
	public String mcityCwash;
	public String mcitySport;
	public String mcityInsolate;
	public String mcityUmbrella;

	public WeatheDetailsApp(String cityId, String cityName, String cityRegionName,
			String cityDate,String cityGMTDate,int cityTemperature,
			String cityWind,int cityHumidity,
			String citySunrise,String citySunset,
			String cityCloth,String cityCold,
			String cityComfort,String cityUV,
			String cityCwash,String citySport,
			String cityInsolate,String cityUmbrella)
	{
		this.mcityId = cityId;
		this.mcityName = cityName;
		this.mcityRegionName = cityRegionName;
		this.mcityDate = cityDate;
		this.mcityGMTDate = cityGMTDate;
		this.mcityTemperature = cityTemperature;
		this.mcityWind = cityWind;
		this.mcityHumidity = cityHumidity;
		this.mcitySunrise = citySunrise;
		this.mcitySunset = citySunset;
		this.mcityCloth = cityCloth;
		this.mcityCold = cityCold;
		
		this.mcityComfort = cityComfort;
		this.mcityUV = cityUV;
		this.mcityCwash = cityCwash;
		this.mcitySport = citySport;
		this.mcityInsolate = cityInsolate;
		this.mcityUmbrella = cityUmbrella;
	}
	public static class WeatheDetailsAppColumns implements BaseColumns {
		/*
		 * Column definitions
		 */
		public static final String CITY_NAME = "city_name";
		public static final String REGION_NAME = "region_name";
		public static final String LOC_PUBDATE = "loc_pubdate";
		public static final String GMT_PUBDATE = "gmt_pubdate";
		public static final String TEMPERATURE = "temperature";
		public static final String WIND = "wind";
		public static final String HUMIDITY = "humidity";
		public static final String SUNRISE = "sunrise";
		public static final String SUNSET = "sunset";
		public static final String CLOTH = "cloth";
		public static final String COLD = "cold";
		public static final String COMFORT = "comfort";
		public static final String UV = "uv";
		public static final String CWASH = "cwash";
		public static final String SPORT = "sport";
		public static final String INSOLATE = "insolate";
		public static final String UMBRELLA = "unbrella";
		public static final String TQT_CODE = "tqt_code";		
		
		public static final Uri CONTENT_URI = Uri.parse("content://sina.mobile.tianqitong.Citys/city_weather_infos?city_type=widget_city");	
														
	}
}
