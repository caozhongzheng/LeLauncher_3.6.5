package com.lenovo.launcher2.weather.widget;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.weather.widget.settings.FetchLenovoExWidgetUtil;
import com.lenovo.launcher2.weather.widget.settings.LenovoExWidgetsProviderInfo;
import com.lenovo.launcher2.weather.widget.utils.WeatherUtilites;

public class AddLeosExWidgetActivity extends Activity implements AdapterView.OnItemClickListener {
    static final String TAG = "AddLeosWidgetActivity";

    GridView mLeosWidgetList;
    ArrayList<ListItem> mItems = new ArrayList<ListItem>();
    private BaseAdapter mAdapter;


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

       // setTitle(R.string.group_leos_widgets);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_leoswidget_grid);
        TextView dialogTitle=(TextView)findViewById(R.id.dialog_title);
        dialogTitle.setText(R.string.group_leos_widgets);
         /* RK_ID: RK_MENU_RFACTOR . AUT: LIUYG1 . DATE: 2012-12-11 .S */
        ImageView icon = (ImageView) findViewById(R.id.dialog_icon);
        icon.setBackgroundResource(R.drawable.leos_widgets);
         /* RK_ID: RK_MENU_RFACTOR . AUT: LIUYG1 . DATE: 2012-12-11 .E */
        mLeosWidgetList = (GridView) findViewById(R.id.applist);
        
        mAdapter = new LeosWidgetAdapter(this);
        mLeosWidgetList.setAdapter(mAdapter);
        mLeosWidgetList.setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    	final Intent intent = new Intent(WeatherUtilites.ACTION_WEATHER_WIDGET_CHANGE_EXWIDGET);
    	final LenovoExWidgetsProviderInfo item = (LenovoExWidgetsProviderInfo) mAdapter.getItem(position);
    	if(item.isInstalled){
    		new Thread(){
    			@Override
    			public void run() {
    				WeatherUtilites.saveExWidgetInfo(AddLeosExWidgetActivity.this, item.appPackageName, item.widgetView);
    	    		intent.putExtra(WeatherUtilites.EXTRA_WEATHER_EXWIDGET_PACKAGENAME, item.appPackageName);
    	    		intent.putExtra(WeatherUtilites.EXTRA_WEATHER_EXWIDGET_CLASSNAME, item.widgetView);
    	    		sendBroadcast(intent);
    			}
    		}.start();
    	}
    	finish();

    }

    public ArrayList<ListItem> getItems() {
        return mItems;
    }
    public class ListItem {
        public final CharSequence text;
        public final Drawable image;
        public final int actionTag;

        public ListItem(Resources res, int textResourceId, int imageResourceId, int actionTag) {
            text = res.getString(textResourceId);
            if (imageResourceId != -1) {
                image = res.getDrawable(imageResourceId);
            } else {
                image = null;
            }
            this.actionTag = actionTag;
        }
    }
    public class LeosWidgetAdapter extends BaseAdapter {

        private final LayoutInflater mInflater;

        public static final int ITEM_LOTUS = 0;
        public static final int ITEM_LOTUS_WEATHER = 1;
        public static final int ITEM_MAGIC_WEATHER = 2;
        public static final int ITEM_TOGGLE = 3;
        ArrayList<LenovoExWidgetsProviderInfo> mInstalledLeosWidgets;
        
        /**
         * Specific item in our list.
         */
        
        public LeosWidgetAdapter(Context context) {
            super();

            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

//            Resources res = context.getResources();
            mInstalledLeosWidgets = new FetchLenovoExWidgetUtil(context).getAllLeosWidgets();
//            if(mInstalledLeosWidgets!=null)
//            	Log.d("a","mInstalledLeosWidgets="+mInstalledLeosWidgets.size());
//            else
//            	Log.d("a","mInstalledLeosWidgets null");
        }

        public View getView(int position, View convertView, ViewGroup parent) {
        	LenovoExWidgetsProviderInfo item = (LenovoExWidgetsProviderInfo) getItem(position);

            View retView = null;
            if (convertView == null) {
            	retView = mInflater.inflate(R.layout.add_leos_widgets_item, parent, false);
            } else {
            	retView = convertView;
            }
            ImageView icon = (ImageView)retView.findViewById(R.id.category_icon);
            icon.setImageDrawable(item.icon);
            TextView textView = (TextView) retView.findViewById(R.id.category_label);
            textView.setText(item.appName);
            retView.setTag(item);
            return retView;
        }

        public int getCount() {
            return mInstalledLeosWidgets.size();
        }

        public Object getItem(int position) {
            return mInstalledLeosWidgets.get(position);
        }

        public long getItemId(int position) {
            return position;
        }
    }

}
