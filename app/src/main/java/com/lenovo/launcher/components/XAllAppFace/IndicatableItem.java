package com.lenovo.launcher.components.XAllAppFace;

import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;

abstract class IndicatableItem extends DrawableItem {

	public IndicatableItem(XContext context) {
		super(context);
	}

	public static final byte INVALID_PAGE = -1;
	private boolean canTravel = false;
	public int page = INVALID_PAGE;
	protected boolean mNormalState = true;
	protected boolean mEnterState = false;
//	protected int mNormalAlpha = 255;

	void setCanTravel(boolean travelable) {
		canTravel = travelable;
	}

	public boolean isTravelable() {
		return canTravel;
	}

	void onShow(boolean useAnimation) {
	};

	void onTravel(float travelDisX, float travelDisY) {
	};

	void onHide(boolean useAnimation) {
	};
	
	void updateTheme(int width, int height, boolean rebuildBitmap){
	       
	};
	
	void showNormalState(boolean normalState) {
		mNormalState = normalState;
	};
	
	void showEnterState(boolean enterState) {
		mEnterState = enterState;
	};
		
//    public void setNormalAlpha(int alpha) {
//    	mNormalAlpha = alpha;
//    }

}
