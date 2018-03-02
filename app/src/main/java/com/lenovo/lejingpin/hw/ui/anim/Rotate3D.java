package com.lenovo.lejingpin.hw.ui.anim;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class Rotate3D extends Animation {
	public static final int VERTICAL = 1;
	public static final int HORIZONTAL = 0;
	private Camera mCamera;
	private  int mOrientation;
	private  int mCenterX;
	private  int mCenterY;
	private  int mFromDegree;
	
	public void setOrientation(int orientation) {
		this.mOrientation = orientation;
	}

	public void setCenterX(int centerX) {
		this.mCenterX = centerX;
	}

	public void setCenterY(int centerY) {
		this.mCenterY = centerY;
	}

	public void setFromDegree(int fromDegree) {
		this.mFromDegree = fromDegree;
	}

	public void setToDegree(int toDegree) {
		this.mToDegree = toDegree;
	}

	private  int mToDegree;

	public Rotate3D(int fromDegree, int toDegree, int centerX, int centerY) {
		this.mFromDegree = fromDegree;
		this.mToDegree = toDegree;
		this.mOrientation = HORIZONTAL;
		this.mCenterX = centerX;
		this.mCenterY = centerY;
		mCamera = new Camera();
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		float toDegree = mFromDegree + ((mToDegree-mFromDegree)*interpolatedTime); 
		
		Matrix matrix = t.getMatrix();
		
		mCamera.save();
		
		if(mOrientation==HORIZONTAL){
			mCamera.rotateY(toDegree);
		}
		
		if(mOrientation==VERTICAL){
			mCamera.rotateX(toDegree);
		}
		
		mCamera.getMatrix(matrix);
		mCamera.restore();
		
		matrix.preTranslate(-mCenterX, -mCenterY);  
        matrix.postTranslate(mCenterX, mCenterY);
	}
}
