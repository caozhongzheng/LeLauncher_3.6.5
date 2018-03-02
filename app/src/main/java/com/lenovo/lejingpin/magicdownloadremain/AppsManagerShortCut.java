package com.lenovo.lejingpin.magicdownloadremain;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.XLauncherModel;

import com.lenovo.launcher2.customizer.GlobalDefine;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.Debug.R5;

public class AppsManagerShortCut extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Intent shortcutIntent= null; 

		// set iconstyle info
        String intentclass = "com.lenovo.lejingpin.ClassicFragmentActivity";

		shortcutIntent = new Intent(SettingsValue.ACTION_LETHEME_LAUNCH);
		shortcutIntent.putExtra("EXTRA", -1);
      
        shortcutIntent.putExtra(XLauncherModel.EXTRA_SHORTCUT_LABEL_RESOURCE, 
        		R.string.magicdownload_upgrade_applist);
        shortcutIntent.putExtra(XLauncherModel.EXTRA_SHORTCUT_LABEL_RESNAME,
                this.getResources().getResourceName(R.string.magicdownload_upgrade_applist));
        Log.d("xujing3",this.getPackageName() + ", " + this.getClass().getName().toString());
        ComponentName component = new ComponentName(this.getPackageName(),intentclass);
        
		shortcutIntent.setComponent(component);
		shortcutIntent.addCategory(SettingsValue.LAUNCHER_NOTIFICATION_CATEGORY);		
        // set name
        Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.magicdownload_upgrade_applist));
//        intent.setComponent(component);
//        intent.addCategory("android.intent.category.LENOVO_LAUNCHER_NOTIFICATION");

        // set icon
        Parcelable iconResource = Intent.ShortcutIconResource.fromContext(this,
                R.drawable.ic_launcher_appsmanager_shortcut);
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);

        //Now, return the result to the launcher
        setResult(RESULT_OK, intent);
        finish();
    }
}
