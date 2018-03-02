package com.lenovo.launcher2.gadgets;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;

/**
 * Author : ChengLiang
 * */
public class GadgetConstants {

	public static final List<ComponentName> sfGadgetList = new ArrayList<ComponentName>();

	private static final String LAUNCHER_PACKAGE_NAME = "com.lenovo.launcher";
	static {
		
		sfGadgetList.add(new ComponentName(
				LAUNCHER_PACKAGE_NAME,
				"com.lenovo.launcher2.gadgets.Lotus.LotusProviderHelper"));
    //<!-- RK_ID: RK_WEATER_WIDGET . AUT: kangwei3 . DATE: 2012-07-02 . S-->
		sfGadgetList.add(new ComponentName(
				LAUNCHER_PACKAGE_NAME,
				"com.lenovo.launcher2.weather.widget.WeatherWidgetAppWidgetProvider"));
		sfGadgetList.add(new ComponentName(
				LAUNCHER_PACKAGE_NAME,
				"com.lenovo.launcher2.weather.widget.WeatherWidgetMagicWidgetProvider"));
		sfGadgetList.add(new ComponentName(
				LAUNCHER_PACKAGE_NAME,
				"com.lenovo.launcher2.weather.widget.WeatherWidgetSquareWidgetProvider"));
		sfGadgetList.add(new ComponentName(
				LAUNCHER_PACKAGE_NAME,
				"com.lenovo.launcher2.toggle.widget.ToggleWidgetAppWidgetProvider"));
       /*RK_ID:RK_LEOSWIDGET AUT:kangwei DATE: 2012-10-17 START */
		sfGadgetList.add(new ComponentName(
				LAUNCHER_PACKAGE_NAME,
				"com.lenovo.launcher2.taskmanager.widget.TaskManagerWidgetProvider"));
		sfGadgetList.add(new ComponentName(
				LAUNCHER_PACKAGE_NAME,
				"com.lenovo.launcher2.shortcut.widget.ShortCutActivity"));
		sfGadgetList.add(new ComponentName(
				LAUNCHER_PACKAGE_NAME,
				"com.lenovo.launcher2.shortcut.widget.LockWidgetProvider"));
      
/*RK_ID:RK_LEOSWIDGET AUT:kangwei DATE: 2012-10-17 E */

    //<!-- RK_ID: RK_WEATER_WIDGET . AUT: kangwei3 . DATE: 2012-07-02 . S-->
	};
	private GadgetConstants() {
		
	}
}
