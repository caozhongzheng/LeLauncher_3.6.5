package com.lenovo.launcher2.customizer;

import java.io.Serializable;
import java.util.HashMap;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

/* RK_ID: RK_LELAUNCHER_REAPER. AUT: zhangdxa DATE: 2013-01-11 */
public class ReaperReceiver extends BroadcastReceiver {

	private final static String TAG = "Reaper";
	
	private static boolean bReaperInitTransferData = false;

	/* If SettingsValue.PREF_REAPER is true ,then it must reaper initialize.
	 * If SettingsValue.PREF_REAPER is false, then it will reaper initialize 
	 *     when it has on of the three following conditions;
	 *   one condition: WIFI network is connected or network enabler of launcher is enabled 
	 *     when launcher created;
	 *   two condition: when the network broadcast received 
	 *     when launcher not reaper initialized;
	 *   three condition: when the network enabler of launcher has been changed enabled 
	 *     when launcher not reaper initialized.
	 */
	
	/*
	 *Reaper initialize need calling:
	 *  Reaper.reaperOn( context );(Reaper initialize configuration)
	 *  Reaper.scheduleReaperInit(context);  
	 *  (  Because Reaper.reaperOn need some time to configure, 
	 *     so usually this function can't transfer data to server successfully. 
	 *     So if must execute this function to transfer data to server.)
	 *  Reaper.scheduleReaperInitAgain(context);
	 *  (  It set a timer to raper initialize again. And it usually is at 8pm next day.)
	 */
	@Override	
	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();
		Log.i("Reaper","***************ReaperReceiver.onReceiver(), action:"+ action );
		SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		boolean bTagReaper = mSharedPreferences.getBoolean(SettingsValue.PREF_REAPER, true);

		if( bTagReaper && action.equals(Reaper.ACTION_REAPER)){
			if( !Reaper.bReaperInitForce ){
				Reaper.setReaperInitForce(true);
				Reaper.reaperOn( context );
			}
				
			String category = intent.getStringExtra("category");
			String act = intent.getStringExtra("action");
			String label = intent.getStringExtra("label");
			int value = intent.getIntExtra("value", Reaper.REAPER_NO_INT_VALUE);
			Reaper.processReaperEvent(context, category, act, label, value);
			
			
		}else if( bTagReaper && action.equals(Reaper.ACTION_REAPER_MAP)){
			if( !Reaper.bReaperInitForce ){
				Reaper.setReaperInitForce(true);
				Reaper.reaperOn( context );
			}
			   
			String category = intent.getStringExtra("category");
			String act = intent.getStringExtra("action");
			Serializable extra =intent.getSerializableExtra("map");
			HashMap<String,String> map = (HashMap<String,String>)extra;
			int value = intent.getIntExtra("value", Reaper.REAPER_NO_INT_VALUE);
			Reaper.processReaperEventMap(context, category, act, map, value);
			
		}
		else if( action.equals(Reaper.ACTION_REAPER_INIT) ||
				 action.equals(Reaper.ACTION_REAPER_INIT_AGAIN)){
			if( Reaper.ISNetworkAvailable(context)){
	    		Log.i("Reaper","***ReaperInit, call Reaper init.....");
	    		setReaperInitTransferData(true);
        	    Reaper.scheduleReaperInitAgain(context);
        	    Reaper.reaperTrackInit();
        	}else{
	    		setReaperInitTransferData(false);
            }
		}else if(action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){ 
			if( bReaperInitTransferData ){
				return;
			}
			if( bTagReaper ||Reaper.bReaperInitCMCC ){
			    NetworkInfo info = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			    if (info != null && info.isConnected() ) {
			        Log.i("Reaper","***ReaperReceiver.onReceiver(), network is connectted, call Reaper init again...");
		    		setReaperInitTransferData(true);
			        Reaper.scheduleReaperInitAgain(context);
			        Reaper.reaperTrackInit();
				}
		    }else {
			    NetworkInfo info = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
		        if ((info != null )&& 
		    	    (info.isConnected()) && 
		    	    (info.getType() == ConnectivityManager.TYPE_WIFI)) {
		        	Log.i("Reaper","***ReaperReceiver.onReceiver(), network is connectted, call Reaper init ...");
		    	    Reaper.setReaperInitCMCC(true);
	           	    Reaper.reaperOn( context );
	           	    Reaper.scheduleReaperInit(context);
		        }
		    }
        }else if( !bTagReaper && action.equals( SettingsValue.ACTION_NETWORK_ENABLER_CHANGED)){
        	if( bReaperInitTransferData ){
				return;
			}
        	if( Reaper.bReaperInitCMCC){	
        		return;
        	}
        	boolean networkEnabled = intent.getBooleanExtra(SettingsValue.EXTRA_NETWORK_ENABLED, false);
        	
        	if( networkEnabled){
        		Log.i("Reaper","***ReaperReceiver.onReceiver(), SettingsValue.EXTRA_NETWORK_ENABLED true, call Reaper init...");
        		//handleSenior( MSG_REAPER_CMCC );
        		Reaper.setReaperInitCMCC(true);
            	Reaper.reaperOn( context );
            	Reaper.scheduleReaperInit(context);
        	}
        }else if ( action.equals(Reaper.ACTION_REAPER_INIT_FORCE) ||
        		   action.equals(Reaper.ACTION_REAPER_INIT_CMCC_FORCE)){
        	Log.i("Reaper","***ReaperReceiver.onReceiver(), Reaper.ACTION_REAPER_INIT_, call Reaper init...");
        	Reaper.reaperOn( context );
        	Reaper.scheduleReaperInit(context);
        }
	}

	private void setReaperInitTransferData(boolean b) {
		// TODO Auto-generated method stub
		bReaperInitTransferData = b;

	}
}
