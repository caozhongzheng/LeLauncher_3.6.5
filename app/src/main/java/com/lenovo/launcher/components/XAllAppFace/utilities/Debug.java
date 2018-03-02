package com.lenovo.launcher.components.XAllAppFace.utilities;

import android.util.Log;


public class Debug {
	public static class R2 {// for debug only
		final static boolean DEBUG = false;
		static final String TAG = "R5";
		
		public static void echo(Object o) {
			if (DEBUG)
				android.util.Log.i(TAG, "" + o.toString() + "\n");
		}

		/**
		 * Print the list of who called this method.
		 * 
		 * @param tag The logcat tag. You can enter "adb logcat -s tag" in DDMS.
		 * 
		 * @author chengliang zhaoxy
		 */
        public static void printStack(String tag) {
            if (DEBUG) {
                StackTraceElement[] stack = Thread.currentThread().getStackTrace();
                StringBuffer print = new StringBuffer();
                print.append("Call ").append(stack[3].getMethodName()).append(":\n");
                for (int i = 4; i < stack.length; i++) {
                    
                        print.append("          at ").append(stack[i].toString()).append("\n");
                        if( i > 4 + 4 ) break;
                    
                }
                Log.d(tag, print.toString());
            }
        }
	} 
	
	public static class D2 {// for debug only
		public final static boolean DEBUG = true;
		static final String TAG = "D2";
		
		public static void echo(Object o) {
			if (DEBUG)
				echo("", o );
		}
		
		public static void echo(String plug, Object o) {
			if (DEBUG) {
				if (!"".equals(plug)) {
					android.util.Log.i(TAG + "@" + plug, "" + o.toString()
							+ "\n");
				} else {
					android.util.Log.i(TAG, "" + o.toString() + "\n");
				}
			}}
			
		}
	} 
