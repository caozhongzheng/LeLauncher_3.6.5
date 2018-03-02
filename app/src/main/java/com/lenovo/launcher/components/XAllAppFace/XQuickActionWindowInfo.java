/*
 * Copyright (C) 2012
 *
 * AUT: liuli liuli0120@use.com.cn DATE: 2012-01-11
 * when user long click launcher's icon, including application icon, shortcut icon and widget
 * Represents the icon info and other info.
 */

package com.lenovo.launcher.components.XAllAppFace;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import android.app.Dialog;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.os.StatFs;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.mms.ContentType;
import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher2.addon.share.LeShareUtils;
import com.lenovo.launcher2.commoninterface.FolderInfo;
import com.lenovo.launcher2.commoninterface.IconCache;
import com.lenovo.launcher2.commoninterface.ItemInfo;
import com.lenovo.launcher2.commoninterface.LauncherAppWidgetInfo;
import com.lenovo.launcher2.commoninterface.LauncherSettings;
import com.lenovo.launcher2.commoninterface.LenovoWidgetViewInfo;
import com.lenovo.launcher2.commoninterface.ShortcutInfo;
import com.lenovo.launcher2.commonui.LauncherAppWidgetHost;
import com.lenovo.launcher2.commonui.LauncherAppWidgetHostView;
import com.lenovo.launcher2.commonui.LeDialog;
import com.lenovo.launcher2.customizer.Constants;
import com.lenovo.launcher2.customizer.Reaper;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.Utilities;
import com.lenovo.launcher2.weather.widget.utils.WeatherUtilites;

public class XQuickActionWindowInfo {
    // infos
    private XLauncher mXLauncher;
    private ItemInfo mInfo;
    private DrawableItem mLongClickView;

    // views
    private RelativeLayout mContentView;
    private ImageView mIndicator;

    private File mChangeIamge;

    // dimens
    private int mActionCount = 0;
    private int mWidth;

    public int mCellWidth;
    public int mBkPading;
    public int mHalfIndicator;

    private static final String QUICK_FOLDER = "/quick_diy";

    /* RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2012-02-07 . S */
    LauncherApplication mApp;

    /* RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2012-02-07 . E */
    /*RK_ID:RK_LESHARE AUT:liuyg1@lenovo.com DATE: 2012-12-21 START */
    private static final String mimeType = "application/vnd.android.package-archive";
    private boolean isLauncherShortcut = false;
    private HashMap<String,String> mSettinMap;
/*RK_ID:RK_LESHARE AUT:liuyg1@lenovo.com DATE: 2012-12-21 END */  
    /*
     * initialize all information of quick action window, including strings and icons, indicators
     * and click listener. NOTE: the function must be called before QuickActionWindow
     */
    public XQuickActionWindowInfo(XLauncher xlauncher, final ItemInfo itemInfo, final DrawableItem v) {
        mXLauncher = xlauncher;
        mInfo = itemInfo;
        mLongClickView = v;
        mSettinMap = Utilities.getLeosWidgetSettingMap();
        LayoutInflater inflater = mXLauncher.getLayoutInflater();
        mContentView = (RelativeLayout) inflater
                .inflate(R.layout.icon_longclick_popup, null, false);

        // we will according to item type, show different user operations
        // so initialize different strings and drawables
//        View child = info.cell;
//        final Object itemInfo = child.getTag();

        Resources resources = xlauncher.getResources();
        mCellWidth = resources.getDimensionPixelSize(R.dimen.quick_action_cell_width_with_padding);
//        BK_SPACING = resources.getDimensionPixelSize(R.dimen.quick_action_window_bk_spacing);

        /* RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2012-02-07 . S */
        mApp = (LauncherApplication) mXLauncher.getApplicationContext();
        Drawable bg = mApp.mLauncherContext.getDrawable(R.drawable.quickaction_bg);
//        /*RK_ID: RK_QUICKACTION . AUT: liuli1 . DATE: 2012-03-14 . S*/
//        if (bg instanceof NinePatchDrawable) {
//            Rect padding = new Rect();
//            bg.getPadding(padding);
//            mBkPading = padding.left;
//        } else {
//            mBkPading = 0;
//        }
//        /*RK_ID: RK_QUICKACTION . AUT: liuli1 . DATE: 2012-03-14 . E*/
        LinearLayout quickActionLayout = (LinearLayout) mContentView
                .findViewById(R.id.quick_action_layout);
        if (quickActionLayout != null) {
            Utilities.setBackgroundDrawable(quickActionLayout, bg);
        }

        bg = mApp.mLauncherContext.getDrawable(R.drawable.quickaction_arrow_up);
        ImageView indicator_up = (ImageView) mContentView
                .findViewById(R.id.quick_action_indicator_up);
        if (indicator_up != null) {
            indicator_up.setImageDrawable(bg);

            int indicatorUpMarginBottom = mApp.mLauncherContext.getDimensionPixel(
                    R.dimen.quick_indicator_up_margin_bottom,
                    R.dimen.def__quick_indicator_up_margin_bottom);
            ((RelativeLayout.LayoutParams) indicator_up.getLayoutParams()).setMargins(0, 0, 0,
                    indicatorUpMarginBottom);
        }
        int indicatorDownMarginTop = mApp.mLauncherContext.getDimensionPixel(
                R.dimen.quick_indicator_down_margin_top,
                R.dimen.def__quick_indicator_down_margin_top);

        bg = mApp.mLauncherContext.getDrawable(R.drawable.quickaction_arrow_down);
        /* RK_ID: RK_QUICKACTION . AUT: liuli1 . DATE: 2012-03-14 . S */
        mHalfIndicator = bg.getIntrinsicWidth() / 2;
        /* RK_ID: RK_QUICKACTION . AUT: liuli1 . DATE: 2012-03-14 . E */
        ImageView indicator_down = (ImageView) mContentView
                .findViewById(R.id.quick_action_indicator_down);
        if (indicator_down != null) {
            indicator_down.setImageDrawable(bg);

            ((RelativeLayout.LayoutParams) indicator_down.getLayoutParams()).setMargins(0,
                    indicatorDownMarginTop, 0, 0);
        }
        /* RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2012-02-03 . E */

        final int[] iconRes = { R.drawable.quick_zoom_icon, R.drawable.quick_replace_icon,
                R.drawable.quick_rename_icon, R.drawable.quick_delete_icon,
                R.drawable.quick_uninstall_icon,R.drawable.quick_share_icon,R.drawable.quick_rename_icon  };
        final int[] iconId = { R.id.rename_icon, R.id.replace_icon, R.id.rename_icon,
                R.id.delete_icon, R.id.uninstall_icon,R.id.share_icon,R.id.rename_icon  };
        int[] stringId = { R.string.quick_action_zoom, R.string.quick_action_replace,
                R.string.quick_action_rename, R.string.quick_action_delete,
                R.string.quick_action_uninstall,R.string.menu_share ,R.string.quick_action_setting  };

        boolean[] enabled = { true, true, true, false, false,false,false };

        if (itemInfo instanceof ShortcutInfo) {
            // disable widget zoom action
            enabled[0] = false;

            /* RK_CANNOTDIY dining@lenovo 2012-9-14 S */
            // don't show rename and change icon item
            boolean bTemp = isNotDiyApp((ShortcutInfo) itemInfo);
            if (bTemp) {
                enabled[1] = false;
                enabled[2] = false;
            }
            /* RK_CANNOTDIY dining@lenovo 2012-9-14 E */
            // if is hotseat, disable rename action
            if (mXLauncher.isHotseatLayout(v.getParent())) {
                enabled[2] = false;
            }
            // if the application is not system app, enable "uninstall" action
            enabled[4] = !isSystemApp((ShortcutInfo) itemInfo);
           
            /*enabled[5] = LeShareUtils.isInstalledQiezi(mXLauncher) && !isLauncherShortcut
            		&&(((ShortcutInfo) itemInfo).itemType != LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT);*/
           initQuickActionWindow(iconRes, iconId, stringId, enabled);
        } else if (itemInfo instanceof LauncherAppWidgetInfo
                || (itemInfo instanceof ItemInfo && !(itemInfo instanceof LenovoWidgetViewInfo)
                        && (((ItemInfo) itemInfo).spanX > 1 || ((ItemInfo) itemInfo).spanY > 1))) {
            // disable all application action : replace, rename, uninstall
            
        	//should be removed
        	/*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-5-30. S***/
        	//enabled[0] = false;
        	/*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-5-30. E***/
        	enabled[1] = false;
            enabled[2] = false;
            /*enabled[5] = LeShareUtils.isInstalledQiezi(mXLauncher);*/
            initQuickActionWindow(iconRes, iconId, stringId, enabled);
        } else if (itemInfo instanceof LenovoWidgetViewInfo) {
            enabled[0] = false;
            enabled[1] = false;
            enabled[2] = false;
/*RK_ID:RK_WIDGET_SETTING AUT:liuyg1@lenovo.com DATE: 2013-04-24 START */
            final LenovoWidgetViewInfo lenovoWidgetViewInfo = (LenovoWidgetViewInfo) itemInfo;
        	Log.d("liuyg1","lenovoWidgetViewInfo.className="+lenovoWidgetViewInfo.className);
            if(mSettinMap.get(lenovoWidgetViewInfo.className)!=null||lenovoWidgetViewInfo.packageName.contains(WeatherUtilites.THIS_WEATHER_WIDGET)){
            	Log.d("liuyg1", "lenovoWidgetViewInfo"+lenovoWidgetViewInfo.className);
            	enabled[6] = true;
            }
/*RK_ID:RK_WIDGET_SETTING AUT:liuyg1@lenovo.com DATE: 2013-04-24 END */
            initQuickActionWindow(iconRes, iconId, stringId, enabled);
        } else if (itemInfo instanceof FolderInfo) {
            // modify folder rename text to edit
            stringId[2] = R.string.quick_action_edit;

            // disable actions: zoom, uninstall
            enabled[0] = false;
            enabled[2] = false;

            initQuickActionWindow(iconRes, iconId, stringId, enabled);
        }
    }

    /* RK_CANNOTDIY dining@lenovo 2012-9-14 S */
    private boolean isNotDiyApp(ShortcutInfo itemInfo) {
        boolean bCannotDiy = false;

        AllAppSortHelper mSortHelper = mXLauncher.getSortHelper();
        if (mSortHelper != null) {
            // check the diy value
            ShortcutInfo tempItem = (ShortcutInfo) itemInfo;
            ComponentName component = tempItem.intent.getComponent();
            if (component != null) {
                bCannotDiy = mSortHelper.checkCannotDiy(component);
            } else {
                // shortcut info icon have an intent without component name
                String packageName = null;
                final Intent intent = tempItem.intent;
                Parcelable extra = intent.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);
                if (extra != null && extra instanceof ShortcutIconResource) {
                    ShortcutIconResource iconResource = (ShortcutIconResource) extra;
                    packageName = iconResource.packageName;
                }
                if (packageName != null) {
                    bCannotDiy = mSortHelper.checkCannotDiy(packageName);
                }

            }
        }
        return bCannotDiy;

    }

    /* RK_CANNOTDIY dining@lenovo 2012-9-14 E */
    private boolean isSystemApp(ShortcutInfo itemInfo) {
        boolean isSystemApp = false;
        final PackageManager packageManager = mXLauncher.getPackageManager();
        int appFlags = 0;
        ComponentName component = itemInfo.intent.getComponent();
        String packageName = null;

        if (component != null) {
            // application info icon have a component name
            packageName = component.getPackageName();

        } else {
            // shortcut info icon have an intent without component name
            final Intent intent = itemInfo.intent;
            Parcelable extra = intent.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);
            if (extra != null && extra instanceof ShortcutIconResource) {
                ShortcutIconResource iconResource = (ShortcutIconResource) extra;
                packageName = iconResource.packageName;

            } else if (itemInfo.iconResource != null) {
                packageName = itemInfo.iconResource.packageName;
            } else {
                List<ResolveInfo> apps = packageManager.queryIntentActivities(intent, 0);

                if (apps != null && apps.size() > 0) {
                    ResolveInfo info = apps.get(0);
                    appFlags = info.activityInfo.applicationInfo.flags;
                }
            }
        }

        if (packageName != null && packageName.equals(getContext().getPackageName())) {
            Log.i(XQuickActionWindow.TAG, "this is com.lenovo.launcher, disable uninstall");
            isSystemApp = true;

        } else if (packageName != null) {
            try {
                appFlags = packageManager.getApplicationInfo(packageName, 0).flags;

            } catch (NameNotFoundException e) {
                // cannot find this app for intent, so disable uninstall.
                isSystemApp = true;
                e.printStackTrace();
            }
        } else if (appFlags == 0) {
            // has no package, and had no application, so disable uninstall.
            isSystemApp = true;
        }

        if ((appFlags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0) {
            isSystemApp = true;
            if ((appFlags & android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0)
                isSystemApp = false;
        }

        return isSystemApp;
    }

    private void initQuickActionWindow(int[] icons, int[] resIds, int[] strings, boolean[] enabled) {
        final DrawableItem child = mLongClickView;
        /* RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2012-02-07 . S */
//        Resources res = mXLauncher.getResources();        
        /* RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2012-02-07 . E */

        // icon and text
        for (int i = 0; i < enabled.length; i++) {
            if (!enabled[i])
                continue;
            TextView textView = (TextView) mContentView.findViewById(resIds[i]);
            textView.setVisibility(View.VISIBLE);
            /* RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2012-02-07 . S */
//            Drawable bg = Utilities.findDrawableById(res, 
//                  R.drawable.focusable_view_bg, 
//                  fc);
//            if (bg == null) {     
//              bg = res.getDrawable(R.drawable.focusable_view_bg);
//            }

            Drawable iconDrawable = mApp.mLauncherContext.getDrawable(icons[i]);
            textView.setCompoundDrawablesWithIntrinsicBounds(null, iconDrawable, null, null);

            ColorStateList textColor = mApp.mLauncherContext.getColor(
                    R.color.quick_action_text_color, R.color.def__quick_action_text_color);
            textView.setTextColor(textColor);
            /* RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2012-02-07 . E */

//            textView.setCompoundDrawablesWithIntrinsicBounds(null, mXLauncher.getResources().getDrawable(icons[i]),
//                    null, null);
            textView.setText(strings[i]);

            // set tag for on click
            textView.setTag(R.id.quick_action_layout, strings[i]);
            textView.setTag(child);
            textView.setOnClickListener(mQuickActionClick);
            mActionCount++;
        }

        mWidth = mActionCount * mCellWidth + mBkPading * 2;

        // indicator chooser
        ImageView indicator_up = (ImageView) mContentView
                .findViewById(R.id.quick_action_indicator_up);
        ImageView indicator_down = (ImageView) mContentView
                .findViewById(R.id.quick_action_indicator_down);
        if (mInfo.cellY == 0 && !mXLauncher.isHotseatLayout(mLongClickView.getParent())) {
            indicator_up.setVisibility(View.VISIBLE);
            indicator_down.setVisibility(View.GONE);
            mIndicator = indicator_up;
        } else {
            indicator_up.setVisibility(View.GONE);
            indicator_down.setVisibility(View.VISIBLE);
            mIndicator = indicator_down;
        }
    }

    private final View.OnClickListener mQuickActionClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            /** AUT: xingqx xingqx@lenovo.com DATE: 2012-03-15 start*/
//            if (mXLauncher.getDragController().dragging()) {
//                mXLauncher.dismissQuickActionWindow();
//                return;
//            }
            /** AUT: xingqx xingqx@lenovo.com DATE: 2012-03-15 end*/
            final DrawableItem cell = (DrawableItem) v.getTag();
            final Object info = cell.getTag();
            int id = (Integer) v.getTag(R.id.quick_action_layout);
            switch (id) {
            case R.string.quick_action_replace:
                if (info instanceof ShortcutInfo || info instanceof FolderInfo) {
//                    final BubbleTextView btv = (BubbleTextView) cell;
//                    final ShortcutInfo si = (ShortcutInfo) info;
                    showReplaceChooser(cell, (ItemInfo) info);
                }
                break;

           /*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-5-30. S***/    
            case R.string.quick_action_zoom:
                resizeCell(cell, info);
                break;
            /*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-5-30. E***/

            case R.string.quick_action_rename:
                if (cell instanceof XShortcutIconView && info instanceof ShortcutInfo) {
                    final ShortcutInfo si = (ShortcutInfo) info;
                    showRenameDialog(si, (XShortcutIconView) cell, info);
                }
                break;

            case R.string.quick_action_delete:
                completeDrop(info, cell);
                break;

            case R.string.quick_action_uninstall:
                if (info instanceof ShortcutInfo) {
                    Intent intent = new Intent(Intent.ACTION_DELETE);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    String packageName = null;
                    if (((ShortcutInfo) info).intent.getComponent() == null) {
                        PackageManager packageManager = mXLauncher.getPackageManager();

                        Intent mIntent = ((ShortcutInfo) info).intent;
                        Parcelable extra = mIntent
                                .getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);
                        if (extra != null && extra instanceof ShortcutIconResource) {
                            ShortcutIconResource iconResource = (ShortcutIconResource) extra;
                            packageName = iconResource.packageName;
                        } else if (((ShortcutInfo) info).iconResource != null) {
                            packageName = ((ShortcutInfo) info).iconResource.packageName;
                        } else {
                            List<ResolveInfo> apps = packageManager.queryIntentActivities(mIntent,
                                    0);

                            if (apps != null && apps.size() > 0) {

                                ResolveInfo mInfo = apps.get(0);
                                packageName = mInfo.activityInfo.applicationInfo.packageName;
                            }
                        }
                    } else {
                        packageName = ((ShortcutInfo) info).intent.getComponent().getPackageName();
                    }
                    intent.setData(Uri.parse("package:" + packageName));
                    mXLauncher.startActivity(intent);
                }
                break;
            case R.string.menu_share:
                /*RK_ID:RK_LESHARE AUT:liuyg1@lenovo.com DATE: 2012-12-21 START */
            	
            	if(!Utilities.isInstalledRightQiezi(mXLauncher)){
            		Utilities.showInstallDialog(mXLauncher,true);
            	}else{
            		Intent shareIntent = new Intent(Intent.ACTION_SEND);
//            		ComponentName  mComponentName = new ComponentName("com.lenovo.anyshare", "com.lenovo.anyshare.apexpress.ApDiscoverActivity"); 
//            		shareIntent.setComponent(mComponentName);
            		shareIntent.setType(mimeType);

            		PackageManager pm = mXLauncher.getPackageManager();
            		
            		List<ResolveInfo> infoList = pm.queryIntentActivities(shareIntent, 0);
            		for (ResolveInfo reInfo : infoList) {
            			String pn = reInfo.activityInfo.packageName;

            			if (pn.equalsIgnoreCase("com.lenovo.anyshare")) {
            				String activity = reInfo.activityInfo.name;
            				shareIntent.setClassName("com.lenovo.anyshare", activity);
            				Log.d("liuyg1", "find activity: " + activity);
            				break;
            			}
            		}
            		
            		
            		String packageName = null;
            		if (info instanceof ShortcutInfo) {

            			if (((ShortcutInfo) info).intent.getComponent() == null) {
            				PackageManager packageManager = mXLauncher
            						.getPackageManager();

            				Intent mIntent = ((ShortcutInfo) info).intent;
            				Parcelable extra = mIntent
            						.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);
            				if (extra != null
            						&& extra instanceof ShortcutIconResource) {
            					ShortcutIconResource iconResource = (ShortcutIconResource) extra;
            					packageName = iconResource.packageName;
            				} else if (((ShortcutInfo) info).iconResource != null) {
            					packageName = ((ShortcutInfo) info).iconResource.packageName;
            				} else {
            					List<ResolveInfo> apps = packageManager
            							.queryIntentActivities(mIntent, 0);

            					if (apps != null && apps.size() > 0) {
            						Log.d(XQuickActionWindow.TAG, "-------2--------------");

            						ResolveInfo mInfo = apps.get(0);
            						packageName = mInfo.activityInfo.applicationInfo.packageName;
            					}
            				}
            			} else {
            				packageName = ((ShortcutInfo) info).intent
            						.getComponent().getPackageName();
            			}
            		}else if(info instanceof LauncherAppWidgetInfo) {
            			final LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) info;
            			AppWidgetProviderInfo appWidgetInfo = AppWidgetManager.getInstance(
            					mXLauncher).getAppWidgetInfo(launcherAppWidgetInfo.appWidgetId);
            			if( appWidgetInfo != null && appWidgetInfo.provider != null){
            				packageName = appWidgetInfo.provider.getPackageName();
            			}
            		}
            		String sourceDir = "";
            		if (packageName != null) {
            			try {
            				sourceDir = pm.getApplicationInfo(packageName, 0).sourceDir;
            			} catch (NameNotFoundException e) {
            				// TODO Auto-generated catch block
            				sourceDir = "";
            			}
            		}
            		if (sourceDir != null && !sourceDir.equals("")) {
            			File file = new File(sourceDir);
            			Uri uri = Uri.fromFile(file);
            			shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            			shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
            			
                		try{
                			mXLauncher.startActivity(shareIntent);
                		}catch(Exception e){
                			Toast.makeText(mXLauncher, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
                		}
            			
            		}
            	}
 				break; 
                /*RK_ID:RK_LESHARE AUT:liuyg1@lenovo.com DATE: 2012-12-21 END */
            	 /*RK_ID:RK_WIDGET_SETTING AUT:liuyg1@lenovo.com DATE: 2013-04-24 START */
            case R.string.quick_action_setting:
            	final LenovoWidgetViewInfo lenovoWidgetViewInfo = (LenovoWidgetViewInfo) info;
            	Log.d("liuyg1","lenovoWidgetViewInfo.className="+lenovoWidgetViewInfo.className);
    			Intent	intent = new Intent();
    			if(lenovoWidgetViewInfo.packageName.contains(WeatherUtilites.THIS_WEATHER_WIDGET)){
    				ComponentName comp = new ComponentName(mXLauncher.getPackageName(),mSettinMap.get(Constants.WEATHERMAGICWIDGETVIEWHELPER));
    				intent.setComponent(comp);
    			}else{
    				ComponentName comp = new ComponentName(mXLauncher.getPackageName(),mSettinMap.get(lenovoWidgetViewInfo.className));
    				intent.setComponent(comp);
    			}
//    			, com.lenovo.launcher2.settings.LeosAppSetting.class);
    			try{
    			mXLauncher.startActivity(intent);
    			}catch (Exception e) {
					// TODO: handle exception
				}
                break;   
                /*RK_ID:RK_WIDGET_SETTING AUT:liuyg1@lenovo.com DATE: 2013-04-24 END */
            default:
                break;
            }

            mXLauncher.dismissQuickActionWindow();
            /* add by zhang@bj.cobellink.com DATA 2012-07-24 S */
//            mXLauncher.getWorkspace().clearChildrenDrawingCacheForSlideEffect();
            /* add by zhang@bj.cobellink.com DATA 2012-07-24 S */
        } // end on-click
    };

    private void completeDrop(Object info, DrawableItem cell) {
        ItemInfo item = (ItemInfo) info;

        if (info instanceof ShortcutInfo) {
            Log.i(XQuickActionWindow.TAG, "this is shortcut info");
            XLauncherModel.deleteItemFromDatabase(mXLauncher, item);
        } else if (info instanceof FolderInfo) {
            // Remove the folder from the workspace and delete the contents from launcher model
            Log.i(XQuickActionWindow.TAG, "this is folder info");
            FolderInfo folderInfo = (FolderInfo) item;
//            mXLauncher.removeFolder(folderInfo);
            XLauncherModel.deleteFolderContentsFromDatabase(mXLauncher, folderInfo);
        } else if (info instanceof LauncherAppWidgetInfo
                || (!(info instanceof LenovoWidgetViewInfo) && (item.spanX > 1 || item.spanY > 1))) {
            // Remove the widget from the workspace
            Log.i(XQuickActionWindow.TAG, "this is launcher app widget info");
//            mXLauncher.removeAppWidget((LauncherAppWidgetInfo) item);
            XLauncherModel.deleteItemFromDatabase(mXLauncher, item);

            final LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) item;
            final LauncherAppWidgetHost appWidgetHost = mXLauncher.getAppWidgetHost();
            if (appWidgetHost != null) {
                // Deleting an application widget ID is a void call but writes to disk before
                // returning to the caller...
                new Thread("deleteAppWidgetId") {
                    public void run() {
                        appWidgetHost.deleteAppWidgetId(launcherAppWidgetInfo.appWidgetId);
                    }
                }.start();

                /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-3-26 S*/
                AppWidgetProviderInfo appWidgetInfo = AppWidgetManager.getInstance(
                    this.getContext()).getAppWidgetInfo(launcherAppWidgetInfo.appWidgetId );
                if( appWidgetInfo != null && appWidgetInfo.provider != null)
                {
                    //Launcher.processReaper(mLauncher, Reaper.REAPER_EVENT_REMOVE_WIDGET, 
                	//	appWidgetInfo.provider.getPackageName(), Reaper.REAPER_NO_INT_VALUE); 
                    Reaper.processReaper( this.getContext(), 
                     	   Reaper.REAPER_EVENT_CATEGORY_WIDGET, 
             			   Reaper.REAPER_EVENT_ACTION_WIDGET_LONGCLICKDELETE,
             			   appWidgetInfo.provider.getPackageName(),
             			   Reaper.REAPER_NO_INT_VALUE );
                }
                /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-3-26 E*/

            } // end if (appWidgetHost != null)
        } else if (info instanceof LenovoWidgetViewInfo) {
            Log.i(XQuickActionWindow.TAG, "this is launcher le widget info");
            XLauncherModel.deleteItemFromDatabase(mXLauncher, item);

            XLauncherModel.sLeosWidgets.remove(item);
//            if(!IsAddedSameLeosWidgets((LenovoWidgetViewInfo)item)){
            Log.d(XQuickActionWindow.TAG, "delete all widget packagename =="
                    + ((LenovoWidgetViewInfo) item).className);
            Intent intent = new Intent("com.lenovo.launcher.LEOS_E2E_WIDGET_DELETE_ALL");
            intent.putExtra("LEOS_E2E_WIDGET_VIEW", ((LenovoWidgetViewInfo) item).className);
            mXLauncher.sendBroadcast(intent);
//            }
                        
            /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2012-3-26 S*/
            Reaper.processReaper( this.getContext(), 
           	   Reaper.REAPER_EVENT_CATEGORY_WIDGET, 
        	   Reaper.REAPER_EVENT_ACTION_WIDGET_LONGCLICKDELETEIDEA,
        	   ((LenovoWidgetViewInfo)item).className,
        	   Reaper.REAPER_NO_INT_VALUE );
            /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2012-3-26 E*/ 
        }

        // remove it on UI, Note : follow Workspace.java (completeDrop)
        if (item.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
            mXLauncher.getHotseat().removeView(cell);
//            mXLauncher.getHotseat().createDockAddIcon(item.cellX, item.cellY);
        } else {
            mXLauncher.getWorkspace().removePagedViewItem(item);
        }
    }

    private void showRenameDialog(final ShortcutInfo si, final XShortcutIconView cell,
            final Object info) {
        // final View textEntryView =
        // mXLauncher.getLayoutInflater().inflate(R.layout.quick_action_rename_layout, null);

        LayoutInflater inflater = (LayoutInflater) mXLauncher
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout renameLayout = (LinearLayout) inflater.inflate(
                R.layout.quick_action_rename_layout, null, false);

        TextView dialogTitle = (TextView) renameLayout.findViewById(R.id.dialog_title);
        dialogTitle.setText(R.string.quick_action_rename_title);
        ImageView dialogIcon = (ImageView) renameLayout.findViewById(R.id.dialog_icon);
        dialogIcon.setImageResource(R.drawable.quickaction_rename);

        CharSequence original = si.replaceTitle != null ? si.replaceTitle : si.title;

        // init edit text
        final EditText rename = (EditText) renameLayout.findViewById(R.id.rename_edit);
        // bug 164952
        /* add by xingqx for fix bug 166626 2012.06.29 s */
        int maxLength = mXLauncher.getResources().getInteger(
                R.integer.config_maxQuickEditTextLength);
        if (original != null && original.length() > maxLength) {
            original = original.subSequence(0, maxLength);
        }
        /* add by xingqx for fix bug 166626 2012.06.29 e */
        rename.setText(original == null ? "" : original);
        rename.setSelection(original == null ? 0 : original.length());

        final LeDialog d = new LeDialog(mXLauncher, R.style.Theme_LeLauncher_Dialog_Shortcut);
        d.setContentView(renameLayout);
        Button cancelBtn = (Button) renameLayout.findViewById(R.id.canceladd);
        cancelBtn.setText(mXLauncher.getString(android.R.string.cancel));
        final CharSequence label = original;
        cancelBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                InputMethodManager ime = ((InputMethodManager) mXLauncher
                        .getSystemService(Context.INPUT_METHOD_SERVICE));
                if (ime != null) {
                    ime.hideSoftInputFromWindow(rename.getWindowToken(), 0);
                }

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                cell.setText(label == null ? "" : label.toString());

                d.dismiss();
            }
        });

        Button finishBtn = (Button) renameLayout.findViewById(R.id.addfinish);
        finishBtn.setText(mXLauncher.getString(android.R.string.ok));
        finishBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//                mXLauncher.destroyCellLayoutHardwareLayers(si.screen);

                InputMethodManager ime = ((InputMethodManager) mXLauncher
                        .getSystemService(Context.INPUT_METHOD_SERVICE));
                if (ime != null) {
                    ime.hideSoftInputFromWindow(rename.getWindowToken(), 0);
                }

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                /* User clicked OK so do some stuff */
                String newLabel = rename.getEditableText().toString();
                cell.setText(newLabel);
                // Bug 152857
                si.replaceTitle = newLabel;

//                mXLauncher.enableCelllayoutHardwareLayers(si.screen);

                // update databases
                ContentValues values = new ContentValues();
                values.put(LauncherSettings.Favorites.TITLE_REPLACE, newLabel);
                XLauncherModel.updateItemInDatabaseHelper(mXLauncher, values, (ItemInfo) info,
                        "mQuickActionClick");
                d.dismiss();
            }
        });
        d.show();

//         show alert dialog
//        Dialog d = new AlertDialog.Builder(mXLauncher, R.style.Theme_LeLauncher_Dialog_Alert)
//        .setIconAttribute(android.R.attr.alertDialogIcon).setTitle(
//                R.string.quick_action_rename_title).setView(textEntryView).setPositiveButton(
//                R.string.profile_save_button, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        mXLauncher.destroyCellLayoutHardwareLayers(si.screen);
//
//                        /* User clicked OK so do some stuff */
//                        String newLabel = rename.getEditableText().toString();
//                        ((BubbleTextView) cell).setText(newLabel);
//                        // Bug 152857
//                        si.replaceTitle = newLabel;
//
//                        mXLauncher.enableCelllayoutHardwareLayers(si.screen);
//
//                        // update databases
//                        ContentValues values = new ContentValues();
//                        values.put(LauncherSettings.Favorites.TITLE_REPLACE, newLabel);
//                        XLauncherModel.updateItemInDatabaseHelper(mXLauncher, values, (ItemInfo) info,
//                                "mQuickActionClick");
//                    }
//                }).setNegativeButton(R.string.add_profile_cancel, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//            }
//        }).show();

        mXLauncher.setPopupChildDlg(d);
    }

    /*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-5-30. S***/
    private void resizeCell(DrawableItem cell, final Object info) {
        if (!(cell instanceof XViewContainer)) {
            return;
        }

        XViewContainer container = (XViewContainer) cell;
        
        // follow drag layer's solution
        final LauncherAppWidgetHostView hostView = (LauncherAppWidgetHostView) container.getParasiteView();
        AppWidgetProviderInfo pinfo = hostView.getAppWidgetInfo();
        if (pinfo == null) {
            return;
        } else {// if (pinfo.resizeMode == AppWidgetProviderInfo.RESIZE_NONE) {
        // if the original widget cannot support resize mode
        // we modify it as 1*1 ( 70*1 - 30 = 40 ) RESIZE_BOTH mode
            pinfo.minResizeWidth = pinfo.minResizeHeight = 40;
            pinfo.resizeMode = AppWidgetProviderInfo.RESIZE_BOTH;
        }
        Log.i("zdx1","******XQuickActionWindowInfo.resizeCell*******"+pinfo.provider);
        final ItemInfo itemInfo = (ItemInfo)cell.getTag() ;
        mXLauncher.getMainView().post(new Runnable() {
        	 public void run() {
             	 Log.i("zdx1","******XQuickActionWindowInfo.resizeCell*******run()");
                 XDragLayer dragLayer = mXLauncher.getDragLayer();
                 dragLayer.addResizeFrame(mXLauncher, itemInfo, hostView);
             }
       });
    }
    /*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-5-30. E***/

    private void showReplaceChooser(final DrawableItem cell, final ItemInfo info) {
        LauncherApplication app = (LauncherApplication) mXLauncher.getApplicationContext();
        final IconCache iconCache = app.getIconCache();

        // fix bug 12552
        mXLauncher.resetAddInfo();

        final ListAdapter mAdapter = new ChangeActionAdapter(mXLauncher,
                R.layout.quick_action_change_item, mXLauncher.getResources().getStringArray(
                        R.array.quick_action_change_choices));

        // Dialog d = new AlertDialog.Builder(mXLauncher,
        // R.style.Theme_LeLauncher_Dialog_Alert).setTitle(R.string.quick_action_change_title).setAdapter(mAdapter,
        // l).show();
        LayoutInflater inflater = (LayoutInflater) mXLauncher
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout quickacitonLayout = (LinearLayout) inflater.inflate(
                R.layout.quickaction_change_icon_list, null, false);
        TextView dialogTitle = (TextView) quickacitonLayout.findViewById(R.id.dialog_title);
        dialogTitle.setText(R.string.quick_action_change_title);
        final Dialog d = new LeDialog(mXLauncher, R.style.Theme_LeLauncher_Dialog_Shortcut);
        ListView list = (ListView) quickacitonLayout.findViewById(R.id.quick_action_list_item);
        list.setAdapter(mAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                case 0: // restore default icon
                    File f = findFileById(info.id);
                    if (f != null && f.exists()) {
                        f.delete();
                    }

                    if (info instanceof ShortcutInfo) {
                        ShortcutInfo sInfo = (ShortcutInfo) info;
                        // save to databases
                        sInfo.setReplaceIcon(null);
                        XLauncherModel.updateItemInDatabase(mXLauncher, sInfo);

                        // ui restore
//                        mXLauncher.destroyCellLayoutHardwareLayers(info.screen);
//                        ((BubbleTextView) cell).applyFromShortcutInfo(sInfo, iconCache);
//                        mXLauncher.enableCelllayoutHardwareLayers(info.screen);
                        if (info.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT
                                && cell instanceof XIconDrawable) {
                            mXLauncher.getHotseat().bindInfo(info, iconCache);
                        } else if (cell instanceof XShortcutIconView) {
                            ((XShortcutIconView) cell).applyFromShortcutInfo(sInfo, iconCache);
                        }
                        Toast.makeText(mXLauncher, R.string.quick_action_replace_success,
                                Toast.LENGTH_SHORT).show();

//                        /** RK_ID: RK_NEWFEATURE_SHOW_MISSED_NUMBER. AUT: yumina DATE: 2012-10-18 S */
//                        mXLauncher.getWorkspace().refreshMissNumForThemeChanged(cell, sInfo);
//                        /** RK_ID: RK_NEWFEATURE_SHOW_MISSED_NUMBER. AUT: yumina DATE: 2012-10-18 S */
//                        /** RK_ID: RK_NEWFEATURE_SHOW_MISSED_NUMBER. AUT: yumina DATE: 2012-10-18 S */
                    } else if (info instanceof FolderInfo) {
                        FolderInfo fInfo = (FolderInfo) info;
                        fInfo.mReplaceIcon = null;

                        XLauncherModel.updateItemInDatabase(mXLauncher, fInfo);
                        ((XFolderIcon) cell).updateFolderPreviewBackground();
                        Toast.makeText(mXLauncher, R.string.quick_action_replace_success,
                                Toast.LENGTH_SHORT).show();
                    }
//                    /* RK_ID: RK_DOCK_ICON . AUT: zhanggx1 . DATE: 2012-07-10 . S */
//                    mXLauncher.getWorkspace().changeIconSize(cell, info.container);
//                    /* RK_ID: RK_DOCK_ICON . AUT: zhanggx1 . DATE: 2012-07-10 . E */
                    break;

                case 1: // theme icon
                    Intent themeIntent = new Intent(SettingsValue.ACTION_LETHEME_IMAGE);
                    themeIntent.putExtra(Constants.EXTRA_FOLDER,
                            info instanceof FolderInfo ? true : false);
                    mXLauncher.startActivityForResult(themeIntent,
                            XLauncher.REQUEST_CODE_CHANGE_THEME_ICON);
                    break;

                case 2: // custom icon
                    // fix bug 165266
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        File path = Environment.getExternalStorageDirectory();
                        StatFs stat = new StatFs(path.getPath());
                        long blockSize = stat.getBlockSize();
                        long availableBlocks = stat.getAvailableBlocks();
                        long size = blockSize * availableBlocks;
                        if (size == 0) {
                            Toast.makeText(mXLauncher, R.string.sdcard_storage_full,
                                    Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                    int width;
                    /*int height = width = mXLauncher.getResources().getDimensionPixelSize(
                            R.dimen.app_icon_size);*/
                    /***RK_ID:RK_CHANGE_ICON_SIZE AUT:zhanglz1@lenovo.com. DATE:2013-01-14.S***/       
                    int iconsize = SettingsValue.getIconSizeValueNew(mXLauncher);
                    int height = width = iconsize;
                	/***RK_ID:RK_CHANGE_ICON_SIZE AUT:zhanglz1@lenovo.com. DATE:2013-01-14.E***/ 
                    if (info instanceof ShortcutInfo) {
                        ShortcutInfo sInfo = (ShortcutInfo) info;
                        Bitmap icon = sInfo.getIcon(iconCache, false);
                        width = icon.getWidth();
                        height = icon.getHeight();
                    } else if (info instanceof FolderInfo) {
                        width = ((XFolderIcon) cell).getPreviewLayoutParamWidth();
                        height = ((XFolderIcon) cell).getPreviewLayoutParamHeight();
                    }

                    Log.i(XQuickActionWindow.TAG, "showReplaceChooser width = " + width
                            + "    height = " + height);
                    mChangeIamge = findFileById(info.id);
                    QuickActionHelper.setFilePath(mChangeIamge.getAbsolutePath());

                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
                    intent.setType(ContentType.IMAGE_UNSPECIFIED);

                    intent.putExtra("crop", "true");
                    intent.putExtra("outputX", width);
                    intent.putExtra("outputY", height);
                    intent.putExtra("aspectX", width);
                    intent.putExtra("aspectY", height);
                    intent.putExtra("scale", true);
                    intent.putExtra("noFaceDetection", true);
                    intent.putExtra("output", Uri.fromFile(mChangeIamge));
                    intent.putExtra("outputFormat", "JPEG");

                    mXLauncher
                            .startActivityForResult(intent, XLauncher.REQUEST_CODE_CHANGE_PICTURE);
                    break;

                default:
                    break;
                }
                if (d != null) {
                    d.dismiss();
                }
            }
        });

        d.setContentView(quickacitonLayout);
        d.show();
       mXLauncher.setPopupChildDlg(d);
    }

    private File findFileById(long id) {
        // create diy folder
        File folder = new File(Environment.getExternalStorageDirectory()
                + Constants.IDEA_FOLDER + QUICK_FOLDER);
//        File folder = new File(Environment.getExternalStorageDirectory() + XLauncherModel.CUSTOM_BIMAP_PATH);
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.i(XQuickActionWindow.TAG, "external storage not mounted");
        } else if (!folder.exists()) {
            boolean res = folder.mkdirs();
            Log.i(XQuickActionWindow.TAG, "mkdirs = " + res);
        }

        return new File(folder, id + Constants.CUSTOM_BIMAP_TYPE);
    }

    public File getChangeImage() {
        return mChangeIamge;
    }

    public View getContentView() {
        return mContentView;
    }

    public int getWidth() {
        return mWidth;
    }

    public XLauncher getContext() {
        return mXLauncher;
    }

    public ImageView getIndicator() {
        return mIndicator;
    }

    public ItemInfo getCellInfo() {
        return mInfo;
    }

    public int getActionCount() {
        return mActionCount;
    }

    public DrawableItem getLongClickView() {
        return mLongClickView;
    }

    protected class ChangeActionAdapter extends ArrayAdapter<String> {
        private final int[] mChangeActionIcons = { R.drawable.change_icon_dialog_launcher_icon,
                R.drawable.change_icon_dialog_themes_icon,
                R.drawable.change_icon_dialog_custom_icon };

        public ChangeActionAdapter(Context context, int resource, String[] strings) {
            super(context, resource, strings);
        }

        ViewHolder holder;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = mXLauncher.getLayoutInflater().inflate(
                        R.layout.quick_action_change_item, null);
                holder = new ViewHolder();
                holder.icon = (ImageView) convertView.findViewById(R.id.quick_change_icon);
                holder.title = (TextView) convertView.findViewById(R.id.quick_change_title);
                convertView.setTag(holder);
            } else {
                // view already defined, retrieve view holder
                holder = (ViewHolder) convertView.getTag();
            }

            holder.title.setText(getItem(position));
            holder.icon.setImageResource(mChangeActionIcons[position]);

            return convertView;

        }

        class ViewHolder {
            ImageView icon;
            TextView title;
        }
    }

}
