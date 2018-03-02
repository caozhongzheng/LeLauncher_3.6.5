package com.lenovo.launcher.components.XAllAppFace;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.slimengine.BaseDrawableGroup;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;

public class XSmartDock extends BaseDrawableGroup {

	XIconDrawable mBoard;
	ArrayList<DrawableItem> mIconList;
	XContext mContext;
	private String ACTION_SEARCH_ACTIVITY_NEW = "android.intent.action.SEARCH_ACTIVITY_NEW";

	public float mIconSize = 0;

	RectF mRegion;
	
	private LauncherApplication mApp;
	
	//private String ACTION_LEJINGPIN = "android.intent.action.start.lejingpin";
	public XSmartDock(XContext context, RectF rect) {
		super(context);

		mContext = context;

		mIconSize = mContext.getContext().getResources().getDimensionPixelSize(R.dimen.icon_style_app_icon_size);
		//mIconSize *= 1.2;
		mApp = (LauncherApplication)mContext.getContext().getApplicationContext();
	}

	private void init() {

		if (mIconList != null) {
			mIconList.clear();
		}

		clearAllItems();
        /* // cancel background bitmap by xingqx 2012.12.25 s 
		Bitmap boardFace = Utilities.getBitmapFromDrawable(mContext,
				R.drawable.x_applist_board);
		mBoard = new XIconDrawable(boardFace);
		mBoard.setIconBitmap(Bitmap.createScaledBitmap(boardFace,
				(int) mRegion.width(), (int) mRegion.height(), true));
		
		mBoard.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.x_applist_board_backgroud));

		addItem(mBoard);
        // cancel background bitmap by xingqx 2012.12.25 e
        */
		// center
		addQuickLauncherItemToRing(createRingView((int) (mRegion.width() / 2),
				(int) (mRegion.height() / 2), R.drawable.x_applist_2_home,
				new Runnable() {
					@Override
					public void run() {
						if (mContext.getContext() instanceof XLauncher) {
							((XLauncher) mContext.getContext()).getXLauncherView().post(
									new Runnable() {
										@Override
										public void run() {
											((XLauncher) mContext.getContext())
													.showWorkspace(true);
										}
									});
						}
					}
				}));

		// hide the icon and move the item to Menu
		// the left icon was changed to search
		addQuickLauncherItemToRing(createRingView((int) (mRegion.width() / 5),
				(int) (mRegion.height() / 2), R.drawable.x_applist_search, /*R.drawable.x_applist_appmgr,*/
				new Runnable() {
					@Override
					public void run() {
						/*Intent intent = new Intent(SettingsValue.ACTION_LETHEME_LAUNCH);
	                    intent.putExtra("EXTRA",0);	                    
						
						
						if (mContext.getContext() instanceof XLauncher) {							
							((XLauncher) mContext.getContext())
									.startActivitySafely(intent, null);
						} */
						Intent intent = new Intent(ACTION_SEARCH_ACTIVITY_NEW);

						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						
						if (mContext.getContext() instanceof XLauncher) {
							((XLauncher) mContext.getContext())
							        .startActivitySafely(intent, null);
						}
						
					}
				}));
        
		// right  change by xingqx 2012.12.25 old is 3/4 ==> 4/5
		//the icon action is to show menu
		addQuickLauncherItemToRing(createRingView(
				(int) (mRegion.width() * 4 / 5), (int) (mRegion.height() / 2),
				R.drawable.x_applist_menu/*R.drawable.x_applist_search*/, new Runnable() {
					@Override
					public void run() {
				        /*Intent intent = new Intent(ACTION_SEARCH_ACTIVITY_NEW);

						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						
						if (mContext.getContext() instanceof XLauncher) {
							((XLauncher) mContext.getContext())
							        .startActivitySafely(intent, null);
						}*/
//						Intent intent = new Intent("com.lenovo.launcher.action.applist_menu");
						if (mContext.getContext() instanceof XLauncher) {
							((XLauncher) mContext.getContext()).showMenu();
//							.showWorkspace(true);
//							((XLauncher) mContext.getContext())
//							        .startActivityForResult(intent, XLauncher.REQUEST_CODE_SHOW_AMENUDILAOG);	
						}
			    		 
						
					}
				}));
	}

	public void addQuickLauncherItemToRing(DrawableItem itemToAdd) {
		if (mIconList == null) {
			mIconList = new ArrayList<DrawableItem>();
		}
		mIconList.add(itemToAdd);

		addItem(itemToAdd);
	}

	public void resize(RectF rect) {
		super.resize(rect);

		mRegion = new RectF(rect);

		init();
	}

	@Override
	public void draw(IDisplayProcess c) {
		super.draw(c);
	}

	public DrawableItem createRingView(int anchorX, int anchorY,
			int drawableId, final Runnable actionOnClick) {
			Drawable iconBg = mContext.getResources().getDrawable(drawableId);
//		Drawable iconBg = mApp.mLauncherContext.getDrawable(drawableId);
		DrawableItem icon = new XIconDrawable(mContext, null);
		icon.resize(new RectF(0, 0, iconBg.getIntrinsicWidth(), iconBg.getIntrinsicHeight()));
		Rect r = new Rect();
		icon.localRect.round(r);
		iconBg.setBounds( r );

		icon.setBackgroundDrawable(iconBg);

		// test value
		icon.setRelativeX(anchorX - icon.localRect.width() / 2);
		icon.setRelativeY(anchorY - icon.localRect.height() / 2);

		icon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(DrawableItem item) {
			    /*** RK_ID: fixbug.  AUT: zhaoxy . DATE: 2013-02-18 . START***/
                if (mContext.getContext() instanceof XLauncher) {
                    ((XLauncher) mContext.getContext()).stopEditMode();
                }
			    /*** RK_ID: fixbug.  AUT: zhaoxy . DATE: 2013-02-18 . END***/
				actionOnClick.run();
			}
		});

//		icon.enableCache();
		
		IconInfo info = new IconInfo(drawableId, anchorX, anchorY);
		icon.setTag(info);

		return icon;
	}

    /* RK_ID: RK_TOUCHEVENT. AUT: liuli1 . DATE: 2012-12-25 . START */
    public void setBackgroundVisible(boolean b) {
        if (mBoard != null) {
            mBoard.setVisibility(b);
        }
    }
    /* RK_ID: RK_TOUCHEVENT. AUT: liuli1 . DATE: 2012-12-25 . END */
    
    private class IconInfo {
    	int drawableId;
    	int anchorX;
    	int anchorY;
    	
    	private IconInfo(int drawableId, int anchorX, int anchorY) {
    		this.drawableId = drawableId;
    		this.anchorX = anchorX;
    		this.anchorY = anchorY;
    	}
    }
    public void updateTheme() {
    	if (mIconList == null) {
    		return;
    	}
    	for (DrawableItem icon : mIconList) {
    		if (icon.getTag() != null && icon.getTag() instanceof IconInfo) {
    			icon.disableCache();
    			
    			IconInfo info = (IconInfo)icon.getTag();
    			Drawable iconBg = mApp.mLauncherContext.getDrawable(info.drawableId);
    			icon.resize(new RectF(0, 0, iconBg.getIntrinsicWidth(), iconBg.getIntrinsicHeight()));
    			Rect r = new Rect();
    			icon.localRect.round(r);
    			iconBg.setBounds( r );

    			icon.setBackgroundDrawable(iconBg);
    			
    			icon.setRelativeX(info.anchorX - icon.localRect.width() / 2);
    			icon.setRelativeY(info.anchorY - icon.localRect.width() / 2);
    			
//    			icon.invalidate();
//    			icon.enableCache();
    		}
    	}
    }
}
