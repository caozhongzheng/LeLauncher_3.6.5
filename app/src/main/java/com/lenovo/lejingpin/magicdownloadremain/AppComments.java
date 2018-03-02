package com.lenovo.lejingpin.magicdownloadremain;

import java.sql.Date;
import java.util.ArrayList;


import com.lenovo.launcher.R;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.lejingpin.hw.content.data.HwConstant;



import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AppComments extends Activity {
	private static final String TAG = "zdx";
	
	//private FrameLayout mCommentLayer= null;
	private CommentAdapter mCommentAdapter;
	private AppCommentReceiver mCommentReceiver;
	private Context mContext = null;
	private String mPackageName = null;
	private String mVersionCode = null;
	private String mCategory = null ;
	
	private ListView mListView;
	private LinearLayout loadingV;
	private ProgressBar progressBarV;
	private TextView loading_textV;
	private Button loading_refresh;
	
	private static final int MSG_GETLIST = 100;
	
	private Handler mInitHandler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
	        switch (msg.what) {
	        case MSG_GETLIST:
	        	requestCommentList();
	            break;
	            
	        	default:
	        		
	        	break;
	        }
	    }
	};
	
	private static class CommentAdapter extends ArrayAdapter<String> {

		public CommentAdapter(Context context, int resource, int textViewResourceId) {
			super(context, resource, textViewResourceId);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = super.getView(position, convertView, parent);
			String[] infos = getItem(position).split("@%!");
			// ViewGroup itemRoot = (ViewGroup) view.getParent();
			((TextView) view.findViewById(R.id.comment_author)).setText(infos[0]);
			((TextView) view.findViewById(R.id.comment_desc)).setText(infos[1]);
			((TextView) view.findViewById(R.id.comment_time)).setText(parseDate(infos[2]));
			return view;
		}
		private static String parseDate(String longDate) {
			try {
				return String.format("%tF", new Date(Long.parseLong(longDate.trim())));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}
	}
	
	//------Get App Comment------
	private class AppCommentReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.d(TAG, "AppCommentReceiver.onReceive, action : " + action);
			if((HwConstant.ACTION_REQUEST_APP_COMMON_LIST + "_COMPLETE").equals(action)) {
				ArrayList<String> commentList = intent.getStringArrayListExtra("common_list");
				boolean  result = intent.getBooleanExtra("result", false);
				updateComment(commentList, result);
			}else if((HwConstant.ACTION_REQUEST_HAWAII_SEARCH_APP_COMMENT_LIST + "_COMPLETE").equals(action)) {
				Log.i(TAG, "AppCommentReceiver get Hawaii Search COMMENT_LIST Action");
				ArrayList<String> commentList = intent.getStringArrayListExtra("common_list");
				boolean  result = intent.getBooleanExtra("result", false);
				updateComment(commentList, result);
			}
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG,"AppComments.onCreate()");	
		this.setContentView(R.layout.magicdownload_app_detail_comment);		
		mContext = this;
		if(SettingsValue.getCurrentMachineType(this)==-1){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		Intent intent = getIntent();
		mPackageName = intent.getStringExtra(HwConstant.EXTRA_PACKAGENAME);
		mVersionCode = intent.getStringExtra(HwConstant.EXTRA_VERSION);
		mCategory = intent.getStringExtra(HwConstant.EXTRA_CATEGORY);
		Log.i(TAG,"AppComments.onCreate(), packname:"+ mPackageName +", versioncode:"+ this.mVersionCode );

		setupView();	
        handleGetMessage(MSG_GETLIST);
	}
	
    private void handleGetMessage(int msgID ) {
    	mInitHandler.removeMessages(msgID);
    	mInitHandler.sendEmptyMessage(msgID);
    }
	
	private void setupView() {
		setContentView(R.layout.magicdownload_app_detail_comment);
		loadingV = (LinearLayout) findViewById(R.id.loading);
		progressBarV = (ProgressBar) findViewById(R.id.progressing);
		loading_textV = (TextView) findViewById(R.id.loading_text);
		loading_refresh = (Button) findViewById(R.id.refresh_button);
		loading_refresh.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		    	handleGetMessage(MSG_GETLIST);
		    }
		});
		
		mListView = (ListView) findViewById(R.id.comment_list);
		mCommentAdapter = new CommentAdapter(mContext, R.layout.magicdownload_app_detail_comment_item, R.id.comment_desc);
		mListView.setAdapter(mCommentAdapter);
		
		//------Get App Comment------
		IntentFilter filter = new IntentFilter();
		filter.addAction(HwConstant.ACTION_REQUEST_APP_COMMON_LIST + "_COMPLETE");
		filter.addAction(HwConstant.ACTION_REQUEST_HAWAII_SEARCH_APP_COMMENT_LIST+ "_COMPLETE");
		mCommentReceiver = new AppCommentReceiver();
		registerReceiver(mCommentReceiver, filter);
	}

	private void showLoadView(){
		mListView.setVisibility(View.GONE);
		loadingV.setVisibility(View.VISIBLE);
		loading_textV.setText(R.string.detail_comment_loading);
		loading_refresh.setVisibility(View.GONE);
	}
	
	private void showErrorView(){
		loadingV.setVisibility(View.VISIBLE);
		loading_textV.setText(R.string.detail_comment_empty_1);
		loading_refresh.setVisibility(View.VISIBLE);
		progressBarV.setVisibility(View.GONE);
		mListView.setVisibility(View.GONE);
	}
	
	private void showEmptyView(){
	    loadingV.setVisibility(View.VISIBLE);
	    loading_textV.setText(R.string.detail_comment_empty);
	    loading_refresh.setVisibility(View.GONE);
	    progressBarV.setVisibility(View.GONE);
	    mListView.setVisibility(View.GONE);
	}
	
	private void showListView(ArrayList<String> commentList){
	    loadingV.setVisibility(View.GONE);
	    mListView.setVisibility(View.VISIBLE);
	    mCommentAdapter.addAll(commentList);
	}
	
	private void updateComment(ArrayList<String> commentList, boolean result) {
		//Log.i(TAG,"updateComment--------------result:"+ result);
		if(result){
		    if(commentList != null && mCommentAdapter!=null) {
		    	if( !commentList.isEmpty())
		    	    showListView(commentList);
		    	else 
		    		showEmptyView();
		    } else 
		    	showEmptyView();
		}else
			showErrorView();
	}
	
	private void requestCommentList() {
		//Log.i(TAG,"AppComments.requestCommentList()");
		String netType = HwConstant.getConnectType(this);
		if("other".equals(netType)) {
			updateComment(null, false);
		} else {	
			showLoadView();
			//------Get App Comment------
			
			if(mCategory!=null && mCategory.equals(HwConstant.CATEGORY_HAWAII_SEARCH_APP)){
				Intent intent = new Intent(HwConstant.ACTION_REQUEST_HAWAII_SEARCH_APP_COMMENT_LIST);
				Log.d(TAG, "requestCommentList_hawaii_search.requestCommentList, pkg=" + mPackageName + ", vcode=" + mVersionCode);
				intent.putExtra("package_name", mPackageName);
				intent.putExtra("version_code", mVersionCode);
				mContext.sendBroadcast(intent);
				
			}else{
				Intent intent = new Intent(HwConstant.ACTION_REQUEST_APP_COMMON_LIST);
				Log.d(TAG, "AppCommentList.requestCommentList, pkg=" + mPackageName + ", vcode=" + mVersionCode);
				intent.putExtra("package_name", mPackageName);
				intent.putExtra("version_code", mVersionCode);
				mContext.sendBroadcast(intent);
			}
		}
	}
	
	public void onDestroy(){
		unregisterReceiver(mCommentReceiver);
		super.onDestroy();
	}

}
