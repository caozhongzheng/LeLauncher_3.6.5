package com.lenovo.launcher2;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import android.content.ComponentName;

import com.lenovo.launcher2.customizer.SettingsValue;


public class Launcher extends Activity{
    private final static String TAG = "LauncherTheme";

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Intent intent = getIntent();
        Log.e(TAG,"packageName =========================="+intent);
        if(intent != null){
            if (SettingsValue.ACTION_SHOW_THEME_DLG.equals(intent.getAction())) {
                String packageName = intent.getStringExtra(SettingsValue.EXTRA_THEME_VALUE);
                startAndApplyTheme(packageName);
            }
        }
        finish();
    }
    private void startAndApplyTheme(String packageName){
        Intent intent = new Intent(SettingsValue.ACTION_SHOW_THEME_DLG);
        intent.putExtra(SettingsValue.EXTRA_THEME_VALUE, packageName);
        intent.setComponent(getLeLauncherComponentName());
        startActivity(intent);
    }
    private ComponentName getLeLauncherComponentName(){
        final PackageManager packageManager = getPackageManager();
        Intent a = packageManager.getLaunchIntentForPackage(getPackageName());
        Log.e(TAG,"className ======================="+a.toString()+""+a.getComponent());
        return a.getComponent();
    }

}
