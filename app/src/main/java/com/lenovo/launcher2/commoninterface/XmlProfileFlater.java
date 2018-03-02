package com.lenovo.launcher2.commoninterface;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.net.INetworkStatsService;
import android.util.Xml;

import com.lenovo.launcher2.commoninterface.InfoFactory.*;
import com.lenovo.launcher2.commoninterface.InfoFactory.FolderInfo;

import com.lenovo.launcher2.customizer.ConstantAdapter;
import com.lenovo.launcher2.customizer.Debug;
import com.lenovo.launcher2.customizer.Debug.R2;
import com.lenovo.launcher2.customizer.SettingsValue;


public class XmlProfileFlater {

	private ProfileInfo profile = null;

	private XmlSerializer serializer = null;
	private FileOutputStream outStream = null;
	private BufferedWriter writer = null;
	private OutputStreamWriter outStreamWriter = null;
    private static Context mContext;
	public static XmlProfileFlater getInstance(Context context) {
		mContext = context;
		return new XmlProfileFlater();
	}

	// 0
	void flat(ProfileInfo info) {
		try {
			if (info.name == null || "".equals(info.name)) {
				info.name = "default";
			}
			profile = info;

			prepareFlat();

			flatProfiles();

			endFlat();
			
			profile = null;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// first
	void prepareFlat() {

		File xmlFile = new File(
				ConstantAdapter.DIR_PARENT_OF_STORAGE_BACKUP_FILE
						+ ConstantAdapter.DIR_TO_STORAGE_BACKUP_FILE
						+ File.separator + profile.name + File.separator
						+ ConstantAdapter.PROFILE_DESC_XML_NAME);

		if (Debug.MAIN_DEBUG_SWITCH)
			R2.echo("Will feed up : " + xmlFile.getPath());

		try {
			serializer = Xml.newSerializer();
			outStream = new FileOutputStream(xmlFile);
			outStreamWriter = new OutputStreamWriter(outStream, "UTF-8");
			writer = new BufferedWriter(outStreamWriter);

			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
		} catch (Exception e) {
			e.printStackTrace();
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("Exception  ---1!");
		}

	}

	// last
	void endFlat() {
		try {
			serializer.endDocument();
			outStream.close();
			writer.close();
			outStreamWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("Exception  ---0!");
		}
	}

	void flatProfiles() throws Exception {
		serializer.text("\n");
		serializer.startTag("", ConstantAdapter.PROFILES);
		if(SettingsValue.getSingleLayerValue(mContext)){
			if(SettingsValue.getCurrentMachineType(mContext)!=-1)
			{
				serializer.attribute("",ConstantAdapter.VERSION, "2.0pad");	
			}else{
				serializer.attribute("",ConstantAdapter.VERSION, "2.0");	
			}
		}else{
			serializer.attribute("",ConstantAdapter.VERSION, "1.0");
		}
		
		flatProfile(profile);
		serializer.endTag("", ConstantAdapter.PROFILES);
		serializer.text("\n");

	}

	void flatProfile(ProfileInfo info) throws Exception {
		// profile
		serializer.text("\n");
		serializer.startTag("", ConstantAdapter.PROFILE);
		serializer.attribute("", ConstantAdapter.KEY, info.getKey());
		serializer.attribute("", ConstantAdapter.NAME, info.name);

		flatFolders();
		serializer.text("\n");
		flatPriorities();
		serializer.text("\n");
		flatWidgets();
		serializer.text("\n");		
		 /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-10 START */
		flatLeosE2Ewidgets();
		serializer.text("\n");
		 /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-10 END */
		flatQuickEntries();
		serializer.text("\n");
		flatSettings();

		// ~ profile
		serializer.endTag("", ConstantAdapter.PROFILE);
		serializer.text("\n");
	}

	void flatQuickEntries() throws Exception {

		serializer.text("\n");

		// widgets
		serializer.startTag("", ConstantAdapter.QUICKENTIRES);

		// widget
		for (int i = 0; i < profile.quickentries.size(); i++) {
			try {
				flatAttributeCarrier(profile.quickentries.get(i));
			} catch (Exception e) {
				continue;
			}
		}

		// widgets
		serializer.endTag("", ConstantAdapter.QUICKENTIRES);
		serializer.text("\n");
	}

	void flatFolders() throws Exception {
		// Folders
		serializer.text("\n");
		serializer.startTag("", ConstantAdapter.FOLDERS);

		for (int i = 0; i < profile.folders.size(); i++) {
			FolderInfo folder = profile.folders.get(i);
			// folder
			plantModuleHeadTag(folder);

			// config
			flatAttributeCarrier(folder.config);

			// applist
			serializer.startTag("", ConstantAdapter.FOLDER_APPS);

			// apps
			for (int j = 0; j < folder.appList.folderApps.size(); j++) {
				try {
					flatAttributeCarrier(folder.appList.folderApps.get(j));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				}
			}

			// applist
			serializer.endTag("", ConstantAdapter.FOLDER_APPS);
			serializer.text("\n");

			// ~ folder
			plantModuleEndTag(folder);
		}

		// Folders
		serializer.endTag("", ConstantAdapter.FOLDERS);
		serializer.text("\n");
	}

	void flatPriorities() throws Exception {
		// Priorities
		serializer.startTag("", ConstantAdapter.PRIORITIES);

		for (int i = 0; i < profile.priorities.size(); i++) {
			PriorityInfo priority = profile.priorities.get(i);

			// priority
			plantModuleHeadTag(priority);

			// config
			if (!priority.config.configValues.isEmpty())
				flatAttributeCarrier(priority.config);

			// rules
			for (int j = 0; j < priority.priorityRules.size(); j++) {
				try {
					flatAttributeCarrier(priority.priorityRules.get(j));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				}
			}

			// ~ priority
			plantModuleEndTag(priority);
		}

		// Priorities
		serializer.endTag("", ConstantAdapter.PRIORITIES);
		serializer.text("\n");
	}

	void flatWidgets() throws Exception {

		serializer.text("\n");

		// widgets
		serializer.startTag("", ConstantAdapter.WIDGETS);

		// widget
		for (int i = 0; i < profile.widgets.size(); i++) {
			try {
				flatAttributeCarrier(profile.widgets.get(i));
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}

		// widgets
		serializer.endTag("", ConstantAdapter.WIDGETS);
		serializer.text("\n");
	}
	
 /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-10 START */
	void flatLeosE2Ewidgets() throws Exception {

		serializer.text("\n");

		// LeosE2Ewidgets
		serializer.startTag("", ConstantAdapter.LEOSE2EWIDGETS);

		// LeosE2Ewidgets
		for (int i = 0; i < profile.leosE2EWidgets.size(); i++) {
			try {
				flatAttributeCarrier(profile.leosE2EWidgets.get(i));
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}

		serializer.endTag("", ConstantAdapter.LEOSE2EWIDGETS);
		serializer.text("\n");
	}

	 /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-10 END */
	void flatSettings() throws Exception {

		serializer.text("\n");

		// settings
		serializer.startTag("", ConstantAdapter.SETTINGS);

		// setting
		for (int i = 0; i < profile.settings.size(); i++) {
			try {
				flatAttributeCarrier(profile.settings.get(i));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
		}

		// settings
		serializer.endTag("", ConstantAdapter.SETTINGS);
		serializer.text("\n");
	}

	void plantModuleEndTag(BaseInfo info) {
		try {
			serializer.endTag("", getTagName(info));
			serializer.text("\n");

		} catch (Exception e) {

			e.printStackTrace();
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("Exception  ---343!");

		}

	}

	String getTagName(BaseInfo info) {
		String targetTag = null;

		if (info instanceof FolderInfo) {
			targetTag = ConstantAdapter.FOLDER;
		} else if (info instanceof PriorityInfo) {
			targetTag = ConstantAdapter.PRIORITY;
		}

		return targetTag;
	}

	void plantModuleHeadTag(BaseInfo info) {
		try {
			serializer.text("\n");
			serializer.startTag("", getTagName(info));

			serializer.attribute("", ConstantAdapter.KEY, info.getKey());
			serializer.attribute("", ConstantAdapter.NAME, info.name);
			// serializer.text("\n");

		} catch (Exception e) {

			e.printStackTrace();
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("Exception  ---3!");

		}
	}

	void flatAttributeCarrier(BaseInfo info) {

		try {

			String targetTag = null;
			List<Attribute> attrList = new ArrayList<Attribute>();
			if (info instanceof SettingInfo) {
				targetTag = ConstantAdapter.SETTING;
				attrList = ((SettingInfo) info).attrList;
			} else if (info instanceof WidgetInfo) {
				targetTag = ConstantAdapter.WIDGET;
				attrList = ((WidgetInfo) info).attibutes;
			} else if (info instanceof ConfigInfo) {
				targetTag = ConstantAdapter.CONFIG;
				attrList = ((ConfigInfo) info).configValues;
			} else if (info instanceof AppInfo) {
				targetTag = ConstantAdapter.APP;
				attrList = ((AppInfo) info).attrList;
			} else if (info instanceof QuickEntryInfo) {
				targetTag = ConstantAdapter.QICKENTRY;
				attrList = ((QuickEntryInfo) info).attrList;
			}			
			 /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-10 START */
			else if (info instanceof LeosE2EWidgetInfo) {
				targetTag = ConstantAdapter.LEOSE2EWIDGET;
				attrList = ((LeosE2EWidgetInfo) info).attibutes;
			}
			 /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-10 END */
			serializer.text("\n");
			serializer.startTag("", targetTag);

			for (int i = 0; i < attrList.size(); i++) {
				Attribute attr = attrList.get(i);
				try {
					if("title" == attr.getAttrName() && !isLegalName(attr.getAttrValue())){
						serializer.attribute("", attr.getAttrName(),
								"");
					}else{
						serializer.attribute("", attr.getAttrName(),
								attr.getAttrValue());
					}
				} catch (Throwable e) {
					e.printStackTrace();
					continue;
				}
			}

			serializer.endTag("", targetTag);
			serializer.text("\n");

		} catch (Exception e) {
			e.printStackTrace();
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("Exception  ---4!");
		}
	}
	
	private boolean isLegalName(String s){
		boolean isTrue = false;
		if(s.length() != 0){
			for(int i = 0; i < s.length(); i++){
				char c = s.charAt(i);
				isTrue = (c >= 0x20 && c <= 0xd7ff) || (c >= 0xe000 && c <= 0xfffd);
				if(!isTrue){
					return false;
				}
			}
		}
		
		return true;
	}
}
