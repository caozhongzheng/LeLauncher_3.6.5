package com.lenovo.launcher2.weather.widget.settings;


import android.net.Uri;
import android.provider.BaseColumns;

public class WeatherApp {
	private static final String TAG = "WeatherDetails";
	public String mcityData;
	public String mcityId;
	public String mcityName;
	public String mcityRegionName;
	public String mcityStatus1;
	public String mcityStatus2;
	public int mcityStatusCode1;
	public int mcityStatusCode2;
	public int mcityTemperature1;
	public int mcityTemperature2;
	public String mcityPower1;
	public String mcityPower2;//风力
	public WeatherApp(String cityData,String cityId, String cityName, String cityRegionName,
			String cityStatus1,String cityStatus2,
			int cityStatusCode1,int cityStatusCode2,
			int cityTemperature1,int cityTemperature2,
			String cityPower1,String cityPower2)
	{
		this.mcityData = cityData;
		this.mcityName = cityName;
		this.mcityRegionName = cityRegionName;
		this.mcityStatus1 = cityStatus1;
		this.mcityStatus2 = cityStatus2;
		this.mcityStatusCode1 = cityStatusCode1;
		this.mcityStatusCode2 = cityStatusCode2;
		this.mcityTemperature1 = cityTemperature1;
		this.mcityTemperature2 = cityTemperature2;
		this.mcityPower1 = cityPower1;
		this.mcityPower2 = cityPower2;
		this.mcityId = cityId;
	}
	public static class WeatherAppColumns implements BaseColumns {
		/*
		 * Column definitions
		 */
		public static final String CITY_NAME = "city_name";
		public static final String REGION_NAME = "region_name";
		public static final String DATE = "date";
		public static final String DAY_CODE = "day_code";
		public static final String DAY_TEMP = "day_temp";
		public static final String DAY_TEXT = "day_text";
		public static final String DAY_WIND = "day_wind";
		public static final String NIGHT_CODE = "night_code";
		public static final String NIGHT_TEMP = "night_temp";
		public static final String NIGHT_TEXT = "night_text";
		public static final String NIGHT_WIND = "night_wind";
		public static final String TQT_CODE = "tqt_code";
		public static final Uri CONTENT_URI = Uri.parse("content://sina.mobile.tianqitong.Citys/forecasts?city_type=widget_city");	

	}
}
