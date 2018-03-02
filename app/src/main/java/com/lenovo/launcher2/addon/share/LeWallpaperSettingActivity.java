package com.lenovo.launcher2.addon.share;

import java.io.IOException;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher.R;


/**
 * Author : Liuyg1@lenovo.com
 * */
public class LeWallpaperSettingActivity extends Activity {


	//	private static final String mimeType = "application/vnd.android.package-archive";
	public static final String LELAUNCHER_CLASS_NAME = "com.lenovo.launcher2.Launcher";
	private String mDestPath ;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_wallpaper_setting);
		Intent intent = getIntent();
		if(intent!=null&&intent.getData()!=null){
			String str = intent.getData().getEncodedPath();
			if(str!=null&&!str.equals("")){
				mDestPath = intent.getData().getEncodedPath();
			}else {
				mDestPath= "sdcard/wallpaper.png";
			}
		}else{
			mDestPath= "sdcard/wallpaper.png";
		}
		LinearLayout imageview = (LinearLayout) findViewById(R.id.imageView);
		final WallpaperManager wpm = (WallpaperManager)LeWallpaperSettingActivity.this.getSystemService(
				Context.WALLPAPER_SERVICE);
		final Bitmap imageBitmap = BitmapFactory.decodeFile(mDestPath) ;
		BitmapDrawable bd= new BitmapDrawable(imageBitmap); 
		imageview.setBackgroundDrawable(bd);

		View setBtn = (View) findViewById(R.id.btn_set);
		setBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//				progressBar.setVisibility(View.VISIBLE);
				//				loading_txt.setVisibility(View.VISIBLE);
				//				new Thread("SnapSavingThread") {
				//					public void run() {
				//
				//						try {
				//							wpm.setBitmap(imageBitmap);
				//						} catch (IOException e) {
				//							// TODO Auto-generated catch block
				//							e.printStackTrace();
				//						}
				//						finish();
				//					}
				//				}.start();
				Toast.makeText(LeWallpaperSettingActivity.this, R.string.setting_wallpaper, Toast.LENGTH_SHORT).show();
				try {

					wpm.setBitmap(imageBitmap);
				} catch (IOException e) {
					Toast.makeText(LeWallpaperSettingActivity.this, R.string.wallpaper_insall_fail_toast, Toast.LENGTH_SHORT).show();
					e.printStackTrace();
					return;

				}
				Toast.makeText(LeWallpaperSettingActivity.this,R.string.share_set_wallper, Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setClassName(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF,LELAUNCHER_CLASS_NAME);
				startActivity(intent);
				finish();

			}
		});
	}

}
	

