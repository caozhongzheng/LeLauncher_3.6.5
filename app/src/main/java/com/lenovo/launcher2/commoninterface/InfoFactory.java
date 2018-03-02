package com.lenovo.launcher2.commoninterface;

import java.util.ArrayList;
import java.util.List;

import com.lenovo.launcher2.commoninterface.DesiredExceptions.InvalidAttributeException;
import com.lenovo.launcher2.customizer.ConstantAdapter.ProfilesAttributes.FolderTitleAttributes;


/**
 * Author : ChengLiang
 * */
public class InfoFactory {

	public static final InfoFactory INSTANCE = new InfoFactory();

	public class BaseInfo {
		// the key is most important
		private String key = "";

		public String name = "";

		public String getKey() {
			return key;
		}

		// public void setKey(String key) throws InvalidKeyNameException {
		public void setKey(String key) {
			// if (key == null || "".equalsIgnoreCase(key.trim()))
			// throw new InvalidKeyNameException(name);
			this.key = key;
		}
	}

	public class AppInfo extends BaseInfo {
		public List<Attribute> attrList = new ArrayList<Attribute>();
	}
	public class TitleInfo extends BaseInfo {
		public List<Attribute> attrList = new ArrayList<Attribute>();
	}
	
	public class Attribute {
		// we desired an attribute name at least
		private String attrName = "";
		private String attrValue = "";

		public Attribute() {

		}

		public Attribute(String name, String value) {

			attrName = name;
			attrValue = value;
		}

		public Attribute(String name) {
			this(name, "");
		}

		public String getAttrValue() {
			if (attrValue == null)
				attrValue = "";
			return attrValue;
		}

		public void setAttrValue(String attrValue) throws InvalidAttributeException {
			if (attrName == null || "".equalsIgnoreCase(attrName.trim()))
				throw new InvalidAttributeException(attrValue);
			this.attrValue = attrValue;
		}

		public String getAttrName() {
			return attrName;
		}

		public void setAttrName(String attrName) {
			this.attrName = attrName;
		}
	}

	public class FolderInfo extends BaseInfo {
		public ConfigInfo config = new ConfigInfo();
		public AppsInfo appList = new AppsInfo();
		public List<TitleInfo> titleList = new ArrayList<TitleInfo>();

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof FolderInfo)) {
				return false;
			}

			//FolderInfo info = (FolderInfo) o;
			

			return super.equals(o);
		}

	}

	public class ConfigInfo extends BaseInfo {
		public List<Attribute> configValues = new ArrayList<Attribute>();
	}

	public class AppsInfo extends BaseInfo {
		public List<AppInfo> folderApps = new ArrayList<AppInfo>();
	}
	public class TitlesInfo extends BaseInfo {
		public List<TitleInfo> folderTitles = new ArrayList<TitleInfo>();
	}

	public class PriorityInfo extends BaseInfo {
		public ConfigInfo config = new ConfigInfo();
		public List<AppInfo> priorityRules = new ArrayList<AppInfo>();
	}

	public class ProfileInfo extends BaseInfo {
		public boolean singleLayer = true;
		public List<FolderInfo> folders = new ArrayList<FolderInfo>();
		public List<PriorityInfo> priorities = new ArrayList<PriorityInfo>();
		public List<WidgetInfo> widgets = new ArrayList<WidgetInfo>();
		public List<SettingInfo> settings = new ArrayList<SettingInfo>();
		public List<QuickEntryInfo> quickentries = new ArrayList<QuickEntryInfo>();		
		 /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-10 START */
		public List<LeosE2EWidgetInfo> leosE2EWidgets = new ArrayList<LeosE2EWidgetInfo>();
		 /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-10 END */
	}

	public class SettingInfo extends BaseInfo {
		public List<Attribute> attrList = new ArrayList<Attribute>();
	}

	public class WidgetInfo extends BaseInfo {
		public List<Attribute> attibutes = new ArrayList<Attribute>();
	}

	public class QuickEntryInfo extends BaseInfo {
		public boolean needSaveSnap = true;
		public List<Attribute> attrList = new ArrayList<Attribute>();
	}	
	
	 /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-10 START */
	public class LeosE2EWidgetInfo extends BaseInfo {
		public List<Attribute> attibutes = new ArrayList<Attribute>();
	}
	 /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-10 END */
	private InfoFactory() {
		// TODO Auto-generated constructor stub
	}
}
