package com.lenovo.lejingpin.settings;


import java.io.File;
import java.util.List;
import java.util.Random;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.commonui.LeAlertDialog;
import com.lenovo.launcher2.commonui.LeProcessDialog;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.Utilities;
import com.lenovo.launcher2.settings.BaseSettingActivty;


import android.app.ActionBar;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnKeyListener;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LejingpingLockScreenSettings extends BaseSettingActivty implements OnPreferenceChangeListener {
	private Context mContext;
	private SharedPreferences mSharedPreferences;
	
	private static final String TAG = "LejingpingSettingsLock";
	private static final int MSG_SHOW_CLEAR_CACHE_DIALOG = 1 ;
	
	private static final int MSG_AKEY_INSTALL_SHOW_FAIL =2 ;
	
	private static final int MSG_REMOVE_PROGRESSDIALOG = 3;
		
	private static final int SHOW_PROGRESS_DIALOG = 4;
	
	private static final String LOCK_SETTING_PACKAGE_NAME = "lock_setting_package_name";

	private Toast toast = null;
	private View toastView = null;
	
	private List<ResolveInfo> mLockAPKList;
	
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(arg0);
		Log.i(TAG, "----------oncreate");
		addPreferencesFromResource(R.xml.lejingpin_settings_lockscreen);		
		setLejingpinSettingsPreferences();		
//		ActionBar actionBar = getActionBar();
//                actionBar.setDisplayOptions(  ActionBar.DISPLAY_USE_LOGO | ActionBar.DISPLAY_SHOW_HOME |  ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);

		title.setText(R.string.pref_lock_settings_title);
		icon.setImageResource(R.drawable.dialog_title_back_arrow);
		
		findJinglingAppList();
		
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		setLejingpinSettingsPreferences();
		super.onResume();
	}
	
	
	private void setLejingpinSettingsPreferences(){
		mContext = this;
		findJinglingAppList();	
		
		 Preference mGoLocalLock = findPreference(LejingpingSettingsValues.KEY_GO_LOCALLOCK);
		 mGoLocalLock.setOnPreferenceClickListener(new OnPreferenceClickListener() {			
				@Override
				public boolean onPreferenceClick(Preference arg0) {
					// TODO Auto-generated method stub
					startLocalLockFragment();
					return false;
				}
			});
		
		
		
        CheckBoxPreference useJinglingLock = (CheckBoxPreference) findPreference(LejingpingSettingsValues.KEY_LOCK_USE);
        if(mLockAPKList!=null && mLockAPKList.size()!=0){        
        	useJinglingLock.setChecked(LejingpingSettingsValues.useJinglingLockValue(this));
        }else{
        	useJinglingLock.setChecked(false);
        }
        useJinglingLock.setOnPreferenceChangeListener(this);
        
        
        CheckBoxPreference LockShake = (CheckBoxPreference) findPreference(LejingpingSettingsValues.KEY_LOCK_SHAKE);
        if(mLockAPKList!=null && mLockAPKList.size()!=0){
        	
        	LockShake.setChecked(LejingpingSettingsValues.LockShakeValue(this));
        }else{
        	LockShake.setChecked(false);
        }
        LockShake.setOnPreferenceChangeListener(this);
        
        
        CheckBoxPreference LockVoice = (CheckBoxPreference) findPreference(LejingpingSettingsValues.KEY_LOCK_VOICE);
        if(mLockAPKList!=null && mLockAPKList.size()!=0){
        	LockVoice.setChecked(LejingpingSettingsValues.LockVoiceValue(this));
        }else{
        	LockVoice.setChecked(false);
        }
        LockVoice.setOnPreferenceChangeListener(this);
        
        
		
	}
	
	
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
        if (LejingpingSettingsValues.KEY_LOCK_USE.equals(preference.getKey())) {
            boolean useJinglingLock = (Boolean) newValue;
            if(mLockAPKList!=null && mLockAPKList.size()!=0){
            	if(useJinglingLock){
            		
            		
            		String mLockAppPackageName = Settings.System.getString(getContentResolver(),LOCK_SETTING_PACKAGE_NAME );
            		Log.i("yangmao_lock", "before mLockAppPackageName is:"+ mLockAppPackageName);
            		if(mLockAppPackageName==null || mLockAppPackageName.equals("")){
            			Log.i("yangmao_lock", "get packagename  from system is null");
            			mLockAppPackageName = mLockAPKList.get(0).activityInfo.packageName;
            			Log.i("yangmao_lock", "after mLockAppPackageName is:"+ mLockAppPackageName);
            		}
            		
            		startJLLock(mLockAppPackageName);
            		
        			((CheckBoxPreference)preference).setChecked(useJinglingLock);
    				Settings.System.putInt(getContentResolver(), "isLockOpen",1);
        			
    				setLockShakeTrue();    				
    				setLockVoiceTrue();
    				
    				return true;
        		}else{
        			((CheckBoxPreference)preference).setChecked(useJinglingLock);
        			
        			setLockShakeFalse();
        			setLockVoiceFalse();
        			
            		Settings.System.putInt(getContentResolver(), "isLockOpen",0);
            		stopJLLock();
            		return false;
        		}   		
        	}else{       		
        		showUseJinglingLockDialog();
        		return false;
        	}
            
            
			
        }
        
       
        
        else if (LejingpingSettingsValues.KEY_LOCK_SHAKE.equals(preference.getKey())) {
        	 boolean isLockShake = (Boolean) newValue;

        	 
             if(mLockAPKList!=null && mLockAPKList.size()!=0){
             	if(isLockShake){       		
         			((CheckBoxPreference)preference).setChecked(isLockShake);
         			 Settings.System.putInt(getContentResolver(), "isOpenVibrate",1);
     				return true;
         		}else{
         			((CheckBoxPreference)preference).setChecked(isLockShake);
             		
         			Settings.System.putInt(getContentResolver(), "isOpenVibrate",0);
             		return false;
         		}   		
         	}else{
         		showUseJinglingLockDialog();
         		return false;
         	}
        	       
        }
        
        else if (LejingpingSettingsValues.KEY_LOCK_VOICE.equals(preference.getKey())) {
        	
	       	boolean isLockVoice = (Boolean) newValue;
	       	
	       	
            if(mLockAPKList!=null && mLockAPKList.size()!=0){
             	if(isLockVoice){       		
         			((CheckBoxPreference)preference).setChecked(isLockVoice);
         			Settings.System.putInt(getContentResolver(), "isOpenSound",1);
     				return true;
         		}else{
         			((CheckBoxPreference)preference).setChecked(isLockVoice);
             		
         			Settings.System.putInt(getContentResolver(), "isOpenSound",0);
             		return false;
         		}   		
         	}else{
         		showUseJinglingLockDialog();
         		return false;
         	}
	       	
        }
        
        
		
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
		

	
//	private void toastShow(String text) {
//		if (toast == null) {
//			toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
//			toastView = toast.getView();
//		} else {
//			if (toastView != null) {
//				toast.setText(text);
//				toast.setDuration(Toast.LENGTH_SHORT);
//				toast.setView(toastView);
//			}
//		}
//		toast.show();
//	}
    
	
	
	

	
	
    private void showUseJinglingLockDialog(){
    	LeAlertDialog mAlertDialog = new LeAlertDialog(mContext,
				R.style.Theme_LeLauncher_Dialog_Shortcut);
		mAlertDialog.setLeTitle(R.string.lejingpin_settings_uselejingpinlock_dialog_title);
		mAlertDialog.setLeMessage(R.string.lejingpin_settings_uselejingpinlock_dialog_content);
		mAlertDialog.setOnKeyListener(new OnKeyListener() {
              @Override
              public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                  if (keyCode == KeyEvent.KEYCODE_BACK) {
                      dialog.cancel();
                  }
                  return false;
              }
          });
		
		
		mAlertDialog.setLePositiveButton(
				mContext.getString(R.string.dialog_confirm),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
		                  //TODO
						startNetLockFragment();
					}
				});
		
		mAlertDialog.setLeNegativeButton(
				mContext.getString(R.string.dialog_cancle),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						dialog.cancel();
		                  //TODO
					}
				});
		
		
		mAlertDialog.show();
    }
	
	
    private void findJinglingAppList() {
		final Intent bmainIntent = new Intent(Intent.ACTION_MAIN);
		bmainIntent.addCategory("android.service.famelock");
		if(mLockAPKList!=null && mLockAPKList.size()!=0){
			mLockAPKList.clear();
		}
		mLockAPKList = getPackageManager().queryIntentActivities(
				bmainIntent, 0);
	}
    
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	Log.i(TAG, "----------ondestroy");
    	if(mLockAPKList!=null){
    		mLockAPKList.clear();
    	}
    	
    	super.onDestroy();
    }
    
    
    
    private void setLockShakeTrue(){
    	
    	CheckBoxPreference LockShake = (CheckBoxPreference) findPreference(LejingpingSettingsValues.KEY_LOCK_SHAKE);
        if(mLockAPKList!=null && mLockAPKList.size()!=0){
        	
        	LockShake.setChecked(LejingpingSettingsValues.LockShakeValue(this));
        }else{
        	LockShake.setChecked(false);
        }
    	
    }
    
    private void setLockShakeFalse(){
		CheckBoxPreference LockShake = (CheckBoxPreference) findPreference(LejingpingSettingsValues.KEY_LOCK_SHAKE);
		
		LockShake.setChecked(false);
    }
    
    
    
    private void setLockVoiceTrue(){
        CheckBoxPreference LockVoice = (CheckBoxPreference) findPreference(LejingpingSettingsValues.KEY_LOCK_VOICE);
        if(mLockAPKList!=null && mLockAPKList.size()!=0){
        	LockVoice.setChecked(LejingpingSettingsValues.LockVoiceValue(this));
        }else{
        	LockVoice.setChecked(false);
        }
    }
    
    private void setLockVoiceFalse(){
        CheckBoxPreference LockVoice = (CheckBoxPreference) findPreference(LejingpingSettingsValues.KEY_LOCK_VOICE);
       
        LockVoice.setChecked(false);
    }
    
    
    private void startNetLockFragment(){
        Intent shortcutIntent = new Intent(SettingsValue.ACTION_LETHEME_LAUNCH);
        shortcutIntent.putExtra("EXTRA",13);
        startActivity(shortcutIntent);
    }	
    
    private void startLocalLockFragment(){
        Intent shortcutIntent = new Intent(SettingsValue.ACTION_LETHEME_LAUNCH);
        shortcutIntent.putExtra("EXTRA",3);
        startActivity(shortcutIntent);
    }	
    
    
    
    private void startJLLock(String mPackageName){
    	Log.i(TAG,"send start broadcast:"+mPackageName);
    	Intent startI = new Intent("com.qigame.lock.start");
    	//test by dining 2013-06-27 this intent for qigame lock,
    	//the extra string is 'com.lenovo.launcher' ??
    	startI.putExtra("pack_sender", "com.lenovo.launcher");
    	startI.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
    	startI.putExtra("pack_receiver", mPackageName);
    	sendBroadcast(startI);
    }
    
    
	private void stopJLLock() {
		Intent endI = new Intent("com.qigame.lock.exit");
    	endI.putExtra("pack", getApplicationInfo().packageName);
    	//私钥取当前时间
    	String prikey = System.currentTimeMillis()+"";
    	//公钥约定值
    	String pubkey = "com.qigame.lock";
    	endI.putExtra("prikey", prikey);
    	endI.putExtra("pubkey", pubkey);
    	//获取系统IMEI做为明文
    	EnCode ec = new EnCode();
    	String imei = ec.getIMEI(getApplicationContext());
    	Log.i("lock","prikey:"+prikey);
    	Log.i("lock","pubkey:"+pubkey);
    	Log.i("lock","原文:"+imei);
    	//加密明文
    	imei = ec.encode(imei, pubkey, prikey);
    	endI.putExtra("imei", imei);
    	sendBroadcast(endI);
	}
    
    
    
    class EnCode {

   	 
    	/**
    	 * 获取IMEI的统一方法
    	 * 
    	 * @param context
    	 * @return
    	 */
    	public String getIMEI(Context context) {
    		String IMSI = "";
    		try {
    			TelephonyManager tm = (TelephonyManager) (context
    					.getSystemService(Context.TELEPHONY_SERVICE));
    			IMSI = tm.getDeviceId();

    		} catch (Exception e) {
    		}
    		return IMSI;
    	}
    	
    	private char[] replaceByte(char[] data, byte l) {
    		int len = data.length;
    		int m = len >>1;
    		if(m==l || l==0)return data;
    		char tp;
    		int c = 0;
    		for (; c+l < m; ) {
    			tp = data[c];
    			int ep = len -c-1;
    			data[c] = data[ep];
    			data[ep] = tp;
    			c += l ;
    		}
    		return data;
    	}
    	
    	 
    	private byte[] replaceByte(byte[] data, byte l) {
    		
    		int len = data.length;
    		int m = len >>1;
    		if(m==l || l==0)return data;
    		byte tp;
    		int c = 0;
    		for (; c+l < m; ) {
    			tp = data[c];
    			int ep = len -c-1;
    			data[c] = data[ep];
    			data[ep] = tp;
    			c += l ;
    		}
    		return data;
    	}

    	 
    	private byte[] replaceByte(byte[] data, int l) {
    		if(l==0)return data;
    		int len = data.length;
    		int n = len / l;
    		int m = n >>1;
    		byte tp;
    		int c = 0;
    		for (int i = 0; i < m; ++i) {
    			tp = data[c];
    			data[c] = data[c + l];
    			data[c + l] = tp;
    			c += (l<<1);
    		}
    		return data;
    	}
    	
    	 
    	private char[] replaceByte(char[] data, int l) {
    		int len = data.length;
    		int n = len / l;
    		int m = n >>1;
    		char tp;
    		int c = 0;
    		for (int i = 0; i < m; ++i) {
    			tp = data[c];
    			data[c] = data[c + l];
    			data[c + l] = tp;
    			c += (l<<1);
    		}
    		return data;
    	}
    		
    	
    	
    	private boolean isNumber(String numbStr) {
    		if (numbStr == null || numbStr == "")
    			return false;
    		int n = numbStr.toCharArray().length;
    		for (int i = 0; i < n; ++i) {
    			char ch = numbStr.charAt(i);
    			if ((ch < '0' || ch > '9')) {
    				return false;
    			}
    		}
    		return true;
    	}
    	
    	private int toInt(String strParam) {
    		try {
    			if (strParam!=null && strParam!="" && isNumber(strParam))
    				return Integer.parseInt(strParam);
    			else{
    				if(strParam == null){
    					strParam = "";
    				}
    				int v=0;
    				char[] chs = strParam.toCharArray();
    				for(char ch:chs)
    				{
    					v+=ch;
    					if(v>99999999){
    						v=99999999>>2;
    					}
    				}
    				return v;
    			}
    				
    		} catch (Exception e) {
    			return 0;
    		}
    	}
    	
    	private  int getLockKey(String key)
    	{
    		int ch=0;
    		for(int i=0;i<key.length();++i)
    		{
    			ch+=key.charAt(i); 
    			if(ch>9999999)ch=9999999>>3;
    		} 
    		ch=(ch%0xF)+(ch%(0x7F-0x2F))+0xF;
    		
    		 
    		return ch;
    	}
    	private byte[] encode(byte[] data,boolean longencode, String pubkey,String prikey) {
    		int k=getLockKey(pubkey+prikey);
    		int l=(toInt(prikey)+k)%prikey.length();
    		if(l<=1)l=2;
    		try {
    			data=replaceByte(data, l); 
    			int sz = data.length; 
    			if(longencode){
    				byte data2[] = new byte[sz<<1]; 
    				int c=0; 
    				for(int i=0;i<sz;++i)
    				{  
    					int d=data[i]+k;
    					
    					data2[c]=(byte)(d>>1);
    					data2[c+1]=(byte)(d-data2[c]);
    					if((c+2)%2==1){
    						data2[c]^=(k)& 0xff;
    				
    					}else{
    						data2[c+1]^=(k)& 0xff;
    					}
    					c+=2;
    				}
    				data = null;
    				sz = data2.length;
    				byte r = (byte)((k+l)%(sz%10+1)+1);
    				data2=replaceByte(data2, r);
    				
    				int rl = (toInt(prikey)%5)+1;
    				data2=replaceByte(data2, rl);
    				
    				r = (byte)((k)%(sz%5+1)+1);
    				data2=replaceByte(data2, r);
    				
    				return data2;
    			}else{
    				int key = (k/2+l);
    				for(int i=0;i<sz;++i)
    				{  
    					int d=data[i]+k;
    					d^=key;
    					data[i]=(byte) (d & 0xff);
    				}
    				byte r = (byte)((k+l)%(sz%10+1)+1); 
    				data=replaceByte(data, r);
    				
    				int rl = (toInt(prikey)%5)+1;
    				data=replaceByte(data, rl);
    				
    				r = (byte)((k)%(sz%5+1)+1);
    				data=replaceByte(data, r);
    				return data;
    			}
    			
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} 
    		
    		return null;
    	}
    	
    	/**
    	 * 请用此方法加密内容
    	 * @param encryptxt 待加密的明文
    	 * @param pubkey 加密公钥
    	 * @param prikey 加密私钥
    	 * @return
    	 */
    	public String encode(String encryptxt,String pubkey,String prikey){
    		try {
    			byte [] data2 = encode(encryptxt.getBytes("UTF-8"),true,pubkey,prikey);
    			int sz = data2.length;
    			StringBuffer sb = new StringBuffer();
    			int c = 0;
    			String s=Integer.toHexString(sz).toUpperCase();
    			int l = s.length();
    			l=4-l;
    			while(l-->0){
    				sb.append("0");
    			}
    			sb.append(s);
    			//String t="";
    			while(c<sz)
    			{
    				int k=0;
    				if(c+4<=sz){
    					k=((data2[c]<<24)&0xFF000000)
    					+((data2[c+1]<<16)&0x00FF0000)
    					+((data2[c+2]<<8)&0x0000FF00)
    					+(data2[c+3]&0x000000FF);
    					//t+=data2[c]+","+data2[c+1]+","+data2[c+2]+","+data2[c+3]+",";
    					c+=4; 
    				}else if(c+3<=sz){
    					k=((data2[c]<<16)&0x00FF0000)
    						+((data2[c+1]<<8)&0x0000FF00)
    						+(data2[c+2]&0x000000FF);
    					c+=3;
    				}
    				else if(c+2<=sz){
    					k=((data2[c]<<8)&0x0000FF00)
    						+(data2[c+1]&0x000000FF);
    					c+=2;
    				}else if(c+1<=sz){
    					k=(data2[c]&0x000000FF);
    					c+=1;
    				}
    				s=Integer.toHexString(k).toUpperCase();
    				l = s.length();
    				l=8-l;
    				while(c<sz&&l-->0){
    					sb.append("0");
    				}
    				sb.append(s);
    			}
    			data2 = null;
    			//return data2;
    			//Trace.e("lock", "加密 d sz "+sz+",len "+sb.length());
    			//Trace.e("lock", "加密内容: t "+t);
    			Random rd = new Random(System.currentTimeMillis());
    			int r=(Math.abs(rd.nextInt())%9+1);
    			char[] chs = sb.toString().toCharArray();
    			sb = null;
    			chs = replaceByte(chs, r);
    			chs = replaceByte(chs, (byte)r);
    			sb = new StringBuffer();
    			sb.append(r);
    			for(char ch:chs)
    			{
    				sb.append(ch);
    			} 
    			return  sb.toString();
    			//return new String(data2,charset);
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    			return encryptxt;
    		}
    	}
    }
    
    
    
    
    
    
    
    
    
    

}
