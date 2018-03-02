package com.lenovo.lejingpin.hw.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class DownloadButton extends TextView{

	public DownloadButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public DownloadButton(Context arg0, AttributeSet arg1, int arg2) {
		
		super(arg0, arg1, arg2);
		// TODO Auto-generated constructor stub
	}

	public DownloadButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setPressed(boolean pressed) {
		// TODO Auto-generated method stub
		if(pressed && ((View)getParent()).isPressed()){
			return;
		}
		super.setPressed(pressed);
	}
	
	

	
}
