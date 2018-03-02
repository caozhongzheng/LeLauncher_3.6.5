package com.lenovo.lejingpin.share.download;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.CrossProcessCursor;
import android.database.Cursor;
import android.database.CursorWindow;
import android.database.CursorWrapper;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

/**
 * Allows application to interact with the download manager.
 */
public final class DownloadProvider extends ContentProvider {

	/** Database filename */
	private static final String DB_NAME = "sharedownloads.db";
	/** Current database version */
	private static final int DB_VERSION = 100;
	/** Database version from which upgrading is a nop */
	private static final int DB_VERSION_NOP_UPGRADE_FROM = 31;
	/** Database version to which upgrading is a nop */
	private static final int DB_VERSION_NOP_UPGRADE_TO = 100;
	/** Name of table in the database */
	private static final String DB_TABLE = "downloads";

	/** MIME type for the entire download list */
	private static final String DOWNLOAD_LIST_TYPE = "vnd.android.cursor.dir/download";
	/** MIME type for an individual download */
	private static final String DOWNLOAD_TYPE = "vnd.android.cursor.item/download";
	
	private static final String AUTHORITY = "com.lenovo.lejingpin.share.download";

	/** URI matcher used to recognize URIs sent by applications */
	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	/** URI matcher constant for the URI of the entire download list */
	private static final int DOWNLOADS = 1;
	/** URI matcher constant for the URI of an individual download */
	private static final int DOWNLOADS_ID = 2;

	private static final int DOWNLOADS_VIEWCUSTOM = 3;

	private static final String TAG = "xujing3";

	static {
		sURIMatcher.addURI(AUTHORITY, "download",
				DOWNLOADS);
		sURIMatcher.addURI(AUTHORITY, "download/#",
				DOWNLOADS_ID);
		sURIMatcher.addURI(AUTHORITY, "viewcustom",
				DOWNLOADS_VIEWCUSTOM);
	}

	private static final String[] sAppReadableColumnsArray = new String[] {
			Downloads.Impl._ID, Downloads.Impl.COLUMN_APP_DATA,
			Downloads.Impl._DATA, Downloads.Impl.COLUMN_MIME_TYPE,
			Downloads.Impl.COLUMN_VISIBILITY,
			Downloads.Impl.COLUMN_DESTINATION, Downloads.Impl.COLUMN_CONTROL,
			Downloads.Impl.COLUMN_STATUS,
			Downloads.Impl.COLUMN_LAST_MODIFICATION,
			Downloads.Impl.COLUMN_NOTIFICATION_PACKAGE,
			Downloads.Impl.COLUMN_NOTIFICATION_CLASS,
			Downloads.Impl.COLUMN_TOTAL_BYTES,
			Downloads.Impl.COLUMN_CURRENT_BYTES, Constants.UID,
			Downloads.Impl.COLUMN_TITLE, Downloads.Impl.COLUMN_DESCRIPTION,
			Downloads.Impl.COLUMN_PKGNAME, Downloads.Impl.COLUMN_VERSIONCODE,
			Downloads.Impl.COLUMN_VERSIONNAME, Downloads.Impl.COLUMN_APPNAME,
			Downloads.Impl.COLUMN_APPSIZE, Downloads.Impl.COLUMN_ICONADDR,
			Downloads.Impl.COLUMN_WIFISTATUS,
			Downloads.Impl.COLUMN_HANDTOPAUSE,
			// zdx modify
			Downloads.Impl.COLUMN_CATEGORY, Downloads.Impl.COLUMN_EXT_1,
			Downloads.Impl.COLUMN_EXT_2 };

	private static HashSet<String> sAppReadableColumnsSet;
	static {
		sAppReadableColumnsSet = new HashSet<String>();
		for (int i = 0; i < sAppReadableColumnsArray.length; ++i) {
			sAppReadableColumnsSet.add(sAppReadableColumnsArray[i]);
		}
	}

	/** The database that lies underneath this content provider */
	private SQLiteOpenHelper mOpenHelper = null;

	/** List of uids that can access the downloads */
	/*
	 * private int mSystemUid = -1; private int mDefContainerUid = -1;
	 */

	/**
	 * Creates and updated database on demand when opening it. Helper class to
	 * create database the first time the provider is initialized and upgrade it
	 * when a new version of the provider needs an updated version of the
	 * database.
	 */
	private final class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(final Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		/**
		 * Creates database the first time we try to open it.
		 */
		@Override
		public void onCreate(final SQLiteDatabase db) {
			createTable(db);
		}

		/**
		 * Updates the database format when a content provider is used with a
		 * database that was created with a different format.
		 */
		// Note: technically, this could also be a downgrade, so if we want
		// to gracefully handle upgrades we should be careful about
		// what to do on downgrades.
		@Override
		public void onUpgrade(final SQLiteDatabase db, int oldV, final int newV) {
			if (oldV == DB_VERSION_NOP_UPGRADE_FROM) {
				if (newV == DB_VERSION_NOP_UPGRADE_TO) { // that's a no-op
															// upgrade.
					return;
				}
				// NOP_FROM and NOP_TO are identical, just in different
				// codelines. Upgrading
				// from NOP_FROM is the same as upgrading from NOP_TO.
				oldV = DB_VERSION_NOP_UPGRADE_TO;
			}
			Log.i(Constants.TAG, "Upgrading downloads database from version "
					+ oldV + " to " + newV
					+ ", which will destroy all old data");
			dropTable(db);
			createTable(db);
		}
	}

	/**
	 * Initializes the content provider when it is created.
	 */
	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		return true;
	}

	/**
	 * Returns the content-provider-style MIME types of the various types
	 * accessible through this content provider.
	 */
	@Override
	public String getType(final Uri uri) {
		int match = sURIMatcher.match(uri);
		String type = null;
		switch (match) {
		case DOWNLOADS_VIEWCUSTOM:
		case DOWNLOADS: {
			type = DOWNLOAD_LIST_TYPE;
			break;
		}
		case DOWNLOADS_ID: {
			type = DOWNLOAD_TYPE;
			break;
		}
		default: {
			Log.i(TAG, "Unknown URI: " + uri);
			break;
		}
		}
		return type;
	}

	/**
	 * Creates the table that'll hold the download information.
	 */
	private void createTable(SQLiteDatabase db) {
		try {
			db.execSQL("CREATE TABLE " + DB_TABLE + "(" + Downloads.Impl._ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ Downloads.Impl.COLUMN_PKGNAME + " TEXT, "
					+ Downloads.Impl.COLUMN_VERSIONCODE + " TEXT, "
					+ Downloads.Impl.COLUMN_VERSIONNAME + " TEXT, "
					+ Downloads.Impl.COLUMN_APPNAME + " TEXT, "
					+ Downloads.Impl.COLUMN_APPSIZE + " TEXT, "
					+ Downloads.Impl.COLUMN_ICONADDR + " TEXT, "
					+ Downloads.Impl.COLUMN_WIFISTATUS + " TEXT, "
					+ Downloads.Impl.COLUMN_HANDTOPAUSE + " TEXT, "
					+ Downloads.Impl.COLUMN_URI + " TEXT, "
					+ Constants.RETRY_AFTER_X_REDIRECT_COUNT + " INTEGER, "
					+ Downloads.Impl.COLUMN_APP_DATA + " TEXT, "
					+ Downloads.Impl.COLUMN_NO_INTEGRITY + " BOOLEAN, "
					+ Downloads.Impl.COLUMN_FILE_NAME_HINT + " TEXT, "
					+ Constants.OTA_UPDATE + " BOOLEAN, "
					+ Downloads.Impl._DATA + " TEXT, "
					+ Downloads.Impl.COLUMN_MIME_TYPE + " TEXT, "
					+ Downloads.Impl.COLUMN_DESTINATION + " INTEGER, "
					+ Constants.NO_SYSTEM_FILES + " BOOLEAN, "
					+ Downloads.Impl.COLUMN_VISIBILITY + " INTEGER, "
					+ Downloads.Impl.COLUMN_CONTROL + " INTEGER, "
					+ Downloads.Impl.COLUMN_STATUS + " INTEGER, "
					+ Constants.FAILED_CONNECTIONS + " INTEGER, "
					+ Downloads.Impl.COLUMN_LAST_MODIFICATION + " BIGINT, "
					+ Downloads.Impl.COLUMN_NOTIFICATION_PACKAGE + " TEXT, "
					+ Downloads.Impl.COLUMN_NOTIFICATION_CLASS + " TEXT, "
					+ Downloads.Impl.COLUMN_NOTIFICATION_EXTRAS + " TEXT, "
					+ Downloads.Impl.COLUMN_COOKIE_DATA + " TEXT, "
					+ Downloads.Impl.COLUMN_USER_AGENT + " TEXT, "
					+ Downloads.Impl.COLUMN_REFERER + " TEXT, "
					+ Downloads.Impl.COLUMN_TOTAL_BYTES + " INTEGER, "
					+ Downloads.Impl.COLUMN_CURRENT_BYTES + " INTEGER, "
					+ Constants.ETAG + " TEXT, " + Constants.UID + " INTEGER, "
					+ Downloads.Impl.COLUMN_OTHER_UID + " INTEGER, "
					+ Downloads.Impl.COLUMN_TITLE
					+ " TEXT, "
					+ Downloads.Impl.COLUMN_DESCRIPTION
					+ " TEXT, "
					// zdx modify
					+ Downloads.Impl.COLUMN_CATEGORY + " INTEGER, "
					+ Downloads.Impl.COLUMN_EXT_1 + " TEXT, "
					+ Downloads.Impl.COLUMN_EXT_2 + " TEXT, "
					+ Constants.MEDIA_SCANNED + " BOOLEAN);");
		} catch (SQLException ex) {
			Log.e(Constants.TAG, "couldn't create table in downloads database");
			throw ex;
		}
	}

	/**
	 * Deletes the table that holds the download information.
	 */
	private void dropTable(SQLiteDatabase db) {
		try {
			db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
		} catch (SQLException ex) {
			Log.e(Constants.TAG, "couldn't drop table in downloads database");
			throw ex;
		}
	}

	/**
	 * Inserts a row in the database
	 */
	@Override
	public Uri insert(final Uri uri, final ContentValues values) {
		SQLiteDatabase db = null;
		try{
			db = mOpenHelper.getWritableDatabase();
		}catch(SQLiteException ex){
			return null;
		}
//		SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		if (sURIMatcher.match(uri) != DOWNLOADS) {
			Log.e(Constants.TAG,
					"DownloadPRovider.insert, an unknown/invalid URI: " + uri);
			return null;
		}
		Log.e(Constants.TAG, "DownloadPRovider.insert, uri: " + uri + ",pkg:"
				+ values.get(Downloads.Impl.COLUMN_PKGNAME).toString()
				+ ",version:"
				+ values.get(Downloads.Impl.COLUMN_VERSIONCODE).toString());

		// //test
		// ContentValues value = new ContentValues();
		// value.put(Downloads.Impl.COLUMN_PKGNAME, "test.pacakgename");
		// value.put(Downloads.Impl.COLUMN_VERSIONCODE, "11");
		long rowID = db.insert(DB_TABLE, null, /* value */values);
		Uri ret = null;
		if (rowID != -1) {
			Log.i(TAG,
					"********************DownloadProvider.insert(), insert one download to db, start DownloadService");
			Context context = getContext();
			context.startService(new Intent(context, DownloadService.class));

			ret = Uri.parse(Downloads.Impl.CONTENT_URI + "/" + rowID);
			context.getContentResolver().notifyChange(uri, null);
			// -------philn----------
			// updateDownloadStatus(uri);
		} else {
			Log.e(Constants.TAG, "DownloadPRovider.insert, error !");
		}
//		DownloadInfoContainer.add(rowID, values);

		return ret;
	}

	/**
	 * Starts a database query
	 */
	@Override
	public Cursor query(final Uri uri, String[] projection,
			final String selection, final String[] selectionArgs,
			final String sort) {
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		int match = sURIMatcher.match(uri);
		// Uri qUri = uri;
		switch (match) {
		case DOWNLOADS_VIEWCUSTOM:
			// Log.v(Constants.TAG,
			// "this is in view alll   !!!!!!!!!!!!!!!!!!");
			qb.setTables(DB_TABLE);
			// qUri = Downloads.CONTENT_URI;
			break;
		case DOWNLOADS: {
			// Log.v(Constants.TAG, "this is in downloads!!!!!!!!!!!!!!!!!!");
			qb.setTables(DB_TABLE);
			break;
		}
		case DOWNLOADS_ID: {
			qb.setTables(DB_TABLE);
			// Log.v(Constants.TAG,
			// "this is in DOWNLOADS_ID!!!!!!!!!!!!!!!!!!");
			qb.appendWhere(Downloads.Impl._ID + "=");
			qb.appendWhere(uri.getPathSegments().get(1));
			break;
		}
		default: {
			Log.v(TAG, "DownloadProvider.query(), querying unknown URI: " + uri);
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		}

		Cursor ret = qb.query(db, projection, selection, selectionArgs, null,
				null, sort);

		if (ret != null) {
			ret = new ReadOnlyCursorWrapper(ret);
		}

		if (ret != null) {
			ret.setNotificationUri(getContext().getContentResolver(), uri);
		}

		return ret;
	}
// xujing3 remove
//	DownloadSubject sp = DownloadSubject.getInstance();

	/**
	 * Updates a row in the database
	 */
	@Override
	public int update(final Uri uri, final ContentValues values,
			final String where, final String[] whereArgs) {
		// Log.i("zdx","***********************************************************DownloadProvider.update, uri:"+
		// uri);
		SQLiteDatabase db = null;
		try{
			db = mOpenHelper.getWritableDatabase();
		}catch(SQLiteException ex){
			return 0;
		}
//		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count=0;
		long rowId = 0;
		boolean startService = false;
//		int preStatus = 0;

		String filename = values.getAsString(Downloads.Impl._DATA);
		if (filename != null) {
			Cursor c = query(uri, new String[] { Downloads.Impl.COLUMN_TITLE },
					null, null, null);
			if (!c.moveToFirst() || c.getString(c.getColumnIndex(Downloads.Impl.COLUMN_TITLE)) == null) {
				values.put(Downloads.Impl.COLUMN_TITLE,
						new File(filename).getName());
			}
//			if (!c.moveToFirst()) {
//				preStatus = c.getInt(c.getColumnIndex(Downloads.Impl.COLUMN_STATUS));
//			}
			c.close();
		}

		Integer i = values.getAsInteger(Downloads.Impl.COLUMN_CONTROL);
		if (i != null) {
			startService = true;
		}
		Integer s = values.getAsInteger(Downloads.Impl.COLUMN_STATUS);
		if (s != null) {
			 startService = true;
		}

		int match = sURIMatcher.match(uri);
		switch (match) {
		case DOWNLOADS:
		case DOWNLOADS_ID: {
			String myWhere;
			if (where != null) {
				if (match == DOWNLOADS) {
					myWhere = "( " + where + " )";
				} else {
					myWhere = "( " + where + " ) AND ";
				}
			} else {
				myWhere = "";
			}
			if (match == DOWNLOADS_ID) {
				String segment = uri.getPathSegments().get(1);
				rowId = Long.parseLong(segment);
				myWhere += " ( " + Downloads.Impl._ID + " = " + rowId + " ) ";
				
			}
			if (values.size() > 0) {
				// hubing3 add fix bug : 13580
				try{
					count = db.update(DB_TABLE, values, myWhere, whereArgs);
				}catch(Exception e){
					e.printStackTrace();
				}

			} else {
				count = 0;
			}
			break;
		}
		default: {
			Log.e(Constants.TAG,
					"downloadProvider.update, unknown/invalid URI: " + uri);
			throw new UnsupportedOperationException("Cannot update URI: " + uri);
		}
		}
		// Log.i("zdx","Call notifyChange........");

		Context context = getContext();
		context.getContentResolver().notifyChange(uri, null);
		
		//xujing add, sending broadcast when status changed
//		if(preStatus != s && preStatus != 0){
//			Intent intent = new Intent(
//					DownloadConstant.ACTION_DOWNLOAD_STATE_CHANGED);
//			intent.putExtra(DownloadConstant.EXTRA_PACKAGENAME, packageName);
//			intent.putExtra(DownloadConstant.EXTRA_VERSION, versionCode);
//			intent.putExtra(DownloadConstant.EXTRA_STATUS, s);
//			intent.putExtra(DownloadConstant.EXTRA_PROGRESS, progress);
//			context.sendBroadcast(intent);
//		}
		
		// ----------philn--------
		//xujing3 remove
//		 updateDownloadStatus(uri);

		if (startService) {
			Log.i(TAG,
					"*******************DownloadProvider.update(), update one download in db, start DownloadService");
			Intent intent = new Intent(context, DownloadService.class);
			intent.putExtra("type", 1);
			context.startService(intent);
		}

		return count;
	}

	// xujing3 remove
//	private void updateDownloadStatus(Uri uri) {
//		Cursor sc = query(uri, null, null, null, null);
//		if (sc.moveToFirst()) {
//			DownloadInfo info = new DownloadInfo();
//			int id = sc.getInt(sc.getColumnIndex("_id"));
//			String pkgName = sc.getString(sc.getColumnIndex("pkgname"));
//			String versionCode = sc.getString(sc.getColumnIndex("versioncode"));
//			String appName = sc.getString(sc.getColumnIndex("title"));
//			String iconAddr = sc.getString(sc.getColumnIndex("iconaddr"));
//			String installPath = sc.getString(sc.getColumnIndex("_data"));
//			String downloadUrl = sc.getString(sc.getColumnIndex("uri"));
//			String handpause = sc.getString(sc.getColumnIndex("handpause"));
//			int status = sc.getInt(sc.getColumnIndex("status"));
//			String wifistatus = sc.getString(sc.getColumnIndex("wifistatus"));
//			long currentBytes = sc.getLong(sc.getColumnIndex("current_bytes"));
//			long totalBytes = sc.getLong(sc.getColumnIndex("total_bytes"));
//			int progress = 0;
//			if (totalBytes != 0 && currentBytes != 0) {
//				progress = DownloadHelpers.getProgresss(currentBytes,
//						totalBytes);
//			}
//			// zdx modify
//			if ((status != Downloads.STATUS_INSTALL)
//					&& (status != Downloads.STATUS_SUCCESS)) {
//				if (progress == 100){
//					status = Downloads.STATUS_SUCCESS;
//				}else if (DownloadHelpers.waitWifiStatus(getContext(), String.valueOf(status), wifistatus,
//						handpause)) {
//					wifistatus = LDownloadManager.STATUS_VALUES[1];
//					status = DownloadInfo.DOWNLOAD_WAIT_WIFI;
//				}else if( Downloads.Impl.STATUS_FILE_ERROR == status){
//					//file error
//					status = -2;
//				}else{
//					status = Helpers.checkErrorCode(status);
//				}
////				status = Helpers.checkErrorCode(status);
////				if (progress == 100)
////					status = Downloads.STATUS_SUCCESS;
////				if (DownloadHelpers.waitWifiStatus(getContext(),
////						String.valueOf(status), wifistatus, handpause)) {
////					wifistatus = LDownloadManager.STATUS_VALUES[1];
////					status = DownloadInfo.DOWNLOAD_WAIT_WIFI;
////				}
//			}
//
//			info.setId(String.valueOf(id));
//			info.setPackageName(pkgName);
//			info.setVersionCode(versionCode);
//			info.setAppName(appName);
//			info.setAppSize(String.valueOf(totalBytes));
//			info.setCurrentBytes(currentBytes);
//			info.setTotalBytes(totalBytes);
//			info.setProgress(progress);
//			info.setIconAddr(iconAddr);
//			info.setInstallPath(installPath);
//			info.setWifistatus(Integer.parseInt(wifistatus));
//			info.setDownloadStatus(status);
//			info.setDownloadUrl(downloadUrl);
//			sp.changes(info);
//		}
//		sc.close();
//
//	}

	/**
	 * Deletes a row in the database
	 */
	@Override
	public int delete(final Uri uri, final String where,
			final String[] whereArgs) {

		// Helpers.validateSelection(where, sAppReadableColumnsSet);
		SQLiteDatabase db = null;
		try{
			db = mOpenHelper.getWritableDatabase();
		}catch(SQLiteException ex){
			return 0;
		}
//		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		int match = sURIMatcher.match(uri);
		switch (match) {
		case DOWNLOADS:
		case DOWNLOADS_ID: {
			String myWhere;
			if (where != null) {
				if (match == DOWNLOADS) {
					myWhere = "( " + where + " )";
				} else {
					myWhere = "( " + where + " ) AND ";
				}
			} else {
				myWhere = "";
			}
			if (match == DOWNLOADS_ID) {
				String segment = uri.getPathSegments().get(1);
				long rowId = Long.parseLong(segment);
				myWhere += " ( " + Downloads.Impl._ID + " = " + rowId + " ) ";
//				DownloadInfoContainer.remove(rowId);
				Log.i(TAG, "delete myWhere:" + myWhere);
			}
			// int callingUid = Binder.getCallingUid();
			count = db.delete(DB_TABLE, myWhere, whereArgs);
			

			break;
		}
		default: {
			Log.e(Constants.TAG,
					"DownloadProvider.delete,unknown/invalid URI: " + uri);
			throw new UnsupportedOperationException("Cannot delete URI: " + uri);
		}
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	/**
	 * Remotely opens a file
	 */
	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode)
			throws FileNotFoundException {

		// This logic is mostly copied form openFileHelper. If openFileHelper
		// eventually
		// gets split into small bits (to extract the filename and the
		// modebits),
		// this code could use the separate bits and be deeply simplified.
		Cursor c = query(uri, new String[] { "_data" }, null, null, null);
		int count = (c != null) ? c.getCount() : 0;
		if (count != 1) {
			// If there is not exactly one result, throw an appropriate
			// exception.
			if (c != null) {
				c.close();
			}
			if (count == 0) {
				throw new FileNotFoundException("No entry for " + uri);
			}
			throw new FileNotFoundException("Multiple items at " + uri);
		}

		c.moveToFirst();
		String path = c.getString(0);
		c.close();
		if (path == null) {
			throw new FileNotFoundException("No filename found.");
		}
		if (!Helpers.isFilenameValid(path)) {
			throw new FileNotFoundException("Invalid filename.");
		}

		if (!"r".equals(mode)) {
			throw new FileNotFoundException("Bad mode for " + uri + ": " + mode);
		}
		ParcelFileDescriptor ret = ParcelFileDescriptor.open(new File(path),
				ParcelFileDescriptor.MODE_READ_ONLY);

		if (ret == null) {
			Log.v(TAG, "DownloadProvider.openFile, couldn't open file");
			throw new FileNotFoundException("couldn't open file");
		} else {
			ContentValues values = new ContentValues();
			values.put(Downloads.Impl.COLUMN_LAST_MODIFICATION,
					System.currentTimeMillis());
			update(uri, values, null, null);
		}
		return ret;
	}

	private class ReadOnlyCursorWrapper extends CursorWrapper implements
			CrossProcessCursor {
		public ReadOnlyCursorWrapper(Cursor cursor) {
			super(cursor);
			mCursor = (CrossProcessCursor) cursor;
		}

		public boolean deleteRow() {
			throw new SecurityException(
					"Download manager cursors are read-only");
		}

		public boolean commitUpdates() {
			throw new SecurityException(
					"Download manager cursors are read-only");
		}

		public void fillWindow(int pos, CursorWindow window) {
			mCursor.fillWindow(pos, window);
		}

		public CursorWindow getWindow() {
			return mCursor.getWindow();
		}

		public boolean onMove(int oldPosition, int newPosition) {
			return mCursor.onMove(oldPosition, newPosition);
		}

		private CrossProcessCursor mCursor;
	}

}
