package com.lenovo.launcher2.weather.widget;

import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.Debug.R5;
import com.lenovo.launcher2.gadgets.GadgetUtilities;
import com.lenovo.launcher2.weather.widget.settings.WeatherDetails;
import com.lenovo.launcher2.weather.widget.utils.WeatherUtilites;
import com.lenovo.launcher2.weather.widget.utils.Debug.R2;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class WidgetReceiver extends BroadcastReceiver {

	private final static String TAG = "WidgetReceiver";
	private static final String EXTRA_NETWORK_ENABLED = "network_enabled";
	private Context mcontext;
	private boolean misunlock = true;
	public static String FETE_THEME_STORE_IMAGE_BASE_PATH = "/mnt/sdcard/Android/data/com.lenovo.launcher/files/.themeImage/";
   /*RK_ID:RK_LEOSWIDGET AUT:kangwei DATE: 2012-10-17 START */
//	public static final String FETE_THEME_URL = "http://launcher.test.surepush.cn/launcher1/fetch.php?date=$";
	public static final String FETE_THEME_URL = "http://launcher.test.surepush.cn/launcher1/data/attachment/magicweather/fetetheme$.txt";
	private static final String THIS_PUSHMAILWIDGET_TASK =
			"com.lenovo.launcher2.taskmanager.widget.TaskManagerWidget";
   /*RK_ID:RK_LEOSWIDGET AUT:kangwei DATE: 2012-10-17 E */
	private static final String PREF_WEATHER_ANIM_SETTING = "pref_weather_anim_setting";
    ActivityManager mActivityManager;
	private static final String ACTION_TASKMANAGER_CLEAR_TASK = "com.lenovo.launcher.taskmanager.widget.clear";
	private static final String ACTION_TASKMANAGER_WORKSPACE_PAGE_UPDATE = "android.intent.action.WORKSPACE_PAGE_UPDATE";
	private static final String ACTION_TASKMANAGER_ADD_WIDGET = "com.lenovo.launcher.taskmanager.widget.add";
	private static final String ACTION_TASKMANAGER_VIEW_UPDATE = "com.lenovo.launcher2.taskmanager.widget.update";
	private static final String ACTION_TASKMANAGER_VIEW_STOP = "com.lenovo.launcher2.taskmanager.widget.stop";
	private static final String ACTION_TASKMANAGER_START_ANIMATION = "com.lenovo.launcher2.taskmanager.widget.start";
	private static final String ACTION_TASKMANAGER_WHITE_LIST_REFRESH = "com.lenovo.launcher2.taskmanager.widget.whitelist_refresh";
	byte[] mBuffer = new byte[1024];
	private static long mreadavailmem = 0;
	private static long mtotalmem = 0;
	private Handler mTaskHandler = null;
	private static final int UPDATE_TASKMANAGER_VIEW_MSG = 0x00001;
	private static final int KILL_TASKMANAGER_ALL_TASK_MSG = 0x00002;
	private static final int UPDATE_TASKMANAGER_ALL_TASK_MSG = 0x00003;
	private static final int TIPS_TASKMANAGER_TASK_MSG = 0x00004;
	private static final int UPDATE_TASKMANAGER_ALL_TASK_STOP_MSG = 0x00005;
	private static final int SEARCH_CITY_NET_NULL = 0x00006;
	private static final int SEARCH_CITY_NET_FAILD = 0x00007;
	private static final int SEARCH_CITY_NET_FAILD_REOPEN = 0x00008;
	private static final int START_WEATHER_ANIM = 0x00009;
	private static final int STOP_WEATHER_ANIM = 0x00010;
	private static final int SEARCH_CITY_NET_FAILD_NULL = 0x000011;
	private static final int REFRESHTIME = 5000;
	List<RunningAppProcessInfo> mprogresses;
	private long moldreadavailmem;
    private static final String[] EXCEPTION_RUNNING_LIST = {
        "com.lenovo.launcher",
        "com.lenovo.xlauncher",
        "com.android.settings",
        "com.lenovo.leos.widgets.weather",
        "com.lenovo.leos.hw",
        "com.lenovo.magicplus"
    };
    List <String >mlist = Arrays.asList(EXCEPTION_RUNNING_LIST);
    List <String > mHiddenList = new ArrayList<String>();
    private boolean misrefreshing = false;
    private boolean mistimeset = false;
    private boolean mislocationchanged = false;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		mcontext = context;
		if(intent!=null){
			initHandler(context);
			mHiddenList = getHiddenList();
			mActivityManager = (ActivityManager) mcontext.getSystemService(Activity.ACTIVITY_SERVICE);
			misrefreshing = false;
			String action = intent.getAction(); 
			if(action==null)
				return;
			R5.echo("WidgetReceiver********onReceive********action= "+action + "  unlock= "+misunlock);
			if(action.equals(Intent.ACTION_SCREEN_OFF)){
		    	misunlock = false;
		    	R2.echo("stop Update Time unclock="+misunlock);
		    }else if(action.equals(Intent.ACTION_SCREEN_ON)){
		    	misunlock = true;
		    	R2.echo("start Update Time unclock="+misunlock);
		    }
			if(WeatherUtilites.ACTION_WEATHER_WIDGET_SEVICE_RESTART.equals(action)){
				misunlock = true;
				mcontext.sendBroadcast(new Intent(WeatherUtilites.ACTION_TIEM_CHANGE));
    		}
			if(!misunlock){
            	return;
			}
			if(!WeatherUtilites.hasInstances(context,WeatherUtilites.THIS_WEATHER_WIDGET)&&
        			!WeatherUtilites.hasInstances(context,THIS_PUSHMAILWIDGET_TASK)
        			&&!action.equals(WeatherUtilites.ACTION_DELETE_LEOS_WIDGET)){
        		return;
			}
			if(WeatherUtilites.ACTION_ADD_WEATHER_WIDGET.equals(action)){
	    		updateWeatherDataIfNeeded(context);
	    		mcontext.sendBroadcast(new Intent(WeatherUtilites.ACTION_TIEM_CHANGE));
	    		mcontext.sendBroadcast(new Intent(WeatherUtilites.ACTION_UPDATE_WEATHER));
    		}
    		else if(action.equals(ACTION_TASKMANAGER_CLEAR_TASK)){
				mprogresses = mActivityManager.getRunningAppProcesses();
				mTaskHandler.sendEmptyMessage(KILL_TASKMANAGER_ALL_TASK_MSG);
				mTaskHandler.sendEmptyMessage(UPDATE_TASKMANAGER_VIEW_MSG);
			}else if(action.equals(ACTION_TASKMANAGER_ADD_WIDGET)){
				mTaskHandler.sendEmptyMessage(UPDATE_TASKMANAGER_ALL_TASK_MSG);
			}else if (action.equals(Intent.ACTION_TIME_CHANGED)
                    || action.equals(Intent.ACTION_TIME_TICK)||action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                R2.echo("Update Time");
		        if(!action.equals(Intent.ACTION_TIME_TICK)){
		        	mistimeset = true;
		        }
		    	if(WeatherUtilites.hasInstances(context,WeatherUtilites.THIS_WEATHER_WIDGET)){
		    		if(!action.equals(Intent.ACTION_TIME_TICK)){
		    			if(isFastDoubleClick(context)){
		    				return;
		    			}
		    			context.sendBroadcast(new Intent(WeatherUtilites.ACTION_TIEM_CHANGE));
		    		} else {
		    			context.sendBroadcast(new Intent(WeatherUtilites.ACTION_WEATHER_WIDGET_TIME_TICK));
		    		}
		        	updateWeatherDataIfNeeded(context);
		    	}
		    }else if (action.equalsIgnoreCase(ConnectivityManager.CONNECTIVITY_ACTION)) {
		    	if(WeatherUtilites.hasInstances(context,WeatherUtilites.THIS_WEATHER_WIDGET)){
		    		updateWeatherDataIfNeeded(context);
		    	}
		    }else if (action.equals(WeatherUtilites.ACTION_WEATHER_NETWORK_STATE_CHANGE)) {
		    	if(WeatherUtilites.hasInstances(context,WeatherUtilites.THIS_WEATHER_WIDGET)){
		        	boolean mNetworkEnabled = intent.getBooleanExtra(
		                    EXTRA_NETWORK_ENABLED, false);
		        	if(mNetworkEnabled){
		        		updateWeatherDataIfNeeded(context);
		        	}
		    	}
		    }else if(action.equals(WeatherUtilites.ACTION_DELETE_LEOS_WIDGET)){
		    	final String extra =intent.getStringExtra(WeatherUtilites.EXTRA_DELETE_LEOS_WIDGET);
		    	if(extra.equals(GadgetUtilities.WEATHERMAGICWIDGETVIEWHELPER)/*||
		    			extra.equals(GadgetUtilities.TASKMANAGERWIDGETVIEWHELPER)*/){
//		    		Intent i = new Intent();
//		        	stopService(i.setClass(context,WidgetService.class));
		    	}
		    }else if(action.equals(WeatherUtilites.ACTION_LOCATION_CHANGE)){
		    	if(WeatherUtilites.hasInstances(context,WeatherUtilites.THIS_WEATHER_WIDGET)){
		    		misrefreshing = false;
		    		mislocationchanged = true;
		    		UpdateWeatherIfNetworkEnabled(context);
		    	}
		    }
			else if(action.equals(ACTION_TASKMANAGER_WORKSPACE_PAGE_UPDATE))
		    	if(WeatherUtilites.hasInstances(context,THIS_PUSHMAILWIDGET_TASK))
		    		mTaskHandler.sendEmptyMessage(UPDATE_TASKMANAGER_ALL_TASK_MSG);
			else if(ACTION_TASKMANAGER_WHITE_LIST_REFRESH.equals(action)){
				if(WeatherUtilites.hasInstances(context,THIS_PUSHMAILWIDGET_TASK)){
		        	mHiddenList.clear();
		        	mHiddenList = getHiddenList();
				}
		    }/*else if(action.equals(Intent.ACTION_SCREEN_OFF)){
		    	misunlock = false;
		    	R2.echo("stop Update Time unclock="+misunlock);
		    }else if(action.equals(Intent.ACTION_SCREEN_ON)){
		    	R2.echo("start Update Time unclock="+misunlock);
		    	misunlock = true;
		    }*/else if(action.equals(WeatherUtilites.ACTION_WEATHER_ANIMATE_START)){
		    	if(mistimeset){
					mistimeset = false;
		    		updateWeatherDataIfNeeded(context);
					context.sendBroadcast(new Intent(WeatherUtilites.ACTION_TIEM_CHANGE));
				}
		    	mTaskHandler.sendEmptyMessage(START_WEATHER_ANIM);
		    }else if(action.equals(WeatherUtilites.ACTION_WEATHER_ANIMATE_STOP)){
		    	if(WeatherUtilites.hasInstances(context,THIS_PUSHMAILWIDGET_TASK))
		    		mTaskHandler.sendEmptyMessage(UPDATE_TASKMANAGER_ALL_TASK_STOP_MSG);
		    	if(WeatherUtilites.hasInstances(context,WeatherUtilites.THIS_WEATHER_WIDGET))
		    		mTaskHandler.sendEmptyMessage(STOP_WEATHER_ANIM);
		    }/*else if(action.equals(WeatherUtilites.ACTION_WEATHER_SCREEN_ANIMATE_START)){
		    	if(mistimeset){
					mistimeset = false;
		    		updateWeatherDataIfNeeded(context);
					context.sendBroadcast(new Intent(WeatherUtilites.ACTION_TIEM_CHANGE));
				}
		    	mTaskHandler.sendEmptyMessage(START_WEATHER_ANIM);
		    }else if(action.equals(WeatherUtilites.ACTION_WEATHER_SCREEN_ANIMATE_STOP)){
		    	if(WeatherUtilites.hasInstances(context,THIS_PUSHMAILWIDGET_TASK))
		    		mTaskHandler.sendEmptyMessage(UPDATE_TASKMANAGER_ALL_TASK_STOP_MSG);
		    	if(WeatherUtilites.hasInstances(context,WeatherUtilites.THIS_WEATHER_WIDGET))
		    		mTaskHandler.sendEmptyMessage(STOP_WEATHER_ANIM);
		    }*/
		}
	}
	
	 public  boolean isFastDoubleClick(final Context context) {
	       SharedPreferences prefers=context.getSharedPreferences(WeatherUtilites.STORAGED_WEATHER_INFO, 
	    		   Context.MODE_PRIVATE|Context.MODE_APPEND);
	       Editor edit=prefers.edit();
	       long lastClickTime=prefers.getLong("lastClickTime", 0);
	    	long time = System.currentTimeMillis();
	    	long timeD = time - lastClickTime;
	    	if ( 0 < timeD && timeD <5000) {
	    	  return true;
	    	}
	    	edit.putLong("lastClickTime",time);
	    	edit.commit();
	    	return false;
	  }
	
	private void initHandler(final Context context) {
		if(mTaskHandler!=null)
			return;
		mTaskHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch(msg.what) {
					/*case UPDATE_TASKMANAGER_VIEW_MSG:
						mreadavailmem = readAvailMem();
						Log.d(TAG,"UPDATE_TASK_VIEW_MSG readavailmem="+mreadavailmem);
						mtotalmem = readTotalMem();
						SendBroadcast(ACTION_TASKMANAGER_START_ANIMATION,mreadavailmem,mtotalmem);
						break;
					case KILL_TASKMANAGER_ALL_TASK_MSG:
						moldreadavailmem = readAvailMem();
						Log.d(TAG,"KILL_ALL_TASK_MSG oldreadavailmem="+moldreadavailmem);
						mTaskHandler.sendEmptyMessageDelayed(TIPS_TASKMANAGER_TASK_MSG,3000);
						killtask();
						mTaskHandler.sendEmptyMessageDelayed(UPDATE_TASKMANAGER_ALL_TASK_MSG,1000);
		    			break;				
					case UPDATE_TASKMANAGER_ALL_TASK_MSG:
						mreadavailmem = readAvailMem();
						Log.d(TAG,"UPDATE_ALL_TASK_MSG readavailmem="+mreadavailmem);
						mtotalmem = readTotalMem();
						SendBroadcast(ACTION_TASKMANAGER_VIEW_UPDATE,mreadavailmem,mtotalmem);
		    			break;
					case UPDATE_TASKMANAGER_ALL_TASK_STOP_MSG:
						mreadavailmem = readAvailMem();
						Log.d(TAG,"UPDATE_ALL_TASK_STOP_MSG readavailmem="+mreadavailmem);
						mtotalmem = readTotalMem();
						SendBroadcast(ACTION_TASKMANAGER_VIEW_STOP,mreadavailmem,mtotalmem);
						break;
					case TIPS_TASKMANAGER_TASK_MSG:
						final int releasemem = (int)(Math.abs((moldreadavailmem-readAvailMem())/(1024*1024)));
						if(releasemem>5){
							Toast.makeText(context, String.format(
									context.getString(R.string.taskmanager_release_mem_tips),
									releasemem), Toast.LENGTH_SHORT).show();
						}else{
							Toast.makeText(context,
									context.getString(R.string.taskmanager_no_release_mem_tips),
									Toast.LENGTH_SHORT).show();
						}
						break;*/
					case SEARCH_CITY_NET_NULL:
//						ShowToast(R.string.weaher_city_search_net_faild);
						break;
					case SEARCH_CITY_NET_FAILD:
						updateWeather(mcontext,true);
						break;
					case SEARCH_CITY_NET_FAILD_REOPEN:
						updateWeather(mcontext,false);
//						ShowToast(R.string.weaher_city_search_net_faild);
						break;
					case SEARCH_CITY_NET_FAILD_NULL:
						ShowToast(R.string.weaher_city_search_net_faild);
						break;
					case START_WEATHER_ANIM:
//						int celly = (Integer)msg.obj;
//	            		getScreenAnimation(context,WeatherUtilites.ACTION_WEATHER_SCREEN_ANIMATE_START,celly);
						getScreenAnimation(mcontext,WeatherUtilites.ACTION_WEATHER_SCREEN_ANIMATE_START);
						break;
					case STOP_WEATHER_ANIM:
						getScreenAnimation(mcontext,WeatherUtilites.ACTION_WEATHER_SCREEN_ANIMATE_STOP);
						break;
		    		default:
		    			break;
				}
			}
		};
	}
	protected void ShowToast(int weaherCitySearchNetFaild) {
		// TODO Auto-generated method stub
		Toast toast = Toast.makeText(mcontext,
	            mcontext.getString(weaherCitySearchNetFaild), Toast.LENGTH_SHORT);
//	    toast.setGravity(Gravity.CENTER, 0, 0);
	    toast.show();
	}
	private void killtask()
    {
    	for (ActivityManager.RunningAppProcessInfo process : mprogresses) 
    	{ 
    		final String proname = process.processName;
    		if(!mlist.contains(proname)&&!mHiddenList.contains(proname)){
    			mActivityManager.killBackgroundProcesses(proname);
    		}
    	}
    }
	private long readTotalMem() {
        try {
            long memtotal = 0;
            FileInputStream is = new FileInputStream("/proc/meminfo");
            int len = is.read(mBuffer);
            is.close();
            final int BUFLEN = mBuffer.length;
            for (int i=0; i<len && (memtotal == 0); i++) {
                if (matchText(mBuffer, i, "MemTotal")) {
                    i += 7;
                    memtotal = extractMemValue(mBuffer, i);
                } 
                while (i < BUFLEN && mBuffer[i] != '\n') {
                    i++;
                }
            }
            return memtotal;
        } catch (java.io.FileNotFoundException e) {
        } catch (java.io.IOException e) {
        }
        return 0;
    }
	private long readAvailMem() {
        try {
            long memFree = 0;
            long memCached = 0;
            FileInputStream is = new FileInputStream("/proc/meminfo");
            int len = is.read(mBuffer);
            is.close();
            final int BUFLEN = mBuffer.length;
            for (int i=0; i<len && (memFree == 0 || memCached == 0); i++) {
                if (matchText(mBuffer, i, "MemFree")) {
                    i += 7;
                    memFree = extractMemValue(mBuffer, i);
                } else if (matchText(mBuffer, i, "Cached")) {
                    i += 6;
                    memCached = extractMemValue(mBuffer, i);
                }
                while (i < BUFLEN && mBuffer[i] != '\n') {
                    i++;
                }
            }
            return memFree + memCached;
        } catch (java.io.FileNotFoundException e) {
        } catch (java.io.IOException e) {
        }
        return 0;
    }
	private boolean matchText(byte[] buffer, int index, String text) {
        int N = text.length();
        if ((index+N) >= buffer.length) {
            return false;
        }
        for (int i=0; i<N; i++) {
            if (buffer[index+i] != text.charAt(i)) {
                return false;
            }
        }
        return true;
    }
	private long extractMemValue(byte[] buffer, int index) {
        while (index < buffer.length && buffer[index] != '\n') {
            if (buffer[index] >= '0' && buffer[index] <= '9') {
                int start = index;
                index++;
                while (index < buffer.length && buffer[index] >= '0'
                    && buffer[index] <= '9') {
                    index++;
                }
                String str = new String(buffer, 0, start, index-start);
                return ((long)Integer.parseInt(str)) * 1024;
            }
            index++;
        }
        return 0;
    }
	private void SendBroadcast(String aciton,long avial,long total)
    {
    	Intent intent = new Intent(aciton);
    	intent.putExtra("avial", avial);
    	intent.putExtra("total", total);
    	mcontext.sendBroadcast(intent);
    }
	public List <String > getHiddenList() {
    	List <String > hlist = new ArrayList<String>();
    	SharedPreferences mPreferences = mcontext.getSharedPreferences("com.lenovo.launcher_taskmanager", Context.MODE_PRIVATE);
    	Set<String> HiddenList = mPreferences.getAll().keySet();
    	for(String list :HiddenList){
    		if(gettaskmanagerenabled(list)){
    			hlist.add(list);
    		}
    	}
    	return hlist;
    }
	public boolean gettaskmanagerenabled(String key ) {
        SharedPreferences sharedPreferences = mcontext.getSharedPreferences("com.lenovo.launcher_taskmanager",
        		Context.MODE_APPEND | Context.MODE_MULTI_PROCESS );
        return sharedPreferences.getBoolean(key, false);
    }
	private void updateWeatherDataIfNeeded(final Context context) {
		new Thread(){
			@Override
			public void run() {
		    	boolean mNetworkEnabled = SettingsValue.isNetworkEnabled(context);
		    	if(!mNetworkEnabled)
		    		return;
		    	String cityname = null;
		        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		        final int currenttime = (int)(calendar.getTimeInMillis()/3600000);
		        int oldcurrenttime = 0;
		
		    	if(!WeatherUtilites.findForPackage(context,WeatherUtilites.SINA_PACKAGENAME)){
		    		oldcurrenttime = (int)(WeatherUtilites.getupdatetime(context)/3600000);
		        	if(Math.abs(currenttime-oldcurrenttime)>=2){
		        		cityname =WeatherUtilites.getCityName(context,3);
			    		if(cityname==null||TextUtils.isEmpty(cityname))
				       		return;
		        		UpdateWeatherIfNetworkEnabled(context);
		        	}
		    	}
		    	else{
			    	final String time = WeatherUtilites.getWeatherDetailsAppUpdatetime(context);
			    	if(time!=null&&!TextUtils.isEmpty(time)){
				    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				    	try {
							Date date = formatter.parse(time);
							oldcurrenttime = (int)(date.getTime()/3600000);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			    	}
			    	if(currenttime-oldcurrenttime>=2){
					    		final int isConnected = WeatherUtilites.getNetWorkConnectState(context);
					    		if(isConnected!=0){
					    			cityname = WeatherUtilites.getWeatherDetailsAppName(context);
							    	if(cityname==null||TextUtils.isEmpty(cityname))
							       		return;
							    	Intent intent = new Intent(WeatherUtilites.ACTION_WEATHER_WIDGET_UPDATEDATA_FROMSINA);
							    	mcontext.startService(intent);
					    		}
			    			
			    	}
		    	}
			}
		}.start();
    }
	private void UpdateWeatherIfNetworkEnabled(final Context context) {
    	boolean mNetworkEnabled = SettingsValue.isNetworkEnabled(context);
    	Log.d(TAG,"mNetworkEnabled="+mNetworkEnabled);
    	if(mNetworkEnabled&&!misrefreshing){
    		new Thread() {
    			@Override
    			public void run() {
    				misrefreshing = true;
		    		final int isConnected = WeatherUtilites.getNetWorkConnectState(context);
		    		Log.d(TAG,"isConnected="+isConnected);
					if(isConnected!=0){
				       	final String cityname =WeatherUtilites.getCityName(context,1);
				       	
						List<WeatherDetails> weatherresults = WeatherUtilites.updateWeatherData(context,cityname);
						if(weatherresults!=null&&weatherresults.size()>0){
							final String name =  Settings.System.getString(context.getContentResolver(), "city_name");
							Log.i("ss", "REceiver UpdateWeatherIfNetworkEnabled settings name is " + name + " and cityname is " + cityname);
//							if(cityname.equals(name)){
								WeatherUtilites.saveWeather(context,weatherresults,cityname);
								mcontext.sendBroadcast(new Intent(WeatherUtilites.ACTION_UPDATE_WEATHER));
//							}
							misrefreshing = false;
						}else{
							misrefreshing = false;
							mTaskHandler.sendEmptyMessageAtTime(SEARCH_CITY_NET_FAILD,REFRESHTIME);
						}
					}else {
						misrefreshing = false;
						mTaskHandler.sendEmptyMessage(SEARCH_CITY_NET_NULL);
						if (mislocationchanged) {
							mislocationchanged = false;
							mTaskHandler.sendEmptyMessage(SEARCH_CITY_NET_FAILD_NULL);
							mcontext.sendBroadcast(new Intent(WeatherUtilites.ACTION_UPDATE_WEATHER_FAILED));
						}
					}
    			}
    		}.start();
    	}
    }
	private void updateWeather(final Context context,final boolean repeate)
    {
    	boolean mNetworkEnabled = SettingsValue.isNetworkEnabled(context);
    	if(mNetworkEnabled&&!misrefreshing){
    		new Thread() {
    			@Override
    			public void run() {
    				misrefreshing = true;
		    		final int isConnected = WeatherUtilites.getNetWorkConnectState(context);
					if(isConnected!=0){
						final String cityname =WeatherUtilites.getCityName(context,1);
						List<WeatherDetails> weatherresults = WeatherUtilites.updateWeatherData(context,cityname);
						if(weatherresults!=null&&weatherresults.size()>0){
							WeatherUtilites.saveWeather(context,weatherresults,cityname);
							mcontext.sendBroadcast(new Intent(WeatherUtilites.ACTION_UPDATE_WEATHER));
							misrefreshing = false;
						}
						else{
							if(repeate){
								misrefreshing = false;
								mTaskHandler.sendEmptyMessageAtTime(SEARCH_CITY_NET_FAILD_REOPEN,REFRESHTIME);
							} else {
								if (mislocationchanged) {
									mislocationchanged = false;
									mTaskHandler.sendEmptyMessage(SEARCH_CITY_NET_FAILD_NULL);
									mcontext.sendBroadcast(new Intent(WeatherUtilites.ACTION_UPDATE_WEATHER_FAILED));
								}
							}
						}
					}else {
						misrefreshing = false;
						mTaskHandler.sendEmptyMessage(SEARCH_CITY_NET_NULL);
					}
    			}
    		}.start();
    	}
    }
	private void getScreenAnimation(final Context context,String action)
    {
		R2.echo("getScreenAnimation= "+action);
    	if(getweatheranimsettingenabled()){
    		R2.echo("getScreenAnimation= satrtService ");
			Intent intent = new Intent(action);
			intent.setPackage("com.lenovo.leos.weatheranimation");
			intent.setClassName("com.lenovo.leos.weatheranimation", "com.lenovo.leos.weatheranimation.WeatherAnimService");
			PackageManager mPackageManager = context.getPackageManager();
			List<ResolveInfo> list = mPackageManager.queryIntentServices(
			        intent, 0);
			if (list.size()>0) {
				try{
					context.startService(intent);
				}catch(SecurityException e){
					e.printStackTrace();
				}
			}
    	}
    }
	private void getScreenAnimation(final Context context,String action,int celly)
    {
    	if(getweatheranimsettingenabled()){
			Intent intent = new Intent(action);
			intent.setPackage("com.lenovo.leos.weatheranimation");
			intent.setClassName("com.lenovo.leos.weatheranimation", "com.lenovo.leos.weatheranimation.WeatherAnimService");
			intent.putExtra("celly", celly);
			PackageManager mPackageManager = context.getPackageManager();
			List<ResolveInfo> list = mPackageManager.queryIntentServices(
			        intent, 0);
			if (list.size()>0) {
				try{
					context.startService(intent);
				}catch(SecurityException e){
					e.printStackTrace();
				}
			}
    	}
    }
	public boolean getweatheranimsettingenabled() {
		SharedPreferences sharedPreferences = mcontext.getSharedPreferences(SettingsValue.PERFERENCE_NAME,
				Context.MODE_APPEND | Context.MODE_MULTI_PROCESS );
		return sharedPreferences.getBoolean(PREF_WEATHER_ANIM_SETTING, true);
	}
}
