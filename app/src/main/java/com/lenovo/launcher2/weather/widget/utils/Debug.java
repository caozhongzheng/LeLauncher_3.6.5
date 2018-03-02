package com.lenovo.launcher2.weather.widget.utils;

public class Debug {
	public static class R2 {// for debug only
		final static boolean DEBUG = true;
		static final String TAG = "R5";
		
		public static void echo(Object o) {
			if (DEBUG)
				android.util.Log.i(TAG, "" + o.toString() + "\n");
		}
	} 
}