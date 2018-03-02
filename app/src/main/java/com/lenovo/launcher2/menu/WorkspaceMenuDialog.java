package com.lenovo.launcher2.menu;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.commonui.LeAlertDialog;
import com.lenovo.launcher2.commonui.MenuGridView;
import com.lenovo.launcher2.customizer.GlobalDefine;
import com.lenovo.launcher2.customizer.Reaper;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.ThemeSimpleAdapter;
import com.lenovo.launcher2.settings.MoreSettings;
//import com.lenovo.lejingpin.hw.ui.Util;

public class WorkspaceMenuDialog implements DialogInterface.OnCancelListener, DialogInterface.OnDismissListener,
DialogInterface.OnShowListener  {

	private static final String TAG = "LauncherMenuDialogActivity";
	
	   /***RK_ID:RK_CHANGE_MENU_LAYOUT AUT:zhanglz1@lenovo.com. DATE:2013-01-16.S***/    
	private static final int DLGMENU_ADD = 0;
    private static final int DLGMENU_THEME_SETTINGS = DLGMENU_ADD + 1;
    private static final int DLGMENU_WALLPAPER_SETTINGS = DLGMENU_ADD + 2;
    private static final int DLGMENU_SCREEN_SETTINGS = DLGMENU_ADD + 3; 
    private static final int DLGMENU_DESKTOP_SETTINGS = DLGMENU_ADD + 4;
    private static final int DLGMENU_SYSTEM_SETTINGS = DLGMENU_ADD + 5;
//  private static final int DLGMENU_APPLICATION_SETTINGS = DLGMENU_ADD + 3;
//    private static final int DLGMENU_SHARE = DLGMENU_ADD + 5;

    public static final int  DLGMENU_ADD_SHOW = 51;
    public static final int  DLGMENU_SCREEN_SETTINGS_SHOW = 52;
    private int[] menuname_array = { 
    		R.string.menu_add, 
    		R.string.menu_theme_settings, 
    		R.string.menu_wallpaper,
    		R.string.menu_screen_setting,
    		R.string.menu_desktop_settings,
    		R.string.menu_settings };
		//R.string.menu_manage_apps, R.string.menu_share,
    
    private int[] menuimage_array = { 
    		R.drawable.main_menu_addprogram, 
            R.drawable.main_menu_themesetting,
            R.drawable.main_menu_wallpaper,
            
            R.drawable.main_menu_screensetting,
            
            R.drawable.desk_setting,  
            R.drawable.main_menu_setting };
    //R.drawable.main_menu_personal, R.drawable.main_menu_share_us,  
    
    /***RK_ID:RK_CHANGE_MENU_LAYOUT AUT:zhanglz1@lenovo.com. DATE:2013-01-16.E***/    
    private Dialog mLeosDialog = null;
    private LeAlertDialog mLeAlertDialog = null;
   	private Context mContext;
   	private  Handler mHandler;
   	public Dialog createDialog(Context context, Handler handler) {
   		mContext = context;
   		mHandler = handler;  
   		mLeosDialog = new Dialog(mContext,R.style.Theme_LeLauncher_TransDialog);// , R.style.menu_style);
		mLeosDialog.setCanceledOnTouchOutside(true);
		mLeosDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mLeosDialog.setContentView(R.layout.menu_layout);
        Window window =mLeosDialog.getWindow();

        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    //    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL);
        window.setWindowAnimations(R.style.MenuWindowAnimTest);
        
        MenuGridView menuGrid = (MenuGridView) mLeosDialog.findViewById(R.id.grid_view);
        View menuDialog_bg =  mLeosDialog.findViewById(R.id.menu_dialog_bg);
        menuDialog_bg.setOnClickListener(new OnClickListener() {
    		
    		@Override
    		public void onClick(View v) {
    			if(mLeosDialog != null){
    			    mLeosDialog.dismiss();
    			}
    		}
    	});
        
        //View menuGrid_bg =  mLeosDialog.findViewById(R.id.grid_view_bg);
        //menuGrid_bg.setBackgroundColor(0xFFFFFFFF);
        //menuGrid_bg.setBackgroundDrawable(getMenuBackground(menuname_array.length));
//    	menuGrid.setNumColumns(3);//Math.min(4, menuname_array.length)

    	menuGrid.setAdapter(getMenuAdapter(menuname_array, menuimage_array,true));
        menuGrid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                case DLGMENU_ADD:
                	Message msg1 = new Message();  
					msg1.what = DLGMENU_ADD_SHOW;
					mHandler.sendMessage(msg1);
					 cleanup();
                    /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2012-12-21 S*/
                    Reaper.processReaper( mContext, 
                    	   Reaper.REAPER_EVENT_CATEGORY_DESKTOPADD, 
      					   Reaper.REAPER_EVENT_ACTION_DESKTOPADD_FROMMENU,
      					   Reaper.REAPER_NO_LABEL_VALUE, 
      					   Reaper.REAPER_NO_INT_VALUE );
                    /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2012-12-21 E*/     
                   
                    break;
                case DLGMENU_THEME_SETTINGS:
                	
                    //added by yumina for the themecenter   
                    if(SettingsValue.isContainsThemeCenter(mContext)){
                        Intent newIntent = new Intent();
                        //newIntent.setClassName("com.lenovo.themecenter","com.lenovo.themecenter.ThemeCenterActivity");
                        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        //newIntent.setAction("com.lenovo.themecenter.launcher");
                        newIntent.setAction("com.lenovo.themecenter.main");
                        newIntent.putExtra("invoke_external",true);

                        startActivitySafely(newIntent, "");
                        cleanup();
                        return;
                    }
/* //removed by yumina for the jira bug BLADEX-3271
                    if(!checkNetwork()){
                		cleanup();
                		break;
                	}
*/
                    Intent intent = null;
                    intent = new Intent(SettingsValue.ACTION_LETHEME_LAUNCH);
                    intent.setPackage(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF);
                    intent.putExtra("EXTRA",1);
                    startActivitySafely(intent, "");
                    cleanup();
                    break;
                /***RK_ID:RK_CHANGE_MENU_LAYOUT AUT:zhanglz1@lenovo.com. DATE:2013-01-16.S***/    
                case DLGMENU_WALLPAPER_SETTINGS:
                	
                    if(SettingsValue.isContainsThemeCenter(mContext)){
                        Intent newIntent = new Intent("com.lenovo.themecenter.action.wallpaperex");
//                        newIntent.setClassName("com.lenovo.themecenter","com.lenovo.themecenter.ThemeCenterActivity");
                        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        newIntent.setAction("com.lenovo.themecenter.wallpaper");
                        startActivitySafely(newIntent, "");
                        cleanup();
                        return;
                    }
/*
                    if(!checkNetwork()){
                		cleanup();
                		break;
                	}
*/
                Intent setWallpaper = new Intent(SettingsValue.ACTION_LETHEME_LAUNCH);
                setWallpaper.setPackage(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF);
                setWallpaper.putExtra("EXTRA",2);
                startActivitySafely(setWallpaper, "");
                cleanup();
        			
                   	/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2012-12-21 S*/
                     Reaper.processReaper( mContext, 
                         Reaper.REAPER_EVENT_CATEGORY_WALLPAPER, 
             		    Reaper.REAPER_EVENT_ACTION_WALLPAPER_FROMMENU,
             			Reaper.REAPER_NO_LABEL_VALUE, 
             			Reaper.REAPER_NO_INT_VALUE );
                     /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2012-12-21 E*/ 
                    
                    break;
                /*case DLGMENU_APPLICATION_SETTINGS:
                	intent = new Intent(SettingsValue.ACTION_LETHEME_LAUNCH);
					intent.putExtra("EXTRA", -1);
					startActivitySafely(intent, "");
					cleanup();
                  	Reaper.processReaper( mContext, 
                        Reaper.REAPER_EVENT_CATEGORY_APPPROMOTE, 
            		    Reaper.REAPER_EVENT_ACTION_APPPROMOTE_ENTER,
            			Reaper.REAPER_NO_LABEL_VALUE, 
            			Reaper.REAPER_NO_INT_VALUE );
                    
                    break;*/
                /***RK_ID:RK_CHANGE_MENU_LAYOUT AUT:zhanglz1@lenovo.com. DATE:2013-01-16.E***/    
                case DLGMENU_DESKTOP_SETTINGS:
                	 intent = new Intent(SettingsValue.ACTION_DESKTOPSETTING_LAUNCH);
                	 intent.setPackage(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF);
                	 
                	 startActivitySafely(intent, "");
                	  cleanup();
    				/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2012-12-21 S*/    
    				//Launcher.processReaper(mLauncher, Reaper.REAPER_EVENT_DESKTOP_SETTINGS, 
   					//     Reaper.REAPER_NO_LABEL_VALUE, Reaper.REAPER_NO_INT_VALUE);
                    Reaper.processReaper( mContext, 
                    	   Reaper.REAPER_EVENT_CATEGORY_DESKTOPSETTING, 
      					   Reaper.REAPER_EVENT_ACTION_DESKTOPSETTING_ENTER,
      					   Reaper.REAPER_NO_LABEL_VALUE, 
      					   Reaper.REAPER_NO_INT_VALUE );
                    /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2012-12-21 E*/ 
                  
                  break;    
                case DLGMENU_SYSTEM_SETTINGS:
                    	intent = new Intent(
    						android.provider.Settings.ACTION_SETTINGS);
    				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
    						| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
    				startActivitySafely(intent, "");
    				cleanup();
    				break;
    				
/*                case DLGMENU_SHARE:
                	intent = new Intent(mContext, LeShareActivity.class);
                	mContext.startActivity(intent);
			        cleanup();
			        Reaper.processReaper( mContext, 
			        	   Reaper.REAPER_EVENT_CATEGORY_SHARE, 
						   Reaper.REAPER_EVENT_ACTION_SHARE_ENTER,
						   Reaper.REAPER_NO_LABEL_VALUE, 
						   Reaper.REAPER_NO_INT_VALUE );

                	break;
*/
                	
                case DLGMENU_SCREEN_SETTINGS:
                	Message msg2 = new Message();  
                	msg2.what = DLGMENU_SCREEN_SETTINGS_SHOW;
					mHandler.sendMessage(msg2);
					 cleanup();
    					/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2012-12-21 S*/    
				        Reaper.processReaper( mContext, 
				        	   Reaper.REAPER_EVENT_CATEGORY_SCREEN, 
							   Reaper.REAPER_EVENT_ACTION_SCREEN_SCREENENTER,
							   Reaper.REAPER_NO_LABEL_VALUE, 
							   Reaper.REAPER_NO_INT_VALUE );
				        /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2012-12-21 E*/ 
//    				}
				       
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
        
        mLeAlertDialog  = new LeAlertDialog(mContext, R.style.Theme_LeLauncher_Dialog_Shortcut);
        mLeAlertDialog.setLeTitle(R.string.lejingpin_settings_title);
        mLeAlertDialog.setLeMessage(mContext.getText(R.string.confirm_network_open));
        mLeAlertDialog.setLeNegativeButton(mContext.getText(R.string.cancel_action), new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		        	dialog.dismiss();
		            Toast.makeText(mContext, R.string.version_update_toast, Toast.LENGTH_SHORT).show();
		        }
		    });

        mLeAlertDialog.setLePositiveButton(mContext.getText(R.string.rename_action), new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		        	Intent intent = new Intent();
		            intent.setClass(mContext, MoreSettings.class);
		            mContext.startActivity(intent);
		        }
		    });
        mLeAlertDialog.setOnKeyListener(new OnKeyListener() {
         @Override
         public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
             if (keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_BACK) {
                 if (event.getAction() == MotionEvent.ACTION_DOWN)
                 {
                 	dialog.dismiss();
                 }
             }
             return false;
         }
		});
        
        return mLeosDialog;
    } 
   	
   	public LeAlertDialog getAlertDialog(){
   		return mLeAlertDialog;
   	}
   	
	public void onCancel(DialogInterface dialog) {
	}

	public void onDismiss(DialogInterface dialog) {
	}

	private void cleanup() {
		if(mLeosDialog != null){
		    mLeosDialog.dismiss();
		}
	}
	@Override
	public void onShow(DialogInterface arg0) {
		// TODO Auto-generated method stub
		
	}
//	@Override
//	public void onClick(View v) {
//		// TODO Auto-generated method stub
//		
//	}
//    private boolean mAttached = false;
//	 @Override
//	    public void onAttachedToWindow() {
//	        super.onAttachedToWindow();
//	        mAttached = true;
//	        final IntentFilter filter = new IntentFilter();
//	        filter.addAction(Intent.ACTION_SCREEN_OFF);
//	        filter.addAction(Intent.ACTION_SCREEN_ON);
//	        filter.addAction(Intent.ACTION_USER_PRESENT);
//	        mContext.registerReceiver(mReceiver, filter);
//	    }
//	@Override
//    public void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        if (mAttached) {
//            unregisterReceiver(mReceiver);
//            mAttached = false;
//        }
//    }
	
//	private boolean checkNetwork(){
//		if( GlobalDefine.getVerisonWWConfiguration(mContext)
//		  || GlobalDefine.getVerisonCMCCTDConfiguration(mContext)){
//			return true;
//		}
//		if(!Util.getInstance().isNetworkEnabled(mContext) && mLeAlertDialog != null){
//			mLeAlertDialog.show();
//   		    return false;
//	   }
//		return true;
//	}

	/* RK_ID: RK_MENU_RFACTOR . AUT: LIUYG1 . DATE: 2012-11-29 . S */
	 public ThemeSimpleAdapter getMenuAdapter(int[] str, int[] pic,boolean fromTheme) {
	        ArrayList<HashMap<String, Object>> menulist = new ArrayList<HashMap<String, Object>>();
	        final String image_key = "itemImage";
	        final String text_key = "itemText";
	        
	        /*RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2011-12-26 . S*/
	        //LauncherApplication app = (LauncherApplication) getApplicationContext();
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

	        ThemeSimpleAdapter simple = new ThemeSimpleAdapter(mContext, menulist, griditem, new String[] { image_key,
	                text_key }, new int[] {R.id.menuitem_text, R.id.menuitem_text}, R.color.menu_text_color, R.color.def__menu_text_color,fromTheme);
	        return simple;
	    }
        /* RK_ID: RK_MENU_RFACTOR . AUT: LIUYG1 . DATE: 2012-11-29 . E */
        private Drawable getMenuBackground(int itemNum) { 
	        Drawable bg;
	        int backRes;
	       
	        bg = mContext.getResources().getDrawable(R.drawable.menu_background);
	        return bg;
        }
        private boolean startActivitySafely(Intent intent, Object tag) {
        	
    		//Fix bug for LELAUNCHER-1207   Martin{
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            /* RK_ID: RK_SHORTCUT. AUT: liuli1 . DATE: 2012-10-19 . START */
            // fix bug 171724
//            if (SettingsValue.ACTION_LETHEME_LAUNCH.equals(intent.getAction())) {
//                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            }
            /* RK_ID: RK_SHORTCUT. AUT: liuli1 . DATE: 2012-10-19 . END */
        	//}
            try {
            	mContext.startActivity(intent);
                return true;
            } catch (ActivityNotFoundException e) {
                Toast.makeText(mContext, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Unable to launch. tag=" + tag + " intent=" + intent, e);
            } catch (SecurityException e) {
                Toast.makeText(mContext, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Launcher does not have the permission to launch " + intent +
                        ". Make sure to create a MAIN intent-filter for the corresponding activity " +
                        "or use the exported attribute for this activity. "
                        + "tag="+ tag + " intent=" + intent, e);
            }
            return false;
        }

}
