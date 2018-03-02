package com.lenovo.lejingpin.hw.lcapackageinstaller.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.BaseColumns;
import android.util.Log;

public class LcaInfoUtils {
	private final static String tag = "LcaPackageInstaller";

	private static String SYSVERSION;
	
	private LcaInfoUtils(){
		
	}

	public static void add(Context context, String appid, String version,
			String pkgname, String apptype, String liteVersion) {
		ContentValues values = new ContentValues();
		values.put(Apps.APPID, appid);
		values.put(Apps.PKGNAME, pkgname);
		values.put(Apps.VERSION, version);
		if (liteVersion != null)
			values.put(Apps.LITEVERSION, liteVersion);
		if (apptype != null)
			values.put(Apps.APPTYPE, apptype);
		Log.d(tag, String.format(
				"add value appid(%s) version(%s) pkgname (%s) apptype(%s) ",
				appid, version, pkgname, apptype));
		ContentResolver resolver = context.getContentResolver();
		try {
			resolver.insert(Apps.CONTENT_URI, values);
		} catch (Exception e) {
			Log.d(tag, "Hase exception when insert values to Apps db ", e);
		}
	}

	public static void delete(Context context, String pkgname) {
		String where = "(" + Apps.PKGNAME + "=\"" + pkgname + "\")";
		ContentResolver resolver = context.getContentResolver();
		int row = resolver.delete(Apps.CONTENT_URI, where, null);
		Log.d(tag, String.format("delete from db pkgname(%s) rowid(%d)",
				pkgname, row));
	}

	public static void update(Context context, String pkgname,String issuccess) {
		ContentValues values = new ContentValues();
		values.put(Apps.ISSUCCESS,issuccess);
		String where = "(" + Apps.PKGNAME + "=\"" + pkgname + "\")";
		ContentResolver resolver = context.getContentResolver();
		resolver.update(Apps.CONTENT_URI, values, where, null);
	}

	public static Cursor query(Context context, String pkgname) {
		ContentResolver resolver = context.getContentResolver();
		return resolver.query(Apps.CONTENT_URI, null, Apps.PKGNAME + "=?",
				new String[] { pkgname }, null);
	}

	public static boolean findByAppId(Context context, String appId) {
		String where = "(" + Apps.APPID + "=\"" + appId + "\")";
		ContentResolver resolver = context.getContentResolver();
		Cursor c = resolver.query(Apps.CONTENT_URI, null, where, null, null);
		boolean found = false;
		if (c != null) {
			if (c.getCount() > 0) {
				found = true;
			}
			c.close();
		} else {
			found = false;
		}
		return found;
	}

	private static final class Apps implements BaseColumns {
		public static final String TAB_NAME = "lcainfo";

		//test by dining 2013-06-24
		public static final Uri CONTENT_URI = Uri
				.parse("content://com.lenovo.lejingpin.provider.hw.appinfonew/"
						+ TAB_NAME);

		public static final String PKGNAME = "pkgname";
		public static final String APPID = "appid";
		public static final String VERSION = "version";
		public static final String APPTYPE = "apptype";
		public static final String LITEVERSION = "isliteversion";
		public static final String ISSUCCESS = "successfulinstall";
	}

	public static boolean isSdkSupport(String apkVersion) {
		if (apkVersion == null || apkVersion.trim().length() == 0) {
			return true;
		}
		String s1 = null;
		if (SYSVERSION == null) {
			initSystemVersion();
		}
		if (SYSVERSION.length() > 3) {
			s1 = SYSVERSION.substring(0, 3);
		} else {
			s1 = SYSVERSION;
		}

		apkVersion = apkVersion.length() > 3 ? apkVersion.substring(0, 3)
				: apkVersion;
		return s1.compareTo(apkVersion) >= 0;
	}

	private static String getSoftwareVersion(String key) {

		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					"/etc/version.conf"), 1024);

			String a = "";
			try {
				while ((a = reader.readLine()) != null) {
					if (a.indexOf(key) != -1) {
						return a.substring(a.indexOf(',') + 1, a.length());
					}
				}

			} finally {
				reader.close();
			}
		} catch (IOException e) {
			Log.e(tag,
					"IO Exception when getting kernel version for Device Info screen",
					e);
		}

		return null;
	}

	private static void initSystemVersion() {
		if (SYSVERSION == null) {
			String operating = getSoftwareVersion("operating");
			if (operating == null || operating.indexOf("build:") < 0) {
				SYSVERSION = Build.VERSION.RELEASE;
			} else {
				SYSVERSION = operating.substring(operating.indexOf("build:")
						+ "build:".length());
			}
			Log.d(tag, "\n-------------------\nSystem version is:\n"
					+ SYSVERSION + "\n-------------------\n");
			return;
		}
	}
}
