package com.lenovo.launcher2.commonui;

import com.lenovo.launcher.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LeProcessDialog extends Dialog {
    protected Context mContext;
    protected LinearLayout ll;
    private TextView tMessage = null;
    
    public LeProcessDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ll = (LinearLayout) inflater.inflate(R.layout.apply_progressbar, null,
                false);
        tMessage = (TextView) ll.findViewById(R.id.progress_msg);
        Window window = getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
		window.requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(ll);
        setCancelable(false);
        
    }
	
    public LeProcessDialog(Context context) {
        this(context, R.style.Theme_LeLauncher_ProgressDialog);
                
    }
    
    public void setLeMessage(CharSequence msg){
    	if(tMessage != null ){
            if(msg != null && msg.length() > 0){
            	tMessage.setText(msg);
            	
            }    		
    	}
    }
    public void setLeMessage(int msgId){
    	if(tMessage != null ){
    		if(msgId != 0){
            	tMessage.setText(msgId);
            	
            }else{
            	tMessage.setText("");
            }
    	}
    }
}
