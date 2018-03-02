/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lenovo.launcher2.commoninterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;
import com.lenovo.launcher2.commonui.ShortcutGridView;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.Utilities;
import com.lenovo.launcher2.weather.widget.utils.WeatherUtilites;

public class AddShortcutItemActivity extends Activity implements AdapterView.OnItemClickListener {
    static final String TAG = "AddShortcutItemActivity";

    Button mCancel, mAddFinish;
    ListView mShortcutList;
    /*RK_GRID_VIEW  dining@lenovo.com 2012-10-16 S*/
    boolean usingGrid  =  true;// if is false use list view
    ShortcutGridView mShortcutGridView;
    /*RK_GRID_VIEW  dining@lenovo.com 2012-10-16 S*/
    List<Item> items = new ArrayList<Item>();
    private BaseAdapter mAdapter;
    private int emptynum = 0;
    private int selectednum = 0;
    // private LauncherService mLauncherService = LauncherService.getInstance();
    private LinkedList<String> mIntentListInfo = new LinkedList<String>();
    private boolean isFolder;
    private ViewHolder mHolder;
//    private String selectedPackageName;
    /* RK_ID: RK_FOLDER_EDITOR . AUT: chenrong2 . DATE: 2012-03-20 . S */
    private LinearLayout mFolderNameView;
    private EditText mFolderNameEditor;
    /* RK_ID: RK_FOLDER_EDITOR . AUT: chenrong2 . DATE: 2012-03-20 . E */
    
    /*PK_ID:SHORTCUT NUM DISPLAY AUTH:GECN1 DATE:2012-10-29 S  */
    private String mTitle;
    private TextView mDialogTitle;
    /*PK_ID:SHORTCUT NUM DISPLAY AUTH:GECN1 DATE:2012-10-29 E  */

    OnClickListener mButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.equals(mAddFinish)) {
                if (isFolder) {
                    Intent i = getIntent();
                    i.putExtra("EXTRA_INTENT", mIntentListInfo);
                    i.putExtra("EXTRA_FOLDER", true);
                    /* RK_ID: RK_FOLDER_EDITOR . AUT: chenrong2 . DATE: 2012-03-20 . S */
                    i.putExtra("EXTRA_FOLDER_NAME", mFolderNameEditor.getText().toString());
                    /* RK_ID: RK_FOLDER_EDITOR . AUT: chenrong2 . DATE: 2012-03-20 . E */
                    setResult(Activity.RESULT_OK, i);
                    finish();
                    Log.e(TAG, "Click on unknown view");
                }
                // RK_ID: WORKSPACE_ADD_APP AUT: liuli1 DATE: 2012-02-17 START
                // bug : Bug 153466
                else {
                    Intent i = getIntent();
                    i.putExtra("EXTRA_INTENT", mIntentListInfo);
                    i.putExtra("EXTRA_FOLDER", false);
                    setResult(Activity.RESULT_OK, i);
                    Log.e(TAG, "mIntentListInfo" + mIntentListInfo.toString());
                    finish();
                }
            } else if (v.equals(mCancel)) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        }
    };

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        WeatherUtilites.setAnimaState(this, false);
        final Intent intent = getIntent();
        emptynum = intent.getIntExtra("EMPTYNUM", 0);
        isFolder = intent.getBooleanExtra("ISFOLDER", false);
        /*** AUT:zhaoxy . DATE:2012-03-06 . START ***/
        //DELETE BY GECN1
        //setTitle(isFolder ? R.string.addfolder : R.string.title_select_application);
        /*** AUT:zhaoxy . DATE:2012-03-06 . END ***/
        /*RK_GRID_VIEW  dining@lenovo.com 2012-10-16 S*/
        if(usingGrid){
            setContentView(R.layout.add_shortcut_grid);
        }else{
            setContentView(R.layout.add_shortcut_list);
        }
        /*RK_GRID_VIEW  dining@lenovo.com 2012-10-16 E*/

        mCancel = (Button) findViewById(R.id.canceladd);
        mCancel.setText(getString(android.R.string.cancel));
        mAddFinish = (Button) findViewById(R.id.addfinish);
        mAddFinish.setText(getString(android.R.string.ok));
        
        /*RK_GRID_VIEW  dining@lenovo.com 2012-10-16 S*/
        if(usingGrid){
            mShortcutGridView = (ShortcutGridView) findViewById(R.id.applist);
            mAdapter = new AppsGridAdapter(this, getItems());
            mShortcutGridView.setAdapter(mAdapter);
        }else{
            mShortcutList = (ListView) findViewById(R.id.applist);
            mShortcutList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            mAdapter = new EfficientAdapter(this, getItems());
            mShortcutList.setAdapter(mAdapter);
            mShortcutList.setOnItemClickListener(this);
        }
        /*RK_GRID_VIEW  dining@lenovo.com 2012-10-16 E*/
        /* AUT: liuli1 DATE: 2012-02-13. START */
        // Bug 152768 liuli1
//        if (isFolder){
//        	emptynum = getResources().getInteger(R.integer.folder_max_num_items);
//        }
        /* AUT: liuli1 DATE: 2012-02-13. END */

        mCancel.setOnClickListener(mButtonListener);
        mAddFinish.setOnClickListener(mButtonListener);
        // mLauncherService.mIntentListInfo.clear();
        mIntentListInfo.clear();

        /* RK_ID: RK_FOLDER_EDITOR . AUT: chenrong2 . DATE: 2012-03-20 . S */
        mFolderNameView = (LinearLayout) findViewById(R.id.folder_name_view);
        mFolderNameView.setVisibility(isFolder ? View.VISIBLE : View.GONE);
        mFolderNameEditor = (EditText) findViewById(R.id.folder_name_editor);
        /* RK_ID: RK_FOLDER_EDITOR . AUT: chenrong2 . DATE: 2012-03-20 . E */
        /*PK_ID:SHORTCUT NUM DISPLAY AUTH:GECN1 DATE:2012-10-29 S  */
        mTitle = getResources().getString((isFolder ? R.string.addfolder : R.string.title_select_application ));
//        setTitle(mTitle+ "(" +String.valueOf(selectednum) +"/" + String.valueOf(emptynum)+")" );
        /* RK_ID: RK_FOLDER_TITLE . AUT: shenchao1 . DATE: 2012-12-12 . S */
        mDialogTitle=(TextView)findViewById(R.id.dialog_title);
        if(isFolder){
            mDialogTitle.setText(mTitle+ "(" +String.valueOf(selectednum) +")");
        }else{
            mDialogTitle.setText(mTitle+ "(" +String.valueOf(selectednum) +"/" + String.valueOf(emptynum)+")");

        }
        ImageView icon = (ImageView) findViewById(R.id.dialog_icon);
        if(isFolder){
        icon.setBackgroundResource(R.drawable.icon_for_folder);
        }else{
        	 icon.setBackgroundResource(R.drawable.icon_for_apps);
        }
 
        Window window = getWindow();
		window.setGravity(Gravity.CENTER);
		window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		window.setBackgroundDrawable(null);
		window.setBackgroundDrawableResource(android.R.color.transparent);
		window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setWindowAnimations(R.style.dialogWindowAnim);
        //test by dining
        View shareDialog_bg = this.findViewById(R.id.share_dialog_bg);
        if(shareDialog_bg != null){
        	
			shareDialog_bg.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View v) {
					// TODO
					finish();
				}
			});
        }
		//set the window width 
        Utilities.setDialogWidth(window,getResources());
        this.setFinishOnTouchOutside(true);

    }

    @Override
    protected void onDestroy() {
        /* RK_ID: RK_TOAST. AUT: liuli1 . DATE: 2012-10-25 . START */
        if (usingGrid) {
            ((AppsGridAdapter) mAdapter).cancelToast();
        }
        /* RK_ID: RK_TOAST. AUT: liuli1 . DATE: 2012-10-25 . END */
        super.onDestroy();
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // CheckedTextView checked = (CheckedTextView) view;
//        if (isFolder) {
//            emptynum = getResources().getInteger(R.integer.folder_max_num_items);
//        }
        mHolder = (ViewHolder) view.getTag();
        
        boolean checked = mHolder.mCheck.isChecked();
        if (selectednum < emptynum) {
            mHolder.mCheck.toggle();
            checked = mHolder.mCheck.isChecked();
            items.get(position).checked = checked;
            if (checked) {
                selectednum++;
                mIntentListInfo.add(items.get(position).getIntent().toUri(0));
            } else {
                selectednum--;
                mIntentListInfo.remove(items.get(position).getIntent().toUri(0));
            }
        } else {
            if (checked) {
                mHolder.mCheck.toggle();
                items.get(position).checked = false;
                selectednum--;
                mIntentListInfo.remove(items.get(position).getIntent().toUri(0));
            } else {
                Toast.makeText(this, R.string.add_shortcut_to_workspace_more, Toast.LENGTH_SHORT).show();
            }
        }
        /*PK_ID:SHORTCUT NUM DISPLAY AUTH:GECN1 DATE:2012-10-29 S  */
//        setTitle(mTitle + "(" +String.valueOf(selectednum) +"/" + String.valueOf(emptynum)+")");
        if(isFolder){
            mDialogTitle.setText(mTitle+ "(" +String.valueOf(selectednum) +")");
        }else{
            mDialogTitle.setText(mTitle+ "(" +String.valueOf(selectednum) +"/" + String.valueOf(emptynum)+")");

        }        /*PK_ID:SHORTCUT NUM DISPLAY AUTH:GECN1 DATE:2012-10-29 E  */
        /* AUT: liuli1 DATE: 2012-02-13. END */
        Log.e(TAG, "int position =================" + position + "view==========" + view + " num=" + selectednum
                + "mIntentListInfo" + mIntentListInfo.toString());
    }

    public List<Item> getItems() {
        PackageManager packageManager = getPackageManager();
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        items.clear();
        final List<ResolveInfo> infolist = packageManager.queryIntentActivities(mainIntent, 0);

        Collections.sort(infolist, new ResolveInfo.DisplayNameComparator(packageManager));
//        ActivityInfo ai = null;
//   	    boolean checked =false;

        for (int i = 0; i < infolist.size(); i++) {
            ResolveInfo info = infolist.get(i);
            if (info.activityInfo.packageName.startsWith(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF)
            	|| info.activityInfo.packageName.contains(SettingsValue.THEME_PACKAGE_NAME_PREF)
            	|| info.activityInfo.packageName.contains(SettingsValue.THEME_PACKAGE_QIGAMELOCKSCREEN_PREF)) {
            	continue;
            }
            items.add(new Item(this, packageManager, info, false));
        }
        return items;
    }

    public static class Item {

        CharSequence label;
        Drawable icon;
        String packageName;
        String className;
        Boolean checked;
        /**
         * Create a list item and fill it with details from the given {@link ResolveInfo} object.
         */
        Item(Context context, PackageManager pm, ResolveInfo resolveInfo, Boolean check) {
            label = resolveInfo.loadLabel(pm);
            if (label == null && resolveInfo.activityInfo != null) {
                label = resolveInfo.activityInfo.name;
            }

            // icon = resolveInfo.loadIcon(pm);
            packageName = resolveInfo.activityInfo.applicationInfo.packageName;
            className = resolveInfo.activityInfo.name;

            LauncherApplication app = (LauncherApplication) context.getApplicationContext();
            ApplicationInfo appInfo = new ApplicationInfo(context.getPackageManager(), resolveInfo, app.getIconCache(),
                    null);
            icon = new BitmapDrawable(app.getResources(), appInfo.iconBitmap);

            checked = check;
        }

        /**
         * Build the {@link Intent} described by this item. If this item can't create a valid
         * {@link android.content.ComponentName}, it will return
         * {@link Intent#ACTION_CREATE_SHORTCUT} filled with the item label.
         */
        Intent getIntent() {
            Intent mBaseIntent = new Intent(Intent.ACTION_MAIN, null);
            mBaseIntent.addCategory(Intent.CATEGORY_DEFAULT);

            if (packageName != null && className != null) {
                // Valid package and class, so fill details as normal intent
                mBaseIntent.setClassName(packageName, className);
                return mBaseIntent;
            }
            return mBaseIntent;
        }
    }

    private class EfficientAdapter extends BaseAdapter {
//        private LayoutInflater mInflater;
        private final List<Item> mItems;
        private Context mContext;

        /**
         * Create an adapter for the given items.
         */
        public EfficientAdapter(Context context, List<Item> items) {
//            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mItems = items;
            mContext = context;
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
         * @see android.widget.ListAdapter#getView(int, android.view.View, android.view.ViewGroup)
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            // A ViewHolder keeps references to children views to avoid unneccessary calls
            // to findViewById() on each row.

            // When convertView is not null, we can reuse it directly, there is no need
            // to reinflate it. We only inflate a new View when the convertView supplied
            // by ListView is null.
            ViewHolder holder;
            View retView;
            if (convertView == null) {
            	retView = View.inflate(mContext, R.layout.add_shortcut_item, null);
                holder = new ViewHolder();
                holder.mText = (TextView) retView.findViewById(R.id.app_text);
                holder.mIcon = (ImageView) retView.findViewById(R.id.app_icon);
                holder.mCheck = (CheckBox) retView.findViewById(R.id.app_selected);
                retView.setTag(holder);
            } else {
            	retView = convertView;
                holder = (ViewHolder) retView.getTag();                
            }
            holder.mIcon.setImageDrawable(mItems.get(position).icon);
            holder.mText.setText(mItems.get(position).label);
            holder.mCheck.setChecked(mItems.get(position).checked);
            return retView;
        }
    }

    private class ViewHolder {
        TextView mText;
        ImageView mIcon;
        CheckBox mCheck;
    }
    
    /*RK_GRID_VIEW  dining@lenovo.com 2012-10-16 S*/
    private class AppsGridAdapter extends BaseAdapter {

    	 private final List<Item> mItems;
         private Context mContext;
         
         /* RK_ID: RK_TOAST. AUT: liuli1 . DATE: 2012-10-25 . START */
         private Toast mToast;
         /* RK_ID: RK_TOAST. AUT: liuli1 . DATE: 2012-10-25 . END */
         /**
          * Create an adapter for the given items.
          */
         public AppsGridAdapter(Context context, List<Item> items) {
             mItems = items;
             mContext = context;
         }
        @Override
        public int getCount() {
            return mItems.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {  
        	
        	ViewHolder holder;
            View retView;
        	if (convertView == null) {
        		convertView = View.inflate(mContext, R.layout.app_to_category, null);
        	}
        	
        	final TextView label = (TextView) convertView.findViewById(R.id.category_label);
            final ImageView icon = (ImageView) convertView.findViewById(R.id.category_icon);
            final ImageView select_icon = (ImageView) convertView.findViewById(R.id.select_icon);
            
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
//                    if (isFolder) {
//                        emptynum = getResources().getInteger(R.integer.folder_max_num_items);
//                    }
                    checked = !mItems.get(position).checked;
                	if(vis == View.VISIBLE){
                		select_icon.setVisibility(View.INVISIBLE);
                 	}else{
                 		select_icon.setVisibility(View.VISIBLE);
                 	}
                	if(isFolder){
                        mItems.get(position).checked = checked;
                        if (checked) {
                            selectednum++;
                            mIntentListInfo.add(items.get(position).getIntent().toUri(0));
                        } else {
                            selectednum--;
                            mIntentListInfo.remove(items.get(position).getIntent().toUri(0));
                        }
                        mDialogTitle.setText(mTitle+ "(" +String.valueOf(selectednum) +")");
                	}else{
                        if (selectednum < emptynum) {
                            
                            mItems.get(position).checked = checked;
                            if (checked) {
                                selectednum++;
                                mIntentListInfo.add(items.get(position).getIntent().toUri(0));
                            } else {
                                selectednum--;
                                mIntentListInfo.remove(items.get(position).getIntent().toUri(0));
                            }
                        }else{
                            if (!checked) {
                                mItems.get(position).checked = false;
                                selectednum--;
                                mIntentListInfo.remove(items.get(position).getIntent().toUri(0));
                            } else {
                                /* RK_ID: RK_TOAST. AUT: liuli1 . DATE: 2012-10-25 . START */
                                if (mToast == null) {
                                    mToast = Toast.makeText(mContext, R.string.add_shortcut_to_workspace_more, Toast.LENGTH_SHORT);
                                }
                                mToast.show();
//                                Toast.makeText(mContext, R.string.add_shortcut_to_workspace_more, Toast.LENGTH_SHORT).show();
                                /* RK_ID: RK_TOAST. AUT: liuli1 . DATE: 2012-10-25 . END */
                                if(select_icon.getVisibility() == View.VISIBLE){
                                    select_icon.setVisibility(View.INVISIBLE);
                                }
                            }
                        }
                        /*PK_ID:SHORTCUT NUM DISPLAY AUTH:GECN1 DATE:2012-10-29 S  */
//                        setTitle(mTitle+ "(" +String.valueOf(selectednum) +"/" + String.valueOf(emptynum)+")" );
                         mDialogTitle.setText(mTitle+ "(" +String.valueOf(selectednum) +"/" + String.valueOf(emptynum)+")" );
                        /*PK_ID:SHORTCUT NUM DISPLAY AUTH:GECN1 DATE:2012-10-29 E  */
                	}

                }
            });
            return convertView;
        }
        /* RK_ID: RK_TOAST. AUT: liuli1 . DATE: 2012-10-25 . START */
        protected void cancelToast() {
            if (mToast != null) {
                mToast.cancel();
            }
        }
        /* RK_ID: RK_TOAST. AUT: liuli1 . DATE: 2012-10-25 . END */
    }
    /*RK_GRID_VIEW  dining@lenovo.com 2012-10-16 E*/
}
