package com.lenovo.launcher.components.XAllAppFace;

import android.graphics.RectF;
import android.view.MotionEvent;

import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;

public class XPressedTextView extends XTextView{

	public XPressedTextView(XContext context, String text, RectF rect) {
		super(context, text, rect);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void setPressed(boolean pressed) {
		// TODO Auto-generated method stub
		if (getParent() instanceof XShortcutIconView)
		{
			((XShortcutIconView)getParent()).mIconDrawable.setPressed(pressed);
		}		
				
		super.setPressed(pressed);
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		super.onDown(e);
		return true;
	}
	
	@Override
    public boolean onSingleTapUp(MotionEvent e) {
		super.onSingleTapUp(e);
		return true;
	}
	
    @Override
    public boolean onFingerUp(MotionEvent e) {
        super.onFingerUp(e);
        return true;
    }

    @Override
    public boolean onShowPress(MotionEvent e) {
        super.onShowPress(e);
        return true;
    }

    @Override
    public boolean onFingerCancel(MotionEvent e) {
        super.onFingerCancel(e);
        return true;
    }

}
