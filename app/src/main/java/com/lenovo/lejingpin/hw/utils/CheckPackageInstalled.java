package com.lenovo.lejingpin.hw.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class CheckPackageInstalled {
	
	private CheckPackageInstalled(){};
	
	// check whether a package with the packageName is installed
	public static boolean checkApplication(Context context,String packageName){
		if (packageName == null || "".equals(packageName)){
			return false;  
		}else{
			try {  
		         context.getPackageManager().getApplicationInfo(  
		                packageName, PackageManager.GET_UNINSTALLED_PACKAGES);  
		         return true;  
		     } catch (NameNotFoundException e) {  
		        return false;  
		    }  
		}
	}
}
