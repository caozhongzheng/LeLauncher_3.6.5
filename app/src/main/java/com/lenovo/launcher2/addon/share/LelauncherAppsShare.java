package com.lenovo.launcher2.addon.share;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;
import com.lenovo.launcher2.commoninterface.ApplicationInfo;
import com.lenovo.launcher2.commonui.ShortcutGridView;
import com.lenovo.launcher2.customizer.Reaper;
import com.lenovo.launcher2.customizer.SettingsValue;

/**
 * Author : Liuyg1@lenovo.com
 * */
public class LelauncherAppsShare extends Activity {

	private Button mCancel, mShareFinish, mSelectAll, mSelectClear;
    private ShortcutGridView mShortcutGrid;
    private  List<Item> items = new ArrayList<Item>();
    private BaseAdapter mAdapter;
//    private ViewHolder mHolder;
//    File mDestFile = new File("sdcard/share.launcher");
    private ArrayList<String> submit = new ArrayList<String>();
    private static final String mimeType = "application/vnd.android.package-archive";
    private Typeface tf;
    private OnClickListener mButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
        	if (v.getId() == R.id.addfinish) {
            	if(!LeShareUtils.isInstalledQiezi(LelauncherAppsShare.this)){
            		LeShareUtils.showInstallDialog(LelauncherAppsShare.this,false);
                    Reaper.processReaper(LelauncherAppsShare.this, 
                            "Share", 
              				"ToAppQiezi",
              				""+submit.size(), 
              				Reaper.REAPER_NO_INT_VALUE );
            		return;
            	}else if(!LeShareUtils.isInstalledRightQiezi(LelauncherAppsShare.this)){
            		LeShareUtils.showInstallDialog(LelauncherAppsShare.this,true);
                    Reaper.processReaper(LelauncherAppsShare.this, 
                            "Share", 
              				"ToAppQiezi",
              				""+submit.size(), 
              				Reaper.REAPER_NO_INT_VALUE );
            		return;
            	}
            	if(submit.size()==0){
            		Toast.makeText(LelauncherAppsShare.this, getString(R.string.share_no_app), Toast.LENGTH_SHORT).show();
            		return;
            	}
        		String[] res = new String[submit.size()];
        		submit.toArray(res);
        		//                try {
        		//                    List <File> mDataList = new ArrayList<File>();
        		//        	        for (String resFile : submit) {
        		//        	            File file = new File(resFile);
        		//        	            mDataList.add(file);
        		//        	        }    
        		//        			zipFiles(mDataList,mDestFile);
        		//        		} catch (IOException e) {
        		//        			// TODO Auto-generated catch block
        		//        			e.printStackTrace();
        		//        		}
        		ArrayList<Parcelable> list = new ArrayList<Parcelable>();

        		for (String resFile : submit) {
        			File file = new File(resFile);
        			Uri uri = Uri.fromFile(file);
        			list.add(uri);
        		} 


        		final Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
//        		ComponentName  mComponentName = new ComponentName("com.lenovo.anyshare", "com.lenovo.anyshare.apexpress.ApDiscoverActivity"); 
//        		shareIntent.setComponent(mComponentName);
        		shareIntent.setType(mimeType);
        		PackageManager pm = getPackageManager();
        		
        		List<ResolveInfo> infoList = pm.queryIntentActivities(shareIntent, 0);
        		for (ResolveInfo info : infoList) {
        			String pn = info.activityInfo.packageName;

        			if (pn.equalsIgnoreCase("com.lenovo.anyshare")) {
        				String activity = info.activityInfo.name;
        				shareIntent.setClassName("com.lenovo.anyshare", activity);
        				Log.d("liuyg1", "find activity: " + activity);
        				break;
        			}
        		}

        		//            String mm = mDestFile.getPath();
        		//            Uri muri = Uri.parse(mm);
        		//            mIntent.putExtra(Intent.EXTRA_STREAM, muri);

//        		Bundle mBundle = new Bundle();
//        		mBundle.putString("FROM","launcherShare");
//        		mBundle.putString("CATEGORY","app");  //应用就是app；壁纸：wallpaper;主题：theme; 场景：scene 压缩包：zip
//        		shareIntent.putExtra("LAUNCHERBUNDLE",mBundle);

        		shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, list);
        		shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
        		try{
        			startActivity(shareIntent);
        		}catch(Exception e){
        			Toast.makeText(LelauncherAppsShare.this, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
        		}
                Reaper.processReaper(LelauncherAppsShare.this, 
                        "Share", 
          				"ToAppQiezi",
          				""+submit.size(), 
          				Reaper.REAPER_NO_INT_VALUE );
        		finish();

        	} else if (v.getId() == R.id.canceladd) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            } else if (v.getId() == R.id.selectAll) {
                int size = items.size();
                submit.clear();
                for (int i = 0; i < size; i++) {
                    Item item = items.get(i);
                    item.checked = true;
                    if (item.sourceDir != null && !item.sourceDir.equals("")) {
                        submit.add(item.sourceDir);
                    }
                }
                mAdapter.notifyDataSetChanged();
            } else if (v.getId() == R.id.clear) {
                int size = items.size();
                submit.clear();
                for (int i = 0; i < size; i++) {
                    Item item = items.get(i);
                    item.checked = false;
                }
                mAdapter.notifyDataSetChanged();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apps_share_layout);

        Window window = this.getWindow();

		//WindowManager.LayoutParams lp = window.getAttributes();
        window.setWindowAnimations(R.style.dialogWindowAnim);
        // set layout param
        
        TextView title = (TextView) findViewById(R.id.dialog_title);
        title.setText(R.string.desktop_share_app_text);
        View shareDialog_bg = this.findViewById(R.id.share_app_dialog_bg);
		shareDialog_bg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO
				finish();
			}
		});
        
        mCancel = (Button) findViewById(R.id.canceladd);
        mCancel.setText(R.string.btn_cancel);
        mShareFinish = (Button) findViewById(R.id.addfinish);
        mShareFinish.setText(R.string.send_leshare);
        mSelectAll = (Button) findViewById(R.id.selectAll);
        mSelectClear = (Button) findViewById(R.id.clear);
        mShortcutGrid = (ShortcutGridView) findViewById(R.id.applist);
        /***RK_ID:RK_FONT AUT:zhanglz1@lenovo.com. DATE: 2013-02-21 S***/        
		if(tf == null)
			tf = SettingsValue.getFontStyle(this);
        if (tf != null && tf != title.getTypeface()){
        	title.setTypeface(tf);
        	mCancel.setTypeface(tf);
        	mShareFinish.setTypeface(tf);
        	mSelectAll.setTypeface(tf);
        	mSelectClear.setTypeface(tf);
        }
        /***RK_ID:RK_FONT AUT:zhanglz1@lenovo.com. DATE: 2013-02-21 E***/         
        mCancel.setOnClickListener(mButtonListener);
        mShareFinish.setOnClickListener(mButtonListener);
        mSelectAll.setOnClickListener(mButtonListener);
        mSelectClear.setOnClickListener(mButtonListener);
        mAdapter = new EfficientAdapter(this, getItems());
        mShortcutGrid.setAdapter(mAdapter);
    }

//    @Override
//    public void onAttachedToWindow() {
//        super.onAttachedToWindow();
//
//        final IntentFilter filter = new IntentFilter();
//        filter.addAction(Intent.ACTION_SCREEN_OFF);
//        registerReceiver(mReceiver, filter);
//
//        mAttached = true;
//    }

//    @Override
//    public void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//
//        if (mAttached) {
//            unregisterReceiver(mReceiver);
//            mAttached = false;
//        }
//    }

//    private boolean mAttached = false;
//    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            final String action = intent.getAction();
//            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
//                finish();
//            }
//        }
//    };




    public List<Item> getItems() {
        PackageManager packageManager = getPackageManager();
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        items.clear();
        final List<ResolveInfo> infolist = packageManager.queryIntentActivities(mainIntent, 0);

        Collections.sort(infolist, new ResolveInfo.DisplayNameComparator(packageManager));

        for (int i = 0; i < infolist.size(); i++) {
            ResolveInfo info = infolist.get(i);
           // if (info.activityInfo.packageName.contains(SettingsValue.THEME_PACKAGE_NAME_PREF)) {
            	//continue;
           // }
            items.add(new Item(this, packageManager, info, false));
        }

        return items;
    }

    public static class Item {

    	private CharSequence label;
    	private  Drawable icon;
    	protected  String sourceDir;
    	private  Boolean checked;

        /**
         * Create a list item and fill it with details from the given
         * {@link ResolveInfo} object.
         */
        Item(Context context, PackageManager pm, ResolveInfo resolveInfo, Boolean check) {
            label = resolveInfo.loadLabel(pm);
            if (label == null && resolveInfo.activityInfo != null) {
                label = resolveInfo.activityInfo.name;
            }

            // icon = resolveInfo.loadIcon(pm);
            sourceDir = resolveInfo.activityInfo.applicationInfo.sourceDir;
            //use this method is better than below 
            //String packageName = resolveInfo.activityInfo.applicationInfo.packageName;            
//            if (packageName != null) {
//                try {
//					sourceDir = pm.getApplicationInfo(packageName, 0).sourceDir;
//				} catch (NameNotFoundException e) {
//					// TODO Auto-generated catch block
//					sourceDir = "";
//				}
//            }
            
            LauncherApplication app = (LauncherApplication) context.getApplicationContext();
            ApplicationInfo appInfo = new ApplicationInfo(context.getPackageManager(), resolveInfo, app.getIconCache(), null);
            icon = new BitmapDrawable(app.getResources(), appInfo.iconBitmap);
            checked = check;
        }

    }

    private class EfficientAdapter extends BaseAdapter {
        // private LayoutInflater mInflater;
        private final List<Item> mItems;
        private Context mContext;

        /**
         * Create an adapter for the given items.
         */
        public EfficientAdapter(Context context, List<Item> items) {
            // mInflater = (LayoutInflater)
            // context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mItems = items;
            mContext = context;
            /*** RK_ID:RK_FONT AUT:zhanglz1@lenovo.com. DATE: 2013-02-21 S ***/
    		if(tf == null)
    			tf = SettingsValue.getFontStyle(mContext);
    		/*** RK_ID:RK_FONT AUT:zhanglz1@lenovo.com. DATE: 2013-02-21 E ***/
        }

        public int getCount() {
            return mItems.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        /**
         * Make a view to hold each row.
         * 
         * @see android.widget.ListAdapter#getView(int, android.view.View,
         *      android.view.ViewGroup)
         */
        public View getView(int position, View convertView, ViewGroup parent) {

        	
        	if (convertView == null) {
        		convertView = View.inflate(mContext, R.layout.app_to_category, null);
        	}
        	final TextView label = (TextView) convertView.findViewById(R.id.category_label);
            final ImageView icon = (ImageView) convertView.findViewById(R.id.category_icon);
            final ImageView select_icon = (ImageView) convertView.findViewById(R.id.select_icon);
    		/*** RK_ID:RK_FONT AUT:zhanglz1@lenovo.com. DATE: 2013-02-21 S ***/
			if (tf != null && tf != label.getTypeface())
				label.setTypeface(tf);
			/*** RK_ID:RK_FONT AUT:zhanglz1@lenovo.com. DATE: 2013-02-21 E ***/
    		
        	icon.setImageDrawable(mItems.get(position).icon);
            label.setText(mItems.get(position).label);
            if(mItems.get(position).checked){
            	select_icon.setVisibility(View.VISIBLE);
            }else{
            	select_icon.setVisibility(View.INVISIBLE);
            }
            
            convertView.setTag(position);
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                	int position = Integer.parseInt(view.getTag().toString());
                    final ImageView select_icon = (ImageView) view.findViewById(R.id.select_icon);
                    int vis = select_icon.getVisibility();
                    boolean checked = false;
                    checked = !mItems.get(position).checked;
                	if(vis == View.VISIBLE){
                		select_icon.setVisibility(View.INVISIBLE);
                 	}else{
                 		select_icon.setVisibility(View.VISIBLE);
                 	}
                	
                	mItems.get(position).checked = checked;
                    String temp;
                    if (checked) {
                        temp = mItems.get(position).sourceDir;
                        if (temp != null && !temp.equals("")) {
                            submit.add(mItems.get(position).sourceDir);
                        }
                    } else {
                        temp = mItems.get(position).sourceDir;
                        submit.remove(temp);
                    }
                }
            });
             /* RK_ID: RK_MENU_RFACTOR . AUT: LIUYG1 . DATE: 2012-12-11 .E */
            return convertView;
        }
    }
//	 private static final int BUFF_SIZE = 1024 * 1024; // 1M Byte
//
//	    /**
//	     * 批量压缩文件（夹）
//	     *
//	     * @param resFileList 要压缩的文件（夹）列表
//	     * @param zipFile 生成的压缩文件
//	     * @throws IOException 当压缩过程出错时抛出
//	     */
//	    public static void zipFiles(List<File> resFileList, File zipFile) throws IOException {
//	        ZipOutputStream zipout = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(
//	                zipFile), BUFF_SIZE));
//	        for (File resFile : resFileList) {
//	            zipFile(resFile, zipout, "");
//	        }
//	        zipout.close();
//	    }
//	    /**
//	     * 压缩文件
//	     *
//	     * @param resFile 需要压缩的文件（夹）
//	     * @param zipout 压缩的目的文件
//	     * @param rootpath 压缩的文件路径
//	     * @throws FileNotFoundException 找不到文件时抛出
//	     * @throws IOException 当压缩过程出错时抛出
//	     */
//	    private static void zipFile(File resFile, ZipOutputStream zipout, String rootpath)
//	    		throws FileNotFoundException, IOException {
//	    	rootpath = rootpath + (rootpath.trim().length() == 0 ? "" : File.separator)
//	    			+ resFile.getName();
//	    	rootpath = new String(rootpath.getBytes("8859_1"), "GB2312");
//	    	if (resFile.isDirectory()) {
//	    		File[] fileList = resFile.listFiles();
//	    		for (File file : fileList) {
//	    			zipFile(file, zipout, rootpath);
//	    		}
//	    	} else {
//	    		byte buffer[] = new byte[BUFF_SIZE];
//	    		BufferedInputStream in = new BufferedInputStream(new FileInputStream(resFile),
//	    				BUFF_SIZE);
//	    		zipout.putNextEntry(new ZipEntry(rootpath));
//	    		int realLength;
//	    		while ((realLength = in.read(buffer)) != -1) {
//	    			zipout.write(buffer, 0, realLength);
//	    		}
//	    		in.close();
//	    		zipout.flush();
//	    		zipout.closeEntry();
//	    	}
//}
}
