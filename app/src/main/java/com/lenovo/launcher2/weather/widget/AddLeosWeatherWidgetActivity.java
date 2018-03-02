package com.lenovo.launcher2.weather.widget;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.lenovo.launcher2.addleoswidget.LenovoWidgetsProviderInfo;
import com.lenovo.launcher2.weather.widget.settings.FetchLenovoWeatherWidgetUtil;
import com.lenovo.launcher2.weather.widget.utils.WeatherUtilites;

public class AddLeosWeatherWidgetActivity extends Activity {
    static final String TAG = "AddLeosWidgetActivity";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        final LenovoWidgetsProviderInfo item = new FetchLenovoWeatherWidgetUtil(this).fetchAllInstalledLeosWidgets().get(0);
        if(item.isInstalled){
        	Log.i("zzcao", "item="+item.appPackageName+" widgetView="+item.widgetView+" x="+ item.x+" y="+ item.y);
        	Intent intent = new Intent(WeatherUtilites.ACTION_ADD_LENOVOWIDGET);
    		intent.putExtra("EXTRA_PACKAGENAME", item.appPackageName);
    		intent.putExtra("EXTRA_CALSS", item.widgetView);
    		intent.putExtra("EXTRA_WIDTH", item.x);
    		intent.putExtra("EXTRA_HIEGHT", item.y);
    		this.sendBroadcast(intent );
    		finish();
        }
    }

}
