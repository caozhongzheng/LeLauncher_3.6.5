package com.lenovo.senior.utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;

public class Utilities {

	public Utilities(){
		
	}
	
	public void copyAssets(Context context, String fromwhich, String towhere) {
		AssetManager am = context.getAssets();
		try {
			InputStream is = am.open(fromwhich);
			OutputStream os = new FileOutputStream(new File(towhere));
			copyFile(is, os);
			android.util.Log.i("file", "file : " + fromwhich + " , " + towhere);
		} catch (Exception e) {
			android.util.Log.i("file", "fail to copy.");
		}
	}

	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
		
		try {
			out.flush();
			in.close();
			out.close();
		} catch (Exception e) {
		}
	}

	public static long blur( Bitmap src, Bitmap store, int degree ){
		long current = System.currentTimeMillis();
		int w = src.getWidth(), h = src.getHeight();
		int[] pixOrigin = new int[w * h];
		src.getPixels(pixOrigin, 0, w, 0, 0, w, h);
		if( store.isRecycled() || ! store.isMutable() ){
			store = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		}
		int[] pixStore = new int[w * h];
		store.getPixels( pixStore, 0, w, 0, 0, w, h );
		NativeMethods.nBlur( pixOrigin, pixStore, w, h, degree);		
		store.setPixels( pixStore, 0, w, 0, 0, w, h);
		long performance = System.currentTimeMillis() - current;
		return performance;
	}
	
	public static long stackBoxBlur( Bitmap src, Bitmap store, int degree ){
		long current = System.currentTimeMillis();
		int w = src.getWidth(), h = src.getHeight();
		int[] pixOrigin = new int[w * h];
		src.getPixels(pixOrigin, 0, w, 0, 0, w, h);
		if( store.isRecycled() || ! store.isMutable() ){
			store = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		}
		NativeMethods.stackBoxBlur( pixOrigin, w, h, degree);	
		store.setPixels( pixOrigin, 0, w, 0, 0, w, h);
		long performance = System.currentTimeMillis() - current;
		return performance;
	}
	
    public static long fastBlur(Bitmap src, Bitmap store, int degree) {
        long current = System.currentTimeMillis();
        int w = src.getWidth(), h = src.getHeight();
        if (store.isRecycled() || !store.isMutable()) {
            store = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        }
        NativeMethods.fastBlur(src, store, w, h, degree);
        long performance = System.currentTimeMillis() - current;
        return performance;
    }
}
