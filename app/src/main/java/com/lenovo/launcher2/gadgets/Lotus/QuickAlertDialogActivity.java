package com.lenovo.launcher2.gadgets.Lotus;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;
import com.lenovo.launcher.components.XAllAppFace.XLauncher;
import com.lenovo.launcher2.commoninterface.ApplicationInfo;
import com.lenovo.launcher2.commoninterface.LauncherService;
import com.lenovo.launcher2.customizer.FastBitmapDrawable;
import com.lenovo.launcher2.customizer.Reaper;
import com.lenovo.launcher2.customizer.Utilities;
import com.lenovo.lejingpin.hw.content.data.HwConstant;
import com.lenovo.lejingpin.magicdownloadremain.MagicDownloadControl;
import com.lenovo.lejingpin.share.download.DownloadConstant;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class QuickAlertDialogActivity extends Activity implements View.OnClickListener {
	private static final String TAG = "QuickAlertDialogActivity";
	
    public static final String NAME_MESSAGE_RES_ID = "message_res_id";
    public static final String NAME_CANCEL_RES_ID = "cancel_res_id";
    public static final String NAME_OK_RES_ID = "ok_res_id";
    private TextView mMessage;
    private Button mCancel;
    private Button mOk;
    private int mMessageResId = -1;
    private int mCancelResId = -1;
    private int mOkResId = -1;
    private int mLeafId = -1;

	private static final String PREFIX_LOTUS_CENTER_PIC_PATH = "/data/data/com.lenovo.launcher/files/extra/";
    ViewGroup container = null;
	private float mDeviceDensity;
	private int desWidth=0;
	private int desHeight=0;
	private PackageManager packageManager;
	private ArrayList<Intent> mIntents = new ArrayList<Intent>();
	private SharedPreferences preferences = null;
	private static final String LOTUSINFO = "lotuspage";
	private static final String PREFIX_LEFAMILY = "lefamily_";
	private static final String PREFIX_SHOW_DOWNLOAD = "show_download";
	private static final String PREFIX_SEARCHKEY = "searchkey_";
	private static final String PREFIX_NAME = "name_";

    private boolean hasXml = false; //是否有配置文件

	private boolean showDownload = false;

	private Context mContext;

	private static XLauncher mLauncher;
    public static void setContext(XLauncher launcher) {
        mLauncher = launcher;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quick_alert_dialog);
        mContext = QuickAlertDialogActivity.this;
        Intent intent = this.getIntent();
        mMessageResId = intent.getIntExtra(NAME_MESSAGE_RES_ID, -1);
        mCancelResId = intent.getIntExtra(NAME_CANCEL_RES_ID, -1);
        mOkResId = intent.getIntExtra(NAME_OK_RES_ID, -1);
        /*** RK_ID:RK_LOTUS_FOR _OTHERS_1964 AUT:zhanglz1@lenovo.com. DATE:2012-02-23 S***/
        mLeafId = intent.getIntExtra("leaf_id", -1);
        String mKeyword = intent.getStringExtra("keyword");
		Log.i("LotusProviderHelper", " mLeafId===* " +mLeafId+"======mKeyword===="+mKeyword);
		LinearLayout mBar = (LinearLayout) findViewById(R.id.okcancelBar);
        mMessage = (TextView) findViewById(R.id.message);
        mCancel = (Button) findViewById(R.id.cancel);
        mCancel.setOnClickListener(this);
        mOk = (Button) findViewById(R.id.ok);
        mOk.setOnClickListener(this);
		/* 下载跳转对话框*/
		if (mKeyword == null) {
			if (mMessageResId == -1) {
				mMessage.setText("");
				if (mMessage.getVisibility() != View.GONE) {
					mMessage.setVisibility(View.GONE);
				}
			} else {
				mMessage.setText(getString(mMessageResId));
				if (mMessage.getVisibility() != View.VISIBLE) {
					mMessage.setVisibility(View.VISIBLE);
				}
			}
			if (mCancelResId == -1) {
				mCancel.setText(android.R.string.cancel);
			} else {
				mCancel.setText(getString(mCancelResId));
			}
			if (mOkResId == -1) {
				mOk.setText(android.R.string.ok);
			} else {
				mOk.setText(getString(mOkResId));
			}
		}else{
			/* 乐家族对话框*/
			this.setTitle(R.string.title_lefamily);
			mBar.setVisibility(View.GONE);
			mCancel.setVisibility(View.GONE);
			mOk.setVisibility(View.GONE);
			packageManager = getPackageManager();
			
			DisplayMetrics dm = new DisplayMetrics();
			this.getWindowManager().getDefaultDisplay().getMetrics(dm);
			mDeviceDensity = dm.density;
			
//		#########################有配置文件的情况下：不存在即显示下载图标 没有配置文件的情况下 不存在即不显示######	
			/* 获取配置文件中对乐家族的定义*/
			preferences = getSharedPreferences(LOTUSINFO, 0);
			try {
				mUnInstallReceiver = new UnInstallReceiver();
				IntentFilter unInstallfilter = new IntentFilter();
				unInstallfilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
				unInstallfilter.addAction(Intent.ACTION_PACKAGE_ADDED);
				unInstallfilter.addDataScheme("package");
				this.registerReceiver(mUnInstallReceiver, unInstallfilter);

			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			mShowLefamilyAppGridView = (GridView)findViewById(R.id.gridLayout1);
			mIntents = getIntents();
            setup();
		}
   }
	private void setup() {
		List<LefamilyAppItem> mLefamilyApps = getLefamilyAppItems();
		mMessage = (TextView) findViewById(R.id.message);

		if (mLefamilyApps.size() < 1) {
			mMessage.setText(R.string.lefamily_no_apk);
			mMessage.setVisibility(View.VISIBLE);
			mShowLefamilyAppGridView.setVisibility(View.GONE);
		} else {
			mLefamilyAppAdapter = new LefamilyAppAdapter(this,
					mLefamilyApps);
			mShowLefamilyAppGridView.setAdapter(mLefamilyAppAdapter);
			mShowLefamilyAppGridView.setVisibility(View.VISIBLE);
			mMessage.setVisibility(View.GONE);
		}		
	}
	private GridView mShowLefamilyAppGridView;
    @Override
   public void onClick(View view) {
    	Intent intent = new Intent(Intent.ACTION_MAIN);
    	intent.addCategory(Intent.CATEGORY_LAUNCHER);
    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        int id = view.getId();
		/* 下载对话框中选择下载或取消*/
        switch (id) {
        case R.id.cancel: {
            setResult(Activity.RESULT_CANCELED);
            finish();
            break;
        }
        case R.id.ok: {
            setResult(Activity.RESULT_OK);
            /*** RK_ID:RK_LOTUS_FOR _OTHERS_1964 AUT:zhanglz1@lenovo.com. DATE:2012-02-23 S***/
        	if(mLeafId!=-1){
        		
        	}else{
                setResult(Activity.RESULT_OK);
        	}
            /*** RK_ID:RK_LOTUS_FOR _OTHERS_1964 AUT:zhanglz1@lenovo.com. DATE:2012-02-23 S***/
            finish();
            break;
        }
        default:
            break;
        }
	}
	private UnInstallReceiver mUnInstallReceiver;
	private class UnInstallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
                //取被卸载的包名
                String pName = intent.getData().getSchemeSpecificPart();
                Log.i("00", "--------------ACTION_PACKAGE_REMOVED====" +pName);

                if(mIntents.toString().contains(pName) && mShowLefamilyAppGridView.getVisibility() == View.VISIBLE){
                    Log.i("00",":-D====");

                	setup();
                }
            }
            if (action.equals(Intent.ACTION_PACKAGE_ADDED)) {
            	String pName = intent.getData().getSchemeSpecificPart();
                Log.i("00", "--------------ACTION_PACKAGE_ADDED====" +pName);
                
            	 if(mIntents.toString().contains(pName)&& mShowLefamilyAppGridView.getVisibility() == View.VISIBLE){
                     Log.i("00", ":-)====");
            		 setup();
                 }
            }

        }
    }
	/*下载*/
	private Drawable addDownload(Drawable resdrawable) {
		Bitmap downIcon = null;
		downIcon = BitmapFactory.decodeResource(getResources(),
				R.drawable.stamp_app);
		BitmapDrawable bd = (BitmapDrawable)resdrawable;
		Bitmap res = bd.getBitmap();
		Bitmap newBmp = res.copy(res.getConfig(), true);
		Canvas c = new Canvas(newBmp);
		c.drawBitmap(downIcon, res.getWidth() - downIcon.getWidth(), 0, null);
		c.setBitmap(null);
		return new FastBitmapDrawable(newBmp);
	}
	/* 缩放图片*/
	private Drawable zoomBitmap(Bitmap bitmap,int desWidth,int desHeight) {
		Matrix matrix = new Matrix();
		int orgWidth = bitmap.getWidth();
		int orgHeight = bitmap.getHeight();
		float scaleWidth;
		float scaleHeight;
		scaleWidth = (float) (mDeviceDensity * (desWidth * 1.0) / (orgWidth * 1.0));
		scaleHeight = (float) (mDeviceDensity * (desHeight * 1.0) / (orgHeight * 1.0));
		Log.i("00", " lotus desWidth ******************************** "+desWidth);
		Log.i("00", " lotus orgWidth ******************************** "+orgWidth);
		Log.i("00", " lotus scaleWidth ******************************** "+scaleWidth);


		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);


		return new BitmapDrawable(newbmp);

	}
	/* 获取配置文件中的信息*/
	private Intent getLefamilyInfo(int num) {
		/*** RK_ID:RK_REDSUQARE_1745 AUT:zhanglz1@lenovo.com. ***/
		Intent intent = null;
		String intent_str = preferences.getString(PREFIX_LEFAMILY+Integer.toString(num), "");
		Log.i("zlz","========intent_str===="+intent_str);
		String packageName = null;
		String className = null;
		ComponentName cn = null;
		if (intent_str != null && intent_str.length() > 0) {
			try {
				String[] infos = intent_str.split(File.separator);
				packageName = infos[0];
				className = infos[1];
				cn = new ComponentName(packageName, className);
				intent = new Intent();
				intent.setComponent(cn);
				
			} catch (Exception e) {
			}
		}
		return intent;
	}
	private Drawable getAppIcon(ResolveInfo resolveInfo,PackageManager packagemanager){
		
		desWidth = getResources().getDrawable(R.drawable.lefamily_default).getIntrinsicWidth();
   		desHeight = getResources().getDrawable(R.drawable.lefamily_default).getIntrinsicWidth();

		Drawable icon = null;
		LauncherApplication app = (LauncherApplication) this
				.getApplicationContext();
		ApplicationInfo appInfo = new ApplicationInfo(
				getPackageManager(), resolveInfo, app.getIconCache(),
				null);
		Bitmap newBitmap = appInfo.iconBitmap;
		icon = zoomBitmap(newBitmap,desWidth,desHeight);
		return icon;
	}

	//获取各个应用的intent,先从配置文件中获取 如果获取后为空的话，即没有配置的情况下，默认采用7个应用
	private ArrayList<Intent> getIntents(){
		 ArrayList<Intent> intents = new  ArrayList<Intent>();
			for (int k = 0; k < 100; k++) {
				Intent data = getLefamilyInfo(k);
				if(data != null){
					intents.add(data);
				}else{
					break;
				}
			}
			if(intents.size() < 1){
				hasXml = false;
				showDownload = false; 
				Intent data_0 =new Intent();
				data_0.setComponent(new ComponentName("com.lenovo.safecenter", "com.lenovo.safecenter.MainTab.SplashActivity"));
				Intent data_1 =new Intent();
				data_1.setComponent(new ComponentName("com.lenovo.lps.nps", "com.lenovo.lps.nps.MainActivity"));
				Intent data_2 =new Intent();
				data_2.setComponent(new ComponentName("com.lenovo.leos.hw", "com.lenovo.leos.hw.ui.HwUiActivity"));
				Intent data_3=new Intent();
				data_3.setComponent(new ComponentName("com.lenovo.levoice", "com.lenovo.lv.vehicle.activity.MainActivity"));
				Intent data_4 =new Intent();
				data_4.setComponent(new ComponentName("com.lenovo.leos.cloud.sync", "com.lenovo.leos.cloud.sync.common.activity.SplashScreenActivity"));
				Intent data_5 =new Intent();
				data_5.setComponent(new ComponentName("com.lenovo.leos.dc.portal", "com.lenovo.ms.MagicShareActivity"));
				Intent data_6 =new Intent();
				data_6.setComponent(new ComponentName("com.snda.inote.lenovo", "com.snda.inote.lenovo.activity.WelcomeActivity"));
				intents.add(data_0);
				intents.add(data_1);
				intents.add(data_2);
				intents.add(data_3);
				intents.add(data_4);
				intents.add(data_5);
				intents.add(data_6);
			}else{
				hasXml = true;
			}
		return intents;
	}
	//获取各个应用的名称
	// 有配置文件 且开关开着时 获取配置文件中名称 ，没有的话获取应用名称，没有的话使用乐应用默认名称。
	// 有配置文件 且开关关着 获取配置文件中名称 ，没有的话获取应用名称，没有的话使用乐应用默认名称。
	// 没有配置文件 获取配置文件中名称 ，没有的话获取应用名称，没有的话使用乐应用默认名称。
	//获取搜索关键字 先从配置文件中获取，没有的话使用应用中文名称，没有的话使用英文名称，没有的话为空
	private String getName(int num){
		// 1.系统是什么语言就显示什么语言种类的名词
		// 2.若该语言不存在，则显示该应用的名称。如果该应用名称无法取到，则显示指定字符串“默认应用/默認應用”；
          
		String name = null;
		Resources res = mContext.getResources();
		String country = res.getConfiguration().locale.getCountry();
		boolean isCN = country.equals("CN");
		boolean isTW = country.equals("TW");
		boolean isUK = country.equals("UK");
		boolean isUS = country.equals("US");

		// 取默认标题英文名
		if (isUK || isUS) {
			name = preferences.getString(PREFIX_LEFAMILY + PREFIX_NAME
					+"en_"+ Integer.toString(num), "");
		}
		// 取默认标题简体中文名
		else if (isCN) {
			name = preferences.getString(PREFIX_LEFAMILY + PREFIX_NAME
					+"cn_"+ Integer.toString(num), "");
		}
		// 取默认标题繁体中文名
		else if (isTW) {
			name = preferences.getString(PREFIX_LEFAMILY + PREFIX_NAME
					+"tw_"+Integer.toString(num), "");
		}
		// 取默认标题其他语言名
		else {
			name = preferences.getString(PREFIX_LEFAMILY + PREFIX_NAME
					+country.toLowerCase()+"_"+ Integer.toString(num), "");
		}
		return name;
	}
	
    List<LefamilyAppItem> items = new ArrayList<LefamilyAppItem>();

	private LefamilyAppAdapter mLefamilyAppAdapter;
	private class LefamilyAppAdapter extends BaseAdapter {

		private final List<LefamilyAppItem> mItems;
		private Context mContext;
        
        public LefamilyAppAdapter(Context context, List<LefamilyAppItem> items) {
            mItems = items;
            mContext = context;
        }
       @Override
       public int getCount() {
           return mItems.size();
       }
       @Override
       public Object getItem(int position) {
           return mItems.get(position);
       }
       @Override
       public long getItemId(int position) {
           return position;
       }
       @Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(mContext, R.layout.app_to_category,
						null);
			}
			final TextView label = (TextView) convertView
					.findViewById(R.id.category_label);
			final ImageView icon = (ImageView) convertView
					.findViewById(R.id.category_icon);
			icon.setImageDrawable(mItems.get(position).mIcon);
			label.setText(mItems.get(position).mLabel);
			convertView.setTag(position);
			convertView.setOnClickListener(new OnClickListener() {
               @Override
				public void onClick(View view) {

					int position = Integer.parseInt(view.getTag().toString());
					final Intent intent = getIntentForPosition(position);
					if(!mItems.get(position).mShowDownload){
	        			 Intent in = packageManager.getLaunchIntentForPackage(mItems.get(position).mPackageName);
	     				//如果该程序不可启动（像系统自带的包，有很多是没有入口的）会返回NULL
	        			 
	     				if (in != null){
	     				    intent.setComponent(new ComponentName(in.getComponent().getPackageName(),
	     				    		in.getComponent().getClassName()));
	     	            }else{
	     					intent.setComponent(new ComponentName(mItems.get(position).mPackageName,
	     							mItems.get(position).mClassName));
	     				}
	     				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

	     				try {
	    					startActivity(intent);
	    				} catch (ActivityNotFoundException e) {
	    				}
	    			} else {
	    				MagicDownloadControl.downloadFromCommon (QuickAlertDialogActivity.this,
	    						mItems.get(position).mPackageName,
                                "214",
                                DownloadConstant.CATEGORY_COMMON_APP,
                                mItems.get(position).mLabel.toString(),
                                null,
                                LotusUtilites.IDEA_STORE_URL+mItems.get(position).mPackageName,
                                HwConstant.MIMETYPE_APK,
                                "true",
                                "true");
	    				/*if(mLauncher !=null)
	    				    mLauncher.downCommendApplication(mItems.get(position).mPackageName,searchString);*/
					}
					QuickAlertDialogActivity.this.finish();
				}
           });
           return convertView;
       }
	}
			
	protected Intent getIntentForPosition(int position) {
		LefamilyAppItem item = (LefamilyAppItem) mLefamilyAppAdapter.getItem(position);
		return item.getIntent();
	}
	private List<LefamilyAppItem> getLefamilyAppItems() {
		Boolean intent_str = preferences.getBoolean(PREFIX_LEFAMILY+PREFIX_SHOW_DOWNLOAD,false);
		items.clear();
		//ArrayList<String[]> appInfos = new  ArrayList<String[]>();
		for (int i = 0; i < mIntents.size(); i++) {
			Intent data = mIntents.get(i);
			String searchKeyWords = null;
			Drawable lefamily_icon = null;
			String picPath = null;
			Boolean showDownloadTemp = false;
			String label = null;
			if (data != null) {
				ResolveInfo resolveInfo = packageManager.resolveActivity(data,
						0);

				label = getName(i);
				if (label == null || label.length() == 0) {
					if (resolveInfo != null) {
						label = resolveInfo.loadLabel(packageManager)
								.toString();
						if (label == null && resolveInfo.activityInfo != null) {
							label = resolveInfo.activityInfo.name;
						}

					}
					if (label == null || label.length() == 0) {
						label = this.getResources().getString(
								R.string.title_lefamily);
					}
				}

				picPath = PREFIX_LOTUS_CENTER_PIC_PATH + PREFIX_LEFAMILY
						+ Integer.toString(i) + ".png"; // 图标路径
				if (hasXml && intent_str && resolveInfo == null) {
					// 显示下载图标
					showDownloadTemp = true;
				} else {
					// 不显示下载图标,View不显示
					showDownloadTemp = false;
				}

				Bitmap lefamily = null;
				try {
					lefamily = BitmapFactory.decodeFile(picPath);
				} catch (Throwable e1) {
					e1.printStackTrace();
				}
				if (lefamily != null) {
					desWidth = getResources().getDrawable(
							R.drawable.lefamily_default).getIntrinsicWidth();
					desHeight = getResources().getDrawable(
							R.drawable.lefamily_default).getIntrinsicWidth();
					lefamily_icon = zoomBitmap(lefamily, desWidth, desHeight);

				} else {
					if (resolveInfo != null)
						lefamily_icon = getAppIcon(resolveInfo, packageManager);
				}
				boolean add = true;
				if (lefamily_icon == null) {
					lefamily_icon = getResources().getDrawable(
							R.drawable.lefamily_default);
				}
				if (resolveInfo == null) {
					if (showDownloadTemp) {
						lefamily_icon = addDownload(lefamily_icon);
						add = true;
					} else {
						add = false;
					}
				} else {
					add = true;
				}

				searchKeyWords = preferences.getString(PREFIX_LEFAMILY
						+ PREFIX_SEARCHKEY + Integer.toString(i), "");
				
				if (add) {
					items.add(new LefamilyAppItem(this, data, label,
							lefamily_icon, searchKeyWords,
							showDownloadTemp));
				}
			} else {
				break;
			}
		}
		return items;
	}
	private static class LefamilyAppItem {

        CharSequence mLabel;//显示名称
        Drawable mIcon;//显示图标
        String mPackageName;//包名
        String mClassName;//类名
        String mKeyWords;//搜索关键字
        Boolean mShowDownload;//是否显示下载

        LefamilyAppItem(Context context, Intent data, CharSequence label,Drawable icon,String keyWords,Boolean showDownload) {
        	
        	mLabel = label;
            mIcon = icon;
            mPackageName = data.getComponent().getPackageName();
            mClassName = data.getComponent().getClassName();
            mKeyWords = keyWords;
            mShowDownload = showDownload;
        }

        Intent getIntent() {
            Intent mBaseIntent = new Intent(Intent.ACTION_MAIN, null);
            mBaseIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            if (mPackageName != null && mClassName != null) {
                // Valid package and class, so fill details as normal intent
                mBaseIntent.setClassName(mPackageName, mClassName);
                return mBaseIntent;
            }
            return mBaseIntent;
        }
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			this.unregisterReceiver(mUnInstallReceiver);
		} catch (Exception e) {
			return;
		}
	}
}
