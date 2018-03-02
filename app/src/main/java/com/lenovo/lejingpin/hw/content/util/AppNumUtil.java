package com.lenovo.lejingpin.hw.content.util;

import android.content.Context;

import com.lenovo.lejingpin.hw.content.data.HwConstant;
import com.lenovo.lejingpin.hw.content.timetask.SharePreferenceUtil;

public class AppNumUtil {
	
	private String TAG = "AppNumUtil";
	private static AppNumUtil mInstance;
	private SharePreferenceUtil shareUtil;
	
	public static final int SPECIAL_TYPE = 1;
	public static final int FREE_TOP_TYPE = 2;
	public static final int SPERE_TYPE = 3;
	
	private AppNumUtil (){}
	private AppNumUtil(Context context){
		shareUtil = SharePreferenceUtil.getInstance(context);
	}
	
	public static AppNumUtil getInstance(Context context){
		if(mInstance==null){
			mInstance = new AppNumUtil(context);
		}
		return mInstance;
	}
	public int[] getIndex(int type){
		switch(type){
		case SPECIAL_TYPE:
			return getSpecialIndex();
		case FREE_TOP_TYPE:
			return getTopIndex();
		case SPERE_TYPE:
			return getSpereIndex();
		default:
			break;
		}
		return null;
	}
	
	private  int[] getSpecialIndex(){
		int n = shareUtil.getSpecailAppNum();
		int startIndex = n*HwConstant.REQUEST_SPECIAL_APP_NUM;
		int count = HwConstant.REQUEST_SPECIAL_APP_NUM;
		return new int[]{startIndex,count};
	}
	
	private int[] getTopIndex(){
		int n = shareUtil.getTopAppNum();
		int startIndex = n*HwConstant.REQUEST_TOP_APP_NUM;
		int count = HwConstant.REQUEST_TOP_APP_NUM;
		return new int[]{startIndex,count};
	}
	
	private int[] getSpereIndex(){
		int n = shareUtil.getSpereAppNum();
		int startIndex = n*HwConstant.REQUEST_SPERE_APP_NUM;
		int count = HwConstant.REQUEST_SPERE_APP_NUM;
		return new int[]{startIndex,count};
	}
	public void saveIndex(int size,int type){
		switch(type){
		case SPECIAL_TYPE:
			if(size < HwConstant.REQUEST_SPECIAL_APP_NUM){
				removeSpecialNum();
			}else{
				saveSpecialNum();
			}
			break;
		case FREE_TOP_TYPE:
			if(size < HwConstant.REQUEST_TOP_APP_NUM){
				removeTopNum();
			}else{
				saveTopNum();
			}
			break;
		case SPERE_TYPE:
			if(size < HwConstant.REQUEST_SPERE_APP_NUM){
				removeSpereNum();
			}else{
				saveSpereNum();
			}
			break;
		default:
			ContentManagerLog.d(TAG, " default .");
		}
	}
	
	private void saveSpecialNum(){
		shareUtil.saveSpecailAppNum();
	}
	private void saveTopNum(){
		shareUtil.saveTopAppNum();
	}
	private void saveSpereNum(){
		shareUtil.saveSpereAppNum();
	}
	
	private void removeSpecialNum(){
		shareUtil.removeSpecialAppNum();
	}
	private void removeTopNum(){
		shareUtil.removeTopAppNum();
	}
	private void removeSpereNum(){
		shareUtil.removeSpereAppNum();
	}
	

}
