package com.lenovo.launcher2.commoninterface;



import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.os.Parcelable;

public class PendingAddWidgetInfo extends ItemInfo {
	public int minWidth;
	public int minHeight;
	public boolean hasDefaultPreview;
	public int previewImage;
	public int icon;

	public ComponentName componentName;
    // Any configuration data that we want to pass to a configuration activity when
    // starting up a widget
    public String mimeType;
    public Parcelable configurationData;

    public PendingAddWidgetInfo(AppWidgetProviderInfo i, String dataMimeType, Parcelable data) {
        itemType = LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET;
        componentName = i.provider;
        minWidth = i.minWidth;
        minHeight = i.minHeight;
        hasDefaultPreview = i.previewImage <= 0;
        previewImage = i.previewImage;
        icon = i.icon;
        if (dataMimeType != null && data != null) {
            mimeType = dataMimeType;
            configurationData = data;
        }
    }
    
}
