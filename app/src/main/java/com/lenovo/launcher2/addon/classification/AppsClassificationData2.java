package com.lenovo.launcher2.addon.classification;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.util.Log;

/**
 * 该类是在launcher启动後，触发分类事件所使用的类
 * @author gecn1
 * 
 */
public class AppsClassificationData2 implements AppsClassificationData {
    private Context mContext;
    private Map<String,List<String>> mAllAppsInfo;
    private Set<String> mInstalledPackages ;
    
    private  final String[] DEFAULT_INDEX_OF_DB = {
            "游戏",//游戏
            "阅读教育",//阅读教育
            "系统工具",//系统工具
            "通信社交",//通信社交
            "影音娱乐",//影音娱乐
            "生活地图",//生活地图
            "办公商务",//办公商务
            "其他"//其它
        };


    public AppsClassificationData2(Context context) {
        mContext = context;
        mAllAppsInfo  =new LinkedHashMap<String, List<String>>();
        mInstalledPackages = new HashSet<String>();
    }
    
    /**
     * 
     * @param Context c
     * @return if no packages set size =0;
     */
    private void  getInstalledPackages(Context c ,Map<String,List<String>> allAppsInfo,Set<String> packages){
        final PackageManager packageManager = c.getPackageManager();

        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<String> inttentURIList  = new ArrayList<String>();
        
        final List<ResolveInfo> apps = packageManager.queryIntentActivities(mainIntent, 0);
        Iterator<ResolveInfo> it = apps.iterator();
        if(!it.hasNext()){
            return ;
        }
//        ResolveInfo firstR = it.next();
        String prePkgName = null;//firstR.activityInfo.packageName;
        String intentURI= null ; //= makeNewIntentURI(prePkgName, firstR.activityInfo.name);
//        inttentURIList.add(intentURI);
//        allAppsInfo.put(prePkgName, inttentURIList);
        boolean isExist = true;
        while(it.hasNext()){
            ResolveInfo r = it.next();
            String pkgName = r.activityInfo.packageName;
            if(AppsFilter.filterPackageName(pkgName,r.activityInfo.name)){
                continue;
            }
//            Log.d("testcategory", "-------- prePkgName = " + prePkgName );

            
            intentURI = makeNewIntentURI(pkgName, r.activityInfo.name);//pkgName +"/"+ r.activityInfo.name+"/" + r.loadLabel(packageManager);
            isExist = !packages.add(pkgName);
            if(isExist){
                if(pkgName.equals(prePkgName)){
                    inttentURIList.add(intentURI);
                }else{
                    allAppsInfo.put(prePkgName, inttentURIList);
                    inttentURIList=allAppsInfo.get(pkgName);
                    inttentURIList.add(intentURI);
                    prePkgName = pkgName;
                }
            }else{
                if(prePkgName != null){
                    allAppsInfo.put(prePkgName, inttentURIList);
                }
                inttentURIList = new ArrayList<String>();
                inttentURIList.add(intentURI);
                prePkgName = pkgName;
                
            }
        }
        
        if(prePkgName != null){
            Log.d("testcategory", "-------- set  prePkgName = " + prePkgName );
            Log.d("testcategory", "-------- set  activityInfo = " + inttentURIList.toString() );
            allAppsInfo.put(prePkgName, inttentURIList);
        }
        
        return ;
    }
    
    
    
    private String makeNewIntentURI(String packageName,String activityName){
        Intent mBaseIntent = new Intent(Intent.ACTION_MAIN, null);
        mBaseIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mBaseIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        mBaseIntent.setClassName(packageName, activityName);
        return mBaseIntent.toUri(0);
    }
    
    private Map<String,List<String>> getAllCategoryPackagesFromDataBase(Context c,String whereQuery){
        
        Map<String,List<String>> appsCategorys  =new LinkedHashMap<String, List<String>>();
        
        //query
        Cursor cursor = null;
        cursor = c.getContentResolver().query(AppsCategoryProviderURI.CONTENT_URI_CATEGORY_AND_APPS
                ,null,null, new String[] { whereQuery }, null);
        
        if(cursor ==null ){
            return appsCategorys;
        }
        try {
            cursor.moveToFirst();
            final int nameIndex = cursor.getColumnIndexOrThrow(AppsCategoryProviderURI.CATEGORY_NAME);
            final int packageIndex = cursor.getColumnIndexOrThrow(AppsCategoryProviderURI.PACKAGE_NAME);
            ArrayList<String> packagesName =null;// new ArrayList<String>();
            String preName =null;// cursor.getString(nameIndex);
            while(!cursor.isAfterLast()){
                String name = cursor.getString(nameIndex);
                String pkgName = cursor.getString(packageIndex);
//                Log.d("testcategory", "-------- set  name = " + name );
                if(name.equals(preName)){
                    packagesName.add(pkgName);
                }else{
                    if(preName != null){
                        appsCategorys.put(preName, packagesName);
                    }
                    packagesName = new  ArrayList<String>();
                    packagesName.add(pkgName);
                    preName = name;
                }
                mInstalledPackages.remove(pkgName);
                cursor.moveToNext();
                
            }
            if(preName != null){
//                Log.d("testcategory", "-------- set  preName = " + preName );
                appsCategorys.put(preName, packagesName);
            }
            return appsCategorys;
        } catch (Exception e) {
            e.printStackTrace();
            return appsCategorys;
        }finally{
            cursor.close();
        }
    }

    
    
    public final Map<String,ArrayList<String>> getCategoryApps(){
        Map<String,ArrayList<String>> appsInfo = new LinkedHashMap<String,ArrayList<String>>();
        getInstalledPackages(mContext,mAllAppsInfo,mInstalledPackages);
        //查询条件
        String packagesStr = mInstalledPackages.toString();
        StringBuffer whereArgs = null;
        int length = packagesStr.length();
        if(length>2){
            packagesStr = packagesStr.substring(1, length-1);
            packagesStr = packagesStr.replace(", ", "\",\"");
            whereArgs  = new StringBuffer("\"");
            whereArgs.append(packagesStr).append("\"");
            
        }else{
            return appsInfo;
        }
        
        //query database
        Map<String,List<String>> allCategroyApps = getAllCategoryPackagesFromDataBase(mContext,whereArgs.toString());
        Iterator<Map.Entry<String, List<String>>>it = allCategroyApps.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<String, List<String>> map =it.next();
            String categoryName = map.getKey();
            List<String> packagesName = map.getValue();
            ArrayList<String>allAppsInfo = new ArrayList<String>();
            
            Iterator<String> pkgIt = packagesName.iterator();
            while(pkgIt.hasNext()){
                String p = pkgIt.next();
                List<String> l1= mAllAppsInfo.get(p);
                if(l1 != null){
                    allAppsInfo.addAll(l1);
                }else{
                    Log.d("testcategory", "----nooooooooo---- pkgIt  " + p );
                }
            }
            
            //如果存在其他类别 把未进行分类的app 放入其他类别
            if(categoryName.equals(DEFAULT_INDEX_OF_DB[7])){
                Iterator<String> unCategroyPackges = mInstalledPackages.iterator();
                Log.d("testcategory", "\n-------- unCategroyPackges  " + mInstalledPackages.toString()  +"\n\n\n");
                while(unCategroyPackges.hasNext()){
                    String u = unCategroyPackges.next();
                    List<String> l2= mAllAppsInfo.get(u);
                    if(l2 != null){
                        allAppsInfo.addAll(l2);
                    }else{
                        Log.d("testcategory", "---uuuuuuuuuu---unCategroyPackges = " +u );
                    }
                }
                mInstalledPackages.clear();
            }
            
            Log.d("testcategory", "-------- categoryName  " + categoryName );
//            Log.d("testcategory", "-------- allAppsInfo  " + allAppsInfo.toString() );

            appsInfo.put(categoryName, allAppsInfo);
            
        }
        //如果此时存在未分类的apps 创建其他类别
        if(!mInstalledPackages.isEmpty()){
            //创建其他 类别
            ArrayList<String>allAppsInfo = new ArrayList<String>();
            Iterator<String> unCategroyPackges = mInstalledPackages.iterator();
            Log.d("testcategory", "\n-------- create  " + mInstalledPackages.toString()  +"\n\n\n");
            while(unCategroyPackges.hasNext()){
                String u = unCategroyPackges.next();
                List<String> l2= mAllAppsInfo.get(u);
                if(l2 != null){
                    allAppsInfo.addAll(l2);
                }else{
                    Log.d("testcategory", "---create---unCategroyPackges = " +u );
                }
            }
            appsInfo.put(DEFAULT_INDEX_OF_DB[7], allAppsInfo);
        }
        
        return appsInfo;
    }
    
    
    
}
