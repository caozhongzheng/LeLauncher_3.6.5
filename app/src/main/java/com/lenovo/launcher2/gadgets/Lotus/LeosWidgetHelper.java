package com.lenovo.launcher2.gadgets.Lotus;

import android.view.View;

import com.lenovo.launcher2.commoninterface.ItemInfo;
public class LeosWidgetHelper{
	public static boolean isGadgetOnScreen(View holdedView, int screenIndex) {
        /*RK_ID:RK_LEOSWIDGET AUT:gecn1 DATE: 2012-10-17 START */
 		ItemInfo info = (ItemInfo) holdedView
 				.getTag();
        /*RK_ID:RK_LEOSWIDGET AUT:gecn1 DATE: 2012-10-17 E */
 		if (info == null)
 			return false;

 		return info.screen == screenIndex;
 	}
    /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-09-26 START */
	/*public boolean isLEOSWidgetOnScreen(View holdedView, int screenIndex) {

		LenovoWidgetViewInfo info = (LenovoWidgetViewInfo) holdedView

				.getTag();
		if (info == null)
			return false;

		return info.screen == screenIndex;
	}*/
/*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-09-26 END */
	private LeosWidgetHelper() {
		// TODO Auto-generated constructor stub
	}
}
