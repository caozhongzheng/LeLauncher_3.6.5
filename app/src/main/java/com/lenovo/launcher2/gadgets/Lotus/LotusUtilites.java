package com.lenovo.launcher2.gadgets.Lotus;

import java.io.File;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.customizer.GlobalDefine;
import com.lenovo.launcher2.customizer.Debug.R2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

/** 四叶草工具静态类，其成员域必须不和Context有关系！！！ */
public class LotusUtilites {
    /** 四叶草lbk带入图标根路径 */
    public static final String PREFIX_LOTUS_ICON_PATH = "/data/data/com.lenovo.launcher/files/extra/lotusicon/";
    /** 四叶草lbk带入资源根路径 */
    public static final String PREFIX_LOTUS_CENTER_PIC_PATH = "/data/data/com.lenovo.launcher/files/extra/";
    /** 四叶草lbk带入叶心普通态图片文件名 */
    public static final String LOTUS_CENTER_PIC_NORMAL_NAME = "lotus_center.png";
    /** 四叶草lbk带入叶心下压态图片文件名 */
    public static final String LOTUS_CENTER_PIC_PRESSD_NAME = "lotus_center_press.png";
    /** 四叶草lbk带入叶子图片文件名 */
    public static final String[] LOTUS_PIC_NAME = {"lotus_left_top.png", "lotus_right_top.png", "lotus_left_bottom.png", "lotus_right_bottom.png"};
    
    /** 四叶草信息SharedPreferences名称 */
    public static final String LOTUSINFO = "lotuspage";
    /** 四叶草图标SharedPreferences名称 */
    public static final String LOTUSICON = "lotusicon";
    
    public static final int REQUEST_DOWNLOAD_CENTER = 30;
    public static final int REQUEST_DOWNLOAD_LT = 31;
    public static final int REQUEST_DOWNLOAD_RT = 32;
    public static final int REQUEST_DOWNLOAD_LB = 33;
    public static final int REQUEST_DOWNLOAD_RB = 34;

    //四叶草参数集合存取用字串
    public static final String[] PREFIX_LEAF = {"leaf_left_top_", "leaf_right_top_", "leaf_left_bottom_", "leaf_right_bottom_"};
    public static final String SUFFIX_LEAF_FIRST = "first";
    public static final String SUFFIX_LEAF_SOLID = "solid";
    public static final String SUFFIX_LEAF_START_TARGET = "target";
    public static final String SUFFIX_LEAF_APP_NAME_CN = "app_name_cn";
    public static final String SUFFIX_LEAF_APP_NAME_EN = "app_name_en";
    public static final String SUFFIX_LEAF_APP_NAME_TW = "app_name_tw";
    public static final String SUFFIX_LEAF_APP_NAME_SUFIX = "app_name_";
    public static final String SUFFIX_LEAF_ENABLE_CHANGE = "enable_change";
    public static final String LOTUS_CENTER_TARGET = "lotus_center_target";
    
    //叶片id
    public static final int LT = 0;
    public static final int RT = 1;
    public static final int LB = 2;
    public static final int RB = 3;
    public static final int TOTAL_LEAF = 4;
    public static final int CENTER = 4;
    public static final int TOTAL = 5;

    //“未接数据”类型id
    public static final int GET_MMS = 1;
    public static final int GET_PHONE = 2;
    public static final int GET_SMS = 3; 

    /**  */
    public final static String ACTION_PICK_SHORTCUT = "android.intent.action.PICK_SHORTCUT_LOTUS";
    /**  */
    public final static String EXTRA_APPCLASSNAME = "APPCLASSNAME";
    
    /** component分隔符 */
    public final static String COMPONENT_SPLIT = "/";
    
    /** 乐商店完整名 */
    public final static String FULL_LE_APPSTORE = "com.lenovo.leos.appstore/com.lenovo.leos.appstore.ui.Loft";
    /** 乐商店配包名 */
    public final static String PACKAGE_LE_APPSTORE = "com.lenovo.leos.appstore";
    /** 乐商店配应用名 */
    public final static String ACTIVITY_LE_APPSTORE = "com.lenovo.leos.appstore.ui.Loft";
    
    /** 乐导航配置名 */
    public final static String FAKE_LE_NAVI = "com.lenovo.fakenavi00";
    /** 乐应用配置名 */
    public final static String FAKE_LE_FAMILY = "com.lenovo.allappunit00";
    /** 系统信息配置名 */
    public final static String PACKAGE_ANDROID_MESSAGE = "com.android.mms";
    /** 系统拨号配置名 */
    public final static String ACTIVITY_ANDROID_DIAL = "com.android.contacts.activities.DialtactsActivity";
    
    /** 设备密码KEY */
    public static final String DEVICE_KEY = "12345678";
    
    /** rom参数提取key：当前应用设置串 */
    public static final String[] sfPrefKeyMapTarget = new String[TOTAL_LEAF];
    /** rom参数提取key：默认应用设置串 */
    public static final String[] sfPrefKeyMapSolid = new String[TOTAL_LEAF];

    /**　lbk默认图标 */
    public static final Drawable[] pic_first = new Drawable[TOTAL_LEAF];
    /**　默认标题 */
    public static final String[] app_name_first = new String[TOTAL_LEAF];
    /**　默认搜索文本 */
    public static final String[] app_name_search = new String[TOTAL];
    /**　默认应用设置字串 */
    public static final String[] intent_solid = new String[TOTAL_LEAF];
    /** 记录叶子图标是否可更改 */
    public static final Boolean[] sfPrefKeyMapEnableChange = new Boolean[TOTAL_LEAF];

    /** 四叶草是否可以被从桌面主页上移除 */
    public static boolean isLotusChangeable = true;

    /** 设备id */
    public static String deviceID;
    
    /**　叶心图标普通态 */
    public static Bitmap centerFace;
    /**　叶心图标压下态 */
    public static Bitmap centerPressFace;

    /** sd卡应用是否有效 */
    public static boolean sIsSdcardAppAvalible;
    /** 长按叶子序号 */
    public static int longClickIndex = -1;
    /** 中心叶片的intent */
    public static String LOTUS_CENTER_INTENT;
	public final static String IDEA_STORE_URL = "http://www.lenovomm.com/appstore/psl/";
    static {
        for(int i = 0; i < TOTAL_LEAF; i++){
            sfPrefKeyMapTarget[i] = PREFIX_LEAF[i] + SUFFIX_LEAF_START_TARGET + "_" + SUFFIX_LEAF_FIRST;
            sfPrefKeyMapSolid[i] = PREFIX_LEAF[i] + SUFFIX_LEAF_SOLID + "_" + SUFFIX_LEAF_FIRST;
        }
    }

    /** 取设备id */
	public static String getDeviceId(Context context) {
		if (deviceID != null){
			return deviceID;
		}
		
		String imei = getImeiAddr(context);
		
		//imei存在，就用之作为设备id。
		if (imei != null) {
			deviceID = imei;
			return deviceID;
		}
		
		String MANUFACTURER = android.os.Build.MANUFACTURER.toLowerCase();
		//如果工厂标识串含lenovo，且编译序列号不是未知，则取编译序列号作为设备id。
		if (null != MANUFACTURER && MANUFACTURER.toLowerCase().indexOf("lenovo") >= 0) {
			String sn = getSnAddr(context);
			if (sn != null && !sn.equalsIgnoreCase("unknown")) {
				deviceID = sn;
				return deviceID;
			}
		}
		
		//取wifi硬件地址作为设备id
		deviceID = getMacAddr(context);
		return deviceID;
	}

	/** 取imei号 */
	public static String getImeiAddr(Context context) {
		try {
			TelephonyManager telephonyMgr = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String imei = telephonyMgr.getDeviceId();
			if (imei != null){
				return imei.toUpperCase();
			}
		} catch (Exception e) {

		}
		return null;
	}

	/** 取编译序列号 */
	public static String getSnAddr(Context context) {
		try {
			if (android.os.Build.VERSION.SDK_INT <= 8)
				return null;
			else
				return android.os.Build.SERIAL;
		} catch (Exception e) {

		}
		return null;
	}

	/** 取wifi硬件地址 */
	public static String getMacAddr(Context context) {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		if(info == null || info.getMacAddress() == null){
			return "";
		}else{
			return info.getMacAddress().replace(":", "");
		}
	}

	/** 创建图标位图，传入原始图标、目标图标尺寸 */
	public static Bitmap createIconBitmap(Drawable icon, Context context, int defaultIconSize) {
        Canvas sCanvas = new Canvas();
        sCanvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.DITHER_FLAG, Paint.FILTER_BITMAP_FLAG));

        //目标图标宽高
		int width = defaultIconSize;
		int height = defaultIconSize;

		if (icon instanceof PaintDrawable) {
		    //如果是PaintDrawable就直接设置宽高
			PaintDrawable painter = (PaintDrawable) icon;
			painter.setIntrinsicWidth(width);
			painter.setIntrinsicHeight(height);
		} else if (icon instanceof BitmapDrawable) {
            //如果是BitmapDrawable就确保像素密度正确
			BitmapDrawable bitmapDrawable = (BitmapDrawable) icon;
			Bitmap bitmap = bitmapDrawable.getBitmap();
			if (bitmap.getDensity() == Bitmap.DENSITY_NONE) {
				bitmapDrawable.setTargetDensity(context.getResources().getDisplayMetrics());
			}
		}
		
		//取内容宽高
		int sourceWidth = icon.getIntrinsicWidth();
		int sourceHeight = icon.getIntrinsicHeight();

		//如果内容有效
		if (sourceWidth > 0 && sourceHeight > 0) {
			if (width < sourceWidth || height < sourceHeight) {
                //单纯压缩的情况
			    //计算内容宽高比，根据内容宽高比调整目标宽高。
				final float ratio = (float) sourceWidth / sourceHeight;
				if (sourceWidth > sourceHeight) {
					height = (int) (width / ratio);
				} else if (sourceHeight > sourceWidth) {
					width = (int) (height * ratio);
				}
			} else {
			    //需要拉伸的情况，不拉伸。
				width = sourceWidth;
				height = sourceHeight;
			}
		}

		//打印到目标位图
		final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		final Canvas canvas = sCanvas;
		canvas.setBitmap(bitmap);
		//保存恢复原始图标的Bounds
		Rect sOldBounds = new Rect();
		sOldBounds.set(icon.getBounds());
		//需要压缩时，这样绘制会正确压缩吗？
		icon.setBounds(0, 0, width, height);
		icon.draw(canvas);
		icon.setBounds(sOldBounds);

		return bitmap;
	}

	/** 从资源目录解析出lbk带入的四叶草叶子默认应用图标资源 */
	public static synchronized void getIcon(Context context){
	    for(int i = 0; i < TOTAL_LEAF; i++){
	        try {
	            Bitmap bitmap = null;
	            if (pic_first[i] == null){
	                bitmap = BitmapFactory.decodeFile(PREFIX_LOTUS_CENTER_PIC_PATH + LOTUS_PIC_NAME[i]);
	            }
	            if(bitmap !=null){
	                BitmapDrawable bd = new BitmapDrawable(bitmap);
	                //校正像素密度
	                bd.setTargetDensity(context.getResources().getDisplayMetrics());
	                pic_first[i] = bd;
	            }
	        } catch (Exception e1) {
	            e1.printStackTrace();
	        }
	    }

		try {
            if (centerFace == null)
                centerFace = BitmapFactory.decodeFile(PREFIX_LOTUS_CENTER_PIC_PATH + LOTUS_CENTER_PIC_NORMAL_NAME);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        try {
            if (centerPressFace == null)
                centerPressFace = BitmapFactory.decodeFile(PREFIX_LOTUS_CENTER_PIC_PATH + LOTUS_CENTER_PIC_PRESSD_NAME);
        } catch (Exception e2) {
            e2.printStackTrace();
        }   
	}
	
	/** 解析叶子应用的intent，传入配置字串 */
	public static Intent getLotusPageInfo(String intent_str) {
		/*** RK_ID:RK_REDSUQARE_1745 AUT:zhanglz1@lenovo.com. ***/
		Intent intent = null;
		String packageName = null;
		String className = null;
		ComponentName cn = null;
		if (intent_str != null && intent_str.length() > 0) {
			try {
			    //根据“/”拆分字串
			//	String s = File.separator;
				String[] infos = intent_str.split("/");
				packageName = infos[0];
				className = infos[1];

				cn = new ComponentName(packageName, className);

				intent = new Intent();
				intent.setComponent(cn);
			} catch (Exception e) {
			}
		}

		R2.echo("Fetch : str is : " + intent_str + "  , packageName = " + packageName);
		return intent;
	}
	
	/** 从rom里取叶片默认标题，传入参数集合 */
    public static void getString(Context context, SharedPreferences preferences){
        /*对于普通版本：
         * 1.如果系统语言为非英文时，显示配置文件中的对应语言的名称，在这种情况下，如果非英文名称不存在，则显示配置文件中的英文名称
         * ， 如果英文名称也不存在，则显示该应用的名称。如果该应用名称无法取到，则显示指定字符串“默认应用/默認應用”；
         * 2.如果系统语言非中文/繁体中的任意一个，此时显示配置文件中的英文名称，这种情况下，如果配置文件中的英文名词不存在，则显示该应用的名称。
         * 3.如果该应用名称无法取到，则显示指定字符串则显示指定字符串“Default”
         * 对于海外版本：
         * 1.系统是什么语言就显示什么语言种类的名词
         * 2.若该语言不存在，则显示该应用的名称。如果该应用名称无法取到，则显示指定字符串“默认应用/默認應用”；
         */ 

        
        Resources res = context.getResources();
        String country = res.getConfiguration().locale.getCountry();
        boolean isCN = country.equals("CN");
        boolean isTW = country.equals("TW");
        boolean isUK = country.equals("UK");
        boolean isUS = country.equals("US");
        
        for(int i = 0; i < TOTAL_LEAF; i++){

            //取默认标题英文名
            if(isUK || isUS){
                app_name_first[i] = preferences.getString(PREFIX_LEAF[i] + SUFFIX_LEAF_APP_NAME_EN + "_" + SUFFIX_LEAF_FIRST, "");
            }
            //取默认标题简体中文名
            else if(isCN){
                app_name_first[i] = preferences.getString(PREFIX_LEAF[i] + SUFFIX_LEAF_APP_NAME_CN + "_" + SUFFIX_LEAF_FIRST, app_name_first[i]);
            }            
            //取默认标题繁体中文名
            else if(isTW){
                app_name_first[i] = preferences.getString(PREFIX_LEAF[i] + SUFFIX_LEAF_APP_NAME_TW + "_" + SUFFIX_LEAF_FIRST, app_name_first[i]);
            }
            //取默认标题其他语言名
            else {
                //In version_ww, for other language, we get the lotus default string from configuration file
                //if it is not defined in configuration file, the default value is en-string
                app_name_first[i] = preferences.getString(PREFIX_LEAF[i] + SUFFIX_LEAF_APP_NAME_SUFIX + country + "_" + SUFFIX_LEAF_FIRST, app_name_first[i]);                       
            }
        }
	}
	
    /** 初始化一些四叶草用到的静态数据 */
    public static void fillLeafMapInfo(Context context, SharedPreferences preferences) {
        isLotusChangeable = preferences.getBoolean("lotus_changeable_homepage", true);

        for(int i = 0; i < TOTAL_LEAF; i++){
            String tmpKey = PREFIX_LEAF[i] + SUFFIX_LEAF_ENABLE_CHANGE + "_" + SUFFIX_LEAF_FIRST;
            sfPrefKeyMapEnableChange[i] = preferences.getBoolean(tmpKey, true);
        }
    }

    /** 取四叶草默认搜索字串 */
    public static void getSearchList(SharedPreferences preferences) {
        app_name_search[LT] = preferences.getString("leaf_left_top_search", "");
        app_name_search[RT] = preferences.getString("leaf_right_top_search", "");
        app_name_search[LB] = preferences.getString("leaf_left_bottom_search", "");
        app_name_search[RB] = preferences.getString("leaf_right_bottom_search", "");
        app_name_search[CENTER] = preferences.getString("lotus_center_search", "");
    }
    
    /** 取四叶草默认应用的设置串 */
    public static void getIntentStr(SharedPreferences preferences){
        for(int i = 0; i < TOTAL_LEAF; i++){
            String intent_str = preferences.getString(sfPrefKeyMapSolid[i], "");
            if (intent_str != null && intent_str.length()!=0){
                intent_solid[i] = intent_str.replace(COMPONENT_SPLIT, "");
            }
        }
        LOTUS_CENTER_INTENT = preferences.getString("lotus_center_target", "com.lenovo.leos.appstore/com.lenovo.leos.appstore.ui.Loft");
    }
    
    /** 检查是否默认应用配置串 */
    public static boolean checkIntentSolid(int index, String s){
        return (LotusUtilites.intent_solid[index] != null && LotusUtilites.intent_solid[index].contains(s));
    }

    /** 查找是哪个默认应用配置串 */
    public static int findIntentSolid(String s){
        for(int i = 0; i < LotusUtilites.TOTAL_LEAF; i++){
            if(checkIntentSolid(i, s)){
                return i;
            }
        }
        return -1;
    }
    
    /** 取四叶草数据（包名、类名），传入叶子序号、是否取默认值 */
	public static Intent getLotusPageInfo(SharedPreferences preferences, int pagenum, boolean getSolid) {
		/*** RK_ID:RK_REDSUQARE_1745 AUT:zhanglz1@lenovo.com. ***/
		Intent intent = null;
		String intent_str = null;
		if(getSolid){
            intent_str = LotusUtilites.sfPrefKeyMapSolid[pagenum];
		}else{
            intent_str = LotusUtilites.sfPrefKeyMapTarget[pagenum];
		}
		intent_str = preferences.getString(intent_str, "");
		String packageName = null;
		String className = null;
		ComponentName cn = null;
		if (intent_str != null && intent_str.length() > 0) {
			try {
				//String s = File.separator;
				String[] infos = intent_str.split(LotusUtilites.COMPONENT_SPLIT);
				packageName = infos[0];
				className = infos[1];

				cn = new ComponentName(packageName, className);

				intent = new Intent();
				intent.setComponent(cn);
			} catch (Exception e) {
			}
		}

		R2.echo("Fetch : str is : " + intent_str + "  , packageName = " + packageName);
		return intent;
	}
	private LotusUtilites() {
		// TODO Auto-generated constructor stub
	}
	public static String getPackageNameFromIntent(String intent){
		if (intent != null && intent.length() != 0) {
			//String s = File.separator;
			String[] info = intent.split(LotusUtilites.COMPONENT_SPLIT);
			return info[0];
		}
		return intent;
	}
	//test by dining
	public static void clearLotusPageTargetInfo(SharedPreferences preferences, int pageNum){
		if(preferences == null)
			return;
		
		final SharedPreferences.Editor editor = preferences.edit();
		editor.putString(LotusUtilites.sfPrefKeyMapTarget[pageNum], "");
		editor.apply();
		//LotusUtilites.sfPrefKeyMapTarget[pageNum]="";
	}

	public static boolean isSolidExit(int index) {
		// TODO Auto-generated method stub
		if(LotusUtilites.intent_solid[index] == null || LotusUtilites.intent_solid[index] == ""
				||LotusUtilites.intent_solid[index] == "/")
             return false;
		else{
			return true;
		}

	}

	public static void setIsSdcardAppAvalible(boolean b) {
		// TODO Auto-generated method stub
		sIsSdcardAppAvalible = b;
	}

	public static void setlongClickIndex(int position) {
		// TODO Auto-generated method stub
		longClickIndex = position;
	}

}