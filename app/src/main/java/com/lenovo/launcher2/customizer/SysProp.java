package com.lenovo.launcher2.customizer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SysProp {
	
	private static Method sysPropGet;
	
	static {
	    try {
	        Class<?> S = Class.forName("android.os.SystemProperties");
	        Method M[] = S.getMethods();
	        //Log.e(MobileLogActivity.LOG_TAG, "Methods are: ");
	        for (Method m : M) {
	            //Log.e(MobileLogActivity.LOG_TAG, "\t" + m);
	            String n = m.getName();
	            if (n.equals("get")) {
	                sysPropGet = m;
	            } 
	        }
	    } catch (ClassNotFoundException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	}
    
    public static String get(String name, String default_value) {
        try {
            return (String) sysPropGet.invoke(null, name, default_value);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return default_value;
    }
}
