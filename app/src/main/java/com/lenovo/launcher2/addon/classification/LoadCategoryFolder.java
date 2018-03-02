package com.lenovo.launcher2.addon.classification;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;
import com.lenovo.launcher2.LauncherProvider;
import com.lenovo.launcher2.commoninterface.ConstantPasser.Favorites;
import com.lenovo.launcher2.commoninterface.FolderInfo;
import com.lenovo.launcher2.commoninterface.ItemInfo;
import com.lenovo.launcher2.commoninterface.LauncherAppWidgetInfo;
import com.lenovo.launcher2.commoninterface.LauncherSettings;
import com.lenovo.launcher2.commoninterface.ShortcutInfo;
import com.lenovo.launcher2.customizer.Debug.R2;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.Utilities;

public class LoadCategoryFolder {
    private Context mContext;
    private AppsClassificationData mData;
    
    public LoadCategoryFolder(Context c){
        mContext = c;
        mData = AppsClassificaionDataFactory.getAppsClassificationData
                (mContext, AppsClassificaionDataFactory.DATA1);
    }
    
    public LoadCategoryFolder(Context c,AppsClassificationData data){
        mContext = c;
        mData = data;
    }
    
    public void AppsClassificaionData(int data){
        mData = AppsClassificaionDataFactory.getAppsClassificationData
        (mContext,data);
    }
    
    public void LoadCategoryFolderIntoWorsapce(){
        
        Map<String,ArrayList<String>> appsCategory = mData.getCategoryApps();
        Iterator<Map.Entry<String,ArrayList<String>>> it = appsCategory.entrySet().iterator();
        int i =4;
        while(it.hasNext()){
            Map.Entry<String,ArrayList<String>> map = it.next();
            String folderName = map.getKey();
            FolderInfo info = new FolderInfo();

            info.title = folderName;
            info.cellX = i % 4;
            info.cellY = i / 4;
            info.screen = 2;
            info.container = LauncherSettings.Favorites.CONTAINER_DESKTOP;
            i++;

            Log.d("category","-----LoadCategoryFolderIntoWorsapce ----info.cellX= "+ info.cellX);
            Log.d("category","-----LoadCategoryFolderIntoWorsapce ----info.cellY = "+ info.cellY);

            long container = surelyAddItemInDatabase(mContext,new FolderInfo[] { info }, null);
            Log.d("category","-----LoadCategoryFolderIntoWorsapce ----container = "+ container);
            Iterator<String> appIt = map.getValue().iterator();
            ArrayList<ShortcutInfo> infoList = new ArrayList<ShortcutInfo>();
            while(appIt.hasNext()){
                ShortcutInfo  launcherShortcutInfo = new ShortcutInfo();
                String str1[] = appIt.next().split("/");
                if(str1.length!=3){
                    continue;
                }
                String packageName = str1[0];
                String className = str1[1];
                String title = str1[2];
                Intent intent = null;
                

                
                if (packageName != null && !"".equals(packageName)
                        && className != null && !"".equals(className)) {
                    intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.setComponent(new ComponentName(packageName,
                            className));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

                }
                
                launcherShortcutInfo.container =  container;
                launcherShortcutInfo.cellX = 0;
                launcherShortcutInfo.cellY = 0;
                launcherShortcutInfo.screen = 0;
                launcherShortcutInfo.spanX = 1;
                launcherShortcutInfo.spanY = 1;
                launcherShortcutInfo.uri = intent == null ? null : intent.toUri(0);
                launcherShortcutInfo.intent = intent;
                launcherShortcutInfo.itemType =0;// LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT;
                launcherShortcutInfo.title = title;

                
                infoList.add(launcherShortcutInfo);
            }
            surelyAddItemInDatabase(mContext,infoList.toArray(new ShortcutInfo[0]),null);
        }
        
        return;
        
    }
    

    public long surelyAddItemInDatabase(final Context context,
            ItemInfo[] infos, ContentValues[] extraValues) {

        ContentValues[] allValues = new ContentValues[infos.length];
        for (int i = 0; i < infos.length; i++) {
            // R2

            allValues[i] = new ContentValues();

            if (extraValues != null) {
                allValues[i].putAll(extraValues[i]);
            }

            LauncherApplication app = (LauncherApplication) context
                    .getApplicationContext();
            infos[i].id = app.getLauncherProvider().generateNewId();
            allValues[i].put(LauncherSettings.Favorites._ID, infos[i].id);
            infos[i].updateValuesWithCoordinates(allValues[i],
                    infos[i].cellX, infos[i].cellY);
            allValues[i].put(Favorites.CONTAINER, infos[i].container);
            allValues[i].put(Favorites.SCREEN, infos[i].screen);
            allValues[i].put(Favorites.SPANX, infos[i].spanX);
            allValues[i].put(Favorites.SPANY, infos[i].spanY);
            allValues[i].put(Favorites.ITEM_TYPE, infos[i].itemType);

            if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
                R2.echo("info is : " + infos[i].getClass().getSimpleName());
            if (infos[i] instanceof FolderInfo) {
                FolderInfo folder = (FolderInfo) infos[i];
                allValues[i].put(Favorites.TITLE, folder.title.toString());

            } else if (infos[i] instanceof ShortcutInfo) {
                ShortcutInfo shortcut = (ShortcutInfo) infos[i];
                allValues[i]
                        .put(Favorites.INTENT, shortcut.intent.toUri(0));
                allValues[i]
                        .put(Favorites.TITLE, shortcut.title.toString());
                if (shortcut.replaceTitle != null
                        && !"".equals(shortcut.replaceTitle.toString()
                                .trim())) {
                    allValues[i].put(Favorites.TITLE_REPLACE,
                            shortcut.replaceTitle.toString());
                }
                if (!allValues[i].containsKey(Favorites.ICON)) {
                    allValues[i].put(
                            Favorites.ICON,
                            Utilities.newInstance().bitmap2ByteArray(
                                    app.getModel().getFallbackIcon()));
                }
                if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
                    R2.echo("ShortcutInfo add : " + infos[i].id);
            } else if (infos[i] instanceof LauncherAppWidgetInfo) {
                LauncherAppWidgetInfo appWidgetInfo = (LauncherAppWidgetInfo) infos[i];
                allValues[i].put(LauncherSettings.Favorites.APPWIDGET_ID,
                        appWidgetInfo.appWidgetId);
            }

            // commend face  --- coordinated with MEM OPT.
            if (infos[i] instanceof LauncherAppWidgetInfo) {
                LauncherAppWidgetInfo infoTmp = (LauncherAppWidgetInfo) infos[i];
                if (infoTmp != null) {
                    if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
                        R2.echo("Settle Commend face 2.");
                    ItemInfo.writeBitmap(allValues[i], infoTmp.iconBitmap);
                    if (com.lenovo.launcher2.customizer.Debug.MAIN_DEBUG_SWITCH)
                        R2.echo("Settle Commend face 1.");
                    infoTmp = null;
                }
            }
            
            allValues[i].put(LauncherSettings.Favorites.LAST_USE_TIME, System.currentTimeMillis());

        }

        // bulk insert now
        LauncherApplication app = (LauncherApplication) context
                .getApplicationContext();
        LauncherProvider cp = app.getLauncherProvider();
        cp.bulkInsert(
                LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION,
                allValues);
        
        SettingsValue.rescheduleCleanupAlarm(context);

        try {
            return infos[0].id;
        } catch (Exception e) {
            return -1;
        }
    }
    
    //launcherFolderInfo = new FolderInfo();
}
