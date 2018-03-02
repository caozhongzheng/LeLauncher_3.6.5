package com.lenovo.launcher2.commonui;

import android.view.View;

public interface Page {
	
	public int getPageChildCount();
    public View getChildOnPageAt(int i);
    public void removeAllViewsOnPage();
    public void removeViewOnPageAt(int i);
    public int indexOfChildOnPage(View v);

}

