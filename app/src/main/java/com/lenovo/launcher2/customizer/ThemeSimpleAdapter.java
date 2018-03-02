package com.lenovo.launcher2.customizer;

import java.util.List;
import java.util.Map;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ThemeSimpleAdapter extends SimpleAdapter {
	
    private int[] mTo;
    private String[] mFrom;

    private List<? extends Map<String, ?>> mData;
    private int mResource;
    private LayoutInflater mInflater;
    private Context mContext;
    private LauncherApplication mApp;
    private int mTextColorRes;
    private int mTextColorResDef;
    private boolean mFromTheme;
    private Typeface mTypeface;
    
    //test by dining 
    private boolean mIsList = false;
    
	public ThemeSimpleAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to, int textColor, int textColorDef , Typeface typeface) {
		super(context, data, resource, from, to);
        mData = data;
        mResource  = resource;
        mFrom = from;
        mTo = to;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        mTextColorRes = textColor;
        mTextColorResDef = textColorDef;
        mFromTheme = true;
        mApp = (LauncherApplication) mContext.getApplicationContext();
        /***RK_ID:RK_FONT AUT:zhanglz1@lenovo.com. DATE: 2013-02-21 ***/        
        mTypeface = typeface;
	}
	public ThemeSimpleAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to, int textColor, int textColorDef) {
		super(context, data, resource, from, to);
        mData = data;
        mResource  = resource;
        mFrom = from;
        mTo = to;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        mTextColorRes = textColor;
        mTextColorResDef = textColorDef;
        mFromTheme = true;
     //   mApp = (LauncherApplication) mContext.getApplicationContext();
	}
	public ThemeSimpleAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to, int textColor, int textColorDef,boolean fromTheme) {
		this(context, data, resource, from, to,textColor,textColorDef);
		  mFromTheme = fromTheme;
	}
	public ThemeSimpleAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to, int textColor, int textColorDef,Typeface typeface,boolean fromTheme) {
		this(context, data, resource, from, to,textColor,textColorDef,typeface);
		mFromTheme = fromTheme;
        /***RK_ID:RK_FONT AUT:zhanglz1@lenovo.com. DATE: 2013-02-21 ***/        
		mTypeface = typeface;
	}
	//test by dining
	public ThemeSimpleAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to, int textColor, int textColorDef,boolean fromTheme, boolean isList) {
		this(context, data, resource, from, to,textColor,textColorDef);
		mFromTheme = fromTheme;
        /***RK_ID:RK_FONT AUT:zhanglz1@lenovo.com. DATE: 2013-02-21 ***/        
		mIsList = isList;
	}
	
	private ColorStateList getTextColor() {
		ColorStateList color;
		if(!mFromTheme){
			color = mContext.getResources().getColorStateList(mTextColorRes);
			return color;
		}
//		color = mApp.mLauncherContext.getColor(mTextColorRes, mTextColorResDef);	
//		color = mApp.getResources().getColorStateList(mTextColorRes);
		color = mContext.getResources().getColorStateList(mTextColorRes);
		return color;
	}
	
    private void bindView(int position, View view) {
        final Map dataSet = mData.get(position);
        if (dataSet == null) {
            return;
        }

        final ViewBinder binder = getViewBinder();
        final String[] from = mFrom;
        final int[] to = mTo;
        final int count = to.length;

        for (int i = 0; i < count; i++) {
            final View v = view.findViewById(to[i]);
            if (v != null) {            	
            	
                final Object data = dataSet.get(from[i]);
                String text = data == null ? "" : data.toString();

                boolean bound = false;
                if (binder != null) {
                    bound = binder.setViewValue(v, data, text);
                }

                if (!bound) {
                    if (v instanceof Checkable) {
                        if (data instanceof Boolean) {
                            ((Checkable) v).setChecked((Boolean) data);
                        } else if (v instanceof TextView) {
                            // Note: keep the instanceof TextView check at the bottom of these
                            // ifs since a lot of views are TextViews (e.g. CheckBoxes).
                            setViewText((TextView) v, text);
                        } else {
                            throw new IllegalStateException(v.getClass().getName() +
                                    " should be bound to a Boolean, not a " +
                                    (data == null ? "<unknown type>" : data.getClass()));
                        }
                    } else if (v instanceof TextView) {
                        // Note: keep the instanceof TextView check at the bottom of these
                        // ifs since a lot of views are TextViews (e.g. CheckBoxes).
                    	if (data instanceof Drawable) {
                    		setViewImage((TextView) v, (Drawable) data);
                    	} else if (data instanceof Integer) {
                    		setViewImage((TextView) v, (Integer) data);
                    	} else {
	                        setViewText((TextView) v, text);
	                        ColorStateList textColor = getTextColor();
	                        if (textColor != null ) {
	                        	setTextColor((TextView) v, textColor);
	                        }
                    	}
                    } else if (v instanceof ImageView) {
                        if (data instanceof Integer) {
                            setViewImage((ImageView) v, (Integer) data);                            
                        } else if (data instanceof Drawable) {
                        	setViewImage((ImageView) v, (Drawable) data);
                        } else {
                            setViewImage((ImageView) v, text);
                        }
                    } else {
                        throw new IllegalStateException(v.getClass().getName() + " is not a " +
                                " view that can be bounds by this SimpleAdapter");
                    }
                }
            }
        }
    }
    
	public void setViewImage(ImageView v, Drawable value) {
    	v.setImageDrawable(value);
    }
    
    public void setViewImage(TextView v, Drawable value) {
    	if(mIsList){
    		v.setCompoundDrawablesWithIntrinsicBounds(value, null,null, null);
    	}else{
    	    v.setCompoundDrawablesWithIntrinsicBounds(null, value, null, null);
    	}
    }
    
    public void setViewImage(TextView v, int value) {
    	if(mIsList){
    		v.setCompoundDrawablesWithIntrinsicBounds(value, 0, 0, 0);
    	}else{
    	    v.setCompoundDrawablesWithIntrinsicBounds(0, value, 0, 0);
    	}
    }
    
    public void setTextColor(TextView v, int color) {
    	v.setTextColor(color);
    }
    
    public void setTextColor(TextView v, ColorStateList colors) {
    	if (colors == null) {
    		return;
    	}
    	v.setTextColor(colors);
    }
    
    /**
     * @see android.widget.Adapter#getView(int, View, ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, mResource);
    }

    private View createViewFromResource(int position, View convertView,
            ViewGroup parent, int resource) {
        View v;
        if (convertView == null) {
            v = mInflater.inflate(resource, parent, false);
        } else {
            v = convertView;
        }
        bindView(position, v);
        if(position == 6 && SettingsValue.getAllNewTagPreference(mContext)){
        	v.findViewById(R.id.newtag).setVisibility(View.VISIBLE);
        }
        return v;
    }
}
