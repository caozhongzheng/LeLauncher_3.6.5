package com.lenovo.lejingpin.hw.content.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.lenovo.lejingpin.hw.content.data.HwConstant;
import com.lenovo.lejingpin.hw.content.db.AppInfo;
import com.lenovo.lejingpin.hw.content.db.HwSetting;
import com.lenovo.lejingpin.hw.content.db.LocalApp;
import com.lenovo.lejingpin.hw.content.db.SpereApp;
import com.lenovo.lejingpin.hw.content.db.StoreApp;

public class HwContentProvider extends ContentProvider {
	
	private static final String TAG = "HwContentProvider";
	
	private static final String DATABASE_NAME = "lejingpin_hw_content.db";
	private static final int DATABASE_VERSION = 4;
	private static final String SPERE_TABLE_NAME = "spere_app";
	private static final String LOCAL_TABLE_NAME = "local_app";
	private static final String APPINFO_TABLE_NAME = "app_info";
	private static final String SETTING_TABLE_NAME = "hw_setting";
	private static final String APP_STORE_TABLE_NAME = "store_app";
	
	
	private static final String CREATE_SPERE_TABLE ="CREATE TABLE IF NOT EXISTS "
													+SPERE_TABLE_NAME
													+" (" 
													+SpereApp._ID
													+" INTEGER PRIMARY KEY AUTOINCREMENT,"
													+SpereApp.APP_NAME
													+" TEXT NOT NULL,"
													+SpereApp.PACKAGE_NAME
													+" TEXT NOT NULL,"
													+SpereApp.VERSION_CODE
													+" TEXT NOT NULL,"
													+SpereApp.ICON_ADDRESS
													+" TEXT NOT NULL,"
													+SpereApp.APP_COLLECT
													+" TEXT,"
													+SpereApp.APP_DELETE
													+" TEXT,"
													+SpereApp.APP_LOCAL_ID
													+" TEXT,"
													+SpereApp.APP_FAVORITES
													+" TEXT,"
													+SpereApp.APP_DOWNLOAD_COUNT
													+" TEXT,"
													+SpereApp.APP_STAR
													+" TEXT,"
													+SpereApp.EXT_COLUMN_2
													+" TEXT,"
													+SpereApp.APP_SIZE
													+" TEXT,"
													+" UNIQUE ("
													+SpereApp.PACKAGE_NAME
													+","
													+SpereApp.VERSION_CODE
													+"));";
	private static final String CREATE_LOCAL_TABLE = "CREATE TABLE IF NOT EXISTS "
													+LOCAL_TABLE_NAME
													+" ("
													+LocalApp._ID
													+" INTEGER PRIMARY KEY AUTOINCREMENT,"
													+LocalApp.PACKAGE_NAME
													+" TEXT NOT NULL,"
													+LocalApp.VERSION_CODE
													+" TEXT NOT NULL,"
													+LocalApp.APP_STATE
													+" TEXT,"
													+LocalApp.APP_DOWNLOAD_PATH
													+" TEXT,"
													+LocalApp.EXT_COLUMN_1
													+" TEXT,"
													+LocalApp.EXT_COLUMN_2
													+" TEXT,"
													+" UNIQUE ("
													+LocalApp.PACKAGE_NAME
													+","
													+LocalApp.VERSION_CODE
													+"));";
	
	private static final String CREATE_APPINFO_TABLE ="CREATE TABLE IF NOT EXISTS "
													+APPINFO_TABLE_NAME
													+" ("
													+AppInfo._ID
													+" INTEGER PRIMARY KEY AUTOINCREMENT,"
													+AppInfo.APP_NAME
													+" TEXT NOT NULL,"
													+AppInfo.PACKAGE_NAME
													+" TEXT NOT NULL,"
													+AppInfo.VERSION_CODE
													+" TEXT NOT NULL,"
													+AppInfo.APP_STAR
													+" TEXT,"
													+AppInfo.APP_PAY
													+" TEXT NOT NULL,"
													+AppInfo.APP_SIZE
													+" TEXT NOT NULL,"
													+AppInfo.APP_SNAPSHOT
													+" TEXT,"
													+AppInfo.APP_DESC
													+" TEXT,"
													+AppInfo.APP_VERSION
													+" TEXT,"
													+AppInfo.APP_PUBLISH_DATE
													+" TEXT,"
													+AppInfo.APP_COMMENT_COUNT
													+" TEXT,"
													+AppInfo.APP_DOWNLOAD_COUNT
													+" TEXT,"
													+AppInfo.EXT_COLUMN_1
													+" TEXT,"
													+AppInfo.EXT_COLUMN_2
													+" TEXT,"
													+" UNIQUE ("
													+AppInfo.PACKAGE_NAME
													+","
													+AppInfo.VERSION_CODE
													+"));";
	private static final String CREATE_SETTING_TABLE = "CREATE TABLE IF NOT EXISTS "
													+SETTING_TABLE_NAME
													+" ("
													+HwSetting.ID
													+" INTEGER PRIMARY KEY AUTOINCREMENT,"
													+HwSetting.ACTION
													+" TEXT NOT NULL,"
													+HwSetting.VALUE
													+" TEXT NOT NULL,"
													+HwSetting.EXT_COLUMN_1
													+" TEXT,"
													+HwSetting.EXT_COLUMN_2
													+" TEXT);";
	private static final String CREATE_STORE_APP_TABLE = "CREATE TABLE IF NOT EXISTS "
													+APP_STORE_TABLE_NAME
													+" ("
													+StoreApp._ID
													+" INTEGER PRIMARY KEY AUTOINCREMENT,"
													+StoreApp.APP_NAME
													+" TEXT NOT NULL,"
													+StoreApp.PACKAGE_NAME
													+" TEXT NOT NULL,"
													+StoreApp.VERSION_CODE
													+" TEXT NOT NULL,"
													+StoreApp.ICON_ADDRESS
													+" TEXT NOT NULL,"
													+StoreApp.APP_FROM
													+" TEXT,"
													+StoreApp.APP_DELETE
													+" TEXT,"
													+StoreApp.APP_LOCAL_ID
													+" TEXT,"
													+StoreApp.EXT_COLUMN_1
													+" TEXT,"
													+StoreApp.EXT_COLUMN_2
													+" TEXT,"
													+" UNIQUE ("
													+StoreApp.PACKAGE_NAME
													+","
													+StoreApp.VERSION_CODE
													+"));";
	
	private static HashMap<String,String> mSpereAppListProjectionMap;
	private static HashMap<String,String> mLocalAppListProjectionMap;
	private static HashMap<String,String> mAppInfoProjectionMap;
	private static HashMap<String,String> mSettingProjectionMap;
	private static HashMap<String,String> mStoreAppProjectionMap;
	
	private static  UriMatcher mUriMatcher;
	private DatabaseHepler mOpenHelper;
	
	private static final int SPERE = 1;
	private static final int SPERE_List = 4;
	private static final int LOCAL = 2;
	private static final int AppINFO = 3;
	private static final int SETTING = 5;
	private static final int STORE = 6;
	private static final int STORE_List = 7;
	private static final int AppInfo_List = 8;
	
	static{
		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		mUriMatcher.addURI(HwConstant.AUTHORITY, "spere", SPERE);
		mUriMatcher.addURI(HwConstant.AUTHORITY, "sperelist", SPERE_List);
		mUriMatcher.addURI(HwConstant.AUTHORITY, "local", LOCAL);
		mUriMatcher.addURI(HwConstant.AUTHORITY, "appinfo", AppINFO);
		mUriMatcher.addURI(HwConstant.AUTHORITY, "appinfolist", AppInfo_List);
		mUriMatcher.addURI(HwConstant.AUTHORITY, "setting", SETTING);
		mUriMatcher.addURI(HwConstant.AUTHORITY, "store", STORE);
		mUriMatcher.addURI(HwConstant.AUTHORITY, "storelist", STORE_List);
		
		mSpereAppListProjectionMap = new HashMap<String,String>();
		mSpereAppListProjectionMap.put(SpereApp._ID, SpereApp._ID);
		mSpereAppListProjectionMap.put(SpereApp.APP_NAME, SpereApp.APP_NAME);
		mSpereAppListProjectionMap.put(SpereApp.PACKAGE_NAME, SpereApp.PACKAGE_NAME);
		mSpereAppListProjectionMap.put(SpereApp.VERSION_CODE, SpereApp.VERSION_CODE);
		mSpereAppListProjectionMap.put(SpereApp.ICON_ADDRESS, SpereApp.ICON_ADDRESS);
		mSpereAppListProjectionMap.put(SpereApp.APP_COLLECT, SpereApp.APP_COLLECT);
		mSpereAppListProjectionMap.put(SpereApp.APP_DELETE, SpereApp.APP_DELETE);
		mSpereAppListProjectionMap.put(SpereApp.APP_LOCAL_ID, SpereApp.APP_LOCAL_ID);
		mSpereAppListProjectionMap.put(SpereApp.APP_FAVORITES, SpereApp.APP_FAVORITES);
		mSpereAppListProjectionMap.put(SpereApp.APP_DOWNLOAD_COUNT, SpereApp.APP_DOWNLOAD_COUNT);
		mSpereAppListProjectionMap.put(SpereApp.APP_STAR, SpereApp.APP_STAR);
		mSpereAppListProjectionMap.put(SpereApp.EXT_COLUMN_2, SpereApp.EXT_COLUMN_2);
		mSpereAppListProjectionMap.put(SpereApp.APP_SIZE, SpereApp.APP_SIZE);
		
		mLocalAppListProjectionMap = new HashMap<String,String>();
		mLocalAppListProjectionMap.put(LocalApp._ID, LocalApp._ID);
		mLocalAppListProjectionMap.put(LocalApp.PACKAGE_NAME, LocalApp.PACKAGE_NAME);
		mLocalAppListProjectionMap.put(LocalApp.VERSION_CODE, LocalApp.VERSION_CODE);
		mLocalAppListProjectionMap.put(LocalApp.APP_STATE, LocalApp.APP_STATE);
		mLocalAppListProjectionMap.put(LocalApp.APP_DOWNLOAD_PATH, LocalApp.APP_DOWNLOAD_PATH);
		mLocalAppListProjectionMap.put(LocalApp.EXT_COLUMN_1, LocalApp.EXT_COLUMN_1);
		mLocalAppListProjectionMap.put(LocalApp.EXT_COLUMN_2, LocalApp.EXT_COLUMN_2);
		
		mAppInfoProjectionMap = new HashMap<String,String>();
		mAppInfoProjectionMap.put(AppInfo._ID, AppInfo._ID);
		mAppInfoProjectionMap.put(AppInfo.APP_NAME, AppInfo.APP_NAME);
		mAppInfoProjectionMap.put(AppInfo.PACKAGE_NAME, AppInfo.PACKAGE_NAME);
		mAppInfoProjectionMap.put(AppInfo.VERSION_CODE, AppInfo.VERSION_CODE);
		mAppInfoProjectionMap.put(AppInfo.APP_STAR, AppInfo.APP_STAR);
		mAppInfoProjectionMap.put(AppInfo.APP_PAY, AppInfo.APP_PAY);
		mAppInfoProjectionMap.put(AppInfo.APP_SIZE, AppInfo.APP_SIZE);
		mAppInfoProjectionMap.put(AppInfo.APP_SNAPSHOT, AppInfo.APP_SNAPSHOT);
		mAppInfoProjectionMap.put(AppInfo.APP_DESC, AppInfo.APP_DESC);
		mAppInfoProjectionMap.put(AppInfo.APP_VERSION, AppInfo.APP_VERSION);
		mAppInfoProjectionMap.put(AppInfo.APP_PUBLISH_DATE, AppInfo.APP_PUBLISH_DATE);
		mAppInfoProjectionMap.put(AppInfo.APP_COMMENT_COUNT, AppInfo.APP_COMMENT_COUNT);
		mAppInfoProjectionMap.put(AppInfo.APP_DOWNLOAD_COUNT, AppInfo.APP_DOWNLOAD_COUNT);
		mAppInfoProjectionMap.put(AppInfo.EXT_COLUMN_1, AppInfo.EXT_COLUMN_1);
		mAppInfoProjectionMap.put(AppInfo.EXT_COLUMN_2, AppInfo.EXT_COLUMN_2);
		
		mSettingProjectionMap = new HashMap<String,String>();
		mSettingProjectionMap.put(HwSetting.ID, HwSetting.ID);
		mSettingProjectionMap.put(HwSetting.ACTION, HwSetting.ACTION);
		mSettingProjectionMap.put(HwSetting.VALUE, HwSetting.VALUE);
		mSettingProjectionMap.put(HwSetting.EXT_COLUMN_1, HwSetting.EXT_COLUMN_1);
		mSettingProjectionMap.put(HwSetting.EXT_COLUMN_2, HwSetting.EXT_COLUMN_2);
		
		mStoreAppProjectionMap = new HashMap<String,String>();
		mStoreAppProjectionMap.put(StoreApp._ID, StoreApp._ID);
		mStoreAppProjectionMap.put(StoreApp.APP_NAME, StoreApp.APP_NAME);
		mStoreAppProjectionMap.put(StoreApp.PACKAGE_NAME, StoreApp.PACKAGE_NAME);
		mStoreAppProjectionMap.put(StoreApp.VERSION_CODE, StoreApp.VERSION_CODE);
		mStoreAppProjectionMap.put(StoreApp.ICON_ADDRESS, StoreApp.ICON_ADDRESS);
		mStoreAppProjectionMap.put(StoreApp.APP_FROM, StoreApp.APP_FROM);
		mStoreAppProjectionMap.put(StoreApp.APP_DELETE, StoreApp.APP_DELETE);
		mStoreAppProjectionMap.put(StoreApp.APP_LOCAL_ID, StoreApp.APP_LOCAL_ID);
		mStoreAppProjectionMap.put(StoreApp.EXT_COLUMN_1, StoreApp.EXT_COLUMN_1);
		mStoreAppProjectionMap.put(StoreApp.EXT_COLUMN_2, StoreApp.EXT_COLUMN_2);
		
	}
	
	
	
	private static class DatabaseHepler extends SQLiteOpenHelper{
		
		DatabaseHepler(Context context){
			super(context,DATABASE_NAME,null,DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db){
			ContentManagerLog.d(TAG, " DatabaseHepler >> onCreate ");
			db.execSQL(CREATE_SPERE_TABLE);
			db.execSQL(CREATE_LOCAL_TABLE);
			db.execSQL(CREATE_APPINFO_TABLE);
			db.execSQL(CREATE_SETTING_TABLE);
			db.execSQL(CREATE_STORE_APP_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
			db.execSQL(CREATE_STORE_APP_TABLE);
			db.execSQL("DROP TABLE "+SPERE_TABLE_NAME);
			db.execSQL(CREATE_SPERE_TABLE);
		}

		@Override
		public void onDowngrade(SQLiteDatabase db, int oldVersion,
				int newVersion) {
			db.execSQL("DROP TABLE IF  EXISTS "+SPERE_TABLE_NAME);
			db.execSQL("DROP TABLE IF  EXISTS "+CREATE_LOCAL_TABLE);
			db.execSQL("DROP TABLE IF  EXISTS "+CREATE_APPINFO_TABLE);
			db.execSQL("DROP TABLE IF  EXISTS "+CREATE_SETTING_TABLE);
			db.execSQL("DROP TABLE IF  EXISTS "+CREATE_STORE_APP_TABLE);
			db.execSQL(CREATE_SPERE_TABLE);
			db.execSQL(CREATE_LOCAL_TABLE);
			db.execSQL(CREATE_APPINFO_TABLE);
			Log.d(TAG, "onDowngrade >> oldVersion : "+oldVersion+" ; newVersion : "+newVersion);
		}
		
		
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs){
		int count=0;
		try{
			SQLiteDatabase db = mOpenHelper.getWritableDatabase();
			switch(mUriMatcher.match(uri)){
			case SPERE:
				count = deleteSpere(db,where,whereArgs);
				break;
			case LOCAL:
				count =  deleteLocal(db,where,whereArgs);
				break;
			case AppINFO:
				count = deleteAppInfo(db,where,whereArgs);
				break;
			case STORE:
				count = deleteStore(db,where,whereArgs);
				break;
				default :
					ContentManagerLog.d(TAG, "Delete >> Unknown URI : "+uri);
					count = 0;
			}
			getContext().getContentResolver().notifyChange(uri, null);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return count;
	}
	
	private int deleteSpere(SQLiteDatabase db,String where, String[] whereArgs){
		return db.delete(SPERE_TABLE_NAME, where, whereArgs);
	}
	
	private int deleteStore(SQLiteDatabase db,String where, String[] whereArgs){
		return db.delete(APP_STORE_TABLE_NAME, where, whereArgs);
	}
	
	private int deleteLocal(SQLiteDatabase db, String where,String[] whereArgs){
		return db.delete(LOCAL_TABLE_NAME, where, whereArgs);
	}
	private int deleteAppInfo(SQLiteDatabase db,String where, String[] whereArgs){
		return db.delete(APPINFO_TABLE_NAME, where, whereArgs);
	}

	@Override
	public String getType(Uri uri) {
		switch(mUriMatcher.match(uri)){
		case SPERE:
			return SpereApp.CONTENT_SPERE_TYPE;
		case LOCAL:
			return LocalApp.CONTENT_LOCAL_TYPE;
		case AppINFO:
			return AppInfo.CONTENT_APPINFO_ITEM_TYPE;
			default :
				ContentManagerLog.d(TAG, " getType >> Unknown URI : "+uri);
				return null;
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues){
		ContentValues values;
		if(initialValues!=null){
			values = new ContentValues(initialValues);
		}else{
			values = new ContentValues();
		}
		switch(mUriMatcher.match(uri)){
		case SPERE:
			return insertSpere(values);
		case LOCAL:
			return insertLocal(values);
		case AppINFO:
			return insertAppInfo(values);
		case SETTING:
			return insertSetting(values);
		case STORE:
			return insertStore(values);
			default :
				ContentManagerLog.d(TAG, " Insert >> Failed to insert row into : "+uri);
				return null;
			
		}
	}
	
	@Override
	public int bulkInsert(Uri uri, ContentValues[] values){
		if(values!=null && values.length!=0){
			switch(mUriMatcher.match(uri)){
			case SPERE_List:
				return	insertSpereList(values);
			case STORE_List:
				return	insertStoreAppList(values);
			case AppInfo_List:
				return insertAppInfoList(values);
			}
		}
		return -1;
	}
	
	private Uri insertSpere(ContentValues values){
		try{
			if(values.containsKey(SpereApp.PACKAGE_NAME)==false || values.containsKey(SpereApp.VERSION_CODE)==false){
				return null;
			}
			try{
				SQLiteDatabase db = mOpenHelper.getWritableDatabase();
				long rowId = db.insert(SPERE_TABLE_NAME, SpereApp.PACKAGE_NAME, values);
				if(rowId > 0){
					Uri spereUri = ContentUris.withAppendedId(SpereApp.CONTENT_SPERE_URI, rowId);
					getContext().getContentResolver().notifyChange(spereUri, null);
					return spereUri;
				}
			}catch(Exception e){
				ContentManagerLog.d(TAG,"insertSpere >> Exception : "+e.toString());
			}
			
		}catch(Exception e){
			ContentManagerLog.d(TAG,"insert spere app error");
			e.printStackTrace();
		}
		return null;
	}
	
	private int insertSpereList(ContentValues[] list){
		int len = 0;
		if(list!=null && list.length!=0){
			len = list.length;
			SQLiteDatabase db = mOpenHelper.getWritableDatabase();
			db.beginTransaction();
			try{
				for(ContentValues value : list){
					String sql = getInsertSpereSql();
					String app_name = value.getAsString(SpereApp.APP_NAME);
					String package_name = value.getAsString(SpereApp.PACKAGE_NAME);
					String version_code = value.getAsString(SpereApp.VERSION_CODE);
					String icon_address = value.getAsString(SpereApp.ICON_ADDRESS);
					String app_local_id = value.getAsString(SpereApp.APP_LOCAL_ID);
					String app_delete = value.getAsString(SpereApp.APP_DELETE);
					String app_collect = value.getAsString(SpereApp.APP_COLLECT);
					String app_favorites = value.getAsString(SpereApp.APP_FAVORITES);
					String app_downlad_count = value.getAsString(SpereApp.APP_DOWNLOAD_COUNT);
					String app_star = value.getAsString(SpereApp.APP_STAR);
					String app_version = value.getAsString(SpereApp.EXT_COLUMN_2);
					String app_size = value.getAsString(SpereApp.APP_SIZE);
					db.execSQL(sql,new Object[]{app_name,package_name,version_code,icon_address,app_local_id,app_delete,app_collect,app_favorites,app_downlad_count,app_star,app_version,app_size});
				}
				db.setTransactionSuccessful();
			}catch(Exception e){
				if(db.isOpen()){
					db.endTransaction();
					db.close();
				}
				for(ContentValues values : list){
					insertSpere(values);
				}
			}
			finally{
				if(db.isOpen()){
					db.endTransaction();
					db.close();
				}
			}
		}
		return len;
	}
	
	private Uri insertStore(ContentValues values){
		try{
			if(values.containsKey(StoreApp.PACKAGE_NAME)==false || values.containsKey(StoreApp.VERSION_CODE)==false){
				return null;
			}
			try{
				SQLiteDatabase db = mOpenHelper.getWritableDatabase();
				long rowId = db.insert(APP_STORE_TABLE_NAME, StoreApp.PACKAGE_NAME, values);
				if(rowId > 0){
					Uri storeUri = ContentUris.withAppendedId(StoreApp.CONTENT_STORE_URI, rowId);
					getContext().getContentResolver().notifyChange(storeUri, null);
					return storeUri;
				}
			}catch(Exception e){
				ContentManagerLog.d(TAG,"insertStore >> Exception : "+e.toString());
			}
			
		}catch(Exception e){
			ContentManagerLog.d(TAG, " insert store app error ");
			e.printStackTrace();
		}
		return null;
	}
	private int insertStoreAppList(ContentValues[] list){
		ContentManagerLog.d(TAG,"insertStoreAppList >> list : "+Arrays.toString(list));
		int len = 0;
		if(list!=null && list.length!=0){
			len = list.length;
			ContentManagerLog.d(TAG,"insertStoreAppList >> list  >> length : "+list.length);
			SQLiteDatabase db = mOpenHelper.getWritableDatabase();
			db.beginTransaction();
			try{
				for(ContentValues value : list){
					String sql = getInsertStoreSql();
					String app_name = value.getAsString(StoreApp.APP_NAME);
					String package_name = value.getAsString(StoreApp.PACKAGE_NAME);
					String version_code = value.getAsString(StoreApp.VERSION_CODE);
					String icon_address = value.getAsString(StoreApp.ICON_ADDRESS);
					String app_from = value.getAsString(StoreApp.APP_FROM);
					String app_delete = value.getAsString(StoreApp.APP_DELETE);
					String app_local_id = value.getAsString(StoreApp.APP_LOCAL_ID);
					db.execSQL(sql,new Object[]{app_name,package_name,version_code,icon_address,app_from,app_delete,app_local_id});
				}
				db.setTransactionSuccessful();
			}catch(Exception e){
				e.printStackTrace();
				if(db.isOpen()){
					db.endTransaction();
					db.close();
				}
				for(ContentValues values : list){
					insertStore(values);
				}
			}
			finally{
				if(db.isOpen()){
					db.endTransaction();
					db.close();
				}
			}
		}
		return len;
	}
	
	private Uri insertLocal(ContentValues values){
		try{
			if(values.containsKey(LocalApp.PACKAGE_NAME)==false || values.containsKey(LocalApp.VERSION_CODE)==false){
				return null;
			}
			SQLiteDatabase db = mOpenHelper.getWritableDatabase();
			long rowId = db.insert(LOCAL_TABLE_NAME, LocalApp.PACKAGE_NAME, values);
			if(rowId > 0){
				Uri localUri = ContentUris.withAppendedId(LocalApp.CONENT_LOCAL_URI, rowId);
				getContext().getContentResolver().notifyChange(localUri, null);
				return localUri;
			}
			
		}catch(Exception e){
			ContentManagerLog.d(TAG, " insert local app error ");
			e.printStackTrace();
		}
		return null;
	}
	
	private int insertAppInfoList(ContentValues[] list){
		ContentManagerLog.d(TAG,"insertAppInfoList >> list : "+Arrays.toString(list));
		int len = 0;
		if(list!=null && list.length!=0){
			len = list.length;
			ContentManagerLog.d(TAG,"insertAppInfoList >> list  >> length : "+list.length);
			SQLiteDatabase db = mOpenHelper.getWritableDatabase();
			db.beginTransaction();
			try{
				for(ContentValues value : list){
					String sql = getInsertAppInfoSql();
					String app_name = value.getAsString(AppInfo.APP_NAME);
					String package_name = value.getAsString(AppInfo.PACKAGE_NAME);
					String version_code = value.getAsString(AppInfo.VERSION_CODE);
					String app_star = value.getAsString(AppInfo.APP_STAR);
					String app_pay = value.getAsString(AppInfo.APP_PAY);
					String app_size = value.getAsString(AppInfo.APP_SIZE);
					String app_desc = value.getAsString(AppInfo.APP_DESC);
					String app_snapshot = value.getAsString(AppInfo.APP_SNAPSHOT);
					String app_version = value.getAsString(AppInfo.APP_VERSION);
					String app_publish_date = value.getAsString(AppInfo.APP_PUBLISH_DATE);
					String app_comment_count = value.getAsString(AppInfo.APP_COMMENT_COUNT);
					String app_download_count = value.getAsString(AppInfo.APP_DOWNLOAD_COUNT);
					String ext_column_1 = value.getAsString(AppInfo.EXT_COLUMN_1);
					String ext_column_2 = value.getAsString(AppInfo.EXT_COLUMN_2);
					db.execSQL(sql,new Object[]{app_name,package_name,version_code,app_star,app_pay,app_size,app_desc,app_snapshot,app_version,app_publish_date,app_comment_count,app_download_count,ext_column_1,ext_column_2});
				}
				db.setTransactionSuccessful();
			}catch(Exception e){
				e.printStackTrace();
				if(db.isOpen()){
					db.endTransaction();
					db.close();
				}
				for(ContentValues values : list){
					insertAppInfo(values);
				}
			}
			finally{
				if(db.isOpen()){
					db.endTransaction();
					db.close();
				}
			}
		}
		return len;
	}
	
	private Uri insertAppInfo(ContentValues values){
		try{
			if(values.containsKey(AppInfo.PACKAGE_NAME)==false || values.containsKey(AppInfo.VERSION_CODE)==false){
				return null;
			}
			SQLiteDatabase db = mOpenHelper.getWritableDatabase();
			long rowId = db.insert(APPINFO_TABLE_NAME, AppInfo.PACKAGE_NAME, values);
			if(rowId > 0){
				Uri appInfoUri = ContentUris.withAppendedId(AppInfo.CONENT_APPINFO_URI, rowId);
				getContext().getContentResolver().notifyChange(appInfoUri, null);
				return appInfoUri;
			}
		}catch(Exception e){
			ContentManagerLog.d(TAG, " insert app info  error ");
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public boolean onCreate(){
		ContentManagerLog.d(TAG, "ContentProvider >> onCreate");
		mOpenHelper = new DatabaseHepler(getContext());
		insertSetting();
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		
		switch(mUriMatcher.match(uri)){
			case SPERE:
				qb.setTables(SPERE_TABLE_NAME);
				qb.setProjectionMap(mSpereAppListProjectionMap);
				break;
			case LOCAL:
				qb.setTables(LOCAL_TABLE_NAME);
				qb.setProjectionMap(mLocalAppListProjectionMap);
				break;
			case AppINFO:
				qb.setTables(APPINFO_TABLE_NAME);
				qb.setProjectionMap(mAppInfoProjectionMap);
				break;
			case SETTING:
				qb.setTables(SETTING_TABLE_NAME);
				qb.setProjectionMap(mSettingProjectionMap);
				break;
			case STORE:
				qb.setTables(APP_STORE_TABLE_NAME);
				qb.setProjectionMap(mStoreAppProjectionMap);
				break;
				default:
					ContentManagerLog.d(TAG, " Query >> Unknown URI : "+uri);
		}
		
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = null;
		try{
			c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
			ContentManagerLog.d(TAG, " Query  >> selection  : "+selection +" ; selectionArgs : "+Arrays.toString(selectionArgs)+" ; uri : "+uri);
			c.setNotificationUri(getContext().getContentResolver(), uri);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch(mUriMatcher.match(uri)){
		case SPERE:
			count = updateSpere(db,values,selection,selectionArgs);
			break;
		case LOCAL:
			count = updateLocal(db,values,selection,selectionArgs);
			break;
		case AppINFO:
			count = updateAppInfo(db,values,selection,selectionArgs);
			break;
		case SETTING:
			count = updateSetting(db,values,selection,selectionArgs);
			break;
		case STORE:
			count = updateStore(db,values,selection,selectionArgs);
			break;
			default :
				ContentManagerLog.d(TAG, " Update >> Unknown URI : "+uri);
				count = 0;
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	private int updateStore(SQLiteDatabase db,ContentValues values, String selection,
			String[] selectionArgs){
		int size = 0;
		try{
			size = db.update(APP_STORE_TABLE_NAME, values, selection, selectionArgs);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return size;
	}
	private int updateSpere(SQLiteDatabase db,ContentValues values, String selection,
			String[] selectionArgs){
		int size = 0;
		try{
			size = db.update(SPERE_TABLE_NAME, values, selection, selectionArgs);
		}catch(Exception e){
			e.printStackTrace();
		}
		return size;
	}
	private int updateLocal(SQLiteDatabase db,ContentValues values, String selection,
			String[] selectionArgs){
		int size = 0;
		try{
			size = db.update(LOCAL_TABLE_NAME, values, selection, selectionArgs);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return size;
	}
	private int updateAppInfo(SQLiteDatabase db,ContentValues values, String selection,
			String[] selectionArgs){
		int size =0;
		try{
			size = db.update(APPINFO_TABLE_NAME, values, selection, selectionArgs);
			
		}catch(Exception e){
			
		}
		return size;
	}
	
	private int updateSetting(SQLiteDatabase db,ContentValues values, String selection,
			String[] selectionArgs){
		db.beginTransaction();
		try{
			if(null!=values){
				Iterator<String>  iter = values.keySet().iterator();
				while(iter.hasNext()){
					String key = iter.next();
					String v = values.getAsString(key);
					if(!TextUtils.isEmpty(v)){
						db.execSQL(getUpdateSettingSql(key,v));
					}
				}
			}
			db.setTransactionSuccessful();
		}catch(Exception e){
			ContentManagerLog.d(TAG,"Update Setting >> Exception : "+e.toString());
		}
		finally{
			if(db.isOpen()){
				db.endTransaction();
				db.close();
			}
		}
		return -1;
	}
	
	private Uri insertSetting(ContentValues values){
		try{
			if(values.containsKey(HwSetting.ACTION)==false || values.containsKey(HwSetting.VALUE)==false){
				return null;
			}
			SQLiteDatabase db = mOpenHelper.getWritableDatabase();
			long rowId = db.insert(SETTING_TABLE_NAME, HwSetting.ACTION, values);
			if(rowId > 0){
				Uri settingUri = ContentUris.withAppendedId(HwSetting.CONENT_SETTING_URI, rowId);
				getContext().getContentResolver().notifyChange(settingUri, null);
				return settingUri;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	private void insertSetting(){
		SQLiteDatabase db = null;
		Cursor cursor =null;
		try{
			cursor = query(HwSetting.CONENT_SETTING_URI,null,null,null,null);
			if(cursor!=null && cursor.getCount()!=0){
				return ;
			}
			db = mOpenHelper.getWritableDatabase();
			db.beginTransaction();
		
			String sql = getInsertSettingSql();
			db.execSQL(sql,new Object[]{"set_3g","1"});
			db.execSQL(sql,new Object[]{"set_icon_unuse","1"});
			db.execSQL(sql,new Object[]{"set_install_app_delete_file","1"});
			db.execSQL(sql,new Object[]{"set_spere_switch","1"});
			db.setTransactionSuccessful();
		}catch(Exception e){
			ContentManagerLog.d(TAG,"Insert Setting >> Exception : "+e.toString());
		}
		finally{
			ContentManagerLog.d(TAG,"cursor : "+cursor);
			if(cursor!=null && !cursor.isClosed()){
				ContentManagerLog.d(TAG,"cursor : "+cursor.isClosed());
				cursor.close();
			}
			if(db!=null&& db.isOpen()){
				db.endTransaction();
				db.close();
			}
		}
	}
	
	private String getInsertSpereSql(){
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("INSERT INTO ");
		sqlBuilder.append(SPERE_TABLE_NAME);
		sqlBuilder.append("(");
		sqlBuilder.append(SpereApp.APP_NAME);
		sqlBuilder.append(",");
		sqlBuilder.append(SpereApp.PACKAGE_NAME);
		sqlBuilder.append(",");
		sqlBuilder.append(SpereApp.VERSION_CODE);
		sqlBuilder.append(",");
		sqlBuilder.append(SpereApp.ICON_ADDRESS);
		sqlBuilder.append(",");
		sqlBuilder.append(SpereApp.APP_LOCAL_ID);
		sqlBuilder.append(",");
		sqlBuilder.append(SpereApp.APP_DELETE);
		sqlBuilder.append(",");
		sqlBuilder.append(SpereApp.APP_COLLECT);
		sqlBuilder.append(",");
		sqlBuilder.append(SpereApp.APP_FAVORITES);
		sqlBuilder.append(",");
		sqlBuilder.append(SpereApp.APP_DOWNLOAD_COUNT);
		sqlBuilder.append(",");
		sqlBuilder.append(SpereApp.APP_STAR);
		sqlBuilder.append(",");
		sqlBuilder.append(SpereApp.EXT_COLUMN_2);
		sqlBuilder.append(",");
		sqlBuilder.append(SpereApp.APP_SIZE);
		sqlBuilder.append(") ");
		sqlBuilder.append("values (?,?,?,?,?,?,?,?,?,?,?)");
		return sqlBuilder.toString();
	}
	
	private String getInsertAppInfoSql(){
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("INSERT INTO ");
		sqlBuilder.append(APPINFO_TABLE_NAME);
		sqlBuilder.append("(");
		sqlBuilder.append(AppInfo.APP_NAME);
		sqlBuilder.append(",");
		sqlBuilder.append(AppInfo.PACKAGE_NAME);
		sqlBuilder.append(",");
		sqlBuilder.append(AppInfo.VERSION_CODE);
		sqlBuilder.append(",");
		sqlBuilder.append(AppInfo.APP_STAR);
		sqlBuilder.append(",");
		sqlBuilder.append(AppInfo.APP_PAY);
		sqlBuilder.append(",");
		sqlBuilder.append(AppInfo.APP_SIZE);
		sqlBuilder.append(",");
		sqlBuilder.append(AppInfo.APP_DESC);
		sqlBuilder.append(",");
		sqlBuilder.append(AppInfo.APP_SNAPSHOT);
		sqlBuilder.append(",");
		sqlBuilder.append(AppInfo.APP_VERSION);
		sqlBuilder.append(",");
		sqlBuilder.append(AppInfo.APP_PUBLISH_DATE);
		sqlBuilder.append(",");
		sqlBuilder.append(AppInfo.APP_COMMENT_COUNT);
		sqlBuilder.append(",");
		sqlBuilder.append(AppInfo.APP_DOWNLOAD_COUNT);
		sqlBuilder.append(",");
		sqlBuilder.append(AppInfo.EXT_COLUMN_1);
		sqlBuilder.append(",");
		sqlBuilder.append(AppInfo.EXT_COLUMN_2);
		sqlBuilder.append(") ");
		sqlBuilder.append("values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		return sqlBuilder.toString();
	}
	
	private String getInsertStoreSql(){
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("INSERT INTO ");
		sqlBuilder.append(APP_STORE_TABLE_NAME);
		sqlBuilder.append("(");
		sqlBuilder.append(StoreApp.APP_NAME);
		sqlBuilder.append(",");
		sqlBuilder.append(StoreApp.PACKAGE_NAME);
		sqlBuilder.append(",");
		sqlBuilder.append(StoreApp.VERSION_CODE);
		sqlBuilder.append(",");
		sqlBuilder.append(StoreApp.ICON_ADDRESS);
		sqlBuilder.append(",");
		sqlBuilder.append(StoreApp.APP_FROM);
		sqlBuilder.append(",");
		sqlBuilder.append(StoreApp.APP_DELETE);
		sqlBuilder.append(",");
		sqlBuilder.append(StoreApp.APP_LOCAL_ID);
		sqlBuilder.append(") ");
		sqlBuilder.append("values (?,?,?,?,?,?,?)");
		return sqlBuilder.toString();
	}
	
	private String getUpdateSettingSql(String action,String value){
		if(value!=null){
			StringBuilder build = new StringBuilder();
			build.append("UPDATE ");
			build.append(SETTING_TABLE_NAME);
			build.append(" SET value = ");
			build.append("'");
			build.append(value);
			build.append("'");
			build.append(" WHERE action= ");
			build.append("'");
			build.append(action);
			build.append("'");
			return build.toString();
		}else{
			return null;
		}
	}
	
	private String getInsertSettingSql(){
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("INSERT INTO ");
		sqlBuilder.append(SETTING_TABLE_NAME);
		sqlBuilder.append("(");
		sqlBuilder.append(HwSetting.ACTION);
		sqlBuilder.append(",");
		sqlBuilder.append(HwSetting.VALUE);
		sqlBuilder.append(") ");
		sqlBuilder.append("values (?,?)");
		return sqlBuilder.toString();
	}

}
