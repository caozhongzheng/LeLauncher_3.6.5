/*
 * Copyright (C) 2011
 *
 * AUT: liuli liuli0120@use.com.cn DATE: 2011-12-20
 * add activity for theme settings
 */

package com.lenovo.launcher2.settings;

import java.util.List;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import android.preference.CheckBoxPreference;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.settings.ColorPickerDialog.OnColorCanelListener;
import com.lenovo.launcher2.settings.ColorPickerDialog.OnColorConfirmListener;

@SuppressWarnings("deprecation")
public class PersonalSettings extends BaseSettingActivty implements OnPreferenceChangeListener, ColorPickerDialog.OnColorChangedListener {
    private static final String TAG = "DesktopSettings";
	private ActionBar mActionBar;

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle icicle) {
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(icicle);

		addPreferencesFromResource(R.xml.personal_settings);
		/*mActionBar = getActionBar();
		mActionBar.setTitle(R.string.settings_pref_icon);*/
		setDesktopPreferences();
     
		title.setText(R.string.settings_pref_icon);
		icon.setImageResource(R.drawable.dialog_title_back_arrow);

	}

	protected void onDestroy(){
		super.onDestroy();
		
		if(mColorView != null){
		    mColorView.dismiss();
		    mColorView = null;
		}
			
		
	}
	private void setDesktopPreferences() {
		/* RK_ID: RK_MENU_RFACTOR . AUT: LIUYG1 . DATE: 2012-11-29 . S */
		Preference iconBgStylePref = findPreference(SettingsValue.PREF_ICON_BG_STYLE);
		iconBgStylePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(PersonalSettings.this, IconStyleSettings.class);
				PersonalSettings.this.startActivity(intent);
				/*** AUT:zhaoxy . DATE:20120228 . START***/
				//finish();
				/*** AUT:zhaoxy . DATE:20120228 . END***/
				return true;
			}
		});
		/* RK_ID: RK_MENU_RFACTOR . AUT: LIUYG1 . DATE: 2012-11-29 . E */
		
		/***RK_ID:RK_CHANGE_ICON_SIZE AUT:zhanglz1@lenovo.com. DATE:2013-01-14.S***/       
        // list preference for icon text size
        ListPreference iconSizePref = (ListPreference) findPreference(SettingsValue.PREF_ICON_SIZE_NEW);
        DisplayMetrics dm ;
        dm = getApplicationContext().getResources().getDisplayMetrics();  
		float mDeviceDensity = dm.density;
		String icon_size = String.valueOf((int) (SettingsValue.getIconSizeValueNew(this) / mDeviceDensity));
		iconSizePref.setValue(icon_size);
        iconSizePref.setSummary(iconSizePref.getEntry());
        iconSizePref.setOnPreferenceChangeListener(this);
        /***RK_ID:RK_CHANGE_ICON_SIZE AUT:zhanglz1@lenovo.com. DATE:2013-01-14.E***/
        
        Preference iconTextStylePref = findPreference(SettingsValue.PREF_ICON_TEXT_STYLE);
        iconTextStylePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
            	new ShowColorPickerTask().execute(new Void[]{});
                return true;
            }
        });

        // list preference for icon text size
        ListPreference iconTextSizePref = (ListPreference) findPreference(SettingsValue.PREF_ICON_TEXT_SIZE);
        String icon_text = SettingsValue.getIconTextSizeValue(this);
        iconTextSizePref.setValue(icon_text);
        iconTextSizePref.setSummary(iconTextSizePref.getEntry());
        iconTextSizePref.setOnPreferenceChangeListener(this);
        /** RK_ID: RK_NEWFEATURE_TEXTBACKGROUND. AUT: yumina DATE: 2012-10-18 S */
        CheckBoxPreference worktextback = (CheckBoxPreference) findPreference(SettingsValue.PREF_DESKTOP_TEXT_BACKGROUND);
        worktextback.setSummaryOn(R.string.checkbox_open);
        worktextback.setSummaryOff(R.string.checkbox_Close);
        worktextback.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue instanceof Boolean) {
                    SettingsValue.enableDesktopTextBackground((Boolean) newValue);
                    /***RK_ID:RK_MUEU_CHANGE AUT:zhanglz1@lenovo.com DATE: 2013-01-24 S***/ 
                    Intent i = new Intent(SettingsValue.ACTION_TEXT_BACKGROUND_ONOFF);
    		        i.putExtra(SettingsValue.ACTION_TEXT_BACKGROUND_ONOFF,
    		        		(Boolean) newValue);
    				sendBroadcast(i);
    				/***RK_ID:RK_MUEU_CHANGE AUT:zhanglz1@lenovo.com DATE: 2013-01-24 E***/ 
                }
		        
                return true;
            }
        });
        /** RK_ID: RK_NEWFEATURE_TEXTBACKGROUND. AUT: yumina DATE: 2012-10-18 E */
        /*RK_ID: RK_SCREEN_CELLCOUNT . AUT:liuyg1@lenovo.com DATE: 2012-11-13 START*/       
        ListPreference cellarray = (ListPreference) findPreference(SettingsValue.PREF_SCREEN_CELLCOUNT);
        String cellcount= SettingsValue.getScreenListCellArray(this);
        if(cellcount.equals("")){
        	cellcount = "4x4";
/*        	DisplayMetrics dm = new DisplayMetrics(); 
        	getWindowManager().getDefaultDisplay().getMetrics(dm); 
*/        	if(dm.heightPixels>854){
        		cellcount = "5x4"; 
        	}
        }
        cellarray.setValue(cellcount);
        cellarray.setSummary(cellarray.getEntry());
        cellarray.setOnPreferenceChangeListener(this);
        
       //move the item to the parent view
     // y count setting
        /*ListPreference yCount = (ListPreference) findPreference(SettingsValue.PREF_APP_CELLY);
        String cellY = SettingsValue.getAppListCellY(this);
        yCount.setValue(cellY);
        yCount.setSummary(yCount.getEntry());
        yCount.setOnPreferenceChangeListener(this);
        */
        
        removeNoNeedSettings();
        
        // list preference for app list edit style
//        ListPreference appsEditStylePref = (ListPreference) findPreference(SettingsValue.PREF_APPLIST_EDIT);
//        String edit_value = SettingsValue.getAppListEditValue(this);
//        appsEditStylePref.setValue(edit_value);
//        appsEditStylePref.setSummary(appsEditStylePref.getEntry());
//        appsEditStylePref.setOnPreferenceChangeListener(this);

	}
	
	@Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        if (preference.getKey().equals(SettingsValue.PREF_APPLIST_EDIT)) {
            if (newValue instanceof String) {
                String sPrefAppListEdit = (String) newValue;
				if (preference instanceof ListPreference)
					((ListPreference) preference).setValue(sPrefAppListEdit);
                preference.setSummary(((ListPreference) preference).getEntry());
                SettingsValue.setAppListEditValue(sPrefAppListEdit);
                this.sendBroadcast(new Intent(SettingsValue.ACTION_APP_EDIT_CHANGED));
            }
            return true;
        } else if(preference.getKey().equals(SettingsValue.PREF_ICON_TEXT_SIZE)) {
        	if (newValue instanceof String) {
                String sPrefIconTextSize = (String) newValue;
                /*RK_ID: RK_FIX_BUG . AUT: zhangdxa . DATE: 2013-01-29 . BUG:6651.S*/
                SettingsValue.setIconTextSizeValue(this, sPrefIconTextSize);
                /*RK_ID: RK_FIX_BUG . AUT: zhangdxa . DATE: 2013-01-29 . BUG:6651.E*/
                ((ListPreference) preference).setValue(sPrefIconTextSize);
                preference.setSummary(((ListPreference) preference).getEntry());
                this.sendBroadcast(new Intent(SettingsValue.ACTION_TEXT_SIZE_CHANGED));
            }
            return true;
            /***RK_ID:RK_CHANGE_ICON_SIZE AUT:zhanglz1@lenovo.com. DATE:2013-01-14.S***/       
        }else if(preference.getKey().equals(SettingsValue.PREF_ICON_SIZE_NEW)) {
        	if (newValue instanceof String) {
                String sPrefIconSize = (String) newValue;
                DisplayMetrics dm ;
                dm = getApplicationContext().getResources().getDisplayMetrics();  
        		float mDeviceDensity = dm.density;
        		int iconSize = Math.round((mDeviceDensity * Integer.parseInt(sPrefIconSize)));
        		if(iconSize == SettingsValue.getIconSizeValueNew(this))
                	return true;
                SettingsValue.setIconSizeValue(sPrefIconSize);
                ((ListPreference) preference).setValue(sPrefIconSize);
                preference.setSummary(((ListPreference) preference).getEntry());
                new Thread(new ChangeIconSizeRunnable()).start();
            }
            return true;
        }
        /***RK_ID:RK_CHANGE_ICON_SIZE AUT:zhanglz1@lenovo.com. DATE:2013-01-14.E***/       
        /*RK_ID: RK_SCREEN_CELLCOUNT . AUT:liuyg1@lenovo.com DATE: 2012-11-13 START*/       
        else if (preference.getKey().equals(SettingsValue.PREF_SCREEN_CELLCOUNT)) {
        	if (newValue instanceof String)
        		SettingsValue.onScreenListCellArrayPreferenceChange(preference, (String) newValue,this);
        	return true;
        }
		 /*RK_ID: RK_SCREEN_CELLCOUNT . AUT:liuyg1@lenovo.com DATE: 2012-11-13 END*/ 
        /***RK_ID:RK_MUEU_CHANGE AUT:zhanglz1@lenovo.com DATE: 2013-01-24 S***/ 
        else if (preference.getKey().equals(SettingsValue.PREF_APP_CELLY)) {
            if (newValue instanceof String)
                SettingsValue.onAppListCellYPreferenceChange(preference, (String) newValue, this);
            return true;
        }
        /***RK_ID:RK_MUEU_CHANGE AUT:zhanglz1@lenovo.com DATE: 2013-01-24 E***/ 
        return false;
    }
	
	@Override
	public void colorChanged(int color) {
	    SettingsValue.setIconTextStyleValue(color);
	}
    
    /*RK_ID: RK_PERSONAL . AUT: zhanggx1 . DATE: 2012-02-24 . S*/
    private class ShowColorPickerTask extends AsyncTask<Void, Void, Integer> {
		@Override
		protected Integer doInBackground(Void... arg0) {	
			int iconStyle = SettingsValue.getIconTextStyleValue(PersonalSettings.this);
			return iconStyle;
		}
		
		@Override
	    protected void onPostExecute(Integer result) {
			mColorView = new ColorPickerDialog(PersonalSettings.this, PersonalSettings.this, result,
                /*RK_CUSTOM_TITLE dining 2013-02-22 S*/
					R.style.Theme_LeLauncher_Dialog_Shortcut );//R.style.Theme_LeLauncher_Dialog); //
			mColorView.setOnColorConfirmListener(mColorConfirmListener);
			mColorView.setOnColorCanelListener(new OnColorCanelListener() {
				@Override
				public void colorCanel() {
					mColorView.dismiss();
					mColorView = null;
				}
			});
			mColorView.show();
		}
	}
    
    private class ChangeTextColorTask extends AsyncTask<Integer, Void, Void> {
		@Override
		protected Void doInBackground(Integer... arg0) {	
			int color = arg0[0];
			SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(PersonalSettings.this).edit();
			editor.putInt(SettingsValue.PREF_ICON_TEXT_STYLE, color);
			try {
				editor.apply();
			} catch (AbstractMethodError unused) {
				editor.commit();
			}
			SettingsValue.setIconTextStyleValue(color);
			sendBroadcast(new Intent(SettingsValue.ACTION_TEXT_SIZE_CHANGED));
			return null;
		}
		
		@Override
	    protected void onPostExecute(Void result) {
			if (mColorView != null) {
			    mColorView.dismiss();
			    mColorView = null;
			}
		}
	}
    
    private OnColorConfirmListener mColorConfirmListener = new OnColorConfirmListener() {
		@Override
		public void colorConfirm(int color) {
			new ChangeTextColorTask().execute(new Integer[]{color});			
		}
	};
	
	private ColorPickerDialog mColorView = null;
    /*RK_ID: RK_PERSONAL . AUT: zhanggx1 . DATE: 2012-02-24 . E*/
	
	private void removeNoNeedSettings(){
    	PreferenceScreen pg = (PreferenceScreen)findPreference("pref_personal_screen");
    	if(pg == null ) return;
    	/*
		//if in BU version, don't show whitelist and cellcountXY 
		if(!GlobalDefine.BU_VERSION){
			Preference tempObj = findPreference(SettingsValue.PREF_ICON_BG_STYLE);
			pg.removePreference(tempObj);
			
		}*/
		/*RK_ID: RK_SCREEN_CELLCOUNT . AUT:liuyg1@lenovo.com bugfix:172571 DATE: 2012-11-22 START*/       
    	DisplayMetrics dm = new DisplayMetrics(); 
    	getWindowManager().getDefaultDisplay().getMetrics(dm); 
    	
    	if(dm.widthPixels==480
    			/*&&(dm.heightPixels==800||dm.heightPixels==854)*/){
    		Preference tempObj = findPreference(SettingsValue.PREF_SCREEN_CELLCOUNT);
    		if(tempObj!=null)
    		pg.removePreference(tempObj);
    	}
/*RK_ID: RK_SCREEN_CELLCOUNT . AUT:liuyg1@lenovo.com DATE: 2012-11-22 END*/ 
    	
		// just for 0318
		Preference tempObjdatascreencellcount = findPreference(SettingsValue.PREF_SCREEN_CELLCOUNT);
		if (tempObjdatascreencellcount != null)
			pg.removePreference(tempObjdatascreencellcount);
    			
    }
	private class ChangeIconSizeRunnable implements Runnable {
        @Override
        public void run() {
        	Intent i = new Intent(SettingsValue.ACTION_ICON_SIZE_CHANGED);
            i.putExtra("iconSize", SettingsValue.getIconSizeValue(PersonalSettings.this));
            try {
                LauncherApplication app = (LauncherApplication) getApplicationContext();
                app.mIconCache.flush();
                sendBroadcast(i);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            	Log.i(TAG, "send intent error- ");
            }
        }
    }
	//keep it for later
    /*@Override
	protected void onResume() {
		super.onResume();
		int newtagindex = 3;
		String orderInParent = "0302";
	    SettingsValue.addorRemoveAllNewTag(this, newtagindex ,orderInParent);
	}*/
}
