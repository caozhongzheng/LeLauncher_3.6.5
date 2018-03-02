package com.lenovo.lejingpin.hw.content.data;

import java.util.ArrayList;
import java.util.List;

import com.lenovo.lejingpin.hw.content.util.ContentManagerLog;


public class UpgradeAppList {
	private String TAG = "UpgradeAppList";
	
	private List<UpgradeApp> mList;

	public UpgradeAppList(){
		if(mList==null){
			mList = new ArrayList<UpgradeApp>();
		}
	}
	
	public void add(UpgradeApp app){
		if(mList!=null){
			mList.add(app);
		}else{
			ContentManagerLog.d(TAG, "UpgradeAppList list is null");
		}
	}
	
	public UpgradeApp getApp(int index){
		if(mList!=null && index >= 0){
			int size = mList.size();
			if(size > index){
				return mList.get(index);
			}
		}else{
			ContentManagerLog.d(TAG, "UpgradeAppList list is null or size is 0");
		}
		return null;
	}
	
	public List<UpgradeApp> getAppStoreList(){
		return mList;
	}
	
	public int getSize(){
		if(mList!=null){
			return mList.size();
		}else{
			return 0;
		}
	}
	
	public boolean isEmpty(){
		if(mList!=null && mList.size() > 0){
			return false;	
		}else{
			return true;
		}
	}
	
	public boolean isContain(UpgradeApp app){
		if(!isEmpty() && mList.contains(app)){
			return true;
		}else{
			return false;
		}
	}
	
}
