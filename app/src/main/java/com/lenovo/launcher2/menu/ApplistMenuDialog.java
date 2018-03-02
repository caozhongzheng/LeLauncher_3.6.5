package com.lenovo.launcher2.menu;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

import com.lenovo.launcher.R;
//import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;
import com.lenovo.launcher2.commonui.MenuGridView;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.ThemeSimpleAdapter;
import com.lenovo.launcher2.settings.AllAppsSettings;
import com.lenovo.launcher2.settings.HideAppsSettings;


public class ApplistMenuDialog implements DialogInterface.OnCancelListener, DialogInterface.OnDismissListener,
DialogInterface.OnShowListener {

	private static final String TAG = "ApplistMenuDialogActivity";
	

  private static final int DLGMENU_ORDER = 0;
  private static final int DLGMENU_HIDE_APP = DLGMENU_ORDER + 1;
  private static final int DLGMENU_APPS_SETTINGS = DLGMENU_ORDER + 2;
  
  private static final int DLGMENU_APPS_LJPAPPMANAGER = DLGMENU_ORDER + 3;
  public static final int  DLGMENU_ORDER_SHOW = 50;
  private boolean mIsList = true;
  private int[] appmenuname_array = {R.string.app_order_settings, 
  		R.string.applist_hiddenlist_settings_title, 
  		R.string.header_category_applist };
  private int[] appmenuimage_array = {R.drawable.main_menu_sort, 
  		R.drawable.main_menu_hideapp, 
  		R.drawable.main_menu_applist};
  
  private int[] appmenuname_array_list = {R.string.app_order_settings, 
	  		R.string.applist_hiddenlist_settings_title, 
	  		R.string.header_category_applist ,
	  		R.string.applist_appmanager 
	  		};
  
  private int[] appmenuimage_array_list = {R.drawable.main_menu_sort_list, 
	  		R.drawable.main_menu_hideapp_list, 
	  		R.drawable.main_menu_applist_list,
	  		R.drawable.main_menu_applist_manager_list }; 
  private Dialog mLeosDialog = null;
	private Context mContext;
	private  Handler mHandler;
	public Dialog createDialog(Context context, Handler handler) {
		mContext = context;
		mHandler = handler;
		mLeosDialog = new Dialog(mContext,R.style.Theme_LeLauncher_TransDialog);// , R.style.menu_style);
		mLeosDialog.setCanceledOnTouchOutside(true);
		mLeosDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	if(mIsList){
    		mLeosDialog.setContentView(R.layout.menu_list);
    	}else{
    		mLeosDialog.setContentView(R.layout.menu_layout);
    	}
    	
        Window window = mLeosDialog.getWindow();

        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL);
        window.setWindowAnimations(R.style.dialogWindowAnim);
        
        /* RK_ID: RK_MENU_RFACTOR . AUT: LIUYG1 . DATE: 2012-11-29 . S */
        MenuGridView menuGrid = (MenuGridView) mLeosDialog.findViewById(R.id.grid_item);
        View menuGrid_bg =  mLeosDialog.findViewById(R.id.grid_item_bg);
//        menuGrid.setFocusable(false);
    /* RK_ID: RK_MENU_RFACTOR . AUT: LIUYG1 . DATE: 2012-11-29 . E */
        View menuDialog_bg =  mLeosDialog.findViewById(R.id.menu_dialog_bg);
        menuDialog_bg.setOnClickListener(new OnClickListener() {
    		
    		@Override
    		public void onClick(View v) {
                /*** fixbug . AUT: zhaoxy . DATE: 2013-05-16. START ***/
                if (mLeosDialog != null && mLeosDialog.isShowing()) {
                    mLeosDialog.dismiss();
                }
                /*** fixbug . AUT: zhaoxy . DATE: 2013-05-16. END ***/
    			mLeosDialog = null;
    		}
    	});
        ViewGroup.LayoutParams lp = menuGrid_bg.getLayoutParams();
        LinearLayout.LayoutParams layoutParam = new LinearLayout.LayoutParams(lp);
        layoutParam.gravity= android.view.Gravity.RIGHT;
        
        int buttonbarHeight = mContext.getResources().getDimensionPixelSize(R.dimen.button_bar_height_plus_padding);
        
        int buttonHeight = mContext.getResources().getDrawable(R.drawable.x_applist_search_normal).getIntrinsicHeight();
        int marginBottom = (buttonbarHeight + buttonHeight)/2;
        
        layoutParam.setMargins(layoutParam.leftMargin, layoutParam.topMargin, 10, marginBottom);
        menuGrid_bg.setLayoutParams(layoutParam);
        
        //menuGrid_bg.setBackgroundDrawable(getMenuBackground(appmenuimage_array.length));
        //menuGrid.setNumColumns(Math.min(4, appmenuname_array.length));
        
        
        if(mIsList){
        	menuGrid_bg.setBackgroundColor(0xBE000000);
        	//menuGrid_bg.setBackgroundDrawable(getMenuBackground(appmenuimage_array_list.length));
            menuGrid.setNumColumns(1);
        	menuGrid.setAdapter(getMenuAdapter(appmenuname_array_list, appmenuimage_array_list,true));
        }else{
        	menuGrid_bg.setBackgroundColor(0xFFFFFFFF);
        	//menuGrid_bg.setBackgroundDrawable(getMenuBackground(appmenuimage_array.length));
    	    menuGrid.setAdapter(getMenuAdapter(appmenuname_array, appmenuimage_array,true));
    	    menuGrid.setNumColumns(Math.min(4, appmenuname_array.length));
            
        }
        menuGrid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            	switch (position) {
            	case DLGMENU_ORDER:
					Message msg1 = new Message();  
					msg1.what = DLGMENU_ORDER_SHOW;
					mHandler.sendMessage(msg1);
                    cleanup();
            		break;
            	case DLGMENU_HIDE_APP:
            		Intent  intent = new Intent(mContext, HideAppsSettings.class);
            		mContext.startActivity(intent);
            		cleanup();
            		break;
            	case DLGMENU_APPS_SETTINGS:
            		intent = new Intent(mContext, AllAppsSettings.class);
            		mContext.startActivity(intent);
            		cleanup();
            		break;
            	case DLGMENU_APPS_LJPAPPMANAGER:
            		Intent ljpintent = new Intent(SettingsValue.ACTION_LETHEME_LAUNCH);
            		ljpintent.putExtra("EXTRA",0);	                    
					
					try {
						mContext.startActivity(ljpintent);
			        } catch (ActivityNotFoundException e) {
			            Log.e(TAG, "Unable to launch. "+ ljpintent,e);
			        } catch (SecurityException e) {
			            Log.e(TAG, "Launcher does not have the permission to launch " 
			                    + " intent=" + ljpintent, e);
			        }
					cleanup();
            		break;
            	default:
            		break;

            	}
            }
        });
        mLeosDialog.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_BACK) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN)
                    {
                    	dialog.dismiss();
                    	mLeosDialog = null;
                    }
                }
                return false;
            }
        });
        return mLeosDialog;
    } 
	public void onCancel(DialogInterface dialog) {
	}

	public void onDismiss(DialogInterface dialog) {
	}

	private void cleanup() {
		mLeosDialog.dismiss();
	}
	public void onShow(DialogInterface dialog) {
	}

      
/* RK_ID: RK_MENU_RFACTOR . AUT: LIUYG1 . DATE: 2012-11-29 . S */
	 public ThemeSimpleAdapter getMenuAdapter(int[] str, int[] pic,boolean fromTheme) {
	        ArrayList<HashMap<String, Object>> menulist = new ArrayList<HashMap<String, Object>>();
	        final String image_key = "itemImage";
	        final String text_key = "itemText";
	        
	        /*RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2011-12-26 . S*/
//	        LauncherApplication app = (LauncherApplication) mContext.getApplicationContext();
	        //Resources res = getResources();//cancel by xingqx for sonar
	        /*RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2011-12-26 . E*/

	        for (int i = 0; i < str.length; i++) {
	            HashMap<String, Object> map = new HashMap<String, Object>();
	            /*RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2011-12-26 . S*/
//	            Drawable icon = Utilities.findDrawableById(res, pic[i], 
//	                  Launcher.this);
//	            Drawable icon = app.mLauncherContext.getDrawable(pic[i], false);
	            //Drawable icon = app.getResources().getDrawable(pic[i]);
	            Drawable icon = mContext.getResources().getDrawable(pic[i]);
	            
	            if (icon != null) {
	                map.put(image_key, icon);
	            } else {
	                map.put(image_key, pic[i]);
	            }
	            /*RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2011-12-26 . E*/
	            
	            map.put(text_key, mContext.getString(str[i]));
	            menulist.add(map);
	        }
	        int griditem = R.layout.menu_griditem;
	        if(!fromTheme){
	            griditem = R.layout.menu_add_griditem;
	        }

	        ThemeSimpleAdapter simple;
	        if(mIsList){
	        	griditem = R.layout.menu_listitem;
	            simple = new ThemeSimpleAdapter(mContext, menulist, griditem, new String[] { image_key,
	                text_key }, new int[] {R.id.menuitem_text, R.id.menuitem_text}, R.color.menu_text_color_list, R.color.def__menu_text_color,fromTheme, true);
	        }else{
	        	simple = new ThemeSimpleAdapter(mContext, menulist, griditem, new String[] { image_key,
		                text_key }, new int[] {R.id.menuitem_text, R.id.menuitem_text}, R.color.menu_text_color, R.color.def__menu_text_color,fromTheme);	
	        }
	        return simple;
	    }

/*private Drawable getMenuBackground(int itemNum) { 
	Drawable bg;
	int backRes;
	LauncherApplication app = (LauncherApplication) mContext.getApplicationContext();
		
    int rowNum = (int)Math.ceil(itemNum / 4.0f); 
	if (rowNum != 2) {
		
		backRes = R.drawable.menu_background_single;
		
	} else {
		backRes = R.drawable.menu_background;
	}
	
	if(mIsList){
		backRes = R.drawable.menu_background_list;
	}
	
	bg = app.getResources().getDrawable(backRes);
	return bg;
}*/
}
