/*
 * Copyright (C) 2012
 *
 * AUT: liuli1 DATE: 2012-02-22
 * settings value set and get
 */
package com.lenovo.launcher2.customizer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;
import android.os.SystemProperties;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;
import com.lenovo.launcher.components.XAllAppFace.XLauncher;

public class SettingsValue {
    public static final String ACTION_WORKSPACE_EFFECT = "com.lenovo.launcher.change_effect";
    public static final String ACTION_WORKSPACE_APPS_CATEGORY = "com.lenovo.launcher.appscategory";
    public static final String KEY_WALLPAPER_SETTING_RESULTCODE = "resul_code";
    public static final String ACTION_SCENE_APPLY = "com.lenovo.launcher.Intent.ACTION_SCENE_APPLY";
    public static final String ACTION_SCENE_APPLY_FINISHED = "com.lenovo.launcher.Intent.ACTION_SCENE_APPLY_FINISHED";
    public static final String ACTION_SCENE_BACKUP = "com.lenovo.launcher.Intent.ACTION_SCENE_BACKUP";
    public static final String ACTION_SCENE_BACKUP_FINISHED = "com.lenovo.launcher.Intent.ACTION_SCENE_BACKUP_FINISHED";
    public static final String ACTION_LETHEME_IMAGE = "com.lenovo.launcher.action.LETHEME_IMAGE";
    public static final String ACTION_REFRESH_MNG_VIEW = "com.lenovo.launcher.action.ACTION_REFRESH_MNG_VIEW";
    private static String INBUILD_THME_POSTFIX="_lib";
    public static final String ACTION_REMOVE_LOADING_DIALOG = "com.lenovo.launcher.Intent.ACIONT_REMOVE_LOADING_DIALOG";    


    public static final String KEY_IS_DEFAULT_PROFILE = "isdefault";

    public enum SettingsType {
        FOLDER_STYLE  (0),
        HOTSEAT_STYLE (1);
        
        final int nativeInt;
        SettingsType(int ni) {
            nativeInt = ni;
        }
    }
    



    public static Runnable resetToMySettings(final Activity context) {
        return new Runnable() {

            @Override
            public void run() {
                SharedPreferences.Editor shared_editor = PreferenceManager
                        .getDefaultSharedPreferences(context).edit();
                shared_editor.putString(PREF_APPLIST_ENTER, "ALPHA");
                shared_editor.putString(PREF_APPLIST_SLIDE, "CYLINDER");
                shared_editor.putString(PREF_WORKSPACE_SLIDE, "NONE");
                shared_editor.putBoolean(PREF_WORKSPACE_LOOP, false);
                shared_editor.commit();

                sPrefAppListEnter = "ALPHA";
                sPrefAppListSlide = "CYLINDER";
                sPrefWorkspaceSlide = "NONE";
                sWorkspaceSlideLoop = false;

                if (context instanceof XLauncher) {
                    ((XLauncher)context).updateAppSlideValue();
                } else if (context instanceof XLauncher) {
                	((XLauncher)context).updateAppSlideValue();
                }
                Intent i = new Intent(ACTION_WORKSPACE_LOOP);
                i.putExtra(EXTRA_WORKSPACE_IS_LOOP, sWorkspaceSlideLoop);
                context.sendBroadcast(i);

                SharedPreferences prefs = context.getSharedPreferences(
                        "com.lenovo.launcher2.prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(KEY_RESET_SETTINGS, true).putBoolean(
                        ConstantAdapter.EXCLUDED_SETTING_KEY, true);
                editor.commit();
            }
        };
    }

    
    

 
    public static void enableLeVoice(boolean enable) {
    	mEnableLeVoice = enable;
        sIsLeVoiceEnablerChanged = true;
    }
    
	/***RK_ID:RK_SQURE_ICONSTYLE AUT:zhanglz1@lenovo.com DATE: 2013-04-12 ***/ 
	public final static int[] SQUARE_BG = new int[]{2,6};
    
    private static String sPrefCurrentTheme;
    public static final String PREF_THEME = "pref_theme";
    public static final String PREF_THEME_DEFAULT = "pref_theme_default";    
    public static final String EXTRA_THEME_VALUE = "theme_value";
    public static final String EXTRA_DEFAULT_THEME = "default_theme";
    public static final String ACTION_SHOW_THEME_DLG = "action.show_theme_dlg";
    public static final String ACTION_LETHEME_APPLY = "action.letheme.apply";
    public static final String ACTION_LETHEME_APPLING = "action.letheme.appling";
    public static final String ACTION_APPLY_THEME_DONE = "com.lenovo.launcher.Intent.APPLY_THEME_DONE";
    public static final String ACTION_LETHEME_SHOW_PROGRESS_DIALOG = "action.letheme.show_progress_dialog";
    public static final String ACTION_LETHEME_LAUNCH = "com.lenovo.launcher.action.THEME_SETTING";
    public static final String ACTION_REFRESH_LOTUS = "com.lenovo.launcher.action.REFRESH_LOTUS";
    public static final String KEY_SET_THEME = "lenovo_launcher_theme";
    public static final String PREF_FIRST_START = "pref_first_start";
    public static final String DEFAULT_WALLPAPER_NAME = "default_wallpaper";
    public static final String ACTION_LAUNCHER_THEME = "action_themecenter_themechange_launcher";
    public static final String ACTION_LAUNCHER_THEME_NAME = "theme_packagename";
    public static final String DEFAULT_THEME_VERSION = "v3.x";
    public static final String THEME_VERSION_CODE = "theme_version_code";
    public static final String ACTION_LESEARCH_LAUNCH = "com.lenovo.launcher.action.SEARCH_SHORTCUT"; 
    //test by dining
    public static final String ACTION_DESKTOPSETTING_LAUNCH = "com.lenovo.launcher.action.DESKTOP_SETTING";
    public static final String ACTION_DESKTOPSETTING_EFFECT_LAUNCH = "com.lenovo.launcher.action.DESKTOP_SETTING_EFFECT";
    public static final String ACTION_DESKTOPSETTING_PERSONAL_LAUNCH = "com.lenovo.launcher.action.DESKTOP_SETTING_PERSONAL";
    
    
    public static final int THEME_SETTING_PREVIEW = 0;
    public static final int THEME_SETTING_STYLE_SINGLE = 1;
    
    public static final int OP_NONE = -1;
    public static final int OP_ADD = 0;
    public static final int OP_UPDATE = 1;
    public static final int OP_REMOVE = 2;
    //change by xingqx old is com.lenovo.launcher.theme.
    public static final String THEME_PACKAGE_NAME_PREF = "com.lenovo.launcher";
    public static final String LAUNCHER_PACKAGE_NAME_PREF = "com.lenovo.launcher";
    
    public static final String PERFERENCE_NAME ="com.lenovo.launcher_preferences";
    //test by dining 2013-05-24 
    //add the qigame.lockscreen
    public static final String THEME_PACKAGE_QIGAMELOCKSCREEN_PREF = "com.qigame.lock";
    
    // default theme
    public static final String THEME_PACKAGE_CATEGORY = "android.intent.category.THEMESKIN";
    private static String sPrefDefaultTheme = null;
    private static String sPrefDefaultThemeConfig = null;
    
    // pref_dync_wallpaper_change
    public static final String PREF_DYNC_THEME_CHANGE = "pref_dync_theme_change";

    // profile
    private static String sPrefCurrentProfile;
    public static final String PREFS_FILE_NAME = "com.lenovo.launcher2.profiles";
    public static final String PREF_PROFILE = "pref_profile";

    // profile - bundle keys
    public static final String KEY_RESULT_MSG = "result_msg";
    public static final String KEY_BITMAPS = "bitmaps";
    public static final String KEY_TYPE = "oper_type";
    public static final String KEY_APPLY_FOLLOWING = "apply_following";

    // icon style index
    private static int sIconStyle = Integer.MIN_VALUE;
    public static final String PREF_ICON_BG_STYLE = "pref_icon_bg_style";
    public static final String ACTION_ICON_STYLE_INDEX_CHANGED = "com.lenovo.action.ICON_STYLE_INDEX_CHANGED";
    public static final String THEME_ICON_BG_NAME = "theme_appbg";
    public static final String THEME_ICON_FG_NAME = "theme_appfg";
    public static final String THEME_ICON_MASK_NAME = "theme_appmask";
    public static final String THEME_ICON_BG_CONFIG = "config_theme_icon_bg_index";
    public static final int THEME_ICON_BG_INDEX = -2;
    public static final int DEFAULT_ICON_BG_INDEX = 6;
    private static Bitmap[] mThemeBgBitmap = new Bitmap[]{null, null, null};
    
    /*** AUT:zhaoxy . DATE:2012-03-03 . START***/
    public static final String ACTION_ONFINISH_ICON_STYLE_CHANGE = "action.iconstylesettings.onfinish";
    /*** AUT:zhaoxy . DATE:2012-03-03 . END***/
    public static final String ACTION_FOR_LAUNCHER_FINISH = "com.lenovo.action.launcher_finish_refresh";

    /***RK_ID:RK_CHANGE_ICON_SIZE AUT:zhanglz1@lenovo.com. DATE:2013-01-14.S***/       
 // key - icon size
    public static final String PREF_ICON_SIZE = "pref_icon_size";
    public static final String PREF_ICON_SIZE_NEW = "pref_icon_size_new";

    private static String sPrefIconSize;
    private static String sPrefIconSizeNew;

	private static boolean sIconSizeChanged = false;
    public static final String ACTION_ICON_SIZE_CHANGED = "com.lenovo.action.ICON_SIZE_CHANGED";
    /***RK_ID:RK_CHANGE_ICON_SIZE AUT:zhanglz1@lenovo.com. DATE:2013-01-14.E***/       
    
    // key - text size
    public static final String PREF_ICON_TEXT_SIZE = "pref_icon_text_size";
    private static String sPrefIconTextSize;
    public static final String ACTION_TEXT_SIZE_CHANGED = "com.lenovo.action.TEXT_SIZE_CHANGED";
	
    /***RK_ID:RK_FONT AUT:zhanglz1@lenovo.com. DATE: 2013-02-21 S***/        
    //key - text font
    public static String ACTION_TEXT_FONT_CHANGED= "com.lenovo.action.TEXT_FONT_CHANGED";
    public static final String PREF_TEXT_FONT = "pref_icon_text_font";
    private static String sPrefTextFont;
	private static String sFontEntry;
    public static final String PREF_FONT_STYLE = "pref_font_style";
    /***RK_ID:RK_FONT AUT:zhanglz1@lenovo.com. DATE: 2013-02-21 E***/        

    // key - text color
    public static final String PREF_ICON_TEXT_STYLE = "pref_icon_text_style";
    private static int sPrefIconTextStyle = Integer.MIN_VALUE;
    
    // key - edit style
    public static final String PREF_APPLIST_EDIT = "pref_icon_edit_style";
    private static String sPrefAppListEdit;
    public static final String ACTION_APP_EDIT_CHANGED = "com.lenovo.action.APP_EDIT_CHANGED";
    
    // key - folder style
    public static final String PREF_FOLDER_STYLE = "pref_icon_style";
    private static String sPrefIconStyle;
    
    //key - backup and restore
    /*RK_ID: RK_BACKUP_RESTROE . AUT:xujing3@lenovo.com DATE: 2013-6-9 START*/    
    public static final String PREF_LOCAL_BACKUP = "pref_local_backup";
    public static final String PREF_CLOUD_BACKUP = "pref_cloud_backup";
    public static final String PREF_LOCAL_RESTORE = "pref_local_restore";
    public static final String PREF_CLOUD_RESTORE = "pref_cloud_restore";
    /*RK_ID: RK_BACKUP_RESTROE . AUT:xujing3@lenovo.com DATE: 2013-6-9 END*/   
    
    // keys - app list cell Y
    public static final String PREF_APP_CELLY = "pref_applist_y_count";
    private static String sAllAppCellY;
    public static final String ACTION_CELLY_CHANGED = "com.lenovo.action.CELLY_CHANGED";

    public static final String CELLX_COUNT_OF_ALLAPP = "cellx_count_of_allapp";
    public static final String CELLY_COUNT_OF_ALLAPP = "celly_count_of_allapp";
/*RK_ID: RK_SCREEN_CELLCOUNT . AUT:liuyg1@lenovo.com DATE: 2012-11-13 START*/      
    public static final String ACTION_SCREEN_CELLCOUNT_CHANGED = "com.lenovo.action.SCREEN_CELLCOUNT_CHANGED";
 	 public static final String PREF_SCREEN_CELLCOUNT = "pref_key_screen_cellcount";
    private static String sScreenCellcount;
/*RK_ID: RK_SCREEN_CELLCOUNT . AUT:liuyg1@lenovo.com DATE: 2012-11-13 END*/   
    // key - indicator
    public static final String PREF_HOTSEAT_STYLE = "pref_hotseat_style";
    private static String sPrefHotseatStyle;
    public static final String ACTION_INDICATOR_CHANGED = "com.lenovo.action.INDICATOR_CHANGED";

    /*RK_SEMI_TRANSPARNET dining@lenovo.com 2012-10-16 S*/
    public static final String APPBG_TRANSLUCENT_ENABLE_CONFIG = "config_appbg_translucent_enable";
    public static final String PREF_APPLIST_BGSEMITARNSPARENT = "pref_applist_bgTransparent";
    public static final String PREF_TARNSPARENT_VALUE = "pref_transparent_value";
    private static int sPrefTransparent = Integer.MIN_VALUE;
    /*RK_SEMI_TRANSPARNET dining@lenovo.com 2012-10-16 E*/
    
    //key - use default theme icons,default value is true
    /*RK_SEMI_TRANSPARNET dining@lenovo.com 2012-11-28 S*/
    public static final String PREF_USE_DEFAULTTHEME_ICON = "pref_use_defaulttheme_icon";
    
    /*RK_SEMI_TRANSPARNET dining@lenovo.com 2012-11-28 E*/
    
    /*RK_default_icon_style dining@lenovo.com 2013-9-12 S*/
    public static final String PREF_USE_DEFAULT_ICON_STYLE = "pref_use_default_iconstyle";
    /*RK_default_icon_style dining@lenovo.com 2013-9-12 E*/
    
    /*RK_GESTURE_SETTING liuyg1@lenovo.com 2013-5-30 S*/
    public static final String PREF_SCROLL_UP_GESTURE = "pref_scroll_up_guesture";
    private static String sScrollUpGuesture;
    public static final String ACTION_SCROLL_UP_GESTURE_CHANGED = "com.lenovo.action.SCROLL_UP_GESTURE_CHANGED";
    public static final String PREF_SCROLL_DOWN_GESTURE = "pref_scroll_down_guesture";
    private static String sScrollDownGuesture;
    public static final String ACTION_SCROLL_DOWN_GESTURE_CHANGED = "com.lenovo.action.SCROLL_DOWN_GESTURE_CHANGED";
    public static final String PREF_DOUBLE_CLICK_GESTURE = "pref_double_click_guesture";
    private static String sDoubleClickGuesture;
    public static final String ACTION_DOUBLE_CLICK_GESTURE_CHANGED = "com.lenovo.action.DOUBLE_CLICK_GESTURE_CHANGED";
    /*RK_GESTURE_SETTING liuyg1@lenovo.com 2013-5-30 E*/  
// values - effects
    private static String sPrefWorkspaceSlide;
    private static boolean sWorkspaceSlideLoop;
    private static String sPrefAppListEnter;
    private static String sPrefAppListSlide;

    // keys - effects
    public static final String PREF_WORKSPACE_SLIDE = "pref_workspace_slide_effect";
    public static final String PREF_WORKSPACE_SLIDE_LELIST = "pref_workspace_slide_effect_lelist";
    public static final String PREF_WORKSPACE_LOOP = "pref_workspace_loop";
    public static final String PREF_APPLIST_ENTER = "pref_applist_enter_effect";
    public static final String PREF_APPLIST_SLIDE = "pref_applist_slide_effect";
    public static final String PREF_LOAD_WIDGET_PREVIEW = "pref_load_widget_preview";

    // intents - effects
    public static final String ACTION_WORKSPACE_LOOP = "com.lenovo.action.workspace_loop";
    public static final String EXTRA_WORKSPACE_IS_LOOP = "com.lenovo.extra.workspace_is_loop";

    // values - load widget preview
    private static boolean mLoadWidgetPreview = true;// whether load widget preivew in AllAppList
    private static boolean sIsLoadWidgetSync = false;

    // intent's action - effects
    public static final String ACTION_LOAD_WIDGET_SETTINGS = "com.lenovo.action.load_widget_settings";

    // keys - theme detail
    public static final String PREF_WALLPAPER = "pref_wallpaper";
//    public static final String PREF_APPS_WALLPAPER = "pref_applist_wallpaper";
//    public static final String PREF_RESET_APPS_WALLPAPER = "pref_reset_applist_wallpaper";
    public static final String PREF_WALLPAPER_SLIDE = "pref_wallpaper_slide";
    
    // intent - wallpaper
    public static final String ACTION_START_WALLPAPER = "com.lenovo.action.START_WALLPAPER";
    public static final String ACTION_START_APPS_WALLPAPER = "com.lenovo.action.START_APPS_WALLPAPER";
    public static final String ACTION_RESET_APPS_WALLPAPER = "com.lenovo.action.RESET_APPS_WALLPAPER";
    public static final String ACTION_ONFINISH_APPS_WALLPAPER = "com.lenovo.action.ONFINISH_APPS_WALLPAPER";
    
    // variable - theme detail
    private static boolean mEnableWallpaperSlide = true;// whether wallpapers move follow workspace
    private static boolean sIsWallpaperSync = false;
    
    // senior settings
    public static final String ALWAYS_ALIVE = "always_alive";
    public static final String KEY_ALWAYS_ALIVE_ONOFF = "pref_always_alive_on";
    public static final String PREF_WEB_SITE = "pref_web_site";
    public static final String PREF_DATA_ACQU_ENABLER = "pref_data_acquisition";
    public static final String PREF_NETWORK_ENABLER = "pref_network_enabler";
    public static final String PREF_REAPER = "pref_reaper";
    public static final String PREF_SEND_EXCEPTION = "pref_send_exception";
    /*RK_ID: RK_LELAUNCHER_MAGICDOWNLOAD. AUT:zhangdxa. DATE: 2012-09-21. S*/
    public static final String PREF_AKEY_INSTALL_SET = "pref_akey_install_set";
    public static final String PREF_AKEY_INSTALL_IGNORE = "pref_akey_install_ignore";
    /*RK_ID: RK_LELAUNCHER_MAGICDOWNLOAD. AUT:zhangdxa. DATE: 2012-09-21. E*/
    
    /*RK_MAGICGESTRUE_LEVOICE zhanglz1@lenovo.com 2012-11-21 S*/
    public static final String PREF_AKEY_ENABLE_LEVOICE = "pref_enable_levoice";
    private static boolean mEnableLeVoice = true;//
	private static boolean sIsLeVoiceEnablerChanged = false;
    public static final String ACTION_LEVOICE_ENABLE_CHANGE = "com.lenovo.action.LEVOICE_ENABLE_CHANGE";

    /*RK_MAGICGESTRUE_LEVOICE zhanglz1@lenovo.com 2012-11-21 E*/

    
    // intent - senior settings
    private static boolean mIsNetworkEnabled = false;
    private static boolean sIsNetworkSync = false;
    public static final String ACTION_NETWORK_ENABLER_CHANGED = "com.lenovo.action.ACTION_NETWORK_ENABLER_CHANGED";
    public static final String ACTION_DATA_ACQU_ENABLER_CHANGED = "com.lenovo.action.ACTION_DATA_ACQU_ENABLER_CHANGED";
    public static final String EXTRA_NETWORK_ENABLED = "network_enabled";
    public static final String EXTRA_ISFROM_WEATHER = "isfrom_weather";
    public static final String EXTRA_DATA_ACQU_ENABLED = "data_acqu_enabled";
    public static final String ACTION_PERSISTENT_CHANGED = "com.lenovo.action.persistent_changed";
    public static final String EXTRA_PERSISTENT_ENABLED = "persistent_enabled";
    
    public static final String ACTION_CLOSE_ACTIVITY = "com.lenovo.action.CLOSE_ACTIVITY";
    /* RK_ID: zhanghong5. AUT: zhanghong5 . DATE: 2012-06-20 . START */
    public static final String ACTION_ALLAPPLIST_CHANGED = "com.lenovo.action.ACTION_ALLAPPLIST_CHANGED";
    public static final String ACTION_APP_TO_POSITION = "com.lenovo.action.ACTION_APP_TO_POSITION";
    public static final String EXTRA_SORT_KEY = "sortkey";
    /* RK_ID: zhanghong5. AUT: zhanghong5 . DATE: 2012-06-20 . END */
    
    /* RK_ID: RK_HAWAII. AUT: liuli1 . DATE: 2012-06-01 . START */
    // keys -hawaii settings
    public static final String PREF_HAWAII_UPGRADE_SWITCH = "pref_key_hawaii_upgrade_swtich";
    // notify launcher invalidate
    public static final String ACTION_HAWAII_SWITCHED = "com.lenovo.action.hawaii_switched";
    public static final String HAWAII_PAGE_INVALIDATE = "com.lenovo.leos.hw_launcher.ACTION_RESET";
//    private static boolean mIsHwUpgradeEnabled;
//    private static boolean sHwUpgradeSync = false;
    /* RK_ID: RK_HAWAII. AUT: liuli1 . DATE: 2012-06-01 . END */

    /*** AUT: zhaoxy . DATE: 2012-04-19. START***/
    //applist sort
    public static final String KEY_APPLIST_LAST_SORTMODE = "applist_last_sort_by";
    public static final int SORT_BY_NONE = 0;
    public static final int SORT_BY_DES = 1;
    public static final int SORT_BY_ASC = 2;
    /*** AUT: zhaoxy . DATE: 2012-04-19. END***/
    /*** RK_ID: APP_SORT. AUT: zhaoxy . DATE: 2012-05-09. START***/
    public static final String KEY_APPLIST_CURRENT_SORTMODE = "applist_current_sort_by";
    /*** RK_ID: APP_SORT. AUT: zhaoxy . DATE: 2012-05-09. END***/
    
    // dync theme change
    // variable - theme detail
    private static boolean mEnableDyncThemeChange = true;// whether theme random change
    private static boolean sIsDyncThemeChange = false;
    private static final String PREF_SENSITIVITY = "pref_sensitivity";
    private static int mSensitivity = Integer.MIN_VALUE;    
    private static final int DEFAULT_SENSITIVITY = 2; //bigger, more sensitive
    
    /*RK_ID: RK_TRASH . AUT: zhanggx1 . DATE: 2012-04-05 . S*/
    public static final String ACTION_WORKSPACE_CLEANUP = "android.intent.action.WORKSPACE_CLEANUP";
    public static final String ACTION_START_WORKSPACE_CLEANUP = "android.intent.action.START_WORKSPACE_CLEANUP";
    public static final String EXTRA_CLEANUP_ITEMS = "CLEANUP_ITEMS";
    public static final String EXTRA_CLEANUP_METHOD = "CLEANUP_METHOD";
    public static final String EXTRA_CLEANUP_TYPE_TIMING = "CLEANUP_TYPE_TIMING";
    public static final String PREF_AUTO_WORKSPACE_CLEANUP = "pref_auto_workspace_cleanup";
    /*PK_ID:AUTOMATICALLY_CLASSIFY AUT:GECN1 DATE:2012-03-24 S */
    public static final String PREF_AUTO_APPS_CLASSIFY = "pref_auto_apps_classify";
    public static final String FIRST_USED_CLASSIFICATON = "first_used_classification";
    public static final String FIRST_USED_AUTO_CLASSIFICATON = "first_used_auto_classification";
    /*PK_ID:AUTOMATICALLY_CLASSIFY AUT:GECN1 DATE:2012-03-24 S */
    public static final String PREF_WORKSPACE_CLEANUP_TIMEOUT = "pref_workspace_cleanup_timeout";
    public static final String PREF_WORKSPACE_CLEANUP_TIMING = "pref_workspace_cleanup_timing";
    public static final String PREF_WORKSPACE_CLEANUP_METHOD = "pref_workspace_cleanup_method";
    public static final String PREF_WORKSPACE_NEXT_CLEANTIME = "pref_workspace_next_cleantime";
    public static final int MAX_FOLDER_ICON_NUM = 16;
    
    private static final String CLEANUP_TIME_SEPERATOR = "x";
    private static boolean mEnableAutoWorkspaceCleanup = true;
    private static boolean sIsAutoWorkspaceCleanup = false;
    private static String mWorkspaceCleanupTimeout;
    private static String mWorkspaceCleanupTiming;
    private static String mWorkspaceCleanupMethod;
    private static long mWorkspaceNextCleanTime = Long.MIN_VALUE;    
    /*RK_ID: RK_TRASH . AUT: zhanggx1 . DATE: 2012-04-05 . E*/
    
    // save the applist style (list or grid)
    public static final String PREF_IS_LIST_STYLE = "pref_is_list_style";
    
    // the feedback message
    public static final String PREF_FEEDBACK_TITLE = "pref_feedback_title";
    public static final String PREF_FEEDBACK_CONTENT = "pref_feedback_content";
    
    //RK_LEOS_APP_SETTING,zhanglz1,20130311
    public static final String ACTION_LEOS_APP_SETTING_REFRESH = "com.lenovo.launcher.leosappsettingrefresh";
	public static CharSequence PREF_LEOS_APP_SETTING = "pref_leos_app_settings";
	
	public static final String PREF_VERSION_UPDATE ="pref_key_version_update";
	public static final String PREF_SINGLE_lAYER ="pref_key_single_layer";
	public static  boolean mSingleLayerValue = true;
	// key - about add shortcut to workspace
    public static final String EXTRA_CURRENT_PAGE = "extra_current_page";
//    public static SettingsValue getInstance() {
//        if (sInstance == null) {
//            sInstance = new SettingsValue();
//        }
//        return sInstance;
//    }
    
    public static boolean mAppHardware = false;

    public static final String ACTION_APP_HARDWARE = "android.intent.action.app_hardware";
    
    public static final long WAIT_TO_ENTER_DELAY = 1500L;
    public static final long SCROLL_DURATION = 300L;
    public static final int DRAG_ACTION_MOVE = 0;
    
    // theme value
    public static String getThemeValue(Context c) {
    	if (sPrefCurrentTheme == null) {
        	String defaultTheme = getDefaultThemeValue(c);
        	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        	String set_Theme = Settings.System.getString(c.getContentResolver(), KEY_SET_THEME);
        	String themePref = sp.getString(PREF_THEME, null);
        	if ((set_Theme != null && !"null".equals(set_Theme)) 
        			&& (themePref != null && !"null".equals(themePref))) {
        		SharedPreferences.Editor sharedPreferences = sp.edit();
	        	sharedPreferences.putString(PREF_THEME, set_Theme);
	        	sharedPreferences.apply();
        	} else if ((set_Theme == null || "null".equals(set_Theme))
        			&& (themePref != null && !"null".equals(themePref))) {
        		set_Theme = themePref;
        		Settings.System.putString(c.getContentResolver(), KEY_SET_THEME, set_Theme);
        	} else {
        		set_Theme = defaultTheme;
        		SharedPreferences.Editor sharedPreferences = sp.edit();
	        	sharedPreferences.putString(PREF_THEME, set_Theme);
	        	sharedPreferences.apply();
	        	
	        	Settings.System.putString(c.getContentResolver(), KEY_SET_THEME, set_Theme);
        	}        	
            sPrefCurrentTheme = set_Theme;//sharedPreferences.getString(PREF_THEME, defaultTheme);
        }
        return sPrefCurrentTheme;
    }

    public static void setThemeValue(Context context, String theme) {
        sPrefCurrentTheme = theme;
    }
    
    public static String getDefaultThemeValue(Context c) {
    	String androidTheme = getDefaultAndroidTheme(c);    		
    	sPrefDefaultTheme = getConfigDefaultTheme(c);
    	if (!androidTheme.equals(sPrefDefaultTheme)) {
    		try {
				/*android.content.pm.ApplicationInfo info = */c.getPackageManager().getApplicationInfo(sPrefDefaultTheme, 0);
			} catch (NameNotFoundException e) {
				System.out.println("--------- getDefaultThemeValue not found--"+sPrefDefaultTheme);
				sPrefDefaultTheme = androidTheme;
			}
    	}
        return sPrefDefaultTheme;
    }
    
    public static String getConfigDefaultTheme(Context c) {
    	if (sPrefDefaultThemeConfig == null) {
    		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
    		String androidTheme = getDefaultAndroidTheme(c);    		
    		sPrefDefaultThemeConfig = sharedPreferences.getString(PREF_THEME_DEFAULT, androidTheme);
    	}
    	System.out.println("------- sPrefDefaultThemeConfig="+sPrefDefaultThemeConfig);
    	return sPrefDefaultThemeConfig;
    }
    
    public static String getDefaultAndroidTheme(Context c) {
    	return RES_DEFAULT_ANDROID_THEME;  
    }

    // profile value
    public static String getProfileValue(Context c) {
        if (sPrefCurrentProfile == null) {
            SharedPreferences sharedPreferences = c.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
            sPrefCurrentProfile = sharedPreferences.getString(PREF_PROFILE, getDefaultProfileValue(c));
        }
        
        //fix bug 170123 start
        boolean recheckInCaseDefault = false;
        SharedPreferences sharedPreferences = c.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        recheckInCaseDefault = sharedPreferences.getBoolean(KEY_IS_DEFAULT_PROFILE, true);
        if( recheckInCaseDefault )
        	sPrefCurrentProfile = getDefaultProfileValue(c);
        //end
        
        return sPrefCurrentProfile;
    }

    public static String getDefaultProfileValue(Context c) {
        return c.getString(R.string.pref_default_profile);
    }

    // icon style settings
    public static final int getIconStyleIndex(Context context) {
        if (sIconStyle == Integer.MIN_VALUE) {
            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(context);
            sIconStyle = sharedPreferences.getInt(PREF_ICON_BG_STYLE, SettingsValue.DEFAULT_ICON_BG_INDEX);
        }
        return sIconStyle;
    }

    public static void setIconStyleIndex(int index) {
        sIconStyle = index;
    }
    /***RK_ID:RK_FONT AUT:zhanglz1@lenovo.com. DATE: 2013-02-21 S***/        
	//bugfix 8238 zhanglz1 0305
    private static Typeface tf = null;
    public static Typeface getFontStyle(Context context) {
		//Typeface tf = Typeface.DEFAULT;
		/*if (context != null) {
			SharedPreferences sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(context);
			sFontEntry = sharedPreferences.getString(PREF_TEXT_FONT, null);
			if (sFontEntry != null) {
				try {
					tf = Typeface.createFromFile(sFontEntry);
				} catch (Exception e) {
					// TODO: handle exception
				}
				//for 3.0
				return tf;
			}else{
				return null;
			}
		} else{
			return null;
		}*/
    	return null;
    }
    public static String getFontStyleFile(Context context) {
		if (context != null) {
			SharedPreferences sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(context);
			sFontEntry = sharedPreferences.getString(PREF_TEXT_FONT, null);
			return sFontEntry;
		} else {
			return null;
		}
    }
	private static boolean sIsFontStylerChanged = false;
    public static void setFontStyle(String fontEntry) {
    	sFontEntry = fontEntry;
    	sIsFontStylerChanged = true;
    }
    /***RK_ID:RK_FONT AUT:zhanglz1@lenovo.com. DATE: 2013-02-21 E***/        

    public static boolean isAllAppsReady(Context context) {
        LauncherApplication app = (LauncherApplication) context.getApplicationContext();
        if (!app.getModel().isAllAppsLoaded()) {
            Toast.makeText(context, R.string.wait_allapps_loaded, Toast.LENGTH_SHORT).show();
        }
        return app.getModel().isAllAppsLoaded();
    }
    /***RK_ID:RK_CHANGE_ICON_SIZE AUT:zhanglz1@lenovo.com. DATE:2013-01-14.S***/       
    // icon size
    public static int getIconSizeValue(Context c) {
/*    	if(c == null) return 0;
    	String[] keys = RES_ICON_SIZE_VALUES;
		sPrefIconSize = keys[0];
		LauncherApplication la = null;
    	la = (LauncherApplication)c.getApplicationContext();
        int iconSize = -1;
        if(c.getResources()!=null){
        	DisplayMetrics dm = c.getResources().getDisplayMetrics();
			float mDeviceDensity = dm.density;
		    iconSize = Math.round((mDeviceDensity * (float)Integer.parseInt(sPrefIconSize)));
		}else if(la !=null){
			Resources res = la.getResources();
			DisplayMetrics dm = res.getDisplayMetrics();
			float mDeviceDensity = dm.density;
		    iconSize = Math.round((mDeviceDensity * (float)Integer.parseInt(sPrefIconSize)));
		}*/
        return  c.getResources().getDimensionPixelSize(R.dimen.app_icon_size);
    }
    
    public static void setIconSizeValue(String s) {
        sPrefIconSize = s;
    }
    public static int getIconSizeValueNew(Context c) {
		/*if (sPrefIconSizeNew == null) {
		//	sIconSizeChanged=true;
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
			sPrefIconSizeNew = sharedPreferences.getString(PREF_ICON_SIZE_NEW,c.getString(R.string.prefDefault_icon_size));
			if (sPrefIconSizeNew.equalsIgnoreCase("NORMAL")) {
				String[] keys = RES_ICON_SIZE_VALUES;
				sPrefIconSizeNew = keys[0];
			} else if (sPrefIconSizeNew.equalsIgnoreCase("SMALL")) {
				String[] keys = RES_ICON_SIZE_VALUES;
				sPrefIconSizeNew = keys[1];
			}
		}
		int iconSize = -1;
		int sizeInt = -1;
		LauncherApplication la = null;

		if(c != null){
    	    la = (LauncherApplication)c.getApplicationContext();
		}
        
        if(c!=null && c.getResources()!=null){
    		sizeInt = c.getResources().getDimensionPixelOffset(R.dimen.icon_style_app_icon_size);
    		DisplayMetrics dm = c.getResources().getDisplayMetrics();
			float mDeviceDensity = dm.density;
			try {
				sizeInt = Integer.parseInt(sPrefIconSizeNew);
			} catch (Exception e) {
				// TODO: handle exception
			}
		    iconSize = Math.round((mDeviceDensity * (float)sizeInt));
		}else if(la !=null){
			Resources res = la.getResources();
    		sizeInt = res.getDimensionPixelOffset(R.dimen.icon_style_app_icon_size);
    		try {
				sizeInt = Integer.parseInt(sPrefIconSizeNew);
			} catch (Exception e) {
				// TODO: handle exception
			}
    		DisplayMetrics dm = res.getDisplayMetrics();
			float mDeviceDensity = dm.density;
		    iconSize = Math.round((mDeviceDensity * (float)sizeInt));
		}*/
        return c.getResources().getDimensionPixelOffset(R.dimen.app_icon_drawable_size);
    }
    
    /*public static void setIconSizeValueNew(String s) {
        sPrefIconSizeNew = s;
    }*/
    public static int getHotSeatIconSizeValue(Context c) {
    	int hotSeatIconSize = c.getResources()
				.getDimensionPixelSize(
						R.dimen.app_icon_size);
    	//如果以后有需要要hotseat中的图标大小随设置的正常或较小变化，可用下面代码
    	//当前先不用，即无论设置为正常还是较小，hotseat中的图标大小都是不变的
 /*   	DisplayMetrics dm = new DisplayMetrics();
        dm = c.getApplicationContext().getResources().getDisplayMetrics();  
		float mDeviceDensity = dm.density;
		int temp = Math.round(SettingsValue.getIconSizeValue(c)/mDeviceDensity);
		String[] keys = ICON_SIZE_VALUES;
		//normal
		if (temp == Integer.parseInt(keys[0])) {
			hotSeatIconSize = c.getResources()
					.getDimensionPixelSize(
							R.dimen.app_icon_hotseat_size);
		} //small
		else if (temp == Integer.parseInt(keys[1])) {
			hotSeatIconSize = c.getResources()
					.getDimensionPixelSize(
							R.dimen.app_icon_hotseat_size);
		}*/
        return hotSeatIconSize;
    }
    /***RK_ID:RK_CHANGE_ICON_SIZE AUT:zhanglz1@lenovo.com. DATE:2013-01-14.E***/       

    // text size
    public static String getIconTextSizeValue(Context c) {
        if (sPrefIconTextSize == null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
            sPrefIconTextSize = sharedPreferences.getString(PREF_ICON_TEXT_SIZE, c
                    .getString(R.string.prefDefault_icon_text_size));
            /*RK_ID: RK_FIX_BUG . AUT: zhangdxa . DATE: 2013-01-29 . BUG:6651.S*/
            String[] keys = c.getResources().getStringArray(R.array.pref_icon_text_size_values);
            if (sPrefIconTextSize.equalsIgnoreCase("BIG")) {
                sPrefIconTextSize = keys[0];
            } else if (sPrefIconTextSize.equalsIgnoreCase("NORMAL")) {
                sPrefIconTextSize = keys[1];
            } else if (sPrefIconTextSize.equalsIgnoreCase("SMALL")) {
                sPrefIconTextSize = keys[2];
            } 
            else if( (!sPrefIconTextSize.equals(keys[0]) ) &&
            		 (!sPrefIconTextSize.equals(keys[1])) &&
            		 (!sPrefIconTextSize.equals(keys[2]) )){
                // add default value.
            	boolean b =false;
            	try{
            		Integer temp = Integer.valueOf(sPrefIconTextSize);
            		Integer temp2 = Integer.valueOf(keys[2]);
            		
            		if(temp2.intValue() > temp.intValue()){
            			b = true;
            		}
            	}catch(NumberFormatException e ){
            		
            	}
            	if( !b){
            		sPrefIconTextSize = keys[1];
            	}
            	
            }
            /*RK_ID: RK_FIX_BUG . AUT: zhangdxa . DATE: 2013-01-29 . BUG:6651.E*/
        }
        return sPrefIconTextSize;
    }
    
    /*RK_ID: RK_FIX_BUG . AUT: zhangdxa . DATE: 2013-01-29 . BUG:6651.S*/
    public static void setIconTextSizeValue(Context c, String s) {
        sPrefIconTextSize = s;
        if(sPrefIconTextSize != null){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(PREF_ICON_TEXT_SIZE, s);
            editor.commit();
        }
    }
    /*RK_ID: RK_FIX_BUG . AUT: zhangdxa . DATE: 2013-01-29 . BUG:6651.E*/
    
    // text color
    public static int getIconTextStyleValue(Context c) {
        if (sPrefIconTextStyle == Integer.MIN_VALUE) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
            sPrefIconTextStyle = sharedPreferences.getInt(PREF_ICON_TEXT_STYLE, c.getResources().getColor(R.color.apps_icon_text_color));
        }
        return sPrefIconTextStyle;
    }
    
    public static void setIconTextStyleValue(int s) {
        sPrefIconTextStyle = s;
    }
    /***RK_ID:RK_CUSTOM_ICON_TEXT_SHADOW_COLOR AUT:zhanglz1@lenovo.com.DATE:2012-12-13. S***/        
    private static int sPrefIconTextShadowColorInApplist = Integer.MIN_VALUE;
    public static int getIconTextShadowColorValueInApplist(Context c) {
        if (sPrefIconTextShadowColorInApplist == Integer.MIN_VALUE) {
            sPrefIconTextShadowColorInApplist = c.getResources().getColor(R.color.apps_icon_text_shadow_color_in_applist);
        }
        return sPrefIconTextShadowColorInApplist;
    }
    public static void setIconTextShadowColorValueInApplist(int s) {
    	sPrefIconTextShadowColorInApplist = s;
    }
    private static int sPrefIconTextShadowColorInWorkspace = Integer.MIN_VALUE;
    public static int getIconTextShadowColorValueInWorkspace(Context c) {
        if (sPrefIconTextShadowColorInWorkspace == Integer.MIN_VALUE) {
            sPrefIconTextShadowColorInWorkspace = c.getResources().getColor(R.color.apps_icon_text_shadow_color_in_workspace);
        }
        return sPrefIconTextShadowColorInWorkspace;
    }
    public static void setIconTextShadowColorValueInWorkspace(int s) {
    	sPrefIconTextShadowColorInWorkspace = s;
    }
    /***RK_ID:RK_CUSTOM_ICON_TEXT_SHADOW_COLOR AUT:zhanglz1@lenovo.com.DATE:2012-12-13. E***/        

    // applist edit style
    public static String getAppListEditValue(Context c) {
        if (sPrefAppListEdit == null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
            sPrefAppListEdit = sharedPreferences.getString(PREF_APPLIST_EDIT, c
                    .getString(R.string.prefDefault_icon_edit));
        }
        return sPrefAppListEdit;
    }
    
    public static void setAppListEditValue(String s) {
        sPrefAppListEdit = s;
    }
    
    // folder style && hotseat style
    public static void setIconStyleValue(String iconStyle) {
        sPrefIconStyle = iconStyle;
    }
    
    public static String getIconStyleValue(Context c) {
        if (sPrefIconStyle == null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
            sPrefIconStyle = sharedPreferences.getString(PREF_FOLDER_STYLE, c.getString(R.string.prefDefault_folderStyle));
        }
        return sPrefIconStyle;
    }
 /** RK_ID: RK_MENU_REFACTOR. AUT: LIUYG1 DATE: 2012-12-01 S */
    public static boolean useAndroidStyle(Context c, SettingsType settingType) {
        if(!GlobalDefine.BU_VERSION){
        	return false;
        }else{
    	    String style = null;
            switch (settingType) {
                case FOLDER_STYLE:
                    style = getIconStyleValue(c);
                    break;
                case HOTSEAT_STYLE:
                    style = getHotseatStyleValue(c);
                    break;          
            }
            return "ANDROID".equals(style);
        }
    }    
 /** RK_ID: RK_MENU_REFACTOR. AUT: LIUYG1 DATE: 2012-12-01 E */

    // indicator
    public static void setHotseatStyleValue(String hotseatStyle) {
        sPrefHotseatStyle = hotseatStyle;
    }

    public static String getHotseatStyleValue(Context c) {
        if (sPrefHotseatStyle == null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
            sPrefHotseatStyle = sharedPreferences.getString(PREF_HOTSEAT_STYLE, c.getString(R.string.prefDefault_hotseatStyle));
        }
        return sPrefHotseatStyle;
    }

    // effects - workspace slide
    public static void setWorkspaceSlide(String workspaceSlide) {
        sPrefWorkspaceSlide = workspaceSlide;
    }
    
    public static String getWorkspaceSlideValue(Context c) {
        if (sPrefWorkspaceSlide == null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
            sPrefWorkspaceSlide = sharedPreferences.getString(PREF_WORKSPACE_SLIDE, c.getString(R.string.prefDefault_workspaceSlide));
        }
        String[] values = c.getResources().getStringArray(R.array.pref_slide_effect_values);
        int count = values.length;
        boolean found = false;
        for (int i = 0; i < count; i++) {
            if (values[i].equals(sPrefWorkspaceSlide)) {
                found = true;
                break;
            }
        }
        if (!found)
            sPrefWorkspaceSlide = c.getString(R.string.prefDefault_workspaceSlide);
        return sPrefWorkspaceSlide;
    }

    // workspace loop
    public static boolean isWorkspaceLoop(Context c) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        sWorkspaceSlideLoop = sharedPreferences.getBoolean(PREF_WORKSPACE_LOOP, true);
        return sWorkspaceSlideLoop;
    }

    public static final String KEY_RESET_SETTINGS = "reset_to_mysettings";

    public static Runnable resetToMySettings(final XLauncher context) {
        return new Runnable() {

            @Override
            public void run() {
                SharedPreferences.Editor shared_editor = PreferenceManager
                        .getDefaultSharedPreferences(context).edit();
                shared_editor.putString(PREF_APPLIST_ENTER, "ALPHA");
                shared_editor.putString(PREF_APPLIST_SLIDE, "CYLINDER");
                shared_editor.putString(PREF_WORKSPACE_SLIDE, "NONE");
                shared_editor.putBoolean(PREF_WORKSPACE_LOOP, false);
                shared_editor.commit();

                sPrefAppListEnter = "ALPHA";
                sPrefAppListSlide = "CYLINDER";
                sPrefWorkspaceSlide = "NONE";
                sWorkspaceSlideLoop = false;

                context.updateAppSlideValue();
                Intent i = new Intent(ACTION_WORKSPACE_LOOP);
                i.putExtra(EXTRA_WORKSPACE_IS_LOOP, sWorkspaceSlideLoop);
                context.sendBroadcast(i);

                SharedPreferences prefs = context.getSharedPreferences(
                        "com.lenovo.launcher2.prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(KEY_RESET_SETTINGS, true).putBoolean(
                        ConstantAdapter.EXCLUDED_SETTING_KEY, true);
                editor.commit();
            }
        };
    }

    // app list enter
    public static void setAppListEnterValue(String appListEnter) {
        sPrefAppListEnter = appListEnter;
    }

    public static String getAppListEnterValue(Context c) {
        if (sPrefAppListEnter == null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
            String default_value = c.getString(R.string.prefDefault_applist);
            sPrefAppListEnter = sharedPreferences.getString(PREF_APPLIST_ENTER, default_value);

            String[] values = c.getResources().getStringArray(R.array.pref_applist_effect_values);
            int count = values.length;
            boolean found = false;
            for (int i = 0; i < count; i++) {
                if (values[i].equals(sPrefAppListEnter)) {
                    found = true;
                    break;
                }
            }
            if (!found)
                sPrefAppListEnter = default_value;
        }
        return sPrefAppListEnter;
    }

    public static void onAppListPreferenceChange(Preference preference, Object newValue) {
        if (newValue instanceof String) {
            String enterValue = (String) newValue;
            if (preference instanceof ListPreference) {
    			((ListPreference) preference).setValue(enterValue);
    		}
            preference.setSummary(((ListPreference) preference).getEntry());
            SettingsValue.setAppListEnterValue(enterValue);
        }
    }

    // app list slide
    public static void setAppListSlideValue(String appListSlide) {
        sPrefAppListSlide = appListSlide;
    }

    public static String getAppListSlideValue(Context c) {
        if (sPrefAppListSlide == null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
            sPrefAppListSlide = sharedPreferences.getString(PREF_APPLIST_SLIDE, c.getString(R.string.prefDefault_workspaceSlide));
        }
        /* cancel by xingqx 2012.12.10 s */
        /*
        if (isVersionLargerThan41()//Build.VERSION.RELEASE.equals(SettingsValue.VERSION_CODE)
                && (sPrefAppListSlide.equals(LauncherPersonalSettings.SLIDEEFFECT_CYLINDER) || sPrefAppListSlide
                        .equals(LauncherPersonalSettings.SLIDEEFFECT_SPHERE))) {
            sPrefAppListSlide = c.getString(R.string.prefDefault_workspaceSlide);
        }
        */
        /* cancel by xingqx 2012.12.10 e */
        return sPrefAppListSlide;
    }

    public static void resetPreference(ListPreference preference, String value) {
        // remove CHARIOT for workspace because not implement.
        CharSequence[] entries = preference.getEntries();
        CharSequence[] entryValue = preference.getEntryValues();
        int length = Math.min(entries.length, entryValue.length);

        CharSequence[] new_entries = new CharSequence[length - 1];
        CharSequence[] new_entryValue = new CharSequence[length - 1];
        int index1 = 0;
        for (int i = 0; i < length && index1 < (length - 1); i++) {
            // first entries, them entryValue
            if (entryValue[i].equals(value)) {
                continue;
            }

            new_entries[index1] = entries[i];
            new_entryValue[index1++] = entryValue[i];
        }

        preference.setEntries(new_entries);
        preference.setEntryValues(new_entryValue);
    }

    public static void onAppListSlidePreferenceChange(Preference preference, Object newValue, Context context) {
        if (newValue instanceof String) {
            String slideValue = (String) newValue;
            if (preference instanceof ListPreference) {
    			((ListPreference) preference).setValue(slideValue);
    		}
            preference.setSummary(((ListPreference) preference).getEntry());
            SettingsValue.setAppListSlideValue(slideValue);
            checkShowToast(slideValue, context);
        }
    }

    public static boolean isVersionLargerThan41() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1;
    }
    //bugfix4214 zhanglz1 20121228
    public static boolean isVersionLargerThanJellyBean() {
        return Build.VERSION.SDK_INT > 16;
    }

//    public static void setPreferenceForJellyBean(ListPreference preference, String value) {
//        if (isVersionLargerThan41()) {//Build.VERSION.RELEASE.equals(SettingsValue.VERSION_CODE)) {
//            // remove CYLINDER and SPHERE
//            // first entries, them entryValue
//            CharSequence[] entries = preference.getEntries();
//            CharSequence[] new_entries = new CharSequence[entries.length - 2];
//            System.arraycopy(entries, 0, new_entries, 0, new_entries.length);
//            preference.setEntries(new_entries);
//
//            CharSequence[] entryValue = preference.getEntryValues();
//            CharSequence[] new_entryValue = new CharSequence[entryValue.length - 2];
//            System.arraycopy(entryValue, 0, new_entryValue, 0, new_entryValue.length);
//            preference.setEntryValues(new_entryValue);
//
//            if (preference.findIndexOfValue(value) == -1) {
//                // invalid value. reset to default value
//                String defaultValue = preference.getContext().getString(R.string.prefDefault_workspaceSlide);
//                setAppListSlideValue(defaultValue);
//                preference.setValue(defaultValue);
//            }
//        }
//    }
    
    public static void checkShowToast(String newValue, Context context) {
//        if (newValue.equals(LauncherPersonalSettings.SLIDEEFFECT_CYLINDER)
//                || newValue.equals(LauncherPersonalSettings.SLIDEEFFECT_SPHERE))
//        {
//            Toast.makeText(context, R.string.slide_effect_notify, Toast.LENGTH_SHORT).show();
//            //开启硬件加速
//            mAppHardware = true;
//        }
//        else if (newValue.equals(LauncherPersonalSettings.SLIDEEFFECT_RANDOM))
//        {
//            //开启硬件加速
//            Toast.makeText(context, R.string.slide_effect_notify, Toast.LENGTH_SHORT).show();
//            mAppHardware = true;
//        }
//        else
//        {
//            mAppHardware = false;
//            //关闭硬件加速
//        }
        
//        Intent intent = new Intent(SettingsValue.ACTION_APP_HARDWARE);
//        intent.putExtra("intent", );      
//        context.sendBroadcast(intent);
    }
    
    public static void setAppHardware(Context context) {
//        String newValue = getAppListSlideValue(context);
//        Log.d("my", "newValue = " + newValue);
//        if (newValue.equals(LauncherPersonalSettings.SLIDEEFFECT_CYLINDER)
//                || newValue.equals(LauncherPersonalSettings.SLIDEEFFECT_SPHERE))
//        {
//            //开启硬件加速
//            mAppHardware = true;
//        }
//        else if (newValue.equals(LauncherPersonalSettings.SLIDEEFFECT_RANDOM))
//        {
//            //开启硬件加速
//            mAppHardware = true;
//        }
//        else
//        {
//            mAppHardware = false;
//            //关闭硬件加速
//        }
    }

    // app list cell y count
    public static String getAppListCellY(Context c) {
        if (sAllAppCellY == null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
            sAllAppCellY = sharedPreferences.getString(PREF_APP_CELLY, c.getString(R.string.pref_all_app_y_default));
        }
        return sAllAppCellY;
    }

    public static void onAppListCellYPreferenceChange(Preference preference, String newVaule, Context c) {
        sAllAppCellY = newVaule;
		if (preference instanceof ListPreference) {
			((ListPreference) preference).setValue(sAllAppCellY);
		}
        preference.setSummary(((ListPreference) preference).getEntry());
        c.sendBroadcast(new Intent(ACTION_CELLY_CHANGED));
    }
   /*RK_ID: RK_SCREEN_CELLCOUNT . AUT:liuyg1@lenovo.com DATE: 2012-11-13 START*/ 
    public static String getScreenListCellArray(Context c) {
        if (sScreenCellcount == null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
            sScreenCellcount = sharedPreferences.getString(PREF_SCREEN_CELLCOUNT,"");
        }
        return sScreenCellcount;
    }
    public static void onScreenListCellArrayPreferenceChange(Preference preference, String newVaule,Context c) {
    	sScreenCellcount = newVaule;
        if (preference instanceof ListPreference) {
			((ListPreference) preference).setValue(sScreenCellcount);
		}
        preference.setSummary(((ListPreference) preference).getEntry());
        c.sendBroadcast(new Intent(ACTION_SCREEN_CELLCOUNT_CHANGED));
    }
/*RK_ID: RK_SCREEN_CELLCOUNT . AUT:liuyg1@lenovo.com DATE: 2012-11-13 END*/ 
/*RK_GESTURE_SETTING liuyg1@lenovo.com 2013-5-30 S*/
    public static String getScrollUpGuestureArray(Context c) {
        if (sScrollUpGuesture == null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
            sScrollUpGuesture = sharedPreferences.getString(PREF_SCROLL_UP_GESTURE,c.getString(R.string.pref_scroll_up_guesture_default));
        }
        return sScrollUpGuesture;
    }
    public static void onScrollUpGuestureArrayPreferenceChange(Preference preference, String newVaule,Context c) {
    	sScrollUpGuesture = newVaule;
        if (preference instanceof ListPreference) {
			((ListPreference) preference).setValue(sScrollUpGuesture);
		}
        preference.setSummary(((ListPreference) preference).getEntry());
        c.sendBroadcast(new Intent(ACTION_SCROLL_UP_GESTURE_CHANGED));
    }
   
      public static String getScrollDownGuestureArray(Context c) {
        if (sScrollDownGuesture == null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
            sScrollDownGuesture = sharedPreferences.getString(PREF_SCROLL_DOWN_GESTURE,c.getString(R.string.pref_scroll_down_guesture_default));
        }
        return sScrollDownGuesture;
    }
    public static void onScrollDownGuestureArrayPreferenceChange(Preference preference, String newVaule,Context c) {
    	sScrollDownGuesture = newVaule;
        if (preference instanceof ListPreference) {
			((ListPreference) preference).setValue(sScrollDownGuesture);
		}
        preference.setSummary(((ListPreference) preference).getEntry());
        c.sendBroadcast(new Intent(ACTION_SCROLL_DOWN_GESTURE_CHANGED));
    }
    public static String getDoubleClickGuestureArray(Context c) {
        if (sDoubleClickGuesture == null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
            sDoubleClickGuesture = sharedPreferences.getString(PREF_DOUBLE_CLICK_GESTURE,c.getString(R.string.pref_double_click_guesture_default));
        }
        return sDoubleClickGuesture;
    }
    public static void onDoubleClickGuesturePreferenceChange(Preference preference, String newVaule,Context c) {
    	sDoubleClickGuesture = newVaule;
        if (preference instanceof ListPreference) {
			((ListPreference) preference).setValue(sDoubleClickGuesture);
		}
        preference.setSummary(((ListPreference) preference).getEntry());
        c.sendBroadcast(new Intent(ACTION_DOUBLE_CLICK_GESTURE_CHANGED));
    }  
 /*RK_GESTURE_SETTING liuyg1@lenovo.com 2013-5-30 E*/
    // widget preview load setting
    public static void enableLoadWidget(boolean enable) {
        mLoadWidgetPreview = enable;
        sIsLoadWidgetSync = true;
    }

    public static boolean getLoadWidgetSettings(Context c) {
        if (!sIsLoadWidgetSync) {
            sIsLoadWidgetSync = true;
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
            mLoadWidgetPreview = sharedPreferences.getBoolean(PREF_LOAD_WIDGET_PREVIEW, false);
        }
        return mLoadWidgetPreview;
    }

    public static void onLoadwidgetSettingsChange(Context c, Object newValue) {
        if (newValue instanceof Boolean) {
            boolean loadWidget = (Boolean) newValue;
            SettingsValue.enableLoadWidget(loadWidget);
            c.sendBroadcast(new Intent(SettingsValue.ACTION_LOAD_WIDGET_SETTINGS));
        }
    }

    // wallpaper slide together with workspace
    public static void enableWallpaperSlide(boolean enable) {
        mEnableWallpaperSlide = enable;
        sIsWallpaperSync = true;
    }
    
    public static boolean isWallpaperSlideEnabled(Context c) {
        if (!sIsWallpaperSync) {
            sIsWallpaperSync = true;
            SharedPreferences sharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(c);
            mEnableWallpaperSlide = sharedPreferences.getBoolean(PREF_WALLPAPER_SLIDE, true);
        }
        return mEnableWallpaperSlide;
    }
    
    // senior settings
    public static void enableNetwork(boolean enable) {
        mIsNetworkEnabled = enable;
        sIsNetworkSync = true;
    }
    
    public static boolean isNetworkEnabled(Context c) {
        //added by yumina for the wifi img
        if(isWifiProduct()) return true;
        if (!sIsNetworkSync) {
            sIsNetworkSync = true;
            SharedPreferences sharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(c);
            mIsNetworkEnabled = sharedPreferences.getBoolean(PREF_NETWORK_ENABLER, true);
        }
        return mIsNetworkEnabled;
    }

    // fix bug 171611
    public static void setDataAcqu(Context context, boolean flag) {
        SharedPreferences settings = context
                .getSharedPreferences(SettingsValue.PERFERENCE_NAME, Context.MODE_APPEND
                        | Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(PREF_DATA_ACQU_ENABLER, flag);
        editor.commit();
    }

    /** RK_ID: RK_NEWFEATURE_TEXTBACKGROUND. AUT: yumina DATE: 2012-10-18 S */
    private static boolean mIsDesktopBackEnabled = true;
    private static boolean sIsDesktopBackSync = false;
    public static final String PREF_DESKTOP_TEXT_BACKGROUND = "pref_icon_text_background";
    public static final String ACTION_TEXT_BACKGROUND_ONOFF = "com.lenovo.launcher.action.REFRESH_TEXTBACKGROUND";

    public static void enableDesktopTextBackground(boolean enable){
        mIsDesktopBackEnabled = enable;
		sIsDesktopBackSync = true;
    }
    public static boolean isDesktopTextBackgroundEnabled(Context c) {
        return false;
//        if (!sIsDesktopBackSync) {
//            sIsDesktopBackSync = true;
//            SharedPreferences sharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(c);
//            mIsDesktopBackEnabled = sharedPreferences.getBoolean(PREF_DESKTOP_TEXT_BACKGROUND, false);
//
//        }
//        return mIsDesktopBackEnabled;
    }
    /** RK_ID: RK_NEWFEATURE_TEXTBACKGROUND. AUT: yumina DATE: 2012-10-18 E */
    /* RK_ID: RK_HAWAII. AUT: liuli1 . DATE: 2012-06-01 . START */
    // hawaii
    public static boolean isHwUpgradeEnabled(Context context) {
//        if (!sHwUpgradeSync) {
//            sHwUpgradeSync = true;
//            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
//            mIsHwUpgradeEnabled = sp.getBoolean(PREF_HAWAII_UPGRADE_SWITCH, true);
//        }
//        return mIsHwUpgradeEnabled;
        return false;
    }
    /* RK_ID: RK_HAWAII. AUT: liuli1 . DATE: 2012-06-01 . END */

    // theme
    public static void setThemeIconBg(Context friendContext) {
    	if (friendContext == null) {
    		mThemeBgBitmap[0] = null;
    		mThemeBgBitmap[1] = null;
    		mThemeBgBitmap[2] = null;
    	}
    	/*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-01 S*/
    	String themeIconBgName = SettingsValue.THEME_ICON_BG_NAME;
    	String themeIconFgName = SettingsValue.THEME_ICON_FG_NAME;
    	String themeIconMaskName = SettingsValue.THEME_ICON_MASK_NAME;
    	if(friendContext != null && friendContext.getPackageName().equals("com.lenovo.launcher")){
    		themeIconBgName  += INBUILD_THME_POSTFIX;
    		themeIconFgName +=INBUILD_THME_POSTFIX;
    		themeIconMaskName +=INBUILD_THME_POSTFIX;
		}
    	/*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-01 E*/
    	Drawable drawable = Utilities.findDrawableByResourceName(themeIconBgName, 
    			friendContext);
    	if (drawable == null) {
    		mThemeBgBitmap[0] = null;
    		mThemeBgBitmap[1] = null;
    		mThemeBgBitmap[2] = null;
    	} else {
		    mThemeBgBitmap[0] = Utilities.createBitmap(drawable, 0, 0, friendContext);		
    	    drawable = Utilities.findDrawableByResourceName(themeIconFgName, friendContext);
    	    mThemeBgBitmap[1] = Utilities.createBitmap(drawable, 0, 0, friendContext); 
    	    drawable = Utilities.findDrawableByResourceName(themeIconMaskName, friendContext);
    	    mThemeBgBitmap[2] = Utilities.createBitmap(drawable, 0, 0, friendContext);     	    
    	}
    }
    
    public static Bitmap[] getThemeIconBg() {
    	return mThemeBgBitmap;
    }
    
    // dync theme change
    public static void enableDyncThemeChange(boolean enable) {
    	mEnableDyncThemeChange = enable;
        sIsDyncThemeChange = true;
    }
    
    public static boolean isDyncThemeChange(Context c) {
        if (!sIsDyncThemeChange) {
        	sIsDyncThemeChange = true;
            SharedPreferences sharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(c);
            mEnableDyncThemeChange = sharedPreferences.getBoolean(PREF_DYNC_THEME_CHANGE, false);
        }
        //20130121 zhanglz1
       // return mEnableDyncThemeChange;
        return false;
    }
    
    public static int getSensitivity(Context c) {
        if (mSensitivity == Integer.MIN_VALUE) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
            mSensitivity = sharedPreferences.getInt(PREF_SENSITIVITY, DEFAULT_SENSITIVITY);
        }
        return mSensitivity;
    }
    
    public static void setSensitivity(int s) {
    	mSensitivity = s;
    }
    
    public static void setNetworkSync(boolean enable) {
        sIsNetworkSync = enable;
    }
    
    /*RK_ID: RK_TRASH . AUT: zhanggx1 . DATE: 2012-04-05 . S*/    
    // Workspace Cleanup
    public static void enableAutoWorkspaceCleanup(boolean enable) {
    	mEnableAutoWorkspaceCleanup = enable;
    	sIsAutoWorkspaceCleanup = true;
    }
    
    public static boolean isAutoWorkspaceCleanup(Context c) {
        if (!sIsAutoWorkspaceCleanup) {
        	sIsAutoWorkspaceCleanup = true;
            SharedPreferences sharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(c);
            mEnableAutoWorkspaceCleanup = sharedPreferences.getBoolean(PREF_AUTO_WORKSPACE_CLEANUP, false);
        }
        return mEnableAutoWorkspaceCleanup;
    }
    
    
    /*PK_ID:AUTOMATICALLY_CLASSIFY AUT:GECN1 DATE:2012-03-24 S */
    public static void enableAutoAppsClassify(boolean enable,Context c) {
        SharedPreferences sharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_AUTO_APPS_CLASSIFY, enable);
        editor.commit();
    }
    
    public static boolean isAutoAppsClassify(Context c) {
        SharedPreferences sharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(c);
        boolean autoClassify = sharedPreferences.getBoolean(PREF_AUTO_APPS_CLASSIFY, false);
        return autoClassify;
    }
    
    
    public static void setFirstUsedClassification(boolean enable,Context c){
        SharedPreferences sharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(FIRST_USED_CLASSIFICATON, enable);
        editor.commit();
    }
    
    public static boolean isFirstUsedClassification(Context c) {
        SharedPreferences sharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(c);
        boolean autoClassify = sharedPreferences.getBoolean(FIRST_USED_CLASSIFICATON, true);
        return autoClassify;
    }
    
    public static void setFirstUsedAutoClassification(boolean enable,Context c){
        SharedPreferences sharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(FIRST_USED_AUTO_CLASSIFICATON, enable);
        editor.commit();
    }
    
    public static boolean isFirstAutoUsedClassification(Context c) {
        SharedPreferences sharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(c);
        boolean autoClassify = sharedPreferences.getBoolean(FIRST_USED_AUTO_CLASSIFICATON, true);
        return autoClassify;
    }
    /*PK_ID:AUTOMATICALLY_CLASSIFY AUT:GECN1 DATE:2012-03-24 S */
    
    public static void setWorkspaceCleanupTimeout(String value) {
		mWorkspaceCleanupTimeout = value;
    }
    
    public static String getWorkspaceCleanupTimeout(Context c) {
        if (mWorkspaceCleanupTimeout == null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
            mWorkspaceCleanupTimeout = sharedPreferences.getString(PREF_WORKSPACE_CLEANUP_TIMEOUT, c.getString(R.string.prefDefault_Timeout));
        }
        return mWorkspaceCleanupTimeout;
    }
    public static void setWorkspaceCleanupTiming(String value) {
    	mWorkspaceCleanupTiming = value;
    }
    
    public static String getWorkspaceCleanupTiming(Context c) {
        if (mWorkspaceCleanupTiming == null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
            mWorkspaceCleanupTiming = sharedPreferences.getString(PREF_WORKSPACE_CLEANUP_TIMING, c.getString(R.string.prefDefault_Timing));
        }
        return mWorkspaceCleanupTiming;
    }
    public static void setWorkspaceCleanupMethod(String value) {
    	mWorkspaceCleanupMethod = value;
    }
    
    public static String getWorkspaceCleanupMethod(Context c) {
        if (mWorkspaceCleanupMethod == null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
            mWorkspaceCleanupMethod = sharedPreferences.getString(PREF_WORKSPACE_CLEANUP_METHOD, c.getString(R.string.prefDefault_Method));
        }
        return mWorkspaceCleanupMethod;
    }
    
    public static void setWorkspaceNextCleanTime(long value) {
    	mWorkspaceNextCleanTime = value;
    }
    
    public static long getWorkspaceNextCleanTime(Context c) {
        if (mWorkspaceNextCleanTime == Long.MIN_VALUE) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
            mWorkspaceNextCleanTime = sharedPreferences.getLong(PREF_WORKSPACE_NEXT_CLEANTIME, Long.MIN_VALUE);
        }
        return mWorkspaceNextCleanTime;
    }
    
    public static long convertWorkspaceTime(String numStr) {
    	if (numStr == null) {
    		return 0L;
    	}
    	long ret = 1L;
    	String[] temp = numStr.split(CLEANUP_TIME_SEPERATOR);
    	try {
	    	for (String item : temp) {
	    		ret *= Long.valueOf(item.trim());
	    	}
    	} catch (NumberFormatException e) {
    		Debug.printException("SettingsValue-->convertTimeStr--", e);
    	}
    	return ret;
    }
    
    public static void scheduleWorkspaceCleanupAlarm(Context context, boolean enable) {
    	long firstTime;
    	AlarmManager am = (AlarmManager)
    	        context.getSystemService(Context.ALARM_SERVICE);
    	Intent intent = new Intent(ACTION_START_WORKSPACE_CLEANUP); 
    	PendingIntent sender = PendingIntent.getBroadcast(
    			context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    	
    	if (enable) {
    	    long interval = convertWorkspaceTime(getWorkspaceCleanupTiming(context));
    	    firstTime = getWorkspaceNextCleanTime(context);
    	    if (firstTime == Long.MIN_VALUE) {		
    	        firstTime =	System.currentTimeMillis() + interval;
    	        setWorkspaceNextCleanTime(firstTime);
    	        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
    	        editor.putLong(PREF_WORKSPACE_NEXT_CLEANTIME, firstTime);
    	        editor.commit();
    	    }
    	    
//    	    Toast.makeText(context, DateUtils.formatDateTime(context, 
//    	    		firstTime, (DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR)), Toast.LENGTH_LONG).show();
    	    am.setRepeating(AlarmManager.RTC, firstTime, interval, sender);
    	} else {
    		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
    	    editor.putLong(PREF_WORKSPACE_NEXT_CLEANTIME, Long.MIN_VALUE);
    	    editor.commit();
    		am.cancel(sender);
    		
    	    SettingsValue.setWorkspaceNextCleanTime(Long.MIN_VALUE);
    	}
    }
    
    public static void rescheduleCleanupAlarm(Context context) {   
    	boolean autoClean = isAutoWorkspaceCleanup(context);
    	if (!autoClean) {
    		return;
    	}
    	AlarmManager am = (AlarmManager)
    	        context.getSystemService(Context.ALARM_SERVICE);
    	Intent intent = new Intent(ACTION_START_WORKSPACE_CLEANUP); 
    	PendingIntent sender = PendingIntent.getBroadcast(
    			context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    	am.cancel(sender);
    	
    	long interval = convertWorkspaceTime(getWorkspaceCleanupTiming(context));
    	long firstTime = System.currentTimeMillis() + interval;
        setWorkspaceNextCleanTime(firstTime);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putLong(PREF_WORKSPACE_NEXT_CLEANTIME, firstTime);
        editor.commit();
        
//        Toast.makeText(context, DateUtils.formatDateTime(context, 
//	    		firstTime, (DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR)), Toast.LENGTH_LONG).show();
    	am.setRepeating(AlarmManager.RTC, firstTime, interval, sender);
    }
    /*RK_ID: RK_TRASH . AUT: zhanggx1 . DATE: 2012-04-05 . E*/

    public static Uri getAppsWallperTempPath(boolean notInput) {
    	String folder = getDesktopDirInSdcard(notInput);
    	if (folder == null) {
    		return null;
    	} 
    	
    	String path = folder + File.separator
    			+ "apps_wallpaper"
                + Constants.CUSTOM_BIMAP_TYPE;
    	File file = new File(path);
    	return Uri.fromFile(file);
    }
    
    public static String getDesktopDirInSdcard(boolean notInput) {
    	String folder = Environment.getExternalStorageDirectory() + Constants.CUSTOM_BIMAP_PATH;
    	boolean ret = com.lenovo.launcher2.customizer.Utilities.newInstance().ensureParentsPaths(folder, true, notInput);
    	if (!ret) {
    		return null;
    	}
    	return folder;
    }
    
    public static Uri getAppsWallperPath(Context context, boolean notInput) {
    	String folder = getDesktopDirInData(context, notInput);
    	if (folder == null) {
    		return null;
    	} 
    	
    	String path = folder + File.separator
    			+ "apps_wallpaper"
                + Constants.CUSTOM_BIMAP_TYPE;
    	File file = new File(path);
    	return Uri.fromFile(file);
    }
    
    public static String getDesktopDirInData(Context context, boolean notInput) {
    	String dataDir = context.getApplicationInfo().dataDir;
    	StringBuilder sb = new StringBuilder(dataDir);
    	sb.append(ConstantAdapter.DIR_DATA_FILES)
    	        .append(ConstantAdapter.DIR_DATA_FILES_EXTRA_FILES)
    	        .append("wallpaper");
    	String folder = sb.toString();
    	boolean ret = com.lenovo.launcher2.customizer.Utilities.newInstance().ensureParentsPaths(folder, true, notInput);
    	if (!ret) {
    		return null;
    	}
    	return folder;
    }
    
    public static String getPackageVersion(Context context) {
    	String appVersion = "1.0";
    	if (context == null) {
    		return appVersion;
    	}
		
		PackageManager manager = context.getPackageManager();
		try { PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
		    appVersion = info.versionName;   //版本名
		} catch (NameNotFoundException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		return appVersion;
	}
    
    
    /* RK_ID: RK_LELAUNCHER_MAGICDOWNLOAD. AUT:zhangdxa. DATE: 2012-09-21.S */
    public static boolean isAKeyInstall(Context context) {
    	int akeyInstallType = Utilities.canAKeyIntall(context);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isAkeyInstall = sp.getBoolean(PREF_AKEY_INSTALL_SET, true);
        if( ( akeyInstallType == Utilities.INSTALL_TYPE_NOT_AKEY ) && isAkeyInstall){
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(PREF_AKEY_INSTALL_SET, false);
            editor.commit();
            isAkeyInstall = false;
        }
        return isAkeyInstall;
    }
    public static void setAKeyInstall(Context context,boolean enable) {
    	 SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    	 SharedPreferences.Editor editor = sp.edit();
         editor.putBoolean(PREF_AKEY_INSTALL_SET, enable);
         editor.commit();
         setAkeyInstallCancel(context, enable);
    }
    public static boolean getAkeyInstall(Context context){
    	 SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
         boolean isAkeyInstall = sp.getBoolean(PREF_AKEY_INSTALL_SET, true);
         return isAkeyInstall;
    }
    public static boolean getAkeyInstallIgnore(Context context){
   	    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isAkeyInstallIgnore = sp.getBoolean(PREF_AKEY_INSTALL_IGNORE, false);
        return isAkeyInstallIgnore;
    }
    public static void setAkeyInstallIgnore(Context context,boolean enable) {
   	    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
   	    SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(PREF_AKEY_INSTALL_IGNORE, enable);
        editor.commit();
    }
    public static boolean getAkeyInstallCancel(Context context){
    	SharedPreferences sp =
                context.getSharedPreferences(Utilities.PREF_MAGICDOWNLOAD, Context.MODE_PRIVATE);
        boolean isAkeyInstallCancel = sp.getBoolean(Utilities.PREF_AKEY_INSTALL_CANCEL, true);
        return isAkeyInstallCancel;
    } 
    public static void setAkeyInstallCancel(Context context,boolean enable) {
    	SharedPreferences sp =
                context.getSharedPreferences(Utilities.PREF_MAGICDOWNLOAD, Context.MODE_PRIVATE);
   	    SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(Utilities.PREF_AKEY_INSTALL_CANCEL, enable);
        editor.commit();
    }
    /* RK_ID: RK_LELAUNCHER_MAGICDOWNLOAD. AUT:zhangdxa. DATE: 2012-09-21.E */
    
    /*RK_SEMI_TRANSPARNET dining@lenovo.com 2012-10-16 S*/
    public static boolean getApplistSemiTransParentValueInStore(Context c){
    	//default is true
    	SharedPreferences sharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(c);
        boolean bTemp = sharedPreferences.getBoolean(SettingsValue.PREF_APPLIST_BGSEMITARNSPARENT, true);
        String sTemp = getThemeValue(c);
        if(sTemp.equals(c.getString(R.string.pref_default_theme))){
        	return true;
        }
        return bTemp;
    }
    /*RK_SEMI_TRANSPARNET dining@lenovo.com 2012-10-16 E*/
    /*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-5 S*/
    public static void setInbulidThemePostfix(String postfix){
    	INBUILD_THME_POSTFIX = postfix;
    }
    public static String getInbuildThemePostfix(){
    	return INBUILD_THME_POSTFIX;
    }
    /*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-5 E*/
    /***RK_ID:RK_REMOVE_DOUBLE_CLICL_DELAY  AUT:zhanglz1@lenovo.com.DATE:2013-01-18. S***/        
    public static void enableLeVoice(boolean enable,Context context) {
    	mEnableLeVoice = enable;
    	sIsLeVoiceEnablerChanged = true;
    	Intent intent = new Intent(SettingsValue.ACTION_LEVOICE_ENABLE_CHANGE);
    	if(context!=null)
    	context.sendBroadcast(intent);
    }
    /***RK_ID:RK_REMOVE_DOUBLE_CLICL_DELAY  AUT:zhanglz1@lenovo.com.DATE:2013-01-18. E***/        
    public static boolean isLeVoiceEnabled(Context context) {
        if (!sIsLeVoiceEnablerChanged) {
        	sIsLeVoiceEnablerChanged = true;
        	SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        	mEnableLeVoice = mPreferences.getBoolean(PREF_AKEY_ENABLE_LEVOICE, true);
        }
        return mEnableLeVoice;
    }
    /*RK_MAGICGESTRUE_LEVOICE zhanglz1@lenovo.com 2012-11-21 E*/
    /*RK_SEMI_TRANSPARNET dining@lenovo.com 2012-10-16 S*/    
    public static int getApplistTransParentSettingValue(Context c){
    	   int default_intvalue = 65;
    	   if(c != null){
    		   if(sPrefTransparent == Integer.MIN_VALUE){
    			   SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
    			   sPrefTransparent = sharedPreferences.getInt(PREF_TARNSPARENT_VALUE, default_intvalue);
    		   }
    		   return sPrefTransparent;
    	   }
    	   return default_intvalue;
    
    }
    /*RK_SEMI_TRANSPARNET dining@lenovo.com 2012-10-16 E*/
    
    public static boolean getUseDefaultThemeIconValue(Context c){
 	   boolean default_value = true;
 	   if(c != null){
 		   SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
 		   boolean bTemp = sharedPreferences.getBoolean(PREF_USE_DEFAULTTHEME_ICON, default_value);
 		   return bTemp;
 	   }
 	   return default_value;
 
 }
    
    /*** RK_ID: APPLIST_LOOP.  AUT: zhaoxy . DATE: 2012-12-17 . START***/
    public static final String PREF_APPLIST_ICON_TEXT_BACKGROUND = "pref_applist_icon_text_background";
    public static final String ACTION_APPLIST_ICON_TEXT_BACKGROUND = "com.lenovo.action.applist_icon_text_background";
    public static final String EXTRA_APPLIST_ICON_TEXT_BACKGROUND = "com.lenovo.extra.applist_icon_text_background";
    
    public static final String PREF_APPLIST_LOOP = "pref_applist_loop";
    public static final String ACTION_APPLIST_LOOP = "com.lenovo.action.applist_loop";
    public static final String EXTRA_APPLIST_IS_LOOP = "com.lenovo.extra.applist_is_loop";
    public static void onApplistIconTextBackgroundChange(Context c, Object newValue) {
        if (newValue instanceof Boolean) {
            boolean applistIconTextBackgroundEnable = (Boolean) newValue;
            Intent i = new Intent(SettingsValue.ACTION_APPLIST_ICON_TEXT_BACKGROUND);
            i.putExtra(SettingsValue.EXTRA_APPLIST_ICON_TEXT_BACKGROUND, applistIconTextBackgroundEnable);
            c.sendBroadcast(i);
        }
    }
    
    public static boolean isApplistIconTextBackgroundEnable(Context c) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        return sharedPreferences.getBoolean(PREF_APPLIST_ICON_TEXT_BACKGROUND, true);
    }
    public static void onApplistLoopChange(Context c, Object newValue) {
        if (newValue instanceof Boolean) {
            boolean applistSlideLoop = (Boolean) newValue;
            Intent i = new Intent(SettingsValue.ACTION_APPLIST_LOOP);
            i.putExtra(SettingsValue.EXTRA_APPLIST_IS_LOOP, applistSlideLoop);
            c.sendBroadcast(i);
        }
    }
    
    public static boolean isApplistLoop(Context c) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        return sharedPreferences.getBoolean(PREF_APPLIST_LOOP, true);
    }
    /*** RK_ID: APPLIST_LOOP.  AUT: zhaoxy . DATE: 2012-12-17 . END***/

    static int sCurrentCellLayout = -1;
    static boolean sWorkspaceCustom = false;

    public static void setCurrentCellLayout(int screen, boolean custom) {
        sCurrentCellLayout = screen;
        sWorkspaceCustom = custom;
    }
    public static int getCurrentCellLayout() {
        return sCurrentCellLayout;
    }
    public static boolean isWorkspaceCustom() {
        return sWorkspaceCustom;
    }
    
    public static String isSuitableTheme(Context context, String packageName) {
    	if (context == null || packageName == null) {
    		return DEFAULT_THEME_VERSION;
    	}
    	float versionCode = 1.0f;
    	int launcherVersion = 1;
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(packageName, 0);
			if (pi != null) {
				versionCode = pi.versionCode;
			}
			
			PackageInfo launcherPI = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			if (launcherPI != null) {
				String versionName = launcherPI.versionName;
				if (versionName != null) {
					versionName = versionName.toLowerCase();
					int index1 = versionName.indexOf(".");
					versionName = versionName.substring(1, index1);
					android.util.Log.i("dooba", ".............xxxxxxxx....." + versionName);
					launcherVersion = Integer.parseInt(versionName);
				}
			}
		} catch (NameNotFoundException e) {
			Debug.printException("The packageName cannot be resolved-->", e);
		}
		float tmp = launcherVersion * 100;
		if (versionCode >= (tmp - 99) && versionCode <= tmp) {
			return null;
		}
		return "v" + launcherVersion + ".x";
    }
    /***RK_ID:RK_AUTO_NEW AUT:zhanglz1@lenovo.com DATE: 2013-04-17 S***/ 
    public static final String[] newtagvalues = {
    	// "050000"//小部件设置不可以有 因为他不是preference
//    	"080000",
//    	"090100"
    	};
    public static int SESIONER_SETTING_INDEX = 6;
	public static boolean getNewTagPreference(Context c, String num) {
		// TODO Auto-generated method stub
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(c);
		boolean isNewTag = sharedPreferences.getBoolean(num, true);
		//if(num == "000000") isNewTag = false;
		return isNewTag;
	}
	public static void setAllNewTag(Context c, String num,boolean value) {
		// TODO Auto-generated method stub
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(c);
		Editor e = sharedPreferences.edit();
		e.putBoolean(num, value);
		e.commit();
	}
	public static boolean getAllNewTagPreference(Context c) {
		// TODO Auto-generated method stub
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(c);
		boolean isNewTag = false;
		for (int index1 = 0; index1 < SettingsValue.newtagvalues.length; index1++) {
			final String num = SettingsValue.newtagvalues[index1];
			isNewTag = sharedPreferences.getBoolean(num, true);
			//if(num == "000000") isNewTag = false;
			if(isNewTag) return true;
		}
		return false;
	}
	private static ArrayList<Boolean> excute = new ArrayList<Boolean>();
	private static boolean hasChildNew(Context context,int newtagindex, String num) {
		// TODO Auto-generated method stub
		boolean hasChildNew = false;
        //最后一级了 如果不是new 那就应该返回false
		for (int index1 = 0; index1 < SettingsValue.newtagvalues.length; index1++) {
			final String numA = SettingsValue.newtagvalues[index1];
			if (num.substring(0, 2 * (newtagindex)).equals(
					numA.substring(0, 2 * (newtagindex)))) {
				hasChildNew = (SettingsValue.getNewTagPreference(context, numA));
				if(hasChildNew) return true;
			}
		}
		return hasChildNew;
	}
	private static int index = 0 ;
	public static void addorRemoveAllNewTag(final PreferenceActivity context,final int newtagindex,
			String orderInParent) {
		// TODO Auto-generated method stub
		int i = 0;
		index=0;
		excute = new ArrayList<Boolean>();
		for (index = 0; index < SettingsValue.newtagvalues.length; index++) {
			
			excute.add(index, true);
			final String num = SettingsValue.newtagvalues[index];
			i = Integer.valueOf(num.substring(2 * (newtagindex - 1),
					2 * newtagindex));
			if (i == 0)
				continue;
			Preference temp = context.getPreferenceScreen()
					.getPreference(i - 1);
			if (temp == null)
				continue;
			
			boolean isShow = false;

			if(orderInParent == null) return;
			if(newtagindex != 1 && orderInParent.length()!=2*(newtagindex-1)) continue;

			if(num.substring(0, 2*(newtagindex-1)).equals(orderInParent.substring(0, 2*(newtagindex-1)))){ 
				isShow = true;
			}
            boolean hasChildNew = hasChildNew(context,newtagindex,num);
			if (isShow && (hasChildNew || SettingsValue.getNewTagPreference(context, num))){
				int order = temp.getOrder();
				context.getPreferenceScreen().removePreference(temp);
				temp.setLayoutResource(R.layout.custom_preference_new);
				temp.setOrder(order);
				context.getPreferenceScreen().addPreference(temp);
				temp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						// TODO Auto-generated method stub
						int indextemp = getIndexbyNum(num);
						
						if (indextemp!=-1 && excute.get(indextemp)) {
							
							if (newtagindex<3 
									&& (((2 * (newtagindex + 1)) <= num.length() 
									&&Integer.valueOf(num.substring(2 * newtagindex,2 * (newtagindex + 1))) == 0) 
									|| num.length() == (2 * newtagindex))
									|| newtagindex == 3) {
								excute.set(indextemp, false);
								preference.getOnPreferenceClickListener()
										.onPreferenceClick(preference);
								SettingsValue.setAllNewTag(context, num, false);
							//	SettingsValue.newtagvalues[indextemp] = "000000";
								int order = preference.getOrder();
								context.getPreferenceScreen().removePreference(
										preference);
								preference
										.setLayoutResource(R.layout.custom_preference);
								preference.setOrder(order);
								context.getPreferenceScreen().addPreference(
										preference);
							}
						}
						return false;
					}
				});
			}else{
                  
				int order = temp.getOrder();
				context.getPreferenceScreen().removePreference(temp);
				temp.setLayoutResource(R.layout.custom_preference);
				temp.setOrder(order);
				context.getPreferenceScreen().addPreference(temp);
			}
		}
	}
	private static int getIndexbyNum(String num) {
		// TODO Auto-generated method stub
		for(int i=0;i<newtagvalues.length;i++){
			if(newtagvalues[i] == num) return i;
		}
		return -1;
	}
	/***RK_ID:RK_AUTO_NEW AUT:zhanglz1@lenovo.com DATE: 2013-04-17 E***/ 
	private SettingsValue() {
		// TODO Auto-generated constructor stub
	}
	
	public static String[] RES_ICON_SIZE_VALUES;
	public static String RES_DEFAULT_ANDROID_THEME;
	public static int RES_ICON_TEXTURE_SIZE = 1;
	//The packageName of the context must be launcher
	public static void initImportantValues(Context context) {
		Resources res = context.getResources();
		RES_ICON_SIZE_VALUES = res.getStringArray(R.array.pref_icon_size_values);
		RES_DEFAULT_ANDROID_THEME = res.getString(R.string.pref_default_theme);
		RES_ICON_TEXTURE_SIZE = res.getDimensionPixelSize(R.dimen.app_icon_texture_size);
		/*** RK_ID: HIGH_QUALITY_EFFECTS. AUT: zhaoxy. DATE: 2013-10-16 . START***/
		SharedPreferences sharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
		ENABLE_HIGH_QUALITY_EFFECTS = sharedPreferences.getBoolean(PREF_ENABLE_HIGH_QUALITY_EFFECTS, true);
		/*** RK_ID: HIGH_QUALITY_EFFECTS. AUT: zhaoxy. DATE: 2013-10-16 . END***/
	}
	
    public static boolean getSingleLayerValue(Context c) {
        if (!mSingleLayerValue) {
        	mSingleLayerValue = true;
            SharedPreferences sharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(c);
            mSingleLayerValue = sharedPreferences.getBoolean(PREF_SINGLE_lAYER, true);
        }
        return mSingleLayerValue;
    }
    public static void SetSingleLayerValue(boolean singleLayerValue ) {
    	mSingleLayerValue = singleLayerValue;
    	mSingleLayerValue = true;
}    
        /*
            -1 // phone
            0  //7 inch pad
            1  //10 inch pad
        */
        public static int getCurrentMachineType(Context context){
            if(context == null) return -1;
            Resources res = context.getResources();
            int phoneindex = res.getInteger(R.integer.config_machine_type);
            Log.d("PAD", " current machine type phoneindex ==============================="+phoneindex);
            return phoneindex;
        }
        public static boolean isCurrentPortraitOrientation(Context context){
            if(context == null) return true;
            boolean flag = true;
            int mCurrentOrientation = context.getResources().getConfiguration().orientation;

            if (mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                flag = false;
            }else if(mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT){
                flag = true;
            }
            Log.d("PAD", " current orientation ==============================="+flag);
            return flag;
        }
        public static boolean isRotationEnabled(Context context) {
            if(context == null) return false;
            boolean enableRotation = context.getResources().getBoolean(R.bool.allow_rotation);
            return enableRotation;
        }

    public static boolean isContainsThemeCenter(Context context){
        if(context == null) return false;
        if(getCurrentMachineType(context) > -1) return false;
        if(getCurentVersionName( context,"com.lenovo.themecenter")) return false;
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.setPackage("com.lenovo.themecenter");
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> apps = null;
        apps = packageManager.queryIntentActivities(mainIntent, 0);
        if(apps.size() > 0) return true;
        return false;
    }
    public static boolean isBlade8Pad(){
        String deviceModel  = android.os.Build.MODEL;
        Log.d("PAD", " current ========appVersion="+deviceModel);
        if(deviceModel == null) return false;
        if(deviceModel.contains("B6000")){
            return true;
        }else{
            return false;
        }
    }
    public static boolean isWifiProduct(){
      String s = SystemProperties.get("ro.build.product");
      Log.e("yumin0820","getSystemProperites="+s);
      if(s != null && s.contains("wifi")){
          return true;
      }
      return false;
    }



    private static boolean getCurentVersionName( Context context,String packageName){
        final PackageManager manager = context.getPackageManager();
	try { PackageInfo info = manager.getPackageInfo(packageName, 0);
	    String appVersion = info.versionName;   //版本名
            float versionnum = Float.valueOf(appVersion.substring(0,3));
            Log.d("PAD", " current ========appVersion="+appVersion+" code="+info.versionCode+" int num="+versionnum);
            if(versionnum < 1.0){
                return true;
            }
	} catch (Exception e) {
	    return false;
	}
	return false;
    }
	
	//之所以写两个是因为接口文档写错了，为了能支持此两种。加上了如下判断。
	public static String LAUNCHER_NOTIFICATION_CATEGORY = "android.intent.category.LENOVO_LAUNCHER_NOTIFICAITON";
	public static String LAUNCHER_NOTIFICATION_CATEGORY2 = "android.intent.category.LENOVO_LAUNCHER_NOTIFICATION";

    public static boolean isLauncherNotification(Intent intent) {
        if (intent.hasCategory(SettingsValue.LAUNCHER_NOTIFICATION_CATEGORY)
                || intent.hasCategory(SettingsValue.LAUNCHER_NOTIFICATION_CATEGORY2))
        {
            return true;
        }
        
        return false;
    }
    
    public static boolean isLauncherNotification(IntentFilter filter) {
        if (filter.hasCategory(SettingsValue.LAUNCHER_NOTIFICATION_CATEGORY)
            || filter.hasCategory(SettingsValue.LAUNCHER_NOTIFICATION_CATEGORY2))
        {
            return true;
        }
        
        return false;
    }
    
    
    private static final String MAXCOUNT = "MAX_CELLCOUNT";
    private static final String SCREENREFERENCE = "com.lenovo.launcher2.PreviewScreenPagedView";
    
    public static int getLauncherScreenMaxCount(Context c){
    	boolean isSingle = getSingleLayerValue(c);
    	int defaulValue = isSingle?18:9;
    	int max_Count = c.getSharedPreferences(SCREENREFERENCE, Context.MODE_PRIVATE).getInt(MAXCOUNT, defaulValue);
    	if(!isSingle){
    		return Math.min(max_Count,defaulValue);
    	}else{
    		return Math.min(max_Count, defaulValue);
    	}
    }
    public static final String PREF_WORKSPACE_SHADOW = "pref_workspace_icon_shadow";
    public static boolean isShadow(Context c) {
    	 SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
         boolean isShadow = sharedPreferences.getBoolean(PREF_WORKSPACE_SHADOW, false);
         return isShadow;
    }
    
    public static final String PREF_FIRST_LOAD_LBK ="pref_first_load_lbk";
    public static boolean isFirstLoadLbk(Context c){
    	SharedPreferences sharedPreferences = c.getSharedPreferences(ConstantAdapter.PREF_VERSION_LAUNCH_OLD_NAME,
				Activity.MODE_APPEND | Activity.MODE_PRIVATE);
    	boolean isFirst =  sharedPreferences.getBoolean(PREF_FIRST_LOAD_LBK, false);
    	SharedPreferences.Editor editor = sharedPreferences.edit();
    	editor.remove(PREF_FIRST_LOAD_LBK);
    	editor.commit();
    	return isFirst;
    }
    
    public static void setFirstLoadLbk(Context context) {
    	SharedPreferences sp = context.getSharedPreferences(ConstantAdapter.PREF_VERSION_LAUNCH_OLD_NAME,
				Activity.MODE_APPEND | Activity.MODE_PRIVATE);
   	    SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(PREF_FIRST_LOAD_LBK, true).putBoolean(ConstantAdapter.EXCLUDED_SETTING_KEY, true).commit();
    }
    
    //add by zhanggx1 for new layout.s
    public static final String PREF_HAS_EXTRA_TOP_MARGIN = "pref_has_extra_top_margin";//0是没有，1是有
    private static int mExtraTopMargin = Integer.MIN_VALUE;
    private static boolean hasExtraTopMargin = false;
    private static int mStatusBarHeight = Integer.MIN_VALUE;
    public static int initExtraTopMargin(final Context context) {
    	if (mExtraTopMargin == Integer.MIN_VALUE) {
    		int statusBarHeight = getStatusBarHeight(context);
    		SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    		int hasExtraMargin = mPreferences.getInt(PREF_HAS_EXTRA_TOP_MARGIN, 1);
    		mExtraTopMargin = (hasExtraMargin == 0) ? 0 : statusBarHeight;
    		hasExtraTopMargin = mExtraTopMargin != 0;
    	}
    	return mExtraTopMargin;
    }

    public static int getExtraTopMargin() {
        return mExtraTopMargin;
    }
    
    public static int getStatusBarHeight(Context context) {
    	if (mStatusBarHeight == Integer.MIN_VALUE) {
    		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
            	mStatusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
            } else {
            	mStatusBarHeight = (int)(25 * context.getResources().getDisplayMetrics().density);
            }
    	}
        
        return mStatusBarHeight;
    }
    
    public static boolean hasExtraTopMargin() {
//    	if (mExtraTopMargin == Integer.MIN_VALUE) {
//    		getExtraTopMargin(context);
//    	}
    	return hasExtraTopMargin;
    }
    //add by zhanggx1 for new layout.e
    
    /*RK_default_icon_style dining@lenovo.com 2013-9-12 S*/
    public static boolean getUseDefaultThemeIconStyle(Context c) {
		boolean default_value = true;	
		if(c != null){
		    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
		    boolean bTemp = sharedPreferences.getBoolean(PREF_USE_DEFAULT_ICON_STYLE, default_value);
		    return bTemp;
		}
		   
		return default_value;
	}
   /*RK_default_icon_style dining@lenovo.com 2013-9-12 E*/

    /*** RK_ID: HIGH_QUALITY_EFFECTS. AUT: zhaoxy. DATE: 2013-10-16 . START***/
    public static boolean ENABLE_HIGH_QUALITY_EFFECTS = true;
    private static final String PREF_ENABLE_HIGH_QUALITY_EFFECTS = "pref_enable_high_quality_effects";
    /*** RK_ID: HIGH_QUALITY_EFFECTS. AUT: zhaoxy. DATE: 2013-10-16 . END***/
	//bugfix orochi-2903 zhanglz1 20131201 s
    public static String lbkdefaultthemeincase = null;
	public static String getlbkdefaultthemeincase() {
		// TODO Auto-generated method stub
		return lbkdefaultthemeincase;
	}
	public static void setlbkdefaultthemeincase(String s) {
		// TODO Auto-generated method stub
		lbkdefaultthemeincase = s;
	}
	//bugfix orochi-2903 zhanglz1 20131201 e
	
	 //add by zhanggx1 for reordering all pages on 2013-11-20. s
    public static final String PREF_AUTO_REORDER = "pref_auto_reorder";
    public static final String ACTION_DO_AUTO_REORDER = "com.lenovo.launcher.intent.ACTION_DO_AUTO_REORDER";
    private static boolean mEnableAutoReorder = true;//自动排列图标功能是否开启
    private static boolean sAutoReorderSync = false;
    
    public static void enableAutoReorder(boolean enable) {
    	mEnableAutoReorder = enable;
    	sAutoReorderSync = true;
    }
    
    public static boolean isAutoReorderEnabled(Context c) {
        if (!sAutoReorderSync) {
        	sAutoReorderSync = true;
            SharedPreferences sharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(c);
            mEnableAutoReorder = sharedPreferences.getBoolean(PREF_AUTO_REORDER, false);
        }
        return mEnableAutoReorder;
    }
    //add by zhanggx1 for reordering all pages on 2013-11-20. e

}
