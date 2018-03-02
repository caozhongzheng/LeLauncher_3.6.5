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

package com.lenovo.launcher2.addleoswidget;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.gadgets.GadgetUtilities;
import com.lenovo.launcher2.weather.widget.settings.FetchLenovoWeatherWidgetUtil;
import com.lenovo.launcher2.weather.widget.utils.WeatherUtilites;
/* RK_ID: RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-09-26START */
public class AddLeosWidgetActivity extends Activity implements AdapterView.OnItemClickListener {
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
        
        Window window = getWindow();
		window.setGravity(Gravity.CENTER);
		//tesy by dining
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
        View shareDialog = this.findViewById(R.id.share_dialog);
		shareDialog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});
		//set the window width 
        WindowManager.LayoutParams params = window.getAttributes();
        
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        
        int widthPersent = resources.getInteger(R.integer.dialog_width_major);
        
        params.width = (int) (dm.widthPixels*(widthPersent/100.0f));
        window.setAttributes(params);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    	LenovoWidgetsProviderInfo item = (LenovoWidgetsProviderInfo) mAdapter.getItem(position);
    	if(item.isInstalled){
    		Log.d("gecn1",item.widgetView );
    		if(item.widgetView.equals(GadgetUtilities.WEATHERMAGICWIDGETVIEWHELPER)){
    			Intent i = new Intent(WeatherUtilites.ACTION_ADD_LENOVO_WEATHER_WIDGET_ACTIVITY);
            	i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			try{
    				startActivity(i);
    			}catch(Exception e){
    				e.printStackTrace();
    			}
    			finish();
    			return;
    		}
    		Intent intent = new Intent(WeatherUtilites.ACTION_ADD_LENOVOWIDGET);
    		intent.putExtra("EXTRA_PACKAGENAME", item.appPackageName);
    		Log.d("gecn1",item.appPackageName );
    		intent.putExtra("EXTRA_CALSS", item.widgetView);
    		intent.putExtra("EXTRA_WIDTH", item.x);
    		intent.putExtra("EXTRA_HIEGHT", item.y);
    		this.sendBroadcast(intent );
    	}else{
    		Log.e("THEME","================================onItemClick");
    		//zhangdxa modify for remove magicdownload
    		/*MagicDownloadControl.downloadFromCommon (this, 
    				"com.google.android.voicesearch",  
    				"214", 
    				HwConstant.CATEGORY_LELAUNCHER_SERVER_APK+"", 
    				getString(R.string.audio_search), 
    				null, 
    				"http://launcher.lenovo.com/launcher/data/attachment/app/voiceSearch.apk", 
    				HwConstant.MIMETYPE_APK,
    				"true", 
    				"true");
    		setResult(Activity.RESULT_CANCELED,null);*/
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
        ArrayList<LenovoWidgetsProviderInfo> mInstalledLeosWidgets;
        
        /**
         * Specific item in our list.
         */
        
        public LeosWidgetAdapter(Context context) {
            super();

            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

//            Resources res = context.getResources();
            mInstalledLeosWidgets = new FetchLenovoWidgetUtil(context).getAllLeosWidgets();
        }

        public View getView(int position, View convertView, ViewGroup parent) {
        	LenovoWidgetsProviderInfo item = (LenovoWidgetsProviderInfo) getItem(position);

            View retView = null;
            if (convertView == null) {
            	retView = mInflater.inflate(R.layout.add_leos_widgets_item, parent, false);
            } else {
            	retView = convertView;
            }
            
           
            ImageView icon = (ImageView)retView.findViewById(R.id.category_icon);
            icon.setImageDrawable(item.icon);
            TextView textView = (TextView) retView.findViewById(R.id.category_label);
            textView.setText(item.appName+" "+item.x+"X"+item.y);
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
    /* RK_ID: RK_LEOSWIDGET AUT:liuyg1@lenovo.com DATE: 2012-09-26 END */
}
