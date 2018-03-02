package com.lenovo.launcher.components.XAllAppFace.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.SystemProperties;
import android.provider.CallLog.Calls;
import android.util.Log;

import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;
import com.lenovo.launcher.components.XAllAppFace.XCell;
import com.lenovo.launcher.components.XAllAppFace.XFolder;
import com.lenovo.launcher.components.XAllAppFace.XFolderIcon;
import com.lenovo.launcher.components.XAllAppFace.XHotseat;
import com.lenovo.launcher.components.XAllAppFace.XIconDrawable;
import com.lenovo.launcher.components.XAllAppFace.XIconView;
import com.lenovo.launcher.components.XAllAppFace.XLauncher;
import com.lenovo.launcher.components.XAllAppFace.XPagedViewItem;
import com.lenovo.launcher.components.XAllAppFace.XShortcutIconView;
import com.lenovo.launcher.components.XAllAppFace.slimengine.BaseDrawableGroup;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher2.commoninterface.FolderInfo;
import com.lenovo.launcher2.commoninterface.IconCache;
import com.lenovo.launcher2.commoninterface.ItemInfo;
import com.lenovo.launcher2.commoninterface.LauncherSettings;
import com.lenovo.launcher2.commoninterface.ShortcutInfo;
import com.lenovo.launcher2.customizer.SettingsValue;

public class Utilities {

	public static Utilities newInstance(){
		return new Utilities();
	}

	public synchronized static Bitmap getBitmapFromDrawable(
			final Context context, final int id) {
		final Drawable d = context.getResources().getDrawable(id);
		if (d instanceof BitmapDrawable) {
			return ((BitmapDrawable) (context.getResources().getDrawable(id)))
					.getBitmap();
		}

		return null;
	}

	/*
	 * PK_ID:GET THE REAL SCREEN HEIGHT(SOME DEVICES HAVE NAVIGATION BAR)
	 * AUTH:GECN1 DATE:2012-12-7 S
	 */
	public static int getNavigationBarHeight(Context context) {
		// for navigation bar height
		Resources res = context.getResources();// launcher.getResources();
		boolean hasNavigationBar = res
				.getBoolean(com.android.internal.R.bool.config_showNavigationBar);

		// if we cannot retrieve navigation configuration,
		// maybe there is an error about framework.jar. so change a way to try.
		if (!hasNavigationBar) {
			try {
				Class<?> c = Class.forName("com.android.internal.R$bool");
				Field f = c.getField("config_showNavigationBar");
				int resid = f.getInt(null);

				hasNavigationBar = context.getResources().getBoolean(resid);

			} catch (Exception e) {
				Log.v("Xlauncher","=== get field error");
			}
}

		// Allow a system property to override this. Used by the emulator.
		// See also hasNavigationBar().
		String navBarOverride = SystemProperties.get("qemu.hw.mainkeys");
		if (!"".equals(navBarOverride)) {
			if (navBarOverride.equals("1")) {
				hasNavigationBar = false;
            } else if (navBarOverride.equals("0")) {
				hasNavigationBar = true;
            }
		}
		return (hasNavigationBar ? res
				.getDimensionPixelSize(com.android.internal.R.dimen.navigation_bar_height)
				: 0);
	}
	public static void refreshItem(Context context, DrawableItem target, ItemInfo info,
            IconCache iconCache, boolean bitmap) {
        if (context == null || target == null || info == null) {
            return;
        }

        /*RK_ID:RK_SD_APPS zhangdxa 2013-4-15. S***/
        if (info.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION ||
        	info.itemType == LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT	) {
            final ShortcutInfo sInfo = (ShortcutInfo) info;
            final XShortcutIconView view = (XShortcutIconView) target;
            if (bitmap)
                sInfo.setIcon(null);
            view.applyFromShortcutInfo(sInfo, iconCache);
        }
        /*RK_ID:RK_SD_APPS zhangdxa 2013-4-15. E***/
        else if (info.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT) {
            final ShortcutInfo sInfo = (ShortcutInfo) info;
            final XShortcutIconView view = (XShortcutIconView) target;
            if (bitmap) {
                retrieveIcon(context, sInfo);
            }
            view.applyFromShortcutInfo(sInfo, iconCache);
        }
        if (info instanceof FolderInfo) {
            final XFolderIcon view = (XFolderIcon) target;
            XFolder folder = view.mFolder;

			folder.refreshIconCache(iconCache, bitmap);
			folder.invalidate();
			
			view.changeFolderIconThemes();
			view.invalidate();
        }
    		
    }
	public static Bitmap retrieveIcon(Context context, ShortcutInfo info) {
        Bitmap icon = null;

        if (info.customIcon) {
            // do nothing
        } else {
            LauncherApplication la = (LauncherApplication) context.getApplicationContext();
            // we need to find it from databases.
            final android.database.Cursor c = context.getContentResolver().query(
                    LauncherSettings.Favorites.getContentUri(info.id, false), null, null, null,
                    null);

            if (c != null && c.moveToFirst()) {
                String packageName = c.getString(c
                        .getColumnIndex(LauncherSettings.Favorites.ICON_PACKAGE));
                String resourceName = c.getString(c
                        .getColumnIndex(LauncherSettings.Favorites.ICON_RESOURCE));

                PackageManager packageManager = context.getPackageManager();
                // the resource
                try {
                    Resources resources = packageManager.getResourcesForApplication(packageName);
                    if (resources != null) {
                        final int id = resources.getIdentifier(resourceName, null, null);
                        icon = la.mLauncherContext.getIconBitmap(resources, id, packageName);
//                        icon = Utilities.createIconBitmap(mIconCache.getFullResIcon(resources, id), this, packageName);
                    }
                } catch (Exception e) {
                    // drop this. we have other places to look for icons
                	Log.v("XLauncher","=====get icon bitmap error");
                }

                // the db
                if (icon == null) {
                    int iconIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.ICON);
                    icon = la.getModel().getIconFromCursor(c, iconIndex, context);
                }

                // the fallback icon
                if (icon == null) {
                    icon = la.getModel().getFallbackIcon();
                    info.usingFallbackIcon = true;
                }

                c.close();
            }
            info.setIcon(icon);
        }

        return icon;
    }
	
	// convert array by orientation
	public static final byte LEFT = 0;
	public static final byte RIGHT = 1;
	public static final byte TOP = 2;
	public static final byte BOTTOM = 3;
	public static final byte LEFT_TOP = 4;
	public static final byte RIGHT_TOP = 5;
	public static final byte LEFT_BOTTOM = 6;
	public static final byte RIGHT_BOTTOM = 7;

	public boolean convert(int[] array, byte orient, int spanX, int spanY) {
		if (orient > RIGHT_BOTTOM || 0 > orient || array == null) {
			return false;
		}

		int n = spanX;
		int m = spanY;
		
//		android.util.Log.i( "orderX", "\nshow time ................." );
		
		Bitmap bmp = Bitmap.createBitmap(n, m, Bitmap.Config.ARGB_8888);
		for(int i = 0 ; i< array.length; i ++){
//			android.util.Log.i( "orderX",  "" + array[i]);			
			bmp.setPixel( i % n, i / n, Color.argb(array[i], 0, 0, 0));
		}
		
		Matrix matrix = new Matrix();
		switch (orient) {
		case LEFT:
			break;
		case RIGHT:
			matrix.setRotate(-90f);
			break;
		case TOP:
			break;
		case BOTTOM:
			matrix.setRotate(180f);
			break;
		case LEFT_TOP:
			break;
		case RIGHT_TOP:
			matrix.setRotate(-90f);
			break;
		case LEFT_BOTTOM:
			matrix.setRotate(90f);
			break;
		case RIGHT_BOTTOM:
			Matrix mt = new Matrix();
			Camera ca = new Camera();
			ca.rotateX( -180f );
			ca.rotateY( -180f );
			ca.translate( - n,  - m, 0);
			ca.getMatrix(mt);
			matrix.postConcat(mt);
			break;
		default:
			bmp.recycle();
			return false;
		}
		
		bmp = Bitmap.createBitmap(bmp, 0, 0, Math.abs(n), Math.abs(m), matrix, false);
		int count = 0;
		for(int i = 0; i< bmp.getWidth(); i ++){
			for(int j = 0; j < bmp.getHeight() ; j ++){
				array[ count ] = Color.alpha(bmp.getPixel(i, j));
				count ++;
			}
		}
		
		bmp.recycle();
		
//		android.util.Log.i( "orderX", "======================\n" );
		for(int i = 0 ; i< array.length; i ++){
//			android.util.Log.i( "orderX",  "" + array[i]);				
		}

		return true;
	}
	public static void iconSizeChange(DrawableItem iconDrawable,int iconsize){
		if(iconDrawable == null || iconDrawable.localRect == null) return;
		float px = iconDrawable.localRect.centerX();
		float py = iconDrawable.localRect.centerY();
		final float scale = (float) (iconsize / iconDrawable
				.getHeight());
		/*Matrix iconSizeMatrix = iconDrawable
				.getIconSizeMatrix();
		iconSizeMatrix.reset();
		iconSizeMatrix.setScale(scale, scale, px, py);
		iconDrawable.setIconSizeMatrix(iconSizeMatrix);
		*/
		Matrix m = iconDrawable.getMatrix();
		m.reset();
    	m.postScale(scale, scale,  px, py);
    	iconDrawable.updateMatrix();
	}

	public static int mIconSize = 0; 
	public static void updateIconSizeinWorkspace(HashMap<Long, XPagedViewItem> mItemIDMap,
			int iconsize) {
		if (iconsize == mIconSize)
		{
			return;
		}
		mIconSize = iconsize;		
		if (!mItemIDMap.isEmpty()) {
			Iterator<XPagedViewItem> items = mItemIDMap.values().iterator();
			while (items.hasNext()) {
				try {
				XPagedViewItem pagedViewItem = items.next();
				ItemInfo itemInfo = pagedViewItem.getInfo();
				int spanX = itemInfo.spanX;
				int spanY = itemInfo.spanY;
				if (spanX == 1 && spanY == 1) {
					// int itemType = itemInfo.itemType;
					XCell cell = pagedViewItem.getCells()[0];
					if (!cell.isEmpty()) {
	                    DrawableItem drawableTarget = pagedViewItem.getDrawingTarget();
	                    if (drawableTarget == null
	                            || (!(drawableTarget instanceof XShortcutIconView) && !(drawableTarget instanceof XFolderIcon))) {
	                        continue;
	                    }
						if (drawableTarget instanceof XShortcutIconView) {
							final XShortcutIconView view = (XShortcutIconView) drawableTarget;
							XIconDrawable iconDrawable = view
									.getIconDrawable();
							iconSizeChange(iconDrawable,iconsize);
						}
						
	                    if (drawableTarget instanceof XFolderIcon) {
	                    	//把folder 缩小
	                    	final XFolderIcon view = (XFolderIcon) drawableTarget;
	                    	view.resize(view.localRect);
//	                    	DrawableItem iconDrawableFolderIcon = view
//									.getIconDrawable();
//							iconSizeChange(iconDrawableFolderIcon,iconsize);
							//把folder里面的icon缩小
							XFolder folder =  view.mFolder;
							updateIconSizeinWorkspace(folder.getItemIDMap(), iconsize);
	                    }
	                    drawableTarget.invalidate();
					}
				}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
	}
	public static void updateIconSizeinApplist(BaseDrawableGroup appContentView,int iconsize) {
        if (appContentView.getChildCount() > 0) {
          for (int i = 0; i < appContentView.getChildCount(); i++) {
              DrawableItem item = appContentView.getChildAt(i);
              if (item instanceof XIconView) {
                  XIconView iconView = (XIconView) item;
                  XIconDrawable iconDrawable = iconView.getIconDrawable();
				iconSizeChange(iconDrawable,iconsize);
              }
          }
      }
	}
	
    /** RK_ID: RK_NEWFEATURE_SHOW_MISSED_NUMBER. AUT: zhanglz1 DATE: 2013-05-06 S */
	public static int getMissedMessageNum(Context context){
        return findNewSmsCount(context)+findNewMmsCount(context);
    }
    private static int findNewSmsCount(Context context) {
        int newSmsCount = 0;
        Cursor csr = null;
        try {
            csr = context.getContentResolver().query(
                                        Uri.parse("content://sms/inbox"), new String[] { "_id" },
                                        "read = 0", null, null);
            if (csr != null) {
                newSmsCount = csr.getCount(); // 未读短信数目
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (csr != null) {
                csr.close();
            }
        }
        return newSmsCount;
    }
    
    private static int findNewMmsCount(Context context) {
        int newMmsCount = 0;
        Cursor csr = null;
        try {
        	 csr = context.getContentResolver().query(
                     Uri.parse("content://mms/inbox"), new String[] { "_id" },
                     "read = 0 and m_type != 128 and m_type != 129 and m_type != 134 and m_type!=136 and m_type!=135", null, null);

            if (csr != null) {
                newMmsCount = csr.getCount();// 未读彩信数目
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (csr != null) {
                 csr.close();
            }
        }
        return newMmsCount;
    }
    public static String LEVOICE_CALL_PACKAGENAME = "com.android.contacts.activities.DialtactsActivity";//打电话
    public static String LEVOICE_CALL_PACKAGENAME_OTHER = "com.lenovo.ideafriend.alias.DialtactsActivity";
    public static String LEVOICE_CALL_PACKAGENAME_OTHERS = "com.android.contacts.DialtactsActivity";
    public static String LEVOICE_CALL_PACKAGENAME_OTHERSS = "com.android.contacts.activities.DialpadActivity";
    public static String LEVOICE_CALL_PACKAGENAME_OTHERSSS = "com.android.dialer.DialtactsActivity";
    
    public static String LEVOICE_SMS_PACKAGENAME = "com.android.mms.ui";//发短信 
    public static String LEVOICE_SMS_PACKAGENAME_OTHER = "com.lenovo.ideafriend.alias.MmsActivity";
    public static String LEVOICE_SMS_PACKAGENAME_SHORTCUT_ACTION = "android.intent.action.SENDTO";//发短信 

    public static String LEVOICE_PEOPLE_PACKAGENAME = "com.android.contacts.activities.PeopleActivity";//联系人
    public static String LEVOICE_PEOPLE_PACKAGENAME_OTHER = "com.lenovo.ideafriend.alias.PeopleActivity";
    public static String LEVOICE_BROWSER_PACKAGENAME = "com.android.browser";//浏览器
    public static String LEVOICE_APP_PACKAGENAME = "com.lenovo.levoice.action.VOICE_RECOGNIZE_APP";//打开应用
    public static String DEFAULT_HOTSEAT_CALL_STRING = "android.intent.action.DIAL";//打电话
    public static String DEFAULT_HOTSEAT_SMS_STRING = "vnd.android-dir/mms-sms";//发短信 
    public static String DEFAULT_HOTSEAT_PEOPLE_STRING = "com.android.contacts/contacts";//联系人
    public static String DEFAULT_HOTSEAT_BROWSER_STRING = "com.android.browser";//浏览器
    public static int getMissedCallNum(Context context) {
        int newCallNumber = 0;
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(Calls.CONTENT_URI,
                                        new String[] { Calls.NEW },
                                        "type=" + Calls.MISSED_TYPE + " AND new=1", null, null);
            if (cursor != null) {
                newCallNumber = cursor.getCount();
            }
            return newCallNumber;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

	public static int getShowMissedNum(ItemInfo info, int type,int missedNum) {
		int showMissedNum = 0;
		if(missedNum <1) return 0;
		if(info == null ) return 0;
		if (info instanceof ShortcutInfo) {
			final Intent intent = ((ShortcutInfo) info).intent;
			if(intent == null) return 0;
			final ComponentName name = intent.getComponent();
			String className = null;
			if (name != null) {
				className = name.getClassName();
			} else if (intent!=null){
				className = intent.toString();
			}
			if (className == null)
				return 0;
			if (type == HAS_NEW_CALL) {
				if (isCallIcon(className)) {
					showMissedNum = missedNum;
				}else{
					showMissedNum = 0;
				}
			} else if (type == HAS_NEW_MSG) {
				if (isSmsIcon(className)) {
					showMissedNum = missedNum;
				}else{
					showMissedNum = 0;
				}
				if(intent!=null && intent.getAction() !=null && intent.getAction().contains(LEVOICE_SMS_PACKAGENAME_SHORTCUT_ACTION))
					showMissedNum = 0;
			}
		}
		return showMissedNum;
	}
	private static boolean isSmsIcon(String className){
		if(className == null){
			return false;
		}
		if (className
				.contains(LEVOICE_SMS_PACKAGENAME)
				|| className
						.contains(LEVOICE_SMS_PACKAGENAME_OTHER)
				|| (className
						.contains(DEFAULT_HOTSEAT_SMS_STRING))){
			return true;
		}else{
			return false;
		}
	}
	private static boolean isCallIcon(String className){
		if(className == null){
			return false;
		}
		if (className
				.contains(LEVOICE_CALL_PACKAGENAME)
				|| className
						.contains(LEVOICE_CALL_PACKAGENAME_OTHER)
				|| className
						.contains(DEFAULT_HOTSEAT_CALL_STRING)
				|| className
						.contains(LEVOICE_CALL_PACKAGENAME_OTHERS)
				|| className
						.contains(LEVOICE_CALL_PACKAGENAME_OTHERSS)		
				||className
						.contains(LEVOICE_CALL_PACKAGENAME_OTHERSSS)){
			return true;
		}else{
			return false;
		}
	}
	public static final int HAS_NEW_CALL = 1;
	public static final int HAS_NEW_MSG = 0;
	public static int[] missedNum = {-1,-1};// first message,second call
	public static void getMissedNumFirsttime(Context context){
		if(missedNum[HAS_NEW_MSG] == -1){
        	missedNum[HAS_NEW_MSG] = getMissedMessageNum(context);
        }
        if(missedNum[HAS_NEW_CALL] == -1){
        	missedNum[HAS_NEW_CALL] = getMissedCallNum(context);
        }
	}
    /** RK_ID: RK_NEWFEATURE_SHOW_MISSED_NUMBER. AUT: zhanglz1 DATE: 2013-05-06 E */
	public static void saveBitmap(Bitmap bitmap,String name) throws IOException
    {
            /*File file = new File("/sdcard/picture/"+name+".png");
            file.createNewFile();
            FileOutputStream out;
            try{
                    out = new FileOutputStream(file);
                    if(bitmap.compress(Bitmap.CompressFormat.PNG, 70, out))
                    {
                            out.flush();
                            out.close();
                    }
            }
            catch (FileNotFoundException e)
            {
                    e.printStackTrace();
            }
            catch (IOException e)
            {
                    e.printStackTrace();
            }*/
    }

    /*** RK_ID: ANIM_FOLDER. AUT: zhaoxy . DATE: 2013-07-18 . START ***/
    public static float formatInt2Float(int input) {
        return input * .1f / (int) Math.pow(10, (int) Math.log10(input));
    }
    /*** RK_ID: ANIM_FOLDER. AUT: zhaoxy . DATE: 2013-07-18 . END ***/
    
    public static boolean isFHD() {
		String deviceModel = android.os.Build.MODEL;
		if (deviceModel.contains( "B8080" )) {
			return true;
		} else {
			return false;
		}
		
	}
}
