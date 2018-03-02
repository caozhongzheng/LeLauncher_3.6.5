package com.lenovo.launcher2.weather.widget;

import android.app.Activity;
import android.content.ComponentName;

import com.lenovo.launcher2.customizer.SettingsValue;
/* RK_ID: RK_LENOVO_WIDGET . AUT: kangwei . DATE: 2012-10-17 . S */
public class WeatherWidgetMagicWidgetProvider extends Activity {
/* RK_ID: RK_LENOVO_WIDGET . AUT: kangwei . DATE: 2012-10-17 . E */
	private static final ComponentName THIS_PUSHMAILWIDGET_LOTUS =
	        new ComponentName(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF,
	                 "com.lenovo.launcher2.weather.widget.WeatherWidgetAppWidgetProvider");

/* RK_ID: RK_LENOVO_WIDGET . AUT: kangwei . DATE: 2012-10-17 . S */
//	@Override
//	public void onDisabled(final Context context)
//	{
//		super.onDisabled(context);
//   	if(!WeatherUtilites.hasInstances(context,THIS_PUSHMAILWIDGET_LOTUS)){
//        	Intent intent = new Intent();
//        	context.stopService(intent.setClass(context,WidgetService.class));
//    	}
//	} 
/* RK_ID: RK_LENOVO_WIDGET . AUT: kangwei . DATE: 2012-10-17 . E */
}
