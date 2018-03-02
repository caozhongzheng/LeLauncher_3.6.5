/*
 * Copyright (C) 2009 The Android Open Source Project
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
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView;

import android.app.ListActivity;

import android.widget.AdapterView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;
import com.lenovo.launcher2.customizer.SettingsValue;

/**
 * Displays a list of all activities matching the incoming
 * {@link Intent#EXTRA_INTENT} query, along with any injected items.
 */
public class ShortcutActivityPicker extends ListActivity implements AdapterView.OnItemClickListener{
    
    private static String TAG = "ShortcutActivityPicker";
    
    /*
     * Adapter of items that are displayed in this dialog.
     */
    private PickAdapter mAdapter;
    
    /**
     * Base {@link Intent} used when building list.
     */
    private Intent mBaseIntent;
    
    public ArrayList<String> addedAppClassNameList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        final Intent intent = getIntent();
        
        // Read base intent from extras, otherwise assume default
        Parcelable parcel = intent.getParcelableExtra(Intent.EXTRA_INTENT);
        if (parcel instanceof Intent) {
            mBaseIntent = (Intent) parcel;
        } else {
            mBaseIntent = new Intent(Intent.ACTION_MAIN, null);
            mBaseIntent.addCategory(Intent.CATEGORY_DEFAULT);
        }
        mBaseIntent.putExtra("CELL_X", intent.getIntExtra("CELL_X", -1));
        mBaseIntent.putExtra("CELL_Y", intent.getIntExtra("CELL_Y", -1));
        
        addedAppClassNameList  = intent.getStringArrayListExtra("APPCLASSNAME");

        // Build list adapter of pickable items
        setContentView(R.layout.setup_pererence_layout);
		TextView title = (TextView)findViewById(R.id.dialog_title);
		title.setText(R.string.menu_addapp);
        
		//test by dining
		ImageView icon = (ImageView)findViewById(R.id.dialog_icon);
		icon.setImageResource(R.drawable.dialog_title_back_arrow);
		icon.setOnClickListener(new View.OnClickListener() {

		            @Override
		            public void onClick(View view) {
		                finish();
		            }
		});
		
		final ListView lv = (ListView)findViewById(android.R.id.list);
		List<PickAdapter.Item> items = getItems();
       
        mAdapter = new PickAdapter(this, items);
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(this);
    }
    
    
    
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        PickAdapter.Item item = (PickAdapter.Item) mAdapter.getItem(position);
        if(item.misadded) return;
        Intent intent = getIntentForPosition(position);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    /**
     * Handle canceled dialog by passing back {@link Activity#RESULT_CANCELED}.
     */
    public void onCancel(DialogInterface dialog) {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    /**
     * Build the specific {@link Intent} for a given list position. Convenience
     * method that calls through to {@link PickAdapter.Item#getIntent(Intent)}.
     */
    protected Intent getIntentForPosition(int position) {
        PickAdapter.Item item = (PickAdapter.Item) mAdapter.getItem(position);
        return item.getIntent(mBaseIntent);
    }

    /**
     * Build and return list of items to be shown in dialog. Default
     * implementation mixes activities matching {@link #mBaseIntent} from
     * {@link #putIntentItems(Intent, List)} with any injected items from
     * {@link Intent#EXTRA_SHORTCUT_NAME}. Override this method in subclasses to
     * change the items shown.
     */
    protected List<PickAdapter.Item> getItems() {
        PackageManager packageManager = getPackageManager();
        List<PickAdapter.Item> items = new ArrayList<PickAdapter.Item>();

        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> infolist = packageManager.queryIntentActivities(mainIntent, 0);

        Collections.sort(infolist, new ResolveInfo.DisplayNameComparator(packageManager));
        for(int j=0;j<infolist.size();j++){
            ResolveInfo info = infolist.get(j);
            
            if (info.activityInfo.packageName.startsWith(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF)
            	|| info.activityInfo.packageName.contains(SettingsValue.THEME_PACKAGE_NAME_PREF)
            	|| info.activityInfo.packageName.contains(SettingsValue.THEME_PACKAGE_QIGAMELOCKSCREEN_PREF)) {
            	continue;
            }
            //bugfix 12633
            PickAdapter.Item item = new PickAdapter.Item(this, packageManager, info, false);

			if (addedAppClassNameList != null) {
				String className = info.activityInfo.name;
				String packagename = info.activityInfo.packageName;
				String packageAndClassName = packagename+className;
                for(int i = 0 ; i <addedAppClassNameList.size();i++){
                	if (addedAppClassNameList.get(i).equals(packageAndClassName)) {
                		item.setAdded(true);
    				} 
                }
				items.add(item);

			} else {
				items.add(item);
			}
        }
        
        return items;
    }

    /**
     * Fill the given list with any activities matching the base {@link Intent}. 
     */
    protected void putIntentItems(Intent baseIntent, List<PickAdapter.Item> items) {
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(baseIntent,
                0 /* no flags */);
        Collections.sort(list, new ResolveInfo.DisplayNameComparator(packageManager));
        items.clear();
        
        final int listSize = list.size();
        for (int i = 0; i < listSize; i++) {
            ResolveInfo resolveInfo = list.get(i);
            if(addedAppClassNameList != null){
                String className = resolveInfo.activityInfo.name;
                if(addedAppClassNameList.contains(className)){
                    items.add(new PickAdapter.Item(this, packageManager, resolveInfo,true));
                }else{
                    items.add(new PickAdapter.Item(this, packageManager, resolveInfo,false));
                }
            }else{
                items.add(new PickAdapter.Item(this, packageManager, resolveInfo,false));
            }
        }
    }
    
    /**
     * Adapter which shows the set of activities that can be performed for a
     * given {@link Intent}.
     */
    protected static class PickAdapter extends BaseAdapter {
        
        /**
         * Item that appears in a {@link PickAdapter} list.
         */
        public static class Item {
            //protected static IconResizer sResizer;
            boolean misadded;
            Bundle extras;
            ApplicationInfo appInfo;

            /**
             * Create a list item and fill it with details from the given
             * {@link ResolveInfo} object.
             */
            Item(Context context, PackageManager pm, ResolveInfo resolveInfo, boolean added) {
                misadded = added;
                LauncherApplication app = (LauncherApplication) context.getApplicationContext();
                appInfo = new ApplicationInfo(context.getPackageManager(), resolveInfo, app.getIconCache(), null);
            }

            public void setAdded(boolean b) {
				// TODO Auto-generated method stub
            	misadded = b;
			}

			/**
             * Build the {@link Intent} described by this item. If this item
             * can't create a valid {@link android.content.ComponentName}, it will return
             * {@link Intent#ACTION_CREATE_SHORTCUT} filled with the item label.
             */
            Intent getIntent(Intent baseIntent) {
                Intent intent = new Intent(baseIntent);
                intent.setComponent(appInfo.componentName);
                intent.setAction(Intent.ACTION_MAIN);
                return intent;
            }
        }
        
        private final LayoutInflater mInflater;
        private final List<Item> mItems;
        
        /**
         * Create an adapter for the given items.
         */
        public PickAdapter(Context context, List<Item> items) {
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mItems = items;
        }

        /**
         * {@inheritDoc}
         */
        public int getCount() {
            return mItems.size();
        }

        /**
         * {@inheritDoc}
         */
        public Object getItem(int position) {
            return mItems.get(position);
        }

        /**
         * {@inheritDoc}
         */
        public long getItemId(int position) {
            return position;
        }

        /**
         * {@inheritDoc}
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.pick_item, parent, false);
            }
            
            Item item = (Item) getItem(position);
            
            TextView textView = (TextView) convertView.findViewById(R.id.app_textview);            
            if(textView != null){
	            textView.setText(item.appInfo.title);
            }
            
            ImageView appIcon = (ImageView) convertView.findViewById(R.id.app_icon);
            if (appIcon != null) {
	            BitmapDrawable bitmapDrawable = 
	            		new BitmapDrawable(item.appInfo.iconBitmap);
	            appIcon.setImageDrawable(bitmapDrawable);
            }
            ImageView image = (ImageView) convertView.findViewById(R.id.add_icon);
            if(item.misadded){
                image.setVisibility(View.VISIBLE);
                image.setSelected(true);
            }else{
                image.setVisibility(View.GONE);
            }
            
            return convertView;
        }
    }
    /***RK_ID:RK_BUGFIX_172254   AUT:zhanglz1@lenovo.com.DATE:2012-11-29. S***/        
	@Override
	public void onStop() {
	     super.onStop();
	     setResult(Activity.RESULT_CANCELED);
	}
	/***RK_ID:RK_BUGFIX_172254   AUT:zhanglz1@lenovo.com.DATE:2012-11-29. E***/        
		
}
