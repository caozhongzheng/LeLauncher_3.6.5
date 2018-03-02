package com.lenovo.launcher2.addon.share;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.customizer.Reaper;
import com.lenovo.launcher2.customizer.Utilities;
import com.lenovo.launcher2.customizer.SettingsValue;


/**
 * Author : Liuyg1@lenovo.com
 * */
public class LeShareWallpaperChooserActivity extends Activity {

//	private ProgressBar mProgressBar;
	private static final String mimeType = "image/*";
	private static final String mSharemimeType = "application/vnd.android.package-archive";
	String mDestPath = Environment.getExternalStorageDirectory()+ "/.IdeaDesktop/";
	String fileName = "wallpaper.png";
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.share_wallpaper_chooser);

		//test by dining add custom title
		TextView title = (TextView)findViewById(R.id.dialog_title);
		title.setText(R.string.desktop_share_wallpaper_text);
		ImageView icon = (ImageView)findViewById(R.id.dialog_icon);
		icon.setImageResource(R.drawable.dialog_title_back_arrow);
		
		//test by dining
				
		icon.setOnClickListener(new View.OnClickListener() {

		            @Override
		            public void onClick(View view) {
		                finish();
		            }
		        });
				
		WallpaperManager wallpaperManager = WallpaperManager  
				.getInstance(this);  
		LinearLayout imageview = (LinearLayout) findViewById(R.id.imageView);
		final Drawable wallpaper = wallpaperManager.getDrawable();
//		mProgressBar = (ProgressBar) imageview.findViewById(R.id.progressing);
//		final TextView loading_txt = (TextView) imageview.findViewById(R.id.loading_text);
		imageview.setBackgroundDrawable(wallpaper);
		new Thread("SnapSavingThread") {
			public void run() {
				BitmapDrawable bd = (BitmapDrawable)wallpaper;
				Bitmap wallpaperBmp = bd.getBitmap();
				Utilities.newInstance().ensureDir(mDestPath);
				Utilities.newInstance().saveBitmapToPng(mDestPath+fileName, wallpaperBmp);

			}
		}.start();
		final Button leSendBtn = (Button) findViewById(R.id.addfinish);//btn_leshare
		leSendBtn.setText(R.string.send_leshare);
		leSendBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!LeShareUtils.isInstalledQiezi(LeShareWallpaperChooserActivity.this)){
					LeShareUtils.showInstallDialog(LeShareWallpaperChooserActivity.this,false);
					return;
				}else if(!LeShareUtils.isInstalledRightQiezi(LeShareWallpaperChooserActivity.this)){
					LeShareUtils.showInstallDialog(LeShareWallpaperChooserActivity.this,true);
					return;
				}
				Log.d("liuyg1", "start clicked...");
				long currentTime = System.currentTimeMillis();
				//leSendBtn.setBackgroundResource(R.drawable.button_press);
				leSendBtn.setClickable(false);
				Intent shareIntent = new Intent(Intent.ACTION_SEND);
				Uri uri = Uri.fromFile(new File(mDestPath+fileName));
				shareIntent.setType(mSharemimeType);
				shareIntent.setPackage("com.lenovo.anyshare");
				
//				ComponentName  mComponentName = new ComponentName("com.lenovo.anyshare", "com.lenovo.anyshare.apexpress.ApDiscoverActivity"); 
//				shareIntent.setComponent(mComponentName);
        		PackageManager pm = getPackageManager();
        		ResolveInfo reInfo = pm.resolveActivity(shareIntent, 0);
        		if(reInfo!=null){
        			shareIntent.setClassName(reInfo.activityInfo.packageName, reInfo.activityInfo.name);
        		}
        		shareIntent.setDataAndType(uri, "image/wallpaper");
				shareIntent.putExtra(Intent.EXTRA_STREAM,uri);
				shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
				try{
					startActivity(shareIntent);
				}catch(Exception e){
					Toast.makeText(LeShareWallpaperChooserActivity.this, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
				}
				Reaper.processReaper(LeShareWallpaperChooserActivity.this, 
                        "Share", 
          				"ToPaperQiezi",
          				"1", 
          				Reaper.REAPER_NO_INT_VALUE );
				Log.d("liuyg1", "cost time : "+(System.currentTimeMillis() - currentTime));
				finish();

			}
		});
		Button otherSendBtn = (Button) findViewById(R.id.canceladd);//btn_other
		otherSendBtn.setText(R.string.send_othershare);
		otherSendBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread("SnapSavingThread") {
					public void run() {
						
						final Intent shareIntent = new Intent(Intent.ACTION_SEND);
						shareIntent.setType(mimeType);
						Bundle mBundle = new Bundle();
						shareIntent.putExtra("LAUNCHERBUNDLE",mBundle);
//						Uri muri = Uri.parse(new File(mDestPath+fileName).getPath());
						Uri uri = Uri.fromFile(new File(mDestPath+fileName));
						Log.d("liuyg1","muri="+uri.toString());
						shareIntent.putExtra(Intent.EXTRA_STREAM,uri);
						shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
						startActivity(shareIntent);
						Reaper.processReaper(LeShareWallpaperChooserActivity.this, 
		                        "Share", 
		          				"ToPaperOthers",
		          				"1", 
		          				Reaper.REAPER_NO_INT_VALUE );
						finish();
					}
				}.start();
			}
		});
		/*** RK_ID:RK_FONT AUT:zhanglz1@lenovo.com. DATE: 2013-02-21 S ***/
        Typeface tf = SettingsValue.getFontStyle(LeShareWallpaperChooserActivity.this);
		if (tf != null && tf !=otherSendBtn.getTypeface() ) {
			leSendBtn.setTypeface(tf);
			otherSendBtn.setTypeface(tf);
		}
		/*** RK_ID:RK_FONT AUT:zhanglz1@lenovo.com. DATE: 2013-02-21 E ***/
		if(!SettingsValue.isRotationEnabled(this)){
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

}
