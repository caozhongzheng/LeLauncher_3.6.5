package com.lenovo.launcher2.addon.classification;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.lenovo.launcher2.commoninterface.ApplicationInfo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.util.Log;


public class GetAppsCategory implements AppsClassificationData {
    
    private Context mContext;
    private List<ApplicationInfo> mAppsInfo;
    
    private  final static String[] DEFAULT_INDEX_OF_DB = {
            "游戏",//游戏
            "阅读教育",//阅读教育
            "系统工具",//系统工具
            "通信社交",//通信社交
            "影音娱乐",//影音娱乐
            "生活地图",//生活地图
            "办公商务",//办公商务
            "其他"//其它
        };

    public GetAppsCategory(Context c,List<ApplicationInfo>appsInfo){
        mAppsInfo = appsInfo;
        mContext = c;
        
    }
    public static String getAppsCategoryByPackageName(Context c,String packageName){
        if(packageName == null  ){
            return DEFAULT_INDEX_OF_DB[7];
        }
        if(AppsFilter.filterPackageName(packageName, null)){
            return null;
        }
        Cursor cursor = null;
        cursor = c.getContentResolver().query(AppsCategoryProviderURI.CONTENT_URI_CATEGORY_BY_PACKAGE_NAME
                ,null,null, new String[] { packageName }, null);
        if(cursor == null){
            return DEFAULT_INDEX_OF_DB[7];
        }
        try {
            cursor.moveToFirst();
            if(!cursor.isAfterLast()){
                final int nameIndex = cursor.getColumnIndex(AppsCategoryProviderURI.CATEGORY_NAME);
                String cate_name = cursor.getString(nameIndex); 
                return  cate_name;//DEFAULT_INDEX_OF_DB[7];
            }else{
                return DEFAULT_INDEX_OF_DB[7];
            }
        } catch (Exception e) {
            return DEFAULT_INDEX_OF_DB[7];
        }finally{
            cursor.close();
        }
        
    }
    
    
    private List<ResolveInfo> findActivitiesForPackage(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();

        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mainIntent.setPackage(packageName);

        final List<ResolveInfo> apps = packageManager.queryIntentActivities(mainIntent, 0);
        return apps;
    }
    
    
    private String makeNewIntentURI(String packageName,String activityName){
        Intent mBaseIntent = new Intent(Intent.ACTION_MAIN, null);
        mBaseIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mBaseIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        mBaseIntent.setClassName(packageName, activityName);
        return mBaseIntent.toUri(0);
    }
    
    
    @Override
    public Map<String, ArrayList<String>> getCategoryApps() {
        Map<String,ArrayList<String>> appsInfo = new LinkedHashMap<String,ArrayList<String>>();
        
        Iterator<ApplicationInfo> it = mAppsInfo.iterator();
        while(it.hasNext()){
            ApplicationInfo info = it.next();
            if(info.componentName.getPackageName() == null){
                if(AppsClassifiction.DEBUG){
                    Log.d(AppsClassifiction.TAG, "GetAppsCategory->getCategoryApps  :componentName == NULL");
                }
                continue;
            }
            String pn = info.componentName.getPackageName();
            String categoryName = getAppsCategoryByPackageName(mContext,pn);
            if(AppsClassifiction.DEBUG){
                Log.d(AppsClassifiction.TAG, "GetAppsCategory->getCategoryApps  :categoryName =" + categoryName);
            }
            if(categoryName == null){
                continue;
            }
            
            ArrayList<String> intentURI = new ArrayList<String>();
            
            final List<ResolveInfo> matches = findActivitiesForPackage(mContext, pn);
            if (matches != null) {
                int size = matches.size();
                for (int j = 0; j < size; j++) {
                    ResolveInfo rInfo = matches.get(j);
                    intentURI.add(makeNewIntentURI(pn,rInfo.activityInfo.name));
                }
            }
            
            ArrayList<String> tempIntentURI = appsInfo.get(categoryName);
            if(tempIntentURI !=null){
                tempIntentURI.addAll(intentURI);
            }else{
                appsInfo.put(categoryName, intentURI);
            }
            if(AppsClassifiction.DEBUG){
                Log.d(AppsClassifiction.TAG, "GetAppsCategory->getCategoryApps  :categoryName =" + categoryName + "intentURI  =" + intentURI.toString());
            }
        }
        
        return appsInfo;
    }
}
