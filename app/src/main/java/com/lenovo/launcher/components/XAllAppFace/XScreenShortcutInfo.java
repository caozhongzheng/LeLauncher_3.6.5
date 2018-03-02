package com.lenovo.launcher.components.XAllAppFace;

import com.lenovo.launcher2.commoninterface.ShortcutInfo;

/**
 * 屏幕编辑页的应用的INFO
 * @author zhanggx1
 *
 */
public class XScreenShortcutInfo extends ShortcutInfo {
	/**
	 * 用于屏幕编辑页的多选功能
	 */
	public boolean checked = false;
	
	public XScreenShortcutInfo(ShortcutInfo info) {
		super(info);
	}
}
