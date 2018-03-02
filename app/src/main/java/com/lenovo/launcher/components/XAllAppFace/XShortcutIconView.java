package com.lenovo.launcher.components.XAllAppFace;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.slimengine.BaseDrawableGroup;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;
import com.lenovo.launcher2.commoninterface.IconCache;
import com.lenovo.launcher2.commoninterface.LauncherSettings;
import com.lenovo.launcher2.commoninterface.ShortcutInfo;
import com.lenovo.launcher2.commoninterface.ShowStringInfo;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.ShadowUtilites;
import com.lenovo.launcher2.customizer.TipsUtilities;
import com.lenovo.launcher2.customizer.TipsUtilities.TipPoint;

public class XShortcutIconView extends BaseDrawableGroup {
    
    private static final int DEFAULT_TEXT_SIZE = 14;
    //Ignore in sonar for pad. 横竖屏时需要重置以下数值，考虑到图标较多，为减少处理压力，故使用静态参数并只在横竖屏时重新加载一次.
    private static int app_icon_padding_top = 0, app_icon_size = 0,paddingleft=0,paddingright=0, app_icon_text_width = 0;
    // fix bug 17768 by liuli1
    private static int folder_icon_padding_top = 0;
    private static int hotseat_icon_padding_top = 0;
    private static int iconDrawablePadding = 0;
    public static boolean dimenDirty = true;
    XPressIconDrawable mIconDrawable;
    XPressedTextView mTextView;
    Intent intent;
    XContext mContext;
    boolean inHotseat = false;
    boolean inFolder = false;
    
    private ShortcutInfo infoLocal = null;
    
    public XShortcutIconView(XShortcutIconView iconView){
    	this(iconView.infoLocal, iconView.localRect, iconView.mContext);
    }
    public XShortcutIconView(XContext context) {
    	super(context);
    }
	public XShortcutIconView(ShortcutInfo info, RectF rect, XContext context) {
        super(context);
        mContext = context;
        app_icon_size = mContext.getResources().getDimensionPixelSize(R.dimen.app_icon_drawable_size);
        folder_icon_padding_top = mContext.getResources().getDimensionPixelOffset(R.dimen.folder_app_icon_padding_top);
        iconDrawablePadding = mContext.getResources().getDimensionPixelOffset(R.dimen.app_icon_drawable_padding);
        
        app_icon_text_width = mContext.getResources().getDimensionPixelSize(R.dimen.app_icon_text_width);

        paddingleft = paddingright  = mContext.getResources().getDimensionPixelOffset(R.dimen.app_text_l_r_padding);
        if (rect != null && rect.width() > 0 && rect.height() > 0) {
            this.localRect.set(rect);
            setInvertMatrixDirty();
        }
        inHotseat = info.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT;
        final float scale = context.getResources().getDisplayMetrics().density;
        LauncherApplication la = (LauncherApplication)context.getContext().getApplicationContext();
        
        mIconDrawable = new XPressIconDrawable(context, info.getIcon(la.mIconCache, true));
        mIconDrawable.setFillMode(XIconDrawable.FILLMODE_WRAPSRC);
        mIconDrawable.resize(new RectF(0, 0, app_icon_size, app_icon_size));
        mIconDrawable.setRelativeX(this.localRect.width() / 2 - mIconDrawable.localRect.width() / 2);
        //add by zhanggx1 on 2013-07-03.s
        if (info.container == LauncherSettings.Favorites.CONTAINER_DESKTOP && app_icon_padding_top <= 0
                || dimenDirty) {
            app_icon_padding_top = mContext.getResources().getDimensionPixelOffset(
                    R.dimen.app_icon_padding_top);
            if (SettingsValue.getCurrentMachineType(mContext.getContext()) == -1) {
                app_icon_padding_top = (int) (this.localRect.height() / 2 - mIconDrawable.localRect.height() / 2);
            }
        }
        if(info.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT&& hotseat_icon_padding_top<= 0
                || dimenDirty){
        	hotseat_icon_padding_top = mContext.getResources().getDimensionPixelOffset(
                    R.dimen.app_icon_hotseat_padding_top);
        }
        //add by zhanggx1 on 2013-07-03.e
        mIconDrawable.setRelativeY(inHotseat ? hotseat_icon_padding_top : app_icon_padding_top);
        if (inFolder) {
            mIconDrawable.setRelativeY(inHotseat ? 0 : folder_icon_padding_top);
        }
        CharSequence title = info.replaceTitle != null ? info.replaceTitle : info.title;
        
        mTextView = new XPressedTextView(context, title.toString(), new RectF(0, 0, app_icon_text_width, DEFAULT_TEXT_SIZE));
        
        /*if(inHotseat){
        	mTextView = new XTextView(context, title.toString(), new RectF(0, 0, this.localRect.width() - paddingleft - paddingright, DEFAULT_TEXT_SIZE));
        }else{
        	mTextView = new XTextView(context, title.toString(), new RectF(0, 0, this.localRect.width(), DEFAULT_TEXT_SIZE));
            
        }*/
        mTextView.setTextSize(Integer.valueOf(SettingsValue.getIconTextSizeValue(mContext.getContext())) * scale);
        mTextView.setRelativeX(this.localRect.width() / 2 - mTextView.localRect.width() / 2);
      //add by zhanggx1 on 2013-07-03.s
//        if (info.container <= 0
//        		&& SettingsValue.getCurrentMachineType(mContext.getContext()) == -1) {
//        	mTextView.setRelativeY(mIconDrawable.localRect.bottom 
//        			+ (localRect.height() - mIconDrawable.localRect.bottom - mTextView.localRect.height()) / 2);
//        } else {
            //add by zhanggx1 on 2013-07-03.e
            mTextView.setRelativeY(mIconDrawable.localRect.bottom + iconDrawablePadding);
//        }
        if (info.container == LauncherSettings.Favorites.CONTAINER_DESKTOP
        		&& !inFolder
        		&& (app_icon_padding_top + mIconDrawable.localRect.height() + iconDrawablePadding + mTextView.localRect.height() > localRect.height())) {
        	mTextView.setRelativeY(localRect.height() - mTextView.localRect.height());
        	app_icon_padding_top = (int)(localRect.height() - mTextView.localRect.height() - iconDrawablePadding - mIconDrawable.localRect.height());
        	mIconDrawable.setRelativeY(app_icon_padding_top);
        }
        mTextView.setBackgroundEnable(SettingsValue.isDesktopTextBackgroundEnabled(context.getContext()));
        mTextView.enableCache();
        
        infoLocal = info;
        
        intent = info.intent;
        
        //removed by yumina on 2013-07-12
        //mIconDrawable.setTouchable(false);
        //mTextView.setTouchable(false);
        
        addItem(mIconDrawable);
        addItem(mTextView);
        
        setTag(info);

        if (infoLocal.mNewAdd != 0)
        {
            showTipForNewAdded(infoLocal.mNewString);                        
        }
    }
	
	protected void setInFolder(boolean inFolder) {
        this.inFolder = inFolder;
        updateLayout();
    }

	@Override
	public void resize(RectF rect) {
	    super.resize(rect);
		if(mIconDrawable==null){
			return;
		}
	    if (dimenDirty) {
	        folder_icon_padding_top = mContext.getResources().getDimensionPixelOffset(R.dimen.folder_app_icon_padding_top);
	        iconDrawablePadding = mContext.getResources().getDimensionPixelOffset(R.dimen.app_icon_drawable_padding);
	        app_icon_size = mContext.getResources().getDimensionPixelSize(R.dimen.app_icon_drawable_size);
//	        if(infoLocal.container>0){
//	        	paddingleft = paddingright = 0;
//		    }else{
//		    	paddingleft = paddingright = mContext.getResources().getDimensionPixelOffset(R.dimen.app_text_l_r_padding);
//		    }
	        paddingleft = paddingright = mContext.getResources().getDimensionPixelOffset(R.dimen.app_text_l_r_padding);
	        //add by zhanggx1 on 2013-07-03.s
	        if (infoLocal.container == LauncherSettings.Favorites.CONTAINER_DESKTOP) {
	            app_icon_padding_top = mContext.getResources().getDimensionPixelOffset(R.dimen.app_icon_padding_top);
	            dimenDirty = false;
	            if (SettingsValue.getCurrentMachineType(mContext.getContext()) == -1) {
                    app_icon_padding_top = (int)(this.localRect.height() / 2 - mIconDrawable.localRect.height() / 2);
                }
	        }
	        if(infoLocal.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT){
	        	hotseat_icon_padding_top = mContext.getResources().getDimensionPixelOffset(
	                    R.dimen.app_icon_hotseat_padding_top);
	        }
	        //add by zhanggx1 on 2013-07-03.e
        }
	    mIconDrawable.setRelativeX((int) (getWidth() / 2 - mIconDrawable.getWidth() / 2));
	    mIconDrawable.setRelativeY(inHotseat ? hotseat_icon_padding_top : app_icon_padding_top);
        if (inFolder) {
            mIconDrawable.setRelativeY(inHotseat ? 0 : folder_icon_padding_top);
        }
	    //final float scale = mContext.getResources().getDisplayMetrics().density;
        
        
        mTextView.resize(new RectF(0, 0, app_icon_text_width, mTextView.getHeight()));
        
        
       /* if(inHotseat){
        	 mTextView.resize(new RectF(0, 0, getWidth(), mTextView.getHeight()));
        }else{
        	 mTextView.resize(new RectF(paddingleft, 0, getWidth()  - paddingright, mTextView.getHeight()));
            
        }*/
	   
	    mTextView.setRelativeX((int) (getWidth() / 2 - mTextView.getWidth() / 2));
	  //add by zhanggx1 on 2013-07-03.s
//        if (infoLocal.container <= 0
//        		&& SettingsValue.getCurrentMachineType(mContext.getContext()) == -1) {
//        	mTextView.setRelativeY(mIconDrawable.localRect.bottom 
//        			+ (localRect.height() - mIconDrawable.localRect.bottom - mTextView.localRect.height()) / 2);
//        } else {
            //add by zhanggx1 on 2013-07-03.e
            mTextView.setRelativeY(mIconDrawable.localRect.bottom + iconDrawablePadding);
//        }
		if (infoLocal.container == LauncherSettings.Favorites.CONTAINER_DESKTOP
				&& !inFolder
				&& (app_icon_padding_top + mIconDrawable.localRect.height()
						+ iconDrawablePadding + mTextView.localRect.height() > localRect
							.height())) {
			mTextView.setRelativeY(localRect.height()
					- mTextView.localRect.height());
			app_icon_padding_top = (int) (localRect.height() - mTextView.localRect.height()
					- iconDrawablePadding - mIconDrawable.localRect.height());
			mIconDrawable.setRelativeY(app_icon_padding_top);
		}
        
        locateTip();
	    mTextView.invalidate();
	}

	private void updateLayout() {
		if(mIconDrawable==null){
			return;
		}
	    mIconDrawable.setRelativeX((int) (getWidth() / 2 - mIconDrawable.getWidth() / 2));
//        mIconDrawable.setRelativeY(inHotseat ? 0 : app_icon_padding_top);
	    mIconDrawable.setRelativeY(inHotseat ? hotseat_icon_padding_top : app_icon_padding_top);
        if (inFolder) {
            mIconDrawable.setRelativeY(inHotseat ? 0 : folder_icon_padding_top);
        }
        //final float scale = mContext.getResources().getDisplayMetrics().density;
        mTextView.resize(new RectF(0, 0,app_icon_text_width, mTextView.getHeight()));
        mTextView.setRelativeX((int) (getWidth() / 2 - mTextView.getWidth() / 2));
        //add by zhanggx1 on 2013-07-03.s
//        if (infoLocal.container <= 0
//                && SettingsValue.getCurrentMachineType(mContext.getContext()) == -1) {
//            mTextView.setRelativeY(mIconDrawable.localRect.bottom 
//                    + (localRect.height() - mIconDrawable.localRect.bottom - mTextView.localRect.height()) / 2);
//        } else {
            //add by zhanggx1 on 2013-07-03.e
            mTextView.setRelativeY(mIconDrawable.localRect.bottom + iconDrawablePadding);
//        }
        locateTip();
        mTextView.invalidate();
	}

	@Override
	public boolean onDown(MotionEvent e) {
	    super.onDown(e);
//	    mIconDrawable.setAlpha(.6f);
	    return true;
	}
	
//    @Override
//    public void setPressed(boolean pressed) {
////    	R5.echo("setPressed " + pressed + "this =  " + this);
//        super.setPressed(pressed);
//        if (pressed)
//        {
//        	if(mIconDrawable!=null) mIconDrawable.setAlpha(.6f);
//        }
//        else
//        {
//        	 if(mIconDrawable!=null) mIconDrawable.setAlpha(1f);
//        }
//        
//        if(mIconDrawable!=null) mIconDrawable.invalidate();
//    }

	@Override
    public void resetPressedState() {
//	    mIconDrawable.setAlpha(1f);
	    super.resetPressedState();
    }

	public void setBackgroundEnable(boolean enable) {
	    if (mTextView != null) {
	        mTextView.setBackgroundEnable(enable);
        }
	}

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        super.onSingleTapUp(e);
        return true;
    }

    /* RK_ID: RK_ALLAPPS. AUT: liuli1 . DATE: 2012-11-13 . START */
    private ShortcutInfo mTag;

    protected void setTag(ShortcutInfo o) {
        mTag = o;
    }

    public ShortcutInfo getTag() {
        return mTag;
    }

    protected void updateIconText() {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        mTextView.setTextColor(mContext.getContext());
        mTextView
                .setTextSize(Integer.valueOf(SettingsValue.getIconTextSizeValue(mContext.getContext())) * scale);
        mTextView.setBackgroundEnable(SettingsValue.isDesktopTextBackgroundEnabled(mContext.getContext()));
    }

    @Override
    public boolean onFingerUp(MotionEvent e) {
        // fix bug , moving drag item to another page, up, pressed state not reset.
        resetPressedState();
        return super.onFingerUp(e);
    }

    public void setText(String text) {
        if (mTextView != null)
            mTextView.setText(text);
    }
    /* RK_ID: RK_ALLAPPS. AUT: liuli1 . DATE: 2012-11-13 . END */

    /* RK_ID: RK_MEM. AUT: liuli1 . DATE: 2012-11-22 . START */
    @Override
    public void clean() {
        intent = null;
        mContext = null;
        super.clean();
    }
    /* RK_ID: RK_MEM. AUT: liuli1 . DATE: 2012-11-22 . END */

	public ShortcutInfo getLocalInfo(){
		return infoLocal;
	}

	public void applyFromShortcutInfo(ShortcutInfo info, IconCache iconCache) {
        Bitmap b = info.getIcon(iconCache, true);
//        mIconDrawable = new XIconDrawable(getXContext(), b);
        mIconDrawable.setIconBitmap(b);
        boolean inHotseat = info.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT;
        mIconDrawable.resize(new RectF(0 ,0, app_icon_size, app_icon_size));
        mIconDrawable.setRelativeX(this.localRect.width() / 2 - mIconDrawable.localRect.width() / 2);
        mIconDrawable.setRelativeY(inHotseat? hotseat_icon_padding_top : app_icon_padding_top);
        if (inFolder) {
            mIconDrawable.setRelativeY(inHotseat ? hotseat_icon_padding_top : folder_icon_padding_top);
        }
        
        mIconDrawable.invalidate();
        mTextView.setText(info.replaceTitle != null 
        		? info.replaceTitle.toString() 
        		: info.title.toString());
        updateIconText();
        mTextView.setRelativeY(mIconDrawable.localRect.bottom + iconDrawablePadding);
        /* by liuyg1@lenovo.com 换主题后，dock区文字下移
        //RK_LECHANG_BUG  zhangluyuan@handscape.com.cn 2013-08-28 START
        //Bug 21803 -应用主题，Home键回到桌面，图标字体会跳动
        if (info.container <= 0
        		&& SettingsValue.getCurrentMachineType(mContext.getContext()) == -1) {
        	mTextView.setRelativeY(mIconDrawable.localRect.bottom 
        			+ (localRect.height() - mIconDrawable.localRect.bottom - mTextView.localRect.height()) / 2);
        } else {
            //add by zhanggx1 on 2013-07-03.e
            mTextView.setRelativeY(mIconDrawable.localRect.bottom + iconDrawablePadding);
        }
        //RK_LECHANG_BUG  zhangluyuan@handscape.com.cn 2013-08-28 END
         
         */
        setTag(info);
    }

	public XIconDrawable getIconDrawable() {
	    return mIconDrawable;
	}

	public XTextView getTextView() {
	    return mTextView;
	}

	/*RK_ID:RK_SD_APPS zhangdxa 2013-4-15. S***/
	/*private Bitmap updateDownloadPic(Context context, ShortcutInfo info,Bitmap bitmap){
	    Bitmap newBmp = bitmap;
	    if( bitmap != null && info != null){
	        if( info.itemType == LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT){
	        	Bitmap downIcon = null;
	    		downIcon = BitmapFactory.decodeResource(context.getResources(),
	    				R.drawable.stamp_app);
	    		newBmp = bitmap.copy(bitmap.getConfig(), true);
	    		Canvas c = new Canvas(newBmp);
	    		c.drawBitmap(downIcon, bitmap.getWidth() - downIcon.getWidth(), 0, null);
	    		c.setBitmap(null);
	       	}
	    }
	    return newBmp;
	}*/
	/*RK_ID:RK_SD_APPS zhangdxa 2013-4-15. E***/

    private Bitmap mTipBg = null;    
    private TipPoint mTipPoint;
    @Override
    public void onDraw(IDisplayProcess c) { 
    	 if (infoLocal!=null&&enableShadow && infoLocal.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
 			if (mShadowBm != null && !mShadowBm.isRecycled() && mShadowPoint != null) {
 				c.drawBitmap(mShadowBm, mShadowPoint.x, mShadowPoint.y, getPaint());
 			}
 		}
        super.onDraw(c);
        if(infoLocal==null){
        	return;
        }
       
		// OPT. chengliang
		if (mTipBg != null && !mTipBg.isRecycled()) {
			if ((infoLocal.mNewAdd == ShowStringInfo.SHOW_NEW || infoLocal.mNewAdd == ShowStringInfo.SHOW_NUM)
					&& infoLocal.itemType != LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT
					&& (infoLocal.container == LauncherSettings.Favorites.CONTAINER_DESKTOP || infoLocal.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT)) {
				c.drawBitmap(mTipBg, mTipPoint.x, mTipPoint.y, getPaint());
			} else if (infoLocal.mNewAdd == ShowStringInfo.SHOW_MISSED_NUM && enableNum) {
				c.drawBitmap(mTipBg, mTipPoint.x, mTipPoint.y, getPaint());
			}
		}


        /*if (infoLocal.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT && enableNum) {
			if (mNumBg != null && !mNumBg.isRecycled() && mNumPoint != null) {
				c.drawBitmap(mNumBg, mNumPoint.x, mNumPoint.y, getPaint());
			}
		}
        */
    }

    public void showTipForNewAdded(String str) {
//      if (mTipBg == null || mTipBg.isRecycled()) {
//          mTipBg = TipsUtilities.getTipDrawable(str, mContext);
//      }
        if (mTipBg != null)
        {
            mTipBg.recycle();
            mTipBg = null;
        }
      
        mTipPoint = null;
      
        if (str != null)
        {
          mTipBg = TipsUtilities.getTipDrawable(str, mContext);
  
          locateTip();
        }
    }
    
    private void locateTip() {
        if (mTipBg != null)
        {
            float backgroundPaddingTop = mIconDrawable.getRelativeY() ;
//          int widthGap = ((XLauncherView)mContext).getApplistView().getAppContentView().getWidthGap();
            int widthGap = ((XLauncherView)mContext).getWorkspace().getPagedView().getWidthGap();
            mTipPoint = TipsUtilities.getTipDrawableRelativeParentPosition(getWidth() + widthGap, mIconDrawable.localRect.right, backgroundPaddingTop,
                mTipBg.getWidth(), mTipBg.getHeight(), mContext, 3);
        }
    }

    public static int getIconPaddingTop() {
        return app_icon_padding_top;
    }

    static float getFolderIconPaddingTop() {
        return folder_icon_padding_top;
    }

    static float getHotseatIconPaddingTop() {
        return hotseat_icon_padding_top;
    }

    private Bitmap mShadowBm = null;
    private Point mShadowPoint = new Point();
    private boolean enableShadow = false;
	public void showShadow(int size) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
        final float scale = getXContext().getContext().getResources().getDisplayMetrics().density;
		enableShadow = true;
		if (mShadowBm != null && !mShadowBm.isRecycled()) {
			mShadowBm.recycle();
		}
		if(mIconDrawable!=null && mIconDrawable.iconBitmap!=null)
			mShadowBm = ShadowUtilites.getShadowBitmap(mIconDrawable.iconBitmap,getXContext());
		int px = 0;
		int py = 0;
		if(mIconDrawable!=null && mIconDrawable.localRect!=null){
			px = (int)mIconDrawable.localRect.left;
			py = (int)(mIconDrawable.localRect.bottom+scale);
		}
		mShadowPoint = new Point(px, py);
		invalidate();
	}
	private Bitmap mNumBg = null;
	private TipPoint mNumPoint = null;
	private boolean enableNum = false;

	public void showTipForNewAdded(int num) {
		if (num > 0) {
			enableNum = true;
			infoLocal.mNewAdd = ShowStringInfo.SHOW_MISSED_NUM;
			showTipForNewAdded(String.valueOf(num));
			/*if (mNumBg != null && !mNumBg.isRecycled()) {
				mNumBg.recycle();
			}
			mNumBg = TipsUtilities.getTipDrawable(num, getXContext());
			float width = mNumBg.getWidth();
			float height = mNumBg.getHeight();
			float parentRightX = this.getWidth();
			float childTopY = 0;
			float childRight = this.getRelativeX()
					+ this.getWidth();
			mNumPoint = TipsUtilities.getTipDrawableRelativeParentPosition(parentRightX,
			        childRight, childTopY, width, height,
					getXContext());*/
			invalidate();
		}
	}
	public void dismissTip() {
		
		enableNum = false;
       /* if (mNumBg != null && !mNumBg.isRecycled()) {
        	mNumBg.recycle();
        	mNumBg = null;
        }
        mNumPoint = null;*/
        invalidate();
    }
	
	public static int getAppIconSize(){
		return app_icon_size;
	}
}
