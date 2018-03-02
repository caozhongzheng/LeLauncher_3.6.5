/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lenovo.launcher.components.XAllAppFace;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.addon.classification.GetAppsCategory;
import com.lenovo.launcher2.commoninterface.FolderInfo;
import com.lenovo.launcher2.commoninterface.ItemInfo;
import com.lenovo.launcher2.commoninterface.LauncherService;
import com.lenovo.launcher2.commoninterface.LauncherSettings;
import com.lenovo.launcher2.commoninterface.ShortcutInfo;
import com.lenovo.launcher2.customizer.SettingsValue;

public class InstallShortcutReceiver extends BroadcastReceiver {
    public static final String ACTION_INSTALL_SHORTCUT =
            "com.android.launcher.action.INSTALL_SHORTCUT";

    // A mime-type representing shortcut data
    public static final String SHORTCUT_MIMETYPE =
            "com.android.launcher/shortcut";

    private final int[] mCoordinates = new int[2];
	 /** AUT: zhanglq@bj.cobellink.com DATE: 2012-1-12 start*/
    private static LauncherService mLauncherService = LauncherService.getInstance();
	 /** AUT: zhanglq@bj.cobellink.com DATE: 2012-1-12 end*/

    /* RK_ID: RK_SHORTCUT. AUT: liuli1 . DATE: 2012-04-10 . START */
    private static final String COLON = " : ";
    /* RK_ID: RK_SHORTCUT. AUT: liuli1 . DATE: 2012-04-10 . END */

    /* RK_ID: RK_SHORTCUT. AUT: yumina . DATE: 2012-07-12 . START */
    private static final int MSG_ID_ONPAUSE = 1;
    private static final String TAG = "InstallShortcutReceiver";
    private static final boolean DUBUG = true;

    private Handler mLauncherHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_ID_ONPAUSE:
                mLauncherService.mCellXY = null;
            break;
            default:
            	break;
            } 
        }    
    };
    /* RK_ID: RK_SHORTCUT. AUT: yumina . DATE: 2012-07-12 . END */

    public void onReceive(Context context, Intent data) {
        boolean single = SettingsValue.getSingleLayerValue(context);
        Log.i(TAG, "single is  ====" + single);
//        if (single) {
//            return;
//        }

        if (!ACTION_INSTALL_SHORTCUT.equals(data.getAction())) {
            return;
        }

        int screen = XLauncher.getScreen();
        mLauncherHandler.removeMessages(MSG_ID_ONPAUSE);
        mLauncherHandler.sendMessageDelayed(mLauncherHandler.obtainMessage(MSG_ID_ONPAUSE),2000);
                
        if (mLauncherService.mAddView)
        {
            screen = screen - 1;
        }

        if (!installShortcut(context, data, screen)) {
            // The target screen is full, let's try the other screens
            for (int i = 0; i < mLauncherService.mScreenCount; i++) {
                if(i != screen) mLauncherService.mCellXY = null;
                if (i != screen && installShortcut(context, data, i)) break;
            }
        }
        //add by zhanggx1, 2013-06-20，添加完SHORTCUT刷新屏幕编辑界面
        Intent intentRefresh = new Intent(SettingsValue.ACTION_REFRESH_MNG_VIEW);
    	context.sendBroadcast(intentRefresh);
    }

    private boolean installShortcut(Context context, Intent data, int screen) {
        String name = data.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);
        android.util.Log.i("dooba", "------------InstallReceiver---------name:  " + name);
        if(SettingsValue.isAutoAppsClassify(context) && false){
        	Parcelable extra = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);
            String packageName = null;
            Intent intent = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);
            if (intent != null) {
                if (intent.getAction() == null) {
                    intent.setAction(Intent.ACTION_VIEW);
                }
            }else{
                //Can't construct ShorcutInfo with null intent
                return true;
            }
            if(extra!=null && extra instanceof ShortcutIconResource){
                //try  best to find shorcut's package name
                packageName = ((ShortcutIconResource) extra).packageName;
                if(packageName == null){
                    if(intent.getComponent() !=null){
                        packageName = intent.getComponent().getPackageName();
                    }
                }
            }
            String appCategoryName = GetAppsCategory.getAppsCategoryByPackageName(context, packageName);
            if(DUBUG)Log.d(TAG, "------------------installShortcut = " + appCategoryName);
            final ContentResolver cr = context.getContentResolver();
            long folderID = -1;
            Cursor c = cr.query(LauncherSettings.Favorites.CONTENT_URI,
                    new String[] { LauncherSettings.Favorites._ID, LauncherSettings.Favorites.INTENT },
                    LauncherSettings.Favorites.TITLE + "=?", new String[] { appCategoryName }, null);
            if(c!= null ){
                try{
                    c.moveToFirst();
                    int index = c.getColumnIndexOrThrow(LauncherSettings.Favorites._ID);
                    while(!c.isAfterLast()){
                        folderID = c.getLong(index);
                        c.moveToNext();
                        if(DUBUG)Log.d(TAG, "------------------folderID = " + folderID);
                        break;
                    }
                       
                }finally{
                    c.close();
                }
            }
            
            if(folderID ==-1 ){
                if(findEmptyCell(context, mCoordinates, screen)){
                    mLauncherService.mCellXY = mCoordinates; 
                    int container = LauncherSettings.Favorites.CONTAINER_DESKTOP;
                    final FolderInfo folderInfo = new FolderInfo();
                    folderInfo.title =appCategoryName;
                    
                    if(DUBUG)Log.d(TAG, "------------------folderID = " + -1);
                    // Update the model
                    folderInfo.screen = screen;
                    folderInfo.cellX =  mCoordinates[0];
                    folderInfo.cellY =  mCoordinates[1];
                    XLauncherModel.addItemToDatabase(context, folderInfo, container, screen, folderInfo.cellX, folderInfo.cellY, false);
                    XFolderIcon.setNewAppsNumberTip(folderInfo.id, context,1);
                    LauncherApplication app = (LauncherApplication) context.getApplicationContext();
                    ShortcutInfo info = app.getModel().addShortcut(context, data,folderInfo.id, 0, 0,0, true);
                    if(info!=null){
                        Toast.makeText(context,context.getString(R.string.application_name) + COLON
                                + context.getString(R.string.shortcut_installed_intofloder,name,appCategoryName), Toast.LENGTH_SHORT)
                                .show();
                        return true;
                    }else{
                        return false;
                    }
                }else{
                    return false;
                }
                
            }else{
                if(!shortcutIsExsitInFolder(context, name, intent,folderID)){
                    LauncherApplication app = (LauncherApplication) context.getApplicationContext();
                    ShortcutInfo info = app.getModel().addShortcut(context, data,folderID, 0, 0,0, true);
                    XFolderIcon.setNewAppsNumberTip(folderID, context,1);
                    if(DUBUG)Log.d(TAG, "------------------shortcut no exits  " );
                    
                    if(info != null){
                        Toast.makeText(context,context.getString(R.string.application_name) + COLON
                                + context.getString(R.string.shortcut_installed_intofloder, name,appCategoryName), Toast.LENGTH_SHORT)
                                .show();
                        return true;
                    }else{
                        return false;
                    }
                    
                }else{
                    if(DUBUG)Log.d(TAG, "------------------shortcut exist " );
                    Toast.makeText(context,context.getString(R.string.application_name) + COLON+ 
                            context.getString(R.string.shortcut_duplicate, name), Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
        }else{
            if (findEmptyCell(context, mCoordinates, screen)) {
                mLauncherService.mCellXY = mCoordinates; 
                Intent intent = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);
                if (intent != null) {
                    if (intent.getAction() == null) {
                        intent.setAction(Intent.ACTION_VIEW);
                    }

                    // By default, we allow for duplicate entries (located in
                    // different places)
                    
                    /**boolean duplicate = data.getBooleanExtra(XLauncher.EXTRA_SHORTCUT_DUPLICATE, true);**/
                    if (/**duplicate ||*/ !shortcutIsExsitInWorkSpaceOrHotseat(context, name, intent)) {
				        Log.d("gecn1", "shortcutIsExsitInWorkSpaceOrHotseat    false" );
                        LauncherApplication app = (LauncherApplication) context.getApplicationContext();
                        ShortcutInfo info = app.getModel().addShortcut(context, data,
                                LauncherSettings.Favorites.CONTAINER_DESKTOP, screen, mCoordinates[0],
                                mCoordinates[1], true);
                        if (info != null) {
                            /* RK_ID: RK_SHORTCUT. AUT: liuli1 . DATE: 2012-04-10 . START */
//                            Toast.makeText(
//                                    context,
//                                    context.getString(R.string.application_name) + COLON
//                                            + context.getString(R.string.shortcut_installed,name), Toast.LENGTH_SHORT)
//                                    .show();
                            /* RK_ID: RK_SHORTCUT. AUT: liuli1 . DATE: 2012-04-10 . END */
                        } else {
                            return false;
                        }
                    } else {
                        /* RK_ID: RK_SHORTCUT. AUT: liuli1 . DATE: 2012-04-10 . START */
//                        Toast.makeText(
//                                context,
//                                context.getString(R.string.application_name) + COLON
//                                        + context.getString(R.string.shortcut_duplicate,name), Toast.LENGTH_SHORT).show();
                        /* RK_ID: RK_SHORTCUT. AUT: liuli1 . DATE: 2012-04-10 . END */
                    }

                    return true;
                }
            } else {
                /* RK_ID: RK_SHORTCUT. AUT: liuli1 . DATE: 2012-04-10 . START */
//                Toast.makeText(context,
//                        context.getString(R.string.application_name) + COLON + context.getString(R.string.out_of_space),
//                        Toast.LENGTH_SHORT).show();
                /* RK_ID: RK_SHORTCUT. AUT: liuli1 . DATE: 2012-04-10 . END */
            }
            
        }

        return false;
    }
    
    private boolean shortcutIsExsitInFolder(Context context,String shortcutName,Intent intent,long folderId){
        final ContentResolver cr = context.getContentResolver();
        Cursor c = cr.query(LauncherSettings.Favorites.CONTENT_URI,
            new String[] { "intent" }, "title=? and container =?",
            new String[] { shortcutName,String.valueOf(folderId)}, null);
        if(c == null) return false;
        try {
            c.moveToFirst();
            String intentName = null;
            int index = c.getColumnIndexOrThrow(LauncherSettings.Favorites.INTENT);
            if(!c.isAfterLast()){
                intentName = c.getString(index);
                Intent i = Intent.parseUri(intentName, 0);
                return  intent.filterEquals(i);
                
            }else{
                return false;
            }
        } catch (Exception e){
            return false;
        }finally {
            c.close();
        }
    }
    
    private boolean shortcutIsExsitInWorkSpaceOrHotseat(Context context,String shortcutName,Intent intent){
        if(intent == null ){
        	return false;
        }
    	StringBuffer str_intent= new StringBuffer();

        str_intent = str_intent.append("%").append(intent.getComponent()!=null ? intent.getComponent().getPackageName():intent.toUri(0)).append("%");
        Log.d("gecn1", "intent   = " + intent.toString() );


        final ContentResolver cr = context.getContentResolver();
        Cursor c = cr.query(LauncherSettings.Favorites.CONTENT_URI,
            new String[] { "title" ,"intent"}, "title=? or intent like ? ",
            new String[] { shortcutName, str_intent.toString() }, null);
//        int title_index = c.getColumnIndexOrThrow(LauncherSettings.Favorites.TITLE);
        int intent_index = c.getColumnIndexOrThrow(LauncherSettings.Favorites.INTENT);
        Bundle addExtras =  intent.getExtras();
        try {
            while(c.moveToNext()){
            	boolean componentIsEquals = false;
//            	if(shortcutName.equals(c.getString(title_index))){
//            		return true;
//            	}
            	Intent it;
            	Bundle extras = null;
				try {
            		Log.d("gecn1", "intent in worksapce  :" + c.getString(intent_index));

					it = Intent.parseUri(c.getString(intent_index), 0);
					extras = it.getExtras();
					
	            	if(intent.getComponent() !=null && it.getComponent()!=null){
	            		Log.d("gecn1", "worksapce data =" + intent.getComponent().toString());
	            		Log.d("gecn1", "intent  data =" + it.getComponent().toString());
	            		if(intent.getComponent().getPackageName().equals(it.getComponent().getPackageName())&&
	            				intent.getComponent().getShortClassName().equals(it.getComponent().getShortClassName())){
	            			componentIsEquals = true;
	            		}
	            	}
					if(intent.filterEquals(it)){
	            		Log.d("gecn1", "intent.filterEquals(it)" + true);
						componentIsEquals = true;
					}
					if(extras != null && addExtras != null &&  componentIsEquals){
						String s1 = parseIntentExtras(extras);
						String s2 = parseIntentExtras(addExtras);
	            		Log.d("gecn1", "worksapce extras =" + s1);
	            		Log.d("gecn1", "intent extras =" + s2);
						componentIsEquals = componentIsEquals && s1.equals(s2);
					}else if(componentIsEquals && ((extras != null)^(addExtras != null))){
						Log.d("gecn1", "extras  null");
						componentIsEquals = false;
					}
					if(componentIsEquals){
						return componentIsEquals;
					}
					

				} catch (URISyntaxException e) {
				}

            }
        }catch(Exception e){
        	return false;
        }finally {
            c.close();
        }
        return false;
    }

    
    private String parseIntentExtras(Bundle intentExtras){
    	ArrayList<String> parts = new ArrayList<String>();
    	StringBuilder result = new StringBuilder();
        if (intentExtras != null) {
            for (String key : intentExtras.keySet()) {
                final Object value = intentExtras.get(key);
                char entryType =
                        value instanceof String    ? 'S' :
                        value instanceof Boolean   ? 'B' :
                        value instanceof Byte      ? 'b' :
                        value instanceof Character ? 'c' :
                        value instanceof Double    ? 'd' :
                        value instanceof Float     ? 'f' :
                        value instanceof Integer   ? 'i' :
                        value instanceof Long      ? 'l' :
                        value instanceof Short     ? 's' :
                        '\0';

                if (entryType != '\0') {
                	StringBuilder uri = new StringBuilder();
                    uri.append(entryType);
                    uri.append('.');
                    uri.append(Uri.encode(key));
                    uri.append('=');
                    uri.append(Uri.encode(value.toString()));
                    uri.append(';');
                    parts.add(uri.toString());
                }
            }
            Collections.sort(parts);
            for(String s : parts){
            	result.append(s);
            }
        }
        return result.toString();
    }
    
    private static boolean findEmptyCell(Context context, int[] xy, int screen) {
        final int xCount = XLauncherModel.getCellCountX();
        final int yCount = XLauncherModel.getCellCountY();
        boolean[][] occupied = new boolean[xCount][yCount];

        ArrayList<ItemInfo> items = XLauncherModel.getItemsInLocalCoordinates(context);
        ItemInfo item = null;
        int cellX, cellY, spanX, spanY;
        for (int i = 0; i < items.size(); ++i) {
            item = items.get(i);
            if (item.container == LauncherSettings.Favorites.CONTAINER_DESKTOP) {
                if (item.screen == screen) {
                    cellX = item.cellX;
                    cellY = item.cellY;
                    spanX = item.spanX;
                    spanY = item.spanY;
                    if (cellX < 0 || cellY < 0) {
                        continue;
                    }
                    for (int x = cellX; x < cellX + spanX && x < xCount; x++) {
                        for (int y = cellY; y < cellY + spanY && y < yCount; y++) {
                            occupied[x][y] = true;
                        }
                    }
                }
            }
        }
        /* RK_ID: RK_SHORTCUT. AUT: yumina . DATE: 2012-07-12 . START */
        if(mLauncherService.mCellXY != null){
            occupied[mLauncherService.mCellXY[0]][mLauncherService.mCellXY[1]] = true;
        }
        /* RK_ID: RK_SHORTCUT. AUT: yumina . DATE: 2012-07-12 . END */
        return XCellLayout.findVacantCell(xy, 1, 1, xCount, yCount, occupied);
    }
}
