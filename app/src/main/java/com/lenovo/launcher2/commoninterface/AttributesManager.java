package com.lenovo.launcher2.commoninterface;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.lenovo.launcher2.commoninterface.DesiredExceptions.NotSupportedTypeException;
import com.lenovo.launcher2.commoninterface.InfoFactory.FolderInfo;
import com.lenovo.launcher2.commoninterface.InfoFactory.*;
import com.lenovo.launcher2.customizer.ConstantAdapter.ProfilesAttributes.XM3DWidgetAttributes;

/**
 * This class will provide all excepted values which you want to get and
 * configure our launcher as soon as it launched at the first time for you. You
 * can slot your values in the customize file whose path was defined in
 * ConstantAdapter. It's worthy to tell that the values will never be retrieved
 * once done.
 * 
 * @author ChengLiang
 * */
public class AttributesManager {

	private List<ProfileInfo> mProfiles = null;

	private XmlProfilesInflater inflater = null;

	public AttributesManager(List<ProfileInfo> profiles) {
		mProfiles = profiles;
	}

	public AttributesManager(ProfileInfo profile) {
		mProfiles = new ArrayList<ProfileInfo>();
		if (profile != null)
			mProfiles.add(profile);
	}

	// use default profile
	public AttributesManager() {
		inflater = new XmlProfilesInflater();
		mProfiles = new ArrayList<ProfileInfo>();
		initDataContainer();
	}

	private void initDataContainer() {
		try {
			inflater.forceParse();
		} catch (Exception e) {
			e.printStackTrace();
		}

		while (inflater.isProcessing()) {
			try {
				Thread.sleep(10L);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		if (inflater.isParsed()) {
			try {
				mProfiles = inflater.getCustomizedProfiles();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * Get a profile by the given key AND name. This means that the result will
	 * be exactly match the given key and name. Because of the profile is on top
	 * level of the tree, the owner don't need to be specified.
	 * 
	 * @param key
	 * @param name
	 * 
	 * @return An instance of {@link ProfileInfo}. The returned value maybe
	 *         empty, but was always NOT null.
	 * 
	 * */
	public ProfileInfo getProfileByKeyAndName(String key, String name) {
		return getProfile(key, name, true);
	}

	/**
	 * Get a profile by the given key OR name. Because of the profile is on top
	 * level of the tree, the owner don't need to be specified. The result will
	 * be NOT exactly match the given key and name.
	 * 
	 * 
	 * @param key
	 * @param name
	 * 
	 * @return Once the key OR the name was matched, an instance of
	 *         {@link ProfileInfo} will be returned. The returned value maybe
	 *         empty, but was always NOT null .
	 * 
	 * */
	public ProfileInfo getProfileByKeyOrName(String key, String name) {
		return getProfile(key, name, false);
	}

	private ProfileInfo getProfile(String key, String name, boolean strictMode) {
		BaseInfo bi = getTargetInfoAtMatchedDegree(mProfiles, key, name, strictMode);
		return bi == null ? InfoFactory.INSTANCE.new ProfileInfo() : (ProfileInfo) bi;
	}

	/**
	 * Get a folder which belongs to the specified profile by the given key AND
	 * name. This means that the result will be exactly match the given key and
	 * name.
	 * 
	 * @param owner
	 *            The profile contains the expected folder.
	 * @param key
	 * @param name
	 * 
	 * @return An instance of {@link FolderInfo}. The returned value maybe
	 *         empty, but was always NOT null.
	 * 
	 * */
	public FolderInfo getFolderByKeyAndName(ProfileInfo owner, String key, String name) {
		return getFolder(owner, key, name, true);
	}

	/**
	 * Get a folder by the given key OR name. Because of the profile is on top
	 * level of the tree, the owner don't need to be specified. The result will
	 * be NOT exactly match the given key and name.
	 * 
	 * @param owner
	 *            The profile contains the expected folder.
	 * @param key
	 * @param name
	 * 
	 * @return Once the key OR the name was matched, an instance of
	 *         {@link FolderInfo} will be returned. The returned value maybe
	 *         empty, but was always NOT null .
	 * 
	 * */
	public FolderInfo getFolderByKeyOrName(ProfileInfo owner, String key, String name) {
		return getFolder(owner, key, name, false);
	}

	private FolderInfo getFolder(ProfileInfo owner, String key, String name, boolean strictMode) {
		BaseInfo bi = getTargetInfoAtMatchedDegree(
				owner == null ? InfoFactory.INSTANCE.new ProfileInfo().folders : owner.folders,
				key, name, strictMode);
		return bi == null ? InfoFactory.INSTANCE.new FolderInfo() : (FolderInfo) bi;
	}

	/**
	 * Get a priority which belongs to the specified profile by the given key
	 * AND name. This means that the result will be exactly match the given key
	 * and name.
	 * 
	 * @param owner
	 *            The profile contains the expected priority.
	 * @param key
	 * @param name
	 * 
	 * @return An instance of {@link PriorityInfo}. The returned value maybe
	 *         empty, but was always NOT null.
	 * 
	 * */
	public PriorityInfo getPriorityAndKeyAndName(ProfileInfo owner, String key, String name) {
		return getPriority(owner, key, name, true);
	}

	/**
	 * Get a priority by the given key OR name. Because of the profile is on top
	 * level of the tree, the owner don't need to be specified. The result will
	 * be NOT exactly match the given key and name.
	 * 
	 * @param owner
	 *            The profile contains the expected priority.
	 * @param key
	 * @param name
	 * 
	 * @return Once the key OR the name was matched, an instance of
	 *         {@link PriorityInfo} will be returned. The returned value maybe
	 *         empty, but was always NOT null .
	 * 
	 * */
	public PriorityInfo getPriorityAndKeyOrName(ProfileInfo owner, String key, String name) {
		return getPriority(owner, key, name, false);
	}

	private PriorityInfo getPriority(ProfileInfo owner, String key, String name, boolean strictMode) {
		BaseInfo bi = getTargetInfoAtMatchedDegree(
				owner == null ? InfoFactory.INSTANCE.new ProfileInfo().priorities
						: owner.priorities, key, name, strictMode);
		return bi == null ? InfoFactory.INSTANCE.new PriorityInfo() : (PriorityInfo) bi;
	}

	/**
	 * Get a setting which belongs to the specified profile by the given key AND
	 * name. This means that the result will be exactly match the given key and
	 * name.
	 * 
	 * @param owner
	 *            The profile contains the expected setting.
	 * @param key
	 * @param name
	 * 
	 * @return An instance of {@link SettingInfo}. The returned value maybe
	 *         empty, but was always NOT null.
	 * 
	 * */
	public SettingInfo getSettingByKeyAndName(ProfileInfo owner, String key, String name) {
		return getSetting(owner, key, name, true);
	}

	/**
	 * Get a setting by the given key OR name. Because of the profile is on top
	 * level of the tree, the owner don't need to be specified. The result will
	 * be NOT exactly match the given key and name.
	 * 
	 * @param owner
	 *            The profile contains the expected setting.
	 * @param key
	 * @param name
	 * 
	 * @return Once the key OR the name was matched, an instance of
	 *         {@link SettingInfo} will be returned. The returned value maybe
	 *         empty, but was always NOT null .
	 * 
	 * */
	public SettingInfo getSettingByKeyOrName(ProfileInfo owner, String key, String name) {
		return getSetting(owner, key, name, false);
	}

	private SettingInfo getSetting(ProfileInfo owner, String key, String name, boolean strictMode) {
		BaseInfo bi = getTargetInfoAtMatchedDegree(
				owner == null ? InfoFactory.INSTANCE.new ProfileInfo().settings : owner.settings,
				key, name, strictMode);
		return bi == null ? InfoFactory.INSTANCE.new SettingInfo() : (SettingInfo) bi;
	}

	/**
	 * Get a widget configuration info which belongs to the specified profile by
	 * the given key AND name. This means that the result will be exactly match
	 * the given key and name.
	 * 
	 * 
	 * @param owner
	 *            The profile contains the expected widget.
	 * @param key
	 * @param name
	 * 
	 * @return An instance of {@link WidgetInfo}. The returned value maybe
	 *         empty, but was always NOT null.
	 * 
	 * */
	public WidgetInfo getWidgetByKeyAndName(ProfileInfo owner, String key, String name) {
		return getWidget(owner, key, name, true);
	}

	/**
	 * Get a widget by the given key OR name. Because of the profile is on top
	 * level of the tree, the owner don't need to be specified. The result will
	 * be NOT exactly match the given key and name.
	 * 
	 * @param owner
	 *            The profile contains the expected widget.
	 * @param key
	 * @param name
	 * 
	 * 
	 * @return Once the key OR the name was matched, an instance of
	 *         {@link WidgetInfo} will be returned. The returned value maybe
	 *         empty, but was always NOT null .
	 * 
	 * */
	public WidgetInfo getWidgetByKeyOrName(ProfileInfo owner, String key, String name) {
		return getWidget(owner, key, name, true);
	}

	private WidgetInfo getWidget(ProfileInfo owner, String key, String name, boolean strictMode) {
		BaseInfo bi = getTargetInfoAtMatchedDegree(
				owner == null ? InfoFactory.INSTANCE.new ProfileInfo().widgets : owner.widgets,
				key, name, strictMode);
		return bi == null ? InfoFactory.INSTANCE.new WidgetInfo() : (WidgetInfo) bi;
	}

	/**
	 * Fetch an attribute which belongs to the specified info. The container
	 * must be the following ones: {@link WidgetInfo}, {@link SettingInfo},
	 * {@link AppInfo}, {@link ConfigInfo}, {@link QuickEntryInfo}. Because only
	 * these info-s own attribute directly.
	 * 
	 * @param info
	 *            The attribute attribute-holding container
	 * @param attrName
	 *            The attribute name
	 * 
	 * @return An initialized instance of {@link Attribute } will be returned
	 *         once found with the given attribute name, or an empty attribute
	 *         will be returned.
	 * */
	public Attribute getAttributeByName(BaseInfo info, String attrName)
			throws NotSupportedTypeException {
		if (attrName == null || info == null)
			return InfoFactory.INSTANCE.new Attribute();
		List<Attribute> attrList = null;
		if (info instanceof ConfigInfo) {
			attrList = ((ConfigInfo) info).configValues;
		} else if (info instanceof AppInfo) {
			attrList = ((AppInfo) info).attrList;
		} else if (info instanceof WidgetInfo) {
			attrList = ((WidgetInfo) info).attibutes;
		} else if (info instanceof SettingInfo) {
			attrList = ((SettingInfo) info).attrList;
		} else if (info instanceof QuickEntryInfo) {
			attrList = ((QuickEntryInfo) info).attrList;
		}		
		 /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-10 START */
		else if (info instanceof LeosE2EWidgetInfo) {
			attrList = ((LeosE2EWidgetInfo) info).attibutes;
		}
	    /*RK_ID:RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-10-10 END */
		else if (info instanceof TitleInfo) {
			attrList = ((TitleInfo) info).attrList;
		}
		else
			throw new NotSupportedTypeException(info);

		for (int i = 0; i < attrList.size(); i++) {
			if (attrList.get(i).getAttrName().equalsIgnoreCase(attrName))
				return attrList.get(i);
		}

		return InfoFactory.INSTANCE.new Attribute();
	}

	private enum MatchDegree {
		FULL_MATCH, HALF_MATCH, NO_MATCH
	};

	private final BaseInfo getTargetInfoAtMatchedDegree(List<? extends BaseInfo> infoList,
			String key, String name, boolean strictMode) {
		if (infoList == null || mProfiles.isEmpty()) {
			return null;
		}

		BaseInfo info = null;
		Iterator<? extends BaseInfo> it = infoList.iterator();
		while (it.hasNext()) {
			info = it.next();
			if (info.getKey().equals(key) || info.name.equals(name)) {
				MatchDegree degree = judgeMatchInInfo(info, key, name);
				if (degree == MatchDegree.FULL_MATCH)
					return info;
				if (degree == MatchDegree.HALF_MATCH && !strictMode)
					return info;
				if (degree == MatchDegree.NO_MATCH)
					continue;
			}
		}
		return null;
	}

	private MatchDegree judgeMatchInInfo(BaseInfo info, String key, String name) {
		if (info == null || info.getKey() == null) {
			return MatchDegree.NO_MATCH;
		}

		byte dKey = 0, dName = 0, sum = 0;

		if (key == null)
			dKey -= 1;
		else
			dKey += 1;

		if (key != null && key.trim().equals(info.getKey()))
			dKey += 1;
		else
			dKey -= 1;

		if (name == null)
			dName -= 1;
		else
			dName += 1;

		if (name != null && name.trim().equals(info.name))//change by xingqx for sonar
			dName += 1;
		else
			dName -= 1;

		sum = (byte) (dKey + dName);

		// R2
		// R2.echo("dKey : " + dKey + " , dName " + dName + " , sum : " + sum);
		// 2R

		if (sum >= 4)
			return MatchDegree.FULL_MATCH;
		if (sum == 2)
			return MatchDegree.HALF_MATCH;
		if (sum == 0) {
			if (dKey == 0 && dName == 0)
				return MatchDegree.NO_MATCH;
			if (dKey == 2 || dName == 2)
				return MatchDegree.HALF_MATCH;
		}
		if (sum < 0)
			return MatchDegree.NO_MATCH;

		return MatchDegree.NO_MATCH;
	}
}
