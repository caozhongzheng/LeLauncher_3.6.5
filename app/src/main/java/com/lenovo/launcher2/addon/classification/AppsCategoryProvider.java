package com.lenovo.launcher2.addon.classification;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;


public class AppsCategoryProvider extends ContentProvider {

    private static final String DATABASE_NAME = "lenovocategory.db";
    //test by dining 2013-06-24 launcher2->xlauncher2
    public static final String AUTHORITY = "com.lenovo.launcher2.addon.classification";
    public static final String CATEGORY_AND_APPS_URL = "t_app_and_t_category ";
    public static final String CATEGORY_BY_PACKAGE_NAME="t_app_by_packgage_name";
    private static final int URL_CATEGORY_AND_APPS = 1;
    private static final int URL_CATEGORY_PACKAGE_NAME = 2;
    
    private SQLiteDatabase mDb;
    private static final int DATABASE_VERSION = 8;
    private final String  TAG = "category";
    private final boolean DEBUG = true;

    
    private static final UriMatcher s_urlMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        s_urlMatcher.addURI(AUTHORITY, CATEGORY_AND_APPS_URL, URL_CATEGORY_AND_APPS);
        s_urlMatcher.addURI(AUTHORITY, CATEGORY_BY_PACKAGE_NAME, URL_CATEGORY_PACKAGE_NAME);
    }

    
    @Override
    public boolean onCreate() {
        // TODO Auto-generated method stub
        if (DEBUG) android.util.Log.i(TAG, "AppsCategoryProvider created");
        return copyDb();
    }

    
    private boolean copyDb() {
        boolean res = true;
        Context context = getContext();
        try {
            File file = getContext().getFileStreamPath(DATABASE_NAME);
            if (DEBUG) android.util.Log.i(TAG, "getdb file info: canRead=" + file.canRead() + //
                    ",canWrite=" + file.canWrite() + //
                    ",canExcute=" + file.canExecute() + //
                    ",isFile=" + file.isFile() + //
                    ",absoluetPath=" + file.getAbsolutePath() + //
                    ",size=" + file.getTotalSpace());

            mDb = SQLiteDatabase.openOrCreateDatabase(context.getFileStreamPath(DATABASE_NAME), null);

            int version = mDb.getVersion();
            if (DEBUG) android.util.Log.v(TAG, "copyDb ==== Database version : " + version);

            if (version != DATABASE_VERSION) {
                res = createTable(mDb);
            }

        } catch (SQLException e) {
            res = false;
        }

        return res;
    }
    
    
    
    private boolean createTable(SQLiteDatabase db) {
        if (DEBUG) android.util.Log.i(TAG, "AppsCategoryProvider createTable ");
        boolean res = true;

        Context context = getContext();
        FileOutputStream out = null;
        InputStream ins = null;
        try {
            out = context.openFileOutput(DATABASE_NAME, Context.MODE_PRIVATE);
            ins = context.getAssets().open(DATABASE_NAME);
            byte[] b = new byte[1024];

            while (ins.read(b) != -1) {
                out.write(b);
            }

            if (DEBUG) android.util.Log.i(TAG, "copy db file sucess..");

        } catch (Exception e) {
            res = false;
            if (DEBUG) android.util.Log.e(TAG, e.getMessage());

        } finally {
            try {
                if (ins != null)
                    ins.close();

                if (out != null)
                    out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        db.setVersion(DATABASE_VERSION);
        return res;
    }
    
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        int match = s_urlMatcher.match(uri);
        android.util.Log.i("testcategory", "url = " + uri + "   match = " + match);
        
        switch(match){
            case URL_CATEGORY_AND_APPS:{
                if(selectionArgs == null){
                    break;
                }
                int selectionLength = selectionArgs.length;
                if (selectionLength > 0) {
                    String pkgArgs = selectionArgs[0];
                    String sql="select cate_name,pkgname from  t_category inner join t_app on t_category.cate_p =t_app.cate_p where pkgname in"
                            + "(" + pkgArgs + ") order by t_app.cate_p";
                    
                    android.util.Log.v("testcategory", sql);
    
                    return mDb.rawQuery(sql, null);
                }
            }
            break;
            case URL_CATEGORY_PACKAGE_NAME:{
                if(selectionArgs == null){
                    break;
                }
                int length = selectionArgs.length;
                if(length<0){
                    break;
                }
                String sql = "select cate_name from t_category where cate_p =(select cate_p from t_app where pkgname =\"" + selectionArgs[0]+ "\")";
                Log.d("addmoldeer", sql);
                return mDb.rawQuery(sql, null);
            }
            default :
                return null;
        }
        return null;
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

}
