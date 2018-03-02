package com.lenovo.launcher2.backup;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
//import android.net.Uri;
//import android.provider.BaseColumns;
import android.util.Log;
//import android.view.View;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;
//import com.lenovo.launcher.components.XAllAppFace.XCellLayout;
//import com.lenovo.launcher.components.XAllAppFace.XHotseat;
import com.lenovo.launcher.components.XAllAppFace.XLauncher;
import com.lenovo.launcher.components.XAllAppFace.XLauncherModel;
import com.lenovo.launcher.components.XAllAppFace.XPagedView;
import com.lenovo.launcher.components.XAllAppFace.XPagedViewItem;
import com.lenovo.launcher.components.XAllAppFace.XWorkspace;

import com.lenovo.launcher2.LauncherProvider;
import com.lenovo.launcher2.commoninterface.ApplicationInfo;
import com.lenovo.launcher2.commoninterface.AttributesManager;
import com.lenovo.launcher2.commoninterface.ConstantPasser;
import com.lenovo.launcher2.commoninterface.FolderInfo;
import com.lenovo.launcher2.commoninterface.InfoFactory;
import com.lenovo.launcher2.commoninterface.InfoFactory.TitleInfo;
import com.lenovo.launcher2.commoninterface.ItemInfo;
import com.lenovo.launcher2.commoninterface.LauncherAppWidgetInfo;
import com.lenovo.launcher2.commoninterface.LauncherSettings;
import com.lenovo.launcher2.commoninterface.LenovoWidgetViewInfo;
import com.lenovo.launcher2.commoninterface.ShortcutInfo;
import com.lenovo.launcher2.commoninterface.InfoFactory.AppInfo;
import com.lenovo.launcher2.commoninterface.InfoFactory.LeosE2EWidgetInfo;
import com.lenovo.launcher2.commoninterface.InfoFactory.PriorityInfo;
import com.lenovo.launcher2.commoninterface.InfoFactory.ProfileInfo;
import com.lenovo.launcher2.commoninterface.InfoFactory.QuickEntryInfo;
import com.lenovo.launcher2.commoninterface.InfoFactory.TitlesInfo;
import com.lenovo.launcher2.commoninterface.InfoFactory.WidgetInfo;
//import com.lenovo.launcher2.commoninterface.LauncherSettings.Applications;
import com.lenovo.launcher2.commoninterface.LauncherSettings.Favorites;
import com.lenovo.launcher2.customizer.ConstantAdapter;
import com.lenovo.launcher2.customizer.ConstantAdapter.ProfilesAttributes.FolderTitleAttributes;
import com.lenovo.launcher2.customizer.Debug;
import com.lenovo.launcher2.customizer.GlobalDefine;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.Utilities;
import com.lenovo.launcher2.customizer.ConstantAdapter.ProfilesAttributes.ContainerType;
import com.lenovo.launcher2.customizer.ConstantAdapter.ProfilesAttributes.FolderAttributes;
import com.lenovo.launcher2.customizer.ConstantAdapter.ProfilesAttributes.LeosWidgetAttributes;
import com.lenovo.launcher2.customizer.ConstantAdapter.ProfilesAttributes.PriorityAttributes;
import com.lenovo.launcher2.customizer.ConstantAdapter.ProfilesAttributes.QuickEntryAppAttributes;
import com.lenovo.launcher2.customizer.ConstantAdapter.ProfilesAttributes.WidgetAttributes;
import com.lenovo.launcher2.customizer.Debug.R2;
import com.lenovo.launcher2.customizer.Debug.R3;
import com.lenovo.launcher2.customizer.Debug.R5;

/**
 * This class will be the helper the ProfileReloader
 * 
 * @author chengliang
 * */
public class ProfileInteractiveHelper {

	

	// 2R

	public static final ProfileInteractiveHelper INSTANCE = new ProfileInteractiveHelper();

	public static String getApplicationInfoTitle(XLauncher launcher, Intent data) {
		ShortcutInfo info = launcher.getModel().getShortcutInfo(
				launcher.getPackageManager(), data, launcher);
		return info.title.toString();
	}

	public static class SnapShotHelper {
		private Context mContext = null;

		private SnapShotHelper(Context context) {
			mContext = context;
		}

		public static SnapShotHelper newInstance(Context context) {
			return new SnapShotHelper(context);
		}

		/**
		 * save the snapshot indicated by the given position and spanition
		 * 
		 * */
		public boolean saveItemInfoSnapshot(long idInDb, int cellX, int cellY,
				int spanX, int spanY, int screen, long containerDirectly,
				String pathOfParentDir, final int flagOfSaveMethod,
				final int itemType, Bitmap quickFetch) {
			// XMUI
			if (flagOfSaveMethod == ConstantAdapter.ITEM_SNAPSHOT_SAVE_METHOD_NONE) {
				if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
					R2.echo("No need to snap .");
				return true;
			}
			// XMUI
			try {
				final Map<String, Bitmap> bitmapToSave = new HashMap<String, Bitmap>();
				String pathToSave = null;

				if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
					R2.echo("The tyep is : " + flagOfSaveMethod);
				switch (flagOfSaveMethod) {
				case ConstantAdapter.ITEM_SNAPSHOT_SAVE_METHOD_WIDGET:
					XPagedViewItem targetView = null;
					try {

						if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
							R2.echo("Touch widget snap 1");
						// while backup , the context is an launcher context
						XWorkspace workspace = ((XLauncher) mContext).getWorkspace();

						// R2
						if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
							R2.echo("Method is  -----------------> "
									+ flagOfSaveMethod);
						// 2R

						boolean found = false;
						
						// get snaps
						XPagedView page = workspace.getPagedView();
						targetView = page.findPageItemAt(screen, cellX, cellY);
						if( targetView != null ){
							found = true;
							if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH){
								R2.echo(" Now We touch Screen Item : " + targetView);
							}
						}
						//
						// if not found we need look up the hotseat
						if (!found) {
							if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
								R2.echo(" ALSSOOOOOO  Not Found ! ignore~");
							return true;
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					Bitmap bitmap = null;
					try {
						if (itemType == ConstantPasser.Favorites.ITEM_TYPE_COMMEND_APPWIDGET
								|| itemType == ConstantPasser.Favorites.ITEM_TYPE_APPWIDGET 
								/*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. S***/
								|| itemType == ConstantPasser.Favorites.ITEM_TYPE_LEOSWIDGET_VIEW
								/*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. E***/) {
							bitmap = quickFetch != null ? quickFetch : null;
						}

						if (bitmap == null) {
							bitmap = targetView.getDrawingTarget().getSnapshot(1.0f);
						}
					} catch (Throwable e3) {
						e3.printStackTrace();
						return false;
					}

					// R2 -----------stamp
					// try {
					// R3.echo("ItemType is :  " + itemType);
					// if (itemType ==
					// ConstantPasser.Favorites.ITEM_TYPE_APPWIDGET) {
					// bitmap =
					// Utilities.newInstance().bitmapStampForWidgetWithSnap(
					// bitmap,
					// Utilities.newInstance().drawableToBitmap(
					// mContext.getResources().getDrawable(
					// R.drawable.stamp_widget)));
					// }else if(itemType ==
					// ConstantPasser.Favorites.ITEM_TYPE_COMMEND_APPWIDGET){
					// bitmap = quickFetch
					// }
					// } catch (Throwable e2) {
					// e2.printStackTrace();
					// }
					// 2R

					// now save
					pathToSave = new StringBuilder()
							.append(pathOfParentDir)
							.append(File.separator)
							.append(idInDb)
							.append(ConstantAdapter.SUFFIX_FOR_PREVIEW_SNAPSHOT)
							.toString();
					bitmapToSave.put(pathToSave, bitmap);

					break;

				case ConstantAdapter.ITEM_SNAPSHOT_SAVE_METHOD_SHORTCUT:

					ContentResolver cr1 = mContext.getContentResolver();
					Cursor c2 = cr1
							.query(LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION,
									new String[] {
											LauncherSettings.Favorites.INTENT,
											LauncherSettings.Favorites.ICON,
											LauncherSettings.Favorites.ICON_REPLACE,
											LauncherSettings.Favorites.ICON_PACKAGE,
											LauncherSettings.Favorites.ICON_RESOURCE },
									"_id=?",
									new String[] { String.valueOf(idInDb) },
									null);
					try {
						if (c2.moveToFirst()) {
							Bitmap bmp = null;
							int intentIndex = c2
									.getColumnIndex(LauncherSettings.Favorites.INTENT);
							Intent intent = Intent.parseUri(
									c2.getString(intentIndex), 0);
							PackageManager pm = mContext.getPackageManager();

							try {
								int iconReplacedIndex = c2
										.getColumnIndex(LauncherSettings.Favorites.ICON_REPLACE);
								byte[] replace = c2.getBlob(iconReplacedIndex);
								bmp = BitmapFactory.decodeByteArray(replace, 0,
										replace.length);

							} catch (Throwable e) {
							}

							try {
								if (bmp == null) {
									int iconPackageIndex = c2
											.getColumnIndex(LauncherSettings.Favorites.ICON_PACKAGE);
									int iconResIndex = c2
											.getColumnIndex(LauncherSettings.Favorites.ICON_RESOURCE);
									String iconPackage = c2
											.getString(iconPackageIndex);
									String iconIdName = c2
											.getString(iconResIndex);

									if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
										R2.echo("Touch end friendly 1: "
												+ iconPackage);
									Context fContext = mContext
											.createPackageContext(
													iconPackage,
													Context.CONTEXT_IGNORE_SECURITY
															| Context.CONTEXT_INCLUDE_CODE);
									if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
										R2.echo("Touch end friendly 2: "
												+ iconPackage);
									Resources resources = pm
											.getResourcesForApplication(iconPackage);
									final int resId = resources.getIdentifier(
											iconIdName, null, null);
									if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
										R2.echo("Touch end friendly 4: "
												+ resId);

									try {
										Drawable db = Utilities
												.findDrawableById(resources,
														resId, fContext);
										bmp = Utilities.newInstance()
												.drawableToBitmap(db);
									} catch (Exception e) {
										if (resId != 0) {
											// not an icon in drawable , so we
											// use default application icon
											bmp = Utilities
													.newInstance()
													.drawableToBitmap(
															pm.getActivityIcon(intent));
										}
									}
									if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
										R2.echo("Touch end friendly 5: "
												+ iconPackage);
									fContext = null;
									if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
										R2.echo("Touch end friendly X: "
												+ iconPackage);
								}
							} catch (Exception e1) {
								Debug.wtf("ProfileDebug", e1);
							}

							if (bmp == null) {
								try {
									String action = intent.getAction();
									Set<String> categories = intent
											.getCategories();
									if (Intent.ACTION_MAIN.equals(action)
											&& categories != null
											&& categories
													.contains(Intent.CATEGORY_LAUNCHER)) {
										Drawable dr = pm
												.getActivityIcon(intent);
										bmp = Utilities.newInstance()
												.drawableToBitmap(dr);
									}
								} catch (Exception e1) {
									Debug.wtf("ProfileDebug", e1);
								}
							}

							try {
								if (bmp == null) {
									int iconIndex = c2
											.getColumnIndex(LauncherSettings.Favorites.ICON);
									byte[] origin = c2.getBlob(iconIndex);
									bmp = BitmapFactory.decodeByteArray(origin,
											0, origin.length);
								}
							} catch (Throwable e) {
							}

							if (bmp == null) {
								bmp = ((XLauncher) mContext).getModel()
										.getFallbackIcon();
							}
							pathToSave = pathOfParentDir + File.separator
									+ idInDb + ".png";
							bitmapToSave.put(pathToSave, bmp);
						}
					} catch (Throwable e1) {
						Debug.wtf("ProfileDebug", e1);
					} finally {
						c2.close();
					}

					break;

				case ConstantAdapter.ITEM_SNAPSHOT_SAVE_METHOD_FOLDER:
					if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
						R2.echo("Check folder snapshot saving . ");
					if (quickFetch != null) {
						String path = pathOfParentDir + File.separator + idInDb
								+ ".png";
						if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
							R2.echo("Save folder snapshot to: " + path);
						bitmapToSave.put(path, quickFetch);
					}
					break;

				default:
					return true;

				}

				if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
					R2.echo("Save snapshot : id --> " + idInDb + ", to : "
							+ pathToSave);
				new Thread("SnapSavingThread") {
					public void run() {
						String[] paths = bitmapToSave.keySet().toArray(
								new String[] {});
						for (int k = 0; k < paths.length; k++) {
							// R2
							if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
								R2.echo("Save snap : " + paths[k]);
							try {
								Bitmap bmpTmp;
								try {
									bmpTmp = ConstantAdapter.ITEM_SNAPSHOT_SAVE_METHOD_SHORTCUT == flagOfSaveMethod ? Bitmap
											.createScaledBitmap(
													bitmapToSave.get(paths[k]),
													/*
													 * fix bug 167222
													 * zhanglq@bj.cobellink.com
													 */
													mContext.getResources()
															.getDimensionPixelSize(
																	R.dimen.app_icon_size),
													mContext.getResources()
															.getDimensionPixelSize(
																	R.dimen.app_icon_size),
													/*
													 * fix bug 167222
													 * zhanglq@bj.cobellink.com
													 */
													true)
											: bitmapToSave.get(paths[k]);
									boolean res = Utilities.newInstance()
											.saveBitmapToPng(paths[k], bmpTmp);
									bitmapToSave.get(paths[k]).recycle();
									bmpTmp.recycle();
								} catch (Throwable e) {
									Debug.wtf("ProfileDebug", e);
								}
							} catch (Exception e) {
								Debug.wtf("ProfileDebug", e);
							}
						}
						
						bitmapToSave.clear();
					}
				}.start();
			} catch (Exception e) {
				Debug.wtf("ProfileDebug", e);
				return false;
			}

			return true;
		}
	}

	public static class ReloaderHelper {

		private synchronized boolean filterShortcutInfo(final Context context,
				ShortcutInfo info) {

			// R2
			try {
				PackageManager pm = context.getPackageManager();
				boolean isDisable = pm.getApplicationEnabledSetting(info.intent
						.getComponent().getPackageName()) == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER;
				if (isDisable) {
					return true;
				}
			} catch (Exception e) {
				return false;
			}

			return false;
			// 2R
		}

		public static final ReloaderHelper INSTANCE = new ReloaderHelper();

		// for some unnormal item excluding
		public static List<ComponentName> excludeList = new ArrayList<ComponentName>();
		static {
			// excludeList.add(new ComponentName("com.android.gallery3d",
			// "com.android.gallery3d.gadget.PhotoAppWidgetProvider"));
/*RK_ID:RK_REMOVE_LOTUS AUT:liuyg1@lenovo.com DATE: 2013-3-25 START */			
 excludeList.add(new ComponentName("com.lenovo.launcher",
			 "com.lenovo.launcher2.gadgets.Lotus.LotusProviderHelper"));
/*RK_ID:RK_REMOVE_LOTUS AUT:liuyg1@lenovo.com DATE: 2013-3-25 END */
		}

		public class WidgetReloader {

			private Context mContext = null;

			public WidgetReloader(final Context context) {
				mContext = context;
			}

			private boolean filterNotSupportedWidget(String packageName,
					String className) {
				if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
					R2.echo("Check block , : " + packageName + " , "
							+ className);
				if (packageName == null || className == null)
					return true;
				ComponentName cn = new ComponentName(packageName, className);
				if (excludeList.contains(cn)) {
					return true;
				}

				return false;
			}

			public boolean process(ProfileInfo profile) {
				// we do a clean bind
				AppWidgetHost.deleteAllHosts();
				
				  if (( mContext.getApplicationInfo().flags &  mContext.getApplicationInfo().FLAG_SYSTEM) == 0){
		
					  return true;
				  }
				 
				
				// 2R
				List<WidgetInfo> widgets = profile.widgets;
				AppWidgetManager widgetManager = AppWidgetManager
						.getInstance(mContext);
				AttributesManager attrManager = new AttributesManager(profile);

				// the context must be an Launcher context
				AppWidgetHost widgetHost = null;
				try {
					widgetHost = ((XLauncher) mContext).getAppWidgetHost();
				} catch (Exception e3) {
					widgetHost = new AppWidgetHost(mContext,
							XLauncher.APPWIDGET_HOST_ID);
				}

				ContentValues[] extraValues = new ContentValues[widgets.size()];
				LauncherAppWidgetInfo[] infoS = new LauncherAppWidgetInfo[widgets
						.size()];
				// WidgetInfo[]

				try {
					if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
						R2.echo("Widgets size is : " + widgets.size());

					// R2

					for (int i = 0; i < widgets.size(); i++) {

						WidgetInfo widgetInfo = widgets.get(i);
						int widgetId = widgetHost.allocateAppWidgetId();

						extraValues[i] = new ContentValues();

						infoS[i] = new LauncherAppWidgetInfo(widgetId);
						String packageName, className;
						ComponentName cname;
						Intent intent;
						try {// may failed when the widget provider has not been
								// installed

							packageName = attrManager.getAttributeByName(
									widgetInfo, WidgetAttributes.PACKAGENAME)
									.getAttrValue();
							className = attrManager.getAttributeByName(
									widgetInfo, WidgetAttributes.CLASSNAME)
									.getAttrValue();
							if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
								R2.echo("packageName is : " + packageName
										+ "  , className is : " + className);

							// get component
							cname = new ComponentName(packageName, className);

							// intent
							intent = new Intent();
							intent.setComponent(cname);
							extraValues[i].put(Favorites.INTENT,
									intent.toUri(0));

							// filter some kind widget , because they were not
							// well
							// handle currently.
							if (filterNotSupportedWidget(packageName, className)) {
								if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
									R2.echo("Block widget : " + packageName
											+ className);
								continue;
							}

							// R2
							R3.echo("cname is : " + cname);

							widgetManager.bindAppWidgetId(widgetId, cname);

							// R2
							AppWidgetProviderInfo appWidgetInfo = widgetManager
									.getAppWidgetInfo(widgetId);
							if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
								R2.echo("widgetXXX widgetId is : " + widgetId
										+ "  appWidgetInfo : " + appWidgetInfo);
							if (appWidgetInfo.configure != null) {
								infoS[i].needConfig = 1;
								if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
									R2.echo("SetConfig = 1 , " + appWidgetInfo);

								try {
									String snapfile = attrManager
											.getAttributeByName(widgetInfo,
													WidgetAttributes.SNAPTHOT)
											.getAttrValue();
									String commendFace = profile.getKey()
											+ File.separator
											+ ConstantAdapter.DIR_SNAPSHOT
											+ File.separator
											+ ConstantAdapter.DIR_SNAPSHOT_WIDGET
											+ File.separator + snapfile;
									if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
										R2.echo("Commend face : " + commendFace);
									if (new File(commendFace).exists())
										R2.echo("FOUND______________________________________________");
									try {
										infoS[i].iconBitmap = BitmapFactory
												.decodeFile(commendFace);
									} catch (Throwable ex) {
										Debug.wtf("ProfileDebug", ex);
									}

								} catch (Exception e2) {
									Debug.wtf("ProfileDebug", e2);
								}

								extraValues[i].put(Favorites.CONFIGABLE_WIDGET,
										1);
							}
							// 2R

							infoS[i].itemType = Favorites.ITEM_TYPE_APPWIDGET;
						} catch (Exception e) {
							// we think it as an uninstalled one
							try {

								// fake widget
								infoS[i].itemType = Favorites.ITEM_TYPE_COMMEND_APPWIDGET;
								if(GlobalDefine.getVerisonCMCCTDConfiguration(mContext)){
									continue;
								}
								if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
									R2.echo(" --------------->   Widget!");
								
								/*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. S***/
								/*String urlDesc = attrManager
										.getAttributeByName(widgetInfo,
												WidgetAttributes.URI)
										.getAttrValue();*/
								packageName = attrManager.getAttributeByName(
										widgetInfo, WidgetAttributes.PACKAGENAME)
										.getAttrValue();
								String urlDesc = XLauncherModel.getCommandAppDownloadUri(packageName, infoS[i].label);
								if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
									R2.echo("URL is : " + urlDesc);

								extraValues[i].put(Favorites.URI, urlDesc);
								/*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. E***/

								try {
									String snapfile = attrManager
											.getAttributeByName(widgetInfo,
													WidgetAttributes.SNAPTHOT)
											.getAttrValue();
									String commendFace = profile.getKey()
											+ File.separator
											+ ConstantAdapter.DIR_SNAPSHOT
											+ File.separator
											+ ConstantAdapter.DIR_SNAPSHOT_WIDGET
											+ File.separator + snapfile;
									if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
										R2.echo("Commend face : " + commendFace);
									if (new File(commendFace).exists())
										R2.echo("FOUND______________________________________________");
									try {
										infoS[i].iconBitmap = BitmapFactory
												.decodeFile(commendFace);
									} catch (Throwable ex) {
										Debug.wtf("ProfileDebug", ex);
									}

								} catch (Exception e2) {
									Debug.wtf("ProfileDebug", e2);
								}
							} catch (Exception e1) {
								Debug.wtf("ProfileDebug", e1);
								if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
									R2.echo("Widget also not info OK----------------*");
								continue;
							}

						}

						infoS[i].appWidgetId = widgetId;

						infoS[i].cellX = Integer.parseInt(attrManager
								.getAttributeByName(widgetInfo,
										WidgetAttributes.CELLX).getAttrValue());
						infoS[i].cellY = Integer.parseInt(attrManager
								.getAttributeByName(widgetInfo,
										WidgetAttributes.CELLY).getAttrValue());
						infoS[i].spanX = Integer.parseInt(attrManager
								.getAttributeByName(widgetInfo,
										WidgetAttributes.SPANX).getAttrValue());
						infoS[i].spanY = Integer.parseInt(attrManager
								.getAttributeByName(widgetInfo,
										WidgetAttributes.SAPNY).getAttrValue());
						infoS[i].screen = Integer
								.parseInt(attrManager.getAttributeByName(
										widgetInfo, WidgetAttributes.SCREEN)
										.getAttrValue());
						infoS[i].container = LauncherSettings.Favorites.CONTAINER_DESKTOP;

						// handle label
						String label = attrManager.getAttributeByName(
								widgetInfo, WidgetAttributes.LABEL)
								.getAttrValue();
						infoS[i].label = label == null ? "" : label;
						extraValues[i].put(Favorites.TITLE, infoS[i].label);

					}

					surelyAddItemInDatabase(mContext, infoS, extraValues);
				} catch (Exception e) {
					Debug.wtf("ProfileDebug", e);
					return false;
				}

				return true;
			}
		}// end widget reload
		
		
		/*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-10 START */
		/**
		 * for LeosE2Ewidgets
		 * **/
		public class LeosE2EWidgetLoader {
			private Context mContext = null;

			public LeosE2EWidgetLoader(final Context context) {
				if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
					R2.echo("HERE -------______! in LeosWidgetLoader 1");
				mContext = context;
			}

			public boolean process(final ProfileInfo profile) {

				if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
					R2.echo("HERE -------______! in LeosWidgetLoader ");

				try {
					if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
						R2.echo("Touch slot xm3dwidgets. "
								+ profile.leosE2EWidgets);
					List<LeosE2EWidgetInfo> leosWidgets = profile.leosE2EWidgets;

					AttributesManager attrManager = new AttributesManager(
							profile);

					int count = leosWidgets.size();

					LenovoWidgetViewInfo[] infoS = new LenovoWidgetViewInfo[count];
					ContentValues extraValues[] = new ContentValues[count];

					for (int i = 0; i < count; i++) {

						LeosE2EWidgetInfo xmInfo = leosWidgets.get(i);

						String className = attrManager.getAttributeByName(
								xmInfo, LeosWidgetAttributes.CLASS_NAME)
								.getAttrValue();
						String packageName = attrManager.getAttributeByName(
								xmInfo, LeosWidgetAttributes.PACKAGE_NAME)
								.getAttrValue();

						infoS[i] = new LenovoWidgetViewInfo();
						extraValues[i] = new ContentValues();

						int cellX = Integer.valueOf(attrManager
								.getAttributeByName(xmInfo,
										LeosWidgetAttributes.CELLX)
										.getAttrValue());
						int cellY = Integer.valueOf(attrManager
								.getAttributeByName(xmInfo,
										LeosWidgetAttributes.CELLY)
										.getAttrValue());
						int container = ConstantPasser.Favorites.CONTAINER_DESKTOP;
						int screen = Integer.valueOf(attrManager
								.getAttributeByName(xmInfo,
										LeosWidgetAttributes.SCREEN)
										.getAttrValue());

						int spanX = Integer.valueOf(attrManager
								.getAttributeByName(xmInfo,
										LeosWidgetAttributes.SPANX)
										.getAttrValue());
						int spanY = Integer.valueOf(attrManager
								.getAttributeByName(xmInfo,
										LeosWidgetAttributes.SPANY)
										.getAttrValue());
						//cancel by xingqx for sonar
						//LenovoWidgetViewInfo xmLauncherInfo = new LenovoWidgetViewInfo();
						infoS[i].cellX = cellX;
						infoS[i].cellY = cellY;
						infoS[i].container = container;
						infoS[i].screen = screen;
						infoS[i].spanX = spanX;
						infoS[i].spanY = spanY;
						infoS[i].className = className;
						infoS[i].packageName = packageName;

						extraValues[i].put(LauncherSettings.Favorites.TITLE,
								className);
						extraValues[i].put(LauncherSettings.Favorites.URI,
								packageName);
						//cancel by xingqx for sonar
						//if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
						//	R2.echo("info need be :  "
						//			+ xmLauncherInfo.getClass().getSimpleName());
						if (filterNotSupportedWidget(packageName, className)) {
							if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
								R2.echo("Block widget : " + packageName
										+ className);
							continue;
						}
						
						/*RK_ID:RK_SD_WIDGETS liuyg1 2013-5-14. START***/
						PackageManager pm = mContext.getPackageManager();    
						try{ 
							if (infoS[i].packageName != null){
								pm.getApplicationInfo(infoS[i].packageName, 0);
							}
						}catch (NameNotFoundException e) { 
                            try {
                            	// fake widget
								infoS[i].itemType = Favorites.ITEM_TYPE_COMMEND_APPWIDGET;
								if(GlobalDefine.getVerisonCMCCTDConfiguration(mContext)){
									continue;
								}
								/*String urlDesc = attrManager
									.getAttributeByName(xmInfo,
										WidgetAttributes.URI)
									.getAttrValue();*/
								String urlDesc = XLauncherModel.getCommandAppDownloadUri(packageName, infoS[i].className);
								if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
										R2.echo("URL is : " + urlDesc);
								extraValues[i].put(Favorites.URI, urlDesc);
								
								ComponentName cname = new ComponentName(packageName, className);
								Intent intent = new Intent();
								intent.setComponent(cname);
								extraValues[i].put(Favorites.INTENT,
										intent.toUri(0));
								
								try {
									String snapfile = attrManager
										.getAttributeByName(xmInfo,
												LeosWidgetAttributes.SNAPTHOT)
										.getAttrValue();
									String commendFace = profile.getKey()
												+ File.separator
												+ ConstantAdapter.DIR_SNAPSHOT
												+ File.separator
												+ ConstantAdapter.DIR_SNAPSHOT_WIDGET
												+ File.separator + snapfile;
									if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
										R2.echo("Commend face : " + commendFace);
									if (new File(commendFace).exists())
										R2.echo("FOUND LeosWidget________________________________");
									try {
										infoS[i].iconBitmap = BitmapFactory.decodeFile(commendFace);
									} catch (Exception ex) {
										Debug.wtf("ProfileDebug", ex);
									}

								} catch (Exception e2) {
									Debug.wtf("ProfileDebug", e2);
								}
							} catch (Exception e1) {
								Debug.wtf("ProfileDebug", e1);
								if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
									R2.echo("Widget also not info OK----------------*");
								continue;
							}		
						}
                		 /*RK_ID:RK_SD_WIDGETS liuyg1 2013-5-14. END***/
					}

					surelyAddItemInDatabase(mContext, infoS,
							extraValues);

					if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
						R2.echo("Touch end slot xm3dwidgets. ");
					return true;

				} catch (Exception e) {
					e.printStackTrace();
					if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
						R2.echo("Exception ;;; Touch end slot xm3dwidgets. "
								+ e.toString());
					return false;
				}

			}
			private boolean filterNotSupportedWidget(String packageName,
					String className) {
				if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
					R2.echo("Check block , : " + packageName + " , "
							+ className);
				if (packageName == null || className == null)
					return true;
				ComponentName cn = new ComponentName(packageName, className);
				if (excludeList.contains(cn)) {
					return true;
				}

				return false;
			}
		}
		/*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-10 END */
		/**
		 * an app loader, this will be used not only by desktop but also by
		 * folder etc.
		 * */
		class ApplicationLoader {
			private Context mContext = null;

			private ProfileInfo currProfile = null;

			public ApplicationLoader(final Context context,
					ProfileInfo profileAttached) {
				mContext = context;
				currProfile = profileAttached;
			}

			private ContentValues values = new ContentValues();
			private ShortcutInfo shortcutInfo = new ShortcutInfo();

			public ContentValues getConvertedBaseInfoContentValues() {
				return values;
			}

			public ShortcutInfo getConvertedShortcutInfo() {
				return shortcutInfo;
			}

			// the BaseInfo will be convert to an LauncherShortcutInfo
			public boolean convertBaseInfo(AttributesManager attrManager,
					QuickEntryInfo entryInfo, final Long entryContainer) {

				// reconstruct
				ShortcutInfo launcherShortcutInfo = new ShortcutInfo();
				ContentValues extraValues = new ContentValues();

				int cellX = 0, cellY = 0, screen = 0, spanX = 1, spanY = 1;
				long container = entryContainer;
				String title = "";
				String titleReplaced = "";
				String packageName = "";
				String className = "";
				String actionOfUirShortcut = "";
				String intentOfShortcut = "";
				int iconType = 0;
				String iconResource = "";
				boolean isIconReplaced = false;
				String iconSnapPath = "";
				int itemType = 0;
				String iconPackage = "";

				try {
					cellX = Integer.parseInt(attrManager.getAttributeByName(
							entryInfo, QuickEntryAppAttributes.CELLX)
							.getAttrValue());
//					R2.echo("cellX = "+cellX);
				} catch (Exception e1) {
					Debug.wtf("ProfileDebug", e1);
				}

				try {
					cellY = Integer.parseInt(attrManager.getAttributeByName(
							entryInfo, QuickEntryAppAttributes.CELLY)
							.getAttrValue());
//					R2.echo("cellX = "+cellY);
				} catch (Exception e1) {
					Debug.wtf("ProfileDebug", e1);
				}

				try {
					container = container == ItemInfo.NO_ID ? container = attrManager
							.getAttributeByName(entryInfo,
									QuickEntryAppAttributes.CONTAINER)
							.getAttrValue()
							.equalsIgnoreCase(ContainerType.CONTAINER_HOTSEAT) ? LauncherSettings.Favorites.CONTAINER_HOTSEAT
							: LauncherSettings.Favorites.CONTAINER_DESKTOP
							: container;
				} catch (Exception e1) {
					Debug.wtf("ProfileDebug", e1);
				}
				try {
					screen = Integer.parseInt(attrManager.getAttributeByName(
							entryInfo, QuickEntryAppAttributes.SCREEN)
							.getAttrValue());
//					R2.echo("screen = "+screen);
				} catch (Exception e1) {
					Debug.wtf("ProfileDebug", e1);
				}
				try {
					title = String.valueOf(attrManager.getAttributeByName(
							entryInfo, QuickEntryAppAttributes.TITLE)
							.getAttrValue());
//					R2.echo("title = "+title);
				} catch (Exception e1) {
					Debug.wtf("ProfileDebug", e1);
				}

				try {
					titleReplaced = String.valueOf(attrManager
							.getAttributeByName(entryInfo,
									QuickEntryAppAttributes.REPLACE_TITLE)
							.getAttrValue());
				} catch (Exception e1) {
					Debug.wtf("ProfileDebug", e1);
				}

				try {
					packageName = attrManager.getAttributeByName(entryInfo,
							QuickEntryAppAttributes.PACKAGENAME).getAttrValue();
//					R2.echo("packageName = "+packageName);
				} catch (Exception e1) {
					Debug.wtf("ProfileDebug", e1);
				}
				try {
					className = attrManager.getAttributeByName(entryInfo,
							QuickEntryAppAttributes.CLASSNAME).getAttrValue();
//					R2.echo("className = "+className);
				} catch (Exception e1) {
					Debug.wtf("ProfileDebug", e1);
				}
				try {
					actionOfUirShortcut = attrManager.getAttributeByName(
							entryInfo, QuickEntryAppAttributes.ACTION)
							.getAttrValue();
//					R2.echo("actionOfUirShortcut = "+actionOfUirShortcut);
				} catch (Exception e1) {
					Debug.wtf("ProfileDebug", e1);
				}

				try {
					itemType = Integer.valueOf(attrManager.getAttributeByName(
							entryInfo, QuickEntryAppAttributes.ITEM_TYPE)
							.getAttrValue());
//					R2.echo("itemType = "+itemType);
				} catch (Exception e1) {
					Debug.wtf("ProfileDebug", e1);
				}

				try {
					iconResource = attrManager.getAttributeByName(entryInfo,
							QuickEntryAppAttributes.ICONRESOURCE)
							.getAttrValue();
//					R2.echo("iconResource = ");
				} catch (Exception e1) {
					Debug.wtf("ProfileDebug", e1);
				}

				try {
					iconSnapPath = new StringBuffer()
							.append(currProfile.getKey())
							.append(File.separator)
							.append(File.separator)
							.append(ConstantAdapter.DIR_SNAPSHOT)
							.append(File.separator)
							.append(ConstantAdapter.DIR_SNAPSHOT_SHORTCUT)
							.append(File.separator)
							.append(attrManager.getAttributeByName(entryInfo,
									QuickEntryAppAttributes.ICON)
									.getAttrValue()).toString();
//					R2.echo("iconSnapPath = ");
				} catch (Exception e1) {
					Debug.wtf("ProfileDebug", e1);
				}

				try {
					iconType = Integer.valueOf(attrManager.getAttributeByName(
							entryInfo, QuickEntryAppAttributes.ICON_TYPE)
							.getAttrValue());
//					R2.echo("iconType = ");
				} catch (Exception e1) {
					Debug.wtf("ProfileDebug", e1);
				}

				try {
					iconPackage = attrManager.getAttributeByName(entryInfo,
							QuickEntryAppAttributes.ICONPACKAGE).getAttrValue();
//					R2.echo("iconPackage = ");
				} catch (Exception e1) {
					Debug.wtf("ProfileDebug", e1);
				}

				try {
					isIconReplaced = Boolean.valueOf(attrManager
							.getAttributeByName(entryInfo,
									QuickEntryAppAttributes.ICONREPLACED_MARK)
							.getAttrValue());
//					R2.echo("isIconReplaced = "+isIconReplaced);
				} catch (Exception e1) {
					Debug.wtf("ProfileDebug", e1);
				}

				// now check an uri shortcut or application or application like
				// uri shortcut like , then fill in an intent
				Intent intent = null;
				if (actionOfUirShortcut != null
						&& !"".equals(actionOfUirShortcut)) {
					// deal as an uri shortcut
					try {
						intent = Intent.parseUri(actionOfUirShortcut, 0);
//bugfix 17646
//						intent = processIntent(intent,mContext);
						R2.echo("intent = "+intent);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Debug.wtf("ProfileDebug", e);
					}
				}
				else if (packageName != null && !"".equals(packageName)
						&& className != null && !"".equals(className)) {
					intent = new Intent(Intent.ACTION_MAIN);
					intent.addCategory(Intent.CATEGORY_LAUNCHER);
					ComponentName component = new ComponentName(packageName, className);
					if(container ==LauncherSettings.Favorites.CONTAINER_HOTSEAT ){
//						component = processComponentName(packageName,className,mContext);
					}
					intent.setComponent(component);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

				}

				//RK_REMOVE_CODE_BU  dining 2013-06-24 start
				//remove the code in BU version
//				DefaultApp ret = isDefaultApp(packageName, className, actionOfUirShortcut);
//				
//                if (ret.mDefault)
//                {
//                    R5.echo("isDefaultApp");
//                    try {
//                    intent = Intent.parseUri(ret.mIntent, 0);
//                    } catch (Exception e) {
//                        // TODO Auto-generated catch block
//                        Debug.wtf("ProfileDebug", e);
//                    }
//                    packageName = "";
//                    className = "";
//                    itemType = 1;
//                    iconSnapPath = "";
//                    iconType = Favorites.ICON_TYPE_RESOURCE;
//                    iconPackage = mContext.getPackageName();
//                    iconResource = mContext.getResources().getResourceName(ret.mResId);
//                 
//                }
              //RK_REMOVE_CODE_BU  dining 2013-06-24 end
				// now check if the target application was installed
				try {
					R2.echo("try = ");
					if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
						R2.echo("find icon from : " + iconSnapPath);
					byte[] bmpToByteArray = null;
					if(itemType==Favorites.ITEM_TYPE_SHORTCUT){
						Bitmap bmp = BitmapFactory.decodeFile(iconSnapPath);
						if (bmp == null) {
							//bugfix nullpoint by zhanglz1 20121205
							if (((LauncherApplication) mContext).getModel() != null) {
								bmp = ((LauncherApplication) mContext).getModel()
										.getFallbackIcon();
							}
						}
						bmpToByteArray = Utilities.newInstance()
								.bitmap2ByteArray(bmp);
					}
					if (intent != null) {
						boolean isInstalled = Utilities.newInstance()
								.checkAppInstallState(
										mContext.getPackageManager(), intent);
						R2.echo("isInstalled = "+isInstalled);
						if (!isInstalled) {
							// handle commend icon
							// for all
							if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
								R2.echo("Fake commend icon is : " + title);
							if(itemType==Favorites.ITEM_TYPE_SHORTCUT){
								extraValues.put(Favorites.ICON, bmpToByteArray);
							}
							/*
							 * RK_ID: RK_SHORTCUT . AUT: zhanggx1 . DATE:
							 * 2012-07-10 . PUR: fix bug for effect shortcut . S
							 */
							if (iconPackage == null&&itemType==Favorites.ITEM_TYPE_SHORTCUT) {
								extraValues.put(Favorites.ICON_REPLACE,
										bmpToByteArray);
							} else {
								android.content.pm.ApplicationInfo applicationInfo = null;
								try {
									applicationInfo = mContext
											.getPackageManager()
											.getApplicationInfo(iconPackage, 0);
								} catch (NameNotFoundException e) {
									Debug.e("Cannot found the package", e);
								}
								if (applicationInfo == null) {
								if(itemType==Favorites.ITEM_TYPE_SHORTCUT)extraValues.put(Favorites.ICON_REPLACE,
											bmpToByteArray);
								}
							}
							/*
							 * RK_ID: RK_SHORTCUT . AUT: zhanggx1 . DATE:
							 * 2012-07-10 . PUR: fix bug for effect shortcut . S
							 */

//							launcherShortcutInfo.iconBitmap = bmp;
						} else {
							if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
								R2.echo("Fake commend icon is 1 : " + title);
							if(itemType==Favorites.ITEM_TYPE_SHORTCUT)extraValues.put(Favorites.ICON, bmpToByteArray);
							if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
								R2.echo("Fake commend icon is3 : " + title);
						}
					}

					// check if icon was replaced
					if (isIconReplaced) {
						if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
							R2.echo("Replaced icon for : " + title);
						if(itemType==Favorites.ITEM_TYPE_SHORTCUT)extraValues.put(
								LauncherSettings.Favorites.ICON_REPLACE,
								bmpToByteArray);
					}
				} catch (Exception t1) {
					Debug.wtf("ProfileDebug", t1);
				}

				try {

					launcherShortcutInfo.cellX = cellX;
					launcherShortcutInfo.cellY = cellY;
					launcherShortcutInfo.screen = screen;
					launcherShortcutInfo.container = container;
					launcherShortcutInfo.spanX = spanX;
					launcherShortcutInfo.spanY = spanY;
					launcherShortcutInfo.uri = intent == null ? null : intent
							.toUri(0);
					launcherShortcutInfo.intent = intent;
					launcherShortcutInfo.title = title;
					launcherShortcutInfo.replaceTitle = titleReplaced;
					launcherShortcutInfo.itemType = itemType;
					R2.echo("launcherShortcutInfo = ");
					// extra values
					extraValues.put(LauncherSettings.Favorites.ICON_TYPE,
							iconType);
					extraValues.put(LauncherSettings.Favorites.ICON_RESOURCE,
							iconResource);
					extraValues.put(LauncherSettings.Favorites.ICON_PACKAGE,
							iconPackage);

					shortcutInfo = launcherShortcutInfo;
					R2.echo("shortcutInfo = launcherShortcutInfo ");
					values = extraValues;

				} catch (Exception e) {
					R2.echo("convertBaseInfo return false ProfileDebug"+ e);
					return false;
				}
				return true;
			}

//bugfix 17646

			private ComponentName processComponentName(String pkgName,
					String clsName, Context context) {
				PackageManager pm = mContext.getPackageManager(); 
				Intent intent = null;
		        //拨号，
		        if ((pkgName.equals("com.android.contacts") && clsName.equals("com.android.contacts.activities.DialtactsActivity"))
		              ||(pkgName.equals("com.lenovo.ideafriend") && clsName.equals("com.lenovo.ideafriend.alias.DialtactsActivity")))
		        {
		        	
		        	try {
		        		intent = Intent.parseUri("#Intent;action=android.intent.action.DIAL;launchFlags=0x10000000;end", 0);
		        	} catch (Exception e) {
		        		// TODO Auto-generated catch block
		        		Debug.wtf("ProfileDebug", e);
		        	};
		        	
		        }
		        //联系人，
		        else if (pkgName.equals("com.android.contacts") && clsName.equals("com.android.contacts.activities.PeopleActivity")
		                ||(pkgName.equals("com.lenovo.ideafriend") && clsName.equals("com.lenovo.ideafriend.alias.PeopleActivity")))
		        {
		        	try {
		        		intent = Intent.parseUri("content://com.android.contacts/contacts#Intent;action=android.intent.action.VIEW;launchFlags=0x10000000;end", 0);
		        	} catch (Exception e) {
		        		// TODO Auto-generated catch block
		        		Debug.wtf("ProfileDebug", e);
		        	};
		        }
		        //短信
		        else if ((pkgName.equals("com.android.mms") && clsName.equals("com.android.mms.ui.ConversationList"))
		                || pkgName.equals("com.lenovo.mms")
		                ||  (pkgName.equals("com.lenovo.ideafriend") && clsName.equals("com.lenovo.ideafriend.alias.MmsActivity")))
		        {
		        	try {
		        		intent = Intent.parseUri("#Intent;action=android.intent.action.MAIN;type=vnd.android-dir/mms-sms;launchFlags=0x10000000;end", 0);
		        	} catch (Exception e) {
		        		// TODO Auto-generated catch block
		        		Debug.wtf("ProfileDebug", e);
		        	};
		        }
		        //浏览器
		        else if (pkgName.equals("com.android.browser"))
		        {
		        	try {
		        		intent = Intent.parseUri("http://m.idea123.cn/?c=02#Intent;action=android.intent.action.VIEW;S.com.android.browser.application_id=-2036735900831708960;end", 0);
		        	} catch (Exception e) {
		        		// TODO Auto-generated catch block
		        		Debug.wtf("ProfileDebug", e);
		        	};
		        }
		        //相机
		        else if (pkgName.equals("com.android.camera")
//		                || pkgName.equals("com.android.gallery3d")
//		                || pkgName.equals("com.lenovo.scg")
		                )
		        {
		        	try {
		        		intent = Intent.parseUri("#Intent;action=android.media.action.STILL_IMAGE_CAMERA;launchFlags=0x10000000;end", 0);
		        	} catch (Exception e) {
		        		// TODO Auto-generated catch block
		        		Debug.wtf("ProfileDebug", e);
		        	};
		        }
//		        //图库
//		        else if (pkgName.equals("com.android.gallery3d"))
//		        {
//		            ret.mDefault = true;
//		            ret.mIntent = "#Intent;action=android.intent.action.VIEW;type=vnd.android.cursor.dir/image;launchFlags=0x10000000;end";
//		            ret.mResId = R.drawable.com_android_camera__ic_launcher_camera;  
//		        }
//		        //音乐
		        else if (pkgName.equals("com.android.music"))
		        {
		        	try {
		        		intent = Intent.parseUri("#Intent;action=android.intent.action.MUSIC_PLAYER;launchFlags=0x10000000;end", 0);
		        	} catch (Exception e) {
		        		// TODO Auto-generated catch block
		        		Debug.wtf("ProfileDebug", e);
		        	};
		        }
		        if(intent!=null){
		        	List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
		        	int size = infos.size();
		        	int index = 0;
		        	for (int i = 0; i < size; i++) {
		        		ResolveInfo info = infos.get(i);
		        		if(info!=null&&info.system){
		        			index = i;
		        			break;
		        		}
		        	}
		        	ResolveInfo info = infos.get(index);
		        	if (info != null) {
		        		ComponentName comp = Utilities.getComponentNameFromResolveInfo(info);
		        		intent.setComponent(comp);
		        		return comp;
		        	}
		        }
				return new ComponentName(pkgName, clsName);
			}

			private Intent processIntent(Intent intent,Context context) {
//				R2.echo("	processIntent  begin intent.toString();"+intent.toString());
				 PackageManager pm = mContext.getPackageManager(); 
				 
					if (intent != null && intent.getComponent() == null){
						if(intent.getAction()!=null&&(intent.getAction().equals("android.intent.action.DIAL")
								||intent.getAction().equals("action=android.media.action.STILL_IMAGE_CAMERA")||
								intent.getAction().equals("android.intent.action.MUSIC_PLAYER"))){
							 List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
					         int size = infos.size();
					         if(size>0){
					        	 int index = 0;
					        	 for (int i = 0; i < size; i++) {
					        		 ResolveInfo info = infos.get(i);
					        		 if(info!=null&&info.system){
					        			 index = i;
					        			 break;
					        		 }
					        	 }
					        	 ResolveInfo info = infos.get(index);
					        	 if (info != null) {
					        		 ComponentName comp = Utilities.getComponentNameFromResolveInfo(info);
					        		 intent.setComponent(comp);
					        		 intent.setAction("android.intent.action.MAIN");
					        	
					        		 R2.echo("processIntent	intent.toString();"+intent.toString());
					        	 }	
					         }
						}else if(intent.getType()!=null&&intent.getType().equals("vnd.android-dir/mms-sms")){
							 List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
					         int size = infos.size();
					         if(size>0){
					        	 int index = 0;
					        	 for (int i = 0; i < size; i++) {
					        		 ResolveInfo info = infos.get(i);
					        		 if(info!=null&&info.system){
					        			 index = i;
					        			 break;
					        		 }
					        	 }
					        	 ResolveInfo info = infos.get(index);
					        	 if (info != null) {
					        		 ComponentName comp = Utilities.getComponentNameFromResolveInfo(info);
					        		 intent.setComponent(comp);
					        		 R2.echo("processIntent	intent.toString();"+intent.toString());
					        	 }
					         }
						}else if(intent.getDataString()!=null&&intent.getDataString().equals("content://com.android.contacts/contacts")){
							 List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
					         int size = infos.size();
					         if(size>0){
					        	 int index = 0;
					        	 for (int i = 0; i < size; i++) {
					        		 ResolveInfo info = infos.get(i);
					        		 if(info!=null&&info.system){
					        			 index = i;
					        			 break;
					        		 }
					        	 }
					        	 ResolveInfo info = infos.get(index);
					        	 if (info != null) {
					        		 ComponentName comp = Utilities.getComponentNameFromResolveInfo(info);
					        		 intent.setComponent(comp);
					        		 intent.setData(null);
					        		 intent.setAction("android.intent.action.MAIN");
					        		 R2.echo("processIntent	intent.toString();"+intent.toString());
					        	 }	
					         }
						}
						//test by dining change the intent shortcut to application shortcut for Browser
						//2013-07-25
						else if(intent.getDataString()!=null && intent.getDataString().equals("http://m.idea123.cn/?c=02")){
							 Intent tmp = new Intent("android.intent.action.MAIN");
							 tmp.addCategory("android.intent.category.BROWSABLE");
							 tmp.addCategory("android.intent.category.LAUNCHER");
							 tmp.addCategory("android.intent.category.APP_BROWSER");
							 List<ResolveInfo> infos = pm.queryIntentActivities(tmp, 0);
							 
							         int size = infos.size();
							          int index = 0;
							          for (int i = 0; i < size; i++) {
							        	  ResolveInfo info = infos.get(i);
							        	  
							        	  if(info!=null&&info.system){
							        		  R2.echo("=== infos="+info.toString() +" info.system="+info.system);
							        		  index = i;
							        		  break;
							        	  }
							          }
							          ResolveInfo info = infos.get(index);
										if (info != null) {
											ComponentName comp = Utilities.getComponentNameFromResolveInfo(info);
											intent.setComponent(comp);
										}
						}
						
					}
					

				return intent;
			}
		}

		/**
		 * quick entry reloader
		 * */
		public class QuickEntryReloader {
			private Context mContext = null;

			public QuickEntryReloader(final Context context) {
				mContext = context;
			}

			public boolean process(ProfileInfo profile) {
				List<QuickEntryInfo> entries = profile.quickentries;

				AttributesManager attrmanager = new AttributesManager(profile);

				boolean res = true;
				ShortcutInfo[] infoS = new ShortcutInfo[entries.size()];
				ContentValues[] valueS = new ContentValues[entries.size()];
				for (int i = 0; i < entries.size(); i++) {
					try {
						ApplicationLoader appLoader = new ApplicationLoader(
								mContext, profile);
						res = appLoader.convertBaseInfo(attrmanager,
								entries.get(i), Long.valueOf(ItemInfo.NO_ID));
						R2.echo("res =" +res);
						infoS[i] = appLoader.getConvertedShortcutInfo();

						valueS[i] = appLoader
								.getConvertedBaseInfoContentValues();

					} catch (Exception e) {
						R2.echo("QuickEntryReloader return false1 "+ e);
						e.printStackTrace();
						res = false;
					}
				}

				ArrayList<ShortcutInfo> infoList = new ArrayList<ShortcutInfo>();
				ArrayList<ContentValues> valueList = new ArrayList<ContentValues>();
				try {

					for (int nx = 0; nx < infoS.length; nx++) {
  /*RK_ID:RK_SD_APPS liuyg1 2013-5-8. START***/
						ShortcutInfo sinfo = infoS[nx];
						if(sinfo == null){
							R2.echo("sinfo == null");
							continue;
						}
//						if(sinfo!=null&&sinfo.intent!=null&&sinfo.intent.getComponent()!=null){
//							sinfo.intent.getComponent().getPackageName();
//							Log.d("liuyg1","sinfo.intent.getComponent().getPackageName()="+sinfo.intent.getComponent().getPackageName());
//						}
						 int itemType = infoS[nx].itemType;
						 Intent intent = sinfo.intent;
						 PackageManager pm = mContext.getPackageManager();    
//						 if(itemType == LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT&&GlobalDefine.getVerisonCMCCTDConfiguration(mContext)){
						 if(itemType == LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT){
							 continue;
						 }
						if (itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION ||
								itemType == LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT) {
							try{ 
								if (intent != null && intent.getComponent() != null){
									pm.getApplicationInfo(intent.getComponent().getPackageName(), 0);
									
					                List<ResolveInfo> apps = pm.queryIntentActivities(intent, 0);
					                if (apps == null || apps.size() <=0) {
					                	continue;
					                }
					                
									if(intent.getComponent().getPackageName()!=null&&intent.getComponent().getPackageName().equals("com.lenovo.app.Calendar")){
										sinfo.intent = pm.getLaunchIntentForPackage(intent.getComponent().getPackageName());
									}
								}
								infoS[nx].itemType = LauncherSettings.Favorites.ITEM_TYPE_APPLICATION;
							}catch (NameNotFoundException e) {
								continue;
//								infoS[nx].itemType = LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT;   
//								if(GlobalDefine.getVerisonCMCCTDConfiguration(mContext)){
//									continue;
//								}
//                                String packageName = null;
//                            	if (intent != null && intent.getComponent() != null){
//                            		packageName = intent.getComponent().getPackageName();
//                            	}
//								infoS[nx].uri = XLauncherModel.getCommandAppDownloadUri(packageName, infoS[nx].title.toString());
								
//								if(sinfo!=null&&sinfo.intent!=null&&sinfo.intent.getComponent()!=null){
//									if(sinfo.intent.getComponent().getPackageName().equals("com.android.gallery3d")){
//										Intent intent1 = getInstalledGalleyIntent(mContext);
//
//										if(intent1!=null){
//											infoS[nx].intent = intent1;
//											infoS[nx].itemType = LauncherSettings.Favorites.ITEM_TYPE_APPLICATION;
//											infoS[nx].uri = null;
//										}else{
//											continue;
//										}
//									}else{
//										continue;
//									}
//								}else{
//									continue;
//								}
							}

						} 
					 /*RK_ID:RK_SD_APPS liuyg1 2013-5-8. End***/
						if(!profile.singleLayer&&infoS[nx].container==LauncherSettings.Favorites.CONTAINER_HOTSEAT){
							R2.echo("CONTAINER_HOTSEAT "+infoS[nx].toString());
							if(infoS[nx].cellX>2){
								infoS[nx].cellX = infoS[nx].cellX-1;
								infoS[nx].screen = infoS[nx].cellX;
								R2.echo("CONTAINER_HOTSEAT "+infoS[nx].toString());
							}
						}
//bugfix 17646
						if(infoS[nx].container==LauncherSettings.Favorites.CONTAINER_HOTSEAT){
							R2.echo("CONTAINER_HOTSEAT"+infoS[nx].title);
							if(!processHotseat(infoS[nx])){
								R2.echo("CONTAINER_HOTSEAT continue");
								continue;
							}
						if(	infoS[nx].intent!=null&&infoS[nx].intent.getComponent()
								!=null){
							infoS[nx].itemType = LauncherSettings.Favorites.ITEM_TYPE_APPLICATION; 
						}
//bugfix 17646 end
						}
						if (!filterShortcutInfo(mContext, infoS[nx])) {
							infoList.add(infoS[nx]);
							if(infoS[nx].itemType!=Favorites.ITEM_TYPE_SHORTCUT)valueS[nx].put(Favorites.ICON,"");
							valueList.add(valueS[nx]);
						}
					}
					if(surelyAddItemInDatabase(mContext,
							infoList.toArray(new ShortcutInfo[0]),
							valueList.toArray(new ContentValues[0]))==-2){
						R2.echo("QuickEntryReloader surelyAddItemInDatabase == -2");
						return false;
					}
				} catch (Exception e) {
					R2.echo("QuickEntryReloader return false 2"+ e);
//					R2.echo("QuickEntryReloader return false 2"+ e.getStackTrace());
//					R2.echo("QuickEntryReloader return false 2"+ e.getMessage());
//					return false;
				}

				return res;
			}
//bugfix 17646
			private boolean processHotseat(ShortcutInfo shortcutInfo) {
				if(shortcutInfo==null||shortcutInfo.intent==null){
					return false;
				}
				Intent intent =shortcutInfo.intent;
				String packageName = "";	
				if(intent.getComponent()==null){
					boolean isComment = false;

					R2.echo("processHotseat intent.getComponent()==null)"+intent.toString());
					if(intent.getAction()!=null){
						R2.echo("intent.getAction()"+intent.getAction());
						if(intent.getAction().equals("android.intent.action.DIAL")){
							 isComment = true;
							 packageName ="com.android.contacts";
						}else if(intent.getAction().equals("action=android.media.action.STILL_IMAGE_CAMERA")){
							
						}else if(intent.getAction().equals("android.intent.action.MUSIC_PLAYER")){
							
						}
								
					}
					if(intent.getType()!=null){
						R2.echo("intent.getType()"+intent.getType());
						if(intent.getType().equals("vnd.android-dir/mms-sms")){
							isComment = true;
							packageName ="com.android.mms";
						}
					}
					if(intent.getDataString()!=null){
						R2.echo("intent.getDataString()"+intent.getDataString());
						if(intent.getDataString().equals("content://com.android.contacts/contacts")){
						 isComment = true;
						 packageName ="com.android.contacts";}
					}
					R2.echo("isComment"+isComment+"  "+"packageName="+packageName);
					if(isComment){

						R2.echo("isComment packageName"+packageName);
						try{ 
							PackageManager pm = mContext.getPackageManager(); 
								pm.getApplicationInfo(packageName, 0);
						}catch (NameNotFoundException e) {
							R2.echo("NameNotFoundException"+e);
							return false;
							
//							shortcutInfo.itemType = LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT;   
//                        	shortcutInfo.uri = XLauncherModel.getCommandAppDownloadUri(packageName, shortcutInfo.title.toString());
							
					}
					}
					return true;

				}else{
					String pkgName = intent.getComponent().getPackageName();	
					String clsName = intent.getComponent().getClassName();
					boolean needCheck = false;
					if(pkgName==null||clsName==null){
						return false;
					}
					if(pkgName.equals("com.android.contacts")&&clsName.contains("NonPhoneActivity")){
						return false;
					}
					if ((pkgName.equals("com.android.contacts") && clsName.equals("com.android.contacts.activities.DialtactsActivity"))
							||(pkgName.equals("com.lenovo.ideafriend") && clsName.equals("com.lenovo.ideafriend.alias.DialtactsActivity"))){
						needCheck = true;
					}
					//联系人，
					else if (pkgName.equals("com.android.contacts") && clsName.equals("com.android.contacts.activities.PeopleActivity")
							||(pkgName.equals("com.lenovo.ideafriend") && clsName.equals("com.lenovo.ideafriend.alias.PeopleActivity")))
					{
						needCheck = true;
					}
					//短信
					else if ((pkgName.equals("com.android.mms") && clsName.equals("com.android.mms.ui.ConversationList"))
							|| pkgName.equals("com.lenovo.mms")
							|| pkgName.equals("com.lenovo.ideafriend"))
					{
						needCheck = true;
					}
					//浏览器
					else if (pkgName.equals("com.android.browser"))
					{
						needCheck = true;
					}
					//相机
					else if (pkgName.equals("com.android.camera")
							//			                || pkgName.equals("com.android.gallery3d")
							//			                || pkgName.equals("com.lenovo.scg")
							)
					{
						needCheck = true;
					}

					//			        //音乐
					else if (pkgName.equals("com.android.music"))
					{
						needCheck = true;
					}
					if(needCheck){
//						R2.echo("needCheck intent.getComponent().getPackageName()"+intent.getComponent().getPackageName());
//						R2.echo("needCheck intent.getComponent().getClassName()"+intent.getComponent().getClassName());
						try{ 
							PackageManager pm = mContext.getPackageManager(); 
							pm.getApplicationInfo(intent.getComponent().getPackageName(), 0);
						}catch (NameNotFoundException e) {
							R2.echo("NameNotFoundException"+e);
							return false;
//							shortcutInfo.itemType = LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT;   
//                        	if (intent != null && intent.getComponent() != null){
//                        		packageName = intent.getComponent().getPackageName();
//                        		shortcutInfo.uri = XLauncherModel.getCommandAppDownloadUri(packageName, shortcutInfo.title.toString());
                        	}
                        	
							
//						
//							return shortcutInfo;
//						}
						
					}
					
				}
//				return shortcutInfo;
				return true;
			}

		}
		/**
		 * folder reloader
		 * */
		public class FolderReloader {

			private Context mContext = null;

			public FolderReloader(final Context context) {
				mContext = context;
			}

			public boolean process(ProfileInfo profile) {
				List<InfoFactory.FolderInfo> folders = profile.folders;
				AttributesManager attrManager = new AttributesManager(profile);

				boolean res = true;
				for (int i = 0; i < folders.size(); i++) {
					InfoFactory.FolderInfo folderInfo = folders.get(i);
					FolderInfo launcherFolderInfo = new FolderInfo();

					try {
						launcherFolderInfo.title = attrManager
								.getAttributeByName(folderInfo.config,
										FolderAttributes.TITLE).getAttrValue();
						String country = System.getProperty("ro.product.countrycode");
						/*if(country == null){
						Resources resource = mContext.getResources();
			        	country = resource.getConfiguration().locale.getCountry();
						}*/
						Log.i("00", "====country="+country);

						if(folderInfo.titleList!=null && folderInfo.titleList.size()>0 && country !=null){
							for(int j = 0;j<folderInfo.titleList.size();j++){
								TitleInfo titleInfo = folderInfo.titleList.get(j);
								if(titleInfo==null) continue;
								String lbkCountry = attrManager.getAttributeByName(
										titleInfo, FolderTitleAttributes.COUNTRY).getAttrValue();
								Log.i("00", "====lbkCountry="+lbkCountry);
                                if(lbkCountry!=null && lbkCountry.equalsIgnoreCase(country)){
                                	launcherFolderInfo.title = attrManager.getAttributeByName(
    										titleInfo, FolderTitleAttributes.VALUE).getAttrValue();

                                }
							}
						}
						
						launcherFolderInfo.cellX = Integer.parseInt(attrManager
								.getAttributeByName(folderInfo.config,
										FolderAttributes.CELLX).getAttrValue());
						launcherFolderInfo.cellY = Integer.parseInt(attrManager
								.getAttributeByName(folderInfo.config,
										FolderAttributes.CELLY).getAttrValue());
						launcherFolderInfo.screen = Integer
								.parseInt(attrManager.getAttributeByName(
										folderInfo.config,
										FolderAttributes.SCREEN).getAttrValue());
						launcherFolderInfo.container = attrManager
								.getAttributeByName(folderInfo.config,
										FolderAttributes.CONTAINER)
								.getAttrValue()
								.equalsIgnoreCase(
										ContainerType.CONTAINER_HOTSEAT) ? LauncherSettings.Favorites.CONTAINER_HOTSEAT
								: LauncherSettings.Favorites.CONTAINER_DESKTOP;

						if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
							R2.echo("Touch! Add to database!!!!!!!!!!!!!!!!!");
						if(!profile.singleLayer&&launcherFolderInfo.container==LauncherSettings.Favorites.CONTAINER_HOTSEAT){
							if(launcherFolderInfo.cellX>2){
								launcherFolderInfo.cellX = launcherFolderInfo.cellX-1;
								launcherFolderInfo.screen = launcherFolderInfo.cellX;
							}
						}
						// snap
						long containerId = -1;
						ContentValues cvalue = new ContentValues();
//						try {

//							String folderSnapshot = new StringBuffer()
//									.append(profile.getKey())
//									.append(File.separator)
//									.append(File.separator)
//									.append(ConstantAdapter.DIR_SNAPSHOT)
//									.append(File.separator)
//									.append(ConstantAdapter.DIR_SNAPSHOT_SHORTCUT)
//									.append(File.separator)
//									.append(attrManager.getAttributeByName(
//											folderInfo.config,
//											FolderAttributes.ICON)
//											.getAttrValue()).toString();

//							cvalue.put(
//									ConstantPasser.Favorites.ICON_REPLACE,
//									Utilities
//											.newInstance()
//											.bitmap2ByteArray(
//													BitmapFactory
//															.decodeFile(folderSnapshot)));
//							if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
//								R2.echo("folderSnapshot is from : "
//										+ folderSnapshot);
//						} catch (Exception e) {
//						}

						// get id as the app's container

						// now extract apps
						if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
							R2.echo("Touch! 33fd-----=====fdaffd");

						int size = folderInfo.appList.folderApps.size();
						ContentValues values[] = new ContentValues[size];
						ShortcutInfo infoS[] = new ShortcutInfo[size];

						containerId = surelyAddItemInDatabase(mContext,
								new FolderInfo[] { launcherFolderInfo },
								new ContentValues[] { cvalue });
						if(containerId==-1){
							R2.echo("folder reload containerId==-1");
							continue;
						}else if(containerId==-2){
							R2.echo("FolderReloader surelyAddItemInDatabase == -2");
							return false;	
						}
						if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
							R2.echo("id is   --->> " + containerId);

						for (int j = 0; j < size; j++) {

							ApplicationLoader appLoader = new ApplicationLoader(
									mContext, profile);
							AppInfo appInfo = folderInfo.appList.folderApps
									.get(j);

							// AppInfo and QuickEntryInfo is same
							QuickEntryInfo toEntryInfo = InfoFactory.INSTANCE.new QuickEntryInfo();
							toEntryInfo.attrList = appInfo.attrList;
							toEntryInfo.name = appInfo.name;
							toEntryInfo.setKey(appInfo.getKey());

							appLoader.convertBaseInfo(attrManager, toEntryInfo,
									containerId);

							values[j] = appLoader
									.getConvertedBaseInfoContentValues();
							infoS[j] = appLoader.getConvertedShortcutInfo();

						}

						ArrayList<ShortcutInfo> infoList = new ArrayList<ShortcutInfo>();
						ArrayList<ContentValues> valueList = new ArrayList<ContentValues>();
						try {

							for (int nx = 0; nx < infoS.length; nx++) {
 		/*RK_ID:RK_SD_APPS liuyg1 2013-5-8. START***/
								ShortcutInfo sinfo = infoS[nx];
								 int itemType = infoS[nx].itemType;
									R2.echo("FolderReloader for"+infoS[nx].title);
									R2.echo("FolderReloader for"+infoS[nx].itemType);
								 Intent intent = sinfo.intent;
								 PackageManager pm = mContext.getPackageManager();  
//								 if(itemType == LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT&&GlobalDefine.getVerisonCMCCTDConfiguration(mContext)){
								 if(itemType == LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT){
									 continue;
								 }
								if (itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION ||
										itemType == LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT) {
									R2.echo("FolderReloader for itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION"+infoS[nx].title);
									try{ 
										R2.echo("FolderReloader try"+intent);
										R2.echo("FolderReloader try"+infoS[nx].intent);
										if (intent != null && intent.getComponent() != null){
											R2.echo("FolderReloader for intent.getComponent().getPackageName()"+intent.getComponent().getPackageName());
											pm.getApplicationInfo(intent.getComponent().getPackageName(), 0);
											R2.echo("FolderReloader for intent.getComponent().getPackageName()"+intent.getComponent().getPackageName());
											  List<ResolveInfo> apps = pm.queryIntentActivities(intent, 0);
								                if (apps == null || apps.size() <=0) {
								                	continue;
								                }
								                R2.echo("FolderReloader for apps.size()"+apps.size());
											infoS[nx].itemType = LauncherSettings.Favorites.ITEM_TYPE_APPLICATION;
//											if(intent.getComponent().getPackageName()!=null&&intent.getComponent().getPackageName().equals("com.lenovo.app.Calendar")){
//												sinfo.intent = pm.getLaunchIntentForPackage(intent.getComponent().getPackageName());

//											}
										}
									}catch (NameNotFoundException e) {
										R2.echo("FolderReloader for NameNotFoundException e"+infoS[nx].title);
										continue;
//										infoS[nx].itemType = LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT;  
//										 if(GlobalDefine.getVerisonCMCCTDConfiguration(mContext)){
//											 continue;
//										 }
//		                                String packageName = null;
//		                            	if (intent != null && intent.getComponent() != null){
//		                            		packageName = intent.getComponent().getPackageName();
//		                            	}
//										infoS[nx].uri = XLauncherModel.getCommandAppDownloadUri(packageName, infoS[nx].title.toString());
//										if(sinfo!=null&&sinfo.intent!=null&&sinfo.intent.getComponent()!=null){
//											if(sinfo.intent.getComponent().getPackageName().equals("com.android.gallery3d")){
//												 Intent intent1 = getInstalledGalleyIntent(mContext);
//													
//												 if(intent1!=null){
//													 infoS[nx].intent = intent1;
//													 infoS[nx].itemType = LauncherSettings.Favorites.ITEM_TYPE_APPLICATION;
//													 infoS[nx].uri = null;
//												}else{
//													continue;
//												}
//
//											}else if(sinfo.intent.getComponent().getPackageName().equals("com.lenovo.app.Calendar")){
//												continue;
//											}
//
//										}else{
//											continue;
//										}

									}

								} 
					 /*RK_ID:RK_SD_APPS liuyg1 2013-5-8. END***/
								if (!filterShortcutInfo(mContext, infoS[nx])) {
									infoList.add(infoS[nx]);
									R2.echo("FolderReloader infoList.add(infoS[nx]);"+infoS[nx].title);
									R2.echo("FolderReloader infoList.add(infoS[nx]);"+infoS[nx].itemType);
									if(infoS[nx].itemType!=Favorites.ITEM_TYPE_SHORTCUT)values[nx].put(Favorites.ICON,"");
									valueList.add(values[nx]);
								}
							}

						} catch (Exception e) {
							
						}

						try {
							if (infoList == null || infoList.size() == 0
									|| infoList.isEmpty()) {
//								XLauncherModel.deleteItemFromDatabase(mContext,
//										launcherFolderInfo);
//								return true;
							}
						} catch (Exception e) {
							continue;
						}

						if(surelyAddItemInDatabase(mContext,
								infoList.toArray(new ShortcutInfo[0]),
								valueList.toArray(new ContentValues[0]))==-2){
							R2.echo("FolderReloader surelyAddItemInDatabase == -2");
							return false;
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				return res;
			}
		}
  /*RK_ID:RK_SD_APPS liuyg1 2013-5-8. START***/
		public static List<PackageInfo> getAllSysTemApps(Context context) {
	        List<PackageInfo> apps = new ArrayList<PackageInfo>();
	        PackageManager pManager = context.getPackageManager();
	        //获取手机内所有应用
	        List<PackageInfo> paklist = pManager.getInstalledPackages(0);
	        for (int i = 0; i < paklist.size(); i++) {
	            PackageInfo pak = (PackageInfo) paklist.get(i);
	            //判断是否为系统预装的应用程序
	            if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) != 0) {
	                // customs applications
	                apps.add(pak);
	            }
	        }
	        return apps;
	    }
 /*RK_ID:RK_SD_APPS liuyg1 2013-5-8. END***/
  /*RK_ID:RK_SD_APPS liuyg1 2013-5-8. START***/
		private Intent getInstalledGalleyIntent(Context context) {
			PackageManager packageManager = context.getPackageManager();
			final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
			mainIntent.addCategory(Intent.CATEGORY_APP_GALLERY);

			final List<ResolveInfo> infolist = packageManager.queryIntentActivities(mainIntent, 0);
			Intent intent = null;
			List<PackageInfo> appSystemList  = getAllSysTemApps(context); 
			ResolveInfo info = null;
			if(infolist.size()!=0){
				for(ResolveInfo rInfo:infolist){
					for(PackageInfo pInfo:appSystemList){

						if(pInfo.packageName.equals(rInfo.activityInfo.packageName)){
							info = rInfo; 
							Log.d("liuyg1", "get system Gallery "+pInfo.packageName);
							break;
						}
					}
					if(info!=null){
						break;
					}
				}
				if(info==null){
					info = (ResolveInfo)infolist.get(0);
				}
				if(info!=null&& info.activityInfo!=null){
					String packagename = info.activityInfo.packageName;
					String className = info.activityInfo.name;
					intent = new Intent(Intent.ACTION_MAIN);
					intent.addCategory(Intent.CATEGORY_LAUNCHER);
					intent.setComponent(new ComponentName(packagename,
							className));
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				}
			}else{
				Log.d("liuyg1", "infolist.size()!=0");
			}
			return intent;
		}
  /*RK_ID:RK_SD_APPS liuyg1 2013-5-8. END***/
		/**
		 * priorities reloader
		 * */

		public class PriorityReloader {

			private Context mContext = null;

			private boolean databaseOperationReturned = false;

			public PriorityReloader(final Context context) {
				mContext = context;
			}

			public boolean process(ProfileInfo info) {
				int end = info.priorities.size();
				AttributesManager atm = new AttributesManager(info);

				// R2
				Long t;
				for (int i = 0; i < end; i++) {
					PriorityInfo priority = info.priorities.get(i);
					int end2 = priority.priorityRules.size();
					final ArrayList<ApplicationInfo> appInfoS = new ArrayList<ApplicationInfo>();
					for (int j = 0; j < end2; j++) {
						ApplicationInfo applicationInfo = new ApplicationInfo();
						InfoFactory.AppInfo appInfo = priority.priorityRules
								.get(j);
						String packageName, className;
						try {
							packageName = atm.getAttributeByName(appInfo,
									PriorityAttributes.PACKAGENAME)
									.getAttrValue();
							className = atm.getAttributeByName(appInfo,
									PriorityAttributes.CLASSNAME)
									.getAttrValue();

							applicationInfo.title = atm.getAttributeByName(
									appInfo, PriorityAttributes.TITLE)
									.getAttrValue();

							int dragValue = 1;
							try {
								dragValue = Integer.valueOf(atm
										.getAttributeByName(appInfo,
												PriorityAttributes.DRAGABLE)
										.getAttrValue());
							} catch (Exception e) {
								e.printStackTrace();
							}

							applicationInfo.canDrag = dragValue == 1 ? true
									: false;

							applicationInfo.componentName = new ComponentName(
									packageName, className);

							appInfoS.add(applicationInfo);

						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}
					}

					// R3.echo("Priorities is : " + appInfoS);

					// if(com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
					R2.echo("Waiting for action_sync_todb");

					// ContentResolver cr = mContext.getContentResolver();

					// R2
					LauncherApplication app = (LauncherApplication) mContext
							.getApplicationContext();
					LauncherProvider cp = app.getLauncherProvider();
					// 2R

					ContentValues[] contentValues = new ContentValues[appInfoS
							.size()];
					// R2
					if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
						R2.echo("TOUCH XXX 0!!!!!!!!!!!!!!!!!!!!"
								+ (t = System.currentTimeMillis()));
					for (int index = 0; index < appInfoS.size(); index++) {
						try {

							ApplicationInfo infoNow = appInfoS.get(index);
							if (infoNow.title == null) {
								infoNow.title = "";
							}

							contentValues[index] = new ContentValues();

							contentValues[index].put(
									LauncherSettings.Applications._ID, app
											.getLauncherProvider()
											.generateNewAppsId());

							contentValues[index].put(
									LauncherSettings.Applications.APP_CAN_DRAG,
									infoNow.canDrag ? 1 : 0);
							contentValues[index].put(
									LauncherSettings.Applications.LABEL,
									infoNow.title.toString());
							contentValues[index].put(
									LauncherSettings.Applications.CELL_INDEX,
									index);
							contentValues[index].put(
									LauncherSettings.Applications.CLASS,
									infoNow.componentName.flattenToString());

						} catch (Exception e) {
							if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
								R2.echo("ExceptionSSS!");
							e.printStackTrace();
							continue;
						}
						if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
							R2.echo("TOUCH XXX 1!!!!!!!!!!!!!!!!!!!!"
									+ (System.currentTimeMillis() - t));
					}
				if(	cp.bulkInsert(LauncherSettings.Applications.CONTENT_URI,
							contentValues)==-1){
					R2.echo("PriorityReloader cp.bulkInsert == -1");
					return false;
				}
				}

				return true;
			}
		}

		// length of infos should be equal with the lenght of extraValues
		public long surelyAddItemInDatabase(final Context context,
				ItemInfo[] infos, ContentValues[] extraValues) {

			ContentValues[] allValues = new ContentValues[infos.length];
			for (int i = 0; i < infos.length; i++) {
				// R2

				allValues[i] = new ContentValues();

				if (extraValues != null) {
					allValues[i].putAll(extraValues[i]);
				}

				LauncherApplication app = (LauncherApplication) context
						.getApplicationContext();
				infos[i].id = app.getLauncherProvider().generateNewId();
				allValues[i].put(LauncherSettings.Favorites._ID, infos[i].id);
				infos[i].updateValuesWithCoordinates(allValues[i],
						infos[i].cellX, infos[i].cellY);
				allValues[i].put(Favorites.CONTAINER, infos[i].container);
				allValues[i].put(Favorites.SCREEN, infos[i].screen);
				allValues[i].put(Favorites.SPANX, infos[i].spanX);
				allValues[i].put(Favorites.SPANY, infos[i].spanY);
				allValues[i].put(Favorites.ITEM_TYPE, infos[i].itemType);

				if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
					R2.echo("info is : " + infos[i].getClass().getSimpleName());
				if (infos[i] instanceof FolderInfo) {
					FolderInfo folder = (FolderInfo) infos[i];
					allValues[i].put(Favorites.TITLE, folder.title.toString());

				} else if (infos[i] instanceof ShortcutInfo) {
					ShortcutInfo shortcut = (ShortcutInfo) infos[i];
					allValues[i]
							.put(Favorites.INTENT, shortcut.intent.toUri(0));
					/*RK_ID:RK_SD_APPS liuyg1 2013-5-8. START***/
					if( shortcut.itemType == LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT){
					    allValues[i]
							.put(Favorites.URI, shortcut.uri);
					}
					/*RK_ID:RK_SD_APPS liuyg1 2013-5-8. END***/
					allValues[i]
							.put(Favorites.TITLE, shortcut.title.toString());
					if (shortcut.replaceTitle != null
							&& !"".equals(shortcut.replaceTitle.toString()
									.trim())) {
						allValues[i].put(Favorites.TITLE_REPLACE,
								shortcut.replaceTitle.toString());
					}
//					if (!allValues[i].containsKey(Favorites.ICON)) {
//						allValues[i].put(
//								Favorites.ICON,
//								Utilities.newInstance().bitmap2ByteArray(
//										app.getModel().getFallbackIcon()));
//					}
					if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
						R2.echo("ShortcutInfo add : " + infos[i].id);
				} else if (infos[i] instanceof LauncherAppWidgetInfo) {
					LauncherAppWidgetInfo appWidgetInfo = (LauncherAppWidgetInfo) infos[i];
					allValues[i].put(LauncherSettings.Favorites.APPWIDGET_ID,
							appWidgetInfo.appWidgetId);
				}

				// commend face  --- coordinated with MEM OPT.
				if (infos[i] instanceof LauncherAppWidgetInfo) {
					LauncherAppWidgetInfo infoTmp = (LauncherAppWidgetInfo) infos[i];
					if (infoTmp != null) {
						if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
							R2.echo("Settle Commend face 2.");
						ItemInfo.writeBitmap(allValues[i], infoTmp.iconBitmap);
						if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
							R2.echo("Settle Commend face 1.");
						infoTmp = null;
					}
				}
				/*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. S***/
				else if(infos[i] instanceof LenovoWidgetViewInfo) {
					LenovoWidgetViewInfo infoTmp = (LenovoWidgetViewInfo) infos[i];
					if (infoTmp != null) {
						Log.i("zdx1","************Write Bitmap****");
						ItemInfo.writeBitmap(allValues[i], infoTmp.iconBitmap);
						infoTmp = null;
					}
				}
				/*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. E***/
				
				allValues[i].put(LauncherSettings.Favorites.LAST_USE_TIME, System.currentTimeMillis());

			}

			// bulk insert now
			LauncherApplication app = (LauncherApplication) context
					.getApplicationContext();
			LauncherProvider cp = app.getLauncherProvider();
			if(cp.bulkInsert(
					LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION,
					allValues)==-1){
				R2.echo("surelyAddItemInDatabase cp.bulkInsert == -1");
				return -2;
			}
			
			SettingsValue.rescheduleCleanupAlarm(context);

			try {
				return infos[0].id;
			} catch (Exception e) {
				return -1;
			}
		}


	static class DefaultApp{
        private boolean mDefault;  
        private String mIntent;
        private int mResId;
	}
	
    public static DefaultApp isDefaultApp(String pkgName, String clsName, String actionOfUirShortcut)
    {
        R5.echo("getPackageName = " + pkgName + "class = " + clsName);
        DefaultApp ret = new DefaultApp();
        //拨号，
        if ((pkgName.equals("com.android.contacts") && clsName.equals("com.android.contacts.activities.DialtactsActivity"))
              ||(pkgName.equals("com.lenovo.ideafriend") && clsName.equals("com.lenovo.ideafriend.alias.DialtactsActivity")))
        {
            ret.mDefault = true;
            ret.mIntent = "#Intent;action=android.intent.action.DIAL;launchFlags=0x10000000;end";
            ret.mResId = R.drawable.com_android_contacts__ic_launcher_phone;            
        }
        //联系人，
        else if (pkgName.equals("com.android.contacts") && clsName.equals("com.android.contacts.activities.PeopleActivity")
                ||(pkgName.equals("com.lenovo.ideafriend") && clsName.equals("com.lenovo.ideafriend.alias.PeopleActivity")))
        {
            ret.mDefault = true;
            ret.mIntent = "content://com.android.contacts/contacts#Intent;action=android.intent.action.VIEW;launchFlags=0x10000000;end";
            ret.mResId = R.drawable.com_android_contacts__ic_launcher_contacts;            
        }
        //短信
        else if ((pkgName.equals("com.android.mms") && clsName.equals("com.android.mms.ui.ConversationList"))
                || pkgName.equals("com.lenovo.mms")
                || pkgName.equals("com.lenovo.ideafriend"))
        {
            ret.mDefault = true;
            ret.mIntent = "#Intent;action=android.intent.action.MAIN;type=vnd.android-dir/mms-sms;launchFlags=0x10000000;end";
            ret.mResId = R.drawable.com_android_mms__ic_launcher_smsmms;  
        }
        //浏览器
        else if (pkgName.equals("com.android.browser")
                || (actionOfUirShortcut !=  null && actionOfUirShortcut.equals("http://m.idea123.cn/?c=02#Intent;action=android.intent.action.VIEW;S.com.android.browser.application_id=-2036735900831708960;end")))
        {
            ret.mDefault = true;
            ret.mIntent = "http://m.idea123.cn/?c=02#Intent;action=android.intent.action.VIEW;S.com.android.browser.application_id=-2036735900831708960;end";
            ret.mResId = R.drawable.com_android_browser__ic_launcher_browser;  
        }
        //相机
        else if (pkgName.equals("com.android.camera")
//                || pkgName.equals("com.android.gallery3d")
//                || pkgName.equals("com.lenovo.scg")
                )
        {
            ret.mDefault = true;
            ret.mIntent = "#Intent;action=android.media.action.STILL_IMAGE_CAMERA;launchFlags=0x10000000;end";
            ret.mResId = R.drawable.com_android_camera__ic_launcher_camera;  
        }
//        //图库
//        else if (pkgName.equals("com.android.gallery3d"))
//        {
//            ret.mDefault = true;
//            ret.mIntent = "#Intent;action=android.intent.action.VIEW;type=vnd.android.cursor.dir/image;launchFlags=0x10000000;end";
//            ret.mResId = R.drawable.com_android_camera__ic_launcher_camera;  
//        }
//        //音乐
//        else if (pkgName.equals("com.android.music"))
//        {
//            ret.mDefault = true;
//            ret.mIntent = "#Intent;action=android.intent.action.MUSIC_PLAYER;launchFlags=0x10000000;end";
//            ret.mResId = R.drawable.com_android_camera__ic_launcher_camera;  
//        }
        
        return ret;
    }
	}
	private ProfileInteractiveHelper(){
		
	}
}
