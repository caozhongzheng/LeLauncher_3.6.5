package com.lenovo.launcher2.addon.search;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.GZIPInputStream;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnKeyListener;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.speech.RecognizerIntent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;
import com.lenovo.launcher2.commoninterface.ApplicationInfo;
import com.lenovo.launcher2.commonui.LeAlertDialog;
import com.lenovo.launcher2.customizer.GlobalDefine;
import com.lenovo.launcher2.customizer.HanziToPinyin;
import com.lenovo.launcher2.customizer.Reaper;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.HanziToPinyin.Token;
import com.lenovo.launcher2.settings.SeniorSettings;
import com.lenovo.lejingpin.ams.AmsRequest;
import com.lenovo.lejingpin.ams.AmsSession;
import com.lenovo.lejingpin.ams.GetHotRequestFD;
import com.lenovo.lejingpin.ams.GetImageRequest;
import com.lenovo.lejingpin.ams.NewSearchAppName;
import com.lenovo.lejingpin.ams.AmsSession.AmsCallback;
import com.lenovo.lejingpin.ams.GetHotRequestFD.GetHotResponse;
import com.lenovo.lejingpin.ams.NewSearchAppName.Application;
import com.lenovo.lejingpin.ams.NewSearchAppName.NewSearchResponse;
import com.lenovo.lejingpin.hw.content.data.HwConstant;
import com.lenovo.lejingpin.hw.content.util.DeviceInfo;
import com.lenovo.lejingpin.hw.ui.RecommendLocalAppInfo.Status;
import com.lenovo.lejingpin.hw.ui.Util;
import com.lenovo.lejingpin.magicdownloadremain.AppDownloadControl;
import com.lenovo.lejingpin.magicdownloadremain.HawaiiSearchAppInfoDialogFragment;
import com.lenovo.lejingpin.magicdownloadremain.MagicDownloadControl;
import com.lenovo.lejingpin.settings.LejingpingSettings;
import com.lenovo.lejingpin.settings.LejingpingSettingsValues;
import com.lenovo.lejingpin.share.download.DownloadConstant;
import com.lenovo.lejingpin.share.download.DownloadInfo;
import com.lenovo.lejingpin.share.download.LDownloadManager;



public class SearchActivityNew extends Activity {
    /** Called when the activity is first created. */
    private static final String TAG = "searchactivitynew";
 
	EditText mEditSearch;
	SearchListView searchListView;
	RelativeLayout mSearchParent;
	TextView mNumMsg;
	ImageView mDivider2;
	Context mContext;
	
	ImageView mLocalSearchNodataImg,mNetSearchNodataImg;
	TextView mLocalSearchNodataText,mNetSearchNodataText;
	Button mLoacalSearchNodataBtn,mNetSearchNodataBtn;
	
	RelativeLayout mNetSearchNodataLayout ;

	MyAdapter mMyAdapter;
	String mEditString;
	String mOldSting;
	Button mSearchButton, mNetworkSearchButton, mReciButton, mHistoryButton;

	boolean mSearch = true;
	List<Map<String, Object>> mDataList = new ArrayList<Map<String, Object>>();
	private InputMethodManager mIME;
	AbsListView app_listView;
	TextView mTextView_nodata;
	HawaiiSearchAdapter mHawaiiSearchAdapter;
	String mSearchContent;
	LinearLayout mHawaii_search_linearlayout;
	private int startindex = 0;
	private View mFootView;
	private List<DownloadInfo> downloadInfos;
	
	private List<PackageInfo> packagesList;
	List<NewSearchAppName.Application> TotalList, searchResultList,FootSearchResultList;
	private LinearLayout mLoadingLayout;
	private static final int SHOW_SEARCH_APP_LIST = 1;
	private static final int SEND_CLIENTID_EXPIRED = 2;
	private static final int JSON_SEARCH_IS_FALSE = 3;
	private static final int HAWAII_SEARCH_NO_DATA = 5;
	private static final int HAWAII_REQUEST_CODE_ERROR = 6;
	private static final int HAWAII_REMOVE_FOOTVIEW = 7;
	private static final int REQUEST_CONFIRM_SENIOR_NETWORK = 8;
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 9;
	private static final int SEARCH_HISTORY_MAX_LENGTH = 10;
	private static final int SEARCH_MSG_HISTORY_LOAD_COMPLETED = 11;
	private static final int GET_HOT_LABLE = 12;
	private static final int REQUEST_HOT = 13;
	private static final int RECI_REQUEST_ERROR = 14;
	private static final int NO_NET_CONNECT = 15;
	private static final int LOAD_HOT_LIST = 16;
	private static final int MSG_NO_MORE = 17;
	private static final String REQUEST_SEARCH_EXCEPTION = "com.lenovo.lejingpin.newsearch.exception";
	private boolean foottag = true;
	private boolean STARTINDEX_TAG = true;
	//private static ConnectivityManager mConnectManager;
	private static final int search_click_tag = 0;
	private static final int cleanCachTag = -1;
	private int mDownloadUpperLimit = 4;

	// 页卡内容
	private ViewPager mPager;
	// Tab页面列表
	private List listViews;
	// 动画图片
	private ImageView cursor;
	// 页卡头标
	private RadioButton t1, t2, t3, t4;
	private RadioGroup mToolBar;
	private Matrix matrix3 = new Matrix();
	private Matrix matrix4 = new Matrix();

	// 动画图片偏移量
	private int offset = 0;
	// 当前页卡编号
	private int currIndex = 0;
	// 动画图片宽度
	private int bmpW;

	private MyPagerAdapter mpAdapter;

	LayoutInflater mLi;

	private View view0;
	private View view1;
	private View view2, view3;

	private String previousSearchContent;
	private ArrayList<String> mHistoryKeyword = new ArrayList<String>();
	private SearchHistoryAdapter mSearchHistoryAdapter;

	private ListView searchHistoryList;
	private TextView mTextViewNoHistory;
	private View searchHistoryFooter;

	private static final String SEARCH_HISTORY_SHARE_NAME = "search_history";
	private static final String SEARCH_HISTORY_SHARE_KEY = "keyword";
	
	private static final String SEARCH_HOT_SHARE_NAME = "search_hot";

	private String mKeyword;

	private TextView mTextView1, mTextView2, mTextView3, mTextView4,
			mTextView5, mTextView6, mTextView7, mTextView8, mTextView9,
			mTextView10, mTextView11, mTextView12, mTextView13, mTextView14,
			mTextView15, mTextView16;

	private Button mButtonChange;

	private GridLayout mContainer;

	private TextView mTextViewReciNodata;

	private ProgressBar mProgressBarReci;

	private int hotTag = 0;

	private LinearLayout mContainerParent;

	TextView textview[] = new TextView[16];

	List<TextView> listTextView = new ArrayList<TextView>();

	List<Integer> mColorList = new ArrayList<Integer>();

		
			
	ImageButton audioSearch;
	String mUseLocalString;
	private Typeface tf;
	
	private int w = 0;
	private int h = 0;
	
	//yangmao add 0604
    private WindowManager mWM;
    private TextView mOverlay;
    private int PadOrPhone ;
	//yangmao add 0604
	
	//yangmao add it 0506 
	
	private Handler mInitHandler = new Handler();	
	//List<Item> items = new ArrayList<Item>();		
	
	List<Item> items = new CopyOnWriteArrayList<Item>();
	private boolean isNetSearchToLocalSearch = false;
	private ArrayList<String> mHotLableListString = new ArrayList<String>();
	//private static ArrayList<GetHotRequestFD.HotLable> mHotLableList;
	
	//yangmao add it 0506
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_SEARCH_APP_LIST:

				int clickTag = msg.getData().getInt("CLICK_TAG");
				
				if(clickTag==0 && TotalList!=null && TotalList.size()!=0){
					TotalList.clear();
				}
				if (TotalList != null) {					
				    TotalList.addAll(searchResultList);
				}
				
				if (TotalList != null && packagesList != null) {
					for (int i = 0; i < TotalList.size(); i++) {
						NewSearchAppName.Application mm = (Application) TotalList.get(i);
						for (int k = 0; k < packagesList.size(); k++) {
							PackageInfo InstallpackageInfo = packagesList.get(k);
							if (InstallpackageInfo == null)
								continue; 
							android.content.pm.ApplicationInfo InstallappInfo = InstallpackageInfo.applicationInfo;
							if (InstallappInfo == null)
								continue;
							
							if(mm.getPackage_name().equals(InstallappInfo.packageName)){
								mm.setApp_status(getString(R.string.download_installed));
							}
							
							
						}
					}
				}
				
				
				
				
				if (TotalList != null && downloadInfos != null) {
					for (int i = 0; i < TotalList.size(); i++) {
						NewSearchAppName.Application mm = (Application) TotalList.get(i);
						for (int j = 0; j < downloadInfos.size(); j++) {
							if (mm.getPackage_name().equals(downloadInfos.get(j).getPackageName())
									&& mm.getApp_versioncode().equals(downloadInfos.get(j).getVersionCode())) {
								switch (downloadInfos.get(j).getDownloadStatus()) {
								case 192:
									mm.setApp_status(getString(R.string.download_pause));
									break;
								case 193:
									mm.setApp_status(getString(R.string.download_resume));
									break;
								case 200:
									mm.setApp_status(getString(R.string.app_detail_install));	
									break;
								case 10000:
									mm.setApp_status(getString(R.string.download_installed));
									break;
									
								default :
									
									break;
								}

							}
						}
					}
				}
				
	
				
				foottag = true;
				STARTINDEX_TAG = true;
				mLoadingLayout.setVisibility(View.GONE);
				mTextView_nodata.setVisibility(View.GONE);
				
				mNetSearchNodataLayout.setVisibility(View.GONE);
				
				if(!mSearch){
					Log.i(TAG, "mHawaii_search_linearlayout.setVisibility(View.VISIBLE)");
					mHawaii_search_linearlayout.setVisibility(View.VISIBLE);
				}else{
					Log.i(TAG, "mHawaii_search_linearlayout.setVisibility(View.GONE)");
					mHawaii_search_linearlayout.setVisibility(View.GONE);
				}
				
				if(PadOrPhone==-1){
					// yangmao add for bug 170635
					if (((ListView)app_listView).getFooterViewsCount() != 0) {
						((ListView)app_listView).removeFooterView(mFootView);					
					}
					if (((ListView)app_listView).getFooterViewsCount() == 0) {
						((ListView)app_listView).addFooterView(mFootView);
					}
					if (TotalList != null && TotalList.size() < 20) {
						((ListView)app_listView).removeFooterView(mFootView);
					}
				}else{
					hideOverLay();
				}
				mHawaiiSearchAdapter.notifyDataSetChanged();
				break;
				
			case JSON_SEARCH_IS_FALSE:
				break;
				
			case NO_NET_CONNECT:
				
				if(PadOrPhone==-1){				
					if (((ListView)app_listView).getFooterViewsCount() != 0) {
						((ListView)app_listView).removeFooterView(mFootView);					
					}
				}
				
				if(mHawaii_search_linearlayout.getVisibility()==View.GONE){
					mHawaii_search_linearlayout.setVisibility(View.VISIBLE);
				}
				mLoadingLayout.setVisibility(View.GONE);
				mTextView_nodata.setVisibility(View.GONE);
				app_listView.setVisibility(View.GONE);
				mNetSearchNodataLayout.setVisibility(View.VISIBLE);
				
				mNetSearchNodataImg.setBackgroundResource(R.drawable.netsearch_unconnected);
				mNetSearchNodataText.setText(R.string.net_search_nodata_noconneted_text);
				mNetSearchNodataBtn.setText(R.string.net_search_nodata_set_net);
				
				
				break;
			case HAWAII_REQUEST_CODE_ERROR:
				if(PadOrPhone==-1){
					if (((ListView)app_listView).getFooterViewsCount() != 0) {
						((ListView)app_listView).removeFooterView(mFootView);					
					}
				}
				
				if(mHawaii_search_linearlayout.getVisibility()==View.GONE){
					mHawaii_search_linearlayout.setVisibility(View.VISIBLE);
				}
				mLoadingLayout.setVisibility(View.GONE);
				mTextView_nodata.setVisibility(View.GONE);
//				mTextView_nodata.setText(R.string.hawaii_search_jason_false);
				mNetSearchNodataLayout.setVisibility(View.VISIBLE);
				
				mNetSearchNodataImg.setBackgroundResource(R.drawable.netsearch_error);
				mNetSearchNodataText.setText(R.string.net_search_nodata_error_text);
				mNetSearchNodataBtn.setText(R.string.net_search_nodata_refresh);
				
				
				break;
				
			case HAWAII_SEARCH_NO_DATA:
				if(mHawaii_search_linearlayout.getVisibility()==View.GONE){
					mHawaii_search_linearlayout.setVisibility(View.VISIBLE);
				}
				mNetSearchNodataLayout.setVisibility(View.GONE);
				mTextView_nodata.setVisibility(View.VISIBLE);
				mTextView_nodata.setText(R.string.hawaii_search_no_data);
				break;
				
			case SEND_CLIENTID_EXPIRED:
				final SharedPreferences preferences = mContext.getSharedPreferences("Ams", Context.MODE_PRIVATE);
				if(preferences.getString("ClientId", null) != null){					
					Editor editor = preferences.edit();
					editor.remove("ClientId");
					editor.commit();
					Log.i(TAG, ">>>>>>>>>>> remove the ClientId  <<<<<<<<<");
				}				
				requestNewSearch(mSearchContent,0,search_click_tag);
				break;
				
			case HAWAII_REMOVE_FOOTVIEW:
				if(PadOrPhone==-1){
					if(((ListView)app_listView).getFooterViewsCount()!=0){
						((ListView)app_listView).removeFooterView(mFootView);					
						mHawaiiSearchAdapter.notifyDataSetChanged();
					}
				}
				break;
								
				
			// yangmao add 0305

			case SEARCH_MSG_HISTORY_LOAD_COMPLETED:

				Log.i("yangmao_0301", "get SEARCH_MSG_HISTORY_LOAD_COMPLETED ");
				String[] historyArray = (String[]) msg.obj;

				if (mHistoryKeyword != null && mHistoryKeyword.size() != 0) {
					for (int i = 0; i < historyArray.length; i++) {
						if (mHistoryKeyword.contains(historyArray[i])) {
							mHistoryKeyword.remove(historyArray[i]);
						}
					}
				}
				if(mHistoryKeyword != null){
					mHistoryKeyword.addAll(Arrays.asList(historyArray)); // it's ok
				}
				if (mHistoryKeyword != null &&  mHistoryKeyword.size() == SEARCH_HISTORY_MAX_LENGTH) {
					mHistoryKeyword.remove(SEARCH_HISTORY_MAX_LENGTH - 1);
				}

				showSearchHistory();
				mSearchHistoryAdapter.notifyDataSetChanged();

				break;
				
			case LOAD_HOT_LIST:
				String[] hotArray = (String[]) msg.obj;
				Log.i(TAG, "LOAD_HOT_LIST ----- hotArray is:"+hotArray.length);				
				Log.i(TAG, "LOAD_HOT_LIST ----mHotLableListString is:"+mHotLableListString);
				mHotLableListString.addAll(Arrays.asList(hotArray));				
				Log.i(TAG, "mHotLableListString size is:"+mHotLableListString.size());				
				mProgressBarReci.setVisibility(View.GONE);
				mTextViewReciNodata.setVisibility(View.GONE);
				mContainerParent.setVisibility(View.VISIBLE);

				Collections.shuffle(listTextView);
				Collections.shuffle(mColorList);

				for (int i = 0; i < textview.length; i++) {
//					listTextView.get(i).setText(mHotLableList.get(i).getKeyname());
//					listTextView.get(i).setBackgroundColor(mColorList.get(i % 5));								
					listTextView.get(i).setText(mHotLableListString.get(i));
					listTextView.get(i).setBackgroundColor(mColorList.get(i % 5));

				}
							
				break;
				
				

			case GET_HOT_LABLE:
				Log.i("yangmao_0304", "handler receive GET_HOT_LABLE ");
				mProgressBarReci.setVisibility(View.GONE);
				mTextViewReciNodata.setVisibility(View.GONE);
				mContainerParent.setVisibility(View.VISIBLE);

				Collections.shuffle(listTextView);
				Collections.shuffle(mColorList);

				for (int i = 0; i < textview.length; i++) {
//					listTextView.get(i).setText(mHotLableList.get(i).getKeyname());
//					listTextView.get(i).setBackgroundColor(mColorList.get(i % 5));
					listTextView.get(i).setText(mHotLableListString.get(i));
					listTextView.get(i).setBackgroundColor(mColorList.get(i % 5));

				}

				break;

			case REQUEST_HOT:

				requestHot();

				break;

			case RECI_REQUEST_ERROR:

				mContainerParent.setVisibility(View.GONE);
				mProgressBarReci.setVisibility(View.GONE);
				mTextViewReciNodata.setVisibility(View.VISIBLE);
				break;
				
			case MSG_NO_MORE:
				 hideOverLay();
	             break;
			
			 default:
				
				break;
				
			}

							
			super.handleMessage(msg);
		}

	};
		
	
    //yangmao add end
	
    BroadcastReceiver mRecevier = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            final String action = intent.getAction();
            Log.i(TAG, "get action is:"+action);
            String pkg = intent.getStringExtra(HwConstant.EXTRA_PACKAGENAME);
			String vcode = intent.getStringExtra(HwConstant.EXTRA_VERSION);           

            if (SettingsValue.ACTION_ALLAPPLIST_CHANGED.equals(action)) {
                Log.d(TAG, "PACKAGE changed");
                      	
                    getItems();
                    getData();
                    mMyAdapter.notifyDataSetChanged();
                
            }
            
            
            if(REQUEST_SEARCH_EXCEPTION.equals(action)){
            	Log.i(TAG, "get search exception");
            	mHandler.sendEmptyMessage(HAWAII_REQUEST_CODE_ERROR);
            	
            }
            
            
			Status s = null;		
			Application app = null;

			if (DownloadConstant.ACTION_DOWNLOAD_STATE_CHANGED.equals(action)) {
				
				s = Status.parseStatus(intent.getStringExtra(DownloadConstant.EXTRA_STATUS));
				
			} else if ((DownloadConstant.ACTION_DOWNLOAD_DELETE.equals(action))) {
				s = Status.UNDOWNLOAD;
			}

			else if (DownloadConstant.ACTION_DOWNLOAD_INSTALL_UNINSTALL.equals(action)) {
				
				boolean bInstalled = intent.getBooleanExtra(DownloadConstant.EXTRA_RESULT, true);
				
				s = bInstalled ? Status.INSTALL : Status.UNINSTALL;

			}
			
			//yangmao add 0227 start
			
			else if(DownloadConstant.ACTION_APK_PARSE_OR_INSTALL_FAILED.equals(action)){
				Log.i("xujing3", "get parse or install failed,so let download status is undownload");
				s = Status.UNDOWNLOAD;
				
				DownloadInfo mDownloadInfo = new DownloadInfo(pkg,vcode);
				
				LDownloadManager.getDefaultInstance(mContext.getApplicationContext()).deleteTask(mDownloadInfo);
			}
			
			else if(DownloadConstant.ACTION_APK_FAILD_DOWNLOAD.equals(action)){
				Log.i(TAG, "get DownloadConstant.ACTION_APK_FAILD_DOWNLOAD");
				int category = intent.getIntExtra(DownloadConstant.EXTRA_CATEGORY, DownloadConstant.CATEGORY_ERROR);
			
	            String download_faild_pkg = intent.getStringExtra(DownloadConstant.EXTRA_PACKAGENAME);
				String download_faild_vcode = intent.getStringExtra(DownloadConstant.EXTRA_VERSION);  
				
				int result = intent.getIntExtra(DownloadConstant.EXTRA_RESULT,-1);
				Log.i(TAG, "category result is:"+category +";"+result);
				if(category == DownloadConstant.CATEGORY_SEARCH_APP){
					s = Status.UNDOWNLOAD;
					DownloadInfo mDownloadInfo = new DownloadInfo(download_faild_pkg,download_faild_vcode);
					
					LDownloadManager.getDefaultInstance(mContext.getApplicationContext()).deleteTask(mDownloadInfo);
					
					String error_string = getResources().getString(R.string.failed_download_other_error);
					if(result == DownloadConstant.FAILD_DOWNLOAD_NO_ENOUGHT_SPACE){
						error_string = getResources().getString(R.string.failed_download_no_enough_space);
					}else if(result == DownloadConstant.FAILD_DOWNLOAD_NETWORK_ERROR){
						error_string = getResources().getString(R.string.failed_download_network_error);
						
					}
					
					Toast.makeText(context, error_string, Toast.LENGTH_SHORT).show();
					
					
				}
				
			}
			
			else if(action.equals(Intent.ACTION_PACKAGE_REMOVED)){
				Log.i(TAG, "get ----------- removed action");
				s = Status.UNDOWNLOAD;
				pkg = intent.getData().getSchemeSpecificPart();
				
				app = findAppFromListForRemove(pkg);
				Log.i(TAG, "find fromlist remove app is:"+app);
				
				View view_remove = app_listView.findViewWithTag(new ViewHolder(pkg,null));
				Log.i(TAG, "view_remove  is :"+view_remove);
				TextView textview_remove =null ;
				if(app!=null){
					app.setApp_status(getString(R.string.app_detail_install));
				}
				if(view_remove!=null){
					textview_remove = (TextView) view_remove.findViewById(R.id.detail_download);
				}
				if (view_remove != null){
					Drawable drawable ;
					drawable = context.getResources().getDrawable(R.drawable.ic_download_install_normal);  
					drawable.setBounds(0, 0, w, h); 
					textview_remove.setCompoundDrawables(null, drawable, null, null);
					textview_remove.setText(R.string.app_detail_install);
				}
				
			}
			
			//yangmao add 0227 end
			
			//Application app = null;
			Drawable drawable;
			if(pkg!=null && vcode!=null){
				app = findAppFromList(pkg, vcode);
				Log.i(TAG, "findAPP");
			}
			View view = app_listView.findViewWithTag(new ViewHolder(pkg,vcode));
			TextView hawaii_search_list_download = null;
			if(view!=null){
				hawaii_search_list_download = (TextView) view.findViewById(R.id.detail_download);
			}
			if(s!=null){
				switch (s) {
				case DOWNLOADING:
					if(app!=null){
						app.setApp_status(getString(R.string.download_pause));
					}	
					if (hawaii_search_list_download != null){
						
						drawable = context.getResources().getDrawable(R.drawable.ic_download_pause_normal);  
						drawable.setBounds(0, 0, w, h); 
						hawaii_search_list_download.setCompoundDrawables(null, drawable, null, null);						
						hawaii_search_list_download.setText(R.string.download_pause);
					}
					break;
				case UNINSTALL:
					if(app!=null){
						app.setApp_status(getString(R.string.app_detail_install));
					}	
					if (hawaii_search_list_download != null){
						drawable = context.getResources().getDrawable(R.drawable.ic_download_install_normal);  
						drawable.setBounds(0, 0, w, h); 
						hawaii_search_list_download.setCompoundDrawables(null, drawable, null, null);
						hawaii_search_list_download.setText(R.string.app_detail_install);
					}
					
					break;
				case PAUSE:
					if(app!=null){
						app.setApp_status(getString(R.string.download_resume));
					}
					if (hawaii_search_list_download != null){
						drawable = context.getResources().getDrawable(R.drawable.ic_download_coutinue_normal);  
						drawable.setBounds(0, 0, w, h); 
						hawaii_search_list_download.setCompoundDrawables(null, drawable, null, null);
						hawaii_search_list_download.setText(R.string.download_resume);
					}
					break;
				case UNDOWNLOAD:
					if(app!=null){
						app.setApp_status(getString(R.string.download_download));
					}
					if (hawaii_search_list_download != null){
						Log.i("xujing3", "UNDOWNLOAD");
						drawable = context.getResources().getDrawable(R.drawable.ic_download_normal);  
						drawable.setBounds(0, 0, w, h); 
						hawaii_search_list_download.setCompoundDrawables(null, drawable, null, null);
						hawaii_search_list_download.setText(R.string.download_download);
					}
					break;
				case INSTALL:
					if(app!=null){
						app.setApp_status(getString(R.string.download_installed));
					}	
					if (hawaii_search_list_download != null){
						drawable = context.getResources().getDrawable(R.drawable.lejingpin_searchlist_run);  
						drawable.setBounds(0, 0, w, h); 
						hawaii_search_list_download.setCompoundDrawables(null, drawable, null, null);
						hawaii_search_list_download.setText(R.string.download_installed);
					}	
					break;
				default:
					if (hawaii_search_list_download != null){
						drawable = context.getResources().getDrawable(R.drawable.ic_download_normal);  
						drawable.setBounds(0, 0, w, h); 
						hawaii_search_list_download.setCompoundDrawables(null, drawable, null, null);
						hawaii_search_list_download.setText(R.string.download_download);
					}	
					break;
				}
			}	
		}
    };
    
    
    Application findAppFromList(String pkgName, String versionCode){
    	for(Application app : TotalList) {
    		if(pkgName.equals(app.getPackage_name()) && versionCode.equals(app.getApp_versioncode()) ){
    			return app;
    		}
    	}
    	return null;
    }
    
    Application findAppFromListForRemove(String pkgName){
    	for(Application app : TotalList) {
    		if(pkgName.equals(app.getPackage_name()) ){
    			return app;
    		}
    	}
    	return null;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.search_list_new_ym);
		PadOrPhone = SettingsValue.getCurrentMachineType(this);
		
		if(SettingsValue.getCurrentMachineType(this)==-1){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		
		mLi = LayoutInflater.from(this);
		
		if(PadOrPhone!=-1){
			mWM = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
		}
		delayCreateView();

		mSearchParent = (RelativeLayout) findViewById(R.id.search_parent);

		mEditSearch = (EditText) findViewById(R.id.search_view);

		mEditSearch.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView arg0, int actionId,
					KeyEvent arg2) {
				// TODO Auto-generated method stub

				if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
					
					if(mTextView_nodata.getVisibility()==View.VISIBLE){
						Log.i(TAG, "the user ues method search or search again,so delete the nodata tishi");
						mTextView_nodata.setVisibility(View.GONE);
					}
					netWorkSearch(search_click_tag);
					
					 if(mDataList.size()==0 && !mEditSearch.getText().toString().trim().isEmpty()){
			    		  Log.i("getdata", "local search is 0 so netsearch");
			    		  replaceView(1);   	  
			    	 }
					
					
				}
				return false;
			}
		});
               
		mEditSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int count) {
				// TODO Auto-generated method stub

				if (arg0.toString().trim().equals("")) {
					isNetSearchToLocalSearch = false;
					mSearchButton.setVisibility(View.GONE);
					mNetworkSearchButton.setVisibility(View.GONE);
					mReciButton.setVisibility(View.VISIBLE);
					mHistoryButton.setVisibility(View.VISIBLE);
					replaceView2(0);

				} else {
					Log.i(TAG, "the user input text is not null");
					View mView = (View) listViews.get(0);
					
					
					if (mView.getTag() != null && mView.getTag().equals("reci")) {
						Log.i(TAG, "the use input text is in first view");
						mSearchButton.setVisibility(View.VISIBLE);
						mNetworkSearchButton.setVisibility(View.VISIBLE);
						mReciButton.setVisibility(View.GONE);
						mHistoryButton.setVisibility(View.GONE);
						mSearch = true;
						Log.i("getdata", "replaceView 00000000");
						replaceView(0);
					}

				}

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}
		});

        audioSearch = (ImageButton) findViewById(R.id.audio_search);
        /*RK_AUDIOBUTTON dining 2012-11-12 S*/
        //request from BU, don't show any Google products in prodcuts
        //so Google audio search would be called by the button
        if( GlobalDefine.getVerisonWWConfiguration(this)){
        	audioSearch.setVisibility(View.GONE);
        }else{
            setaudioSearchListener();
        }
        /*RK_AUDIOBUTTON dining 2012-11-12 E*/
            

		mFootView = LayoutInflater.from(this).inflate(R.layout.hawaii_search_footer, null);

		mSearchButton = (Button) findViewById(R.id.search_btn);
		mNetworkSearchButton = (Button) findViewById(R.id.network_search_btn);

		mReciButton = (Button) findViewById(R.id.search_btn_reci);

		mHistoryButton = (Button) findViewById(R.id.search_lishi);

		mContext = this.getApplicationContext();
		
		w = dip2px(mContext,25);
		h = dip2px(mContext,25);
		//mConnectManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

		mReciButton.setSelected(true);
		mReciButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				mReciButton.setSelected(true);
				mHistoryButton.setSelected(false);
				mPager.setCurrentItem(0);

			}
		});
        
        
		mHistoryButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				mReciButton.setSelected(false);
				mHistoryButton.setSelected(true);
				mPager.setCurrentItem(1);

			}
		});
        
        
        
        
        mSearchButton.setSelected(true);
        mSearchButton.setClickable(true);
        mSearchButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (!mSearch) {
					mSearchButton.setSelected(true);
					mNetworkSearchButton.setSelected(false);
					mPager.setCurrentItem(0);

				}
			}
		});
		mNetworkSearchButton.setClickable(true);
		mNetworkSearchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if (mSearch) {
					mSearchButton.setSelected(false);
					mNetworkSearchButton.setSelected(true);
					mPager.setCurrentItem(1);

				}
			}
		});
        
       
        
		/*** RK_ID:RK_FONT AUT:zhanglz1@lenovo.com. DATE: 2013-02-21 S ***/
        if(tf == null)
        	tf= SettingsValue.getFontStyle(this);
		if(tf!=null && tf != mNumMsg.getTypeface()){
			mNetworkSearchButton.setTypeface(tf);
			mSearchButton.setTypeface(tf);
			mTextView_nodata.setTypeface(tf);
			mEditSearch.setTypeface(tf);

	        mNumMsg.setTypeface(tf);
	      //yangmao add history and hot-key
	        mHistoryButton.setTypeface(tf);
	        mReciButton.setTypeface(tf);
	        mButtonChange.setTypeface(tf);
	        mTextViewReciNodata.setTypeface(tf);
	        mTextViewNoHistory.setTypeface(tf);
	        
		}
		/*** RK_ID:RK_FONT AUT:zhanglz1@lenovo.com. DATE: 2013-02-21 E ***/

        /*RK_VERSION_WW dining 2012-10-22 S*/
        if(GlobalDefine.getVerisonWWConfiguration(this) ){
		    View button_line = findViewById(R.id.button_line);
		    if(button_line != null){
		    	button_line.setVisibility(View.GONE);
		    }
		}
        /*RK_VERSION_WW dining 2012-10-22 E*/
        mIME = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        
        searchListView.setOnTouchListener(new SearchListView.OnSearchListTouchListener(){

            @Override
            public void onTouch(MotionEvent event) {
                // TODO Auto-generated method stub
           	     /***RK_ID:RK_BUGFIX_173398 AUT:zhanglz1@lenovo.com.DATE:2012-12-19. ***/        
			    if (mIME != null && SearchActivityNew.this != null && SearchActivityNew.this.getCurrentFocus() != null)
                mIME.hideSoftInputFromWindow(SearchActivityNew.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }

        });   
        
        getData();
        Log.i(TAG,"main thread id is:"+Thread.currentThread().getId());
        new Thread(){
        	public void run() {
        		Log.i(TAG," sub thread id is:"+Thread.currentThread().getId());
        		getItems();      		
        	};       	
        }.start();
        
		mMyAdapter = new MyAdapter();

		searchListView.setAdapter(mMyAdapter);
		searchListView.setClickable(false);
		
		packagesList = mContext.getPackageManager()
				.getInstalledPackages(0);
		
		downloadInfos = LDownloadManager.getDefaultInstance(mContext.getApplicationContext()).getAllDownloadInfo();

		TotalList = new ArrayList<NewSearchAppName.Application>();

		mHawaiiSearchAdapter = new HawaiiSearchAdapter(this);
		if(PadOrPhone==-1){
			((ListView)app_listView).addFooterView(mFootView, null, false);
		}

		app_listView.setAdapter(mHawaiiSearchAdapter);
           
        
//		mHawaiiSearchButton.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Log.i(tag, "onClick-----------------------");
//				if(TotalList!=null && TotalList.size()!=0){
//					TotalList.clear();
//				}
//				
//				/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2012-12-21 S*/
//				/*String userNetworkString = mNetworkSearch.getText().toString();
//				if (!userNetworkString.isEmpty())
//				{
//				    Log.d(TAG, "userNetworkString = " + userNetworkString);				
//				    Launcher.processReaper(mContext, Reaper.REAPER_EVENT_NETWORK_SEARCH_NUM, 
//				             userNetworkString, Reaper.REAPER_NO_INT_VALUE);
//				}*/
//				/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2012-12-21 E*/
//				
//				netWorkSearch(search_click_tag);
//			}
//
//		});
        
		app_listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

				if (TotalList != null && TotalList.size() != 0 && TotalList.size() > arg2) {
					showAppDetail_Hawaii_new(TotalList.get(arg2));
				}

			}
		});
		
		
		
		app_listView.setOnScrollListener(new OnScrollListener() {

			int firstVisibleItem_new, visibleItemCount_new, totalItemCount_new;

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

				firstVisibleItem_new = firstVisibleItem;
				visibleItemCount_new = visibleItemCount;
				totalItemCount_new = totalItemCount;

			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {

					if(PadOrPhone==-1){
						if (firstVisibleItem_new != 0
								&& firstVisibleItem_new + visibleItemCount_new == totalItemCount_new) {
	
							if (startindex < 4 && STARTINDEX_TAG) {
								startindex++;
								STARTINDEX_TAG = false;
								if (startindex < 3) {
	
									new AsyncTask<Void, Void, Void>() {
	
										@Override
										protected Void doInBackground(Void... params) {
											// TODO Auto-generated method stub
	
	
											try {
												Thread.sleep(500);
											} catch (InterruptedException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
	
											if (foottag) {
												
												getNewSearch(mSearchContent,startindex, cleanCachTag);
												foottag = false;
											}
											return null;
										}
	
									}.execute();
	
								} else if (startindex == 3) {
									mHandler.sendEmptyMessage(HAWAII_REMOVE_FOOTVIEW);
								}
							}
	
						}
					}
					else if(PadOrPhone!=-1){
						if( app_listView.getLastVisiblePosition() == mHawaiiSearchAdapter.getCount() -1){
							Log.i("pad_search","step this------");
							
							if (startindex < 4 && STARTINDEX_TAG) {
							startindex++;
							STARTINDEX_TAG = false;
							if (startindex < 3) {
								showOverLay();
								new AsyncTask<Void, Void, Void>() {

									@Override
									protected Void doInBackground(Void... params) {
										// TODO Auto-generated method stub


										try {
											Thread.sleep(500);
										} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}

										if (foottag) {
											getNewSearch(mSearchContent,startindex, cleanCachTag);
											foottag = false;
										}
										return null;
									}

								}.execute();

							} else if (startindex == 3) {
								mHandler.sendEmptyMessage(HAWAII_REMOVE_FOOTVIEW);
							}
						}
						}
					}

				}
			}

		});
       
        searchListView.setClickable(true);                
        		
        mEditSearch.addTextChangedListener(new TextWatcher(){

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
                // TODO Auto-generated method stub 
                mOldSting = s.toString().trim();
                mEditString = s.toString().trim().toUpperCase();
                if (!mOldSting.isEmpty())
                {
                    if (mUseLocalString == null)
                    {
                        mUseLocalString = mOldSting;
                    }
                    else if (!mUseLocalString.contains(mOldSting))
                    {
                        mUseLocalString = mOldSting;
                    }
                }
                
                Log.d(TAG, "mEditString = " + mEditString);
                Log.i(TAG, "local search ontextchanged ");
                
                //before add this ,because the voice input use it
//                CharSequence text = mEditSearch.getText();
//    				if (text instanceof Spannable) {
//    					Spannable spanText = (Spannable) text;
//    					Selection.setSelection(spanText, text.length());
//    				}
                
                getData();               
                searchListView.setVisibility(View.VISIBLE);
                mMyAdapter.notifyDataSetChanged();

                
            }
        });  
            
        registerReceiver();
        
        /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2012-12-21 S*/
        Reaper.processReaper(mContext, 
           	   Reaper.REAPER_EVENT_CATEGORY_APPLIST, 
   			   Reaper.REAPER_EVENT_ACTION_APPLIST_SEARCH,
   			   Reaper.REAPER_NO_LABEL_VALUE,
   			   Reaper.REAPER_NO_INT_VALUE );
         /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2012-12-21 E*/
    }
    
	private void registerReceiver() {
		IntentFilter filter = new IntentFilter(SettingsValue.ACTION_ALLAPPLIST_CHANGED);

		filter.addAction(DownloadConstant.ACTION_DOWNLOAD_STATE_CHANGED);
		filter.addAction(DownloadConstant.ACTION_DOWNLOAD_INSTALL_UNINSTALL);
		filter.addAction(DownloadConstant.ACTION_DOWNLOAD_DELETE);
		filter.addAction(DownloadConstant.ACTION_APK_FAILD_DOWNLOAD);
		filter.addAction(DownloadConstant.ACTION_APK_PARSE_OR_INSTALL_FAILED);
		filter.addAction(REQUEST_SEARCH_EXCEPTION);
		filter.addAction(DownloadConstant.ACTION_APK_FAILD_DOWNLOAD);
		registerReceiver(mRecevier, filter);
		
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		mFilter.addDataScheme("package");
		registerReceiver(mRecevier, mFilter);
		
	}
    
    protected void getData() {
        Log.d(TAG, "getData");
        if(mDataList!=null){
        	mDataList.clear();
        }
        if (mEditString == null || mEditString.isEmpty())
        {	
            mNumMsg.setVisibility(View.GONE);
            mDivider2.setVisibility(View.GONE);
            return;
        }
        
        int num = 0;
//        ApplicationInfo info = null;       
//        Iterator<ApplicationInfo> it = ((LauncherApplication) mContext.getApplicationContext()).getModel().getAllAppsList().data.iterator();       
//        while (it.hasNext()) {
//            info = it.next();
//            if (!info.hidden && HanziToPinyin.getInstance().includeLookUpKey(mEditString, info.mLookupKeys.iterator())) {
//                Log.d(TAG, info.toString());
//                Map<String, Object> temp = new HashMap<String, Object>();
//                temp.put("icon", info.iconBitmap);
//                
//                SpannableString  colorTitle = getColorString(info.title.toString());                
//                temp.put("label", colorTitle);
//                temp.put("intent", info.intent);
//                temp.put("sortkey", info.sortKey[0]);
//                mDataList.add(temp);
//                num++;
//            }
//        }
        
		  Item info =null;       
		  Iterator<Item> it = items.iterator();  
	      while (it.hasNext()) {
	    	  info = it.next();
		      if (!info.hiden && HanziToPinyin.getInstance().includeLookUpKey(mEditString, info.mLookupKeys.iterator())) {
		          Log.d(TAG, info.toString());
		          Map<String, Object> temp = new HashMap<String, Object>();
		          temp.put("icon", info.icon);
		          
		          SpannableString  colorTitle = getColorString(info.title.toString());                
		          temp.put("label", colorTitle);
		          temp.put("intent", info.intent);
		          temp.put("sortkey", info.sortKey);
		          mDataList.add(temp);
		          
		          num++;
		      }
	      }
                
/*      if( !isNetSearchToLocalSearch  ){   	  
    	  if(mDataList.size()==0){
    		  Log.i("getdata", "local search is 0 so netsearch");
    		  replaceView(1);   	  
    	  }
      }*/
      
      
        
        String string = mContext.getResources().getString(R.string.search_num);
        
        if(num==0){
	        mNumMsg.setVisibility(View.GONE);
	        mDivider2.setVisibility(View.GONE);
	        
	    	mLocalSearchNodataImg.setVisibility(View.VISIBLE);
	    	mLocalSearchNodataText.setVisibility(View.VISIBLE);
	    	mLoacalSearchNodataBtn.setVisibility(View.VISIBLE);
	        
	        
	        
        }else{
        
	        Object[] param = new String[]{num+"", mOldSting}; 
	        string = String.format(string, param);    
	        mNumMsg.setText(string);
	//        mNumMsg.setTextColor(SettingsValue.getIconTextStyleValue(mContext));
	//        mNumMsg.setShadowLayer(1.0f, 0, 0, Color.BLACK);
	    	mLocalSearchNodataImg.setVisibility(View.GONE);
	    	mLocalSearchNodataText.setVisibility(View.GONE);
	    	mLoacalSearchNodataBtn.setVisibility(View.GONE);
	        mNumMsg.setVisibility(View.VISIBLE);
	        mDivider2.setVisibility(View.VISIBLE);
        }
        return;
    }
    SpannableString getColorString(String title)
    {
        Log.d(TAG, "title = " + title + "mEditString = " + mEditString);
        ColorStateList redColors = ColorStateList.valueOf(0xFFFF0000);
        
        //直接是title的一部分，如短对应于短信。
        SpannableString tSS = new SpannableString(title);
        Log.d(TAG, "tSS len = " + tSS.length());
        if (title.toUpperCase().contains(mEditString))
        {   
            int start = title.toUpperCase().indexOf(mEditString);
            int editlen = mEditString.length();
            while (start != -1)
            {
                tSS.setSpan(new TextAppearanceSpan(null, 0, 0, redColors, null), start, start+editlen, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                start = title.toUpperCase().indexOf(mEditString, start+editlen);
            }
        }

        boolean pinyin = false;
        ArrayList<Token> tokens = HanziToPinyin.getInstance().getSort(title);
        final int tokenCount = tokens.size();
        final StringBuilder keyInitial = new StringBuilder();
        final StringBuilder keyPinyin = new StringBuilder();
        int oldType = Token.PINYIN;
        for (int i = tokenCount - 1; i >= 0; i--) {
            final Token token = tokens.get(i);
            if (Token.PINYIN == token.type) {
                keyInitial.insert(0, token.target.charAt(0));
                keyPinyin.insert(0, token.target);
                pinyin = true;
            } else if (Token.LATIN == token.type) {
                // Avoid adding space at the end of String.
                keyInitial.insert(0, token.source.charAt(0));
                keyPinyin.insert(0, token.source);
            }
            oldType = token.type;
        }
        
        if (pinyin)
        {
            String pinyinString = keyPinyin.toString().toUpperCase();
            
            //部分符合的拼音。如duanx对应于短信。
            if(pinyinString.contains(mEditString))
            {
                int start = pinyinString.indexOf(mEditString);
                int editlen = mEditString.length();
                while (start != -1)
                {
                    int len = 0;
                    int newlen = 0;
                    int end = start + editlen;
                    for (int i = 0; i < tokenCount; i++) {
                        final Token token = tokens.get(i);
                        newlen = len + token.target.length();
                        if (Token.PINYIN == token.type)
                        {
                            if (newlen > start && len < end) {
                                tSS.setSpan(new TextAppearanceSpan(null, 0, 0, redColors, null), token.position, token.position+1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            }
                        }
                        else
                        {
                            //对于英文和数字来说，完全被包含,end所在位置的拼音并没有被选中， newlen所在位置是下一个字符。
                            if(len >= start && newlen <= end)
                            {                                
                                tSS.setSpan(new TextAppearanceSpan(null, 0, 0, redColors, null), token.position, token.position+token.source.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            }
                            //前面部分被包含。
                            else if (len >= start && end > len && end < newlen)
                            {
                                tSS.setSpan(new TextAppearanceSpan(null, 0, 0, redColors, null), token.position, token.position+end-len, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            }
                            //后面部分被包含
                            else if (len < start && end >= newlen && start < newlen)
                            {
                                tSS.setSpan(new TextAppearanceSpan(null, 0, 0, redColors, null), token.position + start -len, token.position +  token.source.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            }
                            //不用考虑完全被包含的情况，即(len <= start && newlen >= end)，第一种中就包含了此种情况。
                        }
                        len = newlen;                    
                    }
                    start = pinyinString.indexOf(mEditString, start+editlen);
                }       
            }
        }        
        
        //dx对应于短信。
        String s = keyInitial.toString().toUpperCase();
        Log.d(TAG, "s = " + s);
        if(s.contains(mEditString))
        {
            int start = s.indexOf(mEditString);
            int editlen = mEditString.length();
            while (start != -1)
            {
                for (int j = start; j < start+editlen; j++)
                {
                    int pos = tokens.get(j).position;
                    Log.d(TAG, "pos = " + pos);
                    tSS.setSpan(new TextAppearanceSpan(null, 0, 0, redColors, null), pos, pos+1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                }
                
                start = s.indexOf(mEditString, start+editlen);
            }
        }

        return tSS;
    }
            
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

    @Override
	protected void onPause() {
		// TODO Auto-generated method stub
		
		
		/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2012-12-21 S*/
		/*if (mUseLocalString !=null && !mUseLocalString.isEmpty())
		{
	        Log.d(TAG, "mUseLocalString = " + mUseLocalString);                
            Launcher.processReaper(mContext, Reaper.REAPER_EVENT_LOCAL_SEARCH_NUM, 
                    mUseLocalString, Reaper.REAPER_NO_INT_VALUE);          
            
		}*/
		/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2012-12-21 E*/
		
		startindex=0;
		downloadInfos = null;
			
        /* RK_ID: RK_APPSMANAGER. AUT: liuli1 . DATE: 2012-08-01 . START */
        if (mIME == null) {
            mIME = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        if (mIME != null && mSearchParent != null) {
            Log.i(TAG, "mIME != null && mSearchParent != null");
//		mIME.hideSoftInputFromWindow(SearchActivity.this.getCurrentFocus()
//				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            mIME.hideSoftInputFromWindow(mSearchParent.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        Log.i(TAG, "ime op end");
        /* RK_ID: RK_APPSMANAGER. AUT: liuli1 . DATE: 2012-08-01 . END */
		// finish();
//		overridePendingTransition(R.anim.fade_in_fast, R.anim.fade_out_fast);
        
        mEditSearch.clearFocus();
	super.onPause();        
	}

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        
        Log.i(TAG, "ondestroy");
        isNetSearchToLocalSearch = false;
        unregisterReceiver(mRecevier);
        previousSearchContent = null;
        if(TotalList!=null){
        	TotalList.clear();
        }
        
        if(packagesList!=null){
        	packagesList.clear();
        }
        
		String tmp = mHistoryKeyword.toString();
		if( tmp.length() > 1){
			tmp = tmp.substring(1, tmp.length()-1).replace(" ", "");
			saveSearchHistory(tmp);
		}
		
		
		String tmpHot = mHotLableListString.toString();
		if( tmpHot.length() > 1){
			tmpHot = tmpHot.substring(1, tmpHot.length()-1).replace(" ", "");			
			saveHotList(tmpHot);
		}
		
		
		
		if(items!=null && items.size()!=0){
			items.clear();
		}
		
		if(mHotLableListString!=null && mHotLableListString.size()!=0){
			mHotLableListString.clear();
		}
		
		mIME = null;
		
		//clearAllNews();
		
		//this.mInitHandler.postDelayed(this.killService,500);
		
        try{
        if(mWM != null){
        if(overlayflag) 
        mWM.removeView(mOverlay);
        }
        }catch(Exception e){
            Log.e(TAG,"onDestroy error for mWM overlay");
        }
		super.onDestroy();
        
    }
    
    private void clearAllNews(){
        mEditSearch = null;
        searchListView = null;
        mSearchParent = null;
        mNumMsg = null;
        mDivider2 = null;
        
        mLocalSearchNodataImg = null;
        mNetSearchNodataImg = null;
        mLocalSearchNodataText = null;
        mNetSearchNodataText = null;
        mLoacalSearchNodataBtn  = null;
        mNetSearchNodataBtn = null;
        
        mNetSearchNodataLayout = null;

        mSearchButton  = null;
        mNetworkSearchButton = null;
        mReciButton = null;
        mHistoryButton  = null;

        app_listView = null;
        mTextView_nodata  = null;
        mHawaii_search_linearlayout  = null;
        mFootView = null;
        
        mLoadingLayout = null;
        mPager = null;
        listViews = null;
        cursor = null;
        t1 = null; t2 = null; t3 = null; t4 = null;
        mToolBar = null;
        view0 = null;
        view1 = null;
        view2 = null;
        view3 = null;

        searchHistoryList = null;
        mTextViewNoHistory = null;
        searchHistoryFooter = null;
        mTextView1 = null; 
        mTextView2 = null;
        mTextView3 = null;
        mTextView4 = null;
          
        mTextView5 = null;
        mTextView6 = null;
        mTextView7 = null;
        mTextView8 = null;
        mTextView9 = null;
            
        mTextView10 = null;
        mTextView11 = null;
        mTextView12 = null;
        mTextView13 = null;
        mTextView14 = null;
            
        mTextView15 = null;
        mTextView16 = null;
        

        mButtonChange = null;
        
        mContainer = null;
        
        mTextViewReciNodata = null;
        
        mProgressBarReci = null;

        mContainerParent = null;
        
        textview = null;
        
        listTextView  = null;
                
        audioSearch  = null;
    }
    
    
    Runnable killService = new Runnable(){
        public void run()
        {
            Log.e(TAG,"killProcess<F2><F2><F2><F2><F2><F2><F2><F2><F2>");
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    };
    
    
    
    public class HawaiiSearchAdapter extends BaseAdapter{
  	
    	private Context mContext;
		HashMap<String, Drawable> imgcache = new HashMap<String, Drawable>();
		
		public HawaiiSearchAdapter(Context context){
			this.mContext = context;			
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (TotalList == null) {
				return 0;
			} else {
				return TotalList.size();
			}
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {			
			final NewSearchAppName.Application app = TotalList.get(position);
			// TODO Auto-generated method stub
			
			final ViewHolder holder;
			Drawable mDrawable =null;
			
			if (convertView == null) {
				holder = new ViewHolder();				
				convertView = LayoutInflater.from(mContext).inflate(
						
						R.layout.hawaii_search_app_row, parent,false);								
				holder.appName = (TextView) convertView
						.findViewById(R.id.detail_name);
				holder.appIcon = (ImageView) convertView
						.findViewById(R.id.detail_icon);
				holder.appSize = (TextView) convertView
						.findViewById(R.id.detail_size);
				holder.appIspay = (TextView) convertView
						.findViewById(R.id.detail_pay);
				holder.appStar = (RatingBar) convertView
						.findViewById(R.id.detail_star);
				holder.appDownload = (TextView) convertView
						.findViewById(R.id.detail_download);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.position = position;
			holder.packageName = app.getPackage_name();
			holder.versionCode = app.getApp_versioncode();
			convertView.setTag(holder);
		
			holder.appDownload.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(final View v) {
					//yangmao add new start
					v.setEnabled(false);
					v.postDelayed(new Runnable() {
						public void run() {
							v.setEnabled(true);
						}
					}, 1500);
					//yangmao add new end
					
					//yangmao add for fix bug 5601 start 0116
//					int currCount = DownloadHelpers.getRunningAndControlPaused(mContext);
//					if (currCount >= mDownloadUpperLimit) {
//						Toast.makeText(mContext, R.string.download_count_overflow, Toast.LENGTH_SHORT).show();
//						return;
//					}
					
					//yangmao add for fix bug 5601 end 0116
					
					if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
						Toast.makeText(mContext, R.string.download_sd_error_1, Toast.LENGTH_SHORT).show();
						return;
					}
															
					NewSearchAppName.Application mSearchApp = (Application) TotalList.get(position);
					AppDownloadControl.prepareDownloadBySearch(mSearchApp,mContext);
					
					String btn_text =(String)holder.appDownload.getText();
					if(btn_text.equals(getString(R.string.download_download))){
						Log.i(TAG, "report hawaii download event, appId:"+ app.getAppId());
						/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-1-5 S*/
						//Reaper.reportSearch_Hawaii_DownlodEvent(mContext,app.getAppId(), getNetworkType(),app.getPackage_name(), app.getApp_versioncode());
						Reaper.processReaper( mContext, 
	                        	   Reaper.REAPER_EVENT_CATEGORY_APPLIST, 
	          					   Reaper.REAPER_EVENT_ACTION_APPLIST_SEARCH_DOWNLOAD,
	          					   app.getPackage_name(), 
	          					   Reaper.REAPER_NO_INT_VALUE );
						/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-1-5 E*/
					}
				}
			});
			
					
			
			
			
			holder.appName.setText(app.getAppName());			
//			holder.appName.setTextColor(SettingsValue.getIconTextStyleValue(mContext));
//			holder.appName.setShadowLayer(1.0f, 0, 0, Color.BLACK);
			
			
			holder.appSize.setText(app.getApp_size());
//			holder.appSize.setTextColor(SettingsValue.getIconTextStyleValue(mContext));
//			holder.appSize.setShadowLayer(1.0f, 0, 0, Color.BLACK);

			if (app.getApp_status() != null && !app.getApp_status().equals("")) {
				holder.appDownload.setText(app.getApp_status());
				if(app.getApp_status().equals(getString(R.string.download_pause))){

					mDrawable = mContext.getResources().getDrawable(R.drawable.ic_download_pause_normal);  
					mDrawable.setBounds(0, 0, w, h);
					holder.appDownload.setCompoundDrawables(null, mDrawable, null, null);
				}
				if(app.getApp_status().equals(getString(R.string.app_detail_install))){

					mDrawable = mContext.getResources().getDrawable(R.drawable.ic_download_install_normal);  
					mDrawable.setBounds(0, 0, w, h);
					
					holder.appDownload.setCompoundDrawables(null, mDrawable, null, null);
				}
				if(app.getApp_status().equals(getString(R.string.download_resume))){

					mDrawable = mContext.getResources().getDrawable(R.drawable.ic_download_coutinue_normal);  
					mDrawable.setBounds(0, 0, w, h);
					holder.appDownload.setCompoundDrawables(null, mDrawable, null, null);
				}
				if(app.getApp_status().equals(getString(R.string.download_download))){

					mDrawable = mContext.getResources().getDrawable(R.drawable.ic_download_normal);  
					mDrawable.setBounds(0, 0, w, h);
					holder.appDownload.setCompoundDrawables(null, mDrawable, null, null);
				}
				if(app.getApp_status().equals(getString(R.string.download_installed))){

					mDrawable = mContext.getResources().getDrawable(R.drawable.lejingpin_searchlist_run);  
					mDrawable.setBounds(0, 0, w, h);
					holder.appDownload.setCompoundDrawables(null, mDrawable, null, null);
				}
	
				
				
			} else {
				holder.appDownload.setText(R.string.download_download);

				mDrawable = mContext.getResources().getDrawable(R.drawable.ic_download_normal);  
				mDrawable.setBounds(0, 0, w, h);
				holder.appDownload.setCompoundDrawables(null, mDrawable, null, null);
				
			}
			
//			holder.appIspay.setTextColor(SettingsValue.getIconTextStyleValue(mContext));
//			holder.appIspay.setShadowLayer(1.0f, 0, 0, Color.BLACK);
			
			
			if (!app.getIsPay().equals("0")) {
				holder.appIspay.setText(app.getApp_price());
			}

			holder.appStar.setRating(Float.parseFloat(app.getStar_level()));

			final String url = app.getIcon_addr();
			Drawable cache = imgcache.get(url);
			holder.appIcon.setImageResource(R.drawable.push_app_icon_def);
			
			if(LejingpingSettingsValues.previewDownloadValue(mContext)) {
			
				if (cache == null) {
					
					loadImg(url, position, new OnImgLoadListener() {
	
						@Override
						public void onLoadComplete(final Integer position,final Drawable img) {
							
							imgcache.put(url, img);
	
							if (img != null) {
								((Activity) SearchActivityNew.this)
										.runOnUiThread(new Runnable() {
											@Override
											public void run() {
												if (holder.position == position) {
													holder.appIcon.setImageDrawable(img);
												}
											}
										});
							}
						}
					});
				} else {
					holder.appIcon.setImageDrawable(cache);
				}
			}
			return convertView;

		}
		
		public void releaseCache() {
			imgcache.clear();
		}
    	
    }
    
    
    
    
    public class  MyAdapter extends  BaseAdapter{
        /***RK_ID:RK_FONT AUT:zhanglz1@lenovo.com. DATE: 2013-02-21 S***/        

    	public MyAdapter() {
            if(tf == null)
            	tf = SettingsValue.getFontStyle(SearchActivityNew.this);
        }
        /***RK_ID:RK_FONT AUT:zhanglz1@lenovo.com. DATE: 2013-02-21 E***/        

    	public int getCount() {
            // TODO Auto-generated method stub
            return mDataList.size();
        }
 
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return mDataList.get(position);
        }
 
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }
 
 
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
             LayoutInflater inflater = getLayoutInflater();
             final View view = inflater.inflate(R.layout.search_app_item, parent, false);
             final ImageView icon = (ImageView) view.findViewById(R.id.icon);
             final TextView label = (TextView) view.findViewById(R.id.label);
//             ImageView button = (ImageView)view.findViewById(R.id.view_btn);
//             
//             LinearLayout m2 = (LinearLayout)view.findViewById(R.id.view_btn_container);
             
             LinearLayout m1 = (LinearLayout)view.findViewById(R.id.other_container);
             
             icon.setImageBitmap((Bitmap)mDataList.get(position).get("icon"));
             
             label.setText((SpannableString)mDataList.get(position).get("label"));
//             label.setTextColor(SettingsValue.getIconTextStyleValue(mContext));
//             label.setShadowLayer(1.0f, 0, 0, Color.BLACK);
             m1.setTag(position);
//             m2.setTag(position);
//             button.setTag(position);
             label.setTag(position);
             icon.setTag(position);
     		/*** RK_ID:RK_FONT AUT:zhanglz1@lenovo.com. DATE: 2013-02-21 S ***/
			if (tf != null && tf != label.getTypeface()) {
				label.setTypeface(tf);
			}
			/*** RK_ID:RK_FONT AUT:zhanglz1@lenovo.com. DATE: 2013-02-21 E ***/

//             button.setOnClickListener(myListener);
//             label.setOnClickListener(myListener); 
//             icon.setOnClickListener(myListener); 
             
             m1.setOnClickListener(myListener);
             //m2.setOnClickListener(myListener2);
             
             return view;
        }
    }
    
    
    OnClickListener myListener2 = new OnClickListener(){
        
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int position = Integer.parseInt(v.getTag().toString());
//            Toast.makeText(mContext, "定位", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(SettingsValue.ACTION_APP_TO_POSITION);
            intent.putExtra("intent", (Intent)mDataList.get(position).get("intent"));
            String sortKey = (String)mDataList.get(position).get("sortkey");
            Log.d(TAG, "sortKey = " + sortKey);
            intent.putExtra("sortkey", sortKey.charAt(0));
            Log.d(TAG, "send ACTION_APP_TO_POSITION");
            sendBroadcast(intent);
            finish();
             
    }};
    
    
    
    
    
    OnClickListener myListener = new OnClickListener(){
        
    public void onClick(View v) {
    	

        // TODO Auto-generated method stub
        int position = Integer.parseInt(v.getTag().toString());
        	
        	final Intent intent = (Intent) mDataList.get(position).get("intent");
            try {
                startActivity(intent);
                /*** RK_ID: USAGES_STATS.  AUT: zhaoxy . DATE: 2012-08-23 . START***/
//                LauncherApplication app = (LauncherApplication) mContext.getApplicationContext();
//                app.getModel().getUsageStatsMonitor().add(intent);
                /*** RK_ID: USAGES_STATS.  AUT: zhaoxy . DATE: 2012-08-23 . END***/
            } catch (ActivityNotFoundException e) {
                Toast.makeText(mContext, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
            } catch (SecurityException e) {
                Toast.makeText(mContext, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
            }
        
        
    }};
    

//    protected void setBackground() {
//            Uri appWallpaperUri = SettingsValue.getAppsWallperPath(mContext, true);
//            String awPath = appWallpaperUri == null ? null : appWallpaperUri.getPath();
//            
//            Bitmap appsWallpaper = null;
//            if (awPath == null) {
//                appsWallpaper = null;
//            } else {
//                appsWallpaper = BitmapFactory.decodeFile(awPath); 
//            }  
//            
//            if (appsWallpaper != null) {
//                mSearchParent.setBackgroundDrawable(new BitmapDrawable(appsWallpaper));
//            } else { 
//                
//                Resources res = mContext.getResources();
//                if (res == null) {
//                    return;
//                }
//                
//                LauncherApplication app = (LauncherApplication) mContext.getApplicationContext();
//                if (app == null) {
//                    return;
//                }
//                
//                Drawable b = app.mLauncherContext.getDrawable(R.drawable.all_apps_wallpaper);
//                mSearchParent.setBackgroundDrawable(b);//////////dooba
//                mSearchParent.invalidate();
//            }
//        }
    
    
    //yangmao add start
    
    private void hideIme() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mEditSearch.getWindowToken(), 0);
	}
    
	private  void requestNewSearch(final String query, final int startindex,final int clickTag) {
		DeviceInfo deviceInfo = DeviceInfo.getInstance(mContext.getApplicationContext());
		Log.i(TAG, "start AmsSession init");
		AmsSession.init(mContext, new AmsCallback() {
			@Override
			public void onResult(AmsRequest request, int code, byte[] bytes) {
				if (code != 200) {
					Log.i(TAG, "the network is error");					
					mHandler.sendEmptyMessage(HAWAII_REQUEST_CODE_ERROR);
				} else {
					Log.i(TAG, "getNewSearch");
					getNewSearch(query, startindex,clickTag);
				}
			}

		}, deviceInfo.getWidthPixels(), deviceInfo.getHeightPixels());
	}

	
	private void getNewSearch(final String query, final int startindex,final int clickTag) {
		
			
			int padOrPhone = SettingsValue.getCurrentMachineType(this);
		
			NewSearchAppName request = new NewSearchAppName(padOrPhone);
			request.setData(query);
			request.setStartIndex(startindex);
			Log.i(TAG, "set data is : " + query + "   the startIndex is :"+ startindex);
			AmsSession.execute(mContext, request, new AmsCallback() {
				@Override
				public void onResult(AmsRequest request, int code, byte[] bytes) {
					NewSearchResponse response = new NewSearchResponse(mContext);
	
					if (bytes != null) {
						Log.i(TAG, "............response.parseFrom(bytes).........");
						try {
							response.parseFrom(bytes);
						} catch (Exception e) {
							// TODO: handle exception
							Log.i(TAG, "catttttttttttttttt exception");
						}
						
					}
					if (!response.getIsSuccess() && response.getIsExpired()) {
						
						Log.i(TAG, "response.getIsExpired ");
						mHandler.sendEmptyMessage(SEND_CLIENTID_EXPIRED);
					}else if(!response.getIsSuccess()){
						
						mHandler.sendEmptyMessage(JSON_SEARCH_IS_FALSE);
					}
					
					if(response.getIsSuccess()){
						
						if(response.getApplicationItemCount()==0 && startindex == 0){					
							mHandler.sendEmptyMessage(HAWAII_SEARCH_NO_DATA);
						}else{
						
							searchResultList = response.getApplicationItemList();
											
							if(query.equals(mSearchContent)){
								
								Message msg = new Message();
								msg.what = SHOW_SEARCH_APP_LIST;
								Bundle mBundle = new Bundle();
								mBundle.putInt("CLICK_TAG", clickTag);
								msg.setData(mBundle);						
								mHandler.sendMessage(msg);
							}	
						}
					}
				}
	
			});
		
	}
    
	
	private void loadImg(final String url, final Integer position,
			final OnImgLoadListener callback) {

		if (TextUtils.isEmpty(url))
			return;

		AsyncTask.execute(new Runnable() {

			@Override
			public void run() {
				GetImageRequest imageRequest = new GetImageRequest();
				imageRequest.setData(url);
				AmsSession.execute(mContext, imageRequest, new AmsCallback() {

					@Override
					public void onResult(AmsRequest request, int code,final byte[] bytes) {

						Drawable drawable = null;
						ByteArrayInputStream bs = null;
						GZIPInputStream input = null;
						
						try {
							input = new GZIPInputStream(new ByteArrayInputStream(bytes));
							drawable = Drawable.createFromStream(input, null);
						} catch (Exception e) {
							// e.printStackTrace();
							bs = new ByteArrayInputStream(bytes);
							drawable = Drawable.createFromStream(bs, null);
						} finally {
							try {
								if (input != null) {
									input.close();
								}
								if (bs != null) {
									bs.close();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						callback.onLoadComplete(position, drawable);

					}
				});
			}
		});

	}
	
	
	private interface OnImgLoadListener {
		void onLoadComplete(Integer position, Drawable img);
	}
	
	
	static final class ViewHolder {
		TextView appName;
		ImageView appIcon;
		TextView appSize;
		TextView appIspay;
		TextView appDownload;
		RatingBar appStar;
		String packageName;
		String versionCode;
		int position;
		
		public ViewHolder() {
			super();
		}

		public ViewHolder(String packageName, String versionCode) {
			super();
			this.packageName = packageName;
			this.versionCode = versionCode;
		}

		@Override
		public boolean equals(Object o) {
			if(o instanceof ViewHolder && packageName!=null && versionCode!=null){
					ViewHolder vh = (ViewHolder) o;
					return packageName.equals(vh.packageName) && versionCode.equals(vh.versionCode);
				
			}else if(o instanceof ViewHolder && packageName!=null && versionCode ==null){
				ViewHolder vh = (ViewHolder) o;
				return packageName.equals(vh.packageName);
			}
			
			return false;
		}
	}
	

	
	private void showAppDetail_Hawaii_new(NewSearchAppName.Application hawaii_search_app) {		
		
		Intent intent = new Intent(this, HawaiiSearchAppInfoDialogFragment.class);
		intent.putExtra("Hawaii_Search_app", hawaii_search_app );
	    intent.putExtra("Hawaii_Search_packagename", hawaii_search_app.getPackage_name());
	    intent.putExtra("Hawaii_Search_versioncode", hawaii_search_app.getApp_versioncode());
	    intent.putExtra("Hawaii_Search_Appname", hawaii_search_app.getAppName());
	    intent.putExtra("Hawaii_Search_Iconaddr", hawaii_search_app.getIcon_addr());	    
	    intent.putExtra(HwConstant.EXTRA_CATEGORY, HwConstant.CATEGORY_HAWAII_SEARCH_APP);
	    
		try {
			startActivity(intent);
            /*** RK_ID: USAGES_STATS.  AUT: zhaoxy . DATE: 2012-08-23 . START***/
//            LauncherApplication app = (LauncherApplication) mContext.getApplicationContext();
//            app.getModel().getUsageStatsMonitor().add(intent);
            /*** RK_ID: USAGES_STATS.  AUT: zhaoxy . DATE: 2012-08-23 . END***/
		} catch (Exception e) {
			Toast.makeText(this, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
		}
	    
	}
	
	

	public void notifyDataChange(){
		mHawaiiSearchAdapter.notifyDataSetChanged();
	}
	
	
	 private void popupSeniorNetworkEnableDialog() {
		 if( !isNetworkEnabled()){
	    //	AlertDialog.Builder builder = new AlertDialog.Builder(this)
	    	LeAlertDialog alertDialog = new LeAlertDialog(this,R.style.Theme_LeLauncher_Dialog_Shortcut);
	    	alertDialog.setLeTitle(R.string.settings_network_dialog_title);
	    	alertDialog.setLeMessage(R.string.confirm_network_open);
	    	alertDialog.setLeNegativeButton(this.getString(R.string.cancel_action),new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	                dialog.dismiss();
	                Toast.makeText(mContext, R.string.version_update_toast, 1).show();
	                SearchActivityNew.this.finish();
	            }
	        });
	        alertDialog.setLePositiveButton(this.getString(R.string.rename_action),new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	                startConfirm();
	                dialog.dismiss();
	            }
	        });
	    	alertDialog.setOnCancelListener( new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
	                dialog.dismiss();
	                Toast.makeText(mContext, R.string.version_update_toast, 1).show();
	                SearchActivityNew.this.finish();
				}
	        	
	        });
	    	alertDialog.show();
		 }
	 }

	 private void startConfirm() {
	     Intent intent = new Intent();
	     intent.setClass(this, SeniorSettings.class);
	     startActivityForResult(intent, REQUEST_CONFIRM_SENIOR_NETWORK);
	 }
	    
	 protected void onActivityResult(final int requestCode, int resultCode, final Intent data) {
	     if (requestCode == REQUEST_CONFIRM_SENIOR_NETWORK ) {
	    	boolean bEnable = isNetworkEnabled();
	    	Log.i("zdx","bEnable:"+ bEnable);
	    	if( !bEnable ){
	    		Toast.makeText(this, R.string.version_update_toast, 1).show();
	    		((Activity)this).finish();
	    	}
	     }
	     else if (requestCode == VOICE_RECOGNITION_REQUEST_CODE)
	     {
	         if (resultCode == RESULT_OK) {
                 ArrayList<String> matches = data
                         .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//                 beginSearch(matches.get(0), FROM_AUDIO);
                 
                 if (mSearch)
                 {
                	 mEditSearch.setText(matches.get(0));
                 }
                 else
                 {
                	 mEditSearch.setText(matches.get(0));
                     netWorkSearch(search_click_tag);
                 }
	         }
	     }
	 }
	 private boolean isNetworkEnabled(){
//         SharedPreferences sharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
//         boolean isNetworkEnabled = sharedPreferences.getBoolean(SettingsValue.PREF_NETWORK_ENABLER, true);
//         return isNetworkEnabled;
		 
		 return Util.getInstance().isNetworkEnabled(mContext.getApplicationContext());
	 }
	
	
//	private static String getNetworkType() {
//		NetworkInfo info = mConnectManager.getActiveNetworkInfo();
//
//		String nettype = "";
//		if (info != null) {
//			nettype = info.getTypeName();
//		}
//		return nettype;
//	}
	
    //yangmao add end
		
	private void setaudioSearchListener()
	{
	    audioSearch.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                audioSearch.setEnabled(false);
                audioSearch.postDelayed(new Runnable() {
                    public void run() {
                        audioSearch.setEnabled(true);
                    }
                }, 1500);
                
                mEditSearch.setText("");

                
                List<ResolveInfo> activity = getPackageManager().
                        queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
                if(activity.size()>0){
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getResources().getString(R.string.audio_search));
                    intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "com.lenovo.leos.launcher");
                    startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);  
                }else{                                                           
                    MagicDownloadControl.Status status = MagicDownloadControl.queryDownloadStatus(mContext, "com.google.android.voicesearch", "214");
                    switch (status) {
                        case DOWNLOADING:
                            Toast.makeText(mContext, R.string.audio_search_download, Toast.LENGTH_SHORT).show();
                            break;
                        case PAUSE:
                            Toast.makeText(mContext, R.string.audio_search_download, Toast.LENGTH_SHORT).show();
                            //启动
                            
                            if("mobile".equals(HwConstant.getConnectType(mContext)) && LejingpingSettingsValues.wlanDownloadValue(mContext)) {				
                 				popupWlanDownloadDialog();
                 			}else{                            
	                            MagicDownloadControl.downloadFromCommon (mContext, 
	                                    "com.google.android.voicesearch",  
	                                    "214", 
	                                    DownloadConstant.CATEGORY_COMMON_APP | DownloadConstant.CATEGORY_LENOVO_APK, 
	                                    getString(R.string.audio_search), 
	                                    null, 
	                                    mAddress, 
	                                    HwConstant.MIMETYPE_APK,
	                                    "true", 
	                                    "true");
                 			}
                            break;
                        case UNINSTALL:
                            Toast.makeText(mContext, R.string.audio_search_install, Toast.LENGTH_SHORT).show();
                            //提示安装
                            MagicDownloadControl.downloadFromCommon (mContext, 
                                    "com.google.android.voicesearch",  
                                    "214", 
                                    DownloadConstant.CATEGORY_COMMON_APP | DownloadConstant.CATEGORY_LENOVO_APK, 
                                    getString(R.string.audio_search), 
                                    null, 
                                    mAddress, 
                                    HwConstant.MIMETYPE_APK,
                                    "true", 
                                    "true");
                            break; 
                        case UNDOWNLOAD:
                        	 if("mobile".equals(HwConstant.getConnectType(mContext)) && LejingpingSettingsValues.wlanDownloadValue(mContext)) {				
                 				popupWlanDownloadDialog();
                 			}else{
                 				showWarningDialog();
                 			}
                            break;
                        default:
                        	 if("mobile".equals(HwConstant.getConnectType(mContext)) && LejingpingSettingsValues.wlanDownloadValue(mContext)) {				
                  				popupWlanDownloadDialog();
                  			}else{
                  				showWarningDialog();
                  			}
                            break;
                    }                    
                }
            }
        });
	}
	
	public void netWorkSearch(final int clickTag) {
        // TODO Auto-generated method stub
		
		Log.i(TAG, "networksearch method");
        mSearchContent = mEditSearch.getText().toString().trim();
        if (!mSearchContent.isEmpty()) {
        	
        	previousSearchContent = mSearchContent;
        	
        	addKeywordToHistory(mSearchContent);
            startindex = 0;
            TotalList.clear();
            hideIme();
            foottag =true;
            STARTINDEX_TAG=true;
            if(mHawaii_search_linearlayout.getVisibility()==View.GONE){
                mHawaii_search_linearlayout.setVisibility(View.VISIBLE);
            }
            
            mNetSearchNodataLayout.setVisibility(View.GONE);
            
            mLoadingLayout.setVisibility(View.VISIBLE);

            if (app_listView.getVisibility() == View.GONE) {
                app_listView.setVisibility(View.VISIBLE);
            }

            mHawaiiSearchAdapter.releaseCache();
            downloadInfos = LDownloadManager.getDefaultInstance(mContext.getApplicationContext()).getAllDownloadInfo();

            SearchActivityNew.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                  requestNewSearch(mSearchContent, startindex,clickTag);
                }
            });

        } else {
            Toast.makeText(mContext,R.string.hawaii_search_content_isnull,Toast.LENGTH_SHORT).show();
        }

    }
	
	protected void showWarningDialog() {
    	LeAlertDialog alertDialog = new LeAlertDialog(this,R.style.Theme_LeLauncher_Dialog_Shortcut);
    	alertDialog.setLeTitle(R.string.settings_network_dialog_title);
        alertDialog.setLeMessage(R.string.audio_search_summary);
        alertDialog.setLePositiveButton(this.getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 startDownloadVoiceSearch();
                 dialog.dismiss();
             }
         });
        alertDialog.setLeNegativeButton(this.getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                 dialog.cancel();
            }
        });

    	alertDialog.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.cancel();
                }
                return false;
            }
        });

    	alertDialog.show();
    }
	
	public final static String  mAddress = "http://launcher.lenovo.com/launcher/data/attachment/app/voiceSearch.apk";
	
	public void startDownloadVoiceSearch()
	{
//      Toast.makeText(mContext, "请下载语音插件", Toast.LENGTH_SHORT).show();
//      String address = LauncherService.LENOVO_HOME_URL;
//      address = LauncherService.LENOVO_APPSTORE_URL+"com.google.android.voicesearch";
//      Uri uri = Uri.parse(address);
//      startActivity(new Intent(Intent.ACTION_VIEW, uri));
      
//      Intent intent = new Intent(HwConstant.ACTION_APP_UPGRADE);
//      
//      intent.putExtra("package_name", "com.google.android.voicesearch");
//      intent.putExtra("version_code", "214");
//      intent.putExtra("app_name", "yuyin");
//      intent.putExtra("category", "50");
//      intent.putExtra("app_iconurl", "http://test2.surepush.cn/appstore/psl/com.google.android.voicesearch/214");
//
//      sendBroadcast(intent);
      
//	    Uri uri = Uri.parse(mAddress);
//	    startActivity(new Intent(Intent.ACTION_VIEW, uri));
	    MagicDownloadControl.downloadFromCommon (mContext, 
	                                            "com.google.android.voicesearch",  
	                                            "214", 
	                                            DownloadConstant.CATEGORY_COMMON_APP | DownloadConstant.CATEGORY_LENOVO_APK, 
	                                            getString(R.string.audio_search), 
	                                            null, 
	                                            mAddress, 
	                                            HwConstant.MIMETYPE_APK,
	                                            "true", 
	                                            "true");
	}
	
	
	
	//yangmao add
	
    private void InitViewPager() {
        mPager = (ViewPager) findViewById(R.id.vPager_search_new);
        listViews = new ArrayList();
        mpAdapter = new MyPagerAdapter();

        listViews.add(view0);
        listViews.add(view1);
        
        mPager.setAdapter(mpAdapter);
        mPager.setCurrentItem(0);
        
        mPager.setAdapter(mpAdapter);
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
        
        mContainerParent.setVisibility(View.GONE);
        mProgressBarReci.setVisibility(View.VISIBLE);
        
        
        
//      if(mHotLableListString!=null && mHotLableListString.size()!=0){
//    	Log.i(TAG, "hotlable is not null so direct show");
//    	mHandler.sendEmptyMessage(GET_HOT_LABLE);
//      }else{        
//        Log.i(TAG, "send hot request");
//        mHandler.sendEmptyMessage(REQUEST_HOT);
//        }
//      loadHotList();
        
      if(mHotLableListString!=null && mHotLableListString.size()!=0){
      	Log.i(TAG, "hotlable is not null so direct show");
    	  mHandler.sendEmptyMessage(GET_HOT_LABLE);
    	 
      }else{
    	  Log.i(TAG, "start load HOT list");
    	  loadHotList();
      }           
        loadSearchHistory();

        
    }
	
    
    
	private void replaceView(int index) {

		mpAdapter.destroyItem(mPager, index, null);
		listViews.clear();
		listViews.add(view2);
		listViews.add(view3);
		mpAdapter.instantiateItem(mPager, index);
		mPager.setAdapter(mpAdapter);
		mPager.setCurrentItem(index);
		
		if(index ==0){		
			mSearchButton.setSelected(true);
			mNetworkSearchButton.setSelected(false);
			//why step this because the seconde search same text the netsearch result will not show 
			preShowSearchLoadView();
		}else if(index ==1){
			mSearchButton.setSelected(false);
			mNetworkSearchButton.setSelected(true);
		}
//		preShowSearchLoadView();
		Log.e(TAG,"replaceView 11111111111111111111111 size=" + listViews.size());

	}
    
    
    
	private void replaceView2(int index) {

		mpAdapter.destroyItem(mPager, index, null);
		listViews.clear();
		listViews.add(view0);
		listViews.add(view1);
		mpAdapter.instantiateItem(mPager, index);
		mPager.setAdapter(mpAdapter);
		mPager.setCurrentItem(index);
		Log.e(TAG, "replaceView 222222222222222 size=" + listViews.size());
		mReciButton.setSelected(true);
		mHistoryButton.setSelected(false);
		preShowHistory();

	}
    
    
    
    
    
    /**
     * ViewPager适配器
   */
   public class MyPagerAdapter extends PagerAdapter {
        //public List mListViews;

/*
      public MyPagerAdapter(List mListViews) {
           this.mListViews = mListViews;
      }
*/
       public MyPagerAdapter() {
            //this.mListViews = listViews;
       }
       @Override
       public void destroyItem(View arg0, int arg1, Object arg2) {
    	   if(arg0 instanceof ViewPager){    	   
    		   ((ViewPager) arg0).removeView(((View)listViews.get(arg1)));
    	   }
       }
       @Override
       public void finishUpdate(View arg0) {
       }
       @Override
       public int getCount() {
           //Log.e(TAG,"getouCount 2222222222222222 size======="+listViews.size());
           return listViews.size();
       }
       @Override
       public Object instantiateItem(View arg0, int arg1) {
           try {
        	   
        	   if(arg0 instanceof ViewPager){        	   
        		   ((ViewPager) arg0).addView(((View)listViews.get(arg1)), 0);
        	   }
           } catch (Exception e) {

                              
           }
           return  listViews.get(arg1);
           //((ViewPager) arg0).addView(((View)listViews.get(arg1)), 0);
           //return listViews.get(arg1);
       }

       @Override
       public boolean isViewFromObject(View arg0, Object arg1) {
           return arg0 == (arg1);
       }
     

       @Override
       public void restoreState(Parcelable arg0, ClassLoader arg1) {
       }

       @Override
       public Parcelable saveState() {
           return null;
       }

       @Override
       public void startUpdate(View arg0) {
       }
   }
	
   
   
   /**
    * 页卡切换监听
   */
	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
		int two = one * 2;// 页卡1 -> 页卡3 偏移量

		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			Log.d(TAG, "---OnPageChangeListener--- case arg0=" + arg0+ "   currIndex=" + currIndex);

			switch (arg0) {
			case 0:
				Log.i(TAG, "case 0");
				mSearchButton.setSelected(true);
				mNetworkSearchButton.setSelected(false);
				mReciButton.setSelected(true);
				mHistoryButton.setSelected(false);

				if (((View) listViews.get(0)).getTag() == null) {
					Log.i(TAG, "onpageChange the second view change so user localSearchDo");
					
					Log.i("getdata", "localSearchDo 111");
					isNetSearchToLocalSearch = true;
					localSearchDo();
				}

				if (currIndex == 1) {

					Log.i(TAG, "currIndex == 1");

					animation = new TranslateAnimation(one, 0, 0, 0);
				} else if (currIndex == 2) {
					Log.i(TAG, "currIndex == 2");
					animation = new TranslateAnimation(two, 0, 0, 0);
				}

				break;
				
			case 1:
				Log.i(TAG, "case 1");
				mNetworkSearchButton.setSelected(true);
				mSearchButton.setSelected(false);
				mReciButton.setSelected(false);
				mHistoryButton.setSelected(true);

				if(!isNetworkEnabled()){
					popupSeniorNetworkEnableDialog();
					return;
				}
				
				Log.i(TAG," view0.getTag() is:"+ ((View) listViews.get(0)).getTag());

				Log.i(TAG,"history size is:" + mHistoryKeyword.size());
				if (((View) listViews.get(0)).getTag() == null) {
					Log.i(TAG, "onpageChange the second view change so user netSearchDo");
					netSearchDo();

				} else {
					Log.i(TAG, "step loadSearchHistory");
					if (mHistoryKeyword != null && mHistoryKeyword.size() != 0) {

						searchHistoryList.setVisibility(View.VISIBLE);

						if (searchHistoryList.getFooterViewsCount() == 0) {
							searchHistoryList.addFooterView(searchHistoryFooter);
						}

						mTextViewNoHistory.setVisibility(View.GONE);

					}
					loadSearchHistory();
				}
				if (currIndex == 0) {
					Log.i(TAG, "currIndex == 0");
					animation = new TranslateAnimation(offset, one, 0, 0);
				} else if (currIndex == 2) {
					Log.i(TAG, "currIndex == 2");
					animation = new TranslateAnimation(two, one, 0, 0);
				}

				break;
				
				default :
					
					break;

			}
			if (animation == null){
				return;
			}	
			currIndex = arg0;
			// animation.setFillAfter(true);// True:图片停在动画结束位置
			// animation.setDuration(100);
			// cursor.startAnimation(animation);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}
   
	
	
	
	private void delayCreateView() {
		Log.i(TAG, "delayCreateView");
		if(PadOrPhone!=-1){
			InitOverlay(); 
		}
		view0 = mLi.inflate(R.layout.search_list_reci_ym, null);
		view0.setTag("reci");
		initReciLayout();

		view1 = mLi.inflate(R.layout.search_list_histroy_ym, null);
		mSearchHistoryAdapter = new SearchHistoryAdapter();
		searchHistoryList = (ListView) view1.findViewById(R.id.search_history_list_ym);
		mTextViewNoHistory = (TextView) view1.findViewById(R.id.text_no_history);
		searchHistoryFooter = getLayoutInflater().inflate(R.layout.search_history_footer, searchHistoryList, false);
		searchHistoryList.addFooterView(searchHistoryFooter); 															
		searchHistoryList.setOnItemClickListener(new SearchHistoryItemClickListener());
		searchHistoryList.setAdapter(mSearchHistoryAdapter);

		

		if (mHistoryKeyword != null && mHistoryKeyword.size() == 0) {
			searchHistoryList.setVisibility(View.GONE);
			mTextViewNoHistory.setVisibility(View.VISIBLE);

		}

		view2 = mLi.inflate(R.layout.search_list_local_search_ym, null);
		mNumMsg = (TextView) view2.findViewById(R.id.num_msg);
		mDivider2 = (ImageView) view2.findViewById(R.id.header_divider2);
		
		mLocalSearchNodataImg = (ImageView)view2.findViewById(R.id.local_no_data_img);
		mLocalSearchNodataText= (TextView)view2.findViewById(R.id.loacal_no_data_text);
		mLoacalSearchNodataBtn= (Button)view2.findViewById(R.id.loacal_no_data_btn);
		searchListView = (SearchListView) view2.findViewById(R.id.list);

		view3 = mLi.inflate(R.layout.search_list_net_search_ym, null);
		//yangmao add
		view3.setTag("netsearch");
		if(PadOrPhone==-1){
			app_listView = (ListView) view3.findViewById(R.id.listview);
		}else{
			app_listView = (GridView) view3.findViewById(R.id.listview);
		}
		mTextView_nodata = (TextView) view3.findViewById(R.id.nodata);
		
		mNetSearchNodataImg = (ImageView)view3.findViewById(R.id.net_no_data_img);
		mNetSearchNodataText= (TextView)view3.findViewById(R.id.net_no_data_text);
		mNetSearchNodataBtn= (Button)view3.findViewById(R.id.net_no_data_btn);
		mNetSearchNodataLayout = (RelativeLayout)view3.findViewById(R.id.net_search_nodata_layout);
		mLoadingLayout = (LinearLayout) view3.findViewById(R.id.detail_watting);
		mHawaii_search_linearlayout = (LinearLayout) view3.findViewById(R.id.hawaii_search_list);
		
		
		mLoacalSearchNodataBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mPager.setCurrentItem(1);
			}
		});
		
		
		
		mNetSearchNodataBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mNetSearchNodataBtn.getText().equals(getString(R.string.net_search_nodata_refresh))){
//					netSearchDo();
					netWorkSearch(search_click_tag);
					
				}else if(mNetSearchNodataBtn.getText().equals(getString(R.string.net_search_nodata_set_net))){

					Intent intent = new Intent(
							android.provider.Settings.ACTION_SETTINGS);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
					startActivity(intent);
					
					mPager.setCurrentItem(0);
					
				}
				
			}
		});
		
		
		
		InitViewPager();

	}
   
   
   
	
	
	
	
   
	private void localSearchDo() {
		if (!mSearch) {
			mSearchButton.setSelected(true);
			mNetworkSearchButton.setSelected(false);
			mEditSearch.setText(mEditSearch.getText());
			
			mPager.setCurrentItem(0);
			searchListView.setVisibility(View.VISIBLE);
			//mNumMsg.setVisibility(View.VISIBLE);
			
			CharSequence text = mEditSearch.getText();
			if (text instanceof Spannable) {
				Spannable spanText = (Spannable) text;
				Selection.setSelection(spanText, text.length());
			}

			// yangmao add for bug 170635
			if(PadOrPhone==-1){	
				if (((ListView)app_listView) != null && ((ListView)app_listView).getFooterViewsCount() != 0) {
					((ListView)app_listView).removeFooterView(mFootView);
				}
			}
			mSearch = true;
		}
	}
	
	
   
   private void netSearchDo(){
	   Log.i(TAG, "netSearchDo");

       if (mSearch)
       {	
			mSearchButton.setSelected(false);
			mNetworkSearchButton.setSelected(true);
			mPager.setCurrentItem(1);
			searchListView.setVisibility(View.GONE);
           
           CharSequence text = mEditSearch.getText();
           if (text instanceof Spannable) {
               Spannable spanText = (Spannable)text;
               Selection.setSelection(spanText, text.length());
           }
           mNumMsg.setVisibility(View.GONE);
           mDivider2.setVisibility(View.GONE);
           mSearch = false;
           popupSeniorNetworkEnableDialog();
           downloadInfos = LDownloadManager.getDefaultInstance(mContext.getApplicationContext()).getAllDownloadInfo();

           Log.i(TAG, "previousSearchContent is:"+previousSearchContent);
           
           if(DownloadConstant.getConnectType(mContext)==DownloadConstant.CONNECT_TYPE_OTHER){
        	   
        	   mHandler.sendEmptyMessage(NO_NET_CONNECT);
        	   
           }else{
        	   
        	   mNetSearchNodataLayout.setVisibility(View.GONE);
        	   Log.i(TAG, "llllllllllllllll11111111111");
	           if(previousSearchContent!=null && previousSearchContent.equals(mEditSearch.getText().toString().trim())){
	        	   Log.i(TAG, "some times the previous search key is equal,but the Toatallis size is 0 so netsearch again");
	        	   if(TotalList==null || TotalList.size()==0 ){
	        		   Log.i(TAG, "6666666666666666");
	        		   netWorkSearch(search_click_tag);
	        	   }
	           }    
	           Log.i(TAG, "llllllllllllllll2222222222");
	           if(previousSearchContent ==null){
	        	   Log.i(TAG, "previous is null so the first search");
	        	   netWorkSearch(search_click_tag);        	   
	           }
	           Log.i(TAG, "llllllllllllllll333333333333");
	           if(previousSearchContent!=null && !previousSearchContent.equals(mEditSearch.getText().toString().trim())){
	           	Log.i(TAG, "step this");
	           	previousSearchContent = mEditSearch.getText().toString().trim();
	           	netWorkSearch(search_click_tag);
	           }
	           //Log.i(TAG, "llllllllllllllll44444444444444 TotaList is:"+TotalList);
	           
	           Log.i(TAG, "llllllllllllllll44444444444444");
	           //yangmao add for search 0219 start
	           if(TotalList!=null && TotalList.size()!=0 ){
	           	Log.i(TAG, "===========net click======= totalist is not null && size is not 0");
	           	mLoadingLayout.setVisibility(View.GONE);
	           	mHawaii_search_linearlayout.setVisibility(View.VISIBLE);
	           	app_listView.setVisibility(View.VISIBLE);
	           	//modify this ,because local and net change,getview will step in,may 
	           	//delay the view
	           	//mHawaiiSearchAdapter.notifyDataSetChanged();
	           }
				//           else{ 
				//        	   Log.i(TAG, "step 9999999999999999");
				//           	netWorkSearch(search_click_tag);
				//           }
           
           }
       }
	   
   }
	
   
   
	final class SearchHistoryAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mHistoryKeyword == null ?  0 : mHistoryKeyword.size() ;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final SearchHistoryHolder holder ;
			if( convertView != null) {
				holder = (SearchHistoryHolder) convertView.getTag();
			} else {
				holder = new SearchHistoryHolder();
				convertView = getLayoutInflater().inflate(R.layout.search_history_item, parent, false);
//				holder.searchHistoryItemAdd = (ImageView) convertView.findViewById(R.id.search_history_item_add);
				holder.searchHistoryItemKeyword = (TextView) convertView.findViewById(R.id.search_history_item_keyword);
				convertView.setTag(holder);
			}
			String keyword = mHistoryKeyword.get(position);
			Log.d(TAG, "getView >>>>>> keyword: " + keyword);
			holder.searchHistoryItemKeyword.setText(keyword);
			holder.searchHistoryItemKeyword.setSelected(true);	//used for text view's Scrolling effect 
//			holder.searchHistoryItemAdd.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					String keyword = holder.searchHistoryItemKeyword.getText().toString();
//					mEditSearch.setText(keyword);
//					mEditSearch.setSelection(keyword.length());
//				}
//			});
			return convertView;
		}
	}
   
   
	static final class SearchHistoryHolder {
		ImageView searchHistoryItemAdd;
		TextView searchHistoryItemKeyword;
	}
   
   
   
	private void loadSearchHistory(){
		new Thread(){
			@Override
			public void run() {
				SharedPreferences searchHistoryShare = getSharedPreferences(SEARCH_HISTORY_SHARE_NAME, Context.MODE_PRIVATE);
				String tmp = searchHistoryShare.getString(SEARCH_HISTORY_SHARE_KEY, null);
				Log.i(TAG, "loadSearchHistory tmp is:"+tmp);
				if( tmp != null && tmp.length() != 0 ) {
					String[] historyArray = tmp.split(",");
					Message msg = mHandler.obtainMessage(SEARCH_MSG_HISTORY_LOAD_COMPLETED);
					msg.obj = historyArray;
					mHandler.sendMessage(msg);
				}
			}
		}.start();
	}
   
   
	
	
	private void showSearchHistory() {
		
		Log.i(TAG, "showHistory size is:"+mHistoryKeyword.size());
		
		if( mHistoryKeyword.size() != 0 ) {
			if(searchHistoryList.getFooterViewsCount() == 0 ) {
				searchHistoryList.addFooterView(searchHistoryFooter);
			}
			mTextViewNoHistory.setVisibility(View.GONE);
			searchHistoryList.setVisibility(View.VISIBLE);	//do NOT need to care about history_footer view
		} else if(searchHistoryList.getFooterViewsCount() != 0 ) {
			searchHistoryList.removeFooterView(searchHistoryFooter);
		}{
			
		}
	}
   
	
	private void saveSearchHistory(final String keyword) {
		Log.d(TAG, "saveSearchHistory >>>> keyword: " + keyword);
//		new Thread(){
//			@Override
//			public void run() {
//				super.run();
				SharedPreferences searchHistoryShare = getSharedPreferences(SEARCH_HISTORY_SHARE_NAME, Context.MODE_PRIVATE);
				searchHistoryShare.edit().putString(SEARCH_HISTORY_SHARE_KEY, keyword).commit();
//			}
//		}.start();
	}
	
	
	private void addKeywordToHistory(String keyword) {
		Log.d(TAG, "addKeywordToHistory >>> keyword: "+keyword);
		if( mHistoryKeyword.contains(keyword) )
			mHistoryKeyword.remove(keyword);	//this keyword has already in the list
		if(mHistoryKeyword.size() == SEARCH_HISTORY_MAX_LENGTH )
			mHistoryKeyword.remove(SEARCH_HISTORY_MAX_LENGTH-1);	//the list is full,remove the oldest one
		
		mHistoryKeyword.add(0, keyword);
		mSearchHistoryAdapter.notifyDataSetChanged();
	}
	
	private void clearSearchHistory() {
		mHistoryKeyword.clear();
		searchHistoryList.removeFooterView(searchHistoryFooter);	
		searchHistoryList.setVisibility(View.GONE);
		mTextViewNoHistory.setVisibility(View.VISIBLE);
		
		mSearchHistoryAdapter.notifyDataSetChanged();

		new Thread(){
			@Override
			public void run() {
				SharedPreferences searchHistoryShare = getSharedPreferences(SEARCH_HISTORY_SHARE_NAME, Context.MODE_PRIVATE);
				searchHistoryShare.edit().remove(SEARCH_HISTORY_SHARE_KEY).commit();
			}
		}.start();
	}
	
   
	final class SearchHistoryItemClickListener implements AdapterView.OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			SearchHistoryHolder holder = (SearchHistoryHolder) view.getTag();
			if( holder != null ) {
				mKeyword = holder.searchHistoryItemKeyword.getText().toString();
				mEditSearch.setText(mKeyword);
				//yangmao add fix bug 9013
				CharSequence text = mEditSearch.getText();
				if (text instanceof Spannable) {
					Spannable spanText = (Spannable) text;
					Selection.setSelection(spanText, text.length());
				}
				
				
			} else {
				showClearHistoryDialog();
			}
		}
	}
	
	
	
	private void showClearHistoryDialog(){
		//AlertDialog.Builder's constructor need a Activity as parameter
    	LeAlertDialog alertDialog = new LeAlertDialog(this,R.style.Theme_LeLauncher_Dialog_Shortcut);
		alertDialog.setLeTitle(R.string.search_history_deleteall_dialog_title);
		alertDialog.setLeMessage(R.string.search_history_deleteall_dialog_message);
		alertDialog.setLePositiveButton(this.getString(R.string.search_history_deleteall_dialog_confirm), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					clearSearchHistory();
				}
			});
		alertDialog.setLeNegativeButton(this.getString(R.string.search_history_deleteall_dialog_concel), null);
		alertDialog.show();
	}
	
	
	private void initReciLayout() {

		mContainer = (GridLayout) view0.findViewById(R.id.container);
		mProgressBarReci = (ProgressBar) view0.findViewById(R.id.search_loading_reci);
		mTextViewReciNodata = (TextView) view0.findViewById(R.id.search_nodata_reci);
		mContainerParent = (LinearLayout) view0.findViewById(R.id.container_parent);
		mButtonChange = (Button) view0.findViewById(R.id.btn_change);

		mTextView1 = (TextView) view0.findViewById(R.id.tv1);
		mTextView2 = (TextView) view0.findViewById(R.id.tv2);
		mTextView3 = (TextView) view0.findViewById(R.id.tv3);
		mTextView4 = (TextView) view0.findViewById(R.id.tv4);

		mTextView5 = (TextView) view0.findViewById(R.id.tv5);
		mTextView6 = (TextView) view0.findViewById(R.id.tv6);
		mTextView7 = (TextView) view0.findViewById(R.id.tv7);
		mTextView8 = (TextView) view0.findViewById(R.id.tv8);

		mTextView9 = (TextView) view0.findViewById(R.id.tv9);
		mTextView10 = (TextView) view0.findViewById(R.id.tv10);
		mTextView11 = (TextView) view0.findViewById(R.id.tv11);
		mTextView12 = (TextView) view0.findViewById(R.id.tv12);

		mTextView13 = (TextView) view0.findViewById(R.id.tv13);
		mTextView14 = (TextView) view0.findViewById(R.id.tv14);
		mTextView15 = (TextView) view0.findViewById(R.id.tv15);
		mTextView16 = (TextView) view0.findViewById(R.id.tv16);

		textview[0] = mTextView1;
		textview[1] = mTextView2;
		textview[2] = mTextView3;
		textview[3] = mTextView4;
		textview[4] = mTextView5;
		textview[5] = mTextView6;
		textview[6] = mTextView7;
		textview[7] = mTextView8;
		textview[8] = mTextView9;
		textview[9] = mTextView10;
		textview[10] = mTextView11;
		textview[11] = mTextView12;
		textview[12] = mTextView13;
		textview[13] = mTextView14;
		textview[14] = mTextView15;
		textview[15] = mTextView16;

		listTextView.add(mTextView1);
		listTextView.add(mTextView2);
		listTextView.add(mTextView3);
		listTextView.add(mTextView4);
		listTextView.add(mTextView5);
		listTextView.add(mTextView6);
		listTextView.add(mTextView7);
		listTextView.add(mTextView8);
		listTextView.add(mTextView9);
		listTextView.add(mTextView10);
		listTextView.add(mTextView11);
		listTextView.add(mTextView12);
		listTextView.add(mTextView13);
		listTextView.add(mTextView14);
		listTextView.add(mTextView15);
		listTextView.add(mTextView16);

		mColorList.add(getResources().getColor(R.color.hot_word_color_black));
		mColorList.add(getResources().getColor(R.color.hot_word_color_blue));
		mColorList.add(getResources().getColor(R.color.hot_word_color_green));
		mColorList.add(getResources().getColor(R.color.hot_word_color_pink));
		mColorList.add(getResources().getColor(R.color.hot_word_color_red));

		for (int i = 0; i < textview.length; i++) {
			textview[i].setOnClickListener(new gridlistener());
		}

		mButtonChange.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				//Log.i(TAG,"mHotLableList size is:" + mHotLableList.size()+ "; result is:" + mHotLableList.size() / 16);

				Collections.shuffle(listTextView);
				Collections.shuffle(mColorList);

				hotTag++;

/*				if (mHotLableList.size() / 16 == 1) {
					hotTag = 0;
					Log.i(TAG, "stejjjjjjjjjj is:" + hotTag);

					for (int i = 0; i < textview.length; i++) {

						listTextView.get(i).setText(mHotLableList.get(i + 16 * hotTag).getKeyname());
						listTextView.get(i).setBackgroundColor(mColorList.get(i % 5));
					}

				} else {
					if (hotTag != 1 && hotTag == mHotLableList.size() / 16) {
						hotTag = 0;
					}
					for (int i = 0; i < textview.length; i++) {
						listTextView.get(i).setText(mHotLableList.get(i + 16 * hotTag).getKeyname());
						listTextView.get(i).setBackgroundColor(mColorList.get(i % 5));
					}

				}*/
				
			if (mHotLableListString.size() / 16 == 1) {
				hotTag = 0;
				Log.i(TAG, "stejjjjjjjjjj is:" + hotTag);

				for (int i = 0; i < textview.length; i++) {

					listTextView.get(i).setText(mHotLableListString.get(i + 16 * hotTag));
					listTextView.get(i).setBackgroundColor(mColorList.get(i % 5));
				}

			} else {
				if (hotTag != 1 && hotTag == mHotLableListString.size() / 16) {
					hotTag = 0;
				}
				for (int i = 0; i < textview.length; i++) {
					listTextView.get(i).setText(mHotLableListString.get(i + 16 * hotTag));
					listTextView.get(i).setBackgroundColor(mColorList.get(i % 5));
				}

			}
				
				
				

			}

		});

	}
	
	
	
	
	private void requestHot() {
		DeviceInfo deviceInfo = DeviceInfo.getInstance(mContext.getApplicationContext());
		Log.i(TAG, "start requestHot AmsSession init");
		AmsSession.init(mContext, new AmsCallback() {
			@Override
			public void onResult(AmsRequest request, int code, byte[] bytes) {
				if (code != 200) {
					Log.i(TAG, "the network is error");
					// not do ui
					mHandler.sendEmptyMessage(RECI_REQUEST_ERROR);

				} else {
					Log.i("yangmao", "getHot");
					getHot();
				}
			}

		}, deviceInfo.getWidthPixels(), deviceInfo.getHeightPixels());
	}

	private void getHot() {
		Log.i(TAG, "getHot method");
		GetHotRequestFD request = new GetHotRequestFD(mContext);

		AmsSession.execute(mContext, request, new AmsCallback() {
			@Override
			public void onResult(AmsRequest request, int code, byte[] bytes) {
				GetHotResponse response = new GetHotResponse();
				if (bytes != null) {
					response.parseFrom(bytes);
				}
				Log.i(TAG, "sucess is:" + response.getIsSuccess());
				boolean isSuccess = response.getIsSuccess();
				if (isSuccess) {
					
					
//					if(mHotLableList!=null && mHotLableList.size()!=0){
//						Log.i(TAG, "letHotlablist clean");
//						mHotLableList.clear();
//					}
//					
//					mHotLableList = response.getItemList();
					
					
					if(mHotLableListString!=null && mHotLableListString.size()!=0){
						Log.i(TAG, "letHotlablistString clean");
						mHotLableListString.clear();
					}
				
					mHotLableListString = response.getItemListString();
					

					mHandler.sendEmptyMessage(GET_HOT_LABLE);

				} else {
					mHandler.sendEmptyMessage(RECI_REQUEST_ERROR);
				}

			}

		});
	}
	
	
	class gridlistener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			Log.i(TAG, "text onclick");
			if(v instanceof TextView){
				TextView mTextView = (TextView) v;
				
				if (mTextView.getText() != null && !mTextView.getText().equals("")) {
	
					mKeyword = mTextView.getText().toString();
					mEditSearch.setText(mKeyword);
					//yangmao add fix bug 9013
					CharSequence text = mEditSearch.getText();
					if (text instanceof Spannable) {
						Spannable spanText = (Spannable) text;
						Selection.setSelection(spanText, text.length());
					}
	
				}

			}

		}

	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

		Log.i(TAG, "onbackPressed");
		Log.i(TAG, "getText is :"+ mEditSearch.getText().toString().trim());
		if (mEditSearch.getText().toString().trim().equals("")) {
			Log.i(TAG, "super.onback");
			super.onBackPressed();
		}
		hideIme();
		Log.i(TAG, "is focused");
		if (!mEditSearch.getText().toString().trim().equals("")) {
			mEditSearch.setText("");
		}
	}
	
	
	private void preShowHistory(){
		Log.i(TAG, "preshow history");
		if (((View) listViews.get(0)).getTag() != null) {
			if (mHistoryKeyword != null && mHistoryKeyword.size() != 0) {
				Log.i(TAG, "presho history method set visible");
				searchHistoryList.setVisibility(View.VISIBLE);
	
				if (searchHistoryList.getFooterViewsCount() == 0) {
					searchHistoryList.addFooterView(searchHistoryFooter);
				}
	
				mTextViewNoHistory.setVisibility(View.GONE);
	
			}
		}
	}
	
	
	
	private void preShowSearchLoadView(){
		Log.i(TAG, "preshowSearchLoadview");
		mNetSearchNodataLayout.setVisibility(View.GONE);
		mTextView_nodata.setVisibility(View.GONE);
		mLoadingLayout.setVisibility(View.VISIBLE);
	}
	
	public static int dip2px(Context context, float dipValue){ 
		final float scale = context.getResources().getDisplayMetrics().density; 
		return (int)(dipValue * scale + 0.5f); 
	} 
	
	
	
	 private void popupWlanDownloadDialog() {

	    	LeAlertDialog alertDialog = new LeAlertDialog(this,R.style.Theme_LeLauncher_Dialog_Shortcut);
	    	alertDialog.setLeTitle(R.string.lejingpin_wlan_download_dialog_title);
	    	alertDialog.setLeMessage(R.string.lejingpin_wlan_download_dialog_body);
	    	alertDialog.setLeNegativeButton(this.getString(R.string.lejingpin_wlan_download_dialog_btn_cancel),new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	                dialog.dismiss();
	                
	            }
	        });
	        alertDialog.setLePositiveButton(this.getString(R.string.lejingpin_wlan_download_dialog_btn_confirm),new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            	startLejingpinSettings();
	                dialog.dismiss();
	            }
	        });
	    	alertDialog.setOnCancelListener( new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
	                dialog.dismiss();
	                
				}
	        	
	        });
	    	alertDialog.show();
		 
	 }
	
	 public void startLejingpinSettings(){
	    	Intent mIntent = new Intent(this, LejingpingSettings.class);   	
	    	startActivity(mIntent);
	    	
	 }
	 
	 
	 
	 
	 
	 
	 //yangmao add start 0425
	 
	    public List<Item> getItems() {
	    	
	    	Log.i("getdata","getItems-------------");
	        PackageManager packageManager = getPackageManager();
	        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
	        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
	        items.clear();
	        final List<ResolveInfo> infolist = packageManager.queryIntentActivities(mainIntent, 0);

	        Collections.sort(infolist, new ResolveInfo.DisplayNameComparator(packageManager));
//	        ActivityInfo ai = null;
//	   	    boolean checked =false;

	        for (int i = 0; i < infolist.size(); i++) {
	            ResolveInfo info = infolist.get(i);
	            if ( info.activityInfo.packageName.startsWith(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF)
	            	 || info.activityInfo.packageName.contains(SettingsValue.THEME_PACKAGE_NAME_PREF)) {
	            	continue;
	            }
	            items.add(new Item(this, packageManager, info, false));
	            
	            
	        }
	        return items;
	    }

	    public static class Item {

	        CharSequence label;
	        Bitmap icon;
	        String packageName;
	        String className;
	        Boolean hiden;
	        Intent intent;
	        CharSequence title;
	        String  sortKey;
	        HashSet<String> mLookupKeys;
	        
	        /**
	         * Create a list item and fill it with details from the given {@link ResolveInfo} object.
	         */
	        Item(Context context, PackageManager pm, ResolveInfo resolveInfo, Boolean check) {
	            label = resolveInfo.loadLabel(pm);
	            if (label == null && resolveInfo.activityInfo != null) {
	                label = resolveInfo.activityInfo.name;
	            }

	            // icon = resolveInfo.loadIcon(pm);
	            packageName = resolveInfo.activityInfo.applicationInfo.packageName;
	            className = resolveInfo.activityInfo.name;
	            
	            
	           

	            LauncherApplication app = (LauncherApplication) context.getApplicationContext();
	            ApplicationInfo appInfo = new ApplicationInfo(context.getPackageManager(), resolveInfo, app.getIconCache(),
	                    null);
	            
	            
	            icon = appInfo.iconBitmap;
	            hiden = appInfo.hidden;
	            intent = appInfo.intent;
	            title = appInfo.title;
	            sortKey = appInfo.sortKey[0];
	            mLookupKeys = appInfo.mLookupKeys;
	            
	        }

	        /**
	         * Build the {@link Intent} described by this item. If this item can't create a valid
	         * {@link android.content.ComponentName}, it will return
	         * {@link Intent#ACTION_CREATE_SHORTCUT} filled with the item label.
	         */
	        Intent getIntent() {
	            Intent mBaseIntent = new Intent(Intent.ACTION_MAIN, null);
	            mBaseIntent.addCategory(Intent.CATEGORY_DEFAULT);

	            if (packageName != null && className != null) {
	                // Valid package and class, so fill details as normal intent
	                mBaseIntent.setClassName(packageName, className);
	                return mBaseIntent;
	            }
	            return mBaseIntent;
	        }
	    }
	 
	 
	 
		private void saveHotList(final String keyword) {
			
//			new Thread(){
//				@Override
//				public void run() {
//					super.run();
					Log.i(TAG, "save the hot list ");
					SharedPreferences searchHistoryShare = getSharedPreferences(SEARCH_HOT_SHARE_NAME, Context.MODE_PRIVATE);
					searchHistoryShare.edit().clear().commit();
					searchHistoryShare.edit().putString(SEARCH_HISTORY_SHARE_KEY, keyword).commit();
					
//				}
//			}.start();
		}
	 
	 
		private void loadHotList(){
			new Thread(){
				@Override
				public void run() {
					SharedPreferences searchHistoryShare = getSharedPreferences(SEARCH_HOT_SHARE_NAME, Context.MODE_PRIVATE);
					String tmp = searchHistoryShare.getString(SEARCH_HISTORY_SHARE_KEY, null);
					Log.i(TAG, "loadHotlist thread");
					if( tmp != null && tmp.length() != 0 ) {
						String[] hotArray = tmp.split(",");
						Message msg = mHandler.obtainMessage(LOAD_HOT_LIST);
						msg.obj = hotArray;
						mHandler.sendMessage(msg);
					}else{
						Log.i(TAG, "the shared_prefernce is null so send request hot request");
						mHandler.sendEmptyMessage(REQUEST_HOT);
					}
					
					
				}
			}.start();
		}
		
		
		private boolean overlayflag = false;
		private void InitOverlay() {
	        try{
	        mOverlay = (TextView) mLi.inflate(R.layout.loading_text, null);
	        mOverlay.setVisibility(View.INVISIBLE);
	        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
	            LayoutParams.FILL_PARENT,
	            LayoutParams.WRAP_CONTENT,
	            WindowManager.LayoutParams.TYPE_APPLICATION,
	            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
	            PixelFormat.TRANSLUCENT);
	        lp.gravity = Gravity.BOTTOM | Gravity.CENTER;
	        //lp.gravity = Gravity.TOP;
	        mWM.addView(mOverlay, lp);
	        overlayflag = true;
	        }catch(Exception e){
	        	Log.i("pad_search", "getException");
	        }
	    }
	    private void showOverLay(){
	    	Log.i("pad_search","showOverLay");
	        mOverlay.setVisibility(View.VISIBLE);
	        mHandler.removeMessages(MSG_NO_MORE);
	        
	        //mHandler.sendEmptyMessageDelayed(MSG_NO_MORE, 1500);
	    }
	    private void hideOverLay(){
	        mOverlay.setVisibility(View.GONE);
	    }
		
	
}
