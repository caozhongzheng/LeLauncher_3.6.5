package com.lenovo.launcher2.lock.widget;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.XLauncherModel;
import com.lenovo.launcher2.customizer.SettingsValue;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

public class LockWidgetProvider extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent shortcutIntent = new Intent("com.lenovo.launcher.action.shortcutdetail");
        shortcutIntent.setPackage(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF);
        shortcutIntent.setClassName(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF, "com.lenovo.launcher2.toggle.widget.LockScreen$Controller");
        shortcutIntent.putExtra("EXTRA",12);
        shortcutIntent.putExtra(XLauncherModel.EXTRA_SHORTCUT_LABEL_RESOURCE, R.string.toggle_widget_lock_name);
        shortcutIntent.putExtra(XLauncherModel.EXTRA_SHORTCUT_LABEL_RESNAME,
                this.getResources().getResourceName(R.string.toggle_widget_lock_name));

        // set theme info
//        shortcutIntent.setClassName(this, ThemeSettings.class.getName());

        // set name
        Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.toggle_widget_lock_name));

        // set icon
        Parcelable iconResource = Intent.ShortcutIconResource.fromContext(this,
                R.drawable.lock_widget_app_icon);
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);

        // // Now, return the result to the launcher
        setResult(RESULT_OK, intent);
        finish();

    }
}
