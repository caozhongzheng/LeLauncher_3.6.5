package com.lenovo.launcher.components.XAllAppFace;

import com.lenovo.launcher2.commoninterface.ItemInfo;

public class LocateItem {
	private ItemInfo shortcutInfo = null;
	private ItemInfo folderInfo = null;	
	LocateItem(ItemInfo shortcut, ItemInfo folder){
		shortcutInfo = shortcut;
		folderInfo = folder;
	}
	ItemInfo getShortcutInfo(){
		return shortcutInfo;
	}
	ItemInfo getFolderInfo(){
		return folderInfo;
	}
}
