package com.lenovo.lejingpin.hw.content.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;


public class LocalAppUtil {
	private String TAG = "LocalAppUtil";
	
	private Context context;
	
	public LocalAppUtil(Context context){
		this.context = context;
	}
	
	private List<PackageInfo> getAllPackageInfo(){
		List<PackageInfo> list = null;
		try{
			list= context.getPackageManager().getInstalledPackages(0);
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	public HashMap<String,String[]> getAllMap(){
		List<PackageInfo> list = getAllPackageInfo();
		List<String> packageNameList = new ArrayList<String>();
		List<String> versionCodeList = new ArrayList<String>();
		HashMap<String,String[]> map = new HashMap<String,String[]>();
		if(list!=null && !list.isEmpty()){
			for(PackageInfo info : list){
				String packageName = info.packageName;
				String versionCode = String.valueOf(info.versionCode);
				packageNameList.add(packageName);
				versionCodeList.add(versionCode);
			}
		}
		map.put("package_name", packageNameList.toArray(new String[]{}));
		map.put("version_code", versionCodeList.toArray(new String[]{}));
		
		return map;
	}
	
	public  HashMap<String,String> getLocalAllMap(){
		HashMap<String,String> map = null;
		try{
			List<PackageInfo> list = getAllPackageInfo();
			map = new HashMap<String,String>();
			if(list!=null && !list.isEmpty()){
				for(PackageInfo info : list){
						String packageName = info.packageName;
						int versionCodeInt = 0;
						try {
							versionCodeInt = info.versionCode;
						} catch (Exception e) {
							e.printStackTrace();
						}
						ContentManagerLog.d(TAG, "getAllLauncherMap >>  packageName : "+packageName +"; versionCodeInt : "+versionCodeInt);
						map.put(packageName, String.valueOf(versionCodeInt));
					}
				}
			ContentManagerLog.d(TAG, "getLocalAllMap >> size : "+map.size());
		}catch(Exception e){
			ContentManagerLog.d(TAG, "get local app error");
			e.printStackTrace();
		}
		
		return map;
	}
}
