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

public class FetchLenovoExWidgetUtil {
	private Context mContext;
	private  static final String TAG_LEOSWIDGETS = "leoswidgets";
    private  static final String TAG = "ExWidget";
    private  static final String LENOVO_WIDGETS_ACTION="com.lenovo.launcher.action.LENOVOEXWIDGETS";
    private  static final String LENOVO_WIDGETS_XML_NAME = "lenovo_ex_widget_provider_info";
    private  static final String LENOVO_WIDGETS_XML_STARTTAG = "lenovo-exwidgets";
    private List<LenovoExWidgetsProviderInfo> mInstalledLeosWidgets;
    private List<LenovoExWidgetsProviderInfo> mUninstalledLeosWidgets;
    private List<LenovoExWidgetsProviderInfo> mPushLeosWidgets;
    private static final String PREFIX_LENOVO_WIDGETS_CONFIG_PATH = "/data/data/com.lenovo.launcher/files/extra/exwidget.xml";


    public FetchLenovoExWidgetUtil(Context c){
		mContext = c;
		mInstalledLeosWidgets = fetchAllInstalledLeosWidgets();
		mPushLeosWidgets = loadLeosWidgets();
		mUninstalledLeosWidgets = fetchUninstallLoesWidgets(mInstalledLeosWidgets,mPushLeosWidgets);
	}
    
    
	/**
	 * 获取所有 leos widgets的package name
	 * get all installed leos widgets‘ packageName 
	 * @return all installed leos widgets
	 */
	private List<LenovoExWidgetsProviderInfo> fetchAllInstalledLeosWidgets(){
		PackageManager pm = mContext.getPackageManager();
		List<PackageInfo> packages =pm.getInstalledPackages(0);
		List<LenovoExWidgetsProviderInfo> installedLeosWidgets =new LinkedList<LenovoExWidgetsProviderInfo>();
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
			                if(widgetsConfig.getName().equals("lenovo-exwidget")){
			                	LenovoExWigetInfo lwi = new LenovoExWigetInfo();
			                	lwi.activity_name = widgetsConfig.getAttributeValue(null,"activity_name");
			                	lwi.widgetView_name = widgetsConfig.getAttributeValue(null,"WidgetView");
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
	
					LenovoExWidgetsProviderInfo lpi = new LenovoExWidgetsProviderInfo();
					lpi.icon =r.activityInfo.loadIcon(pm);// p.applicationInfo.loadIcon();
					lpi.isInstalled = true;
					lpi.appPackageName = r.activityInfo.packageName;
					Log.d(TAG, "FetchInstallPackageUtil-->fetchAllInstalledLeosWidgets() : lpi.appPackageName ="+ lpi.appPackageName);
					lpi.appName =(String)r.activityInfo.loadLabel(pm);
		            Log.d(TAG, "FetchInstallPackageUtil-->fetchAllInstalledLeosWidgets() : lpi.appName ="+ lpi.appName);
					LenovoExWigetInfo info =(LenovoExWigetInfo) widgetsInfoMap.get(key);
					if(info == null || info.widgetView_name == null){
						continue;
					}
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
	private List<LenovoExWidgetsProviderInfo> fetchUninstallLoesWidgets(List<LenovoExWidgetsProviderInfo> installedLeosWidgets,List<LenovoExWidgetsProviderInfo>pushLeosWidgets){
		boolean isEquals =false;
		int size = pushLeosWidgets.size();
		for(int i= 0;i<size;i++){
			LenovoExWidgetsProviderInfo temp =pushLeosWidgets.get(i);
			isEquals = false;
			for(LenovoExWidgetsProviderInfo p :installedLeosWidgets){
				if(temp.widgetPackageName.equals(p.widgetPackageName)){
					pushLeosWidgets.remove(temp);
					size = pushLeosWidgets.size();
					i--;
					isEquals = true;
					break;
				}
			}
			if(!isEquals){
				temp.icon = mContext.getResources().getDrawable(R.drawable.ic_allapps);
			}
		}

		
		return pushLeosWidgets;
		
	}
	
	/**
     * Loads the default set of all leos widgets from an xml file.
     *@return all leos widgets
     */
	private List<LenovoExWidgetsProviderInfo> loadLeosWidgets() {
		
    	List<LenovoExWidgetsProviderInfo> pushLeosWidgets = new LinkedList<LenovoExWidgetsProviderInfo>();
        try {
            XmlResourceParser parser = mContext.getResources().getXml(R.xml.exwidegt);
            AttributeSet attrs = Xml.asAttributeSet(parser);
            XmlUtils.beginDocument(parser, TAG_LEOSWIDGETS);

            final int depth = parser.getDepth();

            int type;
            while (((type = parser.next()) != XmlPullParser.END_TAG ||
                    parser.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {

                if (type != XmlPullParser.START_TAG) {
                    continue;
                }
                LenovoExWidgetsProviderInfo lwp = new LenovoExWidgetsProviderInfo();
                TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.Widget);
                lwp.appName = a.getString(R.styleable.Widget_widgetName);
                lwp.iconName = a.getString(R.styleable.Widget_iconName);
                lwp.widgetUrl = a.getString(R.styleable.Widget_widgetUrl);
                lwp.widgetPackageName = a.getString(R.styleable.Widget_widgetPackage);
                lwp.icon = null;
                lwp.isInstalled = false;
                lwp.appPackageName = a.getString(R.styleable.Widget_widgetName) ;
                lwp.widgetView = null;
                pushLeosWidgets.add(lwp);
                a.recycle();
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
    
    
	public ArrayList<LenovoExWidgetsProviderInfo> getAllLeosWidgets() {
		mInstalledLeosWidgets.addAll(mUninstalledLeosWidgets);
		return new ArrayList<LenovoExWidgetsProviderInfo>(mInstalledLeosWidgets);
	}

}
