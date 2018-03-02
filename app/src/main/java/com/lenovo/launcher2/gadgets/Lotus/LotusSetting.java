package com.lenovo.launcher2.gadgets.Lotus;

import java.util.ArrayList;
import java.util.List;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;
import com.lenovo.launcher.components.XAllAppFace.XLauncher;
import com.lenovo.launcher2.commoninterface.ApplicationInfo;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.Utilities;
import com.lenovo.launcher2.gadgets.Lotus.ShortcutActivityPicker.PickAdapter;
import com.lenovo.launcher2.gadgets.Lotus.ShortcutActivityPicker.PickAdapter.Item;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LotusSetting extends ListActivity implements AdapterView.OnItemClickListener{
	/**
	 * @see android.app.Activity#onCreate(Bundle)
	 */
	//private 
	private LeafAdapter mAdapter;
	private boolean hasFilled = false;
	private Context mContext;
	 /** 四叶草信息SharedPreferences */
    private SharedPreferences mPreferences;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO 将代码放在此处
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setup_pererence_layout);
		TextView title = (TextView)findViewById(R.id.dialog_title);
		title.setText(R.string.gadget_name_lotus);
		ImageView icon = (ImageView)findViewById(R.id.dialog_icon);
		icon.setImageResource(R.drawable.dialog_title_back_arrow);
		title.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		icon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mContext = this;
		mPreferences = mContext.getSharedPreferences(LotusUtilites.LOTUSINFO, Activity.MODE_APPEND | Activity.MODE_MULTI_PROCESS);
		if(!SettingsValue.isRotationEnabled(this)){
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}
	
    @Override
    protected void onResume(){
    	super.onResume();
    	List<LeafEntry> items = getItems();
		mAdapter = new LeafAdapter(this, items);
		getListView().setAdapter(mAdapter);
		getListView().setOnItemClickListener(this);
    }
	private List<LeafEntry> getItems() {
		// TODO Auto-generated method stub
		initLotusUtilitesRomData();
		List<LeafEntry> mLeafEntrys = new ArrayList<LeafEntry>();
	//	mLeafEntrys.add(new LeafEntry(this, packageManager, info, true));
		//LeafEntry mLeafEntry = new LeafEntry();
		for(int i = 0 ; i < LotusUtilites.TOTAL_LEAF ; i++){
			Intent data = LotusUtilites.getLotusPageInfo(mPreferences,i, false);
			String appName = getLeafAppNameByIndex(i,data);
			int iconId = getLeafIconByIndex(i);
			mLeafEntrys.add(new LeafEntry(i, data, appName,iconId));
		}
		return mLeafEntrys;
	}

	private void initLotusUtilitesRomData(){
        if (!hasFilled ) {
            LotusUtilites.fillLeafMapInfo(mContext, mPreferences);
            LotusUtilites.getIcon(mContext);
            LotusUtilites.getString(mContext, mPreferences);
            LotusUtilites.getIntentStr(mPreferences);
            hasFilled = true;
        }
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		LotusUtilites.setlongClickIndex(position);
		if (!LotusUtilites.sfPrefKeyMapEnableChange[position]) {
            //do nothing
        } else {
            addLotusPageInfo(LotusUtilites.longClickIndex);
        }
	}
	private void addLotusPageInfo(int pageNum) {
		Intent intent = new Intent(LotusUtilites.ACTION_PICK_SHORTCUT);
		intent.setPackage(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF);
        //bugfix 12633
		ArrayList<String> addedPackageAndClassName = getLotusPagePackageAndClassName(pageNum);
		intent.putStringArrayListExtra(LotusUtilites.EXTRA_APPCLASSNAME, addedPackageAndClassName);
		intent.putExtra("PAGENUM", pageNum);
		mContext.startActivity(intent);
	}

	/** 取当前被选中的所有应用，传入要更改的叶子序号 */
	private ArrayList<String> getLotusPagePackageAndClassName(int pageNum) {
		ArrayList<String> packageAndClassNameList = new ArrayList<String>();
		for (int i = 0; i < LotusUtilites.TOTAL_LEAF; i++) {
			Intent data = LotusUtilites.getLotusPageInfo(mPreferences,i, false);
			if (data != null && data.getComponent() != null) {
				String packageAndClassName = data.getComponent().getPackageName()+data.getComponent().getClassName();
				packageAndClassNameList.add(packageAndClassName);
			}else {
			}
		}
		return packageAndClassNameList;
	}
	private String getLeafNameByIndex(int leafIndex) {
		return mContext.getString(LEAF_NAME[leafIndex]);
	}
	private int getLeafIconByIndex(int leafIndex) {
		return LEAF_ICON[leafIndex];
	}
	
	private String getLeafAppNameByIndex(int leafIndex, Intent data) {
		PackageManager packagemanager = mContext.getPackageManager();
		if (data != null) {
           // LotusUtilites.getIntentStr(mPreferences);
          //  LotusUtilites.getString(mContext, mPreferences); 

            ResolveInfo resolveInfo = packagemanager.resolveActivity(data, 0);
            String checkName = data.getComponent().getClassName();

            //系统有这个应用的情况
            if (resolveInfo != null) {
                //取应用标题
    		    CharSequence title = null;
    			title = resolveInfo.loadLabel(packagemanager);
    			if (title == null) {
    				title = resolveInfo.activityInfo.name;
    			}
    
    			if (checkName != null && checkName.length() > 0) {
    			    boolean isSolid = false;
    			    for(int i = 0; i < LotusUtilites.TOTAL_LEAF; i++){
    			        //如果是默认应用
    			        if(LotusUtilites.checkIntentSolid(i, checkName)){
    			            //先取默认应用名
                            String app_name_first = LotusUtilites.app_name_first[i];
                            //再取应用名
                            if(app_name_first == null || app_name_first.length() == 0){
                                app_name_first = (String)title;
                            }
                            //最后取缺省名
                           /* if(app_name_first == null || app_name_first.length() == 0){
                                app_name_first = mContext.getResources().getString(R.string.lotus_default);
                            }*/
                            isSolid = true;
                            return app_name_first;
    			        }
    			    }

    			    //非默认应用，直接取应用名
    			    if (!isSolid) {
    				    CharSequence name = resolveInfo.loadLabel(packagemanager);
    					if (name == null) {
    						name = resolveInfo.activityInfo.name;
    					}
    					return name.toString();
    				}
    			}
    		}
            //是默认应用，但是系统没有这个应用或是fake应用的情况
            else if (LotusUtilites.findIntentSolid(checkName) >= 0) {
    			for(int i = 0; i < LotusUtilites.TOTAL_LEAF; i++){
    			    if(LotusUtilites.checkIntentSolid(i, checkName)){
    			        String s = LotusUtilites.app_name_first[i];
						if (s != null) {
							return s;
						} else {
							return null;
						}
    			    }
    			}
    		}
            //系统中没有这个应用，也不是默认应用的情况。
            else {
                if(LotusUtilites.checkIntentSolid(leafIndex, checkName)){
                    String s = LotusUtilites.app_name_first[leafIndex];

					if (s != null) {
						return s;
					} else {
						return null;
					}
				}
    		}
		}
		return null;
	} 
	private final static int[] LEAF_NAME= new int[]{R.string.lotus_setting_lt,R.string.lotus_setting_rt,R.string.lotus_setting_lb,R.string.lotus_setting_rb};
	private final static int[] LEAF_ICON= new int[]{R.drawable.lotus_setting_lt_selector,R.drawable.lotus_setting_rt_selector,R.drawable.lotus_setting_lb_selector,R.drawable.lotus_setting_rb_selector};

	private class LeafEntry {
		String leafName;
		Intent intent;
		String appName;
		int iconId;
		LeafEntry(int leafIndex, Intent intent, String appName,int iconId) {
			this.leafName = getLeafNameByIndex(leafIndex);
			this.intent = intent;
			this.appName = appName;
			this.iconId = iconId;
		}

		Intent getIntent() {
			if (this.intent != null) {
				return this.intent;
			} else {
				return null;
			}
		}
	}
	private static  class LeafAdapter extends BaseAdapter {

		private final LayoutInflater mInflater;
		private final List<LeafEntry> mItems;

		public LeafAdapter(Context context, List<LeafEntry> items) {
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mItems = items;
		}
		public int getCount() {
			return mItems.size();
		}

		public Object getItem(int position) {
			return mItems.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.custom_preference, parent, false);
			}

			LeafEntry item = (LeafEntry) getItem(position);
			ImageView icon = (ImageView) convertView.findViewById(com.android.internal.R.id.icon);
			TextView title = (TextView) convertView.findViewById(com.android.internal.R.id.title);
			
			TextView summary = (TextView) convertView.findViewById(com.android.internal.R.id.summary);
			ImageView more = (ImageView) convertView.findViewById(R.id.more);
			if (!LotusUtilites.sfPrefKeyMapEnableChange[position]) {
				convertView.setClickable(false);
				convertView.setSelected(false);
				title.setTextColor(summary.getTextColors());
			} else if (more != null) {
				more.setVisibility(View.VISIBLE);
			}

			if(icon !=null){
				icon.setImageResource(item.iconId);
			}
			if (title != null) {
				title.setText(item.leafName);
			}
			if (summary != null) {
				summary.setText(item.appName);
			}
			return convertView;
		}
	}
	
}
