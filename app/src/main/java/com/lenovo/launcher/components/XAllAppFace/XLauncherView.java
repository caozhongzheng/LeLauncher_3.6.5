package com.lenovo.launcher.components.XAllAppFace;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;
import com.lenovo.launcher2.backup.BackupManager;
import com.lenovo.launcher2.commoninterface.ApplicationInfo;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.Debug.R5;

public class XLauncherView extends XContext {
	
    /*** RK_ID: DRAG. AUT: zhaoxy . DATE: 2013-02-19 . START ***/
	private XDragLayer mDragLayer;
    private XDragController mDragController;
    /*** RK_ID: DRAG. AUT: zhaoxy . DATE: 2013-02-19 . END ***/
    private XApplistView mAppListView;
	private XWorkspace mWorkspace;
	private XHotseat mHotseat;
	private XScreenMngView mScreenMngView;
	private XDeleteDropTarget mDeleteDropTarget;
	//private XShadowDrawableItem mTopshadow;
	private XWallpapperBlur mWallpapperBlur;

	public XLauncherView(Context context) {
		super(context);
	}

	public XLauncherView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

    /*** RK_ID: DRAG. AUT: zhaoxy . DATE: 2013-02-19 . START ***/
	public XDragLayer getDragLayer() {
	    return mDragLayer;
	}
    /*** RK_ID: DRAG. AUT: zhaoxy . DATE: 2013-02-19 . END ***/

	public XApplistView getApplistView() {
		return mAppListView;
	}

	public XWorkspace getWorkspace() {
		return mWorkspace;
	}

	public XWallpapperBlur getWallpapperBlur() {
	    return mWallpapperBlur;
	}

	public XScreenMngView getScreenMngView() {
		return mScreenMngView;
	}
		private DisplayMetrics metrics ;

	@Override
	public void init() {
		super.init();

		WindowManager wm = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		Display dl = wm.getDefaultDisplay();
		//DisplayMetrics metrics = new DisplayMetrics();
		metrics = new DisplayMetrics();
		dl.getMetrics(metrics);

		/*** RK_ID: DRAG. AUT: zhaoxy . DATE: 2013-02-19 . START ***/
		mDragController = new XDragController((XLauncher) mContext);
		/*** RK_ID: DRAG. AUT: zhaoxy . DATE: 2013-02-19 . END ***/

        /*** RK_ID: XFOLDER. AUT: zhaoxy . DATE: 2013-01-22 . START ***/
        final RectF sizeR = new RectF(0, 0, metrics.widthPixels, metrics.heightPixels);
        mDragLayer = new XDragLayer(this, sizeR);
        mDragLayer.setup(mDragController);
        getExchangee().getDrawingPass().addDrawingItem(mDragLayer);
        /*** RK_ID: XFOLDER. AUT: zhaoxy . DATE: 2013-01-22 . END ***/
        
      //  mTopshadow = new XShadowDrawableItem(this);
      //  mDragLayer.addItem(mTopshadow);
        
        //add by zhanggx1 for new layout.s
//        final boolean hasExtraMarginTop = SettingsValue.hasExtraTopMargin();
        final int extraMarginTop = SettingsValue.getExtraTopMargin();
        //add by zhanggx1 for new layout.e
        
        boolean single = SettingsValue.getSingleLayerValue(mContext);
        Log.i(TAG, "init~~ single is  ====" + single);
        if (!single) {
            mAppListView = new XApplistView(this, new RectF(0, extraMarginTop, metrics.widthPixels,
                    metrics.heightPixels));
            /*** RK_ID: DRAG. AUT: zhaoxy . DATE: 2013-02-19 . START ***/
            mDragLayer.addItem(mAppListView);
            /*** RK_ID: DRAG. AUT: zhaoxy . DATE: 2013-02-19 . END ***/
            mAppListView.setVisibility(false);
        }
		
		mWorkspace = new XWorkspace(this, getWorkspaceRect(metrics.widthPixels, metrics.heightPixels));
		mWorkspace.setup(mDragController);
		mWorkspace.setOnLongClickListener((XLauncher) getContext());
       /*** RK_ID: XFOLDER. AUT: zhaoxy . DATE: 2013-01-22 . START ***/
		mDragLayer.addItem(mWorkspace);
       /*** RK_ID: XFOLDER. AUT: zhaoxy . DATE: 2013-01-22 . END ***/
        /*** RK_ID: DRAG. AUT: zhaoxy . DATE: 2013-02-19 . START ***/
        if (mAppListView != null) {
            mAppListView.setup(mDragController);
        }
        /*** RK_ID: DRAG. AUT: zhaoxy . DATE: 2013-02-19 . END ***/

        final int hotseat_height = this.getResources().getDimensionPixelSize(
                R.dimen.button_bar_height_plus_padding);
        mHotseat = new XHotseat(this, new RectF(0, metrics.heightPixels - hotseat_height,
                metrics.widthPixels, metrics.heightPixels));
        mHotseat.setup(mDragController);
        /*** RK_ID: XFOLDER. AUT: zhaoxy . DATE: 2013-01-22 . START ***/
        mDragLayer.addItem(mHotseat);
        /*** RK_ID: XFOLDER. AUT: zhaoxy . DATE: 2013-01-22 . END ***/
        
        mScreenMngView = new XScreenMngView(this, new RectF(0, extraMarginTop, metrics.widthPixels, metrics.heightPixels));
        mScreenMngView.setVisibility(false);
        mDragLayer.addItem(mScreenMngView);

        mDeleteDropTarget = new XDeleteDropTarget(this, new RectF(0, 0, metrics.widthPixels,
                getContext().getResources().getDimensionPixelSize(R.dimen.delete_zone_height)));
        mDeleteDropTarget.setVisibility(false);
        mDeleteDropTarget.setup(mDragLayer, mDragController);
        mDragLayer.addItem(mDeleteDropTarget);
        
        mWallpapperBlur = new XWallpapperBlur(this, mDragLayer);
	}
	
	/*private*/ RectF getWorkspaceRect(int screenWidth, int screenHeight) {
		int wkPaddingLeft = 0;//getResources().getDimensionPixelSize(
                //R.dimen.workspace_screen_padding_left);
		int wkPaddingTop = getResources().getDimensionPixelSize(
                R.dimen.workspace_screen_padding_top);
		int wkPaddingRight = 0;//getResources().getDimensionPixelSize(
                //R.dimen.workspace_screen_padding_right);
		int wkPaddingBottom = getResources().getDimensionPixelSize(
                R.dimen.workspace_screen_padding_bottom);
		int cellPaddingTop = 0;//getResources().getDimensionPixelSize(
//                R.dimen.workspace_top_padding) / 2;
		final int hotseat_height = this.getResources().getDimensionPixelSize(
                R.dimen.button_bar_height_plus_padding);


		phoneindex = SettingsValue.getCurrentMachineType(mContext);
//            int phoneindex = SettingsValue.getCurrentMachineType(mContext);
//            if (phoneindex == 0 && !SettingsValue.isCurrentPortraitOrientation(mContext)){
//		return new RectF(wkPaddingLeft, wkPaddingTop + cellPaddingTop,
//                screenWidth - wkPaddingRight - hotseat_height, 
//                screenHeight -cellPaddingTop );
//            }
//		LauncherApplication app = (LauncherApplication) getContext().getApplicationContext();
		//int cellPaddingBottom = app.mLauncherContext.getDimensionPixel(
          //      R.dimen.home_point_height, R.dimen.def__home_point_height);/*getResources().getDimensionPixelSize(
          //      R.dimen.workspace_bottom_padding) * 3;*/
		//add by zhanggx1 for new layout.s
		int extraMarginTop = SettingsValue.getExtraTopMargin();
		//add by zhanggx1 for new layout.e
		return new RectF(wkPaddingLeft, wkPaddingTop + cellPaddingTop + extraMarginTop,
                screenWidth - wkPaddingRight, 
                screenHeight - hotseat_height - wkPaddingBottom);
	}

	public void setApps(ArrayList<ApplicationInfo> apps) {
        if (mAppListView == null) {
            return;
        }
		final ArrayList<ApplicationInfo> infos = apps;
		mAppListView.setDataReady();
		mAppListView.getAppContentView().setApps(infos);
		getRenderer().invalidate();
	}
	private final static int MSG_REFRESH =0x100001;
	private final static int MSG_REFRESH_NEW =0x100002;
	private final static int MSG_REFRESH_NEW2 =0x100003;
	private final static int MSG_REFRESH_NEW3 =0x100004;
	private final static int MSG_REFRESH_TIMEOUT =150;
	private int mtimeout_count = 0;
	private int phoneindex =-1;
	private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_REFRESH_NEW2:
                    Log.d("PAD0516","re onSizeChanged freezingOrientation");
                    XLauncher ll2 = (XLauncher) getContext();
                    com.lenovo.launcher2.customizer.Utilities.newfreezingOrientation(ll2);
                break;
                case MSG_REFRESH_NEW:
                    Log.d("PAD0516","re onSizeChanged freezingOrientation port");
                    XLauncher ll = (XLauncher) getContext();
                    com.lenovo.launcher2.customizer.Utilities.newfreezingOrientation(ll, false);
                    mHandler.sendMessageDelayed( mHandler.obtainMessage(MSG_REFRESH_NEW2),300);
                break;
                case MSG_REFRESH_NEW3:
                    Log.d("PAD0516","re onSizeChanged freezingOrientation land");
                    XLauncher lll = (XLauncher) getContext();
                    com.lenovo.launcher2.customizer.Utilities.newfreezingOrientation(lll, true);
                    mHandler.sendMessageDelayed( mHandler.obtainMessage(MSG_REFRESH_NEW2),350);
                break;
                case MSG_REFRESH:
                	XLauncher l = (XLauncher) getContext();
                	Log.d("PAD0516","getConfigureState refresh 12022222222222222222222");
                	//if(l.getConfigureState()){
                	//if(true){
                		mHandler.removeMessages(MSG_REFRESH);
                		final Bundle bunld = (Bundle)msg.obj;
	                	//MsgResize(bunld.getInt("w"),bunld.getInt("h"),
	                	//		bunld.getInt("oldw1"),bunld.getInt("oldh1"));
	                	onSizeChanged(bunld.getInt("w"),bunld.getInt("h"),
	                			bunld.getInt("oldw1"),bunld.getInt("oldh1"));
	                	l.setConfigureState(false);
                	/*}else{
                		mtimeout_count++;
                		if(mtimeout_count<5){
							Message message = mHandler.obtainMessage(MSG_REFRESH,(Bundle)msg.obj);  
	    					mHandler.sendMessageDelayed(message,MSG_REFRESH_TIMEOUT);
                		}else{
                			mHandler.removeMessages(MSG_REFRESH);
                			mtimeout_count =0;
                		}
                	}*/
                	break;
            	default:
            		break;
            }
        }
	};
	private void MsgResize(int w,int h,int oldw,int oldh)
	{
		XLauncher l = (XLauncher) getContext();
		
		 // No more use
//        if(SettingsValue.getCurrentMachineType( l ) == -1){
//            if( !l.hasFocus() || ! l.isLauncherAtStackTop()){
//            	if (mHotseat != null) {
//            		final int hotseat_height = this.getResources().getDimensionPixelSize(
//            				R.dimen.button_bar_height_plus_padding);
//            		mHotseat.resize(new RectF(0, h - hotseat_height, w, h));
//
//            	}
//            	return;
//            }
//        }
		
		int padding =l .getStatusBarHeight();
        WindowManager wm = (WindowManager) l 
                .getSystemService(Context.WINDOW_SERVICE);
		Display dl = wm.getDefaultDisplay();
		metrics = new DisplayMetrics();
		dl.getMetrics(metrics);
		if (mDragLayer != null) {
            mDragLayer.resize(new RectF(0, 0, w, h));
        }
		
		//add by zhanggx1 for new layout.s
		int extraMarginTop = SettingsValue.getExtraTopMargin();
		boolean hasMarginTop = SettingsValue.hasExtraTopMargin();
		//add by zhanggx1 for new layout.e
        
        //由于长按时系统栏消失，此时重新调用onSizeChanged，此时高度变化，但是宽度不变。如下不需要重新resize。
//        if (w != oldw)
//        {  
    		if (mAppListView != null) {
			mAppListView.resize(new RectF(0, extraMarginTop, metrics.widthPixels, h));
    		}
    
            mDeleteDropTarget.resize(new RectF(0, 0, w, getContext().getResources()
                    .getDimensionPixelSize(R.dimen.delete_zone_height)));
//        }
//           int phoneindex = SettingsValue.getCurrentMachineType(mContext);
        if (mHotseat != null) {
            final int hotseat_height = l.getResources().getDimensionPixelSize(
                    R.dimen.button_bar_height_plus_padding);
//            if(phoneindex == 0){
//            if (SettingsValue.isCurrentPortraitOrientation(mContext)){
//            mHotseat.resize(new RectF(0, h - hotseat_height, w, h));
//            }else{
//            mHotseat.resize(new RectF(w-hotseat_height,0, w, h));
//            }
//            }else{
        //    mHotseat.refreshLocationPrepareForConfigeChange();
            mHotseat.resize(new RectF(0, h - hotseat_height, w, h));

//            }
        }
        
        final boolean flag = l.isCurrentWindowFullScreen();

        if (mWorkspace != null) {
            RectF r = getWorkspaceRect(w, h);
            if (flag && !hasMarginTop) {
                r.offset(0, padding);
                r.bottom -= padding;
            }
            mWorkspace.resize(r);
        }

        if (flag) {
            mDeleteDropTarget.setVisibility(true);
            mDragLayer.bringChildToFront(mDeleteDropTarget);
            mDragLayer.checkDragViewToFront();
            mDragController.addDropTarget(mDeleteDropTarget);
          //  mTopshadow.setVisibility(false);
        } else {
            mDeleteDropTarget.setVisibility(false);
            mDragController.removeDropTarget(mDeleteDropTarget);
         //   mTopshadow.setVisibility(true);

        }

        if (mScreenMngView != null) {
            RectF screenRect = new RectF(0, extraMarginTop, w, h);
            if (flag && !hasMarginTop) {
                screenRect.bottom -= padding;
            }
            mScreenMngView.resize(screenRect);
        }
        if (mScreenMngView != null
        		&& (mScreenMngView.isVisible() || l.isAnimPreviewScreen())
        		&& !l.isAnimCloseScreen()) {
        	mScreenMngView.onConfigurationChange();
        }
        l.updateIconSizeValue();
	}
	private boolean mfirst = true;
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		XLauncher l = (XLauncher) getContext();
        if(l == null){
        	return;
        }
        
        // block phone resize
		if (SettingsValue.getCurrentMachineType(l) == -1) {
			// EXTRA FOR PHONE
			l.confirmPhoneOrientation();
			if (w > h) {
				return;
			}
		}
        
        if (mWorkspace != null) {
            // fix bug 473, by liuli1.
            try {
				mWorkspace.getPagedView().restoreStage();
				XFolder folder = mWorkspace.getOpenFolder();
				if (folder != null) {
				    float y = (int) (mDragLayer.getHeight() - folder.getHeight());
				    folder.setRelativeY(y);
				    l.closeFolder();
				}
			} catch (Exception e) {
			}
        }
        
		R5.echo("onSizeChanged w = " + w + "h = " + h + "oldw = " + oldw + "oldh = " + oldh);
		
		if (w == oldw && h == oldh)
		{		    
		    return;
		}		
                try{
		getExchangee().getLGestureDetector().setOffsetWhenSizeChanged(w - oldw, h - oldh);
		} catch (Exception e) {
                    return;
		}
		final int w1=w;
		final int h1=h;
		final int oldw1=oldw;
		final int oldh1=oldh;
        final boolean flag = l.isCurrentWindowFullScreen();
        //Log.d("PAD0516","getWindowState="+l.getWindowState());
        //Log.d("PAD0516","getConfigureState="+l.getConfigureState());
        //Log.d("PAD0516","onSizeChanged 00000000000000000000000000=");

        if(phoneindex!=-1&&!mfirst&&!l.getConfigureState()&&!l.getWindowState()){
			new Thread(){
				@Override
		        public void run() {
					Bundle bundle = new Bundle();
					bundle.putInt("h", h1);
					bundle.putInt("w", w1);
					bundle.putInt("oldw1", oldw1);
					bundle.putInt("oldh1", oldh1);
					if(mHandler.hasMessages(MSG_REFRESH)){
						mHandler.removeMessages(MSG_REFRESH);
					}
					mtimeout_count =0;
					Message message = mHandler.obtainMessage(MSG_REFRESH,bundle);  
					mHandler.sendMessageDelayed(message,MSG_REFRESH_TIMEOUT);
				}
			}.start();
			return;
        }
        mfirst = false;
        Log.d("PAD0516","onSizeChanged 11111111111111111111111111=");
        l.setWindowState(false);
        l.setConfigureState(false);

        int padding = l.getStatusBarHeight();
                        WindowManager wm = (WindowManager) mContext
                                .getSystemService(Context.WINDOW_SERVICE);
                Display dl = wm.getDefaultDisplay();
                metrics = new DisplayMetrics();
                dl.getMetrics(metrics);
         Log.e("PAD0516","onSizeChanged w====="+w+" h============"+h+" metrics.heigh="+metrics.heightPixels+" metrics.width="+metrics.widthPixels);
        if (mDragLayer != null) {
            mDragLayer.resize(new RectF(0, 0, w, h));
        }        
        
        //add by zhanggx1 for new layout.s
      	int extraMarginTop = SettingsValue.getExtraTopMargin();
      	boolean hasMarginTop = SettingsValue.hasExtraTopMargin();
      	//add by zhanggx1 for new layout.e
      		
        //由于长按时系统栏消失，此时重新调用onSizeChanged，此时高度变化，但是宽度不变。如下不需要重新resize。
//        if (w != oldw)
//        {  
    		if (mAppListView != null) {
			mAppListView.resize(new RectF(0, extraMarginTop, metrics.widthPixels, h));
    		}
            if(mDeleteDropTarget!=null){
            mDeleteDropTarget.resize(new RectF(0, 0, w, getContext().getResources()
                    .getDimensionPixelSize(R.dimen.delete_zone_height)));
            }
//        }
//           int phoneindex = SettingsValue.getCurrentMachineType(mContext);
        if (mHotseat != null) {
            final int hotseat_height = this.getResources().getDimensionPixelSize(
                    R.dimen.button_bar_height_plus_padding);
//            if(phoneindex == 0){
//            if (SettingsValue.isCurrentPortraitOrientation(mContext)){
//            mHotseat.resize(new RectF(0, h - hotseat_height, w, h));
//            }else{
//            mHotseat.resize(new RectF(w-hotseat_height,0, w, h));
//            }
//            }else{
        //    mHotseat.refreshLocationPrepareForConfigeChange();
            mHotseat.resize(new RectF(0, h - hotseat_height, w, h));

//            }
        }
        

        if (mWorkspace != null) {
            RectF r = getWorkspaceRect(w, h);
            if (flag && !hasMarginTop) {
                r.offset(0, padding);
                r.bottom -= padding;
            }
            mWorkspace.getPagedView().configurationChange( true );
            mWorkspace.resize(r);
        }
        if(mWorkspace != null ){
            if( mWorkspace.getOrientationStatus() == 0 ){
                Log.e("PAD0516","resize because port errorrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr");
                com.lenovo.launcher2.customizer.Utilities.newfreezingOrientation(l, true);
                mHandler.sendMessageDelayed( mHandler.obtainMessage(MSG_REFRESH_NEW),300);
            }
            if( mWorkspace.getOrientationStatus() == 1 ){
                Log.e("PAD0516","resize because land errorrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr");
                com.lenovo.launcher2.customizer.Utilities.newfreezingOrientation(l, false);
                mHandler.sendMessageDelayed( mHandler.obtainMessage(MSG_REFRESH_NEW3),300);
            }
        }

        
        if (mWallpapperBlur != null) {
            RectF r = mWallpapperBlur.localRect;
            r.bottom = h;
            mWallpapperBlur.resize(r);
        }

        if(mDeleteDropTarget!=null){
        	
        	if (flag) {
        		mDeleteDropTarget.setVisibility(true);
        		mDragLayer.bringChildToFront(mDeleteDropTarget);
        		mDragLayer.checkDragViewToFront();
        		mDragController.addDropTarget(mDeleteDropTarget);
        		//    mTopshadow.setVisibility(false);
        	} else {
        		mDeleteDropTarget.setVisibility(false);
        		mDragController.removeDropTarget(mDeleteDropTarget);
        		//    mTopshadow.setVisibility(true);
        	}
        }

        if (mScreenMngView != null) {
            RectF screenRect = new RectF(0, extraMarginTop, w, h);
            if (flag && !hasMarginTop) {
                screenRect.bottom -= padding;
            }
            mScreenMngView.resize(screenRect);
        }
        if (mScreenMngView != null
        		&& (mScreenMngView.isVisible() || l.isAnimPreviewScreen())
        		&& !l.isAnimCloseScreen()) {
        	mScreenMngView.onConfigurationChange();
        }
        l.updateIconSizeValue();
      //change for pad zhanglz1 20130521
        /**/
        
        
        // EXTRA FOR PHONE
        l.confirmPhoneOrientation();
	}

	public void addApps(final ArrayList<ApplicationInfo> list) {
        if (mAppListView == null) {
            return;
        }
		mAppListView.getAppContentView().addApps(list);
		getRenderer().invalidate();
	}

	public void removeApps(final List<ApplicationInfo> list) {
	    if (mDeleteDropTarget != null) {
	        mDeleteDropTarget.removeApps(this, list);
	    }
        if (mAppListView == null) {
            return;
        }
		mAppListView.getAppContentView().removeApps(list);
		getRenderer().invalidate();

	}

	public void updateApps(final List<ApplicationInfo> list) {
        if (mAppListView == null) {
            return;
        }
		mAppListView.getAppContentView().updateApps(list);
		getRenderer().invalidate();
	}

	public void hideApps(final List<ApplicationInfo> list) {
        if (mAppListView == null) {
            return;
        }
		mAppListView.getAppContentView().hideApps(list);
		getRenderer().invalidate();

	}

    public XHotseat getHotseat() {
        return mHotseat;
    }
    
    public void updateForTheme(final ArrayList<ApplicationInfo> apps) {
        boolean single = SettingsValue.getSingleLayerValue(getContext());
        Log.i(TAG, "updateForTheme~~ single is  ====" + single);
        if (single || mAppListView == null) {
            return;
        }

		mAppListView.updateTheme(apps);
		getRenderer().invalidate();
	}

	public void updateForText() {
        if (mAppListView == null) {
            return;
        }
		mAppListView.getAppContentView().updateForText();
		getRenderer().invalidate();
	}
	
	public void resizeAppListView() {
        if (mAppListView == null) {
            return;
        }
		RectF rect = new RectF(0, 0,
				this.getWidth(), this.getHeight());		
		mAppListView.resize(rect);
	}
	
	public ArrayList<ApplicationInfo> getApps() {
        if (mAppListView == null) {
            return null;
        }
		return mAppListView.getAppContentView().getApps();
	}
	
    private IBinder mWindowToken;
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mWindowToken = getWindowToken();
        mWorkspace.setWindowToken(mWindowToken);        
    }
    
    protected void onDetachedFromWindow() {
        mWindowToken = null;
    } 
    
    public XDragController getDragController() {
    	return this.mDragController;
    }
    
    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);        
        if( visibility != View.VISIBLE && mAppListView != null){
            mAppListView.clearAppToPostionView();     
        }
    }

    void changeThemes() {
        if (mDeleteDropTarget != null) {
            mDeleteDropTarget.changeThemes();
        }
    }

    //test by liuli 2013-08-06
    int getWindowTop() {
        int[] pos = new int[2];
        getLocationOnScreen(pos);
        Log.i(TAG, "getWindowTop~~~~~pos[1] ====" + pos[1]);
        return pos[1];
    }

    void bringDelteTargetToFront() {
        mDragController.removeDropTarget(mDeleteDropTarget);
        mDragLayer.bringChildToFront(mDeleteDropTarget);
        mDragController.addDropTarget(mDeleteDropTarget);

        mDragLayer.bringDragViewToFront();
    }

    public boolean resetToNormal(XLauncher xLauncher, boolean fromKey) {
        if (mDeleteDropTarget != null) {
            // from KEY_CANCEL, or KEY_HOME
            return mDeleteDropTarget.resetToNormal(xLauncher, this, fromKey);
        }
        return false;
    }

    public void removeDeleteDialog(XLauncher xLauncher) {
        if (mDeleteDropTarget != null) {
            mDeleteDropTarget.onConfigureChanged(xLauncher, this);
        }
    }

    //add by zhanggx1 for new layout.s
    public void showDeleteBarOrNot(boolean show) {
    	if (show) {
            mDeleteDropTarget.setVisibility(true);
            mDragLayer.bringChildToFront(mDeleteDropTarget);
            mDragLayer.checkDragViewToFront();
            mDragController.addDropTarget(mDeleteDropTarget);
        } else {
            mDeleteDropTarget.setVisibility(false);
            mDragController.removeDropTarget(mDeleteDropTarget);
        }
    }
    //add by zhanggx1 for new layout.e
    
    public boolean isScreenMngView()
    {
        if (mScreenMngView != null && mScreenMngView.isVisible())
        {
    		return true;
    	}
        
        return false;
    }
}
