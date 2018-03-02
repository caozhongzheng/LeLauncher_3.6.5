package com.lenovo.lejingpin;

import com.lenovo.launcher.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ProgressBar;

public class MyProgressBar extends ProgressBar{

	final static int STATUS_DOWNLOAD = -1;
	final static int STATUS_APPLY = 0;
	final static int STATUS_CANCEL= 1;
	final static int STATUS_INSTALL = 2;
	int mProgress;
	String mProgressText;
	String mDownloadStatusText;
	TextPaint mPaint;
	int mStatus;
	Context mContext;
    public MyProgressBar(Context context) {
    	super(context);
    	// TODO Auto-generated constructor stub
    	mContext = context;
    	initText();
	}
	public MyProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mContext = context;
		initText();
	}
	public MyProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context;
		initText();
	}
	@Override
	public synchronized void setProgress(int progress) {
		// TODO Auto-generated method stub
		mProgress = progress;
		Log.e("mohl","==== myprogress setProgress:"+mProgress);
		setText(progress);
		super.setProgress(progress);
	}
	@Override
	protected synchronized void onDraw(Canvas canvas) {
	   // TODO Auto-generated method stub
	   super.onDraw(canvas);
           if(mDownloadStatusText == null) return;
	   Log.e("mohl","==== onDraw:"+getDownloadStatus()+", mDownloadStatusText = "+mDownloadStatusText);
	  /* if(STATUS_CANCEL == getDownloadStatus()){
		 /*  Rect rect = new Rect();
		   mPaint.getTextBounds(mDownloadStatusText, 0, mDownloadStatusText.length(), rect);
		   Rect progressRect = new Rect();
		   mPaint.getTextBounds(mProgressText, 0, mProgressText.length(), progressRect);
		   int toppadding = (getHeight() - 2*rect.centerY() - 2*progressRect.centerY())/3;
		   int x = (getWidth() / 2) - rect.centerX();
		   int y = toppadding;
		   canvas.drawText(mDownloadStatusText, x, y, mPaint); 
		   int x1 = (getWidth() / 2) - progressRect.centerX();
		   int y1 = getHeight() - toppadding - 2*progressRect.centerY();
		   canvas.drawText(mProgressText, x1, y1, mPaint);
		   Log.e("mohl","==== onDraw: text height = "+rect.height()+", progress height = "+progressRect.height()+
				   ", x = "+x+", y = "+y+", x1 = "+x1+", y1 = "+y1);
		   * /
		   String text = mDownloadStatusText + " " + mProgressText;
		   Rect rect = new Rect();
		   mPaint.getTextBounds(text, 0, text.length(), rect);
		   int x = (getWidth() / 2) - rect.centerX();
		   int y = (getHeight() / 2) - rect.centerY();;
		   canvas.drawText(text, x, y, mPaint); 
	   }else*/{
		   Rect rect = new Rect();
		   mPaint.getTextBounds(mDownloadStatusText, 0, mDownloadStatusText.length(), rect);
		   int x = (getWidth() / 2) - rect.centerX();
		   int y = (getHeight() / 2) - rect.centerY();
		   canvas.drawText(mDownloadStatusText, x, y, mPaint); 
	   }
	   
	}
	

	private synchronized void initText(){

//        int textsize =   mContext.getResources().getDimensionPixelSize(R.dimen.indicator_text_size);
		int textsize =   mContext.getResources().getDimensionPixelSize(R.dimen.shortcut_dialog_button_textsize);
		mPaint = new TextPaint();
		mPaint.setTextSize(textsize);
		mPaint.setColor(Color.BLACK);
		mStatus = STATUS_DOWNLOAD;
	}


	private void setText(int progress){
		int i = (progress * 100)/this.getMax();
		mProgressText = String.valueOf(i) + "%";
	}
	
	public synchronized void setDownloadStatus(int status){
		Log.e("mohl","=== myprogressbar setDownloadStatus: " + status);
		mStatus = status;
		switch(status){
		case STATUS_DOWNLOAD:
			mDownloadStatusText = mContext.getResources().getString(R.string.download_download);
			break;
		case STATUS_APPLY:
			mDownloadStatusText = mContext.getResources().getString(R.string.data_apply);
			break;
		case STATUS_CANCEL:
			mDownloadStatusText = mContext.getResources().getString(R.string.btn_cancel);
			break;
		case STATUS_INSTALL:
			mDownloadStatusText = mContext.getResources().getString(R.string.le_download_install);
			break;
		default:
			mDownloadStatusText = mContext.getResources().getString(R.string.download_download);
			break;
		}
	}
	
	public synchronized int getDownloadStatus(){
		return mStatus;
	}
}
