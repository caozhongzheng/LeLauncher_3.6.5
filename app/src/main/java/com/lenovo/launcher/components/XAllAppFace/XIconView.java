package com.lenovo.launcher.components.XAllAppFace;

import java.util.Random;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.slimengine.BaseDrawableGroup;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;
import com.lenovo.launcher2.commoninterface.ApplicationInfo;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.TipsUtilities;
import com.lenovo.launcher2.customizer.Debug.R2;
import com.lenovo.launcher2.customizer.Debug.R5;
import com.lenovo.launcher2.customizer.TipsUtilities.TipPoint;

public class XIconView extends BaseDrawableGroup {
    
    private static final int DEFAULT_TEXT_SIZE = 14;
    XIconDrawable mIconDrawable;

    XTextView mTextView;
    Intent intent;
    XContext mContext;
    private XIconDrawable mMarkerDelete = null;
    private XIconDrawable mMarkerKill = null;
    private boolean mEditMode = false;
    private ApplicationInfo infoLocal = null;
    private int app_icon_padding_top = 0;
    
    private ValueAnimator editmodeAnim = null;
    
    public XIconView(XIconView iconView){
    	this(iconView.infoLocal, iconView.localRect, iconView.mContext);
    }
    
	public XIconView(ApplicationInfo info, RectF rect, XContext context) {
	    
	    this(context, rect, info.iconBitmap, info.title.toString(), info.intent); 
	    infoLocal = info;
	    if (infoLocal.mNewAdd != 0)
        {
            showTipForNewAdded(infoLocal.mNewString);                        
        }
    }
	
	public XIconView(XContext context, RectF rect, Bitmap iconBitmap, String title, Intent intent) {
        super(context);
        mContext = context;
        app_icon_padding_top = mContext.getResources().getDimensionPixelSize(R.dimen.app_icon_padding_top);
        if (rect != null && rect.width() > 0 && rect.height() > 0) {
			resize(rect);
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        mIconDrawable = new XIconDrawable(context, iconBitmap);
        mIconDrawable.setRelativeX(this.localRect.width() / 2 - mIconDrawable.localRect.width() / 2);
        mIconDrawable.setRelativeY(app_icon_padding_top);
        mTextView = new XTextView(context, title, new RectF(0, 0, this.localRect.width() - mPaddingLeft - mPaddingRight, DEFAULT_TEXT_SIZE));
        mTextView.setTextSize(Integer.valueOf(SettingsValue.getIconTextSizeValue(mContext.getContext())) * scale);
        mTextView.setRelativeX(this.localRect.width() / 2 - mTextView.localRect.width() / 2);
        mTextView.setRelativeY(mIconDrawable.localRect.bottom + 1);
        mTextView.setBackgroundEnable(SettingsValue.isDesktopTextBackgroundEnabled(context.getContext()));
        mTextView.enableCache();
        
        this.intent = intent;
        
        mIconDrawable.setTouchable(false);
        mTextView.setTouchable(false);
        
        addItem(mIconDrawable);
        addItem(mTextView);
        
        LauncherApplication la = (LauncherApplication)context.getContext().getApplicationContext();
        mMarkerDelete = new XIconDrawable(mContext, la.mLauncherContext.getIconDelete());
        mMarkerDelete.setRelativeX(this.localRect.width() / 10);// old is (this.localRect.width() / 3) * 2
        mMarkerDelete.setRelativeY(this.localRect.height() / 20);
        mMarkerDelete.setVisibility(false);
    	addItem(mMarkerDelete);
    	
    	mMarkerKill = new XIconDrawable(mContext, la.mLauncherContext.getIconKill());
    	mMarkerKill.setRelativeX((this.localRect.width() / 3) * 2);
    	mMarkerKill.setRelativeY(this.localRect.height() / 20);
    	mMarkerKill.setVisibility(false);
    	addItem(mMarkerKill);
    	
    	//create face
    	this.invalidate();
    }
	
	@Override
	public boolean onDown(MotionEvent e) {
	    super.onDown(e);
	    mIconDrawable.setAlpha(.6f);
	    return true;
	}
	
	@Override
    public void resetPressedState() {
	    mIconDrawable.setAlpha(1f);
	    super.resetPressedState();
    }
	
	public void setBackgroundEnable(boolean enable) {
	    if (mTextView != null) {
	        mTextView.setBackgroundEnable(enable);
        }
	}
    
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        R2.echo("XIconView onSingleTapUp "+ mTextView.getText());
        super.onSingleTapUp(e);
        return true;
    }
    
    /* RK_ID: RK_ALLAPPS. AUT: liuli1 . DATE: 2012-11-13 . START */
    private ApplicationInfo mTag;

    protected void setTag(ApplicationInfo o) {
        mTag = o;
    }

    @Override
    public ApplicationInfo getTag() {
        return mTag;
    }

    protected void updateIconText() {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        mTextView.setTextColor(mContext.getContext());
        mTextView
                .setTextSize(Integer.valueOf(SettingsValue.getIconTextSizeValue(mContext.getContext())) * scale);
    }

    @Override
    public boolean onFingerUp(MotionEvent e) {
        // fix bug , moving drag item to another page, up, pressed state not reset.
        resetPressedState();
        return super.onFingerUp(e);
    }
    /* RK_ID: RK_ALLAPPS. AUT: liuli1 . DATE: 2012-11-13 . END */
    
    public void startEditMode(boolean delete) {
    	//R2
    	enableEditAnim();
    	//2R
    	mEditMode = true;
    	if (mTag == null || mTag.flags == 0) {
    		return;
    	}
    	if (delete) {
    	    mMarkerDelete.setVisibility(true);
    	    mMarkerKill.setVisibility(false);
    	} else {
    		mMarkerDelete.setVisibility(false);
    	    mMarkerKill.setVisibility(true);
    	}
    	invalidate();
    }
        
    public void stopEditMode() {
    	mMarkerDelete.setVisibility(false);
	    mMarkerKill.setVisibility(false);
    	mEditMode = false;
    	
    	//R2
    	disableEditAnim();
    	//2R
    	
    	invalidate();
    }
    
    /* RK_ID: RK_MEM. AUT: liuli1 . DATE: 2012-11-22 . START */
    @Override
    public void clean() {
        intent = null;
        mContext = null;
        super.clean();
    }
    /* RK_ID: RK_MEM. AUT: liuli1 . DATE: 2012-11-22 . END */
    
//    Float editModeAnimCallbackValue = 1f;
    int editModeAnimCallbackValue = 0;
    public void enableEditAnim(){
		
        //R2
		disableEditAnim();
    	Random r = new Random();
//		editmodeAnim = ValueAnimator.ofFloat(0.95f, 1.05f, 0.95f);
        editmodeAnim = ValueAnimator.ofInt(0, 5, 0, -5, 0);
		editmodeAnim.setDuration( 460L );
		editmodeAnim.setRepeatCount(ValueAnimator.INFINITE);
//		editmodeAnim.setInterpolator(new DecelerateInterpolator());
		editmodeAnim.setStartDelay( r.nextInt( 300 ) );
//		editmodeAnim.setCurrentPlayTime(r.nextInt( 300 ));
		editmodeAnim.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
//				editModeAnimCallbackValue = (Float) (animation.getAnimatedValue());
                editModeAnimCallbackValue = (Integer) animation.getAnimatedValue();
			}
		});
		mContext.getRenderer().injectAnimation(editmodeAnim, true);
    }
    
    public void disableEditAnim(){
    	mContext.getRenderer().ejectAnimation(editmodeAnim);
//    	editModeAnimCallbackValue = 1f;
        editModeAnimCallbackValue = 0;
    }

	@Override
	public void draw(IDisplayProcess c) {
		if (mEditMode && editModeAnimCallbackValue != 1f) {
//			getMatrix().preScale(editModeAnimCallbackValue,
//					editModeAnimCallbackValue, localRect.centerX(),
//					localRect.centerY());
            getMatrix().preRotate(editModeAnimCallbackValue, localRect.centerX(),
                    localRect.centerY());
		}	
		
		super.draw(c);
		
	}

	/*** RK_ID: XFOLDER. AUT: zhaoxy . DATE: 2013-01-22 . START ***/
	public XIconDrawable getIconDrawable() {
	    return mIconDrawable;
	}
	/*** RK_ID: XFOLDER. AUT: zhaoxy . DATE: 2013-01-22 . END ***/
	
	private Bitmap mTipBg = null;   
    private TipPoint mTipPoint;
    @Override
    public void onDraw(IDisplayProcess c) {               
        super.onDraw(c);
        
        if (infoLocal.mNewAdd == 1 || infoLocal.mNewAdd == 2)
        {
            if (mTipBg != null && !mTipBg.isRecycled()) {
                c.drawBitmap(mTipBg, mTipPoint.x, mTipPoint.y, getPaint());
            }
        }
        
    }
        
    public void showTipForNewAdded(String str) {
//        if (mTipBg == null || mTipBg.isRecycled()) {
//            mTipBg = TipsUtilities.getTipDrawable(str, mContext);
//        }
        if (mTipBg != null)
        {
            mTipBg.recycle();
            mTipBg = null;
        }
        
        mTipPoint = null;
        
        if (str != null)
        {
            mTipBg = TipsUtilities.getTipDrawable(str, mContext);
    
            float backgroundPaddingTop = mIconDrawable.getRelativeY() ;
            int widthGap = ((XLauncherView)mContext).getApplistView().getAppContentView().getWidthGap();
            mTipPoint = TipsUtilities.getTipDrawableRelativeParentPosition(getWidth() + widthGap, mIconDrawable.localRect.right, backgroundPaddingTop,
                    mTipBg.getWidth(), mTipBg.getHeight(), mContext, 3);
        }
    }
    
    public ApplicationInfo getLocalInfo(){
        return infoLocal;
    }

}
