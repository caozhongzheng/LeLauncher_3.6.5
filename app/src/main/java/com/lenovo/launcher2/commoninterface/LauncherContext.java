package com.lenovo.launcher2.commoninterface;


import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;
import com.lenovo.launcher2.customizer.Debug;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.Utilities;
import android.os.SystemProperties;
import android.content.Context;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LauncherContext {	
	
	private Context mFriendContext = null;
	private LauncherApplication mLa = null;
	private Resources mRes = null;
	
	/*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-01 S*/
	private  boolean inBuildTheme = true;
	/*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-01 E*/
   private Bitmap mDeleteIcon = null;
	private Bitmap mKillIcon = null;
	private Drawable mCheckedIcon = null;
	
	public LauncherContext(LauncherApplication la) {
		mLa = la;
		mRes = mLa.getResources();
		Utilities.initStatics(mLa);
	}
	
	public Context getFriendContext() {
		return mFriendContext;
	}
	
	public void setFriendContext(Context context) {
		mFriendContext = context;
	}
	
	public int getDimensionPixel(int dimenId, int defDimenId) {
		int ret;
		if (mFriendContext == null) {
			ret = mRes.getDimensionPixelOffset(dimenId);
    	} else {
    		/*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-01 S*/
    		float temp = 0f;
    		if(this.isInbulidTheme(mFriendContext, inBuildTheme)){
    			int id = Utilities.findInbuildThemeIdbyId(mFriendContext, dimenId, "dimen");
    			if(id != 0){
    				temp = mRes.getDimensionPixelOffset(id);
    			}
    		}else{
    			temp = Utilities.findDimensionPixelOffsetById(mRes, 
            			dimenId, 
                		mFriendContext, 
                		Utilities.DimenType.DIMENSION_OFFSET);
    		}
    		/*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-01 S*/
            if (Float.compare(temp, Float.MIN_VALUE) == 0) {
            	ret = mRes.getDimensionPixelOffset(defDimenId);
            } else {
            	ret = (int)temp;
            }
    	}
		return ret;
	}
	
	public ColorStateList getColor(int colorId, int defColorId) {
		ColorStateList ret =null;
		if (mFriendContext == null) {
			ret = mRes.getColorStateList(colorId);
    	} else {
    		/*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-01 S*/
    		if(this.isInbulidTheme(mFriendContext, inBuildTheme)){
    			int id = Utilities.findInbuildThemeIdbyId(mFriendContext, colorId, mRes.getResourceTypeName(colorId));
    			if(id != 0){
    				ret = mRes.getColorStateList(id);
    			}
    		}else{
    			ret = Utilities.findColorById(mRes, colorId, mFriendContext);
    		}
    		/*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-01 E*/
            if (ret == null) {
            	ret = mRes.getColorStateList(defColorId);
            } 
    	}
		return ret;
	}
	
	public Drawable getDrawable(int drawableId) {
		return getDrawable(drawableId, true);
	}
	
	public Drawable getDrawable(int drawableId, boolean useDef) {
		Drawable ret = null;
		
		
		if (mFriendContext == null) {
			ret = mRes.getDrawable(drawableId);
    	} else {
    		/*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-01 S*/
    		if(this.isInbulidTheme(mFriendContext, inBuildTheme)){
    			int id = Utilities.findInbuildThemeIdbyId(mFriendContext, drawableId,"drawable");
    			if(id != 0){
    				ret = mRes.getDrawable(id);
    				
    			}
    		}else{
    			ret = Utilities.findDrawableById(mRes, 
        				drawableId, 
                		mFriendContext);
    		}
    		/*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-01 E*/
    		
    		
            if (ret == null && useDef) {
            	ret = mRes.getDrawable(drawableId);
            } 
    	}
		return ret;
	}
	
	public Bitmap getBitmap(int drawableId) {
		return getBitmap(drawableId, true);
	}
	
	public Bitmap getBitmap(int drawableId, boolean useDef) {
		Bitmap ret;
		try {
			if (mFriendContext == null) {
				ret = BitmapFactory.decodeResource(mRes, drawableId);
	    	} else {
	    		int resID = 0;
	    		/*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-01 S*/
	    		if(this.isInbulidTheme(mFriendContext, inBuildTheme)){
	    			resID = Utilities.findInbuildThemeIdbyId(mFriendContext, drawableId, "drawable");
	    		}else{
		    		resID = Utilities.getResourceId(mRes, drawableId, "drawable", mFriendContext);
	    		}
	    		/*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-01 E*/
	    		if (resID == 0) {
	    			ret = BitmapFactory.decodeResource(mRes, drawableId);
	    		} else {
	    			ret = BitmapFactory.decodeResource(mFriendContext.getResources(), resID);
	    			if (ret == null && useDef) {
	    				ret = BitmapFactory.decodeResource(mRes, drawableId);
		    		}
	    		}	    		
	    	}
		}  catch (OutOfMemoryError e) {
        	Debug.printException("Utilities->findBitmapById error", e);
        	ret = null;
        }
		return ret;
	}
	
	public int getInteger(int intId, int defIntId) {
		int ret = 0;
		if (mFriendContext == null) {
			ret = mRes.getInteger(intId);
    	} else {
    		
    		/*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-01 S*/
    		if(this.isInbulidTheme(mFriendContext, inBuildTheme)){
    			int id = Utilities.findInbuildThemeIdbyId(mFriendContext, intId,"integer");
    			if(id != 0){
    				ret = mRes.getInteger(id);
    			}
    		}else{
    			ret = Utilities.findIntegerById(mRes, 
            			intId, 
                		mFriendContext);
    		}
    		/*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-01 E*/
    		
            if (ret == Integer.MIN_VALUE) {
            	ret = mRes.getInteger(defIntId);
            }
    	}
		return ret;
	}
	
	/*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-05 S*/
	public int getIntegerByResName(String resName){
		if (mFriendContext != null) {
			if(this.isInbulidTheme(mFriendContext, this.inBuildTheme)){
				resName += themePostfix;
			}
			return Utilities.findIntegerByResourceName(resName, mFriendContext);
    	} 
		return  Integer.MIN_VALUE;
	}
	
	public int getIdByResName(String resName, String type,String packageName){
		if (mFriendContext != null) {
			if(this.isInbulidTheme(mFriendContext, this.inBuildTheme)){
				resName += themePostfix;
			}
			return mFriendContext.getResources().getIdentifier(resName, type, packageName);
    	}
		return 0;
	}
	
	/*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-05 E*/

	
	public Bitmap getIconBitmap(ResolveInfo info, String packageName) {
		Bitmap ret = null;
                //added for the unicom s5000 by yumina 20131030
                /*

                String operator = SystemProperties.get("ro.lenovo.operator");
                if("cu".equals(operator) && "com.infinit.wostore.ui".equals(packageName)){
                    Log.e("S5000","wwwwwwooooooo get systemlenovo.operator="+operator+" packageName============="+packageName);
                    ret = Utilities.createIconBitmapWithoutStyle(mLa.mIconCache.getFullResIcon(info), mLa);
                    return ret;
                }

                */
		if (mFriendContext == null) {
			/*RK_ID_USE_THEMEVALUE_ICON 2012-11-28 S*/
			//if the value is false, use the icon of app
			boolean bUse = SettingsValue.getUseDefaultThemeIconValue(mLa);
			if(bUse){
        	    ret = Utilities.retrieveCustomIconFromFile(info.activityInfo, mLa, mLa);
			}else{
				ret = null;
			}
			/*RK_ID_USE_THEMEVALUE_ICON 2012-11-28 S*/
        } else {
        	
        	
        	/*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-01 S*/
        	if(this.isInbulidTheme(mFriendContext, this.inBuildTheme)){
        		String[] names = Utilities.findInbulidThemeCustomIconNameIdbyId(mFriendContext, info.activityInfo.getIconResource(), 
        				info.activityInfo.packageName, "drawable", mLa);
        		if(names!=null){
        			ret = Utilities.findIconBitmapByIdName(names[0],mLa,mLa,info.activityInfo.packageName);
        			if (ret == null && !names[0].equals(names[1])) {
        				ret = Utilities.findIconBitmapByIdName(names[1],mLa,mLa,info.activityInfo.packageName);
        			}
        		}
        	}else{
        		ret = Utilities.retrieveCustomIconFromFile(info.activityInfo, mFriendContext, mLa);
        	}
        	/*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-01 S*/
        	
        	
        }
        if (ret == null) {
            ret = Utilities.createIconBitmap(mLa.mIconCache.getFullResIcon(info), mLa, packageName);
        }
        return ret;
	}

	/** 只从当前主题包取图片Drawable（不从主程序取），传入资源名 */
    public Drawable getThemeDrawableByResourceName(String name) {
        if (mFriendContext != null) {
        	/*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-01 S*/
        	if(this.isInbulidTheme(mFriendContext,inBuildTheme)){
        		int id = Utilities.findInbuildThemeIdbyName(mFriendContext, name, "drawable");
    			if(id != 0){
    				return mRes.getDrawable(id);
    			}
        	}else{
        		return Utilities.findDrawableByResourceName(name, mFriendContext);
        	}
        	/*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-01 E*/
            
        }
        return null;
    }
	
	public Bitmap getIconBitmap(Resources resources, int id, String packageName) {
		Bitmap icon = null;
		if (mFriendContext == null) {
			/*RK_ID_USE_THEMEVALUE_ICON 2012-11-28 S*/
			//if the value is false, use the icon of app
			boolean bUse = SettingsValue.getUseDefaultThemeIconValue(mLa);
			if(bUse){
				icon = Utilities.retrieveCustomIconFromFile(id, packageName, mLa, mLa);
			}else{
				icon = null;
			}
			/*RK_ID_USE_THEMEVALUE_ICON 2012-11-28 S*/
        	
        } else {
        	/*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-01 S*/
        	if(this.isInbulidTheme(mFriendContext, this.inBuildTheme)){
        		String[] names = Utilities.findInbulidThemeCustomIconNameIdbyId(mFriendContext, id, packageName, "drawable", mLa);
        		if(names!=null){
        			icon = Utilities.findIconBitmapByIdName(names[0],mLa,mLa,packageName);
        			if (icon == null && !names[0].equals(names[1])) {
        				icon = Utilities.findIconBitmapByIdName(names[1],mLa,mLa,packageName);
        			}
        		}
        	}else{
        		icon = Utilities.retrieveCustomIconFromFile(id, packageName, mFriendContext, mLa);
        	}
        	/*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-01 S*/
        }
        if (icon == null) {
        	icon = Utilities.createIconBitmap(
        			mLa.mIconCache.getFullResIcon(resources, id), mLa, packageName);
        }
        return icon;
	}

	/** 取主题包的layout并充气成view，传入取不到时是否用主程序包的默认资源、资源名字串、父view */
	public View getLayoutViewByName(boolean useDefault, String resName, ViewGroup parent){
		View v = null;
		/*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-01 S*/
		if(mFriendContext != null){
			if(this.isInbulidTheme(mFriendContext,inBuildTheme)){
	    		v = getLayoutViewByName(mFriendContext, resName+this.themePostfix, parent);
	    	}else{
	    		v = getLayoutViewByName(mFriendContext, resName, parent);
	    	}
		}
		if(v == null && useDefault){
	        v = getLayoutViewByName(mLa, resName, parent);
	    }
    	
    	/*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-01 S*/
	    
	    return v;
	}
	
    /** 取layout并充气成view，传入资源名字串、父view */
	private View getLayoutViewByName(Context context, String resName, ViewGroup parent){
        View v = null;

        if(context != null && resName != null && resName.length() > 0){
            Resources res = context.getResources();
            String packageName = context.getPackageName();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            int layoutId = res.getIdentifier(resName, "layout", packageName);
            if(layoutId > 0){
                v = inflater.inflate(layoutId, parent, false);
            }
        }

        return v;
	}

    /** 根据id资源名找view，传入要找的根View、id资源名字串 */
    public View findViewByIdName(View rootView, String idName){
        if(rootView == null || idName == null || idName.length() <= 0){
            return null;
        }
        
        int id = getIdByName(rootView.getContext(), idName);

        if(id <= 0){
            return null;
        }
        
        View v = rootView.findViewById(id);

        return v;
    }

     /** 根据资源名取id，传入资源名字串 */
    private int getIdByName(Context context, String idName){
        if(context == null || idName == null || idName.length() <= 0){
            return 0;
        }

        Resources res = context.getResources();
        String packageName = context.getPackageName();

        int id = res.getIdentifier(idName, "id", packageName);
        
        return id;
    }

    /** 取主题包的id，传入资源名字串 */
    public int getIdByName(String resName){
        int id = 0;

        Context context = mFriendContext;
        
        /*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-01 S*/
    	if(this.isInbulidTheme(mFriendContext,inBuildTheme)){
    		resName += this.themePostfix;
    		id = mRes.getIdentifier(resName, "id", mLa.getPackageName());
    		return id;
    	}
    	/*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-01 E*/
        
        if(context != null && resName != null && resName.length() > 0){
            Resources res = context.getResources();
            String packageName = context.getPackageName();

            id = res.getIdentifier(resName, "id", packageName);
        }
        
        return id;
    }
    
    public Bitmap getIconDelete() {
    	if (mDeleteIcon == null) {
    		mDeleteIcon = getBitmap(R.drawable.delete);
    	}
    	return mDeleteIcon;
    }

    public void resetIconEdit() {
    	if (mDeleteIcon != null) {
            // cancel by liuli1, fix bug 173381
//    		mDeleteIcon.recycle();
    	    mDeleteIcon = null;
    	}
    	if (mKillIcon != null) {
    		mKillIcon.recycle();
    	    mKillIcon = null;
    	}
    }
    
    public Bitmap getIconKill() {
    	if (mKillIcon == null) {
    		mKillIcon = getBitmap(R.drawable.killicon);
    	}
    	return mKillIcon;
    }
    
	/*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-01 S*/
	private  String themePostfix=SettingsValue.getInbuildThemePostfix();

	private boolean isInbulidTheme(Context friendContext,boolean isNeed){
		
		if(isNeed && friendContext != null  && friendContext.getPackageName().equals("com.lenovo.launcher")){
			return true;
		}
		return false;
	}
	/*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-01 E*/

    public Drawable getCheckedIcon() {
    	if (mCheckedIcon == null) {
    		mCheckedIcon = getDrawable(R.drawable.app_add_select);
    	}
    	return mCheckedIcon;
    }
}
