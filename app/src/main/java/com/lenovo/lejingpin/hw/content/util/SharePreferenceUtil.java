package com.lenovo.lejingpin.hw.content.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.lenovo.lejingpin.hw.content.data.HwConstant;

public class SharePreferenceUtil {
	private static final String TAG = "SharePreferenceUtil";
	private Context mContext;
	private static SharePreferenceUtil mUtil;
	private static SharedPreferences settings;
	private static String file_name = "hw_timeschedule_time";
	        
	private static String app_upgrade_time = "all_apps_upgrade_complete_time";
	private static String spere_app_time = "spere_apps_complete_time";
	private static String friend_app_time = "friend_apps_complete_time";
	private static String check_hawaii_time = "check_hawaii_time";
	         
	private static String app_num_special = "app_num_special";
	private static String app_num_top = "app_num_top";
	private static String app_num_spere = "app_num_spere";
	
	private static String first_enter_hawaii_time = "first_enter_hawaii_time";
	private static String first_launcher_app_upgrade_time = "first_launcher_app_upgrade_time";
	
	//
	private static String spere_app_update = "spere_app_update";
	private static String enter_hw = "enter_hw";
	
	public static final int FLAG_APP_UPGRADE = 0;
	public static final int FLAG_SPERE_APP = 1;
	public static final int FLAG_FRIEND_APP = 2;
	public static final int FLAG_CHECK_VERSION = 3;
	
	private ConnectivityManager mConnMgr;
	
	private long intervalTime = HwConstant.TIME_INTERVAL_HOUR;//8 * 60 * 60 * 1000;

	private SharePreferenceUtil(){}
	
	private SharePreferenceUtil(Context context){
		mContext = context;
		settings = mContext.getSharedPreferences(file_name, context.MODE_PRIVATE);
		mConnMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}
	
	public static SharePreferenceUtil getInstance(Context context){
		if(mUtil==null){
			mUtil = new SharePreferenceUtil(context);
		}
		return mUtil;
	}
	
	public void setPullSpereSwitch(boolean pullswitch){
		Editor editor = settings.edit();
		editor.putBoolean(enter_hw, pullswitch);
		editor.commit();
	}
	
	public boolean getPullSpereSwitch(){
		return settings.getBoolean(enter_hw, true);
	}
	public int getSpereUpdateState(){
		return settings.getInt(spere_app_update ,0);
	}
	
	public void setSpereUpdateState(int state){
		Editor editor = settings.edit();
		editor.putInt(spere_app_update, state);
		editor.commit();
	}
	
	private long getTime(String flag){
		String time = settings.getString(flag, 0+"");
		ContentManagerLog.d(TAG, " getTime >> flag : "+flag +" time : "+time);
		return Long.valueOf(time);
	}
	private void saveTime(String flag){
		ContentManagerLog.d(TAG, " saveTime >> flag : "+flag);
		Editor editor = settings.edit();
		editor.putString(flag, String.valueOf(System.currentTimeMillis()));
		editor.commit();
	}
	
	public void saveSpecailAppNum(){
		saveAppNum(app_num_special);
	}
	public void saveTopAppNum(){
		saveAppNum(app_num_top);
	}
	public void saveSpereAppNum(){
		saveAppNum(app_num_spere);
	}
	private void saveAppNum(String action){
		int n = getAppNum(action);
		Editor editor = settings.edit();
		editor.putInt(action, n+1);
		editor.commit();
	}
	public int getSpecailAppNum(){
		return getAppNum(app_num_special);
	}
	public int getTopAppNum(){
		return getAppNum(app_num_top);
	}
	public int getSpereAppNum(){
		return getAppNum(app_num_spere);
	}
	private int getAppNum(String action){
		return settings.getInt(action ,0);
	}
	
	public void removeSpecialAppNum(){
		removeAppNum(app_num_special);
	}
	public void removeTopAppNum(){
		removeAppNum(app_num_top);
	}
	
	public void removeSpereAppNum(){
		removeAppNum(app_num_spere);
	}
	
	private void removeAppNum(String action){
		settings.edit().remove(action);
	}
	
	public void saveFirstEnterHawaiiTime(){
		Editor editor = settings.edit();
		editor.putString(first_enter_hawaii_time, String.valueOf(System.currentTimeMillis()));
		editor.commit();
	}
	
	public boolean isEnterHawaii(){
		String time = settings.getString(first_enter_hawaii_time,"0");
		if("0".equals(time)){
			return false;
		}else{
			return true;
		}
	}
	
	private void saveFirstLauncherAppUpgradeTime(){
		ContentManagerLog.d(TAG, " saveFirstLauncherAppUpgradeTime ");
		Editor editor = settings.edit();
		editor.putString(first_launcher_app_upgrade_time, String.valueOf(System.currentTimeMillis()));
		editor.commit();
	}
	
	public void configLauncherAppUpgradeTime(){
		boolean isFirstUpgrade = isFirstUpgradeTime();
		if(isFirstUpgrade){
			saveFirstLauncherAppUpgradeTime();
		}
	}
	
	public String getFirstLauncherAppUpgradeTime(){
		return settings.getString(first_launcher_app_upgrade_time,"0");
	}
	
	private boolean isFirstUpgradeTime(){
		String time = getFirstLauncherAppUpgradeTime();
		if("0".equals(time)){
			return true;
		}else{
			return false;
		}
	}
	
	private long getAllAppUpgradeCompleteTime(){//
		return getTime(app_upgrade_time);
	}
	
	private long getSystemAppCompleteTime(){
		return getTime(spere_app_time);
	}
	
	private long getFriendAppCompleteTime(){
		return getTime(friend_app_time);
	}
	
	private void saveAllAppUpgradeCommpleteTime(){
		saveTime(app_upgrade_time);
	}
	
	private void saveSystemAppCompleteTime(){
		saveTime(spere_app_time);
	}
	
	private void saveFriendAppCommpleteTime(){
		saveTime(friend_app_time);
	}
	
	private boolean isCanDo(long time){
		long currentTime = System.currentTimeMillis();
		String connectType = getConnectType();
		Log.d(TAG, ">>>>>>>>>>currTime="+currentTime+", prefTime="+time+", intervalTime="+intervalTime);
		if("mobile".equals(connectType)){
			return (currentTime - time) >= HwConstant.TIME_INTERVAL_HOUR_MOBILE;
		}else{
			return (currentTime - time) >= intervalTime;
		}
	}
	
	public boolean canDo(int flag){
		boolean isCanDo = true;
		switch(flag){
			case FLAG_APP_UPGRADE:
				isCanDo =  isUpgradeCanDo();
				break;
			case FLAG_SPERE_APP:
				isCanDo =  isSystemCanDo();
				break;
			case FLAG_FRIEND_APP:
				isCanDo =  isFriendCanDo();
				break;
				default:
					ContentManagerLog.d(TAG, " default .");
		}
		
		ContentManagerLog.d(TAG, " canDo >> flag : "+flag +" isCanDo : "+isCanDo);
		
		return isCanDo;
	}
	
	public void save(int flag){
		ContentManagerLog.d(TAG, " save >> flag : "+flag);
		switch(flag){
		case FLAG_APP_UPGRADE:
			saveAllAppUpgradeCommpleteTime();
			break;
		case FLAG_SPERE_APP:
			saveSystemAppCompleteTime();
			break;
		case FLAG_FRIEND_APP:
			saveFriendAppCommpleteTime();
			break;
		default:
			ContentManagerLog.d(TAG, " default .");
		}
	}
	
	public long getIntervalTime(int flag){
		long time = 0;
		switch(flag){
			case FLAG_APP_UPGRADE:
				time = getUpgradeIntervalTime();
				break;
			case FLAG_SPERE_APP:
				time = getSystemIntervalTime();
				break;
			case FLAG_FRIEND_APP:
				time = getFriendIntervalTime();
				break;
			default:
				ContentManagerLog.d(TAG, " default .");
		}
		ContentManagerLog.d(TAG, " getIntervalTime >> flag : "+flag +" IntervalTime : "+time);
		return time;
	}
	
	public long getSpereTime(String flag){
		String time = settings.getString(flag, System.currentTimeMillis()+"");
		return Long.valueOf(time);
	}
	
	private long getIterval(long time){
		long currentTime = System.currentTimeMillis();
		String connectType = getConnectType();
		if("wifi".equals(connectType)){
			return intervalTime +  time - currentTime;
		}else if("mobile".equals(connectType)){
			return HwConstant.TIME_INTERVAL_HOUR_MOBILE +  time - currentTime;
		}
		return intervalTime +  time - currentTime;
	}
	
	private long getUpgradeIntervalTime(){
		long time = this.getAllAppUpgradeCompleteTime();
		return getIterval(time);
	}
	private long getSystemIntervalTime(){
		long time = this.getSystemAppCompleteTime();
		return getIterval(time);
	}
	private long getFriendIntervalTime(){
		long time = this.getFriendAppCompleteTime();
		return getIterval(time);
	}
	
	
	private boolean isUpgradeCanDo(){
		Long time = getAllAppUpgradeCompleteTime();
		return isCanDo(time);
	}
	
	private boolean isSystemCanDo(){
		Long time = getSystemAppCompleteTime();
		return isCanDo(time);
	}
	
	private boolean isFriendCanDo(){
		Long time = getFriendAppCompleteTime();
		return isCanDo(time);
	}
	
	private String getConnectType(){
        NetworkInfo info = mConnMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo infoM = mConnMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if(info.isConnected()){
        	return "wifi";
        }else if(infoM != null && infoM.isConnected()){
        	return "mobile";
        }
        return "other";
    }
	
	/**
	 * 是否显示帮助文档
	 * @param flag
	 */
	public void setShowHelp(boolean flag){
		Editor editor = settings.edit();
		editor.putBoolean("isHaveShowHelp", flag);
		editor.commit();
	}
	public boolean getShowHelp(){
		return settings.getBoolean("isHaveShowHelp", true);
	}
	
	private void saveCheckNewVersionTime(){
		Editor editor = settings.edit();
		editor.putString(check_hawaii_time, String.valueOf(System.currentTimeMillis()));
		editor.commit();
	}
	
	public boolean checkNewVersion(){
		String netType = getConnectType();
		if("other".equals(netType)){
			return false;
		}
		Long time  = Long.valueOf(getCheckNewVersionTime());
		if(time==0){
			saveCheckNewVersionTime();
			return false;
		}else{
			long interval = (System.currentTimeMillis() - time);
			if(interval > HwConstant.TIME_INTERVAL_CHECK_HAWAII){
				return true;
			}else{
				return false;
			}
		}
		
	}
	private String getCheckNewVersionTime(){
		return settings.getString(check_hawaii_time, "0");
	}
}
