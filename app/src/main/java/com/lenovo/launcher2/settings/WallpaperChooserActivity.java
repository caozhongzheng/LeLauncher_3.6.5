package com.lenovo.launcher2.settings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.lenovo.launcher.R;



public class WallpaperChooserActivity extends Activity implements
AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener{
	
	private PopupWindow popupWindow;
	private ListView lv_group;
	private View view;
	//private View top_title;
	private ImageView tvtitle;
	private List<String> groups;
	Gallery gallery;
	private static final String TAG = "Launcher.WallpaperChooserActivity";
	
	//private Bitmap mBitmap = null;

    private ArrayList<Integer> mThumbs;
    private ArrayList<Integer> mImages;
    private WallpaperLoader mLoader;
    private PackageManager mpackageManager;
 //   List<CharSequence> listname= new ArrayList();
    private List<ResolveInfo> listInfo;
  //  private WallpaperDrawable mWallpaperDrawable = new WallpaperDrawable();

	@Override
	 public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		findWallpapers();
		setContentView(R.layout.wallpaper_chooser);
	
		//top_title = this.findViewById(R.id.top_title);
		tvtitle = (ImageView) findViewById(R.id.tvtitle);

		tvtitle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			showWindow(v);
			}
		});
		
		 gallery = (Gallery) findViewById(R.id.gallery);
         gallery.setCallbackDuringFling(false);
         gallery.setOnItemSelectedListener(this);
         gallery.setAdapter(new ImageAdapter(this));

         View setButton = (View) findViewById(R.id.set);
         setButton.setOnClickListener(new OnClickListener() {
             @Override
             public void onClick(View v) {
                 selectWallpaper(gallery.getSelectedItemPosition());
             }
         });
         
//         Spinner setSpinner = (Spinner) findViewById(R.id.setmore);
//         Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
//         mpackageManager = this.getPackageManager();
//         listInfo =  mpackageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
//       //  List<CharSequence> listname= new ArrayList();
//         for(int i=0;i<listInfo.size();i++){
//        	 Log.i("shenchao","is "+listInfo.get(i).loadLabel(mpackageManager));
//        	 listname.add(listInfo.get(i).loadLabel(mpackageManager).toString());
//        	 Log.i("shenchao","is "+listInfo.get(i).activityInfo);
//         }
//         ArrayAdapter<CharSequence> adapter =  new ArrayAdapter<CharSequence>(
//         		this, android.R.layout.simple_spinner_item,listname);
//         adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//         setSpinner.setAdapter(adapter);
//         setSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
//        	 public void onItemSelected(AdapterView<?> parent,
//     	            View view, int pos, long id) {
////     	        	String str = listInfo.get(pos).activityInfo.packageName;
////     	        	Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
////     	        	Intent intent1 = intent1.setComponent(listInfo.get(pos).);
//     	        	String packageName = listInfo.get(pos).activityInfo.packageName;
//     	        	String className = listInfo.get(pos).activityInfo.name;
//     	        	Intent intent = new Intent();
//     	        	intent.setClassName(packageName, className);
//     	        	startActivity(intent);
//     	        	//finish();
//     	        }
//
//     	        public void onNothingSelected(AdapterView parent) {
//     	          // Do nothing.
//     	        }
//         });
	}
	
	private void showWindow(View parent) {
		Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
		mpackageManager = this.getPackageManager();
		listInfo =  mpackageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		if(listInfo == null){
			
		}else{
		groups = new ArrayList<String>();
		//  List<CharSequence> listname= new ArrayList();
		for(int i=0;i<listInfo.size();i++){
			Log.i("shenchao","is "+listInfo.get(i).loadLabel(mpackageManager));
			groups.add(listInfo.get(i).loadLabel(mpackageManager).toString());
			Log.i("shenchao","is "+listInfo.get(i).activityInfo);
		}

		if (popupWindow == null) {

		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		view = layoutInflater.inflate(R.layout.group_list, null);
		view.setBackgroundColor(Color.WHITE);
		view.getBackground().setAlpha(180);
		lv_group = (ListView) view.findViewById(R.id.lvGroup);

		GroupAdapter groupAdapter = new GroupAdapter(this, groups);

		lv_group.setAdapter(groupAdapter);

		// 创建一个PopuWidow对象

		popupWindow = new PopupWindow(view, getResources().getDimensionPixelSize(R.dimen.wallpaper_choose_popwindow_width),
				LayoutParams.WRAP_CONTENT);

		}
		
//		popupWindow.setWidth(LayoutParams.WRAP_CONTENT);
//		popupWindow.setHeight(LayoutParams.WRAP_CONTENT); 
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new ColorDrawable(android.R.color.darker_gray));
		WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		int xPos = windowManager.getDefaultDisplay().getWidth() / 2 - popupWindow.getWidth() / 2;
		Log.i("coder", "xPos:" + xPos);
		popupWindow.showAsDropDown(parent, xPos, 0);
		lv_group.setOnItemClickListener(new OnItemClickListener() {

			@Override	
			public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {	
 	        	String packageName = listInfo.get(position).activityInfo.packageName;
 	        	String className = listInfo.get(position).activityInfo.name;
 	        	Intent intent = new Intent();
 	        	intent.setClassName(packageName, className);
 	        	startActivity(intent);
//			if (popupWindow != null) {	
//			popupWindow.dismiss();	
//				}	
			finish();
			}
		});}

		}
	
	@Override
	public void finish(){
		super.finish();
	}
	
	 @Override
	    public void onDestroy() {

	        if (mLoader != null && mLoader.getStatus() != WallpaperLoader.Status.FINISHED) {
	            mLoader.cancel(true);
	            mLoader = null;
	        }
	        super.onDestroy();
	    }
	 
	 private void selectWallpaper(int position) {
	        try {
	            WallpaperManager wpm = (WallpaperManager) this.getSystemService(
	                    Context.WALLPAPER_SERVICE);
	            wpm.setResource(mImages.get(position));
	           
	            this.setResult(Activity.RESULT_OK);
	            this.finish();
	        } catch (IOException e) {
	            Log.e(TAG, "Failed to set wallpaper: " + e);
	        }
	    }
	 @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        selectWallpaper(position);
	    }

	    // Selection handler for the embedded Gallery view
	    @Override
	    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
	        if (mLoader != null && mLoader.getStatus() != WallpaperLoader.Status.FINISHED) {
	            mLoader.cancel();
	        }
	        mLoader = (WallpaperLoader) new WallpaperLoader().execute(position);
	        
	        final int pos = position;
	       Handler mHandler = new Handler();
	       mHandler.post(new Runnable(){

               @Override
               public void run() {
                   // TODO Auto-generated method stub
            	   ImageView imageview = (ImageView) findViewById(R.id.imageView);
            	   imageview.setBackgroundResource(mImages.get(pos));
               }
               
           });
	       
	    }

	    @Override
	    public void onNothingSelected(AdapterView<?> parent) {
	    }
	    
	    private void findWallpapers() {
	        mThumbs = new ArrayList<Integer>(24);
	        mImages = new ArrayList<Integer>(24);

	        final Resources resources = getResources();
	        // Context.getPackageName() may return the "original" package name,
	        // com.android.launcher2; Resources needs the real package name,
	        // com.android.launcher. So we ask Resources for what it thinks the
	        // package name should be.
	        final String packageName = resources.getResourcePackageName(R.array.wallpapers);	        
	        
	        addWallpapers(resources, packageName, R.array.wallpapers);
	        /*
	        if(bWWVersion){
	        	addWallpapers(resources, packageName, R.array.wallpapers_ww);
	        }else{
	            addWallpapers(resources, packageName, R.array.wallpapers);
	        }
	        */
	        /*RK_VERSION_WW dining 2012-10-25 E*/ 
	        addWallpapers(resources, packageName, R.array.extra_wallpapers);
	    }
	    
	    private void addWallpapers(Resources resources, String packageName, int list) {
	        final String[] extras = resources.getStringArray(list);
	        for (String extra : extras) {
	            int res = resources.getIdentifier(extra, "drawable", packageName);
	            if (res != 0) {
	                final int thumbRes = resources.getIdentifier(extra + "_small",
	                        "drawable", packageName);

	                if (thumbRes != 0) {
	                    mThumbs.add(thumbRes);
	                    mImages.add(res);
	                    // Log.d(TAG, "add: [" + packageName + "]: " + extra + " (" + res + ")");
	                }
	            }
	        }
	    }

	 private class ImageAdapter extends BaseAdapter implements ListAdapter, SpinnerAdapter {
	        private LayoutInflater mLayoutInflater;

	        ImageAdapter(Activity activity) {
	            mLayoutInflater = activity.getLayoutInflater();
	        }

	        public int getCount() {
	            return mThumbs.size();
	        }

	        public Object getItem(int position) {
	            return position;
	        }

	        public long getItemId(int position) {
	            return position;
	        }

	        public View getView(int position, View convertView, ViewGroup parent) {
	            View viewItem;

	            if (convertView == null) {
	                viewItem = mLayoutInflater.inflate(R.layout.wallpaper_item, parent, false);
	            } else {
	                viewItem = convertView;
	            }

	            ImageView image = (ImageView) viewItem.findViewById(R.id.wallpaper_image);

	            int thumbRes = mThumbs.get(position);
	            image.setImageResource(thumbRes);
	            Drawable thumbDrawable = image.getDrawable();
	            if (thumbDrawable != null) {
	                thumbDrawable.setDither(true);
	            } else {
	                Log.e(TAG, "Error decoding thumbnail resId=" + thumbRes + " for wallpaper #"
	                        + position);
	            }

	            return viewItem;
	        }
	    }
	
	 class WallpaperLoader extends AsyncTask<Integer, Void, Bitmap> {
	        BitmapFactory.Options mOptions;

	        WallpaperLoader() {
	            mOptions = new BitmapFactory.Options();
	            mOptions.inDither = false;
	            mOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
	        }

	        @Override
	        protected Bitmap doInBackground(Integer... params) {
	            if (isCancelled()) return null;
	            try {
	                return BitmapFactory.decodeResource(getResources(),
	                        mImages.get(params[0]), mOptions);
	            } catch (OutOfMemoryError e) {
	                return null;
	            }
	        }

	        @Override
	        protected void onPostExecute(Bitmap b) {
	            if (b == null) return;
	            
//	            ImageView imageview = (ImageView) findViewById(R.id.imageView);
//	            imageview.setImageResource(mImages.get(0));

//	            if (!isCancelled() && !mOptions.mCancel) {
//	                // Help the GC
//	                if (mBitmap != null) {
//	                    mBitmap.recycle();
//	                }
//
//	                View v = getView()b;
//	                if (v != null) {
//	                    mBitmap = b;
//	                    mWallpaperDrawable.setBitmap(b);
//	                    v.postInvalidate();
//	                } else {
//	                    mBitmap = null;
//	                    mWallpaperDrawable.setBitmap(null);
//	                }
//	                mLoader = null;
//	            } else {
//	               b.recycle();
//	            }
	        }

	        void cancel() {
	            mOptions.requestCancelDecode();
	            super.cancel(true);
	        }
	    }
	 static class WallpaperDrawable extends Drawable {

	        Bitmap mBitmap;
	        int mIntrinsicWidth;
	        int mIntrinsicHeight;

	        /* package */void setBitmap(Bitmap bitmap) {
	            mBitmap = bitmap;
	            if (mBitmap == null)
	                return;
	            mIntrinsicWidth = mBitmap.getWidth();
	            mIntrinsicHeight = mBitmap.getHeight();
	        }

	        @Override
	        public void draw(Canvas canvas) {
	            if (mBitmap == null) return;
	            int width = canvas.getWidth();
	            int height = canvas.getHeight();
	            int x = (width - mIntrinsicWidth) / 2;
	            int y = (height - mIntrinsicHeight) / 2;
	            Log.i("calcul"," mIntrinsicWidth is "+ mIntrinsicWidth+"  mIntrinsicHeight is "+ mIntrinsicHeight);
	            Log.i("calcul","width is "+width+" and height is "+height);

	            canvas.drawBitmap(mBitmap, x, y, null);
	        }

	        @Override
	        public int getOpacity() {
	            return android.graphics.PixelFormat.OPAQUE;
	        }

	        @Override
	        public void setAlpha(int alpha) {
	            // Ignore
	        }

	        @Override
	        public void setColorFilter(ColorFilter cf) {
	            // Ignore
	        }
	    }
	
//	  public class MyOnItemSelectedListener implements OnItemSelectedListener {
//
//	        public void onItemSelected(AdapterView<?> parent,
//	            View view, int pos, long id) {
////	        	String str = listInfo.get(pos).activityInfo.packageName;
////	        	Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
////	        	Intent intent1 = intent1.setComponent(listInfo.get(pos).);
//	        	String packageName = listInfo.get(pos).activityInfo.packageName;
//	        	String className = listInfo.get(pos).activityInfo.name;
//	        	Intent intent = new Intent();
//	        	intent.setClassName(packageName, className);
//	        	startActivity(intent);
//	        	//finish();
//	        }
//
//	        public void onNothingSelected(AdapterView parent) {
//	          // Do nothing.
//	        }
//	    }
	 
}

class GroupAdapter extends BaseAdapter {

	private Context context;
	private List<String> list;	
	public GroupAdapter(Context context, List<String> list) {
	
		this.context = context;	
		this.list = list;	
	}
	
	@Override	
	public int getCount() {	
		return list.size();
	}
	
	@Override	
	public Object getItem(int position) {	
		return list.get(position);	
	}
	
	@Override	
	public long getItemId(int position) {	
		return position;	
	}
	
	@Override	
	public View getView(int position, View convertView, ViewGroup viewGroup) {		
		
		ViewHolder holder;	
		if (convertView==null) {	
		convertView=LayoutInflater.from(context).inflate(R.layout.group_item_view, null);	
		holder=new ViewHolder();	
		convertView.setTag(holder);	
		holder.groupItem=(TextView) convertView.findViewById(R.id.groupItem);	
	}
	
	else{		
		holder=(ViewHolder) convertView.getTag();		
	}		
		holder.groupItem.setTextColor(Color.BLACK);		
		holder.groupItem.setText(list.get(position));	
		return convertView;		
	}
	
	static class ViewHolder {	
		TextView groupItem;
	}

}
