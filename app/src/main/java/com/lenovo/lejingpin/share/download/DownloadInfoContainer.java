package com.lenovo.lejingpin.share.download;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

public class DownloadInfoContainer {
	private static final String TAG = "DownloadInfoContainer";
	
	public static String mNetworkType;
	static private HashMap<Long,DownloadInfo> mAllDownloadinfo = new HashMap<Long,DownloadInfo>();
	/**
	 * @param args
	 */
	private DownloadInfoContainer(){
		
	}
	
	public synchronized static ArrayList<DownloadInfo> getAll(Context context){
		if(mAllDownloadinfo.size() == 0){
			loadAllDownloadInfo(context);
		}
		ArrayList<DownloadInfo> downloads = new ArrayList<DownloadInfo>();
		 Set<Long> set = mAllDownloadinfo.keySet();
		 for(Iterator<Long> iterator = set.iterator();iterator.hasNext();){
			 Long id = iterator.next();
			 DownloadInfo info = get(context,id);
			 downloads.add(info);
		 }
		
		return downloads;
	}
	
	public synchronized static DownloadInfo get(Context context,long id){
		if(mAllDownloadinfo.size() == 0){
			loadAllDownloadInfo(context);
		}
		return mAllDownloadinfo.get(id);
	}
	public synchronized static boolean remove(long id){
		if(!mAllDownloadinfo.containsKey(id))
			return false;
		Log.d(TAG,"-------DownloadInfoContainer-----------remove--------id:" + id);
		mAllDownloadinfo.remove(id);
		return true;
	}
	public synchronized static int deleteDBAndBufferById(Context context,Uri uri ,String id){
		 if(context == null || uri == null)
			 return 0;
		 int count = 0;
		 if(id != null){
			 count = context.getContentResolver().delete(uri, "_ID = ?", new String[] { id });
			 remove(Long.valueOf(id));
		 }else{
			 count = context.getContentResolver().delete(uri, null, null);
			 String segment = uri.getPathSegments().get(1);
			 if(segment!=null){
				 Long rowId = Long.parseLong(segment);
				 remove(rowId);
			 }
		 }
		 return count;
	}
	
	public synchronized static void deleteDBAndBuffer(Context context,String pkName,String versionCode){
		 if(context == null)
			 return ;
		 Set<Long> set = mAllDownloadinfo.keySet();
		 for(Iterator<Long> iterator = set.iterator();iterator.hasNext();){
			 Long id = iterator.next();
			 DownloadInfo info = get(context,id);
			 if(info.getPackageName().equals(pkName) && info.getVersionCode().equals(versionCode)){
				 remove(id);
				 context.getContentResolver().delete(ContentUris.withAppendedId(Downloads.Impl.CONTENT_URI,id), null, null);
			 }
		 }
	}
	public synchronized static int getDownloadSize(Context context){
		int count = 0;
		if(0 == mAllDownloadinfo.size()){
			loadAllDownloadInfo(context);
		}
		 Set<Long> set = mAllDownloadinfo.keySet();
		 for(Iterator<Long> iterator = set.iterator();iterator.hasNext();){
			 Long id = iterator.next();
			 DownloadInfo info = get(context,id);
			 int status = info.getDownloadStatus();
			 if(Downloads.Impl.STATUS_RUNNING == status
					 || Downloads.Impl.STATUS_PENDING == status || Downloads.Impl.STATUS_PENDING_PAUSED == status)
				 count ++;
		 }
		return count;
	}
	public synchronized static int getDownloadCountByStates(Context context,int status){
		int count = 0;
		if(0 == mAllDownloadinfo.size()){
			loadAllDownloadInfo(context);
		}
		 Set<Long> set = mAllDownloadinfo.keySet();
		 for(Iterator<Long> iterator = set.iterator();iterator.hasNext();){
			 Long id = iterator.next();
			 DownloadInfo info = get(context,id);
			 int s = info.getDownloadStatus();
			 if(s == status)
				 count ++;
		 }
		return count;
	}
	
	public synchronized static int updateDBAndBufferById(Context context,Uri uri,ContentValues values,String id){
		 if(context == null || uri == null)
			 return 0;
		 int count;
		 Log.d(TAG,"updateDBAndBufferById,values:" + values);
		 if(id != null){
			 put(context,Long.valueOf(id),values);
			 count = context.getContentResolver().update(uri, values,"_ID = ?", new String[] { id });
		 }else{
			 String segment = uri.getPathSegments().get(1);
			 if(segment!=null){
				 Long rowId = Long.parseLong(segment);
				 put(context,rowId,values);
			 }
			 count = context.getContentResolver().update(uri, values, null, null);
		 }
		 return count;
	}
	
	public synchronized static Uri insertDBAndBuffer(Context context,Uri uri,ContentValues values){
		 if(context == null)
			 return null;
		 Uri returnUri = context.getContentResolver().insert(uri, values);
		 if(returnUri !=null){
			 String segment = returnUri.getPathSegments().get(1);
			 Long id = Long.parseLong(segment);
			 put(context,id, values);
		 }
		 return returnUri;
	}
	private synchronized static int getProgresss(long currentBytes, long totalBytes) {
		int i = (int) (currentBytes / (float) totalBytes * 100);
		return i;
	}
	
	private synchronized static void loadAllDownloadInfo(Context context){
		ContentResolver resolver = context.getContentResolver();
		Cursor sc = resolver.query(Downloads.CONTENT_URI, null, null, null,
				null);
		if(sc == null)
			return ;
		try {
			while (sc.moveToNext()) {
				DownloadInfo info = new DownloadInfo();
				int id = sc.getInt(sc.getColumnIndex(Downloads.Impl._ID));
				String pkgName = sc.getString(sc.getColumnIndex(Downloads.Impl.COLUMN_PKGNAME));
				String versionCode = sc.getString(sc
						.getColumnIndex(Downloads.Impl.COLUMN_VERSIONCODE));
				String appName = sc.getString(sc.getColumnIndex(Downloads.Impl.COLUMN_APPNAME));
				String iconAddr = sc.getString(sc.getColumnIndex(Downloads.Impl.COLUMN_ICONADDR));
				String installPath = sc.getString(sc.getColumnIndex(Downloads.Impl._DATA));
				String handpause = sc.getString(sc.getColumnIndex(Downloads.Impl.COLUMN_HANDTOPAUSE));
				int status = sc.getInt(sc.getColumnIndex(Downloads.Impl.COLUMN_STATUS));
				int control = sc.getInt(sc.getColumnIndex(Downloads.Impl.COLUMN_CONTROL));
				String wifistatus = sc.getString(sc
						.getColumnIndex(Downloads.Impl.COLUMN_WIFISTATUS));
				long currentBytes = sc.getLong(sc
						.getColumnIndex(Downloads.Impl.COLUMN_CURRENT_BYTES));
				long totalBytes = sc.getLong(sc.getColumnIndex(Downloads.Impl.COLUMN_TOTAL_BYTES));
				int category = sc.getInt(sc.getColumnIndex(Downloads.Impl.COLUMN_CATEGORY));

				int progress = 0;
				if (totalBytes != 0 && currentBytes != 0) {
					progress = getProgresss(currentBytes, totalBytes);
				}

				if ((status != Downloads.STATUS_INSTALL)
						&& (status != Downloads.STATUS_SUCCESS)) {
					if (progress == 100){
						status = Downloads.STATUS_SUCCESS;
					}
//					else if (waitWifiStatus(context, String.valueOf(status), wifistatus,
//							handpause)) {
//						wifistatus = LDownloadManager.STATUS_VALUES[1];
//						status = DownloadInfo.DOWNLOAD_WAIT_WIFI;
//					}
					else{
						status = Helpers.getStatus(status);
					}
				}
				info.setId(String.valueOf(id));
				info.setPackageName(pkgName);
				info.setVersionCode(versionCode);
				info.setAppName(appName);
				info.setAppSize(String.valueOf(totalBytes));
				info.setProgress((int) progress);
				info.setIconAddr(iconAddr);
				info.setInstallPath(installPath);
				info.setWifistatus(Integer.parseInt(wifistatus));
				info.setDownloadStatus(status);
				info.setCurrentBytes(currentBytes);
				info.setTotalBytes(totalBytes);
				info.setCategory(category);
				info.setControl(control);
				Log.d(TAG,"loadAllDownloadInfo, pkgName:" + pkgName);
				mAllDownloadinfo.put((long)id, info);
//				infos.add(info);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sc.close();
		}
		return ;
	}

	/**
	 * The current network signal type
	 */
	public static String currentNetworkType(Context context) {
		if (null == mNetworkType) {
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetInfo = connectivityManager
					.getActiveNetworkInfo();
			if (activeNetInfo != null
					&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				mNetworkType = LDownloadManager.STATUS_VALUES[1];
			}else
				mNetworkType = LDownloadManager.STATUS_VALUES[0];
		}
		return mNetworkType;
	}
	
	private static boolean waitWifiStatus(Context context, String status,
			String wifistatus, String handpause) {
		SharedPreferences downloadsp = context.getSharedPreferences("download",
				Context.MODE_WORLD_WRITEABLE + Context.MODE_WORLD_READABLE
						+ Context.MODE_APPEND);
		boolean wifiStatus = downloadsp.getBoolean("wifi", false);
		if (LDownloadManager.STATUS_VALUES[0]
				.equals(currentNetworkType(context)) && wifiStatus) {
			if ((String.valueOf(DownloadInfo.DOWNLOAD_READY).equals(status) && LDownloadManager.STATUS_VALUES[1]
					.equals(wifistatus))
					|| (LDownloadManager.STATUS_VALUES[1].equals(wifistatus) && LDownloadManager.STATUS_VALUES[0]
							.equals(handpause))) {
				return true;
			}

		}
		return false;
	}
	
	public synchronized static DownloadInfo get(Context context,String pkName,String versionCode){
		if(pkName == null || versionCode == null)
			return null;
		if(mAllDownloadinfo.size() == 0){
			loadAllDownloadInfo(context);
		}
		Set<Long> set = mAllDownloadinfo.keySet();
		
	    for(Iterator<Long> iterator = set.iterator();iterator.hasNext();){
	    	DownloadInfo info = mAllDownloadinfo.get(iterator.next());
//			Log.d(TAG,"-------DownloadInfoContainer-----------get--------pkName:" + info.getPackageName()
//			+ ",versionCode:" + info.getVersionCode());
			if(info.getPackageName().equals(pkName) || info.getVersionCode().equals(versionCode)){
				Log.d(TAG,"-------DownloadInfoContainer-----------get--------pkName:" + pkName
						+ ",versionCode:" + versionCode + ",size:" + mAllDownloadinfo.size());
				return info;
			}
	    }
//		for(DownloadInfo info :  (Collection<DownloadInfo>)AllDownloadinfo.values()){
//			Log.d(TAG,"-------DownloadInfoContainer-----------get--------pkName:" + info.getPackageName()
//					+ ",versionCode:" + info.getVersionCode());
//			if(info.getPackageName().equals(pkName) || info.getVersionCode().equals(versionCode))
//				return info;
//		}
		
		return null;
	}
	
	public synchronized static boolean put(Context context,long id,final ContentValues values){
		DownloadInfo info = get(context,id);
		if(info == null){
			if(values.get(Downloads.Impl.COLUMN_PKGNAME) == null 
					|| values.get(Downloads.Impl.COLUMN_VERSIONCODE) == null
					|| values.get(Downloads.Impl.COLUMN_PKGNAME).toString().isEmpty()
					|| values.get(Downloads.Impl.COLUMN_VERSIONCODE).toString().isEmpty())
				return false;
			info = new DownloadInfo(values.get(Downloads.Impl.COLUMN_PKGNAME).toString()
							,values.get(Downloads.Impl.COLUMN_VERSIONCODE).toString());
		}
		Log.d(TAG,"-------DownloadInfoContainer-----------put--------values:" + values);
		info.setId(String.valueOf(id));
		if(values.getAsString(Downloads.Impl.COLUMN_PKGNAME) != null)
			info.setPackageName(values.getAsString(Downloads.Impl.COLUMN_PKGNAME));
		
		if(values.getAsString(Downloads.Impl.COLUMN_VERSIONCODE) != null)
			info.setVersionCode(values.getAsString(Downloads.Impl.COLUMN_VERSIONCODE));
		
		if(values.getAsString(Downloads.Impl.COLUMN_VERSIONNAME) != null)
			info.setVersionName(values.getAsString(Downloads.Impl.COLUMN_VERSIONNAME));
		
		if(values.getAsString(Downloads.Impl.COLUMN_APPNAME) != null)
			info.setAppName(values.getAsString(Downloads.Impl.COLUMN_APPNAME));
		
		if(values.getAsString(Downloads.Impl.COLUMN_APPSIZE) != null)
			info.setAppSize(values.getAsString(Downloads.Impl.COLUMN_APPSIZE));
		
		if(values.getAsString(Downloads.Impl.COLUMN_ICONADDR) != null)
			info.setIconAddr(values.getAsString(Downloads.Impl.COLUMN_ICONADDR));
		
		if(values.getAsString(Downloads.Impl._DATA) != null)
			info.setInstallPath(values.getAsString(Downloads.Impl._DATA));
		
		Long currentBytes = values.getAsLong(Downloads.Impl.COLUMN_CURRENT_BYTES);
		if(currentBytes != null)
			info.setCurrentBytes(currentBytes);
		
		Long totalBytes = values.getAsLong(Downloads.Impl.COLUMN_TOTAL_BYTES);
		if(totalBytes != null)
			info.setTotalBytes(totalBytes);
		
		if(values.getAsInteger(Downloads.Impl.COLUMN_CONTROL) != null){
			info.setControl(values.getAsInteger(Downloads.Impl.COLUMN_CONTROL));
		}
		
		if(values.getAsInteger(Downloads.Impl.COLUMN_CATEGORY) != null){
			info.setCategory(values.getAsInteger(Downloads.Impl.COLUMN_CATEGORY));
		}
		
		int progress = 0;
		if (currentBytes != null && info.getTotalBytes() != 0) {
			progress = getProgresss(currentBytes, info.getTotalBytes());
			info.setProgress(progress);
		}
		
		Integer status = values.getAsInteger(Downloads.Impl.COLUMN_STATUS);
//		String handpause = values.getAsString(Downloads.Impl.COLUMN_HANDTOPAUSE);
		String wifistatus = values.getAsString(Downloads.Impl.COLUMN_WIFISTATUS);
		if (status!= null
				&& (status != Downloads.STATUS_INSTALL)
				&& (status != Downloads.STATUS_SUCCESS)) {
			if (progress == 100){
				status = Downloads.STATUS_SUCCESS;
			}else{
				status = Helpers.getStatus(status);
			}
		}
		if(wifistatus != null){
			info.setWifistatus(Integer.valueOf(wifistatus));
		}
		
		if(status != null){
			info.setDownloadStatus(status);
		}
		
		mAllDownloadinfo.put(id, info);
		return true;
	}

}
