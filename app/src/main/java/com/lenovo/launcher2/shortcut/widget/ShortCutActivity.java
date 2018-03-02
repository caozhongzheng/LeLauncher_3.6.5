package com.lenovo.launcher2.shortcut.widget;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.XLauncherModel;
import com.lenovo.launcher2.customizer.SettingsValue;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

public class ShortCutActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*		Intent intentshutcut = new Intent("com.lenovo.launcher.action.shortcutdetail");
		intentshutcut.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intentshutcut.putExtra(XLauncherModel.EXTRA_SHORTCUT_LABEL_RESNAME, R.string.shortcut_widget_app_name);
		intentshutcut.putExtra(XLauncherModel.EXTRA_SHORTCUT_LABEL_RESNAME,
                this.getResources().getResourceName(R.string.shortcut_widget_app_name));
		
        Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intentshutcut);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.shortcut_widget_app_name));
        
        
        
        // set icon
        Parcelable iconResource = Intent.ShortcutIconResource.fromContext(this,
                R.drawable.shutcut_widget_app_icon);
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);

        // // Now, return the result to the launcher
        setResult(RESULT_OK, intent);
        finish();*/
        
        Intent shortcutIntent = new Intent("com.lenovo.launcher.action.shortcutdetail");
        shortcutIntent.setPackage(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF);
        shortcutIntent.putExtra("EXTRA",12);
        shortcutIntent.putExtra(XLauncherModel.EXTRA_SHORTCUT_LABEL_RESOURCE, R.string.shortcut_widget_app_name);
        shortcutIntent.putExtra(XLauncherModel.EXTRA_SHORTCUT_LABEL_RESNAME,
                this.getResources().getResourceName(R.string.shortcut_widget_app_name));

        // set theme info
//        shortcutIntent.setClassName(this, ThemeSettings.class.getName());

        // set name
        Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.shortcut_widget_app_name));

        // set icon
        Parcelable iconResource = Intent.ShortcutIconResource.fromContext(this,
                R.drawable.shutcut_widget_app_icon);
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);

        // // Now, return the result to the launcher
        setResult(RESULT_OK, intent);
        finish();
    }
}
