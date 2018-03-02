package com.lenovo.launcher2.gadgets.Lotus;

import java.util.ArrayList;

import com.lenovo.launcher.R;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

/** 标准四叶草widget。
 * Author : ChengLiang
 * */
public class LotusProviderHelper extends AppWidgetProvider {
    /** Log Tag */
	private static final String TAG = "LotusProviderHelper";

    @Override
	public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        Log.i(TAG, " onUpdate******************** ");

        super.onUpdate(context, appWidgetManager, appWidgetIds);

        //普通模式不支持主题
		RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.gadget_lotus_face_base);
		
		SharedPreferences preferences = context.getSharedPreferences(LotusUtilites.LOTUSINFO,
				Activity.MODE_APPEND | Activity.MODE_MULTI_PROCESS);
		
		//取叶子上的默认应用
		String[] intentString = new String[LotusUtilites.TOTAL];
        for(int i = 0; i < LotusUtilites.TOTAL_LEAF; i++){
            intentString[i] = preferences.getString(LotusUtilites.PREFIX_LEAF[i] + LotusUtilites.SUFFIX_LEAF_SOLID + "_" + LotusUtilites.SUFFIX_LEAF_FIRST, "");
        }
		intentString[LotusUtilites.CENTER] = preferences.getString(LotusUtilites.LOTUS_CENTER_TARGET, LotusUtilites.FULL_LE_APPSTORE);

        //解析叶子上的默认应用
		Intent[] intent = new Intent[LotusUtilites.TOTAL];
	    for(int i = 0; i < LotusUtilites.TOTAL; i++){
	        intent[i] = LotusUtilites.getLotusPageInfo(intentString[i]);
	    }

		//初始化叶片上的标题
		LotusUtilites.getString(context, preferences);

		//设置叶片
		clickToStart(context, rv, R.id.top_left, R.id.leftup_part, R.id.top_left_image, intentString[LotusUtilites.LT], LotusUtilites.app_name_first[LotusUtilites.LT], intent[LotusUtilites.LT]);
		clickToStart(context, rv, R.id.top_right, R.id.rightup_part, R.id.top_right_image, intentString[LotusUtilites.RT], LotusUtilites.app_name_first[LotusUtilites.RT], intent[LotusUtilites.RT]);
		clickToStart(context, rv, R.id.bottom_left, R.id.leftdown_part, R.id.bottom_left_image, intentString[LotusUtilites.LB], LotusUtilites.app_name_first[LotusUtilites.LB], intent[LotusUtilites.LB]);
		clickToStart(context, rv, R.id.bottom_right, R.id.rightdown_part, R.id.bottom_right_image, intentString[LotusUtilites.RB], LotusUtilites.app_name_first[LotusUtilites.RB], intent[LotusUtilites.RB]);
		clickToStart(context, rv, R.id.store, R.id.store, R.id.store, intentString[LotusUtilites.CENTER],"" ,intent[LotusUtilites.CENTER]);

		//更新widget
		ComponentName cn = new ComponentName(context, LotusProviderHelper.class);
		appWidgetManager.updateAppWidget(cn, rv);
	}
    
    @Override
    public void onDisabled(Context context) {  
        Log.i(TAG, " onDisable******************** ");
        super.onDisabled(context); 
    }   

	/** 设置叶子。传入远程view、叶子文本view id、叶子背景view id、叶子图标view id、应用、标题、intent。 */
	private void clickToStart(Context context, RemoteViews rv, int labelViewId, int leafViewId, int iconViewId, String intent_str,String name_str,Intent intent) {
	    PackageManager packagemanager = context.getPackageManager();
	    ResolveInfo resolveInfo = null;

	    //设置叶子操作
	    if (intent != null) {
		    //取应用解决信息
			resolveInfo = packagemanager.resolveActivity(intent, 0);
			Log.i(TAG, " **********intent_str*******==="+intent_str);

			if (resolveInfo != null) {
			    //如果取到应用，就完善intent。
				intent.addCategory(Intent.CATEGORY_LAUNCHER);
				intent.setAction(Intent.ACTION_MAIN);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			} else if (intent_str.contains("com.lenovo.fakenavi00")) {
			    //处理乐导航
				String deviceId = LotusUtilites.getDeviceId(context);
				String result = null;
				try {
					result = DES.encryptDES(deviceId, LotusUtilites.DEVICE_KEY);// 加密后的设备ID

				} catch (Exception e) {
					e.printStackTrace();
				}
				String loadUrl = "http://m.idea123.cn/?d=" + result + "&c=2";
				Uri uri = Uri.parse(loadUrl);
				intent = new Intent(Intent.ACTION_VIEW, uri);
				intent.setComponent(new ComponentName("com.android.browser",
						"com.android.browser.BrowserActivity"));
			}  else if (intent_str.contains(LotusUtilites.FAKE_LE_FAMILY)) {
			    //处理乐家族
				intent = new Intent(Intent.ACTION_VIEW);
				intent.setClass(context, QuickAlertDialogActivity.class);
				intent.putExtra("keyword",
						"startAppUnit");
				Log.i(TAG, " resolveInfo==nul**********fakefolder*******==="+intent.getStringExtra("keyword"));
			}else {
				//应用不存在
				Log.i(TAG, " intent**********null*******id===" + labelViewId);
				intent = new Intent(Intent.ACTION_VIEW);
				intent.setClass(context, QuickAlertDialogActivity.class);
				intent.putExtra("message_res_id",
						R.string.inform_application_is_not_install_not_download);
				intent.putExtra("leaf_id", labelViewId);

			}
			
			//设置intent
			PendingIntent pendingIntent = PendingIntent.getActivity(context,
					0, intent, 0);
			rv.setOnClickPendingIntent(leafViewId, pendingIntent);
			Log.i(TAG, " setOnClickPendingIntent ===id ===" + leafViewId);
		}
		
		//设置叶子文本
	    String app_name = reGetName(context, name_str, resolveInfo, 0);
		if (labelViewId != R.id.store && app_name != null) {
			rv.setTextViewText(labelViewId, app_name);
		}

		//设置叶子图标
		 Bitmap b = reGetIcon(context, null, resolveInfo, 0);
        if (iconViewId != R.id.store && b != null) {
            rv.setImageViewBitmap(iconViewId, b);
        }
	}

	/** 取应用名，传入默认串、解决信息、默认id */
    private String reGetName(Context context, String name ,ResolveInfo info, int nameId){
        if(name == null){
            //获取应用名
            if (info != null) {
                PackageManager packagemanager = context.getPackageManager();
                String label = info.loadLabel(packagemanager).toString();
                if (label != null) {
                    name = label;
                }else{
                    name = info.activityInfo.name;
                }
            }else if(context != null && nameId > 0){
                name = context.getResources().getString(nameId);
            }
        }
        
        return name;
    }

    /** 取应用图标，传入默认图、解决信息、默认id */
    private Bitmap reGetIcon(Context context, Bitmap pic ,ResolveInfo info, int picId){
		if(pic ==null){
            //获取应用图标
			if (info != null) {
				PackageManager packagemanager = context.getPackageManager();
				Drawable d = info.activityInfo.loadIcon(packagemanager);
				pic = LotusUtilites.createIconBitmap(d, context, context.getResources().getDimensionPixelSize(R.dimen.app_icon_size));
			}else if(context != null && picId > 0){
			    pic = BitmapFactory.decodeResource(context.getResources(), picId);
			}
		}
		
		return pic;
	}
}
