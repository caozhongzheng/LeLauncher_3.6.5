package com.lenovo.lejingpin.net;

import java.io.File;
import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import android.os.IBinder;

import com.lenovo.lejingpin.net.CacheProvider.CacheFiles;

public class CacheCleanerService extends Service {
	public static final int LASTACCESSTIME = 10;
	public ArrayList<String> expiredDataList = new ArrayList<String>();
	public ArrayList<String> expiredFileList = new ArrayList<String>();
	public ArrayList<String> expiredPostdataList = new ArrayList<String>();

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		Cursor cursor = CacheUtils.query(this.getApplication()
				.getApplicationContext());
		long nowTime = System.currentTimeMillis();
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				int separatedTime = (int) (nowTime - Long.parseLong(cursor
						.getString(cursor
								.getColumnIndex(CacheFiles.LASTACCESSTIME))))
						/ 1000 / 60 / 60 / 24;
				if (separatedTime >= LASTACCESSTIME) {
					expiredDataList.add(cursor.getString(cursor
							.getColumnIndex(CacheFiles.URL)));
					expiredFileList.add(cursor.getString(cursor
							.getColumnIndex(CacheFiles.FILENAME)));
					expiredPostdataList.add(cursor.getString(cursor
							.getColumnIndex(CacheFiles.POSTDATA)));
				}
			} while (cursor.moveToNext());
		}
		if(cursor!=null){
			cursor.close();
		}
		if (expiredDataList.size() > 0
				&& Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
			for (int i = 0; i < expiredDataList.size(); i++) {
				CacheUtils.delete(this.getApplicationContext(),
						expiredDataList.get(i), expiredPostdataList.get(i));
				File file = new File(expiredFileList.get(i));
				if (file.exists()) {
					file.delete();
				}
			}
			expiredDataList.clear();
			expiredFileList.clear();
			expiredPostdataList.clear();
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
