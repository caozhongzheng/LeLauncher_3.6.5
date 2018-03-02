package com.lenovo.lejingpin.hw.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.lenovo.launcher.R;
import com.lenovo.lps.sus.EventType;
import com.lenovo.lps.sus.SUS;
import com.lenovo.lps.sus.SUSListener;

public class UpdateNewVersion {
	
	
	private static final String TAG = "yangmao";
	private Context mContext;
	
	
	public   UpdateNewVersion (Context context){
		
			 mContext = context;
	}
	

	public void  setListener(){
		Log.i(TAG, "SUS_setListener");
		SUS.setDebugModeFlag(true);
		
		SUS.setSUSListener(new SUSListener() {
			@Override
			public void onUpdateNotification(EventType eventType, String param) {
				// TODO Auto-generated method stub
				
				switch (eventType) {
				/** 版本更新过程启动失败的通知事件，原因：当前网络不可用 */
				case SUS_FAIL_NETWORKUNAVAILABLE:
					
					Toast.makeText(mContext,mContext.getResources().getString(R.string.SUS_MSG_FAIL_NETWORKUNAVAILABLE), Toast.LENGTH_SHORT).show();
					break;
				/** 版本更新过程启动失败的通知事件，原因：设置了仅在wifi模式下更新，但当前设备wifi没有打开 */
//				case SUS_FAIL_NOWIFICONNECTED:
//					break;
				/** 版本更新过程失败的通知事件，原因：存储空间不足 */
				case SUS_FAIL_INSUFFICIENTSTORAGESPACE:
					Toast.makeText(mContext,mContext.getResources().getString(R.string.SUS_MSG_INSUFFICIENTSTORAGESPACE), Toast.LENGTH_SHORT).show();
					break;
				/** 版本更新过程失败的通知事件，原因：下载目录不存在 */
				case SUS_FAIL_DOWNOLADFOLDER_FOLDER_NOTEXIST:
					
					break;
				/** 版本更新过程失败的通知事件，原因：SDCARD不存在 */
//				case SUS_FAIL_DOWNOLADFOLDER_SDCARD_NOTEXIST:
//					Toast.makeText(mContext,mContext.getResources().getString(R.string.check_sdcard_status_result), Toast.LENGTH_SHORT).show();
//					break;
				/** 版本更新过程失败的通知事件，原因：查询可更新版本时出现异常 */
//				case SUS_FAIL_QUERY_EXCEPTION:
//					break;
				/** 版本更新过程失败的通知事件，原因：下载过程出现异常 */
				case SUS_FAIL_DOWNLOAD_EXCEPTION:
					break;
				
				
				/** 已有版本更新过程在处理中的通知事件 */
				case SUS_WARNING_PENDING:
					break;
				
				
				/** 接收到版本更新的响应信息的通知事件 */
//				case SUS_RECEIVE_QUERYRESP:
//					break;
				/** 程序包下载开始的通知事件 */
				case SUS_DOWNLOADSTART:
					break;
				/** 程序包下载完成的通知事件 */
				case SUS_DOWNLOADCOMPLETE:
					break;
				

				/** 查询可更新版本的响应事件 */
				case SUS_QUERY_RESP:
					break;
				/** 当前版本已是最新的程序包的通知事件 */
//				case SUS_LATESTVERSION:
//					
//					if(!HwUiActivity.AU_MENU){
//						Toast.makeText(mContext,mContext.getResources().getString(R.string.SUS_MSG_LATESTVERSION), Toast.LENGTH_SHORT).show();
//					}
//					break;
				/** 在SUS系统中没有发现可更新的版本的通知事件 */
//				case SUS_NOTFOUND:
//
//					if(!HwUiActivity.AU_MENU){
//						Toast.makeText(mContext,mContext.getResources().getString(R.string.SUS_MSG_NOTFOUND), Toast.LENGTH_SHORT).show();
//					}
//					break;
				}
			
				//Toast.makeText(mContext,param, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	public void upGrade(){
		Log.i(TAG, "sus_upGrade");
		SUS.AsyncStartVersionUpdate(mContext);						
    }
	
	public void upGradeImmediately(){
		Log.i(TAG, "sus_upgradeImmediately");	
		
		if( !SUS.isVersionUpdateStarted() ) {
			SUS.AsyncStartVersionUpdate_IgnoreUserSettings(mContext);
		}
		
		
		
	}
	
	public void finishUpdate(){
		Log.i(TAG, "sus finish");
		SUS.finish();
	}

}
