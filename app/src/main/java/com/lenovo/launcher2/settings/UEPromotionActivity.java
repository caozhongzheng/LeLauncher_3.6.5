package com.lenovo.launcher2.settings;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.inputmethodservice.Keyboard.Key;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;

import com.android.internal.app.AlertActivity;
import com.lenovo.launcher.R;
import com.lenovo.launcher2.commonui.LeAlertDialog;
import com.lenovo.launcher2.customizer.SettingsValue;

//add  by xingqx for user data acqu

public class UEPromotionActivity extends Activity {
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
		mAlertDialog.setLeTitle(R.string.push_text11);
		mAlertDialog.setLeMessage(R.string.uepromotion_protocol);
		mAlertDialog.setLePositiveButton(getText(R.string.btn_ok), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mIsOkClicked = true;
                setDataAcquEnable(true);
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

    private static final String PREF_DATA_ACQU_ENABLER = "pref_data_acquisition";

    void setDataAcquEnable(final boolean flag) {

        new Thread(new Runnable() {
            public void run() {
                setSharedPreferences(flag);
            }
        }).start();

        Intent intent = new Intent(SettingsValue.ACTION_DATA_ACQU_ENABLER_CHANGED);
        intent.putExtra(SettingsValue.EXTRA_DATA_ACQU_ENABLED, flag);
        this.sendBroadcast(intent);

    }

    void setSharedPreferences(boolean flag) {
        SharedPreferences settings = getSharedPreferences(SettingsValue.PERFERENCE_NAME, MODE_APPEND
                | MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(PREF_DATA_ACQU_ENABLER, flag);
        editor.commit();
    }

    /* RK_ID: RK_STYLE. AUT: yumina . DATE: 2012-07-20 . START */
    @Override
    protected void onDestroy() {
        if (!mIsOkClicked) {
            setSharedPreferences(false);
            Intent intent = new Intent(SettingsValue.ACTION_DATA_ACQU_ENABLER_CHANGED);
            intent.putExtra(SettingsValue.EXTRA_DATA_ACQU_ENABLED, false);
            this.sendBroadcast(intent);
        }
//        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }
    /* RK_ID: RK_STYLE. AUT: yumina . DATE: 2012-07-20 . END */

}
