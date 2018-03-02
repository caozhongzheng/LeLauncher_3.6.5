package com.lenovo.launcher.components.XAllAppFace;

import junit.framework.Assert;
import android.appwidget.AppWidgetHostView;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.SystemProperties;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.lenovo.launcher.components.XAllAppFace.ClipableItem.IFragAction;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;
import com.lenovo.launcher2.commoninterface.ItemInfo;
import com.lenovo.launcher2.commonui.LauncherAppWidgetHostView;
import com.lenovo.launcher2.customizer.Debug.R5;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.weather.widget.WeatherWidgetSquareView;

public class XViewContainer extends ClipableItem implements IFragAction {

	private View mParasiteView;
	private FrameLayout.LayoutParams mLayoutParams;
	public static final String ACTION_UPDATE_CACHE = "com.lenovo.launcher.updateViewCache";

	private boolean isAppWidgetHostView = false;
	private boolean isWidgetSquareView = false;

	private Matrix mExtraEffectMatrix = null;

	public static final float PARASITE_VIEW_ALPHA = 0f;
	
	/** Placed to res value in future , in dp */
	public static int WIDGET_EXTRA_MODIFY = 0;
	private static boolean initfirst = true;
	private static boolean hasNavigationBar = false;

	private boolean mNeedForceUpdateCache = false;
	private BroadcastReceiver mExtraHandler = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (Intent.ACTION_TIME_TICK.equals(intent.getAction()) && mHost != null
					&& mHost.isPageMoving) {
				return;
			}

			mNeedForceUpdateCache = true;
		}
	};

	public Matrix extraMatrix() {
		if (mExtraEffectMatrix == null) {
			mExtraEffectMatrix = new Matrix();
		}
		return mExtraEffectMatrix;
	}

	public void extraMatrix(Matrix m) {
		mExtraEffectMatrix = m;
	}

	public static final byte DRAWING_MODE_CACHE = 0;
	public static final byte DRAWING_MODE_VIEW = 1;

	private byte mCurrentDrawingMode = DRAWING_MODE_VIEW;

	public XViewContainer(XContext context, int minWidth, int minHeight, View parasiteView) {
		super(context);
		
//		assert parasiteView != null;
//		assert minWidth >= 0;
//		assert minHeight >= 0;

		if (initfirst) {
	        String navBarOverride = SystemProperties.get("qemu.hw.mainkeys"); 
	        if (! "".equals(navBarOverride)) { 
	            if      (navBarOverride.equals("1")) hasNavigationBar = false; 
	            else if (navBarOverride.equals("0")) hasNavigationBar = true; 
	        }else{
	        	android.util.Log.i( "Razer", "We judge from here . " );
	        	hasNavigationBar =
	        			context.getContext().getResources().getBoolean( com.android.internal.R.bool.config_showNavigationBar );
	        }
	        
	        String model = SystemProperties.get("ro.product.model");
	        if( model.contains( "K910" )
	        		|| model.contains("S960")
	        		|| model.contains("S698")){
	        	hasNavigationBar = false;
	        }
	        
	        if( SettingsValue.getCurrentMachineType(context.getContext()) != -1 ){
	        	hasNavigationBar = true;
	        }
	        
			if( hasNavigationBar ){
				android.util.Log.i( "Razer", "we judge that has navgation bar . " );
				WIDGET_EXTRA_MODIFY = 0;
			}
			WIDGET_EXTRA_MODIFY *= context.getResources().getDisplayMetrics().density;
			initfirst = false;
		}

		localRect = new RectF(0, 0, minWidth, minHeight);

		mParasiteView = parasiteView;

		mParasiteView.setDrawingCacheEnabled(true);

		disableCache();

		resize(localRect);

		mLayoutParams = new FrameLayout.LayoutParams((int) localRect.width(),
				(int) localRect.height() + WIDGET_EXTRA_MODIFY);

		mParasiteView.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				return false;
			}
		});
		mParasiteView.setLongClickable(false);
		mParasiteView.setHapticFeedbackEnabled(false);

		IntentFilter filter = new IntentFilter();
		filter.addAction(SettingsValue.ACTION_LETHEME_APPLING);
		filter.addAction(ACTION_UPDATE_CACHE);
		filter.addAction(SettingsValue.ACTION_REFRESH_LOTUS);
		filter.addAction(SettingsValue.ACTION_REFRESH_LOTUS);
		filter.addAction(Intent.ACTION_LOCALE_CHANGED);
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
		filter.addAction(Intent.ACTION_TIME_CHANGED);
		filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
		filter.addAction(Intent.ACTION_TIME_TICK);
		filter.addAction(Intent.ACTION_USER_PRESENT);
		getXContext().getContext().registerReceiver(mExtraHandler, filter);
		registerExtraHandler = true;

		// hack
		isAppWidgetHostView = mParasiteView instanceof LauncherAppWidgetHostView;
		isWidgetSquareView = mParasiteView instanceof WeatherWidgetSquareView;
	}

	// private RectF clipRectF = new RectF();
	// @Override
	// public void onSetClipRectF(RectF r) {
	// clipRectF = new RectF(r);
	// }

	@Override
	public void onSlot(ItemInfo info, RectF region, final XPagedView host, XPagedViewItem who) {

		super.onSlot(info, region, host, who);

		checkStage();

		// modify spanXY for AppWidget
		if (mParasiteView instanceof AppWidgetHostView) {
			// int maxHeight = (int) getXContext().getResources().getDimension(
			// R.dimen.workspace_cell_height)
			// * info.spanY;
			mLayoutParams = new FrameLayout.LayoutParams((int) localRect.width(),
					(int) localRect.height() + WIDGET_EXTRA_MODIFY);
		}

		if (isWidgetSquareView) {
			WeatherWidgetSquareView view = (WeatherWidgetSquareView) mParasiteView;
			view.setMposx((int) mSlotX);
			view.setMposy((int) mSlotY);
		}

		mLayoutParams.leftMargin = (int) mSlotX + (mHost != null ? mHost.getPageGap() / 2 : 0);
		mLayoutParams.topMargin = (int) mSlotY - WIDGET_EXTRA_MODIFY / 2;

		mCurrentScreen = host.getCurrentPage();

		int visible = mCurrentScreen == info.screen ? VISIBILITY_SHOW_VIEW : VISIBILITY_SHOW_NONE;
		if (hasMoved) {
			visible = VISIBILITY_SHOW_SHADOW;
		}

		final boolean fromResize = host.configurationChange();
		manageVisibility(host.getStage(), mParasiteView, visible, new Runnable() {

			@Override
			public void run() {

				if( ! hasNavigationBar ){
					mLayoutParams.height = (int) (localRect.height() + WIDGET_EXTRA_MODIFY);
				}
				if (mParasiteView.getParent() == null) {
					host.getStage().addView(mParasiteView, mLayoutParams);
				}

				mParasiteView.requestLayout();
				mParasiteView.invalidate();
				mParasiteView.setAlpha(PARASITE_VIEW_ALPHA);
				if (!fromResize) {
					buildViewCache();
				}
				hasMoved = false;

				getXContext().getRenderer().invalidate();
			}
		});
		mHostedItem = who;
		cellLen = who.getCells().length;
		mCurrentDrawingMode = DRAWING_MODE_VIEW;
	}

	@Override
	public void resize(final RectF rect) {
		android.util.Log.i("widget", "resize ...");
		super.resize(rect);
		if (mHost == null) {
			return;
		}

		if (mHost.getStage() != null) {
			containerRegion.set(rect);
			mSlotX = mInfo.cellX * (rect.width() / mInfo.spanX);
			mSlotY = mInfo.cellY * (rect.height() / mInfo.spanY);

			mLayoutParams = new FrameLayout.LayoutParams((int) rect.width(),
					(int) rect.height() + WIDGET_EXTRA_MODIFY);
			
			mLayoutParams.leftMargin = (int) mSlotX + (mHost != null ? mHost.getPageGap() / 2 : 0);
			mLayoutParams.topMargin = (int) mSlotY - WIDGET_EXTRA_MODIFY / 2;

			// remove and new add
			try {
//				mHost.getStage().removeView(mParasiteView);
				/*LauncherApplication app = (LauncherApplication) getXContext().getContext()
						.getApplicationContext();
				XLauncher l = app.getModel().getCallBack().getLauncherInstance();
				AppWidgetProviderInfo appWidgetInfo = AppWidgetManager.getInstance(l)
						.getAppWidgetInfo(((LauncherAppWidgetInfo) mInfo).appWidgetId);
				
//				 * RK_ID: RK_FOR_BUG_LELAUNCHER284 && 336. AUTH:
//				 * shenchao1@lenovo.com DATE:2013-09-24. S
				 
				ComponentName cn1 = null;
				if (((LauncherAppWidgetInfo) mInfo).intent == null) {
					if (appWidgetInfo != null) {
						cn1 = appWidgetInfo.provider;
					}
				} else {
					cn1 = ((LauncherAppWidgetInfo) mInfo).intent.getComponent();
				}
				boolean isBookMark = false;
				if (cn1 != null) {
					isBookMark = (cn1.getClassName().equals(
							"com.lenovo.browser.widget.BookmarkThumbnailWidgetProvider") || cn1
							.getClassName().equals(
									"com.android.browser.widget.BookmarkThumbnailWidgetProvider")) ? true
							: false;
				}

				if (((LauncherAppWidgetInfo) mInfo).itemType == LauncherSettings.Favorites.ITEM_TYPE_COMMEND_APPWIDGET) {
					final LauncherAppWidgetInfo app1 = (LauncherAppWidgetInfo) mInfo;
					mParasiteView = new FavoriteWidgetView(getXContext().getContext(), app1);
					mParasiteView.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							XLauncher launcher = (XLauncher) getXContext().getContext();
							launcher.onCommendViewClick(app1);
						}
					});
				} else if (((LauncherAppWidgetInfo) mInfo).needConfig == 1
						&& ((LauncherAppWidgetInfo) mInfo).itemType == LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET
						&& !isBookMark) {
					if (appWidgetInfo == null) {
						XLauncherModel.deleteItemFromDatabase(getXContext().getContext(), mInfo);
						return;
					}

					final LauncherAppWidgetInfo item = (LauncherAppWidgetInfo) mInfo;
					mParasiteView = new FavoriteWidgetView(getXContext().getContext(), item);
					mParasiteView.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {

							AppWidgetProviderInfo appWidget = AppWidgetManager.getInstance(
									getXContext().getContext()).getAppWidgetInfo(item.appWidgetId);
							PendingAddWidgetInfo info = new PendingAddWidgetInfo(appWidget, null,
									null);

							XLauncher launcher = (XLauncher) getXContext().getContext();
							launcher.addAppWidgetImpl(item.appWidgetId, info);
						}
					});
				} else {
					mParasiteView = l.getAppWidgetHost().createView(app,
							((LauncherAppWidgetInfo) mInfo).appWidgetId, appWidgetInfo);
				}
				
//				 * RK_ID: RK_FOR_BUG_LELAUNCHER284 && 336. AUTH:
//				 * shenchao1@lenovo.com DATE:2013-09-24. E
				 */
				if( !hasNavigationBar ){
					mLayoutParams.height = (int) (localRect.height() + WIDGET_EXTRA_MODIFY);
				}

//				mHost.getStage().addView(mParasiteView, mLayoutParams);

				mParasiteView.layout((int) rect.left, (int) rect.top, (int) rect.right,
						(int) rect.bottom);
				mParasiteView.requestLayout();
				mParasiteView.invalidate();
				onFragEndMove(mHostedItem, mHost.getCurrentPage());
				mParasiteView.setAlpha(PARASITE_VIEW_ALPHA);
				mViewCache = null;
			} catch (Exception e) {
			}
		}

		mNeedForceUpdateCache = true;
	}

	public void checkStage() {
	}

	private Bitmap mViewCache = null;

	public Rect getDrawRect() {
		return drawRect;
	}

	private static final long _CACHE_DELAY = 3 * 1000L;
	private boolean canMakeCache = true;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0) {
				canMakeCache = true;
			}
		};
	};

	private Bitmap buildViewCache() {

		if (mViewCache != null && !canMakeCache && !mNeedForceUpdateCache
				&& !mViewCache.isRecycled()) {
			return mViewCache;
		}

		try {
			if (mViewCache == null || mViewCache.isRecycled()) {
				mViewCache = Bitmap.createBitmap(mParasiteView.getWidth(),
						mParasiteView.getHeight(), Bitmap.Config.ARGB_8888);
			} else {
				mViewCache.eraseColor(0);
			}

			// float alpha = mParasiteView.getAlpha();
			// mParasiteView.setAlpha( PARASITE_VIEW_ALPHA );
			mParasiteView.buildDrawingCache();
			final Canvas c = new Canvas(mViewCache);
			mParasiteView.draw(c);
			// mParasiteView.setAlpha( alpha );

			// final Bitmap face = mParasiteView.getDrawingCache();
			// if( face == null ){
			// mViewCache = getReliableCache();
			// }else{
			// mViewCache = face;
			// }
			//
			android.util.Log.i("cache", "build cache ...");
			if (mViewCache != null) {
				canMakeCache = false;
				mHandler.sendEmptyMessageDelayed(0, _CACHE_DELAY);
			} else {
				mViewCache = mParasiteView.getDrawingCache();
			}

		} catch (OutOfMemoryError e) {
			canMakeCache = true;
		} catch (Exception e) {
			canMakeCache = true;
		}

		return mViewCache;
	}

	private Bitmap getReliableCache() {
		Assert.assertNotNull(mParasiteView);
		if (!(mParasiteView.getWidth() > 0 && mParasiteView.getHeight() > 0)) {
			return mViewCache;
		}
		Bitmap cacheReliable = null;
		try {/*
			 * cacheReliable = Bitmap.createBitmap(mParasiteView.getWidth(),
			 * mParasiteView.getHeight(), Config.ARGB_8888); Canvas canvas = new
			 * Canvas(cacheReliable); mParasiteView.draw(canvas);
			 */
		} catch (Error e) {
			// e.printStackTrace();
			canMakeCache = true;
		}
		return cacheReliable;
	}

	private int blockSize = 0;
	private XPagedViewItem mHostedItem;
	private int cellLen = 0;

	private boolean extraInternalHide = false;

	@Override
	public void onDraw(IDisplayProcess c) {

		if (mParasiteView != null) {
			if (!freezingDrawingMode) {
				if (enableForceDrawingModeCache) {

					mCurrentDrawingMode = DRAWING_MODE_CACHE;

				} else if (enableForceDrawingModeView || !mHost.isPageMoving) {

					mCurrentDrawingMode = DRAWING_MODE_VIEW;

				} else {

					mCurrentDrawingMode = mHost.isPageSlideMode() ? DRAWING_MODE_VIEW
							: DRAWING_MODE_CACHE;
					
					//lechang add for airbuse start
					// force page cache for low end devices
					//String s = android.os.Build.DEVICE;
    				if(android.os.Build.DEVICE.contains("airbuse")){
					    mCurrentDrawingMode = DRAWING_MODE_CACHE;
  					}
    				// lechang add for airbuse end
				}
			}

			if (extraInternalHide) {
				return;
			}

			if (mExtraEffectMatrix != null && !mExtraEffectMatrix.isIdentity()) {
				c.getCanvas().concat(mExtraEffectMatrix);
			}
			
//			android.util.Log.i( "tb", "Current Drawing Mode : " + mCurrentDrawingMode + " , " + mParasiteView);

			switch (mCurrentDrawingMode) {

			case DRAWING_MODE_CACHE:

				if (isAppWidgetHostView) {
					if (((LauncherAppWidgetHostView) mParasiteView).needCache()) {
						((LauncherAppWidgetHostView) mParasiteView).needCache(false);
						mNeedForceUpdateCache = true;
					}
				}

				if (/* mViewCacheDirty || */mNeedForceUpdateCache || mViewCache == null
						|| mViewCache.isRecycled()) {
					buildViewCache();
					mNeedForceUpdateCache = false;
				}

				if (mViewCache == null) {
					return;
				}

				c.drawBitmap(mViewCache, drawRect, targetRect, getPaint());
				break;

			case DRAWING_MODE_VIEW:

				if (blockSize == cellLen) {
					blockSize = 0;
				}

				blockSize ++;
				if (blockSize != 1) {
					return;
				}
				
				c.save();
				// c.clipRect(clipRectF);
				c.getCanvas().clipRect( localRect );
				
				//c.translate(mSlotX, mSlotY);
				mParasiteView.draw(c.getCanvas());
				c.restore();
				break;
			default:
				break;

			}

		}

	}

	@Override
	public void draw(IDisplayProcess c) {
		try {
			if (((XLauncher) getXContext().getContext()).getWorkspace().getOpenFolder() != null) {
				return;
			}
		} catch (Exception e) {
		}

		updateFinalAlpha();
		onDraw(c);
	}

	public static final int VISIBILITY_SHOW_ALL = 0;
	public static final int VISIBILITY_SHOW_NONE = VISIBILITY_SHOW_ALL + 1;
	public static final int VISIBILITY_SHOW_VIEW = VISIBILITY_SHOW_NONE + 1;
	public static final int VISIBILITY_SHOW_SHADOW = VISIBILITY_SHOW_VIEW + 1;
	public static final int VISIBILITY_SHOW_NONE_ALL = VISIBILITY_SHOW_SHADOW + 1;

	public static final int GONE = 4096;
	public static final int SHOW = 0;

	private int mVisibleTag = VISIBILITY_SHOW_NONE;

	public int getVisibleTag() {
		return mVisibleTag;
	}

	public void manageVisibility(int vtag, final Runnable callback) {
		this.manageVisibility(mHost.getStage(), mParasiteView, vtag, callback);
	}

	public void manageVisibility(final View vp, final View vc, int vtag, final Runnable callback) {
		
		
		
		// android.util.Log.i( "WOW", "vtag is : " + vtag );
		// R2.printStack( "WOW" );
		vc.setTranslationY(0);
		mVisibleTag = vtag;
		switch (vtag) {
		case VISIBILITY_SHOW_ALL:
			vp.post(new Runnable() {
				@Override
				public void run() {
					vc.setVisibility(View.VISIBLE);
					extraInternalHide = false;
					if (callback != null) {
						callback.run();
					}
					getXContext().getRenderer().invalidate();
				}
			});
			break;
		case VISIBILITY_SHOW_NONE:
		case VISIBILITY_SHOW_NONE_ALL:
			vp.post(new Runnable() {
				@Override
				public void run() {
					vc.setVisibility(View.INVISIBLE);
					vc.setAlpha(PARASITE_VIEW_ALPHA);
					extraInternalHide = true;
					if (callback != null) {
						callback.run();
					}
					getXContext().getRenderer().invalidate();
				}
			});
			break;
		// show
		case VISIBILITY_SHOW_VIEW:
			vp.post(new Runnable() {
				@Override
				public void run() {
					vc.setVisibility(View.VISIBLE);
					vc.setAlpha(PARASITE_VIEW_ALPHA);
					extraInternalHide = false;
					if (callback != null) {
						callback.run();
					}
					getXContext().getRenderer().invalidate();
				}
			});
			break;
		// hide
		case VISIBILITY_SHOW_SHADOW:
			vp.post(new Runnable() {
				@Override
				public void run() {
					vc.setVisibility(View.INVISIBLE);
					vc.setAlpha(PARASITE_VIEW_ALPHA);
					extraInternalHide = false;
					if (callback != null) {
						callback.run();
					}
					getXContext().getRenderer().invalidate();
				}
			});
			break;
		default:
			break;
		}
	}

	public void onFragBeginMove(XPagedViewItem itemMove, int currentPage) {

		if (!freezingDrawingMode) {
			enableForceDrawingModeView = false;
		}

		mCurrentScreen = currentPage;
		manageVisibility(VISIBILITY_SHOW_SHADOW, null);

		firstAttach = false;

		// touch
		getXContext().setSingleTouchIn(true);
	}

	@Override
	public void onFragScrolling(XPagedViewItem itemMoving, int from, int to) {
		if (!freezingDrawingMode) {
			enableForceDrawingModeCache = false;
		}
	}

	private boolean enableForceDrawingModeView = false;
	private boolean enableForceDrawingModeCache = false;
	private boolean freezingDrawingMode = false;

	public void freezingDrawingModeTo(byte forceDrawingMode) {
		freezingDrawingMode = true;
		mCurrentDrawingMode = forceDrawingMode;
	}

	public void unfreezingDrawingMode() {
		freezingDrawingMode = false;
	}

	@Override
	public void onFragEndMove(XPagedViewItem itemMoving, int currentPage) {

		if (!freezingDrawingMode) {
			enableForceDrawingModeCache = false;
		}

		mCurrentScreen = currentPage;
		// build cache
		// int checkpage1 = (checkpage1 = currentPage + 1) >=
		// mHost.getPageCount() ? 0 : checkpage1;
		// int checkpage_1 = (checkpage_1 = currentPage - 1) < 0 ?
		// mHost.getPageCount() - 1 : checkpage_1;
		if ((mCurrentDrawingMode == DRAWING_MODE_CACHE && mHost.needCacheViewContainer() && currentPage == mInfo.screen)
				|| (isAppWidgetHostView && ((LauncherAppWidgetHostView) mParasiteView).needCache() && currentPage == mInfo.screen)) {
			buildViewCache();
			if (isAppWidgetHostView)
				((LauncherAppWidgetHostView) mParasiteView).needCache(false);
		}

		// touch
		getXContext().setSingleTouchIn(false);
		if (mCurrentScreen == mScreen) {
			manageVisibility(VISIBILITY_SHOW_VIEW, null);
			if (getXContext().isStateOfLongPress()) {
				getXContext().bringContentViewToFront();
				return;
			}

			if (!freezingDrawingMode) {
				enableForceDrawingModeView = true;
			}
		} else {
			manageVisibility(VISIBILITY_SHOW_NONE, null);
		}
	}

	// test

	@Override
	public boolean onLongPress(MotionEvent e) {
		// android.util.Log.i("touch", "XViewContainer onLongPress.");
		if (getXContext().isGrabScrollState()) {
			return false;
		}

		if (mHost.isPageMoving) {
			return false;
		}

		manageVisibility(VISIBILITY_SHOW_NONE, null);

		// for long press
		mHost.getStage().setLongPressState(true);

		// bring content view to the front
		getXContext().bringContentViewToFront();

		return super.onLongPress(e);
	}

	public void manageVisibilityDirect(int vtag, final Runnable callback) {
		extraInternalHide = false;
		if (mParasiteView == null) {
			R5.echo("manageVisibilityDirect failure mParasiteView = null");
		}

		invalidate();
	}

	public View getParasiteView() {
		return mParasiteView;
	}

	@Override
	public boolean onFingerCancel(MotionEvent e) {

		// for long press
		mHost.getStage().setLongPressState(false);

		return super.onFingerCancel(e);
	}

	public Bitmap getSnapshot(float scale, boolean rebuildcache) {
		if (mViewCache != null && rebuildcache) {
			mViewCache.recycle();
		}
		mViewCache = buildViewCache();
		if (mViewCache != null) {
			return mViewCache.copy(mViewCache.getConfig(), true);
		} else {
			return null;
		}
	}

	@Override
	public Bitmap getSnapshot(float scale) {
		return getSnapshot(scale, false);
	}

	private boolean hasMoved = false;

	@Override
	public void onMovingTo(XPagedViewItem itemMoving, int screen, int cellX, int cellY) {
		hasMoved = true;
		mScreen = screen;
		manageVisibility(VISIBILITY_SHOW_SHADOW, null);
		mInfo.screen = screen;
		mInfo.cellX = cellX;
		mInfo.cellY = cellY;
		onSlot(mInfo, containerRegion, mHost, mHostedItem);
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		mHost.getStage().setLongPressState(false);
		return super.onSingleTapUp(e);
	}

	@Override
	public boolean onFingerUp(MotionEvent e) {
		// add for edit mode,begin
		// if((e.getAction() & MotionEvent.ACTION_MASK) !=
		// MotionEvent.ACTION_POINTER_UP)
		// {
		mHost.getStage().setLongPressState(false);
		// }
		// add for edit mode, end
		return super.onFingerUp(e);
	}

	@Override
	public void setVisibility(boolean visibility) {
		super.setVisibility(visibility);
		manageVisibility(VISIBILITY_SHOW_VIEW, null);
	}

	@Override
	public void clean() {
		super.clean();
		unregisterReceiver();
	}

	// add by zhanggx1 for memory.s
	private boolean registerExtraHandler = false;

	public void unregisterReceiver() {
		if (registerExtraHandler) {
			getXContext().getContext().unregisterReceiver(mExtraHandler);
		}
		registerExtraHandler = false;
	}

	// add by zhanggx1 for memory.e

	public View getParalistView() {
		return mParasiteView;
	}
}
