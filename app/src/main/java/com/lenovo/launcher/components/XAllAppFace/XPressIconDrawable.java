package com.lenovo.launcher.components.XAllAppFace;

import android.graphics.Bitmap;

import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;

public class XPressIconDrawable extends XIconDrawable{

	public XPressIconDrawable(XContext context, Bitmap icon) {
		super(context, icon);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void setPressed(boolean pressed) {
		// TODO Auto-generated method stub
		if (pressed)
		{
			setAlpha(.6f);
		}
		else
		{
			setAlpha(1f);
		}
		super.setPressed(pressed);
	}

}
