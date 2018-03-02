package com.lenovo.launcher.components.XAllAppFace;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.slimengine.BaseDrawableGroup;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;
import com.lenovo.launcher.components.XAllAppFace.utilities.Utilities;
import com.lenovo.launcher2.commoninterface.ApplicationInfo;
import com.lenovo.launcher2.customizer.SettingsValue;

public class XApplistView extends BaseDrawableGroup {

	public static final String SHOW_WORKSPACE = "com.lenovo.launcher.SHOW_WORKSPACE";

	final XContext mContext;

	private AppContentView mAppContentView;
	private AppContentIndicator mPageIndicator;
	private XLoading mXLoading;
	private XSmartDock mSmartDock;
	private XWorkspaceThumb mWorkspaceThumb;
    private ValueAnimator mThumbFadeInAlphaAnim;
    private ValueAnimator mThumbFadeOutAlphaAnim;

	private RectF mRegion;

    private XDragController mDragController;

	public XApplistView(XContext context, RectF region) {
		super(context);
		
		mContext = context;

		mRegion = new RectF(region);

		mAppContentView = new AppContentView(new RectF(0, 0, 0, 0), mContext);
		mPageIndicator = new AppContentIndicator(mContext,
				new RectF(0, 0, 0, 0));
		mAppContentView.setPageSwitchListener(mPageIndicator);
		mAppContentView.setLoop(SettingsValue.isApplistLoop(mContext.getContext()));

		mSmartDock = new XSmartDock(mContext, new RectF(0, 0, 0, 0));
		setTouchable(false);
		
		addItem(mAppContentView);
		addItem(mPageIndicator);
		addItem(mSmartDock);
		
        mWorkspaceThumb = new XWorkspaceThumb(context, new RectF(0, 0, 0, 0));
        addItem(mWorkspaceThumb);
        mWorkspaceThumb.setVisibility(false);

		resize(region);
	}

	/*** RK_ID: DRAG. AUT: zhaoxy . DATE: 2013-02-19 . START ***/
    public void setup(XDragController mDragController) {
        this.mDragController = mDragController;
        this.mAppContentView.setup(mDragController);
    }
    /*** RK_ID: DRAG. AUT: zhaoxy . DATE: 2013-02-19 . END ***/

	public void resize(RectF rect) {
		// android.util.Log.i("resize", rect + "..");
		super.resize(rect);
		
		int boardHeight = mContext.getResources().getDimensionPixelSize(
				R.dimen.button_bar_height_plus_padding);
//		int indicatorPaddingBottom = mContext.getResources()
//				.getDimensionPixelSize(R.dimen.home_point_bottom_padding);
		int appViewPaddingTop = mContext.getResources().getDimensionPixelSize(
				R.dimen.workspace_screen_padding_top);
        int homePointHeight = ((LauncherApplication) (mContext.getContext().getApplicationContext())).mLauncherContext.getDimensionPixel(
				R.dimen.home_point_height, R.dimen.def__home_point_height);

		mPageIndicator.resize(new RectF(0, 0, rect.width(), homePointHeight));

		mPageIndicator.setRelativeY(rect.height() - boardHeight
				- homePointHeight);

		mAppContentView.resize(new RectF(0, 0, rect.width(), rect.height()
		        - boardHeight
		        - homePointHeight));
		mAppContentView.setRelativeY(appViewPaddingTop);

		mSmartDock.resize(new RectF(0, 0, rect.width(), boardHeight));
		mSmartDock.setRelativeY(rect.height() - boardHeight);
		
		int topPadding = getXContext().getResources().getDimensionPixelSize(R.dimen.allapps_thumb_top_padding);
		mWorkspaceThumb.resize(new RectF(0, 0, rect.width(), (boardHeight-topPadding)*1.5f));
		mWorkspaceThumb.setRelativeY(rect.height() - (boardHeight-topPadding)*1.5f);
	}
	
	public void setDataReady() {
        if (mXLoading != null) {
            mXLoading.stop();
            mXLoading.setVisibility(false);
            removeItem(mXLoading);
            mXLoading = null;
        }
        setTouchable(true);

        /* RK_ID: RK_ALLAPPS. AUT: liuli1 . DATE: 2013-05-29 . START */
        if (mSmartDock != null) {
            mSmartDock.setVisibility(true);
        }

        if (mWorkspaceThumb != null) {
            mWorkspaceThumb.setVisibility(false);
            if (mDragController != null) {
                mDragController.removeDropTarget(mWorkspaceThumb);
            }
        }
        /* RK_ID: RK_ALLAPPS. AUT: liuli1 . DATE: 2013-05-29 . END */
    }

	public AppContentView getAppContentView() {
		return mAppContentView;
	}

	public AppContentIndicator getAppContentIndicator() {
		return mPageIndicator;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY, float previousX, float previousY) {
		boolean res = super.onScroll(e1, e2, distanceX, distanceY, previousX, previousY);
		if (mAppContentView.isDraggingMode()) {
			DrawableItem draggingItem = mAppContentView.getDraggingItem(false);
			if (draggingItem != null) {
				res &= draggingItem.onScroll(e1, e2, distanceX, distanceY, previousX, previousY);
			}
		}
		return res;
	}

	@Override
	public boolean onFingerUp(MotionEvent e) {
		boolean res = super.onFingerUp(e);
		DrawableItem draggingItem = mAppContentView.getDraggingItem(true);
		if (draggingItem != null) {
//			android.util.Log.i("drag", "dragging item finger up.");
			res &= draggingItem.onFingerUp(e);
		}
		return res;
	}

	@Override
	public void draw(IDisplayProcess c) {
		super.draw(c);
		DrawableItem draggingItem = mAppContentView.getDraggingItem(false);
		if (draggingItem != null) {
			draggingItem.draw(c);
		}
	}

	public void updateTheme(final ArrayList<ApplicationInfo> apps) {
//		RectF tmpRect = new RectF(mPageIndicator.localRect);
//		removeItem(mPageIndicator);
//		mAppContentView.setPageSwitchListener(null);
//		mPageIndicator = new AppContentIndicator(mContext,
//				new RectF(0, 0, 0, 0));
//		mPageIndicator.resize(tmpRect);
//		mPageIndicator.onUpdatePage(mAppContentView.getPageCount());
//
//		mPageIndicator.setRelativeX(mAppContentView.getWidth() / 2);
//		mPageIndicator.setRelativeY(mAppContentView.getHeight() + 5);
//
//		mAppContentView.setPageSwitchListener(mPageIndicator);
//
//		addItem(mPageIndicator);
		setAppsWallpaper(false);

	    if(mPageIndicator != null){
	    	int homePointHeight = ((LauncherApplication) (mContext.getContext().getApplicationContext())).mLauncherContext.getDimensionPixel(
					R.dimen.home_point_height, R.dimen.def__home_point_height);
	        int boardHeight = mContext.getResources().getDimensionPixelSize(
				R.dimen.button_bar_height_plus_padding);

			mPageIndicator.resize(new RectF(0, 0, localRect.width(), homePointHeight));

			mPageIndicator.setRelativeY(localRect.height() - boardHeight
					- homePointHeight);	
			
	        mPageIndicator.getIndicators().updateTheme();
	    }
	    ((LauncherApplication) (mContext.getContext().getApplicationContext())).mLauncherContext.resetIconEdit();
	    mAppContentView.updateForTheme(apps);

//       if (mSmartDock != null) {
//	    	mSmartDock.updateTheme();
//	    }
	}
	
	private ValueAnimator inoutAnim = null;

    public void playEnterAnim(boolean enterByButton) {
        mAppContentView.setTouchable(false);
        final Camera mCamera = new Camera();
        if (inoutAnim != null) {
            mContext.getRenderer().ejectAnimation(inoutAnim);
        }
        String key = SettingsValue.getAppListEnterValue(mContext.getContext());
        if (!enterByButton) {
//            key = "A";
            mAppContentView.setTouchable(true);
            return;
        }
        final Resources res = mContext.getResources();
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
                        mAppContentView.setTouchable(true);
                    }
                    
                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }
                });
                mContext.getRenderer().injectAnimation(inoutAnim, false);
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
                        /* RK_ID: RK_TOUCHEVENT. AUT: liuli1 . DATE: 2012-12-25 . START */
//                        if (value > 0.8f) {
//                            mSmartDock.setBackgroundVisible(true);
//                        }
                        /* RK_ID: RK_TOUCHEVENT. AUT: liuli1 . DATE: 2012-12-25 . END */
                    }
                });
                inoutAnim.addListener(new AnimatorListener() {
                    
                    @Override
                    public void onAnimationStart(Animator animation) {
                        /* RK_ID: RK_TOUCHEVENT. AUT: liuli1 . DATE: 2012-12-25 . START */
//                        mSmartDock.setBackgroundVisible(false);
                        mSmartDock.setTouchable(false);
                        /* RK_ID: RK_TOUCHEVENT. AUT: liuli1 . DATE: 2012-12-25 . END */
                    }
                    
                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                    
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Matrix m = getMatrix();
                        m.reset();
                        updateMatrix(m);
                        mAppContentView.setTouchable(true);
                        /* RK_ID: RK_TOUCHEVENT. AUT: liuli1 . DATE: 2012-12-25 . START */
//                        mSmartDock.setBackgroundVisible(true);
                        mSmartDock.setTouchable(true);
                        /* RK_ID: RK_TOUCHEVENT. AUT: liuli1 . DATE: 2012-12-25 . END */
                    }
                    
                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }
                });
                mContext.getRenderer().injectAnimation(inoutAnim, false);
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
                        mAppContentView.setTouchable(true);
                    }
                    
                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }
                });
                mContext.getRenderer().injectAnimation(inoutAnim, false);
                break;
            case 'A':
                Matrix m4 = getMatrix();
                m4.reset();
                updateMatrix(m4);
                setAlpha(0);
                inoutAnim = ValueAnimator.ofFloat(0.2f, 1f).setDuration(duration);
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
                        mAppContentView.setTouchable(true);
                    }
                    
                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }
                });
                mContext.getRenderer().injectAnimation(inoutAnim, false);
                break;
            default:
            	break;
        }
    }
    
    public void setAppToPosition(ComponentName componentName)
    {
        mAppContentView.setAppToPosition(componentName);
    }  
    
    @Override
    public boolean onDown(MotionEvent e) {
    	
    	// wippling
//		final float x = e.getX();
//		final float y = e.getY();
//		((Launcher) mContext).getWorkspace().post(new Runnable() {
//
//			@Override
//			public void run() {
//				showWippling(x, y);
//			}
//		});

    	return super.onDown(e);
    }
    
    ValueAnimator wipplingAnim = ValueAnimator.ofFloat(0.1f, 1.8f);
	XIconDrawable wipplingShadow = null;

//	void showWippling(final float x, final float y) {
//
////		if(mContext.getRenderer().containsAnimation(wipplingAnim)){
////        return;
////    }
//		if(wipplingAnim != null){
//			wipplingAnim.cancel();
//		}
//		
//		if(wipplingShadow != null){
//			wipplingShadow.getMatrix().reset();
//		}
//		wipplingAnim.setDuration( 200L );
//		wipplingAnim.addUpdateListener(new AnimatorUpdateListener() {
//			@Override
//			public void onAnimationUpdate(ValueAnimator animation) {
//				
//				if (wipplingShadow == null) {
//					wipplingShadow = new XIconDrawable(mContext,
//							com.lenovo.launcher.components.XAllAppFace.utilities.Utilities
//									.getBitmapFromDrawable(mContext.getContext(),
//											R.drawable.add_lotus));
//					addItem( wipplingShadow );
//}
//				
//				Float scaleValue = (Float) animation.getAnimatedValue();
//				float alphaValue = scaleValue / 1.8f;
//				Matrix m = wipplingShadow.getMatrix();
//				m.reset();
//				m.setTranslate(x - wipplingShadow.getWidth()/2, y - wipplingShadow.getHeight()/2);
//				m.preScale(scaleValue, scaleValue,
//						wipplingShadow.localRect.centerX(),
//						wipplingShadow.localRect.centerY());
//				wipplingShadow.setAlpha(1 - alphaValue);
//			}
//		});
//
//		wipplingAnim.addListener(new AnimatorListener() {
//
//			@Override
//			public void onAnimationStart(Animator animation) {
//				wipplingShadow.setVisibility( true );
//			}
//
//			@Override
//			public void onAnimationRepeat(Animator animation) {
//			}
//
//			@Override
//			public void onAnimationEnd(Animator animation) {
//				wipplingShadow.setVisibility( false );
//				moveChildToIndex(wipplingShadow, XApplistView.this.getChildCount() - 1);
//			}
//
//			@Override
//			public void onAnimationCancel(Animator animation) {
//			}
//		});
//
//		mContext.getRenderer().injectAnimation(wipplingAnim, true);
//	}
	
	public void clearAppToPostionView() {
	    mAppContentView.clearAppToPostionView();
	}
	
	public void stopEditMode() {
		mAppContentView.stopEditMode();
        if (mWorkspaceThumb != null) {
            initFadeOutAnim();
            getXContext().getRenderer().injectAnimation(mThumbFadeOutAlphaAnim, false);
        }
	}

    private void initFadeOutAnim() {
        final int height = (int) mWorkspaceThumb.getHeight();
        if (mThumbFadeOutAlphaAnim == null) {
            mThumbFadeOutAlphaAnim = ValueAnimator.ofFloat(1f, 0);
            mThumbFadeOutAlphaAnim.setInterpolator(new DecelerateInterpolator(2f));
            mThumbFadeOutAlphaAnim.setDuration(300);
            mThumbFadeOutAlphaAnim.addUpdateListener(new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Matrix m = mWorkspaceThumb.getMatrix();
                    m.reset();
                    Float value = (Float) (animation.getAnimatedValue());
                    int translateY = (int) ((1f - value) * height);
                    m.setTranslate(0, translateY);
                    mWorkspaceThumb.updateMatrix(m);
                }
            });

            mThumbFadeOutAlphaAnim.addListener(new AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (mSmartDock != null) {
                        mSmartDock.setVisibility(true);
                    }
                    mWorkspaceThumb.setVisibility(false);
                    XApplistView.this.mDragController.removeDropTarget(mWorkspaceThumb);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }
            });
        }
    }

    public boolean isEditMode() {
		return mAppContentView.isEditMode();
	}
	
	public void updateSlideValue() {
		if (mAppContentView != null) {
			mAppContentView.updateSlideValue();
		}
	}

	public void setLoop(boolean isLoop) {
		final AppContentView appView = getAppContentView();
		if (appView != null) {
			appView.setLoop(isLoop);
		}
	}
	
	public void setIconTextBackgroundEnable(boolean enable) {
		final AppContentView appView = getAppContentView();
		if (appView != null) {
			appView.setIconTextBackgroundEnable(enable);
		}
	}
	
	public void appsCellYChanged() {
		final AppContentView appView = getAppContentView();
		if (appView != null) {
			appView.appsCellYChanged();
		}
	}

    public void updateForText() {
        if (mAppContentView != null) {
            mAppContentView.updateForText();
        }
    }

    public void startEditMode() {
        if (mWorkspaceThumb != null) {
            mWorkspaceThumb.init(mContext);
            initFadeInAnim();
            getXContext().getRenderer().injectAnimation(mThumbFadeInAlphaAnim, false);
        }

    }

    private void initFadeInAnim() {
        final int height = (int) mWorkspaceThumb.getHeight();
        if (mThumbFadeInAlphaAnim == null) {
            mThumbFadeInAlphaAnim = ValueAnimator.ofFloat(0, 1f);
            mThumbFadeInAlphaAnim.setInterpolator(new DecelerateInterpolator(2f));
            mThumbFadeInAlphaAnim.setDuration(400);
            mThumbFadeInAlphaAnim.addUpdateListener(new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Matrix m = mWorkspaceThumb.getMatrix();
                    m.reset();
                    Float value = (Float) (animation.getAnimatedValue());
                    int translateY = (int) ((1f - value) * height);
                    m.setTranslate(0, translateY);
                    mWorkspaceThumb.updateMatrix(m);
                }
            });

            mThumbFadeInAlphaAnim.addListener(new AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mWorkspaceThumb.setVisibility(true);
                    Matrix m = mWorkspaceThumb.getMatrix();
                    m.reset();
                    m.setTranslate(0, height);
                    mWorkspaceThumb.updateMatrix(m);

                    if (mSmartDock != null) {
                        mSmartDock.setVisibility(false);
                    }
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mWorkspaceThumb.invalidate();
                    XApplistView.this.mDragController.addDropTarget(mWorkspaceThumb);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }
            });
        }
    }
    
    private XBlackboard mBlackboard = null;

    public void setAppsWallpaper(boolean invalidate) {
    	Context context = mContext.getContext();
		boolean bAppTransparent = SettingsValue
				.getApplistSemiTransParentValueInStore(context);
		
		if (bAppTransparent) {
		    setBackgroundDrawable(null);
		    int value = SettingsValue.getApplistTransParentSettingValue(context);
		    if (mBlackboard != null) {
		        mBlackboard.setAlpha(value / 255f);
            } else {
                mBlackboard = new XBlackboard(getXContext(), this, value / 255f);
                mBlackboard.show();
            }
        } else {
            if (mBlackboard != null) {
                mBlackboard.hide();
                mBlackboard.destory();
                mBlackboard = null;
            }
            Uri appWallpaperUri = SettingsValue.getAppsWallperPath(
                    context, true);
            String awPath = appWallpaperUri == null ? null : appWallpaperUri
                    .getPath();

            Bitmap appsWallpaper = null;
            if (awPath == null) {
                appsWallpaper = null;
            } else {
                appsWallpaper = BitmapFactory.decodeFile(awPath);
            }

            if (appsWallpaper != null) {
                setBackgroundDrawable(
                        new BitmapDrawable(appsWallpaper));
            } else {
                Drawable b = ((LauncherApplication) (mContext.getContext().getApplicationContext())).mLauncherContext
                        .getDrawable(R.drawable.all_apps_wallpaper);
                if (b != null) {
                    setBackgroundDrawable(b);// ////////dooba
                } else {
                    setBackgroundDrawable(null);
                }
            }
        }
		
		if (invalidate) {
			invalidate();
		}
    }
    
    public void setVisibility(boolean visibility) {
        if (!visibility)
        {
            clearAppToPostionView();
            mAppContentView.resetAnim(true);
        }
        super.setVisibility(visibility);
    }

    public void updateWorkspaceThumb(int screen) {
        if (mWorkspaceThumb != null) {
            mWorkspaceThumb.updateThumb(screen);
        }
    }
    public void updateIconSizeValue() {
		if (mAppContentView != null) {
	        final int iconsize = SettingsValue.getIconSizeValueNew(mContext.getContext());
			Utilities.updateIconSizeinApplist(mAppContentView,iconsize);
		}
	}
    
    public void showAppFlag(String pkgName, int num) {
        mAppContentView.showAppFlag(pkgName, num);
    }
    
    public void checkLoading() {
        if (mAppContentView.isDataReady())
        {
            return;
        }
        
        if (mXLoading == null)
        {
            mXLoading = new XLoading(mContext);
            addItem(mXLoading);
        }
        
        mXLoading.setRelativeX(getWidth() / 2 - mXLoading.getWidth() / 2);
        mXLoading.setRelativeY(getHeight() / 2 - mXLoading.getHeight() / 2);
        if (!mXLoading.isStart()) {
            mXLoading.start();
        }
    }
}
