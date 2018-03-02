package com.lenovo.lejingpin.hw.content.data;

import java.util.ArrayList;
import java.util.List;

import com.lenovo.lejingpin.hw.content.util.ContentManagerLog;

import android.util.Log;

public class RecommendsAppList{
	private static final String TAG = "RecommendsAppList";

	private List<ReCommendsApp> mList;
	
	private List<ReCommendsApp> mNewAppList;
	
	private int newAppSize;
	
	public RecommendsAppList(){
		if(mList==null){
			mList = new ArrayList<ReCommendsApp>();
		}
		if(mNewAppList==null){
			mNewAppList  = new ArrayList<ReCommendsApp>(); 
		}
	}
	
	public void add(ReCommendsApp app){
		if(mList!=null){
			mList.add(app);
		}else{
			ContentManagerLog.d(TAG, "RecommendsAppList list is null");
		}
	}
	
	public void addNewApp(ReCommendsApp app){
		if(mNewAppList!=null){
			mNewAppList.add(app);
		}else{
			ContentManagerLog.d(TAG, "RecommendsAppList list is null");
		}
	}
	public void addRecommendsAppList(RecommendsAppList applist){
		if(applist!=null && !applist.isEmpty()){
			mList.addAll(applist.getAppStoreList());
		}
	}
	public void addNewRecommendsAppList(RecommendsAppList applist){
		if(applist!=null && !applist.isEmpty()){
			mNewAppList.addAll(applist.getAppStoreList());
		}
	}
	private void addRecommends(List<ReCommendsApp> list){
		if(list==null || list.isEmpty()){
			list = new ArrayList<ReCommendsApp>();
		}
		mList.addAll(list);
	}
	
	public int indexOf(ReCommendsApp app){
		if(mList!=null && !mList.isEmpty()){
			return mList.indexOf(app);	
		}else{
			return -1;
		}
	}
	
	public boolean isNewContain(ReCommendsApp app){
		if(!isNewEmpty() && app!=null && mNewAppList.contains(app)){
			return true;
		}else{
			return false;
		}
	}
	
	
	public ReCommendsApp getApp(int index){
		if(mList!=null && index >= 0){
			int size = mList.size();
			if(size > index){
				return mList.get(index);
			}
		}else{
			ContentManagerLog.d(TAG, "RecommendsAppList list is null or size is 0");
		}
		return null;
	}
	
	public List<ReCommendsApp> getAppStoreList(){
		return mList;
	}
	
	public List<ReCommendsApp> getNewAppStoreList(){
		return mNewAppList;
	}
	
	public int getSize(){
		if(mList!=null){
			return mList.size();
		}else{
			return 0;
		}
	}
	
	public boolean isEmpty(){
		if(mList!=null && !mList.isEmpty()){
			return false;	
		}else{
			return true;
		}
	}
	
	public boolean isNewEmpty(){
		if(mNewAppList!=null && !mNewAppList.isEmpty()){
			return false;	
		}else{
			return true;
		}
	}
	
	public boolean removeApp(ReCommendsApp app){
		if(!isEmpty() && mList.contains(app)){
			return mList.remove(app);
		}else{
			return false;
		}
	}
	public boolean removeNewApp(ReCommendsApp app){
		if(!isNewEmpty() && mNewAppList.contains(app)){
			return mNewAppList.remove(app);
		}else{
			return false;
		}
	}
	public boolean isContain(ReCommendsApp app){
		if(!isEmpty() && mList.contains(app)){
			return true;
		}else{
			return false;
		}
	}
	
	public int getNewAppSize() {
		return newAppSize;
	}

	public void setNewAppSize(int newAppSize) {
		this.newAppSize = newAppSize;
	}
	
	public RecommendsAppList displayAppStoreList(int startIndex,int count){
		if(count==-1){
			count = HwConstant.COUNT_DISPLAY_APP_LIST;
		}
		RecommendsAppList appLists = new RecommendsAppList();
		ArrayList<ReCommendsApp> list = new ArrayList<ReCommendsApp>();
		if(mList!=null && !mList.isEmpty()){
			int index = mList.size()-1;
			startIndex = Math.max(0, startIndex>=index?index:startIndex);
			int endIndex = Math.max(startIndex, startIndex+count>=index?index:startIndex+count);
			Log.d(TAG, "displayAppStoreList>>>>>>>>>>>>>>>startIndex="+startIndex+", endIndex="+endIndex);
			list.addAll(mList.subList(startIndex, endIndex));
			/*int size = mList.size();
			if(startIndex >= size){
				if(count > size){
					list.addAll(mList);
				}else{
					list.addAll(mList.subList(0, count - 1));
				}
			}else if(startIndex < size){
				if(count > size){
					list.addAll(mList);
				}else if(startIndex + count > size){
					list.addAll(mList.subList(startIndex, size-1));
				}else if(startIndex + count <= size){
					list.addAll(mList.subList(startIndex, count-1));
				}
			}*/
		}
		appLists.addRecommends(list);
		return appLists;
	}
	
	public RecommendsAppList subRecommendsAppList(int startIndex,int endIndex){
		if(mList!=null && !mList.isEmpty()){
			RecommendsAppList newInstance = new RecommendsAppList();
			newInstance.addRecommends(mList.subList(startIndex, endIndex));
			return newInstance;
		}else{
			return null;
		}
	}
	
	public RecommendsAppList storeAppStoreList(){
		if(mList!=null && !mList.isEmpty()){
			int size = mList.size();
			if(size > HwConstant.COUNT_DB_APP_LIST){
				List<ReCommendsApp> l = new ArrayList<ReCommendsApp>();
				l.addAll(mList.subList(0, HwConstant.COUNT_DB_APP_LIST -1));
				RecommendsAppList newInstance = new RecommendsAppList();
				newInstance.addRecommends(l);
				return newInstance;
			}else{
				return this;
			}
		}
		return this;
	}
	
	
}
