package com.lenovo.launcher.components.XAllAppFace;

import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.ComponentName;
import android.content.res.Resources;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;

import com.lenovo.launcher2.commoninterface.ApplicationInfo;
import com.lenovo.launcher2.customizer.SettingsValue;

public class AppContentTabHost extends XTabHost implements XTabHost.OnTabChangeListener {
	
	private XLauncher mLauncher;
	public AppContentView mAppContentView;
	public static final String APPS_TAB_TAG = "APPS";
    public static final String APPTASK_TAB_TAG = "APPTASK";
    public static final String APPRUNNING_TAB_TAG = "APPRUNNING";
    private static final int TABWIDGET_TEXT_SIZE = 30;
    
    /* RK_ID: RK_TASKKILLBAR. AUT: liuli1 . DATE: 2012-11-30 . START */
    private AppContentTabContent tabContent;
    /* RK_ID: RK_TASKKILLBAR. AUT: liuli1 . DATE: 2012-11-30 . END */
    private XLoading mXLoading;

	public AppContentTabHost(XContext context, RectF wholeRectF,
			RectF tabwidgetRect) {
		super(context, wholeRectF, tabwidgetRect);
		setTouchable(false);
	}
	
	public void setupViews() {
		mAppContentView = new AppContentView(new RectF(0, 0, 0, 0),
				getXContext());
        /* RK_ID: RK_TASKKILLBAR. AUT: liuli1 . DATE: 2012-11-30 . START */
//        TaskKillView taskKillView = new TaskKillView(new RectF(0, 0, 0, 0), getXContext());
//        tabContent.setup(getContext(), mAppContentView, taskKillView);
        /* RK_ID: RK_TASKKILLBAR. AUT: liuli1 . DATE: 2012-11-30 . END */
		
		TabContentFactory contentFactory = new TabContentFactory() {
            public DrawableItem createTabContent(String tag) {
            	// new art
//                return appsCustomizePane;
                /* RK_ID: RK_TASKKILLBAR. AUT: liuli1 . DATE: 2012-11-30 . START */
            		return tabContent;//mAppContentView;
                /* RK_ID: RK_TASKKILLBAR. AUT: liuli1 . DATE: 2012-11-30 . END */
            	// new art
            }
        };
        
        String label = getContext().getString(R.string.all_apps_button_label);
//        XTextArea tabView = new XTextArea(label, new RectF(0, 0, 150, 69));
//        tabView.setTextColor(0xffffffff);
//        tabView.setTextSize(TABWIDGET_TEXT_SIZE);
//        tabView.setTextAlign(Align.CENTER);
//        tabView.setText(label);
        addTab(newTabSpec(APPS_TAB_TAG).setIndicator(label).setContent(contentFactory));
        setOnTabChangedListener(this);
        
        label = getContext().getString(R.string.apptask_tab_label);
//        tabView = new XTextArea(label, new RectF(150, 0, 300, 69));
//        tabView.setTextColor(0xffffffff);
//        tabView.setTextSize(TABWIDGET_TEXT_SIZE);
//        tabView.setTextAlign(Align.CENTER);
//        tabView.setText(label);
        addTab(newTabSpec(APPTASK_TAB_TAG).setIndicator(label).setContent(contentFactory));
        setOnTabChangedListener(this);
        
        label = getContext().getString(R.string.apprunning_tab_label);
//        tabView = new XTextArea(label, new RectF(300, 0, 450, 69));
//        tabView.setTextColor(0xffffffff);
//        tabView.setTextSize(TABWIDGET_TEXT_SIZE);
//        tabView.setTextAlign(Align.CENTER);
//        tabView.setText(label);
        addTab(newTabSpec(APPRUNNING_TAB_TAG).setIndicator(label).setContent(contentFactory));
        setOnTabChangedListener(this);
        
        mXLoading = new XLoading(getXContext());
        addItem(mXLoading);
	}

	@Override
	public void onTabChanged(String tabId) {
		final String tab = tabId;
		getXContext().post(new Runnable() {

			@Override
			public void run() {
//				if (mAppContentView.getWidth() <= 0 ||
//                        mAppContentView.getHeight() <= 0) {
				    //mLauncher.setSurfaceIndicatorVisibility(APPS_TAB_TAG.equals(tab));
                    reloadCurrentPage(tab);
//                }
			}
		});
		getXContext().getRenderer().invalidate();
	}
	
	private void reloadCurrentPage(String tab) {
        /* RK_ID: RK_TASKKILLBAR. AUT: liuli1 . DATE: 2012-11-30 . START */
        tabContent.setTaskVisibility(tab, AppContentTabHost.APPRUNNING_TAB_TAG.equals(tab));
        /* RK_ID: RK_TASKKILLBAR. AUT: liuli1 . DATE: 2012-11-30 . END */

		mAppContentView.reloadData(tab);
	}
	
	public AppContentView getAppContentView() {
		return mAppContentView;
	}
	
	public void resize(RectF rect, RectF rectWidget) {
		super.resize(rect, rectWidget);
		mAppContentView.resize(new RectF(0, 0, getContent().getWidth(), getContent().getHeight()));
        mXLoading.setRelativeX(getWidth() / 2 - mXLoading.getWidth() / 2);
        mXLoading.setRelativeY(getHeight() / 2 - mXLoading.getHeight() / 2);
        mXLoading.start();
        /* RK_ID: RK_TASKKILLBAR. AUT: liuli1 . DATE: 2012-11-30 . START */
        tabContent.resize(new RectF(0, 0, getContent().getWidth(), getContent().getHeight()));
        /* RK_ID: RK_TASKKILLBAR. AUT: liuli1 . DATE: 2012-11-30 . END */
	}
	
	public void setDataReady() {
	    if (mXLoading != null) {
	        mXLoading.stop();
	        mXLoading.setVisibility(false);
        }
	    setTouchable(true);
	}
	
	public void updateForTheme(List<ApplicationInfo> apps) {
		//
//		changeTabHostTheme
		LauncherApplication app = (LauncherApplication) getContext().getApplicationContext();
		Drawable tabWidgetBg = app.mLauncherContext.getDrawable(R.drawable.tab_area_background);
		getTabWidget().setBackgroundDrawable(tabWidgetBg);
		
		for (int i = 0; i < getTabWidget().getChildCount(); i++) {
			Drawable tabSelector = app.mLauncherContext.getDrawable(R.drawable.tab_widget_indicator_selector);
			int textColor = app.mLauncherContext
					.getColor(R.color.tab_indicator_text_color, R.color.tab_indicator_text_color).getDefaultColor();
			getTabWidget().getChildAt(i).setBackgroundDrawable(tabSelector);
			((XTextArea)getTabWidget().getChildAt(i)).setTextColor(textColor);
		}
		//
		app.mLauncherContext.resetIconEdit();
		mAppContentView.updateForTheme(apps);
		invalidate();
	}
	
	public void setup(XLauncher launcher) {
		mLauncher = launcher;
		this.setupViews();
		
//		mAppContentView.setup(launcher);
	}
	
	//R2
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY, float previousX, float previousY) {
		boolean res = super.onScroll(e1, e2, distanceX, distanceY, previousX, previousY);
		if (mAppContentView.isDraggingMode()) {
			DrawableItem draggingItem = mAppContentView.getDraggingItem( false );
			if (draggingItem != null) {
				res &= draggingItem.onScroll(e1, e2, distanceX, distanceY, previousX, previousY);
			}
		}
		return res;
	}
	//2R
	
	@Override
	public boolean onFingerUp(MotionEvent e) {
		boolean res = super.onFingerUp(e);
		DrawableItem draggingItem = mAppContentView.getDraggingItem( true );
		if (draggingItem != null) {
			android.util.Log.i("drag", "dragging item finger up.");
			res &= draggingItem.onFingerUp(e);
		}
		return res;
}
	
	@Override
	public void draw(IDisplayProcess c) {
		super.draw(c);
		
		// R2
		DrawableItem draggingItem = mAppContentView.getDraggingItem( false );
		if (draggingItem != null) {
			draggingItem.draw(c);
		}
	    //2R
	}
	
    public void setAppToPosition(ComponentName componentName)
    {
        mAppContentView.setAppToPosition(componentName);
    }  

	private ValueAnimator inoutAnim = null;

    public void playEnterAnim() {
        final Camera mCamera = new Camera();
        if (inoutAnim != null) {
        	getXContext().getRenderer().ejectAnimation(inoutAnim);
        }
        String key = SettingsValue.getAppListEnterValue(getXContext().getContext());
        final Resources res = getXContext().getContext().getResources();
        final int duration = res.getInteger(R.integer.config_appsCustomizeZoomInTime);
        final int fadeDuration = res.getInteger(R.integer.config_appsCustomizeFadeInTime);
        switch (key.charAt(0)) {
            case 'Z': // Zoom
                final float scale = (float) res.getInteger(R.integer.config_appsCustomizeZoomScaleFactor);
                final float fadeStartPoint = fadeDuration * 1f / duration;
                setAlpha(0);
                Matrix m = getMatrix();
                m.reset();
                m.setScale(scale, scale, localRect.centerX(),
                        localRect.centerY());
                inoutAnim = ValueAnimator.ofFloat(0f, 1f);
                inoutAnim.setDuration(duration);
                inoutAnim.setInterpolator(new DecelerateInterpolator(2f));
                inoutAnim.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Matrix m = getMatrix();
                        m.reset();
                        Float value = (Float) (animation.getAnimatedValue());
                        float s = scale - value * (scale - 1);
                        m.setScale(s, s, localRect.centerX(),
                                localRect.centerY());
                        updateMatrix(m);
                        setAlpha(Math.max(0, (value + fadeStartPoint - 1) / fadeStartPoint));
                    }
                });
                inoutAnim.addListener(new AnimatorListener() {
                    
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }
                    
                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                    
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        setAlpha(1);
                        Matrix m = getMatrix();
                        m.reset();
                        updateMatrix(m);
                    }
                    
                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }
                });
                getXContext().getRenderer().injectAnimation(inoutAnim, true);
                break;
            case 'W': // windmill
                Matrix m2 = getMatrix();
                m2.reset();
                m2.setScale(.2f, .2f, localRect.centerX(), localRect.centerY());
                updateMatrix(m2);
                inoutAnim = ValueAnimator.ofFloat(0f, 1f);
                inoutAnim.setDuration(duration);
                inoutAnim.setInterpolator(new DecelerateInterpolator());
                inoutAnim.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Matrix m = getMatrix();
                        m.reset();
                        Float value = (Float) (animation.getAnimatedValue());
                        float s = .2f + value * .8f;
                        m.setScale(s, s, localRect.centerX(), localRect.centerY());
                        m.postRotate(360 * value, localRect.centerX(), localRect.centerY());
                        updateMatrix(m);
                    }
                });
                inoutAnim.addListener(new AnimatorListener() {
                    
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }
                    
                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                    
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Matrix m = getMatrix();
                        m.reset();
                        updateMatrix(m);
                    }
                    
                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }
                });
                getXContext().getRenderer().injectAnimation(inoutAnim, true);
                break;
            case 'F': // Flip
                Matrix m3 = getMatrix();
                m3.reset();
                mCamera.save();
                mCamera.rotateY(90);
                mCamera.getMatrix(m3);
                mCamera.restore();
                m3.preTranslate(-localRect.centerX(), -localRect.centerY());
                m3.postTranslate(localRect.centerX(), localRect.centerY());
                updateMatrix(m3);
                inoutAnim = ValueAnimator.ofFloat(0f, 1f);
                inoutAnim.setDuration(duration);
                inoutAnim.setInterpolator(new DecelerateInterpolator(2f));
                inoutAnim.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Matrix m = getMatrix();
                        m.reset();
                        Float value = (Float) (animation.getAnimatedValue());
                        mCamera.save();
                        mCamera.rotateY(90 - 90 * value);
                        mCamera.getMatrix(m);
                        mCamera.restore();
                        m.preTranslate(-localRect.centerX(), -localRect.centerY());
                        m.postTranslate(localRect.centerX(), localRect.centerY());
                        updateMatrix(m);
                    }
                });
                inoutAnim.addListener(new AnimatorListener() {
                    
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }
                    
                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                    
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Matrix m = getMatrix();
                        m.reset();
                        updateMatrix(m);
                    }
                    
                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }
                });
                getXContext().getRenderer().injectAnimation(inoutAnim, true);
                break;
            case 'A':
                setAlpha(0);
                inoutAnim = ValueAnimator.ofFloat(0.2f, 1f).setDuration(fadeDuration);
                inoutAnim.setInterpolator(new DecelerateInterpolator(1.5f));
                inoutAnim.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Float value = (Float) (animation.getAnimatedValue());
                        setAlpha(value);
                    }
                });
                inoutAnim.addListener(new AnimatorListener() {
                    
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }
                    
                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                    
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        setAlpha(1);
                    }
                    
                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }
                });
                getXContext().getRenderer().injectAnimation(inoutAnim, true);
                break;
            default:
            	break;
        }
    }
}
