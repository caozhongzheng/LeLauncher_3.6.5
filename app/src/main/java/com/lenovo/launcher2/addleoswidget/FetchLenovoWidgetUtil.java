package com.lenovo.launcher2.addleoswidget;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import com.android.internal.util.XmlUtils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import com.lenovo.launcher.R;

public class FetchLenovoWidgetUtil {
	
	private Context mContext;
	private  final String TAG_LEOSWIDGETS = "leoswidgets";
    private  final String TAG = "Widget";
    private  final String LENOVO_WIDGETS_ACTION="com.lenovo.launcher.action.LENOVOWIDGETS";
    private  final String LENOVO_WIDGETS_XML_NAME = "lenovo_widget_provider_info";
    private  final String LENOVO_WIDGETS_XML_STARTTAG = "lenovo-widgets";
    private List<LenovoWidgetsProviderInfo> mInstalledLeosWidgets;
    private List<LenovoWidgetsProviderInfo> mUninstalledLeosWidgets;
    private List<LenovoWidgetsProviderInfo> mPushLeosWidgets;
    
    private static final String PREFIX_LENOVO_WIDGETS_CONFIG_PATH = "/data/data/com.lenovo.launcher/files/extra/widget.xml";

    public FetchLenovoWidgetUtil(Context c){
		mContext = c;
		//modify by zhanggx1 on 2013-08-01. pur: 优化取所有联想WIDGET的时间 . s
//		mInstalledLeosWidgets = fetchAllInstalledLeosWidgets();
		mInstalledLeosWidgets = fetchAllInstalledLeosWidgetsNew();
		//modify by zhanggx1 on 2013-08-01. pur: 优化取所有联想WIDGET的时间 . e
		mPushLeosWidgets = loadLeosWidgets();
		mUninstalledLeosWidgets = fetchUninstallLoesWidgets(mInstalledLeosWidgets,mPushLeosWidgets);
	}
    
    //add by zhanggx1 on 2013-08-01. pur: 优化取所有联想WIDGET的时间, 减少300-500毫秒时间 . s
    /**
     * 功能同fetchAllInstalledLeosWidgets
     * @return
     */
    private List<LenovoWidgetsProviderInfo> fetchAllInstalledLeosWidgetsNew() {
		PackageManager pm = mContext.getPackageManager();
		
		//查询所有ACTION包含LENOVO_WIDGETS_ACTION的信息
		Intent intent = new Intent(LENOVO_WIDGETS_ACTION);
		List<ResolveInfo> infoList = pm.queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);
		
		List<LenovoWidgetsProviderInfo> installedLeosWidgets =new LinkedList<LenovoWidgetsProviderInfo>();
		Map<String, LenovoWigetInfo> widgetsInfoMap  = new HashMap<String, LenovoWigetInfo>();
		
		if (infoList == null || infoList.size() <= 0) {
			return installedLeosWidgets;
		}
		
		//将查询结果按包名进行排序，主要是按包名进行分组用
		Collections.sort(infoList, new PackageNameComparator());
		
		String packageName = null;
		Context friendContext=null;
		for (ResolveInfo rInfo : infoList) {
			if (rInfo.activityInfo == null || rInfo.activityInfo.packageName == null) {
				continue;
			}
			
			//只有当前rInfo的包名不等于前一个rInfo的包名时，才读取lenovo_widget_provider_info.xml， 清除widgetsInfoMap缓存
			if (!rInfo.activityInfo.packageName.equals(packageName)) {
				packageName = rInfo.activityInfo.packageName;
				
				widgetsInfoMap.clear();				
				
				//读当前包里的lenovo_widget_provider_info.xml，该段代码同fetchAllInstalledLeosWidgets函数里完全一样，未做修改
				try {
					friendContext = mContext.createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY);
					int id = friendContext.getResources().getIdentifier(LENOVO_WIDGETS_XML_NAME, "xml", packageName);
					if (id == 0) {
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
			                if (widgetsConfig.getName().equals("lenovo-widget")) {
			                	LenovoWigetInfo lwi = new LenovoWigetInfo();
			                	lwi.activity_name = widgetsConfig.getAttributeValue(null,"activity_name");
			                	lwi.widgetView_name = widgetsConfig.getAttributeValue(null,"WidgetView");
			                	lwi.x = Integer.parseInt(widgetsConfig.getAttributeValue(null,"Width"));
			                	lwi.y = Integer.parseInt(widgetsConfig.getAttributeValue(null,"Height"));
			                	Log.w(TAG, "FetchInstallPackageUtil-->fetchAllInstalledLeosWidgets(): lwi.x " + lwi.x);
			                	Log.w(TAG, "FetchInstallPackageUtil-->fetchAllInstalledLeosWidgets(): lwi.y " + lwi.y);
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
			}
			
			//解析每一个rInfo，该段代码同fetchAllInstalledLeosWidgets函数里一样，只将 widgetsInfoMap.get(key)的判断提前了
			String className = rInfo.activityInfo.name;
			if(className== null){
				continue;
			}
			String cn[] = className.split("\\.");
			if(cn ==null || cn.length < 1){
				continue;
			}
			int length = cn.length;
			String key = cn[length-1];
			
			LenovoWigetInfo info = (LenovoWigetInfo) widgetsInfoMap.get(key);
			if(info == null || info.x < 1 || info.y < 1 || info.widgetView_name == null){
				continue;
			}

			LenovoWidgetsProviderInfo lpi = new LenovoWidgetsProviderInfo();
			lpi.icon = friendContext.getResources().getDrawable(rInfo.activityInfo.icon);//rInfo.activityInfo.loadIcon(pm);// p.applicationInfo.loadIcon();
			lpi.isInstalled = true;
			lpi.appPackageName = packageName;
			Log.d(TAG, "FetchInstallPackageUtil-->fetchAllInstalledLeosWidgets() : lpi.appPackageName ="+ lpi.appPackageName);
			lpi.appName =(String)rInfo.activityInfo.loadLabel(pm);
            Log.d(TAG, "FetchInstallPackageUtil-->fetchAllInstalledLeosWidgets() : lpi.appName ="+ lpi.appName);
			
			lpi.x = info.x;
			lpi.y = info.y;
			lpi.widgetView = className.substring(0, className.length()-1-key.length()) + "."+info.widgetView_name;
            Log.d(TAG, "FetchInstallPackageUtil-->fetchAllInstalledLeosWidgets() : lpi.widgetView ="+ lpi.widgetView);

			installedLeosWidgets.add(lpi);
		}
		return installedLeosWidgets;
	}
    
    /**
     * 将List<ResolveInfo>对象按包名进行排序
     * @author zhanggx1
     *
     */
	public static class PackageNameComparator implements
			Comparator<ResolveInfo> {
		public PackageNameComparator() {
		}

		public final int compare(ResolveInfo a, ResolveInfo b) {
			CharSequence sa = a.activityInfo == null ? "" : a.activityInfo.packageName;
			CharSequence sb = b.activityInfo == null ? "" : b.activityInfo.packageName;

			return sCollator.compare(sa.toString(), sb.toString());
		}

		private final Collator sCollator = Collator.getInstance();
	}
	//add by zhanggx1 on 2013-08-01. pur: 优化取所有联想WIDGET的时间 . e
    
    
	/**
	 * 获取所有 leos widgets的package name
	 * get all installed leos widgets‘ packageName 
	 * @return all installed leos widgets
	 */
	private List<LenovoWidgetsProviderInfo> fetchAllInstalledLeosWidgets(){
		PackageManager pm = mContext.getPackageManager();
		List<PackageInfo> packages =pm.getInstalledPackages(0);
		List<LenovoWidgetsProviderInfo> installedLeosWidgets =new LinkedList<LenovoWidgetsProviderInfo>();
		Map<String, LenovoWigetInfo> widgetsInfoMap  = new HashMap<String, LenovoWigetInfo>();
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
			                if(widgetsConfig.getName().equals("lenovo-widget")){
			                	LenovoWigetInfo lwi = new LenovoWigetInfo();
			                	lwi.activity_name = widgetsConfig.getAttributeValue(null,"activity_name");
			                	lwi.widgetView_name = widgetsConfig.getAttributeValue(null,"WidgetView");
			                	lwi.x = Integer.parseInt(widgetsConfig.getAttributeValue(null,"Width"));
			                	lwi.y = Integer.parseInt(widgetsConfig.getAttributeValue(null,"Height"));
			                	Log.w(TAG, "FetchInstallPackageUtil-->fetchAllInstalledLeosWidgets(): lwi.x " + lwi.x);
			                	Log.w(TAG, "FetchInstallPackageUtil-->fetchAllInstalledLeosWidgets(): lwi.y " + lwi.y);
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
					LenovoWigetInfo info =(LenovoWigetInfo) widgetsInfoMap.get(key);
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
		//List<LenovoWidgetsProviderInfo> uninstalledLeosWidgets = new LinkedList<LenovoWidgetsProviderInfo>();
//		for(LenovoWidgetsProviderInfo pn : pushLeosWidgets){
//			isEquals = false;
//			for(LenovoWidgetsProviderInfo p :installedLeosWidgets){
//				if(pn.widgetPackageName.equals(p.widgetPackageName)){
//					isEquals = true;
//					break;
//				}
//			}
//			if(!isEquals){
//				pn.icon = mContext.getResources().getDrawable(R.drawable.ic_allapps);
//				uninstalledLeosWidgets.add(pn);
//			}
//		}
		int size = pushLeosWidgets.size();
		for(int i= 0;i<size;i++){
			LenovoWidgetsProviderInfo temp =pushLeosWidgets.get(i);
			isEquals = false;
			for(LenovoWidgetsProviderInfo p :installedLeosWidgets){
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
	private List<LenovoWidgetsProviderInfo> loadLeosWidgets() {
		
    	List<LenovoWidgetsProviderInfo> pushLeosWidgets = new LinkedList<LenovoWidgetsProviderInfo>();
//    	
//    	FileInputStream in =null;
//		try {
//			in = new FileInputStream(PREFIX_LENOVO_WIDGETS_CONFIG_PATH);
//			XmlPullParser parser = Xml.newPullParser(); 
//			try {
//				parser.setInput(in, "UTF-8");
//				XmlUtils.beginDocument(parser, TAG_LEOSWIDGETS);
//				final int depth = parser.getDepth();
//				int type;
//		        while (((type = parser.next()) != XmlPullParser.END_TAG ||
//		        		parser.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {
//		            if (type != XmlPullParser.START_TAG) {
//		                continue;
//		            }
//		            if(parser.getName().equals("leoswidget")){
//		            	Log.w(TAG, "FetchInstallPackageUtil-->loadLeosWidgets():  " + parser.getAttributeValue(null,"widget:widgetName"));
//		            }
//		        }
//			} catch (XmlPullParserException e) {
//				e.printStackTrace();
//				return pushLeosWidgets;
//			} catch (IOException e) {
//				e.printStackTrace();
//				return pushLeosWidgets;
//			}
//		} catch (FileNotFoundException e1) {
//			Log.w(TAG, "FetchInstallPackageUtil-->loadLeosWidgets():  no file" );
//			return pushLeosWidgets;
//		}
		
		
    	
        try {
            XmlResourceParser parser = mContext.getResources().getXml(R.xml.widegt);
            AttributeSet attrs = Xml.asAttributeSet(parser);
            XmlUtils.beginDocument(parser, TAG_LEOSWIDGETS);

            final int depth = parser.getDepth();

            int type;
            while (((type = parser.next()) != XmlPullParser.END_TAG ||
                    parser.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {

                if (type != XmlPullParser.START_TAG) {
                    continue;
                }
                LenovoWidgetsProviderInfo lwp = new LenovoWidgetsProviderInfo();
                TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.Widget);
                lwp.appName = a.getString(R.styleable.Widget_widgetName);
                lwp.iconName = a.getString(R.styleable.Widget_iconName);
                lwp.widgetUrl = a.getString(R.styleable.Widget_widgetUrl);
                lwp.widgetPackageName = a.getString(R.styleable.Widget_widgetPackage);
                lwp.icon = null;
                lwp.x = -1;
                lwp.y = -1;
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
    
    
	public ArrayList<LenovoWidgetsProviderInfo> getAllLeosWidgets() {
		mInstalledLeosWidgets.addAll(mUninstalledLeosWidgets);
		return new ArrayList<LenovoWidgetsProviderInfo>(mInstalledLeosWidgets);
	}

	
	public class LenovoWigetInfo{
		public int x;
		public int y;
		public String activity_name;
		public String widgetView_name;
	}
}
