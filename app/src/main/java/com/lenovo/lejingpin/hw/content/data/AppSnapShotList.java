package com.lenovo.lejingpin.hw.content.data;

import java.util.ArrayList;
import java.util.List;

import com.lenovo.lejingpin.hw.content.util.ContentManagerLog;


public class AppSnapShotList {
	private static final String TAG = "AppSnapShotList";
	
	private List<AppSnapshot> mList;

	public AppSnapShotList(){
		if(mList==null){
			mList = new ArrayList<AppSnapshot>();
		}
	}
	
	public void add(AppSnapshot app){
		if(mList!=null){
			mList.add(app);
		}else{
			ContentManagerLog.d(TAG, "AppSnapShotList list is null");
		}
	}
	
	public AppSnapshot getApp(int index){
		if(mList!=null && index >= 0){
			int size = mList.size();
			if(size > index){
				return mList.get(index);
			}
		}else{
			ContentManagerLog.d(TAG, "AppSnapShotList list is null or size is 0");
		}
		return null;
	}
	
	public List<AppSnapshot> getAppStoreList(){
		return mList;
	}
	
	public void addAppSnapshotList(ArrayList<AppSnapshot> l){
		if(mList==null){
			mList = new ArrayList<AppSnapshot>(); 
		}
		mList.addAll(l);
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
	
	public String getFirstSnapShotPathString(){
		if(!isEmpty()){
			List<AppSnapshot> l = getAppStoreList();
			AppSnapshot shot = l.get(0);
			return shot!=null? shot.getAppimgPath() : null;
		}else{
			return null;
		}
	}
}
