package com.lenovo.launcher2.weather.widget.settings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;

import com.android.internal.util.XmlUtils;
import com.lenovo.launcher.R;
import com.lenovo.launcher2.addleoswidget.LenovoWidgetsProviderInfo;
import com.lenovo.launcher2.weather.widget.utils.WeatherUtilites;

public class FetchLenovoWeatherWidgetUtil {
	private Context mContext;
	private  static final String TAG_LEOSWIDGETS = "leos_weather_widgets";
    private  static final String TAG = "ExWidget";
    private  static final String LENOVO_WIDGETS_ACTION="com.lenovo.launcher.action.LENOVO_WEATHER_WIDGETS";
    private  static final String LENOVO_WIDGETS_XML_NAME = "lenovo_weather_widget_provider_info";
    private  static final String LENOVO_WIDGETS_XML_STARTTAG = "lenovo-weather-widgets";
    public List<LenovoWidgetsProviderInfo> mInstalledLeosWidgets;
    private List<LenovoWidgetsProviderInfo> mUninstalledLeosWidgets;
    private List<LenovoWidgetsProviderInfo> mPushLeosWidgets;
    private static final String PREFIX_LENOVO_WIDGETS_CONFIG_PATH = "/data/data/com.lenovo.launcher/files/extra/weather_widget.xml";


    public FetchLenovoWeatherWidgetUtil(Context c){
		mContext = c;
		mInstalledLeosWidgets =fetchInstalledWidgets();
		mPushLeosWidgets = loadLeosWidgets();
		mUninstalledLeosWidgets = fetchUninstallLoesWidgets(mInstalledLeosWidgets,mPushLeosWidgets);
	}
    
    private List<LenovoWidgetsProviderInfo> fetchInstalledWidgets(){
    	return fetchAllInstalledLeosWidgets();
    }
	/**
	 * 获取所有 leos widgets的package name
	 * get all installed leos widgets‘ packageName 
	 * @return all installed leos widgets
	 */
    public List<LenovoWidgetsProviderInfo> fetchAllInstalledLeosWidgets(){
		PackageManager pm = mContext.getPackageManager();
		List<PackageInfo> packages =pm.getInstalledPackages(0);
		List<LenovoWidgetsProviderInfo> installedLeosWidgets =new LinkedList<LenovoWidgetsProviderInfo>();
		Map<String, LenovoExWigetInfo> widgetsInfoMap  = new HashMap<String, LenovoExWigetInfo>();
		Intent t = new Intent();
		t.setAction(LENOVO_WIDGETS_ACTION);
		for(PackageInfo p :packages ){
			t.setPackage(p.packageName);
			List<ResolveInfo> ri = pm.queryIntentActivities(t, PackageManager.GET_INTENT_FILTERS);
			if(ri != null && ri.size()>0){
				//read lenovo-widgets xml
				widgetsInfoMap.clear();
				Context friendContext=null;
				try {
					friendContext = mContext.createPackageContext(p.packageName, Context.CONTEXT_IGNORE_SECURITY);
					int id = friendContext.getResources().getIdentifier(LENOVO_WIDGETS_XML_NAME, "xml", p.packageName);
					if(id == 0){
						continue;
					}
					XmlResourceParser widgetsConfig = friendContext.getResources().getXml(id);
					try {
						XmlUtils.beginDocument(widgetsConfig, LENOVO_WIDGETS_XML_STARTTAG);
						final int depth = widgetsConfig.getDepth();
						int type;
			            while (((type = widgetsConfig.next()) != XmlPullParser.END_TAG ||
			            		widgetsConfig.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {
			                if (type != XmlPullParser.START_TAG) {
			                    continue;
			                }
			                if(widgetsConfig.getName().equals("lenovo-weather-widget")){
			                	LenovoExWigetInfo lwi = new LenovoExWigetInfo();
			                	lwi.activity_name = widgetsConfig.getAttributeValue(null,"activity_name");
			                	lwi.widgetView_name = widgetsConfig.getAttributeValue(null,"WidgetView");
			                	lwi.x = Integer.parseInt(widgetsConfig.getAttributeValue(null,"Width"));
			                	lwi.y = Integer.parseInt(widgetsConfig.getAttributeValue(null,"Height"));
			                	widgetsInfoMap.put(lwi.activity_name, lwi);
			                }
			            }
					} catch (XmlPullParserException e) {
						e.printStackTrace();
						continue;
					} catch (IOException e) {
						e.printStackTrace();
						continue;
					}
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				}
				
				for(ResolveInfo r : ri){
					String className = r.activityInfo.name;
					if(className== null){
						continue;
					}
					String cn[] = className.split("\\.");
					if(cn ==null || cn.length < 1){
						continue;
					}
					int length = cn.length;
					String key = cn[length-1];
					LenovoWidgetsProviderInfo lpi = new LenovoWidgetsProviderInfo();
					lpi.icon =r.activityInfo.loadIcon(pm);// p.applicationInfo.loadIcon();
					lpi.isInstalled = true;
					lpi.appPackageName = r.activityInfo.packageName;
					Log.d(TAG, "FetchInstallPackageUtil-->fetchAllInstalledLeosWidgets() : lpi.appPackageName ="+ lpi.appPackageName);
					lpi.appName =(String)r.activityInfo.loadLabel(pm);
		            Log.d(TAG, "FetchInstallPackageUtil-->fetchAllInstalledLeosWidgets() : lpi.appName ="+ lpi.appName);
					LenovoExWigetInfo info =(LenovoExWigetInfo) widgetsInfoMap.get(key);
					if(info == null || info.x<1 || info.y <1 || info.widgetView_name == null){
						continue;
					}
					lpi.x = info.x;
					lpi.y = info.y;
					lpi.widgetView = className.substring(0, className.length()-1-key.length()) + "."+info.widgetView_name;
		            Log.d(TAG, "FetchInstallPackageUtil-->fetchAllInstalledLeosWidgets() : lpi.widgetView ="+ lpi.widgetView);

					installedLeosWidgets.add(lpi);
				}
			}
		}
		return installedLeosWidgets;
	}
	
	
	/**
	 * 获取未安装的leoswidgets
	 * fetchUninstallLoesWidgets should know installed widgets and all leos widgets that can be fetched from leoswidets.xml
	 * @param installedLeosWidgets: all installed leos widgets
	 * @param pushLeosWidgets : all leos widgets
	 * @return 未安装的leos widgets
	 */
	private List<LenovoWidgetsProviderInfo> fetchUninstallLoesWidgets(List<LenovoWidgetsProviderInfo> installedLeosWidgets,List<LenovoWidgetsProviderInfo>pushLeosWidgets){
		boolean isEquals =false;
		int size = pushLeosWidgets.size();
		for(int i= 0;i<size;i++){
			LenovoWidgetsProviderInfo temp =pushLeosWidgets.get(i);
			isEquals = false;
			for(LenovoWidgetsProviderInfo p :installedLeosWidgets){
				if(temp.widgetPackageName.equals(p.appPackageName)){
					pushLeosWidgets.remove(temp);
					size = pushLeosWidgets.size();
					i--;
					isEquals = true;
					break;
				}
			}
			if(!isEquals){
				temp.icon = mContext.getResources().getDrawable(temp.previewImage);
			}
		}

		
		return pushLeosWidgets;
		
	}
	
	/**
     * Loads the default set of all leos widgets from an xml file.
     *@return all leos widgets
     */
	private List<LenovoWidgetsProviderInfo> loadLeosWidgets() {
		
    	List<LenovoWidgetsProviderInfo> pushLeosWidgets = new LinkedList<LenovoWidgetsProviderInfo>();
        try {
            XmlResourceParser parser = mContext.getResources().getXml(R.xml.weather_widegt);
            AttributeSet attrs = Xml.asAttributeSet(parser);
            XmlUtils.beginDocument(parser, TAG_LEOSWIDGETS);

            final int depth = parser.getDepth();
            Log.d("ac","depth="+depth);
            int type;
            while (((type = parser.next()) != XmlPullParser.END_TAG ||
                    parser.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {

                if (type != XmlPullParser.START_TAG) {
                    continue;
                }
                if(parser.getName().equals("leos_weather_widget")){
	                LenovoWidgetsProviderInfo lwp = new LenovoWidgetsProviderInfo();
	                TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.Widget);
	                lwp.appName = mContext.getResources().getString(a.getResourceId(R.styleable.Widget_widgetName,0));
	                lwp.previewImage = a.getResourceId(R.styleable.Widget_iconName,0);
	                lwp.widgetUrl = a.getString(R.styleable.Widget_widgetUrl);
	                lwp.widgetPackageName = a.getString(R.styleable.Widget_widgetPackage);
	                lwp.icon = null;
	                lwp.x = -1;
	                lwp.y = -1;
	                lwp.isInstalled = false;
	                lwp.versioncode = a.getString(R.styleable.Widget_widgetversion);
	                lwp.widgetsize = a.getString(R.styleable.Widget_widgetsize);
	                lwp.appPackageName = a.getString(R.styleable.Widget_widgetPackage) ;
	                lwp.widgetView = "";
	                pushLeosWidgets.add(lwp);
	                a.recycle();
                }
            }
        } catch (XmlPullParserException e) {
        	
            Log.w(TAG, "FetchInstallPackageUtil-->loadFavorites():Got exception parsing widgets.", e);
            return pushLeosWidgets;
        } catch (IOException e) {
            Log.w(TAG, "FetchInstallPackageUtil-->loadFavorites():Got exception parsing widgets.", e);
            return pushLeosWidgets;
        } catch (RuntimeException e) {
            Log.w(TAG, "FetchInstallPackageUtil-->loadFavorites():Got exception parsing widgets.", e);
            return pushLeosWidgets;
        }
        return pushLeosWidgets;
    }
    
    
	public ArrayList<LenovoWidgetsProviderInfo> getAllLeosWidgets() {
		mInstalledLeosWidgets.addAll(mUninstalledLeosWidgets);
//		return new ArrayList<LenovoWidgetsProviderInfo>(mInstalledLeosWidgets);
		ArrayList<LenovoWidgetsProviderInfo> list = new ArrayList<LenovoWidgetsProviderInfo>(mInstalledLeosWidgets);
		int size = list.size();
		for(int i=size; i>0; i--){
			LenovoWidgetsProviderInfo lwp = list.get(i-1);
			if(lwp.appPackageName.equals("com.lenovo.launcher2.weather.widget.aurora") && WeatherUtilites.findForPackage(mContext, "com.lenovo.launcher.theme.aurora")
					|| lwp.appPackageName.equals("com.lenovo.launcher2.weather.widget.tea") && WeatherUtilites.findForPackage(mContext, "com.lenovo.launcher.theme.tea"))
					list.remove(i-1);
		}
		return list;
	}
	public class LenovoExWigetInfo{
		public int x;
		public int y;
		public String activity_name;
		public String widgetView_name;
	}
}
