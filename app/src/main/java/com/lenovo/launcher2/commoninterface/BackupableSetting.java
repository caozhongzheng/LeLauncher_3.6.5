package com.lenovo.launcher2.commoninterface;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.lenovo.launcher2.commoninterface.InfoFactory.SettingInfo;
import com.lenovo.launcher2.customizer.ConstantAdapter;
import com.lenovo.launcher2.customizer.Debug;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.Utilities;
import com.lenovo.launcher2.customizer.ConstantAdapter.ProfilesAttributes.SettingAttributes;
import com.lenovo.launcher2.customizer.Debug.R2;

public class BackupableSetting {

	private static String WORKING_DIR = null;

	private Context mContext = null;

	// private String mCatetory = null;

	private BackupableSetting(Context context, String category) {
		mContext = context;
		// mCatetory = category;
	}

	public static BackupableSetting newInstance(final Context context,
			String category) {
		String strPath = null;
		/* Lenovo zhaoxin5 20131111 fix multi-user bug begin*/
		//Utilities.newInstance().ensureDir(strPath = ConstantAdapter.DIR_DATA);
		Utilities.newInstance().ensureDir(strPath = ConstantAdapter.getMyPackageDir(context));
		/* Lenovo zhaoxin5 20131111 fix multi-user bug end*/
		Utilities.newInstance().ensureDir(
				strPath = strPath + ConstantAdapter.DIR_PREFS);

		WORKING_DIR = strPath;

		BackupableSetting instance = new BackupableSetting(context, category);

		return instance;
	}

	public void restore(List<SettingInfo> settings) {

		if (Debug.MAIN_DEBUG_SWITCH)
			R2.echo("RESTORE--------&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&-----------_SETTING");
		AttributesManager atm = new AttributesManager();
		int end = settings.size();
		// clean old files
//		for (int j = 0; j < end; j++) {
//
//			File f = new File(ConstantAdapter.DIR_DATA + File.separator
//					+ ConstantAdapter.DIR_PREFS);
//			File[] files = f
//					.listFiles(ConstantAdapter.FILE_FILTER_SHARED_PREFS);
//			if (files != null) {
//				for (int x = 0; x < files.length; x++) {
//					SharedPreferences pref = mContext.getSharedPreferences(
//							Utilities.newInstance().deSuffix(
//									files[x].getName(),
//									ConstantAdapter.SUFFIX_FOR_PREF_FILE),
//							Activity.MODE_APPEND);
//					// R2.echo("Pref is : " + pref.getAll());
//					if (!pref.getBoolean(ConstantAdapter.EXCLUDED_SETTING_KEY,
//							false)) {
//						 R2.echo("Delete pref file : " + files[x].getName());
//							Log.d("liuyg1","BackupableSetting Delete "+files[x].getName());
//						files[x].delete();
//					}
//				}
//			}
//		}
		//

		for (int i = 0; i < end; i++) {
			SettingInfo info = settings.get(i);
			String category = null;
			String type;
			try {
				category = atm.getAttributeByName(info,
						SettingAttributes.CATEGORY).getAttrValue();
				if (Debug.MAIN_DEBUG_SWITCH)
					if (Debug.MAIN_DEBUG_SWITCH)
						R2.echo("encount category is  : " + category);
				type = atm
						.getAttributeByName(info, SettingAttributes.VALUETYPE)
						.getAttrValue();
			} catch (Exception e) {
				continue;
			}
			SharedPreferences pref = mContext.getSharedPreferences(category,
					Activity.MODE_APPEND | Activity.MODE_MULTI_PROCESS);

			try {
				String name = atm.getAttributeByName(info,
						SettingAttributes.NAME).getAttrValue();
				String value = atm.getAttributeByName(info,
						SettingAttributes.VALUE).getAttrValue();

				if (Debug.MAIN_DEBUG_SWITCH)
					R2.echo("encount restore setting name is  : " + name+"  value is  : " + value);

				if (String.class.getSimpleName().equals(type)) {
					//bugfix orochi-2903 zhanglz1 20131201 
					if(name.equals("pref_theme")){
						SettingsValue.setlbkdefaultthemeincase(value);
					}
					pref.edit().putString(name, String.valueOf(value)).commit();
				} else if (Boolean.class.getSimpleName().equals(type)) {
					pref.edit().putBoolean(name, Boolean.valueOf(value))
							.commit();
				} else if (Float.class.getSimpleName().equals(type)) {
					pref.edit().putFloat(name, Float.valueOf(value)).commit();
				} else if (Integer.class.getSimpleName().equals(type)) {
					pref.edit().putInt(name, Integer.valueOf(value)).commit();
				} else if (Long.class.getSimpleName().equals(type)) {
					pref.edit().putLong(name, Long.valueOf(value)).commit();
				} else if (Set.class.getSimpleName().equals(type)) {
					Set<String> set = new HashSet<String>();
					String setName = name;
					if (Debug.MAIN_DEBUG_SWITCH)
						R2.echo("encount set name is  : " + name);
					int j = i;
					for (; j < end; j++) {
						try {
							SettingInfo setInfoNow = settings.get(j);
							String setNameNow = atm.getAttributeByName(
									setInfoNow, SettingAttributes.NAME)
									.getAttrValue();
							if (!setName.equals(setNameNow)) {
								break;
							}
							String setValueNow = atm.getAttributeByName(
									setInfoNow, SettingAttributes.VALUE)
									.getAttrValue();
							if (setValueNow != null && !"".equals(setValueNow))
								set.add(setValueNow);
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}
					}

					i = j;

					pref.edit().putStringSet(setName, set).commit();
				}
			} catch (Exception e) {
				e.printStackTrace();
				R2.echo("encount Exception : ");

				continue;
			}
			
		}
		Log.d("liuyg1","--restore-1---getlbkdefaultthemeincase="+SettingsValue.getlbkdefaultthemeincase());

	}

	void appendExtraSettings() {

		// handle wallpaper offset
		/*
		try {
			((Launcher) mContext).getWorkspace().dumpNecessarySettings();
		} catch (Exception e) {
		}
		*/

	}

	@SuppressWarnings("unchecked")
	ArrayList<SettingInfo> retrieve(final Context context) {

		// extra settings
		appendExtraSettings();

		File[] prefFiles = new File(WORKING_DIR)
				.listFiles(ConstantAdapter.FILE_FILTER_SHARED_PREFS);

		if (Debug.MAIN_DEBUG_SWITCH)
			R2.echo("Work dir is ------------" + WORKING_DIR);

		if (prefFiles == null)
			return null;

		ArrayList<SettingInfo> settings = new ArrayList<SettingInfo>();
		for (int i = 0; i < prefFiles.length; i++) {

			String simpleName = Utilities.newInstance().deSuffix(
					prefFiles[i].getName(), ".xml");
			SharedPreferences pref = context.getSharedPreferences(simpleName,
					Activity.MODE_PRIVATE);
			// R2 -- excluded settings
			if (pref.getBoolean(ConstantAdapter.EXCLUDED_SETTING_KEY, false)||simpleName.equals("reaper")) {
				continue;
			}
			// 2R
			/*PK_ID:KEYSET -->ENTTRYSET  AUHT:GECN1 DATE:2013-01-28 S*/
			Map<String, ?> allPairs = pref.getAll();
			for(Map.Entry<String, ?> entry : allPairs.entrySet()){
				SettingInfo setting = InfoFactory.INSTANCE.new SettingInfo();
				setting.name = simpleName;
				setting.setKey(simpleName);
				String attName =entry.getKey();
				Object attValue = entry.getValue();

				String type = "";
				if (attValue instanceof String) {
					if (Debug.MAIN_DEBUG_SWITCH)
						R2.echo("String !");
					type = String.class.getSimpleName();

				} else if (attValue instanceof Boolean) {
					if (Debug.MAIN_DEBUG_SWITCH)
						R2.echo("Booolean");
					type = Boolean.class.getSimpleName();
				} else if (attValue instanceof Integer) {
					type = Integer.class.getSimpleName();
					if (Debug.MAIN_DEBUG_SWITCH)
						R2.echo("Integer");
				} else if (attValue instanceof Long) {
					type = Long.class.getSimpleName();
					if (Debug.MAIN_DEBUG_SWITCH)
						R2.echo("Long");
				} else if (attValue instanceof Float) {
					type = Float.class.getSimpleName();
					if (Debug.MAIN_DEBUG_SWITCH)
						R2.echo("Float");
				} else if (attValue instanceof Set<?>) {
					type = Set.class.getSimpleName();
					if (Debug.MAIN_DEBUG_SWITCH)
						R2.echo("Set");
				}
				if (Debug.MAIN_DEBUG_SWITCH)
					R2.echo("final is is type 8***************: " + type);
				if (!Set.class.getSimpleName().equals(type)) {
					setting.attrList.add(InfoFactory.INSTANCE.new Attribute(
							SettingAttributes.VALUETYPE, type));
					setting.attrList.add(InfoFactory.INSTANCE.new Attribute(
							SettingAttributes.CATEGORY, setting.name));
					setting.attrList.add(InfoFactory.INSTANCE.new Attribute(
							SettingAttributes.NAME, attName));
					setting.attrList.add(InfoFactory.INSTANCE.new Attribute(
							SettingAttributes.VALUE, String.valueOf(attValue)));
				} else {
					Set<String> valueSet = (Set<String>) attValue;
					Iterator<String> itTmp = valueSet.iterator();
					while (itTmp.hasNext()) {
						String nowValue = itTmp.next();
						SettingInfo setInfo = InfoFactory.INSTANCE.new SettingInfo();
						setInfo.attrList
								.add(InfoFactory.INSTANCE.new Attribute(
										SettingAttributes.VALUETYPE, type));
						setInfo.attrList
								.add(InfoFactory.INSTANCE.new Attribute(
										SettingAttributes.CATEGORY,
										setting.name));
						setInfo.attrList
								.add(InfoFactory.INSTANCE.new Attribute(
										SettingAttributes.NAME, attName));
						setInfo.attrList
								.add(InfoFactory.INSTANCE.new Attribute(
										SettingAttributes.VALUE, nowValue));
						settings.add(setInfo);
					}

				}

				if (Debug.MAIN_DEBUG_SWITCH)
					R2.echo("Setting----------------- : " + attName + ","
							+ attValue + "   ,  for setting:  " + setting.name);
				if (Debug.MAIN_DEBUG_SWITCH)
					R2.echo("Addd dddddd setting:    ->" + setting.name);
				settings.add(setting);
			}
			/*PK_ID:KEYSET -->ENTTRYSET  AUHT:GECN1 DATE:2013-01-28 S*/
		}

		return settings;
	}
}
