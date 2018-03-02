package com.lenovo.launcher2.commonui;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.customizer.Utilities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.WindowManager;
import android.content.res.Resources;

public class LeDialog extends Dialog {
    protected LinearLayout mButtonbar;
    protected TextView mMessageView;
    protected TextView mTitleTextView; 
    protected Button finishBtn;
    protected Button cancelBtn;
    protected Context mContext;
    protected LinearLayout ll;
    protected LinearLayout mTitleView;
	protected ImageView mTitleIconView;
	
	protected boolean useDeviceDefaultTheme = true;
	
	public LeDialog(Context context, int theme) {
    	
    	super(context, android.R.style.Theme_DeviceDefault_Light_Dialog);
    	
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        ll = (LinearLayout) inflater.inflate(R.layout.alert_dialog_with_message, null,
                false);      

        mButtonbar = (LinearLayout) ll.findViewById(R.id.msg_btnbar);
        mButtonbar.setVisibility(View.GONE);
        
        mMessageView = (TextView) ll.findViewById(R.id.message);
        mMessageView.setVisibility(View.GONE);
        
        
            mTitleView = (LinearLayout)ll.findViewById(R.id.view_title);
            mTitleView.setVisibility(View.GONE);
        
            mTitleTextView = (TextView) mTitleView.findViewById(R.id.dialog_title);
            mTitleTextView.setVisibility(View.GONE);
        
            mTitleIconView = (ImageView) mTitleView.findViewById(R.id.dialog_icon);
        
        
        finishBtn = (Button) mButtonbar.findViewById(R.id.addfinish);
        cancelBtn = (Button) mButtonbar.findViewById(R.id.canceladd);
        finishBtn.setVisibility(View.GONE);
        cancelBtn.setVisibility(View.GONE);
        
        Window window = getWindow();
        if(!useDeviceDefaultTheme){
        	window.requestFeature(Window.FEATURE_NO_TITLE);
        }
        
        setContentView(ll);
        
        /***RK_ID:RK_SETTING_ORIENTATION_PAD AUT:zhanglz1@lenovo.com. DATE: 2013-05-16 S***/        
        
		window.setGravity(Gravity.CENTER);
        Utilities.setDialogWidth(window,context.getResources());
        
        /***RK_ID:RK_SETTING_ORIENTATION_PAD AUT:zhanglz1@lenovo.com. DATE: 2013-05-16 E***/        
		setCanceledOnTouchOutside(true);

    }
	public void setLeIcon(int ResId){
    	if(mTitleIconView != null && mTitleView!=null){
            if(ResId!=0){
            	mTitleIconView.setImageResource(ResId);
            	mTitleView.setVisibility(View.VISIBLE);
            }    		
    	}else{
    		
    	}
    }
    public void setLeIcon(Bitmap icon){
    	if(mTitleIconView != null && mTitleView!=null){
            if(icon!=null){
            	mTitleIconView.setImageBitmap(icon);
            	mTitleView.setVisibility(View.VISIBLE);
            }    		
    	}
    }
    /*RK_NEW_TITLE dining 2013-02-22 S*/
    //rewrite the function to set title string
//    public void setTitle(int ResId){
//		if (mTitleTextView != null && mTitleView!=null) {
//			mTitleView.setVisibility(View.VISIBLE);
//			mTitleTextView.setVisibility(View.VISIBLE);
//			mTitleTextView.setText(getContext().getText(ResId));
//		}else{
//			setTitle(getContext().getText(ResId));
//		}
//    }
//    public void setTitle(CharSequence message){
//    	if(mTitleTextView != null && mTitleView!=null){
//            if(message != null && message.length() > 0){
//            	mTitleView.setVisibility(View.VISIBLE);
//    			mTitleTextView.setVisibility(View.VISIBLE);
//            	mTitleTextView.setText(message);
//            }else{
//            	mTitleTextView.setVisibility(View.GONE);
//            }
//    	}else{
//    		setTitle(message);
//    	}
//    }
    /*RK_NEW_TITLE dining 2013-02-22 E*/

    public void setLeTitle(CharSequence title){
    	if(mTitleTextView != null && mTitleView!=null && !useDeviceDefaultTheme){
            if(title != null && title.length() > 0){
            	mTitleTextView.setText(title);
            	mTitleView.setVisibility(View.VISIBLE);
            	mTitleTextView.setVisibility(View.VISIBLE);
            }    		
    	}else{
			setTitle(title);
		}
    }
    public void setLeTitle(int titleId){
    	if(mTitleTextView != null && mTitleView!=null && !useDeviceDefaultTheme){
            if(titleId!=0){
            	mTitleTextView.setText(titleId);
            	mTitleView.setVisibility(View.VISIBLE);
            	mTitleTextView.setVisibility(View.VISIBLE);
            }
    	}else{
			setTitle(getContext().getText(titleId));
		}   
    }
    public void setLeContentView(View contentView){
    	ViewParent v1 = mTitleView.getParent();
    	if(v1!=null && v1 instanceof ViewGroup){
    		((ViewGroup)v1).removeView(mTitleView);
    	}
    	ViewParent v = mButtonbar.getParent();
    	if(v!=null && v instanceof ViewGroup){
    		((ViewGroup)v).removeView(mButtonbar);
    	}
    	ll.removeView(mTitleView);
    	ll.removeView(mButtonbar);
    	ll.removeAllViews();
    	ll.addView(mTitleView,0);
    	ll.addView(contentView,1);
    	ll.addView(mButtonbar,2);
     	setContentView(ll);
     	
     	if(useDeviceDefaultTheme){
     		mTitleView.setVisibility(View.GONE);
     		mTitleTextView.setVisibility(View.GONE);
     	}
    }
    public void setLeContentView(int viewId){
    	ViewParent v1 = mTitleView.getParent();
    	if(v1!=null && v1 instanceof ViewGroup){
    		((ViewGroup)v1).removeView(mTitleView);
    	}
    	ViewParent v = mButtonbar.getParent();
    	if(v!=null && v instanceof ViewGroup){
    		((ViewGroup)v).removeView(mButtonbar);
    	}
     	ll.removeView(mTitleView);
    	ll.removeView(mButtonbar);
    	ll.removeAllViews();
    	ll.addView(mTitleView,0);
    	LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	LinearLayout contentView = (LinearLayout) inflater.inflate(viewId, null,
                false);
    	ll.addView(contentView,1);
    	ll.addView(mButtonbar,2);
    	setContentView(ll);
    	
    	if(useDeviceDefaultTheme){
     		mTitleView.setVisibility(View.GONE);
     		mTitleTextView.setVisibility(View.GONE);
     	}
    }

    public void setLePositiveButton(CharSequence text,
            final DialogInterface.OnClickListener listener) {
    	mButtonbar.setVisibility(View.VISIBLE);
        finishBtn.setVisibility(View.VISIBLE);
        finishBtn.setText(text);
        setButtonListener(finishBtn, listener, DialogInterface.BUTTON_POSITIVE);
    }

    protected void setButtonListener(Button btn, final DialogInterface.OnClickListener listener,
            final int which) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  dismiss();
                if (listener != null) {
                    listener.onClick(LeDialog.this, which);
                }
            }

        });
    }

    public void setLeNegativeButton(CharSequence text,
            final DialogInterface.OnClickListener listener) {
    	mButtonbar.setVisibility(View.VISIBLE);
    	cancelBtn.setVisibility(View.VISIBLE);
        cancelBtn.setText(text);
        setButtonListener(cancelBtn, listener, DialogInterface.BUTTON_NEGATIVE);
    }
    public Button getLeNegativeButton(){
    	return cancelBtn;
    }
    public Button getLePositiveButton(){
    	return finishBtn;
    }
}
