package com.lenovo.lejingpin.hw.lcapackageinstaller;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.lenovo.launcher.R;

import com.lenovo.launcher2.customizer.SettingsValue;

import com.lenovo.lejingpin.hw.content.data.HwConstant;
import com.lenovo.lejingpin.hw.content.util.ContentManagerLog;
import com.lenovo.lejingpin.hw.lcapackageinstaller.utils.SignatureVerify;
import com.lenovo.lejingpin.hw.lcapackageinstaller.utils.XmlLcaInfoHandler;
import com.lenovo.lejingpin.hw.lcapackageinstaller.utils.XmlParser;
import com.lenovo.lejingpin.share.download.DownloadConstant;
import com.lenovo.lsf.util.PsDeviceInfo;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageParser;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class LcaInstallerService extends Service{
	private String TAG = "Install_zdx";
	
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private final HashMap<String, Uri > mInstallRequest =
            new HashMap<String, Uri >();
    
    private final HashMap <String, ErrorApk> mErrorApkMap = new HashMap<String, ErrorApk>();
    
	private static final int DLG_INSTALL_EXIST = 1;
	private static final int DLG_PACKAGE_ERROR =  2;
	private static final int DLG_PERMISSION_ERROR =  3;
	private static final int DLG_DATE_ERROR =  4;
	private static final int DLG_DEVICE_ERROR =  5;
	private static final int DLG_NETWORK_PERMISSION_ERROR = 6;
	private static final int DLG_OUT_OF_SPACE =  7;
	
	private static final int DLG_INSTALL_FAILED_INVALID_APK =  8;
	private static final int DLG_INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES =  9;
	private static final int DLG_INSTALL_FAILED_OLDER_SDK =  10;
	
    private final int INSTALL_COMPLETE = 1;
    private final int INSTALL_PROCESS = 2;
    private final int SHOW_ERROR_TOAST = 3;
    private final int PACKAGE_PARSE = 4;
    private final int FINISH_SERVICE = 5;
    
	private Toast toast = null;
	private View toastView = null;
    
	private class PackageInstallObserver extends IPackageInstallObserver.Stub {
        public void packageInstalled(String packageName, int returnCode) {
        	mServiceHandler.removeMessages(INSTALL_COMPLETE);
    		Message msg = mServiceHandler.obtainMessage(INSTALL_COMPLETE);
    		msg.arg1 = returnCode;
    		msg.obj = packageName;
    		mServiceHandler.sendMessage(msg);
        }
    }
	private final class ServiceHandler extends Handler {
	    public ServiceHandler(Looper looper) {
	        super(looper);
	    }	    
	    public void handleMessage(Message msg) {
            switch (msg.what) {
                case INSTALL_COMPLETE:{
                	Log.i(TAG,"LcaInstallerService, process INSTALL_COMPLETE");
                    String packageName = (String)msg.obj;
                    if (msg.arg1 == PackageManager.INSTALL_SUCCEEDED) {
                    	Log.i(TAG,"****************PackageInstallObserver INSTALL_COMPLETE****success!!!");
     	                if( packageName != null){
     	                	Log.i(TAG, "INSTALL_SUCCEEDED INSTALL_SUCCEEDED remove ");
    	                    mInstallRequest.remove(packageName);
    	                    mErrorApkMap.remove(packageName);
    	                }
     	                
     	               if( mInstallRequest.isEmpty()){
     	      			Log.i(TAG, "INSTALL_SUCCEEDED finish service");
     	      			mServiceHandler.removeMessages(FINISH_SERVICE);
     	      		    Message msgs = mServiceHandler.obtainMessage(FINISH_SERVICE);
     	      		    mServiceHandler.sendMessageDelayed(msgs, 5000);
     	      		}
     	                
                    } else if (msg.arg1 == PackageManager.INSTALL_FAILED_INSUFFICIENT_STORAGE){
                    	Log.i(TAG,"****************PackageInstallObserver INSTALL_COMPLETE****fail!!!");
                    	showToast(DLG_OUT_OF_SPACE, packageName);
                    } else {
                    	Log.i(TAG,"****************PackageInstallObserver INSTALL_COMPLETE*******fail!!!");
                    	switch (msg.arg1) {
                        case PackageManager.INSTALL_FAILED_INVALID_APK:{
                        	showToast(DLG_INSTALL_FAILED_INVALID_APK, packageName);
                        	break;
                        }    
                        case PackageManager.INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES:{
                        	showToast(DLG_INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES, packageName);
                        	break;
                        }
                        case PackageManager.INSTALL_FAILED_OLDER_SDK:{
                        	showToast(DLG_INSTALL_FAILED_OLDER_SDK,packageName);
                        	break;
                        }
                        default:{
                        	showToast(DLG_INSTALL_FAILED_INVALID_APK,packageName);
                        	break;
                        }
                        }
                    }
                    //sendResultToUI(bResult);
                    break;
                }
                case INSTALL_PROCESS:{
                	Bundle bundle = (Bundle)msg.obj;
                	String packageName = bundle.getString("package");
                	String fileUriStr = bundle.getString("uri");
                	String type = bundle.getString("type");
                	String versionCode = bundle.getString("versioncode");
                	int category = bundle.getInt("category");
                	
                	ErrorApk mErrorApk = new ErrorApk();
        			mErrorApk.setCategory(category);
        			mErrorApk.setVersionCode(versionCode);
                	
                	if(mErrorApkMap!=null && !mErrorApkMap.containsKey(packageName)){
                		Log.i(TAG, "mErrorApkMap put "+"packageName is:"+packageName+";versionCode is:"+versionCode+";category is:"+category);
        				mErrorApkMap.put(packageName, mErrorApk);
        			}
                	
                	if( "package_install".equals(type)){
                		Log.i(TAG,"LcaInstallerService, process INSTALL_PROCESS , package_install");
                		if( !mInstallRequest.containsKey(packageName) ){
            		         mInstallRequest.put(packageName, Uri.parse(fileUriStr));
            		    }else{
            		    	//yangmao modify it for silence-install
            		    	 //showToast(DLG_INSTALL_EXIST,null);
            		    	showToast(DLG_INSTALL_EXIST,packageName);
            		    	 return;
            		    }
                		installPackage(packageName, Uri.parse(fileUriStr));
                	}else if("package_install_after_parse".equals(type)){
                		Log.i(TAG,"LcaInstallerService, process INSTALL_PROCESS , package_install_after_parse");
                		installPackage(packageName, Uri.parse(fileUriStr));
                	}
                	else if("package_install_continue".equals(type)){
                		Log.i(TAG,"LcaInstallerService, process INSTALL_PROCESS , package_install_continue");
                		boolean akeyInstall = bundle.getBoolean("akeyinstall");
                		int installType = bundle.getInt("install_type");
                		if( !mInstallRequest.containsKey(packageName) ){
           		             mInstallRequest.put(packageName, Uri.parse(fileUriStr));
           		        }else{
           		    	     showToast(DLG_INSTALL_EXIST,null);
           		    	     return;
           		        }
                		installPackageContinue(packageName, Uri.parse(fileUriStr), akeyInstall, installType);
                	}
                	break;
                }
                case SHOW_ERROR_TOAST:{
                	Bundle bundle = (Bundle)msg.obj;
                	String packageName = bundle.getString("package");
                	int error = bundle.getInt("error");
                	showToast(error, packageName);
                	break;
                }
                case PACKAGE_PARSE:{
                	Log.i(TAG,"LcaInstallerService, process PACKAGE_PARSE");
                	Bundle bundle = (Bundle) msg.obj;
        			String uriStr = bundle.getString("uri");
        			String packageName = bundle.getString("package");
        			//yangmao modify
        			String versionCode = bundle.getString("versioncode");
        			int category = bundle.getInt("category");
        			
        			ErrorApk mErrorApk = new ErrorApk();
        			mErrorApk.setCategory(category);
        			mErrorApk.setVersionCode(versionCode);
        			
        			if(mErrorApkMap!=null && !mErrorApkMap.containsKey(packageName)){
        				Log.i(TAG, "mErrorApkMap put "+"packageName is:"+packageName+";versionCode is:"+versionCode+";category is:"+category);
        				mErrorApkMap.put(packageName, mErrorApk);
        			}
        			verifyPackage(uriStr, packageName );
                	break;
                }
                case FINISH_SERVICE:{
                	if( mInstallRequest.isEmpty()){
                		Log.i(TAG, "true finishSelf()");
                	    finishSelf();
                	}
                	break;
                }
                default:
        			ContentManagerLog.d(TAG, " default .");
            }
        }
	}
	
	void finishSelf(){
		this.stopSelf();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG,"---LcaInstallerService.onCreate()");
	    HandlerThread thread = new HandlerThread("InstallerService", Process.THREAD_PRIORITY_BACKGROUND);
	    thread.start();
	    mServiceLooper = thread.getLooper();
	    mServiceHandler = new ServiceHandler(mServiceLooper);
	}
	
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
        	String action = intent.getAction();
        	if( "com.lenovo.action.packageparse".equals(action)){
        		Log.i(TAG,"***************LcaInstallerService.onStartCommand(), com.lenovo.action.packageparse");
        		mServiceHandler.removeMessages(PACKAGE_PARSE);
                Message msg = mServiceHandler.obtainMessage(PACKAGE_PARSE);
                msg.obj = intent.getExtras();
                mServiceHandler.sendMessage(msg);
            }else if("com.lenovo.action.packageinstall".equals(action)){     
            	Log.i(TAG,"***************LcaInstallerService.onStartCommand(), com.lenovo.action.packageinstall");
            	mServiceHandler.removeMessages(INSTALL_PROCESS);
        		Message msg = mServiceHandler.obtainMessage(INSTALL_PROCESS);
        		msg.obj = intent.getExtras();
        		mServiceHandler.sendMessage(msg);
            }
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
    	Log.i(TAG,"*******************LcaInstallService.onDestroy**************************");
        mServiceLooper.quit();
        mInstallRequest.clear();
        mErrorApkMap.clear();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
	private void verifyPackage(String uriStr, String packageName){
		Uri packageURI =  null;
		if( uriStr != null && packageName != null ){
		    packageURI = Uri.parse(uriStr);
		    if( !mInstallRequest.containsKey(packageName) ){
		        mInstallRequest.put(packageName, packageURI);
		    }else{
		    	//yangmao modify this
		    	//showToast(DLG_INSTALL_EXIST,null);
		    	showToast(DLG_INSTALL_EXIST,packageName);
		    	return;
		   }
		}else{
			showToast(DLG_PACKAGE_ERROR,null);
			return;
		}
		JarFile jarFile = null;
		JarEntry jarEntry = null;
		try {
			jarFile = new JarFile(packageURI.getPath());
		} catch (Exception e) {
			Log.i(TAG,"LcaInstallerService, new JarFile error!");
			showToast(DLG_PACKAGE_ERROR, packageName);
			return;
		}
		
		JarEntry entryTemp = null;
		File tempFile = null;
		boolean bFoundXML  = false;
		boolean bFoundAPK = false;
		for (Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements() && (!bFoundXML|| !bFoundAPK) ;) {
			entryTemp = e.nextElement();
			String entryName = entryTemp.getName();
			if (entryName.equalsIgnoreCase("lcainfo.xml") && !bFoundXML) {
				jarEntry = entryTemp;
				bFoundXML = true;
			}else if (entryName.endsWith(".apk") && !bFoundAPK) {
				tempFile = extractFileFromJarFile(packageURI, jarFile, entryTemp);
				bFoundAPK = true;
			}
		}
		if (jarEntry != null) {// LCA package
			boolean verified = true;
			XmlLcaInfoHandler mLcaHandler = new XmlLcaInfoHandler();
			try {
				XmlParser.Parse(jarFile.getInputStream(jarEntry), mLcaHandler);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			Map<String, String>  mLcaInfo = mLcaHandler.getParseData();
			if (mLcaInfo == null) {
				Log.i(TAG," LcaInstallerService, mLcaHandler.getParseData() error!");
				showToast(DLG_PACKAGE_ERROR, packageName);
				return;
			}
			// Verify the lca package
			if (!SignatureVerify.verifySignature(this, packageURI.getPath())) {
				if (mLcaHandler.getHasTest()
						&& SignatureVerify.verifyTestSignature(this,
								packageURI.getPath())) {
					String lcadate = mLcaInfo.get(XmlLcaInfoHandler.TAG_InvalidDate);
					Log.i(TAG, "lcadate: " + lcadate);
					DateFormat dateFormat = new SimpleDateFormat(
							"yyyyMMddhhmmss");
					try {
						Date date = dateFormat.parse(lcadate + "23" + "59"+ "59");
						long lastdate = date.getTime();
						if (System.currentTimeMillis() > lastdate) {
							verified = false;
						}
					} catch (Exception e) {
						verified = false;
					}
					Log.i(TAG, "lcadate verified: " + verified);
					if (!verified) {
						showToast(DLG_DATE_ERROR, packageName);
						return;
					}
					String imei = PsDeviceInfo.getDeviceId(this);
					ArrayList<String> apkImei = mLcaHandler.getImei();
					if (imei == null) {
						verified = false;
					} else if (apkImei.size() != 0 && !apkImei.contains(imei)) {
						verified = false;
					}
					Log.i(TAG, "imei verified: " + verified);
					if (!verified) {
						showToast(DLG_DEVICE_ERROR, packageName);
						return;
					}
				} else {
					verified = false;
				}
			}
			verified = true;
			
			if (!verified) {
				showToast(DLG_PERMISSION_ERROR, packageName);
			}else{
				if (tempFile == null) {
					showToast(DLG_PACKAGE_ERROR, packageName);
					return;
				}
				
				//yangmao add for 0115 
				//if delete the install-file ,the new download-file name will use old name
//				File f = new File(uriStr);
//				if(f.exists()){
//					f.delete();	
//				}

				Uri fileUri = Uri.fromFile(tempFile );
	    		mServiceHandler.removeMessages(INSTALL_PROCESS);
	    		Message msg = mServiceHandler.obtainMessage(INSTALL_PROCESS);
	    		Bundle bundle = new Bundle();
	    		bundle.putString("type", "package_install_after_parse");
	    		bundle.putString("package", packageName);
	    		bundle.putString("uri", fileUri.toString());
	    		msg.obj = bundle;
	    		mServiceHandler.sendMessage(msg);
			}
		} else {
			Log.i(TAG," LcaInstallerService, jarEntry is null,  error!");
			showToast(DLG_PACKAGE_ERROR, packageName);
		}
	}
	
	private void showToast(int id, String packageName){
		String message = "";
		boolean bTerminal = true;
		switch (id) {
		case DLG_INSTALL_EXIST:{
			message = getString(R.string.lcapackageinstaller_install_exist);
			bTerminal = false;
			break;
		}
	    case DLG_PACKAGE_ERROR:{
		    message = getString(R.string.lcapackageinstaller_parse_error);
		    break;
	    }
	    case DLG_PERMISSION_ERROR:{
	    	message= getString(R.string.lcapackageinstaller_permission_verify_error);
	    	break;
	    }
	    case DLG_DATE_ERROR:{
	    	message= getString(R.string.lcapackageinstaller_permission_date_error);
            break;
	    }
	    case DLG_DEVICE_ERROR:{
    	    message= getString(R.string.lcapackageinstaller_permission_device_error);
            break;
        }
	    case DLG_NETWORK_PERMISSION_ERROR:{
    	    message= getString(R.string.lcapackageinstaller_permission_verify_error);
            break;
        }
	    case DLG_OUT_OF_SPACE:{
	    	message= getString(R.string.lcapackageinstaller_out_of_space_dlg_text);
	        break;
	    }
	    case DLG_INSTALL_FAILED_INVALID_APK:{
	    	message = getString(R.string.lcapackageinstaller_install_error);
	    	break;
	    }
	    case DLG_INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES:{
	    	message = getString( R.string.install_failed_inconsistent_certificates);
	    	break;
	    }
	    case DLG_INSTALL_FAILED_OLDER_SDK :{
	    	message = getString(R.string.install_failed_older_sdk);
	    	break;
	    }
	    default:
	    	break;
	    }
		
		Log.i(TAG, "showToas method");
		
		
		if(mErrorApkMap!=null && mErrorApkMap.containsKey(packageName)){
			Log.i(TAG, "mErrorApkMap getVersionCode");
			sendInstallError(packageName,mErrorApkMap.get(packageName).getVersionCode(),
					mErrorApkMap.get(packageName).getCategory());
		}
		
		
		
		if( message != null && !message.equals("")){
			
	       // Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
			//yangmao add 0226
			toastShow(message);
		}	
		//yangmao modify this for silence-install
//		if( bTerminal && ( packageName != null ) && (mInstallRequest.containsKey(packageName))){
//		    mInstallRequest.remove(packageName);
//		}
		if(( packageName != null ) && (mInstallRequest.containsKey(packageName))){
		    mInstallRequest.remove(packageName);
		}
		if( mInstallRequest.isEmpty()){
			Log.i(TAG, "toast method finish service");
			mServiceHandler.removeMessages(FINISH_SERVICE);
		    Message msg = mServiceHandler.obtainMessage(FINISH_SERVICE);
		    mServiceHandler.sendMessageDelayed(msg, 5000);
		}
    }
	
	
	private void showToastInThread(int error, String packageName){
		mServiceHandler.removeMessages(SHOW_ERROR_TOAST);
		Message msg = mServiceHandler.obtainMessage(SHOW_ERROR_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString("package", packageName);
		bundle.putInt("error", error);
		msg.obj = bundle;
		mServiceHandler.sendMessage(msg);
	}

	private void installPackage(String packageName, Uri fileUri) {
		File file = new File(fileUri.toString() );
		runResult("chmod 777 " + file);
		int installType = HwConstant.canAKeyIntall(this);
		Log.i(TAG,"LcaInstallerService.installPackage , packageName:"+ packageName+", fileURI:"+ fileUri);
		
		if( installType== HwConstant.INSTALL_TYPE_NOT_AKEY ){
		    Log.i(TAG,"LcaInstallerService, not install directly...");
		    callSystemInstallPackage(packageName, fileUri);
		}/*else if( installType== HwConstant.INSTALL_TYPE_AKEY_SHELL ){
			Log.i(TAG,"LcaInstallerService, install directly...INSTALL_TYPE_AKEY_SHELL...");
			int autoInstall = judgeAutoInstall(packageName, fileUri, installType);
			if( autoInstall == 1){
			    autoInstallPackageByShell(packageName, fileUri);
			}else  if( autoInstall == 0){
				callSystemInstallPackage(packageName, fileUri);
			}
		}*/else {
			Log.i(TAG,"LcaInstallerService, install directly...INSTALL_TYPE_AKEY_NORMAL...");
			//yangmao modify this ,we have the settings of control install-auto or not ,so directely to auto-install 0723
//			int autoInstall = judgeAutoInstall(packageName, fileUri, installType);
//			if( autoInstall == 1){
//				autoInstallPackageByNormal(packageName, fileUri);
//			}else if( autoInstall == 0){
//				callSystemInstallPackage(packageName, fileUri);
//			}
			//yangmao modify this 0723
			autoInstallPackageByNormal(packageName, fileUri);
		}
		
//		if( mInstallRequest.isEmpty()){
//			Log.i(TAG, "install package method finish service");
//			mServiceHandler.removeMessages(FINISH_SERVICE);
//		    Message msg = mServiceHandler.obtainMessage(FINISH_SERVICE);
//		    mServiceHandler.sendMessageDelayed(msg, 5000);
//		}
	}
	
	private void installPackageContinue( String packageName, Uri fileUri, boolean akeyInstall, int installType){
		Log.i(TAG,"LcaInstallerService.installPackageContinue, packageName:"+ packageName +
				", akeyInstall:"+ akeyInstall+" ,installType:"+ installType );
		if(akeyInstall){
			if( installType == HwConstant.INSTALL_TYPE_AKEY_NORMAL){
			    autoInstallPackageByNormal(packageName, fileUri);
			}else {
				autoInstallPackageByShell(packageName, fileUri);
			}
		}else{
			callSystemInstallPackage(packageName, fileUri);
		}
		
		if( mInstallRequest.isEmpty()){
			Log.i(TAG, "installpackageContinue finish service");
			mServiceHandler.removeMessages(FINISH_SERVICE);
		    Message msg = mServiceHandler.obtainMessage(FINISH_SERVICE);
		    mServiceHandler.sendMessageDelayed(msg, 5000);
		}
	}
	
	private File extractFileFromJarFile(Uri packageURI, JarFile file, JarEntry entry) {
		File apkFile = null;
		byte[] readBuffer = new byte[8192];
		try {
			String path = packageURI.getPath();
			String dir = path.substring(0, path.lastIndexOf(File.separator));
			String fileName = path.substring(path.lastIndexOf(File.separator)+1);
			apkFile = new File(dir + File.separator + "lca" + fileName.replace(".lca", ".apk"));
			InputStream is = file.getInputStream(entry);
			OutputStream os = new FileOutputStream(apkFile);
			int num;
			while ((num = is.read(readBuffer)) > 0) {
				os.write(readBuffer, 0, num);
			}
			os.flush();
			is.close();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return apkFile;
	}
	
	private void runResult(final String str) {
		//Log.i(TAG,"****************************LcaUnstallerService.runResult, str:"+ str);
		Runtime runtime = Runtime.getRuntime();
		java.lang.Process process = null;
		try {
			process = runtime.exec(str);
		} catch (IOException e) {
		    Log.w(TAG, "LcaInstallerService.runResult, IOException", e);
		}finally{
			if (process != null) { 
			    process.destroy();
			}
		}
	}
	
	private void runInstallByShell(final String filePath, final String packageName) {
		new Thread() { 
        	public void run() { 
        	   java.lang.Process process = null; 
        	   OutputStream out = null; 
        	   InputStream in = null; 
        	   try { 
        	        process = Runtime.getRuntime().exec("su");  
        	        out = process.getOutputStream(); 
        	        out.write(("pm install -r " + filePath + "\n").getBytes()); 
        	        in = process.getInputStream(); 
        	        int len = 0; 
        	        byte[] bs = new byte[256]; 
        	        while (-1 != (len = in.read(bs))) { 
        	            String state = new String(bs, 0, len); 
        	            if (state.equals("Success\n")) { 
        	               Log.i("zdx","*************************runInstallByShell,install success!!!");
        	               if( packageName != null){
        	                   mInstallRequest.remove(packageName);
        	               }
        	            }else{
        	            	Log.i("zdx","*************************runInstallByShell,install fail!!!");
        	            	showToastInThread(DLG_INSTALL_FAILED_INVALID_APK, packageName);
        	            }
        	        } 
        	    } catch (IOException e) { 
        	        e.printStackTrace(); 
        	        Log.i("zdx","*************************runInstallByShell,install fail!!!");
        	        showToastInThread(DLG_INSTALL_FAILED_INVALID_APK, packageName);
        	    } catch (Exception e) { 
        	        e.printStackTrace(); 
        	        Log.i("zdx","*************************runInstallByShell,install fail!!!");
        	        showToastInThread(DLG_INSTALL_FAILED_INVALID_APK, packageName);
        	    } finally { 
        	        try { 
        	            if (out != null) { 
        	               out.flush(); 
        	               out.close(); 
        	            } 
        	            if (in != null) { 
        	               in.close(); 
        	            } 
        	            if( process != null){
        	          	   process.destroy();
        	            }
        	        } catch (IOException e) { 
        	            e.printStackTrace(); 
        	        } 
        	    } 
           } 
       }.start(); 
	}
	
	/*
	 * -1: Prompt dialog to confirm whether use auto install.
	 * 0: Not auto install
	 * 1: Auto install
	 * */
	private int judgeAutoInstall(String packageName, Uri fileUri, int installType){
		boolean bAkeyInstall = SettingsValue.getAkeyInstall(this);
		//Log.i(TAG,"LcaInstallerService.judgeAutoInstall--- "+ bAkeyInstall +", installType:"+ installType);
		if( !bAkeyInstall){
			if(!SettingsValue.getAkeyInstallIgnore(this) && 
					!SettingsValue.getAkeyInstallCancel(this)){
			    //Log.i(TAG,"LcaInstallerService.judgeAutoInstall--- start LcaInstallerActivity");
			    Intent intent = new Intent();
	 		    intent.setClass( this, MagicLcaInstallerActivity.class );
	 		    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	 		    intent.putExtra("package", packageName);
	 		    intent.putExtra("uri", fileUri.toString());
	 		    intent.putExtra("install_type", installType);
	 		    intent.setPackage(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF);
	 		    try {
	 			    startActivity(intent);
	 			    if( packageName != null){
	 				    mInstallRequest.remove(packageName); 
	 				}
	 			    SettingsValue.setAkeyInstallCancel(this, true);
	 			    return -1;
	 		    } catch (Exception e) {
	 			    //Log.i(TAG,"LcaInstallerService.judgeAutoInstall--- start LcaInstallerActivity error!");
	 			    return 0;
	 		    }
			}
			return 0;
		}
		return 1;
	}
	
	private void autoInstallPackageByNormal(String packageName, Uri fileURI){
		//Log.i(TAG," LcaInstallerService.autoInstallPackageByNormal,packageName:"+packageName+", fileURI:"+ fileURI);
	    PackageParser.Package packageInfo = getPackageInfo(fileURI);
	    if(packageInfo == null) {
	    	showToast(DLG_INSTALL_FAILED_INVALID_APK, packageName);
            return;
        }
	    ApplicationInfo appInfo = packageInfo.applicationInfo;
	    PackageInstallObserver observer = new PackageInstallObserver();
	    PackageManager packagemanager = getPackageManager();
	    int installFlags = 0;
	    if( appInfo != null){
	        try {
                PackageInfo pi = packagemanager.getPackageInfo(appInfo.packageName, 
                PackageManager.GET_UNINSTALLED_PACKAGES);
                //Log.i(TAG," LcaInstallerService.autoInstallPackageByNormal, install package:"+ appInfo.packageName);
                if(pi != null) {
                    installFlags |= PackageManager.INSTALL_REPLACE_EXISTING;
                }
            } catch (NameNotFoundException e) {
            }
	    }
        Log.i(TAG," LcaInstallerService.autoInstallPackageByNormal, 1 call packagemanager.installPackage, caller package:"+ this.getPackageName());
	    packagemanager.installPackage(fileURI, observer, installFlags, this.getPackageName());
	    //if( packageName != null){
		//	mInstallRequest.remove(packageName); 
		//}
	}
	
	private void autoInstallPackageByShell(String packageName, Uri fileURI){
		Log.i(TAG," LcaInstallerService.autoInstallPackageByShell");
		if(fileURI==null){
			showToast(DLG_INSTALL_FAILED_INVALID_APK, packageName);
		}else{
			String filePath = fileURI.toString().replace("file://", "");
			runInstallByShell( filePath, packageName );
		}
	}
	
	private void callSystemInstallPackage(String packageName, Uri fileUri){
		Log.i(TAG,"*************LcaInstallerService.callSystemInstallPackage********"+ fileUri);
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(fileUri,
				"application/vnd.android.package-archive");
		try {
		    startActivity(intent);
		} catch (Exception e) {
		    Toast.makeText(this, R.string.lca_installer_not_found, Toast.LENGTH_SHORT).show();
		}
		Log.i(TAG, "use system install method remove package");
		if( packageName != null){
			Log.i(TAG, "use system install method  true remove package");
		    mInstallRequest.remove(packageName); 
		}
		if( mInstallRequest.isEmpty()){
			Log.i(TAG, "use system install method finish service");
			mServiceHandler.removeMessages(FINISH_SERVICE);
		    Message msg = mServiceHandler.obtainMessage(FINISH_SERVICE);
		    mServiceHandler.sendMessageDelayed(msg, 5000);
		}
	}
	
	private PackageParser.Package getPackageInfo(Uri packageURI) {
		if( packageURI == null){
			return null;
		}
        final String archiveFilePath = packageURI.getPath();
        PackageParser packageParser = new PackageParser(archiveFilePath);
        File sourceFile = new File(archiveFilePath);
        DisplayMetrics metrics = new DisplayMetrics();
        metrics.setToDefaults();
        PackageParser.Package pkg =  packageParser.parsePackage(sourceFile,
                archiveFilePath, metrics, 0);
        packageParser = null;
        return pkg;
    }
	
	private void sendResultToUI(boolean result){
		//Log.i(TAG,"LcaInstallerService.sendResultToUI(), result:"+ result);
	}
	
	
	 private void toastShow(String text) {
			if (toast == null) {
				toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
				toastView = toast.getView();
			} else {
				if (toastView != null) {
					toast.setText(text);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastView);
				}
			}
			toast.show();
	}
	 
	
	 private void sendInstallError(String packageName ,String versionCode,int category){
		 Log.i(TAG, "sendInstallError packageName -- versionCode is:"+packageName+";"+versionCode+"; category is:"+category);
		 	Intent mIntent = new Intent();
		 	mIntent.setAction(DownloadConstant.ACTION_APK_PARSE_OR_INSTALL_FAILED);
		 	mIntent.putExtra(DownloadConstant.EXTRA_PACKAGENAME, packageName);
		 	mIntent.putExtra(DownloadConstant.EXTRA_VERSION, versionCode);
		 	mIntent.putExtra(DownloadConstant.EXTRA_CATEGORY, category);
		 	mIntent.setPackage(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF);
			this.sendBroadcast(mIntent);
		} 
	 
	 
	 
	 public static final class ErrorApk implements Serializable {
			/**
			 * 
			 */
			private static final long serialVersionUID = 3830870434843595687L;
			private String versionCode;
			private int category;
			
			public ErrorApk(){
				versionCode = "";
				category = -1;
			}
			
			
			public String getVersionCode(){
				
				return versionCode;
			}
			
			public void setVersionCode(String versionCode){
				this.versionCode = versionCode;
			}	
	 
			public int getCategory(){
				
				return category;
			}
	 
			public void setCategory(int category){
				
				this.category = category;
			}
				
	 }
	 
	 
	 
	 
	
}
