package com.lenovo.launcher2.commoninterface;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.backup.ProfileInteractiveHelper.SnapShotHelper;
import com.lenovo.launcher2.commoninterface.InfoFactory.AppInfo;
import com.lenovo.launcher2.commoninterface.InfoFactory.FolderInfo;
import com.lenovo.launcher2.commoninterface.InfoFactory.LeosE2EWidgetInfo;
import com.lenovo.launcher2.commoninterface.InfoFactory.PriorityInfo;
import com.lenovo.launcher2.commoninterface.InfoFactory.ProfileInfo;
import com.lenovo.launcher2.commoninterface.InfoFactory.QuickEntryInfo;
import com.lenovo.launcher2.commoninterface.InfoFactory.WidgetInfo;
import com.lenovo.launcher2.customizer.ConstantAdapter;
import com.lenovo.launcher2.customizer.Debug;
import com.lenovo.launcher2.customizer.ProcessIndicator;
import com.lenovo.launcher2.customizer.Utilities;
import com.lenovo.launcher2.customizer.ConstantAdapter.ProfilesAttributes.ContainerType;
import com.lenovo.launcher2.customizer.ConstantAdapter.ProfilesAttributes.FolderAttributes;
import com.lenovo.launcher2.customizer.ConstantAdapter.ProfilesAttributes.LeosWidgetAttributes;
import com.lenovo.launcher2.customizer.ConstantAdapter.ProfilesAttributes.PriorityAttributes;
import com.lenovo.launcher2.customizer.ConstantAdapter.ProfilesAttributes.QuickEntryAppAttributes;
import com.lenovo.launcher2.customizer.ConstantAdapter.ProfilesAttributes.WidgetAttributes;
import com.lenovo.launcher2.customizer.Debug.R2;

/**
 * value -- ProfileInfo ---> xml
 * */
public class ProfileExtracter {

	// for new ProfileManager
	public static final String PROFILE_FLATED_FILENAME = "profile.xml";

	private ProfileInfo profile = InfoFactory.INSTANCE.new ProfileInfo();
	public static final String WORK_DIR = ConstantAdapter.DIR_PARENT_OF_STORAGE_BACKUP_FILE
			+ File.separator + ConstantAdapter.DIR_TO_STORAGE_BACKUP_FILE;

	// private String profileName = null;

	private String profileFullStoragePath = null;
	private String profileSnapshotStorageParentPath = null;
	private String profileSnapshotStorageWidgetPath = null;
	private String profileSnapshotStorageShortcutPath = null;

	private List<FolderInfo> folders = new ArrayList<FolderInfo>();
	private List<WidgetInfo> widgets = new ArrayList<WidgetInfo>();
	private List<PriorityInfo> priorities = new ArrayList<PriorityInfo>();
	// private List<SettingInfo> settings = new ArrayList<SettingInfo>();
	private List<QuickEntryInfo> quickentries = new ArrayList<QuickEntryInfo>();

	private Context mContext = null;
	
	 /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-10 START */
	private List<LeosE2EWidgetInfo> leoswidgets = new ArrayList<LeosE2EWidgetInfo>();

	 /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-10 END */
	public ProfileExtracter(Context context, String name) {
		this(context, name, name);
	}

	public ProfileExtracter(Context context, String profileKey,
			String profileName) {
		mContext = context;
		// this.profileName = profileName;
		profile.name = profileName;
		try {
			profile.setKey(profileKey);

			// paths
			profileFullStoragePath = WORK_DIR + File.separator + profileName;

			profileSnapshotStorageParentPath = profileFullStoragePath
					+ ConstantAdapter.DIR_SNAPSHOT;
			profileSnapshotStorageWidgetPath = profileSnapshotStorageParentPath
					+ ConstantAdapter.DIR_SNAPSHOT_WIDGET;
			profileSnapshotStorageShortcutPath = profileSnapshotStorageParentPath
					+ ConstantAdapter.DIR_SNAPSHOT_SHORTCUT;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getStoragePath() {
		return profileFullStoragePath;
	}

	public boolean extractProfile() {

		// firstly , ensure dirs
		List<Thread> backupThread = new ArrayList<Thread>();
		
		Utilities.newInstance().ensureDir(
				ConstantAdapter.DIR_PARENT_OF_STORAGE_BACKUP_FILE);
		Utilities.newInstance().ensureDir(profile.getKey());
		Utilities.newInstance().ensureDir(profileFullStoragePath);
		Utilities.newInstance().ensureDir(profileSnapshotStorageParentPath);
		Utilities.newInstance().ensureDir(profileSnapshotStorageWidgetPath);
		Utilities.newInstance().ensureDir(profileSnapshotStorageShortcutPath);

		// extract settings first!~
		Thread settingBackupThread = new Thread(
				"settingBackupThread") {
			public void run() {

				ProcessIndicator.getInstance(mContext).setState(
						R.string.backup_extract_settings);
				extractAllSettings();
			}
		};
		settingBackupThread.start();
		backupThread.add(settingBackupThread);

		// secondly, extract favorite ItemsInDatabase
		Thread favoriteBackupThread = new Thread(
				"favoriteBackupThread") {
			public void run() {
				ProcessIndicator.getInstance(mContext).setState(
						R.string.backup_extract_items);
				extractFavoriteItemInDatabase();
				}
		};
		favoriteBackupThread.start();
		backupThread.add(favoriteBackupThread);


		// exract Priorities
		Thread prioritiesBackupThread = new Thread(
				"prioritiesBackupThread") {
			public void run() {
				ProcessIndicator.getInstance(mContext).setState(
						R.string.backup_extract_priorites);
				extractAllPrioritites();			
				}
		};
		prioritiesBackupThread.start();
		backupThread.add(prioritiesBackupThread);

		

		// flat the profile to xml
		boolean done = false;
		int count = 3;
		while (!done) {
			count++;
			try {
				Thread.sleep(200L);
			} catch (Exception e) {
			}

			int i = 0;
			for (; i < backupThread.size(); i++) {
				if (backupThread.get(i).isAlive()) {
					Thread curr = backupThread.get(i);
					if (Debug.MAIN_DEBUG_SWITCH){
						R2.echo("ReloadDesktop Waiting for restore main threads . "
								+ backupThread.get(i).getName()
								+ "  now elapse: " + count + "s");
					}

					ProcessIndicator.getInstance(mContext).setState(
							R.string.restore_items);
					curr.setPriority(Math.min(curr.getPriority() + 1,
							Thread.MAX_PRIORITY - 1));
					break;
				}
			}

			if (i >= backupThread.size()) {
				done = true;
			}
		}
		Long systemTime2 = System.currentTimeMillis();
		Log.d("liuyg123","XmlProfileFlater "+systemTime2);
		ProcessIndicator.getInstance(mContext).setState(
				R.string.backup_flatten_profile);
		XmlProfileFlater.getInstance(mContext).flat(profile);

		return true;
	}

	private boolean extractAllPrioritites() {

		// ALL APP LIST ORDER
		final ContentResolver cr = mContext.getContentResolver();
		final Cursor c = cr.query(ConstantPasser.Applications.CONTENT_URI,
				new String[] { ConstantPasser.Applications.LABEL,
						ConstantPasser.Applications.CLASS,
						ConstantPasser.Applications.ITEM_DRAGABLE }, null,
				null, ConstantPasser.Applications.CELL_INDEX);
		try {

			PriorityInfo priorityAllApplist = InfoFactory.INSTANCE.new PriorityInfo();
			priorityAllApplist.name = PriorityAttributes.NAME_ALLAPPLIST;
			priorityAllApplist.setKey(priorityAllApplist.name);
			final int classIndex = c
					.getColumnIndex(ConstantPasser.Applications.CLASS);
			final int labelIndex = c
					.getColumnIndex(ConstantPasser.Applications.LABEL);
			final int dragableIndex = c
					.getColumnIndex(ConstantPasser.Applications.ITEM_DRAGABLE);

			int index = 0;
			while (c.moveToNext()) {
				AppInfo appInfo = InfoFactory.INSTANCE.new AppInfo();
				if (Debug.MAIN_DEBUG_SWITCH)
					R2.echo("Added profile app ");
				String[] who = c.getString(classIndex).split(File.separator);

				String label = c.getString(labelIndex);
				if (label != null && !"".equals(label))
					appInfo.attrList.add(InfoFactory.INSTANCE.new Attribute(
							PriorityAttributes.TITLE, label));
				appInfo.attrList.add(InfoFactory.INSTANCE.new Attribute(
						PriorityAttributes.ORDER, String.valueOf(index)));
				appInfo.attrList.add(InfoFactory.INSTANCE.new Attribute(
						PriorityAttributes.PACKAGENAME, who[0]));
				appInfo.attrList.add(InfoFactory.INSTANCE.new Attribute(
						PriorityAttributes.CLASSNAME, who[1]));
				appInfo.attrList.add(InfoFactory.INSTANCE.new Attribute(
						PriorityAttributes.DRAGABLE, String.valueOf(c
								.getInt(dragableIndex))));

				priorityAllApplist.priorityRules.add(appInfo);

				index++;
			}

			priorities.add(priorityAllApplist);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			c.close();
		}

		profile.priorities = priorities;
		return true;
	}

	private class IndexInfo {
		int idIndex = 0;
		int itemTypeIndex = 0;
		int appWidgetIdIndex = 0;
		int intentIndex = 0;
		int screenIndex = 0;
		int cellXIndex = 0;
		int cellYIndex = 0;
		int spanXIndex = 0;
		int spanYIndex = 0;
		int uriIndex = 0;
		int containerIndex = 0;

		int titleIndex = 0;
		int titleReplacedIndex = 0;

		int iconReplaceIndex = 0;
		int iconResourceIndex = 0;
		int iconPackageIndex = 0;

		int iconType = 0;

		// for widget
		int icon = 0;
		int needConfig = 0;
	}

	private void extractFavoriteItemInDatabase() {

		final ContentResolver cr = mContext.getContentResolver();
		final Cursor c = cr.query(ConstantPasser.Favorites.CONTENT_URI, null,
				null, null, null);
		IndexInfo indexInfo = new IndexInfo();
		try {
			indexInfo.idIndex = c
					.getColumnIndexOrThrow(ConstantPasser.Favorites._ID);
			indexInfo.itemTypeIndex = c
					.getColumnIndexOrThrow(ConstantPasser.Favorites.ITEM_TYPE);
			indexInfo.appWidgetIdIndex = c
					.getColumnIndexOrThrow(ConstantPasser.Favorites.APPWIDGET_ID);
			indexInfo.intentIndex = c
					.getColumnIndexOrThrow(ConstantPasser.Favorites.INTENT);
			indexInfo.screenIndex = c
					.getColumnIndexOrThrow(ConstantPasser.Favorites.SCREEN);
			indexInfo.cellXIndex = c
					.getColumnIndexOrThrow(ConstantPasser.Favorites.CELLX);
			indexInfo.cellYIndex = c
					.getColumnIndexOrThrow(ConstantPasser.Favorites.CELLY);
			indexInfo.spanXIndex = c
					.getColumnIndexOrThrow(ConstantPasser.Favorites.SPANX);
			indexInfo.spanYIndex = c
					.getColumnIndexOrThrow(ConstantPasser.Favorites.SPANY);
			indexInfo.titleIndex = c
					.getColumnIndexOrThrow(ConstantPasser.Favorites.TITLE);
			// indexInfo.iconBitmapIndex =
			// c.getColumnIndexOrThrow(ConstantPasser.Favorites.ICON);
			indexInfo.uriIndex = c
					.getColumnIndexOrThrow(ConstantPasser.Favorites.URI);
			indexInfo.containerIndex = c
					.getColumnIndexOrThrow(ConstantPasser.Favorites.CONTAINER);
			indexInfo.titleReplacedIndex = c
					.getColumnIndex(ConstantPasser.Favorites.TITLE_REPLACE);

			indexInfo.iconReplaceIndex = c
					.getColumnIndex(ConstantPasser.Favorites.ICON_REPLACE);
			indexInfo.iconResourceIndex = c
					.getColumnIndex(ConstantPasser.Favorites.ICON_RESOURCE);
			indexInfo.iconPackageIndex = c
					.getColumnIndex(ConstantPasser.Favorites.ICON_PACKAGE);

			indexInfo.iconType = c
					.getColumnIndex(ConstantPasser.Favorites.ICON_TYPE);

			// R2
			indexInfo.icon = c.getColumnIndex(ConstantPasser.Favorites.ICON);
			indexInfo.needConfig = c
					.getColumnIndex(ConstantPasser.Favorites.NEED_CONFIG_WIDGET);

			AppWidgetManager awm = AppWidgetManager.getInstance(mContext);

			String snapShotDir = null;

			Long currentContainer = Long.MIN_VALUE;

			while (c.moveToNext()) {

				try {
					ProcessIndicator.getInstance(mContext).setState(
							R.string.backup_extract_items);

					currentContainer = c.getLong(indexInfo.containerIndex);

					byte methodToSaveItemSnapshot = ConstantAdapter.ITEM_SNAPSHOT_SAVE_METHOD_NONE;

					int itemType = c.getInt(indexInfo.itemTypeIndex);

					switch (itemType) {
					// folder
					case ConstantPasser.Favorites.ITEM_TYPE_FOLDER:
						FolderInfo folderInfo = InfoFactory.INSTANCE.new FolderInfo();

						// container
						String sContainer = c.getInt(indexInfo.containerIndex) == ConstantPasser.Favorites.CONTAINER_DESKTOP ? ContainerType.CONTAINER_DESKTOP
								: ContainerType.CONTAINER_HOTSEAT;

						// retrieve config
						folderInfo.config.configValues
								.add(InfoFactory.INSTANCE.new Attribute(
										FolderAttributes.CELLX,
										c.getString(indexInfo.cellXIndex)));
						folderInfo.config.configValues
								.add(InfoFactory.INSTANCE.new Attribute(
										FolderAttributes.CELLY,
										c.getString(indexInfo.cellYIndex)));
						folderInfo.config.configValues
								.add(InfoFactory.INSTANCE.new Attribute(
										FolderAttributes.TITLE,
										c.getString(indexInfo.titleIndex)));
						folderInfo.config.configValues
								.add(InfoFactory.INSTANCE.new Attribute(
										FolderAttributes.SCREEN,
										c.getString(indexInfo.screenIndex)));
						folderInfo.config.configValues
								.add(InfoFactory.INSTANCE.new Attribute(
										FolderAttributes.CONTAINER, sContainer));
						folderInfo.config.configValues
								.add(InfoFactory.INSTANCE.new Attribute(
										FolderAttributes.ICON, c
												.getLong(indexInfo.idIndex)
												+ ".png"));

						// get all snapshot, use shortcut path
						snapShotDir = profileSnapshotStorageShortcutPath;
						// retrieve applist
						final ContentResolver crApp = mContext
								.getContentResolver();
						final Cursor cApp = crApp
								.query(ConstantPasser.Favorites.CONTENT_URI,
										null,
										"container=?",
										new String[] { c
												.getString(indexInfo.idIndex) },
										null);
						try {
							// R2
							currentContainer = c.getLong(indexInfo.idIndex);
							while (cApp.moveToNext()) {
								try {
									if (Debug.MAIN_DEBUG_SWITCH)
										R2.echo("folder -----dfdfdf-----------__> "
												+ cApp.getString(indexInfo.intentIndex));

									QuickEntryInfo entryInfo = retrieveItemsToQuickEntryInfo(
											cApp, indexInfo);

									AppInfo appInfo = InfoFactory.INSTANCE.new AppInfo();
									appInfo.attrList = entryInfo.attrList;
									appInfo.name = entryInfo.name;
									appInfo.setKey(entryInfo.getKey());

									// icon handle in case can not fetch later

//									methodToSaveItemSnapshot = ConstantAdapter.ITEM_SNAPSHOT_SAVE_METHOD_SHORTCUT;
						
//									SnapShotHelper
//											.newInstance(mContext)
//											.saveItemInfoSnapshot(
//													cApp.getLong(indexInfo.idIndex),
//													cApp.getInt(indexInfo.cellXIndex),
//													cApp.getInt(indexInfo.cellYIndex),
//													cApp.getInt(indexInfo.spanXIndex),
//													cApp.getInt(indexInfo.spanYIndex),
//													cApp.getInt(indexInfo.screenIndex),
//													currentContainer,
//													snapShotDir,
//													methodToSaveItemSnapshot,
//													cApp.getInt(indexInfo.itemTypeIndex),
//													null);
									// slot to folderApps
									folderInfo.appList.folderApps.add(appInfo);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}

							// save folder item snapshot
							try {
	//							methodToSaveItemSnapshot = ConstantAdapter.ITEM_SNAPSHOT_SAVE_METHOD_FOLDER;
								byte[] blob = c
										.getBlob(indexInfo.iconReplaceIndex);
								if (blob != null) {
								Bitmap quichF = BitmapFactory.decodeByteArray(
										blob, 0, blob.length);
								
//									SnapShotHelper
//											.newInstance(mContext)
//											.saveItemInfoSnapshot(
//													c.getLong(indexInfo.idIndex),
//													-1, -1, -1, -1, -1, -1,
//													snapShotDir,
//													methodToSaveItemSnapshot,
//													-1, quichF);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							folders.add(folderInfo);
							break;

						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							cApp.close();
						}

						// widget
					case ConstantPasser.Favorites.ITEM_TYPE_APPWIDGET:// widget
					case ConstantPasser.Favorites.ITEM_TYPE_COMMEND_APPWIDGET:
						WidgetInfo widgetInfo = InfoFactory.INSTANCE.new WidgetInfo();

						int appWidgetId = c.getInt(indexInfo.appWidgetIdIndex);

						widgetInfo.attibutes
								.add(InfoFactory.INSTANCE.new Attribute(
										WidgetAttributes.CELLX,
										c.getString(indexInfo.cellXIndex)));
						widgetInfo.attibutes
								.add(InfoFactory.INSTANCE.new Attribute(
										WidgetAttributes.CELLY,
										c.getString(indexInfo.cellYIndex)));
						widgetInfo.attibutes
								.add(InfoFactory.INSTANCE.new Attribute(
										WidgetAttributes.SPANX,
										c.getString(indexInfo.spanXIndex)));
						widgetInfo.attibutes
								.add(InfoFactory.INSTANCE.new Attribute(
										WidgetAttributes.SAPNY,
										c.getString(indexInfo.spanYIndex)));
						widgetInfo.attibutes
								.add(InfoFactory.INSTANCE.new Attribute(
										WidgetAttributes.SCREEN,
										c.getString(indexInfo.screenIndex)));

						final AppWidgetProviderInfo providerInfo = awm
								.getAppWidgetInfo(appWidgetId);

						String widgetPackageName = null;
						String widgetProviderName = null;
						try {
							widgetPackageName = providerInfo.provider
									.getPackageName();
							widgetProviderName = providerInfo.provider
									.getClassName();
						} catch (Exception e) {
							try {
								String intentDesc = c
										.getString(indexInfo.intentIndex);
								Intent intent = Intent.parseUri(intentDesc, 0);
								widgetPackageName = intent.getComponent()
										.getPackageName();
								widgetProviderName = intent.getComponent()
										.getClassName();
							} catch (Exception e1) {
								e1.printStackTrace();
							}

							itemType = ConstantPasser.Favorites.ITEM_TYPE_COMMEND_APPWIDGET;
						}

						widgetInfo.attibutes
								.add(InfoFactory.INSTANCE.new Attribute(
										WidgetAttributes.PACKAGENAME,
										widgetPackageName));
						widgetInfo.attibutes
								.add(InfoFactory.INSTANCE.new Attribute(
										WidgetAttributes.CLASSNAME,
										widgetProviderName));
						widgetInfo.attibutes
								.add(InfoFactory.INSTANCE.new Attribute(
										WidgetAttributes.SNAPTHOT,
										c.getLong(indexInfo.idIndex)
												+ ConstantAdapter.SUFFIX_FOR_PREVIEW_SNAPSHOT));

						// handle title
						String title = null;

						try {
							title = c.getString(indexInfo.titleIndex);
							if (title == null || "".equals(title.trim())) {
								// fetch from database
								title = providerInfo.label;
							}
						} catch (Exception e1) {
							e1.printStackTrace();
						}

						if (title != null)
							widgetInfo.attibutes
									.add(InfoFactory.INSTANCE.new Attribute(
											WidgetAttributes.LABEL, title));

						methodToSaveItemSnapshot = ConstantAdapter.ITEM_SNAPSHOT_SAVE_METHOD_WIDGET;

						snapShotDir = profileSnapshotStorageWidgetPath;

						widgets.add(widgetInfo);
						break;

					// QuickEntry
					case ConstantPasser.Favorites.ITEM_TYPE_APPLICATION:
					case ConstantPasser.Favorites.ITEM_TYPE_SHORTCUT:
					case ConstantPasser.Favorites.ITEM_TYPE_COMMEND_SHORTCUT:
						if(itemType == ConstantPasser.Favorites.ITEM_TYPE_SHORTCUT){
							methodToSaveItemSnapshot = ConstantAdapter.ITEM_SNAPSHOT_SAVE_METHOD_SHORTCUT;
							// snapshot dir
							snapShotDir = profileSnapshotStorageShortcutPath;
						}
						try {
							// filter folder, folder is a unfixed container
							int iContainer = c.getInt(indexInfo.containerIndex);
							if (!(iContainer == ConstantPasser.Favorites.CONTAINER_DESKTOP || iContainer == ConstantPasser.Favorites.CONTAINER_HOTSEAT)) {
								break;
							}

							QuickEntryInfo entryInfo = retrieveItemsToQuickEntryInfo(
									c, indexInfo);

//							methodToSaveItemSnapshot = ConstantAdapter.ITEM_SNAPSHOT_SAVE_METHOD_SHORTCUT;

							// snapshot dir
//							snapShotDir = profileSnapshotStorageShortcutPath;

							// slot to quick entry
							quickentries.add(entryInfo);
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;			
					 /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-10 START */
					case ConstantPasser.Favorites.ITEM_TYPE_LEOSWIDGET_VIEW:
						if (Debug.MAIN_DEBUG_SWITCH)
							R2.echo("TOUCH XMPLUGIN VIEW .");
						try {
							LeosE2EWidgetInfo leosWidgetInfo = InfoFactory.INSTANCE.new LeosE2EWidgetInfo();
							leosWidgetInfo.attibutes
									.add(InfoFactory.INSTANCE.new Attribute(
											LeosWidgetAttributes.CLASS_NAME,
											c.getString(indexInfo.titleIndex)));
							leosWidgetInfo.attibutes
									.add(InfoFactory.INSTANCE.new Attribute(
											LeosWidgetAttributes.PACKAGE_NAME,
											c.getString(indexInfo.uriIndex)));
							leosWidgetInfo.attibutes
									.add(InfoFactory.INSTANCE.new Attribute(
											LeosWidgetAttributes.CELLX,
											"" + c.getInt(indexInfo.cellXIndex)));
							leosWidgetInfo.attibutes
									.add(InfoFactory.INSTANCE.new Attribute(
											LeosWidgetAttributes.CELLY,
											"" + c.getInt(indexInfo.cellYIndex)));
							leosWidgetInfo.attibutes
									.add(InfoFactory.INSTANCE.new Attribute(
											LeosWidgetAttributes.SCREEN,
											""
													+ c.getInt(indexInfo.screenIndex)));
							leosWidgetInfo.attibutes
									.add(InfoFactory.INSTANCE.new Attribute(
											LeosWidgetAttributes.CONTAINER,
											""
													+ c.getInt(indexInfo.containerIndex)));
							leosWidgetInfo.attibutes
									.add(InfoFactory.INSTANCE.new Attribute(
											LeosWidgetAttributes.SPANX,
											"" + c.getInt(indexInfo.spanXIndex)));
							leosWidgetInfo.attibutes
									.add(InfoFactory.INSTANCE.new Attribute(
											LeosWidgetAttributes.SPANY,
											"" + c.getInt(indexInfo.spanYIndex)));
							
							/*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. START***/
							leosWidgetInfo.attibutes
							.add(InfoFactory.INSTANCE.new Attribute(
									LeosWidgetAttributes.SNAPTHOT,
									c.getLong(indexInfo.idIndex)
											+ ConstantAdapter.SUFFIX_FOR_PREVIEW_SNAPSHOT));
							/*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. END***/


							leoswidgets.add(leosWidgetInfo);

							/*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. S***/
							//methodToSaveItemSnapshot = ConstantAdapter.ITEM_SNAPSHOT_SAVE_METHOD_NONE;
							methodToSaveItemSnapshot = ConstantAdapter.ITEM_SNAPSHOT_SAVE_METHOD_WIDGET;
							snapShotDir = profileSnapshotStorageWidgetPath;
							/*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. E***/
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					  /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-10 END */
					default:
						break;

					} // end switch

					// R2 try to get snapshot for widget
					// we need not fetch snapshot again in this case
					Bitmap bmpForFakeAndConfigableWidget = null;

					if (itemType == ConstantPasser.Favorites.ITEM_TYPE_COMMEND_APPWIDGET
							|| c.getInt(indexInfo.needConfig) == 1) {
						byte[] blob = c.getBlob(indexInfo.icon);
						bmpForFakeAndConfigableWidget = BitmapFactory
								.decodeByteArray(blob, 0, blob.length);
					}

					// R2 try to save snapshot
					if (Debug.MAIN_DEBUG_SWITCH)
						R2.echo("schedule");
					SnapShotHelper.newInstance(mContext).saveItemInfoSnapshot(
							c.getLong(indexInfo.idIndex),
							c.getInt(indexInfo.cellXIndex),
							c.getInt(indexInfo.cellYIndex),
							c.getInt(indexInfo.spanXIndex),
							c.getInt(indexInfo.spanYIndex),
							c.getInt(indexInfo.screenIndex), currentContainer,
							snapShotDir, methodToSaveItemSnapshot, itemType,
							bmpForFakeAndConfigableWidget);
					// 2R
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}

		profile.folders = folders;
		profile.widgets = widgets;
		profile.quickentries = quickentries;
		 /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-10 START */
		profile.leosE2EWidgets = leoswidgets;		
		 /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-10 END */
	}

	private QuickEntryInfo retrieveItemsToQuickEntryInfo(Cursor c,
			IndexInfo indexInfo) {
		QuickEntryInfo entryInfo = InfoFactory.INSTANCE.new QuickEntryInfo();

		String intentDescription = c.getString(indexInfo.intentIndex);
		String uriDescription = c.getString(indexInfo.uriIndex);
		Intent targetIntent = null;
		ComponentName cn = null;
		try {
			targetIntent = Intent.parseUri(intentDescription, 0);
			cn = targetIntent.getComponent();
		} catch (Exception e) {
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("---------------------rewre---------_!");
			e.printStackTrace();
		}

		if (cn == null || LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT == c.getInt(indexInfo.itemTypeIndex)) {
			// handle in-normal ones -- real shortcut
			if (uriDescription != null) {
				entryInfo.attrList.add(InfoFactory.INSTANCE.new Attribute(
						QuickEntryAppAttributes.URI, intentDescription));
			}

			entryInfo.attrList.add(InfoFactory.INSTANCE.new Attribute(
					QuickEntryAppAttributes.ACTION, intentDescription));

		} else {
			String entryPackageName = cn.getPackageName();
			String entryClassName = cn.getClassName();
			entryInfo.attrList.add(InfoFactory.INSTANCE.new Attribute(
					QuickEntryAppAttributes.PACKAGENAME, entryPackageName));
			entryInfo.attrList.add(InfoFactory.INSTANCE.new Attribute(
					QuickEntryAppAttributes.CLASSNAME, entryClassName));

		}

		// title
		// title may replace!
		String titleOrigin = c.getString(indexInfo.titleIndex);
		String titleReplace = c.getString(indexInfo.titleReplacedIndex);
		// String title = titleReplace != null && !"".equals(titleReplace) ?
		// titleReplace
		// : titleOrigin;

		if (Debug.MAIN_DEBUG_SWITCH)
			R2.echo("Title :              ------------------->  " + "  , !"
					+ titleOrigin + " , " + titleReplace);

		entryInfo.attrList.add(InfoFactory.INSTANCE.new Attribute(
				QuickEntryAppAttributes.TITLE, titleOrigin));

		entryInfo.attrList.add(InfoFactory.INSTANCE.new Attribute(
				QuickEntryAppAttributes.REPLACE_TITLE, titleReplace));

		entryInfo.attrList.add(InfoFactory.INSTANCE.new Attribute(
				QuickEntryAppAttributes.CELLX, c
						.getString(indexInfo.cellXIndex)));
		entryInfo.attrList.add(InfoFactory.INSTANCE.new Attribute(
				QuickEntryAppAttributes.CELLY, c
						.getString(indexInfo.cellYIndex)));
		entryInfo.attrList.add(InfoFactory.INSTANCE.new Attribute(
				QuickEntryAppAttributes.SCREEN, c
						.getString(indexInfo.screenIndex)));
		entryInfo.attrList.add(InfoFactory.INSTANCE.new Attribute(
				QuickEntryAppAttributes.ICON, c.getLong(indexInfo.idIndex)
						+ ConstantAdapter.SUFFIX_FOR_PREVIEW_SNAPSHOT));

		// iconResource
		entryInfo.attrList.add(InfoFactory.INSTANCE.new Attribute(
				QuickEntryAppAttributes.ICONRESOURCE, c
						.getString(indexInfo.iconResourceIndex)));

		// mark weather icon was an replaced one.
		boolean isIconReplaced = c.getBlob(indexInfo.iconReplaceIndex) != null;
		if (isIconReplaced) {
			entryInfo.attrList.add(InfoFactory.INSTANCE.new Attribute(
					QuickEntryAppAttributes.ICONREPLACED_MARK, "true"));
		} else
			entryInfo.attrList.add(InfoFactory.INSTANCE.new Attribute(
					QuickEntryAppAttributes.ICONREPLACED_MARK, "false"));

		// iconType
		entryInfo.attrList.add(InfoFactory.INSTANCE.new Attribute(
				QuickEntryAppAttributes.ICON_TYPE, String.valueOf(c
						.getInt(indexInfo.iconType))));

		// itemType
		entryInfo.attrList.add(InfoFactory.INSTANCE.new Attribute(
				QuickEntryAppAttributes.ITEM_TYPE, String.valueOf(c
						.getInt(indexInfo.itemTypeIndex))));

		// container
		Long entryContainer = c.getLong(indexInfo.containerIndex);
		String container = String.valueOf(entryContainer);
		if (entryContainer == ConstantPasser.Favorites.CONTAINER_HOTSEAT)
			container = ContainerType.CONTAINER_HOTSEAT;
		else if (entryContainer == ConstantPasser.Favorites.CONTAINER_DESKTOP)
			container = ContainerType.CONTAINER_DESKTOP;

		entryInfo.attrList.add(InfoFactory.INSTANCE.new Attribute(
				QuickEntryAppAttributes.CONTAINER, container));

		// iconPackage
		entryInfo.attrList.add(InfoFactory.INSTANCE.new Attribute(
				QuickEntryAppAttributes.ICONPACKAGE, c
						.getString(indexInfo.iconPackageIndex)));

		return entryInfo;
	}

	boolean extractAllSettings() {
		try {

			profile.settings = BackupableSetting.newInstance(mContext, "")
					.retrieve(mContext);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

}
