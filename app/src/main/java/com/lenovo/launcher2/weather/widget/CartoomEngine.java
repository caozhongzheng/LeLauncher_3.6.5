package com.lenovo.launcher2.weather.widget;

import java.util.Timer;
import java.util.TimerTask;

import com.lenovo.launcher2.weather.widget.AnimationImageView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class CartoomEngine {
	public Handler 		mHandler; 
	public boolean 		mBCartoom;				// 是否正在作动画 
	public Timer   		mTimer;					// 用于作动画的TIMER 
	public MyTimerTask	mTimerTask;				// 动画任务
	public int          mTimerInterval;			// 定时器触发间隔时间(ms)	 
	public int        mCurUpProcess;		// 作动画时当前进度值 
	public int        mCurDownProcess;		// 作动画时当前进度值 
	public int mupMax;
	public int mdownMax;
	private AnimationImageView mAnimationImageView;
	private long timeMil;
	private boolean misup = false;
	public CartoomEngine(AnimationImageView AnimationImageView)
	{
		mAnimationImageView = AnimationImageView;
		mHandler = new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch(msg.what)
				{
					case TIMER_ID:
					{
						if (mBCartoom == false)
						{
							return ;
						}
						if(misup){
							mCurUpProcess -=7;
							mCurDownProcess +=7;

						}else{
							mCurUpProcess +=7;
							mCurDownProcess -=7;

						}
						mAnimationImageView.updateprogress(mCurUpProcess,
								mCurDownProcess);
						long curtimeMil = System.currentTimeMillis();
						timeMil = curtimeMil;
						if(misup){
							if (mCurUpProcess <= mupMax)
								stopCartoom();
						}else{
							if (mCurUpProcess >= 0)
								stopCartoom();
						}
					}
					break;
					default:break;
				}
			}
		};
		mBCartoom = false;
		mTimer = new Timer();
		mTimerInterval = 10;
		mCurUpProcess = 0;
		mCurDownProcess = 0;
	}
	public void setMaxSize(int upMax,int downMax)
	{
		mupMax = upMax;
		mdownMax = downMax;
	}
	public synchronized void  startCartoom(int time,boolean isup)
	{
		if (time <= 0 || mBCartoom == true)
		{
			return ;
		}
		timeMil = 0;
		mBCartoom = true;
		misup = isup;
		if(misup){
			mCurUpProcess = 0;
			mCurDownProcess = 0;
		}else{
			mCurUpProcess = mupMax;
			mCurDownProcess = mdownMax;
		}
		mTimerTask = new MyTimerTask();
		mTimer.schedule(mTimerTask, mTimerInterval, mTimerInterval);
	}
	
	public synchronized void  stopCartoom()
	{
		if (mBCartoom == false)
			return ;
		mBCartoom = false;
		if(misup)
			mAnimationImageView.updateprogress(mupMax,mdownMax);
		else{
			mAnimationImageView.updateprogress(0,0);
			mAnimationImageView.closewindow();
		}
		if (mTimerTask != null)
		{
			mTimerTask.cancel();
			mTimerTask = null;
		}
	}
	private final static int TIMER_ID = 0x0010;
	class MyTimerTask extends TimerTask{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message msg = mHandler.obtainMessage(TIMER_ID);
			msg.sendToTarget();
		}
	}
}
