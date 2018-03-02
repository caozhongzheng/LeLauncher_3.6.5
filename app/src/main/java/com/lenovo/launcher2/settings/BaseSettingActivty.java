/*
 * Copyright (C) 2011
 *
 * AUT: liuli liuli0120@use.com.cn DATE: 2011-12-20
 * add activity for theme settings
 */

package com.lenovo.launcher2.settings;

import android.app.ActionBar;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.Utilities;


@SuppressWarnings("deprecation")
public class BaseSettingActivty extends PreferenceActivity implements OnPreferenceChangeListener {
	private ActionBar mActionBar;
    private String version = Build.VERSION.RELEASE;
    private Context mContext;
    protected  TextView title ;
    protected ImageView icon; 
    protected boolean useDeviceDefaultTheme = true;
    
	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle icicle) {
		//test by dining 2013-07-29
		
		if(useDeviceDefaultTheme){
			getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
			setTheme(android.R.style.Theme_DeviceDefault_Light);
		}else{
			setTheme(R.style.Theme_LeLauncher_NoActionBar);
		    getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		}
		super.onCreate(icicle);
		
		if(!useDeviceDefaultTheme){
		    setContentView(R.layout.setup_pererence_layout);
		    title = (TextView) findViewById(R.id.dialog_title);
		    icon = (ImageView) findViewById(R.id.dialog_icon);
		    icon.setOnClickListener(new View.OnClickListener() {
			    @Override
			    public void onClick(View view) {
				    finish();
			    }
		    });
		}else{
			
			mActionBar = getActionBar();
			mActionBar.setDisplayOptions(    ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
			
		}
		if(!SettingsValue.isRotationEnabled(this)){
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {   
    	super.onConfigurationChanged(newConfig);
        /*Resources localResources = getResources();
        Configuration localConfiguration = getResources().getConfiguration();
        DisplayMetrics localDisplayMetrics = getResources().getDisplayMetrics();
        localResources.updateConfiguration(localConfiguration, localDisplayMetrics);
        
        // 检测屏幕的方向：纵向或横向
        Log.i("00", "=====newConfig====effectSetting="+newConfig.orientation);
		Log.i("00", "=====onConfigurationChanged====effectSetting="+localConfiguration.orientation);
		if (!Utilities.isPad) {
			if (localConfiguration.orientation== Configuration.ORIENTATION_LANDSCAPE) {
				this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}else if (localConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT) {
				this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
		}*/

        /*//检测实体键盘的状态：推出或者合上    

        if (newConfig.hardKeyboardHidden 

                == Configuration.HARDKEYBOARDHIDDEN_NO){ 

            //实体键盘处于推出状态，在此处添加额外的处理代码

        } 

        else if (newConfig.hardKeyboardHidden

                == Configuration.HARDKEYBOARDHIDDEN_YES){ 

            //实体键盘处于合上状态，在此处添加额外的处理代码

        }
*/
    }
	@Override
	public boolean onPreferenceChange(Preference arg0, Object arg1) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	   public boolean onOptionsItemSelected(MenuItem item) {
	    	
	    	
	      	if(item.getItemId() == android.R.id.home ){
	                finish();
	                return true;            
	        }   
	       return true;
	   }
}
