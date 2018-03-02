package com.lenovo.launcher2.weather.widget.settings;

import com.lenovo.launcher.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class HotCityAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	private Context mcontext;
	private String[] mcitynames;
	private String[] mcityids;
	public HotCityAdapter(Context context) {
		mcontext = context;
		mcitynames = context.getResources().getStringArray(R.array.hot_citys);
	    mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
    public View getView(int position, View convertView, ViewGroup parent) {
    	TextView textview;
	    if (convertView == null) 
	    	textview = (TextView)mInflater.inflate(R.layout.weather_hot_citys_item, null);
	    else
	    	textview = (TextView)convertView;
	    textview.setText(mcitynames[position]);
        return textview;
    }

	public int getCount() {
		// TODO Auto-generated method stub
		return mcitynames.length;
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		 return mcitynames[position];
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		 return position;
	}


}
