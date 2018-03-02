package com.lenovo.launcher2.gadgets;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.ComponentName;
import android.content.Context;
import android.graphics.Point;
import android.view.View;

import com.lenovo.launcher.components.XAllAppFace.XLauncher;

import com.lenovo.launcher2.customizer.Constants;
import com.lenovo.launcher2.customizer.Debug;
import com.lenovo.launcher2.customizer.Debug.R2;
import com.lenovo.launcher2.customizer.Debug.R5;
import com.lenovo.launcher2.gadgets.Lotus.LotusDefaultViewHelper;
//import com.lenovo.launcher2.shortcut.widget.ShortCutView;
//import com.lenovo.launcher2.toggle.widget.ToggleWidgetView;
import com.lenovo.launcher2.weather.widget.WeatherWidgetMagicView;
import com.lenovo.launcher2.weather.widget.WeatherWidgetSquareView;
import com.lenovo.launcher2.weather.widget.WeatherWidgetView;

/**
 * Author : ChengLiang
 * */

public class GadgetUtilities {
    //<!-- RK_ID: RK_WEATER_WIDGET . AUT: kangwei3 . DATE: 2012-07-02 . S-->
   /* RK_ID: RK_LENOVO_WIDGET . AUT: liuyaguang . DATE: 2012-10-17 . S */
	public static final String LOTUSDEFAULTVIEWHELPER = "com.lenovo.launcher2.gadgets.Lotus.LotusProviderHelper";
	public static final String WEATHERWIDGETVIEWHELPER = "com.lenovo.launcher2.weather.widget.WeatherWidgetMagicView";
	public static final String WEATHERMAGICWIDGETVIEWHELPER = "com.lenovo.launcher2.weather.widget.WeatherWidgetView";
	public static final String WEATHERSQUAREWIDGETVIEWHELPER = "com.lenovo.launcher2.weather.widget.WeatherWidgetSquareView";
//	public static final String TOGGLEWIDGETVIEWHELPER = "com.lenovo.launcher2.toggle.widget.ToggleWidgetView";
//	public static final String TASKMANAGERWIDGETVIEWHELPER = "com.lenovo.launcher2.taskmanager.widget.TaskManagerWidget";
	public static final String SHORTCUT = "com.lenovo.launcher2.shortcut.widget.ShortCutView";
	public static final String LOCKWIDGET = "com.lenovo.launcher2.shortcut.widget.LockWidgetView";
   
	public static final String GAME = "com.lenovo.lejingpin.hw.game.widget.GameView";
/* RK_ID: RK_LENOVO_WIDGET . AUT: liuyaguang . DATE: 2012-10-17 . E */
    //<!-- RK_ID: RK_WEATER_WIDGET . AUT: kangwei3 . DATE: 2012-07-02 . S-->

	public static boolean isGadget(ComponentName provider) {

		String pkName = provider.getPackageName();
		String czName = provider.getClassName();

		for (int i = 0; i < GadgetConstants.sfGadgetList.size(); i++) {

			ComponentName cn = GadgetConstants.sfGadgetList.get(i);
			if (cn.getPackageName().equals(pkName)
					&& cn.getClassName().equals(czName)) {
				R2.echo("Will expand widget.");
				return true;
			}
		}

		return false;
	}
	
	public static View fetchView(XLauncher context, ComponentName whose) {
		// only test now
		//<!-- RK_ID: RK_WEATER_WIDGET . AUT: kangwei3 . DATE: 2012-07-02 . S-->
		String czName = whose.getClassName();
		if(czName.equals(LOTUSDEFAULTVIEWHELPER))
		{
		    R5.echo("fetchView LotusDefaultViewHelper");
		    LotusDefaultViewHelper lotus = new LotusDefaultViewHelper(context);
		    mLotusList.add(lotus);
			return lotus.getHoldedView();
		}
		else if(czName.equals(WEATHERWIDGETVIEWHELPER))
			return new WeatherWidgetMagicView(context.getApplicationContext());
		else if(czName.equals(WEATHERMAGICWIDGETVIEWHELPER))
			return new WeatherWidgetView(context.getApplicationContext());
		else if(czName.equals(WEATHERSQUAREWIDGETVIEWHELPER))
			return new WeatherWidgetSquareView(context.getApplicationContext());
      /* RK_ID: RK_LENOVO_WIDGET . AUT: kangwei . DATE: 2012-10-17 . S */
//		else if(czName.equals(TOGGLEWIDGETVIEWHELPER ))
//			return new ToggleWidgetView(context.getApplicationContext());
//		else if(czName.equals(TASKMANAGERWIDGETVIEWHELPER ))
//			return new TaskManagerWidget(context);
//		else if(czName.equals(SHORTCUT ))
//			return new ShortCutView(context);
		/*else if(czName.equals(LOCKWIDGET))
			return new LockWidgetView(context);*/
		/*else if(czName.equals(GAME))
			return new GameView(context);*/
		else
			return null;
      /* RK_ID: RK_LENOVO_WIDGET . AUT: kangwei . DATE: 2012-10-17 . E */
	}
	///new art
	
	private static ArrayList<LotusDefaultViewHelper> mLotusList = new ArrayList<LotusDefaultViewHelper>();
	
	public static void clean() {

	    Iterator<LotusDefaultViewHelper>  it = mLotusList.iterator();
	    while(it.hasNext()){
	        LotusDefaultViewHelper lotus = (LotusDefaultViewHelper)it.next();
	        lotus.clean();
	    }
	    mLotusList.clear();
	    
        return;
    }
	private GadgetUtilities(){
		
	}

}
