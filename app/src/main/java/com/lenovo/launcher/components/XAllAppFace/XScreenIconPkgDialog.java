package com.lenovo.launcher.components.XAllAppFace;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.slimengine.BaseDrawableGroup;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;

/**
 * 此为屏幕编辑的应用多选操作弹出框，包括多选的缩略图，形成文件夹，散放应用，取消按钮等
 * @author zhanggx1
 *
 */
public class XScreenIconPkgDialog extends BaseDrawableGroup {
	
	private DrawableItem mCreateFolder;
	private DrawableItem mAddIcons;
	private DrawableItem mCancel;
	private DrawableItem mSnap;
	private static final int DEGREE_OFFSET = 20;
	public static final int ICON_PKG_CREATE_FOLDER = 0;
    public static final int ICON_PKG_ADD_ICONS = 1;
    public static final int ICON_PKG_CANCEL_DRAG = 2;
    
    public static final int POS_LEFT_BOTTOM = 0;
    public static final int POS_LEFT_TOP = 1;
    public static final int POS_RIGHT_TOP = 2;
    public static final int POS_RIGHT_BOTTOM = 3;
    
    private int mPosition = POS_LEFT_BOTTOM;
    
    private ValueAnimator mFirstAnim = null;
    private ValueAnimator mSecondAnim = null;
    private ValueAnimator mThirdAnim = null;
    
    private ValueAnimator mFirstExitAnim = null;
    private ValueAnimator mSecondExitAnim = null;
    private ValueAnimator mThirdExitAnim = null;
    private static final long ANIMATION_DURATION = 100L;
    private int mExitAniCount = 3;
	private OnDestroyListener mDestroyListener;
    
    private float mSnapX;
    private float mSnapY;    
    private float mCreateFolderX;
    private float mCreateFolderY;
    private float mAddIconsX;
    private float mAddIconsY;
    private float mCancelX;
    private float mCancelY;
    

	public XScreenIconPkgDialog(XContext context, Bitmap snap, Object list, RectF rect, int positionType) {
		super(context);
		
		if (rect == null || rect.width() <= 0 || rect.height() <= 0 || snap == null) {
			return;
		}
		mPosition = positionType;
		
		Resources res = context.getResources();
		
		//多选应用的缩略图
		mSnap = new DrawableItem(context);
		mSnap.setBackgroundDrawable(new BitmapDrawable(snap));
		mSnap.resize(new RectF(0, 0, snap.getWidth(), snap.getHeight()));
		
		//形成文件夹按钮
		mCreateFolder = new DrawableItem(context);
		Drawable bg = res.getDrawable(R.drawable.ic_launcher_app);//res.getDrawable(R.drawable.xscreen_create_folder);
		mCreateFolder.setBackgroundDrawable(bg);
		mCreateFolder.setTag(ICON_PKG_CREATE_FOLDER);
		mCreateFolder.resize(new RectF(0, 0, bg.getIntrinsicWidth(), bg.getIntrinsicHeight()));
		
		//散放应用按钮
		mAddIcons = new DrawableItem(context);
		mAddIcons.setBackgroundDrawable(res.getDrawable(R.drawable.ic_launcher_app));//res.getDrawable(R.drawable.xscreen_add_icons));
		mAddIcons.setTag(ICON_PKG_ADD_ICONS);
		mAddIcons.resize(new RectF(0, 0, bg.getIntrinsicWidth(), bg.getIntrinsicHeight()));
		
		//取消按钮
		mCancel = new DrawableItem(context);
		mCancel.setBackgroundDrawable(res.getDrawable(R.drawable.ic_launcher_app));//res.getDrawable(R.drawable.xscreen_cancel_drag));
		mCancel.setTag(ICON_PKG_CANCEL_DRAG);
		mCancel.resize(new RectF(0, 0, bg.getIntrinsicWidth(), bg.getIntrinsicHeight()));
		
		addItem(mSnap);
		addItem(mCreateFolder);
		addItem(mAddIcons);
		addItem(mCancel);
		
		setTag(list);
		
		resize(rect);
	}
	
	public int getIconRelativeX(float touchX) {
		return (int)(touchX + mSnap.getRelativeX());
	}
	
	public int getIconRelativeY(float touchY) {
		return (int)(touchY + mSnap.getRelativeY());
	}
	
	public float getIconRelativeX() {
		return mSnap.getRelativeX();
	}
	
	public float getIconRelativeY() {
		return mSnap.getRelativeY();
	}
	
	@Override
	public void resize(RectF rect) {
		super.resize(rect);
		
		switch (mPosition) {
		case POS_LEFT_BOTTOM:
			calculateLeftBottomType(rect);
			break;
		case POS_LEFT_TOP:
			calculateLeftTopType(rect);
			break;
		case POS_RIGHT_TOP:
			calculateRightTopType(rect);
			break;
		case POS_RIGHT_BOTTOM:
			calculateRightBottomType(rect);
			break;
		default:
			calculateLeftBottomType(rect);
			break;
		}
		positionIcons();
	}
	
	/**
	 * 缩略图放置在左下角，按钮向右上角弹，为最常用的
	 * @param rect
	 */
	private void calculateLeftBottomType(RectF rect) {
		float radium = rect.width() - mAddIcons.getWidth() / 2.0f - mSnap.getWidth() / 2.0f;
		double degreeBottom = DEGREE_OFFSET * Math.PI / 180;
		double xBottom = radium * Math.cos(degreeBottom);
		double yBottom = radium * Math.sin(degreeBottom);
				
		float bottomHeight = (float)(yBottom + mCancel.getHeight() / 2.0f);
		float topHeight = rect.height() - bottomHeight;
		mSnapX = 0;
		mSnapY = topHeight - mSnap.getHeight() / 2.0f;		
		
		mCancelX = (float)(mSnap.getWidth() / 2.0f + xBottom - mCancel.getWidth() / 2.0f);
		mCancelY = rect.height() - mCancel.getHeight();		
		
		float yTop = topHeight - mCreateFolder.getHeight() / 2.0f;
		double degreeTop = Math.asin(yTop / radium);
		double xTop = radium * Math.cos(degreeTop);		
		
		mCreateFolderX = (float)(mSnap.getWidth() / 2.0f + xTop - mCreateFolder.getWidth() / 2.0f);
		mCreateFolderY = 0;		
		
		double degreeMiddle = (degreeBottom + degreeTop) / 2 - degreeBottom;
		double xMiddle = radium * Math.cos(degreeMiddle);
		double yMiddle = radium * Math.sin(degreeMiddle);
		
		mAddIconsX = (float)(mSnap.getWidth() / 2.0f + xMiddle - mAddIcons.getWidth() / 2.0f);
		mAddIconsY = (float)(topHeight - yMiddle - mAddIcons.getHeight() / 2.0f);
	}
	
	/**
	 * 缩略图放置在右下角，按钮向左上角弹，用于屏幕右侧一列（除右上角）
	 * @param rect
	 */
	private void calculateRightBottomType(RectF rect) {
		float radium = rect.width() - mAddIcons.getWidth() / 2.0f - mSnap.getWidth() / 2.0f;
		double degreeBottom = DEGREE_OFFSET * Math.PI / 180;
		double xBottom = radium * Math.cos(degreeBottom);
		double yBottom = radium * Math.sin(degreeBottom);
		
		float bottomHeight = (float)(yBottom + mCancel.getHeight() / 2.0f);
		float topHeight = rect.height() - bottomHeight;
		mSnapX = rect.width() - mSnap.getWidth();
		mSnapY = topHeight - mSnap.getHeight() / 2.0f;
		
		mCancelX = (float)(rect.width()
				- (mSnap.getWidth() / 2.0f + xBottom - mCancel.getWidth() / 2.0f) - mCancel.getWidth());
		mCancelY = rect.height() - mCancel.getHeight();
		
		float yTop = topHeight - mCreateFolder.getHeight() / 2.0f;
		double degreeTop = Math.asin(yTop / radium);
		double xTop = radium * Math.cos(degreeTop);
		
		mCreateFolderX = (float)(rect.width()
				- (mSnap.getWidth() / 2.0f + xTop - mCreateFolder.getWidth() / 2.0f) - mCreateFolder.getWidth());
		mCreateFolderY = 0;
		
		double degreeMiddle = (degreeBottom + degreeTop) / 2 - degreeBottom;
		double xMiddle = radium * Math.cos(degreeMiddle);
		double yMiddle = radium * Math.sin(degreeMiddle);
		mAddIconsX = (float)(rect.width()
				- (mSnap.getWidth() / 2.0f + xMiddle - mAddIcons.getWidth() / 2.0f) - mAddIcons.getWidth());
		mAddIconsY = (float)(topHeight - yMiddle - mAddIcons.getHeight() / 2.0f);
	}
	
	/**
	 * 缩略图在左上角，按钮弹在右下角，用于屏幕最上一列（除右上角）
	 * @param rect
	 */
	private void calculateLeftTopType(RectF rect) {
		float radium = rect.width() - mAddIcons.getWidth() / 2.0f - mSnap.getWidth() / 2.0f;
		double degreeTop = DEGREE_OFFSET * Math.PI / 180;
		double xTop = radium * Math.cos(degreeTop);
		double yTop = radium * Math.sin(degreeTop);
		
		float topHeight = (float)(yTop + mCreateFolder.getHeight() / 2.0f);
		float bottomHeight = rect.height() - topHeight;
		mSnapX = 0;
		mSnapY = topHeight - mSnap.getHeight() / 2.0f;
		
		mCreateFolderX = (float)(mSnap.getWidth() / 2.0f + xTop - mCreateFolder.getWidth() / 2.0f);
		mCreateFolderY = 0;
		
		float yBottom = bottomHeight - mCancel.getHeight() / 2.0f;
		double degreeBottom = Math.asin(yBottom / radium);
		double xBottom = radium * Math.cos(degreeBottom);
		
		mCancelX = (float)(mSnap.getWidth() / 2.0f + xBottom - mCancel.getWidth() / 2.0f);
		mCancelY = rect.height() - mCancel.getHeight();
		
		double degreeMiddle = (degreeBottom + degreeTop) / 2 - degreeTop;
		double xMiddle = radium * Math.cos(degreeMiddle);
		double yMiddle = radium * Math.sin(degreeMiddle);
		mAddIconsX = (float)(mSnap.getWidth() / 2.0f + xMiddle - mAddIcons.getWidth() / 2.0f);
		mAddIconsY = (float)(topHeight + yMiddle - mAddIcons.getHeight() / 2.0f);
	}
	
	/**
	 * 缩略图在右上角，按钮弹在左下角，用于屏幕右上角弹框
	 * @param rect
	 */
	private void calculateRightTopType(RectF rect) {
		float radium = rect.width() - mAddIcons.getWidth() / 2.0f - mSnap.getWidth() / 2.0f;
		double degreeTop = DEGREE_OFFSET * Math.PI / 180;
		double xTop = radium * Math.cos(degreeTop);
		double yTop = radium * Math.sin(degreeTop);
		
		float topHeight = (float)(yTop + mCreateFolder.getHeight() / 2.0f);
		float bottomHeight = rect.height() - topHeight;
		mSnapX = rect.width() - mSnap.getWidth();
		mSnapY = topHeight - mSnap.getHeight() / 2.0f;
		
		mCreateFolderX = (float)(rect.width() 
				- (mSnap.getWidth() / 2.0f + xTop - mCreateFolder.getWidth() / 2.0f) - mCreateFolder.getWidth());
		mCreateFolderY = 0;
		
		float yBottom = bottomHeight - mCancel.getHeight() / 2.0f;
		double degreeBottom = Math.asin(yBottom / radium);
		double xBottom = radium * Math.cos(degreeBottom);
		
		mCancelX = (float)(rect.width()
				- (mSnap.getWidth() / 2.0f + xBottom - mCancel.getWidth() / 2.0f) - mCancel.getWidth());
		mCancelY = rect.height() - mCancel.getHeight();
		
		double degreeMiddle = (degreeBottom + degreeTop) / 2 - degreeTop;
		double xMiddle = radium * Math.cos(degreeMiddle);
		double yMiddle = radium * Math.sin(degreeMiddle);
		mAddIconsX = (float)(rect.width()
				- (mSnap.getWidth() / 2.0f + xMiddle - mAddIcons.getWidth() / 2.0f) - mAddIcons.getWidth());
		mAddIconsY = (float)(topHeight + yMiddle - mAddIcons.getHeight() / 2.0f);
	}
	
	/**
	 * 设置三个按钮的点击事件
	 * @param listener
	 */
	public void setOnClickListeners(OnClickListener listener) {
		mAddIcons.setOnClickListener(listener);
		mCreateFolder.setOnClickListener(listener);
		mCancel.setOnClickListener(listener);
	}
	
	@Override
	public void draw(IDisplayProcess c) {
//		Paint paint = new Paint();
//		paint.setColor(Color.RED);		
//		c.drawRect(localRect, paint);
		super.draw(c);
	}
	
	/**
	 * 初始化动画
	 * @param item  做动画的按钮
	 * @param relativeX  按钮的终点X坐标
	 * @param relativeY  按钮的终点Y坐标
	 * @param delay  动画的延迟时间
	 * @return 动画对象
	 */
	private ValueAnimator initAnimation(final DrawableItem item, final float relativeX, final float relativeY, long delay) {	    
	    if (item == null) {
	    	return null;
	    }
		
	    //按钮的起始中心值
		final float snapCenterX = mSnapX + mSnap.getWidth() / 2.0f;
		final float snapCenterY = mSnapY + mSnap.getHeight() / 2.0f; 
		//按钮的终点中心值
		final float mDestCenterX = relativeX + item.getWidth() / 2.0f;
		final float mDestCenterY = relativeY + item.getHeight() / 2.0f;
		final float deltaX = mDestCenterX - snapCenterX;
		final float deltaY = mDestCenterY - snapCenterY;
		
		final Matrix matrix = item.getMatrix();
		
		//设置按钮的初始状态
		item.setRelativeX(snapCenterX - item.getWidth() / 2.0f);
		item.setRelativeY(snapCenterY - item.getHeight() / 2.0f);
		matrix.reset();
		matrix.setScale(0, 0, item.localRect.centerX(), item.localRect.centerY());
		
		ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
		anim.setInterpolator(new AccelerateDecelerateInterpolator());
		anim.setDuration(ANIMATION_DURATION);
		anim.setStartDelay(delay);
		anim.addListener(new AnimatorListener() {			
			@Override
			public void onAnimationStart(Animator animation) {
			}
			
			@Override
			public void onAnimationRepeat(Animator animation) {
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				matrix.reset();
				matrix.setScale(1.0f, 1.0f, item.localRect.centerX(), item.localRect.centerY());
				matrix.postTranslate(deltaX, deltaY);
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
				matrix.reset();
				matrix.setScale(1.0f, 1.0f, item.localRect.centerX(), item.localRect.centerY());
				matrix.postTranslate(deltaX, deltaY);
			}
		});
		anim.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				final float value = (Float)animation.getAnimatedValue();
				
				matrix.reset();
				matrix.setScale(value, value, item.localRect.centerX(), item.localRect.centerY());
				matrix.postTranslate(deltaX * value, deltaY * value);
			}
		});
		return anim;
	}
	
	/**
	 * 初始化退出动画
	 * @param item  做动画的按钮
	 * @param relativeX  按钮的终点X坐标
	 * @param relativeY  按钮的终点Y坐标
	 * @param delay  动画的延迟时间
	 * @return 动画对象
	 */
	private ValueAnimator initExitAnimation(final DrawableItem item, final float relativeX, final float relativeY, long delay) {	    
	    if (item == null) {
	    	return null;
	    }
		
	    //按钮的终点中心值
		final float snapCenterX = mSnapX + mSnap.getWidth() / 2.0f;
		final float snapCenterY = mSnapY + mSnap.getHeight() / 2.0f; 
		//按钮的起始中心值
		final float mDestCenterX = relativeX + item.getWidth() / 2.0f;
		final float mDestCenterY = relativeY + item.getHeight() / 2.0f;
		final float deltaX = snapCenterX - mDestCenterX;
		final float deltaY = snapCenterY - mDestCenterY;
		
		final Matrix matrix = item.getMatrix();
				
		ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
		anim.setInterpolator(new AccelerateDecelerateInterpolator());
		anim.setDuration(ANIMATION_DURATION);//ANIMATION_DURATION
		anim.setStartDelay(delay);
		anim.addListener(new AnimatorListener() {			
			@Override
			public void onAnimationStart(Animator animation) {
			}
			
			@Override
			public void onAnimationRepeat(Animator animation) {
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				matrix.reset();
				matrix.setScale(0.0f, 0.0f, item.localRect.centerX(), item.localRect.centerY());
				mExitAniCount--;
				destroyThemes();
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
				matrix.reset();
				matrix.setScale(0.0f, 0.0f, item.localRect.centerX(), item.localRect.centerY());
//				matrix.postTranslate(deltaX, deltaY);
			}
		});
		anim.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				final float value = (Float)animation.getAnimatedValue();
				
				matrix.reset();
				matrix.setScale(1 - value, 1 - value, item.localRect.centerX(), item.localRect.centerY());
				matrix.postTranslate(deltaX * value, deltaY * value);
			}
		});
		return anim;
	}
	
	/**
	 * 动画显示各个部分
	 */
	private void positionIcons() {		
		mSnap.setRelativeX(mSnapX);
		mSnap.setRelativeY(mSnapY);
		
		//初始化“添加文件夹按钮”的动画
		if (mFirstAnim == null) {
			mFirstAnim = initAnimation(mCreateFolder, mCreateFolderX, mCreateFolderY, 0);
		}
		//初始化“散放按钮”的动画
		if (mSecondAnim == null) {
			mSecondAnim = initAnimation(mAddIcons, mAddIconsX, mAddIconsY, ANIMATION_DURATION);
		}
		//初始化“取消按钮”的动画
		if (mThirdAnim == null) {
			mThirdAnim = initAnimation(mCancel, mCancelX, mCancelY, ANIMATION_DURATION * 2);
		}
		
		this.getXContext().getRenderer().injectAnimation(mFirstAnim, false);
		this.getXContext().getRenderer().injectAnimation(mSecondAnim, false);
		this.getXContext().getRenderer().injectAnimation(mThirdAnim, false);
	}
	
	public void dismiss() {
		mExitAniCount = 3;
		// 初始化“添加文件夹按钮”的动画
		if (mFirstExitAnim == null) {
			mFirstExitAnim = initExitAnimation(mCreateFolder, mCreateFolderX,
					mCreateFolderY, 0);
		}
		// 初始化“散放按钮”的动画
		if (mSecondExitAnim == null) {
			mSecondExitAnim = initExitAnimation(mAddIcons, mAddIconsX,
					mAddIconsY, 0);
		}
		// 初始化“取消按钮”的动画
		if (mThirdExitAnim == null) {
			mThirdExitAnim = initExitAnimation(mCancel, mCancelX, mCancelY, 0);
		}

		this.getXContext().getRenderer().injectAnimation(mFirstExitAnim, false);
		this.getXContext().getRenderer()
				.injectAnimation(mSecondExitAnim, false);
		this.getXContext().getRenderer().injectAnimation(mThirdExitAnim, false);
	}
	
	private void destroyThemes() {
		if (mExitAniCount != 0) {
			return;
		}
    	clearAllItems();
    	clean();
    	invalidate();
    	if (mDestroyListener != null) {
    		mDestroyListener.onDestroy();
    	}
	}
	
	public interface OnDestroyListener {
		public void onDestroy();
	}
	
	public void setOnDestroyListener(OnDestroyListener l) {
		mDestroyListener = l;
	}

}
