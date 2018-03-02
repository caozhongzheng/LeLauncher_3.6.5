package com.lenovo.launcher2.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;
import com.lenovo.launcher2.commoninterface.ApplicationInfo;
import com.lenovo.launcher2.commoninterface.HiddenApplist;
import com.lenovo.launcher2.commonui.ShortcutGridView;
import com.lenovo.launcher2.customizer.SettingsValue;

public class HideAppsSettings extends Activity implements AdapterView.OnItemClickListener {

    private static final boolean DEBUG = true;
    private static final String TAG = "HideAppsSettings";
    //TextView mSelectLabel;
    Button mCancel, mAddFinish, mSelectAll, mSelectClear;
 /* RK_ID: RK_MENU_RFACTOR . AUT: LIUYG1 . DATE: 2012-12-11 .S */
    ShortcutGridView mShortcutGrid;
 /* RK_ID: RK_MENU_RFACTOR . AUT: LIUYG1 . DATE: 2012-12-11 .E */
    List<Item> items = new ArrayList<Item>();
    private BaseAdapter mAdapter;
    //private ViewHolder mHolder;
    private ArrayList<String> submit = new ArrayList<String>();

    OnClickListener mButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
        	if (v.equals(mAddFinish)) {//if (v == mAddFinish) {
                String[] res = new String[submit.size()];
                submit.toArray(res);
                Intent intent = new Intent(HiddenApplist.ACTION_SET_APP_HIDDEN);
                intent.putExtra(HiddenApplist.KEY_HIDDENLIST_DATE, res);
                sendBroadcast(intent);
                finish();
        	} else if (v.equals(mCancel)) {//} else if (v == mCancel) {
                setResult(Activity.RESULT_CANCELED);
                finish();
        	} else if (v.equals(mSelectAll)) {//} else if (v == mSelectAll) {
                int size = items.size();
                submit.clear();
                for (int i = 0; i < size; i++) {
                    Item item = items.get(i);
                    item.checked = true;
                    if (item.componentName != null && !item.componentName.equals("")) {
                        submit.add(item.componentName);
                    }
                }
                mAdapter.notifyDataSetChanged();
        	} else if (v.equals(mSelectClear)) {//} else if (v == mSelectClear) {
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
 /* RK_ID: RK_MENU_RFACTOR . AUT: LIUYG1 . DATE: 2012-12-11 .S */
//        setTitle(R.string.applist_hiddenlist_settings_title);
 /* RK_ID: RK_MENU_RFACTOR . AUT: LIUYG1 . DATE: 2012-12-11 .E */

        setContentView(R.layout.apps_hide_settings);

        //mSelectLabel = (TextView) findViewById(R.id.selectlabel);
 /* RK_ID: RK_MENU_RFACTOR . AUT: LIUYG1 . DATE: 2012-12-11 .S */
        TextView title = (TextView) findViewById(R.id.dialog_title);
        title.setText(R.string.applist_hiddenlist_settings_title);
 /* RK_ID: RK_MENU_RFACTOR . AUT: LIUYG1 . DATE: 2012-12-11 .E */
        mCancel = (Button) findViewById(R.id.canceladd);
        mAddFinish = (Button) findViewById(R.id.addfinish);
        mSelectAll = (Button) findViewById(R.id.selectAll);
        mSelectClear = (Button) findViewById(R.id.clear);
        mShortcutGrid = (ShortcutGridView) findViewById(R.id.applist);

        mCancel.setText(getString(android.R.string.cancel));
        mCancel.setOnClickListener(mButtonListener);
        mAddFinish.setText(getString(android.R.string.ok));
        mAddFinish.setOnClickListener(mButtonListener);
        mSelectAll.setOnClickListener(mButtonListener);
        mSelectClear.setOnClickListener(mButtonListener);
        //mSelectLabel.setText(getString(R.string.selectedcellnum) + submit.size());
//        mShortcutGrid.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mAdapter = new EfficientAdapter(this, getItems());
        mShortcutGrid.setAdapter(mAdapter);
        mShortcutGrid.setOnItemClickListener(this);
        
        this.setFinishOnTouchOutside(true);
    }

    /* RK_ACTIVITY_MANAGER. AUT: liuli1 . DATE: 2012-11-07 . START */
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mReceiver, filter);

        mAttached = true;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (mAttached) {
            unregisterReceiver(mReceiver);
            mAttached = false;
        }
    }

    private boolean mAttached = false;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                // this activity must be finished when screen off.
                finish();
            }
        }
    };

    /* RK_ID: RK_ACTIVITY_MANAGER. AUT: liuli1 . DATE: 2012-11-07 . END */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//    	Object obj = view.getTag();
//        if (!(obj instanceof ViewHolder)) {
//            return;
//        }
//        mHolder = (ViewHolder) obj;
//        mHolder.mCheck.toggle();
//        boolean checked = mHolder.mCheck.isChecked();
//        items.get(position).checked = checked;
//        String temp;
//        if (checked) {
//            temp = items.get(position).componentName;
//            if (temp != null && !temp.equals("")) {
//                submit.add(items.get(position).componentName);
//            }
//        } else {
//            temp = items.get(position).componentName;
//            submit.remove(temp);
//        }
//        String[] res = new String[submit.size()];
//        submit.toArray(res);
//        for (int i = 0; i < res.length; i++) {
//            echo("submit " + i + " : " + res[i]);
//        }
//        //mSelectLabel.setText(getString(R.string.selectedcellnum) + submit.size());
    }

    private class ViewHolder {
        TextView mText;
        ImageView mIcon;
        CheckBox mCheck;
    }

    public List<Item> getItems() {
        PackageManager packageManager = getPackageManager();
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        items.clear();
        final List<ResolveInfo> infolist = packageManager.queryIntentActivities(mainIntent, 0);

        Collections.sort(infolist, new ResolveInfo.DisplayNameComparator(packageManager));
        // ActivityInfo ai = null;
        // boolean checked =false;

        for (int i = 0; i < infolist.size(); i++) {
            ResolveInfo info = infolist.get(i);
            if (info.activityInfo.packageName.startsWith(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF)
            	|| info.activityInfo.packageName.contains(SettingsValue.THEME_PACKAGE_NAME_PREF)) {
            	continue;
            }
            items.add(new Item(this, packageManager, info, false));
        }

        /*** RK_ID: SONAR.  AUT: zhaoxy . DATE: 2012-09-05 . START***/
        //Set<String> hiddenList = HiddenApplist.getHiddenList(this);
        Set<String> hiddenList = ((LauncherApplication) getApplication()).getModel().getHiddenApplist().getHiddenList(this);
        /*** RK_ID: SONAR.  AUT: zhaoxy . DATE: 2012-09-05 . START***/
        int size = items.size();
        for (int i = 0; i < size; i++) {
            Item item = items.get(i);
            if (hiddenList.contains(item.componentName)) {
                item.checked = true;
                submit.add(item.componentName);
            }
        }
        return items;
    }

    public static class Item {

        CharSequence label;
        Drawable icon;
        String componentName;
        Boolean checked;

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
            String packageName = resolveInfo.activityInfo.applicationInfo.packageName;
            String className = resolveInfo.activityInfo.name;
            if (packageName != null && className != null) {
                componentName = new ComponentName(resolveInfo.activityInfo.applicationInfo.packageName, resolveInfo.activityInfo.name).flattenToShortString();
            }

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
            // A ViewHolder keeps references to children views to avoid
            // unneccessary calls
            // to findViewById() on each row.

            // When convertView is not null, we can reuse it directly, there is
            // no need
            // to reinflate it. We only inflate a new View when the convertView
            // supplied
            // by ListView is null.
 /* RK_ID: RK_MENU_RFACTOR . AUT: LIUYG1 . DATE: 2012-12-11 .S */
   /*         ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.add_shortcut_item, null);
                holder = new ViewHolder();
                holder.mText = (TextView) convertView.findViewById(R.id.app_text);
                holder.mIcon = (ImageView) convertView.findViewById(R.id.app_icon);
                holder.mCheck = (CheckBox) convertView.findViewById(R.id.app_selected);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.mIcon.setImageDrawable(mItems.get(position).icon);
            holder.mText.setText(mItems.get(position).label);
            holder.mCheck.setChecked(mItems.get(position).checked);
            return convertView;*/
        	
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
                    checked = !mItems.get(position).checked;
                	if(vis == View.VISIBLE){
                		select_icon.setVisibility(View.INVISIBLE);
                 	}else{
                 		select_icon.setVisibility(View.VISIBLE);
                 	}
                	
                	mItems.get(position).checked = checked;
                    String temp;
                    if (checked) {
                        temp = mItems.get(position).componentName;
                        if (temp != null && !temp.equals("")) {
                            submit.add(mItems.get(position).componentName);
                        }
                    } else {
                        temp = mItems.get(position).componentName;
                        submit.remove(temp);
                    }
                }
            });
             /* RK_ID: RK_MENU_RFACTOR . AUT: LIUYG1 . DATE: 2012-12-11 .E */
            return convertView;
        }
    }

    private void echo(String info) {
        if (DEBUG) Log.d(TAG, info);
    }

}
