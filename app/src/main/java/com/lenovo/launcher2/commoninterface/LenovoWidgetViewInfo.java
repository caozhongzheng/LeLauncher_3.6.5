package com.lenovo.launcher2.commoninterface;



import android.content.ContentValues;
import android.graphics.Bitmap;

public class LenovoWidgetViewInfo  extends PendingAddItemInfo {

	public String className;
	public String packageName;
	public int minWidth;
	public int minHeight;
	public int previewImage;
	/*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. S***/
	public Bitmap iconBitmap;
	/*RK_ID:RK_SD_WIDGETS zhangdxa 2013-5-15. E***/
	public LenovoWidgetViewInfo() {
        itemType = LauncherSettings.Favorites.ITEM_TYPE_LEOSWIDGET;
    }
    @Override
    public void onAddToDatabase(ContentValues values) {
        super.onAddToDatabase(values);
        values.put(LauncherSettings.Favorites.ICON_RESOURCE, previewImage);
        if (className != null) {
        	values.put(LauncherSettings.Favorites.TITLE, className);
        }
        if(packageName!=null){
        	values.put(LauncherSettings.Favorites.URI, packageName);
        	
        }
    }
    public String getPackageName(){
    	return packageName;
    }
    public LenovoWidgetViewInfo copy() {
        LenovoWidgetViewInfo newInfo = new LenovoWidgetViewInfo();

        newInfo.id = this.id;
        newInfo.cellX = this.cellX;
        newInfo.cellY = this.cellY;
        newInfo.spanX = this.spanX;
        newInfo.spanY = this.spanY;
        newInfo.screen = this.screen;
        newInfo.itemType = this.itemType;
        newInfo.container = this.container;
        newInfo.attachedIndexArray = this.attachedIndexArray;

        newInfo.componentName = this.componentName;
        newInfo.className = this.className;
        newInfo.packageName = this.packageName;
        newInfo.minWidth = this.minWidth;
        newInfo.minHeight = this.minHeight;
        newInfo.previewImage = this.previewImage;

        return newInfo;
    }
}
