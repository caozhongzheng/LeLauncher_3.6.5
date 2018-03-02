package com.lenovo.lejingpin.hw.lcapackageinstaller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.pm.IPackageInstallObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.customizer.SettingsValue;



import com.lenovo.lejingpin.hw.lcapackageinstaller.utils.LcaInfoUtils;
import com.lenovo.lejingpin.hw.lcapackageinstaller.utils.SignatureVerify;
import com.lenovo.lejingpin.hw.lcapackageinstaller.utils.XmlLcaInfoHandler;
import com.lenovo.lejingpin.hw.lcapackageinstaller.utils.XmlParser;
import com.lenovo.lejingpin.hw.utils.UMentAnalyticsUtil;
import com.lenovo.lejingpin.share.download.DownloadHelpers;
import com.lenovo.lejingpin.share.download.DownloadInfo;
import com.lenovo.lejingpin.share.download.LDownloadManager;
import com.lenovo.lps.reaper.sdk.AnalyticsTracker;
import com.lenovo.lsf.account.PsAuthenServiceL;
import com.lenovo.lsf.util.PsDeviceInfo;


public class LcaInstallerActivity extends Activity implements DialogInterface.OnCancelListener, DialogInterface.OnClickListener {
	private static final String tag = "LcaInstallerActivity";

	private int mDlgCurr = DLG_BASE;
	private static final int DLG_BASE = 0;
	private static final int DLG_PREPARE_INSTALL = DLG_BASE + 1;
	private static final int DLG_PACKAGE_ERROR = DLG_BASE + 2;
	private static final int DLG_PERMISSION_ERROR = DLG_BASE + 3;
	private static final int DLG_DATE_ERROR = DLG_BASE + 4;
	private static final int DLG_DEVICE_ERROR = DLG_BASE + 5;
	private static final int DLG_NETWORK_PERMISSION_ERROR = DLG_BASE + 6;
	private static final int DLG_OUT_OF_SPACE = DLG_BASE + 7;

	private static final int HANDLER_BASE_MSG_IDX = 0;
	private static final int INSTALL_PERMISSION = HANDLER_BASE_MSG_IDX + 1;

	private Uri mPackageURI = null;
	private XmlLcaInfoHandler mLcaHandler = null;
	private static Map<String, String> mLcaInfo = null;
	private String mCheckErrorString = "";
	private String installAppPackagename;

	private static final int SUCCEEDED = 0;
	private static final int FAILED = -1;
//	private  static int mScreenWidth = 0;
//	private  static int mScreenHeight = 0;

	private String mLicense = "";
	
	
	//yangmao add 
	
	private Looper mWorkerLooper;
	 
	private static final int DLG_PKG_PI_NOT_EXIST = 8;
	private final int INSTALL_COMPLETE = 9;
	private final int INSTALL_SUCCEED = 10 ;
	private static final int DLG_INSTALL_FAILED_INVALID_APK =  11;
	private static final int DLG_INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES = 12;
	private static final int DLG_INSTALL_FAILED_OLDER_SDK =  13;
	 
	//yangmao add end
	
	private Handler mHandler = null;
	private boolean mIsDestory;

	//zhangdxa modify 20121227 for remove Reaper
	/*protected void onResume() {
		try {
			if(UMentAnalyticsUtil.UMENGFLAG) {
				MobclickAgent.onResume(this);
			}
			AnalyticsTracker.getInstance().trackResume(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onResume();
	};

	@Override
	protected void onPause() {
		try {
			if(UMentAnalyticsUtil.UMENGFLAG) {
				MobclickAgent.onPause(this);
			}
			AnalyticsTracker.getInstance().trackPause(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onPause();
	}*/

	private void showDialogInner(final int id) {
		Log.d(tag, "showDialogInner >> start >> id="+id);
		runOnUiThread(new Runnable() {
			public void run() {
				if(!mIsDestory) {
					if(mDlgCurr != DLG_BASE) {
						Log.d(tag, "showDialogInner >> removeDialog >> id="+id);
						removeDialog(mDlgCurr);
					}
					showDialog(mDlgCurr = id);
				}
				Log.d(tag, "showDialogInner >> end.....mDlgCurr="+mDlgCurr);
			}
		});
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
//		DisplayMetrics dm = new DisplayMetrics();
//		getWindowManager().getDefaultDisplay().getMetrics(dm);
//		mScreenWidth = dm.widthPixels;
//		mScreenHeight = dm.heightPixels;
		super.onConfigurationChanged(newConfig);
	}
	
//	
//	public static int getScreenWidth(){
//		return mScreenWidth;
//	}
//	
//	public static int getScreenHeight(){
//		return mScreenHeight;
//	}
	

	@Override
	public Dialog onCreateDialog(int id, Bundle bundle) {
		switch (id) {
		case DLG_PREPARE_INSTALL:
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage(getString(R.string.lcapackageinstaller_lenovo_dlg_prepare_install));
			dialog.setIndeterminate(true);
			dialog.setCancelable(false);
			dialog.setOnCancelListener(this);
			dialog.setCanceledOnTouchOutside(false);
			return dialog;
		case DLG_PACKAGE_ERROR:
			return new AlertDialog.Builder(this)//
					.setTitle(R.string.lcapackageinstaller_install_hint_title)//
					.setMessage(R.string.lcapackageinstaller_install_hint_message)//
					.setPositiveButton(R.string.lcapackageinstaller_ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							if(downloadInfo != null) {
								Uri downloadUri = Uri.parse("content://com.lenovo.lejingpin.hw.content.download/download");
								getContentResolver().delete(downloadUri, "pkgname = ? and versioncode = ?",
										new String[] { downloadInfo.getPackageName(), downloadInfo.getVersionCode() });
								//DownloadHelpers.notifyDelete(LcaInstallerActivity.this, downloadInfo);
								LDownloadManager.getDefaultInstance(getBaseContext()).addTask(downloadInfo);
							} else {
								Toast.makeText(getBaseContext(), R.string.download_error, Toast.LENGTH_SHORT).show();
							}
							finish();
						}
					})//
					.setNegativeButton(R.string.lcapackageinstaller_cancel, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					})//
					.setOnCancelListener(this)//
					.create();
		case DLG_PERMISSION_ERROR:
			return new AlertDialog.Builder(this)//
					.setTitle(R.string.lcapackageinstaller_permission_error_dlg_title)//
					.setMessage(R.string.lcapackageinstaller_permission_error_dlg_text)//
					.setPositiveButton(R.string.lcapackageinstaller_ok, this)//
					.setOnCancelListener(this)//
					.create();
		case DLG_DATE_ERROR:
			return new AlertDialog.Builder(this).setTitle(R.string.lcapackageinstaller_permission_error_dlg_title)//
					.setMessage(R.string.lcapackageinstaller_permission_date_error)//
					.setPositiveButton(R.string.lcapackageinstaller_ok, this)//
					.setOnCancelListener(this)//
					.create();
		case DLG_DEVICE_ERROR:
			return new AlertDialog.Builder(this)//
					.setTitle(R.string.lcapackageinstaller_permission_error_dlg_title)//
					.setMessage(R.string.lcapackageinstaller_permission_device_error)//
					.setPositiveButton(R.string.lcapackageinstaller_ok, this)//
					.setOnCancelListener(this)//
					.create();
		case DLG_NETWORK_PERMISSION_ERROR:
			String message = "";
			if(mCheckErrorString.equals("neterr")) message = getString(R.string.lcapackageinstaller_permission_error_dlg_text1);
			else if(mCheckErrorString.equals("sterror")) message = PsAuthenServiceL.getLastErrorString(this);
			else if(mCheckErrorString.equals("cancel")) message = getString(R.string.lcapackageinstaller_permission_error_dlg_text3);
			else message = getString(R.string.lcapackageinstaller_permission_error_dlg_text);
			if("".equals(message)) return null;
			return new AlertDialog.Builder(this)//
					.setTitle(R.string.lcapackageinstaller_permission_error_dlg_title)//
					.setMessage(message)//
					.setPositiveButton(R.string.lcapackageinstaller_ok, this)//
					.setOnCancelListener(this)//
					.create();
		case DLG_OUT_OF_SPACE:
			String dlgText = getString(R.string.lcapackageinstaller_out_of_space_dlg_text, "");
			return new AlertDialog.Builder(this)//
					.setTitle(R.string.lcapackageinstaller_out_of_space_dlg_title)//
					.setMessage(dlgText).setPositiveButton(R.string.lcapackageinstaller_manage_applications, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent("android.intent.action.MANAGE_PACKAGE_STORAGE");
							startActivity(intent);
							finish();
						}
					})//
					.setNegativeButton(R.string.lcapackageinstaller_cancel, this)//
					.setOnCancelListener(this)//
					.create();
		}
		return null;
	}

	DownloadInfo downloadInfo;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		mIsDestory = false;

		HandlerThread worker = new HandlerThread("lcawork");
		worker.start();
		mWorkerLooper = worker.getLooper();
		mHandler = new Handler(mWorkerLooper) {
			public void handleMessage(final Message msg) {
				switch (msg.what) {
				case INSTALL_PERMISSION:
					if(msg.arg1 == SUCCEEDED) {
						Log.i("yangmao_install", "start method InstallLcaPackage()");
						InstallLcaPackage();
					} else {
						showDialogInner(DLG_NETWORK_PERMISSION_ERROR);
					}
					break;
				default:
					break;
				}
			}
		};

		final Intent intent = getIntent();
		downloadInfo = intent.getParcelableExtra("data");
		mPackageURI = intent.getData();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		DisplayMetrics dm = new DisplayMetrics();
//		getWindowManager().getDefaultDisplay().getMetrics(dm);
//		mScreenWidth = dm.widthPixels;
//		mScreenHeight = dm.heightPixels;

		mHandler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				JarFile jarFile = null;
				JarEntry jarEntry = null;
				try {
					jarFile = new JarFile(mPackageURI.getPath());
				} catch (Exception e) {
					showDialogInner(DLG_PACKAGE_ERROR);
					return;
				}

				JarEntry entryTemp = null;
				for (Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements();) {
					entryTemp = e.nextElement();
					String entryName = entryTemp.getName();
					if(entryName.equalsIgnoreCase("lcainfo.xml")) {
						jarEntry = entryTemp;
						break;
					}
				}

				if(jarEntry != null) {// LCA package
					boolean verified = true;
					mLcaHandler = new XmlLcaInfoHandler();
					try {
						XmlParser.Parse(jarFile.getInputStream(jarEntry), mLcaHandler);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					mLcaInfo = mLcaHandler.getParseData();
					if(mLcaInfo == null) {
						showDialogInner(DLG_PACKAGE_ERROR);
						return;
					}

					// Verify the lca package
					if(!SignatureVerify.verifySignature(LcaInstallerActivity.this, mPackageURI.getPath())) {
						Log.i(tag, "verify public key failure");

						if(mLcaHandler.getHasTest() && SignatureVerify.verifyTestSignature(LcaInstallerActivity.this, mPackageURI.getPath())) {
							Log.i(tag, "verify test key true");

							String lcadate = mLcaInfo.get(XmlLcaInfoHandler.TAG_InvalidDate);
							Log.i(tag, "lcadate: " + lcadate);
							DateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
							try {
								Date date = dateFormat.parse(lcadate + "23" + "59" + "59");
								long lastdate = date.getTime();
								if(System.currentTimeMillis() > lastdate) {
									verified = false;
								}
							} catch (Exception e) {
								verified = false;
							}
							Log.i(tag, "lcadate verified: " + verified);
							if(!verified) {
								showDialogInner(DLG_DATE_ERROR);
								return;
							}

							String imei = PsDeviceInfo.getDeviceId(LcaInstallerActivity.this);
							ArrayList<String> apkImei = mLcaHandler.getImei();
							Log.i(tag, "IMEI: " + imei + ", apkImei: " + apkImei.size());
							if(imei == null) {
								verified = false;
							} else if(apkImei.size() != 0 && !apkImei.contains(imei)) {
								verified = false;
							}
							Log.i(tag, "imei verified: " + verified);
							if(!verified) {
								showDialogInner(DLG_DEVICE_ERROR);
								return;
							}
						} else {
							Log.i(tag, "verify no test key");
							verified = false;
						}
					}
					//yangmao change it for all-lca can install 1206
					verified = true;
					if(!verified) {
						showDialogInner(DLG_PERMISSION_ERROR);
						return;
					}

					Log.d(tag, "verify success!");
					Log.i("yangmao_install", "show_install dialog");
					//because zhigang,rongfeng suggest the dialog is not show is best,so canle the dialog show.
					//showDialogInner(DLG_PREPARE_INSTALL);

					Message msg = mHandler.obtainMessage(INSTALL_PERMISSION);
					msg.arg1 = SUCCEEDED;
					mHandler.sendMessageDelayed(msg, 2000);

				} else {
					showDialogInner(DLG_PACKAGE_ERROR);
				}
			}
		});

		/*			
					if ("0".equals(mLcaInfo.get(XmlLcaInfoHandler.TAG_AUTHCODE))) {
						Message msg = mHandler.obtainMessage(INSTALL_PERMISSION);
						msg.arg1 = SUCCEEDED;
						mHandler.sendMessageDelayed(msg, 1000);
					} else {
						NetworkUtils.checkInstallPermission(this,
								mLcaInfo.get(XmlLcaInfoHandler.TAG_APPID),
								mLcaInfo.get(XmlLcaInfoHandler.TAG_VERSION),
								mLcaInfo.get(XmlLcaInfoHandler.TAG_LITEVERSION),
								mLcaInfo.get(XmlLcaInfoHandler.TAG_AUTHCODE),
								new INetworkResult() {
									public void onError(String data) {
										mCheckErrorString = data;
										mHandler.obtainMessage(INSTALL_PERMISSION,
												FAILED, 0, data).sendToTarget();
									}

									public void onSuccess(String data) {
										mHandler.obtainMessage(INSTALL_PERMISSION,
												SUCCEEDED, 0).sendToTarget();
										if (mLcaInfo.get(XmlLcaInfoHandler.TAG_AUTHCODE).equals("2")) { 
											mLicense = data;
										} else {
											mLicense = null;
										}
									}
								});
					}
					*/

	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	/**
	 * install the .apk files inside the lca package
	 */
	private void InstallLcaPackage() {
		String lcaPath = mPackageURI.getPath();

		JarFile jarFile = null;
		JarEntry jarEntry = null;
		try {
			jarFile = new JarFile(lcaPath);
		} catch (Exception e) {
			showDialogInner(DLG_PACKAGE_ERROR);
			Log.e(tag, "Error opening file " + lcaPath, e);
			return;
		}

		File tempFile = null;
		for (Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements();) {
			jarEntry = e.nextElement();
			String entryName = jarEntry.getName();
			if(entryName.endsWith(".apk") || entryName.endsWith(".ltp")) {
				tempFile = extractFileFromJarFile(jarFile, jarEntry);
				String tempFilePath = tempFile.getPath();
				installAppPackagename = getInatllApkInfo(this, tempFilePath);
				if(mLcaInfo != null) {
					/*if(mLcaInfo.get(XmlLcaInfoHandler.TAG_PUSH) != null) {
						try {
							PsPushAppData
									.addValue(
											this,
											mLcaInfo.get(XmlLcaInfoHandler.TAG_APPID),
											installAppPackagename,
											mLcaInfo.get(XmlLcaInfoHandler.TAG_SID),
											mLcaInfo.get(XmlLcaInfoHandler.TAG_PUSH_TYPE),
											mLcaInfo.get(XmlLcaInfoHandler.TAG_CMTYPE));
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}*/

					if(mLcaInfo.get(XmlLcaInfoHandler.TAG_AUTHCODE).equals("2") && mLicense != null) {
						String license = mLicense;
						// try {
						// JSONObject jsonObject = new JSONObject(mLicense);
						// license = jsonObject.getString("license");
						// } catch (JSONException e1) {
						// // TODO Auto-generated catch block
						// e1.printStackTrace();
						// }
						FileOutputStream fos = null;
						try {
							File lic = new File(getFilesDir() + "/" + installAppPackagename.replace(".", "_") + ".lic");
							fos = new FileOutputStream(lic);
							fos.write(license.getBytes());
							fos.flush();

							// runResult("chmod 777 " + getFilesDir());
							// Log.i(tag, "chmod 777 " + getFilesDir());
							// Log.i(tag, "chmod 777 " + getFilesDir() + "/" + installAppPackagename.replace(".", "_") + ".lic");
							// runResult("chmod 777 " + getFilesDir() + "/" + installAppPackagename.replace(".", "_") + ".lic");

						} catch (IOException e2) {}
						finally {
							if(fos != null) {
								try {
									fos.close();
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}
						}
					}

					String appid = mLcaInfo.get(XmlLcaInfoHandler.TAG_APPID);
					String appname = installAppPackagename;
					String version = mLcaInfo.get(XmlLcaInfoHandler.TAG_VERSION);
					String apptype = mLcaInfo.get(XmlLcaInfoHandler.TAG_APPTYPE);
					String liteVersion = mLcaInfo.get(XmlLcaInfoHandler.TAG_LITEVERSION);
					LcaInfoUtils.add(this, appid, version, appname, apptype, liteVersion);
				}
				break;
			}
		}
		// Log.d(tag, "InstallLcaPackage >> tempFile :  "+tempFile);
		// runResult("chmod 777 " + tempFile);

		if(tempFile == null) {
			Log.i(tag, "can't find any installable application in the .lca package, filePath: " + lcaPath + ". Discontinuing installation");
			showDialogInner(DLG_PACKAGE_ERROR);
			return;
		}

		if(tempFile.getName().endsWith(".ltp")) {
			Intent i = new Intent("com.lenovo.leos.lcainstaller.THEME");
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.setClassName("com.leos.ThemeManager", "com.leos.ThemeManager.ThemeInstallActivity");
			i.setData(Uri.fromFile(tempFile));
			i.putExtra("appid", mLcaInfo.get(XmlLcaInfoHandler.TAG_APPID));
			i.putExtra("version", mLcaInfo.get(XmlLcaInfoHandler.TAG_VERSION));
			i.putExtra("liteversion", (Boolean.parseBoolean(mLcaInfo.get(XmlLcaInfoHandler.TAG_LITEVERSION)) ? 1 : 0));
			startActivity(i);
		} else {
			//add judge the silence-key is open or not.
			boolean bAkeyInstall = SettingsValue.getAkeyInstall(this);
			if(bAkeyInstall){			
				// install apk file. use silence-install and system-install add by yangmao
				Log.i("yangmao_install", "use silenceInstall or system-install");
				if(canSilentInstall()){				
					Log.i("yangmao_install", "silenceInstall arg is:"+tempFile.getPath()+";package name is:"+installAppPackagename);
					silenceInstall(tempFile.getPath(), installAppPackagename);
				}else{
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra("apk_from", "com.lenovo.appstore");
					intent.setDataAndType(Uri.fromFile(tempFile), "application/vnd.android.package-archive");
					this.startActivity(intent);
					LcaInstallerReceiver.mInstallData.put(installAppPackagename, "" + tempFile);
				}
			}else{
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("apk_from", "com.lenovo.appstore");
				intent.setDataAndType(Uri.fromFile(tempFile), "application/vnd.android.package-archive");
				this.startActivity(intent);
				LcaInstallerReceiver.mInstallData.put(installAppPackagename, "" + tempFile);
			}
		}

		this.finish();
	}

	private File extractFileFromJarFile(JarFile file, JarEntry entry) {
		String path = mPackageURI.getPath();
		String dir = path.substring(0, path.lastIndexOf(File.separator));
		// String fileName = entry.getName();
		String fileName = path.substring(path.lastIndexOf(File.separator) + 1);

		File apkFile = new File(dir + File.separator + "lca" + fileName.replace(".lca", ".apk"));

		InputStream is = null;
		OutputStream os = null;
		try {
			is = new BufferedInputStream(file.getInputStream(entry));
			os = new BufferedOutputStream(new FileOutputStream(apkFile));

			byte[] readBuffer = new byte[8192];
			int num;
			while ((num = is.read(readBuffer)) > 0) {
				os.write(readBuffer, 0, num);
			}

			os.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(is != null) {
					is.close();
				}
				if(os != null) {
					os.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return apkFile;
	}

	public String getInatllApkInfo(Context context, String archiveFilePath) {
		PackageManager pm = context.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(archiveFilePath, PackageManager.GET_ACTIVITIES);
		if(info != null) {
			ApplicationInfo appInfo = info.applicationInfo;
			return appInfo.packageName;
		}
		return null;
	}

	public void onClick(DialogInterface dialog, int which) {
		finish();
	}

	public void onCancel(DialogInterface dialog) {
		finish();
	}
	
	public static Map<String, String> getLcaInfo() {
		return mLcaInfo;
	}

	@Override
	public void onDestroy() {
		Log.i("yangmao_install", "onDestroy");
		Log.i(tag, installAppPackagename + " hased-----3");
		mIsDestory = true;
		//yangmao add
		mWorkerLooper.quit();
		super.onDestroy();
	}

	private String runResult(String str) {
		String line = "";
		StringBuilder sb = new StringBuilder();
		Runtime runtime = Runtime.getRuntime();
		try {
			Process process = runtime.exec(str);
			InputStream input = process.getInputStream();
			InputStreamReader reader = new InputStreamReader(input);
			BufferedReader br = new BufferedReader(reader);
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			br.close();
		} catch (IOException e) {
			Log.w("SerialUtil", "IOException", e);
		}
		return sb.toString().trim();
	}
	
	
	
	//yangmao add silence-install method start
	
	 private boolean checkInstallPackagesPermission(){
		   
		   if(this.checkCallingOrSelfPermission("android.permission.INSTALL_PACKAGES")==0){
			   Log.i("yangmao_install", "have install_packages permission ");
			   return true;
		   }else{
			   Log.i("yangmao_install", "not have install_packages permission ");
			   return false;
		   }
		   
		   
	   }
	   
		private boolean checkSystemPermission() {

			PackageInfo packageinfo = null;
			PackageManager packagemanager = this.getPackageManager();
			String packName = this.getPackageName();
			try {
				packageinfo = packagemanager.getPackageInfo(packName, 0);
			} catch (NameNotFoundException e) {
				return false;
			}
			ApplicationInfo appInfo = packageinfo.applicationInfo;
			if (appInfo != null) {
				Log.i("yangmao_install", "appinfo.flags is:"+appInfo.flags);
				if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
					Log.i(tag, "checkSystemPermission,  system app!!!");
					return true;
				}
			}
			Log.i("yangmao_install", "checkSystemPermission,not system app!!!");

			return false;
		}
	   
	   
		
		private boolean canSilentInstall(){
			
			if(checkSystemPermission() && checkInstallPackagesPermission ()){
				return true;
			}else{
				return false;
			}
			
		}
	
	
	
	    private void silenceInstall(String filePath,String pkName){
	    	Log.i("yangmao_install", "silenceInstall method");
			if(null != filePath){
	            Uri uri = Uri.fromFile(new File(filePath));
	            int installFlags = 0;
	            PackageManager pm = this.getPackageManager();
	            try {
	                PackageInfo pi = pm.getPackageInfo(pkName, PackageManager.GET_UNINSTALLED_PACKAGES);
	                if(pi != null) {
	                    installFlags = PackageManager.INSTALL_REPLACE_EXISTING;	                    
	                }else{
	                	Log.i("yangmao_install", "pi is null");
	                	showToast(DLG_PKG_PI_NOT_EXIST);
	                }
	            } catch (NameNotFoundException e) {
	            	
	            }
	            
	            PackageInstallObserver observer = new PackageInstallObserver();
	            pm.installPackage(uri, observer, installFlags, pkName);

	            
			}
	    	
	    }
	       
	    class PackageInstallObserver extends IPackageInstallObserver.Stub {
	        public void packageInstalled(String packageName, int returnCode) {
	        	Log.i("yangmao_install", "returnCode: " + returnCode);
	        	mInstallHandler.removeMessages(INSTALL_COMPLETE);
	    		Message msg = mHandler.obtainMessage(INSTALL_COMPLETE);
	    		msg.arg1 = returnCode;
	    		msg.obj = packageName;
	    		mInstallHandler.sendMessage(msg);
	        	
	        }
	    };
	    
	    
	private Handler mInstallHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case INSTALL_COMPLETE: {

				if (msg.arg1 == PackageManager.INSTALL_SUCCEEDED) {
					Log.i("yangmao_install","****************PackageInstallObserver INSTALL_COMPLETE****success!!!");
					showToast(INSTALL_SUCCEED);
					
				} else if (msg.arg1 == PackageManager.INSTALL_FAILED_INSUFFICIENT_STORAGE) {
					Log.i("yangmao_install","****************PackageInstallObserver INSTALL_COMPLETE****fail!!!");
					showToast(DLG_OUT_OF_SPACE);
				} else {
					Log.i("yangmao_install","****************PackageInstallObserver INSTALL_COMPLETE*******fail!!!");
					switch (msg.arg1) {
						case PackageManager.INSTALL_FAILED_INVALID_APK: {
							showToast(DLG_INSTALL_FAILED_INVALID_APK);
							break;
						}
						case PackageManager.INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES: {
							showToast(DLG_INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES);
							break;
						}
						case PackageManager.INSTALL_FAILED_OLDER_SDK: {
							showToast(DLG_INSTALL_FAILED_OLDER_SDK);
							break;
						}
						default: {
							showToast(DLG_INSTALL_FAILED_INVALID_APK);
							break;
						}
					}
				}

				break;
			}

			}
		}
	};
	    
	    

	    
	    
	    
	private void showToast(int id) {
		String message = "";
		switch (id) {

		case INSTALL_SUCCEED: {

			message = getString(R.string.lcapackageinstaller_install_succeed);
			break;
		}
		case DLG_PKG_PI_NOT_EXIST: {
			message = getString(R.string.lcapackageinstaller_Parse_error_dlg_text);
			break;
		}

		case DLG_INSTALL_FAILED_INVALID_APK: {
			message = getString(R.string.lcapackageinstaller_install_error);
			break;
		}
		case DLG_INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES: {
			message = getString(R.string.install_failed_inconsistent_certificates);
			break;
		}
		case DLG_INSTALL_FAILED_OLDER_SDK: {
			message = getString(R.string.install_failed_older_sdk);
			break;
		}
		default:
			break;
		}
		if (message != null && !message.equals(""))
			Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

	}
	   	    
	// yangmao add silence-install add end
	
	
	
}
