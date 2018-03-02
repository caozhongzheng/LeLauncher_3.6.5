package com.lenovo.launcher2.customizer;

import android.util.Log;

/**
 * Author : ChengLiang
 * */
public class Debug {
	public static final boolean MAIN_DEBUG_SWITCH = true;

	public static class R2 {// for debug only
		final static boolean DEBUG = true & MAIN_DEBUG_SWITCH;
		static final String TAG = "R2";

		public static void echo(Object o) {
			if (DEBUG)
				android.util.Log.i(TAG, "" + o.toString() + "\n");
		}
	}

	public static class R3 {// for debug only
		final static boolean DEBUG = true & MAIN_DEBUG_SWITCH;
		static final String TAG = "R3";

		public static void echo(Object o) {
			if (DEBUG)
				android.util.Log.i(TAG, "" + o.toString() + "\n");
		}
	}

	public static class R4 {// for debug only
		final static boolean DEBUG = true & MAIN_DEBUG_SWITCH;
		static final String TAG = "R4";

		public static void echo(Object o) {
			if (DEBUG)
				android.util.Log.i(TAG, "" + o.toString() + "\n");
		}
	}
	
	public static class RX {// for debug only
		final static boolean DEBUG = true & MAIN_DEBUG_SWITCH;
		static final String TAG = "RX";

		public static void echo(Object o) {
			if (DEBUG)
				android.util.Log.i(TAG, "" + o.toString() + "\n");
		}
	}

	public static class R5 {// for debug only
		final static boolean DEBUG = true;
		static final String TAG = "R5";

		public static void echo(Object o) {
			if (DEBUG)
				android.util.Log.i(TAG, "" + o.toString() + "\n");
		}
	}
	private static final boolean DEBUG = true;
	private static final String TAG_MAGIC_LAUNCHER = "MAGIC_LAUNCHER";
	
	private static final String PRINT_EXCEPTION = " The exception catched is : ";
	
	public static int v(String msg) {
		if (DEBUG) {
		    return Log.v(TAG_MAGIC_LAUNCHER, msg);
		} else {
			return -1;
		}
	}
	
	public static int v(String msg, Throwable tr) {
		if (DEBUG) {
		    return Log.v(TAG_MAGIC_LAUNCHER, msg, tr);
		} else {
			return -1;
		}
	}
	
	public static int d(String msg) {
		if (DEBUG) {
		    return Log.d(TAG_MAGIC_LAUNCHER, msg);
		} else {
			return -1;
		}
	}
	
	public static int d(String msg, Throwable tr) {
		if (DEBUG) {
		    return Log.d(TAG_MAGIC_LAUNCHER, msg, tr);
		} else {
			return -1;
		}
	}
	
	public static int i(String msg) {
		if (DEBUG) {
		    return Log.i(TAG_MAGIC_LAUNCHER, msg);
		} else {
			return -1;
		}
	}
	
	public static int i(String msg, Throwable tr) {
		if (DEBUG) {
		    return Log.i(TAG_MAGIC_LAUNCHER, msg, tr);
		} else {
			return -1;
		}
	}
	
	public static int w(String msg) {
		if (DEBUG) {
		    return Log.w(TAG_MAGIC_LAUNCHER, msg);
		} else {
			return -1;
		}
	}
	
	public static int w(String msg, Throwable tr) {
		if (DEBUG) {
		    return Log.w(TAG_MAGIC_LAUNCHER, msg, tr);
		} else {
			return -1;
		}
	}
	
	public static int wtf(String msg) {
		if (DEBUG) {
		    return Log.wtf(TAG_MAGIC_LAUNCHER, msg);
		} else {
			return -1;
		}
	}
	
	public static int wtf(String msg, Throwable tr) {
		if (DEBUG) {
		    return Log.wtf(TAG_MAGIC_LAUNCHER, msg, tr);
		} else {
			return -1;
		}
	}
	
	public static int e(String msg) {
		if (DEBUG) {
		    return Log.e(TAG_MAGIC_LAUNCHER, msg);
		} else {
			return -1;
		}
	}
	
	public static int e(String msg, Throwable tr) {
		if (DEBUG) {
		    return Log.e(TAG_MAGIC_LAUNCHER, msg, tr);
		} else {
			return -1;
		}
	}
	
	public static int printException(String msg, Throwable tr) {
		if (DEBUG) {
		    return Log.e(TAG_MAGIC_LAUNCHER, msg + PRINT_EXCEPTION + tr.toString());
		} else {
			return -1;
		}
	}
	private Debug() {
		// TODO Auto-generated constructor stub
	}
}
