package com.lenovo.launcher2.backup;

import java.io.File;
import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import android.appwidget.AppWidgetHost;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.provider.Settings;
import android.util.Log;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;
import com.lenovo.launcher.components.XAllAppFace.XLauncher;
import com.lenovo.launcher2.LauncherProvider;
import com.lenovo.launcher2.backup.ProfileInteractiveHelper.ReloaderHelper;
import com.lenovo.launcher2.backup.ProfileInteractiveHelper.ReloaderHelper.FolderReloader;
import com.lenovo.launcher2.backup.ProfileInteractiveHelper.ReloaderHelper.LeosE2EWidgetLoader;
import com.lenovo.launcher2.backup.ProfileInteractiveHelper.ReloaderHelper.PriorityReloader;
import com.lenovo.launcher2.backup.ProfileInteractiveHelper.ReloaderHelper.QuickEntryReloader;
import com.lenovo.launcher2.backup.ProfileInteractiveHelper.ReloaderHelper.WidgetReloader;
import com.lenovo.launcher2.commoninterface.BackupableSetting;
import com.lenovo.launcher2.commoninterface.ConstantPasser;
import com.lenovo.launcher2.commoninterface.ConstantPasser.Favorites;
import com.lenovo.launcher2.commoninterface.InfoFactory.ProfileInfo;
import com.lenovo.launcher2.commoninterface.LauncherSettings;
import com.lenovo.launcher2.commoninterface.XmlProfilesInflater;
import com.lenovo.launcher2.customizer.ConstantAdapter;
import com.lenovo.launcher2.customizer.Debug;
import com.lenovo.launcher2.customizer.Debug.R2;
import com.lenovo.launcher2.customizer.EnableState;
import com.lenovo.launcher2.customizer.ProcessIndicator;
import com.lenovo.launcher2.customizer.SettingsValue;

public class ProfileReloader {

	private String currentProfileParentReloaderDir = null;

	private XmlProfilesInflater xmlInflater = null;

	private ProfileInfo profile = null;

	private Context mContext = null;

	private boolean performingDefault = false;

	private EnableState enableState = null;

	public ProfileReloader(Context context, String profileDirPathName,
			EnableState enableState) {

		// enableState can not be null
		Assert.assertNotNull(enableState);

		performingDefault = false;

		this.enableState = enableState;

		mContext = context;
		if (Debug.MAIN_DEBUG_SWITCH){
			R2.echo("Will reload : " + profileDirPathName);
		}

		currentProfileParentReloaderDir = profileDirPathName;
		try {
			// R2

			String descFilePath = profileDirPathName + File.separator
					+ ConstantAdapter.PROFILE_DESC_XML_NAME;

			if (Debug.MAIN_DEBUG_SWITCH){
				R2.echo("Inflater check : " + descFilePath);
			}

			ProcessIndicator.getInstance(mContext).setState(
					R.string.inflater_ready_to_go);
			xmlInflater = new XmlProfilesInflater(new FileInputStream(new File(
					descFilePath)));

		} catch (Exception e) {
			e.printStackTrace();
			ProcessIndicator.getInstance(mContext).setState(
					R.string.file_may_damaged);
			if (Debug.MAIN_DEBUG_SWITCH){
				R2.echo(" Inflater checked FAILED, file may not exists!");
				}
		}
	}

	public void setPerforDefaultProfileTag(boolean isDefault) {
		performingDefault = isDefault;
	}

	public String getCurrentProfileWorkDir() {
		return currentProfileParentReloaderDir;
	}
	public String getProfileVerion(){
		if(xmlInflater!=null){
			return xmlInflater.getProfliesVersion(); 	
		}else return null;
		
	}
	public boolean reloadProfile(boolean isDefault) {

		// parse
		try {
			xmlInflater.forceParse();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		// wait for parse thread
		while (xmlInflater.isProcessing()) {
			try {
				ProcessIndicator.getInstance(mContext).setState(
						R.string.inflater_ready_to_go);
				Thread.sleep(10L);
			} catch (Exception ex) {
				ex.printStackTrace();
				return false;
			}
		}

		// is our inflater performed successfully
		if (xmlInflater.isFailed()) {
			if (Debug.MAIN_DEBUG_SWITCH){
				R2.echo("Inflater failed !");
			}
			return false;
		}

		// receive profile
		try {
			// the first item is the most wanted
			profile = xmlInflater.getCustomizedProfiles().get(0);
			String version = xmlInflater.getProfliesVersion();
			if(version!=null&&version.contains("pad")&&SettingsValue.getCurrentMachineType(mContext)==-1){
				return false;
			}else if(version!=null&&!version.contains("pad")&&SettingsValue.getCurrentMachineType(mContext)!=-1){
				return false;
			}
			if(version!=null&&!version.startsWith("2.0")){
//				SettingsValue.SetSingleLayerValue(false);
				profile.singleLayer = false;
			}
			if (profile != null) {
				if (Debug.MAIN_DEBUG_SWITCH){
					R2.echo("Begin to RELOAD ----> " + profile.name);
				}
				if (Debug.MAIN_DEBUG_SWITCH){
					R2.echo("Reloader will worked in : "
							+ currentProfileParentReloaderDir);
				}
				profile.setKey(currentProfileParentReloaderDir);
				ProcessIndicator.getInstance(mContext).setState(
						R.string.init_reloader);
				return processReload(isDefault);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return false;
	}

	private boolean processReload(boolean isDefault) {

		// clean the temporary db
		cleanDatabase();

		// reload desktop first
		boolean res = reloadDesktop(isDefault);
		
//        reloadHotseat();
		return res;
	}
	private static final String TAG_SHORTCUT = "shortcut";
	private static final String TAG_FAVORITES = "favorites";
	private static final String TAG = "ProfileReloader";
//	private void reloadHotseat() {
//		ContentResolver cr = mContext.getContentResolver();
//		String[] args = {String.valueOf(LauncherSettings.Favorites.CONTAINER_HOTSEAT)};
//
//		cr.delete(ConstantPasser.Favorites.CONTENT_URI_NO_NOTIFICATION,
//				LauncherSettings.Favorites.CONTAINER +"=?", args);
//		
//		Intent intent = new Intent(Intent.ACTION_MAIN, null);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        ContentValues values = new ContentValues();
//
//        //PackageManager packageManager = mContext.getPackageManager();
//        try {
//        	XmlResourceParser parser = mContext.getResources().getXml(R.xml.default_hotseat);
//        	AttributeSet attrs = Xml.asAttributeSet(parser);
//        	XmlUtils.beginDocument(parser, TAG_FAVORITES);
//
//        	final int depth = parser.getDepth();
//
//        	int type = parser.next();
//        	while ((type != XmlPullParser.END_TAG ||
//        			parser.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {
//
//        		if (type != XmlPullParser.START_TAG) {
//        			continue;
//        		}
//
//        		final String name = parser.getName();
//
//        		TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.Favorite);
//
//        		long container = LauncherSettings.Favorites.CONTAINER_DESKTOP;
//        		if (a.hasValue(R.styleable.Favorite_container)) {
//        			container = Long.valueOf(a.getString(R.styleable.Favorite_container));
//        		}
//        		String screen = a.getString(R.styleable.Favorite_screen);
//        		String x = a.getString(R.styleable.Favorite_x);
//        		String y = a.getString(R.styleable.Favorite_y);
//        		/** AUT: henryyu1986@163.com DATE: 2011-12-30 S */
//        		int icon = a.getResourceId(R.styleable.Favorite_icon, -1);
//        		String uri = a.getString(R.styleable.Favorite_uri);
//
//        		values.put(LauncherSettings.Favorites.CELLY, y);
//        		/** AUT: henryyu1986@163.com DATE: 2011-12-30 E */
//        		// If we are adding to the hotset, the screen is used as the position in the
//        		// hotset. This screen can't be at position 0 because AllApps is in the
//        		// zeroth position.
//        		if (container == LauncherSettings.Favorites.CONTAINER_HOTSEAT &&
//        				XHotseat.isAllAppsButtonRank(Integer.valueOf(screen))) {
//        			throw new RuntimeException("Invalid screen position for hotseat item");
//        		}
//
//        		values.clear();
//        		values.put(LauncherSettings.Favorites.CONTAINER, container);
//        		values.put(LauncherSettings.Favorites.SCREEN, screen);
//        		values.put(LauncherSettings.Favorites.CELLX, x);
//        		values.put(LauncherSettings.Favorites.CELLY, y);
//        		/** AUT: henryyu1986@163.com DATE: 2011-12-30 S */
//        		if(uri != null && !"".equals(uri)) {
//        			values.put(LauncherSettings.Favorites.URI, uri);
//        		}
//        		if(icon != -1) {
//        			values.put(LauncherSettings.Favorites.ICON_RESOURCE, icon);
//        		}
//        		/** AUT: henryyu1986@163.com DATE: 2011-12-30 S */
//
//        		if (TAG_SHORTCUT.equals(name)) {
//        			addUriShortcut(values, a);
//        		}
//        		a.recycle();
//        		type = parser.next();
//        	} 
//        }catch (XmlPullParserException e) {
//            Log.w(TAG, "Got exception parsing favorites.", e);
//        } catch (IOException e) {
//            Log.w(TAG, "Got exception parsing favorites.", e);
//        } catch (RuntimeException e) {
//            Log.w(TAG, "Got exception parsing favorites.", e);
//        }
//
//		
//	}
	 private void addUriShortcut(ContentValues values,
             TypedArray a) {
         Resources r = mContext.getResources();

         final int iconResId = a.getResourceId(R.styleable.Favorite_icon, 0);
         final int titleResId = a.getResourceId(R.styleable.Favorite_title, 0);

         Intent intent;
         String uri = null;
         try {
             uri = a.getString(R.styleable.Favorite_uri);
             intent = Intent.parseUri(uri, 0);
         } catch (URISyntaxException e) {
             Log.w(TAG, "Shortcut has malformed uri: " + uri);
             return; // Oh well
         }

         if (iconResId == 0 || titleResId == 0) {
             Log.w(TAG, "Shortcut is missing title or icon resource ID");
             return;
         }

         intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
         values.put(Favorites.INTENT, intent.toUri(0));
         values.put(Favorites.TITLE, r.getString(titleResId));
         values.put(Favorites.ITEM_TYPE, Favorites.ITEM_TYPE_SHORTCUT);
         values.put(Favorites.SPANX, 1);
         values.put(Favorites.SPANY, 1);
         values.put(Favorites.ICON_TYPE, Favorites.ICON_TYPE_RESOURCE);
         values.put(Favorites.ICON_PACKAGE, mContext.getPackageName());
         values.put(Favorites.ICON_RESOURCE, r.getResourceName(iconResId));
         
         
     	LauncherApplication app = (LauncherApplication) mContext
				.getApplicationContext();
		LauncherProvider cp = app.getLauncherProvider();
		 long id  = cp.generateNewId();
		 values.put(Favorites._ID, id);
		cp.insert(
				LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION,
				values);
        
     }
	private void cleanDatabase() {

		// extra clean widget id S.
		ContentResolver cr = mContext.getContentResolver();

		Cursor c = cr.query(
				ConstantPasser.Favorites.CONTENT_URI_NO_NOTIFICATION,
				new String[] { ConstantPasser.Favorites.APPWIDGET_ID }, null,
				null, null);
		try {
			AppWidgetHost widgetHost = ((XLauncher) mContext).getAppWidgetHost();
			int index = c.getColumnIndex(ConstantPasser.Favorites.APPWIDGET_ID);
			while (c.moveToNext()) {
				int widgetId = c.getInt(index);
				if (Debug.MAIN_DEBUG_SWITCH){
					R2.echo("Invalid widget id : " + widgetId);
				}
				widgetHost.deleteAppWidgetId(widgetId);
			}
		} catch (Exception e) {
		} finally {
			if (c != null){
				c.close();
			}
		}

		// clean and do not notify

		if (enableState.enableFolder || enableState.enableQuickEntries
				|| enableState.enableWidgets){
			cr.delete(ConstantPasser.Favorites.CONTENT_URI_NO_NOTIFICATION,
					null, null);
		}

		if (enableState.enablePriorities){
			cr.delete(ConstantPasser.Applications.CONTENT_URI, null, null);
		}

		/* RK_ID: RK_THEME .AUT: zhanggx1 . DATE: 2012-03-31 . S */
		Settings.System.putString(cr, SettingsValue.KEY_SET_THEME, null);
		/* RK_ID: RK_THEME .AUT: zhanggx1 . DATE: 2012-03-31 . E */
	}
    boolean returnValue = true;
	boolean reloadDesktop(boolean isDefault) {
         if(isDefault){
R2.echo(" reloadDesktop isDefault");

     		// load settings first
     					ProcessIndicator.getInstance(mContext).setState(
     							R.string.restore_settings);
     					SettingsReloader settingReloader = new SettingsReloader(
     							mContext);
     					settingReloader.process(profile);

     		// load priorities
     					ProcessIndicator.getInstance(mContext).setState(
     							R.string.restore_priorites);
     					PriorityReloader priorityReloader = ReloaderHelper.INSTANCE.new PriorityReloader(
     							mContext);
     					priorityReloader.process(profile);

     					ProcessIndicator.getInstance(mContext).setState(
     							R.string.restore_items);
     					WidgetReloader widgetReloader = ReloaderHelper.INSTANCE.new WidgetReloader(
     							mContext);
     					if(!(widgetReloader.process(profile))){
     						R2.echo("ProfileReloader "+ "widgetReloader returnValue = false;");
     						returnValue = false;
     					}
     					else{
     						R2.echo("ProfileReloader " +"widgetReloader returnValue = true;");
     					}
     		 /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-10 START */
     					ProcessIndicator.getInstance(mContext).setState(
     							mContext.getString(R.string.restore_items));
     					LeosE2EWidgetLoader leosWidgetReloader = ReloaderHelper.INSTANCE.new LeosE2EWidgetLoader(
     							mContext);
     					if(!(leosWidgetReloader.process(profile))){
     						R2.echo("ProfileReloader " +"leosWidgetReloader returnValue = false;");
     						returnValue = false;
     					}
     					else{
     						R2.echo("ProfileReloader "+ "leosWidgetReloader returnValue = true;");
     					}
     		 /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-10 END */

     		// load quick entries
     					QuickEntryReloader entryLoader = ReloaderHelper.INSTANCE.new QuickEntryReloader(
     							mContext);
     					if(!(entryLoader.process(profile))){
     						R2.echo("ProfileReloader "+ "entryRestoreThread returnValue = false;");
     						returnValue = false;
     					}
     					else{
     						R2.echo("ProfileReloader " +"entryLoader returnValue = true;");
     					}

     		// load folder
     					FolderReloader folderLoader = ReloaderHelper.INSTANCE.new FolderReloader(
     							mContext);
     					if(!(folderLoader.process(profile))){
     						R2.echo("ProfileReloader " +"folderLoader returnValue = false;");
     						returnValue = false;
     					}
     					else{
     						R2.echo("ProfileReloader "+ "folderLoader returnValue = true;");
     					}
     		return returnValue;
         }else{
		List<Thread> restoreThread = new ArrayList<Thread>();
//		ExecutorService executorService = Executors.newSingleThreadExecutor();  
 /* RK_ID:RK_LOAD_DESKTOP_OPTIMIZE AUT:liuyg1@lenovo.com DATE: 2013-3-28 END */	
	// load settings first
		if (enableState.enableSettings) {
			Thread settingRestoreThread = new Thread(
					"ProfileSettingReloaderThread") {
				public void run() {
					ProcessIndicator.getInstance(mContext).setState(
							R.string.restore_settings);
					SettingsReloader settingReloader = new SettingsReloader(
							mContext);
					settingReloader.process(profile);
				}
			};
			restoreThread.add(settingRestoreThread);
			settingRestoreThread.start();
		}

		// load priorities
		if (enableState.enablePriorities) {
			Thread priorityThread = new Thread("PrioritiesRestoreThread") {
				public void run() {
					ProcessIndicator.getInstance(mContext).setState(
							R.string.restore_priorites);
					PriorityReloader priorityReloader = ReloaderHelper.INSTANCE.new PriorityReloader(
							mContext);
					priorityReloader.process(profile);
				}
			};
			if (!performingDefault) {
				restoreThread.add(priorityThread);
				priorityThread.start();
			}
		}

		// load widgets
		if (enableState.enableWidgets) {
			Thread widgetThread = new Thread("WidgetRestoreThread") {
				public void run() {
					ProcessIndicator.getInstance(mContext).setState(
							R.string.restore_items);
					WidgetReloader widgetReloader = ReloaderHelper.INSTANCE.new WidgetReloader(
							mContext);
					if(!(widgetReloader.process(profile))){
						R2.echo("ProfileReloader "+ "widgetReloader returnValue = false;");
						returnValue = false;
					}
					else{
						R2.echo("ProfileReloader " +"widgetReloader returnValue = true;");
					}
				}
			};
			restoreThread.add(widgetThread);
			widgetThread.start();
		 /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-10 START */
			Thread LeosE2EWidgetThread = new Thread("LEOSE2EWidgetRestoreThread") {
				public void run() {
					if (Debug.MAIN_DEBUG_SWITCH)
						R2.echo("LEOSWidgetRestoreThread go . ") ;
					ProcessIndicator.getInstance(mContext).setState(
							mContext.getString(R.string.restore_items));
					LeosE2EWidgetLoader leosWidgetReloader = ReloaderHelper.INSTANCE.new LeosE2EWidgetLoader(
							mContext);
					if(!(leosWidgetReloader.process(profile))){
						R2.echo("ProfileReloader " +"leosWidgetReloader returnValue = false;");
						returnValue = false;
					}
					else{
						R2.echo("ProfileReloader "+ "leosWidgetReloader returnValue = true;");
					}
				}
			};
			restoreThread.add(LeosE2EWidgetThread);
			LeosE2EWidgetThread.start();
		 /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-10 END */
		}

		// load quick entries
		if (enableState.enableQuickEntries) {
			Thread entryRestoreThread = new Thread("QuickEntryThread") {
				public void run() {
					QuickEntryReloader entryLoader = ReloaderHelper.INSTANCE.new QuickEntryReloader(
							mContext);
					if(!(entryLoader.process(profile))){
						R2.echo("ProfileReloader "+ "entryRestoreThread returnValue = false;");
						returnValue = false;
					}
					else{
						R2.echo("ProfileReloader " +"entryLoader returnValue = true;");
					}
				}
			};

			restoreThread.add(entryRestoreThread);
			entryRestoreThread.start();
		}

		// load folder
		if (enableState.enableFolder) {
			Thread folderReloadThread = new Thread("FolderReloaderThread") {
				public void run() {
					FolderReloader folderLoader = ReloaderHelper.INSTANCE.new FolderReloader(
							mContext);
					if(!(folderLoader.process(profile))){
						R2.echo("ProfileReloader " +"folderLoader returnValue = false;");
						returnValue = false;
					}
					else{
						R2.echo("ProfileReloader "+ "folderLoader returnValue = true;");
					}
				}
			};
			restoreThread.add(folderReloadThread);
			folderReloadThread.start();
		}

		boolean done = false;
		int count = 3;
		while (!done) {
			count++;
			try {
				Thread.sleep(1200L);
			} catch (Exception e) {
			}

			int i = 0;
			for (; i < restoreThread.size(); i++) {
				if (restoreThread.get(i).isAlive()) {
					Thread curr = restoreThread.get(i);
					if (Debug.MAIN_DEBUG_SWITCH){
						R2.echo("ReloadDesktop Waiting for restore main threads . "
								+ restoreThread.get(i).getName()
								+ "  now elapse: " + count + "s");
					}

					ProcessIndicator.getInstance(mContext).setState(
							R.string.restore_items);
					curr.setPriority(Math.min(curr.getPriority() + 1,
							Thread.MAX_PRIORITY - 1));
					break;
				}
			}

			if (i >= restoreThread.size()) {
				done = true;
			}
		}
 /* RK_ID:RK_LOAD_DESKTOP_OPTIMIZE AUT:liuyg1@lenovo.com DATE: 2013-3-28 END */

		return returnValue;
         }
	}

	/**
	 * Settings Reloader
	 * 
	 * */
	class SettingsReloader {

		private Context mContext = null;

		public SettingsReloader(final Context context) {
			mContext = context;
		}

		public boolean process(ProfileInfo info) {
			try {
				// R2
				if (Debug.MAIN_DEBUG_SWITCH){
					R2.echo("Will restore settings ----------------------------------");
				}
				if (performingDefault) {
					// cleanNecessarySettings();
				}
				BackupableSetting.newInstance(mContext, "").restore(
						profile.settings);
				syncSettings();
				syncLockScreenSettings();
				syncCitySettings();

			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
	}

	// hacks
	void cleanNecessarySettings() {
		SharedPreferences pref = mContext.getSharedPreferences(
				"com.lenovo.launcher2.profiles",
				android.app.Activity.MODE_APPEND);
		pref.edit().clear().apply();
	}

	private void syncSettings() {
		SettingsValue.setNetworkSync(false);
		if (SettingsValue.isNetworkEnabled(mContext)){
			Settings.System.putInt(mContext.getContentResolver(),
					SettingsValue.PREF_NETWORK_ENABLER, 1);
		}
		else{
			Settings.System.putInt(mContext.getContentResolver(),
					SettingsValue.PREF_NETWORK_ENABLER, 0);
		}
		Intent intent = new Intent(SettingsValue.ACTION_NETWORK_ENABLER_CHANGED);
		Log.d("syncSettings",
				"isNetworkEnabled = "
						+ SettingsValue.isNetworkEnabled(mContext));
		intent.putExtra(SettingsValue.EXTRA_NETWORK_ENABLED,
				SettingsValue.isNetworkEnabled(mContext));
		mContext.sendBroadcast(intent);
	}

	private static final String IMAGE_SRC = "image_src";
	private static final String CITY_NAME = "city_name";
	private static final String TEMPS = "temps";
	private static final String CONDITION = "condition";

	private void syncCitySettings() {
		String pName = null;
		String pcode = null;
		String cityid = null;
		String cityname = null;
		SharedPreferences preferences = mContext.getSharedPreferences(
				"lockscreen", 0);
		if (preferences != null) {
			pName = preferences.getString("pname", null);
			pcode = preferences.getString("pcode", null);
			cityid = preferences.getString("cityid", null);
			cityname = preferences.getString("cityname", null);
		}

		String oldCityid = Settings.System.getString(
				mContext.getContentResolver(), "cityID");
		if (cityid != null && !cityid.equals(oldCityid)) {
			Settings.System.putString(mContext.getContentResolver(),
					"province_name", pName);
			Settings.System.putString(mContext.getContentResolver(),
					"province_code", pcode);
			Settings.System.putString(mContext.getContentResolver(), "cityID",
					cityid);
			Settings.System.putString(mContext.getContentResolver(),
					"city_name", cityname);

			// clear the old data
			Settings.System
					.putInt(mContext.getContentResolver(), IMAGE_SRC, -1);
			Settings.System.putString(mContext.getContentResolver(), CONDITION,
					"");
			Settings.System.putString(mContext.getContentResolver(), TEMPS, "");
			Settings.System.putInt(mContext.getContentResolver(), "day", 0);
			Settings.System.putInt(mContext.getContentResolver(), "hour", 0);
			Log.d("syncCitySettings", "send broadcast");
			Intent intent = new Intent(
					"com.lenovo.leos.widgets.weather.LOCATION_CHANGED");
			intent.putExtra("cityid", cityid);
			intent.putExtra("cityname", cityname);
			mContext.sendBroadcast(intent);
		}
	}

	private static final String LOCK_SCREEN_ON_OFF = "lock_screen_on_off";
	private static final String LOCK_SCREEN_WALLPAPER_ON_OFF = "lock_screen_wallpaper_on_off";

	private void syncLockScreenSettings() {
		SharedPreferences sharedPreferences = android.preference.PreferenceManager
				.getDefaultSharedPreferences(mContext);
		boolean lockScreenOn = sharedPreferences.getBoolean(
				"pref_lockscreen_lenovo_on", true);
		boolean useLenLockWallpaper = sharedPreferences.getBoolean(
				"pref_lockscreen_wallpaper_on", false);
		if (lockScreenOn){
			Settings.System.putInt(mContext.getContentResolver(),
					LOCK_SCREEN_ON_OFF, 1);
		}
		else{
			Settings.System.putInt(mContext.getContentResolver(),
					LOCK_SCREEN_ON_OFF, 0);
		}

		if (useLenLockWallpaper){
			Settings.System.putInt(mContext.getContentResolver(),
					LOCK_SCREEN_WALLPAPER_ON_OFF, 1);
		}
		else{
			Settings.System.putInt(mContext.getContentResolver(),
					LOCK_SCREEN_WALLPAPER_ON_OFF, 0);
		}
	}
}
