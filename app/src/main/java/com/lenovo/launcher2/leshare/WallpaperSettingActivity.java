package com.lenovo.launcher2.leshare;

import com.lenovo.launcher2.addon.share.LeWallpaperSettingActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class WallpaperSettingActivity extends Activity {
	private String TAG = "WallpaperSettingActivity";
	private String mDestPath ;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		startIntent();
	}
	
	private void startIntent(){
		Intent intent = getIntent();
		Log.d(TAG, "startIntent : "+intent);
		if(intent!=null&&intent.getData()!=null){
			Intent sendIntent = new Intent(this,LeWallpaperSettingActivity.class);
			sendIntent.setData(intent.getData());
			this.startActivity(sendIntent);
		}
		this.finish();
	}
	
}
