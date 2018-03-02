package com.lenovo.launcher2.gadgets.Lotus;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.CallLog.Calls;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;
import com.lenovo.launcher.components.XAllAppFace.XLauncher;

import com.lenovo.launcher2.commoninterface.ApplicationInfo;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.Debug.R2;

/** 四叶草内置版主文件。
 * Author : ChengLiang
 * */

// FOR TEST ONLY NOW
public class LotusDefaultViewHelper {
	private static final String TAG = "LotusDefaultViewHelper";

    private XLauncher mContext;
    
    /** 四叶草信息SharedPreferences */
    private SharedPreferences mPreferences;
    /** 四叶草图标SharedPreferences */
    private SharedPreferences mAppiconPreferences;

    /** 当下的内置四叶草view */
	private View holdedView;
	
    /** 叶心图片view */
    private ImageView iAppStore;
    /** 叶片背景view */
    private final View[] mLeafView = new View[LotusUtilites.TOTAL_LEAF];
    /** 叶片标题文本view */
	private final TextView[] mTextView = new TextView[LotusUtilites.TOTAL_LEAF];
    /** 叶片图标view */
    private final ImageView[] mImageView = new ImageView[LotusUtilites.TOTAL_LEAF];
    /** 叶片未接文本view */
    private final TextView[] mMissedNum = new TextView[LotusUtilites.TOTAL_LEAF];

    /** 四叶草应用无效图标 */
    public Drawable iconunAvailabe;

    /** 新短信数目 */
    private int newSmsCount;
    /** 新彩信数目 */
    private int newMmsCount;
    /** 新未接来电数目 */
    private int missedCallCount;
    
    /** 是否完成四叶草静态数据的初始化 */
    private static boolean hasFilled = false;
    /** 通话数据库监视器处理子线程 */
    private static HandlerThread mProcessThreadCall = new HandlerThread("CallContentObserver");
    /** 信息数据库监视器处理子线程 */
    private static HandlerThread mProcessThreadSms = new HandlerThread("SmsContentObserver");

    /** 主线程handler */
    private Handler mHandler = new Handler();

    /** 应用卸载广播收音机 */
	private UnInstallReceiver mUnInstallReceiver;
    /** t卡应用有效无效广播收音机 */
	private ExternalAppReceiver mExternalAppReceiver;
	/** 基础广播收音机:接收全部重置、叶子变更、叶子重置、语言变更、刷新、桌面结束等广播 */
	private MissedInfoReceiver mMissedInfoReceiver;
	
    /** 通话数据库监听器handler */
    private CallContentObserverHandler mCallHandler;
    /** 信息数据库监听器handler */
    private SmsContentObserverHandler mSmsHandler;
    /** 信息数据库监听器 */
    private newMmsContentObserver mMmsContentObserver;
    /** 通话数据库监听器 */
    private newCallsContentObserver mCallsContentObserver;
    
    


    static {
        mProcessThreadCall.start(); 
        mProcessThreadSms.start();
    }
    
	public LotusDefaultViewHelper(final XLauncher launcher) {
		mContext = launcher;
	}

	/** 关键方法：取四叶草内置view */
	public View getHoldedView() {
		Log.i(TAG, "-----------------------getHoldedView=");

        iconunAvailabe = mContext.getResources().getDrawable(R.drawable.lotus_for_others);

        //处理静态数据
		mPreferences = mContext.getSharedPreferences(LotusUtilites.LOTUSINFO, Activity.MODE_APPEND | Activity.MODE_MULTI_PROCESS);
        mAppiconPreferences = mContext.getSharedPreferences(LotusUtilites.LOTUSICON, Activity.MODE_APPEND  | Activity.MODE_MULTI_PROCESS);
        LotusUtilites.getIntentStr(mPreferences);
        initLotusUtilitesRomData();

        //处理数据库见识
        registerSmsContentObserver();
        registerCallContentObserver();

        //处理广播收音机
		try {
			mMissedInfoReceiver = new MissedInfoReceiver();
            registerReceiver(mContext, mMissedInfoReceiver);
            
			mUnInstallReceiver = new UnInstallReceiver();
            registerReceiver(mContext, mUnInstallReceiver);
            
			mExternalAppReceiver = new ExternalAppReceiver();
			registerReceiver(mContext, mExternalAppReceiver);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

        //处理四叶草view
        holdedView = View.inflate(mContext, R.layout.gadget_lotus_face_base, null);
        createLotusView();
        updateLotusView();
        return holdedView;
	}

    public void onDetachedToWindow( final Context context) {
        Log.i(TAG, "onDetachedToWindow");

        /***RK_ID:RK_LOTUS_BUGFIX_1928 AUT:zhanglz1@lenovo.com. S***/        
        unregisterReceiver(context, mMissedInfoReceiver);
        unregisterReceiver(context, mUnInstallReceiver);
        unregisterReceiver(context, mExternalAppReceiver);
        /***RK_ID:RK_LOTUS_BUGFIX_1928 AUT:zhanglz1@lenovo.com. E***/ 

        unRegisterContentObserver();

        hasFilled = false;
    }
    
    /** 基础广播收音机:接收全部重置、叶子变更、叶子重置、语言变更、刷新、桌面结束等广播 */
    private class MissedInfoReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			R2.echo("Action is : " + action);
			
			/*** RK_ID:RK_BUGFIX_169053 AUT:zhanglz1@lenovo.com. DATE:2012-08-15. S ***/
			//长按叶子时隐藏未接数据显示
			if (LotusUtilites.longClickIndex >= 0
			        && LotusUtilites.longClickIndex < LotusUtilites.TOTAL_LEAF
			        && mMissedNum[LotusUtilites.longClickIndex] != null
					&& (mMissedNum[LotusUtilites.longClickIndex].getVisibility() == View.VISIBLE)) {
				mMissedNum[LotusUtilites.longClickIndex].setVisibility(View.INVISIBLE);
			}
			/*** RK_ID:RK_BUGFIX_169053 AUT:zhanglz1@lenovo.com. DATE:2012-08-15.E ***/
			
            //处理叶子变更广播:选择程序列表中选择一个
			if (action.equals(ShortcutActivityPicker.ACTION_LOTUS_CHANGE)) {
				R2.echo("Intent is : " + intent.toUri(0));
				addLotusPageShortcuts(LotusUtilites.longClickIndex, intent);
			}
            //处理全部重置广播：选择程序列表菜单中选择重设全部
			else if (action.equals(ShortcutActivityPicker.ACTION_LOTUS_ALL_RESET)) {
				for (int i = 0; i < LotusUtilites.TOTAL_LEAF; i++) {
					resetLotusPageInfo(i, true);
					Intent data = LotusUtilites.getLotusPageInfo(mPreferences,i, true);
					addLotusPageShortcuts(i, data);
				}
			}
            //处理叶子重置广播：选择程序列表菜单中选择重设该叶片
			else if (action.equals(ShortcutActivityPicker.ACTION_LOTUS_PAGENUM_RESET)) {
				Log.e(TAG, "---------------MissedInfoReceiver-- ACTION_LOTUS_PAGENUM_RESET "
						+ LotusUtilites.longClickIndex);
				resetLotusPageInfo(LotusUtilites.longClickIndex, true);
				/***RK_ID:RK_LOTUS_CUSTOMZ_1897  AUT:zhanglz1@lenovo.com. S**/
    			Intent data = LotusUtilites.getLotusPageInfo(mPreferences,LotusUtilites.longClickIndex, true);
    			addLotusPageShortcuts(LotusUtilites.longClickIndex, data);
                /***RK_ID:RK_LOTUS_CUSTOMZ_1897  AUT:zhanglz1@lenovo.com. E**/ 
			}
            //处理语言变更广播：系统设置菜单中选择更改语言
			else if(action.equals(Intent.ACTION_LOCALE_CHANGED)){
				for (int i = 0; i < LotusUtilites.TOTAL_LEAF; i++) {
				    refreshLotusForLocalChange(i);
				}
			}
			/*RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2012-03-27 . S*/
            //处理重绘广播：更换主题
			else if (SettingsValue.ACTION_REFRESH_LOTUS.equals(action)) {
				refreshLotusForTheme();
				for (int i = 0; i < LotusUtilites.TOTAL_LEAF; i++) {
				    refreshLotusforIconStyle(i);
				}
			}
			/*RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2012-03-27 . S*/
            //处理桌面结束广播：更换图标装饰
			else if (SettingsValue.ACTION_FOR_LAUNCHER_FINISH.equals(action)) {
			    Log.e(TAG, "---------------ACTION_FOR_LAUNCHER_FINISH");
				for (int i = 0; i < LotusUtilites.TOTAL_LEAF; i++) {
				    refreshLotusforIconStyle(i);
				}
			}

			refreshLotusView();
		}
	}

    /** 应用卸载广播收音机 */
    private class UnInstallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
                //处理应用移除广播
                Log.i(TAG, "-------------------------ACTION_PACKAGE_REMOVED====" +intent.getDataString());
                
                /***RK_ID:RK_LOTUS_BUGFIX_1928 AUT:zhanglz1@lenovo.com. S***/     
                //设置sd卡应用有效？
                LotusUtilites.setIsSdcardAppAvalible(true);
                /***RK_ID:RK_LOTUS_BUGFIX_1928 AUT:zhanglz1@lenovo.com. E***/       
                //取被卸载的包名
                String pName = intent.getData().getSchemeSpecificPart();
                resetLotusPageInfoToDefault(pName);
            }
        }
    }

    /** t卡应用有效无效广播收音机 */
    private class ExternalAppReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            //开机一会儿以后sd卡上应用可用时
            if (action.equals(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE)) {
                //处理外部应用有效广播
                /***RK_ID:RK_LOTUS_BUGFIX_1928 AUT:zhanglz1@lenovo.com. S***/        
                //设置t卡应用无效？
                LotusUtilites.setIsSdcardAppAvalible(false);
                /***RK_ID:RK_LOTUS_BUGFIX_1928 AUT:zhanglz1@lenovo.com. E***/        
                for (int i = 0; i < LotusUtilites.TOTAL_LEAF; i++) {
                 //   Drawable ics = mImageView[i].getDrawable();
                  //  if (ics != null && ics.equals(iconunAvailabe)) {
                        // reset the page
                        Intent data = LotusUtilites.getLotusPageInfo(mPreferences,i, false);
                        addLotusPageShortcuts(i, data);
                  //  }
                }
            } else if (action.equals(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE)) {
                //处理外部应用无效广播
                /***RK_ID:RK_LOTUS_BUGFIX_1928 AUT:zhanglz1@lenovo.com. S***/        
                //设置t卡应用无效？
                LotusUtilites.setIsSdcardAppAvalible(false);
                /***RK_ID:RK_LOTUS_BUGFIX_1928 AUT:zhanglz1@lenovo.com. E***/   
                /*  
                for (int i = 0; i < LotusUtilites.TOTAL_LEAF; i++) {
                    PackageManager packagemanager = mContext.getPackageManager();
                    ResolveInfo resolveInfo = null;
                    Intent data = getLotusPageInfo(i);
                    if (data != null)
                        resolveInfo = packagemanager.resolveActivity(data, 0);
                    if (data != null && resolveInfo == null) {
                        // setLotusPageToLoading(i);
                        //resetLotusPageInfo(i, true);
                    }
                }
                */
            }
        }
    }

    /** 注销广播收音机 */
    private static void unregisterReceiver(Context context, BroadcastReceiver receiver) {

		if (context != null && context.getApplicationContext()!=null) {
			try{
			   context.getApplicationContext().unregisterReceiver(receiver);
			} catch (Exception e) {
				return;
			}
		}
	}

    /** 注册广播收音机 */
	private void registerReceiver(Context context, BroadcastReceiver receiver) {
		R2.echo("Register : " + context + "  , receiver : " + receiver);

		if ((context != null) && (receiver != null)) {
			if(receiver.equals(mMissedInfoReceiver)){
				IntentFilter missedInfoFilter = new IntentFilter();
				Log.e(TAG, "registerReceiver");
				missedInfoFilter.addAction(ShortcutActivityPicker.ACTION_LOTUS_ALL_RESET);
				missedInfoFilter.addAction(ShortcutActivityPicker.ACTION_LOTUS_CHANGE);
				missedInfoFilter.addAction(ShortcutActivityPicker.ACTION_LOTUS_PAGENUM_RESET);
				missedInfoFilter.addAction(Intent.ACTION_LOCALE_CHANGED);
				missedInfoFilter.addAction(SettingsValue.ACTION_REFRESH_LOTUS);
				missedInfoFilter.addAction(SettingsValue.ACTION_FOR_LAUNCHER_FINISH);
				context.getApplicationContext().registerReceiver(receiver, missedInfoFilter);
			}else if(receiver.equals(mExternalAppReceiver)){
				IntentFilter externalfilter = new IntentFilter();
				externalfilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
				externalfilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
				context.getApplicationContext().registerReceiver(receiver, externalfilter);
			}else if(receiver.equals(mUnInstallReceiver)){
				IntentFilter unInstallfilter = new IntentFilter();
				unInstallfilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
				unInstallfilter.addDataScheme("package");
				context.getApplicationContext().registerReceiver(receiver, unInstallfilter);
			}
		}
	}

    // this must be called after the fetch enabling info
	/** 看指定叶子是否允许被改动 */
    private boolean checkLeafChangableByLeafNum(int leafNum) {
        initLotusUtilitesRomData();
        return LotusUtilites.sfPrefKeyMapEnableChange[leafNum];
    }

	// when receive the PACKGE_REMOVED message, check the data and reset to
	// default
    /** 当有包卸载时重置四叶草相关内容 */
	private void resetLotusPageInfoToDefault(String packageName) {
		if (packageName == null){
			return;
		}
		
		PackageManager packagemanager = null;
		if(mContext != null){
			packagemanager = mContext.getPackageManager();
		}
		
		ResolveInfo resolveInfo = null;
		for (int i = 0; i < LotusUtilites.TOTAL_LEAF; i++) {
			Intent data = LotusUtilites.getLotusPageInfo(mPreferences,i, false);
			/*** RK_ID:RK_LOTUS_BUGFIX_163586 AUT:zhanglz1@lenovo.com. S ***/
			if (data != null && data.getComponent() != null) {
			    if(packagemanager != null){
			        resolveInfo = packagemanager.resolveActivity(data, 0);
			    }else{
			        resolveInfo = null;
			    }
			    
				if (resolveInfo == null) {
					resetLotusPageInfo(i, true);
				}
			}
			/*** RK_ID:RK_LOTUS_BUGFIX_163586 AUT:zhanglz1@lenovo.com. S ***/
		}
		
		initLotusFirstPageInfo();
		
		refreshLotusView();
	}

	/** 重置四叶草为单行文本，传入叶子序号和是否单行 */
	private void resetTextViewSignal(int index, boolean singleline) {
		if (index >= 0 && index < LotusUtilites.TOTAL_LEAF) {
			if (mTextView[index] != null) {
				mTextView[index].setSingleLine(singleline);
			}
		}
	}

	/** 指定叶片添加或替换应用，传入叶子序号 */
	private void addLotusPageInfo(int pageNum) {
		Intent intent = new Intent(LotusUtilites.ACTION_PICK_SHORTCUT);
		intent.setPackage(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF);
        //bugfix 12633
		ArrayList<String> addedPackageAndClassName = getLotusPagePackageAndClassName(pageNum);
		intent.putStringArrayListExtra(LotusUtilites.EXTRA_APPCLASSNAME, addedPackageAndClassName);
		intent.putExtra("PAGENUM", pageNum);
		mContext.startActivityForResult(intent, 0);
	}

	/** 取当前被选中的所有应用，传入要更改的叶子序号 */
	private ArrayList<String> getLotusPagePackageAndClassName(int pageNum) {
		ArrayList<String> packageAndClassNameList = new ArrayList<String>();
		for (int i = 0; i < LotusUtilites.TOTAL_LEAF; i++) {
			Intent data = LotusUtilites.getLotusPageInfo(mPreferences,i, false);
			// if (i == pageNum){
			// Log.i(TAG, "------------------i == pageNum");
			// continue;
			// }
			if (data != null && data.getComponent() != null) {
				String packageAndClassName = data.getComponent().getPackageName()+data.getComponent().getClassName();
				packageAndClassNameList.add(packageAndClassName);
			}else {
				Log.i(TAG, "------------------getLotusPageClassName:" + i);
			}
		}
		return packageAndClassNameList;
	}

	/** 设置叶片数据到静态数据（包名、类名） */
	private void setLotusPageInfo(int pagenum, Intent data) {
		/*** RK_ID:RK_REDSUQARE_1745 AUT:zhanglz1@lenovo.com. ***/
		// final SharedPreferences preferences =
		// this.mContext.getSharedPreferences(
		// LOTUSINFO, 0);
		final SharedPreferences.Editor editor = mPreferences.edit();
		if (data == null) {
			R2.echo("Name -is1 :  " + LotusUtilites.sfPrefKeyMapTarget[pagenum]);
			editor.putString(LotusUtilites.sfPrefKeyMapTarget[pagenum], null);
			/*if(!LotusUtilites.isSolidExit(pagenum)){
				editor.putString(LotusUtilites.sfPrefKeyMapSolid[pagenum], null);
			}*/
		} else {
			String name = data.getComponent().getPackageName() + File.separator
					+ data.getComponent().getClassName();
			R2.echo("Name -is :  " + name + "  , pagenum : " + LotusUtilites.sfPrefKeyMapTarget[pagenum]
					+ " , ");
			editor.putString(LotusUtilites.sfPrefKeyMapTarget[pagenum], name);
			/*if(!LotusUtilites.isSolidExit(pagenum)){
				editor.putString(LotusUtilites.sfPrefKeyMapSolid[pagenum], name);
			}*/
		}
		editor.apply();
	}

	// init lotus'leafs
	/** 初始化四叶草内容 */
	private void initLotusFirstPageInfo() {
		/*** RK_ID:RK_LONGPRESS_INFO AUT:zhanglz1@lenovo.com. 2012-03-03 ***/
		// Toast.makeText(mContext, R.string.inform_lotus_can_change,
		// Toast.LENGTH_SHORT).show();
		for (int i = 0; i < LotusUtilites.TOTAL_LEAF; i++) {
			Intent data = LotusUtilites.getLotusPageInfo(mPreferences,i, false);
			addLotusPageShortcuts(i, data);
		}
	}

	/** 重置四叶草叶子数据（包名、类名），传入叶子序号、t卡是否有效，返回是否有恢复默认 */
	private boolean resetLotusPageInfo(int pageNum, boolean token) {
		/*** RK_ID:RK_REDSUQARE_1745 AUT:zhanglz1@lenovo.com. ***/
		if(!token){
        	boolean sdcardStateRemoved = Environment.getExternalStorageState().equals(
    				Environment.MEDIA_REMOVED);
        	if(!sdcardStateRemoved){
    			return false;
        	}
        }

		final SharedPreferences.Editor editor = mPreferences.edit();
		editor.putString(LotusUtilites.sfPrefKeyMapTarget[pageNum], mPreferences.getString(LotusUtilites.sfPrefKeyMapSolid[pageNum], ""));
		editor.commit();
		
		return true;
	}
	
	/** 重置所有叶子内容 */
	private void resetAllLotusPageInfo() {
		Log.e(TAG, "resetAllLotusPageInfo");
		for (int i = 0; i < LotusUtilites.TOTAL_LEAF; i++) {
			if (checkLeafChangableByLeafNum(i)){
				resetLotusPageInfo(i, true);
		    }
		}
	}

	/** 设置某叶片的显示，传入叶子序号、数据 */
	private void addLotusPageShortcuts(final int pageNum, Intent data) {
        //记录叶子应用的intent
        Intent itLocal = new Intent();
        String empty = "";
        String packageName = empty;
        String className = empty;

        //提取叶子应用存入intent里
        if (data == null) {
        }else if (data.getStringExtra("className") != null) {
            packageName = data.getStringExtra("packageName");
            className = data.getStringExtra("className");
		} else if(data.getComponent() != null){
            packageName = data.getComponent().getPackageName();
            className = data.getComponent().getClassName();
		}
        
        //取叶子应用的解决信息
        PackageManager packagemanager = mContext.getPackageManager();
        itLocal.setClassName(packageName, className);
		ResolveInfo resolveInfo = packagemanager.resolveActivity(itLocal, 0);
		if (className.contains(LotusUtilites.FAKE_LE_FAMILY)){
            setLotusPageInfo(pageNum, itLocal);
		}else if(resolveInfo != null){
            setLotusPageInfo(pageNum, itLocal);
		}else if(LotusUtilites.findIntentSolid(className) >= 0){
            setLotusPageInfo(pageNum, itLocal);
		}else{
            // maybe the app in SD was not available
            if(!resetLotusPageInfo(pageNum, LotusUtilites.sIsSdcardAppAvalible)){
                setLotusPageInfo(pageNum, itLocal);
            }else{
                itLocal = LotusUtilites.getLotusPageInfo(mPreferences,pageNum, false);

                if(itLocal != null && itLocal.getComponent() != null){
                    packageName = itLocal.getComponent().getPackageName();
                    className = itLocal.getComponent().getClassName();
                    resolveInfo = packagemanager.resolveActivity(itLocal, 0);
                }else{
                    packageName = empty;
                    className = empty;
                    resolveInfo = null;
                }
                //再来一次
                if (className.contains(LotusUtilites.FAKE_LE_FAMILY)){
                }else if(resolveInfo != null){
                }else if(LotusUtilites.findIntentSolid(className) >= 0){
                }else if(!className.isEmpty()){
                }else{
                    itLocal = null;
                    setLotusPageInfo(pageNum, itLocal);
                }
            }
		}
		
		Drawable icon = null;
        String label = null;
		
        //是乐应用的情况。
        if (className.contains(LotusUtilites.FAKE_LE_FAMILY)) {
            icon = resolveAppIconDrawable(className, null, R.drawable.lotus_lefamily);

            //如果fake是默认配置着的，取默认资源
            for(int i = 0; i < LotusUtilites.TOTAL_LEAF; i++){
                if (LotusUtilites.checkIntentSolid(i, className)) {
                    if(LotusUtilites.app_name_first[i] != null){
                        label = LotusUtilites.app_name_first[i];
                    }
                }
            }
            if(label == null || label.isEmpty()){
                label = mContext.getResources().getString(R.string.title_lefamily);
            }
        }
		//如果系统有这个应用的情况
        else if (resolveInfo != null) {
            refreshMissedNum(className, pageNum);

            icon = resolveAppIconDrawable(className, resolveInfo, R.drawable.lotus_for_others);

            //先用lbk标题、再是缺省标题、最后是异常图标。
            for(int i = 0; i < LotusUtilites.TOTAL_LEAF; i++){
                if (LotusUtilites.checkIntentSolid(i, className)) {
                    if(LotusUtilites.app_name_first[i] != null){
                        label = LotusUtilites.app_name_first[i];
                    }
                }
            }
            if(label == null || label.isEmpty()){
                label = (String)(resolveInfo.loadLabel(packagemanager));
            }
            if(label == null || label.isEmpty()){
                label = resolveInfo.activityInfo.name;
            }
            if(label == null || label.isEmpty()){
                label = mContext.getResources().getString(R.string.lotus_default);
            }
		}
        //是默认应用，但是系统没有这个应用或是fake应用的情况。
		else if (LotusUtilites.findIntentSolid(className) >= 0) {
			icon = resolveAppIconDrawable(className, null, R.drawable.lotus_for_others);

            //如果fake是默认配置着的，取默认资源
            for(int i = 0; i < LotusUtilites.TOTAL_LEAF; i++){
                if (LotusUtilites.checkIntentSolid(i, className)) {
                    if(LotusUtilites.app_name_first[i] != null){
                        label = LotusUtilites.app_name_first[i];
                    }
                }
            }
            if(label == null || label.isEmpty()){
                label = mContext.getResources().getString(R.string.lotus_default);
            }
		}
        //sdcard移除时暂存的应用
        //test by dining 2013-05-28 
		else if(!className.isEmpty()/* && LotusUtilites.checkIntentSolid(pageNum, className)*/){
            icon = resolveAppIconDrawable(className, null, R.drawable.lotus_for_others);

            label = "";/*mContext.getResources().getString(R.string.lotus_click_download);*/
		}
        //空白的情况，恢复成添加状态
		else{
            icon = resolveAppIconDrawable(null, null, R.drawable.add_lotus);

            label = mContext.getResources().getString(R.string.add_new);
            //test by dining ,remove form the database,
            if(!className.isEmpty()){
            	LotusUtilites.clearLotusPageTargetInfo(mPreferences,pageNum);
            }
		}

        setLotusLableAndIcon(pageNum, icon, label);

        resetTextViewSignal(pageNum, true);
	}
	
    /** 设置四叶草的标题和图标 */
	private void setLotusLableAndIcon(int pageNum, Drawable iconDrawable, CharSequence name){
        if(iconDrawable!=null){
            mImageView[pageNum].setImageDrawable(iconDrawable);
        }
        if(name != null){
            mTextView[pageNum].setText(name);
        }
	}
	
//IF(DESKTOP_MACRO_LOTUS_ICON_THEME)
	/** 取四叶草主题图标drawable */
	private Drawable getAppIconDrawableTheme(ResolveInfo resolveInfo){
	    //判断参数是否有效
	    if(resolveInfo == null || resolveInfo.activityInfo == null){
	        return null;
	    }

        Resources res = null;
        String name = null;
        
        //取应用资源
        try {
            res = mContext.getPackageManager().getResourcesForApplication(resolveInfo.activityInfo.packageName);
        }catch (NameNotFoundException e){
            res = null;
        }
        
        if(res != null){
            //取应用图标资源名，用于根据icon替换
            int resId = resolveInfo.activityInfo.getIconResource();
            if(resId > 0){
                name = res.getResourceName(resId);
            }
            //取应用类名，用于根据class替换
//            String name = resolveInfo.activityInfo.name;
        }
        
        if(name != null){
            //加工出主题包四叶草图标名：公共步骤
            name = name.toLowerCase();

            //加工出主题包四叶草图标名：根据icon替换时的特殊步骤
            int indexColon = name.indexOf(":");
            int indexSeperator = name.indexOf(File.separator);
            if (indexColon != -1 && indexSeperator != -1) {
                String packageName = name.substring(0, indexColon);                    
                name = name.substring(indexSeperator + 1);
                name = packageName + "__" + name;
            }

            //加工出主题包四叶草图标名：公共步骤
            name = name.replace(".", "_");
            name = "lotusicon_" + name;

            //取图标drawable
            LauncherApplication app = (LauncherApplication)(mContext.getApplicationContext());
            Drawable d = app.mLauncherContext.getThemeDrawableByResourceName(name);
            return d;
        }

        return null;
	}
//END
	
    /** 取四叶草缺省图标drawable */
    private Drawable getAppIconDrawable(ResolveInfo resolveInfo){
        //判断参数是否有效
        if(resolveInfo == null || resolveInfo.activityInfo == null){
            return null;
        }

        Bitmap b = getAppIcon(resolveInfo);
        if(b != null){
            BitmapDrawable bd = new BitmapDrawable(b);
            bd.setTargetDensity(mContext.getResources().getDisplayMetrics());
            return bd;
        }

        return null;
	}
	
    /** 取四叶草lbk图标drawable */
    private Drawable getAppIconDrawableLBK(String className){
        //判断参数是否有效
        if(className == null || className.isEmpty()){
            return null;
        }
        
        String iconPath = mAppiconPreferences.getString(className, "");
        iconPath = LotusUtilites.PREFIX_LOTUS_ICON_PATH + iconPath; 
        Bitmap b = BitmapFactory.decodeFile(iconPath);
        if(b != null){
            BitmapDrawable bd = new BitmapDrawable(b);
            bd.setTargetDensity(mContext.getResources().getDisplayMetrics());
            return bd;
        }

        return null;
    }

    /** 解决四叶草图标的获取 */
    private Drawable resolveAppIconDrawable(String className, ResolveInfo resolveInfo, int lastId){
        //应用默认图标
        Drawable bitmapIconSolid = null;
        if(className != null && !className.isEmpty()){
            for(int i = 0; i < LotusUtilites.TOTAL_LEAF; i++){
                if (LotusUtilites.checkIntentSolid(i, className)) {
                    if(LotusUtilites.pic_first[i] != null){
                        bitmapIconSolid = LotusUtilites.pic_first[i];
                    }
                }
            }
        }
        
        Drawable icon = null;
        if(icon == null && resolveInfo != null){
            icon = getAppIconDrawableTheme(resolveInfo);
        }
        if(icon == null && className != null && !className.isEmpty()){
            icon = getAppIconDrawableLBK(className);
        }
        if(icon == null){
            icon = bitmapIconSolid;
        }
        if(icon == null && resolveInfo != null){
            icon = getAppIconDrawable(resolveInfo);
        }
        if(icon == null && mContext != null && lastId > 0){
            LauncherApplication app = (LauncherApplication) mContext.getApplicationContext();
            icon = app.mLauncherContext.getDrawable(lastId);
        }
        
        return icon;
    }
    
    private Bitmap getAppIcon(ResolveInfo resolveInfo){
		/*	Bitmap tempicon = LotusUtilites.createIconBitmap(
				resolveInfo.activityInfo.loadIcon(packagemanager), mContext,
				mDefaultIconSize);
		try {
			icon = new BitmapDrawable(tempicon);

		} catch (Throwable e) {
		}*/
		/*DELETE BY GECN1@LENOVO.COM
		int vSize = (int)(mDeviceDensity * mDefaultIconSize);
        */
		LauncherApplication app = (LauncherApplication) mContext.getApplicationContext();
		ApplicationInfo appInfo = new ApplicationInfo(
				mContext.getPackageManager(), resolveInfo, app.getIconCache(),
				null);
		/* DELETE BY GECN1@LENOVO.COM
		BitmapDrawable bitmapDrawable = new BitmapDrawable(appInfo.iconBitmap);
		icon = LotusUtilites.createIconBitmap(bitmapDrawable, mContext,
				vSize);
		*/
		/*PK_ID:LOTUS APP ICON SIZE NOT CORRECT AUTH:GECN1@LENOVO.COM DATE:2012-9-27 S*/
		Bitmap icon = appInfo.iconBitmap;
		/*PK_ID:LOTUS APP ICON SIZE NOT CORRECT AUTH:GECN1@LENOVO.COM DATE:2012-9-27 E*/
		if (icon == null) {
			icon = mContext.getModel().getFallbackIcon();
		}

		return icon;
	}

	/** 启动叶片上的应用 */
	private void dispatchClick(Intent in) {
		if (in != null) {
			//getIntentStr();
			String checkName = in.getComponent().getPackageName();
			/**RK_ID: RK_LOTUS_NEWTASK. AUT: zhanglz1 DATE:2012-04-05 S*/
			in.addCategory(Intent.CATEGORY_LAUNCHER);
			in.setAction(Intent.ACTION_MAIN);
			in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			/**RK_ID: RK_LOTUS_NEWTASK. AUT: zhanglz1 DATE:2012-04-05 E*/

            try {
                mContext.getApplicationContext().startActivity(in);
                /*** RK_ID: USAGES_STATS.  AUT: zhaoxy . DATE: 2012-08-23 . START***/
//                mContext.getModel().getUsageStatsMonitor().add(in);
                /*** RK_ID: USAGES_STATS.  AUT: zhaoxy . DATE: 2012-08-23 . END***/
            } catch (ActivityNotFoundException e) {
                boolean hasRun = false;
                for(int i = 0; i < LotusUtilites.TOTAL_LEAF; i++){
                    if (LotusUtilites.checkIntentSolid(i, checkName)) {
                        hasRun = true;
                        Intent intentPop = new Intent(Intent.ACTION_VIEW);
						// add by zhanglz1 for lefamily 20121119
						QuickAlertDialogActivity.setContext(mContext);
                        intentPop.setClass(mContext, QuickAlertDialogActivity.class);
                        intentPop.putExtra("message_res_id", R.string.inform_application_is_not_install);
                        intentPop.putExtra("ok_res_id", R.string.goto_store);
                        if (i == LotusUtilites.LT) {
                            mContext.startActivityForResult(intentPop, LotusUtilites.REQUEST_DOWNLOAD_LT);
                        }else if (i == LotusUtilites.RT) {
                            mContext.startActivityForResult(intentPop, LotusUtilites.REQUEST_DOWNLOAD_RT);
                        }else if (i == LotusUtilites.LB) {
                            mContext.startActivityForResult(intentPop, LotusUtilites.REQUEST_DOWNLOAD_LB);
                        }else if (i == LotusUtilites.RB) {
                            mContext.startActivityForResult(intentPop, LotusUtilites.REQUEST_DOWNLOAD_RB);
                        }
                        break;
                    }               
                }
                if(!hasRun){
                    Toast.makeText(mContext, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
                }
            }
		}
	}
	
	/** 叶片点击监听器 */
	private class BtnClickListener implements View.OnClickListener {
		public void onClick(View v) {
			Intent in = new Intent();
			for(int i = 0; i < LotusUtilites.TOTAL_LEAF; i++){
	            if (v == mLeafView[i]) {
	                in = LotusUtilites.getLotusPageInfo(mPreferences,i, false);
	                LotusUtilites.longClickIndex = i;
	                break;
	            }
			}
			Log.i(TAG, "------------------in=" + in);

			if(in == null){
			    //叶子为空，添加内容。
				addLotusPageInfo(LotusUtilites.longClickIndex); 
			}else if(in.toString().contains(LotusUtilites.FAKE_LE_FAMILY)){
				startAppUnit();
			}else{
				dispatchClick(in);
			}
		}
	}
	
	/** 启动乐家族 */
	private void startAppUnit(){
		// add by zhanglz1 for lefamily 20121119
		QuickAlertDialogActivity.setContext(mContext);
		Intent intentPop = new Intent(Intent.ACTION_VIEW);
		intentPop.setClass(mContext, QuickAlertDialogActivity.class);
		intentPop.putExtra("keyword", "startAppUnit");
		mContext.startActivity(intentPop);
	}
	
	/*** RK_ID:RK_LENAVI_WITHOUT_APK_1791 AUT:zhanglz1@lenovo.com. S ***/
	/** 启动乐导航 */
	private void startNav() {
		/*** RK_ID:RK_DEVICEID_1807 AUT:zhanglz1@lenovo.com. ***/
        /*** RK_ID:RK_LOTUS_FOR _OTHERS_1964 AUT:zhanglz1@lenovo.com. DATE:2012-02-23 ***/
		String deviceId = LotusUtilites.getDeviceId(mContext);
		String result = null;
		
		try {
			result = DES.encryptDES(deviceId, LotusUtilites.DEVICE_KEY);// 加密后的设备ID
		} catch (Exception e) {
			e.printStackTrace();
		}

		String loadUrl = "http://m.idea123.cn/?d=" + result + "&c=2";
		Uri uri = Uri.parse(loadUrl);
		
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		intent.setComponent(new ComponentName("com.android.browser",
				"com.android.browser.BrowserActivity"));

		try {
			mContext.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			// add by zhanglz1 for lefamily 20121119
			QuickAlertDialogActivity.setContext(mContext);
			Intent intentPop = new Intent(Intent.ACTION_VIEW);
			intentPop.setClass(mContext, QuickAlertDialogActivity.class);
			intentPop.putExtra("message_res_id", R.string.inform_application_is_not_install);
			intentPop.putExtra("ok_res_id", R.string.goto_store);
			mContext.startActivityForResult(intentPop, LotusUtilites.REQUEST_DOWNLOAD_LB);
		}
	}

	/*** RK_ID:RK_LENAVI_WITHOUT_APK_1791 AUT:zhanglz1@lenovo.com. E ***/
	/** 语言变更时刷新四叶草，传入叶子序号 */
	private void refreshLotusForLocalChange(int pageNum){
		if (mContext == null || holdedView == null) {
			return;
		}
		
		PackageManager packagemanager = mContext.getPackageManager();
		Intent data = LotusUtilites.getLotusPageInfo(mPreferences,pageNum, false);
		if (data != null) {
            LotusUtilites.getIntentStr(mPreferences);
            LotusUtilites.getString(mContext, mPreferences); 

            ResolveInfo resolveInfo = packagemanager.resolveActivity(data, 0);
            String checkName = data.getComponent().getClassName();
            refreshMissedNum(checkName, pageNum);

            //系统有这个应用的情况
            if (resolveInfo != null) {
                //取应用标题
    		    CharSequence title = null;
    			title = resolveInfo.loadLabel(packagemanager);
    			if (title == null) {
    				title = resolveInfo.activityInfo.name;
    			}
    
    			if (checkName != null && checkName.length() > 0) {
    			    boolean isSolid = false;
    			    for(int i = 0; i < LotusUtilites.TOTAL_LEAF; i++){
    			        //如果是默认应用
    			        if(LotusUtilites.checkIntentSolid(i, checkName)){
    			            //先取默认应用名
                            String app_name_first = LotusUtilites.app_name_first[i];
                            
                            //再取应用名
                            if(app_name_first == null || app_name_first.length() == 0){
                                app_name_first = (String)title;
                            }
                            
                            //最后取缺省名
                            if(app_name_first == null || app_name_first.length() == 0){
                                app_name_first = mContext.getResources().getString(R.string.lotus_default);
                            }
                            
                            mTextView[pageNum].setText(app_name_first);
                            
                            isSolid = true;
                            break;
    			        }
    			    }

    			    //非默认应用，直接取应用名
    			    if (!isSolid) {
    				    CharSequence name = resolveInfo.loadLabel(packagemanager);
    					if (name == null) {
    						name = resolveInfo.activityInfo.name;
    					}
    					mTextView[pageNum].setText(name);
    				}
    			}
    		}
            //是默认应用，但是系统没有这个应用或是fake应用的情况
            else if (LotusUtilites.findIntentSolid(checkName) >= 0) {
    			mTextView[pageNum].setText(mContext.getResources().getText(R.string.lotus_default));
    			
    			for(int i = 0; i < LotusUtilites.TOTAL_LEAF; i++){
    			    if(LotusUtilites.checkIntentSolid(i, checkName)){
    			        String s = LotusUtilites.app_name_first[i];

    			        if (s != null) {
                            mTextView[pageNum].setText(s);
                        } else {
                            mTextView[pageNum].setText(mContext.getResources().getText(R.string.lotus_click_download));
                        }
                        
                        break;
    			    }
    			}
    		}
            //系统中没有这个应用，也不是默认应用的情况。
            else {
    			mTextView[pageNum].setText(mContext.getResources().getText(R.string.title_uninstalled));
    			
                if(LotusUtilites.checkIntentSolid(pageNum, checkName)){
                    String s = LotusUtilites.app_name_first[pageNum];

                    if (s != null) {
                        mTextView[pageNum].setText(s);
                    } else {
                        mTextView[pageNum].setText(mContext.getResources().getText(R.string.lotus_click_download));
                    }
                }
    		}

            resetTextViewSignal(pageNum, true);
		}
	} 

	/** 图标风格变更时刷新四叶草，传入叶子序号 */
    private void refreshLotusforIconStyle(final int pageNum){
    	if (mContext == null || holdedView == null) {
			return;
		}
    	
		Intent data = LotusUtilites.getLotusPageInfo(mPreferences,pageNum, false);

		if (data != null && data.getComponent() != null) {
	        PackageManager packagemanager = mContext.getPackageManager();
			ResolveInfo resolveInfo = packagemanager.resolveActivity(data, 0);
            String className = data.getComponent().getClassName();
			
			if (resolveInfo != null) {
				if (className != null && !className.isEmpty()) {
                    LotusUtilites.getIntentStr(mPreferences);

                    refreshMissedNum(className, pageNum);

	                Drawable icon = resolveAppIconDrawable(className, resolveInfo, R.drawable.lotus_for_others);

	                setLotusLableAndIcon(pageNum, icon, null);
				}
			}
		}
    }

	/*RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2012-03-27 . S*/
    /** 主题变更时刷新四叶草 */
	private void refreshLotusForTheme() {
        createLotusView();
        updateLotusView();
	}
	/*RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2012-03-27 . E*/
	/***RK_ID:RK_LOTUS_MISSED_NUM_1942 AUT:zhanglz1@lenovo.com. S***/        

	/** 注销数据库监听器 */
	private void unRegisterContentObserver() { 
	    if(mMmsContentObserver != null){
	        mContext.getContentResolver().unregisterContentObserver(mMmsContentObserver);
	        mMmsContentObserver = null;
	    }
	    if(mCallsContentObserver != null){
	        mContext.getContentResolver().unregisterContentObserver(mCallsContentObserver);
	        mCallsContentObserver = null;
	    }
        //mContext.getContentResolver().unregisterContentObserver(mSmsContentObserver);  
        //mSmsContentObserver = null;
    }  
	
	/** 注册信息数据库监听器 */
    private void registerSmsContentObserver() {
        if(mSmsHandler == null){
            mSmsHandler = new SmsContentObserverHandler(mProcessThreadSms.getLooper());
        }
        if(mMmsContentObserver == null){
            mMmsContentObserver = new newMmsContentObserver(mContext, mSmsHandler);
            mContext.getContentResolver().registerContentObserver(Uri.parse("content://mms-sms/"), true,  mMmsContentObserver);
        }
        //mSmsContentObserver = new newSmsContentObserver(mContext, mSmsHandler[pageNum],pageNum);
        //mContext.getContentResolver().registerContentObserver(Uri.parse("content://mms/inbox"), true,  mMmsContentObserver);
        //mContext.getContentResolver().registerContentObserver(Uri.parse("content://sms/inbox"), true,  mSmsContentObserver);         
    }
	
    /** 注册信息数据库监听器 */
	private void registerCallContentObserver() {
        if(mCallHandler == null){
            mCallHandler = new CallContentObserverHandler(mProcessThreadCall.getLooper());
        }
		if(mCallsContentObserver == null){
		    mCallsContentObserver = new newCallsContentObserver(mContext, mCallHandler);
	        mContext.getContentResolver().registerContentObserver(Calls.CONTENT_URI, true, mCallsContentObserver);
		}
	}

    /** 刷新所有未接数字 */
    private void refreshMissedNum(){
        for(int pageNum = 0; pageNum < LotusUtilites.TOTAL_LEAF; pageNum++){
            Intent data = LotusUtilites.getLotusPageInfo(mPreferences,pageNum, false);
            if (data == null) {
                continue;
            }
            
            ResolveInfo resolveInfo = mContext.getPackageManager().resolveActivity(data, 0);
            String checkName = data.getComponent().getClassName();
            if (resolveInfo != null) {
                refreshMissedNum(checkName, pageNum);
            }
        }
    }
    
	/** 刷新未接数字，传入类名、叶子序号 */
	private void refreshMissedNum(String checkName, int pageNum){
	    if (checkName == null || pageNum < 0 || pageNum >= LotusUtilites.TOTAL_LEAF){
	        return;
	    }
	    
        if (checkName.contains(LotusUtilites.PACKAGE_ANDROID_MESSAGE)) {
            findNewMmsCount(mContext);  
            findNewSmsCount(mContext); 
            refreshNum(newSmsCount + newMmsCount, pageNum);
        } else if (checkName.contains(LotusUtilites.ACTIVITY_ANDROID_DIAL)) {
            getNewCallsNum();
            refreshNum(missedCallCount, pageNum);
        }else{
            mMissedNum[pageNum].setVisibility(View.INVISIBLE);
        }
	}
	
	/** 刷新未接数字view */
	private void refreshNum(int num, int pageNum) {
		if (num == 0) {
			mMissedNum[pageNum].setVisibility(View.INVISIBLE);
		} else {
			mMissedNum[pageNum].setText("" + num);
			//由于规格变更，暂时关闭未接数字提示
//			mMissedNum[pageNum].setVisibility(View.VISIBLE);
            mMissedNum[pageNum].setVisibility(View.INVISIBLE);
		}
		
		refreshLotusView();
	}

    /** 查询新未接数量 */
	private void getNewCallsNum() {
	    missedCallCount = 0;
		Cursor cursor = null;
		try {
			cursor = mContext.getContentResolver().query(Calls.CONTENT_URI,
					new String[] { Calls.NEW },
					"type=" + Calls.MISSED_TYPE + " AND new=1", null, null);
			if (cursor != null) {
			    missedCallCount = cursor.getCount();
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}
	
    /** 查询新短信数量 */
	private void findNewSmsCount(Context ctx) {
	    newSmsCount = 0;
		Cursor csr = null;
		try {
			csr = ctx.getContentResolver().query(
					Uri.parse("content://sms/inbox"), new String[] { "_id" },
					"read = 0", null, null);
			if (csr != null) {
				newSmsCount = csr.getCount(); // 未读短信数目
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (csr != null) {
				csr.close();
			}
		}
		Log.v(TAG, "newSmsCount=" + newSmsCount);
	}

	/** 查询新彩信数量 */
	private void findNewMmsCount(Context ctx) {
	    newMmsCount = 0;
		Cursor csr = null;
		try {
			csr = ctx.getContentResolver().query(
					Uri.parse("content://mms/inbox"), new String[] { "_id" },
					"m_type != 134 and read = 0", null, null);
			if (csr != null) {
				newMmsCount = csr.getCount();// 未读彩信数目
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (csr != null) {
				csr.close();
			}
		}
		Log.v(TAG, "newMmsCount=" + newMmsCount);
	}

	/** 信息数据库监听器 */
	private class newMmsContentObserver extends ContentObserver {
		public newMmsContentObserver(Context context, Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			Log.v(TAG, "newMmsContentObserver onChange");
			mSmsHandler.removeMessages(LotusUtilites.GET_MMS);
			mSmsHandler.sendEmptyMessageDelayed(LotusUtilites.GET_MMS, 1000);
			mSmsHandler.removeMessages(LotusUtilites.GET_SMS);
	        mSmsHandler.sendEmptyMessageDelayed(LotusUtilites.GET_SMS, 1000); 
			// mBigCloverView.refreshMmsNum(newMmsCount + newSmsCount);
		}
	}
	
    /** 通话数据库监听器 */
	private class newCallsContentObserver extends ContentObserver {
		public newCallsContentObserver(Context context, Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			mCallHandler.removeMessages(LotusUtilites.GET_PHONE);
			mCallHandler.sendEmptyMessageDelayed(LotusUtilites.GET_PHONE, 1000);
		}
	}

    /** 通话数据库监听器handler */
    private class CallContentObserverHandler extends Handler {
		public CallContentObserverHandler(Looper looper) {
			super(looper);
		}

        @Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
    			case LotusUtilites.GET_PHONE:
    				getNewCallsNum();
    				Log.v(TAG,
    						"newCallsContentObserver onChange: missedCallCount = "
    								+ missedCallCount);

    				mHandler.post(new Runnable() {
    					@Override
    					public void run() {
    						// TODO Auto-generated method stub
    						refreshMissedNum();
    					}
    				});
    				break;
    			default:
			}
		}
	}
    
    /** 信息数据库监听器handler */
    private class SmsContentObserverHandler extends Handler {
		public SmsContentObserverHandler(Looper looper) {
			super(looper);
		}

        @Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			    case LotusUtilites.GET_MMS:
    				findNewMmsCount(mContext);
    				Log.v(TAG, "newCallsContentObserver onChange: newMmsCount = "
    				        + newMmsCount);

    				mHandler.post(new Runnable() {
    				    @Override
    					public void run() {
    						// TODO Auto-generated method stub
    						refreshMissedNum();
    					}
    				});
    				break;
    			case LotusUtilites.GET_SMS:
    				findNewSmsCount(mContext);
    				Log.v(TAG,
    						"newCallsContentObserver onChange: findNewSmsCount = "
    								+ newSmsCount);

    				mHandler.post(new Runnable() {
    					@Override
    					public void run() {
    						// TODO Auto-generated method stub
    					    refreshMissedNum();
    					}
    				});
    				break;
    			default:
			}
		}
	}
	
	/** 初始化四叶草静态数据 */
	private void initLotusUtilitesRomData(){
        if (!hasFilled) {
            LotusUtilites.fillLeafMapInfo(mContext, mPreferences);
            LotusUtilites.getIcon(mContext);
            LotusUtilites.getString(mContext, mPreferences);
            hasFilled = true;
        }
	}
	
    /** 更新四叶草 */
    private void updateLotusView(){
        if(holdedView == null || mContext == null){
            return;
        }
        
        //取Application
        LauncherApplication app = (LauncherApplication) mContext.getApplicationContext();

        // chengliang 2012.03.14 for BU --- S
        //设置lbk的叶心资源和相关处理，如果lbk有引入叶心资源的话。
        if (LotusUtilites.centerFace != null) {
            iAppStore.setImageBitmap(LotusUtilites.centerFace);
        }else{
            /*RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2012-03-26 . S*/
            Drawable b = app.mLauncherContext.getDrawable(R.drawable.lotus_center_selector);
            iAppStore.setImageDrawable(b);
            /*RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2012-03-26 . E*/
        }
        // chengliang 2012.03.14 for BU --- E

        //主题化叶子背景
        Drawable b = null;
        b = app.mLauncherContext.getDrawable(R.drawable.lotus_leftup_selector);        
        if (mLeafView[LotusUtilites.LT] != null) {
            mLeafView[LotusUtilites.LT].setBackgroundDrawable(b);
        }
        b = app.mLauncherContext.getDrawable(R.drawable.lotus_rightup_selector);
        if (mLeafView[LotusUtilites.RT] != null) {
            mLeafView[LotusUtilites.RT].setBackgroundDrawable(b);
        }
        b = app.mLauncherContext.getDrawable(R.drawable.lotus_leftdown_selector);
        if (mLeafView[LotusUtilites.LB] != null) {
            mLeafView[LotusUtilites.LB].setBackgroundDrawable(b);
        }
        b = app.mLauncherContext.getDrawable(R.drawable.lotus_rightdown_selector);
        if (mLeafView[LotusUtilites.RB] != null) {
            mLeafView[LotusUtilites.RB].setBackgroundDrawable(b);
        }
        
        //主题化叶子字色
        ColorStateList lotusTextColor = app.mLauncherContext.getColor(R.color.lotus_text_color, R.color.def__lotus_text_color);       
        for (int i = 0; i < LotusUtilites.TOTAL_LEAF; i++) {
            mTextView[i].setTextColor(lotusTextColor);
        }        

        initLotusFirstPageInfo();
        
        refreshLotusView();
    }
        
    /** 创建四叶草 */
	private void createLotusView(){
	    if(holdedView == null || mContext == null){
	        return;
	    }
	    
        //取Application
        LauncherApplication app = (LauncherApplication) mContext.getApplicationContext();

        //从主题包充气view，是否取默认必须为true，否则反复切换带布局和不带布局的主题会异常，因为holdedView本身不会重新创建。
        View replaceView = app.mLauncherContext.getLayoutViewByName(true, "gadget_lotus_face", (ViewGroup)holdedView);

        //主题包是否未修改四叶草布局
        if(replaceView == null){
            //直接取各个子view
            iAppStore = (ImageView) holdedView.findViewById(R.id.store);
            mLeafView[0] = holdedView.findViewById(R.id.leftup_part);
            mLeafView[1] = holdedView.findViewById(R.id.rightup_part);
            mLeafView[2] = holdedView.findViewById(R.id.leftdown_part);
            mLeafView[3] = holdedView.findViewById(R.id.rightdown_part);
            mImageView[0] = (ImageView) holdedView.findViewById(R.id.top_left_image);
            mImageView[1] = (ImageView) holdedView.findViewById(R.id.top_right_image);
            mImageView[2] = (ImageView) holdedView.findViewById(R.id.bottom_left_image);
            mImageView[3] = (ImageView) holdedView.findViewById(R.id.bottom_right_image);
            mTextView[0] = (TextView) holdedView.findViewById(R.id.top_left);
            mTextView[1] = (TextView) holdedView.findViewById(R.id.top_right);
            mTextView[2] = (TextView) holdedView.findViewById(R.id.bottom_left);
            mTextView[3] = (TextView) holdedView.findViewById(R.id.bottom_right);
            mMissedNum[0] = (TextView) holdedView.findViewById(R.id.missed_num_tl);
            mMissedNum[1] = (TextView) holdedView.findViewById(R.id.missed_num_tr);
            mMissedNum[2] = (TextView) holdedView.findViewById(R.id.missed_num_bl);
            mMissedNum[3] = (TextView) holdedView.findViewById(R.id.missed_num_br);
        }else{
            //替换入四叶草
            ViewGroup vg = (ViewGroup)holdedView;
            vg.removeAllViews();
            vg.addView(replaceView);

            //间接取各个子view
            iAppStore = (ImageView) app.mLauncherContext.findViewByIdName(replaceView, "store");
            mLeafView[0] = app.mLauncherContext.findViewByIdName(replaceView, "leftup_part");
            mLeafView[1] = app.mLauncherContext.findViewByIdName(replaceView, "rightup_part");
            mLeafView[2] = app.mLauncherContext.findViewByIdName(replaceView, "leftdown_part");
            mLeafView[3] = app.mLauncherContext.findViewByIdName(replaceView, "rightdown_part");
            mImageView[0] = (ImageView) app.mLauncherContext.findViewByIdName(replaceView, "top_left_image");
            mImageView[1] = (ImageView) app.mLauncherContext.findViewByIdName(replaceView, "top_right_image");
            mImageView[2] = (ImageView) app.mLauncherContext.findViewByIdName(replaceView, "bottom_left_image");
            mImageView[3] = (ImageView) app.mLauncherContext.findViewByIdName(replaceView, "bottom_right_image");
            mTextView[0] = (TextView) app.mLauncherContext.findViewByIdName(replaceView, "top_left");
            mTextView[1] = (TextView) app.mLauncherContext.findViewByIdName(replaceView, "top_right");
            mTextView[2] = (TextView) app.mLauncherContext.findViewByIdName(replaceView, "bottom_left");
            mTextView[3] = (TextView) app.mLauncherContext.findViewByIdName(replaceView, "bottom_right");
            mMissedNum[0] = (TextView) app.mLauncherContext.findViewByIdName(replaceView, "missed_num_tl");
            mMissedNum[1] = (TextView) app.mLauncherContext.findViewByIdName(replaceView, "missed_num_tr");
            mMissedNum[2] = (TextView) app.mLauncherContext.findViewByIdName(replaceView, "missed_num_bl");
            mMissedNum[3] = (TextView) app.mLauncherContext.findViewByIdName(replaceView, "missed_num_br");
        }
        
        //设置叶心触摸监听器
        iAppStore.setOnTouchListener(null);
        if (LotusUtilites.centerFace != null) {
            iAppStore.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    //用这个方法来实现叶心的普通态和压下态的切换
                    if (event.getAction() == MotionEvent.ACTION_DOWN
                            && LotusUtilites.centerPressFace != null) {
                        iAppStore.setImageBitmap(LotusUtilites.centerPressFace);
                    } else if ((event.getAction() == MotionEvent.ACTION_UP || event
                            .getAction() == MotionEvent.ACTION_CANCEL)
                            && LotusUtilites.centerFace != null) {
                        iAppStore.setImageBitmap(LotusUtilites.centerFace);
                    }
                    return false;
                }
            });
        }
        
        //设置叶心的点击监听器
        iAppStore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // chengliang 2012.03.14 for BU --- S
                String value = mPreferences.getString(LotusUtilites.LOTUS_CENTER_TARGET, LotusUtilites.FULL_LE_APPSTORE);
                Intent intent = new Intent();
                String[] info = value.split(LotusUtilites.COMPONENT_SPLIT);
                try {
                    intent.setComponent(new ComponentName(info[0], info[1]));
                } catch (Exception e1) {
                    intent.setComponent(new ComponentName(LotusUtilites.PACKAGE_LE_APPSTORE, LotusUtilites.ACTIVITY_LE_APPSTORE));
                }
                // chengliang 2012.03.14 for BU --- E
                
                try {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    intent.setAction(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    mContext.getApplicationContext().startActivity(intent);
                } catch (ActivityNotFoundException e) {
					// 启动失败的处理
					// add by zhanglz1 for lefamily 20121119
					QuickAlertDialogActivity.setContext(mContext);
					Intent intentPop = new Intent(Intent.ACTION_VIEW);
                    intentPop.setClass(mContext, QuickAlertDialogActivity.class);
                    intentPop.putExtra("message_res_id", R.string.inform_application_is_not_install);
                    intentPop.putExtra("ok_res_id", R.string.goto_store);
                    mContext.startActivityForResult(intentPop, LotusUtilites.REQUEST_DOWNLOAD_CENTER);
                }
            }
        });

        //关闭叶心触摸反馈
        iAppStore.setHapticFeedbackEnabled(false);
        
        //设置叶心长按监听器
        iAppStore.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                // chengliang 2012.03.14 for BU --- S
                /*** RK_ID:RK_LOTUS_FOR _OTHERS_1964 AUT:zhanglz1@lenovo.com. DATE:2012-02-23 ***/
                //确保拾起时widget的截图的叶心不在压下状态
                if (LotusUtilites.centerFace != null) {
                    iAppStore.setImageBitmap(LotusUtilites.centerFace);
                }
                // chengliang 2012.03.14 for BU --- E
                
                //判断四叶草是否可被拾起
                if(LeosWidgetHelper.isGadgetOnScreen(holdedView, mContext.getWorkspace().getDefaultPage())  && !LotusUtilites.isLotusChangeable){
                    return true;
                }else{
                    holdedView.performLongClick();
                    return true;            
                }
            }
        });
        
        //设置叶片的点击和长按监听器
        for (int i = 0; i < LotusUtilites.TOTAL_LEAF; i++) {
            //设置点击监听器
            mLeafView[i].setOnClickListener(new BtnClickListener());

           /* mLeafView[i].setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View v) {
                    for(int ii = 0; ii < LotusUtilites.TOTAL_LEAF; ii++){
                        if (v == mLeafView[ii]) {
                            LotusUtilites.longClickIndex = ii;
                            break;
                        }
                    }
                    Log.i(TAG, " lotus on long clicked ******************************** "+checkLeafChangableByLeafNum(LotusUtilites.longClickIndex)+"=========isLotusChangeable========="+LotusUtilites.isLotusChangeable);

                    //叶子是否不可变更
                    if (!checkLeafChangableByLeafNum(LotusUtilites.longClickIndex)) {
                        //是否四叶草在桌面主页并且四叶草不可从桌面主页上移除
                        if(LeosWidgetHelper.isGadgetOnScreen(holdedView, mContext.getWorkspace().getDefaultPage()) && !LotusUtilites.isLotusChangeable){
                        }else{
                             holdedView.performLongClick();
                        }
                    } else {
                        addLotusPageInfo(LotusUtilites.longClickIndex);
                    }
                    
                    return true;
                }
            });*/
        }
	}
	
	/** 触发系统刷新四叶草view */
	private void refreshLotusView(){
	    if(holdedView != null){
            holdedView.requestLayout();
            View widgetView = (View) holdedView.getParent();
            if (widgetView != null) {
                widgetView.requestLayout();
            }
	    }
	}
	
    public void clean(){
        unregisterReceiver(mContext, mMissedInfoReceiver);
        unregisterReceiver(mContext, mUnInstallReceiver);
        unregisterReceiver(mContext, mExternalAppReceiver);

        unRegisterContentObserver();
    }
	
}
