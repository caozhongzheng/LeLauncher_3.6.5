package com.lenovo.launcher.components.XAllAppFace;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;
import com.lenovo.launcher2.commoninterface.LauncherContext;
import com.lenovo.launcher2.customizer.Utilities;
import com.lenovo.launcher2.customizer.Debug.R5;

class ThemeStyleIndicator extends IndicatableItem {

	private static Bitmap normalFace = null;
	private static Bitmap showingFace = null;
	private static int mWidth;
	private static int mHeight;
	private static Drawable mNormalDrawable = null;
	private static Drawable mShowingDrawable = null;

	ValueAnimator showAnim = null;
	ValueAnimator hideAnim = null;

	LauncherContext mLauncherContext;
	
	XContext mXContext = null;

	public ThemeStyleIndicator(XContext xContext, LauncherContext context, RectF region, int width, int height) {
		super( xContext );
		disableCache();
		localRect = region;

		mLauncherContext = context;
		
		if (mNormalDrawable == null)
        {
        	mNormalDrawable = mContext.getResources().getDrawable(R.drawable.xpage_indicator_normal);
        }
        
        if (mShowingDrawable == null)
        {
        	mShowingDrawable = mContext.getResources().getDrawable(R.drawable.xpage_indicator_current);
        }         	

		updateTheme(width, height, true);
	}

	void updateTheme(int width, int height, boolean rebuildBitmap){
		R5.echo("updateTheme");
		mWidth = width;
		mHeight = height;
		
	    if (rebuildBitmap || normalFace == null)
        {
            normalFace = Utilities.drawableToBitmap(
                mLauncherContext.getDrawable(R.drawable.blackpoint), width, height);
        }
        
        if (rebuildBitmap || showingFace == null)
        {
            showingFace = Utilities.drawableToBitmap(
                    mLauncherContext.getDrawable(R.drawable.whitepoint), width, height);
        }
	}

	boolean isShowing = false;

	@Override
	public void onShow(boolean useAnimation) {

		if (isShowing) {
			return;
		}

		if (showAnim != null || hideAnim != null) {
			// android.util.Log.i("RX", "eject Animation.");
			getXContext().getRenderer().ejectAnimation(showAnim);
			getXContext().getRenderer().ejectAnimation(hideAnim);
		}
		
		if (!useAnimation)
		{
			isShowing = true;
			return;
		}
		
		showAnim = ValueAnimator.ofFloat(0.8f, 1.3f, 1.0f);
		showAnim.setDuration(350L);
		showAnim.setInterpolator(new DecelerateInterpolator());
		showAnim.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				Matrix m = new Matrix();
				Float value = (Float) (animation.getAnimatedValue());
				m.setScale(value, value, localRect.centerX(),
						localRect.centerY());
				updateMatrix(m);
				// invalidate();
			}
		});

		// android.util.Log.i("RX", "Inject Animation.");
		getXContext().getRenderer().injectAnimation(showAnim, true);

		isShowing = true;

	}

	@Override
	public void onTravel(float travelDisX, float travelDisY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onHide(boolean useAnimation) {

		if (!isShowing) {
			return;
		}

		if (hideAnim != null || showAnim != null) {
			// android.util.Log.i("RX", "eject Animation.");
			getXContext().getRenderer().ejectAnimation(hideAnim);
			getXContext().getRenderer().ejectAnimation(showAnim);
		}
		
		if (!useAnimation)
		{
			isShowing = false;
			return;
		}

		hideAnim = ValueAnimator.ofFloat(1.3f, 1.0f);
		hideAnim.setDuration(300L);
		hideAnim.setInterpolator(new DecelerateInterpolator());
		hideAnim.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				Matrix m = new Matrix();
				Float value = (Float) (animation.getAnimatedValue());
				m.setScale(value, value, localRect.centerX(),
						localRect.centerY());
				updateMatrix(m);
				// invalidate();
			}
		});

		// android.util.Log.i("RX", "Inject Animation.");
		getXContext().getRenderer().injectAnimation(hideAnim, true);
		isShowing = false;
	}

	@Override
	public void onDraw(IDisplayProcess c) {
		super.onDraw(c);
		if (showingFace == null || normalFace == null)
		{
			R5.echo("showingFace null");
			updateTheme(mWidth, mHeight, false);
		}
		
//		R5.echo("alpha = " + getPaint().getAlpha() + "float = " + alpha);
		
		if (mNormalState)
		{
//			int oldAlpha = 255;
//			if (mEnterState)
//			{
//				oldAlpha = getPaint().getAlpha();
//				getPaint().setAlpha(mNormalAlpha);
//			}
			
			if (isShowing) {
				c.drawBitmap(showingFace, mDrawX, mDrawY, getPaint());
			} else {
				c.drawBitmap(normalFace, mDrawX, mDrawY, getPaint());
			}
			
//			if (mEnterState)
//			{
//				getPaint().setAlpha(oldAlpha);
//			}
		}
		
		if (mEnterState)
		{
//			int oldAlpha = 255;
//			if (mNormalState)
//			{
//				oldAlpha = getPaint().getAlpha();
//				getPaint().setAlpha(255 - mNormalAlpha);
//			}
			
			RectF rect = ((XPagedViewIndicator)getParent()).getEnterDrawRect();
//			R5.echo("onDraw rect  = " + rect.toShortString());
			int alpha = (int) (getFinalAlpha() * 255);
			
			if (isShowing && ((XPagedViewIndicator)getParent()).getState() != XPagedViewIndicator.STATE_ENTER) {
			    mShowingDrawable.setAlpha(alpha);
				c.drawDrawable(mShowingDrawable, rect);
			} else {
			    mNormalDrawable.setAlpha(alpha);
				c.drawDrawable(mNormalDrawable, rect);
			}
			
//			if (mNormalState)
//			{
//				getPaint().setAlpha(oldAlpha);
//			}
		}
	}
	
	public static void clearBitmap()
    {      
	    if (normalFace != null)
        {
            normalFace.recycle();
            normalFace = null;
        }
        
        if (showingFace != null)
        {
            showingFace.recycle();
            showingFace = null;
        }
        
        return;
    }
	
	int mDrawX = 0;
	int mDrawY = 0;
	
    public void updatePosition(){
    	mDrawX = (int)((localRect.width() - mWidth) * .5f);
    	mDrawY = (int)((localRect.height() - mHeight) * .5f);
    }
        
    @Override
    public void resize(RectF rect) {
    	// TODO Auto-generated method stub
    	super.resize(rect);
    	updatePosition();
    }
    
    @Override
	void showNormalState(boolean normalState) {
		super.showNormalState(normalState);
//		if (!normalState)
//		{
//			mEnterWidth = mSmallWidth;
//			mEnterDrawX = (int)((localRect.width() - mEnterWidth) * .5f);
//		}
	}
	
	public static Drawable getShowingDrawable(){
		return mShowingDrawable;
	}
	
//	@Override
//	public void clean() {
//		normalFace.recycle();
//	    showingFace.recycle();
//	    
//	    normalFace = null;
//	    showingFace = null;
//		super.clean();
//	}	
}
