/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lenovo.launcher.components.XAllAppFace;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.commoninterface.LauncherAppWidgetInfo;
import com.lenovo.launcher2.commonui.LauncherAppWidgetHostView;
import com.lenovo.launcher2.customizer.GlobalDefine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

/*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. S***/
/**
 * FavoriteWidgetView
 * 
 * @author Henry yu
 */
public class FavoriteWidgetView extends LauncherAppWidgetHostView {
	private LauncherAppWidgetInfo mAppWidgetInfo;

	public FavoriteWidgetView(Context context, LauncherAppWidgetInfo appWidgetInfo) {
        this(context, null, appWidgetInfo);
    }

    public FavoriteWidgetView(Context context, AttributeSet attrs, LauncherAppWidgetInfo appWidgetInfo) {
        this(context, attrs, 0, appWidgetInfo);
    }

    public FavoriteWidgetView(Context context, AttributeSet attrs, int defStyle, LauncherAppWidgetInfo appWidgetInfo) {
    	super( context );
    	//Bitmap bitmap = appWidgetInfo.iconBitmap;
        Bitmap bitmapTmp = appWidgetInfo.iconBitmap.copy(Bitmap.Config.ARGB_8888, true);
        /* RK_ID: RK_WIDGET_BUG . AUT: SHENCHAO1 . DATE: 2013-08-22 . S */
        Bitmap bitmap = null;
        if(appWidgetInfo.needConfig == 1){
        	bitmap = com.lenovo.launcher2.customizer.Utilities.newInstance().bitmapStampForWidgetWithSnap( 
        			bitmapTmp, 
        			GlobalDefine.getConfigurableAppWidgetTagEnableState() ? 
        					((BitmapDrawable)getResources().getDrawable(R.drawable.stamp_widget_config)).getBitmap() : null);    
        }else{        	
        			bitmap = com.lenovo.launcher2.customizer.Utilities.newInstance().bitmapStampForWidgetWithSnap( 
        			bitmapTmp, 
        			((BitmapDrawable)getResources().getDrawable(R.drawable.stamp_widget)).getBitmap());        
        }
        /* RK_ID: RK_WIDGET_BUG . AUT: SHENCHAO1 . DATE: 2013-08-22 . E */
        
        setBackgroundDrawable( new BitmapDrawable(getResources(), bitmap));
//        setImageBitmap(bitmap);
        mAppWidgetInfo = appWidgetInfo;
    }

    public void removeSelf() {
    	ViewParent parent = getParent();
    	if(parent instanceof ViewGroup) {
    		((ViewGroup) parent).removeView(this);
    	}
    }
	public void setInfo(LauncherAppWidgetInfo appWidgetInfo) {
		this.mAppWidgetInfo = appWidgetInfo;
	}

	public LauncherAppWidgetInfo getInfo() {
		return mAppWidgetInfo;
	}
}
