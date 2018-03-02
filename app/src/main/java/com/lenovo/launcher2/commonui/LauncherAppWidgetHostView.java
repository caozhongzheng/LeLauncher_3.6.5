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

package com.lenovo.launcher2.commonui;

import android.appwidget.AppWidgetHostView;
import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.RemoteViews;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;
import com.lenovo.launcher.components.XAllAppFace.XLauncher;
import com.lenovo.launcher.components.XAllAppFace.XWallpaperPagedView;
import com.lenovo.launcher2.customizer.Debug.R5;


/**
 * {@inheritDoc}
 */
public class LauncherAppWidgetHostView extends AppWidgetHostView {
    private boolean mHasPerformedLongPress;
    private CheckForLongPress mPendingCheckForLongPress;
    private LayoutInflater mInflater;

    public LauncherAppWidgetHostView(Context context) {
        super(context);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    protected View getErrorView() {
        /* RK_ID: RK_QUICKACTION. AUT: liuli1 . DATE: 2012-05-21 . START */
        mIsError = true;
        /* RK_ID: RK_QUICKACTION. AUT: liuli1 . DATE: 2012-05-21 . END */
        return mInflater.inflate(R.layout.appwidget_error, this, false);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
    	
        // Consume any touch events for ourselves after longpress is triggered
        if (mHasPerformedLongPress) {
            mHasPerformedLongPress = false;
            return true;
        }

        // Watch for longpress events at this level to make sure
        // users can always pick up this widget
        try {
			LauncherApplication la = (LauncherApplication) getContext().getApplicationContext();
			switch (ev.getAction()) {
			    case MotionEvent.ACTION_DOWN: {
//                postCheckForLongClick();
			    	((XWallpaperPagedView)(la.getModel().getLauncher().
			    			getWorkspace().getPagedView())).setGestureEnable( false );
			        break;
			    }
			    
			    case MotionEvent.ACTION_MOVE:
			    	((XWallpaperPagedView)(la.getModel().getLauncher().
			    			getWorkspace().getPagedView())).setGestureEnable( false );
			    	break;

			    case MotionEvent.ACTION_UP:
			    	((XWallpaperPagedView)(la.getModel().getLauncher().
			    			getWorkspace().getPagedView())).setGestureEnable(true);
			    	needCache( true );
			    	break;
			    case MotionEvent.ACTION_CANCEL:
			    	((XWallpaperPagedView)(la.getModel().getLauncher().
			    			getWorkspace().getPagedView())).setGestureEnable( true );
			        break;
			    default:
			    	break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

        // Otherwise continue letting touch events fall through to children
        return false;
    }

    class CheckForLongPress implements Runnable {
        private int mOriginalWindowAttachCount;

        public void run() {
            if ((mParent != null) && hasWindowFocus()
                    && mOriginalWindowAttachCount == getWindowAttachCount()
                    && !mHasPerformedLongPress) {
                if (performLongClick()) {
                    mHasPerformedLongPress = true;
                }
            }
        }

        public void rememberWindowAttachCount() {
            mOriginalWindowAttachCount = getWindowAttachCount();
        }
    }

    private void postCheckForLongClick() {
        mHasPerformedLongPress = false;

        if (mPendingCheckForLongPress == null) {
            mPendingCheckForLongPress = new CheckForLongPress();
        }
        mPendingCheckForLongPress.rememberWindowAttachCount();
        postDelayed(mPendingCheckForLongPress, ViewConfiguration.getLongPressTimeout());
    }

    @Override
    public void cancelLongPress() {
        super.cancelLongPress();

        mHasPerformedLongPress = false;
        if (mPendingCheckForLongPress != null) {
            removeCallbacks(mPendingCheckForLongPress);
        }
    }

    @Override
    public int getDescendantFocusability() {
        return ViewGroup.FOCUS_BLOCK_DESCENDANTS;
    }

    /* RK_ID: RK_QUICKACTION. AUT: liuli1 . DATE: 2012-05-21 . START */
    private boolean mIsError = false;

    protected boolean isErrorView() {
        return mIsError;
    }
    /* RK_ID: RK_QUICKACTION. AUT: liuli1 . DATE: 2012-05-21 . END */
    
    private boolean _needCache = false;
    public boolean needCache(){
    	return _needCache;
    }
    
    public void needCache( boolean $needCache){
    	_needCache = $needCache;
    }
    
    @Override
    public void updateAppWidget(RemoteViews remoteViews) {
//    	R5.echo("updateAppWidget remoteViews " + remoteViews);
    	needCache( true );
    	super.updateAppWidget(remoteViews);
    }
    
    @Override
	protected void prepareView(View view) {
		super.prepareView(view);
		try {
			final LauncherApplication app = (LauncherApplication) getContext()
					.getApplicationContext();
			app.getModel().getCallBack().getLauncherInstance()
					.getXLauncherView().post(new Runnable() {

						@Override
						public void run() {
							try{
							app.getModel().getCallBack().getLauncherInstance()
									.getXLauncherView().getRenderer()
									.invalidate();
							}catch(Exception e){
								
							}
						}
					});
		} catch (Exception e) {
			// FIXME need to handle ?
		}

	}
    
//    public void invalidate() {
//		// TODO Auto-generated method stub
//    	super.invalidate();
//    	R5.echo("AppWidgetHostView invalidate");
//	}
}
