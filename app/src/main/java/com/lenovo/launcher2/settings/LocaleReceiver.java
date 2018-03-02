package com.lenovo.launcher2.settings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.weather.widget.settings.CityColumns;
import com.lenovo.launcher2.weather.widget.utils.Debug.R2;
import com.lenovo.launcher2.weather.widget.utils.WeatherUtilites;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Settings;
import android.util.Log;

public class LocaleReceiver extends BroadcastReceiver {
    
    private static final String TAG = "LocaleReceiver";
	private static final String DB_PATH  = "/data/data/com.lenovo.launcher/databases/";
	private static final String DB_PATH_DIR  = "/data/data/com.lenovo.launcher/databases/cities.db";
	private static final String DB_NAME  = "cities.db";
	private String where = CityColumns.COLUMN_CITY_ID +" = ?";
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        SQLiteDatabase db = null;
        Cursor cursor = null;
       try {
    	   if (Intent.ACTION_LOCALE_CHANGED.equals(action)) {
               Log.d(TAG, "receive");
               db = WeatherUtilites.openDatabase(context);
               String strCityID = Settings.System.getString(context.getContentResolver(), "cityID");
   			if(db==null || strCityID == null) {
   				if(db != null) {
   					db.close();
   					db = null;
   				}
   				return;
   			}
   			if (!tabbleIsExist("cities", db))  {
   				if(db != null) {
   					db.close();
   					db = null;
   				}
   				return;
   			}
               int lan = getLan();
               switch(lan){
               case 0:
   				cursor= db.query("cities", new String[] {
   						CityColumns.COLUMN_CITY_NAME_EN}
   						,where,//
   						new String[]{strCityID} 
   						, null, null,null,null);
               	break;
               case 1:
   				cursor= db.query("cities", new String[] {
   						CityColumns.COLUMN_CITY_NAME}
   						,where,//
   						new String[]{strCityID} 
   						, null, null,null,null);
               	break;
               case 2:
   				cursor= db.query("cities", new String[] {
   						CityColumns.COLUMN_CITY_NAME_TW}
   						,where,//
   						new String[]{strCityID} 
   						, null, null,null,null);
               	break;
               default:
               	break;
               }
               if(cursor!=null){
               	if(cursor.moveToFirst()&&cursor.getCount()>0){
                       String cityName = cursor.getString(0);
                       if (cityName == null || cityName.isEmpty())
                           return;
                       R2.echo("LocaleReceiver cityName="+cityName);
                       Settings.System.putString(context.getContentResolver(), "city_name", cityName);
               	}
               }
//               final Configuration configuration = context.getResources().getConfiguration();
//               String mLanguage = configuration.locale.getLanguage();
//               String mCountry = configuration.locale.getCountry().toLowerCase();
//               resetCityName(mLanguage, mCountry, context);
//               resetProvinceName(mLanguage,mCountry,context);
           }
		} catch (Exception e) {
			e.printStackTrace();
		}
        finally{
        	if (cursor!=null) {
				cursor.close();
			}
        	if(db != null) {
        		db.close();
        		db = null;
        	}
        }
     
    }
    
    //判断是否某个表
 	 public boolean tabbleIsExist(String tableName,SQLiteDatabase db){  
 	        boolean result = false;  
 	        if(tableName == null){  
 	              return false;  
 	        }  
 	        Cursor cursor = null;  
 	        try {  
 	                String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='"+tableName.trim()+"' ";  
 	                cursor = db.rawQuery(sql, null);  
 	                if(cursor.moveToNext()){  
 	                        int count = cursor.getInt(0);  
 	                        if(count>0){ 
 	                        	Log.d("db", "有这张表");
 	                                result = true;  
 	                        }  
 	                }  
 	                  
 	        } catch (Exception e) {  
 	        	e.printStackTrace();
 	        }                  
 	        return result;  
 	}  
    
    
	public static int getLan() {
		if(Locale.getDefault().getCountry().equals(Locale.CHINA.getCountry())) { return 1; }
		if(Locale.getDefault().getCountry().equals(Locale.TAIWAN.getCountry())) { return 2; }
		return 0;
	}
	/*public  SQLiteDatabase openDatabase(Context context){
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
					R2.echo("创建成功");//System.out.println("创建成功");
				}else{
					R2.echo("创建失败");//System.out.println("创建失败");
				};
				try {
					//得到资源
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
    public void resetCityName(String mLanguage, String mCountry, Context context){
 /*       String strCityID = Settings.System.getString(context.getContentResolver(), "cityID");
        if (strCityID == null || strCityID.isEmpty())
        {
//          strCityID = strDefCityID;
            return;
        }
        
        int cityxml;            
        if (mLanguage.equals("zh")) {
            if ("cn".equals(mCountry))
            {
                cityxml = R.xml.city;
            }
            else
            {
                cityxml = R.xml.city_tw;
            }
        }
        else
        {
            cityxml = R.xml.city_en;
        }           
                        
        XmlResourceParser x = context.getResources().getXml(cityxml);
        String cityName = "";
        try {
            int eventType = x.getEventType();
            boolean flag = false;
            String code = "";
            while (eventType != XmlPullParser.END_DOCUMENT) {

                if (eventType == XmlPullParser.START_TAG) {
                    String tag = x.getName();
                    if (tag.equals("string")) {
                        code = x.getAttributeValue(0);
                        if (code.equals(strCityID))
                        {
                            flag = true;
                        }
                    }
                } else if (eventType == XmlPullParser.TEXT) {
                    if (flag) {
                        cityName = x.getText();
                        break;
                    }
                } 
                eventType = x.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        if (cityName == null || cityName.isEmpty())
        {
//          strCityID = strDefCityID;
            return;
        }
        Settings.System.putString(context.getContentResolver(), "city_name", cityName);
  */  }
    
    public void resetProvinceName(String mLanguage, String mCountry, Context context){
        String strCityID = Settings.System.getString(context.getContentResolver(), "province_code");
        if (strCityID == null || strCityID.isEmpty())
        {
//          strCityID = strDefCityID;
            return;
        }
        
        int cityxml;            
        if (mLanguage.equals("zh")) {
            if ("cn".equals(mCountry))
            {
                cityxml = R.xml.province;
            }
            else
            {
                cityxml = R.xml.province_tw;
            }
        }
        else
        {
            cityxml = R.xml.province_en;
        }           
                        
        XmlResourceParser x = context.getResources().getXml(cityxml);
        String cityName = "";
        try {
            int eventType = x.getEventType();
            String tag = "";
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    tag = x.getName();
                    if (tag.equals("province")) {
                        String code = x.getAttributeValue(0);
                        if (code.equals(strCityID)){
                            cityName = x.getAttributeValue(1);
                        }
                    }
                } 
                /*else if (eventType == XmlPullParser.TEXT) {
                }*/
                eventType = x.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        if (cityName == null || cityName.isEmpty())
        {
//          strCityID = strDefCityID;
            return;
        }
        Settings.System.putString(context.getContentResolver(), "province_name", cityName);
    }

}
