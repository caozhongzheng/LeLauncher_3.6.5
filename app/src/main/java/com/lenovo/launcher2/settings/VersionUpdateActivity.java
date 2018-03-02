package com.lenovo.launcher2.settings;


import java.net.URLDecoder;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.internal.app.AlertActivity;
import com.lenovo.launcher.R;
import com.lenovo.launcher2.commonui.LeAlertDialog;
import com.lenovo.launcher2.commonui.LeDialog;
import com.lenovo.launcher2.commonui.LeProcessDialog;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.VersionUpdateSUS;
import com.lenovo.lps.sus.SUS;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

/*RK_ID: RK_LELAUNCHER_VERSION_UPDATE. AUT:zhangdxa. DATE: 2012-06-29.*/
public class VersionUpdateActivity extends AlertActivity {//Activity {
	
    private static final String TAG ="VersionUpdate";
	private final static int REQUEST_CONFIRM_SENIOR_NETWORK = 1;
	
	private static final String AUTO_UPDATE_PREF = "com.lenovo.launcher.versionupdate_preferences";
	private static final String KEY_AUTO_UPDATE_TIME = "version_update_time";

	private LeProcessDialog mQueryProgressDialog;//private ProgressDialog mQueryProgressDialog;
	private QueryCompleteReceiver mQueryCompleteReceiver;

	private LeDialog myCustomDialog = null;
	private static final int MSG_UPDATE_DOWNLOAD = 100;
	
	private String mChannelKey = null;
	private String mVersionName = null;
	private String mUpdateDesc = null;
	private String mUrl = null;
	private String mFileName = null;
	private String mFileSize = null;
	private String mPackageId = null;
	private Context mContext = null;
	
    private Handler myMSGHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_UPDATE_DOWNLOAD:
            	processDownload();
                break;
            default:
				break; 
            }
        }
    };
	
	public class QueryCompleteReceiver extends BroadcastReceiver {
    	@Override
    	public void onReceive(Context context, Intent intent) {
    		final String action = intent.getAction();
    		if(VersionUpdateSUS.ACTION_QUERY_COMPLETE.equals(action)) {
    			String param = intent.getStringExtra("queryinfo");
    			int type = intent.getIntExtra("type", 0);
    			processQueryCompleteMessage(param, type);
            }else if( VersionUpdateSUS.ACTION_QUERY_ERROR.equals(action)){
            	finishWindow();
            }
    	}
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"VersionUpdateActivity.onCreate()");
        
        //close the last uncompleted session
        //SUS.finish();
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        VersionUpdateSUS.getInstance().setAutoUpdate(getIntent().getBooleanExtra("auto", false));   
        String param = getIntent().getStringExtra("version_param");
        mQueryCompleteReceiver = new QueryCompleteReceiver();
        IntentFilter queryCompleteFilter = new IntentFilter();
        queryCompleteFilter.addAction(VersionUpdateSUS.ACTION_QUERY_COMPLETE);
        queryCompleteFilter.addAction(VersionUpdateSUS.ACTION_QUERY_ERROR);
        registerReceiver(mQueryCompleteReceiver, queryCompleteFilter);
        
        if( ( "other".equals(VersionUpdateSUS.getInstance().getNetworkConnectType()) )&& 
        		( !VersionUpdateSUS.getInstance().mAutoUpdate)){
        	Toast.makeText(this, R.string.SUS_MSG_FAIL_NETWORKUNAVAILABLE, 1).show();
        	finish();
        }else if( (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED )) && 
        		  (!VersionUpdateSUS.getInstance().mAutoUpdate))	{
 			Toast.makeText(this, R.string.version_update_sd, 1).show();
        	finish();
 		} else{
 			if( !VersionUpdateSUS.getInstance().mAutoUpdate && !isNetworkEnabled()){
 				popupSeniorNetworkEnableDialog();
 			}else{
        	    initUpdate(VersionUpdateSUS.getInstance().mAutoUpdate, param);
 			}
        }
    }
 
    @Override
	protected void onDestroy() {
		Log.i(TAG,"VersionUpdateActivity.onDestroy! ");
		unregisterReceiver( mQueryCompleteReceiver );
		if( mQueryProgressDialog != null && mQueryProgressDialog.isShowing())
			mQueryProgressDialog.dismiss();
		mQueryProgressDialog = null;
		super.onDestroy();
	}    

    private void resetAutoUpdate() {
	    Long currentTime = System.currentTimeMillis();
	    SharedPreferences prefs =
            getSharedPreferences(AUTO_UPDATE_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(KEY_AUTO_UPDATE_TIME, currentTime);
        editor.commit();
    }
    
    private void initUpdate(boolean bAuto, String param) {
       Log.i(TAG,"VersionUpdateActivity.initUpdate, bAuto:"+ bAuto);
       Log.i(TAG,"VersionUpdateActivity.initUpdate--isVersionUpdateStarted:"+
           SUS.isVersionUpdateStarted());
       if( !bAuto ){
    	   if (SUS.isVersionUpdateStarted()) {
   			    Toast.makeText(this, R.string.SUS_MSG_WARNING_PENDING, 1).show();
   			    finish();
   			    return;
    	    }
        	showQueryProgressBar();
        	//VersionUpdateSUS.countTestServer = 0;
        	//SUS.testSUSServer(this); 
        	if( !VersionUpdateSUS.getInstance().getInstance().queryVersion()){
				finish();
			}
        }else {
        	processVersion(param);
        }
    }
    
    private void popupSeniorNetworkEnableDialog() {
    	Log.i(TAG,"VersionUpdateActivity.popupSeniorNetworkEnableDialog()");
    	final Context context = VersionUpdateSUS.getInstance().getContext();
    	if( context == null ) return;
    	LeAlertDialog mAlertDialog = new LeAlertDialog(this,
				R.style.Theme_LeLauncher_Dialog_Shortcut);
		mAlertDialog.setLeTitle(R.string.SUS_VERSIONUPDATE);
		mAlertDialog.setLeMessage(R.string.confirm_network_open);
		mAlertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                Toast.makeText(context, R.string.version_update_toast, 1).show();
                finish();
			}
        });
		mAlertDialog.setLeNegativeButton(
				this.getString(R.string.rename_action),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						dialog.dismiss();
		                Toast.makeText(context, R.string.version_update_toast, 1).show();
		                finish();
					}
				});
		mAlertDialog.show();
    }

	private void startConfirm() {
        Intent intent = new Intent();
        intent.setClass(this, SeniorSettings.class);
        startActivityForResult(intent, REQUEST_CONFIRM_SENIOR_NETWORK);
    }
    
    protected void onActivityResult(final int requestCode, int resultCode, final Intent data) {
    	 if (requestCode == REQUEST_CONFIRM_SENIOR_NETWORK ) {
    		 boolean bEnable = isNetworkEnabled();
    		 Log.i(TAG,"VersionUpdateActivity.onActivityResult, bEnable :"+ bEnable);
    		 if( bEnable ){
    			 initUpdate(VersionUpdateSUS.getInstance().mAutoUpdate, null);
    		 }else{
    			 Toast.makeText(this, R.string.version_update_toast, 1).show();
    			 finish();
    		 } 
         }
    }
	private boolean isNetworkEnabled(){
         //added by yumina for the wifi img
         if(SettingsValue.isWifiProduct()) return true;

         SharedPreferences sharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
         boolean isNetworkEnabled = sharedPreferences.getBoolean(SettingsValue.PREF_NETWORK_ENABLER, true);
         return isNetworkEnabled;
	}
	
	private void showQueryProgressBar(){
		if(mQueryProgressDialog == null)
			mQueryProgressDialog = new LeProcessDialog(this);
    	    if(mQueryProgressDialog != null){
    	    	mQueryProgressDialog.setLeMessage(R.string.version_update_query_version);
    	    }
		    //mQueryProgressDialog = new ProgressDialog(this,R.style.Theme_LeLauncher_ProgressDialog);
		    //mQueryProgressDialog.setMessage( getString(R.string.version_update_query_version));
		    //mQueryProgressDialog.setIndeterminate(true);
		    mQueryProgressDialog.setOnCancelListener( new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
                finish();
			}	
        });
		mQueryProgressDialog.show();
	}
	
	private void processQueryCompleteMessage(String param, int type){
		if( type == 0 ){
			if( mQueryProgressDialog != null && mQueryProgressDialog.isShowing())
				mQueryProgressDialog.dismiss();
		    processVersion(param);
		}else if( type == 1 ){
			if( param.equals("SUCCESS")){
				if( !VersionUpdateSUS.getInstance().getInstance().getInstance().queryVersion()){
					finish();
				}
			}else{
				//String message = getString( R.string.SUS_MSG_FAIL_SUSSERVER );
				String message = getString( R.string.SUS_MSG_UPDATE_EXCEPTION );
				if(  message != null && !VersionUpdateSUS.getInstance().mAutoUpdate)
				    Toast.makeText(this, message, 1).show();
				finish();
			}
		}else{ 
			finish();
        }
	}
	
	private void popupNewVersionPromotion() {
        Log.i(TAG,"popupNewVersionPromotion(), new version");
		//LayoutInflater inflater = LayoutInflater.from(this);
		final Context context = this;
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.sus_updateinfo_dialog, null);
		
		myCustomDialog = new LeDialog(this, R.style.Theme_LeLauncher_Dialog_Shortcut);
        myCustomDialog.setLeTitle(R.string.SUS_VERSIONUPDATE);
        myCustomDialog.setLeContentView(layout);
        myCustomDialog.setLePositiveButton(context.getText(R.string.SUS_UPDATE), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				myMSGHandler.removeMessages(MSG_UPDATE_DOWNLOAD);
				myMSGHandler.sendEmptyMessage(MSG_UPDATE_DOWNLOAD);
				dialog.dismiss();
			}

		});
        myCustomDialog.setLeNegativeButton(context.getText(R.string.SUS_CANCEL), 
        		new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				((Activity)context).finish();
			}

		});
        
        myCustomDialog.setOnCancelListener( new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				((Activity)context).finish();
			}
        	
        });

        if( null == myCustomDialog ) {
        	((Activity)context).finish();
        	return;
        }
        
    	PackageInfo packageinfo = null;
    	PackageManager packagemanager = getPackageManager();
    	String packName = getPackageName();
		try {
			packageinfo = packagemanager.getPackageInfo(packName, 0);
		} catch (NameNotFoundException e) {
			//e.printStackTrace();
			myCustomDialog.dismiss();
			((Activity)context).finish();
			return;
		}
		String currentVersionName = packageinfo.versionName;
		Log.i(TAG,"Current Version:"+ currentVersionName);
		if(currentVersionName != null && !currentVersionName.isEmpty()) {
	         TextView text_currentinfo = (TextView)layout.findViewById( R.id.SUS_currentversioninfo );
	         if( text_currentinfo == null){
	        	 myCustomDialog.dismiss();
	        	 ((Activity)context).finish();
	        	 return;
	         }
	         text_currentinfo.setText(getString(R.string.SUS_QUERY_CURRENT_VERSION) + "  "+ currentVersionName);
		} 
		Log.i(TAG,"New Version:"+ mVersionName +", size:"+ mFileSize);
		if(mVersionName != null && !mVersionName.isEmpty()) {
	         TextView text_versioninfo = (TextView)layout.findViewById( R.id.SUS_newversioninfo );
	         if( text_versioninfo == null){
	        	 myCustomDialog.dismiss();
	        	 ((Activity)context).finish();
	        	 return;
	         }
	         String info = getString(R.string.SUS_QUERY_NEW_VERSION) + "  "+ mVersionName;
	         text_versioninfo.setText(info);
		} 
		
		
		if(mFileSize != null && !mFileSize.isEmpty()) {
	         TextView text_versioninfo_size = (TextView)layout.findViewById( R.id.SUS_newversioninfo_size );
	         if( text_versioninfo_size == null){
	        	 myCustomDialog.dismiss();
	        	 ((Activity)context).finish();
	        	 return;
	         }
	         String tmpSize = calculateSize(mFileSize);
	         String	info =  getString(R.string.SUS_QUERY_NEW_SIZE)+ "  "+  tmpSize;
	         text_versioninfo_size.setText(info);
		} 
		
		Log.i(TAG,"Update:"+ mUpdateDesc);
		if(mUpdateDesc != null && !mUpdateDesc.isEmpty()) {
			TextView text_versioncomments = (TextView)layout.findViewById( R.id.SUS_versiondescribe_content );
			if( text_versioncomments == null){
	       	    myCustomDialog.dismiss();
	       	    ((Activity)context).finish();
	       	    return;
	        }
			if(!mUpdateDesc.equals("No")){
	             text_versioncomments.setText(mUpdateDesc);
			}else{
				 text_versioncomments.setVisibility(View.GONE);
			}
		}
		myCustomDialog.show();
    }
	
	private String calculateSize(String size){
		if (size == null || size.length() == 0 ) {
			return null;
		}
		float sizeInt = Float.parseFloat(size);
		if(sizeInt > 1048576){
			return String.format("%.2f", sizeInt/1048576) + " M";
		} else if(sizeInt > 1024){
			//return Math.round( sizeInt/1024 ) + " K";
			return String.format("%.1f", sizeInt/1024)+" K";
		} else{
			return Math.round( sizeInt ) + " B";
		}
	}
	
	private void processDownload(){
		Log.i(TAG,"VersionUpdateQueryAcitivity.processDownload()++++++++++++++++");
		Context context = VersionUpdateSUS.getInstance().getContext();
		if( context != null){
		    SUS.downloadApp(context, mUrl, mFileName, Long.valueOf(mFileSize),
				SettingsValue.LAUNCHER_PACKAGE_NAME_PREF,mChannelKey, mPackageId );
		}
		finish();
	}
	
	private void processVersion( String param){
		Log.i(TAG,"VersionUpdateActivity.processVersion(), ---auto:"+ VersionUpdateSUS.getInstance().mAutoUpdate);
	     String jsonObjectStr = null;
		 String resultion = null;
		 //String channelKey = null;
		 //String verCode = null;
		 String message = null;
		 if( null != param && param.length() > 0 ){
		 	JSONObject jsonObject = null;
			try {
				jsonObject = new JSONObject(param);
				resultion = jsonObject.getString("RES");

				if("SUCCESS".equals(resultion)){
					jsonObjectStr = jsonObject.getString("ChannelKey");
					mChannelKey = (null!=jsonObjectStr && jsonObjectStr.length()>0)?URLDecoder.decode(jsonObjectStr):null;
					//jsonObjectStr = jsonObject.getString("VerCode");
					//verCode = (null!=jsonObjectStr && jsonObjectStr.length()>0)?URLDecoder.decode(jsonObjectStr):null;
					jsonObjectStr = jsonObject.getString("VerName");
					mVersionName = (null!=jsonObjectStr && jsonObjectStr.length()>0)?URLDecoder.decode(jsonObjectStr):null;
					jsonObjectStr = jsonObject.getString("DownloadURL");
					mUrl = (null!=jsonObjectStr && jsonObjectStr.length()>0)?URLDecoder.decode(jsonObjectStr):null;
					jsonObjectStr = jsonObject.getString("Size");
					mFileSize = (null!=jsonObjectStr && jsonObjectStr.length()>0)?URLDecoder.decode(jsonObjectStr):null;
					jsonObjectStr = jsonObject.getString("UpdateDesc");
					mUpdateDesc = (null!=jsonObjectStr && jsonObjectStr.length()>0)?URLDecoder.decode(jsonObjectStr):null;
					jsonObjectStr = jsonObject.getString("FileName");
					mFileName = (null!=jsonObjectStr && jsonObjectStr.length()>0)?URLDecoder.decode(jsonObjectStr):null;
					//jsonObjectStr = jsonObject.getString("CustKey");
					//CustKey = (null!=jsonObjectStr && jsonObjectStr.length()>0)?URLDecoder.decode(jsonObjectStr):null;
					//jsonObjectStr = jsonObject.getString("ForceUpdate");
					//ForceUpdate = (null!=jsonObjectStr && jsonObjectStr.length()>0)?URLDecoder.decode(jsonObjectStr):null;
					jsonObjectStr = jsonObject.getString("PackageId");
					mPackageId = (null!=jsonObjectStr && jsonObjectStr.length()>0)?URLDecoder.decode(jsonObjectStr):null;
					
					
					popupNewVersionPromotion();
					return;
				}else if("LATESTVERSION".equals(resultion) ||
						 "NOTFOUND".equals(resultion)){
					message = getString(R.string.SUS_MSG_LATESTVERSION);
				}/*else if("NOTFOUND".equals(resultion)){
					message = getString(R.string.SUS_MSG_NOTFOUND);
				}*/else if("EXCEPTION".equals(resultion)){
					message = getString(R.string.SUS_MSG_UPDATE_EXCEPTION);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				message = getString(R.string.SUS_MSG_UPDATE_EXCEPTION);
			}
		}
		if(  message != null && !VersionUpdateSUS.getInstance().mAutoUpdate)
		    Toast.makeText(this, message, 1).show();
		finish();
	 }

	private void finishWindow(){
		//Log.i(TAG,"***************finish()");
		finish();
	}

}
