package com.lenovo.launcher2.bootpolicy;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;
import com.lenovo.launcher.components.XAllAppFace.XLauncher;
import com.lenovo.launcher2.backup.BackupManager;
import com.lenovo.launcher2.customizer.ConstantAdapter;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.ConstantAdapter.OperationState;
import com.lenovo.senior.utilities.Utilities;

public final class LoadBootPolicy {
	private static final String TAG = "liuyg1";
	private static boolean mDefautProfileProcessing = true;
//	private static final int INVALID_INDEX = -1;
//	private static final String IMAGE_FORMAT = ConstantAdapter.SUFFIX_FOR_PREVIEW_SNAPSHOT;
//	private Bitmap mPreview;
//	private Bitmap mWallpaper;
	private Context mContext;
//	private static final File PREVIEW_DIR = new File(ConstantAdapter.LOCAL_DATA_FILE_PATH_TO_BACK_UP
//			+ ConstantAdapter.PROFILE_SNAPSHOT_STORAGE_PATH);
//	private static final String PREVIEW_IMAGE = "profile_preview";
	public static LoadBootPolicy getInstance(Context context) {

		return new LoadBootPolicy(context);
	}
	private LoadBootPolicy(Context context) {
		mContext = context;
	}
	public boolean getDefaultProfileProcessingState(){
		synchronized (this) {
			return mDefautProfileProcessing;
		}
		
	}
	public void setDefaultProfileProcessingState(boolean state){
		synchronized (this) {
			mDefautProfileProcessing = state;
		}
	}
	public boolean loadFactoryProfile(boolean updateLoad) {
		com.lenovo.launcher2.backup.BackupManager bm =
				com.lenovo.launcher2.backup.BackupManager.getInstance(mContext.getApplicationContext());
		byte res = bm.performDefaultRestore(updateLoad);
		try {
			switch (res) {
			case OperationState.CRITICAL_DEFAULT_RESTORING_NEED_START:
			case OperationState.CRITICAL_DEFAULT_RESTORING_ALREADY_START:
				synchronized (this) {
					mDefautProfileProcessing = true;
				}
//				Dialog indicator = new Dialog(mContext,R.style.Theme_LeLauncher_LoadProgressDialog);
//				indicator.requestWindowFeature(Window.FEATURE_NO_TITLE);
//				indicator.setContentView(R.layout.boot_custom_progressdialog);
//				indicator.setCancelable(false);
//	    		Window window = indicator.getWindow();
//	    		window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//	    		window.setBackgroundDrawableResource(R.drawable.wallpaper_grass);
//	    		window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//	    		window.setGravity(Gravity.CENTER);
//	    		WindowManager.LayoutParams lp = window.getAttributes();
//	    		lp.dimAmount = 0.9f;
//	    		window.setAttributes(lp);
//				indicator.show();
				return true;

			case OperationState.CRITICAL_DEFAULT_RESTORING_NO_NEED_START:
				synchronized (this) {
					mDefautProfileProcessing = false;
				}
				if( !bm.isNotifiedInterrupt() ){
					bm.performInterruptedRestore();
				}

				break;
			default: return false;	
			}

			return false;

		} finally {
			bm = null;
			System.gc();
		}

	}
//	private XLauncher mLauncher;
	XLauncher getLauncher(Context context){
		try {
			LauncherApplication app = (LauncherApplication) context.getApplicationContext();
			return app.getModel().getCallBack().getLauncherInstance();
		} catch (Exception e) {
			// can not retrieve instance , app may gone
			return null;
		}
	}
//	public void backupAndRestoreDesktop(final Handler handler) {
//		Log.d("liuyg1","backupDesktopProfile");
//		mLauncher =getLauncher(mContext);
//		savePreviewSnap(); 
//		AsyncTask.execute(new Runnable() {
//			public void run() {
//
//				com.lenovo.launcher2.backup.BackupManager bm =
//						com.lenovo.launcher2.backup.BackupManager.getInstance(mContext.getApplicationContext());
//				byte resBackup = bm.backup(mContext.getString(R.string.boot_backup_profile_name));
//    			Message msg = new Message();  
//    			if(resBackup == OperationState.SUCCESS){
//
//    				msg.what =BootPolicyConstant.BACKUP_PROFILE_SUCCESS;
//    			}else{
//    				msg.what = BootPolicyConstant.BACKUP_PROFILE_FAIL;
//    			}
//
//				byte resRestore = bm.performDefaultRestore(true);
//				switch (resRestore) {
//				case OperationState.CRITICAL_DEFAULT_RESTORING_NEED_START:
//				case OperationState.CRITICAL_DEFAULT_RESTORING_ALREADY_START:
//					mDefautProfileProcessing = true;
//					handler.sendMessage(msg);
//					break;
//				case OperationState.CRITICAL_DEFAULT_RESTORING_NO_NEED_START:
//					mDefautProfileProcessing = false;
//
//					if( !bm.isNotifiedInterrupt() )
//						bm.performInterruptedRestore();
//					BootPolicyConstant.recordVersion(mContext);
//					mLauncher.restartLauncher();
//					break;
//				}
//
//
//
//			}
//
//
//		});
//
//
//	}
//	private void savePreviewSnap() {
//		// TODO Auto-generated method stub
//		ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
//
//		int dragWidth = (int)mLauncher.getWorkspace().getWidth();
//		double width = mLauncher.getResources().getDimension(R.dimen.profile_setting_thumbnails_width);
//		float scale = (float) (width / dragWidth);
//		bitmaps.add(mLauncher.getDragLayer().getSnapshot(scale));
//		bitmaps.add(getViewBitmap(mLauncher.getXLauncherView(), scale));
//		saveCurrentPageThumb(bitmaps);
//
//	}
	/**
	 * Draw the view into a bitmap.
	 * @param width
	 */
//	private Bitmap getViewBitmap(View v, float scale) {
//		v.clearFocus();
//		v.setPressed(false);
//
//		boolean willNotCache = v.willNotCacheDrawing();
//		v.setWillNotCacheDrawing(false);
//
//		// Reset the drawing cache background color to fully transparent
//		// for the duration of this operation
//		int color = v.getDrawingCacheBackgroundColor();
//		v.setDrawingCacheBackgroundColor(0);
//		float alpha = v.getAlpha();
//		v.setAlpha(1.0f);
//
//		if (color != 0) {
//			v.destroyDrawingCache();
//		}
//		v.buildDrawingCache();
//		Bitmap cacheBitmap = v.getDrawingCache();
//		boolean rebuild = false;
//		if (cacheBitmap == null) {
//			/*** MODIFY BY:zhaoxy . DATE:2012-03-09 . START***/
//			//return null;
//			try {
//				Drawable temp = mLauncher.getResources().getDrawable(R.drawable.comunavailable);
//				cacheBitmap = drawableToBitmap(temp, temp.getIntrinsicWidth(), temp.getIntrinsicHeight());
//				double width =  mLauncher.getResources().getDimension(R.dimen.profile_setting_thumbnails_width);
//				scale = (float) (width / cacheBitmap.getWidth());
//				rebuild = true;
//			} catch (Throwable e) {
//				e.printStackTrace();
//			}
//		}
//
//		Matrix matrix = new Matrix();
//		matrix.postScale(scale, scale);
//
//		Bitmap bitmap = null;
//		try {
//			if (cacheBitmap != null) {
//				bitmap = Bitmap.createBitmap(cacheBitmap, 0, 0, cacheBitmap.getWidth(), cacheBitmap.getHeight(), matrix,
//						true);
//			}			
//		} catch (Throwable e) {
//			bitmap = null;
//			e.printStackTrace();
//		}
//
//		// Restore the view
//		v.destroyDrawingCache();
//		v.setAlpha(alpha);
//		v.setWillNotCacheDrawing(willNotCache);
//		v.setDrawingCacheBackgroundColor(color);
//
//		if (rebuild) {
//			recycle(cacheBitmap);
//		}
//		if (bitmap == null) {
//			try {
//				bitmap = drawableToBitmap( mLauncher.getResources().getDrawable(R.drawable.comunavailable), cacheBitmap.getWidth(), cacheBitmap.getHeight());
//			} catch (Throwable e) {
//				e.printStackTrace();
//			}
//		}
//		return bitmap;
//	}
	public void recycle(Bitmap b) {
		if (b != null && !b.isRecycled()) {
			b.recycle();
			//	            b = null;
		}

		System.gc();
	}
//	private Bitmap saveCurrentPageThumb(ArrayList<Bitmap> bitmaps) {
//		Log.i(TAG, "saveCurrentPageThumb = " + bitmaps.size());
//		if (mLauncher == null) {
//			Log.w(TAG, "cannot backup, because we cannot get launcher instance ... ");
//			return null;
//		}
//
//		// save the draglayer thumb nail first
//		Resources res = mLauncher.getResources();
//		//	        final DragLayer draglayer = mLauncher.getDragLayer();
//		WallpaperManager wm = WallpaperManager.getInstance(mLauncher);
//
//		//	        int dragWidth = draglayer.getWidth();
//		int dragWidth = (int)mLauncher.getWorkspace().getWidth();
//		double width = 300.0;
//		float scale = (float) (width / dragWidth);
//
//		Bitmap preview = null;
//		if (!getWallpaper(wm, scale)) {
//			Log.i(TAG, "cannot retrieve wallpaper !!! ");
//		} else {
//			Bitmap layerBitmap = bitmaps.get(0);
//			Drawable[] array = new Drawable[3];
//			array[0] = new BitmapDrawable(res, mWallpaper);
//			array[1] = new BitmapDrawable(res, layerBitmap);
//			array[2] = new BitmapDrawable(res, bitmaps.get(1));
//
//			LayerDrawable ld = new LayerDrawable(array);
//			ld.setLayerInset(0, 0, 0, 0, 0);
//			ld.setLayerInset(1, 0, 0, 0, 0);
//			preview = drawableToBitmap(ld, ld.getIntrinsicWidth(), ld.getIntrinsicHeight());
//
//			savePreviewBitmap(preview, INVALID_INDEX);
//		}
//
//		return preview;
//	}

//	private boolean savePreviewBitmap(Bitmap bitmap, int index) {
//		Log.i(TAG, "savePreviewBitmap" + bitmap + "    and index = " + index);
//		if (bitmap == null)
//			return false;
//
//		try {
//			if (!PREVIEW_DIR.exists()) {
//				PREVIEW_DIR.mkdirs();
//			}
//			String fileName = PREVIEW_IMAGE;
//			if (index != INVALID_INDEX)
//				fileName += String.valueOf(index) + IMAGE_FORMAT;
//			else
//				fileName = ConstantAdapter.PROFILE_SNAPSHOT_PREVIEW_NAME;
//			File f = new File(PREVIEW_DIR, fileName);
//			ParcelFileDescriptor fd = ParcelFileDescriptor.open(f, ParcelFileDescriptor.MODE_CREATE
//					| ParcelFileDescriptor.MODE_READ_WRITE);
//			if (fd == null) {
//				return false; // create or open fail
//			}
//			FileOutputStream fos = null;
//			try {
//				fos = new ParcelFileDescriptor.AutoCloseOutputStream(fd);
//				bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
//			} finally {
//				if (fos != null) {
//					fos.close();
//				}
//			}
//
//			return true;
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		return false;
//	}
//	private boolean getWallpaper(WallpaperManager wm, float scale) {
//		if (mWallpaper != null && !mWallpaper.isRecycled()) {
//			return true;
//		}
//		Bitmap wallpaperBmp = wm.getBitmap();
//
//		// check the drawable is avaid
//		if (wallpaperBmp == null || wallpaperBmp.isRecycled()) {
//			Log.w(TAG, "the bitmap of wallpaper we got is recycle or null");
//			return false;
//		}
//
//		Rect rect = new Rect();
//		mLauncher.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
//		int statusBarHeight = rect.top;
//
//		// for navigation bar height
//		Resources res = mLauncher.getResources();//launcher.getResources();
//		boolean hasNavigationBar = res.getBoolean(com.android.internal.R.bool.config_showNavigationBar);
//
//		// if we cannot retrieve navigation configuration,
//		// maybe there is an error about framework.jar. so change a way to try.
//		if (!hasNavigationBar) {
//			try {
//				Class<?> c = Class.forName("com.android.internal.R$bool");
//				Field f = c.getField("config_showNavigationBar");
//				int resid = f.getInt(null);
//
//				hasNavigationBar = mLauncher.getResources().getBoolean(resid);
//				Log.v(TAG, " res = " + hasNavigationBar);
//
//			} catch (Exception e) {
//			}
//		}
//
//		// Allow a system property to override this. Used by the emulator.
//		// See also hasNavigationBar().
//		String navBarOverride = SystemProperties.get("qemu.hw.mainkeys");
//		if (!"".equals(navBarOverride)) {
//			if (navBarOverride.equals("1"))
//				hasNavigationBar = false;
//			else if (navBarOverride.equals("0"))
//				hasNavigationBar = true;
//		}
//		int navigationBarHeight = hasNavigationBar ? res
//				.getDimensionPixelSize(com.android.internal.R.dimen.navigation_bar_height) : 0;
//
//				int height = wallpaperBmp.getHeight() - statusBarHeight - navigationBarHeight;
//
//				int width = wallpaperBmp.getWidth();
//				//	        width = width > w ? w : width;
//
//				Matrix matrix = new Matrix();
//				matrix.postScale(300.0f / width, scale);
//
//				/** fix bug: Bug 166400   AUT: chengliang  DATE: 06.26   S*/
//				try {
//					XWorkspace workspace = mLauncher.getWorkspace();
////					int currentPage = mLauncher.getCurrentWorkspaceScreen();
//					//				CellLayout cell = (CellLayout) workspace.getChildAt(currentPage);
//					//				int cellWidth = cell.getWidth();
//					int cellWidth = (int)workspace.getWidth();
//
//					SharedPreferences prf = PreferenceManager.getDefaultSharedPreferences(mLauncher);
//					float beginX = prf.getFloat( "wallpaper_offset_x", 0);
//
//					//				int beginX = (int) workspace.getWorkspaceWallpaperOffsetX();
//
//					mWallpaper = Bitmap.createBitmap(wallpaperBmp, (int) beginX, 0, 
//							cellWidth - 1, height, matrix, true);
//				} catch (Exception e) {
//					mWallpaper = Bitmap.createBitmap(wallpaperBmp, 0, 0, 480, height, matrix, true);
//				}
//
//				return true;
//	}
//	private Bitmap drawableToBitmap(Drawable drawable, int w, int h) {
//		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
//				: Bitmap.Config.RGB_565;
//		// create bitmap
//		Bitmap bitmap = Bitmap.createBitmap(w, h, config);
//		// create canvas of bitmap
//		Canvas canvas = new Canvas(bitmap);
//		drawable.setBounds(0, 0, w, h);
//		// draw
//		drawable.draw(canvas);
//		return bitmap;
//	}
	public boolean showUpdateExperiencePolicyDialog(Handler handler){
		final SharedPreferences pref = mContext.getSharedPreferences(
				ConstantAdapter.PREF_FIRST_LAUNCH_CHECK_NAME,
				Activity.MODE_APPEND | Activity.MODE_PRIVATE);
		final SharedPreferences pref_loading = mContext.getSharedPreferences(
				ConstantAdapter.PREF_FIRST_LOADING_DEFAULT_FILE_NAME,
				Activity.MODE_APPEND | Activity.MODE_PRIVATE);
		if (!pref.getBoolean(ConstantAdapter.PREF_FIRST_LAUNCH_CHECK_KEY,
				true)){
			if(checkupdateverion()) { 
////				Log.d(TAG,"oncreate "+"showUpdateExperiencePolicyDialog");
//				mDefautProfileProcessing = true;
//				Dialog mLeosDialog = new UpdateRemindDialog().createDialog(mContext,handler);
//				mLeosDialog.show();
////				Log.d(TAG,"UpdateRemindDialog().createDialog()");
//				return true;
				synchronized (this) {
					mDefautProfileProcessing = true;
				}
				BootPolicyUtility.recordVersion(mContext);
		    	SettingsValue.setFirstLoadLbk(mContext);
				pref_loading.edit().putBoolean(ConstantAdapter.PREF_FIRST_LOADING_DEFAULT_FILE_KEY, false).commit();
				Message msg1 = new Message();  
				msg1.what = BootPolicyUtility.UPDATE_PROFILE_START;
				handler.sendMessage(msg1);
				Log.d(TAG, "loadprofile");
				return true;
			}
		}
    	synchronized (this) {
    		mDefautProfileProcessing = false;
		}
		return false;

	}
	
	private void favorToLoadImageLib(){
		try {
			System.loadLibrary("ImgFun");
		} catch (UnsatisfiedLinkError e) {
			String path = "/data/data/com.lenovo.launcher/files/libImgFun.so";
			new Utilities().copyAssets( mContext, "managedlib/libImgFun.so",
					path);
			System.load(path);
		}
	}
	
	public boolean showFirstExperiencePolicyDialog(Handler handler){
		
		favorToLoadImageLib();
	    
		final SharedPreferences pref = mContext.getSharedPreferences(
				ConstantAdapter.PREF_FIRST_LAUNCH_CHECK_NAME,
				Activity.MODE_APPEND | Activity.MODE_PRIVATE);
		final SharedPreferences pref_loading = mContext.getSharedPreferences(
				ConstantAdapter.PREF_FIRST_LOADING_DEFAULT_FILE_NAME,
				Activity.MODE_APPEND | Activity.MODE_PRIVATE);
		String desFile = "//data//data//com.lenovo.launcher//shared_prefs//first_check.xml";
		
		File prefFile = new File(desFile);
		if(!prefFile.exists()&&!pref.getBoolean(ConstantAdapter.PREF_FIRST_LAUNCH_CHECK_KEY,
				true)){
			Log.d("liuyg123","desFile !exist and first_check false");
			BackupManager.getInstance(mContext).reLaunch();
		
		}

		if(pref_loading.getBoolean(ConstantAdapter.PREF_FIRST_LOADING_DEFAULT_FILE_KEY,
				false)||pref.getBoolean(ConstantAdapter.PREF_FIRST_LAUNCH_CHECK_KEY,
						true)){
	    	SettingsValue.setFirstLoadLbk(mContext);
	    	synchronized (this) {
	    		mDefautProfileProcessing = true;
			}
			BootPolicyUtility.recordVersion(mContext);
			pref_loading.edit().putBoolean(ConstantAdapter.PREF_FIRST_LOADING_DEFAULT_FILE_KEY, false).commit();
			Message msg1 = new Message();  
			msg1.what = BootPolicyUtility.UPDATE_PROFILE_START;
			handler.sendMessage(msg1);
			return true;
		}
//		else if (pref.getBoolean(ConstantAdapter.PREF_FIRST_LAUNCH_CHECK_KEY,
//				true)){
//			mDefautProfileProcessing = true;
//			Dialog dialog = new FirstLoadRemindDialog().createDialog(mContext,handler);
//			dialog.show();
//			//			BootPolicyUtility.recordVersion(mContext);
//			//			Message msg1 = new Message();  
//			//			msg1.what = BootPolicyUtility.RESTORE_DEFAULT_PROFILE;
//			//			handler.sendMessage(msg1);
//			//			Log.d(TAG,"mHandler.sendMessage  BACKUP_PROFILE_START");
//			//			Log.d(TAG,"FirstLoadRemindDialog().createDialog()");
//			return true;
//		}
    	synchronized (this) {
    		mDefautProfileProcessing = false;
		}
		return false;
	}
	
	private  boolean checkupdateverion() {
//		Log.d(TAG,"checkupdateverion");
		SharedPreferences mPref = null;
		mPref = mContext.getSharedPreferences(ConstantAdapter.PREF_VERSION_LAUNCH_OLD_NAME,
				Activity.MODE_APPEND | Activity.MODE_PRIVATE);

		String appVersion = "1.0";
		PackageManager manager = mContext.getPackageManager();
		try { PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
		appVersion = info.versionName;   //版本名
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
//		Log.d(TAG,"appVersion"+appVersion);
		String oldVersion = mPref.getString(ConstantAdapter.PREF_VERSION_LAUNCH_OLD_KEY, "");
		Log.d(TAG,"oldVersion"+oldVersion);
		if(oldVersion.equals("")){
			return true;
		}
		else if(oldVersion.equals(appVersion)){
			return false;
		}
		else if(oldVersion.startsWith("v3.6")){

			if(appVersion!=null&&!appVersion.equals("")){
				mPref.edit().putBoolean(ConstantAdapter.EXCLUDED_SETTING_KEY, true).putString(ConstantAdapter.PREF_VERSION_LAUNCH_OLD_KEY, appVersion).commit();
			}

			return false;
		} 
		return true;
	}
//	private static Dialog mBakcupProgressDlg;
//    public Dialog getBackupProgressDialog() {
//    	if (mBakcupProgressDlg != null && mBakcupProgressDlg.isShowing()) {
//    		Log.d(TAG, "showProgressDialog, dialog is showing1 ... ");
//    	}else {
//    		Log.d(TAG, "showProgressDialog, dialog is showing2 ... ");
//    		mBakcupProgressDlg = new Dialog(mContext,R.style.Theme_LeLauncher_BackupProgressDialog);
////    		mbakcupProgessLayout = (LinearLayout) mBakcupProgressDlg.getLayoutInflater().inflate(R.layout.boot_backup_desktop_progressbar, null);
//    		mBakcupProgressDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
//    		mBakcupProgressDlg.setContentView(R.layout.boot_backup_desktop_progressbar);
//
//    		Window window = mBakcupProgressDlg.getWindow();
//    		window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//    		window.setBackgroundDrawableResource(R.drawable.wallpaper_grass);
//    		window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//    		window.setGravity(Gravity.CENTER);
//    		WindowManager.LayoutParams lp = window.getAttributes();
//    		lp.dimAmount = 0.5f;
//    		window.setAttributes(lp);
//
//    		mBakcupProgressDlg.setCancelable(false);
//    	}
//    	if (mBakcupProgressDlg.isShowing()) {
//    		
//    		return mBakcupProgressDlg;
//    	}
//    	mBakcupProgressDlg.show();
//    	Log.d(TAG, "showProgressDialog, dialog is showing3 ... ");
//    	return mBakcupProgressDlg;
//    }

    public void showLoadProgressDialog() {
    	Dialog loadProgressDlg;
    	loadProgressDlg = new Dialog(mContext,R.style.Theme_LeLauncher_LoadProgressDialog);
		loadProgressDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
		loadProgressDlg.setContentView(R.layout.boot_custom_progressdialog);
		loadProgressDlg.setCancelable(false);
		Window window = loadProgressDlg.getWindow();
		window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		// yangbin5 start Fix bug ACE-401
		String deviceModel = android.os.Build.MODEL;
        if (deviceModel.contains("A3500")){
        	
        }else {
    		window.setBackgroundDrawableResource(R.drawable.wallpaper_grass);
		}

		window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		window.setGravity(Gravity.CENTER);
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.dimAmount = 1.0f;
		// yangbin5 end
		window.setAttributes(lp);
		loadProgressDlg.show();
		
    }
	
}
