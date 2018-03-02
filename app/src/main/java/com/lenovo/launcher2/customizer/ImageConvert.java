package com.lenovo.launcher2.customizer;

import android.content.Context;
import android.graphics.Bitmap;

public interface ImageConvert {
	public Bitmap convert(Context context,Bitmap bitmap,int backGround);

    public Bitmap convert(Context context, Bitmap thumb, Bitmap bg);
}
