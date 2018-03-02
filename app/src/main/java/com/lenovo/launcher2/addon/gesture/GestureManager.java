package com.lenovo.launcher2.addon.gesture;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;
import com.lenovo.launcher.components.XAllAppFace.XLauncher;
import com.lenovo.launcher2.commoninterface.ApplicationInfo;
import com.lenovo.launcher2.commoninterface.ShortcutInfo;
import com.lenovo.launcher2.commonui.LeDialog;
import com.lenovo.launcher2.customizer.SettingsValue;
/**
 * Author : liuyg1@lenovo.com
 * */
public class GestureManager {
	private RecentTaskAdapter mRecentTaskAdapter;
	private LeDialog mRecentTaskDialog;
	private TextView mNoRecentTask;
	private GridView mShowRecentTaskGridView;
//	private ImageView mKillAll;
	private  XLauncher mLauncher;
	//test by dining 2013-06-09
	private InnerRecevier mRecevier;
	private IntentFilter mFilter;
	
	public static final String ACTION_GESTURE_SCROLL_UP = "com.lenovo.launcher.gesture.Intent.ACTION_SCROLL_UP";
	public static final String ACTION_GESTURE_SCROLL_DOWN = "com.lenovo.launcher.gesture.Intent.ACTION_SCROLL_DOWN";
	public static final String ACTION_GESTURE_DOUBLE_CLICK = "com.lenovo.launcher.gesture.Intent.ACTION_DOUBLE_CLICK";
	
	public GestureManager(XLauncher launcher) {
		mLauncher = launcher;
	}
	public void showNotifications() {
		try {
			Object service = mLauncher.getSystemService("statusbar");
			if (service == null)  return;
			try{
				Class<?> statusbarManager = Class
						.forName("android.app.StatusBarManager");
				Method expand = statusbarManager.getMethod("expand");
				if (expand != null)
					expand.invoke(service);
			} catch (Exception ex) {
				try {
					Class<?> statusbarManager = Class
							.forName("android.app.StatusBarManager");
					Method expand = statusbarManager
							.getMethod("expandNotificationsPanel");
					if (expand != null)
						expand.invoke(service);
				} catch (Exception e) {
					return;
				}
			}
		} catch (Exception ex) {
			return;
		}
	}


	public void showRecentTask() {	
		Intent intent = new Intent();
		intent.setAction("com.android.systemui.TOGGLE_RECENTS");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		try{
			mLauncher.startActivity(intent);
		}catch(ActivityNotFoundException e){


			if (mRecentTaskDialog != null && mRecentTaskDialog.isShowing())
			{
				return;
			}
			mRecentTaskDialog = new LeDialog(mLauncher,R.style.Theme_LeLauncher_Dialog_Shortcut);
			mRecentTaskDialog.setCanceledOnTouchOutside(true);
			mRecentTaskDialog.setContentView(R.layout.show_recent_task);
			mRecentTaskDialog.setLeTitle(R.string.apptask_tab_label);
          mNoRecentTask = (TextView) mRecentTaskDialog.findViewById(R.id.no_recent_task);
			mShowRecentTaskGridView = (GridView) mRecentTaskDialog.findViewById(R.id.recenttasklist);
			//		mKillAll = (ImageView)mRecentTaskDialog.findViewById(R.id.clear_all_task);

			mShowRecentTaskGridView.setScrollContainer(true);
			List<RecentTaskItem> mRecentTasks = getRecetTaskItems();
			if(mRecentTasks==null){
				return;
			}
			if(mRecentTasks.size()<1){
				mNoRecentTask.setVisibility(View.VISIBLE);
				mShowRecentTaskGridView.setVisibility(View.GONE);
				//			mKillAll.setVisibility(View.GONE);
			}else{
				mRecentTaskAdapter = new RecentTaskAdapter(mLauncher, mRecentTasks);
				mShowRecentTaskGridView.setAdapter(mRecentTaskAdapter);
				//			mKillAll.setVisibility(View.GONE);
				//			mKillAll.setOnClickListener(new OnClickListener() {
				//				@Override
				//				public void onClick(View v) {
				//					PreferenceManager
				//					.getDefaultSharedPreferences(mLauncher)
				//					.edit()
				//					.putLong(UsageStatsMonitor.KEY_LAST_BOOT_TIME,
				//							System.currentTimeMillis()).commit();
				//					mShowRecentTaskGridView.setVisibility(View.GONE);
				//					mNoRecentTask.setVisibility(View.VISIBLE);
				//					mKillAll.setVisibility(View.GONE);
				//
				//					return;
				//				}
				//			});
			}

			mRecentTaskDialog.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_BACK||keyCode == KeyEvent.KEYCODE_HOME) {
						if (event.getAction() == MotionEvent.ACTION_DOWN)
						{
							if (dialog != null)
								dialog.dismiss();

						}
					}
					return false;
				}
			});
			mRecentTaskDialog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface arg0) {
					stopWatch();
					mRecentTaskDialog = null;
					mRecevier = null;
					mFilter = null;
					Log.e("mRecentTaskDialog", "onDismiss");
				}
			});

			mRecentTaskDialog.setOnShowListener(new OnShowListener() {

				@Override
				public void onShow(DialogInterface dialog) {
					if(mRecevier == null){
						mRecevier = new InnerRecevier();
					}
					if(mFilter == null){
						mFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
					}
					startWatch();	

				}
			});

			mRecentTaskDialog.show();
		}
	}
	//test by dining 2013-06-09
	private void startWatch() {
		
	    if (mRecevier != null && mRecentTaskDialog != null && mFilter != null) {
	    	mRecentTaskDialog.getContext().registerReceiver(mRecevier, mFilter);
	    	
		}
    }
	
	public void stopWatch() {
		if (mRecevier != null && mRecentTaskDialog != null) {
			mRecentTaskDialog.getContext().unregisterReceiver(mRecevier);
		 }
	}
	
	List<RecentTaskItem> items = new ArrayList<RecentTaskItem>();
	private class RecentTaskAdapter extends BaseAdapter {

		private final List<RecentTaskItem> mItems;
		private Context mContext;

		public RecentTaskAdapter(Context context, List<RecentTaskItem> items) {
			mItems = items;
			mContext = context;
		}
		@Override
		public int getCount() {
			return mItems.size();
		}
		@Override
		public Object getItem(int position) {
			return mItems.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(mContext, R.layout.app_to_category,
						null);
			}
			final TextView label = (TextView) convertView
					.findViewById(R.id.category_label);
			final ImageView icon = (ImageView) convertView
					.findViewById(R.id.category_icon);
			icon.setImageDrawable(mItems.get(position).icon);
			label.setText(mItems.get(position).label);
			label.setTextSize(Integer.valueOf(SettingsValue.getIconTextSizeValue(mContext)));
			convertView.setTag(position);
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {

					int position = Integer.parseInt(view.getTag().toString());
					final Intent intent = getIntentForPosition(position);
					PackageManager packageManager = mContext.getPackageManager();
					if (packageManager != null) {
						ResolveInfo resolveInfo = packageManager
								.resolveActivity(intent, 0);
						LauncherApplication app = (LauncherApplication) mContext.getApplicationContext();
						if (app != null) {

							ApplicationInfo appInfo = new ApplicationInfo(
									packageManager, resolveInfo, app
									.getIconCache(), null);
							if (appInfo != null) {
								mLauncher.startActivitySafely(appInfo.intent, appInfo);
//								if (mLauncher.getModel() != null
//										&& mLauncher.getModel().getUsageStatsMonitor() != null)
//									mLauncher.getModel().getUsageStatsMonitor().add(
//											appInfo.intent);

							}
						}
					}
					if (mRecentTaskDialog != null && mRecentTaskDialog.isShowing()) {
						mRecentTaskDialog.dismiss();
					}
				}
			});
			return convertView;
		}
	}
	protected Intent getIntentForPosition(int position) {
		RecentTaskItem item = (RecentTaskItem) mRecentTaskAdapter.getItem(position);
		return item.getIntent();
	}
	private List<RecentTaskItem> getRecetTaskItems() {

		PackageManager packageManager = mLauncher.getPackageManager();
		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		items.clear();
		final List<RecentTaskInfo> infolist = getRecentTasks(20);
		for (int i = 0; i < infolist.size(); i++) {
			RecentTaskInfo info = infolist.get(i);
//			ComponentName componentName = info.origActivity;
			if(info==null||info.baseIntent==null){
				continue;
			}
							
			String packageName = info.baseIntent.getComponent().getPackageName();
			String classname = info.baseIntent.getComponent().getClassName();
			Intent checkIntent = info.baseIntent;
			
			if(packageName.equals("com.lenovo.ideafriend")){
				if(info.origActivity != null){
					classname = info.origActivity.getClassName();
					checkIntent = new Intent(Intent.ACTION_MAIN, null);
					checkIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			
					checkIntent.setClassName(packageName, classname);
				}
				
			}
			Log.i("GestureManager","-----package=" + packageName + "  classname="+ classname + " checkIntant=" + checkIntent);
			
			ShortcutInfo shortcutInfo = null;
			//shortcutInfo = mLauncher.getModel().getShortcutInfo(packageManager, info.baseIntent, mLauncher);
			shortcutInfo = mLauncher.getModel().getShortcutInfo(packageManager, checkIntent, mLauncher);
			
			if(shortcutInfo!=null&&info.baseIntent.getComponent()!=null){
				if(info.baseIntent.getComponent().getPackageName()==null){
					continue;
				}
				if(!info.baseIntent.getComponent().getPackageName().equals(mLauncher.getPackageName())){
					ResolveInfo resolveInfo = null;
					List<ResolveInfo>  resolveInfoList = packageManager.queryIntentActivities(mainIntent, 0);
					Log.i("GestureManager","-----className is " +classname);
					if (resolveInfoList !=null && resolveInfoList.size() > 0) {
						for (ResolveInfo tempInfo : resolveInfoList) {
							if (tempInfo != null&&tempInfo.activityInfo!=null&&tempInfo.activityInfo.applicationInfo!=null) {
								/*RK_ID: BUG_SHINE_2523. AUT: shenchao1@lenovo.com DATE: 2013-11-25 S*/
								if (packageName.equals(tempInfo.activityInfo.applicationInfo.packageName) &&
										(info.baseIntent.getAction() != null)) {
									if(info.baseIntent.getAction().equals("android.intent.action.MAIN")){
										resolveInfo = tempInfo;
										break;
									}
									/*RK_ID: BUG_SHINE_2523. AUT: shenchao1@lenovo.com DATE: 2013-11-25 E*/
								}
							}
						}
					}

					if(resolveInfo!=null&&shortcutInfo.title!=null){
						
						items.add(new RecentTaskItem(mLauncher, packageManager, shortcutInfo, checkIntent,false)); //
					}
					
				}
			}
		}
		return items;
	}
	private List<RecentTaskInfo> getRecentTasks(int max) {
		if (max <= 0) {
			return null;
		}
//		if (mLauncher.getModel().getUsageStatsMonitor() != null) {
//			mLauncher.getModel().getUsageStatsMonitor().updateCatch();
//		}
//		final ArrayList<ApplicationInfo> apps = mLauncher.getModel().getAllAppsList().data;
//		ArrayList<ApplicationInfo> temp = (ArrayList<ApplicationInfo>) apps.clone();
		
		ActivityManager am = (ActivityManager) mLauncher.getApplicationContext().getSystemService(Activity.ACTIVITY_SERVICE);
		List<ActivityManager.RecentTaskInfo> recentList = am.getRecentTasks(max, 0);
		
//		UsageStatsMonitor.getRecentTasks(temp, max, PreferenceManager.getDefaultSharedPreferences(mLauncher).getLong(UsageStatsMonitor.KEY_LAST_BOOT_TIME, 0));
		return recentList;
	}
	private static class RecentTaskItem {

		CharSequence label;
		Drawable icon;
//		String packageName;
//		String className;
		Intent baseIntent;
		RecentTaskItem(Context context, PackageManager pm, ShortcutInfo shortcutInfo,Intent baseintent, Boolean check) {
			label = shortcutInfo.title;
//			packageName = componentName.getPackageName();
//			className = componentName.getClassName();
			baseIntent = baseintent;
			LauncherApplication app = (LauncherApplication) context.getApplicationContext();
			icon = new BitmapDrawable(app.getResources(), shortcutInfo.getIcon(app.getIconCache()));
		}

		Intent getIntent() {
			return baseIntent;
//			Intent mBaseIntent = new Intent(Intent.ACTION_MAIN, null);
//			mBaseIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//			mBaseIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//			if (packageName != null && className != null) {
//				// Valid package and class, so fill details as normal intent
//				mBaseIntent.setClassName(packageName, className);
//				return mBaseIntent;
//			}
//			return mBaseIntent;
		}
	}
	
	class InnerRecevier extends BroadcastReceiver {
		  final String SYSTEM_DIALOG_REASON_KEY = "reason";
		  final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

		  @Override
		  public void onReceive(Context context, Intent intent) {
		   String action = intent.getAction();
		   if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
		        String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
		        if (reason != null) {
		            if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
		                Log.e("mRecentTaskDialog", "get HOME KEY");
		                if(mRecentTaskDialog != null && mRecentTaskDialog.isShowing()){
		                	mRecentTaskDialog.dismiss();
		                }
		            }
		     
		        }
		   }
		  }
		 
		}
	
	public void hideRencentDlg(){
		if( mRecentTaskDialog != null && mRecentTaskDialog.isShowing()){
			mRecentTaskDialog.dismiss();
		}
	}
}
