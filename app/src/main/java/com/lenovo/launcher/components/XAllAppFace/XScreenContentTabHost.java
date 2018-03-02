package com.lenovo.launcher.components.XAllAppFace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.widget.Toast;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.XDropTarget.XDragObject;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;
import com.lenovo.launcher2.addleoswidget.FetchLenovoWidgetUtil;
import com.lenovo.launcher2.addleoswidget.LenovoWidgetsProviderInfo;
import com.lenovo.launcher2.commoninterface.IconCache;
import com.lenovo.launcher2.commoninterface.ItemInfo;
import com.lenovo.launcher2.commoninterface.LenovoWidgetViewInfo;
import com.lenovo.launcher2.commoninterface.ShortcutInfo;
import com.lenovo.launcher2.customizer.FastBitmapDrawable;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.gadgets.GadgetUtilities;

public class XScreenContentTabHost extends XTabHost implements XTabHost.OnTabChangeListener,
        DrawableItem.OnClickListener, DrawableItem.OnLongClickListener, XDragSource {

    private XLauncher mLauncher;
    private XPagedView xAppsPagedView;
    private XPagedView xWidgetsPagedView;
    private XPagedViewIndicator xPageIndicator;

    private XLoading mLoading;

    private static final String APPS_TAB_TAG = "APPS";
    private static final String WIDGETS_TAB_TAG = "WIDGETS";

    private static final String TAG = "XScreenContentTabHost";

    private int APPS_CELL_X = 4;
    private int APPS_CELL_Y = 3;
    private int WIDGETS_CELL_X = 2;
    private int WIDGETS_CELL_Y = 2;

    private final int INIT_DELAY = 600;

    //for drag by zhanggx1 . S
    private Toast mClickMsg;
    private XDragController mDragController;
    private final int[] mTmpPoint = new int[2];
    private Drawable mTabWidgetDivider;
    private boolean mDividerVisible = true;
    private int mDividerHeight;
    private int mIconPkgSize;
    private List<XScreenShortcutInfo> mCheckedList;
    private boolean mPendingHandle = false;
    private DrawableItem mDragView = null;
    private boolean canBindWidget = false;
    private boolean isSingleLayer = false;
    
    private List<ResolveInfo> mAppInfos = null;
    private ArrayList<Object> mWidgets = null;
    //for drag by zhanggx1 . E

    public XScreenContentTabHost(XContext context, RectF wholeRectF, RectF tabwidgetRect) {
        super(context, wholeRectF, tabwidgetRect);
        
        mTabWidgetDivider = context.getResources().getDrawable(R.drawable.xscreen_tab_widget_divider);
        mDividerHeight = context.getResources().getDimensionPixelSize(R.dimen.xscreen_mng_tab_widget_divider_height);
        mIconPkgSize = context.getResources().getDimensionPixelSize(R.dimen.xscreen_mng_icon_pkg_size);
        
        APPS_CELL_X = context.getResources().getInteger(R.integer.xscreen_tab_content_apps_cellx);
        APPS_CELL_Y = context.getResources().getInteger(R.integer.xscreen_tab_content_apps_celly);
        WIDGETS_CELL_X = context.getResources().getInteger(R.integer.xscreen_tab_content_widgets_cellx);
        WIDGETS_CELL_Y = context.getResources().getInteger(R.integer.xscreen_tab_content_widgets_celly);

        addIndicator();
        if (!SettingsValue.getSingleLayerValue(context.getContext())) {
            addLoading();
        }
    }

    void setup(XLauncher launcher, XDragController dragController, boolean addState) {
        mLauncher = launcher;
        mDragController = dragController;
        if (mCheckedList != null) {
        	mCheckedList.clear();
        }
        canBindWidget = checkBindAppPermission();
        isSingleLayer = SettingsValue.getSingleLayerValue(launcher);
        
//        mHandlerThread = new HandlerThread("TabHost-Thread");
//
//        mHandlerThread.start();

		mHandler = new Handler(/*mHandlerThread.getLooper()*/) {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				
				final int cellX = msg.arg1;
				final int cellY = msg.arg2;
				final ItemInfo info = (ItemInfo)msg.obj;
				
				endAddAnim(cellX, cellY, info);
			}
		};
       
        setupViews(addState);
        mPendingHandle = false;
    }

    /**
     * 加入加载进度条
     */
    private void addLoading() {
        mLoading = new XLoading(getXContext());
        addItem(mLoading);
    }

    /**
     * 初始化页码
     */
    private void addIndicator() {
        initContentPagedViews();

        xPageIndicator = new XPagedViewIndicator(getXContext(), new RectF(0, 0, 0, 0));
        if (!isSingleLayer) {
            xAppsPagedView.addPageSwitchListener(xPageIndicator);
        }
        xWidgetsPagedView.addPageSwitchListener(xPageIndicator);
        xPageIndicator.setPagedView(xWidgetsPagedView);

        addItem(xPageIndicator);
    }

    private void setupViews(final boolean addState) {
        initContentPagedViews();

        TabContentFactory contentFactory = new TabContentFactory() {
            public DrawableItem createTabContent(String tag) {
                if (tag.equals(APPS_TAB_TAG)) {
                    Log.i(TAG, "createTabContent APPS_TAB_TAG *****  ");
                    if (xAppsPagedView.getPageCount() <= 0) {
                        setupContent(xAppsPagedView, APPS_TAB_TAG);
                    }
                    return xAppsPagedView;
                } else if (tag.equals(WIDGETS_TAB_TAG)) {
                    Log.i(TAG, "createTabContent WIDGETS_TAB_TAG *****  ");
                    if (addState && xWidgetsPagedView.getPageCount() <= 0) {
                        setupContent(xWidgetsPagedView, WIDGETS_TAB_TAG);
                    }
                    return xWidgetsPagedView;
                }

                Log.e(TAG, "this tag of content error !!! ");
                return null;
            }
        };
        
        XTabWidget tabWidget = getTabWidget();
        int tabWidgetGap = getContext().getResources().getDimensionPixelOffset(R.dimen.xscreen_mng_tab_gap);
        int tabWidgetPaddingTop = getContext().getResources().getDimensionPixelOffset(R.dimen.xscreen_mng_tab_widget_padding_top);
        int tabWidgetPaddingBottom = getContext().getResources().getDimensionPixelOffset(R.dimen.xscreen_mng_tab_widget_padding_bottom);
        tabWidget.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.xscreen_tab_area_bg));
        tabWidget.setPadding(0, tabWidgetPaddingTop, 0, tabWidgetPaddingBottom);
        tabWidget.setChildrenGap(tabWidgetGap);
        
        String label = getContext().getString(R.string.widgets_tab_label);
        addTab(newTabSpec(WIDGETS_TAB_TAG).setIndicator(createTabWidgetView(label)).setContent(contentFactory));
        setOnTabChangedListener(this);

        if (!isSingleLayer) {
	        label = getContext().getString(R.string.group_applications);
	        addTab(newTabSpec(APPS_TAB_TAG).setIndicator(createTabWidgetView(label)).setContent(contentFactory));
	        setOnTabChangedListener(this);
        }
    }

    /**
     * 初始化内容区域
     * @param content  内容容器（应用的/小部件）
     * @param tag 标识内容类别的字符串（应用/小部件）
     */
    protected void setupContent(final XPagedView content, final String tag) {        
        //加载时，先显示进度条
        if (mLoading != null) {
            mLoading.start();
            mLoading.setVisibility(true);
        }

        if (tag.equals(APPS_TAB_TAG)) {
            //初始化应用
            List<ResolveInfo> infos = getItems(tag);
            setContentItems(infos, content);
            mAppInfos = infos;
        } else if (tag.equals(WIDGETS_TAB_TAG)) {
            //初始化小部件
            ArrayList<Object> widgets = new ArrayList<Object>();
            
            //先加入文件夹
            if (!SettingsValue.getSingleLayerValue(mLauncher)) {
	            Item folder = new Item(R.drawable.xscreen_folder_icon, R.string.add_folder, SimpleItemInfo.ACTION_TYPE_ADD_FOLDER);
	            widgets.add(folder);
            }
            
            //判断是否有绑定小部件的权限，若没有，加入“其它小部件”图标
            if (!canBindWidget) {
	            // 最后加入“其它小部件”
	            Item others = new Item(R.drawable.xscreen_other_widget, R.string.other_widgets, SimpleItemInfo.ACTION_TYPE_ADD_OTHER_WIDGET);
	            widgets.add(others);
            }
                        
            //再加入联想小部件
            ArrayList<LenovoWidgetsProviderInfo> items = new FetchLenovoWidgetUtil(
                    mLauncher.getApplicationContext()).getAllLeosWidgets();
            if (items != null) {
                widgets.addAll(items);
            }
            
            //加入联想快捷方式
            List<ResolveInfo> leosShorts = getLeosShortcuts();
            if (leosShorts != null) {
                widgets.addAll(leosShorts);
            }
            
            //判断若有绑定小部件的权限，则取得所有小部件的列表
            if (canBindWidget) {
	            // 加入其它小部件
	            List<AppWidgetProviderInfo> otherWidgets =
	                    AppWidgetManager.getInstance(mLauncher).getInstalledProviders();
	            widgets.addAll(otherWidgets);
            }
            
            // 加入系统的快捷方式
            List<ResolveInfo> infos = getItems(tag);
            widgets.addAll(infos);
            
            mWidgets = widgets;
            
            // 解析并显示所有内容
            setWidgetItems(widgets, content);
        }

        setDataReady();
    }

    /**
     * 解析并显示所有小部件区域的内容
     * @param widgets  “小部件”的列表 
     * @param content  小部件的显示容器
     */
    private void setWidgetItems(final ArrayList<Object> widgets, final XPagedView content) {
        if (widgets == null || widgets.size() <= 0) {
            return;
        }
        //根据小部件的个数，初始化显示容器
        int size = widgets.size();
        initContent(content, size, WIDGETS_CELL_X, WIDGETS_CELL_Y);

        final XContext context = getXContext();
        final PackageManager pm = mLauncher.getPackageManager();
        final IconCache cache = mLauncher.getIconCache();

        int firstRound = Math.min(WIDGETS_CELL_X * WIDGETS_CELL_Y, size);
        if (SettingsValue.getSingleLayerValue(content.getXContext().getContext())) {
            // load faster.
            firstRound = size;
        }
        for (int index = 0; index < firstRound; index++) {
            addOneWidget(context, widgets, index, pm, cache, content);
        }
        mPendingHandle = false;
        
        //fix bug for pad
        //若取得的当前页小于0，则显示第0页
        if (xWidgetsPagedView != null && xWidgetsPagedView.getCurrentPage() < 0) {
            resetState();
        }
    }

    /**
     * 根据包名和类名创建intent
     * @param packageName  包名
     * @param className  类名
     * @return intent
     */
    private Intent getShortcutIntent(String packageName, String className) {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        if (packageName != null && className != null) {
            // Valid package and class, so fill details as normal intent
            intent.setClassName(packageName, className);
        }
        return intent;
    }

    /**
     * 设置内容区的属性
     * @param content  容器
     * @param size  子项个数
     * @param cellX
     * @param cellY
     */
    private void initContent(XPagedView content, int size, int cellX, int cellY) {
        final int pageChildCount = (cellX * cellY);
        int pageCount = (int) Math.ceil((float) size / pageChildCount);
        content.setup(pageCount, cellX, cellY);
        content.setEnableEffect(false);
        content.setScrollBackEnable(false);
        content.resetSlideAdapter();
    }

    /**
     * 解析并显示所有应用程序
     * @param infos  应用程序列表
     * @param content  容器
     */
    private void setContentItems(final List<ResolveInfo> infos, final XPagedView content) {
        if (infos == null || infos.size() <= 0) {
            return;
        }
        int size = infos.size();
        initContent(content, size, APPS_CELL_X, APPS_CELL_Y);

        final PackageManager pm = mLauncher.getPackageManager();
        final XContext context = getXContext();
        final IconCache cache = mLauncher.getIconCache();

        final int pageCount = APPS_CELL_X * APPS_CELL_Y;
        final int firstRound = Math.min(2 * pageCount, size);
        for (int index = 0; index < firstRound; index++) {
            addOneApp(pm, content, index, context, cache, infos);
        }

        final int lastRound = size - size % pageCount;
        for (int index = lastRound; index < size; index++) {
            addOneApp(pm, content, index, context, cache, infos);
        }

        if (firstRound < lastRound) {
            getXContext().postDelayed(new Runnable() {
                @Override
                public void run() {
                    for (int index = firstRound; index < lastRound; index++) {
                        addOneApp(pm, content, index, context, cache, infos);
                    }
                }

            }, INIT_DELAY);
        }
    }

    /**
     * 解析并显示一个应用
     * @param pm  PackageManager对象
     * @param content  应用容器
     * @param index  应用索引值
     * @param context  XContext
     * @param cache  IconCache对象
     * @param infos  应用列表
     */
    private void addOneApp(PackageManager pm, XPagedView content, int index, XContext context,
            IconCache cache, List<ResolveInfo> infos) {
        ResolveInfo resolveInfo = infos.get(index);
        Intent intent = getShortcutIntent(resolveInfo.activityInfo.packageName,
                resolveInfo.activityInfo.name);
        //创建ShortcutInfo
        ShortcutInfo info = mLauncher.getModel().getShortcutInfo(pm, intent, mLauncher);
        if (info == null) {
            return;
        }
        //设置ShortcutInfo的intent
        info.setActivity(intent.getComponent(), Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        //根据索引值，行列数计算位置
        setItemInfoPosition(info, index, APPS_CELL_X, APPS_CELL_Y);

        XScreenShortcutInfo csi = new XScreenShortcutInfo(info);

        //创建VIEW
        final XIconTextView target = new XIconTextView(csi, new RectF(0, 0, content.getCellWidth(),
                content.getCellHeight()), cache, context);

        //设置应用图标的事件
        XIconDrawable iconView = target.getIconDrawable();
        iconView.setTag(csi);
        iconView.setOnLongClickListener(XScreenContentTabHost.this);
        iconView.setOnClickListener(XScreenContentTabHost.this);

        XPagedViewItem itemToAdd = new XPagedViewItem(getXContext(), target, csi);
        content.addPagedViewItem(itemToAdd);
    }
    
//    private List<ResolveInfo> getItems(String tag) {
//        PackageManager packageManager = mLauncher.getPackageManager();
//        Intent mainIntent = null;
//        if (tag.equals(APPS_TAB_TAG)) {
//            mainIntent = new Intent(Intent.ACTION_MAIN, null);
//            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//        } else if (tag.equals(WIDGETS_TAB_TAG)) {
//        	mainIntent = new Intent("android.intent.action.LE_SHORTCUT", null);
//        	mainIntent.addCategory(Intent.CATEGORY_DEFAULT);
//        	mainIntent.setPackage("com.lenovo.launcher");
//        }
//        final List<ResolveInfo> infolist = packageManager.queryIntentActivities(mainIntent, 0);
//        if (tag.equals(APPS_TAB_TAG)) {
//            List<ResolveInfo> tmpList = new ArrayList<ResolveInfo>();
//            for (ResolveInfo info : infolist) {
//                if (info.activityInfo.packageName.startsWith("com.lenovo.launcher")
//                        || info.activityInfo.packageName
//                                .startsWith(SettingsValue.THEME_PACKAGE_NAME_PREF)
//                        || info.activityInfo.packageName
//                                .startsWith(SettingsValue.THEME_PACKAGE_QIGAMELOCKSCREEN_PREF)) {
//                    tmpList.add(info);
//                }
//            }
//            infolist.removeAll(tmpList);
//        }
//        Collections.sort(infolist, new ResolveInfo.DisplayNameComparator(packageManager));
//
//        return infolist;
//    }
    
    /**
     * 取得联想快捷方式
     * @return 快捷方式列表
     */
    private List<ResolveInfo> getLeosShortcuts() {
    	PackageManager packageManager = mLauncher.getPackageManager();
    	Intent leShortcutIntent = new Intent("android.intent.action.LE_SHORTCUT", null);
        leShortcutIntent.addCategory(Intent.CATEGORY_DEFAULT);
        leShortcutIntent.setPackage(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF);
        final List<ResolveInfo> listLeos = packageManager.queryIntentActivities(
                leShortcutIntent, 0);
        //设备若为手机，则过滤一些快捷方式，例如壁纸
        if (SettingsValue.getCurrentMachineType(mLauncher) == -1) {
        	filterShortcut(listLeos);
        }
        
        return listLeos;
    }

    /**
     * 根据标签名查询所有应用/系统快捷方式的列表
     * @param tag  标签名（应用/小部件）
     * @return  列表
     */
    private List<ResolveInfo> getItems(String tag) {
        PackageManager packageManager = mLauncher.getPackageManager();
        Intent mainIntent = null;
        //若标签名指定应用，则查询所有的应用列表，否则查询所有的快捷方式列表
        if (tag.equals(APPS_TAB_TAG)) {
            mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        } else if (tag.equals(WIDGETS_TAB_TAG)) {
            mainIntent = new Intent(Intent.ACTION_CREATE_SHORTCUT);
        }
        final List<ResolveInfo> infolist = packageManager.queryIntentActivities(mainIntent, 0);
        // 过滤应用
        if (tag.equals(APPS_TAB_TAG)) {
            List<ResolveInfo> tmpList = new ArrayList<ResolveInfo>();
            for (ResolveInfo info : infolist) {
                if (info.activityInfo.packageName.startsWith(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF)
                        || info.activityInfo.packageName
                                .startsWith(SettingsValue.THEME_PACKAGE_NAME_PREF)
                        || info.activityInfo.packageName
                                .startsWith(SettingsValue.THEME_PACKAGE_QIGAMELOCKSCREEN_PREF)) {
                    tmpList.add(info);
                }
            }
            infolist.removeAll(tmpList);
        } else {
        	//过滤快捷方式
            filterShortcut(infolist);
        }
        //按图标名称排序
        Collections.sort(infolist, new ResolveInfo.DisplayNameComparator(packageManager));
//        if (tag.equals(WIDGETS_TAB_TAG)) {
//            Intent leShortcutIntent = new Intent("android.intent.action.LE_SHORTCUT", null);
//            leShortcutIntent.addCategory(Intent.CATEGORY_DEFAULT);
//            leShortcutIntent.setPackage(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF);
//            final List<ResolveInfo> listLeos = packageManager.queryIntentActivities(
//                    leShortcutIntent, 0);
//            if (SettingsValue.getCurrentMachineType(mLauncher) == -1) {
//            	filterShortcut(listLeos);
//            }
//            
//            if (listLeos != null) {
//                infolist.addAll(0, listLeos);
//            }
//        }

        return infolist;
    }

    /**
     * 解析并显示一个小部件
     * @param context  XContext
     * @param widgets  小部件列表
     * @param index  索引值
     * @param pm  PackageManager对象
     * @param cache  IconCache对象
     * @param content  容器
     */
    private void addOneWidget(XContext context, ArrayList<Object> widgets, int index,
            PackageManager pm, IconCache cache, XPagedView content) {
//        Log.i(TAG, "addOneWidget === index============" + index);
        Object widgetInfo = widgets.get(index);

        ItemInfo info = new ItemInfo();

        //根据widgetInfo创建一个XWidgetTextView对象
        final XWidgetTextView target = new XWidgetTextView(context, new RectF(0, 0,
                content.getCellWidth(), content.getCellHeight()));
        //若当前为联想小部件
        if (widgetInfo instanceof LenovoWidgetsProviderInfo) {
            LenovoWidgetsProviderInfo leosInfo = (LenovoWidgetsProviderInfo) widgetInfo;
            target.setup(leosInfo);

            info = getLenovoWidgetInfo(leosInfo);
        //若当前为系统快捷方式
        } else if (widgetInfo instanceof ResolveInfo) {
            ResolveInfo resolveInfo = (ResolveInfo) widgets.get(index);

            SimpleItemInfo itemInfo = new SimpleItemInfo(SimpleItemInfo.ACTION_TYPE_ADD_SHORTCUT,
                    resolveInfo);
            setItemInfoPosition(info, index, APPS_CELL_X, APPS_CELL_Y);

            ComponentName componentName = new ComponentName(resolveInfo.activityInfo.packageName,
                    resolveInfo.activityInfo.name);
            cache.remove(componentName);
            target.setup(itemInfo, cache, pm);

            info = itemInfo;
        //若当前为“文件夹”或“其它小部件”
        } else if (widgetInfo instanceof Item) {
            Item otherInfo = (Item) widgetInfo;
            target.setup(otherInfo.icon, otherInfo.label, otherInfo.type);

            info = new SimpleItemInfo(otherInfo.type, null);
        //若当前为系统小部件
        } else if (widgetInfo instanceof AppWidgetProviderInfo) {
        	AppWidgetProviderInfo appWidgetInfo = (AppWidgetProviderInfo) widgetInfo;
        	int[] spanXY = mLauncher.getSpanForWidget(appWidgetInfo, null);
            target.setup(appWidgetInfo, spanXY);

            info = getAppWidgetInfo(appWidgetInfo, spanXY);
        }
        setItemInfoPosition(info, index, WIDGETS_CELL_X, WIDGETS_CELL_Y);

        XWidgetIconDrawable iconView = target.getIconDrawable();
        iconView.setTag(info);
        iconView.setOnLongClickListener(XScreenContentTabHost.this);
        iconView.setOnClickListener(XScreenContentTabHost.this);

        XPagedViewItem itemToAdd = new XPagedViewItem(getXContext(), target, info);
        content.addPagedViewItem(itemToAdd);
    }

    /**
     * 根据LenovoWidgetsProviderInfo对象，得到LenovoWidgetViewInfo对象
     * @param item
     * @return
     */
    private LenovoWidgetViewInfo getLenovoWidgetInfo(LenovoWidgetsProviderInfo item) {
        LenovoWidgetViewInfo lenovoWidget = new LenovoWidgetViewInfo();
        //类名
        lenovoWidget.className = item.widgetView;
        //包名
        lenovoWidget.packageName = item.appPackageName;
        ComponentName component = new ComponentName(lenovoWidget.packageName,
                lenovoWidget.className);
        lenovoWidget.componentName = component;
        //实际所占的spanX
        lenovoWidget.minWidth = item.x;
        //实际所占的spanY
        lenovoWidget.minHeight = item.y;
        //默认图标
        lenovoWidget.previewImage = R.drawable.lotus_icon;
        return lenovoWidget;
    }
   
    /**
     * 根据AppWidgetProviderInfo得到SimpleItemInfo
     * @param item
     * @param spanXY
     * @return
     */
    private SimpleItemInfo getAppWidgetInfo(AppWidgetProviderInfo item, int[] spanXY) {
    	SimpleItemInfo lenovoWidget = new SimpleItemInfo(item, SimpleItemInfo.ACTION_TYPE_CREATE_WIDGET, spanXY);
        return lenovoWidget;
    }

    /**
     * 设置子项的位置
     * @param info  
     * @param index
     * @param cellX
     * @param cellY
     */
    private void setItemInfoPosition(ItemInfo info, int index, int cellX, int cellY) {
        final int pageChildCount = (cellX * cellY);
        info.screen = (index / pageChildCount);
        info.cellX = (index % pageChildCount) % cellX;
        info.cellY = (index % pageChildCount) / cellX;
        info.spanX = info.spanY = 1;
    }

    /**
     * 添加剩余的小部件
     * @param context
     * @param widgets
     * @param pm
     * @param cache
     * @param content
     */
    protected void addLeftWidget(XContext context, ArrayList<Object> widgets, PackageManager pm,
            IconCache cache, XPagedView content) {
        int start = WIDGETS_CELL_X * WIDGETS_CELL_Y;
        int end = widgets.size();
        for (int index = start; index < end; index++) {
            addOneWidget(context, widgets, index, pm, cache, content);
        }
    }

    private void initContentPagedViews() {
        XContext context = getXContext();
        if (!isSingleLayer) {
	        if (xAppsPagedView == null) {
	            xAppsPagedView = new XPagedView(context, new RectF(0, 0, 0, 0));
	            xAppsPagedView.setVisibility(false);
	        }
        }
        if (xWidgetsPagedView == null) {
            xWidgetsPagedView = new XPagedView(context, new RectF(0, 0, 0, 0));
            xWidgetsPagedView.setVisibility(false);
        }
    }

    @Override
    public void onTabChanged(String tabId) {
        final String tab = tabId;

        getXContext().post(new Runnable() {

            @Override
            public void run() {
                if (tab.equals(APPS_TAB_TAG)) {
                    Log.i(TAG, "onTabChanged APPS_TAB_TAG ========== ");
                    xAppsPagedView.updateIndicator();
                    xAppsPagedView.setVisibility(true);
                    xWidgetsPagedView.setVisibility(false);
                } else if (tab.equals(WIDGETS_TAB_TAG)) {
                    Log.i(TAG, "onTabChanged WIDGETS_TAB_TAG  ========== ");
                    xAppsPagedView.setVisibility(false);
                    xWidgetsPagedView.updateIndicator();
                    xWidgetsPagedView.setVisibility(true);
                }

                invalidate();
            }

        });
    }

    @Override
    public void resize(RectF rect, RectF rectWidget) {
        super.resize(rect, rectWidget);
        Log.i(TAG, "XScreenContentTabHost resize....");

        initContentPagedViews();
        RectF contentRect = new RectF(0, rectWidget.height(), rect.width(), rect.height());
        final float width = contentRect.width();
        final float height = contentRect.height();
         // zhanggx1
        Resources res = getContext().getResources();
        int widgetsPaddingLeft = res.getDimensionPixelOffset(R.dimen.xscreen_mng_tab_widgets_padding_left);
        int widgetsPaddingTop = res.getDimensionPixelOffset(R.dimen.xscreen_mng_tab_widgets_padding_top);
        int widgetsPaddingRight = res.getDimensionPixelOffset(R.dimen.xscreen_mng_tab_widgets_padding_right);
        int widgetsPaddingBottom = res.getDimensionPixelOffset(R.dimen.xscreen_mng_tab_widgets_padding_bottom);
        xWidgetsPagedView.resize(new RectF(widgetsPaddingLeft, widgetsPaddingTop, 
        		width - widgetsPaddingRight, height -widgetsPaddingBottom));
                
        int textSize = res.getDimensionPixelSize(R.dimen.xscreen_mng_tab_apps_text_size);
        int imagePaddingTop = res.getDimensionPixelOffset(R.dimen.xscreen_mng_tab_apps_image_padding_top);
        int iconSize = res.getDimensionPixelSize(R.dimen.app_icon_size);
        int appsPaddingLeft = res.getDimensionPixelOffset(R.dimen.xscreen_mng_tab_apps_padding_left);
        int appsPaddingTop = res.getDimensionPixelOffset(R.dimen.xscreen_mng_tab_apps_padding_top);
        int appsPaddingRight = res.getDimensionPixelOffset(R.dimen.xscreen_mng_tab_apps_padding_right);
        int appsPaddingBottom = res.getDimensionPixelOffset(R.dimen.xscreen_mng_tab_apps_padding_bottom);
        int minSize = APPS_CELL_Y * (textSize + iconSize + imagePaddingTop);
        boolean resetHeight = false;
        if ((height - appsPaddingTop - appsPaddingBottom) <= minSize) {
        	appsPaddingTop = 0;
        	resetHeight = true;
        }
        if (!isSingleLayer) {
	        xAppsPagedView.resize(new RectF(appsPaddingLeft, appsPaddingTop, 
	        		width - appsPaddingRight, height -appsPaddingBottom));
        }
        
        if (xPageIndicator != null) {
        	int pageHeight = res.getDimensionPixelOffset(R.dimen.xscreen_mng_tab_indicator_region_height);
        	pageHeight = resetHeight ? (pageHeight * 2) / 3 : pageHeight;
        	//modify for quick drag mode by sunzq3, begin;
        	int pagePaddingBottom = pageHeight - xPageIndicator.getHomePointHeight();
        	xPageIndicator.resize(new RectF(0, 0, getWidth(),
                    xPageIndicator.getHomePointHeight()));
        	xPageIndicator.setRelativeY(rect.height() - pagePaddingBottom - xPageIndicator.getHomePointHeight());
        	//modify for quick drag mode by sunzq3, end;
        }

        if (mLoading != null) {
            mLoading.setRelativeX(getWidth() / 2 - mLoading.getWidth() / 2);
            mLoading.setRelativeY(getHeight() / 2 - mLoading.getHeight() / 2);
//            mLoading.start();
        }
    }

    /**
     * 数据填充完毕后，关闭进度条
     */
    void setDataReady() {
        if (mLoading != null) {
            mLoading.stop();
            mLoading.setVisibility(false);
        }
        setTouchable(true);
    }

    static class Item {
        public Item(int drawableID, int labelID, int actionType) {
            label = labelID;
            icon = drawableID;
            type = actionType;
        }

        int label;
        int icon;
        int type;
    }

    /**
     * 重组内容区
     */
    void updateAllItems() {
        initContentPagedViews();

        if (!isSingleLayer) {
	        xAppsPagedView.clearAllItems();
	        xAppsPagedView.addPageSwitchListener(xPageIndicator);
	        setupContent(xAppsPagedView, APPS_TAB_TAG);
        }
        
        xWidgetsPagedView.clearAllItems();
        xWidgetsPagedView.addPageSwitchListener(xPageIndicator);
        setupContent(xWidgetsPagedView, WIDGETS_TAB_TAG);
    }
    
    void updateAllItemsWithOldData() {
    	initContentPagedViews();

        if (!isSingleLayer) {
	        xAppsPagedView.clearAllItems();
	        xAppsPagedView.addPageSwitchListener(xPageIndicator);
	        setContentItems(mAppInfos, xAppsPagedView);
        }
        
        xWidgetsPagedView.clearAllItems();
        xWidgetsPagedView.addPageSwitchListener(xPageIndicator);
        setWidgetItems(mWidgets, xWidgetsPagedView);
    }

    @Override
    public void clean() {
        super.clean();
        clearAllTabs();
        if (!isSingleLayer) {
            destoryPagedView(xAppsPagedView);
        }
        destoryPagedView(xWidgetsPagedView);

        mPendingHandle = false;
        mDragView = null;
        if (mAppInfos != null) {
        	mAppInfos.clear();
        	mAppInfos = null;
        }
        if (mWidgets != null) {
        	mWidgets.clear();
        	mWidgets = null;
        }
//        mHandlerThread.getLooper().quit();
    }

    private void destoryPagedView(XPagedView view) {
        if (view != null) {
            view.destory();
            view = null;
        }
    }

    @Override
	public void onDropCompleted(DrawableItem target, XDragObject d,
			boolean success) {    	
    	
//    	if (mCheckedList != null) {
//	    	for (XScreenShortcutInfo info : mCheckedList) {
//	    		info.checked = !info.checked;
//	    		XPagedViewItem item = xAppsPagedView.findPageItemAt(info.screen, info.cellX, info.cellY);
//	    		if (item != null && item.getDrawingTarget() != null) {
//	    			((XIconTextView)item.getDrawingTarget()).setChecked(info.checked);
//	    		}
//	    	}
//	    	if (target instanceof XScreenMngView) {
//	    		mCheckedList.clear();
//	    	}
//    	}
    	if (mDragView != null) {
    		if (mDragView instanceof XIconDrawable) {
    			((XIconDrawable)mDragView).releaseLongPressed();
    		} else if (mDragView instanceof XWidgetIconDrawable) {
    			((XWidgetIconDrawable)mDragView).releaseLongPressed();
    		}
    	}
        mLauncher.getDragLayer().showPendulumAnim(d, target);
      //add for quick drag mode by sunzq3, begin;
        ((XLauncherView)mContext).getWorkspace().getPageIndicator().startNormalAnimation();
      //add for quick drag mode by sunzq3, end;
    }

	@Override
	public boolean onLongClick(DrawableItem item) {
		if (item == null || item.getTag() == null) {
			return true;
		}
		Object obj = item.getTag();
		if (!(obj instanceof ItemInfo)) {
		    return true;
		}
		
		if (obj instanceof ShortcutInfo)
        {
            ShortcutInfo shortcutInfo = (ShortcutInfo)obj;
            ComponentName componentName = shortcutInfo.intent.getComponent();
            
            if (componentName != null && mLauncher.getModel().getAllAppsList().isNewAddApk(mLauncher, componentName.flattenToString()))
            {
                mLauncher.clearAndShowNewBg(componentName.flattenToString());
            }                   
        }	
				
		//屏幕编辑的应用多选功能
		if (mCheckedList != null 
				&& mCheckedList.size() > 1
				&& mCheckedList.indexOf(item.getTag()) != -1) {
			XScreenShortcutInfo info = (XScreenShortcutInfo)obj;
			mCheckedList.remove(info);
			mCheckedList.add(0, info);
			XScreenIconPkgView iconPkg = new XScreenIconPkgView(getXContext(), 
					mCheckedList, 
					new RectF(0, 0, mIconPkgSize, mIconPkgSize));
			iconPkg.setTag(mCheckedList);
			beginDragShared(iconPkg, item);
		} else {
			//屏幕编辑的应用单选功能
			mDragView = item;
		    beginDragShared(item, item);
		}
       return true;
	}

	@Override
	public void onClick(final DrawableItem item) {
//	    if (mLauncher.isWidgetConfiged()) {
//	        // fix bug 5073
//	        Log.w(TAG, "widget is configured, so pass this click event.");
//	        return;
//	    }
		// for multi check by zhanggx1
//		onMultiClick(item);
//		return;
		
		if (mPendingHandle) {
			return;
		}
		
		// for single click
		onSingleClick(item);
	}
	
	/**
	 * 用于屏幕应用的多选功能
	 * @param view
	 */
	private void checkedItem(DrawableItem view) {
		if (view == null || view.getTag() == null || view.getParent() == null) {
			return;
		}
		XScreenShortcutInfo info = (XScreenShortcutInfo)view.getTag();
		info.checked = !info.checked;
		((XIconTextView)view.getParent()).setChecked(info.checked);
		
		if (mCheckedList == null) {
			mCheckedList = new ArrayList<XScreenShortcutInfo>();
		}
		if (info.checked) {
			mCheckedList.add(info);
		} else {
			mCheckedList.remove(info);
		}
	}
	
	private Runnable mClickMsgRunnable = new Runnable() {
		@Override
		public void run() {
			if(mClickMsg == null) {
				mClickMsg = Toast.makeText(mLauncher, R.string.xscreen_item_click_msg,
					Toast.LENGTH_SHORT);
			} else{
				mClickMsg.setText(R.string.xscreen_item_click_msg);
			}
			mClickMsg.show();
			
		}				
	};
	
	/**
	 * 长按拖动一个应用
	 * @param child  被拖动的物体
	 * @param positionChild  拖起的位置。之所以又加个这个参数，因为多选时，拖动的物体根本不是内容区本身的子项
	 */
	private void beginDragShared(DrawableItem child, DrawableItem positionChild) {
		mLauncher.getDragLayer().getLocationInDragLayer(positionChild == null ? child : positionChild, mTmpPoint);
		
		child.setAlpha(1);
		
		int dragLayerX = mTmpPoint[0];
        int dragLayerY = mTmpPoint[1]; 
        Bitmap bitmap;
        if (child instanceof XIconDrawable) {
            bitmap = ((XIconDrawable)child).iconBitmap;
            dragLayerX = mTmpPoint[0] + (int)(((XIconDrawable)child).getBitmapX());
            dragLayerY = mTmpPoint[1] + (int)(((XIconDrawable)child).getBitmapY());
        } else {
            bitmap = child.getSnapshot(1f);
        }
        mDragController.startDrag(bitmap, dragLayerX, dragLayerY, this, child.getTag(), SettingsValue.DRAG_ACTION_MOVE, null, null, false);
        child.setAlpha(.6f);
	}
	
	/**
	 * 初始化标签卡区的文字
	 * @param label
	 * @return
	 */
	private XTextArea createTabWidgetView(String label) {
		Resources res = getContext().getResources();
		int textSize = res.getDimensionPixelSize(R.dimen.xscreen_mng_tab_text_size);
		Drawable tabSelector = res.getDrawable(R.drawable.xscreen_tab_widget_indicator_selector);
		
        final XTextArea tv = new XTextArea(getXContext(), label, new RectF());
        tv.setTextAlign(Align.CENTER);
        tv.setTextColor(0xffffffff);        
        tv.setTextSize(textSize);        
        tv.setBackgroundDrawable(tabSelector);
        tv.enableCache();
        return tv;
    }
	
	@Override
	public void onDraw(IDisplayProcess canvas) {
		//若标签卡之间有分割线，绘制分割线
		if (this.mDividerVisible) {
			int cnt = getTabWidget().getChildCount();
			int dividerWidth = mTabWidgetDivider.getIntrinsicWidth();
			float width = getTabWidget().getWidth() / cnt;
			float paddingVertical = (getTabWidget().getHeight() - mDividerHeight) / 2.0f;
			
			canvas.save();
			for (int i = 1; i < cnt; i++) {
				float middle = i * width;
				float left = middle - dividerWidth / 2.0f;
				canvas.drawDrawable(mTabWidgetDivider, 
						new RectF(left, paddingVertical, left + dividerWidth, paddingVertical + mDividerHeight));
			}
			canvas.restore();
		}
		super.onDraw(canvas);
	}
	
	/**
	 * 多选操作时的点击事件
	 * @param item
	 */
	private void onMultiClick(final DrawableItem item) {
		if (item != null
				&& item.getTag() != null && item.getTag() instanceof XScreenShortcutInfo) {
			checkedItem(item);			
		} else {
		    getXContext().removeCallbacks(mClickMsgRunnable);
		    getXContext().post(mClickMsgRunnable);
		}
	}
	
	/**
	 * 单选操作时的点击事件
	 * @param item
	 */
	private void onSingleClick(final DrawableItem item) {
		if (item == null || item.getTag() == null) {
			return;
		}
		
		mPendingHandle = true;
		
		final XDragLayer dragLayer = mLauncher.getDragLayer();
		final XWorkspace workspace = mLauncher.getWorkspace();
		
		final ItemInfo info = (ItemInfo)item.getTag();
		int spanX = info.spanX;
		int spanY = info.spanY;
		if (info instanceof LenovoWidgetViewInfo) {
			spanX = ((LenovoWidgetViewInfo)info).minWidth;
			spanY = ((LenovoWidgetViewInfo)info).minHeight;
		} else if (info.itemType == SimpleItemInfo.ACTION_TYPE_CREATE_WIDGET) {
			spanX = ((SimpleItemInfo)info).spanXY[0];
			spanY = ((SimpleItemInfo)info).spanXY[1];
		}
		if (spanX < 0) {
			spanX = 1;
		}
		if (spanY < 0) {
			spanY = 1;
		}
		//查询当前桌面是否已满
		int[] targetCell = new int[2];
		//查找界面上第一个空位
		targetCell = workspace.getPagedView().findFirstVacantCell(workspace.getCurrentPage(), spanX, spanY);
		if (targetCell == null
				|| targetCell[0] < 0
				|| targetCell[1] < 0) {
			mLauncher.showOutOfSpaceMessage();
			mPendingHandle = false;
			return;
		}		
		
		if (info instanceof ShortcutInfo)
		{
		    ShortcutInfo shortcutInfo = (ShortcutInfo)info;
		    ComponentName componentName = shortcutInfo.intent.getComponent();
		    
		    if (componentName != null && mLauncher.getModel().getAllAppsList().isNewAddApk(mLauncher, componentName.flattenToString()))
            {
                mLauncher.clearAndShowNewBg(componentName.flattenToString());
            }
	          
            if (componentName != null)
            {
                String str = Settings.System.getString(mLauncher.getContentResolver(),"NEWMSG_" + componentName.flattenToString());
                if (str != null && !str.isEmpty())
                {
                    shortcutInfo.updateInfo(str);
                }                                          
            }	    

		}
		
		//以下皆为点击一个WIDGET/应用的动画
		final ValueAnimator anim = initAppAddAnimation(dragLayer, workspace, item, info, targetCell, spanX, spanY);
		if (anim != null) {
		    this.getXContext().getRenderer().injectAnimation(anim, false);
		}
	}
	
	/**
	 * 初始化一个应用点击的动画
	 * @param dragLayer
	 * @param workspace
	 * @param item
	 * @param info
	 * @param spanY
	 * @param spanX
	 * @return
	 */
	private ValueAnimator initAppAddAnimation(final XDragLayer dragLayer,
			final XWorkspace workspace, final DrawableItem item, final ItemInfo info, final int[] targetCell, int spanX, int spanY) {
		if (dragLayer == null
				|| workspace == null
				|| item == null
				|| info == null
				|| targetCell == null
				|| mLauncher == null
				|| mLauncher.getXScreenMngView() == null) {
			mPendingHandle = false;
			return null;
		}
		
		// 计算所点击应用的X，Y坐标
	    mTmpPoint[0] = (int) (item.getWidth() / 2);
	    mTmpPoint[1] = (int) (item.getHeight() / 2);
		dragLayer.getDescendantCoordRelativeToSelf(item, mTmpPoint);
		final int dragLayerX = mTmpPoint[0];
        final int dragLayerY = mTmpPoint[1];
		
        final boolean createNew = !(item instanceof XIconDrawable);
		final Bitmap bitmap = createNew ? item.getSnapshot(1f) : ((XIconDrawable)item).iconBitmap;
		
		if (bitmap == null) {
			mPendingHandle = false;
			return null;
		}
		
		// 创建所点击图标的副本
		final DrawableItem snap = new DrawableItem(getXContext());
		snap.setBackgroundDrawable(new FastBitmapDrawable(bitmap));
		
		final int width = bitmap.getWidth();
		final int height = bitmap.getHeight();
		
		// 将副本添加到应用的位置，与应用重叠
		snap.resize(new RectF(0, 0, width, height));
		snap.setRelativeX(dragLayerX - width / 2);
		snap.setRelativeY(dragLayerY - height / 2);
		dragLayer.addItem(snap);
		
		// 计算副本的初始中心点坐标
		final float startCenterX = snap.getRelativeX() + width / 2.0f;
		final float startCenterY = snap.getRelativeY() + height / 2.0f;

        // calculate target cell coordinate
        Log.i(TAG, "startX ===" + startCenterX + "   startCenterY ===" + startCenterY);
        int targetXY[] = mLauncher.getXScreenMngView().initTargetCellCoord(workspace, targetCell,
                spanX, spanY);

		// 计算副本的移动距离
		final float deltaX = targetXY[0]/*dragLayer.getWidth() / 2.0f*/ - startCenterX;
        final float deltaY = targetXY[1]/*mLauncher.getXScreenMngView().getHomeHeight() / 2.0f*/ - startCenterY;
		
		final ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
		anim.setDuration(150L);
		anim.setStartDelay(0);
		anim.setInterpolator(new AccelerateInterpolator());//AccelerateDecelerateInterpolator
		anim.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {				
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				if (mLauncher == null
						|| workspace == null
						|| dragLayer == null
						|| info == null) {
					mPendingHandle = false;
					return;
				}
				// 从屏幕上移除副本
				dragLayer.removeItem(snap);
				snap.invalidate();
				if (createNew) {
				    bitmap.recycle();
				}
				dragLayer.invalidateAtOnce();
				
				mHandler.sendMessage(mHandler.obtainMessage(0, targetCell[0], targetCell[1], info));
				
//				endAddAnim(targetCell[0], targetCell[1], info);				
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				// 从屏幕上移除副本
				dragLayer.removeItem(snap);
				snap.invalidate();
				if (createNew) {
				    bitmap.recycle();
				}
				mPendingHandle = false;
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}
		});
		
		anim.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				final float value = (Float) animation.getAnimatedValue();
				
				float deltaScale = 1.0f - value * 0.5f;
				
				Matrix matrix = snap.getMatrix();
				
				matrix.reset();
				matrix.setScale(deltaScale, deltaScale, snap.localRect.centerX(), snap.localRect.centerY());
				matrix.postTranslate(value * deltaX, value * deltaY);
			}
		});
		return anim;
	}
	
	private void endAddAnim(final int cellX, final int cellY, final ItemInfo info) {
		if (mLauncher == null) {
			mPendingHandle = false;
			return;
		}
		final XWorkspace workspace = mLauncher.getWorkspace();
		int[] touchXY = new int[2];
		
		touchXY[0] = cellX * workspace.getPagedView().getCellWidth() + 1;
		touchXY[1] = cellY * workspace.getPagedView().getCellHeight() + 1;
		//将点击的内容添加入桌面
		workspace.addExternalItemInfo(touchXY, info, true, null);
		
		mPendingHandle = false;
		//刷新屏幕编辑的缩略图
		if (info instanceof LenovoWidgetViewInfo
				&& ((LenovoWidgetViewInfo)info).componentName != null
        		&& (GadgetUtilities.LOTUSDEFAULTVIEWHELPER.equals(((LenovoWidgetViewInfo)info).componentName.getClassName())
        				|| ((LenovoWidgetViewInfo)info).componentName.getClassName().startsWith("com.lenovo.launcher2.weather."))) {
			mLauncher.refreshMngViewDelayed(800L, workspace.getCurrentPage());
        } else {
        	mLauncher.refreshMngView(workspace.getCurrentPage());
        }
	}
	
	/**
	 * 设置标签卡的初始状态
	 */
	void resetState() {
		if (!isSingleLayer) {
			setCurrentTabByTag(WIDGETS_TAB_TAG);
			xAppsPagedView.setCurrentPage(0);
		}
        xWidgetsPagedView.setCurrentPage(0);
	}
	public void setCurrentPage(int[] page) {
		if (!isSingleLayer) {
			setCurrentTabByTag(WIDGETS_TAB_TAG);
			xAppsPagedView.setCurrentPage(page[0]);
		}
		xWidgetsPagedView.setCurrentPage(page[1]);
	}
	public int[] getCurrentPage() {
		int[] current = new int[2];
		current[0] = isSingleLayer ? 0 : xAppsPagedView.getCurrentPage();
		current[1] = xWidgetsPagedView.getCurrentPage();
		return current;
	}
	
	public int[] getPageCount() {
		int[] count = new int[2];
		count[0] = isSingleLayer ? 0 : xAppsPagedView.getPageCount();
		count[1] = xWidgetsPagedView.getPageCount();
		return count;
	}
	/**
	 * 过滤非系统应用的快捷方式
	 * @param infolist
	 */
	private void filterShortcut(List<ResolveInfo> infolist) {
    	if (infolist == null) {
    		return;
    	}
        List<ResolveInfo> remove = new ArrayList<ResolveInfo>();
    	for (ResolveInfo info : infolist) {
    		if (!isSystemApp(info)) {
    			remove.add(info);
    		}
    	}
    	infolist.removeAll(remove);
    }
	
	/**
	 * 判断是否为系统应用
	 * @param info
	 * @return
	 */
	private boolean isSystemApp(ResolveInfo info) {
    	if (info == null
    			|| info.activityInfo == null
    			|| info.activityInfo.applicationInfo == null) {
    		return true;
    	}
		boolean ret = false;
		ApplicationInfo ai = info.activityInfo.applicationInfo;
		if (info.activityInfo.name != null
				&& "com.lenovo.launcher2.settings.ThemeShortCut".equals(info.activityInfo.name)) {
			return false;
		}
		
		if (info.activityInfo.packageName.equals(mLauncher.getPackageName())) {
			return true;
		}
		ret = (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0
				|| (ai.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0;
		return ret;
	}
	
	/**
	 * 判断是否有绑定小部件的权限
	 * @return
	 */
	private boolean checkBindAppPermission() {
		int checkResult = mLauncher.getPackageManager()
				.checkPermission("android.permission.BIND_APPWIDGET", mLauncher.getPackageName());
		return checkResult == PackageManager.PERMISSION_GRANTED;
	}

    void reset() {
        if (xAppsPagedView != null && xAppsPagedView.isVisible()) {
            xAppsPagedView.resetAnim();
        } else if (xWidgetsPagedView != null && xWidgetsPagedView.isVisible()) {
            xWidgetsPagedView.resetAnim();
        }
    }
    
    /**
     * 显示小部件的内容
     */
    void addWidgetContent() {
    	if (xWidgetsPagedView.getPageCount() <= 0) {
            setupContent(xWidgetsPagedView, WIDGETS_TAB_TAG);
        }
    }
    
    void onConfigurationChange() {
    	mPendingHandle = true;
    	final Context context = getXContext().getContext();
    	mTabWidgetDivider = context.getResources().getDrawable(R.drawable.xscreen_tab_widget_divider);
        mDividerHeight = context.getResources().getDimensionPixelSize(R.dimen.xscreen_mng_tab_widget_divider_height);
        mIconPkgSize = context.getResources().getDimensionPixelSize(R.dimen.xscreen_mng_icon_pkg_size);
        
        APPS_CELL_X = context.getResources().getInteger(R.integer.xscreen_tab_content_apps_cellx);
        APPS_CELL_Y = context.getResources().getInteger(R.integer.xscreen_tab_content_apps_celly);
        WIDGETS_CELL_X = context.getResources().getInteger(R.integer.xscreen_tab_content_widgets_cellx);
        WIDGETS_CELL_Y = context.getResources().getInteger(R.integer.xscreen_tab_content_widgets_celly);
        
        updateAllItemsWithOldData();
    }
    
//    private HandlerThread mHandlerThread;
	private Handler mHandler;
}
