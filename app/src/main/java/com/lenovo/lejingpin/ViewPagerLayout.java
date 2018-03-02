package com.lenovo.lejingpin;

import com.lenovo.lejingpin.AsyncImageLoader;
import com.lenovo.lejingpin.DetailClassicActivity;
import com.lenovo.lejingpin.LEJPConstant;
import com.lenovo.launcher.R;
import com.lenovo.lejingpin.AsyncImageLoader.ImageCallback;
import com.lenovo.lejingpin.AsyncImageLoader.ImagePathCallback;
import com.lenovo.lejingpin.network.AmsApplication;
import com.lenovo.lejingpin.network.WallpaperResponse.ApplicationData;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ViewPagerLayout extends LinearLayout{
	public ImageView mimage;
	private LayoutInflater mInflater;
	private int mPosition;
	private int mtypeindex;
   	AsyncImageLoader asyncImageLoader;
   	LEJPConstant mLeConstant;
   	private int mpos;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	Holder holder = (Holder)msg.obj;
        	holder.mimg.setBackgroundDrawable(holder.mdb);
        }
    };
	public ViewPagerLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		// TODO Auto-generated constructor stub
	}

	public ViewPagerLayout(Context context) {
		super(context);
		init(context);
		// TODO Auto-generated constructor stub
	}
   	public void setPosition(int Position)
   	{
   		mPosition = Position;
   	}
   	public void setTypeindex(int typeindex)
   	{
   		mtypeindex = typeindex;
   	}
   	public void setLeConstant(LEJPConstant leconstant)
   	{
   		mLeConstant = leconstant;
   	}
   	public void setAsyncImageLoader(AsyncImageLoader asyncimageloader)
   	{
   		asyncImageLoader = asyncimageloader;
   	}
   	public void setDisplayPos(int pos)
   	{
   		mpos = pos;
   	}
   	public void Ondisplay(Context context)
   	{

   	}
	private void init(Context context)
	{
	    mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	View view = mInflater.inflate(R.layout.detail_classic_viewpager_item, null);
		mimage = (ImageView)view.findViewById(R.id.detail_classic_viewpager_item_image);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		this.addView(view,lp);
	}
}
