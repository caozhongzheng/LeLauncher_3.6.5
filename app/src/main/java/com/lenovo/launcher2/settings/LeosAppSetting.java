package com.lenovo.launcher2.settings;

import java.util.ArrayList;

import com.lenovo.launcher.R;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.Utilities;

import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class LeosAppSetting extends ListActivity implements AdapterView.OnItemClickListener{
	private Context mContext;
	private AppSettingAdapter mAdapter;
	private ArrayList<LeosAppSettingInfo> mLeosAppSettingInfos;
	@Override
	protected void onCreate(Bundle icicle) {
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(icicle);
		mContext = this;
		//getActionBar().setTitle(R.string.leos_app_settings);
		setContentView(R.layout.setup_pererence_layout);
		TextView title = (TextView)findViewById(R.id.dialog_title);
		title.setText(R.string.leos_app_settings);
		ImageView icon = (ImageView)findViewById(R.id.dialog_icon);
		icon.setImageResource(R.drawable.dialog_title_back_arrow);
		
		//test by dining
		
		icon.setOnClickListener(new View.OnClickListener() {

		            @Override
		            public void onClick(View view) {
		                finish();
		            }
		});
				
		
		
		ListView lv = (ListView)findViewById(android.R.id.list);
		mLeosAppSettingInfos = LeosAppSettingUtilities.getAppSettingInfos();
		if (mLeosAppSettingInfos != null && mLeosAppSettingInfos.size() > 0) {
			mAdapter = new AppSettingAdapter(mContext, mLeosAppSettingInfos);
			mAdapter.notifyDataSetChanged();
			lv.setAdapter(mAdapter);
			lv.setOnItemClickListener(this);
		}
		if(!SettingsValue.isRotationEnabled(this)){
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		final Intent intent = getIntentForPosition(position);
		try {
	        mContext.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			// TODO: handle exception
			Toast.makeText(mContext, R.string.leos_app_not_found, Toast.LENGTH_SHORT).show();
			//LeosAppSettingUtilities.rmLeosAppSettingInfo(mAdapter.getItem(position),LeosAppSettingUtilities.LENOVO_SETTING_PLUGIN);
			LeosAppSettingUtilities.rmLeosAppSettingInfo(mAdapter.getItem(position),LeosAppSettingUtilities.LENOVO_SETTING_WIDGET);
            if(LeosAppSettingUtilities.getAppSettingInfos().size()<1){
            	Intent i = new Intent();
				i.setAction(SettingsValue.ACTION_LEOS_APP_SETTING_REFRESH);
				mContext.sendBroadcast(i);
            }
			mAdapter.notifyDataSetChanged();

		}
	}
	
	protected Intent getIntentForPosition(int position) {
		LeosAppSettingInfo item = (LeosAppSettingInfo) mAdapter.getItem(position);
		return item.app_setting_intent;
	}
	
	protected static  class AppSettingAdapter extends BaseAdapter {
		private  LayoutInflater mInflater;
		private  ArrayList<LeosAppSettingInfo> mAppSettingInfos;
		public AppSettingAdapter(Context mContext,
				ArrayList<LeosAppSettingInfo> items) {
			mAppSettingInfos = items;
			mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		public int getCount() {
			return mAppSettingInfos.size();
		}
		
		public LeosAppSettingInfo getItem(int position) {
			return mAppSettingInfos.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.custom_preference, parent, false);
			}

			LeosAppSettingInfo mAppSettingInfo = (LeosAppSettingInfo) getItem(position);
			TextView textView = (TextView) convertView.findViewById(android.R.id.title);
			if (textView != null) {
				textView.setText(mAppSettingInfo.app_name);
			}
			TextView textDescribView = (TextView) convertView.findViewById(android.R.id.summary);
			if(textDescribView !=null){
				if(mAppSettingInfo.app_describ!=null){
					textDescribView.setText(mAppSettingInfo.app_describ);
				}else{
					textDescribView.setVisibility(View.GONE);
				}
			}
			ImageView more = (ImageView) convertView
					.findViewById(R.id.more);
			more.setVisibility(View.VISIBLE);
			ImageView imageadd = (ImageView) convertView.findViewById(android.R.id.icon);
			if (imageadd != null) {
				imageadd.setImageDrawable(mAppSettingInfo.app_icon);
				imageadd.setVisibility(View.GONE);
			}
			return convertView;
		}
	}
	/*@Override
	protected void onDestroy() {
		super.onDestroy();

		//unregisterReceiver(mIconStyleReceiver);
		mAppSettingInfos = null;
		System.gc();
	}
*/
}
