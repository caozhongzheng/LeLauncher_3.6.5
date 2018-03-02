package com.lenovo.launcher2.weather.widget.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.XLauncherModel;
import com.lenovo.launcher2.commoninterface.LenovoWidgetViewInfo;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.Debug.R5;
import com.lenovo.launcher2.weather.widget.settings.City;
import com.lenovo.launcher2.weather.widget.settings.CityColumns;
import com.lenovo.launcher2.weather.widget.settings.LenovoExWigetInfo;
import com.lenovo.launcher2.weather.widget.settings.WeatheDetailsApp;
import com.lenovo.launcher2.weather.widget.settings.WeatherApp;
import com.lenovo.launcher2.weather.widget.settings.WeatherDetails;
import com.lenovo.launcher2.weather.widget.settings.WeatherHandler;
import com.lenovo.launcher2.weather.widget.utils.Debug.R2;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;


public class WeatherUtilites {
    public static List<WeatherDetails> weatherdetails=new ArrayList<WeatherDetails>();
    private WeatherUtilites(){
    	
    }
	private class QueryParams {
		private QueryParams(){
			
		}
		public static final String kServerIPParam = "rwthr01";
		public static final String kWeatherURL = "weather/1.0/";
		public static final String kWeatherQuery = "query?";
		public static final String kCityParam = "c=";
		public static final String kForecastDaysParam = "&f=";
		public static final String kLocaleParam = "&l=";
		public static final int kDefaultFDays = 1;
		public static final String KIDCServer = "http://wth.lenovomm.com/";
	}
    public static final int[] SQUARE_NUMBER = new int[] {
        R.drawable.weather_widget_square_num0, 
        R.drawable.weather_widget_square_num1, 
        R.drawable.weather_widget_square_num2, 
        R.drawable.weather_widget_square_num3, 
        R.drawable.weather_widget_square_num4, 
        R.drawable.weather_widget_square_num5, 
        R.drawable.weather_widget_square_num6, 
        R.drawable.weather_widget_square_num7, 
        R.drawable.weather_widget_square_num8, 
        R.drawable.weather_widget_square_num9
    };
    public static final int[] MAGIC_NUMBER = new int[] {
        R.drawable.weather_widget_magic_num0, 
        R.drawable.weather_widget_magic_num1, 
        R.drawable.weather_widget_magic_num2, 
        R.drawable.weather_widget_magic_num3, 
        R.drawable.weather_widget_magic_num4, 
        R.drawable.weather_widget_magic_num5, 
        R.drawable.weather_widget_magic_num6, 
        R.drawable.weather_widget_magic_num7, 
        R.drawable.weather_widget_magic_num8, 
        R.drawable.weather_widget_magic_num9
    };
    public static final int[] NUMBER = new int[] {
        R.drawable.weather_widget_num0, 
        R.drawable.weather_widget_num1, 
        R.drawable.weather_widget_num2, 
        R.drawable.weather_widget_num3, 
        R.drawable.weather_widget_num4, 
        R.drawable.weather_widget_num5, 
        R.drawable.weather_widget_num6, 
        R.drawable.weather_widget_num7, 
        R.drawable.weather_widget_num8, 
        R.drawable.weather_widget_num9
    };
    public static final int[] MAGIC_BAIDU_NUMBER = new int[] {
        R.drawable.weather_widget_magic_baidu_num0, 
        R.drawable.weather_widget_magic_baidu_num1, 
        R.drawable.weather_widget_magic_baidu_num2, 
        R.drawable.weather_widget_magic_baidu_num3, 
        R.drawable.weather_widget_magic_baidu_num4, 
        R.drawable.weather_widget_magic_baidu_num5, 
        R.drawable.weather_widget_magic_baidu_num6, 
        R.drawable.weather_widget_magic_baidu_num7, 
        R.drawable.weather_widget_magic_baidu_num8, 
        R.drawable.weather_widget_magic_baidu_num9
    };
    public static final HashMap<String, String> ICON_SQUARE_SINA_MAP = new HashMap<String, String>();
	static {
		ICON_SQUARE_SINA_MAP.put("晴","d0");
		ICON_SQUARE_SINA_MAP.put("多云","d1");
		ICON_SQUARE_SINA_MAP.put("雾","d2");
		ICON_SQUARE_SINA_MAP.put("烟","d2");
		ICON_SQUARE_SINA_MAP.put("霾","d2");
		ICON_SQUARE_SINA_MAP.put("暴雾","d2");
		ICON_SQUARE_SINA_MAP.put("浓雾","d2");
		ICON_SQUARE_SINA_MAP.put("阴","d3");
		ICON_SQUARE_SINA_MAP.put("阵雨","d4");
		ICON_SQUARE_SINA_MAP.put("雷阵雨","d4");
		ICON_SQUARE_SINA_MAP.put("雷阵雨伴有冰雹","d5");
		ICON_SQUARE_SINA_MAP.put("雷阵雨并伴有冰雹","d5");
		ICON_SQUARE_SINA_MAP.put("雨夹雪","d6");
		ICON_SQUARE_SINA_MAP.put("冻雨","d7");
		ICON_SQUARE_SINA_MAP.put("小雨","d7");
		ICON_SQUARE_SINA_MAP.put("小到中雨","d7");
		ICON_SQUARE_SINA_MAP.put("中到大雨","d7");
		ICON_SQUARE_SINA_MAP.put("中雨","d7");
		ICON_SQUARE_SINA_MAP.put("大雨","d8");
		ICON_SQUARE_SINA_MAP.put("大到暴雨","d8");
		ICON_SQUARE_SINA_MAP.put("暴雨","d8");
		ICON_SQUARE_SINA_MAP.put("大暴雨","d8");
		ICON_SQUARE_SINA_MAP.put("特大暴雨","d8");
		ICON_SQUARE_SINA_MAP.put("冰雹","d9");
		ICON_SQUARE_SINA_MAP.put("阵雪","d10");
		ICON_SQUARE_SINA_MAP.put("小雪","d10");
		ICON_SQUARE_SINA_MAP.put("小到中雪","d10");
		ICON_SQUARE_SINA_MAP.put("中雪","d10");
		ICON_SQUARE_SINA_MAP.put("中到大雪","d10");
		ICON_SQUARE_SINA_MAP.put("大雪","d11");
		ICON_SQUARE_SINA_MAP.put("大到暴雪","d11");
		ICON_SQUARE_SINA_MAP.put("暴雪","d11");
		ICON_SQUARE_SINA_MAP.put("沙尘暴","d12");
		ICON_SQUARE_SINA_MAP.put("强沙尘暴","d12");
		ICON_SQUARE_SINA_MAP.put("浮沙","d13");
		ICON_SQUARE_SINA_MAP.put("扬沙","d13");

	}
	public static final HashMap<String, String> ICON_SINA_MAP = new HashMap<String, String>();
	static {
		ICON_SINA_MAP.put("晴","d0");
		ICON_SINA_MAP.put("多云","d1");
		ICON_SINA_MAP.put("雾","d18");
		ICON_SINA_MAP.put("烟","d18");
		ICON_SINA_MAP.put("霾","d18");
		ICON_SINA_MAP.put("暴雾","d18");
		ICON_SINA_MAP.put("浓雾","d18");
		ICON_SINA_MAP.put("阴","d2");
		ICON_SINA_MAP.put("阵雨","d3");
		ICON_SINA_MAP.put("雷阵雨","d4");
		ICON_SINA_MAP.put("雷阵雨伴有冰雹","d4");
		ICON_SINA_MAP.put("雷阵雨并伴有冰雹","d4");
		ICON_SINA_MAP.put("雨夹雪","d6");
		ICON_SINA_MAP.put("冻雨","d7");
		ICON_SINA_MAP.put("小雨","d7");
		ICON_SINA_MAP.put("冰雹","d7");
		ICON_SINA_MAP.put("小到中雨","d7");
		ICON_SINA_MAP.put("中到大雨","d7");
		ICON_SINA_MAP.put("中雨","d7");
		ICON_SINA_MAP.put("大雨","d7");
		ICON_SINA_MAP.put("大到暴雨","d7");
		ICON_SINA_MAP.put("暴雨","d7");
		ICON_SINA_MAP.put("大暴雨","d7");
		ICON_SINA_MAP.put("特大暴雨","d7");
		ICON_SINA_MAP.put("阵雪","d13");
		ICON_SINA_MAP.put("小雪","d14");
		ICON_SINA_MAP.put("小到中雪","d14");
		ICON_SINA_MAP.put("中雪","d14");
		ICON_SINA_MAP.put("中到大雪","d14");
		ICON_SINA_MAP.put("大雪","d14");
		ICON_SINA_MAP.put("大到暴雪","d14");
		ICON_SINA_MAP.put("暴雪","d17");
		ICON_SINA_MAP.put("沙尘暴","d20");
		ICON_SINA_MAP.put("强沙尘暴","d20");
		ICON_SINA_MAP.put("浮沙","d20");
		ICON_SINA_MAP.put("扬沙","d20");

	}
	public static final HashMap<String, String> ICON_SINA_EN_STRING_MAP = new HashMap<String, String>();
	static {
		ICON_SINA_EN_STRING_MAP.put("晴","Sun");
		ICON_SINA_EN_STRING_MAP.put("多云","Cloudy");
		ICON_SINA_EN_STRING_MAP.put("雾","Fog");
		ICON_SINA_EN_STRING_MAP.put("烟","烟");
		ICON_SINA_EN_STRING_MAP.put("霾","Haze");
		ICON_SINA_EN_STRING_MAP.put("暴雾","Exposure fog");
		ICON_SINA_EN_STRING_MAP.put("浓雾","Dense fog");
		ICON_SINA_EN_STRING_MAP.put("阴","Overcast");
		ICON_SINA_EN_STRING_MAP.put("阵雨","Showers");
		ICON_SINA_EN_STRING_MAP.put("雷阵雨","Scattered T-storms");
		ICON_SINA_EN_STRING_MAP.put("雷阵雨伴有冰雹","Scattered T-storms");
		ICON_SINA_EN_STRING_MAP.put("雷阵雨并伴有冰雹","Scattered T-storms");
		ICON_SINA_EN_STRING_MAP.put("雨夹雪","Sleet");
		ICON_SINA_EN_STRING_MAP.put("冻雨","Freezing rain");
		ICON_SINA_EN_STRING_MAP.put("小雨","Light rain");
		ICON_SINA_EN_STRING_MAP.put("冰雹","Hail");
		ICON_SINA_EN_STRING_MAP.put("小到中雨","Small to moderate rain");
		ICON_SINA_EN_STRING_MAP.put("中到大雨","Moderate to heavy rain");
		ICON_SINA_EN_STRING_MAP.put("中雨","Moderate rain");
		ICON_SINA_EN_STRING_MAP.put("大雨","Heavy rain");
		ICON_SINA_EN_STRING_MAP.put("大到暴雨","Heavy rain to rainstorm");
		ICON_SINA_EN_STRING_MAP.put("暴雨","Rainstorm");
		ICON_SINA_EN_STRING_MAP.put("大暴雨","Rainstorm");
		ICON_SINA_EN_STRING_MAP.put("特大暴雨","Heavy rain to rainstorm");
		ICON_SINA_EN_STRING_MAP.put("阵雪","Snow shower");
		ICON_SINA_EN_STRING_MAP.put("小雪","Slight snow");
		ICON_SINA_EN_STRING_MAP.put("小到中雪","Small to moderate snow");
		ICON_SINA_EN_STRING_MAP.put("中雪","In the snow");
		ICON_SINA_EN_STRING_MAP.put("中到大雪","Moderate to heavy snow");
		ICON_SINA_EN_STRING_MAP.put("大雪","Heavy snow");
		ICON_SINA_EN_STRING_MAP.put("大到暴雪","To Blizzard");
		ICON_SINA_EN_STRING_MAP.put("暴雪","Blizzard");
		ICON_SINA_EN_STRING_MAP.put("沙尘暴","Sand storm");
		ICON_SINA_EN_STRING_MAP.put("强沙尘暴","Strong sandstorm");
		ICON_SINA_EN_STRING_MAP.put("浮沙","Quick sand");
		ICON_SINA_EN_STRING_MAP.put("扬沙","Blowing sand");

	}
	public static final HashMap<String, String> ICON_SINA_TW_STRING_MAP = new HashMap<String, String>();
	static {
		ICON_SINA_TW_STRING_MAP.put("晴","晴");
		ICON_SINA_TW_STRING_MAP.put("多云","多雲");
		ICON_SINA_TW_STRING_MAP.put("雾","霧");
		ICON_SINA_TW_STRING_MAP.put("烟","煙");
		ICON_SINA_TW_STRING_MAP.put("霾","霾");
		ICON_SINA_TW_STRING_MAP.put("暴雾","暴霧");
		ICON_SINA_TW_STRING_MAP.put("浓雾","濃霧");
		ICON_SINA_TW_STRING_MAP.put("阴","陰");
		ICON_SINA_TW_STRING_MAP.put("阵雨","陣雨");
		ICON_SINA_TW_STRING_MAP.put("雷阵雨","雷陣雨");
		ICON_SINA_TW_STRING_MAP.put("雷阵雨伴有冰雹","雷陣雨伴有冰雹");
		ICON_SINA_TW_STRING_MAP.put("雷阵雨并伴有冰雹","雷陣雨並伴有冰雹");
		ICON_SINA_TW_STRING_MAP.put("雨夹雪","雨夾雪");
		ICON_SINA_TW_STRING_MAP.put("冻雨","凍雨");
		ICON_SINA_TW_STRING_MAP.put("小雨","小雨");
		ICON_SINA_TW_STRING_MAP.put("冰雹","冰雹");
		ICON_SINA_TW_STRING_MAP.put("小到中雨","小到中雨");
		ICON_SINA_TW_STRING_MAP.put("中到大雨","中到大雨");
		ICON_SINA_TW_STRING_MAP.put("中雨","中雨");
		ICON_SINA_TW_STRING_MAP.put("大雨","大雨");
		ICON_SINA_TW_STRING_MAP.put("大到暴雨","大到暴雨");
		ICON_SINA_TW_STRING_MAP.put("暴雨","暴雨");
		ICON_SINA_TW_STRING_MAP.put("大暴雨","大暴雨");
		ICON_SINA_TW_STRING_MAP.put("特大暴雨","特大暴雨");
		ICON_SINA_TW_STRING_MAP.put("阵雪","陣雪");
		ICON_SINA_TW_STRING_MAP.put("小雪","小雪");
		ICON_SINA_TW_STRING_MAP.put("小到中雪","小到中雪");
		ICON_SINA_TW_STRING_MAP.put("中雪","中雪");
		ICON_SINA_TW_STRING_MAP.put("中到大雪","中到大雪");
		ICON_SINA_TW_STRING_MAP.put("大雪","大雪");
		ICON_SINA_TW_STRING_MAP.put("大到暴雪","大到暴雪");
		ICON_SINA_TW_STRING_MAP.put("暴雪","暴雪");
		ICON_SINA_TW_STRING_MAP.put("沙尘暴","沙塵暴");
		ICON_SINA_TW_STRING_MAP.put("强沙尘暴","強沙塵暴");
		ICON_SINA_TW_STRING_MAP.put("浮沙","浮沙");
		ICON_SINA_TW_STRING_MAP.put("扬沙","揚沙");

	}
	public static final HashMap<Integer, String> ICON_SINA_APP_MAP = new HashMap<Integer, String>();
	static {
		ICON_SINA_APP_MAP.put(0,"d0");
		ICON_SINA_APP_MAP.put(1,"d1");
		ICON_SINA_APP_MAP.put(2,"d3");
		ICON_SINA_APP_MAP.put(3,"d4");
		ICON_SINA_APP_MAP.put(4,"d4");
		ICON_SINA_APP_MAP.put(5,"d5");
		ICON_SINA_APP_MAP.put(6,"d6");
		ICON_SINA_APP_MAP.put(7,"d7");
		ICON_SINA_APP_MAP.put(8,"d7");
		ICON_SINA_APP_MAP.put(9,"d8");
		ICON_SINA_APP_MAP.put(10,"d8");
		ICON_SINA_APP_MAP.put(11,"d8");
		ICON_SINA_APP_MAP.put(12,"d8");
		ICON_SINA_APP_MAP.put(13,"d10");
		ICON_SINA_APP_MAP.put(14,"d10");
		ICON_SINA_APP_MAP.put(15,"d10");
		ICON_SINA_APP_MAP.put(16,"d11");
		ICON_SINA_APP_MAP.put(17,"d11");
		ICON_SINA_APP_MAP.put(18,"d2");
		ICON_SINA_APP_MAP.put(19,"d7");
		ICON_SINA_APP_MAP.put(20,"d12");
		ICON_SINA_APP_MAP.put(21,"d7");
		ICON_SINA_APP_MAP.put(22,"d7");
		ICON_SINA_APP_MAP.put(23,"d8");
		ICON_SINA_APP_MAP.put(24,"d8");
		ICON_SINA_APP_MAP.put(25,"d8");
		ICON_SINA_APP_MAP.put(26,"d10");
		ICON_SINA_APP_MAP.put(27,"d10");
		ICON_SINA_APP_MAP.put(28,"d11");
		ICON_SINA_APP_MAP.put(29,"d13");
		ICON_SINA_APP_MAP.put(30,"d13");
		ICON_SINA_APP_MAP.put(31,"d12");
	}
	public static final HashMap<Integer, String> ICON_SINA_EN_APP_STRING_MAP = new HashMap<Integer, String>();
	static {
		ICON_SINA_EN_APP_STRING_MAP.put(0,"Sun");
		ICON_SINA_EN_APP_STRING_MAP.put(1,"Cloudy");
		ICON_SINA_EN_APP_STRING_MAP.put(18,"Fog");
		ICON_SINA_EN_APP_STRING_MAP.put(2,"Overcast");
		ICON_SINA_EN_APP_STRING_MAP.put(3,"Showers");
		ICON_SINA_EN_APP_STRING_MAP.put(4,"Scattered T-storms");
		ICON_SINA_EN_APP_STRING_MAP.put(5,"Scattered T-storms");
		ICON_SINA_EN_APP_STRING_MAP.put(6,"Sleet");
		ICON_SINA_EN_APP_STRING_MAP.put(19,"Freezing rain");
		ICON_SINA_EN_APP_STRING_MAP.put(7,"Light rain");
		ICON_SINA_EN_APP_STRING_MAP.put(21,"Small to moderate rain");
		ICON_SINA_EN_APP_STRING_MAP.put(22,"Moderate to heavy rain");
		ICON_SINA_EN_APP_STRING_MAP.put(8,"Moderate rain");
		ICON_SINA_EN_APP_STRING_MAP.put(9,"Heavy rain");
		ICON_SINA_EN_APP_STRING_MAP.put(24,"Rainstorm");
		ICON_SINA_EN_APP_STRING_MAP.put(25,"Rainstorm");
		ICON_SINA_EN_APP_STRING_MAP.put(23,"Heavy rain to rainstorm");
		ICON_SINA_EN_APP_STRING_MAP.put(10,"Rainstorm");
		ICON_SINA_EN_APP_STRING_MAP.put(11,"Big Rainstorm");
		ICON_SINA_EN_APP_STRING_MAP.put(12,"Heavy rain to rainstorm");
		ICON_SINA_EN_APP_STRING_MAP.put(13,"Snow shower");
		ICON_SINA_EN_APP_STRING_MAP.put(14,"Slight snow");
		ICON_SINA_EN_APP_STRING_MAP.put(26,"Small to moderate snow");
		ICON_SINA_EN_APP_STRING_MAP.put(15,"In the snow");
		ICON_SINA_EN_APP_STRING_MAP.put(27,"Moderate to heavy snow");
		ICON_SINA_EN_APP_STRING_MAP.put(16,"Heavy snow");
		ICON_SINA_EN_APP_STRING_MAP.put(28,"To Blizzard");
		ICON_SINA_EN_APP_STRING_MAP.put(17,"Blizzard");
		ICON_SINA_EN_APP_STRING_MAP.put(20,"Sand storm");
		ICON_SINA_EN_APP_STRING_MAP.put(31,"Strong sandstorm");
		ICON_SINA_EN_APP_STRING_MAP.put(29,"Quick sand");
		ICON_SINA_EN_APP_STRING_MAP.put(30,"Blowing sand");
	}
	public static final HashMap<String, String> ICON_SINA_TW_APP_STRING_MAP = new HashMap<String, String>();
	static {
		ICON_SINA_TW_APP_STRING_MAP.put("0","晴");
		ICON_SINA_TW_APP_STRING_MAP.put("1","多雲");
		ICON_SINA_TW_APP_STRING_MAP.put("18","霧");
		ICON_SINA_TW_APP_STRING_MAP.put("2","陰");
		ICON_SINA_TW_APP_STRING_MAP.put("3","陣雨");
		ICON_SINA_TW_APP_STRING_MAP.put("4","雷陣雨");
		ICON_SINA_TW_APP_STRING_MAP.put("5","雷雨冰雹");
		ICON_SINA_TW_APP_STRING_MAP.put("6","雨夾雪");
		ICON_SINA_TW_APP_STRING_MAP.put("19","凍雨");
		ICON_SINA_TW_APP_STRING_MAP.put("7","小雨");
		ICON_SINA_TW_APP_STRING_MAP.put("21","小到中雨");
		ICON_SINA_TW_APP_STRING_MAP.put("22","中到大雨");
		ICON_SINA_TW_APP_STRING_MAP.put("8","中雨");
		ICON_SINA_TW_APP_STRING_MAP.put("9","大雨");
		ICON_SINA_TW_APP_STRING_MAP.put("23","大到暴雨");
		ICON_SINA_TW_APP_STRING_MAP.put("10","暴雨");
		ICON_SINA_TW_APP_STRING_MAP.put("11","大暴雨");
		ICON_SINA_TW_APP_STRING_MAP.put("12","特大暴雨");
		ICON_SINA_TW_APP_STRING_MAP.put("24","暴雨大暴雨");
		ICON_SINA_TW_APP_STRING_MAP.put("25","大到特大暴雨");
		ICON_SINA_TW_APP_STRING_MAP.put("13","陣雪");
		ICON_SINA_TW_APP_STRING_MAP.put("14","小雪");
		ICON_SINA_TW_APP_STRING_MAP.put("26","小到中雪");
		ICON_SINA_TW_APP_STRING_MAP.put("15","中雪");
		ICON_SINA_TW_APP_STRING_MAP.put("27","中到大雪");
		ICON_SINA_TW_APP_STRING_MAP.put("16","大雪");
		ICON_SINA_TW_APP_STRING_MAP.put("28","大到暴雪");
		ICON_SINA_TW_APP_STRING_MAP.put("17","暴雪");
		ICON_SINA_TW_APP_STRING_MAP.put("20","沙塵暴");
		ICON_SINA_TW_APP_STRING_MAP.put("31","強沙塵暴");
		ICON_SINA_TW_APP_STRING_MAP.put("29","浮沙");
		ICON_SINA_TW_APP_STRING_MAP.put("30","揚沙");

	}
	public static final HashMap<String, Integer> ICON_K5_BIG_MAP = new HashMap<String, Integer>();
	static {
		ICON_K5_BIG_MAP.put("default", R.drawable.weather_widget_big_default);
        
		ICON_K5_BIG_MAP.put("d0", R.drawable.weather_widget_big_d0);
		ICON_K5_BIG_MAP.put("n0", R.drawable.weather_widget_big_n0);
        
		ICON_K5_BIG_MAP.put("d1", R.drawable.weather_widget_big_d1);
        ICON_K5_BIG_MAP.put("n1", R.drawable.weather_widget_big_n1);
        
        ICON_K5_BIG_MAP.put("d2", R.drawable.weather_widget_big_d2);
        ICON_K5_BIG_MAP.put("n2", R.drawable.weather_widget_big_d2);
        
        ICON_K5_BIG_MAP.put("d3", R.drawable.weather_widget_big_d3);
        ICON_K5_BIG_MAP.put("n3", R.drawable.weather_widget_big_n3);
        
        ICON_K5_BIG_MAP.put("d4", R.drawable.weather_widget_big_d4);
        ICON_K5_BIG_MAP.put("n4", R.drawable.weather_widget_big_d4);
        
        ICON_K5_BIG_MAP.put("d5", R.drawable.weather_widget_big_d5);
        ICON_K5_BIG_MAP.put("n5", R.drawable.weather_widget_big_d5);
        
        ICON_K5_BIG_MAP.put("d6", R.drawable.weather_widget_big_d6);
        ICON_K5_BIG_MAP.put("n6", R.drawable.weather_widget_big_d6);
        
        ICON_K5_BIG_MAP.put("d7", R.drawable.weather_widget_big_d7);
        ICON_K5_BIG_MAP.put("n7", R.drawable.weather_widget_big_d7);
        
        ICON_K5_BIG_MAP.put("d8", R.drawable.weather_widget_big_d8);
        ICON_K5_BIG_MAP.put("n8", R.drawable.weather_widget_big_d8);
        
        ICON_K5_BIG_MAP.put("d9", R.drawable.weather_widget_big_d9);
        ICON_K5_BIG_MAP.put("n9", R.drawable.weather_widget_big_d9);
        
        ICON_K5_BIG_MAP.put("d10", R.drawable.weather_widget_big_d10);
        ICON_K5_BIG_MAP.put("n10", R.drawable.weather_widget_big_d10);
        
        ICON_K5_BIG_MAP.put("d11", R.drawable.weather_widget_big_d11);
        ICON_K5_BIG_MAP.put("n11", R.drawable.weather_widget_big_d11);
        
        ICON_K5_BIG_MAP.put("d12", R.drawable.weather_widget_big_d12);
        ICON_K5_BIG_MAP.put("n12", R.drawable.weather_widget_big_d12);
        
        ICON_K5_BIG_MAP.put("d13", R.drawable.weather_widget_big_d13);
        ICON_K5_BIG_MAP.put("n13", R.drawable.weather_widget_big_n13);
        
        ICON_K5_BIG_MAP.put("d14", R.drawable.weather_widget_big_d14);
        ICON_K5_BIG_MAP.put("n14", R.drawable.weather_widget_big_d14);
        
        ICON_K5_BIG_MAP.put("d15", R.drawable.weather_widget_big_d15);
        ICON_K5_BIG_MAP.put("n15", R.drawable.weather_widget_big_d15);
        
        ICON_K5_BIG_MAP.put("d16", R.drawable.weather_widget_big_d16);
        ICON_K5_BIG_MAP.put("n16", R.drawable.weather_widget_big_d16);
        
        ICON_K5_BIG_MAP.put("d17", R.drawable.weather_widget_big_d17);
        ICON_K5_BIG_MAP.put("n17", R.drawable.weather_widget_big_d17);
        
        ICON_K5_BIG_MAP.put("d18", R.drawable.weather_widget_big_d18);
        ICON_K5_BIG_MAP.put("n18", R.drawable.weather_widget_big_d18);
        
        ICON_K5_BIG_MAP.put("d19", R.drawable.weather_widget_big_d19);
        ICON_K5_BIG_MAP.put("n19", R.drawable.weather_widget_big_d19);
        
        ICON_K5_BIG_MAP.put("d20", R.drawable.weather_widget_big_d20);
        ICON_K5_BIG_MAP.put("n20", R.drawable.weather_widget_big_d20);
        
        ICON_K5_BIG_MAP.put("d21", R.drawable.weather_widget_big_d21);
        ICON_K5_BIG_MAP.put("n21", R.drawable.weather_widget_big_d21);
        
        ICON_K5_BIG_MAP.put("d22", R.drawable.weather_widget_big_d22);
        ICON_K5_BIG_MAP.put("n22", R.drawable.weather_widget_big_d22);
        
        ICON_K5_BIG_MAP.put("d23", R.drawable.weather_widget_big_d23);
        ICON_K5_BIG_MAP.put("n23", R.drawable.weather_widget_big_d23);
        
        ICON_K5_BIG_MAP.put("d24", R.drawable.weather_widget_big_d24);
        ICON_K5_BIG_MAP.put("n24", R.drawable.weather_widget_big_d24);
        
        ICON_K5_BIG_MAP.put("d25", R.drawable.weather_widget_big_d25);
        ICON_K5_BIG_MAP.put("n25", R.drawable.weather_widget_big_d25);
        
        ICON_K5_BIG_MAP.put("d26", R.drawable.weather_widget_big_d26);
        ICON_K5_BIG_MAP.put("n26", R.drawable.weather_widget_big_d26);
        
        ICON_K5_BIG_MAP.put("d27", R.drawable.weather_widget_big_d27);
        ICON_K5_BIG_MAP.put("n27", R.drawable.weather_widget_big_d27);
        
        ICON_K5_BIG_MAP.put("d28", R.drawable.weather_widget_big_d28);
        ICON_K5_BIG_MAP.put("n28", R.drawable.weather_widget_big_d28);
        
        ICON_K5_BIG_MAP.put("d29", R.drawable.weather_widget_big_d29);
        ICON_K5_BIG_MAP.put("n29", R.drawable.weather_widget_big_d29);
        
        ICON_K5_BIG_MAP.put("d30", R.drawable.weather_widget_big_d30);
        ICON_K5_BIG_MAP.put("n30", R.drawable.weather_widget_big_d30);
        
        ICON_K5_BIG_MAP.put("d31", R.drawable.weather_widget_big_d31);
        ICON_K5_BIG_MAP.put("n31", R.drawable.weather_widget_big_d31);
	}
	public static final HashMap<String, Integer> ICON_K5_SMALL_MAP = new HashMap<String, Integer>();
	static {
		ICON_K5_SMALL_MAP.put("d0", R.drawable.weather_widget_small_d0);
		ICON_K5_SMALL_MAP.put("n0", R.drawable.weather_widget_small_n0);
		ICON_K5_SMALL_MAP.put("d1", R.drawable.weather_widget_small_d1);
        ICON_K5_SMALL_MAP.put("n1", R.drawable.weather_widget_small_n1);
        ICON_K5_SMALL_MAP.put("d2", R.drawable.weather_widget_small_d2);
        ICON_K5_SMALL_MAP.put("n2", R.drawable.weather_widget_small_d2);
        ICON_K5_SMALL_MAP.put("d3", R.drawable.weather_widget_small_d3);
        ICON_K5_SMALL_MAP.put("n3", R.drawable.weather_widget_small_n3);
        ICON_K5_SMALL_MAP.put("d4", R.drawable.weather_widget_small_d4);
        ICON_K5_SMALL_MAP.put("n4", R.drawable.weather_widget_small_d4);
        ICON_K5_SMALL_MAP.put("d5", R.drawable.weather_widget_small_d5);
        ICON_K5_SMALL_MAP.put("n5", R.drawable.weather_widget_small_d5);
        ICON_K5_SMALL_MAP.put("d6", R.drawable.weather_widget_small_d6);
        ICON_K5_SMALL_MAP.put("n6", R.drawable.weather_widget_small_d6);
        ICON_K5_SMALL_MAP.put("d7", R.drawable.weather_widget_small_d7);
        ICON_K5_SMALL_MAP.put("n7", R.drawable.weather_widget_small_d7);
        ICON_K5_SMALL_MAP.put("d8", R.drawable.weather_widget_small_d8);
        ICON_K5_SMALL_MAP.put("n8", R.drawable.weather_widget_small_d8);
        ICON_K5_SMALL_MAP.put("d9", R.drawable.weather_widget_small_d9);
        ICON_K5_SMALL_MAP.put("n9", R.drawable.weather_widget_small_d9);
        ICON_K5_SMALL_MAP.put("d10", R.drawable.weather_widget_small_d10);
        ICON_K5_SMALL_MAP.put("n10", R.drawable.weather_widget_small_d10);
        ICON_K5_SMALL_MAP.put("d11", R.drawable.weather_widget_small_d11);
        ICON_K5_SMALL_MAP.put("n11", R.drawable.weather_widget_small_d11);
        ICON_K5_SMALL_MAP.put("d12", R.drawable.weather_widget_small_d12);
        ICON_K5_SMALL_MAP.put("n12", R.drawable.weather_widget_small_d12);
        ICON_K5_SMALL_MAP.put("d13", R.drawable.weather_widget_small_d13);
        ICON_K5_SMALL_MAP.put("n13", R.drawable.weather_widget_small_n13);
        ICON_K5_SMALL_MAP.put("d14", R.drawable.weather_widget_small_d14);
        ICON_K5_SMALL_MAP.put("n14", R.drawable.weather_widget_small_d14);
        ICON_K5_SMALL_MAP.put("d15", R.drawable.weather_widget_small_d15);
        ICON_K5_SMALL_MAP.put("n15", R.drawable.weather_widget_small_d15);
        ICON_K5_SMALL_MAP.put("d16", R.drawable.weather_widget_small_d16);
        ICON_K5_SMALL_MAP.put("n16", R.drawable.weather_widget_small_d16);
        ICON_K5_SMALL_MAP.put("d17", R.drawable.weather_widget_small_d17);
        ICON_K5_SMALL_MAP.put("n17", R.drawable.weather_widget_small_d17);
        ICON_K5_SMALL_MAP.put("d18", R.drawable.weather_widget_small_d18);
        ICON_K5_SMALL_MAP.put("n18", R.drawable.weather_widget_small_d18);
        ICON_K5_SMALL_MAP.put("d19", R.drawable.weather_widget_small_d19);
        ICON_K5_SMALL_MAP.put("n19", R.drawable.weather_widget_small_d19);
        ICON_K5_SMALL_MAP.put("d20", R.drawable.weather_widget_small_d20);
        ICON_K5_SMALL_MAP.put("n20", R.drawable.weather_widget_small_d20);
        ICON_K5_SMALL_MAP.put("d21", R.drawable.weather_widget_small_d21);
        ICON_K5_SMALL_MAP.put("n21", R.drawable.weather_widget_small_d21);        
        ICON_K5_SMALL_MAP.put("d22", R.drawable.weather_widget_small_d22);
        ICON_K5_SMALL_MAP.put("n22", R.drawable.weather_widget_small_d22);        
        ICON_K5_SMALL_MAP.put("d23", R.drawable.weather_widget_small_d23);
        ICON_K5_SMALL_MAP.put("n23", R.drawable.weather_widget_small_d23);        
        ICON_K5_SMALL_MAP.put("d24", R.drawable.weather_widget_small_d24);
        ICON_K5_SMALL_MAP.put("n24", R.drawable.weather_widget_small_d24);        
        ICON_K5_SMALL_MAP.put("d25", R.drawable.weather_widget_small_d25);
        ICON_K5_SMALL_MAP.put("n25", R.drawable.weather_widget_small_d25);        
        ICON_K5_SMALL_MAP.put("d26", R.drawable.weather_widget_small_d26);
        ICON_K5_SMALL_MAP.put("n26", R.drawable.weather_widget_small_d26);       
        ICON_K5_SMALL_MAP.put("d27", R.drawable.weather_widget_small_d27);
        ICON_K5_SMALL_MAP.put("n27", R.drawable.weather_widget_small_d27);       
        ICON_K5_SMALL_MAP.put("d28", R.drawable.weather_widget_small_d28);
        ICON_K5_SMALL_MAP.put("n28", R.drawable.weather_widget_small_d28);      
        ICON_K5_SMALL_MAP.put("d29", R.drawable.weather_widget_small_d29);
        ICON_K5_SMALL_MAP.put("n29", R.drawable.weather_widget_small_d29);       
        ICON_K5_SMALL_MAP.put("d30", R.drawable.weather_widget_small_d30);
        ICON_K5_SMALL_MAP.put("n30", R.drawable.weather_widget_small_d30);       
        ICON_K5_SMALL_MAP.put("d31", R.drawable.weather_widget_small_d31);
        ICON_K5_SMALL_MAP.put("n31", R.drawable.weather_widget_small_d31);
	}
	public static final HashMap<String, Integer> SQUARE_ICON_MAP = new HashMap<String, Integer>();
	static {
		SQUARE_ICON_MAP.put("d0", R.drawable.weather_square_widget_d0);
		SQUARE_ICON_MAP.put("n0", R.drawable.weather_square_widget_d0);

		SQUARE_ICON_MAP.put("d1", R.drawable.weather_square_widget_d1);
		SQUARE_ICON_MAP.put("n1", R.drawable.weather_square_widget_d1);

		SQUARE_ICON_MAP.put("d2", R.drawable.weather_square_widget_d2);
		SQUARE_ICON_MAP.put("n2", R.drawable.weather_square_widget_d2);

		SQUARE_ICON_MAP.put("d3", R.drawable.weather_square_widget_d3);
		SQUARE_ICON_MAP.put("n3", R.drawable.weather_square_widget_d3);

		SQUARE_ICON_MAP.put("d4", R.drawable.weather_square_widget_d4);
		SQUARE_ICON_MAP.put("n4", R.drawable.weather_square_widget_d4);

		SQUARE_ICON_MAP.put("d5", R.drawable.weather_square_widget_d5);
		SQUARE_ICON_MAP.put("n5", R.drawable.weather_square_widget_d5);

		SQUARE_ICON_MAP.put("d6", R.drawable.weather_square_widget_d6);
		SQUARE_ICON_MAP.put("n6", R.drawable.weather_square_widget_d6);

		SQUARE_ICON_MAP.put("d7", R.drawable.weather_square_widget_d7);
		SQUARE_ICON_MAP.put("n7", R.drawable.weather_square_widget_d7);

		SQUARE_ICON_MAP.put("d8", R.drawable.weather_square_widget_d8);
		SQUARE_ICON_MAP.put("n8", R.drawable.weather_square_widget_d8);

		SQUARE_ICON_MAP.put("d9", R.drawable.weather_square_widget_d9);
		SQUARE_ICON_MAP.put("n9", R.drawable.weather_square_widget_d9);

		SQUARE_ICON_MAP.put("d10", R.drawable.weather_square_widget_d10);
		SQUARE_ICON_MAP.put("n10", R.drawable.weather_square_widget_d10);

		SQUARE_ICON_MAP.put("d11", R.drawable.weather_square_widget_d11);
		SQUARE_ICON_MAP.put("n11", R.drawable.weather_square_widget_d11);

		SQUARE_ICON_MAP.put("d12", R.drawable.weather_square_widget_d12);
		SQUARE_ICON_MAP.put("n12", R.drawable.weather_square_widget_d12);

		SQUARE_ICON_MAP.put("d13", R.drawable.weather_square_widget_d13);
		SQUARE_ICON_MAP.put("n13", R.drawable.weather_square_widget_d13);

		SQUARE_ICON_MAP.put("d14", R.drawable.weather_square_widget_d14);
		SQUARE_ICON_MAP.put("n14", R.drawable.weather_square_widget_d14);
	}
	public static final HashMap<String, Integer> MAGIC_ICON_MAP = new HashMap<String, Integer>();
	static {
		MAGIC_ICON_MAP.put("default", R.drawable.weather_magic_widget_default);
        
		MAGIC_ICON_MAP.put("d0", R.drawable.weather_magic_widget_d0);
		MAGIC_ICON_MAP.put("n0", R.drawable.weather_magic_widget_n0);
        
		MAGIC_ICON_MAP.put("d1", R.drawable.weather_magic_widget_d1);
		MAGIC_ICON_MAP.put("n1", R.drawable.weather_magic_widget_n1);
        
		MAGIC_ICON_MAP.put("d2", R.drawable.weather_magic_widget_d2);
		MAGIC_ICON_MAP.put("n2", R.drawable.weather_magic_widget_d2);
        
		MAGIC_ICON_MAP.put("d3", R.drawable.weather_magic_widget_d3);
		MAGIC_ICON_MAP.put("n3", R.drawable.weather_magic_widget_n3);
        
		MAGIC_ICON_MAP.put("d4", R.drawable.weather_magic_widget_d4);
		MAGIC_ICON_MAP.put("n4", R.drawable.weather_magic_widget_d4);
        
		MAGIC_ICON_MAP.put("d5", R.drawable.weather_magic_widget_d6);
		MAGIC_ICON_MAP.put("n5", R.drawable.weather_magic_widget_d6);
        
        MAGIC_ICON_MAP.put("d6", R.drawable.weather_magic_widget_d6);
        MAGIC_ICON_MAP.put("n6", R.drawable.weather_magic_widget_d6);
        
        MAGIC_ICON_MAP.put("d7", R.drawable.weather_magic_widget_d7);
        MAGIC_ICON_MAP.put("n7", R.drawable.weather_magic_widget_d7);
        
        MAGIC_ICON_MAP.put("d8", R.drawable.weather_magic_widget_d7);
        MAGIC_ICON_MAP.put("n8", R.drawable.weather_magic_widget_d7);
        
        MAGIC_ICON_MAP.put("d9", R.drawable.weather_magic_widget_d7);
        MAGIC_ICON_MAP.put("n9", R.drawable.weather_magic_widget_d7);
        
        MAGIC_ICON_MAP.put("d10", R.drawable.weather_magic_widget_d10);
        MAGIC_ICON_MAP.put("n10", R.drawable.weather_magic_widget_d10);
        
        MAGIC_ICON_MAP.put("d11", R.drawable.weather_magic_widget_d10);
        MAGIC_ICON_MAP.put("n11", R.drawable.weather_magic_widget_d10);
        
        MAGIC_ICON_MAP.put("d12", R.drawable.weather_magic_widget_d10);
        MAGIC_ICON_MAP.put("n12", R.drawable.weather_magic_widget_d10);
        
        MAGIC_ICON_MAP.put("d13", R.drawable.weather_magic_widget_d13);
        MAGIC_ICON_MAP.put("n13", R.drawable.weather_magic_widget_n13);
        
        MAGIC_ICON_MAP.put("d14", R.drawable.weather_magic_widget_d14);
        MAGIC_ICON_MAP.put("n14", R.drawable.weather_magic_widget_d14);
        
        MAGIC_ICON_MAP.put("d15", R.drawable.weather_magic_widget_d14);
        MAGIC_ICON_MAP.put("n15", R.drawable.weather_magic_widget_d14);
        
        MAGIC_ICON_MAP.put("d16", R.drawable.weather_magic_widget_d14);
        MAGIC_ICON_MAP.put("n16", R.drawable.weather_magic_widget_d14);
        
        MAGIC_ICON_MAP.put("d17", R.drawable.weather_magic_widget_d17);
        MAGIC_ICON_MAP.put("n17", R.drawable.weather_magic_widget_d17);
        
        MAGIC_ICON_MAP.put("d18", R.drawable.weather_magic_widget_d18);
        MAGIC_ICON_MAP.put("n18", R.drawable.weather_magic_widget_d18);
        
        MAGIC_ICON_MAP.put("d19", R.drawable.weather_magic_widget_d7);
        MAGIC_ICON_MAP.put("n19", R.drawable.weather_magic_widget_d7);
        
        MAGIC_ICON_MAP.put("d20", R.drawable.weather_magic_widget_d20);
        MAGIC_ICON_MAP.put("n20", R.drawable.weather_magic_widget_d20);
        
        MAGIC_ICON_MAP.put("d21", R.drawable.weather_magic_widget_d7);
        MAGIC_ICON_MAP.put("n21", R.drawable.weather_magic_widget_d7);
        
        MAGIC_ICON_MAP.put("d22", R.drawable.weather_magic_widget_d7);
        MAGIC_ICON_MAP.put("n22", R.drawable.weather_magic_widget_d7);
        
        MAGIC_ICON_MAP.put("d23", R.drawable.weather_magic_widget_d10);
        MAGIC_ICON_MAP.put("n23", R.drawable.weather_magic_widget_d10);
        
        MAGIC_ICON_MAP.put("d24", R.drawable.weather_magic_widget_d10);
        MAGIC_ICON_MAP.put("n24", R.drawable.weather_magic_widget_d10);
        
        MAGIC_ICON_MAP.put("d25", R.drawable.weather_magic_widget_d10);
        MAGIC_ICON_MAP.put("n25", R.drawable.weather_magic_widget_d10);
        
        MAGIC_ICON_MAP.put("d26", R.drawable.weather_magic_widget_d17);
        MAGIC_ICON_MAP.put("n26", R.drawable.weather_magic_widget_d17);
        
        MAGIC_ICON_MAP.put("d27", R.drawable.weather_magic_widget_d17);
        MAGIC_ICON_MAP.put("n27", R.drawable.weather_magic_widget_d17);
        
        MAGIC_ICON_MAP.put("d28", R.drawable.weather_magic_widget_d17);
        MAGIC_ICON_MAP.put("n28", R.drawable.weather_magic_widget_d17);
        
        MAGIC_ICON_MAP.put("d29", R.drawable.weather_magic_widget_d20);
        MAGIC_ICON_MAP.put("n29", R.drawable.weather_magic_widget_d20);
        
        MAGIC_ICON_MAP.put("d30", R.drawable.weather_magic_widget_d20);
        MAGIC_ICON_MAP.put("n30", R.drawable.weather_magic_widget_d20);
        
        MAGIC_ICON_MAP.put("d31", R.drawable.weather_magic_widget_d20);
        MAGIC_ICON_MAP.put("n31", R.drawable.weather_magic_widget_d20);
	}
	public static final HashMap<Integer, String> MONTH = new HashMap<Integer, String>();
	static {
		MONTH.put(1, "Jan");
		MONTH.put(2, "Feb");
		MONTH.put(3, "Mar");
		MONTH.put(4, "Apr");
		MONTH.put(5, "May");
		MONTH.put(6, "Jun");
		MONTH.put(7, "Jul");
		MONTH.put(8, "Aug");
		MONTH.put(9, "Sep");
		MONTH.put(10, "Oct");
		MONTH.put(11, "Nov");
		MONTH.put(12, "Dec");
    };
	public static final String CITY_ID = "city_id";
	public static final String CITY_NAME = "city_name";
	public static final String ACTION_UPDATE_WEATHER = "com.lenovo.launcher.widgets.weather.update_weather";
	public static final String ACTION_UPDATE_WEATHER_FAILED = "com.lenovo.launcher.widgets.weather.update_weather_failed";
	public static final String ACTION_UPDATE_DEFAILD_WEATHER = "com.lenovo.leos.widgets.weather.update_defaild_weather";
	public static final String ACTION_WEATHER_ANIMATE_STOP = "com.lenovo.leos.widgets.weather.stop";
	public static final String ACTION_WEATHER_WEATHERANIMATION_STOP = "com.lenovo.leos.weatheranimation.stop";
	public static final String ACTION_WEATHER_ANIMATE_START = "com.lenovo.launcher.widgets.weather.start";
	public static final String ACTION_WEATHER_NETWORK_STATE_CHANGE = "com.lenovo.action.ACTION_NETWORK_ENABLER_CHANGED";
	public static final String ACTION_WEATHER_INSTALL_CHANGE = "com.lenovo.leos.widgets.weather.refreshIntent";
	public static final String ACTION_LOCATION_CHANGE = "com.lenovo.leos.widgets.weather.LOCATION_CHANGED";
	public static final String ACTION_WEATHER_SCREEN_ANIMATE_START = "com.lenovo.leos.widgets.weather.animation.start";
	public static final String ACTION_WEATHER_SCREEN_ANIMATE_STOP = "com.lenovo.leos.widgets.weather.animation.stop";
	public static final String ACTION_WEATHER_APP_INSTALL = "com.lenovo.leos.widgets.weather.ACTION_APP_INSTALL";
	public static final String ACTION_WEATHER_APP_REMOVED = "com.lenovo.leos.widgets.weather.ACTION_APP_REMOVED";
	public static final String ACTION_WEATHER_WIDGET_UPDATEDATA = "com.lenovo.launcher2.weather.widget.updatedata";
	public static final String ACTION_WEATHER_WIDGET_UPDATEDATA_LOCAL_FROMSINA = "sina.mobile.tianqitong.action.startservice.addupdate_locate_city";
	public static final String ACTION_WEATHER_WIDGET_UPDATEDATA_FROMSINA = "sina.mobile.tianqitong.action.startservice.update_all_citys";
	public static final String ACTION_WEATHER_WIDGET_CHANGECITY_FROMSINA = "sina.mobile.tianqitong.INTENT_BC_ACTION_WIDGETCITY_CHANGED";
	public static final String ACTION_WEATHER_WIDGET_ADDCITY_FROMSINA = "sina.mobile.tianqitong.INTENT_BC_ACTION_CITYWEATHERINFO_ADDED";
	public static final String ACTION_WEATHER_WIDGET_DELETECITY_FROMSINA = "sina.mobile.tianqitong.INTENT_BC_ACTION_CITYWEATHERINFO_DELETED";
	public static final String ACTION_WEATHER_WIDGET_CHANGE_EXWIDGET = "com.lenovo.launcher2.weather.widget.ADDEXWIDGET";
	public static final String ACTION_WEATHER_WIDGET_UPDATECITY_FROMSINA = "sina.mobile.tianqitong.INTENT_BC_ACTION_CITYWEATHERINFO_UPDATE";
	public static final String ACTION_WEATHER_WIDGET_TIME_TICK = "action_weather_widget_time_tick";
	public static final String ACTION_WEATHER_WIDGET_LOCATION_CHANGE = "action_weather_widget_location_change";
	public static final String ACTION_REFRESH_FETE_THEME_LAYOUT = "action_refresh_fete_theme_layout";
	public static final String ACTION_MES_ACTION_SCREEN_ON = "action_mes_action_screen_on";
	public static final String ACTION_TIEM_CHANGE = "action_tiem_change";
	public static final String ACTION_ADD_WEATHER_WIDGET="com.lenovo.launcher.weather.widget.weatherwidget.add";
	public static final String ACTION_WEATHER_WIDGET_SEVICE_RESTART="com.lenovo.launcher.weather.widget.weatherwidget";
	public static final String ACTION_ADD_TASKMANAGER_WIDGET = "com.lenovo.launcher.taskmanager.widget.add";
	public static final String ACTION_WEATHER_WIDGET_CARRIERTEXTCHANGE = "android.lenovo.action.CarrierTextChange";
	public static final String ACTION_WEATHER_WIDGET_CARRIER_UPDATE = "com.lenovo.action.Carrier.update";
	public static final String ACTION_WEATHER_WIDGET_PACKAGE_UPDATE = "com.lenovo.action.package.update";
	public static final String ACTION_SHORTCUT_WIDGET_SEVICE_START="com.lenovo.launcher.shortcut.widget.start";
	public static final String ACTION_SHORTCUT_WIDGET_SEVICE_CLOSE="com.lenovo.shortcut.close";
	public static final String ACTION_WIDGET_SEVICE_CLOSE= "com.lenovo.leos.toggle.remove";
    public static final String ACTION_ADD_LENOVOWIDGET_ACTIVITY = "android.intent.action.WORKSPACE_PICK_LEOSWIDGET_ACTIVITY";
    public static final String ACTION_ADD_LENOVO_WEATHER_WIDGET_ACTIVITY = "android.intent.action.WORKSPACE_PICK_LEOSWEATHERWIDGET_ACTIVITY";
    public static final String ACTION_ADD_LENOVOWIDGET = "android.intent.action.WORKSPACE_PICK_LEOSWIDGET";
    public static final String ACTION_ADD_LENOVO_WEATHER_WIDGET = "android.intent.action.WORKSPACE_PICK_LEOSWEATHERWIDGET";
	/* RK_ID: RK_LENOVO_WIDGET . AUT: kangwei . DATE: 2012-10-17 . S */
	public static final String ACTION_DELETE_LEOS_WIDGET="com.lenovo.launcher.LEOS_E2E_WIDGET_DELETE_ALL";
	public static final String EXTRA_DELETE_LEOS_WIDGET="LEOS_E2E_WIDGET_VIEW";
	/* RK_ID: RK_LENOVO_WIDGET . AUT: kangwei . DATE: 2012-10-17 . E */
	private static final String SEARCH_URL_PATTERN = "http://3g.sina.com.cn/interface/f/weather/api.php?wm=9007_0002&ver=2&day=5&word=$";

	public static final String THIS_WEATHER_WIDGET =
            "com.lenovo.launcher2.weather.widget";
	
	
	
	public static final int MES_UPDATE_WEATHER = 0X0001;
	public static final int MES_WEATHER_ANIMATE_STOP = 0X0002;
	public static final int MES_WEATHER_ANIMATE_START = 0X0003;
	public static final int MES_WEATHER_NETWORK_STATE_CHANGE = 0X0004;
	public static final int MES_WEATHER_INSTALL_CHANGE = 0X0005;
	public static final int MES_UPDATE_DETAILS_WEATHER = 0X0006;
	public static final int MES_LOCATION_CHANGE = 0X0007;
	public static final int MES_ACTION_SCREEN_ON = 0X0008;
	public static final int MES_CONNECTIVITY_ACTION = 0X0009;
	public static final int MES_REFRESH_FETE_THEME = 0X0010;
	public static final int MES_REFRESH_FETE_THEME_LAYOUT = 0X0011;
	public static final int MES_REFRESH_MAGICWEATER_LAYOUT = 0X0012;
	public static final int MES_WEATHER_SCREEN_ANIMATE_START = 0X0013;
	public static final int MES_WEATHER_WIDGET_INIT = 0X0014;
	public static final int MES_TIME_TICK = 0X0015;
	public static final int MES_TIEM_CHANGE = 0X0016;
	public static final int MES_WEATHER_APP_INSTALL = 0X0017;
	public static final int MES_WEATHER_APP_REMOVED = 0X0018;
	public static final int MES_WEATHER_DOWNLOAD_RECORD_REFRESH = 0X0019;
	public static final int MES_WEATHER_CHANGE_EXWIDGET = 0X0020;
	public static final int MES_WEATHER_WIDGET_CARRIER_UPDATE = 0X0021;
	public static final int MES_UPDATE_WEATHER_DETAILS = 0X00022;
	public static final int MES_UPDATE_WEATHER_ZHISHU_DETAILS = 0X00023;
	public static final int MES_UPDATE_WEATHER_CITYNAME = 0X00024;
	public static final int MES_CHANGE_CITY_WEATHER = 0X00025;
	public static final int MES_UPDATE_SERVICE_WEATHER_DETAILS = 0X00026;
	public static final int MES_UPDATE_LOCAL_WEATHER_CITYNAME = 0X00027;
	public static final int MES_UPDATE_TIME = 0X00028;
	public static final int MES_UPDATE_WEATHER_FAILED = 0x00029;
	public static final int MES_WEATHER_DOWNLOAD_RECORD_REFRESH_OUTTIME = 4000;
	
	public static final String DIALOG_X = "widget_dialog_x";
	public static final String DIALOG_Y = "widget_dialog_y";

	//theme
    private final static String PREF_THEME = "lenovo_launcher_theme";
    private static final String PACKAGENAME = SettingsValue.LAUNCHER_PACKAGE_NAME_PREF;
    private static final String DEFAULT_THEME = "DEFAULT THEME";
    private static final String THEME_DRAWABLE = "drawable";
	public static final String STORAGED_WEATHER_INFO = "weather_widget";
	public static final String INSTALL_APP_PACKAGENAME = "install_app_packagename";
	
	public static final String FETE_THEME_UPDATE_DATA ="fete_theme_update_data";
	public static final String WEATHER_WIDGET_CITYNAME ="weather_widget_cityname";
	public static final String WEATHER_WIDGET_ZHCITYNAME ="weather_widget_zhcityname";
	public static final String WEATHER_WIDGET_ENCITYNAME ="weather_widget_encityname";
	public static final String WEATHER_WIDGET_TWCITYNAME ="weather_widget_twcityname";
	
	
	public static final String EXTRA_WEATHER_EXWIDGET_PACKAGENAME ="extra_weather_exwidget_packagename";
	public static final String EXTRA_WEATHER_EXWIDGET_CLASSNAME ="extra_weather_exwidget_classname";
	
   /*RK_ID:RK_LEOSWIDGET AUT:kangwei DATE: 2012-10-17 START */
	public static final String FETE_THEME_UPDATE_PATH ="fete_theme_update_path";
   /*RK_ID:RK_LEOSWIDGET AUT:kangwei DATE: 2012-10-17 E */
	public static final String EXTRA_DIALOG_TYPE ="extra_dialog_type";
	public static final String WEATHER_DOWNLOAD_APK_URL ="" +
			"http://launcher.lenovo.com/launcher/data/attachment/app/WeatherAnimation.apk";
	// the only opened API for caller
	public static final String PREF_WEATHER_DOWNLOAD_CLICK ="pref_weather_download_click";
	public static final String PREF_WEATHER_DOWNLOAD_CLICK_TIME ="pref_weather_download_click_time";
	
	public static final String PLACEHOLDER = "$";
//    private static final String DETAILS_SERVER_URL_PATTERN = "http://m.weather.com.cn/data/$.html";
    private static final String DETAILS_SERVER_URL_PATTERN = "http://launcher.test.surepush.cn/launcher1/data/attachment/weather/$.html";
    private static final String CURRENT_SERVER_URL_PATTERN = "http://www.weather.com.cn/data/sk/$.html";
    
    private static final String KEY_WEATHER = "weatherinfo";
    // city info
    private static final String KEY_WEATHER_CITY_ID = "cityid";
    private static final String KEY_WEATHER_CITY_NAME = "city";
    private static final String KEY_WEATHER_CITY_NAME_EN = "city_en";
    private static final String KEY_WEATHER_DATE_ZH = "date_y";

    // date info
    public static final String KEY_WEATHER_FCHH = "fchh";
    public static final String KEY_WEATHER_TEMP_BASE = "temp";
    public static final String KEY_WEATHER_CONDITION_BASE = "weather";
    public static final String KEY_WEATHER_WIND_BASE = "wind";
    public static final String KEY_WEATHER_IMG_BASE = "img";
    public static final String KEY_WEATHER_FL_BASE = "fl";
    public static final String KEY_WEATHER_INDEX_BASE = "index";
    public static final String KEY_WEATHER_INDEX_UV_BASE = "index_uv";
    public static final String KEY_WEATHER_INDEX_XC_BASE = "index_xc";
    public static final String KEY_WEATHER_INDEX_TR_BASE = "index_tr";
    public static final String KEY_WEATHER_INDEX_CO_BASE = "index_co";
    public static final String KEY_WEATHER_INDEX_CL_BASE = "index_cl";
    public static final String KEY_WEATHER_INDEX_LS_BASE = "index_ls";
    public static final String KEY_WEATHER_INDEX_AG_BASE = "index_ag";
    
    public static final String SYSTEM_WEATHER_FCHH = "weather_fchh";
    public static final String SYSTEM_WEATHER_DETAIL_CITYID = "weather_detail_cityid";
    public static final String SYSTEM_WEATHER_TEMP_BASE = "weather_temp";
    public static final String SYSTEM_WEATHER_CONDITION_BASE = "weather_weather";
    public static final String SYSTEM_WEATHER_WIND_BASE = "weather_wind";
    public static final String SYSTEM_WEATHER_IMG_BASE = "weather_img";
    public static final String SYSTEM_WEATHER_FL_BASE = "weather_fl";
    public static final String SYSTEM_WEATHER_INDEX_BASE = "weather_index";
    public static final String SYSTEM_WEATHER_INDEX_UV_BASE = "weather_index_uv";
    public static final String SYSTEM_WEATHER_INDEX_XC_BASE = "weather_index_xc";
    public static final String SYSTEM_WEATHER_INDEX_TR_BASE = "weather_index_tr";
    public static final String SYSTEM_WEATHER_INDEX_CO_BASE = "weather_index_co";
    public static final String SYSTEM_WEATHER_INDEX_CL_BASE = "weather_index_cl";
    public static final String SYSTEM_WEATHER_INDEX_LS_BASE = "weather_index_ls";
    public static final String SYSTEM_WEATHER_INDEX_AG_BASE = "weather_index_ag";
    public static final String  SYSTEM_WEATHER_CURRENT_TIME_BASE = "weather_current_time";
    
    public static final String SYSTEM_WEATHER_TIME_TEMP_BASE = "weather_time_temp";
    public static final String SYSTEM_WEATHER_TIME_CITYID = "weather_time_cityid";
    public static final String SYSTEM_WEATHER_SD_BASE = "weather_sd";
    public static final String KEY_WEATHER_SD_BASE = "SD";
    public static final String SYSTEM_WEATHER_DEFAULT_VALUE = "";
    
    public static final String PRE_LAUNCHER = "com.lenovo.launcher2.prefs";
    public static final String PRE_LAUNCHER_KEY_PHONE = "pre_phone_identification";
    public static final String PRE_LAUNCHER_KEY_PHONE_BU = "pre_phone_identification_bu";
    public static final String PRE_LAUNCHER_KEY_PHONE_DEFAULT = "default";
    public static final boolean PRE_LAUNCHER_KEY_PHONE_DEFAULT_BU = false;
    public static final String PRE_LAUNCHER_KEY_PHONE_BAIDU = "baidu_sicily";
    
    public static String SINA_PACKAGENAME ="sina.mobile.tianqitong.a";
    public static String ANIM_PACKAGENAME= "com.lenovo.leos.weatheranimation"; 
    public static final String PREF_WEATHER_ANIM_SETTING = "pref_weather_anim_setting";
    public static final String PREF_LETHEME = "pref_theme";
    public static final String PRE_LAUNCHER_KEY_PHONE_CMMC = "phone_cmmc";
    public static String MPACKAGENAME = "com.lenovo.leos.weatheranimation" ;
    public static String MCLASSNAME = "com.lenovo.leos.weatheranimation.WeatherAnimService";
    public static final String PREF_START_ANIMA = "pref_start_anima";
	private static void clearweatherinfo(Context context)
	{
		Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_TEMP_BASE, SYSTEM_WEATHER_DEFAULT_VALUE);
		Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_CONDITION_BASE, SYSTEM_WEATHER_DEFAULT_VALUE);
		Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_WIND_BASE, SYSTEM_WEATHER_DEFAULT_VALUE);
		Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_IMG_BASE, SYSTEM_WEATHER_DEFAULT_VALUE);
		Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_FL_BASE, SYSTEM_WEATHER_DEFAULT_VALUE);
		Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_DETAIL_CITYID, SYSTEM_WEATHER_DEFAULT_VALUE);
		Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_INDEX_BASE, SYSTEM_WEATHER_DEFAULT_VALUE);
		Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_INDEX_UV_BASE, SYSTEM_WEATHER_DEFAULT_VALUE);
		Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_INDEX_XC_BASE, SYSTEM_WEATHER_DEFAULT_VALUE);
		Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_INDEX_TR_BASE, SYSTEM_WEATHER_DEFAULT_VALUE);
		Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_INDEX_CO_BASE, SYSTEM_WEATHER_DEFAULT_VALUE);
		Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_INDEX_CL_BASE, SYSTEM_WEATHER_DEFAULT_VALUE);
		Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_INDEX_LS_BASE, SYSTEM_WEATHER_DEFAULT_VALUE);
		Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_INDEX_AG_BASE, SYSTEM_WEATHER_DEFAULT_VALUE);
		Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_TIME_CITYID, SYSTEM_WEATHER_DEFAULT_VALUE);
	}
	public static void updateweatherinfo(Context context, String cityId, String cityName)
	{
		if(cityId==null||TextUtils.isEmpty(cityId))
			return;
		final String result = requestServer(cityId);
		R2.echo("result+"+result);
		if(result!=null){
			parseWeaher(context,result);
			context.sendBroadcast(new Intent(WeatherUtilites.ACTION_UPDATE_DEFAILD_WEATHER));
		}
	}
	private static String requestServer(String cityId) {
		String requestServer=null;
		HttpClient mclient = null;
        try {
            BasicHttpParams params = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(params, 8000);
            HttpConnectionParams.setSoTimeout(params, 8000);
            HttpClientParams.setRedirecting(params, true);
            mclient = new DefaultHttpClient(params);
            //
            HttpGet get = null;
            get = new HttpGet(DETAILS_SERVER_URL_PATTERN.replace(PLACEHOLDER, cityId));
            HttpResponse response = mclient.execute(get);
            StatusLine status = response.getStatusLine();
            if (status.getStatusCode() == 200) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                    	requestServer = EntityUtils.toString(entity,"GBK");
                    }
            }
        }catch(ConnectException e){
        	e.printStackTrace();
        }catch (ParseException e){
        	e.printStackTrace();
        }catch (IOException e){
        	e.printStackTrace();
        }
    	if(mclient!=null)
    		mclient.getConnectionManager().shutdown();
        return requestServer;

	}
	private static void parseWeaher(Context context ,String result)
	{
		Calendar gc = GregorianCalendar.getInstance();
		final long time = gc.getTimeInMillis();
		try {
			JSONObject jsonObj = new JSONObject(result);
			JSONObject info = jsonObj.getJSONObject(KEY_WEATHER);
			StringBuffer temp = new StringBuffer();
			StringBuffer weather = new StringBuffer();
			StringBuffer wind = new StringBuffer();
			StringBuffer img = new StringBuffer();
			StringBuffer fl = new StringBuffer();
			for(int i=1;i<7;i++){
				temp.append(info.getString(KEY_WEATHER_TEMP_BASE+i)).append("@");
				weather.append(info.getString(KEY_WEATHER_CONDITION_BASE+i)).append("@");
				wind.append(info.getString(KEY_WEATHER_WIND_BASE+i)).append("@");
				img.append(info.getString(KEY_WEATHER_IMG_BASE+(i*2-1))).append("@");
				img.append(info.getString(KEY_WEATHER_IMG_BASE+i*2)).append("@");
				fl.append(info.getString(KEY_WEATHER_FL_BASE+i)).append("@");
			}
			clearweatherinfo(context);
			Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_TEMP_BASE, temp.toString());
			Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_CONDITION_BASE, weather.toString());
			Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_WIND_BASE, wind.toString());
			Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_IMG_BASE, img.toString());
			Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_FL_BASE, fl.toString());
			Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_DETAIL_CITYID, info.getString(KEY_WEATHER_CITY_ID));
			Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_INDEX_BASE, info.getString(KEY_WEATHER_INDEX_BASE));
			Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_INDEX_UV_BASE, info.getString(KEY_WEATHER_INDEX_UV_BASE));
			Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_INDEX_XC_BASE, info.getString(KEY_WEATHER_INDEX_XC_BASE));
			Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_INDEX_TR_BASE, info.getString(KEY_WEATHER_INDEX_TR_BASE));
			Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_INDEX_CO_BASE, info.getString(KEY_WEATHER_INDEX_CO_BASE));
			Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_INDEX_CL_BASE, info.getString(KEY_WEATHER_INDEX_CL_BASE));
			Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_INDEX_LS_BASE, info.getString(KEY_WEATHER_INDEX_LS_BASE));
			Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_INDEX_AG_BASE, info.getString(KEY_WEATHER_INDEX_AG_BASE));
			Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_TIME_CITYID, info.getString(KEY_WEATHER_CITY_ID));
			Settings.System.putLong(context.getContentResolver(), SYSTEM_WEATHER_CURRENT_TIME_BASE, time);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static InputStream requestServerForWeatherData(Context context,
			String city, int forcastDays) {
		String post = "";
		String mUrl = getWeatherURL(context, city, forcastDays);
		int responseCode = 0;

		R2.echo("URL is " + mUrl);

		InputStream is = null;
		try {

			URL url = new URL(mUrl);
			HttpURLConnection mHttpURLConnection = (HttpURLConnection) url
					.openConnection();

			mHttpURLConnection.setRequestMethod("GET");
			mHttpURLConnection.setDoInput(true);
			mHttpURLConnection.setDoOutput(true);
			mHttpURLConnection.setRequestProperty("Content-length", String
					.valueOf(post.length()));
			mHttpURLConnection.setRequestProperty("Content-Type", "text/html");
			mHttpURLConnection.setConnectTimeout(8000);
			mHttpURLConnection.setReadTimeout(8000);
			responseCode = mHttpURLConnection.getResponseCode();

			R2.echo("Response code is " + responseCode);
			if (responseCode == 200) {
				is = mHttpURLConnection.getInputStream();
			}

		} catch (IOException e) {
		    e.printStackTrace();
			R2.echo(">>>>>Response code is " + responseCode);
		} catch (Exception e) {
			e.printStackTrace();
			R2.echo(">>>>>Response code is " + responseCode);
		} 

		return is;
	}

	private static String getWeatherURL(Context context, String city,
			int forcastDays) {
//		String tmpLang = configuration.locale.getLanguage();
		String locale = "zh_CN";
//		if ("zh".equals(tmpLang)) {
//			locale = "zh_CN";
//		} else if ("en".equals(tmpLang)) {
//			locale = "en_US";
//		}
		StringBuilder sb = new StringBuilder("");

		try{
		sb.append(QueryParams.KIDCServer).append(QueryParams.kWeatherURL)
				.append(QueryParams.kWeatherQuery).append(
						QueryParams.kCityParam).append(city).append(
						QueryParams.kForecastDaysParam).append(forcastDays)
				.append(QueryParams.kLocaleParam).append(locale).trimToSize();
		}catch(Exception e){
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static void clearOldWeatherInfo(Context context){
//        Settings.System.putString(context.getContentResolver(), IMAGE_SRC, "");
        Settings.System.putString(context.getContentResolver(), CONDITION,
                "");
        Settings.System.putString(context.getContentResolver(), CONDITIONID, "");
        Settings.System.putString(context.getContentResolver(), TEMPS, "");
        Settings.System.putString(context.getContentResolver(), WEATHERTEMPS, "");
        Settings.System.putInt(context.getContentResolver(), DAY, 0);
        Settings.System.putInt(context.getContentResolver(), HOUR, 0);
        Settings.System.putLong(context.getContentResolver(), WEATHER_UPDATETIME,0);

	}
	
	public static final String IMAGE_SRC = "image_src";
	public static final String IMAGE_SRC_TIME = "image_src_time";
    public static final String TEMPS = "widgettemps";
    public static final String WEATHERTEMPS = "temps";
    public static final String DAY = "day";
    public static final String HOUR = "hour";
    public static final String WEATHER_UPDATETIME = "weather_updatetime";
    public static final String CONDITION = "condition";
    public static final String CONDITIONID = "conditionid";
	public static void clearWeatherInfoInSettings(Context context) {
		R2.echo("clearWeatherInfoInSettings");
//        Settings.System.putString(context.getContentResolver(), IMAGE_SRC, "");
        Settings.System.putString(context.getContentResolver(), CONDITION,
                "");
        Settings.System.putString(context.getContentResolver(), CONDITIONID, "");
        Settings.System.putString(context.getContentResolver(), TEMPS, "");
        Settings.System.putString(context.getContentResolver(), WEATHERTEMPS, "");
        Settings.System.putInt(context.getContentResolver(), DAY, 0);
        Settings.System.putInt(context.getContentResolver(), HOUR, 0);
        Settings.System.putLong(context.getContentResolver(), WEATHER_UPDATETIME,0);
        
		Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_TEMP_BASE, "");
		Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_CONDITION_BASE, "");
		Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_WIND_BASE, "");
		Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_IMG_BASE, "");
		Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_FL_BASE, "");
		Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_DETAIL_CITYID, "");
		Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_INDEX_BASE, "");
		Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_INDEX_UV_BASE, "");
		Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_INDEX_XC_BASE, "");
		Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_INDEX_TR_BASE, "");
		Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_INDEX_CO_BASE, "");
		Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_INDEX_CL_BASE, "");
		Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_INDEX_LS_BASE, "");
		Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_INDEX_AG_BASE, "");
		Settings.System.putString(context.getContentResolver(), SYSTEM_WEATHER_TIME_CITYID, "");
		Settings.System.putLong(context.getContentResolver(), SYSTEM_WEATHER_CURRENT_TIME_BASE, 0);

        
    }
    public static Context GetCurrentThemesContext(Context context)
    {
    	Context tcx = context;
		try {
			String packagename = Settings.System.getString(context.getContentResolver(), PREF_THEME);
			R2.echo("packagename="+packagename);
			if(packagename!=null){
				if(!packagename.equals(DEFAULT_THEME)){
					tcx = context.createPackageContext(packagename, Context.CONTEXT_IGNORE_SECURITY);
				}else{
					tcx = context.createPackageContext(PACKAGENAME, Context.CONTEXT_IGNORE_SECURITY);
				}
			}else{
				tcx = context.createPackageContext(PACKAGENAME, Context.CONTEXT_IGNORE_SECURITY);
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			try {
				tcx = context.createPackageContext(PACKAGENAME, Context.CONTEXT_IGNORE_SECURITY);
			} catch (NameNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		return tcx;
    }
    public static Bitmap drawableToBitmap(String drawablename,Context context)
    {
    	Bitmap bitmap = null;
        if (context != null){
	        try {
		        int resID = context.getResources().getIdentifier(drawablename,THEME_DRAWABLE, context.getPackageName());
		        if (resID != 0) 
		        	bitmap  = BitmapFactory.decodeResource(context.getResources(), resID);
	        } catch (NotFoundException e) {
	            e.printStackTrace();
	        } catch (OutOfMemoryError e) {
	        	e.printStackTrace();
	        }finally{
	        	return bitmap;
	        }
	     }
		return bitmap;
    }
    public static ColorStateList findColorById(Resources res, int iconId, Context context) {
        if (iconId == 0)
            return null;

        try {
            // get icon id name
            String s = res.getResourceName(iconId);

            //parse the id name
            if (s != null) {
                int index = s.indexOf(File.separator);
                if (index != -1) {
                    s = s.substring(index + 1);
                    return findColorByResourceName(s, res.getResourceTypeName(iconId), context);
                }
            } // end if s != null
        } catch (NotFoundException e) {
//            e.printStackTrace();
        }
        
        return null;
    }
    
    /*
     * retrieve customize bitmap icon by given id name
     */
    public static ColorStateList findColorByResourceName(String name, String defType, Context context) {
        if (context == null)
            return null;
        int resID = context.getResources().getIdentifier(name, defType, context.getPackageName());
        if (resID == 0) {
        	return null;
        }        
        try {
	        ColorStateList colors = context.getResources().getColorStateList(resID);
	        return colors;
        } catch (NotFoundException e) {
        }

        return null;
    }
    
    public static boolean isThemeUpdateDate(Context context) {
		boolean refresh = false;
		String today = new SimpleDateFormat("yyyyMMdd").format(new Date());
		final SharedPreferences preferences = context.getSharedPreferences(
				WeatherUtilites.STORAGED_WEATHER_INFO, Activity.MODE_PRIVATE|Activity.MODE_APPEND);
		if(preferences!=null){
			final String update = preferences.getString(FETE_THEME_UPDATE_DATA, "");
			R2.echo("update = "+update);
			if(today.equals(update))
				return true;
		}
		return refresh;
	}
    public static void saveThemeUpdateDate(Context context) {
		String today = new SimpleDateFormat("yyyyMMdd").format(new Date());
		final SharedPreferences preferences = context.getSharedPreferences(
				STORAGED_WEATHER_INFO, Activity.MODE_PRIVATE|Activity.MODE_APPEND);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(FETE_THEME_UPDATE_DATA, today);
		editor.commit();
	}
    public static void saveCityName(Context context,City city) {
		final SharedPreferences preferences = context.getSharedPreferences(
				STORAGED_WEATHER_INFO, Activity.MODE_PRIVATE|Activity.MODE_APPEND);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(WEATHER_WIDGET_CITYNAME, city.getcityname());
		editor.putString(WEATHER_WIDGET_ENCITYNAME, city.getencity());
		editor.putString(WEATHER_WIDGET_TWCITYNAME, city.gettwcity());
		editor.putString(WEATHER_WIDGET_ZHCITYNAME, city.getzhcity());
		editor.commit();
	}
    public static void saveExWidgetInfo(Context context,String packagename,String classname) {
		final SharedPreferences preferences = context.getSharedPreferences(
				STORAGED_WEATHER_INFO, Activity.MODE_PRIVATE|Activity.MODE_APPEND);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(EXTRA_WEATHER_EXWIDGET_PACKAGENAME, packagename);
		editor.putString(EXTRA_WEATHER_EXWIDGET_CLASSNAME, classname);
		editor.commit();
	}
    public static LenovoExWigetInfo getExWidgetInfo(Context context) {
    	String packagename = "";
    	String classname = "";
		final SharedPreferences preferences = context.getSharedPreferences(
				STORAGED_WEATHER_INFO, Activity.MODE_PRIVATE|Activity.MODE_APPEND);
		if(preferences!=null){
			packagename = preferences.getString(EXTRA_WEATHER_EXWIDGET_PACKAGENAME,"");
			classname = preferences.getString(EXTRA_WEATHER_EXWIDGET_CLASSNAME,"");
		}
		LenovoExWigetInfo lwi = new LenovoExWigetInfo();
		lwi.activity_name = packagename;
    	lwi.widgetView_name = classname;
    	return lwi;
	}
    public static void saveCityName(Context context,String city) {
		final SharedPreferences preferences = context.getSharedPreferences(
				STORAGED_WEATHER_INFO, Activity.MODE_PRIVATE|Activity.MODE_APPEND);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(WEATHER_WIDGET_CITYNAME, city);
		editor.commit();
	}
    public static void saveupdatetime(Context context,long time) {
//    	Calendar gc = GregorianCalendar.getInstance();
//        int day = (int) (gc.getTimeInMillis() / 86400000);
//        int hour = gc.get(Calendar.HOUR_OF_DAY);
		final SharedPreferences preferences = context.getSharedPreferences(
				STORAGED_WEATHER_INFO, Activity.MODE_PRIVATE|Activity.MODE_APPEND);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putLong(WEATHER_UPDATETIME, time);
		editor.commit();
		// Lockscreen used before
//        Settings.System.putInt(context.getContentResolver(), DAY, day);
//        Settings.System.putInt(context.getContentResolver(), HOUR, hour);
	}
    public static long getupdatetime(Context context) {
		final SharedPreferences preferences = context.getSharedPreferences(
				STORAGED_WEATHER_INFO, Activity.MODE_PRIVATE|Activity.MODE_APPEND);
		long updatetime =0;
		if(preferences!=null){
			updatetime = preferences.getLong(WEATHER_UPDATETIME, 0);
		}
		return updatetime;
	}
    public static String getCityName(Context context,int type) {
 		final SharedPreferences preferences = context.getSharedPreferences(
 				STORAGED_WEATHER_INFO, Activity.MODE_PRIVATE|Activity.MODE_APPEND);
 		String cityname =null;
 		if(preferences!=null){
 			switch(type){
 			case 0:
 				cityname = preferences.getString(WEATHER_WIDGET_ENCITYNAME, "");
 				break;
 			case 1:
 				cityname = preferences.getString(WEATHER_WIDGET_ZHCITYNAME, "");
 				break;
 			case 2:
 				cityname = preferences.getString(WEATHER_WIDGET_TWCITYNAME, "");
 				break;
			default:
				break;
 			}
 		}
 		if(cityname==null||TextUtils.isEmpty(cityname))
 			cityname = preferences.getString(WEATHER_WIDGET_CITYNAME, "");
 		R2.echo("getCityName  pre cityname"+cityname);
 		if(cityname==null||TextUtils.isEmpty(cityname)){
 			cityname =  Settings.System.getString(context.getContentResolver(), CITY_NAME);
	 		if(cityname!=null&&!TextUtils.isEmpty(cityname)){
	 			City city = getcityByKey(context,cityname);
				if(city!=null){
					saveCityName(context,city);
					return getCityName(context,type);
				}
	 		}
 		}
 		R2.echo("getCityName setting cityname"+cityname);
 		return cityname;
 	}
	public static int getLan() {
		if(Locale.getDefault().getCountry().equals(Locale.CHINA.getCountry())) { return 1; }
		if(Locale.getDefault().getCountry().equals(Locale.TAIWAN.getCountry())) { return 2; }
		return 0;
	}
    /*RK_ID:RK_LEOSWIDGET AUT:kangwei DATE: 2012-10-17 START */
    public static void saveThemeUpdatePicPath(Context context,String path) {
		final SharedPreferences preferences = context.getSharedPreferences(
				STORAGED_WEATHER_INFO, Activity.MODE_PRIVATE|Activity.MODE_APPEND);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(FETE_THEME_UPDATE_PATH, path);
		editor.commit();
	}
    public static boolean isThemeUpdatePath(Context context,String path) {
		boolean refresh = false;
		final SharedPreferences preferences = context.getSharedPreferences(
				WeatherUtilites.STORAGED_WEATHER_INFO, Activity.MODE_PRIVATE|Activity.MODE_APPEND);
		if(preferences!=null){
			final String update_path = preferences.getString(FETE_THEME_UPDATE_PATH, "");
			R2.echo("update = "+update_path);
			if(update_path.equals(path))
				return true;
		}
		return refresh;
	}
   /*RK_ID:RK_LEOSWIDGET AUT:kangwei DATE: 2012-10-17 E */
    public static void saveWeatherDownloadClickState(Context context,boolean value) {
		final SharedPreferences preferences = context.getSharedPreferences(
				STORAGED_WEATHER_INFO, Activity.MODE_PRIVATE|Activity.MODE_APPEND);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(PREF_WEATHER_DOWNLOAD_CLICK, value);
		editor.commit();
	}
    public static void saveWeatherDownloadClickTime(Context context,long value) {
		final SharedPreferences preferences = context.getSharedPreferences(
				STORAGED_WEATHER_INFO, Activity.MODE_PRIVATE|Activity.MODE_APPEND);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putLong(PREF_WEATHER_DOWNLOAD_CLICK_TIME, value);
		editor.commit();
	}

    public static boolean isdiaplayDownloadRecord(Context context)
    {
    	final SharedPreferences preferences = context.getSharedPreferences(
				STORAGED_WEATHER_INFO, Activity.MODE_PRIVATE|Activity.MODE_APPEND);
		if(preferences!=null){
			final boolean clickstate = preferences.getBoolean(PREF_WEATHER_DOWNLOAD_CLICK, false);
			R2.echo("Math.abs(currenttime-clicktime)/3600000>24 clickstate="+clickstate);
			if(clickstate){
				final long clicktime = preferences.getLong(PREF_WEATHER_DOWNLOAD_CLICK_TIME, 0);
				final long currenttime  = System.currentTimeMillis();
				final long time = Math.abs(currenttime-clicktime)/3600000;
				R2.echo("Math.abs(currenttime-clicktime)/3600000>24="+time);
				if(time>=24){
					return false;
				}
			}
		}
    	return true;
    }
    public static boolean getNetConnectState(Context context) {
		boolean netConnect = false;
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo infoM = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if ((info != null && info.isConnected())|| (infoM != null && infoM.isConnected())) {
			netConnect = true;
		}
		return netConnect;
	}
    public static Bitmap getBGBitmap(Context context, int width, int height) {

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        Drawable bm = context.getResources().getDrawable(R.drawable.mytheme_button_pic_bg);
        // create canvas of bitmap
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(0xcc000000);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return bitmap;
    }
    /*RK_ID:RK_LEOSWIDGET AUT:kangwei DATE: 2012-10-17 START */
    public static boolean hasInstances(Context context,String classname) {
    	try{
	    	ArrayList<LenovoWidgetViewInfo> sLeosWidgets = XLauncherModel.getLeosWidgetIds();
	    	if(sLeosWidgets!=null){
		    	for(LenovoWidgetViewInfo info :sLeosWidgets ){
		    		if(info!=null&&info.className!=null&&!TextUtils.isEmpty(info.className)){
			    		if(info.className.contains(classname)){
			    			return true;
			    		}
		    		}
		    	}
	    	}
    	}catch(RuntimeException ex){
    		ex.printStackTrace();
    	}
    	return false;
    }
    /*RK_ID:RK_LEOSWIDGET AUT:kangwei DATE: 2012-10-17 E */
	public static List<WeatherDetails> updateWeatherData(Context context,String cityName) {
		return requestServer(context, cityName);
	}
	private static List<WeatherDetails> requestServer(Context context,String cityName){
		try {
			String url = SEARCH_URL_PATTERN.replace(PLACEHOLDER, URLEncoder.encode(cityName));
			R5.echo("updateWeatherData="+url);
			return obtainWeacherInfo(url,context,cityName);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
	}
	private static List<WeatherDetails>  obtainWeacherInfo(String path,Context context,String cityName) throws ParserConfigurationException, SAXException, UnsupportedEncodingException, IOException
    {
		List<WeatherDetails> weatherdetailslist = new ArrayList<WeatherDetails>();

        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        SAXParser sp = parserFactory.newSAXParser();
        XMLReader xmlReader = sp.getXMLReader();
        WeatherHandler handler = new WeatherHandler(context,weatherdetailslist,cityName);
        xmlReader.setContentHandler(handler);
        String post = "";
		URL url = new URL(path);
		HttpURLConnection mHttpURLConnection = (HttpURLConnection) url
				.openConnection();

		mHttpURLConnection.setRequestMethod("GET");
		mHttpURLConnection.setDoInput(true);
		mHttpURLConnection.setDoOutput(true);
		mHttpURLConnection.setRequestProperty("Content-length", String
				.valueOf(post.length()));
		mHttpURLConnection.setUseCaches(false);
		mHttpURLConnection.setRequestProperty("Content-Type", "text/html");
		mHttpURLConnection.setConnectTimeout(8000);
		mHttpURLConnection.setReadTimeout(8000);
        InputStreamReader isr = new InputStreamReader(mHttpURLConnection.getInputStream(), "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String sssline=null;
        StringBuffer sb = new StringBuffer();
        while((sssline=br.readLine())!=null) {
                sb.append(sssline);
        }
        StringReader sr = new StringReader(sb.toString());
        InputSource is = new InputSource(sr);
        xmlReader.parse(is);
        br.close();
        R5.echo("obtainWeacherInfo weatherdetailslist="+weatherdetailslist.size());
        return weatherdetailslist;
    }
    public static int getNetWorkConnectState(Context context) {
    	int netConnect = 0;
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo infoM = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (info != null && info.isConnected()){
			netConnect = 1;
		}
		if(infoM != null && infoM.isConnected()){ 
			netConnect = 2;
		}
		return netConnect;
	}
    public static void saveWeather(Context context,List<WeatherDetails> weatherresults,String cityname)
    {
    	try{
			ContentValues values = new ContentValues();
			Calendar gc = GregorianCalendar.getInstance();
			saveupdatetime(context,gc.getTimeInMillis());
			City city = getcityByKey(context,cityname);
			if(city!=null)
				saveCityName(context,city);
			else
				saveCityName(context,cityname);
			deleteWeatherDetails(context);
			for(WeatherDetails detailsItem:weatherresults){
				values.put(WeatherDetails.WeatherDetailsColumns.CITYCHY, detailsItem.mcityChy);
				values.put(WeatherDetails.WeatherDetailsColumns.CITYCHYL, detailsItem.mcityChyL);
				values.put(WeatherDetails.WeatherDetailsColumns.CITYDATE, detailsItem.mcityDate);
				values.put(WeatherDetails.WeatherDetailsColumns.CITYDIRECTION, detailsItem.mcityDirection);
				values.put(WeatherDetails.WeatherDetailsColumns.CITYDIRECTION1, detailsItem.mcityDirection1);
				values.put(WeatherDetails.WeatherDetailsColumns.CITYDIRECTION2, detailsItem.mcityDirection2);
				values.put(WeatherDetails.WeatherDetailsColumns.CITYID, detailsItem.mcityId);
				values.put(WeatherDetails.WeatherDetailsColumns.CITYKTK, detailsItem.mcityKtk);
				values.put(WeatherDetails.WeatherDetailsColumns.CITYKTKL, detailsItem.mcityKtkL);
				values.put(WeatherDetails.WeatherDetailsColumns.CITYKTKS, detailsItem.mcityKtkS);
				values.put(WeatherDetails.WeatherDetailsColumns.CITYLASTUPDATE, detailsItem.mcityLastupdate);
				values.put(WeatherDetails.WeatherDetailsColumns.CITYNAME, detailsItem.mcityName);
				values.put(WeatherDetails.WeatherDetailsColumns.CITYPOLLUTION, detailsItem.mcityPollution);
				values.put(WeatherDetails.WeatherDetailsColumns.CITYPOLLUTIONL, detailsItem.mcityPollutionL);
				values.put(WeatherDetails.WeatherDetailsColumns.CITYPOLLUTIONS, detailsItem.mcityPollutionS);
				
				
				values.put(WeatherDetails.WeatherDetailsColumns.CITYPOWER, detailsItem.mcityPower);
				values.put(WeatherDetails.WeatherDetailsColumns.CITYSTATUS, detailsItem.mcityStatus);
				values.put(WeatherDetails.WeatherDetailsColumns.CITYSTATUS1, detailsItem.mcityStatus1);
				values.put(WeatherDetails.WeatherDetailsColumns.CITYSTATUS2, detailsItem.mcityStatus2);
				values.put(WeatherDetails.WeatherDetailsColumns.CITYTEMPERATURE, detailsItem.mcityTemperature);
				values.put(WeatherDetails.WeatherDetailsColumns.CITYTEMPERATURE1, detailsItem.mcityTemperature1);
				values.put(WeatherDetails.WeatherDetailsColumns.CITYTEMPERATURE2, detailsItem.mcityTemperature2);
				
				values.put(WeatherDetails.WeatherDetailsColumns.CITYXCZ, detailsItem.mcityXcz);
				values.put(WeatherDetails.WeatherDetailsColumns.CITYXCZL, detailsItem.mcityXczL);
				values.put(WeatherDetails.WeatherDetailsColumns.CITYXCZS, detailsItem.mcityXczS);
				
				values.put(WeatherDetails.WeatherDetailsColumns.CITYZWX, detailsItem.mcityZwx);
				values.put(WeatherDetails.WeatherDetailsColumns.CITYZWXL, detailsItem.mcityZwxL);
				values.put(WeatherDetails.WeatherDetailsColumns.CITYZWXS, detailsItem.mcityZwxS);
				context.getContentResolver().insert(WeatherDetails.WeatherDetailsColumns.CONTENT_URI
		                , values);
			}
	    }catch(Exception e){
			e.printStackTrace();
		}
    }
    public synchronized static List<WeatherDetails> getWeatherDetails(Context context)
    {
    	if (weatherdetails!=null&&weatherdetails.size()>0) {
			return weatherdetails;
		}
    	final String cityName = getCityName(context,1);
         weatherdetails = new ArrayList<WeatherDetails>();
        try{
	        String where = WeatherDetails.WeatherDetailsColumns.CITYNAME +" = ?";
	        Cursor noteCur = context.getContentResolver().query(WeatherDetails.WeatherDetailsColumns.CONTENT_URI, null, where, new String[]{cityName}, null);
	        if (noteCur != null && noteCur.moveToFirst()){
		        while(!noteCur.isAfterLast()){
		            String mcityDate = noteCur.getString(noteCur.getColumnIndex(WeatherDetails.WeatherDetailsColumns.CITYDATE));
		            String mcityId = noteCur.getString(noteCur.getColumnIndex(WeatherDetails.WeatherDetailsColumns.CITYID));
		            String mcityName =noteCur.getString(noteCur.getColumnIndex(WeatherDetails.WeatherDetailsColumns.CITYNAME));
		            String mcityStatus = noteCur.getString(noteCur.getColumnIndex(WeatherDetails.WeatherDetailsColumns.CITYSTATUS));
		            String mcityStatus1 = noteCur.getString(noteCur.getColumnIndex(WeatherDetails.WeatherDetailsColumns.CITYSTATUS1));
		            String mcityStatus2 = noteCur.getString(noteCur.getColumnIndex(WeatherDetails.WeatherDetailsColumns.CITYSTATUS2));
		            String mcityDirection = noteCur.getString(noteCur.getColumnIndex(WeatherDetails.WeatherDetailsColumns.CITYDIRECTION));
		            String mcityDirection1 = noteCur.getString(noteCur.getColumnIndex(WeatherDetails.WeatherDetailsColumns.CITYDIRECTION1));
		            String mcityDirection2 =noteCur.getString(noteCur.getColumnIndex(WeatherDetails.WeatherDetailsColumns.CITYDIRECTION2));
		            String mcityPower = noteCur.getString(noteCur.getColumnIndex(WeatherDetails.WeatherDetailsColumns.CITYPOWER));
		            String mcityTemperature = noteCur.getString(noteCur.getColumnIndex(WeatherDetails.WeatherDetailsColumns.CITYTEMPERATURE));
		            String mcityTemperature1 = noteCur.getString(noteCur.getColumnIndex(WeatherDetails.WeatherDetailsColumns.CITYTEMPERATURE1));
		            String mcityTemperature2 =noteCur.getString(noteCur.getColumnIndex(WeatherDetails.WeatherDetailsColumns.CITYTEMPERATURE2));
		            String mcityZwx = noteCur.getString(noteCur.getColumnIndex(WeatherDetails.WeatherDetailsColumns.CITYZWX));
		            String mcityZwxL = noteCur.getString(noteCur.getColumnIndex(WeatherDetails.WeatherDetailsColumns.CITYZWXL));
		            String mcityZwxS = noteCur.getString(noteCur.getColumnIndex(WeatherDetails.WeatherDetailsColumns.CITYZWXS));
		            String mcityKtk = noteCur.getString(noteCur.getColumnIndex(WeatherDetails.WeatherDetailsColumns.CITYKTK));
		            String mcityKtkL = noteCur.getString(noteCur.getColumnIndex(WeatherDetails.WeatherDetailsColumns.CITYKTKL));
		            String mcityKtkS = noteCur.getString(noteCur.getColumnIndex(WeatherDetails.WeatherDetailsColumns.CITYKTKS));
		            String mcityPollution = noteCur.getString(noteCur.getColumnIndex(WeatherDetails.WeatherDetailsColumns.CITYPOLLUTION));
		            String mcityPollutionL = noteCur.getString(noteCur.getColumnIndex(WeatherDetails.WeatherDetailsColumns.CITYPOLLUTIONL));
		            String mcityPollutionS = noteCur.getString(noteCur.getColumnIndex(WeatherDetails.WeatherDetailsColumns.CITYPOLLUTIONS));
		            String mcityXcz = noteCur.getString(noteCur.getColumnIndex(WeatherDetails.WeatherDetailsColumns.CITYXCZ));
		            String mcityXczL = noteCur.getString(noteCur.getColumnIndex(WeatherDetails.WeatherDetailsColumns.CITYXCZL));
		            String mcityXczS = noteCur.getString(noteCur.getColumnIndex(WeatherDetails.WeatherDetailsColumns.CITYXCZS));
		            String mcityChy = noteCur.getString(noteCur.getColumnIndex(WeatherDetails.WeatherDetailsColumns.CITYCHY));
		            String mcityChyL = noteCur.getString(noteCur.getColumnIndex(WeatherDetails.WeatherDetailsColumns.CITYCHYL));
		            String mcityLastupdate = noteCur.getString(noteCur.getColumnIndex(WeatherDetails.WeatherDetailsColumns.CITYLASTUPDATE));
		            WeatherDetails weatherdetail= new       WeatherDetails(mcityId, mcityName, mcityStatus,
		                            mcityStatus1,mcityStatus2,mcityDirection,
		                            mcityDirection1, mcityDirection2, mcityPower,
		                            mcityTemperature,mcityTemperature1,mcityTemperature2,
		                            mcityZwx,mcityZwxL,mcityZwxS,
		                            mcityKtk,mcityKtkL,mcityKtkS,
		                            mcityPollution,mcityPollutionL,mcityPollutionS,
		                            mcityXcz,mcityXczL,mcityXczS,
		                            mcityChy,mcityChyL, mcityLastupdate,mcityDate);
			        weatherdetails.add(weatherdetail);
			        noteCur.moveToNext();
		        }
	        }
	        if(noteCur!=null)
	            noteCur.close();
	    }catch(Exception e){
			e.printStackTrace();
		}
        return weatherdetails;
    }
	public static boolean isAdded(Context context,String cityName) {
		try{
			String where = WeatherDetails.WeatherDetailsColumns.CITYNAME +" = ?";
			Cursor cur = context.getContentResolver().query(WeatherDetails.WeatherDetailsColumns.CONTENT_URI, null, where, new String[]{cityName}, null);
	        if(cur!=null && cur.getCount() > 0){
//	            if(cur!=null)
	            	cur.close();
	        	return true;
	        }
	        if(cur!=null)
	        	cur.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	public static void deleteWeatherDetails(Context context)
	{
		try{
		context.getContentResolver().delete(WeatherDetails.WeatherDetailsColumns.CONTENT_URI, null, null);
		if (weatherdetails!=null&&weatherdetails.size()>0)
		   weatherdetails.clear();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static boolean getWeatherAppstate(Context context)
	{
		Cursor noteCur =null;
		boolean state = false;
		try{
			noteCur = context.getContentResolver().query(WeatherApp.WeatherAppColumns.CONTENT_URI, null, null, null, null);
			if (noteCur != null ){
				if(noteCur.getCount()>0)
					state = true;
				noteCur.close();
			}
		}catch(IllegalStateException e){
			e.printStackTrace();
		}catch(IllegalArgumentException ex){
			ex.printStackTrace();
		}catch(SQLiteException e){
			e.printStackTrace();
		}finally{
			if(noteCur!=null)
				noteCur.close();
		}
		return state;
	}
	public static List<WeatherApp> getWeatherApp(Context context)
	{
		List<WeatherApp> weatherapps = new ArrayList<WeatherApp>();
		Cursor noteCur = null;
		try{
		noteCur = context.getContentResolver().query(WeatherApp.WeatherAppColumns.CONTENT_URI, null, null, null, null);
		if (noteCur != null && noteCur.moveToFirst()){
			while(!noteCur.isAfterLast()){
				int day_code = -1;
				int night_code = noteCur.getInt(noteCur.getColumnIndex(WeatherApp.WeatherAppColumns.NIGHT_CODE));
				String city_name = noteCur.getString(noteCur.getColumnIndex(WeatherApp.WeatherAppColumns.CITY_NAME));
				String date = noteCur.getString(noteCur.getColumnIndex(WeatherApp.WeatherAppColumns.DATE));
				if(noteCur.isNull(noteCur.getColumnIndex(WeatherApp.WeatherAppColumns.DAY_CODE)))
					day_code = night_code;
				else
					day_code = noteCur.getInt(noteCur.getColumnIndex(WeatherApp.WeatherAppColumns.DAY_CODE));
				int day_temp = noteCur.getInt(noteCur.getColumnIndex(WeatherApp.WeatherAppColumns.DAY_TEMP));
				String day_text = noteCur.getString(noteCur.getColumnIndex(WeatherApp.WeatherAppColumns.DAY_TEXT));
				String day_wind = noteCur.getString(noteCur.getColumnIndex(WeatherApp.WeatherAppColumns.DAY_WIND));
				
				int night_temp = noteCur.getInt(noteCur.getColumnIndex(WeatherApp.WeatherAppColumns.NIGHT_TEMP));
				String night_text = noteCur.getString(noteCur.getColumnIndex(WeatherApp.WeatherAppColumns.NIGHT_TEXT));
				String night_wind = noteCur.getString(noteCur.getColumnIndex(WeatherApp.WeatherAppColumns.NIGHT_WIND));
				String region_name = noteCur.getString(noteCur.getColumnIndex(WeatherApp.WeatherAppColumns.REGION_NAME));
				String tqt_code = noteCur.getString(noteCur.getColumnIndex(WeatherApp.WeatherAppColumns.TQT_CODE));
				
				WeatherApp weatherapp = new WeatherApp(date,tqt_code, city_name,region_name,
						day_text,night_text,
						day_code,night_code,
						day_temp,night_temp,
						day_wind,night_wind);
				weatherapps.add(weatherapp);
				noteCur.moveToNext();
			}
		}
		}catch(IllegalStateException e){
			e.printStackTrace();
			return weatherapps;
		}catch(IllegalArgumentException ex){
			ex.printStackTrace();
			return weatherapps;
		}catch(SQLiteException e){
			e.printStackTrace();
		}finally{
			if(noteCur!=null)
				noteCur.close();	
		}
		return weatherapps;
	}
	public static WeatheDetailsApp getWeatherDetailsApp(Context context)
	{
		WeatheDetailsApp weathedetailsapp = null;
		Cursor noteCur = null;
		try{
		noteCur = context.getContentResolver().query(WeatheDetailsApp.WeatheDetailsAppColumns.CONTENT_URI, null, null, null, null);
		if (noteCur != null && noteCur.moveToFirst()){
			String city_name = noteCur.getString(noteCur.getColumnIndex(WeatheDetailsApp.WeatheDetailsAppColumns.CITY_NAME));
			String cloth = noteCur.getString(noteCur.getColumnIndex(WeatheDetailsApp.WeatheDetailsAppColumns.CLOTH));
			String cold = noteCur.getString(noteCur.getColumnIndex(WeatheDetailsApp.WeatheDetailsAppColumns.COLD));
			String comfort = noteCur.getString(noteCur.getColumnIndex(WeatheDetailsApp.WeatheDetailsAppColumns.COMFORT));
			String cwash = noteCur.getString(noteCur.getColumnIndex(WeatheDetailsApp.WeatheDetailsAppColumns.CWASH));
			String gmt_pubdate = noteCur.getString(noteCur.getColumnIndex(WeatheDetailsApp.WeatheDetailsAppColumns.GMT_PUBDATE));
			int humidity = noteCur.getInt(noteCur.getColumnIndex(WeatheDetailsApp.WeatheDetailsAppColumns.HUMIDITY));
			String insolate = noteCur.getString(noteCur.getColumnIndex(WeatheDetailsApp.WeatheDetailsAppColumns.INSOLATE));
			String oc_pubdate = noteCur.getString(noteCur.getColumnIndex(WeatheDetailsApp.WeatheDetailsAppColumns.LOC_PUBDATE));
			String region_name = noteCur.getString(noteCur.getColumnIndex(WeatheDetailsApp.WeatheDetailsAppColumns.REGION_NAME));
			String sport = noteCur.getString(noteCur.getColumnIndex(WeatheDetailsApp.WeatheDetailsAppColumns.SPORT));
			String sunrise = noteCur.getString(noteCur.getColumnIndex(WeatheDetailsApp.WeatheDetailsAppColumns.SUNRISE));
			String sunset = noteCur.getString(noteCur.getColumnIndex(WeatheDetailsApp.WeatheDetailsAppColumns.SUNSET));
			int temperature = noteCur.getInt(noteCur.getColumnIndex(WeatheDetailsApp.WeatheDetailsAppColumns.TEMPERATURE));
			String tqt_code = noteCur.getString(noteCur.getColumnIndex(WeatheDetailsApp.WeatheDetailsAppColumns.TQT_CODE));
			String umbrella = noteCur.getString(noteCur.getColumnIndex(WeatheDetailsApp.WeatheDetailsAppColumns.UMBRELLA));
			String uv = noteCur.getString(noteCur.getColumnIndex(WeatheDetailsApp.WeatheDetailsAppColumns.UV));
			String wind = noteCur.getString(noteCur.getColumnIndex(WeatheDetailsApp.WeatheDetailsAppColumns.WIND));
			weathedetailsapp = new  WeatheDetailsApp(tqt_code,city_name,region_name,
					oc_pubdate,gmt_pubdate,temperature,
					wind,humidity,
					sunrise,sunset,
					cloth,cold,
					comfort,uv,
					cwash,sport,
					insolate,umbrella);
       	}
		}catch(IllegalStateException e){
			e.printStackTrace();
		}catch(IllegalArgumentException ex){
			ex.printStackTrace();
		}catch(SQLiteException e){
			e.printStackTrace();
		}finally{
			if(noteCur!=null)
				noteCur.close();
		}
	    return weathedetailsapp;
	}
	public static String getWeatherDetailsAppName(Context context)
	{
		String city_name =null;
		Cursor noteCur = null;
		try{
			noteCur = context.getContentResolver().query(WeatheDetailsApp.WeatheDetailsAppColumns.CONTENT_URI, new String[]{"city_name"}, null, null, null);
			if (noteCur != null && noteCur.moveToFirst())
				city_name = noteCur.getString(noteCur.getColumnIndex(WeatheDetailsApp.WeatheDetailsAppColumns.CITY_NAME));
		}catch(IllegalArgumentException e){
			e.printStackTrace();
			return city_name;
		}
		catch(IllegalStateException e){
			e.printStackTrace();
		}catch(SQLiteException e){
			e.printStackTrace();
		}finally{
			if(noteCur!=null)
				noteCur.close();	
		}
	    return city_name;
	}
	public static String getWeatherDetailsAppUpdatetime(Context context)
	{
		String oc_pubdate =null;
		Cursor noteCur =null;
		try{
			noteCur = context.getContentResolver().query(WeatheDetailsApp.WeatheDetailsAppColumns.CONTENT_URI, new String[]{WeatheDetailsApp.WeatheDetailsAppColumns.LOC_PUBDATE}, null, null, null);
			if (noteCur != null && noteCur.moveToFirst())
				oc_pubdate = noteCur.getString(noteCur.getColumnIndex(WeatheDetailsApp.WeatheDetailsAppColumns.LOC_PUBDATE));
		    if(noteCur!=null)
		    	noteCur.close();
		}catch(IllegalStateException e){
			e.printStackTrace();
		}catch(IllegalArgumentException ex){
			ex.printStackTrace();
		}catch(SQLiteException e){
			e.printStackTrace();
		}finally{
			if(noteCur!=null)
				noteCur.close();	
		}
	    return oc_pubdate;
	}
	public static boolean findForPackage(Context context,String packagename) {
		final PackageManager packageManager = context.getPackageManager();

		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		mainIntent.setPackage(packagename);
		mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		final List<ResolveInfo> apps = packageManager.queryIntentActivities(
				mainIntent, 0);
		if(apps != null&&apps.size()>0){
			if(SINA_PACKAGENAME.equals(packagename)){
				PackageInfo packInfo;
				try {
					packInfo = packageManager.getPackageInfo(packagename,0);
					String ver = packInfo.versionName;
					if(ver!=null && !TextUtils.isEmpty(ver)){
						float fv = Float.parseFloat(ver);
						int b = Float.compare(fv, 2.3f);
						if(b>0)
							return true;
					}
					return false;
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
			}
			return true;
		}else
			return false;
	}
	private static String DB_PATH  = "/data/data/com.lenovo.launcher/databases/";
	private static String DB_PATH_DIR  = "/data/data/com.lenovo.launcher/databases/cities.db";
	private static String DB_NAME  = "cities.db";
	public static City getcityByKey(Context context,String key)
	{
		City city = null;
		SQLiteDatabase db= null;
		try{
			db = openDatabase(context);
		}catch(Exception e){
			e.printStackTrace();
		}
		if(db==null)
			return city;
		int lan =WeatherUtilites.getLan();
		Log.d("ac","lan="+lan);
		String where = CityColumns.COLUMN_CITY_NAME +" = ? or "+
				CityColumns.COLUMN_CITY_NAME_EN +" = ? or "+
				CityColumns.COLUMN_CITY_NAME_TW +" = ? ";
		try{
			Cursor cursor= db.query("cities", null
					,where,//
					new String[] {key,key,key}
					, null, null,null,null);
			if (cursor != null && cursor.moveToFirst()) {
				Log.d("ac","cursor="+cursor.getCount());
	    		String cityid =null;
	    		String cityname  =null;
	    		String zhcityname  =null;
	    		String encityname  =null;
	    		String twcityname  =null;
	    		String province = null;
	    		zhcityname = cursor.getString(cursor.getColumnIndex(CityColumns.COLUMN_CITY_NAME));
	    		twcityname = cursor.getString(cursor.getColumnIndex(CityColumns.COLUMN_CITY_NAME_TW));
	    		encityname = cursor.getString(cursor.getColumnIndex(CityColumns.COLUMN_CITY_NAME_EN));
	    		if(lan==1){
	    		    cityid = cursor.getString(CityColumns.INDEX_DEF_CITY_ID);
	    			cityname = cursor.getString(cursor.getColumnIndex(CityColumns.COLUMN_CITY_NAME));
	    			province = cursor.getString(CityColumns.INDEX_DEF_CITY_NAME_PROVINCE);
	    		}else if(lan==2){
	    			cityid = cursor.getString(CityColumns.INDEX_DEF_CITY_ID);
	    			cityname = cursor.getString(cursor.getColumnIndex(CityColumns.COLUMN_CITY_NAME_TW));
	    			province = cursor.getString(cursor.getColumnIndex(CityColumns.COLUMN_PROVINCE_NAME_TW));
	    		}else{
	    			cityid = cursor.getString(CityColumns.INDEX_DEF_CITY_ID);
	    			cityname =cursor.getString(cursor.getColumnIndex(CityColumns.COLUMN_CITY_NAME_EN));
	    			province = cursor.getString(cursor.getColumnIndex(CityColumns.COLUMN_PROVINCE_NAME_EN));
	    		}
	    		city = new City(zhcityname,cityname,twcityname,encityname,cityid,province);
			}
			if(cursor!=null)
				cursor.close();
			db.close();
		}catch(Exception ex){
			ex.printStackTrace();
			CreateDb(db,context);
			city = new City(key,key,key,key,null,key);
		}catch(StackOverflowError e){
			e.printStackTrace();
		}
		return city;
	}
	public static ArrayList<City> getcitysByKey(Context context,String key)
	{
		ArrayList<City> cities = new ArrayList<City>();
		SQLiteDatabase db = openDatabase(context);
		if(db==null)
			return cities;
		int lan =WeatherUtilites.getLan();
		Log.d("ac","lan="+lan);
		try{
			Cursor cursor= db.query("cities", new String[] {
					CityColumns.COLUMN_CITY_ID,
					CityColumns.COLUMN_CITY_NAME ,
					CityColumns.COLUMN_CITY_NAME_TW ,
					CityColumns.COLUMN_CITY_NAME_EN,
					CityColumns.COLUMN_PROVINCE_NAME ,
					CityColumns.COLUMN_PROVINCE_NAME_TW ,
					CityColumns.COLUMN_PROVINCE_NAME_EN}
					,CityColumns.COLUMN_CITY_NAME + " like ? " + //
							" or " + //
							"lower(" + CityColumns.COLUMN_CITY_NAME_EN + ") like ? "+
							" or " +
							CityColumns.COLUMN_CITY_NAME_TW + " like ? ",//
					new String[] { "%" + key + "%", "%" +  key.toLowerCase() + "%", "%" + key + "%" }
					, null, null,null,null);
			if (cursor != null && cursor.moveToFirst()&&cursor.getCount()>0) {
				Log.d("ac","cursor="+cursor.getCount());
		    	while(!cursor.isAfterLast()){
		    		String cityid =null;
		    		String cityname  =null;
		    		String zhcityname  =null;
		    		String encityname  =null;
		    		String twcityname  =null;
		    		String province = null;
		    		zhcityname = cursor.getString(CityColumns.INDEX_DEF_CITY_NAME);
		    		twcityname = cursor.getString(CityColumns.INDEX_DEF_CITY_NAME_TW);
		    		encityname = cursor.getString(CityColumns.INDEX_DEF_CITY_NAME_EN);
		    		if(lan==1){
		    		    cityid = cursor.getString(CityColumns.INDEX_DEF_CITY_ID);
		    			cityname = cursor.getString(CityColumns.INDEX_DEF_CITY_NAME);
		    			province = cursor.getString(CityColumns.INDEX_DEF_CITY_NAME_PROVINCE);
		    			if(province==null)
		    				province = cityname;
		    		}else if(lan==2){
		    			cityid = cursor.getString(CityColumns.INDEX_DEF_CITY_ID);
		    			cityname = cursor.getString(CityColumns.INDEX_DEF_CITY_NAME_TW);
		    			province = cursor.getString(CityColumns.INDEX_DEF_CITY_NAME_PROVINCE_TW);
		    			if(cityname==null)
		    				cityname = zhcityname;
		    			if(province==null)
		    				province = cityname;
		    		}else{
		    			cityid = cursor.getString(CityColumns.INDEX_DEF_CITY_ID);
		    			cityname = cursor.getString(CityColumns.INDEX_DEF_CITY_NAME_EN);
		    			province = cursor.getString(CityColumns.INDEX_DEF_CITY_NAME_PROVINCE_EN);
		    			if(province==null)
		    				province = cityname;
		    		}
		    		City city = new City(zhcityname,cityname,twcityname,encityname,cityid,province);
		    		cities.add(city); 
		    		cursor.moveToNext();
		    	}
			}
		    if(cursor!=null)
		    	cursor.close();
		    db.close();
		}catch(Exception ex){
			ex.printStackTrace();
			CreateDb(db,context);
		}
		return cities;
	}
	private static final int DATABASE_VERSION = 2;
	public static SQLiteDatabase CreateDb(SQLiteDatabase db,Context context){
		FileOutputStream out = null;
        InputStream ins = null;
		try {
            out = context.openFileOutput(DB_NAME, Context.MODE_PRIVATE);
            ins = context.getAssets().open(DB_NAME);
            byte[] b = new byte[1024];
            while (ins.read(b) != -1) {
                out.write(b);
            }
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
            try {
                if (ins != null)
                    ins.close();
                if (out != null)
                    out.close();
                db = SQLiteDatabase.openOrCreateDatabase(context.getFileStreamPath(DB_NAME), null);
        		if(db!=null)
        			db.setVersion(DATABASE_VERSION);
            } catch (IOException e) {
                e.printStackTrace();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
		return db;
	}
	public static SQLiteDatabase openDatabase(Context context){
		//数据库存储路径
		SQLiteDatabase db =null;
		try{
			db = SQLiteDatabase.openOrCreateDatabase(context.getFileStreamPath(DB_NAME), null);
	        int version = db.getVersion();
	
	        if (version != DATABASE_VERSION) {
	        	return CreateDb(db,context);
	        }
		}catch(SQLException e){
			e.printStackTrace();
			CreateDb(db,context);
		}
 		/*File jhPath=new File(DB_PATH_DIR);
 			//查看数据库文件是否存在
		if(jhPath.exists())
			//存在则直接返回打开的数据库
			db = SQLiteDatabase.openOrCreateDatabase(jhPath, null);
			if(db.getVersion()!=DATABASE_VERSION)
				return CreateDb(db,context);
		}else{
			//不存在先创建文件夹
	        return CreateDb(db,context);
		}*/
		return db;
	}/*
	public static SQLiteDatabase openDatabase(Context context){
		//数据库存储路径
		
 		File jhPath=new File(DB_PATH_DIR);
 			//查看数据库文件是否存在
		if(jhPath.exists()){
			//存在则直接返回打开的数据库
			return SQLiteDatabase.openOrCreateDatabase(jhPath, null);
		}else{
			//不存在先创建文件夹
			File path=new File(DB_PATH);
			if (path.mkdir()){
				System.out.println("创建成功");
			}else{
				System.out.println("创建失败");
			};
			try {//得到资源
				AssetManager am= context.getAssets();
				//得到数据库的输入流
				InputStream is=am.open(DB_NAME);
				//用输出流写到SDcard上面  
				FileOutputStream fos=new FileOutputStream(jhPath);
				//创建byte数组  用于1KB写一次
				byte[] buffer=new byte[1024];
				int count = 0;
				while((count = is.read(buffer))>0){
					fos.write(buffer,0,count);
				}
				//最后关闭就可以了
				fos.flush();
				fos.close();
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			//如果没有这个数据库  我们已经把他写到SD卡上了，然后在执行一次这个方法 就可以返回数据库了
			return openDatabase(context);
		}
	}*/
	public static Object getLeosWidgetViewToWorkspace(String packageName,String className,Context context) {
        if (packageName != null && packageName.length() > 0) {
            try {
                String apkName = context.getPackageManager().getApplicationInfo(packageName, 0).sourceDir;
                dalvik.system.PathClassLoader myClassLoader = new dalvik.system.PathClassLoader(apkName, "",
                        ClassLoader.getSystemClassLoader());
                Class classType = Class.forName(className, true, myClassLoader);
                Class[] args = new Class[] { Class.forName("android.content.Context") };
                Constructor cons = classType.getConstructor(args);
                return cons.newInstance(context.createPackageContext(packageName, Context.CONTEXT_RESTRICTED));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
    public static boolean setOnClickListenerIntent(final Context context,
			String targetPackage) {
		return setOnClickListenerIntent(context,targetPackage, null);
	}

	public static boolean setOnClickListenerIntent(final Context context,
			String targetPackage, String className) {
		List<ResolveInfo> entries = findActivitiesForPackage(context,
				targetPackage);
	
		if (entries != null && entries.size() > 0) {
	
			Intent intent = new Intent(Intent.ACTION_MAIN, null);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.setPackage(targetPackage);
			try {
				context.startActivity(intent);
			}catch (ActivityNotFoundException e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;

	}


	/**
	 * Query the package manager for MAIN/LAUNCHER activities in the supplied
	 * package.
	 * 
	 */
	public static List<ResolveInfo> findActivitiesForPackage(Context context,
			String packageName) {
		if(context==null)
			return null;
		final PackageManager packageManager = context.getPackageManager();

		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		mainIntent.setPackage(packageName);
		mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		final List<ResolveInfo> apps = packageManager.queryIntentActivities(
				mainIntent, 0);

		return apps != null ? apps : new ArrayList<ResolveInfo>();
	}
	public static void saveCarrier(Context context,String key,String Carrier) {
		final SharedPreferences preferences = context.getSharedPreferences(
				STORAGED_WEATHER_INFO, Activity.MODE_PRIVATE|Activity.MODE_APPEND);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(key, Carrier);
		editor.commit();
		context.sendBroadcast(new Intent(ACTION_WEATHER_WIDGET_CARRIER_UPDATE));
	}
    public static String getCarrier(Context context,String sim) {
    	String Carrier =null;
		final SharedPreferences preferences = context.getSharedPreferences(
				STORAGED_WEATHER_INFO, Activity.MODE_PRIVATE|Activity.MODE_APPEND);
		if(preferences!=null)
			Carrier = preferences.getString(sim, "");
		return Carrier;
	}
	public static void saveWeatherIconUpdate(final Context context,
			final String update,final String icon,final boolean isupdate) {
		/*new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Settings.System.putString(context.getContentResolver(), IMAGE_SRC,icon);
				Log.d("cs","icon="+icon);
		        Calendar gc = GregorianCalendar.getInstance();
		        if(!isupdate)
		        	Settings.System.putLong(context.getContentResolver(), WEATHER_UPDATETIME,0);
		        else
		        	Settings.System.putLong(context.getContentResolver(), WEATHER_UPDATETIME, gc.getTimeInMillis());
			}
		}.start();*/
    	new Thread() {
			@Override
			public void run() {
				final SharedPreferences preferences = context.getSharedPreferences(
						STORAGED_WEATHER_INFO, Activity.MODE_PRIVATE|Activity.MODE_APPEND);
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString(IMAGE_SRC_TIME, update);
				editor.putString(IMAGE_SRC, icon);
				editor.commit();
			}
    	}.start();
	}
    public static String getCurrentIcon(Context context) {
    	String icon =null;
		final SharedPreferences preferences = context.getSharedPreferences(
				STORAGED_WEATHER_INFO, Activity.MODE_PRIVATE|Activity.MODE_APPEND);
		if(preferences!=null)
			icon = preferences.getString(IMAGE_SRC, "");
		return icon;
	}
    public static String getCurrentUpdate(Context context) {
    	String update =null;
		final SharedPreferences preferences = context.getSharedPreferences(
				STORAGED_WEATHER_INFO, Activity.MODE_PRIVATE|Activity.MODE_APPEND);
		if(preferences!=null)
			update = preferences.getString(IMAGE_SRC_TIME, "");
		return update;
	}
	public static boolean getNavigationBarHeight(Context context){
		// for navigation bar height
        Resources res = context.getResources();//launcher.getResources();
        boolean hasNavigationBar = res.getBoolean(com.android.internal.R.bool.config_showNavigationBar);

        // if we cannot retrieve navigation configuration,
        // maybe there is an error about framework.jar. so change a way to try.
        if (!hasNavigationBar) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$bool");
                Field f = c.getField("config_showNavigationBar");
                int resid = f.getInt(null);

                hasNavigationBar = context.getResources().getBoolean(resid);

            } catch (Exception e) {
            	e.printStackTrace();
            }
        }

        // Allow a system property to override this. Used by the emulator.
        // See also hasNavigationBar().
        String navBarOverride = SystemProperties.get("qemu.hw.mainkeys");
        if (!"".equals(navBarOverride)) {
            if (navBarOverride.equals("1"))
                hasNavigationBar = false;
            else if (navBarOverride.equals("0"))
                hasNavigationBar = true;
        }
        return hasNavigationBar;
	}
	 public static boolean isNeedDownloadApp(final Context context,String packagename,String classname)
	    {
	                Intent intent = new Intent();
	                intent.setPackage(packagename);
	                intent.setClassName(packagename, classname);
	                PackageManager mPackageManager = context.getPackageManager();
	                List<ResolveInfo> list = mPackageManager.queryIntentServices(
	                        intent, 0);
	                if (list.size()>0) {
	                        return false;
	                }
	                return true;
	    }
		public static boolean getAnimaState(Context context) {
	    	boolean state = true;
			final SharedPreferences preferences = context.getSharedPreferences(
					STORAGED_WEATHER_INFO, Activity.MODE_PRIVATE|Activity.MODE_APPEND);
			if(preferences!=null)
				state = preferences.getBoolean(PREF_START_ANIMA,false);
			return state;
		}
	    public static void setAnimaState(Context context,boolean state) {
	    	Log.d("ac","setAnimaState="+state);
			final SharedPreferences preferences = context.getSharedPreferences(
					STORAGED_WEATHER_INFO, Activity.MODE_PRIVATE|Activity.MODE_APPEND);
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean(PREF_START_ANIMA, state);
			editor.commit();
		}

	    public static boolean isInDefaultTheme(Context context) {
	    	/*SharedPreferences sharedPreferences = context.getSharedPreferences("com.lenovo.launcher_preferences",
	    			Activity.MODE_APPEND | Activity.MODE_MULTI_PROCESS );
	    	String theme = sharedPreferences.getString(PREF_LETHEME, "");
	    	Log.i("TETE", "theme="+theme);
	    	if(theme!=null && theme.equals(">DEFAULT THEME"))
	    		return true;*/
	    	return true;
	    }
	    
	    public static  String getWeek(final Context context,int daynum)
		{
	    	Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		  	calendar.setTimeInMillis(System.currentTimeMillis());
		  	Date now = calendar.getTime();
		  	now.setDate(now.getDate()+daynum);
		  	calendar.setTime(now);
	        String weekStr = (String)DateUtils.formatDateTime(context, 
	        		calendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_WEEKDAY);
		  	return weekStr;

		}
	    
	    public static int getStatusHeights(Context context){
	        int statusHeight = 0;
	        if (0 == statusHeight){
	            Class<?> localClass;
	            try {
	                localClass = Class.forName("com.android.internal.R$dimen");
	                Object localObject = localClass.newInstance();
	                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
	                statusHeight = context.getResources().getDimensionPixelSize(i5);
	            } catch (ClassNotFoundException e) {
	                e.printStackTrace();
	            } catch (IllegalAccessException e) {
	                e.printStackTrace();
	            } catch (InstantiationException e) {
	                e.printStackTrace();
	            } catch (NumberFormatException e) {
	                e.printStackTrace();
	            } catch (IllegalArgumentException e) {
	                e.printStackTrace();
	            } catch (SecurityException e) {
	                e.printStackTrace();
	            } catch (NoSuchFieldException e) {
	                e.printStackTrace();
	            }catch(Exception e){
	            	 e.printStackTrace();
	            }
	        }
	        return statusHeight;
	    }
}
