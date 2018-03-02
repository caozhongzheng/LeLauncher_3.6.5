package com.lenovo.launcher2.customizer;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.XmlResourceParser;
import android.util.Log;

import com.android.internal.util.XmlUtils;
import com.lenovo.launcher.R;
import com.lenovo.launcher2.customizer.Debug.R5;

/**
 * @author Lenovo
 * 
 * Define the default variables.
 *
 */
public final class GlobalDefine {
	
	/**
	 * Just take one Screenshot of HomePage when save profile.
	 */
	public static final boolean DEFAULT_SCREENSHOT_HOME_ONLY = true;
	
	/**
	 * change LAYER_TYPE_HARDWARE TO LAYER_TYPE_NONE
	 */
	public static boolean MAGIC_LAYER_NONE = true;
	
	/*PK_ID:SOME_DEVICES_NOT_ALLOW_LAYER_TYPE_HARDWARE AUTH:GECN1@LNEOVO.COM DATE:2011-11-21 S*/
	public static boolean MAGIC_LAYER_NONE_FOR_SOME_DEVICES = false;
	/*PK_ID:SOME_DEVICES_NOT_ALLOW_LAYER_TYPE_HARDWARE AUTH:GECN1@LNEOVO.COM DATE:2011-11-21 S*/
	
	
	private GlobalDefine() {}

	/** ID : GlobalDefine    AUT: chengliang     2012.06.14    S  */
	private static final String EXTERNAL_CONFIGURATION_PREF = "external_global_define";
	private static final String GLOBAL_DEFINE_USE_LAYERTYPE_NONE = "use_layer_type_none";
	
	private static boolean OBTAINED = false; 
	public static void obtainExternalConfiguration(final Context context) {
		if( OBTAINED ){
			R5.echo("GlobalDefine has obtained .");
			return;
		}
		
		SharedPreferences pref = context.getSharedPreferences(EXTERNAL_CONFIGURATION_PREF,
				Activity.MODE_APPEND);
		MAGIC_LAYER_NONE = pref.getBoolean(GLOBAL_DEFINE_USE_LAYERTYPE_NONE, false);
		
		/*PK_ID:SOME_DEVICES_NOT_ALLOW_LAYER_TYPE_HARDWARE AUTH:GECN1@LNEOVO.COM DATE:2011-11-21 S*/
		MAGIC_LAYER_NONE_FOR_SOME_DEVICES = getLayerHardwareConfigForSomeDevices(context);
		MAGIC_LAYER_NONE_FOR_SOME_DEVICES = false;
		MAGIC_LAYER_NONE_FOR_SOME_DEVICES = MAGIC_LAYER_NONE || MAGIC_LAYER_NONE_FOR_SOME_DEVICES;
		/*PK_ID:SOME_DEVICES_NOT_ALLOW_LAYER_TYPE_HARDWARE AUTH:GECN1@LNEOVO.COM DATE:2011-11-21 E*/
		Log.d("hardware", "-------------------------MAGIC_LAYER_NONE_FOR_SOME_DEVICES = " + MAGIC_LAYER_NONE_FOR_SOME_DEVICES);
		Log.d("hardware", "-------------------------MAGIC_LAYER_NONE = " + MAGIC_LAYER_NONE);

		R5.echo("GlobalDefine value is: " + MAGIC_LAYER_NONE);
		
//		MAGIC_LAYER_NONE = false;
		
		OBTAINED = true;
	}
	/** ID : GlobalDefine    AUT: chengliang     2012.06.14    E  */
	
    /* RK_ID: RK_APPSCATEGORY. AUT: liuli1 . DATE: 2012-06-25 . START */
    // NOTE: if change this flag to true, need to modify all_apps_settings.xml
    // at the same time to make sure it worked.
    public static boolean WIDGET_TAB_ENABLED = false;

    // for hawaii, 2012-06-01
    public static final boolean HAWAII_PAGE_ENABLED = true;
    /* RK_ID: RK_APPSCATEGORY. AUT: liuli1 . DATE: 2012-06-25 . END */
    
    public static final int MENU_TYPE_WORKSPACE = 0;
    public static final int MENU_TYPE_APPS = 1;
    public static final int MENU_SUBTYPE_COMMON = 2;
    public static final int MENU_SUBTYPE_CHARACTER = 3;
    public static final int MENU_SUBTYPE_MORE = 4;
    
    public static final boolean MAGIC_GESTRUE_ENABLED = true;
    /*RK_FOR_BU_VERSION dining 2012-12-05 S*/
    //in BU version, don't show 1. whitelist 2. cellcount  
    // show 3. "slide type", 4 icon style
    public static final boolean BU_VERSION = false;
    
    
    /** ID : GlobalDefine    AUT: chengliang     2012.05.16    E  */
	/* RK_VERSION_WW dining@lenovo.com 2012-07-30 S**/
	//for Version WW, get the value from the configuraiton file 
	//and in current branch ics_ww, the default value is true 
	
	public static boolean getVerisonWWConfiguration(final Context context) {
		boolean originvalue = false;			
		SharedPreferences prefversion = context.getSharedPreferences("LeLauncher_VersionWW", Activity.MODE_APPEND);
        if( prefversion != null ){
        	originvalue = prefversion.getBoolean( "version_enable", originvalue);
        }
		return originvalue;
	}
	/* RK_VERSION_WW dining@lenovo.com 2012-07-30 E**/
	
	//add the new value for CMCC_TD
		public static boolean getVerisonCMCCTDConfiguration(final Context context) {
			boolean originvalue = false;			
			SharedPreferences prefversion = context.getSharedPreferences("LeLauncher_VersionWW", Activity.MODE_APPEND);
	        if( prefversion != null ){
	        	originvalue = prefversion.getBoolean( "cmcc_td_enable", originvalue);
	        }
			return originvalue;
		}
	
		
		/* RK_VERSION_WW kangwei3@lenovo.com 2012-07-30 S**/
		//for Version WW, get the value from the configuraiton file 
		//and in current branch ics_ww, the default value is true 
		
		public static boolean getWeatherVerisonWWConfiguration(final Context context) {
			boolean originvalue = false;	
			boolean weatheroriginvalue = false;
			SharedPreferences prefversion = context.getSharedPreferences("LeLauncher_VersionWW", Activity.MODE_APPEND);
	        if( prefversion != null ){
	        	originvalue = prefversion.getBoolean( "version_enable", originvalue);
	        }
	        if(!originvalue)
	        	weatheroriginvalue = true;
	        if( prefversion != null )
	        	weatheroriginvalue = prefversion.getBoolean( "weather_version_enable", weatheroriginvalue);
	        Log.d("ww","weatheroriginvalue="+weatheroriginvalue);
			return weatheroriginvalue;
		}
		/* RK_VERSION_WW kangwei3@lenovo.com 2012-07-30 E**/
	/*PK_ID:SOME_DEVICES_NOT_ALLOW_LAYER_TYPE_HARDWARE AUTH:GECN1@LNEOVO.COM DATE:2011-11-21 S*/
	/**
	 * AUTH:GECN1@LENOVO.COM
	 * 
	 * @param context
	 * @return
	 */
	public static boolean getLayerHardwareConfigForSomeDevices(final Context context){
		
		String mtype = android.os.Build.MODEL;
                if(mtype ==null){
			return false;
		}
		XmlResourceParser config = context.getResources().getXml(R.xml.hardware_for_some_devices);
		try {
			XmlUtils.beginDocument(config, "devices");
			final int depth = config.getDepth();
			int type;
               while (((type = config.next()) != XmlPullParser.END_TAG ||
            		config.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {

                if (type != XmlPullParser.START_TAG) {
                    continue;
                }
                if(config.getName().equals("device")){
                	type = config.next();
                	if(mtype.equals(config.getText())){
                		return true;
                	}
                	type = config.next();
                }
            }
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	/*PK_ID:SOME_DEVICES_NOT_ALLOW_LAYER_TYPE_HARDWARE AUTH:GECN1@LNEOVO.COM DATE:2011-11-21 E*/
	
	private static boolean mConfigurableWidgetTagEnable = false;
	public static boolean getConfigurableAppWidgetTagEnableState(){
		return mConfigurableWidgetTagEnable;
	}
	
}
