package com.lenovo.launcher2.shortcut.widget;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;

import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.weather.widget.utils.Debug.R2;
import com.lenovo.launcher2.weather.widget.utils.WeatherUtilites;

public class ShortCutDetailActivity extends Activity {
	private Context mcontext;
	private WindowManager mwm=null;
	private DetailsShortCutView mview ;
    private LauncherApplication mapp ;
    private int mheight =0;
    private int mwidth =0;
    private IntentReceiver mIntentReceiver ;
    private Handler mHandler = null;
    private static final int SET_RECT_SHOTCUTVIEW = 0x0001;
    private int deviceType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceType=SettingsValue.getCurrentMachineType(this);
        mcontext = ShortCutDetailActivity.this;
        WeatherUtilites.setAnimaState(this, false);
        Rect rect = this.getIntent().getSourceBounds();
        if(rect!=null){
            Log.d("a","rect.top="+rect.top);
            Log.d("a","rect.left="+rect.left);
        }else{
            Log.d("a","rect null");
        }
        if(mapp==null)
            mapp = (LauncherApplication)getApplicationContext();
        initHandler(this);
        registerShortcutIntentReceiver();
        new Thread(){
			@Override
            public void run() {
				mHandler.removeMessages(SET_RECT_SHOTCUTVIEW);
				mHandler.sendEmptyMessage(SET_RECT_SHOTCUTVIEW);
			}
		}.start();
    }
    private void initHandler(Context context) {
		// TODO Auto-generated method stub
		mHandler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch(msg.what){
				case SET_RECT_SHOTCUTVIEW:
//					startShurtcutDetails(getIntent().getSourceBounds());
					final int height = getResources().getDisplayMetrics().heightPixels;
			        final int width = getResources().getDisplayMetrics().widthPixels;
			        Log.i("T3", "setrectview");
					mview = new DetailsShortCutView(mcontext);
					if (deviceType==0) {
						mview.setRect7(getIntent().getSourceBounds(), height, width,mapp,deviceType);
					}else if(deviceType==1){
						mview.setRect1(getIntent().getSourceBounds(), height, width,mapp,deviceType);
					}
					else{
					  mview.setRect(getIntent().getSourceBounds(), height, width,mapp,deviceType);
					}
					((Activity) mcontext).setContentView(mview);
	    			break;
				default:
	    			break;
				}
			}
			
		};
	}
	/*private void startShurtcutDetails(final Rect rect)
	{
		new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				final int height = getResources().getDisplayMetrics().heightPixels;
		        final int width = getResources().getDisplayMetrics().widthPixels;
		        Log.i("T3", "setrectview");
				mview = new DetailsShortCutView(mcontext);
				mview.setRect(rect, height, width,mapp);
				((Activity) mcontext).setContentView(mview);
			}
			
		}).start();
	}*/
    private void startShortcutIntent(Intent intent)
    {
        int type = intent.getIntExtra("shortcut_type", 0);
        R2.echo("WidgetService !!!!type " + type);
        switch(type){
        case 1:
            Intent i1 = new Intent("android.settings.DISPLAY_SETTINGS");
            i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(i1);
            }catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
            break;
        case 2:
            boolean res = WeatherUtilites.setOnClickListenerIntent(this,"com.lenovomobile.deskclock");
            if (!res) {
                if(!WeatherUtilites.setOnClickListenerIntent(this ,"com.android.deskclock")) {
                    if(!WeatherUtilites.setOnClickListenerIntent(this, "com.ontim.clock")){
                        if(!WeatherUtilites.setOnClickListenerIntent(this ,"com.lenovo.deskclock")){
                            Intent i = new Intent("android.settings.DATE_SETTINGS");
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            try {
                                startActivity(i);
                            }catch (ActivityNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            break;
        case 3:
            Intent i3 = new Intent();
            if(!WeatherUtilites.findForPackage(this, "com.lenovo.safe.powercenter")){
                i3.setAction("android.settings.SECURITY_SETTINGS");
            }else{
                i3.setComponent(new ComponentName("com.lenovo.safe.powercenter", "com.lenovo.safe.powercenter.ui.SplashActivity"));

                i3.setAction(Intent.ACTION_MAIN);
                i3.addCategory(Intent.CATEGORY_LAUNCHER);
            }
            i3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(i3);
            }catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
            break;
        case 4:
            Intent i4 = new Intent("android.settings.APPLICATION_SETTINGS");
            i4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(i4);
            }catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
            break;
        case 5:
            Intent i5 = new Intent("com.android.settings.SOUND_SETTINGS");
            i5.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(i5);
            }catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
            break;
        default:
            break;
        }
        finish();
    }
    public void registerShortcutIntentReceiver()
    {   
        mIntentReceiver = new IntentReceiver();
        IntentFilter eventFilter = new IntentFilter();
        eventFilter.addAction(WeatherUtilites.ACTION_SHORTCUT_WIDGET_SEVICE_CLOSE);
        eventFilter.addAction(WeatherUtilites.ACTION_WIDGET_SEVICE_CLOSE);
        registerReceiver(mIntentReceiver, eventFilter);
    }
    private class IntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent==null) 
                return ;
            String action = intent.getAction();
            R2.echo("ShortCutService !!!!recievied " + action);
            if(action.equals(WeatherUtilites.ACTION_SHORTCUT_WIDGET_SEVICE_CLOSE)){
                R2.echo("WidgetService !!!! " + action);
                startShortcutIntent(intent);
            }else if(action.equals(WeatherUtilites.ACTION_WIDGET_SEVICE_CLOSE)){
                finish();
             }
        }
    };
    @Override
    protected void onPause() {
        finish();
        super.onPause();
    }
    @Override
    public void onDestroy()
    {
        try {
            if(mIntentReceiver!=null) {
                unregisterReceiver(mIntentReceiver);
            }
        } catch (IllegalArgumentException e) { 
            e.printStackTrace();
        } 
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }
}
