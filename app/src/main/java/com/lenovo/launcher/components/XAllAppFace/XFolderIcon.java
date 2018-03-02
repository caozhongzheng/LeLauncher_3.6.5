package com.lenovo.launcher.components.XAllAppFace;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.XDropTarget.XDragObject;
import com.lenovo.launcher.components.XAllAppFace.slimengine.BaseDrawableGroup;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;
import com.lenovo.launcher2.commoninterface.ApplicationInfo;
import com.lenovo.launcher2.commoninterface.FolderInfo;
import com.lenovo.launcher2.commoninterface.FolderInfo.FolderListener;
import com.lenovo.launcher2.commoninterface.ItemInfo;
import com.lenovo.launcher2.commoninterface.LauncherSettings;
import com.lenovo.launcher2.commoninterface.ShortcutInfo;
import com.lenovo.launcher2.customizer.Debug.R5;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.ShadowUtilites;
import com.lenovo.launcher2.customizer.TipsUtilities;
import com.lenovo.launcher2.customizer.TipsUtilities.TipPoint;
import com.lenovo.launcher2.customizer.Utilities;

public class XFolderIcon extends BaseDrawableGroup implements FolderListener {
    
    private static final String TAG = "XFolderIcon";
    public XFolder mFolder;
    public FolderInfo mInfo;
    
    // The number of icons to display in the
    /*RK_ID: RK_FOLDER . AUT: zhanggx1 . DATE: 2011-12-13 . PUR: for leos folder . S*/
    protected static final int NUM_ITEMS_IN_PREVIEW = 9;///old: 4///////old: 3
    /*RK_ID: RK_FOLDER . AUT: zhanggx1 . DATE: 2011-12-13 . PUR: for leos folder . E*/
    private DrawableItem mBlackshadow;
    private DrawableItem mPreviewBackground;
    private XTextView mFolderName;
    private int padding;
    XContext mXContext;
    private TipPoint mTipPoint =null;
    private static Bitmap mBackgroundIconBg;
    private static XLauncher mlauncher;
    private static float mOneNumberTipDrawableWidth = 0;
	private static Drawable s = null;
    private int FOLDER_CELL_Y_OFFSET = 0;
    private int FOLDER_CELL_X_OFFSET = 0;
    private float folder_scale = 1;
    private static int app_icon_text_width = 0;

    public XFolderIcon(XContext context) {        
        super(context);
        mXContext = context;
        init(context);        
    }

    private void init(XContext context) {
        Log.d("tip", "mOneNumberTipDrawableWidth  = " + mOneNumberTipDrawableWidth);
        if(mOneNumberTipDrawableWidth == 0){
            Log.d("tip", "mOneNumberTipDrawableWidth  = " + 0);
            mOneNumberTipDrawableWidth = getOneNumerTipDrawableWidth(context);
        }
        final Resources resources = context.getResources();
        padding = (int) (resources.getDisplayMetrics().density * 1);
        FOLDER_CELL_Y_OFFSET = resources.getDimensionPixelSize(R.dimen.folder_cell_y_offset);
        FOLDER_CELL_X_OFFSET = resources.getDimensionPixelSize(R.dimen.folder_cell_x_offset);
        folder_scale = com.lenovo.launcher.components.XAllAppFace.utilities.Utilities.formatInt2Float(resources.getDimensionPixelSize(R.dimen.folder_scale));
        app_icon_text_width = resources.getDimensionPixelSize(R.dimen.app_icon_text_width);
    }
    
    public static Drawable getBackgroundBitmap(XContext context, FolderInfo folderInfo)
    {
        if (folderInfo != null && folderInfo.mReplaceIcon != null)
        {
            return new BitmapDrawable(context.getResources(), folderInfo.mReplaceIcon);
        }
        
//        if (mBackgroundIconBg == null)
//        {
            LauncherApplication app = (LauncherApplication) context.getContext().getApplicationContext();
            Drawable background = app.mLauncherContext.getDrawable(R.drawable.portal_ring_inner_holo);  
            
//            mBackgroundIconBg = Utilities.drawableToBitmap(background);
//        }
        
        return background;
    }
    
    public static void clearBackgroundBitmap()
    {        
        if (mBackgroundIconBg != null)
        {
//            mBackgroundIconBg.recycle();
            mBackgroundIconBg = null;
        }
        
        return;
    }

    public static XFolderIcon obtain(XContext context, XLauncher launcher, FolderInfo folderInfo) {
        if (folderInfo == null) {
            throw new java.lang.NullPointerException("folderInfo can not be null!");
        }
        mlauncher = launcher;
        final XFolderIcon icon = new XFolderIcon(context);
        folderInfo.clearListener();
        icon.setTag(folderInfo);
        icon.mInfo = folderInfo;
        
        final Resources res = context.getResources();
                 
        icon.mPaddingLeft = icon.mPaddingRight = res.getDimensionPixelOffset(R.dimen.app_text_l_r_padding);
//        Bitmap bg = getBackgroundBitmap(context, folderInfo);
        final LauncherApplication la = (LauncherApplication) context.getContext().getApplicationContext();
        Drawable bg = la.mLauncherContext.getDrawable(R.drawable.portal_ring_inner_holo);
//        icon.mPreviewBackground = new XIconDrawable(context, bg);
        icon.mPreviewBackground = new DrawableItem(context) {
            @Override
            public void updateFinalAlpha() {
                icon.mPreviewBackground.setBgAlphaDirty();
                super.updateFinalAlpha();
            }
        };
        icon.mPreviewBackground.setBackgroundDrawable(bg);
        icon.addItem(icon.mPreviewBackground);
        /*PK_ID:Folde name do not change when language changed AUTH:GECN1 S*/
        if(folderInfo.title.equals("")){
            folderInfo.title = res.getString(R.string.folder_name);
            XLauncherModel.updateItemInDatabase(context.getContext(), folderInfo);
        }
        /*PK_ID:Folde name do not change when language changed AUTH:GECN1 E*/
//        icon.mFolderName = new XTextView(context, folderInfo.title.toString(), new RectF(0, 0, icon.localRect.width() - icon.mPaddingLeft - icon.mPaddingRight, 14));
        //TODO
        icon.mFolderName = new XTextView(context, folderInfo.title.toString(), new RectF(0, 0, app_icon_text_width, 14));
        icon.mFolderName.setBackgroundEnable(SettingsValue.isDesktopTextBackgroundEnabled(context.getContext()));
        icon.mFolderName.enableCache();
        icon.addItem(icon.mFolderName);
        
        Drawable bs = new BitmapDrawable(ShadowUtilites.shadows[ShadowUtilites.FOLDER_ICON]);
        icon.mBlackshadow = new DrawableItem(context);
        if(bs!=null && icon.mInfo.container != LauncherSettings.Favorites.CONTAINER_HOTSEAT){
          icon.mBlackshadow.setBackgroundDrawable(bs);
          icon.addItem(icon.mBlackshadow);
        }
        //为了阴影不被文字两边的渐变白色蒙板盖上
        icon.bringChildToFront(icon.mPreviewBackground);
        
        icon.resize(icon.localRect);
        
        XFolder folder = new XFolder(context,launcher);
        folder.setDragController(((XLauncherView) context).getDragController());
        folder.setFolderIcon(icon);
        folder.bind(folderInfo);
        folder.setStateLinstener(launcher);
        icon.mFolder = folder;
        icon.setOnClickListener(launcher);
        icon.enableCache();
        
        folderInfo.addListener(icon);
        /*PK_ID:SHOW_TIPS AUTH :GECN1 DATE:2012-04-18 S*/
        int showNums = initNewAppsNumberTip(folderInfo,context.getContext());
        if(showNums>0){
            icon.showTipForNewAdded(showNums);
        }
        /*PK_ID:SHOW_TIPS AUTH :GECN1 DATE:2012-04-18 E*/
        return icon;
    }

	@Override
    public void resize(RectF rect) {
        super.resize(rect);
        boolean inHotseat = mInfo.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT;
        
        final Resources res = mXContext.getContext().getResources();
        LauncherApplication app = (LauncherApplication) mXContext.getContext().getApplicationContext();
        
        int paddingTop = 0;
        /***RK_ID:RK_CHANGE_ICON_SIZE AUT:zhanglz1@lenovo.com. DATE:2013-01-14.S***/
        int smallIconSize = SettingsValue.getHotSeatIconSizeValue(app);
        int originalAppSize = SettingsValue.getIconSizeValue(mXContext.getContext());
        /***RK_ID:RK_CHANGE_ICON_SIZE AUT:zhanglz1@lenovo.com. DATE:2013-01-14.E***/       
        int iconSize = inHotseat ? smallIconSize : originalAppSize;
        int iconPaddingTop = res.getDimensionPixelOffset(R.dimen.app_icon_padding_top);        
        int iconDrawablePadding = res.getDimensionPixelOffset(R.dimen.app_icon_drawable_padding);
        
        int drawableSize = res.getDimensionPixelSize(R.dimen.folder_preview_size);
        /***RK_ID:RK_CHANGE_ICON_SIZE AUT:zhanglz1@lenovo.com. DATE:2013-01-14.S***/       
        DisplayMetrics dm =app.getApplicationContext().getResources().getDisplayMetrics();  
        float mDeviceDensity = dm.density;
        int temp = Math.round(SettingsValue.getIconSizeValue(app)/mDeviceDensity);
        String[] keys = app.getResources().getStringArray(R.array.pref_icon_size_values);
        //normal
        if (temp == Integer.parseInt(keys[0])) {
           //keep still
        } //small
        else if (temp == Integer.parseInt(keys[1])) {
            drawableSize = drawableSize * Integer.parseInt(keys[1]) / Integer.parseInt(keys[0]);
        }
        /***RK_ID:RK_CHANGE_ICON_SIZE AUT:zhanglz1@lenovo.com. DATE:2013-01-14.E***/       
        drawableSize = inHotseat ? smallIconSize : drawableSize;
        
        mPaddingLeft = mPaddingRight = res.getDimensionPixelOffset(R.dimen.app_text_l_r_padding);
//      mFolderName.resize(new RectF(0, mFolderName.getRelativeY(), getWidth() - mPaddingLeft - mPaddingRight, mFolderName.localRect.bottom));

        final float density = mContext.getContext().getResources().getDisplayMetrics().density;
        mFolderName.setTextSize(Integer.valueOf(SettingsValue.getIconTextSizeValue(mContext.getContext())) * density);
        mFolderName.resize(new RectF(0, 0, app_icon_text_width, mFolderName.getHeight()));
      
      
        mFolderName.setRelativeX((int) (localRect.width() / 2 - mFolderName.localRect.width() / 2));
        
        //add by zhanggx1 on 2013-07-03.s
        if (SettingsValue.getCurrentMachineType(mXContext.getContext()) == -1) {
      	    iconPaddingTop = (int)(this.localRect.height() / 2 - drawableSize / 2);
        }
        //add by zhanggx1 on 2013-07-03.e
        
        iconPaddingTop = (int)Math.round(iconPaddingTop + (originalAppSize - iconSize) / 2.0f);
        
        if (drawableSize > iconSize) {
            paddingTop = (int)Math.round(iconPaddingTop - (drawableSize - iconSize) / 2.0f);
        } else {
            paddingTop = (int)Math.round(iconPaddingTop + (iconSize - drawableSize) / 2.0f);
        }
        
        int folderIconDrawablePadding = iconPaddingTop + iconSize + iconDrawablePadding - paddingTop - drawableSize;
        float left = localRect.width() / 2 - mPreviewBackground.localRect.width() / 2;
        
        mPreviewBackground.resize(new RectF(left, 0, left + drawableSize, drawableSize));
        mPreviewBackground.setRelativeX((int) (localRect.width() / 2 - mPreviewBackground.localRect.width() / 2));
        R5.echo("XFolderIcon paddingTop = " + paddingTop);
        if (inHotseat) {
        	paddingTop = mContext.getResources().getDimensionPixelOffset(
                    R.dimen.app_icon_hotseat_padding_top);
        } else {
        	if (paddingTop + drawableSize + folderIconDrawablePadding + mFolderName.localRect.height() > localRect.height()) {
    			paddingTop = (int) (localRect.height() - mFolderName.localRect.height()
    					- folderIconDrawablePadding - drawableSize);
    		}
        }
        mPreviewBackground.setRelativeY(paddingTop);
		Bitmap shadow = ShadowUtilites.shadows[ShadowUtilites.FOLDER_ICON];
		if(shadow !=null && !inHotseat){
        float shadowLeft = mPreviewBackground.localRect.centerX()
				- (shadow.getWidth() >> 1);
		float shadowTop = mPreviewBackground.localRect.centerY()
				- (shadow.getHeight() >> 1);
        int shadowSize = shadow.getHeight(); 
        mBlackshadow.resize(new RectF(0, 0, shadowSize,shadowSize));
        mBlackshadow.setRelativeX(shadowLeft/*localRect.width() / 2 - mBlackshadow.localRect.width() / 2*/);
        mBlackshadow.setRelativeY(inHotseat ? 0 : /*paddingTop*/shadowTop); 
		}
		
        
		mFolderName.setRelativeY(mPreviewBackground.localRect.bottom + folderIconDrawablePadding);

        // update icon matrix follow iconsize
        int iconsize = SettingsValue.getIconSizeValueNew(mXContext.getContext());
        float px = mPreviewBackground.localRect.centerX();
        float py = mPreviewBackground.localRect.centerY();
        final float scale = (float) (iconsize / mPreviewBackground.getHeight());
        Matrix m = mPreviewBackground.getMatrix();
        m.reset();
        m.postScale(scale, scale, px, py);
        mPreviewBackground.updateMatrix();
        
//        if (mFolderName.localRect.bottom >  rect.bottom)
//        {
//        	rect.bottom = mFolderName.localRect.bottom;
//        	super.resize(rect);
//        }
        
        initTipDrawablePosition();
        if (mFolder != null && !mFolder.isAnimating()) {
            mFolder.updatePositionAndSizeAsIcon();
        }        
    }

    public float contentScale = 1;
    
    protected boolean neetInvalidateDelay = false;
    public RectF getGlobalRect()
    {
    	int width = mlauncher.getResources().getDisplayMetrics().widthPixels;
    	return new RectF(width+this.getGlobalX2(),this.getGlobalY2(),width+this.getGlobalX2()+this.getWidth(),this.getGlobalY2()+this.getHeight());
    }
    @Override
    public void invalidate() {
        if (mFolder.isAnimating()) {
            neetInvalidateDelay = true;
            return;
        }        
                
        super.invalidate();
        if (mInfo.container == LauncherSettings.Favorites.CONTAINER_DESKTOP)
        {
        	((XLauncherView)mXContext).getWorkspace().getPagedView().refreshBitmapCacheCurrent();
        }
    }

    @Override
    public void onDraw(IDisplayProcess c) {
		if (mBlackshadow != null && mBlackshadow.isVisible()) {
			Bitmap shadow = ShadowUtilites.shadows[ShadowUtilites.FOLDER_ICON];
			if (mInfo.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
				mBlackshadow.setVisibility(false);
			} else if (shadow != null) {
				mBlackshadow.setVisibility(true);
			}
		}
		 if (mFolder != null && enableShadow && mInfo.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
				if (mShadowBm != null && !mShadowBm.isRecycled() && mShadowPoint != null) {
					c.drawBitmap(mShadowBm, mShadowPoint.x, mShadowPoint.y, getPaint());
				}
			}
        super.onDraw(c);
        
        if (mFolder == null) return;
       
        if (mInfo == null || mInfo.opened ||mFolder.isAnimating()) return;
        final XPagedView mContent = mFolder.getPagedView();
        if(mContent == null) {
        	return;
        }
        
//        float w = mPreviewBackground.getWidth();
//        final float contentPadding = 8;
//        final float scale_w = (w - contentPadding - contentPadding) * 1.3f / mFolder.getMaxCountX() / mContent.getCellWidth();
//        final float scale_h = (w - contentPadding - contentPadding) * 1.3f / mFolder.getMaxCountX() / mContent.getCellHeight();
//        contentScale = scale_w < scale_h ? scale_w : scale_h;
        
        /*** RK_ID: ANIM_FOLDER. AUT: zhaoxy . DATE: 2013-06-23 . START ***/
        mFolder.updatePositionAndSizeAsIcon();
        mFolder.updateFolderEffect(false);
        
        Matrix m = new Matrix();
        c.save();
        m.setScale(folder_scale, folder_scale);
        m.preTranslate(-mContent.getWidth() / 2 + FOLDER_CELL_X_OFFSET, -mContent.getHeight() / 2 + FOLDER_CELL_Y_OFFSET);
        m.postTranslate(mPreviewBackground.localRect.centerX(), mPreviewBackground.localRect.centerY());
        c.concat(m);
        c.translate(-mContent.getRelativeX(), -mContent.getRelativeY());
        mContent.getMatrix().invert(m);
        c.concat(m);
        mContent.draw(c);
        c.restore();
        /*** RK_ID: ANIM_FOLDER. AUT: zhaoxy . DATE: 2013-06-23 . END ***/
        /*** RK_ID: APPS_CATEGORY. AUT: zhaoxy . DATE: 2013-03-23 . START ***/
        if (enableTip && mInfo.container != LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
            if(mTipPoint == null){
                initTipDrawablePosition();
            }
            if (mTipBg != null && !mTipBg.isRecycled() && mTipPoint != null ) {
                getPaint().setAlpha(255);
                c.drawBitmap(mTipBg, mTipPoint.x , mTipPoint.y, getPaint());
            }
        }
        /*** RK_ID: APPS_CATEGORY. AUT: zhaoxy . DATE: 2013-03-23 . END ***/
		
        if (mBlackshadow != null)
			mBlackshadow.setVisibility(true);
        
    }
    
    @Override
    public String toString() {
        StringBuffer out = new StringBuffer("XFolderIcon = {\n");
        out.append("    icon: x = ").append(mPreviewBackground.getRelativeX());
        out.append(" y = ").append(mPreviewBackground.getRelativeY());
        out.append(" w = ").append(mPreviewBackground.getWidth());
        out.append(" h = ").append(mPreviewBackground.getHeight());
        out.append("\n    title: x = ").append(mFolderName.getRelativeX());
        out.append(" y = ").append(mFolderName.getRelativeY());
        out.append(" w = ").append(mFolderName.getWidth());
        out.append(" h = ").append(mFolderName.getHeight()).append("\n}");
        return out.toString();
    }
    
    public void changeFolderIconThemes() {        
    	LauncherApplication app = (LauncherApplication) getXContext().getContext().getApplicationContext();
    	   	      
    	Drawable bg = getBackgroundBitmap(getXContext(), mInfo);
        Log.i(TAG, "changeFolderIconThemes ~~~  bg = " + bg);
        if (bg != null) {
            mPreviewBackground.setBackgroundDrawable(bg);
        }
        Drawable bs = new BitmapDrawable(ShadowUtilites.shadows[ShadowUtilites.FOLDER_ICON]);
        if(bs!=null && mInfo.container != LauncherSettings.Favorites.CONTAINER_HOTSEAT){
          mBlackshadow.setBackgroundDrawable(bs);
        }
    	mFolderName.setTextColor(getXContext().getContext());
    	mFolderName.setBackgroundEnable(SettingsValue.isDesktopTextBackgroundEnabled(app));
    	
    	FolderRingAnimator.updateForTheme(getXContext());

    	resize(localRect);
    }

    /* RK_ID: RK_QUICKACTION . AUT: liuli1 . DATE: 2012-04-13 . START */
    public void updateFolderPreviewBackground() {
        boolean inHotseat = mInfo == null ? true
                : mInfo.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT;

        setPreviewBG(getXContext(), inHotseat, mInfo);
        invalidate();

    }

    public int getPreviewLayoutParamWidth() {
        return (int) mPreviewBackground.localRect.width();
    }

    public int getPreviewLayoutParamHeight() {
        return (int) mPreviewBackground.localRect.height();
    }

    @Override
    public void resetPressedState() {
        if (mPreviewBackground != null) {
            mPreviewBackground.resetPressedState();
        }
        setAlpha(1.0f);
        super.resetPressedState();
    }

    void updateInfoContanier(FolderInfo folderInfo) {
        XContext context = getXContext();
        boolean inHotseat = folderInfo.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT;

        setPreviewBG(context, inHotseat, folderInfo);

        if (!inHotseat) {
            mFolderName.setVisibility(true);
        }
        invalidate();
    }

    private void setPreviewBG(XContext context, boolean inHotseat, FolderInfo folderInfo) {        
        Drawable bg = getBackgroundBitmap(context, folderInfo);

        Log.i(TAG, "setPreviewBG~~~~bg ==" + bg);
        if (bg != null) {
            mPreviewBackground.setBackgroundDrawable(bg);
            resize(localRect);
        }
    }

    /* RK_ID: RK_QUICKACTION . AUT: liuli1 . DATE: 2012-04-13 . END */

    @Override
    public void onAdd(ShortcutInfo item) {
    }

    @Override
    public void onRemove(ShortcutInfo item) {
    }

    @Override
    public void onTitleChanged(CharSequence title) {
        mFolderName.setText(title.equals("") ? getXContext().getResources().getString(R.string.folder_name) : title.toString());
        XLauncherModel.updateItemInDatabase(getXContext().getContext(), mInfo);
        invalidate();
    }

    @Override
    public void onItemsChanged() {
    }

    public boolean acceptDrop(ItemInfo item) {
        return willAcceptItem(item);
    }

    private boolean willAcceptItem(ItemInfo item) {
        final int itemType = item.itemType;
//        R5.echo("willAcceptItem itemType = " + itemType + "mFolder.isFull() " + mFolder.isFull()
//                + "item = " + item + "mInfo = " + mInfo + "mInfo.opened = " + mInfo.opened + "mInfo.contains(item) = " + mInfo.contains(item));
        return ((itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION || itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT
                || itemType == LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT)
                && !mFolder.isFull() && item != mInfo && !mInfo.contains(item));
    }

    public void onDrop(ItemInfo info) {
        ShortcutInfo item = new ShortcutInfo((ShortcutInfo) info);
        item.container = mInfo.id;
        onDrop(item, null, null, 1.0f, mInfo.contents.size(), null, null);
    }

    public void onDrop(XDragObject d) {
        ShortcutInfo item;
        if (d.dragInfo instanceof ApplicationInfo) {
            // Came from all apps -- make a copy
            item = ((ApplicationInfo) d.dragInfo).makeShortcut();
        } else {
            item = new ShortcutInfo((ShortcutInfo) d.dragInfo);
        }
        item.container = mInfo.id;
//        mFolder.notifyDrop();
        onDrop(item, d.dragView, null, 1.0f, mInfo.contents.size(), null/* d.postAnimationRunnable */, d.dragSource);
    }

    private void onDrop(final ShortcutInfo item, final XDragView dragView, Rect finalRect,
            float scaleRelativeToDragLayer, int index, Runnable postAnimationRunnable, XDragSource source) {
        item.screen = -1;
        item.cellX = -1;
        item.cellY = -1;

        // Typically, the animateView corresponds to the DragView; however, if this is being done
        // after a configuration activity (ie. for a Shortcut being dragged from AllApps) we
        // will not have a view to animate
        if (dragView != null) {
//            DragLayer dragLayer = mLauncher.getDragLayer();
//            Rect from = new Rect();
//            dragLayer.getViewRectRelativeToSelf(animateView, from);
//            Rect to = finalRect;
//            if (to == null) {
//                to = new Rect();
//                Workspace workspace = mLauncher.getWorkspace();
//                // Set cellLayout and this to it's final state to compute final animation locations
//                workspace.setFinalTransitionTransform((CellLayout) getParent().getParent());
//                float scaleX = getScaleX();
//                float scaleY = getScaleY();
//                setScaleX(1.0f);
//                setScaleY(1.0f);
//                scaleRelativeToDragLayer = dragLayer.getDescendantRectRelativeToSelf(this, to);
//                // Finished computing final animation locations, restore current state
//                setScaleX(scaleX);
//                setScaleY(scaleY);
//                workspace.resetTransitionTransform((CellLayout) getParent().getParent());
//            }
//
//            int[] center = new int[2];
//            float scale = getLocalCenterForIndex(index, center);
//            center[0] = (int) Math.round(scaleRelativeToDragLayer * center[0]);
//            center[1] = (int) Math.round(scaleRelativeToDragLayer * center[1]);
//
//            to.offset(center[0] - animateView.getMeasuredWidth() / 2,
//                    center[1] - animateView.getMeasuredHeight() / 2);
//
//            float finalAlpha = index < NUM_ITEMS_IN_PREVIEW ? 0.5f : 0f;
//
//            dragLayer.animateView(animateView, from, to, finalAlpha,
//                    scale * scaleRelativeToDragLayer, DROP_IN_ANIMATION_DURATION,
//                    new DecelerateInterpolator(2), new AccelerateInterpolator(2),
//                    postAnimationRunnable, false);
//            postDelayed(new Runnable() {
//                public void run() {
//                    addItem(item);
//                }
//            }, DROP_IN_ANIMATION_DURATION);

            addItem(item);
            invalidate();

            long container = mInfo.id;
            if (source instanceof XWorkspace) {
                container = LauncherSettings.Favorites.CONTAINER_DESKTOP;
            } else if (source instanceof XHotseat) {
                container = LauncherSettings.Favorites.CONTAINER_HOTSEAT;
            }
            mFolder.animDropIntoPosition(dragView, item, container);
        } else {
            addItem(item);
        }
    }

    public boolean addItem(ShortcutInfo item) {
        if (!mInfo.contains(item)) {
            mInfo.add(item);
            return true;
//        LauncherModel.addOrMoveItemInDatabase(mLauncher, item, mInfo.id, 0, item.cellX, item.cellY);
        }
        return false;
    }
    
    public void setTextVisible(boolean visible) {
        mFolderName.setVisibility(visible);
    }

    /*** RK_ID: APPS_CATEGORY. AUT: zhaoxy . DATE: 2013-03-23 . START ***/
    private boolean enableTip = false;
    private Bitmap mTipBg = null;

    public void dismissTip() {
        deleteNumbersTipFromPreference(mInfo.id,getXContext().getContext());
        enableTip = false;
        if (mTipBg != null && !mTipBg.isRecycled()) {
            mTipBg.recycle();
            mTipBg = null;
        }
        mTipPoint = null;
        invalidate();
    }

    private void showTipForNewAdded(int num) {
        if (num > 0) {
            
            if (mTipBg != null && !mTipBg.isRecycled()) {
                mTipBg.recycle();
            }
            mTipBg = TipsUtilities.getTipDrawable(num, getXContext());
            enableTip = true;
        }
    }
    
    private void initTipDrawablePosition(){
        if(mTipBg == null){
            return;
        }
        float width = mTipBg.getWidth();
        float height = mTipBg.getHeight();
        float parentRightX = this.getWidth();
        float childTopY = mPreviewBackground.getRelativeY();
        float childRight = mPreviewBackground.getRelativeX() + mPreviewBackground.getWidth();
        mTipPoint =TipsUtilities.getTipDrawableRelativeParentPosition(parentRightX, childRight, childTopY, width, height, getXContext());
        
    }
    /*** RK_ID: APPS_CATEGORY. AUT: zhaoxy . DATE: 2013-03-23 . END ***/
    /*PK_ID:SHOW_TIPS AUTH :GECN1 DATE:2012-04-18 S*/
    private static int initNewAppsNumberTip(FolderInfo info,Context c){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        return  sharedPreferences.getInt(String.valueOf(info.id), 0);
        
        
    }
    
    public static void setNewAppsNumberTip(Long folderID,Context c,int newNumbers){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        int olderNum = sharedPreferences.getInt(String.valueOf(folderID), 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(String.valueOf(folderID), newNumbers + olderNum);
        editor.commit();
    }
    
    public static void deleteNumbersTipFromPreference(Long folderID,Context c){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(String.valueOf(folderID));
        editor.commit();
    }
    
    public void ShowNewAddedAppsNumber(int newNumbers){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getXContext().getContext());
        int olderNumber = sharedPreferences.getInt(String.valueOf(mInfo.id), 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(String.valueOf(mInfo.id), newNumbers + olderNumber);
        editor.commit();
        showTipForNewAdded(newNumbers + olderNumber);
    }
    
    private  float getOneNumerTipDrawableWidth(final XContext context){
        final Resources res = context.getResources();
        int textSize =res.getDimensionPixelSize(R.dimen.foldericon_addappsnums_tipsize );
        Paint p = new Paint();
        p.setColor(Color.WHITE);
        p.setTextSize(textSize);
        p.setAntiAlias(true);
        p.setTextAlign(Align.CENTER);
        FontMetrics fm = new FontMetrics();
        p.getFontMetrics(fm);
        float width =  p.measureText("1");
        p = null;
        return width;
    }
    /*PK_ID:SHOW_TIPS AUTH :GECN1 DATE:2012-04-18 E*/
    public DrawableItem getIconDrawable() {
	    return mPreviewBackground;
	}

    public DrawableItem getTextDrawable() {
        return mFolderName;
    }

    private static final int CONSUMPTION_ANIMATION_DURATION = 100;
    // The degree to which the outer ring is scaled in its natural state
    private static final float OUTER_RING_GROWTH_FACTOR = 0.3f;

    // The degree to which the inner ring grows when accepting drop
    private static float INNER_RING_GROWTH_FACTOR = 0.15f;
    
    public static class FolderRingAnimator {
        public int mCellX;
        public int mCellY;
        public float mOuterRingSize;
        public float mInnerRingSize;
        public XFolderIcon mFolderIcon = null;
//        public Drawable mOuterRingDrawable = null;
//        public Drawable mInnerRingDrawable = null;
        public static Drawable sSharedOuterRingDrawable = null;
        public static Drawable sSharedInnerRingDrawable = null;
        private static boolean sStaticValuesDirty = true;
        public /*static*/ int sPreviewSize = -1;
//        public /*static*/ int sPreviewPadding = -1;
        /*RK_ID: RK_FOLDER_ICON . AUT: zhanggx1 . DATE: 2012-02-28 . S*/
        public /*static*/ int sPreviewPaddingTop = -1;
//        public /*static*/ int mFolerIconPaddingTop = 0;
        /*RK_ID: RK_FOLDER_ICON . AUT: zhanggx1 . DATE: 2012-02-28 . E*/
        XWorkspace mWorkspace;
        XContext mXContext;
        XHotseat mHotseat;
        private ValueAnimator mAcceptAnimator;
        private ValueAnimator mNeutralAnimator;

        public FolderRingAnimator(XContext xContext, XFolderIcon folderIcon) {
            mFolderIcon = folderIcon;
            mXContext = xContext;
            Resources res = xContext.getContext().getResources();            
            LauncherApplication app = (LauncherApplication) xContext.getContext().getApplicationContext();                        
        
            INNER_RING_GROWTH_FACTOR = com.lenovo.launcher.components.XAllAppFace.utilities.Utilities.formatInt2Float(res.getDimensionPixelSize(R.dimen.foldericon_inner_ring_factor));
            if (SettingsValue.getCurrentMachineType(mXContext.getContext()) >= 0) {
                INNER_RING_GROWTH_FACTOR /= 10f;
            }
            
            sPreviewSize = XShortcutIconView.getAppIconSize();
            sPreviewPaddingTop = XShortcutIconView.getIconPaddingTop();

            // We need to reload the static values when configuration changes in case they are
            // different in another configuration
            if (sStaticValuesDirty) {
                
                /*RK_ID: RK_FOLDER_ICON . AUT: zhanggx1 . DATE: 2012-02-28 . E*/
                sSharedOuterRingDrawable = app.mLauncherContext.getDrawable(R.drawable.portal_ring_outer_holo);
                sSharedInnerRingDrawable = app.mLauncherContext.getDrawable(R.drawable.portal_ring_inner_holo);
//                sSharedFolderLeaveBehind = app.mLauncherContext.getDrawable(R.drawable.portal_ring_rest);
//              if (inHotseat) {
//                int inSize = mInnerRingDrawable.getIntrinsicWidth();
//                int resetSize = sSharedFolderLeaveBehind.getIntrinsicWidth();
//                float quote = (smallIconSize * 1.0f) / inSize;
//                sSharedFolderLeaveBehind = 
//                        new FastBitmapDrawable(Bitmap.createScaledBitmap(
//                                Utilities.drawableToBitmap(sSharedFolderLeaveBehind), 
//                                (int)(resetSize * quote), (int)(resetSize * quote), true));
//              }
                sStaticValuesDirty = false;
            }

            /*RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2011-01-04 . E*/
        }

        public void setFolderIcon(XFolderIcon folderIcon) {
            mFolderIcon = folderIcon;
        }
        
        public void animateToAcceptState() {
            if (mNeutralAnimator != null) {
                mNeutralAnimator.cancel();
            }
            mAcceptAnimator = ValueAnimator.ofFloat(0f, 1f);
            mAcceptAnimator.setDuration(CONSUMPTION_ANIMATION_DURATION);
            mAcceptAnimator.addUpdateListener(new AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    final float percent = (Float) animation.getAnimatedValue();
                    mOuterRingSize = (1 + percent * OUTER_RING_GROWTH_FACTOR) * sPreviewSize;
                    mInnerRingSize = (1 + percent * INNER_RING_GROWTH_FACTOR) * sPreviewSize;
//                    if (mCellLayout != null) {
//                        mCellLayout.invalidate();
//                    }
                }
            });
            mAcceptAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    R5.echo("mAcceptAnimator onAnimationStart");
//                    if (mFolderIcon != null) {
//                        mFolderIcon.mPreviewBackground.setVisibility(false);
//                        R5.echo("mFolderIcon.mPreviewBackground false");
//                    }
                }
            });
            
            if (mFolderIcon != null) {                
                mXContext.post(mPreviewInvisbleRunnable);                
            }
//            mAcceptAnimator.start();
            mXContext.getRenderer().injectAnimation(mAcceptAnimator, false);
        }
        
        Runnable mPreviewInvisbleRunnable = new Runnable() {
            public void run() {
            	mFolderIcon.mPreviewBackground.setVisibility(false);
            	mFolderIcon.mBlackshadow.setVisibility(false);
            	R5.echo("mFolderIcon.mPreviewBackground false");
                mFolderIcon.invalidate();
            }
        };

        public void animateToNaturalState() {
            if (mAcceptAnimator != null) {
                mAcceptAnimator.cancel();
            }
            mNeutralAnimator = ValueAnimator.ofFloat(0f, 1f);
            mNeutralAnimator.setDuration(CONSUMPTION_ANIMATION_DURATION);
            mNeutralAnimator.addUpdateListener(new AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    final float percent = (Float) animation.getAnimatedValue();
                    mOuterRingSize = (1 + (1 - percent) * OUTER_RING_GROWTH_FACTOR) * sPreviewSize;
                    mInnerRingSize = (1 + (1 - percent) * INNER_RING_GROWTH_FACTOR) * sPreviewSize;
//                    if (mCellLayout != null) {
//                        mCellLayout.invalidate();
//                    }
                }
            });
            mNeutralAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    R5.echo("mNeutralAnimator onAnimationEnd");
                    closeAnimation();
                }
            });
//            mNeutralAnimator.start();
//            closeAnimation();
            mXContext.getRenderer().injectAnimation(mNeutralAnimator, false);
        }
        
        public void toNaturalState() {
            if (mAcceptAnimator != null) {
                mAcceptAnimator.cancel();
            }
            
            if (mWorkspace != null) {
            	R5.echo("toNaturalState mWorkspace.hideFolderAccept(FolderRingAnimator.this)");
                mWorkspace.hideFolderAccept(FolderRingAnimator.this);
            }
            if (mHotseat != null) {
            	R5.echo("toNaturalState mHotseat.hideFolderAccept(FolderRingAnimator.this);");
            	mHotseat.hideFolderAccept(FolderRingAnimator.this);
            }
            if (mFolderIcon != null) {                
                mXContext.post(new Runnable() {
                    public void run() {
            			mXContext.removeCallbacks(mPreviewInvisbleRunnable);
                    	mFolderIcon.mPreviewBackground.setVisibility(true);
                    	mFolderIcon.mBlackshadow.setVisibility(true);
                    	//mFolderIcon.showShadow(SettingsValue.getIconSizeValue(mXContext.getContext()));
                    	R5.echo("mFolderIcon.mPreviewBackground true");
                        mFolderIcon.invalidate();
                    }
                });                
            }
        }
        
        public void closeAnimation(){
            if (mWorkspace != null) {
            	R5.echo("closeAnimation mWorkspace.hideFolderAccept(FolderRingAnimator.this);");
            	
                mWorkspace.hideFolderAccept(FolderRingAnimator.this);
            }
            if (mHotseat != null) {
            	R5.echo("closeAnimation mHotseat.hideFolderAccept(FolderRingAnimator.this);");
            	mHotseat.hideFolderAccept(FolderRingAnimator.this);
            }
            if (mFolderIcon != null) {                
//                mXContext.post(new Runnable() {
//                    public void run() {
            			mXContext.removeCallbacks(mPreviewInvisbleRunnable);
                    	mFolderIcon.mPreviewBackground.setVisibility(true);
                    	mFolderIcon.mBlackshadow.setVisibility(true);
                    	//mFolderIcon.showShadow(SettingsValue.getIconSizeValue(mXContext.getContext()));
                    	R5.echo("mFolderIcon.mPreviewBackground true");
                        mFolderIcon.invalidate();
//                    }
//                });                
            }
        }

        // Location is expressed in window coordinates
        public void getCell(int[] loc) {
            loc[0] = mCellX;
            loc[1] = mCellY;
        }

        // Location is expressed in window coordinates
        public void setCell(int x, int y) {
            mCellX = x;
            mCellY = y;
        }

        public float getOuterRingSize() {
            return mOuterRingSize;
        }

        public float getInnerRingSize() {
            return mInnerRingSize;
        }
        
        public void setWorkspace(XWorkspace workspace) {
            mWorkspace = workspace;
        }
        public void setHotseat(XHotseat hotseat) {
            mHotseat = hotseat;
        }
        public static void setDataDirty()
        {
            sStaticValuesDirty = true;
        }
        public static void updateForTheme(XContext xContext) {
            LauncherApplication app = (LauncherApplication) xContext.getContext().getApplicationContext();
            sSharedOuterRingDrawable = app.mLauncherContext.getDrawable(R.drawable.portal_ring_outer_holo);
            sSharedInnerRingDrawable = app.mLauncherContext.getDrawable(R.drawable.portal_ring_inner_holo);
        }
    }

    float getFolderScale() {
        return folder_scale;
    }
    private Bitmap mShadowBm = null;
    private Point mShadowPoint = new Point();
    private boolean enableShadow = false;
    public void setShowShadow(boolean b){
    	enableShadow = b;
    }
	public void showShadow(int size) {
		// TODO Auto-generated method stub
        final float scale = mXContext.getContext().getResources().getDisplayMetrics().density;
		enableShadow = true;
		if (mShadowBm != null && !mShadowBm.isRecycled()) {
			mShadowBm.recycle();
		}
		if(mPreviewBackground!=null && mPreviewBackground.getBackgroundDrawable()!=null)
			mShadowBm = ShadowUtilites.getShadowBitmap(Utilities.drawableToBitmap(mPreviewBackground.getBackgroundDrawable(),size,size),getXContext());
		int px = 0;
		int py = 0;
		if(mPreviewBackground!=null && mPreviewBackground.localRect!=null){
			px = (int)mPreviewBackground.localRect.left;
			py = (int)(mPreviewBackground.localRect.bottom+scale);
		}
		mShadowPoint = new Point(px, py);
		invalidate();
	}
	public void rmBlackShadow(){
		if(mBlackshadow !=null){
			mBlackshadow.setVisibility(false);
		}
	}
	
	//RK_TOUCH_SOUND dining 20130826 
    public boolean onSingleTapUp(MotionEvent e) {
		super.onSingleTapUp(e);
		return true;
	}
  //RK_TOUCH_SOUND dining 20130826 END
    public boolean isPreviewBgVisible(){
    	return mPreviewBackground.isVisible();
    }
    public void setPreviewBgVisible(boolean b){
    	mPreviewBackground.setVisibility(b);
    }
}
