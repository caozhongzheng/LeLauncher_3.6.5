package com.lenovo.launcher2.customizer;

public class Constants {
	/* AUT: liuli liuli0120@use.com.cn DATE: 2012-01-12 START */
    public static final String CUSTOM_BIMAP_TYPE = ".jpg";
    public static final String IDEA_FOLDER = "/.IdeaDesktop";
    public static final String CUSTOM_BIMAP_PATH = IDEA_FOLDER + ConstantAdapter.DIR_DIY_WALLPAPER;

    //LauncherProvider
    //test by dining 2013-06-24 launcher2->xlauncher2
    public static final String AUTHORITY = "com.lenovo.launcher2.settings";

    public static final String TABLE_FAVORITES = "favorites";
    public static final String PARAMETER_NOTIFY = "notify";
    /* AUT: liuli liuli0120@use.com.cn DATE: 2012-01-31 START */
    // add new table, for app order
    public static final String TABLE_APPLICATIONS = "applications";
    /* AUT: liuli liuli0120@use.com.cn DATE: 2012-01-31 END */

    //<!-- RK_ID: RK_WEATER_WIDGET . AUT: kangwei3 . DATE: 2012-07-02 . S-->
   /* RK_ID: RK_LENOVO_WIDGET . AUT: liuyaguang . DATE: 2012-10-17 . S */
	public static final String LOTUSDEFAULTVIEWHELPER = "com.lenovo.launcher2.gadgets.Lotus.LotusProviderHelper";
	public static final String WEATHERWIDGETVIEWHELPER = "com.lenovo.launcher2.weather.widget.WeatherWidgetView";
	public static final String WEATHERMAGICWIDGETVIEWHELPER = "com.lenovo.launcher2.weather.widget.WeatherWidgetMagicView";
	public static final String WEATHERSQUAREWIDGETVIEWHELPER = "com.lenovo.launcher2.weather.widget.WeatherWidgetSquareView";
	public static final String TOGGLEWIDGETVIEWHELPER = "com.lenovo.launcher2.toggle.widget.ToggleWidgetView";
	public static final String TASKMANAGERWIDGETVIEWHELPER = "com.lenovo.launcher2.taskmanager.widget.TaskManagerWidget";
	  /*RK_ID:RK_WIDGET_SETTING AUT:liuyg1@lenovo.com DATE: 2013-04-24 START */
	public static final String LOTUSSETTING = "com.lenovo.launcher2.gadgets.Lotus.LotusSetting";
	public static final String TASKMANAGERETTING = "com.lenovo.launcher2.settings.WhiteListAppActivity";
	public static final String WEATHERSETTING = "com.lenovo.launcher2.settings.WeatherSettings";
	public static final String WEATHERLOTUSWIDGETVIEWHELPER = "com.lenovo.launcher2.weather.widget.lotus.WeatherWidgetLotusView";
	  /*RK_ID:RK_WIDGET_SETTING AUT:liuyg1@lenovo.com DATE: 2013-04-24 END */
   /* RK_ID: RK_LENOVO_WIDGET . AUT: liuyaguang . DATE: 2012-10-17 . E */
    //<!-- RK_ID: RK_WEATER_WIDGET . AUT: kangwei3 . DATE: 2012-07-02 . S-->
	public static final String CHANGE_ICON_KEY = "change_theme_icon";
    public static final String EXTRA_FOLDER = "QUICK_FOLDER";
    
    //just for bug KSI-113 by zhanggx1.s
    public static final String[] STK_PKG_NAMES = new String[] {
    	"com.android.stk",
    	"com.mediatek.stkselection"
    };
    //just for bug KSI-113 by zhanggx1.e
    private Constants(){
    }
}