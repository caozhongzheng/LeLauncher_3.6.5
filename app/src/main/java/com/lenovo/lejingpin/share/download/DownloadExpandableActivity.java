package com.lenovo.lejingpin.share.download;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.GZIPInputStream;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.CursorTreeAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.commonui.LeAlertDialog;

import com.lenovo.lejingpin.DetailClassicActivity;
import com.lenovo.lejingpin.ams.AmsRequest;
import com.lenovo.lejingpin.ams.AmsSession;
import com.lenovo.lejingpin.ams.GetImageRequest;
import com.lenovo.lejingpin.ams.AmsSession.AmsCallback;
import com.lenovo.lejingpin.appsmgr.content.UpgradeAppDownloadControl;
import com.lenovo.lejingpin.hw.lcapackageinstaller.utils.LcaInstallerUtils;
import com.lenovo.lejingpin.hw.lcapackageinstaller.utils.SignatureVerify;
import com.lenovo.lejingpin.hw.lcapackageinstaller.utils.XmlLcaInfoHandler;
import com.lenovo.lejingpin.hw.lcapackageinstaller.utils.XmlParser;
import com.lenovo.lejingpin.settings.LejingpingSettingsValues;
import com.lenovo.lejingpin.share.download.DownloadConstant;
import com.lenovo.lejingpin.share.download.DownloadInfo;
import com.lenovo.lejingpin.share.download.Downloads;
import com.lenovo.lejingpin.share.download.LDownloadManager;
import com.lenovo.lejingpin.share.util.Utilities;
import com.lenovo.lsf.util.PsDeviceInfo;


public class DownloadExpandableActivity extends ExpandableListActivity {
	//test by dining 2013-06-24 lejingpin->xlejingpin
	public static final Uri CONENT_DOWNLOAD_URI = Uri.parse("content://com.lenovo.lejingpin.share.download/download");
	public static final String ACTION_DOWNLOAD_DELETE = "com.lenovo.action.ACTION_DOWNLOAD_DELETE";
	private static final  String SP_THEME_LOCAL_PREVIEW = "download_themepreview_ur";
	private static final  String SP_WALLPAPER_LOCAL_PREVIEW = "download_preview_url";
	private static final String TAG = "DownloadExpandableActivity";
	private static final String SHARE_MIME_TYPE = "application/vnd.android.package-archive";
	
	private HashMap<String, Drawable> mImgcache = new HashMap<String, Drawable>();
//	private ArrayList<DownloadInfo> mDownloadList = new ArrayList<DownloadInfo>();
//	private ArrayList<DownloadInfo> mDownloadPauseList = new ArrayList<DownloadInfo>();
//	private ArrayList<DownloadInfo> mDownloadCompletedList = new ArrayList<DownloadInfo>();
	private DownloadExpandableListAdapter  mAdapter ;
	private ExpandableListView mListView ;
	private static Cursor groupCursor;
	private Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utilities.setDownloadActive(true, DownloadExpandableActivity.class.getName());
		mContext = this;
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        final ActionBar bar = getActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_USE_LOGO | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
//        int titleId = getResources().getSystem().getIdentifier(  
//                "action_bar_title", "id", "android");  
//		TextView titleView = (TextView) findViewById(titleId);  
//		titleView.setTextColor(Color.WHITE);
		
		Log.d(TAG, "DownloadExpandableActivity >>>>>>>>> onCreate");
		setContentView(R.layout.download_list_expandable);
		
		String selection = "status==? or status==? or status==?) GROUP BY (" + "status";
		String[] selectionArg = new String[] { "192","193","200" };	
//		String selection = "category=?) GROUP BY (" + "status";
		// get count group by status
		if(groupCursor != null && !groupCursor.isClosed()){
			groupCursor.close();
		}
		groupCursor = getContentResolver().query(CONENT_DOWNLOAD_URI, 
				new String[]{"_id","status" , "count(*)",Downloads.Impl.COLUMN_CONTROL,Downloads.Impl.COLUMN_PKGNAME}, 
				selection,
				selectionArg,
				null);

		mAdapter = new DownloadExpandableListAdapter(groupCursor,this);

		setListAdapter(mAdapter);
		//dismiss ExpandableListActivity's default icon on the left
		int width = getWindowManager().getDefaultDisplay().getWidth();
		mListView = getExpandableListView();
		mListView.setIndicatorBounds(width-40, width-10);
		
		mListView = getExpandableListView();
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	finish();
		return true;
    	
    }
	@Override
	protected void onStop() {
		Log.d(TAG, "DownloadExpandableActivity >>>>>>>>> onStop");
		super.onStop();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, "DownloadExpandableActivity >>>>>>>>> onStart");
	}
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "DownloadExpandableActivity >>>>>>>>> onResume");
		isPaused = false;
	}
	
	@Override
	protected void onPause() {
		isPaused = true;
		Log.d(TAG, "DownloadExpandableActivity >>>>>>>>> onPause");

		super.onPause();
	}
		@Override
	protected void onDestroy() {
		Log.d(TAG, "DownloadExpandableActivity >>>>>>>>> onDestroy");
//		mDownloadList.clear();
		mImgcache.clear();
//		groupCursor.close();
		Utilities.setDownloadActive(false, DownloadExpandableActivity.class.getName());
		Utilities.killLejingpinProcess();
		super.onDestroy();
	}

	boolean isPaused ;
	enum STATUS {
		RUNNING_PAUSE,
		RUNNING,
		PAUSE,
		COMPLETE
	}
	
	final class DownloadExpandableListAdapter extends CursorTreeAdapter{

		LayoutInflater inflater;
		Context mContext ;
		
		int DOWNLOAD_OPTION_ID_SHARE = 1;
		int DOWNLOAD_OPTION_ID_REDOWNLOAD = 2;
		int DOWNLOAD_OPTION_ID_DELETE = 3;
		
		private String mExpandItemPN = null;
		private int mExpandItemStatus = -1;
		//id , stringid , drawable id
		int[][] optionFun={
				{DOWNLOAD_OPTION_ID_SHARE,R.string.local_app_share,R.drawable.ic_local_apps_share_selector},
				{DOWNLOAD_OPTION_ID_REDOWNLOAD,R.string.download_app_redownload,R.drawable.ic_redownload_selector},
				{DOWNLOAD_OPTION_ID_DELETE,R.string.download_delete,R.drawable.ic_delete_selector}
		};
		
		boolean hasOptionView(String packageName , int status){
			if(mExpandItemPN != null && mExpandItemPN.equals(packageName)){
				if(status == mExpandItemStatus)
					return true;
				else{
					mExpandItemPN = null;
					mExpandItemStatus = -1;
				}
			}
			return false;
		}
		
		private void shareOnClick(String filePath , String pkname , int category){
			if(filePath == null || filePath.isEmpty())
				return;
			
			if(!com.lenovo.launcher2.addon.share.LeShareUtils.isInstalledQiezi(mContext)){
				com.lenovo.launcher2.addon.share.LeShareUtils.showInstallDialog(mContext,false);
        		return;
        	}else if(!com.lenovo.launcher2.addon.share.LeShareUtils.isInstalledRightQiezi(mContext)){
        		com.lenovo.launcher2.addon.share.LeShareUtils.showInstallDialog(mContext,true);
        		return;
        	}
			
			final Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType(SHARE_MIME_TYPE);
            Log.d(TAG,"shareOnClick pkname:" + pkname + ",category:" + category);
            try {
                File file = null;
            	if(DownloadConstant.CATEGORY_LENOVO_LCA == DownloadConstant.getInstallCategory(category)){
            		file = verifyPackage(filePath,pkname);

                    if(file == null){
                    	Toast.makeText(mContext, R.string.download_share_app_error, Toast.LENGTH_SHORT).show();
                    	return;
                    }
            	}else{
            		file = new File(filePath);
            	}
                Log.d(TAG,"shareOnClick file:" + file.getPath());
                
                Uri uri = Uri.fromFile(file);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(shareIntent);
            } catch (Exception e) {
            	Log.d(TAG,"shareOnClick Exception download");
             }
		}

		private void setOnClickListenerById(View tv,int id,final ChildHolder holder){
			if(DOWNLOAD_OPTION_ID_REDOWNLOAD == id){
				tv.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						DownloadInfo downloadInfo = new DownloadInfo(holder.pkgName,holder.versionCode);
						if( DownloadConstant.CONNECT_TYPE_MOBILE == DownloadConstant.getConnectType(mContext)
								&& Helpers.getwlanDownloadValue(mContext)){
							LejingpingSettingsValues.popupWlanDownloadDialog(mContext);
						}else{
							LDownloadManager.getDefaultInstance(mContext.getApplicationContext()).reDownloadTask(downloadInfo);
						}

					}
				});
			}
			if(DOWNLOAD_OPTION_ID_SHARE == id){
				tv.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						shareOnClick(holder.filePath, holder.pkgName ,holder.category);
					}
				});
			}
			if(DOWNLOAD_OPTION_ID_DELETE == id){
				tv.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						DownloadInfo downloadInfo = new DownloadInfo(holder.pkgName,holder.versionCode);
						LDownloadManager.getDefaultInstance(mContext.getApplicationContext()).deleteTask(downloadInfo);
						notifyDelete(holder.category,holder.pkgName, holder.versionCode, holder.name, DownloadConstant.ACTION_DOWNLOAD_DELETE);

					}
				});
			}
		}

		private void setOptionView(View view, ChildHolder holder){
			
			TextView fun1 = (TextView)view.findViewById(R.id.fun1);
			TextView fun2 = (TextView)view.findViewById(R.id.fun2);
			TextView fun3 = (TextView)view.findViewById(R.id.fun3);
			
			ArrayList<TextView> listTextView = new ArrayList<TextView>();
			listTextView.add(fun1);
			listTextView.add(fun2);
			listTextView.add(fun3);
			
			ArrayList<Integer> listOption = new ArrayList<Integer>();
			if(holder.status == Downloads.Impl.STATUS_SUCCESS)
				listOption.add(DOWNLOAD_OPTION_ID_SHARE);
			listOption.add(DOWNLOAD_OPTION_ID_REDOWNLOAD);
			listOption.add(DOWNLOAD_OPTION_ID_DELETE);
			
			int textViewWidth = 0;
			if(listOption.size() != 0){
				double temp = (double)320/listOption.size();
				textViewWidth = UpgradeAppDownloadControl.dip2px(mContext,(int)temp);
			}

			int w = UpgradeAppDownloadControl.dip2px(mContext,25);
			int h = UpgradeAppDownloadControl.dip2px(mContext,25);
			
			int textid = 0;
			for(int i=0 ; i<listOption.size() ; i++){
				int optionId = listOption.get(i);
				for(int j=0 ; j<optionFun.length; j++){
					if( (optionId == optionFun[j][0]) && (textid < listTextView.size())){
						TextView tv= listTextView.get(textid);
						tv.setVisibility(View.VISIBLE);
//						if(APPS_OPTION_ID_UPDATE_IGNORE == optionId 
//								&& app.upgradeApp != null && app.upgradeApp.getUpdateIgnore()){
//							tv.setText(R.string.local_app_update_remind);
//						}else{
							tv.setText(optionFun[j][1]);
//						}
							tv.getLayoutParams().width = textViewWidth;
						Drawable drawable = mContext.getResources().getDrawable(optionFun[j][2]);
						drawable.setBounds(0, 0, w, h); 
						tv.setCompoundDrawables(null, drawable, null, null);
						textid ++;
						
						setOnClickListenerById(tv,optionId,holder);
					}
				}
			}
			for(int k=textid; k<listTextView.size() ; k++){
				listTextView.get(k).setVisibility(View.GONE);
			}
			
			LinearLayout line1 = (LinearLayout)view.findViewById(R.id.download_app_item_option_1);
			LinearLayout line2 = (LinearLayout)view.findViewById(R.id.download_app_item_option_2);
			
			if(listOption.size() == 0){
				line1.setVisibility(View.GONE);
			}else{
				line1.setVisibility(View.VISIBLE);
			}
			if(listOption.size() < 4){
				line2.setVisibility(View.GONE);
			}else{
				line2.setVisibility(View.VISIBLE);
			}
		}

		public DownloadExpandableListAdapter(Cursor cursor, Context context) {
			super(cursor, context,true);
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mContext = context;
		}

		@Override
		protected Cursor getChildrenCursor(Cursor groupCursor) {
			int status = groupCursor.getInt(groupCursor.getColumnIndex(Downloads.Impl.COLUMN_STATUS));
			int contrl = groupCursor.getInt(groupCursor.getColumnIndex(Downloads.Impl.COLUMN_CONTROL));
			String pkname = groupCursor.getString(groupCursor.getColumnIndex(Downloads.Impl.COLUMN_PKGNAME));
			Log.d(TAG, "DownloadExpandableActivity ----------getChildrenCursor----- status:" + status + ",contrl:" + contrl + ",pkname:" + pkname);
//			int statusInt = Integer.parseInt(status);
			Cursor childCursor = null;
			switch (status) {
				case Downloads.Impl.STATUS_SUCCESS:
					childCursor = getContentResolver().query(CONENT_DOWNLOAD_URI, 
							new String[]{"_id","status","title","total_bytes","versioncode","pkgname","_data","uri","category",Downloads.Impl.COLUMN_CONTROL,Downloads.Impl.COLUMN_CURRENT_BYTES,Downloads.Impl.COLUMN_ICONADDR,Downloads.Impl.COLUMN_VERSIONNAME}, "status = ?", new String[]{String.valueOf(Downloads.STATUS_SUCCESS)}, null); 
					break;
				case Downloads.Impl.STATUS_RUNNING:
					childCursor = getContentResolver().query(CONENT_DOWNLOAD_URI, 
							new String[]{"_id","status","title","total_bytes","versioncode","pkgname","_data","uri","category",Downloads.Impl.COLUMN_CONTROL,Downloads.Impl.COLUMN_CURRENT_BYTES,Downloads.Impl.COLUMN_ICONADDR,Downloads.Impl.COLUMN_VERSIONNAME}, "status = ?", new String[]{String.valueOf(Downloads.STATUS_RUNNING)}, null);
					break;
				case Downloads.Impl.STATUS_RUNNING_PAUSED:
					childCursor = getContentResolver().query(CONENT_DOWNLOAD_URI, 
							new String[]{"_id","status","title","total_bytes","versioncode","pkgname","_data","uri","category",Downloads.Impl.COLUMN_CONTROL,Downloads.Impl.COLUMN_CURRENT_BYTES,Downloads.Impl.COLUMN_ICONADDR,Downloads.Impl.COLUMN_VERSIONNAME}, "status = ?", new String[]{String.valueOf(Downloads.STATUS_RUNNING_PAUSED)}, null);
					break;
				default:
					break;
			}
			if(childCursor != null)
				Log.d(TAG, "childCursor count : " + childCursor.getCount());
			
			return childCursor;
		}

		@Override
		protected View newGroupView(Context context, Cursor cursor,
				boolean isExpanded, ViewGroup parent) {
			View contentView = inflater.inflate(R.layout.download_list_item_expandable_group, parent,false);
			GroupHolder gh = new GroupHolder();
			gh.groupName = (TextView) contentView.findViewById(R.id.download_group_title);
			gh.groupButton = (TextView) contentView.findViewById(R.id.download_group_button);
			contentView.setTag(gh);
			mListView.setGroupIndicator(null);
			return contentView;
		}

		@Override
		protected void bindGroupView(View view, Context context, Cursor cursor,
				boolean isExpanded) {
			GroupHolder holder ;
			if(view.getTag() != null && view.getTag() instanceof GroupHolder){
				holder = (GroupHolder) view.getTag();
			}else{
				return;
			}
			int status = cursor.getInt(1);
			String count = cursor.getString(2);
			view.setVisibility(View.VISIBLE);
			switch (status) {
			case Downloads.Impl.STATUS_SUCCESS:
				holder.groupButton.setOnClickListener(
						new OnClickListener(){
							@Override
							public void onClick(View arg0) {
								deleteAllOnClick();
							}
						}
				);
				holder.groupName.setText(count +" "+ context.getResources().getString(R.string.download_count_downloaded));
				holder.groupButton.setText(context.getResources().getString(R.string.download_delete_all));
				break;
			case Downloads.Impl.STATUS_RUNNING:
				holder.groupButton.setOnClickListener(
						new OnClickListener(){
							@Override
							public void onClick(View arg0) {
								pauseAllOnClick();
							}
						}
				);
				holder.groupButton.setText(context.getResources().getString(R.string.download_pause_all));
				holder.groupName.setText(count +" "+ context.getResources().getString(R.string.download_count_downloading));
				break;
			case Downloads.Impl.STATUS_RUNNING_PAUSED:
				holder.groupButton.setOnClickListener(
						new OnClickListener(){
							@Override
							public void onClick(View arg0) {
								continueAllOnClick();
							}
						}
				);
				holder.groupButton.setText(context.getResources().getString(R.string.download_continue_all));
				holder.groupName.setText(count +" "+ context.getResources().getString(R.string.download_count_pause));
				break;
			default:
				view.setVisibility(View.GONE);
				break;
			}

			mListView.expandGroup(cursor.getPosition());
		}
		
//		private void deleteTaskinList(String pkName,String versionCode){
//			if(pkName == null || versionCode == null)
//				return;
//			List<DownloadInfo> downloads = LDownloadManager.getDefaultInstance(mContext).getAllDownloadInfo();
//			for(DownloadInfo info : downloads){
//				if(pkName.equals(info.getPackageName()) && versionCode.equals(info.getVersionCode())){
//					downloads.remove(info);
//					return;
//				}
//			}
//		}
		
		private void pauseAllOnClick(){
			Log.d(TAG,"pauseAllOnClick ");
			new Thread(new Runnable(){
				@Override
				public void run() {
					List<DownloadInfo> downloads = LDownloadManager.getDefaultInstance(mContext.getApplicationContext()).getAllDownloadInfo();
					for(DownloadInfo info : downloads){
						Log.d(TAG,"pauseAllOnClick info:" + info);
						if(Downloads.CONTROL_RUN == info.getControl())
							LDownloadManager.getDefaultInstance(mContext.getApplicationContext()).pauseTask(info);
					}
				}
				
			}).start();

		}
		private void continueAllOnClick(){
			Log.d(TAG,"continueAllOnClick ");
			new Thread(new Runnable(){
				@Override
				public void run() {
					List<DownloadInfo> downloads = LDownloadManager.getDefaultInstance(mContext.getApplicationContext()).getAllDownloadInfo();
					for(DownloadInfo info : downloads){
						if(Downloads.CONTROL_PAUSED == info.getControl())
							LDownloadManager.getDefaultInstance(mContext.getApplicationContext()).resumeTask(info);
					}
				}
			}).start();

		}
		private void deleteAllOnClick(){
			Log.d(TAG,"deleteAllOnClick ");
			new Thread((new Runnable(){
				@Override
				public void run() {
					List<DownloadInfo> downloads = LDownloadManager.getDefaultInstance(mContext.getApplicationContext()).getAllDownloadInfo();
					for(DownloadInfo info : downloads){
						if(Downloads.Impl.STATUS_SUCCESS == info.getDownloadStatus()){
						LDownloadManager.getDefaultInstance(mContext.getApplicationContext()).deleteTask(info);
						notifyDelete(info.getCategory(), info.getPackageName(), info.getVersionCode(), info.getAppName(), DownloadConstant.ACTION_DOWNLOAD_DELETE);
						}
					}
				}
			})).start();
		}

		private Drawable getThemePreviewIcon(String packagename){
			SharedPreferences sp = mContext.getSharedPreferences(SP_THEME_LOCAL_PREVIEW, MODE_WORLD_READABLE);
			String imagePath = sp.getString(packagename, "");
			Log.i(TAG,"----getThemePreviewIcon----packagename:" + packagename + ",imagePath:" + imagePath);
			if(imagePath != null){
//				String[] patharray = Utilities.findStringArrayByResourceName(packagename, mContext);
//				if(patharray != null){
//					Log.i(TAG,"----getThemePreviewIcon----patharray:" + patharray);
//					Drawable icon = Utilities.findDrawableByResourceName(patharray[0], mContext);
//					return icon;
//				}
			}
			return null;
		}
		
		private Drawable getWallpaperPreviewIcon(final String packageName){
			Drawable cache = mImgcache.get(packageName);
			if(cache == null){
				new Thread(new Runnable() {
					@Override
					public void run() {
						SharedPreferences sp = mContext.getSharedPreferences(SP_WALLPAPER_LOCAL_PREVIEW, MODE_WORLD_READABLE);
						String imagePath = sp.getString(packageName, "");
			//			Log.i(TAG,"----getWallpaperPreviewIcon----key:" + key + ",imagePath:" + imagePath);
						if(imagePath != null){
							File file=new File(imagePath);
							if (file.exists()) {
								Bitmap bm = BitmapFactory.decodeFile(imagePath);
								Drawable cacheTmp = new BitmapDrawable(bm);
								mImgcache.put(packageName, cacheTmp);
								drawIconOnUiThread(packageName,cacheTmp);
							}
						}
					}
				}).start();
			}
			return cache;
		}
		
		private void drawIconOnUiThread(final String packageName,final Drawable icon){
			((Activity) mContext).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					View view = mListView.findViewWithTag(new ChildHolder(packageName));
					if(view != null){	
						ChildHolder holder = (ChildHolder)view.getTag();
						holder.icon = icon;
						holder.iconView.setImageDrawable(holder.icon);
					}
				}
			});
		}
		
		private Drawable getAppIcon(final String iconurl,final String packagename){
			if(iconurl == null)
				return null;

			Log.i(TAG,"----getAppIcon-------iconurl:" + iconurl + ", packagename:" + packagename);
			Drawable cache = mImgcache.get(iconurl);
			if (cache == null) {
				loadImg(mContext, iconurl, new OnImgLoadListener() {
					@Override
					public void onLoadComplete(final Drawable img) {
						Log.i(TAG,"----onLoadComplete-------");
						if(img == null){
							return ;
						}
						mImgcache.put(iconurl, img);
						drawIconOnUiThread(packagename,img);
					}
				});
			}
			return cache;	
		}

		@Override
		protected View newChildView(Context context, Cursor cursor,
				boolean isLastChild, ViewGroup parent) {
			final View contentView = inflater.inflate(R.layout.download_list_item_expandable, parent,false);
			
			final ChildHolder holder = new ChildHolder();

			holder.iconView = (ImageView) contentView.findViewById(R.id.app_icon);
			holder.details = (LinearLayout) contentView.findViewById(R.id.downloadContainer);
			holder.option = (LinearLayout) contentView.findViewById(R.id.download_app_item_option);
			holder.nameTextView = (TextView) contentView.findViewById(R.id.app_name);
			holder.sizeTextView = (TextView) contentView.findViewById(R.id.app_size);
			holder.progressTextView = (TextView) contentView.findViewById(R.id.download_prosses_view);
			holder.foldIcon = (ImageView)contentView.findViewById(R.id.option_arrow);
			holder.pauseResumeInstallTextView = (TextView) contentView.findViewById(R.id.download_resume);
			holder.progressbar = (ProgressBar)contentView.findViewById(R.id.download_prosses);
			holder.versionTextView = (TextView) contentView.findViewById(R.id.app_version);
			holder.progressViewLayout = (LinearLayout)contentView.findViewById(R.id.progress_view_layout);
			holder.pkgName = cursor.getString(cursor.getColumnIndex("pkgname"));
			holder.filePath = cursor.getString(cursor.getColumnIndex(Downloads.Impl._DATA));

//			holder.hasOption = false;
			holder.option.setVisibility(View.GONE);
//			LinearLayout downloadAppsMain =  (LinearLayout) contentView.findViewById(R.id.download_apps_main);

			setOptionView(contentView,holder);
			
			contentView.setTag(holder);
			return contentView;
		}

		@Override
		protected void bindChildView(final View view, Context context, Cursor cursor,
				boolean isLastChild) {
			boolean loadImageFlag = false;
			final ChildHolder holder ;
			if( view.getTag() != null && view.getTag() instanceof ChildHolder ){
				holder = (ChildHolder) view.getTag(); 
			}else{
				return;
			}
			if(holder.icon == null)
				loadImageFlag = true;
			
			String id = cursor.getString(cursor.getColumnIndex("_id"));
			int contrl = cursor.getInt(cursor.getColumnIndex(Downloads.Impl.COLUMN_CONTROL));
			final String uri = cursor.getString(cursor.getColumnIndex("uri"));
			final String iconurl = cursor.getString(cursor.getColumnIndex(Downloads.Impl.COLUMN_ICONADDR));
			holder.status = cursor.getInt(cursor.getColumnIndex("status"));
			holder.name = cursor.getString(cursor.getColumnIndex("title"));
//			String versionCode = cursor.getString(cursor.getColumnIndex(Downloads.Impl.COLUMN_VERSIONCODE));
//			String appSize = cursor.getString(cursor.getColumnIndex("total_bytes"));
			holder.version = cursor.getString(cursor.getColumnIndex(Downloads.Impl.COLUMN_VERSIONNAME));
			holder.versionCode = cursor.getString(cursor.getColumnIndex(Downloads.Impl.COLUMN_VERSIONCODE));
			holder.filePath = cursor.getString(cursor.getColumnIndex("_data"));
			String pkg = cursor.getString(cursor.getColumnIndex("pkgname"));
			if(!pkg.equals(holder.pkgName)){
				loadImageFlag = true;
			}	
			holder.pkgName = pkg;
			holder.totalByte = cursor.getInt(cursor.getColumnIndex(Downloads.Impl.COLUMN_TOTAL_BYTES));
			holder.currentByte = cursor.getInt(cursor.getColumnIndex(Downloads.Impl.COLUMN_CURRENT_BYTES));
			holder.category = cursor.getInt(cursor.getColumnIndex(Downloads.Impl.COLUMN_CATEGORY));
			
			int downloadcategory = DownloadConstant.getDownloadCategory(holder.category);
			
			Log.i(TAG,"--DownloadExpandableActivity--bindChildView----name:" + holder.name + ",category:" + holder.category);
			if(loadImageFlag){
				if(downloadcategory == DownloadConstant.CATEGORY_COMMON_APP
						|| downloadcategory == DownloadConstant.CATEGORY_APP_UPGRADE
						|| downloadcategory == DownloadConstant.CATEGORY_APPMANAGER_UPGRADE
						|| downloadcategory == DownloadConstant.CATEGORY_RECOMMEND_APP
						|| downloadcategory == DownloadConstant.CATEGORY_SEARCH_APP){
					holder.icon = getAppIcon(iconurl,holder.pkgName);
				}else if(downloadcategory == DownloadConstant.CATEGORY_THEME ||
						downloadcategory == DownloadConstant.CATEGORY_LIVE_WALLPAPER){
//					holder.icon = getAppIcon(holder.pkgName);
					holder.icon = getAppIcon(iconurl,holder.pkgName);
				}else if(downloadcategory == DownloadConstant.CATEGORY_WALLPAPER){
					holder.icon = getWallpaperPreviewIcon(holder.pkgName);
				}
			}
			
			if(hasOptionView(holder.pkgName,holder.status)){
				holder.option.setVisibility(View.VISIBLE);
				holder.foldIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_arrow_up));
			}else{
				holder.option.setVisibility(View.GONE);
				holder.foldIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_arrow));			}	
			
			if(holder.icon == null){
				holder.icon  = mContext.getResources().getDrawable(R.drawable.download_app_icon_def);
			}
			
			holder.details.setOnClickListener(
					new OnClickListener(){
						@Override
						public void onClick(View arg0) {
							if(hasOptionView(holder.pkgName,holder.status)){
								mExpandItemPN = null;
								mExpandItemStatus = -1;
								holder.option.setVisibility(View.GONE);
								holder.foldIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_arrow));
							}else{
								
								View expandView = mListView.findViewWithTag(new ChildHolder(mExpandItemPN));
								if(expandView != null){
									ChildHolder tag = (ChildHolder)expandView.getTag();
									tag.option.setVisibility(View.GONE);
									tag.foldIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_arrow));
								}
								mExpandItemPN = holder.pkgName;
								mExpandItemStatus = holder.status;
								
								holder.option.setVisibility(View.VISIBLE);
								setOptionView(view,holder);
								holder.foldIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_arrow_up));
							}
						}
					}
			);
			
			DownloadInfo info = new DownloadInfo();
			info.setPackageName(holder.pkgName);
			info.setVersionCode(holder.versionCode);
			info.setControl(contrl);
			info.setDownloadStatus(holder.status);
			
//			updateDownloadList(info);
			
//			info.setDownloadUrl(uri);
			
			int w = dip2px(context,25);
			int h = dip2px(context,25);
			
			Drawable drawable;
			switch (holder.status) {
				case Downloads.Impl.STATUS_SUCCESS:
					drawable = context.getResources().getDrawable(R.drawable.ic_download_install_normal);  
					drawable.setBounds(0, 0, w, h); 
					holder.pauseResumeInstallTextView.setCompoundDrawables(null, drawable, null, null);
					holder.pauseResumeInstallTextView.setText(R.string.download_install);
					showAppDetail(view,holder);
					break;
				case Downloads.Impl.STATUS_RUNNING:
					drawable = context.getResources().getDrawable(R.drawable.ic_download_pause_normal);  
					drawable.setBounds(0, 0, w, h); 
					holder.pauseResumeInstallTextView.setCompoundDrawables(null, drawable, null, null);
					holder.pauseResumeInstallTextView.setText(R.string.download_pause);
					showDownloadPrograss(view,holder);
					break;
//				case RUNNING:
//					drawable = context.getResources().getDrawable(R.drawable.ic_download_pause_normal);  
//					drawable.setBounds(0, 0, 40, 40); 
//					holder.pauseResumeInstallTextView.setCompoundDrawables(null, drawable, null, null);
//					holder.pauseResumeInstallTextView.setText(R.string.download_pause);
//					showDownloadPrograss(view,holder);
//					break;
				case Downloads.Impl.STATUS_RUNNING_PAUSED:
					if(Downloads.CONTROL_RUN == contrl){
						drawable = context.getResources().getDrawable(R.drawable.ic_download_wait); 
						holder.pauseResumeInstallTextView.setText(R.string.local_app_wait);
					}else{
						drawable = context.getResources().getDrawable(R.drawable.ic_download_coutinue_normal); 
						holder.pauseResumeInstallTextView.setText(R.string.download_resume);
					}
					
					drawable.setBounds(0, 0, w, h); 
					holder.pauseResumeInstallTextView.setCompoundDrawables(null, drawable, null, null);
					showDownloadPrograss(view,holder);
					break;
				default:
					break;
			}
//			holder.cancelDeleteTextView.setOnClickListener(new DownloadClickListener(id,holder.status,holder.name, holder.pkgName, versionCode, holder.installPath,uri,holder.category));
			holder.pauseResumeInstallTextView.setOnClickListener(
					new DownloadClickListener(id,holder.status, holder.name, holder.pkgName, 
							holder.versionCode, holder.filePath,uri,holder.category));
			
		}
		
		private int dip2px(Context context, float dipValue){ 
			final float scale = context.getResources().getDisplayMetrics().density; 
			return (int)(dipValue * scale + 0.5f); 
		} 
		
		private void showDownloadInfo(View view, ChildHolder child){
			String vn;
			String size;
			
			vn = getString(R.string.magicdownload_upgrade_currentversion)
					+ child.version;
			float s;
			try{
				s = (float) (Integer.valueOf(child.totalByte)/((1024.0)*(1024.0)));
			}catch(NumberFormatException e){
				s = 0;
			}
			size = getString(R.string.local_app_size) + String.format("%.2f", s)+"M";
			
			child.sizeTextView.setVisibility(View.VISIBLE);
			child.versionTextView.setVisibility(View.VISIBLE);
			child.sizeTextView.setText(size);
			child.versionTextView.setText(vn);
		}
		
		private void showDownloadPrograss(View view,ChildHolder child){
			if(child.icon != null){
				child.iconView.setImageDrawable(child.icon);
			}else{
				child.iconView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.download_app_icon_def));
			}
			
			long currentBytes = child.currentByte;
			long totalBytes = child.totalByte;
			Log.i(TAG, "currentbytes is:"+"currentBytes" +";"+"totalbytes is:"+totalBytes);
			int progress = 0;
			float cb = (float)currentBytes/(1024*1024);
			float tb = (float)totalBytes/(1024*1024);
			Log.i(TAG, "cb is:"+cb+";"+"tb is:"+tb);
			String bytes = "";
			if(totalBytes==-1){
				 bytes = String.format("%.2f", cb) + "M/" +getResources().getString(R.string.download_total_bytes_error);
			}else{
				 bytes = String.format("%.2f", cb) + "M/" + String.format("%.2f", tb) + "M";
			}
			Log.i(TAG, "bytes is:"+bytes);
			if ( DownloadConstant.CATEGORY_WALLPAPER == child.category ||
					DownloadConstant.CATEGORY_THEME == child.category) {
				child.pauseResumeInstallTextView.setVisibility(View.GONE);
//				split.setVisibility(View.INVISIBLE);
			}else{
				child.pauseResumeInstallTextView.setVisibility(View.VISIBLE);
//				split.setVisibility(View.VISIBLE);
			}
			child.nameTextView.setText(child.name);
			child.progressTextView.setText(bytes);
			child.progressTextView.setVisibility(View.VISIBLE);
			child.progressViewLayout.setVisibility(View.VISIBLE);
			child.progressbar.setVisibility(View.VISIBLE);
			child.sizeTextView.setVisibility(View.GONE);
			child.versionTextView.setVisibility(View.GONE);
			if(currentBytes != 0 && totalBytes != 0){
				progress = (int)(currentBytes/(float)totalBytes*100);
			}
			if(totalBytes==-1){
				child.progressbar.setVisibility(View.GONE);
			}else{
				child.progressbar.setProgress(progress);
			}
//			Log.d(TAG,"--------DownloadExpandableActivity-------------------progress:"+progress+",name:"+app.getAppName());
		}
		
		private void showAppDetail(View view,ChildHolder child){
			
			if(child.icon != null){
				child.iconView.setImageDrawable(child.icon);
			}else{
				child.iconView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.download_app_icon_def));
			}

			if(DownloadConstant.CATEGORY_WALLPAPER == child.category || DownloadConstant.CATEGORY_LBK == child.category ){
				child.pauseResumeInstallTextView.setVisibility(View.GONE);
//				split.setVisibility(View.INVISIBLE);
			}else{
//				split.setVisibility(View.VISIBLE);
				child.pauseResumeInstallTextView.setVisibility(View.VISIBLE);
				child.pauseResumeInstallTextView.setText(R.string.download_install);
			}
			showDownloadInfo(view,child);
			child.nameTextView.setText(child.name);
			child.progressViewLayout.setVisibility(View.GONE);
			child.progressbar.setVisibility(View.GONE);
			child.progressTextView.setVisibility(View.GONE);
		}

		final class DownloadClickListener implements View.OnClickListener{
			int mStatus ;
			DownloadInfo downloadInfo ;
			String mInstallPath ;
			String mAppName;
			int mCategory;
			
			public DownloadClickListener(String id, int status,String appName, String pkgName,String versionCode,String installPath,String url,int category) {
				mStatus = status;
				downloadInfo = new DownloadInfo(pkgName,versionCode);
				downloadInfo.setId(id);
				downloadInfo.setInstallPath(installPath);
				downloadInfo.setAppName(appName);
				downloadInfo.setDownloadUrl(url);
				downloadInfo.setCategory(category);
				mCategory = category;
				mInstallPath = installPath;
				mAppName = appName;
			}

			@Override
			public void onClick(final View v) {
				Log.d(TAG, "isPaused >>>>>>>>>>>>> " + isPaused);
				if( isPaused ) return ;
				switch (v.getId()) {
//				case R.id.download_delete:
//					new AlertDialog.Builder(mContext).
//					setMessage(R.string.download_delete_dialog_title_lejingpin).
//					setPositiveButton(R.string.download_delete_dialog_confirm, new DialogInterface.OnClickListener(){
//						public void onClick(DialogInterface dialog, int which) {
//							Log.d(TAG, "DownloadClickListener >> PositiveButton >> onClick >> mAppName : " + mAppName + ",Category :" +  downloadInfo.getCategory());
//
//							LDownloadManager.getDefaultInstance(mContext).deleteTask(downloadInfo);
//							//move notifyDelete to after database real deleteTask run
//							notifyDelete(downloadInfo.getCategory(),downloadInfo.getPackageName(), downloadInfo.getVersionCode(), mAppName, DownloadConstant.ACTION_DOWNLOAD_DELETE);
//						}
//					}).setNegativeButton(R.string.download_delete_dialog_cancle, null).show();
//					break;
				case R.id.download_resume:
					//fix bug 166262 author:ljc19851119@126.com Date:20120620 S
					v.setEnabled(false);
					v.postDelayed(new Runnable() {
						public void run() {
							v.setEnabled(true);
						}
					}, 1500);
					//fix bug 166262 author:ljc19851119@126.com Date:20120620 E
					switch (mStatus) {
					case Downloads.Impl.STATUS_RUNNING:
						LDownloadManager.getDefaultInstance(mContext.getApplicationContext()).pauseTask(downloadInfo);
						break;
					case Downloads.Impl.STATUS_RUNNING_PAUSED:
						if( DownloadConstant.CONNECT_TYPE_OTHER == DownloadConstant.getConnectType(mContext)){
							Toast.makeText(mContext, R.string.error_network_state, Toast.LENGTH_SHORT).show();
//							showNetErrorToast();
						} else{
							LDownloadManager.getDefaultInstance(mContext.getApplicationContext()).resumeTask(downloadInfo);
						}
						break;
					case Downloads.Impl.STATUS_SUCCESS:
						LcaInstallerUtils.installApplication(mContext, downloadInfo);
//						install(mContext, mInstallPath);
						break;
					default:
						LDownloadManager.getDefaultInstance(mContext.getApplicationContext()).deleteTask(downloadInfo);
						LDownloadManager.getDefaultInstance(mContext.getApplicationContext()).addTask(downloadInfo);
						break;
					}
					break;
				}
				//mAdapter.notifyDataSetChanged(false);
			}
		}
		
		private void notifyDelete(int category,String packageName, String versionCode,String appName ,String action) {
			Intent intent = new Intent(action);
			intent.putExtra(DownloadConstant.EXTRA_PACKAGENAME, packageName);
			intent.putExtra(DownloadConstant.EXTRA_VERSION, versionCode);
			intent.putExtra(DownloadConstant.EXTRA_APPNAME, appName);
			intent.putExtra(DownloadConstant.EXTRA_CATEGORY, category);
			mContext.sendBroadcast(intent);
			Log.d(TAG, "DownloadExpandableActivity >>> notifyDelete >>> action : " + action);
		}
		
	}
	
	
	/*protected void install(Context context ,String installPath){
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setClassName("com.lenovo.leos.hw", "com.lenovo.leos.hw.lcapackageinstaller.LcaInstallerActivity");
		intent.setData(Uri.parse(installPath));
		try {
			context.startActivity(intent);
		} catch (Exception e) {
			Toast.makeText(context, R.string.lca_installer_not_found, Toast.LENGTH_SHORT).show();
		}
	}*/
//	private void updateDownloadList(DownloadInfo info){
//		boolean flag = false;
////		if(mDownloadList.size() != 0){
//			for(DownloadInfo download: mDownloadList){
//				if(download.getPackageName() != null &&
//					download.getVersionCode() != null &&
//					download.getPackageName().equals(info.getPackageName()) &&
//					download.getVersionCode().equals(info.getVersionCode())){
//					download = info;
//					flag = true;
//					break;
//				}
//			}
////		}
//		if(!flag){
//			mDownloadList.add(info);
//		}
//	}
	
	private static final String calculateSize(String size){
		if (size == null || size.length() == 0 ) {
			return null;
		}
		float sizeInt = Float.parseFloat(size);
		if(sizeInt > 1048576){
			return String.format("%.2f", sizeInt/1048576) + " M";
		} else if(sizeInt > 1024){
			return Math.round( sizeInt/1024 ) + " K";
		} else{
			return Math.round( sizeInt ) + " B";
		}
	}
	
	private void loadImg(final Context context,final String url,
			final OnImgLoadListener callback) {

		if (TextUtils.isEmpty(url))
			return;

		new Thread(new Runnable() {

			@Override
			public void run() {
				Log.i(TAG,"----loadImg----111----");
				GetImageRequest imageRequest = new GetImageRequest();
				imageRequest.setData(url);
				AmsSession.execute(context, imageRequest, new AmsCallback() {
					
					@Override
					public void onResult(AmsRequest request, int code,final byte[] bytes) {
						Log.i(TAG,"----loadImg----222----");
						Drawable drawable = null;
						ByteArrayInputStream bs = null;
						GZIPInputStream input = null;
						
						try {
							input = new GZIPInputStream(new ByteArrayInputStream(bytes));
							drawable = Drawable.createFromStream(input, null);
						} catch (Exception e) {
							// e.printStackTrace();
							bs = new ByteArrayInputStream(bytes);
							drawable = Drawable.createFromStream(bs, null);
						} finally {
							try {
								if (input != null) {
									input.close();
								}
								if (bs != null) {
									bs.close();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						callback.onLoadComplete(drawable);

					}
				});
			}
		}).start();

	}
	
	private interface OnImgLoadListener {
		void onLoadComplete(Drawable img);
	}
	
	static final class ChildHolder{
		LinearLayout details ;
		LinearLayout option;
		LinearLayout progressViewLayout;
		ImageView iconView;
		TextView nameTextView;
		TextView tipsTextView;
		TextView versionTextView;
		TextView sizeTextView;
//		TextView cancelDeleteTextView ;
		TextView pauseResumeInstallTextView;
		TextView progressTextView;
//		ProgressBar iconProgress;
		ImageView foldIcon;
		ProgressBar progressbar;
		
//		String installPath ;
		int totalByte;
		int currentByte;
		int status;
		int category;
		String name;
		String pkgName;
		String version;
		String versionCode;
		String filePath;
		Drawable icon;
//		boolean hasOption;

		public ChildHolder() {
			super();
		}
		
		public ChildHolder(String packageName) {
			super();
			this.pkgName = packageName;
		}
		
		@Override
		public boolean equals(Object o) {
			if(o instanceof ChildHolder && pkgName!=null){
				ChildHolder vh = (ChildHolder) o;
					return pkgName.equals(vh.pkgName);
				
			}
			return false;
		}
	}
	
	static final class GroupHolder{
		TextView groupName;
		TextView groupButton;
	}
	
	
	private DownloadInfo getDownloadInfo(String packageName,String versionCode){
		DownloadInfo info = new DownloadInfo();
		info.setPackageName(packageName);
		info.setVersionCode(versionCode);
		return LDownloadManager.getDefaultInstance(this).getDownloadInfo(info);
		
	}
	
	 //yangmao add new
    
		private File verifyPackage(String uriStr, String packageName){
			Uri packageURI =  null;
			if( uriStr != null && packageName != null ){
			    packageURI = Uri.parse(uriStr);

			}else{
//				showToast(DLG_PACKAGE_ERROR,null);
				return null;
			}
			JarFile jarFile = null;
			JarEntry jarEntry = null;
			try {
				jarFile = new JarFile(packageURI.getPath());
			} catch (Exception e) {
				Log.i(TAG,"LcaInstallerService, new JarFile error!");
//				showToast(DLG_PACKAGE_ERROR, packageName);
				return null;
			}
			
			JarEntry entryTemp = null;
			File tempFile = null;
			boolean bFoundXML  = false;
			boolean bFoundAPK = false;
			for (Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements() && (!bFoundXML|| !bFoundAPK) ;) {
				entryTemp = e.nextElement();
				String entryName = entryTemp.getName();
				if (entryName.equalsIgnoreCase("lcainfo.xml") && !bFoundXML) {
					jarEntry = entryTemp;
					bFoundXML = true;
				}else if (entryName.endsWith(".apk") && !bFoundAPK) {
					tempFile = extractFileFromJarFile(packageURI, jarFile, entryTemp);
					bFoundAPK = true;
				}
			}
			if (jarEntry != null) {// LCA package
				boolean verified = true;
				XmlLcaInfoHandler mLcaHandler = new XmlLcaInfoHandler();
				try {
					XmlParser.Parse(jarFile.getInputStream(jarEntry), mLcaHandler);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				Map<String, String>  mLcaInfo = mLcaHandler.getParseData();
				if (mLcaInfo == null) {
					Log.i(TAG," LcaInstallerService, mLcaHandler.getParseData() error!");
//					showToast(DLG_PACKAGE_ERROR, packageName);
					return null;
				}
				// Verify the lca package
				if (!SignatureVerify.verifySignature(this, packageURI.getPath())) {
					if (mLcaHandler.getHasTest()
							&& SignatureVerify.verifyTestSignature(this,
									packageURI.getPath())) {
						String lcadate = mLcaInfo.get(XmlLcaInfoHandler.TAG_InvalidDate);
						Log.i(TAG, "lcadate: " + lcadate);
						DateFormat dateFormat = new SimpleDateFormat(
								"yyyyMMddhhmmss");
						try {
							Date date = dateFormat.parse(lcadate + "23" + "59"+ "59");
							long lastdate = date.getTime();
							if (System.currentTimeMillis() > lastdate) {
								verified = false;
							}
						} catch (Exception e) {
							verified = false;
						}
						Log.i(TAG, "lcadate verified: " + verified);
						if (!verified) {
//							showToast(DLG_DATE_ERROR, packageName);
							return null;
						}
						String imei = PsDeviceInfo.getDeviceId(this);
						ArrayList<String> apkImei = mLcaHandler.getImei();
						if (imei == null) {
							verified = false;
						} else if (apkImei.size() != 0 && !apkImei.contains(imei)) {
							verified = false;
						}
						Log.i(TAG, "imei verified: " + verified);
						if (!verified) {
//							showToast(DLG_DEVICE_ERROR, packageName);
							return null;
						}
					} else {
						verified = false;
					}
				}
				verified = true;

				if (!verified) {
//					showToast(DLG_PERMISSION_ERROR, packageName);
				}else{
					if (tempFile != null) {
//						showToast(DLG_PACKAGE_ERROR, packageName);
						return tempFile;
					}

				}
			} else {
				Log.i(TAG," LcaInstallerService, jarEntry is null,  error!");
//				showToast(DLG_PACKAGE_ERROR, packageName);
			}
			return null;
		}
	 
	 
		private File extractFileFromJarFile(Uri packageURI, JarFile file, JarEntry entry) {
			File apkFile = null;
			byte[] readBuffer = new byte[8192];
			try {
				String path = packageURI.getPath();
				String dir = path.substring(0, path.lastIndexOf(File.separator));
				String fileName = path.substring(path.lastIndexOf(File.separator)+1);
				apkFile = new File(dir + File.separator + "lca" + fileName.replace(".lca", ".apk"));
				InputStream is = file.getInputStream(entry);
				OutputStream os = new FileOutputStream(apkFile);
				int num;
				while ((num = is.read(readBuffer)) > 0) {
					os.write(readBuffer, 0, num);
				}
				os.flush();
				is.close();
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return apkFile;
		}
	 
		
		
//		private void showToast(int id, String packageName){
//			String message = "";
//			
//			switch (id) {
//
//		    case DLG_PACKAGE_ERROR:{
//			    message = getString(R.string.lcapackageinstaller_parse_error);
//			    break;
//		    }
//		    case DLG_PERMISSION_ERROR:{
//		    	message= getString(R.string.lcapackageinstaller_permission_verify_error);
//		    	break;
//		    }
//		    case DLG_DATE_ERROR:{
//		    	message= getString(R.string.lcapackageinstaller_permission_date_error);
//	            break;
//		    }
//		    case DLG_DEVICE_ERROR:{
//	    	    message= getString(R.string.lcapackageinstaller_permission_device_error);
//	            break;
//	        }
//		    
//		    default:
//		    	break;
//		    }
//				
//			
//			if( message != null && !message.equals(""))				
//				toastShow(message);		
//	    }
//		
//		
//		 private void toastShow(String text) {
//				if (toast == null) {
//					toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
//					toastView = toast.getView();
//				} else {
//					if (toastView != null) {
//						toast.setText(text);
//						toast.setDuration(Toast.LENGTH_SHORT);
//						toast.setView(toastView);
//					}
//				}
//				toast.show();
//		}
	
}
