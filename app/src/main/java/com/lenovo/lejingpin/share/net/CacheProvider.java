package com.lenovo.lejingpin.share.net;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

public class CacheProvider extends ContentProvider {
	private static final String TAG = "zdx";

	private static final String DATABASE_NAME = "cache.db";
	private static final int DATABASE_VERSION = 1;

	private static UriMatcher url_mathcer;

	private DatabaseHelper mDbHelper;

	public static final class CacheFiles implements BaseColumns {
		public static final String TAB_NAME = "cache";

		public static final String CONTENT_URI_STRING = "com.lenovo.provider.appstore.cache";
		public static final Uri CONTENT_URI = Uri
				.parse("content://com.lenovo.provider.appstore.cache/"
						+ TAB_NAME);

		public static final String URL = "url";
		public static final String POSTDATA = "postdata";
		public static final String FILENAME = "filename";
		public static final String LASTACCESSTIME = "lastaccesstime";
		public static final String RESERVED = "reserved";

		public static final int URL_INDEX = 1;
		public static final int POSTDATA_INDEX = 2;
		public static final int FILENAME_INDEX = 3;
		public static final int LASTACCESSTIME_INDEX = 4;
		public static final int RESERVED_INDEX = 5;
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + CacheFiles.TAB_NAME + " ( "
					+ CacheFiles._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ CacheFiles.URL + " TEXT UNIQUE ON CONFLICT REPLACE,"
					+ CacheFiles.POSTDATA + " TEXT," + CacheFiles.FILENAME
					+ " TEXT," + CacheFiles.LASTACCESSTIME + " TEXT,"
					+ CacheFiles.RESERVED + " TEXT,"
					+ "CONSTRAINT UNKEY UNIQUE(" + CacheFiles.URL + ")" + ");");
			// loadDefaultValue(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");

			db.execSQL("DROP TABLE IF EXISTS " + CacheFiles.TAB_NAME);
			onCreate(db);
		}

		// private void loadDefaultValue(SQLiteDatabase db) {
		// // init database
		// }
	}

	public boolean onCreate() {
		mDbHelper = new DatabaseHelper(getContext(), DATABASE_NAME, null,
				DATABASE_VERSION);
		return true;
	}

	@Override
	public int delete(Uri uri, String where, String[] selectionArgs) {
		int count = 0;
		if (url_mathcer.match(uri) == CACHE) {
			SQLiteDatabase db = mDbHelper.getWritableDatabase();
			count = db.delete(CacheFiles.TAB_NAME, where, selectionArgs);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		return "vnd.android.cursor.dir/cache";
	}

	@Override
	public Uri insert(Uri url, ContentValues initialValues) {
		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}
		Uri uri = null;
		if (url_mathcer.match(url) == CACHE) {
			SQLiteDatabase db = mDbHelper.getWritableDatabase();
			long rowID = db.insert(CacheFiles.TAB_NAME, "filename", values);
			if (rowID > 0) {
				uri = Uri.withAppendedPath(CacheFiles.CONTENT_URI, "" + rowID);
			}
		} else {
			throw new IllegalArgumentException("Unknown URL");
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return uri;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		if (url_mathcer.match(uri) == CACHE) {
			qb.setTables(CacheFiles.TAB_NAME);
		} else {
			throw new IllegalArgumentException("Unknown URL " + uri);
		}
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		Cursor cursor = qb.query(db, projection, selection, selectionArgs,
				null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] selectionArgs) {
		int count = 0;
		if (url_mathcer.match(uri) == CACHE) {
			SQLiteDatabase db = mDbHelper.getWritableDatabase();
			count = db
					.update(CacheFiles.TAB_NAME, values, where, selectionArgs);
		}
		return count;
	}

	public static final int CACHE = 1;
	static {
		url_mathcer = new UriMatcher(UriMatcher.NO_MATCH);
		url_mathcer.addURI(CacheFiles.CONTENT_URI_STRING, CacheFiles.TAB_NAME,
				CACHE);
	}

}
