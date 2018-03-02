package com.lenovo.launcher2.commonui;

import com.lenovo.launcher.R;

import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LeAlertDialog extends LeDialog {
   
    public LeAlertDialog(Context context, int theme) {
        super(context, theme);
    }

    public void setLeMessage(CharSequence message) {
    	mMessageView.setVisibility(View.VISIBLE);
        mMessageView.setText(message);
    }
    public void setLeMessage(int messageId) {
    	mMessageView.setVisibility(View.VISIBLE);
        mMessageView.setText(messageId);
    }

    @Override
    protected void setButtonListener(Button btn, final DialogInterface.OnClickListener listener,
            final int which) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (listener != null) {
                    listener.onClick(LeAlertDialog.this, which);
                }
            }

        });
    }
}
