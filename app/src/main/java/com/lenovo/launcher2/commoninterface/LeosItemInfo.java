package com.lenovo.launcher2.commoninterface;



import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;


public class LeosItemInfo extends ItemInfo {

    /**
     * The intent used to start the application.
     */
    public Intent intent;
    
	public LeosItemInfo() {
		itemType = -1;
	}
	
    public LeosItemInfo(LeosItemInfo info) {
        super(info);
        intent = new Intent(info.intent);
    }
    
    /**
     * Creates the application intent based on a component name and various launch flags.
     * Sets {@link #itemType} to {@link LauncherSettings.BaseLauncherColumns#ITEM_TYPE_APPLICATION}.
     *
     * @param className the class name of the component representing the intent
     * @param launchFlags the launch flags
     */
    final void setActivity(ComponentName className, int launchFlags) {
        intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(className);
        intent.setFlags(launchFlags);
    }
    
    @Override
    public void onAddToDatabase(ContentValues values) {
        super.onAddToDatabase(values);

        String uri = intent != null ? intent.toUri(0) : null;
        values.put(LauncherSettings.BaseLauncherColumns.INTENT, uri);
    }
}
