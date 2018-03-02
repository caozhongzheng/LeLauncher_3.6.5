package com.lenovo.lejingpin.hw.content.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.lenovo.lejingpin.hw.content.data.DownloadAppInfo;
import com.lenovo.lejingpin.hw.content.data.HwConstant;
import com.lenovo.lejingpin.hw.content.data.ReCommendsApp;
import com.lenovo.lejingpin.hw.content.data.RecommendsAppList;
import com.lenovo.lejingpin.hw.content.data.UpgradeApp;
import com.lenovo.lejingpin.hw.content.data.UpgradeAppList;
import com.lenovo.lejingpin.hw.content.util.ContentManagerLog;
import com.lenovo.lejingpin.hw.content.util.LocalAppUtil;
import com.lenovo.lejingpin.hw.content.util.SharePreferenceUtil;



public class HWDBUtil {

	private static String TAG = "HWDBUtil";
	
	public static final Uri CONENT_DOWNLOAD_URI = Uri.parse("content://com.lenovo.lejingpin.hw.content.download/download/");

	private HWDBUtil(){}
	
	public synchronized static RecommendsAppList insterSpereApp(Context context, RecommendsAppList list){
		//int size = 0;
		if(list!=null && !list.isEmpty()){
			list = getRecommendsAppList(context, list);
			if(list!=null && !list.isEmpty()){
				//size = list.getNewAppSize();
				ArrayList<ContentValues> valueList = new ArrayList<ContentValues>();
				StringBuffer appids = new StringBuffer();
				RecommendsAppList recommendAppsInDB = getAllRecommendsAppListByCursor(context, null, null);
				if(recommendAppsInDB != null ){
					List<ReCommendsApp> recommendList = recommendAppsInDB.getAppStoreList();
					if(recommendList != null && !recommendList.isEmpty()){
						for(ReCommendsApp app: recommendAppsInDB.getAppStoreList()){
							String localId = app.getLcaid();
							if( !TextUtils.isEmpty(localId) ){
								appids.append(localId).append(":").append(app.getCollect()).append(",");
							}
						}
					}
				}
				for (ReCommendsApp app : list.getAppStoreList()) {
					ContentValues values = new ContentValues();
					String localId = app.getLcaid();
					values.put(SpereApp.APP_NAME, app.getAppName());
					values.put(SpereApp.PACKAGE_NAME, app.getPackageName());
					values.put(SpereApp.VERSION_CODE, app.getVersionCode());
					values.put(SpereApp.ICON_ADDRESS, app.getIconAddress());
					values.put(SpereApp.APP_LOCAL_ID, localId);
					values.put(SpereApp.APP_DELETE, app.getDelete());
					values.put(SpereApp.APP_COLLECT, app.getCollect());
					values.put(SpereApp.APP_DOWNLOAD_COUNT, app.getDownloadCount());
					values.put(SpereApp.APP_FAVORITES, app.getFavorites());
					values.put(SpereApp.APP_STAR, app.getStartLevel());
					values.put(SpereApp.EXT_COLUMN_2, app.getVersion());
					values.put(SpereApp.APP_SIZE, app.getAppSize());
					valueList.add(values);
					if( !TextUtils.isEmpty(localId) ){
						appids.append(localId).append(":").append(app.getCollect()).append(",");
					}
				}
				context.getContentResolver().bulkInsert(SpereApp.CONTENT_SPERE_LIST_URI, valueList.toArray(new ContentValues[] {}));
				if(appids.length() > 0){
					appids.substring(0, appids.length() - 1);
				}
			}
		}
		boolean isupdate = updateSpereFavoritesState(context);
		if(isupdate){
			int state = SharePreferenceUtil.getInstance(context).getSpereUpdateState();
			if(state==0){
				SharePreferenceUtil.getInstance(context).setSpereUpdateState(1);
			}
		}
		return list;
	}
	
	private static boolean updateSpereFavoritesState(Context context){
		boolean result = false;
		try{
			ContentValues values = new ContentValues();
			values.put(SpereApp.APP_FAVORITES, "3");
			String whereStr = SpereApp.APP_FAVORITES+"=1";
			int num = context.getContentResolver().update(SpereApp.CONTENT_SPERE_URI, values, whereStr, null);
			if(num > 0){
				result= true;
			}else{
				result = false;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}

	public static void insertAppInfo(Context context, DownloadAppInfo info) {
		boolean dbInfo = queryAppInfo(context, info);
		Log.d(TAG, " insertAppInfo >> dbInfo : " + dbInfo);
		if(dbInfo) {
			return;
		} else {
			if(info != null) {
				ContentValues values = new ContentValues();
				ContentManagerLog.d(TAG, "insertAppInfo >>  appName : " + info.getAppName() + ";getPackageName : " + info.getPackageName()
						+ ";getVersionCode : " + info.getVersionCode());
				values.put(AppInfo.APP_NAME, info.getAppName());
				values.put(AppInfo.PACKAGE_NAME, info.getPackageName());
				values.put(AppInfo.VERSION_CODE, info.getVersionCode());
				values.put(AppInfo.APP_STAR, info.getStarLevel());
				values.put(AppInfo.APP_PAY, info.isPay() + "");
				values.put(AppInfo.APP_SIZE, info.getAppSize());
				values.put(AppInfo.APP_SNAPSHOT, info.getFirstSnapPath());
				values.put(AppInfo.APP_DESC, info.getAppAbstract());
				values.put(AppInfo.APP_NAME, info.getAppName());
				values.put(AppInfo.APP_VERSION, info.getAppVersion());
				values.put(AppInfo.APP_PUBLISH_DATE, info.getAppPublishdate());
				values.put(AppInfo.APP_COMMENT_COUNT, info.getAppCommentCount());
				values.put(AppInfo.APP_DOWNLOAD_COUNT, info.getDownloadCount());
				context.getContentResolver().insert(AppInfo.CONENT_APPINFO_URI, values);
			}
		}
	}
	
	public synchronized static void insertAppInfoList(Context context , ArrayList<DownloadAppInfo> infoList){
		if(infoList!=null && infoList.size()!=0){
			Log.d(TAG, "insertAppInfoList >> infoList >> size : "+infoList.size());
			ArrayList<ContentValues> valueList = new ArrayList<ContentValues>();
			for(DownloadAppInfo info : infoList){
				ContentValues values = new ContentValues();
				values.put(AppInfo.APP_NAME, info.getAppName());
				values.put(AppInfo.PACKAGE_NAME, info.getPackageName());
				values.put(AppInfo.VERSION_CODE, info.getVersionCode());
				values.put(AppInfo.APP_STAR, info.getStarLevel());
				values.put(AppInfo.APP_PAY, info.isPay() + "");
				values.put(AppInfo.APP_SIZE, info.getAppSize());
				values.put(AppInfo.APP_DESC, info.getAppAbstract());
				values.put(AppInfo.APP_SNAPSHOT, info.getFirstSnapPath());
				values.put(AppInfo.APP_VERSION, info.getAppVersion());
				values.put(AppInfo.APP_PUBLISH_DATE, info.getAppPublishdate());
				values.put(AppInfo.APP_COMMENT_COUNT, info.getAppCommentCount());
				values.put(AppInfo.APP_DOWNLOAD_COUNT, info.getDownloadCount());
				values.put(AppInfo.EXT_COLUMN_1, "0");
				values.put(AppInfo.EXT_COLUMN_2, "0");
				valueList.add(values);
			}
			context.getContentResolver().bulkInsert(AppInfo.CONTENT_APPINFO_LIST_URI, valueList.toArray(new ContentValues[] {}));
		}
	}
	
	
	public static HashMap<String,DownloadAppInfo> queryAppInfoList(Context context){
		Cursor cursor = null;
		DownloadAppInfo info = null;
		HashMap<String,DownloadAppInfo> appInfoMap = new HashMap<String,DownloadAppInfo>();
		try{
			cursor = context.getContentResolver().query(AppInfo.CONENT_APPINFO_URI, null, null, null, null);
			if(cursor == null || !cursor.moveToFirst()) return null;
			info = new DownloadAppInfo();
			String packageName = cursor.getString(cursor.getColumnIndex(AppInfo.PACKAGE_NAME));
			String versionCode = cursor.getString(cursor.getColumnIndex(AppInfo.VERSION_CODE));
			info.setPackageName(packageName);
			info.setVersionCode(versionCode);
			appInfoMap.put(packageName+versionCode, info);
		}catch(Exception e){
			ContentManagerLog.d(TAG, "Can not query app info");
			e.printStackTrace();
		}finally{
			if(cursor!=null && !cursor.isClosed()){
				cursor.close();
			}
		}
		return appInfoMap;
	}


	public static DownloadAppInfo queryAppInfo(Context context, String packageName, String versioncode) {
		String whereStr = "package_name = '" + packageName + "' AND version_code = '" + versioncode + "'";
		Cursor cursor = null;
		DownloadAppInfo info = null;
		try{
			cursor = context.getContentResolver().query(AppInfo.CONENT_APPINFO_URI, null, whereStr, null, null);
			if(cursor == null || !cursor.moveToFirst()) return null;
			info = new DownloadAppInfo();
			info.setAppName(cursor.getString(cursor.getColumnIndex(AppInfo.APP_NAME)));
			info.setPackageName(cursor.getString(cursor.getColumnIndex(AppInfo.PACKAGE_NAME)));
			info.setVersionCode(cursor.getString(cursor.getColumnIndex(AppInfo.VERSION_CODE)));
			info.setStarLevel(cursor.getString(cursor.getColumnIndex(AppInfo.APP_STAR)));
			info.setPay(Boolean.valueOf(cursor.getString(cursor.getColumnIndex(AppInfo.APP_PAY))));
			info.setAppSize(cursor.getString(cursor.getColumnIndex(AppInfo.APP_SIZE)));
			info.setFirstSnapPath(cursor.getString(cursor.getColumnIndex(AppInfo.APP_SNAPSHOT)));
			info.setAppAbstract(cursor.getString(cursor.getColumnIndex(AppInfo.APP_DESC)));
			info.setAppPublishDate(cursor.getString(cursor.getColumnIndex(AppInfo.APP_PUBLISH_DATE)));
			info.setAppCommentCount(cursor.getString(cursor.getColumnIndex(AppInfo.APP_COMMENT_COUNT)));
			info.setAppVersion(cursor.getString(cursor.getColumnIndex(AppInfo.APP_VERSION)));
			info.setDownloadCount(cursor.getString(cursor.getColumnIndex(AppInfo.APP_DOWNLOAD_COUNT)));
		}catch(Exception e){
			ContentManagerLog.d(TAG, "Can not query app info");
			e.printStackTrace();
		}finally{
			if(cursor!=null && !cursor.isClosed()){
				cursor.close();
			}
		}
		return info;
	}

	public static void insertLocalApp(Context context, UpgradeAppList list) {
		if(!list.isEmpty()) {
			list = getUpgradeAppList(context, list);
			for (UpgradeApp app : list.getAppStoreList()) {
				ContentValues values = new ContentValues();
				//ContentManagerLog.d(TAG, "insertLocalApp >> getPackageName : " + app.getPackageName() + ";getVersionCode : " + app.getVersionCode());
				values.put(LocalApp.PACKAGE_NAME, app.getPackageName());
				values.put(LocalApp.VERSION_CODE, app.getVersionCode());
				values.put(LocalApp.APP_STATE, app.getState());
				values.put(LocalApp.APP_DOWNLOAD_PATH, app.getDownloadApkPath());
				context.getContentResolver().insert(LocalApp.CONENT_LOCAL_URI, values);
			}
		}
	}

	private static boolean queryAppInfo(Context context, DownloadAppInfo info) {
		Cursor cursor = null;
		boolean isContain = false;
		try{
			String package_name = info.getPackageName();
			String version_code = info.getVersionCode();
			String whereStr = "package_name LIKE '" + package_name + "' AND version_code LIKE '" + version_code + "'";
			cursor = context.getContentResolver().query(AppInfo.CONENT_APPINFO_URI, null, whereStr, null, null);
			isContain = (cursor == null || cursor.getCount() == 0) ? false : true;
			
		}catch(Exception e){
			ContentManagerLog.d(TAG, "Can not query app info.");
			e.printStackTrace();
		}finally{
			if(cursor!=null && !cursor.isClosed()){
				cursor.close();
			}
		}

		return isContain;
	}

	private static RecommendsAppList getRecommendsAppList(Context context, RecommendsAppList list){
		int dbUnFavoritesSameAppSize = 0;
		RecommendsAppList dataList = new RecommendsAppList();
		RecommendsAppList deleteAppList = new RecommendsAppList();
		RecommendsAppList dbList = getAllRecommendsAppListByCursor(context, null,null);
		if(dbList != null && !dbList.isEmpty()) {
			for (ReCommendsApp app : list.getAppStoreList()){
				boolean isContain = dbList.isContain(app);
				if(isContain){
					int index =dbList.indexOf(app);
					ReCommendsApp dbApp = dbList.getApp(index);
					if("0".equals(dbApp.getFavorites())){
						dbUnFavoritesSameAppSize = dbUnFavoritesSameAppSize + 1;
						deleteAppList.add(dbApp);
					}else{
						continue;
					}
				}else{
					dataList.addNewApp(app);
				}
				dataList.add(app);
			}
		} else {
			dataList.addRecommendsAppList(list);
			dataList.addNewRecommendsAppList(list);
		}
		deleteDbApp(context,deleteAppList);
		dataList = delteLocalAppFromRecommendAppList(context, dataList);
		//dataList = delteDownloadedAppFromRecommendAppList(context, dataList);
		int size = dataList.getSize();
		int newAppSize = size - dbUnFavoritesSameAppSize;
		dataList.setNewAppSize(newAppSize);
		int dbUnFavoritessize = queryUnFavoritesSize(context);
		size = dbUnFavoritessize + size;
		if(size > HwConstant.COUNT_SPRERE_LIST_DB_TOTAL){
			int deleteSize = size - HwConstant.COUNT_SPRERE_LIST_DB_TOTAL;
			int result = deleteSpereAppFromDB(context,deleteSize);
			int deleteAppSize = 0;
			if(deleteSize > result){
				deleteAppSize = deleteSize - result;
				int appSize = dataList.getSize();
				if(appSize >= deleteAppSize){
					return dataList.subRecommendsAppList(deleteAppSize-1, appSize-1);
				}else{
					return dataList;
				}
			}else{
				return dataList;
			}
		}
		return dataList;
	}
	
	
	//yangmao add for code test
/*	private static void notificationNewApp(Context context,int size){
		NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		String title = context.getString(R.string.spere_new_app_notifi_title);
		String content = context.getString(R.string.spere_new_app_notifi_content,size);
		Notification n = new Notification(R.drawable.hw_new_app_notification, title, System.currentTimeMillis());
		n.flags = Notification.FLAG_AUTO_CANCEL;
		Intent i = new Intent(context, TestHwDownloadActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent contentIntent = PendingIntent.getActivity(
				context, 
		        R.string.app_name, 
		        i, 
		        PendingIntent.FLAG_UPDATE_CURRENT);
		n.setLatestEventInfo(
				context,
				title,
				content,
		        contentIntent);
		nm.notify(R.string.app_name, n);
	}*/
	
	private static void deleteDbApp(Context context,RecommendsAppList list){
		if(!list.isEmpty()){
			StringBuilder builder = new StringBuilder();
			builder.append(" _id in ");
			builder.append("(");
			List<ReCommendsApp>  listApp = list.getAppStoreList();
			int size = listApp.size();
			for(int i=0 ; i<size;i++){
				if(i==size-1){
					builder.append(String.valueOf(listApp.get(i).getId()));
				}else{
					builder.append(String.valueOf(listApp.get(i).getId())+",");
				}
			}
			builder.append(")");
			try{
				context.getContentResolver().delete(SpereApp.CONTENT_SPERE_URI, builder.toString(), null);
				
			}catch(Exception e){
				ContentManagerLog.d(TAG, "delete app error");
			}
		}
	}
	
	private static int deleteSpereAppFromDB(Context context,int deleteSize){
		int result = 0;
		if(deleteSize > 0){
			ArrayList<String> list = getUnFavoritesId(context,deleteSize);
			if(list!=null && !list.isEmpty()){
				StringBuilder builder = new StringBuilder();
				builder.append(" _id in ");
				builder.append("(");
				for(String id : list){
					builder.append(String.valueOf(id)+",");
				}
				builder.append(String.valueOf(deleteSize));
				builder.append(")");
				try{
					result = context.getContentResolver().delete(SpereApp.CONTENT_SPERE_URI, builder.toString(), null);
				}catch(Exception e){
					ContentManagerLog.d(TAG, "delete app error");
				}
			}
		}
		return result;
	}
	
	private static ArrayList<String> getUnFavoritesId(Context context,int size){
		Cursor cursor = null;
		ArrayList<String> list= null;
		try{
			String whereStr = SpereApp.APP_FAVORITES+"=0";
			cursor = context.getContentResolver().query(SpereApp.CONTENT_SPERE_URI, null, whereStr, null, null);
			if(cursor!=null && cursor.moveToFirst()){
				list = new ArrayList<String>();
				int index = 0;
				while(!cursor.isAfterLast()){
					if(index > size){
						break;
					}
					list.add(cursor.getString(cursor.getColumnIndex(SpereApp._ID)));
					index++;
					cursor.moveToNext();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(cursor!=null && !cursor.isClosed()){
				cursor.close();
			}
		}
		return list;
	}
	

	private static RecommendsAppList delteLocalAppFromRecommendAppList(Context context, RecommendsAppList list) {
		if(!list.isEmpty()) {
			LocalAppUtil localUtil = new LocalAppUtil(context);
			HashMap<String, String> localMap = localUtil.getLocalAllMap();
			ContentManagerLog.d(TAG, "delteLocalAppFromRecommendAppList : " + localMap);
			if(localMap!=null && !localMap.isEmpty()) {
				ContentManagerLog.d(TAG, "delteLocalAppFromRecommendAppList >> size : " + localMap.size());
				RecommendsAppList newRecommendsAppList = new RecommendsAppList();
				for (ReCommendsApp app : list.getAppStoreList()) {
					String package_name = app.getPackageName();
					if(package_name!=null){
						package_name = package_name.trim();
					}
					if(!localMap.containsKey(package_name)){
						newRecommendsAppList.add(app);
					} else {
						String hwPackageName = context.getPackageName();
						if(package_name.equals(hwPackageName)){
							int hwLocalVersionCode = Integer.valueOf(localMap.get(package_name));
							int hwServerVersionCode = Integer.valueOf(app.getVersionCode());
							if(hwLocalVersionCode <  hwServerVersionCode){
								newRecommendsAppList.add(app);
							}else{
								continue;
							}
						}else{
							continue;
						}
					}
				}
				
				for (ReCommendsApp app : list.getNewAppStoreList()){
					String package_name = app.getPackageName();
					if(package_name!=null){
						package_name = package_name.trim();
					}
					if(!localMap.containsKey(package_name)){
						newRecommendsAppList.addNewApp(app);
					} else {
						String hwPackageName = context.getPackageName();
						if(package_name.equals(hwPackageName)){
							int hwLocalVersionCode = Integer.valueOf(localMap.get(package_name));
							int hwServerVersionCode = Integer.valueOf(app.getVersionCode());
							if(hwLocalVersionCode <  hwServerVersionCode){
								newRecommendsAppList.addNewApp(app);
							}else{
								continue;
							}
						}else{
							continue;
						}
					}
				}
				
				return newRecommendsAppList;
			} else {
				return list;
			}
		}
		return list;
	}
	
	private static RecommendsAppList getAllRecommendsAppListByCursor(Context context, String whereStr,String order){
		Cursor cursor = null;
		RecommendsAppList list  = null;
		try {
			//String orederStr = " _id  DESC ";
			cursor = context.getContentResolver().query(SpereApp.CONTENT_SPERE_URI, null, whereStr, null, order);
			if(cursor != null && cursor.moveToFirst()) {
				list = new RecommendsAppList();
				while (!cursor.isAfterLast()) {
					ReCommendsApp app = new ReCommendsApp();
					app.setId(cursor.getInt(cursor.getColumnIndex(SpereApp._ID)));
					app.setPackageName(cursor.getString(cursor.getColumnIndex(SpereApp.PACKAGE_NAME)));
					app.setVersionCode(cursor.getString(cursor.getColumnIndex(SpereApp.VERSION_CODE)));
					app.setDelete(cursor.getString(cursor.getColumnIndex(SpereApp.APP_DELETE)));
					app.setCollect(cursor.getString(cursor.getColumnIndex(SpereApp.APP_COLLECT)));
					app.setIconAddress(cursor.getString(cursor.getColumnIndex(SpereApp.ICON_ADDRESS)));
					app.setAppName(cursor.getString(cursor.getColumnIndex(SpereApp.APP_NAME)));
					app.setLcaid(cursor.getString(cursor.getColumnIndex(SpereApp.APP_LOCAL_ID)));
					app.setFavorites(cursor.getString(cursor.getColumnIndex(SpereApp.APP_FAVORITES)));
					app.setDownloadCount(cursor.getString(cursor.getColumnIndex(SpereApp.APP_DOWNLOAD_COUNT)));
					app.setStartLevel(cursor.getString(cursor.getColumnIndex(SpereApp.APP_STAR)));
					app.setVersion(cursor.getString(cursor.getColumnIndex(SpereApp.EXT_COLUMN_2)));
					app.setAppSize(cursor.getString(cursor.getColumnIndex(SpereApp.APP_SIZE)));
					list.add(app);
					cursor.moveToNext();
				}
			}

		} catch (Exception e) {
			ContentManagerLog.d(TAG, "can not query spere app list.");
			e.printStackTrace();
		}finally{
			if(cursor!=null && !cursor.isClosed()){
				cursor.close();
			}
		}
		return list;
	}

	public static RecommendsAppList queryUnFavoritesRecommendsAppListByCursor(Context context) {
		return getAllRecommendsAppListByCursor(context, null ,"_id DESC");
	}
	
	public static boolean hasUnFavoritesRecommends(Context context){
		boolean has = false;
		RecommendsAppList list = queryUnFavoritesRecommendsAppListByCursor(context);
		if(list!=null && !list.isEmpty()){
			has = true;
		}
		return has;
	}
	
	private static UpgradeAppList getUpgradeAppList(Context context, UpgradeAppList list) {
		UpgradeAppList newList = new UpgradeAppList();
		UpgradeAppList dbList = queryUpgradeAppListByCursor(context);
		if(dbList != null && !dbList.isEmpty()) {
			for (UpgradeApp app : list.getAppStoreList()) {
				boolean isContain = dbList.isContain(app);
				if(!isContain) {
					newList.add(app);
				}
			}
		} else {
			return list;
		}
		return newList;
	}

	private static UpgradeAppList queryUpgradeAppListByCursor(Context context){
		Cursor cursor = null;
		UpgradeAppList list = new UpgradeAppList();
		try{
			cursor = context.getContentResolver().query(LocalApp.CONENT_LOCAL_URI, null, null, null, null);
			if(cursor != null && cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					UpgradeApp app = new UpgradeApp();
					app.setPackageName(cursor.getString(cursor.getColumnIndex(LocalApp.PACKAGE_NAME)));
					app.setVersionCode(cursor.getString(cursor.getColumnIndex(LocalApp.VERSION_CODE)));
					list.add(app);
					cursor.moveToNext();
				}
			}
		}catch(Exception e){
			ContentManagerLog.d(TAG, "can not query local db.");
			e.printStackTrace();
		}finally{
			if(cursor!=null && !cursor.isClosed()){
				cursor.close();
			}
		}
		
		return list;
	}

	public static boolean isEmptySpereList(Context context) {
		int size = 0;
		Cursor cursor = null;
		try{
			cursor = context.getContentResolver().query(SpereApp.CONTENT_SPERE_URI, null, null, null, null);
			if(cursor != null) {
				size = cursor.getCount();
				cursor.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(cursor!=null && !cursor.isClosed()){
				cursor.close();
			}
		}
		return size == 0 ? true : false;
	}
	
	private static int queryUnFavoritesSize(Context context){
		return  queryUnFavoritesAppsSize(context);
	}
	
	public static int queryUnFavoritesAppsSize(Context context) {
		Cursor cursor = null;
		int count = 0;
		String selection = SpereApp.APP_FAVORITES + "=0";
		try{
			cursor = context.getContentResolver().query(SpereApp.CONTENT_SPERE_URI, null, selection, null, null);
			if(cursor != null && cursor.moveToFirst()) {
				count = cursor.getCount();
			}
		}catch(Exception e){
			ContentManagerLog.d(TAG, "can not query favorites app size ");
			e.printStackTrace();
		}finally{
			if(cursor!=null && !cursor.isClosed()){
				cursor.close();
			}
		}
		return count;
	}

	/**
	 * @author add by chenyong 2011-02-14
	 */
	public static RecommendsAppList queryFavoritesApps(Context context) {
		Cursor cursor = null;
		String selection = SpereApp.APP_FAVORITES + "!=0";
		RecommendsAppList list = new RecommendsAppList();
		try{
			cursor = context.getContentResolver().query(SpereApp.CONTENT_SPERE_URI, null, selection, null, null);
			if(cursor != null && cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					ReCommendsApp app = new ReCommendsApp();
					app.setPackageName(cursor.getString(cursor.getColumnIndex(SpereApp.PACKAGE_NAME)));
					app.setVersionCode(cursor.getString(cursor.getColumnIndex(SpereApp.VERSION_CODE)));
					app.setDelete(cursor.getString(cursor.getColumnIndex(SpereApp.APP_DELETE)));
					app.setCollect(cursor.getString(cursor.getColumnIndex(SpereApp.APP_COLLECT)));
					app.setIconAddress(cursor.getString(cursor.getColumnIndex(SpereApp.ICON_ADDRESS)));
					app.setAppName(cursor.getString(cursor.getColumnIndex(SpereApp.APP_NAME)));
					app.setLcaid(cursor.getString(cursor.getColumnIndex(SpereApp.APP_LOCAL_ID)));
					app.setDownloadCount(cursor.getString(cursor.getColumnIndex(SpereApp.APP_DOWNLOAD_COUNT)));
					app.setStartLevel(cursor.getString(cursor.getColumnIndex(SpereApp.APP_STAR)));
					list.add(app);
					cursor.moveToNext();
				}
			}
		}catch(Exception e){
			ContentManagerLog.d(TAG, "Can not query favorites app");
			e.printStackTrace();
		}finally{
			if(cursor!=null && !cursor.isClosed()){
				cursor.close();
			}
		}
		return list;
	}

	/**
	 * @author add by chenyong 2011-03-14
	 */
	public static ReCommendsApp queryApp(Context context, String packageName, String versionCode) {
		String whereStr = SpereApp.PACKAGE_NAME + " LIKE '" + packageName + "' AND " + //
				SpereApp.VERSION_CODE + " LIKE '" + versionCode + "'";
		Cursor cursor  = null;
		ReCommendsApp app = null;
		try{
			cursor = context.getContentResolver().query(SpereApp.CONTENT_SPERE_URI, null, whereStr, null, null);
			if(cursor != null) {
				if(cursor.moveToFirst()) {
					app = new ReCommendsApp();
					app.setAppName(cursor.getString(cursor.getColumnIndex(SpereApp.APP_NAME)));
					app.setPackageName(cursor.getString(cursor.getColumnIndex(SpereApp.PACKAGE_NAME)));
					app.setVersionCode(cursor.getString(cursor.getColumnIndex(SpereApp.VERSION_CODE)));
					app.setIconAddress(cursor.getString(cursor.getColumnIndex(SpereApp.ICON_ADDRESS)));
					app.setLcaid(cursor.getString(cursor.getColumnIndex(SpereApp.APP_LOCAL_ID)));
					app.setDelete(cursor.getString(cursor.getColumnIndex(SpereApp.APP_DELETE)));
					app.setCollect(cursor.getString(cursor.getColumnIndex(SpereApp.APP_COLLECT)));
					app.setFavorites(cursor.getString(cursor.getColumnIndex(SpereApp.APP_FAVORITES)));
					app.setDownloadCount(cursor.getString(cursor.getColumnIndex(SpereApp.APP_DOWNLOAD_COUNT)));
					app.setStartLevel(cursor.getString(cursor.getColumnIndex(SpereApp.APP_STAR)));
				}
			}
		}catch(Exception e){
			ContentManagerLog.d(TAG, "Can not query app .");
			e.printStackTrace();
		}finally{
			if(cursor!=null && !cursor.isClosed()){
				cursor.close();
			}
		}
		return app;
	}
	
	/**
	 * @author add by chenyong 2011-05-15
	 */
	public static boolean hasFavoritesApp(Context context) {
		String selection = SpereApp.APP_FAVORITES + "!=0";
		Cursor cursor = context.getContentResolver().query(SpereApp.CONTENT_SPERE_URI, null, selection, null, null);
		
		boolean b = false;
		if(cursor != null) {
			b = cursor.getCount() > 0;
			cursor.close();
		}
		
		return b;
	}

	/**
	 * @author add by chenyong 2011-02-21
	 */
	public static int addFavoritesApp(Context context, String packageName, String versionCode) {
		ContentValues values = new ContentValues();
		values.put(SpereApp.APP_FAVORITES, 1);
		String whereStr = SpereApp.PACKAGE_NAME + " LIKE '" + packageName + "' AND " + SpereApp.VERSION_CODE + " LIKE '" + versionCode + "'";
		return context.getContentResolver().update(SpereApp.CONTENT_SPERE_URI, values, whereStr, null);
	}

	/**
	 * @author add by chenyong 2011-02-21
	 */
	public static void deleteFavoritesApp(Context context, String packageName, String versionCode) {
		ContentValues values = new ContentValues();
		values.put(SpereApp.APP_FAVORITES, 0);
		String whereStr = SpereApp.PACKAGE_NAME + " LIKE '" + packageName + "' AND " + SpereApp.VERSION_CODE + " LIKE '" + versionCode + "'";
		context.getContentResolver().update(SpereApp.CONTENT_SPERE_URI, values, whereStr, null);

	}

	/**
	 * @author add by chenyong 2011-02-21
	 */
	public static void deleteSpereApp(Context context, String packageName, String versionCode) {
		ContentValues values = new ContentValues();
		values.put(SpereApp.APP_FAVORITES, 0);
		String whereStr = SpereApp.PACKAGE_NAME + " LIKE '" + packageName + "' AND " + SpereApp.VERSION_CODE + " LIKE '" + versionCode + "'";
		context.getContentResolver().delete(SpereApp.CONTENT_SPERE_URI, whereStr,null);

	}
	
	/**
	 * @author add by chenyong 2011-02-27
	 */
	public static boolean isFavoritesApp(Context context, String packageName, String versionCode) {
		String whereStr = SpereApp.PACKAGE_NAME + " LIKE '" + packageName + "' AND " + //
				SpereApp.VERSION_CODE + " LIKE '" + versionCode + "' AND " + //
				SpereApp.APP_FAVORITES + "!=0";
		Cursor cursor  = null;
		boolean b = false;
		try{
			cursor = context.getContentResolver().query(SpereApp.CONTENT_SPERE_URI, null, whereStr, null, null);
			if(cursor == null) return false;
			if(cursor.getCount() > 0) b = true;
		}catch(Exception e){
			ContentManagerLog.d(TAG, "Can not query favorites app.");
			e.printStackTrace();
		}finally{
			if(cursor!=null && !cursor.isClosed()){
				cursor.close();
			}
		}
		return b;
	}
	
	public synchronized static boolean isHWSettingEmpty(Context context,String whereStr){
		boolean isEmpty = false;
		Cursor cursor = null;
		try{
			cursor = context.getContentResolver().query(HwSetting.CONENT_SETTING_URI, null, whereStr, null, null);
			if(cursor==null || cursor.getCount()==0){
				isEmpty = true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(cursor!=null && !cursor.isClosed()){
				cursor.close();
			}
		}
		return isEmpty;
	}

	public synchronized static HashMap<String, String> getSpereAndUpgradeSwitch(Context context) {
		HashMap<String, String> resultMap = null;
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(HwSetting.CONENT_SETTING_URI, null,
					"action='set_launcher_upgrade' OR action='set_spere_switch'", null, null);
			if(cursor != null && cursor.moveToFirst()) {
				resultMap = new HashMap<String, String>();
				while (!cursor.isAfterLast()) {
					String action = cursor.getString(cursor.getColumnIndex("action"));
					String value = cursor.getString(cursor.getColumnIndex("value"));
					ContentManagerLog.d(TAG, "getSpereAndUpgradeSwitch >> action >> action : " + action + "; value : " + value);
					if("set_launcher_upgrade".equals(action)) {

						resultMap.put("set_upgrade_switch", value);

					} else if("set_spere_switch".equals(action)) {

						resultMap.put("set_spere_switch", value);
					}
					cursor.moveToNext();
				}
			}
		} catch (Exception e) {
			ContentManagerLog.d(TAG, "get spere switch error : " + e);
			e.printStackTrace();
		} finally {
			if(cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return resultMap;
	}
}
