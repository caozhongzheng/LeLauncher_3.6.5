package com.lenovo.launcher2.settings;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.commonui.LeAlertDialog;
import com.lenovo.launcher2.customizer.SettingsValue;

//add  by LIUYG for first boot

public class LicenseContentActivity extends Activity {
    private boolean mIsOkClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        popupPromotion(this);
    }

    void popupPromotion(final Context context) {
        mIsOkClicked = false;
    	LeAlertDialog mAlertDialog = new LeAlertDialog(this,
				R.style.Theme_LeLauncher_Dialog_Shortcut);
		mAlertDialog.setLeTitle(R.string.app_license);
		mAlertDialog.setLeMessage(R.string.app_license_content);
		mAlertDialog.setLePositiveButton(getText(R.string.btn_ok), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mIsOkClicked = true;
                dialog.dismiss();
                finish();
            }

        });
		mAlertDialog.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				 if (keyCode == KeyEvent.KEYCODE_BACK) {
					 dialog.dismiss();
					 finish();
                     return true;
                 }
				return false;
			}
		});
		mAlertDialog.show();
		
//        mAlert.setButton(DialogInterface.BUTTON_NEGATIVE, getText(R.string.btn_cancel), new OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                setDataAcquEnable(false);
//            }
//
//        }, null);


    }

    
    
    /* RK_ID: RK_STYLE. AUT: yumina . DATE: 2012-07-20 . START */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    /* RK_ID: RK_STYLE. AUT: yumina . DATE: 2012-07-20 . END */

}
