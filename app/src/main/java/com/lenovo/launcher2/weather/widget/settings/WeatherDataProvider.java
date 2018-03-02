package com.lenovo.launcher2.weather.widget.settings;
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
public class WeatherDataProvider extends ContentProvider {
	private SQLiteOpenHelper mDBHelper;
	private static final String TAG = "WeatherDataProvider";

	public static final String DATABASE_NAME = "weather.db";
	private static final int DATABASE_VERSION = 2;

	/**
	 * Standard projection for the interesting columns of a normal weather.
	 */
	public static final int INDEX_DATA = 1;

	/**
	 * matcher definition
	 */
	private static final int WEATHER_DETAILS = 1;

	
	public WeatherDataProvider() {
	}

	@Override
	public boolean onCreate() {
		mDBHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		long rowId = -1;
		
		rowId = db.insert(WeatherDetails.WeatherDetailsColumns.TABLE_NAME, null, values);

	    if (rowId > 0) {
	        Uri noteUri = ContentUris.withAppendedId(uri, rowId);
	        getContext().getContentResolver().notifyChange(noteUri, null);
	        return noteUri;
	    }
		return null;
	}

	/**
	 * unSpprot here
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int rowCount = -1;
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		
		rowCount = db.delete(WeatherDetails.WeatherDetailsColumns.TABLE_NAME, selection, selectionArgs);
		
	        // Notify any listeners and return the deleted row count.
	   getContext().getContentResolver().notifyChange(uri, null);
	   return rowCount;

	}

	/**
	 * unSpprot here
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		int count = -1;
		
		count = db.update(WeatherDetails.WeatherDetailsColumns.TABLE_NAME, values, selection, selectionArgs);

		getContext().getContentResolver().notifyChange(uri, null);
		return count;

	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
	    SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setTables(WeatherDetails.WeatherDetailsColumns.TABLE_NAME);
	    SQLiteDatabase db = mDBHelper.getReadableDatabase();
	    Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
	    c.setNotificationUri(getContext().getContentResolver(), uri);
	    return c;
	}

	@Override
	public String getType(Uri uri) {
		
		return WeatherDetails.WeatherDetailsColumns.CONTENT_TYPE;
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		private Context mContext;

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			mContext = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + WeatherDetails.WeatherDetailsColumns.TABLE_NAME + " (" //
					+ WeatherDetails.WeatherDetailsColumns._ID + " INTEGER PRIMARY KEY,"//
					+ WeatherDetails.WeatherDetailsColumns.CITYCHY + " TEXT ," 
					+ WeatherDetails.WeatherDetailsColumns.CITYCHYL + " TEXT ," 
					+ WeatherDetails.WeatherDetailsColumns.CITYDATE + " TEXT ," 
					+ WeatherDetails.WeatherDetailsColumns.CITYDIRECTION + " TEXT ," 
					+ WeatherDetails.WeatherDetailsColumns.CITYDIRECTION1 + " TEXT ," 
					+ WeatherDetails.WeatherDetailsColumns.CITYDIRECTION2 + " TEXT ," 
					+ WeatherDetails.WeatherDetailsColumns.CITYID + " TEXT ," 
					+ WeatherDetails.WeatherDetailsColumns.CITYKTK + " TEXT ," 
					+ WeatherDetails.WeatherDetailsColumns.CITYKTKL + " TEXT ," 
					+ WeatherDetails.WeatherDetailsColumns.CITYKTKS + " TEXT ," 
					+ WeatherDetails.WeatherDetailsColumns.CITYLASTUPDATE + " TEXT ," 
					+ WeatherDetails.WeatherDetailsColumns.CITYNAME + " TEXT ," 
					+ WeatherDetails.WeatherDetailsColumns.CITYPOLLUTION + " TEXT ," 
					+ WeatherDetails.WeatherDetailsColumns.CITYPOLLUTIONL + " TEXT ," 
					+ WeatherDetails.WeatherDetailsColumns.CITYPOLLUTIONS + " TEXT ," 
					+ WeatherDetails.WeatherDetailsColumns.CITYPOWER + " TEXT ," 
					+ WeatherDetails.WeatherDetailsColumns.CITYSTATUS + " TEXT ," 
					+ WeatherDetails.WeatherDetailsColumns.CITYSTATUS1 + " TEXT ," 
					+ WeatherDetails.WeatherDetailsColumns.CITYSTATUS2 + " TEXT ," 
					+ WeatherDetails.WeatherDetailsColumns.CITYTEMPERATURE + " TEXT ," 
					+ WeatherDetails.WeatherDetailsColumns.CITYTEMPERATURE1 + " TEXT ," 
					+ WeatherDetails.WeatherDetailsColumns.CITYTEMPERATURE2 + " TEXT ," 
					+ WeatherDetails.WeatherDetailsColumns.CITYXCZ + " TEXT ," 
					+ WeatherDetails.WeatherDetailsColumns.CITYXCZL + " TEXT ," 
					+ WeatherDetails.WeatherDetailsColumns.CITYXCZS + " TEXT ," 
					+ WeatherDetails.WeatherDetailsColumns.CITYZWX + " TEXT ," 
					+ WeatherDetails.WeatherDetailsColumns.CITYZWXL + " TEXT ," 
					+ WeatherDetails.WeatherDetailsColumns.CITYZWXS + " TEXT "
					+ ");");
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + WeatherDetails.WeatherDetailsColumns.TABLE_NAME);
			onCreate(db);
		}
	}

}
