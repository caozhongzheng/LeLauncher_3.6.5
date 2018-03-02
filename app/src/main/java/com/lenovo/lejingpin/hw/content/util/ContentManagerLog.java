package com.lenovo.lejingpin.hw.content.util;

import android.util.Log;

public class ContentManagerLog {
	private static boolean DEBUG = false;
	
	private ContentManagerLog(){};
	
	public static void d(String tag,String msg){
		if(DEBUG){
			Log.d(tag, "####################################");
			Log.d(tag, msg);
			Log.d(tag, "####################################");
		}
	}

}
