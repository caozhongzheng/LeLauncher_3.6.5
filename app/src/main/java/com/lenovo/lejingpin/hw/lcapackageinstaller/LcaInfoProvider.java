package com.lenovo.lejingpin.hw.lcapackageinstaller;

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

public class LcaInfoProvider extends ContentProvider {
	private static final String tag = "LcaPackageInstaller";

	private static final String DATABASE_NAME = "lcainfo.db";
	private static final int DATABASE_VERSION = 1;

	private static UriMatcher URL_MATCHER;

	private DatabaseHelper mDbHelper;

	public static final class Apps implements BaseColumns {
		public static final String TAB_NAME = "lcainfo";

		//test by dining 2013-06-24 lejingpin -> xlejingpin
		public static final String CONTENT_URI_STRING = "com.lenovo.lejingpin.provider.hw.appinfonew";
		
		public static final Uri CONTENT_URI = Uri
				.parse("content://com.lenovo.lejingpin.provider.hw.appinfonew/"
						+ TAB_NAME);

		public static final String PKGNAME = "pkgname";
		public static final String APPID = "appid";
		public static final String VERSION = "version";
		public static final String APPTYPE = "apptype";
		public static final String LITEVERSION = "isliteversion";
		public static final String ISSUCCESS = "successfulinstall";

		public static final int PKGNAME_INDEX = 1;
		public static final int APPID_INDEX = 2;
		public static final int VERSION_INDEX = 3;
		public static final int APPTYPE_INDEX = 4;
		public static final int LITEVERSION_INDEX = 5;
		public static final int ISSUCCESS_INDEX = 6;
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + Apps.TAB_NAME + " ( " + Apps._ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT," + Apps.PKGNAME
					+ " TEXT UNIQUE ON CONFLICT REPLACE," + Apps.APPID
					+ " TEXT UNIQUE ON CONFLICT REPLACE," + Apps.VERSION
					+ " TEXT," + Apps.APPTYPE + " TEXT default \"other\","
					+ Apps.LITEVERSION + " TEXT default \"false\","
					+ Apps.ISSUCCESS + " TEXT default \"false\"" + ");");
			loadDefaultValue(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d(tag, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");

			db.execSQL("DROP TABLE IF EXISTS " + Apps.TAB_NAME);
			onCreate(db);
		}

		private void loadDefaultValue(SQLiteDatabase db) {
			// init database
		}
	}

	public boolean onCreate() {
		mDbHelper = new DatabaseHelper(getContext(), DATABASE_NAME, null,
				DATABASE_VERSION);

		return true;
	}

	@Override
	public int delete(Uri uri, String where, String[] selectionArgs) {
		int count = 0;
		if (URL_MATCHER.match(uri) == LCAINFO) {
			SQLiteDatabase db = mDbHelper.getWritableDatabase();
			count = db.delete(Apps.TAB_NAME, where, selectionArgs);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		return "vnd.android.cursor.dir/lcainfo";
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
		if (URL_MATCHER.match(url) == LCAINFO) {
			SQLiteDatabase db = mDbHelper.getWritableDatabase();
			long rowID = db.insert(Apps.TAB_NAME, "apptype", values);
			if (rowID > 0) {
				uri = Uri.withAppendedPath(Apps.CONTENT_URI, "" + rowID);
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
		if (URL_MATCHER.match(uri) == LCAINFO) {
			qb.setTables(Apps.TAB_NAME);
		} else {
			throw new IllegalArgumentException("Unknown URL " + uri);
		}

		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null,
				null, sortOrder);

		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] selectionArgs) {
		int count = 0;
		if (URL_MATCHER.match(uri) == LCAINFO) {
			SQLiteDatabase db = mDbHelper.getWritableDatabase();
			count = db.update(Apps.TAB_NAME, values, where, selectionArgs);
		}
		return count;
	}

	public static final int LCAINFO = 1;
	static {
		URL_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		URL_MATCHER.addURI(Apps.CONTENT_URI_STRING, Apps.TAB_NAME, LCAINFO);
	}
}
