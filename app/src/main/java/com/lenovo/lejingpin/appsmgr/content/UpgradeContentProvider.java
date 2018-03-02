package com.lenovo.lejingpin.appsmgr.content;

import java.util.Arrays;
import java.util.HashMap;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class UpgradeContentProvider extends ContentProvider {

	private static final String TAG = "xujing3";
	private static final String DATABASE_NAME = "local_app_upgrade.db";
	private static final int DATABASE_VERSION = 3;
	private static final String UPGRADEAPP_TABLE_NAME = "upgrade_app";

	private static final String CREATE_UPGRADEAPP_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ UPGRADEAPP_TABLE_NAME
			+ " ("
			+ UpgradeLocalApp._ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ UpgradeLocalApp.PACKAGE_NAME
			+ " TEXT NOT NULL,"
			+ UpgradeLocalApp.VERSION_CODE
			+ " TEXT NOT NULL,"
			+ UpgradeLocalApp.APP_NAME
			+ " TEXT,"
			+ UpgradeLocalApp.ICON_ADDRESS
			+ " TEXT,"
			+ UpgradeLocalApp.APP_CATEGORY
			+ " TEXT,"
			+ UpgradeLocalApp.APP_SIZE
			+ " TEXT,"
			+ UpgradeLocalApp.APP_STAR
			+ " TEXT,"
			+ UpgradeLocalApp.APP_PAY
			+ " TEXT,"
			+ UpgradeLocalApp.VERSION_NAME
			+ " TEXT,"
			+ UpgradeLocalApp.APP_ISPAY
			+ " TEXT,"
			+ UpgradeLocalApp.APP_DESC
			+ " TEXT,"
			+ UpgradeLocalApp.APP_SNAPSHOT
			+ " TEXT,"
			+ UpgradeLocalApp.APP_PUBLISH_NAME
			+ " TEXT,"
			+ UpgradeLocalApp.APP_PUBLISH_DATE
			+ " TEXT,"
			+ UpgradeLocalApp.APP_COMMENT_COUNT
			+ " TEXT,"
			+ UpgradeLocalApp.APP_DOWNLAOD_COUNT
			+ " TEXT,"
			+ UpgradeLocalApp.APP_DELETE
			+ " TEXT,"
			+ UpgradeLocalApp.EXT_COLUMN_1
			+ " TEXT,"
			+ UpgradeLocalApp.EXT_COLUMN_2
			+ " TEXT,"
			+ UpgradeLocalApp.APP_UPDATE_IGNORE
			+ " INTEGER,"
			+ " UNIQUE ("
			+ UpgradeLocalApp.PACKAGE_NAME
			+ ","
			+ UpgradeLocalApp.VERSION_CODE
			+ "));";

	private static HashMap<String, String> mUpgradeAppListProjectionMap;
	private static UriMatcher mUriMatcher;
	private DatabaseHepler mOpenHelper;
	private static final int UPGRADE_APP = 1;
	private static final int UPGRADE_APP_List = 2;

	static {
		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		mUriMatcher.addURI(UpgradeUtil.AUTHORITY, "upgradeapp", UPGRADE_APP);
		mUriMatcher.addURI(UpgradeUtil.AUTHORITY, "upgradeapplist",
				UPGRADE_APP_List);
		mUpgradeAppListProjectionMap = new HashMap<String, String>();
		mUpgradeAppListProjectionMap.put(UpgradeLocalApp._ID,
				UpgradeLocalApp._ID);
		mUpgradeAppListProjectionMap.put(UpgradeLocalApp.PACKAGE_NAME,
				UpgradeLocalApp.PACKAGE_NAME);
		mUpgradeAppListProjectionMap.put(UpgradeLocalApp.VERSION_CODE,
				UpgradeLocalApp.VERSION_CODE);
		mUpgradeAppListProjectionMap.put(UpgradeLocalApp.APP_NAME,
				UpgradeLocalApp.APP_NAME);
		mUpgradeAppListProjectionMap.put(UpgradeLocalApp.ICON_ADDRESS,
				UpgradeLocalApp.ICON_ADDRESS);
		mUpgradeAppListProjectionMap.put(UpgradeLocalApp.APP_CATEGORY,
				UpgradeLocalApp.APP_CATEGORY);
		mUpgradeAppListProjectionMap.put(UpgradeLocalApp.APP_SIZE,
				UpgradeLocalApp.APP_SIZE);
		mUpgradeAppListProjectionMap.put(UpgradeLocalApp.APP_STAR,
				UpgradeLocalApp.APP_STAR);
		mUpgradeAppListProjectionMap.put(UpgradeLocalApp.APP_PAY,
				UpgradeLocalApp.APP_PAY);
		mUpgradeAppListProjectionMap.put(UpgradeLocalApp.VERSION_NAME,
				UpgradeLocalApp.VERSION_NAME);
		mUpgradeAppListProjectionMap.put(UpgradeLocalApp.APP_ISPAY,
				UpgradeLocalApp.APP_ISPAY);
		mUpgradeAppListProjectionMap.put(UpgradeLocalApp.APP_DESC,
				UpgradeLocalApp.APP_DESC);
		mUpgradeAppListProjectionMap.put(UpgradeLocalApp.APP_SNAPSHOT,
				UpgradeLocalApp.APP_SNAPSHOT);
		mUpgradeAppListProjectionMap.put(UpgradeLocalApp.APP_PUBLISH_NAME,
				UpgradeLocalApp.APP_PUBLISH_NAME);
		mUpgradeAppListProjectionMap.put(UpgradeLocalApp.APP_PUBLISH_DATE,
				UpgradeLocalApp.APP_PUBLISH_DATE);
		mUpgradeAppListProjectionMap.put(UpgradeLocalApp.APP_COMMENT_COUNT,
				UpgradeLocalApp.APP_COMMENT_COUNT);
		mUpgradeAppListProjectionMap.put(UpgradeLocalApp.APP_DOWNLAOD_COUNT,
				UpgradeLocalApp.APP_DOWNLAOD_COUNT);
		mUpgradeAppListProjectionMap.put(UpgradeLocalApp.APP_DELETE,
				UpgradeLocalApp.APP_DELETE);
		mUpgradeAppListProjectionMap.put(UpgradeLocalApp.EXT_COLUMN_1,
				UpgradeLocalApp.EXT_COLUMN_1);
		mUpgradeAppListProjectionMap.put(UpgradeLocalApp.EXT_COLUMN_2,
				UpgradeLocalApp.EXT_COLUMN_2);
		mUpgradeAppListProjectionMap.put(UpgradeLocalApp.APP_UPDATE_IGNORE,
				UpgradeLocalApp.APP_UPDATE_IGNORE);
	}

	private static class DatabaseHepler extends SQLiteOpenHelper{

		DatabaseHepler(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i(TAG,
					"^^^^^^^^^^^^^^^^^^^^^^^^^  UpgradeContentProvider.DatabaseHelper.onCreate() ");
			try {
				// Log.i("zdx","sql:"+ CREATE_UPGRADEAPP_TABLE);
				db.execSQL(CREATE_UPGRADEAPP_TABLE);
			} catch (Exception e) {
				Log.i(TAG,
						"^^^^^^^^^^^^^^^^^^^^^^^^^^^db.execSQL(CREATE_UPGRADEAPP_TABLE) fail.");
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.i(TAG,
					"^^^^^^^^^^^^^^^^^^^^^^^^^  UpgradeContentProvider.DatabaseHelper.onUpgrade() ");
			db.execSQL("DROP TABLE IF EXISTS upgrade_app");
			onCreate(db);
		}
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		int count = 0;
		try{
			SQLiteDatabase db = mOpenHelper.getWritableDatabase();
			switch (mUriMatcher.match(uri)) {
			case UPGRADE_APP:
				count = deleteUpgradeApp(db, where, whereArgs);
				break;
			default:
				Log.i(TAG, "UpgradeContentProvider.Delete >> Unknown URI : " + uri);
				count = 0;
			}
			getContext().getContentResolver().notifyChange(uri, null);

		}catch(SQLiteException ex){
				
		}
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (mUriMatcher.match(uri)) {
		case UPGRADE_APP:
			return UpgradeLocalApp.CONTENT_UPGRADE_APP_TYPE;
		default:
			Log.i(TAG, " UpgradeContentProvider.getType >> Unknown URI : "
					+ uri);
			return null;
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}
		switch (mUriMatcher.match(uri)) {
		case UPGRADE_APP:
			return insertUpgradeApp(values);
		default:
			Log.i(TAG,
					" UpgradeContentProvider.Insert >> Failed to insert row into : "
							+ uri);
			return null;

		}
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		if (values != null && values.length != 0) {
			switch (mUriMatcher.match(uri)) {
			case UPGRADE_APP_List:
				return insertUpgradeAppList(values);
			}
		}
		return -1;
	}

	@Override
	public boolean onCreate() {
		// Log.i(TAG, "UpgradeContentProvider.onCreate(), new DatabaseHepler");
		mOpenHelper = new DatabaseHepler(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		 Log.i(TAG," UpgradeContentProvider.query()");
		switch (mUriMatcher.match(uri)) {
		case UPGRADE_APP:
			Log.i(TAG, "upgradecontentprovider matched--------------UPGRADE_APP---- ");
			qb.setTables(UPGRADEAPP_TABLE_NAME);
			qb.setProjectionMap(mUpgradeAppListProjectionMap);
			break;
		default:
			Log.i(TAG, "  .query >> Unknown URI : " + uri);
		}

		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null,
				null, sortOrder);
		 Log.i(TAG,
		 " UpgradeContentProvider.query  >> selection  : "+selection
		 +" , selectionArgs : "+Arrays.toString(selectionArgs)+" , uri : "+uri);
		c.setNotificationUri(getContext().getContentResolver(), uri); 
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = null;
		try{
			db = mOpenHelper.getWritableDatabase();
		}catch(SQLiteException ex){
			return 0;
		}
		
//		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (mUriMatcher.match(uri)) {
		case UPGRADE_APP:
			count = updateUpgradeApp(db, values, selection, selectionArgs);
			break;
		default:
			Log.i(TAG, " UpgradeContentProvider.update >> Unknown URI : " + uri);
			count = 0;
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	private Uri insertUpgradeApp(ContentValues values) {
		if (!values.containsKey(UpgradeLocalApp.PACKAGE_NAME)
				|| !values.containsKey(UpgradeLocalApp.VERSION_CODE)) {
			return null;
		}
		// Log.i("zdx","UpgradeContentProvider.insertUpgradeApp()");
		try {
			SQLiteDatabase db = null;
			try{
				db = mOpenHelper.getWritableDatabase();
			}catch(SQLiteException ex){
				return null;
			}
//			SQLiteDatabase db = mOpenHelper.getWritableDatabase();
			long rowId = db.insert(UPGRADEAPP_TABLE_NAME,
					UpgradeLocalApp.PACKAGE_NAME, values);
			if (rowId > 0) {
				Uri upgradeUri = ContentUris.withAppendedId(
						UpgradeLocalApp.CONENT_UPGRADE_APP_URI, rowId);
				getContext().getContentResolver()
						.notifyChange(upgradeUri, null);
				return upgradeUri;
			}
		} catch (Exception e) {
			Log.i(TAG,
					"UpgradeContentProvider.insertUpgradeApp >> Exception : "
							+ e.toString());
		}
		return null;
	}

	private int deleteUpgradeApp(SQLiteDatabase db, String where,
			String[] whereArgs) {
		Log.i(TAG,
				"***************UpgradeContentProvider.deleteUpgradeApp, where:"
						+ where);
		return db.delete(UPGRADEAPP_TABLE_NAME, where, whereArgs);
	}

	private int updateUpgradeApp(SQLiteDatabase db, ContentValues values,
			String selection, String[] selectionArgs) {
		return db.update(UPGRADEAPP_TABLE_NAME, values, selection,
				selectionArgs);
	}

	private int insertUpgradeAppList(ContentValues[] list) {
		// Log.i(TAG,"UpgradeContentProvider.insertUpgradeAppList >> list : "+list);
		int len = 0;
		if (list != null && list.length != 0) {
			len = list.length;
			Log.i(TAG,
					"UpgradeContentProvider.insertUpgradeAppList >> list  >> length : "
							+ list.length);
			SQLiteDatabase db = null;
			try{
				db = mOpenHelper.getWritableDatabase();
			}catch(SQLiteException ex){
				return 0;
			}
//			SQLiteDatabase db = mOpenHelper.getWritableDatabase();
			db.beginTransaction();
			try {
				for (ContentValues value : list) {
					String sql = getInsertUpgradeAppListSql();
					String package_name = value
							.getAsString(UpgradeLocalApp.PACKAGE_NAME);
					String version_code = value
							.getAsString(UpgradeLocalApp.VERSION_CODE);
					String app_name = value
							.getAsString(UpgradeLocalApp.APP_NAME);
					String icon_address = value
							.getAsString(UpgradeLocalApp.ICON_ADDRESS);
					String app_category = value
							.getAsString(UpgradeLocalApp.APP_CATEGORY);
					String app_size = value
							.getAsString(UpgradeLocalApp.APP_SIZE);
					String app_star = value
							.getAsString(UpgradeLocalApp.APP_STAR);
					String app_pay = value.getAsString(UpgradeLocalApp.APP_PAY);
					String app_version = value
							.getAsString(UpgradeLocalApp.VERSION_NAME);
					// Log.i("zdx","------  "+package_name+","+
					// version_code+","+ app_name +","+ icon_address+","+
					// app_category+","+ app_size+", "+ app_star +","+
					// app_version);

					db.execSQL(sql, new Object[] { package_name, version_code,
							app_name, icon_address, app_category, app_size,
							app_star, app_pay, app_version });
				}
				db.setTransactionSuccessful();
			} catch (Exception e) {
				if (db.isOpen()) {
					db.endTransaction();
					db.close();
				}
				Log.i(TAG,
						"_____________insertUpgradeAppList_________Exception____________________________________");
				for (ContentValues values : list) {
					insertUpgradeApp(values);
				}
			} finally {
				if (db.isOpen()) {
					db.endTransaction();
					db.close();
				}
			}
		}
		return len;
	}

	private String getInsertUpgradeAppListSql() {
		// Log.i("zdx","UpgradeContentProvier.getInsertUpgradeAppListSql()");
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("INSERT INTO ");
		sqlBuilder.append(UPGRADEAPP_TABLE_NAME);
		sqlBuilder.append("(");
		sqlBuilder.append(UpgradeLocalApp.PACKAGE_NAME);
		sqlBuilder.append(",");
		sqlBuilder.append(UpgradeLocalApp.VERSION_CODE);
		sqlBuilder.append(",");
		sqlBuilder.append(UpgradeLocalApp.APP_NAME);
		sqlBuilder.append(",");
		sqlBuilder.append(UpgradeLocalApp.ICON_ADDRESS);
		sqlBuilder.append(",");
		sqlBuilder.append(UpgradeLocalApp.APP_CATEGORY);
		sqlBuilder.append(",");
		sqlBuilder.append(UpgradeLocalApp.APP_SIZE);
		sqlBuilder.append(",");
		sqlBuilder.append(UpgradeLocalApp.APP_STAR);
		sqlBuilder.append(",");
		sqlBuilder.append(UpgradeLocalApp.APP_PAY);
		sqlBuilder.append(",");
		sqlBuilder.append(UpgradeLocalApp.VERSION_NAME);
		sqlBuilder.append(") ");
		sqlBuilder.append("values (?,?,?,?,?,?,?,?,?)");
		return sqlBuilder.toString();
	}
}
