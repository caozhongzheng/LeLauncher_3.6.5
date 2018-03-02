package com.lenovo.launcher2.commoninterface;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.lenovo.launcher2.commoninterface.DesiredExceptions.InvalidAttributeException;
import com.lenovo.launcher2.commoninterface.DesiredExceptions.ParseNotPerformedException;
import com.lenovo.launcher2.commoninterface.DesiredExceptions.ParserInProcessException;
import com.lenovo.launcher2.commoninterface.InfoFactory.AppInfo;
import com.lenovo.launcher2.commoninterface.InfoFactory.AppsInfo;
import com.lenovo.launcher2.commoninterface.InfoFactory.Attribute;
import com.lenovo.launcher2.commoninterface.InfoFactory.BaseInfo;
import com.lenovo.launcher2.commoninterface.InfoFactory.ConfigInfo;
import com.lenovo.launcher2.commoninterface.InfoFactory.FolderInfo;
import com.lenovo.launcher2.commoninterface.InfoFactory.LeosE2EWidgetInfo;
import com.lenovo.launcher2.commoninterface.InfoFactory.PriorityInfo;
import com.lenovo.launcher2.commoninterface.InfoFactory.ProfileInfo;
import com.lenovo.launcher2.commoninterface.InfoFactory.QuickEntryInfo;
import com.lenovo.launcher2.commoninterface.InfoFactory.SettingInfo;
import com.lenovo.launcher2.commoninterface.InfoFactory.TitleInfo;
import com.lenovo.launcher2.commoninterface.InfoFactory.TitlesInfo;
import com.lenovo.launcher2.commoninterface.InfoFactory.WidgetInfo;
import com.lenovo.launcher2.customizer.ConstantAdapter;
import com.lenovo.launcher2.customizer.Debug;
import com.lenovo.launcher2.customizer.Debug.R2;


/**
 * Author : ChengLiang
 * */
public class XmlProfilesInflater extends DefaultHandler {
	private List<ProfileInfo> profiles = null;
	private InputStream inputStream = null;

	private InflaterState mInflaterState = InflaterState.NOT_PARSES_YET;

	private static enum InflaterState {
		READY, FAILED, IN_PROCESS, NOT_PARSES_YET
	};

	private List<FolderInfo> tmpFolders = null;
	private List<WidgetInfo> tmpWidgets = null;
	private List<PriorityInfo> tmpPriorities = null;
	private List<SettingInfo> tmpSettings = null;
	private List<QuickEntryInfo> tmpQuickEntries = null;
	
	 /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-10 START */
	private List<LeosE2EWidgetInfo> tmpLeosE2EWidgets = null;
	 /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-10 END */
	private ProfileInfo tmpProfile = null;
	private FolderInfo tmpFolder = null;
	private ConfigInfo tmpConfig = null;
	private AppsInfo tmpFolderApps = null;
	private AppInfo tmpAppInfo = null;
	
	private List<TitleInfo>  tmpFolderTitles = null;
	private TitleInfo tmpTitleInfo = null;

	private WidgetInfo tmpWidget = null;
	private SettingInfo tmpSetting = null;
	private PriorityInfo tmpPriority = null;
	private QuickEntryInfo tmpQuickEntry = null;	
	
	// LeosE2EWidgets
	private LeosE2EWidgetInfo tmpLeosE2EWidget = null;
	private String version;
	// LeosE2EWidgets
	public XmlProfilesInflater() {
		this(null);
	}

	public XmlProfilesInflater(InputStream is) {
		if (is == null)
			// TODO need a default profile!
			inputStream = null;
		else
			inputStream = is;

		if (inputStream == null) {
			mInflaterState = InflaterState.FAILED;
		}
	}

	public List<ProfileInfo> getCustomizedProfiles()
			throws ParserInProcessException, ParseNotPerformedException {
		if (mInflaterState == InflaterState.NOT_PARSES_YET) {

			throw new ParseNotPerformedException();

		} else if (mInflaterState == InflaterState.IN_PROCESS) {

			throw new ParserInProcessException();

		} else if (mInflaterState == InflaterState.READY) {

			return this.profiles;

		}

		return new ArrayList<ProfileInfo>();
	}
    public String getProfliesVersion(){
    	return this.version;
    }
	public boolean isParsed() {
		return mInflaterState == InflaterState.READY
				|| mInflaterState == InflaterState.NOT_PARSES_YET;
	}

	public boolean isFailed() {
		return mInflaterState == InflaterState.FAILED;
	}

	public boolean isReady() {
		return mInflaterState != InflaterState.IN_PROCESS;
	}

	public boolean isProcessing() {
		return mInflaterState == InflaterState.IN_PROCESS;
	}

	public void forceParse() throws ParserInProcessException {
		if (isReady()) {
			if (profiles != null) {
				if (Debug.MAIN_DEBUG_SWITCH)
					R2.echo("Profiles were already collected .");
				return;
			}

			mInflaterState = InflaterState.IN_PROCESS;
			new Thread() {
				public void run() {
					performParse();
				};
			}.start();
		} else
			throw new ParserInProcessException();
	}

	private void performParse() {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp;
			sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			xr.setContentHandler(this);
			xr.parse(new InputSource(inputStream));
		} catch (Exception e) {
			e.printStackTrace();
			mInflaterState = InflaterState.FAILED;
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();

				mInflaterState = InflaterState.FAILED;
			}
		}
	}

	@Override
	public void startDocument() throws SAXException {
		if (Debug.MAIN_DEBUG_SWITCH)
			R2.echo("Try to parse...");
		mInflaterState = InflaterState.IN_PROCESS;
		super.startDocument();
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();

		System.gc();
		if (Debug.MAIN_DEBUG_SWITCH)
			R2.echo("Parse over.");
		mInflaterState = InflaterState.READY;
	}

	private void setElementKey(BaseInfo info, Attributes attributes) {
		try {
			info.setKey(attributes.getValue(ConstantAdapter.KEY));
		} catch (Exception e) {
			e.printStackTrace();
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo(e.getMessage());
			// throw new SAXException();
		}

	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);

		if (ConstantAdapter.PROFILES.equals(localName)) {

			profiles = new ArrayList<ProfileInfo>();
			version = attributes.getValue(ConstantAdapter.VERSION);
		} else if (ConstantAdapter.PROFILE.equals(localName)) {

			tmpProfile = InfoFactory.INSTANCE.new ProfileInfo();
			tmpProfile.name = attributes.getValue(ConstantAdapter.NAME);
			setElementKey(tmpProfile, attributes);

		} else if (ConstantAdapter.FOLDERS.equals(localName)) {

			tmpFolders = new ArrayList<FolderInfo>();

		} else if (ConstantAdapter.FOLDER.equals(localName)) {

			tmpFolder = InfoFactory.INSTANCE.new FolderInfo();
			tmpFolder.name = attributes.getValue(ConstantAdapter.NAME);
			setElementKey(tmpFolder, attributes);

		} else if (ConstantAdapter.CONFIG.equals(localName)) {

			tmpConfig = InfoFactory.INSTANCE.new ConfigInfo();
			
		} else if (ConstantAdapter.FOLDER_TITLES.equals(localName)) {
			//tmpFolderTitles = InfoFactory.INSTANCE.new TitlesInfo();
			tmpFolderTitles = new ArrayList<TitleInfo>();

		} else if (ConstantAdapter.FOLDER_TITLE.equals(localName)) {
			tmpTitleInfo = InfoFactory.INSTANCE.new TitleInfo();
		} else if (ConstantAdapter.FOLDER_APPS.equals(localName)) {

			tmpFolderApps = InfoFactory.INSTANCE.new AppsInfo();

		} else if (ConstantAdapter.APP.equals(localName)) {

			tmpAppInfo = InfoFactory.INSTANCE.new AppInfo();

		} else if (ConstantAdapter.PRIORITIES.equals(localName)) {

			tmpPriorities = new ArrayList<PriorityInfo>();

		} else if (ConstantAdapter.PRIORITY.equals(localName)) {

			tmpPriority = InfoFactory.INSTANCE.new PriorityInfo();
			tmpPriority.name = attributes.getValue(ConstantAdapter.NAME);
			setElementKey(tmpPriority, attributes);

		} else if (ConstantAdapter.WIDGETS.equals(localName)) {

			tmpWidgets = new ArrayList<WidgetInfo>();

		} else if (ConstantAdapter.WIDGET.equals(localName)) {

			tmpWidget = InfoFactory.INSTANCE.new WidgetInfo();
			tmpWidget.name = attributes.getValue(ConstantAdapter.NAME);
			setElementKey(tmpWidget, attributes);

		} else if (ConstantAdapter.SETTINGS.equals(localName)) {

			tmpSettings = new ArrayList<SettingInfo>();

		} else if (ConstantAdapter.SETTING.equals(localName)) {

			tmpSetting = InfoFactory.INSTANCE.new SettingInfo();

		} else if (ConstantAdapter.QUICKENTIRES.equals(localName)) {

			tmpQuickEntries = new ArrayList<QuickEntryInfo>();

		} else if (ConstantAdapter.QICKENTRY.equals(localName)) {

			tmpQuickEntry = InfoFactory.INSTANCE.new QuickEntryInfo();
		}
		
		 /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-10 START */
		else if (ConstantAdapter.LEOSE2EWIDGETS.equals(localName)) {

			tmpLeosE2EWidgets = new ArrayList<LeosE2EWidgetInfo>();

		} else if (ConstantAdapter.LEOSE2EWIDGET.equals(localName)) {

			tmpLeosE2EWidget = InfoFactory.INSTANCE.new LeosE2EWidgetInfo();

		}
		
		 /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-10 END */
		// Collect all attributes for this tag
		List<Attribute> tmpAttributes = new ArrayList<Attribute>();
		for (int i = 0; i < attributes.getLength(); i++) {
			Attribute attr = InfoFactory.INSTANCE.new Attribute();
			attr.setAttrName(attributes.getLocalName(i));
			try {
				attr.setAttrValue(attributes.getValue(i));
			} catch (InvalidAttributeException e) {
				e.printStackTrace();
				continue;
			}
			tmpAttributes.add(attr);
		}

		if (tmpAppInfo != null) {
			tmpAppInfo.attrList.addAll(tmpAttributes);

		} else if (tmpConfig != null) {
			tmpConfig.configValues.addAll(tmpAttributes);

		} else if (tmpSetting != null) {
			tmpSetting.attrList.addAll(tmpAttributes);

		} else if (tmpWidget != null) {
			tmpWidget.attibutes.addAll(tmpAttributes);

		} else if (tmpQuickEntry != null) {
			tmpQuickEntry.attrList.addAll(tmpAttributes);
		}		
		 /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-10 START */
		else if (tmpLeosE2EWidget != null) {
			tmpLeosE2EWidget.attibutes.addAll(tmpAttributes);
		}
		 /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-10 END */
		else if (tmpTitleInfo != null) {
			tmpTitleInfo.attrList.addAll(tmpAttributes);
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);

		if (ConstantAdapter.CONFIG.equals(localName)) {

			if (tmpFolder != null)
				tmpFolder.config = tmpConfig;
			else if (tmpPriority != null) {
				tmpPriority.config = tmpConfig;
			}

			tmpConfig = null;
		} else if (ConstantAdapter.FOLDER_TITLE.equals(localName)) {

			if (tmpFolderTitles != null) {
				tmpFolderTitles.add(tmpTitleInfo);
			} 
			tmpTitleInfo = null;

		} else if (ConstantAdapter.FOLDER_TITLES.equals(localName)) {
			tmpFolder.titleList = tmpFolderTitles;
			tmpFolderTitles = null;
		} else if (ConstantAdapter.APP.equals(localName)) {

			if (tmpFolderApps != null) {

				tmpFolderApps.folderApps.add(tmpAppInfo);

			} else if (tmpPriority != null) {

				tmpPriority.priorityRules.add(tmpAppInfo);
			}

			tmpAppInfo = null;

		} else if (ConstantAdapter.FOLDER_APPS.equals(localName)) {

			tmpFolder.appList = tmpFolderApps;
			tmpFolderApps = null;

		} else if (ConstantAdapter.FOLDER.equals(localName)) {

			tmpFolders.add(tmpFolder);
			tmpFolder = null;

		} else if (ConstantAdapter.FOLDERS.equals(localName)) {

			tmpProfile.folders = tmpFolders;
			tmpFolders = null;

		} else if (ConstantAdapter.PRIORITY.equals(localName)) {

			tmpPriorities.add(tmpPriority);
			tmpPriority = null;

		} else if (ConstantAdapter.PRIORITIES.equals(localName)) {

			tmpProfile.priorities = tmpPriorities;
			tmpPriorities = null;

		} else if (ConstantAdapter.WIDGET.equals(localName)) {

			tmpWidgets.add(tmpWidget);
			tmpWidget = null;

		} else if (ConstantAdapter.WIDGETS.equals(localName)) {

			tmpProfile.widgets = tmpWidgets;
			tmpWidgets = null;

		} else if (ConstantAdapter.QICKENTRY.equals(localName)) {

			tmpQuickEntries.add(tmpQuickEntry);
			tmpQuickEntry = null;

		} else if (ConstantAdapter.QUICKENTIRES.equals(localName)) {

			tmpProfile.quickentries = tmpQuickEntries;
			tmpQuickEntries = null;

		} else if (ConstantAdapter.SETTING.equals(localName)) {

			tmpSettings.add(tmpSetting);
			tmpSetting = null;

		} else if (ConstantAdapter.SETTINGS.equals(localName)) {

			tmpProfile.settings = tmpSettings;
			tmpSettings = null;

		} else if (ConstantAdapter.PROFILE.equals(localName)) {

			profiles.add(tmpProfile);
			tmpProfile = null;

		}		
		 /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-10 START */
		else if (ConstantAdapter.LEOSE2EWIDGET.equals(localName)) {

			tmpLeosE2EWidgets.add(tmpLeosE2EWidget);
			tmpLeosE2EWidget = null;

		} else if (ConstantAdapter.LEOSE2EWIDGETS.endsWith(localName)) {

			tmpProfile.leosE2EWidgets = tmpLeosE2EWidgets;
			tmpLeosE2EWidgets = null;

		}
		 /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-10 END */
		
	}
}// end of inflater