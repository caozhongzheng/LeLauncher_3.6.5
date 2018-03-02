/*
 * Copyright (C) 2009 The Android Open Source Project
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

package com.lenovo.launcher2.commoninterface;



import java.lang.reflect.Method;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.XLauncher;
import com.lenovo.launcher.components.XAllAppFace.XLauncherModel;
import com.lenovo.launcher.components.XAllAppFace.XWorkspace;

import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;

/**
 * Represents a widget (either instantiated or about to be) in the Launcher.
 */
public class LauncherAppWidgetInfo extends ItemInfo {
	/** AUT: henryyu1986@163.com DATE: 2011-12-29 S */
//	int icon = -1;
//	String iconPath = "";
	public Intent intent;
	public String label;
	public Bitmap iconBitmap;
//	String uri;
//	FavoriteWidgetView commendView;
	/** AUT: henryyu1986@163.com DATE: 2011-12-29 E */

    /**
     * Indicates that the widget hasn't been instantiated yet.
     */
	public static final int NO_ID = -1;

    /**
     * Identifier for this widget when talking with
     * {@link android.appwidget.AppWidgetManager} for updates.
     */
    public int appWidgetId = NO_ID;

    public ComponentName providerName;
    private boolean mHasNotifiedInitialWidgetSizeChanged;
    // TODO: Are these necessary here?
    public int minWidth = -1;
    public int minHeight = -1;

    /**
     * View that holds this widget after it's been created.  This view isn't created
     * until Launcher knows it's needed.
     */
    public AppWidgetHostView hostView = null;
    
    /** ID: configurable widget restore . AUT: chengliang . DATE: 2012.05.31 . S */
    public int needConfig = 0;
    /** ID: configurable widget restore . AUT: chengliang . DATE: 2012.05.31 . E */

    /**
     * Constructor for use with AppWidgets that haven't been instantiated yet.
     */
    public LauncherAppWidgetInfo(ComponentName providerName) {
        itemType = LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET;
        this.providerName = providerName;

        // Since the widget isn't instantiated yet, we don't know these values. Set them to -1
        // to indicate that they should be calculated based on the layout and minWidth/minHeight
        spanX = -1;
        spanY = -1;
    }

    public LauncherAppWidgetInfo(int appWidgetId) {
        itemType = LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET;
        this.appWidgetId = appWidgetId;
    }

    @Override
    public void onAddToDatabase(ContentValues values) {
        super.onAddToDatabase(values);
        values.put(LauncherSettings.Favorites.APPWIDGET_ID, appWidgetId);
        values.put(LauncherSettings.Favorites.ICON_RESOURCE, icon);
        if(uri != null) {
        	values.put(LauncherSettings.Favorites.URI, uri);
        }
        if(intent != null) {
        	values.put(LauncherSettings.Favorites.INTENT, intent.toUri(0));
        }
        if (label != null) {
        	values.put(LauncherSettings.Favorites.TITLE, label);
        }
        
        /** ID: configurable widget restore . AUT: chengliang . DATE: 2012.05.31 . S */
        values.put(LauncherSettings.Favorites.CONFIGABLE_WIDGET, needConfig);
        /** ID: configurable widget restore . AUT: chengliang . DATE: 2012.05.31 . E */
        
        if( iconBitmap != null ){
        	writeBitmap(values, iconBitmap);
        }
    }

    @Override
    public String toString() {
        return "AppWidget(id=" + Integer.toString(appWidgetId) + ")";
    }

    @Override
    public void unbind() {
        super.unbind();
        hostView = null;
    }

    public LauncherAppWidgetInfo copy() {
        LauncherAppWidgetInfo newInfo = new LauncherAppWidgetInfo(this.appWidgetId);

        newInfo.id = this.id;
        newInfo.cellX = this.cellX;
        newInfo.cellY = this.cellY;
        newInfo.spanX = this.spanX;
        newInfo.spanY = this.spanY;
        newInfo.screen = this.screen;
        newInfo.itemType = this.itemType;
        newInfo.container = this.container;
        newInfo.attachedIndexArray = this.attachedIndexArray;

        newInfo.providerName = this.providerName;
        newInfo.minWidth = this.minWidth;
        newInfo.minHeight = this.minHeight;
        newInfo.hostView = this.hostView;
        newInfo.needConfig = this.needConfig;

        newInfo.intent = this.intent;
        newInfo.label = this.label;
        newInfo.iconBitmap = this.iconBitmap;
        newInfo.uri = this.uri;

        return newInfo;
    }
    private  Rect mTmpRect = new Rect();
    private Rect mLandscapeCellLayoutMetrics = null;
    private Rect mPortraitCellLayoutMetrics = null;
    /**
     * Trigger an update callback to the widget to notify it that its size has changed.
     */
    public void notifyWidgetSizeChanged(XLauncher launcher) {
        mHasNotifiedInitialWidgetSizeChanged = true;
        /*int left = launcher.getWorkspace().getPagedView().getCellCountX();
    	int top = launcher.getWorkspace().getPagedView().getCellCountY();
    	int right = left+launcher.getWorkspace().getPagedView().getCellWidth()*spanX;
    	int bottom = top+launcher.getWorkspace().getPagedView().getCellHeight()*spanY;
    	updateAppWidgetSize(right, bottom,
    			right,bottom);*/
//	updateAppWidgetSize(384, 175,448,216);
	    getWidgetSizeRanges(launcher, spanX, spanY,mTmpRect);
	    updateAppWidgetSize( mTmpRect.left, mTmpRect.top,
	            mTmpRect.right, mTmpRect.bottom);

	}
    private Rect getWidgetSizeRanges(XLauncher launcher, int spanX, int spanY, Rect rect) {
	    if (rect == null) {
	        rect = new Rect();
	    }
	    Rect landMetrics = getCellLayoutMetrics(launcher,0);
	    Rect portMetrics = getCellLayoutMetrics(launcher,1);
	    final float density = launcher.getResources().getDisplayMetrics().density;
	
	    // Compute landscape size
	    int cellWidth = landMetrics.left;
	    int cellHeight = landMetrics.top;
	    int widthGap = landMetrics.right;
	    int heightGap = landMetrics.bottom;
	    int landWidth = (int) ((spanX * cellWidth + (spanX - 1) * widthGap) / density);
	    int landHeight = (int) ((spanY * cellHeight + (spanY - 1) * heightGap) / density);
	
	    // Compute portrait size
	    cellWidth = portMetrics.left;
	    cellHeight = portMetrics.top;
	    widthGap = portMetrics.right;
	    heightGap = portMetrics.bottom;
	    int portWidth = (int) ((spanX * cellWidth + (spanX - 1) * widthGap) / density);
	    int portHeight = (int) ((spanY * cellHeight + (spanY - 1) * heightGap) / density);
	    rect.set(portWidth, landHeight, landWidth, portHeight);
	    return rect;
	}
    
	private  Rect getCellLayoutMetrics(XLauncher launcher, int orientation) {
	    Resources res = launcher.getResources();
	    Display display = launcher.getWindowManager().getDefaultDisplay();
	    Point smallestSize = new Point();
	    Point largestSize = new Point();
	    display.getRealSize(smallestSize);
	    display.getRealSize(largestSize);
	    if (orientation == 0) {
	        if (mLandscapeCellLayoutMetrics == null) {
	            int paddingLeft = res.getDimensionPixelSize(R.dimen.workspace_left_padding_land);
	            int paddingRight = res.getDimensionPixelSize(R.dimen.workspace_right_padding_land);
	            int paddingTop = res.getDimensionPixelSize(R.dimen.workspace_top_padding_land);
	            int paddingBottom = res.getDimensionPixelSize(R.dimen.workspace_bottom_padding_land);
	            int width = largestSize.x - paddingLeft - paddingRight;
	            int height = smallestSize.y - paddingTop - paddingBottom;
	            mLandscapeCellLayoutMetrics = new Rect();
	            getMetrics(mLandscapeCellLayoutMetrics, res,
	                    width, height, XLauncherModel.getCellCountX(), XLauncherModel.getCellCountY(),
	                    orientation);
	        }
	        return mLandscapeCellLayoutMetrics;
	    } else if (orientation == 1) {
	        if (mPortraitCellLayoutMetrics == null) {
	            int paddingLeft = res.getDimensionPixelSize(R.dimen.workspace_left_padding_land);
	            int paddingRight = res.getDimensionPixelSize(R.dimen.workspace_right_padding_land);
	            int paddingTop = res.getDimensionPixelSize(R.dimen.workspace_top_padding_land);
	            int paddingBottom = res.getDimensionPixelSize(R.dimen.workspace_bottom_padding_land);
	            int width = smallestSize.x - paddingLeft - paddingRight;
	            int height = largestSize.y - paddingTop - paddingBottom;
	            mPortraitCellLayoutMetrics = new Rect();
	            getMetrics(mPortraitCellLayoutMetrics, res,
	                    width, height, XLauncherModel.getCellCountX(), XLauncherModel.getCellCountY(),
	                    orientation);
	        }
	        return mPortraitCellLayoutMetrics;
	    }
	    return null;
	}
	private void getMetrics(Rect metrics, Resources res, int measureWidth, int measureHeight,
            int countX, int countY, int orientation) {
        int numWidthGaps = countX - 1;
        int numHeightGaps = countY - 1;

        int widthGap;
        int heightGap;
        int cellWidth;
        int cellHeight;
        int paddingLeft;
        int paddingRight;
        int paddingTop;
        int paddingBottom;

        int maxGap = res.getDimensionPixelSize(R.dimen.workspace_max_gap);
        if (orientation == 0) {
            cellWidth = res.getDimensionPixelSize(R.dimen.workspace_cell_width_land);
            cellHeight = res.getDimensionPixelSize(R.dimen.workspace_cell_height_land);
            widthGap = res.getDimensionPixelSize(R.dimen.workspace_width_gap_land);
            heightGap = res.getDimensionPixelSize(R.dimen.workspace_height_gap_land);
            paddingLeft = res.getDimensionPixelSize(R.dimen.cell_layout_left_padding_land);
            paddingRight = res.getDimensionPixelSize(R.dimen.cell_layout_right_padding_land);
            paddingTop = res.getDimensionPixelSize(R.dimen.cell_layout_top_padding_land);
            paddingBottom = res.getDimensionPixelSize(R.dimen.cell_layout_bottom_padding_land);
        } else {
            // PORTRAIT
            cellWidth = res.getDimensionPixelSize(R.dimen.workspace_cell_width_port);
            cellHeight = res.getDimensionPixelSize(R.dimen.workspace_cell_height_port);
            widthGap = res.getDimensionPixelSize(R.dimen.workspace_width_gap_port);
            heightGap = res.getDimensionPixelSize(R.dimen.workspace_height_gap_port);
            paddingLeft = res.getDimensionPixelSize(R.dimen.cell_layout_left_padding_port);
            paddingRight = res.getDimensionPixelSize(R.dimen.cell_layout_right_padding_port);
            paddingTop = res.getDimensionPixelSize(R.dimen.cell_layout_top_padding_port);
            paddingBottom = res.getDimensionPixelSize(R.dimen.cell_layout_bottom_padding_port);
        }

        if (widthGap < 0 || heightGap < 0) {
            int hSpace = measureWidth - paddingLeft - paddingRight;
            int vSpace = measureHeight - paddingTop - paddingBottom;
            int hFreeSpace = hSpace - (countX * cellWidth);
            int vFreeSpace = vSpace - (countY * cellHeight);
            widthGap = Math.min(maxGap, numWidthGaps > 0 ? (hFreeSpace / numWidthGaps) : 0);
            heightGap = Math.min(maxGap, numHeightGaps > 0 ? (vFreeSpace / numHeightGaps) : 0);
        }
        metrics.set(cellWidth, cellHeight, widthGap, heightGap);
    }
    /**
     * When we bind the widget, we should notify the widget that the size has changed if we have not
     * done so already (only really for default workspace widgets).
     */
    public void onBindAppWidget(XLauncher launcher) {
        if (!mHasNotifiedInitialWidgetSizeChanged) {
        	notifyWidgetSizeChanged(launcher);
        }
    }
    private int updateAppWidgetSize(int left,int top,int right,int bottom)
    {
    	int callProviderIntent = -1;
	    Method m = null;
	    try
	    {
	        m = AppWidgetHostView.class
	            .getMethod("updateAppWidgetSize", new Class[]
	            { Bundle.class, Integer.TYPE,Integer.TYPE,Integer.TYPE,Integer.TYPE,});
	    }
	    catch (NoSuchMethodException e)
	    {
	    	e.printStackTrace();
	    }
	    if (m != null)
	    {
	        try
	        {
	            m.invoke(hostView,
	                     null,
	                     left,top,right,bottom);
	        }
	        catch (Exception e)
	        {
	        	e.printStackTrace();
	        }
	    }
	    return callProviderIntent;
    }
}
