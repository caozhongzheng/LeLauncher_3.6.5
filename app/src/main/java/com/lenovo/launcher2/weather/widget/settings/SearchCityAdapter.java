package com.lenovo.launcher2.weather.widget.settings;

import java.util.ArrayList;
import java.util.List;

import com.lenovo.launcher.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class SearchCityAdapter extends BaseAdapter {
	
	 private LayoutInflater mInflater;
     private Context mcontext;
     private ArrayList<City> mcities;
     private int mwidth = 0;
     private int mheight = 0;
     public SearchCityAdapter(Context context,ArrayList<City> cities) {
             mcontext = context;
             mcities = cities;
             mwidth = context.getResources().getDisplayMetrics().widthPixels;
             mheight = context.getResources().getDisplayMetrics().heightPixels;
             mwidth = mwidth < mheight ? mwidth : mheight;
         mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
     }
 public View getView(int position, View convertView, ViewGroup parent) {
     TextView textview;
         if (convertView == null){
             textview = (TextView)mInflater.inflate(R.layout.weather_citys_item, null);
             if(mwidth>=1080)
            	 textview.setHeight(100);
             else if(mwidth<1080&&mwidth>=720)
            	 textview.setHeight(60);
             else 
            	 textview.setHeight(40);
         }
         else
             textview = (TextView)convertView;
         textview.setText(mcities.get(position).getcityname()+"--"+mcities.get(position).getcityprovince());
     return textview;
 }

     public int getCount() {
             // TODO Auto-generated method stub
             return mcities.size();
     }

     public Object getItem(int position) {
             // TODO Auto-generated method stub
              return mcities.get(position).getcityname();
     }

     public long getItemId(int position) {
             // TODO Auto-generated method stub
              return position;
     }

}
