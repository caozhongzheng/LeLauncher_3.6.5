package com.lenovo.lejingpin.hw.content.action;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lenovo.lejingpin.ams.AmsRequest;
import com.lenovo.lejingpin.ams.AmsSession;
import com.lenovo.lejingpin.ams.AmsSession.AmsCallback;
import com.lenovo.lejingpin.ams.CommInfoRequest;
import com.lenovo.lejingpin.ams.CommInfoRequest.CommInfo;
import com.lenovo.lejingpin.ams.CommInfoRequest.CommInfoResponse;
import com.lenovo.lejingpin.hw.content.data.HwConstant;

import com.lenovo.lejingpin.hw.content.util.DeviceInfo;
import com.lenovo.lejingpin.share.service.TaskService.Action;

public class CommonListAction implements Action {
	private static final String TAG = "CommonListAction";
//	private Context mContext;
	private static String mPackageName;
	private static String mVersionCode;
	private static CommonListAction mAction;

	private CommonListAction() {
	}

	private CommonListAction(String packageName, String versionCode) {
//		mContext = context;
	}

	public static CommonListAction getInstance(String packageName, String versioncode) {
		if(mAction == null) {
			mAction = new CommonListAction(packageName, versioncode);
		}
		mPackageName = packageName;
		mVersionCode = versioncode;
		return mAction;
	}

	@Override
	public void doAction(Context context) {
		requestCommonList(context);
	}
	
	//yangmao add for search_move 1225 start
	
	public void doAction_hawaii_search(Context context) {
		Log.i(TAG,"CommentListAction.doAction_hawaii_search");
		requestCommentList_HawaiiSearch(context);
	}
	
	//yangmao add for search_move 1225 end
	
	private void requestCommonList(final Context context) {
		DeviceInfo deviceInfo = DeviceInfo.getInstance(context);
		AmsSession.init(context, new AmsCallback() {

			public void onResult(AmsRequest request, int code, byte[] bytes) {
				if(code != 200) {
					sendIntentForCommonListFinished(context, false, null, null);
				} else {
					getCommonList(context, mPackageName, mVersionCode);
				}
			}
		}, deviceInfo.getWidthPixels(), deviceInfo.getHeightPixels());
	}
	
	//yangmao add for search_move 1225 start
	
	private void requestCommentList_HawaiiSearch(final Context context) {	
		DeviceInfo deviceInfo = DeviceInfo.getInstance(context);
		AmsSession.init(context, new AmsCallback() {
			public void onResult(AmsRequest request, int code, byte[] bytes) {
				Log.i(TAG,"requestCommentList_HawaiiSearch.requestCommonList >> result code:"+ code);
				if(code != 200) {
					sendIntentForCommentListFinished_HawaiiSearch(context, false, null);
				} else {
					getCommentList_HawaiiSearch(context, mPackageName, mVersionCode);
				}
			}
		}, deviceInfo.getWidthPixels(), deviceInfo.getHeightPixels());
	}
	
	//yangmao add for search_move 1225 end
	
	

	private void getCommonList(final Context context, String packageName, String versionCode) {
		CommInfoRequest request = new CommInfoRequest(context);
		request.setData(packageName, versionCode, 0, 20, null);
		Log.d(TAG, "pkg=" + packageName + ", vcode=" + versionCode);
		AmsSession.execute(context, request, new AmsCallback() {
			@Override
			public void onResult(AmsRequest request, int code, byte[] bytes) {
				CommInfoResponse response = new CommInfoResponse();
				if(bytes != null) {
					response.parseFrom(bytes);
				}
				ArrayList<CommInfo> commonList = response.getItemList();
				ArrayList<String> commonStringList = new ArrayList<String>();
				if(commonList != null) {
					for (CommInfo info : commonList) {
						StringBuilder builder = new StringBuilder();
						builder.append(info.getUserNick());
						builder.append("@%!");
						builder.append(info.getCommentContext());
						builder.append("@%!");
						builder.append(info.getCommentDate());
						commonStringList.add(builder.toString());
					}
				}
				String allCount = response.getAllCount();
				sendIntentForCommonListFinished(context, response.getIsSuccess(), commonStringList, allCount);
			}
		});
	}

	private void sendIntentForCommonListFinished(Context context, boolean result, ArrayList<String> commonArray, String allCount) {
		Intent intent = new Intent();
		intent.setAction(HwConstant.ACTION_REQUEST_APP_COMMON_LIST + "_COMPLETE");
		intent.putExtra("result", result);
		intent.putStringArrayListExtra("common_list", commonArray);
		intent.putExtra("all_count", allCount);
		context.sendBroadcast(intent);
	}
	
	//yangmao add for search_move 1225 start
	
	private void sendIntentForCommentListFinished_HawaiiSearch(Context context, boolean result,
			ArrayList<String> commonArray) {
		Log.i(TAG, "CommentListAction.sendIntentForCommentListFinished(), result:"+ result);
		Intent intent = new Intent();
		intent.setAction(HwConstant.ACTION_REQUEST_HAWAII_SEARCH_APP_COMMENT_LIST + "_COMPLETE");
		intent.putExtra("result", result);
		intent.putStringArrayListExtra("common_list", commonArray);
		context.sendBroadcast(intent);
	}
	
	
	
	private void getCommentList_HawaiiSearch(final Context context, String packageName, String versionCode) {
		CommInfoRequest request = new CommInfoRequest(context);
		request.setData(packageName, versionCode, 0, 50, null);

		AmsSession.execute(context, request, new AmsCallback() {
			@Override
			public void onResult(AmsRequest request, int code, byte[] bytes) {
				Log.i(TAG, "getCommentList_HawaiiSearch.getCommonList >> result code:" + code );
				CommInfoResponse response = new CommInfoResponse();
				if(bytes != null) {
					response.parseFrom(bytes);
				}
				boolean success= response.getIsSuccess();
				ArrayList<String> commonStringList = null;
				Log.i(TAG, "CommentListAction.getCommonList >> success :" + success );
				if( success){
				    ArrayList<CommInfo> commonList = response.getItemList();
				    commonStringList = new ArrayList<String>();
				    if(commonList != null) {
					    for (CommInfo info : commonList) {
						    StringBuilder builder = new StringBuilder();
						    builder.append(info.getUserNick());
						    builder.append("@%!");
						    builder.append(info.getCommentContext());
						    builder.append("@%!");
						    builder.append(info.getCommentDate());
						    commonStringList.add(builder.toString());
						    Log.i(TAG,"-----comment:"+ builder.toString());
					    }
				    }
				}
				sendIntentForCommentListFinished_HawaiiSearch(context, success, commonStringList);
			}
		});
	}
	//yangmao add for search_move 1225 end
}
