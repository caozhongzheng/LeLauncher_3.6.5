package com.lenovo.senior.utilities;

import android.graphics.Bitmap;

public class NativeMethods {

	public static native void nBlur(int[] src,int[] dst,int width,int height, int degree); 

	public static native void stackBoxBlur(int[] src,int width,int height, int degree);

	public static native void fastBlur(Bitmap src, Bitmap store, int width, int height, int degree);

}
