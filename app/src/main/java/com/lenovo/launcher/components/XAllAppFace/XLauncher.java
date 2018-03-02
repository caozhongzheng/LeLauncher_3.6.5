package com.lenovo.launcher.components.XAllAppFace;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDiskIOException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.preference.PreferenceManager;
import android.provider.CallLog.Calls;
import android.provider.Settings;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Advanceable;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.XDropTarget.XDragObject;
import com.lenovo.launcher.components.XAllAppFace.XFolder.OnXFolderStateLinstener;
import com.lenovo.launcher.components.XAllAppFace.XFolderIcon.FolderRingAnimator;
import com.lenovo.launcher.components.XAllAppFace.slimengine.BaseDrawableGroup;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem.OnLongClickListener;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.LGestureDetector;
import com.lenovo.launcher.components.XAllAppFace.slimengine.NormalDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;
import com.lenovo.launcher.components.XAllAppFace.utilities.CubicInterpolator;
import com.lenovo.launcher.components.XAllAppFace.utilities.QuadInterpolator;
import com.lenovo.launcher.components.XAllAppFace.utilities.QuartInterpolator;
import com.lenovo.launcher.components.XAllAppFace.utilities.Utilities;
import com.lenovo.launcher2.addleoswidget.LenovoWidgetsProviderInfo;
import com.lenovo.launcher2.addon.classification.AppCategoryBehavior;
import com.lenovo.launcher2.addon.classification.AppsClassificationData;
import com.lenovo.launcher2.addon.classification.AppsClassifiction;
import com.lenovo.launcher2.addon.classification.GetAppsCategory;
import com.lenovo.launcher2.addon.gesture.GestureManager;
import com.lenovo.launcher2.backup.BackupManager;
import com.lenovo.launcher2.bootpolicy.BootPolicyUtility;
import com.lenovo.launcher2.bootpolicy.LoadBootPolicy;
import com.lenovo.launcher2.commoninterface.ApplicationInfo;
import com.lenovo.launcher2.commoninterface.DeferredHandler;
import com.lenovo.launcher2.commoninterface.FolderInfo;
import com.lenovo.launcher2.commoninterface.HiddenApplist;
import com.lenovo.launcher2.commoninterface.IconCache;
import com.lenovo.launcher2.commoninterface.ItemInfo;
import com.lenovo.launcher2.commoninterface.LauncherAppWidgetInfo;
import com.lenovo.launcher2.commoninterface.LauncherService;
import com.lenovo.launcher2.commoninterface.LauncherSettings;
import com.lenovo.launcher2.commoninterface.LenovoWidgetViewInfo;
import com.lenovo.launcher2.commoninterface.LeosItemInfo;
import com.lenovo.launcher2.commoninterface.PendingAddWidgetInfo;
import com.lenovo.launcher2.commoninterface.ShortcutInfo;
import com.lenovo.launcher2.commoninterface.UsageStatsMonitor;
import com.lenovo.launcher2.commonui.LauncherAppWidgetHost;
import com.lenovo.launcher2.commonui.LeAlertDialog;
import com.lenovo.launcher2.commonui.LeDialog;
import com.lenovo.launcher2.commonui.LeProcessDialog;
import com.lenovo.launcher2.commonui.MenuGridView;
import com.lenovo.launcher2.customizer.ConstantAdapter;
import com.lenovo.launcher2.customizer.ConstantAdapter.OperationState;
import com.lenovo.launcher2.customizer.Constants;
import com.lenovo.launcher2.customizer.Debug;
import com.lenovo.launcher2.customizer.Debug.R2;
import com.lenovo.launcher2.customizer.Debug.R5;
import com.lenovo.launcher2.customizer.ExternalCommander;
import com.lenovo.launcher2.customizer.Reaper;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.ShadowUtilites;
import com.lenovo.launcher2.customizer.ThemeSimpleAdapter;
import com.lenovo.launcher2.customizer.VersionUpdateSUS;
import com.lenovo.launcher2.gadgets.GadgetUtilities;
import com.lenovo.launcher2.gadgets.Lotus.LotusUtilites;
import com.lenovo.launcher2.menu.WorkspaceMenuDialog;
import com.lenovo.launcher2.weather.widget.WidgetReceiver;
import com.lenovo.launcher2.weather.widget.settings.FetchLenovoWeatherWidgetUtil;
import com.lenovo.launcher2.weather.widget.settings.WeatherWidgetPosInfo;
import com.lenovo.launcher2.weather.widget.utils.WeatherUtilites;
import com.lenovo.lejingpin.hw.content.data.HwConstant;
import com.lenovo.lejingpin.magicdownloadremain.MagicDownloadControl;
import com.lenovo.lejingpin.share.download.DownloadConstant;

public class XLauncher extends Activity implements XLauncherModel.Callbacks, OnLongClickListener, com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem.OnClickListener, OnXFolderStateLinstener {
	private final String ACTION_SCENE_APPLY = "com.lenovo.launcher.tools.Intent.ACTION_SCENE_APPLY";
	private XLauncherView mMainSurface;
	private XDragLayer mDragLayer;
	private XApplistView mAppListView;
	private XWorkspace mWorkspace;
	private XScreenMngView mScreenMngView;
	private XWallpapperBlur mWallpapperBlur;
	private XDragController mDragController;
	 /*RK_ID: RK_SHOW_MENU Bug 9941 AUT:liuyg1@lenovo.com DATE: 2013-03-27 START*/
    private static final int MENU_WALLPAPER_SETTINGS = Menu.FIRST + 1;
    private static final int MENU_MANAGE_APPS = MENU_WALLPAPER_SETTINGS + 1;
    private static final int MENU_SYSTEM_SETTINGS = MENU_MANAGE_APPS + 1;
    private static final int MENU_HELP = MENU_SYSTEM_SETTINGS + 1;
	 /*RK_ID: RK_SHOW_MENU Bug 9941 AUT:liuyg1@lenovo.com DATE: 2013-03-27 END*/

	private XBlackboard mBlackboard;
	
	private XLauncherModel mModel;
	private IconCache mIconCache;
	
	private Dialog mLeosDialog = null;
	private Dialog mWorkspaceMenuDialog = null;
	private LeAlertDialog mLeAlertDialog = null;
        /* RK_ID: RK_SONAR  AUT: yumina. DATE: 2013-06-28 . begin **/
	//private Dialog mApplistMenuDialog = null;
	private LeProcessDialog mProgressDlg = null;//private Dialog mProgressDlg;
	
	private int[] menuname_array = { R.string.menu_add, R.string.menu_wallpaper,
            R.string.menu_theme_settings, R.string.menu_personal_settings,
            R.string.menu_desktop_settings,R.string.menu_settings };
	private int[] menuimage_array = { R.drawable.main_menu_addprogram, R.drawable.main_menu_wallpaper,
            R.drawable.main_menu_themesetting,  R.drawable.main_menu_personal,
            R.drawable.desk_setting,  R.drawable.main_menu_setting };
	
    private int[] appmenuname_array = {R.string.app_order_settings, 
    		R.string.applist_hiddenlist_settings_title, 
    		R.string.header_category_applist };
    private int[] appmenuimage_array = {R.drawable.main_menu_sort, 
    		R.drawable.main_menu_hideapp, 
    		R.drawable.main_menu_applist};
    
	private enum State { WORKSPACE, APPS_CUSTOMIZE, APPS_CUSTOMIZE_SPRING_LOADED };	
	private State mState = State.WORKSPACE;
	
	private static final int DLGMENU_ADD = 0;
    private static final int DLGMENU_WALLPAPER_SETTINGS = DLGMENU_ADD + 1;
    private static final int DLGMENU_THEME_SETTINGS = DLGMENU_WALLPAPER_SETTINGS + 1;
    private static final int DLGMENU_PRESONAL_SETTINGS = DLGMENU_THEME_SETTINGS + 1;
    private static final int DLGMENU_DESKTOP_SETTINGS = DLGMENU_PRESONAL_SETTINGS + 1;
    private static final int DLGMENU_SYSTEM_SETTINGS = DLGMENU_DESKTOP_SETTINGS + 1;
    
    private static final int DLGMENU_ORDER = 0;
    private static final int DLGMENU_HIDE_APP = DLGMENU_ORDER + 1;
    private static final int DLGMENU_APPS_SETTINGS = DLGMENU_ORDER + 2;
    
    static final int DIALOG_CREATE_SHORTCUT = 1;
    static final int DIALOG_RENAME_FOLDER = 2;
//    static final int DIALOG_LOAD_WORKSAPCE =DIALOG_RENAME_FOLDER +1; 
    
    public boolean mWaitingForResult;
    
    private AppWidgetManager mAppWidgetManager;
    private LauncherAppWidgetHost mAppWidgetHost;
    public static final int APPWIDGET_HOST_ID = 1024;
    
    public static final int REQUEST_CREATE_SHORTCUT = 1;
    public static final int REQUEST_CREATE_APPWIDGET = 5;
    private static final int REQUEST_PICK_APPLICATION = 6;
    private static final int REQUEST_PICK_SHORTCUT = 7;
    private static final int REQUEST_PICK_APPWIDGET = 9;
    private static final int REQUEST_PICK_WALLPAPER = 10;
    /*RK_ID: RK_DOCK_ADD . AUT: zhanggx1 . S*/
    private static final int REQUEST_PICK_HOTSEAT_APPLICATIONS = 12;
    private static final int REQUEST_PICK_APPS_WALLPAPER = 15;
    /*RK_ID: RK_DOCK_ADD . AUT: zhanggx1 . E*/
    /* AUT: liuli liuli0120@use.com.cn DATE: 2012-01-10 START */
    // for quick action replace icon
    public final static int REQUEST_CODE_CHANGE_PICTURE = 13;
    public final static int REQUEST_CODE_CHANGE_THEME_ICON = 14;
    /* AUT: liuli liuli0120@use.com.cn DATE: 2012-01-10 END */

    /* AUT: xingqx xingqx@lenovo.com DATE: 2012-01-19 START */
    // for show add widget list dialog
    private static final int REQUEST_PICK_APPWIDGET_LIST = 21;
    /* AUT: liuli1 DATE: 2012-02-10 START */
    private final static int REQUEST_PICK_MOREAPPLICATION = 22;
    /* AUT: liuli1 DATE: 2012-02-10 END */
    
    public final static int REQUEST_CODE_ADD_APP_TO_CATEGORY = 23;
/*RK_ID: RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-09-26START */
    private static final int REQUEST_PICK_LEOSAPPWIDGET_LIST = 24;
/*RK_ID: RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-09-26END */
    public final static int REQUEST_CONFIRM_NETWORK_ENABLED = 25;
    
    public final static int REQUEST_CONFIRM_NETWORK_ENABLED_WALLPAPER = 26;
 /* RK_ID:MENU_DIALOG_ACTIVITY. AUT:liuyg1 . DATE:2013-3-14. START*/       
public final static int  REQUEST_CODE_SHOW_AMENUDILAOG = 27;
    public final static int  REQUEST_CODE_SHOW_WMENUDILAOG = 28;
 /* RK_ID:MENU_DIALOG_ACTIVITY. AUT:liuyg1 . DATE:2013-3-14. END*/  
/* RK_ID:RK_BOOT_POLICY_DIALOG. AUT:liuyg1 . DATE:2013-3-21. START*/  
    public final static int  REQUEST_CODE_FIRST_LOAD_PROFILE = 29;
    public final static int  REQUEST_CODE_LOAD_UPDATE_PROFILE = 30;
 /* RK_ID:RK_BOOT_POLICY_DIALOG. AUT:liuyg1 . DATE:2013-3-21. END*/  
    public static final String EXTRA_SHORTCUT_DUPLICATE = "duplicate";
    
	static final String TAG = "XLauncher";
	private boolean mWorkspaceLoading = true;
    private boolean mPaused = true;
    private boolean mOnResumeNeedsLoad;
    static final int SCREEN_COUNT = 5;
    
    private int[] mTmpAddItemCellCoordinates = new int[2];
    private boolean mRestoring;
    
    private ArrayList<Point> mEmptyPointList = null;
    private int loadIndex = 0;
    private int maxLoadIndex = 0;
        
    private Bundle mSavedState;
    private ItemInfo mPendingAddInfo = new ItemInfo();
    private static HashMap<Long, FolderInfo> sFolders = new HashMap<Long, FolderInfo>();
    private FolderInfo mFolderInfo;
    private Bundle mSavedInstanceState;
    private boolean mProfileEnabled;
    
    LauncherService mLauncherService = LauncherService.getInstance();
    static final boolean DEBUG_WIDGETS = true;
    private HashMap<View, AppWidgetProviderInfo> mWidgetsToAdvance =
            new HashMap<View, AppWidgetProviderInfo>();
    
 // Type: int
    private static final String RUNTIME_STATE_CURRENT_SCREEN = "launcher.current_screen";
    // Type: int
    private static final String RUNTIME_STATE = "launcher.state";
    // Type: int
    private static final String RUNTIME_STATE_PENDING_ADD_CONTAINER = "launcher.add_container";
    // Type: int
    private static final String RUNTIME_STATE_PENDING_ADD_SCREEN = "launcher.add_screen";
    // Type: int
    private static final String RUNTIME_STATE_PENDING_ADD_CELL_X = "launcher.add_cell_x";
    // Type: int
    private static final String RUNTIME_STATE_PENDING_ADD_CELL_Y = "launcher.add_cell_y";
    // Type: boolean
    private static final String RUNTIME_STATE_PENDING_FOLDER_RENAME = "launcher.rename_folder";
    // Type: long
    private static final String RUNTIME_STATE_PENDING_FOLDER_RENAME_ID = "launcher.rename_folder_id";

    private static final String TOOLBAR_ICON_METADATA_NAME = "com.lenovo.launcher.toolbar_icon";

	private static final String CELLLAYOUT_COUNT = "com.lenovo.launcher2.celllayoutCount";
	private boolean[][] mOccupiedTmp;
	
	private static final int MENU_GROUP_WALLPAPER = 1;
	
	private final int ADVANCE_MSG = 1;
	private static final int MSG_REMOVE_SHORTCUTS = 1101;
    private static final int MSG_MOVE_SHORTCUTS_TO_FOLDER = 1102;
    private static final int MSG_SEND_THEME_APPLING = 1103;
    private static final int  DISMISS_APPLY_PROGRESS = 1104;
    private final int APPLIST_FLAG = 0x10;
    private final int WORKSPACE_FLAG = 0x01;
    
    private static final int MSG_DELAY_BUILDBLURBITMAP = 99;
    private static final int MSG_ADD_DEFWIDGET = 100;
    private static final int MSG_ADD_EXISTFOLDER = 101;
    private static final int MSG_RESET_WORKSPACE = 102;
    private static final int MSG_REFRESH_WORKSPACE = 103;
    
    // FOR SENSOR REGISTER AND UNREGISTER
    private static final int MSG_GSENSOR_REGISTER = 105;
    private static final int MSG_GSENSOR_UNREGISTER = 106;
    private static final String PREFS_KEY = "com.lenovo.launcher2.prefs";

	private static class PendingAddArguments {
		int requestCode;
		Intent intent;
		long container;
		int screen;
		int cellX;
		int cellY;
	}
	
	private boolean workspace_occupied_needsave = false;

    private XHotseat mHotseat;
    private AllAppSortHelper mAllAppSortHelper;
    private SpannableStringBuilder mDefaultKeySsb = null;
    private AllApplicationsThread mApplicationThread;
    private final BroadcastReceiver mCloseSystemDialogsReceiver
            = new CloseSystemDialogsIntentReceiver();
    
    private SettingsChangedReceiver mSettingsChangedReceiver;
    private WidgetReceiver mWidgetReceiver;
    
    private boolean mVisible = false;
    private boolean mAttached = false;
    
    public enum DetailState { NORMAL, ICONSTYLEAPPLING, THEMEAPPLING, SCENEAPPLING, SCENEBACKUP };
    
    private DetailState mDetailState = DetailState.NORMAL;
    private Object mThemeLock = new Object();
    private LauncherApplication mApp;
    static final int DEFAULT_SCREEN = 0;
    private static int sScreen = DEFAULT_SCREEN;
    private static final Object sLock = new Object();
    private Dialog mLoadWorkspaceDialog = null;

//    /*** RK_ID: APPS_CATEGORY. AUT: zhaoxy . DATE: 2013-03-21 . START ***/
//    private static final String MAXCOUNT = "MAX_CELLCOUNT";
//    private int MAX_CELLCOUNT = 9;
//    /*** RK_ID: APPS_CATEGORY. AUT: zhaoxy . DATE: 2013-03-21 . END ***/
    
    private ThemePkgChangedReceiver mThemePkgReceiver;
    
    private int mRestoreScreen = -1;
    private boolean isConfiguringWidget = false;
    
    private GestureManager mGestureManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SettingsValue.initExtraTopMargin(this);
		XShortcutIconView.dimenDirty = true;
		
		//add by zhanggx1 for new layout.s
		if (SettingsValue.hasExtraTopMargin()) {
			Window win = getWindow();
	        WindowManager.LayoutParams winParams = win.getAttributes();
	        winParams.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
	        win.setAttributes(winParams);
		}
		//add by zhanggx1 for new layout.e
		
		Log.i(TAG,"XLauncher.onCreate---"+ this);
		initAtTheBeginning();
		setContentView(R.layout.xlauncher);

		
        /* RK_ID:RK_BUG zhangdxa . DATE:2013-7-16. S*/
        mApp = ((LauncherApplication)getApplication());
        mModel = mApp.getModel();
        XLauncher launcher = mModel.getLauncher();
        Log.i("XLauncher","XLauncher.onCreate 2-----mModel.getLauncher():"+ launcher);
        if( launcher != null && launcher != this){
             mModel.resetLoad();
        }
        mApp.setLauncher(this);
        /* RK_ID:RK_BUG zhangdxa . DATE:2013-7-16. E*/
        
        mIconCache = mApp.getIconCache();
        
        mProfileEnabled = true;
        /* RK_ID:RK_BOOT_POLICY_DIALOG. AUT:liuyg1 . DATE:2013-3-21. START*/ 
        if (mProgressDlg != null && mProgressDlg.isShowing()) {
            Log.i("onresume", "showProgressDialog, dialog is showing ... ");
            return ;
        }
      if( LoadBootPolicy.getInstance(this).showFirstExperiencePolicyDialog(mHandler) ){
      	return;
      }
      if( LoadBootPolicy.getInstance(this).showUpdateExperiencePolicyDialog(mHandler) ){
        	return;
        }
//      else
//      	if(LoadBootPolicy.getInstance(this).showUpdateExperiencePolicyDialog(mHandler)){
//      	return;
//      }
     /* RK_ID:RK_BOOT_POLICY_DIALOG. AUT:liuyg1 . DATE:2013-3-21. END*/ 
        
        
        /** RK_ID: RK_BUG_174814. AUT: zhangdxa DATE: 2013-3-28 S */
        reloadThemeIfPowerOff();
        /** RK_ID: RK_BUG_174814. AUT: zhangdxa DATE: 2013-3-28 E */

        mSavedState = savedInstanceState;
        
		mMainSurface = (XLauncherView) findViewById(R.id.xlauncher_surface);
		
		mDragLayer = mMainSurface.getDragLayer();
		mAppListView = mMainSurface.getApplistView();
		mWorkspace = mMainSurface.getWorkspace();
		//add by zhanggx1 for reordering all pages on 2013-11-20. s
	    mWorkspace.setReorderingChangedListener(mReorderChangedListener);
		//add by zhanggx1 for reordering all pages on 2013-11-20. e	
		mWallpapperBlur = mMainSurface.getWallpapperBlur();
		mHotseat = mMainSurface.getHotseat();
		mScreenMngView = mMainSurface.getScreenMngView();
		mBlackboard = new XBlackboard(getXLauncherView(), mDragLayer, .4f);
		mDragController = mMainSurface.getDragController();
		
	    mAppWidgetManager = AppWidgetManager.getInstance(this);
	    mAppWidgetHost = new LauncherAppWidgetHost(this.getApplicationContext(), APPWIDGET_HOST_ID);
	    mAppWidgetHost.startListening();
	    
	    mApplicationThread = new AllApplicationsThread(mApp);
	    mAllAppSortHelper = new AllAppSortHelper(mApp);
	    
	    restoreState(mSavedState);
	    
	    setupViews();
	    
        if (!mRestoring) {
        	if(mLoadWorkspaceDialog == null){
        		mLoadWorkspaceDialog = creatLoadWokspaceDialog();
        		mLoadWorkspaceDialog.show();
        		Log.d("gecn1", "Load Worksapce dialog show   " );

        	}else{
        		Log.d("gecn1", "Load Worksapce dialog is showing");

        	}
//    		showDialog(DIALOG_LOAD_WORKSAPCE);
    		Log.d("gecn1", "Load Worksapce dialog show");
    		mHandler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
		    		Log.d("gecn1", "Load Worksapce dialog remove");
					if(mLoadWorkspaceDialog != null && mLoadWorkspaceDialog.isShowing()){
						Log.d("gecn1", "Load Worksapce dialog remove " + mLoadWorkspaceDialog);
						mLoadWorkspaceDialog.dismiss();
						mLoadWorkspaceDialog = null;
					}else if(mLoadWorkspaceDialog != null && !mLoadWorkspaceDialog.isShowing()){
						Log.d("gecn1", "Load Worksapce dialog remove not showing " + mLoadWorkspaceDialog);
						mLoadWorkspaceDialog = null;
					}else{
						Log.d("gecn1", "Load Worksapce dialog  is null" );
					}
		    		Log.d("gecn1", "Load Worksapce launcher " + XLauncher.this + "     pid = " +  android.os.Process.myPid());
				}
			}, 20*1000);
            mModel.startLoader(this, true);
        }
        
        mGestureManager = new GestureManager(this);
        
        // For handling default keys
        mDefaultKeySsb = new SpannableStringBuilder();
        Selection.setSelection(mDefaultKeySsb, 0);
        
        IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        filter.addAction(SettingsValue.ACTION_REMOVE_LOADING_DIALOG);
        registerReceiver(mCloseSystemDialogsReceiver, filter);
        
        mAppToPositionReceiver = new AppToPositionReceiver(); 
        filter.addAction(SettingsValue.ACTION_APP_TO_POSITION);
        registerReceiver(mAppToPositionReceiver, filter);
        
        mSettingsChangedReceiver = new SettingsChangedReceiver();
        IntentFilter settingsFilter = new IntentFilter(SettingsValue.ACTION_NETWORK_ENABLER_CHANGED);
        settingsFilter.addAction(SettingsValue.ACTION_TEXT_SIZE_CHANGED);
        settingsFilter.addAction(SettingsValue.ACTION_APP_EDIT_CHANGED);
        settingsFilter.addAction(SettingsValue.ACTION_INDICATOR_CHANGED);
        settingsFilter.addAction(SettingsValue.ACTION_START_WALLPAPER);
        settingsFilter.addAction(SettingsValue.ACTION_START_APPS_WALLPAPER);
        settingsFilter.addAction(SettingsValue.ACTION_RESET_APPS_WALLPAPER);
        settingsFilter.addAction(SettingsValue.ACTION_ICON_STYLE_INDEX_CHANGED);
        settingsFilter.addAction(SettingsValue.ACTION_PERSISTENT_CHANGED);
        settingsFilter.addAction(SettingsValue.ACTION_LOAD_WIDGET_SETTINGS);
        settingsFilter.addAction(SettingsValue.ACTION_CELLY_CHANGED);
//        settingsFilter.addAction(SettingsValue.ACTION_SCREEN_CELLCOUNT_CHANGED);
        settingsFilter.addAction(SettingsValue.ACTION_WORKSPACE_CLEANUP);
        settingsFilter.addAction(SettingsValue.ACTION_TEXT_BACKGROUND_ONOFF);
        
        // for settings
        settingsFilter.addAction(SettingsValue.ACTION_WORKSPACE_LOOP);
        settingsFilter.addAction(SettingsValue.HAWAII_PAGE_INVALIDATE);
        // fix bug 171611
        settingsFilter.addAction(SettingsValue.ACTION_DATA_ACQU_ENABLER_CHANGED);
        settingsFilter.addAction(SettingsValue.ACTION_APPLIST_LOOP);
//        settingsFilter.addAction(SettingsValue.ACTION_APPLIST_ICON_TEXT_BACKGROUND);
        
        settingsFilter.addAction(SettingsValue.ACTION_ICON_SIZE_CHANGED);
        settingsFilter.addAction("com.lenovo.action.BRIGTHNESS_CHANGED");
        settingsFilter.addAction(SettingsValue.ACTION_REFRESH_MNG_VIEW);
        settingsFilter.addAction(Intent.ACTION_WALLPAPER_CHANGED );
        settingsFilter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
        settingsFilter.addAction("action.lenovo.smartsidebar.hide");
      //add by zhanggx1 for reordering all pages on 2013-11-20. s
        settingsFilter.addAction(SettingsValue.ACTION_DO_AUTO_REORDER);
        //add by zhanggx1 for reordering all pages on 2013-11-20. e 

        registerReceiver(mSettingsChangedReceiver, settingsFilter);
        
        mWidgetReceiver = new WidgetReceiver();
        IntentFilter widgetFilter = new IntentFilter(SettingsValue.ACTION_NETWORK_ENABLER_CHANGED);
        widgetFilter.addAction(WeatherUtilites.ACTION_WEATHER_WIDGET_SEVICE_RESTART);
        widgetFilter.addAction(WeatherUtilites.ACTION_ADD_WEATHER_WIDGET);
        widgetFilter.addAction("com.lenovo.launcher.taskmanager.widget.clear");
        widgetFilter.addAction("com.lenovo.launcher.taskmanager.widget.add");
        widgetFilter.addAction(Intent.ACTION_TIME_CHANGED);
        widgetFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        widgetFilter.addAction(Intent.ACTION_TIME_TICK);
        widgetFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        widgetFilter.addAction(WeatherUtilites.ACTION_LOCATION_CHANGE);
        widgetFilter.addAction(WeatherUtilites.ACTION_WEATHER_ANIMATE_STOP);
        widgetFilter.addAction(WeatherUtilites.ACTION_WEATHER_ANIMATE_START);
        widgetFilter.addAction(WeatherUtilites.ACTION_WEATHER_NETWORK_STATE_CHANGE);
        widgetFilter.addAction(Intent.ACTION_SCREEN_OFF);
        widgetFilter.addAction(Intent.ACTION_SCREEN_ON);
        widgetFilter.addAction(WeatherUtilites.ACTION_DELETE_LEOS_WIDGET);
        widgetFilter.addAction("com.lenovo.launcher2.taskmanager.widget.whitelist_refresh");
        widgetFilter.addAction("android.intent.action.WORKSPACE_PAGE_UPDATE");
        registerReceiver(mWidgetReceiver, widgetFilter);
        
        mThemePkgReceiver = new ThemePkgChangedReceiver();
        IntentFilter themeFilter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        themeFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        themeFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        themeFilter.addDataScheme("package");
        registerReceiver(mThemePkgReceiver, themeFilter);
        
        
        mAppFlagReceiver = new AppFlagReceiver();
        IntentFilter flagFilter = new IntentFilter("com.android.intent.action.NEW_NOTIFICATION");
        registerReceiver(mAppFlagReceiver, flagFilter);
        
        /* RK_ID: RK_LELAUNCHER_REAPER_INIT. AUT: zhangdxa DATE: 2013-03-04 S*/
        initReaper();
        /* RK_ID: RK_LELAUNCHER_REAPER_INIT. AUT: zhangdxa DATE: 2013-03-04 E*/
        
        /* RK_ID: RK_LELAUNCHER_VERSION_UPDATE. AUT: zhangdxa DATE: 2013-03-04 S*/
        initAutoVersionUpdate();
        /* RK_ID: RK_LELAUNCHER_VERSION_UPDATE. AUT: zhangdxa DATE: 2013-03-04 E*/
        registerMissedContentObserver();
        if(!SettingsValue.isRotationEnabled(XLauncher.this)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        if(SettingsValue.getCurrentMachineType(this) == -1){
        	//getWindow().setWindowAnimations(R.style.LauncherActivityAnimation);
            getWindow().setBackgroundDrawableResource(R.drawable.statusbar_home_bottom_bg);
            
        }
	}
	
	private static final int DEFAULT_CELL_COUNT_X = 4;
    private static final int DEFAULT_CELL_COUNT_Y = 4;
	
	private void setupViews() {
		SharedPreferences preferences = getSharedPreferences(CELLLAYOUT_COUNT, Context.MODE_PRIVATE);
        int childCount = preferences.getInt(CELLLAYOUT_COUNT, 5);
        mLauncherService.mScreenCount = childCount;
        
        int cellCountX = DEFAULT_CELL_COUNT_X;
        int cellCountY = DEFAULT_CELL_COUNT_Y;
        String cellCount = SettingsValue.getScreenListCellArray(this);
        Log.d("liuyg1", "cellCount" + cellCount);
        if (cellCount.equals("")) {
             if(this.getDesplyheightPixels()>854){
                 cellCountY = 5;
             }
        } else {
            cellCountY = Integer.parseInt(cellCount.substring(0, 1));
            cellCountX = Integer.parseInt(cellCount.substring(2));
        }
        int phoneindex = SettingsValue.getCurrentMachineType(XLauncher.this);

        if(phoneindex == 0){//7 inch
            cellCountX = 6;
            cellCountY = 4;
        }else if(phoneindex == 1){//10 inch
            cellCountX = 6;
            cellCountY = 4;
        }

        XLauncherModel.updateWorkspaceLayoutCells(cellCountX, cellCountY);
        
        mWorkspace.init();       
        
//        if (mMainSurface != null) {
//            Log.w(TAG, "reset settings");
//            final SharedPreferences sharedPreferences = getSharedPreferences(PREFS_KEY,
//                    Context.MODE_PRIVATE);
//            if (!sharedPreferences.getBoolean(SettingsValue.KEY_RESET_SETTINGS, false)) {
//                mMainSurface.post(SettingsValue.resetToMySettings(this));
//            }
//        }
        /*** RK_ID: RK_FULLSCREEN_FOLDER  AUT: gecn1 & zhaoxy . DATE: 2012-05-23 . START***/
        mExpandDuration = getResources().getInteger(R.integer.config_folderAnimDuration);
        mScaleFolderAnimExtra = Float.valueOf(getResources().getString(R.string.config_folderAnimScaleString));
        getStatusBarHeight();
        mWallpapperBlur.setWallpaperPagedView((XWallpaperPagedView) mWorkspace.getPagedView());
        /*** RK_ID: RK_FULLSCREEN_FOLDER  AUT: gecn1 & zhaoxy . DATE: 2012-05-23 . END***/
	}
        /*** RK_ID: RK_PAD_SWTICH  AUT: yumina . DATE: 2013-06-13 . START***/
        @Override
        public void onConfigurationChanged(Configuration newConfig) {        	
            super.onConfigurationChanged(newConfig);
            
            // confirm phone ori
            confirmPhoneOrientation();
            
            Log.d(TAG,"onConfiguratonChanged");
            
            if (mHotseat != null) {
                mHotseat.refreshLocationPrepareForConfigeChange();
            }
            
            if (mWorkspace != null) {
                // fix bug 473, by liuli1.
                mWorkspace.getPagedView().restoreStage();
                XFolder folder = mWorkspace.getOpenFolder();
                if (folder != null) {
                    float y = (int) (mDragLayer.getHeight() - folder.getHeight());
                    folder.setRelativeY(y);
                }
            }
            closeFolderNow();
            mConfigureState = true;
            if( mWorkspace != null && mWorkspace.getPagedView() != null ){
            
            	     mWorkspace.getPagedView().configurationChange( true );

                 orientationflag = false; 
            	 mWorkspace.getPagedView().onTouchCancel( null );
            	 mWorkspace.cleanDragData();
                 mWorkspace.getPagedView().resetOffset();

            }
            
            XShortcutIconView.dimenDirty = true;
         // do not resize draglayer here
//            mDragLayer.resizeDragLayer();
          
            /*** RK_ID: RK_PAD_MENU  AUT: liuyg1. DATE: 2012-05-31 . START***/
            if (mWorkspaceMenuDialog != null && mWorkspaceMenuDialog.isShowing()) {
            	mWorkspaceMenuDialog.dismiss();
            	 Log.d(TAG,"onConfiguratonChanged mWorkspaceMenuDialog.dismiss();");
            	mWorkspaceMenuDialog = new WorkspaceMenuDialog().createDialog(XLauncher.this,mHandler);
            	mWorkspaceMenuDialog.show();
            }
            /*
            if (mApplistMenuDialog != null && mApplistMenuDialog.isShowing()) {
            	mApplistMenuDialog.dismiss();
            	mApplistMenuDialog  = new ApplistMenuDialog().createDialog(XLauncher.this,mHandler);
            	mApplistMenuDialog.show();
            }
            */
            /*** RK_ID: RK_PAD_MENU  AUT: liuyg1. DATE: 2012-05-31 . END***/
            
            /*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-5-30. S***/
            if(mDragLayer != null)
            mDragLayer.clearAllResizeFrames();
            /*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-5-30. E***/

            if (mDragController != null && mDragController.isDragging()) {
                mDragController.cancelDrag();
            }
            
            try{
            mMainSurface.getRenderer().invalidate(); 
            closeSystemDialogs();
			//test by liuli new deletebar
            View view = mMainSurface.findViewById(R.id.uninstall_apps_dlg);
            if (view != null) {
                // fix bug 21065
                mMainSurface.removeDeleteDialog(this);
            }
            }catch(Exception e){
                Log.e(TAG,"catch the exception when transfer the screen");
            }
            
            if (mMainSurface != null)
            {
            	mMainSurface.getExchangee().getLGestureDetector().cancel();
            }
            
            // confirm phone ori 2
            confirmPhoneOrientation();
        }
        /*** RK_ID: RK_PAD_SWTICH  AUT: yumina . DATE: 2013-06-13 . END***/

	
	public void updateAppSlideValue() {
        if (mAppListView != null) {
        	mAppListView.updateSlideValue();
        }
    }
	protected void updateIconSizeValue() {
        if (mWorkspace != null) {
            mWorkspace.updateIconSizeValue();
        }

        if (mAppListView != null) {
            mAppListView.updateIconSizeValue();
        }
    }

	@Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
		/** ID: Profile restoring. AUT: chengliang . DATE: 2012.03.22 . S */
		if(LoadBootPolicy.getInstance(this).getDefaultProfileProcessingState()){
			Log.d("liuyg1","return onRestoreInstanceState"); 
			BackupManager.getInstance(this).reLaunch();
			return;
		}
		/** ID: Profile restoring. AUT: chengliang . DATE: 2012.03.22 . E */
		
        // Do not call super here
        mSavedInstanceState = savedInstanceState;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
		/** ID: Profile restoring. AUT: chengliang . DATE: 2012.03.22 . S */
		if(LoadBootPolicy.getInstance(this).getDefaultProfileProcessingState()){
			return;
		}
		/** ID: Profile restoring. AUT: chengliang . DATE: 2012.03.22 . E */
		 
    	
    	R5.echo("Launcher onSaveInstanceState before super.");
		
        outState.putInt(RUNTIME_STATE_CURRENT_SCREEN, mWorkspace.getCurrentPage());
        super.onSaveInstanceState(outState);

//        outState.putInt(RUNTIME_STATE, mState.ordinal());
        // We close any open folder since it will not be re-opened, and we need to make sure
        // this state is reflected.
        closeFolder();
//        /*RK_ID: RK_BUGFIX_179771 . AUT: zhanglz1 . DATE: 2012-0-01 . */
//        if(mLeosDialog!=null){
//            mLeosDialog.dismiss();            
//        }
//  
//        mLeosDialog = null;
        if (mPendingAddInfo.container != ItemInfo.NO_ID && mPendingAddInfo.screen > -1 &&
                (mWaitingForResult || isConfiguringWidget)) {
            outState.putLong(RUNTIME_STATE_PENDING_ADD_CONTAINER, mPendingAddInfo.container);
            outState.putInt(RUNTIME_STATE_PENDING_ADD_SCREEN, mPendingAddInfo.screen);
            outState.putInt(RUNTIME_STATE_PENDING_ADD_CELL_X, mPendingAddInfo.cellX);
            outState.putInt(RUNTIME_STATE_PENDING_ADD_CELL_Y, mPendingAddInfo.cellY);
        }

//        if (mFolderInfo != null && mWaitingForResult) {
//            outState.putBoolean(RUNTIME_STATE_PENDING_FOLDER_RENAME, true);
//            outState.putLong(RUNTIME_STATE_PENDING_FOLDER_RENAME_ID, mFolderInfo.id);
//        }

        // Save the current AppsCustomize tab
        // art
        if (mAppListView != null) {
        	outState.putString("apps_customize_currentTab", "APP");
        	outState.putInt("apps_customize_currentIndex", 1);
        }
    }
	
	@Override
	public void onDestroy() {
	    R5.echo("onDestroy");
	    super.onDestroy();
	    
		if (LoadBootPolicy.getInstance(this).getDefaultProfileProcessingState()) {
			return;
		}
		Log.i(TAG,"XLauncher.onDestroy---"+ this + "     pid = " +  android.os.Process.myPid());

		
		if(mLoadWorkspaceDialog != null && mLoadWorkspaceDialog.isShowing()){
			Log.d("gecn1", "XLauncher.onDestroy- Load Worksapce dialog remove  " + mLoadWorkspaceDialog);
			mLoadWorkspaceDialog.dismiss();
			mLoadWorkspaceDialog = null;
		}else if(mLoadWorkspaceDialog != null && !mLoadWorkspaceDialog.isShowing()){
			Log.d("gecn1", "XLauncher.onDestroy-  not showing " + mLoadWorkspaceDialog);
			mLoadWorkspaceDialog = null;
		}else{
			Log.d("gecn1", "XLauncher.onDestroy- Load Worksapce dialog  is null" );
		}
		
//		removeDialog(DIALOG_LOAD_WORKSAPCE);
		/*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-5-30. S***/
        mDragLayer.clearAllResizeFrames();
        /*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-5-30. E***/
		
        /* RK_ID:RK_BUG zhangdxa . DATE:2013-7-16. S*/
        mModel.clearShortcutInfo();  
        if( mModel.getLauncher() == this){
        	Log.i("zdx1","XLauncher.onDestroy 2**********call mModel.stopLoader()");
            LauncherApplication app = ((LauncherApplication) getApplication());
            mModel.stopLoader();
            app.setLauncher(null);
        }
        /* RK_ID:RK_BUG zhangdxa . DATE:2013-7-16. E*/
        
        try {
            mAppWidgetHost.stopListening();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mAppWidgetHost = null;
        
        mWidgetsToAdvance.clear();
        
//        mMainSurface.getRenderer().getEventHandler().removeCallbacks(mCancelRunnable);

//		mMainSurface.getRenderer().getEventHandler().getLooper().quit();
        if (outAnim != null) {
            outAnim.cancel();
            outAnim =null;
        }
        if (inAnim != null) {
            inAnim.cancel();
            inAnim = null;
        }
         
        mWorkspace.clearAllItems();
        
        mMainSurface = null;
        
        mDragLayer = null;
        mAppListView = null;
        mWorkspace = null;
        mScreenMngView = null;
        mDragController = null;
        mBlackboard = null;
        mHotseat = null;
        mQuickView = null;        
        
//        setContentView(null);
        
        destroySenior();
        
        mLauncherHandler.removeMessages(MSG_ID_ONRESUME);
        mLauncherHandler.removeMessages(MSG_ID_ONPAUSE);
        mLauncherHandler.removeMessages(MSG_ID_SETINIT_ANIMA);
        
        unregisterReceiver(mCloseSystemDialogsReceiver);
        unregisterReceiver(mAppToPositionReceiver);
        unregisterReceiver(mSettingsChangedReceiver);
        unregisterReceiver(mWidgetReceiver);
        unregisterReceiver(mThemePkgReceiver);
        unregisterReceiver(mAppFlagReceiver);
        unRegisterMissedContentObserver();
        
        GadgetUtilities.clean();
        
        clearStaticBitmap();
        
        mGestureManager = null;
	}
	
	private void destroySenior() {
        VersionUpdateSUS.getInstance().finishVersionUpdate();
        if( VersionUpdateSUS.getInstance().getAutoUpdateOn() ) {
            mInitHandler.removeMessages( MSG_AUTO_UPDATE );
        }
    }
	
	void confirmPhoneOrientation() {
		if (SettingsValue.getCurrentMachineType(this) == -1) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

	}
		
	@Override
    protected void onResume() {
		
		confirmPhoneOrientation();
		
        super.onResume();
        
        if( SettingsValue.getCurrentMachineType( this ) != -1 ){
        	com.lenovo.launcher2.customizer.Utilities.freezingOrientation( this , false );
        }
        
        R5.echo("onResume");
        onPauseFlag = false;
        
        LGestureDetector.updateLongPressTimeout();

		if(LoadBootPolicy.getInstance(this).getDefaultProfileProcessingState()){
			Log.d("liuyg1","return");
			return;
		}
		
		// new art
		if (mAppListView != null) {
			mAppListView.updateSlideValue();
        }
		        
        if (mWorkspace != null) {
            mWorkspace.updateSlideValue();
        }
        updateIconSizeValue();
        mPaused = false;
        if (mRestoring || mOnResumeNeedsLoad) {
            mWorkspaceLoading = true;
            mModel.startLoader(this, true);
            mRestoring = false;
            mOnResumeNeedsLoad = false;
        }       
           
		new Thread(new Runnable() {
			@Override
			public void run() {
				if( /*!hasWindowFocus() &&*/ mWorkspace != null ){
				    Log.i("square", "send MSG_ID_RESTART_WEATHER_SERVICE in onResume");
				    mLauncherHandler.removeMessages(MSG_ID_RESTART_WEATHER_SERVICE);
			        mLauncherHandler.sendEmptyMessageDelayed(MSG_ID_RESTART_WEATHER_SERVICE, 500);
			   }
			}
		}).start();
//        new Thread(){
//        	public void run(){
//            	if(WeatherUtilites.hasInstances(XLauncher.this,WeatherUtilites.THIS_WEATHER_WIDGET))
//            	{
//	            	Intent intent = new Intent(WeatherUtilites.ACTION_WEATHER_WIDGET_SEVICE_RESTART);
//	            	sendBroadcast(intent);
//            	}
////            	}else if(WeatherUtilites.hasInstances(XLauncher.this,GadgetUtilities.TASKMANAGERWIDGETVIEWHELPER)){
////                	Intent intent = new Intent(WeatherUtilites.ACTION_ADD_TASKMANAGER_WIDGET);
////                	startService(intent.setClass(XLauncher.this,WidgetService.class));
////            	}
//        	}
//        }.start();
//        if (mWaitingForResume != null) {
//            mWaitingForResume.setStayPressed(false);
//        }
		if (mWallpapperBlur.checkLiveWallpaper()) {
		    /*** RK_ID: HIGH_QUALITY_EFFECTS. AUT: zhaoxy. DATE: 2013-10-16 . START***/
            if (mWallpapperBlur.isEnable() && SettingsValue.ENABLE_HIGH_QUALITY_EFFECTS) {
            /*** RK_ID: HIGH_QUALITY_EFFECTS. AUT: zhaoxy. DATE: 2013-10-16 . END***/
                mWallpapperBlur.buildBlurBitmap();
            }
        }

        clearTypedText();
//        closeFolder();
        mAnimScreen = false;
        mAnimClose = false;
//        reloadSwapConfig();
    }
	
	private final Runnable mCacheViewRunnable = new Runnable() {
		
		@Override
		public void run() {
			if( mPaused ){
				sendBroadcast( new Intent(XViewContainer.ACTION_UPDATE_CACHE) );
			}
		}
	};
	
    @Override
    public Object onRetainNonConfigurationInstance() {
		if(LoadBootPolicy.getInstance(this).getDefaultProfileProcessingState()){
			return null;
		}
    	
        // Flag the loader to stop early before switching
        mModel.stopLoader();
        return Boolean.TRUE;
    }
	/*RK_ID: RK_SHOW_MENU Bug 9941 AUT:liuyg1@lenovo.com DATE: 2013-03-27 START*/
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
    	XLauncher.setScreen(mWorkspace.getCurrentPage());
        R2.echo("onMenuOpened");
        showMenu();
        return false;// if true, return system menu
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		if(LoadBootPolicy.getInstance(this).getDefaultProfileProcessingState()){
			Log.d("liuyg1","return");
			return false;
		}
		super.onCreateOptionsMenu(menu);

		
       Intent manageApps = new Intent(Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS);
       manageApps.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
               | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
       Intent settings = new Intent(android.provider.Settings.ACTION_SETTINGS);
       settings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
               | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
       String helpUrl = getString(R.string.help_url);
       Intent help = new Intent(Intent.ACTION_VIEW, Uri.parse(helpUrl));
       help.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
               | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

       menu.add(MENU_GROUP_WALLPAPER, MENU_WALLPAPER_SETTINGS, 0, R.string.menu_wallpaper)
           .setIcon(android.R.drawable.ic_menu_gallery)
           .setAlphabeticShortcut('W');
       menu.add(0, MENU_MANAGE_APPS, 0, R.string.menu_manage_apps)
           .setIcon(android.R.drawable.ic_menu_manage)
           .setIntent(manageApps)
           .setAlphabeticShortcut('M');
       menu.add(0, MENU_SYSTEM_SETTINGS, 0, R.string.menu_settings)
           .setIcon(android.R.drawable.ic_menu_preferences)
           .setIntent(settings)
           .setAlphabeticShortcut('P');
       if (!helpUrl.isEmpty()) {
           menu.add(0, MENU_HELP, 0, R.string.menu_help)
               .setIcon(android.R.drawable.ic_menu_help)
               .setIntent(help)
               .setAlphabeticShortcut('H');
       }
		return true;
	}
	 /*RK_ID: RK_SHOW_MENU Bug 9941 AUT:liuyg1@lenovo.com DATE: 2013-03-27 END*/
     void showMenu() {
    	 XLauncher.setScreen(mWorkspace.getCurrentPage());
    	 /*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-5-30. S***/
     	 mDragLayer.clearAllResizeFrames();
     	 /*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-5-30. E***/
     	
        boolean dragLayerNotMove = false;
        if (mMainSurface != null) {
            // fix bug 21093
            dragLayerNotMove = mMainSurface.resetToNormal(this, true);
            if (dragLayerNotMove) {
                return;
            }
        }
//        if ((snapView != null && snapView.getVisibility() == View.VISIBLE)
//                || mWorkspace.isCustomState()
//                /*RK_ID: RK_LIST_APP . AUT: zhanggx1 . DATE: 2012-06-26 . S*/
//                //new art || (mState == State.APPS_CUSTOMIZE && mAppsListPage.getVisibility() == View.VISIBLE)
//                /*RK_ID: RK_LIST_APP . AUT: zhanggx1 . DATE: 2012-06-26 . E*/
//                ) {
//            /*** RK_ID: CATEGORY_MENU.  AUT: zhaoxy . DATE: 2012-07-03 . START***/
//            //new art
////            if (mAppsCustomizeTabHost.getmCategoryMenu().isOpened()) {
////                mAppsCustomizeTabHost.closeCategoryMenu();
////            } else {
////                mAppsCustomizeTabHost.openCategoryMenu();
////            }
//            //new art
//            /*** RK_ID: CATEGORY_MENU.  AUT: zhaoxy . DATE: 2012-07-03 . END***/
//            return;
//        }
        /*** fixbug 9713  . AUT: zhaoxy . DATE: 2013-03-26. START***/
        dismissOpenFolder();
        /*** fixbug 9713  . AUT: zhaoxy . DATE: 2013-03-26. END***/
 /* RK_ID:MENU_DIALOG_ACTIVITY. AUT:liuyg1 . DATE:2013-3-25. START*/ 
    	  Log.d("liuyg", "showMenu()");
 /* RK_ID:MENU_DIALOG_ACTIVITY. AUT:liuyg1 . DATE:2013-3-25. END*/ 
        if(!isAllAppsCustomizeOpen() && (mScreenMngView == null || !mScreenMngView.isVisible())) {
 /* RK_ID: RK_MENU_BOOST . AUT: yumina . DATE: 2013-03-04 . S */
        //Intent finishIntent = new Intent("com.lenovo.launcher.boost.lejingpin");
        //this.sendBroadcast(finishIntent);
 /* RK_ID: RK_MENU_BOOST . AUT: yumina . DATE: 2012-03-04 . E */
 /* RK_ID:MENU_DIALOG_ACTIVITY. AUT:liuyg1 . DATE:2013-3-14. START*/ 
        	WorkspaceMenuDialog dlg = new WorkspaceMenuDialog();
        	mWorkspaceMenuDialog = dlg.createDialog(XLauncher.this,mHandler);
        	mWorkspaceMenuDialog.show();
 /* RK_ID:MENU_DIALOG_ACTIVITY. AUT:liuyg1 . DATE:2013-3-14. END*/ 
 /* RK_ID:MENU_DIALOG_ACTIVITY. AUT:mohl . DATE:2013-7-16. START*/ 
        	mLeAlertDialog = dlg.getAlertDialog();
 /* RK_ID:MENU_DIALOG_ACTIVITY. AUT:mohl . DATE:2013-7-16. END*/       	
        /*** RK_ID: TASK_TAB.  AUT: zhaoxy . DATE: 2012-08-14 . START***/
        } 
        else if (mMainSurface != null && (mScreenMngView == null || !mScreenMngView.isVisible())) {
 /* RK_ID:MENU_DIALOG_ACTIVITY. AUT:liuyg1 . DATE:2013-3-14. START*/ 
       	//mApplistMenuDialog  = new ApplistMenuDialog().createDialog(XLauncher.this,mHandler);
       	//mApplistMenuDialog.show();
 /* RK_ID:MENU_DIALOG_ACTIVITY. AUT:liuyg1 . DATE:2013-3-14. END*/ 
        }
        /* RK_ID:WEATHER_ANIM. AUT:kangwei3 . DATE:2013-3-21. S*/ 
        notifyWeatherAnimation( false );
        /* RK_ID:WEATHER_ANIM. AUT:kangwei3 . DATE:2013-3-21. E*/ 
       //art
	    /* RK_ID: RK_ANIM_WEATHER . AUT: KANGWEI3 . DATE: 2013-03-27 . S */
        mLauncherHandler.removeMessages(MSG_ID_SETINIT_ANIMA);
        mLauncherHandler.sendMessageDelayed(mLauncherHandler.obtainMessage(MSG_ID_SETINIT_ANIMA), 500);
	    /* RK_ID: RK_ANIM_WEATHER . AUT: KANGWEI3 . DATE: 2013-03-27 . E */

    }
//    private Drawable getMenuBackground(int itemNum) { 
//        Drawable bg;
//        int backRes;
//        LauncherApplication app = (LauncherApplication) this.getApplicationContext(); 
//        int rowNum = (int)Math.ceil(itemNum / 4.0f); 
//        if (rowNum != 2) {
//            backRes = R.drawable.menu_background_single;
//        } else {
//            backRes = R.drawable.menu_background;
//        }
////        bg = app.mLauncherContext.getDrawable(backRes);
//        bg = app.getResources().getDrawable(backRes);
////      bg.setAlpha(204);
//        return bg;
//    }
    
    private void dismissOpenFolder() {
        if (mWorkspace.getOpenFolder() != null) {
            closeFolder();
        }
    }
    
    public ThemeSimpleAdapter getMenuAdapter(int[] str, int[] pic,boolean fromTheme) {
        ArrayList<HashMap<String, Object>> menulist = new ArrayList<HashMap<String, Object>>();
        final String image_key = "itemImage";
        final String text_key = "itemText";
        
        /*RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2011-12-26 . S*/
        LauncherApplication app = (LauncherApplication) getApplicationContext();
        //Resources res = getResources();//cancel by xingqx for sonar
        /*RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2011-12-26 . E*/

        for (int i = 0; i < str.length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            /*RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2011-12-26 . S*/
//            Drawable icon = Utilities.findDrawableById(res, pic[i], 
//                  Launcher.this);
//            Drawable icon = app.mLauncherContext.getDrawable(pic[i], false);
            Drawable icon = app.getResources().getDrawable(pic[i]);
            
            if (icon != null) {
                map.put(image_key, icon);
            } else {
                map.put(image_key, pic[i]);
            }
            /*RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2011-12-26 . E*/
            
            map.put(text_key, getString(str[i]));
            menulist.add(map);
        }
        int griditem = R.layout.menu_griditem;
        if(!fromTheme){
            griditem = R.layout.menu_add_griditem;
        }

        ThemeSimpleAdapter simple = new ThemeSimpleAdapter(this, menulist, griditem, new String[] { image_key,
                text_key }, new int[] {R.id.menuitem_text, R.id.menuitem_text}, R.color.menu_text_color, R.color.def__menu_text_color,fromTheme);
        return simple;
    }
    
    public boolean isAllAppsCustomizeOpen() {
        return mState == State.APPS_CUSTOMIZE;
    }

    void showAddDialog() {
    	if (isScreenMngOpendOrExiting()) {
    		return;
    	}
    	if (!mWorkspace.isTouchable()) {
    		return;
    	}
    	if (isWorkspaceLocked()) {
    		if (!sPendingMsg.contains(PENDING_EDIT_SCREEN)
        			&& !sPendingMsg.contains(PENDING_PREVIEW_SCREEN)) {
        		sPendingMsg.add(PENDING_EDIT_SCREEN);
        	}
    		return;
    	}
    	showWidgetSnap(true, XScreenMngView.State.ADDED);
    	
//    	mWorkspace.getPagedView().adjustPageOffset(200L);
//    	
//        resetAddInfo();
//        mPendingAddInfo.container = LauncherSettings.Favorites.CONTAINER_DESKTOP;
//        mPendingAddInfo.screen = mWorkspace.getCurrentPage();
//        mWaitingForResult = true;
//        showDialog(DIALOG_CREATE_SHORTCUT);
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        
        /** ID: Profile restoring. AUT: chengliang . DATE: 2012.03.22 . S */
        if(LoadBootPolicy.getInstance(this).getDefaultProfileProcessingState()){
            return super.onCreateDialog(id);
        }
        /** ID: Profile restoring. AUT: chengliang . DATE: 2012.03.22 . E */
        
        switch (id) {
            case DIALOG_CREATE_SHORTCUT:
                return new CreateShortcut().createDialog();

        }

        return super.onCreateDialog(id);
    }
    
    
    private Dialog creatLoadWokspaceDialog(){
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	Exception e = new Exception();
    	e.printStackTrace(new PrintStream(out));
    	Log.d("gecn1", "XLauncher.this   " + out.toString());
    	Log.d("gecn1", " launchr = " + XLauncher.this);
    	
    	Dialog loadProgressDlg;
    	loadProgressDlg = new Dialog(this,R.style.Theme_LeLauncher_LoadProgressDialog);
		loadProgressDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
		loadProgressDlg.setContentView(R.layout.load_worksapce_progressdialog);
		loadProgressDlg.setCancelable(false);
		Window window = loadProgressDlg.getWindow();
		window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//		window.setBackgroundDrawableResource(R.drawable.wallpaper_grass);
		window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		window.setGravity(Gravity.CENTER);
		WindowManager.LayoutParams lp = window.getAttributes();
//		lp.dimAmount = 0.9f;
		window.setAttributes(lp);
		Log.d("gecn1", "loadProgressDlg  =" + loadProgressDlg + "     pid = " +  android.os.Process.myPid() + "    xlauncher " + XLauncher.this );
//		loadProgressDlg.show();
		return loadProgressDlg;
    }
    
    /**
     * Displays the shortcut creation dialog and launches, if necessary, the
     * appropriate activity.
     */
    private class CreateShortcut implements DialogInterface.OnClickListener,
            DialogInterface.OnCancelListener, DialogInterface.OnDismissListener,
            DialogInterface.OnShowListener {

        private static final int ITEM_APPLICATION = 0;
        private static final int ITEM_APPWIDGET = ITEM_APPLICATION +1;
        private static final int ITEM_LOES_WIDGET = ITEM_APPWIDGET+1;
        private static final int ITEM_SHORTCUT = ITEM_LOES_WIDGET+1;
        private static final int ITEM_FOLDER = ITEM_SHORTCUT+1;
        private static final int ITEM_WALLPAPER = ITEM_FOLDER+1;


        private int[] addmenuname_array = {R.string.group_applications, R.string.group_widgets,
                R.string.group_leos_widgets, R.string.group_shortcuts,
                R.string.add_folder,R.string.group_wallpapers };
        private int[] addmenuimage_array = {R.drawable.ic_launcher_app,R.drawable.ic_launcher_widget,
                R.drawable.ic_launcher_leoswidget,  R.drawable.ic_launcher_shortcut,
                R.drawable.ic_launcher_folder,  R.drawable.ic_launcher_wallpaper };

        Dialog createDialog() {
    /* RK_ID: RK_MENU_RFACTOR . AUT: LIUYG1 . DATE: 2012-11-29 .S */

            mLeosDialog = new LeDialog(XLauncher.this, R.style.Theme_LeLauncher_Dialog_Shortcut);// , R.style.menu_style);
            mLeosDialog.setCanceledOnTouchOutside(true);
//            mLeosDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mLeosDialog.setContentView(R.layout.menu_add_item);

            Window window = mLeosDialog.getWindow();

//            // add alpha for menu, but it cannot work in device, can work in emulator
//            WindowManager.LayoutParams lp = window.getAttributes();
//            // lp.alpha = 0.9f;
//            lp.dimAmount = 0.0f;
//            window.setAttributes(lp);
            // set layout param
            //window.setBackgroundDrawableResource(R.drawable.menu_dialog_bg);
            //window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setWindowAnimations(R.style.dialogWindowAnim);
            window.setGravity(Gravity.CENTER );
            
            
            mLeosDialog.setOnCancelListener(this);
            mLeosDialog.setOnDismissListener(this);
            mLeosDialog.setOnShowListener(this);
            
            TextView title = (TextView) mLeosDialog.findViewById(R.id.dialog_title);
            title.setText(R.string.menu_item_add_item);
            
            ImageView icon = (ImageView) mLeosDialog.findViewById(R.id.dialog_icon);
            icon.setBackgroundResource(R.drawable.ic_add_title_icon);
            
            
            View addDialog_bg =  mLeosDialog.findViewById(R.id.add_dialog_bg);
            addDialog_bg.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    cleanup();
                }
            });
            
            
            MenuGridView menuGrid = (MenuGridView) mLeosDialog.findViewById(R.id.grid_item);
            menuGrid.setFocusable(false);
               
            menuGrid.setAdapter(getMenuAdapter(addmenuname_array, addmenuimage_array,false));
            menuGrid.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    switch (position) {

//                    /* AUT: xingqx xingqx@lenovo.com DATE: 2012-02-22 START */
                    case ITEM_SHORTCUT: {
                        // Insert extra item to handle picking application
                        pickShortcut();
                        break;
                    }
                    case ITEM_FOLDER: {
                        workspacePickApplication(true);
                        break;
                    }
                    case ITEM_APPLICATION: {
                        workspacePickApplication(false);
                        break;
                    }
                    case ITEM_LOES_WIDGET: {
                        Intent pickIntent = new Intent(WeatherUtilites.ACTION_ADD_LENOVOWIDGET_ACTIVITY);
                        resetAddInfo();
                        Log.d("liuyg1","ITEM_LOES_WIDGET");
                        try{
                        	startActivity(pickIntent);
                        }catch(Exception e){
                        	e.printStackTrace();
                        }
                        break;
                    }
                    case ITEM_APPWIDGET: {
                        resetAddInfo();
                        pickupOtherWidgets();
                        break;
                    }
                    case ITEM_WALLPAPER: {
                        showWorkspace(true);
                        	 Intent setWallpaper = new Intent(SettingsValue.ACTION_LETHEME_LAUNCH);
                             setWallpaper.putExtra("EXTRA",2);
                             startActivitySafely(setWallpaper, "");
                             
                 		/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-3-26 S*/
                        Reaper.processReaper( XLauncher.this, 
                              Reaper.REAPER_EVENT_CATEGORY_WALLPAPER, 
                      		  Reaper.REAPER_EVENT_ACTION_WALLPAPER_FROMADD,
                      		  Reaper.REAPER_NO_LABEL_VALUE, 
                      		  Reaper.REAPER_NO_INT_VALUE );
                        /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-3-26 E*/     
                        
                        break;
                    }
                    }
                    cleanup();
                }
            });
    /* RK_ID: RK_MENU_RFACTOR . AUT: LIUYG1 . DATE: 2012-11-29 . E */
                //the code should be removed ,it caused when the activity restore instance,
                //system shouled call the createDialog() ,and here, show the dialog
                //mLeosDialog.show();
            
            return mLeosDialog;
        }

        public void onCancel(DialogInterface dialog) {
            mWaitingForResult = false;
            cleanup();
        }

        public void onDismiss(DialogInterface dialog) {
            mWaitingForResult = false;
            cleanup();
        }

        private void cleanup() {
            try {
                dismissDialog(DIALOG_CREATE_SHORTCUT);
            } catch (Exception e) {
                // An exception is thrown if the dialog is not visible, which is fine
            }
        }

        /**
         * Handle the action clicked in the "Add to home" dialog.
         */
        public void onClick(DialogInterface dialog, int which) {
            
            /** ID: Profile restoring. AUT: chengliang . DATE: 2012.03.22 . S */
            if(LoadBootPolicy.getInstance(XLauncher.this).getDefaultProfileProcessingState()){
                return;
            }
            /** ID: Profile restoring. AUT: chengliang . DATE: 2012.03.22 . E */
            
            cleanup();
 /* RK_ID: RK_MENU_RFACTOR . AUT: LIUYG1 . DATE: 2012-11-29 .S */
//            AddAdapter.ListItem item = (AddAdapter.ListItem) mAdapter.getItem(which);
//            AddAdapter.ListItem item = (AddAdapter.ListItem) mAdapter.getItem(which);
//            onClickAdd(item.actionTag);
//            switch (item.actionTag) {
//                /* AUT: xingqx xingqx@lenovo.com DATE: 2012-02-22 START */
//                case AddAdapter.ITEM_SHORTCUT: {
//                    // Insert extra item to handle picking application
//                    pickShortcut();
//                    break;
//                }
//                case AddAdapter.ITEM_FOLDER: {
//                    workspacePickApplication(true);
//                   break;
//                }
//                /* AUT: xingqx xingqx@lenovo.com DATE: 2012-02-22 END */
//                case AddAdapter.ITEM_APPLICATION: {
//                    /* AUT: liuli1 DATE: 2012-02-10 START */
////                    if (mAppsCustomizeTabHost != null) {
////                        mAppsCustomizeTabHost.selectAppsTab();
////                    }
////                    showAllApps(true);
//                    workspacePickApplication(false);
//
//                /* AUT: liuli1 DATE: 2012-02-10 END */
//
//                    break;
//                }
//                /* RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-09-26 START */
//                case AddAdapter.ITEM_LOES_WIDGET: {
//                       Intent pickIntent = new Intent("android.intent.action.WORKSPACE_PICK_LEOSWIDGET");
//                    // start the pick activity
//                      resetAddInfo();
//                       Log.d("liuyg1","ITEM_LOES_WIDGET");
//                    startActivityForResult(pickIntent, REQUEST_PICK_LEOSAPPWIDGET_LIST);
//                    break;
//                }
//                /* RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-09-26 END */
//                case AddAdapter.ITEM_APPWIDGET: {
//                    /* AUT: xingqx xingqx@lenovo.com DATE: 2012-01-19 START */
//                    /*
//                    if (mAppsCustomizeTabHost != null) {
//                        mAppsCustomizeTabHost.selectWidgetsTab();
//                    }
//                    showAllApps(true);
//                    */
//                    resetAddInfo();
//                    int appWidgetId = Launcher.this.mAppWidgetHost.allocateAppWidgetId();
//                    Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
//                    pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
//                    // start the pick activity
//                    startActivityForResult(pickIntent, REQUEST_PICK_APPWIDGET_LIST);
//                    /* AUT: xingqx xingqx@lenovo.com DATE: 2012-01-19 END */
//                    break;
//                }

//                case AddAdapter.ITEM_WALLPAPER: {
///* RK_ID: RK_MENU_RFACTOR . AUT: SHENCHAO1 . DATE: 2012-11-29 . S */
//                  showWorkspace(true);
//                  Intent setWallpaper = new Intent(Launcher.this,WallpaperChooser.class);
//                  startActivityForResult(setWallpaper,REQUEST_PICK_WALLPAPER);
///* RK_ID: RK_MENU_RFACTOR . AUT: SHENCHAO1 . DATE: 2012-11-29 . S */                  
//  break;
//                }
//            }
 /* RK_ID: RK_MENU_RFACTOR . AUT: LIUYG1 . DATE: 2012-11-29 .E */
        }

        public void onShow(DialogInterface dialog) {
            mWaitingForResult = true;
        }
    }
    
    public void resetAddInfo() {
        mPendingAddInfo.container = ItemInfo.NO_ID;
        mPendingAddInfo.screen = -1;
        mPendingAddInfo.cellX = mPendingAddInfo.cellY = -1;
        mPendingAddInfo.spanX = mPendingAddInfo.spanY = -1;
        mPendingAddInfo.dropPos = null;
    }
    
    private static ArrayList<PendingAddArguments> sPendingAddList
    = new ArrayList<PendingAddArguments>();
    
    private static ArrayList<String> sPendingMsg = new ArrayList<String>();
    private static final String PENDING_PREVIEW_SCREEN = "PENDING_PREVIEW_SCREEN";
    private static final String PENDING_EDIT_SCREEN = "PENDING_EDIT_SCREEN";
        
    private ArrayList<String> mIntentListInfo = null;
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean delayExitSpringLoadedMode = false;
        mWaitingForResult = false;
        isConfiguringWidget = false;
        
        if (resultCode == RESULT_OK && mPendingAddInfo.container != ItemInfo.NO_ID) {
            final PendingAddArguments args = new PendingAddArguments();
            args.requestCode = requestCode;
            args.intent = data;
            args.container = mPendingAddInfo.container;
            args.screen = mPendingAddInfo.screen;
            args.cellX = mPendingAddInfo.cellX;
            args.cellY = mPendingAddInfo.cellY;


//            /* AUT: xingqx xingqx@lenovo.com DATE: 2012-01-19 START */
//            if (requestCode == REQUEST_PICK_APPWIDGET_LIST) {
//                args.screen = mWorkspace.getCurrentPage();
//                args.cellX = -1;
//                args.cellY = -1;
//            }
//            /* AUT: xingqx xingqx@lenovo.com DATE: 2012-01-19 END */

            /* AUT: liuli1 DATE: 2012-02-10 START */
            if (requestCode == REQUEST_PICK_MOREAPPLICATION && data != null) {
                boolean isFolder = data.getBooleanExtra("EXTRA_FOLDER", false);
                /* RK_ID: RK_FOLDER_EDITOR . AUT: chenrong2 . DATE: 2012-03-20 . S */
                String folderName = data.getStringExtra("EXTRA_FOLDER_NAME");
                mIntentListInfo = data.getStringArrayListExtra("EXTRA_INTENT");
                
                addFolderOrShortcuts(isFolder, folderName, mIntentListInfo);
            }
            /* AUT: liuli1 DATE: 2012-02-10 END */

            // If the loader is still running, defer the add until it is done.
            if (isWorkspaceLocked()) {
                sPendingAddList.add(args);
            } else {
                delayExitSpringLoadedMode = completeAdd(args);
            }
        } 
        else if ((requestCode == REQUEST_PICK_APPWIDGET ||
                requestCode == REQUEST_CREATE_APPWIDGET
                || requestCode == REQUEST_PICK_APPWIDGET_LIST) && resultCode == RESULT_CANCELED) {
            if (data != null) {
                // Clean up the appWidgetId if we canceled
                int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
         /** ID: configurable widget restore . AUT: chengliang . DATE: 2012.05.31 . S */
                R2.echo("May touch CANCEL . " + appWidgetId);
                ContentResolver cr = getContentResolver();
                Cursor c = cr.query(LauncherSettings.Favorites.CONTENT_URI,
                        new String[]{LauncherSettings.Favorites._ID},
                        "appWidgetId=?", new String[]{"" + appWidgetId }, null);
                try {
                    if(c.moveToNext()){
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally{
                    c.close();
                }
                if (appWidgetId != -1) {
                    mAppWidgetHost.deleteAppWidgetId(appWidgetId);
                }
            }
        }
        else if (requestCode == REQUEST_PICK_APPWIDGET_LIST
                && resultCode == RESULT_OK) {
            int appWidgetId1 = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);                
            final AppWidgetProviderInfo widgetInfo = 
                    AppWidgetManager.getInstance(this).getAppWidgetInfo(appWidgetId1);

            final PendingAddWidgetInfo createInfo = new PendingAddWidgetInfo(widgetInfo, null,
                    null);
//                checkAddPage();
            //cancel by xingqx 20120221
//            mWaitingForResult = true;
            addAppWidgetFromDialog(createInfo, LauncherSettings.Favorites.CONTAINER_DESKTOP,
                       mWorkspace.getCurrentPage(), null, mPendingAddInfo.dropPos,appWidgetId1);
            long delay = (createInfo != null 
            		&& createInfo.componentName != null 
            		&& "com.lenovopad.guide".equals(createInfo.componentName.getPackageName())) ? 2600L : 500L;
            refreshMngViewDelayed(delay, mWorkspace.getCurrentPage());
            
        }
        else if(requestCode == REQUEST_CREATE_APPWIDGET && resultCode == RESULT_OK){
            
            int appWidgetId1 = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);       
            R2.echo("Touch TRANSFORMINGGGGGGGNIINNGN....  : " + appWidgetId1);
                ContentResolver cr = getContentResolver();
                final Cursor c = cr.query(LauncherSettings.Favorites.CONTENT_URI, 
                        new String[]{LauncherSettings.Favorites.CONFIGABLE_WIDGET}, 
                        "appWidgetId=?", 
                        new String[]{"" + appWidgetId1}, null);
                try {
                int configIndex = c.getColumnIndex(LauncherSettings.Favorites.CONFIGABLE_WIDGET);
                if(c.moveToNext() ){
                int needConfig = c.getInt( configIndex );
                if( needConfig == 1 ){
                    ContentValues value = new ContentValues();
                    value.put(LauncherSettings.Favorites.CONFIGABLE_WIDGET, 0);
                    value.put(LauncherSettings.Favorites.APPWIDGET_ID, appWidgetId1);
                    cr.update(LauncherSettings.Favorites.CONTENT_URI, value, "appWidgetId=?", new String[]{""+appWidgetId1});
                    R2.echo("Start LOADER    LLLLLLLLLLLLLLLLLLLLLLLLLL");
//                    mModel.forceReloadWorkspce();
                    return;
                }}
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }finally{
                c.close();
            }
        /** ID: configurable widget restore . AUT: chengliang . DATE: 2012.05.31 . E */
        } 
        /*else if(requestCode==REQUEST_PICK_LEOSAPPWIDGET_LIST&& resultCode == RESULT_OK){
            Log.d("liuyg1","REQUEST_PICK_LEOSAPPWIDGET_LIST");
            LenovoWidgetViewInfo lenovoWidget = new LenovoWidgetViewInfo();
            String packageName = data.getStringExtra("EXTRA_PACKAGENAME");
            String label = data.getStringExtra("EXTRA_CALSS");
            lenovoWidget.className = label;
            lenovoWidget.packageName = packageName;
            ComponentName component= new ComponentName(lenovoWidget.packageName, lenovoWidget.className);
            lenovoWidget.componentName = component;
            lenovoWidget.minWidth =  data.getIntExtra("EXTRA_WIDTH", 286);
            lenovoWidget.minHeight = data.getIntExtra("EXTRA_HIEGHT", 286);
            lenovoWidget.previewImage = R.drawable.lotus_icon;
            lenovoWidget.cellX = mPendingAddInfo.cellX;
            lenovoWidget.cellY = mPendingAddInfo.cellY;
            lenovoWidget.screen = mWorkspace.getCurrentPage();
            addLeosWidgetViewToWorkspace(lenovoWidget);
           
        } */else if ((requestCode == REQUEST_CODE_CHANGE_PICTURE) && resultCode == RESULT_OK
                && data != null) {
//          QuickActionWindowInfo winInfo = mPopupWindow.getInfo();
//          Bitmap bitmap = BitmapFactory.decodeFile(winInfo.getChangeImage().getAbsolutePath());
            Bitmap bitmap = BitmapFactory.decodeFile(QuickActionHelper.getFilePath());
            Log.i(TAG, "bitmap of changed = " + bitmap);

            QuickActionHelper.updateIconAndSyncDb(this, bitmap, mQuickView);
            /* RK_ID: RK_DOCK_ICON . AUT: zhanggx1 . DATE: 2012-07-10 . S */
//          Object tag = mQuickView == null ? null : mQuickView.getTag();
//          ItemInfo itemInfo = null;
//          if (tag != null) {
//              itemInfo = (ItemInfo)tag;
//              getWorkspace().changeIconSize(mQuickView, itemInfo.container);
//          }            
            /* RK_ID: RK_DOCK_ICON . AUT: zhanggx1 . DATE: 2012-07-10 . E */
        } else if ((requestCode == REQUEST_CODE_CHANGE_THEME_ICON) && resultCode == RESULT_OK
                && data != null) {
            Bitmap bitmap = data.getParcelableExtra(Constants.CHANGE_ICON_KEY);
            QuickActionHelper.updateIconAndSyncDb(this, bitmap, mQuickView);
            /** RK_ID: RK_NEWFEATURE_SHOW_MISSED_NUMBER. AUT: yumina DATE: 2012-10-18 S */
//          if(mQuickView instanceof BubbleTextView){
//              ShortcutInfo info = (ShortcutInfo) mQuickView.getTag();
//
//              mWorkspace.refreshMissNumForThemeChanged(mQuickView,info);
        } else if ((requestCode == REQUEST_PICK_HOTSEAT_APPLICATIONS) && resultCode == RESULT_OK
                && data != null) {
            Intent intent = (Intent) data;
            int cellX = data.getIntExtra("CELL_X", -1);
            int cellY = data.getIntExtra("CELL_Y", -1);
            if (cellX != -1 && cellY != -1) {
                // remove the add icon in the cell(cellX, cellY)
//              mHotseat.removeViewAt(cellX, cellY);
                int screen = mHotseat.getOrderInHotseat(cellX, cellY);
                // add the shortcut from the ShortcutActivityPicker
                completeAddHotseatApp(intent, screen, cellX, cellY);
            }
        } else if ((requestCode == REQUEST_CREATE_SHORTCUT || requestCode == REQUEST_PICK_APPLICATION)
                && resultCode == RESULT_OK) {
            final PendingAddArguments args = new PendingAddArguments();
            args.requestCode = requestCode;
            args.intent = data;
            args.container = LauncherSettings.Favorites.CONTAINER_DESKTOP;//mPendingAddInfo.container;
            args.screen = this.getWorkspace().getCurrentPage();
            args.cellX = mPendingAddInfo.cellX;
            args.cellY = mPendingAddInfo.cellY;
            completeAdd(args);
        }

//        switch (requestCode) {
//        case MY_REQUEST_APPWIDGET:
//            Log.i(TAG, "MY_REQUEST_APPWIDGET intent info is -----> " + data);
//            int appWidgetId = data.getIntExtra(
//                    AppWidgetManager.EXTRA_APPWIDGET_ID,
//                    AppWidgetManager.INVALID_APPWIDGET_ID);
//
//            Log.i(TAG, "MY_REQUEST_APPWIDGET : appWidgetId is ----> "
//                    + appWidgetId);
//
//            // 得到的为有效的id
//            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
//                // 查询指定appWidgetId的 AppWidgetProviderInfo对象 ，
//                // 即在xml文件配置的<appwidget-provider />节点信息
//                AppWidgetProviderInfo appWidgetProviderInfo = appWidgetManager
//                        .getAppWidgetInfo(appWidgetId);
//
//                // 如果配置了configure属性 ， 即android:configure = ""
//                // ，需要再次启动该configure指定的类文件,通常为一个Activity
//                if (appWidgetProviderInfo.configure != null) {
//
//                    Log.i(TAG,
//                            "The AppWidgetProviderInfo configure info -----> "
//                                    + appWidgetProviderInfo.configure);
//
//                    // 配置此Action
//                    Intent intent = new Intent(
//                            AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
//                    intent.setComponent(appWidgetProviderInfo.configure);
//                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
//                            appWidgetId);
//
//                    startActivityForResult(intent, MY_CREATE_APPWIDGET);
//                } else
//                    // 直接创建一个AppWidget
//                    onActivityResult(MY_CREATE_APPWIDGET, RESULT_OK, data); // 参数不同，简单回调而已
//            }
//            break;
//        case REQUEST_PICK_APPWIDGET_LIST:
//            completeAddAppWidget(data);
//            break;
//        }
        if (resultCode == RESULT_OK) {
			SharedPreferences preferences = getSharedPreferences(LotusUtilites.LOTUSINFO, 0);
			LotusUtilites.getSearchList(preferences);
			LotusUtilites.getIntentStr(preferences);
			switch (requestCode) {
			case LotusUtilites.REQUEST_DOWNLOAD_CENTER:
				MagicDownloadControl.downloadFromCommon (this,
						LotusUtilites.getPackageNameFromIntent(LotusUtilites.LOTUS_CENTER_INTENT),
                        "214",
                        DownloadConstant.CATEGORY_COMMON_APP,
                        LotusUtilites.app_name_search[LotusUtilites.CENTER],
                        null,
                        LotusUtilites.IDEA_STORE_URL+LotusUtilites.getPackageNameFromIntent(LotusUtilites.LOTUS_CENTER_INTENT),
                        HwConstant.MIMETYPE_APK,
                        "true",
                        "true");
	
				break;
			case LotusUtilites.REQUEST_DOWNLOAD_LT:
				MagicDownloadControl.downloadFromCommon (this,
						LotusUtilites.getPackageNameFromIntent(LotusUtilites.intent_solid[LotusUtilites.LT]),
                        "214",
                        DownloadConstant.CATEGORY_COMMON_APP,
                        LotusUtilites.app_name_search[LotusUtilites.LT],
                        null,
                        LotusUtilites.IDEA_STORE_URL+LotusUtilites.getPackageNameFromIntent(LotusUtilites.intent_solid[LotusUtilites.LT]),
                        HwConstant.MIMETYPE_APK,
                        "true",
                        "true");
	
				break;
			case LotusUtilites.REQUEST_DOWNLOAD_RT:
				MagicDownloadControl.downloadFromCommon (this,
						LotusUtilites.getPackageNameFromIntent(LotusUtilites.intent_solid[LotusUtilites.RT]),
                        "214",
                        DownloadConstant.CATEGORY_COMMON_APP,
                        LotusUtilites.app_name_search[LotusUtilites.RT],
                        null,
                        LotusUtilites.IDEA_STORE_URL+LotusUtilites.getPackageNameFromIntent(LotusUtilites.intent_solid[LotusUtilites.RT]),
                        HwConstant.MIMETYPE_APK,
                        "true",
                        "true");	
				break;
			case LotusUtilites.REQUEST_DOWNLOAD_LB:
				MagicDownloadControl.downloadFromCommon (this,
						LotusUtilites.getPackageNameFromIntent(LotusUtilites.intent_solid[LotusUtilites.LB]),
                        "214",
                        DownloadConstant.CATEGORY_COMMON_APP,
                        LotusUtilites.app_name_search[LotusUtilites.LB],
                        null,
                        LotusUtilites.IDEA_STORE_URL+LotusUtilites.getPackageNameFromIntent(LotusUtilites.intent_solid[LotusUtilites.LB]),
                        HwConstant.MIMETYPE_APK,
                        "true",
                        "true");		
				break;
			case LotusUtilites.REQUEST_DOWNLOAD_RB:
				MagicDownloadControl.downloadFromCommon (this,
						LotusUtilites.getPackageNameFromIntent(LotusUtilites.intent_solid[LotusUtilites.RB]),
                        "214",
                        DownloadConstant.CATEGORY_COMMON_APP,
                        LotusUtilites.app_name_search[LotusUtilites.RB],
                        null,
                        LotusUtilites.IDEA_STORE_URL+LotusUtilites.getPackageNameFromIntent(LotusUtilites.intent_solid[LotusUtilites.RB]),
                        HwConstant.MIMETYPE_APK,
                        "true",
                        "true");	
				break;
				/***RK_ID:RK_LOTUS_BUGFIX_1937 AUT:zhanglz1@lenovo.com. E***/   
			default:
				break;

			}
		}
    }
    
    /**
     * Process a widget drop.
     *
     * @param info The PendingAppWidgetInfo of the widget being added.
     * @param screen The screen where it should be added
     * @param cell The cell it should be added to, optional
     * @param position The location on the screen where it was dropped, optional
     */
    public void addAppWidgetFromDrop(PendingAddWidgetInfo info, long container, int screen,
            int[] cell, int[] loc) {
        resetAddInfo();
        mPendingAddInfo.container = info.container = container;
        mPendingAddInfo.screen = info.screen = screen;
        mPendingAddInfo.dropPos = loc;
        if (cell != null) {
            mPendingAddInfo.cellX = cell[0];
            mPendingAddInfo.cellY = cell[1];
        }

        int appWidgetId = getAppWidgetHost().allocateAppWidgetId();
        AppWidgetManager.getInstance(this).bindAppWidgetId(appWidgetId, info.componentName);
        addAppWidgetImpl(appWidgetId, info);
    }
        
    void addAppWidgetFromDialog(PendingAddWidgetInfo info, long container, int screen,
            int[] cell, int[] loc,int id) {
        resetAddInfo();
        mPendingAddInfo.container = info.container = container;
        mPendingAddInfo.screen = info.screen = screen;
        mPendingAddInfo.dropPos = loc;
        if (cell != null) {
            mPendingAddInfo.cellX = cell[0];
            mPendingAddInfo.cellY = cell[1];
        }
        addAppWidgetImpl(id, info);
    }
    
    /**
     * Add a widget to the workspace.
     *
     * @param appWidgetId The app widget id
     * @param cellInfo The position on screen where to create the widget.
     */
    private void completeAddAppWidget(final int appWidgetId, long container, int screen) {
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);

        // Calculate the grid spans needed to fit this widget
        int[] spanXY = getSpanForWidget(appWidgetInfo, null);

        // Try finding open space on Launcher screen
        // We have saved the position to which the widget was dragged-- this really only matters
        // if we are placing widgets on a "spring-loaded" screen
        int[] cellXY = mTmpAddItemCellCoordinates;
        int[] touchXY = mPendingAddInfo.dropPos;
        boolean foundCellSpan = false;
        if (mPendingAddInfo.cellX >= 0 && mPendingAddInfo.cellY >= 0) {
            cellXY[0] = mPendingAddInfo.cellX;
            cellXY[1] = mPendingAddInfo.cellY;
            foundCellSpan = true;
        } else if (touchXY != null) {
        	int[] result = mWorkspace.getDropTargetCellXY(touchXY, spanXY[0], spanXY[1], screen);
        	if (result == null
        			|| result[0] == -1
        			|| result[1] == -1) {
        		foundCellSpan = false;
        	} else {
        		foundCellSpan = true;
        		cellXY[0] = result[0];
                cellXY[1] = result[1];
        	}
        } else {
            foundCellSpan = mWorkspace.getPagedView().findCellForSpan(cellXY, spanXY[0], spanXY[1], screen, mPendingAddInfo);
        }

        if (!foundCellSpan) {
            if (appWidgetId != -1) {
                // Deleting an app widget ID is a void call but writes to disk before returning
                // to the caller...
                new Thread("deleteAppWidgetId") {
                    public void run() {
                        mAppWidgetHost.deleteAppWidgetId(appWidgetId);
                    }
                }.start();
            }
            showOutOfSpaceMessage();
            return;
        }

       /** ID: configurable widget restore . AUT: chengliang . DATE: 2012.05.31 . S */
        // Build Launcher-specific widget info and save to database
        final LauncherAppWidgetInfo launcherInfo = new LauncherAppWidgetInfo(appWidgetId);
        launcherInfo.spanX = spanXY[0];
        launcherInfo.spanY = spanXY[1];
        launcherInfo.needConfig = 0;
        
        launcherInfo.screen = screen;
        launcherInfo.cellX = cellXY[0];
        launcherInfo.cellY = cellXY[1];
        launcherInfo.container = container;
               
        XLauncherModel.addItemToDatabase(this, launcherInfo,
                container, screen, cellXY[0], cellXY[1], false);

        if (!mRestoring) {
            // R2 view to add
            View v = null;
            // Perform actual inflation because we're live
            launcherInfo.hostView = mAppWidgetHost.createView(this.getApplicationContext(), appWidgetId, appWidgetInfo);

            launcherInfo.hostView.setAppWidget(appWidgetId, appWidgetInfo);
//            launcherInfo.hostView.setTag(launcherInfo);

            /** ID: Lenovo gadget add-on. AUT: chengliang . DATE: 2012.01.11 . S */
            boolean isGadget = com.lenovo.launcher2.gadgets.GadgetUtilities
                    .isGadget(appWidgetInfo.provider);
            if (!isGadget) {
                v = launcherInfo.hostView;
                R2.echo("Touch 1");
            }
            else {
                R2.echo("Touch 2");
                if(this !=null){
//                v = com.lenovo.launcher2.gadgets.GadgetUtilities
//                        .fetchView(this, appWidgetInfo.provider);
//                v.setTag(launcherInfo);
                }
            }
            launcherInfo.hostView.setTag(launcherInfo);
            launcherInfo.hostView.setVisibility(View.VISIBLE);
            launcherInfo.notifyWidgetSizeChanged(this);
            mWorkspace.addInScreen(new XViewContainer(mMainSurface,
            		launcherInfo.spanX * mWorkspace.getPagedView().getCellWidth(), 
            		launcherInfo.spanY * mWorkspace.getPagedView().getCellHeight(), 
            		launcherInfo.hostView), launcherInfo);
            /** ID: Lenovo gadget add-on. AUT: chengliang . DATE: 2012.01.11 . N */
           /** ID: configurable widget restore . AUT: chengliang . DATE: 2012.05.31 . E */
//            addWidgetToAutoAdvanceIfNeeded(launcherInfo.hostView, appWidgetInfo);
            
            refreshMngViewDelayed(1000L, screen);
            
          	/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-03-04 S*/
            if( appWidgetInfo.provider != null)
            {
            	 Reaper.processReaper( this, 
                  	   Reaper.REAPER_EVENT_CATEGORY_WIDGET, 
          			   Reaper.REAPER_EVENT_ACTION_WIDGET_ADD,
          			   appWidgetInfo.provider.getPackageName(), 
          			   Reaper.REAPER_NO_INT_VALUE );
            }
            /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-03-04 E*/  
        }
    }
    
    public int[] getSpanForWidget(ComponentName component, int minWidth, int minHeight, int[] spanXY) {
        if (spanXY == null) {
            spanXY = new int[2];
        }

        Rect padding = AppWidgetHostView.getDefaultPaddingForWidget(this, component, null);
        // We want to account for the extra amount of padding that we are adding to the widget
        // to ensure that it gets the full amount of space that it has requested
        int requiredWidth = minWidth + padding.left + padding.right;
        int requiredHeight = minHeight + padding.top + padding.bottom;
        return XCellLayout.rectToCell(getResources(),
        		requiredWidth, requiredHeight, null, mWorkspace.getPagedView().getCellCountX(), mWorkspace.getPagedView().getCellCountY());
    }

    int[] getSpanForWidget(AppWidgetProviderInfo info, int[] spanXY) {
        return getSpanForWidget(info.provider, info.minWidth, info.minHeight, spanXY);
    }
    
    void showOutOfSpaceMessage() {
        //change by xingqx 2012.11.13
    	mMainSurface.removeCallbacks(mOutOfSpaceMsgRunnable);
    	mMainSurface.post(mOutOfSpaceMsgRunnable);        
    }

	@Override
	public boolean setLoadOnResume() {
		if(LoadBootPolicy.getInstance(this).getDefaultProfileProcessingState()){
			return false;
		}
    	
        if (mPaused) {
            Log.i(TAG, "setLoadOnResume");
            mOnResumeNeedsLoad = true;
            return true;
        } else {
            return false;
        }
	}

	@Override
	public int getCurrentWorkspaceScreen() {
		if (mWorkspace != null) {
            return mWorkspace.getCurrentPage();
        } else {
            return SCREEN_COUNT / 2;
        }
	}

	@Override
	public void startBinding() {
		/** ID: Profile restoring. AUT: chengliang . DATE: 2012.03.22 . S */
		if(LoadBootPolicy.getInstance(this).getDefaultProfileProcessingState()){
			return;
		}
		/** ID: Profile restoring. AUT: chengliang . DATE: 2012.03.22 . E */
    	
        final XWorkspace workspace = mWorkspace;
        if (mDragLayer != null) {
            mDragLayer.setTouchable(false);
        }
        /*RK_ID: RK_SCREEN_CELLCOUNT . AUT:liuyg1@lenovo.com DATE: 2012-11-19START*/ 
        mWorkspace.setCurrentPage(mRestoreScreen == -1 ? mWorkspace.getCurrentPage() : mRestoreScreen);//fix bug Bug 172436 
        mRestoreScreen = -1;
        /*RK_ID: RK_SCREEN_CELLCOUNT . AUT:liuyg1@lenovo.com DATE: 2012-11-19 END*/ 
        
        //////new new art
//        workspace.getPagedView().clearAllItems();
//        mWorkspace.clearDropTargets();
//        int count = workspace.getChildCount();
//        for (int i = 0; i < count; i++) {
//            // Use removeAllViewsInLayout() to avoid an extra requestLayout() and invalidate().
//            final CellLayout layoutParent = (CellLayout) workspace.getChildAt(i);
//            layoutParent.removeAllViewsInLayout();
//            layoutParent.requestLayout();//add by xingqx 2012.04.24
//        }
//        workspace.invalidate();//add by xingqx 2012.04.24
        mWidgetsToAdvance.clear();
//        if (mHotseat != null) {
//            mHotseat.resetLayout();
//        }
//
//
//        /* RK_ID: RK_QUICKACTION. AUT: liuli1 . DATE: 2012-08-16 . START */
//        // fix bug 169106
//        if (mQuickView != null) {
//            dismissQuickActionWindow();
//            dismissPopupChildDlg();
//        }
//        /* RK_ID: RK_QUICKACTION. AUT: liuli1 . DATE: 2012-08-16 . END */
//        // add by xingqx for fix bug 169202
//        mDragController.cancelDrag();
          //////new new art
        mModel.clearShortcutInfo();
	}

	@Override
	public void bindItems(ArrayList<ItemInfo> shortcuts, int start, int end) {
        setLoadOnResume();

        final XWorkspace workspace = mWorkspace;
        for (int i=start; i<end; i++) {
            final ItemInfo item = shortcuts.get(i);

            // Short circuit if we are loading dock items for a configuration which has no dock
            if (item.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
                if (mHotseat == null)
                {
                    R5.echo("bindItems mHotseat = " + mHotseat);
                }
                else //if (!isCommendItem(item))
                {
                	//dooba add.s
                	//判断应用是否重复，重复就删除                	
                	if (item.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION) {
                		if (mModel.checkShortcutInfoExist((ShortcutInfo)item)) {
                			XLauncherModel.deleteItemFromDatabase(this, item);
                			continue;
                		}
                		mModel.saveShortcutInfo((ShortcutInfo)item);
                	}
                	//dooba add.e
                    mHotseat.bindInfo(item, mIconCache);
                }
                
                continue;
            }

            switch (item.itemType) {
            /*AUT:zhanglq, zhanglq@bj.cobellink.com DATE:2012-2-10 start*/
                case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
                case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
                /*RK_ID:RK_SD_APPS zhangdxa 2013-4-15. S***/
                case LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT:
                /*RK_ID:RK_SD_APPS zhangdxa 2013-4-15. E***/
                    ShortcutInfo si = (ShortcutInfo) item;
                   //dooba add.s
                	//判断应用是否重复，重复就删除                	
                	if (item.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION) {
                		if (mModel.checkShortcutInfoExist((ShortcutInfo)item)) {
                			XLauncherModel.deleteItemFromDatabase(this, item);
                			continue;
                		}
                		mModel.saveShortcutInfo((ShortcutInfo)item);
                	}
                	//dooba add.e
                    workspace.addInScreen(si, mIconCache, false);
                    break;
                case LauncherSettings.Favorites.ITEM_TYPE_FOLDER:                    
                    /*RK_ID:RK_SD_APPS zhangdxa 2013-4-15. S***/
                    /*FolderInfo fInfo = (FolderInfo) item;
                    int length = fInfo.contents.size();
                    for (int j = length - 1; j >= 0; j--) {
                        ShortcutInfo child = fInfo.contents.get(j);
                        if (isCommendItem(child)) {
                            fInfo.remove(child);
                        }
                    }*/
                    /*RK_ID:RK_SD_APPS zhangdxa 2013-4-15. E***/
                	FolderInfo fInfo = (FolderInfo) item;
                	if(!fInfo.contents.isEmpty()){
                		workspace.addInScreen((FolderInfo) item);

                	}else{
                		/*PK_ID REMOVE DATE BASE AUTH:GECN1 S*/
                		removeFolder(fInfo);
                		XLauncherModel.deleteFolderContentsFromDatabase(this, fInfo);
                		XLauncherModel.deleteItemFromRAM(fInfo.id);
                		/*PK_ID REMOVE DATE BASE AUTH:GECN1 E*/
                	}
                    break;
            default:
            	/*RK_ID:RK_SD_APPS zhangdxa 2013-4-15. S***/
                //isCommendItem(item);
            	/*RK_ID:RK_SD_APPS zhangdxa 2013-4-15. E***/
                break;
            }
        }
        
//        workspace.requestLayout();
	}

	/*RK_ID:RK_SD_APPS zhangdxa 2013-4-15. S***/
   /* private boolean isCommendItem(ItemInfo item) {
        if (item.itemType == LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT
                || item.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION) {
            ShortcutInfo si = (ShortcutInfo) item;
            PackageManager pm = this.getPackageManager();
            ComponentName componentName = si.intent.getComponent();
            try {
                pm.getActivityInfo(componentName, 0);
            } catch (Exception e) {
                //XLauncherModel.deleteItemFromDatabase(this, si);
                android.util.Log.e("XLauncher", e.toString());
                return true;
            }
        } 
        else if (item.itemType == LauncherSettings.Favorites.ITEM_TYPE_FOLDER) {
            FolderInfo fInfo = (FolderInfo) item;
            int length = fInfo.contents.size();
            for (int j = length - 1; j >= 0; j--) {
                ShortcutInfo child = fInfo.contents.get(j);
                if (isCommendItem(child)) {
                    fInfo.remove(child);
                }
            }
            return false;
        }
        return false;
    }*/
	/*RK_ID:RK_SD_APPS zhangdxa 2013-4-15. E***/

    @Override
    public void bindOtherApps(LinkedList<ShortcutInfo> listInfo) {
        setLoadOnResume();
        if(listInfo == null || listInfo.size() == 0){
        	return ;
        }
        
		if(mWorkspace!=null){
			mWorkspace.filterApplicationExsitedInWorkspace(listInfo);
		}
		if(mHotseat!=null){
			mHotseat.filterApplicationExsitedInHotseat(listInfo);
		}
		
        Iterator<ShortcutInfo> it = listInfo.iterator();
        ShortcutInfo item;

        boolean isAdd = false;
        LinkedList<ShortcutInfo> toRemove = new LinkedList<ShortcutInfo>();
        while(it.hasNext()){
        	item = it.next();
        	//dooba add.s
            if (mModel.checkShortcutInfoExist(item)) {
            	toRemove.add(item);
            	continue;
        	}
            //dooba add.e
        	isAdd = addItemIntoScreen(item, (mWorkspace.getDefaultPage() == mWorkspace.getPageCount()-1));
        	if(!isAdd){
        		Log.d("gecn1", "======bindOtherApps remove");
        		toRemove.add(item);
        	} else {
        		//dooba add.s
        		mModel.saveShortcutInfo(item);
        		//dooba add.e
        	}
        }
        listInfo.removeAll(toRemove);
        mModel.surelyAddItemInDatabase(this, listInfo, null);
        
    }
    
	public boolean addItemIntoScreen(ShortcutInfo item, boolean addIntoNewScrenn) {
		if(item ==null || mWorkspace == null){
			Log.d("gecn1", "addItemIntoScreen  item ==null || mWorkspace == null");
			return false;
		}
		int cell[] = new int[3];
		boolean isEmptyCell = addIntoNewScrenn ? mWorkspace.findCellXYNextScreen(cell, item) : mWorkspace.findCellXY(cell,item);
		if (isEmptyCell) {
			item.spanX = 1;
			item.spanY = 1;
			item.container = LauncherSettings.Favorites.CONTAINER_DESKTOP;
			item.cellX = cell[0];
			item.cellY = cell[1];
			item.screen = cell[2];
			mWorkspace.addInScreen(item, mIconCache, false);
			return true;
		} else {
			XPagedView pagedView = mWorkspace.getPagedView();
			XPagedViewItem pagedViewItem = pagedView.findPageItemAt(cell[2],cell[0], cell[1]);
			DrawableItem drawableTarget = pagedViewItem.getDrawingTarget();
			if (drawableTarget != null) {
				if ((drawableTarget.getTag()) instanceof ShortcutInfo) {
					ShortcutInfo destInfo = new ShortcutInfo((ShortcutInfo) (drawableTarget.getTag()));
					pagedView.removePagedViewItem(pagedViewItem);
					final FolderInfo folderInfo = new FolderInfo();
					folderInfo.title = "";
					folderInfo.screen = cell[2];
					folderInfo.cellX = cell[0];
					folderInfo.cellY = cell[1];
					destInfo.screen = 0;
					destInfo.cellX = -1;
					destInfo.cellY = -1;

					item.screen = 0;
					item.cellX = -1;
					item.cellY = -1;

					XLauncherModel.addItemToDatabase(this, folderInfo,LauncherSettings.Favorites.CONTAINER_DESKTOP,folderInfo.screen, folderInfo.cellX,folderInfo.cellY, false);
					sFolders.put(folderInfo.id, folderInfo);
					destInfo.container = folderInfo.id;
					item.container = folderInfo.id;
					folderInfo.add(destInfo);
					XLauncherModel.updateItemInDatabase(this, destInfo);
					folderInfo.add(item);
					mWorkspace.addInScreen(folderInfo);
					return true;

				} else if ((drawableTarget) instanceof XFolderIcon) {
					XFolderIcon fi = (XFolderIcon) drawableTarget;
					item.container = fi.mInfo.id;
					// NOTE:item is not added into database
					fi.mInfo.add(item);
					return true;
				}
			}
		}
		return false;
	}
    

    @Override
	public void bindFolders(HashMap<Long, FolderInfo> folders) {
		setLoadOnResume();
        sFolders.clear();
        sFolders.putAll(folders);
		
	}

    
//    public int addNewScreen() {
//
//        Log.d("gecn1", "setupViews before     CELLLAYOUT_COUNTt   = " +  mWorkspace.getPagedView().getPageCount()  );
//
//        mWorkspace.addNewScreen();
//        int pageCnt = mWorkspace.getPagedView().getPageCount();
//        Log.d("gecn1", "setupViews after     CELLLAYOUT_COUNTt   = " +  mWorkspace.getPagedView().getPageCount()  );
//
//        SharedPreferences preferrences = getSharedPreferences(
//                CELLLAYOUT_COUNT, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = preferrences.edit();
//        editor.putInt(CELLLAYOUT_COUNT, pageCnt);
//        editor.commit();
//        Log.d("gecn1", "setupViews afteraaaa     CELLLAYOUT_COUNTt   = " + preferrences.getInt(CELLLAYOUT_COUNT, -1));
//
//
//        mLauncherService.mScreenCount = pageCnt;
//        return pageCnt-1;
//    }
	@Override
	public void finishBindingItems() {
		setLoadOnResume();

        if (mSavedState != null) {
//            if (!mWorkspace.hasFocus()) {
//                mWorkspace.getChildAt(mWorkspace.getCurrentPage()).requestFocus();
//            }
            mSavedState = null;
        }

        Log.d("gecn1", "finishBindingItems  Load Worksapce dialog remove1111111");
        /** ID: profile restore   AUT: Chengliang  PUP: fix bug, 153716  DATE: 2012.02.24   S.**/
        if (mSavedInstanceState != null) {
        	try {
        		super.onRestoreInstanceState(mSavedInstanceState);
        	} catch (Exception e) {
        		mModel.forceReloadWorkspce();
        	}
        	mSavedInstanceState = null;
        }
        /** ID: profile restore   AUT: Chengliang  PUP: fix bug, 153716  DATE: 2012.02.24   E.**/

        mWorkspaceLoading = false;

        // If we received the result of any pending adds while the loader was running (e.g. the
        // widget configuration forced an orientation change), process them now.
        for (int i = 0; i < sPendingAddList.size(); i++) {
            completeAdd(sPendingAddList.get(i));
        }
        sPendingAddList.clear();
        
        for (int i = 0; i < sPendingMsg.size(); i++) {
        	handlePendingMsg(sPendingMsg.get(i));
        }
        sPendingMsg.clear();
//
//        // Update the market app icon as necessary (the other icons will be managed in response to
//        // package changes in bindSearchablesChanged()
//        //updateAppMarketIcon();
//
//        mWorkspace.post(mBuildLayersRunnable);
//bugfix 17646
        //added by yumina for the bug BLADEX-2575 finishBinding nullpointer error
        Log.d("gecn1", "finishBindingItems  Load Worksapce dialog remove");
        Log.d("gecn1", "finishBindingItems  Load Worksapce dialog " + XLauncher.this + "     pid = " +  android.os.Process.myPid());
        
		if(mLoadWorkspaceDialog != null && mLoadWorkspaceDialog.isShowing()){
			Log.d("gecn1", "finishBindingItems  Load Worksapce dialog remove  " + mLoadWorkspaceDialog);
			mLoadWorkspaceDialog.dismiss();
			mLoadWorkspaceDialog = null;
		}else if(mLoadWorkspaceDialog != null && !mLoadWorkspaceDialog.isShowing()){
			Log.d("gecn1", "finishBindingItems  Load Worksapce dialog  not showing " + mLoadWorkspaceDialog);
			mLoadWorkspaceDialog = null;
		}else{
			Log.d("gecn1", "finishBindingItems  Load Worksapce dialog  is null" );
		}
        
        

        try{
        mHotseat.validateHotseat();
        //mModel.setWorkspaceLoaded();
        mWallpapperBlur.checkLiveWallpaper();
        /*** RK_ID: HIGH_QUALITY_EFFECTS. AUT: zhaoxy. DATE: 2013-10-16 . START***/
        if (mWallpapperBlur.isEnable() && SettingsValue.ENABLE_HIGH_QUALITY_EFFECTS) {
        /*** RK_ID: HIGH_QUALITY_EFFECTS. AUT: zhaoxy. DATE: 2013-10-16 . END***/
            mWallpapperBlur.buildBlurBitmap();
        }
        }catch(Exception e){
        }

        if (mDragLayer != null) {
            mDragLayer.setTouchable(true);
        }
        
        //DisplayMetrics dm = new DisplayMetrics();

        //this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        
//        if (isUsedBitmapCache())
//        {
//        	mWorkspace.getPagedView().enableBitmapCache();
//        }
	}
    public boolean isUsedBitmapCache() {
    	String str = SystemProperties.get("ro.product.name","unknow");
    	if("S960".equals(str) || "S968t".equals(str))
    	{
    		return true;
    	}
    	
    	return false;
//        DisplayMetrics dm = new DisplayMetrics();
//        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
//        return (Float.compare(dm.density, 3f) >= 0);
    }
	
	private void handlePendingMsg(String msg) {
		if (PENDING_PREVIEW_SCREEN.equals(msg)) {
			mGestureInProgress = false;
            showWidgetSnap(true);
		} else if (PENDING_EDIT_SCREEN.equals(msg)) {
			showWidgetSnap(true, XScreenMngView.State.ADDED);
		}
	}

	@Override
	public void bindAppWidget(final LauncherAppWidgetInfo item) {
		setLoadOnResume();
        if(item == null) {
            return;
        }
		
		Log.d("zdx1", "bindAppWidget: " + item+", intent:"+ item.intent +", itemtype:"+ item.itemType);
	    
    	/** ID: Lenovo gadget add-on. AUT: chengliang . DATE: 2012.01.11 . S */
		if (bindAsGadgetIfNeed(item)) {
			com.lenovo.launcher2.customizer.Debug.R2.echo(" Add Gadget Over. ");
			return;
		}
    	/** ID: Lenovo gadget add-on. AUT: chengliang . DATE: 2012.01.11 . E */

        final long start = DEBUG_WIDGETS ? SystemClock.uptimeMillis() : 0;
        if (DEBUG_WIDGETS) {
            Log.d(TAG, "bindAppWidget: " + item+", intent:"+ item.intent +", itemtype:"+ item.itemType);
        }
        final XWorkspace workspace = mWorkspace;

        int appWidgetId = item.appWidgetId;
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        
        /* RK_ID: FOR_BOOKMARK_WIDGET . AUT: SHENCHAO1 . DATE: 2013-09-02 . S */
        ComponentName cn1 = null;
        if (item.intent == null) {
        	if(appWidgetInfo != null){
			    cn1 = appWidgetInfo.provider;
        	}
		} else {
			cn1 = item.intent.getComponent();
		}
       boolean isBookMark = false;
       if(cn1 != null){
    	isBookMark = (cn1.getClassName().equals("com.lenovo.browser.widget.BookmarkThumbnailWidgetProvider") ||
        		cn1.getClassName().equals("com.android.browser.widget.BookmarkThumbnailWidgetProvider")) ? true : false;
       }
       /* RK_ID: FOR_BOOKMARK_WIDGET . AUT: SHENCHAO1 . DATE: 2013-09-02 .E*/
        /*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. S***/
        View widget = null;
        if( item.itemType == LauncherSettings.Favorites.ITEM_TYPE_COMMEND_APPWIDGET ){
            if( item.iconBitmap == null) {
                 return;
            }
            if( item.intent != null){
                ComponentName cn = item.intent.getComponent();
                if(processLeosWidget(item, cn)){
            	    return;
                }
            }

            /*Bitmap bitmapTmp = item.iconBitmap.copy(Bitmap.Config.ARGB_8888, true);
            Bitmap bitmapBase = com.lenovo.launcher2.customizer.Utilities.newInstance().bitmapStampForWidgetWithSnap( 
            		bitmapTmp, 
            		((BitmapDrawable)getResources().getDrawable(R.drawable.stamp_widget)).getBitmap());
        	item.iconBitmap = bitmapBase;*/
        	
            item.commendView = new FavoriteWidgetView(this, item);
            item.commendView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onCommendViewClick(item);
				}
			});
            widget = item.commendView;
            /* RK_ID: BUG20962 . AUT: SHENCHAO1 . DATE: 2013-08-22 . S */
        } else if( item.needConfig == 1 && item.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET && !isBookMark){
        	if (appWidgetInfo == null) {
                XLauncherModel.deleteItemFromDatabase(this, item);
            	return;
            }
//         	item.iconBitmap = com.lenovo.launcher2.customizer.Utilities.newInstance().bitmapStampForWidgetWithSnap(
//        			getOriginalAppWidgetInfoBitmap(item), ((BitmapDrawable)getResources().getDrawable(R.drawable.stamp_widget_config)).getBitmap());
        	item.commendView = new FavoriteWidgetView(this, item);
        	item.commendView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
		            int appWidgetId = item.appWidgetId;        
		            AppWidgetProviderInfo widgetInfo = 
		            		AppWidgetManager.getInstance(XLauncher.this).getAppWidgetInfo(appWidgetId);
		            // the target application may invalid the id so we check it
		            if( widgetInfo == null ){
		            	
		            	ContentResolver cr = getContentResolver();
		            	Cursor c = cr.query(LauncherSettings.Favorites.CONTENT_URI,
		            			new String[]{ LauncherSettings.Favorites.INTENT,
		            			LauncherSettings.Favorites.CELLX,
		            			LauncherSettings.Favorites.CELLY,
		            			LauncherSettings.Favorites.SCREEN,
		            			LauncherSettings.Favorites.ICON}, "appWidgetId=?", 
		            			new String[]{ "" + appWidgetId }, null);
		            	try {
							if( c.moveToNext() ){
								((FavoriteWidgetView)item.commendView).removeSelf();
								item.intent = Intent.parseUri(c.getString(c.getColumnIndex(LauncherSettings.Favorites.INTENT)), 0);
								item.cellX = c.getInt(c.getColumnIndex(LauncherSettings.Favorites.CELLX));
								item.cellY = c.getInt(c.getColumnIndex(LauncherSettings.Favorites.CELLY));
								item.screen = c.getInt(c.getColumnIndex(LauncherSettings.Favorites.SCREEN));
								item.itemType = LauncherSettings.Favorites.ITEM_TYPE_COMMEND_APPWIDGET;
								byte[] arr = c.getBlob(c.getColumnIndex(LauncherSettings.Favorites.ICON));
								int len = arr.length;
								item.iconBitmap = BitmapFactory.decodeByteArray( arr, 0, len);
								bindAppWidget( item );
							}
						} catch (Exception e) {
							e.printStackTrace();
							return;
						} finally{
							c.close();
						}
		            }else{
		            	PendingAddWidgetInfo createInfo = new PendingAddWidgetInfo(widgetInfo, null,
		            			null);
		            	addAppWidgetImpl(appWidgetId, createInfo);
		            }
				}
			});
        	widget = item.commendView;
        	 /* RK_ID: BUG20962 . AUT: SHENCHAO1 . DATE: 2013-08-22 . E */
        }else{
        	if (appWidgetInfo == null) {
                XLauncherModel.deleteItemFromDatabase(this, item);
            	return;
            }
        	item.hostView = mAppWidgetHost.createView(this.getApplicationContext(), appWidgetId, appWidgetInfo);
            item.hostView.setAppWidget(appWidgetId, appWidgetInfo);
            item.hostView.setTag(item);
            item.onBindAppWidget(this);
            widget = item.hostView;
        }
        
        workspace.addInScreen(new XViewContainer(mMainSurface, 
        		item.spanX * mWorkspace.getPagedView().getCellWidth(), 
        		item.spanY * mWorkspace.getPagedView().getCellHeight(), widget/*item.hostView*/), item);
        
        addWidgetToAutoAdvanceIfNeeded(widget/*item.hostView*/, appWidgetInfo);
        /*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. E***/


        if (DEBUG_WIDGETS) {
            Log.d(TAG, "bound widget id="+item.appWidgetId+" in "
                    + (SystemClock.uptimeMillis()-start) + "ms");
        }
	}

	@Override
	public void bindAllApplications(final ArrayList<ApplicationInfo> apps) {
		if(LoadBootPolicy.getInstance(this).getDefaultProfileProcessingState()){
			return;
		}

        boolean single = SettingsValue.getSingleLayerValue(this);
        Log.i(TAG, "bindAllApplications~~ single is  ====" + single);
        if (single) {
            return;
        }

		mMainSurface.post(new Runnable() {
            public void run() {
                if (mMainSurface != null) {
                    /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. START***/
                    for (int i = apps.size() - 1; i >= 0; i--) {
                        if (apps.get(i).hidden) {
                            if (HiddenApplist.DEBUG) Log.d(HiddenApplist.TAG, "bindAllApplications remove " + i + " cmp=" + apps.get(i).componentName.flattenToShortString());
                            apps.remove(i);
                        }
                    }
                    /*** RK_ID: APP_HIDDEN. AUT: zhaoxy . DATE: 2012-04-23. END***/
                    /*** RK_ID: APP_SORT. AUT: zhaoxy . DATE: 2012-05-09. START***/
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(XLauncher.this);
                    int sortmodeAll = preferences.getInt(SettingsValue.KEY_APPLIST_CURRENT_SORTMODE, 0);
                    final int sortmode = sortmodeAll >> 4;
                    boolean isTopLastest = (sortmodeAll & 0xf) != SettingsValue.SORT_BY_ASC;;
                    switch (sortmode) {
                    case -1:
                    case 3:
                    case 4:
                        sortByComparator(apps, AllAppSortHelper.HISTORY_COMPARATOR);
                        break;
                    case 1:
                        sortByComparator(apps, AllAppSortHelper.NAME_COMPARATOR);
                        break;
                    case 2:
                        if (isTopLastest) {
                            sortByComparator(apps, AllAppSortHelper.FIRST_INSTALL_COMPARATOR_DES);
                        } else {
                            sortByComparator(apps, AllAppSortHelper.FIRST_INSTALL_COMPARATOR_ASC);
                        }
                        break;
                    /*case 3:
                        if (mModel.getUsageStatsMonitor() != null) {
                            mModel.getUsageStatsMonitor().updateImm();
                        }
                        sortByComparator(apps, AllAppSortHelper.LAUNCH_COUNT_COMPARATOR);
                        break;
                    case 4:
                        if (mModel.getUsageStatsMonitor() != null) {
                            mModel.getUsageStatsMonitor().updateImm();
                        }
                        sortByComparator(apps, AllAppSortHelper.LAST_RESUME_TIME_COMPARATOR);
                        break;*/
                    case 0:
                    default:
                        sortByComparator(apps, AllAppSortHelper.REGULAR_COMPARATOR);
                        break;
                    }
                    /* AUT: liuli liuli0120@use.com.cn DATE: 2012-01-31 START */
                    //final ArrayList<ApplicationInfo> newApps = mApplicationThread.startSortAllApps(apps);
                     mMainSurface.setApps(apps);
                    /* AUT: liuli liuli0120@use.com.cn DATE: 2012-01-31 END */
                    /*** RK_ID: APP_SORT. AUT: zhaoxy . DATE: 2012-05-09. END***/
                }
            }
        });
	}

	@Override
	public void bindAppsAdded(final ArrayList<ApplicationInfo> apps) {
		if(LoadBootPolicy.getInstance(this).getDefaultProfileProcessingState()){
			return;
		}
        
        TaskRunnable addRunnable = new TaskRunnable() {

			@Override
			public void run() {
				android.util.Log.i("dooba", "--------->doAdddddd--------------");
				
				setLoadOnResume();
		        removeDialog(DIALOG_CREATE_SHORTCUT);
		        addItem(apps,SettingsValue.getSingleLayerValue(XLauncher.this));
		        
		        // new art
		        if (mMainSurface != null) {
		        	mMainSurface.addApps(apps);
		        }
				
		        if (mWorkspace != null && mWorkspace.getState() == XWorkspace.State.SCR_MGR
		        		&& mScreenMngView != null) {
		            mScreenMngView.updateTabContent();
		        }
		        
		        checkMccPackage(apps);
			}

			@Override
			public boolean getAnimFlag() {
				return false;
			}        	
        };
        
        if (mWorkspace != null
        		&& (mWorkspace.isReordering() || !mPendingRunnableList.isEmpty())) {
        	mPendingRunnableList.add(addRunnable);
        } else {
        	mRecorderHandler.post(addRunnable);
        	mRecorderHandler.post(new Runnable() {
    			@Override
    			public void run() {
    				//add by zhanggx1 for removing on 2013-11-13 . s
    		        autoReorder();
    		        //add by zhanggx1 for removing on 2013-11-13 . e
    			}
    			
    		});
        }
    }
	
	
//	   @Override
//	    public void bindAppsAvialiableAdded(ArrayList<ApplicationInfo> apps) {
//	        if(LoadBootPolicy.getInstance(this).getDefaultProfileProcessingState()){
//	            return;
//	        }
//
//	        setLoadOnResume();
//	        removeDialog(DIALOG_CREATE_SHORTCUT);
//	        Set<ApplicationInfo>updateApps = new HashSet<ApplicationInfo>();
//	        if (mWorkspace != null) {
//	            mWorkspace.updateShortcuts(apps, mIconCache,);
//	        }
//	        if(mHotseat != null){
//	        	mHotseat.updateShortcuts(apps, mIconCache);
//	        }
//	        // new art
//	        if (mMainSurface != null) {
//	            mMainSurface.addApps(apps);
//	        }
//	        
//	        if (mWorkspace != null && mWorkspace.getState() == XWorkspace.State.SCR_MGR
//	                && mScreenMngView != null) {
//	            mScreenMngView.updateTabContent();
//	        }
//	    }

	private void addItem(ArrayList<ApplicationInfo> apps, boolean added) {
		List<ApplicationInfo> ls =null;
		if(mWorkspace!=null){
			ls= mWorkspace.filterApplicationExsitedInWorkspace(apps,mIconCache);
		}
		if(mHotseat!=null){
			ls = mHotseat.filterApplicationExsitedInHotseat(ls,mIconCache);
		}
		if(ls ==null)return;
		// mod by caozz
		if (SettingsValue.isAutoAppsClassify(this) && false) {
			AppsClassificationData data = new GetAppsCategory(this, ls);
		} else {
			Iterator<ApplicationInfo> it = ls.iterator();
			//add by zhanggx1 for refresh mng view.s
			List<Integer> screenList = new ArrayList<Integer>();
			//add by zhanggx1 for refresh mng view.e
			while (it.hasNext()) {
				ShortcutInfo childInfo = new ShortcutInfo(it.next());
				//dooba add.s
	            if (mModel.checkShortcutInfoExist(childInfo)) {
	            	continue;
	        	}
				boolean result = addItemIntoScreen(childInfo, (mWorkspace.getDefaultPage() == mWorkspace.getPageCount()-1));
				if (result) {
					mModel.saveShortcutInfo(childInfo);
				}
				//dooba add.e
				Log.d("gecn1", "updateOrAddItem  childInfo =  "
						+ childInfo.container);
				//add by zhanggx1 for refresh mng view.s
				if (childInfo.container == LauncherSettings.Favorites.CONTAINER_DESKTOP
						&& !screenList.contains(childInfo.screen)) {
					screenList.add(childInfo.screen);
				}
				//add by zhanggx1 for refresh mng view.e
				XLauncherModel.addItemToDatabase(this, childInfo,
						childInfo.container, childInfo.screen, childInfo.cellX,
						childInfo.cellY, false);
			}
			//add by zhanggx1 for refresh mng view.s
			refreshMngViewOnUpdateWorkspace(screenList);
			//add by zhanggx1 for refresh mng view.e
		}

	}
    


    @Override
	public void bindAppsUpdated(final ArrayList<ApplicationInfo> apps) {
		if(LoadBootPolicy.getInstance(this).getDefaultProfileProcessingState()){
			return;
		}
        
        TaskRunnable updateRunnable = new TaskRunnable() {
			@Override
			public void run() {
				setLoadOnResume();
				
//		        removeDialog(DIALOG_CREATE_SHORTCUT);
		        if (mWorkspace != null ) {
		            mWorkspace.updateShortcuts(apps,mIconCache);
		        }
		        if(mHotseat != null){
		        	mHotseat.updateShortcuts(apps, mIconCache);
		        }
		        if (mMainSurface != null) {
		        	mMainSurface.updateApps(apps);
		        }
		        if (mWorkspace != null && mWorkspace.getState() == XWorkspace.State.SCR_MGR && mScreenMngView != null) {
		            mScreenMngView.updateTabContent();
		        }
		        checkMccPackage(apps);
			}

			@Override
			public boolean getAnimFlag() {
				return false;
			}        	
        };
//        removeDialog(DIALOG_CREATE_SHORTCUT);
        if (mWorkspace != null
        		&& (mWorkspace.isReordering() || !mPendingRunnableList.isEmpty())) {
        	mPendingRunnableList.add(updateRunnable);
        } else {
        	mRecorderHandler.post(updateRunnable);
        	mRecorderHandler.post(new Runnable() {
    			@Override
    			public void run() {
    				//add by zhanggx1 for removing on 2013-11-13 . s
    		        autoReorder();
    		        //add by zhanggx1 for removing on 2013-11-13 . e
    			}
    			
    		});
        }
	}

	@Override
	public void bindAppsRemoved(final ArrayList<ApplicationInfo> apps,
			final boolean permanent) {
		/** ID: Profile restoring. AUT: chengliang . DATE: 2012.03.22 . S */
		if(LoadBootPolicy.getInstance(this).getDefaultProfileProcessingState()){
			return;
		}
		/** ID: Profile restoring. AUT: chengliang . DATE: 2012.03.22 . E */

        removeDialog(DIALOG_CREATE_SHORTCUT);        
        
        TaskRunnable removeRunnable = new TaskRunnable() {

			@Override
			public void run() {
				android.util.Log.i("dooba", "------------>doAppRemove----------------");
				if (permanent) {
		             if(mDragController != null && (mScreenMngView == null || !isScreenMngOpenedNotExiting())){
		                mDragController.cancelDrag();
		             }
		            if (mWorkspace != null) {
		                mWorkspace.removeItems(apps);
		                //dooba add.s
		                for (ApplicationInfo info : apps) {
		                	mModel.removeFromShortcutInfos(info.componentName);
		                }
		                //dooba add.e
		            }
		            if (mHotseat != null) {
		                mHotseat.removeItems(apps);
		            }
		        }else{
//		        	//update shoutcut commendshortcut
//		        	  if (mWorkspace != null) {
//		        		  mWorkspace.deleteShortcuts(apps, mIconCache);
//		        		  mWorkspace.updateShortcuts(apps,mIconCache,null,true);
//		              }
//		        	  if(mHotseat !=null){
//		        		  mHotseat.deleteShortcuts(apps, mIconCache);
//		        		  mHotseat.updateShortcuts(apps,mIconCache,null,true);
//		        	  }
		        	
		        }
		        
		        if (mMainSurface != null) {
		        	mMainSurface.removeApps(apps);
		        }
		        // Notify the drag controller
//		        mDragController.onAppsRemoved(apps, this);
				
		        if (mWorkspace != null && mWorkspace.getState() == XWorkspace.State.SCR_MGR && mScreenMngView != null) {
		            mScreenMngView.updateTabContent();
		        }
		        checkMccPackage(apps);
			}

			@Override
			public boolean getAnimFlag() {
				return false;
			}
        	
        };
        
        if (mWorkspace != null
        		&& (mWorkspace.isReordering() || !mPendingRunnableList.isEmpty())) {
        	mPendingRunnableList.add(removeRunnable);
        } else {
        	mRecorderHandler.post(removeRunnable);
        	mRecorderHandler.post(new Runnable() {
    			@Override
    			public void run() {
    				//add by zhanggx1 for removing on 2013-11-13 . s
    		        autoReorder();
    		        //add by zhanggx1 for removing on 2013-11-13 . e
    			}
    			
    		});
        }
	}

	@Override
	public void bindPackagesUpdated() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isAllAppsVisible() {
	    return SettingsValue.getSingleLayerValue(this);
	}

	@Override
	public void bindSearchablesChanged() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initDockAddIcons() {
		// for theme
        if (mHotseat != null) {
//            mHotseat.initDockAddIcons();
            mHotseat.initForTheme();
        }
	}

	@Override
	public void initThemeElements() {
		// for theme
		if (mAppListView != null) {
			mAppListView.setAppsWallpaper(true);
		}
	}

	@Override
	public void setProfileBackupEnable(String action) {
		if (Intent.ACTION_DEVICE_STORAGE_LOW.equals(action)) {
            mProfileEnabled = false;
        } else if (Intent.ACTION_DEVICE_STORAGE_OK.equals(action)) {
            mProfileEnabled = true;
        }
	}

	@Override
	public void bindAppsHidden(ArrayList<ApplicationInfo> apps) {
        boolean single = SettingsValue.getSingleLayerValue(this);
        Log.i(TAG, "bindAppsHidden~~ single is  ====" + single);
        if (single) {
            return;
        }

		setLoadOnResume();
        // new art
        if (mMainSurface != null) {
            mMainSurface.hideApps(apps);
        }
        // new art
	}

	@Override
	public void restartLauncher() {
		if(mAppWidgetHost!=null){
			mAppWidgetHost.stopListening();
		}
    	android.os.Process.killProcess(android.os.Process.myPid());
	}

	@Override
	public void bindLeosWidget(LenovoWidgetViewInfo item) {
		setLoadOnResume();

		Log.d("liuyg1", "bindLoesWidget: cellX = " + item.cellX + " cellY = "
				+ item.cellY + " spanX = " + item.spanX + " spanY = "
				+ item.spanY + "item.className = " + item.className
				+ "item.packageName = " + item.packageName);
		String packageName = getPackageName();
		item.componentName = new ComponentName(packageName, item.className);
		View v;
		if (item.packageName.equals(getPackageName())) {
			Log.d("liuyg1", "GadgetUtilities.fetchView ");
			v = com.lenovo.launcher2.gadgets.GadgetUtilities.fetchView(
					this, item.componentName);
		} else {
			Log.d("liuyg1", "getLeosWidgetViewToWorkspace ");
			if (item.className != null) {

			}
			v = (View) getLeosWidgetViewToWorkspace(item);
		}
		if (v == null) {
			if (XLauncherModel.sLeosWidgets != null) {
				Log.d("liuyg1", "LauncherModel.sLeosWidgets.remove(item); ");
				XLauncherModel.sLeosWidgets.remove(item);
			}
			XLauncherModel.deleteItemFromDatabase(this, item);
			return;
		}
//		v.setTag(item);

//		int screen = item.screen;

		mWorkspace.addInScreen(new XViewContainer(mMainSurface, 
				item.spanX * mWorkspace.getPagedView().getCellWidth(), 
				item.spanY * mWorkspace.getPagedView().getCellHeight(), v), item);

	}

	@Override
	public XLauncher getLauncherInstance() {
		// TODO Auto-generated method stub
		return this;
	}
	
    public void workspacePickApplication(boolean isFolder) {
        int page = mWorkspace.getCurrentPage();
        mEmptyPointList = mWorkspace.getPagedView().findVacantCellNumber(page);

        if (mEmptyPointList != null && mEmptyPointList.size() > 0) {
        	boolean addThroughly = false;//SettingsValue.getSingleLayerValue(this);
        	if (addThroughly) {
        		addFolderOrShortcuts(true, null, null);
        		mWaitingForResult = false;
        		this.refreshMngView(mWorkspace.getCurrentPage());
        	} else {
	            Intent intent = new Intent("android.intent.action.WORKSPACE_PICK_SHORTCUT");
	            intent.putExtra("EMPTYNUM", mEmptyPointList.size());
	            intent.putExtra("ISFOLDER", isFolder);
	            intent.putExtra(SettingsValue.EXTRA_CURRENT_PAGE, page);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivityForResult(intent, REQUEST_PICK_MOREAPPLICATION);
        	}
        } else {
            Toast.makeText(XLauncher.this, R.string.workspace_pick_applications_nospace, Toast.LENGTH_SHORT).show();
            mWaitingForResult = false;
        }
        
        /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-3-26 S*/
        if( isFolder){
	        Reaper.processReaper( XLauncher.this, 
	        	   Reaper.REAPER_EVENT_CATEGORY_DESKTOPADD, 
				   Reaper.REAPER_EVENT_ACTION_DESKTOPADD_FOLDER,
				   Reaper.REAPER_NO_LABEL_VALUE, 
				   Reaper.REAPER_NO_INT_VALUE );
        }else{
	        Reaper.processReaper( XLauncher.this, 
	        	   Reaper.REAPER_EVENT_CATEGORY_DESKTOPADD, 
				   Reaper.REAPER_EVENT_ACTION_DESKTOPADD_APPLICATION,
				   Reaper.REAPER_NO_LABEL_VALUE, 
				   Reaper.REAPER_NO_INT_VALUE );   
        }
        /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-3-26 E*/
    }
    
    public boolean isWorkspaceLocked() {
        return mWorkspaceLoading || mWaitingForResult;
    }
    
    /**
     * Returns whether we should delay spring loaded mode -- for shortcuts and widgets that have
     * a configuration step, this allows the proper animations to run after other transitions.
     */
    private boolean completeAdd(PendingAddArguments args) {
        boolean result = false;
        switch (args.requestCode) {
            case REQUEST_PICK_APPLICATION:
                completeAddApplication(args.intent, args.container, args.screen, args.cellX,
                        args.cellY);
                break;
            case REQUEST_PICK_SHORTCUT:
                /*** fixbug 170492  . AUT: zhaoxy . DATE: 2012-09-13. START***/
                workspace_occupied_needsave = true;
                /*** fixbug 170492  . AUT: zhaoxy . DATE: 2012-09-13. END***/
                processShortcut(args.intent);
                break;
            case REQUEST_CREATE_SHORTCUT:
                completeAddShortcut(args.intent, args.container, args.screen, args.cellX,
                        args.cellY);                
                result = true;
                break;
            case REQUEST_PICK_APPWIDGET:
                addAppWidgetFromPick(args.intent);
                break;
            case REQUEST_CREATE_APPWIDGET:
                int appWidgetId = args.intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
                completeAddAppWidget(appWidgetId, args.container, args.screen);
                result = true;
                break;
            case REQUEST_PICK_WALLPAPER:
                // We just wanted the activity result here so we can clear mWaitingForResult
                break;
            /* AUT: xingqx xingqx@lenovo.com DATE: 2012-01-19 START */
//            case REQUEST_PICK_APPWIDGET_LIST:
//                int appWidgetId1 = args.intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
//                completeAddAppWidget(appWidgetId1, args.container, args.screen);
//                break;
            /* AUT: xingqx xingqx@lenovo.com DATE: 2012-01-19 END */
        }
        // In any situation where we have a multi-step drop, we should reset the add info only after
        // we complete the drop
        resetAddInfo();
        /*** AUT:zhaoxy . DATE:2012-03-08 . START***/
        if (mWorkspace!= null) {
            mWorkspace.invalidate();
        }
        
      //add by zhanggx1 for reordering.s
        autoReorder();
        //add by zhanggx1 for reordering.e
        refreshMngView(args.screen);
        /*** AUT:zhaoxy . DATE:2012-03-08 . END***/
        return result;
    }
    
    private void addMoreShortcutsOnce(boolean flag, String folderName, int originPage, int[] cellXY) {
        Log.e(TAG, "11111111111 in the appWidgetId=" + mIntentListInfo + " flag=" + flag + " pointlist="
                + mEmptyPointList);
        int currentPage = 0;
        if (mWorkspace.getCurrentPage() > 8) {
            currentPage = mWorkspace.getCurrentPage() % 9;
        } else {
            currentPage = mWorkspace.getCurrentPage();
        }

//        //liuli1, fix bug 168891
//        if (currentPage != originPage) {
//            currentPage = originPage;
//            mWorkspace.snapToPage(currentPage);
//        }

        if (flag) {
            loadIndex = 0;

            /* AUT: liuli1 DATE: 2012-02-13. START */
            maxLoadIndex = Math.min(mIntentListInfo == null ? 0 : mIntentListInfo.size(),
                    XLauncherModel.getCellCountX() * XLauncherModel.getCellCountY() * 5);
            /* AUT: liuli1 DATE: 2012-02-13. END */
            // for screen edit
//            if (maxLoadIndex == 0)
//                return;
//            
//            if (maxLoadIndex == 1) {
//                addIconCellIntoScreen(currentPage);
////                loadIndex++;
//                Toast.makeText(this, getString(R.string.no_folder_created), Toast.LENGTH_SHORT).show();
//                return;
//            }
            
            //startLoopLoadAddFolder(currentPage);
            int container = LauncherSettings.Favorites.CONTAINER_DESKTOP;
            final FolderInfo folderInfo = new FolderInfo();
            /*PK_ID:Folde name do not change when language changed AUTH:GECN1 S*/
            folderInfo.title = (folderName == null || folderName.equals("")) ? getText(R.string.folder_name) : folderName;
            /*PK_ID:Folde name do not change when language changed AUTH:GECN1 E*/

            int cellX = mEmptyPointList.get(0).x;
            int cellY = mEmptyPointList.get(0).y;
            if (cellXY != null && cellXY[0] != -1 && cellXY[1] != -1) {
            	cellX = cellXY[0];
            	cellY = cellXY[1];
            }
            // Update the model
            folderInfo.screen = currentPage;
            folderInfo.cellX = cellX;
            folderInfo.cellY = cellY;
            XLauncherModel.addItemToDatabase(this, folderInfo, container, currentPage, folderInfo.cellX, folderInfo.cellY, false);
            sFolders.put(folderInfo.id, folderInfo);
            
            mWorkspace.addInScreen(folderInfo);
            /*** RK_ID: XFOLDER.  AUT: zhaoxy . DATE: 2013-01-30 . START***/
            for (int i = loadIndex; i < maxLoadIndex; i++) {
                Intent cellintent = null;
                try {
                    cellintent = Intent.parseUri(mIntentListInfo.get(i), 0);
                } catch (URISyntaxException e) {
                    cellintent = new Intent();
                    e.printStackTrace();
                }
                final ShortcutInfo info = mModel.getShortcutInfo(getPackageManager(), cellintent, this);
                if (info != null) {
                    info.setActivity(cellintent.getComponent(), Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    info.container = ItemInfo.NO_ID;
                                        
                    info.cellX = 0;
                    info.cellY = 0;
                    info.screen = 0;
                }
                    folderInfo.add(info);
//                    View view = createShortcut(info);
//                    if (loadIndex == 1) {
//                        mWorkspace.createUserFolderIfNecessary(view, container, layout, mAddItemCell, folderName);
//                    } else {
//                        DragObject dragObject = new DragObject();
//                        dragObject.dragInfo = info;
//                        mWorkspace.addToExistingFolderIfNecessary(view, layout, mAddItemCell, dragObject, true);
//                    }                    
                }                
            /*** RK_ID: XFOLDER.  AUT: zhaoxy . DATE: 2013-01-30 . END***/
        } else {
            /* AUT: liuli1 DATE: 2012-02-13. START */
            // Bug 152767 liuli1
            maxLoadIndex = Math.min(mIntentListInfo == null ? 0 : mIntentListInfo.size(), mEmptyPointList.size());
            /* AUT: liuli1 DATE: 2012-02-13. END */
            loadIndex = 0;
            // delayLoadIconCell(currentPage);

            for (int i = loadIndex; i < maxLoadIndex; i++) {
                addIconCellIntoScreen(currentPage);
                loadIndex++;
            }
            /*
             * RK_ID: RK_LELAUNCHER_STYLE . AUT: chenrong2 . DATE: 2012-02-27 .
             * E
             */
        }
    }
    
    void addAppWidgetFromPick(Intent data) {
        // TODO: catch bad widget exception when sent
        int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        // TODO: Is this log message meaningful?
        addAppWidgetImpl(appWidgetId, null);
    }
    
    void addAppWidgetImpl(int appWidgetId, PendingAddWidgetInfo info) {
        AppWidgetProviderInfo appWidget = mAppWidgetManager.getAppWidgetInfo(appWidgetId);

        if (appWidget.configure != null) {
            // Launch over to configure widget, if needed
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(appWidget.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            if (info != null) {
                if (info.mimeType != null && !info.mimeType.isEmpty()) {
                    intent.putExtra(
                            InstallWidgetReceiver.EXTRA_APPWIDGET_CONFIGURATION_DATA_MIME_TYPE,
                            info.mimeType);

                    final String mimeType = info.mimeType;
                    final ClipData clipData = (ClipData) info.configurationData;
                    final ClipDescription clipDesc = clipData.getDescription();
                    for (int i = 0; i < clipDesc.getMimeTypeCount(); ++i) {
                        if (clipDesc.getMimeType(i).equals(mimeType)) {
                            final ClipData.Item item = clipData.getItemAt(i);
                            final CharSequence stringData = item.getText();
                            final Uri uriData = item.getUri();
                            final Intent intentData = item.getIntent();
                            final String key =
                                InstallWidgetReceiver.EXTRA_APPWIDGET_CONFIGURATION_DATA;
                            if (uriData != null) {
                                intent.putExtra(key, uriData);
                            } else if (intentData != null) {
                                intent.putExtra(key, intentData);
                            } else if (stringData != null) {
                                intent.putExtra(key, stringData);
                            }
                            break;
                        }
                    }
                }
            }

            startActivityForResultSafely(intent, REQUEST_CREATE_APPWIDGET);
            isConfiguringWidget = true;
        } else {
            // Otherwise just add it
            completeAddAppWidget(appWidgetId, info.container, info.screen);

            // Exit spring loaded mode if necessary after adding the widget
//            exitSpringLoadedDragModeDelayed(true, false);

            // added by liuli1, fix bug 171424
            resetAddInfo();
        }
    }
    /*
    class PendingAddWidgetInfo extends PendingAddItemInfo {
        int minWidth;
        int minHeight;
        boolean hasDefaultPreview;
        int previewImage;
        int icon;

        // Any configuration data that we want to pass to a configuration activity when
        // starting up a widget
        String mimeType;
        Parcelable configurationData;

        public PendingAddWidgetInfo(AppWidgetProviderInfo i, String dataMimeType, Parcelable data) {
            itemType = LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET;
            componentName = i.provider;
            minWidth = i.minWidth;
            minHeight = i.minHeight;
            hasDefaultPreview = i.previewImage <= 0;
            previewImage = i.previewImage;
            icon = i.icon;
            if (dataMimeType != null && data != null) {
                mimeType = dataMimeType;
                configurationData = data;
            }
        }
    }*/
    
    void startActivityForResultSafely(Intent intent, int requestCode) {
        try {
            startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {
            Toast.makeText(this, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Launcher does not have the permission to launch " + intent +
                    ". Make sure to create a MAIN intent-filter for the corresponding activity " +
                    "or use the exported attribute for this activity.", e);
        }
    }
	/**
     * Restores the previous state, if it exists.
     *
     * @param savedState The previous state.
     */
    private void restoreState(Bundle savedState) {
        if (savedState == null) {
            return;
        }

//        State state = intToState(savedState.getInt(RUNTIME_STATE, State.WORKSPACE.ordinal()));
//        if (state == State.APPS_CUSTOMIZE) {
//            showAllApps(false);
//        }

        final int currentScreen = savedState.getInt(RUNTIME_STATE_CURRENT_SCREEN, -1);
        if (mWorkspace != null && currentScreen > -1) {
//            mWorkspace.setCurrentPage(currentScreen);
            mRestoreScreen = currentScreen;
        }

        final long pendingAddContainer = savedState.getLong(RUNTIME_STATE_PENDING_ADD_CONTAINER, -1);
        final int pendingAddScreen = savedState.getInt(RUNTIME_STATE_PENDING_ADD_SCREEN, -1);

        if (pendingAddContainer != ItemInfo.NO_ID && pendingAddScreen > -1) {
            mPendingAddInfo.container = pendingAddContainer;
            mPendingAddInfo.screen = pendingAddScreen;
            mPendingAddInfo.cellX = savedState.getInt(RUNTIME_STATE_PENDING_ADD_CELL_X);
            mPendingAddInfo.cellY = savedState.getInt(RUNTIME_STATE_PENDING_ADD_CELL_Y);
            mRestoring = true;
        }

        boolean renameFolder = savedState.getBoolean(RUNTIME_STATE_PENDING_FOLDER_RENAME, false);
        if (renameFolder) {
            long id = savedState.getLong(RUNTIME_STATE_PENDING_FOLDER_RENAME_ID);
            mFolderInfo = mModel.getFolderById(this, sFolders, id);
            mRestoring = true;
        }

        /*** fixbug 170492  . AUT: zhaoxy . DATE: 2012-09-13. START***/
//        int countx = savedState.getInt("workspace_currscreen_countx", -1);
//        if (countx > 0) {
//            mOccupiedTmp = new boolean[countx][];
//            for (int i = 0; i < countx; i++) {
//                mOccupiedTmp[i] = savedState.getBooleanArray("workspace_currscreen_occupied_" + i);
//            }
//            workspace_occupied_needsave = true;
//        }
        /*** fixbug 170492  . AUT: zhaoxy . DATE: 2012-09-13. END***/
    }
    
    public boolean getProfileEnabled() {
        return mProfileEnabled;
    }
    
    private void addIconCellIntoScreen(int currentPage) {
        Intent cellintent = null;
        try {
            cellintent = Intent.parseUri(mIntentListInfo.get(loadIndex), 0);
        } catch (URISyntaxException e) {
            cellintent = new Intent();
            e.printStackTrace();
        }
        Point point = mEmptyPointList.get(loadIndex);
        final ShortcutInfo info = mModel.getShortcutInfo(getPackageManager(), cellintent, this);
        if (info != null) {
            info.setActivity(cellintent.getComponent(), Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            info.container = ItemInfo.NO_ID;
            
//            View shortcut = createShortcut(info);
            int container = LauncherSettings.Favorites.CONTAINER_DESKTOP;
//            mWorkspace.addInScreen(shortcut, container, currentPage, point.x, point.y, 1, 1, false);
            info.screen = currentPage;
            info.cellX = point.x;
            info.cellY = point.y;
            info.spanX = 1;
            info.spanY = 1;
            
            mWorkspace.addInScreen(info, mIconCache, false);
            XLauncherModel.addOrMoveItemInDatabase(this, info, container, currentPage, point.x, point.y);
        }
    }
        
    void addWidgetToAutoAdvanceIfNeeded(View hostView, AppWidgetProviderInfo appWidgetInfo) {
        if (appWidgetInfo == null || appWidgetInfo.autoAdvanceViewId == -1) return;
        View v = hostView.findViewById(appWidgetInfo.autoAdvanceViewId);
        if (v instanceof Advanceable) {
            mWidgetsToAdvance.put(hostView, appWidgetInfo);
            ((Advanceable) v).fyiWillBeAdvancedByHostKThx();
//            updateRunning();
        }
    }
    
	public boolean bindAsGadgetIfNeed(LauncherAppWidgetInfo item) {
		//setLoadOnResume();
		int appWidgetId = item.appWidgetId;
		AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager
				.getAppWidgetInfo(appWidgetId);
		Log.d("liuyg1", "bindAsGadgetIfNeed****id:"+ appWidgetId+", intent:"+ item.intent +", appWidgetInfo:"+appWidgetInfo);
		
		if (appWidgetInfo == null && item.intent == null) {
			Log.d("liuyg1", "bindAsGadgetIfNeed false");
			return false;
		}
		Log.d("liuyg1", "bindAsGadgetIfNeed true");
		ComponentName cn;
		if (item.intent == null) {
			cn = appWidgetInfo.provider;
		} else {
			cn = item.intent.getComponent();
		}
	
		return processLeosWidget(item, cn);
	}
	
	public void addLeosWidgetViewToWorkspace(LenovoWidgetViewInfo lenovoWidget) {
		long container = LauncherSettings.Favorites.CONTAINER_DESKTOP;
		int screen = lenovoWidget.screen;
		// int[] spanXY = getSpanForWidget(lenovoWidget, null);
		lenovoWidget.spanX = lenovoWidget.minWidth;
		lenovoWidget.spanY = lenovoWidget.minHeight;
		Log.d("liuyg1", "lenovoWidget.spanY =" + lenovoWidget.spanY);
		
		int[] spanXY = { lenovoWidget.spanX, lenovoWidget.spanY };
		int[] cellXY = mTmpAddItemCellCoordinates;
		int[] touchXY = mPendingAddInfo.dropPos;
		boolean foundCellSpan = false;
		if (lenovoWidget.cellX >= 0 && lenovoWidget.cellY >= 0) {
			boolean ret = mWorkspace.getPagedView().findCellForSpanRightHere(lenovoWidget.cellX,
					lenovoWidget.cellY, spanXY[0], spanXY[1], screen);
			if (ret) {
				cellXY[0] = lenovoWidget.cellX;
				cellXY[1] = lenovoWidget.cellY;
				foundCellSpan = true;
			} else {
				int dropPosX = (int)(lenovoWidget.cellX * mWorkspace.getPagedView().getCellWidth() + 1);
				int dropPosY = (int)(lenovoWidget.cellY * mWorkspace.getPagedView().getCellHeight() + 1);
				cellXY = mWorkspace.findNearestVacantArea(screen, dropPosX, dropPosY, spanXY[0], spanXY[1], null, null);
				foundCellSpan = (cellXY != null && cellXY[0] >= 0 && cellXY[1] >= 0);
			}
			
		} else if (touchXY != null) {
			// when dragging and dropping, just find the closest free spot
			int[] result = mWorkspace.findNearestVacantArea(screen, touchXY[0], touchXY[1],
					spanXY[0], spanXY[1], null, cellXY);
			lenovoWidget.cellX = cellXY[0];
			lenovoWidget.cellY = cellXY[1];
//			int[] result = mWorkspace.fin
			foundCellSpan = (result != null);
		} else {
			foundCellSpan = mWorkspace.getPagedView().findCellForSpan(cellXY, spanXY[0], spanXY[1], screen, lenovoWidget);
		}

		if (!foundCellSpan) {
			showOutOfSpaceMessage();
			return;
		}
		// lenovoWidget.spanX = spanXY[0];
		// lenovoWidget.spanY = spanXY[1];
		// fix bug 171763
		XLauncherModel.addItemToDatabase(this, lenovoWidget,
				LauncherSettings.Favorites.CONTAINER_DESKTOP,
				lenovoWidget.screen, cellXY[0], cellXY[1], false);
		XLauncherModel.sLeosWidgets.add(lenovoWidget);
		// args.cellY = mPendingAddInfo.cellY;
		Log.d("liuyg1", "className =" + lenovoWidget.className);
		Log.d("liuyg1", "packageName =" + lenovoWidget.packageName);
		View v;
		// if(lenovoWidget.className.equals(com.lenovo.launcher2.gadgets.GadgetUtilities.LOTUSDEFAULTVIEWHELPER)||
		// lenovoWidget.className.equals(com.lenovo.launcher2.gadgets.GadgetUtilities.TOGGLEWIDGETVIEWHELPER)
		// ||lenovoWidget.className.equals(com.lenovo.launcher2.gadgets.GadgetUtilities.WEATHERMAGICWIDGETVIEWHELPER)
		// ||lenovoWidget.className.equals(com.lenovo.launcher2.gadgets.GadgetUtilities.WEATHERWIDGETVIEWHELPER)){
		if (lenovoWidget.packageName.equals(getPackageName())) {
			Log.d("liuyg1", "GadgetUtilities.fetchView ");
			v = com.lenovo.launcher2.gadgets.GadgetUtilities.fetchView(
					this, lenovoWidget.componentName);
		} else {
			Log.d("liuyg1", "getLeosWidgetViewToWorkspace ");
			v = (View) getLeosWidgetViewToWorkspace(lenovoWidget);
		}
		if (v == null) {
			Log.d("liuyg1", "getLeosWidgetViewToWorkspace view == null");
			return;
		}
//		v.setTag(lenovoWidget);
		
		XViewContainer viewContainer = new XViewContainer(mMainSurface, 
				lenovoWidget.spanX * mWorkspace.getPagedView().getCellWidth(), 
				lenovoWidget.spanY * mWorkspace.getPagedView().getCellHeight(), v);
		mWorkspace.addInScreen(viewContainer, lenovoWidget);
//		viewContainer.manageVisibility(XViewContainer.VISIBILITY_SHOW_VIEW, null);
		viewContainer.manageVisibilityDirect(XViewContainer.VISIBILITY_SHOW_SHADOW, null);
		
		/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-3-26 S*/
        Reaper.processReaper( this, 
        	   Reaper.REAPER_EVENT_CATEGORY_WIDGET, 
			   Reaper.REAPER_EVENT_ACTION_WIDGET_IDEAADD,
			   lenovoWidget.className, 
			   Reaper.REAPER_NO_INT_VALUE );
        /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-3-26 E*/ 

        if (lenovoWidget.componentName != null
        		&& (GadgetUtilities.LOTUSDEFAULTVIEWHELPER.equals(lenovoWidget.componentName.getClassName())
        				|| lenovoWidget.componentName.getClassName().startsWith("com.lenovo.launcher2.weather."))) {
        	this.refreshMngViewDelayed(800L, lenovoWidget.screen);
        } else {
            this.refreshMngView(lenovoWidget.screen);
        }
	}
	
    protected Object getLeosWidgetViewToWorkspace(LenovoWidgetViewInfo lenovoWidget) {
        if (lenovoWidget.packageName != null && lenovoWidget.packageName.length() > 0) {
            try {
                String className = lenovoWidget.className;
                String apkName = getPackageManager().getApplicationInfo(lenovoWidget.packageName, 0).sourceDir;
                Log.d("liuyg1", "packageName ====================" + lenovoWidget.packageName + "className=" + lenovoWidget.className + "apkName="
                        + apkName);
                dalvik.system.PathClassLoader myClassLoader = new dalvik.system.PathClassLoader(apkName, "",
                        ClassLoader.getSystemClassLoader());
                Class classType = Class.forName(className, true, myClassLoader);
                Class[] args = new Class[] { Class.forName("android.content.Context") };
                Constructor cons = classType.getConstructor(args);
                return cons.newInstance(createPackageContext(lenovoWidget.packageName, Context.CONTEXT_RESTRICTED));
            } catch (Exception e) {
            	 Log.d("liuyg1", "Exception========");
                e.printStackTrace();
                //String log = e.getMessage();
                //System.out.print(log);
                return null;
            }
        }
        return null;
    }
    
    public LauncherAppWidgetHost getAppWidgetHost() {
        return mAppWidgetHost;
    }

    /**
    * The action after clicking an dock add icon
    * @author zhanggx1
    * @date 2011-12-15
    * @param v
    */
    public void addHotseatShortcutsForLeos(DrawableItem item) {
        if (item != null) {
            Object tag = item.getTag();
            if (tag != null && tag instanceof LeosItemInfo) {
                /*** AUT:zhaoxy . DATE:2012-02-22 . START***/
                resetAddInfo();
                /*** AUT:zhaoxy . DATE:2012-02-22 . END***/
                Intent intent = ((LeosItemInfo) tag).intent;
                startActivityForResult(intent, REQUEST_PICK_HOTSEAT_APPLICATIONS);
            }
        }
    }

    private void completeAddHotseatApp(Intent data, int screen, int cellX, int cellY) {
        final ShortcutInfo info = mModel.getShortcutInfo(getPackageManager(), data, this);

        if (info != null) {
            info.setActivity(data.getComponent(), Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            info.container = ItemInfo.NO_ID;
            info.screen = screen;
            info.cellX = cellX;
            info.cellY = cellY;

            mHotseat.bindInfo(info, mIconCache);

            XLauncherModel.addOrMoveItemInDatabase(this, info,
                    LauncherSettings.Favorites.CONTAINER_HOTSEAT, screen, cellX, cellY);
        } else {
            Log.e(TAG, "Couldn't find ActivityInfo for selected application: " + data);
        }
    }

    private XQuickActionWindow mPopupWindow;
    private DrawableItem mQuickView;
    private int mStatusBarHeight = -1;
    private Dialog mPopupChildDlg;

    // show pop up window
    public void showPopUpWindow(final DrawableItem view, final ItemInfo itemInfo) {
        dismissQuickActionWindow();
        if (itemInfo == null || view == null) {
            return;
        }
        /*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. S***/
        //test by dining 2013-06-19, only widgets to show PopupWindow Menu
        if (itemInfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET ||
        		itemInfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_LEOSWIDGET	) {
        //if (itemInfo.itemType != LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT &&
        //		itemInfo.itemType != LauncherSettings.Favorites.ITEM_TYPE_COMMEND_APPWIDGET	) {
        /*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. E***/
        	
        	 if(itemInfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET ){
        		 return;
        		 /*XViewContainer container = (XViewContainer) view;
        	     final LauncherAppWidgetHostView hostView = (LauncherAppWidgetHostView) container.getParasiteView();
                 AppWidgetProviderInfo pinfo = hostView.getAppWidgetInfo();
                 if (pinfo == null) {
                     return;
                 } else if (pinfo.resizeMode == AppWidgetProviderInfo.RESIZE_NONE) {
                	 Log.i("zdx1","pinfo.resizeMode===="+ pinfo.resizeMode);
                     return;
                }*/
        	}
        	 
            mMainSurface.post(new Runnable() {

                @Override
                public void run() {
                    mQuickView = view;
                    XQuickActionWindowInfo windowInfo = new XQuickActionWindowInfo(XLauncher.this,
                            itemInfo, view);
                    mPopupWindow = new XQuickActionWindow(true, windowInfo);
                    mPopupWindow.show();
            	    /* RK_ID: RK_ANIM_WEATHER . AUT: KANGWEI3 . DATE: 2013-03-27 . S */
                    mLauncherHandler.removeMessages(MSG_ID_SETINIT_ANIMA);
                    mLauncherHandler.sendMessageDelayed(mLauncherHandler.obtainMessage(MSG_ID_SETINIT_ANIMA), 500);
            	    /* RK_ID: RK_ANIM_WEATHER . AUT: KANGWEI3 . DATE: 2013-03-27 . E */
                }

            });
        }
    }

    public void dismissQuickActionWindow() {
		if (mMainSurface != null) {
			mMainSurface.post(new Runnable() {

				@Override
				public void run() {
					if (mPopupWindow != null) {
						mPopupWindow.dismiss();
						mPopupWindow = null;
						// mQuickView = null;
					}

				}

			});
		}
    }

    public void removePopupWindow(ItemInfo info) {
        if (isPopupWindowHostRemoved(info)) {
            Log.i(TAG, "because host is removed, so dismiss popup window ...");
            dismissQuickActionWindow();
            mQuickView = null;
        }
    }

    private boolean isPopupWindowHostRemoved(ItemInfo info) {
        if (mQuickView != null) {
            Object o = mQuickView.getTag();
            if (o instanceof ItemInfo) {
                final ItemInfo itemInfo = (ItemInfo) o;
                return (itemInfo.screen == info.screen && itemInfo.cellX == info.cellX
                        && itemInfo.cellY == info.cellY && itemInfo.spanX == info.spanX && itemInfo.spanY == info.spanY);
            }
        }
        return false;
    }

    public void setPopupChildDlg(Dialog d) {
        mPopupChildDlg = d;
    }

    private void dimissChildDialog() {
        if (mPopupChildDlg != null && mPopupChildDlg.isShowing()) {
            mPopupChildDlg.dismiss();
            mPopupChildDlg = null;
        }
    }

    void dismissQuickWindowIfMove() {
        Log.i(TAG, "dismissQuickWindowIfMove ~~~~");

        if (mQuickView != null) {
            Object o = mQuickView.getTag();
            if (o instanceof ItemInfo) {
                final ItemInfo itemInfo = (ItemInfo) o;
                DrawableItem target = null;

                if (itemInfo.container == LauncherSettings.Favorites.CONTAINER_DESKTOP) {
                    XPagedViewItem item = getWorkspace().getPagedView().findPageItemAt(
                            itemInfo.screen, itemInfo.cellX, itemInfo.cellY);
                    target = item != null ? item.getDrawingTarget() : null;
                } else if (itemInfo.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
                    target = getHotseat().getLayout().getChildAt(itemInfo.cellX, itemInfo.cellY);
                }

                if (target != mQuickView) {
                    dismissQuickActionWindow();
                }
            }
        }
    }

    void deliverFolder(FolderInfo info) {
        Log.i("Test", "deliverFolder~~~~");
        XLauncher launcher = this;
        XWorkspace workspace = launcher.getWorkspace();

        int[] cell = new int[3];

        // remove folder UI first.
        workspace.removePagedViewItem(info);

        int size = info.contents.size();
        IconCache cache = launcher.getIconCache();

        for (int i = 0; i < size; i++) {
            workspace.findVacantCellXY(cell);
            if (cell == null) {
                continue;
            }

            ShortcutInfo childInfo = info.contents.get(i);
            childInfo.container = LauncherSettings.Favorites.CONTAINER_DESKTOP;
            childInfo.cellX = cell[0];
            childInfo.cellY = cell[1];
            childInfo.screen = cell[2];

            Log.i("Test", "childInfo ~~~~" + childInfo);

            workspace.addInScreen(childInfo, cache, false);
            XLauncherModel.moveItemInDatabase(launcher, childInfo,
                    LauncherSettings.Favorites.CONTAINER_DESKTOP, cell[2], cell[0], cell[1]);
        }

        // remove folder db last.
        XLauncherModel.deleteItemFromDatabase(launcher, info);
    }

    void showAnimDownOrUp(final float wT, final float hT) {
//        if (mMainSurface != null) {
//            mMainSurface.post(new Runnable() {
//
//                @Override
//                public void run() {
                    if (mWorkspace != null) {
                        mWorkspace.setRelativeY(wT);
                        mWorkspace.getPagedView().moveWidgets();
                    }

                    if (mHotseat != null) {
                        mHotseat.setRelativeY(hT);
                    }
//                }
//
//            });
//        }
    }

    void moveFolderDownOrUp(final float y) {
        if (mWorkspace != null) {
            XFolder folder = mWorkspace.getOpenFolder();
            if (folder != null) {
                folder.setRelativeY(y);
            }
        }
    }

    float getWorkspaceOldTop() {
        int w = (int) mDragLayer.getWidth();
        int h = (int) mDragLayer.getHeight();

        RectF r = mMainSurface.getWorkspaceRect(w, h);
        boolean fullScreen = isCurrentWindowFullScreen();

        if (fullScreen && !SettingsValue.hasExtraTopMargin()) {
            r.offset(0, getStatusBarHeight());
//            r.bottom -= padding;
        }

        return r.top;
    }

    float getHotseatOldTop() {
        int h = (int) mDragLayer.getHeight();
        final int hotseat_height = this.getResources().getDimensionPixelSize(
                R.dimen.button_bar_height_plus_padding);

        return h - hotseat_height;
    }

    float getFolderOldTop() {
        final boolean flag = isCurrentWindowFullScreen();
        float y = 0;
        if (flag && !SettingsValue.hasExtraTopMargin()) {
            y = getStatusBarHeight();
        }
        return y;
    }

    void animHotseatView(float rotation, Object dragInfo,float dy) {
        if (mHotseat != null) {
            mHotseat.animViews(rotation, dragInfo,dy);
        }
    }
    
    void animHotseatReset() {
        if (mHotseat != null) {
            mHotseat.animReset();
        }
    }

    @Override
    public boolean onLongClick(DrawableItem item) {
    	
		if( mMainSurface.isGrabScrollState() ){
//			android.util.Log.i( "RR", "now main surface is grabscroll state true." );
			return false;
		}
    	
    	if (isScreenMngOpendOrExiting()) {
    		return false;
    	}
        
        mLauncherHandler.removeMessages(XLauncher.MSG_ID_SHOW_RECENT);
        mGestureManager.hideRencentDlg();
        
        
        if (mDragController.isDragging())
        {            
            mDragController.cancelDrag();
        }
        
        Object o = item.getTag();
        if (o instanceof ItemInfo) {
            /*** fixbug 9771. AUT: zhaoxy . DATE: 2013-03-28 . START ***/
            if (mWorkspace.getPagedView().isPageMoving()) {
                return true;
            }
            /*** fixbug 9771. AUT: zhaoxy . DATE: 2013-03-28 . END ***/
        	final ItemInfo info = (ItemInfo) o;
            if (info.container == LauncherSettings.Favorites.CONTAINER_DESKTOP) {
                if (info.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT ||
                    info.itemType == LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT ||
                    info.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION
                    || info.itemType == LauncherSettings.Favorites.ITEM_TYPE_FOLDER
                    || info.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET
                    || info.itemType == LauncherSettings.Favorites.ITEM_TYPE_LEOSWIDGET
                    /*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. S***/
                    || info.itemType == LauncherSettings.Favorites.ITEM_TYPE_COMMEND_APPWIDGET
                    /*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. E***/) {
                    if (info instanceof ShortcutInfo)
                    {
                        ShortcutInfo shortcutInfo = (ShortcutInfo)info;
                        if (((ShortcutInfo) info).mNewAdd == 1)
                        {
                            clearAndShowNewBg(shortcutInfo.intent.getComponent().flattenToString());
                        }
                    }
                    mWorkspace.startDrag(this, (ItemInfo) o);
                }
            }
            else if (info.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT){
                mHotseat.startDrag((ItemInfo) o);
            }
            //removed by yumina for do not popup any popupwindow 2013-07-04
            //showPopUpWindow(item, info);
        }
        if (item instanceof XWorkspace) {
        	showAddDialog();
        }
        return true;
    }
	private boolean mWindowState = false;
	public void setWindowState(boolean state)
	{
		mWindowState = state;
	}
	public boolean getWindowState()
	{
		return mWindowState;
	}
	private boolean mConfigureState = false;
	public void setConfigureState(boolean state)
	{
		mConfigureState = state;
	}
	public boolean getConfigureState()
	{
		return mConfigureState;
	}
    public void setLauncherWindowStatus(final boolean flag) {
        mMainSurface.post(new Runnable() {

            @Override
            public void run() {
                Window win = getWindow();
                WindowManager.LayoutParams winParams = win.getAttributes();
                final int bits = WindowManager.LayoutParams.FLAG_FULLSCREEN;
                if (flag) {
                    winParams.flags |= bits;
                } else {
                    winParams.flags &= ~bits;
                }
                mWindowState = true;
                win.setAttributes(winParams);
            }

        });
    }

    protected boolean isCurrentWindowFullScreen() {
        Window win = getWindow();
        int flag = win.getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN;
        return flag == WindowManager.LayoutParams.FLAG_FULLSCREEN;
    }

    protected int getReguForWindow() {
        if (isCurrentWindowFullScreen()) {
            return getStatusBarHeight();
        } else {
            return 0;
        }
    }

    public boolean isHotseatLayout(DrawableItem layout) {
        return mHotseat != null && layout != null && (layout == mHotseat.getLayout());
    }

    public XHotseat getHotseat() {
        return mHotseat;
    }

    IconCache getIconCache() {
        return mIconCache;
    }

    private void refreshIconStyleAndSize(final boolean draglayer, final boolean allapp, final boolean bitmapUpdate, final int currentIconStyle,final int currentIconSize) {
        if (currentIconStyle != Integer.MIN_VALUE || currentIconSize!=Integer.MIN_VALUE) {
            if(currentIconStyle != Integer.MIN_VALUE){
        		// indicate this action : change icon style index
                SettingsValue.setIconStyleIndex(currentIconStyle);
            }
            XLauncherModel.clearViewCache();
            mIconCache.flush();

//            getModel().getAllAppsList().updateAllPackagesIcon(XLauncher.this);
        }
        getModel().refreshIconStyleAndSize(this, draglayer,
      			allapp, bitmapUpdate, currentIconStyle, currentIconSize);
    }

    public View getMainView() {
        return mMainSurface;
    }

    public int getStatusBarHeight() {
        // return getResources().getDimensionPixelSize(R.dimen.status_bar_height);
        // return
        // getResources().getDimensionPixelSize(com.android.internal.R.dimen.status_bar_height);

        if (mStatusBarHeight <= 0) {
            Rect rect = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
            mStatusBarHeight = rect.top;
        }

        return mStatusBarHeight;
    }

    public XLauncherModel getModel() {
        return mModel;
    }
    
   /* public void downCommendApplication(String packageName,String title){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    	final String webDownWay = preferences.getString("pref_web_site", this.getString(R.string.prefDefault_webSite));
    	
    	
		String address = LauncherService.LENOVO_HOME_URL;
		 RK_DOWNLOAD_SEARCH_KEY zhanglz1@lenovo.com 2012-11-16 S 
		// if packagename is null,we use title to search app.
		if (webDownWay.equalsIgnoreCase("appStore")) {
			if (packageName != null && !packageName.equalsIgnoreCase("")) {
				address = LauncherService.LENOVO_APPSTORE_URL + packageName;
			} else if (title != null && !title.equalsIgnoreCase("")) {
				address = LauncherService.LENOVO_APPSTORE_APPNAME_URL
						+ title;
			}			
			
		} else if (webDownWay.equalsIgnoreCase("appchina")) {
			if (title != null && !title.equalsIgnoreCase("")) {
				address = LauncherService.APPCHINA_APPSTORE_URL + title;
			}
		}
		 RK_DOWNLOAD_SEARCH_KEY zhanglz1@lenovo.com 2012-11-16 E 
		Uri uri = Uri.parse(address);
		startActivity(new Intent(Intent.ACTION_VIEW, uri));
	}*/
    
    
    public XWorkspace getWorkspace() {
    	return mWorkspace;
    }
    
    public int getDesplyheightPixels(){
        DisplayMetrics dm = new DisplayMetrics(); 
        getWindowManager().getDefaultDisplay().getMetrics(dm); 
        Log.d("liuyg1","屏幕分辨率为:"+dm.widthPixels+" * "+dm.heightPixels);
        return dm.heightPixels;
        
    }
    
    public void pickShortcut() {
//        Bundle bundle = new Bundle();
//
//        ArrayList<String> shortcutNames = new ArrayList<String>();
//        //shortcutNames.add(getString(R.string.group_applications));
//        shortcutNames.add(getString(R.string.effect_shortcut));
//
//        bundle.putStringArrayList(Intent.EXTRA_SHORTCUT_NAME, shortcutNames);
//
//        ArrayList<ShortcutIconResource> shortcutIcons = new ArrayList<ShortcutIconResource>();
//        //shortcutIcons.add(ShortcutIconResource.fromContext(this,
//        //                R.drawable.ic_launcher_application));
//        shortcutIcons.add(ShortcutIconResource.fromContext(this,
//                    R.drawable.ic_launcher_animate_shortcut));
//        
//        
//        
//        bundle.putParcelableArrayList(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, shortcutIcons);

        /*PK_ID:CUSTOM ADD SHOUTCUT ITEM AUTH:GECN1 DATE:2012-12-14 S*/
        Intent pickIntent = new Intent();
        pickIntent.setAction("com.lenovo.launcher.action.PICK_ACTIVITY");
        /*PK_ID:CUSTOM ADD SHOUTCUT ITEM AUTH:GECN1 DATE:2012-12-14 S*/
        pickIntent.putExtra(Intent.EXTRA_INTENT, new Intent(Intent.ACTION_CREATE_SHORTCUT));
        pickIntent.putExtra(Intent.EXTRA_TITLE, getText(R.string.title_select_shortcut));
//        pickIntent.putExtras(bundle);
        pickIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivityForResult(pickIntent, REQUEST_PICK_SHORTCUT);
        
        /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-3-26 S*/
        Reaper.processReaper( XLauncher.this, 
        	   Reaper.REAPER_EVENT_CATEGORY_DESKTOPADD, 
			   Reaper.REAPER_EVENT_ACTION_DESKTOPADD_SHORTCUT,
			   Reaper.REAPER_NO_LABEL_VALUE, 
			   Reaper.REAPER_NO_INT_VALUE );
        /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-3-26 E*/ 
    }
    
    void processShortcut(Intent intent) {
        // Handle case where user selected "Applications"
        String applicationName = getResources().getString(R.string.group_applications);
        String shortcutName = intent.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);
        /* RK_ID: RK_SHORTCUT. AUT: liuli1 . DATE: 2012-11-01 . START */
        //String effectShortcutName = getResources().getString(R.string.effect_shortcut);
        /* RK_ID: RK_SHORTCUT. AUT: liuli1 . DATE: 2012-11-01 . END */

        if (applicationName != null && applicationName.equals(shortcutName)) {
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

            Intent pickIntent = new Intent(Intent.ACTION_PICK_ACTIVITY);
            pickIntent.putExtra(Intent.EXTRA_INTENT, mainIntent);
            pickIntent.putExtra(Intent.EXTRA_TITLE, getText(R.string.title_select_application));
            startActivityForResultSafely(pickIntent, REQUEST_PICK_APPLICATION);
            /* RK_ID: RK_SHORTCUT. AUT: liuli1 . DATE: 2012-11-01 . START */
        } /**else if (effectShortcutName != null && effectShortcutName.equals(shortcutName)) {
            Intent i = new Intent(this, EffectSettingShortCut.class);
            startActivityForResultSafely(i, REQUEST_CREATE_SHORTCUT);**/
            /* RK_ID: RK_SHORTCUT. AUT: liuli1 . DATE: 2012-11-01 . END */
        /**}**/
        else {
            startActivityForResultSafely(intent, REQUEST_CREATE_SHORTCUT);
        }
    }
    
    /**
     * Add a shortcut to the workspace.
     *
     * @param data The intent describing the shortcut.
     * @param cellInfo The position on screen where to create the shortcut.
     */
    private void completeAddShortcut(Intent data, long container, int screen, int cellX,
            int cellY) {
        ShortcutInfo info = mModel.infoFromShortcutIntent(this, data, null);
        completeAddShortcut(info, container, screen, cellX, cellY);
    }
    
    private void completeAddShortcut(ShortcutInfo info, long container, int screen, int cellX,
            int cellY){
        int[] cellXY = mTmpAddItemCellCoordinates;
        int[] touchXY = mPendingAddInfo.dropPos;
//        CellLayout layout = getCellLayout(container, screen);

        boolean foundCellSpan = false;
        if (info == null) {
            return;
        }
//        final View view = createShortcut(info);

        // First we check if we already know the exact location where we want to add this item.
        if (cellX >= 0 && cellY >= 0) {
            cellXY[0] = cellX;
            cellXY[1] = cellY;
            foundCellSpan = true;
//
//            // If appropriate, either create a folder or add to an existing folder
//            if (mWorkspace.createUserFolderIfNecessary(view, container, layout, cellXY,
//                    true, null,null)) {
//                return;
//            }
//            DragObject dragObject = new DragObject();
//            dragObject.dragInfo = info;
//            if (mWorkspace.addToExistingFolderIfNecessary(view, layout, cellXY, dragObject, true)) {
//                return;
//            }
        } else if (touchXY != null) {
            // when dragging and dropping, just find the closest free spot
//            int[] result = layout.findNearestVacantArea(touchXY[0], touchXY[1], 1, 1, cellXY);
//            foundCellSpan = (result != null);
        } else {
            /*** fixbug 170492  . AUT: zhaoxy . DATE: 2012-09-13. START***/
/*** fixbug 173073  . AUT: liuyg1 . DATE: 2012-12-18. START***/
//            if (workspace_occupied_needsave && mOccupiedTmp != null) {
//                foundCellSpan = layout.findCellForSpanThatIntersectsIgnoring(cellXY, 1, 1, -1, -1, null, mOccupiedTmp);
//            } else {
            foundCellSpan = mWorkspace.getPagedView().findCellForSpan(cellXY, 1, 1, screen, info);
//            }
/*** fixbug 173073  . AUT: liuyg1 . DATE: 2012-12-18. START***/
            workspace_occupied_needsave = false;
            mOccupiedTmp = null;
            /*** fixbug 170492  . AUT: zhaoxy . DATE: 2012-09-13. END***/
        }

        if (!foundCellSpan) {
            showOutOfSpaceMessage();
            return;
        }
        int newscreen = screen;
//        if (mWorkspace.mAddView)
//        {
//            checkAddPage();
//            newscreen = mWorkspace.getCurrentPage();
//        }       
        
        XLauncherModel.addItemToDatabase(this, info, container, newscreen, cellXY[0], cellXY[1], false);

        if (!mRestoring) {
//            mWorkspace.addInScreen(view, container, newscreen, cellXY[0], cellXY[1], 1, 1,
//                    isWorkspaceLocked());
            info.screen = newscreen;
            info.cellX = cellXY[0];
            info.cellY = cellXY[1];
            info.spanX = 1;
            info.spanY = 1;
            mWorkspace.addInScreen(info, mIconCache, false);
        }
    }
    
    public void completeAddSpecialShortcut(Intent data){
         mWaitingForResult = false;
         long container = mPendingAddInfo.container;
         int screen = mPendingAddInfo.screen;
         int cellX = mPendingAddInfo.cellX;
         int cellY = mPendingAddInfo.cellY;
         ShortcutInfo info = mModel.getShortcutInfo(getPackageManager(), data, this);
         info.intent=data;
         info.itemType = LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT;
         
         //add for change theme by zhanggx1 on 2013-10-16.s
         if (data != null && data.getComponent() != null) {
        	 ComponentName cn = data.getComponent();
        	 final PackageManager pm = getPackageManager();
        	 int iconId = 0;
        	 Context context = null;
        	 try {
	        	 ActivityInfo ai = pm.getActivityInfo(cn, 0);
	        	 iconId = ai.icon;
	        	 context = createPackageContext(cn.getPackageName(), 0);
        	 } catch (NameNotFoundException e) {
             }
        	 if (context != null && iconId != 0) {
	        	 info.iconResource = new Intent.ShortcutIconResource();
		         info.iconResource.packageName = cn.getPackageName();
		         info.iconResource.resourceName = context.getResources().getResourceName(iconId);
        	 }
         }
         //add for change theme by zhanggx1 on 2013-10-16.e
         
         completeAddShortcut(info, container, screen, cellX, cellY);
         resetAddInfo();
         if (mWorkspace!= null) {
             mWorkspace.invalidate();
         }
         refreshMngView(screen);
         
    }
    
  	 /*RK_ID: RK_SHOW_MENU Bug 9941 AUT:liuyg1@lenovo.com DATE: 2013-03-27 START*/  
//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_MENU) {
//            R2.echo("KeyEvent.KEYCODE_MENU");
//            if (!mScreenMngView.isVisible()) {
//                showMenu();
//            }
//        }
//        return super.onKeyUp(keyCode, event);
//    }
	 /*RK_ID: RK_SHOW_MENU Bug 9941 AUT:liuyg1@lenovo.com DATE: 2013-03-27 END*/
    
    public void completeAddApplication(Intent data, long container, int screen, int cellX, int cellY) {
        final int[] cellXY = mTmpAddItemCellCoordinates;
//        final CellLayout layout = getCellLayout(container, screen);

        // First we check if we already know the exact location where we want to add this item.
        if (cellX >= 0 && cellY >= 0) {
            cellXY[0] = cellX;
            cellXY[1] = cellY;
        } else if (!mWorkspace.getPagedView().findCellForSpan(cellXY, 1, 1, screen, null)) {
            showOutOfSpaceMessage();
            return;
        }

        final ShortcutInfo info = mModel.getShortcutInfo(getPackageManager(), data, this);
        
        int newscreen = screen;
//        if (mWorkspace.mAddView)
//        {
//            checkAddPage();
//            newscreen = mWorkspace.getCurrentPage();
//        } 

        if (info != null) {
            info.setActivity(data.getComponent(), Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            info.container = ItemInfo.NO_ID;
            
            info.screen = newscreen;
//            mWorkspace.addApplicationShortcut(info, layout, container, newscreen, cellXY[0], cellXY[1],
//                    isWorkspaceLocked(), cellX, cellY);
            info.screen = newscreen;
            info.cellX = cellXY[0];
            info.cellY = cellXY[1];
            info.spanX = 1;
            info.spanY = 1;
            //dooba add .s
            //添加时判断是否唯一
            if (mModel.checkShortcutInfoExist(info)) {
            	Toast.makeText(this, getString(R.string.shortcut_duplicate, info.title), Toast.LENGTH_SHORT).show();
            	return;
            }
            mModel.saveShortcutInfo(info);
            //dooba add .e
            mWorkspace.addInScreen(info, mIconCache, false);
            XLauncherModel.addOrMoveItemInDatabase(this, info, container, screen, cellXY[0],
                    cellXY[1]);
        } else {
            Log.e(TAG, "Couldn't find ActivityInfo for selected application: " + data);
        }
    }
    
    public void startWallpaper() {
//        showWorkspace(true);
        final Intent pickWallpaper = new Intent(Intent.ACTION_SET_WALLPAPER);
        Intent chooser = Intent.createChooser(pickWallpaper,
                getText(R.string.chooser_wallpaper));
        // NOTE: Adds a configure option to the chooser if the wallpaper supports it
        //       Removed in Eclair MR1
//        WallpaperManager wm = (WallpaperManager)
//                getSystemService(Context.WALLPAPER_SERVICE);
//        WallpaperInfo wi = wm.getWallpaperInfo();
//        if (wi != null && wi.getSettingsActivity() != null) {
//            LabeledIntent li = new LabeledIntent(getPackageName(),
//                    R.string.configure_wallpaper, 0);
//            li.setClassName(wi.getPackageName(), wi.getSettingsActivity());
//            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { li });
//        }
        startActivityForResult(chooser, REQUEST_PICK_WALLPAPER);
    }
     /**
     * Click the AllAppBtn
     * @param v
     */
    public void onClickAllAppsButton(DrawableItem v) {
    	/***RK_ID:RK_BUGFIX_173016 AUT:zhanglz1@lenovo.com.DATE:2012-12-07 S */
//		if (snapView != null&&snapView.getVisibility() ==View.VISIBLE)
//			return;
        /***RK_ID:RK_BUGFIX_173016 AUT:zhanglz1@lenovo.com.DATE:2012-12-07 E */ 
        showAllApps(true);
    }
    
    private void sortByComparator(List<ApplicationInfo> list, int sortMode) {
        if (list != null && AllAppSortHelper.isValid(sortMode)) {
            Collections.sort(list, mAllAppSortHelper.getComparator(sortMode));
        }
    }
    
    public AllAppSortHelper getSortHelper() {
        if (mAllAppSortHelper == null) {
            mAllAppSortHelper = new AllAppSortHelper(getApplication());
        }
        return mAllAppSortHelper;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(LoadBootPolicy.getInstance(this).getDefaultProfileProcessingState()){
			return true;
		}
		if (mAppListView != null && mAppListView.isEditMode()) {
			mAppListView.stopEditMode();
			return true;
		}
        final int uniChar = event.getUnicodeChar();
        final boolean handled = super.onKeyDown(keyCode, event);
        final boolean isKeyNotWhitespace = uniChar > 0 && !Character.isWhitespace(uniChar);
        if (!handled && acceptFilter() && isKeyNotWhitespace) {
//            boolean gotKey = TextKeyListener.getInstance().onKeyDown(mWorkspace, mDefaultKeySsb,
//                    keyCode, event);
//            if (gotKey && mDefaultKeySsb != null && mDefaultKeySsb.length() > 0) {
//                // something usable has been typed - start a search
//                // the typed text will be retrieved and cleared by
//                // showSearchDialog()
//                // If there are multiple keystrokes before the search dialog takes focus,
//                // onSearchRequested() will be called for every keystroke,
//                // but it is idempotent, so it's fine.
//                return onSearchRequested();
//            }
        }

        // Eat the long press event so the keyboard doesn't come up.
        if (keyCode == KeyEvent.KEYCODE_MENU && event.isLongPress()) {
            return true;
        }
        /*RK_ID: RK_WEATHER_WIDGET . AUT: KANGWEI3 . DATE: 2012-08-29 . S*/
        if(keyCode == KeyEvent.KEYCODE_BACK){
        	//stopWeatherAnimation();
        	removeToggleExWindow();
        	/** RK_ID: RK_MAGIC_GESTRUE . AUT: zhanglz1 . DATE: 2012-09-27 .**/
//            if(isFromWallpaperShow){
//        		fullWorkspace();
//    			isFromWallpaperShow = false;
//            }
        }
        /*RK_ID: RK_WEATHER_WIDGET . AUT: KANGWEI3 . DATE: 2012-08-29 . E*/
        return handled;
    }
    
    private boolean acceptFilter() {
        final InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        return !inputManager.isFullscreenMode();
    }
    
    private String getTypedText() {
        return mDefaultKeySsb.toString();
    }

    private void clearTypedText() {
    	if(mDefaultKeySsb!=null){
      mDefaultKeySsb.clear();

    	mDefaultKeySsb.clearSpans();

    	Selection.setSelection(mDefaultKeySsb, 0);
    	}
    }
    
    private void stopWeatherAnimation() {
       /* R2.echo("!!send com.lenovo.leos.widgets.weather.stop");
				Intent intent = new Intent("com.lenovo.leos.widgets.weather.stop");
		        XLauncher.this.sendBroadcast(intent);
		        getScreenAnimation(XLauncher.this,WeatherUtilites.ACTION_WEATHER_SCREEN_ANIMATE_STOP);*/
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
		if(LoadBootPolicy.getInstance(this).getDefaultProfileProcessingState()){
			Log.d("liuyg1","onNewIntent return");
			return;
		}
        //Cancel drag !
        if (mDragController != null && mDragController.isDragging()) {
            mDragController.cancelDrag();
        }

        
		if(LoadBootPolicy.getInstance(this).getDefaultProfileProcessingState()){
			return;
		}
		
		if( intent.getBooleanExtra(ExternalCommander.COMMAND_KEY_SHOW_ALLAPPLIST, false) ){
//			showAllApps(true);
			return;
		}
        // Close the menu
        if (Intent.ACTION_MAIN.equals(intent.getAction())) {
            // also will cancel mWaitingForResult.
            closeSystemDialogs();

            boolean alreadyOnHome = ((intent.getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
                        != Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);

            XFolder openFolder = mWorkspace.getOpenFolder();
            /*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-5-30. S***/
            mDragLayer.clearAllResizeFrames();
            /*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-5-30. E***/
            if (mScreenMngView != null && mScreenMngView.isVisible() && Float.compare(mScreenMngView.getAlpha(), 1.0f) == 0) {
            	closePreviewScreen();            	
            }
            boolean dragLayerNotMove = false;
            if (mMainSurface != null) {
                dragLayerNotMove = mMainSurface.resetToNormal(this, true);
            }
            
            if (alreadyOnHome && mState == State.WORKSPACE /*&& !mWorkspace.isTouchActive()*/ &&
                    openFolder == null && !mWorkspace.getPagedView().isPageMoving && !isFolderAnimating) {
                if (!dragLayerNotMove)
                    mWorkspace.moveToDefaultScreen(true);
            }

            if (!dragLayerNotMove) {
                //closeFolder();
                closeFolderNow();
            }
//            exitSpringLoadedDragMode();
            showWorkspace(alreadyOnHome);

            final View v = getWindow().peekDecorView();
            if (v != null && v.getWindowToken() != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            } 

            if (/*!alreadyOnHome && */mAppListView != null) {
                mAppListView.stopEditMode();
            }
        } else if (SettingsValue.ACTION_SHOW_THEME_DLG.equals(intent.getAction())) { 
        	String packageName = intent.getStringExtra(SettingsValue.EXTRA_THEME_VALUE); 
        	handleTheme(packageName, false, null);
		} else if ("com.lenovo.launcher.classification".equals(intent.getAction())) {
		    classifyAppsWithBehavior();
		}
    }
    
    AlertDialog mThemeVersionDlg = null;
    private void handleTheme(final String packageName, boolean notConfirm, String option) {
    	Context friendContext = null;
    	if (packageName == null) {
    		Toast.makeText(XLauncher.this, R.string.theme_pkg_not_exist, Toast.LENGTH_SHORT);
			return;
    	}
    	boolean isDefaultTheme = packageName.equals(SettingsValue.getDefaultAndroidTheme(XLauncher.this));
    	if (isDefaultTheme) {
    		applyTheme(packageName, friendContext, option);
    	} else {
    		
    		try {
				friendContext = createPackageContext(packageName,
				        Context.CONTEXT_IGNORE_SECURITY);
			} catch (NameNotFoundException e2) {
				e2.printStackTrace();
			}
    		if (friendContext == null) {
    			Toast.makeText(XLauncher.this, R.string.theme_pkg_not_exist, Toast.LENGTH_SHORT);
        		return;
    		}
    		if (notConfirm) {
    			ServiceInfo si = null;
    			try {
					si = getPackageManager().getServiceInfo(
							new ComponentName(packageName, "com.lenovo.launcher.theme.template.DownloadService"), 0);
				} catch (NameNotFoundException e) {
				}
    			if (si != null) {
    				return;
    			}
    		}
    		
    		// 1. do not confirm the version code
    		applyTheme(packageName, friendContext, option);
    		
    		// 2. confirm the version code
//    		final Context context = friendContext;
//    		
//    		String correctVersion = SettingsValue.isSuitableTheme(this, packageName);
//    		
//    		if (correctVersion == null) {
//    			applyTheme(packageName, context);
//    		} else {
//    			if (mThemeVersionDlg != null && mThemeVersionDlg.isShowing()) {
//    				mThemeVersionDlg.dismiss();
//    			}
//    			String msg = getString(R.string.theme_version_not_correct, correctVersion);
//    			mThemeVersionDlg = new AlertDialog.Builder(this)
//    	            .setMessage(msg)
//    	            .setNegativeButton(R.string.theme_apply_negative_btn, new DialogInterface.OnClickListener() {
//    	                public void onClick(DialogInterface dialog, int which) {
//    	                    dialog.dismiss();
//    	                }
//    	            }).setPositiveButton(R.string.theme_apply_positive_btn, new DialogInterface.OnClickListener() {
//    	                public void onClick(DialogInterface dialog, int which) {
//    	                	dialog.dismiss();
//    	                	applyTheme(packageName, context);
//    	                }
//    	            }).show();
//    		}
    	}
    }
    
    private void applyTheme(String packageName, Context friendContext, String option) {
    	setDetailState(DetailState.THEMEAPPLING);
    	dismissThemeDialog();
        if(mLoadWorkspaceDialog !=null  && !mLoadWorkspaceDialog.isShowing()){
        	showProgressDialog(getString(R.string.theme_appling_progress_info));
        }
//    	if (mWorkspace.isCustomState()) {
//        	mCustomTabHost.notifyDataChanged(MenuInterface.TAG_THEME, packageName);
//        }        	
    	new ApplyThemeTask().execute(new Object[]{packageName, friendContext, option});
    }
    
    void closeSystemDialogs() {
        getWindow().closeAllPanels();

        //open for hide dialog when home menu is pressed
        try {
            dismissDialog(DIALOG_CREATE_SHORTCUT);
            // Unlock the workspace if the dialog was showing
        } catch (Exception e) {
            // An exception is thrown if the dialog is not visible, which is fine
        }
        
//    	dismissThemeDialog();
//    	
//    	/*** AUT:zhaoxy . DATE:2012-02-22 . START***/
//    	if (snapView != null) {
//    		snapView.dismissDialog();
//		}
//    	/*** AUT:zhaoxy . DATE:2012-02-22 . END***/
//    	if(commendDialog != null){
//    		commendDialog.dismiss();
//    		commendDialog = null;
//    	}
//
//        dismissPopupChildDlg();

        if (mLeosDialog != null && mLeosDialog.isShowing()) {
            mLeosDialog.dismiss();
            mLeosDialog = null;
        }
/*** RK_ID: RK_PAD_MENU  AUT: liuyg1. DATE: 2012-05-31 . START***/
        if (mWorkspaceMenuDialog != null && mWorkspaceMenuDialog.isShowing()) {
        	mWorkspaceMenuDialog.dismiss();
        	mWorkspaceMenuDialog = null;
        }
/*
        if (mApplistMenuDialog != null && mApplistMenuDialog.isShowing()) {
        	mApplistMenuDialog.dismiss();
        	mApplistMenuDialog = null;
        }
*/
/*** RK_ID: RK_PAD_MENU  AUT: liuyg1. DATE: 2012-05-31 . END***/

/*** RK_ID: RK_PAD_MENU  AUT: mohl. DATE: 2012-7-16 . START***/
        if (mLeAlertDialog != null && mLeAlertDialog.isShowing()) {
           	mLeAlertDialog.dismiss();
           	mLeAlertDialog = null;
        }
/*** RK_ID: RK_PAD_MENU  AUT: mohl. DATE: 2012-7-16 . END***/
            
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            mPopupWindow = null;
        }
        dimissChildDialog();

        // Whatever we were doing is hereby canceled.
        mWaitingForResult = false;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        XLauncher.setScreen(mWorkspace.getCurrentPage());
        
		/** ID: Profile restoring. AUT: chengliang . DATE: 2012.03.22 . S */
		if(LoadBootPolicy.getInstance(this).getDefaultProfileProcessingState()){
			return false;
		}
		/** ID: Profile restoring. AUT: chengliang . DATE: 2012.03.22 . E */
       // stopWeatherAnimation();
        
        // new art
//        if (mNewArtSurface.isTransitioning()) {
//        	return false;
//        }
        boolean allAppsVisible = mAppListView != null ? mAppListView.isVisible() : false;
     // new art
        menu.setGroupVisible(MENU_GROUP_WALLPAPER, !allAppsVisible);
        return true;
    }
    
	void showAllApps(boolean animated) {
		if (LoadBootPolicy.getInstance(this).getDefaultProfileProcessingState()) {
			return;
		}
		if (mLeosDialog != null && mLeosDialog.isShowing()) {
			mLeosDialog.dismiss();
		}
		mLeosDialog = null;
/*** RK_ID: RK_PAD_MENU  AUT: liuyg1. DATE: 2012-05-31 . START***/
        if (mWorkspaceMenuDialog != null && mWorkspaceMenuDialog.isShowing()) {
        	mWorkspaceMenuDialog.dismiss();
        	mWorkspaceMenuDialog = null;
        }
/*
        if (mApplistMenuDialog != null && mApplistMenuDialog.isShowing()) {
        	mApplistMenuDialog.dismiss();
        	mApplistMenuDialog = null;
        }
*/
/*** RK_ID: RK_PAD_MENU  AUT: liuyg1. DATE: 2012-05-31 . END***/
		if (mState != State.WORKSPACE) {
			return;
		}
//		mWorkspace.setSlideEffectValue(null);
		
//		String key = SettingsValue.getAppListEnterValue(this);
//		if (key.charAt(0) == 'Z') {
//			showAppsCustomizeHelper_403(State.APPS_CUSTOMIZE, animated, false);
//		} else {
//			cameraZoomOut(State.APPS_CUSTOMIZE, animated, false);
//		}
		mHotseat.setVisibility(false);
		mWorkspace.setVisibility(false);
		mWorkspace.setAllWidgetVisible(false);
				
		mMainSurface.resizeAppListView();
        if (mAppListView != null) {
            mAppListView.setVisibility(true);
            mAppListView.checkLoading();
            mAppListView.playEnterAnim(true);
        }

	    /* RK_ID: RK_ANIM_WEATHER . AUT: KANGWEI3 . DATE: 2013-03-27 . S */
        mLauncherHandler.removeMessages(MSG_ID_SETINIT_ANIMA);
        mLauncherHandler.sendMessageDelayed(mLauncherHandler.obtainMessage(MSG_ID_SETINIT_ANIMA), 500);
	    /* RK_ID: RK_ANIM_WEATHER . AUT: KANGWEI3 . DATE: 2013-03-27 . E */

		// Hide the search bar and hotseat
//		mSearchDropTargetBar.hideSearchBar(animated);
//
//		// Change the state *after* we've called all the transition code
		mState = State.APPS_CUSTOMIZE;
//
//		// Pause the auto-advance of widgets until we are out of AllApps
//		mUserPresent = false;
//		updateRunning();
//		closeFolder();
//
//		// Send an accessibility event to announce the context change
//		getWindow().getDecorView().sendAccessibilityEvent(
//				AccessibilityEvent.TYPE_VIEW_SELECTED);
	}
	
	public void showWorkspace(boolean animated) {
		if (LoadBootPolicy.getInstance(this).getDefaultProfileProcessingState()) {
			return;
		}

		//Resources res = getResources();
		//int stagger = res
		//		.getInteger(R.integer.config_appsCustomizeWorkspaceAnimationStagger);
		/* AUT:zhanglq, zhanglq@bj.cobellink.com DATE:2012-1-9 start */
		// if(snapView != null && snapView.getVisibility() == View.VISIBLE){
		// closeWidgetSnap();
		// }
		/* AUT:zhanglq, zhanglq@bj.cobellink.com DATE:2012-1-9 end */

		/*** AUT:zhaoxy . DATE:2012-03-08 . START ***/
		 if (mDetailState == DetailState.THEMEAPPLING 
				 || mDetailState == DetailState.SCENEAPPLING 
				 || mDetailState == DetailState.SCENEBACKUP) {
		 } else
		 /*** AUT:zhaoxy . DATE:2012-03-08 . END***/
		 if (mDetailState == DetailState.ICONSTYLEAPPLING) {
		     showProgressDialog(getString(R.string.icon_style_apply));
		 } else if (mDetailState == DetailState.NORMAL) {
		     dismissProgressDialog(0);
		 }
		/* RK_ID: ICONSTYLE. AUT: liuli1 . DATE: 2012-03-05 . END */
		// if (mWorkspace.isCustomState()) {
		// this.hideCustomWorkspace();
		// } else {
		// mWorkspace.changeState(Workspace.State.NORMAL, animated, stagger);
		// }

		if (mState == State.APPS_CUSTOMIZE) {
			closeAllApps(animated);
		}

		// mWorkspace.flashScrollingIndicator(animated);
		// mCustomTabHost.clean();
		// mCustomTabHost.setVisibility(View.GONE);
		// updatePageVisibility(true);
		// // Change the state *after* we've called all the transition code
		mState = State.WORKSPACE;
		//
		// // Resume the auto-advance of widgets
		// mUserPresent = true;
		// updateRunning();

		// send an accessibility event to announce the context change
		getWindow().getDecorView().sendAccessibilityEvent(
				AccessibilityEvent.TYPE_VIEW_SELECTED);
	}

	void closeAllApps(boolean animated) {
		if (mState == State.APPS_CUSTOMIZE
				|| mState == State.APPS_CUSTOMIZE_SPRING_LOADED) {
			mHotseat.setVisibility(true);
			mWorkspace.setVisibility(true);
			mWorkspace.setAllWidgetVisible(true);
			
            if (mAppListView != null) {
                mAppListView.setVisibility(false);
            }
				
			mWorkspace.invalidate();
			mState = State.WORKSPACE;
//			mMainSurface.invalidate();
//			cameraZoomIn(State.APPS_CUSTOMIZE, animated, false);
			
			// Show the search bar and hotseat
//			mSearchDropTargetBar.showSearchBar(animated);
		}
	}
	
	public XLauncherView getXLauncherView() {
		return mMainSurface;
	}
	
    public boolean startActivitySafely(Intent intent, Object tag) {
    	
    	         //com.lenovo.launcher2.customizer.Utilities.freezingOrientation( this , true );
    	
		/** ID: Profile restoring. AUT: chengliang . DATE: 2012.03.22 . S */
		if(LoadBootPolicy.getInstance(this).getDefaultProfileProcessingState()){
			return false;
		}
		/** ID: Profile restoring. AUT: chengliang . DATE: 2012.03.22 . E */
		
                /*removed by yumina 20090916 for the JIRA bug SHINE-1963 
		if (intent.getComponent() == null)
		{
	        PackageManager mPackageManager = getPackageManager();;
	        List<ResolveInfo> newlist = mPackageManager.queryIntentActivities(intent, 0);
	        
	        int appFlags;
	        for (ResolveInfo info : newlist)
	        {
	            appFlags = info.activityInfo.applicationInfo.flags;
	            
	            if (((appFlags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0)
	                || ((appFlags & android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0))
	            {
	                Intent intent1 = new Intent(intent);
	                intent1.setComponent(new ComponentName(info.activityInfo.packageName, info.activityInfo.name));
	                intent = intent1;
	                break;
	            }
	        }
		}*/
    	
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        /* RK_ID: RK_SHORTCUT. AUT: liuli1 . DATE: 2012-10-19 . START */
        // fix bug 171724
        if (SettingsValue.ACTION_LETHEME_LAUNCH.equals(intent.getAction())) {
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        /* RK_ID: RK_SHORTCUT. AUT: liuli1 . DATE: 2012-10-19 . END */
        try {
            startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Unable to launch. tag=" + tag + " intent=" + intent, e);
        } catch (SecurityException e) {
            Toast.makeText(this, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Launcher does not have the permission to launch " + intent +
                    ". Make sure to create a MAIN intent-filter for the corresponding activity " +
                    "or use the exported attribute for this activity. "
                    + "tag="+ tag + " intent=" + intent, e);
        }
        return false;
    }
    
    
    /*RK ADD NEW METHOD dining 2013-07-19 S*/
    //test by dining use Intel Launcher code add the animation when click the bubbleTextView
    public boolean startActivitySafely(Intent intent, int[] measured, Object tag) {
    	
    	if(measured == null){
    		return false;
    	}
    	/** ID: Profile restoring. AUT: chengliang . DATE: 2012.03.22 . S */
		if(LoadBootPolicy.getInstance(this).getDefaultProfileProcessingState()){
			return false;
		}
		/** ID: Profile restoring. AUT: chengliang . DATE: 2012.03.22 . E */
		
		if (intent.getComponent() == null)
		{
	        PackageManager mPackageManager = getPackageManager();;
	        List<ResolveInfo> newlist = mPackageManager.queryIntentActivities(intent, 0);
	        
	        int appFlags;
	        for (ResolveInfo info : newlist)
	        {
	            appFlags = info.activityInfo.applicationInfo.flags;
	            
	            if (((appFlags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0)
	                || ((appFlags & android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0))
	            {
	                Intent intent1 = new Intent(intent);
	                intent1.setComponent(new ComponentName(info.activityInfo.packageName, info.activityInfo.name));
	                intent = intent1;
	                break;
	            }
	        }
		}
		
    	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (SettingsValue.ACTION_LETHEME_LAUNCH.equals(intent.getAction())) {
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }

    	try {
				Class<?> aops = Class
						.forName("android.app.ActivityOptions");
				
				Bundle localBundle = null;
				
//				int k = v.getMeasuredWidth();
//		        int m = v.getMeasuredHeight();
				int k = measured[0];
		        int m = measured[1];
                				
		        Rect rc = intent.getSourceBounds();
		        if(rc == null){
		        	return false;
		        }
		        
//		        int px = rc.centerX(); 
//		        int py = rc.centerY();
		        
		        int px = rc.left; 
		        int py = rc.top;
		        
		        String packageName = intent.getComponent().getPackageName();
		        Log.e(TAG,"---  startActivitySafely packageName="+packageName+" pointX="+px+" pointY="+py);
		        
		        if(packageName == null){
		        	return false;
		        }
		        //startActivity(paramIntent, localBundle);
		        
//				Method makeScaleUpAnimation = aops.getMethod("makeScaleUpAnimation",
//						                          new Class<?>[]{View.class, int.class,int.class,int.class,int.class});
				
				Method makeScaleUpAnimation = aops.getMethod("makeScaleUpAnimationEx",
                        new Class<?>[]{String.class, int.class,int.class,int.class,int.class,int.class,int.class});

				Object obj = makeScaleUpAnimation.invoke(null, new Object[]{packageName, px, py, 0,0,k,m});
								
				Method toBundle = obj.getClass().getMethod("toBundle");
				localBundle = (Bundle)toBundle.invoke(obj);
				//get the startActivity
				Method startactivity = this.getClass().getMethod("startActivity", new Class<?>[]{Intent.class, Bundle.class});
				
				startactivity.invoke(this, new Object[]{intent, localBundle});
				
				return true;
           
    	 } catch (Exception ex) {
    		 Log.e(TAG,"---  startActivitySafely exception="+ex);
				
         }
    	return false;
		
    }
    /*RK ADD NEW METHOD dining 2013-07-19 E*/
    
    public AllApplicationsThread getAllAppThread() {
        return mApplicationThread;
    }
    
    AppToPositionReceiver mAppToPositionReceiver;
    private ComponentName mAppToPosition;
    private String mAppToPositionString;
    public class AppToPositionReceiver extends BroadcastReceiver {
        
        @Override
        public void onReceive(Context context, Intent intent) {
            if(LoadBootPolicy.getInstance(XLauncher.this).getDefaultProfileProcessingState()){
                return;
            }
            
            final String action = intent.getAction();
            if (SettingsValue.ACTION_APP_TO_POSITION.equals(action)) {
                Log.d(TAG, "receive ACTION_APP_TO_POSITION");
//                hideCustomWorkspace();
                Intent intent1 = (Intent)intent.getExtra("intent");
                mAppToPosition = intent1.getComponent();
                mAppToPositionString = String.valueOf(intent.getCharExtra(SettingsValue.EXTRA_SORT_KEY, '#'));
                Log.d(TAG, "componentName = " + intent1.getComponent());
                if (mState == State.WORKSPACE) {
                    showAllApps(true);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setAppToPosition();            
                        }           
                    }, 300);
                } else {
                    setAppToPosition();
                }              
                
            } 
        }
    }
    
    private void setAppToPosition() {
        if (mAppListView != null) {
            mAppListView.setAppToPosition(mAppToPosition);
        }
    }
   
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
/* RK_ID:RK_BOOT_POLICY_DIALOG. AUT:liuyg1 . DATE:2013-3-21. START*/ 
			 if (msg.what == BootPolicyUtility.RESTORE_DEFAULT_PROFILE){
				Log.d("liuyg1","mHandler msg.what == PolicyConstant.BACKUP_PROFILE_START");
				LoadBootPolicy.getInstance(XLauncher.this).showLoadProgressDialog();
				if(!LoadBootPolicy.getInstance(XLauncher.this).loadFactoryProfile(false)){
					BootPolicyUtility.recordVersion(XLauncher.this);
					restartLauncher();
				}
			}else if(msg.what ==  BootPolicyUtility.UPDATE_PROFILE_START){
				Log.d("liuyg1","mHandler msg.what ==  PolicyConstant.BACKUP_PROFILE_START");
				LoadBootPolicy.getInstance(XLauncher.this).showLoadProgressDialog();
				if(!LoadBootPolicy.getInstance(XLauncher.this).loadFactoryProfile(true)){
					BootPolicyUtility.recordVersion(XLauncher.this);
					final SharedPreferences pref = XLauncher.this.getSharedPreferences(
							ConstantAdapter.PREF_FIRST_LAUNCH_CHECK_NAME,
							Activity.MODE_APPEND | Activity.MODE_PRIVATE);
					pref.edit().putBoolean(ConstantAdapter.PREF_FIRST_LAUNCH_CHECK_NAME, false).commit();
					Log.d("liuyg1","mHandler msg.what ==  PolicyConstant.BACKUP_PROFILE_START !LoadBootPolicy.getInstance(XLauncher.this).loadFactoryProfile()");
					restartLauncher();
				}
//				LoadBootPolicy.getInstance(XLauncher.this).backupAndRestoreDesktop(mHandler);
//				mProgressDlg =LoadBootPolicy.getInstance(XLauncher.this).getBackupProgressDialog();
			/*}else if(msg.what == ApplistMenuDialog.DLGMENU_ORDER_SHOW){
				showAppOrderChoices();
*/
			}else if(msg.what == WorkspaceMenuDialog.DLGMENU_ADD_SHOW){
				showAddDialog();
			}else if(msg.what == WorkspaceMenuDialog.DLGMENU_SCREEN_SETTINGS_SHOW){
				if (mScreenMngView != null && !mScreenMngView.isVisible()) {
					if(isWorkspaceLocked()) {
			        	if (!sPendingMsg.contains(PENDING_EDIT_SCREEN)
			        			&& !sPendingMsg.contains(PENDING_PREVIEW_SCREEN)) {
			        		sPendingMsg.add(PENDING_PREVIEW_SCREEN);
			        	}
			        	return;
			        }
        			showWidgetSnap(false);
        			mGestureInProgress = false;
        		}
			}
			
//			else if(msg.what == BootPolicyConstant.BACKUP_PROFILE_SUCCESS){
//				LinearLayout mll = (LinearLayout) mProgressDlg.getLayoutInflater().inflate(R.layout.boot_backup_desktop_progressbar, null);
//				TextView textmsg1 =  (TextView)mProgressDlg.findViewById(R.id.progress_msg_title);
//				
//				textmsg1.setText(R.string.boot_backup_desktop_success);
//				TextView textmsg2 = (TextView)mProgressDlg.findViewById(R.id.backup_location);
//				textmsg2.setVisibility(View.GONE);
//				mll.invalidate();
//				
//				textmsg1.setText(R.string.boot_loading_desktop);
//				mll.invalidate();
//				
//			}else if(msg.what == BootPolicyConstant.BACKUP_PROFILE_FAIL){
//				LinearLayout mll = (LinearLayout) mProgressDlg.getLayoutInflater().inflate(R.layout.boot_backup_desktop_progressbar, null);
//				
//				TextView textmsg1 =  (TextView)mProgressDlg.findViewById(R.id.progress_msg_title);
//				textmsg1.setText(R.string.boot_backup_desktop_success);
//				TextView textmsg2 = (TextView)mProgressDlg.findViewById(R.id.backup_location);
//				textmsg2.setVisibility(View.GONE);
//				mll.invalidate();
//				
//				textmsg1.setText(R.string.boot_loading_desktop);
//				mll.invalidate();
//			}
/* RK_ID:RK_BOOT_POLICY_DIALOG. AUT:liuyg1 . DATE:2013-3-21. END*/ 
    		/** ID: Profile restoring. AUT: chengliang . DATE: 2012.03.22 . S */
    		if(LoadBootPolicy.getInstance(XLauncher.this).getDefaultProfileProcessingState()){
    			return;
    		}
    		/** ID: Profile restoring. AUT: chengliang . DATE: 2012.03.22 . E */
            if (msg.what == ADVANCE_MSG) {
            	
            } else if (msg.what == MSG_SEND_THEME_APPLING) {
				String pkgName = msg.obj.toString();
				themePackageName = pkgName;
				Settings.System.putString(getContentResolver(), SettingsValue.KEY_SET_THEME, pkgName);
				SettingsValue.setThemeValue(XLauncher.this, null);
				
//				android.util.Log.i("dooba", "+++++++++++++send Theme Changed ACTION: " + pkgName);
				Intent themechanged = new Intent(SettingsValue.ACTION_LETHEME_APPLING);
	    		themechanged.putExtra(SettingsValue.EXTRA_THEME_VALUE, pkgName);
	    		if(pkgName.equals(SettingsValue.getDefaultThemeValue(XLauncher.this))) {
					themechanged.putExtra(SettingsValue.EXTRA_DEFAULT_THEME, true);
					
				} else {
				    themechanged.putExtra(SettingsValue.EXTRA_DEFAULT_THEME, false);				    
				}
				sendBroadcast(themechanged);
				// added by yumina for the themecenter 20130820
				Intent themecenter = new Intent("action.launchertheme.appling");
				themecenter.putExtra("theme_change_result", true);
				themecenter.putExtra(SettingsValue.EXTRA_THEME_VALUE, themePackageName);
				Log.i("mhl", "===== send themecenter themePackageName: " + themePackageName);
				if (SettingsValue.THEME_PACKAGE_NAME_PREF.equals(themePackageName)) {
					themecenter.putExtra(SettingsValue.EXTRA_DEFAULT_THEME, true);
				} else {
					themecenter.putExtra(SettingsValue.EXTRA_DEFAULT_THEME, false);
				}
				sendBroadcast(themecenter);

	    		Intent oldIntent = new Intent(SettingsValue.ACTION_LETHEME_APPLY);
	    		oldIntent.putExtra(SettingsValue.EXTRA_THEME_VALUE, pkgName);
	    		if(pkgName.equals(SettingsValue.getDefaultThemeValue(XLauncher.this))) {
	    			oldIntent.putExtra(SettingsValue.EXTRA_DEFAULT_THEME, true);	    			
				} else {
					oldIntent.putExtra(SettingsValue.EXTRA_DEFAULT_THEME, false);					
				}
				sendBroadcast(oldIntent);
				/* RK_ID: RK_LENOVO_THEMECENTER. AUT: yumina . DATE: 2013-07-22. START */
				Log.e("yumin0806", "11111111111111111111111111111111111111 packageName="+ pkgName);
				if (pkgName.contains("DEFAULT THEME")) {
					Settings.System.putString(getContentResolver(),"lenovo_desktop_theme",
							SettingsValue.LAUNCHER_PACKAGE_NAME_PREF);
				} else {
					Settings.System.putString(getContentResolver(),"lenovo_desktop_theme", pkgName);
				}
				/* RK_ID: RK_LENOVO_THEMECENTER. AUT: yumina . DATE: 2013-07-22. END */
			}
            /*RK_ID: RK_TRASH . AUT: zhanggx1 . DATE: 2012-04-09 . E*/
            else if (msg.what == 333) {
            	if (mLeosDialog != null && mLeosDialog.isShowing()) {
            		mLeosDialog.dismiss();
            	}
/** ID: Profile restoring tools. AUT: liuyg1. DATE: 2012.04.18 . S */
            }else if(msg.what == DISMISS_APPLY_PROGRESS){
            	dismissProgressDialog(0);
            	 final byte apply = (Byte) msg.obj;
            	 int resInt = 0;
                 resInt = R.string.profile_apply_profile_fail;
                 switch (apply) {
                 case OperationState.SUCCESS:
                     resInt = R.string.profile_apply_profile_ok;
                     break;
                 case OperationState.FAILED_NO_SDCARD:
                     resInt = R.string.profile_apply_sdcard_unmount;
                     break;
                 case OperationState.PAD_APPALY_PHONE_SCENE:
                     resInt = R.string.failed_to_unmix;
                     break;
                 case OperationState.PHONE_APPALY_PAD_SCENE:
                     resInt = R.string.failed_to_unmix;
                     break;    
                 default:
                	 resInt = R.string.profile_apply_profile_fail;
                     break;
                 }
                 final LeDialog resultDialog = new LeDialog(XLauncher.this, R.style.Theme_LeLauncher_Dialog_Shortcut);
                   String text = getString(resInt);
                   LinearLayout alert = (LinearLayout) XLauncher.this.getLayoutInflater().inflate(R.layout.profile_backup_result, null);
                   TextView tv = (TextView) alert.findViewById(R.id.profile_backup_hint);
                   tv.setText(text);
                   resultDialog.setLeContentView(alert);
                   resultDialog.setLePositiveButton(getString(R.string.profile_save_button),  new DialogInterface.OnClickListener() {

                	   @Override
                	   public void onClick(DialogInterface arg0, int arg1) {
                		   resultDialog.dismiss();
                		   if(apply==OperationState.SUCCESS){
                			   BackupManager.getInstance(XLauncher.this).reLaunch();
                		   }

                	   }
                   });

                   resultDialog.show();
   /** ID: Profile restoring tools. AUT: liuyg1. DATE: 2012.04.18 . E */                
            }
        }
    };
    
    private class CloseSystemDialogsIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
        	if(Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())){
            	closeSystemDialogs();
        	}else if(intent.getAction().equals(SettingsValue.ACTION_REMOVE_LOADING_DIALOG)){
                Log.d("gecn1", "CloseSystemDialogsIntentReceiver  Load Worksapce dialog remove1111111");
                Log.d("gecn1", "CloseSystemDialogsIntentReceiver  Load Worksapce " + XLauncher.this+ "     pid = " +  android.os.Process.myPid());
                
        		if(mLoadWorkspaceDialog != null && mLoadWorkspaceDialog.isShowing()){
        			Log.d("gecn1", "CloseSystemDialogsIntentReceiver  Load Worksapce dialog remove  " + mLoadWorkspaceDialog);
        			mLoadWorkspaceDialog.dismiss();
        			mLoadWorkspaceDialog = null;
        		}else if(mLoadWorkspaceDialog != null && !mLoadWorkspaceDialog.isShowing()){
        			Log.d("gecn1", "CloseSystemDialogsIntentReceiver  Load Worksapce dialog  not showing " + mLoadWorkspaceDialog);
        			mLoadWorkspaceDialog = null;
        		}else{
        			Log.d("gecn1", "CloseSystemDialogsIntentReceiver  Load Worksapce dialog  is null" );
        		}

        	}
        }
    }
    
    /*** RK_ID: XFOLDER.  AUT: zhaoxy . DATE: 2013-01-22 . START***/
    @Override
    public void onClick(DrawableItem item) {
        Object tag = item.getTag();
        if (tag instanceof FolderInfo) {
            if (item instanceof XFolderIcon) {
                XFolderIcon fi = (XFolderIcon) item;
                handleFolderClick(fi);
            }
        }
    }
    /*** RK_ID: XFOLDER.  AUT: zhaoxy . DATE: 2013-01-22 . END***/
    
    @Override
    public void onBackPressed() {
		if(LoadBootPolicy.getInstance(this).getDefaultProfileProcessingState()){
			return;
		}
		//test by liuli new deletebar
        boolean dragLayerNotMove = false;
        if (mMainSurface != null) {
            dragLayerNotMove = mMainSurface.resetToNormal(this, true);
        }
		/** ID: Profile restoring. AUT: chengliang . DATE: 2012.03.22 . E */
        if (mState == State.APPS_CUSTOMIZE) {
                showWorkspace(true);
                return;
            /*** RK_ID: CATEGORY_MENU.  AUT: zhaoxy . DATE: 2012-06-29 . END***/
        } else if (!dragLayerNotMove && mWorkspace.getOpenFolder() != null) {
              closeFolder();
//            Folder openFolder = mWorkspace.getOpenFolder();
//            if (openFolder.isEditingName()) {
//                openFolder.dismissEditingName();
//            } else {
//                closeFolder();
//            }
              return;
        } else {
        	/*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-5-30. S***/
        	mDragLayer.clearAllResizeFrames();
        	/*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-5-30. E***/
            /*RK_ID: RK_CUSTOM_WORKSPACE . AUT: zhanggx1 . DATE: 2012-11-27 . S*/
//            hideCustomWorkspace();
            /*RK_ID: RK_CUSTOM_WORKSPACE . AUT: zhanggx1 . DATE: 2012-11-27 . E*/
        }
        
        if (mScreenMngView != null && mScreenMngView.isVisible() && Float.compare(mScreenMngView.getAlpha(), 1.0f) == 0) {
        	mWorkspace.setCurrentPage(mWorkspace.getCurrentPage());
        	mDragController.cancelDrag();
        	closePreviewScreen();
        }
        
        // dooba edit s
        this.hideIconPkgDialog();
     // dooba edit e
    }
    
    void showAppOrderChoices() { 
        boolean currentSortByDes = false;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int sortmode = preferences.getInt(SettingsValue.KEY_APPLIST_LAST_SORTMODE, 0);
        //test by dining
        int getTempindex = sortmode >> 4;
        
        int sortBy = sortmode & 0xf;
        int resIdTemp = -1;
        if (sortBy == SettingsValue.SORT_BY_DES) {
            resIdTemp = R.drawable.sort_by_des;
            currentSortByDes = true;
        } else if (sortBy == SettingsValue.SORT_BY_ASC) {
            resIdTemp = R.drawable.sort_by_asc;
            currentSortByDes = false;
        }
        final boolean isTopLastest = currentSortByDes;
        final int resIdDetail = resIdTemp;
        final boolean isUsageStatsAvailable = UsageStatsMonitor.isAvailable();
        //test by dining
        if(getTempindex == 2){
        	if(currentSortByDes){
        		getTempindex++;
        	}
        }else if(getTempindex >= 3){
        	getTempindex++;
        }
        final int index = getTempindex;
        //end test
        /*       ListAdapter adapter = new ArrayAdapter<CharSequence>(this, R.layout.select_dialog_item, R.id.select_item_text, getResources().getTextArray(R.array.app_order_choices)) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (index == position) {
                    ((ImageView) v.findViewById(R.id.select_item_mark)).setVisibility(View.VISIBLE);
                    if (resIdDetail > 0) {
                        ImageView detail = (ImageView) v.findViewById(R.id.select_item_detail);
                        detail.setImageResource(resIdDetail);
                        detail.setVisibility(View.VISIBLE);
                    }
                } else {
                    ((ImageView) v.findViewById(R.id.select_item_mark)).setVisibility(View.INVISIBLE);
                    ((ImageView) v.findViewById(R.id.select_item_detail)).setVisibility(View.INVISIBLE);
                }
                return v;
            }
            @Override
            public int getCount() {
                if (!isUsageStatsAvailable) {
                    return super.getCount() - 2;
                }
                return super.getCount();
            }
        };
    	mLeosDialog = new AlertDialog.Builder(this, R.style.Theme_LeLauncher_Dialog_Alert)
        .setIconAttribute(android.R.attr.alertDialogIcon)
        .setTitle(R.string.app_order_settings)
        .setIcon(R.drawable.main_menu_sort)
        .setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, final int whichButton) {
                mMainSurface.post(new Runnable() {
                    public void run() {
                        if (mMainSurface != null) {
                            final ArrayList<ApplicationInfo> apps = mMainSurface.getApps();
                            SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(XLauncher.this).edit();

                            switch (whichButton) {
                            case 0: // default
                                sortByComparator(apps, AllAppSortHelper.REGULAR_COMPARATOR);
                                mMainSurface.setApps(apps);
                                edit.putInt(SettingsValue.KEY_APPLIST_LAST_SORTMODE, 0);
                                edit.putInt(SettingsValue.KEY_APPLIST_CURRENT_SORTMODE, 0);
                                edit.apply();
                                break;

                            case 1: // alphabet
                                sortByComparator(apps, AllAppSortHelper.NAME_COMPARATOR);
                                *//*** AUT:zhaoxy . DATE:2012-02-24 . END***//*
                                mMainSurface.setApps(apps);
                                edit.putInt(SettingsValue.KEY_APPLIST_LAST_SORTMODE, 1 << 4);
                                edit.putInt(SettingsValue.KEY_APPLIST_CURRENT_SORTMODE, 1 << 4);
                                edit.apply();
                                break;

                            case 2: // Installation time
                                if (isTopLastest) {
                                    sortByComparator(apps, AllAppSortHelper.FIRST_INSTALL_COMPARATOR_ASC);
                                } else {
                                    sortByComparator(apps, AllAppSortHelper.FIRST_INSTALL_COMPARATOR_DES);
                                }
                                mMainSurface.setApps(apps);
                                int temp = 2 << 4 | (isTopLastest ? SettingsValue.SORT_BY_ASC : SettingsValue.SORT_BY_DES);
                                edit.putInt(SettingsValue.KEY_APPLIST_LAST_SORTMODE, temp);
                                edit.putInt(SettingsValue.KEY_APPLIST_CURRENT_SORTMODE, temp);
                                edit.apply();
                                break;

                            case 3: // Launcher Count
                                if (mModel.getUsageStatsMonitor() != null) {
                                    *//*** RK_ID: USAGES_STATS.  AUT: zhaoxy . DATE: 2012-06-27 . START***//*
                                    mModel.getUsageStatsMonitor().updateCatch();
                                    *//*** RK_ID: USAGES_STATS.  AUT: zhaoxy . DATE: 2012-06-27 . END***//*
                                }
                                sortByComparator(apps, AllAppSortHelper.LAUNCH_COUNT_COMPARATOR);
                                mMainSurface.setApps(apps);
                                edit.putInt(SettingsValue.KEY_APPLIST_LAST_SORTMODE, 3 << 4);
                                edit.putInt(SettingsValue.KEY_APPLIST_CURRENT_SORTMODE, -1 << 4);
                                edit.apply();
                                break;

                            case 4: // Last Resume Time
                                if (mModel.getUsageStatsMonitor() != null) {
                                    *//*** RK_ID: USAGES_STATS.  AUT: zhaoxy . DATE: 2012-06-27 . START***//*
                                    mModel.getUsageStatsMonitor().updateCatch();
                                    *//*** RK_ID: USAGES_STATS.  AUT: zhaoxy . DATE: 2012-06-27 . END***//*
                                }
                                //sortByLastResumeTime(apps);
                                sortByComparator(apps, AllAppSortHelper.LAST_RESUME_TIME_COMPARATOR);
                                mMainSurface.setApps(apps);
                                edit.putInt(SettingsValue.KEY_APPLIST_LAST_SORTMODE, 4 << 4);
                                edit.putInt(SettingsValue.KEY_APPLIST_CURRENT_SORTMODE, -1 << 4);
                                edit.apply();
                                break;
                            }
                            mApplicationThread.setAction(AllApplicationsThread.ACTION_SYNC_TODB, apps);
                        }
                    } // end run
                });

                dialog.dismiss();
                mLeosDialog = null;
            }
                })
        .setNegativeButton(R.string.add_profile_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        }).create();
        mLeosDialog.show();*/
        ListAdapter adapter = new ArrayAdapter<CharSequence>(this, R.layout.select_dialog_item, R.id.select_item_text, getResources().getTextArray(R.array.app_order_choices)) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (index == position) {
                    ((ImageView) v.findViewById(R.id.select_item_mark)).setSelected(true);
                    /*if (resIdDetail > 0) {
                        ImageView detail = (ImageView) v.findViewById(R.id.select_item_detail);
                        detail.setImageResource(resIdDetail);
                        detail.setVisibility(View.VISIBLE);
                    }*/
                } else {
                    ((ImageView) v.findViewById(R.id.select_item_mark)).setSelected(false);
                    ((ImageView) v.findViewById(R.id.select_item_detail)).setVisibility(View.INVISIBLE);
                }
                return v;
            }
            @Override
            public int getCount() {
            	if (!isUsageStatsAvailable) {
                    return super.getCount() - 2;
                }
                return super.getCount();
            }
        };
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        final Dialog dialog = new Dialog(this,R.style.Theme_LeLauncher_Dialog_Shortcut);
        View contentView// = onCreateDialogView();
        = (LinearLayout)inflater.inflate(R.layout.le_dialog_preference_layout, null);
        
		TextView title1 = (TextView) contentView
				.findViewById(R.id.dialog_title);
		title1.setText(R.string.app_order_settings);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		Window window = dialog.getWindow();
		window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, 200);
		window.setGravity(Gravity.CENTER);
		dialog.setCanceledOnTouchOutside(true);
		ListView preferenceList = (ListView)contentView.findViewById(R.id.preference_list);
		preferenceList.setAdapter(adapter);
		preferenceList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				// TODO Auto-generated method stub
				 mMainSurface.post(new Runnable() {
	                    public void run() {
	                        if (mMainSurface != null) {
	                            final ArrayList<ApplicationInfo> apps = mMainSurface.getApps();
	                            SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(XLauncher.this).edit();
	                            switch (position) {
	                            case 0: // default
	                                sortByComparator(apps, AllAppSortHelper.REGULAR_COMPARATOR);
	                                mMainSurface.setApps(apps);
	                                edit.putInt(SettingsValue.KEY_APPLIST_LAST_SORTMODE, 0);
	                                edit.putInt(SettingsValue.KEY_APPLIST_CURRENT_SORTMODE, 0);
	                                edit.apply();
	                                break;

	                            case 1: // alphabet
	                                sortByComparator(apps, AllAppSortHelper.NAME_COMPARATOR);
	                                mMainSurface.setApps(apps);
	                                edit.putInt(SettingsValue.KEY_APPLIST_LAST_SORTMODE, 1 << 4);
	                                edit.putInt(SettingsValue.KEY_APPLIST_CURRENT_SORTMODE, 1 << 4);
	                                edit.apply();
	                                break;

	                            case 2: // Installation time isTopLastest == true
//	                                if (isTopLastest) {
//	                                    sortByComparator(apps, AllAppSortHelper.FIRST_INSTALL_COMPARATOR_ASC);
//	                                } else {
//	                                    sortByComparator(apps, AllAppSortHelper.FIRST_INSTALL_COMPARATOR_DES);
//	                                }
	                            	sortByComparator(apps, AllAppSortHelper.FIRST_INSTALL_COMPARATOR_ASC);
	                                mMainSurface.setApps(apps);
	                                //int temp = 2 << 4 | (isTopLastest ? SettingsValue.SORT_BY_ASC : SettingsValue.SORT_BY_DES);
	                                int temp = 2 << 4 | (SettingsValue.SORT_BY_ASC);
	                                edit.putInt(SettingsValue.KEY_APPLIST_LAST_SORTMODE, temp);
	                                edit.putInt(SettingsValue.KEY_APPLIST_CURRENT_SORTMODE, temp);
	                                edit.apply();
	                                break;
	                                
	                            case 3: //Installation time isTopLastest == false
	                            	sortByComparator(apps, AllAppSortHelper.FIRST_INSTALL_COMPARATOR_DES);
	                                
	                                mMainSurface.setApps(apps);
	                                int des = 2 << 4 | (SettingsValue.SORT_BY_DES);
	                                edit.putInt(SettingsValue.KEY_APPLIST_LAST_SORTMODE, des);
	                                edit.putInt(SettingsValue.KEY_APPLIST_CURRENT_SORTMODE, des);
	                                edit.apply();
	                                break;

	                            case 4://case 3: // Launcher Count
//	                                if (mModel.getUsageStatsMonitor() != null) {
//	                                    mModel.getUsageStatsMonitor().updateCatch();
//	                                }
	                                sortByComparator(apps, AllAppSortHelper.LAUNCH_COUNT_COMPARATOR);
	                                mMainSurface.setApps(apps);
	                                edit.putInt(SettingsValue.KEY_APPLIST_LAST_SORTMODE, 3 << 4);
	                                edit.putInt(SettingsValue.KEY_APPLIST_CURRENT_SORTMODE, -1 << 4);
	                                edit.apply();
	                                break;

	                            case 5://case 4: // Last Resume Time
//	                                if (mModel.getUsageStatsMonitor() != null) {
//	                                    mModel.getUsageStatsMonitor().updateCatch();
//	                                }
	                                //sortByLastResumeTime(apps);
	                                sortByComparator(apps, AllAppSortHelper.LAST_RESUME_TIME_COMPARATOR);
	                                mMainSurface.setApps(apps);
	                                edit.putInt(SettingsValue.KEY_APPLIST_LAST_SORTMODE, 4 << 4);
	                                edit.putInt(SettingsValue.KEY_APPLIST_CURRENT_SORTMODE, -1 << 4);
	                                edit.apply();
	                                break;
	                            default:
	                              	break;
	                            }
	                            mApplicationThread.setAction(AllApplicationsThread.ACTION_SYNC_TODB, apps);
	                            
	                         	/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-3-26 S*/    
	                            Reaper.processReaper( XLauncher.this, 
	                            	   Reaper.REAPER_EVENT_CATEGORY_APPLIST, 
	                    			   Reaper.REAPER_EVENT_ACTION_APPLIST_SORT,
	                    			   String.valueOf(position), 
	                    			   Reaper.REAPER_NO_INT_VALUE );
	                            /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-3-26 E*/ 
	                        }
	                    } // end run
	                });

				dialog.dismiss();
			}
		});
		dialog.setContentView(contentView);
		dialog.show();
		mLeosDialog = dialog;
    }
    
    public class SettingsChangedReceiver extends BroadcastReceiver {
    	
    	@Override
    	public void onReceive(Context context, Intent intent) {
    		if(LoadBootPolicy.getInstance(XLauncher.this).getDefaultProfileProcessingState()){
    			return;
    		}
    		
    		final String action = intent.getAction();
    		if (SettingsValue.ACTION_NETWORK_ENABLER_CHANGED.equals(action)) {
            	boolean networkEnabler = intent.getBooleanExtra(SettingsValue.EXTRA_NETWORK_ENABLED, false);
            	if (SettingsValue.isNetworkEnabled(context) != networkEnabler) {
            	    SettingsValue.enableNetwork(networkEnabler);
            	    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            	    editor.putBoolean(SettingsValue.PREF_NETWORK_ENABLER, networkEnabler);
            	    editor.commit();
            	    Settings.System.putInt(getContentResolver(), 
            	    		SettingsValue.PREF_NETWORK_ENABLER, 
            	    		networkEnabler ? 1 : 0);
            	}
            	
            	boolean fromWeather = intent.getBooleanExtra(SettingsValue.EXTRA_ISFROM_WEATHER, false);
            	if (fromWeather) {
            		Intent outIntent = new Intent(SettingsValue.ACTION_NETWORK_ENABLER_CHANGED);
            		outIntent.putExtra(SettingsValue.EXTRA_NETWORK_ENABLED, SettingsValue
                            .isNetworkEnabled(XLauncher.this));
                    sendBroadcast(outIntent);
            	}
            } else if (SettingsValue.ACTION_TEXT_SIZE_CHANGED.equals(action)) {
                resetWorkspace(APPLIST_FLAG | WORKSPACE_FLAG, false, Integer.MIN_VALUE,Integer.MIN_VALUE);
            } else if (SettingsValue.ACTION_APP_EDIT_CHANGED.equals(action)) {
                resetWorkspace(APPLIST_FLAG, false, Integer.MIN_VALUE,Integer.MIN_VALUE);
            } else if (SettingsValue.ACTION_INDICATOR_CHANGED.equals(action)) {
//                mWorkspace.resetHomePoint(true, true);
            } else if (SettingsValue.ACTION_START_WALLPAPER.equals(action)) {
                boolean delayExitSpringLoadedMode = false;
                mWaitingForResult = false;
                int resultCode = intent.getIntExtra(SettingsValue.KEY_WALLPAPER_SETTING_RESULTCODE, RESULT_CANCELED);
//                exitSpringLoadedDragModeDelayed((resultCode != RESULT_CANCELED), delayExitSpringLoadedMode);
            } else if (SettingsValue.ACTION_RESET_APPS_WALLPAPER.equals(action)) {
                Toast.makeText(context, R.string.app_wallpaper_reset_success, Toast.LENGTH_SHORT).show();
            } else if (SettingsValue.ACTION_START_APPS_WALLPAPER.equals(action)) {
                Toast.makeText(context, R.string.app_wallpaper_change_success, Toast.LENGTH_SHORT).show();
            } else if (SettingsValue.ACTION_ICON_STYLE_INDEX_CHANGED.equals(action)) {
                int index = intent.getIntExtra(SettingsValue.PREF_ICON_BG_STYLE, -1);
                SettingsValue.setIconStyleIndex(index);
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
                       getApplicationContext()).edit();
                editor.putInt(SettingsValue.PREF_ICON_BG_STYLE, index);
                try {
                    editor.apply();
                } catch (AbstractMethodError unused) {
                    editor.commit();
                }
                resetWorkspace(APPLIST_FLAG | WORKSPACE_FLAG, true, index,Integer.MIN_VALUE);
            } else if (SettingsValue.ACTION_PERSISTENT_CHANGED.equals(action)) {
                setPersistent(intent.getBooleanExtra(SettingsValue.EXTRA_PERSISTENT_ENABLED, false));
            }
            else if (SettingsValue.ACTION_WORKSPACE_LOOP.equals(action) && mWorkspace != null
                    && intent.hasExtra(SettingsValue.EXTRA_WORKSPACE_IS_LOOP)) {
            	mWorkspace.setLoop(intent.getBooleanExtra(SettingsValue.EXTRA_WORKSPACE_IS_LOOP, true));
            }
            else if (SettingsValue.ACTION_CELLY_CHANGED.equals(action)) {
                if (mAppListView != null) {
                	mAppListView.appsCellYChanged();
                }
//            }else if (SettingsValue.ACTION_SCREEN_CELLCOUNT_CHANGED.equals(action)) {
//                if (mWorkspace != null&&mModel!=null) {
//                	mWorkspace.screenCellCountChanged();
//                	mWorkspace.removeAllViews();
//                	setupViews();
//                	mWorkspaceLoading = true;
//                	mModel.startLoaderFromBackground();
//                }
            } 
            else if (SettingsValue.ACTION_TEXT_BACKGROUND_ONOFF.equals(action)) {
            	if( mWorkspace != null ) {
            		mWorkspace.setIconTextBackgroundEnable(false);
                }
                if( mHotseat != null ) {
                    mHotseat.refreshIconStyle(mIconCache, false);
                }

                if( mAppListView != null ) {
                    mAppListView.setIconTextBackgroundEnable(SettingsValue.isDesktopTextBackgroundEnabled(XLauncher.this));
                }
            }
            else if (SettingsValue.ACTION_WORKSPACE_CLEANUP.equals(action)) {
            	List<Parcelable> wi = (List<Parcelable>)intent.getParcelableArrayListExtra(SettingsValue.EXTRA_CLEANUP_ITEMS);
            	boolean toDelete = intent.getBooleanExtra(SettingsValue.EXTRA_CLEANUP_METHOD, true);
            	if (toDelete) {
            		mHandler.removeMessages(MSG_REMOVE_SHORTCUTS);
            		mHandler.sendMessage(mHandler.obtainMessage(MSG_REMOVE_SHORTCUTS, wi));
            	} else {
            		mHandler.removeMessages(MSG_MOVE_SHORTCUTS_TO_FOLDER);
            		mHandler.sendMessage(mHandler.obtainMessage(MSG_MOVE_SHORTCUTS_TO_FOLDER, wi));
            	}
            }
            else if (SettingsValue.ACTION_APPLIST_LOOP.equals(action)) {
                final boolean isApplistLoop = intent.getBooleanExtra(SettingsValue.EXTRA_APPLIST_IS_LOOP, true);
                Log.d("APPLIST_LOOP", "APPLIST_LOOP " + isApplistLoop);
                if( mAppListView != null ) {
                	mAppListView.setLoop(isApplistLoop);
                }
            }/* else if (SettingsValue.ACTION_APPLIST_ICON_TEXT_BACKGROUND.equals(action)) {
                final boolean isApplistIconBackgroundEnable = intent.getBooleanExtra(SettingsValue.EXTRA_APPLIST_ICON_TEXT_BACKGROUND, true);
                Log.d("APPLIST_LOOP", "APPLIST_ICON_TEXT_BACKGROUND " + isApplistIconBackgroundEnable);
                if( mAppListView != null ) {
                	mAppListView.setIconTextBackgroundEnable(isApplistIconBackgroundEnable);
                }
            }*/
            else if (SettingsValue.ACTION_ICON_SIZE_CHANGED.equals(action)) {
            	int sizeS = intent.getIntExtra("iconSize", Integer.MIN_VALUE);
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
                       getApplicationContext()).edit();
                DisplayMetrics dm = getResources().getDisplayMetrics();
        		float mDeviceDensity = dm.density;
        		int iconSize = Math.round(sizeS/mDeviceDensity);
        		String[] keys = getResources().getStringArray(R.array.pref_icon_size_values);
        		String value = null;
        		if (iconSize == Integer.parseInt(keys[0])) {
        			value = "NORMAL";
    			} else if (iconSize == Integer.parseInt(keys[1])) {
        			value = "SMALL";
    			}
                editor.putString(SettingsValue.PREF_ICON_SIZE_NEW, value);
                try {
                    editor.apply();
                } catch (AbstractMethodError unused) {
                    editor.commit();
                }
              //  resetWorkspace(APPLIST_FLAG | WORKSPACE_FLAG, true, Integer.MIN_VALUE ,sizeS);
                updateIconSizeValue();
                /***RK_ID:RK_CHANGE_ICON_SIZE AUT:zhanglz1@lenovo.com. DATE:2013-01-14.E***/       
            }
            else if ("com.lenovo.action.BRIGTHNESS_CHANGED".equals(action))
            {
                int brightness = intent.getIntExtra("brightness", -1);
                if (brightness != -1)
                {
                    setBrightness(brightness);
                }                
            } else if (SettingsValue.ACTION_REFRESH_MNG_VIEW.equals(action)) {
            	if (mWorkspace != null) {
            		refreshMngViewDelayed(3000L, mWorkspace.getCurrentPage());
            	}
            } else if (Intent.ACTION_WALLPAPER_CHANGED.equals(action)) {
                mWallpapperBlur.checkLiveWallpaper();
                /*** RK_ID: HIGH_QUALITY_EFFECTS. AUT: zhaoxy. DATE: 2013-10-16 . START***/
                if (mWallpapperBlur.isEnable() && SettingsValue.ENABLE_HIGH_QUALITY_EFFECTS) {
                /*** RK_ID: HIGH_QUALITY_EFFECTS. AUT: zhaoxy. DATE: 2013-10-16 . END***/
                    killLejingpin(); 
//                    mWallpapperBlur.buildBlurBitmap();
                }
            } else if (Intent.ACTION_CONFIGURATION_CHANGED.equals(action)){
                Log.e("yumin0813","recive intent  22222222222222222 onPasuseflag="+onPauseFlag+" orientation="+mCurOrientation+" getcuro"+getCurrentOrientation());
                if(!onPauseFlag) {
                    orientationflag = false;
                  return; 
                  }
                if(getCurrentOrientation() != mCurOrientation){
                    orientationflag = true;
                    mCurOrientation = getCurrentOrientation();

                }

            }else if("action.lenovo.smartsidebar.hide".equals(action)){
                boolean toDelete = intent.getBooleanExtra("smartsidebar_hide", true);
                Log.e("yumin0829","recive intent  22222222222222222 onPasuseflag="+toDelete);
                if(!toDelete){//popup the smart sidebar
                    if (mWorkspaceMenuDialog != null && mWorkspaceMenuDialog.isShowing()) {
            	        mWorkspaceMenuDialog.dismiss();
                    }
               }else{   //dimiss the smart sidebar
                    if (mWorkspaceMenuDialog != null && mWorkspaceMenuDialog.isShowing()) {
                        mWorkspaceMenuDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            	        mWorkspaceMenuDialog.show();
                        mRSSTimeoutHandler.removeMessages(MSG_ADD_DEFWIDGET);
                        mRSSTimeoutHandler.sendMessageDelayed(mRSSTimeoutHandler.obtainMessage(MSG_ADD_DEFWIDGET), 10);
                    }
                }
            }//add by zhanggx1 for reordering all pages on 2013-11-20. s
            else if (SettingsValue.ACTION_DO_AUTO_REORDER.equals(action)) {
            	boolean autoReorder = SettingsValue.isAutoReorderEnabled(XLauncher.this);
            	if (autoReorder) {
            		autoReorder(true);
            	} else {
        		    handlePendingRunnable();
        		}
            	
            }
    		//add by zhanggx1 for reordering all pages on 2013-11-20. e
    	}
	}
    private boolean onPauseFlag = false;
    private boolean orientationflag = false;
    private int mCurOrientation = -1;

    public int getCurrentOrientation(){
        return getResources().getConfiguration().orientation;
    }
    private void killLejingpin() {
        if(SettingsValue.getCurrentMachineType( this ) == -1){
            mWallpapperBlur.buildBlurBitmap();
        }else{
            //delay build bulr bitmap for kill lejingpin free the memory by yumina for pad 20131022
            mRSSTimeoutHandler.removeMessages(MSG_DELAY_BUILDBLURBITMAP);
            mRSSTimeoutHandler.sendMessageDelayed(mRSSTimeoutHandler.obtainMessage(MSG_DELAY_BUILDBLURBITMAP), 1000);
        }
    }


    
    private void resetWorkspace(int flag, boolean includeBitmap, int iconStyleIndex,int iconSize) {
		if(LoadBootPolicy.getInstance(XLauncher.this).getDefaultProfileProcessingState()){
			return;
		}
        mRSSTimeoutHandler.removeMessages(MSG_RESET_WORKSPACE);
        Message msg = mRSSTimeoutHandler.obtainMessage(MSG_RESET_WORKSPACE);
        msg.arg1 = flag;
        msg.arg2 = iconStyleIndex;
        msg.obj = includeBitmap;
        Bundle b = new Bundle(); 
        b.putInt("iconSize", iconSize); 
        msg.setData(b);
        mRSSTimeoutHandler.sendMessage(msg);
    }
    
    Handler mRSSTimeoutHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
    		if(LoadBootPolicy.getInstance(XLauncher.this).getDefaultProfileProcessingState()){
    			return;
    		}
        	
            switch (msg.what) {
            case MSG_DELAY_BUILDBLURBITMAP: 
                    mWallpapperBlur.buildBlurBitmap();
                    break;
            case MSG_ADD_DEFWIDGET:
//                delayLoadIconCell(msg.arg1);
                     mWorkspaceMenuDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                break;
            case MSG_ADD_EXISTFOLDER:
//                delayLoadAddExistFolder(msg.arg1);
                break;
            case MSG_RESET_WORKSPACE:
                int flag = msg.arg1;
                int iconIndex = msg.arg2;
                int iconSize = msg.getData().getInt("iconSize");
                refreshIconStyleAndSize(((flag & WORKSPACE_FLAG) == WORKSPACE_FLAG), ((flag & APPLIST_FLAG) == APPLIST_FLAG),
                        (Boolean) msg.obj, iconIndex,iconSize);
                break;
            case MSG_REFRESH_WORKSPACE:
//                refreshWorkspace(msg.arg1);
                break;
            }
        }
    };

    private void handleFolderClick(XFolderIcon folderIcon) {
        final FolderInfo info = folderIcon.mInfo;
        XFolder openFolder = mWorkspace.getFolderForTag(info);
        
        // If the folder info reports that the associated folder is open, then verify that
        // it is actually opened. There have been a few instances where this gets out of sync.
        if (info.opened && openFolder == null) {
            Log.d(TAG, "Folder info marked as open, but associated folder is not open. Screen: "
                    + info.screen + " (" + info.cellX + ", " + info.cellY + ")");
            info.opened = false;
        }

        if (!info.opened) {
            // Close any open folder
            // closeFolder();
            // Open the requested folder
            openFolder(folderIcon);
        } else {
            
        }
    }

    /*** RK_ID: RK_FULLSCREEN_FOLDER  AUT: gecn1 & zhaoxy . DATE: 2012-05-23 . START***/
    private float startX = 0;
    private float startY = 0;
    private boolean computeX = true, computeY = true;
    private int mExpandDuration;
    private float mScaleFolderAnimExtra = 1f;
    private boolean isFolderAnimating = false;
    private ValueAnimator inAnim = null, outAnim = null;
    float folderAniminput = 0f;
    /*** RK_ID: RK_FULLSCREEN_FOLDER  AUT: gecn1 & zhaoxy . DATE: 2012-05-23 . END***/
    
    /**
     * Opens the user folder described by the specified tag. The opening of the folder
     * is animated relative to the specified View. If the View is null, no animation
     * is played.
     *
     * @param folderInfo The FolderInfo describing the folder to open.
     */
    public void openFolder(XFolderIcon folderIcon) {
    	if (isScreenMngOpendOrExiting()) {
    		return;
    	}
    	

    	/*** fixbug LELAUNCHER-398. AUT: zhaoxy. DATE: 2013-10-17 . START***/
        if (mDragLayer != null) {
            mDragLayer.cleanDragView();
        }
    	/*** fixbug LELAUNCHER-398. AUT: zhaoxy. DATE: 2013-10-17 . END***/

        XFolder folder = folderIcon.mFolder;
        FolderInfo info = folder.getInfo();
//        mWorkspace.setBlurEnable(true);
        mWallpapperBlur.show(true);
        
        
        //growAndFadeOutFolderIcon(folderIcon);
        info.opened = true;
        
        if (!mWallpapperBlur.isEnable()) {
            mBlackboard.setTargetAlpha(.9f);
        }
        mBlackboard.show(true);

        // Just verify that the folder hasn't already been added to the DragLayer.
        // There was a one-off crash where the folder had a parent already.
        final boolean flag = isCurrentWindowFullScreen();
        if (folder.getParent() == null) {
            mDragLayer.addItem(folder);
            mDragLayer.checkDragViewToFront();
//            folder.setRelativeX((mDragLayer.getWidth() - folder.getWidth()) / 2);
//            folder.setRelativeY((mDragLayer.getHeight() + getReguForWindow() - folder.getHeight()) / 2);
            float y = 0;
            float h = mDragLayer.getHeight();
            if (flag && !SettingsValue.hasExtraTopMargin()) {
                y = getStatusBarHeight();
                h -= getStatusBarHeight();
            }
            folder.resize(new RectF(0, y, mDragLayer.getWidth(), h));
            mMainSurface.getDragController().addDropTarget(folder);
        } else {
            Log.w(TAG, "Opening folder (" + folder + ") which already has a parent (" +
                    folder.getParent() + ").");
        }
        getXLauncherView().bringContentViewToFront();
        folder.animateOpen();
        /*** RK_ID: RK_FULLSCREEN_FOLDER  AUT: gecn1 & zhaoxy . DATE: 2012-05-23 . START***/

        if (isCurrentWindowFullScreen()) {
            Log.i(TAG, "bring delete target to front .. ");
            mMainSurface.bringDelteTargetToFront();
        }

        RectF mIconRect = new RectF();
        DrawableItem descendant = folderIcon.getIconDrawable();
        Matrix mGlobalMatrix = new Matrix();
        mIconRect.set(descendant.localRect);
        mIconRect.offsetTo(0, 0);
        mGlobalMatrix.set(descendant.getInvertMatrix());
        mGlobalMatrix.postTranslate(-descendant.getRelativeX(), -descendant.getRelativeY());
        mGlobalMatrix.preConcat(mWorkspace.getMatrix());
        mGlobalMatrix.invert(mGlobalMatrix);
        float[] values = new float[9];
        mGlobalMatrix.getValues(values);
        for (int i = 0; i < values.length; i++) {
            if (values[i] == -0.0f) {
                values[i] = 0.0f;
            }
        }
        mGlobalMatrix.setValues(values);
        mGlobalMatrix.mapRect(mIconRect);
        startX = mIconRect.centerX();
        startY = mIconRect.centerY();
        if (mWallpapperBlur.isEnable()) {
            computeX = mWallpapperBlur.checkX(startX);
            computeY = mWallpapperBlur.checkY(startY);
        }
        mScaleFolderAnimExtra = folder.getKuang().getWidth() / folderIcon.getIconDrawable().getWidth();
        this.playFolderAnimExtraIn(startX, startY, computeX, computeY);
        this.mWallpapperBlur.show(true);
        
        /*** RK_ID: RK_FULLSCREEN_FOLDER  AUT: gecn1 & zhaoxy . DATE: 2012-05-23 . END***/
        /*** RK_ID: APPS_CATEGORY. AUT: zhaoxy . DATE: 2013-03-23 . START ***/
        folderIcon.dismissTip();
        /*** RK_ID: APPS_CATEGORY. AUT: zhaoxy . DATE: 2013-03-23 . END ***/
    }

    /*** RK_ID: RK_FULLSCREEN_FOLDER  AUT: gecn1 & zhaoxy . DATE: 2012-05-23 . START***/
    
    private void playFolderAnimExtraIn(final float startX, final float startY, final boolean computeX, final boolean computeY) {
        if (outAnim != null) {
            outAnim.cancel();
        }
        if (inAnim != null) {
            inAnim.cancel();
        }
        mWorkspace.getPagedView().hideViewContainerCurrentPage();
        ((XWallpaperPagedView) mWorkspace.getPagedView()).isdraw = true;
        inAnim = ValueAnimator.ofFloat(folderAniminput, 1f);
        inAnim.setInterpolator(new CubicInterpolator(CubicInterpolator.OUT));
        inAnim.addUpdateListener(new AnimatorUpdateListener() {

            final Matrix tempM = new Matrix();
            float alphaOutEnd = .8f;
            float alphaOutDuration = .4f;
            float scale = 1f;
            float alpha = 0f;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                folderAniminput = (Float) (animation.getAnimatedValue());
                alpha = (alphaOutEnd - folderAniminput) / alphaOutDuration;
                if (alpha > 1f) {
                    alpha = 1f;
                } else if (alpha < 0) {
                    alpha = 0;
                }
                scale = (mScaleFolderAnimExtra - 1) * folderAniminput + 1;
                tempM.setScale(scale, scale);
                tempM.preTranslate(-startX, -startY);
                float deltaX = (mDragLayer.localRect.centerX() - startX) * folderAniminput;
                float deltaY = (mDragLayer.localRect.centerY() - startY) * folderAniminput;
                tempM.postTranslate(startX + deltaX, startY + deltaY);
                mWorkspace.updateMatrix(tempM);
                mWorkspace.updateFolderAnim(folderAniminput, mScaleFolderAnimExtra);
                mWorkspace.setAlpha(alpha);
                mHotseat.updateMatrix(tempM);
                mHotseat.setAlpha(alpha);
                tempM.reset();
                scale = .5f * folderAniminput + 1;
                tempM.setScale(scale, scale);
                tempM.preTranslate(-startX, -startY);
                if (!computeX) {
                    deltaX = 0;
                }
                if (!computeY) {
                    deltaY = 0;
                }
                tempM.postTranslate(startX + deltaX, startY + deltaY);
                mWallpapperBlur.updateFolderAnim(folderAniminput);
                	mWallpapperBlur.updateMatrix(tempM);
               
            }

        });
        inAnim.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                isFolderAnimating = true;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mWorkspace.getPagedView().hideViewContainerCurrentPage();
                final Matrix tempM = new Matrix();
                tempM.setScale(mScaleFolderAnimExtra, mScaleFolderAnimExtra);
                tempM.preTranslate(-startX, -startY);
                tempM.postTranslate(mDragLayer.localRect.centerX(), mDragLayer.localRect.centerY());
                mWorkspace.updateMatrix(tempM);
                mWorkspace.updateFolderAnim(folderAniminput, mScaleFolderAnimExtra);
                mWorkspace.setAlpha(0f);
                mHotseat.updateMatrix(tempM);
                mHotseat.setAlpha(0f);
                mWallpapperBlur.updateFolderAnim(folderAniminput);
                
                isFolderAnimating = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });

//        FrameLayoutEx ex = mWorkspace.getPagedView().getStage();
//        ObjectAnimator ax = ObjectAnimator.ofFloat(ex, "scaleX", 1f, mScaleFolderAnimExtra);
//        ObjectAnimator ay = ObjectAnimator.ofFloat(ex, "scaleY", 1f, mScaleFolderAnimExtra);
//        ObjectAnimator ap = ObjectAnimator.ofFloat(ex, "alpha", 1f, 0f);
//        ax.setDuration(mExpandDuration - 100);
//        ay.setDuration(mExpandDuration - 100);
//        ap.setDuration(mExpandDuration - 100);
        inAnim.setDuration(mExpandDuration);
        inAnim.start();
//        AnimatorSet animSetXY = new AnimatorSet();
//        animSetXY.playTogether(inAnim, ax, ay, ap);
//        animSetXY.start();
    }

    public void playFolderAnimExtraOut(final float startX, final float startY, final boolean computeX, final boolean computeY) {
        if (outAnim != null) {
            outAnim.cancel();
        }
        if (inAnim != null) {
            inAnim.cancel();
        }
        ((XWallpaperPagedView) mWorkspace.getPagedView()).isdraw = true;
        outAnim = ValueAnimator.ofFloat(folderAniminput, 0f);
        outAnim.setInterpolator(new QuartInterpolator(QuartInterpolator.OUT));
        outAnim.addUpdateListener(new AnimatorUpdateListener() {

            final Matrix tempM = new Matrix();
            float scale = 1f;
            float alpha = 0;
            float alphaInStart = .6f;
            float alphaInDuration = .4f;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                folderAniminput = (Float) (animation.getAnimatedValue());
                alpha = (alphaInStart - folderAniminput) / alphaInDuration;
                if (alpha > 1f) {
                    alpha = 1f;
                } else if (alpha < 0) {
                    alpha = 0;
                }
                scale = (mScaleFolderAnimExtra - 1) * folderAniminput + 1;
                tempM.setScale(scale, scale);
                tempM.preTranslate(-startX, -startY);
                float deltaX = (mDragLayer.localRect.centerX() - startX) * folderAniminput;
                float deltaY = (mDragLayer.localRect.centerY() - startY) * folderAniminput;
                tempM.postTranslate(startX + deltaX, startY + deltaY);
                mWorkspace.updateMatrix(tempM);
                mWorkspace.updateFolderAnim(folderAniminput, mScaleFolderAnimExtra);
                mWorkspace.setAlpha(alpha);
                mHotseat.updateMatrix(tempM);
                mHotseat.setAlpha(alpha);

                tempM.reset();
                scale = .5f * folderAniminput + 1;
                tempM.setScale(scale, scale);
                tempM.preTranslate(-startX, -startY);
                if (!computeX) {
                    deltaX = 0;
                }
                if (!computeY) {
                    deltaY = 0;
                }
                tempM.postTranslate(startX + deltaX, startY + deltaY);
                mWallpapperBlur.updateFolderAnim(folderAniminput);
                mWallpapperBlur.updateMatrix(tempM);
                
            }

        });
        outAnim.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ((XWallpaperPagedView) mWorkspace.getPagedView()).isdraw = false;
                mWorkspace.getPagedView().showViewContainerCurrentPage();
                final Matrix tempM = new Matrix();
                mWorkspace.updateMatrix(tempM);
                mWorkspace.updateFolderAnim(folderAniminput, mScaleFolderAnimExtra);
                mWallpapperBlur.updateFolderAnim(folderAniminput);
                
                mWorkspace.setAlpha(1f);
                mHotseat.updateMatrix(tempM);
                mHotseat.setAlpha(1f);
                isFolderAnimating = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                ((XWallpaperPagedView) mWorkspace.getPagedView()).isdraw = false;
            }
        });
//        FrameLayoutEx ex = mWorkspace.getPagedView().getStage();
//        ObjectAnimator ax = ObjectAnimator.ofFloat(ex, "scaleX", mScaleFolderAnimExtra, 1f);
//        ObjectAnimator ay = ObjectAnimator.ofFloat(ex, "scaleY", mScaleFolderAnimExtra, 1f);
//        ObjectAnimator ap = ObjectAnimator.ofFloat(ex, "alpha", 0f, 1f);
//        ax.setDuration(mExpandDuration - 100);
//        ay.setDuration(mExpandDuration - 100);
//        ap.setDuration(mExpandDuration - 100);
        outAnim.setDuration(mExpandDuration);
        outAnim.setStartDelay(150);
        outAnim.start();
        /*** RK_ID: HIGH_QUALITY_EFFECTS. AUT: zhaoxy. DATE: 2013-10-16 . START***/
        isFolderAnimating = true;
        mWorkspace.getPagedView().hideViewContainerCurrentPage();
        /*** RK_ID: HIGH_QUALITY_EFFECTS. AUT: zhaoxy. DATE: 2013-10-16 . END***/
//        AnimatorSet animSetXY = new AnimatorSet();
//        animSetXY.playTogether(ax, ay, ap);
//        animSetXY.start();
    }

    public boolean isFolderAnimating() {
        return isFolderAnimating;
    }

    /*** RK_ID: RK_FULLSCREEN_FOLDER  AUT: gecn1 & zhaoxy . DATE: 2012-05-23 . END***/

    public void closeFolder() {
        // fix bug 17844
        if (mWorkspace == null) {
            return;
        } else {
            mWorkspace.getPagedView().restoreStage();
        }
        XFolder folder = mWorkspace.getOpenFolder();
        if (folder != null) {
            if(folder.getisclose()){
            	folder.setisclose(false);
            	return;
            }
            // fix bug 379 by liuli1
            float y = (int) (mDragLayer.getHeight() - folder.getHeight());
//            folder.setRelativeY(y);
            if (Float.compare(y, folder.getRelativeY()) != 0) {
                Log.w(TAG, "relativeY is not current, cannot close it." + y);
                return;
            }
        	closeFolder(folder);
        	
            if (mWorkspace != null && !mDragController.isDragging()) {
                mWorkspace.getPagedView().bringStageToFront();
            }
//            if (folder.isEditingName()) {
//                folder.dismissEditingName();
//        }
        }
    }

    public void closeFolderNow() {
        if (mWorkspace == null) {
            return;
        } else {
            mWorkspace.getPagedView().restoreStage();
        }
        XFolder folder = mWorkspace.getOpenFolder();
        if (folder != null) {
            folder.setisclose(false);
            float y = mDragLayer.getHeight() - folder.getHeight();
            folder.setRelativeY(y);

            // close now start

            folder.getInfo().opened = false;

            BaseDrawableGroup parent = (BaseDrawableGroup) folder.getParent();
            if (parent != null) {
                XFolderIcon fi = folder.getXFolderIcon();
                shrinkAndFadeInFolderIcon(fi);
                RectF mIconRect = new RectF();
                DrawableItem descendant = fi.getIconDrawable();
                Matrix mGlobalMatrix = new Matrix();
                mIconRect.set(descendant.localRect);
                mIconRect.offsetTo(0, 0);
                mGlobalMatrix.set(descendant.getInvertMatrix());
                mGlobalMatrix.postTranslate(-descendant.getRelativeX(), -descendant.getRelativeY());
                mGlobalMatrix.preConcat(mWorkspace.getMatrix());
                mGlobalMatrix.invert(mGlobalMatrix);
                float[] values = new float[9];
                mGlobalMatrix.getValues(values);
                for (int i = 0; i < values.length; i++) {
                    if (values[i] == -0.0f) {
                        values[i] = 0.0f;
                    }
                }
                mGlobalMatrix.setValues(values);
                mGlobalMatrix.mapRect(mIconRect);
                startX = mIconRect.centerX();
                startY = mIconRect.centerY();
                boolean inHotseat = folder.getInfo().container == LauncherSettings.Favorites.CONTAINER_HOTSEAT;
                if (inHotseat) {
                    if (isCurrentWindowFullScreen() && !SettingsValue.hasExtraTopMargin()) {
                        startY -= getStatusBarHeight();
                    }
                }
                mScaleFolderAnimExtra = folder.getKuang().getWidth() / fi.getIconDrawable().getWidth();
                folder.closeNow();
            }

            if (outAnim != null) {
                outAnim.cancel();
            }
            if (inAnim != null) {
                inAnim.cancel();
            }
            ((XWallpaperPagedView) mWorkspace.getPagedView()).isdraw = false;
            mWorkspace.getPagedView().showViewContainerCurrentPage();
            final Matrix tempM = new Matrix();
            mWorkspace.updateMatrix(tempM);
            mWorkspace.updateFolderAnim(0, mScaleFolderAnimExtra);
            mWallpapperBlur.updateFolderAnim(0);
            
            mWorkspace.setAlpha(1f);
            mHotseat.updateMatrix(tempM);
            mHotseat.setAlpha(1f);

            mBlackboard.hide();

            animFolder = null;

            // close now finished

            if (!mDragController.isDragging()) {
                mWorkspace.getPagedView().bringStageToFront();
            }
        }
    }

    private XFolder animFolder = null;

    public XFolder getAnimateFolder() {
        return animFolder;
    }

    void closeFolder(XFolder folder) {
        folder.getInfo().opened = false;
        animFolder = folder;

        BaseDrawableGroup parent = (BaseDrawableGroup) folder.getParent();
        if (parent != null) {
            XFolderIcon fi = folder.getXFolderIcon();
            shrinkAndFadeInFolderIcon(fi);
            RectF mIconRect = new RectF();
            DrawableItem descendant = fi.getIconDrawable();
            Matrix mGlobalMatrix = new Matrix();
            mIconRect.set(descendant.localRect);
            mIconRect.offsetTo(0, 0);
            mGlobalMatrix.set(descendant.getInvertMatrix());
            mGlobalMatrix.postTranslate(-descendant.getRelativeX(), -descendant.getRelativeY());
            mGlobalMatrix.preConcat(mWorkspace.getMatrix());
            mGlobalMatrix.invert(mGlobalMatrix);
            float[] values = new float[9];
            mGlobalMatrix.getValues(values);
            for (int i = 0; i < values.length; i++) {
                if (values[i] == -0.0f) {
                    values[i] = 0.0f;
                }
            }
            mGlobalMatrix.setValues(values);
            mGlobalMatrix.mapRect(mIconRect);
            startX = mIconRect.centerX();
            startY = mIconRect.centerY();
            boolean inHotseat = folder.getInfo().container == LauncherSettings.Favorites.CONTAINER_HOTSEAT;
            if (inHotseat) {
                if (isCurrentWindowFullScreen() && !SettingsValue.hasExtraTopMargin()) {
                    startY -= getStatusBarHeight();
                }
            }
            mScaleFolderAnimExtra = folder.getKuang().getWidth() / fi.getIconDrawable().getWidth();
            folder.animateClosed(startX, startY);
        }
        playFolderAnimExtraOut(startX, startY, computeX, computeY);
        mBlackboard.hide();
    }

    private void shrinkAndFadeInFolderIcon(XFolderIcon fi) {
        // TODO XFolderIcon play shrink And Fade Animation.
    }

    @Override
    public void onFolderOpen() {
    	
    	// layer
    	getXLauncherView().bringContentViewToFront();
        mDragLayer.cancelPendulumAnim();
    	
        if (mWorkspace != null) {
            mWorkspace.setTouchable(false);
            mWorkspace.getPagedView().setTouchable(false);
            mWorkspace.getPagedView().hideStage();
            
          //add for quick drag mode by sunzq3, begin;
            final XPagedViewIndicator mIndicator = mWorkspace.getPageIndicator();
            if (mIndicator != null && (mIndicator.isSingleVisible() || mWorkspace.getPageCount() > 1)) {
                mWorkspace.getPageIndicator().setVisibility(false);
            }
          //add for quick drag mode by sunzq3, end;
        }
        if (mHotseat != null) {
            mHotseat.setTouchable(false);
        }
    }
    
    @Override
    public void onFolderClose() {
    	// layer
        //if (mWorkspace != null && !mDragController.isDragging()) {
    	//mWorkspace.getPagedView().bringStageToFront();
        //}
    	
        if (mWorkspace != null) {
            mWorkspace.getPagedView().setTouchable(true);
          //add for quick drag mode by sunzq3, begin;
            final XPagedViewIndicator mIndicator = mWorkspace.getPageIndicator();
            if (mIndicator != null && (mIndicator.isSingleVisible() || mWorkspace.getPageCount() > 1)) {
                mWorkspace.getPageIndicator().setVisibility(true);
            }
          //add for quick drag mode by sunzq3, end;
            mWorkspace.setTouchable(true);
//            mWorkspace.setBlurEnable(false);
            mWallpapperBlur.hide();
            
            mBlackboard.setTargetAlpha(.4f);
        }
        if (mHotseat != null) {
            mHotseat.setTouchable(true);
        }
        if (mDragController != null && mDragController.isDragging()) {
            mDragController.forceMoveEvent();
        }

        animFolder = null;
    }
    /*** RK_ID: XFOLDER.  AUT: zhaoxy . DATE: 2013-01-22 . END***/
    
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        
		if(LoadBootPolicy.getInstance(this).getDefaultProfileProcessingState()){
			return;
		}

        // Listen for broadcasts related to user-presence
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(SettingsValue.ACTION_SHOW_THEME_DLG);
        filter.addAction(SettingsValue.ACTION_LETHEME_SHOW_PROGRESS_DIALOG);        
        filter.addAction(SettingsValue.ACTION_SCENE_APPLY_FINISHED);
        filter.addAction(SettingsValue.ACTION_SCENE_BACKUP_FINISHED);
        filter.addAction(SettingsValue.ACTION_SCENE_BACKUP);
        filter.addAction(SettingsValue.ACTION_SCENE_APPLY);
        filter.addAction(WeatherUtilites.ACTION_ADD_LENOVOWIDGET);
        filter.addAction(SettingsValue.ACTION_LAUNCHER_THEME);
/** ID: Profile restoring tools. AUT: liuyg1. DATE: 2012.04.18 . S */
        filter.addAction(ACTION_SCENE_APPLY);
/*RK_GESTURE_SETTING liuyg1@lenovo.com 2013-5-30 S*/
        filter.addAction(GestureManager.ACTION_GESTURE_SCROLL_UP);
        filter.addAction(GestureManager.ACTION_GESTURE_SCROLL_DOWN);
        filter.addAction(GestureManager.ACTION_GESTURE_DOUBLE_CLICK);
/*RK_GESTURE_SETTING liuyg1@lenovo.com 2013-5-30 E*/
        /** ID: Profile restoring tools. AUT: liuyg1. DATE: 2012.04.18 . E */
        registerReceiver(mReceiver, filter);
        mAttached = true;
        mVisible = true;
    }
    
    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        
		if(LoadBootPolicy.getInstance(this).getDefaultProfileProcessingState()){
			return;
		}
		
        mVisible = false;

        if (mAttached) {
            unregisterReceiver(mReceiver);
            mAttached = false;
        }
//        updateRunning();
    }
    
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
      /*RK_GESTURE_SETTING liuyg1@lenovo.com 2013-5-30 S*/  	
        	 String[] prefGestureValues = context.getResources().getStringArray(R.array.pref_gesture_values);
       /*RK_GESTURE_SETTING liuyg1@lenovo.com 2013-5-30 E*/ 	
			/** ID: Profile restoring. AUT: chengliang . DATE: 2012.03.22 . S */
	        if( LoadBootPolicy.getInstance(XLauncher.this).getDefaultProfileProcessingState() ){
	        	return;
	        }
	        /** ID: Profile restoring. AUT: chengliang . DATE: 2012.03.22 . E */
            final String action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
//                mUserPresent = false;
            	/*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-5-30. S***/
                mDragLayer.clearAllResizeFrames();
            	/*RK_ID:RK_ZOOM_WIDGETS zhangdxa 2013-5-30. E***/
//                updateRunning();
//
//                // Reset AllApps to its initial state only if we are not in the middle of
//                // processing a multi-step drop
                  if (mAppListView != null && mPendingAddInfo.container == ItemInfo.NO_ID) {
                      mAppListView.stopEditMode();
                      showWorkspace(false);
                  }
                	//new art
            	if (mDragController != null && mDragController.isDragging()) {
            		mDragController.onTouchCancel();
            	}
            	
            	if (mWorkspace != null) {
            		mWorkspace.onTouchCancel( null );
            	}
                // fix bug 16225
                if (mScreenMngView != null) {
                    mScreenMngView.resetPagedView();
                }
                // fix bug 17958
                if (mWorkspace != null) {
                    XFolder folder = mWorkspace.getOpenFolder();
                    if (folder != null && folder.getPagedView() != null
                            && folder.getPagedView().isPageMoving) {
                        folder.getPagedView().resetAnim();
                    }
                    closeFolder();
                }
            } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
//                mUserPresent = true;
//                updateRunning();
			} else if (SettingsValue.ACTION_SCENE_APPLY_FINISHED.equals(action)) {
				dismissThemeDialog();
				dismissProgressDialog(0);
			} else if (SettingsValue.ACTION_SCENE_BACKUP.equals(action)) {
				setDetailState(DetailState.SCENEBACKUP);
				dismissThemeDialog();
				showProgressDialog(getString(R.string.profile_backuping_progress_info));
			} else if (SettingsValue.ACTION_SCENE_BACKUP_FINISHED.equals(action)) {
				dismissThemeDialog();
				dismissProgressDialog(0);
			} else if (SettingsValue.ACTION_SCENE_APPLY.equals(intent.getAction())) {
				setDetailState(DetailState.SCENEAPPLING);
				dismissThemeDialog();
				showProgressDialog(getString(R.string.profile_applying_progress_info));
			}else if(WeatherUtilites.ACTION_ADD_LENOVOWIDGET.equals(action)){
				final Intent tempintent = intent;
				 mHandler.post(new Runnable() {
	        			@Override
	        			public void run() {
							LenovoWidgetViewInfo lenovoWidget = new LenovoWidgetViewInfo();
				            String packageName = tempintent.getStringExtra("EXTRA_PACKAGENAME");
				            String label = tempintent.getStringExtra("EXTRA_CALSS");
				            lenovoWidget.className = label;
				            lenovoWidget.packageName = packageName;
				            Log.d("gecn1","packageName="+packageName );
				            Log.d("gecn1","label="+label );
				            ComponentName component= new ComponentName(lenovoWidget.packageName, lenovoWidget.className);
				            lenovoWidget.componentName = component;
				            lenovoWidget.minWidth =  tempintent.getIntExtra("EXTRA_WIDTH", 286);
				            lenovoWidget.minHeight = tempintent.getIntExtra("EXTRA_HIEGHT", 286);
				            lenovoWidget.previewImage = R.drawable.lotus_icon;
				            lenovoWidget.cellX = mPendingAddInfo.cellX;
				            lenovoWidget.cellY = mPendingAddInfo.cellY;
				            lenovoWidget.screen = mWorkspace.getCurrentPage();
				            
				            addLeosWidgetViewToWorkspace(lenovoWidget);
	        			}
				 });
			} else if (SettingsValue.ACTION_LAUNCHER_THEME.equals(action)) {
                setDetailState(DetailState.THEMEAPPLING);
                dismissThemeDialog();
                if(mLoadWorkspaceDialog !=null  && !mLoadWorkspaceDialog.isShowing()){
                	showProgressDialog(getString(R.string.theme_appling_progress_info));
                }
                String packageName = intent.getStringExtra(SettingsValue.ACTION_LAUNCHER_THEME_NAME);
                themePackageName = packageName;

                if(packageName.equals("com.lenovo.launcher")) {
                        //modified by yumina for the themecener
                        packageName = SettingsValue.getDefaultAndroidTheme(XLauncher.this);
                }
                handleTheme(packageName, false, null);
 /** ID: Profile restoring tools. AUT: liuyg1. DATE: 2012.04.18 . S */
            }else if(ACTION_SCENE_APPLY.equals(action)){
            	setDetailState(DetailState.SCENEAPPLING);
				dismissThemeDialog();
				showProgressDialog(getString(R.string.profile_applying_progress_info));
				final String targetProfilePath = intent.getStringExtra("applyLbkfile");
				File defaultProfile = new File(targetProfilePath);
				if (!defaultProfile.exists()) {
                    if (mHandler != null) {
                    	mHandler.removeMessages(DISMISS_APPLY_PROGRESS);
                        Message msg = mHandler.obtainMessage();
                        msg.what = DISMISS_APPLY_PROGRESS;
                        msg.obj = OperationState.FAILED_NORMAL;
                        mHandler.sendMessage(msg);
                    }
					if(Debug.MAIN_DEBUG_SWITCH){
					R2.echo("Default profile not found !");
					}
					return;
					
				}
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                    	
                    	getSharedPreferences(SettingsValue.PREFS_FILE_NAME, Activity.MODE_PRIVATE).edit().putBoolean(SettingsValue.KEY_IS_DEFAULT_PROFILE, false).commit();
                    	
                        byte result =  BackupManager.getInstance(XLauncher.this).realRestore(targetProfilePath,
                				BackupManager.State.RESTORE_FACTORY, null,true);

                        if (mHandler != null) {
                        	mHandler.removeMessages(DISMISS_APPLY_PROGRESS);
                            Message msg = mHandler.obtainMessage();
                            msg.what = DISMISS_APPLY_PROGRESS;
                            msg.obj = result;
                            mHandler.sendMessage(msg);
                        }
                    }
                }, "ApplyProfile").start();
/** ID: Profile restoring tools. AUT: liuyg1. DATE: 2012.04.18 . E */
/*RK_GESTURE_SETTING liuyg1@lenovo.com 2013-5-30 S*/
            }else if(GestureManager.ACTION_GESTURE_SCROLL_UP.equals(action)){
            	String scrollUp = SettingsValue.getScrollUpGuestureArray(XLauncher.this);
            	if(scrollUp.equals(prefGestureValues[1])){
            		XLauncher.setScreen(mWorkspace.getCurrentPage());
            		showMenu();
            	}else if(scrollUp.equals(prefGestureValues[2])){
            		mGestureManager.showRecentTask();
            	}else if(scrollUp.equals(prefGestureValues[3])){
            		mGestureManager.showNotifications();
            	}
            }
            else if(GestureManager.ACTION_GESTURE_SCROLL_DOWN.equals(action)){
            	String scrollDown = SettingsValue.getScrollDownGuestureArray(XLauncher.this);
            	if(scrollDown.equals(prefGestureValues[1])){
            		XLauncher.setScreen(mWorkspace.getCurrentPage());
            		showMenu();
            	}else if(scrollDown.equals(prefGestureValues[2])){
            		mGestureManager.showRecentTask();
            	}else if(scrollDown.equals(prefGestureValues[3])){
            		mGestureManager.showNotifications();
            	}
            }
            else if(GestureManager.ACTION_GESTURE_DOUBLE_CLICK.equals(action)){
            	String doubleClick = SettingsValue.getDoubleClickGuestureArray(XLauncher.this);
            	if(doubleClick.equals(prefGestureValues[1])){
            		XLauncher.setScreen(mWorkspace.getCurrentPage());
            		showMenu();
            	}else if(doubleClick.equals(prefGestureValues[2])){
            		mGestureManager.showRecentTask();
            	}else if(doubleClick.equals(prefGestureValues[3])){
            		mGestureManager.showNotifications();
            	}
/*RK_GESTURE_SETTING liuyg1@lenovo.com 2013-5-30 E*/
            }
        }
    };
    private String themePackageName;

    
    private void dismissThemeDialog() {
    	if (mLeosDialog != null && mLeosDialog.isShowing()) {
    		mLeosDialog.dismiss();
    	}
    	mLeosDialog = null;
    	dismissQuickActionWindow();
    }
    
    public void showProgressDialog(String msg) {
        if (mProgressDlg != null && mProgressDlg.isShowing()) {
            Log.i("Test0303", "showProgressDialog, dialog is showing ... ");
            return ;
        }
        //test by dining 2013-4-23
        /*mProgressDlg = new Dialog(this, R.style.Theme_LeLauncher_ProgressDialog);
        LinearLayout ll = (LinearLayout) this.getLayoutInflater().inflate(R.layout.apply_progressbar, null);
        
        mProgressDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDlg.setContentView(ll);

        Window window = mProgressDlg.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        window.setGravity(Gravity.CENTER);

        TextView msgView = (TextView) ll.findViewById(R.id.progress_msg);
        msgView.setText(msg);

        mProgressDlg.setCancelable(false);
        */
        mProgressDlg = new LeProcessDialog(this);
    	if(mProgressDlg == null){
    		
    	}
    	mProgressDlg.setLeMessage(msg);
    	
        if (mProgressDlg.isShowing()) {
        	return;
        }
        mProgressDlg.show();
    }

    public void dismissProgressDialog(int resId) {
        try {
			if (mProgressDlg != null && mProgressDlg.isShowing()) {
			    mProgressDlg.dismiss();
			}
			mProgressDlg = null;
			if (resId != 0) {
                        if (getLenovoThemeCenterCurentVersionName("com.lenovo.themecenter")){
                        }else{
			    Toast.makeText(this, resId, Toast.LENGTH_LONG).show();
                        }
			}
		} catch (Exception e) {
			mProgressDlg = null;
		}
    }
    
    /*
     * set detail state
     * if lelauncher is appling the icon style, we modify this state
     */
    public void setDetailState(DetailState ds) {
        mDetailState = ds;
    }
    
    private class ApplyThemeTask extends AsyncTask<Object, Void, ArrayList<ApplicationInfo>> {
    	
    	/** RK_ID: RK_BUG_174814. AUT: zhangdxa DATE: 2013-3-28 S */
		private String mFlag = null;
		/** RK_ID: RK_BUG_174814. AUT: zhangdxa DATE: 2013-3-28 E */
		
		@Override
        protected void onPreExecute() {
        }

		@Override
		protected ArrayList<ApplicationInfo> doInBackground(Object... arg0) {
			final String packageName = arg0[0] == null ? null : arg0[0].toString();
			Context friendContext = arg0[1] == null ? null : (Context)arg0[1];
			
			AssetManager assetManager = null;
			InputStream is = null;
            ColorStateList iconTextColor = null;
	    	int iconBgIndex = SettingsValue.DEFAULT_ICON_BG_INDEX;
			
			if (packageName == null) {
				return null;
			}
			
			/** RK_ID: RK_BUG_174814. AUT: zhangdxa DATE: 2013-3-28 S */
			SharedPreferences.Editor editorStart = getSharedPreferences("theme_apply", Context.MODE_PRIVATE).edit();
			editorStart.putString(SettingsValue.PREF_THEME, packageName);
			editorStart.commit();
		    if( arg0.length > 2){
		    	 mFlag = arg0[2] == null ? null : arg0[2].toString();
		    }
			/** RK_ID: RK_BUG_174814. AUT: zhangdxa DATE: 2013-3-28 E */
		    
			boolean isDefaultTheme = packageName.equals(SettingsValue.getDefaultAndroidTheme(XLauncher.this));
			if (!isDefaultTheme && friendContext == null) {
	    		return null;
	    	}
			
			SettingsValue.setThemeValue(XLauncher.this, packageName);
			LauncherApplication app = (LauncherApplication) getApplicationContext();
	    	
			/*RK_default_icon_style dining@lenovo.com 2013-9-12 S*/
	    	//from BU request, when set the default theme,not use the default icon background
	    	boolean bStyleTemp = SettingsValue.getUseDefaultThemeIconStyle(XLauncher.this);
	    	if(isDefaultTheme && !bStyleTemp){
	    		iconBgIndex = -1;
	    	}
	    	/*RK_default_icon_style dining@lenovo.com 2013-9-12 E*/
	    	
	    	boolean appbgTranslucent_enable = true;
	    	if (isDefaultTheme) {
                app.mLauncherContext.setFriendContext(null);
            } else {
            	app.mLauncherContext.setFriendContext(friendContext);
            }
			try {
				if (isDefaultTheme) {
					SettingsValue.setThemeIconBg(null);
					/*RK_VERSION_WW dining 2012-10-25 S*/
					//此代码实际有效作用于BU版本，在BU版本中才添加海外版壁纸文件，
					//因为BU国内版的”石头“壁纸要避免海外的版权问题，所以海外版壁纸需要使用另一张壁纸
					is = getResources().openRawResource(R.drawable.wallpaper_grass);
					/*
					if(getVersionWWEnabled()){
						InputStream temp = getResources().openRawResource(R.drawable.wallpaper_grass_ww);
						if(temp != null){
							is = temp;
						}
					}
					*/
					/*RK_VERSION_WW dining 2012-10-25 S*/
				} else {
					
					/*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-5 S*/
					iconTextColor = app.mLauncherContext.getColor(R.color.apps_icon_text_color, R.color.apps_icon_text_color);
//					
					iconBgIndex = app.mLauncherContext.getIntegerByResName(SettingsValue.THEME_ICON_BG_CONFIG);
				
					SettingsValue.setThemeIconBg(friendContext);
					Bitmap[] themeIconBg = SettingsValue.getThemeIconBg();
					if (themeIconBg == null || themeIconBg[0] == null) {
						if (iconBgIndex == Integer.MIN_VALUE
								|| iconBgIndex < -1
								|| iconBgIndex >= com.lenovo.launcher2.customizer.Utilities.ICON_STYLE_COUNT) {
							iconBgIndex = -1;
						}
			        } else {
			        	iconBgIndex = SettingsValue.THEME_ICON_BG_INDEX;
			        }
					
					int wallpaperId = app.mLauncherContext.getIdByResName(SettingsValue.DEFAULT_WALLPAPER_NAME,"drawable",packageName);
					if (wallpaperId == 0) {
                        assetManager = friendContext.getAssets();	
                        //Notice: Because  Android library do not support asset so we need not do nothing for inbuild theme
                        //请注意：因为android library 不支持 asset 资源，内置主题资源不需要考虑这种情况
					    is = assetManager.open(SettingsValue.DEFAULT_WALLPAPER_NAME + ".png");
					} else {
						is = friendContext.getResources().openRawResource(wallpaperId);
					}
					 /*RK_SEMI_TRANSPARNET dining@lenovo.com 2012-10-16 S*/
					 int temp = app.mLauncherContext.getIntegerByResName(SettingsValue.APPBG_TRANSLUCENT_ENABLE_CONFIG);
					 if(temp == Integer.MIN_VALUE || temp <= 0){
						 appbgTranslucent_enable = false;
					 }
					  /*RK_SEMI_TRANSPARNET dining@lenovo.com 2012-10-16 E*/
				}
			}catch (IOException e) {
				Debug.
                        printException("Launcher->ApplyThemeTask. Read default_wallpaper.png error", e);
			} catch (NotFoundException e) {
				Debug.
                        printException("Launcher->ApplyThemeTask. Read drawalbe/default_wallpaper.png error", e);
			}
			
			try {
						
				ContentValues contentValues = new ContentValues();
				byte[] data = null;
				contentValues.put(LauncherSettings.Favorites.ICON_REPLACE, data);
				getContentResolver().update(LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION, 
						contentValues, 
						new StringBuilder().append("itemType!=")
						.append(LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT).toString(),
						null);	
			} catch (SQLiteDiskIOException e) {
				Debug.
                printException("Launcher->ApplyThemeTask. updateUserError", e);
			}
			
			// save theme to prefs
			if (isDefaultTheme) {
				iconTextColor = getResources().getColorStateList(R.color.apps_icon_text_color);
			} else {
		        if (iconTextColor == null) {
		        	iconTextColor = getResources().getColorStateList(R.color.def__apps_icon_text_color);
		        }
			}
	        SettingsValue.setIconTextStyleValue(iconTextColor.getDefaultColor());
	        SettingsValue.setIconStyleIndex(iconBgIndex);
	        
	        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(XLauncher.this).edit();
	        editor.putString(SettingsValue.PREF_THEME, packageName);
	        editor.putInt(SettingsValue.PREF_ICON_TEXT_STYLE, SettingsValue.getIconTextStyleValue(XLauncher.this));	        
        	editor.putInt(SettingsValue.PREF_ICON_BG_STYLE, iconBgIndex);
        	
        	 /*RK_SEMI_TRANSPARNET dining@lenovo.com 2012-10-16 S*/
        	editor.putBoolean(SettingsValue.PREF_APPLIST_BGSEMITARNSPARENT, appbgTranslucent_enable);
           /*RK_SEMI_TRANSPARNET dining@lenovo.com 2012-10-16 E*/
            /* RK_ID: RK_QUICKACTION. AUT: liuli1 . DATE: 2012-08-27 . START */
//            LauncherModel.clearViewCache();
            /* RK_ID: RK_QUICKACTION. AUT: liuli1 . DATE: 2012-08-27 . END */

	        try {
	            editor.apply();
	        } catch (AbstractMethodError unused) {
	            editor.commit();
	        }
	        
	        mHandler.removeMessages(MSG_SEND_THEME_APPLING);
            mHandler.sendMessage(mHandler.obtainMessage(MSG_SEND_THEME_APPLING, packageName));
	        			
            Uri appWallpaperPath = SettingsValue.getAppsWallperPath(XLauncher.this, true);
			if (appWallpaperPath != null) {
				File file = new File(appWallpaperPath.getPath());
				if (file.exists()) {
					file.delete();
				}
			}
			appWallpaperPath = SettingsValue.getAppsWallperTempPath(true);
			if (appWallpaperPath != null) {
				File file = new File(appWallpaperPath.getPath());
				if (file.exists()) {
					file.delete();
				}
			}
			
			WallpaperManager wm = (WallpaperManager) getSystemService(Context.WALLPAPER_SERVICE);
			
            if (is != null) {
            	try {
            		wm.setStream(is);
            		is.close();
    			} catch (IOException e) {
    				Debug.
    				        printException("Launcher->ApplyThemeTask. Set wallpaper error", e);
    			} finally {    				
    				is = null;
    			}
            }
            
            getModel().changeThemeIcon(XLauncher.this, mFlag);
			return null;
		}
		
		@Override
        protected void onPostExecute(ArrayList<ApplicationInfo> result) {
        }
		
	}
    
    
    
    private boolean getLenovoThemeCenterCurentVersionName( String packageName){
        final PackageManager manager = getPackageManager();
        try { PackageInfo info = manager.getPackageInfo(packageName, 0);
            String appVersion = info.versionName;   
            float versionnum = Float.valueOf(appVersion.substring(0,3));
            Log.d("PAD", " phone current ========appVersion="+appVersion+" code="+info.versionCode+" int num="+versionnum);
            if(versionnum >= 1.0){
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    
	public void refreshForThemes(final ArrayList<ApplicationInfo> apps) {
		if (apps == null) {
			return;
		}

		clearStaticBitmap();
		
		if (mHotseat != null) {
			mHotseat.changeHotseatThemes(apps);
		}
		
//		mWorkspace.resetHomePoint(true, true);
//		mWorkspace.setHomePointPosition();
//
//		mWorkspace.setHomePointVisibility(mLauncherService.bSnapShowFlag
//				&& !isAllAppsCustomizeOpen());
//		updatePageVisibility(!mWorkspace.isCustomState());
		bindAppsThoroughly(apps);
        if (mWorkspace != null && mWorkspace.getState() == XWorkspace.State.SCR_MGR && mScreenMngView != null) {
            mScreenMngView.updateTabContent();
        }

        if (mMainSurface != null) {
            mMainSurface.changeThemes();
        }
        
        //add by zhanggx1 for refresh mng view.s
        refreshMngViewOnUpdateWorkspace();
        //add by zhanggx1 for refresh mng view.e
	}
	
	private void clearStaticBitmap(){
	    //XFolderIcon的background所用的Bitmap是static类型，所有Folder共用，换主题时在这里清空，在第一次重新获取时赋值。
        //所有的staic都在这个函数中清空
		R5.echo("clearStaticBitmap");
	    XFolderIcon.clearBackgroundBitmap();
        ThemeStyleIndicator.clearBitmap();
        FolderRingAnimator.setDataDirty();
	}
	
	public void bindAppsThoroughly(final ArrayList<ApplicationInfo> apps) {
        setLoadOnResume();
        removeDialog(DIALOG_CREATE_SHORTCUT);
        
        mHandler.post(new Runnable() {
			@Override
			public void run() {
				if (mWorkspace != null) {
		            mWorkspace.updateShortcutsThoroughly(apps);
		        }				
			}        	
        });
        
        mHandler.post(new Runnable() {
			@Override
			public void run() {
                if (mMainSurface != null) {
                	mMainSurface.updateForTheme(apps);
                }
			}        	
        });
        sendBroadcast(new Intent(SettingsValue.ACTION_REFRESH_LOTUS));
    }
	
	Bitmap retrieveIcon(ShortcutInfo info) {
        Bitmap icon = null;

        if (info.customIcon) {
            // do nothing
        } else {
            // we need to find it from databases.
            final android.database.Cursor c = this.getContentResolver().query(
                    LauncherSettings.Favorites.getContentUri(info.id, false), null, null, null, null);

            if (c != null && c.moveToFirst()) {
                String packageName = c.getString(c.getColumnIndex(LauncherSettings.Favorites.ICON_PACKAGE));
                String resourceName = c.getString(c.getColumnIndex(LauncherSettings.Favorites.ICON_RESOURCE));

                PackageManager packageManager = getPackageManager();
                // the resource
                try {
                    Resources resources = packageManager.getResourcesForApplication(packageName);
                    if (resources != null) {
                        final int id = resources.getIdentifier(resourceName, null, null);
                        LauncherApplication la = (LauncherApplication)getApplicationContext();
                        icon = la.mLauncherContext.getIconBitmap(resources, id, packageName);
//                        icon = Utilities.createIconBitmap(mIconCache.getFullResIcon(resources, id), this, packageName);
                    }
                } catch (Exception e) {
                    // drop this. we have other places to look for icons
                }

                // the db
                if (icon == null) {
                    int iconIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.ICON);
                    icon = mModel.getIconFromCursor(c, iconIndex, this);
                }

                // the fallback icon
                if (icon == null) {
                    icon = mModel.getFallbackIcon();
                    info.usingFallbackIcon = true;
                }

                c.close();
            }
            info.setIcon(icon);
        }

        return icon;
    }
	
	public void initAtTheBeginning() {
    	String theme = null;
    	boolean resetWallpaper = false;
        /*** RK_SEMI_TRANSPARNET  . AUT: zhaoxy . DATE: 2013-05-07. START***/
        boolean appbgTranslucent_enable = true;
        /*** RK_SEMI_TRANSPARNET  . AUT: zhaoxy . DATE: 2013-05-07. END***/
    	LauncherApplication app = (LauncherApplication) getApplicationContext();
    	SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String defaultTheme = SettingsValue.getDefaultThemeValue(this);
        String defaultAndroidTheme = SettingsValue.getDefaultAndroidTheme(this);
        
        boolean firstStart = sharedPreferences.getBoolean(SettingsValue.PREF_FIRST_START, true);
    	if (firstStart) {
    		SharedPreferences.Editor editor = sharedPreferences.edit();
    		editor.putBoolean(SettingsValue.PREF_FIRST_START, false);
    		editor.commit();
    	}
        
    	String currentTheme = sharedPreferences.getString(SettingsValue.PREF_THEME, defaultTheme);
    	
    	int iconStyleIndex = sharedPreferences.getInt(SettingsValue.PREF_ICON_BG_STYLE, SettingsValue.DEFAULT_ICON_BG_INDEX);
    	if (defaultTheme.equals(defaultAndroidTheme) && defaultTheme.equals(currentTheme)) {    		
    		app.mLauncherContext.setFriendContext(null);
    		if (iconStyleIndex < -1 || iconStyleIndex >= com.lenovo.launcher2.customizer.Utilities.ICON_STYLE_COUNT) {
    			iconStyleIndex = SettingsValue.DEFAULT_ICON_BG_INDEX;
    		}
    		SettingsValue.setThemeIconBg(null);
    	} else if (!defaultTheme.equals(defaultAndroidTheme) && defaultAndroidTheme.equals(currentTheme)) {
    		theme = defaultTheme;    		
    		resetWallpaper = true;    		
    	} else {
    		theme = currentTheme;
    	}
    	Log.i(TAG,"------defaultTheme="+defaultTheme+" currentTheme="+currentTheme+" defaultAndroidTheme="+defaultAndroidTheme
    			   +"  final theme="+theme);
    	
    	if (theme != null) {
    		try {
				getPackageManager().getApplicationInfo(theme, 0);
			} catch (NameNotFoundException e1) {
				if (defaultAndroidTheme.equals(defaultTheme)) {
					app.mLauncherContext.setFriendContext(null);
		    		if (iconStyleIndex < -1 || iconStyleIndex >= com.lenovo.launcher2.customizer.Utilities.ICON_STYLE_COUNT) {
		    			iconStyleIndex = SettingsValue.DEFAULT_ICON_BG_INDEX;
		    		}
		    		SettingsValue.setThemeIconBg(null);
		    		theme = defaultAndroidTheme;
				} else {
					theme = defaultTheme;
				}
				Log.i(TAG,"getPackageManager().getApplicationInfo(theme, 0) exception"+ "  final theme="+theme);
//				resetWallpaper = true;
			}    		
    	}
    	if (theme != null && !defaultAndroidTheme.equals(theme)) {
			Context friendContext;
			try {
				friendContext = createPackageContext(theme,
				        Context.CONTEXT_IGNORE_SECURITY);
				app.mLauncherContext.setFriendContext(friendContext);
				
				SettingsValue.setThemeIconBg(friendContext);
				if (iconStyleIndex < -1 
						|| iconStyleIndex >= com.lenovo.launcher2.customizer.Utilities.ICON_STYLE_COUNT) {
					Bitmap[] themeIconBg = SettingsValue.getThemeIconBg();
					if (themeIconBg == null || themeIconBg[0] == null) {
						iconStyleIndex = -1;
					} else {
						iconStyleIndex = SettingsValue.THEME_ICON_BG_INDEX;
					}
	    		}
				
				if (resetWallpaper) {
					InputStream is = null;
					int wallpaperId = friendContext.getResources().getIdentifier(SettingsValue.DEFAULT_WALLPAPER_NAME, "drawable", theme);
					if (wallpaperId == 0) {
						AssetManager assetManager = friendContext.getAssets();					
					    is = assetManager.open(SettingsValue.DEFAULT_WALLPAPER_NAME + ".png");
					} else {
						is = friendContext.getResources().openRawResource(wallpaperId);
					}
					if (is != null) {
						WallpaperManager wm = (WallpaperManager) getSystemService(Context.WALLPAPER_SERVICE);
						wm.setStream(is);
						is.close();
						is = null;
					}
				}

                /*** RK_SEMI_TRANSPARNET  . AUT: zhaoxy . DATE: 2013-05-07. START***/
                int temp = app.mLauncherContext.getIntegerByResName(SettingsValue.APPBG_TRANSLUCENT_ENABLE_CONFIG);
                if (temp == Integer.MIN_VALUE || temp <= 0) {
                    appbgTranslucent_enable = false;
                }
                /*** RK_SEMI_TRANSPARNET  . AUT: zhaoxy . DATE: 2013-05-07. END***/

			} catch (NameNotFoundException e) {
				Debug.
	                printException("Launcher->intAtTheBeginning. Create friendContext error", e);
			} catch (IOException e) {
				Debug.
                printException("Launcher->intAtTheBeginning. get Wallpaper failed", e);
			} catch (NotFoundException e) {
				Debug.
				    printException("Launcher->intAtTheBeginning. Read drawalbe/default_wallpaper.png error", e);
			}
            
    	}
        //final SharedPreferences wallpaper_sp = getSharedPreferences(PREFS_KEY,
       //         Context.MODE_PRIVATE);
      //delete start by liuyg1
//        //add by xingqx when first install innovation ,use special wallpaper
//        if ((resetWallpaper && defaultAndroidTheme.equals(theme))
//                || !wallpaper_sp.getBoolean(SettingsValue.KEY_RESET_SETTINGS, false)) {
//			InputStream is = null;			
//			try {
//				/*RK_VERSION_WW dining 2012-10-25 S*/
//				//此代码实际有效作用于BU版本，在BU版本中才有用到海外版壁纸文件，
//				//因为BU国内版的”石头“壁纸要避免海外的版权问题，所以海外版壁纸需要使用另一张壁纸
//				is = getResources().openRawResource(R.drawable.wallpaper_grass);
//                /*
//				if(getVersionWWEnabled()){
//					InputStream temp = getResources().openRawResource(R.drawable.wallpaper_grass_ww);
//					if(temp != null){
//						is = temp;
//					}
//				}
//				*/
//				/*RK_VERSION_WW dining 2012-10-25 E*/
//				if (is != null) {
//					WallpaperManager wm = (WallpaperManager) getSystemService(Context.WALLPAPER_SERVICE);
//					wm.setStream(is);
//					is.close();
//					is = null;
//				}
//			} catch (IOException e) {
//				Debug.
//                printException("Launcher->intAtTheBeginning. get Wallpaper failed", e);
//			} catch (NotFoundException e) {
//				Debug.printException("Launcher->intAtTheBeginning, wallpaper not found", e);
//			}
//    	}
        //delete end by liuyg1
    	SettingsValue.setIconStyleIndex(iconStyleIndex);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SettingsValue.PREF_ICON_BG_STYLE, iconStyleIndex);

        /*** RK_SEMI_TRANSPARNET  . AUT: zhaoxy . DATE: 2013-05-07. START***/
        editor.putBoolean(SettingsValue.PREF_APPLIST_BGSEMITARNSPARENT, appbgTranslucent_enable);
        /*** RK_SEMI_TRANSPARNET  . AUT: zhaoxy . DATE: 2013-05-07. END***/

        if (theme == null) {
        	mHandler.removeMessages(MSG_SEND_THEME_APPLING);
            mHandler.sendMessage(mHandler.obtainMessage(MSG_SEND_THEME_APPLING, defaultAndroidTheme));
            
            editor.putString(SettingsValue.PREF_THEME, defaultAndroidTheme);
            SettingsValue.setThemeValue(this, defaultAndroidTheme);
        } else {
        	mHandler.removeMessages(MSG_SEND_THEME_APPLING);
            mHandler.sendMessage(mHandler.obtainMessage(MSG_SEND_THEME_APPLING, theme));
            
            editor.putString(SettingsValue.PREF_THEME, theme);
            SettingsValue.setThemeValue(this, theme);
        }
        try {
	        editor.apply();
	    } catch (AbstractMethodError unused) {
	        editor.commit();
	    }
        ShadowUtilites.createGlowingOutline(XLauncher.this);
    }
	
	public void reloadAnIcon(ArrayList<ApplicationInfo> apps, 
			ItemInfo itemInfo, DrawableItem view) {
		boolean hasApp = false;		
        
		if (itemInfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION 
			/*RK_ID:RK_SD_APPS zhangdxa 2013-5-15. S***/
			||itemInfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT	
			/*RK_ID:RK_SD_APPS zhangdxa 2013-5-15. E***/) {
			ShortcutInfo info = (ShortcutInfo) itemInfo;
			final Intent intent = info.intent;
	        final ComponentName name = intent.getComponent();
	        if (!(Intent.ACTION_MAIN.equals(intent.getAction())
	        		|| Intent.ACTION_VIEW.equals(intent.getAction()))|| name == null) {
	        	return;
	        }
	        
        	hasApp = false;
            for (int k = 0; k < apps.size(); k++) {
                ApplicationInfo app = apps.get(k);                           
                if (app.componentName.equals(name)) {
                	refreshAShortcutIcon(view, info, true);
                	hasApp = true;
                	break;
                }                          
            }
            if (!hasApp) {
            	info.setReplaceIcon(null);
            	info.setIcon(null);
            	mIconCache.remove(name);
                applyFromShortcutInfo(view, info);
            }
        } else if (itemInfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT) {
        	ShortcutInfo info = (ShortcutInfo) itemInfo;
        	resetShortcutInfoIcon(info);                    	
        	applyFromShortcutInfo(view, info);
        } else if (itemInfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_FOLDER) {
        	XFolderIcon folderIcon = (XFolderIcon)view;
        	XFolder folder = folderIcon.mFolder; 
        	FolderInfo folderInfo = folderIcon.mInfo;
        	for (ShortcutInfo info : folderInfo.contents) {
                XPagedViewItem item = folder.findPageItemAt(info.screen, 
                		info.cellX, info.cellY);
				if (item == null) {
					continue;
				}
				DrawableItem drawableTarget = item.getDrawingTarget();
				if (drawableTarget == null 
						|| !(drawableTarget instanceof XShortcutIconView)) {
					continue;
				}
				ItemInfo shortInfo = item.getInfo();
				if (shortInfo == null) {
					continue;
				}
				reloadAnIcon(apps, shortInfo, drawableTarget);
        	}
        	folder.changeFolderThemes();
        	folder.invalidate();
        	
        	Bitmap tmp = folderInfo.mReplaceIcon;
        	folderInfo.mReplaceIcon = null;
        	if (tmp != null) {
        		tmp.recycle();
        		tmp = null;
        	}
        	folderIcon.changeFolderIconThemes();
        	folderIcon.invalidate();
        }
	}
	
	private void resetShortcutInfoIcon(ShortcutInfo info) {
    	info.setIcon(null);
    	info.setReplaceIcon(null);
    	Bitmap icon = null;
    	    	
    	mIconCache.remove(info.intent.getComponent());
    	if (info.iconResource != null 
    			&& info.iconResource.packageName != null 
    			&& info.iconResource.resourceName != null) {
        	PackageManager packageManager = mApp.getPackageManager();
        	try {
                Resources resources = packageManager.getResourcesForApplication(info.iconResource.packageName);
                if (resources != null) {
                    final int id = resources.getIdentifier(info.iconResource.resourceName, null, null);
                    /* RK_ID: ICONSTYLE. AUT: liuli1 . DATE: 2012-02-29 . START */
                    icon = mApp.mLauncherContext.getIconBitmap(resources, id, info.iconResource.packageName);
                    /* RK_ID: ICONSTYLE. AUT: liuli1 . DATE: 2012-02-29 . END */
                }
            } catch (Exception e) {
            	icon = mIconCache.getIcon(info.intent);
            	info.usingFallbackIcon = mIconCache.isDefaultIcon(icon);
            }
        	info.customIcon = false;
        	info.setIcon(icon);
    	} else {
    		boolean srcCustom = info.customIcon;
    		info.customIcon = false;
    	    retrieveIcon(info);
    	    info.customIcon = srcCustom;
    	}
    }
    
    private void refreshAShortcutIcon(DrawableItem view, ShortcutInfo info, boolean flag) {
    	info.setReplaceIcon(null);
        info.setIcon(mIconCache.getIcon(info.intent));
        applyFromShortcutInfo(view, info);
//        if(flag)
//        refreshMissNumForThemeChanged(view,info);
//        ((TextView)view).invalidate();
    }
    
    private void applyFromShortcutInfo(DrawableItem view, ShortcutInfo info) {
    	if (view instanceof XIconDrawable) {
    		XIconDrawable xd = (XIconDrawable) view;
    		
    		Bitmap b = info.getIcon(mIconCache, true);
    		int width = (int) view.getWidth();
            int height = (int) view.getHeight();

            /* RK_ID: RK_MEMORY. AUT: liuli1 . DATE: 2013-04-27 . START */
//            Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, width, height, true);
            xd.resize(new RectF(0, 0, width, height));
//            xd.setBackgroundDrawable(new FastBitmapDrawable(scaledBitmap));
            xd.setBackgroundDrawable(new BitmapDrawable(view.getXContext().getResources(), b));
            /* RK_ID: RK_MEMORY. AUT: liuli1 . DATE: 2013-04-27 . END */
            mHotseat.centerItem(info.cellX, width, height);
    	} else {
    		XShortcutIconView siv = (XShortcutIconView)view;
    		siv.applyFromShortcutInfo(info, mIconCache);
    	}
    }

    public XDragLayer getDragLayer() {
        return mDragLayer;
    }
    
    public static int getScreen() {
        synchronized (sLock) {
            return sScreen;
        }
    }

    public static void setScreen(int screen) {
        synchronized (sLock) {
            sScreen = screen;
        }
    }

    /*** RK_ID: fixbug.  AUT: zhaoxy . DATE: 2013-02-18 . START***/
    public void stopEditMode() {
        if (mAppListView != null) {
            mAppListView.stopEditMode();
        }
    }
    /*** RK_ID: fixbug.  AUT: zhaoxy . DATE: 2013-02-18 . END***/

    
    private float mTopFingerBeginX;
    private float mTopFingerBeginY;
    private float mBottomFingerBeginX;
    private float mBottomFingerBeginY;
    private float mTopFingerCurrX;
    private float mTopFingerCurrY;
    private float mBottomFingerCurrX;
    private float mBottomFingerCurrY;
    boolean mGestureInProgress = false;
    
    public boolean processTouchEvent(MotionEvent event) {
    	if(LoadBootPolicy.getInstance(this).getDefaultProfileProcessingState()){
        	mGestureInProgress = false;
            return true;
        }

        if (mLauncherService.mBootFlag) {
            Log.e(TAG, "onTouchEvent-------2488--------------mLauncherService.mBootFlag===="
                    + mLauncherService.mBootFlag);
            mGestureInProgress = false;
            return true;
        }
        
        if (!isWorkspaceNormalState())
        {
        	R5.echo("isWorkspaceNormalState false");
        	mGestureInProgress = false;
            return false;
        }
        
        /** ID: Profile restoring. AUT: chengliang . DATE: 2012.03.22 . S */
        
//        /*add by xingqx for cancel gesture on custom status  2012.12.10  s */
//        if(mWorkspace.getState() == Workspace.State.CUSTOM) {
//            return true;
//        }
//        /*add by xingqx for cancel gesture on custom status  2012.12.10  s */
        if (event.getPointerCount() == 1 && event.getAction() == MotionEvent.ACTION_DOWN )
        {
        	R5.echo("mPointerThanTwo false");
        	mPointerThanTwo = false;
        }        
        
        if (event.getPointerCount() > 2)
        {
        	R5.echo("pointer > 2");
        	mPointerThanTwo = true;
        }
        
        if (event.getPointerCount() >= 2
        		&& mWorkspace.getOpenFolder() == null
        		&& mWorkspace.getPagedView().isTouchable()
        		&& !isFolderAnimating) {
        	
//              /** RK_ID: RK_MAGIC_GESTRUE . AUT: zhanglz1 . DATE: 2012-09-27 S.**/
            int action = event.getAction();
            if (!mGestureInProgress) {
                if (action == MotionEvent.ACTION_POINTER_1_DOWN || action == MotionEvent.ACTION_POINTER_2_DOWN
                        || action == 2) {
                    mBottomFingerBeginX = event.getX(0);
                    mBottomFingerBeginY = event.getY(0);
                    mTopFingerBeginX = event.getX(1);
                    mTopFingerBeginY = event.getY(1);
        			mLastMotionX = event.getX();
        			mLastMotionY = event.getY();
                    R5.echo("mBottomFingerBeginX = " + mBottomFingerBeginX + "mBottomFingerBeginY = " + mBottomFingerBeginY
                            + "mTopFingerBeginX = " + mTopFingerBeginX + "mTopFingerBeginY = " + mTopFingerBeginY);
                    
                    mGestureInProgress = true;
                }
            } else if (action == MotionEvent.ACTION_MOVE) {
                mGestureInProgress = true;
            } else if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP
                    || action == MotionEvent.ACTION_POINTER_1_UP || action == MotionEvent.ACTION_POINTER_2_UP) {
            	
            	R5.echo("ACTION_POINTER_1_UP");
            	if (mMainSurface.getExchangee().getLGestureDetector().mAlwaysInTapRegion)
            	{
            		mMainSurface.getExchangee().getLGestureDetector().cancel();
            	}            	
            	else if (!mMainSurface.getExchangee().getLGestureDetector().isTwoFinger())
            	{
            		R5.echo("isTwoFinger false");
            		mGestureInProgress = false;
                    return false;
            	}
                
            	if(!mPointerThanTwo && !isSameOrientation(event))
                {
                    int xOrientation = getOrientation((int)mTopFingerBeginX, (int)mTopFingerCurrX, (int)mBottomFingerBeginX, (int)mBottomFingerCurrX);
                    int yOrientation = getOrientation((int)mTopFingerBeginY, (int)mTopFingerCurrY, (int)mBottomFingerBeginY, (int)mBottomFingerCurrY);
                    boolean isCenterDirection = ((xOrientation == DIRECTION_OPPOSITE_CENTER) && (yOrientation != DIRECTION_OPPOSITE_OUT))
							|| ((yOrientation == DIRECTION_OPPOSITE_CENTER) && (xOrientation != DIRECTION_OPPOSITE_OUT));
                    
                    R5.echo("xOrientation = " + xOrientation + "yOrientation = " + yOrientation);
                    if (isCenterDirection)
                    {
                    	if(isWorkspaceLocked()) {
    			        	if (!sPendingMsg.contains(PENDING_EDIT_SCREEN)
    			        			&& !sPendingMsg.contains(PENDING_PREVIEW_SCREEN)) {
    			        		sPendingMsg.add(PENDING_PREVIEW_SCREEN);
    			        	}
    			        	mGestureInProgress = false;
    			        	
    			        	return true;
    			        }
	                    R5.echo("showWidgetSnap");
	                    mGestureInProgress = false;
	                    showWidgetSnap(true);
	                    return true;
                    }
                }
            	else if (event.getY() - mLastMotionY > 0)
            	{
            		R5.echo("two finger down");
            	}
            	else if (event.getY() - mLastMotionY < 0)
            	{
            		R5.echo("two finger up");
                }
                
                mGestureInProgress = false;
                return true;
            }
            
            return false;
        }
        
        return false;
    }

    private boolean folderPageMoving() {
        boolean resVal = false;

        if (mWorkspace != null) {
            XFolder folder = mWorkspace.getOpenFolder();
            if (folder != null) {
                resVal = folder.getPagedView().isPageMoving;
            }
        }

        return resVal;
    }
    
    boolean freezingOrientation = false;
    public boolean dispatchTouchEvent(MotionEvent ev) {
    	
		if (SettingsValue.getCurrentMachineType(this) != -1) {
			if (!freezingOrientation) {
				com.lenovo.launcher2.customizer.Utilities.freezingOrientation(this, true);
				freezingOrientation = true;
			}

			if ((MotionEvent.ACTION_UP == ev.getAction() || MotionEvent.ACTION_CANCEL == ev
					.getAction()) && ev.getPointerCount() == 1) {
				com.lenovo.launcher2.customizer.Utilities.freezingOrientation(this, false);
				freezingOrientation = false;
			}}
    	
//    	R5.echo("XLauncher dispatchTouchEvent");
    	//for widget up event no firing purpose
//    	if(ev.getPointerCount() > 1){
//    		XContext.blockAllEvent(true);
//    	}

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
        	XContext.blockAllEvent(false);
            //stopWeatherAnimation();
        }
                
        if (processTouchEvent(ev))
        {
        	ev.setAction(MotionEvent.ACTION_CANCEL);
//            return true;
        }
        
//        if (mBlockEvent)
//        {
//	        int action = ev.getAction();
//	    	if (action == MotionEvent.ACTION_UP
//	                || action == MotionEvent.ACTION_POINTER_1_UP || action == MotionEvent.ACTION_POINTER_2_UP)
//	    	{
//	    		mBlockEvent = false;
//	    		mGestureInProgress = false;
//	    		ev.setAction(MotionEvent.ACTION_CANCEL);
//	    	}
//        }
    	
        return super.dispatchTouchEvent(ev);
    }
    
    /** AUT: zhanglq@bj.cobellink.com DATE: 2012-1-4 start*/
    public void showWidgetSnap(boolean anim) {
        showWidgetSnap(anim, XScreenMngView.State.NORMAL);
    }

    public void showWidgetSnap(boolean anim, XScreenMngView.State state) {
//        Toast.makeText(this, "屏幕管理", Toast.LENGTH_LONG).show();
        showPreviewScreen(anim, state);
        
//        /** AUT: chengliang    FIX BUG: 166026    DATA: 2012.06.14  S*/
//        notifyWeatherAnimation( false );
//        /** AUT: chengliang    FIX BUG: 166026    DATA: 2012.06.14  E*/
//        
//        /** AUT: xingqx xingqx@lenovo.com DATE: 2012-03-15 start*/
//        isonInterceptTouchEventUp =  true;
//        closeFolder();
//        /** AUT: xingqx xingqx@lenovo.com DATE: 2012-03-15 end*/
//        Message msg = showWidgetSnapHandler.obtainMessage(SHOW_PREVIEW_SCREEN, new Boolean(isFromDragEvent));
//        showWidgetSnapHandler.sendMessageDelayed(msg, 130);
//
//        /* RK_ID: RK_QUICKACTION. AUT: liuli1 . DATE: 2012-07-23 . START */
//        dismissQuickActionWindow();
//        /* RK_ID: RK_QUICKACTION. AUT: liuli1 . DATE: 2012-07-23 . END */
        setLauncherWindowStatus(false);
    }
    
    private boolean mAnimScreen = false;
    private boolean mAnimClose = false;
    
    public boolean isAnimCloseScreen() {
    	return mAnimClose;
    }
    
    public boolean isAnimPreviewScreen() {
    	return mAnimScreen;
    }
    private void showPreviewScreen(boolean anim, XScreenMngView.State state){
    	if (mAnimScreen || mAnimClose) {
    		return;
    	}
    	mAnimScreen = true;
    	
    	XFolder folder = mWorkspace.getOpenFolder();
        if (folder != null) {
    		mAnimScreen = false;
    		return;
        }
    	
    	closeFolder();
    	
    	if (!XLauncherModel.isFinishLoad) {
    		mAnimScreen = false;
    		return;
    	}
    	
    	if (mIconPkgDialog != null && mIconPkgDialog.isVisible()) {
    		mAnimScreen = false;
    		return;
    	}
    	
    	if (Float.compare(getDragLayer().getAlpha(), 1.0f) != 0) {
    		mAnimScreen = false;
    		return;
    	}
    	
    	getXLauncherView().bringContentViewToFront();
    	
    	((XWallpaperPagedView) mWorkspace.getPagedView()).enableScrollWhenSetCurrentPage(false);
    	
        /*** fixbug . AUT: zhaoxy . DATE: 2013-03-20 . START ***/
        mMainSurface.getRenderer().getExchangee().ignoreOnceLongpress();
        /*** fixbug . AUT: zhaoxy . DATE: 2013-03-20 . END ***/
        mWorkspace.setWidgetVisible(true);
        
        mScreenMngView.setWorkspaceScale(mWorkspace, mScreenMngView.getCellWidth() - XScreenItemView.getWidthGap(this) * 2, 
                mScreenMngView.getCellHeight() - XScreenItemView.getHeightGap(this));
        ArrayList<Bitmap> snapList = encloseWidgetSnapBitmap(mScreenMngView.getCellWidth() - XScreenItemView.getWidthGap(this) * 2, 
        		mScreenMngView.getCellHeight() - XScreenItemView.getHeightGap(this));
        mLauncherService.mWorkspaceWallpaperScrollX = 0;
      
        if (anim) {
        	enableWidgetCache(true);
        } else {
            mWorkspace.setVisibility(false);
        }
        mHotseat.setVisibility(false);
        
        mWorkspace.setWidgetVisible(false);
          
        mWorkspace.setWorkspaceState( XWorkspace.State.SCR_MGR );
        
        mBlackboard.show(false, anim);
        
        mDragLayer.bringChildToFront(mScreenMngView);
        mScreenMngView.setScreenState(state);
        mScreenMngView.setup(mWorkspace.getCurrentPage(), mDragController, snapList, anim);                
        
        if (mLeosDialog != null && mLeosDialog.isShowing()) {
    		mLeosDialog.dismiss();
    	}
/*** RK_ID: RK_PAD_MENU  AUT: liuyg1. DATE: 2012-05-31 . START***/
        if (mWorkspaceMenuDialog != null && mWorkspaceMenuDialog.isShowing()) {
        	mWorkspaceMenuDialog.dismiss();
        	mWorkspaceMenuDialog = null;
        }
        /*** RK_ID: RK_PAD_MENU  AUT: liuyg1. DATE: 2012-05-31 . END***/
        
        if (anim) {
        	//The time delayed is very important for mngview resize.
            mLauncherHandler.sendEmptyMessageDelayed(MSG_ID_SHOW_MGRVIEW, 50L);
        } else {
        	mScreenMngView.setVisibility(true);
            mScreenMngView.invalidate();
            mAnimScreen = false;
        }
    }
    
    private void enableWidgetCache(boolean visible) {
    	int pageCnt = mWorkspace.getPageCount();
    	if (visible) {
	    	for (int i = 0; i < pageCnt; i++) {
	    		mWorkspace.getPagedView()
	    		    .lockViewContainerDrawingModeToForced(XViewContainer.DRAWING_MODE_CACHE, i);
	    	}
    	} else {
    		for (int i = 0; i < pageCnt; i++) {
	    		mWorkspace.getPagedView().unlockViewContainerDrawingMode(i);
	    	}
    	}
    }

    private void animPreviewScreen() {        
        ValueAnimator inAnim;
        int pointX = 0;
        int pointY = 0;
        int currentPage = 0;
        
        if (mWorkspace == null) {
        	mAnimScreen = false;
        	return;
        }
        
        if (mWorkspace.getCurrentPage() > 8) {
            currentPage = mWorkspace.getCurrentPage() % 9;
        } else {
            currentPage = mWorkspace.getCurrentPage();
        }
        
        final float[] calResult = getPreviewAnimScale();
        
        if (mScreenMngView.isAddState()) {
        	pointX = (int) (mWorkspace.getWidth() / 2);
        } else if (currentPage == 0 || currentPage == 3 || currentPage == 6) {
            pointX = 0;
        } else if (currentPage == 1 || currentPage == 4 || currentPage == 7) {
            pointX = (int) (mWorkspace.getWidth() / 2);
        } else if (currentPage == 2 || currentPage == 5 || currentPage == 8) {
            pointX = (int) mWorkspace.getWidth();
        }
        if (mScreenMngView.isAddState()) {
        	pointY = 0;
        } else if (currentPage <= 2) {
            pointY = 0;
        } else if (currentPage > 2 && currentPage <= 5) {
            pointY = (int) (mWorkspace.getHeight() / 2);
        } else {
            pointY = (int) mWorkspace.getHeight();
        }
        
        final float scaleStart = 1.0f / calResult[0];

        final float middleValue = (scaleStart + 1) / 2.0f;
        final int duration = 500;
        inAnim = ValueAnimator.ofFloat(scaleStart, 1f);
        inAnim.setInterpolator(new QuartInterpolator(QuartInterpolator.OUT));
        inAnim.setDuration(duration);
        final int px = pointX;
        final int py = pointY;
        inAnim.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
            	Float value = (Float) (animation.getAnimatedValue());
            	if (mScreenMngView != null) {
	                Matrix m = mScreenMngView.getMatrix();
	                m.reset();
	                m.setScale(value, value, px, py);
	                mScreenMngView.updateMatrix(m);
	
	                float alpha = (scaleStart - value) / (scaleStart - 1);
	                mScreenMngView.setAlpha(alpha);
            	}
                
            	if (mWorkspace != null) {
	                Matrix workM = mWorkspace.getMatrix();
	                workM.setScale(value / scaleStart, value / scaleStart, px, py);
	                mWorkspace.updateMatrix();
	                mWorkspace.setAlpha(value <= middleValue ? 0 : (value - middleValue) / (scaleStart - middleValue));
            	}
            }
        });

        inAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator arg0) {            	
                if (mScreenMngView != null) {
                	mScreenMngView.setVisibility(true);
                    mScreenMngView.setAlpha(0f);
                }
            }

            @Override
            public void onAnimationRepeat(Animator arg0) {
            }

            @Override
            public void onAnimationEnd(Animator arg0) {
            	if (mScreenMngView != null) {
	                Matrix m = mScreenMngView.getMatrix();
	                m.reset();
	                m.setScale(1.0f, 1.0f, px, py);
	                mScreenMngView.updateMatrix(m);
	                mScreenMngView.setAlpha(1.0f);
            	}
                
            	if (mWorkspace != null) {
	                Matrix workM = mWorkspace.getMatrix();
	                workM.setScale(1.0f, 1.0f, px, py);
	                mWorkspace.updateMatrix();
	                mWorkspace.setAlpha(1.0f);
	                mWorkspace.updateFinalAlpha();
	                mWorkspace.setVisibility(false);
	                enableWidgetCache(false);
            	}
                mAnimScreen = false;
                
                if (mScreenMngView != null) {
                	mScreenMngView.addWidgetContent();
                }
            }

            @Override
            public void onAnimationCancel(Animator arg0) {
            }
        });

        mMainSurface.getRenderer().injectAnimation(inAnim, false);
    }
    
    private void animClosePreview() {
        ValueAnimator inAnim;
        int pointX = 0;
        int pointY = 0;
        int currentPage = 0;
        
        if (mWorkspace.getCurrentPage() > 8) {
            currentPage = mWorkspace.getCurrentPage() % 9;
        } else {
            currentPage = mWorkspace.getCurrentPage();
        }
        
        if (mScreenMngView.isAddState()) {
        	pointX = (int) (mWorkspace.getWidth() / 2);
        } else if (currentPage == 0 || currentPage == 3 || currentPage == 6) {
            pointX = 0;
        } else if (currentPage == 1 || currentPage == 4 || currentPage == 7) {
            pointX = (int) (mWorkspace.getWidth() / 2);
        } else if (currentPage == 2 || currentPage == 5 || currentPage == 8) {
            pointX = (int) mWorkspace.getWidth();
        }
        
        if (mScreenMngView.isAddState()) {
        	pointY = 0;
        } else if (currentPage <= 2) {
            pointY = 0;
        } else if (currentPage > 2 && currentPage <= 5) {
            pointY = (int) (mWorkspace.getHeight() / 2);
        } else {
            pointY = (int) mWorkspace.getHeight();
        }
        
        mScreenMngView.setEmptyThumb();
        final float scaleStart = (mScreenMngView.getCellWidth() - XScreenItemView.getWidthGap(this) * 2) / mWorkspace.getWidth();

//        final float middleValue = (1.0f + scaleStart) / 2.0f;
        final int duration = 400;
        inAnim = ValueAnimator.ofFloat(scaleStart, 1.0f);
        inAnim.setInterpolator(new QuadInterpolator(QuadInterpolator.OUT));
        inAnim.setDuration(duration);
//            mPreviewScreenInAnim.setFillAfter(true);
        final int px = pointX;
        final int py = pointY;
        inAnim.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
//                  Matrix m = mScreenMngView.getMatrix();
//                  m.reset();
                  Float value = (Float) (animation.getAnimatedValue());
//                  m.setScale(value / scaleStart, value / scaleStart, px, py);
//                  mScreenMngView.updateMatrix(m);
//                  mScreenMngView.setAlpha(1 - value);
            	
                if (mWorkspace == null || mWorkspace.getMatrix() == null) {
                	  return;
                  }
            	  Matrix m1 = mWorkspace.getMatrix();
            	  m1.setScale(value, value, px, py);
            	  mWorkspace.updateMatrix();
            }
        });

        inAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator arg0) {
            	if (mWorkspace == null || mScreenMngView == null || mHotseat == null) {
            		return;
            	}
            	actOpenWorkspace();   
            	mScreenMngView.setVisibility(false);
            	
            	Matrix m = mWorkspace.getMatrix();
            	m.setScale(scaleStart, scaleStart, px, py);
            	mWorkspace.updateMatrix();
            	mWorkspace.setTouchable(false);
            	mWorkspace.getPagedView().generateBitmapCacheAll();
            	mHotseat.setAlpha(0f);
            	mWorkspace.invalidate();
                mHotseat.invalidate();
            }

            @Override
            public void onAnimationRepeat(Animator arg0) {
            }

            @Override
            public void onAnimationEnd(Animator arg0) {
//            	mScreenMngView.setVisibility(false);
//            	Matrix m = mScreenMngView.getMatrix();
//                m.reset();
//                m.setScale(1.0f, 1.0f, px, py);
//                mScreenMngView.updateMatrix(m);
//                mScreenMngView.setAlpha(1);
            	if (mWorkspace == null || mScreenMngView == null || mHotseat == null) {
            		return;
            	}
                
            	Matrix m1 = mWorkspace.getMatrix();
            	m1.setScale(1.0f, 1.0f, px, py);
            	mWorkspace.updateMatrix();
            	mWorkspace.getPagedView().resetTouchBounds();
            	mWorkspace.getPagedView().desireTouchEvent(false);
                
            	mWorkspace.setTouchable(true);
            	mWorkspace.setVisibility(true);
                mWorkspace.setAlpha(1f);
            	mHotseat.setAlpha(1f);
            	mWorkspace.invalidate();
                mHotseat.invalidate();
                
                actClosePreview();
                mAnimClose = false;
            }

            @Override
            public void onAnimationCancel(Animator arg0) {
            }
        });

        mMainSurface.getRenderer().injectAnimation(inAnim, false);
    }


    public ArrayList<Bitmap> encloseWidgetSnapBitmap(final float previewScreenWidth,final float previewScreenHeight) {
//        mWorkspace.setWidgetVisible(true);
    	ArrayList<Bitmap> list = new ArrayList<Bitmap>();
        
        //Integer snapLayout_width = getResources().getDimensionPixelOffset(R.dimen.snapLayout_width);
        //Integer snapLayout_height = getResources().getDimensionPixelOffset(R.dimen.snapLayout_height);
        //int mCurrentOrientation = getResources().getConfiguration().orientation;
        int end = mWorkspace.getPagedView().getPageCount();
        
        for (int i = 0; i < end; i++) {
//                  CellLayout cell = (CellLayout) mWorkspace.getChildAt(i);

          //if (mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT) {
              
              int height = mScreenMngView.getHomeHeight()/* + marginBottom * 2*/;
              int extTop = getResources().getDimensionPixelOffset(R.dimen.xscreen_mng_thumbnail_margin_top);              
              Bitmap bitmap = getSnapBitmap(previewScreenWidth, previewScreenHeight - height - extTop, i, height, extTop);
//              BitmapDrawable drawable = (BitmapDrawable)getResources().getDrawable(R.drawable.add_lotus);  
//              Bitmap bitmap = drawable.getBitmap();            
//                      cell.dispatchDraw(c);                
              
              
              list.add(bitmap);
         /* } else if (mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
              Bitmap bitmap = Bitmap.createBitmap(snapLayout_width, snapLayout_height, Bitmap.Config.ARGB_8888);
              Canvas c = new Canvas(bitmap);
              c.scale((float) 0.25, (float) 0.24, (float) -4.0, (float) -17.0);
//                      cell.dispatchDraw(c);
              

              
              list.add(bitmap);
          }*/
      }
      
//      mWorkspace.setWidgetVisible(false);     
     list.add(Bitmap.createBitmap((int)previewScreenWidth, (int)previewScreenHeight, Bitmap.Config.ARGB_8888)); 
     return list;

    }
    /** AUT: zhanglq@bj.cobellink.com DATE: 2012-1-4 end*/
        
    public LauncherService getLauncherService() {
    	return mLauncherService;
    }
    
    public void closePreviewScreen() {    	
    	mWindowState = false;
    	closePreviewScreen(true);
    }
    
    private void actOpenWorkspace() {
    	if (mWorkspace == null
    			|| mHotseat == null
    			|| mBlackboard == null
    			|| mMainSurface == null
    			|| mDragController == null) {
    		return;
    	}
    	mWorkspace.setWidgetVisible(true);
        mWorkspace.setVisibility(true);
        mHotseat.setVisibility(true);
        
        mWorkspace.getPagedView().resetAnim();
        mWorkspace.setWorkspaceState( XWorkspace.State.NORMAL );
        if( mDragController.isDragging() ){
        	mMainSurface.bringContentViewToFront();
        }else{
        	mWorkspace.getPagedView().bringStageToFront();
        }
        mBlackboard.hide();
        ((XWallpaperPagedView) mWorkspace.getPagedView()).enableScrollWhenSetCurrentPage(true);
    }
    
    private void actClosePreview() {
    	mScreenMngView.setVisibility(false);

//     mLauncherService.bSnapShowFlag = true;
       clearSnapBitmaps();
    	mScreenMngView.clean();
    	if(mBlackboard != null){
    		mBlackboard.hide();
    		//mBlackboard.setVisibility( false );
    	}
    }
    
    public void closePreviewScreen(boolean anim) { 
    	if (mAnimScreen || mAnimClose) {
    		return;
    	}
    	mAnimClose = true;
    	
        if (anim) {
        	mLauncherHandler.removeMessages(MSG_ID_CLOSE_MGRVIEW);
            mLauncherHandler.sendEmptyMessageDelayed(MSG_ID_CLOSE_MGRVIEW, 50);
            mBlackboard.hide();
	    } else {
	    	actClosePreview();
	    	actOpenWorkspace();
	    	mWorkspace.invalidate();
	        mHotseat.invalidate();
	        mBlackboard.hide();
	        mAnimClose = false; 
	    }
    	/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-3-26 S*/
    	 Reaper.processReaper( this, 
            	   Reaper.REAPER_EVENT_CATEGORY_SCREEN, 
    			   Reaper.REAPER_EVENT_ACTION_SCREEN_SCREENCOUNT,
    			   String.valueOf(mWorkspace.getDefaultPage()+1),
    			   mWorkspace.getChildCount());
		/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-3-26 E*/
    }

    public void removeFolder(FolderInfo folder) {
        sFolders.remove(folder.id);
        //add by zhanggx1 for reordering.s
        autoReorder();
        //add by zhanggx1 for reordering.e
    }

    public void addFolder(FolderInfo folder) {
        sFolders.put(folder.id, folder);
    }

    public void updateFolder(FolderInfo folder) {
        sFolders.put(folder.id, folder);
    }

    public void clearSnapBitmaps() {
    }
    
    public Bitmap getSnapBitmap(float previewScreenWidth, float previewScreenHeight, int page, int extHeight, int extTop)
    {        
        Bitmap bitmap = Bitmap.createBitmap((int)previewScreenWidth, (int)(previewScreenHeight + extHeight + extTop), Bitmap.Config.ARGB_8888);
        
        float newHeight = mWorkspace.getHeight() + mWorkspace.getTop();
    //    Canvas c = new Canvas(bitmap);
        float scaleW = previewScreenWidth / (mWorkspace.getWidth() * 1.0f);
        float scaleH = previewScreenHeight / (newHeight * 1.0f);
    //    c.scale(scaleW, scaleH);
                
        IDisplayProcess tmpProc = new NormalDisplayProcess();
        
        tmpProc.beginDisplay(bitmap);
        
        float scale = Math.min(scaleW, scaleH);
        
        float tranX = (previewScreenWidth - scale * mWorkspace.getWidth())/2;
//        float tranY = (scaleH - scale) * mWorkspace.getHeight() /2 + mWorkspace.getTop()*scale + extTop;
        float tranY = extTop;
        R5.echo("tranX = " + tranX);
        R5.echo("tranY = " + tranY);

//        tmpProc.getCanvas().scale(scale, scale);
        
        tmpProc.getCanvas().translate((int)tranX, (int)tranY);
        
        tmpProc.getCanvas().scale(scale, scale);
        
        mWorkspace.draw(tmpProc, page);
        tmpProc.endDisplay();
        return bitmap;
    }
    
    /* RK_ID: RK_LELAUNCHER_REAPER_INIT. AUT: zhangdxa DATE: 2013-03-04 S*/
    private void initReaper(){
    	Log.i("Reaper","XLauncher.initReaper()");
		SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean bTagReaper = mSharedPreferences.getBoolean(SettingsValue.PREF_REAPER, true);
        if( bTagReaper ) {
        	Reaper.processReaperInitForce(this);
        }else if( Reaper.reaperNetworkEnable(this) ){
        	Reaper.processReaperInitCmccForce(this);
        }
    }
    /* RK_ID: RK_LELAUNCHER_REAPER_INIT. AUT: zhangdxa DATE: 2013-03-04 E*/
    
    /* RK_ID: RK_LELAUNCHER_VERSION_UPDATE. AUT: zhangdxa DATE: 2013-03-04 S*/
    private static final int MSG_AUTO_UPDATE = 1000;
	Handler mInitHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_AUTO_UPDATE: {
            	Log.i("VersionUpdate","XLauncher, call startAutoVersionUpdate");
            	VersionUpdateSUS.getInstance().startAutoVersionUpdate();
            }
            break;
            default:
            	break;
            }
        }
    };
    private void initAutoVersionUpdate(){
    	Log.i("VersionUpdate","XLauncher.initAutoVersionUpdate()");
    	VersionUpdateSUS.getInstance().initVersionUpdate(XLauncher.this);
    	boolean bAutoOn = VersionUpdateSUS.getInstance().getAutoUpdateFromPrefs();
    	Log.i("VersionUpdate","XLauncher.initAutoVersionUpdate()---autoOn:"+ bAutoOn);
	    if( bAutoOn ) {
	    	mInitHandler.removeMessages(MSG_AUTO_UPDATE);
	    	mInitHandler.sendEmptyMessage(MSG_AUTO_UPDATE);
	    }
    }
    /* RK_ID: RK_LELAUNCHER_VERSION_UPDATE. AUT: zhangdxa DATE: 2013-03-04 E*/

    public interface LauncherTransitionable {
	    boolean onLauncherTransitionStart(XLauncher l, Animator animation, boolean toWorkspace);
	    void onLauncherTransitionEnd(XLauncher l, Animator animation, boolean toWorkspace);
	}
    
    public FolderInfo addFolder(long container, int screen, int cellX, int cellY) {
        final FolderInfo folderInfo = new FolderInfo();
        /*** fixbug 9925  . AUT: zhaoxy . DATE: 2013-03-27. START***/
        folderInfo.title = "";
        /*** fixbug 9925  . AUT: zhaoxy . DATE: 2013-03-27. END***/

        // Update the model
        folderInfo.screen = screen;
        folderInfo.cellX = cellX;
        folderInfo.cellY = cellY;
        XLauncherModel.addItemToDatabase(this, folderInfo, container, screen, folderInfo.cellX,
                folderInfo.cellY, false);
        sFolders.put(folderInfo.id, folderInfo);
        
        if (container == LauncherSettings.Favorites.CONTAINER_DESKTOP)
        {
            mWorkspace.addInScreen(folderInfo);
        }
        else if (container == LauncherSettings.Favorites.CONTAINER_HOTSEAT)
        {            
            mHotseat.addInHotseat(folderInfo);
        }
        
        return folderInfo;
    }
    
    private boolean hasFocus = true;
    public boolean hasFocus(){
    	return hasFocus;
    }
    public void onWindowFocusChanged(boolean hasFocus) {
  /** ID: stop widget service. AUT: kangwei3 . DATE: 2013.03.20 . s */
		super.onWindowFocusChanged(hasFocus);
		
		this.hasFocus = hasFocus;
		
		//fix bug 空灵触控开启后，拖动桌面/屏幕管理/文件夹的内容用力按压屏幕后松手，拖动物冻住
		if (mDragController != null) {
			mDragController.cancelDrag();
		}
		
		if(LoadBootPolicy.getInstance(XLauncher.this).getDefaultProfileProcessingState()){
			return;
		}
		
		if (mWorkspace != null && mWorkspace.getPagedView() != null) {
			mWorkspace.getPagedView().onTouchCancel( null );
			
			XFolder folder = mWorkspace.getOpenFolder();
			if (folder != null) {
				folder.onTouchCancel( null );
			}
		}
		
		//fix bug 20459 by zhanggx1 on 2013-08-08
		if (mScreenMngView != null) {
			mScreenMngView.onTouchCancel( null );
		}

		if( !hasFocus){
			mLauncherHandler.removeMessages(MSG_ID_ONRESUME);
			return;
		}
		if (hasFocus&&!mPaused&&!mWaitingForResult) {
			boolean state = WeatherUtilites.getAnimaState(XLauncher.this);
			Log.d("ac","state="+state);
			if(state){
				mLauncherHandler.sendMessageDelayed(mLauncherHandler.obtainMessage(MSG_ID_ONRESUME),
					0);
			}
		}
		
/*		if(hasFocus){
			*//** ID: stop widget service. AUT: kangwei3 . DATE: 2013.01.16 . E *//*
	        new Thread(){
	        	public void run(){
	            	if(WeatherUtilites.hasInstances(XLauncher.this,WeatherUtilites.THIS_WEATHER_WIDGET))
	            	{
		            	Intent intent = new Intent(WeatherUtilites.ACTION_WEATHER_WIDGET_SEVICE_RESTART);
		            	startService(intent.setClass(XLauncher.this,WidgetService.class));
	            	}else if(WeatherUtilites.hasInstances(XLauncher.this,GadgetUtilities.TASKMANAGERWIDGETVIEWHELPER)){
	                	Intent intent = new Intent(WeatherUtilites.ACTION_ADD_TASKMANAGER_WIDGET);
	                	startService(intent.setClass(XLauncher.this,WidgetService.class));
	            	}
	        	}
	        }.start();
		}*/
		
    }
	
	private static final int MSG_ID_ONPAUSE = 1;
	private static final int MSG_ID_SETINIT_ANIMA = 2;
	public static final int MSG_ID_SHOW_RECENT = 3;
	public static final int MSG_ID_SHOW_MGRVIEW = 4;
	private static final int MSG_ID_ONRESUME = 5;
	private static final int MSG_ID_RESTART_WEATHER_SERVICE = 6;
	private static final int MSG_ID_CLOSE_MGRVIEW = 7;
	
    private static final String WIDGET_EXTRA_CONTROL_KEY_WEATHER = "com.lenovo.leos.widgets.weather.WeatherWidgetProvider";
    private static final String [] WIDGET_WEATHER_PROVIDER_CLASSENAME= {
    	"com.lenovo.launcher2.weather.widget.WeatherWidgeMagictView",
    	"com.lenovo.launcher2.weather.widget.WeatherWidgetView"};

	private Handler mLauncherHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			
			if(LoadBootPolicy.getInstance(XLauncher.this).getDefaultProfileProcessingState()){
				return;
			}
			switch (msg.what) {
			case MSG_ID_ONRESUME:
				notifyWeatherAnimation(true);
				break;
			case MSG_ID_ONPAUSE:
				notifyWeatherAnimation(false);
				/* RK_ID: RK_ANIM_WEATHER . AUT: KANGWEI3 . DATE: 2013-03-27 . S */
	    	    WeatherUtilites.setAnimaState(XLauncher.this, true);
	    	    /* RK_ID: RK_ANIM_WEATHER . AUT: KANGWEI3 . DATE: 2013-03-27 . E */
				break;
			case MSG_ID_SETINIT_ANIMA:
				WeatherUtilites.setAnimaState(XLauncher.this, false);
				break;
			case MSG_ID_SHOW_RECENT:
			    doubleClick();
			    break;
			case MSG_ID_SHOW_MGRVIEW:
			    animPreviewScreen();
                break;
            case MSG_ID_RESTART_WEATHER_SERVICE:
                new DownloadFilesTask().execute("");
                break;
            case MSG_ID_CLOSE_MGRVIEW:
            	animClosePreview();
            	break;
			default:
				break;
			}
		}
	}; 
	
    private void notifyWeatherAnimation(boolean resume) {
        if (resume && mWorkspace != null && mState == State.WORKSPACE) {
            startWeatherAnimation();
        } else if (!resume) {
            //stopWeatherAnimation();
            removeToggleExWindow();
        }
    }
    
    

    @Override
    protected void onPause() {
        super.onPause();
        onPauseFlag = true;
        mCurOrientation =  getCurrentOrientation();
		if(LoadBootPolicy.getInstance(XLauncher.this).getDefaultProfileProcessingState()){
			return;
		}
//		

		mPaused = true;
        mLauncherHandler.removeMessages(MSG_ID_ONPAUSE);
        mLauncherHandler.sendMessageDelayed(mLauncherHandler.obtainMessage(MSG_ID_ONPAUSE), 0);
       /* new Thread(){
        	public void run(){
//            	if(WeatherUtilites.hasInstances(XLauncher.this,WeatherUtilites.THIS_WEATHER_WIDGET)||
//            			WeatherUtilites.hasInstances(XLauncher.this,GadgetUtilities.TASKMANAGERWIDGETVIEWHELPER))
//            	{
	            	Intent i = new Intent();
                	stopService(i.setClass(XLauncher.this,WidgetService.class));
//            	}
        	}
        }.start();*/
        
//        if (mScreenMngView != null && mScreenMngView.isVisible()) {
//        	closePreviewScreen();            	
//        }
        
//        getXLauncherView().getRenderer().getEventHandler().post(mCancelRunnable);
        if (mDragController != null && mDragController.isDragging()) {
            mDragController.cancelDrag();
        }
        
        mMainSurface.getRenderer().invalidate();        
    }
    
//    private Runnable mCancelRunnable = new Runnable(){
//
//        @Override
//        public void run() {
//            if (mDragController != null && mDragController.isDragging()) {
//                mDragController.cancelDrag();
//            }
//        }
//        
//    };
    
    private void startWeatherAnimation() {
    	if(mWorkspace.getState()!=XWorkspace.State.NORMAL)
    		return;
    	if(checkSpecialWidgetUpdate(WIDGET_EXTRA_CONTROL_KEY_WEATHER)){
            if(getweatheranimsettingenabled()){
	            Intent intent = new Intent("com.lenovo.leos.widgets.weather.start");
	            this.sendBroadcast(intent);
            }
        }else {
        	/*
        	CellLayout layout = (CellLayout) mWorkspace.getChildAt(mWorkspace.getCurrentPage());
        	ArrayList<WeatherWidgetPosInfo> weatherList = checkSpecialWidgetUpdate();
        	final int size = weatherList.size();
        	if(size>0){
	            R2.echo("!!send com.lenovo.launcher.widgets.weather.start");
	            int celly = -1;
	            Intent intent = new Intent("com.lenovo.launcher.widgets.weather.start");
	            this.sendBroadcast(intent);
	            if(size>1){
	            	celly = Math.min( weatherList.get(0).mposy, weatherList.get(size-1).mposy);
	            }else
	            	celly = weatherList.get(size-1).mposy;
	            View view = layout.getChildAt(0, celly);
	            DragLayer parent = (DragLayer) findViewById(R.id.drag_layer);
	            if(view!=null){
	            	Rect frame = new Rect();  
	            	getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);  
	            	Rect temprect = new Rect();
		            parent.getDescendantRectRelativeToSelf(view, temprect);
	        		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
	        		Date now = calendar.getTime();
	    			final String newtime = DateFormat.format("yyyy-MM-dd", now).toString();
	    			final String oldtime = WeatherUtilites.getCurrentUpdate(this);
	    			final String tempicon = WeatherUtilites.getCurrentIcon(this);
					if(newtime.equals(oldtime))
						getScreenAnimation(this,WeatherUtilites.ACTION_WEATHER_SCREEN_ANIMATE_START,view.getTop()+frame.top,tempicon);
	            }
        	}else{*/
        		if(checkSpecialWidget()){
        			Intent intent = new Intent("com.lenovo.launcher.widgets.weather.start");
    	            this.sendBroadcast(intent);
    	            getScreenAnimation(this,WeatherUtilites.ACTION_WEATHER_SCREEN_ANIMATE_START);
        		}
//        	}
        }
    }
    private void getScreenAnimation(final Context context,String action)
    {
    	if(getweatheranimsettingenabled()){
			Intent intent = new Intent(action);
			intent.setPackage("com.lenovo.leos.weatheranimation");
			intent.setClassName("com.lenovo.leos.weatheranimation", "com.lenovo.leos.weatheranimation.WeatherAnimService");
			PackageManager mPackageManager = context.getPackageManager();
			List<ResolveInfo> list = mPackageManager.queryIntentServices(
			        intent, 0);
			if (list.size()>0) {
				try{
					context.startService(intent);
				}catch(SecurityException e){
					e.printStackTrace();
				}
			}
    	}
    }
	private static final String PREF_WEATHER_ANIM_SETTING = "pref_weather_anim_setting";
    public boolean getweatheranimsettingenabled() {
        SharedPreferences sharedPreferences = getSharedPreferences(SettingsValue.PERFERENCE_NAME,
                                                MODE_APPEND | MODE_MULTI_PROCESS );
        return sharedPreferences.getBoolean(PREF_WEATHER_ANIM_SETTING, true);
    }
    private void getScreenAnimation(final Context context,String action,int celly,String icon)
    {
    	if(!getweatheranimsettingenabled())
    		return;
    	Log.d("ad","icon="+icon);
    	Intent intent = new Intent();
		intent.setPackage("com.lenovo.leos.weatheranimation");
		intent.setClassName("com.lenovo.leos.weatheranimation", "com.lenovo.leos.weatheranimation.WeatherAnimService");
    	if(icon!=null&&!TextUtils.isEmpty(icon)){
			intent.setAction(action);
    	}else{
    		if(WeatherUtilites.ACTION_WEATHER_SCREEN_ANIMATE_START.equals(action))
    			intent.setAction(WeatherUtilites.ACTION_WEATHER_SCREEN_ANIMATE_STOP);
    		else
    			intent.setAction(action);
    	}
		intent.putExtra("celly", celly);
		intent.putExtra("image_src", icon);
		intent.putExtra("navigationbar", WeatherUtilites.getNavigationBarHeight(context));
		try{
			context.startService(intent);
		}catch(SecurityException e){
			e.printStackTrace();
		}
    }
    private void removeToggleExWindow() {
        R2.echo("!!send com.lenovo.leos.toggle.remove");
        Intent intent = new Intent("com.lenovo.leos.toggle.remove");
        this.sendBroadcast(intent);
    }
    ArrayList<WeatherWidgetPosInfo> checkSpecialWidgetUpdate() {
    	ArrayList<WeatherWidgetPosInfo> weatherList = new ArrayList<WeatherWidgetPosInfo>();
    	if( mWorkspace == null ){
    		R2.echo("WOW ! Workspace has gone..");
	    	return weatherList;
    	}
        int index = mWorkspace.getCurrentPage();
       /*RK_ID: RK_LEOSWIDGET AUT:kangwei DATE: 2012-10-17 START */
        ArrayList<WeatherWidgetPosInfo> appwidgetidList = XLauncherModel
                .getLeosWidgetIdByScreen(this, index);
        /*RK_ID: RK_LEOSWIDGET AUT:kangwei DATE: 2012-10-17 E */
        if( appwidgetidList == null || appwidgetidList.isEmpty() ){
        	return weatherList;
        }
        int size = appwidgetidList.size();
        try {
			for (int i = 0; i < size; i++) {
			    String idname = appwidgetidList.get(i).mwidgetname;
			    Log.d("ad","checkSpecialWidgetUpdate idname="+idname);
			    if(GadgetUtilities.WEATHERMAGICWIDGETVIEWHELPER.equals(idname)){
			    	weatherList.add(appwidgetidList.get(i));
			    }
			}
		} catch (Exception e) {
			return weatherList;
		}
        return weatherList;
    }
    boolean checkSpecialWidgetUpdate( String key) {
  	   if( mWorkspace == null ){
	      R2.echo("WOW ! Workspace has gone..");
 		 return false;
 	   }
     int index = mWorkspace.getCurrentPage();
     ArrayList<Integer> appwidgetidList = XLauncherModel
             .getAppWidgetIdByScreen(this, index);
     
     if( appwidgetidList == null || appwidgetidList.isEmpty() ){
     	return false;
     }
     
     int size = appwidgetidList.size();
     try {
			for (int i = 0; i < size; i++) {
			    int id = appwidgetidList.get(i);

			    AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager
			            .getAppWidgetInfo(id);

			    if (appWidgetInfo == null || appWidgetInfo.provider == null)
			        continue;

			    ComponentName componentName = appWidgetInfo.provider;
			    if (componentName.getClassName().equals( key )) {
			        return true;
			    }
			    
			}
		} catch (Exception e) {
			return false;
		}
     
     return false;
 }
    boolean checkSpecialWidget() {
//    	ArrayList<WeatherWidgetPosInfo> weatherList = new ArrayList<WeatherWidgetPosInfo>();
    	if( mWorkspace == null ){
    		R2.echo("WOW ! Workspace has gone..");
	    	return false;
    	}
        int index = mWorkspace.getCurrentPage();
       /*RK_ID: RK_LEOSWIDGET AUT:kangwei DATE: 2012-10-17 START */
        ArrayList<WeatherWidgetPosInfo> appwidgetidList = XLauncherModel
                .getLeosWidgetIdByScreen(this, index);
        /*RK_ID: RK_LEOSWIDGET AUT:kangwei DATE: 2012-10-17 E */
        if( appwidgetidList == null || appwidgetidList.isEmpty() ){
        	return false;
        }
       
        int size = appwidgetidList.size();
        Log.d("ad","checkSpecialWidgetUpdate size="+size);
        try {
			for (int i = 0; i < size; i++) {
			    String idname = appwidgetidList.get(i).mwidgetname;
			    if(idname.contains(WeatherUtilites.THIS_WEATHER_WIDGET)){
			    	Log.d("ad","checkSpecialWidgetUpdate idname="+idname);
			    	return true;
			    }
			}
		} catch (Exception e) {
			return false;
		}
        return false;
    }
  /** ID: stop widget service. AUT: kangwei3 . DATE: 2013.03.20 . e */

    /*** RK_ID: APPS_CATEGORY. AUT: zhaoxy . DATE: 2013-03-21 . START ***/
    class XAppsClassifiction extends AppsClassifiction<XFolderIcon> {

        private int  mShowScreenIndex = mWorkspace.getDefaultPage();
        private int MAX_SCREEN = SettingsValue.getLauncherScreenMaxCount(XLauncher.this);
        
        public XAppsClassifiction(Context c) {
            super(c);
        }
        
        public XAppsClassifiction(Context c,AppsClassificationData data){
            super(c,data);
        }

        public int getSnapToScreenIndex(){
            return mShowScreenIndex;
        }
        
        @Override
        public int getWrokSpaceCellCount() {
            return mWorkspace.getCellCountPreScreen();
        }

        @Override
        public int getWorkSpaceScreenNums() {
            return mWorkspace.getPageCount();
        }

        @Override
        public int findEngouhSapce(int needCount) {
            final int screenCount = mWorkspace.getPageCount();
            int screen = -1;
            for(int i = screenCount-1;i>=0;i--){
                int count = mWorkspace.getPagedView().findVacantCellNumber(i).size();
                if(needCount <= count){
                    screen = i;
                    break;
                }
            }
            
            return screen;
        }

        @Override
        public int addNewScreen(int addScreenNums) {
            int countToAdd = MAX_SCREEN - mWorkspace.getPageCount();
            countToAdd = Math.min(addScreenNums, countToAdd);
            for (int i = 0; i < countToAdd; i++) {
                mWorkspace.addNewScreen();
            }
            int pageCnt = mWorkspace.getPagedView().getPageCount();
            SharedPreferences preferrences = getSharedPreferences(
                    CELLLAYOUT_COUNT, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferrences.edit();
            editor.putInt(CELLLAYOUT_COUNT, pageCnt);
            editor.commit();

            mLauncherService.mScreenCount = pageCnt;
            return countToAdd;
        }

        @Override
        public XFolderIcon findContainerByName(String classificationName) {
            XFolderIcon f1 = mWorkspace.getFolderIconByTitle(classificationName);
            return f1 == null ? mHotseat.getFolderIconByTitle(classificationName):f1;
        }

        @Override
        public void addCategoryAppsToContainer(XFolderIcon folderIcon, List<String> intentUris) {
            if (folderIcon != null) {
                int success = 0;
                for (int i = 0; i < intentUris.size(); i++) {
                    Intent cellintent = null;
                    try {
                        cellintent = Intent.parseUri(intentUris.get(i), 0);
                    } catch (URISyntaxException e) {
                        cellintent = new Intent();
                        e.printStackTrace();
                    }
                    final ShortcutInfo info = mModel.getShortcutInfo(getPackageManager(), cellintent, mContext);
                    if (info != null) {
                        info.setActivity(cellintent.getComponent(), Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                        info.container = ItemInfo.NO_ID;

                        info.cellX = -1;
                        info.cellY = -1;
                        info.screen = -1;
//                        info.mNewAdd = 1;

                        if (folderIcon.acceptDrop(info)) {
                            folderIcon.onDrop(info);
                            ++success;
                        }
                    }
                }
                mShowScreenIndex = folderIcon.mInfo.screen;
                folderIcon.ShowNewAddedAppsNumber(success);
            }
        }

        @Override
        public void createContainerAndAddCateoryApps(String folderName, List<String> intentUris, int screen) {
            if (screen < 0 || screen >= mWorkspace.getPageCount() || intentUris == null || intentUris.isEmpty()) {
                return;
            }
            mEmptyPointList = mWorkspace.getPagedView().findVacantCellNumber(screen);
            int container = LauncherSettings.Favorites.CONTAINER_DESKTOP;
            final FolderInfo folderInfo = new FolderInfo();
            folderInfo.title = (folderName == null) ? getText(R.string.folder_name) : folderName;

            // Update the model
            folderInfo.screen = screen;
            folderInfo.cellX = mEmptyPointList.get(0).x;
            folderInfo.cellY = mEmptyPointList.get(0).y;
            XLauncherModel.addItemToDatabase(mContext, folderInfo, container, screen, folderInfo.cellX, folderInfo.cellY, false);
            sFolders.put(folderInfo.id, folderInfo);
            
            XPagedViewItem toAdd = mWorkspace.addInScreen(folderInfo);
            
            for (int i = 0; i < intentUris.size(); i++) {
                Intent cellintent = null;
                try {
                    cellintent = Intent.parseUri(intentUris.get(i), 0);
                } catch (URISyntaxException e) {
                    cellintent = new Intent();
                    e.printStackTrace();
                }
                final ShortcutInfo info = mModel.getShortcutInfo(getPackageManager(), cellintent, mContext);
                if (info != null) {
                    info.setActivity(cellintent.getComponent(), Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    info.container = ItemInfo.NO_ID;

                    info.cellX = -1;
                    info.cellY = -1;
                    info.screen = -1;
                }
                folderInfo.add(info);
            }
            mShowScreenIndex = screen;
            if (toAdd != null && toAdd.getDrawingTarget() != null) {
                XFolderIcon folderIcon = (XFolderIcon) toAdd.getDrawingTarget();
                folderIcon.ShowNewAddedAppsNumber(intentUris.size());
            }
        }
    }
    /*** RK_ID: APPS_CATEGORY. AUT: zhaoxy . DATE: 2013-03-21 . END ***/
    /*** RK_ID: APPS_CATEGORY. AUT: GECN1 . DATE: 2013-03-21 . BGGAIN ***/
    public void classifyAppsWithBehavior(){
        final XAppsClassifiction x = new XAppsClassifiction(this);
        AppCategoryBehavior behavior = new AppCategoryBehavior(){
            
            private LeProcessDialog mClassificationProgressDlg;
            @Override
            public void beforeShowBehavior() {
                showClassificationDialog();
                
            }

            @Override
            public void afterShowBehavior() {
                if(mClassificationProgressDlg!=null && mClassificationProgressDlg.isShowing()){
                    mClassificationProgressDlg.dismiss();
                }
                mWorkspace.setCurrentPage(x.getSnapToScreenIndex());
                
            }
            
            private void showClassificationDialog(){
                mClassificationProgressDlg = new LeProcessDialog(XLauncher.this);
//                LinearLayout layout = (LinearLayout) LayoutInflater.from(XLauncher.this).inflate(R.layout.apps_category_progressbar, null);
//                mClassificationProgressDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                mClassificationProgressDlg.setContentView(layout);
//                Window window = mClassificationProgressDlg.getWindow();
//                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//                window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//                window.setGravity(Gravity.CENTER);
                if(mClassificationProgressDlg != null){
                	mClassificationProgressDlg.setLeMessage(R.string.app_categroy);
                    mClassificationProgressDlg.show();
                }
            }
            
        };
        x.setAppCategoryBehavior(behavior);
        x.show();
    }
    /*** RK_ID: APPS_CATEGORY. AUT: GECN1 . DATE: 2013-03-21 . END ***/


    public void updateWorkspaceThumb(int screen) {
        if (mAppListView != null) {
            mAppListView.updateWorkspaceThumb(screen);
        }
    }
    
    /** RK_ID: RK_BUG_174814. AUT: zhangdxa DATE: 2013-3-28 S */
    private void reloadThemeIfPowerOff(){
        SharedPreferences pref = getSharedPreferences("theme_apply", Context.MODE_PRIVATE);
        String packageName = pref.getString(SettingsValue.PREF_THEME, null);
        if( packageName != null && !packageName.isEmpty()){
        	handleTheme(packageName, false, "theme_apply");
//        	Context friendContext = null;
//        	try {
//				friendContext = createPackageContext(packageName,
//				        Context.CONTEXT_IGNORE_SECURITY);
//			} catch (NameNotFoundException e2) {
//				e2.printStackTrace();
//			}
//    		if (friendContext != null) {
//    			new ApplyThemeTask().execute(new Object[]{packageName, friendContext,"theme_apply"});
//    		}
        	SharedPreferences.Editor editor = getSharedPreferences("theme_apply", Context.MODE_PRIVATE).edit();
		    editor.putString(SettingsValue.PREF_THEME, null);
	        editor.commit();
        }
    }
    /** RK_ID: RK_BUG_174814. AUT: zhangdxa DATE: 2013-3-28 E */
        
	public class ThemePkgChangedReceiver extends BroadcastReceiver {
    	
    	@Override
    	public void onReceive(Context context, Intent intent) {
    		
    		/** ID: Profile restoring. AUT: chengliang . DATE: 2012.03.22 . S */
    		if(LoadBootPolicy.getInstance(XLauncher.this).getDefaultProfileProcessingState()){
    			return;
    		}
    		/** ID: Profile restoring. AUT: chengliang . DATE: 2012.03.22 . E */
    		
    		// TODO Auto-generated method stub
    		final String action = intent.getAction();
    		String defaultTheme = SettingsValue.getDefaultThemeValue(XLauncher.this);
            String defaultAndroidTheme = SettingsValue.getDefaultAndroidTheme(XLauncher.this);

            if (Intent.ACTION_PACKAGE_CHANGED.equals(action)
                    || Intent.ACTION_PACKAGE_REMOVED.equals(action)
                    || Intent.ACTION_PACKAGE_ADDED.equals(action)) {
            	final String packageName = intent.getData().getSchemeSpecificPart();     
                final boolean replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);
                /*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. S***/
                /*final Context tempcontext = context;
                mHandler.post(new Runnable() {
        			@Override
        			public void run() {
			        	if(Intent.ACTION_PACKAGE_ADDED.equals(action)){
		            		final List<LenovoWidgetsProviderInfo> InstalledLeosWidgets = 
		            				new FetchLenovoWeatherWidgetUtil(tempcontext).mInstalledLeosWidgets;
		            		if(InstalledLeosWidgets!=null&&InstalledLeosWidgets.size()>0){
			            		for(LenovoWidgetsProviderInfo item :InstalledLeosWidgets){
			            			if(item.appPackageName.equals(packageName)&&item.isInstalled){
			            	    		LenovoWidgetViewInfo lenovoWidget = new LenovoWidgetViewInfo();
			            	            lenovoWidget.className = item.widgetView;
			            	            lenovoWidget.packageName = item.appPackageName;
			            	            ComponentName component= new ComponentName(lenovoWidget.packageName, lenovoWidget.className);
			            	            lenovoWidget.componentName = component;
			            	            lenovoWidget.minWidth =  item.x;
			            	            lenovoWidget.minHeight = item.y;
			            	            lenovoWidget.previewImage = R.drawable.lotus_icon;
			            	            lenovoWidget.cellX = mPendingAddInfo.cellX;
			            	            lenovoWidget.cellY = mPendingAddInfo.cellY;
			            	            lenovoWidget.screen = mWorkspace.getCurrentPage();
			            	            addLeosWidgetViewToWorkspace(lenovoWidget);
			            			}
			        			}
		            		}
	        			}
        			}
        		});*/
                
                /***RK_ID:RK_REMOVE_DOUBLE_CLICL_DELAY  AUT:zhanglz1@lenovo.com.DATE:2013-01-18. S***/        
//                if (packageName.contains(LauncherService.LEVOICE_PACKAGENAME)) {
//					getHotseat().resetAllappsButtonOnClick();
//					//levoice_bugfix zhanglz1 20130131
//					getHotseat().resetAppsButtonOnClick();
//				}
                /***RK_ID:RK_REMOVE_DOUBLE_CLICL_DELAY  AUT:zhanglz1@lenovo.com.DATE:2013-01-18. E***/        
                int op = SettingsValue.OP_NONE;
                if (packageName == null 
                		|| packageName.length() == 0
                		|| !packageName.contains(SettingsValue.THEME_PACKAGE_NAME_PREF)) {
                    // they sent us a bad intent
                    return;
                }
                if (Intent.ACTION_PACKAGE_CHANGED.equals(action)) {
                    op = SettingsValue.OP_UPDATE;
                } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
                    if (!replacing) {
                        op = SettingsValue.OP_REMOVE;
                    }
                    // else, we are replacing the package, so a PACKAGE_ADDED will be sent
                    // later, we will update the package at this time
                } else if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
                    if (!replacing) {
                        op = SettingsValue.OP_ADD;
                    } else {
                        op = SettingsValue.OP_UPDATE;
                    }
                }

                if (op != SettingsValue.OP_NONE
                        && packageName.equals(SettingsValue.getThemeValue(XLauncher.this))) {
                	if (op == SettingsValue.OP_REMOVE) {
                		dismissThemeDialog();
                		if (packageName.equals(defaultTheme)) {
                			handleTheme(defaultAndroidTheme, false, null);
                		} else {
                			handleTheme(defaultTheme, false, null);
                		}
                	} else {
                		dismissThemeDialog();
                		handleTheme(packageName, true, null);
                	}
                } 

            	if (op == SettingsValue.OP_ADD
                		&& !defaultTheme.equals(defaultAndroidTheme)
                		&& defaultAndroidTheme.equals(SettingsValue.getThemeValue(XLauncher.this))) {
            		handleTheme(defaultTheme, true, null);
                }
            }
    	}
    }
	
	public void pickupOtherWidgets() {
		int appWidgetId = XLauncher.this.mAppWidgetHost.allocateAppWidgetId();
        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        startActivityForResult(pickIntent, REQUEST_PICK_APPWIDGET_LIST);
	}
	
	public void resetPendingInfoBeforePick() {
		resetAddInfo();
        mPendingAddInfo.container = LauncherSettings.Favorites.CONTAINER_DESKTOP;
        mPendingAddInfo.screen = mWorkspace.getCurrentPage();
        mWaitingForResult = true;
	}
	
	/*RK_ID:RK_SD_APPS zhangdxa 2013-4-15. S***/
	public void updateItemsCommend(ArrayList<ItemInfo> listInfo, boolean bCommend){
		Log.i("zdx1","XLauncher.updateItemsCommend, shortcuts size:"+ listInfo.size());
		for (int i=0; i<listInfo.size(); i++) {
	        ItemInfo itemInfo = listInfo.get(i);
	        ShortcutInfo info = (ShortcutInfo)itemInfo;
	        if( bCommend ){
	            info.itemType = LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT;
	            String packageName = null;
	            if (info.intent != null && info.intent.getComponent() != null){
	            	packageName = info.intent.getComponent().getPackageName();
	            }
	            info.uri = XLauncherModel.getCommandAppDownloadUri(packageName, (String)info.title);
	        }else{
	            info.itemType = LauncherSettings.Favorites.ITEM_TYPE_APPLICATION;
	        }
	        info.setIcon(null);
	        XLauncherModel.updateItemInDatabase(XLauncher.this, info);
	            
	        if (itemInfo.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
	         	DrawableItem view = getHotseat().getLayout().getChildAt(itemInfo.cellX, itemInfo.cellY);
	            if (mHotseat != null && view != null) {
	                mHotseat.refreshItem(XLauncher.this, view, itemInfo, mIconCache, true);
	            }
	        }else {
	             XPagedViewItem item = getWorkspace().getPagedView().findPageItemAt(
	            		 itemInfo.screen, itemInfo.cellX, itemInfo.cellY);
	             DrawableItem view = item != null ? item.getDrawingTarget() : null;
	             if( ( view != null ) && ( view instanceof XShortcutIconView) ){
	                 //XShortcutIconView shortcutView = (XShortcutIconView) view;
	                 //shortcutView.applyFromShortcutInfo(info, mIconCache);
	            	 com.lenovo.launcher.components.XAllAppFace.utilities.Utilities.refreshItem(XLauncher.this, 
		            		 view, info, mIconCache, true);
	             }
	         } 
		}
	}
	
	public void updateItemsFolderCommend(ArrayList<ItemInfo> listInfo, ArrayList<ItemInfo> listFolder,boolean bCommend){
		Log.i("zdx1","XLauncher.updateItemsFolderCommend, shortcuts size:"+ listInfo.size());
		for (int i=0; i<listInfo.size(); i++) {
	        ItemInfo itemInfo = listInfo.get(i);
	        ShortcutInfo info = (ShortcutInfo)itemInfo;
	        if( bCommend ){
	            info.itemType = LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT;
	            String packageName = null;
	            if (info.intent != null && info.intent.getComponent() != null){
	            	packageName = info.intent.getComponent().getPackageName();
	            }
	            info.uri = XLauncherModel.getCommandAppDownloadUri(packageName, (String)info.title);
	        }else{
	            info.itemType = LauncherSettings.Favorites.ITEM_TYPE_APPLICATION;
	        }
	        info.setIcon(null);
	        XLauncherModel.updateItemInDatabase(XLauncher.this, info);
		}

		for (int i=0; i<listFolder.size(); i++) {
			ItemInfo itemInfo = listFolder.get(i);
			if (itemInfo.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
	         	DrawableItem view = getHotseat().getLayout().getChildAt(itemInfo.cellX, itemInfo.cellY);
	            if (mHotseat != null && view != null) {
	                mHotseat.refreshItem(XLauncher.this, view, itemInfo, mIconCache, true);
	            }
	            continue;
	        }else {
		        XPagedViewItem item = getWorkspace().getPagedView().findPageItemAt(
       		        itemInfo.screen, itemInfo.cellX, itemInfo.cellY);
                DrawableItem view = item != null ? item.getDrawingTarget() : null;
                if( view == null){
       	            continue;
                }
                if (view instanceof XFolderIcon){
        	        XFolderIcon folderIcon = (XFolderIcon)view;
        	        FolderInfo folderInfo = folderIcon.mInfo;
                	com.lenovo.launcher.components.XAllAppFace.utilities.Utilities.refreshItem(XLauncher.this, 
		            		 view, folderInfo, mIconCache, true);
                }
		    }  
	    }
	}
    /*RK_ID:RK_SD_APPS zhangdxa 2013-4-15. E***/
    /** RK_ID: RK_NEWFEATURE_SHOW_MISSED_NUMBER. AUT: zhanglz1 DATE: 2013-05-06 S */
	private class ShowMissedCallNumTask extends AsyncTask<Void, Void, Integer> {
		@Override
		protected Integer doInBackground(Void... arg0) {	
			try {
				int tempCall = Utilities.getMissedCallNum(getApplicationContext());
				return tempCall;
			} catch (Exception e) {
				// TODO: handle exception
				return 0;
			}
			
		}
		
		@Override
	    protected void onPostExecute(Integer result) {
			if(missedCallNum != result)
				missedCallNum = result;
			
			if(mHotseat != null){
			    mHotseat.updateMissedView(1,missedCallNum);
			}
			if(mWorkspace !=null){
				mWorkspace.updateMissedView(1,missedCallNum);
			}
		}
	}
    private class ShowMissedMsgNumTask extends AsyncTask<Void, Void, Integer> {
		@Override
		protected Integer doInBackground(Void... arg0) {	
			try {
				int tempMsg = Utilities.getMissedMessageNum(getApplicationContext());
				return tempMsg;
			} catch (Exception e) {
				// TODO: handle exception
				return 0;
			}
			
		}
		@Override
	    protected void onPostExecute(Integer result) {
			if(missedMsgNum != result)
				missedMsgNum = result;
			
			if(mHotseat != null){
				mHotseat.updateMissedView(0,missedMsgNum);
			}
			if(mWorkspace !=null){
				mWorkspace.updateMissedView(0,missedMsgNum);
			}
		}
	}
    private static final int MSG_ID_QUERY_CALL = 1;
    private static final int MSG_ID_QUERY_MSG = 2;

	private Handler mMissedNumHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_ID_QUERY_CALL:
				new ShowMissedCallNumTask().execute(new Void[]{});
				break;
			case MSG_ID_QUERY_MSG:	
				new ShowMissedMsgNumTask().execute(new Void[]{});
				break;
			default:
				break;
			}
		}
	}; 

    private final ContentObserver mMissedCallContentObserver = new MissedCallObserver();
    private final ContentObserver mMissedMessageContentObserver = new MissedMessageObserver();
    private int missedMsgNum = -1;
    private int missedCallNum = -1;

    private class MissedCallObserver extends ContentObserver {
        public MissedCallObserver() {
            super(new Handler());
        }
        @Override
        public void onChange(boolean selfChange) {
            mMissedNumHandler.removeMessages(MSG_ID_QUERY_CALL);
            mMissedNumHandler.sendMessageDelayed(mMissedNumHandler
            		.obtainMessage(MSG_ID_QUERY_CALL), 2000);
        }
    }
    private class MissedMessageObserver extends ContentObserver {
        public MissedMessageObserver() {
            super(new Handler());
        }
        @Override
        public void onChange(boolean selfChange) {
            mMissedNumHandler.removeMessages(MSG_ID_QUERY_MSG);
            mMissedNumHandler.sendMessageDelayed(mMissedNumHandler
            		.obtainMessage(MSG_ID_QUERY_MSG), 2000);
        }
    }
    private void registerMissedContentObserver(){
        getContentResolver().registerContentObserver(Calls.CONTENT_URI, true, mMissedCallContentObserver);
        getContentResolver().registerContentObserver(Uri.parse("content://mms-sms/"), true,  mMissedMessageContentObserver);
    }
    public void unRegisterMissedContentObserver() {
        getContentResolver().unregisterContentObserver(mMissedCallContentObserver);
        getContentResolver().unregisterContentObserver(mMissedMessageContentObserver);
    }
    /** RK_ID: RK_NEWFEATURE_SHOW_MISSED_NUMBER. AUT: zhanglz1 DATE: 2013-05-06 E */
    
     /*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. S***/
	public void removeWidgets(ArrayList<ItemInfo> listInfo){
		Log.i("zdx1","XLauncher.removeItemsWidgets, widgets size:"+ listInfo.size());
		for (int i=0; i<listInfo.size(); i++) {
	        ItemInfo itemInfo = listInfo.get(i);
	        Log.i("zdx1","XLauncher.removeItemsWidgets, item:"+ itemInfo.id);
	        LauncherAppWidgetInfo info = (LauncherAppWidgetInfo)itemInfo;   
	        XPagedViewItem item = getWorkspace().getPagedView().findPageItemAt(
	        		 itemInfo.screen, itemInfo.cellX, itemInfo.cellY);
	        DrawableItem view = item != null ? item.getDrawingTarget() : null;
	        if( view != null ){
	        	removeWidget(info,view);
	        }
		}
	}
	
	private void removeWidget(Object info, DrawableItem cell) {
        ItemInfo item = (ItemInfo) info;
        if (info instanceof LauncherAppWidgetInfo
                || (!(info instanceof LenovoWidgetViewInfo) && (item.spanX > 1 || item.spanY > 1))) {
            XLauncherModel.deleteItemFromDatabase(this, item);
            final LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) item;
            final LauncherAppWidgetHost appWidgetHost = getAppWidgetHost();
            if (appWidgetHost != null) {
                new Thread("deleteAppWidgetId") {
                    public void run() {
                        appWidgetHost.deleteAppWidgetId(launcherAppWidgetInfo.appWidgetId);
                    }
                }.start();
            } 
        } 
        if (item.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
            getHotseat().removeView(cell);
//            getHotseat().createDockAddIcon(item.cellX, item.cellY);
        } else {
            getWorkspace().removePagedViewItem(item);
        }
    }
	
	private boolean processLeosWidget(LauncherAppWidgetInfo item,ComponentName cn){
		/* RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-23 START */
		// fix bug 171763
		String LOTUSDEFAULTVIEWHELPER = "com.lenovo.launcher2.gadgets.Lotus.LotusProviderHelper";
		String WEATHERWIDGETVIEWHELPER = "com.lenovo.launcher2.weather.widget.WeatherWidgetAppWidgetProvider";//4*2
		String WEATHERMAGICWIDGETVIEWHELPER = "com.lenovo.launcher2.weather.widget.WeatherWidgetMagicWidgetProvider";//4*1
		String TOGGLEWIDGETVIEWHELPER = "com.lenovo.launcher2.toggle.widget.ToggleWidgetAppWidgetProvider";
		LenovoWidgetViewInfo leosItem = null;
		Log.d("liuyg1", "cn.getClassName()=" + cn.getClassName());
		if (cn.getClassName() != null
				&& cn.getClassName().equals(LOTUSDEFAULTVIEWHELPER)) {
			Log.d("liuyg1", "0000");
			leosItem = new LenovoWidgetViewInfo();
			leosItem.minWidth = 4;
			leosItem.minHeight = 3;
			/*
			 * //cancel by xingqx 2012.12.28
			 * if(getDesplyheightPixels()>854){//800){ modified by liuli1, for
			 * bug 4291 leosItem.minHeight = 4; }else{ leosItem.minHeight = 3; }
			 */

			leosItem.className = Constants.LOTUSDEFAULTVIEWHELPER;
			XLauncherModel.deleteItemFromDatabase(this, item);
			return true;

		} else if (cn.getClassName() != null
				&& cn.getClassName().equals(WEATHERWIDGETVIEWHELPER)) {//zzcao  4*2
			Log.d("liuyg1", "1111");
			leosItem = new LenovoWidgetViewInfo();
			leosItem.minWidth = 4;
			leosItem.minHeight = 2;
			leosItem.className = Constants.WEATHERWIDGETVIEWHELPER;
		} else if (cn.getClassName() != null
				&& cn.getClassName().equals(WEATHERMAGICWIDGETVIEWHELPER)) {//zzcao  4*1
			Log.d("liuyg1", "2222");
			leosItem = new LenovoWidgetViewInfo();
			leosItem.minWidth = 4;
			leosItem.minHeight = 1;
			leosItem.className = Constants.WEATHERMAGICWIDGETVIEWHELPER;

		} else if (cn.getClassName() != null
				&& cn.getClassName().equals(TOGGLEWIDGETVIEWHELPER)) {
			Log.d("liuyg1", "3333");
			leosItem = new LenovoWidgetViewInfo();
			leosItem.minWidth = 4;
			leosItem.minHeight = 1;
			leosItem.className = Constants.TOGGLEWIDGETVIEWHELPER;
		} else {
			return false;
		}

		if (leosItem != null) {
			Log.d("liuyg1", "4444");
			leosItem.packageName = getPackageName();
			ComponentName component = new ComponentName(leosItem.packageName,
					leosItem.className);
			leosItem.componentName = component;
			leosItem.cellX = item.cellX;
			leosItem.cellY = item.cellY;
			leosItem.screen = item.screen;
			XLauncherModel.deleteItemFromDatabase(this, item);
			addLeosWidgetViewToWorkspace(leosItem);

		}
		return true;
	}
    public void onCommendViewClick(LauncherAppWidgetInfo info){
    	if(info.itemType != LauncherSettings.Favorites.ITEM_TYPE_COMMEND_APPWIDGET ||
    		info.uri == null){
    		return;
    	}
    	Uri uri = Uri.parse(info.uri);
        Intent intentView = new Intent(Intent.ACTION_VIEW, uri);
        startActivitySafely(intentView, null);
    }
    
    public void removeLeosWidgetInOtherPackage(ArrayList<ApplicationInfo> apps){
    	Log.i("zdx1","XLauncher.removeLeosWidgetInOtherPackage");
    	if (mWorkspace != null) {
            mWorkspace.removeItems(apps);
        }
    }
    /*RK_ID:RK_SD_WIDGETS kangwei 2013-5-15. S***/
    public void addLeosWidgets(String packageName){
		final List<LenovoWidgetsProviderInfo> InstalledLeosWidgets = 
				new FetchLenovoWeatherWidgetUtil(mApp).mInstalledLeosWidgets;
		if(InstalledLeosWidgets!=null&&InstalledLeosWidgets.size()>0){
    		for(LenovoWidgetsProviderInfo item :InstalledLeosWidgets){
    			if(item.appPackageName.equals(packageName)&&item.isInstalled){
    	    		LenovoWidgetViewInfo lenovoWidget = new LenovoWidgetViewInfo();
    	            lenovoWidget.className = item.widgetView;
    	            lenovoWidget.packageName = item.appPackageName;
    	            ComponentName component= new ComponentName(lenovoWidget.packageName, lenovoWidget.className);
    	            lenovoWidget.componentName = component;
    	            lenovoWidget.minWidth =  item.x;
    	            lenovoWidget.minHeight = item.y;
    	            lenovoWidget.previewImage = R.drawable.lotus_icon;
    	            lenovoWidget.cellX = mPendingAddInfo.cellX;
    	            lenovoWidget.cellY = mPendingAddInfo.cellY;
    	            lenovoWidget.screen = mWorkspace.getCurrentPage();
    	            addLeosWidgetViewToWorkspace(lenovoWidget);
    	            break;
    			}
			}
		}
	
    }
    /*RK_ID:RK_SD_WIDGETS kangwei 2013-5-15. E***/
	/*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. E***/

// dooba edit s
	public void refreshMngView(int screen) {
		if (this.mScreenMngView.isVisible()) {
        	mScreenMngView.onAddSimpleInfo(screen);
        }
	}
	
	/**
	 * 立即刷新屏幕管理
	 */
	public void refreshMngViewDelayed(long time, int screen) {
		if (this.mScreenMngView.isVisible()) {
        	mScreenMngView.onAddSimpleInfo(time, screen);
        }
	}
	
	private XScreenIconPkgDialog mIconPkgDialog;
	
	/**
	 * 屏幕管理应用的多选弹出框的三个按钮的响应事件
	 */
	private DrawableItem.OnClickListener mIconPkgClickListener = new DrawableItem.OnClickListener() {
		@Override
		public void onClick(DrawableItem item) {
			if (item == null || item.getTag() == null || !(item.getTag() instanceof Integer)) {
				return;
			}
			//int[] resultSpan = new int[2];
			int screen = mWorkspace.getCurrentPage();
			final long container = LauncherSettings.Favorites.CONTAINER_DESKTOP;
			boolean createFolder = true;
			List<XScreenShortcutInfo> list = (List<XScreenShortcutInfo>)mIconPkgDialog.getTag();
			int relativeX = mIconPkgDialog.getIconRelativeX(mIconPkgDialog.getRelativeX());
			int relativeY = mIconPkgDialog.getIconRelativeY(mIconPkgDialog.getRelativeY());
			
			int action = (Integer)item.getTag();
			switch (action) {
			//创建文件夹
	        case XScreenIconPkgDialog.ICON_PKG_CREATE_FOLDER:
	        	int[] targetCell = new int[2];
	        	
//	        	targetCell = mWorkspace.createArea(relativeX,
//	        			relativeY, 1, 1, 1, 1,
//	                    null, targetCell, resultSpan, XWorkspace.MODE_ON_DROP);
	                                
	        	targetCell = mWorkspace.findNearestArea(screen, relativeX, relativeY, 1, 1,
	                    null, true, targetCell);
	            if (targetCell == null || targetCell[0] < 0 || targetCell[1] < 0) {
	            	return;
	            }
	            addIconPkgFromMngView(list, 
	        			container, screen, targetCell[0], targetCell[1], createFolder);  
	        	break;
	        	//散放应用
	        case XScreenIconPkgDialog.ICON_PKG_ADD_ICONS:
	        	addIconPkgFromMngView(list, 
	        			container, screen, -1, -1, !createFolder);  
	        	break;
	        	//取消操作
	        case XScreenIconPkgDialog.ICON_PKG_CANCEL_DRAG:
	        	break;
	        default:
	        	break;
	        }
			hideIconPkgDialog();
		}
	};
	
	/**
	 * 清除屏幕编辑多选的弹出框
	 */
	private void hideIconPkgDialog() {
		if (mIconPkgDialog != null) {		
			mIconPkgDialog.dismiss();
        	mBlackboard.hide();
        	mWorkspace.desireTouchEvent(true);
        }
	}
	
	/**
	 * 显示屏幕编辑的多选弹出框
	 * @param touchXY
	 * @param d
	 * @return
	 */
	public boolean showIconPkgDialog(final int[] touchXY, XDragObject d) {
//		if (this.mScreenMngView.isVisible()) {			
//			return false;
//		}
		int width = getResources().getDimensionPixelSize(R.dimen.xscreen_mng_icon_pkg_dlg_width);
		int height = getResources().getDimensionPixelSize(R.dimen.xscreen_mng_icon_pkg_dlg_height);
		int statusBarHeight = getStatusBarHeight();
		Bitmap snap = d.dragView.getSnapshot(1);
		int positionType = XScreenIconPkgDialog.POS_LEFT_BOTTOM;
		
		/**
		 * 根据手指的位置计算弹出框的样式类型
		 */
		if (d.dragView.getRelativeX() < (mWorkspace.getWidth() - width) 
				&& ((d.dragView.getRelativeY() - statusBarHeight) <= d.dragView.getHeight())) {
			positionType = XScreenIconPkgDialog.POS_LEFT_TOP;
		} else if (d.dragView.getRelativeX() >= (mWorkspace.getWidth() - width) 
				&& ((d.dragView.getRelativeY() - statusBarHeight) <= d.dragView.getHeight())) {
			positionType = XScreenIconPkgDialog.POS_RIGHT_TOP;
		} else if (d.dragView.getRelativeX() >= (mWorkspace.getWidth() - width)
				&& (d.dragView.getRelativeY() - statusBarHeight) > d.dragView.getHeight()) {
			positionType = XScreenIconPkgDialog.POS_RIGHT_BOTTOM;
		}
		
		mBlackboard.show(true);
		mWorkspace.desireTouchEvent(false);
		
		/**
		 * 在松手的地方弹框
		 */
		mIconPkgDialog = new XScreenIconPkgDialog(mMainSurface, snap, d.dragInfo, new RectF(0, 0, width, height), positionType);
		mIconPkgDialog.setRelativeX(d.dragView.getRelativeX() - mIconPkgDialog.getIconRelativeX());
		mIconPkgDialog.setRelativeY(d.dragView.getRelativeY()
				- statusBarHeight - mIconPkgDialog.getIconRelativeY());
		mIconPkgDialog.setExtraTouchBounds(new RectF(0, 0, mDragLayer.getWidth(), mDragLayer.getHeight()));
		mIconPkgDialog.setOnClickListeners(mIconPkgClickListener);
		mIconPkgDialog.setOnDestroyListener(new XScreenIconPkgDialog.OnDestroyListener() {
			@Override
			public void onDestroy() {
				mDragLayer.removeItem(mIconPkgDialog);
	        	mIconPkgDialog = null;
			}
			
		});
		
		mIconPkgDialog.desireTouchEvent(true);
		mDragLayer.addItem(mIconPkgDialog);
		
		return true;
	}
	
	/**
	 * 屏幕编辑多选功能的批量形成文件夹/散放
	 * @param list
	 * @param container
	 * @param screen
	 * @param cellX
	 * @param cellY
	 * @param createFolder
	 */
	public void addIconPkgFromMngView(List<XScreenShortcutInfo> list, 
			long container, int screen, int cellX, int cellY, boolean createFolder) {
		if (createFolder) {
			final FolderInfo folderInfo = new FolderInfo();
	        folderInfo.title = getText(R.string.folder_name);
	
	        // Update the model
	        folderInfo.screen = screen;
	        folderInfo.cellX = cellX;
	        folderInfo.cellY = cellY;
	        XLauncherModel.addItemToDatabase(this, folderInfo, container, folderInfo.screen, folderInfo.cellX, folderInfo.cellY, false);
	        addFolder(folderInfo);
	        
	        mWorkspace.addInScreen(folderInfo);
	        
	        int maxLoadIndex = Math.min(list.size(),
                    XLauncherModel.getCellCountX() * XLauncherModel.getCellCountY() * 5);
	        
	        for (int i = 0; i < maxLoadIndex; i++) {                
	            final XScreenShortcutInfo info = 
	            		new XScreenShortcutInfo(list.get(i));
	            if (info != null) {
	                info.container = ItemInfo.NO_ID;
	                                    
	                info.cellX = 0;
	                info.cellY = 0;
	                info.screen = 0;
	            }
	            folderInfo.add(info);  
	        }
	        refreshMngView(screen);
	        return;
		}
		
        ArrayList<Point> emptyPoints = mWorkspace.getPagedView().findVacantCellNumber(screen);
        
        if (emptyPoints == null || emptyPoints.size() < list.size()) {
        	Toast.makeText(XLauncher.this, R.string.workspace_pick_applications_nospace, Toast.LENGTH_SHORT).show();
            mWaitingForResult = false;
            return;
        }

        for (int i = 0; i < list.size(); i++) {
        	final XScreenShortcutInfo info = 
            		new XScreenShortcutInfo(list.get(i));
        	final Point point = emptyPoints.get(i); 
            if (info != null) {
            	addApplication(info, container, screen, point.x, point.y);
            }
        }
        refreshMngView(screen);
	}
	
	public XScreenMngView getXScreenMngView() {
		return mScreenMngView;
	}
	
	public void addApplication(ShortcutInfo info, long container, int screen, int cellX, int cellY) {
		if (info == null) {
			return;
		}
		info.container = ItemInfo.NO_ID;
        
        info.screen = screen;
        info.cellX = cellX;
        info.cellY = cellY;
        info.spanX = 1;
        info.spanY = 1;
        mWorkspace.addInScreen(info, mIconCache, false);
        XLauncherModel.addOrMoveItemInDatabase(this, info, container, screen, cellX, cellY);
	}
	
	private Toast mOutOfSpaceMsg = null;
    private Runnable mOutOfSpaceMsgRunnable  = new Runnable() {

		@Override
		public void run() {
			if(mOutOfSpaceMsg == null) {
				mOutOfSpaceMsg = Toast.makeText(XLauncher.this, getString(R.string.application_name)+" : "+getString(R.string.out_of_space),
					Toast.LENGTH_SHORT);
			} else{
				mOutOfSpaceMsg.setText(getString(R.string.application_name)+" : "+getString(R.string.out_of_space));
			}
			mOutOfSpaceMsg.show();
		}		
	};
	// dooba edit e
	AppFlagReceiver mAppFlagReceiver;
	private class AppFlagReceiver extends BroadcastReceiver {
        
        @Override
        public void onReceive(Context context, Intent intent) {
            if(LoadBootPolicy.getInstance(XLauncher.this).getDefaultProfileProcessingState()){
                return;
            }
            
            final String action = intent.getAction();
            if ("com.android.intent.action.NEW_NOTIFICATION".equals(action)) {
                String pkgName = intent.getStringExtra("packageName");
                int num = intent.getIntExtra("messageNum", 0);
                if (!mModel.getAllAppsList().isNewAddApk(XLauncher.this, pkgName))
                {
                    R5.echo("receive NEW_NOTIFICATION pkgName = " + pkgName + "    num = " + num);
                    clearAndShowNewBg(pkgName, num);
                }                
            } 
        }
    }
	    
    public void clearFolderNewBgAndSetNum(String componentName, int num){
        FolderInfo folderInfo;
        Set<Map.Entry<Long, FolderInfo>> set = sFolders.entrySet();
        for (Map.Entry<Long, FolderInfo> map : set) {
            folderInfo = map.getValue();
            int size = folderInfo.contents.size();
            for (int i = 0; i < size; i++)
            {
                ShortcutInfo item = folderInfo.contents.get(i);
                if (item != null)
                {
                    ComponentName component = item.intent.getComponent();
                    if (component != null && componentName.equals(component.flattenToString()))
                    {
                        item.updateInfo(num);
                    }
                }
            }
            
        }
    }
    
    public void clearAndShowNewBg(String componentName){
        
        String str = Settings.System.getString(getContentResolver(),"NEWMSG_" + componentName);
        int num = 0;
        if (str != null && !str.isEmpty())
        {
            try
            {
                num = Integer.parseInt(str);
            }
            catch (Exception e)
            {
                num = 0;
                R5.echo("no save int");
            }
        }        
        mModel.getAllAppsList().removeNewApk(this, componentName);
        clearAndShowNewBg(componentName, num);       
        
    }
    
    public void clearAndShowNewBg(String componentName, int num){
        if (mAppListView != null && !mModel.getAllAppsList().isNewAddApk(XLauncher.this, componentName))
        {
            mAppListView.getAppContentView().clearNewBgAndSetNum(componentName, num);  
        }
              
//        clearFolderNewBgAndSetNum(componentName, num);
        mWorkspace.clearNewBgAndSetNum(componentName, num);
        
        if (mHotseat != null)
        {
        	mHotseat.clearNewBgAndSetNum(componentName, num); 
        }
    }
    
    public void setPendingObjectPos(int cellX, int cellY) {
    	mPendingAddInfo.cellX = cellX;
    	mPendingAddInfo.cellY = cellY;
    }
    
    public void setPendingObjectPos(int[] pos) {
    	mPendingAddInfo.dropPos = pos;
    }
    
    private void setBrightness(int brightness) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
        getWindow().setAttributes(lp);
    }
    
    //dooba zhanggx1 .s
    public void addAppWidgetFromSlidingBar(final AppWidgetProviderInfo widgetInfo,
    		final int container, final int screen, final int[] cell, final int[] position) {
		final PendingAddWidgetInfo createInfo = new PendingAddWidgetInfo(widgetInfo, null,
                null);
		addAppWidgetFromDrop(createInfo, container, screen, cell, position);
		long delay = (createInfo != null 
        		&& createInfo.componentName != null 
        		&& "com.lenovopad.guide".equals(createInfo.componentName.getPackageName())) ? 3000L : 800L;
		refreshMngViewDelayed(delay, screen);
    }
    
    private void addFolderOrShortcuts(boolean isFolder, String folderName, ArrayList<String> intentList) {
        mIntentListInfo = intentList;
        int currentPage = mWorkspace.getCurrentPage();
        int[] cellXY = new int[2];
        cellXY[0] = mPendingAddInfo.cellX;
        cellXY[1] = mPendingAddInfo.cellY;
        addMoreShortcutsOnce(isFolder, folderName, currentPage, cellXY);
    }
    //dooba zhanggx1 .e
    
    public void doubleClick(){
        String[] prefGestureValues = getResources().getStringArray(R.array.pref_gesture_values);
        String doubleClick = SettingsValue.getDoubleClickGuestureArray(XLauncher.this);
        if(doubleClick.equals(prefGestureValues[1])){
            XLauncher.setScreen(mWorkspace.getCurrentPage());
            showMenu();
        }else if(doubleClick.equals(prefGestureValues[2])){
        	mGestureManager.showRecentTask();
        }else if(doubleClick.equals(prefGestureValues[3])){
        	mGestureManager.showNotifications();
        }
    }
    
    public Handler getLauncherHandler() {
        return mLauncherHandler;
    }
    
    private void restartWeatherService(){
        if(WeatherUtilites.hasInstances(XLauncher.this,WeatherUtilites.THIS_WEATHER_WIDGET))
        {
            Intent intent = new Intent(WeatherUtilites.ACTION_WEATHER_WIDGET_SEVICE_RESTART);
            sendBroadcast(intent);
        }
//      }else if(WeatherUtilites.hasInstances(XLauncher.this,GadgetUtilities.TASKMANAGERWIDGETVIEWHELPER)){
//          Intent intent = new Intent(WeatherUtilites.ACTION_ADD_TASKMANAGER_WIDGET);
//          startService(intent.setClass(XLauncher.this,WidgetService.class));
//      }
    }
    
    private class DownloadFilesTask extends AsyncTask {
		@Override
		protected Object doInBackground(Object... arg0) {
			restartWeatherService();
			return null;
		}
    }
    
    private static int DIRECTION_NONE = 0;
    private static int DIRECTION_SAME = 1;
    private static int DIRECTION_OPPOSITE_CENTER = 2;
    private static int DIRECTION_OPPOSITE_OUT = 3;
    
    private static int TWO_FINGER_SCROLL_DISTANCE = 20;
    private static int TWO_FINGER_IGNORE_DISTANCE = 1;
	
	private int getOrientation(int x1start, int x1end, int x2start, int x2end){
		int smallStart;
		int smallEnd;
		int bigStart;
		int bigEnd;
		
		if (x1start < x2start)
		{
			smallStart = x1start;
			smallEnd = x1end;
			bigStart = x2start;
			bigEnd = x2end;
		}
		else
		{
			smallStart = x2start;
			smallEnd = x2end;
			bigStart = x1start;
			bigEnd = x1end;
		}
		
        int dsmall = smallEnd - smallStart;
        int dbig = bigEnd - bigStart;
        
        if (dsmall > TWO_FINGER_SCROLL_DISTANCE && dbig < TWO_FINGER_IGNORE_DISTANCE)
        {
        	return DIRECTION_OPPOSITE_CENTER;
        }
        else if (dbig < -TWO_FINGER_SCROLL_DISTANCE && dsmall > -TWO_FINGER_IGNORE_DISTANCE)
        {
        	return DIRECTION_OPPOSITE_CENTER;
        }
        else if (dbig > TWO_FINGER_SCROLL_DISTANCE && dsmall < TWO_FINGER_IGNORE_DISTANCE)
        {
        	return DIRECTION_OPPOSITE_OUT;
        }
        else if (dsmall < -TWO_FINGER_SCROLL_DISTANCE && dbig > -TWO_FINGER_IGNORE_DISTANCE)
        {
        	return DIRECTION_OPPOSITE_OUT;
        }
        
        return DIRECTION_SAME;
		
    }
	
    private boolean isSameOrientation(int dx1, int dx2, int dy1, int dy2){
    	R5.echo("dx1 = " + dx1 + "dx2 = " + dx2 + "dy1 = " + dy1 + "dy2 = " + dy2);
        if ((dx1 > TWO_FINGER_IGNORE_DISTANCE && dx2 < -TWO_FINGER_IGNORE_DISTANCE)
                || (dx1 < -TWO_FINGER_IGNORE_DISTANCE && dx2 > TWO_FINGER_IGNORE_DISTANCE))
        {
        	return false;
        }
        
        if ((dy1 > TWO_FINGER_IGNORE_DISTANCE && dy2 < -TWO_FINGER_IGNORE_DISTANCE)
                || (dy1 < -TWO_FINGER_IGNORE_DISTANCE && dy2 > TWO_FINGER_IGNORE_DISTANCE))
        {
        	return false;
        }
                
        return true;
    }
    
    
    public static final String SWAPCONFIG_SETTING_FILE = "//sdcard/lelauncher/swap_config.xml";
    public int readSettingsFromFile(String settingName) {        
        File defaultProfile = new File(SWAPCONFIG_SETTING_FILE);
        if (!defaultProfile.exists()) {
            Log.d(TAG, "Default profile not found !");
            return 0;
        }
        
        Log.d(TAG, "Default profile found ");               
        int value = 0;
        try{
            InputStream ins = new FileInputStream(defaultProfile);
            value = getIntSetting(ins, settingName);
            ins.close();
        } catch (Exception e) {
            Log.e(TAG, "parse weather xml data error: " + e.toString());
            e.printStackTrace();
        }
                
        return value;
    }
    
    private int getIntSetting(InputStream mInputStream, String settingName) throws Exception {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(mInputStream, null);
        int eventType = xpp.getEventType();
                
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                String tag = xpp.getName();    
                // get the wind
                if (tag.equals("string")) {
                    String name = xpp.getAttributeValue(0);
                    Log.d(TAG, "name = " + name);
                    if (name.equals(settingName))
                    {
                        String value = xpp.getAttributeValue(1); 
                        Log.d(TAG, "value = " + value);
                        int ret = Integer.parseInt(value);
                        return ret;
                    }
                }
            }
            eventType = xpp.next();
        }
                   
        return 0;
    }
    
    private void reloadSwapConfig(){
        int reordertime = readSettingsFromFile("reordertime");
        int foldercreatetime = readSettingsFromFile("foldercreatetime");
        int distancefolercreate = readSettingsFromFile("distancefolercreate");
        int scrollzone = readSettingsFromFile("scrollzone");
        
        if (reordertime != 0)
        {
            R5.echo("reordertime = " + reordertime);
            mWorkspace.REORDER_TIMEOUT = reordertime;
        }
        
        if (foldercreatetime != 0)
        {
            R5.echo("foldercreatetime = " + foldercreatetime);
            mWorkspace.FOLDER_CREATION_TIMEOUT = foldercreatetime;
        }
        
        if (distancefolercreate != 0)
        {
            R5.echo("distancefolercreate = " + distancefolercreate);
            float factor = (float)(distancefolercreate / 100f);
            R5.echo("distancefolercreate factor = " + factor);		
            mWorkspace.mMaxDistanceForFolderCreationFactor = factor;
            mWorkspace.mMaxDistanceForFolderCreation = factor * getResources().getDimensionPixelSize(R.dimen.app_icon_size);
        }
        
        if (scrollzone != 0)
        {
            R5.echo("scrollzone = " + scrollzone);
            mDragController.mScrollZone = scrollzone * ((int)getResources().getDisplayMetrics().density);
        }
        
        int longpresstime = readSettingsFromFile("longpresstime");
        if (longpresstime != 0)
        {
        	R5.echo("longpresstime = " + longpresstime);
        	LGestureDetector.setLongPressTimeOut(longpresstime);
        }
        
        int rescrolldelay = readSettingsFromFile("rescrolldelay");
        if (rescrolldelay != 0)
        {
            R5.echo("rescrolldelay = " + rescrolldelay);
            mDragController.RESCROLL_DELAY = rescrolldelay;
        }
        
        int normalscrolldelay = readSettingsFromFile("normalscrolldelay");
        if (normalscrolldelay != 0)
        {
            R5.echo("normalscrolldelay = " + normalscrolldelay);
            mDragController.SCROLL_DELAY = normalscrolldelay;
        }
        
        int xscrollfactor = readSettingsFromFile("xscrollfactor");
        if (normalscrolldelay != 0)
        {
            R5.echo("xscrollfactor = " + xscrollfactor);
            XPagedView.SCROLL_X_FACTOR = (float)xscrollfactor / 100f;
        }
        
        int yscrollfactor = readSettingsFromFile("yscrollfactor");
        if (yscrollfactor != 0)
        {
            R5.echo("yscrollfactor = " + yscrollfactor);
            XPagedView.SCROLL_Y_FACTOR = (float)yscrollfactor / 100f;
        }
        
        int xanimdurtation = readSettingsFromFile("xanimdurtation");
        if (xanimdurtation != 0)
        {
            R5.echo("xanimdurtation = " + xanimdurtation);
            XPagedView.OffsetXAnimDuration = xanimdurtation;
        }
        
        int touchslop = readSettingsFromFile("touchslop");
        if (touchslop != 0)
        {
        	touchslop = (int)(touchslop * getResources().getDisplayMetrics().density);
            R5.echo("touchslop = " + touchslop);
            LGestureDetector.mTouchSlopSquare = touchslop * touchslop;
        }
        
        int srolldegree = readSettingsFromFile("srolldegree");
        if (srolldegree != 0)
        {
            LGestureDetector.mScrollYfactor = (float)Math.tan(srolldegree * Math.PI / 180);
            R5.echo("srolldegree = " + srolldegree + "LGestureDetector.mScrollYfactor = " + LGestureDetector.mScrollYfactor);
        }   
        
    }
    
//    private boolean mBlockEvent = false;
    
    public boolean isSameOrientation(MotionEvent event){

        mBottomFingerCurrX = event.getX(0);
        mBottomFingerCurrY = event.getY(0);
        mTopFingerCurrX = event.getX(1);
        mTopFingerCurrY = event.getY(1);
        R5.echo("mBottomFingerCurrX = " + mBottomFingerCurrX + "mBottomFingerCurrY = " + mBottomFingerCurrY
                + "mTopFingerCurrX = " + mTopFingerCurrX + "mTopFingerCurrY = " + mTopFingerCurrY);
        float s = mTopFingerCurrY-mTopFingerBeginY;
        float k = mBottomFingerCurrY-mBottomFingerBeginY;
        float m = mTopFingerCurrX-mTopFingerBeginX;
        float n = mBottomFingerCurrX-mBottomFingerBeginX;

        boolean isSameOrientation = isSameOrientation((int)m, (int)n, (int)s, (int)k);
        
        return isSameOrientation;
    
    }
    
//    public void setBlockEvent(boolean value){
//    	mBlockEvent = value;
//    }
    

    private float[] getPreviewAnimScale() {
    	float[] result = new float[5];
    	if (mScreenMngView == null) {
    		result[0] = 1.0f;//缩小系数
            result[1] = 0;//transX
            result[2] = 0;//transY;
            result[3] = 0;//transY;
            result[4] = 0;//transY;
    		return result;
    	}
    	float previewScreenWidth = mScreenMngView.getCellWidth() - XScreenItemView.getWidthGap(this) * 2;    	 
    	int height = mScreenMngView.getHomeHeight();
        int extTop = getResources().getDimensionPixelOffset(R.dimen.xscreen_mng_thumbnail_margin_top);
    	float previewScreenHeight = mScreenMngView.getCellHeight() - XScreenItemView.getHeightGap(this) - height - extTop;
    	
    	float newHeight = mWorkspace.getHeight() + mWorkspace.getTop();
        float scaleW = previewScreenWidth / (mWorkspace.getWidth() * 1.0f);
        float scaleH = previewScreenHeight / (newHeight * 1.0f);
        float scale = Math.min(scaleW, scaleH);
        
        int paddingLeft = getResources().getDimensionPixelOffset(R.dimen.xscreen_mng_padding_left);
        int paddingTop = getResources().getDimensionPixelOffset(R.dimen.xscreen_mng_padding_top);
        
        float imageLeft = (previewScreenWidth - scale * mWorkspace.getWidth()) / 2.0f;
                
        result[0] = scale;//缩小系数
        result[1] = imageLeft + paddingLeft + XScreenItemView.getWidthGap(this);//transX
        result[2] = extTop + paddingTop;//transY;
        result[3] = mScreenMngView.getCellWidth();
        result[4] = mScreenMngView.getCellHeight();
        return result;
    }

    void deleteFromLauncher(ShortcutInfo info) {
        XLauncherModel.deleteItemFromDatabase(this, info);

        if (info.container > 0) {
            // this is folder icon. check folder if it is empty.
            long id = info.container;
            FolderInfo fInfo = XLauncherModel.sFolders.get(id);

            if (fInfo != null) {
                XFolder folder = getFolderInstance(fInfo);
                folder.updateFinalAlpha();
                folder.getXFolderIcon().invalidate();
                folder.deleteNonContentFolder();
            }
        }
    }

    XFolder getFolderInstance(FolderInfo fInfo) {
        XFolderIcon icon;
        if (fInfo.container == LauncherSettings.Favorites.CONTAINER_DESKTOP) {
            XWorkspace workspace = this.getWorkspace();
            XPagedView pageView = workspace.getPagedView();

            int index = pageView.getCellIndex(fInfo.screen, fInfo.cellX, fInfo.cellY);
            XCell cell = (XCell) pageView.getChildAt(index);
            icon = (XFolderIcon) cell.getDrawingTarget();

        } else {
            XHotseatCellLayout content = this.getHotseat().getLayout();
            icon = (XFolderIcon) content.getChildAt(fInfo.cellX);

        }

        return icon.mFolder;
    }

    private Bitmap getOriginalAppWidgetInfoBitmap( LauncherAppWidgetInfo item ){
    	ContentResolver cr = getContentResolver();
    	Cursor c = cr.query(LauncherSettings.Favorites.CONTENT_URI,
    			new String[]{
    			LauncherSettings.Favorites.ICON}, "appWidgetId=?", 
    			new String[]{ "" + item.appWidgetId }, null);
    	try {
			if( c.moveToNext() ){
				((FavoriteWidgetView)item.commendView).removeSelf();
				byte[] arr = c.getBlob(c.getColumnIndex(LauncherSettings.Favorites.ICON));
				int len = arr.length;
				return BitmapFactory.decodeByteArray( arr, 0, len).copy(Bitmap.Config.ARGB_8888, true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Bitmap bmp = null;
			try {
				bmp = item.iconBitmap.copy(Bitmap.Config.ARGB_8888, true);
			} catch (Throwable e2) {
			}
			return bmp;
		} finally{
			c.close();
		}
		
		return item.iconBitmap.copy(Bitmap.Config.ARGB_8888, true);
    }
    
    
	public boolean isLauncherAtStackTop() {
		ComponentName cname = new ComponentName(this.getPackageName(), this
				.getClass().getName());
		ActivityManager manager = (ActivityManager) this
				.getSystemService(ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
		ComponentName cmpNameTemp = null;

		if (null != runningTaskInfos) {

			cmpNameTemp = runningTaskInfos.get(0).topActivity;

//			Log.e("cmpname", "cmpname:" + cname + " ,  tmpname : " + cmpNameTemp);

		}

		if (null == cmpNameTemp)
			return false;
		return cmpNameTemp.getPackageName().equals(cname.getPackageName());
	}
	
	//add by zhanggx1 for new layout.s
	@Override
	public void onWindowAttributesChanged(LayoutParams params) {
		super.onWindowAttributesChanged(params);
		
        if (SettingsValue.hasExtraTopMargin() && mMainSurface != null) {
        	mMainSurface.showDeleteBarOrNot(isCurrentWindowFullScreen());
        }
	}
	//add by zhanggx1 for new layout.e

	/** ID: fix bug: LELAUNCHER-89. AUT: zhaoxy . DATE: 2013.09.02 . S */
    @Override
    public boolean isDragging() {
        return mDragController != null && mDragController.isDragging();
    }
    /** ID: fix bug: LELAUNCHER-89. AUT: zhaoxy . DATE: 2013.09.02 . E */
    
	private float mLastMotionY;
	private float mLastMotionX;
	private boolean mPointerThanTwo = false;
	
	//add by zhanggx1 for refresh mng view.s
	public void refreshMngViewOnAddScreen() {
		if (isScreenMngOpenedNotExiting()) {
			if (mDragController != null && mScreenMngView.isPageDragging()) {
				mDragController.cancelDrag();
			}
        	mScreenMngView.refreshOnAddScreen();
        }
	}
	
	public void refreshMngViewOnUpdateWorkspace(List<Integer> screenList) {
		if (isScreenMngOpenedNotExiting()) {
			if (mDragController != null && mScreenMngView.isPageDragging()) {
				mDragController.cancelDrag();
			}
        	mScreenMngView.refreshOnUpdateWorkspace(screenList);
        }
	}
	
	public void refreshMngViewOnUpdateWorkspace() {
		if (isScreenMngOpenedNotExiting()) {
			if (mDragController != null && mScreenMngView.isPageDragging()) {
				mDragController.cancelDrag();
			}
        	mScreenMngView.refreshThumbs();
        }
	}
	//add by zhanggx1 for refresh mng view.e
	/*add by zhanggx1 on 2013-10-17 for theme appling.s*/
    public void endThemeAppling(final ArrayList<ApplicationInfo> result, final String mFlag) {
    	/** RK_ID: RK_BUG_174814. AUT: zhangdxa DATE: 2013-3-28 S */
		SharedPreferences.Editor editorEnd = getSharedPreferences("theme_apply", Context.MODE_PRIVATE).edit();
		editorEnd.putString(SettingsValue.PREF_THEME, null);
		editorEnd.commit();
		/** RK_ID: RK_BUG_174814. AUT: zhangdxa DATE: 2013-3-28 E */
		
		/** RK_ID: RK_BUG_174814. AUT: zhangdxa DATE: 2013-3-28 S */
	    Log.i("zdx1","ApplyThemeTask.mFlg:"+ mFlag);
	    if (result == null) {
	        if(mFlag == null || mFlag.isEmpty() || !mFlag.equals("theme_apply")){
			    Toast.makeText(XLauncher.this, R.string.theme_pkg_not_exist, Toast.LENGTH_SHORT);
		    }
	        return;
	    }
	    /** RK_ID: RK_BUG_174814. AUT: zhangdxa DATE: 2013-3-28 E */
	    
		synchronized(mThemeLock) {
		    refreshForThemes(result);
		}

		/** RK_ID: RK_BUG_174814. AUT: zhangdxa DATE: 2013-3-28 S */
	    if(mFlag == null || mFlag.isEmpty() || !mFlag.equals("theme_apply")){
	    	dismissProgressDialog(R.string.theme_appling_success);
	    } else {
	    	dismissProgressDialog(0);
	    }
	    /** RK_ID: RK_BUG_174814. AUT: zhangdxa DATE: 2013-3-28 E */
        ShadowUtilites.createGlowingOutline(XLauncher.this);
    }
    
    public void changeIconStyle(final boolean draglayer, final boolean allapp, final boolean bitmapUpdate, final int currentIconStyle,final int currentIconSize) {
    	if (draglayer) {
            // workspace
            if (mWorkspace != null)
                mWorkspace.refreshIconStyle(mIconCache, bitmapUpdate);

            // hotseat
            if (mHotseat != null)
                mHotseat.refreshIconStyle(mIconCache, bitmapUpdate);
        }

        if (bitmapUpdate) {
            Intent finishIntent = new Intent(SettingsValue.ACTION_FOR_LAUNCHER_FINISH);
            this.sendBroadcast(finishIntent);
        }

        boolean single = SettingsValue.getSingleLayerValue(this);
        Log.i(TAG, "refreshIconStyleAndSize~~ single is  ====" + single);
        if (single) {
            return;
        }

        final boolean onlyBitmap = bitmapUpdate;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mAppListView != null) {
                    if (onlyBitmap)
                        mAppListView.updateTheme(getModel().getAllAppsList().data);
                    else
                        mAppListView.updateForText();
                }
            }
        });
    }
    /*add by zhanggx1 on 2013-10-17 for theme appling.e*/
    
    public boolean isWorkspaceNormalState() {
    	
        if (isFolderAnimating)
        {
        	return false;
        }
        
        if (mWorkspace.getOpenFolder() != null)
        {
        	return false;
        }
    	                
        if( mWorkspace.getPagedView().isPageMoving ){
        	return false;
        }

        if (folderPageMoving()) {
            return false;
        }

        if (mDragController.isDragging()) 
    	{
    		return false;
    	}
        
        if (mDragLayer.isResizeWidget())
        {
    		return false;
        }
        
        if (mDragLayer.isPendRunning())
        {
    		return false;
        } 
        
        if (isScreenMngOpendOrExiting())
        {
    		return false;
    	}
        
        if (mState == State.APPS_CUSTOMIZE) 
        {
    		return false;
    	}
        
        if (!mWorkspace.isTouchable()) {
    		return false;
        }
        
        if (Float.compare(getDragLayer().getAlpha(), 1.0f) != 0) {
    		return false;
    	}
        
        if (isCurrentWindowFullScreen())
        {
        	return false;
        }
        
        if (mWorkspace.getAlpha() < 1f)
        {
        	return false;
        }
        
        
        if (mDragLayer.getAlpha() < 1f)
        {
        	R5.echo("mDragLayer alpha");
        	return false;
        }
        
        if (mWorkspace.getPagedView().getAlpha() < 1f)
        {
        	R5.echo("getPagedView alpha");
        	return false;
        }
        
        return true;
    }
    
    private boolean isScreenMngOpenedNotExiting() {
    	return (mScreenMngView != null
        		&& (mScreenMngView.isVisible() || isAnimPreviewScreen())
        		&& !isAnimCloseScreen());
    }
    
    private boolean isScreenMngOpendOrExiting() {
    	return (mScreenMngView != null
        		&& (mScreenMngView.isVisible() || isAnimPreviewScreen() || isAnimCloseScreen()));
    }

    public boolean isWidgetConfiged() {
        return mPaused || isConfiguringWidget;
    }
    
    public void removeCheckedApp(final ArrayList<ShortcutInfo> infos) {
    	if (infos == null) {
    		return;
    	}
    	XPagedViewItem pv; 
    	for (ShortcutInfo info : infos) {
    		if (info.container == LauncherSettings.Favorites.CONTAINER_DESKTOP && mWorkspace != null) {
    			pv = mWorkspace.getPagedView().findPageItemAt(info.screen, info.cellX, info.cellY);
    			if (pv == null) {
    				continue;
    			}
    			if (pv instanceof XDropTarget) {
                    mDragController.removeDropTarget((XDropTarget) pv);
                }
    			mWorkspace.getPagedView().removePagedViewItem(pv);
    			XLauncherModel.deleteItemFromDatabase(this, info);
    		} else if (info.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT && mHotseat != null) {
    			mHotseat.removeItemByInfo(info);
    		} else {
    			FolderInfo folderInfo = sFolders.get(info.container);
    			if (folderInfo == null) {
    				continue;
    			}
    			folderInfo.remove(info);
    		}    		
    		mModel.removeFromShortcutInfos(info.intent.getComponent());
    	}
    	if (mMainSurface != null) {
    	    mMainSurface.getRenderer().invalidate();
    	}
    }
    
    private void checkMccPackage(List<ApplicationInfo> apps) {
    	if (apps == null) {
    		return;
    	}
    	if (mModel != null) {
        	List<String> packageList = new ArrayList<String>();
        	for (ApplicationInfo info : apps) {
        		String packageName = info.componentName.getPackageName();
        		if (packageList.contains(packageName)) {
        			continue;
        		}
        		packageList.add(packageName);
        		mModel.checkMccPackage(this, packageName, true);
        	}
        }
    }
    //add by zhanggx1 for reordering all pages on 2013-11-20. s
    @Override
    public void autoReorder() {
    	autoReorder(false);
    }
    
    private void autoReorder(final boolean showToast) {
    	boolean autoReorder = SettingsValue.isAutoReorderEnabled(XLauncher.this);
    	if (!autoReorder
    			|| mWorkspace == null
    			/*|| (isInEditMode() && isDockViewShowing)*/) {
    		return;
    	}
    	
    	TaskRunnable runnable = new TaskRunnable() {
			@Override
			public void run() {
				if (isWorkspaceLocked()) {
					return;
				}
				mWorkspace.autoReorder();
		    	
		    	if (showToast) {
		    		Toast.makeText(XLauncher.this, R.string.auto_reorder_finish, Toast.LENGTH_SHORT).show();
		    		//桌面已经整理好啦
		    	}
		    	//add by zhanggx1 for refresh mng view.s
				refreshMngViewOnUpdateWorkspace();
				//add by zhanggx1 for refresh mng view.e
			}

			@Override
			public boolean getAnimFlag() {
				return true;
			}
    		
    	};
    	if (mWorkspace != null
        		&& (mWorkspace.isReordering() || !mPendingRunnableList.isEmpty())) {
//        	mPendingRunnableList.add(runnable);
        } else {
        	mRecorderHandler.post(runnable);
        }
    }
    
	private XWorkspace.ReorderingChangedListener mReorderChangedListener = new XWorkspace.ReorderingChangedListener() {
		@Override
		public void onReorderEnd() {
			android.util.Log.i("dooba",
					"---------->onReorderEnd----------------");
			handlePendingRunnable();
		}
	};

	public void handlePendingRunnable() {
		if (mWorkspace == null || mWorkspace.isReordering()) {
			return;
		}
		boolean isEmpty = mPendingRunnableList.isEmpty();

		Iterator<TaskRunnable> it = mPendingRunnableList.iterator();
		TaskRunnable runnable = null;

		boolean animFlag = false;
		while (it.hasNext()) {
			runnable = it.next();
			mRecorderHandler.post(runnable);
			animFlag = runnable.getAnimFlag();

			it.remove();
			it = mPendingRunnableList.iterator();
			if (animFlag) {
				return;
			}
		}

		if (!isEmpty && !animFlag) {
			mRecorderHandler.post(new Runnable() {
				@Override
				public void run() {
					// add by zhanggx1 for removing on 2013-11-13 . s
					autoReorder();
					// add by zhanggx1 for removing on 2013-11-13 . e
				}

			});
		}
		mPendingRunnableList.clear();
	}

	private static ArrayList<TaskRunnable> mPendingRunnableList = new ArrayList<TaskRunnable>();
	private DeferredHandler mRecorderHandler = new DeferredHandler();

	private static interface TaskRunnable extends Runnable {
		// 标识该TASK执行完毕是否取决于RUN里的动画
		boolean getAnimFlag();
	}
    //add by zhanggx1 for reordering all pages on 2013-11-20. e
}
