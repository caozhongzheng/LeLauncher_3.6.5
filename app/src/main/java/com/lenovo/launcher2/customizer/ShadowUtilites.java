package com.lenovo.launcher2.customizer;

import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.animation.ScaleAnimation;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;
import com.lenovo.launcher.components.XAllAppFace.XIconDrawable;
import com.lenovo.launcher.components.XAllAppFace.XPagedView;
import com.lenovo.launcher.components.XAllAppFace.XPagedViewItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;
import com.lenovo.launcher2.commoninterface.HolographicOutlineHelper;
public class ShadowUtilites {
    public static Bitmap getShadowBitmap(Bitmap originalImage,XContext context) {
    	    if(context == null) return null;
    	    if(originalImage==null) return null;
        	// 原始图片和反射图片中间的间距
        	int width = originalImage.getWidth();
        	int height = originalImage.getHeight();
        	// 反转
        	Matrix matrix = new Matrix();
        	// 第一个参数为1表示x方向上以原比例为准保持不变，正数表示方向不变。
        	// 第二个参数为-1表示y方向上以原比例为准保持不变，负数表示方向取反。
        	matrix.preScale(1, -1f);
        	// reflectionImage就是下面透明的那部分,可以设置它的高度为原始的3/4,这样效果会更好些
        	Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height * 2 / 3, width, height * 1 / 3, matrix, false);
        
        	Canvas canvasRef = new Canvas(reflectionImage);
        	        	 
        	Paint deafaultPaint = new Paint();
        	
        	int newHeight = reflectionImage.getHeight();
        	int newWidth = reflectionImage.getWidth();
        	
        	canvasRef.drawRect(0, newHeight, newWidth, newHeight, deafaultPaint);
        	// 画被反转以后的图片
        	//canvasRef.drawBitmap(reflectionImage, 0, newHeight, null);
        	canvasRef.drawBitmap(reflectionImage, 0, 0, null);

        	// 创建一个渐变的蒙版放在下面被反转的图片上面
        	 
        	Paint paint = new Paint();
        	 
        	LinearGradient shader = new LinearGradient(0, 0, 0, newHeight
        	 
        	, 0x34ffffff/*26/1a 52/34*/, 0x00ffffff, TileMode.CLAMP);
        	 
        	paint.setShader(shader);
        	 
        	// Set the Transfer mode to be porter duff and destination in
        	 
        	paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        	 
        	// 将蒙板画上
        	 
        	canvasRef.drawRect(0, 0, newWidth, newHeight, paint);
        	 
        	return reflectionImage;
    }
    
    public static Point getShadowPosition(float parentRightX,float childRightX,float childTopY
            ,float tipWidth,float tipHeight,XContext context){
        
    	Point p = new Point();
       /* float mOneNumberTipDrawableWidth = getOneNumerTipDrawableWidth(context, 1);
        float tipTranslateX = Math.max(mOneNumberTipDrawableWidth/4f, parentRightX- childRightX);
        float tipTranslateY  =(childTopY-tipHeight/2f);
        tipTranslateY = tipTranslateY>0f ? tipTranslateY:0;
        p.x = (int) (parentRightX - (tipWidth - mOneNumberTipDrawableWidth/4 + tipTranslateX));
        p.y = (int) tipTranslateY;*/
        return p;
    }
    

    private ShadowUtilites(){
    	
    }

	private final static HolographicOutlineHelper mOutlineHelper = new HolographicOutlineHelper();
	public final static int SHORTCUT_ICON_DRAWABLE = 0;
	public final static int FOLDER_ICON = 1;
	public static Bitmap[] backgrounds = new Bitmap[2];
	public static Bitmap[] shadows = new Bitmap[2];
	public static void createGlowingOutline(Context context) {
		//Log.i("00", "=====createGlowingOutline==null=");
		shadows[SHORTCUT_ICON_DRAWABLE] = null;
		shadows[FOLDER_ICON] = null;
		shadows = new Bitmap[2] ;
		
		backgrounds[SHORTCUT_ICON_DRAWABLE] = null;
		backgrounds[FOLDER_ICON] = null;
		backgrounds = new Bitmap[2] ;
		
		if(isShow(context)){
			//Log.i("00", "=====createGlowingOutline==isShow=");
			backgrounds[SHORTCUT_ICON_DRAWABLE] = getStandardBgForShadow(context);
			backgrounds[FOLDER_ICON] = getStandardFolderBgForShadow(context);
		}else{
			backgrounds[SHORTCUT_ICON_DRAWABLE] = null;
			backgrounds[FOLDER_ICON] = null;
		}
		
//		if (backgrounds[FOLDER_ICON] != null) {
//			try {
//				com.lenovo.launcher.components.XAllAppFace.utilities.Utilities
//						.saveBitmap(backgrounds[FOLDER_ICON], "backgrounds[FOLDER_ICON]");
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		if (backgrounds[SHORTCUT_ICON_DRAWABLE] != null) {
//			//Log.i("00", "======backgrounds[SHORTCUT_ICON_DRAWABLE]===="+backgrounds[SHORTCUT_ICON_DRAWABLE].getHeight());
//			try {
//				com.lenovo.launcher.components.XAllAppFace.utilities.Utilities
//						.saveBitmap(backgrounds[SHORTCUT_ICON_DRAWABLE], "backgrounds[SHORTCUT_ICON_DRAWABLE]");
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		shadows[SHORTCUT_ICON_DRAWABLE]  = getShadow(backgrounds[SHORTCUT_ICON_DRAWABLE],context,(float)1.0);
	    float scale = 1.0f;
//		int index = SettingsValue.getIconStyleIndex(context);
      /*  if(index!=-2){
        	scale = 1.15f;
        }*/
		shadows[FOLDER_ICON]  = getShadow(backgrounds[FOLDER_ICON],context,scale);

//		if (shadows[SHORTCUT_ICON_DRAWABLE] != null) {
//			try {
//				com.lenovo.launcher.components.XAllAppFace.utilities.Utilities
//						.saveBitmap(shadows[SHORTCUT_ICON_DRAWABLE], "shadows[SHORTCUT_ICON_DRAWABLE]");
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		if (shadows[FOLDER_ICON] != null) {
//			try {
//				com.lenovo.launcher.components.XAllAppFace.utilities.Utilities
//						.saveBitmap(shadows[FOLDER_ICON], "shadows[FOLDER_ICON]");
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
    }
	private static boolean isShow(Context context) {//从folder和icon两方面判断是否需要显示阴影
		// TODO Auto-generated method stub
		LauncherApplication app = (LauncherApplication) context
				.getApplicationContext();
		SharedPreferences sharedPreferences = context.getSharedPreferences("theme_apply", Context.MODE_PRIVATE);
        String packageName = sharedPreferences.getString(SettingsValue.PREF_THEME, SettingsValue.RES_DEFAULT_ANDROID_THEME);
	    
		boolean isDefaultTheme = packageName.equals(SettingsValue.getDefaultAndroidTheme(context));
		if(isDefaultTheme){
			int index = SettingsValue.getIconStyleIndex(context);
	        if(index == -1){//无图标装饰 不加阴影
				 return false;
	        }else if(index == -2) {
				// 如果背板和自制icon形状是一致的，则主题中设置该标志位为1；否则为0.
				Drawable theme_appbg = app.mLauncherContext.getDrawable(
						R.drawable.theme_appbg, false);
				if(theme_appbg == null) return false;//主题中没有图标装饰背板 不加阴影
	/*			Drawable bg = app.mLauncherContext.getDrawable(
						R.drawable.portal_ring_inner_holo, false);
				if(bg == null) return false; //主题中没有文件夹背板 不加阴影
*/				else{
					int isAddShadow = app.mLauncherContext.getInteger(
							R.integer.config_same_shape, R.integer.config_same_shape);
					if (isAddShadow == 0) {
						return false;//主题中图标装饰背板和主题图标形状不一致 不加阴影
					}
				}
	        }
		}else{
			// 如果背板和自制icon形状是一致的，则主题中设置该标志位为1；否则为0.
			Drawable theme_appbg = app.mLauncherContext.getDrawable(
					R.drawable.theme_appbg, false);
			if(theme_appbg == null) return false;//主题中没有图标装饰背板 不加阴影
/*			Drawable bg = app.mLauncherContext.getDrawable(
					R.drawable.portal_ring_inner_holo, false);
			if(bg == null) return false; //主题中没有文件夹背板 不加阴影
*/			else{
				int isAddShadow = app.mLauncherContext.getInteger(
						R.integer.config_same_shape, R.integer.config_same_shape);
				if (isAddShadow == 0) {
					return false;//主题中图标装饰背板和主题图标形状不一致 不加阴影
				}
			}
		}
		
		return true;
	}

	private static Bitmap getShadow(Bitmap bitmap,Context context,float scale) {
		if(bitmap==null) return null;
		final int padding = HolographicOutlineHelper.MAX_OUTER_BLUR_RADIUS;
		int outlineColor = 
				context.getResources().getColor(R.color.black/*.dark_blue*/);
		int glowColor =outlineColor;
		Canvas canvas = new Canvas();
        final Bitmap b = Bitmap.createBitmap(
        		bitmap.getWidth() + padding*2, bitmap.getHeight() + padding*2, Bitmap.Config.ARGB_8888);
        if(b==null || canvas == null || bitmap.isRecycled()) return null;
        canvas.setBitmap(b);
        drawWithPadding(bitmap,canvas, padding);
        
        mOutlineHelper.applyExtraThickExpensiveOutlineWithBlur(b, canvas, glowColor, outlineColor);
        canvas.setBitmap(null);
        return getFadeInSmallerBitmap(b,scale);
	}

	private static Bitmap getStandardFolderBgForShadow(Context context) {
		// TODO Auto-generated method stub
		LauncherApplication app = (LauncherApplication) context
				.getApplicationContext();
		Bitmap b = null;
	
		Drawable bg = app.mLauncherContext.getDrawable(
			R.drawable.portal_ring_inner_holo, true);
		int index = SettingsValue.getIconStyleIndex(context);
        //Log.i("00", "===index="+index);
		if (index == -2) {//主题
			// 如果背板和自制icon形状是一致的，则主题中设置该标志位为1；否则为0.
				int isAddShadow = app.mLauncherContext.getInteger(
						R.integer.config_same_shape, R.integer.config_same_shape);
				
				if (isAddShadow == 1) {
					b = Utilities.drawableToBitmap(bg,bg.getIntrinsicWidth(),bg.getIntrinsicHeight());
				} else {
					b = null;
				}
		} else if (index == -1) {// 无图标装饰 不加阴影
			b = null;
		} else {
			b = Utilities.drawableToBitmap(bg,bg.getIntrinsicWidth(),bg.getIntrinsicHeight());
		}
//		if (b != null) {
//			try {
//				com.lenovo.launcher.components.XAllAppFace.utilities.Utilities
//						.saveBitmap(b, "folderbg");
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		
		/*Canvas canvas = new Canvas();
        Bitmap bm = Bitmap.createBitmap(
        		(int)(b.getWidth() + 4*scale), (int)(b.getHeight() + 4*scale), Bitmap.Config.ARGB_8888);
        if(bm==null || canvas == null || b.isRecycled()) return null;
        canvas.setBitmap(bm);
		if(b!=null)
		drawWithPadding(b,canvas,(int)(2*scale));
        if (bm != null) {
			try {
				com.lenovo.launcher.components.XAllAppFace.utilities.Utilities
						.saveBitmap(bm, "111folderbg");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
		return scaleToDefaltSize(b,context);		
	}

	private static Bitmap getStandardBgForShadow(Context context){
		Bitmap bg = null;
        SharedPreferences sharedPreferences = context.getSharedPreferences("theme_apply", Context.MODE_PRIVATE);
        String packageName = sharedPreferences.getString(SettingsValue.PREF_THEME, SettingsValue.RES_DEFAULT_ANDROID_THEME);
	    
		boolean isDefaultTheme = packageName.equals(SettingsValue.getDefaultAndroidTheme(context));
    	LauncherApplication app = (LauncherApplication) context.getApplicationContext();
    	Drawable bgdrawable = null;
		try {
			if (isDefaultTheme) {
				//默认主题
				 int index = SettingsValue.getIconStyleIndex(context);
			    // Log.i("00", "===index="+index);
                 if(index == 5 || index == 6){
                	 bgdrawable = Utilities.findDrawableByResourceName("default_ic_style_"
  							+ index +"_0" + "_bg",context);
                	 bg = Utilities.drawable2BitmapNoScale(bgdrawable);
				  } else if(index == -1){//无图标装饰 不加阴影
					  bg = null;
			      }else if(index == -2){
			    	    Drawable theme_appbg = app.mLauncherContext.getDrawable(
			    					R.drawable.theme_appbg, false);
			    	    if(theme_appbg == null) return null;
			    	    else{
			    	    	// 如果背板和自制icon形状是一致的，则主题中设置该标志位为1；否则为0.
							int isAddShadow = app.mLauncherContext.getInteger(R.integer.config_same_shape, R.integer.config_same_shape);
			                if(isAddShadow ==1){
			                	Bitmap[] themeIconBg = SettingsValue.getThemeIconBg();
			    				bg = themeIconBg[0];
			                }else{
			                	bg = null;
			                }
			    	    }
				}else{
					bgdrawable = Utilities.findDrawableByResourceName("default_ic_style_"
  							+ "_0"  + "_bg",context);
                	 bg = Utilities.drawable2BitmapNoScale(bgdrawable);
				}
			} else {
				Drawable theme_appbg = app.mLauncherContext.getDrawable(
						R.drawable.theme_appbg, false);
				if (theme_appbg == null)
					return null;
				else {
					// 如果背板和自制icon形状是一致的，则主题中设置该标志位为1；否则为0.
					int isAddShadow = app.mLauncherContext.getInteger(
							R.integer.config_same_shape,
							R.integer.config_same_shape);
					if (isAddShadow == 1) {
						Bitmap[] themeIconBg = SettingsValue.getThemeIconBg();
						bg = themeIconBg[0];
					} else {
						bg = null;
					}
				}
			}
		} catch (Exception e) {
			Debug.
                    printException("Launcher->ApplyThemeTask. Read drawalbe/default_wallpaper.png error", e);
		}
		if (bg != null) {
			try {
				com.lenovo.launcher.components.XAllAppFace.utilities.Utilities
						.saveBitmap(bg, "bg");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        return scaleToDefaltSize(bg,context);		
	}
    private static Bitmap scaleToDefaltSize(Bitmap bg,Context c) {
		// TODO Auto-generated method stub
    	       if(bg == null) return null;
    	       int iconsize = SettingsValue.getIconSizeValue(c);
    	               Bitmap tempbm = Utilities.createBitmap(new BitmapDrawable(bg), iconsize, iconsize, c);
    	               return tempbm;

	}

	private static void drawWithPadding(Bitmap background,Canvas destCanvas, int padding) {
        destCanvas.save();
        destCanvas.drawBitmap(background, padding,padding, null);
        destCanvas.restore();
    }
    private static Bitmap getFadeInSmallerBitmap(Bitmap originalImage,float scale) {

    	int orgh = originalImage.getHeight();
    	int orgw = originalImage.getWidth();
    	if(orgh<=0 ||orgw<=0 ) return null;
    	
    	Bitmap a = Bitmap.createScaledBitmap(originalImage, (int)(scale*orgw), (int)(scale*orgh), false);

    	Canvas canvasRef = new Canvas(a);
    	Paint deafaultPaint = new Paint();
    	int height = a.getHeight();
    	int width = a.getWidth();
    	canvasRef.drawRect(0, height, width, height, deafaultPaint);
    	//canvasRef.drawBitmap(a, new Matrix(), null);
    	// 创建一个渐变的蒙版放在下面被反转的图片上面
    	 
    	//从上向下渐变
    	Paint paint = new Paint();
    	LinearGradient shader = new LinearGradient(0, 0, 0, height, new int[]{0x00ffffff, 0xf0ffffff},//240
    			/*new float[]{0 , 0.3f, 0.6f}*/null, TileMode.CLAMP);
    	paint.setShader(shader);
    	// Set the Transfer mode to be porter duff and destination in
    	paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
    	// 将蒙板画上
    	canvasRef.drawRect(0, 0, width, height, paint);
    	
    	//从中心向左渐变
    	Paint paintL = new Paint();
    	LinearGradient shaderL = new LinearGradient(width/4.0f, 0, 0, 0, new int[]{0xffffffff,0x00ffffff},
    			null, TileMode.CLAMP);
    	paintL.setShader(shaderL);
    	paintL.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
    	canvasRef.drawRect(0, 0, width, height, paintL);

    	//从中心向右渐变
    	Paint paintR = new Paint();
    	LinearGradient shaderR = new LinearGradient(width*3.0f/4.0f, 0, width, 0, new int[]{0xffffffff,0x00ffffff },
    			null, TileMode.CLAMP);
    	paintR.setShader(shaderR);
    	paintR.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
    	canvasRef.drawRect(0, 0, width, height, paintR);

    	 
    	return a;
}
}
