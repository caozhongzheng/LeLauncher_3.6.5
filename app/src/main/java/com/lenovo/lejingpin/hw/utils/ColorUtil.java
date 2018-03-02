package com.lenovo.lejingpin.hw.utils;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;

public class ColorUtil {
	
	private ColorUtil(){};

	public static Drawable getGradientDrawable(int[] rgb) {
		final int startColor = Color.argb(0, rgb[0], rgb[1], rgb[2]);// Color.parseColor("#00" + rgb);
		final int endColor = Color.argb(255, rgb[0], rgb[1], rgb[2]);//Color.parseColor("#ff"+rgb);

		ShapeDrawable bg = new ShapeDrawable();
		bg.setShape(new Shape() {
			@Override
			public void draw(Canvas canvas, Paint paint) {
				int w = canvas.getWidth();
				int h = canvas.getHeight();
				LinearGradient gradient = new LinearGradient(0, 0, w, 0, new int[] { startColor, endColor },
						new float[] { 0.6f, 1f }, TileMode.CLAMP);
				//					LinearGradient gradient = new LinearGradient(w/2, 0, w, 0,Color.TRANSPARENT, Color.RED, TileMode.CLAMP);
				paint.setShader(gradient);
				paint.setDither(true);
				//				canvas.drawRect(0, 0, w, h, paint);
				RectF r = new RectF(0, 0, w, h);
				canvas.drawRoundRect(r, 4f, 4f, paint);
			}
		});
		return bg;
	}

	/**
	 * 
	 * 获取bitmap 主色
	 * 
	 * @param bitmap
	 * @return
	 */

	public static int[] getAppIconMainColor(Bitmap bitmap){
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int size = width * height;
		int[] rArray = new int[size];
		int[] gArray = new int[size];
		int[] bArray = new int[size];
		int widthSetp = width / 10;
		int heightSetp = height / 10;
		int count = 0;
		for (int i = 0; i < height;){
			for (int j = 0; j < width;) {
				int color = bitmap.getPixel(j, i);
				int r = Color.red(color);
				rArray[count] = r;
				int g = Color.green(color);
				gArray[count] = g;
				int b = Color.blue(color);
				bArray[count] = b;
				count++;
				j = j + widthSetp;
			}
			i = i + heightSetp;
		}
		Integer[] rMaxNumber = getMaxNumber(rArray);
		Integer[] gMaxNumber = getMaxNumber(gArray);
		Integer[] bMaxNumber = getMaxNumber(bArray);
		int rMax = rMaxNumber.length > 0 ? rMaxNumber[0] : 255;
		int gMax = gMaxNumber.length > 0 ? gMaxNumber[0] : 255;
		int bMax = bMaxNumber.length > 0 ? bMaxNumber[0] : 255;
		return new int[] { rMax, gMax, bMax};
	}
	
	/**
	 * 
	 * 获取 数组中出现次数最多的
	 * 
	 * @param array
	 * @return
	 */
	private static Integer[] getMaxNumber(int[] array) {
		int iMaxCount = 0;
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < array.length; i++) {
			int iCount = 0;
			if (array[i] == 0 || array[i] == 255) {
				continue;
			}
			for (int j = 0; j < array.length; j++) {
				if (array[i] == 0 || array[i] == 255) {
					continue;
				}
				if (array[i] == array[j]) iCount++;
			}
			if (iCount > iMaxCount) {
				list.clear();
				list.add(array[i]);
				iMaxCount = iCount;
			} else if (iCount == iMaxCount) {
				if (list.indexOf(array[i]) == -1) list.add(array[i]);
			}
		}
		return list.toArray(new Integer[] {});
	}
}
