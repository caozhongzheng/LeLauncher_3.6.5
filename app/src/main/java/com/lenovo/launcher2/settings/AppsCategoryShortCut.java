package com.lenovo.launcher2.settings;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.XLauncherModel;
import com.lenovo.launcher2.customizer.SettingsValue;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

public class AppsCategoryShortCut extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // shortcut intent
        Intent shortcutIntent = new Intent(SettingsValue.ACTION_WORKSPACE_APPS_CATEGORY);
        shortcutIntent.setPackage(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF);
        shortcutIntent.putExtra(XLauncherModel.EXTRA_SHORTCUT_LABEL_RESOURCE, R.string.apps_category_shortcut);
        shortcutIntent.putExtra(XLauncherModel.EXTRA_SHORTCUT_LABEL_RESNAME,
                this.getResources().getResourceName(R.string.apps_category_shortcut));

        // set name
        Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.apps_category_shortcut));

        // set icon
        Parcelable iconResource = Intent.ShortcutIconResource.fromContext(this,
                R.drawable.ic_launcher_classify_apps);
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);

        // // Now, return the result to the launcher
        setResult(RESULT_OK, intent);
        finish();
    }
}
